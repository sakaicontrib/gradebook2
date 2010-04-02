package org.sakaiproject.gradebook.gwt.sakai.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.sakaiproject.gradebook.gwt.client.exceptions.GradebookCreationException;

@Provider
public class GradebookCreationMapper implements ExceptionMapper<GradebookCreationException> {
	public Response toResponse(GradebookCreationException ex) {
        return Response.status(202).
            entity(ex.getMessage()).
            type("text/plain").
            build();
    }
}
