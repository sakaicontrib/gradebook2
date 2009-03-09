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

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.action.RemoteCommand;
import org.sakaiproject.gradebook.gwt.client.action.UserCategoryCreateAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.gxt.Notifier;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.model.CategoryModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.Element;

public class AddCategoryDialog extends Dialog {

	private static final Notifier notifier = new Notifier();

	private String directionsText;
	private LabelField directions;
	private TextField<String> categoryName;
	private NumberField categoryWeight;
	private CheckBox equalWeight;
	private NumberField dropLowest;
	
	public AddCategoryDialog() {
		this.directionsText = "Please fill out the fields below. If you choose to equally weight assignments, then each assignment will contribute an equal percent to the category grade.";
		this.directions = new LabelField(directionsText);
		
		setButtons(Dialog.OK);
		setHeading("New Category");
		setHideOnButtonClick(true);
		setLayout(new FitLayout());
		setModal(true);
		setResizable(true);
		setWidth(400);
		
		
		categoryName = new TextField<String>();  
		categoryName.setEmptyText("Required");
		categoryName.setFieldLabel("Name"); 
		categoryName.setSelectOnFocus(true);
		categoryName.setAllowBlank(false);  
		
		categoryWeight = new NumberField();
		categoryWeight.setEmptyText("Optional");
		categoryWeight.setFieldLabel("Category Weight");
		
		equalWeight = new CheckBox();
		equalWeight.setFieldLabel("Equally weight assignments");
		
		dropLowest = new NumberField();
		dropLowest.setPropertyEditorType(Integer.class);
		
		
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setLayout(new RowLayout());
		
		LayoutContainer container = new LayoutContainer();
		FormLayout layout = new FormLayout();
	    layout.setDefaultWidth(190);
	    layout.setLabelWidth(125);
	    layout.setLabelAlign(LabelAlign.LEFT);
	    container.setLayout(layout);
		
	    panel.add(directions, new RowData(1, 1, new Margins(10, 0, 20, 10)));
	    container.add(categoryName);
	    container.add(categoryWeight); 
	    container.add(equalWeight);
	    
	    panel.add(container);
		
	    add(panel);
	    
	    
	    // Before we show the dialog box each time we want to ensure that the fields are cleared out
	    addListener(Events.BeforeShow, new Listener<WindowEvent>() {

			public void handleEvent(WindowEvent be) {
				GradebookModel gbModel = Registry.get(AppConstants.CURRENT);
				// Ensure that the category name is blank
				categoryName.setValue(null);
				// And that it is not showing the "Required" warning
				categoryName.clearInvalid();
				// Category weight should only be visible when we have weighted categories
				categoryWeight.setVisible(gbModel.getCategoryType() == CategoryType.WEIGHTED_CATEGORIES);
				categoryWeight.setValue(null);
				// The only time we don't want to let the user make equally weighting assignments is when
				// the assignment weighting is points based
				equalWeight.setVisible(gbModel.getCategoryType() == CategoryType.WEIGHTED_CATEGORIES);
				equalWeight.setValue(Boolean.TRUE);
			}
	    	
	 	});
	}
	
	
	@Override
	protected void onButtonPressed(Button button) {
		
		categoryName.validate();
		
		String categoryNameValue = categoryName.getValue();
		
		if (categoryNameValue == null || categoryNameValue.trim().equals(""))
			return;
		
		
		super.onButtonPressed(button);

		GradebookModel gbModel = Registry.get(AppConstants.CURRENT);
		UserCategoryCreateAction action = 
			new UserCategoryCreateAction(gbModel, 
					getCategoryName().getValue(), 
				(Double)getCategoryWeight().getValue(), getEqualWeight().getValue(), 
				(Integer)getDropLowest().getValue());
		
		RemoteCommand<CategoryModel> remoteCommand = 
			new RemoteCommand<CategoryModel>() {

				@Override
				public void onCommandSuccess(UserEntityAction<CategoryModel> action, CategoryModel result) {
					notifier.notify("Category Added", "Created new category as '{0}' ", result.getName());

					action.setModel(result);
					
					Dispatcher.forwardEvent(GradebookEvents.UserChange, action);
				}
		};
		
		remoteCommand.execute(action);
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		
		categoryName.focus();
	}
	
	public TextField<String> getCategoryName() {
		return categoryName;
	}


	public void setCategoryName(TextField<String> categoryName) {
		this.categoryName = categoryName;
	}


	public NumberField getCategoryWeight() {
		return categoryWeight;
	}


	public void setCategoryWeight(NumberField categoryWeight) {
		this.categoryWeight = categoryWeight;
	}


	public CheckBox getEqualWeight() {
		return equalWeight;
	}


	public void setEqualWeight(CheckBox equalWeight) {
		this.equalWeight = equalWeight;
	}


	public NumberField getDropLowest() {
		return dropLowest;
	}


	public void setDropLowest(NumberField dropLowest) {
		this.dropLowest = dropLowest;
	}

}
