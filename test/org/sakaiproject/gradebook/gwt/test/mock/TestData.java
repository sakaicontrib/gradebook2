package org.sakaiproject.gradebook.gwt.test.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.AssignmentGradeRecord;
import org.sakaiproject.tool.gradebook.Category;
import org.sakaiproject.tool.gradebook.Gradebook;

public class TestData {

	// Creating static test data for one student ...
	public static final String TEST_STUDENT_1 = "1";
	
	public static final int HOME_WORK_ID = 0;
	public static final int EXAMS_ID = 1;
	public static final int LABS_ID = 2;
	public static final int EXTRA_CREDIT_ID = 3;
	
	private static final String[] CATEGORY_NAMES = {
		"HOME WORK",
		"EXAMS",
		"LABS",
		"EXTRA CREDIT" };
	
	private static final Double[] CATEGORY_WEIGHTIN = {
		new Double(.30),  // Home Work
		new Double(.50),  // Exams
		new Double(.20),  // Labs
		new Double(.10) };// Extra Credit
	
	private static final Boolean[] CATEGORY_IS_REMOVEd = {
		false,   // Home Work
		false,   // Exams
		false,   // Labs
		false }; // Extra Credit
	
	private static final Boolean[] CATEGORY_IS_EXTRA_CREDIT = {
		false,   // Home Work
		false,   // Exams
		false,   // Labs
		true };  // Extra Credit
	
	private static final String[] ASSIGNMENT_NAMES = { 
		"HW 1",
		"HW 2",
		"HW 3",
		"HW 4",
		"HW 5 EC",
		"Midterm 1",
		"Midterm 2",
		"Final",
		"Lab 1",
		"Lab 2",
		"Lab 3",
		"Lab 4",
		"Lab 5",
		"EC 1",
		"EC 2" };

	private static final Double[] ASSIGNMENT_POINTS_POSSIBLE = {
		new Double(50),  // HW 1
		new Double(20),  // HW 2
		new Double(30),  // HW 3
		new Double(40),  // HW 4
		new Double(10),  // HW 5 EC
		new Double(50),  // Midterm 1
		new Double(50),  // Midterm 2
		new Double(100), // Final
		new Double(10),  // Lab 1
		new Double(10),  // Lab 2
		new Double(10),  // Lab 3
		new Double(10),  // Lab 4
		new Double(30),  // Lab 5
		new Double(5),   // EC 1
		new Double(5) }; // EC 2
	
	private static final Double [] POINTS_EARNED = { 
		new Double(41),  // HW 1
		new Double(13),  // HW 2
		new Double(26),  // HW 3
		new Double(39),  // HW 4
		new Double(8),   // HW 5 EC
		new Double(39),  // Midterm 1
		new Double(47),  // Midterm 2
		new Double(88), // Final
		new Double(6),  // Lab 1
		new Double(7),  // Lab 2
		new Double(8),  // Lab 3
		new Double(9),  // Lab 4
		new Double(28),  // Lab 5
		new Double(4),   // EC 1
		new Double(3) }; // EC 2

	private static final Double[] ASSIGNMENT_WEIGHTING = { 
		new Double(.20),  // HW 1 
		new Double(.20),  // HW 2
		new Double(.20),  // HW 3
		new Double(.20),  // HW 4
		new Double(.20),  // HW 5 EC
		new Double(.20),  // Midterm 1
		new Double(.20),  // Midterm 2
		new Double(.60),  // Final
		new Double(.10),  // Lab 1
		new Double(.20),  // Lab 2
		new Double(.20),  // Lab 3
		new Double(.20),  // Lab 4
		new Double(.30),  // Lab 5
		new Double(.40),  // EC 1
		new Double(.60) };// EC 2

	private static final Boolean[] ASSIGNMENT_IS_COUNTED = { 
		true,   // HW 1
		true,   // HW 2
		true,   // HW 3
		true,   // HW 4
		true,   // HW 5 EC
		true,   // Midterm 1
		true,   // Midterm 2
		true,   // Final
		true,   // Lab 1
		true,   // Lab 2
		true,   // Lab 3
		true,   // Lab 4
		true,   // Lab 5
		true,   // EC 1
		true }; // EC 2

	private static final Boolean[] ASSIGNMENT_IS_REMOVED = { 
		false,   // HW 1
		false,   // HW 2
		false,   // HW 3
		false,   // HW 4
		false,   // HW 5 EC
		false,   // Midterm 1
		false,   // Midterm 2
		false,   // Final
		false,   // Lab 1
		false,   // Lab 2
		false,   // Lab 3
		false,   // Lab 4
		false,   // Lab 5
		false,   // EC 1
		false }; // EC 2

	private static final Boolean[] ASSIGNMENT_IS_EXTRA_CREDIT = { 
		false,   // HW 1
		false,   // HW 2
		false,   // HW 3
		false,   // HW 4
		true,    // HW 5 EC
		false,   // Midterm 1
		false,   // Midterm 2
		false,   // Final
		false,   // Lab 1
		false,   // Lab 2
		false,   // Lab 3
		false,   // Lab 4
		false,   // Lab 5
		true,    // EC 1
		true };  // EC 2


	// NOTE: For now we only use one student with ID = 1
	private static final String[] STUDENT_ID = { 
		"1" };

	// Keeping track of all the assignments
	private ArrayList<Assignment> assignments = new ArrayList<Assignment>();
	
	// Keeping track of all the categories
	private ArrayList<Category> categories = new ArrayList<Category>();
	
	// Keeping track of all the AssignmentGradeRecord
	private ArrayList<ArrayList<AssignmentGradeRecord>> assignmentGradeRecords = new ArrayList<ArrayList<AssignmentGradeRecord>>();


	public TestData() { 

		initStaticTestData();
	}

	private void initStaticTestData() {

		Gradebook gradebook = new Gradebook();
		gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY);
		
		// For each assignment ...
		for(int i = 0; i < ASSIGNMENT_NAMES.length; i++) {

			// Create new assignment
			Assignment assignment = new Assignment(null, ASSIGNMENT_NAMES[i], ASSIGNMENT_POINTS_POSSIBLE[i], null);
			assignment.setId(new Long(i));
			assignment.setAssignmentWeighting(ASSIGNMENT_WEIGHTING[i]);
			assignment.setExtraCredit(ASSIGNMENT_IS_EXTRA_CREDIT[i]);
			assignment.setCounted(ASSIGNMENT_IS_COUNTED[i]);
			assignment.setRemoved(ASSIGNMENT_IS_REMOVED[i]);
			assignment.setGradebook(gradebook);
			
			Category category = null;
			
			switch(i) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
				if(HOME_WORK_ID < categories.size()) {
					category = categories.get(HOME_WORK_ID);
				}
				
				if(null == category) {
					category = new Category();
					category.setId(new Long(HOME_WORK_ID));
					category.setName(CATEGORY_NAMES[HOME_WORK_ID]);
					category.setWeight(CATEGORY_WEIGHTIN[HOME_WORK_ID]);
					category.setExtraCredit(CATEGORY_IS_EXTRA_CREDIT[HOME_WORK_ID]);
					category.setRemoved(CATEGORY_IS_REMOVEd[HOME_WORK_ID]);
					category.setGradebook(gradebook);
					categories.add(HOME_WORK_ID, category);
				}
				assignment.setCategory(category);
				
				List<Assignment> assigns = category.getAssignmentList();
				if (assigns == null)
					assigns = new ArrayList<Assignment>();
				
				assigns.add(assignment);
				category.setAssignmentList(assigns);
				
				break;
				
			case 5:
			case 6:
			case 7:
				if(EXAMS_ID < categories.size()) {
					category = categories.get(EXAMS_ID);
				}
				
				if(null == category) {
					category = new Category();
					category.setId(new Long(EXAMS_ID));
					category.setName(CATEGORY_NAMES[EXAMS_ID]);
					category.setWeight(CATEGORY_WEIGHTIN[EXAMS_ID]);
					category.setExtraCredit(CATEGORY_IS_EXTRA_CREDIT[EXAMS_ID]);
					category.setRemoved(CATEGORY_IS_REMOVEd[EXAMS_ID]);
					category.setGradebook(gradebook);
					categories.add(EXAMS_ID, category);
				}
				assignment.setCategory(category);
				
				assigns = category.getAssignmentList();
				if (assigns == null)
					assigns = new ArrayList<Assignment>();
				
				assigns.add(assignment);
				category.setAssignmentList(assigns);
				break;
				
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
				if(LABS_ID < categories.size()) {
					category = categories.get(LABS_ID);
				}
				
				if(null == category) {
					category = new Category();
					category.setId(new Long(LABS_ID));
					category.setName(CATEGORY_NAMES[LABS_ID]);
					category.setWeight(CATEGORY_WEIGHTIN[LABS_ID]);
					category.setExtraCredit(CATEGORY_IS_EXTRA_CREDIT[LABS_ID]);
					category.setRemoved(CATEGORY_IS_REMOVEd[LABS_ID]);
					category.setGradebook(gradebook);
					categories.add(LABS_ID, category);
				}
				assignment.setCategory(category);
				
				assigns = category.getAssignmentList();
				if (assigns == null)
					assigns = new ArrayList<Assignment>();
				
				assigns.add(assignment);
				category.setAssignmentList(assigns);
				break;
				
			case 13:
			case 14:
				if(EXTRA_CREDIT_ID < categories.size()) {
					category = categories.get(EXTRA_CREDIT_ID);
				}
				
				if(null == category) {
					category = new Category();
					category.setId(new Long(EXTRA_CREDIT_ID));
					category.setName(CATEGORY_NAMES[EXTRA_CREDIT_ID]);
					category.setWeight(CATEGORY_WEIGHTIN[EXTRA_CREDIT_ID]);
					category.setExtraCredit(CATEGORY_IS_EXTRA_CREDIT[EXTRA_CREDIT_ID]);
					category.setRemoved(CATEGORY_IS_REMOVEd[EXTRA_CREDIT_ID]);
					category.setGradebook(gradebook);
					categories.add(EXTRA_CREDIT_ID, category);
				}
				assignment.setCategory(category);
				
				assigns = category.getAssignmentList();
				if (assigns == null)
					assigns = new ArrayList<Assignment>();
				
				assigns.add(assignment);
				category.setAssignmentList(assigns);
				break;
			}
			assignments.add(i, assignment);

			// For this assignment at ArrayList location assignmentID, we add an ArrayList to hold all the possible AssignmentGradeRecord
			ArrayList<AssignmentGradeRecord> agrList = new ArrayList<AssignmentGradeRecord>();
			AssignmentGradeRecord agr = new AssignmentGradeRecord(assignment, STUDENT_ID[0], POINTS_EARNED[i]);
			agrList.add(agr);
			assignmentGradeRecords.add(i, agrList);
		}
	}

	public Assignment getAssignment(Long assignmentId) {

		for(Assignment assignment : assignments) {

			if(assignment.getId().equals(assignmentId)) {

				return assignment;
			}
		}

		return null;
	}

	public AssignmentGradeRecord getAssignmentGradeRecord(Assignment assignment, String userId) {

		for(ArrayList<AssignmentGradeRecord> assignmentGradeRecordList : assignmentGradeRecords) {

			for(AssignmentGradeRecord assignmentGradeRecord : assignmentGradeRecordList) {

				if(assignmentGradeRecord.getAssignment().getId().equals(assignment.getId()) && 
						assignmentGradeRecord.getStudentId().equals(userId)) {

					return assignmentGradeRecord;
				}
			}
		}

		return null;
	}

	public int getNumberOfAssignments() {

		return ASSIGNMENT_NAMES.length;
	}
	
	public Collection<Assignment> getAssignments() {
		
		return assignments;
	}
	
	public Category getCategory(int categoryId) {
		
		return categories.get(categoryId);
	}
	
	public Collection<Category> getCategories() {
		
		return categories;
	}
	
	public Collection<AssignmentGradeRecord> getAssignmentGradeRecords(String userId) {
		 
		ArrayList<AssignmentGradeRecord> agrs = new ArrayList<AssignmentGradeRecord>();
		
		for(ArrayList<AssignmentGradeRecord> assignmentGradeRecordList : assignmentGradeRecords) {

			for(AssignmentGradeRecord assignmentGradeRecord : assignmentGradeRecordList) {

				if(assignmentGradeRecord.getStudentId().equals(userId)) {
					
					agrs.add(assignmentGradeRecord);
				}
			}
		}
		
		return agrs;
	}
}
