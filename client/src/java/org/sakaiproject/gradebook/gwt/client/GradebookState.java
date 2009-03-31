package org.sakaiproject.gradebook.gwt.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;

import com.extjs.gxt.ui.client.state.StateManager;

public class GradebookState {

	public static List<String> getSelectedMultigradeColumns(String gradebookUid) {
		String selectedMultigradeColumnId = DataTypeConversionUtil.concat(gradebookUid, ":", AppConstants.SELECTED_COLUMNS);
		String value = StateManager.get().getString(selectedMultigradeColumnId);
		
		List<String> columnIds = new ArrayList<String>();
		
		if (value != null) {
			String[] tokens = value.split(":");
			for (int i=0;i<tokens.length;i++) {
				columnIds.add(tokens[i]);
			}
		}
		
		return columnIds;
	}
	
	public static void setSelectedMultigradeColumns(String gradebookUid, Set<String> visibleStaticIdSet, List<ItemModel> selectedColumns) {
		String selectedMultigradeColumnId = DataTypeConversionUtil.concat(gradebookUid, ":", AppConstants.SELECTED_COLUMNS);
		
		StringBuilder builder = new StringBuilder();
		if (selectedColumns != null) {
			for (ItemModel item : selectedColumns) {
				if (item.getItemType() == Type.ITEM)
					builder.append(item.getIdentifier()).append(":");
			}
		}
		
		for (Iterator<String> it = visibleStaticIdSet.iterator();it.hasNext();) {
			builder.append(it.next()).append(":");
		}
		
		StateManager.get().set(selectedMultigradeColumnId, builder.toString());
	}
	
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
		String stateId = DataTypeConversionUtil.concat(gradebookUid, ":", gridId, ":", identifier, AppConstants.HIDDEN_SUFFIX);
		Boolean hidden = (Boolean)StateManager.get().get(stateId);
		boolean isHidden = defaultState;
		if (hidden != null) {
			isHidden = DataTypeConversionUtil.checkBoolean(hidden);
		}
		return isHidden;
	}
	
	public static void setColumnHidden(String gradebookUid, String gridId, String identifier, boolean hidden) {
		String stateId = DataTypeConversionUtil.concat(gradebookUid, ":", gridId, ":", identifier, AppConstants.HIDDEN_SUFFIX);
		StateManager.get().set(stateId, Boolean.valueOf(hidden));
	}
	
	public static int getColumnWidth(String gradebookUid, String gridId, String identifier, String name) {
		String stateId = DataTypeConversionUtil.concat(gradebookUid, ":", gridId, ":", identifier, AppConstants.WIDTH_SUFFIX);
		int columnWidth = 200;
	
		Integer storedWidth = (Integer)StateManager.get().get(stateId);
		if (storedWidth != null) {
			columnWidth = storedWidth.intValue();
		} else {
			// Otherwise calculate the column width based on the length of the name
			if (name != null)
				columnWidth = name.length() * 5 + 20;
		}
		
		if (columnWidth < 100)
			columnWidth = 100;
		
		return columnWidth;
	}
	
	public static void setColumnWidth(String gradebookUid, String gridId, String identifier, int width) {
		String stateId = DataTypeConversionUtil.concat(gradebookUid, ":", gridId, ":", identifier, AppConstants.WIDTH_SUFFIX);
		StateManager.get().set(stateId, Integer.valueOf(width));
	}
	
}
