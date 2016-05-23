package org.sakaiproject.gradebook.gwt.client.gxt.view;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.ExportDetails;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.ImportPanel;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ImportExportView extends View {

	private ImportPanel importPanel;
	private FormPanel downloadFileForm;
	private final I18nConstants i18n = (I18nConstants) GWT.create(I18nConstants.class);
	
	public ImportExportView(Controller controller) {
		super(controller);
	}

	@Override
	protected void handleEvent(AppEvent event) {
		
		switch (GradebookEvents.getEvent(event.getType()).getEventKey()) {
		case START_IMPORT:
			// this event arrives after the panel is instantiated
			importPanel.startImportWizard();
			break;
		case STOP_IMPORT:
			importPanel.finish();
			importPanel = null;
			break;
		case START_EXPORT:
			ExportDetails ed = (ExportDetails) event.getData(); 
			boolean includeStructure = ed.isIncludeStructure(); 
			String sectionUid = ed.getSectionUid();
			boolean includeComments = ed.includeComments();
			String fileType = "";
			
			if (ed.getFileType() != null) {
				fileType = ed.getFileType().getTypeName();
			}
		
			Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
			StringBuilder uri = new StringBuilder().append(GWT.getModuleBaseURL())
				.append(AppConstants.REST_FRAGMENT)
				.append("/").append(AppConstants.EXPORT_SERVLET)
				.append("/").append(selectedGradebook.getGradebookUid());
			
			uri.append("?").append(AppConstants.REQUEST_FORM_FIELD_FORM_TOKEN).append("=").append(Cookies.getCookie(AppConstants.GB2_TOKEN));
			
			downloadFileForm = new FormPanel();
			
			downloadFileForm.setAction(uri.toString());
			
			downloadFileForm.setEncoding(FormPanel.ENCODING_URLENCODED);
			
			downloadFileForm.setMethod(FormPanel.METHOD_POST);
			
			/*
			 * Constructing export details JSON, which we add as a hidden form field
			 */
			JSONObject jsonObject = new JSONObject();
			
			jsonObject.put(AppConstants.EXPORT_DATA_COMMENTS, JSONBoolean.getInstance(includeComments));
			
			jsonObject.put(AppConstants.EXPORT_DATA_STRUCTURE, JSONBoolean.getInstance(includeStructure));
		
			JSONArray sections = new JSONArray();
			if(null != sectionUid && !"".equals(sectionUid)) {
				
				sections.set(0, new JSONString(sectionUid));
			}
			jsonObject.put(AppConstants.EXPORT_DATA_SECTIONS, sections);
			
			jsonObject.put(AppConstants.EXPORT_DATA_TYPE, new JSONString(fileType));
			
			HiddenField<String> exportData = new HiddenField<String>();
			exportData.setName(AppConstants.EXPORT_DATA_FIELD);
			exportData.setValue(jsonObject.toString());
			
			
			VerticalPanel panel = new VerticalPanel();
			downloadFileForm.setWidget(panel);
			panel.setVisible(false);
			
			panel.add(exportData);
			
			downloadFileForm.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
				
				public void onSubmitComplete(SubmitCompleteEvent event) {
					/// if this is called then something went wrong in the download
					Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), 
							new NotificationEvent(i18n.errorOccurredGeneric(), i18n.exportError()), true);
				}
			});		    
			
			RootPanel.get("mainapp").add(downloadFileForm);
			
			downloadFileForm.submit();
		
			break;
		}
	}

	public ImportPanel getImportDialog() {
		if (importPanel == null) {
			importPanel = new ImportPanel();
		}
		return importPanel;
	}

}
