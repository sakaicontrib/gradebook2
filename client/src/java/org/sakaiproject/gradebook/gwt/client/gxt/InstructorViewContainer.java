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
package org.sakaiproject.gradebook.gwt.client.gxt;

import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.MultiGradeContentPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.settings.AddAssignmentDialog;
import org.sakaiproject.gradebook.gwt.client.gxt.settings.AddCategoryDialog;
import org.sakaiproject.gradebook.gwt.client.gxt.settings.SettingsContentPanel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.XDOM;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ToolBarEvent;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToggleToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;

public class InstructorViewContainer extends ContentPanel {

	private MultiGradeContentPanel multigrade;
	private SettingsContentPanel settings;
	private AddAssignmentDialog addAssignmentDialog;
	private AddCategoryDialog addCategoryDialog;
	private HistoryDialog historyDialog;
	private Window setupWindow;
	private LayoutContainer gradebookContainer;
	private String gradebookUid;
	private MenuItem newCategoryItem;
	
	public InstructorViewContainer(String gradebookUid, LayoutContainer gradebookContainer) {
		this.gradebookUid = gradebookUid;
		this.gradebookContainer = gradebookContainer;
		setHeaderVisible(false);
		setLayout(new FitLayout());
		setMonitorResize(true);
		
		GradebookModel gbModel = Registry.get(gradebookUid);
		
		multigrade = new MultiGradeContentPanel(gradebookUid, this);	
		
		// Since the settings content panel is not yet instantiated, we pass the multigrade for the moment
		addAssignmentDialog = new AddAssignmentDialog(gbModel.getGradebookUid(), multigrade);
		addCategoryDialog = new AddCategoryDialog(gradebookUid, multigrade);
		historyDialog = new HistoryDialog(gradebookUid);
		
		add(multigrade);
		addToolBar(gbModel);
	}
	
	public void showAddCategory(boolean show) {
		if (newCategoryItem != null)
			newCategoryItem.setVisible(show);
	}
	
	public void onLoad() {
		super.onLoad();
		refreshSettingsPanelSize();
	}

	public void onResize(int x, int y) {
		super.onResize(x, y);
		refreshSettingsPanelSize();
	}

	public LayoutContainer getGradebookContainer() {
		return gradebookContainer;
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
	    super.onRender(parent, pos);
	    
	}
	
	private void addToolBar(GradebookModel gbModel) {
		ToolBar toolBar = new ToolBar();
		TextToolItem fileItem = new TextToolItem("File");
		toolBar.add(fileItem);
		
		Menu fileMenu = new Menu();
		
		Menu newSubMenu = new Menu();
		
		MenuItem newGradebookItem = new MenuItem("Gradebook");
		// FIXME: Turned off until this functionality is implemented
		//newSubMenu.add(newGradebookItem);
		
		MenuItem newAssignmentItem = new MenuItem("Grade Item", new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				addAssignmentDialog.show();
			}
			
		});
		newSubMenu.add(newAssignmentItem);
		
		newCategoryItem = new MenuItem("Category", new SelectionListener<MenuEvent>() {
	
			@Override
			public void componentSelected(MenuEvent ce) {
				addCategoryDialog.show();
			}
		
		});
		newSubMenu.add(newCategoryItem);
			
		newCategoryItem.setVisible(gbModel.getCategoryType() != CategoryType.NO_CATEGORIES);
		
				
		MenuItem newItems = new MenuItem("New");
		newItems.setSubMenu(newSubMenu);
		
		fileMenu.add(newItems);
		
		fileMenu.add(new SeparatorMenuItem());
		
		MenuItem importItem = new MenuItem("Import", new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				MessageBox.alert("Not Implemented", "This is a feature that will be added for milestone 3.", null);
			}
			
		});
		fileMenu.add(importItem);
		
		MenuItem exportItem = new MenuItem("Export",  new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				MessageBox.alert("Not Implemented", "This is a feature that will be added for milestone 3.", null);
			}
			
		});
		
		fileMenu.add(exportItem);
		
		fileItem.setMenu(fileMenu);
		
		if (gbModel.isUserAbleToEditAssessments() != null && gbModel.isUserAbleToEditAssessments().booleanValue()) {
			ToggleToolItem setupItem = new ToggleToolItem("Setup") {
				
				@Override
				protected void onButtonToggle(ButtonEvent be) {
					super.onButtonToggle(be);
					
					if (setupWindow == null)
						buildSetupWindow();
					
					if (isPressed()) {
						int height = InstructorViewContainer.this.getTopComponent().getOffsetHeight();
						setupWindow.setPosition(0, height);
						setupWindow.setSize(XDOM.getViewportSize().width, XDOM.getViewportSize().height - height);
						setupWindow.show();
					} else {
						setupWindow.hide();
					}
				}
				
			};
			toolBar.add(setupItem);
		}
		
		TextToolItem historyItem = new TextToolItem("History", new SelectionListener<ToolBarEvent>() {

			@Override
			public void componentSelected(ToolBarEvent ce) {
				if (historyDialog.isVisible())
					historyDialog.hide();
				
				historyDialog.show();
			}
			
		});
		toolBar.add(historyItem);
				
		/*TextToolItem windowItem = new TextToolItem("Window");
		toolBar.add(windowItem);

		Menu windowMenu = new Menu();
		MenuItem historyItem = new MenuItem("Show History", new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				if (historyDialog.isVisible())
					historyDialog.hide();
				
				historyDialog.show();
			}
			
		});
		windowMenu.add(historyItem);
		
		windowItem.setMenu(windowMenu);*/
		
		setTopComponent(toolBar);
	}
	
	private void buildSetupWindow() {
		settings = new SettingsContentPanel(gradebookUid, multigrade, this);
		
		if (addAssignmentDialog != null)
			addAssignmentDialog.setSettingsPanel(settings);
		if (addCategoryDialog != null)
			addCategoryDialog.setSettingsPanel(settings);
		
		setupWindow = new Window();
		setupWindow.add(settings);
		setupWindow.setClosable(false);
		setupWindow.setDraggable(false);
		setupWindow.setFrame(false);
		setupWindow.setHeaderVisible(false);
		setupWindow.setLayout(new FitLayout());
		setupWindow.setModal(false);
		setupWindow.setResizable(false);
		
		setupWindow.addListener(Events.BeforeShow, new Listener<WindowEvent>() {

			public void handleEvent(WindowEvent be) {
				multigrade.hide();
			}
			
		});
		
		setupWindow.addListener(Events.Hide, new Listener<WindowEvent>() {

			public void handleEvent(WindowEvent be) {
				multigrade.show();
			}
			
		});
	}
	
	private void refreshSettingsPanelSize() {
		if (setupWindow != null) {
			int height = InstructorViewContainer.this.getTopComponent().getOffsetHeight();
			setupWindow.setPosition(0, height);
			setupWindow.setSize(XDOM.getViewportSize().width, XDOM.getViewportSize().height - height);
		}
	}

	public MultiGradeContentPanel getMultigrade() {
		return multigrade;
	}

	public HistoryDialog getHistoryDialog() {
		return historyDialog;
	}
	
}
