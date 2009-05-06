package org.sakaiproject.gradebook.gwt.sakai;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.client.GradebookToolFacade;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.server.ImportExportUtility;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class GradebookExportController implements Controller {

	private static final Log log = LogFactory.getLog(GradebookExportController.class);
			
	private GradebookToolFacade delegateFacade;
	private ExportAdvisor exportAdvisor;
	
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
			ImportExportUtility.exportGradebook(delegateFacade, gradebookUid, exportAdvisor, doIncludeStructure, writer);
		} catch (FatalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.flush();
		writer.close();
		
		return null;
	}
	
	public GradebookToolFacade getDelegateFacade() {
		return delegateFacade;
	}

	public void setDelegateFacade(GradebookToolFacade delegateFacade) {
		this.delegateFacade = delegateFacade;
	}

	public ExportAdvisor getExportAdvisor() {
		return exportAdvisor;
	}

	public void setExportAdvisor(ExportAdvisor exportAdvisor) {
		this.exportAdvisor = exportAdvisor;
	}

}
