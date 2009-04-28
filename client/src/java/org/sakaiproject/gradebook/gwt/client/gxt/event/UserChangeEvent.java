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
package org.sakaiproject.gradebook.gwt.client.gxt.event;

import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.ActionType;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;

import com.extjs.gxt.ui.client.event.BaseEvent;

public class UserChangeEvent extends BaseEvent {

	private UserEntityAction action;
	private boolean isTransferred;
	
	public UserChangeEvent() {
		super(null);
		this.isTransferred = false;
	}
	
	public UserChangeEvent(UserEntityAction action) {
		this();
		this.action = action;
	}

	public UserChangeEvent(EntityType entityEventType, ActionType actionEventType) {
		this();
		this.action = new UserEntityUpdateAction(entityEventType, actionEventType);
	}
	
	public UserEntityAction getAction() {
		return action;
	}

	public void setAction(UserEntityAction action) {
		this.action = action;
	}

	public boolean isTransferred() {
		return isTransferred;
	}

	public void setTransferred(boolean isTransferred) {
		this.isTransferred = isTransferred;
	}
	
}
