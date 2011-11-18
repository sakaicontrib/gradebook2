package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.model.key.ConfigurationKey;

@Path("config")
public class Configuration extends Resource {

	@PUT @Path("{gradebookId}")
	@Consumes("application/json")
	@Produces("application/json")
	public String update(@PathParam("gradebookId") Long gradebookId,
			String model) throws InvalidInputException {

		Map<String,Object> map = fromJson(model, Map.class);
		
		if (map != null && map.size() > 0) {
			for (String field : map.keySet()) {
				if (!field.equals(ConfigurationKey.L_GB_ID.name()) &&
						!field.equals(ConfigurationKey.S_USER_UID.name())) {
					String value = String.valueOf(map.get(field));
					service.updateConfiguration(gradebookId, field, value);
				}
			}
		}
		return model;
	}
	
}
