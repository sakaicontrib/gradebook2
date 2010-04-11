package org.sakaiproject.gradebook.gwt.server.model;

import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.model.Permission;
import org.sakaiproject.gradebook.gwt.client.model.key.PermissionKey;
import org.sakaiproject.gradebook.gwt.server.Util;

public class PermissionImpl extends BaseModel implements Permission {

	private static final long serialVersionUID = 1L;

	public PermissionImpl() {
		super();
	}
	
	public PermissionImpl(Map<String, Object> map) {
		super(map);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Permission#getCategoryDisplayName()
	 */
	public String getCategoryDisplayName() {
		return get(PermissionKey.S_CTGRY_NAME.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Permission#getCategoryId()
	 */
	public Long getCategoryId() {
		return Util.toLong(get(PermissionKey.L_CTGRY_ID.name()));
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Permission#getDeleteAction()
	 */
	public String getDeleteAction() {
		return get(PermissionKey.S_DEL_ACT.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Permission#getGradebookId()
	 */
	public Long getGradebookId() {
		return Util.toLong(get(PermissionKey.L_GB_ID.name()));
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Permission#getId()
	 */
	public Long getId() {
		return Util.toLong(get(PermissionKey.L_ID.name()));
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Permission#getIdentifier()
	 */
	public String getIdentifier() {
		Long id = getId();
		return (null == id) ? null : getId().toString();
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Permission#getPermissionId()
	 */
	public String getPermissionId() {
		return get(PermissionKey.S_PERM_ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Permission#getSectionDisplayName()
	 */
	public String getSectionDisplayName() {
		return get(PermissionKey.S_SECT_NM.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Permission#getSectionId()
	 */
	public String getSectionId() {
		return get(PermissionKey.S_SECT_ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Permission#getUserDisplayName()
	 */
	public String getUserDisplayName() {
		return get(PermissionKey.S_DSPLY_NM.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Permission#getUserId()
	 */
	public String getUserId() {
		return get(PermissionKey.S_USR_ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Permission#setCategoryDisplayName(java.lang.String)
	 */
	public void setCategoryDisplayName(String categoryDisplayName) {
		set(PermissionKey.S_CTGRY_NAME.name(), categoryDisplayName);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Permission#setCategoryId(java.lang.Long)
	 */
	public void setCategoryId(Long categoryId) {
		set(PermissionKey.L_CTGRY_ID.name(), categoryId);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Permission#setDeleteAction(java.lang.String)
	 */
	public void setDeleteAction(String deleteAction) {
		set(PermissionKey.S_DEL_ACT.name(), deleteAction);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Permission#setGradebookId()
	 */
	public void setGradebookId(Long gradebookId) {
		set(PermissionKey.L_GB_ID.name(), gradebookId);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Permission#setId(java.lang.Long)
	 */
	public void setId(Long id) {
		set(PermissionKey.L_ID.name(), id);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Permission#setPermissionId(java.lang.String)
	 */
	public void setPermissionId(String permissionId) {
		set(PermissionKey.S_PERM_ID.name(), permissionId);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Permission#setSectionDisplayName(java.lang.String)
	 */
	public void setSectionDisplayName(String sectionDisplayName) {
		set(PermissionKey.S_SECT_NM.name(), sectionDisplayName);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Permission#setSectionId(java.lang.String)
	 */
	public void setSectionId(String sectionId) {
		set(PermissionKey.S_SECT_ID.name(), sectionId);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Permission#setUserDisplayName(java.lang.String)
	 */
	public void setUserDisplayName(String userDisplayName) {
		set(PermissionKey.S_DSPLY_NM.name(), userDisplayName);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Permission#setUserId(java.lang.String)
	 */
	public void setUserId(String userId) {
		set(PermissionKey.S_USR_ID.name(), userId);
	}
	
}
