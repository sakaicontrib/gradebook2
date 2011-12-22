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
import org.sakaiproject.gradebook.gwt.client.model.type.EntityType;

import com.extjs.gxt.ui.client.data.BaseModel;

public class UserEntityCreateAction<M extends BaseModel> extends UserEntityAction<M> {

	private static final long serialVersionUID = 1L;

	public UserEntityCreateAction() {
		super(ActionType.CREATE);
	}

	public UserEntityCreateAction(Gradebook gbModel, EntityType entityType, M model) {
		super(gbModel, ActionType.CREATE, entityType);
		setModel(model);
	}

	public UserEntityCreateAction(Gradebook gbModel, EntityType entityType, 
			Long parentId, String name, Double weight) {
		super(gbModel, ActionType.CREATE, entityType);
		setParentId(parentId);
		setName(name);
		setWeight(weight);
	}


	public String getName() {
		return get(ActionKey.S_NAME.name());
	}

	public void setName(String name) {
		set(ActionKey.S_NAME.name(), name);
	}

	public Double getWeight() {
		return get(ActionKey.D_WGHT.name());
	}

	public void setWeight(Double weight) {
		set(ActionKey.D_WGHT.name(), weight);
	}

	public String toString() {
		StringBuilder text = new StringBuilder();

		text.append(getActionType().getVerb()).append(" ")
		.append(getEntityType());

		return text.toString();
	}
}
