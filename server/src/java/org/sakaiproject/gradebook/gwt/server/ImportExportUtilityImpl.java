 /***********************************************************************************
 *
 * Copyright (c) 2008, 2009, 2010, 2011, 2012 The Regents of the University of California
 *
 * Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.gradebook.gwt.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.POIXMLException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;
import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.api.ImportSettings;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.gxt.ItemModelProcessor;
import org.sakaiproject.gradebook.gwt.client.gxt.type.ExportType;
import org.sakaiproject.gradebook.gwt.client.gxt.type.FileFormat;
import org.sakaiproject.gradebook.gwt.client.gxt.upload.ImportHeader;
import org.sakaiproject.gradebook.gwt.client.gxt.upload.ImportHeader.Field;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.client.model.Roster;
import org.sakaiproject.gradebook.gwt.client.model.Upload;
import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.type.ItemType;
import org.sakaiproject.gradebook.gwt.sakai.GradeCalculations;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ComponentService;
import org.sakaiproject.gradebook.gwt.sakai.GradebookImportException;
import org.sakaiproject.gradebook.gwt.sakai.GradebookToolService;
import org.sakaiproject.gradebook.gwt.sakai.model.GradeItem;
import org.sakaiproject.gradebook.gwt.sakai.model.UserDereference;
import org.sakaiproject.gradebook.gwt.server.exceptions.ImportFormatException;
import org.sakaiproject.gradebook.gwt.server.model.GradeItemImpl;
import org.sakaiproject.gradebook.gwt.server.model.LearnerImpl;
import org.sakaiproject.gradebook.gwt.server.model.UploadImpl;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.util.ResourceLoader;
import org.springframework.web.multipart.MultipartFile;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/*
 * TODO: too many setters here?
 */

public class ImportExportUtilityImpl implements ImportExportUtility {

	private static final Log log = LogFactory.getLog(ImportExportUtilityImpl.class);
	
	private final static int RAWFIELD_FIRST_POSITION = 0; 
	private final static int RAWFIELD_SECOND_POSITION = 1; 

	/// these are injected as of GRBK-407
	private String scantronStudentIdHeader = null; 
	private String scantronScoreHeader = null;
	private String scantronRescoreHeader = null;
	// ... and these, as of GRBK-1221
	private Gradebook2ComponentService service = null;
	private GradebookToolService toolService;	
	
	
	// GRBK-689
	private String clickerStudentIdHeader;
	// Set via IoC
	private ResourceLoader i18n;

	public String[] scantronIgnoreColumns = null;
	public String[] clickerIgnoreColumns = null;
	public String[] idColumns = null;

	public String[] nameColumns = null;

	

	private static enum StructureRow {
		// Gradebook level
		GRADEBOOK("Gradebook:"),
		SCALED_EC("Scaled XC:"),
		SHOWCOURSEGRADES("ShowCourseGrades:"),
		SHOWRELEASEDITEMS("ShowReleasedItems:"),
		SHOWITEMSTATS("ShowItemStats:"),
		SHOWMEAN("ShowMean:"),
		SHOWMEDIAN("ShowMedian:"),
		SHOWMODE("ShowMode:"),
		SHOWRANK("ShowRank:"),
		SHOWSTATISTICSCHART("ShowStatisticsChart:"),
		// Category level
		CATEGORY("Category:"),
		PERCENT_GRADE("% Grade:"),
		POINTS("Points:"), 
		PERCENT_CATEGORY("% Category:"),
		DROP_LOWEST("Drop Lowest:"),
		EQUAL_WEIGHT("Equal Weight Items:"),
		WEIGHT_ITEMS_BY_POINTS("Weight Items By Points:");

		private String displayName;

		StructureRow(String displayName) {
			this.displayName = displayName;
		}

		public String getDisplayName() {
			return displayName;
		}
	};
	
	
	
	private Set<String> headerRowIndicatorSet, idSet, nameSet, scantronIgnoreSet, 
						clickerIgnoreSet;
	private List<String> templateHeaderColumnSet;

	public static String UNSAFE_FILENAME_CHAR_REGEX = "[\\p{Punct}\\p{Space}\\p{Cntrl}]";
	public static List<String> SUPPORTED_FILE_TYPES = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			for (FileType f : FileType.values()) {
				add(f.getName());
			}
		}
	};
	public static String CONTENT_DISPOSITION_HEADER_NAME = "Content-Disposition";
	public static String CONTENT_DISPOSITION_HEADER_ATTACHMENT = "attachment; filename=";
		
	private GradeCalculations gradeCalculations;

	private Tika filetypeDetector;



	
	
	
	public void init() throws Exception {	
		
		this.headerRowIndicatorSet = new HashSet<String>();
		this.nameSet = new HashSet<String>();
		for (int i=0;i<nameColumns.length;i++) {
			nameSet.add(nameColumns[i].toLowerCase());
			headerRowIndicatorSet.add(nameColumns[i].toLowerCase());
		}
		this.idSet = new HashSet<String>();
		for (int i=0;i<idColumns.length;i++) {
			idSet.add(idColumns[i].toLowerCase());
			headerRowIndicatorSet.add(idColumns[i].toLowerCase());
		}
		this.scantronIgnoreSet = new HashSet<String>();
		for (int i=0;i<scantronIgnoreColumns.length;i++) {
			scantronIgnoreSet.add(scantronIgnoreColumns[i].toLowerCase());
		}
		this.clickerIgnoreSet = new HashSet<String>();
		for (int i=0;i<clickerIgnoreColumns.length;i++) {
			clickerIgnoreSet.add(clickerIgnoreColumns[i].toLowerCase());
		}
		
		/// The case is not important here
		this.templateHeaderColumnSet = new ArrayList<String>() {
		    @Override
		    public boolean contains(Object o) {
		        String paramStr = (String)o;
		        for (String s : this) {	
		            if (paramStr.equalsIgnoreCase(s)) return true;
		            }
		        return false;
		        }
		    };
		    
		templateHeaderColumnSet.add(i18n.getString("exportColumnHeaderStudentId").trim());
		templateHeaderColumnSet.add(i18n.getString("exportColumnHeaderStudentName").trim());
		
		
	}
	

	public void setScantronIgnoreColumns(String[] scantronIgnoreColumns) {
		this.scantronIgnoreColumns = scantronIgnoreColumns;
	}


	public void setIdColumns(String[] idColumns) {
		this.idColumns = idColumns;
	}


	public void setNameColumns(String[] nameColumns) {
		this.nameColumns = nameColumns;
	}


	public void setScantronScoreHeader(String scantronScoreHeader) {
		this.scantronScoreHeader = scantronScoreHeader;
	}


	public void setScantronRescoreHeader(String scantronRescoreHeader) {
		this.scantronRescoreHeader = scantronRescoreHeader;
	}


	public void setIdSet(Set<String> idSet) {
		this.idSet = idSet;
	}


	public void setNameSet(Set<String> nameSet) {
		this.nameSet = nameSet;
	}


	public void setScantronIgnoreSet(Set<String> scantronIgnoreSet) {
		this.scantronIgnoreSet = scantronIgnoreSet;
	}
	
	public void setScantronStudentIdHeader(String scantronStudentIdHeader) {
		this.scantronStudentIdHeader = scantronStudentIdHeader;
	}
	


	public ImportExportDataFile exportGradebook(Gradebook2ComponentService service, String gradebookUid, 
			final boolean includeStructure, final boolean includeComments, List<String> sectionUidList) 
	throws FatalException {

		Gradebook gradebook = service.getGradebook(gradebookUid);
		Item gradebookItemModel = gradebook.getGradebookItemModel();
		ImportExportDataFile out = new ImportExportDataFile(); 

		Long gradebookId = gradebook.getGradebookId();
		final List<String> headerIds = new ArrayList<String>();

		final List<String> headerColumns = new LinkedList<String>();

		headerColumns.add(i18n.getString("exportColumnHeaderStudentId"));
		headerColumns.add(i18n.getString("exportColumnHeaderStudentName"));
		headerColumns.add(i18n.getString("exportColumnHeaderSection")); 

		GradeType gradeType = gradebookItemModel.getGradeType();
		
		if (includeStructure) {
			CategoryType categoryType = gradebookItemModel.getCategoryType();
			String categoryTypeText = getDisplayName(categoryType);
			String gradeTypeText = getDisplayName(gradebookItemModel.getGradeType());

			// First, we need to add a row for basic gradebook info
			String[] gradebookInfoRow = { "", StructureRow.GRADEBOOK.getDisplayName(), gradebookItemModel.getName(), categoryTypeText, gradeTypeText};
			out.addRow(gradebookInfoRow);

			exportViewOptionsAndScaleEC(out, gradebook); 


			final List<String> categoriesRow = new LinkedList<String>();
			final List<String> percentageGradeRow = new LinkedList<String>();
			final List<String> pointsRow = new LinkedList<String>();
			final List<String> percentCategoryRow = new LinkedList<String>();
			final List<String> dropLowestRow = new LinkedList<String>();
			final List<String> equalWeightRow = new LinkedList<String>();
			final List<String> weightItemsByPointsRow = new LinkedList<String>();


			categoriesRow.add("");
			categoriesRow.add(StructureRow.CATEGORY.getDisplayName());
			categoriesRow.add("");

			percentageGradeRow.add("");
			percentageGradeRow.add(StructureRow.PERCENT_GRADE.getDisplayName());
			percentageGradeRow.add("");

			pointsRow.add("");
			pointsRow.add(StructureRow.POINTS.getDisplayName());
			pointsRow.add("");

			percentCategoryRow.add("");
			percentCategoryRow.add(StructureRow.PERCENT_CATEGORY.getDisplayName());
			percentCategoryRow.add("");

			dropLowestRow.add("");
			dropLowestRow.add(StructureRow.DROP_LOWEST.getDisplayName());
			dropLowestRow.add("");

			equalWeightRow.add("");
			equalWeightRow.add(StructureRow.EQUAL_WEIGHT.getDisplayName());
			equalWeightRow.add("");
			
			weightItemsByPointsRow.add("");
			weightItemsByPointsRow.add(StructureRow.WEIGHT_ITEMS_BY_POINTS.getDisplayName());
			weightItemsByPointsRow.add("");

			ItemModelProcessor processor = new ItemModelProcessor(gradebookItemModel) {

				@Override
				public void doCategory(Item itemModel, int childIndex) {
					StringBuilder categoryName = new StringBuilder().append(itemModel.getName());

					if (Util.checkBoolean(itemModel.getExtraCredit())) {
						categoryName.append(AppConstants.EXTRA_CREDIT_INDICATOR);
					}

					if (!Util.checkBoolean(itemModel.getIncluded())) {
						categoryName.append(AppConstants.UNINCLUDED_INDICATOR);
					}

					categoriesRow.add(categoryName.toString());
					percentageGradeRow.add(new StringBuilder()
					.append(String.valueOf(itemModel.getPercentCourseGrade()))
					.append("%").toString());
					Integer dropLowest = itemModel.getDropLowest();
					
					if (dropLowest == null)
						dropLowestRow.add("");
					else
						dropLowestRow.add(String.valueOf(dropLowest));
					
					Boolean isEqualWeight = itemModel.getEqualWeightAssignments();
					if (isEqualWeight == null)
						equalWeightRow.add("");
					else
						equalWeightRow.add(String.valueOf(isEqualWeight));

					Boolean weightItemsByPoints = itemModel.getEnforcePointWeighting();
					if(null == weightItemsByPoints)
						weightItemsByPointsRow.add("");
					else
						weightItemsByPointsRow.add(String.valueOf(weightItemsByPoints));

					if (((GradeItem)itemModel).getChildCount() == 0) {
						headerIds.add(AppConstants.EXPORT_SKIPCOLUMN_INDICATOR);
						headerColumns.add("");
						pointsRow.add("");
						percentCategoryRow.add("");
					}

				}

				@Override
				public void doItem(Item itemModel, int childIndex) {
					if (childIndex > 0) {
						categoriesRow.add("");
						percentageGradeRow.add("");
						dropLowestRow.add("");
						equalWeightRow.add("");
						weightItemsByPointsRow.add("");
					} 

					if (includeComments) {
						categoriesRow.add("");
						percentageGradeRow.add("");
						dropLowestRow.add("");
						equalWeightRow.add("");
						weightItemsByPointsRow.add("");
					}

					StringBuilder text = new StringBuilder();
					text.append(itemModel.getName());

					if (Util.checkBoolean(itemModel.getExtraCredit())) {
						text.append(AppConstants.EXTRA_CREDIT_INDICATOR);
					}

					if (!Util.checkBoolean(itemModel.getIncluded())) {
						text.append(AppConstants.UNINCLUDED_INDICATOR);
					}
					
					if (Util.checkBoolean(itemModel.getReleased())) {
						text.append(AppConstants.RELEASE_SCORES_INDICATOR);
					}
					
					if (Util.checkBoolean(itemModel.getNullsAsZeros())) {
						text.append(AppConstants.GIVE_UNGRADED_NO_CREDIT_INDICATOR);
					}

					if (!includeStructure) {
						String points = DecimalFormat.getInstance().format(itemModel.getPoints());
						text.append(" [").append(points).append("]");
					}

					headerIds.add(itemModel.getIdentifier());
					headerColumns.add(text.toString());

					if (itemModel.getPoints() == null)
						pointsRow.add("");
					else
						pointsRow.add(String.valueOf(itemModel.getPoints()));
					
					percentCategoryRow.add(new StringBuilder()
					.append(String.valueOf(itemModel.getPercentCategory()))
					.append("%").toString());

					if (includeComments) {
						StringBuilder commentsText = new StringBuilder();
						commentsText.append(AppConstants.COMMENTS_INDICATOR).append(itemModel.getName());
						headerColumns.add(commentsText.toString());
						pointsRow.add("");
						percentCategoryRow.add("");
					}
				}

			};

			processor.process();

			switch (categoryType) {
				case NO_CATEGORIES:
					out.addRow(pointsRow.toArray(new String[pointsRow.size()]));
					break;
				case SIMPLE_CATEGORIES:
					out.addRow(categoriesRow.toArray(new String[categoriesRow.size()]));
					out.addRow(dropLowestRow.toArray(new String[dropLowestRow.size()]));
					out.addRow(pointsRow.toArray(new String[pointsRow.size()]));

					break;
				case WEIGHTED_CATEGORIES:					
					out.addRow(categoriesRow.toArray(new String[categoriesRow.size()]));
					out.addRow(percentageGradeRow.toArray(new String[percentageGradeRow.size()]));
					out.addRow(dropLowestRow.toArray(new String[dropLowestRow.size()]));
					out.addRow(equalWeightRow.toArray(new String[equalWeightRow.size()]));
					out.addRow(pointsRow.toArray(new String[pointsRow.size()]));
					out.addRow(percentCategoryRow.toArray(new String[percentCategoryRow.size()]));
					out.addRow(weightItemsByPointsRow.toArray(new String[weightItemsByPointsRow.size()]));

					break;
			}

			String[] blankRow = { "" };
			out.addRow(blankRow);
		} else {

			ItemModelProcessor processor = new ItemModelProcessor(gradebookItemModel) {

				@Override
				public void doItem(Item itemModel) {
					StringBuilder text = new StringBuilder();
					text.append(itemModel.getName());

					if (Util.checkBoolean(itemModel.getExtraCredit())) {
						text.append(AppConstants.EXTRA_CREDIT_INDICATOR);
					}

					if (!Util.checkBoolean(itemModel.getIncluded())) {
						text.append(AppConstants.UNINCLUDED_INDICATOR);
					}
					
					if (Util.checkBoolean(itemModel.getReleased())) {
						text.append(AppConstants.RELEASE_SCORES_INDICATOR);
					}
					
					if (Util.checkBoolean(itemModel.getNullsAsZeros())) {
						text.append(AppConstants.GIVE_UNGRADED_NO_CREDIT_INDICATOR);
					}

					if (!includeStructure) {
						String points = DecimalFormat.getInstance().format(itemModel.getPoints());
						text.append(" [").append(points).append("]");
					}

					headerIds.add(itemModel.getIdentifier());
					headerColumns.add(text.toString());

					if (includeComments) {
						StringBuilder commentsText = new StringBuilder();
						commentsText.append(AppConstants.COMMENTS_INDICATOR).append(itemModel.getName());
						headerColumns.add(commentsText.toString());
					}
				}

			};

			processor.process();

		}

		headerColumns.add("Letter Grade");
		
		if (gradeType != GradeType.LETTERS)
			headerColumns.add("Calculated Grade");

		out.addRow(headerColumns.toArray(new String[headerColumns.size()]));

		Roster result = service.getRoster(gradebookUid, gradebookId, null, null, sectionUidList, null, null, null, true, false, false);

		List<Learner> rows = result.getLearnerPage();

		if (headerIds != null) {

			if (rows != null) {
				for (Learner row : rows) {
					List<String> dataColumns = new LinkedList<String>();
					dataColumns.add((String)row.get(LearnerKey.S_EXPRT_USR_ID.name()));
					dataColumns.add((String)row.get(LearnerKey.S_LST_NM_FRST.name()));
					dataColumns.add((String) row.get(LearnerKey.S_SECT.name()));

					for (int column = 0; column < headerIds.size(); column++) {
						String columnIndex = headerIds.get(column);
						
						if (columnIndex != null) {
							if (columnIndex.equals(AppConstants.EXPORT_SKIPCOLUMN_INDICATOR)) {
								dataColumns.add("");
								continue;
							}
							
							Object value = row.get(columnIndex);

							if (value != null)
								dataColumns.add(String.valueOf(value));
							else
								dataColumns.add("");

						} else {
							dataColumns.add("");
						}

						if (includeComments) {
							String commentId = Util.buildCommentTextKey(headerIds.get(column)); 

							Object comment = row.get(commentId);

							if (comment == null)
								comment = "";

							dataColumns.add(String.valueOf(comment));
						}
					}

					dataColumns.add((String)row.get(LearnerKey.S_LTR_GRD.name()));
					
					if (gradeType != GradeType.LETTERS)
						dataColumns.add((String)row.get(LearnerKey.S_CALC_GRD.name()));

					out.addRow(dataColumns.toArray(new String[dataColumns.size()]));
				}
			} 

		}

		service.postEvent("gradebook2.export", String.valueOf(gradebookId));
		
		return out;

	}
	
	private void exportViewOptionsAndScaleEC(ImportExportDataFile out, Gradebook gradebook) {
		
		Item firstGBItem = gradebook.getGradebookItemModel(); 
		if (Util.checkBoolean(firstGBItem.getExtraCreditScaled()))
		{
			outputStructureTwoPartExportRow(StructureRow.SCALED_EC.getDisplayName(), "true", out); 
		}
		
		if (Util.checkBoolean(firstGBItem.getReleaseGrades()))
		{
			outputStructureTwoPartExportRow(StructureRow.SHOWCOURSEGRADES.getDisplayName(), "true", out); 		
		}

		if (Util.checkBoolean(firstGBItem.getReleaseItems()))
		{
			outputStructureTwoPartExportRow(StructureRow.SHOWRELEASEDITEMS.getDisplayName(), "true", out); 		
		}

		if (Util.checkBoolean(firstGBItem.getShowItemStatistics()))
		{
			outputStructureTwoPartExportRow(StructureRow.SHOWITEMSTATS.getDisplayName(), "true", out); 
		}

		if (Util.checkBoolean(firstGBItem.getShowMean()))
		{
			outputStructureTwoPartExportRow(StructureRow.SHOWMEAN.getDisplayName(), "true", out); 
		}

		if (Util.checkBoolean(firstGBItem.getShowMedian()))
		{
			outputStructureTwoPartExportRow(StructureRow.SHOWMEDIAN.getDisplayName(), "true", out); 
		}

		if (Util.checkBoolean(firstGBItem.getShowMode()))
		{
			outputStructureTwoPartExportRow(StructureRow.SHOWMODE.getDisplayName(), "true", out); 
		}

		if (Util.checkBoolean(firstGBItem.getShowRank()))
		{
			outputStructureTwoPartExportRow(StructureRow.SHOWRANK.getDisplayName(), "true", out); 
		}
		
		if (Util.checkBoolean(firstGBItem.getShowStatisticsChart()))
		{
			outputStructureTwoPartExportRow(StructureRow.SHOWSTATISTICSCHART.getDisplayName(), "true", out);
		}
	}

	private void outputStructureTwoPartExportRow(String optionName, String optionValue, ImportExportDataFile out)
	{
		String[] rowString; 
		rowString = new String[3]; 
		rowString[0] = ""; 
		rowString[1] = optionName;
		rowString[2] = optionValue;
		out.addRow(rowString); 
	}
	
	public void exportGradebook(FileType fileType, String filename, OutputStream outStream,
			Gradebook2ComponentService service, String gradebookUid,
			final boolean includeStructure, final boolean includeComments, List<String> sectionUidList) throws FatalException {
		
		
		if(fileType.equals(FileType.XLS97) || fileType.equals(FileType.XLSX)) {
			
			exportGradebookXLS(filename, outStream, service, gradebookUid, includeStructure, includeComments, sectionUidList, fileType.equals(FileType.XLSX)); 
		}
		else if(fileType.equals(FileType.CSV)) {
			
			exportGradebookCSV(filename, outStream, service, gradebookUid, includeStructure, includeComments, sectionUidList);
		}
		else if(fileType.equals(FileType.TEMPLATE)) {
			
			exportGradebookTemplate(filename, outStream, service, gradebookUid, includeStructure, includeComments, sectionUidList);
		}
	}

	private void exportGradebookXLS(String title, OutputStream outStream,
			Gradebook2ComponentService service, String gradebookUid,
			final boolean includeStructure, final boolean includeComments, List<String> sectionUidList, boolean isXSSF)
			throws FatalException {
		
		final ImportExportDataFile file = exportGradebook(service,
				gradebookUid, includeStructure, includeComments, sectionUidList);
		
		Map<StructureRow, String[]> structureColumnsMap = new HashMap<StructureRow, String[]>();
		ImportExportInformation ieInfo = new ImportExportInformation();

		

		int structureStop = 0; 

		structureStop = readDataForStructureInformation(file, buildRowIndicatorMap(), structureColumnsMap);
		if (structureStop != -1)
		readInHeaderRow(file, ieInfo, structureStop);
				
		int studentId = -1;
		if(ieInfo.getHeaders() != null) 
			for (int i=0;i<ieInfo.getHeaders().length;++i) {
				if (ieInfo.getHeaders()[i] != null) {
					String thisHeaderName = ieInfo.getHeaders()[i].getValue();
					for (int j=0;j<idColumns.length;j++) {
						String idColumn = idColumns[j];
						if ( idColumn != null && idColumn.equalsIgnoreCase(thisHeaderName)) {
							studentId = i;
							break;
						}
					}
				}
				if (studentId != -1) break;
			}

		org.apache.poi.ss.usermodel.Workbook wb = isXSSF ? new XSSFWorkbook() : new HSSFWorkbook();
		
		CreationHelper helper = wb.getCreationHelper();
		// GRBK-1086 
		org.apache.poi.ss.usermodel.Sheet s = wb.createSheet(i18n.getString("exportSheetTitle"));

		file.startReading(); 
		String[] curRow = null; 
		int row = 0; 
		
		Row r = null;
		while ( (curRow = file.readNext()) != null) {
			r = s.createRow(row); 

			for (int i = 0; i < curRow.length ; i++) {
				org.apache.poi.ss.usermodel.Cell cl = r.createCell(i);
				//GRBK-840 If the cell is numeric, we should make it numeric...
				// GRBK-979 .... unless it is the student id
				if ( NumberUtils.isNumber(curRow[i]) && i != studentId)
				{
					cl.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					cl.setCellValue(Double.valueOf(curRow[i])); 
				}
				else
				{
					cl.setCellType(HSSFCell.CELL_TYPE_STRING); 
					cl.setCellValue(helper.createRichTextString(curRow[i])); 
				}
			}
			
			row++; 
		}
		
		// Run autosize on last row's columns
		if (r != null) {
			for (int i = 0; i <= r.getLastCellNum() ; i++) {
				s.autoSizeColumn((short) i);
			}
		}
 		writeXLSResponse(wb, outStream); 
 		
	}


	private void writeXLSResponse(org.apache.poi.ss.usermodel.Workbook wb, OutputStream out) throws FatalException {
		try {
			wb.write(out);
			out.flush(); 
		} catch (IOException e) {
			log.error("Caught exception " + e, e); 
			throw new FatalException(e); 

		}		
	}

	private void exportGradebookCSV(String title, OutputStream outStream,
			Gradebook2ComponentService service, String gradebookUid,
			final boolean includeStructure, final boolean includeComments, List<String> sectionUidList)
			throws FatalException {

		final ImportExportDataFile
		            file = exportGradebook (service, gradebookUid, includeStructure, includeComments, sectionUidList);
		OutputStreamWriter
		            writer = new OutputStreamWriter(outStream);
		
		CSVWriter csvWriter = new CSVWriter(writer);
		file.startReading(); 
		String[] curRow; 
		while ((curRow = file.readNext()) != null)
		{
			csvWriter.writeNext(curRow); 
		}
		try {
			csvWriter.flush();
		} catch (IOException e) {
			log.error("Caught ioexception: ", e);
		} 
	}

	private void exportGradebookTemplate(
			String title,
			OutputStream outStream,
			Gradebook2ComponentService service,
			String gradebookUid,
			final boolean includeStructure,
			final boolean includeComments,
			List<String> sectionUidList)
			throws FatalException {

		ImportExportDataFile importExportDataFile = new ImportExportDataFile();
		
		// File Headers
		String[] headerColumns = new String[3];
		headerColumns[0] = i18n.getString("exportColumnHeaderStudentId");
		headerColumns[1] = i18n.getString("exportColumnHeaderStudentName");
		headerColumns[2] = i18n.getString("exportColumnHeaderNewAssignment");
		importExportDataFile.addRow(headerColumns);
		
		Gradebook gradebook = service.getGradebook(gradebookUid);
		Long gradebookId = gradebook.getGradebookId();
		
		// Learner rows
		Roster roster = service.getRoster(gradebookUid, gradebookId, null, null, sectionUidList, null, null, null, true, false, false);
		List<Learner> learners = roster.getLearnerPage();
		
		if(null != learners) {
			
			for(Learner learner : learners) {
				
				String[] learnerData = new String[2];
				learnerData[0] = (String)learner.get(LearnerKey.S_EXPRT_USR_ID.name());
				learnerData[1] = (String)learner.get(LearnerKey.S_LST_NM_FRST.name());
				importExportDataFile.addRow(learnerData);
			}
		}
		
		OutputStreamWriter writer = new OutputStreamWriter(outStream);
		
		CSVWriter csvWriter = new CSVWriter(writer);
		
		importExportDataFile.startReading(); 
		
		String[] curRow; 
		
		while((curRow = importExportDataFile.readNext()) != null) {
			
			csvWriter.writeNext(curRow); 
		}
		
		try {
		
			csvWriter.flush();
		}
		catch(IOException e) {
			
			log.error("Caught ioexception: ", e);
		}
		finally {
			
			try {

				if(null != csvWriter) {
			
					csvWriter.close();
				}
			}
			catch(IOException ioe) {
				
				log.error("IOException", ioe);
			}
		}
	}
	

	private org.apache.poi.ss.usermodel.Workbook readPoiSpreadsheet(File file) 
	{
		org.apache.poi.ss.usermodel.Workbook spread = null;
				
		//file.mark(1024*1024*512); // file-size limit is 512MB
		try {
			spread = new HSSFWorkbook(POIFSFileSystem.createNonClosingInputStream(new FileInputStream(file)));
			log.debug("HSSF file detected"); 
		} 
		catch (IOException e) 
		{
			log.debug("Caught I/O Exception", e);
		} 
		catch (IllegalArgumentException iae)
		{
			log.debug("Caught IllegalArgumentException Exception", iae);
		}
		if (spread == null)
		{
			
			try {
				spread = new XSSFWorkbook(POIFSFileSystem.createNonClosingInputStream(new FileInputStream(file)));
				log.debug("XSSF file detected");

			} catch (IOException e) 
			{
				log.debug("Caught I/O Exception checking for xlsx format", e);
			} 
			catch (IllegalArgumentException iae)
			{
				log.debug("Caught IllegalArgumentException Exception checking for xlsx format", iae);
			} catch (POIXMLException e) 
			{
				log.debug("Caught POIXMLException Exception checking for xlsx format", e);
			}

		}
		
		return spread; 

	}


	private String getMimeType(InputStream is) {
		String detected = null;
		/// get Tika's opinion about the file type in mime-type form
		try {
			detected = filetypeDetector.detect(TikaInputStream.get(is));
			log.debug("------->" + detected);
			
		} catch (IOException e) {
			log.error("ERROR: using Tika file type detector");
			e.printStackTrace();
			
		}
		return detected;
	}


	private boolean checkForCurrentAssignmentInGradebook(String fileName, String gradebookUid)
	{
		Gradebook gm = service.getGradebook(gradebookUid); 
		List<Assignment> assignments = this.toolService.getAssignments(gm.getGradebookId()); 
		for (Assignment curAssignment : assignments)
		{
			String curAssignmentName = curAssignment.getName(); 
			log.debug("curAssignmentName=" + curAssignmentName);
			if (curAssignment.getName().equalsIgnoreCase((fileName)))
			{
				return true; 
			}
		}

		return false; 
	}

	private String getUniqueItemName(String itemName,String gradebookUid) 
	throws GradebookImportException {

		log.debug("fileName=" + itemName);
		if (itemName == null || itemName.equals(""))
		{
			log.debug("null filename, returning default"); 
			return "Scantron Import"; 
		}
		
		int i = 1;
		String curFileName = itemName; 
		while (true)
		{
			log.debug("curFileName: " + curFileName); 
			if (!checkForCurrentAssignmentInGradebook(curFileName, gradebookUid))
			{
				log.debug("returning curFileName"); 
				return curFileName; 
			}
			// else
			
			curFileName = itemName + "-" +i; 
			
			i++; 

			if (i > 1000)
			{
				throw new GradebookImportException(i18n.getString("importUniqueFileNameMessage")); 
			}
		}
	}

	/*
	 * so basically, we'll do: 
	 * 1) Scan the sheet for scantron artifacts, and if so convert to a simple CSV file which is 
	 */
	public Upload parseImportXLS(MultipartFile file, ImportSettings settings) throws InvalidInputException, FatalException, IOException {
		log.debug("parseImportXLS() called"); 

		// Strip off extension
		String fileName = removeFileExenstion(file.getOriginalFilename().toLowerCase());

		
		String realFileName = fileName; 
		boolean isOriginalName; 
		
		try {
			realFileName = getUniqueItemName(fileName, settings.getGradebookUid());
		} catch (GradebookImportException e) { 
			return emptyUploadFileWithNotes(e.getMessage()); 
		} 
		isOriginalName = realFileName.equals(fileName);
		
		log.debug("realFileName=" + realFileName);
		log.debug("isOriginalName=" + isOriginalName);
		
		org.apache.poi.ss.usermodel.Workbook inspread = null;

		BufferedInputStream bufStream = new BufferedInputStream(file.getInputStream()); 

		//inspread = readPoiSpreadsheet(tempfi);
		///TODO: this is broken buonly called by the *Test class for this class and not by default

		if (inspread != null)
		{
			log.debug("Found a POI readable spreadsheet");
			bufStream.close(); 
			return handlePoiSpreadSheet(inspread, realFileName, isOriginalName, settings);
		}
		// else
		
		log.debug("POI couldn't handle the spreadsheet, using jexcelapi");
		bufStream.reset();
		return handleJExcelAPISpreadSheet(bufStream, realFileName, isOriginalName, settings); 
		

	}

	private String removeFileExenstion(String fileName) {
		if (fileName != null) {
			int indexOfExtension = fileName.lastIndexOf('.');
			if (indexOfExtension != -1 && indexOfExtension < fileName.length()) {
				fileName = fileName.substring(0, indexOfExtension);
			}
		}
		return fileName; 
	}

	private Upload handleJExcelAPISpreadSheet(InputStream is,
			String fileName, boolean isNewAssignmentByFileName, ImportSettings settings) throws InvalidInputException, FatalException, IOException {
		Workbook wb = null; 
		final String unexpectedTypeErrorMessage = i18n.getString("filetypeExtensionMismatch", 
				"jexcel ")
				+ lookupI18nExportType(settings.getExportTypeName());
		
		final String unexpectedFormatErrorMessage = i18n.getString("expectedFormat", 
				"The file content did not match your selection: ")
				+ lookupI18nFileFormat(settings.getFileFormatName());
		
		Upload rv = new UploadImpl();
		try {
			wb = Workbook.getWorkbook(is);
		} catch (BiffException e) {
			log.error("Caught a biff exception from JExcelAPI: " + e.getLocalizedMessage(), e); 
			return emptyUploadFileWithNotes(unexpectedTypeErrorMessage); 
		} catch (IOException e) {
			log.error("Caught an IO exception from JExcelAPI: " + e.getLocalizedMessage(), e); 
			return emptyUploadFileWithNotes(unexpectedTypeErrorMessage); 
		} 

		is.close();
		Sheet s = wb.getSheet(0); 
		if (s != null)
		{
			boolean shouldBeScantron = FileFormat.SCANTRON.name().equals(settings.getFileFormatName());
			boolean isReallyScantron = isScantronSheetForJExcelApi(s);
			boolean shouldBeNoStructureGB = FileFormat.NO_STRUCTURE.name().equals(settings.getFileFormatName());
			if (shouldBeNoStructureGB) {
				ImportExportDataFile raw = new ImportExportDataFile();  

				for (int i = 0; i < s.getRows(); i++)
				{
					Cell[] row = null; 
					String[] data = null; 

					row = s.getRow(i);

					data = new String[row.length]; 
					for (int j = 0; j < row.length ; j++)
					{
						data[j] = row[j].getContents(); 
					}
					raw.addRow(data); 
				}
				
				if ( 0 < readDataForStructureInformation(raw, buildRowIndicatorMap(), new HashMap<StructureRow, String[]>()) ) {
					return emptyUploadFileWithNotes(unexpectedFormatErrorMessage);
				}
				//exit if-statement
			} else
				if (shouldBeScantron && !isReallyScantron
					|| !shouldBeScantron && isReallyScantron) {
				rv.setErrors(true);
				rv.setNotes(unexpectedFormatErrorMessage);
				return rv;
			}
			if (shouldBeScantron && isReallyScantron) {
				return handleScantronSheetForJExcelApi(s, settings.getGradebookUid(), fileName, isNewAssignmentByFileName, settings);
			} 
			
			return handleNormalXLSSheetForJExcelApi(s, settings.getGradebookUid(), isNewAssignmentByFileName, settings);
			
		}
		
		
		rv.setErrors(true);
		rv.setNotes(unexpectedTypeErrorMessage);
		return rv;
		
	}

	/*
	 * for Jexcel compat. file types, the clickers and templates and full gradeboook formats are
	 * all passed into this method
	 * so this where we will check for chosen format errors
	 * 
	 */

	private Upload handleNormalXLSSheetForJExcelApi(Sheet s,
			String gradebookUid, boolean isNewAssignmentByFileName, ImportSettings settings) 
	throws InvalidInputException, FatalException {
		ImportExportDataFile raw = new ImportExportDataFile(); 
		int numRows; 
		
		final String unexpectedFormatErrorMessage = i18n.getString("expectedFormat", 
				"The file format did not match your selection: ")
				+ lookupI18nFileFormat(settings.getFileFormatName());
		
		
		

		numRows = s.getRows(); 

		for (int i = 0; i < numRows; i++)
		{
			Cell[] row = null; 
			String[] data = null; 

			row = s.getRow(i);

			data = new String[row.length]; 
			for (int j = 0; j < row.length ; j++)
			{
				data[j] = row[j].getContents(); 
			}
			raw.addRow(data); 
		}
		boolean shouldBeFullGradebook = FileFormat.FULL.name().equals(settings.getFileFormatName());
		boolean isTemplate = couldBeNewItemTemplate(raw);
		boolean shouldBeTemplate = FileFormat.TEMPLATE.name().equals(settings.getFileFormatName()) || FileFormat.CLICKER.name().equals(settings.getFileFormatName());
		boolean isBadFormat = !isTemplate && shouldBeTemplate
				|| isTemplate && !shouldBeTemplate
				|| shouldBeFullGradebook && 
				1 > readDataForStructureInformation(raw, buildRowIndicatorMap(), new HashMap<StructureRow, String[]> ());
		if (isBadFormat) {
			return emptyUploadFileWithNotes(unexpectedFormatErrorMessage);
		}
		
		if (isTemplate) {
			raw.setNewAssignment(isNewAssignmentByFileName); //// TODO: this may not be appropriate for anything but scantrons
		}
		raw.setFileType("Excel 5.0/7.0"); 
		raw.setScantronFile(isTemplate); 
		raw.setJustStructure(settings.isJustStructure());
		raw.setImportSettings(settings);
		return parseImportGeneric(raw);
	}

	private Upload handleScantronSheetForJExcelApi(Sheet s, String gradebookUid, 
			String fileName, boolean isNewAssignmentByFileName, ImportSettings settings) 
	throws InvalidInputException, FatalException 
			{
		StringBuilder err = new StringBuilder(i18n.getString("scantronHasErrors", "Scantron File with errors: ")); 
		ImportExportDataFile raw = new ImportExportDataFile(); 
		boolean stop = false; 
				
		Cell studentIdHeader = s.findCell(scantronStudentIdHeader);
		Cell scoreHeader = s.findCell(scantronScoreHeader);
		
		if (studentIdHeader == null)
		{
				err.append(i18n.getString("noColumnWithHeader","- There is no column with the header: ") + scantronStudentIdHeader);
				stop = true; 
		}

		if (!stop && scoreHeader == null)
		{
			// check for rescore header - GRBK-407
			scoreHeader = s.findCell(scantronRescoreHeader);
			if (scoreHeader == null) {
				err.append(i18n.getString("noColumnWithHeader","- There is no column with the header: ") + scantronRescoreHeader);
				stop = true; 
			}
		}
		
		if (! stop) 
		{
			raw.addRow(createScantronHeaderRow(fileName)); 
			for (int i = 0 ; i < s.getRows() ; i++)
			{
				Cell idCell; 
				Cell scoreCell; 

				idCell = s.getCell(studentIdHeader.getColumn(), i);
				scoreCell = s.getCell(scoreHeader.getColumn(), i); 

				if (!idCell.getContents().equals(studentIdHeader.getContents()))
				{
					String[] item = new String[2]; 
					item[RAWFIELD_FIRST_POSITION] = idCell.getContents(); 
					item[RAWFIELD_SECOND_POSITION] = scoreCell.getContents(); 
					raw.addRow(item); 
					item = null; 
				}
			}
			raw.setFileType("Scantron File"); 
			raw.setScantronFile(true);
			raw.setNewAssignment(isNewAssignmentByFileName);
			raw.setJustStructure(settings.isJustStructure());
			raw.setImportSettings(settings);
			return parseImportGeneric(raw);
		}
		//else
		
		raw.setMessages(err.toString());
		err = null; 
		raw.setErrorsFound(true); 
		raw.setImportSettings(settings);
		return parseImportGeneric(raw);
		

	}

	private String[] createScantronHeaderRow(String fileName)
	{
		String[] header = new String[2]; 
		header[RAWFIELD_FIRST_POSITION] = "Student Id"; 
		if (null != fileName && !"".equals(fileName))
		{
			header[RAWFIELD_SECOND_POSITION] = fileName; 
		}
		else
		{
			header[RAWFIELD_SECOND_POSITION] = "Scantron Item"; 
		}
		return header; 
	}
	
	private List<String[]> convertScantronRowsToTemplate(List<String[]> rows, String itemName) {
		
		if (null == rows) 
			return null;
		
		List<String[]> rv = new ArrayList<String[]>();
		int scoreIndex = -1;
		int idIndex = -1;
		for (Iterator<String[]> i=rows.iterator();i.hasNext();) {
			String[] row = i.next();
			if (isListAScantronHeaderRow(Arrays.asList(row))) {
				scoreIndex = findScantronScoreIndex(row);
				idIndex = findScantronIdIndex(row);
				rv.add(createScantronHeaderRow(itemName));
			} else if (row.length > scoreIndex && row.length > idIndex) {
				String[] newRow = new String[2];
				newRow[RAWFIELD_FIRST_POSITION] = row[idIndex];
				newRow[RAWFIELD_SECOND_POSITION] = row[scoreIndex];
				rv.add(newRow);
			}
			
		}
		
		
		
		return rv;
		
		
	}
	
	private int findScantronScoreIndex(String[] row) {
		int notFound = -1;
		if (null == row)
			return notFound; 
		
		for (int i=0;i<row.length;++i) {
			if (row[i].equalsIgnoreCase(scantronRescoreHeader) 
					|| row[i].equalsIgnoreCase(scantronScoreHeader) ) {
				return i;
			}
		}
		return notFound;
	}
	
	private int findScantronIdIndex(String[] row) {
		int notFound = -1;
		if (null == row)
			return notFound; 
		
		for (int i=0;i<row.length;++i) {
			if (row[i].equalsIgnoreCase(scantronStudentIdHeader)) {
				return i;
			}
		}
		return notFound;
	}


	private boolean isScantronSheetForJExcelApi(Sheet s) {
		Cell studentIdHeader = s.findCell(scantronStudentIdHeader);
		Cell scoreHeader = s.findCell(scantronScoreHeader);
		Cell reScoreHeader = s.findCell(scantronRescoreHeader);

		// GRBK-1044 scantron's can contain either a score header or a rescore header, but not necessarily both
		return (studentIdHeader != null && (scoreHeader != null || reScoreHeader != null)); 
	}
	
	private boolean isClickerSheetForJExcelApi(Sheet s) {
		// no way to find case-insensitively AFAICS
		Cell studentIdHeader = s.findCell(clickerStudentIdHeader);
		boolean clicker = studentIdHeader != null && clickerIgnoreColumns.length>0;
		for (String header : clickerIgnoreColumns) {
			clicker = clicker && s.findCell(header) != null;
		}

		return clicker; 
	}
	
	private boolean isClickerSheetFromPoi(org.apache.poi.ss.usermodel.Sheet sheet) {
		//skip all empty rows
		if(sheet!=null) {
			for (Row row : sheet) {
				if (isClickerHeaderRowPoi(row))
					return true;
			}
		}

		return false; /// empty sheet
	}
	
	private boolean couldBeNewItemTemplate(org.apache.poi.ss.usermodel.Sheet sheet) {
		//skip all empty rows
		if(sheet!=null)
			for (Row row : sheet) {
				for (org.apache.poi.ss.usermodel.Cell cell : row) {
					if (!"".equals(cell.getStringCellValue().trim())) {
						if(couldBeNewItemTemplateHeaderRow(row)) {
							return !hasTooManyItems(row, FileFormat.TEMPLATE);
						}
					}
				}
			}
		
		return false; /// empty sheet
	}
	

	


	private boolean couldBeNewItemTemplateHeaderRow(Row row) {
		boolean isTemplate = templateHeaderColumnSet.size()>0 
								&& row != null && row.getCell(0) != null;
		
		if(isTemplate)
			isTemplate = row.getCell(0).getStringCellValue().trim().equalsIgnoreCase(templateHeaderColumnSet.get(0));
		
		return isTemplate;
	}


	private boolean isClickerHeaderRowPoi(Row row) {
		if (clickerIgnoreColumns.length<=0)
			return false;
		for (String header : clickerIgnoreColumns ) {
			if(!poiRowContainsString(row, header)) {
				return false;
				}
			}
		
		return poiRowContainsString(row, clickerStudentIdHeader);
	}


	private boolean poiRowContainsString(Row row, String text) {
		if (null == text) 
			return false;
		text = text.trim().toLowerCase();
		
		if (row != null && row.cellIterator().hasNext())
			for (org.apache.poi.ss.usermodel.Cell cell : row) {
				if (cell.getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING
						&& cell.getStringCellValue().trim().toLowerCase().equals(text)) {
					return true;
				}
			}
		return false;
	}
	
	private boolean isClickerSheetCSV(ImportExportDataFile rawData) {
		
		// if it has structure, it's not a clicker file
		if( 0 < readDataForStructureInformation(rawData, buildRowIndicatorMap(), new HashMap<StructureRow, String[]>()))
			return false;
		
		boolean isClicker = rawData.getAllRows().size()>0;
		
		for (String[] row : rawData.getAllRows()) {
			List<String> rowLowerCase = new ArrayList<String>();
			for (String cell : Arrays.asList(row)) {
				rowLowerCase.add(cell.trim().toLowerCase());
			}
			isClicker = isClicker && isListAClickerHeaderRow(rowLowerCase);
			// accept first qualified match
			if (isClicker)
				return true;
		}
		return false;
	}


	private boolean isListAClickerHeaderRow(List<String> list) {
		boolean clicker = clickerIgnoreColumns.length>0;
		for (String header : clickerIgnoreColumns ) {
			clicker = clicker && list !=null && list.contains(header.trim().toLowerCase());
			}
		
		return clicker && list.contains(clickerStudentIdHeader.trim().toLowerCase());
	}
	
	/// this requires all header values and fails if the order is different
	private boolean couldBeNewItemTemplateHeaderRow(List<String> list) {
		boolean isTemplate = templateHeaderColumnSet.size()>0;
		return isTemplate && list.get(0).equalsIgnoreCase(templateHeaderColumnSet.get(0));
		}


	private Upload handlePoiSpreadSheet(org.apache.poi.ss.usermodel.Workbook inspread, 
			String fileName, boolean isNewAssignmentByFileName, ImportSettings settings)
	{
		log.debug("handlePoiSpreadSheet() called"); 
		// FIXME - need to do multiple sheets
		int numSheets = inspread.getNumberOfSheets();  
		if (numSheets > 0)
		{
			org.apache.poi.ss.usermodel.Sheet cur = inspread.getSheetAt(0);
			ImportExportDataFile ret = null;
			FileFormat fileFormatChosen = FileFormat.valueOf(settings.getFileFormatName());
			final String unexpectedFormatErrorMessage = i18n.getString("expectedFormat", 
					"The file format did not match your selection: ")
					+ lookupI18nFileFormat(fileFormatChosen.name());
			
			
			boolean mismatch = false;
			if (FileFormat.SCANTRON.equals(fileFormatChosen)) {
				boolean isScantron = isScantronSheetFromPoi(cur);
				if (isScantron)
				{
					log.debug("POI: Scantron");
					ret = processScantronXls(cur, fileName, settings); 
					ret.setNewAssignment(isNewAssignmentByFileName);
					ret.setScantronFile(true);
				} else {
					mismatch  = true;	
				}
			} else 
				if (FileFormat.TEMPLATE.equals(fileFormatChosen)  || FileFormat.CLICKER.equals(fileFormatChosen)   ) {
					if (couldBeNewItemTemplate(cur)) {
						ret = processNormalXls(cur, settings);
						ret.setScantronFile(false);
						ret.setNewAssignment(true);
					} else {
						mismatch = true;
					}
			} else
				if (FileFormat.FULL.equals(fileFormatChosen)) {
					ret = processNormalXls(cur, settings); ///cart before the horse but this is mid refactor
					if ( 0 >= readDataForStructureInformation(ret, buildRowIndicatorMap(), new HashMap<StructureRow, String[]>()) ) {
						mismatch = true;
					}
			} else
				if (FileFormat.NO_STRUCTURE.equals(fileFormatChosen)) {
					ret = processNormalXls(cur, settings); ///cart before the horse but this is mid refactor
					if ( 0 < readDataForStructureInformation(ret, buildRowIndicatorMap(), new HashMap<StructureRow, String[]>()) ) {
						mismatch = true;
					}
				}
				
				
				if (mismatch) {
					return emptyUploadFileWithNotes(unexpectedFormatErrorMessage);
				} 
				
				///#### everything checks out... continue ####
				ret.setJustStructure(settings.isJustStructure());
				ret.setImportSettings(settings);
				return parseImportGeneric(ret);
				
			
		}
		//else numsheets == 0 
		
		return emptyUploadFileWithNotes(i18n.getString("importValidSheetsMessage"));
		

		
	}

	private ImportExportDataFile processNormalXls(org.apache.poi.ss.usermodel.Sheet cur, ImportSettings settings) {
		log.debug("processNormalXls() called");
		ImportExportDataFile data = new ImportExportDataFile();
		int numCols = getNumberOfColumnsFromSheet(cur); 
		Iterator<Row> rowIter = cur.rowIterator(); 
		boolean headerFound = false;
		int id_col = -1; 
		while (rowIter.hasNext())
		{

			Row curRow = rowIter.next();  
			if (!headerFound)
			{
				id_col = readHeaderRow(curRow); 
				headerFound = true; 
				log.debug("Header Row # is " + id_col);
			}
			String[] dataEntity = new String[numCols]; 

			log.debug("numCols = " + numCols);
			
			for (int i = 0; i < numCols; i++) {
				org.apache.poi.ss.usermodel.Cell cl = curRow.getCell(i);
				String cellData;
				if (i == id_col && null != cl) {
					if (cl.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
						cellData = String.format("%.0f", cl
								.getNumericCellValue());
						log.debug("#1:cellData=" + cellData);
					} else {
						cellData = new HSSFDataFormatter().formatCellValue(cl);
						log.debug("#2:cellData=" + cellData);

					}
				} else {

					cellData = new HSSFDataFormatter().formatCellValue(cl);
					log.debug("#3:cellData=" + cellData);
				}
				if (cellData.length() > 0) {
					dataEntity[i] = cellData;
					log.debug("Setting dataEntity[" + i + "] = "
							+ dataEntity[i]);
				}
				else
				{
					dataEntity[i] = ""; 
					log.debug("Inserted empty string at " + i ); 
				}
			}
			data.addRow(dataEntity);
						
		}

		return data; 
	}

	private int getNumberOfColumnsFromSheet(org.apache.poi.ss.usermodel.Sheet cur) {
		int numCols = 0; 
		Iterator<Row> rowIter = cur.rowIterator(); 
		while (rowIter.hasNext())
		{
			Row curRow = rowIter.next(); 
			
			if (curRow.getLastCellNum() > numCols)
			{
				numCols = curRow.getLastCellNum(); 
			}
		}
		return numCols;
	}


	private int readHeaderRow(Row curRow) {
		int ret = -1; 
		Iterator<org.apache.poi.ss.usermodel.Cell> cellIterator = curRow.cellIterator(); 
		// FIXME - need to decide to take this out into the institutional adviser 

		while (cellIterator.hasNext())
		{
			org.apache.poi.ss.usermodel.Cell cl = cellIterator.next();
			String cellData =  new org.apache.poi.ss.usermodel.DataFormatter().formatCellValue(cl).toLowerCase();

			if ("student id".equals(cellData))
			{
				return cl.getColumnIndex(); 
			}

		}
		return ret; 
	}

	private ImportExportDataFile processScantronXls(org.apache.poi.ss.usermodel.Sheet cur, String fileName, ImportSettings settings) {
		ImportExportDataFile data = new ImportExportDataFile(); 
		Iterator<Row> rowIter = cur.rowIterator(); 
		StringBuilder err = new StringBuilder("Scantron File with errors"); 
		boolean stop = false; 

		org.apache.poi.ss.usermodel.Cell studentIdHeader = findCellWithTextonSheetForPoi(cur, scantronStudentIdHeader);
		org.apache.poi.ss.usermodel.Cell scoreHeader = findCellWithTextonSheetForPoi(cur, scantronScoreHeader);
		if (studentIdHeader == null)
		{
			err.append("There is no column with the header student_id");
			stop = true; 
		}

		if (scoreHeader == null)
		{
			// check for a rescore header - GRBK-407
			scoreHeader = findCellWithTextonSheetForPoi(cur, scantronRescoreHeader);
			if(scoreHeader == null) {
				err.append("There is no column with the header score");
				stop = true; 
			}
		}

		if (! stop) 
		{
			data.addRow(createScantronHeaderRow(fileName));
			
			// GRBK-514
			if(settings.isJustStructure()){
				return data;
			}
			
			while (rowIter.hasNext())
			{ 
				Row curRow = rowIter.next();  
				org.apache.poi.ss.usermodel.Cell score = null;
				org.apache.poi.ss.usermodel.Cell id = null; 

				id = curRow.getCell(studentIdHeader.getColumnIndex());
				score = curRow.getCell(scoreHeader.getColumnIndex()); 
				if (id == null )
				{
					err.append("Skipped Row "); 
					err.append(curRow.getRowNum());
					err.append(" does not have a student id column<br>"); 
					continue; 
				}
				String idStr, scoreStr; 
				
				// IF the row contains the header, meaning it is the header row, we want to skip it. 
				if (!id.equals(studentIdHeader))
				{
					// FIXME - need to decide if this is OK for everyone, not everyone will have an ID as a 
					idStr = getDataFromCellAsStringRegardlessOfCellType(id, false); 
					scoreStr = getDataFromCellAsStringRegardlessOfCellType(score, true); 
					String[] ent = new String[2];
					ent[0] = idStr; 
					ent[1] = scoreStr;

					data.addRow(ent); 
				}
			}
		}
		return data; 

	}

	private String getDataFromCellAsStringRegardlessOfCellType(org.apache.poi.ss.usermodel.Cell c, boolean decimal)
	{
		String ret = "";
		String fmt = "%.0f"; 
		if (decimal)
		{
			fmt = "%.2f"; 
		}
		if (c != null)
		{
			if (c.getCellType() == HSSFCell.CELL_TYPE_STRING)
			{
				ret = c.getRichStringCellValue().getString();
			}
			else if (c.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
			{
				ret = String.format(fmt, c.getNumericCellValue());
			} // else we want to return "" 
		} // else we want to return "" 
		return ret; 
	}
	
	// POI doesn't provide the findCell method that jexcelapi does, so we'll simulate it..  We return the first cell we find with the text in searchText
	// if we can't find it, we return null. 
	// 

	private org.apache.poi.ss.usermodel.Cell findCellWithTextonSheetForPoi(org.apache.poi.ss.usermodel.Sheet cur, String searchText)
	{
		if (searchText == null || cur == null) 
		{
			return null; 			
		}

		Iterator<Row> rIter = cur.rowIterator(); 

		while (rIter.hasNext())
		{
			Row curRow = rIter.next(); 
			Iterator<org.apache.poi.ss.usermodel.Cell> cIter = curRow.cellIterator(); 

			while (cIter.hasNext())
			{
				org.apache.poi.ss.usermodel.Cell curCell = cIter.next(); 

				if (curCell.getCellType() == HSSFCell.CELL_TYPE_STRING)
				{
					if ( searchText.equals( curCell.getRichStringCellValue().getString() ) )
					{
						return curCell; 
					}
				}
			}
		}
		return null; 
	}
	
	private boolean isScantronSheetFromPoi(org.apache.poi.ss.usermodel.Sheet cur) {
		Iterator<Row> rowIter = cur.rowIterator(); 
		while (rowIter.hasNext())
		{
			Row curRow = rowIter.next();  
			org.apache.poi.ss.usermodel.Cell possibleHeader = curRow.getCell(0); 

			if (possibleHeader != null && possibleHeader.getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING 
					&&  scantronStudentIdHeader.equals(possibleHeader.getRichStringCellValue().getString()) )
			{
				return true; 
			}
		}
		// If after all that, we don't find a row starting with SCANTRON_HEADER_STUDENT_ID, we're not a scantron.. 
		return false;
	}
	
	
	/*
	 * This import path does not check filetypes and/or file extensions
	 * It just takes a stream reader and assumes it is CSV
	 * (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.server.ImportExportUtility#parseImportCSV(java.lang.String, java.io.Reader, org.sakaiproject.gradebook.gwt.client.api.ImportSettings)
	 */

	public Upload parseImportCSV(Reader reader, ImportSettings importSettings) 
			throws InvalidInputException, FatalException {
		
		ImportExportDataFile rawData = new ImportExportDataFile(); 
		if(importSettings != null) {
			rawData.setJustStructure(importSettings.isJustStructure());
		}else{
			importSettings = new ImportSettingsImpl();	
			log.warn("No import settings supplied to parseImportCSV");
		}
		
		CSVReader csvReader = new CSVReader(reader);
		String[] ent;
		try {
			while ( (ent = csvReader.readNext() ) != null)
			{
				rawData.addRow(ent); 
			}
			csvReader.close();
		} catch (IOException e) {
			// FIXME - error handling
			log.error(e);
			rawData.setErrorsFound(true);
			rawData.setMessages(e.getMessage());
		}

		rawData.setFileType("CSV file"); 
		rawData.setImportSettings(importSettings);
		return parseImportGeneric(rawData);
	}
	
	

	

	/*
	 * Some background on how the actual file data looks is needed for this method. 
	 * 
	 * There are three parts to the import/export file.  
	 * 
	 * The first part is
	 * structure information.  Structure information is data that is included when 
	 * exporting the GB with structure.  This data generally has the first field in
	 * the array as blank.  The next field has an identifier that signifies the type 
	 * of structure data it represents, and then the rest of the row contains data for 
	 * that type.  
	 * 
	 * The second part is what we call the header row.  The header row can be thought 
	 * of as the first row in the spreadsheet if you remove all the structure information.  
	 * It contains column headers for the remaining rows in the spreadsheet. This row 
	 * has data in the first entry which must be contained in the nameColumns static array 
	 * in the beginning of this file. 
	 * 
	 * The third and last part are the student rows which is by definition anything after 
	 * the header row.  Each student row contains data for an individual student.  
	 * Each of these rows has positional data based on the header row.  
	 * 
	 * This method has two goals, bring in the structure information, and find where the 
	 * header row is for later use. 
	 * 
	 * note: if return value < 1 then it can be assumed to not be a gradebook with structure
	 *   
	 */
	private int readDataForStructureInformation(ImportExportDataFile rawData, Map<String, StructureRow> structureRowIndicatorMap, Map<StructureRow, String[]> structureColumnsMap) 
	{
		log.debug("readDataForStructureInformation() called");
		int curRowNumber = 0;
		int retRows = -1; 
		boolean headerFound = false; 
		String[] curRow;
		rawData.startReading();
		while ( !headerFound && (curRow = rawData.readNext()) != null) 
		{
			log.debug(StringUtils.join(curRow, ","));
			if (curRow.length == 0) {//empty rows are ignored
				curRowNumber++;
				continue;
			}
			String firstColumnLowerCase = curRow[0].toLowerCase();
			log.debug("SI[" + curRowNumber + "]: firstColumnLowerCase=" + firstColumnLowerCase);
			/*
			 *  So if we're not a header we are probably a structure row.  We're assuming the 
			 *  import spreadsheet is built as above in proper order
			 */
			if (!headerRowIndicatorSet.contains(firstColumnLowerCase)) {
				processStructureRow(curRow, structureColumnsMap, structureRowIndicatorMap, firstColumnLowerCase, curRowNumber);
			} else {
				retRows = curRowNumber; 
				headerFound = true;
			}
			curRowNumber++; 
		}
		return retRows; 
	}

	private void processStructureRow(String[] curRow,
			Map<StructureRow, String[]> structureRowMap,
			Map<String, StructureRow> structureRowIndicatorMap,
			String firstColumnLowerCase, int curRowNumber) {
		
		log.debug("Processed non header row for row #" + curRowNumber);
		// So for each column in the row, check to see if the text is in the
		// set of structure rows possible, if it is, save it off in the map. 
		for (int i=0;i<curRow.length;i++) {
			if (curRow[i] != null && !curRow[i].equals("")) 
			{

				String columnLowerCase = curRow[i].trim().toLowerCase();
				if (log.isDebugEnabled())
					log.debug("SI: columnLowerCase=" + columnLowerCase);
				StructureRow structureRow = structureRowIndicatorMap.get(columnLowerCase);

				if (structureRow != null) {
					structureRowMap.put(structureRow, curRow);
				}
			}
		}

	}

	private boolean isScantronHeader(String in) 
	{
		return scantronIgnoreSet.contains(in);
	}
	
	private boolean isClickerHeader(String in) {
		return clickerIgnoreSet.contains(in);
	}

	private void readInHeaderRow(ImportExportDataFile rawData, ImportExportInformation ieInfo, int startRow) {
		String[] headerRow = null;
		headerRow = rawData.getRow(startRow);
		
		if (headerRow == null)
			return;
		
		ImportHeader[] headers = new ImportHeader[headerRow.length];
		
		for (int i = 0; i < headerRow.length; i++) {
			String text = headerRow[i];
			ImportHeader header = null;

			header = handleHeaderRowEntry(text, i, ieInfo); 
			
			/*
			 * Note The above handleHeaderRowEntry can return null, but checking for 
			 * null is a check, so I think not checking and just assigning it in the array 
			 * is more efficient. 
			 * 
			 */
			
			headers[i] = header;
		}
		ieInfo.setHeaders(headers);
		log.debug("XXX: readInHeaderInfo() finished");
	}

	private boolean isName(String in)
	{
		return nameSet.contains(in);
	}
	private boolean isId(String in)
	{
		return idSet.contains(in);
	}
	private ImportHeader handleHeaderRowEntry(String text, int entryNumber, ImportExportInformation ieInfo) {
		String lowerText = text == null ? null : text.trim().toLowerCase();
		ImportHeader header = null;
		/* 
		 * FIXME - There's gotta be a better way to handle this. 
		 * 
		 */
		// Empty rows,scantron,clicker headers need to be skipped. 
		if (isEmpty(lowerText) || isScantronHeader(lowerText) || isClickerHeader(lowerText)) { 
			return new ImportHeader(Field.S_EMPTY, text, entryNumber); 
		} else if (isName(lowerText)) {
			header = new ImportHeader(Field.S_NAME, text, entryNumber);
			header.setId("NAME");
		} else if (isId(lowerText)) {
			header = new ImportHeader(Field.S_ID, text, entryNumber);
			header.setId("ID");
			ieInfo.trackActiveHeaderIndex(entryNumber);
		} else if (lowerText.equalsIgnoreCase("course grade")) {
			header = new ImportHeader(Field.S_CRS_GRD, text, entryNumber);
		} else if (lowerText.equalsIgnoreCase("calculated grade")) {
			header = new ImportHeader(Field.S_CALC_GRD, text, entryNumber);
		} else if (lowerText.equalsIgnoreCase("letter grade")) {
			header = new ImportHeader(Field.S_LTR_GRD, text, entryNumber);
		} else if (lowerText.equalsIgnoreCase("audit grade")) {
			header = new ImportHeader(Field.S_ADT_GRD, text, entryNumber);
		} else if (lowerText.equalsIgnoreCase("grade override")) {
			header = new ImportHeader(Field.S_GRB_OVRD, text, entryNumber);
			ieInfo.trackActiveHeaderIndex(entryNumber);
		} else {
			header = buildItemOrCommentHeader(entryNumber, text, lowerText, ieInfo); 			
		}
		return header;
	}

	private String removeIndicatorsFromAssignmentName(String name)
	{
		if (name.contains(AppConstants.EXTRA_CREDIT_INDICATOR))
		{
			name = name.replace(AppConstants.EXTRA_CREDIT_INDICATOR, "");
		}
		
		if (name.contains(AppConstants.UNINCLUDED_INDICATOR))
		{
			name = name.replace(AppConstants.UNINCLUDED_INDICATOR, ""); 
		}
		
		if (name.contains(AppConstants.RELEASE_SCORES_INDICATOR)) {
			
			name = name.replace(AppConstants.RELEASE_SCORES_INDICATOR, "");
		}
		
		if (name.contains(AppConstants.GIVE_UNGRADED_NO_CREDIT_INDICATOR)) {
			
			name = name.replace(AppConstants.GIVE_UNGRADED_NO_CREDIT_INDICATOR, "");
		}
		
		if (name.startsWith(AppConstants.COMMENTS_INDICATOR)) {
			name = name.substring(AppConstants.COMMENTS_INDICATOR.length());
		}
		return name; 
	}
	
	private ImportHeader buildItemOrCommentHeader(int entryNumber, String text,
			String lowerText, ImportExportInformation ieInfo) {
		
		ImportHeader header = null; 
		String name = null;
		String points = null;
		
		boolean isExtraCredit = text.contains(AppConstants.EXTRA_CREDIT_INDICATOR);
		boolean isUnincluded = text.contains(AppConstants.UNINCLUDED_INDICATOR);
		boolean isReleaseScores = text.contains(AppConstants.RELEASE_SCORES_INDICATOR);
		boolean isGiveungradedNoCredit = text.contains(AppConstants.GIVE_UNGRADED_NO_CREDIT_INDICATOR);
		boolean isComment = text.startsWith(AppConstants.COMMENTS_INDICATOR);
		text = removeIndicatorsFromAssignmentName(text);
		name = text; 
		points = getPointsFromName(name, entryNumber); 
		name = removePointsInfoFromName(name, entryNumber); 
		if (name != null) {
			header = createHeaderForItemOrComment(text, name, entryNumber, points, isExtraCredit, isUnincluded, isReleaseScores, isGiveungradedNoCredit, isComment, ieInfo);
		}
		return header; 
	}
	
	

	private ImportHeader createHeaderForItemOrComment(String text,
			String name, int entryNumber, String points, boolean isExtraCredit,
			boolean isUnincluded, boolean isReleaseScores, boolean isGiveungradedNoCredit, boolean isComment, ImportExportInformation ieInfo) {
		ImportHeader header = null; 
		
		if (isComment) {
			header = new ImportHeader(Field.S_COMMENT, text, entryNumber);
			ieInfo.trackActiveHeaderIndex(entryNumber);
		} else {
			header = new ImportHeader(Field.S_ITEM, name, entryNumber);
			header.setExtraCredit(isExtraCredit);
			header.setUnincluded(isUnincluded);
			header.setReleaseScores(isReleaseScores);
			header.setGiveungradedNoCredit(isGiveungradedNoCredit);
			header.setPoints(points);
			ieInfo.trackActiveHeaderIndex(entryNumber);
		}
		header.setHeaderName(name);

		return header; 
	}

	private String removePointsInfoFromName(String text, int entryNumber) {
		String name = text;
		int startParenthesis = text.indexOf("[");
		if (startParenthesis >= 0)
		{
			name = text.substring(0, startParenthesis);
		}
		
		if (log.isDebugEnabled())
			log.debug("X: Column " + entryNumber + " name is " + name);

		if (name != null)
			return name.trim();
		//else
		return name; 
	}

	private String getPointsFromName(String text, int entryNumber) {
		
		String points = null; 
		
		int startParenthesis = text.indexOf("[");
		int endParenthesis = text.indexOf("pts]");

		if (endParenthesis == -1)
			endParenthesis = text.indexOf("]");

		if (startParenthesis != -1 && endParenthesis != -1
				&& endParenthesis > startParenthesis + 1) {
			if (log.isDebugEnabled())
				log.debug("X: Column " + entryNumber + " has pts indicated");
			points = text.substring(startParenthesis + 1, endParenthesis);
			if (log.isDebugEnabled())
				log.debug("X: Column " + entryNumber + " points are " + points);

		}
		return points;
	}

	private void readInGradeDataFromImportFile(ImportExportDataFile rawData, 
			ImportExportInformation ieInfo, Map<String, UserDereference> userDereferenceMap, 
			List<Learner> importRows, int startRow, Gradebook2ComponentService service) {
		String[] curRow; 
		rawData.goToRow(startRow);
		while ((curRow = rawData.readNext()) != null) {

			Learner learnerRow = new LearnerImpl();
			
			GradeType gradeType = ieInfo.getGradebookItemModel().getGradeType();
			
			for (ImportHeader importHeader : ieInfo.findActiveHeaders()) {
				
				if (importHeader == null)
					continue;

				int colIdx = importHeader.getColumnIndex();
				String id = importHeader.getId();
				if (colIdx >= curRow.length)
					continue;
				if (curRow[colIdx] != null && !curRow[colIdx].equals("") && importHeader.getField() != null) { 
					decorateLearnerForSingleHeaderAndRowData(importHeader, curRow, learnerRow, userDereferenceMap, 
							ieInfo, gradeType, colIdx, id);
				}
			}
			
			importRows.add(learnerRow);
		}	
		
	}
	
	private void decorateLearnerForSingleHeaderAndRowData(ImportHeader importHeader, String[] rowData, 
			Learner learnerRow, Map<String, UserDereference> userDereferenceMap, 
			ImportExportInformation ieInfo, GradeType gradeType, int colIdx, String id)
	{

		switch (importHeader.getField()) {
		case S_ID:
			decorateLearnerIdFromHeaderAndRowData(learnerRow, userDereferenceMap, rowData, colIdx, ieInfo);
			break;
		case S_NAME:
			learnerRow.set(LearnerKey.S_DSPLY_NM.name(), rowData[colIdx]);
			break;
		case S_GRB_OVRD:
			learnerRow.set(LearnerKey.S_OVRD_GRD.name(), rowData[colIdx]);
			break;
		case S_ITEM:
			// GRBK-760
			if("".equals(rowData[colIdx].trim())) {
				break;
			}
			decorateLearnerItemFromHeaderAndRowData(learnerRow, importHeader, rowData, colIdx, ieInfo, gradeType, id);
			break;
		case S_COMMENT:
			learnerRow.set(Util.buildCommentKey(id), Boolean.TRUE);
			learnerRow.set(Util.buildCommentTextKey(id), rowData[colIdx]);
			break;
		}
	
	}

	private void decorateLearnerItemFromHeaderAndRowData(Learner learnerRow, ImportHeader importHeader, String[] rowData,
			int colIdx, ImportExportInformation ieInfo, GradeType gradeType, String id) {

		boolean isFailure = false;
		
		// GRBK-668 : For a letter grade type gradebook, we convert all numeric grades into letter grades
		// GRBK-1322 : spaces around letter grades cause failures ... should never be null, but just in case, null-check
		rowData[colIdx] = null == rowData[colIdx] ? null : rowData[colIdx].trim();
		String originalGrade = rowData[colIdx];
		if(GradeType.LETTERS == gradeType && Util.isNumeric(originalGrade)) {
			BigDecimal numericGrade = new BigDecimal(originalGrade);
			String letterGrade = gradeCalculations.convertPercentageToLetterGrade(numericGrade);
			rowData[colIdx] = letterGrade;
			learnerRow.set(id, rowData[colIdx]);
			learnerRow.set(id + AppConstants.ACTUAL_SCORE_SUFFIX, originalGrade);
			learnerRow.set(Util.buildConvertedMessageKey(id), "Converted numeric to letter grade");
			learnerRow.set(Util.buildConvertedGradeKey(id), originalGrade);
			log.debug("#####: Converting numberic grade [" + originalGrade + "] to a letter grade [" + letterGrade + "]");
			return;
		}
		else if(GradeType.LETTERS == gradeType && !Util.isNumeric(originalGrade)) {
		
			if(!service.isValidLetterGrade(rowData[colIdx])) {
				ieInfo.setInvalidScore(true);
				String failedId = Util.buildFailedKey(id);
				learnerRow.set(failedId, "This is an invalid letter grade");
			}
			
			learnerRow.set(id, rowData[colIdx]);
			return;
		}
		// GRBK-806 - Percentages are valid if they are in range of 0..100 inclusive. 
		// GRBK-1056 - give scantrons a pass since we control max points in the client]
		// GRBK-1105 - We need to do this error checking if it is scantron 
		// but not percentages mode as the client side stuff only works if the 
		// wizard code is called, and that only happens for percentages... 
		if (! (ieInfo.getImportSettings().isScantron() && GradeType.PERCENTAGES == gradeType) ){
			if (GradeType.PERCENTAGES == gradeType)
			{
				double d = Double.parseDouble(rowData[colIdx]);
				if (d < 0.0 || d > 100.0)
				{
					isFailure = true; 
					ieInfo.setInvalidScore(true);
				}
			}
			else
			{
				try {
					double d = Double.parseDouble(rowData[colIdx]);
					Item item = importHeader.getItem();
					isFailure = handleSpecialPointsCaseForItem(item, d, ieInfo); 
				} catch (NumberFormatException nfe) {
					// This is not necessarily an exception, for example, we might be
					// reading letter grades
				
					if (gradeType != GradeType.LETTERS || !service.isValidLetterGrade(rowData[colIdx])) {
						log.info("Caught exception " + nfe + " while importing grades.", nfe); 
						isFailure = true;
						ieInfo.setInvalidScore(true);
					} 
				}
			}
		}
		if (isFailure) {
			String failedId = Util.buildFailedKey(id);
			learnerRow.set(failedId, "This entry is not valid");
		}
		learnerRow.set(id, rowData[colIdx]);
		
	}

	private boolean handleSpecialPointsCaseForItem(Item item, double d, ImportExportInformation ieInfo) {
		
		boolean isFailure = false;
		
		if (item != null) {
		
			Double points = item.getPoints();
		
			if (points != null) {
				
				if (points.doubleValue() < d) {

					// GRBK-629 : We don't auto adjust total points for GradeItems that
					// are created new via the import process depending on entered user grades

					// If a grade is higher than total points possible, we flag an error
					isFailure = true;
					ieInfo.setInvalidScore(true);
				}
			}
		} 
		return isFailure; 
	}

	private void decorateLearnerIdFromHeaderAndRowData(
			Learner learnerRow,
			Map<String, UserDereference> userDereferenceMap, String[] rowData,
			int colIdx, ImportExportInformation ieInfo) {
		
		String userImportId = rowData[colIdx];
		learnerRow.setExportUserId(userImportId);
		learnerRow.setStudentDisplayId(userImportId);
		
		UserDereference userDereference = userDereferenceMap.get(userImportId);

		if (userDereference != null) {
			learnerRow.setIdentifier(userDereference.getUserUid());
			learnerRow.setStudentName(userDereference.getDisplayName());
			learnerRow.setLastNameFirst(userDereference.getLastNameFirst());
			learnerRow.setStudentDisplayId(userDereference.getDisplayId());
			learnerRow.setUserNotFound(Boolean.FALSE);
		} else {
			learnerRow.setIdentifier(userImportId);
			learnerRow.setLastNameFirst("User not found");
			learnerRow.setUserNotFound(Boolean.TRUE);
			ieInfo.setUserNotFound(true);
		}
		
	}

	private CategoryType getGradebookCategoryTypeFromGradebookRow(String[] gradebookRow, GradeItem gradebookItemModel)
	{
		CategoryType cType = gradebookItemModel.getCategoryType();
		String categoryType = null;
		if (gradebookRow.length >= 4)
			categoryType = gradebookRow[3];

		if (categoryType != null) {
			if (CategoryType.NO_CATEGORIES.getDisplayName().equals(categoryType))
				cType = CategoryType.NO_CATEGORIES;
			else if (CategoryType.SIMPLE_CATEGORIES.getDisplayName().equals(categoryType))
				cType = CategoryType.SIMPLE_CATEGORIES;
			else if (CategoryType.WEIGHTED_CATEGORIES.getDisplayName().equals(categoryType))
				cType = CategoryType.WEIGHTED_CATEGORIES;

		}
		return cType; 

	}
	
	private String getGradebookNameFromGradebookRow(String[] gradebookRow, GradeItem gradebookItemModel)
	{
		String gradebookName = gradebookItemModel.getName();
		if (gradebookRow.length >= 3)
			gradebookName = gradebookRow[2];
		return gradebookName; 
	}
	
	private GradeType getGradeTypeFromGradebookRow(String[] gradebookRow, GradeItem gradebookItemModel)
	{
		GradeType gType =  gradebookItemModel.getGradeType();
		String gradeType = null;  
		if (gradebookRow.length >= 5)
			gradeType = gradebookRow[4];
		
		if (gradeType != null) {
			if (getDisplayName(GradeType.PERCENTAGES).equals(gradeType))
				gType = GradeType.PERCENTAGES;
			else if (getDisplayName(GradeType.POINTS).equals(gradeType))
				gType = GradeType.POINTS;
			else if (getDisplayName(GradeType.LETTERS).equals(gradeType))
			{
				gType = GradeType.LETTERS;				
			}
		}

		return gType; 
		
	}
	
	private void processStructureInformationForGradebookRow(GradeItem gradebookItemModel, String[] gradebookRow)
	{

		if (gradebookRow != null && gradebookItemModel != null) {
			CategoryType cType = getGradebookCategoryTypeFromGradebookRow(gradebookRow, gradebookItemModel);
			GradeType gType = getGradeTypeFromGradebookRow(gradebookRow, gradebookItemModel);
			String gradebookName = getGradebookNameFromGradebookRow(gradebookRow, gradebookItemModel);
			gradebookItemModel.setCategoryType(cType);
			gradebookItemModel.setGradeType(gType);
			gradebookItemModel.setName(gradebookName);
		}

	}
		
	private void processStructureInformation(ImportExportInformation ieInfo, Map<StructureRow, String[]> structureColumnsMap) throws InvalidInputException
	{
		// Now, modify gradebook structure according to the data stored
		String[] gradebookRow = structureColumnsMap.get(StructureRow.GRADEBOOK);
		GradeItem gradebookItemModel = (GradeItem) ieInfo.getGradebookItemModel();
		
		// this reads from the "Gradebook:" row and processes its options. 
		processStructureInformationForGradebookRow(gradebookItemModel, gradebookRow);
		// this reads from a set of  having to do with display and scaled EC liens. 
		processStructureInformationForDisplayAndScaledOptions(gradebookItemModel, ieInfo, structureColumnsMap);
		
		// If we're in no categories mode, either from the import file itself or
		// because the import file doesn't contain any category info and the gb is a no cats gb
		// then we skip the rest
		if (gradebookItemModel.getCategoryType()  == CategoryType.NO_CATEGORIES)
			return;
		
		String[] categoryRow = structureColumnsMap.get(StructureRow.CATEGORY);
		String[] percentGradeRow = structureColumnsMap.get(StructureRow.PERCENT_GRADE);
		String[] dropLowestRow = structureColumnsMap.get(StructureRow.DROP_LOWEST);
		String[] equalWeightRow = structureColumnsMap.get(StructureRow.EQUAL_WEIGHT);
		String[] weightItemsByPointsRow = structureColumnsMap.get(StructureRow.WEIGHT_ITEMS_BY_POINTS);

		/*
		 *  In order to understand this, one needs to know that the import data is positional 
		 *  in nature.  Categories are at a particular position in the file, and items are 
		 *  relational to the category. So if category A starts at column #4 and the next category 
		 *  starts at column #10, then items in columns 4-10 are in the category A. This is true
		 *  with other row structure data such as percent grade, drop lowest, etc.  
		 *  
		 *  Also, I realize this may not be the most efficient way of doing this.  I'm aiming for 
		 *  being readable/understandable over raw efficiency.  Also, there are a known number of 
		 *  structure rows, so iterating in memory over this list should not be that bad. 
		 *  
		 */
		
		List<CategoryPosition> categoryPositions; 
		categoryPositions = processCategoryRow(categoryRow, gradebookItemModel, ieInfo);
		if (categoryPositions != null)
		{
			processExtraCategoryRelatedData(weightItemsByPointsRow, equalWeightRow, percentGradeRow, dropLowestRow, gradebookItemModel, categoryPositions);
		}
		else 
			/*
			 * The category row either didn't exist, or there was nothing in it.  The old way of doing things said we had 
			 * to build the category list, so that's what we'll do. 
			 */
		{
			populatecategoryIdItemMap(ieInfo, gradebookItemModel); 
			addDefaultCategoryIfNeeded(ieInfo, gradebookItemModel); 
			
		}
	}
	
	private void addDefaultCategoryIfNeeded(ImportExportInformation ieInfo,
			GradeItem gradebookItemModel) {
		if (ieInfo.getCategoryIdItemMap().get("-1") == null) {
			GradeItem categoryModel = new GradeItemImpl();
			categoryModel.setIdentifier("-1");
			categoryModel.setCategoryId(Long.valueOf(-1l));
			categoryModel.setItemType(ItemType.CATEGORY);
			categoryModel.setName(AppConstants.DEFAULT_CATEGORY_NAME);

			gradebookItemModel.addChild((GradeItem)categoryModel);
			ieInfo.getCategoryIdItemMap().put("-1", categoryModel);
		}
		
	}

	private void populatecategoryIdItemMap(ImportExportInformation ieInfo,
			GradeItem gradebookItemModel) {
		
		List<GradeItem> children = gradebookItemModel.getChildren();

		if (children != null) {
			for (GradeItem categoryModel : children) {
				ieInfo.getCategoryIdItemMap().put(categoryModel.getIdentifier(), categoryModel);
			}
		}		
	}

	private void processExtraCategoryRelatedData(String[] weightItemsByPointsRow, String[] equalWeightRow, String[] percentGradeRow, String[] dropLowestRow,
			GradeItem gradebookItemModel,
			List<CategoryPosition> categoryPositions) {
			for (CategoryPosition p : categoryPositions)
			{
				int col = p.getColNumber(); 
				GradeItem categoryModel = p.getCategory(); 
				processPercentGradeRow(percentGradeRow, categoryModel, col);
				processEqualWeightRow(equalWeightRow, categoryModel, col); 
				processDropLowestRow(dropLowestRow, categoryModel, col);
				processWeightItemsByPointsRow(weightItemsByPointsRow, categoryModel, col);
			}
	}
	
	private void processWeightItemsByPointsRow(String[] weightItemsByPointsRow, GradeItem categoryModel, int col) {
		// GRBK-627
		
		if(null != weightItemsByPointsRow) {
			
			if(weightItemsByPointsRow.length > col) {
				
				String curWeightItemsByPoints = weightItemsByPointsRow[col];
				
				if(!isEmpty(curWeightItemsByPoints)) {
					
					try {
						
						boolean isWeightItemsByPoints = Boolean.parseBoolean(curWeightItemsByPoints);
						categoryModel.setEnforcePointWeighting(Boolean.valueOf(isWeightItemsByPoints));
					}
					catch(NumberFormatException nfe) {
						
						log.info("Failed to parse " + curWeightItemsByPoints + " as an Boolean for col " + col + " on Weight Items By Points ROW.", nfe);
					}
				}
			}
		}
	}

	private void processEqualWeightRow(String[] equalWeightRow, GradeItem categoryModel, int col) {
		if (equalWeightRow != null)
		{
			if (equalWeightRow.length > col)
			{
				String curEqualWeight = equalWeightRow[col]; 
					
				if (!isEmpty(curEqualWeight))
				{
					try {
						boolean isEqualWeight = Boolean.parseBoolean(curEqualWeight);
						categoryModel.setEqualWeightAssignments(Boolean.valueOf(isEqualWeight));
					} catch (NumberFormatException nfe) {
						log.info("Failed to parse " + curEqualWeight + " as an Boolean for col " + col + " on Equal Weight ROW.", nfe);
					}
				}
			}
		}		
	}

	private void processDropLowestRow(String[] dropLowestRow,
			GradeItem categoryModel, int col) {
		if (dropLowestRow != null)
		{
			if (dropLowestRow.length > col)
			{
				String curDropLowest = dropLowestRow[col]; 

				if (!isEmpty(curDropLowest))
				{
					try {
						int dL = Integer.parseInt(curDropLowest);
						categoryModel.setDropLowest(Integer.valueOf(dL));
					} catch (NumberFormatException nfe) {
						log.warn("Failed to parse " + curDropLowest+ " as an Integer for col " + col + " on Drop Lowest row.", nfe);
					}

				}
			}				
		}
		
	}

	private void processPercentGradeRow(String[] percentGradeRow,
			GradeItem categoryModel, int col) 
	{
		if (percentGradeRow != null)
		{
			if (percentGradeRow.length > col)
			{
				String curPercentGrade = percentGradeRow[col]; 
				if (!isEmpty(curPercentGrade))
				{
					try {
						curPercentGrade = curPercentGrade.replace("%", "");
						double pG = Double.parseDouble(curPercentGrade);
						categoryModel.setPercentCourseGrade(Double.valueOf(pG));
						categoryModel.setWeighting(Double.valueOf(pG));
					} catch (NumberFormatException nfe) {
						log.info("Failed to parse " + curPercentGrade + " as a Double for col " + col + " on percent Grade row.", nfe);
					}

				}
			}

			
		}
	}

	private boolean isEmpty(String in) 
	{
		if (in != null)
		{
			return "".equals(in.trim());
		}
		else
		{
			return true; 
		}
	}
	private List<CategoryPosition> processCategoryRow(String[] categoryRow,
			GradeItem gradebookItemModel, ImportExportInformation ieInfo) {
		
		if (categoryRow == null  || categoryRow.length < 3)
			return null;
		
		List<CategoryPosition> ret = new ArrayList<CategoryPosition>();
		Map<String, GradeItem> categoryMap = new HashMap<String, GradeItem>();
		
		addExistingCategoriesFromGradebookItemModelToMap(categoryMap, gradebookItemModel); 
		// First position is a blank, second is the Category row identifier.
		// Since this had already been read in as a category, probably don't 
		// need to check twice
		
		processActualCategoryRowData(categoryRow, categoryMap, gradebookItemModel, ret, ieInfo);
		
		return ret;			

	}

private void processActualCategoryRowData(String[] categoryRow, Map<String, GradeItem> categoryMap, 
		GradeItem gradebookItemModel, List<CategoryPosition> catpositions, ImportExportInformation ieInfo) {

	// This array is a quick map from assignment position to category ID.  
	String[] assignmentToCategoryQuick = new String[(ieInfo.getHeaders() != null ? ieInfo.getHeaders().length : 0)];
	String currentCategoryId = null;

	// First position should be blank, second should have Category: in it.  We'll start at two.
	for (int i=2;i<categoryRow.length;i++) {
		String curCategoryString = categoryRow[i];  
		
		if (!isEmpty(curCategoryString))
		{
			GradeItem categoryModel = null;
			categoryModel = buildOrGetExistingCategoryForUpdate(i,curCategoryString, categoryMap, gradebookItemModel);
			curCategoryString = removeIndicators(curCategoryString); 
			// At this point we either have a current category or we made one.  So lets save off the position for posterity...	
			CategoryPosition pos = new CategoryPosition(i, categoryModel, curCategoryString);
			catpositions.add(pos);
			if (categoryModel.getIdentifier() != null && !categoryModel.getIdentifier().equals("null")) {
				currentCategoryId = categoryModel.getIdentifier();
				ieInfo.getCategoryIdItemMap().put(currentCategoryId, categoryModel);
				assignmentToCategoryQuick[i] = currentCategoryId;			
			}
			else
			{
				currentCategoryId = null; 
				assignmentToCategoryQuick[i] = currentCategoryId;							
			}

			if (categoryModel.getCategoryId() != null) {
				String categoryIdAsString = String.valueOf(categoryModel.getCategoryId());
				ieInfo.getCategoryIdNameMap().put(categoryIdAsString, categoryModel.getName());
			}
			categoryModel.setChecked(true);
		
		}
		else
		{
			assignmentToCategoryQuick[i] = currentCategoryId;
		}
	}
	ieInfo.setAssignmentPositionToCategoryIdQuick(assignmentToCategoryQuick); 
		
}

	
private String removeIndicators(String curCategoryString) {

	// We have to do some extra work in here, a cpl of times, but this should be pretty quick.. 
	boolean isExtraCredit = curCategoryString.contains(AppConstants.EXTRA_CREDIT_INDICATOR);

	if (isExtraCredit)
		curCategoryString = curCategoryString.replace(AppConstants.EXTRA_CREDIT_INDICATOR, "");

	boolean isUnincluded = curCategoryString.contains(AppConstants.UNINCLUDED_INDICATOR);

	if (isUnincluded)
		curCategoryString = curCategoryString.replace(AppConstants.UNINCLUDED_INDICATOR, "");
	return curCategoryString;
}

private GradeItem buildOrGetExistingCategoryForUpdate(int col, String curCategoryString, Map<String, GradeItem> categoryMap, GradeItem gradebookItemModel) {
	
	GradeItem categoryModel = null;
	
	boolean isNewCategory = !categoryMap.containsKey(removeIndicators(curCategoryString));	
	boolean isExtraCredit = curCategoryString.contains(AppConstants.EXTRA_CREDIT_INDICATOR);
	boolean isUnincluded = curCategoryString.contains(AppConstants.UNINCLUDED_INDICATOR);
	boolean isDefaultCategory = curCategoryString.equalsIgnoreCase(AppConstants.DEFAULT_CATEGORY_NAME);
	
	if (isDefaultCategory) {
		// Check if the default category is already in this Gradebook
		categoryModel = getDefaultCategoryFromGradebookItemModel(gradebookItemModel);	
	}
	
	if (categoryModel == null)
	{
		if (isNewCategory)
		{
			categoryModel = buildNewCategory(curCategoryString, isDefaultCategory, isUnincluded, isExtraCredit, col);
			gradebookItemModel.addChild((GradeItem)categoryModel);
		}
		else
		{
			categoryModel = categoryMap.get(removeIndicators(curCategoryString));
			// GRBK-627 : Updating the GradeItem/Category
			categoryModel.setIncluded(!isUnincluded);
		}
	}
	return categoryModel;
}

private GradeItem buildNewCategory(String curCategoryString,
			boolean isDefaultCategory, boolean isUnincluded,
			boolean isExtraCredit, int col) {
		GradeItem categoryModel; 
		String identifier = isDefaultCategory ? String.valueOf(Long.valueOf(-1l)) : AppConstants.NEW_CAT_PREFIX + col;
		
		categoryModel = new GradeItemImpl();
		categoryModel.setIdentifier(identifier);
		categoryModel.setItemType(ItemType.CATEGORY);
		categoryModel.setName(removeIndicators(curCategoryString));
		if (!isDefaultCategory) {
			// We only worry about these for new categories, the default category is by definition unincluded and not extra credit
			categoryModel.setIncluded(Boolean.valueOf(!isUnincluded));
			categoryModel.setExtraCredit(Boolean.valueOf(isExtraCredit));
		}
		
		return categoryModel;

	}

	private GradeItem getDefaultCategoryFromGradebookItemModel(GradeItem gradebookItemModel)
	{
		List<GradeItem> children = gradebookItemModel.getChildren();
		if (children != null && children.size() > 0) {
			for (GradeItem child : children) {
				if (child.getName().equals(AppConstants.DEFAULT_CATEGORY_NAME)) {
					return child; 
				}
			}
		}
		return null; 
	}
	
	private void addExistingCategoriesFromGradebookItemModelToMap(Map<String, GradeItem> categoryMap,
			GradeItem gradebookItemModel) {
		for (GradeItem child : gradebookItemModel.getChildren()) {
			if (child.getItemType() != null && child.getItemType() == ItemType.CATEGORY)
			{
				categoryMap.put(child.getName(), child);
			}
		}
	}

	private void processStructureInformationForDisplayAndScaledOptions(
			GradeItem gradebookItemModel, ImportExportInformation ieInfo,
			Map<StructureRow, String[]> structureColumnsMap) {
		
		OptionState scaledEC = checkRowOption(StructureRow.SCALED_EC, structureColumnsMap); 
		OptionState showCourseGrades = checkRowOption(StructureRow.SHOWCOURSEGRADES, structureColumnsMap);
		OptionState showItemStats = checkRowOption(StructureRow.SHOWITEMSTATS, structureColumnsMap); 
		OptionState showMean = checkRowOption(StructureRow.SHOWMEAN, structureColumnsMap);
		OptionState showMedian = checkRowOption(StructureRow.SHOWMEDIAN, structureColumnsMap); 
		OptionState showMode = checkRowOption(StructureRow.SHOWMODE, structureColumnsMap);
		OptionState showRank = checkRowOption(StructureRow.SHOWRANK, structureColumnsMap); 
		OptionState showReleasedItems = checkRowOption(StructureRow.SHOWRELEASEDITEMS, structureColumnsMap);
		OptionState showStatisticsChart = checkRowOption(StructureRow.SHOWSTATISTICSCHART, structureColumnsMap);
		
		if (scaledEC != OptionState.NULL)
		{
			gradebookItemModel.setExtraCreditScaled(scaledEC == OptionState.TRUE ? Boolean.TRUE : Boolean.FALSE);
		}

		if (showCourseGrades != OptionState.NULL)
		{
			gradebookItemModel.setReleaseGrades(showCourseGrades == OptionState.TRUE ? Boolean.TRUE : Boolean.FALSE);
		}

		if (showItemStats != OptionState.NULL)
		{
			gradebookItemModel.setShowItemStatistics(showItemStats == OptionState.TRUE ? Boolean.TRUE : Boolean.FALSE);
		}
		
		if (showMean != OptionState.NULL)
		{
			gradebookItemModel.setShowMean(showMean == OptionState.TRUE ? Boolean.TRUE : Boolean.FALSE);
		}
		if (showMedian != OptionState.NULL)
		{
			gradebookItemModel.setShowMedian(showMedian == OptionState.TRUE ? Boolean.TRUE : Boolean.FALSE);
		}

		if (showMode != OptionState.NULL)
		{
			gradebookItemModel.setShowMode(showMode == OptionState.TRUE ? Boolean.TRUE : Boolean.FALSE);
		}
		
		if (showRank != OptionState.NULL)
		{
			gradebookItemModel.setShowRank(showRank == OptionState.TRUE ? Boolean.TRUE : Boolean.FALSE);
		}

		if (showReleasedItems != OptionState.NULL)
		{
			gradebookItemModel.setReleaseItems(showReleasedItems == OptionState.TRUE ? Boolean.TRUE : Boolean.FALSE);
		}
		
		if (showStatisticsChart != OptionState.NULL)
		{
			gradebookItemModel.setShowStatisticsChart(showStatisticsChart == OptionState.TRUE ? Boolean.TRUE : Boolean.FALSE);
		}

	}
	private OptionState checkRowOption(StructureRow theRow, Map<StructureRow, String[]> structureColumnsMap)
	{
		String[] rowData = structureColumnsMap.get(theRow); 
	
		log.debug("rowData: " + Arrays.toString(rowData)); 
		if (rowData == null)
		{
			return OptionState.NULL; 
		}
		else if (rowData[2].compareToIgnoreCase("true") == 0) 
		{
			return OptionState.TRUE; 
		}
		else
		{
			return OptionState.FALSE; 
		}
		
	}

	private void processHeaders(ImportExportInformation ieInfo, Map<StructureRow, String[]> structureColumnsMap) throws ImportFormatException {
		ImportHeader[] headers = ieInfo.getHeaders();
		
		if (headers == null)
			return;
		
		
		// Although these contain "structure" information, it's most efficient to check them while we're looping through 
		// the header columns
		// During the 6/1 refactor I left this alone, probably could have moved this back as 
		String[] pointsColumns = structureColumnsMap.get(StructureRow.POINTS);
		String[] percentCategoryColumns = structureColumnsMap.get(StructureRow.PERCENT_CATEGORY);
		List<CategoryItemPair> matchedItems = new ArrayList<CategoryItemPair>();
			
		for (int i=0;i<headers.length;i++) {
		
			// Ignore null headers
			if (headers[i] == null)
				continue;
	
			if (headers[i].getField() == Field.S_ITEM || headers[i].getField() == Field.S_COMMENT) {
				CategoryItemPair match = handleItemOrComment(headers[i], pointsColumns, percentCategoryColumns, ieInfo, i);
				if (match != null && headers[i].getField() == Field.S_ITEM) {
					if (matchedItems.contains(match)) {
						StringBuffer msg = new StringBuffer(i18n.getString("importDuplicateItemsFound"));
						if (match.getCategory() != null ) {
							if ("-1".equals(match.getCategory().getIdentifier())) {
								continue;
							} 
							msg.append(i18n.getString("categoryName")).append(" '").append(match.getCategory().getName()).append("' - ");
							
						} 
						msg.append(i18n.getString("itemName")).append(" '").append(match.getItem().getName()).append("'");
						
						throw new ImportFormatException(msg.toString());
					} 
					matchedItems.add(match);
				}
			}
		
		}
	}
	private String getEntryFromRow(String[] row, int col)
	{
		if (row != null && row.length > col && Util.isNotNullOrEmpty(row[col])) {
			return row[col];
		}
		// else
		
		return "";
		
	}
	
	private CategoryItemPair handleItemOrComment(ImportHeader header, String[] pointsColumns, 
			String[] percentCategoryColumns, ImportExportInformation ieInfo, int headerNumber) throws ImportFormatException {

		Item gradebookItemModel = ieInfo.getGradebookItemModel();
		CategoryType categoryType = gradebookItemModel.getCategoryType();
		String itemName = header.getHeaderName();
		
		FileFormat format = FileFormat.valueOf(ieInfo.getImportSettings().getFileFormatName());
		
		boolean treatAllItemsAsNewItems = 
			       format.equals(FileFormat.CLICKER)
				|| format.equals(FileFormat.SCANTRON)
				|| format.equals(FileFormat.TEMPLATE);
		
		if (header.getField() == Field.S_ITEM) {
			// If we have the points and percent Category from the structure information, this will put it where it needs to be. 
			handlePointsAndPercentCategoryForHeader(pointsColumns, percentCategoryColumns, headerNumber, header); 
		}
		CategoryItemPair p = getCategoryAndItemInformation(categoryType, itemName, gradebookItemModel, header, headerNumber, ieInfo); 
		GradeItem itemModel = p.getItem();
		GradeItem categoryModel = p.getCategory();
		boolean isNewItem = false;
		if (itemModel == null || treatAllItemsAsNewItems) {
			isNewItem = true;
			if (treatAllItemsAsNewItems && !ieInfo.getImportSettings().isNameUniquenessCheckDone()) {
				try {
					header.setHeaderName(getUniqueItemName(header.getHeaderName(), ieInfo.getImportSettings().getGradebookUid()));
				} catch (GradebookImportException e) {
					throw new ImportFormatException(e.getMessage());
				}
			}
			itemModel =  createNewGradeItem(header, headerNumber);
		} else {
			header.setId(itemModel.getIdentifier());
		}

		if (header.getField() == Field.S_ITEM) {
			decorateItemFromHeader(header, itemModel, categoryModel); 
		}

		putItemInGradebookModelHierarchy(categoryType, itemModel, gradebookItemModel, categoryModel, isNewItem); 
		p.setItem(itemModel);
		return p;
	}
	
	private void decorateItemFromHeader(ImportHeader header,
			GradeItem itemModel, GradeItem categoryModel) throws ImportFormatException {
		/*
		 * This stuff is because we can include indicators on the normal header row which have points and percent grade. 
		 */
		// Modify the percentage category contribution
		decorateItemForStructureInfo(header, itemModel); 
		itemModel.setIncluded(Boolean.valueOf(!header.isUnincluded()));
		itemModel.setExtraCredit(Boolean.valueOf(header.isExtraCredit()));
		itemModel.setReleased(Boolean.valueOf(header.isReleaseScores()));
		itemModel.setNullsAsZeros(Boolean.valueOf(header.isGiveungradedNoCredit()));
		itemModel.setChecked(true);
		header.setItem(itemModel);
	}

	private void putItemInGradebookModelHierarchy(CategoryType categoryType,
			GradeItem itemModel, Item gradebookItemModel,
			GradeItem categoryModel, boolean isNewItem) {

		if (categoryType == CategoryType.NO_CATEGORIES) {
			((GradeItem)gradebookItemModel).addChild(itemModel);
		} else if (categoryModel != null) {
			if (categoryModel.getName() != null && categoryModel.getName().equals(AppConstants.DEFAULT_CATEGORY_NAME))
				itemModel.setIncluded(Boolean.FALSE);

			categoryModel.addChild(itemModel);
		} else if (isNewItem) {
			itemModel.setIncluded(Boolean.FALSE);
		}
	}
	private void decorateItemForStructureInfo(ImportHeader header,
			GradeItem itemModel) throws ImportFormatException {
		// First handle points
		if (header.getPoints() != null) {
			String pointsField = header.getPoints();

			if (!pointsField.contains("A-F") &&
					!pointsField.contains("%")) {

				try {
					Double points = Util.convertStringToDouble(pointsField);
					itemModel.setPoints(points);
				} catch (NumberFormatException nfe) {
					log.info("User error. Failed on import: points field for column " + header.getValue() + " or " + pointsField + " cannot be formatted as a double");
					throw new ImportFormatException("Failed to import this file. For the column " + header.getValue() + ", the points field " + pointsField + " cannot be read as a number.");
				}
			}
		}
		// Now handle percent category
		if (header.getPercentCategory() != null) {
			String percentCategoryField = header.getPercentCategory();

			try {
				Double percentCategory = Util.fromPercentString(percentCategoryField);
				itemModel.setPercentCategory(percentCategory);
				itemModel.setWeighting(percentCategory);
			} catch (NumberFormatException nfe) {
				log.info("User error. Failed on import: percent category field for column " + header.getValue() + " or " + percentCategoryField + " cannot be formatted as a double");
				throw new ImportFormatException("Failed to import this file. For the column " + header.getValue() + ", the percent category field " + percentCategoryField + " cannot be read as a number.");
			}
		}		
	}

	private GradeItem createNewGradeItem(ImportHeader header, int headerNumber) {
		GradeItem itemModel = null; 
		
		itemModel = new GradeItemImpl();

		String identifier = new StringBuilder().append(AppConstants.NEW_PREFIX).append(headerNumber).toString();
		header.setId(identifier);
		itemModel.setItemType(ItemType.ITEM);
		itemModel.setStudentModelKey(LearnerKey.S_ITEM.name());
		itemModel.setIdentifier(identifier);
		itemModel.setName(header.getHeaderName());
		itemModel.setItemId(Long.valueOf(-1l));
		itemModel.setCategoryId(Long.valueOf(-1l));
		itemModel.setCategoryName(header.getCategoryName());
		itemModel.setPoints(Double.valueOf(100d));
		return itemModel; 		
	}

	private CategoryItemPair getCategoryAndItemInformation(
			CategoryType categoryType, String itemName,
			Item gradebookItemModel, ImportHeader header, int headerNumber,
			ImportExportInformation ieInfo) {

		String[] assignmentPositionToCategoryIdQuick = ieInfo.getAssignmentPositionToCategoryIdQuick();
		CategoryItemPair p = null; 
		
		/*
		 * For templates, scantrons... we assume all items are new items
		 */
		FileFormat f = FileFormat.valueOf(ieInfo.getImportSettings().getFileFormatName());
		
		if (f.equals(FileFormat.CLICKER) || f.equals(FileFormat.SCANTRON) || f.equals(FileFormat.TEMPLATE)) {
			return new CategoryItemPair( ieInfo.getCategoryIdItemMap().get("-1"), null);
		}

		switch (categoryType) {
		case NO_CATEGORIES:
			p = new CategoryItemPair(null, findModelByName(itemName, gradebookItemModel));
			break;
		case SIMPLE_CATEGORIES:
		case WEIGHTED_CATEGORIES:
			String categoryId = null; 
			if (assignmentPositionToCategoryIdQuick != null)
			{
				categoryId = assignmentPositionToCategoryIdQuick[headerNumber];
			}
			p = getCategoryAndItemModel(categoryId, itemName, gradebookItemModel, header, ieInfo.getCategoryIdItemMap());
			
			break;
		}
		
		return p; 
	}

	private void handlePointsAndPercentCategoryForHeader(
			String[] pointsColumns, String[] percentCategoryColumns,
			int headerNumber, ImportHeader header) {
		
		if (!"".equals(getEntryFromRow(pointsColumns, headerNumber))) {
			header.setPoints(getEntryFromRow(pointsColumns, headerNumber));
		}

		if (!"".equals(getEntryFromRow(percentCategoryColumns, headerNumber)))
		{
			header.setPercentCategory( getEntryFromRow(percentCategoryColumns, headerNumber) );
		}		
	}

	private CategoryItemPair getCategoryAndItemModel(String categoryId,
			String itemName, Item gradebookItemModel, ImportHeader header,
			Map<String, GradeItem> categoryIdItemMap) {
		GradeItem itemModel = null; 
		GradeItem categoryModel = null; 
		
		if (categoryId == null) {
			itemModel = findModelByName(itemName, gradebookItemModel);
			// If this is a new item, and we don't have structure info, then
			// we have to make it "Unassigned"
			if (itemModel == null)
				categoryModel = categoryIdItemMap.get("-1");
		} else {
			categoryModel = categoryIdItemMap.get(categoryId);
			if (categoryModel != null) {
				decorateHeaderWithCategoryModel(header, categoryModel); 
				if (findItemInCategory(categoryModel, itemName) != null)
				{
					itemModel = findItemInCategory(categoryModel, itemName); 
				}
			} 
			else
			{
				log.warn("CategoryModel is null via lookup in map");
			}
		} 
		return new CategoryItemPair(categoryModel, itemModel); 
	}

	private GradeItem findItemInCategory(GradeItem categoryModel,
			String itemName) {
		GradeItem itemModel = null; 
		List<GradeItem> children = categoryModel.getChildren();
		if (children != null && children.size() > 0) {
			for (GradeItem item : children) {
				if (item.getName().equals(itemName)) {
					itemModel = item;
					break;
				}
			}
		}

		return itemModel;
	}

	private void decorateHeaderWithCategoryModel(ImportHeader header,
			GradeItem categoryModel) {
		if (categoryModel != null) {
			header.setCategoryId(categoryModel.getIdentifier());
			header.setCategoryName(categoryModel.getCategoryName());
		}		
	}

	public Upload parseImportGeneric(ImportExportDataFile rawData) {
		
		String msgs = rawData.getMessages();
		boolean errorsFound = rawData.isErrorsFound(); 

		if (errorsFound) {
			return emptyUploadFileWithNotes(msgs); 
		}

		Gradebook gradebook = service.getGradebook(rawData.getImportSettings().getGradebookUid());
		Item gradebookItemModel = gradebook.getGradebookItemModel();

		List<UserDereference> userDereferences = this.service.findAllUserDereferences();
		Map<String, UserDereference> userDereferenceMap = new HashMap<String, UserDereference>();
		buildDereferenceIdMap(userDereferences, userDereferenceMap);
		ImportExportInformation ieInfo = new ImportExportInformation();
		
		UploadImpl importFile = new UploadImpl();
		
		// this is just housekeeping and may not be immediately necessary
		importFile.getImportSettings().setScantron(rawData.isScantronFile());
		importFile.getImportSettings().setJustStructure(rawData.isJustStructure());
		ieInfo.setImportSettings(rawData.getImportSettings());
		//
		
		
		ieInfo.setGradebookItemModel(gradebookItemModel);
		
		ArrayList<Learner> importRows = new ArrayList<Learner>();

		Map<StructureRow, String[]> structureColumnsMap = new HashMap<StructureRow, String[]>();

		

		int structureStop = 0; 

		structureStop = readDataForStructureInformation(rawData, buildRowIndicatorMap(), structureColumnsMap);
		if (structureStop != -1)
		{
			try {
				readInHeaderRow(rawData, ieInfo, structureStop);
				processStructureInformation(ieInfo, structureColumnsMap);
				
				/*
				 * if a header name changes when it returns from processHeaders, there was a unique name
				 * generated, and in that case, send notificaton
				 */
				String[] headerNames = new String[ieInfo.getHeaders().length];
				for (int i=0;i<headerNames.length;++i) {
					headerNames[i] = ieInfo.getHeaders()[i].getHeaderName();
				}
				String[] copy = Arrays.copyOf(headerNames, headerNames.length);
				processHeaders(ieInfo, structureColumnsMap);
				for (int i=0;i<headerNames.length;++i) {
					headerNames[i] = ieInfo.getHeaders()[i].getHeaderName();
				}
				
				if ( ! Arrays.deepEquals(copy, headerNames)) {
					importFile.setNotifyAssignmentName(true);
					importFile.setNotes(i18n.getString("gb2ImportItemSameName"));
				}
				
				
				// At this point, we need to remove assignments that are not in the import
				// file
				adjustGradebookItemModel(ieInfo);
				
				if(!ieInfo.getImportSettings().isJustStructure()) {// GRBK-514
					readInGradeDataFromImportFile(rawData, ieInfo, userDereferenceMap, importRows, structureStop, service);
				}
				
				GradeItem gradebookGradeItem = (GradeItem)ieInfo.getGradebookItemModel();
				service.decorateGradebook(gradebookGradeItem, null, null);
				importFile.setGradebookItemModel(gradebookGradeItem);
				importFile.setRows(importRows);
				importFile.setGradeType(gradebookItemModel.getGradeType());
				importFile.setCategoryType(gradebookItemModel.getCategoryType());
					
				if (ieInfo.isUserNotFound()) 
					importFile.addNotes(i18n.getString("importUserNotFoundMessage"));

				if (ieInfo.isInvalidScore()) 
					importFile.addNotes(i18n.getString("importInvalidScoresMessage"));
			} catch (Exception e) {
				importFile.setErrors(true);
				importFile.setNotes(e.getMessage());
				importFile.setRows(null);
				log.warn(e, e);
			}
			
			// GRBK-806 code was here to disable percentage gradebooks in general but if we're a scantron we will not allow it.
			

		}
		else
		{
			importFile.setErrors(true); 
			importFile.setNotes(i18n.getString("importMissingHeaderMessage")); 
		}
		
		service.postEvent("gradebook2.import", String.valueOf(gradebook.getGradebookId()));

		return importFile;
	}

	/*
	 * This method removes assignments that are not present in the import file
	 * but are already in the gradebook
	 * 
	 * TODO: fix the name of this method
	 * 
	 */
	private void adjustGradebookItemModel(ImportExportInformation ieInfo) {
		
		GradeItem gradeItem = (GradeItem) ieInfo.getGradebookItemModel();
		ImportHeader[] newImportHeaders = ieInfo.getHeaders();
		
		
		if (gradeItem.getCategoryType() == CategoryType.NO_CATEGORIES)
		{
			for(Iterator<GradeItem> iter = gradeItem.getChildren().iterator(); iter.hasNext(); ) {

				GradeItem assignment = iter.next();

				if(!hasAssignment(newImportHeaders, assignment.getName())) {
					iter.remove();
				}
			}

		}
		else
		{
			for(GradeItem category : gradeItem.getChildren()) {

				for(Iterator<GradeItem> iter = category.getChildren().iterator(); iter.hasNext(); ) {

					GradeItem assignment = iter.next();

					if(!hasAssignment(newImportHeaders, assignment.getName())) {
						iter.remove();
					}
				}
			}
		}
	}
	
	
	private boolean hasAssignment(ImportHeader[] importHeaders, String assignmentName) {
		
		for(ImportHeader importHeader : importHeaders) {
			
			Item item = importHeader.getItem();
			
			if(null != item && null != item.getName() && item.getName().equals(assignmentName)) {
				return true;
			}
		}
		
		return false;

	}

	private void buildDereferenceIdMap(List<UserDereference> userDereferences,
			Map<String, UserDereference> userDereferenceMap) {

		for (UserDereference dereference : userDereferences) {
			String exportUserId = service.getExportUserId(dereference); 
			userDereferenceMap.put(exportUserId, dereference);
		}
	}

	private Map<String, StructureRow> buildRowIndicatorMap() {
		
		Map<String, StructureRow> structureRowIndicatorMap = new HashMap<String, StructureRow>();

		for (StructureRow structureRow : EnumSet.allOf(StructureRow.class)) {
			String lowercase = structureRow.getDisplayName().toLowerCase();
			structureRowIndicatorMap.put(lowercase, structureRow);
		}
		return structureRowIndicatorMap;
	}
	
	private String getDisplayName(CategoryType categoryType) {
		switch (categoryType) {
		case NO_CATEGORIES:
			return i18n.getString("orgTypeNoCategories");
		case SIMPLE_CATEGORIES:
			return i18n.getString("orgTypeCategories");
		case WEIGHTED_CATEGORIES:
			return i18n.getString("orgTypeWeightedCategories");
		}
		return "N/A";
	}

	private String getDisplayName(GradeType gradeType) {
		switch (gradeType) {
		case POINTS:
			return i18n.getString("gradeTypePoints");
		case PERCENTAGES:
			return i18n.getString("gradeTypePercentages");
		case LETTERS:
			return i18n.getString("gradeTypeLetters");
		}
		
		return "N/A";
	}

	private GradeItem findModelByName(final String name, Item root) {

		ItemModelProcessor processor = new ItemModelProcessor(root) {

			@Override
			public void doItem(Item itemModel) {

				String itemName = itemModel.getName();

				if (itemName != null) {
					String trimmed = itemName.trim();

					if (trimmed.equals(name)) {
						this.result = itemModel;
					}
				}
			}
			
		};

		processor.process();

		return (GradeItem)processor.getResult();
	}
	
	public void setGradeCalculations(GradeCalculations gradeCalculations) {
		this.gradeCalculations = gradeCalculations;
	}


	public void setI18n(ResourceLoader i18n) {
		this.i18n = i18n;
	}
	
	public Upload getImportFile(MultipartFile file, ImportSettings importSettings) {
		Upload rv = null;
		boolean typeOK = true;
		final FileType type = FileType.valueOf(importSettings.getExportTypeName());
		String errorMessage = "";
		final String unexpectedTypeErrorMessage = i18n.getString("filetypeExtensionMismatch", 
				"top.")
				+ lookupI18nExportType(importSettings.getExportTypeName());
		final String unexpectedFormatErrorMessage = i18n.getString("expectedFormat", 
				"The file format did not match your selection: ")
				+ lookupI18nFileFormat(importSettings.getFileFormatName());
		
		String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));
		
		String itemNameFromFile = removeFileExenstion(file.getOriginalFilename());
		String newItemName = null;
		if(importSettings.getFileFormatName().equals(FileFormat.SCANTRON.name())) {
			try {
				newItemName = getUniqueItemName(itemNameFromFile, importSettings.getGradebookUid());
				importSettings.setNameUniquenessCheckDone(true);
			} catch (GradebookImportException e) {
				return emptyUploadFileWithNotes(e.getMessage());
			}
		}
		
		InputStream origFile = null;
		
		/* get the file and save it locally so we can abuse it */
		File tempFile = null;
		String prefix = Thread.currentThread().hashCode() + "_";
		try {
			origFile = file.getInputStream();
			byte[] buf = new byte[1024];
			tempFile = File.createTempFile(prefix, suffix);
			FileOutputStream fos = new FileOutputStream(tempFile, true);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			int cnt;  
			while ((cnt = origFile.read(buf)) != -1)  
			   bos.write(buf, 0, cnt);
			bos.close();
			tempFile.deleteOnExit();
		} catch (Exception e1) {
			return emptyUploadFileWithNotes(e1.getMessage());
		}
		
		// first level of problems would be from declared/detected type mismatch
		errorMessage = unexpectedTypeErrorMessage;
		
		org.apache.poi.ss.usermodel.Workbook wbPoi = null;
		jxl.Workbook wbJxl = null;
		try {
			if (FileType.XLSX.equals(type)) {


				String detected = null;
				try {
					detected = getMimeType(new FileInputStream(tempFile));
				} catch (FileNotFoundException e1) {
					log.error("tempfile read failed: " + tempFile.getPath());
					e1.printStackTrace();
					typeOK = false;
					//I18N 
					errorMessage = "File IO";
				}

				
				typeOK = DETECTOR_OOXML_CONTAINER_MIMETYPE.equals(detected)              //shallow detection
						|| DETECTOR_OOXML_SPREADSHEET_MIME_TYPE.equals(detected);       //deep detection
						
				wbPoi = readPoiSpreadsheet(tempFile);
				typeOK = typeOK && wbPoi != null;
				
				if(typeOK) {
					//proceed
					rv = handlePoiSpreadSheet(wbPoi, newItemName, 
							itemNameFromFile.equalsIgnoreCase(newItemName), importSettings);
				} else {
					errorMessage = unexpectedTypeErrorMessage;
				}
			}
			if(FileType.XLS97.equals(type)) {
				/*
				 * since POI is needed read newer xls files we need to make sure
				 * that the detected mime type is compatible with XLS97
				 * (and not XLSX, for example)typeOk = getMimeType(bis)
				 */
				String detected = getMimeType(new FileInputStream(tempFile));
				typeOK = DETECTOR_MS_OFFICE_GENERIC_MIMETYPE.equals(detected)
						|| DETECTOR_MS_EXCEL_MIMETYPE.equals(detected);
				wbPoi = readPoiSpreadsheet(tempFile);
				typeOK = wbPoi != null;

				if(typeOK) {
					//proceed
					rv = handlePoiSpreadSheet(wbPoi, newItemName, 
							itemNameFromFile.equalsIgnoreCase(newItemName), importSettings);
				} else {
					wbJxl = getJexcelWorkbook(new BufferedInputStream(new FileInputStream(tempFile)));
					typeOK = wbJxl != null;
					if (typeOK) {
						try {
							rv = handleJExcelAPISpreadSheet(file.getInputStream(), newItemName, 
									itemNameFromFile.equalsIgnoreCase(newItemName), importSettings);
						} catch (InvalidInputException e) {
							typeOK = false;
						} catch (FatalException e) {
							log.error("cannot read Jexcel File: " + file.getOriginalFilename());
							errorMessage = unexpectedTypeErrorMessage;
						}
					} else {
						errorMessage = unexpectedTypeErrorMessage;
					}
					
						
					
				}
				
			}
			if(FileType.CSV.equals(type) || FileType.TEMPLATE.equals(type) ) {
				
				ImportExportDataFile rawData = new ImportExportDataFile(); 
				String[] ent;
				
				String detected = getMimeType(new FileInputStream(tempFile));
				
				if (!DETECTOR_CSV_MIMETYPE.equals(detected)) {
					typeOK = false;
					errorMessage = unexpectedTypeErrorMessage;
				} else {
					/// if there is an error after this is it a format error
					errorMessage = unexpectedFormatErrorMessage;
				
			
					InputStreamReader reader = new InputStreamReader(new FileInputStream(tempFile));
					CSVReader csvReader = new CSVReader(reader);
					
					while ( (ent = csvReader.readNext() ) != null)
					{
							rawData.addRow(ent); 
					}
					csvReader.close();
					
					/*
					 * If the above did not throw up 
					 * it does ##not necessarily mean## that
					 * the file is a well formed CSV file
					 */
					
					/* 
					 * for now just check for headers and then, 
					 * if they are ok, then pass
					 * the lot to the generic handler... 
					 */
					 
					if(FileFormat.SCANTRON.equals(FileFormat.valueOf(importSettings.getFileFormatName()))) {
						rawData.setScantronFile(true);					
						if (!isScantronSheetCSV(rawData)) {
							typeOK = false;
						} else {
							/* now that the file format is validated
							 * swap out the header for one based on the filename
							 */
							rawData.setAllRows(convertScantronRowsToTemplate(rawData.getAllRows(), newItemName));
							rawData.setNewAssignment(itemNameFromFile.equalsIgnoreCase(newItemName));
						}
						
					} else if (FileFormat.TEMPLATE.equals(FileFormat.valueOf(importSettings.getFileFormatName())) || FileFormat.CLICKER.equals(FileFormat.valueOf(importSettings.getFileFormatName()))) {
						if(!couldBeNewItemTemplate(rawData)) {
							typeOK = false;
						} else {
							rawData.setNewAssignment(true);
						}
					} else if (FileFormat.FULL.equals(FileFormat.valueOf(importSettings.getFileFormatName()))) {
						if (0 >= readDataForStructureInformation(rawData, buildRowIndicatorMap(), new HashMap<StructureRow, String[]>())){
							typeOK = false;
						}
					} else if (FileFormat.NO_STRUCTURE.equals(FileFormat.valueOf(importSettings.getFileFormatName()))) {
						if (0 < readDataForStructureInformation(rawData, buildRowIndicatorMap(), new HashMap<StructureRow, String[]>())){
							typeOK = false;
						}
					}
				}
				if (typeOK) {
					rawData.setImportSettings(importSettings);
					rv = parseImportGeneric(rawData);

				}
			}
		} catch (IOException e) { // file isn't CSV?
			errorMessage = i18n.getString("errorReadingFile", 
			                                  "The file could not be read.");
			log.error(e);
			
			typeOK = false;
		}

		if (!typeOK) {
			rv = emptyUploadFileWithNotes(errorMessage);
		} else {
			/// TODO: this scantron flag is being too overloaded methinks
			FileFormat format = null;
			try {
				format = FileFormat.valueOf(importSettings.getFileFormatName());
				rv.getImportSettings().setScantron(
						format.equals(FileFormat.CLICKER) 
						|| format.equals(FileFormat.SCANTRON)
						|| format.equals(FileFormat.TEMPLATE));
			} catch (Exception e) {
				rv = emptyUploadFileWithNotes(unexpectedFormatErrorMessage);
			}
		}
		
		if (tempFile != null) 
			tempFile.delete();
		
		return rv; 
	}
	
	
	
	private Upload emptyUploadFileWithNotes(String message) {
		Upload importFile = new UploadImpl(); 
		importFile.setErrors(true); 
		importFile.setNotes(message); 
		return importFile; 
	}


	private boolean isScantronSheetCSV(ImportExportDataFile rawData) {
		
		// if there is structure found it is not a scantron
		if (0 < readDataForStructureInformation(rawData, buildRowIndicatorMap(), new HashMap<StructureRow, String[]>()))
			return false;
		
		boolean isScantron = false;
		
		if(rawData != null && rawData.getAllRows().size()>0)
			for (String[] row : rawData.getAllRows()) {
				List<String> rowLowerCase = new ArrayList<String>();
				for (String cell : Arrays.asList(row)) {
					rowLowerCase.add(cell.trim().toLowerCase());
				}
				isScantron = isListAScantronHeaderRow(rowLowerCase);
				// accept first qualified match
				if (isScantron)
					break;
			}
		return isScantron;
	}


	private boolean isListAScantronHeaderRow(List<String> rowLowerCase) {
		
		if(null == rowLowerCase) {
			
			return false;
		}
		
		boolean answer = rowLowerCase.size()>0;
		
		for (String v : rowLowerCase) {
			answer = answer && 
				(scantronIgnoreSet.contains(v) 
						|| scantronScoreHeader.equalsIgnoreCase(v) 
						|| scantronRescoreHeader.equalsIgnoreCase(v)
						|| scantronStudentIdHeader.equalsIgnoreCase(v));
			if (!answer) 
				break; // fail on first wrong answer
		}
		
		return answer;
	}


	public void setFiletypeDetector(Tika filetypeDetector) {
		this.filetypeDetector = filetypeDetector;
	}


	private boolean couldBeNewItemTemplate(ImportExportDataFile rawData) {
		
		if (null == rawData || 0 == rawData.getAllRows().size())
			return false;
		
		/// if there is structure found, it is a full gradebook format
		if (0 < readDataForStructureInformation(rawData, buildRowIndicatorMap(), new HashMap<StructureRow, String[]>()))
			return false;
		
		for (String[] row : rawData.getAllRows()) {
			List<String> rowLowerCase = new ArrayList<String>();
			for (String cell : Arrays.asList(row)) {
				rowLowerCase.add(cell.trim().toLowerCase());
			}
			// accept first qualified match
			if (couldBeNewItemTemplateHeaderRow(rowLowerCase)) {
				
				return !hasTooManyItems(row, FileFormat.TEMPLATE);
			}
				
		}
		return false;
	}

/*
 * this method is to detect more than one item which is not yet supported
 */
	private boolean hasTooManyItems(String[] row, FileFormat format) {
		boolean rv = false;
		boolean oneItemFound = false;
	
		
		switch (format) {
		case TEMPLATE:
			for (String s:row) {
				/* contains is overriden to be case-insensitive)*/
				if (null==s || templateHeaderColumnSet.contains(s) || "".equals(s.trim()) ) 
					continue;
				
				
				if (!oneItemFound)
					oneItemFound = true;
				else
					 return true;
			}
			
		default:
			return rv;
		}
		
		
	}
	
	private boolean hasTooManyItems(Row row, FileFormat format) {
		String[] cells = new String[row.getPhysicalNumberOfCells()];
		int i=0;
		for (org.apache.poi.ss.usermodel.Cell cell : row) {
			cells[i++]=cell.getStringCellValue().trim();
		}
		return hasTooManyItems(cells, format);
	}


	/*
	 * this will return false if Workbook != null but there was 
	 * an error opening the Workbook
	 */
	private Workbook getJexcelWorkbook(InputStream inputStream) {
		boolean ok = true;
		Workbook wb = null;
		
		
		try {
			 wb = Workbook.getWorkbook(inputStream);
		} catch (BiffException e) {
			ok = false;
			log.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			ok = false; 
		} 
		if(null == wb || wb.getSheet(0) == null ) {
			ok=false;
		} 
		
		return ok?wb:null;
	}


	public Gradebook2ComponentService getGradebook2ComponentService() {
		return service;
	}


	public void setGradebook2ComponentService(Gradebook2ComponentService service) {
		this.service = service;
	}
	
	
	
	/*
	 * 
	 * This import path does not check filetypes and/or file extensions
	 * It just takes a stream reader and assumes it is CSV
	 * 
	 * (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.server.ImportExportUtility#parseImportCSV(java.lang.String, java.io.Reader)
	 */
	public Upload parseImportCSV(String gradebookUid, Reader reader)
			throws InvalidInputException, FatalException {
		ImportSettings importSettings = new ImportSettingsImpl();
		importSettings.setGradebookUid(gradebookUid);
		return parseImportCSV(reader, importSettings);
	}


	public GradebookToolService getToolService() {
		return toolService;
	}


	public void setToolService(GradebookToolService toolService) {
		this.toolService = toolService;
	}
	
	public void setClickerStudentIdHeader(String clickerStudentIdHeader) {
		this.clickerStudentIdHeader = clickerStudentIdHeader;
	}


	public void setClickerIgnoreColumns(String[] clickerIgnoreColumns) {
		this.clickerIgnoreColumns = clickerIgnoreColumns;
	}


	public void setService(Gradebook2ComponentService service) {
		this.service = service;
	}

	public void setTemplateIgnoreColumns(String[] templateIgnoreColumns) {
	}


	public void setTemplateStudentIdHeader(String templateStudentIdHeader) {
	}

	/*
	 * Method that looks up the ExportType ENUM name and returns
	 * the proper i18n strings
	 */
	private String lookupI18nExportType(String exportType) {
		
		if(null == exportType || "".equals(exportType)) {
			
			return "";
		}
		
		if(exportType.equals(ExportType.CSV.name())) {
			
			return i18n.getString("exportTypeCSV");
		}
		else if(exportType.equals(ExportType.XLSX.name())) {
			
			return i18n.getString("exportTypeXLSX");
		}
		else if(exportType.equals(ExportType.XLS97.name())) {
			
			return i18n.getString("exportTypeXLS");
		}
		else {
			
			log.error("ERROR: Did not recoginize ExportType = " + exportType); 
		}
		
		return "";
	}
	
	/*
	 * Method that looks up the FileFormat ENUM name and returns
	 * the proper i18n strings
	 */
	private String lookupI18nFileFormat(String fileFormat) {
		
		if(null == fileFormat || "".equals(fileFormat)) {
			
			return "";
		}
		
		if(fileFormat.equals(FileFormat.CLICKER.name())) {
			
			return i18n.getString("fileFormatNameClicker");
			
		} else if(fileFormat.equals(FileFormat.FULL.name())) {
			
			return i18n.getString("fileFormatNameFull");
		}
		else if(fileFormat.equals(FileFormat.NO_STRUCTURE.name())) {
			
			return i18n.getString("fileFormatNameNoStructure");
		}
		else if(fileFormat.equals(FileFormat.TEMPLATE.name())) {
			
			return i18n.getString("fileFormatNameTemplate");
		}
		else if(fileFormat.equals(FileFormat.SCANTRON.name())) {
			
			return i18n.getString("fileFormatNameScantron");
		}
		else {
			
			log.error("ERROR: Did not recoginize FileFormat = " + fileFormat); 
		}
		
		return "";
	}

}


class ImportExportInformation 
{
	Set<Integer> ignoreColumns;
	int courseGradeFieldIndex;
	boolean foundStructure; 
	boolean foundHeader; 
	Map<String, String> categoryIdNameMap;
	Map<String, GradeItem> categoryIdItemMap;
	
	ImportHeader[] headers;
	String[] assignmentPositionToCategoryIdQuick;

	boolean isInvalidScore;
	boolean isUserNotFound;
	
	List<Integer> activeHeaderIndexes;
	List<CategoryPosition> categoryPositions; 
	Item gradebookItemModel;
	
	ImportSettings importSettings = null;
	
	
	public ImportExportInformation() 
	{
		ignoreColumns = new HashSet<Integer>();
		courseGradeFieldIndex = -1;
		categoryIdNameMap = new HashMap<String, String>();
		categoryIdItemMap = new HashMap<String, GradeItem>();

		activeHeaderIndexes = new LinkedList<Integer>();
		importSettings = new ImportSettingsImpl();
	}

	public void trackActiveHeaderIndex(int index) {
		activeHeaderIndexes.add(Integer.valueOf(index));
	}
	
	public Set<Integer> getIgnoreColumns() {
		return ignoreColumns;
	}

	public void setIgnoreColumns(Set<Integer> ignoreColumns) {
		this.ignoreColumns = ignoreColumns;
	}

	public int getCourseGradeFieldIndex() {
		return courseGradeFieldIndex;
	}

	public void setCourseGradeFieldIndex(int courseGradeFieldIndex) {
		this.courseGradeFieldIndex = courseGradeFieldIndex;
	}

	public boolean isFoundStructure() {
		return foundStructure;
	}

	public void setFoundStructure(boolean foundStructure) {
		this.foundStructure = foundStructure;
	}

	public boolean isFoundHeader() {
		return foundHeader;
	}

	public void setFoundHeader(boolean foundHeader) {
		this.foundHeader = foundHeader;
	}

	public Map<String, String> getCategoryIdNameMap() {
		return categoryIdNameMap;
	}

	public void setCategoryIdNameMap(Map<String, String> categoryIdNameMap) {
		this.categoryIdNameMap = categoryIdNameMap;
	}

	public Item getGradebookItemModel() {
		return gradebookItemModel;
	}

	public void setGradebookItemModel(Item gradebookItemModel) {
		this.gradebookItemModel = gradebookItemModel;
	}

	public Map<String, GradeItem> getCategoryIdItemMap() {
		return categoryIdItemMap;
	}

	public void setCategoryIdItemMap(Map<String, GradeItem> categoryIdItemMap) {
		this.categoryIdItemMap = categoryIdItemMap;
	}
	
	public ImportSettings getImportSettings() {
		return importSettings;
	}

	public void setImportSettings(ImportSettings importSettings) {
		this.importSettings = importSettings;
	}

	public ImportHeader[] findActiveHeaders() {
		ImportHeader[] activeHeaders = new ImportHeader[activeHeaderIndexes.size()];
		
		int i=0;
		for (Integer index : activeHeaderIndexes) {
			activeHeaders[i] = headers[index.intValue()];
			i++;
		}
		
		return activeHeaders;
	}

	public ImportHeader[] getHeaders() {
		return headers;
	}

	public void setHeaders(ImportHeader[] headers) {
		this.headers = headers;
	}

	public boolean isInvalidScore() {
		return isInvalidScore;
	}

	public void setInvalidScore(boolean isInvalidScore) {
		this.isInvalidScore = isInvalidScore;
	}

	public boolean isUserNotFound() {
		return isUserNotFound;
	}

	public void setUserNotFound(boolean isUserNotFound) {
		this.isUserNotFound = isUserNotFound;
	}

	public List<CategoryPosition> getCategoryPositions() {
		return categoryPositions;
	}

	public void setCategoryPositions(List<CategoryPosition> categoryPositions) {
		this.categoryPositions = categoryPositions;
	}

	public String[] getAssignmentPositionToCategoryIdQuick() {
		return assignmentPositionToCategoryIdQuick;
	}

	public void setAssignmentPositionToCategoryIdQuick(String[] categoryRangeColumns) {
		this.assignmentPositionToCategoryIdQuick = categoryRangeColumns;
	}

}
/*
 * This class exists because the way the import file is structured.  Stuff like categories 
 */
class CategoryPosition implements Comparable<CategoryPosition>
{
	
	private int colNumber; 
	private GradeItem category;
	private String name; 

	public CategoryPosition(int colNumber, GradeItem category, String name) {
		super();
		this.colNumber = colNumber;
		this.category = category;
		this.name = name;
	}

	public int getColNumber() {
		return colNumber;
	}

	public void setColNumber(int colNumber) {
		this.colNumber = colNumber;
	}

	public GradeItem getCategory() {
		return category;
	}

	public void setCategory(GradeItem category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int compareTo(CategoryPosition o) {
		return getColNumber() - o.getColNumber(); 
	}
	
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		   if (obj == this) { return true; }
		   if (obj.getClass() != getClass()) {
		     return false;
		   }
		   CategoryPosition rhs = (CategoryPosition) obj;
		   return new EqualsBuilder().appendSuper(super.equals(obj))
		                 .append(colNumber, rhs.colNumber)
		                 .append(category, rhs.category)
		                 .append(name, rhs.name)
		                 .isEquals();
	}
	
}

class CategoryItemPair 
{
	private GradeItem category; 
	private GradeItem item;
	
	
	public CategoryItemPair(GradeItem category, GradeItem item) {
		this.category = category;
		this.item = item;
	}
	
	public GradeItem getCategory() {
		return category;
	}
	public void setCategory(GradeItem category) {
		this.category = category;
	}
	public GradeItem getItem() {
		return item;
	}
	public void setItem(GradeItem item) {
		this.item = item;
	} 
	
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		   if (obj == this) { return true; }
		   if (obj.getClass() != getClass()) {
		     return false;
		   }
		   
		   CategoryItemPair rhs = (CategoryItemPair) obj;
		   if (rhs.getItem() == null && item == null && category == null && rhs.getCategory() == null) {
			   return true;
		   }
		   if (rhs.getItem() == null && item == null) {
			   return new EqualsBuilder().appendSuper(super.equals(obj))
               .append(category.getName(), rhs.getCategory().getName())
               .isEquals();
		   }
		   
		   if (rhs.getCategory() == null && category == null) {
			   return new EqualsBuilder().appendSuper(super.equals(obj))
               .append(item.getName(), rhs.getItem().getName())
               .isEquals();
		   }
		   if (rhs.getCategory() == null || rhs.getItem() == null 
				   || category == null || item == null ) {
			   return false;
		   }
		   
		   return new EqualsBuilder()
		                 .append(category.getName(), rhs.getCategory().getName())
		                 .append(item.getName(), rhs.getItem().getName())
		                 .isEquals();
	}
	
	public int hashCode() {
		
		final int p = 2399;
		int rv = 1;
		int d=0;
		
		if(null != category && category.getName() == null) {
			d = category.getName().hashCode();
		}
		
		rv = p*rv + d;
		
		if(null != item && item.getName() == null) {
			d = item.getName().hashCode();
		}
		
		return p*rv + d;
		
	    
	   }
	
	
	
	
	
}
