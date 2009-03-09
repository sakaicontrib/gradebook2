package org.sakaiproject.gradebook.gwt.client;

import org.sakaiproject.gradebook.gwt.client.model.StudentModel;

import com.extjs.gxt.ui.client.state.StateManager;

public class GradebookState {

	public static boolean getTabMode(String gradebookUid) {
		String tabModeStateId = DataTypeConversionUtil.concat(gradebookUid, AppConstants.TAB_MODE);
		Boolean isChecked = (Boolean)StateManager.get().get(tabModeStateId);
		return DataTypeConversionUtil.checkBoolean(isChecked);
	}
	
	public static void setTabMode(String gradebookUid, boolean tabMode) {
		String tabModeStateId = DataTypeConversionUtil.concat(gradebookUid, AppConstants.TAB_MODE);
		StateManager.get().set(tabModeStateId, Boolean.valueOf(tabMode));
	}
	
	public static void setSortInfo(String gradebookUid, String gridId, String sortField, boolean isAscending) {
		String sortFieldId = DataTypeConversionUtil.concat(gradebookUid, ":", gridId, AppConstants.SORT_FIELD);
		String sortDirectionId = DataTypeConversionUtil.concat(gradebookUid, ":", gridId, AppConstants.SORT_DIRECTION);
		StateManager.get().set(sortFieldId, sortField);
		StateManager.get().set(sortDirectionId, Boolean.valueOf(isAscending));
	}
	
	public static String getSortField(String gradebookUid, String gridId) {
		String sortFieldId = DataTypeConversionUtil.concat(gradebookUid, ":", gridId, AppConstants.SORT_FIELD);
		return StateManager.get().getString(sortFieldId);
	}
	
	public static boolean isAscending(String gradebookUid, String gridId) {
		String sortDirectionId = DataTypeConversionUtil.concat(gradebookUid, ":", gridId, AppConstants.SORT_DIRECTION);
		return DataTypeConversionUtil.checkBoolean((Boolean)StateManager.get().get(sortDirectionId));
	}
	
	public static boolean isColumnHidden(String gradebookUid, String gridId, String identifier, boolean defaultState) {
		String stateId = DataTypeConversionUtil.concat(gradebookUid, ":", gridId, ":", identifier);
		Boolean hidden = (Boolean)StateManager.get().get(stateId);
		boolean isHidden = defaultState;
		if (hidden != null) {
			isHidden = DataTypeConversionUtil.checkBoolean(hidden);
		}
		return isHidden;
	}
	
	public static int getColumnWidth(String gradebookUid, String gridId, String identifier, String name) {
		String stateId = DataTypeConversionUtil.concat(gradebookUid, ":", gridId, ":", identifier);
		int columnWidth = 200;
	
		Integer storedWidth = (Integer)StateManager.get().get(stateId);
		if (storedWidth != null) {
			columnWidth = storedWidth.intValue();
		} else {
			// Otherwise calculate the column width based on the length of the name
			if (name != null)
				columnWidth = name.length() * 10 + 20;
		}
		
		if (columnWidth < 100)
			columnWidth = 100;
		
		return columnWidth;
	}
	
	
}
