package org.sakaiproject.gradebook.gwt.server;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.jersey.spi.spring.container.servlet.SpringServlet;

public class RestServlet extends SpringServlet {

	private static final long serialVersionUID = 7800385748682164593L;
	private static final Log log = LogFactory.getLog(RestServlet.class);
	
	public void service(URI baseUri, URI requestUri, final HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
		if (log.isDebugEnabled())
			log.debug("doService() " + baseUri + " " + requestUri);
		((HttpServletResponse)response).setHeader("Pragma", "no-cache");
		((HttpServletResponse)response).setHeader("Cache-Control", "no-cache");
		((HttpServletResponse)response).setHeader("Expires", "Fri, 12 Dec 1990 12:00:00 GMT");
		super.service(baseUri, requestUri, request, response);
	}
	
}
