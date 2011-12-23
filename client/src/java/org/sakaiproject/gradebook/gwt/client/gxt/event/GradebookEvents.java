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

import java.util.HashMap;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvent.EventKey;

import com.extjs.gxt.ui.client.event.EventType;


public class GradebookEvents {

	private static Map<EventType, GradebookEvent> eventMap = new HashMap<EventType, GradebookEvent>();
	
	protected GradebookEvents() {

	}
	
	public static GradebookEvent registerEvent(EventKey eventKey) {
		GradebookEvent gradebookEvent = new GradebookEvent(eventKey);

		eventMap.put(eventKey.getEventType(), gradebookEvent);
		return gradebookEvent;
	}
	
	public static GradebookEvent getEvent(EventType eventType) {
		GradebookEvent gradebookEvent = eventMap.get(eventType);
	
		if (gradebookEvent == null)
			return None;
		
		return gradebookEvent;
	}
	
	
	public static final GradebookEvent BeginItemUpdates				= registerEvent(EventKey.BEGIN_ITEM_UPDATES);
	public static final GradebookEvent BrowseLearner 				= registerEvent(EventKey.BROWSE_LEARNER);
	public static final GradebookEvent Configuration				= registerEvent(EventKey.CONFIGURATION);
	public static final GradebookEvent Confirmation					= registerEvent(EventKey.CONFIRMATION);
	public static final GradebookEvent ConfirmDeleteItem			= registerEvent(EventKey.CONFIRM_DELETE_ITEM);
	public static final GradebookEvent ClearSearch 					= registerEvent(EventKey.CLEAR_SEARCH);
	public static final GradebookEvent CloseNotification			= registerEvent(EventKey.CLOSE_NOTIFICATION);
	public static final GradebookEvent CreateItem					= registerEvent(EventKey.CREATE_ITEM);
	public static final GradebookEvent CreatePermission				= registerEvent(EventKey.CREATE_PERMISSION);
	public static final GradebookEvent DeleteGradeMap				= registerEvent(EventKey.DELETE_GRADE_MAP);
	public static final GradebookEvent DeleteItem					= registerEvent(EventKey.DELETE_ITEM);
	public static final GradebookEvent DeletePermission				= registerEvent(EventKey.DELETE_PERMISSION);
	public static final GradebookEvent DoSearch 					= registerEvent(EventKey.DO_SEARCH);
	public static final GradebookEvent EndItemUpdates				= registerEvent(EventKey.END_ITEM_UPDATES);
	public static final GradebookEvent Exception 					= registerEvent(EventKey.EXCEPTION);
	public static final GradebookEvent ExpandEastPanel 				= registerEvent(EventKey.EXPAND_EAST_PANEL);
	public static final GradebookEvent GradebookChange 				= registerEvent(EventKey.GRADEBOOK_CHANGE);
	public static final GradebookEvent ShowWeighted 				= registerEvent(EventKey.SHOW_WEIGHTED);	
	public static final GradebookEvent GradeTypeUpdated				= registerEvent(EventKey.GRADE_TYPE_UPDATED);
	public static final GradebookEvent HideColumn					= registerEvent(EventKey.HIDE_COLUMN);
	public static final GradebookEvent HideEastPanel				= registerEvent(EventKey.HIDE_EAST_PANEL);
	public static final GradebookEvent HideFormPanel				= registerEvent(EventKey.HIDE_FORM_PANEL);
	public static final GradebookEvent ItemCreated					= registerEvent(EventKey.ITEM_CREATED);
	public static final GradebookEvent ItemDeleted					= registerEvent(EventKey.ITEM_DELETED);
	public static final GradebookEvent ItemUpdated					= registerEvent(EventKey.ITEM_UPDATED);
	public static final GradebookEvent FailedToUpdateItem			= registerEvent(EventKey.FAILED_TO_UPDATE_ITEM);
	public static final GradebookEvent LearnerGradeRecordUpdated	= registerEvent(EventKey.LEARNER_GRADE_RECORD_UPDATED); 
	public static final GradebookEvent Load				 			= registerEvent(EventKey.LOAD);
	public static final GradebookEvent MaskItemTree					= registerEvent(EventKey.MASK_ITEM_TREE);
	public static final GradebookEvent NewCategory					= registerEvent(EventKey.NEW_CATEGORY);
	public static final GradebookEvent NewItem						= registerEvent(EventKey.NEW_ITEM);
	public static final GradebookEvent None							= registerEvent(EventKey.NONE);
	public static final GradebookEvent Notification					= registerEvent(EventKey.NOTIFICATION);
	public static final GradebookEvent PermissionCreated			= registerEvent(EventKey.PERMISSION_CREATED);
	public static final GradebookEvent PermissionDeleted			= registerEvent(EventKey.PERMISSION_DELETED);
	public static final GradebookEvent Refresh 						= registerEvent(EventKey.REFRESH);
	public static final GradebookEvent RefreshCourseGrades 			= registerEvent(EventKey.REFRESH_COURSE_GRADES);
	public static final GradebookEvent RefreshGradebookItems 		= registerEvent(EventKey.REFRESH_GRADEBOOK_ITEMS);
	public static final GradebookEvent RefreshGradebookSetup 		= registerEvent(EventKey.REFRESH_GRADEBOOK_SETUP);
	public static final GradebookEvent RefreshGradeScale	 		= registerEvent(EventKey.REFRESH_GRADE_SCALE);
	public static final GradebookEvent RevertItem					= registerEvent(EventKey.REVERT_ITEM);
	public static final GradebookEvent SelectDeleteItem				= registerEvent(EventKey.SELECT_DELETE_ITEM);
	public static final GradebookEvent SelectItem					= registerEvent(EventKey.SELECT_ITEM);
	public static final GradebookEvent SelectLearner				= registerEvent(EventKey.SELECT_LEARNER);
	public static final GradebookEvent ShowColumns					= registerEvent(EventKey.SHOW_COLUMNS);
	public static final GradebookEvent ShowGradeScale				= registerEvent(EventKey.SHOW_GRADE_SCALE);
	public static final GradebookEvent ShowHistory					= registerEvent(EventKey.SHOW_HISTORY);
	public static final GradebookEvent ShowStatistics				= registerEvent(EventKey.SHOW_STATISTICS);
	public static final GradebookEvent StopStatistics				= registerEvent(EventKey.STOP_STATISTICS);
	public static final GradebookEvent SingleGrade					= registerEvent(EventKey.SINGLE_GRADE);
	public static final GradebookEvent SingleView 					= registerEvent(EventKey.SINGLE_VIEW);
	public static final GradebookEvent StartEditItem				= registerEvent(EventKey.START_EDIT_ITEM);
	public static final GradebookEvent StartExport					= registerEvent(EventKey.START_EXPORT);
	public static final GradebookEvent StartImport					= registerEvent(EventKey.START_IMPORT);
	public static final GradebookEvent Startup 						= registerEvent(EventKey.STARTUP);
	public static final GradebookEvent StopImport					= registerEvent(EventKey.STOP_IMPORT);
	public static final GradebookEvent StartGraderPermissionSettings = registerEvent(EventKey.START_GRADER_PERMISSION_SETTINGS);
	public static final GradebookEvent StopGraderPermissionSettings = registerEvent(EventKey.STOP_GRADER_PERMISSION_SETTINGS);
	public static final GradebookEvent StartFinalgrade				= registerEvent(EventKey.START_FINAL_GRADE);
	public static final GradebookEvent SwitchEditItem				= registerEvent(EventKey.SWITCH_EDIT_ITEM);
	public static final GradebookEvent SwitchGradebook 				= registerEvent(EventKey.SWITCH_GRADEBOOK);
	public static final GradebookEvent UnmaskItemTree				= registerEvent(EventKey.UNMASK_ITEM_TREE);
	public static final GradebookEvent UpdateLearnerGradeRecord		= registerEvent(EventKey.UPDATE_LEARNER_GRADE_RECORD);
	public static final GradebookEvent UpdateGradeMap				= registerEvent(EventKey.UPDATE_GRADE_MAP);
	public static final GradebookEvent UpdateItem					= registerEvent(EventKey.UPDATE_ITEM);
	public static final GradebookEvent UserChange 					= registerEvent(EventKey.USER_CHANGE);
	public static final GradebookEvent FinishTreeItemDragAndDrop	= registerEvent(EventKey.FINISH_TREE_ITEM_DRAG_AND_DROP);
	public static final GradebookEvent ShowUserFeedback				= registerEvent(EventKey.SHOW_USER_FEEDBACK);
	public static final GradebookEvent HideUserFeedback				= registerEvent(EventKey.HIDE_USER_FEEDBACK);
	public static final GradebookEvent GradeScaleUpdateError		= registerEvent(EventKey.GRADE_SCALE_UPDATE_ERROR);
	public static final GradebookEvent MaskMultiGradeGrid			= registerEvent(EventKey.MASK_MULTI_GRADE_GRID);
	public static final GradebookEvent UnmaskMultiGradeGrid			= registerEvent(EventKey.UNMASK_MULTI_GRADE_GRID);
	public static final GradebookEvent ShowFinalGradeSubmissionStatus = registerEvent(EventKey.SHOW_FINAL_GRADE_SUBMISSION_STATUS);
	public static final GradebookEvent LayoutItemTreePanel			= registerEvent(EventKey.LAYOUT_ITEM_TREE_PANEL);
}
