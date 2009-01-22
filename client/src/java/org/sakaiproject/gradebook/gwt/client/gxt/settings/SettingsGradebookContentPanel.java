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

import org.sakaiproject.gradebook.gwt.client.action.RemoteCommand;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction.ClassType;
import org.sakaiproject.gradebook.gwt.client.gxt.InlineEditField;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.UserChangeEvent;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.Key;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.ui.KeyboardListener;

public class SettingsGradebookContentPanel extends ContentPanel {

	public SettingsGradebookContentPanel(final String gradebookUid) {
		Listener<FieldEvent> listener =  new Listener<FieldEvent>() {

			public void handleEvent(FieldEvent fe) {
				
				GradebookModel gbModel = Registry.get(gradebookUid);
				Field f = fe.field;
				Object actionValue = null;
				Object actionStartValue = null;
				Key property = null;

				ClassType classType = null;
				if (f.getFieldLabel().equals("Name")) {
					classType = ClassType.STRING;
					property = Key.NAME;
					actionValue = fe.value;
					
					actionStartValue = gbModel.getName();
				} else if (f.getName().equals("organizationtype")) {
					classType = ClassType.CATEGORYTYPE;
					property = Key.CATEGORYTYPE;
					String value = ((SimpleComboValue<String>)fe.value).getValue();
					actionStartValue = gbModel.getCategoryType();
					
					if (value.equals("No Categories"))
						actionValue = CategoryType.NO_CATEGORIES;
					else if (value.equals("Categories"))
						actionValue = CategoryType.SIMPLE_CATEGORIES;
					else if (value.equals("Weighted Categories"))
						actionValue = CategoryType.WEIGHTED_CATEGORIES;
						
				} else if (f instanceof Radio) {
					property = Key.GRADETYPE;
					Radio r = (Radio)f;
					if (r.getBoxLabel().equals("Points")) {
						classType = ClassType.GRADETYPE;
						Boolean value = (Boolean)fe.value;
						if (value.booleanValue())
							actionValue = GradeType.POINTS;
						else
							actionValue = GradeType.PERCENTAGES;
						
						actionStartValue = gbModel.getGradeType();
						
					} else if (r.getBoxLabel().equals("Percentages")) {
						classType = ClassType.GRADETYPE;
						Boolean value = (Boolean)fe.value;
						if (value.booleanValue())
							actionValue = GradeType.PERCENTAGES;
						else
							actionValue = GradeType.POINTS;
						
						actionStartValue = gbModel.getGradeType();
					} else if (r.getBoxLabel().equals("Yes")) {
						classType = ClassType.BOOLEAN;
						property = Key.RELEASEGRADES;
						Boolean value = (Boolean)fe.value;
						if (value == null || value.booleanValue())
							actionValue = Boolean.TRUE;
						else
							actionValue = Boolean.FALSE;
						
						actionStartValue = gbModel.isReleaseGrades();
					} else if (r.getBoxLabel().equals("No")) {
						classType = ClassType.BOOLEAN;
						property = Key.RELEASEGRADES;
						Boolean value = (Boolean)fe.value;
						if (value == null || value.booleanValue())
							actionValue = Boolean.FALSE;
						else
							actionValue = Boolean.TRUE;
						
						actionStartValue = gbModel.isReleaseGrades();
					}
				}
				
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
	
								SettingsGradebookContentPanel.this.fireEvent(GradebookEvents.UserChange, new UserChangeEvent(action));
							}
						
					};
					
					
					remoteCommand.execute(action);
				}
			}
	    	
	    };
	    
		setHeaderVisible(false);
		setLayout(new FitLayout());
		setScrollMode(Scroll.AUTO);
		
		LayoutContainer container = new LayoutContainer();

		FormLayout layout = new FormLayout();
	    layout.setLabelAlign(LabelAlign.TOP);
	    layout.setLabelWidth(250);
	    layout.setPadding(10);
	    container.setLayout(layout);
	    //container.setScrollMode(Scroll.AUTO);
		
	    GradebookModel gbModel = Registry.get(gradebookUid);
	    
	    final InlineEditField<String> name = new InlineEditField<String>();
	    name.setFieldLabel("Name");
	    name.setValue(gbModel.getName());
	    name.setStyleAttribute("margin", "7 0 18 5");
	    container.add(name);
	    
	    name.addKeyListener(new KeyListener() {
	    	public void componentKeyPress(ComponentEvent event) {
			    switch (event.getKeyCode()) {
			    case KeyboardListener.KEY_ENTER:
			    	name.complete();
			    	break;
			    }
			}
	    });
	    name.addListener(Events.Change, listener);
	    
	    Radio pointsRadio = new Radio();  
	    pointsRadio.setBoxLabel("Points");
	    pointsRadio.setValue(Boolean.TRUE);
	    
	    Radio percentageRadio = new Radio();  
	    percentageRadio.setBoxLabel("Percentages");
	    percentageRadio.setValue(Boolean.FALSE);
	    
	    RadioGroup group = new RadioGroup();  
	    group.setFieldLabel("How will you enter your grades?");  
	    group.add(pointsRadio);  
	    group.add(percentageRadio);
	    group.setStyleAttribute("margin", "7 0 18 5");
	    switch (gbModel.getGradeType()) {
	    case POINTS:
	    	group.setValue(pointsRadio);
	    	break;
	    case PERCENTAGES:
	    	group.setValue(percentageRadio);
	    	break;
	    }
	    
	    pointsRadio.addListener(Events.Change, listener);
	    percentageRadio.addListener(Events.Change, listener);
	    container.add(group); 
	    
	    
	    SimpleComboBox<String> typePicker = new SimpleComboBox<String>(); 
		typePicker.setAllQuery(null);
		typePicker.setEditable(false);
		typePicker.setFieldLabel("Organization Type");
		typePicker.setForceSelection(true);
		typePicker.setName("organizationtype");
		typePicker.add("No Categories");
		typePicker.add("Categories");
		typePicker.add("Weighted Categories");
	    
		typePicker.setStyleAttribute("margin", "7 0 18 5");
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
	    //typePicker.setSimpleValue("Weighted Categories");
	    //typePicker.setEnabled(false);
	    container.add(typePicker);
	    
	    typePicker.addListener(Events.Change, listener);
	    
	    Radio yesRadio = new Radio();  
	    yesRadio.setBoxLabel("Yes");
	    yesRadio.setValue(Boolean.TRUE);
	    
	    Radio noRadio = new Radio();  
	    noRadio.setBoxLabel("No");  
	    noRadio.setValue(Boolean.FALSE);
	    
	    RadioGroup displayGroup = new RadioGroup();  
	    displayGroup.setFieldLabel("Display course grade to students?");  
	    displayGroup.add(yesRadio);
	    displayGroup.add(noRadio);
	    displayGroup.setStyleAttribute("margin", "7 0 18 5");
	    if (gbModel.isReleaseGrades().booleanValue())
	    	displayGroup.setValue(yesRadio);
	    else
	    	displayGroup.setValue(noRadio);
	    
	    yesRadio.addListener(Events.Change, listener);
	    noRadio.addListener(Events.Change, listener);
	    container.add(displayGroup);
	    
	    displayGroup.addListener(Events.Change, listener);
	    
		add(container);
	}
	
	
	
}
