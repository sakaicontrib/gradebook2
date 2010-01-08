package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.xml.bind.JAXBElement;

import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.sakai.rest.model.GradeRecord;

@Path("/gradebook/rest/item")
public class Item {
	
	@PUT @Path("{gradebookUid}")
	@Consumes({"application/xml", "application/json"})
	public String create(@PathParam("gradebookUid") String gradebookUid,
			JAXBElement<GradeRecord> jaxbAction) throws InvalidInputException {
	
		return null;
	}
	
}
