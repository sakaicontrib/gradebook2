package org.sakaiproject.gradebook.gwt.client.model.key;

public enum FinalGradeSubmissionResultKey {
	
	I_STATUS("status"),
	S_DATA("data");
	
	private String property;
	
	private FinalGradeSubmissionResultKey(String property) {
		
		this.property = property;
	}
	
	public String getProperty() {

		return property;
	}
}
