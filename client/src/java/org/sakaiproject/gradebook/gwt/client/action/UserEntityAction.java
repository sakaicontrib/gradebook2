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

import org.sakaiproject.gradebook.gwt.client.gxt.Notifier;
import org.sakaiproject.gradebook.gwt.client.model.EntityModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;


public abstract class UserEntityAction<M extends EntityModel> extends Action {
	
	public enum ClassType { STRING, DOUBLE, LONG, DATE, BOOLEAN, INTEGER, CATEGORYTYPE, GRADETYPE };
	
	public enum Status { UNSUBMITTED, FAILED, SUCCEEDED };
	
	protected static Notifier notifier = new Notifier();
	
	private static final long serialVersionUID = 1L;

	//protected Status status;
	
	protected ClassType classType;
	
	/*protected String key;
	protected Long entityId;
	protected Long parentId;
	protected ActionType actionType;
	protected M model;
	private StudentModel studentModel;
	
	protected ClassType classType;
	protected String stringValue, stringStartValue;
	protected Double doubleValue, doubleStartValue;
	protected Long longValue, longStartValue;
	protected Date dateValue, dateStartValue;
	protected Boolean booleanValue, booleanStartValue;
	protected Integer integerValue, integerStartValue;
	protected CategoryType categoryTypeValue, categoryTypeStartValue;
	protected GradeType gradeTypeValue, gradeTypeStartValue;*/
	
	private UserEntityAction prerequisiteAction;
	

	public UserEntityAction() {
		super();
	}
	
	public UserEntityAction(ActionType actionType) {
		super(actionType);
	}
	
	public UserEntityAction(GradebookModel gbModel, ActionType actionType) {
		super(gbModel, actionType);
	}
	
	public UserEntityAction(GradebookModel gbModel, ActionType actionType, EntityType entityType) {
		super(gbModel, actionType);
		setEntityType(entityType);
	}
	
	public UserEntityAction(ActionType actionType, EntityType entityType) {
		super(actionType, entityType);
	}
	

	
	
	/*
	public <X> X getValue() {
		switch (classType) {
		case BOOLEAN:
			return (X)booleanValue;
		case DOUBLE:
			return (X)doubleValue;
		case STRING:
			return (X)stringValue;
		case INTEGER:
			return (X)integerValue;
		case DATE:
			return (X)dateValue;
		case LONG:
			return (X)longValue;
		case CATEGORYTYPE:
			return (X)categoryTypeValue;
		case GRADETYPE:
			return (X)gradeTypeValue;
		}
		return null;
	}
	
	public <X> X getStartValue() {
		switch (classType) {
		case BOOLEAN:
			return (X)booleanStartValue;
		case DOUBLE:
			return (X)doubleStartValue;
		case STRING:
			return (X)stringStartValue;
		case INTEGER:
			return (X)integerStartValue;
		case DATE:
			return (X)dateStartValue;
		case LONG:
			return (X)longStartValue;
		case CATEGORYTYPE:
			return (X)categoryTypeStartValue;
		case GRADETYPE:
			return (X)gradeTypeStartValue;
		}
		return null;
	}*/

	public UserEntityAction getPrerequisiteAction() {
		return prerequisiteAction;
	}

	public void setPrerequisiteAction(UserEntityAction prerequisiteAction) {
		this.prerequisiteAction = prerequisiteAction;
	}

	public ClassType getClassType() {
		return classType;
	}
	
	/*protected abstract void doExecute(GradebookToolFacadeAsync service, AsyncCallback<M> callback);
	
	protected void doExecuteList(GradebookToolFacadeAsync service, AsyncCallback<List<M>> callback) {
		
	}*/
	
	
	public abstract void announce(Object... params);
	
	public void notifyUser(String formatText, Object... params) {
		StringBuilder text = new StringBuilder();
		
		text.append(getActionType().getVerb()).append(" ")
			.append(getEntityType()).append(formatText);
		
		notifier.notify(getActionType().toString(), text.toString(), params);
	}

	public Long getParentId() {
		return get(Key.PARENT_ID.name());
	}

	public void setParentId(Long parentId) {
		set(Key.PARENT_ID.name(), parentId);
	}

	public M getModel() {
		return this.<M>get(Key.MODEL.name());
	}

	public void setModel(M model) {
		set(Key.MODEL.name(), model);
	}

	/*public void setCallback(UserActionCallback callback) {
		this.actionCallback = callback;
	}*/

	/*public Status getStatus() {
		return status;
	}*/

	public String getKey() {
		return get(Key.PROPERTY.name());
	}

	public void setKey(String key) {
		set(Key.PROPERTY.name(), key);
	}
	
	public <X> X getValue() {
		return this.<X>get(Key.VALUE.name());
	}
	
	public <X> void setValue(X value) {
		set(Key.VALUE.name(), value);
	}
	
	public <X> X getStartValue() {
		return this.<X>get(Key.START_VALUE.name());
	}
	
	public <X> void setStartValue(X startValue) {
		set(Key.START_VALUE.name(), startValue);
	}
	
	public StudentModel getStudentModel() {
		return get(Key.STUDENT_MODEL.name());
	}

	public void setStudentModel(StudentModel studentModel) {
		set(Key.STUDENT_MODEL.name(), studentModel);
	}
	
	public Status getStatus() {
		String status = get(Key.STATUS.name());
		
		if (status == null)
			return Status.UNSUBMITTED;
		return Status.valueOf(status);
	}
	
	public void setStatus(Status status) {
		set(Key.STATUS.name(), status.name());
	}
	
}
