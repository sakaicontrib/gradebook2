/**********************************************************************************
*
* $Id:$
*
***********************************************************************************
*
* Copyright (c) 2008, 2009 The Regents of the University of California
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

package org.sakaiproject.gradebook.gwt.client.gxt.view.components;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.Gradebook2RPCServiceAsync;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.SecureToken;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.advisor.SampleClientExportAdvisor;
import org.sakaiproject.gradebook.gwt.client.api.ClientExportAdvisor;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.SubmissionVerificationModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GradeSubmissionDialog extends Dialog {

	private I18nConstants i18n;
	
	public GradeSubmissionDialog(I18nConstants i18n) {
		this.i18n = i18n;
	
		setButtons(Dialog.YESNO);
		setHeading(i18n.finalGradeSubmissionConfirmTitle());
		setHideOnButtonClick(true);
	}
	
	public void verify() {
		this.removeAll();
		
		final MessageBox box = MessageBox.wait(i18n.finalGradeSubmissionVerificationTitle(), i18n.finalGradeSubmissionMessageText1c(),  i18n.finalGradeSubmissionMessageText1c());
		
		AsyncCallback<SubmissionVerificationModel> callback = 
			new AsyncCallback<SubmissionVerificationModel>() {

				public void onFailure(Throwable caught) {
					Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(caught, "Unable to submit final grades: "));
					box.close();
				}

				public void onSuccess(SubmissionVerificationModel result) {
					box.close();
					
					StringBuilder text = new StringBuilder();
					
					text.append(i18n.finalGradeSubmissionWarningPrefix1());
					text.append(result.getNumberOfLearners()).append(" ");
					text.append(i18n.finalGradeSubmissionWarningSuffix1());
					
					if (result.isMissingScores())
						text.append(i18n.finalGradeSubmissionWarningPrefix2());
								
					text.append(i18n.finalGradeSubmissionConfirmText());
					
					addText(text.toString());
					
					show();
				}
			
		};
		
		GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
		Gradebook2RPCServiceAsync service = Registry.get("service");
		service.get(selectedGradebook.getGradebookUid(), selectedGradebook.getGradebookId(), EntityType.SUBMISSION_VERIFICATION, null, null, SecureToken.get(), callback);

	}
	
	protected void onButtonPressed(Button button) {
		super.onButtonPressed(button);
		
		if(button.getItemId().equals(Dialog.YES)) {
			
			GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
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
							ClientExportAdvisor clientExportAdvisor = new SampleClientExportAdvisor();
							
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
}
