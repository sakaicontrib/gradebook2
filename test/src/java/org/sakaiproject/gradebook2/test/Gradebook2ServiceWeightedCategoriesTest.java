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

package org.sakaiproject.gradebook2.test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.sakaiproject.gradebook.gwt.client.exceptions.BusinessRuleException;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationModel;
import org.sakaiproject.gradebook.gwt.client.model.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2Service;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class Gradebook2ServiceWeightedCategoriesTest extends TestCase {

	private Gradebook2Service service;
	private ItemModel category;
	private GradebookModel gbModel;

	public Gradebook2ServiceWeightedCategoriesTest(String name) {
		super(name);
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"test.xml", "db.xml"});

		service = (Gradebook2Service)context.getBean("org.sakaiproject.gradebook.gwt.sakai.Gradebook2Service");
	}

	/*
	 * Set a user with all perfect grades and then override his/her course grade to an "F". Then remove that override.
	 */
	public void testAddAndRemoveOverrideGrade() throws Exception {

		int numberOfRows = 19;

		PagingLoadConfig config = new BasePagingLoadConfig(0, numberOfRows);
		PagingLoadResult<StudentModel> learnerResult = service.getStudentRows(gbModel.getGradebookUid(), gbModel.getGradebookId(), config, Boolean.TRUE);

		assertNotNull(learnerResult);

		List<StudentModel> learners = learnerResult.getData();
		assertEquals(numberOfRows, learners.size());

		ModelData firstLearner = learners.get(0);

		assertNull((StudentModel)firstLearner).getStudentGrade());

		List<String> itemIds = new ArrayList<String>(); 
		StudentModel updatedRecord = null;

		int count = 0;
		// Assign all grades to 100
		for (ModelData m : category.getChildren()) {
			ItemModel child = (ItemModel)m;
			itemIds.add(child.getIdentifier());

			Double previousValue = firstLearner.get(child.getIdentifier());
			updatedRecord = service.scoreNumericItem(gbModel.getGradebookUid(), firstLearner, child.getIdentifier(), Double.valueOf(100d), previousValue);

			count++;

			if (count == 4) 
				assertEquals("A+ (100.00%)", updatedRecord.getStudentGrade());
			else
				assertEquals("A+ (100.00%) ***", updatedRecord.getStudentGrade());
		}

		String previousValue = updatedRecord.get(StudentModel.Key.GRADE_OVERRIDE.name());

		updatedRecord = service.scoreTextItem(gbModel.getGradebookUid(), firstLearner, StudentModel.Key.GRADE_OVERRIDE.name(), "F", previousValue);

		assertEquals("F (override)", updatedRecord.getStudentGrade());

		previousValue = updatedRecord.get(StudentModel.Key.GRADE_OVERRIDE.name());

		updatedRecord = service.scoreTextItem(gbModel.getGradebookUid(), firstLearner, StudentModel.Key.GRADE_OVERRIDE.name(), null, previousValue);

		assertEquals("A+ (100.00%)", updatedRecord.getStudentGrade());
	}



	/*
	 * Set item points to 100
	 */
	public void testSetItemPointsTo100() throws Exception {
		Double result = testSetItemPointsTo(Double.valueOf(100d));
		assertEquals(Double.valueOf(100d), result);
	}

	/*
	 * Set item points to 0
	 */
	public void testSetItemPointsTo0() throws Exception {
		Double result = testSetItemPointsTo(Double.valueOf(0d));
		assertEquals(Double.valueOf(0d), result);
	}

	/*
	 * Set item points to 50
	 */
	public void testSetItemPointsTo50() throws Exception {
		Double result = testSetItemPointsTo(Double.valueOf(50d));
		assertEquals(Double.valueOf(50d), result);
	}

	/*
	 * Set item points to 50
	 */
	public void testSetItemPointsToNegative100() throws Exception {
		Double result = testSetItemPointsTo(Double.valueOf(-100d));
		assertEquals(Double.valueOf(-100d), result);
	}


	/*
	 * Test category add business rule #2
	 * 
	 * (2) new category name must not duplicate an existing category name
	 */
	public void testAddDuplicateCategoryName() throws Exception {
		ItemModel essaysCategory = new ItemModel();
		essaysCategory.setName("My Essays");
		essaysCategory.setPercentCourseGrade(Double.valueOf(60d));
		essaysCategory.setDropLowest(Integer.valueOf(0));
		essaysCategory.setEqualWeightAssignments(Boolean.TRUE);
		essaysCategory.setItemType(Type.CATEGORY);
		essaysCategory.setIncluded(Boolean.TRUE);

		boolean isExceptionThrown = false;
		try {
			service.addItemCategory(gbModel.getGradebookUid(), gbModel.getGradebookId(), essaysCategory);
		} catch (BusinessRuleException bre) {
			isExceptionThrown = true;
		}

		assertTrue(isExceptionThrown);
	}

	/*
	 * Test item add business rule #4
	 * 
	 * (4) new item name must not duplicate an active (removed = false) item name in the same category, otherwise throw exception
	 */
	public void testAddDuplicateItemNameWithinCategory() throws Exception {

		ItemModel essay1 = new ItemModel();
		essay1.setName("Essay 1");
		essay1.setPoints(Double.valueOf(20d));
		essay1.setDueDate(new Date());
		essay1.setCategoryId(category.getCategoryId());
		essay1.setReleased(Boolean.TRUE);
		essay1.setItemType(Type.ITEM);
		essay1.setIncluded(Boolean.TRUE);

		boolean isExceptionThrown = false;

		try {
			service.createItem(gbModel.getGradebookUid(), gbModel.getGradebookId(), essay1, true);
		} catch (BusinessRuleException bre) {
			isExceptionThrown = true;
		}

		assertTrue(isExceptionThrown);

	}


	/*
	 * Test item add business rule #5
	 * 
	 * (5) if item is "included" and category has "equal weighting" then recalculate all item weights for this category
	 * (6) item must include a valid category id
	 */
	public void testRecalculateWeightOnNewIncludedItem() throws Exception {

		ItemModel item = new ItemModel();
		item.setName("Essay 5");
		item.setPoints(Double.valueOf(20d));
		item.setDueDate(new Date());
		item.setCategoryId(category.getCategoryId());
		item.setReleased(Boolean.TRUE);
		item.setItemType(Type.ITEM);
		item.setIncluded(Boolean.TRUE);

		ItemModel activeItem = getActiveItem(service.createItem(gbModel.getGradebookUid(), gbModel.getGradebookId(), item, false));
		ItemModel parent = (ItemModel) activeItem.getParent();

		int numberOfItems = 0;
		for (ModelData m : parent.getChildren()) {	
			ItemModel c = (ItemModel)m;
			assertEquals(BigDecimal.valueOf(20.0000d).setScale(4, RoundingMode.HALF_EVEN), BigDecimal.valueOf(c.getPercentCategory().doubleValue()).setScale(4, RoundingMode.HALF_EVEN));
			assertTrue(c.getIncluded());
			numberOfItems++;
		}

		assertEquals(5, numberOfItems);
	}



	/*
	 * Test item update business rule #1
	 * 
	 * (1) If points is null, set points to 100
	 */
	public void testSetItemPointsNull() throws Exception {

		Double result = testSetItemPointsTo(null);
		assertEquals(Double.valueOf(100d), result);
	}


	/*
	 * Test item update business rule #2
	 * 
	 * (2) If weight is null, set weight to be equivalent to points value -- needs to happen after #1
	 */
	public void testSetItemWeightNull() throws Exception {
		// Grab first item from category
		ItemModel item = null;
		for (ModelData m : category.getChildren()) {
			ItemModel child = (ItemModel)m;
			item = child;
			break;
		}

		// FIXME: Potential null pointer dereference
		item.setPercentCategory(null);

		ItemModel parent = service.updateItemModel(item);

		for (ModelData m : parent.getChildren()) {
			ItemModel c = (ItemModel)m;
			if (c.isActive()) {
				assertEquals(Double.valueOf(20d), c.getPercentCategory());
			}
		}	
	}


	/*
	 * Test item update business rule #5
	 * 
	 * (5) new item name must not duplicate an active (removed = false) item name in the same category, otherwise throw exception
	 */
	public void testUpdateDuplicateItemNameInCategory() throws Exception {

		ItemModel item = getFirstItemInCategory(category);
		assertEquals("Essay 1", item.getName());

		item.setName("Essay 2");

		boolean isExceptionThrown = false;

		try {
			service.updateItemModel(item);
		} catch (BusinessRuleException bre) {
			isExceptionThrown = true;
		}

		assertTrue(isExceptionThrown);
	}

	/*
	 * Test item update business rule #6 (part a)
	 * 
	 * (6a) must not include an item in grading that has been deleted (removed = true) 
	 */
	public void testIncludeDeletedItem() throws Exception {
		// Grab first item from category
		ItemModel item = getFirstItemInCategory(category);

		item.setRemoved(Boolean.TRUE);

		ItemModel activeItem = getActiveItem(service.updateItemModel(item));
		assertNull(activeItem);

		item.setIncluded(Boolean.TRUE);

		boolean isExceptionThrown = false;

		try {
			activeItem = getActiveItem(service.updateItemModel(item));
		} catch (BusinessRuleException bre) {
			isExceptionThrown = true;
		}

		assertTrue(isExceptionThrown);
	}

	/*
	 * Test item update business rule #6 (part b)
	 * 
	 * (6b) must not include an item in grading that has a category that has been deleted (removed = true)
	 */
	// FIXME: This is really an anomolous case that may not need testing, since the item itself will not appear after it's category has been deleted
	/*public void testIncludeItemFromDeletedCategory() throws Exception {
		// First, ensure that category is deleted
		category.setRemoved(Boolean.TRUE);
		ItemModel item = getFirstItemInCategory(category);

		item.setIncluded(Boolean.TRUE);

		boolean isExceptionThrown = false;

		try {
			getActiveItem(service.updateItemModel(item));
		} catch (BusinessRuleException bre) {
			isExceptionThrown = true;
		}

		assertTrue(isExceptionThrown);
	}*/


	/*
	 * Test item update business rule #
	 * 
	 * (7) if item is "included" and category has "equal weighting" then recalculate all item weights for this category
	 */
	public void testRecalculateItemWeightsOnIncludedOrUnincludedItem() throws Exception {

		// Grab first item from category
		ItemModel item = getFirstItemInCategory(category);
		assertTrue(item.getIncluded());
		item.setIncluded(Boolean.FALSE);

		ItemModel parent = service.updateItemModel(item);

		for (ModelData m : parent.getChildren()) {
			ItemModel c = (ItemModel)m;
			if (!c.isActive()) {
				assertEquals(BigDecimal.valueOf(33.3333d).setScale(4, RoundingMode.HALF_EVEN), BigDecimal.valueOf(c.getPercentCategory().doubleValue()).setScale(4, RoundingMode.HALF_EVEN));
				assertTrue(c.getIncluded());
			} else {
				assertEquals(BigDecimal.valueOf(0.0000d).setScale(4, RoundingMode.HALF_EVEN), BigDecimal.valueOf(c.getPercentCategory().doubleValue()).setScale(4, RoundingMode.HALF_EVEN));
				assertFalse(c.getIncluded());
			}
		}
	}

	/*
	 * Test item update business rule #8
	 * 
	 * (8) item must include a valid category id
	 */
	// This one is no longer true, as such, since we allow unassigned items in certain cases
	/*public void testItemMustIncludeCategoryId() throws Exception {
		// Grab first item from category
		ItemModel item = getFirstItemInCategory(category);
		assertNotNull(item.getCategoryId());
		item.setCategoryId(null);

		boolean isExceptionThrown = false;

		try {
			getActiveItem(service.updateItemModel(item));
		} catch (BusinessRuleException bre) {
			isExceptionThrown = true;
		}

		assertTrue(isExceptionThrown);
	}*/


	/*
	 * Test item update business rule #9
	 * 
	 * (9) if category has changed, then if the old category had equal weighting and the item was included in that category, then recalculate all item weights for that category
	 */
	public void testOldCategoryOfMovedItemWithEqualWeightsMustBeRecalculated() throws Exception {
		// Make sure that the test category has equal weight items
		assertTrue(category.getEqualWeightAssignments());

		// Create a new target category to move the first item to
		ItemModel hwCategory = new ItemModel();
		hwCategory.setName("Homework");
		hwCategory.setPercentCourseGrade(Double.valueOf(60d));
		hwCategory.setDropLowest(Integer.valueOf(0));
		hwCategory.setEqualWeightAssignments(Boolean.TRUE);
		hwCategory.setItemType(Type.CATEGORY);
		hwCategory.setIncluded(Boolean.TRUE);

		ItemModel destinationCategory = getActiveItem(service.addItemCategory(gbModel.getGradebookUid(), gbModel.getGradebookId(), hwCategory));

		// Grab first item from category
		ItemModel item = getFirstItemInCategory(category);
		assertNotNull(item.getCategoryId());

		// Move the item to the destinationCategory
		item.setCategoryId(destinationCategory.getCategoryId());

		ItemModel movedItem = getActiveItem(service.updateItemModel(item));

		// Make sure it got correctly moved
		assertNotNull(movedItem);
		assertNotNull(movedItem.getCategoryId());
		assertNotNull(destinationCategory.getCategoryId());
		assertEquals(movedItem.getCategoryId(), destinationCategory.getCategoryId());

		// Now grab the old category again
		GradebookModel freshInstanceOfGradebookModel = service.getGradebook(gbModel.getGradebookUid());

		boolean foundCategory = false;
		// Search for the old category in the fresh gradebook model instance
		for (ModelData m : freshInstanceOfGradebookModel.getGradebookItemModel().getChildren()) {
			ItemModel child = (ItemModel)m;
			if (child.getCategoryId().equals(category.getCategoryId())) {
				for (ModelData m2 : child.getChildren()) {
					ItemModel c = (ItemModel)m2;
					assertEquals(BigDecimal.valueOf(33.3333d).setScale(4, RoundingMode.HALF_EVEN), BigDecimal.valueOf(c.getPercentCategory().doubleValue()).setScale(4, RoundingMode.HALF_EVEN));
					assertTrue(c.getIncluded());
				}

				foundCategory = true;
			}
		}

		assertTrue(foundCategory);

	}


	/*
	 * Test item update business rule #10
	 * 
	 * (10) if item weight changes then remove the equal weighting flag (set to false) for the owning category
	 * 
	 * !!!This business rule has been removed!!!
	 * 
	 */
	/*public void testRemoveCategoryEqualWeightWhenItemWeightChanges() throws Exception {
		// Make sure that the test category has equal weight items
		assertTrue(category.getEqualWeightAssignments());
		ItemModel item = getFirstItemInCategory(category);
		item.setPercentCategory(Double.valueOf(100d));

		ItemModel categoryItemModel = service.updateItemModel(item);
		ItemModel updatedItem = getActiveItem(categoryItemModel);

		assertEquals(Type.CATEGORY, categoryItemModel.getItemType());
		assertEquals(categoryItemModel.getCategoryId(), updatedItem.getCategoryId());
		assertEquals(Double.valueOf(100d), updatedItem.getPercentCategory());
		assertFalse(categoryItemModel.getEqualWeightAssignments());
		assertEquals(Double.valueOf(175d), categoryItemModel.getPercentCategory());
	}*/


	/*
	 * Test item update business rule #11
	 * 
	 * (11) if category is not included, then cannot include item
	 */
	public void testIncludeItemFromUnincludedCategory() throws Exception {
		// First, ensure that category is included
		assertTrue(category.getIncluded());
		category.setIncluded(Boolean.FALSE);

		ItemModel unincludedCategory = getActiveItem(service.updateItemModel(category));
		assertFalse(unincludedCategory.getIncluded());

		ItemModel item = getFirstItemInCategory(unincludedCategory);

		assertFalse(item.getIncluded());
		item.setIncluded(Boolean.TRUE);

		boolean isExceptionThrown = false;

		try {
			getActiveItem(service.updateItemModel(item));
		} catch (BusinessRuleException bre) {
			isExceptionThrown = true;
		}

		assertTrue(isExceptionThrown);
	}


	/*
	 * Test item update business rule #12
	 * 
	 * (12) if category is removed, then cannot unremove item
	 */
	// FIXME: This is really an anomolous case that may not need testing, since the item itself will not appear after it's category has been deleted
	public void testUnremoveItemFromRemovedCategory() throws Exception {
		// First, ensure that category is not removed
		assertFalse(category.getRemoved());
		// Remove it
		category.setRemoved(Boolean.TRUE);

		ItemModel removedCategory = getActiveItem(service.updateItemModel(category));
		assertNull(removedCategory);
	}


	/*
	 * Test add category business rule #1 (on every setup)
	 * (1) if no other categories exist, then make the category weight 100%
	 * 
	 */
	protected void setUp() throws Exception {
		super.setUp();

		ApplicationModel applicationModel = service.getApplicationModel(getName());

		gbModel = applicationModel.getGradebookModels().get(0);
		
		ItemModel gradebookItemModel = gbModel.getGradebookItemModel();
		gradebookItemModel.setGradeType(GradeType.PERCENTAGES);
		gradebookItemModel.setCategoryType(CategoryType.WEIGHTED_CATEGORIES);
		service.updateItemModel(gradebookItemModel);


		String gradebookUid = gbModel.getGradebookUid();
		Long gradebookId = gbModel.getGradebookId();

		ItemModel essaysCategory = new ItemModel();
		essaysCategory.setName("My Essays");
		essaysCategory.setPercentCourseGrade(Double.valueOf(100d));
		essaysCategory.setDropLowest(Integer.valueOf(0));
		essaysCategory.setEqualWeightAssignments(Boolean.TRUE);
		essaysCategory.setItemType(Type.CATEGORY);
		essaysCategory.setIncluded(Boolean.TRUE);
		ItemModel addedCategory = getActiveItem(service.addItemCategory(gradebookUid, gradebookId, essaysCategory));

		assertEquals(Type.CATEGORY, addedCategory.getItemType());
		assertEquals(Double.valueOf(100d), addedCategory.getPercentCourseGrade());

		ItemModel essay1 = new ItemModel();
		essay1.setName("Essay 1");
		essay1.setPoints(Double.valueOf(20d));
		essay1.setDueDate(new Date());
		essay1.setCategoryId(addedCategory.getCategoryId());
		essay1.setReleased(Boolean.TRUE);
		essay1.setItemType(Type.ITEM);
		essay1.setIncluded(Boolean.TRUE);
		service.createItem(gradebookUid, gradebookId, essay1, true);

		ItemModel essay2 = new ItemModel();
		essay2.setName("Essay 2");
		essay2.setPoints(Double.valueOf(20d));
		essay2.setDueDate(new Date());
		essay2.setCategoryId(addedCategory.getCategoryId());
		essay2.setReleased(Boolean.TRUE);
		essay2.setItemType(Type.ITEM);
		essay2.setIncluded(Boolean.TRUE);
		service.createItem(gradebookUid, gradebookId, essay2, true);

		ItemModel essay3 = new ItemModel();
		essay3.setName("Essay 3");
		essay3.setPoints(Double.valueOf(20d));
		essay3.setDueDate(new Date());
		essay3.setCategoryId(addedCategory.getCategoryId());
		essay3.setReleased(Boolean.TRUE);
		essay3.setItemType(Type.ITEM);
		essay3.setIncluded(Boolean.TRUE);
		service.createItem(gradebookUid, gradebookId, essay3, true);

		ItemModel essay4 = new ItemModel();
		essay4.setName("Essay 4");
		essay4.setPoints(Double.valueOf(20d));
		essay4.setDueDate(new Date());
		essay4.setCategoryId(addedCategory.getCategoryId());
		essay4.setReleased(Boolean.TRUE);
		essay4.setItemType(Type.ITEM);
		essay4.setIncluded(Boolean.TRUE);

		essay4 = getActiveItem(service.createItem(gradebookUid, gradebookId, essay4, true));

		category = (ItemModel) essay4.getParent();
		
		for (ModelData m : category.getChildren()) {
			ItemModel child = (ItemModel)m;
			Double percentCategory = child.getPercentCategory();
			BigDecimal pC = BigDecimal.valueOf(percentCategory.doubleValue());

			assertTrue(pC.setScale(2).compareTo(BigDecimal.valueOf(25.0)) == 0);
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	private Double testSetItemPointsTo(Double value) throws Exception {

		// Grab first item from category
		ItemModel item = getFirstItemInCategory(category);

		item.setPoints(value);

		ItemModel activeItem = getActiveItem(service.updateItemModel(item));
		assertNotNull(activeItem);

		return activeItem.getPoints();
	}

	private ItemModel getActiveItem(ItemModel parent) {
		if (parent.isActive())
			return parent;

		for (ModelData m : parent.getChildren()) {
			ItemModel c = (ItemModel)m;
			if (c.isActive()) {
				return c;
			}

			if (c.getChildCount() > 0) {
				ItemModel activeItem = getActiveItem(c);

				if (activeItem != null)
					return activeItem;
			}
		}

		return null;
	}

	private ItemModel getFirstItemInCategory(ItemModel category) {
		for (ModelData child : category.getChildren()) {
			return (ItemModel) child;
		}

		return null;
	}

}
