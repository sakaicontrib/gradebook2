package org.sakaiproject.gradebook.gwt.client.model.key;

public enum ApplicationKey {
	A_GB_MODELS("gradebookModels"), 
	S_PLACE_ID("placementId"), 
	S_HELPURL("helpUrl"), 
	V_ENBLD_GRD_TYPES("enabledGradeTypes"),
	S_SH_WTD_ENABLED("showWeightedEnabled");

	
	private String property;

	private ApplicationKey(String property) {
		this.property = property;
	}
	
	public String getProperty() {
		return property;
	}
}
