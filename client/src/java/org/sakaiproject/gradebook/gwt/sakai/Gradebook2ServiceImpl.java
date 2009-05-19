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
import org.sakaiproject.authz.api.Member;
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
import org.sakaiproject.gradebook.gwt.client.model.CommentModel;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumnModel;
import org.sakaiproject.gradebook.gwt.client.model.GradeEventModel;
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
import org.sakaiproject.gradebook.gwt.sakai.InstitutionalAdvisor.Column;
import org.sakaiproject.gradebook.gwt.sakai.model.ActionRecord;
import org.sakaiproject.gradebook.gwt.sakai.model.UserDereference;
import org.sakaiproject.gradebook.gwt.sakai.model.UserDereferenceRealmUpdate;
import org.sakaiproject.gradebook.gwt.server.DataTypeConversionUtil;
import org.sakaiproject.section.api.coursemanagement.CourseSection;
import org.sakaiproject.section.api.coursemanagement.EnrollmentRecord;
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

public class Gradebook2ServiceImpl implements Gradebook2Service {

	
	private static final Log log = LogFactory.getLog(Gradebook2ServiceImpl.class);
	
	private BusinessLogic businessLogic;
	private GradebookFrameworkService frameworkService;
	private GradebookToolService gbService;
	private GradeCalculations gradeCalculations;
	private Gradebook2Security security;
	private InstitutionalAdvisor advisor;
	private SiteService siteService;
	private ToolManager toolManager;
	private UserDirectoryService userService;
	
	
	/**
	 * Method to add a new grade item to the gradebook. 
	 * 
	 * Business rules:
	 * 	(1) If points is null, set points to 100
	 *  (2) If weight is null, set weight to be equivalent to points value -- needs to happen after #1
	 *   	
	 *  - When category type is "No Categories":
	 * 		(3) new item name must not duplicate an active (removed = false) item name in gradebook, otherwise throw exception (NoDuplicateItemNamesRule)
	 * 		
	 * 	- When category type is "Categories" or "Weighted Categories"
	 * 		(4) new item name must not duplicate an active (removed = false) item name in the same category, otherwise throw exception
	 * 		(5) if item is "included" and category has "equal weighting" then recalculate all item weights for this category
	 * 		(6) item must include a valid category id
	 * 
	 * @param gradebookUid
	 * @param gradebookId
	 * @param item
	 * @return ItemModel representing either (a) the gradebook, (b) the item's category, or (c) the item
	 * @throws InvalidInputException
	 */
	public ItemModel createItem(String gradebookUid, Long gradebookId, final ItemModel item, boolean enforceNoNewCategories) throws InvalidInputException {
		
		if (item.getItemType() != null) {
			switch (item.getItemType()) {
			case CATEGORY:
				return addItemCategory(gradebookUid, gradebookId, item);
			}
		}
		
		ActionRecord actionRecord = new ActionRecord(gradebookUid, gradebookId, EntityType.ITEM.name(), ActionType.CREATE.name());
		actionRecord.setEntityName(item.getName());
		Map<String, String> propertyMap = actionRecord.getPropertyMap();
		
		for (String property : item.getPropertyNames()) {
			String value = String.valueOf(item.get(property));
			if (value != null)
				propertyMap.put(property, value);
		}
		
		Gradebook gradebook = null;
		Category category = null;
		Long assignmentId = null;
		
		boolean hasCategories = false;
		List<Assignment> assignments = null;
		
		try {
			boolean includeInGrade = DataTypeConversionUtil.checkBoolean(item.getIncluded());
			
			Long categoryId = item.getCategoryId(); 
			String name = item.getName();
			Double weight = item.getPercentCategory(); 
			Double points = item.getPoints();
			Boolean isReleased = Boolean.valueOf(DataTypeConversionUtil.checkBoolean(item.getReleased()));
			Boolean isIncluded = Boolean.valueOf(includeInGrade);
			Boolean isExtraCredit = Boolean.valueOf(DataTypeConversionUtil.checkBoolean(item.getExtraCredit()));
			Date dueDate = item.getDueDate();
	
			// Business rule #1
			if (points == null)
				points = new Double(100d);
			// Business rule #2
			if (weight == null)
				weight = Double.valueOf(points.doubleValue());
			
			if (categoryId == null) {
				category = findDefaultCategory(gradebookId);
				categoryId = category.getId();
				
				if (category.isRemoved()) {
					category.setRemoved(false);
					gbService.updateCategory(category);
				}
				
			}
			
			if (category == null)
				category = gbService.getCategory(categoryId);
			
			
			gradebook = category.getGradebook();
	
			hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;

			
			// Apply business rules before item creation
			if (hasCategories) {
				assignments = gbService.getAssignmentsForCategory(categoryId);
				// Business rule #4
				businessLogic.applyNoDuplicateItemNamesWithinCategoryRule(categoryId, name, null, assignments);
				// Business rule #6
				if (enforceNoNewCategories)
					businessLogic.applyMustIncludeCategoryRule(item.getCategoryId());
			} else {
				assignments = gbService.getAssignments(gradebookId);
				businessLogic.applyNoDuplicateItemNamesRule(gradebook.getId(), name, null, assignments);
			}
			
			if (assignments == null || assignments.isEmpty())
				weight = new Double(100d);
	
			double w = weight == null ? 0d : ((Double)weight).doubleValue() * 0.01;
			
			assignmentId = gbService.createAssignmentForCategory(gradebookId, categoryId, name, points, Double.valueOf(w), dueDate, 
					Boolean.valueOf(!DataTypeConversionUtil.checkBoolean(isIncluded)), isExtraCredit, Boolean.FALSE, isReleased);
			
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
		
		if (! hasCategories) {
			return getItemModel(gradebook, assignments, assignmentId);
		}
		
		ItemModel categoryItemModel = getItemModelsForCategory(category, createItemModel(gradebook), assignmentId);
		
		/*String assignmentIdAsString = String.valueOf(assignmentId);
		for (ModelData model : categoryItemModel.getChildren()) {
			ItemModel itemModel = (ItemModel)model;
			if (itemModel.getIdentifier().equals(assignmentIdAsString)) 
				itemModel.setActive(true);
		}*/
		
		return categoryItemModel;
	}
	
	
	/**
	 * Method to add a new category to a gradebook
	 * 
	 * Business rules:
	 *  (1) if no other categories exist, then make the category weight 100%
	 * 	(2) new category name must not duplicate an existing category name
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
		
		Category category = null;
		
		try {
			String name = item.getName();
			Double weight = item.getPercentCourseGrade();
			Boolean isEqualWeighting = item.getEqualWeightAssignments();
			Boolean isIncluded = item.getIncluded();
			Integer dropLowest = item.getDropLowest();
			Boolean isExtraCredit = item.getExtraCredit();
			
			boolean isUnweighted = !DataTypeConversionUtil.checkBoolean(isIncluded);
			
			Gradebook gradebook = gbService.getGradebook(gradebookUid);
			List<Category> categories = gbService.getCategories(gradebook.getId()); //getCategoriesWithAssignments(gradebook.getId());
			// Business rule #1
			// FIXME: Does not take into account removed categories
			if (!isUnweighted && (categories == null || categories.isEmpty()) && weight == null)
				weight = Double.valueOf(100d);
				
			double w = weight == null ? 0d : ((Double)weight).doubleValue() * 0.01;
			
			
			boolean hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;
			
			
			//businessLogic.applyRulesBeforeAddingCategory(hasCategories, gradebook.getId(), item.getName(), categories, dropLowest, isEqualWeighting);
			
			if (hasCategories) {
				int dropLowestInt = dropLowest == null ? 0 : dropLowest.intValue();
				boolean equalWeighting = isEqualWeighting == null ? false : isEqualWeighting.booleanValue();
				
				businessLogic.applyNoDuplicateCategoryNamesRule(gradebook.getId(), item.getName(), null, categories);		
				businessLogic.applyOnlyEqualWeightDropLowestRule(dropLowestInt, equalWeighting);
			}
			
			Long categoryId = gbService.createCategory(gradebookId, name, Double.valueOf(w), dropLowest, isEqualWeighting, Boolean.valueOf(isUnweighted), isExtraCredit);
			category = gbService.getCategory(categoryId);
			
			
		} catch (RuntimeException e) {
			actionRecord.setStatus(ActionRecord.STATUS_FAILURE);
			throw e;
		} finally {
			gbService.storeActionRecord(actionRecord);
		}
		
		
		ItemModel categoryItemModel = getItemModelsForCategory(category, createItemModel(category.getGradebook()), null);
		categoryItemModel.setActive(true);
		return categoryItemModel;
	}
	
	
	public CommentModel createOrUpdateComment(Long assignmentId, String studentUid, String text) {
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
	
	
	public SpreadsheetModel createOrUpdateSpreadsheet(String gradebookUid, SpreadsheetModel spreadsheetModel) throws InvalidInputException {
		
		Gradebook gradebook = gbService.getGradebook(gradebookUid);
		Map<String, Assignment> idToAssignmentMap = new HashMap<String, Assignment>();
		List<ItemModel> headers = spreadsheetModel.getHeaders();
		
		if (headers != null) {
			for (ItemModel item : headers) {
				String id = item.getIdentifier();
				if (id != null) { 
					if (id.startsWith("NEW:")) {
						Long categoryId = item.getCategoryId();
						String name = item.getName();
						Double weight = null;
						Double points = item.getPoints();
						Date dueDate = null;
						ItemModel itemModel = new ItemModel();
						itemModel.setCategoryId(categoryId);
						itemModel.setName(name);
						itemModel.setPercentCategory(weight);
						itemModel.setPoints(points);
						ItemModel model = createItem(gradebookUid, gradebook.getId(), itemModel, false);
						//AssignmentModel model = addAssignment(gradebookUid, gradebook.getId(), categoryId, name, weight, points, dueDate);
						
						for (ItemModel child : model.getChildren()) {
							if (child.isActive()) {
								Assignment assignment = gbService.getAssignment(Long.valueOf(child.getIdentifier()));
								idToAssignmentMap.put(id, assignment);
								item.setIdentifier(child.getIdentifier());
								break;
							}
						}
						
						
					} else {
						Assignment assignment = gbService.getAssignment(Long.valueOf(id));
						idToAssignmentMap.put(id, assignment);
					}
				}
			}
		}
		
		Long gradebookId = gradebook.getId();
		
		Site site = getSite();		
		Map<String, UserRecord> userRecordMap = new HashMap<String, UserRecord>();
		
		String[] learnerRoleNames = advisor.getLearnerRoleNames();
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
		
		// Since we index the new items by a phony id e.g. "NEW:123", we need to use this set to iterate
		Set<String> idKeySet = idToAssignmentMap.keySet();
		if (idKeySet != null) {
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
				
				/*if (userRecord == null) {
					builder.append("User not found!");
					results.add(builder.toString());
					continue;
				}*/
					
				
				Map<Long, AssignmentGradeRecord> gradeRecordMap = userRecord == null ? null : userRecord.getGradeRecordMap();
				
				for (String id : idKeySet) {
					Assignment assignment = idToAssignmentMap.get(id);
					// This is the value stored on the client
					Object v = student.get(id);
					
					Double value = null;
					if (v != null && v instanceof String) {
						String strValue = (String)v;
						if (strValue.trim().length() > 0)
							value = Double.valueOf(Double.parseDouble((String)v));
						
					} else
						value = (Double)v;
					
					AssignmentGradeRecord assignmentGradeRecord = null;
					
					if (gradeRecordMap != null) 
						assignmentGradeRecord = gradeRecordMap.get(assignment.getId()); //gbService.getAssignmentGradeRecordForAssignmentForStudent(assignment, student.getIdentifier());
					
					Double oldValue = null;
						
					if (assignmentGradeRecord == null)
						assignmentGradeRecord = new AssignmentGradeRecord();
						
					switch (gradebook.getGrade_type()) {
					case GradebookService.GRADE_TYPE_POINTS:
						oldValue = assignmentGradeRecord.getPointsEarned();
						break;
					case GradebookService.GRADE_TYPE_PERCENTAGE:
						oldValue = assignmentGradeRecord.getPercentEarned();
						break;
					}
						
					if (oldValue == null && value == null)
						continue;
					
					student.set(AppConstants.IMPORT_CHANGES, Boolean.TRUE);
					
					try {
						scoreItem(gradebook, assignment, assignmentGradeRecord, student.getIdentifier(), value, true, false);
							
							
						builder.append(assignment.getName()).append(" (");
							
						if (oldValue != null)
							builder.append(oldValue).append("->");
							
						builder.append(value).append(") ");
							
						//results.add("Successfully scored " + assignment.getName() + " for " + student.getIdentifier() + " to " + value);
					} catch (InvalidInputException e) {
						String failedProperty = new StringBuilder().append(assignment.getId()).append(StudentModel.FAILED_FLAG).toString();
						student.set(failedProperty, e.getMessage());
						log.warn("Failed to score numeric item for " + student.getIdentifier() + " and item " + assignment.getId() + " to " + value);
						
						if (oldValue != null)
							builder.append(oldValue);
							
						builder.append("Invalid) ");
					} catch (Exception e) {
						
						String failedProperty = new StringBuilder().append(assignment.getId()).append(StudentModel.FAILED_FLAG).toString();
						student.set(failedProperty, e.getMessage());
						
						log.warn("Failed to score numeric item for " + student.getIdentifier() + " and item " + assignment.getId() + " to " + value, e);
						
						if (oldValue != null)
							builder.append(oldValue);
							
						builder.append("Failed) ");
					}
					
				}
				
				results.add(builder.toString());
			}
		}
		spreadsheetModel.setResults(results);
		List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
		spreadsheetModel.setGradebookItemModel(getItemModel(gradebook, assignments, null));
		
		return spreadsheetModel;
	}
	
	private List<UserRecord> doSearchUsers(String searchString, List<String> studentUids, Map<String, UserRecord> userRecordMap) {
		
		// Make sure that our search criterion is case insensitive
		if (searchString != null)
			searchString = searchString.toUpperCase();
	
		List<UserRecord> userRecords = new ArrayList<UserRecord>();
		
		// To do a search, we have to get all the users . . . this is also desirable even if we're not searching, if we want to sort on these properties
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
	
	private List<UserRecord> doSearchAndSortUserRecords(Gradebook gradebook, List<Assignment> assignments, List<Category> categories,
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
			case COURSE_GRADE:
				// In this case we need to ensure that we've calculated everybody's course grade
				for (UserRecord record : userRecords) {
					record.setDisplayGrade(getDisplayGrade(gradebook, record.getCourseGradeRecord(), assignments, categories, record.getGradeRecordMap()));
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
		
		assignmentGradeRecord.setExcluded(value);
		
		// Prepare record for update
		assignmentGradeRecord.setGradableObject(assignment);
		assignmentGradeRecord.setStudentId(student.getIdentifier());
		
		Collection<AssignmentGradeRecord> updateGradeRecords = new LinkedList<AssignmentGradeRecord>();
		updateGradeRecords.add(assignmentGradeRecord);
		gbService.updateAssignmentGradeRecords(assignment, updateGradeRecords, gradebook.getGrade_type());

		return refreshLearnerData(gradebook, student, assignment, gradeRecords);
	}
	
	public List<UserDereference> findAllUserDereferences() {
		
		Site site = getSite();
		String siteId = site == null ? null : site.getId();
		
		String[] learnerRoleNames = advisor.getLearnerRoleNames();
		verifyUserDataIsUpToDate(site, learnerRoleNames);
		
		String[] realmIds = null;
		if (siteId == null) {
    		if (log.isInfoEnabled())
				log.info("No siteId defined");
    		return new ArrayList<UserDereference>();
    	}
		
		realmIds = new String[1];
		realmIds[0] = new StringBuffer().append("/site/").append(siteId).toString();
		
		List<UserDereference> dereferences = gbService.getUserUidsForSite(realmIds, "sortName", null, null, -1, -1, true, learnerRoleNames);
		
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
					break;
				case UPDATE:
					actionModel = new UserEntityUpdateAction();
					break;
				}
				
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
				actionModel.setStudentUid(actionRecord.getStudentUid());
				/*actionModel.setKey(actionRecord.getField());
				actionModel.setValue(actionRecord.getValue());
				actionModel.setStartValue(actionRecord.getStartValue());*/
				
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
				
				/*switch (entityType) {
				case CATEGORY:
				case GRADEBOOK:
				case ITEM:
					ItemModel itemModel = new ItemModel();
					if (propertyMap != null) {
						for (String key : propertyMap.keySet()) {
							String value = propertyMap.get(key);
							itemModel.set(key, value);
						}
					}
					actionModel.setModel(itemModel);
					break;
				default:*/
					if (propertyMap != null) {
						for (String key : propertyMap.keySet()) {
							String value = propertyMap.get(key);
							actionModel.set(key, value);
						}
					}
				//}
				
				
				actionModel.setDescription(actionModel.toString());
				
				models.add((X)actionModel);
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
	
	
	public ApplicationModel getApplicationModel() {
		
		/*Site site = getSite();
		String[] learnerRoleKeys = advisor.getLearnerRoleNames();
		
		WorkerThread worker = new WorkerThread(site, learnerRoleKeys);
		worker.start();*/
		
		ApplicationModel model = new ApplicationModel();
		model.setPlacementId(getPlacementId());
		model.setGradebookModels(getGradebookModels());
		
		return model;
	}
	
	public String getExportCourseManagementId(String userEid, Group group) {
		return advisor.getExportCourseManagementId(userEid, group);
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
		return createGradebookModel(gradebook, assignments);
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
				models.add((X)createOrUpdateGradeEventModel(null, event));
			}
		}
		
		ListLoadResult<X> result = new BaseListLoadResult<X>(models);
		
		return result;
	}
	
	public <X extends BaseModel> PagingLoadResult<X> getSections(String gradebookUid,
			Long gradebookId, PagingLoadConfig config) {
		
		List<CourseSection> viewableSections = security.getViewableSections(gradebookUid, gradebookId);
		
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
	
	public <X extends BaseModel> ListLoadResult<X> getSelectedGradeMapping(String gradebookUid) {
		
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
		
		ListLoadResult<X> result = new BaseListLoadResult<X>(gradeScaleMappings);
		
		return result;
	}
	
	public <X extends BaseModel> PagingLoadResult<X> getStudentRows(String gradebookUid, 
			Long gradebookId, PagingLoadConfig config) {

		List<X> rows = new ArrayList<X>();
		
		String[] learnerRoleNames = advisor.getLearnerRoleNames();
		
		List<UserRecord> userRecords = null;
		
		String sectionUuid = null;

		if (config != null && config instanceof MultiGradeLoadConfig) {
			sectionUuid = ((MultiGradeLoadConfig)config).getSectionUuid();
		}
		
	    Gradebook gradebook = null;
	    if (gradebookId == null) {
	    	gradebook = gbService.getGradebook(gradebookUid);
	    	gradebookId = gradebook.getId();
	    }
	   
	    List<Assignment> assignments = gbService.getAssignments(gradebookId);
	    
	    // Don't bother going out to the db for the Gradebook if we've already retrieved it
	    if (gradebook == null && assignments != null && assignments.size() > 0) 
	    	gradebook = assignments.get(0).getGradebook();
	    
	    if (gradebook == null)
	    	gradebook = gbService.getGradebook(gradebookId);
	    
	    List<Category> categories = null;
		if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY)
			categories = getCategoriesWithAssignments(gradebook.getId(), assignments);
	    
	    String columnId = null;
	    StudentModel.Key sortColumnKey = null;
	    
	    int offset = -1;
	    int limit = -1;
	    
	    String searchField = null;
		String searchCriteria = null;
		
	    // This is slightly painful, but since it's a String that gets passed up, we have to iterate
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
				searchCriteria = ((MultiGradeLoadConfig)config).getSearchString();
				
				if (searchCriteria != null)
					searchCriteria = searchCriteria.toUpperCase();
			}
		} 
		
		if (sortColumnKey == null)
			sortColumnKey = StudentModel.Key.DISPLAY_NAME;
		
		boolean isDescending = config != null && config.getSortInfo() != null && config.getSortInfo().getSortDir() == SortDir.DESC;
		
		int totalUsers = 0;
		Site site = getSite();
		String siteId = site == null ? null : site.getId();
		
		
		boolean isUserAuthorizedToGradeAll = security.isUserAbleToGradeAll(gradebook.getUid());
		boolean isLimitedToSection = false;
		Set<String> authorizedGroups = new HashSet<String>();
		if (sectionUuid != null) {
			if (!security.isUserTAinSection(sectionUuid))
				return new BasePagingLoadResult<X>(rows, 0, totalUsers);
			
			authorizedGroups.add(sectionUuid);
			isLimitedToSection = true;
		}
		
		
		Collection<Group> groups = site == null ? new ArrayList<Group>() : site.getGroups();
		Map<String, Group> groupReferenceMap = new HashMap<String, Group>();
		List<String> groupReferences = new ArrayList<String>();
		if (groups != null) {
			for (Group group : groups) {
				String reference = group.getReference();
				groupReferences.add(reference);
				groupReferenceMap.put(reference, group);
				
				String sectionUid = group.getProviderGroupId();
				
				if (! isLimitedToSection) {
					if (!isUserAuthorizedToGradeAll && sectionUid != null && security.isUserTAinSection(reference)) {
						authorizedGroups.add(reference);
					}
				} 
			}
		}
		
		String[] realmGroupIds = null;
		if (!authorizedGroups.isEmpty()) 
			realmGroupIds = authorizedGroups.toArray(new String[authorizedGroups.size()]);
		
		String[] realmIds = realmGroupIds;
    	
    	if (realmIds == null) {
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
						
				
				
				userRecords = findLearnerRecordPage(gradebook, site, realmIds, groupReferences, groupReferenceMap,  sortField, searchField, searchCriteria, offset, limit, !isDescending);
				totalUsers = gbService.getUserCountForSite(realmIds, sortField, searchField, searchCriteria, learnerRoleNames);
				
				int startRow = config == null ? 0 : config.getOffset();
				
				List<FixedColumnModel> columns = getColumns();
				
				rows = new ArrayList<X>(userRecords == null ? 0 : userRecords.size());
				
				// We only want to populate the rowData and rowValues for the requested rows
				for (UserRecord userRecord : userRecords) {
					rows.add((X)buildStudentRow(gradebook, userRecord, columns, assignments, categories));
				}
				
				return new BasePagingLoadResult<X>(rows, startRow, totalUsers);
				
			case SECTION:
			case COURSE_GRADE:
			case GRADE_OVERRIDE:
			case ASSIGNMENT:
				
				userRecords = findLearnerRecordPage(gradebook, site, realmIds, groupReferences, groupReferenceMap, null, searchField, searchCriteria, -1, -1, !isDescending);
				
				Map<String, UserRecord> userRecordMap = new HashMap<String, UserRecord>(); //findStudentRecords(gradebookUid, gradebookId, site, sectionUuid);
				
				for (UserRecord userRecord : userRecords) {
					userRecordMap.put(userRecord.getUserUid(), userRecord);
				}
				
				
			    List<String> studentUids = new ArrayList<String>(userRecordMap.keySet());
				
			   	//Map<String, Map<Long, AssignmentGradeRecord>>  allGradeRecordsMap = new HashMap<String, Map<Long, AssignmentGradeRecord>>();

			    /*
			    List<AssignmentGradeRecord> allGradeRecords = gbService.getAllAssignmentGradeRecords(gradebookId, studentUids);
			   // List<AssignmentGradeRecord> allGradeRecords = gbService.getAllAssignmentGradeRecords(gradebookId, siteId, sectionUuid, learnerRoleNames);

				    	
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
				*/
						
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
					userRecord.setLastNameFirst(user.getSortName());
					userRecord.setSortName(user.getSortName());
					userRecord.setEmail(user.getEmail());
				} catch (UserNotDefinedException e) {
					log.error("No sakai user defined for this member '" + userRecord.getUserUid() + "'", e);
				}
			}
			
			rows.add((X)buildStudentRow(gradebook, userRecord, columns, assignments, categories));
		}
		
		return new BasePagingLoadResult<X>(rows, startRow, totalUsers);
	}
	
	
	public StudentModel scoreNumericItem(String gradebookUid, StudentModel student, String assignmentId, Double value, Double previousValue) throws InvalidInputException {
		
		Assignment assignment = gbService.getAssignment(Long.valueOf(assignmentId));
		Gradebook gradebook = assignment.getGradebook();

		ActionRecord actionRecord = new ActionRecord(gradebook.getUid(), gradebook.getId(), EntityType.GRADE_RECORD.name(), ActionType.GRADED.name());
		actionRecord.setEntityName(new StringBuilder().append(student.getDisplayName()).append(" : ").append(assignment.getName()).toString());
		actionRecord.setEntityId(String.valueOf(assignment.getId()));
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
			//gradeRecords.add(assignmentGradeRecord);
		}
		
		scoreItem(gradebook, assignment, assignmentGradeRecord, student.getIdentifier(), value, false, false);
		
		gradeRecords = gbService.getAssignmentGradeRecordsForStudent(gradebook.getId(), student.getIdentifier());
		
		refreshLearnerData(gradebook, student, assignment, gradeRecords);
		student.set(assignmentId, value);
		
		gbService.storeActionRecord(actionRecord);
		
		return student;
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
		
		List<Category> categories = null;
		List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
		if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY)
			categories = getCategoriesWithAssignments(gradebook.getId(), assignments);
		
		Map<Long, AssignmentGradeRecord> studentGradeMap = new HashMap<Long, AssignmentGradeRecord>();
		List<AssignmentGradeRecord> records = gbService.getAssignmentGradeRecordsForStudent(gradebook.getId(), student.getIdentifier());
		
		if (records != null) {
			for (AssignmentGradeRecord record : records) {
				studentGradeMap.put(record.getAssignment().getId(), record);
			}
		}
		
		
		String freshCourseGrade = getDisplayGrade(gradebook, courseGradeRecord, assignments, categories, studentGradeMap);//requestCourseGrade(gradebookUid, student.getIdentifier());
		student.set(StudentModel.Key.GRADE_OVERRIDE.name(), courseGradeRecord.getEnteredGrade());
		student.set(StudentModel.Key.COURSE_GRADE.name(), freshCourseGrade);
		
		return student;
	}
	
	
	public void submitFinalGrade(List<Map<Column, String>> studentDataList, String gradebookUid, HttpServletRequest request, HttpServletResponse response) {
		advisor.submitFinalGrade(studentDataList, gradebookUid, request, response);
	}
	
	/**
	 * Method to update an item model
	 * 
	 * Business rules:
	 * 	(1) If points is null, set points to 100
	 *  (2) If weight is null, set weight to be equivalent to points value -- needs to happen after #1
	 *   	
	 *  - When category type is "No Categories":
	 * 		(3) updated item name must not duplicate an active (removed = false) item name in gradebook, otherwise throw exception (NoDuplicateItemNamesRule)
	 * 		(4) must not include an item in grading that has been deleted (removed = true)
	 * 
	 * 	- When category type is "Categories" or "Weighted Categories"
	 * 		(5) new item name must not duplicate an active (removed = false) item name in the same category, otherwise throw exception
	 * 		(6) must not include an item in grading that has been deleted (removed = true) or that has a category that has been deleted (removed = true)
	 * 		(7) if item is "included" and category has "equal weighting" then recalculate all item weights for this category
	 * 		(8) item must include a valid category id
	 * 		(9) if category has changed, then if the old category had equal weighting and the item was included in that category, then recalculate all item weights for that category
	 * 	   (10) if item weight changes then remove the equal weighting flag (set to false) for the owning category
	 * 	   (11) if category is not included, then cannot include item
	 *     (12) if category is removed, then cannot unremove item
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

		boolean hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;		
		boolean hasCategoryChanged = false;
		
		if (hasCategories && category != null) 
			hasCategoryChanged = !category.getId().equals(item.getCategoryId());
		
		
		ActionRecord actionRecord = new ActionRecord(gradebook.getUid(), gradebook.getId(), EntityType.ITEM.name(), ActionType.UPDATE.name());
		actionRecord.setEntityName(assignment.getName());
		actionRecord.setEntityId(String.valueOf(assignment.getId()));
		
		Map<String, String> propertyMap = actionRecord.getPropertyMap();
		for (String propertyName : item.getPropertyNames()) {
			Object value = item.get(propertyName);
			if (value != null)
				propertyMap.put(propertyName, String.valueOf(value));
		}
		
		List<Assignment> assignments = null;
		try {
			
			// Check to see if the category id has changed -- this means the user switched the item's category
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
			
			isWeightChanged = isWeightChanged || DataTypeConversionUtil.notEquals(newAssignmentWeight, oldAssignmentWeight);
					
			boolean isUnweighted = !convertBoolean(item.getIncluded()).booleanValue();
			boolean wasUnweighted = DataTypeConversionUtil.checkBoolean(assignment.isUnweighted());
			
			boolean isRemoved = convertBoolean(item.getRemoved()).booleanValue();
			boolean wasRemoved = DataTypeConversionUtil.checkBoolean(assignment.isRemoved());
			
			// We only want to update the weights when we're dealing with an included item
			if (!isUnweighted && !isRemoved) {
				// Business rule #2
				assignment.setAssignmentWeighting(gradeCalculations.calculateItemWeightAsPercentage(newAssignmentWeight, points));
			} else {
				newAssignmentWeight = oldAssignmentWeight;
			}
			
			isWeightChanged = isWeightChanged || isUnweighted != wasUnweighted;
			isWeightChanged = isWeightChanged || isRemoved != wasRemoved;
			
			if (hasCategories && category != null) {
				boolean isCategoryIncluded = !DataTypeConversionUtil.checkBoolean(category.isUnweighted());
				assignments = gbService.getAssignmentsForCategory(category.getId());
				
				// Business rule #12
				businessLogic.applyCannotUnremoveItemWithRemovedCategory(isRemoved, category);
				
				// Business rule #5
				businessLogic.applyNoDuplicateItemNamesWithinCategoryRule(item.getCategoryId(), item.getName(), assignment.getId(), assignments);
			
				// Business rule #6 
				businessLogic.applyCannotIncludeDeletedItemRule(wasRemoved && isRemoved, category.isRemoved(), isUnweighted);
				
				// Business rule #11
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
			
			
			
			// Modify the assignment name
			assignment.setName(convertString(item.getName()));
			if (hasCategories && hasCategoryChanged) {
				category = gbService.getCategory(item.getCategoryId());
				assignment.setCategory(category);
			}
			assignment.setExtraCredit(Boolean.valueOf(isExtraCredit));
			assignment.setReleased(convertBoolean(item.getReleased()).booleanValue());
			assignment.setPointsPossible(points);
			assignment.setDueDate(convertDate(item.getDueDate()));
			assignment.setRemoved(isRemoved);
			assignment.setUnweighted(Boolean.valueOf(isUnweighted || isRemoved));
			
			
			gbService.updateAssignment(assignment);
	
			if (hasCategories) {
				
				// Business rule #7 -- only apply this rule when included/unincluded, deleted/undeleted, made extra-credit/non-extra-credit, or changed category
				if (isUnweighted != wasUnweighted || isRemoved != wasRemoved || isExtraCredit != wasExtraCredit || oldCategory != null) {
					isWeightChanged = true;
					if (businessLogic.checkRecalculateEqualWeightingRule(category))
						recalculateAssignmentWeights(category, !isUnweighted, assignments);
				}
				
				// Business rule #9
				if (oldCategory != null && businessLogic.checkRecalculateEqualWeightingRule(oldCategory)) {
					List<Assignment> oldAssignments = gbService.getAssignmentsForCategory(oldCategory.getId());
					recalculateAssignmentWeights(oldCategory, !wasUnweighted, oldAssignments);
				}
				
				// Business rule #10
				businessLogic.applyRemoveEqualWeightingWhenItemWeightChangesRules(category, oldAssignmentWeight, newAssignmentWeight, isExtraCredit, isUnweighted, wasUnweighted);
			}
			
			if (businessLogic.checkRecalculatePointsRule(assignmentId, points, oldPoints))
				recalculateAssignmentGradeRecords(assignment, points, oldPoints);
			

		} catch (RuntimeException e) {
			actionRecord.setStatus(ActionRecord.STATUS_FAILURE);
			throw e;
		} finally {
			gbService.storeActionRecord(actionRecord);
		}
		
		// The first case is that we're in categories mode and the category has changed
		if (hasCategories && oldCategory != null) {
			assignments = gbService.getAssignments(gradebook.getId());
			return getItemModel(gradebook, assignments, assignment.getId());
		}
		
		// If neither the weight nor the points have changed, then we can just return 
		// the item model itself
		if (!isWeightChanged && !havePointsChanged) {
			ItemModel itemModel = createItemModel(category, assignment, null);
			itemModel.setActive(true);
			return itemModel;
		} else if (! hasCategories) {
			// Otherwise if we're in no categories mode then we want to return the gradebook
			return getItemModel(gradebook, assignments, assignment.getId());
		}
		
		// Otherwise we can return the category parent
		ItemModel categoryItemModel = getItemModelsForCategory(category, item.getParent(), assignment.getId());
		
		String assignmentIdAsString = String.valueOf(assignment.getId());
		for (ModelData model : categoryItemModel.getChildren()) {
			ItemModel itemModel = (ItemModel) model;
			if (itemModel.getIdentifier().equals(assignmentIdAsString)) 
				itemModel.setActive(true);
		}
		
		return categoryItemModel;
	}
	
	public <X extends BaseModel> List<X> updateGradeScaleField(String gradebookUid, Object value, String affectedLetterGrade) {
		
		// FIXME: Need to store action record for this change.
		
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
	
	
	/*
	 * PROTECTED METHODS
	 */
	protected String getPlacementId() {
		if (toolManager == null)
			return null;
		
		return toolManager.getCurrentPlacement().getId();
	}
	
	
	/*
	 * PRIVATE METHODS
	 */
	
	private Map<String, Object> appendItemData(Long assignmentId, Map<String, Object> cellMap, 
			UserRecord userRecord, Gradebook gradebook) {
		AssignmentGradeRecord gradeRecord = null;
		
		String id = String.valueOf(assignmentId);
		//String id = item.getIdentifier();
		//Long assignmentId = Long.valueOf(id);
		
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
				
				//boolean isGraded = gbService.isStudentGraded(userRecord.getUserUid(), assignmentId);
				
				//if (isGraded)
				//	cellMap.put(concat(id, StudentModel.GRADED_FLAG), Boolean.TRUE);
				
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
	
	private StudentModel buildStudentRow(Gradebook gradebook, UserRecord userRecord, 
			List<FixedColumnModel> columns, 
			List<Assignment> assignments, List<Category> categories) {
		
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
		CourseGradeRecord courseGradeRecord = userRecord.getCourseGradeRecord(); //gradebookManager.getStudentCourseGradeRecord(gradebook, userRecord.getUserUid());
	
		String enteredGrade = null;
		String displayGrade = null;
	
		if (courseGradeRecord != null) 
			enteredGrade = courseGradeRecord.getEnteredGrade();
		
		if (userRecord.isCalculated())
			displayGrade = userRecord.getDisplayGrade();
		else
			displayGrade = getDisplayGrade(gradebook, courseGradeRecord, assignments, categories, studentGradeMap);
			
		if (columns != null) {
			for (FixedColumnModel column : columns) {
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
					cellMap.put(StudentModel.Key.LAST_NAME_FIRST.name(), userRecord.getLastNameFirst());
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
	
	// FIXME: This should be moved into GradeCalculations
	private void calculateItemCategoryPercent(Gradebook gradebook, Category category, ItemModel gradebookItemModel, ItemModel categoryItemModel, List<Assignment> assignments,
			Long assignmentId) {
		double pG = categoryItemModel == null || categoryItemModel.getPercentCourseGrade() == null ? 0d : categoryItemModel.getPercentCourseGrade().doubleValue();
		
		
		BigDecimal percentGrade = BigDecimal.valueOf(pG);
		BigDecimal percentCategorySum = BigDecimal.ZERO;
		BigDecimal pointsSum = BigDecimal.ZERO;
		if (assignments != null) {
			for (Assignment a : assignments) {
				double assignmentCategoryPercent = a.getAssignmentWeighting() == null ? 0.0 : a.getAssignmentWeighting().doubleValue() * 100.0;
				BigDecimal points = BigDecimal.valueOf(a.getPointsPossible().doubleValue());

				boolean isRemoved = a.isRemoved();
				boolean isExtraCredit = a.isExtraCredit() != null && a.isExtraCredit().booleanValue();
				boolean isUnweighted = a.isUnweighted() != null && a.isUnweighted().booleanValue();
				
				if (!isExtraCredit && !isUnweighted && !isRemoved) {
					percentCategorySum = percentCategorySum.add(BigDecimal.valueOf(assignmentCategoryPercent));
					pointsSum = pointsSum.add(points);
				}

			}
			
			for (Assignment a : assignments) {
				
				boolean isUnweighted = a.isUnweighted() != null && a.isUnweighted().booleanValue();
				
				BigDecimal courseGradePercent = BigDecimal.ZERO;
				if (!isUnweighted) {
					double w = a == null ? 0d : a.getAssignmentWeighting().doubleValue();
					BigDecimal assignmentWeight = BigDecimal.valueOf(w);
					courseGradePercent = gradeCalculations.calculateItemGradePercent(percentGrade, percentCategorySum, assignmentWeight);
				}
				
				ItemModel assignmentItemModel = createItemModel(category, a, courseGradePercent);
				
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
		
		if (categoryItemModel != null) {
			categoryItemModel.setPercentCategory(Double.valueOf(percentCategorySum.doubleValue()));
			categoryItemModel.setPoints(Double.valueOf(pointsSum.doubleValue()));
		}
	}
	
	private GradebookModel createGradebookModel(Gradebook gradebook, List<Assignment> assignments) {
		GradebookModel model = new GradebookModel();
		String gradebookUid = gradebook.getUid();
		
		model.setGradebookUid(gradebookUid);
		model.setGradebookId(gradebook.getId());
		model.setName(gradebook.getName());
		
		//List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
		List<Category> categories = null;
		if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY)
			categories = getCategoriesWithAssignments(gradebook.getId(), assignments);
		ItemModel gradebookItemModel = getItemModel(gradebook, assignments, null);
		model.setGradebookItemModel(gradebookItemModel);
		List<FixedColumnModel> columns = getColumns();
		
		boolean isUserAbleToGrade = security.isUserAbleToGrade(gradebookUid);
		boolean isUserAbleToViewOwnGrades = security.isUserAbleToViewOwnGrades(gradebookUid);
		
		boolean isSingleUserView = isUserAbleToViewOwnGrades && !isUserAbleToGrade;
		
		model.setUserAbleToGrade(isUserAbleToGrade);
		model.setUserAbleToEditAssessments(security.isUserAbleToEditAssessments(gradebookUid));
		model.setUserAbleToViewOwnGrades(isUserAbleToViewOwnGrades);
		model.setUserHasGraderPermissions(security.isUserHasGraderPermissions(gradebook.getId()));
		
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
				
				model.setUserName(user.getDisplayName());
			}
		} else {
			String[] realmIds = { "/site/mock" };
			List<UserRecord> userRecords = findLearnerRecordPage(gradebook, getSite(), realmIds, null, null, null, null, null, -1, -1, true);
			
			//Map<String, UserRecord> userRecordMap = //findStudentRecords(gradebookUid, gradebook.getId(), null, null);
			
			if (userRecords != null && userRecords.size() > 0) {
				UserRecord userRecord = userRecords.get(0);
				model.setUserName(userRecord.getDisplayName());
				model.setUserAsStudent(buildStudentRow(gradebook, userRecord, columns, assignments, categories));
			}
		}
			
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
		
		return itemModel;
	}
	

	private ItemModel createItemModel(Gradebook gradebook, Category category, List<Assignment> assignments) {
		ItemModel model = new ItemModel();
		
		double categoryWeight = category.getWeight() == null ? 0d : category.getWeight().doubleValue() * 100d;
		boolean isIncluded = category.isUnweighted() == null ? true : ! category.isUnweighted().booleanValue();
		
		//if (! isIncluded || category.isRemoved()) 
		//	categoryWeight = 0d;
		
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
		model.setItemType(Type.CATEGORY);
		
		return model;
	}
	

	private ItemModel createItemModel(Category category, Assignment assignment, BigDecimal percentCourseGrade) {
		
		ItemModel model = new ItemModel();
		
		double assignmentWeight = assignment.getAssignmentWeighting() == null ? 0d : assignment.getAssignmentWeighting().doubleValue() * 100.0;
		Boolean isAssignmentIncluded = assignment.isUnweighted() == null ? Boolean.TRUE : Boolean.valueOf(!assignment.isUnweighted().booleanValue());
		Boolean isAssignmentExtraCredit = assignment.isExtraCredit() == null ? Boolean.FALSE : assignment.isExtraCredit();
		Boolean isAssignmentReleased = Boolean.valueOf(assignment.isReleased());
		Boolean isAssignmentRemoved = Boolean.valueOf(assignment.isRemoved());
		
		Gradebook gradebook = assignment.getGradebook();
		boolean hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;
		
		
		// We don't want to delete assignments based on category when we don't have categories
		if (hasCategories && category != null) {
			
			if (category.isRemoved())
				isAssignmentRemoved = Boolean.TRUE;
			
			if (category.isUnweighted() != null && category.isUnweighted().booleanValue()) 
				isAssignmentIncluded = Boolean.FALSE;
		
		}
		
		//if (! isAssignmentIncluded.booleanValue() || assignment.isRemoved()) 
		//	assignmentWeight = 0d;
		
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
		model.setDataType(AppConstants.NUMERIC_DATA_TYPE);
		model.setStudentModelKey(Key.ASSIGNMENT.name());
		
		if (percentCourseGrade == null && hasCategories && category != null) {
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
	
	private Category findDefaultCategory(Long gradebookId) {
		List<Category> categories = gbService.getCategories(gradebookId);
	
		// Let's see if we already have a default category in existence
		Long defaultCategoryId = null;
		if (categories != null && ! categories.isEmpty()) {
			// First, look for it by name
			for (Category category : categories) {
				if (category.getName().equalsIgnoreCase("Unassigned")) {
					defaultCategoryId = category.getId();
					break;
				}
			}
		}
		
		boolean isCategoryNew = false;
		
		// If we don't have one already, then let's create one
		if (defaultCategoryId == null) {
			defaultCategoryId = gbService.createCategory(gradebookId, "Unassigned", Double.valueOf(1d), 0, null, null, null);
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
	
	protected List<UserRecord> findLearnerRecordPage(Gradebook gradebook, Site site, String[] realmIds, List<String> groupReferences, 
			Map<String, Group> groupReferenceMap, String sortField, String searchField, String searchCriteria,
			int offset, int limit, 
			boolean isAscending) {
		
		String[] learnerRoleKeys = advisor.getLearnerRoleNames();
		verifyUserDataIsUpToDate(site, learnerRoleKeys);
		
		List<UserDereference> dereferences = gbService.getUserUidsForSite(realmIds, sortField, searchField, searchCriteria, offset, limit, isAscending, learnerRoleKeys);
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
	    	    	
		List<CourseGradeRecord> courseGradeRecords = gbService.getAllCourseGradeRecords(gradebook.getId(), realmIds, sortField, searchField, searchCriteria, offset, limit, isAscending, learnerRoleKeys);
		Map<String, CourseGradeRecord> studentCourseGradeRecordMap = new HashMap<String, CourseGradeRecord>();
		
		if (courseGradeRecords != null) {
			for (CourseGradeRecord courseGradeRecord : courseGradeRecords) {
				String studentUid = courseGradeRecord.getStudentId();
				studentCourseGradeRecordMap.put(studentUid, courseGradeRecord);
			}
		}
			
		List<Comment> comments = gbService.getComments(gradebook.getId(), realmIds, learnerRoleKeys, sortField, searchField, searchCriteria, offset, limit, isAscending);
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
		
		List<Object[]> tuples = gbService.getUserGroupReferences(groupReferences, learnerRoleKeys);
		
		if (tuples != null) {
			for (Object[] tuple : tuples) {
				String userUid = (String)tuple[0];
				String realmId = (String)tuple[1];
				
				Group group = groupReferenceMap.get(realmId);
				
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
				UserRecord userRecord = new UserRecord(dereference.getUserUid(), dereference.getEid(), dereference.getDisplayId(), dereference.getDisplayName(),
						dereference.getLastNameFirst(), dereference.getSortName(), dereference.getEmail());
				
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
					for (Iterator<Group> groupIter = userGroups.iterator();groupIter.hasNext();) {
						Group group = groupIter.next();
						groupTitles.append(group.getTitle());
						courseManagementIds.append(advisor.getExportCourseManagementId(userRecord.getUserEid(), group));
					
						if (groupIter.hasNext()) {
							groupTitles.append(",");
							courseManagementIds.append(",");
						}
					}
					userRecord.setSectionTitle(groupTitles.toString());
					userRecord.setExportCourseManagemntId(courseManagementIds.toString());	
				}
				userRecord.setExportUserId(advisor.getExportUserId(dereference));
				userRecord.setFinalGradeUserId(advisor.getFinalGradeUserId(dereference));
				
				userRecords.add(userRecord);
			}
		}

		
		return userRecords;
	}
	
	protected List<User> findAllMembers(Site site) {
		List<User> users = new ArrayList<User>();
		if (site != null) {
			Set<Member> members = site == null ? new HashSet<Member>() : site.getMembers();
			if (members != null) {
				Set<String> userUids = new HashSet<String>();
				for (Member member : members) {
					String userUid = member.getUserId();	
					userUids.add(userUid);
				}
				
				if (userService != null) {
					users = userService.getUsers(userUids);
				}
			}
		}
		return users;
	}

	
	private List<Category> getCategoriesWithAssignments(Long gradebookId, List<Assignment> assignments) {

		List<Category> categories = gbService.getCategories(gradebookId);

		Map<Long, Category> categoryMap = new HashMap<Long, Category>();

		if (categories != null) {
			for (Category category : categories) {
				categoryMap.put(category.getId(), category);
			}
		}
		
		Category category = null;
		List<Assignment> assignmentList = null;
		
		if (assignments != null) {
			for(Assignment assignment : assignments) {
	
				if (assignment.isRemoved())
					continue;
				
				category = categoryMap.get(assignment.getCategory().getId());
	
				if(null == category) {
	
					category = assignment.getCategory();
					categoryMap.put(category.getId(), category);
				}
				
				assignmentList = category.getAssignmentList();
	
				if(null == assignmentList) {
	
					assignmentList = new ArrayList<Assignment>();
					category.setAssignmentList(assignmentList);
				}
	
				if (!assignmentList.contains(assignment))
					assignmentList.add(assignment);
			}
		}

		return categories;	
	}
	
	private <X extends BaseModel> List<X> getColumns() {
		List<X> columns = new LinkedList<X>();
				
		columns.add((X)new FixedColumnModel(StudentModel.Key.DISPLAY_ID, 80, true));
		columns.add((X)new FixedColumnModel(StudentModel.Key.DISPLAY_NAME, 180, false));
		columns.add((X)new FixedColumnModel(StudentModel.Key.LAST_NAME_FIRST, 180, true));
		columns.add((X)new FixedColumnModel(StudentModel.Key.EMAIL, 230, true));
		columns.add((X)new FixedColumnModel(StudentModel.Key.SECTION, 120, true));
		columns.add((X)new FixedColumnModel(StudentModel.Key.COURSE_GRADE, 120, true));
		FixedColumnModel gradeOverrideColumn = new FixedColumnModel(StudentModel.Key.GRADE_OVERRIDE, 120, true);
		gradeOverrideColumn.setEditable(true);
		columns.add((X)gradeOverrideColumn);

		return columns;
	}
	
	private String getDisplayGrade(Gradebook gradebook, CourseGradeRecord courseGradeRecord, 
			List<Assignment> assignments, List<Category> categories, Map<Long, AssignmentGradeRecord> studentGradeMap) {
		
		BigDecimal autoCalculatedGrade = null;
		
		switch (gradebook.getCategory_type()) {
		case GradebookService.CATEGORY_TYPE_NO_CATEGORY:
			autoCalculatedGrade = gradeCalculations.getCourseGrade(gradebook, assignments, studentGradeMap);
			break;
		default: 
			autoCalculatedGrade = gradeCalculations.getCourseGrade(gradebook, categories, studentGradeMap);
		}
		
		Double calculatedGrade = autoCalculatedGrade == null ? null : Double.valueOf(autoCalculatedGrade.doubleValue());
		
		String enteredGrade = null;
		String displayGrade = null;
		String letterGrade = null;
	
		boolean isOverridden = false;
		
		if (courseGradeRecord != null) 
			enteredGrade = courseGradeRecord.getEnteredGrade();
		
		if (enteredGrade == null && calculatedGrade != null) 
			letterGrade = gradebook.getSelectedGradeMapping().getGrade(calculatedGrade);
		else {
			letterGrade = enteredGrade;
			isOverridden = true;
		}
		
		String missingGradesMarker = "";
		
		if (assignments != null) {
			for (Assignment assignment : assignments) {
				if (assignment.isRemoved() || DataTypeConversionUtil.checkBoolean(assignment.isUnweighted()))
					continue;
							
				if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY) {
					Category category = assignment.getCategory();
								
					// If the assignment belongs to a category that's removed or unweighted, skip
					if (category != null && 
						(category.isRemoved() || DataTypeConversionUtil.checkBoolean(category.isUnweighted())))
						continue;
				}
				
				// The student is missing one or more grades if 
				/// (a) there's no studentGradeMap
				/// (b) there's no AssignmentGradeRecord for this assignment
				/// (c) there's no points earned for this AssignmentGradeRecord
				if (studentGradeMap != null && studentGradeMap.get(assignment.getId()) != null) {
								
					AssignmentGradeRecord record = studentGradeMap.get(assignment.getId());
								
					boolean isExcused = record.isExcluded() != null && record.isExcluded().booleanValue();
					boolean isDropped = record.isDropped() != null && record.isDropped().booleanValue();
					if (record.getPointsEarned() == null && !isExcused && !isDropped) 
						missingGradesMarker = "***";
			
				} else 
					missingGradesMarker = "***"; 
			} // for
		} // if 

		
		if (letterGrade != null) {
			StringBuilder buffer = new StringBuilder(letterGrade);
			
			if (isOverridden) {
				buffer.append(" (override)").append(missingGradesMarker);
			} else if (autoCalculatedGrade != null) {
				buffer.append(" (")
				.append(autoCalculatedGrade.setScale(2, RoundingMode.HALF_EVEN).toString())
				.append("%) ").append(missingGradesMarker);
			}
			
			displayGrade = buffer.toString();	
		}
		
		return displayGrade;
	}
	
	protected String getGradebookUid() {
		Placement placement = toolManager.getCurrentPlacement();
	    if (placement == null) {
	    	log.error("Placement is null!");
	    	return null;
	    }

	    return placement.getContext();
	}
	
	private List<GradebookModel> getGradebookModels() {
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

				List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
				
				// There are different ways that unassigned assignments can appear - old gradebooks, external apps
				List<Assignment> unassignedAssigns = new ArrayList<Assignment>();//gbService.getAssignmentsWithNoCategory(gradebook.getId());
				
				if (assignments != null) {
					for (Assignment assignment : assignments) {
						if (assignment.getCategory() == null)
							unassignedAssigns.add(assignment);
					}
				}
				
				// If we have any that are unassigned, we want to assign them to the default category
				if (unassignedAssigns != null && !unassignedAssigns.isEmpty()) {
					List<Category> categories = gbService.getCategories(gradebook.getId());
					
					// Let's see if we already have a default category in existence
					Long defaultCategoryId = null;
					if (categories != null && ! categories.isEmpty()) {
						// First, look for it by name
						for (Category category : categories) {
							if (category.getName().equalsIgnoreCase("Unassigned")) {
								defaultCategoryId = category.getId();
								break;
							}
						}
					}
					
					boolean isCategoryNew = false;
					
					// If we don't have one already, then let's create one
					if (defaultCategoryId == null) {
						defaultCategoryId = gbService.createCategory(gradebook.getId(), "Unassigned", Double.valueOf(1d), 0, null, null, null);
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
							for (Assignment assignment : unassignedAssigns) {
								// Think we need to grab each assignment again - this is stupid, but I'm pretty sure it's what hibernate requires
								//Assignment assignment = gbService.getAssignment(a.getId());
								//assignment.setCategory(defaultCategory);
								gbService.updateAssignment(assignment);
							}
							List<Assignment> unassignedAssignments = gbService.getAssignmentsForCategory(defaultCategory.getId());
							// This will only recalculate assuming that the category has isEqualWeighting as TRUE
							recalculateAssignmentWeights(defaultCategory, null, unassignedAssignments);
						}
					}

				}
				
				GradebookModel model = createGradebookModel(gradebook, assignments);
				models.add(model);
			}
		}
		
		return models;
	}
	
	private ItemModel getItemModel(Gradebook gradebook, List<Assignment> assignments, Long assignmentId) {
		
		ItemModel gradebookItemModel = createItemModel(gradebook);
		
		boolean isNotInCategoryMode = gradebook.getCategory_type() == GradebookService.CATEGORY_TYPE_NO_CATEGORY;
		
		if (isNotInCategoryMode) {
			calculateItemCategoryPercent(gradebook, null, gradebookItemModel, null, assignments, assignmentId);
			
		} else {
			List<Category> categories = getCategoriesWithAssignments(gradebook.getId(), assignments);
		
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
		
		//ItemModel gradebookItemModel = createItemModel(gradebook);
		
		ItemModel categoryItemModel = createItemModel(gradebook, category, null);
		categoryItemModel.setParent(gradebookItemModel);
		gradebookItemModel.add(categoryItemModel);
				
		calculateItemCategoryPercent(gradebook, category, gradebookItemModel, categoryItemModel, assignments, assignmentId);
		
		return categoryItemModel;
	}
	

	protected Site getSite() {
		
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
	
	protected String getSiteContext() {
		return toolManager.getCurrentPlacement().getContext();
	}
	
	private String getSiteId() {
		
		String context = getSiteContext();
	    String siteId = null;
	    
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

	
	private void recalculateAssignmentGradeRecords(Assignment assignment, Double value, Double startValue) {
		//Assignment assignment = gbService.getAssignment(assignmentId);
		
		//List<UserDereference> dereferences = findAllUserDereferences();
		
		// FIXME: Ensure that only users with access to all the students' records can call this method!!!
		//Map<String, EnrollmentRecord> enrollmentRecordMap = security.findEnrollmentRecords(gradebook.getUid(), gradebook.getId(), null, null);
		//List<String> studentUids = new ArrayList<String>(enrollmentRecordMap.keySet());
		//List<EnrollmentRecord> enrollmentRecords = new ArrayList<EnrollmentRecord>(enrollmentRecordMap.values());
		
		//Collections.sort(enrollmentRecords, ENROLLMENT_NAME_COMPARATOR);
		
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

		int weightedCount = 0;
		if (assignments != null) {
			for (Assignment assignment : assignments) {
				boolean isRemoved = assignment.isRemoved();
				boolean isWeighted = assignment.isUnweighted() == null ? true : ! assignment.isUnweighted().booleanValue();
				boolean isExtraCredit = assignment.isExtraCredit() == null ? false : assignment.isExtraCredit().booleanValue();
				if (isWeighted && !isExtraCredit && !isRemoved) {
					weightedCount++;
				}
			}
		}
		
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
					boolean isWeighted = assignment.isUnweighted() == null ? true : ! assignment.isUnweighted().booleanValue();
					boolean isExtraCredit = assignment.isExtraCredit() == null ? false : assignment.isExtraCredit().booleanValue();
					if (!isRemoved && isWeighted) {
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
	
	
	/*private void recalculateAssignmentGradeRecords(Long assignmentId, Double value, Double startValue) {
		Assignment assignment = gbService.getAssignment(assignmentId);
		Gradebook gradebook = assignment.getGradebook();
		
		// FIXME: Ensure that only users with access to all the students' records can call this method!!!
		Map<String, EnrollmentRecord> enrollmentRecordMap = security.findEnrollmentRecords(gradebook.getUid(), gradebook.getId(), null, null);
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
	
	
	private List<Assignment> recalculateAssignmentWeights(Long categoryId, Boolean isEqualWeighting) {
		List<Assignment> updatedAssignments = new ArrayList<Assignment>();
		List<Assignment> assignments = gbService.getAssignmentsForCategory(categoryId);

		int weightedCount = 0;
		if (assignments != null) {
			for (Assignment assignment : assignments) {
				boolean isRemoved = assignment.isRemoved();
				boolean isWeighted = assignment.isUnweighted() == null ? true : ! assignment.isUnweighted().booleanValue();
				boolean isExtraCredit = assignment.isExtraCredit() == null ? false : assignment.isExtraCredit().booleanValue();
				if (isWeighted && !isExtraCredit && !isRemoved) {
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
			Double newWeight = gradeCalculations.calculateEqualWeight(weightedCount);
			if (assignments != null) {
				for (Assignment assignment : assignments) {
					boolean isRemoved = assignment.isRemoved();
					boolean isWeighted = assignment.isUnweighted() == null ? true : ! assignment.isUnweighted().booleanValue();
					boolean isExtraCredit = assignment.isExtraCredit() == null ? false : assignment.isExtraCredit().booleanValue();
					if (!isRemoved && isWeighted) {
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
	}*/
	
	private StudentModel refreshLearnerData(Gradebook gradebook, StudentModel student, Assignment assignment, List<AssignmentGradeRecord> assignmentGradeRecords) {
		
		Map<Long, AssignmentGradeRecord> studentGradeMap = new HashMap<Long, AssignmentGradeRecord>();
		
		for (AssignmentGradeRecord gradeRecord : assignmentGradeRecords) {
			Assignment a = gradeRecord.getAssignment();
			studentGradeMap.put(a.getId(), gradeRecord);
		}
		
		/*if (assignments != null) {
			for (Assignment a : assignments) {
				AssignmentGradeRecord record = gbService.getAssignmentGradeRecordForAssignmentForStudent(a, student.getIdentifier());
				record.setGradableObject(a);
				studentGradeMap.put(a.getId(), record);
			}
		}*/
		
		// FIXME: There has to be a more efficient way of doing this -- all we really need this for is to determine if the learner has been graded for all assignments
		// FIXME: We should be able to replace that logic in getDisplayGrade with a clever db query.
		List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
		List<Category> categories = null;
		if (gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY)
			categories = getCategoriesWithAssignments(gradebook.getId(), assignments);
		CourseGradeRecord courseGradeRecord = gbService.getStudentCourseGradeRecord(gradebook, student.getIdentifier());
		String displayGrade = getDisplayGrade(gradebook, courseGradeRecord, assignments, categories, studentGradeMap);
		
		for (AssignmentGradeRecord record : assignmentGradeRecords) {
			Long aId = record.getGradableObject().getId();
			String dropProperty =  concat(String.valueOf(aId), StudentModel.DROP_FLAG);
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
		
		/*String gradedProperty = assignment.getId() + StudentModel.GRADED_FLAG;
		if (gbService.isStudentGraded(student.getIdentifier(), assignment.getId())) 		
			student.set(gradedProperty, Boolean.TRUE);
		else
			student.set(gradedProperty, null);*/
		
		String commentedProperty = assignment.getId() + StudentModel.COMMENTED_FLAG;
		if (gbService.isStudentCommented(student.getIdentifier(), assignment.getId())) 		
			student.set(commentedProperty, Boolean.TRUE);
		else
			student.set(commentedProperty, null);
		
		student.set(StudentModel.Key.COURSE_GRADE.name(), displayGrade);
		
		return student;
	}
	
	private AssignmentGradeRecord scoreItem(Gradebook gradebook, Assignment assignment, 
			AssignmentGradeRecord assignmentGradeRecord,
			String studentUid, Double value, boolean includeExcluded, boolean deferUpdate) throws InvalidInputException {
		boolean isUserAbleToGrade = security.isUserAbleToGradeAll(gradebook.getUid()) || security.isUserAbleToGradeItemForStudent(gradebook.getUid(), assignment.getId(), studentUid);
	
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
	
		if (!includeExcluded && assignmentGradeRecord.isExcluded() != null && assignmentGradeRecord.isExcluded().booleanValue()) 
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
		
		if (!deferUpdate) {
			Collection<AssignmentGradeRecord> gradeRecords = new LinkedList<AssignmentGradeRecord>();
			gradeRecords.add(assignmentGradeRecord);
			gbService.updateAssignmentGradeRecords(assignment, gradeRecords, gradebook.getGrade_type());
		}
		
		return assignmentGradeRecord;
	}
	
	
	private void verifyUserDataIsUpToDate(Site site, String[] learnerRoleKeys) {
		String siteId = site == null ? null : site.getId();
		
		int totalUsers = gbService.getFullUserCountForSite(siteId, null, learnerRoleKeys);
		int dereferencedUsers = gbService.getDereferencedUserCountForSite(siteId, null, learnerRoleKeys);
		
		int diff = totalUsers - dereferencedUsers;
		
		UserDereferenceRealmUpdate lastUpdate = gbService.getLastUserDereferenceSync(siteId, null);
		
		int realmCount = lastUpdate == null || lastUpdate.getRealmCount() == null ? -1 : lastUpdate.getRealmCount().intValue();
		
		log.info("Total users: " + totalUsers + " Dereferenced users: " + dereferencedUsers + " Realm count: " + realmCount);
		
		// Obviously if the realm count has changed, then we need to update, but let's also do it if more than an hour has passed
		long ONEHOUR = 1000l * 60l * 60l;
		if (lastUpdate == null || lastUpdate.getRealmCount() == null || ! lastUpdate.getRealmCount().equals(Integer.valueOf(diff)) ||
				lastUpdate.getLastUpdate() == null || lastUpdate.getLastUpdate().getTime() + ONEHOUR < new Date().getTime()) {
			gbService.syncUserDereferenceBySite(siteId, null, findAllMembers(site), diff, learnerRoleKeys);
		}
	}
	
	
	private ItemModel updateGradebookModel(ItemModel item) {
		
		Gradebook gradebook = gbService.getGradebook(item.getIdentifier());
		
		ActionRecord actionRecord = new ActionRecord(gradebook.getUid(), gradebook.getId(), EntityType.GRADEBOOK.name(), ActionType.UPDATE.name());
		actionRecord.setEntityName(gradebook.getName());
		actionRecord.setEntityId(gradebook.getUid());
		
		logActionRecord(actionRecord, item);
		
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
		
		gradebook.setGrade_type(newGradeType);
		
		boolean wasReleaseGrades = gradebook.isCourseGradeDisplayed();
		boolean isReleaseGrades = DataTypeConversionUtil.checkBoolean(item.getReleaseGrades());
		
		gradebook.setCourseGradeDisplayed(isReleaseGrades);
		
		gbService.updateGradebook(gradebook);
		
		List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
		return getItemModel(gradebook, assignments, null);
	}
	
	/**
	 * Method to update a category model
	 * 
	 * Business rules:
	 * 	(1) if weight is null or zero, uninclude it
	 *  (2) new category name must not duplicate an existing category name
	 *  (3) if equal weighting is set, then recalculate all item weights of child items
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

			//category.setName(convertString(item.getName()));
			
			boolean originalExtraCredit = DataTypeConversionUtil.checkBoolean(category.isExtraCredit());
			boolean currentExtraCredit = DataTypeConversionUtil.checkBoolean(item.getExtraCredit());
			
			isWeightChanged = originalExtraCredit != currentExtraCredit;
			
			//category.setExtraCredit(Boolean.valueOf(currentExtraCredit));
			
			Double newCategoryWeight = item.getPercentCourseGrade();
			Double oldCategoryWeight = category.getWeight();
			
			isWeightChanged = isWeightChanged || DataTypeConversionUtil.notEquals(newCategoryWeight, oldCategoryWeight);
	
			double w = newCategoryWeight == null ? 0d : ((Double)newCategoryWeight).doubleValue() * 0.01;

			boolean isEqualWeighting = DataTypeConversionUtil.checkBoolean(item.getEqualWeightAssignments());
			boolean wasEqualWeighting = DataTypeConversionUtil.checkBoolean(category.isEqualWeightAssignments());

			isWeightChanged = isWeightChanged || isEqualWeighting != wasEqualWeighting;
			
			
			boolean isUnweighted = !DataTypeConversionUtil.checkBoolean(item.getIncluded());
			boolean wasUnweighted = DataTypeConversionUtil.checkBoolean(category.isUnweighted());
			
			if (wasUnweighted && !isUnweighted && category.isRemoved())
				throw new InvalidInputException("You cannot include a deleted category in grade. Please undelete the category first.");
	
			int oldDropLowest = category.getDrop_lowest();
			int newDropLowest = convertInteger(item.getDropLowest()).intValue();
			
			boolean isRemoved = DataTypeConversionUtil.checkBoolean(item.getRemoved());
			boolean wasRemoved = category.isRemoved();

			// FIXME: Do we want to do this?
			/*if (!isUnweighted && !isRemoved) {
				// Since we don't want to leave the category weighting as 0 if a category has been re-included,
				// but we don't know what the user wants it to be, we set it to 1%
				double aw = category.getWeight() == null ? 0d : category.getWeight().doubleValue();
				if (aw == 0d)
					category.setWeight(Double.valueOf(0.01));
			}*/
			
			List<BusinessLogicImpl> beforeCreateRules = new ArrayList<BusinessLogicImpl>();
			List<BusinessLogicImpl> afterCreateRules = new ArrayList<BusinessLogicImpl>();
			boolean hasCategories = gradebook.getCategory_type() != GradebookService.CATEGORY_TYPE_NO_CATEGORY;

			
			if (hasCategories) {
				List<Category> categories = gbService.getCategories(gradebook.getId());
				// Business rule #2
				businessLogic.applyNoDuplicateCategoryNamesRule(gradebook.getId(), item.getName(), category.getId(), categories);
					
				businessLogic.applyOnlyEqualWeightDropLowestRule(newDropLowest, isEqualWeighting);
			}
			
			
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
			
			gbService.updateCategory(category);
			
			if (hasCategories) {
				List<Assignment> assignmentsForCategory = gbService.getAssignmentsForCategory(category.getId());
				
				if (isRemoved && !wasRemoved) 
					businessLogic.applyRemoveChildItemsWhenCategoryRemoved(category, assignmentsForCategory);
				
				// Business rule #3
				if (isEqualWeighting && !wasEqualWeighting && businessLogic.checkRecalculateEqualWeightingRule(category))
					recalculateAssignmentWeights(category, Boolean.FALSE, assignmentsForCategory);
								
			}
			
		} catch (RuntimeException e) {
			actionRecord.setStatus(ActionRecord.STATUS_FAILURE);
			throw e;
		} finally {
			gbService.storeActionRecord(actionRecord);
		}
		
		List<Assignment> assignments = gbService.getAssignments(gradebook.getId());
		ItemModel gradebookItemModel = getItemModel(gradebook, assignments, null);
		
		for (ItemModel child : gradebookItemModel.getChildren()) {
			if (child.equals(item)) {
				child.setActive(true);
			}
		}
		
		return gradebookItemModel;
	}
	
	
	/*
	 * UTILITY HELPER METHODS
	 */
	private String concat(String... vars) {
		StringBuilder builder = new StringBuilder();
		
		for (int i=0;i<vars.length;i++) {
			builder.append(vars[i]);
		}
		
		return builder.toString();
	}
	
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
	
	private Integer convertInteger(Object value) {
		return value == null ? Integer.valueOf(0) : (Integer)value;
	}
	
	
	/**
	 * INNER CLASSES
	 */
	
	/*private class OnlyEqualWeightDropLowestRule implements BusinessRule {
		
		private int dropLowest;
		private boolean isEqualWeight;
		
		public OnlyEqualWeightDropLowestRule(int dropLowest, boolean isEqualWeight) {
			this.dropLowest = dropLowest;
			this.isEqualWeight = isEqualWeight;
		}
		
		public void isSatisfied() throws BusinessRuleException {
			
			if (!isEqualWeight) {
				if (dropLowest > 0) {
					StringBuilder builder = new StringBuilder();
					builder.append("Drop lowest is only valid for categories with equally weighted items. ")
						.append("Please select equally weighted before setting a drop lowest value.");
					
					throw new BusinessRuleException(builder.toString());
				}
				
			}
			
		}
		
	}
	
	private class NoDuplicateCategoryNamesRule implements BusinessRule {

		private Long categoryId;
		private Long gradebookId;
		private String name;
		private List<Category> categories;
		
		public NoDuplicateCategoryNamesRule(Long gradebookId, String name) {
			this.gradebookId = gradebookId;
			this.name = name;
		}
		
		public NoDuplicateCategoryNamesRule(Long gradebookId, String name, Long categoryId) {
			this(gradebookId, name);
			this.categoryId = categoryId;
		}
		
		public NoDuplicateCategoryNamesRule(Long gradebookId, String name, Long categoryId, List<Category> categories) {
			this(gradebookId, name, categoryId);
			this.categories = categories;
		}
		
		public void isSatisfied() throws BusinessRuleException {
			if (categories == null)
				categories = gbService.getCategories(gradebookId);
			
			if (categories != null) {
				for (Category c : categories) {
					if (!c.isRemoved() && c.getName() != null && name != null && c.getName().trim().equalsIgnoreCase(name.trim())) {
						if (categoryId != null && categoryId.equals(c.getId()))
							continue;
						
						StringBuilder builder = new StringBuilder();
						builder.append("There is already an existing category called \"").append(name).append("\" ").append("in this gradebook. ")
							.append("Please enter a different name for this category.");
						
						throw new BusinessRuleException(builder.toString());
					}
				}
			}
		}
	}
	
	private class NoDuplicateItemNamesRule implements BusinessRule {

		private Long assignmentId;
		private Long gradebookId;
		private String name;
		
		public NoDuplicateItemNamesRule(Long gradebookId, String name) {
			this.gradebookId = gradebookId;
			this.name = name;
		}
		
		public NoDuplicateItemNamesRule(Long gradebookId, String name, Long assignmentId) {
			this(gradebookId, name);
			this.assignmentId = assignmentId;
		}
		
		public void isSatisfied() throws BusinessRuleException {
			List<Assignment> assignments = gbService.getAssignments(gradebookId);
			
			if (assignments != null) {
				for (Assignment a : assignments) {
					if (!a.isRemoved() && a.getName() != null && name != null && a.getName().trim().equalsIgnoreCase(name.trim())) {
						if (assignmentId != null && assignmentId.equals(a.getId()))
							continue;
						
						StringBuilder builder = new StringBuilder();
						builder.append("There is already an existing item called \"").append(name).append("\" ").append("in this gradebook. ")
							.append("Please enter a different name for the grade item.");
						
						throw new BusinessRuleException(builder.toString());
					}
				}
			}
		}
	}
	
	private class NoDuplicateItemNamesWithinCategoryRule implements BusinessRule {
		
		private Long assignmentId;
		private Long categoryId;
		private String name;
		
		public NoDuplicateItemNamesWithinCategoryRule(Long categoryId, String name) {
			this.categoryId = categoryId;
			this.name = name;
		}
		
		public NoDuplicateItemNamesWithinCategoryRule(Long categoryId, String name, Long assignmentId) {
			this(categoryId, name);
			this.assignmentId = assignmentId;
		}
		
		public void isSatisfied() throws BusinessRuleException {
			List<Assignment> assignments = gbService.getAssignmentsForCategory(categoryId);
			
			if (assignments != null) {
				for (Assignment a : assignments) {
					if (!a.isRemoved() && a.getName().trim().equalsIgnoreCase(name.trim())) {
						if (assignmentId != null && assignmentId.equals(a.getId()))
							continue;
						
						StringBuilder builder = new StringBuilder();
						builder.append("There is already an existing item called \"").append(name).append("\" ").append("in this category. ")
							.append("Please enter a different name for the grade item.");
						
						throw new BusinessRuleException(builder.toString());
					}
				}
			}
		}
	}
	
	private class RecalculateEqualWeightingRule implements BusinessRule {
		
		private Category category;
		private boolean includeInGrade;
		
		public RecalculateEqualWeightingRule(Category category, boolean includeInGrade) {
			this.category = category;
			this.includeInGrade = includeInGrade;
		}
		
		public void isSatisfied() throws BusinessRuleException {
			boolean hasEqualWeighting = DataTypeConversionUtil.checkBoolean(category.isEqualWeightAssignments());
			if (hasEqualWeighting)
				recalculateAssignmentWeights(category.getId(), null);
			
			
		}
	}
	
	private class RecalculatePointsRule implements BusinessRule {
		
		private Long assignmentId;
		private Double newPoints;
		private Double oldPoints;
		
		public RecalculatePointsRule(Long assignmentId, Double newPoints, Double oldPoints) {
			this.assignmentId = assignmentId;
			this.newPoints = newPoints;
			this.oldPoints = oldPoints;
		}
		
		public void isSatisfied() throws BusinessRuleException {
			
			if (newPoints != null && oldPoints != null && newPoints.compareTo(oldPoints) != 0) {
				recalculateAssignmentGradeRecords(assignmentId, newPoints, oldPoints);
			}
			
		}
		
		
	}
	
	
	private class MustIncludeCategoryRule implements BusinessRule {
		
		private Long categoryId;
		
		public MustIncludeCategoryRule(Long categoryId) {
			this.categoryId = categoryId;
		}
		
		public void isSatisfied() throws BusinessRuleException {
			if (categoryId == null)
				throw new BusinessRuleException("You must select a category to group this item under.");
		}
		
	}
	
	private class RemoveChildItemsWhenCategoryRemoved implements BusinessRule {
		
		private Category category;
		
		public RemoveChildItemsWhenCategoryRemoved(Category category) {
			this.category = category;
		}
		
		public void isSatisfied() throws BusinessRuleException {
			List<Assignment> assignments = gbService.getAssignmentsForCategory(category.getId());
			
			if (assignments != null) {
				for (Assignment assignment : assignments) {
					assignment.setRemoved(true);
					gbService.updateAssignment(assignment);
				}
			}
			
		}
		
		
	}
	
	private class RemoveEqualWeightingWhenItemWeightChangesRules implements BusinessRule {
		
		private Category category;
		private Double oldAssignmentWeight;
		private Double newAssignmentWeight;
		private boolean isExtraCredit;
		private boolean isUnweighted;
		private boolean wasUnweighted;
		
		public RemoveEqualWeightingWhenItemWeightChangesRules(Category category, Double oldAssignmentWeight, Double newAssignmentWeight, boolean isExtraCredit, 
				boolean isUnweighted, boolean wasUnweighted) {
			this.category = category;
			this.oldAssignmentWeight = oldAssignmentWeight;
			this.newAssignmentWeight = newAssignmentWeight;
			this.isExtraCredit = isExtraCredit;
			this.isUnweighted = isUnweighted;
			this.wasUnweighted = wasUnweighted;
		}
		
		public void isSatisfied() throws BusinessRuleException {
			if (!isUnweighted && !wasUnweighted) {
				if (oldAssignmentWeight == null || !oldAssignmentWeight.equals(newAssignmentWeight)) {
						
					if (!isExtraCredit && category.isEqualWeightAssignments() != null && category.isEqualWeightAssignments().booleanValue()) {
						Category editCategory = gbService.getCategory(category.getId());
						editCategory.setEqualWeightAssignments(Boolean.FALSE);
						gbService.updateCategory(editCategory);
					}
				}
			}
		}
	}
	
	private class CannotIncludeDeletedItemRule implements BusinessRule {

		private boolean isUnweighted;
		private boolean isAssignmentRemoved;
		private boolean isCategoryRemoved;
		
		public CannotIncludeDeletedItemRule(boolean isAssignmentRemoved, boolean isCategoryRemoved, boolean isUnweighted) {
			this.isAssignmentRemoved = isAssignmentRemoved;
			this.isCategoryRemoved = isCategoryRemoved;
			this.isUnweighted = isUnweighted;
		}
		
		public void isSatisfied() throws BusinessRuleException {
			if (!isUnweighted) {		
				if (isCategoryRemoved)
					throw new BusinessRuleException("You cannot include a grade item whose category has been deleted in grading. Please undelete the category first.");
					
				if (isAssignmentRemoved) 
					throw new BusinessRuleException("You cannot include a deleted grade item in grading. Please undelete the grade item first.");
			}
		}
		
	}
	
	
	private class CannotIncludeItemFromUnincludedCategoryRule implements BusinessRule {

		private boolean isCategoryIncluded, isItemIncluded, wasItemIncluded;
		
		public CannotIncludeItemFromUnincludedCategoryRule(boolean isCategoryIncluded, boolean isItemIncluded, boolean wasItemIncluded) {
			this.isCategoryIncluded = isCategoryIncluded;
			this.isItemIncluded = isItemIncluded;
			this.wasItemIncluded = wasItemIncluded;
		}
		
		public void isSatisfied() throws BusinessRuleException {
			if (!isCategoryIncluded) {		
				if (isItemIncluded && !wasItemIncluded)
					throw new BusinessRuleException("You cannot include a grade item whose category is not included in grading. Please include the category first.");
			}
		}
		
	}*/
	
	
	/**
	 * COMPARATORS
	 */
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


	public Gradebook2Security getSecurity() {
		return security;
	}


	public void setSecurity(Gradebook2Security security) {
		this.security = security;
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
	

}
