package org.sakaiproject.gradebook.gwt.client.model;

public interface HistoryRecord {

	public abstract <X> X get(String property);
	
	public abstract <X> X set(String property, X object);
	
}
