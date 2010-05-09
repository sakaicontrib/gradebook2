package org.sakaiproject.gradebook.gwt.sakai;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.ConfigUtil;
import org.sakaiproject.gradebook.gwt.client.exceptions.BusinessRuleException;
import org.sakaiproject.gradebook.gwt.client.exceptions.GradebookCreationException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.exceptions.SecurityException;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationSetup;
import org.sakaiproject.gradebook.gwt.client.model.AuthModel;
import org.sakaiproject.gradebook.gwt.client.model.Configuration;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumn;
import org.sakaiproject.gradebook.gwt.client.model.GradeEvent;
import org.sakaiproject.gradebook.gwt.client.model.History;
import org.sakaiproject.gradebook.gwt.client.model.HistoryRecord;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.client.model.Roster;
import org.sakaiproject.gradebook.gwt.client.model.Statistics;
import org.sakaiproject.gradebook.gwt.client.model.Upload;
import org.sakaiproject.gradebook.gwt.client.model.key.ActionKey;
import org.sakaiproject.gradebook.gwt.client.model.key.GradeFormatKey;
import org.sakaiproject.gradebook.gwt.client.model.key.GradeMapKey;
import org.sakaiproject.gradebook.gwt.client.model.key.GraderKey;
import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;
import org.sakaiproject.gradebook.gwt.client.model.key.SectionKey;
import org.sakaiproject.gradebook.gwt.client.model.key.VerificationKey;
import org.sakaiproject.gradebook.gwt.client.model.type.ActionType;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.EntityType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.type.ItemType;
import org.sakaiproject.gradebook.gwt.sakai.InstitutionalAdvisor.Column;
import org.sakaiproject.gradebook.gwt.sakai.model.ActionRecord;
import org.sakaiproject.gradebook.gwt.sakai.model.GradeItem;
import org.sakaiproject.gradebook.gwt.sakai.model.GradeStatistics;
import org.sakaiproject.gradebook.gwt.sakai.model.StudentScore;
import org.sakaiproject.gradebook.gwt.sakai.model.UserConfiguration;
import org.sakaiproject.gradebook.gwt.sakai.model.UserDereference;
import org.sakaiproject.gradebook.gwt.sakai.model.UserDereferenceRealmUpdate;
import org.sakaiproject.gradebook.gwt.server.Util;
import org.sakaiproject.gradebook.gwt.server.model.ApplicationSetupImpl;
import org.sakaiproject.gradebook.gwt.server.model.ConfigurationImpl;
import org.sakaiproject.gradebook.gwt.server.model.FixedColumnImpl;
import org.sakaiproject.gradebook.gwt.server.model.GradeEventImpl;
import org.sakaiproject.gradebook.gwt.server.model.GradeItemImpl;
import org.sakaiproject.gradebook.gwt.server.model.GradebookImpl;
import org.sakaiproject.gradebook.gwt.server.model.HistoryImpl;
import org.sakaiproject.gradebook.gwt.server.model.HistoryRecordImpl;
import org.sakaiproject.gradebook.gwt.server.model.LearnerImpl;
import org.sakaiproject.gradebook.gwt.server.model.PermissionImpl;
import org.sakaiproject.gradebook.gwt.server.model.RosterImpl;
import org.sakaiproject.gradebook.gwt.server.model.StatisticsImpl;
import org.sakaiproject.section.api.SectionAwareness;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.section.api.coursemanagement.EnrollmentRecord;
import org.sakaiproject.section.api.coursemanagement.ParticipationRecord;
import org.sakaiproject.section.api.facade.Role;
import org.sakaiproject.service.gradebook.shared.GradebookFrameworkService;
import org.sakaiproject.service.gradebook.shared.GradebookNotFoundException;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
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
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class Gradebook2ComponentServiceImpl implements Gradebook2ComponentService, ApplicationContextAware {

	private static ResourceBundle i18n = ResourceBundle.getBundle("org.sakaiproject.gradebook.gwt.client.I18nConstants");
	private static final Log log = LogFactory.getLog(Gradebook2ComponentServiceImpl.class);
	
	private static final String NA = "-";
	private static final String UNIQUESET = "N/A";
	private static enum FunctionalityStatus { OFF, ADMIN_ONLY, INSTRUCTOR_ONLY, GRADER_ONLY, STUDENT_ONLY };
	
	static final Comparator<UserRecord> DEFAULT_ID_COMPARATOR = new Comparator<UserRecord>() {

		public int compare(UserRecord o1, UserRecord o2) {

			if (o1.getUserUid() == null || o2.getUserUid() == null)
				return 0;

			return o1.getUserUid().compareToIgnoreCase(o2.getUserUid());
		}
	};
	static final Comparator<UserRecord> DISPLAY_ID_COMPARATOR = new Comparator<UserRecord>() {

		public int compare(UserRecord o1, UserRecord o2) {

			if (o1.getDisplayId() == null || o2.getDisplayId() == null)
				return 0;

			return o1.getDisplayId().compareToIgnoreCase(o2.getDisplayId());
		}
	};
	static final Comparator<UserRecord> EMAIL_COMPARATOR = new Comparator<UserRecord>() {

		public int compare(UserRecord o1, UserRecord o2) {

			if (o1.getEmail() == null || o2.getEmail() == null)
				return 0;

			return o1.getEmail().compareToIgnoreCase(o2.getEmail());
		}
	};
	static final Comparator<EnrollmentRecord> ENROLLMENT_NAME_COMPARATOR = new Comparator<EnrollmentRecord>() {

		public int compare(EnrollmentRecord o1, EnrollmentRecord o2) {

			return o1.getUser().getSortName().compareToIgnoreCase(o2.getUser().getSortName());
		}
	};
	
	/**
	 * COMPARATORS
	 */
	// Code taken from
	// "org.sakaiproject.service.gradebook.shared.GradebookService.lettergradeComparator"
	static final Comparator<String> LETTER_GRADE_COMPARATOR = new Comparator<String>() {

		public int compare(String o1, String o2) {

			if (o1.toLowerCase().charAt(0) == o2.toLowerCase().charAt(0)) {

				if (o1.length() == 2 && o2.length() == 2) {

					if (o1.charAt(1) == '+')
						return 0;
					else
						return 1;

				}

				if (o1.length() == 1 && o2.length() == 2) {

					if (o2.charAt(1) == '+')
						return 1;
					else
						return 0;
				}

				if (o1.length() == 2 && o2.length() == 1) {

					if (o1.charAt(1) == '+')
						return 0;
					else
						return 1;
				}

				return 0;

			} else {

				return o1.toLowerCase().compareTo(o2.toLowerCase());
			}
		}
	};


	static final String[] orderedPassNoPassGrades = { "P", "NP", "S", "U", "IP", "I", "Y", "NS", "NG" };
	static final Comparator<String> PASS_NOPASS_COMPARATOR = new Comparator<String>() {

		public int compare(String o1, String o2) {

			if (o1 == null || o2 == null)
				return 0;

			for (int i=0;i<orderedPassNoPassGrades.length;i++) {
				if (o1.equals(orderedPassNoPassGrades[i]))
					return -1;
				if (o2.equals(orderedPassNoPassGrades[i]))
					return 1;
			}


			return 0;
		}

	};
	static final Comparator<UserRecord> SECTION_TITLE_COMPARATOR = new Comparator<UserRecord>() {

		public int compare(UserRecord o1, UserRecord o2) {

			if (o1.getSectionTitle() == null || o2.getSectionTitle() == null)
				return 0;

			return o1.getSectionTitle().compareToIgnoreCase(o2.getSectionTitle());
		}
	};
	static final Comparator<UserRecord> SORT_NAME_COMPARATOR = new Comparator<UserRecord>() {

		public int compare(UserRecord o1, UserRecord o2) {

			if (o1.getSortName() == null || o2.getSortName() == null)
				return 0;

			return o1.getSortName().compareToIgnoreCase(o2.getSortName());
		}
	};
	
	private InstitutionalAdvisor advisor;
	private ApplicationContext applicationContext;
	private Gradebook2Authz authz;
	private BusinessLogic businessLogic;
	private ServerConfigurationService configService;
	private List<GradeType> enabledGradeTypes;
	private EventTrackingService eventTrackingService;
	private GradebookFrameworkService frameworkService;
	private GradebookToolService gbService;
	private GradeCalculations gradeCalculations;
	private String helpUrl;	
	private String[] learnerRoleNames;
	private FunctionalityStatus stateOfScaledExtraCredit;
	private CategoryType limitScaledExtraCreditToCategoryType;
	private SectionAwareness sectionAwareness;	
	private SessionManager sessionManager;	
	private SiteService siteService;
	private ToolManager toolManager;	
	private UserDirectoryService userService;
	private boolean isOldImport = false;
	
	
	public Learner assignComment(String itemId, String studentUid, String text) {

		//int indexOf = itemId.indexOf(AppConstants.COMMENT_TEXT_FLAG);
		Long assignmentId = Long.valueOf(Util.unpackItemIdFromKey(itemId)); //Long.valueOf(itemId.substring(0, indexOf));
		Comment comment = doAssignComment(assignmentId, studentUid, text);
		Gradebook gradebook = null;
		
		if (comment != null)
			gradebook = doCommitComment(comment, studentUid, text);
		else {
			Assignment assignment = gbService.getAssignment(assignmentId);
			gradebook = assignment.getGradebook();
		}
			
		Site site = getSite();
		User user = null;
		try {
			user = userService.getUser(studentUid);
		} catch (UserNotDefinedException unde) {
			log.warn("User not defined: " + studentUid);
		}
		return getStudent(gradebook, site, user);
	}
	
	private Comment doAssignComment(Long assignmentId, String studentUid, String text) {
		Comment comment = gbService.getCommentForItemForStudent(assignmentId, studentUid);

		Assignment assignment = null;
		if (comment == null) {
			// We don't need to create a comment object if the user is just passing up a blank
			// comment
			if (text == null || text.equals(""))
				return null;
			
			assignment = gbService.getAssignment(assignmentId);
			comment = new Comment(studentUid, text, assignment);
		} else {
			if ((comment.getCommentText() == null && text == null) 
					|| (comment.getCommentText() != null && text != null && text.equals(comment.getCommentText()))) 
				return null;
			
			assignment = (Assignment)comment.getGradableObject();
			comment.setCommentText(text);
		}
		
		return comment;
	}
	
	private Comment doAssignComment(Assignment assignment, String studentUid, String text) {
		Comment comment = gbService.getCommentForItemForStudent(assignment.getId(), studentUid);

		if (comment == null) {
			// We don't need to create a comment object if the user is just passing up a blank
			// comment
			if (text == null || text.equals(""))
				return null;
			
			comment = new Comment(studentUid, text, assignment);
		} else {
			if ((comment.getCommentText() == null && text == null) 
					|| (comment.getCommentText() != null && text != null && text.equals(comment.getCommentText()))) 
				return null;
			
			comment.setCommentText(text);
		}
		
		return comment;
	}
	
	
	
	private Gradebook doCommitComment(Comment comment, String studentUid, String text) {
		
		if (comment == null)
			return null;
		
		String actionType = ActionType.CREATE.name();
		if (comment.getDateRecorded() != null)
			actionType = ActionType.UPDATE.name();
		
		Assignment assignment = (Assignment)comment.getGradableObject();
		Gradebook gradebook = assignment.getGradebook();
		ActionRecord actionRecord = new ActionRecord(gradebook.getUid(), gradebook.getId(), EntityType.COMMENT.name(), actionType);
		actionRecord.setEntityName(assignment.getName());
		Map<String, String> propertyMap = actionRecord.getPropertyMap();
		
		if (text != null && text.length() > 255)
		{
			propertyMap.put("comment", "Comment text is too long and is truncated.  Here are the first 100 characters: " + text.substring(0, 100));			
		}
		else
		{
			propertyMap.put("comment", text);
		}
		
		try {
			gbService.updateComment(comment);
			postEvent("gradebook2.comment", String.valueOf(gradebook.getId()), String.valueOf(assignment.getId()), studentUid);
		} catch (RuntimeException e) {
			actionRecord.setStatus(ActionRecord.STATUS_FAILURE);
			throw e;
		} finally {
			gbService.storeActionRecord(actionRecord);
		}
		
		return gradebook;
	}
	
	public Learner assignExcused(String itemId, String studentUid, Boolean isExcludedFromGrade) throws InvalidInputException {
		//int indexOf = itemId.indexOf(AppConstants.EXCUSE_FLAG);
		Long assignmentId = Long.valueOf(Util.unpackItemIdFromKey(itemId));
		Assignment assignment = gbService.getAssignment(assignmentId);
		
		if (assignment == null)
			throw new InvalidInputException("This is not a valid item.");
		
		Gradebook gradebook = assignment.getGradebook();
		boolean isUserAbleToGrade = authz.isUserAbleToGradeAll(gradebook.getUid()) || authz.isUserAbleToGradeItemForStudent(gradebook.getUid(), assignment.getId(), studentUid);

		if (!isUserAbleToGrade)
			throw new InvalidInputException("You are not authorized to grade this student for this item.");
		
		
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
		
		// Set the record as "excused"
		assignmentGradeRecord.setExcludedFromGrade(isExcludedFromGrade);
		
		// Prepare record for update
		assignmentGradeRecord.setGradableObject(assignment);
		assignmentGradeRecord.setStudentId(studentUid);

		Collection<AssignmentGradeRecord> updateGradeRecords = new LinkedList<AssignmentGradeRecord>();
		updateGradeRecords.add(assignmentGradeRecord);
		gbService.updateAssignmentGradeRecords(assignment, updateGradeRecords);
		postEvent("gradebook2.excuseGrade", String.valueOf(gradebook.getId()), String.valueOf(assignment.getId()), studentUid);
		
		Site site = getSite();
		User user = null;
		try {
			user = userService.getUser(studentUid);
		} catch (UserNotDefinedException unde) {
			log.warn("User not defined: " + studentUid);
		}
		Learner student = getStudent(gradebook, site, user);

		ActionRecord actionRecord = new ActionRecord(gradebook.getUid(), gradebook.getId(), EntityType.GRADE_RECORD.name(), ActionType.GRADED.name());
		actionRecord.setEntityId(String.valueOf(assignment.getId()));
		actionRecord.setStudentUid(studentUid);
		Map<String, String> propertyMap = actionRecord.getPropertyMap();

		propertyMap.put("score", "Excused");
		
		populatePropertiesFromLearner(propertyMap, student);
		
		actionRecord.setEntityName(new StringBuilder().append((String)student.get(LearnerKey.S_DSPLY_NM.name())).append(" : ").append(assignment.getName()).toString());
		gbService.storeActionRecord(actionRecord);

		return student;
	}
	
	private void populatePropertiesFromLearner(Map<String, String> propertyMap,
			Learner student) {
		
		for (String propertyName : student.getProperties().keySet()) { 
			Object value = student.get(propertyName);
			if (value != null) {
				String stringValue = String.valueOf(value);
				
				if (stringValue.length() >= 756) {
					log.warn("Trying to store a property with a value that's too long for the field, truncating the property " + propertyName + " : " + stringValue);
					stringValue = stringValue.substring(0, 755);
				}
				propertyMap.put(propertyName, stringValue);
			}
		}
		
	}

	public Learner assignScore(String gradebookUid, String studentUid, String assignmentId, Double value, Double previousValue) throws InvalidInputException {
		Assignment assignment = gbService.getAssignment(Long.valueOf(assignmentId));
		Gradebook gradebook = assignment.getGradebook();

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

		scoreItem(gradebook, gradebook.getGrade_type(), assignment, assignmentGradeRecord, studentUid, value, false, false);

		gradeRecords = gbService.getAssignmentGradeRecordsForStudent(gradebook.getId(), studentUid);

		Site site = getSite();
		User user = null;
		try {
			user = userService.getUser(studentUid);
		} catch (UserNotDefinedException unde) {
			log.warn("User not defined: " + studentUid);
		}
		Learner student = getStudent(gradebook, site, user);
		
		ActionRecord actionRecord = new ActionRecord(gradebook.getUid(), gradebook.getId(), EntityType.GRADE_RECORD.name(), ActionType.GRADED.name());
		actionRecord.setEntityId(String.valueOf(assignment.getId()));
		actionRecord.setStudentUid(studentUid);
		Map<String, String> propertyMap = actionRecord.getPropertyMap();

		propertyMap.put("score", String.valueOf(value));
		
		populatePropertiesFromLearner(propertyMap, student);
		
		actionRecord.setEntityName(new StringBuilder().append((String)student.get(LearnerKey.S_DSPLY_NM.name())).append(" : ").append(assignment.getName()).toString());
		gbService.storeActionRecord(actionRecord);

		return student;
	}
	
	public Learner assignScore(String gradebookUid, String studentUid, String property, String value, String previousValue) throws InvalidInputException {
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
		
		Learner student = null;
		
		if (property.equals(LearnerKey.S_OVRD_GRD.name())) {
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
	
			
			Site site = getSite();

			student = getStudent(gradebook, site, user);
			
			ActionRecord actionRecord = new ActionRecord(gradebook.getUid(), gradebook.getId(), EntityType.COURSE_GRADE_RECORD.name(), ActionType.GRADED.name());
			actionRecord.setEntityId(String.valueOf(gradebook.getId()));
			actionRecord.setStudentUid(studentUid);
			Map<String, String> propertyMap = actionRecord.getPropertyMap();

			propertyMap.put("score", value);
			
			populatePropertiesFromLearner(propertyMap, student);
			
			actionRecord.setEntityName(new StringBuilder().append((String)student.get(LearnerKey.S_DSPLY_NM.name())).append(" : ").append(gradebook.getName()).toString());
			gbService.storeActionRecord(actionRecord);

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
	
	public Item createItem(String gradebookUid, Long gradebookId, Item item, boolean enforceNoNewCategories) throws InvalidInputException {

		if (!authz.isUserAbleToEditAssessments(gradebookUid))
			throw new InvalidInputException("You are not authorized to create items.");
		
		ItemType itemType = item.getItemType();

		if (itemType != null) {
			switch (itemType) {
				case CATEGORY:
					return createItemCategory(gradebookUid, gradebookId, item);
			}
		}

		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		boolean hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;

		Long assignmentId = doCreateItem(gradebook, item, hasCategories, enforceNoNewCategories);

		postEvent("gradebook2.newItem", String.valueOf(gradebook.getId()), String.valueOf(assignmentId));
		
		List<Assignment> assignments = gbService.getAssignments(gradebookId);
		List<Category> categories = null;
		
		if (hasCategories)
			categories = getCategoriesWithAssignments(gradebookId, assignments, true);
		
		return getGradeItem(gradebook, assignments, categories, null, assignmentId);
		//return new GradeItemImpl(getItemMap(gradebook, assignments, categories, null, assignmentId));
	}
	
	public Item createItemCategory(String gradebookUid, Long gradebookId, Item item) throws BusinessRuleException {
		String name = item.getName();
		
		// Category category = null;
		Gradebook gradebook = null;
		List<Assignment> assignments = null;
		List<Category> categories = null;
		Long categoryId = null;

		String actionRecordStatus = ActionRecord.STATUS_SUCCESS;
		
		try {
			Double weight = item.getPercentCourseGrade(); //(Double)attributes.get(ItemKey.PERCENT_COURSE_GRADE.name());
			Boolean isEqualWeighting = item.getEqualWeightAssignments(); //(Boolean)attributes.get(ItemKey.EQUAL_WEIGHT.name());
			Boolean isIncluded = item.getIncluded(); //(Boolean)attributes.get(ItemKey.INCLUDED.name());
			Integer dropLowest = item.getDropLowest(); //(Integer)attributes.get(ItemKey.DROP_LOWEST.name());
			Boolean isExtraCredit = item.getExtraCredit(); //(Boolean)attributes.get(ItemKey.EXTRA_CREDIT.name());
			Integer categoryOrder = item.getItemOrder(); //(Integer)attributes.get(ItemKey.ITEM_ORDER.name());
			Boolean doEnforcePointWeighting = item.getEnforcePointWeighting(); //(Boolean)attributes.get(ItemKey.ENFORCE_POINT_WEIGHTING.name());

			boolean isUnweighted = !Util.checkBoolean(isIncluded);

			gradebook = gbService.getGradebook(gradebookUid);

			boolean hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;
			boolean hasWeights = gradebook.getCategory_type() == GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY;

			if (hasCategories) {
				categories = gbService.getCategories(gradebook.getId()); // getCategoriesWithAssignments(gradebook.getId());
				if (categoryOrder == null)
					categoryOrder = categories == null || categories.isEmpty() ? Integer.valueOf(0) : Integer.valueOf(categories.size());
			}

			double w = weight == null ? 0d : ((Double)weight).doubleValue() * 0.01;

			if (hasCategories) {
				int dropLowestInt = dropLowest == null ? 0 : dropLowest.intValue();
				boolean equalWeighting = isEqualWeighting == null ? false : isEqualWeighting.booleanValue();

				businessLogic.applyNoDuplicateCategoryNamesRule(gradebook.getId(), name, null, categories);
				if (hasWeights)
					businessLogic.applyOnlyEqualWeightDropLowestRule(dropLowestInt, equalWeighting);
			}

			categoryId = gbService.createCategory(gradebookId, name, Double.valueOf(w), dropLowest, isEqualWeighting, Boolean.valueOf(isUnweighted), isExtraCredit, categoryOrder, doEnforcePointWeighting);

			postEvent("gradebook2.newCategory", String.valueOf(gradebook.getId()), String.valueOf(categoryId));
			
			assignments = gbService.getAssignments(gradebook.getId());
			categories = null;
			if (hasCategories) {
				categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);
			}

		} catch (RuntimeException e) {
			actionRecordStatus = ActionRecord.STATUS_FAILURE;
			throw e;
		} finally {
			String id = categoryId == null ? null : String.valueOf(categoryId);
			ActionRecord actionRecord = createActionRecord(name, id, gradebook, item, EntityType.CATEGORY.name(), ActionType.CREATE.name());
			actionRecord.setStatus(actionRecordStatus);
			gbService.storeActionRecord(actionRecord);
		}

		return getGradeItem(gradebook, assignments, categories, categoryId, null);
	}
	
	public org.sakaiproject.gradebook.gwt.client.model.Permission createPermission(String gradebookUid, Long gradebookId, org.sakaiproject.gradebook.gwt.client.model.Permission permissionRequest) throws SecurityException, InvalidInputException {
		
		if (!authz.isUserAbleToEditAssessments(gradebookUid))
			throw new SecurityException("You are not authorized to create permissions.");
		
		Permission newPermission = toPermission(gradebookId, permissionRequest);
		// First, we want to verify that the permission does not already exist
		List<Permission> permissions = gbService.getPermissionsForUser(gradebookId, newPermission.getUserId());

		
		Long newCategoryId = newPermission.getCategoryId();
		String newGroupId = newPermission.getGroupId();
		
		if (newCategoryId != null && newCategoryId.longValue() == -1l) {
			newCategoryId = null;
			newPermission.setCategoryId(null);
		}
		
		if (newGroupId != null && newGroupId.equalsIgnoreCase("ALL")) {
			newGroupId = null;
			newPermission.setGroupId(null);
		}
		
		// We can ignore this check is the grader has no permissions yet
		if (permissions != null && !permissions.isEmpty()) {
			
			for (Permission permission : permissions) {
				if (permission.getFunction().equals(newPermission.getFunction())) {
					// The new permission has the same function as an existing permission
					
					Long categoryId = permission.getCategoryId();
					
					String groupId = permission.getGroupId();
					
	
					if (groupId != null && groupId.equalsIgnoreCase("ALL"))
						groupId = null;
					
					if (categoryId != null && categoryId.longValue() == -1l) {
						categoryId = null;
						permission.setCategoryId(null);
					}
						
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
		permissionRequest.setId(id);
		return permissionRequest;
	}
	
	public org.sakaiproject.gradebook.gwt.client.model.Permission deletePermission(String gradebookUid, org.sakaiproject.gradebook.gwt.client.model.Permission permissionDeleteRequest) 
	throws SecurityException {
		
		if (!authz.isUserAbleToEditAssessments(gradebookUid))
			throw new SecurityException("You are not authorized to delete permissions.");
		
		Permission p = toPermission(null, permissionDeleteRequest);
		gbService.deletePermission(p.getId());
		return permissionDeleteRequest;
	}
	
	public List<UserDereference> findAllUserDereferences() {

		Site site = getSite();
		String siteId = site == null ? null : site.getId();

		String[] learnerRoleNames = getLearnerRoleNames();
		verifyUserDataIsUpToDate(site, learnerRoleNames);

		String[] realmIds = null;
		if (siteId == null) {
			if (log.isInfoEnabled())
				log.info("No siteId defined");
			return new ArrayList<UserDereference>();
		}

		realmIds = new String[1];
		realmIds[0] = new StringBuffer().append("/site/").append(siteId).toString();

		List<UserDereference> dereferences = gbService.getUserDereferences(realmIds, "sortName", null, null, -1, -1, true, learnerRoleNames);

		return dereferences;
	}
	
	public InstitutionalAdvisor getAdvisor() {

		return advisor;
	}
	
	public ApplicationSetup getApplicationSetup(String... gradebookUids) 
	throws GradebookCreationException {

		ApplicationSetup setup = new ApplicationSetupImpl();
		setup.setGradebookModels(getGradebookModels(gradebookUids));
		setup.setHelpUrl(helpUrl);
		setup.setEnabledGradeTypes(enabledGradeTypes);
		
		return setup;
	}
	
	public String getAuthorizationDetails(String... gradebookUids) {
		AuthModel authModel = getAuthorization(gradebookUids);
		return authModel.toString();
	}
	
	public List<Map<String,Object>> getAvailableGradeFormats(String gradebookUid, Long gradebookId) {

		List<Map<String,Object>> models = new ArrayList<Map<String,Object>>();

		Set<GradeMapping> gradeMappings = gbService.getGradeMappings(gradebookId);

		for (GradeMapping mapping : gradeMappings) {
			Map<String,Object> model = new HashMap<String,Object>();
			model.put(GradeFormatKey.L_ID.name(), mapping.getId());
			model.put(GradeFormatKey.S_NM.name(), mapping.getName());
			models.add(model);
		}

		return models;
	}
	
	public BusinessLogic getBusinessLogic() {

		return businessLogic;
	}
		
	public List<Category> getCategoriesWithAssignments(Long gradebookId) {

		List<Category> categories = gbService.getCategories(gradebookId);
		List<Category> categoriesWithAssignments = new ArrayList<Category>();
		if (categories != null) {
			for (Category category : categories) {

				if (category != null) {
					List<Assignment> assignments = gbService.getAssignmentsForCategory(category.getId());
					category.setAssignmentList(assignments);
					categoriesWithAssignments.add(category);
				}
			}
		}

		return categoriesWithAssignments;
	}
	
	public ServerConfigurationService getConfigService() {

		return configService;
	}
	
	public String getCurrentSession() {

		if (null == sessionManager) {
			return null;
		}

		Session session = sessionManager.getCurrentSession();

		if (null == session) {
			return null;
		}

		return session.getId();
	}
	
	public String getCurrentUser() {

		if(null == sessionManager) {
			return "0";
		}

		return sessionManager.getCurrentSessionUserId();
	}
	
	public EventTrackingService getEventTrackingService() {
		return eventTrackingService;
	}
	
	public List<String> getExportCourseManagementSetEids(Group group) {

		return advisor.getExportCourseManagementSetEids(group);
	}

	public String getExportCourseManagementId(String userEid, Group group, List<String> enrollmentSetEids) {

		return advisor.getExportCourseManagementId(userEid, group, enrollmentSetEids);
	}

	public String getExportUserId(UserDereference dereference) {

		return advisor.getExportUserId(dereference);
	}

	public String getFinalGradeUserId(UserDereference dereference) {

		return advisor.getFinalGradeUserId(dereference);
	}
	
	public GradebookFrameworkService getFrameworkService() {

		return frameworkService;
	}
	
	public GradebookToolService getGbService() {

		return gbService;
	}
	
	public org.sakaiproject.gradebook.gwt.client.model.Gradebook getGradebook(String uid) {

		Gradebook gradebook = gbService.getGradebook(uid);
		List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
		List<Category> categories = null;
		if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY)
			categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);
		return createGradebookModel(gradebook, assignments, categories, false);
	}
		
	public GradeCalculations getGradeCalculations() {

		return gradeCalculations;
	}
	
	public List<GradeEvent> getGradeEvents(Long assignmentId, String studentUid) {
		List<GradeEvent> gradeEvents = new ArrayList<GradeEvent>();
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
	
	public List<Map<String,Object>> getGradeMaps(String gradebookUid) throws SecurityException {
		
		if (!authz.isUserAbleToEditAssessments(gradebookUid))
			throw new SecurityException("You are not authorized to view grade mappings.");
		
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
			gradeScaleModel.put(GradeMapKey.S_ID.name(), letterGrade);
			gradeScaleModel.put(GradeMapKey.S_LTR_GRD.name(), letterGrade);
			gradeScaleModel.put(GradeMapKey.D_FROM.name(), gradeMapping.getGradeMap().get(letterGrade));
			gradeScaleModel.put(GradeMapKey.D_TO.name(), upperScale);

			gradeScaleMappings.add(gradeScaleModel);
			upperScale = gradeMapping.getGradeMap().get(letterGrade);
		}
		return gradeScaleMappings;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getGraders(String gradebookUid, Long gradebookId) throws SecurityException {
		
		boolean isUserAbleToGrade = authz.isUserAbleToGradeAll(gradebookUid);

		if (!isUserAbleToGrade)
			throw new SecurityException("You are not authorized to view graders.");
		
		List<Map<String, Object>> userList = new ArrayList<Map<String, Object>>();

		String placementId = lookupDefaultGradebookUid();
		List<ParticipationRecord> participationList = sectionAwareness.getSiteMembersInRole(placementId, Role.TA);

		for (ParticipationRecord participationRecord : participationList) {
			org.sakaiproject.section.api.coursemanagement.User user = participationRecord.getUser();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(GraderKey.S_ID.name(), user.getUserUid());
			map.put(GraderKey.S_NM.name(), user.getDisplayName());
			userList.add(map);
		}
		
		return userList;
	}
	
	private enum WeightedCategoriesState { VALID, INVALID_PERCENT_GRADE, INVALID_PERCENT_CATEGORY };
	
	public Map<String,Object> getGradesVerification(String gradebookUid, Long gradebookId) throws SecurityException {
		
		boolean isUserAbleToGrade = authz.isUserAbleToGradeAll(gradebookUid);

		if (!isUserAbleToGrade)
			throw new SecurityException("You are not authorized to submit grades.");
		
		String[] roleNames = getLearnerRoleNames();
		Site site = getSite();
		String siteId = site == null ? null : site.getId();
		String[] realmIds = new String[1];
		realmIds[0] = new StringBuffer().append("/site/").append(siteId).toString();

		verifyUserDataIsUpToDate(site, roleNames);

		List<UserDereference> dereferences = gbService.getUserDereferences(realmIds, "sortName", null, null, -1, -1, true, roleNames);

		Gradebook gradebook = gbService.getGradebook(gradebookId);

		boolean hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;
		boolean isMissingScores = false;
		List<WeightedCategoriesState> state = isFullyWeighted(gradebook);
		
		if (dereferences != null) {
			for (UserDereference dereference : dereferences) {

				if (gbService.isStudentMissingScores(gradebookId, dereference.getUserUid(), hasCategories)) {
					isMissingScores = true;
					break;
				}
			}
		}

		int numberOfLearners = dereferences == null ? 0 : dereferences.size();

		Map<String,Object> map = new HashMap<String,Object>();
		map.put(VerificationKey.I_NUM_LRNRS.name(), Integer.valueOf(numberOfLearners));
		map.put(VerificationKey.B_MISS_SCRS.name(), Boolean.valueOf(isMissingScores));
		map.put(VerificationKey.B_GB_WGHTD.name(), Boolean.valueOf(!state.contains(WeightedCategoriesState.INVALID_PERCENT_GRADE)));
		map.put(VerificationKey.B_CTGRY_WGHTD.name(), Boolean.valueOf(!state.contains(WeightedCategoriesState.INVALID_PERCENT_CATEGORY)));
		
		return map;
	}
	
	public History getHistory(String gradebookUid, Long gradebookId,
			Integer offset, Integer limit) throws SecurityException {
		
		boolean isUserAbleToGrade = authz.isUserAbleToGradeAll(gradebookUid);

		if (!isUserAbleToGrade)
			throw new SecurityException("You are not authorized to view history.");
		
		int off = offset == null ? -1 : offset.intValue();
		int lim = limit == null ? -1 : limit.intValue();
		
		Integer size = gbService.getActionRecordSize(gradebookUid);
		List<ActionRecord> actionRecords = gbService.getActionRecords(gradebookUid, off, lim);
		List<HistoryRecord> models = new ArrayList<HistoryRecord>();

		String description = null;
		SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		format.setLenient(true);
		SimpleDateFormat toFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss ZZZ");
		
		for (ActionRecord actionRecord : actionRecords) {
			HistoryRecord actionModel = new HistoryRecordImpl();
			try {
				ActionType actionType = ActionType.valueOf(actionRecord.getActionType());
				EntityType entityType = EntityType.valueOf(actionRecord.getEntityType());
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
						actionModel.set("score", score);
						
						text.append(actionType.getVerb()).append(" '").append(score)
							.append("'");
	
						description = text.toString();
						break;
					case SUBMITTED:
						score = actionRecord.getPropertyMap().get(Column.LETTER_GRADE.name());
						if (score == null)
							score = "";
						actionModel.set(ActionKey.O_VALUE.name(), score);
						
						text.append(actionType.getVerb()).append(" '").append(score)
							.append("'");

						description = text.toString();
						break;
					case UPDATE:
						actionModel.set(ActionKey.O_VALUE.name(), actionRecord.getEntityName());
						description = actionType.getVerb();
						break;
				}

				if (actionModel == null)
					continue;
				
				Map<String, String> propertyMap = actionRecord.getPropertyMap();

				if (propertyMap != null) {
					for (String key : propertyMap.keySet()) {
						String value = propertyMap.get(key);

						// FIXME: Need to translate old keys to new keys here
						if (value != null && !value.equals("null")) {
							if (key.charAt(0) == AppConstants.DATE_PREFIX ||
									key.charAt(0) == AppConstants.ODD_DATE_PREFIX) {
								Date d = format.parse(value);
								value = toFormat.format(d);
							} 
							
							actionModel.set(key, value);
						}
					}
				}
				
				actionModel.set(ActionKey.S_ID.name(), String.valueOf(actionRecord.getId()));
				actionModel.set(ActionKey.S_GB_UID.name(),actionRecord.getGradebookUid());
				actionModel.set(ActionKey.L_GB_ID.name(),actionRecord.getGradebookId());
				actionModel.set(ActionKey.O_ENTY_TYPE.name(), entityType.name());
				if (actionRecord.getEntityId() != null)
					actionModel.set(ActionKey.S_ENTY_ID.name(),actionRecord.getEntityId());
				if (actionRecord.getEntityName() != null)
					actionModel.set(ActionKey.S_ENTY_NM.name(),actionRecord.getEntityName());
				if (actionRecord.getParentId() != null)
					actionModel.set(ActionKey.L_PRNT_ID.name(),Long.valueOf(actionRecord.getParentId()));
				
				String studentUid = actionRecord.getStudentUid();
				actionModel.set(ActionKey.S_LRNR_UID.name(), studentUid);
				
				if (actionRecord.getEntityName() != null && actionRecord.getEntityName().contains(" : ")) {
					String[] parts = actionRecord.getEntityName().split(" : ");
					
					actionModel.set(ActionKey.S_LRNR_NM.name(),parts[0]);
					actionModel.set(ActionKey.S_ENTY_NM.name(),parts[1]);
				}

				actionModel.set(ActionKey.S_GRDR_NM.name(), actionRecord.getGraderId());
				if (userService != null && actionRecord.getGraderId() != null) {

					try {
						User user = userService.getUser(actionRecord.getGraderId());
						actionModel.set(ActionKey.S_GRDR_NM.name(), user.getDisplayName());
					} catch (UserNotDefinedException e) {
						log.warn("Unable to find grader name for " + actionRecord.getGraderId(), e);
					}

				}

				if (actionRecord.getDatePerformed() != null) 
					actionModel.set(ActionKey.S_ACTION.name(), String.valueOf(actionRecord.getDatePerformed()));
				if (actionRecord.getDateRecorded() != null)
					actionModel.set(ActionKey.S_RECORD.name(), String.valueOf(actionRecord.getDateRecorded()));

				actionModel.set(ActionKey.S_DESC.name(), description);

				models.add(actionModel);
			} catch (Exception e) {
				log.warn("Failed to retrieve history record for " + actionRecord.getId(), e);
			}
		}
		return new HistoryImpl(models, size);
	}
	
	public Item getItem(String gradebookUid, Long gradebookId, String type) {
		GradeItem gradebookGradeItem = null;
		
		if (type == null) {
			Gradebook gradebook = gbService.getGradebook(gradebookUid);
			List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
			List<Category> categories = null;
			if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY)
				categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);
			
			gradebookGradeItem = getGradebookGradeItem(gradebook, assignments, categories, false);
		}
		
		return gradebookGradeItem;
	}
	
	public List<Item> getItems(String gradebookUid, Long gradebookId, String type) {
		List<Item> list = new ArrayList<Item>();
		
		if (type == null || type.equals(ItemKey.S_ITM_TYPE.name())) {
			List<Category> categoryList = gbService.getCategories(gradebookId);

			Item item = new GradeItemImpl();
			item.setIdentifier("ALL");
			item.setCategoryId(Long.valueOf(-1l));
			item.setName("All Categories");
			list.add(item);

			for (Category category : categoryList) {
				if (!category.isRemoved()) {
					item = new GradeItemImpl();
					item.setIdentifier(String.valueOf(category.getId()));
					item.setName(category.getName());
					item.setCategoryId(category.getId());
					item.setCategoryName(category.getName());
					list.add(item);
				}
			}
		}
		
		return list;
	}
	
	public List<org.sakaiproject.gradebook.gwt.client.model.Permission> getPermissions(String gradebookUid, Long gradebookId, String graderId) throws SecurityException {
		
		if (!authz.isUserAbleToEditAssessments(gradebookUid))
			throw new SecurityException("You are not authorized to view permissions.");
		
		
		List<org.sakaiproject.gradebook.gwt.client.model.Permission> list = 
			new ArrayList<org.sakaiproject.gradebook.gwt.client.model.Permission>();

		List<Permission> permissions = gbService.getPermissionsForUser(gradebookId, graderId);

		for (Permission permission : permissions) {

			org.sakaiproject.gradebook.gwt.client.model.Permission p = new PermissionImpl();
			p.setId(permission.getId());
			p.setUserId(permission.getUserId());

			try {
				User user = userService.getUser(permission.getUserId());

				if (null != user) {
					p.setUserDisplayName(user.getDisplayName());
				} else {
					log.error("Was not able go get an User object from userId = " + permission.getUserId());
				}
			} catch (UserNotDefinedException e) {
				log.error("Was not able go get an User object from userId = " + permission.getUserId());
				e.printStackTrace();
			}

			p.setPermissionId(permission.getFunction());

			// If category id is null, the all categories were selected
			if (null != permission.getCategoryId()) {

				p.setCategoryId(permission.getCategoryId());
				Category category = gbService.getCategory(permission.getCategoryId());
				if (null != category) {
					p.setCategoryDisplayName(category.getName());
				} else {
					// TODO: handle error
					log.error("Category is null for category id: " + permission.getCategoryId());
				}

			} else {
				p.setCategoryId(Long.valueOf(-1));
				p.setCategoryDisplayName("All");
			}

			// If section id is null, then all sections were selected
			if (null != permission.getGroupId() && !permission.getGroupId().equalsIgnoreCase("ALL")) {
				p.setSectionId(permission.getGroupId());
				CourseSection courseSection = sectionAwareness.getSection(permission.getGroupId());
				if (null != courseSection) {
					p.setSectionDisplayName(courseSection.getTitle());
				} else {
					// TODO: handle error
					log.error("CourseSection is null");
				}
			} else {
				p.setSectionId("ALL");
				p.setSectionDisplayName("All");
			}

			p.setDeleteAction("Delete");
			list.add(p);
		}

		return list;
	}
	
	
	/*

	 * PRIVATE METHODS
	 */

	@SuppressWarnings("unchecked")
	public Roster getRoster(String gradebookUid, Long gradebookId,
			Integer numberLimit, Integer numberOffset, String sectionUuid,
			String searchCriteria, String sortField, boolean includeCMId, boolean isDescending) {
		
		List<Learner> rows = new ArrayList<Learner>();

		String[] learnerRoleNames = getLearnerRoleNames();

		List<UserRecord> userRecords = null;

		Gradebook gradebook = null;
		if (gradebookId == null) {
			gradebook = gbService.getGradebook(gradebookUid);
			gradebookId = gradebook.getId();
		}

		List<Assignment> assignments = gbService.getAssignments(gradebookId);

		// Don't bother going out to the db for the Gradebook if we've already
		// retrieved it
		if (gradebook == null && assignments != null && assignments.size() > 0)
			gradebook = assignments.get(0).getGradebook();

		if (gradebook == null)
			gradebook = gbService.getGradebook(gradebookId);

		List<Category> categories = null;
		if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY)
			categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);

		String columnId = null;
		LearnerKey sortColumnKey = null;

		int offset = numberOffset == null ? -1 : numberOffset.intValue() ;
		int limit = numberLimit == null ? -1 : numberLimit.intValue();

		String searchField = "sortName";
		
		if (sortField != null) {
			columnId = sortField;
			
			try {
				sortColumnKey = LearnerKey.valueOf(columnId);
			} catch (IllegalArgumentException iae) {
				log.debug("This sort field is not a fixed column: " + sortField);
			}

			if (sortColumnKey == null)
				sortColumnKey = LearnerKey.S_ITEM;
		}

		if (searchCriteria != null) {
			searchCriteria = searchCriteria.toUpperCase();
			sortColumnKey = LearnerKey.S_DSPLY_NM;
		}

		if (sortColumnKey == null)
			sortColumnKey = LearnerKey.S_DSPLY_NM;

		int totalUsers = 0;
		Site site = getSite();
		String siteId = (site == null) ? null : site.getId();

		// Check if the user is a TA and assigned to a section via the Section
		// Info tool
		List<CourseSection> courseSections = sectionAwareness.getSections(getSiteContext());
		boolean isUserAssignedSectionTa = false;

		for (CourseSection courseSection : courseSections) {
			isUserAssignedSectionTa = authz.isUserTAinSection(courseSection.getUuid());
			if (isUserAssignedSectionTa) {
				break;
			}
		}
		// Is the user an Instructor or is the user a TA?
		boolean isInstructor = authz.isUserAbleToGradeAll(gradebook.getUid());

		// Is the user a TA and was he/she granted view/grade access via the
		// Grader Permission UI?
		boolean hasUserGraderPermissions = authz.hasUserGraderPermissions(gradebook.getUid());

		// If the user doesn't have any of the necessary credentials we return
		// an empty data set
		if (!isInstructor && !hasUserGraderPermissions && !isUserAssignedSectionTa) {
			return new RosterImpl(rows, Integer.valueOf(0));
		}

		// Was a section selected
		boolean isLimitedToSelectedSection = false;
		Set<String> authorizedGroups = new HashSet<String>();

		if (sectionUuid != null) {
			authorizedGroups.add(sectionUuid);
			isLimitedToSelectedSection = true;
		}

		// Get the site's groups
		Collection<Group> groups = (site == null) ? new ArrayList<Group>() : site.getGroups();
		Map<String, Group> groupReferenceMap = new HashMap<String, Group>();
		List<String> groupReferences = new ArrayList<String>();

		if (groups != null) {

			for (Group group : groups) {

				String reference = group.getReference();
				groupReferences.add(reference);
				groupReferenceMap.put(reference, group);

				if (!isLimitedToSelectedSection) {

					// GRBK-233 : In case the site has adhoc groups, we cannot check for providerId
					if (!isInstructor && (authz.isUserTAinSection(reference) || authz.hasUserGraderPermission(gradebook.getUid(), reference))) {

						authorizedGroups.add(reference);
					}
				}
			}
		}

		// Turn the groupId set into a String array
		String[] realmIds = null;
		if (!authorizedGroups.isEmpty())
			realmIds = authorizedGroups.toArray(new String[authorizedGroups.size()]);

		if (realmIds == null) {

			// If there are not groupIds we return an empty result set
			if (siteId == null) {
				if (log.isInfoEnabled())
					log.info("No siteId defined");
				return new RosterImpl(rows, Integer.valueOf(0));
			}

			realmIds = new String[1];
			realmIds[0] = new StringBuffer().append("/site/").append(siteId).toString();
		}

		// Check to see if we're sorting or not
		if (sortColumnKey != null) {
			switch (sortColumnKey) {
				case S_DSPLY_NM:
				case S_LST_NM_FRST:
				case S_DSPLY_ID:
				case S_EMAIL:
					sortField = "lastNameFirst";

					switch (sortColumnKey) {
						case S_DSPLY_ID:
							sortField = "displayId";
							break;
						case S_DSPLY_NM:
						case S_LST_NM_FRST:
							sortField = "sortName";
							break;
						case S_EMAIL:
							sortField = "email";
							break;
					}

					// FIXME : GRBK-233 : If a site has sections as well as adhoc groups users that are 
					// in both sections and adhoc groups show up twice
					userRecords = findLearnerRecordPage(gradebook, site, realmIds, groupReferences, groupReferenceMap, sortField, searchField, searchCriteria, offset, limit, !isDescending, includeCMId);
					totalUsers = gbService.getUserCountForSite(realmIds, sortField, searchField, searchCriteria, learnerRoleNames);

					List<FixedColumn> columns = getColumns(true);

					rows = new ArrayList<Learner>(userRecords == null ? 0 : userRecords.size());

					// We only want to populate the rowData and rowValues for
					// the requested rows
					for (UserRecord userRecord : userRecords) {
						rows.add(buildStudentRow(gradebook, userRecord, columns, assignments, categories));
					}

					return new RosterImpl(rows, Integer.valueOf(totalUsers));

				case S_SECT:
				case S_CRS_GRD:
				case S_OVRD_GRD:
				case S_LTR_GRD:
				case S_CALC_GRD:
				case S_ITEM:

					userRecords = findLearnerRecordPage(gradebook, site, realmIds, groupReferences, groupReferenceMap, null, searchField, searchCriteria, -1, -1, !isDescending, includeCMId);

					Map<String, UserRecord> userRecordMap = new HashMap<String, UserRecord>();

					for (UserRecord userRecord : userRecords) {
						userRecordMap.put(userRecord.getUserUid(), userRecord);
					}

					List<String> studentUids = new ArrayList<String>(userRecordMap.keySet());

					userRecords = doSearchAndSortUserRecords(gradebook, assignments, categories, studentUids, userRecordMap, searchCriteria, columnId, isDescending, sortColumnKey);
					totalUsers = userRecords.size();
					break;
			}
		}

		int startRow = offset == -1 ? 0 : offset;
		int lastRow = offset == -1 ? totalUsers : startRow + limit;

		if (lastRow > totalUsers) {
			lastRow = totalUsers;
		}

		List<FixedColumn> columns = getColumns(true);

		// We only want to populate the rowData and rowValues for the requested
		// rows
		for (int row = startRow; row < lastRow; row++) {
			// Everything is indexed by the user, since it's by user id that the
			// rows are distinguished
			UserRecord userRecord = userRecords.get(row);

			// Populate the user record on the fly if necessary
			if (!userRecord.isPopulated()) {
				User user = null;
				try {
					user = userService.getUser(userRecord.getUserUid());
					userRecord.setUserEid(user.getEid());
					userRecord.setDisplayId(user.getDisplayId());
					userRecord.setDisplayName(user.getDisplayName());
					userRecord.setLastNameFirst(user.getSortName());
					userRecord.setSortName(user.getSortName());
					userRecord.setEmail(user.getEmail());
				} catch (UserNotDefinedException e) {
					log.error("No sakai user defined for this member '" + userRecord.getUserUid() + "'", e);
				}
			}

			rows.add(buildStudentRow(gradebook, userRecord, columns, assignments, categories));
		}

		return new RosterImpl(rows, Integer.valueOf(totalUsers));
	}

	public SessionManager getSessionManager() {

		return sessionManager;
	}
	
	public SiteService getSiteService() {

		return siteService;
	}

	public int[] getGradeItemStatistics(Long assignmentId) throws SecurityException {
		
		int[] gradeFrequencies = new int[10];
		
		List<AssignmentGradeRecord> assignmentGradeRecords = gbService.getAllAssignmentGradeRecords(new Long[] {assignmentId});
		
		for(AssignmentGradeRecord assignmentGradeRecord : assignmentGradeRecords) {
			
			Double gradeAsPercentage = assignmentGradeRecord.getGradeAsPercentage();
			
			// In case we encounter an assignmentGradeRecords,
			// for which the grade has been deleted
			if(null == gradeAsPercentage) {
				continue;
			}
			
			// If the percentage grade is 100%, we subtract one so that we don't
			// get an index out of bound exception. 100% is part of the 90+ % category
			if(0 == gradeAsPercentage.compareTo(new Double(100))) {
				gradeAsPercentage = new Double(99);
			}
			
			int value = gradeAsPercentage.intValue() / 10;
			gradeFrequencies[value] = ++gradeFrequencies[value];
		}
		
		return gradeFrequencies;
	}
	
	/*
	 * @deprecated since v1.3.0
	 */
	@Deprecated 
	public List<Statistics> getStatistics(String gradebookUid, Long gradebookId, String studentId) throws SecurityException {
		
		Gradebook gradebook = null;
		if (gradebookId == null) {
			gradebook = gbService.getGradebook(gradebookUid);
			gradebookId = gradebook.getId();
		}

		List<Assignment> assignments = gbService.getAssignments(gradebookId);
		
		// Don't bother going out to the db for the Gradebook if we've already
		// retrieved it
		if (gradebook == null && assignments != null && assignments.size() > 0)
			gradebook = assignments.get(0).getGradebook();

		if (gradebook == null)
			gradebook = gbService.getGradebook(gradebookId);

		List<Category> categories = null;
		boolean hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;
		if (hasCategories)
			categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);

		int gradeType = gradebook.getGrade_type();

		String siteId = getSiteId();

		String[] realmIds = new String[1];
		realmIds[0] = new StringBuffer().append("/site/").append(siteId).toString();

		String[] learnerRoleNames = getLearnerRoleNames();

		List<AssignmentGradeRecord> allGradeRecords = gbService.getAllAssignmentGradeRecords(gradebook.getId(), realmIds, learnerRoleNames);
		Map<String, Map<Long, AssignmentGradeRecord>> studentGradeRecordMap = new HashMap<String, Map<Long, AssignmentGradeRecord>>();

		List<String> gradedStudentUids = new ArrayList<String>();

		Map<Long, BigDecimal> assignmentSumMap = new HashMap<Long, BigDecimal>();
		Map<Long, List<StudentScore>> assignmentGradeListMap = new HashMap<Long, List<StudentScore>>();

		if (allGradeRecords != null) {
			for (AssignmentGradeRecord gradeRecord : allGradeRecords) {
				gradeRecord.setUserAbleToView(true);
				String studentUid = gradeRecord.getStudentId();
				Map<Long, AssignmentGradeRecord> studentMap = studentGradeRecordMap.get(studentUid);
				if (studentMap == null) {
					studentMap = new HashMap<Long, AssignmentGradeRecord>();
				}
				GradableObject go = gradeRecord.getGradableObject();
				studentMap.put(go.getId(), gradeRecord);

				BigDecimal value = null;
				if (gradeRecord.getPointsEarned() != null) {
					Assignment assignment = (Assignment) gradeRecord.getGradableObject();
					switch (gradeType) {
						case GradebookService.GRADE_TYPE_POINTS:
							value = BigDecimal.valueOf(gradeRecord.getPointsEarned().doubleValue());
							break;
						case GradebookService.GRADE_TYPE_PERCENTAGE:
						case GradebookService.GRADE_TYPE_LETTER:
							value = gradeCalculations.getPointsEarnedAsPercent(assignment, gradeRecord);
							break;
					}

					if (value != null && assignment != null) {
						Long assignmentId = assignment.getId();
						List<StudentScore> gradeList = assignmentGradeListMap.get(assignmentId);
						if (gradeList == null) {
							gradeList = new ArrayList<StudentScore>();
							assignmentGradeListMap.put(assignmentId, gradeList);
						}
						
						gradeList.add(new StudentScore(gradeRecord.getStudentId(), value));

						BigDecimal itemSum = assignmentSumMap.get(assignmentId);
						if (itemSum == null)
							itemSum = BigDecimal.ZERO;
						itemSum = itemSum.add(value);
						assignmentSumMap.put(assignmentId, itemSum);
					}

				}
				studentGradeRecordMap.put(studentUid, studentMap);

				if (!gradedStudentUids.contains(studentUid))
					gradedStudentUids.add(studentUid);
			}
		}

		// Now we can calculate the mean course grade
		List<StudentScore> courseGradeList = new ArrayList<StudentScore>();
		BigDecimal sumCourseGrades = BigDecimal.ZERO;
		for (String studentUid : gradedStudentUids) {
			Map<Long, AssignmentGradeRecord> studentMap = studentGradeRecordMap.get(studentUid);
			BigDecimal courseGrade = null;
			
			boolean isScaledExtraCredit = Util.checkBoolean(gradebook.isScaledExtraCredit());
			switch (gradebook.getCategory_type()) {
				case GradebookService.CATEGORY_TYPE_NO_CATEGORY:
					courseGrade = gradeCalculations.getCourseGrade(gradebook, assignments, studentMap, isScaledExtraCredit);
					break;
				default:
					courseGrade = gradeCalculations.getCourseGrade(gradebook, categories, studentMap, isScaledExtraCredit);
			}

			if (courseGrade != null) {
				sumCourseGrades = sumCourseGrades.add(courseGrade);
				courseGradeList.add(new StudentScore(studentUid, courseGrade));
			}
		} 

		GradeStatistics courseGradeStatistics = gradeCalculations.calculateStatistics(courseGradeList, sumCourseGrades, studentId);

		List<Statistics> statsList = new ArrayList<Statistics>();

		long id = 0;
		statsList.add(getStatisticsMap(gradebook, "Course Grade", courseGradeStatistics, Long.valueOf(id), Long.valueOf(-1), studentId ));
		id++;

		if (assignments != null) {
			
			if (hasCategories) {
				if (categories != null) {
					for (Category category : categories) {
						List<Assignment> asns = getUncheckedAssignmentList(category);
						if (asns != null) {
							for (Assignment a : asns) {
								Long assignmentId = a.getId();
								String name = a.getName();
				
								List<StudentScore> gradeList = assignmentGradeListMap.get(assignmentId);
								BigDecimal sum = assignmentSumMap.get(assignmentId);
				
								GradeStatistics assignmentStatistics = null;
								if (gradeList != null && sum != null) {
									assignmentStatistics = gradeCalculations.calculateStatistics(gradeList, sum, studentId);
								}
								
								statsList.add(getStatisticsMap(gradebook, name, assignmentStatistics, Long.valueOf(id), assignmentId, studentId));
								id++;
							}
						}
					}
				}
			} else {
			
				for (Assignment assignment : assignments) {
					Long assignmentId = assignment.getId();
					String name = assignment.getName();
	
					List<StudentScore> gradeList = assignmentGradeListMap.get(assignmentId);
					BigDecimal sum = assignmentSumMap.get(assignmentId);
	
					GradeStatistics assignmentStatistics = null;
					if (gradeList != null && sum != null) {
						assignmentStatistics = gradeCalculations.calculateStatistics(gradeList, sum, studentId);
					}
					
					statsList.add(getStatisticsMap(gradebook, name, assignmentStatistics, Long.valueOf(id), assignmentId, studentId));
					id++;
				}
			}
		}

		return statsList;
	}
	
	public List<Statistics> getLearnerStatistics(String gradebookUid, Long gradebookId, String learnerId) throws SecurityException {
		
		return getStatistics(gradebookUid, gradebookId, learnerId);
	}
	
	public List<Statistics> getGraderStatistics(String gradebookUid, Long gradebookId, String sectionId) throws SecurityException {
		
		Gradebook gradebook = null;
		if (gradebookId == null) {
			gradebook = gbService.getGradebook(gradebookUid);
			gradebookId = gradebook.getId();
		}

		List<Assignment> assignments = gbService.getAssignments(gradebookId);
		
		// Don't bother going out to the db for the Gradebook if we've already
		// retrieved it
		if (gradebook == null && assignments != null && assignments.size() > 0)
			gradebook = assignments.get(0).getGradebook();

		if (gradebook == null)
			gradebook = gbService.getGradebook(gradebookId);

		List<Category> categories = null;
		
		boolean hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;
		if (hasCategories)
			categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);

		int gradeType = gradebook.getGrade_type();

		String siteId = getSiteId();

		
		String[] realmIds = new String[1];
		
		if(sectionId.equals(AppConstants.ALL_SECTIONS)) {
			realmIds[0] = new StringBuffer().append("/site/").append(siteId).toString();
		}
		else {
			realmIds[0] = sectionId;
		}

		String[] learnerRoleNames = getLearnerRoleNames();

		List<AssignmentGradeRecord> allGradeRecords = gbService.getAllAssignmentGradeRecords(gradebook.getId(), realmIds, learnerRoleNames);
		Map<String, Map<Long, AssignmentGradeRecord>> studentGradeRecordMap = new HashMap<String, Map<Long, AssignmentGradeRecord>>();

		List<String> gradedStudentUids = new ArrayList<String>();

		Map<Long, BigDecimal> assignmentSumMap = new HashMap<Long, BigDecimal>();
		Map<Long, List<StudentScore>> assignmentGradeListMap = new HashMap<Long, List<StudentScore>>();

		if (allGradeRecords != null) {
			for (AssignmentGradeRecord gradeRecord : allGradeRecords) {
				gradeRecord.setUserAbleToView(true);
				String studentUid = gradeRecord.getStudentId();
				Map<Long, AssignmentGradeRecord> studentMap = studentGradeRecordMap.get(studentUid);
				if (studentMap == null) {
					studentMap = new HashMap<Long, AssignmentGradeRecord>();
				}
				GradableObject go = gradeRecord.getGradableObject();
				studentMap.put(go.getId(), gradeRecord);

				BigDecimal value = null;
				if (gradeRecord.getPointsEarned() != null) {
					Assignment assignment = (Assignment) gradeRecord.getGradableObject();
					switch (gradeType) {
						case GradebookService.GRADE_TYPE_POINTS:
							value = BigDecimal.valueOf(gradeRecord.getPointsEarned().doubleValue());
							break;
						case GradebookService.GRADE_TYPE_PERCENTAGE:
						case GradebookService.GRADE_TYPE_LETTER:
							value = gradeCalculations.getPointsEarnedAsPercent(assignment, gradeRecord);
							break;
					}

					if (value != null && assignment != null) {
						Long assignmentId = assignment.getId();
						List<StudentScore> gradeList = assignmentGradeListMap.get(assignmentId);
						if (gradeList == null) {
							gradeList = new ArrayList<StudentScore>();
							assignmentGradeListMap.put(assignmentId, gradeList);
						}
						
						gradeList.add(new StudentScore(gradeRecord.getStudentId(), value));

						BigDecimal itemSum = assignmentSumMap.get(assignmentId);
						if (itemSum == null)
							itemSum = BigDecimal.ZERO;
						itemSum = itemSum.add(value);
						assignmentSumMap.put(assignmentId, itemSum);
					}

				}
				studentGradeRecordMap.put(studentUid, studentMap);

				if (!gradedStudentUids.contains(studentUid))
					gradedStudentUids.add(studentUid);
			}
		}

		// Now we can calculate the mean course grade
		List<StudentScore> courseGradeList = new ArrayList<StudentScore>();
		BigDecimal sumCourseGrades = BigDecimal.ZERO;
		for (String studentUid : gradedStudentUids) {
			Map<Long, AssignmentGradeRecord> studentMap = studentGradeRecordMap.get(studentUid);
			BigDecimal courseGrade = null;
			
			boolean isScaledExtraCredit = Util.checkBoolean(gradebook.isScaledExtraCredit());
			switch (gradebook.getCategory_type()) {
				case GradebookService.CATEGORY_TYPE_NO_CATEGORY:
					courseGrade = gradeCalculations.getCourseGrade(gradebook, assignments, studentMap, isScaledExtraCredit);
					break;
				default:
					courseGrade = gradeCalculations.getCourseGrade(gradebook, categories, studentMap, isScaledExtraCredit);
			}

			if (courseGrade != null) {
				sumCourseGrades = sumCourseGrades.add(courseGrade);
				courseGradeList.add(new StudentScore(studentUid, courseGrade));
			}
		} 

		GradeStatistics courseGradeStatistics = gradeCalculations.calculateStatistics(courseGradeList, sumCourseGrades, null);

		List<Statistics> statsList = new ArrayList<Statistics>();

		long id = 0;
		statsList.add(getStatisticsMap(gradebook, "Course Grade", courseGradeStatistics, Long.valueOf(id), Long.valueOf(-1), null));
		id++;

		if (assignments != null) {
			
			if (hasCategories) {
				if (categories != null) {
					for (Category category : categories) {
						List<Assignment> asns = getUncheckedAssignmentList(category);
						if (asns != null) {
							for (Assignment a : asns) {
								Long assignmentId = a.getId();
								String name = a.getName();
				
								List<StudentScore> gradeList = assignmentGradeListMap.get(assignmentId);
								BigDecimal sum = assignmentSumMap.get(assignmentId);
				
								GradeStatistics assignmentStatistics = null;
								if (gradeList != null && sum != null) {
									assignmentStatistics = gradeCalculations.calculateStatistics(gradeList, sum, null);
								}
								
								statsList.add(getStatisticsMap(gradebook, name, assignmentStatistics, Long.valueOf(id), assignmentId, null));
								id++;
							}
						}
					}
				}
			} else {
			
				for (Assignment assignment : assignments) {
					Long assignmentId = assignment.getId();
					String name = assignment.getName();
	
					List<StudentScore> gradeList = assignmentGradeListMap.get(assignmentId);
					BigDecimal sum = assignmentSumMap.get(assignmentId);
	
					GradeStatistics assignmentStatistics = null;
					if (gradeList != null && sum != null) {
						assignmentStatistics = gradeCalculations.calculateStatistics(gradeList, sum, null);
					}
					
					statsList.add(getStatisticsMap(gradebook, name, assignmentStatistics, Long.valueOf(id), assignmentId, null));
					id++;
				}
			}
		}

		return statsList;
	}

	public ToolManager getToolManager() {

		return toolManager;
	}
	
	public UserDirectoryService getUserService() {

		return userService;
	}
	
	public List<Map<String,Object>> getVisibleSections(String gradebookUid, boolean enableAllSectionsEntry, String allSectionsEntryTitle) {
		List<CourseSection> viewableSections = authz.getViewableSections(gradebookUid);

		List<Map<String,Object>> sections = new LinkedList<Map<String,Object>>();

		if (enableAllSectionsEntry) {
			Map<String,Object> map = new HashMap<String,Object>();
			map.put(SectionKey.S_ID.name(), "ALL");
			map.put(SectionKey.S_NM.name(), allSectionsEntryTitle);
			sections.add(map);
		}

		if (viewableSections != null) {
			for (CourseSection courseSection : viewableSections) {
				Map<String,Object> map = new HashMap<String,Object>();
				map.put(SectionKey.S_ID.name(), courseSection.getUuid());
				map.put(SectionKey.S_NM.name(), courseSection.getTitle());
				sections.add(map);
			}
		}
		
		return sections;
	}
	
	public void init() {
		enabledGradeTypes = new ArrayList<GradeType>();
		
		// Since the ApplicationContext only contains the tool specific beans, we need to get the
		// parent ApplicationContext so that we can access all the beans that have been registered
		// with the component manager
		ApplicationContext parentApplicationContext = applicationContext.getParent();
		
		// Checking in the parent ApplicationContext for InstitutionalAdvisor implementation(s)
		String[] beans = parentApplicationContext.getBeanNamesForType(InstitutionalAdvisor.class);
		
		// Make sure that there is just one implementation in the parent context
		if(beans != null && beans.length == 1) {
			advisor = (InstitutionalAdvisor) parentApplicationContext.getBean(beans[0]);
			log.info("Using institutional adviser " + beans[0]);
		}
		else {
			// If the parent context didn't have an implementation, we use the sample
			advisor = (InstitutionalAdvisor) applicationContext.getBean("org.sakaiproject.gradebook.gwt.sakai.api.SampleInstitutionalAdvisor");
			
			if (advisor != null)
				log.info("Using default institutional adviser");
			else
				log.info("Unable to find any instutional advisers");
		}
		
		stateOfScaledExtraCredit = FunctionalityStatus.OFF;
		limitScaledExtraCreditToCategoryType = null;
		
		if (configService != null) {
			helpUrl = configService.getString(AppConstants.HELP_URL_CONFIG_ID);

			boolean isPointsEnabled = true;
			boolean isPercentagesEnabled = true;
			boolean isLettersEnabled = true;
			
			String gradeTypes = configService.getString(AppConstants.ENABLED_GRADE_TYPES_ID);
			if (gradeTypes != null) {
				gradeTypes = gradeTypes.toUpperCase();
				isPointsEnabled = gradeTypes.contains("POINT");
				isPercentagesEnabled = gradeTypes.contains("PERCENT");
				isLettersEnabled = gradeTypes.contains("LETTER");
			}
			
			if (isPointsEnabled)
				enabledGradeTypes.add(GradeType.POINTS);
			if (isPercentagesEnabled)
				enabledGradeTypes.add(GradeType.PERCENTAGES);
			if (isLettersEnabled)
				enabledGradeTypes.add(GradeType.LETTERS);
			
			String learnerRoleNameString = configService.getString(AppConstants.LEARNER_ROLE_NAMES);
			
			if (learnerRoleNameString != null && !learnerRoleNameString.equals(""))
				learnerRoleNames = learnerRoleNameString.split("\\s*,\\s*");
			
			String enableScaledExtraCredit = configService.getString(AppConstants.ENABLE_SCALED_EC);
			
			if (enableScaledExtraCredit != null) {
				enableScaledExtraCredit = enableScaledExtraCredit.trim();
				if (enableScaledExtraCredit.equalsIgnoreCase("instructor")) {
					stateOfScaledExtraCredit = FunctionalityStatus.INSTRUCTOR_ONLY;
				} else if (enableScaledExtraCredit.equalsIgnoreCase("true") ||
						enableScaledExtraCredit.equalsIgnoreCase("admin")) {
					stateOfScaledExtraCredit = FunctionalityStatus.ADMIN_ONLY;
				}
			}
			
			String limitScaledExtraCredit = configService.getString(AppConstants.LIMIT_SCALED_EC);

			if (limitScaledExtraCredit != null) {
				limitScaledExtraCredit = limitScaledExtraCredit.trim().toUpperCase();
				
				if (limitScaledExtraCredit.contains("WEIGHTED"))
					limitScaledExtraCreditToCategoryType = CategoryType.WEIGHTED_CATEGORIES;
				else if (limitScaledExtraCredit.contains("CATEGORIES"))
					limitScaledExtraCreditToCategoryType = CategoryType.SIMPLE_CATEGORIES;
			}
			
			String oldImportIndicator = configService.getString(AppConstants.USE_OLD_IMPORT);
			isOldImport = oldImportIndicator != null && oldImportIndicator.equalsIgnoreCase("true");
			
		} else {
			enabledGradeTypes.add(GradeType.POINTS);
			enabledGradeTypes.add(GradeType.PERCENTAGES);
			enabledGradeTypes.add(GradeType.LETTERS);
		}
		
		if (learnerRoleNames == null)
			learnerRoleNames = advisor.getLearnerRoleNames();
		
		if (learnerRoleNames == null)
			learnerRoleNames = new String[] { "Student", "access" };
		
	}
	
	public boolean isOldImport() {
		return isOldImport;
	}
	
	public boolean isValidLetterGrade(String letterGrade) {
		return gradeCalculations.isValidLetterGrade(letterGrade);
	}
	
	public void postEvent(String message, String gradebookId, String... args) {
		if (eventTrackingService == null)
			return;
		
		StringBuilder objectReference = new StringBuilder("/gradebook/").append(gradebookId);
		
		for (String arg : args) {
			objectReference.append("/").append(arg);
		}
		
		Event event = eventTrackingService.newEvent(message, objectReference.toString(), true);
        eventTrackingService.post(event);
	}

	public void resetGradeMap(String gradebookUid) throws SecurityException {
		if (!authz.isUserAbleToEditAssessments(gradebookUid))
			throw new SecurityException("You are not authorized to update grade mappings.");
		
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		GradeMapping gradeMapping = gradebook.getSelectedGradeMapping();
		gradeMapping.setDefaultValues();
		gbService.updateGradebook(gradebook);
	}

	public void setAdvisor(InstitutionalAdvisor advisor) {

		this.advisor = advisor;
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	public Gradebook2Authz getAuthz() {
		return authz;
	}
	
	public void setAuthz(Gradebook2Authz authz) {

		this.authz = authz;
	}

	public void setBusinessLogic(BusinessLogic businessLogic) {

		this.businessLogic = businessLogic;
	}

	public void setConfigService(ServerConfigurationService configService) {

		this.configService = configService;
	}

	public void setEventTrackingService(EventTrackingService eventTrackingService) {
		this.eventTrackingService = eventTrackingService;
	}

	public void setFrameworkService(GradebookFrameworkService frameworkService) {

		this.frameworkService = frameworkService;
	}

	public void setGbService(GradebookToolService gbService) {

		this.gbService = gbService;
	}

	public void setGradeCalculations(GradeCalculations gradeCalculations) {

		this.gradeCalculations = gradeCalculations;
	}

	public void setSectionAwareness(SectionAwareness sectionAwareness) {

		this.sectionAwareness = sectionAwareness;
	}
	
	
	public void setSessionManager(SessionManager sessionManager) {

		this.sessionManager = sessionManager;
	}


	public void setSiteService(SiteService siteService) {

		this.siteService = siteService;
	}

	public void setToolManager(ToolManager toolManager) {

		this.toolManager = toolManager;
	}

	public void setUserService(UserDirectoryService userService) {

		this.userService = userService;
	}
	
	public void submitFinalGrade(List<Map<Column, String>> studentDataList, String gradebookUid, HttpServletRequest request, HttpServletResponse response) {
		for (Map<Column, String> studentData : studentDataList) {
			String studentUid = studentData.get(Column.STUDENT_UID);
			String finalGradeUserId = studentData.get(Column.FINAL_GRADE_USER_ID);
			String exportUserId = studentData.get(Column.EXPORT_USER_ID);
			String studentName = studentData.get(Column.STUDENT_NAME);
			String exportCmId = studentData.get(Column.EXPORT_CM_ID);
			String letterGrade = studentData.get(Column.LETTER_GRADE);
			
			ActionRecord actionRecord = new ActionRecord(gradebookUid, null, EntityType.GRADE_SUBMISSION.name(), ActionType.SUBMITTED.name());
			actionRecord.setEntityName(studentName);
			actionRecord.setEntityId(finalGradeUserId);
			actionRecord.setStudentUid(studentUid);

			Map<String, String> propertyMap = actionRecord.getPropertyMap();
			propertyMap.put(Column.EXPORT_USER_ID.name(), exportUserId);
			propertyMap.put(Column.EXPORT_CM_ID.name(), exportCmId);
			propertyMap.put(Column.LETTER_GRADE.name(), letterGrade);	
			
			gbService.storeActionRecord(actionRecord);
		}
		
		advisor.submitFinalGrade(studentDataList, gradebookUid, request, response);
	}

	public Boolean updateConfiguration(Long gradebookId, String field, String value) {
		
		try {		
			gbService.createOrUpdateUserConfiguration(getCurrentUser(), gradebookId, field, value);
	
		} catch (Exception e) {
			
			return Boolean.FALSE;
		}
		
		return Boolean.TRUE;
	}

	public void updateGradeMap(String gradebookUid, String affectedLetterGrade, Object value) 
	throws InvalidInputException, SecurityException {
		
		if (!authz.isUserAbleToEditAssessments(gradebookUid))
			throw new SecurityException("You are not authorized to update grade mappings.");
		
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
	
	
	private Long doUpdateItem(Item item, Assignment assignment) throws InvalidInputException {
		boolean isWeightChanged = false;
		boolean havePointsChanged = false;
	
		Long assignmentId = Long.valueOf(item.getIdentifier());
	
		Gradebook gradebook = assignment.getGradebook();
		
		Category oldCategory = null;
		Category category = assignment.getCategory();
	
		if (!authz.isUserAbleToEditAssessments(gradebook.getUid()))
			throw new InvalidInputException("You are not authorized to edit items.");
		
		
		if (category == null)
			category = findDefaultCategory(gradebook.getId());
	
		boolean hasWeightedCategories = gradebook.getCategory_type() == GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY;
		boolean hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;
		boolean hasCategoryChanged = false;
	
		if (hasCategories) {
			if (item.getCategoryId() == null && item.getCategoryName() != null) {
				GradeItem newCategory = new GradeItemImpl();
				newCategory.setName(item.getCategoryName());
				newCategory.setIncluded(Boolean.TRUE);
				newCategory = (GradeItem)addItemCategory(gradebook.getUid(), gradebook.getId(), newCategory);
				item.setCategoryId(newCategory.getCategoryId());
				hasCategoryChanged = true;
			} else
				hasCategoryChanged = !category.getId().equals(item.getCategoryId());
		}
	
		ActionRecord actionRecord = createActionRecord(assignment.getName(), String.valueOf(assignment.getId()), gradebook, item, EntityType.ITEM.name(), ActionType.UPDATE.name());
	
		boolean isRemoved = false;
	
		List<Assignment> assignments = null;
		try {
	
			// Check to see if the category id has changed -- this means the
			// user switched the item's category
			if (hasCategories && hasCategoryChanged)
				oldCategory = category;
	
			boolean wasExtraCredit = Util.checkBoolean(assignment.isExtraCredit());
			boolean isExtraCredit = Util.checkBoolean(item.getExtraCredit());
	
			isWeightChanged = wasExtraCredit != isExtraCredit;
	
			// Business rule #1
			Double points = null;
			Double oldPoints = assignment.getPointsPossible();
			if (item.getPoints() == null)
				points = Double.valueOf(100.0d);
			else
				points = convertDouble(item.getPoints());
	
			havePointsChanged = points != null && oldPoints != null && points.compareTo(oldPoints) != 0;
	
			Double newAssignmentWeight = item.getPercentCategory();
			Double oldAssignmentWeight = assignment.getAssignmentWeighting();
	
			Integer newItemOrder = item.getItemOrder();
			Integer oldItemOrder = assignment.getSortOrder();
	
			isWeightChanged = isWeightChanged || Util.notEquals(newAssignmentWeight, oldAssignmentWeight);
	
			boolean isUnweighted = !convertBoolean(item.getIncluded()).booleanValue();
			boolean wasUnweighted = assignment.isNotCounted();
	
			isRemoved = convertBoolean(item.getRemoved()).booleanValue();
			boolean wasRemoved = assignment.isRemoved();
	
			// We only want to update the weights when we're dealing with an
			// included item
			if (!isUnweighted && !isRemoved) {
				// Business rule #2
				assignment.setAssignmentWeighting(gradeCalculations.calculateItemWeightAsPercentage(newAssignmentWeight, points));
			} else {
				newAssignmentWeight = oldAssignmentWeight;
			}
	
			isWeightChanged = isWeightChanged || isUnweighted != wasUnweighted;
			isWeightChanged = isWeightChanged || isRemoved != wasRemoved;
	
			boolean isNullsAsZeros = convertBoolean(item.getNullsAsZeros()).booleanValue();
			
			if (hasCategories && category != null) {
				if (hasCategoryChanged) {
					Category newCategory = gbService.getCategory(item.getCategoryId());
					if (newCategory != null) {
						category = newCategory;
						assignment.setCategory(category);
					}
				}
	
				boolean isCategoryIncluded = !Util.checkBoolean(category.isUnweighted());
				assignments = gbService.getAssignmentsForCategory(category.getId());
	
				// Business rule #12
				businessLogic.applyCannotUnremoveItemWithRemovedCategory(isRemoved, category);
	
				// Business rule #5
				businessLogic.applyNoDuplicateItemNamesWithinCategoryRule(item.getCategoryId(), item.getName(), assignment.getId(), assignments);
	
				// Business rule #6
				businessLogic.applyCannotIncludeDeletedItemRule(wasRemoved && isRemoved, category.isRemoved(), isUnweighted);
	
				// Business rule #11
				if (!hasCategoryChanged)
					businessLogic.applyCannotIncludeItemFromUnincludedCategoryRule(isCategoryIncluded, !isUnweighted, !wasUnweighted);
	
				// Business rule #8
				businessLogic.applyMustIncludeCategoryRule(item.getCategoryId());
	
	
			} else {
				assignments = gbService.getAssignments(gradebook.getId());
	
				// Business rule #3
				businessLogic.applyNoDuplicateItemNamesRule(gradebook.getId(), item.getName(), assignment.getId(), assignments);
	
				// Business rule #4
				businessLogic.applyCannotIncludeDeletedItemRule(wasRemoved && isRemoved, false, isUnweighted);
	
			}
			
			businessLogic.applyNoZeroPointItemsRule(points);
	
			// If we don't know the old item order then we need to determine it
			if (oldItemOrder == null) {
				if (assignments != null) {
					int count = 0;
					for (Assignment a : assignments) {
						if (a.isRemoved())
							continue;
	
						if (a.getId().equals(assignmentId))
							oldItemOrder = Integer.valueOf(count);
						count++;
					}
				}
			}
	
			if ((!hasCategories || oldCategory == null) && oldItemOrder != null 
					&& newItemOrder != null && oldItemOrder.compareTo(newItemOrder) < 0)
				newItemOrder = Integer.valueOf(newItemOrder.intValue() - 1);
	
			if (newItemOrder == null)
				newItemOrder = oldItemOrder;
	
			// Modify the assignment name
			assignment.setName(convertString(item.getName()));
	
			assignment.setExtraCredit(Boolean.valueOf(isExtraCredit));
			assignment.setReleased(convertBoolean(item.getReleased()).booleanValue());
			assignment.setPointsPossible(points);
			assignment.setDueDate(convertDate(item.getDueDate()));
			assignment.setRemoved(isRemoved);
			assignment.setNotCounted(isUnweighted || isRemoved);
			assignment.setSortOrder(newItemOrder);
			assignment.setCountNullsAsZeros(Boolean.valueOf(isNullsAsZeros));
			gbService.updateAssignment(assignment);
	
			if (isRemoved)
				postEvent("gradebook2.deleteItem", String.valueOf(gradebook.getId()), String.valueOf(assignmentId));
			else
				postEvent("gradebook2.updateItem", String.valueOf(gradebook.getId()), String.valueOf(assignmentId));
			
			if (hasCategories) {
	
				List<Assignment> oldAssignments = null;
	
				boolean applyBusinessRule7 = isUnweighted != wasUnweighted || isRemoved != wasRemoved || isExtraCredit != wasExtraCredit || oldCategory != null;
	
				// Business rule #9
				if (oldCategory != null && businessLogic.checkRecalculateEqualWeightingRule(oldCategory)) {
					oldAssignments = gbService.getAssignmentsForCategory(oldCategory.getId());
					recalculateAssignmentWeights(oldCategory, Boolean.valueOf(!wasUnweighted), oldAssignments);
				}
	
				// Business rule #7 -- only apply this rule when
				// included/unincluded, deleted/undeleted, made
				// extra-credit/non-extra-credit, or changed category
				if (applyBusinessRule7) {
					isWeightChanged = true;
					if (businessLogic.checkRecalculateEqualWeightingRule(category)) {
						assignments = gbService.getAssignmentsForCategory(category.getId());
						recalculateAssignmentWeights(category, Boolean.valueOf(!isUnweighted), assignments);
					}
				}
	
				boolean applyBusinessRule10 = oldItemOrder == null || newItemOrder.compareTo(oldItemOrder) != 0 || oldCategory != null;
	
				if (applyBusinessRule10)
					businessLogic.reorderAllItemsInCategory(assignmentId, category, oldCategory, newItemOrder, oldItemOrder);
			} else {
	
				if (oldItemOrder == null || (newItemOrder != null && newItemOrder.compareTo(oldItemOrder) != 0))
					businessLogic.reorderAllItems(gradebook.getId(), assignment.getId(), newItemOrder, oldItemOrder);
	
			}
	
			if (Util.checkBoolean(item.getDoRecalculatePoints())) {
				if (businessLogic.checkRecalculatePointsRule(assignmentId, points, oldPoints))
					recalculateAssignmentGradeRecords(assignment, points, oldPoints);
			}
			
			if (havePointsChanged && category.getDrop_lowest() > 0 
					&& (!hasWeightedCategories 
						|| (hasWeightedCategories 
								&& Util.checkBoolean(category.isEnforcePointWeighting())))) {
				category.setDrop_lowest(0);
				gbService.updateCategory(category);
			}
			
		} catch (RuntimeException e) {
			actionRecord.setStatus(ActionRecord.STATUS_FAILURE);
			throw e;
		} finally {
			gbService.storeActionRecord(actionRecord);
		}
		
		return assignmentId;
	}
	
	
	public Item updateItem(Item item) throws InvalidInputException {
		
		if (item == null)
			return null;
		
		switch (item.getItemType()) {
			case CATEGORY:
				return updateCategoryModel(item);
			case GRADEBOOK:
				return updateGradebookModel(item);
		}
	
		Long assignmentId = Long.valueOf(item.getIdentifier());
		Assignment assignment = gbService.getAssignment(assignmentId);
		
		Gradebook gradebook = assignment.getGradebook();
		boolean hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;
		
		doUpdateItem(item, assignment);
	
		List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
		List<Category> categories = null;
		if (hasCategories)
			categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);
		
		return getGradeItem(gradebook, assignments, categories, null, assignment.getId());
	}
	
	private void handleImportItemModification(String gradebookUid, Long gradebookId, GradeItem item, Map<String, Assignment> idToAssignmentMap) throws InvalidInputException {
		Long itemId = null;
		String identifier = item.getIdentifier();
		
		if (identifier.startsWith("NEW:") && item.getItemId().equals(Long.valueOf(-1l))) {
			Gradebook gradebook = gbService.getGradebook(gradebookUid);
			boolean hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;
			itemId = doCreateItem(gradebook, item, hasCategories, false);
		} else if (!item.getIdentifier().equals("-1" )) {
		
			switch (item.getItemType()) {
			case ITEM:
				itemId = item.getItemId();
				Assignment assignment = gbService.getAssignment(itemId);
				
				item.setIdentifier(String.valueOf(itemId));
				itemId = doUpdateItem(item, assignment);
				break;
			case CATEGORY:
				itemId = Long.valueOf(item.getIdentifier());
				Category category = gbService.getCategory(itemId);
				
				itemId = doUpdateCategory(item, category);
				break;
			case GRADEBOOK:
				Gradebook gradebook = gbService.getGradebook(gradebookUid);
				
				itemId = doUpdateGradebook(item, gradebook);
				break;
			}
		} else {
			itemId = Long.valueOf(-1);
		}
		
		if (item.getItemType() == ItemType.ITEM) {
			if (itemId != null) {
				// First, check to make sure we haven't already stored this item
				Assignment assignment = idToAssignmentMap.get(identifier);
				
				if (assignment == null) {
					assignment = gbService.getAssignment(itemId);
					idToAssignmentMap.put(identifier, assignment);
				}
			}
		} else {
			List<GradeItem> subChildren = item.getChildren();
		
			for (GradeItem subChild : subChildren) {
				handleImportItemModification(gradebookUid, gradebookId, subChild, idToAssignmentMap);
			}
		}
	}

	
	public Upload upload(String gradebookUid, Long gradebookId, Upload upload, boolean isDryRun) throws InvalidInputException {
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		
		boolean isUserAbleToGrade = authz.isUserAbleToGradeAll(gradebook.getUid());

		if (!isUserAbleToGrade)
			throw new InvalidInputException("You are not authorized to upload grades.");
		
		boolean hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;
		boolean isLetterGrading = gradebook.getGrade_type() == GradebookService.GRADE_TYPE_LETTER;
		Map<String, Assignment> idToAssignmentMap = new HashMap<String, Assignment>();
		//Map<String, Assignment> commentIdToAssignmentMap = new HashMap<String, Assignment>();
		//List<Item> headers = upload.getHeaders();

		GradeItem gradebookItem = (GradeItem)upload.getGradebookItemModel();
		
		if (gradebookItem != null) {

			// First, update the gradebook item
			handleImportItemModification(gradebookUid, gradebookId, gradebookItem, idToAssignmentMap);
			
			gradebook = gbService.getGradebook(gradebookUid);
			
			CategoryType categoryType = gradebookItem.getCategoryType();
			GradeType gradeType = gradebookItem.getGradeType();
			
			hasCategories = categoryType != CategoryType.NO_CATEGORIES;
			isLetterGrading = gradeType == GradeType.LETTERS;

		}

		Site site = getSite();
		Map<String, UserRecord> userRecordMap = new HashMap<String, UserRecord>();

		String[] learnerRoleNames = getLearnerRoleNames();
		String siteId = site == null ? null : site.getId();

		String[] realmIds = null;

		if (siteId == null) {
			if (log.isInfoEnabled())
				log.info("No siteId defined");
			throw new InvalidInputException("No site defined!");
		}

		realmIds = new String[1];
		realmIds[0] = new StringBuffer().append("/site/").append(siteId).toString();

		List<AssignmentGradeRecord> allGradeRecords = gbService.getAllAssignmentGradeRecords(gradebookId, realmIds, learnerRoleNames);

		if (allGradeRecords != null) {
			for (AssignmentGradeRecord gradeRecord : allGradeRecords) {
				gradeRecord.setUserAbleToView(true);
				String studentUid = gradeRecord.getStudentId();
				UserRecord userRecord = userRecordMap.get(studentUid);

				if (userRecord == null) {
					userRecord = new UserRecord(studentUid);
					userRecordMap.put(studentUid, userRecord);
				}

				Map<Long, AssignmentGradeRecord> studentMap = userRecord.getGradeRecordMap();
				if (studentMap == null) {
					studentMap = new HashMap<Long, AssignmentGradeRecord>();
				}
				GradableObject go = gradeRecord.getGradableObject();
				studentMap.put(go.getId(), gradeRecord);

				userRecord.setGradeRecordMap(studentMap);
			}
		}

		// Since we index the new items by a phony id e.g. "NEW:123", we need to
		// use this set to iterate
		Set<String> idKeySet = idToAssignmentMap.keySet();
							
		if (idKeySet != null) {

			List<Learner> rows = upload.getRows();
			
			for (String id : idKeySet) {
				Assignment assignment = idToAssignmentMap.get(id);

				List<AssignmentGradeRecord> gradedRecords = new ArrayList<AssignmentGradeRecord>();
	
				if (rows == null)
					continue;
				
				for (Learner student : rows) {
					String studentUid = student.getIdentifier();
					
					if (! Util.isNotNullOrEmpty(studentUid))
						continue;
					
					UserRecord userRecord = userRecordMap.get(studentUid);

					Map<Long, AssignmentGradeRecord> gradeRecordMap = userRecord == null ? null : userRecord.getGradeRecordMap();

					// This is the value stored on the client
					Object v = student.get(id);

					String hasCommentId = Util.buildCommentKey(id);
					String textCommentId = Util.buildCommentTextKey(id);
					
					boolean hasComment = Util.toBooleanPrimitive(student.get(hasCommentId));
					String c = student.get(textCommentId);

					if (hasComment) {
						Comment comment = doAssignComment(assignment, studentUid, c);
						
						if (comment != null)
							gradebook = doCommitComment(comment, studentUid, c);
	
						if (comment != null) {
							student.set(textCommentId, comment.getCommentText());
							student.set(hasCommentId, Boolean.TRUE);
							student.set(AppConstants.IMPORT_CHANGES, Boolean.TRUE);
						}
					}
					
					Double value = null;
					Double oldValue = null;

					try {
						int gradeType = gradebook.getGrade_type();
						
						if (v != null && v instanceof String) {
							String strValue = (String) v;

							if (strValue.trim().length() > 0) {
								if (isLetterGrading) {	
									if (gradeCalculations.isValidLetterGrade(strValue)) {
										value = gradeCalculations.convertLetterGradeToPercentage((String)v);
									} else {
										boolean isParseable = false;
										try {
											value = Double.valueOf(Double.parseDouble((String) v));
											isParseable = true;
											// If it's a numeric upload into a letter grade gradebook then use points
											gradeType = GradebookService.GRADE_TYPE_POINTS;
										} catch (NumberFormatException nfe) {
											log.debug("This string does not seem to be a double: " + strValue);
										}

										if (!isParseable) {
											String failedProperty = Util.buildFailedKey(String.valueOf(assignment.getId()));
											student.set(failedProperty, "Invalid input");
											log.warn("Failed to score item for " + student.getDisplayName() + " and item " + assignment.getId() + " to " + v);

											student.set(AppConstants.IMPORT_CHANGES, Boolean.TRUE);
											continue;
										}
									}
								} else {
									value = Double.valueOf(Double.parseDouble((String) v));
								}
							}
						} else
							value = (Double) v;

						AssignmentGradeRecord assignmentGradeRecord = null;

						if (gradeRecordMap != null)
							assignmentGradeRecord = gradeRecordMap.get(assignment.getId()); 

						if (assignmentGradeRecord == null)
							assignmentGradeRecord = new AssignmentGradeRecord();
						else {
							switch (gradeType) {
							case GradebookService.GRADE_TYPE_POINTS:
								oldValue = assignmentGradeRecord.getPointsEarned();
								break;
							case GradebookService.GRADE_TYPE_PERCENTAGE:
							case GradebookService.GRADE_TYPE_LETTER:
								BigDecimal d = gradeCalculations.getPointsEarnedAsPercent(assignment, assignmentGradeRecord);
								oldValue = d == null ? null : Double.valueOf(d.doubleValue());
								break;
							}
						}
						
						if (oldValue == null && value == null)
							continue;

						gradedRecords.add(scoreItem(gradebook, gradeType, assignment, assignmentGradeRecord, studentUid, value, true, true));

						String successProperty = Util.buildSuccessKey(String.valueOf(assignment.getId()));
						student.set(successProperty, "S");
						student.set(AppConstants.IMPORT_CHANGES, Boolean.TRUE); 
						
					} catch (NumberFormatException nfe) {
						String failedProperty = Util.buildFailedKey(id);
						student.set(failedProperty, "Invalid input");
						log.warn("Number Format: Failed to score item for " + student.getDisplayName() + " and item " + assignment.getId() + " to " + v);

						student.set(AppConstants.IMPORT_CHANGES, Boolean.TRUE);
					} catch (InvalidInputException e) {
						String failedProperty = Util.buildFailedKey(id);
						String failedMessage = e != null && e.getMessage() != null ? e.getMessage() : "Failed";
						student.set(failedProperty, failedMessage);
						log.warn("Invalid Input: Failed to score numeric item for " + student.getDisplayName() + " and item " + assignment.getId() + " to " + value + " : " + failedMessage);

						student.set(AppConstants.IMPORT_CHANGES, Boolean.TRUE);
					} catch (Exception e) {
						String failedProperty = Util.buildFailedKey(id);
						student.set(failedProperty, e.getMessage());

						log.warn("Failed to score numeric item for " + student.getDisplayName() + " and item " + assignment.getId() + " to " + value, e);

						student.set(AppConstants.IMPORT_CHANGES, Boolean.TRUE);
					} 
				}
				gbService.updateAssignmentGradeRecords(assignment, gradedRecords);
				postEvent("gradebook2.assignGradesBulk", String.valueOf(gradebook.getId()), String.valueOf(assignment.getId()));
			}
			
			upload.setRows(rows);
		}

		
		Gradebook updatedGradebook = gbService.getGradebook(gradebookUid);
		List<Assignment> assignments = gbService.getAssignments(updatedGradebook.getId());
		List<Category> categories = null;
		if (hasCategories)
			categories = getCategoriesWithAssignments(updatedGradebook.getId(), assignments, true);
		
		GradeItem gradebookGradeItem = getGradeItem(updatedGradebook, assignments, categories, null, null);

		upload.setGradebookItemModel(gradebookGradeItem);

		return upload;	
	}
	
	
	public Upload oldUpload(String gradebookUid, Long gradebookId, Upload upload, boolean isDryRun) throws InvalidInputException {
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		
		boolean isUserAbleToGrade = authz.isUserAbleToGradeAll(gradebook.getUid());

		if (!isUserAbleToGrade)
			throw new InvalidInputException("You are not authorized to upload grades.");
		
		boolean hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;
		boolean isLetterGrading = gradebook.getGrade_type() == GradebookService.GRADE_TYPE_LETTER;
		Map<String, Assignment> idToAssignmentMap = new HashMap<String, Assignment>();
		Map<String, Assignment> commentIdToAssignmentMap = new HashMap<String, Assignment>();
		List<Item> headers = upload.getHeaders();

		if (headers != null) {

			Set<Long> newCategoryIdSet = new HashSet<Long>();
			for (Item item : headers) {
				String id = (String)item.get(ItemKey.S_ID.name());
				if (id != null) {
					Long categoryId = Util.toLong(item.get(ItemKey.L_CTGRY_ID.name()));					
					String name = (String)item.get(ItemKey.S_NM.name());
					Double weight = Util.toDouble(item.get(ItemKey.D_PCT_CTGRY.name()));
					Double points = Util.toDouble(item.get(ItemKey.D_PNTS.name()));
					boolean isExtraCredit = Util.toBooleanPrimitive(item.get(ItemKey.B_X_CRDT.name()));
					boolean isIncluded = Util.toBooleanPrimitive(item.get(ItemKey.B_INCLD.name()));

					//int indexOfCommentTextFlag = id.indexOf(AppConstants.COMMENT_TEXT_FLAG);
					
					if (id.startsWith(AppConstants.COMMENT_TEXT_FLAG)) {
						String realId = Util.unpackItemIdFromKey(id);
						
						Assignment assignment = null;
						if (realId.startsWith("NEW:")) {
							assignment = idToAssignmentMap.get(realId);
							
						} else {
							assignment = gbService.getAssignment(Long.valueOf(realId));	
						}
						
						commentIdToAssignmentMap.put(realId, assignment);
					} else if (id.startsWith("NEW:")) {

						GradeItem itemModel = new GradeItemImpl();
						itemModel.setCategoryId(categoryId);
						itemModel.setName(name);
						itemModel.setPercentCategory(weight);
						itemModel.setPoints(points);
						itemModel.setIncluded(Boolean.valueOf(isIncluded));
						itemModel.setExtraCredit(Boolean.valueOf(isExtraCredit));

						Long assignmentId = doCreateItem(gradebook, itemModel, hasCategories, false);
						item.setIdentifier(String.valueOf(assignmentId));

						postEvent("gradebook2.importNewItem", String.valueOf(gradebook.getId()), String.valueOf(assignmentId));
						
						Assignment assignment = gbService.getAssignment(assignmentId);
						idToAssignmentMap.put(id, assignment);

					} else {
						
						Assignment assignment = gbService.getAssignment(Long.valueOf(id));

						boolean isModified = false;

						if (points != null && assignment.getPointsPossible() != null && !points.equals(assignment.getPointsPossible())) {
							assignment.setPointsPossible(points);
							isModified = true;
						}

						if (weight != null && assignment.getAssignmentWeighting() != null && !weight.equals(assignment.getAssignmentWeighting())) {

							BigDecimal decimalWeight = BigDecimal.valueOf(weight.doubleValue()).multiply(BigDecimal.valueOf(0.01d), GradeCalculations.MATH_CONTEXT);
							
							weight = Double.valueOf(decimalWeight.doubleValue());

							assignment.setAssignmentWeighting(weight);
							isModified = true;
						}

						boolean wasIncluded = assignment.isCounted();

						if (wasIncluded != isIncluded) {
							assignment.setCounted(isIncluded);
							isModified = true;
						}

						boolean wasExtraCredit = Util.checkBoolean(assignment.isExtraCredit());

						if (wasExtraCredit != isExtraCredit) {
							assignment.setExtraCredit(Boolean.valueOf(isExtraCredit));
							isModified = true;
						}

						if (isModified) {
							gbService.updateAssignment(assignment);
							postEvent("gradebook2.importUpdateItem", String.valueOf(gradebook.getId()), String.valueOf(assignment.getId()));
						}
						
						idToAssignmentMap.put(id, assignment);
					}
				}
			}

			// Apply business rules after item creation
			if (hasCategories) {
				List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
				List<Category> categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);

				Map<Long, Category> categoryMap = new HashMap<Long, Category>();

				if (categories != null) {
					for (Category category : categories) {
						categoryMap.put(category.getId(), category);
					}
				}

				if (newCategoryIdSet != null && !newCategoryIdSet.isEmpty()) {

					for (Long categoryId : newCategoryIdSet) {
						Category category = categoryMap.get(categoryId);
						List<Assignment> assigns = getUncheckedAssignmentList(category);
						// Business rule #5
						if (businessLogic.checkRecalculateEqualWeightingRule(category))
							recalculateAssignmentWeights(category, Boolean.FALSE, assigns);
					}

				}
			}
		}

		Site site = getSite();
		Map<String, UserRecord> userRecordMap = new HashMap<String, UserRecord>();

		String[] learnerRoleNames = getLearnerRoleNames();
		String siteId = site == null ? null : site.getId();

		String[] realmIds = null;

		if (siteId == null) {
			if (log.isInfoEnabled())
				log.info("No siteId defined");
			throw new InvalidInputException("No site defined!");
		}

		realmIds = new String[1];
		realmIds[0] = new StringBuffer().append("/site/").append(siteId).toString();

		List<AssignmentGradeRecord> allGradeRecords = gbService.getAllAssignmentGradeRecords(gradebookId, realmIds, learnerRoleNames);

		if (allGradeRecords != null) {
			for (AssignmentGradeRecord gradeRecord : allGradeRecords) {
				gradeRecord.setUserAbleToView(true);
				String studentUid = gradeRecord.getStudentId();
				UserRecord userRecord = userRecordMap.get(studentUid);

				if (userRecord == null) {
					userRecord = new UserRecord(studentUid);
					userRecordMap.put(studentUid, userRecord);
				}

				Map<Long, AssignmentGradeRecord> studentMap = userRecord.getGradeRecordMap();
				if (studentMap == null) {
					studentMap = new HashMap<Long, AssignmentGradeRecord>();
				}
				GradableObject go = gradeRecord.getGradableObject();
				studentMap.put(go.getId(), gradeRecord);

				userRecord.setGradeRecordMap(studentMap);
			}
		}

		//List<String> results = new ArrayList<String>();

		// Since we index the new items by a phony id e.g. "NEW:123", we need to
		// use this set to iterate
		Set<String> idKeySet = idToAssignmentMap.keySet();
		Set<String> commentIdKeySet = commentIdToAssignmentMap.keySet();

		if (commentIdKeySet != null) {
			
			for (String commentId : commentIdKeySet) {
				String fullId = new StringBuilder().append(commentId).append(AppConstants.COMMENT_TEXT_FLAG).toString();
				
				List<Learner> rows = upload.getRows();
				if (rows != null) {
					for (Learner student : rows) {
						Object v = student.get(fullId);
		
						String studentUid = (String)student.get(LearnerKey.S_UID.name());
						Assignment assignment = commentIdToAssignmentMap.get(commentId);
						Comment comment = doAssignComment(assignment.getId(), studentUid, (String)v);
	
						if (comment != null)
							gradebook = doCommitComment(comment, studentUid, (String)v);
	
						if (comment != null) {
							student.set(fullId, comment.getCommentText());
							student.set(Util.buildCommentKey(commentId), Boolean.TRUE);
							student.set(AppConstants.IMPORT_CHANGES, Boolean.TRUE);
						}
					}
				}
			}
		}
		
							
		if (idKeySet != null) {

			for (String id : idKeySet) {
				Assignment assignment = idToAssignmentMap.get(id);

				List<AssignmentGradeRecord> gradedRecords = new ArrayList<AssignmentGradeRecord>();

				List<Learner> rows = upload.getRows();
				if (rows == null)
					continue;
				
				for (Learner student : rows) {
					UserRecord userRecord = userRecordMap.get(student.get(LearnerKey.S_UID.name()));

					Map<Long, AssignmentGradeRecord> gradeRecordMap = userRecord == null ? null : userRecord.getGradeRecordMap();

					// This is the value stored on the client
					Object v = student.get(id);

					Double value = null;
					Double oldValue = null;

					try {
						int gradeType = gradebook.getGrade_type();
						
						if (v != null && v instanceof String) {
							String strValue = (String) v;

							if (strValue.trim().length() > 0) {
								if (isLetterGrading) {	
									if (gradeCalculations.isValidLetterGrade(strValue)) {
										value = gradeCalculations.convertLetterGradeToPercentage((String)v);
									} else {
										boolean isParseable = false;
										try {
											value = Double.valueOf(Double.parseDouble((String) v));
											isParseable = true;
											// If it's a numeric upload into a letter grade gradebook then use points
											gradeType = GradebookService.GRADE_TYPE_POINTS;
										} catch (NumberFormatException nfe) {
											log.debug("This string does not seem to be a double: " + strValue);
										}

										if (!isParseable) {
											String failedProperty = Util.buildFailedKey(String.valueOf(assignment.getId()));
											student.set(failedProperty, "Invalid input");
											log.warn("Failed to score item for " + student.get(LearnerKey.S_UID.name()) + " and item " + assignment.getId() + " to " + v);

											student.set(AppConstants.IMPORT_CHANGES, Boolean.TRUE);
											continue;
										}
									}
								} else {
									value = Double.valueOf(Double.parseDouble((String) v));
								}
							}
						} else
							value = (Double) v;

						AssignmentGradeRecord assignmentGradeRecord = null;

						if (gradeRecordMap != null)
							assignmentGradeRecord = gradeRecordMap.get(assignment.getId()); 

						if (assignmentGradeRecord == null)
							assignmentGradeRecord = new AssignmentGradeRecord();
						else {
							switch (gradeType) {
							case GradebookService.GRADE_TYPE_POINTS:
								oldValue = assignmentGradeRecord.getPointsEarned();
								break;
							case GradebookService.GRADE_TYPE_PERCENTAGE:
							case GradebookService.GRADE_TYPE_LETTER:
								BigDecimal d = gradeCalculations.getPointsEarnedAsPercent(assignment, assignmentGradeRecord);
								oldValue = d == null ? null : Double.valueOf(d.doubleValue());
								break;
							}
						}
						
						if (oldValue == null && value == null)
							continue;

						gradedRecords.add(scoreItem(gradebook, gradeType, assignment, assignmentGradeRecord, (String)student.get(LearnerKey.S_UID.name()), value, true, true));

						String successProperty = Util.buildSuccessKey(String.valueOf(assignment.getId()));
						student.set(successProperty, "S");
						student.set(AppConstants.IMPORT_CHANGES, Boolean.TRUE); 
						
					} catch (NumberFormatException nfe) {
						String failedProperty = Util.buildFailedKey(id);
						student.set(failedProperty, "Invalid input");
						log.warn("Failed to score item for " + (String)student.get(LearnerKey.S_UID.name()) + " and item " + assignment.getId() + " to " + v);

						student.set(AppConstants.IMPORT_CHANGES, Boolean.TRUE);
					} catch (InvalidInputException e) {
						String failedProperty = Util.buildFailedKey(id);
						String failedMessage = e != null && e.getMessage() != null ? e.getMessage() : "Failed";
						student.set(failedProperty, failedMessage);
						log.warn("Failed to score numeric item for " + (String)student.get(LearnerKey.S_UID.name()) + " and item " + assignment.getId() + " to " + value + " : " + failedMessage);

						student.set(AppConstants.IMPORT_CHANGES, Boolean.TRUE);
					} catch (Exception e) {
						String failedProperty = Util.buildFailedKey(id);
						student.set(failedProperty, e.getMessage());

						log.warn("Failed to score numeric item for " + (String)student.get(LearnerKey.S_UID.name()) + " and item " + assignment.getId() + " to " + value, e);

						student.set(AppConstants.IMPORT_CHANGES, Boolean.TRUE);
					} 
				}
				gbService.updateAssignmentGradeRecords(assignment, gradedRecords);
				postEvent("gradebook2.assignGradesBulk", String.valueOf(gradebook.getId()), String.valueOf(assignment.getId()));
			}
		}

		List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
		List<Category> categories = null;
		if (hasCategories)
			categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);
		
		GradeItem gradebookGradeItem = getGradeItem(gradebook, assignments, categories, null, null);

		upload.setGradebookItemModel(gradebookGradeItem);

		return upload;	
	}
	

	private Item addItemCategory(String gradebookUid, Long gradebookId, Item item) throws BusinessRuleException {
		return addItemCategory(gradebookUid, gradebookId, item.getProperties());
	}

	/**
	 * Method to add a new category to a gradebook
	 * 
	 * Business rules: (1) if no other categories exist, then make the category
	 * weight 100% (2) new category name must not duplicate an existing category
	 * name
	 * 
	 * @param gradebookUid
	 * @param gradebookId
	 * @param item
	 * @return
	 * @throws BusinessRuleException
	 */
	private Item addItemCategory(String gradebookUid, Long gradebookId, Map<String, Object> attributes) throws BusinessRuleException {

		String name = (String)attributes.get(ItemKey.S_NM.name());
		
		ActionRecord actionRecord = new ActionRecord(gradebookUid, gradebookId, EntityType.CATEGORY.name(), ActionType.CREATE.name());
		actionRecord.setEntityName(name);
		Map<String, String> propertyMap = actionRecord.getPropertyMap();

		for (String property : attributes.keySet()) {
			String value = String.valueOf(attributes.get(property));
			if (value != null)
				propertyMap.put(property, value);
		}

		Gradebook gradebook = null;
		List<Assignment> assignments = null;
		List<Category> categories = null;
		Long categoryId = null;

		try {
			Double weight = (Double)attributes.get(ItemKey.D_PCT_GRD.name());
			Boolean isEqualWeighting = (Boolean)attributes.get(ItemKey.B_EQL_WGHT.name());
			Boolean isIncluded = (Boolean)attributes.get(ItemKey.B_INCLD.name());
			Integer dropLowest = (Integer)attributes.get(ItemKey.I_DRP_LWST.name());
			Boolean isExtraCredit = (Boolean)attributes.get(ItemKey.B_X_CRDT.name());
			Integer categoryOrder = (Integer)attributes.get(ItemKey.I_SRT_ORDR.name());
			Boolean doEnforcePointWeighting = (Boolean)attributes.get(ItemKey.B_WT_BY_PTS.name());

			boolean isUnweighted = !Util.checkBoolean(isIncluded);

			gradebook = gbService.getGradebook(gradebookUid);

			boolean hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;
			boolean hasWeights = gradebook.getCategory_type() == GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY;

			if (hasCategories) {
				categories = gbService.getCategories(gradebook.getId()); // getCategoriesWithAssignments(gradebook.getId());
				if (categoryOrder == null)
					categoryOrder = categories == null || categories.isEmpty() ? Integer.valueOf(0) : Integer.valueOf(categories.size());
			}

			double w = weight == null ? 0d : ((Double)weight).doubleValue() * 0.01;

			if (hasCategories) {
				int dropLowestInt = dropLowest == null ? 0 : dropLowest.intValue();
				boolean equalWeighting = isEqualWeighting == null ? false : isEqualWeighting.booleanValue();

				businessLogic.applyNoDuplicateCategoryNamesRule(gradebook.getId(), name, null, categories);
				if (hasWeights)
					businessLogic.applyOnlyEqualWeightDropLowestRule(dropLowestInt, equalWeighting);
			}

			categoryId = gbService.createCategory(gradebookId, name, Double.valueOf(w), dropLowest, isEqualWeighting, Boolean.valueOf(isUnweighted), isExtraCredit, categoryOrder, doEnforcePointWeighting);

			postEvent("gradebook2.newCategory", String.valueOf(gradebook.getId()), String.valueOf(categoryId));
			
			assignments = gbService.getAssignments(gradebook.getId());
			categories = null;
			if (hasCategories) {
				categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);
			}

		} catch (RuntimeException e) {
			actionRecord.setStatus(ActionRecord.STATUS_FAILURE);
			throw e;
		} finally {
			gbService.storeActionRecord(actionRecord);
		}

	
		return getGradeItem(gradebook, assignments, categories, categoryId, null);
	}

	private Map<String, Object> appendItemData(Long assignmentId, Map<String, Object> cellMap, UserRecord userRecord, Gradebook gradebook, Boolean countNullsAsZeros) {

		AssignmentGradeRecord gradeRecord = null;

		String id = String.valueOf(assignmentId);

		boolean isCommented = userRecord.getCommentMap() != null && userRecord.getCommentMap().get(assignmentId) != null;
		boolean isCountNullsAsZeros = Util.checkBoolean(countNullsAsZeros);
		
		if (isCommented) {
			cellMap.put(Util.buildCommentKey(id), Boolean.TRUE);
			cellMap.put(Util.buildCommentTextKey(id), userRecord.getCommentMap().get(assignmentId).getCommentText());
		}

		Map<Long, AssignmentGradeRecord> studentGradeMap = userRecord.getGradeRecordMap();

		gradeRecord = studentGradeMap == null ? null : studentGradeMap.get(assignmentId);

		
		boolean isExcused = gradeRecord == null ? false : gradeRecord.isExcludedFromGrade() != null && gradeRecord.isExcludedFromGrade().booleanValue();
		boolean isDropped = gradeRecord == null ? false : gradeRecord.isDropped() != null && gradeRecord.isDropped().booleanValue();

		if (isDropped || isExcused)
			cellMap.put(Util.buildDroppedKey(id), Boolean.TRUE);

		if (isExcused)
			cellMap.put(Util.buildExcusedKey(id), Boolean.TRUE);

		try {
			BigDecimal percentage = null;
			switch (gradebook.getGrade_type()) {
			case GradebookService.GRADE_TYPE_POINTS:
				Double value = gradeRecord != null && gradeRecord.getPointsEarned() != null ? gradeRecord.getPointsEarned() : (isCountNullsAsZeros ? Double.valueOf(0) : null);
				cellMap.put(id, value);
				break;
			case GradebookService.GRADE_TYPE_PERCENTAGE:
				percentage = gradeRecord == null ? null : gradeCalculations.getPointsEarnedAsPercent((Assignment) gradeRecord.getGradableObject(), gradeRecord);
				Double percentageDouble = percentage != null ? Double.valueOf(percentage.doubleValue()) : (isCountNullsAsZeros ? Double.valueOf(0) : null);
				cellMap.put(id, percentageDouble);
				break;
			case GradebookService.GRADE_TYPE_LETTER:
				percentage = gradeRecord == null ? null : gradeCalculations.getPointsEarnedAsPercent((Assignment) gradeRecord.getGradableObject(), gradeRecord);
				String letterGrade = percentage != null ? gradeCalculations.convertPercentageToLetterGrade(percentage) : (isCountNullsAsZeros ? "0" : "");
				cellMap.put(id, letterGrade);
				break;
			default:
				cellMap.put(id, "Not implemented");
				break;
			}
		} catch (ClassCastException cce) {
			log.warn("ClassCastException for user " + userRecord.getDisplayId() + " item " + assignmentId);
		}

		return cellMap;
	}

	private GradeEvent buildGradeEvent(GradingEvent event) {
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

		GradeEvent gradeEvent = new GradeEventImpl();		
		gradeEvent.setIdentifier(String.valueOf(event.getId()));
		gradeEvent.setGraderName(graderName);
		gradeEvent.setGrade(event.getGrade());
		gradeEvent.setDateGraded(dateFormat.format(event.getDateGraded()));
	
		return gradeEvent;
	}

	private Learner buildLearnerGradeRecord(Gradebook gradebook, UserRecord userRecord, List<FixedColumn> columns, List<Assignment> assignments, List<Category> categories) {

		if (userRecord == null)
			return null;
		
		Map<Long, AssignmentGradeRecord> studentGradeMap = userRecord.getGradeRecordMap();

		// This is an intermediate map for data to be placed in the record
		Map<String, Object> cellMap = new HashMap<String, Object>();

		// This is how we track which column is which - by the user's uid
		cellMap.put(LearnerKey.S_UID.name(), userRecord.getUserUid());
		cellMap.put(LearnerKey.S_EID.name(), userRecord.getUserEid());
		cellMap.put(LearnerKey.S_EXPRT_CM_ID.name(), userRecord.getExportCourseManagementId());
		cellMap.put(LearnerKey.S_DSPLY_CM_ID.name(), userRecord.getDisplayCourseManagementId());
		cellMap.put(LearnerKey.S_EXPRT_USR_ID.name(), userRecord.getExportUserId());
		cellMap.put(LearnerKey.S_FNL_GRD_ID.name(), userRecord.getFinalGradeUserId());
		
		// Need this to show the grade override
		CourseGradeRecord courseGradeRecord = userRecord.getCourseGradeRecord(); 

		String enteredGrade = null;
		DisplayGrade displayGrade = null;
		BigDecimal calculatedGrade = getCalculatedGrade(gradebook, assignments, categories, studentGradeMap);
		
		if (courseGradeRecord != null)
			enteredGrade = courseGradeRecord.getEnteredGrade();

		if (userRecord.isCalculated())
			displayGrade = userRecord.getDisplayGrade();
		else
			displayGrade = getDisplayGrade(gradebook, userRecord.getUserUid(), courseGradeRecord, calculatedGrade);

		if (columns != null) {
			for (FixedColumn column : columns) {
				LearnerKey key = LearnerKey.valueOf(column.getKey());
				switch (key) {
					case S_DSPLY_ID:
						cellMap.put(LearnerKey.S_DSPLY_ID.name(), userRecord.getDisplayId());
						break;
					case S_DSPLY_NM:
						// For the single view, maybe some redundancy, but not
						// much
						String displayName = userRecord.getDisplayName();

						if (displayName == null)
							displayName = "[User name not found]";

						String lastNameFirst = userRecord.getLastNameFirst();
						
						if (lastNameFirst == null) 
							lastNameFirst = "[User name not found]";
						
						cellMap.put(LearnerKey.S_DSPLY_NM.name(), displayName);
						cellMap.put(LearnerKey.S_LST_NM_FRST.name(), lastNameFirst);
						cellMap.put(LearnerKey.S_EMAIL.name(), userRecord.getEmail());
						break;
					case S_SECT:
						cellMap.put(LearnerKey.S_SECT.name(), userRecord.getSectionTitle());
						break;
					case S_CRS_GRD:
						if (displayGrade != null) {
							cellMap.put(LearnerKey.S_CRS_GRD.name(), displayGrade.toString());
							if (log.isDebugEnabled())
								log.debug("Setting override to " + displayGrade.isOverridden());
							cellMap.put(LearnerKey.B_GRD_OVRDN.name(), Boolean.toString(displayGrade.isOverridden()));
						}
						break;
					case S_OVRD_GRD:
						if (enteredGrade != null)
							cellMap.put(LearnerKey.S_OVRD_GRD.name(), enteredGrade);
						break;
					case S_CALC_GRD:
						if (displayGrade != null) 
							cellMap.put(LearnerKey.S_CALC_GRD.name(), displayGrade.getCalculatedGradeAsString());
						break;
					case S_LTR_GRD:
						if (displayGrade != null) 
							cellMap.put(LearnerKey.S_LTR_GRD.name(), displayGrade.getLetterGrade());
						break;
				};
			}
		}

		if (assignments != null) {

			for (Assignment assignment : assignments) {
				cellMap = appendItemData(assignment.getId(), cellMap, userRecord, gradebook, assignment.getCountNullsAsZeros());
			}

		} else {

			if (studentGradeMap != null) {
				for (AssignmentGradeRecord gradeRecord : studentGradeMap.values()) {
					Assignment assignment = gradeRecord.getAssignment();
					cellMap = appendItemData(assignment.getId(), cellMap, userRecord, gradebook, assignment.getCountNullsAsZeros());
				}
			}
			
		}

		return new LearnerImpl(cellMap);
	}
	
	private Learner buildStudentRow(Gradebook gradebook, UserRecord userRecord, List<FixedColumn> columns, List<Assignment> assignments, List<Category> categories) {
		return buildLearnerGradeRecord(gradebook, userRecord, columns, assignments, categories);
	}
	
	private UserRecord buildUserRecord(Site site, User user, Gradebook gradebook) {
		UserRecord userRecord = new UserRecord(user);
		if (site != null) {
			Collection<Group> groups = site.getGroupsWithMember(user.getId());
			if (groups != null && !groups.isEmpty()) {
				for (Group group : groups) {
					// FIXME: We probably don't just want to grab
					// the first group the user is in
					userRecord.setSectionTitle(group.getTitle());
					break;
				}
			}
		}

		Map<Long, Comment> commentMap = new HashMap<Long, Comment>();
		List<Comment> comments = gbService.getStudentAssignmentComments(user.getId(), gradebook.getId());
		
		for (Comment comment : comments) {
			GradableObject a = comment.getGradableObject();
			
			if (a != null) {
				commentMap.put(a.getId(), comment);
			}
		}
		
		userRecord.setCommentMap(commentMap);
		userRecord.setCourseGradeRecord(gbService.getStudentCourseGradeRecord(gradebook, user.getId()));

		List<AssignmentGradeRecord> records = gbService.getAssignmentGradeRecordsForStudent(gradebook.getId(), userRecord.getUserUid());

		if (records != null) {
			for (AssignmentGradeRecord gradeRecord : records) {
				gradeRecord.setUserAbleToView(true);
				Map<Long, AssignmentGradeRecord> studentMap = userRecord.getGradeRecordMap();
				if (studentMap == null) {
					studentMap = new HashMap<Long, AssignmentGradeRecord>();
					userRecord.setGradeRecordMap(studentMap);
				}
				GradableObject go = gradeRecord.getGradableObject();
				studentMap.put(go.getId(), gradeRecord);
			}
		}
		
		return userRecord;
	}
	
	
	private UserRecord buildUserRecordWithSectionInfo(Site site, User user, Gradebook gradebook) {
	
		UserRecord userRecord = buildUserRecord(site, user, gradebook);
		
		Site s = null;
		try {
			s = siteService.getSite(toolManager.getCurrentPlacement().getContext());
		} catch (IdUnusedException e) {
			log.error("current site not found trying store '" + ActionType.GRADED.name() + "' actionrecord");
			
		}
		Collection<Group> groups = ( null==s ? new ArrayList<Group>() : s.getGroups() );
		List<String> groupReferences = new ArrayList<String>();
		Map<String, Group> groupReferenceMap = new HashMap<String, Group>();
		
		for (Iterator<Group> i = groups.iterator();i.hasNext();) {
			Group g = i.next();
			if ( g != null ) {
				groupReferences.add(g.getReference());
				groupReferenceMap.put(g.getReference(), g);
			}
		}
		
		String[] learnerRoleNames = getLearnerRoleNames();
		List<Object[]> tuples = gbService.getUserGroupReferences(groupReferences, learnerRoleNames);
		List<Group> userGroups = new ArrayList<Group>();
		List<String> eids = new ArrayList<String>();
		
		if (tuples != null) {
			for (Object[] tuple : tuples) {
				String userUid = (String) tuple[0];
				String realmId = (String) tuple[1];
				
				if ( userRecord.getUserUid().equalsIgnoreCase(userUid) ) {
					userGroups.add(groupReferenceMap.get(realmId));
				}
			}
		}
		if(log.isDebugEnabled()) {
			log.debug("[" + userGroups.size() + "] groups found for student");
		}
		
		for (Group g : userGroups) {
			eids.add(g.getProviderGroupId());
		}
		 
		String primaryEid = advisor.getPrimarySectionEid(eids);
		
		userRecord.setDisplayCourseManagementId(advisor.getDisplaySectionId(primaryEid));
		
		return userRecord;
	}


	private void calculateItemCategoryPercent(Gradebook gradebook, Category category, GradeItem gradebookGradeItem, GradeItem categoryGradeItem, List<Assignment> assignments, Long assignmentId) {

		double pG = categoryGradeItem == null || categoryGradeItem.getPercentCourseGrade() == null ? 0d : categoryGradeItem.getPercentCourseGrade().doubleValue();

		boolean isWeighted = gradebook.getCategory_type() == GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY;
		boolean isCategoryExtraCredit = category != null && Util.checkBoolean(category.isExtraCredit());
		boolean isEnforcePointWeighting = category != null && Util.checkBoolean(category.isEnforcePointWeighting());
		BigDecimal percentGrade = BigDecimal.valueOf(pG);
		BigDecimal percentCategorySum = BigDecimal.ZERO;
		BigDecimal pointsSum = BigDecimal.ZERO;
		
		if (assignments != null) {
			BigDecimal[] sums = gradeCalculations.calculatePointsCategoryPercentSum(category, assignments, isWeighted, isCategoryExtraCredit);
			percentCategorySum = sums[0];
			pointsSum = sums[1];
			
			for (Assignment a : assignments) {
				BigDecimal[] result = gradeCalculations.calculateCourseGradeCategoryPercents(a, percentGrade, percentCategorySum, pointsSum, isEnforcePointWeighting);
				
				BigDecimal courseGradePercent = result[0];
				BigDecimal percentCategory = result[1];
				
				GradeItem assignmentGradeItem = createGradeItem(category, a, courseGradePercent, percentCategory);

				if (assignmentId != null && a.getId().equals(assignmentId))
					assignmentGradeItem.setActive(true);

				if (gradebook.getCategory_type() == GradebookService.CATEGORY_TYPE_NO_CATEGORY) {
					gradebookGradeItem.addChild(assignmentGradeItem);
				} else {
					categoryGradeItem.addChild(assignmentGradeItem);
				}
			}
		}

		if (gradebookGradeItem != null) {
			gradebookGradeItem.setPoints(Double.valueOf(pointsSum.doubleValue()));
		}

		if (categoryGradeItem != null) {
			if (isWeighted) {
				if (isEnforcePointWeighting) {
					if (pointsSum.compareTo(BigDecimal.ZERO) > 0)
						categoryGradeItem.setPercentCategory(Double.valueOf(100d));
					else
						categoryGradeItem.setPercentCategory(Double.valueOf(0d));
				} else
					categoryGradeItem.setPercentCategory(Double.valueOf(percentCategorySum.doubleValue()));
			} else {
				categoryGradeItem.setPercentCategory(null);
			}
			categoryGradeItem.setPoints(Double.valueOf(pointsSum.doubleValue()));
		}
	}
	
	
	private void decorateItemCategoryPercent(GradeItem gradebookGradeItem, GradeItem categoryGradeItem, List<GradeItem> assignments, Long assignmentId) {

		double pG = categoryGradeItem == null || categoryGradeItem.getPercentCourseGrade() == null ? 0d : categoryGradeItem.getPercentCourseGrade().doubleValue();

		CategoryType categoryType = gradebookGradeItem.getCategoryType();
		boolean isWeighted = categoryType == CategoryType.WEIGHTED_CATEGORIES;
		boolean isNoCategories = categoryType == CategoryType.NO_CATEGORIES;
		boolean isCategoryExtraCredit = categoryGradeItem != null && Util.checkBoolean(categoryGradeItem.getExtraCredit());
		boolean isEnforcePointWeighting = categoryGradeItem != null && Util.checkBoolean(categoryGradeItem.getEnforcePointWeighting());
		BigDecimal percentGrade = BigDecimal.valueOf(pG);
		BigDecimal percentCategorySum = BigDecimal.ZERO;
		BigDecimal pointsSum = BigDecimal.ZERO;
		
		//if (isWeighted)
		//	categoryGradeItem.setPercentCourseGrade(categoryGradeItem.getWeighting());
		
		if (assignments != null) {
			BigDecimal[] sums = gradeCalculations.calculatePointsCategoryPercentSum(categoryGradeItem, assignments, categoryType, isCategoryExtraCredit);
			percentCategorySum = sums[0];
			pointsSum = sums[1];
			
			for (GradeItem assignmentGradeItem : assignments) {
				BigDecimal[] result = gradeCalculations.calculateCourseGradeCategoryPercents(assignmentGradeItem, percentGrade, percentCategorySum, pointsSum, isEnforcePointWeighting);
				
				BigDecimal percentCourseGrade = result[0];
				BigDecimal percentCategory = result[1];
				
				if (isWeighted) {
					if (percentCategory != null)
						assignmentGradeItem.setPercentCategory(Double.valueOf(percentCategory.doubleValue()));
					
					if (percentCourseGrade != null)
						assignmentGradeItem.setPercentCourseGrade(Double.valueOf(percentCourseGrade.doubleValue()));
				} else {
					assignmentGradeItem.setPercentCategory(null);
					assignmentGradeItem.setPercentCourseGrade(null);
				}
				
				if (assignmentId != null && assignmentGradeItem.getIdentifier().equals(String.valueOf(assignmentId)))
					assignmentGradeItem.setActive(true);
//TODO: Remove these lines below
				/*if (isNoCategories) {
					gradebookGradeItem.addChild(assignmentGradeItem);
				} else {
					categoryGradeItem.addChild(assignmentGradeItem);
				}*/
			}
		}

		if (gradebookGradeItem != null) {
			gradebookGradeItem.setPoints(Double.valueOf(pointsSum.doubleValue()));
		}

		if (categoryGradeItem != null) {
			if (isWeighted) {
				if (isEnforcePointWeighting) {
					if (pointsSum.compareTo(BigDecimal.ZERO) > 0)
						categoryGradeItem.setPercentCategory(Double.valueOf(100d));
					else
						categoryGradeItem.setPercentCategory(Double.valueOf(0d));
				} else
					categoryGradeItem.setPercentCategory(Double.valueOf(percentCategorySum.doubleValue()));
			} else {
				categoryGradeItem.setPercentCategory(null);
			}
			categoryGradeItem.setPoints(Double.valueOf(pointsSum.doubleValue()));
		}
	}


	private String composeModeString(GradeStatistics statistics, Gradebook gradebook) {
		List<BigDecimal> modeList = statistics.getModeList(); 
		StringBuilder sb = new StringBuilder();
		String modeString; 
		boolean first  = true; 
		if (modeList == null) 
		{
			return NA; 
		}
		if (modeList.size() == 0)
		{
			return UNIQUESET; 
		}
		for (BigDecimal mode : modeList)
		{
			if (!first)
			{
				sb.append(", "); 
			}
			else
			{
				first = false; 
			}
			String currentMode = convertBigDecimalStatToString(gradebook, mode, false);
			sb.append(currentMode);
		}
		
		modeString = sb.toString(); 
		sb = null;
		return modeString;

	}

	/*
	 * UTILITY HELPER METHODS
	 */
	private String concat(String... vars) {

		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < vars.length; i++) {
			builder.append(vars[i]);
		}

		return builder.toString();
	}

	private String convertBigDecimalStatToString(Gradebook gradebook, BigDecimal stat, boolean isStandardDev) {
		String statAsString = null;
		
		switch (gradebook.getGrade_type()) {
		case GradebookService.GRADE_TYPE_LETTER:
			if (isStandardDev)
				statAsString = stat != null && stat.compareTo(BigDecimal.ZERO) != 0 ? stat.divide(BigDecimal.valueOf(10), new MathContext(AppConstants.DISPLAY_SCALE, RoundingMode.HALF_EVEN)).toString() : NA;
			else
				statAsString = stat != null ? gradeCalculations.convertPercentageToLetterGrade(stat) : NA;
			break;
		default:
			statAsString = stat != null ? stat.setScale(AppConstants.DISPLAY_SCALE, GradeCalculations.MATH_CONTEXT.getRoundingMode()).toString() : NA;
		}
		
		return statAsString;
	}

	private Boolean convertBoolean(Object value) {

		return value == null ? Boolean.FALSE : (Boolean) value;
	}
	

	private Date convertDate(Object value) {

		Date dueDate = null;
		if (value instanceof Date) 
			dueDate = (Date)value;
		else if (value instanceof Long) {
			Long dueDateMillis = (Long)value;
			dueDate = dueDateMillis == null ? null : new Date(dueDateMillis.longValue());
		}
		
		return dueDate;
	}
	
	private Double convertDouble(Object value) {

		return value == null ? Double.valueOf(0.0) : (Double) value;
	}
	
	private Integer convertInteger(Object value) {

		return value == null ? Integer.valueOf(0) : (Integer) value;
	}

	private String convertString(Object value) {

		return value == null ? "" : (String) value;
	}
	
	private ActionRecord createActionRecord(String name, String id, Gradebook gradebook, Item item, String entityType, String actionType) {
		ActionRecord actionRecord = new ActionRecord(gradebook.getUid(), gradebook.getId(), entityType, actionType);
		actionRecord.setEntityName(name);
		if (id != null)
			actionRecord.setEntityId(id);
	
		Map<String, String> propertyMap = actionRecord.getPropertyMap();
		for (String propertyName : item.getPropertyNames()) { 
			// Don't store the children since this is redundant and will be too long of a string anyway
			if (propertyName.equals(ItemKey.A_CHILDREN.name()))
				continue;
			
			Object value = item.get(propertyName);
			if (value != null) {
				String stringValue = String.valueOf(value);
				
				if (stringValue.length() >= 756) {
					log.warn("Trying to store a property with a value that's too long for the field, truncating the property " + propertyName + " : " + stringValue);
					stringValue = stringValue.substring(0, 755);
				}
				propertyMap.put(propertyName, stringValue);
			}
		}
		
		return actionRecord;
	}
	
	private org.sakaiproject.gradebook.gwt.client.model.Gradebook createGradebookModel(Gradebook gradebook, List<Assignment> assignments, List<Category> categories, boolean isNewGradebook) {

		log.debug("createGradebookModel() called"); 
		Site site = null;

		if (siteService != null) {
			try {
				site = siteService.getSite(getSiteContext());

				if (site.getId().equals(gradebook.getName())) {
					gradebook.setName(i18n.getString("defaultGradebookName"));
				}

			} catch (IdUnusedException e) {
				log.error("Unable to find the current site", e);
			}
		}


		org.sakaiproject.gradebook.gwt.client.model.Gradebook model = new GradebookImpl();
		String gradebookUid = gradebook.getUid();

		boolean isUserAbleToGradeAll = authz.isUserAbleToGradeAll(gradebookUid);
		boolean isUserAbleToGrade = authz.isUserAbleToGrade(gradebookUid);
		boolean isUserAbleToViewOwnGrades = authz.isUserAbleToViewOwnGrades(gradebookUid);

		boolean isSingleUserView = isUserAbleToViewOwnGrades && !isUserAbleToGrade;
		boolean isAnonymousView = !isUserAbleToViewOwnGrades && !isUserAbleToGrade;

		if (isAnonymousView)
			return model;
		
		model.setGradebookUid(gradebookUid);
		model.setGradebookId(gradebook.getId());
		model.setName(gradebook.getName());

		// GRBK-233 create new assignment and category list
		GradeItem gradebookGradeItem = getGradebookGradeItem(gradebook, assignments, categories, isSingleUserView);
		
		model.setNewGradebook(Boolean.valueOf(assignments == null || assignments.isEmpty()));
		model.setGradebookGradeItem(gradebookGradeItem);

		List<FixedColumn> columns = getColumns(isUserAbleToGradeAll);

		Configuration configModel = new ConfigurationImpl();
		
		if (userService != null) {
			User user = userService.getCurrentUser();

			if (user != null) {

				List<UserConfiguration> configs = gbService.getUserConfigurations(user.getId(), gradebook.getId());

				if (configs != null) {
					for (UserConfiguration config : configs) {
						configModel.set(config.getConfigField(), config.getConfigValue());
					}
					
					// FIXME: These two entries below are temporary fixes since due to the bug GRBK-293 there
					// may be some sites that were created without hidden calculated and letter grade entries
					// once we've moved far enough into the future past Fall 2010, we can remove these,
					// and clean installations can remove them immediately
					String calcGradeHiddenId = ConfigUtil.getColumnHiddenId(AppConstants.ITEMTREE, LearnerKey.S_CALC_GRD.name());
					if (configModel.get(calcGradeHiddenId) == null)
						configModel.set(calcGradeHiddenId, "true");
					
					String letterGradeHiddenId = ConfigUtil.getColumnHiddenId(AppConstants.ITEMTREE, LearnerKey.S_LTR_GRD.name());
					if (configModel.get(letterGradeHiddenId) == null)
						configModel.set(letterGradeHiddenId, "true");
				}

				List<String> legacySelectedMultiGradeColumns = configModel.getSelectedMultigradeColumns();

				if (legacySelectedMultiGradeColumns != null && !legacySelectedMultiGradeColumns.isEmpty()) {

					if (columns != null) {

						for (FixedColumn c : columns) {

							String identifier = c.getIdentifier();

							if (!identifier.equals(LearnerKey.S_LST_NM_FRST.name())
									&& !identifier.equals(LearnerKey.S_CRS_GRD.name())
									&& !identifier.equals(LearnerKey.S_OVRD_GRD.name())) {
								if (!legacySelectedMultiGradeColumns.contains(identifier)) {							
									gbService.createOrUpdateUserConfiguration(getCurrentUser(), gradebook.getId(), ConfigUtil.getColumnHiddenId(AppConstants.ITEMTREE, identifier), "true");
									configModel.setColumnHidden(AppConstants.ITEMTREE, identifier, Boolean.TRUE);
								} 
							}
						}


					}

					if (assignments != null) {

						for (Assignment a : assignments) {

							String identifier = String.valueOf(a.getId());

							if (!legacySelectedMultiGradeColumns.contains(identifier)) {								
								gbService.createOrUpdateUserConfiguration(getCurrentUser(), gradebook.getId(), ConfigUtil.getColumnHiddenId(AppConstants.ITEMTREE, identifier), "true");

								configModel.setColumnHidden(AppConstants.ITEMTREE, identifier, Boolean.TRUE);
							} 

						}

					}

					gbService.deleteUserConfiguration(getCurrentUser(), gradebook.getId(), AppConstants.SELECTED_COLUMNS);
				}


				// Don't take the hit of looking this stuff up unless we're in
				// single user view
				if (isSingleUserView) {
					UserRecord userRecord = buildUserRecord(site, user, gradebook);
					model.setUserAsStudent(buildStudentRow(gradebook, userRecord, columns, assignments, categories));
				}
				
				/*if (!isSingleUserView) {
					List<Statistics> statsList = generateStatsList(gradebook, user.getId(), assignments, categories);		
					Collections.sort(statsList, new Comparator<Statistics>() {
	
						public int compare(Statistics o1, Statistics o2) {
							if (o1 != null && o2 != null) {
								String id1 = o1.getAssignmentId();
								String id2 = o2.getAssignmentId();
								if (id1 != null && id2 != null) {
									return id1.compareTo(id2); 
								}
							}
							return -1;
						}
						
					}); 
					
					model.setStatsModel(statsList);
				}*/
				model.setUserName(user.getDisplayName());
			}
		} else {
			String[] realmIds = { "/site/mock" };
			List<UserRecord> userRecords = findLearnerRecordPage(gradebook, getSite(), realmIds, null, null, null, null, null, -1, -1, true, false);

			if (userRecords != null && userRecords.size() > 0) {
				UserRecord userRecord = userRecords.get(0);
				model.setUserName(userRecord.getDisplayName());
				model.setUserAsStudent(buildStudentRow(gradebook, userRecord, columns, assignments, categories));
			}
		}

		model.setConfigurationModel(configModel);
		model.setColumns(columns);

		return model;
	}

	private GradeItem createGradeItem(Category category, Assignment assignment, BigDecimal percentCourseGrade, BigDecimal percentCategory) {

		GradeItem model = new GradeItemImpl();

		double assignmentWeight = assignment.getAssignmentWeighting() == null ? 0d : assignment.getAssignmentWeighting().doubleValue() * 100.0;
		Boolean isAssignmentIncluded = Boolean.valueOf(assignment.isCounted());
		Boolean isAssignmentExtraCredit = assignment.isExtraCredit() == null ? Boolean.FALSE : assignment.isExtraCredit();
		Boolean isAssignmentReleased = Boolean.valueOf(assignment.isReleased());
		Boolean isAssignmentRemoved = Boolean.valueOf(assignment.isRemoved());
		//double points = assignment.getPointsPossible() == null ? 0d : assignment.getPointsPossible().doubleValue();
		
		
		Gradebook gradebook = assignment.getGradebook();
		boolean hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;
		boolean hasWeights = gradebook.getCategory_type() == GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY;
		boolean isLetterGrading = gradebook.getGrade_type() == GradebookService.GRADE_TYPE_LETTER;
		
		// We don't want to delete assignments based on category when we don't
		// have categories
		if (hasCategories && category != null) {

			if (category.isRemoved())
				isAssignmentRemoved = Boolean.TRUE;

			if (category.isUnweighted() != null && category.isUnweighted().booleanValue())
				isAssignmentIncluded = Boolean.FALSE;

			isAssignmentExtraCredit = Boolean.valueOf(Util.checkBoolean(isAssignmentExtraCredit) || Util.checkBoolean(category.isExtraCredit()));
		}

		String categoryName = gradebook.getName();
		if (hasCategories && category != null) {
			categoryName = category.getName();
			model.setCategoryName(categoryName);
			model.setCategoryId(category.getId());
		}

		model.setIdentifier(String.valueOf(assignment.getId()));
		model.setName(assignment.getName());
		model.setItemId(assignment.getId());
		model.setWeighting(Double.valueOf(assignmentWeight));
		model.setReleased(isAssignmentReleased);
		model.setIncluded(isAssignmentIncluded);
		model.setDueDate(assignment.getDueDate());
		model.setPoints(assignment.getPointsPossible());
		model.setExtraCredit(isAssignmentExtraCredit);
		model.setRemoved(isAssignmentRemoved);
		model.setSource(assignment.getExternalAppName());
		model.setNullsAsZeros(assignment.getCountNullsAsZeros());
		if (isLetterGrading)
			model.setDataType(AppConstants.STRING_DATA_TYPE);
		else
			model.setDataType(AppConstants.NUMERIC_DATA_TYPE);
		model.setStudentModelKey(LearnerKey.S_ITEM.name());
		model.setItemOrder(assignment.getSortOrder());
		
		if (hasWeights) {
			if (percentCategory != null)
				model.setPercentCategory(Double.valueOf(percentCategory.doubleValue()));
			
			if (percentCourseGrade != null)
				model.setPercentCourseGrade(Double.valueOf(percentCourseGrade.doubleValue()));
		} else {
			model.setPercentCategory(null);
			model.setPercentCourseGrade(null);
		}
		
		model.setItemType(ItemType.ITEM);

		return model;
	}

	private GradeItem createGradeItem(Gradebook gradebook) {

		GradeItem itemModel = new GradeItemImpl();
		itemModel.setName(gradebook.getName());
		itemModel.setItemType(ItemType.GRADEBOOK);
		itemModel.setIdentifier(gradebook.getUid());

		switch (gradebook.getCategory_type()) {
			case GradebookService.CATEGORY_TYPE_NO_CATEGORY:
				itemModel.setCategoryType(CategoryType.NO_CATEGORIES);
				break;
			case GradebookService.CATEGORY_TYPE_ONLY_CATEGORY:
				itemModel.setCategoryType(CategoryType.SIMPLE_CATEGORIES);
				break;
			case GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY:
				itemModel.setCategoryType(CategoryType.WEIGHTED_CATEGORIES);
				break;
		}
		switch (gradebook.getGrade_type()) {
			case GradebookService.GRADE_TYPE_POINTS:
				itemModel.setGradeType(GradeType.POINTS);
				break;
			case GradebookService.GRADE_TYPE_PERCENTAGE:
				itemModel.setGradeType(GradeType.PERCENTAGES);
				break;
			case GradebookService.GRADE_TYPE_LETTER:
				itemModel.setGradeType(GradeType.LETTERS);
				break;
		}

		itemModel.setReleaseGrades(Boolean.valueOf(gradebook.isCourseGradeDisplayed()));
		itemModel.setReleaseItems(Boolean.valueOf(gradebook.isAssignmentsDisplayed()));
		itemModel.setGradeScaleId(gradebook.getSelectedGradeMapping().getId());
		itemModel.setExtraCreditScaled(gradebook.isScaledExtraCredit());
		itemModel.setShowMean(gradebook.getShowMean());
		itemModel.setShowMedian(gradebook.getShowMedian());
		itemModel.setShowMode(gradebook.getShowMode());
		itemModel.setShowRank(gradebook.getShowRank());
		itemModel.setShowItemStatistics(gradebook.getShowItemStatistics());
		
		Boolean isScaledExtraCreditEnabled = Boolean.FALSE;
		if (stateOfScaledExtraCredit != null) {
			switch (stateOfScaledExtraCredit) {
			case ADMIN_ONLY:
				isScaledExtraCreditEnabled = Boolean.valueOf(authz.isAdminUser());
				break;
			case INSTRUCTOR_ONLY:
				isScaledExtraCreditEnabled = Boolean.valueOf(authz.isUserAbleToGradeAll(gradebook.getUid()));
				break;
			}
		}
		
		if (isScaledExtraCreditEnabled.booleanValue() && limitScaledExtraCreditToCategoryType != null) {
			CategoryType categoryType = null;
			
			switch (gradebook.getCategory_type()) {
				case GradebookService.CATEGORY_TYPE_NO_CATEGORY:
					categoryType = CategoryType.NO_CATEGORIES;
					break;
				case GradebookService.CATEGORY_TYPE_ONLY_CATEGORY:
					categoryType = CategoryType.SIMPLE_CATEGORIES;
					break;
				case GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY:
					categoryType = CategoryType.WEIGHTED_CATEGORIES;
			}
			
			isScaledExtraCreditEnabled = Boolean.valueOf(categoryType == limitScaledExtraCreditToCategoryType);
		}
		
		itemModel.setScaledExtraCreditEnabled(isScaledExtraCreditEnabled);
		
		return itemModel;
	}

	private GradeItem createGradeItem(Gradebook gradebook, Category category, List<Assignment> assignments) {

		GradeItem model = new GradeItemImpl();

		boolean isDefaultCategory = category.getName().equalsIgnoreCase(AppConstants.DEFAULT_CATEGORY_NAME);

		double categoryWeight = category.getWeight() == null ? 0d : category.getWeight().doubleValue() * 100d;
		boolean isIncluded = category.isUnweighted() == null ? !isDefaultCategory : !isDefaultCategory && !category.isUnweighted().booleanValue();

		boolean hasWeights = true;
		
		if (gradebook != null) {
			model.setGradebook(gradebook.getName());
			hasWeights = gradebook.getCategory_type() == GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY;
		}
		model.setIdentifier(String.valueOf(category.getId()));
		model.setName(category.getName());
		model.setCategoryId(category.getId());
		model.setWeighting(Double.valueOf(categoryWeight));
		model.setEqualWeightAssignments(category.isEqualWeightAssignments());
		model.setExtraCredit(category.isExtraCredit() == null ? Boolean.FALSE : category.isExtraCredit());
		model.setIncluded(Boolean.valueOf(isIncluded));
		model.setDropLowest(category.getDrop_lowest() == 0 ? null : Integer.valueOf(category.getDrop_lowest()));
		model.setRemoved(Boolean.valueOf(category.isRemoved()));
		model.setReleased(Boolean.valueOf(businessLogic.checkReleased(assignments)));
		if (hasWeights)
			model.setPercentCourseGrade(Double.valueOf(categoryWeight));
		model.setItemType(ItemType.CATEGORY);
		model.setEditable(!isDefaultCategory);
		model.setItemOrder(category.getCategoryOrder());
		model.setEnforcePointWeighting(category.isEnforcePointWeighting());

		return model;
	}

	private org.sakaiproject.gradebook.gwt.client.model.Gradebook retrieveGradebookModel(String gradebookUid) 
	throws GradebookCreationException {

		org.sakaiproject.gradebook.gwt.client.model.Gradebook model = null;
		Gradebook gradebook = null;

		boolean isNewGradebook = false;

		try {
			// First thing, grab the default gradebook if one exists
			gradebook = gbService.getGradebook(gradebookUid);
			
			// The following statements are to absolutely ensure that we have a fully
			// populated gradebook -- that the Framework Service has finished initializing
			// one we have.
			if (gradebook.getSelectedGradeMapping() == null)
				throw new GradebookNotFoundException("");
			
			if (gradebook.getGrade_type() <= 0)
				throw new GradebookNotFoundException("");
			
			if (gradebook.getCategory_type() <= 0)
				throw new GradebookNotFoundException("");
			
		} catch (GradebookNotFoundException gnfe) {
			throw new GradebookCreationException("Need to wait for first call to complete");
		}

		// If we have a gradebook already, then we have to ensure that it's set
		// up correctly for the new tool
		if (gradebook != null) {

			List<Assignment> assignments = gbService.getAssignments(gradebook.getId());

			List<Category> categories = null;
			if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY)
				categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);

			isNewGradebook = assignments != null && assignments.size() > 0;
			
			model = createGradebookModel(gradebook, assignments, categories, isNewGradebook);
		}

		return model;
	}
	
	private Statistics createStatisticsModel(Gradebook gradebook, String name, GradeStatistics statistics, Long id, Long assignmentId, String studentId) {

		Statistics model = new StatisticsImpl();
		model.setId(String.valueOf(id));
		model.setAssignmentId(String.valueOf(assignmentId));
		model.setName(name);
		
		String mean = statistics != null ? convertBigDecimalStatToString(gradebook, statistics.getMean(), false) : NA;
		String median = statistics != null ? convertBigDecimalStatToString(gradebook, statistics.getMedian(), false) : NA;
		String mode = 	statistics != null ? composeModeString(statistics, gradebook) : NA;
		String standardDev = statistics != null ? convertBigDecimalStatToString(gradebook, statistics.getStandardDeviation(), true) : NA;
		String rank = NA;  

		boolean isStudentView = studentId != null;
		boolean isCourseStats = id != null && id.equals(Long.valueOf(0l));
		
		if (studentId != null && statistics != null)
		{
			StringBuilder sb = new StringBuilder();
			if (statistics.getRank() > 0)
			{
				sb.append(statistics.getRank());
			}
			else
			{
				sb.append("N/A");
			}
			sb.append(" out of "); 
			sb.append(statistics.getStudentTotal());
			rank = sb.toString();
			sb = null; 
		}
		
		boolean isShowCourseStats = gradebook.isCourseGradeDisplayed() && isCourseStats;
		boolean isShowItemStats = Util.checkBoolean(gradebook.getShowItemStatistics()) && !isCourseStats;
		boolean isShowMean = Util.checkBoolean(gradebook.getShowMean());
    	boolean isShowMedian = Util.checkBoolean(gradebook.getShowMedian());
    	boolean isShowMode = Util.checkBoolean(gradebook.getShowMode());
    	boolean isShowRank = Util.checkBoolean(gradebook.getShowRank());
		
    	if (!isStudentView || isShowCourseStats || isShowItemStats) {
			if (!isStudentView || isShowMean) {
				model.setMean(mean);
				model.setStandardDeviation(standardDev);	
			}
			
			if (!isStudentView || isShowMedian)
				model.setMedian(median);
			
			if (!isStudentView || isShowMode)
				model.setMode(mode);
			
			if (!isStudentView || isShowRank)
				model.setRank(rank); 
    	}
    	
		return model;
	}

	private Long doCreateItem(Gradebook gradebook, Item item, boolean hasCategories, boolean enforceNoNewCategories) throws BusinessRuleException {

		String name = item.getName();
		
		
		Category category = null;
		Long assignmentId = null;

		List<Assignment> assignments = null;
		Long categoryId = null;

		String actionRecordStatus = ActionRecord.STATUS_SUCCESS;
		
		try {
			boolean includeInGrade = Util.checkBoolean(item.getIncluded());

			categoryId = item.getCategoryId();
			String categoryName = item.getCategoryName();
			Double weight = item.getPercentCategory();
			Double points = item.getPoints();
			Boolean isReleased = Boolean.valueOf(Util.checkBoolean(item.getReleased()));
			Boolean isIncluded = Boolean.valueOf(includeInGrade);
			Boolean isExtraCredit = Boolean.valueOf(Util.checkBoolean(item.getExtraCredit()));
			Boolean isNullsAsZeros = Boolean.valueOf(Util.checkBoolean(item.getNullsAsZeros()));
			Date dueDate = item.getDueDate();
			Integer itemOrder = item.getItemOrder();

			// Business rule #1
			if (points == null)
				points = new Double(100d);
			// Business rule #2
			if (weight == null)
				weight = Double.valueOf(points.doubleValue());

			if (hasCategories && categoryId == null && categoryName != null 
					&& categoryName.trim().length() > 0) {
				GradeItem newCategory = new GradeItemImpl();
				newCategory.setName(categoryName);
				newCategory.setIncluded(Boolean.TRUE);
				newCategory = getActiveItem((GradeItem)addItemCategory(gradebook.getUid(), gradebook.getId(), newCategory));
				categoryId = newCategory.getCategoryId();
				//item.setCategoryId(categoryId);
				//hasNewCategory = true;
			}

			if (categoryId == null || categoryId.equals(Long.valueOf(-1l))) {
				category = findDefaultCategory(gradebook.getId());
				categoryId = null;
			}

			if (category == null && categoryId != null)
				category = gbService.getCategory(categoryId);

			// Apply business rules before item creation
			if (hasCategories) {
				if (categoryId != null) {
					assignments = gbService.getAssignmentsForCategory(categoryId);
					// Business rule #4
					businessLogic.applyNoDuplicateItemNamesWithinCategoryRule(categoryId, name, null, assignments);
					// Business rule #6
					if (enforceNoNewCategories)
						businessLogic.applyMustIncludeCategoryRule(categoryId);
				}

			} else {
				assignments = gbService.getAssignments(gradebook.getId());
				businessLogic.applyNoDuplicateItemNamesRule(gradebook.getId(), name, null, assignments);
			}
			
			businessLogic.applyNoZeroPointItemsRule(points);

			if (itemOrder == null)
				itemOrder = assignments == null || assignments.isEmpty() ? Integer.valueOf(0) : Integer.valueOf(assignments.size());

				double w = weight == null ? 0d : ((Double) weight).doubleValue() * 0.01;

				assignmentId = gbService.createAssignmentForCategory(gradebook.getId(), categoryId, name, points, Double.valueOf(w), dueDate, Boolean.valueOf(!Util.checkBoolean(isIncluded)), isExtraCredit, Boolean.FALSE,
						isReleased, itemOrder, isNullsAsZeros);

				// Apply business rules after item creation
				if (hasCategories) {
					assignments = gbService.getAssignmentsForCategory(categoryId);
					// Business rule #5
					if (businessLogic.checkRecalculateEqualWeightingRule(category))
						recalculateAssignmentWeights(category, Boolean.FALSE, assignments);
				}

		} catch (RuntimeException e) {
			actionRecordStatus = ActionRecord.STATUS_FAILURE;
			throw e;
		} finally {
			String id = assignmentId == null ? null : String.valueOf(assignmentId);
			ActionRecord actionRecord = createActionRecord(name, id, gradebook, item, EntityType.ITEM.name(), ActionType.CREATE.name());
			actionRecord.setStatus(actionRecordStatus);
			gbService.storeActionRecord(actionRecord);
		}

		return assignmentId;
	}

	private List<UserRecord> doSearchAndSortUserRecords(Gradebook gradebook, List<Assignment> assignments, List<Category> categories, List<String> studentUids, Map<String, UserRecord> userRecordMap, String searchString, String columnId, boolean isDescending, LearnerKey sortColumnKey) {

		List<UserRecord> userRecords = null;

		// Check to see if we're sorting or not
		if (sortColumnKey != null) {
			switch (sortColumnKey) {
				case S_DSPLY_NM:
				case S_LST_NM_FRST:
				case S_DSPLY_ID:
				case S_SECT:
				case S_EMAIL:
					if (userRecords == null) {
						userRecords = doSearchUsers(searchString, studentUids, userRecordMap);
					}
					break;
				case S_CRS_GRD:
				case S_LTR_GRD:
				case S_CALC_GRD:
				case S_OVRD_GRD:
				case S_ITEM:
					if (userRecords == null) {
						userRecords = new ArrayList<UserRecord>(userRecordMap.values());
					}
					break;
			}

			Comparator<UserRecord> comparator = null;
			switch (sortColumnKey) {
				case S_DSPLY_NM:
				case S_LST_NM_FRST:
					comparator = SORT_NAME_COMPARATOR;
					break;
				case S_DSPLY_ID:
					comparator = DISPLAY_ID_COMPARATOR;
					break;
				case S_EMAIL:
					comparator = EMAIL_COMPARATOR;
					break;
				case S_SECT:
					comparator = SECTION_TITLE_COMPARATOR;
					break;
				case S_CALC_GRD:
					// In this case we need to ensure that we've calculated
					// everybody's course grade
					if (userRecords != null) {
						for (UserRecord record : userRecords) {
							BigDecimal calculatedGrade = getCalculatedGrade(gradebook, assignments, categories, record.getGradeRecordMap());
							DisplayGrade displayGrade = getDisplayGrade(gradebook, record.getUserUid(), record.getCourseGradeRecord(), calculatedGrade);
							record.setDisplayGrade(displayGrade);
							record.setCalculated(true);
						}
					}
					comparator = new CalculatedGradeComparator(isDescending);
					break;
				case S_LTR_GRD:
				case S_CRS_GRD:
					// In this case we need to ensure that we've calculated
					// everybody's course grade
					if (userRecords != null) {
						for (UserRecord record : userRecords) {
							BigDecimal calculatedGrade = getCalculatedGrade(gradebook, assignments, categories, record.getGradeRecordMap());
							DisplayGrade displayGrade = getDisplayGrade(gradebook, record.getUserUid(), record.getCourseGradeRecord(), calculatedGrade);
							record.setDisplayGrade(displayGrade);
							record.setCalculated(true);
						}
					}
					comparator = new CourseGradeComparator(isDescending);
					break;
				case S_OVRD_GRD:
					comparator = new EnteredGradeComparator(isDescending);
					break;
				case S_ITEM:
					if (columnId != null && !columnId.equals("")) {
						Long assignmentId = Long.valueOf(columnId);
						comparator = new AssignmentComparator(assignmentId, isDescending);
					}
					break;
			}

			if (comparator != null) {
				if (isDescending)
					comparator = Collections.reverseOrder(comparator);

				Collections.sort(userRecords, comparator);
			}
		}

		if (userRecords == null) {
			// Of course, we need to do this regardless or it will be null
			// This is pretty silly on one level, since it means that we don't
			// take advantage of the database to do this, but it's equivalent to
			// what
			// section awareness is doing behind the scenes and it gives us more
			// control over the process
			if (searchString != null)
				userRecords = doSearchUsers(searchString, studentUids, userRecordMap);
			else
				userRecords = new ArrayList<UserRecord>(userRecordMap.values());

			// This seems a little stupid, but the fact of the matter is that we
			// get an unordered list
			// back from the Map.keySet call, so we do want to ensure that we
			// get the same order each time
			// even when the user has not chosen to sort
			Collections.sort(userRecords, DEFAULT_ID_COMPARATOR);
		}

		return userRecords;
	}

	private List<UserRecord> doSearchUsers(String searchString, List<String> studentUids, Map<String, UserRecord> userRecordMap) {

		// Make sure that our search criterion is case insensitive
		if (searchString != null)
			searchString = searchString.toUpperCase();

		List<UserRecord> userRecords = new ArrayList<UserRecord>();

		// To do a search, we have to get all the users . . . this is also
		// desirable even if we're not searching, if we want to sort on these
		// properties
		List<User> users = userService.getUsers(studentUids);

		if (users != null) {
			for (User user : users) {
				String lastName = user.getLastName() == null ? "" : user.getLastName();
				String firstName = user.getFirstName() == null ? "" : user.getFirstName();

				String sortName = new StringBuilder().append(lastName.toUpperCase()).append("::").append(firstName.toUpperCase()).toString();
				// Make sure that our search field is case insensitive
				if (sortName != null)
					sortName = sortName.toUpperCase();

				// If we're not searching, then return everybody
				if (searchString == null || sortName.contains(searchString)) {
					UserRecord userRecord = userRecordMap.get(user.getId());
					if (userRecord != null) {
						userRecord.populate(user);
						userRecords.add(userRecord);
					}
				}
			}
		}

		return userRecords;
	}

	private List<User> findAllMembers(Site site, String[] learnerRoleKeys) {

		List<User> users = new ArrayList<User>();
		if (site != null) {
			List<String> userUids = gbService.getFullUserListForSite(site.getId(), learnerRoleKeys);

			if (userService != null && userUids != null)
				users = userService.getUsers(userUids);
		}
		return users;
	}

	private synchronized Category findDefaultCategory(Long gradebookId) {

		Category category = new Category();
		category.setName(AppConstants.DEFAULT_CATEGORY_NAME);
		category.setWeight(Double.valueOf(0d));
		category.setExtraCredit(Boolean.FALSE);
		category.setUnweighted(Boolean.TRUE);
		category.setId(Long.valueOf(-1l));

		return category;

	}

	private List<UserRecord> findLearnerRecordPage(Gradebook gradebook, Site site, String[] realmIds, List<String> groupReferences, Map<String, Group> groupReferenceMap, String sortField, String searchField, String searchCriteria,
			int offset, int limit, boolean isAscending, boolean includeCMId) {

		String[] learnerRoleKeys = getLearnerRoleNames();
		verifyUserDataIsUpToDate(site, learnerRoleKeys);

		List<UserDereference> dereferences = gbService.getUserDereferences(realmIds, sortField, searchField, searchCriteria, offset, limit, isAscending, learnerRoleKeys);
		List<AssignmentGradeRecord> allGradeRecords = gbService.getAllAssignmentGradeRecords(gradebook.getId(), realmIds, learnerRoleKeys);
		Map<String, Map<Long, AssignmentGradeRecord>> studentGradeRecordMap = new HashMap<String, Map<Long, AssignmentGradeRecord>>();

		if (allGradeRecords != null) {
			for (AssignmentGradeRecord gradeRecord : allGradeRecords) {
				gradeRecord.setUserAbleToView(true);
				String studentUid = gradeRecord.getStudentId();
				Map<Long, AssignmentGradeRecord> studentMap = studentGradeRecordMap.get(studentUid);
				if (studentMap == null) {
					studentMap = new HashMap<Long, AssignmentGradeRecord>();
				}
				GradableObject go = gradeRecord.getGradableObject();
				studentMap.put(go.getId(), gradeRecord);

				studentGradeRecordMap.put(studentUid, studentMap);
			}
		}

		List<CourseGradeRecord> courseGradeRecords = gbService.getAllCourseGradeRecords(gradebook); //, realmIds, sortField, searchField, searchCriteria, offset, limit, isAscending, learnerRoleKeys);
		Map<String, CourseGradeRecord> studentCourseGradeRecordMap = new HashMap<String, CourseGradeRecord>();

		if (courseGradeRecords != null) {
			for (CourseGradeRecord courseGradeRecord : courseGradeRecords) {
				String studentUid = courseGradeRecord.getStudentId();
				studentCourseGradeRecordMap.put(studentUid, courseGradeRecord);
			}
		}

		List<Comment> comments = gbService.getComments(gradebook.getId()); //, realmIds, learnerRoleKeys, sortField, searchField, searchCriteria, offset, limit, isAscending);
		Map<String, Map<Long, Comment>> studentItemCommentMap = new HashMap<String, Map<Long, Comment>>();

		if (comments != null) {
			for (Comment comment : comments) {
				String studentUid = comment.getStudentId();
				Map<Long, Comment> commentMap = studentItemCommentMap.get(studentUid);
				if (commentMap == null)
					commentMap = new HashMap<Long, Comment>();

				commentMap.put(comment.getGradableObject().getId(), comment);
				studentItemCommentMap.put(studentUid, commentMap);
			}
		}

		Map<String, Set<Group>> userGroupMap = new HashMap<String, Set<Group>>();
		Map<String, List<String>> groupEnrollmentSetEidsMap = new HashMap<String, List<String>>();

		List<Object[]> tuples = gbService.getUserGroupReferences(groupReferences, learnerRoleKeys);

		if (tuples != null) {
			for (Object[] tuple : tuples) {
				String userUid = (String) tuple[0];
				String realmId = (String) tuple[1];

				Group group = groupReferenceMap.get(realmId);

				if (includeCMId && advisor.isExportCourseManagementIdByGroup()) {
					List<String> enrollmentSetEids = advisor.getExportCourseManagementSetEids(group);
					if (enrollmentSetEids != null)
						groupEnrollmentSetEidsMap.put(group.getId(), enrollmentSetEids);
				}

				Set<Group> userGroups = userGroupMap.get(userUid);

				if (userGroups == null) {
					userGroups = new HashSet<Group>();
					userGroupMap.put(userUid, userGroups);
				}

				userGroups.add(group);
			}
		}

		List<UserRecord> userRecords = new ArrayList<UserRecord>();
		if (dereferences != null) {
			for (UserDereference dereference : dereferences) {
				UserRecord userRecord = new UserRecord(dereference.getUserUid(), dereference.getEid(), dereference.getDisplayId(), dereference.getDisplayName(), dereference.getLastNameFirst(), dereference.getSortName(), dereference
						.getEmail());

				Map<Long, AssignmentGradeRecord> gradeRecordMap = studentGradeRecordMap.get(dereference.getUserUid());
				userRecord.setGradeRecordMap(gradeRecordMap);
				CourseGradeRecord courseGradeRecord = studentCourseGradeRecordMap.get(dereference.getUserUid());
				userRecord.setCourseGradeRecord(courseGradeRecord);
				Map<Long, Comment> commentMap = studentItemCommentMap.get(dereference.getUserUid());
				userRecord.setCommentMap(commentMap);

				Set<Group> userGroupSet = userGroupMap.get(userRecord.getUserUid());
				if (userGroupSet != null) {
					List<Group> userGroups = new ArrayList<Group>(userGroupSet);
					Collections.sort(userGroups, new Comparator<Group>() {

						public int compare(Group o1, Group o2) {

							if (o1 != null && o2 != null && o1.getTitle() != null && o2.getTitle() != null) {
								return o1.getTitle().compareTo(o2.getTitle());
							}

							return 0;
						}

					});
					StringBuilder groupTitles = new StringBuilder();
					StringBuilder courseManagementIds = new StringBuilder();
					for (Iterator<Group> groupIter = userGroups.iterator(); groupIter.hasNext();) {
						Group group = groupIter.next();
						groupTitles.append(group.getTitle());

						if (includeCMId) {
							List<String> enrollmentSetEids = groupEnrollmentSetEidsMap.get(group.getId());
							courseManagementIds.append(advisor.getExportCourseManagementId(userRecord.getUserEid(), group, enrollmentSetEids));
						}

						if (groupIter.hasNext()) {
							groupTitles.append(",");
							courseManagementIds.append(",");
						}
					}
					userRecord.setSectionTitle(groupTitles.toString());

					if (includeCMId)
						userRecord.setExportCourseManagementId(courseManagementIds.toString());
				}
				if (includeCMId) {
					userRecord.setExportUserId(advisor.getExportUserId(dereference));
					userRecord.setFinalGradeUserId(advisor.getFinalGradeUserId(dereference));
				}

				userRecords.add(userRecord);
			}
		}

		return userRecords;
	}

	private List<Statistics> generateStatsList(Gradebook gradebook, String studentId, List<Assignment> assignments, List<Category> categories) {
		
		boolean hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;
		int gradeType = gradebook.getGrade_type();

		String siteId = getSiteId();

		String[] realmIds = new String[1];
		realmIds[0] = new StringBuffer().append("/site/").append(siteId).toString();

		String[] learnerRoleNames = getLearnerRoleNames();

		List<AssignmentGradeRecord> allGradeRecords = gbService.getAllAssignmentGradeRecords(gradebook.getId(), realmIds, learnerRoleNames);
		Map<String, Map<Long, AssignmentGradeRecord>> studentGradeRecordMap = new HashMap<String, Map<Long, AssignmentGradeRecord>>();

		List<String> gradedStudentUids = new ArrayList<String>();

		Map<Long, BigDecimal> assignmentSumMap = new HashMap<Long, BigDecimal>();
		Map<Long, List<StudentScore>> assignmentGradeListMap = new HashMap<Long, List<StudentScore>>();

		if (allGradeRecords != null) {
			for (AssignmentGradeRecord gradeRecord : allGradeRecords) {
				gradeRecord.setUserAbleToView(true);
				String studentUid = gradeRecord.getStudentId();
				Map<Long, AssignmentGradeRecord> studentMap = studentGradeRecordMap.get(studentUid);
				if (studentMap == null) {
					studentMap = new HashMap<Long, AssignmentGradeRecord>();
				}
				GradableObject go = gradeRecord.getGradableObject();
				studentMap.put(go.getId(), gradeRecord);

				BigDecimal value = null;
				if (gradeRecord.getPointsEarned() != null) {
					Assignment assignment = (Assignment) gradeRecord.getGradableObject();
					switch (gradeType) {
						case GradebookService.GRADE_TYPE_POINTS:
							value = BigDecimal.valueOf(gradeRecord.getPointsEarned().doubleValue());
							break;
						case GradebookService.GRADE_TYPE_PERCENTAGE:
						case GradebookService.GRADE_TYPE_LETTER:
							value = gradeCalculations.getPointsEarnedAsPercent(assignment, gradeRecord);
							break;
					}

					if (value != null && assignment != null) {
						Long assignmentId = assignment.getId();
						List<StudentScore> gradeList = assignmentGradeListMap.get(assignmentId);
						if (gradeList == null) {
							gradeList = new ArrayList<StudentScore>();
							assignmentGradeListMap.put(assignmentId, gradeList);
						}
						
						gradeList.add(new StudentScore(gradeRecord.getStudentId(), value));

						BigDecimal itemSum = assignmentSumMap.get(assignmentId);
						if (itemSum == null)
							itemSum = BigDecimal.ZERO;
						itemSum = itemSum.add(value);
						assignmentSumMap.put(assignmentId, itemSum);
					}

				}
				studentGradeRecordMap.put(studentUid, studentMap);

				if (!gradedStudentUids.contains(studentUid))
					gradedStudentUids.add(studentUid);
			}
		}

		// Now we can calculate the mean course grade
		List<StudentScore> courseGradeList = new ArrayList<StudentScore>();
		BigDecimal sumCourseGrades = BigDecimal.ZERO;
		for (String studentUid : gradedStudentUids) {
			Map<Long, AssignmentGradeRecord> studentMap = studentGradeRecordMap.get(studentUid);
			BigDecimal courseGrade = null;
			
			boolean isScaledExtraCredit = Util.checkBoolean(gradebook.isScaledExtraCredit());
			switch (gradebook.getCategory_type()) {
				case GradebookService.CATEGORY_TYPE_NO_CATEGORY:
					courseGrade = gradeCalculations.getCourseGrade(gradebook, assignments, studentMap, isScaledExtraCredit);
					break;
				default:
					courseGrade = gradeCalculations.getCourseGrade(gradebook, categories, studentMap, isScaledExtraCredit);
			}

			if (courseGrade != null) {
				sumCourseGrades = sumCourseGrades.add(courseGrade);
				courseGradeList.add(new StudentScore(studentUid, courseGrade));
			}
		} 

		GradeStatistics courseGradeStatistics = gradeCalculations.calculateStatistics(courseGradeList, sumCourseGrades, studentId);

		List<Statistics> statsList = new ArrayList<Statistics>();

		long id = 0;
		statsList.add(createStatisticsModel(gradebook, "Course Grade", courseGradeStatistics, Long.valueOf(id), Long.valueOf(-1), studentId));
		id++;

		if (assignments != null) {
			
			if (hasCategories) {
				if (categories != null) {
					for (Category category : categories) {
						List<Assignment> asns = getUncheckedAssignmentList(category);
						if (asns != null) {
							for (Assignment a : asns) {
								Long assignmentId = a.getId();
								String name = a.getName();
				
								List<StudentScore> gradeList = assignmentGradeListMap.get(assignmentId);
								BigDecimal sum = assignmentSumMap.get(assignmentId);
				
								GradeStatistics assignmentStatistics = null;
								if (gradeList != null && sum != null) {
									assignmentStatistics = gradeCalculations.calculateStatistics(gradeList, sum, studentId);
								}
								
								statsList.add(createStatisticsModel(gradebook, name, assignmentStatistics, Long.valueOf(id), assignmentId, studentId));
								id++;
							}
						}
					}
				}
			} else {
			
				for (Assignment assignment : assignments) {
					Long assignmentId = assignment.getId();
					String name = assignment.getName();
	
					List<StudentScore> gradeList = assignmentGradeListMap.get(assignmentId);
					BigDecimal sum = assignmentSumMap.get(assignmentId);
	
					GradeStatistics assignmentStatistics = null;
					if (gradeList != null && sum != null) {
						assignmentStatistics = gradeCalculations.calculateStatistics(gradeList, sum, studentId);
					}
					
					statsList.add(createStatisticsModel(gradebook, name, assignmentStatistics, Long.valueOf(id), assignmentId, studentId));
					id++;
				}
			}
		}

		return statsList; 
	}

	private GradeItem getActiveItem(GradeItem parent) {

		if (parent.isActive())
			return parent;

		for (GradeItem m : parent.getChildren()) {
			GradeItem c = (GradeItem)m;
			if (c.isActive()) {
				return c;
			}

			if (c.getChildCount() > 0) {
				GradeItem activeItem = getActiveItem(c);

				if (activeItem != null)
					return activeItem;
			}
		}

		return null;
	}

	private AuthModel getAuthorization(String... gradebookUids) {
		
		if (gradebookUids == null || gradebookUids.length == 0)
			gradebookUids = new String[] { lookupDefaultGradebookUid() };

		for (int i = 0; i < gradebookUids.length; i++) {
			boolean isNewGradebook = false;
			Gradebook gradebook = null;
			try {
				// First thing, grab the default gradebook if one exists
				gradebook = gbService.getGradebook(gradebookUids[i]);
			} catch (GradebookNotFoundException gnfe) {
				// If it doesn't exist, then create it
				if (frameworkService != null) {
					frameworkService.addGradebook(gradebookUids[i], i18n.getString("defaultGradebookName"));
					gradebook = gbService.getGradebook(gradebookUids[i]);
					isNewGradebook = true;
				} 
			}
			AuthModel authModel = new AuthModel();
			boolean isUserAbleToGrade = authz.isUserAbleToGrade(gradebookUids[i]);
			boolean isUserAbleToViewOwnGrades = authz.isUserAbleToViewOwnGrades(gradebookUids[i]);

			authModel.setUserAbleToGrade(Boolean.valueOf(isUserAbleToGrade));
			authModel.setUserAbleToEditAssessments(Boolean.valueOf(authz.isUserAbleToEditAssessments(gradebookUids[i])));
			authModel.setUserAbleToViewOwnGrades(Boolean.valueOf(isUserAbleToViewOwnGrades));
			authModel.setUserHasGraderPermissions(Boolean.valueOf(authz.hasUserGraderPermissions(gradebook.getUid())));
			authModel.setNewGradebook(Boolean.valueOf(isNewGradebook));
			authModel.setOldImport(Boolean.valueOf(isOldImport));
			authModel.setPlacementId(getPlacementId());

			return authModel;
		}
		return null;
	}

	private BigDecimal getCalculatedGrade(Gradebook gradebook, List<Assignment> assignments, List<Category> categories, Map<Long, AssignmentGradeRecord> studentGradeMap) {
		BigDecimal autoCalculatedGrade = null;

		boolean isScaledExtraCredit = Util.checkBoolean(gradebook.isScaledExtraCredit());
		
		switch (gradebook.getCategory_type()) {
			case GradebookService.CATEGORY_TYPE_NO_CATEGORY:
				autoCalculatedGrade = gradeCalculations.getCourseGrade(gradebook, assignments, studentGradeMap, isScaledExtraCredit);
				break;
			default:
				autoCalculatedGrade = gradeCalculations.getCourseGrade(gradebook, categories, studentGradeMap, isScaledExtraCredit);
		}

		if (autoCalculatedGrade != null)
			autoCalculatedGrade = autoCalculatedGrade.setScale(AppConstants.DISPLAY_SCALE, GradeCalculations.MATH_CONTEXT.getRoundingMode());

		return autoCalculatedGrade;
	}

	private List<Category> getCategoriesWithAssignments(Long gradebookId, List<Assignment> assignments, boolean includeEmpty) {

		Map<Long, Category> categoryMap = new HashMap<Long, Category>();
		List<Category> categories = null;

		Category defaultCategory = null;

		int categoryOrder = 0;

		// If we need to include categories that do not have any items under
		// them (as we do for the item tree), then we have to do an additional
		// query.
		if (includeEmpty) {
			categories = gbService.getCategories(gradebookId);

			if (categories != null) {
				for (Category category : categories) {
					if (!category.isRemoved()) {
						categoryMap.put(category.getId(), category);
						Integer order = category.getCategoryOrder();
						if (order == null)
							category.setCategoryOrder(Integer.valueOf(categoryOrder++));
					}
				}
			}
		}

		List<Assignment> assignmentList = null;

		if (assignments != null) {
			for (Assignment assignment : assignments) {
				Category category = null;

				if (assignment.isRemoved())
					continue;

				if (assignment.getCategory() != null)
					category = categoryMap.get(assignment.getCategory().getId());

				if (null == category) {

					category = assignment.getCategory();

					if (null == category) {
						if (defaultCategory == null) {
							defaultCategory = findDefaultCategory(gradebookId);
							if (categories != null) {
								categories.add(defaultCategory);
								defaultCategory.setCategoryOrder(Integer.valueOf(categoryOrder++));
							} else
								categoryMap.put(defaultCategory.getId(), defaultCategory);
						}

						category = defaultCategory;
					}
				}

				if (null != category) {

					assignmentList = getUncheckedAssignmentList(category);

					if (null == assignmentList) {
						assignmentList = new ArrayList<Assignment>();
						category.setAssignmentList(assignmentList);
					}

					if (!assignmentList.contains(assignment)) {
						Integer itemOrder = assignment.getSortOrder();
						if (itemOrder == null)
							itemOrder = Integer.valueOf(assignmentList.size());
						assignmentList.add(assignment);
					}
				}
			}
		}

		if (categories == null && categoryMap.size() > 0)
			categories = new ArrayList<Category>(categoryMap.values());

		return categories;
	}

	private List<FixedColumn> getColumns(boolean isUserAbleToGradeAll) {

		List<FixedColumn> columns = new ArrayList<FixedColumn>(10);

		columns.add(new FixedColumnImpl(LearnerKey.S_DSPLY_ID, i18n.getString("displayId"), 80, true));
		columns.add(new FixedColumnImpl(LearnerKey.S_DSPLY_NM, i18n.getString("displayName"), 180, true));
		columns.add(new FixedColumnImpl(LearnerKey.S_LST_NM_FRST, i18n.getString("lastNameFirst"), 180, false));
		columns.add(new FixedColumnImpl(LearnerKey.S_EMAIL, i18n.getString("email"), 230, true));
		columns.add(new FixedColumnImpl(LearnerKey.S_SECT, i18n.getString("section"), 120, true));
		columns.add(new FixedColumnImpl(LearnerKey.S_CRS_GRD, i18n.getString("courseGrade"), 140, false));
		if (isUserAbleToGradeAll) {
			FixedColumn gradeOverrideColumn = new FixedColumnImpl(LearnerKey.S_OVRD_GRD, i18n.getString("gradeOverride"), 120, false);
			gradeOverrideColumn.setEditable(Boolean.TRUE);
			columns.add(gradeOverrideColumn);
		}
		columns.add(new FixedColumnImpl(LearnerKey.S_LTR_GRD, i18n.getString("letterGrade"), 80, true));
		columns.add(new FixedColumnImpl(LearnerKey.S_CALC_GRD, i18n.getString("calculatedGrade"), 80, true));
		
		return columns;
	}
	
	
	/*
	 * DEPENDENCY INJECTION ACCESSORS
	 */

	private DisplayGrade getDisplayGrade(Gradebook gradebook, String studentUid, CourseGradeRecord courseGradeRecord, BigDecimal autoCalculatedGrade) {

		DisplayGrade displayGrade = new DisplayGrade();
		
		boolean hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;
		boolean isLetterGradeMode = gradebook.getGrade_type() == GradebookService.GRADE_TYPE_LETTER;
	
		String enteredGrade = null;
		String letterGrade = null;

		boolean isOverridden = false;

		if (courseGradeRecord != null)
			enteredGrade = courseGradeRecord.getEnteredGrade();

		if (enteredGrade == null)
			letterGrade = getLetterGrade(autoCalculatedGrade, gradebook.getSelectedGradeMapping());
		else {
			letterGrade = enteredGrade;
			isOverridden = true;
		}

		boolean isMissingScores = gbService.isStudentMissingScores(gradebook.getId(), studentUid, hasCategories);

		displayGrade.setLetterGrade(letterGrade);
		displayGrade.setCalculatedGrade(autoCalculatedGrade);
		displayGrade.setMissingGrades(isMissingScores);
		displayGrade.setLetterGradeMode(isLetterGradeMode);
		displayGrade.setOverridden(isOverridden);

		return displayGrade;
	}

	private GradeItem getGradebookGradeItem(Gradebook gradebook, List<Assignment> assignments, 
			List<Category> categories, boolean isSingleUserView) {
		GradeItem gradebookGradeItem = null;

		if (null != categories) {
			List<Category> filteredCategories = new ArrayList<Category>();
			List<Assignment> filteredAssignments = assignments;

			for(Category category : categories) {

				// First we check if the user has "can grade" permission
				boolean canGrade = authz.canUserGradeCategory(gradebook.getUid(), category.getId());
				if(canGrade) {
					filteredCategories.add(category);
				}
				else {
					// User has no "can grade" permission, so let's check if user has "can view" permission for this category
					boolean canView = isSingleUserView || authz.canUserViewCategory(gradebook.getUid(), category.getId());
					if(canView) {
						filteredCategories.add(category);
					}
				}

				if(null != assignments) {
					// Since the user doesn't have permission to either view or grade the category, we 
					// need to remove any associated assignments
					List<Assignment> tempAssignments = new ArrayList<Assignment>();
					if (filteredAssignments != null) {
						for(Assignment assignment : filteredAssignments) {

							if (assignment.getCategory() != null) {
								if(!assignment.getCategory().getId().equals(category.getId())) {

									if (!isSingleUserView || assignment.isReleased())
										tempAssignments.add(assignment);

								}
							}
						}
					}
					filteredAssignments = tempAssignments;
				}

			}
			
			gradebookGradeItem = getGradeItem(gradebook, filteredAssignments, filteredCategories, null, null);
		}
		else {
			gradebookGradeItem = getGradeItem(gradebook, assignments, categories, null, null);
		}
		return gradebookGradeItem;
	}

	private List<org.sakaiproject.gradebook.gwt.client.model.Gradebook> getGradebookModels(String[] gradebookUids) 
	throws GradebookCreationException {

		List<org.sakaiproject.gradebook.gwt.client.model.Gradebook> models = new LinkedList<org.sakaiproject.gradebook.gwt.client.model.Gradebook>();

		if (gradebookUids == null || gradebookUids.length == 0)
			gradebookUids = new String[] { lookupDefaultGradebookUid() };

		for (int i = 0; i < gradebookUids.length; i++)
			models.add(retrieveGradebookModel(gradebookUids[i]));

		return models;
	}

	private GradeItem getGradeItem(Gradebook gradebook, List<Assignment> assignments, List<Category> categories, Long categoryId, Long assignmentId) {

		GradeItem gradebookGradeItem = createGradeItem(gradebook);

		boolean isNotInCategoryMode = gradebook.getCategory_type() == GradebookService.CATEGORY_TYPE_NO_CATEGORY;

		if (isNotInCategoryMode) {
			calculateItemCategoryPercent(gradebook, null, gradebookGradeItem, null, assignments, assignmentId);

		} else {

			if (categories != null) {
				BigDecimal gradebookWeightSum = BigDecimal.ZERO;
				BigDecimal gradebookPointsSum = BigDecimal.ZERO;
				for (Category category : categories) {
					boolean isExtraCredit = category.isExtraCredit() != null && category.isExtraCredit().booleanValue();
					boolean isUnweighted = category.isUnweighted() != null && category.isUnweighted().booleanValue();

					if (!category.isRemoved() || isNotInCategoryMode) {
						double categoryWeight = category.getWeight() == null ? 0d : category.getWeight().doubleValue() * 100d;

						List<Assignment> items = getUncheckedAssignmentList(category);
						GradeItem categoryGradeItem = createGradeItem(gradebook, category, items);

						if (!isNotInCategoryMode) {
							gradebookGradeItem.addChild(categoryGradeItem);
						}

						if (categoryId != null && category.getId().equals(categoryId))
							categoryGradeItem.setActive(true);

						calculateItemCategoryPercent(gradebook, category, gradebookGradeItem, categoryGradeItem, items, assignmentId);

						double categoryPoints = categoryGradeItem.getPoints() == null ? 0d : categoryGradeItem.getPoints().doubleValue();

						if (!isExtraCredit && !isUnweighted) {
							categoryWeight = categoryGradeItem.getPercentCourseGrade() == null ? 0d : categoryGradeItem.getPercentCourseGrade().doubleValue();
							gradebookWeightSum = gradebookWeightSum.add(BigDecimal.valueOf(categoryWeight));
							gradebookPointsSum = gradebookPointsSum.add(BigDecimal.valueOf(categoryPoints));
						}

					}
				}
				gradebookGradeItem.setPoints(Double.valueOf(gradebookPointsSum.doubleValue()));
				gradebookGradeItem.setPercentCourseGrade(Double.valueOf(gradebookWeightSum.doubleValue()));
			}
		}

		return gradebookGradeItem;
	}
	
	
	public GradeItem decorateGradebook(GradeItem gradebookGradeItem, Long categoryId, Long assignmentId) {

		boolean isNotInCategoryMode = gradebookGradeItem.getCategoryType() == CategoryType.NO_CATEGORIES;

		if (isNotInCategoryMode) {
			List<GradeItem> items = gradebookGradeItem.getChildren();
			decorateItemCategoryPercent(gradebookGradeItem, null, items, assignmentId);
		} else {
			List<GradeItem> categoryGradeItems = gradebookGradeItem.getChildren();
			if (categoryGradeItems != null) {
				BigDecimal gradebookWeightSum = BigDecimal.ZERO;
				BigDecimal gradebookPointsSum = BigDecimal.ZERO;
				for (GradeItem categoryGradeItem : categoryGradeItems) {
					boolean isExtraCredit = categoryGradeItem.getExtraCredit() != null && categoryGradeItem.getExtraCredit().booleanValue();
					boolean isUnweighted = categoryGradeItem.getIncluded() == null || ! categoryGradeItem.getIncluded().booleanValue();
					boolean isRemoved = Util.checkBoolean(categoryGradeItem.getRemoved());
					
					if (!isRemoved || isNotInCategoryMode) {
						double categoryWeight = categoryGradeItem.getWeighting() == null ? 0d : categoryGradeItem.getWeighting().doubleValue() * 100d;

						List<GradeItem> items = categoryGradeItem.getChildren();
						
						if (!isNotInCategoryMode) {
							gradebookGradeItem.addChild(categoryGradeItem);
						}

						if (categoryId != null && categoryGradeItem.getIdentifier().equals(String.valueOf(categoryId)))
							categoryGradeItem.setActive(true);

						decorateItemCategoryPercent(gradebookGradeItem, categoryGradeItem, items, assignmentId);

						double categoryPoints = categoryGradeItem.getPoints() == null ? 0d : categoryGradeItem.getPoints().doubleValue();

						if (!isExtraCredit && !isUnweighted) {
							categoryWeight = categoryGradeItem.getPercentCourseGrade() == null ? 0d : categoryGradeItem.getPercentCourseGrade().doubleValue();
							gradebookWeightSum = gradebookWeightSum.add(BigDecimal.valueOf(categoryWeight));
							gradebookPointsSum = gradebookPointsSum.add(BigDecimal.valueOf(categoryPoints));
						}

					}
				}
				gradebookGradeItem.setPoints(Double.valueOf(gradebookPointsSum.doubleValue()));
				gradebookGradeItem.setPercentCourseGrade(Double.valueOf(gradebookWeightSum.doubleValue()));
			}
		}

		return gradebookGradeItem;
	}
	
	

	/*
	 * GENERAL HELPER METHODS
	 */
	private String[] getLearnerRoleNames() {
		return learnerRoleNames;
	}

	private String getLetterGrade(BigDecimal value, GradeMapping mapping) {

		if (value == null || mapping == null)
			return null;

		Map<String, Double> gradeMap = mapping.getGradeMap();
		Collection<String> grades = mapping.getGrades();

		if (gradeMap == null || grades == null)
			return null;

		for (Iterator<String> iter = grades.iterator(); iter.hasNext();) {
			String grade = iter.next();
			Double mapVal = (Double) gradeMap.get(grade);
			double m = mapVal == null ? 0d : mapVal.doubleValue();
			BigDecimal bigMapVal = BigDecimal.valueOf(m).setScale(AppConstants.DISPLAY_SCALE, GradeCalculations.MATH_CONTEXT.getRoundingMode());

			// If the value in the map is less than the value passed, then the
			// map value is the letter grade for this value
			if (bigMapVal != null && bigMapVal.compareTo(value) <= 0) {
				return grade;
			}
		}
		// As long as 'F' is zero, this should never happen.
		return null;
	}

	private String getPlacementId() {

		if (toolManager == null)
			return null;

		Placement placement = toolManager.getCurrentPlacement();

		if (placement == null)
			return null;

		return placement.getId();
	}

	public Site getSite() {

		String context = getSiteContext();
		Site site = null;

		try {

			if (siteService != null)
				site = siteService.getSite(context);

		} catch (IdUnusedException iue) {
			log.error("IDUnusedException : SiteContext = " + context);
			iue.printStackTrace();
		}

		return site;
	}

	private String getSiteContext() {

		if (toolManager == null)
			return "TESTSITECONTEXT";

		return toolManager.getCurrentPlacement().getContext();
	}

	private String getSiteId() {

		String context = getSiteContext();
		String siteId = null;

		if (siteService == null)
			return "TESTSITEID";

		try {

			Site site = siteService.getSite(context);
			siteId = site.getId();

		} catch (IdUnusedException iue) {
			log.error("IDUnusedException : SiteContext = " + context);
			iue.printStackTrace();
		}

		return siteId;
	}

	private Statistics getStatisticsMap(Gradebook gradebook, String name, GradeStatistics statistics, Long id, Long assignmentId, String studentId) {

		Statistics model = new StatisticsImpl();
		model.setId(String.valueOf(id));
		model.setAssignmentId(String.valueOf(assignmentId));
		model.setName(name);
		
		String mean = statistics != null ? convertBigDecimalStatToString(gradebook, statistics.getMean(), false) : NA;
		String median = statistics != null ? convertBigDecimalStatToString(gradebook, statistics.getMedian(), false) : NA;
		String mode = 	statistics != null ? composeModeString(statistics, gradebook) : NA;
		String standardDev = statistics != null ? convertBigDecimalStatToString(gradebook, statistics.getStandardDeviation(), true) : NA;
		String rank = NA;  

		boolean isGrader = authz.isUserAbleToGradeAll(gradebook.getUid()); // || authz.isUserAbleToGrade(gradebook.getUid());
		
		if (studentId != null && statistics != null)
		{
			StringBuilder sb = new StringBuilder();
			if (statistics.getRank() > 0)
			{
				sb.append(statistics.getRank());
			}
			else
			{
				sb.append("N/A");
			}
			sb.append(" out of "); 
			sb.append(statistics.getStudentTotal());
			rank = sb.toString();
			sb = null; 
		}
		
		boolean isCourseStats = assignmentId != null && assignmentId.equals(Long.valueOf(-1l));
		boolean isShowCourseStats = gradebook.isCourseGradeDisplayed() && isCourseStats;
		boolean isShowItemStats = Util.checkBoolean(gradebook.getShowItemStatistics()) && !isCourseStats;
		boolean isShowMean = Util.checkBoolean(gradebook.getShowMean());
    	boolean isShowMedian = Util.checkBoolean(gradebook.getShowMedian());
    	boolean isShowMode = Util.checkBoolean(gradebook.getShowMode());
    	boolean isShowRank = Util.checkBoolean(gradebook.getShowRank());
		
    	// Only transmit the stats data to the client if the user is a grader or
    	// if the grader has authorized students to view that stat
    	
    	if (isGrader || isShowCourseStats || isShowItemStats) {
			if (isGrader || isShowMean) {
				model.setMean(mean);
				model.setStandardDeviation(standardDev);	
			}
			
			if (isGrader || isShowMedian)
				model.setMedian(median);
			
			if (isGrader || isShowMode)
				model.setMode(mode);
			
			if (isGrader || isShowRank)
				model.setRank(rank); 
    	}
    	
		return model;
	}

	private Learner getStudent(Gradebook gradebook, Site site, User user) {
		List<FixedColumn> columns = getColumns(true);
		UserRecord userRecord = buildUserRecordWithSectionInfo(site, user, gradebook);
		List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
		List<Category> categories = null;
		if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY)
			categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);
		return buildLearnerGradeRecord(gradebook, userRecord, columns, assignments, categories);
	}
	
	@SuppressWarnings("unchecked")
	private List<Assignment> getUncheckedAssignmentList(Category category) {
		List<Assignment> items = null;
		if (category != null)
			items = category.getAssignmentList();
		return items;
	}
	
	
	// GRBK-488 : TPA
	// GRBK-602 : JPG
	private boolean isCategoryFullyWeighted(Category category) {
		
		if (null == category) {
			log.error(" isCategoryFullyWeighted(null) ");
			return false;
		}
		
		boolean isExtraCreditCategory = Util.checkBoolean(category.isExtraCredit());
		boolean isCategoryWeightingItemsEqually = Util.checkBoolean(category.isEqualWeightAssignments());
		boolean isCategoryPointsWeighted = Util.checkBoolean(category.isEnforcePointWeighting()); 
		boolean isUnweighted = category.isUnweighted() != null && category.isUnweighted().booleanValue();
	
		boolean isCategoryFullyWeighted = false;


		if (!category.isRemoved()) {

			if (!isUnweighted) {

				// We only verify categories for which equal weight and weighting by points has been turned off
				

				// Get the category's assignments
				List<Assignment> categoryAssignmentList = getUncheckedAssignmentList(category);

				// need to have at least on assigment to be fully weighted
				if(null == categoryAssignmentList || categoryAssignmentList.size() == 0) {
					return false;
				}

				BigDecimal categoryAssignmentWeightSum = BigDecimal.ZERO;

				// Adding up all the assignment weights
				for(Assignment assignment : categoryAssignmentList) {
					
					boolean isExtraCreditItem = Util.checkBoolean(assignment.isExtraCredit()); 
					// For equally weighted and points weighted categories, this is success: we have at least one
					// non-extra credit item in a non-extra credit category or at least one item
					// in an extra credit category 
					if( (isCategoryWeightingItemsEqually || isCategoryPointsWeighted) 
							&& !isExtraCreditCategory && !isExtraCreditItem) {
						return true;
					}

					// Ignoring
					// - extra credit assignment weights
					// - not counted items in any category
					if((!isExtraCreditCategory && isExtraCreditItem) ||
					   (assignment.isNotCounted())) {
						
						continue;
					}

					double assignmentWeighting = assignment.getAssignmentWeighting().doubleValue() * 100d;
					categoryAssignmentWeightSum = categoryAssignmentWeightSum.add(BigDecimal.valueOf(assignmentWeighting));
					
				}

				// Scale/round the sum
				categoryAssignmentWeightSum = categoryAssignmentWeightSum.setScale(AppConstants.DISPLAY_SCALE, BigDecimal.ROUND_HALF_UP);

				// Check if the sum of assignment weights adds up to 100%
				isCategoryFullyWeighted = categoryAssignmentWeightSum.compareTo(BigDecimal.valueOf(100d)) == 0;

				// If the weighted assignment sum doesn't add up to 100%, we just return and stop checking the rest
				if(!isCategoryFullyWeighted) { 
					return isCategoryFullyWeighted;
				}
			}
		}




		return true;
	}

	private List<WeightedCategoriesState> isFullyWeighted(Gradebook gradebook) {
		
		
		List<WeightedCategoriesState> statuses = new ArrayList<WeightedCategoriesState>();
		
		switch (gradebook.getCategory_type()) {
		case GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY:
			List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
			List<Category> categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);
			
			boolean isFullyWeighted = false;
			
			// It's not possible to be ready for final grading when there are no categories in a weighted categories gradebook
			if (categories == null || categories.isEmpty()) {
				return  Arrays.asList(WeightedCategoriesState.INVALID_PERCENT_GRADE);
				}
			
			
			
			
			BigDecimal gradebookWeightSum = BigDecimal.ZERO;
			for (Category category : categories) {
					
				// Don't count deleted categories
				if (category.isRemoved())
					continue;
					
				boolean isUnweighted = Util.checkBoolean(category.isUnweighted());
					
				// Don't count unweighted, null weighted or 0 weighted categories
				if (isUnweighted || category.getWeight() == null || new BigDecimal(category.getWeight().toString()).compareTo(BigDecimal.ZERO) == 0)
					continue;
						
				boolean isExtraCredit = Util.checkBoolean(category.isExtraCredit());


				boolean doItemsInCategoryAddUp = isCategoryFullyWeighted(category);
				
				// If one of the categories doesn't add up, then we can just fail the test now
				if (!doItemsInCategoryAddUp) {
					statuses.add(WeightedCategoriesState.INVALID_PERCENT_CATEGORY);	
				}
				
				// Don't count extra credit categories once we've verified the items add up
				if (isExtraCredit)
					continue;
				
				// Assuming it does add up, we need to check that it's contribution to the overall grade adds up
				BigDecimal bigCategoryWeight = new BigDecimal(category.getWeight().toString());
				gradebookWeightSum = gradebookWeightSum.add(bigCategoryWeight);
			}
			
			isFullyWeighted = gradebookWeightSum.compareTo(BigDecimal.ONE) == 0;
			
			if (!isFullyWeighted)
				statuses.add(WeightedCategoriesState.INVALID_PERCENT_GRADE);
		}
		
		
		if(statuses.size() == 0) 
				return Arrays.asList(WeightedCategoriesState.VALID);
		
		//make unique entries:
		Set<WeightedCategoriesState> set = new HashSet<WeightedCategoriesState>(statuses);
		List<WeightedCategoriesState> rv = new ArrayList<WeightedCategoriesState>(set);
		
		return rv;
	}

	private String lookupDefaultGradebookUid() {

		if (toolManager == null)
			return "TESTSITECONTEXT";

		Placement placement = toolManager.getCurrentPlacement();
		if (placement == null) {
			log.error("Placement is null!");
			return null;
		}

		return placement.getContext();
	}

	private void recalculateAssignmentGradeRecords(Assignment assignment, Double value, Double startValue) {

		List<AssignmentGradeRecord> gradeRecords = gbService.getAssignmentGradeRecords(assignment);
		List<AssignmentGradeRecord> updatedRecords = new ArrayList<AssignmentGradeRecord>();

		if (gradeRecords != null) {
			for (AssignmentGradeRecord gradeRecord : gradeRecords) {
				if (gradeRecord.getPointsEarned() != null) {
					BigDecimal newPoints = gradeCalculations.getNewPointsGrade(gradeRecord.getPointsEarned(), value, startValue);
					gradeRecord.setPointsEarned(Double.valueOf(newPoints.doubleValue()));
					updatedRecords.add(gradeRecord);
				}
			}

			if (!updatedRecords.isEmpty()) {
				gbService.updateAssignmentGradeRecords(assignment, updatedRecords);
			}
		}
	}

	private List<Assignment> recalculateAssignmentWeights(Category category, Boolean enforceEqualWeighting, List<Assignment> assignments) {

		List<Assignment> updatedAssignments = new ArrayList<Assignment>();

		boolean isExtraCreditCategory = category.isExtraCredit() == null ? false : category.isExtraCredit().booleanValue();
		int weightedCount = 0;
		if (assignments != null) {
			for (Assignment assignment : assignments) {
				boolean isRemoved = assignment.isRemoved();
				boolean isWeighted = assignment.isCounted();
				boolean isExtraCredit = assignment.isExtraCredit() == null ? false : assignment.isExtraCredit().booleanValue();
				if (isWeighted && (isExtraCreditCategory || !isExtraCredit) && !isRemoved) {
					weightedCount++;
				}
			}
		}
		
		int dropLowest = category.getDrop_lowest();
		if (weightedCount >= dropLowest) 
			weightedCount -= dropLowest;
		
		boolean doRecalculate = false;

		if (enforceEqualWeighting != null && enforceEqualWeighting.booleanValue() && !category.isEqualWeightAssignments().equals(Boolean.TRUE)) {
			category.setEqualWeightAssignments(enforceEqualWeighting);
			gbService.updateCategory(category);
		}

		doRecalculate = category.isEqualWeightAssignments() == null ? true : category.isEqualWeightAssignments().booleanValue();

		if (doRecalculate) {
			Double newWeight = gradeCalculations.calculateEqualWeight(weightedCount);
			if (assignments != null) {
				for (Assignment assignment : assignments) {
					boolean isRemoved = assignment.isRemoved();
					boolean isWeighted = assignment.isCounted();
					boolean isExtraCredit = assignment.isExtraCredit() == null ? false : assignment.isExtraCredit().booleanValue();
					if (!isRemoved && isWeighted) {
						if (isExtraCredit && !isExtraCreditCategory)
							updatedAssignments.add(assignment);
						else {
							Assignment persistAssignment = gbService.getAssignment(assignment.getId());
							persistAssignment.setAssignmentWeighting(newWeight);
							gbService.updateAssignment(persistAssignment);
							updatedAssignments.add(persistAssignment);
						}
					}
				}
			}
		}
		return updatedAssignments;
	}

	private AssignmentGradeRecord scoreItem(Gradebook gradebook, int gradeType, Assignment assignment, AssignmentGradeRecord assignmentGradeRecord, String studentUid, Double value, boolean includeExcluded, boolean deferUpdate)
	throws InvalidInputException {

		boolean isUserAbleToGrade = authz.isUserAbleToGradeAll(gradebook.getUid()) || authz.isUserAbleToGradeItemForStudent(gradebook.getUid(), assignment.getId(), studentUid);

		if (!isUserAbleToGrade)
			throw new InvalidInputException("You are not authorized to grade this student for this item.");

		if (assignment.isExternallyMaintained())
			throw new InvalidInputException("This grade item is maintained externally. Please input and edit grades through " + assignment.getExternalAppName());

		if (!includeExcluded && assignmentGradeRecord.isExcludedFromGrade() != null && assignmentGradeRecord.isExcludedFromGrade().booleanValue())
			throw new InvalidInputException("The student has been excused from this assignment. It is no longer possible to assign him or her a grade.");
		
		
		switch (gradeType) {
		case GradebookService.GRADE_TYPE_POINTS:
			if (value != null) {
				if (value.compareTo(assignment.getPointsPossible()) > 0)
					throw new InvalidInputException("This grade cannot be larger than " + Util.formatDoubleAsPointsString(assignment.getPointsPossible()));
				else if (value.compareTo(Double.valueOf(0d)) < 0) {
					double v = value.doubleValue();

					if (v < -1d * assignment.getPointsPossible().doubleValue())
						throw new InvalidInputException("The absolute value of a negative point score assigned to a student cannot be greater than the total possible points allowed for an item");
				}
			}
			assignmentGradeRecord.setPointsEarned(value);
			break;
		case GradebookService.GRADE_TYPE_LETTER:
			// GRBK-582 : We reversed the case statements so that we can
			// can reverse lookup the letter grade and set it in the assignmentGradeRecord object
			if(null == value) {
				assignmentGradeRecord.setLetterEarned("");
			}
			else {
				assignmentGradeRecord.setLetterEarned(gradeCalculations.convertPercentageToLetterGrade(new BigDecimal(value)));
			}
		case GradebookService.GRADE_TYPE_PERCENTAGE:
		
			BigDecimal pointsEarned = null;
			if (value != null) {
				if (value.compareTo(Double.valueOf(100d)) > 0)
					throw new InvalidInputException("This grade cannot be larger than " + Util.formatDoubleAsPointsString(Double.valueOf(100d)) + "%");
				else if (value.compareTo(Double.valueOf(0d)) < 0)
					throw new InvalidInputException("This grade cannot be less than " + Util.formatDoubleAsPointsString(Double.valueOf(0d)) + "%");


				pointsEarned = gradeCalculations.getPercentAsPointsEarned(assignment, value);					
			}

			Double pointsEarnedDouble = pointsEarned == null ? null : Double.valueOf(pointsEarned.doubleValue());
			assignmentGradeRecord.setPointsEarned(pointsEarnedDouble);
			assignmentGradeRecord.setPercentEarned(value);
			break;
		}

		// Prepare record for update
		assignmentGradeRecord.setGradableObject(assignment);
		assignmentGradeRecord.setStudentId(studentUid);

		if (!deferUpdate) {
			Collection<AssignmentGradeRecord> gradeRecords = new LinkedList<AssignmentGradeRecord>();
			gradeRecords.add(assignmentGradeRecord);
			gbService.updateAssignmentGradeRecords(assignment, gradeRecords);
			postEvent("gradebook2.assignGrade", String.valueOf(gradebook.getId()), String.valueOf(assignment.getId()), studentUid);
		}

		return assignmentGradeRecord;
	}

	private Permission toPermission(Long gradebookId, org.sakaiproject.gradebook.gwt.client.model.Permission permission) {
		
		Long permissionId = permission.getId();
		Long categoryId = permission.getCategoryId();
		String function = permission.getPermissionId();
		String userId = permission.getUserId();
		String groupId = permission.getSectionId();
		
		if (gradebookId == null)
			gradebookId = permission.getGradebookId();
		
		Permission perm = new Permission();
		perm.setId(permissionId);
		perm.setGradebookId(gradebookId);
		perm.setCategoryId(categoryId);
		perm.setFunction(function);
		perm.setUserId(userId);
		perm.setGroupId(groupId);
	
		return perm;
	}
	
	private Long doUpdateCategory(Item item, Category category) throws InvalidInputException {
		
		Long categoryId = category.getId();
		
		Gradebook gradebook = category.getGradebook();
		
		if (!authz.isUserAbleToEditAssessments(gradebook.getUid()))
			throw new InvalidInputException("You are not authorized to edit categories.");
		
		boolean isWeightChanged = false;
				
		ActionRecord actionRecord = createActionRecord(category.getName(), String.valueOf(category.getId()), gradebook, item, EntityType.CATEGORY.name(), ActionType.UPDATE.name());
		
		try {

			boolean originalExtraCredit = Util.checkBoolean(category.isExtraCredit());
			boolean currentExtraCredit = Util.checkBoolean(item.getExtraCredit());

			isWeightChanged = originalExtraCredit != currentExtraCredit;

			Double newCategoryWeight = item.getPercentCourseGrade();
			Double oldCategoryWeight = category.getWeight();

			isWeightChanged = isWeightChanged || Util.notEquals(newCategoryWeight, oldCategoryWeight);

			double w = newCategoryWeight == null ? 0d : ((Double) newCategoryWeight).doubleValue() * 0.01;

			boolean isEqualWeighting = Util.checkBoolean(item.getEqualWeightAssignments());
			boolean wasEqualWeighting = Util.checkBoolean(category.isEqualWeightAssignments());

			isWeightChanged = isWeightChanged || isEqualWeighting != wasEqualWeighting;

			boolean isUnweighted = !Util.checkBoolean(item.getIncluded());
			boolean wasUnweighted = Util.checkBoolean(category.isUnweighted());

			if (wasUnweighted && !isUnweighted && category.isRemoved())
				throw new InvalidInputException("You cannot include a deleted category in grade. Please undelete the category first.");

			int newDropLowest = convertInteger(item.getDropLowest()).intValue();
			int oldDropLowest = category.getDrop_lowest();
			
			if (newDropLowest < 0)
				throw new InvalidInputException("You cannot set the drop lowest to a negative number.");
			
			boolean isRemoved = Util.checkBoolean(item.getRemoved());
			boolean wasRemoved = category.isRemoved();

			Integer newCategoryOrder = item.getItemOrder();
			Integer oldCategoryOrder = category.getCategoryOrder();

			boolean hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;
			boolean hasWeights = gradebook.getCategory_type() == GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY;

			boolean isEnforcePointWeighting = !currentExtraCredit && Util.checkBoolean(item.getEnforcePointWeighting());
			
			boolean isReleased = Util.checkBoolean(item.getReleased());
			
			if (hasCategories) {
				List<Category> categories = gbService.getCategories(gradebook.getId());
				// Business rule #2
				businessLogic.applyNoDuplicateCategoryNamesRule(gradebook.getId(), item.getName(), category.getId(), categories);

				if (hasWeights)
					businessLogic.applyOnlyEqualWeightDropLowestRule(newDropLowest, isEqualWeighting);

				if (oldCategoryOrder == null) {
					if (categories != null) {
						int count = 0;
						for (Category c : categories) {
							if (c.isRemoved())
								continue;

							if (c.getId().equals(category.getId()))
								oldCategoryOrder = Integer.valueOf(count);
							else if (c.getCategoryOrder() == null) {
								c.setCategoryOrder(Integer.valueOf(count));
								gbService.updateCategory(c);
							}
							count++;
						}
					}
				}
			}

			if (oldCategoryOrder != null && newCategoryOrder != null && oldCategoryOrder.compareTo(newCategoryOrder) < 0)
				newCategoryOrder = Integer.valueOf(newCategoryOrder.intValue() - 1);

			category.setName(convertString(item.getName()));
			category.setExtraCredit(Boolean.valueOf(currentExtraCredit));
			category.setWeight(Double.valueOf(w));
			// Business rule #1
			if (w == 0d)
				category.setUnweighted(Boolean.TRUE);
			category.setEqualWeightAssignments(Boolean.valueOf(isEqualWeighting));
			category.setDrop_lowest(newDropLowest);
			category.setRemoved(isRemoved);
			category.setUnweighted(Boolean.valueOf(isUnweighted || isRemoved));
			category.setEnforcePointWeighting(Boolean.valueOf(isEnforcePointWeighting));
			
			if (newCategoryOrder != null)
				category.setCategoryOrder(newCategoryOrder);
			else if (oldCategoryOrder != null)
				category.setCategoryOrder(oldCategoryOrder);

			gbService.updateCategory(category);

			if (isRemoved)
				postEvent("gradebook2.deleteCategory", String.valueOf(gradebook.getId()), String.valueOf(category.getId()));
			else
				postEvent("gradebook2.updateCategory", String.valueOf(gradebook.getId()), String.valueOf(category.getId()));
			
			if (hasCategories) {
				List<Assignment> assignmentsForCategory = gbService.getAssignmentsForCategory(category.getId());

				if (isRemoved && !wasRemoved)
					businessLogic.applyRemoveChildItemsWhenCategoryRemoved(category, assignmentsForCategory);

				// Business rule #3
				if ((newDropLowest != oldDropLowest) || (isEqualWeighting && !wasEqualWeighting && businessLogic.checkRecalculateEqualWeightingRule(category)))
					recalculateAssignmentWeights(category, Boolean.FALSE, assignmentsForCategory);

				if (oldCategoryOrder == null || (newCategoryOrder != null && newCategoryOrder.compareTo(oldCategoryOrder) != 0))
					businessLogic.reorderAllCategories(gradebook.getId(), category.getId(), newCategoryOrder, oldCategoryOrder);
			
				businessLogic.applyReleaseChildItemsWhenCategoryReleased(category, assignmentsForCategory, isReleased);
			}

		} catch (RuntimeException e) {
			actionRecord.setStatus(ActionRecord.STATUS_FAILURE);
			throw e;
		} finally {
			gbService.storeActionRecord(actionRecord);
		}
		
		return categoryId;
	}

	/**
	 * Method to update a category model
	 * 
	 * Business rules: (1) if weight is null or zero, uninclude it (2) new
	 * category name must not duplicate an existing category name (3) if equal
	 * weighting is set, then recalculate all item weights of child items, (4)
	 * if category is extra credit, ensure that none of its items are extra
	 * credit
	 * 
	 * @param item
	 * @return
	 * @throws InvalidInputException
	 */
	private Item updateCategoryModel(Item item) throws InvalidInputException {

		Category category = gbService.getCategory(Long.valueOf(item.getIdentifier()));
		
		if (category == null) {
			throw new InvalidInputException(i18n.getString("cannotModifyDefaultCategory"));
		}
		
		Gradebook gradebook = category.getGradebook();

		doUpdateCategory(item, category);

		List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
		List<Category> categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);

		GradeItem gradebookGradeItem = getGradeItem(gradebook, assignments, categories, category.getId(), null);

		return gradebookGradeItem;
	}
	
	private Long doUpdateGradebook(Item item, Gradebook gradebook) throws InvalidInputException {
		if (!authz.isUserAbleToEditAssessments(gradebook.getUid()))
			throw new InvalidInputException("You are not authorized to edit this gradebook.");
		
		
		ActionRecord actionRecord = createActionRecord(gradebook.getName(), gradebook.getUid(), gradebook, item, EntityType.GRADEBOOK.name(), ActionType.UPDATE.name());
			
		//	new ActionRecord(gradebook.getUid(), gradebook.getId(), EntityType.GRADEBOOK.name(), ActionType.UPDATE.name());
		//actionRecord.setEntityName(gradebook.getName());
		//actionRecord.setEntityId(gradebook.getUid());

		gbService.storeActionRecord(actionRecord);

		gradebook.setName(item.getName());

		int newCategoryType = -1;

		boolean hasCategories = item.getCategoryType() != CategoryType.NO_CATEGORIES;

		switch (item.getCategoryType()) {
			case NO_CATEGORIES:
				newCategoryType = GradebookService.CATEGORY_TYPE_NO_CATEGORY;
				break;
			case SIMPLE_CATEGORIES:
				newCategoryType = GradebookService.CATEGORY_TYPE_ONLY_CATEGORY;
				break;
			case WEIGHTED_CATEGORIES:
				newCategoryType = GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY;
				break;
		}

		gradebook.setCategory_type(newCategoryType);

		int oldGradeType = gradebook.getGrade_type();
		int newGradeType = -1;

		switch (item.getGradeType()) {
			case POINTS:
				newGradeType = GradebookService.GRADE_TYPE_POINTS;
				break;
			case PERCENTAGES:
				newGradeType = GradebookService.GRADE_TYPE_PERCENTAGE;
				break;
			case LETTERS:
				newGradeType = GradebookService.GRADE_TYPE_LETTER;
				break;
		}
		
		if (oldGradeType != newGradeType) {
			if (gbService.isAnyScoreEntered(gradebook.getId(), hasCategories))
				throw new InvalidInputException("There are one or more scores already entered for this gradebook. To switch grade types at this time you will have to remove those scores.");
		}

		gradebook.setGrade_type(newGradeType);

		boolean isReleaseGrades = Util.checkBoolean(item.getReleaseGrades());

		gradebook.setCourseGradeDisplayed(isReleaseGrades);

		boolean isReleaseItems = Util.checkBoolean(item.getReleaseItems());

		gradebook.setAssignmentsDisplayed(isReleaseItems);

		boolean isExtraCreditScaled = Util.checkBoolean(item.getExtraCreditScaled());
		
		// GRBK-487 : Prevent extra credit scaled setting from being preserved if we're limiting
		// the effectiveness between categories
		if (isExtraCreditScaled && limitScaledExtraCreditToCategoryType != null) {
			// That is, only keep it enabled if we have limited for this particular category type
			isExtraCreditScaled = item.getCategoryType() == limitScaledExtraCreditToCategoryType;
		}
		
		gradebook.setScaledExtraCredit(Boolean.valueOf(isExtraCreditScaled));
		
		boolean isShowMean = Util.checkBoolean(item.getShowMean());
		
		gradebook.setShowMean(Boolean.valueOf(isShowMean));
		
		boolean isShowMedian = Util.checkBoolean(item.getShowMedian());
		
		gradebook.setShowMedian(Boolean.valueOf(isShowMedian));
		
		boolean isShowMode = Util.checkBoolean(item.getShowMode());
		
		gradebook.setShowMode(Boolean.valueOf(isShowMode));

		boolean isShowRank = Util.checkBoolean(item.getShowRank());
		
		gradebook.setShowRank(Boolean.valueOf(isShowRank));
		
		boolean isShowItemStatistics = Util.checkBoolean(item.getShowItemStatistics());
		
		gradebook.setShowItemStatistics(Boolean.valueOf(isShowItemStatistics));
		
		GradeMapping mapping = gradebook.getSelectedGradeMapping();
		Long gradeScaleId = item.getGradeScaleId();
		if (mapping != null && gradeScaleId != null && !mapping.getId().equals(gradeScaleId)) {
			GradeMapping newMapping = gbService.getGradeMapping(gradeScaleId);
			gradebook.setSelectedGradeMapping(newMapping);
		}

		gbService.updateGradebook(gradebook);
		
		return gradebook.getId();
	}

	private Item updateGradebookModel(Item item) throws InvalidInputException {

		Gradebook gradebook = gbService.getGradebook(item.getIdentifier());

		boolean hasCategories = item.getCategoryType() != CategoryType.NO_CATEGORIES;
		
		doUpdateGradebook(item, gradebook);

		List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
		List<Category> categories = null;
		if (hasCategories)
			categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);
		return getGradeItem(gradebook, assignments, categories, null, null);
	}
	
	private void verifyUserDataIsUpToDate(Site site, String[] learnerRoleKeys) {

		String siteId = site == null ? null : site.getId();

		int totalUsers = gbService.getFullUserCountForSite(siteId, null, learnerRoleKeys);
		int dereferencedUsers = gbService.getDereferencedUserCountForSite(siteId, null, learnerRoleKeys);

		int diff = totalUsers - dereferencedUsers;

		UserDereferenceRealmUpdate lastUpdate = gbService.getLastUserDereferenceSync(siteId, null);

		// Obviously if the realm count has changed, then we need to update, but
		// let's also do it if more than an hour has passe
		long ONEHOUR = 1000l * 60l * 60l;
		if (lastUpdate == null || lastUpdate.getRealmCount() == null || !lastUpdate.getRealmCount().equals(Integer.valueOf(diff)) || lastUpdate.getLastUpdate() == null
				|| lastUpdate.getLastUpdate().getTime() + ONEHOUR < new Date().getTime()) {
			gbService.syncUserDereferenceBySite(siteId, null, findAllMembers(site, learnerRoleKeys), diff, learnerRoleKeys);
		}
	}	
}
