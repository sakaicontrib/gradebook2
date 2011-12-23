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

import com.extjs.gxt.ui.client.event.EventType;

public class GradebookEvent {

	public enum EventKey { 
		NONE, BEGIN_ITEM_UPDATES, BROWSE_LEARNER, 
		CONFIGURATION, CONFIRMATION, CONFIRM_DELETE_ITEM, CLEAR_SEARCH,
		CLOSE_NOTIFICATION, CREATE_ITEM, CREATE_PERMISSION, DELETE_GRADE_MAP, 
		DELETE_ITEM, DELETE_PERMISSION, DO_SEARCH, END_ITEM_UPDATES, 
		EXCEPTION, EXPAND_EAST_PANEL, FAILED_TO_UPDATE_ITEM, FINISH_TREE_ITEM_DRAG_AND_DROP,
		GRADEBOOK_CHANGE, GRADE_TYPE_UPDATED, GRADE_SCALE_UPDATE_ERROR, HIDE_COLUMN, HIDE_EAST_PANEL, HIDE_FORM_PANEL, 
		HIDE_USER_FEEDBACK,
		ITEM_CREATED, ITEM_DELETED, ITEM_UPDATED, LEARNER_GRADE_RECORD_UPDATED, 
		LOAD, LOAD_ITEM_TREE_MODEL, MASK_ITEM_TREE, MASK_MULTI_GRADE_GRID, NEW_CATEGORY, 
		NEW_ITEM, NOTIFICATION, PERMISSION_CREATED, PERMISSION_DELETED,
		REFRESH, REFRESH_GRADEBOOK_ITEMS, 
		REFRESH_GRADEBOOK_SETUP,
		REFRESH_COURSE_GRADES, REVERT_ITEM, SELECT_DELETE_ITEM, 
		SELECT_ITEM, SELECT_LEARNER, 
		SHOW_COLUMNS, SHOW_GRADE_SCALE, SHOW_HISTORY, SHOW_STATISTICS,
		SHOW_USER_FEEDBACK,
		STOP_STATISTICS,
		SINGLE_GRADE,
		SINGLE_VIEW, START_EDIT_ITEM, START_EXPORT, START_IMPORT, 
		STARTUP, STOP_IMPORT,	START_FINAL_GRADE, START_GRADER_PERMISSION_SETTINGS,
		STOP_GRADER_PERMISSION_SETTINGS,SWITCH_EDIT_ITEM, 
		SWITCH_GRADEBOOK, UNMASK_ITEM_TREE, UNMASK_MULTI_GRADE_GRID,
		UPDATE_LEARNER_GRADE_RECORD, UPDATE_GRADE_MAP, UPDATE_ITEM, USER_CHANGE, 
		REFRESH_GRADE_SCALE, SHOW_WEIGHTED, SHOW_FINAL_GRADE_SUBMISSION_STATUS,
		LAYOUT_ITEM_TREE_PANEL;
	
		private EventType eventType;
	
		private EventKey() {
			this.eventType = new EventType();
		}
		
		public EventType getEventType() {
			return eventType;
		}
		
	};
	
	private EventKey eventKey;
	
	public GradebookEvent(EventKey eventKey) {
		this.eventKey = eventKey;
	}
	
	public EventType getEventType() {
		return eventKey.getEventType();
	}
	
	public EventKey getEventKey() {
		return eventKey;
	}
}
