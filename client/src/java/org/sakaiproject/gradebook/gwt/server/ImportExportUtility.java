package org.sakaiproject.gradebook.gwt.server;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

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
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;

import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class ImportExportUtility {

	private static final Log log = LogFactory.getLog(ImportExportUtility.class);
	
	public static enum Delimiter {
		TAB, COMMA, SPACE, COLON
	};

	public static void exportGradebook(GradebookToolFacade delegateFacade, String gradebookUid, PrintWriter writer) throws FatalException {
		UserEntityGetAction<GradebookModel> getGradebookAction = new UserEntityGetAction<GradebookModel>(gradebookUid, EntityType.GRADEBOOK);
		GradebookModel gradebook = delegateFacade.getEntity(getGradebookAction);

		Long gradebookId = gradebook.getGradebookId();
		List<String> headerIds = new ArrayList<String>();
		writer.print("Learner,Id");
		for (ItemModel child : gradebook.getGradebookItemModel().getChildren()) {
			switch (child.getItemType()) {
			case CATEGORY:
				for (ItemModel item : child.getChildren()) {
					headerIds.add(item.getIdentifier());
					writer.print(",");
					writer.print(item.getName());
					switch (gradebook.getGradebookItemModel().getGradeType()) {
					case POINTS:
						String points = DecimalFormat.getInstance().format(item.getPoints());
						writer.print(" (");
						writer.print(points);
						writer.print(")");
						break;
					case PERCENTAGES:
						writer.print(" (%)");
						break;
					}
				}
				break;
			case ITEM:
				headerIds.add(child.getIdentifier());
				writer.print(",");
				writer.print(child.getName());
				switch (gradebook.getGradebookItemModel().getGradeType()) {
				case POINTS:
					String points = DecimalFormat.getInstance().format(child.getPoints());
					writer.print(" (");
					writer.print(points);
					writer.print(")");
					break;
				case PERCENTAGES:
					writer.print(" (%)");
					break;
				}
				break;
			}
		}
		writer.println();
		
		//UserEntityGetAction<StudentModel> getRowsAction = new UserEntityGetAction<StudentModel>(gradebookUid, EntityType.STUDENT);
		//List<StudentModel> rows = delegateFacade.getEntityList(getRowsAction);

		PageRequestAction action = new PageRequestAction(EntityType.STUDENT, gradebookUid, gradebookId);
		PagingLoadResult<StudentModel> result = delegateFacade.getEntityPage(action, null);
		
		List<StudentModel> rows = result.getData();
		
		if (headerIds != null) {
			
			if (rows != null) {
				for (StudentModel row : rows) {
					writer.print(row.getDisplayName());
					writer.print(",");
					writer.print(getExportId(row));
					for (int column = 0; column < headerIds.size(); column++) {
						writer.print(",");
						if (headerIds.get(column) != null) {
							Object value = row.get(headerIds.get(column));
							if (value != null)
								writer.print(value);
						} else {
							System.out.println("Null column at " + column);
						}
					}
					writer.println();
				}
			} else {
				writer.println();
			}
		}
	}

	/*private static void importGradebook(GradebookToolFacade delegateFacade, PrintWriter writer) {
		XStream xstream = new XStream(new JsonHierarchicalStreamDriver());

		final DiskFileItemFactory factory = new DiskFileItemFactory();
		// factory.setRepository(new File("/tmp/gwt"));
		factory.setSizeThreshold(1000000);
		final ServletFileUpload upload = new ServletFileUpload(factory);
		final List<FileItem> items = (List<FileItem>) upload.parseRequest(req);

		EnumSet<Delimiter> delimiterSet = EnumSet.noneOf(Delimiter.class);
		String gradebookUid = null;
		FileItem uploadedFile = null;
		for (int i = 0; i < items.size(); i++) {
			FileItem current = items.get(i);

			if (current.isFormField()) {

				String name = current.getFieldName();
				if (name != null) {
					if (name.equals("gradebookUid"))
						gradebookUid = current.getString();
					else if (name.equals("delimiter:comma"))
						delimiterSet.add(Delimiter.COMMA);
					else if (name.equals("delimiter:tab"))
						delimiterSet.add(Delimiter.TAB);
					else if (name.equals("delimiter:space"))
						delimiterSet.add(Delimiter.SPACE);
					else if (name.equals("delimiter:colon"))
						delimiterSet.add(Delimiter.COLON);
				}
			} else
				uploadedFile = current;

		}

		ImportFile importFile = parse(delegateFacade, gradebookUid, uploadedFile.getString(), delimiterSet);

		writer.write(xstream.toXML(importFile));
	}*/

	public static ImportFile parseImportX(GradebookToolFacade delegateFacade, UserDirectoryService userService, String gradebookUid, String content, EnumSet<Delimiter> delimiterSet) throws FatalException {
		UserEntityGetAction<GradebookModel> getGradebookAction = new UserEntityGetAction<GradebookModel>(gradebookUid, EntityType.GRADEBOOK);
		GradebookModel gradebook = delegateFacade.getEntity(getGradebookAction);
		
		ImportFile importFile = new ImportFile();
		//System.out.println(content);
		List<ImportHeader> headers = new ArrayList<ImportHeader>();
		List<ImportRow> importRows = new ArrayList<ImportRow>();
		
		DecimalFormat decimalFormat = new DecimalFormat();
		
		String[] rows = content.split("\n");
		
		if (rows.length > 0) {
			String[] headerFields = splitFields(rows[0], delimiterSet);
			
			int idFieldIndex = -1;
			int nameFieldIndex = -1;
			
			for (int i=0;i<headerFields.length;i++) {
				
				String text = headerFields[i];
				
				if (text != null && !text.trim().equals("")) {
					ImportHeader header = null;
					// Check for name field
					if (text.equalsIgnoreCase("student") || text.equalsIgnoreCase("name") ||
							text.equalsIgnoreCase("learner")) {
						header = new ImportHeader(Field.NAME, text);
						header.setId("NAME");
						nameFieldIndex = i;
					} else if (text.equalsIgnoreCase("id") || text.equalsIgnoreCase("identifier") ||
							text.equalsIgnoreCase("userId") || text.equalsIgnoreCase("learnerid")) {
						header = new ImportHeader(Field.ID, text);
						header.setId("ID");
						idFieldIndex = i;
					} else {
						
						String name = text;
						String points = null;
						
						int startParen = text.indexOf("(");
						int endParen = text.indexOf(")");
						
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
								value.append(" (").append(points).append(")");
							
							header = new ImportHeader(Field.ITEM, value.toString());
							
							if (model != null) { 
								header.setId(model.getIdentifier());
								header.setCategoryName(model.getCategoryName());
							} else {
								header.setId(new StringBuilder().append("NEW:").append(i).toString());
								
								/*if (categories != null) {
									for (CategoryModel category : categories) {
										header.setCategoryName(category.getName());
										break;
									}
								} else {*/
									header.setCategoryName("Default");
								//}

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
			}
			
			for (int r=1;r<rows.length;r++) {
				String[] rowFields = splitFields(rows[r], delimiterSet);
				
				ImportRow row = new ImportRow();
				
				// First, based on whichever column is the id column, look up this row's user
				if (rowFields.length > idFieldIndex && idFieldIndex != -1) {
					String userImportId = rowFields[idFieldIndex];
					row.setUserImportId(userImportId);
				
					if (userService != null) {
						try {
							User user = userService.getUserByEid(userImportId);
							
							if (user != null) {
								row.setUserUid(user.getId());
								row.setUserDisplayName(user.getDisplayName());
							}
							
						} catch (UserNotDefinedException e) {
							row.setUserNotFound(true);
						}
					}
				}
				List<String> columns = new ArrayList<String>();
				for (int i=0;i<rowFields.length;i++) {	
					columns.add(rowFields[i]);
				}
				row.setColumns(columns);
				
				importRows.add(row);
			}
		}
		
		importFile.setItems(headers);
		//importFile.setHeaders(headers);
		importFile.setRows(importRows);
		
		return importFile;
	}
	
	private static String getExportId(StudentModel model) {
		String exportId = model.getEid();

		if (exportId == null)
			exportId = model.getIdentifier();

		return exportId;
	}
	
	private static String[] splitFields(String value, EnumSet<Delimiter> delimiterSet) {
		
		List<String> regexList = new ArrayList<String>();
		
		for (Delimiter delimiter : delimiterSet) {
			switch (delimiter) {
			case COMMA:
				regexList.add(",");
				break;
			case TAB:
				regexList.add("\t");
				break;
			case SPACE:
				regexList.add(" ");
				break;
			case COLON:
				regexList.add(":");
				break;
			}
		}
		
		StringBuilder regex = new StringBuilder();
		
		for (int i=0;i<regexList.size();i++) {
			regex.append(regexList.get(i));
			if (i+1 < regexList.size())
				regex.append("|");
		}
		
		String[] fields = value.split(regex.toString());
		
		for (int i=0;i<fields.length;i++) {
			if (fields[i].startsWith("\"") && fields[i].endsWith("\""))
				fields[i] = fields[i].substring(1, fields[i].length() - 1);
			
		}
		
		return fields;
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
