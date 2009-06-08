package org.sakaiproject.gradebook.gwt.sakai;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.gradebook.gwt.client.exceptions.BusinessRuleException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationModel;
import org.sakaiproject.gradebook.gwt.client.model.AuthModel;
import org.sakaiproject.gradebook.gwt.client.model.CommentModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.SpreadsheetModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.client.model.SubmissionVerificationModel;
import org.sakaiproject.gradebook.gwt.sakai.InstitutionalAdvisor.Column;
import org.sakaiproject.gradebook.gwt.sakai.model.UserDereference;
import org.sakaiproject.site.api.Group;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public interface Gradebook2Service {

	public ItemModel addItemCategory(String gradebookUid, Long gradebookId, ItemModel item) throws BusinessRuleException;
	
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
	public ItemModel createItem(String gradebookUid, Long gradebookId, final ItemModel item, boolean enforceNoNewCategories) throws InvalidInputException;
	
	
	public CommentModel createOrUpdateComment(Long assignmentId, String studentUid, String text);
	
	
	/**
	 * Method to bulk update grades and gradebook structure for a given gradebook
	 * 
	 * @param gradebookUid : a String identifier for the gradebook
	 * @param spreadsheetModel : SpreadsheetModel object representing the desired bulk updates
	 * @return the modified SpreadsheetModel object representing the results of a bulk update
	 * 
	 * @throws InvalidInputException
	 */
	public SpreadsheetModel createOrUpdateSpreadsheet(String gradebookUid, SpreadsheetModel spreadsheetModel) 
	throws InvalidInputException;
	
	public StudentModel excuseNumericItem(String gradebookUid, StudentModel student, String id, Boolean value, Boolean previousValue) 
	throws InvalidInputException;
	
	public List<UserDereference> findAllUserDereferences();
	
	/**
	 * 
	 * 
	 * @param <X>
	 * @param gradebookUid
	 * @param config
	 * @return
	 */
	public <X extends BaseModel> PagingLoadResult<X> getActionHistory(String gradebookUid, PagingLoadConfig config);
	
	public ApplicationModel getApplicationModel(String... gradebookUids);
	
	public AuthModel getAuthorization(String... gradebookUids);
	
	public List<String> getExportCourseManagementSetEids(Group group);
	
	public String getExportCourseManagementId(String userEid, Group group, List<String> enrollmentSetEids);
	
	public String getExportUserId(UserDereference dereference);
	
	public String getFinalGradeUserId(UserDereference dereference);
	
	public GradebookModel getGradebook(String uid);
	
	public <X extends BaseModel> ListLoadResult<X> getGradeEvents(String studentId, Long assignmentId);
	
	public <X extends BaseModel> ListLoadResult<X> getCategories(String gradebookUid,
			Long gradebookId, PagingLoadConfig config);
	
	public <X extends BaseModel> PagingLoadResult<X> getSections(String gradebookUid,
			Long gradebookId, PagingLoadConfig config);
	
	public <X extends BaseModel> ListLoadResult<X> getSelectedGradeMapping(String gradebookUid);
	
	public <X extends BaseModel> PagingLoadResult<X> getStudentRows(String gradebookUid, Long gradebookId, PagingLoadConfig config, Boolean includeExportCourseManagementId);
	
	public SubmissionVerificationModel getSubmissionVerification(String gradebookUid, Long gradebookId);
	
	public StudentModel scoreNumericItem(String gradebookUid, StudentModel student, String assignmentId, Double value, Double previousValue) 
	throws InvalidInputException;
	
	public StudentModel scoreTextItem(String gradebookUid, StudentModel student, String property, String value, String previousValue) 
	throws InvalidInputException;
	
	public void submitFinalGrade(List<Map<Column, String>> studentDataList, String gradebookUid, HttpServletRequest request, HttpServletResponse response);
	
	public <X extends BaseModel> List<X> updateGradeScaleField(String gradebookUid, Object value, String affectedLetterGrade);
	
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
	public ItemModel updateItemModel(ItemModel item) throws InvalidInputException;
	

	/*
	 * The following two methods are used by a security check method
	 */
	public String getCurrentUser();
	public String getCurrentSession();
	
}
