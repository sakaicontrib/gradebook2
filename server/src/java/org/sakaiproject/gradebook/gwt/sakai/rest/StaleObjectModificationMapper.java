package org.sakaiproject.gradebook.gwt.sakai.rest;

import org.sakaiproject.util.ResourceLoader;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.sakaiproject.gradebook.gwt.client.AppConstants;

import org.sakaiproject.service.gradebook.shared.StaleObjectModificationException;

@Provider
public class StaleObjectModificationMapper implements ExceptionMapper<StaleObjectModificationException> {

	// Set via IoC
	private ResourceLoader i18n;
	
	public Response toResponse(StaleObjectModificationException some) {
		
		/*
		 * Setting response to 409 : Conflict
		 * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.10
		 */
		return Response.status(409).entity(i18n.getString("staleObjectModificationExceptionMessage")).type(AppConstants.CONTENT_TYPE_TEXT_PLAIN).build();
	}

	public void setI18n(ResourceLoader i18n) {
		this.i18n = i18n;
	}
}

