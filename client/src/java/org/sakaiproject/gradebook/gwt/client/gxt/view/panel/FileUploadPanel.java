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
import org.sakaiproject.gradebook.gwt.client.gxt.type.ExportType;
import org.sakaiproject.gradebook.gwt.client.gxt.type.FileFormat;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.NullSensitiveCheckBox;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.resource.GradebookResources;
import org.sakaiproject.gradebook.gwt.client.wizard.formpanel.ExportTypeComboBox;
import org.sakaiproject.gradebook.gwt.client.wizard.formpanel.FileFormatComboBox;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;

public class FileUploadPanel extends FormPanel {
	public final static Integer COMMENTS_CHECKBOX_VALUE = Integer.valueOf(0);
	public final static Integer EXPORT_TYPE_VALUE = Integer.valueOf(1);

	private final I18nConstants i18n;
	private final ImportPanel newImportPanel;

	private FileUploadField file = null;
	private FileFormatComboBox importFormatComboBox;
	private Html importFormatInformationMessage;
	private FieldSet importFormatSet;
	private ExportTypeComboBox importTypeComboBox;

	NullSensitiveCheckBox justStructureChoice;

	public FileUploadPanel(final ImportPanel newImportPanel) {

		super();

		GradebookResources resources = Registry.get(AppConstants.RESOURCES);

		this.newImportPanel = newImportPanel;
		i18n = Registry.get(AppConstants.I18N);
		Registry.get(AppConstants.I18N_TEMPLATES);

		final Gradebook gbModel = Registry.get(AppConstants.CURRENT);

		setLabelWidth(175);

		setHeaderVisible(false);

		String action = new StringBuilder().append(GWT.getHostPageBaseURL()).append(AppConstants.IMPORT_SERVLET).toString();

		setFrame(true);
		setAction(action);
		setEncoding(Encoding.MULTIPART);
		setMethod(Method.POST);
		setPadding(4);
		setButtonAlign(HorizontalAlignment.RIGHT);

		//---------------------------------------------------------------------------

		file = new FileUploadField() {
			@Override
			protected void onChange(ComponentEvent ce) {

				super.onChange(ce);
				ExportType type = ExportType.getExportTypeFromFilename(getValue());
				importTypeComboBox.setSelectionByExportType(type);

			}

		};
		file.setValidator(new Validator() {

			@Override
			public String validate(Field<?> field, String value) {
				if (value != null) {
					int dot = value.lastIndexOf(".");
					if (dot > 0 &&
							ExportType.getExportTypeFromFilename(value.substring(dot)) != null ) {
						return null;
					}
				}
				StringBuffer sb = new StringBuffer(i18n.importFileTypesWarning());

				for (ExportType type : ExportType.values()) {
					sb.append(type.getFileExtension()).append(",");
				}
				sb.deleteCharAt(sb.length()-1); // TODO: multibyte charset aware?
				return sb.toString();
			}
		});
		file.setAllowBlank(false);
		file.setFieldLabel(i18n.fileLabel());
		file.setName("Test");

		//---security-values------------------------------------------------------------

		HiddenField<String> gradebookUidField = new HiddenField<String>();
		gradebookUidField.setName(AppConstants.REQUEST_FORM_FIELD_GBUID);
		gradebookUidField.setValue(gbModel.getGradebookUid());
		add(gradebookUidField);

		HiddenField<String> formTokenField = new HiddenField<String>();
		formTokenField.setName(AppConstants.REQUEST_FORM_FIELD_FORM_TOKEN);
		formTokenField.setValue(Cookies.getCookie(AppConstants.GB2_TOKEN));
		add(formTokenField);

		//---invisible combo box set by validator for format combo----------------------

		importTypeComboBox = new ExportTypeComboBox();
		importTypeComboBox.setName(AppConstants.IMPORT_PARAM_FILETYPE);
		importTypeComboBox.setAllowBlank(false);
		importTypeComboBox.setVisible(false);

		//---format combo---------------------------------------------------------------

		importFormatInformationMessage = new Html();
		importFormatInformationMessage.setHtml(i18n.fileFormatImportMessageFull());
		importFormatInformationMessage.setStyleName(resources.css().importFormatInformationMessage());

		importFormatComboBox = new FileFormatComboBox();
		importFormatComboBox.setWidth("50%");
		importFormatComboBox.setName(AppConstants.IMPORT_PARAM_FILEFORMAT);
		importFormatComboBox.setEmptyText(i18n.importFormatFieldEmptyText());
		importFormatComboBox.setAllowBlank(false);
		importFormatComboBox.setValidator(new Validator() {

			@Override
			public String validate(Field<?> field, String value) {
				if (value != null) {
					FileFormat f = FileFormat.valueOf((String)
							importFormatComboBox.getValue().get(importFormatComboBox.getValueField()));
					if (f != null) {
						importFormatInformationMessage.setHtml(f.getImportMessage(i18n));

						if (!f.equals(FileFormat.FULL) ) {

							if(justStructureChoice.getValue()) {
								return i18n.justStructureNotAllowedMessage()
								+ "'" + i18n.justStructureCheckboxLabel() + "'";
							}

							justStructureChoice.setEnabled(false);

							return null;
						}

						justStructureChoice.setEnabled(true);
						return null;
					}
					return "This should not happen: format not valid";
				}
				return "This should not happen: format dropdown value == null";
			}
		});

		importFormatSet = new FieldSet();
		importFormatSet.setCollapsible(false);
		importFormatSet.setHeading(i18n.importFormatFieldLabel());
		importFormatSet.setCheckboxToggle(false);
		importFormatSet.setAutoHeight(true);
		importFormatSet.setScrollMode(Scroll.AUTO);
		importFormatSet.setVisible(true);
		importFormatSet.add(importFormatComboBox);
		importFormatSet.add(importFormatInformationMessage);

		//---just-structure check-box----------------------------------------------------

		// GRBK-514
		justStructureChoice = new NullSensitiveCheckBox() {

			@Override
			protected void onClick(ComponentEvent ce) {
				super.onClick(ce);
				// hack for setting it Dirty?
				importFormatComboBox.setSelection(importFormatComboBox.getSelection());
			}

		};
		justStructureChoice.setName(AppConstants.IMPORT_PARAM_STRUCTURE);
		justStructureChoice.setValueAttribute("c");// we just check for ! null server-side

		ToolTipConfig checkBoxToolTipConfig = new ToolTipConfig(i18n.justStructureCheckboxToolTip());
		checkBoxToolTipConfig.setDismissDelay(10000);
		justStructureChoice.setToolTip(checkBoxToolTipConfig);
		justStructureChoice.setFieldLabel(i18n.justStructureCheckboxLabel());
		justStructureChoice.setValue(false);
		justStructureChoice.setAutoHeight(false);
		justStructureChoice.setAutoWidth(false);
		justStructureChoice.addStyleName(resources.css().gbLeftAlignFlushNoWrapInput());

		addListener(Events.Submit, new Listener<FormEvent>() {

			public void handleEvent(FormEvent fe) {
				newImportPanel.readSubmitResponse(fe.getResultHtml());
			}

		});

		//---------------------------------------------------------------------------

		add(importFormatSet);
		add(file);
		add(justStructureChoice);
		add(importTypeComboBox);
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
