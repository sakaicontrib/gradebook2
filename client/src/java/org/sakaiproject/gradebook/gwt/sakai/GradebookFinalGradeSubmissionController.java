package org.sakaiproject.gradebook.gwt.sakai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.client.GradebookToolFacade;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityGetAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.sakai.ExportAdvisor.Column;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class GradebookFinalGradeSubmissionController implements Controller {

	private static final Log log = LogFactory.getLog(GradebookFinalGradeSubmissionController.class);
	
	private GradebookToolFacade delegateFacade;
	private ExportAdvisor exportAdvisor;
	
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String queryString = request.getQueryString();

		int n = queryString.indexOf("gradebookUid=") + 13;
		String gradebookUid = queryString.substring(n);
		
		List<StudentModel> rows = null;

		UserEntityGetAction<StudentModel> getRowsAction = new UserEntityGetAction<StudentModel>(gradebookUid, EntityType.STUDENT);
		
		try {

			rows = delegateFacade.getEntityList(getRowsAction);

		} catch (FatalException e) {

			log.error("EXCEPTION: Wasn't able to get the list of Student Models");
			// 500 Internal Server Error
			response.setStatus(500);
			e.printStackTrace();
			return null;
		}
		
		List<Map<Column,String>> studentDataList = new ArrayList<Map<Column,String>>();
		
		for (StudentModel studentModel : rows) {
			
			Map<Column, String> studentData = new HashMap<Column, String>();
			studentData.put(Column.EXPORT_USER_ID, studentModel.getExportUserId());
			studentData.put(Column.STUDENT_NAME, studentModel.getStudentName());
			studentData.put(Column.EXPORT_CM_ID, studentModel.getExportCmId());
			studentData.put(Column.STUDENT_GRADE, studentModel.getStudentGrade());
			studentDataList.add(studentData);
		}

		exportAdvisor.submitFinalGrade(studentDataList, gradebookUid, request, response);
		
		return null;
	}
	
	public void setDelegateFacade(GradebookToolFacade delegateFacade) {
		this.delegateFacade = delegateFacade;
	}
	
	public void setExportAdvisor(ExportAdvisor exportAdvisor) {
		this.exportAdvisor = exportAdvisor;
	}

}
