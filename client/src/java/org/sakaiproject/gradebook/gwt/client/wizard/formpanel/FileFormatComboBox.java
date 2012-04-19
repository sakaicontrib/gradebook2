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

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.gxt.type.ExportType;
import org.sakaiproject.gradebook.gwt.client.gxt.type.FileFormat;
import org.sakaiproject.gradebook.gwt.client.gxt.type.FileModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;

public class FileFormatComboBox extends ComboBox<ModelData> {
	
	private static I18nConstants i18n = Registry.get(AppConstants.I18N);

	public FileFormatComboBox() {
		
		ListStore<ModelData> importFormatStore = new ListStore<ModelData>();
		importFormatStore.setModelComparer(new EntityModelComparer<ModelData>(FileFormat.DISPLAY_NAME));
		
		for (FileFormat format : FileFormat.values()) {
			ModelData model = new BaseModel();
			model.set(FileModel.DISPLAY_NAME, format.getDisplayName(i18n));
			model.set(FileModel.DISPLAY_VALUE, format);
			model.set(FileModel.DISPLAY_VALUE_STRING, format.name());
			importFormatStore.add(model);
		}

		setStore(importFormatStore);
		setDisplayField(ExportType.DISPLAY_NAME);
		setValueField(ExportType.DISPLAY_VALUE_STRING);
		setFieldLabel(i18n.importFormatFieldLabel());
		setEmptyText(i18n.importFormatFieldEmptyText());
		setTypeAhead(true);
		setEditable(false);
		setTriggerAction(TriggerAction.ALL);
		
		List<ModelData> initialSelection = new ArrayList<ModelData>();
		ModelData model = new BaseModel();
		model.set(FileModel.DISPLAY_NAME, FileFormat.FULL.getDisplayName(i18n));
		model.set(FileModel.DISPLAY_VALUE, FileFormat.FULL);
		model.set(FileModel.DISPLAY_VALUE_STRING, FileFormat.FULL.name());
		initialSelection.add(model);
		setSelection(initialSelection);
		
	}
	

}
