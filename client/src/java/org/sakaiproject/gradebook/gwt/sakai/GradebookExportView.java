package org.sakaiproject.gradebook.gwt.sakai;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.client.GradebookToolFacade;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.server.ImportExportUtility;
import org.springframework.web.servlet.view.AbstractView;


public class GradebookExportView extends AbstractView {

	private static final Log log = LogFactory.getLog(GradebookExportView.class);
	
	private GradebookToolFacade delegateFacade;
	
	public GradebookExportView(GradebookToolFacade delegateFacade) {
		super();
		this.delegateFacade = delegateFacade;
	}
	
	public String getContentType() {
		return "application/x-download";
	}

	@Override
	protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("application/x-download");
		response.setHeader("Content-Disposition", "attachment; filename=gradebook.csv");
		response.setHeader("Pragma", "no-cache");
		
		PrintWriter writer = response.getWriter();
		
		String queryString = request.getQueryString();
		int n = queryString.indexOf("gradebookUid=") + 13;
		String gradebookUid = queryString.substring(n);
		
		log.warn("GradebookUid: " + gradebookUid);
		log.warn("Content-Disposition: attachment");
		try {
			ImportExportUtility.exportGradebook(delegateFacade, gradebookUid, writer);
		} catch (FatalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.flush();
		writer.close();
	}

	/*private String getExportId(StudentModel model) {
		String exportId = model.getEid();
		
		if (exportId == null)
			exportId = model.getIdentifier();
		
		return exportId;
	}*/
	
	public GradebookToolFacade getDelegateFacade() {
		return delegateFacade;
	}

	public void setDelegateFacade(GradebookToolFacade delegateFacade) {
		this.delegateFacade = delegateFacade;
	}

}
