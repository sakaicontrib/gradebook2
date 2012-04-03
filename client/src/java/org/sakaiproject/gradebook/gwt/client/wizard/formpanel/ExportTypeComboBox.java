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
import org.sakaiproject.gradebook.gwt.client.gxt.type.ExportType;
import org.sakaiproject.gradebook.gwt.client.gxt.type.FileModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;

public class ExportTypeComboBox extends ComboBox<ModelData> {
	
	private static I18nConstants i18n = Registry.get(AppConstants.I18N);

	public ExportTypeComboBox() {
		
		ListStore<ModelData> exportTypeStore = new ListStore<ModelData>();
		exportTypeStore.setModelComparer(new EntityModelComparer<ModelData>(ExportType.DISPLAY_NAME));
		
		for (ExportType type : ExportType.values()){
			ModelData model = new BaseModel();
			model.set(FileModel.DISPLAY_NAME, type.getDisplayName(i18n));
			model.set(FileModel.DISPLAY_VALUE, type);
			model.set(FileModel.DISPLAY_VALUE_STRING, type.name());
			exportTypeStore.add(model);
		}

		setStore(exportTypeStore);
		setDisplayField(ExportType.DISPLAY_NAME);
		setValueField(ExportType.DISPLAY_VALUE_STRING);
		setFieldLabel(i18n.exportFormPanelLabelExportType());
		setEmptyText(i18n.exportFormPanelExportTypeEmptyText());
		setTypeAhead(true);
		setEditable(false);
		setTriggerAction(TriggerAction.ALL);
		
	}
	
	public void setSelectionByExportType (ExportType type) {
		
		if( null != type ) {
			
			for (ModelData item : store.getModels()) {
				
				if (type.getDisplayName(null).equals(item.get(ExportType.DISPLAY_NAME))) {
					
					setValue(item);
					return;
				}
			}
		}
		
		setValue(null);
	}
}
