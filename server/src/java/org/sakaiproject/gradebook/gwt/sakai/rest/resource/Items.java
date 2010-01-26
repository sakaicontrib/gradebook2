package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.sakaiproject.gradebook.gwt.client.AppConstants;

@Path("/gradebook/rest/items")
public class Items extends Resource {

	@GET @Path("{uid}/{id}")
	@Produces("application/json")
	public String getList(@PathParam("uid") String gradebookUid, @PathParam("id") Long gradebookId, 
			@QueryParam("type") String itemType) {
	
		List<Map<String,Object>> list = service.getItems(gradebookUid, gradebookId, itemType);
		
		return toJson(AppConstants.ITEMS_ROOT, list, list.size());
	}
	
}
