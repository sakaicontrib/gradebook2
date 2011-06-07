package org.sakaiproject.gradebook.gwt.server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.jersey.spi.spring.container.servlet.SpringServlet;

public class RestServlet extends SpringServlet {

	private static final long serialVersionUID = 7800385748682164593L;
	private static final Log log = LogFactory.getLog(RestServlet.class);
	
	public int service(URI baseUri, URI requestUri, final HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
		
		// Need to handle the case of different baseUri from requestUri
		if (baseUri != null && requestUri != null && baseUri.getPath() != null &&
				requestUri.getPath() != null) {
		
			String requestUriString = requestUri.toString();
			
			int indexOf = requestUriString.indexOf("/gradebook/rest/");
			
			if (indexOf != -1) {
				String baseUriString = new StringBuilder()
					.append(requestUriString.substring(0, indexOf))
					.append("/gradebook/rest/").toString();
				
				try {
					baseUri = new URI(baseUriString);
				} catch (URISyntaxException e) {
					log.error("Unable to generate base uri", e);
				}
			}
		}
		
		if (log.isDebugEnabled())
			log.debug("doService() " + baseUri + " " + requestUri);
		((HttpServletResponse)response).setHeader("Pragma", "no-cache");
		((HttpServletResponse)response).setHeader("Cache-Control", "no-cache");
		((HttpServletResponse)response).setHeader("Expires", "Fri, 12 Dec 1990 12:00:00 GMT");
		
		return super.service(baseUri, requestUri, request, response);
	}
	
}
