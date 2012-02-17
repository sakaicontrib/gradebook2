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
import org.sakaiproject.gradebook.gwt.client.gxt.type.ExportType;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.NullSensitiveCheckBox;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.SectionsComboBox;
import org.sakaiproject.gradebook.gwt.client.model.key.SectionKey;
import org.sakaiproject.gradebook.gwt.client.resource.GradebookResources;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;

public class ExportFormPanel extends FormPanel {

	public final static Integer COMMENTS_CHECKBOX_VALUE = Integer.valueOf(0);
	public final static Integer EXPORT_TYPE_VALUE = Integer.valueOf(1);
	public final static Integer SECTIONS_VAlUE = Integer.valueOf(2);
	
	private SectionsComboBox<ModelData> sectionsComboBox;
	private ComboBox<ModelData> exportTypeComboBox;
	private CheckBox commentsCheckbox;
	private ToolTipConfig toolTipConfig;
	private GradebookResources resources;
	private I18nConstants i18n;
	
	public ExportFormPanel() {
		super();
		
		this.i18n = Registry.get(AppConstants.I18N);
		this.resources = Registry.get(AppConstants.RESOURCES);
		
		setLabelWidth(200);
		setFieldWidth(150);
		
		sectionsComboBox = new SectionsComboBox<ModelData>();
		AdapterField adapterField = new AdapterField(sectionsComboBox);
		adapterField.setFieldLabel(i18n.exportFormPanelLabelSections());
		add(adapterField);
		
		toolTipConfig = new ToolTipConfig(i18n.exportIncludeComments());
		toolTipConfig.setDismissDelay(10000);
		commentsCheckbox = new NullSensitiveCheckBox();
		commentsCheckbox.setFieldLabel(i18n.exportIncludeComments());
		commentsCheckbox.setAutoHeight(false);
		commentsCheckbox.setAutoWidth(false);
		commentsCheckbox.setVisible(true);
		commentsCheckbox.setToolTip(toolTipConfig);
		commentsCheckbox.setReadOnly(false);
		commentsCheckbox.setValue(Boolean.TRUE);
		commentsCheckbox.addStyleName(resources.css().gbCheckBoxAlignLeft());
		add(commentsCheckbox);
		
		exportTypeComboBox = new ImportExportTypeComboBox();

		add(exportTypeComboBox);
		
	}
	
	public Map<Integer, Object> getValues() {
		
		Map<Integer, Object> values = new HashMap<Integer, Object>();
		
		values.put(COMMENTS_CHECKBOX_VALUE, commentsCheckbox.getValue());
		
		List<ModelData> exportTypeModelData = exportTypeComboBox.getSelection();
		
		if(null != exportTypeModelData && exportTypeModelData.size() > 0) {
			
			values.put(EXPORT_TYPE_VALUE, exportTypeModelData.get(0).get(ExportType.DISPLAY_VALUE));
		}
		else {
			
			// TODO: Verify that CSV is an appropriate default if the user hasn't selected an export type
			values.put(EXPORT_TYPE_VALUE, ExportType.CSV);
		}
		
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
