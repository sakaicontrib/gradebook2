package org.sakaiproject.gradebook.gwt.sakai.rest;

import org.sakaiproject.util.ResourceLoader;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.springframework.dao.DataIntegrityViolationException;

@Provider
public class DataIntegrityViolationMapper implements ExceptionMapper<DataIntegrityViolationException> {

	// Set via IoC
	private ResourceLoader i18n;
	
	public Response toResponse(DataIntegrityViolationException arg0) {
		
		return Response.status(500).entity(i18n.getString("dataIntegrityViolationExceptionMessage")).type(AppConstants.CONTENT_TYPE_TEXT_PLAIN).build();
	}

	public void setI18n(ResourceLoader i18n) {
		this.i18n = i18n;
	}
}
