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

		Registry.register(AppConstants.IS_ABLE_TO_GRADE, Boolean.valueOf(isUserAbleToGrade));
		Registry.register(AppConstants.IS_ABLE_TO_EDIT, Boolean.valueOf(isUserAbleToEditItems));
		
		final I18nConstants i18n = Registry.get(AppConstants.I18N);
		
		if (isUserAbleToGrade) {
			GWT.runAsync(new RunAsyncCallback() {
				public void onFailure(Throwable caught) {
					RootPanel.get().add(new HTML("Server failed to respond with necessary data. Please ensure that your network connection is working and reload."));
				}
	
				public void onSuccess() {
					Dispatcher dispatcher = Dispatcher.get();
					dispatcher.addController(new InstructorController(i18n, isUserAbleToEditItems, isNewGradebook));					
					dispatcher.addController(new ServiceController(i18n));
					
					doNextStep();
				}
			});
		} else if (isUserAbleToViewOwnGrades) {
			GWT.runAsync(new RunAsyncCallback() {
				public void onFailure(Throwable caught) {
					RootPanel.get().add(new HTML("Server failed to respond with necessary data. Please ensure that your network connection is working and reload."));					
				}
	
				public void onSuccess() {
					Dispatcher dispatcher = Dispatcher.get();
					dispatcher.addController(new StudentController());
					dispatcher.addController(new ServiceController(i18n));
					
					doNextStep();
				}
			});
		} else {
			RootPanel.get().add(new HTML("This user is not authorized to view grade information."));
		}
	}
	
	private void doNextStep() {
		ApplicationSetup appModel = Registry.get(AppConstants.APP_MODEL);
		
		if (appModel == null) 
			Registry.register(AppConstants.HAS_CONTROLLERS, Boolean.TRUE);
		else 
			Dispatcher.forwardEvent(GradebookEvents.Startup.getEventType(), appModel);
	
	}

}
