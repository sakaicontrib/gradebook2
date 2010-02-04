package org.sakaiproject.gradebook.gwt.server.model;

import java.util.HashMap;
import java.util.Map;

public class BaseModel extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	public BaseModel() {
		super();
	}
	
	public BaseModel(Map<String, Object> map) {
		super(map);
	}
	
	@SuppressWarnings("unchecked")
	public <X> X get(String property) {
		return (X)super.get(property);
	}
	
	@SuppressWarnings("unchecked")
	public <X> X set(String property, X value) {
		return (X)put(property, value);
	}
	
}
