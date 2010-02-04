package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/gradebook/rest/roster")
public class Roster extends Resource {
    
    @GET @Path("{uid}/{id}")
    @Produces("application/json")
    public String get(@PathParam("uid") String gradebookUid, @PathParam("id") Long gradebookId,
    		@QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset,
    		@QueryParam("sortField") String sortField, @QueryParam("sortDir") String sortDir, 
    		@QueryParam("sectionUuid") String sectionUuid, @QueryParam("searchString") String searchString) {
    	
    	boolean isDescending = sortDir == null || "DESC".equals(sortDir);
    	
    	org.sakaiproject.gradebook.gwt.client.model.Roster roster = 
    		service.getRoster(gradebookUid, gradebookId, limit, offset, sectionUuid, searchString, sortField, false, isDescending);
    
    	return toJson(roster.getLearnerPage(), roster.getTotal());
    }
    
}
