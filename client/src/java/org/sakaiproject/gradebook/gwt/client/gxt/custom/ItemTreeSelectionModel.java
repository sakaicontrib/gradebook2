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

package org.sakaiproject.gradebook.gwt.client.gxt.custom;

import org.sakaiproject.gradebook.gwt.client.gxt.custom.widget.grid.ItemTreeGridView;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ShowColumnsEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.google.gwt.user.client.Event;

public abstract class ItemTreeSelectionModel extends GridSelectionModel<ItemModel> implements ComponentPlugin {
	
	protected ColumnConfig config;
	
	public ItemTreeSelectionModel() {
		super();
		config = newColumnConfig();
		config.setId("checker");
		config.setWidth(20);
		config.setSortable(false);
		config.setResizable(false);
		config.setFixed(true);
		config.setMenuDisabled(true);
		config.setDataIndex("");
		config.setRenderer(new GridCellRenderer<ItemModel>() {
			public String render(ItemModel model, String property, ColumnData config,
					int rowIndex, int colIndex, ListStore<ItemModel> store, Grid<ItemModel> grid) {
				config.cellAttr = "rowspan='2'";
				return "<div class='x-grid3-row-checker'>&#160;</div>";
			}
		});
	}

	/**
	 * Returns the column config.
	 * 
	 * @return the column config
	 */
	public ColumnConfig getColumn() {
		return config;
	}

	@SuppressWarnings("unchecked")
	public void init(Component component) {
		this.grid = (Grid) component;
		grid.addListener(Events.HeaderClick, new Listener<GridEvent>() {
			public void handleEvent(GridEvent e) {
				onHeaderClick(e);
			}
		});
		this.store = grid.getStore();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void handleMouseDown(GridEvent<ItemModel> e) {
		if (e.getEvent().getButton() == Event.BUTTON_LEFT
				&& e.getTarget().getClassName().equals("x-grid3-row-checker")) {
			int rowIndex = e.getRowIndex();
			ItemModel m = listStore.getAt(e.getRowIndex());
			if (m != null) {
				toggle(m, rowIndex);
			}
		} else {
			super.handleMouseDown(e);
		}
	}
	
	protected ColumnConfig newColumnConfig() {
	    return new ColumnConfig();
	  }

	protected void onHeaderClick(GridEvent<ItemModel> e) {
		ColumnConfig c = grid.getColumnModel().getColumn(e.getColIndex());
		if (c == config) {
			El hd = e.getTargetEl().getParent();
			boolean isChecked = hd.hasStyleName("x-grid3-hd-checker-on");

		}
	}
	
	public void toggle(ItemModel m, boolean isChecked) {
		toggleItem(m, !isChecked, listStore.indexOf(m));
		sendShowColumnsEvent(new ShowColumnsEvent(m, !isChecked));
	}
	
	public void toggle(ItemModel m, int rowIndex) {
		switch (m.getItemType()) {
		case GRADEBOOK:
			toggleCategory(m, m.isChecked(), rowIndex);
			break;
		case CATEGORY:
			toggleCategory(m, m.isChecked(), rowIndex);
			break;
		case ITEM:
			toggleItem(m, m.isChecked(), rowIndex);
			break;
		}
		sendShowColumnsEvent(new ShowColumnsEvent(m, !m.isChecked()));
	}
	
	protected abstract void sendShowColumnsEvent(ShowColumnsEvent event);
	
	
	private void toggleCategory(ItemModel m, boolean isChecked, int rowIndex) {
		if (m.getChildCount() > 0) {
			for (int i=0;i<m.getChildCount();i++) {
				ItemModel child = (ItemModel)m.getChild(i);
				switch (child.getItemType()) {
				case CATEGORY:
					toggleCategory(child, isChecked, listStore.indexOf(child));
					break;
				case ITEM:
					toggleItem(child, isChecked, listStore.indexOf(child));
					break;
				}
			}
			
			doToggle(m, isChecked, rowIndex);
		}
	}
	
	private void toggleItem(ItemModel m, boolean isChecked, int rowIndex) {
		String id = m.get(ItemKey.S_ID.name());

		if (id != null) {
			doToggle(m, isChecked, rowIndex);
		}
	}
	
	private void doToggle(Item m, boolean isChecked, int rowIndex) {
		ItemTreeGridView view = null;
		
		if (grid.getView() instanceof ItemTreeGridView)
			view = (ItemTreeGridView)grid.getView();
		
		if (isChecked) {
			if (view != null)
				view.onRowUncheck(rowIndex);
		} else {
			if (view != null)
				view.onRowCheck(rowIndex);
		}
		m.setChecked(!isChecked);
	}
	
}
