package org.sakaiproject.gradebook.gwt.test.mock;

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.action.PageRequestAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.MultiGradeLoadConfig;
import org.sakaiproject.gradebook.gwt.client.model.AssignmentModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class ToolFacadeNoCategoriesPointTest extends AbstractToolFacadePointTest {

	@Override
	protected void initialize() throws InvalidInputException, FatalException {
		initialize(CategoryType.NO_CATEGORIES);
	}

	/*
	 * Overall grade will be points based 20 + 15 + 5 + 10 + 10 + 8 + 2 = 70 points out of 100 possible
	 */
	public void testRepresentativeGrade() throws InvalidInputException, FatalException {
		setRepresentativePointsGrade();
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
}
