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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.SectionsComboBox;
import org.sakaiproject.gradebook.gwt.client.model.key.SectionKey;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;

public class DownloadNewItemFormPanel extends FormPanel {

	public final static Integer SECTIONS_VAlUE = Integer.valueOf(0);
	
	private SectionsComboBox<ModelData> sectionsComboBox;
	private I18nConstants i18n;
	
	public DownloadNewItemFormPanel() {
		super();
		
		this.i18n = Registry.get(AppConstants.I18N);
		
		setLabelWidth(200);
		setFieldWidth(150);
		
		sectionsComboBox = new SectionsComboBox<ModelData>();
		AdapterField adapterField = new AdapterField(sectionsComboBox);
		adapterField.setFieldLabel(i18n.exportFormPanelLabelSections());
		add(adapterField);
	}
	
	public Map<Integer, Object> getValues() {
		
		Map<Integer, Object> values = new HashMap<Integer, Object>();
		
		List<ModelData> sectionsModelData = sectionsComboBox.getSelection();
		
		if(null != sectionsModelData && sectionsModelData.size() > 0) {
		
			String selectedSection = sectionsModelData.get(0).get(SectionKey.S_ID.name());
			if(AppConstants.ALL.equals(selectedSection)) {
				
				values.put(SECTIONS_VAlUE, null);
			}
			else {
				
				values.put(SECTIONS_VAlUE, selectedSection);
			}
		}
		else {
			
			values.put(SECTIONS_VAlUE, null);
		}
		
		return values;
	}
}
