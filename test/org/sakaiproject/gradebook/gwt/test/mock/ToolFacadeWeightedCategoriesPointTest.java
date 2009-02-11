package org.sakaiproject.gradebook.gwt.test.mock;

import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;

public class ToolFacadeWeightedCategoriesPointTest extends AbstractToolFacadePointTest {

	@Override
	protected void initialize() throws InvalidInputException, FatalException {
		initialize(CategoryType.WEIGHTED_CATEGORIES);
	}

	/*
	 * If the essays category has 100% weight, and the hw category has 10% and is an extra credit category,
	 * then giving a learner a 75% on all essays and 100% on all homework should produce an 85%
	 */
	public void testMediocreScoresExtraCreditHomework() throws InvalidInputException, FatalException {
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
	public void testRepresentativeGrade() throws InvalidInputException, FatalException {
		setRepresentativePointsGrade();
		checkCourseGrade("C- (71.67%) ");
	}
	
	/*
	 * If we delete the Essays Category then we only have 
	 * 
	 * Homework (60%)
	 *  - 1 : 25% : 10 of 10 = 1.00 = 25
	 *  - 2 : 25% : 10 of 10 = 1.00 = 25
	 *  - 3 : 25% :  8 of 10 =  .80 = 20
	 *  - 4 : 25% :  2 of 10 =  .20 = 5
	 *  - Total : 75%
	 */
	public void testDeleteEssays() throws InvalidInputException, FatalException {
		setRepresentativePointsGrade();
		essaysCategory = makeCategoryDeleted(essaysCategory, true);
		checkCourseGrade("C (75.00%) ");
	}
	
	/*
	 * If we delete the Homework Category then we only have
	 *  
	 * Essays (40%)
	 * 	- 1 : 33.333% : 20 of 20 = 1.00 = 33.333
	 *  - 2 : 33.333% : 15 of 20 = 0.75 = 25
	 *  - 3 : 33.333% :  5 of 20 = 0.25 =  8.3333325
	 *  - Total : 66.666655
	 */
	public void testDeleteHomework() throws InvalidInputException, FatalException {
		setRepresentativePointsGrade();
		hwCategory = makeCategoryDeleted(hwCategory, true);
		checkCourseGrade("D (66.67%) ");
	}
	
	/*
	 * Overall grades will be 75% + 10% extra credit = 85%
	 */
	public void testExtraCreditCategory() throws InvalidInputException, FatalException {
		super.testExtraCreditCategory();
		checkCourseGrade("B (85.00%) ");
	}
	
	/*
	 * Overall grades with weighted categories:
	 * 75% for all non-extra credit + 7.5% extra credit category = 82.5%
	 * 
	 */
	public void testExtraCreditCategoryPartial() throws InvalidInputException, FatalException {
		super.testExtraCreditCategoryPartial();
		checkCourseGrade("B- (82.50%) ");
	}
	

	/*
	 * With weighted categories, we have 
	 * Essays (40%)
	 * 	- 1 : 33.333% : 15 of 20 = 0.75 = 25
	 *  - 2 : 33.333% : 15 of 20 = 0.75 = 25
	 *  - 3 : 33.333% : 15 of 20 = 0.75 = 25
	 *  - EC : 5%	  : 20 of 20 = 1.00 =  5
	 *  - Total : 80 = 32
	 *  
	 * Homework (60%)
	 *  - 1 : 25% : 7.5 of 10 = 0.75 = 18.75
	 *  - 2 : 25% : 7.5 of 10 = 0.75 = 18.75
	 *  - 3 : 25% : 7.5 of 10 = 0.75 = 18.75
	 *  - 4 : 25% : 7.5 of 10 = 0.75 = 18.75
	 *  - Total : 75 = 45
	 * 
	 * Overall grade will be 77
	 * 
	 */
	public void testExtraCreditItem() throws InvalidInputException, FatalException {
		super.testExtraCreditItem();
		checkCourseGrade("C+ (77.00%) ");
	}
	
}
