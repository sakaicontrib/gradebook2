package org.sakaiproject.gradebook.gwt.sakai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.section.api.SectionAwareness;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.section.api.coursemanagement.EnrollmentRecord;
import org.sakaiproject.section.api.facade.Role;
import org.sakaiproject.tool.gradebook.Permission;
import org.sakaiproject.tool.gradebook.facades.Authn;
import org.sakaiproject.tool.gradebook.facades.Authz;

public class Gradebook2SecurityImpl implements Gradebook2Security {

	private Authn authn;
	private Authz authz;
	private GradebookToolService gbService;
	private SectionAwareness sectionAwareness;
	
	
	// New helper method to replace old code in getWorkingEnrollments
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.Gradebook2Security#findEnrollmentRecords(java.lang.String, java.lang.Long, java.lang.String, java.lang.String)
	 */
	public Map<String, EnrollmentRecord> findEnrollmentRecords(String gradebookUid, Long gradebookId, String optionalSearchString, String optionalSectionUid) {
		// FIXME: Strategy here is copied from old code -- that is, get all the site members (regardless of groups), then replace those that have
		// a group. What would potentially be more efficient would be to get all the group members, then call sectionAwareness.getUnassignedMembersInRole
		/*List<EnrollmentRecord> filteredEnrollments = new ArrayList<EnrollmentRecord>();
		if (optionalSearchString != null)
			filteredEnrollments = sectionAwareness.findSiteMembersInRole(gradebookUid, Role.STUDENT, optionalSearchString);
		else
			filteredEnrollments = sectionAwareness.getSiteMembersInRole(gradebookUid, Role.STUDENT);
		
		
		Map<String, EnrollmentRecord> enrollMap = new HashMap<String, EnrollmentRecord>();
		for (EnrollmentRecord record : filteredEnrollments) {
			enrollMap.put(record.getUser().getUserUid(), record);
		}*/
		
		if (optionalSearchString != null)
			optionalSearchString = optionalSearchString.toUpperCase();
		
		Map<String, EnrollmentRecord> enrollMap = new HashMap<String, EnrollmentRecord>();
		
		// Start by getting a list of sections visible to the current user
		List<CourseSection> viewableSections = getViewableSections(gradebookUid, gradebookId);
		
		for (CourseSection section : viewableSections) {
			List<EnrollmentRecord> sectionMembers = sectionAwareness.getSectionMembersInRole(section.getUuid(), Role.STUDENT);
			
			// This filters by the passed sectionUid if it's not null
			if (optionalSectionUid == null || optionalSectionUid.equals(section.getUuid())) {
				for (EnrollmentRecord sectionMember : sectionMembers) {
					String sectionMemberUid = sectionMember.getUser().getUserUid();
						
					if (!enrollMap.containsKey(sectionMemberUid)) {
						// Only bother to search if we haven't already searched on this item
						if (optionalSearchString != null) {
							String displayName = sectionMember.getUser().getDisplayName().toUpperCase();
							if (displayName.contains(optionalSearchString))
								enrollMap.put(sectionMemberUid, sectionMember);
						} else {
							// We're not searching
							enrollMap.put(sectionMemberUid, sectionMember);
						}
					}
				}
			}
		}
		
		// If the user can grade everybody, then include the non-section members
		if (isUserAbleToGradeAll(gradebookUid)) {
			List<EnrollmentRecord> unassignedMembers = sectionAwareness.getUnassignedMembersInRole(gradebookUid, Role.STUDENT);
			if (unassignedMembers != null) {
				for (EnrollmentRecord nonMember : unassignedMembers) {
					String nonMemberUid = nonMember.getUser().getUserUid();
					
					if (!enrollMap.containsKey(nonMemberUid)) {
						if (optionalSearchString != null) {
							String displayName = nonMember.getUser().getDisplayName().toUpperCase();
							if (displayName.contains(optionalSearchString))
								enrollMap.put(nonMemberUid, nonMember);
						} else {
							// We're not searching
							enrollMap.put(nonMemberUid, nonMember);
						}
					}
				}
		
			}
		}		
		
		return enrollMap;
	}
	
	/*
	 * Old code from the original gradebook project, slightly refactored
	 */
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.Gradebook2Security#getAllSections(java.lang.String)
	 */
	public List<CourseSection> getAllSections(String siteContext) {
		return sectionAwareness.getSections(siteContext);
	}
	
	/*
	 * Old code from the original gradebook project, slightly refactored
	 */
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.Gradebook2Security#getViewableGroupsForUser(java.lang.Long, java.lang.String, java.util.List)
	 */
	public List<String> getViewableGroupsForUser(Long gradebookId, String userId, List<String> groupIds) {
		if(gradebookId == null || userId == null)
			throw new IllegalArgumentException("Null parameter(s) in GradebookPermissionServiceImpl.getViewableSectionsForUser");
		
		if (groupIds == null || groupIds.size() == 0)
			return null;
		
		List<Permission> anyGroupPermission = gbService.getPermissionsForUserAnyGroup(gradebookId, userId);
		if(anyGroupPermission != null && anyGroupPermission.size() > 0 )
		{
			return groupIds;
		}
		else
		{
			List<Permission> permList = gbService.getPermissionsForUserForGroup(gradebookId, userId, groupIds);
			
			List<String> filteredGroups = new ArrayList<String>();
			for(String groupId : groupIds) {
				if(groupId != null && permList != null) {
					for (Permission perm : permList) {
						if(perm != null && perm.getGroupId().equals(groupId))
						{
							filteredGroups.add(groupId);
							break;
						}
					}
				}
			}
			return filteredGroups;
		}
		
	}
	
	/*
	 * Old code from the original gradebook project, slightly refactored
	 */
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.Gradebook2Security#getViewableSections(java.lang.String, java.lang.Long)
	 */
	public List<CourseSection> getViewableSections(String gradebookUid, Long gradebookId) {
		List<CourseSection> viewableSections = new ArrayList<CourseSection>();
		
		// FIXME: We shouldn't use gradebookUid here, but site context
		List<CourseSection> allSections = getAllSections(gradebookUid);
		if (allSections == null || allSections.isEmpty()) {
			return viewableSections;
		}
		
		if (isUserAbleToGradeAll(gradebookUid)) {
			return allSections;
		}

		Map<String, CourseSection> sectionIdCourseSectionMap = new HashMap<String, CourseSection>();

		if (allSections != null) {
			for (CourseSection section : allSections) {
				sectionIdCourseSectionMap.put(section.getUuid(), section);
			}
		}
		
		String userUid = authn.getUserUid();
		
		if (isUserHasGraderPermissions(gradebookId, userUid)) {
			List<String> viewableSectionIds = getViewableGroupsForUser(gradebookId, userUid, new ArrayList<String>(sectionIdCourseSectionMap.keySet()));
			if (viewableSectionIds != null && !viewableSectionIds.isEmpty()) {
				for (String sectionUuid : viewableSectionIds) {
					CourseSection viewableSection = sectionIdCourseSectionMap.get(sectionUuid);
					if (viewableSection != null)
						viewableSections.add(viewableSection);
				}
			}
		} else {
			// return all sections that the current user is a TA for
			for (String sectionUuid : sectionIdCourseSectionMap.keySet()) {
				if (isUserTAinSection(sectionUuid)) {
					CourseSection viewableSection = sectionIdCourseSectionMap.get(sectionUuid);
					if (viewableSection != null)
						viewableSections.add(viewableSection);
				}
			}
		}
		
		return viewableSections;

	}
	
	
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.Gradebook2Security#isUserAbleToGrade(java.lang.String)
	 */
	public boolean isUserAbleToGrade(String gradebookUid) {
		Boolean userAbleToGrade = null;
		if (userAbleToGrade == null) {
			userAbleToGrade = new Boolean(authz.isUserAbleToGrade(gradebookUid));
		}
		return userAbleToGrade.booleanValue();
	}
	
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.Gradebook2Security#isUserAbleToGradeAll(java.lang.String)
	 */
	public boolean isUserAbleToGradeAll(String gradebookUid) {
		// FIXME: In JSF code this is a transient member var. Since we're stateless, we need to figure out
		// if looking it up each time is going to be expensive.
		Boolean userAbleToGradeAll = null;
		if (userAbleToGradeAll == null) {
			userAbleToGradeAll = new Boolean(authz.isUserAbleToGradeAll(gradebookUid));
		}
		return userAbleToGradeAll.booleanValue();
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.Gradebook2Security#isUserAbleToGradeItemForStudent(java.lang.String, java.lang.Long, java.lang.String)
	 */
	public boolean isUserAbleToGradeItemForStudent(String gradebookUid, Long assignmentId, String studentUid) {
		return authz.isUserAbleToGradeItemForStudent(gradebookUid, assignmentId, studentUid);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.Gradebook2Security#isUserAbleToViewOwnGrades(java.lang.String)
	 */
	public boolean isUserAbleToViewOwnGrades(String gradebookUid) {
		return authz.isUserAbleToViewOwnGrades(gradebookUid);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.Gradebook2Security#isUserAbleToEditAssessments(java.lang.String)
	 */
	public boolean isUserAbleToEditAssessments(String gradebookUid) {
		return authz.isUserAbleToEditAssessments(gradebookUid);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.Gradebook2Security#isUserHasGraderPermissions(java.lang.Long)
	 */
	public boolean isUserHasGraderPermissions(Long gradebookId) {
		// FIXME: In JSF code this is a transient member var. Since we're stateless, we need to figure out
		// if looking it up each time is going to be expensive.
		Boolean userHasGraderPermissions = null;
		if (userHasGraderPermissions == null) {
			userHasGraderPermissions = new Boolean(authz.isUserHasGraderPermissions(gradebookId));
		}
		
		return userHasGraderPermissions.booleanValue();
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.Gradebook2Security#isUserHasGraderPermissions(java.lang.Long, java.lang.String)
	 */
	public boolean isUserHasGraderPermissions(Long gradebookId, String userUid) {
		
		// FIXME: In JSF code this is a transient member var. Since we're stateless, we need to figure out
		// if looking it up each time is going to be expensive.
		Boolean userHasGraderPermissions = null;
		if (userHasGraderPermissions == null) {
			userHasGraderPermissions = new Boolean(authz.isUserHasGraderPermissions(gradebookId, userUid));
		}
		
		return userHasGraderPermissions.booleanValue();
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.Gradebook2Security#isUserTAinSection(java.lang.String)
	 */
	public boolean isUserTAinSection(String sectionUid) {
		String userUid = authn.getUserUid();
		return sectionAwareness.isSectionMemberInRole(sectionUid, userUid, Role.TA);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.Gradebook2Security#getAuthz()
	 */
	public Authz getAuthz() {
		return authz;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.Gradebook2Security#setAuthz(org.sakaiproject.tool.gradebook.facades.Authz)
	 */
	public void setAuthz(Authz authz) {
		this.authz = authz;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.Gradebook2Security#getAuthn()
	 */
	public Authn getAuthn() {
		return authn;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.Gradebook2Security#setAuthn(org.sakaiproject.tool.gradebook.facades.Authn)
	 */
	public void setAuthn(Authn authn) {
		this.authn = authn;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.Gradebook2Security#getGbService()
	 */
	public GradebookToolService getGbService() {
		return gbService;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.Gradebook2Security#setGbService(org.sakaiproject.gradebook.gwt.sakai.GradebookToolService)
	 */
	public void setGbService(GradebookToolService gbService) {
		this.gbService = gbService;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.Gradebook2Security#getSectionAwareness()
	 */
	public SectionAwareness getSectionAwareness() {
		return sectionAwareness;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.Gradebook2Security#setSectionAwareness(org.sakaiproject.section.api.SectionAwareness)
	 */
	public void setSectionAwareness(SectionAwareness sectionAwareness) {
		this.sectionAwareness = sectionAwareness;
	}
	
}
