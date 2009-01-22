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

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.GradebookToolFacadeAsync;
import org.sakaiproject.gradebook.gwt.client.gxt.NotifyingAsyncCallback;
import org.sakaiproject.gradebook.gwt.client.model.EntityModel;

import com.extjs.gxt.ui.client.Registry;

public class RemoteCommand<M extends EntityModel> {

	public enum Status { UNSUBMITTED, FAILED, SUCCEEDED };
	
	private Status status;
	
	public RemoteCommand() {
		status = Status.UNSUBMITTED;
	}
	
	public void execute(final UserEntityAction<M> action) {
		GradebookToolFacadeAsync service = Registry.get("service");
		NotifyingAsyncCallback<M> callback = new NotifyingAsyncCallback<M>() {
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				status = Status.FAILED;
				onCommandFailure(action, caught);
			}
			
			public void onSuccess(M result) {
				status = Status.SUCCEEDED;
				onCommandSuccess(action, result);
			}
		};
		
		switch (action.getActionType()) {
		case CREATE:
			service.createEntity((UserEntityCreateAction<M>)action, callback);
			break;
		case GET:
			service.getEntity((UserEntityGetAction<M>)action, callback);
			break;
		case GRADED:
			service.updateEntity((UserEntityUpdateAction<M>)action, callback);
			break;
		case UPDATE:
			service.updateEntity((UserEntityUpdateAction<M>)action, callback);
			break;
		}
		UserActionHistory history = Registry.get("history");
		history.storeAction(action);
	}
	
	public void executeList(final UserEntityAction<M> action) {
		
		GradebookToolFacadeAsync service = Registry.get("service");
		NotifyingAsyncCallback<List<M>> callback = new NotifyingAsyncCallback<List<M>>() {
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				action.setStatus(UserEntityAction.Status.FAILED);
				onCommandFailure(action, caught);
			}
			
			public void onSuccess(List<M> result) {
				action.setStatus(UserEntityAction.Status.SUCCEEDED);
				onCommandListSuccess(action, result);
			}
		};
		switch (action.getActionType()) {
		case GET:
			service.getEntityList((UserEntityGetAction<M>)action, callback);
			break;
		case UPDATE:
			service.updateEntityList((UserEntityUpdateAction<M>)action, callback);
			break;
		}
		
		UserActionHistory history = Registry.get("history");
		history.storeAction(action);
	}


	public Status getStatus() {
		return status;
	}
	
	public void onCommandFailure(UserEntityAction<M> action, Throwable caught) {
		
	}
	
	public void onCommandSuccess(UserEntityAction<M> action, M result) {
		
	}
	
	public void onCommandListSuccess(UserEntityAction<M> action, List<M> result) {
		
	}
	
	
}
