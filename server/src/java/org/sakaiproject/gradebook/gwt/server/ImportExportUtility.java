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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.gxt.ItemModelProcessor;
import org.sakaiproject.gradebook.gwt.client.gxt.upload.ImportFile;
import org.sakaiproject.gradebook.gwt.client.gxt.upload.ImportHeader;
import org.sakaiproject.gradebook.gwt.client.gxt.upload.ImportRow;
import org.sakaiproject.gradebook.gwt.client.gxt.upload.ImportHeader.Field;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2Service;
import org.sakaiproject.gradebook.gwt.sakai.GradebookImportException;
import org.sakaiproject.gradebook.gwt.sakai.GradebookToolService;
import org.sakaiproject.gradebook.gwt.sakai.model.UserDereference;
import org.sakaiproject.tool.gradebook.Assignment;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class ImportExportUtility {

	private static final Log log = LogFactory.getLog(ImportExportUtility.class);
	private static final String SCANTRON_HEADER_STUDENT_ID = "student_id"; 
	private static final String SCANTRON_HEADER_SCORE = "score"; 


	public static enum Delimiter {
		TAB, COMMA, SPACE, COLON
	};

	private static enum StructureRow {
		GRADEBOOK("Gradebook:"), CATEGORY("Category:"), PERCENT_GRADE("% Grade:"), POINTS("Points:"), 
		PERCENT_CATEGORY("% Category:"), DROP_LOWEST("Drop Lowest:"), EQUAL_WEIGHT("Equal Weight Items:");

		private String displayName;

		StructureRow(String displayName) {
			this.displayName = displayName;
		}

		public String getDisplayName() {
			return displayName;
		}
	};

	public static void exportGradebook(Gradebook2Service service, String gradebookUid, 
			final boolean includeStructure, final boolean includeComments, PrintWriter writer, HttpServletResponse response) 
	throws FatalException {

		GradebookModel gradebook = service.getGradebook(gradebookUid);
		ItemModel gradebookItemModel = gradebook.getGradebookItemModel();

		StringBuilder filename = new StringBuilder();

		if (gradebook.getName() == null)
			filename.append("gradebook");
		else {
			String name = gradebook.getName();
			name = name.replaceAll("\\s", "");

			filename.append(name);
		}

		filename.append(".csv");

		if (response != null) {
			response.setContentType("application/ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=" + filename.toString());
		}

		CSVWriter csvWriter = new CSVWriter(writer);

		Long gradebookId = gradebook.getGradebookId();
		final List<String> headerIds = new ArrayList<String>();

		final List<String> headerColumns = new LinkedList<String>();

		headerColumns.add("Student Id");
		headerColumns.add("Student Name");

		if (includeStructure) {
			CategoryType categoryType = gradebookItemModel.getCategoryType();
			String categoryTypeText = categoryType.getDisplayName();
			String gradeTypeText = gradebookItemModel.getGradeType().getDisplayName();

			// First, we need to add a row for basic gradebook info
			String[] gradebookInfoRow = { "", StructureRow.GRADEBOOK.getDisplayName(), gradebook.getName(), categoryTypeText, gradeTypeText};
			csvWriter.writeNext(gradebookInfoRow);

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
				public void doCategory(ItemModel itemModel, int childIndex) {
					StringBuilder categoryName = new StringBuilder().append(itemModel.getName());

					if (DataTypeConversionUtil.checkBoolean(itemModel.getExtraCredit())) {
						categoryName.append(AppConstants.EXTRA_CREDIT_INDICATOR);
					}

					if (!DataTypeConversionUtil.checkBoolean(itemModel.getIncluded())) {
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


					if (itemModel.getChildCount() == 0) {
						headerIds.add("");
						headerColumns.add("");
						pointsRow.add("");
						percentCategoryRow.add("");
					}

				}

				@Override
				public void doItem(ItemModel itemModel, int childIndex) {
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

					if (DataTypeConversionUtil.checkBoolean(itemModel.getExtraCredit())) {
						text.append(AppConstants.EXTRA_CREDIT_INDICATOR);
					}

					if (!DataTypeConversionUtil.checkBoolean(itemModel.getIncluded())) {
						text.append(AppConstants.UNINCLUDED_INDICATOR);
					}

					if (!includeStructure) {
						String points = DecimalFormat.getInstance().format(itemModel.getPoints());
						text.append(" [").append(points).append("]");
					}

					headerIds.add(itemModel.getIdentifier());
					headerColumns.add(text.toString());

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
					csvWriter.writeNext(pointsRow.toArray(new String[pointsRow.size()]));
					break;
				case SIMPLE_CATEGORIES:
					csvWriter.writeNext(categoriesRow.toArray(new String[categoriesRow.size()]));
					csvWriter.writeNext(dropLowestRow.toArray(new String[dropLowestRow.size()]));
					csvWriter.writeNext(pointsRow.toArray(new String[pointsRow.size()]));
					break;
				case WEIGHTED_CATEGORIES:
					csvWriter.writeNext(categoriesRow.toArray(new String[categoriesRow.size()]));
					csvWriter.writeNext(percentageGradeRow.toArray(new String[percentageGradeRow.size()]));
					csvWriter.writeNext(dropLowestRow.toArray(new String[dropLowestRow.size()]));
					csvWriter.writeNext(equalWeightRow.toArray(new String[equalWeightRow.size()]));
					csvWriter.writeNext(pointsRow.toArray(new String[pointsRow.size()]));
					csvWriter.writeNext(percentCategoryRow.toArray(new String[percentCategoryRow.size()]));
					break;
			}

			String[] blankLine = { "" };
			csvWriter.writeNext(blankLine);
		} else {

			ItemModelProcessor processor = new ItemModelProcessor(gradebookItemModel) {

				@Override
				public void doItem(ItemModel itemModel) {
					StringBuilder text = new StringBuilder();
					text.append(itemModel.getName());

					if (DataTypeConversionUtil.checkBoolean(itemModel.getExtraCredit())) {
						text.append(AppConstants.EXTRA_CREDIT_INDICATOR);
					}

					if (!DataTypeConversionUtil.checkBoolean(itemModel.getIncluded())) {
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

		headerColumns.add("Course Grade");

		csvWriter.writeNext(headerColumns.toArray(new String[headerColumns.size()]));

		PagingLoadResult<StudentModel> result = service.getStudentRows(gradebookUid, gradebookId, null, Boolean.TRUE);

		List<StudentModel> rows = result.getData();

		boolean isPercentages = gradebook.getGradebookItemModel().getGradeType() == GradeType.PERCENTAGES;

		if (headerIds != null) {

			if (rows != null) {
				for (StudentModel row : rows) {
					List<String> dataColumns = new LinkedList<String>();

					dataColumns.add(row.getExportUserId());
					dataColumns.add(row.getLastNameFirst());

					for (int column = 0; column < headerIds.size(); column++) {
						if (headerIds.get(column) != null) {
							Object value = row.get(headerIds.get(column));

							if (value != null)
								dataColumns.add(String.valueOf(value));
							else
								dataColumns.add("");

						} else {
							dataColumns.add("");
						}

						if (includeComments) {
							String commentId = new StringBuilder()
							.append(headerIds.get(column)).append(StudentModel.COMMENT_TEXT_FLAG).toString();

							Object comment = row.get(commentId);

							if (comment == null)
								comment = "";

							dataColumns.add(String.valueOf(comment));
						}
					}

					dataColumns.add(row.getStudentGrade());

					csvWriter.writeNext(dataColumns.toArray(new String[dataColumns.size()]));
				}
			} 

		}

		try {
			csvWriter.close();
		} catch (IOException ioe) {
			log.error("Caught ioexception: ", ioe);
		}
	}


	private static HSSFWorkbook readPoiSpreadsheet(BufferedInputStream is) throws IOException 
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


	private static boolean checkForCurrentAssignmentInGradebook(String fileName, Gradebook2Service service, GradebookToolService gbToolService, String gradebookUid)
	{
		GradebookModel gm = service.getGradebook(gradebookUid); 
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
	private static String getUniqueFileNameForFileName(String fileName,
			Gradebook2Service service, GradebookToolService gbToolService, String gradebookUid) throws GradebookImportException 
			{
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
	public static ImportFile parseImportXLS(Gradebook2Service service, 
			String gradebookUid, InputStream is, String fileName, GradebookToolService gbToolService) throws InvalidInputException, FatalException, IOException {
		log.debug("parseImportXLS() called"); 

		String realFileName; 
		boolean isOriginalName; 
		try {
			realFileName = getUniqueFileNameForFileName(fileName, service, gbToolService, gradebookUid);
		} catch (GradebookImportException e) {
			ImportFile importFile = new ImportFile(); 
			importFile.setHasErrors(true); 
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



	private static ImportFile handleJExcelAPISpreadSheet(BufferedInputStream is,
			Gradebook2Service service, String gradebookUid, String fileName, boolean isNewAssignmentByFileName) throws InvalidInputException, FatalException, IOException {
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

	private static ImportFile handleNormalXLSSheetForJExcelApi(Sheet s,
			Gradebook2Service service, String gradebookUid) throws InvalidInputException, FatalException {
		RawFile raw = new RawFile(); 
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

	private static ImportFile handleScantronSheetForJExcelApi(Sheet s,
			Gradebook2Service service, String gradebookUid, String fileName, boolean isNewAssignmentByFileName) throws InvalidInputException, FatalException 
			{
		StringBuilder err = new StringBuilder("Scantron File with errors"); 
		RawFile raw = new RawFile(); 
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

	private static String[] getScantronHeaderLine(String fileName)
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
	private static boolean isScantronSheetForJExcelApi(Sheet s) {
		Cell studentIdHeader = s.findCell(SCANTRON_HEADER_STUDENT_ID);
		Cell scoreHeader = s.findCell("score");

		return (studentIdHeader != null && scoreHeader != null); 
	}

	private static ImportFile handlePoiSpreadSheet(HSSFWorkbook inspread, Gradebook2Service service, String gradebookUid, String fileName, boolean isNewAssignmentByFileName) throws InvalidInputException, FatalException
	{
		log.debug("handlePoiSpreadSheet() called"); 
		// FIXME - need to do multiple sheets, and structure
		int numSheets = inspread.getNumberOfSheets(); 
		String msg = ""; 
		if (numSheets > 0)
		{
			HSSFSheet cur = inspread.getSheetAt(0);
			RawFile ret; 
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
			RawFile d = new RawFile(); 
			d.setMessages("The XLS spreadsheet entered does not contain any valid sheets.  Please correct and try again.");
			d.setErrorsFound(true); 
			return parseImportGeneric(service, gradebookUid, d);

		}
	}

	private static RawFile processNormalXls(HSSFSheet s) {
		log.debug("processNormalXls() called");
		RawFile data = new RawFile(); 
		Iterator<HSSFRow> rowIter = s.rowIterator(); 
		boolean headerFound = false;
		int id_col = -1; 
		int numCols = -1;

		while (rowIter.hasNext())
		{

			HSSFRow curRow = rowIter.next();  
			if (!headerFound)
			{
				id_col = readHeaderRow(curRow); 
				headerFound = true; 
				log.debug("Header Row # is " + id_col);
				numCols = curRow.getPhysicalNumberOfCells();
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

	private static int readHeaderRow(HSSFRow curRow) {
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

	private static RawFile processScantronXls(HSSFSheet s, String fileName) {
		RawFile data = new RawFile(); 
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
				boolean problemFound = false; 
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

					//idStr = String.format("%.0f", id.getRichStringCellValue().getString());
					// FIXME - need to decide if this is OK for everyone, not everyone will have an ID as a 
					idStr = new Integer(id.getRichStringCellValue().getString()).toString();
					if (score != null)
					{
						if (score.getCellType() == HSSFCell.CELL_TYPE_STRING)
						{
							scoreStr = score.getRichStringCellValue().getString();
						}
						else if (score.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
						{
							scoreStr = Double.toString(score.getNumericCellValue());
						}
						else
						{
							scoreStr = ""; 
						}
					}
					else
					{
						scoreStr = ""; 
					}
					String[] ent = new String[2];
					ent[0] = idStr; 
					ent[1] = scoreStr;

					data.addRow(ent); 
				}
			}
		}
		return data; 

	}

	// POI doesn't provide the findCell method that jexcelapi does, so we'll simulate it..  We return the first cell we find with the text in searchText
	// if we can't find it, we return null. 
	// 

	public static HSSFCell findCellWithTextonSheetForPoi(HSSFSheet s, String searchText)
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

	private static String getStringValueFromCell(HSSFCell c)
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
	
	
	private static boolean isScantronSheetFromPoi(HSSFSheet s) {
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

	public static ImportFile parseImportCSV(Gradebook2Service service, 
			String gradebookUid, Reader reader) throws InvalidInputException, FatalException 
			{

		RawFile rawData = new RawFile(); 
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
		}

		rawData.setFileType("CSV file"); 
		return parseImportGeneric(service, gradebookUid, rawData);
			}	

	private static int readDataForStructureInformation(RawFile rawData, Map<String, StructureRow> structureLineIndicatorMap, Map<StructureRow, String[]> structureColumnsMap, Set<String> headerLineIndicatorSet) 
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
			log.debug("SI: firstColumnLowerCase=" + firstColumnLowerCase);
			if (!headerLineIndicatorSet.contains(firstColumnLowerCase)) {
				log.debug("Processed non header line"); 
				// Since it's not processed yet, check to see if the current row has any useful info
				for (int i=0;i<columns.length;i++) {
					if (columns[i] != null && !columns[i].equals("")) 
					{

						String columnLowerCase = columns[i].trim().toLowerCase();
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



	public static void readInHeaderInfo(RawFile rawData,
			ImportExportInformation importInfo, GradebookModel gradebook, NumberFormat decimalFormat, 
			int startRow,String[] pointsColumns, String[] categoryColumns) {
		log.debug("XXX: readInHeaderInfo() called");
		int rows = 0; 
		String[] headerColumns = null;
		boolean headerFound = false; 


		headerColumns = rawData.getRow(startRow);

		String categoryName = null;

		for (int i = 0; i < headerColumns.length; i++) {
			String text = headerColumns[i];

			ImportHeader header = null;
			log.debug("Column[" + i + "] = " + text); 
			// Check for name field
			if (text == null || text.trim().equals("")) {
				importInfo.getIgnoreColumns().add(Integer.valueOf(i));
				log.debug("HI: Ignoring column " + i);
				continue;
			} else if (text.equalsIgnoreCase("student name")
					|| text.equalsIgnoreCase("name")
					|| text.equalsIgnoreCase("learner")) {
				log.debug("HI: Column " + i + " is student name");
				header = new ImportHeader(Field.NAME, text);
				header.setId("NAME");
				importInfo.setNameFieldIndex(i);
				// FIXME - Should this be like this? 
			} else if (text.equalsIgnoreCase("student id")
					|| text.equalsIgnoreCase("identifier")
					|| text.equalsIgnoreCase("userId")
					|| text.equalsIgnoreCase("learnerid")) {
				log.debug("HI: Column " + i + " is student id");
				header = new ImportHeader(Field.ID, text);
				header.setId("ID");
				importInfo.setIdFieldIndex(i);
			} else if (text.equalsIgnoreCase("course grade")) {
				// Do nothing
				log.debug("HI: Column " + i + " is course grade");
				importInfo.setCourseGradeFieldIndex(i);
			} else {

				log.debug("HI: Column " + i + " is not known, probably points");
				String name = null;
				String points = null;
				boolean isExtraCredit = text
				.contains(AppConstants.EXTRA_CREDIT_INDICATOR);

				if (isExtraCredit) {
					log.debug("X: Column " + i + " has extra credit");
					text = text
					.replace(AppConstants.EXTRA_CREDIT_INDICATOR, "");
				}

				boolean isUnincluded = text
				.contains(AppConstants.UNINCLUDED_INDICATOR);

				if (isUnincluded) {
					log.debug("X: Column " + i + " is unincluded");
					text = text.replace(AppConstants.UNINCLUDED_INDICATOR, "");
				}

				name = text;

				boolean isComment = text
					.startsWith(AppConstants.COMMENTS_INDICATOR);

				if (isComment) {
					name = text.substring(AppConstants.COMMENTS_INDICATOR.length());
				}

				int startParenthesis = text.indexOf("[");
				int endParenthesis = text.indexOf("pts]");

				if (endParenthesis == -1)
					endParenthesis = text.indexOf("]");

				if (startParenthesis != -1 && endParenthesis != -1
						&& endParenthesis > startParenthesis + 1) {
					log.debug("X: Column " + i + " has pts indicated");
					points = text.substring(startParenthesis + 1, endParenthesis);
					log.debug("X: Column " + i + " points are " + points);
					name = text.substring(0, startParenthesis);
					log.debug("X: Column " + i + " name is " + points);

					if (name != null)
						name = name.trim();
				}

				if (name != null) {

					if (categoryColumns != null && categoryColumns.length > i) {
						if (categoryColumns[i] != null && categoryColumns[i].trim().length() > 0) {
							categoryName = categoryColumns[i];
							if (categoryName != null) {
								categoryName = categoryName.trim();
							
								int indexOfExtraCreditIndicator = categoryName.indexOf(AppConstants.EXTRA_CREDIT_INDICATOR);
								
								if (indexOfExtraCreditIndicator != -1) 
									categoryName = categoryName.substring(0, indexOfExtraCreditIndicator); 
		
								int indexOfUnincludedIndicator = categoryName.indexOf(AppConstants.UNINCLUDED_INDICATOR);
								
								if (indexOfUnincludedIndicator != -1) 
									categoryName = categoryName.substring(0, indexOfUnincludedIndicator); 
	
							}
						}
					}


					ItemModel model = findModelByName(name, categoryName, gradebook
							.getGradebookItemModel());

					if (isComment) {
						header = new ImportHeader(Field.COMMENT, text);
						if (model != null) {
							header.setAssignmentId(model.getIdentifier());
							header.setId(new StringBuilder().append(model.getIdentifier()).append(StudentModel.COMMENT_TEXT_FLAG).toString());
							header.setCategoryName(model.getCategoryName());
							header.setCategoryId(String.valueOf(model.getCategoryId()));
						} else {
							String newId = new StringBuilder().append("NEW:").append(i - 1).toString();
							header.setAssignmentId(newId);
							header.setId(new StringBuilder().append(newId).append(StudentModel.COMMENT_TEXT_FLAG).toString());
						}
						
					} else {

						StringBuffer value = new StringBuffer(name);

						if (points == null && pointsColumns != null && pointsColumns.length > i)
							points = pointsColumns[i];

						if (points == null && model != null)
							points = decimalFormat.format(model.getPoints());

						if (points != null && points.length() > 0)
							value.append(" [").append(points).append("]");

						header = new ImportHeader(Field.ITEM, value.toString());

						if (model != null) {
							header.setId(model.getIdentifier());
							header.setCategoryName(model.getCategoryName());
							header.setCategoryId(String.valueOf(model
									.getCategoryId()));

							importInfo.getCategoryIdNameMap().put(
									model.getCategoryId(), model.getCategoryName());
						} else {
							header.setId(new StringBuilder().append("NEW:").append(
									i).toString());
							if (categoryName == null)
								header.setCategoryName("Unassigned");
							else
								header.setCategoryName(categoryName);
						}
						header.setHeaderName(name);
						header.setExtraCredit(Boolean.valueOf(isExtraCredit));
						header.setUnincluded(Boolean.valueOf(isUnincluded));
						if (points != null) {
							if (points.equals("%"))
								header.setPercentage(true);
							else {
								try {
									header.setPoints(Double.valueOf(Double
											.parseDouble(points)));
								} catch (NumberFormatException nfe) {
									log.error("Could not parse points "
											+ points);
								}
							}
						}
					}
				}

			}

			StringBuilder key = new StringBuilder();

			if (categoryName != null) 
				key.append(categoryName).append("::");
			key.append(text);

			importInfo.getHeaders().add(header);
			importInfo.getHeaderMap().put(key.toString(), header);
		}
		importInfo.setHeaderColumns(headerColumns);
		log.debug("XXX: readInHeaderInfo() finished");
	}

	public static void readInGradeDataFromImportFile(RawFile rawData, ImportExportInformation ieInfo, Map<String, UserDereference> userDereferenceMap, List<ImportRow> importRows, int startRow) 
	{
		String[] columns; 
		rawData.goToRow(startRow); 
		while ((columns = rawData.readNext()) != null) {

			ImportRow row = new ImportRow();
			// First, based on whichever column is the id column, populate it
			if (columns.length > ieInfo.getIdFieldIndex() &&  ieInfo.getIdFieldIndex() != -1) {
				String userImportId = columns[ieInfo.getIdFieldIndex()];
				row.setUserImportId(userImportId);

				UserDereference userDereference = userDereferenceMap.get(userImportId);

				if (userDereference == null)
					row.setUserNotFound(true);
				else {
					row.setUserNotFound(false);
					row.setUserUid(userDereference.getUserUid());
					row.setUserDisplayName(userDereference.getLastNameFirst());
				}
			}

			if (ieInfo.getCourseGradeFieldIndex() == -1)
				row.setColumns(columns);
			else {
				String[] strippedColumns = new String[columns.length - 1];
				int n = 0;
				for (int i=0;i<columns.length;i++) {
					Integer columnNumber = Integer.valueOf(i);

					if (ieInfo.getIgnoreColumns().contains(columnNumber))
						continue;

					if (ieInfo.getCourseGradeFieldIndex() == i)
						continue;
					if (n < strippedColumns.length)
						strippedColumns[n] = columns[i];
					n++;
				}
				row.setColumns(strippedColumns);
			}
			importRows.add(row);
		}	
	}

	public static void processStructureInformation(ImportExportInformation ieInfo, Map<StructureRow, String[]> structureColumnsMap, GradebookModel gradebook, Gradebook2Service service) throws InvalidInputException
	{
		// Now, modify gradebook structure according to the data stored
		String[] gradebookColumns = structureColumnsMap.get(StructureRow.GRADEBOOK);
		ItemModel gradebookItemModel = gradebook.getGradebookItemModel();

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
				CategoryType cType = gradebookItemModel.getCategoryType();

				if (CategoryType.NO_CATEGORIES.getDisplayName().equals(categoryType))
					cType = CategoryType.NO_CATEGORIES;
				else if (CategoryType.SIMPLE_CATEGORIES.getDisplayName().equals(categoryType))
					cType = CategoryType.SIMPLE_CATEGORIES;
				else if (CategoryType.WEIGHTED_CATEGORIES.getDisplayName().equals(categoryType))
					cType = CategoryType.WEIGHTED_CATEGORIES;

				// If the upload changes the status of having categories, then update this local var
				ieInfo.setHasCategories( cType != CategoryType.NO_CATEGORIES);

				gradebookItemModel.setCategoryType(cType);
			}
			if (gradeType != null) {
				GradeType gType = gradebookItemModel.getGradeType();

				if (GradeType.PERCENTAGES.getDisplayName().equals(gradeType))
					gType = GradeType.PERCENTAGES;
				else if (GradeType.POINTS.getDisplayName().equals(gradeType))
					gType = GradeType.POINTS;

				gradebookItemModel.setGradeType(gType);
			}

			gradebookItemModel = service.updateItemModel(gradebookItemModel);
		}

		String[] categoryColumns = structureColumnsMap.get(StructureRow.CATEGORY);
		String[] percentGradeColumns = structureColumnsMap.get(StructureRow.PERCENT_GRADE);
		String[] dropLowestColumns = structureColumnsMap.get(StructureRow.DROP_LOWEST);
		String[] equalWeightColumns = structureColumnsMap.get(StructureRow.EQUAL_WEIGHT);
		String[] pointsColumns = structureColumnsMap.get(StructureRow.POINTS);
		String[] percentCategoryColumns = structureColumnsMap.get(StructureRow.PERCENT_CATEGORY);

		String[] categoryRangeColumns = new String[(ieInfo.getHeaderColumns() != null ? ieInfo.getHeaderColumns().length : 0)];
		Map<String, ItemModel> categoryMap = new HashMap<String, ItemModel>();

		if (categoryColumns != null) {

			for (ItemModel child : gradebookItemModel.getChildren()) {
				if (child.getItemType() != null && child.getItemType() == Type.CATEGORY)
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

				boolean isModelNew = false;
				ItemModel categoryModel = null;
				// In this case, we have a new category that needs to be added to the gradebook
				String categoryName = categoryColumns[i];
				boolean isExtraCredit = categoryName.contains(AppConstants.EXTRA_CREDIT_INDICATOR);

				if (isExtraCredit)
					categoryName = categoryName.replace(AppConstants.EXTRA_CREDIT_INDICATOR, "");

				boolean isUnincluded = categoryName.contains(AppConstants.UNINCLUDED_INDICATOR);

				if (isUnincluded)
					categoryName = categoryName.replace(AppConstants.UNINCLUDED_INDICATOR, "");

				boolean isDefaultCategory = categoryName.equalsIgnoreCase(AppConstants.DEFAULT_CATEGORY_NAME);

				if (!categoryMap.containsKey(categoryName)) {

					categoryModel = new ItemModel();
					categoryModel.setItemType(Type.CATEGORY);
					categoryModel.setName(categoryName);
					categoryModel.setIncluded(Boolean.valueOf(!isUnincluded));
					categoryModel.setExtraCredit(Boolean.valueOf(isExtraCredit));
					isModelNew = true;
				} else {
					// Otherwise, we may still want to update scores
					categoryModel = categoryMap.get(categoryName);
				}

				boolean isModelUpdated = false;
				if (categoryModel != null) {
					if (percentGradeColumns != null && percentGradeColumns.length > i) {
						String percentGrade = percentGradeColumns[i];

						if (percentGrade != null) {
							try {
								percentGrade = percentGrade.replace("%", "");
								double pG = Double.parseDouble(percentGrade);
								categoryModel.setPercentCourseGrade(Double.valueOf(pG));								
								isModelUpdated = true;
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
								isModelUpdated = true;
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
								isModelUpdated = true;
							} catch (NumberFormatException nfe) {
								log.info("Failed to parse " + equalWeight + " as an Boolean");
							}
						}
					}

					ItemModel result = null;
					if (!isDefaultCategory) {
						if (isModelNew) {
							result = service.createItem(gradebook.getGradebookUid(), gradebook.getGradebookId(), categoryModel, false);
						} else if (isModelUpdated) 
							result = service.updateItemModel(categoryModel);
					} else {
						currentCategoryId = "-1";
						if (categoryRangeColumns.length > i)
							categoryRangeColumns[i] = "-1";
					}

					if (result != null) {
						ItemModel activeModel = getActiveModel(result);

						if (activeModel != null) {
							currentCategoryId = activeModel.getIdentifier();
							if (categoryRangeColumns.length > i)
								categoryRangeColumns[i] = currentCategoryId;

							ieInfo.getCategoryIdNameMap().put(
									activeModel.getCategoryId(), activeModel.getName());
						}
					}

				}
			}
		}

		if (ieInfo.getHeaderColumns() != null) {
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
						categoryName = ieInfo.getCategoryIdNameMap().get(Long.valueOf(categoryId));
						if (categoryName != null) 
							key.append(categoryName).append("::");
					}
				}

				key.append(name);

				ImportHeader header = ieInfo.getHeaderMap().get(key.toString());

				if (header != null) {
					
					if (header.getField() == null || header.getField().equals(Field.COMMENT.name()))
						continue;
					
					if (isExtraCredit)
						header.setExtraCredit(Boolean.valueOf(isExtraCredit));

					if (pointsColumns != null && pointsColumns.length > i) {
						String points = pointsColumns[i];

						if (points != null) {
							try {
								double p = Double.parseDouble(points);
								header.setPoints(Double.valueOf(p));
							} catch (NumberFormatException nfe) {
								log.info("Failed to parse " + points + " as a Double");
							}
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
					}

				}
			}
		}
	}

	public static ImportFile parseImportGeneric(Gradebook2Service service, 
			String gradebookUid, RawFile rawData) throws InvalidInputException, FatalException {
		String msgs = rawData.getMessages();
		boolean errorsFound = rawData.isErrorsFound(); 

		if (errorsFound) 
		{
			ImportFile importFile = new ImportFile();
			importFile.setHasErrors(true); 
			importFile.setNotes(msgs);
			return importFile; 
		}

		GradebookModel gradebook = service.getGradebook(gradebookUid);
		ItemModel gradebookItemModel = gradebook.getGradebookItemModel();
		CategoryType categoryType = gradebookItemModel.getCategoryType();
		boolean hasWeights = categoryType == CategoryType.WEIGHTED_CATEGORIES;
		boolean hasCategories = categoryType != CategoryType.NO_CATEGORIES;
		boolean isLetterGrading = gradebookItemModel.getGradeType() == GradeType.LETTERS;
		
		List<UserDereference> userDereferences = service.findAllUserDereferences();
		Map<String, UserDereference> userDereferenceMap = new HashMap<String, UserDereference>();

		for (UserDereference dereference : userDereferences) {
			String exportUserId = service.getExportUserId(dereference); 
			userDereferenceMap.put(exportUserId, dereference);
		}

		ImportFile importFile = new ImportFile();

		if (rawData.isScantronFile())
		{
			importFile.setNotifyAssignmentName(!rawData.isNewAssignment()); 
		}


		ImportExportInformation ieInfo = new ImportExportInformation();
		ieInfo.setHasWeights(hasWeights);
		ieInfo.setHasCategories(hasCategories);
		ieInfo.setLetterGrading(isLetterGrading);
		ArrayList<ImportRow> importRows = new ArrayList<ImportRow>();

		DecimalFormat decimalFormat = new DecimalFormat();

		Map<String, StructureRow> structureLineIndicatorMap = new HashMap<String, StructureRow>();
		Map<StructureRow, String[]> structureColumnsMap = new HashMap<StructureRow, String[]>();

		// FIXME - Need to decide whether this should be institutional based.  
		// FIXME - does this need i18n ? 
		String[] headerLineIndicators = { "student id", "student name", "learner", "id", "identifier", "userid", "learnerid" };
		Set<String> headerLineIndicatorSet = new HashSet<String>();

		for (int i=0;i<headerLineIndicators.length;i++) {
			headerLineIndicatorSet.add(headerLineIndicators[i]);
		}

		for (StructureRow structureRow : EnumSet.allOf(StructureRow.class)) {
			String lowercase = structureRow.getDisplayName().toLowerCase();
			structureLineIndicatorMap.put(lowercase, structureRow);
		}
		int structureStop = 0; 

		structureStop = readDataForStructureInformation(rawData, structureLineIndicatorMap, structureColumnsMap, headerLineIndicatorSet);
		if (structureStop != -1)
		{
			try {
				String[] pointsColumns = structureColumnsMap.get(StructureRow.POINTS);
				String[] categoryColumns = structureColumnsMap.get(StructureRow.CATEGORY);
				readInHeaderInfo(rawData, ieInfo, gradebook, decimalFormat, structureStop, pointsColumns, categoryColumns);
	
				readInGradeDataFromImportFile(rawData, ieInfo, userDereferenceMap, importRows, structureStop);
				importFile.setItems(ieInfo.getHeaders());
				importFile.setRows(importRows);
				processStructureInformation(ieInfo, structureColumnsMap, gradebook, service);
				importFile.setHasCategories(Boolean.valueOf(ieInfo.hasCategories()));
				importFile.setHasWeights(Boolean.valueOf(ieInfo.hasWeights));
				importFile.setLetterGrading(Boolean.valueOf(ieInfo.isLetterGrading()));
				importFile.setPointsMode(Boolean.valueOf(ieInfo.isPointsMode()));
			} catch (Exception e) {
				importFile.setHasErrors(true);
				importFile.setNotes(e.getMessage());
				importFile.setItems(null);
				importFile.setRows(null);
			}
		}
		else
		{
			importFile.setHasErrors(true); 
			importFile.setNotes("The file loaded does not contain the required header information to load."); 
		}

		return importFile;
	}

	private static ItemModel getActiveModel(ItemModel itemModel) {

		if (itemModel.isActive())
			return itemModel;

		for (ItemModel child : itemModel.getChildren()) {
			if (child.isActive())
				return child;

			ItemModel c2 = getActiveModel(child);

			if (c2 != null)
				return c2;
		}


		return null;
	}

	private static ItemModel findModelByName(final String name, final String categoryName, ItemModel root) {

		ItemModelProcessor processor = new ItemModelProcessor(root) {

			@Override
			public void doItem(ItemModel itemModel) {

				String itemName = itemModel.getName();

				if (itemName != null) {
					String trimmed = itemName.trim();

					if (trimmed.equals(name) &&
							(categoryName == null || (itemModel.getParent() != null &&
									itemModel.getParent().getName().trim().equals(categoryName)))) {
						this.result = itemModel;
					}
				}

			}

		};


		processor.process();

		return processor.getResult();
	}


	private class CategoryRange {

		private ItemModel categoryModel;
		private int startColumn;
		private int endColumn;

		public CategoryRange(ItemModel categoryModel) {
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

	}


}

class ImportExportInformation 
{
	Set<Integer> ignoreColumns;
	int idFieldIndex;
	int nameFieldIndex;
	int courseGradeFieldIndex;
	boolean foundStructure; 
	boolean foundHeader; 
	ArrayList<ImportHeader> headers;
	Map<String, ImportHeader> headerMap;
	Map<Long, String> categoryIdNameMap;
	String[] headerColumns;
	boolean hasCategories;
	boolean hasWeights;
	boolean isLetterGrading;
	boolean isPointsMode;

	public ImportExportInformation() 
	{
		ignoreColumns = new HashSet<Integer>();
		idFieldIndex = -1;
		nameFieldIndex = -1;
		courseGradeFieldIndex = -1;
		headers = new ArrayList<ImportHeader>();
		headerMap = new HashMap<String, ImportHeader>();
		categoryIdNameMap = new HashMap<Long, String>();
		headerColumns = null;
		hasCategories = false; 
		isLetterGrading = false;
	}

	public Set<Integer> getIgnoreColumns() {
		return ignoreColumns;
	}

	public void setIgnoreColumns(Set<Integer> ignoreColumns) {
		this.ignoreColumns = ignoreColumns;
	}

	public int getIdFieldIndex() {
		return idFieldIndex;
	}

	public void setIdFieldIndex(int idFieldIndex) {
		this.idFieldIndex = idFieldIndex;
	}

	public int getNameFieldIndex() {
		return nameFieldIndex;
	}

	public void setNameFieldIndex(int nameFieldIndex) {
		this.nameFieldIndex = nameFieldIndex;
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

	public ArrayList<ImportHeader> getHeaders() {
		return headers;
	}

	public void setHeaders(ArrayList<ImportHeader> headers) {
		this.headers = headers;
	}

	public Map<String, ImportHeader> getHeaderMap() {
		return headerMap;
	}

	public void setHeaderMap(Map<String, ImportHeader> headerMap) {
		this.headerMap = headerMap;
	}

	public Map<Long, String> getCategoryIdNameMap() {
		return categoryIdNameMap;
	}

	public void setCategoryIdNameMap(Map<Long, String> categoryIdNameMap) {
		this.categoryIdNameMap = categoryIdNameMap;
	}

	public String[] getHeaderColumns() {
		return headerColumns;
	}

	public void setHeaderColumns(String[] headerColumns) {
		this.headerColumns = headerColumns;
	}

	public boolean hasCategories() {
		return hasCategories;
	}

	public void setHasCategories(boolean hasCategories) {
		this.hasCategories = hasCategories;
	}

	public boolean isLetterGrading() {
		return isLetterGrading;
	}

	public void setLetterGrading(boolean isLetterGrading) {
		this.isLetterGrading = isLetterGrading;
	}

	public boolean isHasWeights() {
		return hasWeights;
	}

	public void setHasWeights(boolean hasWeights) {
		this.hasWeights = hasWeights;
	}

	public boolean isPointsMode() {
		return isPointsMode;
	}

	public void setPointsMode(boolean isPointsMode) {
		this.isPointsMode = isPointsMode;
	}
}


class RawFile 
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

	public RawFile()
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
