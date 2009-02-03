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

import org.sakaiproject.gradebook.gwt.client.PersistentStore;
import org.sakaiproject.gradebook.gwt.client.action.RemoteCommand;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction.ClassType;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.UserChangeEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.MultiGradeContentPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.settings.AddAssignmentDialog;
import org.sakaiproject.gradebook.gwt.client.gxt.settings.AddCategoryDialog;
import org.sakaiproject.gradebook.gwt.client.gxt.settings.SettingsAssignmentContentPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.settings.SettingsCategoryContentPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.settings.SettingsContentPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.settings.SettingsGradingScaleContentPanel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.Key;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.XDOM;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.TabPanel.TabPosition;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.AdapterMenuItem;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.KeyboardListener;

public class InstructorViewContainer extends ContentPanel {

	private final static String TAB_GRADES_NAME = "Grades";
	private final static String TAB_CATEGORIES_NAME = "Categories";
	private final static String TAB_ITEMS_NAME = "Items";
	private final static String TAB_SCALE_NAME = "Grading Scale";
	private final static String TAB_HISTORY_NAME = "History";
	
	private MultiGradeContentPanel multigrade;
	private SettingsContentPanel settings;
	private AddAssignmentDialog addAssignmentDialog;
	private AddCategoryDialog addCategoryDialog;
	private SettingsCategoryContentPanel categoriesPanel;
	private SettingsAssignmentContentPanel assignmentsPanel;
	private SettingsGradingScaleContentPanel gradingScalePanel;
	private HistoryDialog historyDialog;
	private Window setupWindow;
	private LayoutContainer gradebookContainer;
	private String gradebookUid;
	
	private Menu windowMenu;
	private MenuItem newCategoryItem, categoriesWindowMenuItem;
	
	private boolean tabMode = true;
	
	private TabPanel tabPanel;
	
	public InstructorViewContainer(String gradebookUid, LayoutContainer gradebookContainer) {
		this.gradebookUid = gradebookUid;
		this.gradebookContainer = gradebookContainer;
		setHeaderVisible(false);
		setLayout(new FitLayout());
		setMonitorResize(true);
	
		GradebookModel gbModel = Registry.get(gradebookUid);
			
		multigrade = new MultiGradeContentPanel(gradebookUid, this);	
		
		categoriesPanel = new SettingsCategoryContentPanel(gradebookUid);
		
		assignmentsPanel = new SettingsAssignmentContentPanel(gradebookUid, categoriesPanel);
		
		gradingScalePanel = new SettingsGradingScaleContentPanel(gradebookUid);
		
		addAssignmentDialog = new AddAssignmentDialog(gbModel.getGradebookUid(), this);
		
		addCategoryDialog = new AddCategoryDialog(gradebookUid, this);
		
		historyDialog = new HistoryDialog(gradebookUid);
		
		String storedTabMode = PersistentStore.getPersistentField(gradebookUid, "tabMode", "checked");
		if (storedTabMode != null) {
			Boolean isChecked = Boolean.valueOf(storedTabMode);
			tabMode = isChecked != null && isChecked.booleanValue();
		}
		
		if (tabMode)
			buildTabPanel(gbModel, TabPosition.TOP);
		else
			add(multigrade);
		
		addToolBar(gbModel);
	}
	
	public void showAddCategory(boolean show) {
		if (newCategoryItem != null && isTabVisible(TAB_CATEGORIES_NAME))
			newCategoryItem.setVisible(show);
		
		if (categoriesWindowMenuItem != null)
			categoriesWindowMenuItem.setVisible(show);
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
		TextToolItem fileItem = new TextToolItem("New");
		fileItem.setMenu(buildFileMenu(gbModel));
		
		/*ToggleToolItem setupItem = null;
		if (gbModel.isUserAbleToEditAssessments() != null && gbModel.isUserAbleToEditAssessments().booleanValue()) {
			setupItem = new ToggleToolItem("Setup") {
				
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
		}*/
		
		TextToolItem preferencesItem = new TextToolItem("Preferences");
		preferencesItem.setMenu(buildPreferencesMenu(gbModel));
		
		TextToolItem windowItem = new TextToolItem("View");
		windowMenu = buildWindowMenu(gbModel);
		windowItem.setMenu(windowMenu);
		
		TextToolItem moreItem = new TextToolItem("More Actions");
		moreItem.setMenu(buildMoreActionsMenu(gbModel));
		
		
		/*
		TextToolItem historyItem = new TextToolItem("History", new SelectionListener<ToolBarEvent>() {

			@Override
			public void componentSelected(ToolBarEvent ce) {
				int height = InstructorViewContainer.this.getTopComponent().getOffsetHeight();
				historyDialog.setPosition(0, height);
				historyDialog.setSize(XDOM.getViewportSize().width, XDOM.getViewportSize().height - height);
				
				if (historyDialog.isVisible())
					historyDialog.toFront();
				else
					historyDialog.show();
			}
			
		});
		*/
				
		toolBar.add(fileItem);
		//if (setupItem != null)
		//	toolBar.add(setupItem);
		toolBar.add(preferencesItem);
		//toolBar.add(historyItem);
		toolBar.add(windowItem);
		toolBar.add(moreItem);
		
		setTopComponent(toolBar);
	}
	
	private Menu buildFileMenu(GradebookModel gbModel) {
		Menu fileMenu = new Menu();
		
		//Menu newSubMenu = new Menu();
		
		//MenuItem newGradebookItem = new MenuItem("Gradebook");
		// FIXME: Turned off until this functionality is implemented
		//newSubMenu.add(newGradebookItem);
		
		newCategoryItem = new MenuItem("Category", new SelectionListener<MenuEvent>() {
			
			@Override
			public void componentSelected(MenuEvent ce) {
				addCategoryDialog.show();
			}
		
		});
		//newSubMenu.add(newCategoryItem);
		fileMenu.add(newCategoryItem);	
		
		newCategoryItem.setVisible(gbModel.getCategoryType() != CategoryType.NO_CATEGORIES);
		
		
		MenuItem newAssignmentItem = new MenuItem("Item", new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				addAssignmentDialog.show();
			}
			
		});
		//newSubMenu.add(newAssignmentItem);
		fileMenu.add(newAssignmentItem);
		
				
		/*MenuItem newItems = new MenuItem("New");
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
		
		*/
		return fileMenu;
	}
	
	private Menu buildPreferencesMenu(final GradebookModel gbModel) {
		final Menu preferencesMenu = new Menu();
	
		// Enable popups
		CheckMenuItem enableNotifications = new CheckMenuItem("Enable informational popups");
		preferencesMenu.add(enableNotifications);
		
		String storedEnableNotifications = PersistentStore.getPersistentField(gradebookUid, "enableNotifications", "checked");
		if (storedEnableNotifications != null) {
			Boolean isChecked = Boolean.valueOf(storedEnableNotifications);
			if (isChecked != null) {
				Registry.register("enableNotifications", isChecked);
				enableNotifications.setChecked(isChecked.booleanValue());
			}
		}
		
		enableNotifications.addListener(Events.CheckChange, new Listener<MenuEvent>() {

			public void handleEvent(MenuEvent me) {
				CheckMenuItem enableNotifications = (CheckMenuItem)me.item;
				Boolean isChecked = Boolean.valueOf(enableNotifications.isChecked());
				if (Registry.get("enableNotifications") != null) 
					Registry.unregister("enableNotifications");
				Registry.register("enableNotifications", isChecked);
				
				PersistentStore.storePersistentField(gradebookUid, "enableNotifications", "checked", isChecked.toString());
			}
			
		});
		
		
		
		// Tab locations
		/*MenuItem chooseTabLocation = new MenuItem("Where do you want to see tabs?");
		
		Menu chooseTabSubMenu = new Menu();
		CheckMenuItem tabTopLocation = new CheckMenuItem("Top");
		tabTopLocation.setId("tablocation:top");
		tabTopLocation.setGroup("tablocation");
		chooseTabSubMenu.add(tabTopLocation);
		CheckMenuItem tabBottomLocation = new CheckMenuItem("Bottom");
		tabBottomLocation.setId("tablocation:bottom");
		tabBottomLocation.setGroup("tablocation");
		chooseTabSubMenu.add(tabBottomLocation);
		
		
		// Set the initial value
		tabTopLocation.setChecked(true);
		
		Listener<MenuEvent> tabLocationListener = new Listener<MenuEvent>() {

			public void handleEvent(MenuEvent me) {
				CheckMenuItem checkItem = (CheckMenuItem)me.item;
				String itemId = checkItem.getId();
				
				if (itemId.equals("tablocation:top") && checkItem.isChecked()) {
					tabPanel.removeFromParent();
					buildTabPanel(gbModel, TabPosition.TOP);
					layout();
				}
				if (itemId.equals("tablocation:bottom") && checkItem.isChecked()) {
					tabPanel.removeFromParent();
					buildTabPanel(gbModel, TabPosition.BOTTOM);
					layout();
				}
			}
			
		};
		tabTopLocation.addListener(Events.CheckChange, tabLocationListener);
		tabBottomLocation.addListener(Events.CheckChange, tabLocationListener);
		
		chooseTabLocation.setSubMenu(chooseTabSubMenu);
		preferencesMenu.add(chooseTabLocation);
		*/
		
		// Separator
		preferencesMenu.add(new SeparatorMenuItem());
		
		// Gradebook name
		final InlineEditField<String> name = new InlineEditField<String>();
		name.setValue(gbModel.getName());
		
		
	    name.addListener(Events.Change, new Listener<FieldEvent>() {

			public void handleEvent(FieldEvent fe) {
				Object actionValue = fe.value;
				doUpdate(gbModel, GradebookModel.Key.NAME, ClassType.STRING, actionValue, gbModel.getName());
			}
	    });
		
		MenuItem gradebookName = new MenuItem("What do you want to call this gradebook?");
		//gradebookName.setHideOnClick(false);
		Menu gradebookNameSubMenu = new Menu();
		AdapterMenuItem gradebookNameChanger = new AdapterMenuItem(name);
		gradebookNameChanger.setHideOnClick(false);
		
		name.addKeyListener(new KeyListener() {
	    	public void componentKeyPress(ComponentEvent event) {
			    switch (event.getKeyCode()) {
			    case KeyboardListener.KEY_ENTER:
			    	name.complete();
			    	preferencesMenu.hide();
			    	break;
			    }
			}
	    });
		
		gradebookNameSubMenu.add(gradebookNameChanger);
		gradebookName.setSubMenu(gradebookNameSubMenu);
		preferencesMenu.add(gradebookName);
		
		
		// Choose a category type
		SimpleComboBox<String> typePicker = new SimpleComboBox<String>(); 
		typePicker.setAllQuery(null);
		typePicker.setEditable(false);
		typePicker.setFieldLabel("Organization Type");
		typePicker.setForceSelection(true);
		typePicker.setName("organizationtype");
		typePicker.add("No Categories");
		typePicker.add("Categories");
		typePicker.add("Weighted Categories");
	    
		switch (gbModel.getCategoryType()) {
	    case NO_CATEGORIES:
	    	typePicker.setSimpleValue("No Categories");
	    	break;
	    case SIMPLE_CATEGORIES:
	    	typePicker.setSimpleValue("Categories");
	    	break;	
	    case WEIGHTED_CATEGORIES:
	    	typePicker.setSimpleValue("Weighted Categories");
	    	break;	
	    }
	    
	    typePicker.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
				GradebookModel gbModel = Registry.get(gradebookUid);
				ClassType classType = ClassType.CATEGORYTYPE;
				Key property = Key.CATEGORYTYPE;
				String value = se.getSelectedItem().getValue();
				CategoryType actionValue = null;
				CategoryType actionStartValue = gbModel.getCategoryType();
				
				if (value.equals("No Categories")) {
					actionValue = CategoryType.NO_CATEGORIES;
					showAddCategory(false);
				} else if (value.equals("Categories")) {
					actionValue = CategoryType.SIMPLE_CATEGORIES;
					showAddCategory(true);
				} else if (value.equals("Weighted Categories")) {
					actionValue = CategoryType.WEIGHTED_CATEGORIES;
					showAddCategory(true);
				}
				
				doUpdate(gbModel, property, classType, actionValue, actionStartValue);
			}

	    	
	    });
		
	    MenuItem chooseCategoryType = new MenuItem("How will you organize your gradebook?");
	    
		AdapterMenuItem categoryTypePicker = new AdapterMenuItem(typePicker);
		
		Menu categoryTypeSubMenu = new Menu();
		categoryTypeSubMenu.add(categoryTypePicker);
		chooseCategoryType.setSubMenu(categoryTypeSubMenu);
		preferencesMenu.add(chooseCategoryType);
		
		// Choose a grade type
		MenuItem chooseGradeType = new MenuItem("How will you enter grades?");
		
		Menu gradeTypeSubMenu = new Menu();
		CheckMenuItem points = new CheckMenuItem("Points");
		points.setId("gradetype:points");
		points.setGroup("gradetype");
		gradeTypeSubMenu.add(points);
		CheckMenuItem percentages = new CheckMenuItem("Percentages");
		percentages.setId("gradetype:percentages");
		percentages.setGroup("gradetype");
		
		gradeTypeSubMenu.add(percentages);
		
		
		// Set the initial value
		switch (gbModel.getGradeType()) {
		case POINTS:
			points.setChecked(true);
			percentages.setChecked(false);
			break;
		case PERCENTAGES:
			percentages.setChecked(true);
			points.setChecked(false);
			break;
		}
		
		Listener<MenuEvent> gradeTypeListener = new Listener<MenuEvent>() {

			public void handleEvent(MenuEvent me) {
				CheckMenuItem checkItem = (CheckMenuItem)me.item;
				String itemId = checkItem.getId();
				GradeType actionValue = null;
				GradeType actionStartValue = null;
				if (itemId.equals("gradetype:points") && checkItem.isChecked()) {
					actionValue = GradeType.POINTS;
					actionStartValue = GradeType.PERCENTAGES;
				}
				if (itemId.equals("gradetype:percentages") && checkItem.isChecked()) {
					actionValue = GradeType.PERCENTAGES;
					actionStartValue = GradeType.POINTS;
				}
				
				
				if (actionValue != null)
					doUpdate(gbModel, GradebookModel.Key.GRADETYPE, ClassType.GRADETYPE, actionValue, actionStartValue);
			}
			
		};
		points.addListener(Events.CheckChange, gradeTypeListener);
		percentages.addListener(Events.CheckChange, gradeTypeListener);
		
		chooseGradeType.setSubMenu(gradeTypeSubMenu);
		preferencesMenu.add(chooseGradeType);
				
		
		// Choose a grade type
		MenuItem releaseCourseGrades = new MenuItem("Do you want to release course grades to learners?");
		
		Menu releaseCourseGradesSubMenu = new Menu();
		CheckMenuItem yes = new CheckMenuItem("Yes");
		yes.setId("releasegrades:yes");
		yes.setGroup("releasegrades");
		releaseCourseGradesSubMenu.add(yes);
		CheckMenuItem no = new CheckMenuItem("No");
		no.setId("releasegrades:no");
		no.setGroup("releasegrades");
		releaseCourseGradesSubMenu.add(no);
		
		
		// Set the initial value
		boolean isReleaseGrades = gbModel.isReleaseGrades() != null && gbModel.isReleaseGrades().booleanValue();
		if (isReleaseGrades) {
			yes.setChecked(true);
			no.setChecked(false);
		} else {
			no.setChecked(true);
			yes.setChecked(false);
		}
		
		Listener<MenuEvent> releaseGradesListener = new Listener<MenuEvent>() {

			public void handleEvent(MenuEvent me) {
				CheckMenuItem checkItem = (CheckMenuItem)me.item;
				String itemId = checkItem.getId();
				Boolean actionValue = null;
				Boolean actionStartValue = null;
				if (itemId.equals("releasegrades:yes") && checkItem.isChecked()) {
					actionValue = Boolean.TRUE;
					actionStartValue = Boolean.FALSE;
				}
				if (itemId.equals("releasegrades:no") && checkItem.isChecked()) {
					actionValue = Boolean.FALSE;
					actionStartValue = Boolean.TRUE;
				}
				
				if (actionValue != null)
					doUpdate(gbModel, GradebookModel.Key.RELEASEGRADES, ClassType.BOOLEAN, actionValue, actionStartValue);
			}
			
		};
		yes.addListener(Events.CheckChange, releaseGradesListener);
		no.addListener(Events.CheckChange, releaseGradesListener);
		
		releaseCourseGrades.setSubMenu(releaseCourseGradesSubMenu);
		preferencesMenu.add(releaseCourseGrades);
		
		/* Menu radioMenu = new Menu();  
		     CheckMenuItem r = new CheckMenuItem("Blue Theme");  
		     r.setGroup("radios");  
		     r.setChecked(true);  
		     radioMenu.add(r);  
		     r = new CheckMenuItem("Gray Theme");  
		     r.setGroup("radios");  
		     radioMenu.add(r);  
		     radios.setSubMenu(radioMenu); */
		
		
		return preferencesMenu;
	}
	
	private Menu buildWindowMenu(final GradebookModel gbModel) {
		final Menu windowMenu = new Menu();
	
		categoriesWindowMenuItem = buildWindowMenuItem(TAB_CATEGORIES_NAME, categoriesPanel, 1);
		windowMenu.add(categoriesWindowMenuItem);
		categoriesWindowMenuItem.setVisible(gbModel.getCategoryType() != CategoryType.NO_CATEGORIES);
			
		windowMenu.add(buildWindowMenuItem(TAB_ITEMS_NAME, assignmentsPanel, 2));
		
		windowMenu.add(buildWindowMenuItem(TAB_SCALE_NAME, gradingScalePanel, 3));
		
		windowMenu.add(buildWindowMenuItem(TAB_HISTORY_NAME, historyDialog, 4));
		
		return windowMenu;
	}
	
	private MenuItem buildWindowMenuItem(final String name, final ContentPanel contentPanel, final int order) {
		CheckMenuItem menuItem = new CheckMenuItem(name);
		menuItem.setChecked(true);
		menuItem.setId("windowMenu:" + name);
		
		String storedTabVisibility = PersistentStore.getPersistentField(gradebookUid, "tab", name);
		if (storedTabVisibility != null) {
			Boolean isChecked = Boolean.valueOf(storedTabVisibility);
			if (isChecked != null) {
				menuItem.setChecked(isChecked.booleanValue());
			}
		}
		
		menuItem.addListener(Events.CheckChange, new Listener<MenuEvent>() {

			public void handleEvent(MenuEvent me) {
				CheckMenuItem menuItem = (CheckMenuItem)me.item;
				Boolean isChecked = Boolean.valueOf(menuItem.isChecked());
				
				if (menuItem.isChecked())
					insertTab(name, contentPanel, order);
				else
					removeTab(name);
			}
			
		});
		
		return menuItem;
	}
	
	private Menu buildMoreActionsMenu(GradebookModel gbModel) {
		Menu moreActionsMenu = new Menu();
		
		MenuItem importItem = new MenuItem("Import", new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				MessageBox.alert("Not Implemented", "This is a feature that will be added for milestone 3.", null);
			}
			
		});
		moreActionsMenu.add(importItem);
		
		MenuItem exportItem = new MenuItem("Export",  new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				MessageBox.alert("Not Implemented", "This is a feature that will be added for milestone 3.", null);
			}
			
		});
		
		moreActionsMenu.add(exportItem);
		
		return moreActionsMenu;
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

	
	private void buildTabPanel(GradebookModel gbModel, TabPosition tabPosition) {
		tabPanel = new TabPanel();
		tabPanel.setResizeTabs(true);
		tabPanel.setMinTabWidth(150);
		tabPanel.setTabPosition(tabPosition);
		
		insertTab(TAB_GRADES_NAME, multigrade, 0);
		
		if (gbModel.isUserAbleToEditAssessments() != null && gbModel.isUserAbleToEditAssessments().booleanValue()) {
			
			if (gbModel.getCategoryType() != CategoryType.NO_CATEGORIES && isTabVisible(TAB_CATEGORIES_NAME)) 
				insertTab(TAB_CATEGORIES_NAME, categoriesPanel, 1);
		
			if (isTabVisible(TAB_ITEMS_NAME))
				insertTab(TAB_ITEMS_NAME, assignmentsPanel, 2);
			
			if (isTabVisible(TAB_SCALE_NAME))
				insertTab(TAB_SCALE_NAME, gradingScalePanel, 3);

			addListener(GradebookEvents.UserChange, new Listener<UserChangeEvent>() {

				public void handleEvent(UserChangeEvent uce) {
					if (!uce.isTransferred()) {
						uce.setTransferred(true);
						multigrade.fireEvent(GradebookEvents.UserChange, uce);
						categoriesPanel.fireEvent(GradebookEvents.UserChange, uce);
						assignmentsPanel.fireEvent(GradebookEvents.UserChange, uce);
						historyDialog.fireEvent(GradebookEvents.UserChange, uce);
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
									if (isTabVisible(TAB_CATEGORIES_NAME))
										removeTab(TAB_CATEGORIES_NAME);
									
									/*if (categoriesTab != null) {
										tabPanel.remove(categoriesTab);
										categoriesTab = null;
									}*/
									showAddCategory(false);
									break;
								default:
									if (!isTabVisible(TAB_CATEGORIES_NAME))
										insertTab(TAB_CATEGORIES_NAME, categoriesPanel, 1);
								
									showAddCategory(true);
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
					InstructorViewContainer.this.fireEvent(GradebookEvents.UserChange, uce);				
				}
				
			});
			
			categoriesPanel.addListener(GradebookEvents.UserChange, new Listener<UserChangeEvent>() {
				
				public void handleEvent(UserChangeEvent uce) {
					InstructorViewContainer.this.fireEvent(GradebookEvents.UserChange, uce);
				}
				
			});
			
			gradingScalePanel.addListener(GradebookEvents.UserChange, new Listener<UserChangeEvent>() {

				public void handleEvent(UserChangeEvent uce) {
					multigrade.fireEvent(GradebookEvents.UserChange, uce);
				}
				
			});
		}
		
		if (isTabVisible(TAB_HISTORY_NAME))
			insertTab(TAB_HISTORY_NAME, historyDialog, 4);
		
		
		tabPanel.addListener(Events.Remove, new Listener<TabPanelEvent>() {

			public void handleEvent(TabPanelEvent tpe) {
				if (tpe.container.getItemCount() == 1) {
					removeTab(TAB_GRADES_NAME);
					InstructorViewContainer.this.remove(tabPanel);
					InstructorViewContainer.this.add(multigrade);
					InstructorViewContainer.this.layout();
					tabMode = false;
					PersistentStore.storePersistentField(gradebookUid, "tabMode", "checked", Boolean.FALSE.toString());
				}
				/*String name = tpe.item.getText();
				CheckMenuItem menuItem = (CheckMenuItem)windowMenu.getItemByItemId("windowMenu:" + name);
				if (menuItem != null) {
					menuItem.setChecked(false);
				}*/
				
			}
			
		});
		
		
		/*TabItem historyTab = new TabItem("History");  
		historyTab.addStyleName("pad-text");
		historyTab.add(historyDialog);
		historyTab.setClosable(true);
		historyTab.setLayout(new FitLayout());
		tabPanel.add(historyTab);
		*/
		add(tabPanel);
	}
	
	private void doUpdate(GradebookModel gbModel, Key property, ClassType classType, Object actionValue, Object actionStartValue) {
		if (actionStartValue == null || !actionStartValue.equals(actionValue)) {
			
			UserEntityUpdateAction<GradebookModel> action = 
				new UserEntityUpdateAction<GradebookModel>(gbModel, gbModel, 
						property.name(), classType, actionValue, actionStartValue);
			
			final Key p = property;
			
			RemoteCommand<GradebookModel> remoteCommand = 
				new RemoteCommand<GradebookModel>() {

					@Override
					public void onCommandSuccess(UserEntityAction<GradebookModel> action, GradebookModel result) {
						Registry.unregister(gradebookUid);
						Registry.register(gradebookUid, result);
						action.announce(result.getName(), p.name(), action.getValue());

						InstructorViewContainer.this.fireEvent(GradebookEvents.UserChange, new UserChangeEvent(action));
					}
				
			};
			
			
			remoteCommand.execute(action);
		}
	}
	
	private boolean isTabVisible(String name) {
		String storedTabVisibility = PersistentStore.getPersistentField(gradebookUid, "tab", name);
		if (storedTabVisibility != null) {
			Boolean isChecked = Boolean.valueOf(storedTabVisibility);
			
			return isChecked.booleanValue();
		}
		
		return true;
	}
	
	private TabItem insertTab(final String name, final ContentPanel contentPanel, int order) {
		if (!tabMode) {
			tabMode = true;
			PersistentStore.storePersistentField(gradebookUid, "tabMode", "checked", Boolean.TRUE.toString());
			if (tabPanel == null) {
				GradebookModel gbModel = Registry.get(gradebookUid);
				buildTabPanel(gbModel,  TabPosition.TOP);
			} else {
				add(tabPanel);
				insertTab("Grades", multigrade, 0);
			}
			layout();
		}
		
		TabItem tab = new TabItem(name);  
		tab.addStyleName("pad-text");  
		tab.add(contentPanel);
		tab.setClosable(order != 0);
		tab.setLayout(new FitLayout());
		tab.setItemId("tab:" + name);
		
		tab.addListener(Events.Select, new Listener<TabPanelEvent>() {

			public void handleEvent(TabPanelEvent tpe) {
				contentPanel.fireEvent(GradebookEvents.Refresh);
			}
			
		});
		
		tab.addListener(Events.Close, new Listener<TabPanelEvent>() {

			public void handleEvent(TabPanelEvent tpe) {
				/*if (tpe.container.getItemCount() == 1) {
					removeTab("Grades");
					InstructorViewContainer.this.remove(tabPanel);
					InstructorViewContainer.this.add(multigrade);
					InstructorViewContainer.this.layout();
					tabMode = false;
				}*/
				CheckMenuItem menuItem = (CheckMenuItem)windowMenu.getItemByItemId("windowMenu:" + name);
				if (menuItem != null) {
					menuItem.setChecked(false);
					PersistentStore.storePersistentField(gradebookUid, "tab", name, Boolean.FALSE.toString());
				}
			}
			
		});
		
		if (order != 0 && order > tabPanel.getItemCount())
			tabPanel.add(tab);
		else
			tabPanel.insert(tab, order);
		
		
		PersistentStore.storePersistentField(gradebookUid, "tab", name, Boolean.TRUE.toString());
		
		return tab;
	}
	
	private void removeTab(String name) {
		TabItem item = tabPanel.getItemByItemId("tab:" + name);
		if (item != null) {
			tabPanel.remove(item);
			
			PersistentStore.storePersistentField(gradebookUid, "tab", name, Boolean.FALSE.toString());
		}
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
