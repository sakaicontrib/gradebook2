package org.sakaiproject.gradebook.gwt.test.mock;

import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;

public class ToolFacadeCategoriesPercentageTest extends AbstractToolFacadePercentageTest {

	@Override
	protected void initialize() throws InvalidInputException, FatalException {
		initialize(CategoryType.SIMPLE_CATEGORIES);
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
	 * Overall grade will be points based without the essays 10 + 10 + 8 + 2 = 30 points 
	 * out of 40 possible
	 */
	public void testDeleteEssays() throws InvalidInputException, FatalException {
		setRepresentativePercentagesGrade();
		essaysCategory = makeCategoryDeleted(essaysCategory, true);
		checkCourseGrade("C (75.00%) ");
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
