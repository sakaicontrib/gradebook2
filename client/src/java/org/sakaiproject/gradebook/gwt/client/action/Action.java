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
package org.sakaiproject.gradebook.gwt.client.action;

import java.util.Date;

import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityModel;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.key.ActionKey;
import org.sakaiproject.gradebook.gwt.client.model.type.ActionType;
import org.sakaiproject.gradebook.gwt.client.model.type.EntityType;

public abstract class Action extends EntityModel {

	private static final long serialVersionUID = 1L;

	public Action() {
		super();
		setDatePerformed(new Date());
	}

	public Action(Gradebook gbModel) {
		this();
		setGradebookUid(gbModel.getGradebookUid());
		setGradebookId(gbModel.getGradebookId());
		setGraderName(gbModel.getUserName());
	}

	public Action(Gradebook gbModel, ActionType actionType) {
		this(gbModel);
		setActionType(actionType);
	}

	public Action(ActionType actionType) {
		this();
		setActionType(actionType);
	}

	public Action(EntityType entityType) {
		this();
		setEntityType(entityType);
	}

	public Action(ActionType actionType, EntityType entityType) {
		this();
		setActionType(actionType);
		setEntityType(entityType);
	}

	public Action(String gradebookUid, Long gradebookId) {
		this();
		setGradebookUid(gradebookUid);
		setGradebookId(gradebookId);
	}

	public Action(EntityType entityType, String gradebookUid, Long gradebookId) {
		this(entityType);
		setGradebookUid(gradebookUid);
		setGradebookId(gradebookId);
	}

	public void setIdentifier(String id) {
		set(ActionKey.S_ID.name(), id);
	}

	public String getGradebookUid() {
		return get(ActionKey.S_GB_UID.name());
	}

	public void setGradebookUid(String gradebookUid) {
		set(ActionKey.S_GB_UID.name(), gradebookUid);
	}

	public Long getGradebookId() {
		return get(ActionKey.L_GB_ID.name());
	}

	public void setGradebookId(Long gradebookId) {
		set(ActionKey.L_GB_ID.name(), gradebookId);
	}

	public ActionType getActionType() {
		String actionType = get(ActionKey.O_ACTION_TYPE.name());
		if (actionType == null)
			return null;
		return ActionType.valueOf(actionType);
	}

	public void setActionType(ActionType actionType) {
		set(ActionKey.O_ACTION_TYPE.name(), actionType.name());
	}

	public EntityType getEntityType() {
		String entityType = get(ActionKey.O_ENTY_TYPE.name());
		if (entityType == null)
			return null;
		return EntityType.valueOf(entityType);
	}

	public void setEntityType(EntityType entityType) {
		set(ActionKey.O_ENTY_TYPE.name(), entityType.name());
	}

	public String getEntityName() {
		return get(ActionKey.S_ENTY_NM.name());
	}

	public void setEntityName(String entityName) {
		set(ActionKey.S_ENTY_NM.name(), entityName);
	}

	public String getStudentUid() {
		return get(ActionKey.S_LRNR_UID.name());
	}

	public void setStudentUid(String studentUid) {
		set(ActionKey.S_LRNR_UID.name(), studentUid);
	}

	public String getStudentName() {
		return get(ActionKey.S_LRNR_NM.name());
	}

	public void setStudentName(String studentName) {
		set(ActionKey.S_LRNR_NM.name(), studentName);
	}

	public Date getDatePerformed() {
		return get(ActionKey.S_ACTION.name());
	}

	public void setDatePerformed(Date date) {
		set(ActionKey.S_ACTION.name(), date);
	}

	public Date getDateRecorded() {
		return get(ActionKey.S_RECORD.name());
	}

	public void setDateRecorded(Date date) {
		set(ActionKey.S_RECORD.name(), date);
	}

	public String getEntityId() {
		return get(ActionKey.S_ENTY_ID.name());
	}

	public void setEntityId(String entityId) {
		set(ActionKey.S_ENTY_ID.name(), entityId);
	}

	public Boolean getIncludeAll() {
		return get(ActionKey.B_INCL_ALL.name());
	}

	public void setIncludeAll(Boolean includeAll) {
		set(ActionKey.B_INCL_ALL.name(), includeAll);
	}

	public String getGraderName() {
		return get(ActionKey.S_GRDR_NM.name());
	}

	public void setGraderName(String graderName) {
		set(ActionKey.S_GRDR_NM.name(), graderName);
	}

	public String getDescription() {
		return get(ActionKey.S_DESC.name());
	}

	public void setDescription(String description) {
		set(ActionKey.S_DESC.name(), description);
	}
}
