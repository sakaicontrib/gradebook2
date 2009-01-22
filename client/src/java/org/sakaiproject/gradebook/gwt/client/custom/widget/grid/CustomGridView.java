/**********************************************************************************
*
* $Id: CustomGridView.java 6638 2009-01-22 01:27:23Z jrenfro $
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
package org.sakaiproject.gradebook.gwt.client.custom.widget.grid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.model.ColumnModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel.Group;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel.Key;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Item;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.Cookies;


// SAK-2394 

public abstract class CustomGridView extends BaseCustomGridView {
	
	// Member variables
	private final boolean SHOW = true;
	private String gradebookUid = null;
	private GradebookModel gradebookModel = null;

	public CustomGridView(String gradebookUid) {
		this.gradebookUid = gradebookUid;
	}

	public void doRowRefresh(int row) {
		refreshRow(row);
	}

	/* (non-Javadoc)
	 * @see com.extjs.gxt.ui.client.widget.grid.GridView#createContextMenu(int)
	 */
	@Override
	protected Menu createContextMenu(final int colIndex) {
		
		// Getting the GradebookModel
		gradebookModel = Registry.get(gradebookUid);
		
		final Menu rootMenu = new Menu();

		if (cm.isSortable(colIndex)) {
			MenuItem rootMenuItemSortAscDesc = new MenuItem();
			rootMenuItemSortAscDesc.setText(GXT.MESSAGES.gridView_sortAscText());
			rootMenuItemSortAscDesc.setIconStyle("my-icon-asc");
			rootMenuItemSortAscDesc.addSelectionListener(new SelectionListener<ComponentEvent>() {
				public void componentSelected(ComponentEvent ce) {
					ds.sort(cm.getDataIndex(colIndex), SortDir.ASC);
				}

			});
			rootMenu.add(rootMenuItemSortAscDesc);

			rootMenuItemSortAscDesc = new MenuItem();
			rootMenuItemSortAscDesc.setText(GXT.MESSAGES.gridView_sortDescText());
			rootMenuItemSortAscDesc.setIconStyle("my-icon-desc");
			rootMenuItemSortAscDesc.addSelectionListener(new SelectionListener<ComponentEvent>() {
				public void componentSelected(ComponentEvent ce) {
					ds.sort(cm.getDataIndex(colIndex), SortDir.DESC);
				}
			});
			rootMenu.add(rootMenuItemSortAscDesc);
		}
		
		// Adding top level menu items for Student Information, Grades, ...
		List<ColumnGroup> columnGroups = getColumnGroups();
				
		for(final ColumnGroup columnGroup : columnGroups) {
			
			switch(columnGroup.getGroup()) {
			
				case STUDENT_INFORMATION:
				case GRADES:	
					MenuItem noneCategoryMenuItem = new MenuItem(columnGroup.getGroup().getDisplayName());
					noneCategoryMenuItem.setIconStyle("x-cols-icon");
					rootMenu.add(noneCategoryMenuItem);
					Collection<Key> keys = getGroupColumnKeys(columnGroup);
					final Menu groupMenu = new Menu();
					noneCategoryMenuItem.setSubMenu(groupMenu);
					for(Key key : keys) {
						final int columnIndex = getColumnIndex(key);
						CheckMenuItem checkGroupMenuItem = new CheckMenuItem(key.getDisplayName());
						checkGroupMenuItem.setHideOnClick(false);
						checkGroupMenuItem.setChecked(!cm.isHidden(columnIndex));
						checkGroupMenuItem.addSelectionListener(new SelectionListener<ComponentEvent>() {
							public void componentSelected(ComponentEvent ce) {
								cm.setHidden(columnIndex, !cm.isHidden(columnIndex));
								restrictMenu(rootMenu);
							}
						});
						groupMenu.add(checkGroupMenuItem);
					}
					break;
				case ASSIGNMENTS:
					// With assignments, we need to break things down further into categories
					
					Map<Long, List<ColumnModel>> categoryColumnMap = columnGroup.getCategoryColumnMap();
					
					// Shouldn't happen
					if (categoryColumnMap != null) {

						for (final List<ColumnModel> columns : categoryColumnMap.values()) {

							String categoryName = columns.get(0).getCategoryName();
							
							MenuItem categoryMenuItem = new MenuItem(categoryName);
							categoryMenuItem.setIconStyle("x-cols-icon");
							rootMenu.add(categoryMenuItem);
							
							final Menu categoryMenu = new Menu();
							
							// Adding show/hide all columns for category
							MenuItem showAllCategoryColumnsMenuItem = new MenuItem("Show All");
							MenuItem hideAllCategoryColumnsMenuItem = new MenuItem("Hide All");
							showAllCategoryColumnsMenuItem.setIconStyle("grid-show-columns");
							hideAllCategoryColumnsMenuItem.setIconStyle("grid-hide-columns");
							showAllCategoryColumnsMenuItem.setHideOnClick(false);
							hideAllCategoryColumnsMenuItem.setHideOnClick(false);
							
							categoryMenu.add(showAllCategoryColumnsMenuItem);
							categoryMenu.add(hideAllCategoryColumnsMenuItem);
							hideAllCategoryColumnsMenuItem.addSelectionListener(new SelectionListener<ComponentEvent>() {
								public void componentSelected(ComponentEvent ce) {
									showAllColumns(categoryMenu, columns, SHOW);
									restrictMenu(rootMenu);
								}
							});
							showAllCategoryColumnsMenuItem.addSelectionListener(new SelectionListener<ComponentEvent> () {
								public void componentSelected(ComponentEvent ce) {
									showAllColumns(categoryMenu, columns, !SHOW);
									restrictMenu(rootMenu);
								}
							});
							
							
							categoryMenuItem.setSubMenu(categoryMenu);
							
							for(ColumnModel column : columns) {
								final int columnIndex = cm.getIndexById(column.getIdentifier());
								CheckMenuItem checkColumnMenuItem = new CheckMenuItem(cm.getColumnHeader(columnIndex));
								checkColumnMenuItem.setHideOnClick(false);
								categoryMenu.add(checkColumnMenuItem);
									
								checkColumnMenuItem.setChecked(!cm.isHidden(columnIndex));
								checkColumnMenuItem.addSelectionListener(new SelectionListener<ComponentEvent>() {
									public void componentSelected(ComponentEvent ce) {									
										cm.setHidden(columnIndex, !cm.isHidden(columnIndex));	
										restrictMenu(rootMenu);
									}
								});
							}
						}
					}
					break;
				default:
				// FIXME: Log or handle this error
			}
		}
		
		restrictMenu(rootMenu);
		return rootMenu;
	}
	
	// Helper method
	protected void showAllColumns(Menu categoryMenu, List<ColumnModel> columns, boolean show) {
		
		List<Item> menuItems = categoryMenu.getItems();
		// Determine the number of check menu items, accounting for any regular menu item such as show/hide all, etc.
		int menuIndex = categoryMenu.getItemCount() - columns.size();
		
		for (ColumnModel column : columns) {
			
			int columnIndex = cm.getIndexById(column.getIdentifier());
			
			// Logic to NOT hide the last column
			if(show && 1 == cm.getColumnCount(true)) {
				continue;
			}

			Item item = menuItems.get(menuIndex);

			if(item instanceof CheckMenuItem) {
				
				CheckMenuItem checkMenuItem = (CheckMenuItem) item;
				checkMenuItem.setChecked(!show);

			}
			
			cm.setHidden(columnIndex, show);

			menuIndex++;
		}
	}
	
	protected void updateColumnWidth(int col, int width) {
		super.updateColumnWidth(col, width);
		
		ColumnConfig column = cm.getColumn(col);
		String cookieWidthId = "gb:" + gradebookUid + ":prop:column:" + column.getId() + ":width";
		
		Cookies.setCookie(cookieWidthId, String.valueOf(width));
	}
	
	public class ColumnGroup {
		
		private Group group;
		private List<ColumnModel> columns;
		Map<Long, List<ColumnModel>> categoryColumnMap;
		
		public ColumnGroup(Group group) {
			this.group = group;
			this.columns = new LinkedList<ColumnModel>();
		}
		
		public List<ColumnModel> getColumns() {
			return columns;
		}
		
		public Map<Long, List<ColumnModel>> getCategoryColumnMap() {
			return categoryColumnMap;
		}
		
		public void addColumn(ColumnModel column) {
			// If there is a category, then use it
			if (column.getCategoryId() != null) {
				// We only need to instantiate this object for the ASSIGNMENTS ColumnGroup
				if (categoryColumnMap == null) 
					categoryColumnMap = new HashMap<Long, List<ColumnModel>>();
				
				List<ColumnModel> categoryColumns = categoryColumnMap.get(column.getCategoryId());
				if (categoryColumns == null) {
					categoryColumns = new LinkedList<ColumnModel>();
					categoryColumnMap.put(column.getCategoryId(), categoryColumns);
				}
				categoryColumns.add(column);
			}
			this.columns.add(column);
		}
		
		public Group getGroup() {
			return group;
		}
		
	}
	
	// Helper method
	protected List<ColumnGroup> getColumnGroups() {
		
		Map<Group, ColumnGroup> columnGroupMap = new LinkedHashMap<Group, ColumnGroup>();
		//ArrayList<ColumnGroup> columnGroups = new ArrayList<ColumnGroup>();
		List<ColumnModel> gradebookColumunConfigs = gradebookModel.getColumns();
		
		for(ColumnModel gradebookColumnConfig : gradebookColumunConfigs) {
			StudentModel.Key key = StudentModel.Key.valueOf(gradebookColumnConfig.getKey());
			Group group = key.getGroup();
			ColumnGroup columnGroup = columnGroupMap.get(group);
			
			if (columnGroup == null) {
				columnGroup = new ColumnGroup(group);
				columnGroupMap.put(group, columnGroup);
			}
			columnGroup.addColumn(gradebookColumnConfig);
		}
	
		return new ArrayList<ColumnGroup>(columnGroupMap.values());
	}
	
	// Helper method
	protected Collection<Key> getGroupColumnKeys(Group group) {
		
		ArrayList<Key> groupColumnKeys = new ArrayList<Key>();
		List<ColumnModel> gradebookColumunConfigs = gradebookModel.getColumns();
		
		for(ColumnModel gradebookColumnConfig : gradebookColumunConfigs) {
			
			StudentModel.Key key = StudentModel.Key.valueOf(gradebookColumnConfig.getKey());
			
			if(group.equals(key.getGroup())) {
				groupColumnKeys.add(key);
			}
		}
		
		return groupColumnKeys;
	}
	
	// Helper method
	protected Collection<Key> getGroupColumnKeys(ColumnGroup columnGroup) {
		
		ArrayList<Key> groupColumnKeys = new ArrayList<Key>();
		List<ColumnModel> gradebookColumunConfigs = columnGroup.getColumns();
		
		for(ColumnModel gradebookColumnConfig : gradebookColumunConfigs) {
			
			StudentModel.Key key = StudentModel.Key.valueOf(gradebookColumnConfig.getKey());
		
			groupColumnKeys.add(key);
		}
		
		return groupColumnKeys;
	}
	
	// Helper method
	protected int getColumnIndex(Key key) {
		
		return cm.getIndexById(key.name());
	}
	
	
	/**
	 * Restrict menu. This is a private method in GridView.
	 * TPA: I modified the original method to allow for subMenus
	 * 
	 * @param menu a menu
	 */
	private void restrictMenu(Menu menu) {
	
		if(null == menu) {
			return;
		}

		for(Item item : menu.getItems()) {

			if(item instanceof CheckMenuItem) {

				if(1 == cm.getColumnCount(true)) {

					CheckMenuItem checkMenuItem = (CheckMenuItem) item;

					if(checkMenuItem.isChecked()) {

						checkMenuItem.disable();
					}
				}
				else if(item instanceof CheckMenuItem) {

					item.enable();
				}
			}
			else {

				if(item instanceof MenuItem) {

					MenuItem menuItem = (MenuItem) item;
					Menu subMenu = menuItem.getSubMenu();

					if(null != subMenu) {

						restrictMenu(menuItem.getSubMenu());
					}
				}
			}
		}
	}
}
