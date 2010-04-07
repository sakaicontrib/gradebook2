/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model.key;

public enum GraderKey { 
	S_ID("id"), 
	S_NM("name") ;

	private String property;

	private GraderKey(String property) {
		this.property = property;
	}

	public String getProperty() {
		return property;
	}
}