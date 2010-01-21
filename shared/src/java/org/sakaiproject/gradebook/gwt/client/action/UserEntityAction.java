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

import org.sakaiproject.gradebook.gwt.client.model.ActionKey;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ModelData;


public abstract class UserEntityAction<M extends ModelData> extends Action {

	public enum ClassType { STRING, DOUBLE, LONG, DATE, BOOLEAN, INTEGER, CATEGORYTYPE, GRADETYPE };

	public enum Status { UNSUBMITTED, FAILED, SUCCEEDED };


	private static final long serialVersionUID = 1L;

	protected ClassType classType;

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

	public UserEntityAction getPrerequisiteAction() {
		return prerequisiteAction;
	}

	public void setPrerequisiteAction(UserEntityAction prerequisiteAction) {
		this.prerequisiteAction = prerequisiteAction;
	}

	public ClassType getClassType() {
		return classType;
	}

	public Long getParentId() {
		return get(ActionKey.PARENT_ID.name());
	}

	public void setParentId(Long parentId) {
		set(ActionKey.PARENT_ID.name(), parentId);
	}

	public M getModel() {
		return this.<M>get(ActionKey.MODEL.name());
	}

	public void setModel(M model) {
		set(ActionKey.MODEL.name(), model);
	}

	public String getKey() {
		return get(ActionKey.PROPERTY.name());
	}

	public void setKey(String key) {
		set(ActionKey.PROPERTY.name(), key);
	}

	public <X> X getValue() {
		return this.<X>get(ActionKey.VALUE.name());
	}

	public <X> void setValue(X value) {
		set(ActionKey.VALUE.name(), value);
	}

	public <X> X getStartValue() {
		return this.<X>get(ActionKey.START_VALUE.name());
	}

	public <X> void setStartValue(X startValue) {
		set(ActionKey.START_VALUE.name(), startValue);
	}

	public BaseModel getStudentModel() {
		return get(ActionKey.STUDENT_MODEL.name());
	}

	public void setStudentModel(BaseModel studentModel) {
		set(ActionKey.STUDENT_MODEL.name(), studentModel);
	}

	public Status getStatus() {
		String status = get(ActionKey.STATUS.name());

		if (status == null)
			return Status.UNSUBMITTED;
		return Status.valueOf(status);
	}

	public void setStatus(Status status) {
		set(ActionKey.STATUS.name(), status.name());
	}

}
