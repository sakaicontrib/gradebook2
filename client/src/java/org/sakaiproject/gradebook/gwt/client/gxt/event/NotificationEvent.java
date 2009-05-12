package org.sakaiproject.gradebook.gwt.client.gxt.event;

public class NotificationEvent {

	private Throwable t;
	
	public NotificationEvent(Throwable t) {
		this.t = t;
	}
	
	public Throwable getError() {
		return t;
	}
	
}
