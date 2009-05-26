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
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2Service;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ServiceImpl;
import org.sakaiproject.gradebook.gwt.sakai.InstitutionalAdvisor;
import org.sakaiproject.gradebook.gwt.sakai.SampleInstitutionalAdvisor;
import org.sakaiproject.gradebook.gwt.sakai.InstitutionalAdvisor.Column;
import org.sakaiproject.gradebook.gwt.sakai.mock.IocMock;

import com.extjs.gxt.ui.client.data.PagingLoadResult;

/*
 * Only used in GWT hosted mode
 *
 */

public class FinalGradeSubmissionHandler extends HttpServlet {

	private static final Log log = LogFactory.getLog(FinalGradeSubmissionHandler.class);
	
	private static final long serialVersionUID = 1L;

	private IocMock iocMock = IocMock.getInstance();
	private Gradebook2Service service;
	private InstitutionalAdvisor advisor;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if (service == null) 
			service = (Gradebook2Service)iocMock.getClassInstance(Gradebook2ServiceImpl.class.getName());
		
		if (advisor == null)
			advisor = new SampleInstitutionalAdvisor();
		
		
		String queryString = request.getQueryString();

		int n = queryString.indexOf("gradebookUid=") + 13;
		String gradebookUid = queryString.substring(n);
		
		List<StudentModel> rows = null;

		try {

			PagingLoadResult<StudentModel> result = service.getStudentRows(gradebookUid, null, null, Boolean.TRUE);
			
			if (result != null)
				rows = result.getData();
			
		} catch (Exception e) {

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
				
		advisor.submitFinalGrade(studentDataList, gradebookUid, request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
