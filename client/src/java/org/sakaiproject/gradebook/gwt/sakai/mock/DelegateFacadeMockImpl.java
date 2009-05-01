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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.sakaiproject.gradebook.gwt.sakai.AccessAdvisor;
import org.sakaiproject.gradebook.gwt.sakai.DelegateFacadeImpl;
import org.sakaiproject.gradebook.gwt.sakai.ExportAdvisor;
import org.sakaiproject.gradebook.gwt.sakai.GradeCalculations;
import org.sakaiproject.gradebook.gwt.sakai.GradebookToolService;
import org.sakaiproject.gradebook.gwt.sakai.UserRecord;
import org.sakaiproject.section.api.SectionAwareness;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.tool.gradebook.facades.Authn;
import org.sakaiproject.tool.gradebook.facades.Authz;
import org.sakaiproject.user.api.User;

public class DelegateFacadeMockImpl extends DelegateFacadeImpl {

	private static final int DEFAULT_NUMBER_TEST_LEARNERS = 200;
	
	private int numberOfTestLearners;
	
	public DelegateFacadeMockImpl(SectionAwareness sectionAwareness, Authz authz, Authn authn, 
			GradebookToolService gradebookManager, GradeCalculations gradeCalculations, AccessAdvisor accessAdvisor, ExportAdvisor exportAdvisor) {
		setSectionAwareness(sectionAwareness);
		setAuthz(authz);
		setAuthn(authn);
		setGbService(gradebookManager);
		setGradeCalculations(gradeCalculations);
		setAccessAdvisor(accessAdvisor);
		setExportAdvisor(exportAdvisor);
		numberOfTestLearners = DEFAULT_NUMBER_TEST_LEARNERS;
	}
	
	@Override 
	public String getPlacementId() {
		
		return null;
	}
	
	@Override
	protected List<UserRecord> doSearchUsers(String searchString, List<String> studentUids, Map<String, UserRecord> userRecordMap) {
		
		// Make sure that our search criterion is case insensitive
		if (searchString != null)
			searchString = searchString.toUpperCase();
	
		//List<UserRecord> userRecords = new ArrayList<UserRecord>();
		
		// FIXME: We need to populate the user records somehow here for the mock facade
		// To do a search, we have to get all the users . . . this is also desirable even if we're not searching, if we want to sort on these properties
		List<User> users = null; //userService.getUsers(studentUids);
		
		if (users != null) {
			for (User user : users) {
				String sortName = user.getSortName();
				// Make sure that our search field is case insensitive
				if (sortName != null) 
					sortName = sortName.toUpperCase();
				
				// If we're not searching, then return everybody
				if (searchString == null || sortName.contains(searchString)) {
					UserRecord userRecord = userRecordMap.get(user.getId());
					userRecord.populate(user);
					userRecords.add(userRecord);
				}
			}
		}
		
		return userRecords;
	}
	
	private List<UserRecord> userRecords;
	
	private static final String[] FIRST_NAMES = { "Joel", "John", "Kelly",
		"Freeland", "Bruce", "Rajeev", "Thomas", "Jon", "Mary", "Jane",
		"Susan", "Cindy", "Veronica", "Shana", "Shania", "Olin", "Brenda",
		"Lowell", "Doug", "Yiyun", "Xi-Ming", "Grady", "Martha", "Stewart", 
		"Kennedy", "Joseph", "Iosef", "Sean", "Timothy", "Paula", "Keith",
		"Ignatius", "Iona", "Owen", "Ian", "Ewan", "Rachel", "Wendy", 
		"Quentin", "Nancy", "Mckenna", "Kaylee", "Aaron", "Erin", "Maris", 
		"D.", "Quin", "Tara", "Moira", "Bristol" };

	private static final String[] LAST_NAMES = { "Smith", "Paterson",
		"Haterson", "Raterson", "Johnson", "Sonson", "Paulson", "Li",
		"Yang", "Redford", "Shaner", "Bradley", "Herzog", "O'Neil", "Williams",
		"Simone", "Oppenheimer", "Brown", "Colgan", "Frank", "Grant", "Klein",
		"Miller", "Taylor", "Schwimmer", "Rourer", "Depuis", "Vaugh", "Auerbach", 
		"Shannon", "Stepford", "Banks", "Ashby", "Lynne", "Barclay", "Barton",
		"Cromwell", "Dering", "Dunlevy", "Ethelstan", "Fry", "Gilly",
		"Goodrich", "Granger", "Griffith", "Herbert", "Hurst", "Keigwin", 
		"Paddock", "Pillings", "Landon", "Lawley", "Osborne", "Scarborough",
		"Whiting", "Wibert", "Worth", "Tremaine", "Barnum", "Beal", "Beers", 
		"Bellamy", "Barnwell", "Beckett", "Breck", "Cotesworth", 
		"Coventry", "Elphinstone", "Farnham", "Ely", "Dutton", "Durham",
		"Eberlee", "Eton", "Edgecomb", "Eastcote", "Gloucester", "Lewes", 
		"Leland", "Mansfield", "Lancaster", "Oakham", "Nottingham", "Norfolk",
		"Poole", "Ramsey", "Rawdon", "Rhodes", "Riddell", "Vesey", "Van Wyck",
		"Van Ness", "Twickenham", "Trowbridge", "Ames", "Agnew", "Adlam", 
		"Aston", "Askew", "Alford", "Bedeau", "Beauchamp" };
	
	private static final String[] SECTIONS = { "001", "002", "003", "004" };
	
	private Random random = new Random();
	
	private int getRandomInt(int max) {
		return random.nextInt(max);
	}
	
	private String getRandomSection() {
		return SECTIONS[getRandomInt(SECTIONS.length)];
	}
	
	private UserRecord createUserRecord() {
		String studentId = String.valueOf(100000 + getRandomInt(899999));
		String firstName = FIRST_NAMES[getRandomInt(FIRST_NAMES.length)];
		String lastName = LAST_NAMES[getRandomInt(LAST_NAMES.length)];
		String sortName = lastName + ", " + firstName;
		String displayName = firstName + " " + lastName;
		String section = getRandomSection();
	
		User user = new SakaiUserMock(studentId, studentId, displayName, sortName);
		
		UserRecord userRecord = new UserRecord(user);
		userRecord.setSectionTitle("Section " + section);
		
		return userRecord;
	}
	
	/*@Override
	protected List<UserRecord> findStudentRecordPage(Gradebook gradebook, Site site, String sectionUuid, String sortField, String searchField, String searchCriteria,
			int offset, int limit, 
			boolean isAscending) {
		List<UserRecord> records = new ArrayList<UserRecord>();
		
		if (userRecords == null) {
			userRecords = new ArrayList<UserRecord>(2000);
			for (int i=0;i<numberOfTestLearners;i++) {
				userRecords.add(createUserRecord());
			}
		}
		
		int firstRow = offset;
		int lastRow = offset + limit;
		
		if (lastRow < numberOfTestLearners)
			lastRow = numberOfTestLearners;
		
		for (int i=firstRow;i<lastRow;i++) {
			records.add(userRecords.get(i));
		}
		
		return records;
	}*/
	
	@Override
	protected Map<String, UserRecord> findStudentRecords(String gradebookUid, Long gradebookId, Site site, String optionalSectionUid) {
		Map<String, UserRecord> studentRecords = new HashMap<String, UserRecord>();
		
		boolean canGradeAll = true; //isUserAbleToGradeAll(gradebookUid);
		
		if (canGradeAll && optionalSectionUid == null) {
			// If so, then grab all the members for the site
			
			if (userRecords == null) {
				userRecords = new ArrayList<UserRecord>(2000);
				for (int i=0;i<numberOfTestLearners;i++) {
					userRecords.add(createUserRecord());
				}
			}
			
			for (UserRecord userRecord : userRecords) {
				studentRecords.put(userRecord.getUserUid(), userRecord);
			}
			
			// FIXME: Need to grab this from test data
			/*try {
				site = siteService.getSite(context);
			
				Set<Member> members = site.getMembers();
				for (Member member : members) {
					if (member.getRole().getId().equals("Student")) {
						String userUid = member.getUserId();
						studentRecords.put(userUid, new UserRecord(userUid));
					}
				}
			} catch (IdUnusedException idue) {
				log.error("Unable to find a site for this gradebook", idue);
			}*/
		} 
		
		List<CourseSection> viewableSections = getViewableSections(gradebookUid, gradebookId);
		
		for (CourseSection section : viewableSections) {
			// FIXME: Need to grab this from test data
			/*Group group = siteService.findGroup(section.getUuid());
			Set<Member> members = group.getMembers();
			for (Member member : members) {
				if (member.getRole().getId().equals("Student")) {
					// Filter by section, if such a filter exists
					if (optionalSectionUid == null || section.getUuid().equals(optionalSectionUid)) {
						String userUid = member.getUserId();
						UserRecord userRecord = studentRecords.get(userUid);
						
						if (userRecord == null) {
							userRecord = new UserRecord(userUid);
							studentRecords.put(userUid, userRecord);
						}
						
						userRecord.setSectionTitle(section.getTitle());
					}
				}
			}*/
		}
		
		return studentRecords;
	}
	
	@Override
	protected String getGradebookUid() {
		return "12312409345";
	}
	
	@Override
	protected String getSiteContext() {
		return "blah";
	}

	public int getNumberOfTestLearners() {
		return numberOfTestLearners;
	}

	public void setNumberOfTestLearners(int numberOfTestLearners) {
		this.numberOfTestLearners = numberOfTestLearners;
	}
	
	
}
