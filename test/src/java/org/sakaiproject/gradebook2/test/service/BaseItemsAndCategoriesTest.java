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

package org.sakaiproject.gradebook2.test.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.sakaiproject.gradebook.gwt.client.exceptions.BusinessRuleException;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;

import com.extjs.gxt.ui.client.data.ModelData;

public abstract class BaseItemsAndCategoriesTest extends AbstractServiceTest {

	public BaseItemsAndCategoriesTest(String name) {
		super(name);
	}


	public void testAddHomeworkCategoryAndItems() throws Exception {

		onSetup();

		String gradebookUid = gbModel.getGradebookUid();
		Long gradebookId = gbModel.getGradebookId();

		ItemModel hwCategory = new ItemModel();
		hwCategory.setName("My Homework");
		hwCategory.setPercentCourseGrade(Double.valueOf(40d));
		hwCategory.setDropLowest(Integer.valueOf(0));
		hwCategory.setEqualWeightAssignments(Boolean.FALSE);
		hwCategory.setItemType(Type.CATEGORY);
		hwCategory.setIncluded(Boolean.TRUE);
		hwCategory = getActiveItem(service.addItemCategory(gradebookUid, gradebookId,
				hwCategory));

		assertNotNull(hwCategory);
		assertEquals(Type.CATEGORY, hwCategory.getItemType());
		assertEquals("My Homework", hwCategory.getName());
		assertEquals(Double.valueOf(40d), hwCategory.getPercentCourseGrade());
		assertNull(hwCategory.getDropLowest());
		assertEquals(Boolean.FALSE, hwCategory.getEqualWeightAssignments());
		assertEquals(Double.valueOf(0d), hwCategory.getPercentCategory());

		ItemModel gradebookItemModel = (ItemModel) hwCategory.getParent();
		assertNotNull(gradebookItemModel);
		assertEquals(Type.GRADEBOOK, gradebookItemModel.getItemType());
		assertEquals(Double.valueOf(100d), gradebookItemModel.getPercentCourseGrade());


		ItemModel hw1 = new ItemModel();
		hw1.setName("HW 1");
		hw1.setPoints(Double.valueOf(20d));
		hw1.setDueDate(new Date());
		hw1.setCategoryId(hwCategory.getCategoryId());
		hw1.setReleased(Boolean.TRUE);
		hw1.setItemType(Type.ITEM);
		hw1.setIncluded(Boolean.TRUE);
		hw1 = getActiveItem(service.createItem(gradebookUid, gradebookId, hw1, true));

		assertNotNull(hw1);
		assertEquals(Type.ITEM, hw1.getItemType());
		assertEquals("HW 1", hw1.getName());
		assertEquals(Double.valueOf(20d), hw1.getPercentCategory());
		assertEquals(Boolean.FALSE, hw1.getExtraCredit());
		assertEquals(Boolean.TRUE, hw1.getReleased());
		assertEquals(Boolean.TRUE, hw1.getIncluded());


		// Move hw1 over to essay's category
		hw1.setCategoryId(category.getCategoryId());
		gradebookItemModel = service.updateItemModel(hw1);


		for (ModelData m : gradebookItemModel.getChildren()) {
			ItemModel child = (ItemModel)m;
			
			if (child.getName().equals("My Essays")) {

				assertEquals(Double.valueOf(100d), child.getPercentCategory());
				assertEquals(6, child.getChildCount());


			} else if (child.getName().equals("My Homework")) {
				assertEquals(Double.valueOf(0d), child.getPercentCategory());
				assertEquals(0, child.getChildCount());
			}

		}

	}


	/*
	 * Tests item update business rule #1
	 * 
	 * (1) If points is null, set points to 100
	 */
	public void testSetItemPointsNull() throws Exception {

		onSetup();

		// Grab first item from category
		ItemModel item = null;
		for (ModelData child : category.getChildren()) {
			item = (ItemModel) child;
			break;
		}

		// FIXME: Potential null pointer dereference
		item.setPoints(null);

		ItemModel parent = service.updateItemModel(item);

		for (ModelData m : parent.getChildren()) {
			ItemModel c = (ItemModel)m;
			if (c.isActive()) {
				assertEquals(Double.valueOf(100d), c.getPoints());
			}

		}

	}

	/*
	 * Test item update business rule #2
	 * 
	 * (2) If weight is null, set weight to be equivalent to points value -- needs to happen after #1
	 */
	public void testSetItemWeightNull() throws Exception {

		onSetup();

		// Grab first item from category
		ItemModel item = null;
		for (ModelData child : category.getChildren()) {
			item = (ItemModel) child;
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
	public void testCreateDuplicateItemNameInCategory() throws Exception {

		onSetup();

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
	 * Test item update business rule #6
	 * 
	 * (6) must not include an item in grading that has been deleted (removed = true) or that has a category that has been deleted (removed = true)
	 */
	public void testIncludeDeletedItemFromDeletedCategory() throws Exception {

		onSetup();

		// Grab first item from category
		ItemModel item = null;
		for (ModelData child : category.getChildren()) {
			item = (ItemModel) child;
			break;
		}

		// FIXME: Potential null pointer dereference
		item.setRemoved(Boolean.TRUE);

		ItemModel parent = service.updateItemModel(item);

		for (ModelData m : parent.getChildren()) {
			ItemModel c = (ItemModel)m;
			if (c.isActive()) {
				assertTrue(c.getRemoved());
				item = c;
			}
		}

		item.setIncluded(Boolean.TRUE);

		boolean isExceptionThrown = false;

		try {
			parent = service.updateItemModel(item);
		} catch (BusinessRuleException bre) {
			isExceptionThrown = true;
		}

		assertTrue(isExceptionThrown);

		// FIXME: Need to handle deleted category/included item case
	}

	/*
	 * Test item update business rule #
	 * 
	 * (7) if item is "included" and category has "equal weighting" then recalculate all item weights for this category
	 */
	public void testRecalculateItemWeightsOnIncludedOrUnincludedItem() throws Exception {

		onSetup();

		// Grab first item from category
		ItemModel item = null;
		for (ModelData child : category.getChildren()) {
			item = (ItemModel) child;
			break;
		}

		// FIXME: Potential null pointer dereference
		item.setIncluded(Boolean.FALSE);

		ItemModel parent = service.updateItemModel(item);

		for (ModelData m : parent.getChildren()) {
			ItemModel c = (ItemModel)m;
			if (!c.isActive() && !c.getExtraCredit()) {
				assertEquals(BigDecimal.valueOf(33.3333d).setScale(4, RoundingMode.HALF_EVEN), BigDecimal.valueOf(c.getPercentCategory().doubleValue()).setScale(4, RoundingMode.HALF_EVEN));
			}
		}
	}


	protected abstract void onSetup() throws Exception;


}
