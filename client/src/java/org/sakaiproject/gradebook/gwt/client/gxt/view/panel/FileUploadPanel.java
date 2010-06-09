package org.sakaiproject.gradebook.gwt.client.gxt.view.panel;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;

public class FileUploadPanel extends FormPanel {

	private FileUploadField file;
	private final I18nConstants i18n;
	private final NewImportPanel newImportPanel;
	
	public FileUploadPanel(final NewImportPanel newImportPanel) {
		
		super();
	
		this.newImportPanel = newImportPanel;
		i18n = Registry.get(AppConstants.I18N);

		final Gradebook gbModel = Registry.get(AppConstants.CURRENT);

		FormLayout formLayout = new FormLayout();
		formLayout.setDefaultWidth(350);
		formLayout.setLabelWidth(120);

		setHeaderVisible(false);

		String action = new StringBuilder().append(GWT.getHostPageBaseURL()).append(AppConstants.IMPORT_SERVLET).toString();

		setFrame(true);
		setAction(action);
		setEncoding(Encoding.MULTIPART);
		setMethod(Method.POST);
		setPadding(4);
		setButtonAlign(HorizontalAlignment.RIGHT);

		setLayout(formLayout);


		file = new FileUploadField() {
			@Override
			protected void onChange(ComponentEvent ce) {
				super.onChange(ce);
			}
		};
		file.setAllowBlank(false);
		file.setFieldLabel(i18n.fileLabel());
		file.setName("Test");

		add(file);

		HiddenField<String> gradebookUidField = new HiddenField<String>();
		gradebookUidField.setName("gradebookUid");
		gradebookUidField.setValue(gbModel.getGradebookUid());
		add(gradebookUidField);

		Button submitButton = new Button(i18n.nextButton());
		submitButton.setMinWidth(120);
		submitButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				readFile();
			}

		});
		addButton(submitButton);

		Button cancelButton = new Button(i18n.cancelButton());
		cancelButton.setMinWidth(120);
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				Dispatcher.forwardEvent(GradebookEvents.StopImport.getEventType());
				clear();
			}
		});

		addButton(cancelButton);

		addListener(Events.Submit, new Listener<FormEvent>() {

			public void handleEvent(FormEvent fe) {
				newImportPanel.readSubmitResponse(fe.getResultHtml());
			}

		});
	}
	
	private void readFile() {

		if (file.getValue() != null && file.getValue().trim().length() > 0) {
			newImportPanel.uploadBox = MessageBox.wait(i18n.importProgressTitle(), i18n.importReadingFileMessage(), i18n.importParsingMessage());
			submit();
		}
	}
	
	
}
