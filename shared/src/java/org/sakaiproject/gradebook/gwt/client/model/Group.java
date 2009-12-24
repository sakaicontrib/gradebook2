/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model;

public enum Group {
	STUDENT_INFORMATION("Student Information"),
	GRADES("Grades"), 
	ASSIGNMENTS("Assignments");
	
	private String displayName;
	
	private Group(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
}