package org.sakaiproject.gradebook.gwt.client;

import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;

import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;

public abstract class RestCallback {

	public abstract void onSuccess(Request request, Response response);
	
	public void onFailure(Request request, Throwable exception) {
		onError(request, exception, 500);
	}
	
	public void onError(Request request, Throwable exception, Integer statusCode) {
		Dispatcher.forwardEvent(GradebookEvents.Exception.getEventType(), new NotificationEvent(exception));
	}
	
	
	public void onError(Request request, Throwable exception) {
		onError(request, exception, null);
	}
}
