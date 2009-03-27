package org.sakaiproject.gradebook.gwt.test.mock;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.sakaiproject.gradebook.gwt.client.GradebookToolFacade;
import org.sakaiproject.gradebook.gwt.client.action.PageRequestAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityGetAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction.ClassType;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.MultiGradeLoadConfig;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.GradeType;
import org.sakaiproject.gradebook.gwt.sakai.mock.GradebookToolFacadeMockImpl;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public abstract class AbstractToolFacadeTest extends TestCase {

	protected enum ScoreType { PERFECT_SCORE, ZERO, NEGATIVE, MEDIOCRE };
	
	protected GradebookToolFacade facade = null;
	
	protected GradebookModel gbModel;
	protected ItemModel essaysCategory, hwCategory;
	protected Map<String, ItemModel> assignmentMap;
	protected ItemModel essay1, essay2, essay3, hw1, hw2, hw3, hw4;
	
	
	/*
	 * If a learner gets 75% on all grade items, then his/her course grade should be 75%
	 * 
	 * Points: 15 + 15 + 15 + 7.5 + 7.5 + 7.5 + 7.5 = 75
	 * 
	 */
	public void testMediocreScores() throws InvalidInputException, FatalException {
		gradeAllSameScore(ScoreType.MEDIOCRE, "C (75.00%) ");
	}
	
	/*
	 * If a learner gets 75% on all grade items, then his/her course grade should be 75%
	 * even when we drop the lowest essay
	 */
	public void testMediocreScoresDropLowestEssay() throws InvalidInputException, FatalException {
		essaysCategory = makeCategoryDropLowest(essaysCategory, 1);
		gradeAllSameScore(ScoreType.MEDIOCRE, "C (75.00%) ");
	}
	
	/*
	 * Want to ensure that even if we make the drop lowest value negative, it doesn't cause an 
	 * exception to be thrown
	 */
	public void testNegativeDropLowestEssay() throws InvalidInputException, FatalException  {
		essaysCategory = makeCategoryDropLowest(essaysCategory, -1);
		gradeAllSameScore(ScoreType.MEDIOCRE, "C (75.00%) ");
	}
	
	/*
	 * If a learner gets 100% on all grade items, then his/her course grade should be 100%
	 */
	public void testPerfectScores() throws InvalidInputException, FatalException {
		gradeAllSameScore(ScoreType.PERFECT_SCORE, "A+ (100.00%) ");
	}
	
	/*
	 * If a learner gets 100% on all grade items, then his/her course grade should be 100%
	 * even when we drop the lowest essay
	 */
	public void testPerfectScoresDropLowestEssay() throws InvalidInputException, FatalException {
		essaysCategory = makeCategoryDropLowest(essaysCategory, 1);
		gradeAllSameScore(ScoreType.PERFECT_SCORE, "A+ (100.00%) ");
	}
	
	/*
	 * If a learner gets 100% on all grade items, then his/her course grade should be 100%
	 * even when homework is an extra credit category worth +10%
	 */
	public void testPerfectScoresExtraCreditHomework() throws InvalidInputException, FatalException {
		makeHomeworkTenPercentExtraCredit();
		gradeAllSameScore(ScoreType.PERFECT_SCORE, "A+ (100.00%) ");
	}
	
	/*
	 * If a learner gets 0% on all grade items, then his/her course grade should be 0%
	 */
	public void testZeroScores() throws InvalidInputException, FatalException {
		gradeAllSameScore(ScoreType.ZERO, "F (0.00%) ");
	}
	
	/*
	 * If a learner gets 0% on all grade items, then his/her course grade should be 0%
	 * even when we drop the lowest essay
	 */
	public void testZeroScoresDropLowestEssay() throws InvalidInputException, FatalException {
		essaysCategory = makeCategoryDropLowest(essaysCategory, 1);
		gradeAllSameScore(ScoreType.ZERO, "F (0.00%) ");
	}
	
	/*
	 * If a learner gets 0% on all grade items, then his/her course grade should be 0%
	 * even when homework is an extra credit category worth +10%
	 */
	public void testZeroScoresExtraCreditHomework() throws InvalidInputException, FatalException {
		makeHomeworkTenPercentExtraCredit();
		gradeAllSameScore(ScoreType.ZERO, "F (0.00%) ");
	}
	
	public void testGetGradeItems() throws InvalidInputException, FatalException {
		PageRequestAction action = new PageRequestAction(EntityType.GRADE_ITEM, gbModel.getGradebookUid(), gbModel.getGradebookId());
		PagingLoadConfig config = new MultiGradeLoadConfig();
		config.setOffset(0);
		config.setLimit(5);
		PagingLoadResult<AssignmentModel> result = facade.getEntityPage(action, config);
		
		List<AssignmentModel> gradeItems = result.getData();
		
		assertEquals(5, gradeItems.size());
	}
	
	public void testGetGradeItemsEqualWeighting() throws InvalidInputException, FatalException {
		essaysCategory = makeCategoryEqualWeighting(essaysCategory, true);
		
		PageRequestAction action = new PageRequestAction(EntityType.GRADE_ITEM, gbModel.getGradebookUid(), gbModel.getGradebookId());
		PagingLoadConfig config = new MultiGradeLoadConfig();
		config.setOffset(0);
		config.setLimit(10);
		PagingLoadResult<AssignmentModel> result = facade.getEntityPage(action, config);
		
		List<AssignmentModel> gradeItems = result.getData();
		
		assertEquals(7, gradeItems.size());
		
		for (AssignmentModel gradeItem : gradeItems) {
			// Only look at 'essays'
			if (gradeItem.getCategoryId().equals(Long.valueOf(essaysCategory.getIdentifier()))) {
				Double weighting = gradeItem.getWeighting();
				assertEquals(Double.valueOf(33.33333333333333d), weighting);
			}
		}
	}
	
	/* 
	 *
	 */
	/*public void testExtraCreditCategory() throws InvalidInputException, FatalException {
		// Start by grading everything as mediocre
		gradeAllSameScore(ScoreType.MEDIOCRE, "C (75.00%) ");
		
		CategoryModel ecCategory = facade.createEntity(new UserCategoryCreateAction(gbModel, "Extra Credit", 
				Double.valueOf(10d), Boolean.TRUE, Integer.valueOf(0)));
		
		AssignmentModel ec1 = facade.createEntity(new UserAssignmentCreateAction(gbModel, 
				Long.valueOf(ecCategory.getIdentifier()), 
				"Extra Credit 1", Double.valueOf(0), Double.valueOf(20), new Date()));
		assignmentMap.put(ec1.getIdentifier(), ec1);
		
		gradeCategorySameScore(ecCategory, ScoreType.PERFECT_SCORE);
		
		checkCourseGrade("B (85.00%) ");
	}*/
	
	/*
	// FIXME : Need a test that checks for drop lowest in points-based, where one assignment is 225 and the other 100
	public void testDropLowest() throws InvalidInputException, FatalException {
		
		
		
	}*/
	
	
	
	/*
	 * We expect that deleting the category will hide all of its grade items (the exception to this is
	 * when category type is "no categories" -- see the ToolFacadeNoCategories*Test.java classes for 
	 * override) so we will only see 4 items (the homework items).
	 */
	public void testGetGradeItemsEssaysDeleted() throws InvalidInputException, FatalException {
		essaysCategory = makeCategoryDeleted(essaysCategory, true);
		
		PageRequestAction action = new PageRequestAction(EntityType.GRADE_ITEM, gbModel.getGradebookUid(), gbModel.getGradebookId());
		PagingLoadConfig config = new MultiGradeLoadConfig();
		config.setOffset(0);
		config.setLimit(5);
		PagingLoadResult<AssignmentModel> result = facade.getEntityPage(action, config);
		
		List<AssignmentModel> gradeItems = result.getData();
		
		assertEquals(4, gradeItems.size());
	}
	
	public void testGetCategories() throws InvalidInputException, FatalException {
		PageRequestAction action = new PageRequestAction(EntityType.CATEGORY, gbModel.getGradebookUid(), gbModel.getGradebookId());
		PagingLoadConfig config = new MultiGradeLoadConfig();
		config.setOffset(0);
		config.setLimit(10);
		PagingLoadResult<CategoryModel> result = facade.getEntityPage(action, config);
		
		List<CategoryModel> categories = result.getData();
		
		assertEquals(2, categories.size());
	}
	
	/*
	 * If we delete the essays categories, then the page should only show 1 category
	 */
	public void testGetCategoriesEssaysDeleted() throws InvalidInputException, FatalException {
		essaysCategory = makeCategoryDeleted(essaysCategory, true);
		PageRequestAction action = new PageRequestAction(EntityType.CATEGORY, gbModel.getGradebookUid(), gbModel.getGradebookId());
		PagingLoadConfig config = new MultiGradeLoadConfig();
		config.setOffset(0);
		config.setLimit(10);
		PagingLoadResult<CategoryModel> result = facade.getEntityPage(action, config);
		
		List<CategoryModel> categories = result.getData();
		
		assertEquals(1, categories.size());
	}
	
	/*
	 * Overall grades will be points based 15 + 15 + 15 + 7.5 + 7.5 + 7.5 + 7.5 (+ 20) = 95 out of 100
	 * or with weighted categories:
	 * 75% for all non-extra credit + 10% extra credit category = 85%
	 * 
	 */
	public void testExtraCreditCategory() throws InvalidInputException, FatalException {
		// Start by grading everything as mediocre
		gradeAllSameScore(ScoreType.MEDIOCRE, "C (75.00%) ");
		
		CategoryModel ecCategory = facade.createEntity(new UserCategoryCreateAction(gbModel, "Extra Credit", 
				Double.valueOf(10d), Boolean.TRUE, Integer.valueOf(0)));
		ecCategory = makeCategoryExtraCredit(ecCategory, true);
		
		AssignmentModel ec1 = facade.createEntity(new UserAssignmentCreateAction(gbModel, 
				Long.valueOf(ecCategory.getIdentifier()), 
				"Extra Credit 1", Double.valueOf(100), Double.valueOf(20), new Date()));
		assignmentMap.put(ec1.getIdentifier(), ec1);
		
		gradeCategorySameScore(ecCategory, ScoreType.PERFECT_SCORE);
	}
	
	/*
	 * Overall grades will be points based 15 + 15 + 15 + 7.5 + 7.5 + 7.5 + 7.5 (+ 15) = 90 out of 100
	 * or with weighted categories:
	 * 75% for all non-extra credit + 7.5% extra credit category = 82.5%
	 * 
	 */
	public void testExtraCreditCategoryPartial() throws InvalidInputException, FatalException {
		// Start by grading everything as mediocre
		gradeAllSameScore(ScoreType.MEDIOCRE, "C (75.00%) ");
		
		CategoryModel ecCategory = facade.createEntity(new UserCategoryCreateAction(gbModel, "Extra Credit", 
				Double.valueOf(10d), Boolean.TRUE, Integer.valueOf(0)));
		ecCategory = makeCategoryExtraCredit(ecCategory, true);
		
		AssignmentModel ec1 = facade.createEntity(new UserAssignmentCreateAction(gbModel, 
				Long.valueOf(ecCategory.getIdentifier()), 
				"Extra Credit 1", Double.valueOf(100), Double.valueOf(20), new Date()));
		assignmentMap.put(ec1.getIdentifier(), ec1);
		
		gradeCategorySameScore(ecCategory, ScoreType.MEDIOCRE);
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
	 * 
	 * With points based:
	 * 15 + 15 + 15 (+20) + 7.5 + 7.5 + 7.5 + 7.5 = 95 out of 100
	 * 
	 */
	public void testExtraCreditItem() throws InvalidInputException, FatalException {
		// Start by grading everything as mediocre
		gradeAllSameScore(ScoreType.MEDIOCRE, "C (75.00%) ");
		
		AssignmentModel ec1 = facade.createEntity(new UserAssignmentCreateAction(gbModel, 
				Long.valueOf(essaysCategory.getIdentifier()), 
				"Extra Credit Essay", Double.valueOf(20), Double.valueOf(20), new Date()));
		assignmentMap.put(ec1.getIdentifier(), ec1);
		
		ec1 = makeItemExtraCredit(ec1, true);
		ec1 = setItemWeight(ec1, Double.valueOf(5d));
		
		gradeItem(ec1, ScoreType.PERFECT_SCORE);
	}
	
	
	protected abstract void initialize() throws InvalidInputException, FatalException;
	
	protected void initialize(CategoryType categoryType, GradeType gradeType) throws InvalidInputException, FatalException {
		facade = new GradebookToolFacadeMockImpl();
		
		assignmentMap = new HashMap<String, AssignmentModel>();
		
		gbModel = facade.getEntity(new UserEntityGetAction<GradebookModel>(EntityType.GRADEBOOK, "emptyid"));
		
		gbModel = facade.updateEntity(new UserEntityUpdateAction<GradebookModel>(gbModel, gbModel, GradebookModel.Key.NAME.name(), ClassType.STRING, "Test Gradebook", null));
		gbModel = facade.updateEntity(new UserEntityUpdateAction<GradebookModel>(gbModel, gbModel, GradebookModel.Key.CATEGORYTYPE.name(), ClassType.CATEGORYTYPE, categoryType, null));
		gbModel = facade.updateEntity(new UserEntityUpdateAction<GradebookModel>(gbModel, gbModel, GradebookModel.Key.GRADETYPE.name(), ClassType.GRADETYPE, gradeType, null));
		
		essaysCategory = facade.createEntity(new UserCategoryCreateAction(gbModel, "My Essays", 
				Double.valueOf(40d), Boolean.TRUE, Integer.valueOf(0)));
		
		hwCategory = facade.createEntity(new UserCategoryCreateAction(gbModel, "My Homework", 
				Double.valueOf(60d), Boolean.TRUE, Integer.valueOf(0)));
		
		
		essay1 = facade.createEntity(new UserAssignmentCreateAction(gbModel, 
				Long.valueOf(essaysCategory.getIdentifier()), 
				"Essay 1", Double.valueOf(0), Double.valueOf(20), new Date()));
		assignmentMap.put(essay1.getIdentifier(), essay1);
		
		essay2 = facade.createEntity(new UserAssignmentCreateAction(gbModel, 
				Long.valueOf(essaysCategory.getIdentifier()), 
				"Essay 2", Double.valueOf(0), Double.valueOf(20), new Date()));
		assignmentMap.put(essay2.getIdentifier(), essay2);
		
		essay3 = facade.createEntity(new UserAssignmentCreateAction(gbModel, 
				Long.valueOf(essaysCategory.getIdentifier()), 
				"Essay 3", Double.valueOf(0), Double.valueOf(20), new Date()));
		assignmentMap.put(essay3.getIdentifier(), essay3);
		
		hw1 = facade.createEntity(new UserAssignmentCreateAction(gbModel, 
				Long.valueOf(hwCategory.getIdentifier()), 
				"HW 1", Double.valueOf(0), Double.valueOf(10), new Date()));
		assignmentMap.put(hw1.getIdentifier(), hw1);
		
		hw2 = facade.createEntity(new UserAssignmentCreateAction(gbModel, 
				Long.valueOf(hwCategory.getIdentifier()), 
				"HW 2", Double.valueOf(0), Double.valueOf(10), new Date()));
		assignmentMap.put(hw2.getIdentifier(), hw2);
		
		hw3 = facade.createEntity(new UserAssignmentCreateAction(gbModel, 
				Long.valueOf(hwCategory.getIdentifier()), 
				"HW 3", Double.valueOf(0), Double.valueOf(10), new Date()));
		assignmentMap.put(hw3.getIdentifier(), hw3);
		
		hw4 = facade.createEntity(new UserAssignmentCreateAction(gbModel, 
				Long.valueOf(hwCategory.getIdentifier()), 
				"HW 4", Double.valueOf(0), Double.valueOf(10), new Date()));
		assignmentMap.put(hw4.getIdentifier(), hw4);
		
		((GradebookToolFacadeMockImpl)facade).setNumberOfTestLearners(1);
	}

	protected void setUp() throws InvalidInputException, FatalException {
		initialize();
	}
	
	protected void tearDown() {
		facade = null;
		
		gbModel = null;
		essaysCategory = null; 
		hwCategory = null;
		
		assignmentMap.clear();
		assignmentMap = null;
		
		essay1 = null;
		essay2 = null;
		essay3 = null;
		
		hw1 = null;
		hw2 = null;
		hw3 = null;
		hw4 = null;
	}
	
	protected void makeHomeworkTenPercentExtraCredit() throws InvalidInputException, FatalException {
		// Start by setting the hw category to extra credit
		hwCategory = facade.updateItemEntity(new UserEntityUpdateAction<ItemModel>(gbModel, hwCategory, CategoryModel.Key.EXTRA_CREDIT.name(), ClassType.BOOLEAN, Boolean.TRUE, Boolean.FALSE));
		
		// Reweight the categories to 100% (Essays) and 10% (Homework)
		essaysCategory = facade.updateItemEntity(new UserEntityUpdateAction<ItemModel>(gbModel, essaysCategory, CategoryModel.Key.WEIGHT.name(), ClassType.DOUBLE, Double.valueOf(100d), Double.valueOf(60d)));
		hwCategory = facade.updateItemEntity(new UserEntityUpdateAction<ItemModel>(gbModel, hwCategory, CategoryModel.Key.WEIGHT.name(), ClassType.DOUBLE, Double.valueOf(10d), Double.valueOf(40d)));
	}
	
	protected ItemModel makeCategoryDropLowest(ItemModel category, int dropLowest) throws InvalidInputException, FatalException {
		return facade.updateItemEntity(new UserEntityUpdateAction<ItemModel>(gbModel, category, CategoryModel.Key.DROP_LOWEST.name(), ClassType.INTEGER, Integer.valueOf(dropLowest), Integer.valueOf(0)));
	}
	
	protected CategoryModel makeCategoryEqualWeighting(ItemModel category, boolean isEqual) throws InvalidInputException, FatalException {
		return facade.updateEntity(new UserEntityUpdateAction<ItemModel>(gbModel, category, CategoryModel.Key.EQUAL_WEIGHT.name(), ClassType.BOOLEAN,  Boolean.valueOf(isEqual), Boolean.valueOf(!isEqual)));
	}
	
	protected CategoryModel makeCategoryDeleted(CategoryModel category, boolean isRemoved) throws InvalidInputException, FatalException {
		return facade.updateEntity(new UserEntityUpdateAction<CategoryModel>(gbModel, category, CategoryModel.Key.REMOVED.name(), ClassType.BOOLEAN, Boolean.valueOf(isRemoved), Boolean.valueOf(!isRemoved)));
	}
	
	protected CategoryModel makeCategoryExtraCredit(CategoryModel category, boolean isExtraCredit) throws InvalidInputException, FatalException {
		return facade.updateEntity(new UserEntityUpdateAction<CategoryModel>(gbModel, category, CategoryModel.Key.EXTRA_CREDIT.name(), ClassType.BOOLEAN, Boolean.valueOf(isExtraCredit), Boolean.valueOf(!isExtraCredit)));
	}
	
	protected AssignmentModel makeItemExtraCredit(AssignmentModel item, boolean isExtraCredit) throws InvalidInputException, FatalException {
		return facade.updateEntity(new UserEntityUpdateAction<AssignmentModel>(gbModel, item, AssignmentModel.Key.EXTRA_CREDIT.name(), ClassType.BOOLEAN, Boolean.valueOf(isExtraCredit), Boolean.valueOf(!isExtraCredit)));
	}
	
	protected AssignmentModel setItemWeight(AssignmentModel item, Double weight) throws InvalidInputException, FatalException {
		return facade.updateEntity(new UserEntityUpdateAction<AssignmentModel>(gbModel, item, AssignmentModel.Key.WEIGHT.name(), ClassType.DOUBLE, weight, null));
	}
	
	protected void gradeAllSameScore(ScoreType outlier, String expectedCourseGrade) throws InvalidInputException, FatalException {
		PagingLoadConfig loadConfig = new MultiGradeLoadConfig();
		loadConfig.setOffset(0);
		loadConfig.setLimit(20);
		PagingLoadResult<StudentModel> learnerResult = facade.getEntityPage(new PageRequestAction(EntityType.STUDENT, "emptyid", Long.valueOf(1l)), loadConfig);
		
		for (StudentModel learner : learnerResult.getData()) {
			Map<String, Object> properties = learner.getProperties();
			
			StudentModel result = null;
			for (String name : properties.keySet()) {
				try {
					Long.parseLong(name);
			
					result = gradeLearner(outlier, learner, name);
				} catch (NumberFormatException nfe) {
					// We can safely ignore these exceptions -- we expect to get many of them here
				}
			}
			assertEquals(expectedCourseGrade, result.getStudentGrade());
		}
	}
	
	protected void gradeCategorySameScore(CategoryModel category, ScoreType outlier) throws InvalidInputException, FatalException {
		PagingLoadConfig loadConfig = new MultiGradeLoadConfig();
		loadConfig.setOffset(0);
		loadConfig.setLimit(20);
		PagingLoadResult<StudentModel> learnerResult = facade.getEntityPage(new PageRequestAction(EntityType.STUDENT, "emptyid", Long.valueOf(1l)), loadConfig);
		
		for (StudentModel learner : learnerResult.getData()) {
			Map<String, Object> properties = learner.getProperties();
			
			StudentModel result = null;
			for (String name : properties.keySet()) {
				try {
					Long.parseLong(name);
			
					AssignmentModel assignment = assignmentMap.get(name);
					
					if (assignment.getCategoryId().equals(Long.valueOf(category.getIdentifier()))) {
						result = gradeLearner(outlier, learner, name);
					}
				} catch (NumberFormatException nfe) {
					// We can safely ignore these exceptions -- we expect to get many of them here
				}
			}
		}
	}
	
	protected void gradeItem(AssignmentModel assignment, ScoreType outlier) throws InvalidInputException, FatalException {
		PagingLoadConfig loadConfig = new MultiGradeLoadConfig();
		loadConfig.setOffset(0);
		loadConfig.setLimit(20);
		PagingLoadResult<StudentModel> learnerResult = facade.getEntityPage(new PageRequestAction(EntityType.STUDENT, "emptyid", Long.valueOf(1l)), loadConfig);
		
		for (StudentModel learner : learnerResult.getData()) {
			Map<String, Object> properties = learner.getProperties();
			
			StudentModel result = null;
			for (String name : properties.keySet()) {
				try {
					Long.parseLong(name);
			
					if (assignment.getIdentifier().equals(name)) {
						result = gradeLearner(outlier, learner, name);
					}
				} catch (NumberFormatException nfe) {
					// We can safely ignore these exceptions -- we expect to get many of them here
				}
			}
		}
	}
	
	protected void checkCourseGrade(String expectedCourseGrade) throws InvalidInputException, FatalException {
		PagingLoadConfig loadConfig = new MultiGradeLoadConfig();
		loadConfig.setOffset(0);
		loadConfig.setLimit(20);
		PagingLoadResult<StudentModel> learnerResult = facade.getEntityPage(new PageRequestAction(EntityType.STUDENT, "emptyid", Long.valueOf(1l)), loadConfig);
		
		for (StudentModel learner : learnerResult.getData()) {
			
			assertEquals(expectedCourseGrade, learner.getStudentGrade());
		}
	}
	
	
	
	protected StudentModel gradeLearner(ScoreType outlier, StudentModel learner, String name) throws InvalidInputException, FatalException {
		Double score = null;
		switch (outlier) {
		case MEDIOCRE:
			switch (gbModel.getGradeType()) {
			case POINTS:
				AssignmentModel assignment = assignmentMap.get(name);
				score = Double.valueOf(assignment.getPoints().doubleValue() * 0.75);
				break;
			case PERCENTAGES:
				score = Double.valueOf(75d);
				break;
			}
			break;
		case PERFECT_SCORE:
			switch (gbModel.getGradeType()) {
			case POINTS:
				AssignmentModel assignment = assignmentMap.get(name);
				score = assignment.getPoints();
				break;
			case PERCENTAGES:
				score = Double.valueOf(100d);
				break;
			}
			break;
		case ZERO:
			score = Double.valueOf(0d);
			break;
		case NEGATIVE:
			score = Double.valueOf(-1d);
			break;
		}
		
		return facade.updateEntity(new UserEntityUpdateAction<StudentModel>(gbModel, learner, name, ClassType.DOUBLE, score, null));
	}
	
	
}
