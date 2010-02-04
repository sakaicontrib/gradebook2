package org.sakaiproject.gradebook.gwt.client.model;

public interface HistoryRecord {

	public abstract <X> X get(String property);
	
	public abstract <X> void set(String property, X object);
	
}
