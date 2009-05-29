package org.sakaiproject.gradebook2.test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.sakaiproject.gradebook.gwt.client.exceptions.BusinessRuleException;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2Service;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ServiceImpl;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class Gradebook2ServiceTest extends AbstractDependencyInjectionSpringContextTests {

	protected Gradebook2Service service;
	protected ItemModel category;
	protected GradebookModel gbModel;
	
	public Gradebook2ServiceTest(String name) {
		super(name);
		setAutowireMode(AUTOWIRE_BY_NAME);
	}
	
	protected String[] getConfigLocations() {
		String[] context = new String[2];
		context[0] = "classpath:test.xml";
		context[1] = "classpath:db.xml";
		return context;
	}
	
	protected void onSetup() throws Exception {
		ConfigurableApplicationContext context = applicationContext;
		service = (Gradebook2ServiceImpl) context
				.getBean("org.sakaiproject.gradebook.gwt.sakai.Gradebook2Service");
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
		essaysCategory.setPercentCourseGrade(Double.valueOf(60d));
		essaysCategory.setDropLowest(Integer.valueOf(0));
		essaysCategory.setEqualWeightAssignments(Boolean.TRUE);
		essaysCategory.setItemType(Type.CATEGORY);
		essaysCategory.setIncluded(Boolean.TRUE);
		essaysCategory = service.addItemCategory(gradebookUid, gradebookId,
				essaysCategory);

		ItemModel essay1 = new ItemModel();
		essay1.setName("Essay 1");
		essay1.setPoints(Double.valueOf(20d));
		essay1.setDueDate(new Date());
		essay1.setCategoryId(essaysCategory.getCategoryId());
		essay1.setReleased(Boolean.TRUE);
		essay1.setItemType(Type.ITEM);
		essay1.setIncluded(Boolean.TRUE);
		service.createItem(gradebookUid, gradebookId, essay1, true);

		ItemModel essay2 = new ItemModel();
		essay2.setName("Essay 2");
		essay2.setPoints(Double.valueOf(20d));
		essay2.setDueDate(new Date());
		essay2.setCategoryId(essaysCategory.getCategoryId());
		essay2.setReleased(Boolean.TRUE);
		essay2.setItemType(Type.ITEM);
		essay2.setIncluded(Boolean.TRUE);
		service.createItem(gradebookUid, gradebookId, essay2, true);

		ItemModel essay3 = new ItemModel();
		essay3.setName("Essay 3");
		essay3.setPoints(Double.valueOf(20d));
		essay3.setDueDate(new Date());
		essay3.setCategoryId(essaysCategory.getCategoryId());
		essay3.setReleased(Boolean.TRUE);
		essay3.setItemType(Type.ITEM);
		essay3.setIncluded(Boolean.TRUE);
		service.createItem(gradebookUid, gradebookId, essay3, true);

		ItemModel essay4 = new ItemModel();
		essay4.setName("Essay 4");
		essay4.setPoints(Double.valueOf(20d));
		essay4.setDueDate(new Date());
		essay4.setCategoryId(essaysCategory.getCategoryId());
		essay4.setReleased(Boolean.TRUE);
		essay4.setItemType(Type.ITEM);
		essay4.setIncluded(Boolean.TRUE);

		category = service.createItem(gradebookUid, gradebookId, essay4, true);

		for (ItemModel child : category.getChildren()) {
			Double percentCategory = child.getPercentCategory();
			BigDecimal pC = BigDecimal.valueOf(percentCategory.doubleValue());

			assertTrue(pC.setScale(2).compareTo(BigDecimal.valueOf(25.0)) == 0);
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
		for (ItemModel child : category.getChildren()) {
			item = child;
			break;
		}
		
		item.setPoints(null);
		
		ItemModel parent = service.updateItemModel(item);
		
		for (ItemModel c : parent.getChildren()) {
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
		for (ItemModel child : category.getChildren()) {
			item = child;
			break;
		}
		
		item.setPercentCategory(null);
		
		ItemModel parent = service.updateItemModel(item);
		
		for (ItemModel c : parent.getChildren()) {
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
		for (ItemModel child : category.getChildren()) {
			item = child;
			break;
		}
		
		item.setRemoved(Boolean.TRUE);
		
		ItemModel parent = service.updateItemModel(item);
		
		for (ItemModel c : parent.getChildren()) {
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
		for (ItemModel child : category.getChildren()) {
			item = child;
			break;
		}
		
		item.setIncluded(Boolean.FALSE);
		
		ItemModel parent = service.updateItemModel(item);
		
		for (ItemModel c : parent.getChildren()) {
			if (!c.isActive()) {
				assertEquals(BigDecimal.valueOf(33.3333d).setScale(4, RoundingMode.HALF_EVEN), BigDecimal.valueOf(c.getPercentCategory().doubleValue()).setScale(4, RoundingMode.HALF_EVEN));
			}
		}
	}
	
	
}
