package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("sections/{uid}/{id}")
public class Sections extends Resource {

	// Set via IoC
	private ResourceBundle i18n = ResourceBundle.getBundle("org.sakaiproject.gradebook.gwt.client.I18nConstants");
	
	@GET
    @Produces("application/json")
    public String get(@PathParam("uid") String gradebookUid, @PathParam("id") Long gradebookId) {
		List<Map<String,Object>> sections = service.getVisibleSections(gradebookUid, true, i18n.getString("sectionsEmptyText"));
		return toJson(sections, sections.size());
	}
}
