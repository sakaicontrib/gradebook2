package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.sun.jersey.core.util.Base64;


@Path("gradeevent")
public class GradeEvent extends Resource {

	@GET @Path("{studentUid}/{itemId}")
    @Produces("application/json")
    public String get(@PathParam("studentUid") String studentUid, @PathParam("itemId") Long itemId) {
		List<org.sakaiproject.gradebook.gwt.client.model.GradeEvent> list = 
			service.getGradeEvents(itemId, Base64.base64Decode(studentUid));
		return toJson(list, list.size());
	}
	
}
