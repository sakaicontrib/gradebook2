package org.sakaiproject.gradebook.gwt.sakai.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;

@Provider
public class InvalidInputMapper implements ExceptionMapper<InvalidInputException> {
    public Response toResponse(InvalidInputException ex) {
        return Response.status(400).
            entity(ex.getMessage()).
            type("text/plain").
            build();
    }
}
