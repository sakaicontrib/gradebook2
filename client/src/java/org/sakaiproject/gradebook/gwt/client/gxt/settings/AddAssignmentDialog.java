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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.GradebookToolFacadeAsync;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.action.RemoteCommand;
import org.sakaiproject.gradebook.gwt.client.action.UserAssignmentCreateAction;
import org.sakaiproject.gradebook.gwt.client.action.UserCategoryCreateAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityGetAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.gxt.Notifier;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.model.AssignmentModel;
import org.sakaiproject.gradebook.gwt.client.model.CategoryModel;
import org.sakaiproject.gradebook.gwt.client.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadConfig;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Accessibility;

public class AddAssignmentDialog extends Dialog {

	private static final Notifier notifier = new Notifier();

	private LabelField directions;
	private TextField<String> assignmentName;
	private NumberField assignmentWeight;
	private NumberField assignmentPoints;
	private DateField dueDate;
	private ListStore<CategoryModel> categoriesStore;
	private ComboBox<CategoryModel> categoryPicker;
	private ListLoader<ListLoadConfig> categoriesLoader;
	
	private Listener<WindowEvent> windowListener;
	
	private ItemModel itemModel;
	
	public AddAssignmentDialog() {
		setModal(true);
		setResizable(true);	
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		
		I18nConstants i18n = Registry.get(AppConstants.I18N);
		
		directions = new LabelField(i18n.addItemDirections());
		
		assignmentName = new TextField<String>();
		assignmentName.setAllowBlank(false); 
		assignmentName.setEmptyText(i18n.requiredLabel());
		assignmentName.setFieldLabel(i18n.addItemName());   
		
		assignmentPoints = new NumberField();
		assignmentPoints.setEmptyText(i18n.addItemPointsEmpty());
		assignmentPoints.setFieldLabel(i18n.addItemPoints());
		
		assignmentWeight = new NumberField();
		assignmentWeight.setEmptyText(i18n.addItemWeightEmpty());
		assignmentWeight.setFieldLabel(i18n.addItemWeight());
		assignmentWeight.setVisible(false);
		
		dueDate = new DateField();
		dueDate.setEmptyText(i18n.addItemDueDateEmpty());
		dueDate.setFieldLabel(i18n.addItemDueDate());
		
		final GradebookToolFacadeAsync service = Registry.get("service");
		
		RpcProxy<ListLoadConfig, List<CategoryModel>> categoriesProxy = 
			new RpcProxy<ListLoadConfig, List<CategoryModel>>() {
			@Override
			protected void load(ListLoadConfig loadConfig, AsyncCallback<List<CategoryModel>> callback) {
				GradebookModel gbModel = Registry.get(AppConstants.CURRENT);
				UserEntityGetAction<CategoryModel> action = new UserEntityGetAction<CategoryModel>(gbModel, EntityType.CATEGORY);
				//action.setShowAll(Boolean.FALSE);
				service.getEntityList(action, callback);
			}
		};
		
		categoriesLoader = new BaseListLoader(categoriesProxy);
		
		categoriesLoader.setRemoteSort(true);
		
		categoriesStore = new ListStore<CategoryModel>(categoriesLoader);
		categoriesStore.setModelComparer(new EntityModelComparer<CategoryModel>());
		
		categoryPicker = new ComboBox<CategoryModel>(); 
		categoryPicker.setAllowBlank(false); 
		categoryPicker.setAllQuery(null);
		categoryPicker.setDisplayField(CategoryModel.Key.NAME.name());  
		categoryPicker.setEditable(true);
		categoryPicker.setEmptyText(i18n.requiredLabel());
		categoryPicker.setFieldLabel(i18n.categoryName());
		categoryPicker.setForceSelection(false);
		categoryPicker.setStore(categoriesStore);
		categoryPicker.addSelectionChangedListener(new SelectionChangedListener<CategoryModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<CategoryModel> se) {
				CategoryModel model = se.getSelectedItem();
			
				if (model != null) {
					boolean isEqualWeighting = model.getEqualWeightAssignments() == null ? false : model.getEqualWeightAssignments().booleanValue();
				
					int height = AddAssignmentDialog.this.getHeight();
					if (isEqualWeighting && assignmentWeight.isVisible()) 
						AddAssignmentDialog.this.setHeight(height - 30);
					else if (!isEqualWeighting && !assignmentWeight.isVisible())
						AddAssignmentDialog.this.setHeight(height + 30);
					
					assignmentWeight.setVisible(!isEqualWeighting);
				}
				
			}
			
		});
		
		setButtons(Dialog.OK);
		setHeading(i18n.addItemHeading());
		setHideOnButtonClick(true);
		setLayout(new FitLayout());
		setWidth(400);
		
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setLayout(new RowLayout());
		
		LayoutContainer container = new LayoutContainer();
		FormLayout layout = new FormLayout();
	    layout.setDefaultWidth(190);
	    layout.setLabelWidth(125);
	    layout.setLabelAlign(LabelAlign.LEFT);
	    container.setLayout(layout);
	    
	    panel.add(directions, new RowData(1, 1, new Margins(10, 0, 10, 10)));
	    container.add(categoryPicker);
	    container.add(assignmentName); 
	    container.add(assignmentPoints);
	    container.add(assignmentWeight);
	    container.add(dueDate);
	    panel.add(container);
		
	    add(panel);

	    Accessibility.setRole(categoryPicker.getElement(), "combobox");
	    initListeners();
	    
	    onCategorySelected();
	    // Before we show the dialog box each time we want to ensure that the fields are cleared out
	    addListener(Events.BeforeShow, windowListener);
	}
	
	
	@Override
	protected void onButtonPressed(Button button) {
		assignmentName.validate();
		if (categoryPicker.isVisible())
			categoryPicker.validate();
		
		String assignmentNameValue = assignmentName.getValue();
		
		if (assignmentNameValue == null || assignmentNameValue.trim().equals("")) {
			return;
		}
		
		Long categoryId = null;
		GradebookModel gbModel = Registry.get(AppConstants.CURRENT);
		I18nConstants i18n = Registry.get(AppConstants.I18N);
		
	    switch (gbModel.getCategoryType()) {
	    case SIMPLE_CATEGORIES:
	    case WEIGHTED_CATEGORIES:
			Object categoryValue = categoryPicker.getValue();
			
			if (categoryValue != null && categoryValue instanceof CategoryModel) {
				CategoryModel categoryModel = (CategoryModel)categoryValue;
				categoryId = categoryModel == null ? null : Long.valueOf(categoryModel.getIdentifier());
			} else {
				String categoryName = categoryPicker.getRawValue();
				if (!categoryName.trim().equals("")) {
					addCategory(categoryName, gbModel);
					super.onButtonPressed(button);
					return;
				}
			}
			
			if (categoryId == null) {
				notifier.notify(i18n.addItemNoCategoryHeading(), i18n.addItemNoCategoryMessage());
				return;
			}
			break;
	    }
		super.onButtonPressed(button);
		
		addAssignment(categoryId, gbModel);
	}
	
	
	/*@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		
		if (categoryPicker.isVisible())
			categoryPicker.focus();
	}*/
	
	private void addAssignment(Long categoryId, GradebookModel gbModel) {
		
		UserAssignmentCreateAction action = 
			new UserAssignmentCreateAction(gbModel, 
				categoryId, 
				assignmentName.getValue(), (Double)assignmentWeight.getValue(), 
				(Double)assignmentPoints.getValue(), (Date)dueDate.getValue());
		
		RemoteCommand<AssignmentModel> remoteCommand = 
			new RemoteCommand<AssignmentModel>() {

				@Override
				public void onCommandSuccess(UserEntityAction<AssignmentModel> action, AssignmentModel result) {
					notifier.notify("Grade Item Added", "Created new assignment as '{0}' ", result.getName());
					
					action.setModel(result);

					Dispatcher.forwardEvent(GradebookEvents.UserChange, action);
				}
			
		};
		
		remoteCommand.execute(action);
	}
	
	private void addCategory(String categoryName, final GradebookModel gbModel) {
		
		Double weight = null;
		Boolean isEqualWeight = null;
		Integer dropLowest = null;
		
		UserCategoryCreateAction action = 
			new UserCategoryCreateAction(gbModel, 
					categoryName, weight, isEqualWeight, dropLowest);
		
		RemoteCommand<CategoryModel> remoteCommand = 
			new RemoteCommand<CategoryModel>() {

				@Override
				public void onCommandSuccess(UserEntityAction<CategoryModel> action, CategoryModel result) {
					notifier.notify("Category Added", "Created new category as '{0}' ", result.getName());
					Long categoryId = result == null ? null : Long.valueOf(result.getIdentifier());
					addAssignment(categoryId, gbModel);
					
					action.setModel(result);
					
					Dispatcher.forwardEvent(GradebookEvents.UserChange, action);
					
				}
		};
		
		remoteCommand.execute(action);
	}
	
	private void initListeners() {
		windowListener = new Listener<WindowEvent>() {

			public void handleEvent(WindowEvent be) {
				GradebookModel gbModel = Registry.get(AppConstants.CURRENT);
			    
			    switch (gbModel.getCategoryType()) {
			    case NO_CATEGORIES:
			    	categoryPicker.setVisible(false);
			    	break;
			    default:
			    	categoryPicker.setVisible(true);
			    	categoryPicker.setValue(null);
					categoryPicker.clearInvalid();
					if (categoriesLoader != null)
						categoriesLoader.load();
					break;
			    }
			    
			    onCategorySelected();
				
				assignmentName.setValue(null);
				assignmentName.clearInvalid();
				
				assignmentWeight.setValue(null);
				assignmentWeight.setVisible(false);
				assignmentPoints.setValue(null);
				dueDate.setValue(null);
				
			}
	    	
	    };
	}
	
	private void onCategorySelected() {
		if (categoryPicker == null)
			return;
		
		ItemModel itemModel = getItemModel();
	    if (itemModel != null) {
	    	if (!itemModel.getItemType().equals(Type.CATEGORY.getName())) {
	    		itemModel = itemModel.getParent();
	    	}
	    	
	    	if (itemModel.getItemType().equals(Type.CATEGORY.getName())) {
		    	CategoryModel categoryModel = categoriesStore.findModel(CategoryModel.Key.ID.name(), getItemModel().getIdentifier());
		    	categoryPicker.setValue(categoryModel);
	    	}
	    }
	}

	public ItemModel getItemModel() {
		return itemModel;
	}

	public void setItemModel(ItemModel itemModel) {
		this.itemModel = itemModel;
	}
	
}
