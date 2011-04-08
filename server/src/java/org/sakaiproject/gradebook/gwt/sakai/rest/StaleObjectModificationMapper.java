package org.sakaiproject.gradebook.gwt.sakai.rest;

import java.util.ResourceBundle;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.sakaiproject.gradebook.gwt.client.AppConstants;

import org.sakaiproject.service.gradebook.shared.StaleObjectModificationException;

@Provider
public class StaleObjectModificationMapper implements ExceptionMapper<StaleObjectModificationException> {

	private static ResourceBundle i18n = ResourceBundle.getBundle("org.sakaiproject.gradebook.gwt.client.I18nConstants");
	
	@Override
	public Response toResponse(StaleObjectModificationException some) {
		
		/*
		 * Setting response to 409 : Conflict
		 * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.10
		 */
		return Response.status(409).entity(i18n.getString("staleObjectModificationExceptionMessage")).type(AppConstants.CONTENT_TYPE_TEXT_PLAIN).build();
	}
}

