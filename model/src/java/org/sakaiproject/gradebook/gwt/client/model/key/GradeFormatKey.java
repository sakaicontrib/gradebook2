package org.sakaiproject.gradebook.gwt.client.model.key;

public enum GradeFormatKey { 
	
	L_ID("id"), 
	S_NM("name"),
	B_LK("locked");
	
	private String property;

	private GradeFormatKey(String property) {
		this.property = property;
	}

	public String getProperty() {
		return property;
	}
}