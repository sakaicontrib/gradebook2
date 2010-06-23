package org.sakaiproject.gradebook.gwt.server;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;

public class ServletWrappingController extends
org.springframework.web.servlet.mvc.ServletWrappingController implements ApplicationContextAware {

	private static final Log log = LogFactory.getLog(ServletWrappingController.class);
	
	// We don't want users to access the GB2 servlet directly
	//private static final String VALID_CONTEXT_PATH = "/xsl-portal/";

	private SessionManager sessionManager = null;
	private ServerConfigurationService configService = null;
	
	private boolean hasEnabledSecurityChecks;
	private String validContextPrefix;
	
	private boolean hosted = false;

	private boolean useControllerBean = false;

	private String controllerBeanName = null;

	private OpenController controllerBean = null;

	// IOC init method
	public void init() {

		/*
		 * This security check can be disabled by setting the Sakai property
		 * gb2.security.enabled=false
		 * This needs to be done in GWT hosted/dev mode via -Dgb2.security=false
		 * By default security check is enabled.
		 * 
		 */

	
		// Getting the security related Sakai properties
		hasEnabledSecurityChecks = configService.getBoolean(AppConstants.ENABLED_SECURITY_CHECKS, true);
		log.info("GB2: security is enabled = " + hasEnabledSecurityChecks);

		// We don't want users to access the GB2 servlet directly
		validContextPrefix = configService.getString(AppConstants.SECURITY_CHECK_CONTEXT_PREFIX_PROPNAME, AppConstants.SECURITY_CHECK_CONTEXT_PREFIX_DEFAULT);
		if(validContextPrefix.lastIndexOf("/") != validContextPrefix.length()-1) {
			validContextPrefix += "/";
		}
		hosted = "hosted".equals(System.getProperty("gb2.mode"));
		
		if (hosted) {
			validContextPrefix = "";
		}
		log.info("GB2: security check context prefix = " + validContextPrefix);
	}

	public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) 
	throws Exception {

	
		// In case the user has disabled the gb2.security setting
		if(!hasEnabledSecurityChecks) {

			if(useControllerBean && controllerBean != null) {
				return controllerBean.submit(request, response, null, null);
			}
			return super.handleRequestInternal(request, response);
		}
		else {

			// First we check if someone is trying to access the servlet directly
			String contextPath = request.getContextPath();
			// The contextPath needs to start with what's defined in validContextPrefix
			// e.g. /portal/ or /xls-portal/, etc
			if(!contextPath.startsWith(validContextPrefix)) {
				
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
				
				if(null == jsessionId) {
					jsessionId = request.getParameter(AppConstants.REQUEST_FORM_FIELD_FORM_TOKEN);
				}

				// Getting the current session and then the sessionId
				Session session = sessionManager.getCurrentSession();

				if(null != session) {

					String sessionId = session.getId();

					// We only continue if the JSESSIONIDs match
					if((jsessionId.startsWith(sessionId)) 
							 || hosted) { // until we can figure out how to sycn client and server in devel mode

						if(useControllerBean && controllerBean != null) {
							return controllerBean.submit(request, response, null, null);
						}
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
	
	

	@Override
	public void afterPropertiesSet() throws Exception {
		if(isUseControllerBean() && controllerBeanName != null) {		
			controllerBean = (OpenController) getApplicationContext().getBean(controllerBeanName, OpenController.class);
		} else{
			super.afterPropertiesSet();
		}
	}

	// IOC setter
	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
	
	// IOC setter
	public void setConfigService(ServerConfigurationService configService) {
		this.configService = configService;
	}
	
	public boolean isUseControllerBean() {
		return useControllerBean;
	}

	public void setUseControllerBean(boolean useControllerBean) {
		this.useControllerBean = useControllerBean;
	}
	
	public String getControllerBeanName() {
		return controllerBeanName;
	}

	public void setControllerBeanName(String controllerBeanName) {
		this.controllerBeanName = controllerBeanName;
	}

	
	
}
