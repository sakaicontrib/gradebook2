package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.sakaiproject.gradebook.gwt.client.exceptions.SecurityException;

@Path("/gradebook/rest/statistics/{uid}/{id}")
public class Statistics extends Resource {

	@GET 
    @Produces("application/json")
    public String get(@PathParam("uid") String gradebookUid, @PathParam("id") Long gradebookId) 
	throws SecurityException {
		List<org.sakaiproject.gradebook.gwt.client.model.Statistics> list = 
			service.getStatistics(gradebookUid, gradebookId, null);
		return toJson(list, list.size());
	}
	
	@GET @Path("{studentUid}")
    @Produces("application/json")
    public String get(@PathParam("uid") String gradebookUid, @PathParam("id") Long gradebookId,
    		@PathParam("studentUid") String studentUid) 
	throws SecurityException {
		List<org.sakaiproject.gradebook.gwt.client.model.Statistics> list = 
			service.getStatistics(gradebookUid, gradebookId, studentUid);
		return toJson(list, list.size());
	}
	
}
