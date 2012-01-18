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
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.util.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

// This code was contributed by CARET to Sakai, it' modified below
// for use in Gradebook2
public class WebAppToolServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(WebAppToolServlet.class);

	private static String version = null;
	
	// Set via IoC
	@Autowired
	private ResourceLoader rb = null;
	
	@Autowired
	private SessionManager sessionManager = null;
	
	
	/**
	 * This init parameter should contain an url to the welcome page
	 */
	public static final String FIRST_PAGE = "main-page";

	@Override
	public void init(ServletConfig config) throws ServletException {
		
		super.init(config);
	    SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
	}

	
	protected void service(final HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		// GRBK-908
		if(null == sessionManager) {
			
			log.error("ERROR: SessionManager is null");
		}
		else {
			
			String currentSessionId = sessionManager.getCurrentSession().getId();
			String currentToken = (String) sessionManager.getCurrentSession().getAttribute(AppConstants.GB2_TOKEN);
			Cookie cookie = getCookie(request.getCookies());
			
			/*
			 *  Creating a new GB2 TOKEN if:
			 *  1: During bootstrapping time, fist access
			 *  2: User deleted cookies
			 */
			if(((null == currentToken || "".equals(currentToken)) && null != currentSessionId) || 
				(null == cookie && null != currentSessionId)) {
				
				String hexCurrentSessionId = DigestUtils.md5Hex(currentSessionId.getBytes());
				
				String uuid = java.util.UUID.randomUUID().toString();
				String gb2Token = new StringBuilder(uuid).append("-").append(hexCurrentSessionId).toString();
				
				sessionManager.getCurrentSession().setAttribute(AppConstants.GB2_TOKEN, gb2Token);
				
				// If the cookie exists, we just change its value, otherwise we create a new one
				if(null != cookie) {
					
					cookie.setValue(gb2Token);
					cookie.setPath("/");
					cookie.setMaxAge(-1);
				}
				else {
					
					cookie = new Cookie(AppConstants.GB2_TOKEN, gb2Token);
					cookie.setPath("/");
					cookie.setMaxAge(-1);
				}
				
				response.addCookie(cookie);
			}
		}
		
		final String contextPath = request.getContextPath();
		request.setAttribute(Tool.NATIVE_URL, Tool.NATIVE_URL);
		HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {
			public String getContextPath() {
				return contextPath;
			}
		};

		if (request.getPathInfo() == null
				&& getInitParameter(FIRST_PAGE) != null
				&& !getInitParameter(FIRST_PAGE).equals("/")) {

			String uri = new StringBuilder().append(contextPath).append(getInitParameter(FIRST_PAGE)).toString();

			addVersionAsCookie(response, contextPath);
			// Set locale preferences for user
			uri = uri+"?locale="+rb.getLocale();
			
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
		
		InputStream inputStream = null;
		
		try {
			if (version != null)
				return;
			
			inputStream = this.getClass().getResourceAsStream("VERSION.txt");
			
			if (inputStream != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				WebAppToolServlet.version = reader.readLine();
				log.info("Setting Gradebook2 version = " + WebAppToolServlet.version);
			}
			
		} catch (Exception e) {
			log.warn("Unable to read version file", e);
		} 
		finally {

			if(null != inputStream) {
				
				try {
					
					inputStream.close();
					
				} catch (IOException e) {
					log.warn("Unable to close InputStream", e);
				}
			}
		}
	}
	
	/*
	 * Helper method that iterates over all the cookies and 
	 * returns the cookie that matches the GB2_TOKEN name
	 */
	private Cookie getCookie(Cookie[] cookies) {
		
		if(null == cookies) {
			
			return null;
		}
		else {
			
			for(Cookie cookie : cookies) {
				
				if(AppConstants.GB2_TOKEN.equals(cookie.getName())) {
					
					return cookie;
				}
			}
		}
		
		return null;
	}
	
	// Spring IoC
	public void setSessionManager(SessionManager sessionManager) {
		
		this.sessionManager = sessionManager;
	}


	public void setRb(ResourceLoader rb) {
		this.rb = rb;
	}
}
