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
import org.sakaiproject.gradebook.gwt.client.model.type.ActionType;
import org.sakaiproject.gradebook.gwt.client.model.type.EntityType;

import com.extjs.gxt.ui.client.data.BaseModel;

public class UserEntityGetAction<M extends BaseModel> extends UserEntityAction<M> {

	private static final long serialVersionUID = 1L;

	protected Boolean showAll;

	public UserEntityGetAction() {
		super(ActionType.GET);
	}

	public UserEntityGetAction(EntityType entityType) {
		super(ActionType.GET, entityType);
	}

	public UserEntityGetAction(String gradebookUid, EntityType entityType) {
		super(ActionType.GET, entityType);
		setGradebookUid(gradebookUid);
	}

	public UserEntityGetAction(Gradebook gbModel, EntityType entityType) {
		super(gbModel, ActionType.GET, entityType);
	}

	public UserEntityGetAction(Gradebook gbModel, EntityType entityType, String entityId, Boolean showAll) {
		this(gbModel, entityType);
		setEntityId(entityId);
		setIncludeAll(showAll);
	}

	public UserEntityGetAction(EntityType entityType, String entityId) {
		this(entityType);
		setEntityId(entityId);
	}

}
