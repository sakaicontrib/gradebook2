/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model.key;

public enum GradeMapKey { 
	S_ID("id"), 
	S_LTR_GRD("letterGrade"), 
	D_FROM("fromRange"), 
	D_TO("toRange");
	
	private String property;

	private GradeMapKey(String property) {
		this.property = property;
	}

	public String getProperty() {
		return property;
	}
	
}