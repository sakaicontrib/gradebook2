package org.sakaiproject.gradebook.gwt.client.gxt.view;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.PersistentStore;
import org.sakaiproject.gradebook.gwt.client.action.RemoteCommand;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction.ClassType;
import org.sakaiproject.gradebook.gwt.client.gxt.InlineEditField;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaAdapterMenuItem;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaCheckMenuItem;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaMenu;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaMenuItem;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.Key;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.menu.AdapterMenuItem;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.KeyboardListener;

public class PreferencesMenu extends Menu {

	private CheckMenuItem enablePopupsMenuItem;
	private InlineEditField<String> gradebookNameTextBox;
	private SimpleComboBox<String> typePickerComboBox;
	private CheckMenuItem pointsMenuItem;
	private CheckMenuItem percentagesMenuItem;
	private CheckMenuItem releaseGradesYesMenuItem;
	private CheckMenuItem releaseGradesNoMenuItem;
	
	private KeyListener keyListener;
	private Listener<MenuEvent> menuListener;
	private Listener<FieldEvent> fieldEventListener;
	private SelectionChangedListener<SimpleComboValue<String>> selectionChangedListener;
	
	public PreferencesMenu(I18nConstants i18n) {
		initListeners(i18n);
		
		// Enable popups
		enablePopupsMenuItem = new AriaCheckMenuItem(i18n.prefMenuEnablePopups());
		enablePopupsMenuItem.setId(AppConstants.ID_ENABLE_POPUPS_MENUITEM);
		add(enablePopupsMenuItem);

		enablePopupsMenuItem.addListener(Events.CheckChange, menuListener);
		
		// Separator
		add(new SeparatorMenuItem());
		
		// Gradebook name
		gradebookNameTextBox = new InlineEditField<String>();
	    gradebookNameTextBox.addListener(Events.Change, fieldEventListener);
		
		MenuItem gradebookName = new AriaMenuItem(i18n.prefMenuGradebookName());
		Menu gradebookNameSubMenu = new AriaMenu();
		AdapterMenuItem gradebookNameChanger = new AriaAdapterMenuItem(gradebookNameTextBox);
		gradebookNameChanger.setHideOnClick(false);
		
		gradebookNameTextBox.addKeyListener(keyListener);
		
		gradebookNameSubMenu.add(gradebookNameChanger);
		gradebookName.setSubMenu(gradebookNameSubMenu);
		add(gradebookName);
		
		// Choose a category type
		typePickerComboBox = new SimpleComboBox<String>(); 
		typePickerComboBox.setAllQuery(null);
		typePickerComboBox.setEditable(false);
		typePickerComboBox.setFieldLabel(i18n.prefMenuOrgTypeLabel());
		typePickerComboBox.setForceSelection(true);
		//typePicker.setName("organizationtype");
		typePickerComboBox.add(i18n.orgTypeNoCategories());
		typePickerComboBox.add(i18n.orgTypeCategories());
		typePickerComboBox.add(i18n.orgTypeWeightedCategories());
	    
	    typePickerComboBox.addSelectionChangedListener(selectionChangedListener);
	    
		
	    MenuItem chooseCategoryType = new AriaMenuItem(i18n.prefMenuOrgTypeHeader());
	    
		AdapterMenuItem categoryTypePicker = new AriaAdapterMenuItem(typePickerComboBox);
		
		Menu categoryTypeSubMenu = new AriaMenu();
		categoryTypeSubMenu.add(categoryTypePicker);
		chooseCategoryType.setSubMenu(categoryTypeSubMenu);
		add(chooseCategoryType);
		
		// Choose a grade type
		MenuItem chooseGradeType = new AriaMenuItem(i18n.prefMenuGradeTypeHeader());
		
		Menu gradeTypeSubMenu = new AriaMenu() {
			protected void onRender(Element parent, int pos) {
			    super.onRender(parent, pos);
			    Accessibility.setRole(el().dom, "menu");
			}
		};
		pointsMenuItem = new AriaCheckMenuItem(i18n.gradeTypePoints());
		pointsMenuItem.setId(AppConstants.ID_GT_POINTS_MENUITEM);
		pointsMenuItem.setGroup("gradetype");
		gradeTypeSubMenu.add(pointsMenuItem);
		percentagesMenuItem = new AriaCheckMenuItem(i18n.gradeTypePercentages());
		percentagesMenuItem.setId(AppConstants.ID_GT_PERCENTAGES_MENUITEM);
		percentagesMenuItem.setGroup("gradetype");
		
		gradeTypeSubMenu.add(percentagesMenuItem);
		
		pointsMenuItem.addListener(Events.CheckChange, menuListener);
		percentagesMenuItem.addListener(Events.CheckChange, menuListener);
		
		chooseGradeType.setSubMenu(gradeTypeSubMenu);
		add(chooseGradeType);
		
		// Choose a grade type
		MenuItem releaseCourseGrades = new AriaMenuItem("Do you want to release course grades to learners?");
		
		Menu releaseCourseGradesSubMenu = new AriaMenu();
		releaseGradesYesMenuItem = new AriaCheckMenuItem(i18n.prefMenuReleaseGradesYes());
		releaseGradesYesMenuItem.setId(AppConstants.ID_RG_YES_MENUITEM);
		releaseGradesYesMenuItem.setGroup("releasegrades");
		releaseCourseGradesSubMenu.add(releaseGradesYesMenuItem);
		releaseGradesNoMenuItem = new AriaCheckMenuItem(i18n.prefMenuReleaseGradesNo());
		releaseGradesNoMenuItem.setId(AppConstants.ID_RG_NO_MENUITEM);
		releaseGradesNoMenuItem.setGroup("releasegrades");
		releaseCourseGradesSubMenu.add(releaseGradesNoMenuItem);
		
		releaseGradesYesMenuItem.addListener(Events.CheckChange, menuListener);
		releaseGradesNoMenuItem.addListener(Events.CheckChange, menuListener);
		
		releaseCourseGrades.setSubMenu(releaseCourseGradesSubMenu);
		add(releaseCourseGrades);
	}

	
	protected void initListeners(final I18nConstants i18n) {
		fieldEventListener = new Listener<FieldEvent>() {

			public void handleEvent(FieldEvent fe) {
				Object actionValue = fe.value;
				GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
				doUpdate(selectedGradebook, GradebookModel.Key.NAME, ClassType.STRING, actionValue, selectedGradebook.getName());
			}
	    };
	    
	    keyListener = new KeyListener() {
	    	public void componentKeyPress(ComponentEvent event) {
			    switch (event.getKeyCode()) {
			    case KeyboardListener.KEY_ENTER:
			    	gradebookNameTextBox.complete();
			    	PreferencesMenu.this.hide();
			    	break;
			    }
			}
	    };
	    
	    menuListener = new Listener<MenuEvent>() {

			public void handleEvent(MenuEvent me) {
				GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
				String gradebookUid = selectedGradebook.getGradebookUid();
				
				CheckMenuItem checkItem = (CheckMenuItem)me.item;
				String itemId = checkItem.getId();
				
				// TODO: Is there a more efficient solution here -- rather than doing all these if else comparisons?
				if (itemId.equals(AppConstants.ID_ENABLE_POPUPS_MENUITEM)) {	
					Boolean isChecked = Boolean.valueOf(checkItem.isChecked());
					if (Registry.get(AppConstants.ENABLE_POPUPS) != null) 
						Registry.unregister(AppConstants.ENABLE_POPUPS);
					Registry.register(AppConstants.ENABLE_POPUPS, isChecked);
					
					PersistentStore.storePersistentField(gradebookUid, AppConstants.ENABLE_POPUPS, "checked", isChecked.toString());
				} else if (itemId.equals(AppConstants.ID_GT_POINTS_MENUITEM)) {
					selectGradeType(selectedGradebook, GradeType.POINTS, checkItem.isChecked());
				} else if (itemId.equals(AppConstants.ID_GT_PERCENTAGES_MENUITEM)) {
					selectGradeType(selectedGradebook, GradeType.PERCENTAGES, checkItem.isChecked());				
				} else if (itemId.equals(AppConstants.ID_RG_YES_MENUITEM)) {
					selectReleaseGrades(selectedGradebook, true, checkItem.isChecked());
				} else if (itemId.equals(AppConstants.ID_RG_NO_MENUITEM)) {
					selectReleaseGrades(selectedGradebook, false, checkItem.isChecked());
				}
				
			}
			
		};
		
		selectionChangedListener = new SelectionChangedListener<SimpleComboValue<String>>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
				GradebookModel gbModel = Registry.get(AppConstants.CURRENT);
				ClassType classType = ClassType.CATEGORYTYPE;
				Key property = Key.CATEGORYTYPE;
				String value = se.getSelectedItem().getValue();
				CategoryType actionValue = null;
				CategoryType actionStartValue = gbModel.getCategoryType();
				
				if (value.equals(i18n.orgTypeNoCategories())) {
					actionValue = CategoryType.NO_CATEGORIES;
					// FIXME: Need to move this logic to user change event listener
					//showAddCategory(false);
				} else if (value.equals(i18n.orgTypeCategories())) {
					actionValue = CategoryType.SIMPLE_CATEGORIES;
					//showAddCategory(true);
				} else if (value.equals(i18n.orgTypeWeightedCategories())) {
					actionValue = CategoryType.WEIGHTED_CATEGORIES;
					//showAddCategory(true);
				}
				
				doUpdate(gbModel, property, classType, actionValue, actionStartValue);
			}

	    	
	    };
	}
	

	public void onSwitchGradebook(GradebookModel selectedGradebook) {
		// Initialize enable popups checkbox
		String storedEnableNotifications = PersistentStore.getPersistentField(selectedGradebook.getGradebookUid(), "enableNotifications", "checked");
		if (storedEnableNotifications != null) {
			Boolean isChecked = Boolean.valueOf(storedEnableNotifications);
			if (isChecked != null) {
				Registry.register(AppConstants.ENABLE_POPUPS, isChecked);
				enablePopupsMenuItem.setChecked(isChecked.booleanValue());
			}
		}

		// Initialize gradebook name textbox
		gradebookNameTextBox.setValue(selectedGradebook.getName());
		
		// Initialize type picker
		switch (selectedGradebook.getCategoryType()) {
	    case NO_CATEGORIES:
	    	typePickerComboBox.setSimpleValue("No Categories");
	    	break;
	    case SIMPLE_CATEGORIES:
	    	typePickerComboBox.setSimpleValue("Categories");
	    	break;	
	    case WEIGHTED_CATEGORIES:
	    	typePickerComboBox.setSimpleValue("Weighted Categories");
	    	break;	
	    }
		
		// Initialize grade type
		switch (selectedGradebook.getGradeType()) {
		case POINTS:
			pointsMenuItem.setChecked(true);
			percentagesMenuItem.setChecked(false);
			break;
		case PERCENTAGES:
			percentagesMenuItem.setChecked(true);
			pointsMenuItem.setChecked(false);
			break;
		}
		
		// Initialize release grades radio choice
		boolean isReleaseGrades = selectedGradebook.isReleaseGrades() != null && selectedGradebook.isReleaseGrades().booleanValue();
		if (isReleaseGrades) {
			releaseGradesYesMenuItem.setChecked(true);
			releaseGradesNoMenuItem.setChecked(false);
		} else {
			releaseGradesNoMenuItem.setChecked(true);
			releaseGradesYesMenuItem.setChecked(false);
		}
	}
	
	private void selectGradeType(GradebookModel selectedGradebook, GradeType gradeType, boolean isChecked) {
		if (! isChecked)
			return;
		
		GradeType actionValue = gradeType;
		GradeType actionStartValue = selectedGradebook.getGradeType();;

		doUpdate(selectedGradebook, GradebookModel.Key.GRADETYPE, ClassType.GRADETYPE, actionValue, actionStartValue);
	}
	
	private void selectReleaseGrades(GradebookModel selectedGradebook, boolean isYes, boolean isChecked) {
		if (! isChecked)
			return;

		Boolean actionValue = null;
		Boolean actionStartValue = null;
		
		if (isYes) {
			actionValue = Boolean.TRUE;
			actionStartValue = Boolean.FALSE;
		} else {
			actionValue = Boolean.FALSE;
			actionStartValue = Boolean.TRUE;
		}
		
		doUpdate(selectedGradebook, GradebookModel.Key.RELEASEGRADES, ClassType.BOOLEAN, actionValue, actionStartValue);

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
						String gradebookUid = result.getGradebookUid();
						Registry.unregister(gradebookUid);
						Registry.register(gradebookUid, result);
						action.announce(result.getName(), p.name(), action.getValue());

						// Make sure we replace the current gradebook as well, since it is the one modified
						Registry.unregister(AppConstants.CURRENT);
						Registry.register(AppConstants.CURRENT, result);
						
						action.setModel(result);
						Dispatcher.forwardEvent(GradebookEvents.UserChange, action);
					}
				
			};
			
			
			remoteCommand.execute(action);
		}
	}
}
