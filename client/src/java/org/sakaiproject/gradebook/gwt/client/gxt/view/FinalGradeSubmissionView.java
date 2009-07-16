package org.sakaiproject.gradebook.gwt.client.gxt.view;

import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.GradeSubmissionDialog;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;

public class FinalGradeSubmissionView extends View {

	private GradeSubmissionDialog dialog;
	
	public FinalGradeSubmissionView(Controller controller, I18nConstants i18n) {
		super(controller);
		this.dialog = new GradeSubmissionDialog(i18n);
	}

	@Override
	protected void initialize() {
		super.initialize();
	}
	
	@Override
	protected void handleEvent(AppEvent<?> event) {

		dialog.verify();
		
		/*final GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
		
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
		}); */
	}
}
