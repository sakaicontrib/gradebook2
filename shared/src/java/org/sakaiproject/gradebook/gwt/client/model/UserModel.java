/**********************************************************************************
 *
 * $Id$
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

package org.sakaiproject.gradebook.gwt.client.model;

public class UserModel extends EntityModel {

	private static final long serialVersionUID = 1L;
	
	public enum Key { ID, USER_DISPLAY_NAME };

	public UserModel() {
		super();
	}
	
	public UserModel(String userId, String userDisplayName) {
		super();
		setUserId(userId);
		setUserDisplayName(userDisplayName);
	}
	
	public void setUserId(String userId) {
		set(Key.ID.name(), userId);
	}
	
	public String getUserId() {
		return get(Key.ID.name());
	}
	
	public String getUserDisplayName() {
		return get(Key.USER_DISPLAY_NAME.name());
	}
	
	public void setUserDisplayName(String userDisplayName) {
		set(Key.USER_DISPLAY_NAME.name(), userDisplayName);
	}
	
	@Override
	public String getIdentifier() {
		return getUserId();
	}
	
	@Override
	public String getDisplayName() {
		return getUserDisplayName();
	}
	
}
