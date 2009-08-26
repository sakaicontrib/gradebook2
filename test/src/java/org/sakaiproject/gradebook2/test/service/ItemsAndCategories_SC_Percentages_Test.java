package org.sakaiproject.gradebook2.test.service;

import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.GradeType;

public class ItemsAndCategories_SC_Percentages_Test extends
		BaseItemsAndCategoriesTest {

	public ItemsAndCategories_SC_Percentages_Test(String name) {
		super(name);
	}

	@Override
	protected void onSetup() throws Exception {
		onSetup(GradeType.PERCENTAGES, CategoryType.SIMPLE_CATEGORIES);
	}

}
