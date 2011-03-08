package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("roster")
public class Roster extends Resource {
    
    @GET @Path("{uid}/{id}")
    @Produces("application/json")
    public String get(@PathParam("uid") String gradebookUid, @PathParam("id") Long gradebookId,
    		@QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset,
    		@QueryParam("sortField") String sortField, @QueryParam("sortDir") String sortDir, 
    		@QueryParam("sectionUuid") String sectionUuid, @QueryParam("searchString") String searchString,
    		@QueryParam("searchField") String searchField, @QueryParam("showWeighted") String showWeighted) {
    	
    	boolean isDescending = sortDir == null || "DESC".equals(sortDir);
    	
    	if (sortField != null && sortField.equalsIgnoreCase("null"))
    		sortField = null;
    	if (sortDir != null && sortDir.equalsIgnoreCase("null"))
    		sortDir = null;
    	if (sectionUuid != null && (sectionUuid.equalsIgnoreCase("null") || sectionUuid.equalsIgnoreCase("ALL")))
    		sectionUuid = null;
    	if (searchString != null && searchString.equalsIgnoreCase("null")) 
    		searchString = null;
    	if(searchField != null && searchField.equalsIgnoreCase("null")) {
    		searchField = null;
    	}
    	
    	boolean isShowWeighted = Boolean.TRUE.toString().equalsIgnoreCase(showWeighted);
    	
    	org.sakaiproject.gradebook.gwt.client.model.Roster roster = 
    		service.getRoster(gradebookUid, gradebookId, limit, offset, sectionUuid, searchString, searchField, sortField, false, isDescending, isShowWeighted);
    
    	return toJson(roster.getLearnerPage(), roster.getTotal());
    }
    
}
