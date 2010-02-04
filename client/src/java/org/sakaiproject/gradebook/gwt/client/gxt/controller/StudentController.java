package org.sakaiproject.gradebook.gwt.client.gxt.controller;

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.view.NotificationView;
import org.sakaiproject.gradebook.gwt.client.gxt.view.StudentView;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationModel;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;

public class StudentController extends Controller {

	private StudentView appView;
	private NotificationView notificationView;
	
	public StudentController() {
		super();
		appView = new StudentView(this);
		notificationView = new NotificationView(this);
		
		registerEventTypes(GradebookEvents.Exception.getEventType());
		registerEventTypes(GradebookEvents.Confirmation.getEventType());
		registerEventTypes(GradebookEvents.CloseNotification.getEventType());
		registerEventTypes(GradebookEvents.Notification.getEventType());
		registerEventTypes(GradebookEvents.LearnerGradeRecordUpdated.getEventType());
		registerEventTypes(GradebookEvents.RefreshGradebookItems.getEventType());
		registerEventTypes(GradebookEvents.RefreshGradebookSetup.getEventType());
		registerEventTypes(GradebookEvents.SelectLearner.getEventType());
		registerEventTypes(GradebookEvents.Startup.getEventType());
		registerEventTypes(GradebookEvents.SwitchGradebook.getEventType());
	}
	
	@Override
	public void handleEvent(AppEvent event) {
		// Note: the 'missing' break statements in this switch are intentional, they
		// allow certain events to drop through to multiple views
		switch (GradebookEvents.getEvent(event.getType()).getEventKey()) {
			case EXCEPTION:
			case CONFIRMATION:
			case CLOSE_NOTIFICATION:
			case NOTIFICATION:
				if (notificationView != null)
					forwardToView(notificationView, event);
				break;
			case LEARNER_GRADE_RECORD_UPDATED:
				forwardToView(appView, event);
				break;
			case REFRESH_GRADEBOOK_ITEMS:
			case REFRESH_GRADEBOOK_SETUP:
				forwardToView(appView, event);
				break;
			case SELECT_LEARNER:
				forwardToView(appView, event);
				break;
			case STARTUP:
				onStartup(event);
				break;
			case SWITCH_GRADEBOOK:
				forwardToView(appView, event);
				break;
		}
	}
	
	private void onStartup(AppEvent event) {
		ApplicationModel model = (ApplicationModel)event.getData();

		List<GradebookModel> gradebookModels = model.getGradebookModels();

		Registry.register(AppConstants.HELP_URL, model.getHelpUrl());
		Registry.register(AppConstants.ENABLED_GRADE_TYPES, model.getEnabledGradeTypes());

		// FIXME: Currently we only evaluate the first gradebook model to determine if we have
		// FIXME: an instructor or a student. This needs to be refined.
		for (Gradebook gbModel : gradebookModels) {
			Registry.register(gbModel.getGradebookUid(), gbModel);
			Registry.register(AppConstants.CURRENT, gbModel);

			if (appView != null)
				forwardToView(appView, event);

			return;
		}
	}
}
