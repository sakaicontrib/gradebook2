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

package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.model.ApplicationSetup;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.key.GradebookKey;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.type.ItemType;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ComponentService;
import org.sakaiproject.gradebook.gwt.server.model.GradeItem;
import org.sakaiproject.gradebook.gwt.server.model.GradeItemImpl;

import com.google.gwt.core.client.GWT;

public class DevelopmentModeBean {

	private static final long serialVersionUID = 1L;
	private Gradebook2ComponentService service;
	
	public Gradebook2ComponentService getService() {
		return service;
	}

	public void setService(Gradebook2ComponentService service) {
		this.service = service;
	}

	public void init() {	
		
		try {
			String authDetails = service.getAuthorizationDetails();
			
			ApplicationSetup applicationSetup = service.getApplicationSetup();
			List<Gradebook> gbModels = applicationSetup.getGradebookModels();
			
			Gradebook gbModel = gbModels.get(0);

			Item gradebook = gbModel.getGradebookItemModel();
			
			gradebook.setName("Test Gradebook");
			gradebook.setCategoryType(CategoryType.WEIGHTED_CATEGORIES);
			gradebook.setGradeType(GradeType.POINTS);
			gradebook.setItemType(ItemType.GRADEBOOK);
			gradebook.setExtraCreditScaled(Boolean.TRUE);
			gradebook.setReleaseGrades(Boolean.TRUE);
			
			service.updateItem(gradebook);
			
			String gradebookUid = gbModel.getGradebookUid();
			Long gradebookId = gbModel.getGradebookId();
			
			GradeItem essaysCategory = new GradeItemImpl();
			essaysCategory.setName("My Essays");
			essaysCategory.setPercentCourseGrade(Double.valueOf(50d));
			essaysCategory.setDropLowest(Integer.valueOf(0));
			essaysCategory.setEqualWeightAssignments(Boolean.TRUE);
			essaysCategory.setItemType(ItemType.CATEGORY);
			essaysCategory.setIncluded(Boolean.TRUE);
			essaysCategory.setEnforcePointWeighting(Boolean.TRUE);
			essaysCategory = getActiveItem((GradeItem)service.createItem(gradebookUid, gradebookId, essaysCategory, false));

			
			GradeItem hwCategory = new GradeItemImpl();
			hwCategory.setName("My Homework");
			hwCategory.setPercentCourseGrade(Double.valueOf(40d));
			hwCategory.setDropLowest(Integer.valueOf(0));
			hwCategory.setEqualWeightAssignments(Boolean.TRUE);
			hwCategory.setItemType(ItemType.CATEGORY);
			hwCategory.setIncluded(Boolean.TRUE);
			hwCategory = getActiveItem((GradeItem)service.createItem(gradebookUid, gradebookId, hwCategory, false));
			
			GradeItem emptyCategory = new GradeItemImpl();
			emptyCategory.setName("Empty");
			emptyCategory.setPercentCourseGrade(Double.valueOf(10d));
			emptyCategory.setDropLowest(Integer.valueOf(0));
			emptyCategory.setEqualWeightAssignments(Boolean.TRUE);
			emptyCategory.setItemType(ItemType.CATEGORY);
			emptyCategory.setIncluded(Boolean.TRUE);
			emptyCategory = getActiveItem((GradeItem)service.createItem(gradebookUid, gradebookId, emptyCategory, false));
			
			
			GradeItem ecCategory = new GradeItemImpl();
			ecCategory.setName("Extra Credit");
			ecCategory.setPercentCourseGrade(Double.valueOf(10d));
			ecCategory.setDropLowest(Integer.valueOf(0));
			ecCategory.setEqualWeightAssignments(Boolean.TRUE);
			ecCategory.setItemType(ItemType.CATEGORY);
			ecCategory.setExtraCredit(Boolean.TRUE);
			ecCategory.setIncluded(Boolean.TRUE);
			ecCategory = getActiveItem((GradeItem)service.createItem(gradebookUid, gradebookId, ecCategory, false));
			
			
			GradeItem essay1 = new GradeItemImpl();
			essay1.setName("Essay 1");
			essay1.setPoints(Double.valueOf(10d));
			essay1.setDueDate(new Date());
			essay1.setCategoryId(essaysCategory.getCategoryId());
			essay1.setReleased(Boolean.FALSE);
			essay1.setItemType(ItemType.ITEM);
			essay1.setIncluded(Boolean.TRUE);
			service.createItem(gradebookUid, gradebookId, essay1, false);
			
			GradeItem essay2 = new GradeItemImpl();
			essay2.setName("Essay 2");
			essay2.setPoints(Double.valueOf(20d));
			essay2.setDueDate(new Date());
			essay2.setCategoryId(essaysCategory.getCategoryId());
			essay2.setReleased(Boolean.FALSE);
			essay2.setItemType(ItemType.ITEM);
			essay2.setIncluded(Boolean.TRUE);
			service.createItem(gradebookUid, gradebookId, essay2, false);
			
			GradeItem essay3 = new GradeItemImpl();
			essay3.setName("Essay 3");
			essay3.setPoints(Double.valueOf(10d));
			essay3.setDueDate(new Date());
			essay3.setCategoryId(essaysCategory.getCategoryId());
			essay3.setReleased(Boolean.FALSE);
			essay3.setItemType(ItemType.ITEM);
			essay3.setIncluded(Boolean.TRUE);
			service.createItem(gradebookUid, gradebookId, essay3, false);

			GradeItem ecEssay = new GradeItemImpl();
			ecEssay.setName("EC Essay");
			ecEssay.setPercentCategory(Double.valueOf(100d));
			ecEssay.setPoints(Double.valueOf(5d));
			ecEssay.setDueDate(new Date());
			ecEssay.setCategoryId(essaysCategory.getCategoryId());
			ecEssay.setIncluded(Boolean.FALSE);
			ecEssay.setExtraCredit(Boolean.TRUE);
			ecEssay.setReleased(Boolean.FALSE);
			service.createItem(gradebookUid, gradebookId, ecEssay, false);
			
			
			GradeItem hw1 = new GradeItemImpl();
			hw1.setName("HW 1");
			hw1.setPoints(Double.valueOf(10d));
			hw1.setDueDate(new Date());
			hw1.setCategoryId(hwCategory.getCategoryId());
			hw1.setItemType(ItemType.ITEM);
			hw1.setIncluded(Boolean.TRUE);
			hw1.setReleased(Boolean.FALSE);
			service.createItem(gradebookUid, gradebookId, hw1, false);
			
			GradeItem hw2 = new GradeItemImpl();
			hw2.setName("HW 2");
			hw2.setPoints(Double.valueOf(10d));
			hw2.setDueDate(new Date());
			hw2.setCategoryId(hwCategory.getCategoryId());
			hw2.setItemType(ItemType.ITEM);
			hw2.setIncluded(Boolean.TRUE);
			hw2.setReleased(Boolean.FALSE);
			service.createItem(gradebookUid, gradebookId, hw2, false);
			
			GradeItem hw3 = new GradeItemImpl();
			hw3.setName("HW 3");
			hw3.setPoints(Double.valueOf(10d));
			hw3.setDueDate(new Date());
			hw3.setCategoryId(hwCategory.getCategoryId());
			hw3.setItemType(ItemType.ITEM);
			hw3.setIncluded(Boolean.TRUE);
			hw3.setReleased(Boolean.FALSE);
			service.createItem(gradebookUid, gradebookId, hw3, false);
			
			GradeItem hw4 = new GradeItemImpl();
			hw4.setName("HW 4");
			hw4.setPoints(Double.valueOf(10d));
			hw4.setDueDate(new Date());
			hw4.setCategoryId(hwCategory.getCategoryId());
			hw4.setItemType(ItemType.ITEM);
			hw4.setIncluded(Boolean.TRUE);
			hw4.setReleased(Boolean.FALSE);
			service.createItem(gradebookUid, gradebookId, hw4, false);

			
			GradeItem ec1 = new GradeItemImpl();
			ec1.setName("EC 1");
			ec1.setPercentCategory(Double.valueOf(100d));
			ec1.setPoints(Double.valueOf(10d));
			ec1.setDueDate(new Date());
			ec1.setCategoryId(ecCategory.getCategoryId());
			ec1.setIncluded(Boolean.TRUE);
			ec1.setExtraCredit(Boolean.TRUE);
			ec1.setReleased(Boolean.FALSE);
			service.createItem(gradebookUid, gradebookId, ec1, false);
			
			GradeItem ec2 = new GradeItemImpl();
			ec2.setName("EC 2");
			ec2.setPercentCategory(Double.valueOf(100d));
			ec2.setPoints(Double.valueOf(10d));
			ec2.setDueDate(new Date());
			ec2.setCategoryId(ecCategory.getCategoryId());
			ec2.setIncluded(Boolean.TRUE);
			ec2.setExtraCredit(Boolean.TRUE);
			ec2.setReleased(Boolean.FALSE);
			service.createItem(gradebookUid, gradebookId, ec2, false);
			
			
			
		} catch (Exception fe) {
			GWT.log("Failed to update gradebook properties", fe);
		}
	}
	
	private GradeItem getActiveItem(GradeItem parent) {
		if (parent.isActive())
			return parent;
		
		for (GradeItem c : parent.getChildren()) {
			
			if (c.isActive()) {
				return c;
			}
			
			if (c.getChildCount() > 0) {
				GradeItem activeItem = getActiveItem(c);
				
				if (activeItem != null)
					return activeItem;
			}
		}
		
		return null;
	}
}
