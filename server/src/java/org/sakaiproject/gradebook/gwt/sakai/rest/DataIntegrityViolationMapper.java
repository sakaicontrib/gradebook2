package org.sakaiproject.gradebook.gwt.sakai.rest;

import java.util.ResourceBundle;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.springframework.dao.DataIntegrityViolationException;

@Provider
public class DataIntegrityViolationMapper implements ExceptionMapper<DataIntegrityViolationException> {

	private static ResourceBundle i18n = ResourceBundle.getBundle("org.sakaiproject.gradebook.gwt.client.I18nConstants");
	
	@Override
	public Response toResponse(DataIntegrityViolationException arg0) {
		
		return Response.status(500).entity(i18n.getString("dataIntegrityViolationExceptionMessage")).type(AppConstants.CONTENT_TYPE_TEXT_PLAIN).build();
	}
}
