package org.sakaiproject.gradebook.gwt.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.gradebook.gwt.client.model.AuthModel;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class ResourceController implements Controller {

	private static final String DEFAULT_PAGE = "GradebookApplication.html";
	private static final String RELATIVE_PREFIX = "/";
	
	private Gradebook2Service service;
	
	public Gradebook2Service getService() {
		return service;
	}

	public void setService(Gradebook2Service service) {
		this.service = service;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		StringBuilder url = new StringBuilder();
		url.append(request.getRequestURI()).append(RELATIVE_PREFIX).append(DEFAULT_PAGE);
		
		if (service != null) {
			AuthModel authModel = service.getAuthorization();
		
			if (authModel != null) {
				url.append(authModel.toString());
			}
			
		} else {
			
		}
		
		response.sendRedirect(url.toString());
		
		return null;
	}

}
