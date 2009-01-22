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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.action.RemoteCommand.Status;
import org.sakaiproject.gradebook.gwt.client.model.EntityModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;

import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.GearsException;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.user.client.Window;

public class RemoteCommandGears<M extends EntityModel> {
	private Database db;
	
	private Status status;
	
	public RemoteCommandGears() {
		status = Status.UNSUBMITTED;
	}
	
	public void execute(final UserEntityAction<M> action) {
		openDb();
		M entity = null;
		switch (action.getEntityType()) {
		case STUDENT:
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(action.getKey(), action.getValue());
			properties.put(StudentModel.Key.COURSE_GRADE.name(), "Unknown");
			entity = (M)new StudentModel(properties);
			break;
		}
		
		onCommandSuccess(action, entity);
	}
	
	public void executeList(final UserEntityAction<M> action) {
		
		onCommandListSuccess(action, null);
	}
	
	
	private void openDb() {
		Window.alert("Using gears!");
		// Create the database if it doesn't exist.
	    try {
	      db = Factory.getInstance().createDatabase();
	      db.open("gb-gears-database");
	      db.execute("create table if not exists action (Timestamp int)");
	      
	    } catch (GearsException e) {
	      Window.alert(e.toString());
	    }
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
