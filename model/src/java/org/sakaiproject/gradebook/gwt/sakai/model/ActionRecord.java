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
import java.util.HashMap;
import java.util.Map;

public class ActionRecord {

	public static final String STATUS_FAILURE = "Failed";
	public static final String STATUS_SUCCESS = "Succeeded";
	
	private Long id;
	private Integer version;
	private String gradebookUid;
	private Long gradebookId;
	private String entityType;
	private String actionType;
	private String entityName;
	private String entityId;
	private String parentId;
	private String studentUid;
	private String status;
	private String graderId;
	private Date datePerformed;
	private Date dateRecorded;
	private Map<String, String> propertyMap;
	
	
	public ActionRecord() {
		
	}
	
	public ActionRecord(String gradebookUid, Long gradebookId, String entityType, String actionType) {
		this.gradebookUid = gradebookUid;
		this.gradebookId = gradebookId;
		this.entityType = entityType;
		this.actionType = actionType;
	}
	
	public String getGradebookUid() {
		return gradebookUid;
	}
	public void setGradebookUid(String gradebookUid) {
		this.gradebookUid = gradebookUid;
	}
	public Long getGradebookId() {
		return gradebookId;
	}
	public void setGradebookId(Long gradebookId) {
		this.gradebookId = gradebookId;
	}
	public String getEntityType() {
		return entityType;
	}
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getStudentUid() {
		return studentUid;
	}

	public void setStudentUid(String studentUid) {
		this.studentUid = studentUid;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Map<String, String> getPropertyMap() {
		if (propertyMap == null) 
			propertyMap = new HashMap<String, String>();
		return propertyMap;
	}

	public void setPropertyMap(Map<String, String> propertyMap) {
		this.propertyMap = propertyMap;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Date getDateRecorded() {
		return dateRecorded;
	}

	public void setDateRecorded(Date dateRecorded) {
		this.dateRecorded = dateRecorded;
	}

	public String getGraderId() {
		return graderId;
	}

	public void setGraderId(String graderId) {
		this.graderId = graderId;
	}

	public Date getDatePerformed() {
		return datePerformed;
	}

	public void setDatePerformed(Date datePerformed) {
		this.datePerformed = datePerformed;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
}
