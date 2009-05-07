package org.sakaiproject.gradebook.gwt.sakai;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.gradebook.gwt.sakai.model.UserDereference;
import org.sakaiproject.site.api.Group;



public interface ExportAdvisor {
	
	public enum Column { STUDENT_NAME, STUDENT_GRADE, EXPORT_USER_ID, EXPORT_CM_ID, FINAL_GRADE_USER_ID };
	
	/*
	 * @param userEid : The user's EID
	 * @param providerGroupId : Group.getProviderGroupId();
	 * 
	 */
	public String getExportCourseManagemntId(String userEid, Group group);
	
	/*
	 * @param userEid : The user's EID
	 * 
	 */
	public String getExportUserId(UserDereference dereference);
	
	public String getFinalGradeUserId(UserDereference dereference);
	
	/*
	 * 
	 */
	public void submitFinalGrade(List<Map<Column,String>> studentDataList, String gradebookUid, HttpServletRequest request, HttpServletResponse response);

}
