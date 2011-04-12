/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model.key;

public enum VerificationKey { 
	I_NUM_LRNRS("numberOfLearners"), 
	B_MISS_SCRS("isMissingScores"), 
	B_GB_WGHTD("isFullyWeighted"), 
	S_GB_WGHTD_TT("weightIssueToolTip"),
	B_CTGRY_WGHTD("isCategoryFullyWeighted");
	
	private String property;
		
	private VerificationKey(String property) {
		this.property = property;
	}
	
	public String getProperty() {
		return property;
	}
}

