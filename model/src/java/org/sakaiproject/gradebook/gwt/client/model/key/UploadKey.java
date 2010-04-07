/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model.key;

public enum UploadKey { 
	S_ID("id"), 
	S_NM("displayName"), 
	A_HDRS("headers"), 
	A_ROWS("rows"), 
	B_PCT("isPercentage"), 
	A_RSTS("results"), 
	M_GB_ITM("gradebookItem"), 
	I_NUM_ROWS("numberOfRows");
	
	private String property;

	private UploadKey(String property) {
		this.property = property;
	}
	
	public String getProperty() {
		return property;
	}
	
}