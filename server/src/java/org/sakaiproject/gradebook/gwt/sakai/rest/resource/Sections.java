package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("sections/{uid}/{id}")
public class Sections extends Resource {

	@GET
    @Produces("application/json")
    public String get(@PathParam("uid") String gradebookUid, @PathParam("id") Long gradebookId) {
		List<Map<String,Object>> sections = service.getVisibleSections(gradebookUid, true, "All Viewable Sections");
		return toJson(sections, sections.size());
	}

}
