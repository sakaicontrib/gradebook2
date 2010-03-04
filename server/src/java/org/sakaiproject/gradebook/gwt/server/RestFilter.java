package org.sakaiproject.gradebook.gwt.server;

import java.io.IOException;
import java.net.URI;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;

public class RestFilter extends SpringServlet {

	private static final Log log = LogFactory.getLog(RestFilter.class);
	
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		// We need to force IE not to cache REST/AJAX calls
		//Pragma: no-cache
		//Cache-Control: no-cache
		//Expires: Fri, 30 Oct 1998 14:19:41 GMT
		((HttpServletResponse)response).setHeader("Pragma", "no-cache");
		((HttpServletResponse)response).setHeader("Cache-Control", "no-cache");
		((HttpServletResponse)response).setHeader("Expires", "Fri, 12 Dec 1990 12:00:00 GMT");
		
		chain.doFilter(request, response);
	}
	
	/*
	public void destroy() {
		if (isHostedMode)
			super.destroy();
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		if (isHostedMode)
			super.doFilter(request, response, chain);
		else
			chain.doFilter(request, response);
	}
	
	public void init(FilterConfig filterConfig) throws ServletException {
		String hostedModeProperty = System.getProperty("gb2.mode");
		
		this.isHostedMode = null != hostedModeProperty && "hosted".equals(hostedModeProperty);
		
		if (!isHostedMode)
			super.init(filterConfig);
	}
	
	public void initiate(ResourceConfig config, WebApplication webApplication) {
		if (isHostedMode)
			super.initiate(config, webApplication);
		
		log.info("initiate()");
	}
	*/
	
	/*
	public void service(HttpServletRequest request, HttpServletResponse response) 
	throws IOException, ServletException {
		if (log.isDebugEnabled())
			log.debug("service() " + request.getRequestURI());
		try {
			super.service(request, response);
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	public void service(URI baseUri, URI requestUri, final HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
		if (log.isDebugEnabled())
			log.debug("doService() " + baseUri + " " + requestUri);
		super.service(baseUri, requestUri, request, response);
	}*/
	
}
