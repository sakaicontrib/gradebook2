/**********************************************************************************
*
* $Id:$
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

import java.util.Date;

public class UserDereferenceRealmUpdate {

	private Long id;
	private String realmId;
	private Date lastUpdate;
	private Integer realmCount;
	
	public UserDereferenceRealmUpdate() {
		
	}
	
	public UserDereferenceRealmUpdate(String realmId, Integer realmCount) {
		this.realmId = realmId;
		this.lastUpdate = new Date();
		this.realmCount = realmCount;
	}
	
	public String getRealmId() {
		return realmId;
	}
	public void setRealmId(String realmId) {
		this.realmId = realmId;
	}
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getRealmCount() {
		return realmCount;
	}

	public void setRealmCount(Integer realmCount) {
		this.realmCount = realmCount;
	}
	
}
