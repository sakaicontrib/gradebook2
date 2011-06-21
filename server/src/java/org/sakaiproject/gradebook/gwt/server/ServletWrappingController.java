package org.sakaiproject.gradebook.gwt.server;

import java.io.OutputStream;

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
	
	private ServerConfigurationService configService = null;

	private boolean hasEnabledSecurityChecks;
	private String validContextPrefix;

	private boolean hosted = false;

	private boolean useControllerBean = false;

	private String controllerBeanName = null;

	private OpenController controllerBean = null;
	
	private SessionManager sessionManager = null;

	// IOC init method
	public void init() {

		/*
		 * This security check can be disabled by setting the Sakai property
		 * gb2.security.enabled=false
		 * This needs to be done in GWT hosted/dev mode via -Dgb2.security=false
		 * By default security check is enabled.
		 * 
		 */

		// Getting the security related Sakai properties and make sure that they are not null
		if(null == configService) {
			throw new RuntimeException("EXCEPTION: Configuration Service was not initialized");
		}

		hasEnabledSecurityChecks = configService.getBoolean(AppConstants.ENABLED_SECURITY_CHECKS, true);
		log.info("GB2: security is enabled = " + hasEnabledSecurityChecks);

		// We don't want users to access the GB2 servlet directly
		validContextPrefix = configService.getString(AppConstants.SECURITY_CHECK_CONTEXT_PREFIX_PROPNAME, AppConstants.SECURITY_CHECK_CONTEXT_PREFIX_DEFAULT);
		if(validContextPrefix.lastIndexOf("/") != validContextPrefix.length()-1) {
			validContextPrefix += "/";
		}

		// Are we running in GWT hosted/dev mode
		hosted = AppConstants.SYSTEM_PROPERTY_VALUE_HOSTED.equals(System.getProperty(AppConstants.SYSTEM_PROPERTY_KEY_MODE));

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
			if(null != contextPath && !contextPath.startsWith(validContextPrefix)) {

				log.error("ERROR: User tried to access GB2 via : " + contextPath);
				response.setContentType("text/plain");
				response.setStatus(400);
				// GRBK-756 : Using OutputStream because we already use OutputStream in SecurityFilter
				OutputStream out = response.getOutputStream();
				out.write("Security Exception".getBytes());
				return null;
			}


			// In case gb2.security is enabled: validating GB2_TOKEN
			Session session = sessionManager.getCurrentSession();
			
			if(null != session) {

				// X-XSRF-Cookie is a client side defined cookie : RestBuilder.java
				String gb2ClientToken = request.getHeader(AppConstants.X_XSRF_COOKIE);

				if(null == gb2ClientToken) {
					
					gb2ClientToken = request.getParameter(AppConstants.REQUEST_FORM_FIELD_FORM_TOKEN);
				}

				// DEV mode requires hosted switch
				if ((null != gb2ClientToken) || hosted) {

					String gb2ServerToken = (String)session.getAttribute(AppConstants.GB2_TOKEN);

					// We only continue if the GB2_TOKENs match
					// DEV mode requires hosted switch
					if(hosted || (null != gb2ServerToken && gb2ServerToken.equals(gb2ClientToken))) {

						if(useControllerBean && controllerBean != null) {
							return controllerBean.submit(request, response, null, null);
						}
						
						return super.handleRequestInternal(request, response);
					}
					// else case is handled at the end of method
				}
				else {

					log.error("ERROR: GB2TOKEN is null");
					response.setContentType("text/plain");
					response.setStatus(500);
					// GRBK-756 : Using OutputStream because we already use OutputStream in SecurityFilter
					OutputStream out = response.getOutputStream();
					out.write("ERROR: GB2TOKEN is null".getBytes());
					return null;
				}
			}
			else {

				log.error("ERROR : HttpSession is null");
				response.setContentType("text/plain");
				response.setStatus(500);
				// GRBK-756 : Using OutputStream because we already use OutputStream in SecurityFilter
				OutputStream out = response.getOutputStream();
				out.write("ERROR : HttpSession is null".getBytes());
				return null;
			}
		}

		// Handling the case where the GB2TOKENs don't match
		log.error("ERROR: " + AppConstants.X_XSRF_COOKIE + " violation");
		response.setContentType("text/plain");
		response.setStatus(400);
		// GRBK-756 : Using OutputStream because we already use OutputStream in SecurityFilter
		OutputStream out = response.getOutputStream();
		out.write("Security Exception".getBytes());
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
	public void setConfigService(ServerConfigurationService configService) {
		this.configService = configService;
	}
	
	// IOC setter
	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
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
