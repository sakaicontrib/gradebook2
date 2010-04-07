package org.sakaiproject.gradebook.gwt.client.model.key;

public enum ConfigurationKey {
	L_GB_ID("gradebookId"), 
	S_USER_UID("userUid"), 
	B_CLASSIC_NAV("isClassicNav");
	
	private String property;

	private ConfigurationKey(String property) {
		this.property = property;
	}
	
	public String getProperty() {
		return property;
	}
}
