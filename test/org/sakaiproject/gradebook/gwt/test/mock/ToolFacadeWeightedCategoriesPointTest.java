package org.sakaiproject.gradebook.gwt.test.mock;

import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;

public class ToolFacadeWeightedCategoriesPointTest extends AbstractToolFacadePointTest {

	@Override
	protected void initialize() throws InvalidInputException {
		initialize(CategoryType.WEIGHTED_CATEGORIES);
	}

	/*
	 * If the essays category has 100% weight, and the hw category has 10% and is an extra credit category,
	 * then giving a learner a 75% on all essays and 100% on all homework should produce an 85%
	 */
	public void testMediocreScoresExtraCreditHomework() throws InvalidInputException {
		makeHomeworkTenPercentExtraCredit();
		gradeCategorySameScore(essaysCategory, ScoreType.MEDIOCRE);
		gradeCategorySameScore(hwCategory, ScoreType.PERFECT_SCORE);
		checkCourseGrade("B (85.00%) ");
	}
	
	/*
	 * With weighted categories, we have 
	 * Essays (40%)
	 * 	- 1 : 33.333% : 20 of 20 = 1.00 = 33.333
	 *  - 2 : 33.333% : 15 of 20 = 0.75 = 25
	 *  - 3 : 33.333% :  5 of 20 = 0.25 =  8.3333325
	 *  - Total : 66.666655 = 26.66666
	 *  
	 * Homework (60%)
	 *  - 1 : 25% : 10 of 10 = 1.00 = 25
	 *  - 2 : 25% : 10 of 10 = 1.00 = 25
	 *  - 3 : 25% :  8 of 10 =  .80 = 20
	 *  - 4 : 25% :  2 of 10 =  .20 = 5
	 *  - Total : 75 = 45
	 * 
	 * Overall grade will be 71.67
	 */
	public void testRepresentativeGrade() throws InvalidInputException {
		setRepresentativePointsGrade();
		checkCourseGrade("C- (71.67%) ");
	}
	
}
