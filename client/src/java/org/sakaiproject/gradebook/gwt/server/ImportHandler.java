package org.sakaiproject.gradebook.gwt.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.sakaiproject.gradebook.gwt.client.GradebookToolFacade;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.gxt.upload.ImportFile;
import org.sakaiproject.gradebook.gwt.sakai.mock.DelegateFacadeMockImpl;
import org.sakaiproject.gradebook.gwt.sakai.mock.IocMock;
import org.sakaiproject.gradebook.gwt.server.ImportExportUtility.Delimiter;
import org.sakaiproject.user.api.UserDirectoryService;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;


public class ImportHandler extends HttpServlet {

	//private static enum Delimiter { TAB, COMMA, SPACE, COLON };
	
	private static final String CONTENT_TYPE = "text/html";
	private static final long serialVersionUID = 1L;

	private IocMock iocMock = IocMock.getInstance();
	
	private GradebookToolFacade delegateFacade;
	private UserDirectoryService userService;
	
	public void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		if (delegateFacade == null)
			delegateFacade = (GradebookToolFacade)iocMock.getClassInstance(DelegateFacadeMockImpl.class.getName());
		
		response.setContentType("application/ms-excel");
		response.setHeader("Content-Disposition", "inline; filename=" + "gradebook.csv");
		
		PrintWriter writer = response.getWriter();
		
		String gradebookUid = req.getParameter("gradebookUid");
		try {
			ImportExportUtility.exportGradebook(delegateFacade, gradebookUid, writer);
			/*UserEntityGetAction<GradebookModel> getGradebookAction = new UserEntityGetAction<GradebookModel>(gradebookUid, EntityType.GRADEBOOK);
			GradebookModel gradebook = delegateFacade.getEntity(getGradebookAction);
				
			UserEntityGetAction<AssignmentModel> getHeadersAction = new UserEntityGetAction<AssignmentModel>(gradebookUid, EntityType.GRADE_ITEM);
			List<AssignmentModel> headers = delegateFacade.getEntityList(getHeadersAction);
			
			UserEntityGetAction<StudentModel> getRowsAction = new UserEntityGetAction<StudentModel>(gradebookUid, EntityType.STUDENT);
			List<StudentModel> rows = delegateFacade.getEntityList(getRowsAction);
			
			String[] headerIds = null;
			if (headers != null) {
				writer.print("Learner,Id");	
				headerIds = new String[headers.size()];
				int i=0;
				for (AssignmentModel header : headers) {
					headerIds[i] = header.getIdentifier();
					writer.print(",");
					writer.print(header.getName());
					
					switch (gradebook.getGradeType()) {
					case POINTS:
						String points = DecimalFormat.getInstance().format(header.getPoints());
						writer.print(" (");
						writer.print(points);
						writer.print(")");
						break;
					case PERCENTAGES:
						writer.print(" (%)");
						break;
					} 
					
					i++;
				}
				writer.println();
			
				if (rows != null) {
					for (StudentModel row : rows) {
						writer.print(row.getDisplayName());
						writer.print(",");
						writer.print(getExportId(row));
						for (int column = 0;column<headerIds.length;column++) {
							writer.print(",");
							if (headerIds[column] != null) {
								Object value = row.get(headerIds[column]);
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
			}*/
		} catch (FatalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

		
		XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
		
		response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
		
		String feedback = "nothing";
		//JSONObject feedback = new JSONObject();
        boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        if (isMultipart) {
                final DiskFileItemFactory factory = new DiskFileItemFactory();
                //factory.setRepository(new File("/tmp/gwt"));
                factory.setSizeThreshold(1000000);
                final ServletFileUpload upload = new ServletFileUpload(factory);
                try {
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
                       
                       ImportFile importFile = ImportExportUtility.parseImportX(delegateFacade, userService, gradebookUid, uploadedFile.getString(), delimiterSet);
                        
                       out.write(xstream.toXML(importFile)); 
                       

                } catch (FileUploadException e) {
                        System.out.println(e.getMessage());
                } catch (FatalException fe) {
                		System.out.println(fe.getMessage());
                }

                
                
                
        } else {
        	System.out.println("Not multipart");
        }

        
	}

	/*public ImportFile parse(String gradebookUid, String content, EnumSet<Delimiter> delimiterSet) throws FatalException {
		UserEntityGetAction<CategoryModel> categoryAction = new UserEntityGetAction<CategoryModel>(EntityType.CATEGORY);
		categoryAction.setGradebookUid(gradebookUid);
		UserEntityGetAction<AssignmentModel> itemAction = new UserEntityGetAction<AssignmentModel>(EntityType.GRADE_ITEM);
		itemAction.setGradebookUid(gradebookUid);
		GradebookToolFacade delegateFacade = (GradebookToolFacade)iocMock.getClassInstance(DelegateFacadeMockImpl.class.getName());
		List<CategoryModel> categories = delegateFacade.getEntityList(categoryAction);
		List<AssignmentModel> gradeItems = delegateFacade.getEntityList(itemAction);
		
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
							AssignmentModel model = findModelByName(name, gradeItems);
						
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
								
								if (categories != null) {
									for (CategoryModel category : categories) {
										header.setCategoryName(category.getName());
										break;
									}
								} else {
									header.setCategoryName("Default");
								}

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
	
	
	private String getExportId(StudentModel model) {
		String exportId = model.getEid();
		
		if (exportId == null)
			exportId = model.getIdentifier();
		
		return exportId;
	}
	

	private String[] splitFields(String value, EnumSet<Delimiter> delimiterSet) {
		
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
	
	private AssignmentModel findModelByName(String name, List<AssignmentModel> models) {
		for (AssignmentModel model : models) {
			
			if (model.getName().equals(name))
				return model;
			
		}
		
		return null;
	}*/

	public UserDirectoryService getUserService() {
		return userService;
	}

	public void setUserService(UserDirectoryService userService) {
		this.userService = userService;
	}

	public GradebookToolFacade getDelegateFacade() {
		return delegateFacade;
	}

	public void setDelegateFacade(GradebookToolFacade delegateFacade) {
		this.delegateFacade = delegateFacade;
	}

}
