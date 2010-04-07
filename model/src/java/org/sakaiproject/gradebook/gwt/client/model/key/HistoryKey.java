package org.sakaiproject.gradebook.gwt.client.model.key;

public enum HistoryKey {
	A_PAGE("page"), 
	I_TOTAL("total");
	
	private String property;

	private HistoryKey(String property) {
		this.property = property;
	}

	public String getProperty() {
		return property;
	}
}
