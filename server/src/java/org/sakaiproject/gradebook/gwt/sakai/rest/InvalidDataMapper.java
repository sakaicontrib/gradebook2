package org.sakaiproject.gradebook.gwt.sakai.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidDataException;

public class InvalidDataMapper implements ExceptionMapper<InvalidDataException> {

	public Response toResponse(InvalidDataException ex) {
		String message = ex.getMessage();
		if(null == message || "".equals(message)) {
			
			return Response.status(400).entity("InvalidDataException: no exception message was set").type("text/plain").build();
		}
		else {
			
			return Response.status(400).entity(ex.getMessage()).type("text/plain").build();
		}
	}
}
