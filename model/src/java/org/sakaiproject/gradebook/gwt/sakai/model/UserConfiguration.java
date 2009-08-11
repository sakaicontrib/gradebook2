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

import java.io.Serializable;

public class UserConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String userUid;
	private Long gradebookId;
	private String configField;
	private String configValue;
	
	
	public UserConfiguration() {
		
	}
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getUserUid() {
		return userUid;
	}


	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}


	public Long getGradebookId() {
		return gradebookId;
	}


	public void setGradebookId(Long gradebookId) {
		this.gradebookId = gradebookId;
	}


	public String getConfigField() {
		return configField;
	}


	public void setConfigField(String configField) {
		this.configField = configField;
	}


	public String getConfigValue() {
		return configValue;
	}


	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}
}
