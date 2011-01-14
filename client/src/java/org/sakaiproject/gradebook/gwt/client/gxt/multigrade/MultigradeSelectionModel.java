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

import org.sakaiproject.gradebook.gwt.client.gxt.GbEditorGrid;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.DomEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CellSelectionModel;
import com.google.gwt.event.dom.client.KeyCodes;

public class MultigradeSelectionModel<M extends ModelData> extends CellSelectionModel<M> {

	private Callback callback = new Callback(this);
	private boolean useClassic = false;

	public MultigradeSelectionModel() {
		super();
		setMoveEditorOnEnter(true);
	}

	@Override
	public void onEditorKey(DomEvent e) {
		if (useClassic) {
			super.onEditorKey(e);
			return;
		}
		int k = e.getKeyCode();
		Cell newCell = null;
		CellEditor editor = ((GbEditorGrid<M>)grid).getEditorSupport().getActiveEditor();
		switch (k) {
		case KeyCodes.KEY_ENTER:
			e.stopEvent();
			if (editor != null) {
				editor.completeEdit();

				if (isMoveEditorOnEnter()) {
					if (e.isShiftKey()) {
						newCell = ((GbEditorGrid<M>)grid).doWalkCells(editor.row - 1, editor.col, -1, callback, true);
					} else {
						newCell = ((GbEditorGrid<M>)grid).doWalkCells(editor.row + 1, editor.col, 1, callback, true);
					}
				} else if (k == KeyCodes.KEY_DOWN) {
					newCell = ((GbEditorGrid<M>)grid).doWalkCells(editor.row + 1, editor.col, 1, callback, false);
					break;
				} else if (k == KeyCodes.KEY_UP) {
					newCell = ((GbEditorGrid<M>)grid).doWalkCells(editor.row - 1, editor.col, -1, callback, false);
					break;
				}
			}

			break;
		case KeyCodes.KEY_TAB:
			e.stopEvent();
			if (editor != null) {
				editor.completeEdit();

				if (e.isShiftKey()) {
					newCell = ((GbEditorGrid<M>) grid).doWalkCells(editor.row,
							editor.col - 1, -1, callback, true);
				} else {
					newCell = ((GbEditorGrid<M>) grid).doWalkCells(editor.row,
							editor.col + 1, 1, callback, true);
				}
			}

			break;
		case KeyCodes.KEY_ESCAPE:
			if (editor != null) {
				editor.cancelEdit();
			}
			break;
		case KeyCodes.KEY_UP:
			if(editor != null) {
				newCell = ((GbEditorGrid<M>)grid).doWalkCells(editor.row - 1, editor.col, -1, callback, true);
			}
			break;
		case KeyCodes.KEY_DOWN:
			if(editor != null) {
				newCell = ((GbEditorGrid<M>)grid).doWalkCells(editor.row + 1, editor.col, 1, callback, true);
			}
			break;
		}
		if (newCell != null) {
			((GbEditorGrid<M>)grid).getEditorSupport().startEditing(newCell.row, newCell.cell);
		} else {
			if (k == KeyCodes.KEY_ENTER || k == KeyCodes.KEY_TAB || k == KeyCodes.KEY_ESCAPE) {
				if(editor != null) {
					grid.getView().focusCell(editor.row, editor.col, false);
				}
			}
		}
	}

	protected boolean isSelectable(int row, int cell, boolean acceptsNav) {
		if (acceptsNav) {
			return !grid.getColumnModel().isHidden(cell) && grid.getColumnModel().isCellEditable(cell);
		} else {
			return !grid.getColumnModel().isHidden(cell);
		}
	}

	@Override
	protected void onKeyPress(GridEvent<M> e) {
		if (((GbEditorGrid<M>)grid).getEditorSupport() != null) {
			// ignore events whose source is an input element
			String tag = e.getTarget().getTagName();
			if (tag.equals("INPUT") && !e.getTarget().getClassName().equals("_focus")) {
				return;
			}
		}
		if (selection == null) {
			e.stopEvent();
			Cell cell = ((GbEditorGrid<M>)grid).doWalkCells(0, 0, 1, callback, false);
			if (cell != null) {
				selectCell(cell.row, cell.cell);
			}
			return;
		}

		int r = selection.row;
		int c = selection.cell;

		Cell newCell = null;

		switch (e.getKeyCode()) {
		case KeyCodes.KEY_TAB:
			if (useClassic) {
				if (e.isShiftKey()) 
					newCell = ((GbEditorGrid<M>)grid).doWalkCells(r, c - 1, -1, callback, false);
				else 
					newCell = ((GbEditorGrid<M>)grid).doWalkCells(r, c + 1, 1, callback, false);
			} else {
				if (e.isShiftKey()) 
					newCell = ((GbEditorGrid<M>)grid).doWalkCells(r - 1, c, -1, callback, false);
				else
					newCell = ((GbEditorGrid<M>)grid).doWalkCells(r + 1, c, 1, callback, false);
			}
			break;
		case KeyCodes.KEY_DOWN: {
			newCell = ((GbEditorGrid<M>)grid).doWalkCells(r + 1, c, 1, callback, false);
			break;
		}
		case KeyCodes.KEY_UP: {
			newCell = ((GbEditorGrid<M>)grid).doWalkCells(r - 1, c, -1, callback, false);
			break;
		}
		case KeyCodes.KEY_LEFT:
			newCell = ((GbEditorGrid<M>)grid).doWalkCells(r, c - 1, -1, callback, false);
			break;
		case KeyCodes.KEY_RIGHT:
			newCell = ((GbEditorGrid<M>)grid).doWalkCells(r, c + 1, 1, callback, false);
			break;
		case KeyCodes.KEY_ENTER:
			if (((GbEditorGrid<M>)grid).getEditorSupport() != null) {
				if (!((GbEditorGrid<M>)grid).getEditorSupport().isEditing()) {
					((GbEditorGrid<M>)grid).getEditorSupport().startEditing(r, c);
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

	public boolean isUseClassic() {
		return useClassic;
	}

	public void setUseClassic(boolean useClassic) {
		setMoveEditorOnEnter(!useClassic);
		this.useClassic = useClassic;
	}

}
