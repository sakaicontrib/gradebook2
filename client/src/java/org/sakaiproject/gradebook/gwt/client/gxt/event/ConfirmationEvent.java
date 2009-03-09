package org.sakaiproject.gradebook.gwt.client.gxt.event;

public class ConfirmationEvent {

	public String text;
	
	public int okEventType;
	public Object okEventData;
	
	public int cancelEventType;
	public Object cancelEventData;
	
	public ConfirmationEvent(String text) {
		this.text = text;
		this.okEventType = -1;
		this.cancelEventType = -1;
	}
	
}
