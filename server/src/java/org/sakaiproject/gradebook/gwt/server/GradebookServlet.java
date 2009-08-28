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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.servlets.DefaultServlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.client.model.AuthModel;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class GradebookServlet extends DefaultServlet {

	private static final Log log = LogFactory.getLog(GradebookServlet.class);
	
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_PAGE = "GradebookApplication.html";
	private static final String RELATIVE_PREFIX = "/org.sakaiproject.gradebook.gwt.GradebookApplication/";
	
	protected void doGet(HttpServletRequest request,
			 			 HttpServletResponse response)
		throws IOException, ServletException 
	{
		String relativePath = getRelativePath(request);	  
		
		if (relativePath.equals("/")) {
			String relativePrefix = RELATIVE_PREFIX;
			String defaultPage = DEFAULT_PAGE;

			WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());
			
			Gradebook2Service service = (Gradebook2Service)context.getBean("org.sakaiproject.gradebook.gwt.sakai.Gradebook2Service");
			
			StringBuilder url = new StringBuilder();
			url.append(request.getRequestURI()).append(relativePrefix).append(defaultPage);
			
			if (service != null) {
				AuthModel authModel = service.getAuthorization();
			
				if (authModel != null) {
					log.info("AuthModel" + authModel.toString());
					url.append(authModel.toString());
				}
				
			} else {
				log.info("Unable to grab Gradebook2Service");
			}
			
			response.sendRedirect(url.toString());
			
			return;
		}	
			
		serveResource(request, response, true);
	}

}
