package org.sakaiproject.gradebook.gwt.sakai;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.tool.gradebook.Permission;


public interface Gradebook2Authz {
	
	public boolean isUserAbleToGrade(String gradebookUid);
	public boolean isUserAbleToGradeAll(String gradebookUid);
	public boolean isUserAbleToGradeItemForStudent(String gradebookUid, Long itemId, String studentUid)  throws IllegalArgumentException;
	public boolean isUserAbleToViewOwnGrades(String gradebookUid);
	public boolean isUserAbleToEditAssessments(String gradebookUid);
	public boolean isUserHasGraderPermissions(Long gradebookId);
	public boolean isUserHasGraderPermissions(Long gradebookId, String userUid);
	
	// APIs from the old GradebookPermissionService:
	public List<Permission> getGraderPermissionsForUser(String gradebookUid, String userId);
	public List<String> getViewableGroupsForUser(String gradebookUid, String userId, List<String> groupIds);
	public Map<Long, String> getAvailableItemsForStudent(String gradebookUid, String userId, String studentId, Collection<CourseSection> courseSections) throws IllegalArgumentException;
	public List<Permission> getGraderPermissionsForUser(Long gradebookId, String userId); 
	
}
