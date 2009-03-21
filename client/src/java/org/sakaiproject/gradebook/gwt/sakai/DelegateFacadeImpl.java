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
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.GradebookToolFacade;
import org.sakaiproject.gradebook.gwt.client.action.Action;
import org.sakaiproject.gradebook.gwt.client.action.PageRequestAction;
import org.sakaiproject.gradebook.gwt.client.action.UserAssignmentCreateAction;
import org.sakaiproject.gradebook.gwt.client.action.UserCategoryCreateAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityCreateAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityGetAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityGradeAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.MultiGradeLoadConfig;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationModel;
import org.sakaiproject.gradebook.gwt.client.model.AssignmentModel;
import org.sakaiproject.gradebook.gwt.client.model.CategoryModel;
import org.sakaiproject.gradebook.gwt.client.model.ColumnModel;
import org.sakaiproject.gradebook.gwt.client.model.CommentModel;
import org.sakaiproject.gradebook.gwt.client.model.EntityModel;
import org.sakaiproject.gradebook.gwt.client.model.GradeEventModel;
import org.sakaiproject.gradebook.gwt.client.model.GradeRecordModel;
import org.sakaiproject.gradebook.gwt.client.model.GradeScaleRecordModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.SectionModel;
import org.sakaiproject.gradebook.gwt.client.model.SpreadsheetModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel.Key;
import org.sakaiproject.gradebook.gwt.sakai.model.ActionRecord;
import org.sakaiproject.gradebook.gwt.server.DataTypeConversionUtil;
import org.sakaiproject.section.api.SectionAwareness;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.section.api.coursemanagement.EnrollmentRecord;
import org.sakaiproject.section.api.facade.Role;
import org.sakaiproject.service.gradebook.shared.GradebookExistsException;
import org.sakaiproject.service.gradebook.shared.GradebookFrameworkService;
import org.sakaiproject.service.gradebook.shared.GradebookNotFoundException;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Placement;
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
import org.sakaiproject.tool.gradebook.GradingScale;
import org.sakaiproject.tool.gradebook.Permission;
import org.sakaiproject.tool.gradebook.facades.Authn;
import org.sakaiproject.tool.gradebook.facades.Authz;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.core.client.GWT;

public class DelegateFacadeImpl implements GradebookToolFacade {

private static final long serialVersionUID = 1L;
	
	private static final Log log = LogFactory.getLog(GradebookToolFacadeImpl.class);
	
	private SectionAwareness sectionAwareness;
	private Authz authz;
	private Authn authn;
	protected GradebookToolService gbService;
	private ToolManager toolManager;
	private UserDirectoryService userService;
	private GradeCalculations gradeCalculations;
	private GradebookFrameworkService frameworkService;
	private SiteService siteService;
	private ExportAdvisor exportAdvisor;

	
	@SuppressWarnings("unchecked")
	public <X extends EntityModel> X createEntity(UserEntityCreateAction<X> action) throws FatalException {
	
		X entity = null;
		
		try {
		
			ActionRecord actionRecord = new ActionRecord(action.getGradebookUid(), action.getGradebookId(), action.getEntityType().name(), action.getActionType().name());
			
			Map<String, String> actionRecordPropertyMap = actionRecord.getPropertyMap();
			
			if (action.getDatePerformed() != null)
				actionRecord.setDatePerformed(action.getDatePerformed());
			
			switch (action.getEntityType()) {
			case GRADEBOOK:
				actionRecord.setEntityName(action.getName());
				entity = (X)addGradebook(action.getName());
				break;
			case GRADE_ITEM:
				UserAssignmentCreateAction assignmentCreateAction = (UserAssignmentCreateAction)action;
				
				actionRecord.setParentId(String.valueOf(action.getParentId()));
				actionRecord.setEntityName(action.getEntityName());
				
				//actionRecordPropertyMap.put(Action.Key.NAME.name(), action.getName());
				actionRecordPropertyMap.put(Action.Key.WEIGHT.name(), String.valueOf(action.getWeight()));
				actionRecordPropertyMap.put(Action.Key.POINTS.name(), String.valueOf(assignmentCreateAction.getPoints()));
				actionRecordPropertyMap.put(Action.Key.DUE_DATE.name(), String.valueOf(assignmentCreateAction.getDueDate()));
				
				entity = (X)addAssignment(action.getGradebookUid(), action.getGradebookId(), 
						action.getParentId(), action.getName(), action.getWeight(), 
						assignmentCreateAction.getPoints(), 
						assignmentCreateAction.getDueDate());
				
				break;
			case CATEGORY:
				UserCategoryCreateAction categoryCreateAction = (UserCategoryCreateAction)action;
				
				actionRecord.setEntityName(action.getEntityName());
				
				//actionRecordPropertyMap.put(Action.Key.NAME.name(), action.getName());
				actionRecordPropertyMap.put(Action.Key.WEIGHT.name(), String.valueOf(action.getWeight()));
				actionRecordPropertyMap.put(Action.Key.EQUAL_WEIGHT.name(), String.valueOf(categoryCreateAction.getIsEqualWeight()));
				actionRecordPropertyMap.put(Action.Key.DROP_LOWEST.name(), String.valueOf(categoryCreateAction.getDropLowest()));
				
				entity = (X)addCategory(action.getGradebookUid(), action.getGradebookId(),
						action.getName(), action.getWeight(), categoryCreateAction.getIsEqualWeight(), 
						categoryCreateAction.getDropLowest());
				break;
			case COMMENT:
				CommentModel commentModel = (CommentModel)action.getModel();
				
				if (commentModel != null) {
					actionRecord.setParentId(String.valueOf(commentModel.getAssignmentId()));
					actionRecord.setStudentUid(commentModel.getStudentUid());
					actionRecordPropertyMap.put(Action.Key.TEXT.name(), commentModel.getText());			
				}
				
				entity = (X)createOrUpdateComment(commentModel.getAssignmentId(), commentModel.getStudentUid(), commentModel.getText());
				break;
			case SPREADSHEET:
				SpreadsheetModel spreadsheetModel = (SpreadsheetModel)action.getModel();
				
				entity = (X)createOrUpdateSpreadsheet(action.getGradebookUid(), spreadsheetModel);
			}
			
			if (entity != null && entity instanceof EntityModel)
				actionRecord.setEntityId(((EntityModel)entity).getIdentifier());
			
			if (entity == null)
				actionRecord.setStatus(ActionRecord.STATUS_FAILURE);
			else
				actionRecord.setStatus(ActionRecord.STATUS_SUCCESS);
			
			gbService.storeActionRecord(actionRecord);
		
		} catch (Throwable t) {
			log.warn("FatalException: ", t);
			throw new FatalException(t.getMessage(), t);
		}
		
		return entity;
	}
	
	public <X extends ItemModel> X createItemEntity(UserEntityCreateAction<X> action) throws FatalException {
		
		X entity = null;
		
		try {
		
			ActionRecord actionRecord = new ActionRecord(action.getGradebookUid(), action.getGradebookId(), action.getEntityType().name(), action.getActionType().name());
			
			Map<String, String> actionRecordPropertyMap = actionRecord.getPropertyMap();
			
			if (action.getDatePerformed() != null)
				actionRecord.setDatePerformed(action.getDatePerformed());
			
			switch (action.getEntityType()) {
			/*case GRADEBOOK:
				actionRecord.setEntityName(action.getName());
				entity = (X)addGradebook(action.getName());
				break;*/
			case GRADE_ITEM:
				//UserAssignmentCreateAction assignmentCreateAction = (UserAssignmentCreateAction)action;
				
				actionRecord.setParentId(String.valueOf(action.getParentId()));
				actionRecord.setEntityName(action.getEntityName());
				
				Double points = action.get(ItemModel.Key.POINTS.name());
				Date dueDate = action.get(ItemModel.Key.DUE_DATE.name());
				
				//actionRecordPropertyMap.put(Action.Key.NAME.name(), action.getName());
				actionRecordPropertyMap.put(Action.Key.WEIGHT.name(), String.valueOf(action.getWeight()));
				actionRecordPropertyMap.put(Action.Key.POINTS.name(), String.valueOf(points));
				actionRecordPropertyMap.put(Action.Key.DUE_DATE.name(), String.valueOf(dueDate));
				
				entity = (X)addItem(action.getGradebookUid(), action.getGradebookId(), 
						action.getModel());
				
				break;
			case CATEGORY:
				actionRecord.setEntityName(action.getEntityName());
				
				Boolean equalWeight = action.get(ItemModel.Key.EQUAL_WEIGHT.name());
				Integer dropLowest = action.get(ItemModel.Key.DROP_LOWEST.name());
				
				//actionRecordPropertyMap.put(Action.Key.NAME.name(), action.getName());
				actionRecordPropertyMap.put(Action.Key.WEIGHT.name(), String.valueOf(action.getWeight()));
				actionRecordPropertyMap.put(Action.Key.EQUAL_WEIGHT.name(), String.valueOf(equalWeight));
				actionRecordPropertyMap.put(Action.Key.DROP_LOWEST.name(), String.valueOf(dropLowest));
				
				entity= (X)addItemCategory(action.getGradebookUid(), action.getGradebookId(),
						action.getModel());
				break;
			/*case COMMENT:
				CommentModel commentModel = (CommentModel)action.getModel();
				
				if (commentModel != null) {
					actionRecord.setParentId(String.valueOf(commentModel.getAssignmentId()));
					actionRecord.setStudentUid(commentModel.getStudentUid());
					actionRecordPropertyMap.put(Action.Key.TEXT.name(), commentModel.getText());			
				}
				
				entity = (X)createOrUpdateComment(commentModel.getAssignmentId(), commentModel.getStudentUid(), commentModel.getText());
				break;
			case SPREADSHEET:
				SpreadsheetModel spreadsheetModel = (SpreadsheetModel)action.getModel();
				
				entity = (X)createOrUpdateSpreadsheet(action.getGradebookUid(), spreadsheetModel);*/
			}
			
			if (entity != null && entity instanceof ItemModel)
				actionRecord.setEntityId(((ItemModel)entity).getIdentifier());
			
			if (entity == null)
				actionRecord.setStatus(ActionRecord.STATUS_FAILURE);
			else
				actionRecord.setStatus(ActionRecord.STATUS_SUCCESS);
			
			gbService.storeActionRecord(actionRecord);
		
		} catch (Throwable t) {
			log.warn("FatalException: ", t);
			throw new FatalException(t.getMessage(), t);
		}
		
		return entity;
	}
	
	@SuppressWarnings("unchecked")
	public <X extends EntityModel> X getEntity(UserEntityGetAction<X> action) throws FatalException {
		
		try {
			boolean showAll = action.getIncludeAll() != null && action.getIncludeAll().booleanValue();
			
			switch (action.getEntityType()) {
			case APPLICATION:
				return (X)getApplicationModel();
			case COMMENT:
				return (X)getComment(Long.valueOf(action.getEntityId()), action.getStudentUid());
			case GRADEBOOK:
				return (X)getGradebook(action.getEntityId());
			case GRADE_RECORD:
				GradeRecordModel gradeRecord = (GradeRecordModel)action.getModel();
				return (X)getSingleAssignmentForStudent(action.getGradebookUid(), action.getGradebookId(), 
						action.getStudentUid(), Long.valueOf(action.getEntityId()), Boolean.valueOf(!showAll));
			}
		
		} catch (Throwable t) {
			log.warn("FatalException: ", t);
			throw new FatalException(t.getMessage(), t);
		}
		
		return null;
	}
	
	public <X extends EntityModel> List<X> getEntityList(UserEntityGetAction<X> action) throws FatalException {
		
		try {
		
			boolean showAll = action.getIncludeAll() != null && action.getIncludeAll().booleanValue();
			
			switch (action.getEntityType()) {
			case CATEGORY:
				return getCategories(action.getGradebookUid(), Boolean.FALSE);
			case COLUMN:	
				return getColumns();
			case GRADE_EVENT:
				return getGradeEvents(action.getStudentUid(), Long.valueOf(action.getEntityId()));
			case GRADE_ITEM:
				
				if (action.getGradebookId() == null) {
					Gradebook gradebook = gbService.getGradebook(action.getGradebookUid());
					action.setGradebookId(gradebook.getId());
				}
				
				return getAssignments(action.getGradebookUid(), action.getGradebookId(), showAll);
			case GRADE_SCALE:
				return getSelectedGradeMapping(action.getGradebookUid());
			case STUDENT:
				
				if (action.getGradebookId() == null) {
					Gradebook gradebook = gbService.getGradebook(action.getGradebookUid());
					action.setGradebookId(gradebook.getId());
				}
				
				PagingLoadResult<X> result = getStudentRows(action.getGradebookUid(), action.getGradebookId(), null);
				return result.getData();
			}
		
		} catch (Throwable t) {
			log.warn("FatalException: ", t);
			throw new FatalException(t.getMessage(), t);
		}
		
		return null;
	}
	
	public <X extends EntityModel> PagingLoadResult<X> getEntityPage(PageRequestAction action, PagingLoadConfig config) throws FatalException {
	
		try {
		
			boolean showAll = action.getIncludeAll() != null && action.getIncludeAll().booleanValue();
			
			switch (action.getEntityType()) {
			case ACTION:
				return getActionHistory(action.getGradebookUid(), config);
			case GRADE_ITEM:
				return getAssignments(action.getGradebookUid(), action.getGradebookId(), showAll, config);
			case CATEGORY:
				return getCategories(action.getGradebookUid(), action.getGradebookId(), showAll, config);
			case GRADE_RECORD:
				return getAssignmentRecords(action.getGradebookUid(), action.getGradebookId(), action.getStudentUid(), Boolean.valueOf(!showAll), config);
			case SECTION:
				return getSections(action.getGradebookUid(), action.getGradebookId(), config);
			case STUDENT:
				return getStudentRows(action.getGradebookUid(), action.getGradebookId(), config);
			}
		
		} catch (Throwable t) {
			log.warn("FatalException: ", t);
			throw new FatalException(t.getMessage(), t);
		}
		
		return null;
	}
	
	public <X extends ItemModel> X getEntityTreeModel(String gradebookUid, X parent) {
		
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		List<Category> categoriesWithAssignments = getCategoriesWithAssignments(gradebook.getId());
		return (X)getItemModel(gradebook, categoriesWithAssignments);
		/*
		List<X> items = null;
		// For null parent, return all categories
		if (parent == null) {
			Gradebook gradebook = gbService.getGradebook(gradebookUid);
			
			items = new ArrayList<X>();
			
			ItemModel itemModel = createItemModel(gradebook);
			itemModel.setItemType("Gradebook");
			items.add((X)itemModel);
			
		} else {
			String type = parent.getItemType();
			
			if (type.equals("Gradebook")) {
				Gradebook gradebook = gbService.getGradebook(gradebookUid);
				List<Category> categories = getCategoriesWithAssignments(gradebook.getId());//gbService.getCategories(gradebook.getId());
				
				items = new ArrayList<X>();
				for (Category category : categories) {
					ItemModel itemModel = createItemModel(gradebook, category);
					itemModel.setItemType("Category");
					items.add((X)itemModel);
				}
				
			} else if (type.equals("Category")) {
				Category category = gbService.getCategory(Long.valueOf(parent.getIdentifier()));
				items = new ArrayList<X>();
				List<Assignment> assignments = gbService.getAssignmentsForCategory(Long.valueOf(parent.getIdentifier()));
				
				for (Assignment assignment : assignments) {
					ItemModel itemModel = createItemModel(category, assignment);
					itemModel.setItemType("Item");
					items.add((X)itemModel);
				}
			} 
			
		}
		
		return items;*/
	}
	
	public <X extends ItemModel> X updateItemEntity(UserEntityUpdateAction<X> action) throws InvalidInputException, FatalException {
		X entity = null;
		
		try {
		
			if (action.isBulkUpdate()) {
				
				entity = (X)updateItemModel(action.getModel());
				
				/*
				switch (action.getEntityType()) {
				case CATEGORY:
					entity = (X)updateCategoryModel(action.getModel());
					break;
				case GRADEBOOK:
					entity = (X)updateGradebookModel(action.getModel());
					break;
				case ITEM:
					entity = (X)updateItemModel(action.getModel());
					break;
				}*/
				
				return entity;
			}
			
			
			if (action.getPrerequisiteAction() != null) {
				BaseModel result = updateEntity((UserEntityUpdateAction)action.getPrerequisiteAction());
				
				// Break out of recursion if we get a null back
				if (result == null) 
					return null;
			}
			
			ActionRecord actionRecord = new ActionRecord(action.getGradebookUid(), action.getGradebookId(), action.getEntityType().name(), action.getActionType().name());
			
			if (action.getEntityId() != null)
				actionRecord.setEntityId(action.getEntityId());
			if (action.getEntityName() != null)
				actionRecord.setEntityName(action.getEntityName());
			actionRecord.setField(action.getKey());
			if (action.getValue() != null)
				actionRecord.setValue(String.valueOf(action.getValue()));
			if (action.getStartValue() != null)
				actionRecord.setStartValue(String.valueOf(action.getStartValue()));
			if (action.getDatePerformed() != null)
				actionRecord.setDatePerformed(action.getDatePerformed());
			
			switch (action.getEntityType()) {
			case ITEM:
				ItemModel itemModel = (ItemModel)action.getModel();
				ItemModel.Key itemKey = ItemModel.Key.valueOf(action.getKey());
				
				switch (itemKey) {
				case POINTS:
					if (action.getDoRecalculateChildren() != null && action.getDoRecalculateChildren().booleanValue())
						recalculateAssignmentGradeRecords(itemModel.getIdentifier(), (Double)action.getValue(), (Double)action.getStartValue());
					break;
				}
				
				switch (itemModel.getItemType()) {
				case GRADEBOOK:
					entity = (X)updateGradebookField(itemModel, itemKey, action.getValue());
					break;
				case CATEGORY:
					String categoryId = String.valueOf(action.getEntityId());
					if (itemModel != null) {
						categoryId = itemModel.getIdentifier();
						
						if (categoryId.startsWith(Type.CATEGORY.getName()))
							categoryId = categoryId.substring(Type.CATEGORY.getName().length());
					}
					
					entity = (X)updateCategoryField(categoryId, itemKey, action.getValue());
					break;
				case ITEM:
					entity = (X)updateItemField(itemModel.getIdentifier(), itemKey, action.getValue());
					break;
				}
				
				break;
			}
			
			if (entity == null)
				actionRecord.setStatus(ActionRecord.STATUS_FAILURE);
			else
				actionRecord.setStatus(ActionRecord.STATUS_SUCCESS);
			
			if (action.getModel() != null) {
				actionRecord.setEntityId(action.getModel().getIdentifier());
			}
			
			gbService.storeActionRecord(actionRecord);
		
		} catch (InvalidInputException ie) {
			throw ie;
		} catch (Throwable t) {
			log.warn("FatalException: ", t);
			throw new FatalException(t.getMessage(), t);
		}
		
		return entity;
	}
	
	@SuppressWarnings("unchecked")
	public <X extends EntityModel> X updateEntity(UserEntityUpdateAction<X> action) throws InvalidInputException, FatalException {
		
		X entity = null;
		
		try {
		
			if (action.getPrerequisiteAction() != null) {
				BaseModel result = updateEntity((UserEntityUpdateAction)action.getPrerequisiteAction());
				
				// Break out of recursion if we get a null back
				if (result == null) 
					return null;
			}
			
			ActionRecord actionRecord = new ActionRecord(action.getGradebookUid(), action.getGradebookId(), action.getEntityType().name(), action.getActionType().name());
			
			if (action.getEntityId() != null)
				actionRecord.setEntityId(action.getEntityId());
			if (action.getEntityName() != null)
				actionRecord.setEntityName(action.getEntityName());
			actionRecord.setField(action.getKey());
			if (action.getValue() != null)
				actionRecord.setValue(String.valueOf(action.getValue()));
			if (action.getStartValue() != null)
				actionRecord.setStartValue(String.valueOf(action.getStartValue()));
			if (action.getDatePerformed() != null)
				actionRecord.setDatePerformed(action.getDatePerformed());
			
			switch (action.getEntityType()) {
			case GRADE_ITEM:
				AssignmentModel assignmentModel = (AssignmentModel)action.getModel();
				AssignmentModel.Key assignmentKey = AssignmentModel.Key.valueOf(action.getKey());
				
				switch (assignmentKey) {
				case POINTS:
					if (action.getDoRecalculateChildren() != null && action.getDoRecalculateChildren().booleanValue())
						recalculateAssignmentGradeRecords(assignmentModel.getIdentifier(), (Double)action.getValue(), (Double)action.getStartValue());
					break;
				}
				entity = (X)updateAssignmentField(assignmentModel.getIdentifier(), assignmentKey, action.getValue());
				break;
			case CATEGORY:
				CategoryModel categoryModel = (CategoryModel)action.getModel();
				String categoryId = String.valueOf(action.getEntityId());
				if (categoryModel != null)
					categoryId = categoryModel.getIdentifier();
				CategoryModel.Key categoryKey = CategoryModel.Key.valueOf(action.getKey());
				entity = (X)updateCategoryField(categoryId, categoryKey, action.getValue());
				break;
			case COMMENT:
				CommentModel commentModel = (CommentModel)action.getModel();
				
				if (commentModel != null) {
					actionRecord.setParentId(String.valueOf(commentModel.getAssignmentId()));
					actionRecord.setStudentUid(commentModel.getStudentUid());
				}
				
				entity = (X)createOrUpdateComment(commentModel.getAssignmentId(), commentModel.getStudentUid(), (String)action.getValue());
				break;
			case GRADEBOOK:
				GradebookModel gradebookModel = (GradebookModel)action.getModel();
				GradebookModel.Key gradebookKey = GradebookModel.Key.valueOf(action.getKey());
				entity = (X)updateGradebookField(gradebookModel.getGradebookId(), gradebookKey, action.getValue());
				break;
			case GRADE_RECORD:
				GradeRecordModel.Key recordModelKey = GradeRecordModel.Key.valueOf(action.getKey());
				entity = (X)updateGradeRecordModelField(action.getStudentModel().getIdentifier(), 
						(GradeRecordModel)action.getModel(), recordModelKey, action.getValue());
				break;
			case STUDENT:
				StudentModel student = (StudentModel)action.getModel();
				
				if (student != null && student.getIdentifier() != null) {
					actionRecord.setStudentUid(student.getIdentifier());
				}
				
				/*if (action.getPropertyName() != null) {
					Map<String, String> propertyMap = actionRecord.getPropertyMap();
					propertyMap.put(Action.Key.PROPERTY_NAME.name(), action.getPropertyName());
					actionRecord.setPropertyMap(propertyMap);
				}*/
				
				if (action.getKey().endsWith(StudentModel.COMMENT_TEXT_FLAG)) {
					
					int indexOf = action.getKey().indexOf(StudentModel.COMMENT_TEXT_FLAG);
					String assignmentId = action.getKey().substring(0, indexOf);
					
					CommentModel comment = createOrUpdateComment(Long.valueOf(assignmentId), student.getIdentifier(), (String)action.getValue());
					
					if (comment != null) {
						student.set(action.getKey(), comment.getText());
						student.set(new StringBuilder(assignmentId).append(StudentModel.COMMENTED_FLAG).toString(), Boolean.TRUE);
					}
					
					entity = (X)student;
				} else if (action.getKey().endsWith(StudentModel.EXCUSE_FLAG)) {
					entity = (X)excuseNumericItem(action.getGradebookUid(), student, action.getKey(), (Boolean)action.getValue(), (Boolean)action.getStartValue());
				} else {
					switch (action.getClassType()) {
					case DOUBLE:
						entity = (X)scoreNumericItem(action.getGradebookUid(), student, action.getKey(), (Double)action.getValue(), (Double)action.getStartValue());
						break;
					case STRING:
						entity = (X)scoreTextItem(action.getGradebookUid(), student,  action.getKey(), (String)action.getValue(), (String)action.getStartValue());
						break;
					}
					break;
				}
			}
			
			if (entity == null)
				actionRecord.setStatus(ActionRecord.STATUS_FAILURE);
			else
				actionRecord.setStatus(ActionRecord.STATUS_SUCCESS);
			
			if (action.getModel() != null && action.getModel() instanceof EntityModel) {
				EntityModel model = (EntityModel)action.getModel();
				actionRecord.setEntityId(model.getIdentifier());
				//actionRecord.setEntityName(model.getDisplayName());
			}
			
			gbService.storeActionRecord(actionRecord);
		
		} catch (InvalidInputException ie) {
			throw ie;
		} catch (Throwable t) {
			log.warn("FatalException: ", t);
			throw new FatalException(t.getMessage(), t);
		}
		
		return entity;
	}
	
	@SuppressWarnings("unchecked")
	public <X extends EntityModel> List<X> updateEntityList(UserEntityUpdateAction<X> action) throws InvalidInputException, FatalException {
		
		List<X> entityList = null;
		
		try {
		
			if (action.getPrerequisiteAction() != null) {
				BaseModel result = updateEntity((UserEntityUpdateAction)action.getPrerequisiteAction());
				
				// Break out of recursion if we get a null back
				if (result == null) 
					return null;
			}
			
			ActionRecord actionRecord = new ActionRecord(action.getGradebookUid(), action.getGradebookId(), action.getEntityType().name(), action.getActionType().name());
				
			if (action.getEntityId() != null)
				actionRecord.setEntityId(action.getEntityId());
			if (action.getEntityName() != null)
				actionRecord.setEntityName(action.getEntityName());
			actionRecord.setField(action.getKey());
			if (action.getValue() != null)
				actionRecord.setValue(String.valueOf(action.getValue()));
			if (action.getStartValue() != null)
				actionRecord.setStartValue(String.valueOf(action.getStartValue()));
			if (action.getDatePerformed() != null)
				actionRecord.setDatePerformed(action.getDatePerformed());
			
			switch (action.getEntityType()) {
			
			case GRADE_SCALE:
				GradeScaleRecordModel gradeScaleModel = (GradeScaleRecordModel)action.getModel();
				
				if (gradeScaleModel != null) {
					actionRecord.setEntityId(gradeScaleModel.getLetterGrade());
					actionRecord.setParentId(gradeScaleModel.getIdentifier());
				}
				
				GradeScaleRecordModel.Key gradeScaleRecordKey = GradeScaleRecordModel.Key.valueOf(action.getKey());
				entityList = updateGradeScaleField(action.getGradebookUid(), gradeScaleRecordKey, action.getValue(), gradeScaleModel.getLetterGrade());
				break;
			}
			
			if (entityList == null)
				actionRecord.setStatus(ActionRecord.STATUS_FAILURE);
			else
				actionRecord.setStatus(ActionRecord.STATUS_SUCCESS);
			
			gbService.storeActionRecord(actionRecord);
		
		} catch (Throwable t) {
			log.warn("FatalException: ", t);
			throw new FatalException(t.getMessage(), t);
		}
		
		return entityList;
	}
	
	
	protected CommentModel getComment(Long assignmentId, String studentUid) {
		Assignment assignment = gbService.getAssignment(assignmentId);
		Gradebook gradebook = assignment.getGradebook();
		List<Comment> comments = gbService.getStudentAssignmentComments(studentUid, gradebook.getId());
		Comment comment = null;
		
		// TODO: Make sure that there is only one comment per assignment
		if (comments != null && !comments.isEmpty()) {
			for (Comment c : comments) {
				if (c.getGradableObject().getId().equals(assignmentId)) {
					comment = c;
					break;
				}
			}
		}
		
		return createOrUpdateCommentModel(null, comment);
	}
	
	protected CommentModel createOrUpdateComment(Long assignmentId, String studentUid, String text) {
		Assignment assignment = gbService.getAssignment(assignmentId);
		Gradebook gradebook = assignment.getGradebook();
		
		List<Comment> comments = gbService.getStudentAssignmentComments(studentUid, gradebook.getId());
		Comment comment = null;
		
		// TODO: Make sure that there is only one comment per assignment
		if (comments != null && !comments.isEmpty()) {
			for (Comment c : comments) {
				if (c.getGradableObject().getId().equals(assignment.getId())) {
					comment = c;
					break;
				}
			}
		}
		
		if (comment == null) 
			comment = new Comment(studentUid, text, assignment);
		else
			comment.setCommentText(text);
		
		List<Comment> updatedComments = new ArrayList<Comment>();
		updatedComments.add(comment);
		
		gbService.updateComments(updatedComments);
		
		return createOrUpdateCommentModel(null, comment);
	}
	
	protected SpreadsheetModel createOrUpdateSpreadsheet(String gradebookUid, SpreadsheetModel spreadsheetModel) {
		
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		Map<String, Assignment> idToAssignmentMap = new HashMap<String, Assignment>();
		for (ItemModel item : spreadsheetModel.getHeaders()) {
			String id = item.getIdentifier();
			if (id != null) { 
				if (id.startsWith("NEW:")) {
					Long categoryId = null;
					String name = item.getName();
					Double weight = null;
					Double points = item.getPoints();
					Date dueDate = null;
					AssignmentModel model = addAssignment(gradebookUid, gradebook.getId(), categoryId, name, weight, points, dueDate);
					Assignment assignment = gbService.getAssignment(Long.valueOf(model.getIdentifier()));
					idToAssignmentMap.put(id, assignment);
					item.setIdentifier(model.getIdentifier());
				} else {
					Assignment assignment = gbService.getAssignment(Long.valueOf(id));
					idToAssignmentMap.put(id, assignment);
				}
			}
		}
		
		
		Long gradebookId = gradebook.getId();
		
		Map<String, UserRecord> userRecordMap = findStudentRecords(gradebookUid, gradebookId, null);
	    List<String> studentUids = new ArrayList<String>(userRecordMap.keySet());
		
	   	//Map<String, Map<Long, AssignmentGradeRecord>>  allGradeRecordsMap = new HashMap<String, Map<Long, AssignmentGradeRecord>>();
    	List<AssignmentGradeRecord> allGradeRecords = gbService.getAllAssignmentGradeRecords(gradebookId, studentUids);
		
    	if (allGradeRecords != null) {
	    	for (AssignmentGradeRecord gradeRecord : allGradeRecords) {
				gradeRecord.setUserAbleToView(true);
				String studentUid = gradeRecord.getStudentId();
				UserRecord userRecord = userRecordMap.get(studentUid);
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
		Collection<Assignment> assignments = idToAssignmentMap.values();
		if (assignments != null) {
			for (StudentModel student : spreadsheetModel.getRows()) {
				UserRecord userRecord = userRecordMap.get(student.getIdentifier());
				
				StringBuilder builder = new StringBuilder();
				
				builder.append("Grading ");
				
				if (student.getDisplayName() == null)
					builder.append(student.getStudentDisplayId()).append(": ");
				else
					builder.append(student.getDisplayName()).append(": ");
				
				
				if (userRecord == null) {
					builder.append("User not found!");
					results.add(builder.toString());
					continue;
				}
					
				
				Map<Long, AssignmentGradeRecord> gradeRecordMap = userRecord.getGradeRecordMap();
				
				for (Assignment assignment : assignments) {
					builder.append(assignment.getName()).append(" (");
					Object v = student.get(String.valueOf(assignment.getId()));
					
					Double value = null;
					if (v != null && v instanceof String) {
						String strValue = (String)v;
						if (strValue.trim().length() > 0)
							value = Double.valueOf(Double.parseDouble((String)v));
						
					} else
						value = (Double)v;
					
					//if (value != null) {
						AssignmentGradeRecord assignmentGradeRecord = gradeRecordMap.get(assignment.getId()); //gbService.getAssignmentGradeRecordForAssignmentForStudent(assignment, student.getIdentifier());
						Double oldValue = null;
						
						switch (gradebook.getGrade_type()) {
						case GradebookService.GRADE_TYPE_POINTS:
							oldValue = assignmentGradeRecord.getPointsEarned();
							break;
						case GradebookService.GRADE_TYPE_PERCENTAGE:
							oldValue = assignmentGradeRecord.getPercentEarned();
							break;
						}
						
						try {
							//scoreNumericItem(gradebookUid, student, assignmentId, value, (Double)null);
							scoreItem(gradebook, assignment, assignmentGradeRecord, student.getIdentifier(), value);
							//log.info("Scored item " + assignment.getName() + " for " + student.getIdentifier() + " as " + value);
							
							if (oldValue != null)
								builder.append(oldValue);
							
							builder.append("->").append(value).append(") ");
							
							//results.add("Successfully scored " + assignment.getName() + " for " + student.getIdentifier() + " to " + value);
						} catch (InvalidInputException e) {
							String failedProperty = new StringBuilder().append(assignment.getId()).append(StudentModel.FAILED_FLAG).toString();
							student.set(failedProperty, e.getMessage());
							log.warn("Failed to score numeric item for " + student.getIdentifier() + " and item " + assignment.getId() + " to " + value);
							//results.add("Failed to score " + assignment.getName() + " for " + student.getIdentifier() + " to " + value + ": " + e.getMessage());
							
							if (oldValue != null)
								builder.append(oldValue);
							
							builder.append("->Invalid) ");
						} catch (Exception e) {
							//results.add("Failed to score " + assignment.getName() + " for " + student.getIdentifier() + " to " + value + ": " + e.getMessage());
							
							if (oldValue != null)
								builder.append(oldValue);
							
							builder.append("->Failed) ");
						}
					//}
				}
				
				results.add(builder.toString());
			}
		}
		spreadsheetModel.setResults(results);
		
		return spreadsheetModel;
	}
	
	@SuppressWarnings("unchecked")
	protected <X extends BaseModel> PagingLoadResult<X> getActionHistory(
			String gradebookUid, PagingLoadConfig config) {
		
		Integer size = gbService.getActionRecordSize(gradebookUid);
		List<ActionRecord> actionRecords = gbService.getActionRecords(gradebookUid, config.getOffset(), config.getLimit());
		List<X> models = new ArrayList<X>();
		
		for (ActionRecord actionRecord : actionRecords) {
			UserEntityAction actionModel = null;
			
			UserEntityAction.ActionType actionType = UserEntityAction.ActionType.valueOf(actionRecord.getActionType());
			switch (actionType) {
			case CREATE:
				actionModel = new UserEntityCreateAction();
				break;
			case GRADED:
				actionModel = new UserEntityGradeAction();
				break;
			case UPDATE:
				actionModel = new UserEntityUpdateAction();
				break;
			}
			
			actionModel.setIdentifier(String.valueOf(actionRecord.getId()));
			actionModel.setGradebookUid(actionRecord.getGradebookUid());
			actionModel.setGradebookId(actionRecord.getGradebookId());
			actionModel.setEntityType(UserEntityAction.EntityType.valueOf(actionRecord.getEntityType()));
			if (actionRecord.getEntityId() != null)
				actionModel.setEntityId(actionRecord.getEntityId());
			if (actionRecord.getEntityName() != null)
				actionModel.setEntityName(actionRecord.getEntityName());
			if (actionRecord.getParentId() != null)
				actionModel.setParentId(Long.valueOf(actionRecord.getParentId()));
			actionModel.setStudentUid(actionRecord.getStudentUid());
			actionModel.setKey(actionRecord.getField());
			actionModel.setValue(actionRecord.getValue());
			actionModel.setStartValue(actionRecord.getStartValue());
			
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
			
			models.add((X)actionModel);
		}
		
		return new BasePagingLoadResult<X>(models, config.getOffset(), size.intValue());
	}
	
	protected ApplicationModel getApplicationModel() {
		ApplicationModel model = new ApplicationModel();
		model.setPlacementId(getPlacementId());
		model.setGradebookModels(getGradebookModels());
		
		return model;
	}
	
	@SuppressWarnings("unchecked")
	protected <X extends BaseModel> PagingLoadResult<X> getAssignmentRecords(
			String gradebookUid, Long gradebookId, String studentUid,
			Boolean isStudentView, PagingLoadConfig config) {
		
		if (studentUid == null)
			return new BasePagingLoadResult<X>(new ArrayList<X>(), config.getOffset(), 0);
		
		Collection<String> studentUids = new LinkedList<String>();
		studentUids.add(studentUid);
		List<X> models = new ArrayList<X>();
		
		List<Assignment> allAssignments = gbService.getAssignments(gradebookId);
		
		Collection<GradableObject> gradableObjects = new ArrayList<GradableObject>();
		gradableObjects.addAll(allAssignments);
		
		Map<GradableObject, List<GradingEvent>> map = gbService.getGradingEventsForStudent(studentUid, gradableObjects);

		boolean isStudent = isStudentView == null ? false : isStudentView.booleanValue();
		

		// FIXME: We don't want to get everybody's grade records here -- need a new method on the manager
		List<AssignmentGradeRecord> allGradeRecords = gbService.getAllAssignmentGradeRecords(gradebookId, studentUids);
		
		Map<Long, AssignmentGradeRecord> studentGradeMap = new HashMap<Long, AssignmentGradeRecord>();
    	if (allGradeRecords != null) {
	    	for (AssignmentGradeRecord gradeRecord : allGradeRecords) {
				gradeRecord.setUserAbleToView(true);
				if (studentUid.equals(gradeRecord.getStudentId())) {
					GradableObject go = gradeRecord.getGradableObject();
					studentGradeMap.put(go.getId(), gradeRecord);
				}
			}
		}
		
    	// We have to calculate the student's grade in order to ensure that the drop lowest/excuse/overallWeight data is populated
    	List<Category> categoriesWithAssignments = getCategoriesWithAssignments(gradebookId, allAssignments);
    	gradeCalculations.getCourseGrade(categoriesWithAssignments, studentGradeMap);
    	
		Collections.sort(allAssignments, new Comparator<Assignment>() {

			public int compare(Assignment a1, Assignment a2) {
				
				if (a1 == null || a2 == null)
					return 0;
				
				if (a1.getName() == null || a2.getName() == null)
					return 0;
				
				Category c1 = a1.getCategory();
				Category c2 = a2.getCategory();
				
				if (c1 == null || c2 == null)
					return 0;
				
				if (c1.getName() == null || c2.getName() == null)
					return 0;
				
				String s1 = c1.getName() + ":" + a1.getName();
				String s2 = c2.getName() + ":" + a2.getName();
				
				return s1.compareTo(s2);
			}
			
		});
		
		int fullListSize = 0;
		
		if (allAssignments != null && !allAssignments.isEmpty()) {
			//fullListSize = allAssignments.size();
			
			int startRow = config.getOffset();
			int lastRow = startRow + config.getLimit();
			
			if (lastRow > allAssignments.size()) {
				lastRow = allAssignments.size();
			}
			
			for (int row = startRow;row < lastRow;row++) {
				Assignment a = allAssignments.get(row);
				if (!a.isRemoved()) {
					boolean isUserAbleToGrade = isUserAbleToGradeAll(gradebookUid) || authz.isUserAbleToGradeItemForStudent(gradebookUid, a.getId(), studentUid);
					boolean isUserAbleToView = isUserAbleToGrade || authz.isUserAbleToViewItemForStudent(gradebookUid, a.getId(), studentUid);
				
					Category category = a.getCategory();
					if (!category.isRemoved()) {
						AssignmentGradeRecord agr = studentGradeMap.get(a.getId());	//gradebookManager.getAssignmentGradeRecordForAssignmentForStudent(a, studentUid);
						//agr.setGradableObject(a);
						//agr.setStudentId(studentUid);
						List<GradingEvent> events = map.get(a);
						boolean hasGradingEvents = events != null && events.size() > 0;
						
						boolean showThisItem = false;
						// If we're in Student View then we want to reduce visibility to released items the user is able to view
						if (isStudent) {
							showThisItem = a.isReleased();
						} else {
							showThisItem = isUserAbleToGrade || (isUserAbleToView && a.isReleased());
						}
						
						if (showThisItem) {
							models.add((X)createOrUpdateAssignmentRecordModel(gradebookId, null, agr, category, a, hasGradingEvents)); 
							fullListSize++;
						}
					}
				}
			}
		}

		return new BasePagingLoadResult<X>(models, config.getOffset(), fullListSize);
	}
	
	@SuppressWarnings("unchecked")
	protected GradeRecordModel getSingleAssignmentForStudent(
			String gradebookUid, Long gradebookId, String studentUid,
			Long assignmentId, Boolean isStudentView) {
		// For now we'll just fake it... 
		// FIXME - make this real
		Assignment a = gbService.getAssignment(assignmentId);
		AssignmentGradeRecord agr = gbService.getAssignmentGradeRecordForAssignmentForStudent(a, studentUid);
		Category c = a.getCategory();
		agr.setGradableObject(a);
		
		//Collection<GradableObject> gradableObjects = new LinkedList<GradableObject>();
		//gradableObjects.add(a);
		
		//Map<GradableObject, List<GradingEvent>> map = gbService.getGradingEventsForStudent(studentUid, gradableObjects);
		//List<GradingEvent> events = map.get(a);
		//boolean hasGradingEvents = events != null && events.size() > 0;  
		boolean hasGradingEvents = gbService.isStudentGraded(studentUid, a.getId());
		
		GradeRecordModel r = createOrUpdateAssignmentRecordModel(gradebookId, null, agr, c, a, hasGradingEvents);
			
		return r; 
	}

	@SuppressWarnings("unchecked")
	protected <X extends BaseModel> List<X> getCategories(String gradebookUid,
			boolean includeDeleted) {
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		
		List<X> models = new ArrayList<X>();
		List<Category> categories = gbService.getCategories(gradebook.getId());
		
		if (categories != null) {
			for (Category category : categories) {
				if (includeDeleted || ! category.isRemoved())
					models.add((X)createCategoryModel(gradebook, category));
			}
			
		}
		
		return models;
	}
	
	@SuppressWarnings("unchecked")
	protected <X extends BaseModel> PagingLoadResult<X> getCategories(String gradebookUid,
			Long gradebookId, boolean includeDeleted, PagingLoadConfig config) {
		List<X> models = new ArrayList<X>();
		List<Category> categories = gbService.getCategories(gradebookId);
		
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		
		int startRow = config.getOffset();
		int lastRow = startRow + config.getLimit();
		
		int numberOfRows = 0;
		
		if (categories != null) {
			numberOfRows = categories.size();
			if (lastRow > numberOfRows) 
				lastRow = numberOfRows;
			
			int i=0;
			for (int row = startRow;row < lastRow;row++) {
				Category category = categories.get(row);
				if (includeDeleted || ! category.isRemoved())
					models.add((X)createCategoryModel(gradebook, category));
			}
			
		}
		
		return new BasePagingLoadResult<X>(models, config.getOffset(), numberOfRows);
	}
	
	private <X extends BaseModel> List<X> getColumns() {
		List<X> columns = new LinkedList<X>();
				
		columns.add((X)new ColumnModel("Id", StudentModel.Key.DISPLAY_ID, 80, true));
		columns.add((X)new ColumnModel("Full Name", StudentModel.Key.DISPLAY_NAME, 180, false));
		columns.add((X)new ColumnModel("Sort Name", StudentModel.Key.SORT_NAME, 180, true));
		columns.add((X)new ColumnModel("Email", StudentModel.Key.EMAIL, 230, true));
		columns.add((X)new ColumnModel("Section", StudentModel.Key.SECTION, 120, true));
		columns.add((X)new ColumnModel("Course Grade", StudentModel.Key.COURSE_GRADE, 120, true));
		ColumnModel gradeOverrideColumn = new ColumnModel("Grade Override", StudentModel.Key.GRADE_OVERRIDE, 120, true);
		gradeOverrideColumn.setEditable(true);
		columns.add((X)gradeOverrideColumn);

		/*
		if (categoriesWithAssignments != null) {
			for (Category category : categoriesWithAssignments) {
				Gradebook gradebook = category.getGradebook();
				List<Assignment> assignments = category.getAssignmentList();
				if (assignments != null) {
					for (Assignment assignment : assignments) {
						boolean isRemoved = isAssignmentRemoved(gradebook, category, assignment);
						
						if (category != null && ! isRemoved) {
							boolean isUnweighted = isAssignmentUnweighted(gradebook, category, assignment);
							boolean isExtraCredit = isAssignmentExtraCredit(gradebook, category, assignment);
							
							
							String name = assignment.getName();
							
							switch (gradebook.getGrade_type()) {
							case GradebookService.GRADE_TYPE_POINTS:
								String points = DecimalFormat.getInstance().format(assignment.getPointsPossible());
								name = new StringBuilder(assignment.getName())
											.append(" (").append(points)
											.append(")").toString();
								break;
							case GradebookService.GRADE_TYPE_PERCENTAGE:
								name = new StringBuilder(assignment.getName())
											.append(" (%)").toString();
								break;
							} 
							
							ColumnModel config = new ColumnModel(assignment.getId(), name, StudentModel.Key.ASSIGNMENT, 80);
							config.setMaxPoints(assignment.getPointsPossible() == null ? 0.0 : assignment.getPointsPossible().doubleValue());
							config.setCategoryName(category.getName());
							config.setCategoryId(category.getId());
							config.setUnweighted(Boolean.valueOf(isUnweighted));
							config.setEditable(! assignment.isExternallyMaintained());
							config.setExtraCredit(Boolean.valueOf(isExtraCredit));
							columns.add((X)config);
						}
					}
				}
			}
		}
		
		Collections.sort(columns, new Comparator<X>() {

			public int compare(X o1, X o2) {
				if (((ColumnModel)o1).getCategoryId() == null || ((ColumnModel)o1).getCategoryId() == null)
					return 0;
				
				return ((ColumnModel)o1).getCategoryId().compareTo(((ColumnModel)o2).getCategoryId());
			}
			
		});
		*/
		return columns;
	}	
	
	protected <X extends BaseModel> List<X> getGradeEvents(String studentId, Long assignmentId) {
		
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
				models.add((X)createOrUpdateGradeEventModel(null, event));
			}
		}
		
		return models;
	}
	
	
	protected <X extends BaseModel> List<X> getAssignments(String gradebookUid, Long gradebookId, 
			boolean includeDeleted) {
		
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		List<X> models = new ArrayList<X>();
		List<Category> categories = getCategoriesWithAssignments(gradebookId);
		
		if (categories != null && !categories.isEmpty()) {
			for (Category category : categories) {
				List<Assignment> assignments = (List<Assignment>)category.getAssignmentList();
				if (assignments != null && !assignments.isEmpty()) {
					for (Assignment assignment : assignments) {
						boolean isRemoved = isAssignmentRemoved(gradebook, category, assignment);

						if (includeDeleted || ! isRemoved) {
							models.add((X)createAssignmentModel(category, assignment));
						}
					}
				}
			}
		}
		
		return models;
	}
	
	
	protected <X extends BaseModel> PagingLoadResult<X> getAssignments(String gradebookUid, Long gradebookId, 
			boolean includeDeleted, PagingLoadConfig config) {
		
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		List<X> models = new ArrayList<X>();
		List<Category> categories = getCategoriesWithAssignments(gradebookId);
		
		int row = 0;
		int firstRow = config.getOffset();
		int lastRow = config.getOffset() + config.getLimit();
		if (categories != null && !categories.isEmpty()) {
			for (Category category : categories) {
				List<Assignment> assignments = (List<Assignment>)category.getAssignmentList();
				if (assignments != null && !assignments.isEmpty()) {
					for (Assignment assignment : assignments) {
						boolean isRemoved = isAssignmentRemoved(gradebook, category, assignment);

						if (includeDeleted || ! isRemoved) {
							if (row >= firstRow && row < lastRow) 
								models.add((X)createAssignmentModel(category, assignment));
						}
						row++;
					}
				}
			}
		}
		
		return new BasePagingLoadResult<X>(models, config.getOffset(), row);
	}
	
	protected Category findDefaultCategory(Long gradebookId) {
		List<Category> categories = gbService.getCategories(gradebookId);
	
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
			defaultCategoryId = gbService.createCategory(gradebookId, "Default", Double.valueOf(1d), 0);
			isCategoryNew = true;
		} 

		// TODO: This is a just in case check -- we should probably throw an exception here instead, since it means we weren't able to 
		// TODO: create the category for some reason -- but that probably would throw an exception anyway, so...
		if (defaultCategoryId != null) {
			Category defaultCategory = gbService.getCategory(defaultCategoryId);
			return defaultCategory;
		}
		
		return null;
	}
	
	protected GradebookModel getGradebook(String uid) {
		Gradebook gradebook = gbService.getGradebook(uid);
		
		return createGradebookModel(gradebook);
	}
	
	protected List<GradebookModel> getGradebookModels() {
		String gradebookUid = getGradebookUid();
		List<GradebookModel> models = new LinkedList<GradebookModel>();
		
		if (gradebookUid != null) {
			Gradebook gradebook = null;
			try {
				// First thing, grab the default gradebook if one exists
				gradebook = gbService.getGradebook(gradebookUid);
			} catch (GradebookNotFoundException gnfe) {	
				// If it doesn't exist, then create it
				if (frameworkService != null) {
					frameworkService.addGradebook(gradebookUid, "My Default Gradebook");
					gradebook = gbService.getGradebook(gradebookUid);
				}
			}
			
			// If we have a gradebook already, then we have to ensure that it's set up correctly for the new tool
			if (gradebook != null) {
				// We need to ensure that the category setting is correct
				/*if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY) {
					gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY);
					gradebookManager.updateGradebook(gradebook);
				}*/
				
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
	
	
	public String getPlacementId() {
		return toolManager.getCurrentPlacement().getId();
	}
	
	protected String getSiteContext() {
		return toolManager.getCurrentPlacement().getContext();
	}
	
	protected <X extends BaseModel> PagingLoadResult<X> getSections(String gradebookUid,
			Long gradebookId, PagingLoadConfig config) {
		
		List<CourseSection> viewableSections = getViewableSections(gradebookUid, gradebookId);
		
		List<X> sections = new LinkedList<X>();
		
		SectionModel allSections = new SectionModel();
		//allSections.setSectionId("all");
		allSections.setSectionName("All Viewable Sections");
		sections.add((X)allSections);
		
		if (viewableSections != null) {
			for (CourseSection courseSection : viewableSections) {
				SectionModel sectionModel = new SectionModel();
				sectionModel.setSectionId(courseSection.getUuid());
				sectionModel.setSectionName(courseSection.getTitle());
				sections.add((X)sectionModel);
			}
		}
		
		return new BasePagingLoadResult<X>(sections, config.getOffset(), viewableSections.size());
	}
	
	protected List<UserRecord> doSearchUsers(String searchString, List<String> studentUids, Map<String, UserRecord> userRecordMap) {
	
		// Make sure that our search criterion is case insensitive
		if (searchString != null)
			searchString = searchString.toUpperCase();
	
		List<UserRecord> userRecords = new ArrayList<UserRecord>();
		
		// To do a search, we have to get all the users . . . this is also desirable even if we're not searching, if we want to sort on these properties
		List<User> users = userService.getUsers(studentUids);
		
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
	
	private List<UserRecord> doSearchAndSortUserRecords(Gradebook gradebook, List<Category> categoriesWithAssignments, 
			List<String> studentUids, Map<String, UserRecord> userRecordMap, PagingLoadConfig config) {
		
		String searchString = null;
		if (config instanceof MultiGradeLoadConfig) {
			searchString = ((MultiGradeLoadConfig)config).getSearchString();
		}
		
		List<UserRecord> userRecords = null;
		StudentModel.Key sortColumnKey = null;
		
		String columnId = null;
		
		// This is slightly painful, but since it's a String that gets passed up, we have to iterate
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
			case SORT_NAME:
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
			case SORT_NAME:
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
			case COURSE_GRADE:
				// In this case we need to ensure that we've calculated everybody's course grade
				for (UserRecord record : userRecords) {
					record.setDisplayGrade(getDisplayGrade(gradebook, record.getCourseGradeRecord(), categoriesWithAssignments, record.getGradeRecordMap()));
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
			// This is pretty silly on one level, since it means that we don't take advantage of the database to do this, but it's equivalent to what
			// section awareness is doing behind the scenes and it gives us more control over the process
			if (searchString != null)
				userRecords = doSearchUsers(searchString, studentUids, userRecordMap);
			else 
				userRecords = new ArrayList<UserRecord>(userRecordMap.values());
			
			// This seems a little stupid, but the fact of the matter is that we get an unordered list
			// back from the Map.keySet call, so we do want to ensure that we get the same order each time
			// even when the user has not chosen to sort 
			Collections.sort(userRecords, DEFAULT_ID_COMPARATOR);
		}
		
		return userRecords;
	}
	
	
	protected <X extends BaseModel> PagingLoadResult<X> getStudentRows(String gradebookUid, Long gradebookId, PagingLoadConfig config) {
		
		String sectionUuid = null;

		if (config != null && config instanceof MultiGradeLoadConfig) {
			sectionUuid = ((MultiGradeLoadConfig)config).getSectionUuid();
		}
		
	    Gradebook gradebook = gbService.getGradebook(gradebookId);
	    
	   
	    List<Category> categories = getCategoriesWithAssignments(gradebookId);
	    
	    ItemModel gradebookItemModel = getItemModel(gradebook, categories);
	    
	    Map<String, UserRecord> userRecordMap = findStudentRecords(gradebookUid, gradebookId, sectionUuid);
	    List<String> studentUids = new ArrayList<String>(userRecordMap.keySet());
		
	   	//Map<String, Map<Long, AssignmentGradeRecord>>  allGradeRecordsMap = new HashMap<String, Map<Long, AssignmentGradeRecord>>();
    	List<AssignmentGradeRecord> allGradeRecords = gbService.getAllAssignmentGradeRecords(gradebookId, studentUids);
		
    	if (allGradeRecords != null) {
	    	for (AssignmentGradeRecord gradeRecord : allGradeRecords) {
				gradeRecord.setUserAbleToView(true);
				String studentUid = gradeRecord.getStudentId();
				UserRecord userRecord = userRecordMap.get(studentUid);
				Map<Long, AssignmentGradeRecord> studentMap = userRecord.getGradeRecordMap();
				if (studentMap == null) {
					studentMap = new HashMap<Long, AssignmentGradeRecord>();
				}
				GradableObject go = gradeRecord.getGradableObject();
				studentMap.put(go.getId(), gradeRecord);
					
				userRecord.setGradeRecordMap(studentMap);
			}
		}
    	    	
		List<CourseGradeRecord> courseGradeRecords = gbService.getAllCourseGradeRecords(gradebook);
		
		if (courseGradeRecords != null) {
			for (CourseGradeRecord courseGradeRecord : courseGradeRecords) {
				String studentUid = courseGradeRecord.getStudentId();
				UserRecord userRecord = userRecordMap.get(studentUid);
				if (userRecord != null)
					userRecord.setCourseGradeRecord(courseGradeRecord);
				else
					log.warn("Looking up user record for " + studentUid + " failed." );
			}
		}
		
		List<Comment> comments = gbService.getComments(gradebookId);
		
		if (comments != null) {
			for (Comment comment : comments) {
				String studentUid = comment.getStudentId();
				UserRecord userRecord = userRecordMap.get(studentUid);
				if (userRecord != null) {
					Map<Long, Comment> commentMap = userRecord.getCommentMap();
					if (commentMap == null)
						commentMap = new HashMap<Long, Comment>();
					commentMap.put(comment.getGradableObject().getId(), comment);
					userRecord.setCommentMap(commentMap);
				}
			}
		}
		
				
		List<UserRecord> userRecords = doSearchAndSortUserRecords(gradebook, categories, studentUids, userRecordMap, config);

		int startRow = 0;
		int lastRow = userRecords.size();
		
		if (config != null) {
			startRow = config.getOffset();
			lastRow = startRow + config.getLimit();
		}
		
		if (lastRow > userRecords.size()) {
			lastRow = userRecords.size();
		}
			
		List<ColumnModel> columns = getColumns();
		
		List<X> rows = new ArrayList<X>();
		
		
		// We only want to populate the rowData and rowValues for the requested rows
		for (int row = startRow;row < lastRow;row++) {
			// Everything is indexed by the user, since it's by user id that the rows are distinguished
			UserRecord userRecord = userRecords.get(row);
			
			// Populate the user record on the fly if necessary
			if (!userRecord.isPopulated()) {
				User user = null;
				try {
					user = userService.getUser(userRecord.getUserUid());
					userRecord.setUserEid(user.getEid());
					userRecord.setDisplayId(user.getDisplayId());
					userRecord.setDisplayName(user.getDisplayName());
					userRecord.setSortName(user.getSortName());
					userRecord.setEmail(user.getEmail());
				} catch (UserNotDefinedException e) {
					log.error("No sakai user defined for this member '" + userRecord.getUserUid() + "'", e);
				}
			}
			
			rows.add((X)buildStudentRow(gradebook, userRecord, columns, gradebookItemModel, categories));
		}
		
		return new BasePagingLoadResult<X>(rows, startRow, userRecords.size());
	}
	
	private ItemModel getItemModel(Gradebook gradebook, List<Category> categories) {
		
		ItemModel gradebookItemModel = createItemModel(gradebook);
		
		/*ItemModel studentInformationModel = new ItemModel();
		studentInformationModel.setIdentifier(StudentModel.Group.STUDENT_INFORMATION.name());
		studentInformationModel.setName(StudentModel.Group.STUDENT_INFORMATION.getDisplayName());
		studentInformationModel.setSource("Static");
		studentInformationModel.setItemType(Type.CATEGORY.getName());
		gradebookItemModel.add(studentInformationModel);
		
		ItemModel displayIdModel = new ItemModel();
		displayIdModel.setIdentifier(StudentModel.Key.DISPLAY_ID.name());
		displayIdModel.setName(StudentModel.Key.DISPLAY_ID.getDisplayName());
		displayIdModel.setStudentModelKey(StudentModel.Key.DISPLAY_ID.name());
		displayIdModel.setSource("Static");
		displayIdModel.setItemType(Type.ITEM.getName());
		studentInformationModel.add(displayIdModel);
		
		ItemModel displayNameModel = new ItemModel();
		displayNameModel.setIdentifier(StudentModel.Key.DISPLAY_NAME.name());
		displayNameModel.setName(StudentModel.Key.DISPLAY_NAME.getDisplayName());
		displayNameModel.setStudentModelKey(StudentModel.Key.DISPLAY_NAME.name());
		displayNameModel.setSource("Static");
		displayNameModel.setItemType(Type.ITEM.getName());
		studentInformationModel.add(displayNameModel);*/
		
		boolean isNotInCategoryMode = gradebook.getCategory_type() == GradebookService.CATEGORY_TYPE_NO_CATEGORY;
		
		if (categories != null) {
			for(Category category : categories) {
			
				if (isNotInCategoryMode || !category.isRemoved()) {
					List<Assignment> assignments = category.getAssignmentList();
					ItemModel categoryItemModel = createItemModel(gradebook, category, assignments);
	
					if (!isNotInCategoryMode) {
						categoryItemModel.setParent(gradebookItemModel);
						gradebookItemModel.add(categoryItemModel);
					} 
					
					BigDecimal percentGrade = BigDecimal.valueOf(categoryItemModel.getPercentCourseGrade().doubleValue());
					BigDecimal percentCategory = BigDecimal.valueOf(categoryItemModel.getPercentCategory().doubleValue());
					
					if (assignments != null) {
						for (Assignment assignment : assignments) {
							if (assignment.isRemoved()) 
								continue;
							
							BigDecimal assignmentWeight = BigDecimal.valueOf(assignment.getAssignmentWeighting().doubleValue());
							BigDecimal courseGradePercent = calculateItemGradePercent(percentGrade, percentCategory, assignmentWeight);
							
							ItemModel assignmentItemModel = createItemModel(category, assignment, courseGradePercent);
							//assignmentItemModel.setStudentModelKey(Key.ASSIGNMENT.name());
							
							if (isNotInCategoryMode) {
								assignmentItemModel.setParent(gradebookItemModel);
								gradebookItemModel.add(assignmentItemModel);
							} else {
								assignmentItemModel.setParent(categoryItemModel);
								categoryItemModel.add(assignmentItemModel);
							}
						}
					}
				}
			}
		}
		
		return gradebookItemModel;
	}
	
	private List<Category> getCategoriesWithAssignments(Long gradebookId) {
	    List<Assignment> assignments = gbService.getAssignments(gradebookId);
	 
	    return getCategoriesWithAssignments(gradebookId, assignments);
	}
	
	private List<Category> getCategoriesWithAssignments(Long gradebookId, List<Assignment> assignments) {
		// The following chunk of logic seems inefficient, since there is a method called
	    // getCategoriesWithAssignments, but in fact that method results in N+1 distinct db calls,
	    // where N is the number of categories, whereas this is 2 calls.
		List<Category> categories = gbService.getCategories(gradebookId);
	     
	    Map<Long, List<Assignment>> categoryAssignmentMap = new HashMap<Long, List<Assignment>>();
	    
	    for (Assignment assignment : assignments) {
	    	// It may be that we could simply aggregate these Category children of the Assignment,
	    	// but that would of course mean that we would not include a Category with 0 assignments 
	    	// under it. That _may_ be safe, but for the moment, I'm using this slightly less efficient
	    	// method
	    	Category category = assignment.getCategory();
	    	
	    	List<Assignment> assignmentList = categoryAssignmentMap.get(category.getId());
	    	
	    	// Ensure that it's populated 
	    	if (assignmentList == null) 
	    		assignmentList = new ArrayList<Assignment>();
	    	
	    	assignmentList.add(assignment);
	    	categoryAssignmentMap.put(category.getId(), assignmentList);
	    }
	  
	    for (Category category : categories) {
	    	List<Assignment> assignmentList = categoryAssignmentMap.get(category.getId());
	    	category.setAssignmentList(assignmentList);
	    }
	    
	    return categories;
	}
	
	private boolean isAssignmentRemoved(Gradebook gradebook, Category category, Assignment assignment) {
		boolean isRemoved = assignment.isRemoved();
		
		if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY) 
			isRemoved = isRemoved || category.isRemoved();
		
		return isRemoved;
	}
	
	private boolean isAssignmentUnweighted(Gradebook gradebook, Category category, Assignment assignment) {
		boolean isUnweighted = assignment.isUnweighted() == null ? false : assignment.isUnweighted().booleanValue();
		
		if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY) {
			boolean isCategoryUnweighted = category.isUnweighted() != null && category.isUnweighted().booleanValue();
			isUnweighted = isUnweighted || isCategoryUnweighted;
		}
		
		return isUnweighted;
	}
	
	private boolean isAssignmentExtraCredit(Gradebook gradebook, Category category, Assignment assignment) {
		boolean isExtraCredit = assignment.isExtraCredit() != null && assignment.isExtraCredit().booleanValue();
		
		if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY) {
			boolean isCategoryExtraCredit = category.isExtraCredit() != null && category.isExtraCredit().booleanValue();
			isExtraCredit = isExtraCredit || isCategoryExtraCredit;
		}
			
		return isExtraCredit;
	}
	
	private StudentModel buildStudentRow(Gradebook gradebook, UserRecord userRecord, 
			List<ColumnModel> columns, ItemModel gradebookItemModel,
			List<Category> categoriesWithAssignments) {
		
		Map<Long, AssignmentGradeRecord> studentGradeMap = userRecord.getGradeRecordMap();
		
		// This is an intermediate map for data to be placed in the record
		Map<String, Object> cellMap = new HashMap<String, Object>();
		
		// This is how we track which column is which - by the user's uid
		cellMap.put(StudentModel.Key.UID.name(), userRecord.getUserUid());
		cellMap.put(StudentModel.Key.EID.name(), userRecord.getUserEid());
		cellMap.put(StudentModel.Key.EXPORT_CM_ID.name(), userRecord.getExportCourseManagemntId());
		cellMap.put(StudentModel.Key.EXPORT_USER_ID.name(), userRecord.getExportUserId());
		// Need this to show the grade override
		CourseGradeRecord courseGradeRecord = userRecord.getCourseGradeRecord(); //gradebookManager.getStudentCourseGradeRecord(gradebook, userRecord.getUserUid());
	
		String enteredGrade = null;
		String displayGrade = null;
	
		if (courseGradeRecord != null) 
			enteredGrade = courseGradeRecord.getEnteredGrade();
		
		if (userRecord.isCalculated())
			displayGrade = userRecord.getDisplayGrade();
		else
			displayGrade = getDisplayGrade(gradebook, courseGradeRecord, categoriesWithAssignments, studentGradeMap);
			
		if (columns != null) {
			for (ColumnModel column : columns) {
				StudentModel.Key key = StudentModel.Key.valueOf(column.getKey());
				switch(key) {
				case DISPLAY_ID:
					cellMap.put(StudentModel.Key.DISPLAY_ID.name(), userRecord.getDisplayId());
					break;
				case DISPLAY_NAME:
					// For the single view, maybe some redundancy, but not much
					String displayName = userRecord.getDisplayName();

					if (displayName == null)
						displayName = "[User name not found]";
					
					cellMap.put(StudentModel.Key.DISPLAY_NAME.name(), displayName);
					cellMap.put(StudentModel.Key.SORT_NAME.name(), userRecord.getSortName());
					cellMap.put(StudentModel.Key.EMAIL.name(), userRecord.getEmail());
					break;
				case SECTION:
					cellMap.put(StudentModel.Key.SECTION.name(), userRecord.getSectionTitle());
					break;
				case COURSE_GRADE:
					if (displayGrade != null)
						cellMap.put(StudentModel.Key.COURSE_GRADE.name(), displayGrade);
					break;
				case GRADE_OVERRIDE:
					cellMap.put(StudentModel.Key.GRADE_OVERRIDE.name(), enteredGrade);
					break;
				};
			}
		}
		
		if (gradebookItemModel != null) {
			
			for (ItemModel child : gradebookItemModel.getChildren()) {
				
				if (child.getItemType() == Type.CATEGORY) {
					
					for (ItemModel item : child.getChildren()) {
						cellMap = appendItemData(item, cellMap, userRecord, gradebook);
					}
					
				} else {
					cellMap = appendItemData(child, cellMap, userRecord, gradebook);
				}
			}
			
		}
	
		return new StudentModel(cellMap);
	}
	
	private String concat(String... vars) {
		StringBuilder builder = new StringBuilder();
		
		for (int i=0;i<vars.length;i++) {
			builder.append(vars[i]);
		}
		
		return builder.toString();
	}
	
	private Map<String, Object> appendItemData(ItemModel item, Map<String, Object> cellMap, 
			UserRecord userRecord, Gradebook gradebook) {
		AssignmentGradeRecord gradeRecord = null;
		
		String id = item.getIdentifier();
		Long assignmentId = Long.valueOf(id);
		
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
				
				boolean isGraded = gbService.isStudentGraded(userRecord.getUserUid(), assignmentId);
				
				if (isGraded)
					cellMap.put(concat(id, StudentModel.GRADED_FLAG), Boolean.TRUE);
				
				boolean isCommented = userRecord.getCommentMap() != null && userRecord.getCommentMap().get(assignmentId) != null;
				
				if (isCommented) {
					cellMap.put(concat(id, StudentModel.COMMENTED_FLAG), Boolean.TRUE);
					cellMap.put(concat(id, StudentModel.COMMENT_TEXT_FLAG), userRecord.getCommentMap().get(assignmentId).getCommentText());
				}
					
				switch (gradebook.getGrade_type()) {
				case GradebookService.GRADE_TYPE_POINTS:
					cellMap.put(id, gradeRecord.getPointsEarned());
					break;
				case GradebookService.GRADE_TYPE_PERCENTAGE:
					BigDecimal percentage = gradeCalculations.getPointsEarnedAsPercent((Assignment)gradeRecord.getGradableObject(), gradeRecord);
					Double percentageDouble = percentage == null ? null : Double.valueOf(percentage.doubleValue());
					cellMap.put(id, percentageDouble);
					break;
				case GradebookService.GRADE_TYPE_LETTER:
					cellMap.put(id, "No letter grades");
					break;
				default:
					cellMap.put(id, "Not implemented");
					break;
				}
			}
		}
		
		return cellMap;
	}
	

	public List<CategoryModel> recalculateEqualWeightingCategories(
			String gradebookUid, Long gradebookId, Boolean isEqualWeighting) {
		
		List<Category> categories = gbService.getCategories(gradebookId);
		
		int weightedCount = 0;
		if (categories != null) {
			for (Category category : categories) {
				boolean isWeighted = category.isUnweighted() == null ? true : ! category.isUnweighted().booleanValue();
				boolean isExtraCredit = category.isExtraCredit() == null ? false : category.isExtraCredit().booleanValue();
				if (isWeighted && !isExtraCredit && !category.isRemoved()) {
					weightedCount++;
				}
			}
		}
		
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		//gbService.updateGradebook(gradebook);
		
		List<CategoryModel> models = new ArrayList<CategoryModel>();
		
		// Only recalculate if the isEqualWeighting boolean is true
		if (isEqualWeighting != null && isEqualWeighting.booleanValue()) {
			Double newWeight = calculateEqualWeight(weightedCount);
			if (categories != null) {
				for (Category category : categories) {
					boolean isWeighted = category.isUnweighted() == null ? false : ! category.isUnweighted().booleanValue();
					boolean isExtraCredit = category.isExtraCredit() == null ? false : category.isExtraCredit().booleanValue();
					if (isWeighted && !isExtraCredit && !category.isRemoved()) {
						Category persistCategory = gbService.getCategory(category.getId());
						persistCategory.setWeight(newWeight);
						gbService.updateCategory(persistCategory);
						models.add(createCategoryModel(gradebook, persistCategory));
					}
				}
			}
		}
		
		return models;
	}

	protected List<Assignment> recalculateAssignmentWeights(Long categoryId, Boolean isEqualWeighting) {
		List<Assignment> updatedAssignments = new ArrayList<Assignment>();
		List<Assignment> assignments = gbService.getAssignmentsForCategory(categoryId);

		int weightedCount = 0;
		if (assignments != null) {
			for (Assignment assignment : assignments) {
				boolean isWeighted = assignment.isUnweighted() == null ? true : ! assignment.isUnweighted().booleanValue();
				boolean isExtraCredit = assignment.isExtraCredit() == null ? false : assignment.isExtraCredit().booleanValue();
				if (isWeighted && !isExtraCredit) {
					weightedCount++;
				}
			}
		}
		
		boolean doRecalculate = false;
		
		Category category = gbService.getCategory(categoryId);
		if (isEqualWeighting != null) {
			category.setEqualWeightAssignments(isEqualWeighting);
			gbService.updateCategory(category);
		} 
		
		doRecalculate = category.isEqualWeightAssignments() == null ? true : category.isEqualWeightAssignments().booleanValue();
		
		if (doRecalculate) {
			Double newWeight = calculateEqualWeight(weightedCount);
			if (assignments != null) {
				for (Assignment assignment : assignments) {
					boolean isWeighted = assignment.isUnweighted() == null ? true : ! assignment.isUnweighted().booleanValue();
					boolean isExtraCredit = assignment.isExtraCredit() == null ? false : assignment.isExtraCredit().booleanValue();
					if (isWeighted) {
						if (isExtraCredit)
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
	

	public List<AssignmentModel> recalculateEqualWeightingGradeItems(String gradebookUid, Long gradebookId, Long categoryId,
			Boolean isEqualWeighting) {
		List<Assignment> assignments = gbService.getAssignmentsForCategory(categoryId);
		
		int weightedCount = 0;
		if (assignments != null) {
			for (Assignment assignment : assignments) {
				boolean isWeighted = assignment.isUnweighted() == null ? true : ! assignment.isUnweighted().booleanValue();
				boolean isExtraCredit = assignment.isExtraCredit() == null ? false : assignment.isExtraCredit().booleanValue();
				if (isWeighted && !isExtraCredit) {
					weightedCount++;
				}
			}
		}
		
		boolean doRecalculate = false;
		
		Category category = gbService.getCategory(categoryId);
		if (isEqualWeighting != null) {
			category.setEqualWeightAssignments(isEqualWeighting);
			gbService.updateCategory(category);
		} 
		
		doRecalculate = category.isEqualWeightAssignments() == null ? true : category.isEqualWeightAssignments().booleanValue();
		
		List<AssignmentModel> models = new ArrayList<AssignmentModel>();
		if (doRecalculate) {
			Double newWeight = calculateEqualWeight(weightedCount);
			if (assignments != null) {
				for (Assignment assignment : assignments) {
					boolean isWeighted = assignment.isUnweighted() == null ? true : ! assignment.isUnweighted().booleanValue();
					boolean isExtraCredit = assignment.isExtraCredit() == null ? false : assignment.isExtraCredit().booleanValue();
					if (isWeighted && !isExtraCredit) {
						Assignment persistAssignment = gbService.getAssignment(assignment.getId());
						persistAssignment.setAssignmentWeighting(newWeight);
						gbService.updateAssignment(persistAssignment);
						models.add(createAssignmentModel(category, persistAssignment));
					}
				}
			}
		}
		
		return models;
	}
	
	public String requestCourseGrade(String gradebookUid, String studentId) {
		Gradebook gradebook = null;
		try {
			gradebook = gbService.getGradebook(gradebookUid);
		} catch (Exception e) {
			GWT.log("Can't find gradebookId", e);
		}
		
		if (gradebook != null) {
			
			Collection<Assignment> assignments = gbService.getAssignments(gradebook.getId());
			Map<Long, AssignmentGradeRecord> studentGradeMap = new HashMap<Long, AssignmentGradeRecord>();
			if (assignments != null) {
				for (Assignment assignment : assignments) {
					studentGradeMap.put(assignment.getId(), gbService.getAssignmentGradeRecordForAssignmentForStudent(assignment, studentId));
				}
			}
			List<Category> categoriesWithAssignments = getCategoriesWithAssignments(gradebook.getId());
			
			CourseGradeRecord courseGradeRecord = gbService.getStudentCourseGradeRecord(gradebook, studentId);
			return getDisplayGrade(gradebook, courseGradeRecord, categoriesWithAssignments, studentGradeMap);
		}
		return null;
	}
	
	
	public String requestCourseGrade(String gradebookUid, String studentId, List<Category> categoriesWithAssignments, Map<Long, AssignmentGradeRecord> studentGradeMap) {
		Gradebook gradebook = null;
		try {
			gradebook = gbService.getGradebook(gradebookUid);
		} catch (Exception e) {
			GWT.log("Can't find gradebookId", e);
		}
		
		if (gradebook != null) {
			CourseGradeRecord courseGradeRecord = gbService.getStudentCourseGradeRecord(gradebook, studentId);
			return getDisplayGrade(gradebook, courseGradeRecord, categoriesWithAssignments, studentGradeMap);
		}
		return null;
	}
	
	private String getDisplayGrade(Gradebook gradebook, CourseGradeRecord courseGradeRecord, 
			List<Category> categoriesWithAssignments, Map<Long, AssignmentGradeRecord> studentGradeMap) {
		BigDecimal autoCalculatedGrade = gradeCalculations.getCourseGrade(categoriesWithAssignments, studentGradeMap);
		Double calculatedGrade = autoCalculatedGrade == null ? null : Double.valueOf(autoCalculatedGrade.doubleValue());
		
		String enteredGrade = null;
		String displayGrade = null;
		String letterGrade = null;
	
		if (courseGradeRecord != null) 
			enteredGrade = courseGradeRecord.getEnteredGrade();
		
		if (enteredGrade == null && calculatedGrade != null) 
			letterGrade = gradebook.getSelectedGradeMapping().getGrade(calculatedGrade);
		else
			letterGrade = enteredGrade;
		
		String missingGradesMarker = "";
		
		for (Category category : categoriesWithAssignments) {
			if (category != null) {
				List<Assignment> assignments = category.getAssignmentList();
				if (assignments != null) {
					for (Assignment assignment : assignments) {
						// The student is missing one or more grades if 
						/// (a) there's no studentGradeMap
						/// (b) there's no AssignmentGradeRecord for this assignment
						/// (c) there's no points earned for this AssignmentGradeRecord
						if (studentGradeMap != null && studentGradeMap.get(assignment.getId()) != null) {
							
							AssignmentGradeRecord record = studentGradeMap.get(assignment.getId());
							
							boolean isExcused = record.isExcluded() != null && record.isExcluded().booleanValue();
							boolean isDropped = record.isDropped() != null && record.isDropped().booleanValue();
							if (record.getPointsEarned() == null && !isExcused && !isDropped) { 
								missingGradesMarker = "***";
							}
							
						} else {
							missingGradesMarker = "***";
						}
						
						
					}
				}
			}
		}
		
		if (letterGrade != null) {
			StringBuilder buffer = new StringBuilder(letterGrade);
			
			if (autoCalculatedGrade != null) {
				buffer.append(" (")
				.append(autoCalculatedGrade.setScale(2, RoundingMode.HALF_EVEN).toString())
				.append("%) ").append(missingGradesMarker);
			}
			
			displayGrade = buffer.toString();	
		}
		
		return displayGrade;
	}
	
	private StudentModel refreshLearnerData(Gradebook gradebook, StudentModel student, Assignment assignment) {
		List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
		Map<Long, AssignmentGradeRecord> studentGradeMap = new HashMap<Long, AssignmentGradeRecord>();
		if (assignments != null) {
			for (Assignment a : assignments) {
				AssignmentGradeRecord record = gbService.getAssignmentGradeRecordForAssignmentForStudent(a, student.getIdentifier());
				record.setGradableObject(a);
				studentGradeMap.put(a.getId(), record);
			}
		}
		List<Category> categoriesWithAssignments = getCategoriesWithAssignments(gradebook.getId(), assignments);
		
		CourseGradeRecord courseGradeRecord = gbService.getStudentCourseGradeRecord(gradebook, student.getIdentifier());
		String displayGrade = getDisplayGrade(gradebook, courseGradeRecord, categoriesWithAssignments, studentGradeMap);
		
		for (AssignmentGradeRecord record : studentGradeMap.values()) {
			Long aId = record.getGradableObject().getId();
			String dropProperty =  concat(String.valueOf(aId), StudentModel.DROP_FLAG);
			String excuseProperty = concat(String.valueOf(aId), StudentModel.EXCUSE_FLAG);
			boolean isDropped = record.isDropped() != null && record.isDropped().booleanValue();
			boolean isExcluded = record.isExcluded() != null && record.isExcluded().booleanValue();
			
			if (isDropped || isExcluded)
				student.set(dropProperty, Boolean.TRUE);
			else if (student.get(dropProperty) != null) 
				student.set(dropProperty, null);
			
			if (isExcluded) 
				student.set(excuseProperty, Boolean.TRUE);
		}
		
		String gradedProperty = assignment.getId() + StudentModel.GRADED_FLAG;
		if (gbService.isStudentGraded(student.getIdentifier(), assignment.getId())) 		
			student.set(gradedProperty, Boolean.TRUE);
		else
			student.set(gradedProperty, null);
		
		String commentedProperty = assignment.getId() + StudentModel.COMMENTED_FLAG;
		if (gbService.isStudentCommented(student.getIdentifier(), assignment.getId())) 		
			student.set(commentedProperty, Boolean.TRUE);
		else
			student.set(commentedProperty, null);
		
		student.set(StudentModel.Key.COURSE_GRADE.name(), displayGrade);
		
		return student;
	}
	
	public StudentModel excuseNumericItem(String gradebookUid, StudentModel student, String id, Boolean value, Boolean previousValue) throws InvalidInputException {
		
		int indexOf = id.indexOf(StudentModel.EXCUSE_FLAG);
		
		if (indexOf == -1)
			return null;
		
		String assignmentId = id.substring(0, indexOf);
		
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		Assignment assignment = gbService.getAssignment(Long.valueOf(assignmentId));
		AssignmentGradeRecord assignmentGradeRecord = gbService.getAssignmentGradeRecordForAssignmentForStudent(assignment, student.getIdentifier());
		
		assignmentGradeRecord.setExcluded(value);
		
		// Prepare record for update
		assignmentGradeRecord.setGradableObject(assignment);
		assignmentGradeRecord.setStudentId(student.getIdentifier());
		
		Collection<AssignmentGradeRecord> gradeRecords = new LinkedList<AssignmentGradeRecord>();
		gradeRecords.add(assignmentGradeRecord);
		gbService.updateAssignmentGradeRecords(assignment, gradeRecords, gradebook.getGrade_type());

		refreshLearnerData(gradebook, student, assignment);
		//student.set(id, value);
		
		return student;
	}
	
	public StudentModel scoreNumericItem(String gradebookUid, StudentModel student, String assignmentId, Double value, Double previousValue) throws InvalidInputException {
		
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		Assignment assignment = gbService.getAssignment(Long.valueOf(assignmentId));
		AssignmentGradeRecord assignmentGradeRecord = gbService.getAssignmentGradeRecordForAssignmentForStudent(assignment, student.getIdentifier());
		
		scoreItem(gradebook, assignment, assignmentGradeRecord, student.getIdentifier(), value);
		
		refreshLearnerData(gradebook, student, assignment);
		student.set(assignmentId, value);
		
		/*List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
		Map<Long, AssignmentGradeRecord> studentGradeMap = new HashMap<Long, AssignmentGradeRecord>();
		if (assignments != null) {
			for (Assignment a : assignments) {
				AssignmentGradeRecord record = gbService.getAssignmentGradeRecordForAssignmentForStudent(a, student.getIdentifier());
				record.setGradableObject(a);
				studentGradeMap.put(a.getId(), record);
			}
		}
		List<Category> categoriesWithAssignments = getCategoriesWithAssignments(gradebook.getId(), assignments);
		
		CourseGradeRecord courseGradeRecord = gbService.getStudentCourseGradeRecord(gradebook, student.getIdentifier());
		String displayGrade = getDisplayGrade(gradebook, courseGradeRecord, categoriesWithAssignments, studentGradeMap);
		
		for (AssignmentGradeRecord record : studentGradeMap.values()) {
			Long aId = record.getGradableObject().getId();
			String dropProperty =  concat(String.valueOf(aId), StudentModel.DROP_FLAG);
			String excuseProperty = concat(String.valueOf(aId), StudentModel.EXCUSE_FLAG);
			boolean isDropped = record.isDropped() != null && record.isDropped().booleanValue();
			boolean isExcluded = record.isExcluded() != null && record.isExcluded().booleanValue();
			
			if (isDropped || isExcluded)
				student.set(dropProperty, Boolean.TRUE);
			else if (student.get(dropProperty) != null) 
				student.set(dropProperty, null);
			
			if (isExcluded) 
				student.set(excuseProperty, Boolean.TRUE);
		}
		
		String gradedProperty = assignment.getId() + StudentModel.GRADED_FLAG;
		if (gbService.isStudentGraded(student.getIdentifier(), assignment.getId())) 		
			student.set(gradedProperty, Boolean.TRUE);
		else
			student.set(gradedProperty, null);
		
		String commentedProperty = assignment.getId() + StudentModel.COMMENTED_FLAG;
		if (gbService.isStudentCommented(student.getIdentifier(), assignment.getId())) 		
			student.set(commentedProperty, Boolean.TRUE);
		else
			student.set(commentedProperty, null);
		
		student.set(assignmentId, value);
		student.set(StudentModel.Key.COURSE_GRADE.name(), displayGrade);*/
		
		return student;
	}

	public GradeRecordModel scoreNumericItem(String gradebookUid, Long gradebookId,
			String studentUid, GradeRecordModel assignmentModel, Double value,
			Double previousValue) throws InvalidInputException {

		Assignment assignment = gbService.getAssignment((Long) assignmentModel.getAssignmentId());
		AssignmentGradeRecord assignmentGradeRecord = gbService.getAssignmentGradeRecordForAssignmentForStudent(assignment, studentUid);
		Gradebook gradebook = assignment.getGradebook();
		
		scoreItem(gradebook, assignment, assignmentGradeRecord, studentUid, value);

		Category c = assignment.getCategory(); 
		assignmentModel = createOrUpdateAssignmentRecordModel(gradebookId, assignmentModel, assignmentGradeRecord, c, assignment, true);
		
		String courseGrade = requestCourseGrade(gradebookUid, studentUid);
		assignmentModel.setCourseGrade(courseGrade);
		return assignmentModel;
	}
	
	private void scoreItem(Gradebook gradebook, Assignment assignment, 
			AssignmentGradeRecord assignmentGradeRecord,
			String studentUid, Double value) throws InvalidInputException {
		boolean isUserAbleToGrade = isUserAbleToGradeAll(gradebook.getUid()) || authz.isUserAbleToGradeItemForStudent(gradebook.getUid(), assignment.getId(), studentUid);

		if (!isUserAbleToGrade)
			throw new InvalidInputException("You are not authorized to grade this student for this item.");		
		
		if (assignment.isExternallyMaintained())
			throw new InvalidInputException("This grade item is maintained externally. Please input and edit grades through " + assignment.getExternalAppName());
		
		if (gradebook.getGrade_type() == GradebookService.GRADE_TYPE_POINTS && value != null && value.compareTo(assignment.getPointsPossible()) > 0)
			throw new InvalidInputException("This grade cannot be larger than "+ DataTypeConversionUtil.formatDoubleAsPointsString(assignment.getPointsPossible()));
		else if (gradebook.getGrade_type() == GradebookService.GRADE_TYPE_PERCENTAGE && value != null) {
			if (value.compareTo(Double.valueOf(100d)) > 0) 
				throw new InvalidInputException("This grade cannot be larger than "+ DataTypeConversionUtil.formatDoubleAsPointsString(100d) + "%");
			else if (value.compareTo(Double.valueOf(0d)) < 0)
				throw new InvalidInputException("This grade cannot be less than "+ DataTypeConversionUtil.formatDoubleAsPointsString(0d) + "%");
		}
	
		if (assignmentGradeRecord.isExcluded() != null && assignmentGradeRecord.isExcluded().booleanValue()) 
			throw new InvalidInputException("The student has been excused from this assignment. It is no longer possible to assign him or her a grade.");
		
		switch (gradebook.getGrade_type()) {
		case GradebookService.GRADE_TYPE_POINTS:
			assignmentGradeRecord.setPointsEarned(value);
			break;
		case GradebookService.GRADE_TYPE_PERCENTAGE:
			BigDecimal pointsEarned = gradeCalculations.getPercentAsPointsEarned(assignment, value);
			Double pointsEarnedDouble = pointsEarned == null ? null : Double.valueOf(pointsEarned.doubleValue());
			assignmentGradeRecord.setPointsEarned(pointsEarnedDouble);
			assignmentGradeRecord.setPercentEarned(value);
			break;
		}
		
		// Prepare record for update
		assignmentGradeRecord.setGradableObject(assignment);
		assignmentGradeRecord.setStudentId(studentUid);
		
		Collection<AssignmentGradeRecord> gradeRecords = new LinkedList<AssignmentGradeRecord>();
		gradeRecords.add(assignmentGradeRecord);
		gbService.updateAssignmentGradeRecords(assignment, gradeRecords, gradebook.getGrade_type());
	}


	public StudentModel scoreTextItem(String gradebookUid, StudentModel student, String property, String value, String previousValue) throws InvalidInputException {
		if (value != null && value.trim().equals(""))
			value = null;
		
		// FIXME: Currently only handles grade override edits -- this should handle non-numeric grades too
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		CourseGradeRecord courseGradeRecord = gbService.getStudentCourseGradeRecord(gradebook, student.getIdentifier());
		courseGradeRecord.setEnteredGrade(value);
		Collection<CourseGradeRecord> gradeRecords = new LinkedList<CourseGradeRecord>();
		gradeRecords.add(courseGradeRecord);
		// FIXME: We shouldn't be looking up the CourseGrade if we don't use it anywhere.
		CourseGrade courseGrade = gbService.getCourseGrade(gradebook.getId());
		gbService.updateCourseGradeRecords(courseGrade, gradeRecords);
		
		String freshCourseGrade = requestCourseGrade(gradebookUid, student.getIdentifier());
		student.set(StudentModel.Key.GRADE_OVERRIDE.name(), courseGradeRecord.getEnteredGrade());
		student.set(StudentModel.Key.COURSE_GRADE.name(), freshCourseGrade);
		
		return student;
	}
	
	
	public GradeRecordModel scoreTextItem(String gradebookUid, Long gradebookId, 
			String studentUid, GradeRecordModel assignmentModel, String value, String previousValue) throws InvalidInputException {

		Assignment assignment = gbService.getAssignment((Long) assignmentModel.getAssignmentId());
		
		AssignmentGradeRecord assignmentGradeRecord = gbService.getAssignmentGradeRecordForAssignmentForStudent(assignment, studentUid);
		assignmentGradeRecord.setGradableObject(assignment);
		assignmentGradeRecord.setStudentId(studentUid);
		
		assignmentGradeRecord.setLetterEarned(value);
		Collection<AssignmentGradeRecord> gradeRecords = new LinkedList<AssignmentGradeRecord>();
		gradeRecords.add(assignmentGradeRecord);
		gbService.updateAssignmentGradeRecords(assignment, gradeRecords);
				
		Category c = assignment.getCategory(); 
		assignmentModel = createOrUpdateAssignmentRecordModel(gradebookId, assignmentModel, assignmentGradeRecord, c, assignment, true);
		
		String courseGrade = requestCourseGrade(gradebookUid, studentUid);
		assignmentModel.setCourseGrade(courseGrade);
		return assignmentModel;
	}
	
	private ItemModel updateCategoryField(String categoryId, ItemModel.Key key, Object value) 
		throws InvalidInputException {
		Category category = gbService.getCategory(Long.valueOf(categoryId));
	
		List<Assignment> updatedAssignments = null;
		
		boolean isChanged = true;
		switch (key) {
		case NAME:
			category.setName(convertString(value));
			break;
		case WEIGHT: case PERCENT_COURSE_GRADE:
			double w = value == null ? 0d : ((Double)value).doubleValue() * 0.01;
			category.setWeight(Double.valueOf(w));
			
			if (w == 0d)
				category.setUnweighted(Boolean.TRUE);
			break;
		case EQUAL_WEIGHT:
			Boolean isEqualWeighting = convertBoolean(value);
			category.setEqualWeightAssignments(isEqualWeighting);
			break;
		case EXTRA_CREDIT:
			category.setExtraCredit(convertBoolean(value));
			break;
		case INCLUDED:
			if (category.isRemoved()) {
				throw new InvalidInputException("You cannot include a deleted category in grade. Please undelete the category first.");
			}
			boolean isUnweighted = ! convertBoolean(value).booleanValue();
			category.setUnweighted(Boolean.valueOf(isUnweighted));
			/*if (isUnweighted) {
				category.setWeight(Double.valueOf(0d));
			}
			else*/ if (!isUnweighted) {
				// Since we don't want to leave the category weighting as 0 if a category has been re-included,
				// but we don't know what the user wants it to be, we set it to 1%
				double aw = category.getWeight() == null ? 0d : category.getWeight().doubleValue();
				if (aw == 0d)
					category.setWeight(Double.valueOf(0.01));
			}
			break;
		case DROP_LOWEST:
			category.setDrop_lowest(convertInteger(value).intValue());
			break;
		case REMOVED:
			category.setRemoved(convertBoolean(value).booleanValue());
			category.setUnweighted(Boolean.TRUE);
			break;
		default:
			isChanged = false;
		}
		
		if (isChanged) 
			gbService.updateCategory(category);
		
		switch (key) {
		case EQUAL_WEIGHT:
			Boolean isEqualWeighting = convertBoolean(value);
			updatedAssignments = recalculateAssignmentWeights(category.getId(), isEqualWeighting);
			break;
		}
		
		
		/*Gradebook gradebook = category.getGradebook();
		List<ItemModel> itemModels = new ArrayList<ItemModel>();
		
		if (updatedAssignments == null || updatedAssignments.isEmpty())
			updatedAssignments = gbService.getAssignmentsForCategory(category.getId());
		
		itemModels.add(createItemModel(gradebook));
		ItemModel categoryItemModel = createItemModel(gradebook, category, updatedAssignments);
		itemModels.add(categoryItemModel);
		BigDecimal percentGrade = BigDecimal.valueOf(categoryItemModel.getPercentCourseGrade().doubleValue());
		BigDecimal percentCategory = BigDecimal.valueOf(categoryItemModel.getPercentCategory().doubleValue());
		BigDecimal Big_100 = new BigDecimal(100d);
		if (updatedAssignments != null) {
			for (Assignment a : updatedAssignments) {
				BigDecimal assignmentWeight = BigDecimal.valueOf(a.getAssignmentWeighting().doubleValue());
				BigDecimal courseGradePercent = calculateItemGradePercent(percentGrade, percentCategory, assignmentWeight);
				
				itemModels.add(createItemModel(category, a, courseGradePercent));
			}
		}
		*/
		
		return getItemModelsForCategory(category);
	}
	
	private ItemModel getItemModelsForCategory(Category category) {
		Gradebook gradebook = category.getGradebook();
		
		List<Assignment> assignments = gbService.getAssignmentsForCategory(category.getId());
		
		ItemModel gradebookItemModel = createItemModel(gradebook);
		
		ItemModel categoryItemModel = createItemModel(gradebook, category, assignments);
		categoryItemModel.setParent(gradebookItemModel);
		gradebookItemModel.add(categoryItemModel);
		
		BigDecimal percentGrade = BigDecimal.valueOf(categoryItemModel.getPercentCourseGrade().doubleValue());
		BigDecimal percentCategory = BigDecimal.valueOf(categoryItemModel.getPercentCategory().doubleValue());
		BigDecimal Big_100 = new BigDecimal(100d);
		if (assignments != null) {
			for (Assignment a : assignments) {
				BigDecimal assignmentWeight = BigDecimal.valueOf(a.getAssignmentWeighting().doubleValue());
				BigDecimal courseGradePercent = calculateItemGradePercent(percentGrade, percentCategory, assignmentWeight);
				
				if (DataTypeConversionUtil.checkBoolean(a.isUnweighted())) 
					courseGradePercent = BigDecimal.ZERO;
				
				ItemModel assignmentItemModel = createItemModel(category, a, courseGradePercent);
				assignmentItemModel.setParent(categoryItemModel);
				categoryItemModel.add(assignmentItemModel);
				//itemModels.add(assignmentItemModel);
			}
		}
		
		return categoryItemModel;
	}
	
	private CategoryModel updateCategoryField(String categoryId, CategoryModel.Key key, Object value) 
		throws InvalidInputException {
		Category category = gbService.getCategory(Long.valueOf(categoryId));
	
		boolean isChanged = true;
		switch (key) {
		case NAME:
			category.setName(convertString(value));
			break;
		case WEIGHT:
			double w = value == null ? 0d : ((Double)value).doubleValue() * 0.01;
			category.setWeight(Double.valueOf(w));
			
			if (w == 0d)
				category.setUnweighted(Boolean.TRUE);
			break;
		case EQUAL_WEIGHT:
			Boolean isEqualWeighting = convertBoolean(value);
			category.setEqualWeightAssignments(isEqualWeighting);
			break;
		case EXTRA_CREDIT:
			category.setExtraCredit(convertBoolean(value));
			break;
		case INCLUDED:
			if (category.isRemoved()) {
				throw new InvalidInputException("You cannot include a deleted category in grade. Please undelete the category first.");
			}
			boolean isUnweighted = ! convertBoolean(value).booleanValue();
			category.setUnweighted(Boolean.valueOf(isUnweighted));
			if (isUnweighted)
				category.setWeight(Double.valueOf(0d));
			else {
				// Since we don't want to leave the category weighting as 0 if a category has been re-included,
				// but we don't know what the user wants it to be, we set it to 1%
				double aw = category.getWeight() == null ? 0d : category.getWeight().doubleValue();
				if (aw == 0d)
					category.setWeight(Double.valueOf(0.01));
			}
			break;
		case DROP_LOWEST:
			category.setDrop_lowest(convertInteger(value).intValue());
			break;
		case REMOVED:
			category.setRemoved(convertBoolean(value).booleanValue());
			category.setUnweighted(Boolean.TRUE);
			break;
		default:
			isChanged = false;
		}
		
		if (isChanged) 
			gbService.updateCategory(category);
		
		switch (key) {
		case EQUAL_WEIGHT:
			Boolean isEqualWeighting = convertBoolean(value);
			recalculateAssignmentWeights(category.getId(), isEqualWeighting);
			break;
		}
		
		
		Gradebook gradebook = category.getGradebook();
		return createCategoryModel(gradebook, category);
	}
	
	/*public StudentModel updateEntity(String gradebookUid, StudentModel student, String property, Double value, Double startValue) throws InvalidInputException {
		return scoreNumericItem(gradebookUid, student, property, value, startValue);
	}
	
	public StudentModel updateEntity(String gradebookUid, StudentModel student, String property, String value, String startValue) throws InvalidInputException {
		return scoreTextItem(gradebookUid, student, property, value, startValue);
	}*/
	
	
	private void recalculateAssignmentGradeRecords(String assignmentId, Double value, Double startValue) {
		Assignment assignment = gbService.getAssignment(Long.valueOf(assignmentId));
		Gradebook gradebook = assignment.getGradebook();
		
		// FIXME: Ensure that only users with access to all the students' records can call this method!!!
		Map<String, EnrollmentRecord> enrollmentRecordMap = findEnrollmentRecords(gradebook.getUid(), gradebook.getId(), null, null);
		List<String> studentUids = new ArrayList<String>(enrollmentRecordMap.keySet());
		List<EnrollmentRecord> enrollmentRecords = new ArrayList<EnrollmentRecord>(enrollmentRecordMap.values());
		
		//Collections.sort(enrollmentRecords, ENROLLMENT_NAME_COMPARATOR);
		
		List<AssignmentGradeRecord> gradeRecords = gbService.getAssignmentGradeRecords(assignment, studentUids);
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
	
	private ItemModel updateGradebookField(ItemModel itemModel, ItemModel.Key key, Object value) {
		
		Gradebook gradebook = gbService.getGradebook(itemModel.getIdentifier());
		
		switch (key) {
		case NAME:
			gradebook.setName((String)value);
			break;
		case CATEGORYTYPE:
			switch ((CategoryType)value) {
			case NO_CATEGORIES:
				gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_NO_CATEGORY);
				break;
			case SIMPLE_CATEGORIES:
				gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_ONLY_CATEGORY);
				break;
			case WEIGHTED_CATEGORIES:
				gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY);
				break;
			}
			break;
		case GRADETYPE:
			switch ((GradeType)value) {
			case POINTS: 
				gradebook.setGrade_type(GradebookService.GRADE_TYPE_POINTS);
				break;
			case PERCENTAGES: 
				gradebook.setGrade_type(GradebookService.GRADE_TYPE_PERCENTAGE);
				break;
			case LETTERS:
				gradebook.setGrade_type(GradebookService.GRADE_TYPE_LETTER);
				break;
			}
			break;
		case RELEASEGRADES:
			Boolean b = (Boolean)value;
			gradebook.setCourseGradeDisplayed(b == null ? false : b.booleanValue());
			break;
		}
		
		gbService.updateGradebook(gradebook);
		
		List<Category> categoriesWithAssignments = getCategoriesWithAssignments(gradebook.getId());
		return getItemModel(gradebook, categoriesWithAssignments);
	}
	
	private GradebookModel updateGradebookField(Long gradebookId, GradebookModel.Key key, Object value) {
		
		Gradebook gradebook = gbService.getGradebook(gradebookId);
		
		switch (key) {
		case NAME:
			gradebook.setName((String)value);
			break;
		case CATEGORYTYPE:
			switch ((CategoryType)value) {
			case NO_CATEGORIES:
				gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_NO_CATEGORY);
				break;
			case SIMPLE_CATEGORIES:
				gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_ONLY_CATEGORY);
				break;
			case WEIGHTED_CATEGORIES:
				gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY);
				break;
			}
			break;
		case GRADETYPE:
			switch ((GradeType)value) {
			case POINTS: 
				gradebook.setGrade_type(GradebookService.GRADE_TYPE_POINTS);
				break;
			case PERCENTAGES: 
				gradebook.setGrade_type(GradebookService.GRADE_TYPE_PERCENTAGE);
				break;
			case LETTERS:
				gradebook.setGrade_type(GradebookService.GRADE_TYPE_LETTER);
				break;
			}
			break;
		case RELEASEGRADES:
			Boolean b = (Boolean)value;
			gradebook.setCourseGradeDisplayed(b == null ? false : b.booleanValue());
			break;
		}
		
		gbService.updateGradebook(gradebook);
		
		return createGradebookModel(gradebook);
	}
	
	public <X extends BaseModel> List<X> updateGradeScaleField(String gradebookUid, GradeScaleRecordModel.Key key, Object value, String affectedLetterGrade) {
				
		List<X> gradeScaleMappings = new ArrayList<X>();
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		GradeMapping gradeMapping = gradebook.getSelectedGradeMapping();
		GradingScale gradingScale = gradeMapping.getGradingScale();
		Map<String, Double> gradingScaleMap = gradingScale.getDefaultBottomPercents();
		Map<String, Double> newGradingSacleMap = new HashMap<String, Double>();
		List<String> letterGradesList = new ArrayList<String>(gradingScaleMap.keySet());

		Collections.sort(letterGradesList, LETTER_GRADE_COMPARATOR);

		Double upperScale = null;
		
		GradeScaleRecordModel gradeScaleModel = null;
		
		for(String letterGrade : letterGradesList) {
			
			upperScale = (null == upperScale) ? new Double(100d) : 
				upperScale.equals(Double.valueOf(0d)) ? Double.valueOf(0d) :
				Double.valueOf(upperScale.doubleValue() - 0.00001d);
			
			if(affectedLetterGrade.equals(letterGrade)) {
				gradeScaleModel = new GradeScaleRecordModel(letterGrade, (Double)value, upperScale);
				newGradingSacleMap.put(letterGrade, (Double)value);
				upperScale = (Double)value;
			}
			else {
				gradeScaleModel = new GradeScaleRecordModel(letterGrade, gradingScaleMap.get(letterGrade), upperScale);
				newGradingSacleMap.put(letterGrade, gradingScaleMap.get(letterGrade));
				upperScale = gradingScaleMap.get(letterGrade);
			}
			
			gradeScaleMappings.add((X)gradeScaleModel);
		}
		
		gradingScale.setDefaultBottomPercents(newGradingSacleMap);
		gradebook.setSelectedGradeMapping(new GradeMapping(gradingScale));
		gbService.saveOrUpdateLetterGradePercentMapping(newGradingSacleMap, gradebook);
		
		return gradeScaleMappings;
	}
	
	/*private GradebookModel updateGradebook(GradebookModel model, GradebookModel.Key key) {
		Long gradebookId = model.getGradebookId();
		Gradebook gradebook = gradebookManager.getGradebook(gradebookId);
		
		switch (key) {
		case NAME:
			gradebook.setName(model.getName());
			break;
		case CATEGORYTYPE:
			switch (model.getCategoryType()) {
			case NO_CATEGORIES:
				gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_NO_CATEGORY);
				break;
			case SIMPLE_CATEGORIES:
				gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_ONLY_CATEGORY);
				break;
			case WEIGHTED_CATEGORIES:
				gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY);
				break;
			}
			break;
		case GRADETYPE:
			switch (model.getGradeType()) {
			case POINTS: 
				gradebook.setGrade_type(GradebookService.GRADE_TYPE_POINTS);
				break;
			case PERCENTAGES: 
				gradebook.setGrade_type(GradebookService.GRADE_TYPE_PERCENTAGE);
				break;
			case LETTERS:
				gradebook.setGrade_type(GradebookService.GRADE_TYPE_LETTER);
				break;
			}
			break;
		case RELEASEGRADES:
			gradebook.setCourseGradeDisplayed(model.isReleaseGrades() == null ? false : model.isReleaseGrades().booleanValue());
			break;
		}
		
		gradebookManager.updateGradebook(gradebook);
		
		return createGradebookModel(gradebook);
	}*/
	
	protected <X extends BaseModel> List<X> getSelectedGradeMapping(String gradebookUid) {
		
		List<X> gradeScaleMappings = new ArrayList<X>();
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		GradeMapping gradeMapping = gradebook.getSelectedGradeMapping();
		GradingScale gradingScale = gradeMapping.getGradingScale();
		Map<String, Double> gradingScaleMap = gradingScale.getDefaultBottomPercents();
		
		List<String> letterGradesList = new ArrayList<String>(gradingScaleMap.keySet());

		Collections.sort(letterGradesList, LETTER_GRADE_COMPARATOR);

		Double upperScale = null;
		
		for(String letterGrade : letterGradesList) {
			
			upperScale = (null == upperScale) ? new Double(100d) : 
				upperScale.equals(Double.valueOf(0d)) ? Double.valueOf(0d) :
				Double.valueOf(upperScale.doubleValue() - 0.00001d);
			
			GradeScaleRecordModel gradeScaleModel = new GradeScaleRecordModel(letterGrade, gradingScaleMap.get(letterGrade), upperScale);
			gradeScaleMappings.add((X)gradeScaleModel);
			upperScale = gradingScaleMap.get(letterGrade);
		}
		
		return gradeScaleMappings;
	}
	
	private ItemModel updateGradebookModel(ItemModel item) {
		
		Gradebook gradebook = gbService.getGradebook(item.getIdentifier());
		
		gradebook.setName(item.getName());
		
		int oldCategoryType = gradebook.getCategory_type();
		int newCategoryType = -1;
		
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
		
		boolean wasReleaseGrades = gradebook.isCourseGradeDisplayed();
		boolean isReleaseGrades = DataTypeConversionUtil.checkBoolean(item.getReleaseGrades());
		
		gradebook.setCourseGradeDisplayed(isReleaseGrades);
		
		gbService.updateGradebook(gradebook);
		
		List<Category> categoriesWithAssignments = getCategoriesWithAssignments(gradebook.getId());
		return getItemModel(gradebook, categoriesWithAssignments);
	}
	
	private ItemModel updateCategoryModel(ItemModel item) throws InvalidInputException {
		
		boolean isWeightChanged = false;
		
		Category category = gbService.getCategory(Long.valueOf(item.getIdentifier()));
	
		List<Assignment> updatedAssignments = null;
		
		category.setName(convertString(item.getName()));
		
		boolean originalExtraCredit = DataTypeConversionUtil.checkBoolean(category.isExtraCredit());
		boolean currentExtraCredit = DataTypeConversionUtil.checkBoolean(item.getExtraCredit());
		
		isWeightChanged = originalExtraCredit != currentExtraCredit;
		
		category.setExtraCredit(Boolean.valueOf(currentExtraCredit));
		
		Double newCategoryWeight = item.getPercentCourseGrade();
		Double oldCategoryWeight = category.getWeight();
		
		isWeightChanged = isWeightChanged || DataTypeConversionUtil.notEquals(newCategoryWeight, oldCategoryWeight);

		double w = newCategoryWeight == null ? 0d : ((Double)newCategoryWeight).doubleValue() * 0.01;
		category.setWeight(Double.valueOf(w));
		
		if (w == 0d)
			category.setUnweighted(Boolean.TRUE);

		
		boolean isEqualWeighting = DataTypeConversionUtil.checkBoolean(item.getEqualWeightAssignments());
		boolean wasEqualWeighting = DataTypeConversionUtil.checkBoolean(category.isEqualWeightAssignments());
		
		category.setEqualWeightAssignments(Boolean.valueOf(isEqualWeighting));
		
		isWeightChanged = isWeightChanged || isEqualWeighting != wasEqualWeighting;
		
		
		boolean isUnweighted = !DataTypeConversionUtil.checkBoolean(item.getIncluded());
		boolean wasUnweighted = DataTypeConversionUtil.checkBoolean(category.isUnweighted());
		
		if (wasUnweighted && !isUnweighted && category.isRemoved())
			throw new InvalidInputException("You cannot include a deleted category in grade. Please undelete the category first.");

		
		if (!isUnweighted) {
			// Since we don't want to leave the category weighting as 0 if a category has been re-included,
			// but we don't know what the user wants it to be, we set it to 1%
			double aw = category.getWeight() == null ? 0d : category.getWeight().doubleValue();
			if (aw == 0d)
				category.setWeight(Double.valueOf(0.01));
		}
	
		int oldDropLowest = category.getDrop_lowest();
		int newDropLowest = convertInteger(item.getDropLowest()).intValue();
		
		category.setDrop_lowest(newDropLowest);

		boolean isRemoved = DataTypeConversionUtil.checkBoolean(item.getRemoved());
		boolean wasRemoved = category.isRemoved();
		
		category.setRemoved(isRemoved);
		category.setUnweighted(Boolean.valueOf(isUnweighted || isRemoved));
		
		gbService.updateCategory(category);
		
		if (isEqualWeighting && !wasEqualWeighting) {
			recalculateAssignmentWeights(category.getId(), Boolean.valueOf(isEqualWeighting));
		}
		
		ItemModel categoryItemModel = getItemModelsForCategory(category);
		categoryItemModel.setActive(true);
		
		return categoryItemModel;
	}
	
	private ItemModel updateItemModel(ItemModel item) throws InvalidInputException {
		
		switch (item.getItemType()) {
		case CATEGORY:
			return updateCategoryModel(item);
		case GRADEBOOK:
			return updateGradebookModel(item);
		}
		
		boolean isWeightChanged = false;
		
		Assignment assignment = gbService.getAssignment(Long.valueOf(item.getIdentifier()));
		
		Category oldCategory = null;
		Category category = assignment.getCategory();
		
		Gradebook gradebook = assignment.getGradebook();
		
		assignment.setName(convertString(item.getName()));
		
		if (!category.getId().equals(item.getCategoryId())) {
			oldCategory = category;
			category = gbService.getCategory(item.getCategoryId());
			assignment.setCategory(category);
		}
		
		boolean originalExtraCredit = DataTypeConversionUtil.checkBoolean(assignment.isExtraCredit());
		boolean currentExtraCredit = DataTypeConversionUtil.checkBoolean(item.getExtraCredit());
		
		isWeightChanged = originalExtraCredit != currentExtraCredit;
		
		assignment.setExtraCredit(Boolean.valueOf(currentExtraCredit));
		assignment.setReleased(convertBoolean(item.getReleased()).booleanValue());
		
		assignment.setPointsPossible(convertDouble(item.getPoints()));
		assignment.setDueDate(convertDate(item.getDueDate()));
		
		Double newAssignmentWeight = item.getPercentCategory();
		Double oldAssignmentWeight = assignment.getAssignmentWeighting();
		
		isWeightChanged = isWeightChanged || DataTypeConversionUtil.notEquals(newAssignmentWeight, oldAssignmentWeight);
		
		double w = newAssignmentWeight == null ? 0d : ((Double)newAssignmentWeight).doubleValue() * 0.01;
		assignment.setAssignmentWeighting(Double.valueOf(w));
		
		boolean isUnweighted = !convertBoolean(item.getIncluded()).booleanValue();
		boolean wasUnweighted = DataTypeConversionUtil.checkBoolean(assignment.isUnweighted());
		
		isWeightChanged = isWeightChanged || isUnweighted != wasUnweighted;
		
		if (!isUnweighted) {
			boolean isAssignmentRemoved = assignment.isRemoved();
			if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY) {
				if (category.isRemoved()) 
					throw new InvalidInputException("You cannot include a grade item whose category has been deleted in grading. Please undelete the category first.");
				isAssignmentRemoved = assignment.isRemoved() || category.isRemoved();
			}
			
			if (isAssignmentRemoved) 
				throw new InvalidInputException("You cannot include a deleted grade item in grading. Please undelete the grade item first.");
		}
		
		boolean isRemoved = convertBoolean(item.getRemoved()).booleanValue();
		boolean wasRemoved = DataTypeConversionUtil.checkBoolean(assignment.isRemoved());
		
		isWeightChanged = isWeightChanged || isRemoved != wasRemoved;
		
		if (!isRemoved) {
			if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY && category.isRemoved()) {
				throw new InvalidInputException("You cannot undelete a grade item when the category that owns it has been deleted. Please undelete the category first.");
			}
			assignment.setRemoved(isRemoved);
		}
		
		assignment.setUnweighted(Boolean.valueOf(isUnweighted || isRemoved || w == 0d));
		
		/*if (isUnweighted)
			assignment.setAssignmentWeighting(Double.valueOf(0.0));
		else {
			// Since we don't want to leave the assignment weighting as 0 if an assignment has been re-included,
			// but we don't know what the user wants it to be, we set it to 1%
			double aw = assignment.getAssignmentWeighting() == null ? 0d : assignment.getAssignmentWeighting().doubleValue();
			if (aw == 0d)
				assignment.setAssignmentWeighting(Double.valueOf(0.01));
		} */


		gbService.updateAssignment(assignment);

		
		// Since changing the assignment weight implies that the category is not equally weighted,
		// ensure that we switch it when the assignment weight changes.
		if (oldAssignmentWeight == null || !oldAssignmentWeight.equals(newAssignmentWeight)) {
				
			if (category.isEqualWeightAssignments() != null && category.isEqualWeightAssignments().booleanValue()) {
				Category editCategory = gbService.getCategory(category.getId());
				editCategory.setEqualWeightAssignments(Boolean.FALSE);
				gbService.updateCategory(editCategory);
			}
		}
		
		if (oldCategory != null) {
			recalculateAssignmentWeights(oldCategory.getId(), null);
		
			List<Category> categoriesWithAssignments = getCategoriesWithAssignments(gradebook.getId());
			return getItemModel(gradebook, categoriesWithAssignments);
		}
		
		if (!isWeightChanged) {
			ItemModel itemModel = createItemModel(category, assignment, null);
			itemModel.setActive(true);
			return itemModel;
		}
		
		ItemModel categoryItemModel = getItemModelsForCategory(category);
		
		String assignmentIdAsString = String.valueOf(assignment.getId());
		for (ItemModel model : categoryItemModel.getChildren()) {
			if (model.getIdentifier().equals(assignmentIdAsString)) 
				model.setActive(true);
		}
		
		return categoryItemModel;
	}
	
	private ItemModel updateItemField(String assignmentId, ItemModel.Key key, 
			Object value) throws InvalidInputException {
		
		Assignment assignment = gbService.getAssignment(Long.valueOf(assignmentId));
		
		Category oldCategory = null;
		Category category = assignment.getCategory();
		
		Gradebook gradebook = assignment.getGradebook();
		
		
		switch (key) {
		case NAME:
			assignment.setName(convertString(value));
			break;
		case CATEGORY_ID:
			oldCategory = category;
			category = gbService.getCategory((Long)value);
			assignment.setCategory(category);
			break;
		case EXTRA_CREDIT:
			assignment.setExtraCredit(convertBoolean(value));
			break;
		case RELEASED:
			assignment.setReleased(convertBoolean(value).booleanValue());
			break;
		case WEIGHT: case PERCENT_CATEGORY:
			double w = value == null ? 0d : ((Double)value).doubleValue() * 0.01;
			Double newValue = Double.valueOf(w);
			Double oldValue = assignment.getAssignmentWeighting();
			
			// Since changing the assignment weight implies that the category is not equally weighted,
			// ensure that we switch it when the assignment weight changes.
			if (oldValue == null || !oldValue.equals(newValue)) {
				
				if (category.isEqualWeightAssignments() != null && category.isEqualWeightAssignments().booleanValue()) {
					Category editCategory = gbService.getCategory(category.getId());
					editCategory.setEqualWeightAssignments(Boolean.FALSE);
					gbService.updateCategory(editCategory);
				}
			}
			
			assignment.setAssignmentWeighting(newValue);
			
			if (w == 0d)
				assignment.setUnweighted(Boolean.TRUE);
			
			break;
		case POINTS:
			assignment.setPointsPossible(convertDouble(value));
			break;
		case DUE_DATE:
			assignment.setDueDate(convertDate(value));
			break;
		case INCLUDED:
			boolean isAssignmentRemoved = assignment.isRemoved();
			if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY) {
				if (category.isRemoved()) 
					throw new InvalidInputException("You cannot include a grade item whose category has been deleted in grading. Please undelete the category first.");
				isAssignmentRemoved = assignment.isRemoved() || category.isRemoved();
			}
			
			if (isAssignmentRemoved) 
				throw new InvalidInputException("You cannot include a deleted grade item in grading. Please undelete the grade item first.");
			
			boolean isUnweighted = !convertBoolean(value).booleanValue();
			assignment.setUnweighted(Boolean.valueOf(isUnweighted));
			if (isUnweighted)
				assignment.setAssignmentWeighting(Double.valueOf(0.0));
			else {
				// Since we don't want to leave the assignment weighting as 0 if an assignment has been re-included,
				// but we don't know what the user wants it to be, we set it to 1%
				double aw = assignment.getAssignmentWeighting() == null ? 0d : assignment.getAssignmentWeighting().doubleValue();
				if (aw == 0d)
					assignment.setAssignmentWeighting(Double.valueOf(0.01));
			} 
			break;
		case REMOVED:
			if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY && category.isRemoved()) {
				throw new InvalidInputException("You cannot undelete a grade item when the category that owns it has been deleted. Please undelete the category first.");
			}
			boolean isRemoved = convertBoolean(value).booleanValue();
			assignment.setRemoved(isRemoved);
			assignment.setUnweighted(Boolean.TRUE);
			break;
		};
		
		gbService.updateAssignment(assignment);
		
		
		switch (key) {
		case CATEGORY_ID:
		case EXTRA_CREDIT:
		case INCLUDED:
		case REMOVED:
			recalculateAssignmentWeights(category.getId(), null);
			break;
		}
		
		/*if (updatedAssignments == null || updatedAssignments.isEmpty())
			updatedAssignments = gbService.getAssignmentsForCategory(category.getId());
		
		List<ItemModel> itemModels = new ArrayList<ItemModel>();
		
		ItemModel categoryItemModel = createItemModel(gradebook, category, updatedAssignments);
		itemModels.add(categoryItemModel);
		BigDecimal percentGrade = BigDecimal.valueOf(categoryItemModel.getPercentCourseGrade().doubleValue());
		BigDecimal percentCategory = BigDecimal.valueOf(categoryItemModel.getPercentCategory().doubleValue());
		BigDecimal Big_100 = new BigDecimal(100d);
		if (updatedAssignments != null) {
			for (Assignment a : updatedAssignments) {
				BigDecimal assignmentWeight = BigDecimal.valueOf(a.getAssignmentWeighting().doubleValue());
				BigDecimal courseGradePercent = calculateItemGradePercent(percentGrade, percentCategory, assignmentWeight);
				
				itemModels.add(createItemModel(category, a, courseGradePercent));
			}
		} else {
			BigDecimal assignmentWeight = BigDecimal.valueOf(assignment.getAssignmentWeighting().doubleValue());
			BigDecimal courseGradePercent = calculateItemGradePercent(percentGrade, percentCategory, assignmentWeight);
			
			itemModels.add(createItemModel(category, assignment, courseGradePercent));
		}*/
		
		/*List<ItemModel> models = new ArrayList<ItemModel>();
		
		if (oldCategory != null) {
			recalculateAssignmentWeights(oldCategory.getId(), null);
			models.addAll(getItemModelsForCategory(oldCategory));
		}
		
		models.addAll(getItemModelsForCategory(category));
		*/
		
		if (oldCategory != null) 
			recalculateAssignmentWeights(oldCategory.getId(), null);
		
		List<ItemModel> models = new ArrayList<ItemModel>();
		switch (key) {
		// Changing categories requires that we rebuild the entire tree
		case CATEGORY_ID:
			List<Category> categoriesWithAssignments = getCategoriesWithAssignments(gradebook.getId());
			return getItemModel(gradebook, categoriesWithAssignments);
		// Changing these properties means that we only need to update the item
		case NAME:
		case RELEASED:
		case POINTS:
		case DUE_DATE:
			return createItemModel(category, assignment, null);
		}
		
		// Otherwise, we need to update the item's category -- generally to re-calculate weights
		return getItemModelsForCategory(category);
		//return getItemModelsForGradebook(gradebook);
	}
	
	/*
	 *  For examine, 
	 *  
	 *  (a) If percentGrade is 60, sumCategoryPercents is 100, and assignmentWeight is 0.25, then this item should be worth 15 % of the course grade
	 *  
	 *  	15 = ( 60 * .25 ) / 1
	 *  	
	 *  (b) If percentGrade is 60, sumCategoryPercents is 80, and assignmentWeight is 0.25, then this item should be worth > 15 % of the course grade
	 * 
	 * 		x  = ( 60 * .25 ) / .8
	 * 
	 */
	private BigDecimal calculateItemGradePercent(BigDecimal percentGrade, BigDecimal sumCategoryPercents, BigDecimal assignmentWeight) {
		
		if (percentGrade.compareTo(BigDecimal.ZERO) == 0 
				|| sumCategoryPercents.compareTo(BigDecimal.ZERO) == 0 
				|| assignmentWeight.compareTo(BigDecimal.ZERO) == 0)
			return BigDecimal.ZERO;
		
		BigDecimal categoryPercentRatio = sumCategoryPercents.divide(BigDecimal.valueOf(100d), RoundingMode.HALF_EVEN);
		
		return assignmentWeight.multiply(percentGrade).divide(categoryPercentRatio, RoundingMode.HALF_EVEN);
	}
	
	
	
	private AssignmentModel updateAssignmentField(String assignmentId, AssignmentModel.Key key, 
			Object value) throws InvalidInputException {
		
		Assignment assignment = gbService.getAssignment(Long.valueOf(assignmentId));
		
		Category category = assignment.getCategory();
		
		Gradebook gradebook = assignment.getGradebook();
		
		switch (key) {
		case NAME:
			assignment.setName(convertString(value));
			break;
		case EXTRA_CREDIT:
			assignment.setExtraCredit(convertBoolean(value));
			break;
		case RELEASED:
			assignment.setReleased(convertBoolean(value).booleanValue());
			break;
		case WEIGHT:
			double w = value == null ? 0d : ((Double)value).doubleValue() * 0.01;
			Double newValue = Double.valueOf(w);
			Double oldValue = assignment.getAssignmentWeighting();
			
			// Since changing the assignment weight implies that the category is not equally weighted,
			// ensure that we switch it when the assignment weight changes.
			if (oldValue == null || !oldValue.equals(newValue)) {
				
				if (category.isEqualWeightAssignments() != null && category.isEqualWeightAssignments().booleanValue()) {
					Category editCategory = gbService.getCategory(category.getId());
					editCategory.setEqualWeightAssignments(Boolean.FALSE);
					gbService.updateCategory(editCategory);
				}
			}
			
			assignment.setAssignmentWeighting(newValue);
			
			if (w == 0d)
				assignment.setUnweighted(Boolean.TRUE);
			
			break;
		case POINTS:
			assignment.setPointsPossible(convertDouble(value));
			break;
		case DUE_DATE:
			assignment.setDueDate(convertDate(value));
			break;
		case INCLUDED:
			boolean isAssignmentRemoved = assignment.isRemoved();
			if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY) {
				if (category.isRemoved()) 
					throw new InvalidInputException("You cannot include a grade item whose category has been deleted in grading. Please undelete the category first.");
				isAssignmentRemoved = assignment.isRemoved() || category.isRemoved();
			}
			
			if (isAssignmentRemoved) 
				throw new InvalidInputException("You cannot include a deleted grade item in grading. Please undelete the grade item first.");
			
			boolean isUnweighted = !convertBoolean(value).booleanValue();
			assignment.setUnweighted(Boolean.valueOf(isUnweighted));
			if (isUnweighted)
				assignment.setAssignmentWeighting(Double.valueOf(0.0));
			else {
				// Since we don't want to leave the assignment weighting as 0 if an assignment has been re-included,
				// but we don't know what the user wants it to be, we set it to 1%
				double aw = assignment.getAssignmentWeighting() == null ? 0d : assignment.getAssignmentWeighting().doubleValue();
				if (aw == 0d)
					assignment.setAssignmentWeighting(Double.valueOf(0.01));
			} 
			break;
		case REMOVED:
			if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY && category.isRemoved()) {
				throw new InvalidInputException("You cannot undelete a grade item when the category that owns it has been deleted. Please undelete the category first.");
			}
			boolean isRemoved = convertBoolean(value).booleanValue();
			assignment.setRemoved(isRemoved);
			assignment.setUnweighted(Boolean.TRUE);
			break;
		};
		
		gbService.updateAssignment(assignment);
		
		switch (key) {
		case EXTRA_CREDIT:
		case INCLUDED:
		case REMOVED:
			recalculateAssignmentWeights(category.getId(), null);
			break;
		}
		
		return createAssignmentModel(category, assignment);
	}
	
	/*public AssignmentRecordModel updateGradeRecordModel(String gradebookUid,
			Long gradebookId, String studentUid, AssignmentRecordModel model, AssignmentRecordModel.Key key, Boolean value) {
		return updateGradeRecordModelField(gradebookUid, gradebookId, studentUid, model, key, value);
	}
	
	public AssignmentRecordModel updateGradeRecordModel(String gradebookUid,
			Long gradebookId, String studentUid, AssignmentRecordModel model, AssignmentRecordModel.Key key, String value) {
		return updateGradeRecordModelField(gradebookUid, gradebookId, studentUid, model, key, value);
	}*/
	
	private GradeRecordModel updateGradeRecordModelField(String studentUid, GradeRecordModel model, 
			GradeRecordModel.Key key, Object value) throws InvalidInputException {
		
		Assignment assignment  = gbService.getAssignment(model.getAssignmentId());
		Gradebook gradebook = assignment.getGradebook();
		AssignmentGradeRecord record = gbService.getAssignmentGradeRecordForAssignmentForStudent(assignment, studentUid);
		record.setGradableObject(assignment);
		record.setStudentId(studentUid);
		
		List<Comment> comments = gbService.getStudentAssignmentComments(studentUid, gradebook.getId());
		Comment comment = null;
		
		// TODO: Make sure that there is only one comment per assignment
		if (comments != null && !comments.isEmpty()) {
			for (Comment c : comments) {
				if (c.getGradableObject().getId().equals(assignment.getId())) {
					comment = c;
					break;
				}
			}
		}
		
		switch (key) {
		case POINTS_EARNED:
		case PERCENT_EARNED:
			scoreItem(gradebook, assignment, record, studentUid, (Double)value);

			break;
		case EXCLUDED:
			record.setExcluded(convertBoolean(value));
			Collection<AssignmentGradeRecord> gradeRecords = new LinkedList<AssignmentGradeRecord>();
			gradeRecords.add(record);
			gbService.updateAssignmentGradeRecords(assignment, gradeRecords);
			break;
		case COMMENTS:
			if (comment == null) 
				comment = new Comment(studentUid, convertString(value), assignment);
			else
				comment.setCommentText(convertString(value));
			
			comments.add(comment);
			
			gbService.updateComments(comments);
		};
		
		// FIXME: We don't want to get everybody's grade records here -- need a new method on the manager
		Collection studentUids = new LinkedList<String>();
		studentUids.add(studentUid);
		List<AssignmentGradeRecord> allGradeRecords = gbService.getAllAssignmentGradeRecords(gradebook.getId(), studentUids);
		
		Map<Long, AssignmentGradeRecord> studentGradeMap = new HashMap<Long, AssignmentGradeRecord>();
    	if (allGradeRecords != null) {
	    	for (AssignmentGradeRecord gradeRecord : allGradeRecords) {
				gradeRecord.setUserAbleToView(true);
				if (studentUid.equals(gradeRecord.getStudentId())) {
					GradableObject go = gradeRecord.getGradableObject();
					studentGradeMap.put(go.getId(), gradeRecord);
				}
			}
		}
		
    	// We have to calculate the student's grade in order to ensure that the drop lowest/excuse/overallWeight data is populated
    	List<Assignment> allAssignments = gbService.getAssignments(gradebook.getId());
    	List<Category> categoriesWithAssignments = getCategoriesWithAssignments(gradebook.getId(), allAssignments);
    	CourseGradeRecord courseGradeRecord = gbService.getStudentCourseGradeRecord(gradebook, studentUid);
		
    	String displayGrade = getDisplayGrade(gradebook, courseGradeRecord, categoriesWithAssignments, studentGradeMap);
    	
    	AssignmentGradeRecord updatedRecord = studentGradeMap.get(assignment.getId());
		
		GradeRecordModel assignmentModel = createOrUpdateAssignmentRecordModel(gradebook.getId(), null, updatedRecord, assignment.getCategory(), assignment, true);

		assignmentModel.setCourseGrade(displayGrade);
		
		return assignmentModel;	
	}
	
	// Helper method
	private Double calculateEqualWeight(int numberOfItems) {
		if (numberOfItems <= 1)
			return Double.valueOf(1d);
		
		/*BigDecimal oneHundredPercent = new BigDecimal("1.0");
		BigDecimal itemCount = new BigDecimal(numberOfItems);
		BigDecimal result = oneHundredPercent.divide(itemCount, RoundingMode.HALF_UP);
		result.setScale(10);
		
		return Double.valueOf(result.doubleValue());*/
		
		double result = 1d / (double)numberOfItems;
		
		return Double.valueOf(result);
	}

	// Code taken from "org.sakaiproject.service.gradebook.shared.GradebookService.lettergradeComparator"
	static final Comparator<String> LETTER_GRADE_COMPARATOR = new Comparator<String>() {

		public int compare(String o1, String o2) {

			if(o1.toLowerCase().charAt(0) == o2.toLowerCase().charAt(0)) {

				if(o1.length() == 2 && o2.length() == 2) {

					if(o1.charAt(1) == '+')
						return 0;
					else
						return 1;

				}

				if(o1.length() == 1 && o2.length() == 2) {

					if(o2.charAt(1) == '+')
						return 1;
					else 
						return 0;
				}

				if(o1.length() == 2 && o2.length() == 1) {

					if(o1.charAt(1) == '+')
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
	
	protected boolean isUserAbleToGradeAll(String gradebookUid) {
		// FIXME: In JSF code this is a transient member var. Since we're stateless, we need to figure out
		// if looking it up each time is going to be expensive.
		Boolean userAbleToGradeAll = null;
		if (userAbleToGradeAll == null) {
			userAbleToGradeAll = new Boolean(authz.isUserAbleToGradeAll(gradebookUid));
		}
		return userAbleToGradeAll.booleanValue();
	}
	
	private boolean isUserHasGraderPermissions(Long gradebookId) {
		// FIXME: In JSF code this is a transient member var. Since we're stateless, we need to figure out
		// if looking it up each time is going to be expensive.
		Boolean userHasGraderPermissions = null;
		if (userHasGraderPermissions == null) {
			userHasGraderPermissions = new Boolean(authz.isUserHasGraderPermissions(gradebookId));
		}
		
		return userHasGraderPermissions.booleanValue();
	}
	
	private boolean isUserHasGraderPermissions(Long gradebookId, String userUid) {
		// FIXME: In JSF code this is a transient member var. Since we're stateless, we need to figure out
		// if looking it up each time is going to be expensive.
		Boolean userHasGraderPermissions = null;
		if (userHasGraderPermissions == null) {
			userHasGraderPermissions = new Boolean(authz.isUserHasGraderPermissions(gradebookId, userUid));
		}
		
		return userHasGraderPermissions.booleanValue();
	}
	
	private boolean isUserTAinSection(String sectionUid) {
		String userUid = authn.getUserUid();
		return sectionAwareness.isSectionMemberInRole(sectionUid, userUid, Role.TA);
	}
	
	/*
	 * Old code from the original gradebook project, slightly refactored
	 */
	private List<CourseSection> getAllSections(String siteContext) {
		return sectionAwareness.getSections(siteContext);
	}
	
	/*
	 * Old code from the original gradebook project, slightly refactored
	 */
	public List<String> getViewableGroupsForUser(Long gradebookId, String userId, List<String> groupIds) {
		if(gradebookId == null || userId == null)
			throw new IllegalArgumentException("Null parameter(s) in GradebookPermissionServiceImpl.getViewableSectionsForUser");
		
		if (groupIds == null || groupIds.size() == 0)
			return null;
		
		List<Permission> anyGroupPermission = gbService.getPermissionsForUserAnyGroup(gradebookId, userId);
		if(anyGroupPermission != null && anyGroupPermission.size() > 0 )
		{
			return groupIds;
		}
		else
		{
			List<Permission> permList = gbService.getPermissionsForUserForGroup(gradebookId, userId, groupIds);
			
			List<String> filteredGroups = new ArrayList<String>();
			for(String groupId : groupIds) {
				if(groupId != null && permList != null) {
					for (Permission perm : permList) {
						if(perm != null && perm.getGroupId().equals(groupId))
						{
							filteredGroups.add(groupId);
							break;
						}
					}
				}
			}
			return filteredGroups;
		}
		
	}
	
	/*
	 * Old code from the original gradebook project, slightly refactored
	 */
	protected List<CourseSection> getViewableSections(String gradebookUid, Long gradebookId) {
		List<CourseSection> viewableSections = new ArrayList<CourseSection>();
		
		// FIXME: We shouldn't use gradebookUid here, but site context
		List<CourseSection> allSections = getAllSections(gradebookUid);
		if (allSections == null || allSections.isEmpty()) {
			return viewableSections;
		}
		
		if (isUserAbleToGradeAll(gradebookUid)) {
			return allSections;
		}

		Map<String, CourseSection> sectionIdCourseSectionMap = new HashMap<String, CourseSection>();

		if (allSections != null) {
			for (CourseSection section : allSections) {
				sectionIdCourseSectionMap.put(section.getUuid(), section);
			}
		}
		
		String userUid = authn.getUserUid();
		
		if (isUserHasGraderPermissions(gradebookId, userUid)) {
			List<String> viewableSectionIds = getViewableGroupsForUser(gradebookId, userUid, new ArrayList(sectionIdCourseSectionMap.keySet()));
			if (viewableSectionIds != null && !viewableSectionIds.isEmpty()) {
				for (String sectionUuid : viewableSectionIds) {
					CourseSection viewableSection = (CourseSection)sectionIdCourseSectionMap.get(sectionUuid);
					if (viewableSection != null)
						viewableSections.add(viewableSection);
				}
			}
		} else {
			// return all sections that the current user is a TA for
			for (String sectionUuid : sectionIdCourseSectionMap.keySet()) {
				if (isUserTAinSection(sectionUuid)) {
					CourseSection viewableSection = (CourseSection)sectionIdCourseSectionMap.get(sectionUuid);
					if (viewableSection != null)
						viewableSections.add(viewableSection);
				}
			}
		}
		
		return viewableSections;

	}
	
	protected ItemModel addItem(String gradebookUid, Long gradebookId, ItemModel item) {
	
		Long categoryId = item.getCategoryId(); 
		String name = item.getName();
		Double weight = item.getPercentCategory(); 
		Double points = item.getPoints();
		Boolean isReleased = item.getReleased();
		Boolean isIncluded = item.getIncluded();
		Date dueDate = item.getDueDate();
		//Boolean isEqualWeightAssignments = item.getEqualWeightAssignments();
		
		Boolean isNotCounted = Boolean.FALSE;
		
		if (points == null)
			points = new Double(100d);
		if (weight == null)
			weight = Double.valueOf(points.doubleValue());
		
		Category category = null;
		
		if (categoryId == null) {
			category = findDefaultCategory(gradebookId);
			categoryId = category.getId();
		}
		
		if (category == null)
			category = gbService.getCategory(categoryId);
		
		if (weight == null) {
			List<Assignment> assignments = gbService.getAssignmentsForCategory(categoryId);
			
			if (assignments == null || assignments.isEmpty())
				weight = new Double(100d);
		}
		
		Long assignmentId = gbService.createAssignmentForCategory(gradebookId, categoryId, name, points, dueDate, isNotCounted, isReleased);
		
		Assignment assignment = gbService.getAssignment(assignmentId);
		
		
		double w = weight == null ? 0d : ((Double)weight).doubleValue() * 0.01;
		assignment.setAssignmentWeighting(Double.valueOf(w));
		assignment.setUnweighted(Boolean.valueOf(!DataTypeConversionUtil.checkBoolean(isIncluded)));
		
		gbService.updateAssignment(assignment);
		
		if (DataTypeConversionUtil.checkBoolean(isIncluded) && category.isEqualWeightAssignments() != null && category.isEqualWeightAssignments().booleanValue()) {
			recalculateEqualWeightingGradeItems(gradebookUid, gradebookId, categoryId, category.isEqualWeightAssignments());
			//assignment = gbService.getAssignment(assignment.getId());
		}

		ItemModel categoryItemModel = getItemModelsForCategory(category);
		
		String assignmentIdAsString = String.valueOf(assignmentId);
		for (ItemModel model : categoryItemModel.getChildren()) {
			if (model.getIdentifier().equals(assignmentIdAsString)) 
				model.setActive(true);
		}
		
		return categoryItemModel;
	}
	
	protected ItemModel addItemCategory(String gradebookUid, Long gradebookId,
			ItemModel model) {
		
		String name = model.getName();
		Double weight = model.getPercentCourseGrade();
		Boolean isEqualWeighting = model.getEqualWeightAssignments();
		Boolean isIncluded = model.getIncluded();
		Integer dropLowest = model.getDropLowest();
		
		boolean isUnweighted = !DataTypeConversionUtil.checkBoolean(isIncluded);
		
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		List<Category> categoriesWithAssignments = getCategoriesWithAssignments(gradebook.getId());
		if (!isUnweighted && (categoriesWithAssignments == null || categoriesWithAssignments.isEmpty()) && weight == null)
			weight = Double.valueOf(100d);
			
		double w = weight == null ? 0d : ((Double)weight).doubleValue() * 0.01;
		int dl = dropLowest == null ? 0 : dropLowest.intValue();
		
		Long categoryId = gbService.createCategory(gradebookId, name, Double.valueOf(w), dl);
		
		Category category = gbService.getCategory(categoryId);
		category.setEqualWeightAssignments(isEqualWeighting);
		category.setUnweighted(Boolean.valueOf(isUnweighted));
		gbService.updateCategory(category);
		
		ItemModel categoryItemModel = getItemModelsForCategory(category);
		categoryItemModel.setActive(true);
		return categoryItemModel;
	}
	
	
	protected AssignmentModel addAssignment(String gradebookUid, Long gradebookId, Long categoryId, String name,
			Double weight, Double points, Date dueDate) {
		
		Boolean isNotCounted = Boolean.FALSE;
		Boolean isReleased = Boolean.FALSE;
		
		if (points == null)
			points = new Double(100d);
		if (weight == null)
			weight = Double.valueOf(points.doubleValue());
		
		Category category = null;
		
		if (categoryId == null) {
			category = findDefaultCategory(gradebookId);
			categoryId = category.getId();
		}
		
		if (category == null)
			category = gbService.getCategory(categoryId);
		
		if (weight == null) {
			List<Assignment> assignments = gbService.getAssignmentsForCategory(categoryId);
			
			if (assignments == null || assignments.isEmpty())
				weight = new Double(100d);
		}
		
		Long assignmentId = gbService.createAssignmentForCategory(gradebookId, categoryId, name, points, dueDate, isNotCounted, isReleased);
		
		Assignment assignment = gbService.getAssignment(assignmentId);
		
		
		double w = weight == null ? 0d : ((Double)weight).doubleValue() * 0.01;
		assignment.setAssignmentWeighting(Double.valueOf(w));
		
		gbService.updateAssignment(assignment);
		
		if (category.isEqualWeightAssignments() != null && category.isEqualWeightAssignments().booleanValue()) {
			recalculateEqualWeightingGradeItems(gradebookUid, gradebookId, categoryId, category.isEqualWeightAssignments());
			assignment = gbService.getAssignment(assignment.getId());
		}
		
		AssignmentModel model = createAssignmentModel(category, assignment);
		model.set("isEqualWeightAssignment", category.isEqualWeightAssignments());
		
		return model;
	}
	
	protected CategoryModel addCategory(String gradebookUid, Long gradebookId,
			String name, Double weight, Boolean isEqualWeighting,
			Integer dropLowest) {
		
		List<Category> categories = gbService.getCategories(gradebookId);
		
		if ((categories == null || categories.isEmpty()) && weight == null)
			weight = Double.valueOf(100d);
			
		double w = weight == null ? 1d : ((Double)weight).doubleValue() * 0.01;
		int dl = dropLowest == null ? 0 : dropLowest.intValue();
		
		Long categoryId = gbService.createCategory(gradebookId, name, Double.valueOf(w), dl);
		
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		Category category = gbService.getCategory(categoryId);
		category.setEqualWeightAssignments(isEqualWeighting);
		category.setUnweighted(Boolean.valueOf(w == 0d));
		gbService.updateCategory(category);
		
		return createCategoryModel(gradebook, category);
	}
	
	protected GradebookModel addGradebook(String name) {
		
		String uid = null;
		boolean isAdded = false;
		while (!isAdded) {
			uid = UUID.randomUUID().toString();
			
			try {
				frameworkService.addGradebook(uid, name);
				isAdded = true;
			} catch (GradebookExistsException gee) {
				log.warn("Unable to add gradebook with uid " + uid);
			}
		}
		
		Gradebook gradebook = gbService.getGradebook(uid);
		
		return createGradebookModel(gradebook);
	}
	
	// Helper method
	private ItemModel createItemModel(Gradebook gradebook) {
		ItemModel itemModel = new ItemModel();
		itemModel.setName(gradebook.getName());
		itemModel.setItemType(Type.GRADEBOOK);
		//itemModel.setIdentifier(new StringBuilder().append(AppConstants.GRADEBOOK).append(gradebook.getUid()).toString());
		itemModel.setIdentifier(gradebook.getUid());
		
		List<Category> categories = gbService.getCategories(gradebook.getId());
		
		if (categories != null) {
			double sum = 0d;
			for (Category category : categories) {
				double categoryWeight = category.getWeight() == null ? 0d : category.getWeight().doubleValue() * 100d;
				boolean isExtraCredit = category.isExtraCredit() != null && category.isExtraCredit().booleanValue();
				boolean isUnweighted = category.isUnweighted() != null && category.isUnweighted().booleanValue();
				
				if (!isExtraCredit && !isUnweighted)
					sum += categoryWeight;
			}
			itemModel.setPercentCourseGrade(Double.valueOf(sum));
		}
		
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
		
		return itemModel;
	}
	
	// Helper method
	private ItemModel createItemModel(Gradebook gradebook, Category category, List<Assignment> assignments) {
		ItemModel model = new ItemModel();
		
		double categoryWeight = category.getWeight() == null ? 0d : category.getWeight().doubleValue() * 100d;
		boolean isIncluded = category.isUnweighted() == null ? true : ! category.isUnweighted().booleanValue();
		
		if (! isIncluded || category.isRemoved()) 
			categoryWeight = 0d;
		
		model.setGradebook(gradebook.getName());
		//model.setIdentifier(new StringBuilder().append(AppConstants.CATEGORY).append(String.valueOf(category.getId())).toString());
		model.setIdentifier(String.valueOf(category.getId()));
		model.setName(category.getName());
		model.setCategoryId(category.getId());
		model.setWeighting(Double.valueOf(categoryWeight));
		model.setEqualWeightAssignments(category.isEqualWeightAssignments());
		model.setExtraCredit(category.isExtraCredit() == null ? Boolean.FALSE : category.isExtraCredit());
		model.setIncluded(Boolean.valueOf(isIncluded));
		model.setDropLowest(category.getDrop_lowest() == 0 ? null : Integer.valueOf(category.getDrop_lowest()));
		model.setRemoved(Boolean.valueOf(category.isRemoved()));
		model.setPercentCourseGrade(Double.valueOf(categoryWeight));
		
		if (assignments == null)
			assignments = category.getAssignmentList();
		
		double sum = 0d;
		if (assignments != null && isIncluded) {
			for (Assignment assignment : assignments) {
				double assignmentWeight = assignment.getAssignmentWeighting() == null ? 0.0 : assignment.getAssignmentWeighting().doubleValue() * 100.0;
				boolean isExtraCredit = assignment.isExtraCredit() != null && assignment.isExtraCredit().booleanValue();
				boolean isUnweighted = assignment.isUnweighted() != null && assignment.isUnweighted().booleanValue();
				if (!isExtraCredit && !isUnweighted)
					sum += assignmentWeight;
			}
		}
		model.setPercentCategory(Double.valueOf(sum));
		model.setItemType(Type.CATEGORY);
		
		return model;
	}
	
	// Helper method
	private ItemModel createItemModel(Category category, Assignment assignment, BigDecimal percentCourseGrade) {
		ItemModel model = new ItemModel();
		
		double categoryDecimalWeight = category.getWeight() == null ? 0d : category.getWeight().doubleValue();
		double assignmentWeight = assignment.getAssignmentWeighting() == null ? 0d : assignment.getAssignmentWeighting().doubleValue() * 100.0;
		Boolean isAssignmentIncluded = assignment.isUnweighted() == null ? Boolean.TRUE : Boolean.valueOf(!assignment.isUnweighted().booleanValue());
		Boolean isAssignmentExtraCredit = assignment.isExtraCredit() == null ? Boolean.FALSE : assignment.isExtraCredit();
		Boolean isAssignmentReleased = Boolean.valueOf(assignment.isReleased());
		Boolean isAssignmentRemoved = Boolean.valueOf(assignment.isRemoved());
		
		Gradebook gradebook = assignment.getGradebook();
		
		// We don't want to delete assignments based on category when we don't have categories
		if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY) {
			
			if (category.isRemoved())
				isAssignmentRemoved = Boolean.TRUE;
			
			if (category.isUnweighted() != null && category.isUnweighted().booleanValue()) 
				isAssignmentIncluded = Boolean.FALSE;
		
		}
		
		if (! isAssignmentIncluded.booleanValue() || assignment.isRemoved()) 
			assignmentWeight = 0d;
		
		String categoryName = gradebook.getName();
		if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY)
			categoryName = category.getName();
		
		model.setIdentifier(String.valueOf(assignment.getId()));
		model.setName(assignment.getName());
		model.setCategoryName(categoryName);
		model.setCategoryId(category.getId());
		model.setItemId(assignment.getId());
		model.setWeighting(Double.valueOf(assignmentWeight));
		model.setReleased(isAssignmentReleased);
		model.setIncluded(isAssignmentIncluded);
		model.setDueDate(assignment.getDueDate());
		model.setPoints(assignment.getPointsPossible());
		model.setExtraCredit(isAssignmentExtraCredit);
		model.setRemoved(isAssignmentRemoved);
		model.setSource(assignment.getExternalAppName());
		model.setDataType(AppConstants.NUMERIC_DATA_TYPE);
		model.setStudentModelKey(Key.ASSIGNMENT.name());
		
		if (percentCourseGrade == null) {
			List<Assignment> assignments = category.getAssignmentList();
			
			boolean isIncluded = category.isUnweighted() == null ? true : ! category.isUnweighted().booleanValue();
			
			double sum = 0d;
			if (assignments != null && isIncluded) {
				for (Assignment a : assignments) {
					double assignWeight = a.getAssignmentWeighting() == null ? 0.0 : a.getAssignmentWeighting().doubleValue() * 100.0;
					boolean isExtraCredit = a.isExtraCredit() != null && a.isExtraCredit().booleanValue();
					boolean isUnweighted = a.isUnweighted() != null && a.isUnweighted().booleanValue();
					if (!isExtraCredit && !isUnweighted)
						sum += assignWeight;
				}
			}
			percentCourseGrade = new BigDecimal(String.valueOf(Double.valueOf(sum)));
		}
		
		
		model.setPercentCategory(Double.valueOf(assignmentWeight));
		model.setPercentCourseGrade(Double.valueOf(percentCourseGrade.doubleValue()));
		model.setItemType(Type.ITEM);
		
		return model;
	}
	
	
	// Helper method
	private CategoryModel createCategoryModel(Gradebook gradebook, Category category) {
		CategoryModel model = new CategoryModel();
		
		double categoryWeight = category.getWeight() == null ? 0d : category.getWeight().doubleValue() * 100d;
		boolean isIncluded = category.isUnweighted() == null ? true : ! category.isUnweighted().booleanValue();
		
		if (! isIncluded || category.isRemoved()) 
			categoryWeight = 0d;
		
		model.setGradebook(gradebook.getName());
		model.setIdentifier(String.valueOf(category.getId()));
		model.setName(category.getName());
		model.setWeighting(Double.valueOf(categoryWeight));
		model.setEqualWeightAssignments(category.isEqualWeightAssignments());
		model.setExtraCredit(category.isExtraCredit());
		model.setIncluded(Boolean.valueOf(isIncluded));
		model.setDropLowest(category.getDrop_lowest() == 0 ? null : Integer.valueOf(category.getDrop_lowest()));
		model.setRemoved(Boolean.valueOf(category.isRemoved()));
		
		return model;
	}
	
	// Helper method
	public GradebookModel createGradebookModel(Gradebook gradebook) {
		GradebookModel model = new GradebookModel();
		String gradebookUid = gradebook.getUid();
		
		model.setGradebookUid(gradebookUid);
		model.setGradebookId(gradebook.getId());
		model.setName(gradebook.getName());
		
		List<Category> categoriesWithAssignments = getCategoriesWithAssignments(gradebook.getId());
		ItemModel gradebookItemModel = getItemModel(gradebook, categoriesWithAssignments);
		model.setGradebookItemModel(gradebookItemModel);
		List<ColumnModel> columns = getColumns();
		
		boolean isUserAbleToGrade = authz.isUserAbleToGrade(gradebookUid);
		boolean isUserAbleToViewOwnGrades = authz.isUserAbleToViewOwnGrades(gradebookUid);
		
		boolean isSingleUserView = isUserAbleToViewOwnGrades && !isUserAbleToGrade;
		
		model.setUserAbleToGrade(isUserAbleToGrade);
		model.setUserAbleToEditAssessments(authz.isUserAbleToEditAssessments(gradebookUid));
		model.setUserAbleToViewOwnGrades(isUserAbleToViewOwnGrades);
		model.setUserHasGraderPermissions(authz.isUserHasGraderPermissions(gradebookUid));
		
		
		if (userService != null) {
			User user = userService.getCurrentUser();
			
			if (user != null) {
				// Don't take the hit of looking this stuff up unless we're in single user view
				if (isSingleUserView) {
					
					UserRecord userRecord = new UserRecord(user);
					try {
						Site site = siteService.getSite(getSiteContext());
						Collection<Group> groups = site.getGroupsWithMember(user.getId());
						if (!groups.isEmpty()) {
							for (Group group : groups) {
								// FIXME: We probably don't just want to grab the first group the user is in
								userRecord.setSectionTitle(group.getTitle());
								break;
							}
						}
					} catch (IdUnusedException e) {
						log.error("Unable to find the current user", e);
					}
		
					model.setUserAsStudent(buildStudentRow(gradebook, userRecord, columns, gradebookItemModel, categoriesWithAssignments));
				}
				
				model.setUserName(user.getDisplayName());
			}
		} else {
			Map<String, UserRecord> userRecordMap = findStudentRecords(gradebookUid, gradebook.getId(), null);
			UserRecord userRecord = userRecordMap.values().iterator().next();
			model.setUserName(userRecord.getDisplayName());
			model.setUserAsStudent(buildStudentRow(gradebook, userRecord, columns, gradebookItemModel, categoriesWithAssignments));
		}
			
		model.setColumns(columns);
		
		switch (gradebook.getCategory_type()) {
		case GradebookService.CATEGORY_TYPE_NO_CATEGORY:
			model.setCategoryType(CategoryType.NO_CATEGORIES);
			break;
		case GradebookService.CATEGORY_TYPE_ONLY_CATEGORY:
			model.setCategoryType(CategoryType.SIMPLE_CATEGORIES);
			break;
		case GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY:
			model.setCategoryType(CategoryType.WEIGHTED_CATEGORIES);
			break;
		}
		switch (gradebook.getGrade_type()) {
		case GradebookService.GRADE_TYPE_POINTS:
			model.setGradeType(GradeType.POINTS);
			break;
		case GradebookService.GRADE_TYPE_PERCENTAGE:
			model.setGradeType(GradeType.PERCENTAGES);
			break;
		case GradebookService.GRADE_TYPE_LETTER:
			model.setGradeType(GradeType.LETTERS);
			break;
		}
		model.setReleaseGrades(Boolean.valueOf(gradebook.isCourseGradeDisplayed()));
		
		return model;
	}
	
	// Helper method
	private AssignmentModel createAssignmentModel(Category category, Assignment assignment) {
		AssignmentModel model = new AssignmentModel();
		
		double assignmentWeight = assignment.getAssignmentWeighting() == null ? 0.0 : assignment.getAssignmentWeighting().doubleValue() * 100.0;
		Boolean isAssignmentIncluded = assignment.isUnweighted() == null ? Boolean.TRUE : Boolean.valueOf(!assignment.isUnweighted().booleanValue());
		Boolean isAssignmentExtraCredit = assignment.isExtraCredit() == null ? Boolean.FALSE : assignment.isExtraCredit();
		Boolean isAssignmentReleased = Boolean.valueOf(assignment.isReleased());
		Boolean isAssignmentRemoved = Boolean.valueOf(assignment.isRemoved());
		
		Gradebook gradebook = assignment.getGradebook();
		
		// We don't want to delete assignments based on category when we don't have categories
		if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY) {
			
			if (category.isRemoved())
				isAssignmentRemoved = Boolean.TRUE;
			
			if (category.isUnweighted() != null && category.isUnweighted().booleanValue()) 
				isAssignmentIncluded = Boolean.FALSE;
		
		}
		
		if (! isAssignmentIncluded.booleanValue() || assignment.isRemoved()) 
			assignmentWeight = 0.0;
		
		String categoryName = gradebook.getName();
		if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY)
			categoryName = category.getName();
		
		model.setIdentifier(String.valueOf(assignment.getId()));
		model.setName(assignment.getName());
		model.setCategoryName(categoryName);
		model.setCategoryId(category.getId());
		model.setWeighting(Double.valueOf(assignmentWeight));
		model.setReleased(isAssignmentReleased);
		model.setIncluded(isAssignmentIncluded);
		model.setDueDate(assignment.getDueDate());
		model.setPoints(assignment.getPointsPossible());
		model.setExtraCredit(isAssignmentExtraCredit);
		model.setRemoved(isAssignmentRemoved);
		model.setSource(assignment.getExternalAppName());
		
		return model;
	}
	
	private GradeRecordModel createOrUpdateAssignmentRecordModel(Long gradebookId, GradeRecordModel model, AssignmentGradeRecord record, Category category, Assignment a, boolean hasGradingEvents) {

		if (model == null)
		{
			model = new GradeRecordModel();
		}
		
		BigDecimal categoryWeight = gradeCalculations.getCategoryWeight(category);
		Double assignmentWeight = a.getAssignmentWeighting();
		
		BigDecimal overallWeight = categoryWeight.multiply(new BigDecimal(assignmentWeight.toString())).multiply(new BigDecimal("100"));
		
		List<Comment> comments = null;
		
		boolean isDropped = false;
		boolean isExcused = false;
		if (record != null) {
			overallWeight = record.getOverallWeight();
			if (overallWeight == null)
				overallWeight = BigDecimal.ZERO;
			isExcused = record.isExcluded() != null && record.isExcluded().booleanValue();
			isDropped = record.isDropped() != null && record.isDropped().booleanValue();
			
			// FIXME: This seems highly inefficient. It would be nice to have a boolean on the actual AssignmentGradeRecord object to tell us if we
			// need to bother looking for a comment for this assignment.
			comments = gbService.getStudentAssignmentComments(record.getStudentId(), gradebookId);
		
			switch (a.getGradebook().getGrade_type()) {
			case GradebookService.GRADE_TYPE_POINTS:
				model.setPointsEarned(record.getPointsEarned());
				break;
			case GradebookService.GRADE_TYPE_PERCENTAGE:
				BigDecimal pointsEarnedAsPercent = gradeCalculations.getPointsEarnedAsPercent(a, record);
				Double percentDouble = pointsEarnedAsPercent == null ? null : Double.valueOf(pointsEarnedAsPercent.doubleValue());
				model.setPercentEarned(percentDouble);
				break;
			case GradebookService.GRADE_TYPE_LETTER:
				model.setLetterEarned(record.getLetterEarned());
				break;
			}
		}
		
		Comment comment = null;
		
		// TODO: Make sure that there is only one comment per assignment
		if (comments != null && !comments.isEmpty()) {
			for (Comment c : comments) {
				if (c.getGradableObject().getId().equals(a.getId())) {
					comment = c;
					break;
				}
			}
		}
		
		// FIXME: Need to recalculate all the others
		//if (!isExcused && categoryWeight != null && assignmentWeight != null)
		//	overallWeight = //categoryWeight.multiply(new BigDecimal(assignmentWeight.toString())).multiply(new BigDecimal("100"));
				
		model.setIdentifier(String.valueOf(a.getId()));
		model.setAssignmentId( a.getId() );

		model.setPointsPossible(a.getPointsPossible());
		model.setCategoryName(category.getName());
		model.setExcluded(Boolean.valueOf(isExcused));
		model.setAssignmentName(a.getName());
		model.setWeight(Double.valueOf(overallWeight.doubleValue()));
		model.setLog(Boolean.valueOf(hasGradingEvents));
		model.setDropped(Boolean.valueOf(isDropped));
		
		if (comment != null) {
			model.setComments(comment.getCommentText());
		}
		
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
		
		if (model == null)
		{
			model = new GradeEventModel();
		}
		
		String graderName = event.getGraderId();
		
		try {
			if (userService != null) {
				User grader = userService.getUser(event.getGraderId());
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
	
	
	// Helper method
	protected String getGradebookUid() {
		Placement placement = toolManager.getCurrentPlacement();
	    if (placement == null) {
	    	log.error("Placement is null!");
	    	return null;
	    }

	    return placement.getContext();
	}
	
	private Map<EnrollmentRecord, Map<Long, String>> getWorkingEnrollmentsForAllItems(String gradebookUid, Long gradebookId, String searchString, String selectedSectionUid) {
		Map<EnrollmentRecord, Map<Long, String>> enrollmentMap;

		String selSearchString = null;
		if (searchString != null && searchString.trim().length() > 0) {
			selSearchString = searchString;
		}
		
		String selSectionUid = null;
		if (!isAllSectionsSelected(selectedSectionUid)) {
			selSectionUid = selectedSectionUid;
		}
		
		enrollmentMap = findMatchingEnrollmentsForAllItems(gradebookUid, gradebookId, selSearchString, selSectionUid);

		return enrollmentMap;
	}
	
	private boolean isAllSectionsSelected(String selectedSectionUid) {
		// FIXME: Need to modify this to determine if all sections are selected
		return selectedSectionUid != null;
	}
	
	/*
	 * Warning: Old code from the original gradebook project slightly refactored to add generics, but
	 * still with lots of room for improvements
	 */
	private Map<EnrollmentRecord, Map<Long, String>> assignFunctionsForGradeAll(Map<EnrollmentRecord, 
			Map<Long, String>> enrollmentMap, List<Assignment> assignments, 
			Map<String, EnrollmentRecord> studentIdEnrRecMap) {
		
		
		// First of all, we need to pull out the collection of enrollment records
		Collection<EnrollmentRecord> enrollments = studentIdEnrRecMap.values();
		
		// FIXME : This seems highly inefficient in terms of space -- why not use the flag? 
		// Now we need to generate the map of assignment functions . . . of course, for grade all 
		// it's the full list
		Map<Long, String> assignFunctionMap = new HashMap<Long, String>();
		if (assignments != null && !assignments.isEmpty()) {
			for (Assignment assignment : assignments) {
				if (assignment.getId() != null)
					assignFunctionMap.put(assignment.getId(), GradebookService.gradePermission);
			}
		}
		
		// Okay, so every enrollment record now gets the full map. Again, expensive solution to the problem.
		for (EnrollmentRecord enr : enrollments) {
			enrollmentMap.put(enr, assignFunctionMap);
		}
		
		return enrollmentMap;
	}
	
	private Map<EnrollmentRecord, Map<Long, String>> assignFunctionsBySpecialGraderPermissions(
			Map<EnrollmentRecord, Map<Long, String>> enrollmentMap) {
		// user has special grader permissions that override default perms
		
		/*
		List myStudentIds = new ArrayList(studentIdEnrRecMap.keySet());
		
		List selSections = new ArrayList();
		if (optionalSectionUid == null) {  
			// pass all sections
			selSections = new ArrayList(sectionIdCourseSectionMap.values());
		} else {
			// only pass the selected section
			CourseSection section = (CourseSection) sectionIdCourseSectionMap.get(optionalSectionUid);
			if (section != null)
				selSections.add(section);
		}
		
		// we need to get the viewable students, so first create section id --> student ids map
		myStudentIds = getGradebookPermissionService().getViewableStudentsForUser(gradebookUid, userId, myStudentIds, selSections);
		Map viewableStudentIdItemsMap = new HashMap();
		if (allGbItems == null || allGbItems.isEmpty()) {
			if (myStudentIds != null) {
				for (Iterator stIter = myStudentIds.iterator(); stIter.hasNext();) {
					String stId = (String) stIter.next();
					if (stId != null)
						viewableStudentIdItemsMap.put(stId, null);
				}
			}
		} else {
			viewableStudentIdItemsMap = gradebookPermissionService.getAvailableItemsForStudents(gradebookUid, userId, myStudentIds, selSections);
		}
		
		if (!viewableStudentIdItemsMap.isEmpty()) {
			for (Iterator enrIter = viewableStudentIdItemsMap.keySet().iterator(); enrIter.hasNext();) {
				String studentId = (String) enrIter.next();
				EnrollmentRecord enrRec = (EnrollmentRecord)studentIdEnrRecMap.get(studentId);
				if (enrRec != null) {	
					Map itemIdFunctionMap = (Map)viewableStudentIdItemsMap.get(studentId);
					//if (!itemIdFunctionMap.isEmpty()) {
						enrollmentMap.put(enrRec, itemIdFunctionMap);
					//}
				}
			}
		}
		*/
		
		log.warn("Special grader permissions are not implemented yet!");
		
		return enrollmentMap;
	}
	
	/*
	 * Old code from the original gradebook project, refactored
	 */
	private Map<EnrollmentRecord, Map<Long, String>> assignFunctionsByDefaultSections(Map<EnrollmentRecord, 
			Map<Long, String>> enrollmentMap, List<Assignment> assignments, 
			Map<String, EnrollmentRecord> studentIdEnrRecMap, 
			Map<String, CourseSection> sectionIdCourseSectionMap, 
			String optionalSectionUid) {
	
		// use default section-based permissions
		
		// Determine the current user's section memberships
		List<String> availableSections = new ArrayList<String>();
		if (optionalSectionUid != null && isUserTAinSection(optionalSectionUid)) {
			if (sectionIdCourseSectionMap.containsKey(optionalSectionUid))
				availableSections.add(optionalSectionUid);
		} else {
			for (String sectionUuid : sectionIdCourseSectionMap.keySet()) {
				if (isUserTAinSection(sectionUuid)) {
					availableSections.add(sectionUuid);
				}
			}
		}
		
		// Determine which enrollees are in these sections
		Map<String, EnrollmentRecord> uniqueEnrollees = new HashMap<String, EnrollmentRecord>();
		for (String sectionUuid : availableSections) {
			List<EnrollmentRecord> sectionEnrollments = sectionAwareness.getSectionMembersInRole(sectionUuid, Role.STUDENT);
			for (EnrollmentRecord enr : sectionEnrollments) {
				uniqueEnrollees.put(enr.getUser().getUserUid(), enr);
			}
		}
		
		// Filter out based upon the original filtered students
		for (String enrId : studentIdEnrRecMap.keySet()) {
			if (uniqueEnrollees.containsKey(enrId)) {
				// iterate through the assignments
				Map<Long, String> itemFunctionMap = new HashMap<Long, String>();
				if (assignments != null && !assignments.isEmpty()) {
					for (Assignment assign : assignments) {
					
						if (assign.getId() != null) {
							itemFunctionMap.put(assign.getId(), GradebookService.gradePermission);
						}
					}
				}
				enrollmentMap.put(studentIdEnrRecMap.get(enrId), itemFunctionMap);
			}
		}
		
		return enrollmentMap;
	}
	
	/*
	 * Old code from the original gradebook project, refactored
	 */
	private Map<EnrollmentRecord, Map<Long, String>> assignFunctionsBySection(Map<EnrollmentRecord, 
			Map<Long, String>> enrollmentMap, List<Assignment> assignments, 
			Map<String, EnrollmentRecord> studentIdEnrRecMap, String gradebookUid, Long gradebookId,
			String optionalSectionUid) {
		
		Map<String, CourseSection> sectionIdCourseSectionMap = new HashMap<String, CourseSection>();
		List<CourseSection> viewableSections = getViewableSections(gradebookUid, gradebookId);
		for (CourseSection section : viewableSections) {
			sectionIdCourseSectionMap.put(section.getUuid(), section);
		}
		
		if (isUserHasGraderPermissions(gradebookId)) {
			enrollmentMap = assignFunctionsBySpecialGraderPermissions(enrollmentMap);
		} else {
			enrollmentMap = assignFunctionsByDefaultSections(enrollmentMap, assignments, studentIdEnrRecMap,
					sectionIdCourseSectionMap, optionalSectionUid);
		}
		
		return enrollmentMap;
	}
	
	
	/*
	 * Old code from authz, refactored slightly
	 */
	private Map<EnrollmentRecord, Map<Long, String>> findMatchingEnrollmentsForAllItems(String gradebookUid, Long gradebookId, String optionalSearchString, String optionalSectionUid) {
		//return authz.findMatchingEnrollmentsForViewableItems(gradebookUid,
		//		gradebookManager.getAssignments(gradebookId), optionalSearchString, optionalSectionUid);
	
		List<Assignment> assignments = gbService.getAssignments(gradebookId);
		
		Map<EnrollmentRecord, Map<Long, String>> enrollmentMap = new HashMap<EnrollmentRecord, Map<Long, String>>();
		List<EnrollmentRecord> filteredEnrollments = new ArrayList<EnrollmentRecord>();
		if (optionalSearchString != null)
			filteredEnrollments = sectionAwareness.findSiteMembersInRole(gradebookUid, Role.STUDENT, optionalSearchString);
		else
			filteredEnrollments = sectionAwareness.getSiteMembersInRole(gradebookUid, Role.STUDENT);
		
		if (filteredEnrollments.isEmpty()) 
			return enrollmentMap;
		
		// get all the students in the filtered section, if appropriate
		Map<String, EnrollmentRecord> studentsInSectionMap = new HashMap<String, EnrollmentRecord>();
		if (optionalSectionUid !=  null) {
			List<EnrollmentRecord> sectionMembers = sectionAwareness.getSectionMembersInRole(optionalSectionUid, Role.STUDENT);
			if (!sectionMembers.isEmpty()) {
				for(EnrollmentRecord member : sectionMembers) {
					studentsInSectionMap.put(member.getUser().getUserUid(), member);
				}
			}
		}
		
		Map<String, EnrollmentRecord> studentIdEnrRecMap = new HashMap<String, EnrollmentRecord>();
		for (EnrollmentRecord enr : filteredEnrollments) {
			String studentId = enr.getUser().getUserUid();
			
			EnrollmentRecord sub = (EnrollmentRecord)studentsInSectionMap.get(studentId);
			if (sub == null)
				sub = enr;
			if (optionalSectionUid != null) {
				if (studentsInSectionMap.containsKey(studentId)) {
					studentIdEnrRecMap.put(studentId, sub);
				}
			} else {
				studentIdEnrRecMap.put(studentId, sub);
			}
		}			
			
		if (isUserAbleToGradeAll(gradebookUid)) {
			enrollmentMap = assignFunctionsForGradeAll(enrollmentMap, assignments, studentIdEnrRecMap);
		} else {
			enrollmentMap = assignFunctionsBySection(enrollmentMap, assignments, studentIdEnrRecMap, 
					gradebookUid, gradebookId, optionalSectionUid);
		}

		return enrollmentMap;
	}
	
	/*
	public Map findMatchingEnrollmentsForViewableItems(String gradebookUid, Long gradebookId, List allGbItems, String optionalSearchString, String optionalSectionUid) {
		Map enrollmentMap = new HashMap();
		List filteredEnrollments = new ArrayList();
		if (optionalSearchString != null)
			filteredEnrollments = getSectionAwareness().findSiteMembersInRole(gradebookUid, Role.STUDENT, optionalSearchString);
		else
			filteredEnrollments = getSectionAwareness().getSiteMembersInRole(gradebookUid, Role.STUDENT);
		
		if (filteredEnrollments.isEmpty()) 
			return enrollmentMap;
		
		// get all the students in the filtered section, if appropriate
		Map studentsInSectionMap = new HashMap();
		if (optionalSectionUid !=  null) {
			List sectionMembers = sectionAwareness.getSectionMembersInRole(optionalSectionUid, Role.STUDENT);
			if (!sectionMembers.isEmpty()) {
				for(Iterator memberIter = sectionMembers.iterator(); memberIter.hasNext();) {
					EnrollmentRecord member = (EnrollmentRecord) memberIter.next();
					studentsInSectionMap.put(member.getUser().getUserUid(), member);
				}
			}
		}
		
		Map studentIdEnrRecMap = new HashMap();
		for (Iterator enrIter = filteredEnrollments.iterator(); enrIter.hasNext();) {
			EnrollmentRecord enr = (EnrollmentRecord) enrIter.next();
			String studentId = enr.getUser().getUserUid();
			
			EnrollmentRecord sub = (EnrollmentRecord)studentsInSectionMap.get(studentId);
			if (sub == null)
				sub = enr;
			if (optionalSectionUid != null) {
				if (studentsInSectionMap.containsKey(studentId)) {
					studentIdEnrRecMap.put(studentId, sub);
				}
			} else {
				studentIdEnrRecMap.put(studentId, sub);
			}
		}			
			
		if (isUserAbleToGradeAll(gradebookUid)) {
			List enrollments = new ArrayList(studentIdEnrRecMap.values());
			
			HashMap assignFunctionMap = new HashMap();
			if (allGbItems != null && !allGbItems.isEmpty()) {
				for (Iterator assignIter = allGbItems.iterator(); assignIter.hasNext();) {
					Object assign = assignIter.next();
					Long assignId = null;
					if (assign instanceof org.sakaiproject.service.gradebook.shared.Assignment) {
						assignId = ((org.sakaiproject.service.gradebook.shared.Assignment)assign).getId();
					} else if (assign instanceof org.sakaiproject.tool.gradebook.Assignment) {
						assignId = ((org.sakaiproject.tool.gradebook.Assignment)assign).getId();
					}

					if (assignId != null)
						assignFunctionMap.put(assignId, GradebookService.gradePermission);
				}
			}
			
			for (Iterator enrIter = enrollments.iterator(); enrIter.hasNext();) {
				EnrollmentRecord enr = (EnrollmentRecord) enrIter.next();
				enrollmentMap.put(enr, assignFunctionMap);
			}
			
		} else {
			String userId = authn.getUserUid();
			
			Map sectionIdCourseSectionMap = new HashMap();
			List viewableSections = authz.getViewableSections(gradebookUid);
			for (Iterator sectionIter = viewableSections.iterator(); sectionIter.hasNext();) {
				CourseSection section = (CourseSection) sectionIter.next();
				sectionIdCourseSectionMap.put(section.getUuid(), section);
			}
			
			if (authz.isUserHasGraderPermissions(gradebookUid)) {
				// user has special grader permissions that override default perms
				
				List myStudentIds = new ArrayList(studentIdEnrRecMap.keySet());
				
				List selSections = new ArrayList();
				if (optionalSectionUid == null) {  
					// pass all sections
					selSections = new ArrayList(sectionIdCourseSectionMap.values());
				} else {
					// only pass the selected section
					CourseSection section = (CourseSection) sectionIdCourseSectionMap.get(optionalSectionUid);
					if (section != null)
						selSections.add(section);
				}
				
				// we need to get the viewable students, so first create section id --> student ids map
				myStudentIds = getViewableStudentsForUser(gradebookId, userId, myStudentIds, selSections);
				Map viewableStudentIdItemsMap = new HashMap();
				if (allGbItems == null || allGbItems.isEmpty()) {
					if (myStudentIds != null) {
						for (Iterator stIter = myStudentIds.iterator(); stIter.hasNext();) {
							String stId = (String) stIter.next();
							if (stId != null)
								viewableStudentIdItemsMap.put(stId, null);
						}
					}
				} else {
					viewableStudentIdItemsMap = permissionService.getAvailableItemsForStudents(gradebookId, userId, myStudentIds, selSections);
				}
				
				if (!viewableStudentIdItemsMap.isEmpty()) {
					for (Iterator enrIter = viewableStudentIdItemsMap.keySet().iterator(); enrIter.hasNext();) {
						String studentId = (String) enrIter.next();
						EnrollmentRecord enrRec = (EnrollmentRecord)studentIdEnrRecMap.get(studentId);
						if (enrRec != null) {	
							Map itemIdFunctionMap = (Map)viewableStudentIdItemsMap.get(studentId);
							//if (!itemIdFunctionMap.isEmpty()) {
								enrollmentMap.put(enrRec, itemIdFunctionMap);
							//}
						}
					}
				}

			} else { 
				// use default section-based permissions
				
				// Determine the current user's section memberships
				List availableSections = new ArrayList();
				if (optionalSectionUid != null && isUserTAinSection(optionalSectionUid)) {
					if (sectionIdCourseSectionMap.containsKey(optionalSectionUid))
						availableSections.add(optionalSectionUid);
				} else {
					for (Iterator iter = sectionIdCourseSectionMap.keySet().iterator(); iter.hasNext(); ) {
						String sectionUuid = (String)iter.next();
						if (isUserTAinSection(sectionUuid)) {
							availableSections.add(sectionUuid);
						}
					}
				}
				
				// Determine which enrollees are in these sections
				Map uniqueEnrollees = new HashMap();
				for (Iterator iter = availableSections.iterator(); iter.hasNext(); ) {
					String sectionUuid = (String)iter.next();
					List sectionEnrollments = sectionAwareness.getSectionMembersInRole(sectionUuid, Role.STUDENT);
					for (Iterator eIter = sectionEnrollments.iterator(); eIter.hasNext(); ) {
						EnrollmentRecord enr = (EnrollmentRecord)eIter.next();
						uniqueEnrollees.put(enr.getUser().getUserUid(), enr);
					}
				}
				
				// Filter out based upon the original filtered students
				for (Iterator iter = studentIdEnrRecMap.keySet().iterator(); iter.hasNext(); ) {
					String enrId = (String)iter.next();
					if (uniqueEnrollees.containsKey(enrId)) {
						// iterate through the assignments
						Map itemFunctionMap = new HashMap();
						if (allGbItems != null && !allGbItems.isEmpty()) {
							for (Iterator itemIter = allGbItems.iterator(); itemIter.hasNext();) {
								Object assign = itemIter.next();
								Long assignId = null;
								if (assign instanceof org.sakaiproject.service.gradebook.shared.Assignment) {
									assignId = ((org.sakaiproject.service.gradebook.shared.Assignment)assign).getId();
								} else if (assign instanceof org.sakaiproject.tool.gradebook.Assignment) {
									assignId = ((org.sakaiproject.tool.gradebook.Assignment)assign).getId();
								}

								if (assignId != null) {
									itemFunctionMap.put(assignId, GradebookService.gradePermission);
								}
							}
						}
						enrollmentMap.put(studentIdEnrRecMap.get(enrId), itemFunctionMap);
					}
				}
			}
		}

		return enrollmentMap;
	}*/
	
	private List getViewableStudentsForUser(Long gradebookId, String userId, List studentIds, List sections) {
		if(gradebookId == null || userId == null)
			throw new IllegalArgumentException("Null parameter(s) in GradebookPermissionServiceImpl.getAvailableItemsForStudent");
		
		List viewableStudents = new ArrayList();
		
		if (studentIds == null || studentIds.isEmpty())
			return viewableStudents;
		
		
		List permsForAnyGroup = gbService.getPermissionsForUserAnyGroup(gradebookId, userId);
		if (!permsForAnyGroup.isEmpty()) {
			return studentIds;
		}
		
		Map sectionIdStudentIdsMap = getSectionIdStudentIdsMap(sections, studentIds);
		
		if (sectionIdStudentIdsMap.isEmpty()) {
			return null;
		}
		
		// use a map to make sure the student ids are unique
		Map studentMap = new HashMap();
		
		// Next, check for permissions for specific sections
		List groupIds = new ArrayList(sectionIdStudentIdsMap.keySet());
		List permsForGroupsAnyCategory = gbService.getPermissionsForUserForGroup(gradebookId, userId, groupIds);
		
		if (permsForGroupsAnyCategory.isEmpty()) {
			return viewableStudents;
		}
		
		List studentMembers = new ArrayList();
		for (Iterator permsIter = permsForGroupsAnyCategory.iterator(); permsIter.hasNext();) {
			Permission perm = (Permission) permsIter.next();
			String groupId = perm.getGroupId();
			if (groupId != null) {
				List sectionStudentIds = (ArrayList)sectionIdStudentIdsMap.get(groupId);
				if (sectionStudentIds != null && !sectionStudentIds.isEmpty()) {
					for (Iterator studentIter = sectionStudentIds.iterator(); studentIter.hasNext();) {
						String studentId = (String) studentIter.next();
						studentMap.put(studentId, null);
					}
				}
			}
		}
		
		return new ArrayList(studentMap.keySet());
	}
	
	private Map getSectionIdStudentIdsMap(Collection courseSections, Collection studentIds) {
		Map sectionIdStudentIdsMap = new HashMap();
		if (courseSections != null) {
			for (Iterator sectionIter = courseSections.iterator(); sectionIter.hasNext();) {
				CourseSection section = (CourseSection)sectionIter.next();
				if (section != null) {
					String sectionId = section.getUuid();
					List members = sectionAwareness.getSectionMembersInRole(sectionId, Role.STUDENT);
					List sectionMembersFiltered = new ArrayList();
					if (!members.isEmpty()) {
						for (Iterator memberIter = members.iterator(); memberIter.hasNext();) {
							EnrollmentRecord enr = (EnrollmentRecord) memberIter.next();
							String studentId = enr.getUser().getUserUid();
							if (studentIds.contains(studentId))
								sectionMembersFiltered.add(studentId);
						}
					}
					sectionIdStudentIdsMap.put(sectionId, sectionMembersFiltered);
				}
			}
		}
		return sectionIdStudentIdsMap;
	}
	
	
	protected Map<String, UserRecord> findStudentRecords(String gradebookUid, Long gradebookId, String optionalSectionUid) {
		Map<String, UserRecord> studentRecords = new HashMap<String, UserRecord>();
		
		boolean canGradeAll = isUserAbleToGradeAll(gradebookUid);
		
		if (canGradeAll && optionalSectionUid == null) {
			// If so, then grab all the members for the site
			String context = getSiteContext();
			Site site = null;
			try {
				site = siteService.getSite(context);
			
				Set<Member> members = site.getMembers();
				for (Member member : members) {
					if (member.getRole().getId().equals("Student") && member.isActive()) {
						String userUid = member.getUserId();
						studentRecords.put(userUid, new UserRecord(userUid));
					}
				}
			} catch (IdUnusedException idue) {
				log.error("Unable to find a site for this gradebook", idue);
			}
		} 
		
		List<CourseSection> viewableSections = getViewableSections(gradebookUid, gradebookId);
		
		for (CourseSection section : viewableSections) {
			Group group = siteService.findGroup(section.getUuid());
			Set<Member> members = group.getMembers();
			for (Member member : members) {
				if (member.getRole().getId().equals("Student") && member.isActive()) {
					// Filter by section, if such a filter exists
					if (optionalSectionUid == null || section.getUuid().equals(optionalSectionUid)) {
						String userUid = member.getUserId();
						UserRecord userRecord = studentRecords.get(userUid);
						
						if (userRecord == null) {
							userRecord = new UserRecord(userUid);
							studentRecords.put(userUid, userRecord);
						}
						
						userRecord.setSectionTitle(section.getTitle());
						
						// GRBK-37
						userRecord.setExportCourseManagemntId(exportAdvisor.getExportCourseManagemntId(member.getUserEid(), group.getProviderGroupId()));
						userRecord.setExportUserId(exportAdvisor.getExportUserId(member.getUserEid()));
					}
				}
			}
		}
		
		return studentRecords;
	}
	
	
	// New helper method to replace old code in getWorkingEnrollments
	private Map<String, EnrollmentRecord> findEnrollmentRecords(String gradebookUid, Long gradebookId, String optionalSearchString, String optionalSectionUid) {
		// FIXME: Strategy here is copied from old code -- that is, get all the site members (regardless of groups), then replace those that have
		// a group. What would potentially be more efficient would be to get all the group members, then call sectionAwareness.getUnassignedMembersInRole
		/*List<EnrollmentRecord> filteredEnrollments = new ArrayList<EnrollmentRecord>();
		if (optionalSearchString != null)
			filteredEnrollments = sectionAwareness.findSiteMembersInRole(gradebookUid, Role.STUDENT, optionalSearchString);
		else
			filteredEnrollments = sectionAwareness.getSiteMembersInRole(gradebookUid, Role.STUDENT);
		
		
		Map<String, EnrollmentRecord> enrollMap = new HashMap<String, EnrollmentRecord>();
		for (EnrollmentRecord record : filteredEnrollments) {
			enrollMap.put(record.getUser().getUserUid(), record);
		}*/
		
		if (optionalSearchString != null)
			optionalSearchString = optionalSearchString.toUpperCase();
		
		Map<String, EnrollmentRecord> enrollMap = new HashMap<String, EnrollmentRecord>();
		
		// Start by getting a list of sections visible to the current user
		List<CourseSection> viewableSections = getViewableSections(gradebookUid, gradebookId);
		
		for (CourseSection section : viewableSections) {
			List<EnrollmentRecord> sectionMembers = sectionAwareness.getSectionMembersInRole(section.getUuid(), Role.STUDENT);
			
			// This filters by the passed sectionUid if it's not null
			if (optionalSectionUid == null || optionalSectionUid.equals(section.getUuid())) {
				for (EnrollmentRecord sectionMember : sectionMembers) {
					String sectionMemberUid = sectionMember.getUser().getUserUid();
						
					if (!enrollMap.containsKey(sectionMemberUid)) {
						// Only bother to search if we haven't already searched on this item
						if (optionalSearchString != null) {
							String displayName = sectionMember.getUser().getDisplayName().toUpperCase();
							if (displayName.contains(optionalSearchString))
								enrollMap.put(sectionMemberUid, sectionMember);
						} else {
							// We're not searching
							enrollMap.put(sectionMemberUid, sectionMember);
						}
					}
				}
			}
		}
		
		// If the user can grade everybody, then include the non-section members
		if (isUserAbleToGradeAll(gradebookUid)) {
			List<EnrollmentRecord> unassignedMembers = sectionAwareness.getUnassignedMembersInRole(gradebookUid, Role.STUDENT);
			if (unassignedMembers != null) {
				for (EnrollmentRecord nonMember : unassignedMembers) {
					String nonMemberUid = nonMember.getUser().getUserUid();
					
					if (!enrollMap.containsKey(nonMemberUid)) {
						if (optionalSearchString != null) {
							String displayName = nonMember.getUser().getDisplayName().toUpperCase();
							if (displayName.contains(optionalSearchString))
								enrollMap.put(nonMemberUid, nonMember);
						} else {
							// We're not searching
							enrollMap.put(nonMemberUid, nonMember);
						}
					}
				}
		
			}
		}		
		
		return enrollMap;
	}
	
	
	// Low-level helper methods
	
	private String convertString(Object value) {
		return value == null ? "" : (String)value;
	}
	
	private Date convertDate(Object value) {
		return value == null ? null : (Date)value;
	}
	
	private Double convertDouble(Object value) {
		return value == null ? Double.valueOf(0.0) : (Double)value;
	}
	
	private Boolean convertBoolean(Object value) {
		return value == null ? Boolean.FALSE : (Boolean)value;
	}
	
	public Integer convertInteger(Object value) {
		return value == null ? Integer.valueOf(0) : (Integer)value;
	}

	
	// Dependency injection accessors

	public SectionAwareness getSectionAwareness() {
		return sectionAwareness;
	}

	public void setSectionAwareness(SectionAwareness sectionAwareness) {
		this.sectionAwareness = sectionAwareness;
	}

	public Authz getAuthz() {
		return authz;
	}

	public void setAuthz(Authz authz) {
		this.authz = authz;
	}

	public Authn getAuthn() {
		return authn;
	}

	public void setAuthn(Authn authn) {
		this.authn = authn;
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

	public GradeCalculations getGradeCalculations() {
		return gradeCalculations;
	}

	public void setGradeCalculations(GradeCalculations gradeCalculations) {
		this.gradeCalculations = gradeCalculations;
	}

	public GradebookFrameworkService getFrameworkService() {
		return frameworkService;
	}

	public void setFrameworkService(GradebookFrameworkService frameworkService) {
		this.frameworkService = frameworkService;
	}

	public SiteService getSiteService() {
		return siteService;
	}

	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

	public GradebookToolService getGbService() {
		return gbService;
	}

	public void setGbService(GradebookToolService gbService) {
		this.gbService = gbService;
	}

	public void setExportAdvisor(ExportAdvisor exportAdvisor) {
		this.exportAdvisor = exportAdvisor;
	}
}
