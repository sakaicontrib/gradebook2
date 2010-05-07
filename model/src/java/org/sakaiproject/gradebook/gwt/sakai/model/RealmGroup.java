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

package org.sakaiproject.gradebook.gwt.sakai.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


public class RealmGroup {
	
	Long realmKey;
	String userId;
	Long roleKey;
	Boolean active;
	Realm realm;
	RealmRole role;
	RealmRlGroupId id;
	
	
	public RealmRlGroupId getId() {
		return id;
	}

	public void setId(RealmRlGroupId id) {
		this.id = id;
	}

	public Realm getRealm() {
		return realm;
	}

	public void setRealm(Realm realm) {
		this.realm = realm;
	}

	public RealmRole getRole() {
		return role;
	}

	public void setRole(RealmRole role) {
		this.role = role;
	}

	public Long getRealmKey() {
	
		return realmKey;
	}
	
	public void setRealmKey(Long realmKey) {
	
		this.realmKey = realmKey;
	}
	
	public String getUserId() {
	
		return userId;
	}
	
	public void setUserId(String userId) {
	
		this.userId = userId;
	}

	public Long getRoleKey() {
		return roleKey;
	}

	public void setRoleKey(Long roleKey) {
		this.roleKey = roleKey;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	

}
