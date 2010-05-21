/**********************************************************************************
 *
 * $Id:$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2008, 2009 The Regents of the University of California
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
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
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
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.gxt.ItemModelProcessor;
import org.sakaiproject.gradebook.gwt.client.gxt.upload.NewImportHeader;
import org.sakaiproject.gradebook.gwt.client.gxt.upload.NewImportHeader.Field;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.client.model.Roster;
import org.sakaiproject.gradebook.gwt.client.model.Upload;
import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.type.ItemType;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ComponentService;
import org.sakaiproject.gradebook.gwt.sakai.GradebookImportException;
import org.sakaiproject.gradebook.gwt.sakai.GradebookToolService;
import org.sakaiproject.gradebook.gwt.sakai.model.GradeItem;
import org.sakaiproject.gradebook.gwt.sakai.model.UserDereference;
import org.sakaiproject.gradebook.gwt.server.exceptions.ImportFormatException;
import org.sakaiproject.gradebook.gwt.server.model.GradeItemImpl;
import org.sakaiproject.gradebook.gwt.server.model.LearnerImpl;
import org.sakaiproject.gradebook.gwt.server.model.UploadImpl;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.tool.gradebook.Assignment;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class NewImportExportUtility {

	private static final Log log = LogFactory.getLog(NewImportExportUtility.class);
	private static final String SCANTRON_HEADER_STUDENT_ID = "student_id"; 
	private static final String SCANTRON_HEADER_SCORE = "score"; 
	private static ResourceBundle i18n = ResourceBundle.getBundle("org.sakaiproject.gradebook.gwt.client.I18nConstants");

	public static String[] scantronIgnoreColumns = 
		{ "last name", "first name", "initial" };
	public static String[] idColumns = 
		{ "student id", "identifier", "userId", "learnerid", "id" };
	public static String[] nameColumns =
		{ "student name", "name", "learner" };
	
	public static enum Delimiter {
		TAB, COMMA, SPACE, COLON
	};
	
	public static enum OptionState { NULL, TRUE, FALSE}; 

	private static enum StructureRow {
		GRADEBOOK("Gradebook:"),  SCALED_EC("Scaled XC:"), SHOWCOURSEGRADES("ShowCourseGrades:"), SHOWRELEASEDITEMS("ShowReleasedItems:"),
		SHOWITEMSTATS("ShowItemStats:"), SHOWMEAN("ShowMean:"), SHOWMEDIAN("ShowMedian:"), SHOWMODE("ShowMode:"), SHOWRANK("ShowRank:"),  
		CATEGORY("Category:"), PERCENT_GRADE("% Grade:"), POINTS("Points:"), 
		PERCENT_CATEGORY("% Category:"), DROP_LOWEST("Drop Lowest:"), EQUAL_WEIGHT("Equal Weight Items:");

		private String displayName;

		StructureRow(String displayName) {
			this.displayName = displayName;
		}

		public String getDisplayName() {
			return displayName;
		}
	};
	
	private Set<String> headerLineIndicatorSet, idSet, nameSet, scantronIgnoreSet;
	
	public NewImportExportUtility() {	
		// FIXME - Need to decide whether this should be institutional based.  
		// FIXME - does this need i18n ? 
		this.headerLineIndicatorSet = new HashSet<String>();
		this.nameSet = new HashSet<String>();
		for (int i=0;i<nameColumns.length;i++) {
			nameSet.add(nameColumns[i].toLowerCase());
			headerLineIndicatorSet.add(nameColumns[i].toLowerCase());
		}
		this.idSet = new HashSet<String>();
		for (int i=0;i<idColumns.length;i++) {
			idSet.add(idColumns[i].toLowerCase());
			headerLineIndicatorSet.add(idColumns[i].toLowerCase());
		}
		this.scantronIgnoreSet = new HashSet<String>();
		for (int i=0;i<scantronIgnoreColumns.length;i++) {
			scantronIgnoreSet.add(scantronIgnoreColumns[i].toLowerCase());
		}
	}

	public void exportGradebook(Gradebook2ComponentService service, String gradebookUid, 
			final boolean includeStructure, final boolean includeComments, PrintWriter writer, 
			HttpServletResponse response, String fileType) 
	throws FatalException {

		Gradebook gradebook = service.getGradebook(gradebookUid);
		Item gradebookItemModel = gradebook.getGradebookItemModel();
		NewRawFile out = new NewRawFile(); 

		Long gradebookId = gradebook.getGradebookId();
		final List<String> headerIds = new ArrayList<String>();

		final List<String> headerColumns = new LinkedList<String>();

		headerColumns.add("Student Id");
		headerColumns.add("Student Name");

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


			categoriesRow.add("");
			categoriesRow.add(StructureRow.CATEGORY.getDisplayName());

			percentageGradeRow.add("");
			percentageGradeRow.add(StructureRow.PERCENT_GRADE.getDisplayName());

			pointsRow.add("");
			pointsRow.add(StructureRow.POINTS.getDisplayName());

			percentCategoryRow.add("");
			percentCategoryRow.add(StructureRow.PERCENT_CATEGORY.getDisplayName());

			dropLowestRow.add("");
			dropLowestRow.add(StructureRow.DROP_LOWEST.getDisplayName());

			equalWeightRow.add("");
			equalWeightRow.add(StructureRow.EQUAL_WEIGHT.getDisplayName());

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
					} 

					if (includeComments) {
						categoriesRow.add("");
						percentageGradeRow.add("");
						dropLowestRow.add("");
						equalWeightRow.add("");
					}

					StringBuilder text = new StringBuilder();
					text.append(itemModel.getName());

					if (Util.checkBoolean(itemModel.getExtraCredit())) {
						text.append(AppConstants.EXTRA_CREDIT_INDICATOR);
					}

					if (!Util.checkBoolean(itemModel.getIncluded())) {
						text.append(AppConstants.UNINCLUDED_INDICATOR);
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

					break;
			}

			String[] blankLine = { "" };
			out.addRow(blankLine);
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

		Roster result = service.getRoster(gradebookUid, gradebookId, null, null, null, null, null, true, false);

		List<Learner> rows = result.getLearnerPage();

		if (headerIds != null) {

			if (rows != null) {
				for (Learner row : rows) {
					List<String> dataColumns = new LinkedList<String>();
					dataColumns.add((String)row.get(LearnerKey.S_EXPRT_USR_ID.name()));
					dataColumns.add((String)row.get(LearnerKey.S_LST_NM_FRST.name()));

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

		StringBuilder filename = new StringBuilder();
		Site site = service.getSite();
		
		if (site == null)
			filename.append("gradebook");
		else {
			String name = site.getTitle();
			name = name.replaceAll("\\s", "");

			filename.append(name);
		}

		service.postEvent("gradebook2.export", String.valueOf(gradebookId));
		
		if (fileType.equals("xls97"))
		{
			filename.append(".xls");

			if (response != null) {
				response.setContentType("application/ms-excel");
				response.setHeader("Content-Disposition", "attachment; filename=" + filename.toString());
			}
			createXLS97File(filename.toString(), response, out); 

		}
		else if (fileType.equals("csv"))
		{
			filename.append(".csv");

			if (response != null) {
				response.setContentType("application/ms-excel");
				response.setHeader("Content-Disposition", "attachment; filename=" + filename.toString());
			}
			try {
				createCSVFile(response, out);
			} catch (IOException e) {
				log.error("Caught I/O exception ", e); 
				throw new FatalException(e); 
			}			
		}

	}
	
	private void exportViewOptionsAndScaleEC(NewRawFile out, Gradebook gradebook) {
		
		Item firstGBItem = gradebook.getGradebookItemModel(); 
		if (firstGBItem.getExtraCreditScaled().booleanValue())
		{
			outputStructureTwoPartExportRow(StructureRow.SCALED_EC.getDisplayName(), "true", out); 
		}
		
		if (firstGBItem.getReleaseGrades().booleanValue())
		{
			outputStructureTwoPartExportRow(StructureRow.SHOWCOURSEGRADES.getDisplayName(), "true", out); 		
		}

		if (firstGBItem.getReleaseItems().booleanValue())
		{
			outputStructureTwoPartExportRow(StructureRow.SHOWRELEASEDITEMS.getDisplayName(), "true", out); 		
		}

		if (firstGBItem.getShowItemStatistics().booleanValue())
		{
			outputStructureTwoPartExportRow(StructureRow.SHOWITEMSTATS.getDisplayName(), "true", out); 
		}

		if (firstGBItem.getShowMean().booleanValue())
		{
			outputStructureTwoPartExportRow(StructureRow.SHOWMEAN.getDisplayName(), "true", out); 
		}

		if (firstGBItem.getShowMedian().booleanValue())
		{
			outputStructureTwoPartExportRow(StructureRow.SHOWMEDIAN.getDisplayName(), "true", out); 
		}

		if (firstGBItem.getShowMode().booleanValue())
		{
			outputStructureTwoPartExportRow(StructureRow.SHOWMODE.getDisplayName(), "true", out); 
		}

		if (firstGBItem.getShowRank().booleanValue())
		{
			outputStructureTwoPartExportRow(StructureRow.SHOWRANK.getDisplayName(), "true", out); 
		}		
	}

	private void outputStructureTwoPartExportRow(String optionName, String optionValue, NewRawFile out)
	{
		String[] rowString; 
		rowString = new String[3]; 
		rowString[0] = ""; 
		rowString[1] = optionName;
		rowString[2] = optionValue;
		out.addRow(rowString); 
	}

	private void createXLS97File(String title, HttpServletResponse response, NewRawFile out) throws FatalException {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet s = wb.createSheet(title);
		
		out.startReading(); 
		String[] curLine = null; 
		int row = 0; 
		
		HSSFRow r = null;
		while ( (curLine = out.readNext()) != null) {
			r = s.createRow(row); 

			for (int i = 0; i < curLine.length ; i++) {
				HSSFCell cl = r.createCell(i);
				cl.setCellType(HSSFCell.CELL_TYPE_STRING); 
				cl.setCellValue(new HSSFRichTextString(curLine[i])); 
			}
			
			row++; 
		}
		
		// Run autosize on last row's columns
		if (r != null) {
			for (int i = 0; i <= r.getLastCellNum() ; i++) {
				s.autoSizeColumn((short) i);
			}
		}
 		
		try {
			wb.write(response.getOutputStream());
			response.getOutputStream().flush();
			response.getOutputStream().close(); 
		} catch (IOException e) {
			log.error("Caught exception " + e, e); 
			throw new FatalException(e); 

		}
	}


	private void createCSVFile(HttpServletResponse response,
			 NewRawFile out) throws IOException {
		
		
		CSVWriter csvWriter = new CSVWriter(response.getWriter());
		out.startReading(); 
		String[] curLine; 
		while ((curLine = out.readNext()) != null)
		{
			csvWriter.writeNext(curLine); 
		}
		try {
			csvWriter.close();
			response.getWriter().flush();
			response.getWriter().close(); 
		} catch (IOException e) {
			log.error("Caught ioexception: ", e);
		} 
	}


	private HSSFWorkbook readPoiSpreadsheet(BufferedInputStream is) throws IOException 
	{
		HSSFWorkbook ret = null; 
		HSSFWorkbook inspread = null;


		is.mark(1024*1024*512); 
		try {
			inspread = new HSSFWorkbook(POIFSFileSystem.createNonClosingInputStream(is));
			log.debug("here!"); 
		} 
		catch (IOException e) 
		{
			log.debug("Caught I/O Exception", e);
			ret = null; 
		} 
		catch (IllegalArgumentException iae)
		{
			log.debug("Caught IllegalArgumentException Exception", iae);
			ret = null; 
		}
		if (ret == null)
		{
			is.reset(); 
		}

		return inspread; 

	}


	private boolean checkForCurrentAssignmentInGradebook(String fileName, Gradebook2ComponentService service, GradebookToolService gbToolService, String gradebookUid)
	{
		Gradebook gm = service.getGradebook(gradebookUid); 
		List<Assignment> assignments = gbToolService.getAssignments(gm.getGradebookId()); 
		for (Assignment curAssignment : assignments)
		{
			String curAssignmentName = curAssignment.getName(); 
			log.debug("curAssignmentName=" + curAssignmentName);
			if (curAssignment.getName().equals(fileName))
			{
				return true; 
			}
		}

		return false; 

	}
	private String getUniqueFileNameForFileName(String fileName,
			Gradebook2ComponentService service, GradebookToolService gbToolService, String gradebookUid) throws GradebookImportException {

		log.debug("fileName=" + fileName);
		if (fileName == null || fileName.equals(""))
		{
			log.debug("null filename, returning default"); 
			return "Scantron Import"; 
		}
		
		int i = 1;
		String curFileName = fileName; 
		while (true)
		{
			log.debug("curFileName: " + curFileName); 
			if (!checkForCurrentAssignmentInGradebook(curFileName, service, gbToolService, gradebookUid))
			{
				log.debug("returning curFileName"); 
				return curFileName; 
			}
			else
			{
				curFileName = fileName + "-" +i; 
			}
			i++; 

			if (i > 1000)
			{
				throw new GradebookImportException("Couldn't find a unique filename within 1000 tries, please rename filename manually and import again"); 
			}
		}
	}

	/*
	 * so basically, we'll do: 
	 * 1) Scan the sheet for scantron artifacts, and if so convert to a simple CSV file which is 
	 */
	public Upload parseImportXLS(Gradebook2ComponentService service, 
			String gradebookUid, InputStream is, String fileName, GradebookToolService gbToolService, 
			boolean doPreventOverwrite) throws InvalidInputException, FatalException, IOException {
		log.debug("parseImportXLS() called"); 

		// Strip off extension
		if (fileName != null) {
			int indexOfExtension = fileName.lastIndexOf('.');
			if (indexOfExtension != -1 && indexOfExtension < fileName.length()) {
				fileName = fileName.substring(0, indexOfExtension);
			}
		}
		
		String realFileName = fileName; 
		boolean isOriginalName; 
		
		try {
			realFileName = getUniqueFileNameForFileName(fileName, service, gbToolService, gradebookUid);
		} catch (GradebookImportException e) {
			Upload importFile = new UploadImpl(); 
			importFile.setErrors(true); 
			importFile.setNotes(e.getMessage()); 
			return importFile; 
		} 
		isOriginalName = realFileName.equals(fileName);
		log.debug("realFileName=" + realFileName);
		log.debug("isOriginalName=" + isOriginalName);

		HSSFWorkbook inspread = null;

		BufferedInputStream bufStream = new BufferedInputStream(is); 

		inspread = readPoiSpreadsheet(bufStream);

		if (inspread != null)
		{
			log.debug("Found a POI readable spreadsheet");
			bufStream.close(); 
			return handlePoiSpreadSheet(inspread, service, gradebookUid, realFileName, isOriginalName);
		}
		else
		{
			log.debug("POI couldn't handle the spreadsheet, using jexcelapi");
			return handleJExcelAPISpreadSheet(bufStream, service, gradebookUid, realFileName, isOriginalName); 
		}

	}

	private Upload handleJExcelAPISpreadSheet(BufferedInputStream is,
			Gradebook2ComponentService service, String gradebookUid, String fileName, boolean isNewAssignmentByFileName) throws InvalidInputException, FatalException, IOException {
		Workbook wb = null; 
		try {
			wb = Workbook.getWorkbook(is);
		} catch (BiffException e) {
			log.error("Caught a biff exception from JExcelAPI: " + e.getLocalizedMessage(), e); 
			return null; 
		} catch (IOException e) {
			log.error("Caught an IO exception from JExcelAPI: " + e.getLocalizedMessage(), e); 
			return null; 
		} 

		is.close();
		Sheet s = wb.getSheet(0); 
		if (s != null)
		{
			if (isScantronSheetForJExcelApi(s))
			{
				return handleScantronSheetForJExcelApi(s, service, gradebookUid, fileName, isNewAssignmentByFileName);
			}
			else
			{
				return handleNormalXLSSheetForJExcelApi(s, service, gradebookUid);
			}
		}
		else
		{
			return null;
		}
	}

	private Upload handleNormalXLSSheetForJExcelApi(Sheet s,
			Gradebook2ComponentService service, String gradebookUid) throws InvalidInputException, FatalException {
		NewRawFile raw = new NewRawFile(); 
		int numRows; 

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
		raw.setFileType("Excel 5.0/7.0 Non Scantron"); 
		raw.setScantronFile(false); 

		return parseImportGeneric(service, gradebookUid, raw);
	}
	private final static int RAWFIELD_FIRST_POSITION = 0; 
	private final static int RAWFIELD_SECOND_POSITION = 1; 

	private Upload handleScantronSheetForJExcelApi(Sheet s,
			Gradebook2ComponentService service, String gradebookUid, String fileName, boolean isNewAssignmentByFileName) throws InvalidInputException, FatalException 
			{
		StringBuilder err = new StringBuilder("Scantron File with errors"); 
		NewRawFile raw = new NewRawFile(); 
		boolean stop = false; 

		Cell studentIdHeader = s.findCell(SCANTRON_HEADER_STUDENT_ID);
		Cell scoreHeader = s.findCell(SCANTRON_HEADER_SCORE);

		if (studentIdHeader == null)
		{
			err.append("There is no column with the header student_id");
			stop = true; 
		}

		if (scoreHeader == null)
		{
			err.append("There is no column with the header score");
			stop = true; 

		}

		if (! stop) 
		{
			raw.addRow(getScantronHeaderLine(fileName)); 
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
			return parseImportGeneric(service, gradebookUid, raw);
		}
		else
		{
			raw.setMessages(err.toString());
			err = null; 
			raw.setErrorsFound(true); 

			return parseImportGeneric(service, gradebookUid, raw);
		}

			}

	private String[] getScantronHeaderLine(String fileName)
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
	private boolean isScantronSheetForJExcelApi(Sheet s) {
		Cell studentIdHeader = s.findCell(SCANTRON_HEADER_STUDENT_ID);
		Cell scoreHeader = s.findCell("score");

		return (studentIdHeader != null && scoreHeader != null); 
	}

	private Upload handlePoiSpreadSheet(HSSFWorkbook inspread, Gradebook2ComponentService service, String gradebookUid, String fileName, boolean isNewAssignmentByFileName) throws InvalidInputException, FatalException
	{
		log.debug("handlePoiSpreadSheet() called"); 
		// FIXME - need to do multiple sheets, and structure
		int numSheets = inspread.getNumberOfSheets();  
		if (numSheets > 0)
		{
			HSSFSheet cur = inspread.getSheetAt(0);
			NewRawFile ret; 
			if (isScantronSheetFromPoi(cur))
			{
				log.debug("POI: Scantron");
				ret = processScantronXls(cur, fileName); 
				ret.setScantronFile(true); 
				ret.setNewAssignment(isNewAssignmentByFileName);
			}
			else
			{
				log.debug("POI: Not scantron");
				ret = processNormalXls(cur); 
			}

			return parseImportGeneric(service, gradebookUid, ret);
		}
		else
		{
			NewRawFile d = new NewRawFile(); 
			d.setMessages("The XLS spreadsheet entered does not contain any valid sheets.  Please correct and try again.");
			d.setErrorsFound(true); 
			return parseImportGeneric(service, gradebookUid, d);

		}
	}

	private NewRawFile processNormalXls(HSSFSheet s) {
		log.debug("processNormalXls() called");
		NewRawFile data = new NewRawFile();
		int numCols = getNumberOfColumnsFromSheet(s); 
		Iterator<HSSFRow> rowIter = s.rowIterator(); 
		boolean headerFound = false;
		int id_col = -1; 
		while (rowIter.hasNext())
		{

			HSSFRow curRow = rowIter.next();  
			if (!headerFound)
			{
				id_col = readHeaderRow(curRow); 
				headerFound = true; 
				log.debug("Header Row # is " + id_col);
			}
			String[] dataEntity = new String[numCols]; 

			log.debug("numCols = " + numCols); 

			for (int i = 0; i < numCols; i++) {
				HSSFCell cl = curRow.getCell(i);
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

	private int getNumberOfColumnsFromSheet(HSSFSheet s) {
		int numCols = 0; 
		Iterator<HSSFRow> rowIter = s.rowIterator(); 
		while (rowIter.hasNext())
		{
			HSSFRow curRow = rowIter.next(); 
			
			if (curRow.getLastCellNum() > numCols)
			{
				numCols = curRow.getLastCellNum(); 
			}
		}
		return numCols;
	}


	private int readHeaderRow(HSSFRow curRow) {
		int ret = -1; 
		Iterator<HSSFCell> cellIterator = curRow.cellIterator(); 
		// FIXME - need to decide to take this out into the institutional adviser 

		while (cellIterator.hasNext())
		{
			HSSFCell cl = cellIterator.next();
			String cellData =  new HSSFDataFormatter().formatCellValue(cl).toLowerCase();

			if ("student id".equals(cellData))
			{
				return cl.getColumnIndex(); 
			}

		}
		return ret; 
	}

	private NewRawFile processScantronXls(HSSFSheet s, String fileName) {
		NewRawFile data = new NewRawFile(); 
		Iterator<HSSFRow> rowIter = s.rowIterator(); 
		StringBuilder err = new StringBuilder("Scantron File with errors"); 
		boolean stop = false; 

		HSSFCell studentIdHeader = findCellWithTextonSheetForPoi(s, SCANTRON_HEADER_STUDENT_ID);
		HSSFCell scoreHeader = findCellWithTextonSheetForPoi(s, SCANTRON_HEADER_SCORE);
		if (studentIdHeader == null)
		{
			err.append("There is no column with the header student_id");
			stop = true; 
		}

		if (scoreHeader == null)
		{
			err.append("There is no column with the header score");
			stop = true; 

		}

		if (! stop) 
		{
			data.addRow(getScantronHeaderLine(fileName));
			while (rowIter.hasNext())
			{ 
				HSSFRow curRow = rowIter.next();  
				HSSFCell score = null, id = null; 

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
					idStr = getDataFromHSSFCellAsStringRegardlessOfCellType(id, false); 
					scoreStr = getDataFromHSSFCellAsStringRegardlessOfCellType(score, true); 
					String[] ent = new String[2];
					ent[0] = idStr; 
					ent[1] = scoreStr;

					data.addRow(ent); 
				}
			}
		}
		return data; 

	}

	private String getDataFromHSSFCellAsStringRegardlessOfCellType(HSSFCell c, boolean decimal)
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

	public HSSFCell findCellWithTextonSheetForPoi(HSSFSheet s, String searchText)
	{
		if (searchText == null || s == null) 
		{
			return null; 			
		}

		Iterator<HSSFRow> rIter = s.rowIterator(); 

		while (rIter.hasNext())
		{
			HSSFRow curRow = rIter.next(); 
			Iterator<HSSFCell> cIter = curRow.cellIterator(); 

			while (cIter.hasNext())
			{
				HSSFCell curCell = cIter.next(); 

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

	private String getStringValueFromCell(HSSFCell c)
	{
		String ret = ""; 
		StringBuilder sb = new StringBuilder(); 

		switch (c.getCellType())
		{
			case HSSFCell.CELL_TYPE_NUMERIC:
				sb.append( Double.toString( c.getNumericCellValue() ) );
				break; 
			case HSSFCell.CELL_TYPE_STRING: 
				sb.append(c.getRichStringCellValue().getString());
				break;

			default:
				sb.append(""); 
		}

		ret = sb.toString();
		sb = null; 
		return ret; 
	}
	
	
	private boolean isScantronSheetFromPoi(HSSFSheet s) {
		Iterator<HSSFRow> rowIter = s.rowIterator(); 
		while (rowIter.hasNext())
		{
			HSSFRow curRow = rowIter.next();  
			HSSFCell possibleHeader = curRow.getCell(0); 

			if (possibleHeader != null && possibleHeader.getCellType() == HSSFCell.CELL_TYPE_STRING &&  SCANTRON_HEADER_STUDENT_ID.equals(possibleHeader.getRichStringCellValue().getString()) )
			{
				return true; 
			}
		}
		// If after all that, we don't find a line starting with "student_row", we're not a scantron.. 
		return false;
	}

	public Upload parseImportCSV(Gradebook2ComponentService service, 
			String gradebookUid, Reader reader) throws InvalidInputException, FatalException 
			{

		NewRawFile rawData = new NewRawFile(); 
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
		}

		rawData.setFileType("CSV file"); 
		return parseImportGeneric(service, gradebookUid, rawData);
	}	

	private int readDataForStructureInformation(NewRawFile rawData, Map<String, StructureRow> structureLineIndicatorMap, Map<StructureRow, String[]> structureColumnsMap) 
	{
		log.debug("readDataForStructureInformation() called");
		int rows = 0;
		int retRows = -1; 
		boolean headerFound = false; 
		String[] columns;
		rawData.startReading();
		while ( !headerFound && (columns = rawData.readNext()) != null) 
		{
			// Until we run into a line that begins with one of the header line indicators, we can safely 
			// assume that we might find some structure info
			// Not sure what cases the first column will be null.  
			if (columns[0] == null)
			{
				continue;
			}
			String firstColumnLowerCase = columns[0].toLowerCase();
			if (log.isDebugEnabled())
				log.debug("SI: firstColumnLowerCase=" + firstColumnLowerCase);
			
			if (!headerLineIndicatorSet.contains(firstColumnLowerCase)) {
				log.debug("Processed non header line"); 
				// Since it's not processed yet, check to see if the current row has any useful info
				for (int i=0;i<columns.length;i++) {
					if (columns[i] != null && !columns[i].equals("")) 
					{

						String columnLowerCase = columns[i].trim().toLowerCase();
						if (log.isDebugEnabled())
							log.debug("SI: columnLowerCase=" + columnLowerCase);
						StructureRow structureRow = structureLineIndicatorMap.get(columnLowerCase);

						if (structureRow != null) {
							structureColumnsMap.put(structureRow, columns);
						}
					}
				}

			} else {
				retRows = rows; 
			}
			rows++; 
		}

		return retRows; 
	}


	public void readInHeaderColumns(NewRawFile rawData, NewImportExportInformation ieInfo, int startRow) {
		String[] headerColumns = null;
		headerColumns = rawData.getRow(startRow);
		
		if (headerColumns == null)
			return;
		
		NewImportHeader[] headers = new NewImportHeader[headerColumns.length];
		
		//ieInfo.setHeaderColumns(headerColumns);

		for (int i = 0; i < headerColumns.length; i++) {
			String text = headerColumns[i];
			String lowerText = text == null ? null : text.trim().toLowerCase();

			NewImportHeader header = null;
			
			// Check for name field
			if (lowerText == null || lowerText.equals("") || scantronIgnoreSet.contains(lowerText)) {
				continue;
			} else if (nameSet.contains(lowerText)) {
				header = new NewImportHeader(Field.S_NAME, text, i);
				header.setId("NAME");
				//ieInfo.setNameFieldIndex(i);
			} else if (idSet.contains(lowerText)) {
				header = new NewImportHeader(Field.S_ID, text, i);
				header.setId("ID");
				ieInfo.trackActiveHeaderIndex(i);
				//ieInfo.setIdFieldIndex(i);
			} else if (lowerText.equalsIgnoreCase("course grade")) {
				header = new NewImportHeader(Field.S_CRS_GRD, text, i);
				//ieInfo.setCourseGradeFieldIndex(i);
			} else if (lowerText.equalsIgnoreCase("calculated grade")) {
				header = new NewImportHeader(Field.S_CALC_GRD, text, i);
			} else if (lowerText.equalsIgnoreCase("letter grade")) {
				header = new NewImportHeader(Field.S_LTR_GRD, text, i);
			} else if (lowerText.equalsIgnoreCase("audit grade")) {
				header = new NewImportHeader(Field.S_ADT_GRD, text, i);
			} else if (lowerText.equalsIgnoreCase("grade override")) {
				header = new NewImportHeader(Field.S_GRB_OVRD, text, i);
				ieInfo.trackActiveHeaderIndex(i);
			} else {
				
				// Build an item or comments header
				String name = null;
				String points = null;
				boolean isExtraCredit = text.contains(AppConstants.EXTRA_CREDIT_INDICATOR);

				if (isExtraCredit) {
					if (log.isDebugEnabled())
						log.debug("X: Column " + i + " has extra credit");
					text = text.replace(AppConstants.EXTRA_CREDIT_INDICATOR, "");
				}

				boolean isUnincluded = text.contains(AppConstants.UNINCLUDED_INDICATOR);

				if (isUnincluded) {
					if (log.isDebugEnabled())
						log.debug("X: Column " + i + " is unincluded");
					text = text.replace(AppConstants.UNINCLUDED_INDICATOR, "");
				}

				name = text;

				boolean isComment = text.startsWith(AppConstants.COMMENTS_INDICATOR);

				if (isComment) {
					name = text.substring(AppConstants.COMMENTS_INDICATOR.length());
				}

				int startParenthesis = text.indexOf("[");
				int endParenthesis = text.indexOf("pts]");

				if (endParenthesis == -1)
					endParenthesis = text.indexOf("]");

				if (startParenthesis != -1 && endParenthesis != -1
						&& endParenthesis > startParenthesis + 1) {
					if (log.isDebugEnabled())
						log.debug("X: Column " + i + " has pts indicated");
					points = text.substring(startParenthesis + 1, endParenthesis);
					if (log.isDebugEnabled())
						log.debug("X: Column " + i + " points are " + points);
					name = text.substring(0, startParenthesis);
					if (log.isDebugEnabled())
						log.debug("X: Column " + i + " name is " + points);

					if (name != null)
						name = name.trim();
				}

				if (name != null) {
					if (isComment) {
						header = new NewImportHeader(Field.S_COMMENT, text, i);
						header.setHeaderName(name);
						ieInfo.trackActiveHeaderIndex(i);
					} else {
						StringBuffer value = new StringBuffer(name);
						header = new NewImportHeader(Field.S_ITEM, value.toString(), i);
						header.setHeaderName(name);
						header.setExtraCredit(isExtraCredit);
						header.setUnincluded(isUnincluded);
						header.setPoints(points);
						
						ieInfo.trackActiveHeaderIndex(i);
					}
				}
			}

			headers[i] = header;
		}
		ieInfo.setHeaders(headers);
		//importInfo.setHeaderColumns(headerColumns);
		log.debug("XXX: readInHeaderInfo() finished");
	}

	public void readInGradeDataFromImportFile(NewRawFile rawData, 
			NewImportExportInformation ieInfo, Map<String, UserDereference> userDereferenceMap, 
			List<Learner> importRows, int startRow, Gradebook2ComponentService service) {
		String[] columns; 
		
		rawData.goToRow(startRow); 
		while ((columns = rawData.readNext()) != null) {

			Learner row = new LearnerImpl();
			
			GradeType gradeType = ieInfo.getGradebookItemModel().getGradeType();
			
			for (NewImportHeader importHeader : ieInfo.findActiveHeaders()) {
				
				if (importHeader == null)
					continue;
				
				int i = importHeader.getColumnIndex();
				String id = importHeader.getId();
				
				if (i >= columns.length)
					continue;
				
				if (columns[i] != null && !columns[i].equals("") && importHeader.getField() != null) {
					switch (importHeader.getField()) {
					case S_ID:
						String userImportId = columns[i];
						row.setExportUserId(userImportId);
						row.setStudentDisplayId(userImportId);
						
						UserDereference userDereference = userDereferenceMap.get(userImportId);

						if (userDereference != null) {
							row.setIdentifier(userDereference.getUserUid());
							row.setStudentName(userDereference.getDisplayName());
							row.setLastNameFirst(userDereference.getLastNameFirst());
							row.setStudentDisplayId(userDereference.getDisplayId());
							row.setUserNotFound(Boolean.FALSE);
						} else {
							row.setLastNameFirst("User not found");
							row.setUserNotFound(Boolean.TRUE);
							ieInfo.setUserNotFound(true);
						}
						break;
					case S_NAME:
						row.set(LearnerKey.S_DSPLY_NM.name(), columns[i]);
						break;
					case S_GRB_OVRD:
						row.set(LearnerKey.S_OVRD_GRD.name(), columns[i]);
						break;
					case S_ITEM:
						boolean isFailure = false;
						try {
							double d = Double.parseDouble(columns[i]);
							
							Item item = importHeader.getItem();
							if (item != null) {
								Double points = item.getPoints();
								if (points != null) {
									if (points.doubleValue() < d) {
										
										if (item.getItemId() != null && item.getItemId().equals(Long.valueOf(-1l))) {
											// Ensure that we have an int
											d = Math.ceil(d);
											
											// Round to the nearest hundred if d > 100,
											// otherwise, round to the nearest ten
											if (d > 100) {
												d = d / 100;
												d = Math.ceil(d);
												d = d * 100;
											} else if (d > 10) {
												d = d / 10;
												d = Math.ceil(d);
												d = d * 10;
											}
											
											item.setPoints(d);
										} else {
											isFailure = true;
											ieInfo.setInvalidScore(true);
										}
									}
								}
							} 
							
						} catch (NumberFormatException nfe) {
							// This is not necessarily an exception, for example, we might be
							// reading letter grades
							
							if (gradeType != GradeType.LETTERS || !service.isValidLetterGrade(columns[i])) {
								isFailure = true;
								ieInfo.setInvalidScore(true);
							} 
						}
						
						if (isFailure) {
							String failedId = Util.buildFailedKey(id);
							row.set(failedId, "This entry is not valid");
						}
						
						row.set(id, columns[i]);
						break;
					case S_COMMENT:
						row.set(Util.buildCommentKey(id), Boolean.TRUE);
						row.set(Util.buildCommentTextKey(id), columns[i]);
						break;
					}
				}
			}
			
			importRows.add(row);
		}	
		
	}

	public void processStructureInformation(NewImportExportInformation ieInfo, Map<StructureRow, String[]> structureColumnsMap) throws InvalidInputException
	{
		// Now, modify gradebook structure according to the data stored
		String[] gradebookColumns = structureColumnsMap.get(StructureRow.GRADEBOOK);
		GradeItem gradebookItemModel = (GradeItem)ieInfo.getGradebookItemModel();
		CategoryType cType = gradebookItemModel.getCategoryType();
		GradeType gType = gradebookItemModel.getGradeType();
		
		if (gradebookColumns != null && gradebookItemModel != null) {
			String gradebookName = null;
			String categoryType = null;
			String gradeType = null;

			if (gradebookColumns.length >= 3)
				gradebookName = gradebookColumns[2];
			if (gradebookColumns.length >= 4)
				categoryType = gradebookColumns[3];
			if (gradebookColumns.length >= 5)
				gradeType = gradebookColumns[4];

			if (gradebookName != null)
				gradebookItemModel.setName(gradebookName);
			if (categoryType != null) {
				if (CategoryType.NO_CATEGORIES.getDisplayName().equals(categoryType))
					cType = CategoryType.NO_CATEGORIES;
				else if (CategoryType.SIMPLE_CATEGORIES.getDisplayName().equals(categoryType))
					cType = CategoryType.SIMPLE_CATEGORIES;
				else if (CategoryType.WEIGHTED_CATEGORIES.getDisplayName().equals(categoryType))
					cType = CategoryType.WEIGHTED_CATEGORIES;

				// If the upload changes the status of having categories, then update this local var
				//ieInfo.setCategoryType(cType);

				gradebookItemModel.setCategoryType(cType);
			}
			if (gradeType != null) {
				if (getDisplayName(GradeType.PERCENTAGES).equals(gradeType))
					gType = GradeType.PERCENTAGES;
				else if (getDisplayName(GradeType.POINTS).equals(gradeType))
					gType = GradeType.POINTS;//TODO: Add letter grades

				gradebookItemModel.setGradeType(gType);
			}

			//gradebookItemModel = service.updateItem(gradebookItemModel);
		}
		processStructureInformationForDisplayAndScaledOptions(gradebookItemModel, ieInfo, structureColumnsMap);
		// We don't need to process any of the logic below if the gradebook is in "No Categories" mode
		if (cType == CategoryType.NO_CATEGORIES)
			return;

		String[] categoryColumns = structureColumnsMap.get(StructureRow.CATEGORY);
		String[] percentGradeColumns = structureColumnsMap.get(StructureRow.PERCENT_GRADE);
		String[] dropLowestColumns = structureColumnsMap.get(StructureRow.DROP_LOWEST);
		String[] equalWeightColumns = structureColumnsMap.get(StructureRow.EQUAL_WEIGHT);
		//String[] pointsColumns = structureColumnsMap.get(StructureRow.POINTS);
		//String[] percentCategoryColumns = structureColumnsMap.get(StructureRow.PERCENT_CATEGORY);

		String[] categoryRangeColumns = new String[(ieInfo.getHeaders() != null ? ieInfo.getHeaders().length : 0)];
		Map<String, GradeItem> categoryMap = new HashMap<String, GradeItem>();

		if (categoryColumns != null) {

			for (GradeItem child : gradebookItemModel.getChildren()) {
				if (child.getItemType() != null && child.getItemType() == ItemType.CATEGORY)
					categoryMap.put(child.getName(), child);
			}

			String currentCategoryId = null;

			for (int i=0;i<categoryColumns.length;i++) {
				if (categoryColumns[i].trim().equals("")) {
					if (currentCategoryId != null && i < categoryRangeColumns.length)
						categoryRangeColumns[i] = currentCategoryId;
					
					
					continue;
				}

				if (categoryColumns[i].equals(StructureRow.CATEGORY.getDisplayName()))
					continue;

				GradeItem categoryModel = null;
				// In this case, we have a new category that needs to be added to the gradebook
				String categoryName = categoryColumns[i];
				boolean isExtraCredit = categoryName.contains(AppConstants.EXTRA_CREDIT_INDICATOR);

				if (isExtraCredit)
					categoryName = categoryName.replace(AppConstants.EXTRA_CREDIT_INDICATOR, "");

				boolean isUnincluded = categoryName.contains(AppConstants.UNINCLUDED_INDICATOR);

				if (isUnincluded)
					categoryName = categoryName.replace(AppConstants.UNINCLUDED_INDICATOR, "");

				boolean isDefaultCategory = categoryName.equalsIgnoreCase(AppConstants.DEFAULT_CATEGORY_NAME);
				boolean isNewCategory = !categoryMap.containsKey(categoryName);
				
				if (isNewCategory || isDefaultCategory) {
					
					if (isDefaultCategory) {
						// Check if the default category is already in this Gradebook
						List<GradeItem> children = gradebookItemModel.getChildren();
						if (children != null && children.size() > 0) {
							for (GradeItem child : children) {
								if (child.getName().equals(AppConstants.DEFAULT_CATEGORY_NAME)) {
									categoryModel = child;
									break;
								}
							}
						}
					}
					
					if (categoryModel == null) {
						String identifier = isDefaultCategory ? String.valueOf(Long.valueOf(-1l)) : "NEW:CAT:" + i;
						
						categoryModel = new GradeItemImpl();
						categoryModel.setIdentifier(identifier);
						categoryModel.setItemType(ItemType.CATEGORY);
						categoryModel.setName(categoryName);
						if (isNewCategory) {
							// We only worry about these for new categories, the default category is by definition unincluded and not extra credit
							categoryModel.setIncluded(Boolean.valueOf(!isUnincluded));
							categoryModel.setExtraCredit(Boolean.valueOf(isExtraCredit));
						}
						
						gradebookItemModel.addChild((GradeItem)categoryModel);
					}
				} else {
					// Otherwise, we may still want to update scores
					categoryModel = categoryMap.get(categoryName);
				}


				if (categoryModel != null) {
					if (percentGradeColumns != null && percentGradeColumns.length > i) {
						String percentGrade = percentGradeColumns[i];

						if (percentGrade != null) {
							try {
								percentGrade = percentGrade.replace("%", "");
								double pG = Double.parseDouble(percentGrade);
								categoryModel.setPercentCourseGrade(Double.valueOf(pG));
								categoryModel.setWeighting(Double.valueOf(pG));
							} catch (NumberFormatException nfe) {
								log.info("Failed to parse " + percentGrade + " as a Double");
							}
						}
					}

					if (dropLowestColumns != null && dropLowestColumns.length > i) {
						String dropLowest = dropLowestColumns[i];

						if (dropLowest != null) {
							try {
								if (dropLowest.trim().equals(""))
									dropLowest = "0";

								int dL = Integer.parseInt(dropLowest);
								categoryModel.setDropLowest(Integer.valueOf(dL));
							} catch (NumberFormatException nfe) {
								log.info("Failed to parse " + dropLowest + " as an Integer");
							}
						}

					}

					if (equalWeightColumns != null && equalWeightColumns.length > i) {
						String equalWeight = equalWeightColumns[i];

						if (equalWeight != null) {
							try {
								boolean isEqualWeight = Boolean.parseBoolean(equalWeight);
								categoryModel.setEqualWeightAssignments(Boolean.valueOf(isEqualWeight));
							} catch (NumberFormatException nfe) {
								log.info("Failed to parse " + equalWeight + " as an Boolean");
							}
						}
					}
					
					
					/*Item result = null;
					if (/--*!*--/isDefaultCategory) {
						/--*if (isModelNew) {
							result = service.createItem(gradebook.getGradebookUid(), gradebook.getGradebookId(), categoryModel, false);
						} else if (isModelUpdated) 
							result = service.updateItem(categoryModel);
					} else {*--/
						currentCategoryId = "-1";
						if (categoryRangeColumns.length > i)
							categoryRangeColumns[i] = "-1";
					}*/

					if (categoryModel != null) {
						if (categoryModel.getIdentifier() != null && !categoryModel.getIdentifier().equals("null")) {
							currentCategoryId = categoryModel.getIdentifier();
							ieInfo.getCategoryIdItemMap().put(currentCategoryId, categoryModel);
							
							if (categoryRangeColumns.length > i)
								categoryRangeColumns[i] = currentCategoryId;
						}
						if (categoryModel.getCategoryId() != null) {
							String categoryIdAsString = String.valueOf(categoryModel.getCategoryId());
							ieInfo.getCategoryIdNameMap().put(categoryIdAsString, categoryModel.getName());
						}
					}
					
					categoryModel.setChecked(true);
					/*
					if (result != null) {
						Item activeModel = getActiveModel((GradeItem)result);

						if (activeModel != null) {
							if (activeModel.getIdentifier() != null && !activeModel.getIdentifier().equals("null")) {
								currentCategoryId = activeModel.getIdentifier();
							
								if (categoryRangeColumns.length > i)
									categoryRangeColumns[i] = currentCategoryId;
							}
							if (activeModel.getCategoryId() != null) {
								ieInfo.getCategoryIdNameMap().put(
									activeModel.getCategoryId(), activeModel.getName());
							}
						}
					}*/
				}
			}
		} else {
			// Category columns are null, we still need to ensure that we track all of the existing 
			// categories in this gradebook
			
			if (cType != CategoryType.NO_CATEGORIES && gradebookItemModel != null) {
			
				List<GradeItem> children = gradebookItemModel.getChildren();
				
				if (children != null) {
					for (GradeItem categoryModel : children) {
						ieInfo.getCategoryIdItemMap().put(categoryModel.getIdentifier(), categoryModel);
					}
				}
				
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
		}

		ieInfo.setCategoryRangeColumns(categoryRangeColumns);
	}

	
	private void processStructureInformationForDisplayAndScaledOptions(
			GradeItem gradebookItemModel, NewImportExportInformation ieInfo,
			Map<StructureRow, String[]> structureColumnsMap) {
		
		OptionState scaledEC = checkRowOption(StructureRow.SCALED_EC, structureColumnsMap); 
		OptionState showCourseGrades = checkRowOption(StructureRow.SHOWCOURSEGRADES, structureColumnsMap);
		OptionState showItemStats = checkRowOption(StructureRow.SHOWITEMSTATS, structureColumnsMap); 
		OptionState showMean = checkRowOption(StructureRow.SHOWMEAN, structureColumnsMap);
		OptionState showMedian = checkRowOption(StructureRow.SHOWMEDIAN, structureColumnsMap); 
		OptionState showMode = checkRowOption(StructureRow.SHOWMODE, structureColumnsMap);
		OptionState showRank = checkRowOption(StructureRow.SHOWRANK, structureColumnsMap); 
		OptionState showReleasedItems = checkRowOption(StructureRow.SHOWRELEASEDITEMS, structureColumnsMap);
		
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

	private void processHeaders(NewImportExportInformation ieInfo, Map<StructureRow, String[]> structureColumnsMap) throws ImportFormatException {
		NewImportHeader[] headers = ieInfo.getHeaders();
		
		if (headers == null)
			return;
		
		Item gradebookItemModel = ieInfo.getGradebookItemModel();
		CategoryType categoryType = gradebookItemModel.getCategoryType();
		
		// Although these contain "structure" information, it's most efficient to check them while we're looping through 
		// the header columns
		String[] pointsColumns = structureColumnsMap.get(StructureRow.POINTS);
		String[] percentCategoryColumns = structureColumnsMap.get(StructureRow.PERCENT_CATEGORY);
		
		String[] categoryRangeColumns = ieInfo.getCategoryRangeColumns();
	
		Map<String, GradeItem> categoryIdItemMap = ieInfo.getCategoryIdItemMap();
		
		// Iterate through each header once
		for (int i=0;i<headers.length;i++) {
		
			NewImportHeader header = headers[i];
			
			// Ignore null headers
			if (header == null)
				continue;
	
			
			if (header.getField() == Field.S_ITEM || header.getField() == Field.S_COMMENT) {
				
				// These two rows (pointsColumns and percentCategoryColumns) may not be present, for example, if it's a grades-only
				// import, in which case we want to ensure that 
				if (header.getField() == Field.S_ITEM) {
					if (pointsColumns != null && pointsColumns.length > i && Util.isNotNullOrEmpty(pointsColumns[i])) {
						header.setPoints(pointsColumns[i]);
					}
		
					if (percentCategoryColumns != null && percentCategoryColumns.length > i && Util.isNotNullOrEmpty(percentCategoryColumns[i])) {
						header.setPercentCategory(percentCategoryColumns[i]);
					}
				}
				
				String itemName = header.getHeaderName();
				GradeItem itemModel = null;
				GradeItem categoryModel = null;
				
				switch (categoryType) {
				case NO_CATEGORIES:
					itemModel = findModelByName(itemName, gradebookItemModel);
					break;
				case SIMPLE_CATEGORIES:
				case WEIGHTED_CATEGORIES:
					String categoryId = categoryRangeColumns[i];
					
					if (categoryId == null) {
						itemModel = findModelByName(itemName, gradebookItemModel);
						
						// If this is a new item, and we don't have structure info, then
						// we have to make it "Unassigned"
						if (itemModel == null)
							categoryModel = categoryIdItemMap.get("-1");
						
					} else {
						categoryModel = categoryId == null ? null : categoryIdItemMap.get(categoryId);
						
						if (categoryModel != null) {
							header.setCategoryId(categoryModel.getIdentifier());
							header.setCategoryName(categoryModel.getCategoryName());
						
							List<GradeItem> children = categoryModel.getChildren();
							if (children != null && children.size() > 0) {
								for (GradeItem item : children) {
									if (item.getName().equals(itemName)) {
										itemModel = item;
										break;
									}
								}
								
							}
						} 
					} 
					
					break;
				}
				

				boolean isNewItem = false;
				
				if (itemModel == null) {
					isNewItem = true;
					itemModel = new GradeItemImpl();
					
					String identifier = new StringBuilder().append("NEW:").append(i).toString();
					header.setId(identifier);
					itemModel.setItemType(ItemType.ITEM);
					itemModel.setStudentModelKey(LearnerKey.S_ITEM.name());
					itemModel.setIdentifier(identifier);
					itemModel.setName(header.getHeaderName());
					itemModel.setItemId(Long.valueOf(-1l));
					itemModel.setCategoryId(Long.valueOf(-1l));
					itemModel.setCategoryName(header.getCategoryName());
					itemModel.setPoints(Double.valueOf(100d));
				} else {
					header.setId(itemModel.getIdentifier());
				}
				
				if (header.getField() == Field.S_ITEM) {
					// Modify the points if a point value was included in the import file
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
					
					// Modify the percentage category contribution
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
					
					itemModel.setIncluded(Boolean.valueOf(!header.isUnincluded()));
					itemModel.setExtraCredit(Boolean.valueOf(header.isExtraCredit()));
					itemModel.setChecked(true);
					
					header.setItem(itemModel);
				}
				
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
		
		}
	}
	
	
	/*
	private void processHeadersX(NewImportExportInformation ieInfo, Map<StructureRow, String[]> structureColumnsMap) {
		if (ieInfo.getHeaderColumns() != null) {
			
			
			String[] pointsColumns = structureColumnsMap.get(StructureRow.POINTS);
			String[] percentCategoryColumns = structureColumnsMap.get(StructureRow.PERCENT_CATEGORY);
			String[] categoryRangeColumns = ieInfo.getCategoryRangeColumns();

			String[] headerColumns = ieInfo.getHeaderColumns(); 
			for (int i=0;i<headerColumns.length;i++) {
				String text = headerColumns[i];
				if (text == null || text.trim().equals("")) {
					continue;
				} else if (text.equalsIgnoreCase("student name") || text.equalsIgnoreCase("name") ||
						text.equalsIgnoreCase("learner")) {
					continue;
				} else if (text.equalsIgnoreCase("student id") || text.equalsIgnoreCase("identifier") ||
						text.equalsIgnoreCase("userId") || text.equalsIgnoreCase("learnerid")) {
					continue;
				} else if (text.equalsIgnoreCase("course grade")) {
					continue;
				}

				boolean isExtraCredit = text.contains(AppConstants.EXTRA_CREDIT_INDICATOR);

				if (isExtraCredit) {
					text = text.replace(AppConstants.EXTRA_CREDIT_INDICATOR, "");
				}

				boolean isUnincluded = text.contains(AppConstants.UNINCLUDED_INDICATOR);

				if (isUnincluded) {
					text = text.replace(AppConstants.UNINCLUDED_INDICATOR, "");
				}

				String name = text;

				int startParenthesis = text.indexOf("[");
				int endParenthesis = text.indexOf("pts]");

				if (endParenthesis == -1)
					endParenthesis = text.indexOf("]");

				if (startParenthesis != -1 && endParenthesis != -1 && endParenthesis > startParenthesis+1) {
					name = text.substring(0, startParenthesis);

					if (name != null)
						name = name.trim();
				}

				StringBuilder key = new StringBuilder();

				String categoryId = null;
				String categoryName = null;
				if (categoryRangeColumns != null && categoryRangeColumns.length > i) {
					categoryId = categoryRangeColumns[i];

					if (categoryId != null) {
						categoryName = ieInfo.getCategoryIdNameMap().get(categoryId);
						if (categoryName != null) 
							key.append(categoryName).append("::");
					}
				}

				key.append(name);

				ImportHeader header = ieInfo.getHeaderMap().get(key.toString());

				if (header != null) {
					
					if (header.getField() == null || header.getField().equals(Field.S_COMMENT.name()))
						continue;
					
					if (isExtraCredit)
						header.setExtraCredit(Boolean.valueOf(isExtraCredit));

					if (pointsColumns != null && pointsColumns.length > i) {
						String points = pointsColumns[i];

						if (points != null) {
							header.setPoints(points);
							/--*try {
								double p = Double.parseDouble(points);
								header.setPoints(Double.valueOf(p));
							} catch (NumberFormatException nfe) {
								log.info("Failed to parse " + points + " as a Double");
							}*--/
						}

					}

					if (percentCategoryColumns != null && percentCategoryColumns.length > i) {
						String percentCategory = percentCategoryColumns[i];

						if (percentCategory != null) {
							try {
								percentCategory = percentCategory.replace("%", "");
								double p = Double.parseDouble(percentCategory);
								header.setPercentCategory(Double.valueOf(p));
							} catch (NumberFormatException nfe) {
								log.info("Failed to parse " + percentCategory + " as a Double");
							}
						}
					}

					if (categoryId != null) {
						if (categoryName != null)
							header.setCategoryName(categoryName);
						header.setCategoryId(categoryId);
						
						GradeItem category = (GradeItem)ieInfo.getCategoryIdItemMap().get(categoryId);
						
						if (category != null) {
							category.addChild((GradeItem)header.getItem());
						}
						
					}

				}
			}
		}
	}*/
	
	public Upload parseImportGeneric(Gradebook2ComponentService service, 
			String gradebookUid, NewRawFile rawData) throws InvalidInputException, FatalException {
		String msgs = rawData.getMessages();
		boolean errorsFound = rawData.isErrorsFound(); 

		if (errorsFound) {
			Upload importFile = new UploadImpl();
			importFile.setErrors(true); 
			importFile.setNotes(msgs);
			return importFile; 
		}

		Gradebook gradebook = service.getGradebook(gradebookUid);
		Item gradebookItemModel = gradebook.getGradebookItemModel();

		List<UserDereference> userDereferences = service.findAllUserDereferences();
		Map<String, UserDereference> userDereferenceMap = new HashMap<String, UserDereference>();

		for (UserDereference dereference : userDereferences) {
			String exportUserId = service.getExportUserId(dereference); 
			userDereferenceMap.put(exportUserId, dereference);
		}

		NewImportExportInformation ieInfo = new NewImportExportInformation();
		
		UploadImpl importFile = new UploadImpl();

		if (rawData.isScantronFile())
		{
			importFile.setNotifyAssignmentName(!rawData.isNewAssignment()); 
			if (!rawData.isNewAssignment())
				importFile.addNotes("The scantron assignment entered has previously been imported.  We have changed the assignment name so that it will be imported uniquely. If you wanted to replace the old data, then please change it back.");
		}
		
		ieInfo.setGradebookItemModel(gradebookItemModel);
		
		ArrayList<Learner> importRows = new ArrayList<Learner>();

		Map<String, StructureRow> structureLineIndicatorMap = new HashMap<String, StructureRow>();
		Map<StructureRow, String[]> structureColumnsMap = new HashMap<StructureRow, String[]>();

		for (StructureRow structureRow : EnumSet.allOf(StructureRow.class)) {
			String lowercase = structureRow.getDisplayName().toLowerCase();
			structureLineIndicatorMap.put(lowercase, structureRow);
		}
		int structureStop = 0; 

		structureStop = readDataForStructureInformation(rawData, structureLineIndicatorMap, structureColumnsMap);
		if (structureStop != -1)
		{
			try {
				readInHeaderColumns(rawData, ieInfo, structureStop);
				
				processStructureInformation(ieInfo, structureColumnsMap);
				
				processHeaders(ieInfo, structureColumnsMap);
				
				readInGradeDataFromImportFile(rawData, ieInfo, userDereferenceMap, importRows, structureStop, service);

				GradeItem gradebookGradeItem = (GradeItem)ieInfo.getGradebookItemModel();
				service.decorateGradebook(gradebookGradeItem, null, null);
				importFile.setGradebookItemModel(gradebookGradeItem);
				importFile.setRows(importRows);
				importFile.setGradeType(gradebookItemModel.getGradeType());
				importFile.setCategoryType(gradebookItemModel.getCategoryType());
					
				if (ieInfo.isUserNotFound()) 
					importFile.addNotes("One or more users were not found based on the import identifier provided. This could indicate that the wrong import id is being used, or that the file is incorrectly formatted for import.");

				if (ieInfo.isInvalidScore()) 
					importFile.addNotes("One or more uploaded scores cannot be accepted because they are not in the correct format or the scores are higher than the maximum allowed for those items. These entries have been highlighted in red.");
				
				
			} catch (Exception e) {
				importFile.setErrors(true);
				importFile.setNotes(e.getMessage());
				importFile.setRows(null);
				log.warn(e);
				e.printStackTrace();
			}
		}
		else
		{
			importFile.setErrors(true); 
			importFile.setNotes("The file loaded does not contain the required header information to load."); 
		}
		
		service.postEvent("gradebook2.import", String.valueOf(gradebook.getGradebookId()));

		return importFile;
	}

	private Item getActiveModel(GradeItem itemModel) {

		if (itemModel.isActive())
			return itemModel;

		for (Item m : itemModel.getChildren()) {
			Item child = (Item)m;
			if (child.isActive())
				return child;

			Item c2 = getActiveModel((GradeItem)child);

			if (c2 != null)
				return c2;
		}

		return null;
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

	/*private class CategoryRange {

		private Item categoryModel;
		private int startColumn;
		private int endColumn;

		public CategoryRange(Item categoryModel) {
			this.categoryModel = categoryModel;
			this.startColumn = -1;
			this.endColumn = -1;
		}

		public int getStartColumn() {
			return startColumn;
		}

		public void setStartColumn(int startColumn) {
			this.startColumn = startColumn;
		}

		public int getEndColumn() {
			return endColumn;
		}

		public void setEndColumn(int endColumn) {
			this.endColumn = endColumn;
		}

	}*/

}

class NewImportExportInformation 
{
	Set<Integer> ignoreColumns;
	int idFieldIndex;
	int nameFieldIndex;
	int courseGradeFieldIndex;
	boolean foundStructure; 
	boolean foundHeader; 
	Map<String, String> categoryIdNameMap;
	Map<String, GradeItem> categoryIdItemMap;
	NewImportHeader[] headers;
	String[] categoryRangeColumns;
	
	boolean isInvalidScore;
	boolean isUserNotFound;
	
	List<Integer> activeHeaderIndexes;

	Item gradebookItemModel;
	
	
	public NewImportExportInformation() 
	{
		ignoreColumns = new HashSet<Integer>();
		idFieldIndex = -1;
		nameFieldIndex = -1;
		courseGradeFieldIndex = -1;
		categoryIdNameMap = new HashMap<String, String>();
		categoryIdItemMap = new HashMap<String, GradeItem>();

		activeHeaderIndexes = new LinkedList<Integer>();
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

	public String[] getCategoryRangeColumns() {
		return categoryRangeColumns;
	}

	public void setCategoryRangeColumns(String[] categoryRangeColumns) {
		this.categoryRangeColumns = categoryRangeColumns;
	}

	public NewImportHeader[] findActiveHeaders() {
		NewImportHeader[] activeHeaders = new NewImportHeader[activeHeaderIndexes.size()];
		
		int i=0;
		for (Integer index : activeHeaderIndexes) {
			activeHeaders[i] = headers[index.intValue()];
			i++;
		}
		
		return activeHeaders;
	}

	public NewImportHeader[] getHeaders() {
		return headers;
	}

	public void setHeaders(NewImportHeader[] headers) {
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

}


class NewRawFile 
{
	private String fileType; 
	private String messages; 
	private boolean errorsFound; 
	private boolean newAssignment; 
	private boolean scantronFile;


	public boolean isNewAssignment() {
		return newAssignment;
	}

	public void setNewAssignment(boolean newAssignment) {
		this.newAssignment = newAssignment;
	}

	public boolean isScantronFile() {
		return scantronFile;
	}

	public void setScantronFile(boolean scantronFile) {
		this.scantronFile = scantronFile;
	}

	private static final Log log = LogFactory.getLog(RawFile.class);

	private List<String[]> allRows; 
	private int curRow; 

	public List<String[]> getAllRows() {
		return allRows;
	}

	public void setAllRows(List<String[]> allRows) {
		this.allRows = allRows;
	}

	public NewRawFile()
	{
		this.errorsFound = false; 
		this.newAssignment = false; 
		this.scantronFile = false; 
		allRows = new ArrayList<String[]>(); 
	}
	public void goToRow(int row)
	{
		if (row > 0 && row < allRows.size())
		{
			curRow = row; 
		}
		else
		{
			curRow = 0; 
		}
	}

	public int getCurrentLineNumber()
	{
		return curRow; 
	}

	public void close() 
	{
		this.allRows = null; 
		this.curRow = -2; 
	}
	public void startReading() 
	{
		this.curRow = -1; 
	}

	public String[] readNext() 
	{
		if (curRow == -2)
		{
			return null; 
		}

		this.curRow++; 
		if (curRow >= allRows.size())
		{
			return null; 
		}
		else
		{
			return allRows.get(curRow); 
		}

	}

	public void addRow(String[] rowData)
	{
		if (allRows != null)
		{
			allRows.add(rowData);
		}
	}

	public String[] getRow(int idx)
	{
		if (allRows != null)
		{
			if (idx < allRows.size())
			{
				return allRows.get(idx);
			}
			else
			{
				return null; 
			}
		}
		else
		{
			return null; 
		}
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getMessages() {
		return messages;
	}

	public void setMessages(String messages) {
		this.messages = messages;
	}

	public boolean isErrorsFound() {
		return errorsFound;
	}

	public void setErrorsFound(boolean errorsFound) {
		this.errorsFound = errorsFound;
	}

}
