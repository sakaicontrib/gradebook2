package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.client.model.Roster;
import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;
import org.sakaiproject.gradebook.gwt.sakai.InstitutionalAdvisor.Column;

@Path("submit")
public class SubmitFinalGrades extends Resource {
	
	private static Log log = LogFactory.getLog(SubmitFinalGrades.class);
	
	
	@Path("{uid}")
	public String submitFinalGrades(@PathParam("uid") String gradebookUid,
			@Context HttpServletResponse response, @Context HttpServletRequest request) {
		
		List<Learner> rows = null;

		try {

			Roster result = service.getRoster(gradebookUid, null, null, null, null, null, null, Boolean.TRUE, true, false);

			if (result != null)
				rows = result.getLearnerPage();

		} catch (Exception e) {
			log.error("EXCEPTION: Wasn't able to get the list of Student Models");
			// 500 Internal Server Error
			response.setStatus(500);
			e.printStackTrace();
			return null;
		}

		List<Map<Column,String>> studentDataList = new ArrayList<Map<Column,String>>();

		if (rows != null) {
			for (Learner studentModel : rows) {
	
				Map<Column, String> studentData = new HashMap<Column, String>();
				studentData.put(Column.STUDENT_UID, (String)studentModel.get(LearnerKey.S_UID.name()));
				studentData.put(Column.FINAL_GRADE_USER_ID, (String)studentModel.get(LearnerKey.S_FNL_GRD_ID.name()));
				studentData.put(Column.EXPORT_USER_ID, (String)studentModel.get(LearnerKey.S_EXPRT_USR_ID.name()));
				studentData.put(Column.STUDENT_NAME, (String)studentModel.get(LearnerKey.S_DSPLY_NM.name()));
				studentData.put(Column.EXPORT_CM_ID, (String)studentModel.get(LearnerKey.S_EXPRT_CM_ID.name()));
				studentData.put(Column.LETTER_GRADE, (String)studentModel.get(LearnerKey.S_LTR_GRD.name()));
				studentData.put(Column.RAW_GRADE, (String)studentModel.get(LearnerKey.S_RAW_GRD.name()));
				studentDataList.add(studentData);
			}
		}

		service.submitFinalGrade(studentDataList, gradebookUid, request, response);

		
		
		
		return null;
	}

}
