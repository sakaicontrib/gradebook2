/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model.key;

public enum SectionKey { 
	S_ID("id"), S_NM("name");

	private String property;

	private SectionKey(String property) {
		this.property = property;
	}
	
	public String getProperty() {
		return property;
	}
	
}