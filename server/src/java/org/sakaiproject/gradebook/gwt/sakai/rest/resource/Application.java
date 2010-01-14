package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/gradebook/rest/application")
public class Application extends Resource {
	
	@GET
    @Produces("application/json")
    public String get() {
		return toJson(service.getApplicationMap());
	}

}
