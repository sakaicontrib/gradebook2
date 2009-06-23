package org.sakaiproject.gradebook.gwt.sakai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.sakai.mock.tool.gradebook.Category;
import org.sakaiproject.section.api.SectionAwareness;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.section.api.coursemanagement.EnrollmentRecord;
import org.sakaiproject.section.api.facade.Role;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.Gradebook;
import org.sakaiproject.tool.gradebook.Permission;

public class Gradebook2AuthzImpl implements Gradebook2Authz {

	private Gradebook2Authn authn;
	private SectionAwareness sectionAwareness;
	private GradebookToolService gbToolService;
	private Gradebook2Service gbService;

	public boolean isUserAbleToEditAssessments(String gradebookUid) {
		String userUid = authn.getUserUid();
		return getSectionAwareness().isSiteMemberInRole(gradebookUid, userUid, Role.INSTRUCTOR);
	}

	public boolean isUserAbleToGrade(String gradebookUid) {
		String userUid = authn.getUserUid();
		return (getSectionAwareness().isSiteMemberInRole(gradebookUid, userUid, Role.INSTRUCTOR) || getSectionAwareness().isSiteMemberInRole(gradebookUid, userUid, Role.TA));
	}

	public boolean isUserAbleToGradeAll(String gradebookUid) {
		String userUid = authn.getUserUid();
		return getSectionAwareness().isSiteMemberInRole(gradebookUid, userUid, Role.INSTRUCTOR);
	}

	public boolean isUserAbleToGradeItemForStudent(String gradebookUid, Long itemId, String studentUid) throws IllegalArgumentException {
		return isUserAbleToGradeOrViewItemForStudent(gradebookUid, itemId, studentUid, AppConstants.gradePermission);
	}

	public boolean isUserAbleToViewOwnGrades(String gradebookUid) {
		String userUid = authn.getUserUid();
		return getSectionAwareness().isSiteMemberInRole(gradebookUid, userUid, Role.STUDENT);
	}

	public boolean isUserHasGraderPermissions(Long gradebookId) {
		String userUid = authn.getUserUid();
		List permissions = getGraderPermissionsForUser(gradebookId, userUid);
		return permissions != null && permissions.size() > 0;
	}

	public boolean isUserHasGraderPermissions(Long gradebookId, String userUid) {
		List permissions = getGraderPermissionsForUser(gradebookId, userUid);
		return permissions != null && permissions.size() > 0;
	}
	
	public SectionAwareness getSectionAwareness() {
		return sectionAwareness;
	}
	
	private boolean isUserHasGraderPermissions(String gradebookUid) {
		String userUid = authn.getUserUid();
		List permissions = getGraderPermissionsForUser(gradebookUid, userUid);
		return permissions != null && permissions.size() > 0;
	}
	
	private List getAllSections(String gradebookUid) {
		SectionAwareness sectionAwareness = getSectionAwareness();
		List sections = sectionAwareness.getSections(gradebookUid);

		return sections;
	}
	
	private boolean isUserHasGraderPermissions(String gradebookUid, String userUid) {
		List permissions = getGraderPermissionsForUser(gradebookUid, userUid);
		return permissions != null && permissions.size() > 0;
	}
	
	private boolean isUserTAinSection(String sectionUid) {
		String userUid = authn.getUserUid();
		return getSectionAwareness().isSectionMemberInRole(sectionUid, userUid, Role.TA);
	}
	
	private List getViewableSections(String gradebookUid) {
		List viewableSections = new ArrayList();
		
		List allSections = getAllSections(gradebookUid);
		if (allSections == null || allSections.isEmpty()) {
			return viewableSections;
		}
		
		if (isUserAbleToGradeAll(gradebookUid)) {
			return allSections;
		}

		Map sectionIdCourseSectionMap = new HashMap();

		for (Iterator sectionIter = allSections.iterator(); sectionIter.hasNext();) {
			CourseSection section = (CourseSection) sectionIter.next();
			sectionIdCourseSectionMap.put(section.getUuid(), section);
		}
		
		String userUid = authn.getUserUid();
		
		if (isUserHasGraderPermissions(gradebookUid, userUid)) {	

			List viewableSectionIds =  getViewableGroupsForUser(gradebookUid, userUid, new ArrayList(sectionIdCourseSectionMap.keySet()));
			if (viewableSectionIds != null && !viewableSectionIds.isEmpty()) {
				for (Iterator idIter = viewableSectionIds.iterator(); idIter.hasNext();) {
					String sectionUuid = (String) idIter.next();
					CourseSection viewableSection = (CourseSection)sectionIdCourseSectionMap.get(sectionUuid);
					if (viewableSection != null)
						viewableSections.add(viewableSection);
				}
			}
		} else {
			// return all sections that the current user is a TA for
			for (Iterator iter = sectionIdCourseSectionMap.keySet().iterator(); iter.hasNext(); ) {
				String sectionUuid = (String) iter.next();
				if (isUserTAinSection(sectionUuid)) {
					CourseSection viewableSection = (CourseSection)sectionIdCourseSectionMap.get(sectionUuid);
					if (viewableSection != null)
						viewableSections.add(viewableSection);
				}
			}
		}
		
		return viewableSections;

	}
	
	private boolean isUserAbleToGradeOrViewItemForStudent(String gradebookUid, Long itemId, String studentUid, String function) throws IllegalArgumentException {

		if (itemId == null || studentUid == null || function == null) {

			throw new IllegalArgumentException("Null parameter(s) in AuthzSectionsServiceImpl.isUserAbleToGradeItemForStudent");
		}

		if (isUserAbleToGradeAll(gradebookUid)) {

			return true;
		}

		String userUid = authn.getUserUid();

		List viewableSections = getViewableSections(gradebookUid);

		List sectionIds = new ArrayList();

		if (viewableSections != null && !viewableSections.isEmpty()) {

			for (Iterator sectionIter = viewableSections.iterator(); sectionIter.hasNext();) {

				CourseSection section = (CourseSection) sectionIter.next();

				sectionIds.add(section.getUuid());

			}
		}

		if (isUserHasGraderPermissions(gradebookUid, userUid)) {

			// get the map of authorized item (assignment) ids to grade/view function
			Map itemIdFunctionMap = getAvailableItemsForStudent(gradebookUid, userUid, studentUid, viewableSections);

			if (itemIdFunctionMap == null || itemIdFunctionMap.isEmpty()) {
				return false;  // not authorized to grade/view any items for this student
			}

			String functionValueForItem = (String)itemIdFunctionMap.get(itemId);
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
			for (Iterator iter = sectionIds.iterator(); iter.hasNext(); ) {

				String sectionUuid = (String) iter.next();

				if (isUserTAinSection(sectionUuid) && getSectionAwareness().isSectionMemberInRole(sectionUuid, studentUid, Role.STUDENT)) {

					return true;
				}
			}

			return false;
		}
	}
	
	// API IMPL from the old GradebookPermissionService:
	public Map<Long, String> getAvailableItemsForStudent(String gradebookUid, String userId, String studentId, Collection<CourseSection> courseSections)
	throws IllegalArgumentException {

		if(gradebookUid == null || userId == null || studentId == null)
			throw new IllegalArgumentException("Null parameter(s) in GradebookPermissionServiceImpl.getAvailableItemsForStudent");

		Long gradebookId = gbToolService.getGradebook(gradebookUid).getId();

		return getAvailableItemsForStudent(gradebookId, userId, studentId, courseSections);
	}

	public List<Permission> getGraderPermissionsForUser(String gradebookUid, String userId) {
		if (gradebookUid == null || userId == null) {
			throw new IllegalArgumentException("Null gradebookUid or userId passed to getGraderPermissionsForUser");
		}
		
		Long gradebookId = gbToolService.getGradebook(gradebookUid).getId();
		
		return gbToolService.getPermissionsForUser(gradebookId, userId);
	}

	public List<String> getViewableGroupsForUser(String gradebookUid, String userId, List<String> groupIds) {
		if(gradebookUid == null || userId == null)
			throw new IllegalArgumentException("Null parameter(s) in GradebookPermissionServiceImpl.getViewableSectionsForUser");
	
		Long gradebookId = gbToolService.getGradebook(gradebookUid).getId();
		
		return getViewableGroupsForUser(gradebookId, userId, groupIds);
	}

	public List<Permission> getGraderPermissionsForUser(Long gradebookId, String userId) {
		
		if(gradebookId == null || userId == null)
			throw new IllegalArgumentException("Null parameter(s) in GradebookPermissionServiceImpl.getPermissionsForUser");
		
		return gbToolService.getPermissionsForUser(gradebookId, userId);
	}
	
	private List<String> getViewableGroupsForUser(Long gradebookId, String userId, List<String> groupIds) {
		if(gradebookId == null || userId == null)
			throw new IllegalArgumentException("Null parameter(s) in GradebookPermissionServiceImpl.getViewableSectionsForUser");
		
		if (groupIds == null || groupIds.size() == 0)
			return null;
		
		List<Permission> anyGroupPermission = gbToolService.getPermissionsForUserAnyGroup(gradebookId, userId);
		if(anyGroupPermission != null && anyGroupPermission.size() > 0 )
		{
			return groupIds;
		}
		else
		{
			List<Permission> permList = gbToolService.getPermissionsForUserForGroup(gradebookId, userId, groupIds);
			
			List<String> filteredGroups = new ArrayList<String>();
			for(Iterator<String> groupIter = groupIds.iterator(); groupIter.hasNext();)
			{
				String groupId = (String)groupIter.next();
				if(groupId != null)
				{
					for(Iterator<Permission> iter = permList.iterator(); iter.hasNext();)
					{
						Permission perm = iter.next();
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

	private Map getSectionIdStudentIdsMap(Collection courseSections, Collection studentIds) {
		Map sectionIdStudentIdsMap = new HashMap();
		if (courseSections != null) {
			for (Iterator sectionIter = courseSections.iterator(); sectionIter.hasNext();) {
				CourseSection section = (CourseSection)sectionIter.next();
				if (section != null) {
					String sectionId = section.getUuid();
					List members = getSectionAwareness().getSectionMembersInRole(sectionId, Role.STUDENT);
					List sectionMembersFiltered = new ArrayList();
					if (!members.isEmpty()) {
						for (Iterator memberIter = members.iterator(); memberIter.hasNext();) {
							EnrollmentRecord enr = (EnrollmentRecord) memberIter.next();
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

	
	private Map filterPermissionForGrader(List perms, List studentIds, Map sectionIdStudentIdsMap)
	{
		if(perms != null)
		{
			Map permMap = new HashMap();
			for(Iterator iter = perms.iterator(); iter.hasNext();)
			{
				Permission perm = (Permission)iter.next();
				if(perm != null)
				{
					if(permMap.containsKey(perm.getGroupId()) && ((String)permMap.get(perm.getGroupId())).equalsIgnoreCase(AppConstants.viewPermission))
					{
						if(perm.getFunction().equalsIgnoreCase(AppConstants.gradePermission))
							permMap.put(perm.getGroupId(), AppConstants.gradePermission);
					}
					else if(!permMap.containsKey(perm.getGroupId()))
					{
						permMap.put(perm.getGroupId(), perm.getFunction());
					}
				}
			}
			Map studentMap = new HashMap();

			if(perms != null)
			{
				for(Iterator iter = studentIds.iterator(); iter.hasNext();)
				{
					String studentId = (String) iter.next();
					if (sectionIdStudentIdsMap != null) {
						for(Iterator groupIter = sectionIdStudentIdsMap.keySet().iterator(); groupIter.hasNext();)
						{
							String grpId = (String) groupIter.next();
							List sectionMembers = (List)sectionIdStudentIdsMap.get(grpId);

							if(sectionMembers != null && sectionMembers.contains(studentId) && permMap.containsKey(grpId))
							{
								if(studentMap.containsKey(studentId) && ((String)studentMap.get(studentId)).equalsIgnoreCase(AppConstants.viewPermission))
								{
									if(((String)permMap.get(grpId)).equalsIgnoreCase(AppConstants.gradePermission))
										studentMap.put(studentId, AppConstants.gradePermission);
								}
								else if(!studentMap.containsKey(studentId))
									studentMap.put(studentId, permMap.get(grpId));
							}
						}
					}
				}
			}
			return studentMap;
		}
		else
			return new HashMap();
	}
	
	private Map filterPermissionForGrader(List perms, String studentId, List assignmentList, Map sectionIdStudentIdsMap)
	{
		if(perms != null)
		{
			Map permMap = new HashMap();
			for(Iterator iter = perms.iterator(); iter.hasNext();)
			{
				Permission perm = (Permission)iter.next();
				if(perm != null)
				{
					if(permMap.containsKey(perm.getGroupId()) && ((String)permMap.get(perm.getGroupId())).equalsIgnoreCase(AppConstants.viewPermission))
					{
						if(perm.getFunction().equalsIgnoreCase(AppConstants.gradePermission))
							permMap.put(perm.getGroupId(), AppConstants.gradePermission);
					}
					else if(!permMap.containsKey(perm.getGroupId()))
					{
						permMap.put(perm.getGroupId(), perm.getFunction());
					}
				}
			}
			Map assignmentMap = new HashMap();

			if(perms != null && sectionIdStudentIdsMap != null)
			{
				for(Iterator iter = assignmentList.iterator(); iter.hasNext();)
				{
					Long assignId = ((Assignment)iter.next()).getId();
					for(Iterator groupIter = sectionIdStudentIdsMap.keySet().iterator(); groupIter.hasNext();)
					{
						String grpId = (String) groupIter.next();
						List sectionMembers = (List) sectionIdStudentIdsMap.get(grpId);
						
						if(sectionMembers != null && sectionMembers.contains(studentId) && permMap.containsKey(grpId))
						{
							if(assignmentMap.containsKey(assignId) && ((String)assignmentMap.get(assignId)).equalsIgnoreCase(AppConstants.viewPermission))
							{
								if(((String)permMap.get(grpId)).equalsIgnoreCase(AppConstants.gradePermission))
									assignmentMap.put(assignId, AppConstants.gradePermission);
							}
							else if(!assignmentMap.containsKey(assignId))
								assignmentMap.put(assignId, permMap.get(grpId));
						}
					}
				}
			}
			return assignmentMap;
		}
		else
			return new HashMap();
	}
	
	private Map filterPermissionForGraderForAllAssignments(List perms, List assignmentList)
	{
		if(perms != null)
		{
			Boolean grade = false;
			Boolean view = false;
			for(Iterator iter = perms.iterator(); iter.hasNext();)
			{
				Permission perm = (Permission)iter.next();
				if(perm.getFunction().equalsIgnoreCase(AppConstants.gradePermission))
				{
					grade = true;
					break;
				}
				else if(perm.getFunction().equalsIgnoreCase(AppConstants.viewPermission))
					view = true;
			}

			Map assignMap = new HashMap();

			if(grade || view)
			{
				for(Iterator iter = assignmentList.iterator(); iter.hasNext();)
				{
					Assignment assign = (Assignment) iter.next();
					if(grade && assign != null)
						assignMap.put(assign.getId(), AppConstants.gradePermission);
					else if(view && assign != null)
						assignMap.put(assign.getId(), AppConstants.viewPermission);
				}
			}
			return assignMap;
		}
		else
			return new HashMap();
	}
	
	private	Map filterPermissionForGraderForCategory(List perms, String studentId, List categoryList, Map sectionIdStudentIdsMap)
	{
		if(perms != null)
		{
			Map assignmentMap = new HashMap();
			
			for(Iterator iter = perms.iterator(); iter.hasNext();)
			{
				Permission perm = (Permission)iter.next();
				if(perm != null && perm.getCategoryId() != null)
				{
					for(Iterator cateIter = categoryList.iterator(); cateIter.hasNext();)
					{
						Category cate = (Category) cateIter.next();
						if(cate != null && cate.getId().equals(perm.getCategoryId()))
						{
							List assignmentList = cate.getAssignmentList();
							if (assignmentList != null) {
								for(Iterator assignIter = assignmentList.iterator(); assignIter.hasNext();)
								{
									Assignment as = (Assignment)assignIter.next();
									if(as != null && sectionIdStudentIdsMap != null)
									{
										Long assignId = as.getId();
										for(Iterator groupIter = sectionIdStudentIdsMap.keySet().iterator(); groupIter.hasNext();)
										{
											String grpId = (String) groupIter.next();
											List sectionMembers = (List) sectionIdStudentIdsMap.get(grpId);

											if(sectionMembers != null && sectionMembers.contains(studentId) && as.getCategory() != null)
											{
												if(assignmentMap.containsKey(assignId) && grpId.equals(perm.getGroupId()) && ((String)assignmentMap.get(assignId)).equalsIgnoreCase(AppConstants.viewPermission))
												{
													if(perm.getFunction().equalsIgnoreCase(AppConstants.gradePermission))
													{
														assignmentMap.put(assignId, AppConstants.gradePermission);
													}
												}
												else if(!assignmentMap.containsKey(assignId) && grpId.equals(perm.getGroupId()))
												{
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
		}
		else
			return new HashMap();
	}
	
	private Map getAvailableItemsForStudent(Gradebook gradebook, String userId, String studentId, Map sectionIdCourseSectionMap, Map catIdCategoryMap, List assignments, List permsForUserAnyGroup, List allPermsForUser, List permsForAnyGroupForCategories, List permsForUserAnyGroupAnyCategory, List permsForGroupsAnyCategory, List permsForUserForCategories, Map sectionIdStudentIdsMap) throws IllegalArgumentException
	{
		if(gradebook == null || userId == null || studentId == null)
			throw new IllegalArgumentException("Null parameter(s) in GradebookPermissionServiceImpl.getAvailableItemsForStudent");

		List cateList = new ArrayList(catIdCategoryMap.values());
		List courseSections = new ArrayList(sectionIdCourseSectionMap.values());

		if(gradebook.getCategory_type() == AppConstants.CATEGORY_TYPE_NO_CATEGORY)
		{
			Map assignMap = new HashMap();
			if(permsForUserAnyGroup != null && permsForUserAnyGroup.size() > 0)
			{
				boolean view = false;
				boolean grade = false;
				for(Iterator iter = permsForUserAnyGroup.iterator(); iter.hasNext();)
				{
					Permission perm = (Permission) iter.next();
					if(perm != null && perm.getFunction().equalsIgnoreCase(AppConstants.gradePermission))
					{
						grade = true;
						break;
					}
					if(perm != null && perm.getFunction().equalsIgnoreCase(AppConstants.viewPermission))
					{
						view = true;
					}
				}
				for(Iterator iter = assignments.iterator(); iter.hasNext();)
				{
					Assignment as = (Assignment) iter.next();
					if(grade == true && as != null)
						assignMap.put(as.getId(), AppConstants.gradePermission);
					else if(view == true && as != null)
						assignMap.put(as.getId(), AppConstants.viewPermission);
				}
			}

			if(allPermsForUser != null)
			{
				Map assignsMapForGroups = filterPermissionForGrader(allPermsForUser, studentId, assignments, sectionIdStudentIdsMap);
				for(Iterator iter = assignsMapForGroups.keySet().iterator(); iter.hasNext();)
				{
					Long key = (Long)iter.next();
					if((assignMap.containsKey(key) && ((String)assignMap.get(key)).equalsIgnoreCase(AppConstants.viewPermission))
							|| !assignMap.containsKey(key))
						assignMap.put(key, assignsMapForGroups.get(key));
				}
			}
			return assignMap;
		}
		else
		{

			Map assignMap = new HashMap();
			if(permsForAnyGroupForCategories != null && permsForAnyGroupForCategories.size() > 0)
			{
				for(Iterator iter = permsForAnyGroupForCategories.iterator(); iter.hasNext();)
				{
					Permission perm = (Permission)iter.next();
					if(perm != null)
					{
						if(perm.getCategoryId() != null)
						{
							for(Iterator cateIter = cateList.iterator(); cateIter.hasNext();)
							{
								Category cate = (Category) cateIter.next();
								if(cate != null && cate.getId().equals(perm.getCategoryId()))
								{
									List assignmentList = cate.getAssignmentList();
									if (assignmentList != null) {
										for(Iterator assignIter = assignmentList.iterator(); assignIter.hasNext();)
										{
											Assignment as = (Assignment)assignIter.next();
											if(as != null)
											{
												Long assignId = as.getId();
												if(as.getCategory() != null)
												{
													if(assignMap.containsKey(assignId) && ((String)assignMap.get(assignId)).equalsIgnoreCase(AppConstants.viewPermission))
													{
														if(perm.getFunction().equalsIgnoreCase(AppConstants.gradePermission))
														{
															assignMap.put(assignId, AppConstants.gradePermission);
														}
													}
													else if(!assignMap.containsKey(assignId))
													{
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

			if(permsForUserAnyGroupAnyCategory != null)
			{
				Map assignMapForGroups = filterPermissionForGraderForAllAssignments(permsForUserAnyGroupAnyCategory, assignments);
				for(Iterator iter = assignMapForGroups.keySet().iterator(); iter.hasNext();)
				{
					Long key = (Long)iter.next();
					if((assignMap.containsKey(key) && ((String)assignMap.get(key)).equalsIgnoreCase(AppConstants.viewPermission))
							|| !assignMap.containsKey(key))
						assignMap.put(key, assignMapForGroups.get(key));
				}
			}

			if(permsForGroupsAnyCategory != null)
			{
				Map assignMapForGroups = filterPermissionForGrader(permsForGroupsAnyCategory, studentId, assignments, sectionIdStudentIdsMap);
				for(Iterator iter = assignMapForGroups.keySet().iterator(); iter.hasNext();)
				{
					Long key = (Long)iter.next();
					if((assignMap.containsKey(key) && ((String)assignMap.get(key)).equalsIgnoreCase(AppConstants.viewPermission))
							|| !assignMap.containsKey(key))
						assignMap.put(key, assignMapForGroups.get(key));
				}
			}

			if(permsForUserForCategories != null)
			{
				Map assignMapForGroups = filterPermissionForGraderForCategory(permsForUserForCategories, studentId, cateList, sectionIdStudentIdsMap);
				if(assignMapForGroups != null)
				{
					for(Iterator iter = assignMapForGroups.keySet().iterator(); iter.hasNext();)
					{
						Long key = (Long)iter.next();
						if((assignMap.containsKey(key) && ((String)assignMap.get(key)).equalsIgnoreCase(AppConstants.viewPermission))
								|| !assignMap.containsKey(key))
						{
							assignMap.put(key, assignMapForGroups.get(key));
						}
					}
				}
			}

			return assignMap;
		}
	}

	private Map<Long, String> getAvailableItemsForStudent(Long gradebookId, String userId, String studentId, Collection<CourseSection> courseSections) throws IllegalArgumentException
	{
		if(gradebookId == null || userId == null || studentId == null)
			throw new IllegalArgumentException("Null parameter(s) in GradebookPermissionServiceImpl.getAvailableItemsForStudent");

		List categories = gbService.getCategoriesWithAssignments(gradebookId);
		Map catIdCategoryMap = new HashMap();
		if (!categories.isEmpty()) {
			for (Iterator catIter = categories.iterator(); catIter.hasNext();) {
				Category cat = (Category)catIter.next();
				if (cat != null)
					catIdCategoryMap.put(cat.getId(), cat);
			}
		}
		Map sectionIdCourseSectionMap = new HashMap();
		if (!courseSections.isEmpty()) {
			for (Iterator sectionIter = courseSections.iterator(); sectionIter.hasNext();) {
				CourseSection section = (CourseSection) sectionIter.next();
				if (section != null) {
					sectionIdCourseSectionMap.put(section.getUuid(), section);
				}
			}
		}
		List studentIds = new ArrayList();
		studentIds.add(studentId);
		Map sectionIdStudentIdsMap = getSectionIdStudentIdsMap(courseSections, studentIds);

		Gradebook gradebook = gbToolService.getGradebook(gbToolService.getGradebookUid(gradebookId));
		List assignments = gbToolService.getAssignments(gradebookId);
		List categoryIds = new ArrayList(catIdCategoryMap.keySet());
		List groupIds = new ArrayList(sectionIdCourseSectionMap.keySet());

		// Retrieve all the different permission info needed here so not called repeatedly for each student
		List permsForUserAnyGroup = gbToolService.getPermissionsForUserAnyGroup(gradebookId, userId);
		List allPermsForUser = gbToolService.getPermissionsForUser(gradebookId, userId);
		List permsForAnyGroupForCategories = gbToolService.getPermissionsForUserAnyGroupForCategory(gradebookId, userId, categoryIds);
		List permsForUserAnyGroupAnyCategory = gbToolService.getPermissionsForUserAnyGroupAnyCategory(gradebookId, userId);
		List permsForGroupsAnyCategory = gbToolService.getPermissionsForUserForGoupsAnyCategory(gradebookId, userId, groupIds);
		List permsForUserForCategories = gbToolService.getPermissionsForUserForCategory(gradebookId, userId, categoryIds);

		return getAvailableItemsForStudent(gradebook, userId, studentId, sectionIdCourseSectionMap, catIdCategoryMap, assignments, permsForUserAnyGroup, allPermsForUser, permsForAnyGroupForCategories, permsForUserAnyGroupAnyCategory, permsForGroupsAnyCategory, permsForUserForCategories, sectionIdStudentIdsMap);
	}

	private Map<Long, String> getAvailableItemsForStudents(Long gradebookId, String userId, List studentIds, Collection courseSections) throws IllegalArgumentException
	{
		if(gradebookId == null || userId == null)
			throw new IllegalArgumentException("Null parameter(s) in GradebookPermissionServiceImpl.getAvailableItemsForStudents");

		Map catIdCategoryMap = new HashMap();
		List categories = gbService.getCategoriesWithAssignments(gradebookId);
		if (categories != null && !categories.isEmpty()) {
			for (Iterator catIter = categories.iterator(); catIter.hasNext();) {
				Category cat = (Category)catIter.next();
				if (cat != null) {
					catIdCategoryMap.put(cat.getId(), cat);
				}
			}
		}
		Map sectionIdCourseSectionMap = new HashMap();
		if (!courseSections.isEmpty()) {
			for (Iterator sectionIter = courseSections.iterator(); sectionIter.hasNext();) {
				CourseSection section = (CourseSection) sectionIter.next();
				if (section != null) {
					sectionIdCourseSectionMap.put(section.getUuid(), section);
				}
			}
		}

		Map sectionIdStudentIdsMap = getSectionIdStudentIdsMap(courseSections, studentIds);

		Gradebook gradebook = gbToolService.getGradebook(gbToolService.getGradebookUid(gradebookId));
		List assignments = gbToolService.getAssignments(gradebookId);
		List categoryIds = new ArrayList(catIdCategoryMap.keySet());
		List groupIds = new ArrayList(sectionIdCourseSectionMap.keySet());

		// Retrieve all the different permission info needed here so not called repeatedly for each student
		List permsForUserAnyGroup = gbToolService.getPermissionsForUserAnyGroup(gradebookId, userId);
		List allPermsForUser = gbToolService.getPermissionsForUser(gradebookId, userId);
		List permsForAnyGroupForCategories = gbToolService.getPermissionsForUserAnyGroupForCategory(gradebookId, userId, categoryIds);
		List permsForUserAnyGroupAnyCategory = gbToolService.getPermissionsForUserAnyGroupAnyCategory(gradebookId, userId);
		List permsForGroupsAnyCategory = gbToolService.getPermissionsForUserForGoupsAnyCategory(gradebookId, userId, groupIds);
		List permsForUserForCategories = gbToolService.getPermissionsForUserForCategory(gradebookId, userId, categoryIds);

		if(studentIds != null)
		{
			Map studentsMap = new HashMap();
			for(Iterator iter = studentIds.iterator(); iter.hasNext();)
			{
				Map assignMap = new HashMap();
				String studentId = (String) iter.next();
				if(studentId != null)
				{
					assignMap = getAvailableItemsForStudent(gradebook, userId, studentId, sectionIdCourseSectionMap, catIdCategoryMap, assignments, permsForUserAnyGroup, allPermsForUser, permsForAnyGroupForCategories, permsForUserAnyGroupAnyCategory, permsForGroupsAnyCategory, permsForUserForCategories, sectionIdStudentIdsMap);
					studentsMap.put(studentId, assignMap);
				}
			}
			return studentsMap;
		}

		return new HashMap();
	}


	// Spring DI
	public void setAuthn(Gradebook2Authn authn) {
		this.authn = authn;
	}
	
	// Spring DI
	public void setSectionAwareness(SectionAwareness sectionAwareness) {
		this.sectionAwareness = sectionAwareness;
	}
	
	// Spring DI
	public void setGbToolService(GradebookToolService gbToolService) {
		this.gbToolService = gbToolService;
	}
	
	// Spring DI
	public void setGbService(Gradebook2Service	gbService) {
		this.gbService = gbService;
	}
}
