/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model.key;

public enum PermissionKey {
	ID(Long.class), USER_ID, USER_DISPLAY_NAME, PERMISSION_ID(Long.class), 
	GRADEBOOK_ID(Long.class), CATEGORY_ID(Long.class), CATEGORY_DISPLAY_NAME, 
	SECTION_ID, SECTION_DISPLAY_NAME, DELETE_ACTION; 
	
	private Class<?> type;
	
	private PermissionKey() {
		
	}
	
	private PermissionKey(Class<?> type) {
		this.type = type;
	}
	
	public Class<?> getType() {
		return type;
	}
	
}
