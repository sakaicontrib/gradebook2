package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("items")
public class Items extends Resource {

	@GET @Path("{uid}/{id}")
	@Produces("application/json")
	public String getList(@PathParam("uid") String gradebookUid, @PathParam("id") Long gradebookId, 
			@QueryParam("type") String itemType) {
		List<org.sakaiproject.gradebook.gwt.client.model.Item> list = 
			service.getItems(gradebookUid, gradebookId, itemType);
		return toJson(list, list.size());
	}
	
}
