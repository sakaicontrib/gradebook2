package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.sakaiproject.gradebook.gwt.client.AppConstants;

@Path("/gradebook/rest/history/{uid}/{id}")
public class History extends Resource {

	@GET 
    @Produces("application/json")
    public String get(@PathParam("uid") String gradebookUid, @PathParam("id") Long gradebookId,
    		@QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset,
    		@QueryParam("sortField") String sortField, @QueryParam("sortDir") String sortDir) {
    
		List<Map<String,Object>> list = service.getHistory(gradebookUid, gradebookId, offset, limit);
		
		return toJson(AppConstants.HISTORY_ROOT, list, list.size());
	}
	
}
