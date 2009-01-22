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
package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sakaiproject.section.api.SectionAwareness;
import org.sakaiproject.section.api.coursemanagement.EnrollmentRecord;
import org.sakaiproject.section.api.facade.Role;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.facades.Authz;

public class AuthzMock implements Authz {

	private SectionAwareness sectionAwareness;
	
	public AuthzMock(SectionAwareness sectionAwareness) {
		this.sectionAwareness = sectionAwareness;
	}
	
	public Map findMatchingEnrollmentsForItem(String gradebookUid, Long categoryId, int gbCategoryType, String optionalSearchString, String optionalSectionUid) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map findMatchingEnrollmentsForViewableCourseGrade(String gradebookUid, int gbCategoryType, String optionalSearchString, String optionalSectionUid) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map findMatchingEnrollmentsForViewableItems(String gradebookUid, List allGbItems, String optionalSearchString, String optionalSectionUid) {
		Map enrollmentMap = new HashMap();
		List filteredEnrollments = new ArrayList();
		if (optionalSearchString != null)
			filteredEnrollments = getSectionAwareness().findSiteMembersInRole(gradebookUid, Role.STUDENT, optionalSearchString);
		else
			filteredEnrollments = getSectionAwareness().getSiteMembersInRole(gradebookUid, Role.STUDENT);
		
		if (filteredEnrollments.isEmpty()) 
			return enrollmentMap;
		
		// get all the students in the filtered section, if appropriate
		Map studentsInSectionMap = new HashMap();
		if (optionalSectionUid !=  null) {
			List sectionMembers = getSectionEnrollmentsTrusted(optionalSectionUid);
			if (!sectionMembers.isEmpty()) {
				for(Iterator memberIter = sectionMembers.iterator(); memberIter.hasNext();) {
					EnrollmentRecord member = (EnrollmentRecord) memberIter.next();
					studentsInSectionMap.put(member.getUser().getUserUid(), member);
				}
			}
		}
		
		Map studentIdEnrRecMap = new HashMap();
		for (Iterator enrIter = filteredEnrollments.iterator(); enrIter.hasNext();) {
			EnrollmentRecord enr = (EnrollmentRecord) enrIter.next();
			String studentId = enr.getUser().getUserUid();
			if (optionalSectionUid != null) {
				if (studentsInSectionMap.containsKey(studentId)) {
					studentIdEnrRecMap.put(studentId, enr);
				}
			} else {
				studentIdEnrRecMap.put(studentId, enr);
			}
		}			
			
		if (isUserAbleToGradeAll(gradebookUid)) {
			List enrollments = new ArrayList(studentIdEnrRecMap.values());
			
			HashMap assignFunctionMap = new HashMap();
			if (allGbItems != null && !allGbItems.isEmpty()) {
				for (Iterator assignIter = allGbItems.iterator(); assignIter.hasNext();) {
					Object assign = assignIter.next();
					Long assignId = null;
					/*if (assign instanceof org.sakaiproject.service.gradebook.shared.Assignment) {
						assignId = ((org.sakaiproject.service.gradebook.shared.Assignment)assign).getId();
					} else*/ if (assign instanceof Assignment) {
						assignId = ((Assignment)assign).getId();
					}

					if (assignId != null)
						assignFunctionMap.put(assignId, "grade");
				}
			}
			
			for (Iterator enrIter = enrollments.iterator(); enrIter.hasNext();) {
				EnrollmentRecord enr = (EnrollmentRecord) enrIter.next();
				enrollmentMap.put(enr, assignFunctionMap);
			}
			
		} else {
			/*String userId = authn.getUserUid();
			
			Map sectionIdCourseSectionMap = new HashMap();
			List viewableSections = getViewableSections(gradebookUid);
			for (Iterator sectionIter = viewableSections.iterator(); sectionIter.hasNext();) {
				CourseSection section = (CourseSection) sectionIter.next();
				sectionIdCourseSectionMap.put(section.getUuid(), section);
			}
			
			if (isUserHasGraderPermissions(gradebookUid)) {
				// user has special grader permissions that override default perms
				
				List myStudentIds = new ArrayList(studentIdEnrRecMap.keySet());
				
				List selSections = new ArrayList();
				if (optionalSectionUid == null) {  
					// pass all sections
					selSections = new ArrayList(sectionIdCourseSectionMap.values());
				} else {
					// only pass the selected section
					CourseSection section = (CourseSection) sectionIdCourseSectionMap.get(optionalSectionUid);
					if (section != null)
						selSections.add(section);
				}
				
				// we need to get the viewable students, so first create section id --> student ids map
				myStudentIds = getGradebookPermissionService().getViewableStudentsForUser(gradebookUid, userId, myStudentIds, selSections);
				Map viewableStudentIdItemsMap = new HashMap();
				if (allGbItems == null || allGbItems.isEmpty()) {
					if (myStudentIds != null) {
						for (Iterator stIter = myStudentIds.iterator(); stIter.hasNext();) {
							String stId = (String) stIter.next();
							if (stId != null)
								viewableStudentIdItemsMap.put(stId, null);
						}
					}
				} else {
					viewableStudentIdItemsMap = gradebookPermissionService.getAvailableItemsForStudents(gradebookUid, userId, myStudentIds, selSections);
				}
				
				if (!viewableStudentIdItemsMap.isEmpty()) {
					for (Iterator enrIter = viewableStudentIdItemsMap.keySet().iterator(); enrIter.hasNext();) {
						String studentId = (String) enrIter.next();
						EnrollmentRecord enrRec = (EnrollmentRecord)studentIdEnrRecMap.get(studentId);
						if (enrRec != null) {	
							Map itemIdFunctionMap = (Map)viewableStudentIdItemsMap.get(studentId);
							//if (!itemIdFunctionMap.isEmpty()) {
								enrollmentMap.put(enrRec, itemIdFunctionMap);
							//}
						}
					}
				}

			} else { 
				// use default section-based permissions
				
				// Determine the current user's section memberships
				List availableSections = new ArrayList();
				if (optionalSectionUid != null && isUserTAinSection(optionalSectionUid)) {
					if (sectionIdCourseSectionMap.containsKey(optionalSectionUid))
						availableSections.add(optionalSectionUid);
				} else {
					for (Iterator iter = sectionIdCourseSectionMap.keySet().iterator(); iter.hasNext(); ) {
						String sectionUuid = (String)iter.next();
						if (isUserTAinSection(sectionUuid)) {
							availableSections.add(sectionUuid);
						}
					}
				}
				
				// Determine which enrollees are in these sections
				Map uniqueEnrollees = new HashMap();
				for (Iterator iter = availableSections.iterator(); iter.hasNext(); ) {
					String sectionUuid = (String)iter.next();
					List sectionEnrollments = getSectionEnrollmentsTrusted(sectionUuid);
					for (Iterator eIter = sectionEnrollments.iterator(); eIter.hasNext(); ) {
						EnrollmentRecord enr = (EnrollmentRecord)eIter.next();
						uniqueEnrollees.put(enr.getUser().getUserUid(), enr);
					}
				}
				
				// Filter out based upon the original filtered students
				for (Iterator iter = studentIdEnrRecMap.keySet().iterator(); iter.hasNext(); ) {
					String enrId = (String)iter.next();
					if (uniqueEnrollees.containsKey(enrId)) {
						// iterate through the assignments
						Map itemFunctionMap = new HashMap();
						if (allGbItems != null && !allGbItems.isEmpty()) {
							for (Iterator itemIter = allGbItems.iterator(); itemIter.hasNext();) {
								Object assign = itemIter.next();
								Long assignId = null;
								if (assign instanceof org.sakaiproject.service.gradebook.shared.Assignment) {
									assignId = ((org.sakaiproject.service.gradebook.shared.Assignment)assign).getId();
								} else if (assign instanceof org.sakaiproject.tool.gradebook.Assignment) {
									assignId = ((org.sakaiproject.tool.gradebook.Assignment)assign).getId();
								}

								if (assignId != null) {
									itemFunctionMap.put(assignId, GradebookService.gradePermission);
								}
							}
						}
						enrollmentMap.put(studentIdEnrRecMap.get(enrId), itemFunctionMap);
					}
				}
			}*/
		}

		return enrollmentMap;
	}

	public List findStudentSectionMemberships(String gradebookUid, String studentUid) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getAllSections(String gradebookUid) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getGradeViewFunctionForUserForStudentForItem(String gradebookUid, Long itemId, String studentUid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static final String[] SECTIONS = { "001", "002", "003", "004" };

	public List getStudentSectionMembershipNames(String gradebookUid, String studentUid) {
		return Arrays.asList(SECTIONS);
	}

	public List getViewableSections(String gradebookUid) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isUserAbleToEditAssessments(String gradebookUid) {
		return true;
	}

	public boolean isUserAbleToGrade(String gradebookUid) {
		return true;
	}

	public boolean isUserAbleToGradeAll(String gradebookUid) {
		return true;
	}

	public boolean isUserAbleToGradeItemForStudent(String gradebookUid, Long itemId, String studentUid) throws IllegalArgumentException {
		return true;
	}

	public boolean isUserAbleToViewItemForStudent(String gradebookUid, Long itemId, String studentUid) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isUserAbleToViewOwnGrades(String gradebookUid) {
		return true;
	}

	public boolean isUserHasGraderPermissions(String gradebookUid) {
		return false;
	}

	public boolean isUserHasGraderPermissions(Long gradebookId) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isUserHasGraderPermissions(Long gradebookId, String userUid) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isUserHasGraderPermissions(String gradebookUid, String userUid) {
		// TODO Auto-generated method stub
		return false;
	}

	public SectionAwareness getSectionAwareness() {
		return sectionAwareness;
	}
	
	private List getSectionEnrollmentsTrusted(String sectionUid) {
		return getSectionAwareness().getSectionMembersInRole(sectionUid, Role.STUDENT);
	}

}
