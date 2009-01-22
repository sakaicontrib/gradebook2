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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.sakai.DelegateFacadeImpl;
import org.sakaiproject.gradebook.gwt.sakai.GradeCalculations;
import org.sakaiproject.gradebook.gwt.sakai.GradebookToolService;
import org.sakaiproject.gradebook.gwt.sakai.UserRecord;
import org.sakaiproject.section.api.SectionAwareness;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.service.gradebook.shared.GradebookNotFoundException;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.Category;
import org.sakaiproject.tool.gradebook.Gradebook;
import org.sakaiproject.tool.gradebook.facades.Authn;
import org.sakaiproject.tool.gradebook.facades.Authz;
import org.sakaiproject.user.api.User;

public class DelegateFacadeMockImpl extends DelegateFacadeImpl {

	public DelegateFacadeMockImpl(SectionAwareness sectionAwareness, Authz authz, Authn authn, 
			GradebookToolService gradebookManager, GradeCalculations gradeCalculations) {
		setSectionAwareness(sectionAwareness);
		setAuthz(authz);
		setAuthn(authn);
		setGbService(gradebookManager);
		setGradeCalculations(gradeCalculations);
	}
	
	@Override
	public List<GradebookModel> getGradebookModels() {
		String gradebookUid = getGradebookUid();
		List<GradebookModel> models = new LinkedList<GradebookModel>();
		
		if (gradebookUid != null) {
			Gradebook gradebook = null;
			try {
				// First thing, grab the default gradebook if one exists
				gradebook = gbService.getGradebook(gradebookUid);
			} catch (GradebookNotFoundException gnfe) {	
				// If it doesn't exist, then create it
				//frameworkService.addGradebook(gradebookUid, "My Default Gradebook");
				//gradebook = gradebookManager.getGradebook(gradebookUid);
			}
			
			// If we have a gradebook already, then we have to ensure that it's set up correctly for the new tool
			if (gradebook != null) {
				// We need to ensure that the category setting is correct
				if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY) {
					gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY);
					gbService.updateGradebook(gradebook);
				}
				
				// There are different ways that unassigned assignments can appear - old gradebooks, external apps
				List<Assignment> unassignedAssigns = gbService.getAssignmentsWithNoCategory(gradebook.getId());
				
				// If we have any that are unassigned, we want to assign them to the default category
				if (unassignedAssigns != null && !unassignedAssigns.isEmpty()) {
					List<Category> categories = gbService.getCategories(gradebook.getId());
					
					// Let's see if we already have a default category in existence
					Long defaultCategoryId = null;
					if (categories != null && ! categories.isEmpty()) {
						// First, look for it by name
						for (Category category : categories) {
							if (category.getName().equalsIgnoreCase("Default")) {
								defaultCategoryId = category.getId();
								break;
							}
						}
					}
					
					boolean isCategoryNew = false;
					
					// If we don't have one already, then let's create one
					if (defaultCategoryId == null) {
						defaultCategoryId = gbService.createCategory(gradebook.getId(), "Default", Double.valueOf(1d), 0);
						isCategoryNew = true;
					} 

					// TODO: This is a just in case check -- we should probably throw an exception here instead, since it means we weren't able to 
					// TODO: create the category for some reason -- but that probably would throw an exception anyway, so...
					if (defaultCategoryId != null) {
						Category defaultCategory = gbService.getCategory(defaultCategoryId);

						// Just in case we just created it, or if it happens to have been deleted since it was created
						if (isCategoryNew || defaultCategory.isRemoved()) {
							defaultCategory.setEqualWeightAssignments(Boolean.TRUE);
							defaultCategory.setRemoved(false);
							gbService.updateCategory(defaultCategory);
						}
						
						// Assuming we have the default category by now (which we almost definitely should) then we move all the unassigned items into it
						if (defaultCategory != null) {
							for (Assignment a : unassignedAssigns) {
								// Think we need to grab each assignment again - this is stupid, but I'm pretty sure it's what hibernate requires
								Assignment assignment = gbService.getAssignment(a.getId());
								assignment.setCategory(defaultCategory);
								gbService.updateAssignment(assignment);
							}
							// This will only recalculate assuming that the category has isEqualWeighting as TRUE
							recalculateEqualWeightingGradeItems(gradebook.getUid(), gradebook.getId(), defaultCategory.getId(), null);
						}
					}

				}
				
				GradebookModel model = createGradebookModel(gradebook);
				models.add(model);
			}
		}
		
		return models;
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
	
	
	@Override
	protected Map<String, UserRecord> findStudentRecords(String gradebookUid, Long gradebookId, String optionalSectionUid) {
		Map<String, UserRecord> studentRecords = new HashMap<String, UserRecord>();
		
		boolean canGradeAll = isUserAbleToGradeAll(gradebookUid);
		
		if (canGradeAll && optionalSectionUid == null) {
			// If so, then grab all the members for the site
			String context = getSiteContext();
			Site site = null;
			
			if (userRecords == null) {
				userRecords = new ArrayList<UserRecord>(2000);
				for (int i=0;i<200;i++) {
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
	
	
}
