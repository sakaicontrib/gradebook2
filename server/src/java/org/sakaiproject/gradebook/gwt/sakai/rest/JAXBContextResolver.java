package org.sakaiproject.gradebook.gwt.sakai.rest;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

import org.sakaiproject.gradebook.gwt.server.model.GradeRecord;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;

@Provider
public class JAXBContextResolver implements ContextResolver<JAXBContext> {

	private JAXBContext context;
	private Class[] types = {GradeRecord.class};

	public JAXBContextResolver() throws Exception {
		this.context = 
			new JSONJAXBContext( 
					JSONConfiguration.natural().build(), types); 
	}

	public JAXBContext getContext(Class<?> objectType) {
		for (Class type : types) {
			if (type == objectType) {
				return context;
			}
		}
		return null;
	}
	
}
