package org.sakaiproject.gradebook.gwt.sakai;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.sakaiproject.section.api.SectionAwareness;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.section.api.coursemanagement.EnrollmentRecord;
import org.sakaiproject.tool.gradebook.Permission;


public interface Gradebook2Authz {
	
	// New helper method to replace old code in getWorkingEnrollments
	public Map<String, EnrollmentRecord> findEnrollmentRecords(String gradebookUid, Long gradebookId, String optionalSearchString, String optionalSectionUid);

	/*
	 * Old code from the original gradebook project, slightly refactored
	 */
	public List<CourseSection> getAllSections(String siteContext);

	/*
	 * Old code from the original gradebook project, slightly refactored
	 */
	public List<String> getViewableGroupsForUser(Long gradebookId, String userId, List<String> groupIds);

	/*
	 * Old code from the original gradebook project, slightly refactored
	 */
	public List<CourseSection> getViewableSections(String gradebookUid, Long gradebookId);

	public boolean isUserAbleToGrade(String gradebookUid);

	public boolean isUserAbleToGradeAll(String gradebookUid);

	public boolean isUserAbleToGradeItemForStudent(String gradebookUid, Long assignmentId, String studentUid);

	public boolean isUserAbleToViewOwnGrades(String gradebookUid);

	public boolean isUserAbleToEditAssessments(String gradebookUid);

	public boolean isUserHasGraderPermissions(Long gradebookId);

	public boolean isUserHasGraderPermissions(Long gradebookId, String userUid);

	public boolean isUserTAinSection(String sectionUid);
	
	// APIs from the old GradebookPermissionService:
	public List<Permission> getGraderPermissionsForUser(String gradebookUid, String userId);
	public List<String> getViewableGroupsForUser(String gradebookUid, String userId, List<String> groupIds);
	public Map<Long, String> getAvailableItemsForStudent(String gradebookUid, String userId, String studentId, Collection<CourseSection> courseSections) throws IllegalArgumentException;
	public List<Permission> getGraderPermissionsForUser(Long gradebookId, String userId); 
	
}
