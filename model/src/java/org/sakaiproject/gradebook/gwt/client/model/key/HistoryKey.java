package org.sakaiproject.gradebook.gwt.client.model.key;

public enum HistoryKey {

	HISTORY_PAGE("historyPage"), TOTAL("total");
	
	private String property;
	
	private HistoryKey(String property) {
		this.property = property;
	}
	
	public String toString() {
		return property;
	}
	
}
