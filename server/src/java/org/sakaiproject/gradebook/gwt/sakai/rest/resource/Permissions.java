package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;


@Path("/gradebook/rest/permissions/{uid}/{id}/{graderId}")
public class Permissions extends Resource {

	@GET 
    @Produces("application/json")
    public String get(@PathParam("uid") String gradebookUid, @PathParam("id") Long gradebookId,
    		@PathParam("graderId") String graderId) {
		List<Map<String,Object>> list = service.getPermissions(gradebookUid, gradebookId, graderId);
		return toJson(list, list.size());
	}
	
}
