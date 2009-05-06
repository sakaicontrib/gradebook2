package org.sakaiproject.gradebook.gwt.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.client.GradebookToolFacade;
import org.sakaiproject.gradebook.gwt.client.action.PageRequestAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityGetAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
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
import org.sakaiproject.gradebook.gwt.sakai.DelegateFacadeImpl;
import org.sakaiproject.gradebook.gwt.sakai.ExportAdvisor;
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

	public static void exportGradebook(GradebookToolFacade delegateFacade, String gradebookUid, 
			final boolean includeStructure, PrintWriter writer) throws FatalException {
		UserEntityGetAction<GradebookModel> getGradebookAction = new UserEntityGetAction<GradebookModel>(gradebookUid, EntityType.GRADEBOOK);
		GradebookModel gradebook = delegateFacade.getEntity(getGradebookAction);
		ItemModel gradebookItemModel = gradebook.getGradebookItemModel();
		
		CSVWriter csvWriter = new CSVWriter(writer);
		
		Long gradebookId = gradebook.getGradebookId();
		final List<String> headerIds = new ArrayList<String>();
		
		if (includeStructure) {
			CategoryType categoryType = gradebookItemModel.getCategoryType();
			String categoryTypeText = categoryType.getDisplayName();
			String gradeTypeText = gradebookItemModel.getGradeType().getDisplayName();
			
			// First, we need to add a row for basic gradebook info
			String[] gradebookInfoRow = { "", "", StructureRow.GRADEBOOK.getDisplayName(), gradebook.getName(), categoryTypeText, gradeTypeText};
			csvWriter.writeNext(gradebookInfoRow);
			
			final List<String> categoriesRow = new LinkedList<String>();
			final List<String> percentageGradeRow = new LinkedList<String>();
			final List<String> pointsRow = new LinkedList<String>();
			final List<String> percentCategoryRow = new LinkedList<String>();
			final List<String> dropLowestRow = new LinkedList<String>();
			final List<String> equalWeightRow = new LinkedList<String>();
			
			categoriesRow.add("");
			categoriesRow.add("");
			categoriesRow.add(StructureRow.CATEGORY.getDisplayName());
			percentageGradeRow.add("");
			percentageGradeRow.add("");
			percentageGradeRow.add(StructureRow.PERCENT_GRADE.getDisplayName());
			pointsRow.add("");
			pointsRow.add("");
			pointsRow.add(StructureRow.POINTS.getDisplayName());
			percentCategoryRow.add("");
			percentCategoryRow.add("");
			percentCategoryRow.add(StructureRow.PERCENT_CATEGORY.getDisplayName());
			dropLowestRow.add("");
			dropLowestRow.add("");
			dropLowestRow.add(StructureRow.DROP_LOWEST.getDisplayName());
			equalWeightRow.add("");
			equalWeightRow.add("");
			equalWeightRow.add(StructureRow.EQUAL_WEIGHT.getDisplayName());
			
			ItemModelProcessor processor = new ItemModelProcessor(gradebookItemModel) {
				
				@Override
				public void doCategory(ItemModel itemModel, int childIndex) {
					categoriesRow.add(itemModel.getName());
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
					
				}
				
				@Override
				public void doItem(ItemModel itemModel, int childIndex) {
					if (childIndex > 0) {
						categoriesRow.add("");
						percentageGradeRow.add("");
						dropLowestRow.add("");
						equalWeightRow.add("");
					}
					
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
		}
		
		
		final List<String> headerColumns = new LinkedList<String>();
		
		headerColumns.add("Student Id");
		headerColumns.add("Student Name");
		
		ItemModelProcessor processor = new ItemModelProcessor(gradebookItemModel) {
			
			@Override
			public void doItem(ItemModel itemModel) {
				StringBuilder text = new StringBuilder();
				text.append(itemModel.getName());
				
				if (!includeStructure) {
					String points = DecimalFormat.getInstance().format(itemModel.getPoints());
					text.append(" [").append(points).append("]");
				}
				
				headerIds.add(itemModel.getIdentifier());
				headerColumns.add(text.toString());
			}
			
		};
		
		processor.process();
		
		headerColumns.add("Course Grade");
		
		csvWriter.writeNext(headerColumns.toArray(new String[headerColumns.size()]));
		
		PageRequestAction action = new PageRequestAction(EntityType.LEARNER, gradebookUid, gradebookId);
		PagingLoadResult<StudentModel> result = delegateFacade.getEntityPage(action, null);
		
		List<StudentModel> rows = result.getData();
		
		boolean isPercentages = gradebook.getGradebookItemModel().getGradeType() == GradeType.PERCENTAGES;
		
		if (headerIds != null) {
			
			if (rows != null) {
				for (StudentModel row : rows) {
					List<String> dataColumns = new LinkedList<String>();
					
					dataColumns.add(getExportId(row));
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

	public static ImportFile parseImportX(GradebookToolFacade delegateFacade, ExportAdvisor exportAdvisor, String gradebookUid, Reader reader, EnumSet<Delimiter> delimiterSet) throws FatalException {
		UserEntityGetAction<GradebookModel> getGradebookAction = new UserEntityGetAction<GradebookModel>(gradebookUid, EntityType.GRADEBOOK);
		GradebookModel gradebook = delegateFacade.getEntity(getGradebookAction);
		
		List<UserDereference> userDereferences = ((DelegateFacadeImpl)delegateFacade).findAllUserDeferences();
		Map<String, UserDereference> userDereferenceMap = new HashMap<String, UserDereference>();
		
		for (UserDereference dereference : userDereferences) {
			String exportUserId = exportAdvisor.getExportUserId(dereference); 
			userDereferenceMap.put(exportUserId, dereference);
		}
		
		ImportFile importFile = new ImportFile();
		List<ImportHeader> headers = new ArrayList<ImportHeader>();
		List<ImportRow> importRows = new ArrayList<ImportRow>();
		
		DecimalFormat decimalFormat = new DecimalFormat();
		
		
		CSVReader csvReader = new CSVReader(reader);
		
		String[] headerColumns = null;
		
		int idFieldIndex = -1;
		int nameFieldIndex = -1;
		int courseGradeFieldIndex = -1;
		
		boolean isStructureInfoProcessed = false;
		
		try {
			
			String[] headerLineIndicators = { "student id", "student name", "learner", "id", "identifier", "userid", "learnerid" };
			Set<String> headerLineIndicatorSet = new HashSet<String>();
			
			Map<String, StructureRow> structureLineIndicatorMap = new HashMap<String, StructureRow>();
			
			for (int i=0;i<headerLineIndicators.length;i++) {
				headerLineIndicatorSet.add(headerLineIndicators[i]);
			}
			
			for (StructureRow structureRow : EnumSet.allOf(StructureRow.class)) {
				String lowercase = structureRow.getDisplayName().toLowerCase();
				structureLineIndicatorMap.put(lowercase, structureRow);
			}
			
			Map<StructureRow, String[]> structureColumnsMap = new HashMap<StructureRow, String[]>();
			
			
			String[] columns;
		    while ((columns = csvReader.readNext()) != null) {

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
						if (text.equalsIgnoreCase("student name") || text.equalsIgnoreCase("name") ||
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
							
							String name = text;
							String points = null;
							
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
								} else {
									header.setId(new StringBuilder().append("NEW:").append(i).toString());
									header.setCategoryName("Unassigned");
								}
								header.setHeaderName(name);
								if (points != null && points.equals("%"))
									header.setPercentage(true);
								else {
									try {
										header.setPoints(Double.parseDouble(points));
									} catch (NumberFormatException nfe) {
										System.out.println("Could not parse points " + points);
									}
								}
							}
							
						}
						
						headers.add(header);
						
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
							if (courseGradeFieldIndex == i)
								continue;
							strippedColumns[n] = columns[i];
							n++;
						}
						row.setColumns(strippedColumns);
					}
					importRows.add(row);
				}	
			}
			
			csvReader.close();
		} catch (IOException ioe) {
			log.error("Caught an ioexception: ", ioe);
		}

		importFile.setItems(headers);
		importFile.setRows(importRows);
		
		return importFile;
	}
	
	private static String getExportId(StudentModel model) {
		String exportId = model.getEid();

		if (exportId == null)
			exportId = model.getIdentifier();

		return exportId;
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
	

}
