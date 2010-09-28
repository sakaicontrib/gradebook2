package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.sakaiproject.gradebook.gwt.client.exceptions.SecurityException;

import com.sun.jersey.core.util.Base64;

@Path("permissions/{uid}/{id}/{graderId}")
public class Permissions extends Resource {

	@GET 
    @Produces("application/json")
    public String get(@PathParam("uid") String gradebookUid, @PathParam("id") Long gradebookId,
    		@PathParam("graderId") String graderId) throws SecurityException {
		List<org.sakaiproject.gradebook.gwt.client.model.Permission> list = 
			service.getPermissions(gradebookUid, gradebookId, Base64.base64Decode(graderId));
		return toJson(list, list.size());
	}
	
}
