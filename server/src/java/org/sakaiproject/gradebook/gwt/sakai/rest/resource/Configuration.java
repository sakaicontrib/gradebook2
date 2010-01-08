package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ComponentService;

@Path("/gradebook/rest/config")
public class Configuration {

	private static final Log log = LogFactory.getLog(Configuration.class);
	
	private Gradebook2ComponentService service;
	
	@PUT @Path("{gradebookId}")
	@Consumes({"application/xml", "application/json"})
	public void update(@PathParam("gradebookId") Long gradebookId,
			String json) throws InvalidInputException {

		JsonFactory factory = new JsonFactory();
		JsonParser jp = null;
		try {
			jp = factory.createJsonParser(json);
		} catch (JsonParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (jp != null) {
			
			try {
				JsonToken token; // will return JsonToken.START_OBJECT (verify?)
				while ((token = jp.nextToken()) != JsonToken.END_OBJECT) {
					if (token == JsonToken.START_OBJECT)
						continue;
					
					String field = jp.getCurrentName();
					token = jp.nextToken();
					
					if (token == JsonToken.VALUE_STRING) {
						String value = jp.getText();
						service.updateConfiguration(gradebookId, field, value);
					}
				}
			} catch (JsonParseException e) {
				log.error(e);
			} catch (IOException ioe) {
				log.error(ioe);
			}

			/*
			for (JsonToken token = jsonParser.getCurrentToken();jsonParser.hasCurrentToken();jsonParser.nextToken()) {
				String field;
				try {
					field = jsonParser.getCurrentName();
					String value = token.asString();
					service.updateConfiguration(gradebookId, field, value);
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
		}
	}
	
	public Gradebook2ComponentService getService() {
		return service;
	}

	public void setService(Gradebook2ComponentService service) {
		this.service = service;
	}
	
}
