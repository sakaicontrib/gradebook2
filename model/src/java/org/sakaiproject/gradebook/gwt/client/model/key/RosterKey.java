package org.sakaiproject.gradebook.gwt.client.model.key;

public enum RosterKey {
	
	LEARNER_PAGE("learnerPage"), TOTAL("total");
	
	private String property;
	
	private RosterKey(String property) {
		this.property = property;
	}
	
	public String toString() {
		return property;
	}
	
}
