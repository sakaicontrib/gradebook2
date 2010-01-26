package org.sakaiproject.gradebook.gwt.sakai;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.Formatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.ActionType;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.model.ActionKey;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationKey;
import org.sakaiproject.gradebook.gwt.client.model.ConfigurationModel;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumnKey;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumnModel;
import org.sakaiproject.gradebook.gwt.client.model.GradeEventKey;
import org.sakaiproject.gradebook.gwt.client.model.GradeFormatKey;
import org.sakaiproject.gradebook.gwt.client.model.GradeMapKey;
import org.sakaiproject.gradebook.gwt.client.model.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookKey;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.GraderKey;
import org.sakaiproject.gradebook.gwt.client.model.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.LearnerKey;
import org.sakaiproject.gradebook.gwt.client.model.PermissionEntryListModel;
import org.sakaiproject.gradebook.gwt.client.model.PermissionsModel;
import org.sakaiproject.gradebook.gwt.client.model.PermissionKey;
import org.sakaiproject.gradebook.gwt.client.model.SectionKey;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.sakai.InstitutionalAdvisor.Column;
import org.sakaiproject.gradebook.gwt.sakai.model.ActionRecord;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.section.api.coursemanagement.ParticipationRecord;
import org.sakaiproject.section.api.facade.Role;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.site.api.Site;
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
import org.sakaiproject.tool.gradebook.Permission;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;

public class Gradebook2ComponentServiceImpl extends Gradebook2ServiceImpl
		implements Gradebook2ComponentService {

	private static final Log log = LogFactory.getLog(Gradebook2ComponentServiceImpl.class);
	private static ResourceBundle i18n = ResourceBundle.getBundle("org.sakaiproject.gradebook.gwt.client.I18nConstants");
	
	public Map<String, Object> createItem(String gradebookUid, Long gradebookId, Map<String, Object> attributes) throws InvalidInputException {
	
		ItemModel itemModel = createItem(gradebookUid, gradebookId, toItem(attributes), true);
		
		Map<String, Object> itemMap = new HashMap<String, Object>();
		
		for (Enum<ItemKey> it : EnumSet.allOf(ItemKey.class)) {
			itemMap.put(it.name(), itemModel.get(it.name()));
		}
		
		addChildren(itemModel, itemMap);
		
		return itemMap;
	}
	
	public Map<String, Object> createPermission(String gradebookUid, Long gradebookId, Map<String, Object> attributes) throws InvalidInputException {
		Permission newPermission = toPermission(gradebookId, attributes);
		// First, we want to verify that the permission does not already exist
		List<Permission> permissions = gbService.getPermissionsForUser(gradebookId, newPermission.getUserId());

		// We can ignore this check is the grader has no permissions yet
		if (permissions != null && !permissions.isEmpty()) {
			
			for (Permission permission : permissions) {
				if (permission.getFunction().equals(newPermission.getFunction())) {
					// The new permission has the same function as an existing permission
					
					Long categoryId = permission.getCategoryId();
					Long newCategoryId = newPermission.getCategoryId();
					
					String groupId = permission.getGroupId();
					String newGroupId = newPermission.getGroupId();
	
					if (groupId != null && groupId.equalsIgnoreCase("ALL"))
						groupId = null;
					if (newGroupId != null && newGroupId.equalsIgnoreCase("ALL"))
						newGroupId = null;
						
					boolean isCategoryInclusive = categoryId == null || (newCategoryId != null && categoryId.equals(newCategoryId));
					boolean isGroupInclusive = groupId == null || (newGroupId != null && groupId.equals(newGroupId));
			
					boolean isNewCategoryInclusive = newCategoryId == null || (categoryId != null && newCategoryId.equals(categoryId));
					boolean isNewGroupInclusive = newGroupId == null || (groupId != null && groupId.equals(newGroupId));
					
					if (isCategoryInclusive && isGroupInclusive)
						throw new InvalidInputException(i18n.getString("duplicatePermission"));
					else if (isNewCategoryInclusive && isNewGroupInclusive)
						gbService.deletePermission(permission.getId());
					else if (isNewCategoryInclusive && isGroupInclusive)
						throw new InvalidInputException(i18n.getString("duplicatePermission"));
					else if (isCategoryInclusive && isNewGroupInclusive)
						throw new InvalidInputException(i18n.getString("duplicatePermission"));
				}
			}
		}
		
		Long id = gbService.createPermission(newPermission);
		attributes.put(PermissionKey.ID.name(), id);
		return attributes;
	}
	
	public Map<String, Object> deletePermission(Map<String, Object> attributes) {
		Permission p = toPermission(null, attributes);
		gbService.deletePermission(p.getId());
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
				throw new InvalidInputException(i18n.getString("notAuthToOverwriteCourseGrade"));
			
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
				throw new InvalidInputException(i18n.getString("invalidOverrideCourseGrade"));
	
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
				throw new InvalidInputException(i18n.getString("invalidLetterGrade"));
			
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
				} else if (en.equals(GradebookKey.USERASSTUDENT)) {
					StudentModel studentModel = gbModel.get(en.name());
					if (studentModel != null) {
						Map<String, Object> studentMap = new HashMap<String, Object>();
						
						for (Enum<LearnerKey> it : EnumSet.allOf(LearnerKey.class)) {
							studentMap.put(it.name(), studentModel.get(it.name()));
						}
						
						gbMap.put(en.name(), studentMap);
					}
				} else if (en.equals(GradebookKey.CONFIGURATIONMODEL)) {
					ConfigurationModel configModel = gbModel.get(en.name());
					if (configModel != null) {
						Map<String, Object> configMap = new HashMap<String, Object>();
										
						for (String key : configModel.getPropertyNames()) {
							configMap.put(key, configModel.get(key));
						}
						
						gbMap.put(en.name(), configMap);
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
	
	public List<Map<String,Object>> getAvailableGradeFormats(String gradebookUid, Long gradebookId) {

		List<Map<String,Object>> models = new ArrayList<Map<String,Object>>();

		Set<GradeMapping> gradeMappings = gbService.getGradeMappings(gradebookId);

		for (GradeMapping mapping : gradeMappings) {
			Map<String,Object> model = new HashMap<String,Object>();
			model.put(GradeFormatKey.ID.name(), mapping.getId());
			model.put(GradeFormatKey.NAME.name(), mapping.getName());
			models.add(model);
		}

		return models;
	}
	
	public List<Map<String,Object>> getGradeEvents(Long assignmentId, String studentUid) {
		List<Map<String,Object>> gradeEvents = new ArrayList<Map<String,Object>>();
		Assignment assignment = gbService.getAssignment(assignmentId);
		Collection<GradableObject> gradableObjects = new LinkedList<GradableObject>();
		gradableObjects.add(assignment);

		Map<GradableObject, List<GradingEvent>> map = gbService.getGradingEventsForStudent(studentUid, gradableObjects);

		List<GradingEvent> events = map.get(assignment);

		if (events != null) {
			Collections.sort(events, new Comparator<GradingEvent>() {

				public int compare(GradingEvent o1, GradingEvent o2) {

					if (o2.getDateGraded() == null || o1.getDateGraded() == null)
						return 0;

					return o2.getDateGraded().compareTo(o1.getDateGraded());
				}

			});

			for (GradingEvent event : events) {
				gradeEvents.add(buildGradeEvent(event));
			}
		}

		return gradeEvents;
	}
	
	public List<Map<String,Object>> getGradeMaps(String gradebookUid) {
		List<Map<String,Object>> gradeScaleMappings = new ArrayList<Map<String,Object>>();
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		GradeMapping gradeMapping = gradebook.getSelectedGradeMapping();

		List<String> letterGradesList = new ArrayList<String>(gradeMapping.getGradeMap().keySet());

		if (gradeMapping.getName().equalsIgnoreCase("Pass / Not Pass")) 
			Collections.sort(letterGradesList, PASS_NOPASS_COMPARATOR);
		else
			Collections.sort(letterGradesList, LETTER_GRADE_COMPARATOR);

		Double upperScale = null;

		for (String letterGrade : letterGradesList) {

			upperScale = (null == upperScale) ? new Double(100d) : upperScale.equals(Double.valueOf(0d)) ? Double.valueOf(0d) : Double.valueOf(upperScale.doubleValue() - 0.01d);

			Map<String,Object> gradeScaleModel = new HashMap<String,Object>();
			gradeScaleModel.put(GradeMapKey.ID.name(), letterGrade);
			gradeScaleModel.put(GradeMapKey.LETTER_GRADE.name(), letterGrade);
			gradeScaleModel.put(GradeMapKey.FROM_RANGE.name(), gradeMapping.getGradeMap().get(letterGrade));
			gradeScaleModel.put(GradeMapKey.TO_RANGE.name(), upperScale);

			gradeScaleMappings.add(gradeScaleModel);
			upperScale = gradeMapping.getGradeMap().get(letterGrade);
		}
		return gradeScaleMappings;
	}
	
	public List<Map<String, Object>> getGraders(String gradebookUid, Long gradebookId) {
		List<Map<String, Object>> userList = new ArrayList<Map<String, Object>>();

		String placementId = lookupDefaultGradebookUid();
		List<ParticipationRecord> participationList = sectionAwareness.getSiteMembersInRole(placementId, Role.TA);

		for (ParticipationRecord participationRecord : participationList) {
			org.sakaiproject.section.api.coursemanagement.User user = participationRecord.getUser();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(GraderKey.ID.name(), user.getUserUid());
			map.put(GraderKey.USER_DISPLAY_NAME.name(), user.getDisplayName());
			userList.add(map);
		}
		
		return userList;
	}
	
	public List<Map<String,Object>> getHistory(String gradebookUid, Long gradebookId,
			Integer offset, Integer limit) {
		
		int off = offset == null ? -1 : offset.intValue();
		int lim = limit == null ? -1 : limit.intValue();
		
		Integer size = gbService.getActionRecordSize(gradebookUid);
		List<ActionRecord> actionRecords = gbService.getActionRecords(gradebookUid, off, lim);
		List<Map<String,Object>> models = new ArrayList<Map<String,Object>>();

		String description = null;
		DateFormat format = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
		format.setLenient(true);
		
		for (ActionRecord actionRecord : actionRecords) {
			Map<String,Object> actionModel = new HashMap<String,Object>();
			try {
				UserEntityAction.ActionType actionType = UserEntityAction.ActionType.valueOf(actionRecord.getActionType());
				UserEntityAction.EntityType entityType = UserEntityAction.EntityType.valueOf(actionRecord.getEntityType());
				String score = null;
				StringBuilder text = new StringBuilder();
				switch (actionType) {
					case CREATE:
						description = new StringBuilder().append(actionType.getVerb()).append(" ")
							.append(entityType).toString();
						break;
					case GRADED:
						score = actionRecord.getPropertyMap().get("score");
						if (score == null)
							score = "";
						actionModel.put("score", score);
						
						text.append(actionType.getVerb()).append(" '").append(score)
							.append("'");
	
						description = text.toString();
						break;
					case SUBMITTED:
						score = actionRecord.getPropertyMap().get(Column.LETTER_GRADE.name());
						if (score == null)
							score = "";
						actionModel.put(ActionKey.VALUE.name(), score);
						
						text.append(actionType.getVerb()).append(" '").append(score)
							.append("'");

						description = text.toString();
						break;
					case UPDATE:
						actionModel.put(ActionKey.VALUE.name(), actionRecord.getEntityName());
						description = actionType.getVerb();
						break;
				}

				if (actionModel == null)
					continue;
				
				actionModel.put(ActionKey.ID.name(), String.valueOf(actionRecord.getId()));
				actionModel.put(ActionKey.GRADEBOOK_UID.name(),actionRecord.getGradebookUid());
				actionModel.put(ActionKey.GRADEBOOK_ID.name(),actionRecord.getGradebookId());
				actionModel.put(ActionKey.ENTITY_TYPE.name(), entityType.name());
				if (actionRecord.getEntityId() != null)
					actionModel.put(ActionKey.ENTITY_ID.name(),actionRecord.getEntityId());
				if (actionRecord.getEntityName() != null)
					actionModel.put(ActionKey.ENTITY_NAME.name(),actionRecord.getEntityName());
				if (actionRecord.getParentId() != null)
					actionModel.put(ActionKey.PARENT_ID.name(),Long.valueOf(actionRecord.getParentId()));
				
				String studentUid = actionRecord.getStudentUid();
				actionModel.put(ActionKey.STUDENT_UID.name(), studentUid);
				
				if (actionRecord.getEntityName() != null && actionRecord.getEntityName().contains(" : ")) {
					String[] parts = actionRecord.getEntityName().split(" : ");
					
					actionModel.put(ActionKey.STUDENT_NAME.name(),parts[0]);
					actionModel.put(ActionKey.ENTITY_NAME.name(),parts[1]);
				}

				actionModel.put(ActionKey.GRADER_NAME.name(), actionRecord.getGraderId());

				if (userService != null && actionRecord.getGraderId() != null) {

					try {
						User user = userService.getUser(actionRecord.getGraderId());
						actionModel.put(ActionKey.GRADER_NAME.name(), user.getDisplayName());
					} catch (UserNotDefinedException e) {
						log.warn("Unable to find grader name for " + actionRecord.getGraderId(), e);
					}

				}

				if (actionRecord.getDatePerformed() != null) 
					actionModel.put(ActionKey.DATE_PERFORMED.name(), String.valueOf(actionRecord.getDatePerformed()));
				if (actionRecord.getDateRecorded() != null)
					actionModel.put(ActionKey.DATE_RECORDED.name(), String.valueOf(actionRecord.getDateRecorded()));

				Map<String, String> propertyMap = actionRecord.getPropertyMap();

				if (propertyMap != null) {
					for (String key : propertyMap.keySet()) {
						String value = propertyMap.get(key);

						if (value != null && !value.equals("null"))
							actionModel.put(key, value);
					}
				}
				
				actionModel.put(ActionKey.DESCRIPTION.name(), description);

				models.add(actionModel);
			} catch (Exception e) {
				log.warn("Failed to retrieve history record for " + actionRecord.getId());
			}
		}
		return models;
	}
	
	public List<Map<String,Object>> getItems(String gradebookUid, Long gradebookId, String type) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		
		if (type == null || type.equals(ItemKey.ITEM_TYPE.name())) {
			List<Category> categoryList = gbService.getCategories(gradebookId);

			Map<String,Object> map = new HashMap<String, Object>();
			map.put(ItemKey.NAME.name(), "All Categories");
			list.add(map);

			for (Category category : categoryList) {
				if (!category.isRemoved()) {
					map = new HashMap<String, Object>();
					map.put(ItemKey.ID.name(), String.valueOf(category.getId()));
					map.put(ItemKey.NAME.name(), category.getName());
					map.put(ItemKey.CATEGORY_ID.name(), category.getId());
					map.put(ItemKey.CATEGORY_NAME.name(), category.getName());
					list.add(map);
				}
			}
		}
		
		return list;
	}
	
	public List<Map<String,Object>> getPermissions(String gradebookUid, Long gradebookId, String graderId) {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();

		List<Permission> permissions = gbService.getPermissionsForUser(gradebookId, graderId);

		for (Permission permission : permissions) {

			Map<String,Object> map = new HashMap<String,Object>();
			map.put(PermissionKey.ID.name(), permission.getId());
			map.put(PermissionKey.USER_ID.name(), permission.getUserId());

			try {
				User user = userService.getUser(permission.getUserId());

				if (null != user) {
					map.put(PermissionKey.USER_DISPLAY_NAME.name(), user.getDisplayName());
				} else {
					log.error("Was not able go get an User object from userId = " + permission.getUserId());
				}
			} catch (UserNotDefinedException e) {
				log.error("Was not able go get an User object from userId = " + permission.getUserId());
				e.printStackTrace();
			}

			map.put(PermissionKey.PERMISSION_ID.name(), permission.getFunction());

			// If category id is null, the all categories were selected
			if (null != permission.getCategoryId()) {

				map.put(PermissionKey.CATEGORY_ID.name(), permission.getCategoryId());
				Category category = gbService.getCategory(permission.getCategoryId());
				if (null != category) {
					map.put(PermissionKey.CATEGORY_DISPLAY_NAME.name(), category.getName());
				} else {
					// TODO: handle error
					log.error("Category is null");
				}

			} else {
				map.put(PermissionKey.CATEGORY_ID.name(), "ALL");
				map.put(PermissionKey.CATEGORY_DISPLAY_NAME.name(),"All");
			}

			// If section id is null, then all sections were selected
			if (null != permission.getGroupId() && !permission.getGroupId().equalsIgnoreCase("ALL")) {
				map.put(PermissionKey.SECTION_ID.name(), permission.getGroupId());
				CourseSection courseSection = sectionAwareness.getSection(permission.getGroupId());
				if (null != courseSection) {
					map.put(PermissionKey.SECTION_DISPLAY_NAME.name(), courseSection.getTitle());
				} else {
					// TODO: handle error
					log.error("CourseSection is null");
				}
			} else {
				map.put(PermissionKey.SECTION_ID.name(), "ALL");
				map.put(PermissionKey.SECTION_DISPLAY_NAME.name(), "All");
			}

			map.put(PermissionKey.DELETE_ACTION.name(), "Delete");
			list.add(map);
		}

		return list;
	}
	
		
	public List<Map<String,Object>> getVisibleSections(String gradebookUid, boolean enableAllSectionsEntry, String allSectionsEntryTitle) {
		List<CourseSection> viewableSections = authz.getViewableSections(gradebookUid);

		List<Map<String,Object>> sections = new LinkedList<Map<String,Object>>();

		if (enableAllSectionsEntry) {
			Map<String,Object> map = new HashMap<String,Object>();
			map.put(SectionKey.ID.name(), "ALL");
			map.put(SectionKey.SECTION_NAME.name(), allSectionsEntryTitle);
			sections.add(map);
		}

		if (viewableSections != null) {
			for (CourseSection courseSection : viewableSections) {
				Map<String,Object> map = new HashMap<String,Object>();
				map.put(SectionKey.ID.name(), courseSection.getUuid());
				map.put(SectionKey.SECTION_NAME.name(), courseSection.getTitle());
				sections.add(map);
			}
		}
		
		return sections;
	}
	
	public void resetGradeMap(String gradebookUid) {
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		GradeMapping gradeMapping = gradebook.getSelectedGradeMapping();
		gradeMapping.setDefaultValues();
		gbService.updateGradebook(gradebook);
	}
	
	public Boolean updateConfiguration(Long gradebookId, String field, String value) {
		
		try {		
			gbService.createOrUpdateUserConfiguration(getCurrentUser(), gradebookId, field, value);
	
		} catch (Exception e) {
			
			return Boolean.FALSE;
		}
		
		return Boolean.TRUE;
	}
	
	public void updateGradeMap(String gradebookUid, String affectedLetterGrade, Object value) throws InvalidInputException {
		
		if (value == null) {
			throw new InvalidInputException(i18n.getString("noBlankValue"));
		}
		
		double v = -1d;
		
		if (value instanceof Integer)
			v = ((Integer)value).doubleValue();
		else if (value instanceof Double)
			v = ((Double)value).doubleValue();
		else
			throw new InvalidInputException(i18n.getString("noNonNumericValue"));
		
		BigDecimal bigValue = BigDecimal.valueOf(v).setScale(AppConstants.DISPLAY_SCALE, GradeCalculations.MATH_CONTEXT.getRoundingMode());

		if (bigValue.compareTo(BigDecimal.ZERO) == 0)
			throw new InvalidInputException(i18n.getString("noZeroValue"));
		if (bigValue.compareTo(BigDecimal.valueOf(100.00d)) >= 0) {
			StringBuilder sb = new StringBuilder();
			Formatter formatter = new Formatter(sb);
			formatter.format(i18n.getString("valueMustBeLessThan100"), 
					bigValue.setScale(AppConstants.DISPLAY_SCALE, GradeCalculations.MATH_CONTEXT.getRoundingMode()).toString());

			throw new InvalidInputException(sb.toString());
		}
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		GradeMapping gradeMapping = gradebook.getSelectedGradeMapping();
		
		List<String> letterGradesList = new ArrayList<String>(gradeMapping.getGradeMap().keySet());

		if (gradeMapping.getName().equalsIgnoreCase("Pass / Not Pass")) 
			Collections.sort(letterGradesList, PASS_NOPASS_COMPARATOR);
		else
			Collections.sort(letterGradesList, LETTER_GRADE_COMPARATOR);

		Double upperScale = null;

		for (String letterGrade : letterGradesList) {
			BigDecimal bigOldUpperScale = upperScale == null ? BigDecimal.valueOf(200d) : BigDecimal.valueOf(upperScale.doubleValue());
			
			upperScale = (null == upperScale) ? new Double(100d) : upperScale.equals(Double.valueOf(0d)) ? Double.valueOf(0d) : Double.valueOf(upperScale.doubleValue() - 0.01d);

			if (affectedLetterGrade.equals(letterGrade)) {
				Double oldValue = gradeMapping.getGradeMap().get(letterGrade);
				
				if (oldValue != null) {
					BigDecimal bgOldValue = BigDecimal.valueOf(oldValue.doubleValue()).setScale(AppConstants.DISPLAY_SCALE, GradeCalculations.MATH_CONTEXT.getRoundingMode());
					
					if (bgOldValue.compareTo(BigDecimal.ZERO) == 0) {
						throw new InvalidInputException(i18n.getString("cannotModifyBase"));
					} 
				}
				
				// If the one above is not bigger than the one below then throw an exception
				if (bigOldUpperScale.compareTo(bigValue) <= 0) {
					StringBuilder sb = new StringBuilder();
					Formatter formatter = new Formatter(sb);
					formatter.format(i18n.getString("valueMustBeLessThanAbove"), 
							   bigValue.setScale(AppConstants.DISPLAY_SCALE, GradeCalculations.MATH_CONTEXT.getRoundingMode()).toString(),
							   bigOldUpperScale.setScale(AppConstants.DISPLAY_SCALE, GradeCalculations.MATH_CONTEXT.getRoundingMode()).toString());
					
					throw new InvalidInputException(sb.toString());
				}
				
				gradeMapping.getGradeMap().put(letterGrade, Double.valueOf(v));
				upperScale = Double.valueOf(v);
			} else {
				upperScale = gradeMapping.getGradeMap().get(letterGrade);
				
				if (upperScale != null) {
									
					BigDecimal bigUpperScale = BigDecimal.valueOf(upperScale.doubleValue());
					if (bigOldUpperScale.compareTo(BigDecimal.ZERO) != 0 
							&& bigOldUpperScale.compareTo(bigUpperScale) <= 0) {
						StringBuilder sb = new StringBuilder();
						Formatter formatter = new Formatter(sb);
						formatter.format(i18n.getString("valueMustBeGreaterThanBelow"), 
								bigOldUpperScale.setScale(AppConstants.DISPLAY_SCALE, GradeCalculations.MATH_CONTEXT.getRoundingMode()).toString(),
								bigUpperScale.setScale(2, RoundingMode.HALF_EVEN).toString());
						throw new InvalidInputException(sb.toString());
					}					
				}
			}
		}

		gbService.updateGradebook(gradebook);
	}
	
	public Map<String, Object> updateItem(Map<String, Object> attributes) throws InvalidInputException {
		
		ItemModel result = updateItemModel(toItem(attributes));
		
		Map<String, Object> itemMap = new HashMap<String, Object>();
		
		for (Enum<ItemKey> it : EnumSet.allOf(ItemKey.class)) {
			itemMap.put(it.name(), result.get(it.name()));
		}
		
		addChildren(result, itemMap);
	
		return itemMap;
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
	
	private Map<String, Object> buildGradeEvent(GradingEvent event) {
		SimpleDateFormat dateFormat = new SimpleDateFormat();

		String graderName = event.getGraderId();

		try {
			if (userService != null) {
				User grader = userService.getUser(event.getGraderId());
				if (grader != null)
					graderName = grader.getDisplayName();
			}
		} catch (UserNotDefinedException e) {
			log.info("Failed to find a user for the id " + event.getGraderId());
		}

		Map<String, Object> map = new HashMap<String, Object>();		
		map.put(GradeEventKey.ID.name(), String.valueOf(event.getId()));
		map.put(GradeEventKey.GRADER_NAME.name(), graderName);
		map.put(GradeEventKey.GRADE.name(), event.getGrade());
		map.put(GradeEventKey.DATE_GRADED.name(), dateFormat.format(event.getDateGraded()));
	
		return map;
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
	
	private ItemModel toItem(Map<String,Object> attributes) {
		ItemModel item = new ItemModel();
		for (ItemKey key : EnumSet.allOf(ItemKey.class)) {
			if (key.getType() != null) {
				try {
					Object rawValue = attributes.get(key.name());
					Object value = rawValue;
					
					if (rawValue != null) {
						if (key.getType().equals(Long.class)) 
							value = Long.valueOf(rawValue.toString());
						else if (key.getType().equals(Double.class))
							value = Double.valueOf(rawValue.toString());
						else if (key.getType().equals(Date.class))
							value = new Date((Long)rawValue);
					}
					
					item.set(key.name(), value);
				} catch (ClassCastException cce) {
					log.info("Unable to cast value for " + key.name() + " as " + key.getType().getCanonicalName());
				}
			} else 
				item.set(key.name(), attributes.get(key.name()));
		}
		return item;
	}
	
	private Permission toPermission(Long gradebookId, Map<String, Object> attributes) {
		
		Object permissionIdObj = attributes.get(PermissionKey.ID.name());
		Object categoryIdObj = attributes.get(PermissionKey.CATEGORY_ID.name());
		Object gradebookIdObject = attributes.get(PermissionKey.GRADEBOOK_ID.name());
		
		if (permissionIdObj instanceof Integer)
			permissionIdObj = permissionIdObj == null ? null : Long.valueOf(((Integer) permissionIdObj).longValue());
		if (categoryIdObj instanceof Integer)
			categoryIdObj = categoryIdObj == null ? null : Long.valueOf(((Integer) categoryIdObj).longValue());
		if (gradebookIdObject instanceof Integer)
			gradebookIdObject = categoryIdObj == null ? null : Long.valueOf(((Integer) gradebookIdObject).longValue());
		
		Long permissionId = (Long)permissionIdObj;
		Long categoryId = (Long)categoryIdObj;
		String function = (String)attributes.get(PermissionKey.PERMISSION_ID.name());
		String userId = (String)attributes.get(PermissionKey.USER_ID.name());
		String groupId = (String)attributes.get(PermissionKey.SECTION_ID.name());
		
		if (gradebookId == null)
			gradebookId = (Long)gradebookIdObject;
		
		Permission permission = new Permission();
		permission.setId(permissionId);
		permission.setGradebookId(gradebookId);
		permission.setCategoryId(categoryId);
		permission.setFunction(function);
		permission.setUserId(userId);
		permission.setGroupId(groupId);
	
		return permission;
	}
	
}
