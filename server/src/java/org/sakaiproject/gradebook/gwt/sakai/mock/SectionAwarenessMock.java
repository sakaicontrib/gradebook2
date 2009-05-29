package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.section.api.coursemanagement.EnrollmentRecord;
import org.sakaiproject.section.api.coursemanagement.LearningContext;
import org.sakaiproject.section.api.coursemanagement.ParticipationRecord;
import org.sakaiproject.section.api.coursemanagement.User;
import org.sakaiproject.section.api.facade.Role;
import org.sakaiproject.section.api.SectionAwareness;

public class SectionAwarenessMock implements SectionAwareness {

	private List<ParticipationRecord> allRecords;
	private Map<Integer, ParticipationRecord> participantRecordMap;
	private final static int NUMBER_OF_STUDENTS = 2000;
	
	public SectionAwarenessMock() {
		this.participantRecordMap = new HashMap<Integer, ParticipationRecord>();
		this.allRecords = new ArrayList<ParticipationRecord>();
		
		for (int i=0;i<NUMBER_OF_STUDENTS;i++) {
			allRecords.add(createParticipationRecord());
		}
	}
	
	public List findSiteMembersInRole(String siteContext, Role role, String pattern) {
		
		List<ParticipationRecord> records = new LinkedList<ParticipationRecord>();
		
		for (ParticipationRecord record : allRecords) {
			User user = record.getUser();
			
			if (user.getDisplayName().contains(pattern))
				records.add(record);
		}
		
		return records;
	}

	public String getCategoryName(String categoryId, Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}

	public CourseSection getSection(String sectionUuid) {
		// TODO Auto-generated method stub
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
		
		/*for (int i=0;i<NUMBER_OF_STUDENTS;i++) {
			Integer key = Integer.valueOf(i);
			
			ParticipationRecord record = participantRecordMap.get(key);
			
			if (record == null) {
				record = createParticipationRecord();
				participantRecordMap.put(key, record);
			} 
			
			if (record.getLearningContext().getUuid().equals(sectionUuid))
				records.add(record);
			
		}*/
		return records;
	}

	public List getSections(String siteContext) {
		List<CourseSection> sections = new LinkedList<CourseSection>();
		
		for (String sectionId : getSectionIdList()) {
			sections.add(new CourseSectionMock(sectionId, "Section " + sectionId));
		}
		
		return sections;
	}

	public List getSectionsInCategory(String siteContext, String categoryId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getSiteMembersInRoleCount(String siteContext, Role role) {
		return Integer.valueOf(NUMBER_OF_STUDENTS);
	}
	
	public List getSiteMembersInRole(String siteContext, Role role) {
		/*List<ParticipationRecord> records = new ArrayList<ParticipationRecord>();
		
		for (int i=0;i<NUMBER_OF_STUDENTS;i++) {
			Integer key = Integer.valueOf(i);
			
			if (participantRecordMap.containsKey(key))
				records.add(participantRecordMap.get(key));
			else 
				records.add(createParticipationRecord());
		}*/
		return allRecords;
	}

	public List getUnassignedMembersInRole(String siteContext, Role role) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isSectionMemberInRole(String sectionId, String personId, Role role) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSiteMemberInRole(String siteContext, String userUid, Role role) {
		// TODO Auto-generated method stub
		return false;
	}

	
	/*
	 * Helper methods
	 */
	
	private static final String[] FIRST_NAMES = { "Joel", "John", "Kelly",
		"Freeland", "Bruce", "Rajeev", "Thomas", "Jon", "Mary", "Jane",
		"Susan", "Cindy", "Veronica", "Shana", "Shania", "Olin", "Brenda",
		"Lowell", "Doug", "Yiyun", "Xi-Ming", "Grady" };

	private static final String[] LAST_NAMES = { "Smith", "Paterson",
		"Haterson", "Raterson", "Johnson", "Sonson", "Paulson", "Li",
		"Yang", "Redford", "Shaner", "Bradly", "Herzog" };
	
	private EnrollmentRecord createParticipationRecord() {
		/*String studentId = String.valueOf(100000 + getRandomInt(899999));
		String firstName = FIRST_NAMES[getRandomInt(FIRST_NAMES.length)];
		String lastName = LAST_NAMES[getRandomInt(LAST_NAMES.length)];
		String sortName = lastName + ", " + firstName;
		String displayName = firstName + " " + lastName;
		String section = getRandomSection();
	
		LearningContext learningContext = new LearningContextMock(section, "Section " + section);
		User user = new UserMock(studentId, studentId, displayName, sortName);
		
		return new EnrollmentRecordMock(learningContext, Role.STUDENT, user);
		*/
		
		return null;
	}
	
	
	private static final String[] SECTIONS = { "001", "002", "003", "004" };
	
	private List<String> getSectionIdList() {
		return new ArrayList<String>(Arrays.asList(SECTIONS));
	}
	
	private Map<String, String> getSectionIdMap() {
		Map<String, String> map = new HashMap<String, String>();
		for (int i=0;i<SECTIONS.length;i++) {
			map.put(SECTIONS[i], "Section " + SECTIONS[i]);
		}
		return map;
	}
	
	private String getRandomSection() {
		return SECTIONS[getRandomInt(SECTIONS.length)];
	}
	
	private Random random = new Random();
	
	private int getRandomInt(int max) {
		return random.nextInt(max);
	}

}
