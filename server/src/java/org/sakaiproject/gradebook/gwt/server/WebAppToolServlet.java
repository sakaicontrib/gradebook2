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
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

// This code was contributed by CARET to Sakai, it' modified below
// for use in Gradebook2
public class WebAppToolServlet extends HttpServlet {

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

			addVersionAsCookie(response, uri);
			
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
			
			InputStream inputStream = this.getClass().getResourceAsStream("version.xml");
			
			if (inputStream != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				
				SvnInfoParserHelper helper = new SvnInfoParserHelper();
				XMLReader parser = XMLReaderFactory.createXMLReader();
				parser.setContentHandler(helper);
				parser.parse(new InputSource(reader));
				WebAppToolServlet.version = helper.getVersion();
			}
			
			/*
			InputStream inputStream = this.getClass().getResourceAsStream("version.txt");
			if (inputStream != null) {
				StringBuilder builder = new StringBuilder();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				String line = null;

				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}

				reader.close();

				String result = builder.toString();

				if (result != null) {
					String[] tokens = result.split("/");

					if (tokens.length > 2) {
						String version = tokens[tokens.length - 2];
						if (log.isDebugEnabled())
							log.debug("Version is: " + version);
						
					}

				}
				inputStream.close();
			}*/

		} catch (Exception e) {
			log.warn("Unable to read version file", e);
		} 
	}
	
	private class SvnInfoParserHelper extends DefaultHandler {
		
		private boolean isReading = false;
		private String url;

		public void startElement(String nsURI, String strippedName,
						String tagName, Attributes attributes) throws SAXException {
			if (tagName.equalsIgnoreCase("url"))
				isReading = true;
		}

		public void characters(char[] ch, int start, int length) {
			if (isReading) {
				url = new String(ch, start, length);
				isReading = false;
			}
		}

		public String getVersion() {
			if (url != null && !url.isEmpty()) {
				String[] tokens = url.split("/");
				if (tokens.length > 2) {
					return tokens[tokens.length - 2];
				}
			}
			return null;
		}
	}

}
