package org.sakaiproject.gradebook.gwt.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ComponentService;
import org.springframework.web.servlet.ModelAndView;

public class ServletWrappingController extends
		org.springframework.web.servlet.mvc.ServletWrappingController {

	private Gradebook2ComponentService service;
	
	public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) 
		throws Exception {
		
		
		
		
		return super.handleRequestInternal(request, response);
	}

	
	public Gradebook2ComponentService getService() {
		return service;
	}

	public void setService(Gradebook2ComponentService service) {
		this.service = service;
	}
	
}
