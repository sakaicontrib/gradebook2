package org.sakaiproject.gradebook.gwt.client.model.key;


public enum GradebookKey {
	S_GB_UID("gradebookUid"),
	L_GB_ID("gradebookId"), 
	S_NM("name"),
	M_GB_ITM("gradebookItem"),
	A_CLMNS("fixedColumns"), 
	S_USR_NM("userName"),  
	B_NEW_GB("isNewGradebook"),
	M_CONF("config"), 
	A_STATS("stats"),
	M_USER("user");
	
	private String property;

	private GradebookKey(String property) {
		this.property = property;
	}
	
	public String getProperty() {
		return property;
	}
}
