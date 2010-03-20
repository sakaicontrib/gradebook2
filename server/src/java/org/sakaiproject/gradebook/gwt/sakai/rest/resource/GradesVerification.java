package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.sakaiproject.gradebook.gwt.client.exceptions.SecurityException;

@Path("verification/{uid}/{id}")
public class GradesVerification extends Resource {

	@GET 
    @Produces("application/json")
    public String get(@PathParam("uid") String gradebookUid, @PathParam("id") Long gradebookId) 
		throws SecurityException {
		return toJson(service.getGradesVerification(gradebookUid, gradebookId));
	}
	
}
