/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model.type;

public enum GradeType { POINTS("Points"), PERCENTAGES("Percentages"), LETTERS("Letters"), TEXT("Text");
	private String displayName;
	
	GradeType(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
	
}