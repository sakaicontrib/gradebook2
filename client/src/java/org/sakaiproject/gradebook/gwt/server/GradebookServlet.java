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

public class GradebookServlet extends DefaultServlet {

	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_PAGE = "GradebookApplication.html";
	private static final String RELATIVE_PREFIX = "/org.sakaiproject.gradebook.gwt.GradebookApplication/";
	
	protected void doGet(HttpServletRequest request,
			 			 HttpServletResponse response)
		throws IOException, ServletException 
	{
		String relativePath = getRelativePath(request);	  
		
		if (relativePath.equals("/")) {
			String relativePrefix = RELATIVE_PREFIX; // getServletConfig().getInitParameter("relativePrefix");
			String defaultPage = DEFAULT_PAGE; // getServletConfig().getInitParameter("defaultPage");
					
			response.sendRedirect(request.getRequestURI() + relativePrefix + defaultPage);
			
			return;
		}	
			
		serveResource(request, response, true);
	}

}
