package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.exceptions.SecurityException;
import org.sakaiproject.gradebook.gwt.server.model.PermissionImpl;

@Path("permission")
public class Permission extends Resource {

	@POST @Path("{uid}/{id}")
	@Consumes("application/json")
	@Produces("application/json")
	public String create(@PathParam("uid") String gradebookUid, @PathParam("id") Long gradebookId, 
			String model) throws InvalidInputException, SecurityException {
		
		Map<String,Object> map = fromJson(model, Map.class);
		org.sakaiproject.gradebook.gwt.client.model.Permission result = 
			service.createPermission(gradebookUid, gradebookId, new PermissionImpl(map));
		
		return toJson(result);
	}
		
	@DELETE @Path("{uid}")
	@Consumes("application/json")
	@Produces("application/json")
	public String remove(@PathParam("uid") String gradebookUid, String model) throws SecurityException {
		Map<String,Object> map = fromJson(model, Map.class);
		org.sakaiproject.gradebook.gwt.client.model.Permission result = 
			service.deletePermission(gradebookUid, new PermissionImpl(map));
		
		return toJson(result);
	}
	
}
