/**********************************************************************************
 *
 * $Id:$
 *
 ***********************************************************************************
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.tool.api.Tool;

// This code was contributed by CARET to Sakai, it' modified below
// for use in Gradebook2
public class WebAppToolServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(WebAppToolServlet.class);

	private static String version = null;
	
	/**
	 * This init parameter should contain an url to the welcome page
	 */
	public static final String FIRST_PAGE = "main-page";

	protected void service(final HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		final String contextPath = request.getContextPath();
		request.setAttribute(Tool.NATIVE_URL, Tool.NATIVE_URL);
		HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(
				request) {
			public String getContextPath() {
				return contextPath;
			}
		};

		if (request.getPathInfo() == null
				&& getInitParameter(FIRST_PAGE) != null
				&& !getInitParameter(FIRST_PAGE).equals("/")) {

			String uri = new StringBuilder().append(contextPath).append(getInitParameter(FIRST_PAGE)).toString();

			addVersionAsCookie(response, contextPath);
			
			// Do redirect to first-page
			response.sendRedirect(uri);
		} else if (request.getPathInfo() == null && !request.getRequestURI().endsWith("/")) {
			String uri = new StringBuilder().append(contextPath).append("/").toString();

			// we should do the default redirect to "/"
			response.sendRedirect(uri);
		} else if (request.getPathInfo() != null
				&& (request.getPathInfo().startsWith("/WEB-INF/") || request
						.getPathInfo().equals("/WEB-INF"))) {
			String uri = new StringBuilder().append(contextPath).append("/").toString();

			// Can't allow people to see WEB-INF
			response.sendRedirect(uri);
		} else {
			// otherwise do the dispatch
			RequestDispatcher dispatcher;
			if (request.getPathInfo() == null) {
				dispatcher = request.getRequestDispatcher("");
			} else {
				dispatcher = request.getRequestDispatcher(request.getPathInfo());
			}

			dispatcher.forward(wrappedRequest, response);
		}

	}


	private void addVersionAsCookie(HttpServletResponse response, String uri) {
		if (version == null) {
			readVersionFromFile();
		}
		if (version != null) {
			Cookie cookie = new Cookie(AppConstants.VERSION_COOKIE_NAME, version);
			cookie.setMaxAge(-1);
			cookie.setPath(uri);
			response.addCookie(cookie);
		}
	}
	
	private synchronized void readVersionFromFile() {
		try {
			if (version != null)
				return;
			
			InputStream inputStream = this.getClass().getResourceAsStream("VERSION.txt");
			
			if (inputStream != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				WebAppToolServlet.version = reader.readLine();
				log.info("Setting Gradebook2 version = " + WebAppToolServlet.version);
			}
			
		} catch (Exception e) {
			log.warn("Unable to read version file", e);
		} 
	}
}
