/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model.key;


public enum ActionKey { ID, GRADEBOOK_UID, GRADEBOOK_ID, DATE_PERFORMED, 
	DATE_RECORDED,
	ENTITY_TYPE, ENTITY_NAME, 
	STUDENT_UID, STUDENT_NAME, ENTITY_ID, INCLUDE_ALL, PROPERTY, PARENT_ID, ACTION_TYPE, MODEL, 
	STUDENT_MODEL,
	VALUE, START_VALUE, NAME, WEIGHT, EQUAL_WEIGHT, DROP_LOWEST, POINTS, DUE_DATE, 
	STATUS, GRADER_NAME, DESCRIPTION, TEXT,
	PROPERTY_NAME;

	private Class<?> type;
	
	private ActionKey() {
		
	}
	
	private ActionKey(Class<?> type) {
		this.type = type;
	}
	
	public Class<?> getType() {
		return type;
	}

}