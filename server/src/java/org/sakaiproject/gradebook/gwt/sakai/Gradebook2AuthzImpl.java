package org.sakaiproject.gradebook.gwt.sakai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.section.api.SectionAwareness;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.section.api.coursemanagement.EnrollmentRecord;
import org.sakaiproject.section.api.facade.Role;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.Category;
import org.sakaiproject.tool.gradebook.Gradebook;
import org.sakaiproject.tool.gradebook.Permission;

public class Gradebook2AuthzImpl implements Gradebook2Authz {
	
	private static final int ZERO_ITEMS = 0;

	private Gradebook2Authn authn;
	private Gradebook2Service gbService;
	private GradebookToolService gbToolService;
	private SectionAwareness sectionAwareness;
	private ToolManager toolManager;

	
	// SPRING DI
	public void setAuthn(Gradebook2Authn authn) {
		this.authn = authn;
	}

	// SPRING DI
	public void setGbToolService(GradebookToolService gbToolService) {
		this.gbToolService = gbToolService;
	}

	// SPRING DI
	public void setSectionAwareness(SectionAwareness sectionAwareness) {
		this.sectionAwareness = sectionAwareness;
	}

	// SPRING DI
	public void setGbService(Gradebook2Service gbService) {
		this.gbService = gbService;
	}
	
	// SPRING DI
	public void setToolManager(ToolManager toolManager) {
		this.toolManager = toolManager;
	}
	
	// New helper method to replace old code in getWorkingEnrollments
	public Map<String, EnrollmentRecord> findEnrollmentRecords(String gradebookUid, String optionalSearchString, String optionalSectionUid) {

		if (optionalSearchString != null)
			optionalSearchString = optionalSearchString.toUpperCase();

		Map<String, EnrollmentRecord> enrollMap = new HashMap<String, EnrollmentRecord>();

		// Start by getting a list of sections visible to the current user
		List<CourseSection> viewableSections = getViewableSections(gradebookUid);

		for (CourseSection section : viewableSections) {

			List<EnrollmentRecord> sectionMembers = sectionAwareness.getSectionMembersInRole(section.getUuid(), Role.STUDENT);

			// This filters by the passed sectionUid if it's not null
			if (optionalSectionUid == null || optionalSectionUid.equals(section.getUuid())) {
				for (EnrollmentRecord sectionMember : sectionMembers) {
					String sectionMemberUid = sectionMember.getUser().getUserUid();

					if (!enrollMap.containsKey(sectionMemberUid)) {
						// Only bother to search if we haven't already searched
						// on this item
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

	public List<CourseSection> getAllSections(String siteContext) {

		return sectionAwareness.getSections(siteContext);
	}

	public List<String> getViewableGroupsForUser(String gradebookUid, String userId, List<String> groupIds) {

		if (gradebookUid == null || userId == null)
			throw new IllegalArgumentException("Null parameter(s) in GradebookPermissionServiceImpl.getViewableSectionsForUser");

		if (groupIds == null || groupIds.size() == 0)
			return null;

		Long gradebookId = gbToolService.getGradebook(gradebookUid).getId();
		
		List<Permission> anyGroupPermission = gbToolService.getPermissionsForUserAnyGroup(gradebookId, userId);
		if (anyGroupPermission != null && anyGroupPermission.size() > 0) {
			return groupIds;
		} else {
			List<Permission> permList = gbToolService.getPermissionsForUserForGroup(gradebookId, userId, groupIds);

			List<String> filteredGroups = new ArrayList<String>();
			for (String groupId : groupIds) {
				if (groupId != null && permList != null) {
					for (Permission perm : permList) {
						if (perm != null && perm.getGroupId().equals(groupId)) {
							filteredGroups.add(groupId);
							break;
						}
					}
				}
			}
			return filteredGroups;
		}

	}

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

		if (hasUserGraderPermissions(gradebookUid, userUid)) {
			List<String> viewableSectionIds = getViewableGroupsForUser(gradebookUid, userUid, new ArrayList<String>(sectionIdCourseSectionMap.keySet()));
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

	public boolean isUserAbleToGrade(String gradebookUid) {

		String userUid = authn.getUserUid();
		return (sectionAwareness.isSiteMemberInRole(gradebookUid, userUid, Role.INSTRUCTOR) || sectionAwareness.isSiteMemberInRole(gradebookUid, userUid, Role.TA));
	}

	public boolean isUserAbleToGradeAll(String gradebookUid) {

		String userUid = authn.getUserUid();
		return sectionAwareness.isSiteMemberInRole(gradebookUid, userUid, Role.INSTRUCTOR);
	}

	public boolean isUserAbleToGradeItemForStudent(String gradebookUid, Long assignmentId, String studentUid) {

		return isUserAbleToGradeOrViewItemForStudent(gradebookUid, assignmentId, studentUid, AppConstants.gradePermission);
	}

	public boolean isUserAbleToViewOwnGrades(String gradebookUid) {

		String userUid = authn.getUserUid();
		return sectionAwareness.isSiteMemberInRole(gradebookUid, userUid, Role.STUDENT);
	}

	public boolean isUserAbleToEditAssessments(String gradebookUid) {

		String userUid = authn.getUserUid();
		return sectionAwareness.isSiteMemberInRole(gradebookUid, userUid, Role.INSTRUCTOR);
	}

	public boolean hasUserGraderPermissions(String gradebookUid) {

		String userUid = authn.getUserUid();
		List<Permission> permissions = getGraderPermissionsForUser(gradebookUid, userUid);
		return permissions != null && permissions.size() > 0;
	}

	public boolean hasUserGraderPermissions(String gradebookUid, String userUid) {

		List<Permission> permissions = getGraderPermissionsForUser(gradebookUid, userUid);
		return permissions != null && permissions.size() > 0;
	}

	public boolean isUserTAinSection(String sectionUid) {

		String userUid = authn.getUserUid();
		return sectionAwareness.isSectionMemberInRole(sectionUid, userUid, Role.TA);
	}

	public Map<Long, String> getAvailableItemsForStudent(String gradebookUid, String userId, String studentId, Collection<CourseSection> courseSections) throws IllegalArgumentException {

		if (gradebookUid == null || userId == null || studentId == null)
			throw new IllegalArgumentException("Null parameter(s) in GradebookPermissionServiceImpl.getAvailableItemsForStudent");

		return getAvailableItemsForStudent(getGradebookId(gradebookUid), userId, studentId, courseSections);
	}

	public List<Permission> getGraderPermissionsForUser(String gradebookUid, String userId) {

		if (gradebookUid == null || userId == null)
			throw new IllegalArgumentException("Null parameter(s) in GradebookPermissionServiceImpl.getPermissionsForUser");

		return gbToolService.getPermissionsForUser(getGradebookId(gradebookUid), userId);
	}
	
	public boolean hasUserGraderPermission(String gradebookUid, String groupId) {

		String userUid = authn.getUserUid();
		
		List<String> groupIds = new ArrayList<String>();
		groupIds.add(groupId);
		
		List<Permission> permissions = gbToolService.getPermissionsForUserForGroup(getGradebookId(gradebookUid), userUid, groupIds);
		
		if(null != permissions && permissions.size() > 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public List<CourseSection> getViewableSections(String gradebookUid) {

		List<CourseSection> viewableSections = new ArrayList<CourseSection>();

		List<CourseSection> allSections = getAllSections(gradebookUid);

		if (allSections == null || allSections.isEmpty()) {
			return viewableSections;
		}

		if (isUserAbleToGradeAll(gradebookUid)) {
			return allSections;
		}

		Map<String, CourseSection> sectionIdCourseSectionMap = new HashMap<String, CourseSection>();

		for(CourseSection courseSection : allSections) {
			sectionIdCourseSectionMap.put(courseSection.getUuid(), courseSection);
		}
		
		String userUid = authn.getUserUid();

		if (isUserHasGraderPermissions(gradebookUid, userUid)) {

			List<String> viewableSectionIds = getViewableGroupsForUser(gradebookUid, userUid, new ArrayList<String>(sectionIdCourseSectionMap.keySet()));
			
			if (viewableSectionIds != null && !viewableSectionIds.isEmpty()) {
			
				for(String sectionUuid : viewableSectionIds) {
				
					CourseSection viewableSection = sectionIdCourseSectionMap.get(sectionUuid);
					
					if (viewableSection != null)
						viewableSections.add(viewableSection);
				}
			}
			
		} else {
			// return all sections that the current user is a TA for
			
			for(String sectionUuid: sectionIdCourseSectionMap.keySet()) {
			
				if (isUserTAinSection(sectionUuid)) {
				
					CourseSection viewableSection = sectionIdCourseSectionMap.get(sectionUuid);
				
					if (viewableSection != null)
						viewableSections.add(viewableSection);
				}
			}
		}

		return viewableSections;

	}
	
	// GRBK-233
	public boolean canUserViewCategory(String gradebookUid, Long categoryId) {
		
		return canUserActOnCategory(gradebookUid, categoryId, AppConstants.viewPermission);
	}
	
	// GRBK-233
	public boolean canUserGradeCategory(String gradebookUid, Long categoryId) {
		
		return canUserActOnCategory(gradebookUid, categoryId, AppConstants.gradePermission);
	}
	

	/*
	 * 
	 * HELPER METHODS
	 */
	
	private Long getGradebookId(String gradebookUid) {
		return gbToolService.getGradebook(gradebookUid).getId();
	}

	private boolean isUserAbleToGradeOrViewItemForStudent(String gradebookUid, Long itemId, String studentUid, String function) throws IllegalArgumentException {

		if (itemId == null || studentUid == null || function == null) {

			throw new IllegalArgumentException("Null parameter(s) in AuthzSectionsServiceImpl.isUserAbleToGradeItemForStudent");
		}

		if (isUserAbleToGradeAll(gradebookUid)) {

			return true;
		}

		String userUid = authn.getUserUid();

		List<CourseSection> viewableSections = getViewableSections(gradebookUid);

		List<String> sectionIds = new ArrayList<String>();

		if (viewableSections != null && !viewableSections.isEmpty()) {

			for(CourseSection courseSection : viewableSections) {
			
				sectionIds.add(courseSection.getUuid());
			}
		}

		if (isUserHasGraderPermissions(gradebookUid, userUid)) {

			// get the map of authorized item (assignment) ids to grade/view
			// function
			Map<Long, String> itemIdFunctionMap = getAvailableItemsForStudent(gradebookUid, userUid, studentUid, viewableSections);

			if (itemIdFunctionMap == null || itemIdFunctionMap.isEmpty()) {
				return false; // not authorized to grade/view any items for this
				// student
			}

			String functionValueForItem = (String) itemIdFunctionMap.get(itemId);
			String view = AppConstants.viewPermission;
			String grade = AppConstants.gradePermission;

			if (functionValueForItem != null) {

				if (function.equalsIgnoreCase(grade) && functionValueForItem.equalsIgnoreCase(grade))
					return true;

				if (function.equalsIgnoreCase(view) && (functionValueForItem.equalsIgnoreCase(grade) || functionValueForItem.equalsIgnoreCase(view)))
					return true;
			}

			return false;

		} else {

			// use OOTB permissions based upon TA section membership
			for(String sectionUuid : sectionIds) {
			
				if (isUserTAinSection(sectionUuid) && sectionAwareness.isSectionMemberInRole(sectionUuid, studentUid, Role.STUDENT)) {

					return true;
				}
			}

			return false;
		}
	}

	private Map<Long, String> getAvailableItemsForStudent(Long gradebookId, String userId, String studentId, Collection<CourseSection> courseSections) throws IllegalArgumentException {

		if (gradebookId == null || userId == null || studentId == null)
			throw new IllegalArgumentException("Null parameter(s) in GradebookPermissionServiceImpl.getAvailableItemsForStudent");

		List<Category> categories = gbService.getCategoriesWithAssignments(gradebookId);
		Map<Long, Category> catIdCategoryMap = new HashMap<Long, Category>();
		if (!categories.isEmpty()) {
			for (Category category : categories) {
				if (category != null)
					catIdCategoryMap.put(category.getId(), category);
			}
		}
		Map<String, CourseSection> sectionIdCourseSectionMap = new HashMap<String, CourseSection>();
		if (!courseSections.isEmpty()) {
			for (CourseSection courseSection : courseSections) {
				if (courseSection != null) {
					sectionIdCourseSectionMap.put(courseSection.getUuid(), courseSection);
				}
			}
		}
		List<String> studentIds = new ArrayList<String>();
		studentIds.add(studentId);
		Map<String, List<String>> sectionIdStudentIdsMap = getSectionIdStudentIdsMap(courseSections, studentIds);

		Gradebook gradebook = gbToolService.getGradebook(gbToolService.getGradebookUid(gradebookId));
		List<Assignment> assignments = gbToolService.getAssignments(gradebookId);
		List<Long> categoryIds = new ArrayList<Long>(catIdCategoryMap.keySet());
		List<String> groupIds = new ArrayList<String>(sectionIdCourseSectionMap.keySet());

		// Retrieve all the different permission info needed here so not called
		// repeatedly for each student
		List<Permission> permsForUserAnyGroup = gbToolService.getPermissionsForUserAnyGroup(gradebookId, userId);
		List<Permission> allPermsForUser = gbToolService.getPermissionsForUser(gradebookId, userId);
		List<Permission> permsForAnyGroupForCategories = gbToolService.getPermissionsForUserAnyGroupForCategory(gradebookId, userId, categoryIds);
		List<Permission> permsForUserAnyGroupAnyCategory = gbToolService.getPermissionsForUserAnyGroupAnyCategory(gradebookId, userId);
		List<Permission> permsForGroupsAnyCategory = gbToolService.getPermissionsForUserForGoupsAnyCategory(gradebookId, userId, groupIds);
		List<Permission> permsForUserForCategories = gbToolService.getPermissionsForUserForCategory(gradebookId, userId, categoryIds);

		return getAvailableItemsForStudent(gradebook,
										   userId,
										   studentId, 
										   sectionIdCourseSectionMap,
										   catIdCategoryMap,
										   assignments,
										   permsForUserAnyGroup,
										   allPermsForUser,
										   permsForAnyGroupForCategories,
										   permsForUserAnyGroupAnyCategory,
										   permsForGroupsAnyCategory,
										   permsForUserForCategories,
										   sectionIdStudentIdsMap);
	}

	private boolean isUserHasGraderPermissions(String gradebookUid, String userUid) {
		
		List<Permission> permissions = getGraderPermissionsForUser(gradebookUid, userUid);
		return permissions != null && permissions.size() > 0;
	}

	private Map<String, List<String>> getSectionIdStudentIdsMap(Collection<CourseSection> courseSections, Collection<String> studentIds) {

		Map<String, List<String>> sectionIdStudentIdsMap = new HashMap<String, List<String>>();
		
		if (courseSections != null) {
			for(CourseSection section : courseSections) {
				
				if (section != null) {
					String sectionId = section.getUuid();
					List<EnrollmentRecord> members = sectionAwareness.getSectionMembersInRole(sectionId, Role.STUDENT);
					List<String> sectionMembersFiltered = new ArrayList<String>();
					
					if (!members.isEmpty()) {
					
						for(EnrollmentRecord enr : members) {
					
							String studentId = enr.getUser().getUserUid();
						
							if (studentIds.contains(studentId))
								sectionMembersFiltered.add(studentId);
						}
					}
					sectionIdStudentIdsMap.put(sectionId, sectionMembersFiltered);
				}
			}
		}
		return sectionIdStudentIdsMap;
	}

	private Map<Long, String> getAvailableItemsForStudent(Gradebook gradebook, String userId, String studentId, Map<String, CourseSection> sectionIdCourseSectionMap, Map<Long, Category> catIdCategoryMap, List<Assignment> assignments, List<Permission> permsForUserAnyGroup, List<Permission> allPermsForUser,
			List<Permission> permsForAnyGroupForCategories, List<Permission> permsForUserAnyGroupAnyCategory, List<Permission> permsForGroupsAnyCategory, List<Permission> permsForUserForCategories, Map<String, List<String>> sectionIdStudentIdsMap) throws IllegalArgumentException {

		if (gradebook == null || userId == null || studentId == null)
			throw new IllegalArgumentException("Null parameter(s) in GradebookPermissionServiceImpl.getAvailableItemsForStudent");

		List<Category> cateList = new ArrayList<Category>(catIdCategoryMap.values());

		if (gradebook.getCategory_type() == AppConstants.CATEGORY_TYPE_NO_CATEGORY) {
			Map<Long, String> assignMap = new HashMap<Long, String>();
			if (permsForUserAnyGroup != null && permsForUserAnyGroup.size() > 0) {
				boolean view = false;
				boolean grade = false;
				for(Permission perm : permsForUserAnyGroup) {
					if (perm != null && perm.getFunction().equalsIgnoreCase(AppConstants.gradePermission)) {
						grade = true;
						break;
					}
					if (perm != null && perm.getFunction().equalsIgnoreCase(AppConstants.viewPermission)) {
						view = true;
					}
				}
				for(Assignment as : assignments) {
					if (grade == true && as != null)
						assignMap.put(as.getId(), AppConstants.gradePermission);
					else if (view == true && as != null)
						assignMap.put(as.getId(), AppConstants.viewPermission);
				}
			}

			if (allPermsForUser != null) {
				Map<Long, String> assignsMapForGroups = filterPermissionForGrader(allPermsForUser, studentId, assignments, sectionIdStudentIdsMap);
				for(Long key : assignsMapForGroups.keySet()) {
				
					if ((assignMap.containsKey(key) && ((String) assignMap.get(key)).equalsIgnoreCase(AppConstants.viewPermission)) || !assignMap.containsKey(key))
						assignMap.put(key, assignsMapForGroups.get(key));
				}
			}
			
			return assignMap;
		} else {

			Map<Long, String> assignMap = new HashMap<Long, String>();
			if (permsForAnyGroupForCategories != null && permsForAnyGroupForCategories.size() > 0) {
				for(Permission perm : permsForAnyGroupForCategories) {
				
					if (perm != null) {
						if (perm.getCategoryId() != null) {
							for(Category cate : cateList) {
								if (cate != null && cate.getId().equals(perm.getCategoryId())) {
									List<Assignment> assignmentList = cate.getAssignmentList();
									if (assignmentList != null) {
										for(Assignment as : assignmentList) {
											if (as != null) {
												Long assignId = as.getId();
												if (as.getCategory() != null) {
													if (assignMap.containsKey(assignId) && ((String) assignMap.get(assignId)).equalsIgnoreCase(AppConstants.viewPermission)) {
														if (perm.getFunction().equalsIgnoreCase(AppConstants.gradePermission)) {
															assignMap.put(assignId, AppConstants.gradePermission);
														}
													} else if (!assignMap.containsKey(assignId)) {
														assignMap.put(assignId, perm.getFunction());
													}
												}
											}
										}
									}
									break;
								}
							}
						}
					}
				}
			}

			if (permsForUserAnyGroupAnyCategory != null) {
				Map<Long, String> assignMapForGroups = filterPermissionForGraderForAllAssignments(permsForUserAnyGroupAnyCategory, assignments);
				for(Long key : assignMapForGroups.keySet()) {
					if ((assignMap.containsKey(key) && ((String) assignMap.get(key)).equalsIgnoreCase(AppConstants.viewPermission)) || !assignMap.containsKey(key))
						assignMap.put(key, assignMapForGroups.get(key));
				}
			}

			if (permsForGroupsAnyCategory != null) {
				Map<Long, String> assignMapForGroups = filterPermissionForGrader(permsForGroupsAnyCategory, studentId, assignments, sectionIdStudentIdsMap);
				for(Long key : assignMapForGroups.keySet()) {
					if ((assignMap.containsKey(key) && ((String) assignMap.get(key)).equalsIgnoreCase(AppConstants.viewPermission)) || !assignMap.containsKey(key))
						assignMap.put(key, assignMapForGroups.get(key));
				}
			}

			if (permsForUserForCategories != null) {
				Map<Long, String> assignMapForGroups = filterPermissionForGraderForCategory(permsForUserForCategories, studentId, cateList, sectionIdStudentIdsMap);
				if (assignMapForGroups != null) {
					for(Long key : assignMapForGroups.keySet()) {
						if ((assignMap.containsKey(key) && ((String) assignMap.get(key)).equalsIgnoreCase(AppConstants.viewPermission)) || !assignMap.containsKey(key)) {
							assignMap.put(key, assignMapForGroups.get(key));
						}
					}
				}
			}

			return assignMap;
		}
	}

	private Map<Long, String> filterPermissionForGrader(List<Permission> perms, String studentId, List<Assignment> assignmentList, Map<String, List<String>> sectionIdStudentIdsMap) {

		if (perms != null) {
			Map<String, String> permMap = new HashMap<String, String>();
			for(Permission perm : perms) {
				if (perm != null) {
					if (permMap.containsKey(perm.getGroupId()) && ((String) permMap.get(perm.getGroupId())).equalsIgnoreCase(AppConstants.viewPermission)) {
						if (perm.getFunction().equalsIgnoreCase(AppConstants.gradePermission))
							permMap.put(perm.getGroupId(), AppConstants.gradePermission);
					} else if (!permMap.containsKey(perm.getGroupId())) {
						permMap.put(perm.getGroupId(), perm.getFunction());
					}
				}
			}
			Map<Long, String> assignmentMap = new HashMap<Long, String>();

			if (perms != null && sectionIdStudentIdsMap != null) {
				for(Assignment assignment : assignmentList) {
					Long assignId = assignment.getId();
					for(String grpId : sectionIdStudentIdsMap.keySet()) {
						List<String> sectionMembers = sectionIdStudentIdsMap.get(grpId);

						if (sectionMembers != null && sectionMembers.contains(studentId) && permMap.containsKey(grpId)) {
							if (assignmentMap.containsKey(assignId) && ((String) assignmentMap.get(assignId)).equalsIgnoreCase(AppConstants.viewPermission)) {
								if (((String) permMap.get(grpId)).equalsIgnoreCase(AppConstants.gradePermission))
									assignmentMap.put(assignId, AppConstants.gradePermission);
							} else if (!assignmentMap.containsKey(assignId))
								assignmentMap.put(assignId, permMap.get(grpId));
						}
					}
				}
			}
			return assignmentMap;
		} else
			return new HashMap<Long, String>();
	}

	private Map<Long, String> filterPermissionForGraderForCategory(List<Permission> perms, String studentId, List<Category> categoryList, Map<String, List<String>> sectionIdStudentIdsMap) {

		if (perms != null) {
			Map<Long, String> assignmentMap = new HashMap<Long, String>();
			
			for(Permission perm : perms) {
				if (perm != null && perm.getCategoryId() != null) {
					for(Category cate : categoryList) {
						if (cate != null && cate.getId().equals(perm.getCategoryId())) {
							List<Assignment> assignmentList = cate.getAssignmentList();
							if (assignmentList != null) {
								for(Assignment as : assignmentList) {
									if (as != null && sectionIdStudentIdsMap != null) {
										Long assignId = as.getId();
										for(String grpId : sectionIdStudentIdsMap.keySet()) {
											List<String> sectionMembers = sectionIdStudentIdsMap.get(grpId);

											if (sectionMembers != null && sectionMembers.contains(studentId) && as.getCategory() != null) {
												if (assignmentMap.containsKey(assignId) && grpId.equals(perm.getGroupId()) && ((String) assignmentMap.get(assignId)).equalsIgnoreCase(AppConstants.viewPermission)) {
													if (perm.getFunction().equalsIgnoreCase(AppConstants.gradePermission)) {
														assignmentMap.put(assignId, AppConstants.gradePermission);
													}
												} else if (!assignmentMap.containsKey(assignId) && grpId.equals(perm.getGroupId())) {
													assignmentMap.put(assignId, perm.getFunction());
												}
											}
										}
									}
								}
							}
							break;
						}
					}
				}
			}
			return assignmentMap;
		} else
			return new HashMap<Long, String>();
	}

	private Map<Long, String> filterPermissionForGraderForAllAssignments(List<Permission> perms, List<Assignment> assignmentList) {

		if (perms != null) {
			Boolean grade = false;
			Boolean view = false;
			for(Permission perm : perms) {
				if (perm.getFunction().equalsIgnoreCase(AppConstants.gradePermission)) {
					grade = true;
					break;
				} else if (perm.getFunction().equalsIgnoreCase(AppConstants.viewPermission))
					view = true;
			}

			Map<Long, String> assignMap = new HashMap<Long, String>();

			if (grade || view) {
				for(Assignment assign : assignmentList) {
					if (grade && assign != null)
						assignMap.put(assign.getId(), AppConstants.gradePermission);
					else if (view && assign != null)
						assignMap.put(assign.getId(), AppConstants.viewPermission);
				}
			}
			return assignMap;
		} else
			return new HashMap<Long, String>();
	}
	
	// The action is either view or grade
	private boolean canUserActOnCategory(String gradebookUid, Long categoryId, String action) {
		
		boolean canUserViewCagegory = false;
		
		// Check if the user is an instructor : An instructor can grade and view all
		canUserViewCagegory = isUserAbleToGradeAll(gradebookUid);
		if(canUserViewCagegory) {
			return true;
		}
		
		// Per the "old" gradebook, Grader Permissions overwrite TA section assignment via the section info tool,
		// thus, we check the Grader Permission first
		
		// Checking Grader Permissions
		String userUid = authn.getUserUid();
		List<Permission> permissions = null;
		
		// Case 1: Specific Category
		List<Long> categoryIds = new ArrayList<Long>();
		categoryIds.add(categoryId);
		permissions = gbToolService.getPermissionsForUserForCategory(getGradebookId(gradebookUid), userUid, categoryIds);
		if(null != permissions) {
			for(Permission permission : permissions) {
				if(permission.getFunction().equals(action)) {
					return true;
				}
			}
		}
		
		// Case 2: All Categories
		permissions = null;
		permissions = gbToolService.getPermissionForUserAnyCategory(getGradebookId(gradebookUid), userUid);
		if(null != permissions) {
			for(Permission permission : permissions) {
				if(permission.getFunction().equals(action)) {
					return true;
				}
			}
		}
		
		// We only check the section level permission if there are not Grader Permissions for the current user
		permissions = null;
		permissions = gbToolService.getPermissionsForUser(getGradebookId(gradebookUid), userUid);
		if(null != permissions && permissions.size() > ZERO_ITEMS) {
			return false;
		}
		
		// Check if the user is a TA and was added to a section via the section info tool : The TA can grade and view all for a given section
		
		// Getting all the section and check if TA is assigned to at least one of them
		List<CourseSection> courseSections = getAllSections(getSiteContext());
		for(CourseSection courseSection : courseSections) {
			canUserViewCagegory = isUserTAinSection(courseSection.getUuid());
			if(canUserViewCagegory) {
				return true;
			}
		}
		
		// At this point, there is no permission that matches the given parameters
		return false;
	}
	
	protected String getSiteContext() {

		if (toolManager == null)
			return "TESTSITECONTEXT";

		return toolManager.getCurrentPlacement().getContext();
	}
}
