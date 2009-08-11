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

import org.sakaiproject.gradebook.gwt.sakai.Gradebook2AuthzImpl;
import org.sakaiproject.section.api.SectionAwareness;
import org.sakaiproject.section.api.coursemanagement.EnrollmentRecord;
import org.sakaiproject.section.api.facade.Role;
import org.sakaiproject.tool.gradebook.Assignment;

public class AuthzMock extends Gradebook2AuthzImpl {

	public AuthzMock() {
		
	}
	
	public AuthzMock(SectionAwareness sectionAwareness) {
		setSectionAwareness(sectionAwareness);
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
			
		}

		return enrollmentMap;
	}
	
	private static final String[] SECTIONS = { "001", "002", "003", "004" };

	public List getStudentSectionMembershipNames(String gradebookUid, String studentUid) {
		return Arrays.asList(SECTIONS);
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
		return false;
	}

	public boolean isUserAbleToViewItemForStudent(String gradebookUid, Long itemId, String studentUid) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isUserAbleToViewOwnGrades(String gradebookUid) {
		return true;
	}

	public boolean isUserHasGraderPermissions(String gradebookUid) {
		return false;
	}

	public boolean hasUserGraderPermissions(String gradebookUid) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasUserGraderPermissions(String gradebookUid, String userUid) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isUserHasGraderPermissions(String gradebookUid, String userUid) {
		// TODO Auto-generated method stub
		return false;
	}

	public SectionAwareness getSectionAwareness() {
		return getSectionAwareness();
	}
	
	private List getSectionEnrollmentsTrusted(String sectionUid) {
		return getSectionAwareness().getSectionMembersInRole(sectionUid, Role.STUDENT);
	}


	public boolean isUserTAinSection(String sectionUid) {
		// TODO Auto-generated method stub
		return false;
	}
}
