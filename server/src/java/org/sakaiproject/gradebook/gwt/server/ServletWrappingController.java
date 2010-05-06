package org.sakaiproject.gradebook.gwt.server;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.springframework.web.servlet.ModelAndView;

public class ServletWrappingController extends
org.springframework.web.servlet.mvc.ServletWrappingController {

	private static final Log log = LogFactory.getLog(ServletWrappingController.class);
	
	// We don't want users to access the GB2 servlet directly
	private static final String VALID_CONTEXT_PATH = "/xsl-portal/";

	private SessionManager sessionManager = null;

	public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) 
	throws Exception {

		/*
		 * This security check can be disabled by setting the java property
		 * -Dgb2.security=false
		 * This needs to be done in GWT hosted/dev mode
		 * By default security check is enabled.
		 * 
		 */

		// System property to turn on/off the session validation
		String securityProperty = System.getProperty("gb2.security");

		// In case the user has disabled the gb2.security setting
		if("false".equals(securityProperty)) {

			return super.handleRequestInternal(request, response);
		}
		else {

			// First we check if someone is trying to access the servlet directly
			String contextPath = request.getContextPath();
			// The contextPath needs to start with /portal/tool/
			if(!contextPath.startsWith(VALID_CONTEXT_PATH)) {
				
				log.error("ERROR: User tried to access GB2 via : " + contextPath);
				response.setContentType("text/plain");
		        response.setStatus(400);
		        PrintWriter writer = response.getWriter();
		        writer.print("Security Exception");
		        return null;
			}
				
			
			// In case gb2.security is enabled: validating JSESSIONID

			// Make sure that the session manager got injected
			if(sessionManager != null) {

				// X-XSRF-Cookie is a client side defined cookie : RestBuilder.java
				String jsessionId = request.getHeader("X-XSRF-Cookie");

				// Getting the current session and then the sessionId
				Session session = sessionManager.getCurrentSession();

				if(null != session) {

					String sessionId = session.getId();

					// We only continue if the JSESSIONIDs match
					if((jsessionId.startsWith(sessionId))) {

						return super.handleRequestInternal(request, response);
					}
					// else case is handled at the end of method
				}
				else {
					
					log.error("ERROR: SESSIONID is null");
					response.setContentType("text/plain");
					response.setStatus(500);
					PrintWriter writer = response.getWriter();
					writer.print("ERROR: SESSIONID is null");
					return null;
				}
			}
			else {
				
				log.error("ERROR : SessinManager is null");
				response.setContentType("text/plain");
		        response.setStatus(500);
		        PrintWriter writer = response.getWriter();
		        writer.print("ERROR : SessinManager is null");
		        return null;
			}
		}

		// Handling the case where the JSESSIONIDs don't match
		log.error("ERROR: X-XSRF-Cookie violation");
		response.setContentType("text/plain");
        response.setStatus(400);
        PrintWriter writer = response.getWriter();
        writer.print("Security Exception");
        return null;
	}

	// IOC setter
	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
}
