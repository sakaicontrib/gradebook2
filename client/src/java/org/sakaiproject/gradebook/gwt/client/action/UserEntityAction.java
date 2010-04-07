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

import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.key.ActionKey;
import org.sakaiproject.gradebook.gwt.client.model.type.ActionType;
import org.sakaiproject.gradebook.gwt.client.model.type.ClassType;
import org.sakaiproject.gradebook.gwt.client.model.type.EntityType;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ModelData;


public abstract class UserEntityAction<M extends ModelData> extends Action {

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

	public UserEntityAction(Gradebook gbModel, ActionType actionType) {
		super(gbModel, actionType);
	}

	public UserEntityAction(Gradebook gbModel, ActionType actionType, EntityType entityType) {
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
		return get(ActionKey.L_PRNT_ID.name());
	}

	public void setParentId(Long parentId) {
		set(ActionKey.L_PRNT_ID.name(), parentId);
	}

	public M getModel() {
		return this.<M>get(ActionKey.M_MDL.name());
	}

	public void setModel(M model) {
		set(ActionKey.M_MDL.name(), model);
	}

	public String getKey() {
		return get(ActionKey.S_PROP.name());
	}

	public void setKey(String key) {
		set(ActionKey.S_PROP.name(), key);
	}

	public <X> X getValue() {
		return this.<X>get(ActionKey.O_VALUE.name());
	}

	public <X> void setValue(X value) {
		set(ActionKey.O_VALUE.name(), value);
	}

	public <X> X getStartValue() {
		return this.<X>get(ActionKey.O_OLD_VALUE.name());
	}

	public <X> void setStartValue(X startValue) {
		set(ActionKey.O_OLD_VALUE.name(), startValue);
	}

	public BaseModel getStudentModel() {
		return get(ActionKey.M_LRNR_MDL.name());
	}

	public void setStudentModel(BaseModel studentModel) {
		set(ActionKey.M_LRNR_MDL.name(), studentModel);
	}

	public Status getStatus() {
		String status = get(ActionKey.O_STATUS.name());

		if (status == null)
			return Status.UNSUBMITTED;
		return Status.valueOf(status);
	}

	public void setStatus(Status status) {
		set(ActionKey.O_STATUS.name(), status.name());
	}

}
