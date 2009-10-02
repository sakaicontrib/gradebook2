package org.sakaiproject.gradebook.gwt.server;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class RequestFilter extends org.sakaiproject.util.RequestFilter {

	private boolean isHostedMode = false;
	
	public void destroy() {
		if (!isHostedMode)
			super.destroy();
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		if (!isHostedMode)
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

}
