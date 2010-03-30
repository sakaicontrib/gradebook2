package org.sakaiproject.gradebook.gwt.client.gxt.controller;

import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.view.NotificationView;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;

public class NotificationController extends Controller {

	private NotificationView notificationView;
	
	public NotificationController() {
		notificationView = new NotificationView(this);
		
		registerEventTypes(GradebookEvents.CloseNotification.getEventType());
		registerEventTypes(GradebookEvents.Confirmation.getEventType());
		registerEventTypes(GradebookEvents.Exception.getEventType());
		registerEventTypes(GradebookEvents.Notification.getEventType());
	}
	
	@Override
	public void handleEvent(AppEvent event) {
		switch (GradebookEvents.getEvent(event.getType()).getEventKey()) {
		case EXCEPTION:
		case CONFIRMATION:
		case CLOSE_NOTIFICATION:
		case NOTIFICATION:
			if (notificationView != null)
				forwardToView(notificationView, event);
			break;
		}
	}

}
