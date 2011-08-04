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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sakaiproject.gradebook.gwt.sakai.model.ActionRecord;
import org.sakaiproject.gradebook.gwt.sakai.model.UserConfiguration;
import org.sakaiproject.gradebook.gwt.sakai.model.UserDereference;
import org.sakaiproject.gradebook.gwt.sakai.model.UserDereferenceRealmUpdate;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.AssignmentGradeRecord;
import org.sakaiproject.tool.gradebook.Category;
import org.sakaiproject.tool.gradebook.Comment;
import org.sakaiproject.tool.gradebook.CourseGrade;
import org.sakaiproject.tool.gradebook.CourseGradeRecord;
import org.sakaiproject.tool.gradebook.GradableObject;
import org.sakaiproject.tool.gradebook.GradeMapping;
import org.sakaiproject.tool.gradebook.Gradebook;
import org.sakaiproject.tool.gradebook.GradingEvent;
import org.sakaiproject.tool.gradebook.Permission;
import org.sakaiproject.user.api.User;

public interface GradebookToolService {
	
	public Long createAssignmentForCategory(Long gradebookId, Long categoryId, String name, Double points, Double weight, Date dueDate, Boolean isUnweighted, Boolean isExtraCredit, Boolean isNotCounted, Boolean isReleased, Integer itemOrder, Boolean isNullsAsZeros);
	
	public Long createCategory(Long gradebookId, String name, Double weight, Integer dropLowest, Boolean equalWeightAssignments, Boolean isUnweighted, Boolean isExtraCredit, Integer categoryOrder, Boolean isEnforcePointWeighting);
	
	public void createOrUpdateUserConfiguration(String userUid, Long gradebookId, String configField, String configValue);
	
	public void deleteUserConfiguration(String userUid, Long gradebookId, String configField);
	
	public List<ActionRecord> getActionRecords(final String gradebookUid, final int offset, final int limit);
	
	public Integer getActionRecordSize(final String gradebookUid);
	
	public List<AssignmentGradeRecord> getAllAssignmentGradeRecords(final Long[] assignmentIds);
	
	public List<AssignmentGradeRecord> getAllAssignmentGradeRecords(final Long[] assignmentIds, final String[] realmIds);
	
	public List<AssignmentGradeRecord> getAllAssignmentGradeRecords(Long gradebookId, String[] realmIds, String[] roleNames);
	
	public List<CourseGradeRecord> getAllCourseGradeRecords(Gradebook gradebook);
	
	public Assignment getAssignment(Long assignmentId);
	
	/*
	 * Method that checks if a gradebook has any assignments 
	 * 
	 * @since 1.7.0
	 * 
	 * @param gradebookId
	 * @return true if the gradebook has assignments, false otherwise
	 */
	public boolean hasAssignments(Long gradebookId);
	
	public List<Assignment> getAssignments(Long gradebookId);
	
	public List<Assignment> getAssignmentsForCategory(Long categoryId);
	
	public List<AssignmentGradeRecord> getAssignmentGradeRecords(Assignment assignment);
	
	public List<AssignmentGradeRecord> getAssignmentGradeRecordsForStudent(Long gradebookId, String studentUid);
	
	public Category getCategory(Long categoryId);
	
	public List<Category> getCategories(Long gradebookId);
	
	public List<Category> getCategoriesWithAssignments(Long gradebookId);
	
	public List<Comment> getComments(Long gradebookId);
		
	public CourseGrade getCourseGrade(Long gradebookId);
	
	public Gradebook getGradebook(Long id);
	
	public Gradebook getGradebook(String uid);
	
	public GradeMapping getGradeMapping(Long id);
	
	public Set<GradeMapping> getGradeMappings(Long gradebookId);
	
	public Map<GradableObject, List<GradingEvent>> getGradingEventsForStudent(String studentId, Collection<GradableObject> gradableObjects);

	public UserDereferenceRealmUpdate getLastUserDereferenceSync(String siteId, String realmGroupId);
	
	public List<Permission> getPermissionsForUserAnyGroup(Long gradebookId, String userId) throws IllegalArgumentException;
	
	public List<Permission> getPermissionsForUserForGroup(Long gradebookId, String userId, List<String> groupIds);
	
	public Comment getCommentForItemForStudent(Long assignmentId, String studentId);
	
	public List<Comment> getStudentAssignmentComments(String studentId, Long gradebookId);
	
	public CourseGradeRecord getStudentCourseGradeRecord(Gradebook gradebook, String studentId);
	
	public int getDereferencedUserCountForSite(String siteId, String realmGroupId, String[] roleNames);
	
	public List<String> getFullUserListForSite(String siteId, String[] roleNames);
	
	/*
	 * @since 1.6.0
	 */
	public List<String> getUserListForSections(final String[] roleNames, final String[] realmIds);
	
	public int getFullUserCountForSite(String siteId, String realmGroupId, String[] roleNames);
	
	public List<UserConfiguration> getUserConfigurations(String userUid, Long gradebookId);
	
	public int getUserCountForSite(String[] realmIds, String sortField, 
			String searchField, String searchCriteria, String[] roleNames);
	
	public List<Object[]> getUserGroupReferences(List<String> groupReferences, String[] roleNames);

	public List<UserDereference> getUserDereferences(String[] realmIds, String sortField, String searchField, 
			String searchCriteria, int offset, int limit, boolean isAsc, String[] roleNames);
	
	public boolean isAnyScoreEntered(final Long gradebookId, final boolean hasCategories);
	
	public boolean isStudentMissingScores(Long gradebookId, String studentId, boolean hasCategories);
		
	public Long storeActionRecord(ActionRecord actionRecord);
	
	public void syncUserDereferenceBySite(final String siteId, String realmGroupId, final List<User> users, int realmCount, String[] roleNames);
	
	public void updateAssignment(Assignment assignment);
	
	public Set<AssignmentGradeRecord> updateAssignmentGradeRecords(Assignment assignment, Collection<AssignmentGradeRecord> gradeRecords);
	
	public void updateCourseGradeRecords(CourseGrade courseGrade, Collection<CourseGradeRecord> gradeRecords);
	
	public void updateCategory(Category category);
	
	public void updateComment(Comment comment);
	
	public void updateGradebook(Gradebook gradebook);
	
	public Long createPermission(Permission permission);
	
	public void deletePermission(Long permissionId);
	
	public List<Permission> getPermissionsForUser(final Long gradebookId, final String userId) throws IllegalArgumentException;
	
	public String getGradebookUid(Long id);
	
	public List<Permission> getPermissionsForUserAnyGroupForCategory(final Long gradebookId, final String userId, final List<Long> cateIds) throws IllegalArgumentException;
	
	public List<Permission> getPermissionsForUserAnyGroupAnyCategory(final Long gradebookId, final String userId) throws IllegalArgumentException;
	
	public List<Permission> getPermissionsForUserForGoupsAnyCategory(final Long gradebookId, final String userId, final List<String> groupIds) throws IllegalArgumentException;
	
	public List<Permission> getPermissionsForUserForCategory(final Long gradebookId, final String userId, final List<Long> cateIds) throws IllegalArgumentException;
	
	public List<Permission> getPermissionForUserAnyCategory(final Long gradebookId, final String userId) throws IllegalArgumentException;
	
	public Boolean hasGradeOverrides(final Long gradebookId); 
}
