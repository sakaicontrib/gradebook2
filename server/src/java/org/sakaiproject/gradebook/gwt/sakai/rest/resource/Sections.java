package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.util.List;
import java.util.Map;
import org.sakaiproject.util.ResourceLoader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("sections/{uid}/{id}")
public class Sections extends Resource {

	// Set via IoC
	private ResourceLoader i18n;
	
	@GET
    @Produces("application/json")
    public String get(@PathParam("uid") String gradebookUid, @PathParam("id") Long gradebookId) {
		List<Map<String,Object>> sections = service.getVisibleSections(gradebookUid, true, i18n.getString("sectionsEmptyText"));
		return toJson(sections, sections.size());
	}

	public void setI18n(ResourceLoader i18n) {
		this.i18n = i18n;
	}
	
	

}
