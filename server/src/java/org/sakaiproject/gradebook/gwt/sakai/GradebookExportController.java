package org.sakaiproject.gradebook.gwt.sakai;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.server.ImportExportUtility;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class GradebookExportController implements Controller {

	private static final Log log = LogFactory.getLog(GradebookExportController.class);
			
	private Gradebook2Service service;
	
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		PrintWriter writer = response.getWriter();
		
		String queryString = request.getQueryString();
		int n = queryString.indexOf("gradebookUid=") + 13;
		int m = queryString.indexOf("&include=");
		
		boolean doIncludeStructure = m != -1;
		
		String gradebookUid = queryString.substring(n);
		
		if (doIncludeStructure)
			gradebookUid = queryString.substring(n, m);
		
		try {
			ImportExportUtility.exportGradebook(service, gradebookUid, doIncludeStructure, false, writer, response);
		} catch (FatalException e) {
			log.error("EXCEPTION: Wasn't able to export gradebook: " + gradebookUid, e);
			// 500 Internal Server Error
			response.setStatus(500);
		}
		writer.flush();
		writer.close();
		
		return null;
	}

	public Gradebook2Service getService() {
		return service;
	}

	public void setService(Gradebook2Service service) {
		this.service = service;
	}



}
