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

import org.sakaiproject.gradebook.gwt.client.model.EntityModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;

public abstract class Action extends EntityModel {

	private static final long serialVersionUID = 1L;

	public enum ActionType { CREATE("Create", "Added "), 
		GET("Get", "retrieved"), 
		GRADED("Grade", "Set grade to"),
		UPDATE("Update", "Changed to"), 
		DELETE("Delete", "deleted");
		
		private String desc;
		private String verb;
		
		private ActionType(String desc, String verb) {
			this.desc = desc;
			this.verb = verb;
		}
		
		public String getVerb() {
			return verb;
		}
	
		@Override
		public String toString() {
			return desc;
		}
		
	};
	
	public enum EntityType { APPLICATION("application"), GRADE_ITEM("grade item"),
		CATEGORY("category"), COLUMN("column"), COMMENT("comment"), 
		GRADEBOOK("gradebook"), GRADE_SCALE("grade scale"), GRADE_RECORD("grade record"), 
		GRADE_EVENT("grade event"),
		SECTION("section"), STUDENT("student"), ACTION("action");
	
		private String name;
	
		private EntityType(String name) {
			this.name = name;
		}
	
		@Override
		
		public String toString() {
			return name;
		}
		
	};
	
	public enum Key { ID, GRADEBOOK_UID, GRADEBOOK_ID, DATE_PERFORMED, DATE_RECORDED,
		ENTITY_TYPE, ENTITY_NAME, 
		STUDENT_UID, ENTITY_ID, INCLUDE_ALL, PROPERTY, PARENT_ID, ACTION_TYPE, MODEL, 
		STUDENT_MODEL,
		VALUE, START_VALUE, NAME, WEIGHT, EQUAL_WEIGHT, DROP_LOWEST, POINTS, DUE_DATE, 
		STATUS, GRADER_NAME, DESCRIPTION, TEXT,
		PROPERTY_NAME };

		
	/*protected Date timeStamp;
	protected String gradebookUid;
	protected Long gradebookId;
	protected EntityType entityType;
	protected String studentUid;*/
	
	public Action() {
		super();
		setDatePerformed(new Date());
	}
	
	public Action(GradebookModel gbModel) {
		this();
		setGradebookUid(gbModel.getGradebookUid());
		setGradebookId(gbModel.getGradebookId());
		setGraderName(gbModel.getUserName());
	}
	
	public Action(GradebookModel gbModel, ActionType actionType) {
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
	
	@Override
	public String getDisplayName() {
		return getEntityName();
	}
	
	@Override
	public String getIdentifier() {
		return get(Key.ID.name());
	}
	
	public void setIdentifier(String id) {
		set(Key.ID.name(), id);
	}

	public String getGradebookUid() {
		return get(Key.GRADEBOOK_UID.name());
	}
	
	public void setGradebookUid(String gradebookUid) {
		set(Key.GRADEBOOK_UID.name(), gradebookUid);
	}

	public Long getGradebookId() {
		return get(Key.GRADEBOOK_ID.name());
	}
	
	public void setGradebookId(Long gradebookId) {
		set(Key.GRADEBOOK_ID.name(), gradebookId);
	}

	public ActionType getActionType() {
		String actionType = get(Key.ACTION_TYPE.name());
		if (actionType == null)
			return null;
		return ActionType.valueOf(actionType);
	}

	public void setActionType(ActionType actionType) {
		set(Key.ACTION_TYPE.name(), actionType.name());
	}
	
	public EntityType getEntityType() {
		String entityType = get(Key.ENTITY_TYPE.name());
		if (entityType == null)
			return null;
		return EntityType.valueOf(entityType);
	}
	
	public void setEntityType(EntityType entityType) {
		set(Key.ENTITY_TYPE.name(), entityType.name());
	}

	public String getEntityName() {
		return get(Key.ENTITY_NAME.name());
	}
	
	public void setEntityName(String entityName) {
		set(Key.ENTITY_NAME.name(), entityName);
	}
	
	public String getStudentUid() {
		return get(Key.STUDENT_UID.name());
	}

	public void setStudentUid(String studentUid) {
		set(Key.STUDENT_UID.name(), studentUid);
	}

	public Date getDatePerformed() {
		return get(Key.DATE_PERFORMED.name());
	}
	
	public void setDatePerformed(Date date) {
		set(Key.DATE_PERFORMED.name(), date);
	}
	
	public Date getDateRecorded() {
		return get(Key.DATE_RECORDED.name());
	}
	
	public void setDateRecorded(Date date) {
		set(Key.DATE_RECORDED.name(), date);
	}
	
	public String getEntityId() {
		return get(Key.ENTITY_ID.name());
	}

	public void setEntityId(String entityId) {
		set(Key.ENTITY_ID.name(), entityId);
	}

	public Boolean getIncludeAll() {
		return get(Key.INCLUDE_ALL.name());
	}

	public void setIncludeAll(Boolean includeAll) {
		set(Key.INCLUDE_ALL.name(), includeAll);
	}
	
	public String getGraderName() {
		return get(Key.GRADER_NAME.name());
	}
	
	public void setGraderName(String graderName) {
		set(Key.GRADER_NAME.name(), graderName);
	}
	
	public String getDescription() {
		return get(Key.DESCRIPTION.name());
	}
	
	public void setDescription(String description) {
		set(Key.DESCRIPTION.name(), description);
	}
	
}
