package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.server.model.GradeRecord;

import com.sun.jersey.core.util.Base64;

@Path("learner")
public class LearnerRecord extends Resource {

	@PUT @Path("comment/{gradebookUid}/{itemId}/{studentUid}")
	@Consumes("application/json")
	@Produces("application/json")
	public String assignComment(@PathParam("gradebookUid") String gradebookUid,
			@PathParam("itemId") String itemId,
			@PathParam("studentUid") String studentUid,
			String data) throws InvalidInputException {
		
		GradeRecord action = fromJson(data, GradeRecord.class);
		Learner map = service.assignComment(itemId, Base64.base64Decode(studentUid), action.getStringValue());
		
		return toJson(map);
	}
	
	@PUT @Path("excuse/{gradebookUid}/{itemId}/{studentUid}")
	@Consumes("application/json")
	@Produces("application/json")
	public String assignExcused(@PathParam("gradebookUid") String gradebookUid,
			@PathParam("itemId") String itemId,
			@PathParam("studentUid") String studentUid,
			String data) throws InvalidInputException {
		
		GradeRecord action = fromJson(data, GradeRecord.class);
		Learner map = service.assignExcused(itemId, Base64.base64Decode(studentUid), action.getBooleanValue());
		
		return toJson(map);
	}
	
	@PUT @Path("numeric/{gradebookUid}/{itemId}/{studentUid}")
	@Consumes("application/json")
	@Produces("application/json")
	public String assignNumericScore(@PathParam("gradebookUid") String gradebookUid,
			@PathParam("itemId") String itemId,
			@PathParam("studentUid") String studentUid,
			String data) throws InvalidInputException {
		
		GradeRecord action = fromJson(data, GradeRecord.class);

		Learner map = service.assignScore(gradebookUid, 
				Base64.base64Decode(studentUid), itemId, action.getValue(), 
				action.getPreviousValue());

		return toJson(map);
	}
	
	@PUT @Path("string/{gradebookUid}/{itemId}/{studentUid}")
	@Consumes("application/json")
	@Produces("application/json")
	public String assignStringScore(@PathParam("gradebookUid") String gradebookUid,
			@PathParam("itemId") String itemId,
			@PathParam("studentUid") String studentUid,
			String data) throws InvalidInputException {
		
		GradeRecord action = fromJson(data, GradeRecord.class);

		Learner map = service.assignScore(gradebookUid, 
				Base64.base64Decode(studentUid), itemId, action.getStringValue(), 
					action.getPreviousStringValue());
		
		return toJson(map);
	}
	
}
