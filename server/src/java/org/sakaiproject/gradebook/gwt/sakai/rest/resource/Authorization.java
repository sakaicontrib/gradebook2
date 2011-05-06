package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/*
 * @deprecated As of 1.5.0
 * 
 */

@Deprecated
@Path("authorization")
public class Authorization extends Resource {

	@GET
    @Produces("text/plain")
    public String get() {
		return service.getAuthorizationDetails();
	}
}
