package org.sakaiproject.gradebook.gwt.client.gxt.view;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.ExportDetails;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.ImportPanel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;

public class ImportExportView extends View {

	private ImportPanel importDialog;
	private Frame downloadFileFrame;
	private I18nConstants i18n;
	
	public ImportExportView(Controller controller, I18nConstants i18n) {
		super(controller);
		this.i18n = i18n;
	}

	@Override
	protected void handleEvent(AppEvent event) {
		
		switch (GradebookEvents.getEvent(event.getType()).getEventKey()) {
		case START_IMPORT:
			importDialog = new ImportPanel(i18n);
			break;
		case START_EXPORT:
			ExportDetails ed = (ExportDetails) event.getData(); 
			boolean includeStructure = ed.isIncludeStructure(); 
			String fileType = "";
			
			switch (ed.getFileType())
			{
			case XLS97:
				fileType = "xls97";
				break; 
			case CSV:
			default:
				fileType = "csv"; 
				break;

				
			}
		
			GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
			String uri = GWT.getModuleBaseURL() + "exportGradebook.csv?gradebookUid=" + selectedGradebook.getGradebookUid();
			
			if (includeStructure)
				uri += "&include=true";
			if (fileType != "")
			{
				uri += "&filetype=";
				uri += fileType;
			}
			if (downloadFileFrame == null) {
				downloadFileFrame = new Frame(uri);
				downloadFileFrame.setVisible(false);
				RootPanel.get().add(downloadFileFrame);
			} else {
				downloadFileFrame.setUrl(uri);
			}
			break;
		}
	}

	public ImportPanel getImportDialog() {
		return importDialog;
	}

}
