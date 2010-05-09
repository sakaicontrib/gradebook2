package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.sakaiproject.gradebook.gwt.client.exceptions.SecurityException;

@Path("statistics")
public class Statistics extends Resource {

	@GET @Path("/instructor/{uid}/{id}/{sectionId}")
	@Produces("application/json")
	public String getInstructorStatistics(
			@PathParam("uid") String gradebookUid,
			@PathParam("id") Long gradebookId,
			@PathParam("sectionId") String sectionId) throws SecurityException {

		List<org.sakaiproject.gradebook.gwt.client.model.Statistics> list = 
			service.getGraderStatistics(gradebookUid, gradebookId, sectionId);
		return toJson(list, list.size());
	}

	@GET @Path("/{uid}/{id}/{studentUid}")
	@Produces("application/json")
	public String getStudentStatistics(
			@PathParam("uid") String gradebookUid,
			@PathParam("id") Long gradebookId,
			@PathParam("studentUid") String studentUid) throws SecurityException {

		List<org.sakaiproject.gradebook.gwt.client.model.Statistics> list = 
			service.getLearnerStatistics(gradebookUid, gradebookId, studentUid);
		return toJson(list, list.size());
	}

	@GET @Path("/instructor/{assignmentId}")
	@Produces("application/json")
	public String get(@PathParam("assignmentId") Long assignmentId) throws SecurityException {

		int[] gradeFrequencies = service.getGradeItemStatistics(assignmentId);
		return toJson(gradeFrequencies);
	}
}
