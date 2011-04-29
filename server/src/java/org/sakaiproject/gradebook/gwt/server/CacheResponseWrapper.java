package org.sakaiproject.gradebook.gwt.server;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class CacheResponseWrapper extends HttpServletResponseWrapper {

	public CacheResponseWrapper(HttpServletResponse response) {
		super(response);
		// TODO Auto-generated constructor stub
	}

}
