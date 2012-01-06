/**********************************************************************************
 *
 * Copyright (c) 2008, 2009, 2010, 2011, 2012 The Regents of the University of California
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

package org.sakaiproject.gradebook.gwt.client.gxt.custom.widget.grid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaMenuItem;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ConfigurationModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.FixedColumnModel;
import org.sakaiproject.gradebook.gwt.client.model.Configuration;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumn;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;
import org.sakaiproject.gradebook.gwt.client.model.type.GroupType;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.util.DelayedTask;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;

public abstract class CustomGridView extends BaseCustomGridView {

	private enum SelectionType { SORT_ASC, SORT_DESC, HISTORY, START_GRADER_PERMISSION_SETTINGS, STATISTICS };

	private static final String selectionTypeField = "selectionType";

	private String gridId;
	
	private DelayedTask[] syncTask;

	private SelectionListener<MenuEvent> selectionListener;
	
	public CustomGridView(String gridId) {
		this.gridId = gridId;

		selectionListener = new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent me) {
				MenuItem item = (MenuItem)me.getItem();
				if (item != null) {
					SelectionType selectionType = item.getData(selectionTypeField);
					if (selectionType != null) {
						Integer colIndexInteger = item.getData("colIndex");
						int colIndex = colIndexInteger == null ? -1 : colIndexInteger.intValue();
						switch (selectionType) {
							case START_GRADER_PERMISSION_SETTINGS:
								Dispatcher.forwardEvent(GradebookEvents.StartGraderPermissionSettings.getEventType(), Boolean.TRUE);
								break;
							case HISTORY:
								Dispatcher.forwardEvent(GradebookEvents.ShowHistory.getEventType(), cm.getDataIndex(colIndex));
								break;
							case STATISTICS:
								Dispatcher.forwardEvent(GradebookEvents.ShowStatistics.getEventType(), cm.getDataIndex(colIndex));
								break;
							case SORT_ASC:
								ds.sort(cm.getDataIndex(colIndex), SortDir.ASC);
								break;
							case SORT_DESC:
								ds.sort(cm.getDataIndex(colIndex), SortDir.DESC);
								break;
						}
					}
				}
			}
		};
	}
	
	public void doRowRefresh(int row) {
		refreshRow(row);
	}

	/* (non-Javadoc)
	 * @see com.extjs.gxt.ui.client.widget.grid.GridView#createContextMenu(int)
	 */
	@Override
	protected Menu createContextMenu(int colIndex) {
		I18nConstants i18n = Registry.get(AppConstants.I18N);

		Menu menu = new Menu();

		MenuItem item = null;
		if (cm.isSortable(colIndex)) {
			item = new AriaMenuItem();
			item.setData(selectionTypeField, SelectionType.SORT_ASC);
			item.setData("colIndex", Integer.valueOf(colIndex));
			item.setText(i18n.headerSortAscending());
			item.setTitle(i18n.headerSortAscendingTitle());
			item.setIconStyle("my-icon-asc");
			item.addSelectionListener(selectionListener);
			menu.add(item);

			item = new AriaMenuItem();
			item.setData(selectionTypeField, SelectionType.SORT_DESC);
			item.setData("colIndex", Integer.valueOf(colIndex));
			item.setText(i18n.headerSortDescending());
			item.setTitle(i18n.headerSortDescendingTitle());
			item.setIconStyle("my-icon-desc");
			item.addSelectionListener(selectionListener);
			menu.add(item);
		}

		return menu;
	}

	@Override
	protected void onDataChanged(StoreEvent se) {
		super.onDataChanged(se);
	}

	// Helper method
	protected void showAllColumns(Menu categoryMenu, List<FixedColumnModel> columns, boolean show) {

		List<Component> menuItems = categoryMenu.getItems();
		// Determine the number of check menu items, accounting for any regular menu item such as show/hide all, etc.
		int menuIndex = categoryMenu.getItemCount() - columns.size();

		for (FixedColumn column : columns) {

			int columnIndex = cm.getIndexById(column.getIdentifier());

			// Logic to NOT hide the last column
			if(show && 1 == cm.getColumnCount(true)) {
				continue;
			}

			Component item = menuItems.get(menuIndex);

			if(item instanceof CheckMenuItem) {

				CheckMenuItem checkMenuItem = (CheckMenuItem) item;
				checkMenuItem.setChecked(!show);

			}

			cm.setHidden(columnIndex, show);

			menuIndex++;
		}
	}

	protected void updateColumnWidth(int col, final int width) {
		super.updateColumnWidth(col, width);

		ColumnConfig column = cm.getColumn(col);
		final String columnId = column == null ? null : column.getId();

		if (syncTask == null) 
			syncTask = new DelayedTask[cm.getColumnCount()];
		else if (syncTask.length < cm.getColumnCount()) {
			DelayedTask[] temp = syncTask;
			syncTask = new DelayedTask[cm.getColumnCount()];
			for (int i=0;i<temp.length;i++) {
				if (temp[i] != null) {
					syncTask[i] = temp[i];
				}
			}
		}
			
		if (syncTask[col] != null)
			syncTask[col].cancel();
	
		syncTask[col] = new DelayedTask(new Listener<BaseEvent>() {
		
			public void handleEvent(BaseEvent be) {
				Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
				Configuration model = new ConfigurationModel(selectedGradebook.getGradebookId());
				model.setColumnWidth(gridId, columnId, Integer.valueOf(width));

				Dispatcher.forwardEvent(GradebookEvents.Configuration.getEventType(), model);
			}
		});

		syncTask[col].delay(1000);
	}

	public static class ColumnGroup {

		private GroupType group;
		private ArrayList<FixedColumnModel> columns;
		Map<Long, ArrayList<FixedColumnModel>> categoryColumnMap;

		public ColumnGroup(GroupType group) {
			this.group = group;
			this.columns = new ArrayList<FixedColumnModel>();
		}

		public List<FixedColumnModel> getColumns() {
			return columns;
		}

		public Map<Long, ArrayList<FixedColumnModel>> getCategoryColumnMap() {
			return categoryColumnMap;
		}

		public void addColumn(FixedColumnModel column) {
			// If there is a category, then use it
			if (column.getCategoryId() != null) {
				// We only need to instantiate this object for the ASSIGNMENTS ColumnGroup
				if (categoryColumnMap == null) 
					categoryColumnMap = new HashMap<Long, ArrayList<FixedColumnModel>>();

				ArrayList<FixedColumnModel> categoryColumns = categoryColumnMap.get(column.getCategoryId());
				if (categoryColumns == null) {
					categoryColumns = new ArrayList<FixedColumnModel>();
					categoryColumnMap.put(column.getCategoryId(), categoryColumns);
				}
				categoryColumns.add(column);
			}
			this.columns.add(column);
		}

		public GroupType getGroup() {
			return group;
		}

	}

	// Helper method
	protected Collection<LearnerKey> getGroupColumnKeys(ColumnGroup columnGroup) {

		ArrayList<LearnerKey> groupColumnKeys = new ArrayList<LearnerKey>();
		List<FixedColumnModel> gradebookColumunConfigs = columnGroup.getColumns();

		for(FixedColumn gradebookColumnConfig : gradebookColumunConfigs) {

			LearnerKey key = LearnerKey.valueOf(gradebookColumnConfig.getKey());

			groupColumnKeys.add(key);
		}

		return groupColumnKeys;
	}

	// Helper method
	protected int getColumnIndex(LearnerKey key) {

		return cm.getIndexById(key.name());
	}	
}
