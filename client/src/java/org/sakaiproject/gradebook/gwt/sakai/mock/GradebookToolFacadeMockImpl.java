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

import org.sakaiproject.gradebook.gwt.client.GradebookToolFacade;
import org.sakaiproject.gradebook.gwt.client.action.PageRequestAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityCreateAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityGetAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.exceptions.BusinessRuleException;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.model.EntityModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;
import org.sakaiproject.gradebook.gwt.sakai.GradeCalculations;
import org.sakaiproject.gradebook.gwt.sakai.GradeCalculationsImpl;
import org.sakaiproject.gradebook.gwt.sakai.GradebookToolService;
import org.sakaiproject.section.api.SectionAwareness;
import org.sakaiproject.tool.gradebook.facades.Authn;
import org.sakaiproject.tool.gradebook.facades.Authz;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GradebookToolFacadeMockImpl extends RemoteServiceServlet implements GradebookToolFacade {

	private static final long serialVersionUID = 1L;
		
	private SectionAwareness sectionAwareness = new SectionAwarenessMock();
	private Authz authz = new AuthzMock(sectionAwareness);
	private Authn authn = new AuthnMock();
	private GradebookToolService gradebookManager = new GradebookToolServiceMock();
	private GradeCalculations gradeCalculations = new GradeCalculationsImpl();

	private GradebookToolFacade delegateFacade = new DelegateFacadeMockImpl(sectionAwareness, authz, authn, gradebookManager, gradeCalculations);

	private IocMock iocMock = IocMock.getInstance();	
	
	/*
	 * This method does the hosted mode initialization to ensure that the appropriate test data is in place
	 * 
	 * (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() {
		//UserEntityCreateAction<GradebookModel> createGradebookAction = new UserEntityCreateAction<GradebookModel>();
		//createGradebookAction.setName("Test Gradebook");
		//GradebookModel gbModel = createEntity(createGradebookAction);
		try {
		
			iocMock.registerClassInstance(DelegateFacadeMockImpl.class.getName(), delegateFacade);

			GradebookModel gbModel = getEntity(new UserEntityGetAction<GradebookModel>(EntityType.GRADEBOOK, "emptyid"));

			ItemModel gradebook = new ItemModel();
			gradebook.setName("Test Gradebook");
			gradebook.setCategoryType(CategoryType.WEIGHTED_CATEGORIES);
			gradebook.setGradeType(GradeType.PERCENTAGES);
			gradebook.setItemType(Type.GRADEBOOK);
			
			gradebook = updateItemEntity(new UserEntityUpdateAction<ItemModel>(gbModel, gradebook));
			
			ItemModel essaysCategory = new ItemModel();
			essaysCategory.setName("My Essays");
			essaysCategory.setPercentCourseGrade(Double.valueOf(60d));
			essaysCategory.setDropLowest(Integer.valueOf(1));
			essaysCategory.setEqualWeightAssignments(Boolean.TRUE);
			essaysCategory.setItemType(Type.CATEGORY);
			essaysCategory.setIncluded(Boolean.TRUE);
			essaysCategory = createItemEntity(new UserEntityCreateAction<ItemModel>(gbModel, EntityType.CATEGORY, 
					essaysCategory));
			
			ItemModel hwCategory = new ItemModel();
			hwCategory.setName("My Homework");
			hwCategory.setPercentCourseGrade(Double.valueOf(60d));
			hwCategory.setDropLowest(Integer.valueOf(1));
			hwCategory.setEqualWeightAssignments(Boolean.TRUE);
			hwCategory.setItemType(Type.CATEGORY);
			hwCategory.setIncluded(Boolean.TRUE);
			hwCategory = createItemEntity(new UserEntityCreateAction<ItemModel>(gbModel, EntityType.CATEGORY, 
					hwCategory));
			
			ItemModel essay1 = new ItemModel();
			essay1.setName("Essay 1");
			essay1.setPoints(Double.valueOf(20d));
			essay1.setDueDate(new Date());
			essay1.setCategoryId(essaysCategory.getCategoryId());
			essay1.setReleased(Boolean.TRUE);
			essay1.setItemType(Type.ITEM);
			essay1.setIncluded(Boolean.TRUE);
			essay1 = createItemEntity(new UserEntityCreateAction<ItemModel>(gbModel, EntityType.ITEM, 
					essay1));
			
			ItemModel essay2 = new ItemModel();
			essay2.setName("Essay 2");
			essay2.setPoints(Double.valueOf(20d));
			essay2.setDueDate(new Date());
			essay2.setCategoryId(essaysCategory.getCategoryId());
			essay2.setReleased(Boolean.TRUE);
			essay2.setItemType(Type.ITEM);
			essay2.setIncluded(Boolean.TRUE);
			essay2 = createItemEntity(new UserEntityCreateAction<ItemModel>(gbModel, EntityType.ITEM, 
					essay2));
			
			ItemModel essay3 = new ItemModel();
			essay3.setName("Essay 3");
			essay3.setPoints(Double.valueOf(20d));
			essay3.setDueDate(new Date());
			essay3.setCategoryId(essaysCategory.getCategoryId());
			essay3.setReleased(Boolean.TRUE);
			essay3.setItemType(Type.ITEM);
			essay3.setIncluded(Boolean.TRUE);
			essay3 = createItemEntity(new UserEntityCreateAction<ItemModel>(gbModel, EntityType.ITEM, 
					essay3));
		

			ItemModel hw1 = new ItemModel();
			hw1.setName("HW 1");
			hw1.setPoints(Double.valueOf(10d));
			hw1.setDueDate(new Date());
			hw1.setCategoryId(hwCategory.getCategoryId());
			hw1.setItemType(Type.ITEM);
			hw1.setIncluded(Boolean.TRUE);
			hw1.setReleased(Boolean.FALSE);
			hw1 = createItemEntity(new UserEntityCreateAction<ItemModel>(gbModel, EntityType.ITEM, 
					hw1));
			
			ItemModel hw2 = new ItemModel();
			hw2.setName("HW 2");
			hw2.setPoints(Double.valueOf(10d));
			hw2.setDueDate(new Date());
			hw2.setCategoryId(hwCategory.getCategoryId());
			hw2.setItemType(Type.ITEM);
			hw2.setIncluded(Boolean.TRUE);
			hw2.setReleased(Boolean.FALSE);
			hw2 = createItemEntity(new UserEntityCreateAction<ItemModel>(gbModel, EntityType.ITEM, 
					hw2));
			
			ItemModel hw3 = new ItemModel();
			hw3.setName("HW 3");
			hw3.setPoints(Double.valueOf(10d));
			hw3.setDueDate(new Date());
			hw3.setCategoryId(hwCategory.getCategoryId());
			hw3.setItemType(Type.ITEM);
			hw3.setIncluded(Boolean.TRUE);
			hw3.setReleased(Boolean.FALSE);
			hw3 = createItemEntity(new UserEntityCreateAction<ItemModel>(gbModel, EntityType.ITEM, 
					hw3));
			
			ItemModel hw4 = new ItemModel();
			hw4.setName("HW 4");
			hw4.setPoints(Double.valueOf(10d));
			hw4.setDueDate(new Date());
			hw4.setCategoryId(hwCategory.getCategoryId());
			hw4.setItemType(Type.ITEM);
			hw4.setIncluded(Boolean.TRUE);
			hw4.setReleased(Boolean.FALSE);
			hw4 = createItemEntity(new UserEntityCreateAction<ItemModel>(gbModel, EntityType.ITEM, 
					hw4));

		} catch (Exception fe) {
			GWT.log("Failed to update gradebook properties", fe);
		}
	}
	
	public <X extends EntityModel> X createEntity(UserEntityCreateAction<X> action) throws FatalException {
		
		return delegateFacade.createEntity(action);
	}
	
	public <X extends ItemModel> X createItemEntity(UserEntityCreateAction<X> action) throws BusinessRuleException, FatalException {
		
		return delegateFacade.createItemEntity(action);
	}
	
	public <X extends EntityModel> X getEntity(UserEntityGetAction<X> action) throws FatalException {
		
		return delegateFacade.getEntity(action);
	}
	
	public <X extends EntityModel> List<X> getEntityList(UserEntityGetAction<X> action) throws FatalException {
		
		return delegateFacade.getEntityList(action);
	}
	
	public <X extends EntityModel> PagingLoadResult<X> getEntityPage(PageRequestAction action,
			PagingLoadConfig config) throws FatalException {
		
		return delegateFacade.getEntityPage(action, config);
	}
	
	public <X extends ItemModel> X getEntityTreeModel(String gradebookUid, X parent) {
		
		return delegateFacade.getEntityTreeModel(gradebookUid, parent);
	}

	public <X extends ItemModel> X updateItemEntity(UserEntityUpdateAction<X> action) throws InvalidInputException, FatalException {
		
		return delegateFacade.updateItemEntity(action);
	}
	
	public <X extends EntityModel> X updateEntity(UserEntityUpdateAction<X> action) throws InvalidInputException, FatalException {
		
		return delegateFacade.updateEntity(action);
	}

	public <X extends EntityModel> List<X> updateEntityList(UserEntityUpdateAction<X> action) throws InvalidInputException, FatalException {
	
		return delegateFacade.updateEntityList(action);
	}
	
	public void setNumberOfTestLearners(int n) {
		((DelegateFacadeMockImpl)delegateFacade).setNumberOfTestLearners(n);
	}

}
