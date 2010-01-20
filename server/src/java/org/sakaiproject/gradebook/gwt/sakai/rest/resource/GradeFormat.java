package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.sakaiproject.gradebook.gwt.client.AppConstants;

@Path("/gradebook/rest/gradeformat")
public class GradeFormat extends Resource {

	@GET @Path("{uid}/{id}")
    @Produces("application/json")
    public String get(@PathParam("uid") String gradebookUid, @PathParam("id") Long gradebookId) {
		List<Map<String,Object>> list = service.getAvailableGradeFormats(gradebookUid, gradebookId);
		return toJson(AppConstants.GRADE_FORMAT_ROOT, list, list.size());
	}
	
}
