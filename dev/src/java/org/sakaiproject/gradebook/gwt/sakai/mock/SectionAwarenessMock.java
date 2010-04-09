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
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.sakaiproject.section.api.SectionAwareness;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.section.api.coursemanagement.LearningContext;
import org.sakaiproject.section.api.coursemanagement.ParticipationRecord;
import org.sakaiproject.section.api.facade.Role;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;

public class SectionAwarenessMock implements SectionAwareness {

	private UserDirectoryService userDirectoryService = null;

	private List<ParticipationRecord> allRecords;
	List<CourseSection> sections;
	private final static int NUMBER_OF_TAS = 5;
	private static final String[] SECTIONS = { "001", "002", "003", "004" };
	private static final String[] SECTION_EIDs = { "/section/018u012", null, "/section/sd32122", null };

	public SectionAwarenessMock() {

		this.allRecords = new ArrayList<ParticipationRecord>();
	}

	public void init() {

		List<User> users = userDirectoryService.getUsers();

		int numberOfUsers = users.size();

		for(int i = 0; i < numberOfUsers; i++) {
			User user = users.get(i);
			org.sakaiproject.section.api.coursemanagement.User userSectionMock =  new UserSectionMock(user.getId(), user.getDisplayId(), user.getDisplayName(), user.getSortName());
			CourseSection courseSection = getRandomSection();
			//LearningContext learningContext = new LearningContextMock(section, "Section " + section);

			if(i < (numberOfUsers - NUMBER_OF_TAS)) {
				allRecords.add(new EnrollmentRecordMock((LearningContext)courseSection, Role.STUDENT, userSectionMock));
			}
			else {
				allRecords.add(new EnrollmentRecordMock((LearningContext)courseSection, Role.TA, userSectionMock));
			}
		}
	}

	public List findSiteMembersInRole(String siteContext, Role role, String pattern) {

		List<ParticipationRecord> records = new LinkedList<ParticipationRecord>();

		for (ParticipationRecord record : allRecords) {
			org.sakaiproject.section.api.coursemanagement.User user = record.getUser();

			if(null != pattern && "".equals(pattern) && user.getDisplayName().contains(pattern) &&  role.equals(record.getRole())) {

				records.add(record);
			}
			else if(role.equals(record.getRole())) {
				records.add(record);
			}
		}

		return records;
	}

	public String getCategoryName(String categoryId, Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}

	public CourseSection getSection(String sectionUuid) {
		List<CourseSection> courseSections = getSections(null);
		for(CourseSection courseSection : courseSections) {
			if(courseSection.getUuid().equals(sectionUuid)) {
				return courseSection;
			}
		}
		return null;
	}

	public List getSectionCategories(String siteContext) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getSectionMembers(String sectionId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getSectionMembersInRoleCount(String sectionUuid, Role role) {
		return getSectionMembersInRole(sectionUuid, role).size();
	}

	public List getSectionMembersInRole(String sectionUuid, Role role) {
		List<ParticipationRecord> records = new ArrayList<ParticipationRecord>();

		for (ParticipationRecord record : allRecords) {
			LearningContext context = record.getLearningContext();
			String uuid = context.getUuid();

			if (sectionUuid == null || sectionUuid.equals(uuid)) 
				records.add(record);
		}
		return records;
	}

	public List getSections(String siteContext) {

		if(null == sections) {

			sections = new LinkedList<CourseSection>();

			for (String sectionId : getSectionIdList()) {
				sections.add(new CourseSectionMock(sectionId, "Section " + sectionId));
			}
		}

		return sections;
	}

	public List getSectionsInCategory(String siteContext, String categoryId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getSiteMembersInRoleCount(String siteContext, Role role) {

		List<User> users = userDirectoryService.getUsers();
		return Integer.valueOf(users.size());
	}

	public List getSiteMembersInRole(String siteContext, Role role) {

		List<ParticipationRecord> records = new ArrayList<ParticipationRecord>();

		for(ParticipationRecord participationRecord : allRecords) {
			if(role.equals(participationRecord.getRole())) {
				records.add(participationRecord);
			}
		}

		return records;
	}

	public List getUnassignedMembersInRole(String siteContext, Role role) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isSectionMemberInRole(String sectionId, String personId, Role role) {

		return role.getName().equalsIgnoreCase("instructor");
	}

	public boolean isSiteMemberInRole(String siteContext, String userUid, Role role) {
		
		return role.getName().equalsIgnoreCase("instructor");
	}


	/*
	 * Helper methods
	 */

	private List<String> getSectionIdList() {
		return new ArrayList<String>(Arrays.asList(SECTIONS));
	}

	private CourseSection getRandomSection() {
		String sectionUid = SECTIONS[getRandomInt(SECTIONS.length)];
		String eid = SECTION_EIDs[getRandomInt(SECTION_EIDs.length)];
		CourseSection cs = new CourseSectionMock(sectionUid, "Section " + eid, eid);
		return cs;
	}

	private Random random = new Random();

	private int getRandomInt(int max) {
		return random.nextInt(max);
	}

	public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
		this.userDirectoryService = userDirectoryService;
	}

}
