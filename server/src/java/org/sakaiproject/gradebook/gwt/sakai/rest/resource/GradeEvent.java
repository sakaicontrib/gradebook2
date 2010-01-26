package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;


@Path("/gradebook/rest/gradeevent")
public class GradeEvent extends Resource {

	@GET @Path("{studentUid}/{itemId}")
    @Produces("application/json")
    public String get(@PathParam("studentUid") String studentUid, @PathParam("itemId") Long itemId) {
		List<Map<String,Object>> list = service.getGradeEvents(itemId, studentUid);
		return toJson(list, list.size());
	}
    	
}
