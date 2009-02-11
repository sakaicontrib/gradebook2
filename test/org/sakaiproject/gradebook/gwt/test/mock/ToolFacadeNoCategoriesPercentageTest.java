package org.sakaiproject.gradebook.gwt.test.mock;

import java.util.Date;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.action.PageRequestAction;
import org.sakaiproject.gradebook.gwt.client.action.UserAssignmentCreateAction;
import org.sakaiproject.gradebook.gwt.client.action.UserCategoryCreateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.MultiGradeLoadConfig;
import org.sakaiproject.gradebook.gwt.client.model.AssignmentModel;
import org.sakaiproject.gradebook.gwt.client.model.CategoryModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.test.mock.AbstractToolFacadeTest.ScoreType;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class ToolFacadeNoCategoriesPercentageTest extends AbstractToolFacadePercentageTest {

	@Override
	protected void initialize() throws InvalidInputException, FatalException {
		initialize(CategoryType.NO_CATEGORIES);
	}
	
	/*
	 * Overall grade will be points based 20 + 15 + 5 + 10 + 10 + 8 + 2 = 70 points out of 100 possible
	 */
	// FIXME: We probably shouldn't use "points" in this case, since we're in percentages mode, but that's the current setup
	public void testRepresentativeGrade() throws InvalidInputException, FatalException {
		setRepresentativePercentagesGrade();
		checkCourseGrade("C- (70.00%) ");
	}
	
	/*
	 * Overall grade will be points based -- deleting the essays category shouldn't have any impact,
	 * since it does not delete the individual items and we're in no-category mode
	 * 
	 * so, as above:
	 * 20 + 15 + 5 + 10 + 10 + 8 + 2 = 70 points out of 100 possible
	 */
	public void testDeleteEssays() throws InvalidInputException, FatalException {
		setRepresentativePercentagesGrade();
		essaysCategory = makeCategoryDeleted(essaysCategory, true);
		checkCourseGrade("C- (70.00%) ");
	}
	
	/*
	 * If we have no categories, then we would expect to see all 5 items on this page
	 * 
	 * (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.test.mock.AbstractToolFacadeTest#testGetGradeItemsEssaysDeleted()
	 */
	public void testGetGradeItemsEssaysDeleted() throws InvalidInputException, FatalException {
		essaysCategory = makeCategoryDeleted(essaysCategory, true);
		
		PageRequestAction action = new PageRequestAction(EntityType.GRADE_ITEM, gbModel.getGradebookUid(), gbModel.getGradebookId());
		PagingLoadConfig config = new MultiGradeLoadConfig();
		config.setOffset(0);
		config.setLimit(5);
		PagingLoadResult<AssignmentModel> result = facade.getEntityPage(action, config);
		
		List<AssignmentModel> gradeItems = result.getData();
		
		assertEquals(5, gradeItems.size());
	}
	
	
	/*
	 * Overall grades will be points based 15 + 15 + 15 + 7.5 + 7.5 + 7.5 + 7.5 (+ 20) = 95 out of 100
	 */
	public void testExtraCreditCategory() throws InvalidInputException, FatalException {
		super.testExtraCreditCategory();
		checkCourseGrade("A (95.00%) ");
	}
	
	/*
	 * Overall grades will be points based 15 + 15 + 15 + 7.5 + 7.5 + 7.5 + 7.5 (+ 15) = 90 out of 100
	 * 
	 */
	public void testExtraCreditCategoryPartial() throws InvalidInputException, FatalException {
		super.testExtraCreditCategoryPartial();
		checkCourseGrade("A- (90.00%) ");
	}
	
	/*
	 * With points based:
	 * 15 + 15 + 15 (+20) + 7.5 + 7.5 + 7.5 + 7.5 = 95 out of 100
	 * 
	 */
	public void testExtraCreditItem() throws InvalidInputException, FatalException {
		super.testExtraCreditItem();
		checkCourseGrade("A (95.00%) ");
	}
	
}
