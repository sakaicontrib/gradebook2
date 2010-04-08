/**********************************************************************************
 *
 * $Id: PermissionEntryModel.java 63685 2009-09-30 01:33:01Z jlrenfro@ucdavis.edu $
 *
 ***********************************************************************************
 *
 * Copyright (c) 2008, 2009 The Regents of the University of California
 *
 * Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.gradebook.gwt.client.gxt.model;

import org.sakaiproject.gradebook.gwt.client.model.Permission;
import org.sakaiproject.gradebook.gwt.client.model.key.PermissionKey;


public class PermissionsModel extends EntityModel implements Permission {
	
	private static final long serialVersionUID = 1L;

	public PermissionsModel() {
		super();
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
		return getLong(PermissionKey.L_CTGRY_ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Permission#getDeleteAction()
	 */
	public String getDeleteAction() {
		return get(PermissionKey.S_DEL_ACT.name());
	}
	
	/*@Override
	public String getDisplayName() {
		return getIdentifier();
	}*/
	
	public Long getGradebookId() {
		return getLong(PermissionKey.L_GB_ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Permission#getId()
	 */
	public Long getId() {
		return getLong(PermissionKey.L_ID.name());
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
