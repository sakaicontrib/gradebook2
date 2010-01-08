package org.sakaiproject.gradebook.gwt.sakai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.client.action.Action.ActionType;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationKey;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumnKey;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumnModel;
import org.sakaiproject.gradebook.gwt.client.model.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookKey;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.LearnerKey;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.sakai.model.ActionRecord;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.AssignmentGradeRecord;
import org.sakaiproject.tool.gradebook.Category;
import org.sakaiproject.tool.gradebook.Comment;
import org.sakaiproject.tool.gradebook.CourseGrade;
import org.sakaiproject.tool.gradebook.CourseGradeRecord;
import org.sakaiproject.tool.gradebook.GradeMapping;
import org.sakaiproject.tool.gradebook.Gradebook;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;

public class Gradebook2ComponentServiceImpl extends Gradebook2ServiceImpl
		implements Gradebook2ComponentService {

	private static final Log log = LogFactory.getLog(Gradebook2ComponentServiceImpl.class);
	
	public Map<String, Object> addItem(String gradebookUid, Map<String, Object> attributes) {
		
		
		return attributes;
	}
	
	public Map<String, Object> assignComment(String itemId, String studentUid, String text) {

		int indexOf = itemId.indexOf(StudentModel.COMMENT_TEXT_FLAG);
		Long assignmentId = Long.valueOf(itemId.substring(0, indexOf));
		
		Comment comment = gbService.getCommentForItemForStudent(assignmentId, studentUid);

		String actionType = null;
		Assignment assignment = null;
		if (comment == null) {
			// We don't need to create a comment object if the user is just passing up a blank
			// comment
			if (text == null || text.equals(""))
				return null;
			
			assignment = gbService.getAssignment(assignmentId);
			comment = new Comment(studentUid, text, assignment);
			actionType = ActionType.CREATE.name();
		} else {
			assignment = (Assignment)comment.getGradableObject();
			comment.setCommentText(text);
			actionType = ActionType.UPDATE.name();
		}
		
		Gradebook gradebook = assignment.getGradebook();
		ActionRecord actionRecord = new ActionRecord(gradebook.getUid(), gradebook.getId(), EntityType.COMMENT.name(), actionType);
		actionRecord.setEntityName(assignment.getName());
		Map<String, String> propertyMap = actionRecord.getPropertyMap();
		propertyMap.put("comment", text);
		
		try {
			gbService.updateComment(comment);
			postEvent("gradebook2.comment", String.valueOf(gradebook.getId()), String.valueOf(assignment.getId()), studentUid);
		} catch (RuntimeException e) {
			actionRecord.setStatus(ActionRecord.STATUS_FAILURE);
			throw e;
		} finally {
			gbService.storeActionRecord(actionRecord);
		}
		
		//student.set(action.getKey(), comment.getText());
		//student.set(new StringBuilder(assignmentId).append(StudentModel.COMMENTED_FLAG).toString(), Boolean.TRUE);

		Site site = getSite();
		User user = null;
		try {
			user = userService.getUser(studentUid);
		} catch (UserNotDefinedException unde) {
			log.warn("User not defined: " + studentUid);
		}
		return getStudent(gradebook, site, user);
	}
	
	public Map<String, Object> assignScore(String gradebookUid, String studentUid, String assignmentId, Double value, Double previousValue) throws InvalidInputException {
		Assignment assignment = gbService.getAssignment(Long.valueOf(assignmentId));
		Gradebook gradebook = assignment.getGradebook();

		ActionRecord actionRecord = new ActionRecord(gradebook.getUid(), gradebook.getId(), EntityType.GRADE_RECORD.name(), ActionType.GRADED.name());
		actionRecord.setEntityId(String.valueOf(assignment.getId()));
		actionRecord.setStudentUid(studentUid);
		Map<String, String> propertyMap = actionRecord.getPropertyMap();

		propertyMap.put("score", String.valueOf(value));

		List<AssignmentGradeRecord> gradeRecords = gbService.getAssignmentGradeRecordsForStudent(gradebook.getId(), studentUid);

		AssignmentGradeRecord assignmentGradeRecord = null;

		for (AssignmentGradeRecord currentGradeRecord : gradeRecords) {
			Assignment a = currentGradeRecord.getAssignment();
			if (a.getId().equals(assignment.getId()))
				assignmentGradeRecord = currentGradeRecord;
		}

		if (assignmentGradeRecord == null) {
			assignmentGradeRecord = new AssignmentGradeRecord();
		}

		scoreItem(gradebook, assignment, assignmentGradeRecord, studentUid, value, false, false);

		gradeRecords = gbService.getAssignmentGradeRecordsForStudent(gradebook.getId(), studentUid);

		//refreshLearnerData(gradebook, student, assignment, gradeRecords);
		
		Site site = getSite();
		User user = null;
		try {
			user = userService.getUser(studentUid);
		} catch (UserNotDefinedException unde) {
			log.warn("User not defined: " + studentUid);
		}
		Map<String, Object> student = getStudent(gradebook, site, user);
		
		actionRecord.setEntityName(new StringBuilder().append((String)student.get(LearnerKey.DISPLAY_NAME.name())).append(" : ").append(assignment.getName()).toString());
		gbService.storeActionRecord(actionRecord);

		return student;
	}

	
	public Map<String, Object> assignScore(String gradebookUid, String studentUid, String property, String value, String previousValue) throws InvalidInputException {
		if (value != null && value.trim().equals(""))
			value = null;

		if (value != null)
			value = value.toUpperCase();

		if (property == null)
			return null;
		
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
				
		User user = null;
		try {
			user = userService.getUser(studentUid);
		} catch (UserNotDefinedException unde) {
			log.warn("User not defined: " + studentUid);
		}
		
		Map<String, Object> student = null;
		
		if (property.equals(LearnerKey.GRADE_OVERRIDE.name())) {
			// GRBK-233 : Only IOR can overwrite course grades
			boolean isInstructor = authz.isUserAbleToGradeAll(gradebook.getUid());
			if (!isInstructor)
				throw new InvalidInputException("You are not authorized to overwrite the course grade for this student.");
			
			// Then we are overriding a course grade
			CourseGradeRecord courseGradeRecord = gbService.getStudentCourseGradeRecord(gradebook, studentUid);
			courseGradeRecord.setEnteredGrade(value);
			Collection<CourseGradeRecord> gradeRecords = new LinkedList<CourseGradeRecord>();
			gradeRecords.add(courseGradeRecord);
			// FIXME: We shouldn't be looking up the CourseGrade if we don't use it
			// anywhere.
			CourseGrade courseGrade = gbService.getCourseGrade(gradebook.getId());
	
			GradeMapping gradeMapping = gradebook.getSelectedGradeMapping();
			Set<String> scaledGrades = gradeMapping.getGradeMap().keySet();
	
			if (value != null && !advisor.isValidOverrideGrade(value, user.getEid(), user.getDisplayId(), gradebook, scaledGrades))
				throw new InvalidInputException("This is not a valid override grade for this individual in this course.");
	
			gbService.updateCourseGradeRecords(courseGrade, gradeRecords);
	
			ActionRecord actionRecord = new ActionRecord(gradebook.getUid(), gradebook.getId(), EntityType.COURSE_GRADE_RECORD.name(), ActionType.GRADED.name());
			actionRecord.setEntityId(String.valueOf(gradebook.getId()));
			actionRecord.setStudentUid(studentUid);
			Map<String, String> propertyMap = actionRecord.getPropertyMap();

			propertyMap.put("score", value);
			
			Site site = getSite();
			/*List<FixedColumnModel> columns = getColumns(true);
			UserRecord userRecord = buildUserRecord(site, user, gradebook);
			List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
			List<Category> categories = null;
			if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY)
				categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);
			student = buildLearnerGradeRecord(gradebook, userRecord, columns, assignments, categories);
			*/
			
			student = getStudent(gradebook, site, user);
			actionRecord.setEntityName(new StringBuilder().append((String)student.get(LearnerKey.DISPLAY_NAME.name())).append(" : ").append(gradebook.getName()).toString());
			gbService.storeActionRecord(actionRecord);
			
			/*
			List<Category> categories = null;
			List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
			if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY)
				categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);
	
			Map<Long, AssignmentGradeRecord> studentGradeMap = new HashMap<Long, AssignmentGradeRecord>();
			List<AssignmentGradeRecord> records = gbService.getAssignmentGradeRecordsForStudent(gradebook.getId(), (String)student.get(LearnerKey.UID.name()));
	
			if (records != null) {
				for (AssignmentGradeRecord record : records) {
					studentGradeMap.put(record.getAssignment().getId(), record);
				}
			}
	
			BigDecimal calculatedGrade = getCalculatedGrade(gradebook, assignments, categories, studentGradeMap);
			DisplayGrade displayGrade = getDisplayGrade(gradebook, (String)student.get(LearnerKey.UID.name()), courseGradeRecord, calculatedGrade);// requestCourseGrade(gradebookUid,
			displayGrade.setOverridden(value != null);
			student.set(LearnerKey.GRADE_OVERRIDE.name(), courseGradeRecord.getEnteredGrade());
			student.set(LearnerKey.COURSE_GRADE.name(), displayGrade.toString());
			student.set(LearnerKey.LETTER_GRADE.name(), displayGrade.getLetterGrade());*/
		} else if (gradebook.getGrade_type() == GradebookService.GRADE_TYPE_LETTER) {
			// We must be modifying a letter grade
			if (value != null && !gradeCalculations.isValidLetterGrade(value))
				throw new InvalidInputException("This is not a valid grade.");
			
			Double numericValue = gradeCalculations.convertLetterGradeToPercentage(value);
			Double previousNumericValue = gradeCalculations.convertLetterGradeToPercentage(previousValue);
			
			student = assignScore(gradebookUid, studentUid, property, numericValue, previousNumericValue);
		}
		
		return student;
	}
	
	public Map<String, Object> getApplicationMap(String... gradebookUids) {

		Map<String,Object> map = new HashMap<String,Object>();
		//model.setGradebookModels(getGradebookModels(gradebookUids));
		
		List<GradebookModel> gbModels = getGradebookModels(gradebookUids);
		
		List<Map<String,Object>> gradebookMaps = new ArrayList<Map<String,Object>>();
		
		for (GradebookModel gbModel : gbModels) {
			Map<String,Object> gbMap = new HashMap<String,Object>();
			
			for (Enum<GradebookKey> en : EnumSet.allOf(GradebookKey.class)) {
				
				if (en.equals(GradebookKey.GRADEBOOKITEMMODEL)) {
					ItemModel gradebookItemModel = gbModel.get(en.name());
					Map<String, Object> gradebookItemMap = new HashMap<String, Object>();
					
					for (Enum<ItemKey> it : EnumSet.allOf(ItemKey.class)) {
						gradebookItemMap.put(it.name(), gradebookItemModel.get(it.name()));
					}
					
					addChildren(gradebookItemModel, gradebookItemMap);
					
					gbMap.put(en.name(), gradebookItemMap);
				} else if (en.equals(GradebookKey.COLUMNS)) { 
					List<FixedColumnModel> fixedColumnModelList = gbModel.get(en.name());
					List<Map<String,Object>> fixedColumnMapList = new ArrayList<Map<String,Object>>();
					if (fixedColumnModelList != null) {
						for (FixedColumnModel fixedColumnModel : fixedColumnModelList) {
							Map<String,Object> fixedColumnMap = new HashMap<String, Object>();
							
							for (Enum<FixedColumnKey> it : EnumSet.allOf(FixedColumnKey.class)) {
								fixedColumnMap.put(it.name(), fixedColumnModel.get(it.name()));
							}
							fixedColumnMapList.add(fixedColumnMap);
						}
						gbMap.put(en.name(), fixedColumnMapList);
					}
				} else {
					gbMap.put(en.name(), gbModel.get(en.name()));
				}
				
			}
			gradebookMaps.add(gbMap);
			
			//gbMap.put(key, value);
		}
		
		map.put(ApplicationKey.GRADEBOOKMODELS.name(), gradebookMaps);
		map.put(ApplicationKey.HELPURL.name(), helpUrl);
		
		List<String> gradeTypes = new ArrayList<String>();
		for (GradeType gradeType : enabledGradeTypes) {
			gradeTypes.add(gradeType.name());	
		}
		map.put(ApplicationKey.ENABLEDGRADETYPES.name(), gradeTypes);
		
		return map;
	}
	
	
	public Boolean updateConfiguration(Long gradebookId, String field, String value) {
		
		try {		
			gbService.createOrUpdateUserConfiguration(getCurrentUser(), gradebookId, field, value);
	
		} catch (Exception e) {
			
			return Boolean.FALSE;
		}
		
		return Boolean.TRUE;
	}
	
	private void addChildren(ItemModel itemModel, Map<String,Object> itemMap) {
		
		if (itemModel.getChildCount() > 0) {
			List<Map<String,Object>> childrenList = new ArrayList<Map<String,Object>>();
			for (int i=0;i<itemModel.getChildCount();i++) {
				ItemModel child = (ItemModel)itemModel.getChild(i);
				Map<String,Object> childMap = new HashMap<String,Object>();
				
				for (Enum<ItemKey> it : EnumSet.allOf(ItemKey.class)) {
					childMap.put(it.name(), child.get(it.name()));
				}
				childrenList.add(childMap);
				addChildren(child, childMap);
			}
			itemMap.put(ItemKey.CHILDREN.name(), childrenList);
		}
		
	}
	
	private Map<String, Object> getStudent(Gradebook gradebook, Site site, User user) {
		List<FixedColumnModel> columns = getColumns(true);
		UserRecord userRecord = buildUserRecord(site, user, gradebook);
		List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
		List<Category> categories = null;
		if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY)
			categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);
		return buildLearnerGradeRecord(gradebook, userRecord, columns, assignments, categories);
	}
	
}
