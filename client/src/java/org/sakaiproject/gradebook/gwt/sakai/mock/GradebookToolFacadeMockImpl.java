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
import org.sakaiproject.gradebook.gwt.client.action.UserAssignmentCreateAction;
import org.sakaiproject.gradebook.gwt.client.action.UserCategoryCreateAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityCreateAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityGetAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction.ClassType;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.model.AssignmentModel;
import org.sakaiproject.gradebook.gwt.client.model.CategoryModel;
import org.sakaiproject.gradebook.gwt.client.model.EntityModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.GradeType;
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
		
		GradebookModel gbModel = getEntity(new UserEntityGetAction<GradebookModel>(EntityType.GRADEBOOK, "emptyid"));
		try {
			gbModel = updateEntity(new UserEntityUpdateAction<GradebookModel>(gbModel, gbModel, GradebookModel.Key.NAME.name(), ClassType.STRING, "Test Gradebook", null));
			gbModel = updateEntity(new UserEntityUpdateAction<GradebookModel>(gbModel, gbModel, GradebookModel.Key.CATEGORYTYPE.name(), ClassType.CATEGORYTYPE, CategoryType.NO_CATEGORIES, null));
			gbModel = updateEntity(new UserEntityUpdateAction<GradebookModel>(gbModel, gbModel, GradebookModel.Key.GRADETYPE.name(), ClassType.GRADETYPE, GradeType.PERCENTAGES, null));
		} catch (InvalidInputException e) {
			GWT.log("Failed to update gradebook properties", e);
		}
		
		CategoryModel essaysCategory = createEntity(new UserCategoryCreateAction(gbModel, "My Essays", 
				Double.valueOf(60d), Boolean.TRUE, Integer.valueOf(1)));
		
		CategoryModel hwCategory = createEntity(new UserCategoryCreateAction(gbModel, "My Homework", 
				Double.valueOf(40d), Boolean.TRUE, Integer.valueOf(0)));
		
		
		AssignmentModel essay1 = createEntity(new UserAssignmentCreateAction(gbModel, 
				Long.valueOf(essaysCategory.getIdentifier()), 
				"Essay 1", Double.valueOf(0), Double.valueOf(20), new Date()));
		AssignmentModel essay2 = createEntity(new UserAssignmentCreateAction(gbModel, 
				Long.valueOf(essaysCategory.getIdentifier()), 
				"Essay 2", Double.valueOf(0), Double.valueOf(20), new Date()));
		AssignmentModel essay3 = createEntity(new UserAssignmentCreateAction(gbModel, 
				Long.valueOf(essaysCategory.getIdentifier()), 
				"Essay 3", Double.valueOf(0), Double.valueOf(20), new Date()));
		
		AssignmentModel hw1 = createEntity(new UserAssignmentCreateAction(gbModel, 
				Long.valueOf(hwCategory.getIdentifier()), 
				"HW 1", Double.valueOf(0), Double.valueOf(10), new Date()));
		AssignmentModel hw2 = createEntity(new UserAssignmentCreateAction(gbModel, 
				Long.valueOf(hwCategory.getIdentifier()), 
				"HW 2", Double.valueOf(0), Double.valueOf(10), new Date()));
		AssignmentModel hw3 = createEntity(new UserAssignmentCreateAction(gbModel, 
				Long.valueOf(hwCategory.getIdentifier()), 
				"HW 3", Double.valueOf(0), Double.valueOf(10), new Date()));
		AssignmentModel hw4 = createEntity(new UserAssignmentCreateAction(gbModel, 
				Long.valueOf(hwCategory.getIdentifier()), 
				"HW 4", Double.valueOf(0), Double.valueOf(10), new Date()));
	}
	
	public <X extends EntityModel> X createEntity(UserEntityCreateAction<X> action) {
		
		return delegateFacade.createEntity(action);
	}
	
	public <X extends EntityModel> X getEntity(UserEntityGetAction<X> action) {
		
		return delegateFacade.getEntity(action);
	}
	
	public <X extends EntityModel> List<X> getEntityList(UserEntityGetAction<X> action) {
		
		return delegateFacade.getEntityList(action);
	}
	
	public <X extends EntityModel> PagingLoadResult<X> getEntityPage(PageRequestAction action,
			PagingLoadConfig config) {
		
		return delegateFacade.getEntityPage(action, config);
	}

	public List<CategoryModel> recalculateEqualWeightingCategories(
			String gradebookUid, Long gradebookId, Boolean isEqualWeighting) {
		
		return delegateFacade.recalculateEqualWeightingCategories(gradebookUid, gradebookId, isEqualWeighting);
	}
	
	public <X extends EntityModel> X updateEntity(UserEntityUpdateAction<X> action) throws InvalidInputException {
		
		return delegateFacade.updateEntity(action);
	}

	public <X extends EntityModel> List<X> updateEntityList(UserEntityUpdateAction<X> action) throws InvalidInputException {
	
		return delegateFacade.updateEntityList(action);
	}
	
	public void setNumberOfTestLearners(int n) {
		((DelegateFacadeMockImpl)delegateFacade).setNumberOfTestLearners(n);
	}

}
