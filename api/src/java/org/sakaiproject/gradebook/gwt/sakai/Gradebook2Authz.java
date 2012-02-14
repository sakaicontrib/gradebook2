/**********************************************************************************
 *
 * $Id: Gradebook2Authz.java 63685 2009-09-30 01:33:01Z jlrenfro@ucdavis.edu $
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.tool.gradebook.Permission;


/**
 * The Interface Gradebook2Authz.
 */
public interface Gradebook2Authz {

	/**
	 * Gets the all sections.
	 * 
	 * @param siteContext the site context
	 * 
	 * @return the all sections
	 */
	public List<CourseSection> getAllSections(String siteContext);

	/**
	 * Gets the viewable groups for user.
	 * 
	 * @param gradebookId the gradebook id
	 * @param userId the user id
	 * @param groupIds the group ids
	 * 
	 * @return the viewable groups for user
	 */
	public List<String> getViewableGroupsForUser(String gradebookUid, String userId, List<String> groupIds);

	/**
	 * Gets the viewable sections.
	 * 
	 * @param gradebookUid the gradebook uid
	 * @param gradebookId the gradebook id
	 * 
	 * @return the viewable sections
	 */
	public List<CourseSection> getViewableSections(String gradebookUid);

	/**
	 * Checks if is user able to grade.
	 * 
	 * @param gradebookUid the gradebook uid
	 * 
	 * @return true, if is user able to grade
	 */
	public boolean isUserAbleToGrade(String gradebookUid);

	/**
	 * Checks if is user able to grade all.
	 * 
	 * @param gradebookUid the gradebook uid
	 * 
	 * @return true, if is user able to grade all
	 */
	public boolean isUserAbleToGradeAll(String gradebookUid);

	/**
	 * Checks if is user able to grade item for student.
	 * 
	 * @param gradebookUid the gradebook uid
	 * @param assignmentId the assignment id
	 * @param studentUid the student uid
	 * 
	 * @return true, if is user able to grade item for student
	 */
	public boolean isUserAbleToGradeItemForStudent(String gradebookUid, Long assignmentId, String studentUid);

	/**
	 * Checks if is user able to view own grades.
	 * 
	 * @param gradebookUid the gradebook uid
	 * 
	 * @return true, if is user able to view own grades
	 */
	public boolean isUserAbleToViewOwnGrades(String gradebookUid);

	/**
	 * Checks if is user able to edit assessments.
	 * 
	 * @param gradebookUid the gradebook uid
	 * 
	 * @return true, if is user able to edit assessments
	 */
	public boolean isUserAbleToEditAssessments(String gradebookUid);

	/**
	 * Checks if is user has grader permissions.
	 * 
	 * @param gradebookUid the gradebook id
	 * 
	 * @return true, if is user has grader permissions
	 */
	public boolean hasUserGraderPermissions(String gradebookUid);

	/**
	 * 
	 * @param gradebookUid
	 * @param groupId
	 * @return
	 */
	public boolean hasUserGraderPermission(String gradebookUid, String groupId);

	/**
	 * Checks if is user has grader permissions.
	 * 
	 * @param gradebookUid the gradebook id
	 * @param userUid the user uid
	 * 
	 * @return true, if is user has grader permissions
	 */
	public boolean hasUserGraderPermissions(String gradebookUid, String userUid);

	/**
	 * Checks if is user t ain section.
	 * 
	 * @param sectionUid the section uid
	 * 
	 * @return true, if is user t ain section
	 */
	public boolean isUserTAinSection(String sectionUid);


	/**
	 * Gets the available items for student.
	 * 
	 * @param gradebookUid the gradebook uid
	 * @param userId the user id
	 * @param studentId the student id
	 * @param courseSections the course sections
	 * 
	 * @return the available items for student
	 * 
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public Map<Long, String> getAvailableItemsForStudent(String gradebookUid, String userId, String studentId, Collection<CourseSection> courseSections) throws IllegalArgumentException;

	/**
	 * Gets the grader permissions for user.
	 * 
	 * @param gradebookUid the gradebook id
	 * @param userId the user id
	 * 
	 * @return the grader permissions for user
	 */
	public List<Permission> getGraderPermissionsForUser(String gradebookUid, String userId);

	/**
	 * GRBK-233
	 * 
	 * @param gradebookUid
	 * @param categoryId
	 * @return
	 */
	public boolean canUserViewCategory(String gradebookUid, Long categoryId);

	/**
	 * GRBK-233
	 * 
	 * @param gradebookUid
	 * @param categoryId
	 * @return
	 */
	public boolean canUserGradeCategory(String gradebookUid, Long categoryId);

	/**
	 * GRBK-487
	 * 
	 * @return
	 */
	public boolean isAdminUser();
	
}
