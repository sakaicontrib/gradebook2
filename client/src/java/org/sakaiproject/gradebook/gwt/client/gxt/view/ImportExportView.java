package org.sakaiproject.gradebook.gwt.client.gxt.view;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.ExportDetails;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.RestBuilder.Method;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.ImportPanel;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ImportExportView extends View {

	private ContentPanel importPanel;
	private FormPanel downloadFileForm;
	private final I18nConstants i18n = (I18nConstants) GWT.create(I18nConstants.class);
	
	public ImportExportView(Controller controller) {
		super(controller);
	}

	@Override
	protected void handleEvent(AppEvent event) {
		
		switch (GradebookEvents.getEvent(event.getType()).getEventKey()) {
		case START_IMPORT:
			
			break;
		case STOP_IMPORT:
			importPanel = null;
			break;
		case START_EXPORT:
			ExportDetails ed = (ExportDetails) event.getData(); 
			boolean includeStructure = ed.isIncludeStructure(); 
			String sectionUid = ed.getSectionUid();
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
		
			Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
			StringBuilder uri = new StringBuilder().append(GWT.getModuleBaseURL())
				.append(AppConstants.REST_FRAGMENT)
				.append("/").append(AppConstants.EXPORT_SERVLET)
				.append("/").append(selectedGradebook.getGradebookUid());
			
			if (includeStructure)
				uri.append("/").append("structure").append("/").append("true");
			if (fileType != "") {
				uri.append("/").append("filetype").append("/").append(fileType);
			}
			

			uri.append("?form-token=").append(Cookies.getCookie("JSESSIONID"));
			
			downloadFileForm = new FormPanel();
			
			downloadFileForm.setAction(uri.toString());
			
			downloadFileForm.setEncoding(FormPanel.ENCODING_URLENCODED);
			
			downloadFileForm.setMethod(FormPanel.METHOD_POST);
			
			VerticalPanel panel = new VerticalPanel();
			downloadFileForm.setWidget(panel);
			panel.setVisible(false);
			
			if (sectionUid != null) { 
				List<String> sectionsAsList = new ArrayList<String>();
				sectionsAsList.add(sectionUid);
				/*
				 *  this is being coded as if sectionUid were *not* a single value
				 *  so that it can be used with a list later. The above two could then
				 *  be removed
				 */
				
			
			
			
				StringBuffer iName = new StringBuffer();
				final String iNamePrefix = "section_";
				int i = 0;
				
				for (String section : sectionsAsList) {
					if (section != null) {
						TextBox s = new TextBox();
						s.setName(iName.append(iNamePrefix).append(i++).toString());
						s.setValue(section);
						panel.add(s);
					}
					
				}
			}
			
			downloadFileForm.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
				
				@Override
				public void onSubmitComplete(SubmitCompleteEvent event) {
					/// if this is called then something went wrong in the download
					Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), 
							new NotificationEvent(i18n.errorOccurredGeneric(), i18n.exportError()), true);

					
				}
			});
			      
			      
			
			// Add a 'submit' button. TODO: necessary?
			//Button sub = new Button("Submit");
			//sub.setEnabled(true);
			
			
		    //panel.add(sub);
		    
		    
			
			RootPanel.get().add(downloadFileForm);
			
			downloadFileForm.submit();
		
			break;
		}
	}

	private String getSectionsIndexed(String sectionUid, List<String> allSections) {
		StringBuffer sb = new StringBuffer();
		String[] parts = sectionUid.split(",");
		if(parts.length>0) {
			for(int i=0;i<parts.length;++i) {
				if(allSections.contains(parts[i])) {
					if(sb.length()>0) {
						sb.append(",");
					}
					sb.append(allSections.indexOf(parts[i]));
				}
			}
		}
		return sb.toString();
	}

	public ContentPanel getImportDialog() {
		if (importPanel == null) {
			importPanel = new ImportPanel();
		}
		return importPanel;
	}

}
