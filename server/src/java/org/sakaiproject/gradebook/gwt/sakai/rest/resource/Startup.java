/**********************************************************************************
 *
 * Copyright (c) 2008, 2009, 2010, 2011 The Regents of the University of California
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

package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.sakaiproject.gradebook.gwt.client.exceptions.GradebookCreationException;
import org.sakaiproject.gradebook.gwt.client.exceptions.SecurityException;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationSetup;
import org.sakaiproject.gradebook.gwt.client.model.FinalGradeSubmissionStatus;

/*
 * This is a new REST point, combining the deprecated Authorization.java and Application.java REST points
 * The order in which the calls are made bellow is important and should not be changed.
 * 
 * @since 1.5.0
 */
@Path("startup")
public class Startup extends Resource {

	@GET
    @Produces("application/json")
    public String get() throws GradebookCreationException {
		
		// The following two calls need to be call in the order as they appear
		String authorizationDetails = service.getAuthorizationDetails();
		ApplicationSetup applicationSetup = service.getApplicationSetup();
		
		applicationSetup.setAuthorizationDetails(authorizationDetails);
		
		return toJson(applicationSetup);
	}
	
	/*
	 * @since 1.7.0
	 * 
	 * Check final grade submission status
	 */
	@GET @Path("/fgs/{uid}")
	@Produces("application/json")
	public String getFinalGradeSubmissionStatus(@PathParam("uid") String gradebookUid) throws SecurityException {

		FinalGradeSubmissionStatus status = service.getFinalGradeSubmissionStatus(gradebookUid);
		
		return toJson(status);
	}
}
