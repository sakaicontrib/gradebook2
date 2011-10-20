package org.sakaiproject.gradebook.gwt.client.model.key;

public enum ApplicationKey {
	A_GB_MODELS("gradebookModels"), 
	S_PLACE_ID("placementId"), 
	S_HELPURL("helpUrl"), 
	S_FIND_BY_FIELD("searchRosterByFieldEnabled"), 
	V_ENBLD_GRD_TYPES("enabledGradeTypes"),
	S_SH_WTD_ENABLED("showWeightedEnabled"),
	S_AUTH_DETAILS("authorizationDetails"),
	B_CHECK_FINAL_GRADE_SUBMISSION_STATUS("checkFinalGradeSubmissionStatus");

	
	private String property;

	private ApplicationKey(String property) {
		this.property = property;
	}
	
	public String getProperty() {
		return property;
	}
}
