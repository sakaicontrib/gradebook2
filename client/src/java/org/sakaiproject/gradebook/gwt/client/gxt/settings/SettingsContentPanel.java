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
package org.sakaiproject.gradebook.gwt.client.gxt.settings;

import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.gxt.InstructorViewContainer;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.UserChangeEvent;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

public class SettingsContentPanel extends ContentPanel {

	private String gradebookUid;
	
	//private SettingsGradebookContentPanel gradebookPanel;
	private SettingsCategoryContentPanel categoriesPanel;
	private SettingsAssignmentContentPanel assignmentsPanel;
	private SettingsGradingScaleContentPanel gradingScalePanel;
	
	
	private TabPanel tabPanel;
	private TabItem categoriesTab;
	
	public SettingsContentPanel(String gradebookUid, final ContentPanel multigrade, final InstructorViewContainer instructorViewContainer) {
		this.gradebookUid = gradebookUid;
		
		GradebookModel gbModel = Registry.get(gradebookUid);
		
		setLayout(new FitLayout());
		setHeaderVisible(false);
		setHeading("Setup");
		setMonitorResize(true);
		
		//gradebookPanel = new SettingsGradebookContentPanel(gradebookUid);

		categoriesPanel = new SettingsCategoryContentPanel(gradebookUid);
		
		assignmentsPanel = new SettingsAssignmentContentPanel(gradebookUid, categoriesPanel);
		
		gradingScalePanel = new SettingsGradingScaleContentPanel();
		
		tabPanel = new TabPanel();
		
		/*TabItem gradebookTab = new TabItem("Gradebook");  
		gradebookTab.addStyleName("pad-text");  
		gradebookTab.add(gradebookPanel);
		gradebookTab.setLayout(new FitLayout());
		tabPanel.add(gradebookTab);*/
		
		if (gbModel.getCategoryType() != CategoryType.NO_CATEGORIES) {
			insertCategoriesTab();
		}
		
		TabItem assignmentsTab = new TabItem("Grade Items");  
		assignmentsTab.addStyleName("pad-text");  
		assignmentsTab.add(assignmentsPanel);
		assignmentsTab.setLayout(new FitLayout());
		tabPanel.add(assignmentsTab);
		
		TabItem gradingScaleTab = new TabItem("Grading Scale");
		gradingScaleTab.addStyleName("pad-text");  
		gradingScaleTab.add(gradingScalePanel);
		gradingScaleTab.setLayout(new FitLayout());
		tabPanel.add(gradingScaleTab);
		
		add(tabPanel);

		addListener(GradebookEvents.UserChange, new Listener<UserChangeEvent>() {

			public void handleEvent(UserChangeEvent uce) {
				if (!uce.isTransferred()) {
					uce.setTransferred(true);
					categoriesPanel.fireEvent(GradebookEvents.UserChange, uce);
					multigrade.fireEvent(GradebookEvents.UserChange, uce);
					assignmentsPanel.fireEvent(GradebookEvents.UserChange, uce);
				}
				
				UserEntityAction action = uce.getAction();
				switch (action.getEntityType()) {
				case GRADEBOOK:
					switch (action.getActionType()) {
					case UPDATE:
						UserEntityUpdateAction<GradebookModel> updateAction = 
							(UserEntityUpdateAction<GradebookModel>)action;
						
						GradebookModel.Key gradebookKey = GradebookModel.Key.valueOf(updateAction.getKey());
						
						
						switch (gradebookKey) {
						case CATEGORYTYPE:
							CategoryType categoryType = updateAction.getValue();
							
							switch (categoryType) {
							case NO_CATEGORIES:
								if (categoriesTab != null) {
									tabPanel.remove(categoriesTab);
									categoriesTab = null;
								}
								if (instructorViewContainer != null)
									instructorViewContainer.showAddCategory(false);
								break;
							default:
								if (categoriesTab == null)
									insertCategoriesTab();
							
								if (instructorViewContainer != null)
									instructorViewContainer.showAddCategory(true);
								break;
							}
							break;
						}
						
						break;
					}
					
					break;
				}
			}
			
		});
		
		assignmentsPanel.addListener(GradebookEvents.UserChange, new Listener<UserChangeEvent>() {

			public void handleEvent(UserChangeEvent uce) {
				SettingsContentPanel.this.fireEvent(GradebookEvents.UserChange, uce);				
			}
			
		});
		
		categoriesPanel.addListener(GradebookEvents.UserChange, new Listener<UserChangeEvent>() {
			
			public void handleEvent(UserChangeEvent uce) {
				SettingsContentPanel.this.fireEvent(GradebookEvents.UserChange, uce);
			}
			
		});
		
		/*gradebookPanel.addListener(GradebookEvents.UserChange, new Listener<UserChangeEvent>() {

			public void handleEvent(UserChangeEvent uce) {

				SettingsContentPanel.this.fireEvent(GradebookEvents.UserChange, uce);
				
				UserEntityAction action = uce.getAction();
				switch (action.getEntityType()) {
				case GRADEBOOK:
					switch (action.getActionType()) {
					case UPDATE:
						UserEntityUpdateAction<GradebookModel> updateAction = 
							(UserEntityUpdateAction<GradebookModel>)action;
						
						GradebookModel.Key gradebookKey = GradebookModel.Key.valueOf(updateAction.getKey());
						
						
						switch (gradebookKey) {
						case CATEGORYTYPE:
							CategoryType categoryType = updateAction.getValue();
							
							switch (categoryType) {
							case NO_CATEGORIES:
								if (categoriesTab != null) {
									tabPanel.remove(categoriesTab);
									categoriesTab = null;
								}
								if (instructorViewContainer != null)
									instructorViewContainer.showAddCategory(false);
								break;
							default:
								if (categoriesTab == null)
									insertCategoriesTab();
							
								if (instructorViewContainer != null)
									instructorViewContainer.showAddCategory(true);
								break;
							}
							break;
						}
						
						break;
					}
					
					break;
				}
			}
			
		});*/
		
		gradingScalePanel.addListener(GradebookEvents.UserChange, new Listener<UserChangeEvent>() {

			public void handleEvent(UserChangeEvent uce) {
				multigrade.fireEvent(GradebookEvents.UserChange, uce);
			}
			
		});
		
	}

	@Override
	protected void onRender(Element target, int index) {
		super.onRender(target, index);
	}

	private void insertCategoriesTab() {
		categoriesTab = new TabItem("Categories");  
		categoriesTab.addStyleName("pad-text");  
		categoriesTab.add(categoriesPanel);
		categoriesTab.setLayout(new FitLayout());
		tabPanel.insert(categoriesTab, 0);
	}
	
	public String getGradebookUid() {
		return gradebookUid;
	}
	
	
		
}
