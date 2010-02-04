package org.sakaiproject.gradebook.gwt.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ComponentService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class ResourceController implements Controller {

	private static final Log log = LogFactory.getLog(ResourceController.class);
	
	private static final String DEFAULT_PAGE = "GradebookApplication.html";
	private static final String RELATIVE_PREFIX = "/";
	
	private Gradebook2ComponentService service;
	
	public Gradebook2ComponentService getService() {
		return service;
	}

	public void setService(Gradebook2ComponentService service) {
		this.service = service;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		log.debug("Received request for main gb2 page");
		
		StringBuilder url = new StringBuilder();
		url.append(request.getRequestURI()).append(RELATIVE_PREFIX).append(DEFAULT_PAGE);
		
		if (service != null) {
			String authDetails = service.getAuthorizationDetails();
		
			if (authDetails != null) {
				url.append(authDetails);
			}
		}
		
		response.sendRedirect(url.toString());
		
		return null;
	}

}
