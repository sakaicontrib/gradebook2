/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model.type;

public enum GroupType {
	STUDENT_INFORMATION("Student Information"),
	GRADES("Grades"), 
	ASSIGNMENTS("Assignments");
	
	private String displayName;
	
	private GroupType(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
}