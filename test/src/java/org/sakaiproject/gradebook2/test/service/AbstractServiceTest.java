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
import java.util.Date;

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

import com.extjs.gxt.ui.client.data.ModelData;

public abstract class AbstractServiceTest extends AbstractDependencyInjectionSpringContextTests {

	protected Gradebook2Service service;
	protected ItemModel category;
	protected GradebookModel gbModel;
	
	public AbstractServiceTest(String name) {
		super(name);
		setAutowireMode(AUTOWIRE_BY_NAME);
	}
	
	protected String[] getConfigLocations() {
		String[] context = new String[2];
		context[0] = "classpath:test.xml";
		context[1] = "classpath:db.xml";
		return context;
	}
	
	protected GradebookModel getGradebookModel(String gradebookUid) throws Exception {
		ConfigurableApplicationContext context = applicationContext;
		service = (Gradebook2ServiceImpl) context
				.getBean("org.sakaiproject.gradebook.gwt.sakai.Gradebook2Service");
		ApplicationModel applicationModel = service.getApplicationModel(gradebookUid);

		GradebookModel gbModel = applicationModel.getGradebookModels().get(0);
		
		return gbModel;
	}
	
	protected void onSetup(GradeType gradeType, CategoryType categoryType) throws Exception {

		gbModel = getGradebookModel(getName());

		ItemModel gradebookItemModel = gbModel.getGradebookItemModel();
		gradebookItemModel.setName("My Test Gradebook");
		gradebookItemModel.setGradeType(gradeType);
		gradebookItemModel.setCategoryType(categoryType);
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
		essaysCategory = getActiveItem(service.addItemCategory(gradebookUid, gradebookId,
				essaysCategory));

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
		service.createItem(gradebookUid, gradebookId, essay4, true);
		
		ItemModel ec1 = new ItemModel();
		ec1.setName("Extra Credit");
		ec1.setPoints(Double.valueOf(20d));
		ec1.setDueDate(new Date());
		ec1.setCategoryId(essaysCategory.getCategoryId());
		ec1.setReleased(Boolean.TRUE);
		ec1.setItemType(Type.ITEM);
		ec1.setIncluded(Boolean.FALSE);
		ec1.setExtraCredit(Boolean.TRUE);
		ec1 = getActiveItem(service.createItem(gradebookUid, gradebookId, ec1, true));

		category = (ItemModel) ec1.getParent();
		
		for (ModelData m : category.getChildren()) {
			ItemModel child = (ItemModel)m;
			Double percentCategory = child.getPercentCategory();
			BigDecimal pC = BigDecimal.valueOf(percentCategory.doubleValue());

		}

	}

		
	protected ItemModel getActiveItem(ItemModel parent) {
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
	
}
