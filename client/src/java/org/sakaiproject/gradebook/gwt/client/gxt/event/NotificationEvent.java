package org.sakaiproject.gradebook.gwt.client.gxt.event;

public class NotificationEvent {

	private String title;
	private String context;
	private String message;
	private Throwable t;
	private boolean isFailure;
	
	
	public NotificationEvent(String title, String message) {
		this.title = title;
		this.message = message;
		this.isFailure = false;
	}
	
	public NotificationEvent(Throwable t) {
		this.t = t;
		this.isFailure = true;
	}
	
	public NotificationEvent(Throwable t, String context) {
		this(t);
		this.context = context;
		this.isFailure = true;
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

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public boolean isFailure() {
		return isFailure;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
