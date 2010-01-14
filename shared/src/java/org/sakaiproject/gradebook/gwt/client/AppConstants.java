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

package org.sakaiproject.gradebook.gwt.client;

public interface AppConstants {

	// Registry constants
	public static final String CURRENT 			= "current";
	public static final String ENABLE_POPUPS 	= "enableNotifications";
	public static final String I18N    			= "i18n";
	public static final String RESOURCES		= "resources";
	public static final String SERVICE 			= "service";
	public static final String VERSION			= "version";
	public static final String IS_ABLE_TO_GRADE = "canGrade";
	public static final String IS_ABLE_TO_EDIT 	= "canEdit";
	
	// Item type constants
	public static final String GRADEBOOK 	= "Gradebook";
	public static final String CATEGORY		= "Category";
	public static final String ITEM			= "Item";
	
	// Field value constants
	public static final String STATIC		= "Static";
	
	// Component id constants
	public static final String ID_ADD_CATEGORY_MENUITEM 	= "addCategoryMenuItem";
	public static final String ID_ADD_ITEM_MENUITEM 		= "addItemMenuItem";
	public static final String ID_ENABLE_POPUPS_MENUITEM	= "enablePopupsMenuItem";
	public static final String ID_GT_POINTS_MENUITEM		= "gradeTypePointsMenuItem";
	public static final String ID_GT_PERCENTAGES_MENUITEM	= "gradeTypePercentagesMenuItem";
	public static final String ID_RG_YES_MENUITEM			= "releaseGradesYesMenuItem";
	public static final String ID_RG_NO_MENUITEM			= "releaseGradesNoMenuItem";
	public static final String ID_CT_ADD_CATEGORY_MENUITEM  = "contextAddCategoryMenuItem";
	public static final String ID_CT_ADD_ITEM_MENUITEM  	= "contextAddItemMenuItem";
	public static final String ID_CT_DELETE_ITEM_MENUITEM  	= "contextDeleteItemMenuItem";
	public static final String ID_CT_EDIT_CATEGORY_MENUITEM = "contextEditCategoryMenuItem";
	public static final String ID_CT_EDIT_ITEM_MENUITEM  	= "contextEditItemMenuItem";
	public static final String ID_CT_MOVE_DOWN_MENUITEM 	= "contextMoveDownMenuItem";
	public static final String ID_CT_MOVE_UP_MENUITEM 		= "contextMoveUpMenuItem";
	public static final String ID_HD_ADD_CATEGORY_MENUITEM	= "headerAddCategoryMenuItem";
	public static final String ID_HD_ADD_ITEM_MENUITEM		= "headerAddItemMenuItem";
	public static final String ID_HD_EDIT_ITEM_MENUITEM		= "headerEditItemMenuItem";
	public static final String ID_HD_DELETE_ITEM_MENUITEM	= "headerDeleteItemMenuItem";
	public static final String ID_HD_GRADESCALE_MENUITEM	= "headerGradeScaleMenuItem";
	public static final String ID_HD_HIDE_ITEM_MENUITEM		= "headerHideItemMenuItem";
	public static final String ID_HD_HISTORY_MENUITEM		= "headerHistoryMenuItem";
	public static final String ID_CONFIRM_OK_BUTTON			= "confirmationOkButton";
	public static final String ID_CONFIRM_CANCEL_BUTTON		= "confirmationCancelButton";

	// Grid id constants
	public static final String MULTIGRADE 			= "multigrade";
	public static final String HISTORY				= "history";
	public static final String IMPORT				= "import";
	public static final String ITEMTREE				= "itemtree";
	
	// Tab id constants
	public static final String TAB_GRADES 			= "tabMultiGrade";
	public static final String TAB_GRADESCALE 		= "tabGradeScale";
	public static final String TAB_SETUP			= "tabSetup";
	public static final String TAB_HISTORY			= "tabHistory";
	public static final String TAB_GRADER_PER_SET 	= "tabGraderPermissionSettings";
	public static final String TAB_STATISTICS 		= "tabStatistics";
		
	public static final String WINDOW_MENU_ITEM_PREFIX = "windowMenuItem:";
	
	// Tab mode cookie id
	public static final String TAB_MODE				= ":tm";
	public static final String SELECTED_COLUMNS		= "selMCols";
	public static final String SORT_FIELD			= ":sf";
	public static final String SORT_DIRECTION		= ":sd";
	public static final String COLUMN_PREFIX		= "c:";
	public static final String HIDDEN_SUFFIX		= ":h";
	public static final String WIDTH_SUFFIX			= ":w";
	public static final String PAGE_SIZE			= ":ps";
	
	// Data type constants
	public static final String NUMERIC_DATA_TYPE		= "Number";
	public static final String STRING_DATA_TYPE			= "String";
	
	// Form panel id constants
	public static final String LEARNER_SUMMARY_FIELD_PREFIX		= "learnerField:";
	public static final String LEARNER_ATTRIBUTE_TREE = "learnerAttributeTree";
	
	public static final int SCALE = 10;
	public static final int DISPLAY_SCALE = 2;
	
	public static final String IMPORT_CHANGES		= "importChangesMade";

	public static final String HELP_URL				= "helpUrl";
	public static final String HELP_URL_CONFIG_ID	= "gb2.help.url";
	
	public static final String ENABLED_GRADE_TYPES 	= "enabledGradeTypes";
	public static final String ENABLED_GRADE_TYPES_ID	= "gb2.enabled.grade.types";
	
	public static final String LEARNER_ROLE_NAMES	= "gb2.gradable.role.names";
	
	public static final String DEFAULT_CATEGORY_NAME = "Unassigned";
	
	public static final String EXTRA_CREDIT_INDICATOR = " (+)";
	public static final String UNINCLUDED_INDICATOR = " (-)";
	public static final String COMMENTS_INDICATOR = "Comment : ";
	public static final String COMMENT_SUFFIX = "::C";
	
	public static final String gradePermission = "grade";
	public static final String viewPermission = "view";
	
	public static final int CATEGORY_TYPE_NO_CATEGORY = 1;
	public static final int CATEGORY_TYPE_ONLY_CATEGORY = 2;
	public static final int CATEGORY_TYPE_WEIGHTED_CATEGORY = 3;
	
	public static final String ITEMTREE_HEADER = "itemTreeHeader";
	public static final String ITEMTREE_POINTS_WEIGHTS = "**ptsW";
	public static final String ITEMTREE_POINTS_NOWEIGHTS = "**ptsNoW";
	public static final String ITEMTREE_PERCENT_GRADE = "**pctG";
	public static final String ITEMTREE_PERCENT_CATEGORY = "**pctC";
	
	
	public static final String REST_FRAGMENT 		= "rest";
	public static final String LEARNER_FRAGMENT 	= "learner";
	public static final String CONFIG_FRAGMENT 		= "config";
	public static final String APPLICATION_FRAGMENT = "application";
	public static final String ITEM_FRAGMENT		= "item";
	public static final String ROSTER_FRAGMENT 		= "roster";
	public static final String SECTION_FRAGMENT		= "section";
	public static final String GRADE_EVENT_FRAGMENT	= "gradeevent";
	
}
