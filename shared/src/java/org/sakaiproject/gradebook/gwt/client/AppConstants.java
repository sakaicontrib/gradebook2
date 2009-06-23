package org.sakaiproject.gradebook.gwt.client;

public interface AppConstants {

	// Registry constants
	public static final String CURRENT 			= "current";
	public static final String ENABLE_POPUPS 	= "enableNotifications";
	public static final String I18N    			= "i18n";
	public static final String SERVICE 			= "service";
	
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
	public static final String TAB_HISTORY			= "tabHistory";
	public static final String TAB_GRADER_PER_SET 	= "tabGraderPermissionSettings";
		
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
	
	// Form panel id constants
	public static final String LEARNER_SUMMARY_FIELD_PREFIX		= "learnerField:";
	
	public static final int SCALE = 10;	
	
	public static final String IMPORT_CHANGES		= "importChangesMade";

	public static final String HELP_URL				= "helpUrl";
	public static final String HELP_URL_CONFIG_ID	= "gb2.help.url";
	
	public static final String DEFAULT_CATEGORY_NAME = "Unassigned";
	
	public static final String EXTRA_CREDIT_INDICATOR = " (+)";
	public static final String UNINCLUDED_INDICATOR = " (-)";
	
	public static final String gradePermission = "grade";
	public static final String viewPermission = "view";
	
	public static final int CATEGORY_TYPE_NO_CATEGORY = 1;
	public static final int CATEGORY_TYPE_ONLY_CATEGORY = 2;
	public static final int CATEGORY_TYPE_WEIGHTED_CATEGORY = 3;
}
