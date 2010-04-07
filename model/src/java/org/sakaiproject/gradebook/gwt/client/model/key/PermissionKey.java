/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model.key;

public enum PermissionKey {
	L_ID("id"), 
	S_USR_ID("userId"), 
	S_DSPLY_NM("displayName"), 
	S_PERM_ID("permissionId"), 
	L_GB_ID("gradebookId"), 
	L_CTGRY_ID("categoryId"), 
	S_CTGRY_NAME("categoryName"), 
	S_SECT_ID("sectionId"), 
	S_SECT_NM("sectionName"), 
	S_DEL_ACT("deleteAction");
	
	private String property;

	private PermissionKey(String property) {
		this.property = property;
	}
	
	public String getProperty() {
		return property;
	}
}
