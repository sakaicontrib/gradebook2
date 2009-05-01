package org.sakaiproject.gradebook.gwt.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.client.GradebookToolFacade;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityGetAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.sakai.ExportAdvisor;
import org.sakaiproject.gradebook.gwt.sakai.SampleExportAdvisor;
import org.sakaiproject.gradebook.gwt.sakai.ExportAdvisor.Column;
import org.sakaiproject.gradebook.gwt.sakai.mock.DelegateFacadeMockImpl;
import org.sakaiproject.gradebook.gwt.sakai.mock.IocMock;

/*
 * Only used in GWT hosted mode
 *
 */

public class FinalGradeSubmissionHandler extends HttpServlet {

	private static final Log log = LogFactory.getLog(FinalGradeSubmissionHandler.class);
	
	private static final long serialVersionUID = 1L;

	private IocMock iocMock = IocMock.getInstance();
	private GradebookToolFacade delegateFacade;
	private ExportAdvisor exportAdvisor;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if (delegateFacade == null) {
			
			delegateFacade = (GradebookToolFacade)iocMock.getClassInstance(DelegateFacadeMockImpl.class.getName());
		}
		
		exportAdvisor = new SampleExportAdvisor();
		
		
		String queryString = request.getQueryString();

		int n = queryString.indexOf("gradebookUid=") + 13;
		String gradebookUid = queryString.substring(n);
		
		List<StudentModel> rows = null;

		UserEntityGetAction<StudentModel> getRowsAction = new UserEntityGetAction<StudentModel>(gradebookUid, EntityType.LEARNER);
		try {

			rows = delegateFacade.getEntityList(getRowsAction);

		} catch (FatalException e) {

			log.error("EXCEPTION: Wasn't able to get the list of Student Models");
			// 500 Internal Server Error
			response.setStatus(500);
			e.printStackTrace();
			return;
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
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
