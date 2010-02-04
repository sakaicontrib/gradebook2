/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model.type;

public enum CategoryType { NO_CATEGORIES("No Categories"), SIMPLE_CATEGORIES("Categories"), 
	WEIGHTED_CATEGORIES("Weighted Categories");

	private String displayName;
	
	CategoryType(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
	
}