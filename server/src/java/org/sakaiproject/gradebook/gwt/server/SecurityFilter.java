/**********************************************************************************
 *
 * $Id:$
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

	private static final String SCRIPT_REGEX = "((<[\\s\\/]*script\\b[^>]*>)([^>]*)(<\\/script>))";
	private static final String GWT_RPC_CONTENT_TYPE = "text/x-gwt-rpc";
	private static final Pattern pattern = Pattern.compile(SCRIPT_REGEX);
	private static final int HTTP_500 = 500;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		String contentType = request.getContentType();

		if(null != contentType && contentType.startsWith(GWT_RPC_CONTENT_TYPE)) {

			SecurityRequestWrapper securityRequestWrapper = new SecurityRequestWrapper((HttpServletRequest) request);

			String body = securityRequestWrapper.getBody();

			Matcher matcher = pattern.matcher(body);
			if(matcher.find()) {
				// Instead of throwing an exception here, we just set the
				// response status and return
				((HttpServletResponse) response).setStatus(HTTP_500);
				return;
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
