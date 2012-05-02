/**********************************************************************************
 *
 * Copyright (c) 2012 The Regents of the University of California
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

package org.sakaiproject.gradebook.gwt.client.wizard.formpanel;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityModelComparer;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;

/*
 * GRBK-1290
 */

public class ExportStructureTypeComboBox extends ComboBox<ModelData> {

	public static final String DISPLAY_NAME = "name";
	public static final String DISPLAY_VALUE = "value";
	public static final String DISPLAY_VALUE_STRING = "stringValue";
	
	public enum ExportStructureType { GRADES_ONLY, GRADES_AND_STRUCTURE; 
		
		public Boolean hasStructure() {
		
			if(this == GRADES_ONLY) {
				return Boolean.FALSE;
			}
			else {
				return Boolean.TRUE;
			}
		}
	}
	
	private static I18nConstants i18n = Registry.get(AppConstants.I18N);

	public ExportStructureTypeComboBox() {
		
		ListStore<ModelData> exportStructureTypeStore = new ListStore<ModelData>();
		exportStructureTypeStore.setModelComparer(new EntityModelComparer<ModelData>(DISPLAY_NAME));
		
		for(ExportStructureType type : ExportStructureType.values()) {
			
			ModelData model = new BaseModel();
			model.set(DISPLAY_NAME, getDisplayName(type));
			model.set(DISPLAY_VALUE, type);
			model.set(DISPLAY_VALUE_STRING, type.name());
			exportStructureTypeStore.add(model);
		}
		
		setStore(exportStructureTypeStore);
		setDisplayField(DISPLAY_NAME);
		setValueField(DISPLAY_VALUE_STRING);
		setFieldLabel(i18n.exportStructure());
		setEmptyText(i18n.exportStructureEmptyText());
		setTypeAhead(true);
		setEditable(false);
		setTriggerAction(TriggerAction.ALL);
	}
	
	private String getDisplayName(ExportStructureType type) {
		
		String displayName = "";
		
		switch(type) {
		
		case GRADES_ONLY:
			displayName = i18n.exportStructureOnlyGrades();
			break;
		case GRADES_AND_STRUCTURE:
			displayName = i18n.exportStrcutureAndGrades();
			break;
		}
		
		return displayName;
	}
 }
