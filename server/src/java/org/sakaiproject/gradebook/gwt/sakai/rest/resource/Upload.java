package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.server.model.UploadImpl;

@Path("upload")
public class Upload extends Resource {

	@PUT @Path("{uid}/{id}")
	@Consumes({"application/json"})
	public String update(@PathParam("uid") String gradebookUid, @PathParam("id") Long gradebookId, 
			String model) throws InvalidInputException {
		
		Map<String,Object> map = fromJson(model, Map.class);
		org.sakaiproject.gradebook.gwt.client.model.Upload result = 
			service.upload(gradebookUid, gradebookId, new UploadImpl(map), false);
		
		return toJson(result);
	}
	
}
