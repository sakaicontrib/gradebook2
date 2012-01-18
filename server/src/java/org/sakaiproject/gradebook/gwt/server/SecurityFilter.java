/**********************************************************************************
 *
 * $Id$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2008, 2009 The Regents of the University of California
 *
 * Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.gradebook.gwt.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SecurityFilter implements Filter {

	private static final String SCRIPT_REGEX = "(<[\\s\\/]*script\\b[^>]*>)|(<\\/script>)";
	// GRBK-517 : TPA : Changed the content type from gwt-rpc to "application/json"
	private static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";
	private static final Pattern pattern = Pattern.compile(SCRIPT_REGEX, Pattern.CASE_INSENSITIVE);
	private static final int HTTP_500 = 500;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		String contentType = request.getContentType();

		if(null != contentType && contentType.startsWith(APPLICATION_JSON_CONTENT_TYPE)) {

			SecurityRequestWrapper securityRequestWrapper = new SecurityRequestWrapper((HttpServletRequest) request);

			OutputStream out = response.getOutputStream();
			SecurityResponseWrapper securityResponseWrapper = new SecurityResponseWrapper((HttpServletResponse)response);

			String body = securityRequestWrapper.getBody();

			Matcher requestMatcher = pattern.matcher(body);

			if(requestMatcher.find()) {
				// Instead of throwing an exception here, we just set the
				// response status and return
				((HttpServletResponse) response).setStatus(HTTP_500);
				return;
			}

			chain.doFilter(securityRequestWrapper, securityResponseWrapper);

			// GRBK-697 : Dealing with response
			byte[] responseData = securityResponseWrapper.getData();
			String responseString = new String(responseData);
			Matcher responseMatcher = pattern.matcher(responseString);
			
			if(responseMatcher.find()) {
				
				String escapedResponseString = responseMatcher.replaceAll("");
				responseData = escapedResponseString.getBytes();
				response.setContentLength(escapedResponseString.length());
				out.write(responseData);
				out.flush();
				out.close();
			}
			else {
				
				out.write(responseData);
				out.flush();
				out.close();
			}

		} else {

			chain.doFilter(request, response);
		}
	}

	public void destroy() { }

	public void init(FilterConfig filterConfig) throws ServletException { }

}
