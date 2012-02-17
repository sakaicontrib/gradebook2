/**********************************************************************************
*
* $Id:$
*
***********************************************************************************
*
* Copyright (c) 2008, 2009, 2010 The Regents of the University of California
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

package org.sakaiproject.gradebook.gwt.client.gxt.view.panel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
//import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.gxt.type.ExportType;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.NullSensitiveCheckBox;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.resource.GradebookResources;
import org.sakaiproject.gradebook.gwt.client.wizard.formpanel.ImportExportTypeComboBox;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
//import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
//import com.extjs.gxt.ui.client.event.SelectionListener;
//import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;

public class FileUploadPanel extends FormPanel {

	private FileUploadField file = null;
	private final I18nConstants i18n;
	private final ImportPanel newImportPanel;
	private ImportExportTypeComboBox importTypeComboBox;
	NullSensitiveCheckBox justStructureChoice;
	
	public final static Integer COMMENTS_CHECKBOX_VALUE = Integer.valueOf(0);
	public final static Integer EXPORT_TYPE_VALUE = Integer.valueOf(1);
	
	
	public FileUploadPanel(final ImportPanel newImportPanel) {
		
		super();
		
		GradebookResources resources = Registry.get(AppConstants.RESOURCES);
	
		this.newImportPanel = newImportPanel;
		i18n = Registry.get(AppConstants.I18N);

		final Gradebook gbModel = Registry.get(AppConstants.CURRENT);

		setLabelWidth(200);

		setHeaderVisible(false);

		String action = new StringBuilder().append(GWT.getHostPageBaseURL()).append(AppConstants.IMPORT_SERVLET).toString();

		setFrame(true);
		setAction(action);
		setEncoding(Encoding.MULTIPART);
		setMethod(Method.POST);
		setPadding(4);
		setButtonAlign(HorizontalAlignment.RIGHT);

		file = new FileUploadField() {
			@Override
			protected void onChange(ComponentEvent ce) {
				
				super.onChange(ce);
				ExportType type = ExportType.getExportTypeFromFilename(getValue());
				importTypeComboBox.setSelectionByExportType(type);
				
			}
		};
		file.setAllowBlank(false);
		file.setFieldLabel(i18n.fileLabel());
		file.setName("Test");

		add(file);

		HiddenField<String> gradebookUidField = new HiddenField<String>();
		gradebookUidField.setName(AppConstants.REQUEST_FORM_FIELD_GBUID);
		gradebookUidField.setValue(gbModel.getGradebookUid());
		add(gradebookUidField);
		
		HiddenField<String> formTokenField = new HiddenField<String>();
		formTokenField.setName(AppConstants.REQUEST_FORM_FIELD_FORM_TOKEN);
		formTokenField.setValue(Cookies.getCookie(AppConstants.GB2_TOKEN));
		add(formTokenField);
		
		ListStore<ModelData> exportTypeStore = new ListStore<ModelData>();
		exportTypeStore.setModelComparer(new EntityModelComparer<ModelData>(ExportType.DISPLAY_NAME));
		for (ExportType type : ExportType.values()){
			exportTypeStore.add(ExportType.getExportTypeModel(type));
		}
		importTypeComboBox = new ImportExportTypeComboBox();
		importTypeComboBox.setFieldLabel(i18n.importFormPanelLabelExportType());
		importTypeComboBox.setEmptyText(i18n.importFormPanelImportTypeEmptyText());
		importTypeComboBox.setAllowBlank(false);

		add(importTypeComboBox);
		
		justStructureChoice = new NullSensitiveCheckBox();

		ToolTipConfig checkBoxToolTipConfig = new ToolTipConfig("Importo solamente lo stucture-o");
		checkBoxToolTipConfig.setDismissDelay(10000);
		justStructureChoice.setToolTip(checkBoxToolTipConfig);
		justStructureChoice.setFieldLabel("Importo solamente lo stucture-o");
		justStructureChoice.setValue(false);
		justStructureChoice.setAutoHeight(false);
		justStructureChoice.setAutoWidth(false);
		justStructureChoice.addStyleName(resources.css().gbLeftAlignFlushNoWrapInput());

		
		addListener(Events.Submit, new Listener<FormEvent>() {

			public void handleEvent(FormEvent fe) {
				newImportPanel.readSubmitResponse(fe.getResultHtml());
			}

		});
	}
	

	public void readFile() {
		// TODO Auto-generated method stub
		if (file.getValue() != null && file.getValue().trim().length() > 0) {
			newImportPanel.uploadBox = MessageBox.wait(i18n.importProgressTitle(), i18n.importReadingFileMessage(), i18n.importParsingMessage());
			submit();
		}
	}
	
	public Map<Integer, Object> getValues() {
		
		Map<Integer, Object> values = new HashMap<Integer, Object>();
		
		values.put(COMMENTS_CHECKBOX_VALUE, justStructureChoice.getValue());
		
		List<ModelData> exportTypeModelData = importTypeComboBox.getSelection();
		
		if(null != exportTypeModelData && exportTypeModelData.size() > 0) {
			
			values.put(EXPORT_TYPE_VALUE, exportTypeModelData.get(0).get(ExportType.DISPLAY_VALUE));
		}
		else {
			
			// TODO: We should make a choice mandatory (rather than using this default) after
			//       we've put the right filetype checks into place.
			values.put(EXPORT_TYPE_VALUE, ExportType.CSV);
		}

		
		return values;
	}
 

	
}
