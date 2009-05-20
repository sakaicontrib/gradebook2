package org.sakaiproject.gradebook.gwt.client.gxt.view;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.advisor.SampleClientExportAdvisor;
import org.sakaiproject.gradebook.gwt.client.advisor.UcdClientExportAdvisor;
import org.sakaiproject.gradebook.gwt.client.api.ClientExportAdvisor;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;


import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;


public class FinalGradeSubmissionView extends View {

	private I18nConstants i18n;
	
	public FinalGradeSubmissionView(Controller controller) {
		super(controller);
	}

	@Override
	protected void initialize() {
		super.initialize();
	
		i18n = Registry.get(AppConstants.I18N);
	}
	
	@Override
	protected void handleEvent(AppEvent<?> event) {
		
		final GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
		
		MessageBox.confirm(i18n.finalGradeSubmissionConfirmTitle(), i18n.finalGradeSubmissionConfirmText(), new Listener<WindowEvent>() {

			public void handleEvent(WindowEvent be) {
				
				Button button = be.buttonClicked;
				
				if(button.getItemId().equals(Dialog.YES)) {
					
					final MessageBox box = MessageBox.wait(i18n.finalGradeSubmissionTitle(), i18n.finalGradeSubmissionMessageText1a(),  i18n.finalGradeSubmissionMessageText1b()); 
					
					String uri = GWT.getModuleBaseURL() + "/final-grade-submission?gradebookUid=" + selectedGradebook.getGradebookUid();
					RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, uri);
					requestBuilder.setHeader("Content-Type", "text/html");
					try {
						requestBuilder.sendRequest("", new RequestCallback() {

							public void onError(Request request, Throwable exception) {

								box.close();
								Info.display(i18n.finalGradeSubmissionTitle(), i18n.finalGradeSubmissionMessageText2a());  
							}

							public void onResponseReceived(Request request, Response response) {

								box.close();
								
								if (201 == response.getStatusCode()) {
									
									String responseText = response.getText().trim();
									
									// FIXME : Find a GWT IOC solution, so that we can inject the desired implementation
									//ClientExportAdvisor clientExportAdvisor = new SampleClientExportAdvisor();
									ClientExportAdvisor clientExportAdvisor = new UcdClientExportAdvisor();
									
									clientExportAdvisor.handleServerResponse(responseText);
								}
								else if(500 == response.getStatusCode()) {
									
									Info.display(i18n.finalGradeSubmissionTitle(), i18n.finalGradeSubmissionMessageText5a());
								}
							}
							
						});
					} catch (RequestException e) {
						
						Info.display(i18n.finalGradeSubmissionTitle(), i18n.finalGradeSubmissionMessageText6a());
						e.printStackTrace();
					}
				}
			}	
		}); 
	}
}
