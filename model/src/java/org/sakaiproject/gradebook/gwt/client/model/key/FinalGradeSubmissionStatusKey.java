package org.sakaiproject.gradebook.gwt.client.model.key;

public enum FinalGradeSubmissionStatusKey {
	
	S_BANNER("bannerMessage"),
	S_DIALOG("dialogMessage");
	
	private String property;
	
	private FinalGradeSubmissionStatusKey(String property) {
		
		this.property = property;
	}
	
	public String getProperty() {
		
		return property;
	}

}
