/**********************************************************************************
 *
 * $Id:$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2008, 2009 The Regents of the University of California
 *
 * Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 **********************************************************************************/

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

		// FIXME: Potential null pointer dereference : rows
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
