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

package org.sakaiproject.gradebook.gwt.client.gxt.multigrade;

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.gxt.GbEditorGrid;
import org.sakaiproject.gradebook.gwt.client.gxt.GbGridCallback;
import org.sakaiproject.gradebook.gwt.client.gxt.GbEditorGrid.GbCell;
import org.sakaiproject.gradebook.gwt.client.gxt.custom.widget.grid.BaseCustomGridView;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.KeyboardListener;

public class MultigradeSelectionModel<M extends ModelData> extends
GridSelectionModel<M> {

	private GbGridCallback callback = new GbGridCallback(this);
	private EditorGrid editGrid;

	@Override
	public void bindGrid(Grid grid) {
		if (this.grid != null) {
			this.grid.removeListener(Events.CellMouseDown, this);
			this.grid.getView().removeListener(Events.Refresh, this);
			keyNav.bind(null);
			bind(null);
		}
		this.grid = grid;
		if (grid != null) {
			grid.setTrackMouseOver(false);
			grid.addListener(Events.CellMouseDown, this);
			grid.getView().addListener(Events.Refresh, this);
			editGrid = grid instanceof EditorGrid ? ((EditorGrid) grid) : null;
			keyNav.bind(grid);
			bind(grid.getStore());
		}
		bind(grid != null ? grid.getStore() : null);
	}

	@Override
	public void deselectAll() {
		if (selection != null) {
			((BaseCustomGridView)grid.getView()).doCellDeselect(selection.row, selection.cell);
			selection = null;
		}
	}

	/**
	 * Returns the selected cell.
	 * 
	 * @return the selection cell
	 */
	public CellSelection getSelectCell() {
		return selection;
	}

	@Override
	public void handleEvent(BaseEvent e) {
		switch (e.type) {
			case Events.CellMouseDown:
				handleMouseDown((GridEvent) e);
				break;
			case Events.Refresh:
				refresh();
				break;
		}
	}

	public boolean isCellSelectable(int row, int cell, boolean acceptsNav) {
		if (acceptsNav) {
			return !grid.getColumnModel().isHidden(cell)
			&& grid.getColumnModel().isCellEditable(cell);
		} else {
			return !grid.getColumnModel().isHidden(cell);
		}
	}

	/**
	 * Selects the cell.
	 * 
	 * @param row
	 *            the row index
	 * @param cell
	 *            the cell index
	 */
	@Override
	public void selectCell(int row, int cell) {
		deselectAll();
		M m = store.getAt(row);
		selection = new CellSelection(m, row, cell);
		((BaseCustomGridView)grid.getView()).doCellSelect(row, cell);
		grid.getView().focusCell(row, cell, true);
	}

	@Override
	protected void handleMouseDown(GridEvent e) {
		if (e.event.getButton() != Event.BUTTON_LEFT || isLocked()) {
			return;
		}
		selectCell(e.rowIndex, e.colIndex);
	}

	@Override
	protected void onAdd(List<M> models) {
		deselectAll();
	}

	@Override
	protected void onClear(StoreEvent<M> se) {
		super.onClear(se);
		selection = null;
	}

	@Override
	protected void onKeyPress(GridEvent e) {
		if (editGrid != null) {
			// ignore events whose source is an input element
			String tag = e.getTarget().getTagName();
			if (tag.equals("INPUT")
					&& !e.getTarget().getClassName().equals("_focus")) {
				return;
			}
		}
		if (selection == null) {
			e.stopEvent();
			GbCell cell = ((GbEditorGrid)grid).doWalkCells(0, 0, 1, callback, false);
			if (cell != null) {
				selectCell(cell.row, cell.cell);
			}
			return;
		}

		int r = selection.row;
		int c = selection.cell;

		GbCell newCell = null;

		switch (e.getKeyCode()) {
			case KeyboardListener.KEY_TAB:
				if (e.isShiftKey()) {
					newCell = ((GbEditorGrid)grid).doWalkCells(r, c - 1, -1, callback, false);
				} else {
					newCell = ((GbEditorGrid)grid).doWalkCells(r, c + 1, 1, callback, false);
				}
				break;
			case KeyboardListener.KEY_DOWN: {
				newCell = ((GbEditorGrid)grid).doWalkCells(r + 1, c, 1, callback, false);
				break;
			}
			case KeyboardListener.KEY_UP: {
				newCell = ((GbEditorGrid)grid).doWalkCells(r - 1, c, -1, callback, false);
				break;
			}
			case KeyboardListener.KEY_LEFT:
				newCell = ((GbEditorGrid)grid).doWalkCells(r, c - 1, -1, callback, false);
				break;
			case KeyboardListener.KEY_RIGHT:
				newCell = ((GbEditorGrid)grid).doWalkCells(r, c + 1, 1, callback, false);
				break;
			case KeyboardListener.KEY_ENTER:
				if (editGrid != null) {
					if (!editGrid.isEditing()) {
						editGrid.startEditing(r, c);
						e.stopEvent();
						return;
					}
				}
				break;

		}
		if (newCell != null) {
			selectCell(newCell.row, newCell.cell);
			e.stopEvent();
		}
	}

	@Override
	protected void onRemove(M model) {
		super.onRemove(model);
		if (selection != null && selection.model == model) {
			selection = null;
		}
	}

}
