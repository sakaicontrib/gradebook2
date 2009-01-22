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

import java.util.Date;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.GradebookToolFacadeAsync;
import org.sakaiproject.gradebook.gwt.client.action.RemoteCommand;
import org.sakaiproject.gradebook.gwt.client.action.UserAssignmentCreateAction;
import org.sakaiproject.gradebook.gwt.client.action.UserCategoryCreateAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityGetAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.gxt.Notifier;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.UserChangeEvent;
import org.sakaiproject.gradebook.gwt.client.model.AssignmentModel;
import org.sakaiproject.gradebook.gwt.client.model.CategoryModel;
import org.sakaiproject.gradebook.gwt.client.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;

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

public class AddAssignmentDialog extends Dialog {

	private static final Notifier notifier = new Notifier();
	
	private String gradebookUid;
	private ContentPanel contentPanel;
	
	private String directionsText;
	private LabelField directions;
	private TextField<String> assignmentName;
	private NumberField assignmentWeight;
	private NumberField assignmentPoints;
	private DateField dueDate;
	private ComboBox<CategoryModel> categoryPicker;
	
	public AddAssignmentDialog(final String gradebookUid, ContentPanel contentPanel) {
		this.gradebookUid = gradebookUid;
		this.contentPanel = contentPanel;
		
		setModal(true);
		setResizable(true);
		
		directionsText = "Please fill out the fields below. You may either select an existing category or type in the name of a new one.";
		directions = new LabelField(directionsText);
		
		assignmentName = new TextField<String>();
		assignmentName.setAllowBlank(false); 
		assignmentName.setEmptyText("Required");
		assignmentName.setFieldLabel("Name");   
		
		assignmentPoints = new NumberField();
		assignmentPoints.setEmptyText("Optional - default is 100");
		assignmentPoints.setFieldLabel("Points");
		
		assignmentWeight = new NumberField();
		assignmentWeight.setEmptyText("Optional - default is same as points");
		assignmentWeight.setFieldLabel("Weight");
		assignmentWeight.setVisible(false);
		
		dueDate = new DateField();
		dueDate.setEmptyText("Optional");
		dueDate.setFieldLabel("Due Date");
		
		final GradebookToolFacadeAsync service = Registry.get("service");
		
		RpcProxy<ListLoadConfig, List<CategoryModel>> categoriesProxy = 
			new RpcProxy<ListLoadConfig, List<CategoryModel>>() {
			@Override
			protected void load(ListLoadConfig loadConfig, AsyncCallback<List<CategoryModel>> callback) {
				GradebookModel gbModel = Registry.get(gradebookUid);
				UserEntityGetAction<CategoryModel> action = new UserEntityGetAction<CategoryModel>(gbModel, EntityType.CATEGORY);
				//action.setShowAll(Boolean.FALSE);
				service.getEntityList(action, callback);
			}
		};
		
		final ListLoader<ListLoadConfig> categoriesLoader = new BaseListLoader(categoriesProxy);
		
		categoriesLoader.setRemoteSort(true);
		
		ListStore<CategoryModel> categoriesStore = new ListStore<CategoryModel>(categoriesLoader);
		categoriesStore.setModelComparer(new EntityModelComparer<CategoryModel>());
		
		categoryPicker = new ComboBox<CategoryModel>(); 
		categoryPicker.setAllowBlank(false); 
		categoryPicker.setAllQuery(null);
		categoryPicker.setDisplayField(CategoryModel.Key.NAME.name());  
		categoryPicker.setEditable(true);
		categoryPicker.setEmptyText("Required");
		categoryPicker.setFieldLabel("Category");
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
		setHeading("New Grade Item");
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

	    // Before we show the dialog box each time we want to ensure that the fields are cleared out
	    addListener(Events.BeforeShow, new Listener<WindowEvent>() {

			public void handleEvent(WindowEvent be) {
				GradebookModel gbModel = Registry.get(gradebookUid);
			    
			    switch (gbModel.getCategoryType()) {
			    case NO_CATEGORIES:
			    	categoryPicker.setVisible(false);
			    	break;
			    default:
			    	categoryPicker.setVisible(true);
			    	categoryPicker.setValue(null);
					categoryPicker.clearInvalid();
					categoriesLoader.load();
					break;
			    }
				
				assignmentName.setValue(null);
				assignmentName.clearInvalid();
				
				assignmentWeight.setValue(null);
				assignmentWeight.setVisible(false);
				assignmentPoints.setValue(null);
				dueDate.setValue(null);
				
			}
	    	
	    });
	    
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
		GradebookModel gbModel = Registry.get(gradebookUid);
	    
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
					addCategory(categoryName);
					super.onButtonPressed(button);
					return;
				}
			}
			
			if (categoryId == null) {
				notifier.notify("No category selected", "You must choose a category");
				return;
			}
			break;
	    }
		super.onButtonPressed(button);
		
		addAssignment(categoryId);
	}
	
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		
		if (categoryPicker.isVisible())
			categoryPicker.focus();
	}
	
	private void addAssignment(Long categoryId) {
		
		GradebookModel gbModel = Registry.get(gradebookUid);
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

					if (contentPanel != null)
						contentPanel.fireEvent(GradebookEvents.UserChange, new UserChangeEvent(action));
				}
			
		};
		
		remoteCommand.execute(action);
	}
	
	private void addCategory(String categoryName) {
		
		Double weight = null;
		Boolean isEqualWeight = null;
		Integer dropLowest = null;
		
		GradebookModel gbModel = Registry.get(gradebookUid);
		
		UserCategoryCreateAction action = 
			new UserCategoryCreateAction(gbModel, 
					categoryName, weight, isEqualWeight, dropLowest);
		
		RemoteCommand<CategoryModel> remoteCommand = 
			new RemoteCommand<CategoryModel>() {

				@Override
				public void onCommandSuccess(UserEntityAction<CategoryModel> action, CategoryModel result) {
					notifier.notify("Category Added", "Created new category as '{0}' ", result.getName());
					Long categoryId = result == null ? null : Long.valueOf(result.getIdentifier());
					addAssignment(categoryId);
					
					action.setModel(result);
					
					if (contentPanel != null) 
						contentPanel.fireEvent(GradebookEvents.UserChange, new UserChangeEvent(action));
					
				}
		};
		
		remoteCommand.execute(action);
	}


	public ContentPanel getSettingsPanel() {
		return contentPanel;
	}


	public void setSettingsPanel(ContentPanel contentParent) {
		this.contentPanel = contentParent;
	}
	
}
