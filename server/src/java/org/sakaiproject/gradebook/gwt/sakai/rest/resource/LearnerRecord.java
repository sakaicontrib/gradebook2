package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.xml.bind.JAXBElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.sakai.rest.model.GradeRecord;

@Path("/gradebook/rest/learner")
public class LearnerRecord extends Resource {

	private static final Log log = LogFactory.getLog(LearnerRecord.class);
		
	@PUT @Path("comment/{gradebookUid}/{itemId}/{studentUid}")
	@Consumes({"application/xml", "application/json"})
	public String assignComment(@PathParam("gradebookUid") String gradebookUid,
			@PathParam("itemId") String itemId,
			@PathParam("studentUid") String studentUid,
			JAXBElement<GradeRecord> jaxbAction) throws InvalidInputException {
		
		if (log.isDebugEnabled()) 
			log.debug("assignComment " + gradebookUid + " " + itemId + " " + studentUid);
		
		GradeRecord action = jaxbAction.getValue();
		Learner map = service.assignComment(itemId, studentUid, action.getStringValue());
		
		return toJson(map);
	}
	
	@PUT @Path("numeric/{gradebookUid}/{itemId}/{studentUid}")
	@Consumes({"application/xml", "application/json"})
	public String assignNumericScore(@PathParam("gradebookUid") String gradebookUid,
			@PathParam("itemId") String itemId,
			@PathParam("studentUid") String studentUid,
			JAXBElement<GradeRecord> jaxbAction) throws InvalidInputException {
		
		if (log.isDebugEnabled()) 
			log.debug("assignNumericScore " + gradebookUid + " " + itemId + " " + studentUid);
		
		GradeRecord action = jaxbAction.getValue();

		Learner map = service.assignScore(gradebookUid, 
				studentUid, itemId, action.getValue(), 
				action.getPreviousValue());

		return toJson(map);
	}
	
	@PUT @Path("string/{gradebookUid}/{itemId}/{studentUid}")
	@Consumes({"application/xml", "application/json"})
	public String assignStringScore(@PathParam("gradebookUid") String gradebookUid,
			@PathParam("itemId") String itemId,
			@PathParam("studentUid") String studentUid,
			JAXBElement<GradeRecord> jaxbAction) throws InvalidInputException {
		
		if (log.isDebugEnabled()) 
			log.debug("assignStringScore " + gradebookUid + " " + itemId + " " + studentUid);
		
		GradeRecord action = jaxbAction.getValue();

		Learner map = service.assignScore(gradebookUid, 
					studentUid, itemId, action.getStringValue(), 
					action.getPreviousStringValue());
		
		return toJson(map);
	}
	
}
