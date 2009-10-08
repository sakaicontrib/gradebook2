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

package org.sakaiproject.gradebook.gwt.sakai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.sakai.InstitutionalAdvisor.Column;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class GradebookFinalGradeSubmissionController implements Controller {

	private static final Log log = LogFactory.getLog(GradebookFinalGradeSubmissionController.class);

	private Gradebook2Service service;

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

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
			return null;
		}

		List<Map<Column,String>> studentDataList = new ArrayList<Map<Column,String>>();

		if (rows != null) {
			for (StudentModel studentModel : rows) {
	
				Map<Column, String> studentData = new HashMap<Column, String>();
				studentData.put(Column.STUDENT_UID, studentModel.getIdentifier());
				studentData.put(Column.FINAL_GRADE_USER_ID, studentModel.getFinalGradeUserId());
				studentData.put(Column.EXPORT_USER_ID, studentModel.getExportUserId());
				studentData.put(Column.STUDENT_NAME, studentModel.getStudentName());
				studentData.put(Column.EXPORT_CM_ID, studentModel.getExportCmId());
				studentData.put(Column.LETTER_GRADE, studentModel.getLetterGrade());
				studentDataList.add(studentData);
			}
		}

		service.submitFinalGrade(studentDataList, gradebookUid, request, response);

		return null;
	}

	public Gradebook2Service getService() {
		return service;
	}

	public void setService(Gradebook2Service service) {
		this.service = service;
	}


}
