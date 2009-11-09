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

package org.sakaiproject.gradebook.gwt.sakai;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityCreateAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityGradeAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.ActionType;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.exceptions.BusinessRuleException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.MultiGradeLoadConfig;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationModel;
import org.sakaiproject.gradebook.gwt.client.model.AuthModel;
import org.sakaiproject.gradebook.gwt.client.model.CategoryModel;
import org.sakaiproject.gradebook.gwt.client.model.CommentModel;
import org.sakaiproject.gradebook.gwt.client.model.ConfigurationModel;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumnModel;
import org.sakaiproject.gradebook.gwt.client.model.GradeEventModel;
import org.sakaiproject.gradebook.gwt.client.model.GradeFormatModel;
import org.sakaiproject.gradebook.gwt.client.model.GradeScaleRecordModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.PermissionEntryListModel;
import org.sakaiproject.gradebook.gwt.client.model.PermissionEntryModel;
import org.sakaiproject.gradebook.gwt.client.model.SectionModel;
import org.sakaiproject.gradebook.gwt.client.model.SpreadsheetModel;
import org.sakaiproject.gradebook.gwt.client.model.StatisticsModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.client.model.SubmissionVerificationModel;
import org.sakaiproject.gradebook.gwt.client.model.UserModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel.Key;
import org.sakaiproject.gradebook.gwt.sakai.InstitutionalAdvisor.Column;
import org.sakaiproject.gradebook.gwt.sakai.mock.SiteMock;
import org.sakaiproject.gradebook.gwt.sakai.model.ActionRecord;
import org.sakaiproject.gradebook.gwt.sakai.model.GradeStatistics;
import org.sakaiproject.gradebook.gwt.sakai.model.StudentScore;
import org.sakaiproject.gradebook.gwt.sakai.model.UserConfiguration;
import org.sakaiproject.gradebook.gwt.sakai.model.UserDereference;
import org.sakaiproject.gradebook.gwt.sakai.model.UserDereferenceRealmUpdate;
import org.sakaiproject.gradebook.gwt.server.DataTypeConversionUtil;
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

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.core.client.GWT;

public class Gradebook2ServiceImpl implements Gradebook2Service {

	private static final Log log = LogFactory.getLog(Gradebook2ServiceImpl.class);

	private BusinessLogic businessLogic;
	private GradebookFrameworkService frameworkService;
	private GradebookToolService gbService;
	private GradeCalculations gradeCalculations;
	private Gradebook2Authz authz;
	private SectionAwareness sectionAwareness;
	private InstitutionalAdvisor advisor;
	private SiteService siteService;
	private ToolManager toolManager;
	private UserDirectoryService userService;
	private SessionManager sessionManager;
	private ServerConfigurationService configService;
	private EventTrackingService eventTrackingService;
	
	private String helpUrl;
	private List<GradeType> enabledGradeTypes;
	private String[] learnerRoleNames;
	
	
	public void init() {
		enabledGradeTypes = new ArrayList<GradeType>();
		
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
				enabledGradeTypes.add(GradebookModel.GradeType.POINTS);
			if (isPercentagesEnabled)
				enabledGradeTypes.add(GradebookModel.GradeType.PERCENTAGES);
			if (isLettersEnabled)
				enabledGradeTypes.add(GradebookModel.GradeType.LETTERS);
			
			String learnerRoleNameString = configService.getString(AppConstants.LEARNER_ROLE_NAMES);
			
			if (learnerRoleNameString != null && !learnerRoleNameString.equals(""))
				learnerRoleNames = learnerRoleNameString.split("\\s*,\\s*");
			
		} else {
			enabledGradeTypes.add(GradebookModel.GradeType.POINTS);
			enabledGradeTypes.add(GradebookModel.GradeType.PERCENTAGES);
			enabledGradeTypes.add(GradebookModel.GradeType.LETTERS);
		}
		
		if (learnerRoleNames == null)
			learnerRoleNames = advisor.getLearnerRoleNames();
		
		if (learnerRoleNames == null)
			learnerRoleNames = new String[] { "Student", "access" };
		
	}

	/**
	 * Method to add a new grade item to the gradebook.
	 * 
	 * Business rules: (1) If points is null, set points to 100 (2) If weight is
	 * null, set weight to be equivalent to points value -- needs to happen
	 * after #1
	 * 
	 * - When category type is "No Categories": (3) new item name must not
	 * duplicate an active (removed = false) item name in gradebook, otherwise
	 * throw exception (NoDuplicateItemNamesRule)
	 * 
	 * - When category type is "Categories" or "Weighted Categories" (4) new
	 * item name must not duplicate an active (removed = false) item name in the
	 * same category, otherwise throw exception (5) if item is "included" and
	 * category has "equal weighting" then recalculate all item weights for this
	 * category (6) item must include a valid category id
	 * 
	 * @param gradebookUid
	 * @param gradebookId
	 * @param item
	 * @return ItemModel representing either (a) the gradebook, (b) the item's
	 *         category, or (c) the item
	 * @throws InvalidInputException
	 */
	public ItemModel createItem(String gradebookUid, Long gradebookId, final ItemModel item, boolean enforceNoNewCategories) throws InvalidInputException {

		if (item.getItemType() != null) {
			switch (item.getItemType()) {
				case CATEGORY:
					return addItemCategory(gradebookUid, gradebookId, item);
			}
		}

		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		boolean hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;

		Long assignmentId = doCreateItem(gradebook, item, hasCategories, enforceNoNewCategories);

		postEvent("gradebook2.newItem", String.valueOf(gradebook.getId()), String.valueOf(assignmentId));
		
		if (!hasCategories) {
			List<Assignment> assignments = gbService.getAssignments(gradebookId);
			return getItemModel(gradebook, assignments, null, null, assignmentId);
		}

		List<Assignment> assignments = gbService.getAssignments(gradebookId);
		List<Category> categories = getCategoriesWithAssignments(gradebookId, assignments, true);
		return getItemModel(gradebook, assignments, categories, null, assignmentId);
	}

	public ConfigurationModel createOrUpdateConfigurationModel(Long gradebookId, String field, String value) {

		ConfigurationModel model = new ConfigurationModel();

		gbService.createOrUpdateUserConfiguration(getCurrentUser(), gradebookId, field, value);

		model.set(field, value);

		return model;
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
	public ItemModel addItemCategory(String gradebookUid, Long gradebookId, ItemModel item) throws BusinessRuleException {

		ActionRecord actionRecord = new ActionRecord(gradebookUid, gradebookId, EntityType.CATEGORY.name(), ActionType.CREATE.name());
		actionRecord.setEntityName(item.getName());
		Map<String, String> propertyMap = actionRecord.getPropertyMap();

		for (String property : item.getPropertyNames()) {
			String value = String.valueOf(item.get(property));
			if (value != null)
				propertyMap.put(property, value);
		}

		// Category category = null;
		Gradebook gradebook = null;
		List<Assignment> assignments = null;
		List<Category> categories = null;
		Long categoryId = null;

		try {
			String name = item.getName();
			Double weight = item.getPercentCourseGrade();
			Boolean isEqualWeighting = item.getEqualWeightAssignments();
			Boolean isIncluded = item.getIncluded();
			Integer dropLowest = item.getDropLowest();
			Boolean isExtraCredit = item.getExtraCredit();
			Integer categoryOrder = item.getItemOrder();
			Boolean doEnforcePointWeighting = item.getEnforcePointWeighting();

			boolean isUnweighted = !DataTypeConversionUtil.checkBoolean(isIncluded);

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

				businessLogic.applyNoDuplicateCategoryNamesRule(gradebook.getId(), item.getName(), null, categories);
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

		return getItemModel(gradebook, assignments, categories, categoryId, null);

	}

	public CommentModel createOrUpdateComment(Long assignmentId, String studentUid, String text) {

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
		
		return createOrUpdateCommentModel(null, comment);
	}

	public PermissionEntryListModel getPermissionEntryList(Long gradebookId, String learnerId) {

		PermissionEntryListModel permissionEntryListModel = new PermissionEntryListModel();
		List<PermissionEntryModel> permissionEntryModelList = new ArrayList<PermissionEntryModel>();

		List<Permission> permissions = gbService.getPermissionsForUser(gradebookId, learnerId);

		for (Permission permission : permissions) {

			PermissionEntryModel permissionEntryModel = new PermissionEntryModel();
			permissionEntryModel.setId(permission.getId());
			permissionEntryModel.setUserId(permission.getUserId());

			try {

				User user = userService.getUser(permission.getUserId());

				if (null != user) {
					permissionEntryModel.setUserDisplayName(user.getDisplayName());
				} else {
					log.error("Was not able go get an User object from userId = " + permission.getUserId());
				}
			} catch (UserNotDefinedException e) {
				log.error("Was not able go get an User object from userId = " + permission.getUserId());
				e.printStackTrace();
			}

			permissionEntryModel.setPermissionId(permission.getFunction());

			// If category id is null, the all categories were selected
			if (null != permission.getCategoryId()) {

				permissionEntryModel.setCategoryId(permission.getCategoryId());
				Category category = gbService.getCategory(permission.getCategoryId());
				if (null != category) {
					permissionEntryModel.setCategoryDisplayName(category.getName());
				} else {
					// TODO: handle error
				}

			} else {

				permissionEntryModel.setCategoryId(null);
				permissionEntryModel.setCategoryDisplayName("All");
			}

			// If section id is null, then all sections were selected
			if (null != permission.getGroupId()) {

				permissionEntryModel.setSectionId(permission.getGroupId());
				CourseSection courseSection = sectionAwareness.getSection(permission.getGroupId());
				if (null != courseSection) {
					permissionEntryModel.setSectionDisplayName(courseSection.getTitle());
				} else {
					// TODO: handle error
				}

			} else {

				permissionEntryModel.setSectionId(null);
				permissionEntryModel.setSectionDisplayName("All");
			}

			permissionEntryModel.setDeleteAction("Delete");
			permissionEntryModelList.add(permissionEntryModel);
		}

		permissionEntryListModel.setEntries(permissionEntryModelList);
		return permissionEntryListModel;
	}

	public <X extends BaseModel> ListLoadResult<X> getCategoriesNotRemoved(Long gradebookId) {

		List<X> categoryModelList = new ArrayList<X>();
		List<Category> categoryList = gbService.getCategories(gradebookId);

		// Adding all categories entry

		categoryModelList.add((X) new CategoryModel(null, "All Categories"));

		for (Category category : categoryList) {
			if (!category.isRemoved()) {
				categoryModelList.add((X) new CategoryModel(category.getId(), category.getName()));
			}
		}

		ListLoadResult<X> result = new BaseListLoadResult<X>(categoryModelList);
		return result;
	}

	public PermissionEntryModel createPermissionEntry(Long gradebookId, PermissionEntryModel permissionEntryModel) {

		Permission permission = new Permission();
		permission.setGradebookId(gradebookId);
		permission.setCategoryId(permissionEntryModel.getCategoryId());
		permission.setFunction(permissionEntryModel.getPermissionId());
		permission.setUserId(permissionEntryModel.getUserId());
		permission.setGroupId(permissionEntryModel.getSectionId());
		Long id = gbService.createPermission(permission);
		permissionEntryModel.setId(id);
		return permissionEntryModel;
	}

	public PermissionEntryModel deletePermissionEntry(Long gradebookId, PermissionEntryModel permissionEntryModel) {

		Permission permission = new Permission();
		permission.setGradebookId(gradebookId);
		permission.setId(permissionEntryModel.getId());
		permission.setCategoryId(permissionEntryModel.getCategoryId());
		permission.setFunction(permissionEntryModel.getPermissionId());
		permission.setUserId(permissionEntryModel.getUserId());
		permission.setGroupId(permissionEntryModel.getSectionId());
		gbService.deletePermission(permission);
		return permissionEntryModel;
	}

	public SpreadsheetModel createOrUpdateSpreadsheet(String gradebookUid, SpreadsheetModel spreadsheetModel) throws InvalidInputException {

		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		boolean hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;
		boolean isLetterGrading = gradebook.getGrade_type() == GradebookService.GRADE_TYPE_LETTER;
		Map<String, Assignment> idToAssignmentMap = new HashMap<String, Assignment>();
		Map<String, Assignment> commentIdToAssignmentMap = new HashMap<String, Assignment>();
		List<ItemModel> headers = spreadsheetModel.getHeaders();

		if (headers != null) {

			Set<Long> newCategoryIdSet = new HashSet<Long>();
			for (ItemModel item : headers) {
				String id = item.getIdentifier();
				if (id != null) {
					Long categoryId = item.getCategoryId();
					String name = item.getName();
					Double weight = item.getPercentCategory();
					Double points = item.getPoints();
					boolean isExtraCredit = DataTypeConversionUtil.checkBoolean(item.getExtraCredit());
					boolean isIncluded = DataTypeConversionUtil.checkBoolean(item.getIncluded());

					int indexOfCommentTextFlag = id.indexOf(StudentModel.COMMENT_TEXT_FLAG);
					
					if (indexOfCommentTextFlag != -1) {
						String realId = id.substring(0, indexOfCommentTextFlag);
						
						Assignment assignment = null;
						if (realId.startsWith("NEW:")) {
							assignment = idToAssignmentMap.get(realId);
							
						} else {
							assignment = gbService.getAssignment(Long.valueOf(realId));	
						}
						
						commentIdToAssignmentMap.put(realId, assignment);
					} else if (id.startsWith("NEW:")) {

						ItemModel itemModel = new ItemModel();
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

							weight = weight.doubleValue() * 0.01d;

							assignment.setAssignmentWeighting(weight);
							isModified = true;
						}

						boolean wasIncluded = !DataTypeConversionUtil.checkBoolean(assignment.isUnweighted());

						if (wasIncluded != isIncluded) {
							assignment.setUnweighted(Boolean.valueOf(!isIncluded));
							isModified = true;
						}

						boolean wasExtraCredit = DataTypeConversionUtil.checkBoolean(assignment.isExtraCredit());

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
						List<Assignment> assigns = category.getAssignmentList();
						// Business rule #5
						if (businessLogic.checkRecalculateEqualWeightingRule(category))
							recalculateAssignmentWeights(category, Boolean.FALSE, assigns);
					}

				}
			}
		}

		Long gradebookId = gradebook.getId();

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

		List<String> results = new ArrayList<String>();

		// Since we index the new items by a phony id e.g. "NEW:123", we need to
		// use this set to iterate
		Set<String> idKeySet = idToAssignmentMap.keySet();
		Set<String> commentIdKeySet = commentIdToAssignmentMap.keySet();
		
		if (true) {
			for (StudentModel student : spreadsheetModel.getRows()) {
				UserRecord userRecord = userRecordMap.get(student.getIdentifier());

				StringBuilder builder = new StringBuilder();

				builder.append("Grading ");

				if (userRecord != null) {
					if (userRecord.getDisplayName() == null)
						builder.append(userRecord.getDisplayId()).append(": ");
					else
						builder.append(userRecord.getDisplayName()).append(": ");
				} else {
					builder.append(student.get("NAME")).append(": ");
				}

				Map<Long, AssignmentGradeRecord> gradeRecordMap = userRecord == null ? null : userRecord.getGradeRecordMap();

				if (commentIdKeySet != null) {
					for (String id : commentIdKeySet) {
						String fullId = new StringBuilder().append(id).append(StudentModel.COMMENT_TEXT_FLAG).toString();
						Object v = student.get(fullId);
						
						Assignment assignment = commentIdToAssignmentMap.get(id);
						
						CommentModel comment = createOrUpdateComment(assignment.getId(), student.getIdentifier(), (String)v);
						
						if (comment != null) {
							student.set(fullId, comment.getText());
							student.set(new StringBuilder(id).append(StudentModel.COMMENTED_FLAG).toString(), Boolean.TRUE);
						
							builder.append("Comment for ").append(assignment.getName()).append(" (")
								.append((String)v).append(") ");;
						}
					}
				}
				
				if (idKeySet != null) {
					
					
					for (String id : idKeySet) {
						Assignment assignment = idToAssignmentMap.get(id);
					
						List<AssignmentGradeRecord> gradedRecords = new ArrayList<AssignmentGradeRecord>();
						
						// This is the value stored on the client
						Object v = student.get(id);

						Double value = null;
						Double oldValue = null;
						
						try {
							
							if (isLetterGrading) {
								
								if (!gradeCalculations.isValidLetterGrade((String)v)) {
									String failedProperty = new StringBuilder().append(assignment.getId()).append(StudentModel.FAILED_FLAG).toString();
									student.set(failedProperty, "Invalid input");
									log.warn("Failed to score item for " + student.getIdentifier() + " and item " + assignment.getId() + " to " + v);
			
									if (oldValue != null)
										builder.append(oldValue);
			
									builder.append(" Invalid) ");
									student.set(AppConstants.IMPORT_CHANGES, Boolean.TRUE);
									continue;
								} else 
									value = gradeCalculations.convertLetterGradeToPercentage((String)v);
								
							} else if (v != null && v instanceof String) {
								String strValue = (String) v;
								if (strValue.trim().length() > 0)
									value = Double.valueOf(Double.parseDouble((String) v));
							} else
								value = (Double) v;
		
							AssignmentGradeRecord assignmentGradeRecord = null;
		
							if (gradeRecordMap != null)
								assignmentGradeRecord = gradeRecordMap.get(assignment.getId()); 

							if (assignmentGradeRecord == null)
								assignmentGradeRecord = new AssignmentGradeRecord();
		
							switch (gradebook.getGrade_type()) {
								case GradebookService.GRADE_TYPE_POINTS:
									oldValue = assignmentGradeRecord.getPointsEarned();
									break;
								case GradebookService.GRADE_TYPE_PERCENTAGE:
								case GradebookService.GRADE_TYPE_LETTER:
									BigDecimal d = gradeCalculations.getPointsEarnedAsPercent(assignment, assignmentGradeRecord);
									oldValue = d == null ? null : Double.valueOf(d.doubleValue());
									break;
							}
	
							if (oldValue == null && value == null)
								continue;
		
							student.set(AppConstants.IMPORT_CHANGES, Boolean.TRUE);
		
							
							gradedRecords.add(scoreItem(gradebook, assignment, assignmentGradeRecord, student.getIdentifier(), value, true, true));
							builder.append(assignment.getName()).append(" (");
							
							if (oldValue != null)
								builder.append(oldValue).append("->");
	
							if (value != null)
								builder.append(value);
							
							builder.append(") ");
						} catch (NumberFormatException nfe) {
							String failedProperty = new StringBuilder().append(assignment.getId()).append(StudentModel.FAILED_FLAG).toString();
							student.set(failedProperty, "Invalid input");
							log.warn("Failed to score item for " + student.getIdentifier() + " and item " + assignment.getId() + " to " + v);
	
							if (oldValue != null)
								builder.append(oldValue);
	
							builder.append(" Invalid) ");
							
							student.set(AppConstants.IMPORT_CHANGES, Boolean.TRUE);
						} catch (InvalidInputException e) {
							String failedProperty = new StringBuilder().append(assignment.getId()).append(StudentModel.FAILED_FLAG).toString();
							student.set(failedProperty, e.getMessage());
							log.warn("Failed to score numeric item for " + student.getIdentifier() + " and item " + assignment.getId() + " to " + value);
	
							if (oldValue != null)
								builder.append(oldValue);
	
							builder.append(" Invalid) ");
							
							student.set(AppConstants.IMPORT_CHANGES, Boolean.TRUE);
						} catch (Exception e) {
	
							String failedProperty = new StringBuilder().append(assignment.getId()).append(StudentModel.FAILED_FLAG).toString();
							student.set(failedProperty, e.getMessage());
	
							log.warn("Failed to score numeric item for " + student.getIdentifier() + " and item " + assignment.getId() + " to " + value, e);
	
							if (oldValue != null)
								builder.append(oldValue);
	
							builder.append(" Failed) ");
							
							student.set(AppConstants.IMPORT_CHANGES, Boolean.TRUE);
						} finally {
							gbService.updateAssignmentGradeRecords(assignment, gradedRecords);
							postEvent("gradebook2.assignGradesBulk", String.valueOf(gradebook.getId()), String.valueOf(assignment.getId()));
						}
					}
				}
				
				results.add(builder.toString());
			}
		}
		spreadsheetModel.setResults(results);
		List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
		List<Category> categories = null;
		if (hasCategories)
			categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);
		spreadsheetModel.setGradebookItemModel(getItemModel(gradebook, assignments, categories, null, null));

		return spreadsheetModel;
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
					userRecord.populate(user);
					userRecords.add(userRecord);
				}
			}
		}

		return userRecords;
	}

	private Long doCreateItem(Gradebook gradebook, ItemModel item, boolean hasCategories, boolean enforceNoNewCategories) throws BusinessRuleException {

		ActionRecord actionRecord = new ActionRecord(gradebook.getUid(), gradebook.getId(), EntityType.ITEM.name(), ActionType.CREATE.name());
		actionRecord.setEntityName(item.getName());
		Map<String, String> propertyMap = actionRecord.getPropertyMap();

		for (String property : item.getPropertyNames()) {
			String value = String.valueOf(item.get(property));
			if (value != null)
				propertyMap.put(property, value);
		}

		boolean hasNewCategory = false;

		Category category = null;
		Long assignmentId = null;

		List<Assignment> assignments = null;
		Long categoryId = null;

		try {
			boolean includeInGrade = DataTypeConversionUtil.checkBoolean(item.getIncluded());

			categoryId = item.getCategoryId();
			String name = item.getName();
			Double weight = item.getPercentCategory();
			Double points = item.getPoints();
			Boolean isReleased = Boolean.valueOf(DataTypeConversionUtil.checkBoolean(item.getReleased()));
			Boolean isIncluded = Boolean.valueOf(includeInGrade);
			Boolean isExtraCredit = Boolean.valueOf(DataTypeConversionUtil.checkBoolean(item.getExtraCredit()));
			Boolean isNullsAsZeros = Boolean.valueOf(DataTypeConversionUtil.checkBoolean(item.getNullsAsZeros()));
			Date dueDate = item.getDueDate();
			Integer itemOrder = item.getItemOrder();

			// Business rule #1
			if (points == null)
				points = new Double(100d);
			// Business rule #2
			if (weight == null)
				weight = Double.valueOf(points.doubleValue());

			if (hasCategories && item.getCategoryId() == null && item.getCategoryName() != null && item.getCategoryName().trim().length() > 0) {
				ItemModel newCategory = new ItemModel();
				newCategory.setName(item.getCategoryName());
				newCategory.setIncluded(Boolean.TRUE);
				newCategory = getActiveItem(addItemCategory(gradebook.getUid(), gradebook.getId(), newCategory));
				categoryId = newCategory.getCategoryId();
				item.setCategoryId(categoryId);
				hasNewCategory = true;
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
						businessLogic.applyMustIncludeCategoryRule(item.getCategoryId());
				}

			} else {
				assignments = gbService.getAssignments(gradebook.getId());
				businessLogic.applyNoDuplicateItemNamesRule(gradebook.getId(), name, null, assignments);
			}

			if (itemOrder == null)
				itemOrder = assignments == null || assignments.isEmpty() ? Integer.valueOf(0) : Integer.valueOf(assignments.size());

				double w = weight == null ? 0d : ((Double) weight).doubleValue() * 0.01;

				assignmentId = gbService.createAssignmentForCategory(gradebook.getId(), categoryId, name, points, Double.valueOf(w), dueDate, Boolean.valueOf(!DataTypeConversionUtil.checkBoolean(isIncluded)), isExtraCredit, Boolean.FALSE,
						isReleased, itemOrder, isNullsAsZeros);

				// Apply business rules after item creation
				if (hasCategories) {
					assignments = gbService.getAssignmentsForCategory(categoryId);
					// Business rule #5
					if (businessLogic.checkRecalculateEqualWeightingRule(category))
						recalculateAssignmentWeights(category, Boolean.FALSE, assignments);
				}

				actionRecord.setStatus(ActionRecord.STATUS_SUCCESS);
		} catch (RuntimeException e) {
			actionRecord.setStatus(ActionRecord.STATUS_FAILURE);
			throw e;
		} finally {
			gbService.storeActionRecord(actionRecord);
		}

		return assignmentId;
	}

	private List<UserRecord> doSearchAndSortUserRecords(Gradebook gradebook, List<Assignment> assignments, List<Category> categories, List<String> studentUids, Map<String, UserRecord> userRecordMap, PagingLoadConfig config) {

		String searchString = null;
		if (config instanceof MultiGradeLoadConfig) {
			searchString = ((MultiGradeLoadConfig) config).getSearchString();
		}

		List<UserRecord> userRecords = null;
		StudentModel.Key sortColumnKey = null;

		String columnId = null;

		// This is slightly painful, but since it's a String that gets passed
		// up, we have to iterate
		if (config != null && config.getSortInfo() != null && config.getSortInfo().getSortField() != null) {
			columnId = config.getSortInfo().getSortField();

			for (StudentModel.Key key : EnumSet.allOf(StudentModel.Key.class)) {
				if (columnId.equals(key.name())) {
					sortColumnKey = key;
					break;
				}
			}

			if (sortColumnKey == null)
				sortColumnKey = StudentModel.Key.ASSIGNMENT;

		}

		if (sortColumnKey == null)
			sortColumnKey = StudentModel.Key.DISPLAY_NAME;

		boolean isDescending = config != null && config.getSortInfo() != null && config.getSortInfo().getSortDir() == SortDir.DESC;

		// Check to see if we're sorting or not
		if (sortColumnKey != null) {
			switch (sortColumnKey) {
				case DISPLAY_NAME:
				case LAST_NAME_FIRST:
				case DISPLAY_ID:
				case SECTION:
				case EMAIL:
					if (userRecords == null) {
						userRecords = doSearchUsers(searchString, studentUids, userRecordMap);
					}
					break;
				case COURSE_GRADE:
				case GRADE_OVERRIDE:
				case ASSIGNMENT:
					if (userRecords == null) {
						userRecords = new ArrayList<UserRecord>(userRecordMap.values());
					}
					break;
			}

			Comparator<UserRecord> comparator = null;
			switch (sortColumnKey) {
				case DISPLAY_NAME:
				case LAST_NAME_FIRST:
					comparator = SORT_NAME_COMPARATOR;
					break;
				case DISPLAY_ID:
					comparator = DISPLAY_ID_COMPARATOR;
					break;
				case EMAIL:
					comparator = EMAIL_COMPARATOR;
					break;
				case SECTION:
					comparator = SECTION_TITLE_COMPARATOR;
					break;
				case CALCULATED_GRADE:
					// In this case we need to ensure that we've calculated
					// everybody's course grade
					for (UserRecord record : userRecords) {
						BigDecimal calculatedGrade = getCalculatedGrade(gradebook, assignments, categories, record.getGradeRecordMap());
						DisplayGrade displayGrade = getDisplayGrade(gradebook, record.getUserUid(), record.getCourseGradeRecord(), calculatedGrade);
						record.setDisplayGrade(displayGrade);
						record.setCalculated(true);
					}
					comparator = new CalculatedGradeComparator(isDescending);
					break;
				case COURSE_GRADE:
					// In this case we need to ensure that we've calculated
					// everybody's course grade
					for (UserRecord record : userRecords) {
						BigDecimal calculatedGrade = getCalculatedGrade(gradebook, assignments, categories, record.getGradeRecordMap());
						DisplayGrade displayGrade = getDisplayGrade(gradebook, record.getUserUid(), record.getCourseGradeRecord(), calculatedGrade);
						record.setDisplayGrade(displayGrade);
						record.setCalculated(true);
					}
					comparator = new CourseGradeComparator(isDescending);
					break;
				case GRADE_OVERRIDE:
					comparator = new EnteredGradeComparator(isDescending);
					break;
				case ASSIGNMENT:
					Long assignmentId = Long.valueOf(columnId);
					comparator = new AssignmentComparator(assignmentId, isDescending);
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

	public StudentModel excuseNumericItem(String gradebookUid, StudentModel student, String id, Boolean value, Boolean previousValue) throws InvalidInputException {

		int indexOf = id.indexOf(StudentModel.EXCUSE_FLAG);

		if (indexOf == -1)
			return null;

		String assignmentId = id.substring(0, indexOf);

		Assignment assignment = gbService.getAssignment(Long.valueOf(assignmentId));
		Gradebook gradebook = assignment.getGradebook();

		List<AssignmentGradeRecord> gradeRecords = gbService.getAssignmentGradeRecordsForStudent(gradebook.getId(), student.getIdentifier());

		AssignmentGradeRecord assignmentGradeRecord = null;

		for (AssignmentGradeRecord currentGradeRecord : gradeRecords) {
			Assignment a = currentGradeRecord.getAssignment();
			if (a.getId().equals(assignment.getId()))
				assignmentGradeRecord = currentGradeRecord;
		}

		if (assignmentGradeRecord == null) {
			assignmentGradeRecord = new AssignmentGradeRecord();
		}

		assignmentGradeRecord.setExcluded(value);

		// Prepare record for update
		assignmentGradeRecord.setGradableObject(assignment);
		assignmentGradeRecord.setStudentId(student.getIdentifier());

		Collection<AssignmentGradeRecord> updateGradeRecords = new LinkedList<AssignmentGradeRecord>();
		updateGradeRecords.add(assignmentGradeRecord);
		gbService.updateAssignmentGradeRecords(assignment, updateGradeRecords, gradebook.getGrade_type());

		gradeRecords = gbService.getAssignmentGradeRecordsForStudent(gradebook.getId(), student.getIdentifier());

		return refreshLearnerData(gradebook, student, assignment, gradeRecords);
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

	@SuppressWarnings("unchecked")
	public <X extends BaseModel> PagingLoadResult<X> getActionHistory(String gradebookUid, PagingLoadConfig config) {

		Integer size = gbService.getActionRecordSize(gradebookUid);
		List<ActionRecord> actionRecords = gbService.getActionRecords(gradebookUid, config.getOffset(), config.getLimit());
		List<X> models = new ArrayList<X>();

		for (ActionRecord actionRecord : actionRecords) {
			UserEntityAction actionModel = null;
			try {
				UserEntityAction.ActionType actionType = UserEntityAction.ActionType.valueOf(actionRecord.getActionType());
				UserEntityAction.EntityType entityType = UserEntityAction.EntityType.valueOf(actionRecord.getEntityType());
				switch (actionType) {
					case CREATE:
						actionModel = new UserEntityCreateAction();
						break;
					case GRADED:
						actionModel = new UserEntityGradeAction();
						actionModel.setValue(actionRecord.getPropertyMap().get("score"));
						break;
					case UPDATE:
						actionModel = new UserEntityUpdateAction();
						actionModel.setValue(actionRecord.getEntityName());
						break;
				}

				if (actionModel == null)
					continue;
				
				actionModel.setIdentifier(String.valueOf(actionRecord.getId()));
				actionModel.setGradebookUid(actionRecord.getGradebookUid());
				actionModel.setGradebookId(actionRecord.getGradebookId());
				actionModel.setEntityType(entityType);
				if (actionRecord.getEntityId() != null)
					actionModel.setEntityId(actionRecord.getEntityId());
				if (actionRecord.getEntityName() != null)
					actionModel.setEntityName(actionRecord.getEntityName());
				if (actionRecord.getParentId() != null)
					actionModel.setParentId(Long.valueOf(actionRecord.getParentId()));
				
				String studentUid = actionRecord.getStudentUid();
				actionModel.setStudentUid(studentUid);
				
				if (actionRecord.getEntityName() != null && actionRecord.getEntityName().contains(" : ")) {
					String[] parts = actionRecord.getEntityName().split(" : ");
					
					actionModel.setStudentName(parts[0]);
					actionModel.setEntityName(parts[1]);
				}

				actionModel.setGraderName(actionRecord.getGraderId());

				if (userService != null && actionRecord.getGraderId() != null) {

					try {
						User user = userService.getUser(actionRecord.getGraderId());
						actionModel.setGraderName(user.getDisplayName());
					} catch (UserNotDefinedException e) {
						log.warn("Unable to find grader name for " + actionRecord.getGraderId(), e);
					}

				}

				actionModel.setDatePerformed(actionRecord.getDatePerformed());
				actionModel.setDateRecorded(actionRecord.getDateRecorded());

				Map<String, String> propertyMap = actionRecord.getPropertyMap();

				if (propertyMap != null) {
					for (String key : propertyMap.keySet()) {
						String value = propertyMap.get(key);
						actionModel.set(key, value);
					}
				}


				actionModel.setDescription(actionModel.toString());

				models.add((X) actionModel);
			} catch (Exception e) {
				log.warn("Failed to retrieve history record for " + actionRecord.getId());
			}
		}

		return new BasePagingLoadResult<X>(models, config.getOffset(), size.intValue());
	}

	public class WorkerThread extends Thread {

		private Site site;
		private String[] learnerRoleKeys;

		public WorkerThread(Site site, String[] learnerRoleKeys) {

			this.site = site;
			this.learnerRoleKeys = learnerRoleKeys;
		}

		public void run() {

			verifyUserDataIsUpToDate(site, learnerRoleKeys);
		}
	}

	public ApplicationModel getApplicationModel(String... gradebookUids) {

		ApplicationModel model = new ApplicationModel();
		model.setGradebookModels(getGradebookModels(gradebookUids));
		model.setHelpUrl(helpUrl);
		model.setEnabledGradeTypes(enabledGradeTypes);
		
		return model;
	}

	public AuthModel getAuthorization(String... gradebookUids) {

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
					frameworkService.addGradebook(gradebookUids[i], "My Default Gradebook");
					gradebook = gbService.getGradebook(gradebookUids[i]);

					// Add the default configuration settings
					/*gbService.createOrUpdateUserConfiguration(getCurrentUser(), gradebook.getId(), 
							ConfigurationModel.getColumnHiddenId(AppConstants.ITEMTREE, 
									StudentModel.Key.DISPLAY_ID.name()), String.valueOf(Boolean.TRUE));
					gbService.createOrUpdateUserConfiguration(getCurrentUser(), gradebook.getId(), 
							ConfigurationModel.getColumnHiddenId(AppConstants.ITEMTREE, 
									StudentModel.Key.DISPLAY_NAME.name()), String.valueOf(Boolean.TRUE));
					gbService.createOrUpdateUserConfiguration(getCurrentUser(), gradebook.getId(), 
							ConfigurationModel.getColumnHiddenId(AppConstants.ITEMTREE, 
									StudentModel.Key.EMAIL.name()), String.valueOf(Boolean.TRUE));
					gbService.createOrUpdateUserConfiguration(getCurrentUser(), gradebook.getId(), 
							ConfigurationModel.getColumnHiddenId(AppConstants.ITEMTREE, 
									StudentModel.Key.SECTION.name()), String.valueOf(Boolean.TRUE));	
					gbService.createOrUpdateUserConfiguration(getCurrentUser(), gradebook.getId(), 
							ConfigurationModel.getColumnHiddenId(AppConstants.ITEMTREE, 
									StudentModel.Key.CALCULATED_GRADE.name()), String.valueOf(Boolean.TRUE));
					gbService.createOrUpdateUserConfiguration(getCurrentUser(), gradebook.getId(), 
							ConfigurationModel.getColumnHiddenId(AppConstants.ITEMTREE, 
									StudentModel.Key.LETTER_GRADE.name()), String.valueOf(Boolean.TRUE));*/
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
			authModel.setPlacementId(getPlacementId());

			return authModel;
		}
		return null;
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

	public GradebookModel getGradebook(String uid) {

		Gradebook gradebook = gbService.getGradebook(uid);
		List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
		List<Category> categories = null;
		if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY)
			categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);
		return createGradebookModel(gradebook, assignments, categories, false);
	}

	public <X extends BaseModel> ListLoadResult<X> getGradeEvents(String studentId, Long assignmentId) {

		List<X> models = new ArrayList<X>();
		Assignment assignment = gbService.getAssignment(assignmentId);
		Collection<GradableObject> gradableObjects = new LinkedList<GradableObject>();
		gradableObjects.add(assignment);

		Map<GradableObject, List<GradingEvent>> map = gbService.getGradingEventsForStudent(studentId, gradableObjects);

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
				models.add((X) createOrUpdateGradeEventModel(null, event));
			}
		}

		ListLoadResult<X> result = new BaseListLoadResult<X>(models);

		return result;
	}

	public <X extends BaseModel> ListLoadResult<X> getGradeFormats(String gradebookUid, 
			Long gradebookId) {

		List<X> models = new ArrayList<X>();

		Set<GradeMapping> gradeMappings = gbService.getGradeMappings(gradebookId);

		for (GradeMapping mapping : gradeMappings) {
			GradeFormatModel model = new GradeFormatModel();
			model.setIdentifier(String.valueOf(mapping.getId()));
			model.setName(mapping.getName());
			models.add((X)model);
		}

		return new BaseListLoadResult<X>(models);
	}

	public <X extends BaseModel> ListLoadResult<X> getCategories(String gradebookUid, Long gradebookId, PagingLoadConfig config) {

		List<Category> categories = gbService.getCategories(gradebookId);

		List<X> models = new LinkedList<X>();

		for (Category category : categories) {
			models.add((X) createItemModel(null, category, null));
		}

		return new BaseListLoadResult<X>(models);
	}

	public <X extends BaseModel> PagingLoadResult<X> getSections(String gradebookUid, Long gradebookId, PagingLoadConfig config, boolean enableAllSectionsEntry, String allSectionsEntryTitle) {

		List<CourseSection> viewableSections = authz.getViewableSections(gradebookUid);

		List<X> sections = new LinkedList<X>();

		if (enableAllSectionsEntry) {
			SectionModel allSections = new SectionModel();
			allSections.setSectionName(allSectionsEntryTitle);
			sections.add((X) allSections);
		}

		if (viewableSections != null) {
			for (CourseSection courseSection : viewableSections) {
				SectionModel sectionModel = new SectionModel();
				sectionModel.setSectionId(courseSection.getUuid());
				sectionModel.setSectionName(courseSection.getTitle());
				sections.add((X) sectionModel);
			}
		}

		return new BasePagingLoadResult<X>(sections, config.getOffset(), viewableSections.size());
	}

	public <X extends BaseModel> ListLoadResult<X> getSelectedGradeMapping(String gradebookUid) {

		List<X> gradeScaleMappings = getSelectedGradeMappingList(gradebookUid);
		ListLoadResult<X> result = new BaseListLoadResult<X>(gradeScaleMappings);
		return result;
	}

	private <X extends BaseModel> List<X> getSelectedGradeMappingList(String gradebookUid) {

		List<X> gradeScaleMappings = new ArrayList<X>();
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

			GradeScaleRecordModel gradeScaleModel = new GradeScaleRecordModel(letterGrade, gradeMapping.getGradeMap().get(letterGrade), upperScale);
			gradeScaleMappings.add((X) gradeScaleModel);
			upperScale = gradeMapping.getGradeMap().get(letterGrade);
		}
		return gradeScaleMappings;
	}

	private <X extends Object> List<X> generateStatsList(String gradebookUid, Long gradebookId, String studentId)
	{
		
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
			
			boolean isScaledExtraCredit = DataTypeConversionUtil.checkBoolean(gradebook.isScaledExtraCredit());
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

		List<X> statsList = new ArrayList<X>();

		long id = 0;
		statsList.add((X) createStatisticsModel(gradebook, "Course Grade", courseGradeStatistics, Long.valueOf(id), Long.valueOf(-1), studentId ));
		id++;

		if (assignments != null) {
			
			if (hasCategories) {
				if (categories != null) {
					for (Category category : categories) {
						List<Assignment> asns = category.getAssignmentList();
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
								
								statsList.add((X) createStatisticsModel(gradebook, name, assignmentStatistics, Long.valueOf(id), assignmentId, studentId));
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
					
					statsList.add((X) createStatisticsModel(gradebook, name, assignmentStatistics, Long.valueOf(id), assignmentId, studentId));
					id++;
				}
			}
		}

		return statsList; 
	}

	public <X extends BaseModel> ListLoadResult<X> getStatistics(String gradebookUid, Long gradebookId) {

		List<X> statsList = generateStatsList(gradebookUid, gradebookId, null);
		ListLoadResult<X> result = new BaseListLoadResult<X>(statsList);

		return result;
	}

	private static final String NA = "-";
	private static final String UNIQUESET = "N/A";
	
	private StatisticsModel createStatisticsModel(Gradebook gradebook, String name, GradeStatistics statistics, Long id, Long assignmentId, String studentId) {

		StatisticsModel model = new StatisticsModel();
		model.setId(String.valueOf(id));
		model.setAssignmentId(String.valueOf(assignmentId));
		model.setName(name);
		
		String mean = statistics != null ? convertBigDecimalStatToString(gradebook, statistics.getMean(), false) : NA;
		String median = statistics != null ? convertBigDecimalStatToString(gradebook, statistics.getMedian(), false) : NA;
		String mode = 	statistics != null ? composeModeString(statistics, gradebook) : NA;
		String standardDev = statistics != null ? convertBigDecimalStatToString(gradebook, statistics.getStandardDeviation(), true) : NA;
		String rank = NA;  

		boolean isStudentView = studentId != null;
		
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
		
		boolean isShowMean = DataTypeConversionUtil.checkBoolean(gradebook.getShowMean());
    	boolean isShowMedian = DataTypeConversionUtil.checkBoolean(gradebook.getShowMedian());
    	boolean isShowMode = DataTypeConversionUtil.checkBoolean(gradebook.getShowMode());
    	boolean isShowRank = DataTypeConversionUtil.checkBoolean(gradebook.getShowRank());
		
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
		
		return model;
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
	
	
	public <X extends BaseModel> PagingLoadResult<X> getStudentRows(String gradebookUid, Long gradebookId, PagingLoadConfig config, Boolean includeExportCourseManagementId) {

		boolean includeCMId = DataTypeConversionUtil.checkBoolean(includeExportCourseManagementId);

		List<X> rows = new ArrayList<X>();

		String[] learnerRoleNames = getLearnerRoleNames();

		List<UserRecord> userRecords = null;

		String sectionUuid = null;

		if (config != null && config instanceof MultiGradeLoadConfig) {
			sectionUuid = ((MultiGradeLoadConfig) config).getSectionUuid();
		}

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
		StudentModel.Key sortColumnKey = null;

		int offset = -1;
		int limit = -1;

		String searchField = null;
		String searchCriteria = null;

		// This is slightly painful, but since it's a String that gets passed
		// up, we have to iterate
		if (config != null) {
			offset = config.getOffset();
			limit = config.getLimit();
			if (config.getSortInfo() != null && config.getSortInfo().getSortField() != null) {
				columnId = config.getSortInfo().getSortField();

				for (StudentModel.Key key : EnumSet.allOf(StudentModel.Key.class)) {
					if (columnId.equals(key.name())) {
						sortColumnKey = key;
						break;
					}
				}

				if (sortColumnKey == null)
					sortColumnKey = StudentModel.Key.ASSIGNMENT;
			}

			if (config instanceof MultiGradeLoadConfig) {
				searchField = "sortName";
				searchCriteria = ((MultiGradeLoadConfig) config).getSearchString();

				if (searchCriteria != null)
					searchCriteria = searchCriteria.toUpperCase();
			}
		}

		if (sortColumnKey == null)
			sortColumnKey = StudentModel.Key.DISPLAY_NAME;

		boolean isDescending = config != null && config.getSortInfo() != null && config.getSortInfo().getSortDir() == SortDir.DESC;

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
			return new BasePagingLoadResult<X>(rows, 0, totalUsers);
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
				return new BasePagingLoadResult<X>(rows, 0, totalUsers);
			}

			realmIds = new String[1];
			realmIds[0] = new StringBuffer().append("/site/").append(siteId).toString();
		}

		// Check to see if we're sorting or not
		if (sortColumnKey != null) {
			switch (sortColumnKey) {
				case DISPLAY_NAME:
				case LAST_NAME_FIRST:
				case DISPLAY_ID:
				case EMAIL:
					String sortField = "lastNameFirst";

					switch (sortColumnKey) {
						case DISPLAY_ID:
							sortField = "displayId";
							break;
						case DISPLAY_NAME:
						case LAST_NAME_FIRST:
							sortField = "sortName";
							break;
						case EMAIL:
							sortField = "email";
							break;
					}

					// FIXME : GRBK-233 : If a site has sections as well as adhoc groups users that are 
					// in both sections and adhoc groups show up twice
					userRecords = findLearnerRecordPage(gradebook, site, realmIds, groupReferences, groupReferenceMap, sortField, searchField, searchCriteria, offset, limit, !isDescending, includeCMId);
					totalUsers = gbService.getUserCountForSite(realmIds, sortField, searchField, searchCriteria, learnerRoleNames);

					int startRow = config == null ? 0 : config.getOffset();

					List<FixedColumnModel> columns = getColumns();

					rows = new ArrayList<X>(userRecords == null ? 0 : userRecords.size());

					// We only want to populate the rowData and rowValues for
					// the requested rows
					for (UserRecord userRecord : userRecords) {
						rows.add((X) buildStudentRow(gradebook, userRecord, columns, assignments, categories));
					}

					return new BasePagingLoadResult<X>(rows, startRow, totalUsers);

				case SECTION:
				case COURSE_GRADE:
				case GRADE_OVERRIDE:
				case ASSIGNMENT:

					userRecords = findLearnerRecordPage(gradebook, site, realmIds, groupReferences, groupReferenceMap, null, searchField, searchCriteria, -1, -1, !isDescending, includeCMId);

					Map<String, UserRecord> userRecordMap = new HashMap<String, UserRecord>();

					for (UserRecord userRecord : userRecords) {
						userRecordMap.put(userRecord.getUserUid(), userRecord);
					}

					List<String> studentUids = new ArrayList<String>(userRecordMap.keySet());

					userRecords = doSearchAndSortUserRecords(gradebook, assignments, categories, studentUids, userRecordMap, config);
					totalUsers = userRecords.size();
					break;
			}
		}

		int startRow = 0;
		int lastRow = totalUsers;

		if (config != null) {
			startRow = config.getOffset();
			lastRow = startRow + config.getLimit();
		}

		if (lastRow > totalUsers) {
			lastRow = totalUsers;
		}

		List<FixedColumnModel> columns = getColumns();

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

			rows.add((X) buildStudentRow(gradebook, userRecord, columns, assignments, categories));
		}

		return new BasePagingLoadResult<X>(rows, startRow, totalUsers);
	}

	public SubmissionVerificationModel getSubmissionVerification(String gradebookUid, Long gradebookId) {

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

		if (dereferences != null) {
			for (UserDereference dereference : dereferences) {

				if (gbService.isStudentMissingScores(gradebookId, dereference.getUserUid(), hasCategories)) {
					isMissingScores = true;
					break;
				}
			}
		}

		int numberOfLearners = dereferences == null ? 0 : dereferences.size();

		return new SubmissionVerificationModel(numberOfLearners, isMissingScores);
	}

	public StudentModel scoreNumericItem(String gradebookUid, StudentModel student, String assignmentId, Double value, Double previousValue) throws InvalidInputException {

		Assignment assignment = gbService.getAssignment(Long.valueOf(assignmentId));
		Gradebook gradebook = assignment.getGradebook();

		ActionRecord actionRecord = new ActionRecord(gradebook.getUid(), gradebook.getId(), EntityType.GRADE_RECORD.name(), ActionType.GRADED.name());
		actionRecord.setEntityName(new StringBuilder().append(student.getDisplayName()).append(" : ").append(assignment.getName()).toString());
		actionRecord.setEntityId(String.valueOf(assignment.getId()));
		actionRecord.setStudentUid(student.getIdentifier());
		Map<String, String> propertyMap = actionRecord.getPropertyMap();

		propertyMap.put("score", String.valueOf(value));

		List<AssignmentGradeRecord> gradeRecords = gbService.getAssignmentGradeRecordsForStudent(gradebook.getId(), student.getIdentifier());

		AssignmentGradeRecord assignmentGradeRecord = null;

		for (AssignmentGradeRecord currentGradeRecord : gradeRecords) {
			Assignment a = currentGradeRecord.getAssignment();
			if (a.getId().equals(assignment.getId()))
				assignmentGradeRecord = currentGradeRecord;
		}

		if (assignmentGradeRecord == null) {
			assignmentGradeRecord = new AssignmentGradeRecord();
		}

		scoreItem(gradebook, assignment, assignmentGradeRecord, student.getIdentifier(), value, false, false);

		gradeRecords = gbService.getAssignmentGradeRecordsForStudent(gradebook.getId(), student.getIdentifier());

		refreshLearnerData(gradebook, student, assignment, gradeRecords);
		
		switch (gradebook.getGrade_type()) {
		case GradebookService.GRADE_TYPE_LETTER:
			BigDecimal percentage = value == null ? null : BigDecimal.valueOf(value.doubleValue());
			String letterGrade = gradeCalculations.convertPercentageToLetterGrade(percentage);
			student.set(assignmentId, letterGrade);
			break;
		default:
			student.set(assignmentId, value);
		}
		

		gbService.storeActionRecord(actionRecord);

		return student;
	}

	public StudentModel scoreTextItem(String gradebookUid, StudentModel student, String property, String value, String previousValue) throws InvalidInputException {
		
		if (value != null && value.trim().equals(""))
			value = null;

		if (value != null)
			value = value.toUpperCase();

		if (property == null)
			return null;
		
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
				
		if (property.equals(StudentModel.Key.GRADE_OVERRIDE.name())) {
			// GRBK-233 : Only IOR can overwrite course grades
			boolean isInstructor = authz.isUserAbleToGradeAll(gradebook.getUid());
			if (!isInstructor)
				throw new InvalidInputException("You are not authorized to overwrite the course grade for this student.");
			
			// Then we are overriding a course grade
			CourseGradeRecord courseGradeRecord = gbService.getStudentCourseGradeRecord(gradebook, student.getIdentifier());
			courseGradeRecord.setEnteredGrade(value);
			Collection<CourseGradeRecord> gradeRecords = new LinkedList<CourseGradeRecord>();
			gradeRecords.add(courseGradeRecord);
			// FIXME: We shouldn't be looking up the CourseGrade if we don't use it
			// anywhere.
			CourseGrade courseGrade = gbService.getCourseGrade(gradebook.getId());
	
			GradeMapping gradeMapping = gradebook.getSelectedGradeMapping();
			Set<String> scaledGrades = gradeMapping.getGradeMap().keySet();
	
			if (value != null && !advisor.isValidOverrideGrade(value, student.getEid(), student.getStudentDisplayId(), gradebook, scaledGrades))
				throw new InvalidInputException("This is not a valid override grade for this individual in this course.");
	
			gbService.updateCourseGradeRecords(courseGrade, gradeRecords);
	
			ActionRecord actionRecord = new ActionRecord(gradebook.getUid(), gradebook.getId(), EntityType.COURSE_GRADE_RECORD.name(), ActionType.GRADED.name());
			actionRecord.setEntityName(new StringBuilder().append(student.getDisplayName()).append(" : ").append(gradebook.getName()).toString());
			actionRecord.setEntityId(String.valueOf(gradebook.getId()));
			actionRecord.setStudentUid(student.getIdentifier());
			Map<String, String> propertyMap = actionRecord.getPropertyMap();

			propertyMap.put("score", value);
			
			gbService.storeActionRecord(actionRecord);
			
			List<Category> categories = null;
			List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
			if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY)
				categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);
	
			Map<Long, AssignmentGradeRecord> studentGradeMap = new HashMap<Long, AssignmentGradeRecord>();
			List<AssignmentGradeRecord> records = gbService.getAssignmentGradeRecordsForStudent(gradebook.getId(), student.getIdentifier());
	
			if (records != null) {
				for (AssignmentGradeRecord record : records) {
					studentGradeMap.put(record.getAssignment().getId(), record);
				}
			}
	
			BigDecimal calculatedGrade = getCalculatedGrade(gradebook, assignments, categories, studentGradeMap);
			DisplayGrade displayGrade = getDisplayGrade(gradebook, student.getIdentifier(), courseGradeRecord, calculatedGrade);// requestCourseGrade(gradebookUid,
			displayGrade.setOverridden(value != null);
			student.set(StudentModel.Key.GRADE_OVERRIDE.name(), courseGradeRecord.getEnteredGrade());
			student.set(StudentModel.Key.COURSE_GRADE.name(), displayGrade.toString());
			student.set(StudentModel.Key.LETTER_GRADE.name(), displayGrade.getLetterGrade());
		} else if (gradebook.getGrade_type() == GradebookService.GRADE_TYPE_LETTER) {
			// We must be modifying a letter grade
			if (value != null && !gradeCalculations.isValidLetterGrade(value))
				throw new InvalidInputException("This is not a valid grade.");
			
			Double numericValue = gradeCalculations.convertLetterGradeToPercentage(value);
			Double previousNumericValue = gradeCalculations.convertLetterGradeToPercentage(previousValue);
			
			student = scoreNumericItem(gradebookUid, student, property, numericValue, previousNumericValue);
		}
		
		return student;
	}

	public void submitFinalGrade(List<Map<Column, String>> studentDataList, String gradebookUid, HttpServletRequest request, HttpServletResponse response) {
		for (Map<Column, String> studentData : studentDataList) {
			String studentUid = studentData.get(Column.STUDENT_UID);
			String finalGradeUserId = studentData.get(Column.FINAL_GRADE_USER_ID);
			String exportUserId = studentData.get(Column.EXPORT_USER_ID);
			String studentName = studentData.get(Column.STUDENT_NAME);
			String exportCmId = studentData.get(Column.EXPORT_CM_ID);
			String letterGrade = studentData.get(Column.LETTER_GRADE);
			
			ActionRecord actionRecord = new ActionRecord(gradebookUid, null, EntityType.GRADE_SUBMISSION.name(), ActionType.GRADED.name());
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

	/**
	 * Method to update an item model
	 * 
	 * Business rules: (1) If points is null, set points to 100 (2) If weight is
	 * null, set weight to be equivalent to points value -- needs to happen
	 * after #1
	 * 
	 * - When category type is "No Categories": (3) updated item name must not
	 * duplicate an active (removed = false) item name in gradebook, otherwise
	 * throw exception (NoDuplicateItemNamesRule) (4) must not include an item
	 * in grading that has been deleted (removed = true)
	 * 
	 * - When category type is "Categories" or "Weighted Categories" (5) new
	 * item name must not duplicate an active (removed = false) item name in the
	 * same category, otherwise throw exception (6) must not include an item in
	 * grading that has been deleted (removed = true) or that has a category
	 * that has been deleted (removed = true) (7) if item is "included" and
	 * category has "equal weighting" then recalculate all item weights for this
	 * category (8) item must include a valid category id (9) if category has
	 * changed, then if the old category had equal weighting and the item was
	 * included in that category, then recalculate all item weights for that
	 * category (10) if item order changes, re-order remaining items for that
	 * category (11) if category is not included, then cannot include item (12)
	 * if category is removed, then cannot unremove item
	 * 
	 * @param item
	 * @return
	 * @throws InvalidInputException
	 */
	public ItemModel updateItemModel(ItemModel item) throws InvalidInputException {

		switch (item.getItemType()) {
			case CATEGORY:
				return updateCategoryModel(item);
			case GRADEBOOK:
				return updateGradebookModel(item);
		}

		boolean isWeightChanged = false;
		boolean havePointsChanged = false;

		Long assignmentId = Long.valueOf(item.getIdentifier());
		Assignment assignment = gbService.getAssignment(assignmentId);

		Category oldCategory = null;
		Category category = assignment.getCategory();

		Gradebook gradebook = assignment.getGradebook();

		if (category == null)
			category = findDefaultCategory(gradebook.getId());

		boolean hasWeightedCategories = gradebook.getCategory_type() == GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY;
		boolean hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;
		boolean hasCategoryChanged = false;

		if (hasCategories) {
			if (item.getCategoryId() == null && item.getCategoryName() != null) {
				ItemModel newCategory = new ItemModel();
				newCategory.setName(item.getCategoryName());
				newCategory.setIncluded(Boolean.TRUE);
				newCategory = addItemCategory(gradebook.getUid(), gradebook.getId(), newCategory);
				item.setCategoryId(newCategory.getCategoryId());
				hasCategoryChanged = true;
			} else
				hasCategoryChanged = !category.getId().equals(item.getCategoryId());
		}

		ActionRecord actionRecord = new ActionRecord(gradebook.getUid(), gradebook.getId(), EntityType.ITEM.name(), ActionType.UPDATE.name());
		actionRecord.setEntityName(assignment.getName());
		actionRecord.setEntityId(String.valueOf(assignment.getId()));

		Map<String, String> propertyMap = actionRecord.getPropertyMap();
		for (String propertyName : item.getPropertyNames()) {
			Object value = item.get(propertyName);
			if (value != null)
				propertyMap.put(propertyName, String.valueOf(value));
		}

		boolean isRemoved = false;

		List<Assignment> assignments = null;
		try {

			// Check to see if the category id has changed -- this means the
			// user switched the item's category
			if (hasCategories && hasCategoryChanged)
				oldCategory = category;

			boolean wasExtraCredit = DataTypeConversionUtil.checkBoolean(assignment.isExtraCredit());
			boolean isExtraCredit = DataTypeConversionUtil.checkBoolean(item.getExtraCredit());

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
			Integer oldItemOrder = assignment.getItemOrder();

			isWeightChanged = isWeightChanged || DataTypeConversionUtil.notEquals(newAssignmentWeight, oldAssignmentWeight);

			boolean isUnweighted = !convertBoolean(item.getIncluded()).booleanValue();
			boolean wasUnweighted = DataTypeConversionUtil.checkBoolean(assignment.isUnweighted());

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

				boolean isCategoryIncluded = !DataTypeConversionUtil.checkBoolean(category.isUnweighted());
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

				/*if (hasCategoryChanged) {
					category = gbService.getCategory(item.getCategoryId());
					assignment.setCategory(category);
				}*/

			} else {
				assignments = gbService.getAssignments(gradebook.getId());

				// Business rule #3
				businessLogic.applyNoDuplicateItemNamesRule(gradebook.getId(), item.getName(), assignment.getId(), assignments);

				// Business rule #4
				businessLogic.applyCannotIncludeDeletedItemRule(wasRemoved && isRemoved, false, isUnweighted);

			}

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
			assignment.setUnweighted(Boolean.valueOf(isUnweighted || isRemoved));
			assignment.setItemOrder(newItemOrder);
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
						recalculateAssignmentWeights(category, !isUnweighted, assignments);
					}
				}

				boolean applyBusinessRule10 = oldItemOrder == null || newItemOrder.compareTo(oldItemOrder) != 0 || oldCategory != null;

				if (applyBusinessRule10)
					businessLogic.reorderAllItemsInCategory(assignmentId, category, oldCategory, newItemOrder, oldItemOrder);
			} else {

				if (oldItemOrder == null || (newItemOrder != null && newItemOrder.compareTo(oldItemOrder) != 0))
					businessLogic.reorderAllItems(gradebook.getId(), assignment.getId(), newItemOrder, oldItemOrder);

			}

			if (DataTypeConversionUtil.checkBoolean(item.getDoRecalculatePoints())) {
				if (businessLogic.checkRecalculatePointsRule(assignmentId, points, oldPoints))
					recalculateAssignmentGradeRecords(assignment, points, oldPoints);
			}
			
			if (havePointsChanged && category.getDrop_lowest() > 0 
					&& (!hasWeightedCategories 
						|| (hasWeightedCategories 
								&& DataTypeConversionUtil.checkBoolean(category.isEnforcePointWeighting())))) {
				category.setDrop_lowest(0);
				gbService.updateCategory(category);
			}
			
		} catch (RuntimeException e) {
			actionRecord.setStatus(ActionRecord.STATUS_FAILURE);
			throw e;
		} finally {
			gbService.storeActionRecord(actionRecord);
		}

		// The first case is that we're in categories mode and the category has
		// changed
		if (hasCategories && (oldCategory != null || isRemoved || havePointsChanged)) {
			assignments = gbService.getAssignments(gradebook.getId());
			List<Category> categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);
			return getItemModel(gradebook, assignments, categories, null, assignment.getId());
		}

		// If neither the weight nor the points have changed, then we can just
		// return
		// the item model itself
		/*if (!isWeightChanged && !havePointsChanged) {
			for (Assignment a : assignments) {
				double assignmentCategoryPercent = a.getAssignmentWeighting() == null ? 0.0 : a.getAssignmentWeighting().doubleValue() * 100.0;
				BigDecimal points = BigDecimal.valueOf(a.getPointsPossible().doubleValue());

				boolean isRemoved = a.isRemoved();
				boolean isExtraCredit = a.isExtraCredit() != null && a.isExtraCredit().booleanValue();
				boolean isUnweighted = a.isUnweighted() != null && a.isUnweighted().booleanValue();

				if ((isCategoryExtraCredit || !isExtraCredit) && !isUnweighted && !isRemoved) {
					percentCategorySum = percentCategorySum.add(BigDecimal.valueOf(assignmentCategoryPercent));
					pointsSum = pointsSum.add(points);
				}

			}
			
			ItemModel itemModel = createItemModel(category, assignment, null);
			itemModel.setActive(true);
			return itemModel;
		} else*/ if (!hasCategories) {
			assignments = gbService.getAssignments(gradebook.getId());
			// Otherwise if we're in no categories mode then we want to return
			// the gradebook
			return getItemModel(gradebook, assignments, null, null, assignment.getId());
		}

		// Otherwise we can return the category parent
		ItemModel categoryItemModel = getItemModelsForCategory(category, (ItemModel) item.getParent(), assignment.getId());

		String assignmentIdAsString = String.valueOf(assignment.getId());
		for (ModelData model : categoryItemModel.getChildren()) {
			ItemModel itemModel = (ItemModel) model;
			if (itemModel.getIdentifier().equals(assignmentIdAsString))
				itemModel.setActive(true);
		}

		return categoryItemModel;
	}

	public <X extends BaseModel> List<X> updateGradeScaleField(String gradebookUid, Object value, String affectedLetterGrade) throws InvalidInputException {

		// FIXME: Need to store action record for this change.
		
		if (value == null) {
			throw new InvalidInputException("Value cannot be blank. Please enter a number.");
		}
		
		BigDecimal bigValue = BigDecimal.valueOf(((Double)value).doubleValue()).setScale(AppConstants.DISPLAY_SCALE, GradeCalculations.MATH_CONTEXT.getRoundingMode());

		if (bigValue.compareTo(BigDecimal.ZERO) == 0)
			throw new InvalidInputException("Value cannot be zero. Please enter a number larger than zero.");
		if (bigValue.compareTo(BigDecimal.valueOf(100.00d)) >= 0)
			throw new InvalidInputException("Value (" + bigValue.setScale(AppConstants.DISPLAY_SCALE, GradeCalculations.MATH_CONTEXT.getRoundingMode()).toString() + ") cannot be equal or larger than 100.00.");
		
		List<X> gradeScaleMappings = new ArrayList<X>();
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		GradeMapping gradeMapping = gradebook.getSelectedGradeMapping();
		//GradingScale gradingScale = gradeMapping.getGradingScale();
		// Map<String, Double> gradingScaleMap =
		// gradingScale.getDefaultBottomPercents();
		// Map<String, Double> newGradingScaleMap = new HashMap<String,
		// Double>();
		List<String> letterGradesList = new ArrayList<String>(gradeMapping.getGradeMap().keySet());

		if (gradeMapping.getName().equalsIgnoreCase("Pass / Not Pass")) 
			Collections.sort(letterGradesList, PASS_NOPASS_COMPARATOR);
		else
			Collections.sort(letterGradesList, LETTER_GRADE_COMPARATOR);

		Double upperScale = null;

		GradeScaleRecordModel gradeScaleModel = null;

		for (String letterGrade : letterGradesList) {
			BigDecimal bigOldUpperScale = upperScale == null ? BigDecimal.valueOf(200d) : BigDecimal.valueOf(upperScale.doubleValue());
			
			upperScale = (null == upperScale) ? new Double(100d) : upperScale.equals(Double.valueOf(0d)) ? Double.valueOf(0d) : Double.valueOf(upperScale.doubleValue() - 0.01d);

			if (affectedLetterGrade.equals(letterGrade)) {
				gradeScaleModel = new GradeScaleRecordModel(letterGrade, (Double) value, upperScale);
				
				Double oldValue = gradeMapping.getGradeMap().get(letterGrade);
				
				if (oldValue != null) {
					BigDecimal bgOldValue = BigDecimal.valueOf(oldValue.doubleValue()).setScale(AppConstants.DISPLAY_SCALE, GradeCalculations.MATH_CONTEXT.getRoundingMode());
					
					if (bgOldValue.compareTo(BigDecimal.ZERO) == 0) {
						throw new InvalidInputException("Cannot modify the absolute base of a grading scale or manual override grades.");
					} 
				}
				
				// If the one above is not bigger than the one below then throw an exception
				if (bigOldUpperScale.compareTo(bigValue) <= 0) {
					throw new InvalidInputException("Value (" + bigValue.setScale(AppConstants.DISPLAY_SCALE, GradeCalculations.MATH_CONTEXT.getRoundingMode()).toString() +") must be smaller than the value above (" + bigOldUpperScale.setScale(AppConstants.DISPLAY_SCALE, GradeCalculations.MATH_CONTEXT.getRoundingMode()).toString() + ")");
				}
				
				gradeMapping.getGradeMap().put(letterGrade, (Double) value);
				upperScale = (Double) value;
			} else {
				gradeScaleModel = new GradeScaleRecordModel(letterGrade, gradeMapping.getGradeMap().get(letterGrade), upperScale);

				upperScale = gradeMapping.getGradeMap().get(letterGrade);
				
				if (upperScale != null) {
					BigDecimal bigUpperScale = BigDecimal.valueOf(upperScale.doubleValue());
					if (bigOldUpperScale.compareTo(BigDecimal.ZERO) != 0 
							&& bigOldUpperScale.compareTo(bigUpperScale) <= 0) 
						throw new InvalidInputException("Value (" + bigOldUpperScale.setScale(AppConstants.DISPLAY_SCALE, GradeCalculations.MATH_CONTEXT.getRoundingMode()).toString() + ") cannot be equal or less than the value (" + bigUpperScale.setScale(2, RoundingMode.HALF_EVEN).toString() + ") below ");
					
				}
			}

			gradeScaleMappings.add((X) gradeScaleModel);
		}

		gbService.updateGradebook(gradebook);

		return gradeScaleMappings;
	}

	public <X extends BaseModel> List<X> resetGradeScale(String gradebookUid)
	throws InvalidInputException {
		
		
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		GradeMapping gradeMapping = gradebook.getSelectedGradeMapping();
		gradeMapping.setDefaultValues();
		gbService.updateGradebook(gradebook);
		
		return getSelectedGradeMappingList(gradebookUid);
	}
	
	public <X extends BaseModel> ListLoadResult<X> getUsers() {

		List<X> userList = new ArrayList<X>();

		String placementId = lookupDefaultGradebookUid();
		List<ParticipationRecord> participationList = sectionAwareness.getSiteMembersInRole(placementId, Role.TA);

		for (ParticipationRecord participationRecord : participationList) {

			org.sakaiproject.section.api.coursemanagement.User user = participationRecord.getUser();
			UserModel userModel = new UserModel(user.getUserUid(), user.getDisplayName());
			userList.add((X) userModel);
		}

		ListLoadResult<X> result = new BaseListLoadResult<X>(userList);
		return result;
	}

	/*
	 * PROTECTED METHODS
	 */
	protected String getPlacementId() {

		if (toolManager == null)
			return null;

		Placement placement = toolManager.getCurrentPlacement();

		if (placement == null)
			return null;

		return placement.getId();
	}

	/*
	 * PRIVATE METHODS
	 */

	private Map<String, Object> appendItemData(Long assignmentId, Map<String, Object> cellMap, UserRecord userRecord, Gradebook gradebook) {

		AssignmentGradeRecord gradeRecord = null;

		String id = String.valueOf(assignmentId);

		boolean isCommented = userRecord.getCommentMap() != null && userRecord.getCommentMap().get(assignmentId) != null;

		if (isCommented) {
			cellMap.put(concat(id, StudentModel.COMMENTED_FLAG), Boolean.TRUE);
			cellMap.put(concat(id, StudentModel.COMMENT_TEXT_FLAG), userRecord.getCommentMap().get(assignmentId).getCommentText());
		}

		Map<Long, AssignmentGradeRecord> studentGradeMap = userRecord.getGradeRecordMap();

		if (studentGradeMap != null) {
			gradeRecord = studentGradeMap.get(assignmentId);

			if (gradeRecord != null) {
				boolean isExcused = gradeRecord.isExcluded() != null && gradeRecord.isExcluded().booleanValue();
				boolean isDropped = gradeRecord.isDropped() != null && gradeRecord.isDropped().booleanValue();

				if (isDropped || isExcused)
					cellMap.put(concat(id, StudentModel.DROP_FLAG), Boolean.TRUE);

				if (isExcused)
					cellMap.put(concat(id, StudentModel.EXCUSE_FLAG), Boolean.TRUE);

				BigDecimal percentage = null;
				switch (gradebook.getGrade_type()) {
					case GradebookService.GRADE_TYPE_POINTS:
						cellMap.put(id, gradeRecord.getPointsEarned());
						break;
					case GradebookService.GRADE_TYPE_PERCENTAGE:
						percentage = gradeCalculations.getPointsEarnedAsPercent((Assignment) gradeRecord.getGradableObject(), gradeRecord);
						Double percentageDouble = percentage == null ? null : Double.valueOf(percentage.doubleValue());
						cellMap.put(id, percentageDouble);
						break;
					case GradebookService.GRADE_TYPE_LETTER:
						percentage = gradeCalculations.getPointsEarnedAsPercent((Assignment) gradeRecord.getGradableObject(), gradeRecord);
						String letterGrade = gradeCalculations.convertPercentageToLetterGrade(percentage);
						cellMap.put(id, letterGrade);
						break;
					default:
						cellMap.put(id, "Not implemented");
						break;
				}
			}
		}

		return cellMap;
	}

	private StudentModel buildStudentRow(Gradebook gradebook, UserRecord userRecord, List<FixedColumnModel> columns, List<Assignment> assignments, List<Category> categories) {

		Map<Long, AssignmentGradeRecord> studentGradeMap = userRecord.getGradeRecordMap();

		// This is an intermediate map for data to be placed in the record
		Map<String, Object> cellMap = new HashMap<String, Object>();

		// This is how we track which column is which - by the user's uid
		cellMap.put(StudentModel.Key.UID.name(), userRecord.getUserUid());
		cellMap.put(StudentModel.Key.EID.name(), userRecord.getUserEid());
		cellMap.put(StudentModel.Key.EXPORT_CM_ID.name(), userRecord.getExportCourseManagemntId());
		cellMap.put(StudentModel.Key.EXPORT_USER_ID.name(), userRecord.getExportUserId());
		cellMap.put(StudentModel.Key.FINAL_GRADE_USER_ID.name(), userRecord.getFinalGradeUserId());
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
			for (FixedColumnModel column : columns) {
				StudentModel.Key key = StudentModel.Key.valueOf(column.getKey());
				switch (key) {
					case DISPLAY_ID:
						cellMap.put(StudentModel.Key.DISPLAY_ID.name(), userRecord.getDisplayId());
						break;
					case DISPLAY_NAME:
						// For the single view, maybe some redundancy, but not
						// much
						String displayName = userRecord.getDisplayName();

						if (displayName == null)
							displayName = "[User name not found]";

						cellMap.put(StudentModel.Key.DISPLAY_NAME.name(), displayName);
						cellMap.put(StudentModel.Key.LAST_NAME_FIRST.name(), userRecord.getLastNameFirst());
						cellMap.put(StudentModel.Key.EMAIL.name(), userRecord.getEmail());
						break;
					case SECTION:
						cellMap.put(StudentModel.Key.SECTION.name(), userRecord.getSectionTitle());
						break;
					case COURSE_GRADE:
						if (displayGrade != null)
						{
							cellMap.put(StudentModel.Key.COURSE_GRADE.name(), displayGrade.toString());
							GWT.log("setting override to " + displayGrade.isOverridden(),null);
							cellMap.put(StudentModel.Key.IS_GRADE_OVERRIDDEN.name(), Boolean.toString(displayGrade.isOverridden()));
						}
						break;
					case GRADE_OVERRIDE:
						cellMap.put(StudentModel.Key.GRADE_OVERRIDE.name(), enteredGrade);
						break;
					case CALCULATED_GRADE:
						cellMap.put(StudentModel.Key.CALCULATED_GRADE.name(), displayGrade.getCalculatedGradeAsString());
						break;
					case LETTER_GRADE:
						cellMap.put(StudentModel.Key.LETTER_GRADE.name(), displayGrade.getLetterGrade());
						break;
				};
			}
		}

		if (assignments != null) {

			for (Assignment assignment : assignments) {
				cellMap = appendItemData(assignment.getId(), cellMap, userRecord, gradebook);
			}

		} else {

			for (AssignmentGradeRecord gradeRecord : studentGradeMap.values()) {
				Assignment assignment = gradeRecord.getAssignment();
				cellMap = appendItemData(assignment.getId(), cellMap, userRecord, gradebook);
			}

		}

		return new StudentModel(cellMap);
	}

	private void calculateItemCategoryPercent(Gradebook gradebook, Category category, ItemModel gradebookItemModel, ItemModel categoryItemModel, List<Assignment> assignments, Long assignmentId) {

		double pG = categoryItemModel == null || categoryItemModel.getPercentCourseGrade() == null ? 0d : categoryItemModel.getPercentCourseGrade().doubleValue();

		boolean isWeighted = gradebook.getCategory_type() == GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY;
		boolean isCategoryExtraCredit = category != null && DataTypeConversionUtil.checkBoolean(category.isExtraCredit());
		boolean isEnforcePointWeighting = category != null && DataTypeConversionUtil.checkBoolean(category.isEnforcePointWeighting());
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
				
				ItemModel assignmentItemModel = createItemModel(category, a, courseGradePercent, percentCategory);

				if (assignmentId != null && a.getId().equals(assignmentId))
					assignmentItemModel.setActive(true);

				if (gradebook.getCategory_type() == GradebookService.CATEGORY_TYPE_NO_CATEGORY) {
					assignmentItemModel.setParent(gradebookItemModel);
					gradebookItemModel.add(assignmentItemModel);
				} else {
					assignmentItemModel.setParent(categoryItemModel);
					categoryItemModel.add(assignmentItemModel);
				}
			}

		}

		if (gradebookItemModel != null) {
			gradebookItemModel.setPoints(Double.valueOf(pointsSum.doubleValue()));
		}

		if (categoryItemModel != null) {
			if (isWeighted) {
				if (isEnforcePointWeighting) {
					if (pointsSum.compareTo(BigDecimal.ZERO) > 0)
						categoryItemModel.setPercentCategory(Double.valueOf(100d));
					else
						categoryItemModel.setPercentCategory(Double.valueOf(0d));
				} else
					categoryItemModel.setPercentCategory(Double.valueOf(percentCategorySum.doubleValue()));
			} else {
				categoryItemModel.setPercentCategory(null);
			}
			categoryItemModel.setPoints(Double.valueOf(pointsSum.doubleValue()));
		}
	}

	private GradebookModel createGradebookModel(Gradebook gradebook, List<Assignment> assignments, List<Category> categories, boolean isNewGradebook) {

		
		log.debug("createGradebookModel() called"); 
		Site site = null;

		if (siteService != null) {
			try {
				site = siteService.getSite(getSiteContext());

				if (site.getId().equals(gradebook.getName())) {
					gradebook.setName("My Default Gradebook");
				}

			} catch (IdUnusedException e) {
				log.error("Unable to find the current site", e);
			}
		}


		GradebookModel model = new GradebookModel();
		//model.setNewGradebook(Boolean.valueOf(isNewGradebook));
		String gradebookUid = gradebook.getUid();

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

		boolean isUserAbleToGrade = authz.isUserAbleToGrade(gradebookUid);
		boolean isUserAbleToViewOwnGrades = authz.isUserAbleToViewOwnGrades(gradebookUid);

		boolean isSingleUserView = isUserAbleToViewOwnGrades && !isUserAbleToGrade;


		model.setCategoryType(categoryType);

		model.setGradebookUid(gradebookUid);
		model.setGradebookId(gradebook.getId());
		model.setName(gradebook.getName());

		// GRBK-233 create new assignment and category list
		ItemModel gradebookItemModel = null;

		if(null != categories) {
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
			
			gradebookItemModel = getItemModel(gradebook, filteredAssignments, filteredCategories, null, null);
		}
		else {
			gradebookItemModel = getItemModel(gradebook, assignments, categories, null, null);
		}
		
		model.setNewGradebook(Boolean.valueOf(assignments == null || assignments.isEmpty()));

		model.setGradebookItemModel(gradebookItemModel);
		List<FixedColumnModel> columns = getColumns();

		model.setUserAbleToGrade(Boolean.valueOf(isUserAbleToGrade));
		model.setUserAbleToEditAssessments(Boolean.valueOf(authz.isUserAbleToEditAssessments(gradebookUid)));
		model.setUserAbleToViewOwnGrades(Boolean.valueOf(isUserAbleToViewOwnGrades));
		model.setUserHasGraderPermissions(Boolean.valueOf(authz.hasUserGraderPermissions(gradebookUid)));

		ConfigurationModel configModel = new ConfigurationModel();

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
					String calcGradeHiddenId = ConfigurationModel.getColumnHiddenId(AppConstants.ITEMTREE, StudentModel.Key.CALCULATED_GRADE.name());
					if (configModel.get(calcGradeHiddenId) == null)
						configModel.set(calcGradeHiddenId, "true");
					
					String letterGradeHiddenId = ConfigurationModel.getColumnHiddenId(AppConstants.ITEMTREE, StudentModel.Key.LETTER_GRADE.name());
					if (configModel.get(letterGradeHiddenId) == null)
						configModel.set(letterGradeHiddenId, "true");
				}

				List<String> legacySelectedMultiGradeColumns = configModel.getSelectedMultigradeColumns();

				if (legacySelectedMultiGradeColumns != null && !legacySelectedMultiGradeColumns.isEmpty()) {

					if (columns != null) {

						for (FixedColumnModel c : columns) {

							String identifier = c.getIdentifier();

							if (!identifier.equals(StudentModel.Key.LAST_NAME_FIRST.name())
									&& !identifier.equals(StudentModel.Key.COURSE_GRADE.name())
									&& !identifier.equals(StudentModel.Key.GRADE_OVERRIDE.name())) {
								if (!legacySelectedMultiGradeColumns.contains(identifier)) {
									createOrUpdateConfigurationModel(gradebook.getId(), ConfigurationModel.getColumnHiddenId(AppConstants.ITEMTREE, identifier), "true");								
									configModel.setColumnHidden(AppConstants.ITEMTREE, identifier, Boolean.TRUE);
								} 
							}
						}


					}

					if (assignments != null) {

						for (Assignment a : assignments) {

							String identifier = String.valueOf(a.getId());

							if (!legacySelectedMultiGradeColumns.contains(identifier)) {
								createOrUpdateConfigurationModel(gradebook.getId(), ConfigurationModel.getColumnHiddenId(AppConstants.ITEMTREE, identifier), "true");								
								configModel.setColumnHidden(AppConstants.ITEMTREE, identifier, Boolean.TRUE);
							} 

						}


					}

					gbService.deleteUserConfiguration(getCurrentUser(), gradebook.getId(), AppConstants.SELECTED_COLUMNS);
				}


				// Don't take the hit of looking this stuff up unless we're in
				// single user view
				if (isSingleUserView) {

					UserRecord userRecord = new UserRecord(user);
					if (site != null) {

						Collection<Group> groups = site.getGroupsWithMember(user.getId());
						if (!groups.isEmpty()) {
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
					
					CourseGradeRecord courseGradeRecord = gbService.getStudentCourseGradeRecord(gradebook, user.getId());
					userRecord.setCourseGradeRecord(courseGradeRecord);

					List<AssignmentGradeRecord> records = gbService.getAssignmentGradeRecordsForStudent(gradebook.getId(), userRecord.getUserUid());

					if (records != null) {
						for (AssignmentGradeRecord gradeRecord : records) {
							gradeRecord.setUserAbleToView(true);
							String studentUid = gradeRecord.getStudentId();
							Map<Long, AssignmentGradeRecord> studentMap = userRecord.getGradeRecordMap();
							if (studentMap == null) {
								studentMap = new HashMap<Long, AssignmentGradeRecord>();
								userRecord.setGradeRecordMap(studentMap);
							}
							GradableObject go = gradeRecord.getGradableObject();
							studentMap.put(go.getId(), gradeRecord);
						}
					}

					model.setUserAsStudent(buildStudentRow(gradebook, userRecord, columns, assignments, categories));
				}
				List<StatisticsModel> statsList = generateStatsList(model.getGradebookUid(), model.getGradebookId(), user.getId());		
				Collections.sort(statsList); 
				
				model.setStatsModel(statsList);

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

	private ItemModel createItemModel(Gradebook gradebook) {

		ItemModel itemModel = new ItemModel();
		itemModel.setName(gradebook.getName());
		itemModel.setItemType(Type.GRADEBOOK);
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
		
		return itemModel;
	}

	private ItemModel createItemModel(Gradebook gradebook, Category category, List<Assignment> assignments) {

		ItemModel model = new ItemModel();

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
		if (hasWeights)
			model.setPercentCourseGrade(Double.valueOf(categoryWeight));
		model.setItemType(Type.CATEGORY);
		model.setEditable(!isDefaultCategory);
		model.setItemOrder(category.getCategoryOrder());
		model.setEnforcePointWeighting(category.isEnforcePointWeighting());

		return model;
	}

	private ItemModel createItemModel(Category category, Assignment assignment, BigDecimal percentCourseGrade, BigDecimal percentCategory) {

		ItemModel model = new ItemModel();

		double assignmentWeight = assignment.getAssignmentWeighting() == null ? 0d : assignment.getAssignmentWeighting().doubleValue() * 100.0;
		Boolean isAssignmentIncluded = assignment.isUnweighted() == null ? Boolean.TRUE : Boolean.valueOf(!assignment.isUnweighted().booleanValue());
		Boolean isAssignmentExtraCredit = assignment.isExtraCredit() == null ? Boolean.FALSE : assignment.isExtraCredit();
		Boolean isAssignmentReleased = Boolean.valueOf(assignment.isReleased());
		Boolean isAssignmentRemoved = Boolean.valueOf(assignment.isRemoved());
		double points = assignment.getPointsPossible() == null ? 0d : assignment.getPointsPossible().doubleValue();
		
		
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

			isAssignmentExtraCredit = Boolean.valueOf(DataTypeConversionUtil.checkBoolean(isAssignmentExtraCredit) || DataTypeConversionUtil.checkBoolean(category.isExtraCredit()));
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
		if (isLetterGrading)
			model.setDataType(AppConstants.STRING_DATA_TYPE);
		else
			model.setDataType(AppConstants.NUMERIC_DATA_TYPE);
		model.setStudentModelKey(Key.ASSIGNMENT.name());
		model.setItemOrder(assignment.getItemOrder());

		//boolean isEnforcePointWeighting = DataTypeConversionUtil.checkBoolean(category.isEnforcePointWeighting()) && hasWeights;
		
		/*BigDecimal ratio = null;
		if (percentCourseGrade == null && hasCategories && category != null) {
			List<Assignment> assignments = category.getAssignmentList();

			boolean isIncluded = category.isUnweighted() == null ? true : !category.isUnweighted().booleanValue();

			BigDecimal sum = BigDecimal.ZERO;
			if (assignments != null && isIncluded) {
				for (Assignment a : assignments) {
					double assignWeight = a.getAssignmentWeighting() == null ? 0d : a.getAssignmentWeighting().doubleValue() * 100.0;
					
					if (isEnforcePointWeighting)
						assignWeight = a.getPointsPossible() == null ? 0d : a.getPointsPossible().doubleValue();
					
					boolean isExtraCredit = a.isExtraCredit() != null && a.isExtraCredit().booleanValue();
					boolean isUnweighted = a.isUnweighted() != null && a.isUnweighted().booleanValue();
					if (!isExtraCredit && !isUnweighted)
						sum = sum.add(BigDecimal.valueOf(assignWeight));
				}
			}
			if (isEnforcePointWeighting) {
				percentCourseGrade = BigDecimal.valueOf(category.getWeight() == null ? 0d : category.getWeight().doubleValue());
				ratio = percentCourseGrade.divide(sum, GradeCalculations.MATH_CONTEXT);
			} else
				percentCourseGrade = sum;
		}
		*/
		
		if (hasWeights) {
			if (percentCategory != null)
				model.setPercentCategory(Double.valueOf(percentCategory.doubleValue()));
			
			if (percentCourseGrade != null)
				model.setPercentCourseGrade(Double.valueOf(percentCourseGrade.doubleValue()));
		} else {
			model.setPercentCategory(null);
			model.setPercentCourseGrade(null);
		}
		
		model.setItemType(Type.ITEM);

		return model;
	}

	private CommentModel createOrUpdateCommentModel(CommentModel model, Comment comment) {

		if (comment == null)
			return null;

		if (model == null) {
			model = new CommentModel();
		}

		String graderName = "";
		if (userService != null) {
			try {
				User grader = userService.getUser(comment.getGraderId());
				if (grader != null)
					graderName = grader.getDisplayName();
			} catch (UserNotDefinedException e) {
				log.warn("Couldn't find the grader for " + comment.getGraderId());
			}
		}

		if (comment.getId() != null)
			model.setIdentifier(String.valueOf(comment.getId()));
		model.setAssignmentId(comment.getGradableObject().getId());
		model.setText(comment.getCommentText());
		model.setGraderName(graderName);
		model.setStudentUid(comment.getStudentId());

		return model;
	}

	private GradeEventModel createOrUpdateGradeEventModel(GradeEventModel model, GradingEvent event) {

		SimpleDateFormat dateFormat = new SimpleDateFormat();

		if (model == null) {
			model = new GradeEventModel();
		}

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

		model.setIdentifier(String.valueOf(event.getId()));
		model.setGraderName(graderName);
		model.setGrade(event.getGrade());
		model.setDateGraded(dateFormat.format(event.getDateGraded()));

		return model;
	}

	private GradebookModel createOrRetrieveGradebookModel(String gradebookUid) {

		GradebookModel model = null;
		Gradebook gradebook = null;

		boolean isNewGradebook = false;

		try {
			// First thing, grab the default gradebook if one exists
			gradebook = gbService.getGradebook(gradebookUid);
		} catch (GradebookNotFoundException gnfe) {
			// If it doesn't exist, then create it
			if (frameworkService != null) {
				frameworkService.addGradebook(gradebookUid, "My Default Gradebook");
				gradebook = gbService.getGradebook(gradebookUid);
				isNewGradebook = true;
			}
		}

		// If we have a gradebook already, then we have to ensure that it's set
		// up correctly for the new tool
		if (gradebook != null) {

			List<Assignment> assignments = gbService.getAssignments(gradebook.getId());

			List<Category> categories = null;
			if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY)
				categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);

			model = createGradebookModel(gradebook, assignments, categories, isNewGradebook);
		}

		return model;
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

	protected List<UserRecord> findLearnerRecordPage(Gradebook gradebook, Site site, String[] realmIds, List<String> groupReferences, Map<String, Group> groupReferenceMap, String sortField, String searchField, String searchCriteria,
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
						userRecord.setExportCourseManagemntId(courseManagementIds.toString());
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

	protected List<User> findAllMembers(Site site, String[] learnerRoleKeys) {

		List<User> users = new ArrayList<User>();
		if (site != null) {
			List<String> userUids = gbService.getFullUserListForSite(site.getId(), learnerRoleKeys);

			if (userService != null && userUids != null)
				users = userService.getUsers(userUids);
		}
		return users;
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
							category.setCategoryOrder(categoryOrder++);
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
								defaultCategory.setCategoryOrder(categoryOrder++);
							} else
								categoryMap.put(defaultCategory.getId(), defaultCategory);
						}

						category = defaultCategory;
					}
				}

				if (null != category) {

					assignmentList = category.getAssignmentList();

					if (null == assignmentList) {
						assignmentList = new ArrayList<Assignment>();
						category.setAssignmentList(assignmentList);
					}

					if (!assignmentList.contains(assignment)) {
						Integer itemOrder = assignment.getItemOrder();
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

	private <X extends BaseModel> List<X> getColumns() {

		List<X> columns = new LinkedList<X>();

		columns.add((X) new FixedColumnModel(StudentModel.Key.DISPLAY_ID, 80, true));
		columns.add((X) new FixedColumnModel(StudentModel.Key.DISPLAY_NAME, 180, true));
		columns.add((X) new FixedColumnModel(StudentModel.Key.LAST_NAME_FIRST, 180, false));
		columns.add((X) new FixedColumnModel(StudentModel.Key.EMAIL, 230, true));
		columns.add((X) new FixedColumnModel(StudentModel.Key.SECTION, 120, true));
		columns.add((X) new FixedColumnModel(StudentModel.Key.COURSE_GRADE, 120, false));
		FixedColumnModel gradeOverrideColumn = new FixedColumnModel(StudentModel.Key.GRADE_OVERRIDE, 120, false);
		gradeOverrideColumn.setEditable(true);
		columns.add((X) gradeOverrideColumn);
		columns.add((X) new FixedColumnModel(StudentModel.Key.LETTER_GRADE, 80, true));
		columns.add((X) new FixedColumnModel(StudentModel.Key.CALCULATED_GRADE, 80, true));
		
		return columns;
	}
	
	
	private BigDecimal getCalculatedGrade(Gradebook gradebook, List<Assignment> assignments, List<Category> categories, Map<Long, AssignmentGradeRecord> studentGradeMap) {
		BigDecimal autoCalculatedGrade = null;

		boolean isScaledExtraCredit = DataTypeConversionUtil.checkBoolean(gradebook.isScaledExtraCredit());
		
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
		
		/*if (letterGrade != null) {
			StringBuilder buffer = new StringBuilder(letterGrade);

			if (isOverridden) {
				buffer.append(" (override)").append(missingGradesMarker);
			} else if (autoCalculatedGrade != null) {
				
				if (isLetterGradeMode)
					buffer.append(missingGradesMarker);
				else
					buffer.append(" (").append(autoCalculatedGrade.toString()).append("%) ").append(missingGradesMarker);
			}

			displayGrade = buffer.toString();
		}*/

		return displayGrade;
	}

	protected String lookupDefaultGradebookUid() {

		if (toolManager == null)
			return "TESTGRADEBOOK";

		Placement placement = toolManager.getCurrentPlacement();
		if (placement == null) {
			log.error("Placement is null!");
			return null;
		}

		return placement.getContext();
	}

	private ItemModel getActiveItem(ItemModel parent) {

		if (parent.isActive())
			return parent;

		for (ModelData m : parent.getChildren()) {
			ItemModel c = (ItemModel)m;
			if (c.isActive()) {
				return c;
			}

			if (c.getChildCount() > 0) {
				ItemModel activeItem = getActiveItem(c);

				if (activeItem != null)
					return activeItem;
			}
		}

		return null;
	}

	private List<GradebookModel> getGradebookModels(String[] gradebookUids) {

		List<GradebookModel> models = new LinkedList<GradebookModel>();

		if (gradebookUids == null || gradebookUids.length == 0)
			gradebookUids = new String[] { lookupDefaultGradebookUid() };

		for (int i = 0; i < gradebookUids.length; i++)
			models.add(createOrRetrieveGradebookModel(gradebookUids[i]));

		return models;
	}

	private ItemModel getItemModel(Gradebook gradebook, List<Assignment> assignments, List<Category> categories, Long categoryId, Long assignmentId) {

		ItemModel gradebookItemModel = createItemModel(gradebook);

		boolean isNotInCategoryMode = gradebook.getCategory_type() == GradebookService.CATEGORY_TYPE_NO_CATEGORY;

		if (isNotInCategoryMode) {
			calculateItemCategoryPercent(gradebook, null, gradebookItemModel, null, assignments, assignmentId);

		} else {

			if (categories != null) {
				BigDecimal gradebookWeightSum = BigDecimal.ZERO;
				BigDecimal gradebookPointsSum = BigDecimal.ZERO;
				for (Category category : categories) {
					boolean isExtraCredit = category.isExtraCredit() != null && category.isExtraCredit().booleanValue();
					boolean isUnweighted = category.isUnweighted() != null && category.isUnweighted().booleanValue();

					if (!category.isRemoved() || isNotInCategoryMode) {
						double categoryWeight = category.getWeight() == null ? 0d : category.getWeight().doubleValue() * 100d;

						List<Assignment> items = category.getAssignmentList();
						ItemModel categoryItemModel = createItemModel(gradebook, category, items);

						if (!isNotInCategoryMode) {
							categoryItemModel.setParent(gradebookItemModel);
							gradebookItemModel.add(categoryItemModel);
						}

						if (categoryId != null && category.getId().equals(categoryId))
							categoryItemModel.setActive(true);

						calculateItemCategoryPercent(gradebook, category, gradebookItemModel, categoryItemModel, items, assignmentId);

						double categoryPoints = categoryItemModel.getPoints() == null ? 0d : categoryItemModel.getPoints().doubleValue();

						if (!isExtraCredit && !isUnweighted) {
							gradebookWeightSum = gradebookWeightSum.add(BigDecimal.valueOf(categoryWeight));
							gradebookPointsSum = gradebookPointsSum.add(BigDecimal.valueOf(categoryPoints));
						}

					}
				}
				gradebookItemModel.setPoints(Double.valueOf(gradebookPointsSum.doubleValue()));
				gradebookItemModel.setPercentCourseGrade(Double.valueOf(gradebookWeightSum.doubleValue()));
			}
		}

		return gradebookItemModel;
	}

	private ItemModel getItemModelsForCategory(Category category, ItemModel gradebookItemModel, Long assignmentId) {

		if (category == null)
			return null;

		Gradebook gradebook = category.getGradebook();

		List<Assignment> assignments = gbService.getAssignmentsForCategory(category.getId());

		ItemModel categoryItemModel = createItemModel(gradebook, category, null);
		categoryItemModel.setParent(gradebookItemModel);
		gradebookItemModel.add(categoryItemModel);

		calculateItemCategoryPercent(gradebook, category, gradebookItemModel, categoryItemModel, assignments, assignmentId);

		return categoryItemModel;
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

	protected Site getSite() {

		String context = getSiteContext();
		Site site = null;

		try {

			if (siteService != null)
				site = siteService.getSite(context);
			else
				site = new SiteMock(getSiteId());

		} catch (IdUnusedException iue) {
			log.error("IDUnusedException : SiteContext = " + context);
			iue.printStackTrace();
		}

		return site;
	}

	protected String getSiteContext() {

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

	private void logActionRecord(ActionRecord actionRecord, ItemModel item) {

		Map<String, String> propertyMap = actionRecord.getPropertyMap();
		for (String propertyName : item.getPropertyNames()) {
			Object value = item.get(propertyName);
			if (value != null)
				propertyMap.put(propertyName, String.valueOf(value));
		}

		gbService.storeActionRecord(actionRecord);
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
				boolean isWeighted = assignment.isUnweighted() == null ? true : !assignment.isUnweighted().booleanValue();
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
					boolean isWeighted = assignment.isUnweighted() == null ? true : !assignment.isUnweighted().booleanValue();
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

	
	private StudentModel refreshLearnerData(Gradebook gradebook, StudentModel student, Assignment assignment, List<AssignmentGradeRecord> assignmentGradeRecords) {

		Map<Long, AssignmentGradeRecord> studentGradeMap = new HashMap<Long, AssignmentGradeRecord>();

		for (AssignmentGradeRecord gradeRecord : assignmentGradeRecords) {
			Assignment a = gradeRecord.getAssignment();
			studentGradeMap.put(a.getId(), gradeRecord);
		}

		// FIXME: There has to be a more efficient way of doing this -- all we
		// really need this for is to determine if the learner has been graded
		// for all assignments
		// FIXME: We should be able to replace that logic in getDisplayGrade
		// with a clever db query.
		List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
		List<Category> categories = null;
		if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY)
			categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);
		CourseGradeRecord courseGradeRecord = gbService.getStudentCourseGradeRecord(gradebook, student.getIdentifier());
		BigDecimal calculatedGrade = getCalculatedGrade(gradebook, assignments, categories, studentGradeMap);
		DisplayGrade displayGrade = getDisplayGrade(gradebook, student.getIdentifier(), courseGradeRecord, calculatedGrade);

		for (AssignmentGradeRecord record : assignmentGradeRecords) {
			Long aId = record.getGradableObject().getId();
			String dropProperty = concat(String.valueOf(aId), StudentModel.DROP_FLAG);
			String excuseProperty = concat(String.valueOf(aId), StudentModel.EXCUSE_FLAG);
			boolean isDropped = record.isDropped() != null && record.isDropped().booleanValue();
			boolean isExcluded = record.isExcluded() != null && record.isExcluded().booleanValue();

			if (isDropped)
				student.set(dropProperty, Boolean.TRUE);
			else
				student.set(dropProperty, null);

			if (isExcluded) {
				student.set(excuseProperty, Boolean.TRUE);
				student.set(dropProperty, Boolean.TRUE);
			} else {
				student.set(excuseProperty, null);
				if (!isDropped)
					student.set(dropProperty, null);
			}
		}

		String commentedProperty = assignment.getId() + StudentModel.COMMENTED_FLAG;
		if (gbService.isStudentCommented(student.getIdentifier(), assignment.getId()))
			student.set(commentedProperty, Boolean.TRUE);
		else
			student.set(commentedProperty, null);

		student.set(StudentModel.Key.CALCULATED_GRADE.name(), displayGrade.getCalculatedGradeAsString());
		student.set(StudentModel.Key.COURSE_GRADE.name(), displayGrade.toString());
		student.set(StudentModel.Key.LETTER_GRADE.name(), displayGrade.getLetterGrade());
		
		return student;
	}

	private AssignmentGradeRecord scoreItem(Gradebook gradebook, Assignment assignment, AssignmentGradeRecord assignmentGradeRecord, String studentUid, Double value, boolean includeExcluded, boolean deferUpdate)
	throws InvalidInputException {

		boolean isUserAbleToGrade = authz.isUserAbleToGradeAll(gradebook.getUid()) || authz.isUserAbleToGradeItemForStudent(gradebook.getUid(), assignment.getId(), studentUid);

		if (!isUserAbleToGrade)
			throw new InvalidInputException("You are not authorized to grade this student for this item.");

		if (assignment.isExternallyMaintained())
			throw new InvalidInputException("This grade item is maintained externally. Please input and edit grades through " + assignment.getExternalAppName());

		if (gradebook.getGrade_type() == GradebookService.GRADE_TYPE_POINTS && value != null) {
			if (value.compareTo(assignment.getPointsPossible()) > 0)
				throw new InvalidInputException("This grade cannot be larger than " + DataTypeConversionUtil.formatDoubleAsPointsString(assignment.getPointsPossible()));
			else if (value.compareTo(Double.valueOf(0d)) < 0) {
				double v = value.doubleValue();

				if (v < -1d * assignment.getPointsPossible().doubleValue())
					throw new InvalidInputException("The absolute value of a negative point score assigned to a student cannot be greater than the total possible points allowed for an item");
			}

		} else if (gradebook.getGrade_type() == GradebookService.GRADE_TYPE_PERCENTAGE && value != null) {
			if (value.compareTo(Double.valueOf(100d)) > 0)
				throw new InvalidInputException("This grade cannot be larger than " + DataTypeConversionUtil.formatDoubleAsPointsString(100d) + "%");

			else if (value.compareTo(Double.valueOf(0d)) < 0)
				throw new InvalidInputException("This grade cannot be less than " + DataTypeConversionUtil.formatDoubleAsPointsString(0d) + "%");
		}

		if (!includeExcluded && assignmentGradeRecord.isExcluded() != null && assignmentGradeRecord.isExcluded().booleanValue())
			throw new InvalidInputException("The student has been excused from this assignment. It is no longer possible to assign him or her a grade.");

		switch (gradebook.getGrade_type()) {
			case GradebookService.GRADE_TYPE_POINTS:
				assignmentGradeRecord.setPointsEarned(value);
				break;
			case GradebookService.GRADE_TYPE_PERCENTAGE:
			case GradebookService.GRADE_TYPE_LETTER:
				BigDecimal pointsEarned = gradeCalculations.getPercentAsPointsEarned(assignment, value);
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

	private ItemModel updateGradebookModel(ItemModel item) throws InvalidInputException {

		Gradebook gradebook = gbService.getGradebook(item.getIdentifier());

		ActionRecord actionRecord = new ActionRecord(gradebook.getUid(), gradebook.getId(), EntityType.GRADEBOOK.name(), ActionType.UPDATE.name());
		actionRecord.setEntityName(gradebook.getName());
		actionRecord.setEntityId(gradebook.getUid());

		logActionRecord(actionRecord, item);

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

		boolean isReleaseGrades = DataTypeConversionUtil.checkBoolean(item.getReleaseGrades());

		gradebook.setCourseGradeDisplayed(isReleaseGrades);

		boolean isReleaseItems = DataTypeConversionUtil.checkBoolean(item.getReleaseItems());

		gradebook.setAssignmentsDisplayed(isReleaseItems);

		boolean isExtraCreditScaled = DataTypeConversionUtil.checkBoolean(item.getExtraCreditScaled());
		
		gradebook.setScaledExtraCredit(Boolean.valueOf(isExtraCreditScaled));
		
		boolean isShowMean = DataTypeConversionUtil.checkBoolean(item.getShowMean());
		
		gradebook.setShowMean(Boolean.valueOf(isShowMean));
		
		boolean isShowMedian = DataTypeConversionUtil.checkBoolean(item.getShowMedian());
		
		gradebook.setShowMedian(Boolean.valueOf(isShowMedian));
		
		boolean isShowMode = DataTypeConversionUtil.checkBoolean(item.getShowMode());
		
		gradebook.setShowMode(Boolean.valueOf(isShowMode));

		boolean isShowRank = DataTypeConversionUtil.checkBoolean(item.getShowRank());
		
		gradebook.setShowRank(Boolean.valueOf(isShowRank));
		
		boolean isShowItemStatistics = DataTypeConversionUtil.checkBoolean(item.getShowItemStatistics());
		
		gradebook.setShowItemStatistics(Boolean.valueOf(isShowItemStatistics));
		
		GradeMapping mapping = gradebook.getSelectedGradeMapping();
		Long gradeScaleId = item.getGradeScaleId();
		if (mapping != null && gradeScaleId != null && !mapping.getId().equals(gradeScaleId)) {
			GradeMapping newMapping = gbService.getGradeMapping(gradeScaleId);
			gradebook.setSelectedGradeMapping(newMapping);
		}

		gbService.updateGradebook(gradebook);

		List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
		List<Category> categories = null;
		if (hasCategories)
			categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);
		return getItemModel(gradebook, assignments, categories, null, null);
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
	private ItemModel updateCategoryModel(ItemModel item) throws InvalidInputException {

		boolean isWeightChanged = false;

		Category category = gbService.getCategory(Long.valueOf(item.getIdentifier()));
		Gradebook gradebook = category.getGradebook();

		ActionRecord actionRecord = new ActionRecord(gradebook.getUid(), gradebook.getId(), EntityType.CATEGORY.name(), ActionType.UPDATE.name());
		actionRecord.setEntityName(category.getName());
		actionRecord.setEntityId(String.valueOf(category.getId()));

		Map<String, String> propertyMap = actionRecord.getPropertyMap();

		for (String property : item.getPropertyNames()) {
			String value = String.valueOf(item.get(property));
			if (value != null)
				propertyMap.put(property, value);
		}

		try {

			boolean originalExtraCredit = DataTypeConversionUtil.checkBoolean(category.isExtraCredit());
			boolean currentExtraCredit = DataTypeConversionUtil.checkBoolean(item.getExtraCredit());

			isWeightChanged = originalExtraCredit != currentExtraCredit;

			Double newCategoryWeight = item.getPercentCourseGrade();
			Double oldCategoryWeight = category.getWeight();

			isWeightChanged = isWeightChanged || DataTypeConversionUtil.notEquals(newCategoryWeight, oldCategoryWeight);

			double w = newCategoryWeight == null ? 0d : ((Double) newCategoryWeight).doubleValue() * 0.01;

			boolean isEqualWeighting = DataTypeConversionUtil.checkBoolean(item.getEqualWeightAssignments());
			boolean wasEqualWeighting = DataTypeConversionUtil.checkBoolean(category.isEqualWeightAssignments());

			isWeightChanged = isWeightChanged || isEqualWeighting != wasEqualWeighting;

			boolean isUnweighted = !DataTypeConversionUtil.checkBoolean(item.getIncluded());
			boolean wasUnweighted = DataTypeConversionUtil.checkBoolean(category.isUnweighted());

			if (wasUnweighted && !isUnweighted && category.isRemoved())
				throw new InvalidInputException("You cannot include a deleted category in grade. Please undelete the category first.");

			int newDropLowest = convertInteger(item.getDropLowest()).intValue();
			int oldDropLowest = category.getDrop_lowest();
			
			boolean isRemoved = DataTypeConversionUtil.checkBoolean(item.getRemoved());
			boolean wasRemoved = category.isRemoved();

			Integer newCategoryOrder = item.getItemOrder();
			Integer oldCategoryOrder = category.getCategoryOrder();

			boolean hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;
			boolean hasWeights = gradebook.getCategory_type() == GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY;

			boolean isEnforcePointWeighting = !currentExtraCredit && DataTypeConversionUtil.checkBoolean(item.getEnforcePointWeighting());
			
			
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
			}

		} catch (RuntimeException e) {
			actionRecord.setStatus(ActionRecord.STATUS_FAILURE);
			throw e;
		} finally {
			gbService.storeActionRecord(actionRecord);
		}

		List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
		List<Category> categories = getCategoriesWithAssignments(gradebook.getId(), assignments, true);

		ItemModel gradebookItemModel = getItemModel(gradebook, assignments, categories, category.getId(), null);

		return gradebookItemModel;
	}

	public String getCurrentUser() {

		if(null == sessionManager) {
			return "0";
		}

		return sessionManager.getCurrentSessionUserId();
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

	/*
	 * GENERAL HELPER METHODS
	 */
	private String[] getLearnerRoleNames() {
		return learnerRoleNames;
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

	private String convertString(Object value) {

		return value == null ? "" : (String) value;
	}

	private Date convertDate(Object value) {

		return value == null ? null : (Date) value;
	}

	private Double convertDouble(Object value) {

		return value == null ? Double.valueOf(0.0) : (Double) value;
	}

	private Boolean convertBoolean(Object value) {

		return value == null ? Boolean.FALSE : (Boolean) value;
	}

	private Integer convertInteger(Object value) {

		return value == null ? Integer.valueOf(0) : (Integer) value;
	}

	/**
	 * INNER CLASSES
	 */

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

	static final Comparator<EnrollmentRecord> ENROLLMENT_NAME_COMPARATOR = new Comparator<EnrollmentRecord>() {

		public int compare(EnrollmentRecord o1, EnrollmentRecord o2) {

			return o1.getUser().getSortName().compareToIgnoreCase(o2.getUser().getSortName());
		}
	};

	static final Comparator<UserRecord> DEFAULT_ID_COMPARATOR = new Comparator<UserRecord>() {

		public int compare(UserRecord o1, UserRecord o2) {

			if (o1.getUserUid() == null || o2.getUserUid() == null)
				return 0;

			return o1.getUserUid().compareToIgnoreCase(o2.getUserUid());
		}
	};

	static final Comparator<UserRecord> SORT_NAME_COMPARATOR = new Comparator<UserRecord>() {

		public int compare(UserRecord o1, UserRecord o2) {

			if (o1.getSortName() == null || o2.getSortName() == null)
				return 0;

			return o1.getSortName().compareToIgnoreCase(o2.getSortName());
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

	static final Comparator<UserRecord> SECTION_TITLE_COMPARATOR = new Comparator<UserRecord>() {

		public int compare(UserRecord o1, UserRecord o2) {

			if (o1.getSectionTitle() == null || o2.getSectionTitle() == null)
				return 0;

			return o1.getSectionTitle().compareToIgnoreCase(o2.getSectionTitle());
		}
	};

	/*
	 * DEPENDENCY INJECTION ACCESSORS
	 */

	public GradebookFrameworkService getFrameworkService() {

		return frameworkService;
	}

	public void setFrameworkService(GradebookFrameworkService frameworkService) {

		this.frameworkService = frameworkService;
	}

	public GradebookToolService getGbService() {

		return gbService;
	}

	public void setGbService(GradebookToolService gbService) {

		this.gbService = gbService;
	}

	public GradeCalculations getGradeCalculations() {

		return gradeCalculations;
	}

	public void setGradeCalculations(GradeCalculations gradeCalculations) {

		this.gradeCalculations = gradeCalculations;
	}

	public void setAuthz(Gradebook2Authz authz) {

		this.authz = authz;
	}

	public void setSectionAwareness(SectionAwareness sectionAwareness) {

		this.sectionAwareness = sectionAwareness;
	}

	public InstitutionalAdvisor getAdvisor() {

		return advisor;
	}

	public void setAdvisor(InstitutionalAdvisor advisor) {

		this.advisor = advisor;
	}

	public SiteService getSiteService() {

		return siteService;
	}

	public void setSiteService(SiteService siteService) {

		this.siteService = siteService;
	}

	public ToolManager getToolManager() {

		return toolManager;
	}

	public void setToolManager(ToolManager toolManager) {

		this.toolManager = toolManager;
	}

	public UserDirectoryService getUserService() {

		return userService;
	}

	public void setUserService(UserDirectoryService userService) {

		this.userService = userService;
	}

	public BusinessLogic getBusinessLogic() {

		return businessLogic;
	}

	public void setBusinessLogic(BusinessLogic businessLogic) {

		this.businessLogic = businessLogic;
	}

	public SessionManager getSessionManager() {

		return sessionManager;
	}

	public void setSessionManager(SessionManager sessionManager) {

		this.sessionManager = sessionManager;
	}

	public ServerConfigurationService getConfigService() {

		return configService;
	}

	public void setConfigService(ServerConfigurationService configService) {

		this.configService = configService;
	}

	public EventTrackingService getEventTrackingService() {
		return eventTrackingService;
	}

	public void setEventTrackingService(EventTrackingService eventTrackingService) {
		this.eventTrackingService = eventTrackingService;
	}



}
