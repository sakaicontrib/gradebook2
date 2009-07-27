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

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.sakaiproject.gradebook.gwt.sakai.GradebookToolService;
import org.sakaiproject.gradebook.gwt.sakai.model.ActionRecord;
import org.sakaiproject.gradebook.gwt.sakai.model.UserConfiguration;
import org.sakaiproject.gradebook.gwt.sakai.model.UserDereference;
import org.sakaiproject.gradebook.gwt.sakai.model.UserDereferenceRealmUpdate;
import org.sakaiproject.service.gradebook.shared.StaleObjectModificationException;
import org.sakaiproject.tool.gradebook.AbstractGradeRecord;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.AssignmentGradeRecord;
import org.sakaiproject.tool.gradebook.Category;
import org.sakaiproject.tool.gradebook.Comment;
import org.sakaiproject.tool.gradebook.CourseGrade;
import org.sakaiproject.tool.gradebook.CourseGradeRecord;
import org.sakaiproject.tool.gradebook.GradableObject;
import org.sakaiproject.tool.gradebook.GradeMapping;
import org.sakaiproject.tool.gradebook.Gradebook;
import org.sakaiproject.tool.gradebook.GradingEvent;
import org.sakaiproject.tool.gradebook.GradingEvents;
import org.sakaiproject.tool.gradebook.GradingScale;
import org.sakaiproject.tool.gradebook.LetterGradePercentMapping;
import org.sakaiproject.tool.gradebook.Permission;
import org.sakaiproject.tool.gradebook.Spreadsheet;
import org.sakaiproject.user.api.User;

public class GradebookToolServiceMock implements GradebookToolService {

	public static final MathContext MATH_CONTEXT = new MathContext(10, RoundingMode.HALF_DOWN);
	
	public static class GradebookService {
		public static final int GRADE_TYPE_POINTS = 1;
		public static final int GRADE_TYPE_PERCENTAGE = 2;
		public static final int GRADE_TYPE_LETTER = 3;
	}
	
	public static final int CATEGORY_TYPE_NO_CATEGORY = 1;
	public static final int CATEGORY_TYPE_ONLY_CATEGORY = 2;
	public static final int CATEGORY_TYPE_WEIGHTED_CATEGORY = 3;
	
	//public class GradebookData {
	
		private Map<Long, Assignment> assignmentMap;
		private Gradebook gradebook;
		private Map<String, Map<Assignment, AssignmentGradeRecord>> studentGradeMap;
		private List<Assignment> assignments;
		private List<Long> categoryIds;
		private Map<Long, Category> categoryMap;
		private List<CourseGradeRecord> courseGradeRecords;
		private Map<String, CourseGradeRecord> studentCourseGradeRecords;
		private Map<String, Map<GradableObject, List<GradingEvent>>> studentGoEventListMap;
		private List<ActionRecord> actionRecords;
		private Map<String, Map<Long, Comment>> studentCommentMap;
		private List<Permission> permissions;
	
		
	//}
	
	private long actionRecordId = 0;
	private long categoryCount = 0;
	private long gradeEventId = 0;
	private long permissionId = 0;
	
	
	public GradebookToolServiceMock() {
		this.gradebook = new Gradebook();
		gradebook.setName("My Default Gradebook");
		gradebook.setId(Long.valueOf(1));
		//gradebook.setCategory_type(CATEGORY_TYPE_ONLY_CATEGORY);
		//gradebook.setGrade_type(GradebookService.GRADE_TYPE_PERCENTAGE);
		//gradebook.setEqualWeightCategories(Boolean.TRUE);
		
		GradingScale gradingScale = new GradingScale();
		gradingScale.setDefaultBottomPercents(getDefaultBottomPercents());
		gradingScale.setGrades(getGrades());
		GradeMapping gradeMapping = new GradeMapping(gradingScale);
		this.gradebook.setSelectedGradeMapping(gradeMapping);
		this.studentGradeMap = new HashMap<String, Map<Assignment, AssignmentGradeRecord>>();
		this.studentCourseGradeRecords = new HashMap<String, CourseGradeRecord>();
		
		this.categoryIds = new LinkedList<Long>();
		this.categoryMap = new HashMap<Long, Category>();
		
		this.studentGoEventListMap = new HashMap<String, Map<GradableObject, List<GradingEvent>>>();
		
		this.studentCommentMap = new HashMap<String, Map<Long, Comment>>();
		
		this.actionRecords = new ArrayList<ActionRecord>();
		
		assignments = new LinkedList<Assignment>();
		assignmentMap = new HashMap<Long, Assignment>();
		
		permissions = new LinkedList<Permission>();
		
		/*try {
			Long essaysId = createCategory(Long.valueOf(categoryCount++), "Essays", Double.valueOf(0.60), Integer.valueOf(1));
			Long hwId = createCategory(Long.valueOf(categoryCount++), "Homework", Double.valueOf(0.40), Integer.valueOf(0));
			
			Category essays = getCategory(essaysId);
			Category hw = getCategory(hwId);
			
			Assignment essay1 = constructAssignment(essays, "Essay 1", Double.valueOf(20.0), Double.valueOf(0.25));
			Assignment essay2 = constructAssignment(essays, "Essay 2", Double.valueOf(20.0), Double.valueOf(0.25));
			Assignment essay3 = constructAssignment(essays, "Essay 3", Double.valueOf(20.0), Double.valueOf(0.25));
			Assignment essay4 = constructAssignment(essays, "Essay 4", Double.valueOf(20.0), Double.valueOf(0.25));
			
			Assignment hw1 = constructAssignment(hw, "Homework 1", Double.valueOf(10.0), Double.valueOf(0.20));
			Assignment hw2 = constructAssignment(hw, "Homework 2", Double.valueOf(10.0), Double.valueOf(0.20));
			Assignment hw3 = constructAssignment(hw, "Homework 3", Double.valueOf(10.0), Double.valueOf(0.20));
			Assignment hw4 = constructAssignment(hw, "Homework 4", Double.valueOf(10.0), Double.valueOf(0.20));
			Assignment hw5 = constructAssignment(hw, "Homework 5", Double.valueOf(10.0), Double.valueOf(0.10));
			Assignment hw6 = constructAssignment(hw, "Homework 6", Double.valueOf(10.0), Double.valueOf(0.10));
			
			assignments.add(essay1);
			assignments.add(essay2);
			assignments.add(essay3);
			assignments.add(essay4);
			
			assignments.add(hw1);
			assignments.add(hw2);
			assignments.add(hw3);
			assignments.add(hw4);
			assignments.add(hw5);
			assignments.add(hw6);
			
			assignmentMap.put(essay1.getId(), essay1);
			assignmentMap.put(essay2.getId(), essay2);
			assignmentMap.put(essay3.getId(), essay3);
			assignmentMap.put(essay4.getId(), essay4);
			
			assignmentMap.put(hw1.getId(), hw1);
			assignmentMap.put(hw2.getId(), hw2);
			assignmentMap.put(hw3.getId(), hw3);
			assignmentMap.put(hw4.getId(), hw4);
			assignmentMap.put(hw5.getId(), hw5);
			assignmentMap.put(hw6.getId(), hw6);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}*/
	}

	public void addToGradeRecordMap(Map gradeRecordMap, List gradeRecords) {
		for (Iterator iter = gradeRecords.iterator(); iter.hasNext(); ) {
			AbstractGradeRecord gradeRecord = (AbstractGradeRecord)iter.next();
			if (gradeRecord instanceof AssignmentGradeRecord) {
				((AssignmentGradeRecord)gradeRecord).setUserAbleToView(true);
			}
			String studentUid = gradeRecord.getStudentId();
			Map studentMap = (Map)gradeRecordMap.get(studentUid);
			if (studentMap == null) {
				studentMap = new HashMap();
				gradeRecordMap.put(studentUid, studentMap);
			}
			studentMap.put(gradeRecord.getGradableObject().getId(), gradeRecord);
		}
    }
    
    public void addToGradeRecordMap(Map gradeRecordMap, List gradeRecords, Map studentIdItemIdFunctionMap) {
		for (Iterator iter = gradeRecords.iterator(); iter.hasNext(); ) {
			AbstractGradeRecord gradeRecord = (AbstractGradeRecord)iter.next();
			String studentUid = gradeRecord.getStudentId();
			Map studentMap = (Map)gradeRecordMap.get(studentUid);
			if (studentMap == null) {
				studentMap = new HashMap();
				gradeRecordMap.put(studentUid, studentMap);
			}
			Long itemId = gradeRecord.getGradableObject().getId();
			// check to see if this item is included in the items that the current user is able to view/grade
			Map itemIdFunctionMap = (Map)studentIdItemIdFunctionMap.get(studentUid);
			if (gradeRecord instanceof AssignmentGradeRecord) {
				
				if (itemIdFunctionMap != null && itemIdFunctionMap.get(itemId) != null) {
					((AssignmentGradeRecord)gradeRecord).setUserAbleToView(true);
				} else {
					((AssignmentGradeRecord)gradeRecord).setUserAbleToView(false);
					((AssignmentGradeRecord)gradeRecord).setLetterEarned(null);
					((AssignmentGradeRecord)gradeRecord).setPointsEarned(null);
					((AssignmentGradeRecord)gradeRecord).setPercentEarned(null);
				}
				studentMap.put(itemId, gradeRecord);
			} else {
				studentMap.put(itemId, gradeRecord);
			}
		}
    }
    
    public List<ActionRecord> getActionRecords(final String gradebookUid, int offset, int limit) {
		
    	if ((limit+offset) > actionRecords.size())
    		limit = actionRecords.size() - offset;
    	if (limit < 0)
    		limit = 0;
    	
    	Collections.sort(actionRecords, new Comparator<ActionRecord>() {

			public int compare(ActionRecord o1, ActionRecord o2) {
				if (o1 == null || o2 == null)
					return 0;
				
				if (o1.getDateRecorded() == null || o2.getDateRecorded() == null)
					return 0;
				
				return o2.getDateRecorded().compareTo(o1.getDateRecorded());
			}
    		
    	});
    	
    	return actionRecords.subList(offset, limit);	
	}
	
	public Integer getActionRecordSize(final String gradebookUid) {
		return Integer.valueOf(actionRecords.size());
	}

	public List<ActionRecord> getActionRecords(final String gradebookUid, final String learnerUid, int offset, int limit) {
		List<ActionRecord> learnerActionRecords = new ArrayList<ActionRecord>();
		
		for (ActionRecord record : actionRecords) {
			if (record.getStudentUid() != null && record.getStudentUid().equals(learnerUid))
				learnerActionRecords.add(record);
		}
		
    	if ((limit+offset) > learnerActionRecords.size())
    		limit = learnerActionRecords.size() - offset;
    	if (limit < 0)
    		limit = 0;
    	
    	return learnerActionRecords.subList(offset, limit);	
	}
	
	public Integer getActionRecordSize(final String gradebookUid, final String learnerUid) {
		List<ActionRecord> learnerActionRecords = new ArrayList<ActionRecord>();
		
		for (ActionRecord record : actionRecords) {
			if (record.getStudentUid() != null && record.getStudentUid().equals(learnerUid))
				learnerActionRecords.add(record);
		}
		
		return Integer.valueOf(learnerActionRecords.size());
	}
	
	public Long createAssignment(Long gradebookId, String name, Double points, Date dueDate, Boolean isNotCounted, Boolean isReleased) {
		Assignment assignment = new Assignment(gradebook, name, points, dueDate, isReleased);
		assignment.setId(assignmentIdCount++);
		assignment.setNotCounted(isNotCounted);
		assignment.setUnweighted(Boolean.FALSE);
		assignment.setAssignmentWeighting(Double.valueOf(0.0));
		assignmentMap.put(assignment.getId(), assignment);
		assignments.add(assignment);
		return assignment.getId();
	}

	public Long createAssignmentForCategory(Long gradebookId, Long categoryId, String name, Double points, Double weight, Date dueDate, Boolean isUnweighted, Boolean isExtraCredit, Boolean isNotCounted, Boolean isReleased, Integer itemOrder)
			throws RuntimeException {
		Category category = getCategory(categoryId);
		Assignment assignment = new Assignment(gradebook, name, points, dueDate, isReleased);
		assignment.setId(assignmentIdCount++);
		assignment.setNotCounted(isNotCounted);
		assignment.setUnweighted(Boolean.FALSE);
		assignment.setExtraCredit(isExtraCredit);
		assignment.setReleased(isReleased);
		assignment.setPointsPossible(points);
		assignment.setAssignmentWeighting(weight);
		assignmentMap.put(assignment.getId(), assignment);
		assignments.add(assignment);
		
		List<Assignment> assignments = category.getAssignmentList();
		
		if (assignments == null) 
			assignments = new ArrayList<Assignment>();
		
		assignments.add(assignment);
		
		int count = category.getAssignmentCount() + 1;
		category.setAssignmentCount(count);
		category.setAssignmentList(assignments);
		
		assignment.setCategory(category);
		
		return assignment.getId();
	}

	public Long createCategory(Long gradebookId, String name, Double weight, Integer dropLowest, Boolean equalWeightAssignments, Boolean isUnweighted, Boolean isExtraCredit, Integer categoryOrder) throws RuntimeException {
		Long id = Long.valueOf(categoryCount++);
		int dl = dropLowest == null ? 0 : dropLowest.intValue();
		Category category = new Category();
		category.setId(id);
		category.setName(name);
		category.setWeight(weight);
		category.setUnweighted(isUnweighted);
		category.setDrop_lowest(dl);
		category.setEqualWeightAssignments(equalWeightAssignments);
		categoryMap.put(id, category);
		categoryIds.add(id);
		category.setGradebook(gradebook);
		
		return category.getId();
	}

	public List<AssignmentGradeRecord> getAllAssignmentGradeRecords(Long gradebookId, String[] sectionEid, String[] roleNames) {
		List<AssignmentGradeRecord> grades = new ArrayList<AssignmentGradeRecord>();
		List<AssignmentGradeRecord> assignmentGradeRecord = new ArrayList<AssignmentGradeRecord>();
		
		for (Long gradableObjectId : assignmentMap.keySet()) {
			Assignment assignment = assignmentMap.get(gradableObjectId);
			
			if (assignment != null) {
				for (String studentUid : studentGradeMap.keySet()) {
					Map<Assignment, AssignmentGradeRecord> assignmentGradeMap = studentGradeMap.get(studentUid);
					AssignmentGradeRecord gradeRecord = null;
						
					Double points = assignment.getPointsPossible();
						
					if (assignmentGradeMap == null) {
						/*assignmentGradeMap = new HashMap<Assignment, AssignmentGradeRecord>();
						gradeRecord = new AssignmentGradeRecord(assignment, studentUid, generateRandomGrade(points));
						assignmentGradeMap.put(assignment, gradeRecord);
						studentGradeMap.put(studentUid, assignmentGradeMap);*/
					} else {
						gradeRecord = assignmentGradeMap.get(assignment);
							
						/*if (gradeRecord == null) {
							gradeRecord = new AssignmentGradeRecord(assignment, studentUid, generateRandomGrade(points));
							assignmentGradeMap.put(assignment, gradeRecord);
						} */
					}
					if (gradeRecord != null)
						grades.add(gradeRecord);
				}
			}
		}
		return grades;
	}
	
	public List<AssignmentGradeRecord> getAllAssignmentGradeRecords(Long gradebookId, Collection<String> studentUids) {
		List<AssignmentGradeRecord> grades = new ArrayList<AssignmentGradeRecord>(studentUids.size());
		List<AssignmentGradeRecord> assignmentGradeRecord = new ArrayList<AssignmentGradeRecord>(studentUids.size());
		
		for (Long gradableObjectId : assignmentMap.keySet()) {
			Assignment assignment = assignmentMap.get(gradableObjectId);
			
			if (assignment != null) {
				for (String studentId : (Collection<String>)studentUids) {
					Map<Assignment, AssignmentGradeRecord> assignmentGradeMap = studentGradeMap.get(studentId);
					
					AssignmentGradeRecord gradeRecord = null;
					
					Double points = assignment.getPointsPossible();
					
					if (assignmentGradeMap == null) {
						/*assignmentGradeMap = new HashMap<Assignment, AssignmentGradeRecord>();
						gradeRecord = new AssignmentGradeRecord(assignment, studentId, generateRandomGrade(points));
						assignmentGradeMap.put(assignment, gradeRecord);
						studentGradeMap.put(studentId, assignmentGradeMap);*/
					} else {
						gradeRecord = assignmentGradeMap.get(assignment);
						
						/*if (gradeRecord == null) {
							gradeRecord = new AssignmentGradeRecord(assignment, studentId, generateRandomGrade(points));
							assignmentGradeMap.put(assignment, gradeRecord);
						} */
					}
					
					if (gradeRecord != null)
						grades.add(gradeRecord);
					
				}
			}
		}
		return grades;
	}

	public List getAllAssignmentGradeRecordsConverted(Long gradebookId, Collection studentUids) {
		// FIXME: Isn't doing the converting part
		return getAllAssignmentGradeRecords(gradebookId, studentUids);
	}

	public Assignment getAssignment(Long assignmentId) {
		Assignment assignment = assignmentMap.get(assignmentId);
		
		return assignment;
	}

	public AssignmentGradeRecord getAssignmentGradeRecordForAssignmentForStudent(Assignment assignment, String studentId) {

			if (assignment != null) {
				
					Map<Assignment, AssignmentGradeRecord> assignmentGradeMap = studentGradeMap.get(studentId);
					
					AssignmentGradeRecord gradeRecord = null;
					
					Double points = assignment.getPointsPossible();
					
					if (assignmentGradeMap == null) {
						/*assignmentGradeMap = new HashMap<Assignment, AssignmentGradeRecord>();
						gradeRecord = new AssignmentGradeRecord(assignment, studentId, generateRandomGrade(points));
						logAssignmentGradingEvent(gradeRecord, "Nobody", assignment);
						assignmentGradeMap.put(assignment, gradeRecord);
						studentGradeMap.put(studentId, assignmentGradeMap);*/
					} else {
						gradeRecord = assignmentGradeMap.get(assignment);
						
						/*if (gradeRecord == null) {
							gradeRecord = new AssignmentGradeRecord(assignment, studentId, generateRandomGrade(points));
							assignmentGradeMap.put(assignment, gradeRecord);
							logAssignmentGradingEvent(gradeRecord, "Nobody", assignment);
						} */
					}
					
					
					
					return gradeRecord;
					
			}
			return null;
	}

	public List getAssignmentGradeRecords(Assignment assignment) {
		List<AssignmentGradeRecord> records = new ArrayList<AssignmentGradeRecord>();
		for (String student : studentGradeMap.keySet()) {
			records.add(getAssignmentGradeRecordForAssignmentForStudent(assignment, student));
		}
		return records;
	}	
	
	public List getAssignmentGradeRecords(Assignment assignment, Collection studentUids) {
		List<AssignmentGradeRecord> records = new ArrayList<AssignmentGradeRecord>();
		for (String student : (Collection<String>)studentUids) {
			records.add(getAssignmentGradeRecordForAssignmentForStudent(assignment, student));
		}
		return records;
	}

	public List getAssignments(Long gradebookId) {
		return assignments;
	}

	public List getAssignmentsForCategory(Long categoryId) throws RuntimeException {
		Category category = getCategory(categoryId);
		
		return category.getAssignmentList();
	}

	public List getAssignmentsWithNoCategory(Long gradebookId) {
		
		return null;
	}
	
	public List<Category> getCategories(Long gradebookId) throws RuntimeException {
		List<Category> categories = new LinkedList<Category>();
		
		for (Long id : categoryIds) {
			categories.add(getCategory(id));	
		}
		
		return categories;
	}
	
	public List getCategoriesWithAssignments(Long gradebookId)  {
		List<Category> categories = new LinkedList<Category>();
		
		for (Long id : categoryIds) {
			categories.add(getCategory(id));
		}
		
		return categories;
	}

	public List getCategoriesWithStats(Long gradebookId, String assignmentSort, boolean assignAscending, String categorySort, boolean categoryAscending) {
		// TODO Auto-generated method stub
		return null;
	}

	public Category getCategory(Long categoryId) throws RuntimeException {
		return categoryMap.get(categoryId);
	}

	public Comment getCommentById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getComments(Assignment assignment, Collection studentIds) {
		// TODO Auto-generated method stub
		return null;
	}

	public CourseGrade getCourseGrade(Long gradebookId) {
		CourseGrade courseGrade = new CourseGrade();
		courseGrade.setGradebook(gradebook);
		return courseGrade;
	}

	public LetterGradePercentMapping getDefaultLetterGradePercentMapping() {
		// TODO Auto-generated method stub
		return null;
	}

	public Gradebook getGradebook(Long id) {
		return gradebook;
	}

	public Gradebook getGradebook(String uid) throws RuntimeException {
		gradebook.setUid(uid);
		return gradebook;
	}

	public String getGradebookUid(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	public Gradebook getGradebookWithGradeMappings(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	public GradingEvents getGradingEvents(GradableObject gradableObject, Collection studentUids) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map getGradingEventsForStudent(String studentId, Collection gradableObjects) {
		
		Map<GradableObject, List<GradingEvent>> map = studentGoEventListMap.get(studentId);

		if (map == null) {
			map = new HashMap<GradableObject, List<GradingEvent>>();
			/*for (GradableObject go : (List<GradableObject>)gradableObjects) {
				List<GradingEvent> events = new LinkedList<GradingEvent>();
				GradingEvent event1 = new GradingEvent();
				event1.setGraderId("profno");
				event1.setGrade("F-");
				event1.setDateGraded(new Date());
				events.add(event1);
				map.put(go, events);
			}*/
		}
		
		return map;
	}
	
	private void logAssignmentGradingEvent(AssignmentGradeRecord gradeRecord, String graderId, Assignment assignment) {
		if (gradeRecord == null || assignment == null) {
			throw new IllegalArgumentException("null gradeRecord or assignment passed to logAssignmentGradingEvent");
		}
		
		// Log the grading event, and keep track of the students with saved/updated grades
		// we need to log what the user entered depending on the grade entry type
		//Gradebook gradebook = assignment.getGradebook();
		String gradeEntry = null;
		if (gradebook.getGrade_type() == GradebookService.GRADE_TYPE_LETTER) {
			gradeEntry = gradeRecord.getLetterEarned();
		} else if (gradebook.getGrade_type() == GradebookService.GRADE_TYPE_PERCENTAGE) {
			if (gradeRecord.getPercentEarned() != null)
				gradeEntry = gradeRecord.getPercentEarned().toString();
		} else {
			if (gradeRecord.getPointsEarned() != null)
				gradeEntry = gradeRecord.getPointsEarned().toString();
		}
		GradingEvent event = new GradingEvent(assignment, graderId, gradeRecord.getStudentId(), gradeEntry);
		event.setId(gradeEventId++);
		
		Map<GradableObject, List<GradingEvent>> map = studentGoEventListMap.get(gradeRecord.getStudentId());
		
		if (map == null) {
			map = new HashMap<GradableObject, List<GradingEvent>>();
		}
		
		List<GradingEvent> events = map.get(assignment);
		if (events == null) {
			events = new LinkedList<GradingEvent>();
			map.put(assignment, events);
		}
		
		events.add(event);
		
		
		studentGoEventListMap.put(gradeRecord.getStudentId(), map);
	}
	

	public LetterGradePercentMapping getLetterGradePercentMapping(Gradebook gradebook) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getPermissionsForGB(Long gradebookId) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public List getPermissionsForGBForCategoryIds(Long gradebookId, List cateIds) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public List getPermissionsForUser(Long gradebookId, String userId) throws IllegalArgumentException {
		
		List<Permission> permissionList = new LinkedList<Permission>();
		
		for(Permission permission : permissions) {
			if(permission.getUserId().equals(userId) && permission.getGradebookId().equals(gradebookId)) {
				permissionList.add(permission);
			}
		}

		return permissionList;
	}

	public List getPermissionsForUserAnyCategory(Long gradebookId, String userId) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public List getPermissionsForUserAnyGroup(Long gradebookId, String userId) throws IllegalArgumentException {
		List<Permission> permissions = new LinkedList<Permission>();
		
		return permissions;
	}

	public List getPermissionsForUserAnyGroupAnyCategory(Long gradebookId, String userId) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public List getPermissionsForUserAnyGroupForCategory(Long gradebookId, String userId, List cateIds) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public List getPermissionsForUserForCategory(Long gradebookId, String userId, List cateIds) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public List getPermissionsForUserForGoupsAnyCategory(Long gradebookId, String userId, List groupIds) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public List getPermissionsForUserForGroup(Long gradebookId, String userId, List groupIds) throws IllegalArgumentException {
		List<Permission> permissions = new LinkedList<Permission>();
		
		return permissions;
	}

	public List getPointsEarnedCourseGradeRecords(CourseGrade courseGrade, Collection studentUids) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CourseGradeRecord> getPointsEarnedCourseGradeRecords(final CourseGrade courseGrade, final Collection studentUids, final Collection assignments, final Map gradeRecordMap) {
		
    			if(studentUids == null || studentUids.size() == 0) {
    				return new ArrayList();
    			}

    			if (courseGradeRecords == null)
    				courseGradeRecords = new LinkedList<CourseGradeRecord>();

    			List records = filterAndPopulateCourseGradeRecordsByStudents(courseGrade, courseGradeRecords, studentUids);

    			Gradebook gradebook = getGradebook(courseGrade.getGradebook().getId());
    			List categories = getCategories(courseGrade.getGradebook().getId());
    			
     			Set assignmentsNotCounted = new HashSet();
    			double totalPointsPossible = 0;
					Map cateTotalScoreMap = new HashMap();

					for (Iterator iter = assignments.iterator(); iter.hasNext(); ) {
						Assignment assignment = (Assignment)iter.next();
						if (!assignment.isCounted() || assignment.getPointsPossible().doubleValue() <= 0.0) {
   						assignmentsNotCounted.add(assignment.getId());
   					}
    			}

    			for(Iterator iter = records.iterator(); iter.hasNext();) {
    				CourseGradeRecord cgr = (CourseGradeRecord)iter.next();
    				double totalPointsEarned = 0;
    				double literalTotalPointsEarned = 0;
  					Map cateScoreMap = new HashMap();
    				Map studentMap = (Map)gradeRecordMap.get(cgr.getStudentId());
    				Set assignmentsTaken = new HashSet();
    				if (studentMap != null) {
    					Collection studentGradeRecords = studentMap.values();
    					for (Iterator gradeRecordIter = studentGradeRecords.iterator(); gradeRecordIter.hasNext(); ) {
    						AssignmentGradeRecord agr = (AssignmentGradeRecord)gradeRecordIter.next();
    						if (!assignmentsNotCounted.contains(agr.getGradableObject().getId())) {
    							Double pointsEarned = agr.getPointsEarned();
    							if (pointsEarned != null) {
    								if(gradebook.getCategory_type() == CATEGORY_TYPE_NO_CATEGORY)
    								{
    									totalPointsEarned += pointsEarned.doubleValue();
    						    	literalTotalPointsEarned += pointsEarned.doubleValue();
              				assignmentsTaken.add(agr.getAssignment().getId());
    								}
    								else if(gradebook.getCategory_type() == CATEGORY_TYPE_ONLY_CATEGORY && categories != null)
    	     					{
    									totalPointsEarned += pointsEarned.doubleValue();
    									literalTotalPointsEarned += pointsEarned.doubleValue();
    									assignmentsTaken.add(agr.getAssignment().getId());
    	     					}
    			    			else if(gradebook.getCategory_type() == CATEGORY_TYPE_WEIGHTED_CATEGORY && categories != null)
    			    			{
    			    				for(int i=0; i<categories.size(); i++)
    			    				{
    			    					Category cate = (Category) categories.get(i);
    			    					if(cate != null && !cate.isRemoved() && agr.getAssignment().getCategory() != null && cate.getId().equals(agr.getAssignment().getCategory().getId()))
    			    					{
    		          				assignmentsTaken.add(agr.getAssignment().getId());
    		          				literalTotalPointsEarned += pointsEarned.doubleValue();
    			    						if(cateScoreMap.get(cate.getId()) != null)
    			    						{
    			    							cateScoreMap.put(cate.getId(), new Double(((Double)cateScoreMap.get(cate.getId())).doubleValue() + pointsEarned.doubleValue()));
    			    						}
    			    						else
    			    						{
    			    							cateScoreMap.put(cate.getId(), new Double(pointsEarned));
    			    						}
    			    						break;
    			    					}
    			    				}
    			    			}
    							}
    						}
    					}

    					cateTotalScoreMap.clear();
    		    	if(gradebook.getCategory_type() == CATEGORY_TYPE_WEIGHTED_CATEGORY && categories != null)
    		    	{
    		    		Iterator assignIter = assignments.iterator();
    		    		while (assignIter.hasNext()) 
    		    		{
    		    			Assignment asgn = (Assignment)assignIter.next();
    		    			if(assignmentsTaken.contains(asgn.getId()))
    		    			{
    		    				for(int i=0; i<categories.size(); i++)
    		    				{
    		    					Category cate = (Category) categories.get(i);
    		    					if(cate != null && !cate.isRemoved() && asgn.getCategory() != null && cate.getId().equals(asgn.getCategory().getId()))
    		    					{
    		    						if(cateTotalScoreMap.get(cate.getId()) == null)
    		    						{
    		    							cateTotalScoreMap.put(cate.getId(), asgn.getPointsPossible());
    		    						}
    		    						else
    		    						{
    		    							cateTotalScoreMap.put(cate.getId(), new Double(((Double)cateTotalScoreMap.get(cate.getId())).doubleValue() + asgn.getPointsPossible().doubleValue()));
    		    						}
    		    					}
    		    				}
    		    			}
    		    		}
    		    	}
    					
    		    	if(gradebook.getCategory_type() == CATEGORY_TYPE_WEIGHTED_CATEGORY)
    		    	{
    		    		for(int i=0; i<categories.size(); i++)
    		    		{
    		    			Category cate = (Category) categories.get(i);
    		    			if(cate != null && !cate.isRemoved() && cateScoreMap.get(cate.getId()) != null && cateTotalScoreMap.get(cate.getId()) != null)
    		    			{
    		    				totalPointsEarned += ((Double)cateScoreMap.get(cate.getId())).doubleValue() * cate.getWeight().doubleValue() / ((Double)cateTotalScoreMap.get(cate.getId())).doubleValue();
    		    			}
    		    		}
    		    	}
    				}

    				totalPointsPossible = 0;
    				if(!assignmentsTaken.isEmpty())
    	    	{
    	    		if(gradebook.getCategory_type() == CATEGORY_TYPE_WEIGHTED_CATEGORY)
    	    		{
    		    		for(int i=0; i<categories.size(); i++)
    		    		{
    		    			Category cate = (Category) categories.get(i);
    		    			if(cate != null && !cate.isRemoved() && cateScoreMap.get(cate.getId()) != null && cateTotalScoreMap.get(cate.getId()) != null)
    		    			{
    		    				totalPointsPossible += cate.getWeight().doubleValue();
    		    			}
    		    		}
    	    		}
    	    		Iterator assignIter = assignments.iterator();
  		    		while (assignIter.hasNext()) 
  		    		{
    						Assignment assignment = (Assignment)assignIter.next();
    						if(assignment != null)
    						{
    							Double pointsPossible = assignment.getPointsPossible();
    							if(gradebook.getCategory_type() == CATEGORY_TYPE_NO_CATEGORY && assignmentsTaken.contains(assignment.getId()))
    							{
    	    					totalPointsPossible += pointsPossible.doubleValue();
    							}
    							else if(gradebook.getCategory_type() == CATEGORY_TYPE_ONLY_CATEGORY && assignmentsTaken.contains(assignment.getId()))
    							{
    								totalPointsPossible += pointsPossible.doubleValue();
    							}
    						}
    					}
    	    	}
    				cgr.initNonpersistentFields(totalPointsPossible, totalPointsEarned, literalTotalPointsEarned);
    			}

    			return records;
	}

	public List<CourseGradeRecord> getPointsEarnedCourseGradeRecordsWithStats(CourseGrade courseGrade, Collection studentUids) {
		// TODO Auto-generated method stub
		return null;
	}

	public Spreadsheet getSpreadsheet(Long spreadsheetId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getSpreadsheets(Long gradebookId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getStudentAssignmentComments(String studentId, Long gradebookId) {

		List<Comment> comments = new ArrayList<Comment>();
		
		Map<Long, Comment> commentMap = studentCommentMap.get(studentId);
		
		if (commentMap != null) {
			
			for (Comment comment : commentMap.values()) {
				comments.add(comment);
			}
			
		}
		
		return comments;
		
		/*Set<Comment> commentSet = studentAssignmentComments.get(studentId);
		List<Comment> comments = null;
		
		if (commentSet != null)
			comments = new ArrayList<Comment>(commentSet);
		
		if (comments == null) 
			comments = new LinkedList<Comment>();
		
		return comments;*/
	}

	public CourseGradeRecord getStudentCourseGradeRecord(Gradebook gradebook, String studentId) {
		try {
			CourseGradeRecord courseGradeRecord = studentCourseGradeRecords.get(studentId);
			
			if (courseGradeRecord == null) {
				courseGradeRecord = new CourseGradeRecord(getCourseGrade(gradebook.getId()), studentId);
				studentCourseGradeRecords.put(studentId, courseGradeRecord);
			}
			
			// IGNORE COMMENT - JLR - We need to autocalculate each time
	        // Only take the hit of autocalculating the course grade if no explicit
	        // grade has been entered.
	       /* if (courseGradeRecord.getEnteredGrade() == null) {
	            // TODO We could easily get everything we need in a single query by using an outer join if we
	            // weren't mapping the different classes together into single sparsely populated
	            // tables. When we finally break up the current mungings of Assignment with CourseGrade
	            // and AssignmentGradeRecord with CourseGradeRecord, redo this section.
	        	List cates = getCategories(gradebook.getId());
	        	//double totalPointsPossible = getTotalPointsInternal(gradebook.getId(), session);
	        	//double totalPointsEarned = getTotalPointsEarnedInternal(gradebook.getId(), studentId, session);
	        	double totalPointsPossible = getTotalPointsInternal(gradebook.getId(), gradebook, cates, studentId);
	        	List totalEarned = getTotalPointsEarnedInternal(gradebook.getId(), studentId, gradebook, cates);
	        	double totalPointsEarned = ((Double)totalEarned.get(0)).doubleValue();
	        	double literalTotalPointsEarned = ((Double)totalEarned.get(1)).doubleValue();
	        	courseGradeRecord.initNonpersistentFields(totalPointsPossible, totalPointsEarned, literalTotalPointsEarned);
	        }*/
	        return courseGradeRecord;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List getStudentGradeRecords(Long gradebookId, String studentId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getStudentGradeRecordsConverted(Long gradebookId, String studentId) {
		// TODO Auto-generated method stub
		return null;
	}

	public double getTotalPoints(Long gradebookId) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isEnteredAssignmentScores(Long assignmentId) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isExplicitlyEnteredCourseGradeRecords(Long gradebookId) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeAssignment(Long assignmentId) {
		// TODO Auto-generated method stub

	}

	public void removeCategory(Long categoryId)  {
		// TODO Auto-generated method stub

	}

	public void removeSpreadsheet(Long spreadsheetid)  {
		// TODO Auto-generated method stub

	}

	public void saveOrUpdateLetterGradePercentMapping(Map gradeMap, Gradebook gradebook) {
		// TODO Auto-generated method stub

	}

	public Long storeActionRecord(ActionRecord actionRecord) {
		
		actionRecordId++;
		actionRecord.setId(Long.valueOf(actionRecordId));
		actionRecord.setDateRecorded(new Date());
		actionRecord.setGraderId("Test Grader");
		actionRecords.add(actionRecord);
		
		return Long.valueOf(actionRecordId);
	}
	
	public void updateAssignment(Assignment assignment) {
		assignmentMap.put(assignment.getId(), assignment);
		Category c = assignment.getCategory();
		if (c != null) {
			List<Assignment> oldList = c.getAssignmentList();
			List<Assignment> newList = new LinkedList<Assignment>();
			if (oldList != null) {
				for (Assignment a : oldList) {
					Assignment modified = getAssignment(a.getId());
					newList.add(modified);
				}
			}
			if (!newList.contains(assignment))
				newList.add(assignment);
			c.setAssignmentList(newList);
		}
		
		// Look through all the other categories and make sure that this assignment does not belong to them
		for (Category oc : getCategories(c.getGradebook().getId())) {
			// Skip the curent category
			if (oc.getId().equals(c.getId()))
				continue;
			
			boolean isPresent = false;
			List<Assignment> children = oc.getAssignmentList();
			if (children != null) {
				for (Assignment child : children) {
					if (child.getId().equals(assignment.getId())) {
						isPresent = true;
					}
				}
				if (isPresent)
					children.remove(assignment);
			}
		}
	}

	public Set updateAssignmentGradeRecords(Assignment assignment, Collection gradeRecords) throws RuntimeException {
		if (assignment != null) {
				for (AssignmentGradeRecord updatedGradeRecord : (Collection<AssignmentGradeRecord>)gradeRecords) {
					Map<Assignment, AssignmentGradeRecord> assignmentGradeMap = studentGradeMap.get(updatedGradeRecord.getStudentId());
					if (assignmentGradeMap == null) {
						assignmentGradeMap = new HashMap<Assignment, AssignmentGradeRecord>();
						studentGradeMap.put(updatedGradeRecord.getStudentId(), assignmentGradeMap);
					} 
					assignmentGradeMap.put(assignment, updatedGradeRecord);
					logAssignmentGradingEvent(updatedGradeRecord, "profnobody", assignment);
					updatedGradeRecord.setGradableObject(assignment);
				}
		}
		return null;
	}
	
	public Set updateAssignmentGradesAndComments(Assignment assignment, Collection gradeRecords, Collection comments) throws RuntimeException {
		updateAssignmentGradeRecords(assignment, gradeRecords);
		updateComments(comments);
		return null;
	}

	public Set updateAssignmentGradeRecords(Assignment assignment, Collection gradeRecords, int grade_type)
    {
    	if(grade_type == GradebookService.GRADE_TYPE_POINTS)
    		return updateAssignmentGradeRecords(assignment, gradeRecords);
    	else if(grade_type == GradebookService.GRADE_TYPE_PERCENTAGE)
    	{
    		Collection convertList = new ArrayList();
    		for(Iterator iter = gradeRecords.iterator(); iter.hasNext();) 
    		{
    			AssignmentGradeRecord agr = (AssignmentGradeRecord) iter.next();
    			Double doubleValue = calculateDoublePointForRecord(agr);
    			if(agr != null && doubleValue != null)
    			{
    				agr.setPointsEarned(doubleValue);
    				convertList.add(agr);
    			}
    			else if(agr != null)
    			{
    				agr.setPointsEarned(null);
    				convertList.add(agr);
    			}
    		}
    		return updateAssignmentGradeRecords(assignment, convertList);
    	}
    	else if(grade_type == GradebookService.GRADE_TYPE_LETTER)
    	{
    		Collection convertList = new ArrayList();
    		for(Iterator iter = gradeRecords.iterator(); iter.hasNext();) 
    		{
    			AssignmentGradeRecord agr = (AssignmentGradeRecord) iter.next();
    			Double doubleValue = calculateDoublePointForLetterGradeRecord(agr);
    			if(agr != null && doubleValue != null)
    			{
        		agr.setPointsEarned(doubleValue);
        		convertList.add(agr);
        	}
        	else if(agr != null)
        	{
        		agr.setPointsEarned(null);
        		convertList.add(agr);
        	}
        }
        return updateAssignmentGradeRecords(assignment, convertList);
    	}

    	else
    		return null;
    }
	
	private Double calculateDoublePointForRecord(AssignmentGradeRecord gradeRecordFromCall)
    {
    	Assignment assign = getAssignment(gradeRecordFromCall.getAssignment().getId()); 
    	if(gradeRecordFromCall.getPercentEarned() != null)
    	{
    		if(gradeRecordFromCall.getPercentEarned().doubleValue() / 100.0 < 0)
    		{
    			throw new IllegalArgumentException("percent for record is less than 0 for percentage points in GradebookManagerHibernateImpl.calculateDoublePointForRecord");
    		}
    		return new Double(assign.getPointsPossible().doubleValue() * (gradeRecordFromCall.getPercentEarned().doubleValue() / 100.0));
    	}
    	else
    		return null;
    }

	private Double calculateDoublePointForLetterGradeRecord(AssignmentGradeRecord gradeRecordFromCall)
    {
    	Assignment assign = getAssignment(gradeRecordFromCall.getAssignment().getId()); 
    	if(gradeRecordFromCall.getLetterEarned() != null)
    	{
    		LetterGradePercentMapping lgpm = getLetterGradePercentMapping(gradebook);
    		if(lgpm != null && lgpm.getGradeMap() != null)
    		{
    			Double doublePercentage = lgpm.getValue(gradeRecordFromCall.getLetterEarned());
    			if(doublePercentage == null)
    			{
    				//log.error("percentage for " + gradeRecordFromCall.getLetterEarned() + " is not found in letter grade mapping in GradebookManagerHibernateImpl.calculateDoublePointForLetterGradeRecord");
    				return null;
    			}
    			
    			return calculateEquivalentPointValueForPercent(assign.getPointsPossible(), doublePercentage);
    		}
    		return null;
    	}
    	else
    		return null;
    }
	
	protected Double calculateEquivalentPointValueForPercent(Double doublePointsPossible, Double doublePercentEarned) {
    	if (doublePointsPossible == null || doublePercentEarned == null)
    		return null;
    	
    	BigDecimal pointsPossible = new BigDecimal(doublePointsPossible.toString());
		BigDecimal percentEarned = new BigDecimal(doublePercentEarned.toString());
		BigDecimal equivPoints = pointsPossible.multiply(percentEarned.divide(new BigDecimal("100"), MATH_CONTEXT));
		return new Double(equivPoints.doubleValue());
    }

	public void updateCategory(Category category) throws RuntimeException {	
		categoryMap.put(category.getId(), category);
	}

	public void updateComments(Collection comments) throws RuntimeException {
		
		for (Comment comment : (Collection<Comment>)comments) {
			Map<Long, Comment> commentMap = studentCommentMap.get(comment.getStudentId());
			
			if (commentMap == null)
				commentMap = new HashMap<Long, Comment>();
			
			commentMap.put(comment.getGradableObject().getId(), comment);
			
			studentCommentMap.put(comment.getStudentId(), commentMap);
			
			/*Set<Comment> studentComments = studentAssignmentComments.get(comment.getStudentId());
			
			if (studentComments == null) {
				studentComments = new HashSet<Comment>();
				studentAssignmentComments.put(comment.getStudentId(), studentComments);
			}
			
			studentComments.add(comment);*/
		}
		
	}

	public void updateCourseGradeRecords(CourseGrade courseGrade, Collection gradeRecords) throws RuntimeException {
		for (CourseGradeRecord updatedGradeRecord : (Collection<CourseGradeRecord>)gradeRecords) {
			CourseGradeRecord courseGradeRecord = studentCourseGradeRecords.get(updatedGradeRecord.getStudentId());
			
			if (courseGradeRecord == null) {
				studentCourseGradeRecords.put(updatedGradeRecord.getStudentId(), updatedGradeRecord);
			}
		}
	}

	public void updateGradebook(Gradebook gradebook) throws RuntimeException {
		this.gradebook = gradebook;
	}

	public void updatePermission(Collection perms) {
		// TODO Auto-generated method stub

	}

	public Set updateStudentGradeRecords(Collection gradeRecords, int grade_type, String studentId) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean validateCategoryWeighting(Long gradebookId) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
    private List filterAndPopulateCourseGradeRecordsByStudents(CourseGrade courseGrade, Collection gradeRecords, Collection studentUids) {
		List filteredRecords = new ArrayList();
		Set missingStudents = new HashSet(studentUids);
		for (Iterator iter = gradeRecords.iterator(); iter.hasNext(); ) {
			CourseGradeRecord cgr = (CourseGradeRecord)iter.next();
			if (studentUids.contains(cgr.getStudentId())) {
				filteredRecords.add(cgr);
				missingStudents.remove(cgr.getStudentId());
			}
		}
		for (Iterator iter = missingStudents.iterator(); iter.hasNext(); ) {
			String studentUid = (String)iter.next();
			CourseGradeRecord cgr = new CourseGradeRecord(courseGrade, studentUid);
			filteredRecords.add(cgr);
		}
		return filteredRecords;
	}

	
    private double getTotalPointsInternal(final Long gradebookId, final Gradebook gradebook, final List categories, final String studentId)
    {
    	double totalPointsPossible = 0;
    	List assgnsList = assignments;

    	/*Iterator scoresIter = session.createQuery(
    	"select agr.pointsEarned, asn from AssignmentGradeRecord agr, Assignment asn where agr.gradableObject=asn and agr.studentId=:student and asn.gradebook.id=:gbid and asn.removed=false and asn.notCounted=false and asn.ungraded=false and asn.pointsPossible > 0").
    	setParameter("student", studentId).
    	setParameter("gbid", gradebookId).
    	list().iterator();*/

    	Set assignmentsTaken = new HashSet();
    	Set categoryTaken = new HashSet();
    	for (Assignment go : assignments) {
    		AssignmentGradeRecord assignmentGradeRecord = getAssignmentGradeRecordForAssignmentForStudent(go, studentId);
    		Double pointsEarned = assignmentGradeRecord.getPointsEarned();
    		if (pointsEarned != null) {
    			if(gradebook.getCategory_type() == CATEGORY_TYPE_NO_CATEGORY)
    			{
    				assignmentsTaken.add(go.getId());
    			}
    			else if(gradebook.getCategory_type() == CATEGORY_TYPE_ONLY_CATEGORY && go != null)
    			{
    				assignmentsTaken.add(go.getId());
    			}
    			else if(gradebook.getCategory_type() == CATEGORY_TYPE_WEIGHTED_CATEGORY && go != null && categories != null)
    			{
    				for(int i=0; i<categories.size(); i++)
    				{
    					Category cate = (Category) categories.get(i);
    					if(cate != null && !cate.isRemoved() && go.getCategory() != null && cate.getId().equals(go.getCategory().getId()))
    					{
    						assignmentsTaken.add(go.getId());
    						categoryTaken.add(cate.getId());
    						break;
    					}
    				}
    			}
    		}
    	}

    	if(!assignmentsTaken.isEmpty())
    	{
    		if(gradebook.getCategory_type() == CATEGORY_TYPE_WEIGHTED_CATEGORY)
    		{
	    		for(int i=0; i<categories.size(); i++)
	    		{
	    			Category cate = (Category) categories.get(i);
	    			if(cate != null && !cate.isRemoved() && categoryTaken.contains(cate.getId()) )
	    			{
	    				totalPointsPossible += cate.getWeight().doubleValue();
	    			}
	    		}
	    		return totalPointsPossible;
    		}
    		Iterator assignmentIter = assgnsList.iterator();
    		while (assignmentIter.hasNext()) {
    			Assignment asn = (Assignment) assignmentIter.next();
    			if(asn != null)
    			{
    				Double pointsPossible = asn.getPointsPossible();

    				if(gradebook.getCategory_type() == CATEGORY_TYPE_NO_CATEGORY && assignmentsTaken.contains(asn.getId()))
    				{
    					totalPointsPossible += pointsPossible.doubleValue();
    				}
    				else if(gradebook.getCategory_type() == CATEGORY_TYPE_ONLY_CATEGORY && assignmentsTaken.contains(asn.getId()))
    				{
    					totalPointsPossible += pointsPossible.doubleValue();
    				}
    			}
    		}
    	}
    	else
    		totalPointsPossible = -1;

    	return totalPointsPossible;
    }
    
    private List getTotalPointsEarnedInternal(final Long gradebookId, final String studentId, final Gradebook gradebook, final List categories) 
    {
    	double totalPointsEarned = 0;
    	double literalTotalPointsEarned = 0;
    	/*Iterator scoresIter = session.createQuery(
    			"select agr.pointsEarned, asn from AssignmentGradeRecord agr, Assignment asn where agr.gradableObject=asn and agr.studentId=:student and asn.gradebook.id=:gbid and asn.removed=false and asn.pointsPossible > 0").
    			setParameter("student", studentId).
    			setParameter("gbid", gradebookId).
    			list().iterator();*/

    	List assgnsList = assignments;

    	Map cateScoreMap = new HashMap();
    	Map cateTotalScoreMap = new HashMap();

    	Set assignmentsTaken = new HashSet();
    	for (Assignment go : assignments) {
    		AssignmentGradeRecord assignmentGradeRecord = getAssignmentGradeRecordForAssignmentForStudent(go, studentId);
    		Double pointsEarned = assignmentGradeRecord.getPointsEarned();
    		if (go.isCounted() && pointsEarned != null) {
    			if(gradebook.getCategory_type() == CATEGORY_TYPE_NO_CATEGORY)
    			{
    				totalPointsEarned += pointsEarned.doubleValue();
    				literalTotalPointsEarned += pointsEarned.doubleValue();
    				assignmentsTaken.add(go.getId());
    			}
    			else if(gradebook.getCategory_type() == CATEGORY_TYPE_ONLY_CATEGORY && go != null)
    			{
    				totalPointsEarned += pointsEarned.doubleValue();
    				literalTotalPointsEarned += pointsEarned.doubleValue();
    				assignmentsTaken.add(go.getId());
    			}
    			else if(gradebook.getCategory_type() == CATEGORY_TYPE_WEIGHTED_CATEGORY && go != null && categories != null)
    			{
    				for(int i=0; i<categories.size(); i++)
    				{
    					Category cate = (Category) categories.get(i);
    					if(cate != null && !cate.isRemoved() && go.getCategory() != null && cate.getId().equals(go.getCategory().getId()))
    					{
    						assignmentsTaken.add(go.getId());
    						literalTotalPointsEarned += pointsEarned.doubleValue();
    						if(cateScoreMap.get(cate.getId()) != null)
    						{
    							cateScoreMap.put(cate.getId(), new Double(((Double)cateScoreMap.get(cate.getId())).doubleValue() + pointsEarned.doubleValue()));
    						}
    						else
    						{
    							cateScoreMap.put(cate.getId(), new Double(pointsEarned));
    						}
    						break;
    					}
    				}
    			}
    		}
    	}

    	if(gradebook.getCategory_type() == CATEGORY_TYPE_WEIGHTED_CATEGORY && categories != null)
    	{
    		Iterator assgnsIter = assgnsList.iterator();
    		while (assgnsIter.hasNext()) 
    		{
    			Assignment asgn = (Assignment)assgnsIter.next();
    			if(assignmentsTaken.contains(asgn.getId()))
    			{
    				for(int i=0; i<categories.size(); i++)
    				{
    					Category cate = (Category) categories.get(i);
    					if(cate != null && !cate.isRemoved() && asgn.getCategory() != null && cate.getId().equals(asgn.getCategory().getId()))
    					{
    						if(cateTotalScoreMap.get(cate.getId()) == null)
    						{
    							cateTotalScoreMap.put(cate.getId(), asgn.getPointsPossible());
    						}
    						else
    						{
    							cateTotalScoreMap.put(cate.getId(), new Double(((Double)cateTotalScoreMap.get(cate.getId())).doubleValue() + asgn.getPointsPossible().doubleValue()));
    						}
    					}
    				}
    			}
    		}
    	}

    	if(assignmentsTaken.isEmpty())
    		totalPointsEarned = -1;

    	if(gradebook.getCategory_type() == CATEGORY_TYPE_WEIGHTED_CATEGORY)
    	{
    		for(int i=0; i<categories.size(); i++)
    		{
    			Category cate = (Category) categories.get(i);
    			if(cate != null && !cate.isRemoved() && cateScoreMap.get(cate.getId()) != null && cateTotalScoreMap.get(cate.getId()) != null)
    			{
    				totalPointsEarned += ((Double)cateScoreMap.get(cate.getId())).doubleValue() * cate.getWeight().doubleValue() / ((Double)cateTotalScoreMap.get(cate.getId())).doubleValue();
    			}
    		}
    	}

    	List returnList = new ArrayList();
    	returnList.add(new Double(totalPointsEarned));
    	returnList.add(new Double(literalTotalPointsEarned));
    	return returnList;
    }
    
	private long assignmentIdCount = 0;
	
	/*
	 * Helper methods
	 */
	private Assignment constructAssignment(Category category, String name, Double points, Double weight) {
		Assignment assignment = new Assignment();
		assignment.setId(Long.valueOf(assignmentIdCount));
		assignment.setCategory(category);
		assignment.setGradebook(gradebook);
		assignment.setName(name);
		assignment.setPointsPossible(points);
		assignment.setDueDate(new Date());
		assignment.setAssignmentWeighting(weight);
		assignment.setCounted(true);
		assignment.setExtraCredit(Boolean.FALSE);
		assignment.setRemoved(false);
		assignmentIdCount++;
		
		List<Assignment> assignments = category.getAssignmentList();
		
		if (assignments == null) 
			assignments = new ArrayList<Assignment>();
		
		assignments.add(assignment);
		
		int count = category.getAssignmentCount() + 1;
		category.setAssignmentCount(count);
		category.setAssignmentList(assignments);
		
		return assignment;
	}
	
	private Category constructCategory(String categoryName, Double weighting) {
		Category category = new Category();
		category.setName(categoryName);
		category.setWeight(weighting);
		category.setExtraCredit(Boolean.FALSE);
		category.setRemoved(false);
		category.setGradebook(gradebook);
		
		return category;
	}
	
	private Double generateRandomGrade(Double maxPoints) {
		
		boolean isGoodStudent = getRandomInt(10) > 2;
		boolean isBadStudent = getRandomInt(10) > 2;
		
		int halfMaxPoints = ((int)maxPoints.doubleValue()) / 2;
		if (halfMaxPoints <= 0)
			halfMaxPoints = 1;
		int randomPoints = halfMaxPoints + getRandomInt(halfMaxPoints);
		
		if (isGoodStudent) {
			randomPoints += getRandomInt(halfMaxPoints);
			if (randomPoints > maxPoints)
				randomPoints = (int)maxPoints.doubleValue();
		} else if (isBadStudent) {
			randomPoints -= getRandomInt(halfMaxPoints);
			if (randomPoints < 0)
				randomPoints = 0;
		}
		
		return Double.valueOf(randomPoints);
	}

	private Random random = new Random();
	
	private int getRandomInt(int max) {
		return random.nextInt(max);
	}
	
	private Map<String, Double> getDefaultBottomPercents() {
		Map<String, Double> map = new HashMap<String, Double>();
		
		map.put("A+", Double.valueOf(97.0));
		map.put("A", Double.valueOf(93.0));
		map.put("A-", Double.valueOf(90.0));
		
		map.put("B+", Double.valueOf(87.0));
		map.put("B", Double.valueOf(83.0));
		map.put("B-", Double.valueOf(80.0));
		
		map.put("C+", Double.valueOf(77.0));
		map.put("C", Double.valueOf(73.0));
		map.put("C-", Double.valueOf(70.0));
		
		map.put("D+", Double.valueOf(67.0));
		map.put("D", Double.valueOf(63.0));
		map.put("D-", Double.valueOf(60.0));
		
		map.put("F", Double.valueOf(0.0));
		
		map.put("I", Double.valueOf(0.0));
		
		map.put("NG", Double.valueOf(0.0));
		
		return map;
	}
	
	private List<String> getGrades() {
		List<String> list = new LinkedList<String>();
		list.add("A+");
		list.add("A");
		list.add("A-");
		
		list.add("B+");
		list.add("B");
		list.add("B-");
		
		list.add("C+");
		list.add("C");
		list.add("C-");
		
		list.add("D+");
		list.add("D");
		list.add("D-");
		
		list.add("F");
		
		return list;
	}

	public void deletePermission(Permission arg0)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	public void updatePermission(Permission arg0)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	public List<CourseGradeRecord> getAllCourseGradeRecords(Gradebook gradebook) {
		List<CourseGradeRecord> records = new ArrayList<CourseGradeRecord>();
		
		for (CourseGradeRecord record : studentCourseGradeRecords.values())
			records.add(record);
		
		return records;
	}

	public boolean isStudentCommented(final String studentId, final Long assignmentId) {
		Map<Long, Comment> commentMap = studentCommentMap.get(studentId);
		
		if (commentMap != null) {
			return commentMap.get(assignmentId) != null;
		}
		
		return false;
	}
	
	
	public boolean isStudentGraded(String studentId) {
		Map<Assignment, AssignmentGradeRecord> assignmentGradeMap = studentGradeMap.get(studentId);
				
		return assignmentGradeMap != null;
	}

	public boolean isStudentGraded(String studentId, Long gradableObjectId) {
		Map<Assignment, AssignmentGradeRecord> assignmentGradeMap = studentGradeMap.get(studentId);
		
		AssignmentGradeRecord gradeRecord = null;
		Assignment assignment = getAssignment(gradableObjectId);
		
		if (assignmentGradeMap != null) {
			gradeRecord = assignmentGradeMap.get(assignment);
		} 
		
		return gradeRecord != null;
	}

	public List<Comment> getComments(Long gradebookId) {
		List<Comment> comments = new ArrayList<Comment>();
		
		for (Map<Long, Comment> commentMap : studentCommentMap.values()) {
			for (Comment comment : commentMap.values()) 
				comments.add(comment);
		}
		
		return comments;
	}
	
	private static final int DEFAULT_NUMBER_TEST_LEARNERS = 200;
	
	private List<UserDereference> userDereferences;
	
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
	
	private String getRandomSection() {
		return SECTIONS[getRandomInt(SECTIONS.length)];
	}
	
	private UserDereference createUserDereference() {
		String studentId = String.valueOf(100000 + getRandomInt(899999));
		String firstName = FIRST_NAMES[getRandomInt(FIRST_NAMES.length)];
		String lastName = LAST_NAMES[getRandomInt(LAST_NAMES.length)];
		String lastNameFirst = lastName + ", " + firstName;
		String sortName = lastName.toUpperCase() + firstName.toUpperCase();
		String displayName = firstName + " " + lastName;
		String section = getRandomSection();
		String email = lastName + "@qau.edu";
	
		User user = new SakaiUserMock(studentId, studentId, displayName, sortName);
		
		UserDereference userRecord = new UserDereference(studentId, studentId, studentId, displayName, lastNameFirst, sortName, email);
		
		return userRecord;
	}
	
	
	
	public UserDereferenceRealmUpdate getLastUserDereferenceSync(String siteId, String realmGroupId) {
		return new UserDereferenceRealmUpdate(siteId, DEFAULT_NUMBER_TEST_LEARNERS);
	}

	public List<UserDereference> getUserDereferences(final String[] realmIds, final String sortField, final String searchField, 
			final String searchCriteria, final int offset, final int limit, final boolean isAsc, String[] roleNames) {
		
		if (userDereferences == null) {
			userDereferences = new ArrayList<UserDereference>();
			
			for (int i=0;i<DEFAULT_NUMBER_TEST_LEARNERS;i++) {
				userDereferences.add(createUserDereference());
			}
			
		}
				
		int firstRow = offset;
		int lastRow = offset + limit;
		
		if (firstRow == -1)
			firstRow = 0;
		
		if (lastRow < 0 || lastRow > DEFAULT_NUMBER_TEST_LEARNERS)
			lastRow = DEFAULT_NUMBER_TEST_LEARNERS;
		
		List<UserDereference> records = new ArrayList<UserDereference>();
		for (int i=firstRow;i<lastRow;i++) {
			records.add(userDereferences.get(i));
		}
		
		return records;
	}
	
	public void syncUserDereferenceBySite(final String siteId, final String realmGroupId, final List<User> users, int realmCount, String[] roleNames) {
		
	}
	
	public List<AssignmentGradeRecord> getAssignmentGradeRecordsForStudent(final Long gradebookId, final String studentUid) { 
		Map<Assignment, AssignmentGradeRecord> assignmentGradeMap = studentGradeMap.get(studentUid);
		
		List<AssignmentGradeRecord> gradeRecords = new ArrayList<AssignmentGradeRecord>();
		
		
		for (Assignment assignment : assignments) {
			Double points = assignment.getPointsPossible();
			
			AssignmentGradeRecord gradeRecord = null;
			if (assignmentGradeMap == null) {
				/*assignmentGradeMap = new HashMap<Assignment, AssignmentGradeRecord>();
				gradeRecord = new AssignmentGradeRecord(assignment, studentUid, generateRandomGrade(points));
				logAssignmentGradingEvent(gradeRecord, "Nobody", assignment);
				assignmentGradeMap.put(assignment, gradeRecord);
				studentGradeMap.put(studentUid, assignmentGradeMap);*/
			} else {
				gradeRecord = assignmentGradeMap.get(assignment);
				
				/*if (gradeRecord == null) {
					gradeRecord = new AssignmentGradeRecord(assignment, studentUid, generateRandomGrade(points));
					assignmentGradeMap.put(assignment, gradeRecord);
					logAssignmentGradingEvent(gradeRecord, "Nobody", assignment);
				} */
			}
			
			if (gradeRecord != null)
				gradeRecords.add(gradeRecord);
		}
		
		
		return gradeRecords;
	}
	
	public int getUserCountForSite(final String[] realmIds, final String sortField, 
			final String searchField, final String searchCriteria, String[] roleKeys) {
		return DEFAULT_NUMBER_TEST_LEARNERS;
	}
	
	public List<Comment> getComments(final Long gradebookId, final String[] realmIds, String[] roleNames, final String sortField, 
			final String searchField, final String searchCriteria, final int offset, final int limit, final boolean isAsc) {

		return null;
	}
	
	
	public List<CourseGradeRecord> getAllCourseGradeRecords(final Long gradebookId, final String[] realmIds, final String sortField, final String searchField, 
			final String searchCriteria, final int offset, final int limit, final boolean isAsc, String[] roleNames) {
		
		return null;
	}
	
	public int getDereferencedUserCountForSite(final String siteId, final String realmGroupId, final String[] roleNames) {
		
		return DEFAULT_NUMBER_TEST_LEARNERS;
	}
	
	public int getFullUserCountForSite(final String siteId, final String realmGroupId, String[] roleNames) {
	
		return DEFAULT_NUMBER_TEST_LEARNERS;
	}
	
	public List<Object[]> getUserGroupReferences(final List<String> groupReferences, String[] roleNames) {
		
		return null;
	}
	
	
	public List<String> getFullUserListForSite(final String siteId, final String[] roleNames) {
		
		return null;
	}
	
	public boolean isStudentMissingScores(final Long gradebookId, final String studentId, final boolean hasCategories) {

		return false;
	}
	
	public Comment getCommentForItemForStudent(final Long assignmentId, final String studentId) {
		return null;
	}
	
	public void updateComment(final Comment comment) throws StaleObjectModificationException {
		
	}
	
	public void createOrUpdateUserConfiguration(final String userUid, final Long gradebookId, final String configField, final String configValue) {

	}

	public List<UserConfiguration> getUserConfigurations(String userUid, Long gradebookId) {
		return null;
	}
	
	public List<AssignmentGradeRecord> getAllAssignmentGradeRecords(final Long[] assignmentIds) {
		return null;
	}
	
	public Set<GradeMapping> getGradeMappings(Long id) {
		return null;
	}

	public GradeMapping getGradeMapping(final Long id) {
		return null;
	}
	
	public Long createPermission(Permission permission) {
		permission.setId(permissionId);
		permissions.add(permission);
		return permissionId++;
	}
}
