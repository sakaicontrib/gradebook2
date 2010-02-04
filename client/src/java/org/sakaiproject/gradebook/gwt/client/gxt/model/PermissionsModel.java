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

import org.sakaiproject.gradebook.gwt.client.model.key.PermissionKey;


public class PermissionsModel extends EntityModel {
	
	private static final long serialVersionUID = 1L;

	public PermissionsModel() {
		super();
	}

	public void setId(Long id) {
		set(PermissionKey.ID.name(), id);
	}
	
	public Long getId() {
		return get(PermissionKey.ID.name());
	}
	
	public void setUserId(String userId) {
		set(PermissionKey.USER_ID.name(), userId);
	}
	
	public String getUserId() {
		return get(PermissionKey.USER_ID.name());
	}
	
	public void setUserDisplayName(String userDisplayName) {
		set(PermissionKey.USER_DISPLAY_NAME.name(), userDisplayName);
	}
	
	public String getUserDisplayName() {
		return get(PermissionKey.USER_DISPLAY_NAME.name());
	}
	
	public void setPermissionId(String permissionId) {
		set(PermissionKey.PERMISSION_ID.name(), permissionId);
	}
	
	public String getPermissionId() {
		return get(PermissionKey.PERMISSION_ID.name());
	}
	
	public void setCategoryId(Long categoryId) {
		set(PermissionKey.CATEGORY_ID.name(), categoryId);
	}
	
	public Long getCategoryId() {
		return get(PermissionKey.CATEGORY_ID.name());
	}
	
	public void setCategoryDisplayName(String categoryDisplayName) {
		set(PermissionKey.CATEGORY_DISPLAY_NAME.name(), categoryDisplayName);
	}
	
	public String getCategoryDisplayName() {
		return get(PermissionKey.CATEGORY_DISPLAY_NAME.name());
	}
	
	public void setSectionId(String sectionId) {
		set(PermissionKey.SECTION_ID.name(), sectionId);
	}
	
	public String getSectionId() {
		return get(PermissionKey.SECTION_ID.name());
	}
	
	public void setSectionDisplayName(String sectionDisplayName) {
		set(PermissionKey.SECTION_DISPLAY_NAME.name(), sectionDisplayName);
	}
	
	public String getSectionDisplayName() {
		return get(PermissionKey.SECTION_DISPLAY_NAME.name());
	}
	
	public void setDeleteAction(String deleteAction) {
		set(PermissionKey.DELETE_ACTION.name(), deleteAction);
	}
	
	public String getDeleteAction() {
		return get(PermissionKey.DELETE_ACTION.name());
	}
	
	@Override
	public String getIdentifier() {
		Long id = getId();
		return (null == id) ? null : getId().toString();
	}
	
	@Override
	public String getDisplayName() {
		return getIdentifier();
	}
}
