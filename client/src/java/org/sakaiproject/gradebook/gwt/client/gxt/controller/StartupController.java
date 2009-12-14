package org.sakaiproject.gradebook.gwt.client.gxt.controller;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.Gradebook2RPCServiceAsync;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.SecureToken;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationModel;
import org.sakaiproject.gradebook.gwt.client.model.AuthModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;

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

		final I18nConstants i18n = Registry.get(AppConstants.I18N);
		
		if (isUserAbleToGrade) {
			GWT.runAsync(new RunAsyncCallback() {
				public void onFailure(Throwable caught) {
					
				}
	
				public void onSuccess() {
					Dispatcher dispatcher = Dispatcher.get();
					dispatcher.addController(new InstructorController(i18n, isUserAbleToEditItems, isNewGradebook));					
					dispatcher.addController(new ServiceController());
					getApplicationModel(0, authModel);
				}
			});
		} else if (isUserAbleToViewOwnGrades) {
			GWT.runAsync(new RunAsyncCallback() {
				public void onFailure(Throwable caught) {
					
				}
	
				public void onSuccess() {
					Dispatcher dispatcher = Dispatcher.get();
					dispatcher.addController(new StudentController());
					dispatcher.addController(new ServiceController());
					getApplicationModel(0, authModel);
				}
			});
		}
	}

	private void getApplicationModel(final int i, final AuthModel authModel) {
		AsyncCallback<ApplicationModel> callback = 
			new AsyncCallback<ApplicationModel>() {

				public void onFailure(Throwable caught) {
					Dispatcher dispatcher = Dispatcher.get();
					// If this is the first try, then give it another shot
					if (i == 0)
						getApplicationModel(i+1, authModel);
					else
						dispatcher.dispatch(GradebookEvents.Exception.getEventType(), new NotificationEvent(caught));
				}

				public void onSuccess(ApplicationModel result) {
					Dispatcher dispatcher = Dispatcher.get();
					dispatcher.dispatch(GradebookEvents.Startup.getEventType(), result);
				}
			
		};
		Gradebook2RPCServiceAsync dataService = Registry.get(AppConstants.SERVICE);
		dataService.get(null, null, EntityType.APPLICATION, null, null, SecureToken.get(), callback);
	}
	

}
