/**********************************************************************************
 *
 * Copyright (c) 2008, 2009, 2010, 2011 The Regents of the University of California
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

	public static final char BOOLEAN_PREFIX			= 'B';
	public static final char STRING_PREFIX 			= 'S';
	public static final char DOUBLE_PREFIX 			= 'D';
	public static final char INTEGER_PREFIX 		= 'I';
	public static final char DATE_PREFIX 			= 'T';
	public static final char ODD_DATE_PREFIX 		= 'W';
	public static final char LONG_PREFIX 			= 'L';
	public static final char M_ARRAY_PREFIX			= 'A';
	public static final char O_ARRAY_PREFIX			= 'V';
	public static final char OBJECT_PREFIX			= 'O';
	public static final char MODEL_PREFIX			= 'M';
	public static final char GRADETYPE_PREFIX		= 'G';
	public static final char CATEGORYTYPE_PREFIX	= 'C';
	
	// Registry constants
	public static final String CURRENT 			= "current";
	public static final String ENABLE_POPUPS 	= "enableNotifications";
	public static final String I18N    			= "i18n";
	public static final String I18N_TEMPLATES	= "i18nTmpl";
	public static final String RESOURCES		= "resources";
	public static final String SERVICE 			= "service";
	public static final String VERSION			= "version";
	public static final String IS_ABLE_TO_GRADE = "canGrade";
	public static final String IS_ABLE_TO_EDIT 	= "canEdit";
	public static final String IS_NEW_GRADEBOOK	= "newGradebook";
	public static final String HAS_CONTROLLERS	= "hasControllers";
	public static final String APP_MODEL		= "appModel";
	public static final String WIDGET_INJECTOR = "_widgetInjector"; 
	
	// Item type constants
	public static final String GRADEBOOK 	= "Gradebook";
	public static final String CATEGORY		= "Category";
	public static final String ITEM			= "Item";
	
	// Field value constants
	public static final String STATIC		= "Static";
	
	// Component id constants
	public static final String ID_ADD_CATEGORY_MENUITEM 	= "addCategoryMenuItem";
	public static final String ID_ADD_ITEM_MENUITEM 		= "addItemMenuItem";
	public static final String ID_CT_EDIT_GRADEBOOK_MENUITEM = "contextEditGradebookMenuItem";
	public static final String ID_CT_ADD_CATEGORY_MENUITEM  = "contextAddCategoryMenuItem";
	public static final String ID_CT_ADD_ITEM_MENUITEM  	= "contextAddItemMenuItem";
	public static final String ID_CT_DELETE_ITEM_MENUITEM  	= "contextDeleteItemMenuItem";
	public static final String ID_CT_EDIT_CATEGORY_MENUITEM = "contextEditCategoryMenuItem";
	public static final String ID_CT_EDIT_ITEM_MENUITEM  	= "contextEditItemMenuItem";
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
	
	public static final String IMPORT_CHANGES		= "B_IMP_CHG";

	// Sakai properties
	public static final String HELP_URL				= "helpUrl";
	public static final String HELP_URL_CONFIG_ID	= "gb2.help.url";
	
	public static final String ENABLED_GRADE_TYPES 	= "enabledGradeTypes";
	public static final String ENABLED_GRADE_TYPES_ID	= "gb2.enabled.grade.types";
	
	public static final String LEARNER_ROLE_NAMES	= "gb2.gradable.role.names";
	
	public static final String SHOW_WEIGHTED_ENABLED	= "showWeightedEnabled";
	public static final String ENABLE_SHOW_WEIGHTED_TOGGLE = "gb2.show.weighted.enabled";
	
	public static final String ENABLE_SCALED_EC	= "gb2.enable.scaled.extra.credit";
	public static final String LIMIT_SCALED_EC	= "gb2.limit.scaled.extra.credit";
	
	public static final String IMPORT_DELETE_MISSING_GRADES = "gb2.import.delete.missing.grades";
	
	public static final String ENABLED_SECURITY_CHECKS = "gb2.security.enabled";
	public static final String SECURITY_CHECK_CONTEXT_PREFIX_PROPNAME = "portalPath";
	public static final String SECURITY_CHECK_CONTEXT_PREFIX_DEFAULT = "/portal/";
	
	public static final String ENABLED_SEARCH_ROSTER_BY_FIELD = "gb2.enable.search.roster.by.field";
	
	public static final String ENABLE_FINAL_GRADE_SUBMISSION_CHECK = "gb2.enable.final.grade.submission.check";
	
	public static final String ENABLE_FINAL_GRADE_SUBMISSION = "gb2.enable.final.grade.submission";
	
	/*
	 * GRBK-1210: The default cache TTL/TTI need to match with what's defined
	 * in rest.xml -> org.sakaiproject.gradebook.gwt.sakai.rest.resource.cache
	 */
	public static final String ENABLE_STATISTICS_CACHE = "gb2.enable.statistics.cache";
	public static final String STATISTICS_CACHE_TIME_TO_IDLE_SECONDS = "gb2.statistics.cache.time.to.idle.seconds";
	public static final String STATISTICS_CACHE_TIME_TO_LIVE_SECONDS = "gb2.statistics.cache.time.to.live.seconds";
	public static final int STATISTICS_CACHE_TIME_TO_IDLE_SECONDS_DEFAULT = 30;
	public static final int STATISTICS_CACHE_TIME_TO_LIVE_SECONDS_DEFAULT = 30;
	public static final int CURRENT_STATISTICS_DATA = -1;
	
	public static final String DEFAULT_CATEGORY_NAME = "Unassigned";
	
	public static final String EXTRA_CREDIT_INDICATOR = " (+)";
	public static final String UNINCLUDED_INDICATOR = " (-)";
	public static final String RELEASE_SCORES_INDICATOR = " (R)";
	public static final String GIVE_UNGRADED_NO_CREDIT_INDICATOR = " (0)";
	public static final String COMMENTS_INDICATOR = "Comment : ";
	public static final String COMMENT_SUFFIX = "::C";
	
	public static final String EXPORT_SKIPCOLUMN_INDICATOR = "++SKIPCOLUMN++";
	
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
	
	
	public static final String REST_FRAGMENT 				= "rest";
	public static final String LEARNER_FRAGMENT 			= "learner";
	public static final String CONFIG_FRAGMENT 				= "config";
	public static final String APPLICATION_FRAGMENT 		= "application";
	public static final String ITEM_FRAGMENT				= "item";
	public static final String ITEMS_FRAGMENT				= "items";
	public static final String ROSTER_FRAGMENT 				= "roster";
	public static final String SECTION_FRAGMENT				= "sections";
	public static final String GRADE_EVENT_FRAGMENT			= "gradeevent";
	public static final String GRADE_FORMAT_FRAGMENT		= "gradeformat";
	public static final String GRADE_MAP_FRAGMENT			= "grademap";
	public static final String HISTORY_FRAGMENT				= "history";
	public static final String GRADER_FRAGMENT				= "grader";
	public static final String PERMISSION_FRAGMENT			= "permission";
	public static final String PERMISSIONS_FRAGMENT			= "permissions";
	public static final String STATISTICS_FRAGMENT			= "statistics";
	public static final String GRADES_VERIFICATION_FRAGMENT	= "verification";
	public static final String UPLOAD_FRAGMENT				= "upload";
	public static final String OLD_UPLOAD_FRAGMENT			= "oldupload";
	public static final String AUTHORIZATION_FRAGMENT		= "authorization";
	public static final String INSTRUCTOR_FRAGMENT			= "instructor";
	public static final String OVERWRITE_FRAGMENT 			= "overwrite";
	public static final String MAXPNTS_FRAGMENT 			= "maxpnts";
	public static final String STUDENT_FRAGMENT 			= "student";
	public static final String COURSE_FRAGMENT				= "course";
	public static final String STARTUP_FRAGMENT				= "startup";
	public static final String EXCUSE_FRAGMENT				= "excuse";
	public static final String NUMERIC_FRAGMENT				= "numeric";
	public static final String STRING_FRAGMENT				= "string";
	public static final String COMMENT_FRAGMENT				= "comment";
	public static final String FINAL_GRADES_SUB_FRAGMENT	= "fgs";
	
	
	public static final String LIST_ROOT				= "list";
	public static final String TOTAL					= "total";
	
	public static final String VALUE_CONSTANT			= "value";
	public static final String START_VALUE_CONSTANT		= "previousValue";
	
	public static final String STR_VALUE_CONSTANT		= "stringValue";
	public static final String STR_START_VALUE_CONSTANT	= "previousStringValue";
	public static final String BOOL_VALUE_CONSTANT			= "booleanValue";
	public static final String BOOL_START_VALUE_CONSTANT	= "previousBooleanValue";
	
	public static final String COMMENTED_FLAG = "B_C:";
	public static final String COMMENT_TEXT_FLAG = "S_T:";
	public static final String DROP_FLAG = "B_D:";
	public static final String EXCUSE_FLAG = "B_E:";
	public static final String FAILED_FLAG = "S_F:";
	public static final String SUCCESS_FLAG = "S_S:";
	public static final String CONVERTED_FLAG = "S_C:";
	public static final String CONVERTED_GRADE = "S_G:";
	
	public static final String APP_COOKIE_NAME = "gb2application";
	public static final String AUTH_COOKIE_NAME = "gb2placement";
	public static final String VERSION_COOKIE_NAME = "gb2version";
	
	public static final String IMPORT_SERVLET = "import";
	public static final String EXPORT_SERVLET = "export";
	public static final String SUBMISSION_SERVLET = "submit";
	
	// Sections
	public static final String ALL = "ALL";
	public static final String ALL_DISPLAY = "All";
	
	// Statistics panel
	public static final int POSITIVE_NUMBER = 0;
	public static final int NEGATIVE_NUMBER = 1;
	public final static int CHART_WIDTH = 600;
	public final static int CHART_HEIGHT = 300;
	public final static boolean IS_CHART_3D = true;
	
	// Statistics Data
	public static final String STATISTICS_DATA_NA = "-";
	
	// Import/Export
	public static final String NEW_PREFIX = "NEW:";
	public static final String NEW_CAT_PREFIX = "NEW:CAT:";
	public static final String REQUEST_FORM_FIELD_GBUID = "gradebookUid";
	public static final String REQUEST_FORM_FIELD_FORM_TOKEN = "form-token";
	
	// Hosted Mode
	public static final String SYSTEM_PROPERTY_KEY_MODE = "gb2.mode";
	public static final String SYSTEM_PROPERTY_VALUE_HOSTED = "hosted";
	
	// Component Service
	public static final long ONEHOUR = 1000l * 60l * 60l;
	

	// REST Communication
	public static final String X_XSRF_COOKIE = "X-XSRF-Cookie";
	//public static final String SESSION_COOKIE_KEY = "JSESSIONID";
	public static final String GB2_TOKEN = "GB2TOKEN";
	public static final String NO_VALUE_COOKIE = "No-Cookie";
	public static final String HEADER_CONTENT_TYPE_JSON_UTF8 = "application/json; charset=utf-8";
	public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";

	
	// DEV
	public static final String TEST_SITE_CONTEXT_ID = "TESTSITECONTEXT";
	public static final String TEST_SITE_ID = "/site/TESTSITECONTEXT";
	
	// AUTHZ
	public static final String PERMISSION_GRADE_ALL = "gradebook.gradeAll";
	public static final String PERMISSION_GRADE_SECTION = "gradebook.gradeSection";
	public static final String PERMISSION_EDIT_ASSIGNMENTS = "gradebook.editAssignments";
	public static final String PERMISSION_VIEW_OWN_GRADES = "gradebook.viewOwnGrades";
	
	// HISTORY
	public static final String HISTORY_SCORE = "score";
	public static final String HISTORY_OLD_SCORE = "oldScore";
	public static final String HISTORY_ORIGINAL_SCORE = "origScore";
	
	// DATE FORMATS
	public static final String LONG_DATE = "MM/dd/yyyy HH:mm:ss ZZZ";
	public static final String SHORT_DATE = "yyyy-MM-dd";
	public static final String INPUT_DATE = "MM/dd/yyyy";
	
	// USER FIELDS
	public static final String USER_FIELD_SORT_NAME = "sortName";
	public static final String USER_FIELD_EMAIL = "email";
	public static final String USER_FIELD_DISPLAY_NAME = "displayName";
	public static final String USER_FIELD_LAST_NAME_FIRST = "lastNameFirst";
	public static final String USER_FIELD_DISPLAY_ID = "displayId";
	// GRBK-943 - We present an alert message if the number of GB rows is bigger than...
	public static final int ITEM_MANIP_PERFORMANCE_TRIGGER = 24;
	public static final String LOADINGPANELID = "loading";
	
	// export fields (GRBK-893)
	
	public static final String INCLUDE_COMMENTS_FIELD = "include_comments";
	public static final String SECTIONS_FIELD = "sections";
	public static final String ACTUAL_SCORE_SUFFIX = "-OG";
	
	public static final String EXPORT_DATA_FIELD = "edf";
	public static final String EXPORT_DATA_COMMENTS = "edc";
	public static final String EXPORT_DATA_SECTIONS = "eds";
	public static final String EXPORT_DATA_TYPE = "edt";
	public static final String EXPORT_DATA_STRUCTURE = "edst";
	
	public static final String FILE_TYPE_CSV = "csv";
	public static final String FILE_TYPE_XLS = "xls97";
	public static final String FILE_TYPE_XLSX = "xlsx";
	public static final String FILE_TYPE_TEMPLATE = "template";

	// GRBK-514
	// true == import only structure
	public static final String IMPORT_PARAM_STRUCTURE = "juststruct";
	public static final String IMPORT_PARAM_FILETYPE = "filetype";
	public static final String IMPORT_PARAM_FILEFORMAT = "fileformat";
	
}
