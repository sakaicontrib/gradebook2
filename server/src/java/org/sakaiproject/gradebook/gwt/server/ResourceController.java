package org.sakaiproject.gradebook.gwt.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ComponentService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class ResourceController implements Controller {

	private static final Log log = LogFactory.getLog(ResourceController.class);
	
	private static final String DEFAULT_PAGE = "GradebookApplication.html";
	private static final String RELATIVE_PREFIX = "/";
	
	private Gradebook2ComponentService service;
	
	public Gradebook2ComponentService getService() {
		return service;
	}

	public void setService(Gradebook2ComponentService service) {
		this.service = service;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		log.debug("Received request for main gb2 page");
		
		StringBuilder url = new StringBuilder();
		url.append(request.getRequestURI()).append(RELATIVE_PREFIX).append(DEFAULT_PAGE);
		
		if (service != null) {
			String authDetails = service.getAuthorizationDetails();
		
			if (authDetails != null) {
				Cookie cookie = new Cookie(AppConstants.AUTH_COOKIE_NAME, authDetails);
				response.addCookie(cookie);
			}
		}
		
		try {
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
					
					if (tokens.length > 3) {
						String version = tokens[tokens.length - 3];
						log.info("Version is: " + version);
						Cookie cookie = new Cookie(AppConstants.VERSION_COOKIE_NAME, version);
						response.addCookie(cookie);
					}
					
				}
				inputStream.close();
			}
			
		} catch (Exception e) {
			log.warn("Unable to read version file", e);
		} 
		
		
		response.sendRedirect(url.toString());
		
		return null;
	}

}
