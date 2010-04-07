/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model.key;

public enum GradeEventKey { 
	S_ID("id"), 
	S_GRDR_NM("graderName"), 
	S_GRD("grade"), 
	T_GRADED("gradeDate");
	
	private String property;

	private GradeEventKey(String property) {
		this.property = property;
	}
	
	public String getProperty() {
		return property;
	}
}