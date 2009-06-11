package org.sakaiproject.gradebook2.test.service;

import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.GradeType;

public class ItemsAndCategories_WC_Per_Test extends
		BaseItemsAndCategoriesTest {

	public ItemsAndCategories_WC_Per_Test(String name) {
		super(name);
	}

	@Override
	protected void onSetup() throws Exception {
		onSetup(GradeType.PERCENTAGES, CategoryType.WEIGHTED_CATEGORIES);
	}

}
