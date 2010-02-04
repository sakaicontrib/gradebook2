package org.sakaiproject.gradebook.gwt.sakai.rest.model;

import java.util.HashMap;

import org.sakaiproject.gradebook.gwt.client.model.HistoryRecord;

public class HistoryRecordImpl extends HashMap<String, Object> implements
		HistoryRecord {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public <X> X get(String property) {
		return (X)super.get(property);
	}
	
	public <X> void set(String property, X object) {
		put(property, object);
	}

}
