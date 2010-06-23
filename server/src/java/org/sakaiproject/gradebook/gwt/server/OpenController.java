package org.sakaiproject.gradebook.gwt.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

public interface OpenController {
	
	public ModelAndView submit(HttpServletRequest request,
			HttpServletResponse response,
			Object command, BindException errors) throws Exception;

	
	
	public void initializeBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws ServletException;

}
