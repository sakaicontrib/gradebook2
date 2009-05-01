package org.sakaiproject.gradebook.gwt.sakai;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.client.GradebookToolFacade;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityGetAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.server.ImportExportUtility;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class GradebookExportController implements Controller {

	private static final Log log = LogFactory.getLog(GradebookExportController.class);
			
	private GradebookToolFacade delegateFacade;
	
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String filename = "gradebook.csv";
		
		response.setContentType("application/x-download");
		response.setHeader("Content-Disposition", "attachment; filename=gradebook.csv");
		response.setHeader("Pragma", "no-cache");
		
		PrintWriter writer = response.getWriter();
		
		String queryString = request.getQueryString();
		int n = queryString.indexOf("gradebookUid=") + 13;
		int m = queryString.indexOf("&include=");
		
		boolean doIncludeStructure = m != -1;
		
		String gradebookUid = queryString.substring(n);
		
		if (doIncludeStructure)
			gradebookUid = queryString.substring(n, m);
		
		
		log.warn("GradebookUid: " + gradebookUid);
		log.warn("Content-Disposition: attachment");
		try {
			ImportExportUtility.exportGradebook(delegateFacade, gradebookUid, doIncludeStructure, writer);
		} catch (FatalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.flush();
		writer.close();
		
		/*
		log.warn("GradebookUid: " + gradebookUid);
		try {
			UserEntityGetAction<GradebookModel> getGradebookAction = new UserEntityGetAction<GradebookModel>(gradebookUid, EntityType.GRADEBOOK);
			getGradebookAction.setEntityId(gradebookUid);
			GradebookModel gradebook = delegateFacade.getEntity(getGradebookAction);
			
			if (gradebook.getName() != null) {
				filename = gradebook.getName();
				filename = new StringBuilder().append(filename.replace(' ', '_')).append(".csv").toString();
			}
			
			response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			
			//UserEntityGetAction<ItemModel> getHeadersAction = new UserEntityGetAction<ItemModel>(gradebookUid, EntityType.ITEM);

			List<ItemModel> headers = new ArrayList<ItemModel>(); //delegateFacade.getEntityList(getHeadersAction);
			
			for (ItemModel child : gradebook.getGradebookItemModel().getChildren()) {
				switch (child.getItemType()) {
				case CATEGORY:
					for (ItemModel item : child.getChildren()) {
						headers.add(item);
					}
					break;
				case ITEM:
					headers.add(child);
					break;
				}
			}
			
			
			UserEntityGetAction<StudentModel> getRowsAction = new UserEntityGetAction<StudentModel>(gradebookUid, EntityType.LEARNER);
			List<StudentModel> rows = delegateFacade.getEntityList(getRowsAction);
			
			String[] headerIds = null;
			if (headers != null) {
				writer.print("Learner,Id");	
				headerIds = new String[headers.size()];
				int i=0;
				for (ItemModel header : headers) {
					headerIds[i] = header.getIdentifier();
					writer.print(",");
					writer.print(header.getName());
					
					switch (gradebook.getGradebookItemModel().getGradeType()) {
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
			}
		} catch (FatalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.flush();
		//writer.close();
		*/
		return null;
	}

	private String getExportId(StudentModel model) {
		String exportId = model.getEid();
		
		if (exportId == null)
			exportId = model.getIdentifier();
		
		return exportId;
	}
	
	public GradebookToolFacade getDelegateFacade() {
		return delegateFacade;
	}

	public void setDelegateFacade(GradebookToolFacade delegateFacade) {
		this.delegateFacade = delegateFacade;
	}

}
