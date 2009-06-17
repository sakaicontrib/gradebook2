package org.sakaiproject.gradebook.gwt.server;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class SecurityFilter implements Filter {
	
	private static final String SCRIPT_REGEX = "((<[\\s\\/]*script\\b[^>]*>)([^>]*)(<\\/script>))";
	private static final Pattern pattern = Pattern.compile(SCRIPT_REGEX);

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
				
		String contentType = request.getContentType();
		
		if(null != contentType && contentType.startsWith("text/x-gwt-rpc")) {

			SecurityRequestWrapper securityRequestWrapper = new SecurityRequestWrapper((HttpServletRequest) request);
			
			String body = securityRequestWrapper.getBody();

			Matcher matcher = pattern.matcher(body);
			if(matcher.find()) {
				throw new ServletException("JavaScript Injection Detected!");
			}

			chain.doFilter(securityRequestWrapper, response);
		}
		else {

			chain.doFilter(request, response);
		}
	}

	public void destroy() { }
	
	public void init(FilterConfig filterConfig) throws ServletException { }
	
}
