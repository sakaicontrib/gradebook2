package org.sakaiproject.gradebook.gwt.client.gxt.custom;

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.gxt.custom.widget.grid.ItemTreeGridView;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ShowColumnsEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.google.gwt.user.client.Event;

public class ItemTreeSelectionModel extends GridSelectionModel<ItemModel> implements ComponentPlugin {
	
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
			if (isChecked) {
				//setChecked(false);
				//deselectAll();
			} else {
				//setChecked(true);
				//selectAll();
			}
		}
	}

	@Override
	protected void onAdd(List<? extends ItemModel> models) {
		super.onAdd(models);
		//setChecked(getSelection().size() == grid.getStore().getCount());
	}

	@Override
	protected void onClear(StoreEvent<ItemModel> se) {
		super.onClear(se);
		//setChecked(false);
	}

	@Override
	protected void onRemove(ItemModel model) {
		super.onRemove(model);
		//setChecked(getSelection().size() == grid.getStore().getCount());
	}

	@Override
	protected void onSelectChange(ItemModel model, boolean select) {
		super.onSelectChange(model, select);
		//setChecked(getSelection().size() == grid.getStore().getCount());
	}

	/*private void setChecked(boolean checked) {
		if (grid.isViewReady()) {
			El hd = grid.getView().innerHd.child("div.x-grid3-hd-checker");
			hd.getParent().setStyleName("x-grid3-hd-checker-on", checked);
		}
	}*/
	
	public void toggle(ItemModel m, boolean isChecked) {
		toggleItem(m, !isChecked, listStore.indexOf(m));
		Dispatcher.forwardEvent(GradebookEvents.ShowColumns.getEventType(),
				new ShowColumnsEvent(m, !isChecked));
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
		Dispatcher.forwardEvent(GradebookEvents.ShowColumns.getEventType(),
				new ShowColumnsEvent(m, !m.isChecked()));
	}
	
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
