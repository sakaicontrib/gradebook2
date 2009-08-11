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

public class GradebookEvent {

	public enum EventKey { NONE(-1), BEGIN_ITEM_UPDATES(14000), BROWSE_LEARNER(14001), 
		CONFIRMATION(14002), CONFIRM_DELETE_ITEM(14003), CLEAR_SEARCH(12002),
		CLOSE_NOTIFICATION(14004), CREATE_ITEM(14005), DELETE_ITEM(14006), DO_SEARCH(12007), 
		END_ITEM_UPDATES(14007), EQUL_WEIGHT(14008), EXCEPTION(15000), EXPAND_EAST_PANEL(14014), 
		GRADEBOOK_CHANGE(12005), HIDE_COLUMN(14015), HIDE_EAST_PANEL(14060), HIDE_FORM_PANEL(15020), 
		ITEM_CREATED(14016), ITEM_DELETED(14017), ITEM_UPDATED(14018), LEARNER_GRADE_RECORD_UPDATED(14019), 
		LOAD(14020), LOAD_ITEM_TREE_MODEL(14021), MASK_ITEM_TREE(14022), NEW_CATEGORY(14025), 
		NEW_ITEM(14030), NOTIFICATION(14031), REFRESH(14032), REFRESH_GRADEBOOK_ITEMS(14033), 
		REFRESH_GRADEBOOK_SETUP(14034),
		REFRESH_COURSE_GRADES(14035), REVERT_ITEM(14036), SELECT_DELETE_ITEM(14037), 
		SELECT_ITEM(14038), SELECT_LEARNER(14039), 
		SHOW_COLUMNS(14040), SHOW_GRADE_SCALE(14041), SHOW_HISTORY(14042), SHOW_STATISTICS(14043), 
		SINGLE_GRADE(14045),
		SINGLE_VIEW(14050), START_EDIT_ITEM(14051), START_EXPORT(14052), START_IMPORT(14053), 
		STARTUP(14055), STOP_IMPORT(14056),	START_FINAL_GRADE(14057), START_GRADER_PERMISSION_SETTINGS(14058),
		STOP_GRADER_PERMISSION_SETTINGS(14059),SWITCH_EDIT_ITEM(14061), 
		SWITCH_GRADEBOOK(14065), UNMASK_ITEM_TREE(14066), 
		UPDATE_LEARNER_GRADE_RECORD(14070), UPDATE_ITEM(14075), USER_CHANGE(14090), 
		REFRESH_GRADE_SCALE(14091);
	
		private int eventType;
	
		private EventKey(int eventType) {
			this.eventType = eventType;
		}
		
		public int getEventType() {
			return eventType;
		}
		
	};
	
	private EventKey eventKey;
	
	public GradebookEvent(EventKey eventKey) {
		this.eventKey = eventKey;
	}
	
	public int getEventType() {
		return eventKey.getEventType();
	}
	
	public EventKey getEventKey() {
		return eventKey;
	}
}
