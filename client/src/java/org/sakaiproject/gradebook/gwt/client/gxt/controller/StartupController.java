/**********************************************************************************
*
* Copyright (c) 2008, 2009, 2010, 2011 The Regents of the University of California
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

package org.sakaiproject.gradebook.gwt.client.gxt.controller;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationSetup;
import org.sakaiproject.gradebook.gwt.client.model.AuthModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

public class StartupController extends Controller {

	public StartupController() {
		registerEventTypes(GradebookEvents.Load.getEventType());
	}
	
	@Override
	public void handleEvent(AppEvent event) {
		switch (GradebookEvents.getEvent(event.getType()).getEventKey()) {
		case LOAD:
			onLoad(event);
			break;
		}
	}
	
	private void onLoad(AppEvent event) {
		final AuthModel authModel = (AuthModel)event.getData();

		final boolean isUserAbleToGrade = authModel.isUserAbleToGrade() == null ? false : authModel.isUserAbleToGrade().booleanValue();
		final boolean isUserAbleToViewOwnGrades = authModel.isUserAbleToViewOwnGrades() == null ? false : authModel.isUserAbleToViewOwnGrades().booleanValue();
		final boolean isUserAbleToEditItems = DataTypeConversionUtil.checkBoolean(authModel.isUserAbleToEditAssessments());
		final boolean isNewGradebook = DataTypeConversionUtil.checkBoolean(authModel.isNewGradebook());
		
		Registry.register(AppConstants.IS_NEW_GRADEBOOK, Boolean.valueOf(isNewGradebook));
		Registry.register(AppConstants.IS_ABLE_TO_GRADE, Boolean.valueOf(isUserAbleToGrade));
		Registry.register(AppConstants.IS_ABLE_TO_EDIT, Boolean.valueOf(isUserAbleToEditItems));
		
		final I18nConstants i18n = Registry.get(AppConstants.I18N);
		
		if (isUserAbleToGrade) {
			GWT.runAsync(new RunAsyncCallback() {
				public void onFailure(Throwable caught) {
					RootPanel.get("mainapp").add(new HTML(i18n.serverNetworkConnectionError()));
				}
	
				public void onSuccess() {
					Dispatcher dispatcher = Dispatcher.get();
					dispatcher.addController(new InstructorController(i18n, isUserAbleToEditItems, isNewGradebook));					
					dispatcher.addController(new ServiceController(i18n));
					
					doNextStep(i18n);
				}
			});
		} else if (isUserAbleToViewOwnGrades) {
			GWT.runAsync(new RunAsyncCallback() {
				public void onFailure(Throwable caught) {
					RootPanel.get("mainapp").add(new HTML(i18n.serverNetworkConnectionError()));					
				}
	
				public void onSuccess() {
					Dispatcher dispatcher = Dispatcher.get();
					dispatcher.addController(new StudentController());
					dispatcher.addController(new ServiceController(i18n));
					
					doNextStep(i18n);
				}
			});
		} else {
			RootPanel.get("mainapp").add(new HTML(i18n.userAuthorizationError()));
		}
	}
	
	private void doNextStep(I18nConstants i18n) {
		
		ApplicationSetup appModel = Registry.get(AppConstants.APP_MODEL);
		
		if (appModel != null) {
		
			Dispatcher.forwardEvent(GradebookEvents.Startup.getEventType(), appModel);
		}
		else {
			
			RootPanel.get("mainapp").add(new HTML(i18n.applicationStartupError()));
		}
	}

}
