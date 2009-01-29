package org.sakaiproject.gradebook.gwt.test.mock;

import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;

public class ToolFacadeCategoriesPointTest extends AbstractToolFacadePointTest {

	@Override
	protected void initialize() throws InvalidInputException, FatalException {
		initialize(CategoryType.SIMPLE_CATEGORIES);
	}

	/*
	 * Overall grade will be points based 20 + 15 + 5 + 10 + 10 + 8 + 2 = 70 points out of 100 possible
	 */
	public void testRepresentativeGrade() throws InvalidInputException, FatalException {
		setRepresentativePointsGrade();
		checkCourseGrade("C- (70.00%) ");
	}
	
	/*
	 * Overall grade will be points based without the essays 10 + 10 + 8 + 2 = 30 points 
	 * out of 40 possible
	 */
	public void testDeleteEssays() throws InvalidInputException, FatalException {
		setRepresentativePointsGrade();
		essaysCategory = makeCategoryDeleted(essaysCategory, true);
		checkCourseGrade("C (75.00%) ");
	}

	
}
