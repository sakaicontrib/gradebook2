package org.sakaiproject.gradebook.gwt.client.gxt.event;

public class NotificationEvent {

	private String context;
	private String message;
	private Throwable t;
	
	
	public NotificationEvent(String message) {
		this.message = message;
	}
	
	public NotificationEvent(Throwable t) {
		this.t = t;
	}
	
	public NotificationEvent(Throwable t, String context) {
		this(t);
		this.context = context;
	}
	
	public Throwable getError() {
		return t;
	}
	
	public String getText() {
		StringBuilder text = new StringBuilder();
		
		if (context != null)
			text.append(context);
		
		if (t != null)
			text.append(t.getMessage());
		
		if (message != null)
			text.append(message);
		
		return text.toString();
	}
	
}
