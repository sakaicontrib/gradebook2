package org.sakaiproject.gradebook.gwt.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.text.DecimalFormat;
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
import org.sakaiproject.gradebook.gwt.sakai.model.UserDereference;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class ImportExportUtility {

	private static final Log log = LogFactory.getLog(ImportExportUtility.class);
	
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
			final boolean includeStructure, PrintWriter writer, HttpServletResponse response) 
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
			//response.setHeader("Pragma", "no-cache");
		}
		
		CSVWriter csvWriter = new CSVWriter(writer);
		
		Long gradebookId = gradebook.getGradebookId();
		final List<String> headerIds = new ArrayList<String>();
		List<Long> itemCategoryIdList = new LinkedList<Long>(); 
		
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
	
	
	/*
	 * so basically, we'll do: 
	 * 1) Scan the sheet for scantron artifacts, and if so convert to a simple CSV file which is 
	 */
	public static ImportFile parseImportXLS(Gradebook2Service service, 
			String gradebookUid, InputStream is) throws InvalidInputException, FatalException, IOException {
		log.debug("parseImportXLS() called"); 
		
		HSSFWorkbook inspread = null;
		
		BufferedInputStream bufStream = new BufferedInputStream(is); 
		
		inspread = readPoiSpreadsheet(bufStream);
		
		if (inspread != null)
		{
			log.debug("Found a POI readable spreadsheet");
			bufStream.close(); 
			return handlePoiSpreadSheet(inspread, service, gradebookUid);
		}
		else
		{
			log.debug("POI couldn't handle the spreadsheet, using jexcelapi");
			return handleJExcelAPISpreadSheet(bufStream, service, gradebookUid); 
		}
		
		
	}
	
	private static ImportFile handleJExcelAPISpreadSheet(BufferedInputStream is,
			Gradebook2Service service, String gradebookUid) throws InvalidInputException, FatalException, IOException {
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
				return handleScantronSheetForJExcelApi(s, service, gradebookUid);
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
		
		return parseImportGeneric(service, gradebookUid, raw);
	}

	private static ImportFile handleScantronSheetForJExcelApi(Sheet s,
			Gradebook2Service service, String gradebookUid) throws InvalidInputException, FatalException 
	{
		RawFile raw = new RawFile(); 

		Cell studentIdHeader = s.findCell("student_id");
		Cell scoreHeader = s.findCell("score");
		
		String[] header = new String[2]; 
		header[0] = "Student Id"; 
		header[1] = "Scantron Item"; 
		
		raw.addRow(header); 
		header = null; 
		for (int i = 0 ; i < s.getRows() ; i++)
		{
			Cell idCell; 
			Cell score; 
			
			idCell = s.getCell(studentIdHeader.getColumn(), i);
			score = s.getCell(scoreHeader.getColumn(), i); 
			
			if (!idCell.getContents().equals(studentIdHeader.getContents()))
			{
				String[] item = new String[2]; 
				item[0] = idCell.getContents(); 
				item[1] = score.getContents(); 
				raw.addRow(item); 
				item = null; 
			}
		}
		return parseImportGeneric(service, gradebookUid, raw);

	}

	private static boolean isScantronSheetForJExcelApi(Sheet s) {
		Cell studentIdHeader = s.findCell("student_id");
		Cell scoreHeader = s.findCell("score");
		
		return (studentIdHeader != null && scoreHeader != null); 
	}

	private static ImportFile handlePoiSpreadSheet(HSSFWorkbook inspread, Gradebook2Service service, String gradebookUid ) throws InvalidInputException, FatalException
	{
		log.debug("handlePoiSpreadSheet() called"); 
		// FIXME - need to do multiple sheets, and structure
		int numSheets = inspread.getNumberOfSheets(); 
		if (numSheets > 0)
		{
			HSSFSheet cur = inspread.getSheetAt(0);
			RawFile ret; 
			if (isScantronSheetFromPoi(cur))
			{
				log.debug("POI: Scantron");
				ret = processScantronXls(cur); 
			}
			else
			{
				log.debug("POI: Not scantron");
				ret = processNormalXls(cur); 
			}

			return parseImportGeneric(service, gradebookUid, ret);
		}
		return null; 
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

	private static RawFile processScantronXls(HSSFSheet s) {
		RawFile data = new RawFile(); 
		Iterator<HSSFRow> rowIter = s.rowIterator(); 
		
		while (rowIter.hasNext())
		{
			HSSFRow curRow = rowIter.next();  
			int numCells = curRow.getPhysicalNumberOfCells();
			String[] dataEntity = new String[2]; 
			int i = 0; 
	
			if (numCells == 5)
			{
				HSSFCell idField = curRow.getCell(0);
				HSSFCell scoreField = curRow.getCell(4);
				
				String idString = getStringValueFromCell(idField); 
				String scoreString = getStringValueFromCell(scoreField);
				
				dataEntity[0] = idString; 
				dataEntity[1] = scoreString;
				data.addRow(dataEntity);
			}
			else
			{
				log.debug("Row #" + curRow.getRowNum() + " has " + numCells + " cells.");
			}
		}
		
		return data; 
	}

	private static String getStringValueFromCell(HSSFCell c)
	{
		String ret = ""; 
		StringBuilder sb = new StringBuilder(); 
		
		switch (c.getCellType())
		{
			case HSSFCell.CELL_TYPE_NUMERIC:
				log.debug("Type Numeric, value: " + c.getNumericCellValue());
				sb.append( Double.toString( c.getNumericCellValue() ) );
				break; 
			case HSSFCell.CELL_TYPE_STRING: 
				log.debug("Type String, value: " + c.getRichStringCellValue().getString());
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
			
			if (possibleHeader != null && possibleHeader.getCellType() == HSSFCell.CELL_TYPE_STRING &&  "student_row".equals(possibleHeader.getRichStringCellValue().getString()) )
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
	
 
		return parseImportGeneric(service, gradebookUid, rawData );
	}	
	
	public static ImportFile parseImportGeneric(Gradebook2Service service, 
			String gradebookUid, RawFile rawData) throws InvalidInputException, FatalException {
		
		GradebookModel gradebook = service.getGradebook(gradebookUid);
		boolean hasCategories = gradebook.getGradebookItemModel().getCategoryType() != CategoryType.NO_CATEGORIES;
		
		List<UserDereference> userDereferences = service.findAllUserDereferences();
		Map<String, UserDereference> userDereferenceMap = new HashMap<String, UserDereference>();
		
		for (UserDereference dereference : userDereferences) {
			String exportUserId = service.getExportUserId(dereference); 
			userDereferenceMap.put(exportUserId, dereference);
		}
		
		ImportFile importFile = new ImportFile();
		List<ImportHeader> headers = new ArrayList<ImportHeader>();
		Map<String, ImportHeader> headerMap = new HashMap<String, ImportHeader>();
		List<ImportRow> importRows = new ArrayList<ImportRow>();
		
		DecimalFormat decimalFormat = new DecimalFormat();
		
		Map<Long, String> categoryIdNameMap = new HashMap<Long, String>();
		//CSVReader csvReader = new CSVReader(reader);
		
		rawData.startReading(); 
		rawData.dumpFileToLog(); 
		String[] headerColumns = null;
		
		int idFieldIndex = -1;
		int nameFieldIndex = -1;
		int courseGradeFieldIndex = -1;
		
		Set<Integer> ignoreColumns = new HashSet<Integer>();
		
		boolean isStructureInfoProcessed = false;
		
		Map<String, StructureRow> structureLineIndicatorMap = new HashMap<String, StructureRow>();
		Map<StructureRow, String[]> structureColumnsMap = new HashMap<StructureRow, String[]>();
		
		String[] headerLineIndicators = { "student id", "student name", "learner", "id", "identifier", "userid", "learnerid" };
		Set<String> headerLineIndicatorSet = new HashSet<String>();
		
		
		
		for (int i=0;i<headerLineIndicators.length;i++) {
			headerLineIndicatorSet.add(headerLineIndicators[i]);
		}
		
		for (StructureRow structureRow : EnumSet.allOf(StructureRow.class)) {
			String lowercase = structureRow.getDisplayName().toLowerCase();
			structureLineIndicatorMap.put(lowercase, structureRow);
		}
		

		String[] columns;
		while ((columns = rawData.readNext()) != null) {

			// First we need to decide if there is any structure information in this file
			if (!isStructureInfoProcessed) {
				// Until we run into a line that begins with one of the header line indicators, we can safely 
				// assume that we might find some structure info
				if (columns[0] == null)
					continue;
				
				String firstColumnLowerCase = columns[0].toLowerCase();
				if (!headerLineIndicatorSet.contains(firstColumnLowerCase)) {
					// Since it's not processed yet, check to see if the current row has any useful info
					for (int i=0;i<columns.length;i++) {
						String columnLowerCase = columns[i].trim().toLowerCase();
						
						StructureRow structureRow = structureLineIndicatorMap.get(columnLowerCase);
						
						if (structureRow != null) {
							structureColumnsMap.put(structureRow, columns);
						}
					}
				
					continue;
				} else {
					isStructureInfoProcessed = true;
				}
			}
			
			
			if (headerColumns == null) {
				headerColumns = columns;
				
				for (int i=0;i<headerColumns.length;i++) {
					String text = headerColumns[i];
					
					ImportHeader header = null;
					// Check for name field
					if (text == null || text.trim().equals("")) {
						ignoreColumns.add(Integer.valueOf(i));
						continue;
					} else if (text.equalsIgnoreCase("student name") || text.equalsIgnoreCase("name") ||
							text.equalsIgnoreCase("learner")) {
						header = new ImportHeader(Field.NAME, text);
						header.setId("NAME");
						nameFieldIndex = i;
					} else if (text.equalsIgnoreCase("student id") || text.equalsIgnoreCase("identifier") ||
							text.equalsIgnoreCase("userId") || text.equalsIgnoreCase("learnerid")) {
						header = new ImportHeader(Field.ID, text);
						header.setId("ID");
						idFieldIndex = i;
					} else if (text.equalsIgnoreCase("course grade")) {
						// Do nothing
						courseGradeFieldIndex = i;
					} else {
						
						String name = null;
						String points = null;
						boolean isExtraCredit = text.contains(AppConstants.EXTRA_CREDIT_INDICATOR);
						
						if (isExtraCredit) {
							text = text.replace(AppConstants.EXTRA_CREDIT_INDICATOR, "");
						}
						
						boolean isUnincluded = text.contains(AppConstants.UNINCLUDED_INDICATOR);
						
						if (isUnincluded) {
							text = text.replace(AppConstants.UNINCLUDED_INDICATOR, "");
						}
						
						name = text;
						
						int startParen = text.indexOf("[");
						int endParen = text.indexOf("pts]");
						
						if (endParen == -1)
							endParen = text.indexOf("]");
						
						if (startParen != -1 && endParen != -1 && endParen > startParen+1) {
							points = text.substring(startParen+1, endParen);
							name = text.substring(0, startParen);
							
							if (name != null)
								name = name.trim();
						}
						
						if (name != null) {
							ItemModel model = findModelByName(name, gradebook.getGradebookItemModel());
						
							StringBuffer value = new StringBuffer(name);
							
							if (model != null) 
								points = decimalFormat.format(model.getPoints());
							
							if (points == null)
								points = "100";
							
							if (points != null && points.length() > 0)
								value.append(" [").append(points).append("]");
							
							header = new ImportHeader(Field.ITEM, value.toString());
							
							if (model != null) { 
								header.setId(model.getIdentifier());
								header.setCategoryName(model.getCategoryName());
								header.setCategoryId(String.valueOf(model.getCategoryId()));
								
								categoryIdNameMap.put(model.getCategoryId(), model.getCategoryName());
							} else {
								header.setId(new StringBuilder().append("NEW:").append(i).toString());
								header.setCategoryName("Unassigned");
							}
							header.setHeaderName(name);
							header.setExtraCredit(Boolean.valueOf(isExtraCredit));
							header.setUnincluded(Boolean.valueOf(isUnincluded));
							if (points != null && points.equals("%"))
								header.setPercentage(true);
							else {
								try {
									header.setPoints(Double.valueOf(Double.parseDouble(points)));
								} catch (NumberFormatException nfe) {
									System.out.println("Could not parse points " + points);
								}
							}
						}
						
					}
					
					headers.add(header);
					headerMap.put(text, header);
				}
				
			} else {
				ImportRow row = new ImportRow();
				
				// First, based on whichever column is the id column, populate it
				if (columns.length > idFieldIndex && idFieldIndex != -1) {
					String userImportId = columns[idFieldIndex];
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
				
				if (courseGradeFieldIndex == -1)
					row.setColumns(columns);
				else {
					String[] strippedColumns = new String[columns.length - 1];
					int n = 0;
					for (int i=0;i<columns.length;i++) {
						Integer columnNumber = Integer.valueOf(i);
						
						if (ignoreColumns.contains(columnNumber))
							continue;
						
						if (courseGradeFieldIndex == i)
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

		importFile.setItems(headers);
		importFile.setRows(importRows);
		
		
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
				hasCategories = cType != CategoryType.NO_CATEGORIES;
				
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
		
		String[] categoryRangeColumns = new String[headerColumns.length];
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
						if (isModelNew) 
							result = service.createItem(gradebook.getGradebookUid(), gradebook.getGradebookId(), categoryModel, false);
						else if (isModelUpdated) 
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
						}
					}
					
				}
			}
		}
		
		if (headerColumns != null) {
			
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
				
				int startParen = text.indexOf("[");
				int endParen = text.indexOf("pts]");
				
				if (endParen == -1)
					endParen = text.indexOf("]");
				
				if (startParen != -1 && endParen != -1 && endParen > startParen+1) {
					name = text.substring(0, startParen);
					
					if (name != null)
						name = name.trim();
				}
				
				
				ImportHeader header = headerMap.get(name);
				
				if (header != null) {
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
					
					if (categoryRangeColumns != null && categoryRangeColumns.length > i) {
						String categoryId = categoryRangeColumns[i];
						
						if (categoryId != null) {
							String categoryName = categoryIdNameMap.get(categoryId);
							if (categoryName != null)
								header.setCategoryName(categoryName);
							header.setCategoryId(categoryId);
						}
						
					}
					
				}
			}
		}
		
		importFile.setHasCategories(Boolean.valueOf(hasCategories));

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

	private static ItemModel findModelByName(final String name, ItemModel root) {
		
		ItemModelProcessor processor = new ItemModelProcessor(root) {
			
			@Override
			public void doItem(ItemModel itemModel) {
			
				String itemName = itemModel.getName();
				
				if (itemName != null) {
					String trimmed = itemName.trim();
					
					if (trimmed.equals(name))
						this.result = itemModel;
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

class RawFile 
{
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
		allRows = new ArrayList<String[]>(); 
	}
	
	public void dumpFileToLog()
	{
		int row = 1; 
		log.debug("---> Starting to dump to log");
		for (String[] cur : allRows) 
		{
			StringBuilder msg = new StringBuilder("Row #"); 
			msg.append(row); 
			msg.append(" = "); 
			for (int j = 0; j < cur.length ; j++)
			{
				msg.append("C"); 
				msg.append(j); 
				msg.append(": "); 
				msg.append(cur[j]); 
				msg.append(" "); 
			}
			log.debug(msg.toString()); 
		}
		log.debug("---> Finished dump to log");

		
		
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
}
