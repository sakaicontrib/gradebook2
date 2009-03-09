package org.sakaiproject.gradebook.gwt.test.mock;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.sakaiproject.gradebook.gwt.sakai.GradeCalculations;
import org.sakaiproject.gradebook.gwt.sakai.GradeCalculationsImpl;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.AssignmentGradeRecord;
import org.sakaiproject.tool.gradebook.Category;

/*
 * JLR: This is the original TestCase that Thomas developed, with several modifications throughout
 * reflecting ongoing development. It is still important to provide regression testing of the 
 * low-level methods, but we need a more easily configured set of test data to thoroughly test
 * each of the different permutations of grade types and category types. 
 */
public class GradeCalculationTest extends TestCase {
	
	private GradeCalculations gradeCalculationMock = null;
	private TestData testData = null;
	
	protected void setUp() {
		
		gradeCalculationMock = new GradeCalculationsImpl();
		testData = new TestData();
	}
	
	protected void tearDown() {
		
	}
	
	public void testGetPointsEarnedAsPercent() {
		
		Assignment assignment = testData.getAssignment(new Long(1));
		AssignmentGradeRecord assignmentGradeRecord = testData.getAssignmentGradeRecord(assignment, testData.TEST_STUDENT_1);
		BigDecimal pointsEarnedAsPercent = gradeCalculationMock.getPointsEarnedAsPercent(assignment, assignmentGradeRecord);
		assertNotNull(pointsEarnedAsPercent);
		assertEquals(new BigDecimal("65.00000"), pointsEarnedAsPercent);
	}
	
	public void testGetEarnedWeightedPercentage() {
		
		Assignment assignment = testData.getAssignment(new Long(1));
		AssignmentGradeRecord assignmentGradeRecord = testData.getAssignmentGradeRecord(assignment, testData.TEST_STUDENT_1);
		BigDecimal pointsEarnedAsPercent = gradeCalculationMock.getPointsEarnedAsPercent(assignment, assignmentGradeRecord);
		
		BigDecimal earnedWeightedPercentage = gradeCalculationMock.getEarnedWeightedPercentage(assignment, pointsEarnedAsPercent, Boolean.TRUE);
		assertNotNull(earnedWeightedPercentage);
		//System.out.println("Earned weighted percentage " + earnedWeightedPercentage);
		assertEquals(new BigDecimal("13.000000"), earnedWeightedPercentage);
	}
	
	public void testGetSumAssignmentsEarnedWeightedPercentage() {
		
		// Testing for Category HOME_WORK
		Collection<Assignment> assignments = testData.getAssignments();
		Collection<AssignmentGradeRecord> assignmentGradeRecords = testData.getAssignmentGradeRecords(testData.TEST_STUDENT_1);
		Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap = new HashMap<Long, AssignmentGradeRecord>();
		for (AssignmentGradeRecord agr : assignmentGradeRecords) {
			assignmentGradeRecordMap.put(agr.getAssignment().getId(), agr);
		}
		
		Category category = testData.getCategory(testData.HOME_WORK_ID);
		BigDecimal sumAssignmentsEarnedWeightedPercentage = gradeCalculationMock.sumEarnedWeightedPercentages(category, assignmentGradeRecordMap);
		
		//System.out.println("Sum assignments earned weighted percentage " + sumAssignmentsEarnedWeightedPercentage);
		assertNotNull(sumAssignmentsEarnedWeightedPercentage);
		assertEquals(new BigDecimal("66.233333334"), sumAssignmentsEarnedWeightedPercentage);
	}
	
	public void testGetSumAssignmentWeights() {
		// Testing for Category HOME_WORK
		Collection<Assignment> assignments = testData.getAssignments();
		Collection<AssignmentGradeRecord> assignmentGradeRecords = testData.getAssignmentGradeRecords(testData.TEST_STUDENT_1);
		Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap = new HashMap<Long, AssignmentGradeRecord>();
		for (AssignmentGradeRecord agr : assignmentGradeRecords) {
			assignmentGradeRecordMap.put(agr.getAssignment().getId(), agr);
		}
		Category category = testData.getCategory(testData.HOME_WORK_ID);
		BigDecimal sumAssignmentWeights = gradeCalculationMock.sumAssignmentWeights(category, assignmentGradeRecordMap);
		//System.out.println("Sum assignment weights " + sumAssignmentWeights);
		
		assertNotNull(sumAssignmentWeights);
	}
	
	public void testGetCategoryWeight() {
		
		Category category = testData.getCategory(testData.HOME_WORK_ID);
		BigDecimal categoryWeight = gradeCalculationMock.getCategoryWeight(category);
		assertNotNull(categoryWeight);
		assertEquals(new BigDecimal("0.3"), categoryWeight);
		
		category = testData.getCategory(testData.EXTRA_CREDIT_ID);
		categoryWeight = gradeCalculationMock.getCategoryWeight(category);
		assertNotNull(categoryWeight);
		assertEquals(new BigDecimal("0.1"), categoryWeight);
	}
	
	public void testGetSumExtraCreditAssignmentEarnedWeightedPercentage() {
		
		// Testing for Category HOME_WORK
		Collection<Assignment> assignments = testData.getAssignments();
		Collection<AssignmentGradeRecord> assignmentGradeRecords = testData.getAssignmentGradeRecords(testData.TEST_STUDENT_1);
		Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap = new HashMap<Long, AssignmentGradeRecord>();
		for (AssignmentGradeRecord agr : assignmentGradeRecords) {
			assignmentGradeRecordMap.put(agr.getAssignment().getId(), agr);
		}
		Category category = testData.getCategory(testData.HOME_WORK_ID);
		BigDecimal sumExtraCreditAssignmentEarnedWeightedPercentage = gradeCalculationMock.sumExtraCreditEarnedWeightedPercentage(category, assignmentGradeRecordMap);
		assertNotNull(sumExtraCreditAssignmentEarnedWeightedPercentage);
		assertEquals(new BigDecimal("16.000000"), sumExtraCreditAssignmentEarnedWeightedPercentage);
	}
	
	public void testGetCategoryGrade() {
		
		
		Collection<Assignment> assignments = testData.getAssignments();
		Collection<AssignmentGradeRecord> assignmentGradeRecords = testData.getAssignmentGradeRecords(testData.TEST_STUDENT_1);
		Category category = null;
		BigDecimal categoryGrade = null;
		Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap = new HashMap<Long, AssignmentGradeRecord>();
		for (AssignmentGradeRecord agr : assignmentGradeRecords) {
			assignmentGradeRecordMap.put(agr.getAssignment().getId(), agr);
		}
		
		// Testing category Home Work
		category = testData.getCategory(testData.HOME_WORK_ID);
		categoryGrade = gradeCalculationMock.getCategoryGrade(category, assignmentGradeRecordMap);
		assertNotNull(categoryGrade);
		assertEquals(new BigDecimal("98.79166667"), categoryGrade);
		
		// Testing category Exams
		category = testData.getCategory(testData.EXAMS_ID);
		categoryGrade = gradeCalculationMock.getCategoryGrade(category, assignmentGradeRecordMap);
		assertNotNull(categoryGrade);
		assertEquals(new BigDecimal("87.20000"), categoryGrade);
		
		// Testing category Labs
		category = testData.getCategory(testData.LABS_ID);
		categoryGrade = gradeCalculationMock.getCategoryGrade(category, assignmentGradeRecordMap);
		assertNotNull(categoryGrade);
		assertEquals(new BigDecimal("82.00000000"), categoryGrade);
		
		// Testing category Extra Credit
		category = testData.getCategory(testData.EXTRA_CREDIT_ID);
		categoryGrade = gradeCalculationMock.getCategoryGrade(category, assignmentGradeRecordMap);
		assertNotNull(categoryGrade);
		assertEquals(new BigDecimal("68.000000"), categoryGrade);
	}
	
	public void testGetCourseGrade() {
		
		long start = System.nanoTime();
		Collection<Assignment> assignments = testData.getAssignments();
		Collection<AssignmentGradeRecord> assignmentGradeRecords = testData.getAssignmentGradeRecords(testData.TEST_STUDENT_1);
		Collection<Category> categories= testData.getCategories();
		Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap = new HashMap<Long, AssignmentGradeRecord>();
		for (AssignmentGradeRecord agr : assignmentGradeRecords) {
			assignmentGradeRecordMap.put(agr.getAssignment().getId(), agr);
		}
		
		BigDecimal courseGrade = gradeCalculationMock.getCourseGrade(categories, assignmentGradeRecordMap);
		long end = System.nanoTime();
		System.out.println("Elapsed: " + (end-start));
		
		//System.out.println("Course Grade: " + courseGrade.toString());
		assertNotNull(courseGrade);
		assertEquals(new BigDecimal("96.437500001"), courseGrade);
	}
	
	public void testGetCourseGradeUngradedCategory() {
		
		Collection<Assignment> assignments = testData.getAssignments();
		Collection<AssignmentGradeRecord> assignmentGradeRecords = testData.getAssignmentGradeRecords(testData.TEST_STUDENT_1);
		Collection<Category> categories= testData.getCategories();
		Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap = new HashMap<Long, AssignmentGradeRecord>();
		Map<Long, Assignment> assignmentMap = new HashMap<Long, Assignment>();
		
		for (Assignment assignment : assignments) {
			assignmentMap.put(assignment.getId(), assignment);
		}
		
		for (AssignmentGradeRecord agr : assignmentGradeRecords) {
			Assignment a = assignmentMap.get(agr.getAssignment().getId());
			Category c = a.getCategory();
			
			// If we hide every grade except the ones under the "HOME WORK" category
			if (c.getName().equals("HOME WORK"))
				assignmentGradeRecordMap.put(agr.getAssignment().getId(), agr);
		}
		
		BigDecimal courseGrade = gradeCalculationMock.getCourseGrade(categories, assignmentGradeRecordMap);
		//System.out.println("Course Grade: " + courseGrade.toString());
		assertNotNull(courseGrade);
		// FIXME: Not verified mathematically outside of this logic
		// Note : this is the same value (at a different scale) that is returned by getCategoryGrade for "HOME WORK" category
		assertEquals(new BigDecimal("98.791666670"), courseGrade);
	}
	
	public void testGetCourseGradeDeletedCategory() {
		
		Collection<Assignment> assignments = testData.getAssignments();
		Collection<AssignmentGradeRecord> assignmentGradeRecords = testData.getAssignmentGradeRecords(testData.TEST_STUDENT_1);
		Collection<Category> categories= testData.getCategories();
		Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap = new HashMap<Long, AssignmentGradeRecord>();
		Map<Long, Assignment> assignmentMap = new HashMap<Long, Assignment>();
		
		for (Category category : categories) {
			if (category.getName().equals("HOME WORK"))
				category.setRemoved(true);
		}
		
		for (Assignment assignment : assignments) {
			assignmentMap.put(assignment.getId(), assignment);
		}
		
		for (AssignmentGradeRecord agr : assignmentGradeRecords) {
			Assignment a = assignmentMap.get(agr.getAssignment().getId());
			Category c = a.getCategory();
			
			assignmentGradeRecordMap.put(agr.getAssignment().getId(), agr);
		}
		
		BigDecimal courseGrade = gradeCalculationMock.getCourseGrade(categories, assignmentGradeRecordMap);
		//System.out.println("Course Grade: " + courseGrade.toString());
		assertNotNull(courseGrade);
		// FIXME: Not verified mathematically outside of this logic
		// Note : this is the same value (at a different scale) that is returned by getCategoryGrade for "HOME WORK" category
		//assertEquals(new BigDecimal("98.791666670"), courseGrade);
		
		//System.out.println("----> Course grade is " + courseGrade);
	}
	
	public void testGetCourseGradeDeletedAssignment() {
		
		Collection<Assignment> assignments = testData.getAssignments();
		Collection<AssignmentGradeRecord> assignmentGradeRecords = testData.getAssignmentGradeRecords(testData.TEST_STUDENT_1);
		Collection<Category> categories= testData.getCategories();
		Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap = new HashMap<Long, AssignmentGradeRecord>();
		Map<Long, Assignment> assignmentMap = new HashMap<Long, Assignment>();
		
		for (Assignment assignment : assignments) {
			if (assignment.getName().equals("HW 1"))
				assignment.setRemoved(true);
			
			assignmentMap.put(assignment.getId(), assignment);
		}
		
		for (AssignmentGradeRecord agr : assignmentGradeRecords) {
			assignmentGradeRecordMap.put(agr.getAssignment().getId(), agr);
		}
		
		BigDecimal courseGrade = gradeCalculationMock.getCourseGrade(categories, assignmentGradeRecordMap);
		//System.out.println("Course Grade: " + courseGrade.toString());
		assertNotNull(courseGrade);
		// FIXME: Not verified mathematically outside of this logic
		//assertEquals(new BigDecimal("96.926388889"), courseGrade);
	}
	
	public void testGetCourseGradeExcusedAssignment() {
		
		Collection<Assignment> assignments = testData.getAssignments();
		Collection<AssignmentGradeRecord> assignmentGradeRecords = testData.getAssignmentGradeRecords(testData.TEST_STUDENT_1);
		Collection<Category> categories= testData.getCategories();
		Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap = new HashMap<Long, AssignmentGradeRecord>();
		Map<Long, Assignment> assignmentMap = new HashMap<Long, Assignment>();
		
		for (Assignment assignment : assignments) {
			assignmentMap.put(assignment.getId(), assignment);
		}
		
		for (AssignmentGradeRecord agr : assignmentGradeRecords) {
			Assignment a = assignmentMap.get(agr.getAssignment().getId());
			Category c = a.getCategory();
			
			// Excuse the student from the assignment "Lab 1"
			if (a.getName().equals("Lab 1"))
				agr.setExcluded(Boolean.TRUE);
				
			assignmentGradeRecordMap.put(agr.getAssignment().getId(), agr);
		}
		
		BigDecimal courseGrade = gradeCalculationMock.getCourseGrade(categories, assignmentGradeRecordMap);
		//System.out.println("Course Grade: " + courseGrade.toString());
		assertNotNull(courseGrade);
		// FIXME: Not verified mathematically outside of this logic
		assertEquals(new BigDecimal("96.926388889"), courseGrade);
	}
	
	
	public void testGetCourseGradeDropLowest() {
		
		Collection<Assignment> assignments = testData.getAssignments();
		Collection<AssignmentGradeRecord> assignmentGradeRecords = testData.getAssignmentGradeRecords(testData.TEST_STUDENT_1);
		Collection<Category> categories= testData.getCategories();
		Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap = new HashMap<Long, AssignmentGradeRecord>();
		for (AssignmentGradeRecord agr : assignmentGradeRecords) {
			assignmentGradeRecordMap.put(agr.getAssignment().getId(), agr);
		}
		
		for (Category c : categories) {
			if (c.getName().equals("HOME WORK"))
				c.setDrop_lowest(3);
		}
		
		BigDecimal courseGrade = gradeCalculationMock.getCourseGrade(categories, assignmentGradeRecordMap);
		//System.out.println("Course Grade: " + courseGrade.toString());
		assertNotNull(courseGrade);
		// FIXME: Not verified mathematically outside of this logic
		//assertEquals(new BigDecimal("96.926388889"), courseGrade);
	}
	
	
}
