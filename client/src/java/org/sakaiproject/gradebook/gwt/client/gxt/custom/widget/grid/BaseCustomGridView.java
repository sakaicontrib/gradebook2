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
import java.util.List;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.google.gwt.user.client.ui.Widget;


public class BaseCustomGridView extends GridView {


	public void doCellDeselect(int row, int col) {
		super.onCellDeselect(row, col);
	}

	public void doCellSelect(int row, int col) {
		super.onCellSelect(row, col);
	}


	protected String markupCss(Record r, ModelData model, String property, boolean isShowDirtyCells, boolean isPropertyChanged) {

		return null;
	}

	protected String markupInnerCss(ModelData model, String property, boolean isShowDirtyCells, boolean isPropertyChanged) {

		return null;
	}

	private boolean selectable = true;
	
	@Override
	protected String doRender(List<ColumnData> cs, List<ModelData> rows, int startRow, int colCount, boolean stripe) {
		int last = colCount - 1;
		String tstyle = new StringBuilder("width:").append(getTotalWidth()).append("px;").toString();

		StringBuilder buf = new StringBuilder();

		for (int j = 0; j < rows.size(); j++) {
			ModelData model = (ModelData) rows.get(j);

			model = prepareData(model);

			Record r = ds.hasRecord(model) ? ds.getRecord(model) : null;

			int rowBodyColSpanCount = colCount;
			if (enableRowBody) {
				if (grid.getSelectionModel() instanceof CheckBoxSelectionModel<?>) {
					CheckBoxSelectionModel<?> sm = (CheckBoxSelectionModel<?>) grid.getSelectionModel();
					if (cm.getColumnById(sm.getColumn().getId()) != null) {
						rowBodyColSpanCount--;
					}
				}
				for (ColumnConfig c : cm.getColumns()) {
					if (c instanceof RowExpander || c instanceof RowNumberer) {
						rowBodyColSpanCount--;
					}
				}
			}
			int rowIndex = (j + startRow);

			buf.append("<div class=\"x-grid3-row ");
			if (stripe && ((rowIndex + 1) % 2 == 0)) {
				buf.append(" x-grid3-row-alt");
			}
			if (!selectable) {
				buf.append(" x-unselectable-single");
			}

			if (isShowDirtyCells() && r != null && r.isDirty()) {
				buf.append(" x-grid3-dirty-row");
			}
			if (viewConfig != null) {
				buf.append(" ");
				buf.append(viewConfig.getRowStyle(model, rowIndex, ds));
			}
			buf.append("\" style=\"");
			buf.append(tstyle);
			buf.append("\"><table class=\"x-grid3-row-table\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"");
			buf.append(tstyle);
			buf.append("\"><tbody><tr>");
			widgetList.add(rowIndex, new ArrayList<Widget>());
			for (int i = 0; i < colCount; i++) {
				ColumnData c = cs.get(i);
				c.css = c.css == null ? "" : c.css;
				String rv = getRenderedValue(c, rowIndex, i, model, c.name);

				String attr = c.cellAttr != null ? c.cellAttr : "";
				String cellAttr = c.cellAttr != null ? c.cellAttr : "";

				buf.append("<td class=\"x-grid3-col x-grid3-cell x-grid3-td-");
				buf.append(c.id);
				buf.append(" ");
				buf.append(i == 0 ? "x-grid-cell-first " : (i == last ? "x-grid3-cell-last " : ""));
				if (c.css != null) {
					buf.append(c.css);
				}

				// Gradebook2 customization begins here
				boolean isDirty = isShowDirtyCells();
				boolean isChanged = r != null && r.getChanges().containsKey(c.id);

				String markedUpCss = markupCss(r, model, c.id, isDirty, isChanged);
				String markedUpInnerCss = markupInnerCss(model, c.id, isDirty, isChanged);

				if (markedUpCss != null && markedUpCss.length() > 0)
					buf.append(markedUpCss);

				buf.append("\" style=\"");
				buf.append(c.style);
				buf.append("\" tabIndex=\"0\" ");
				buf.append(cellAttr);
				buf.append("><div unselectable=\"");
				buf.append(selectable ? "off" : "on");
				buf.append("\" class=\"x-grid3-cell-inner x-grid3-col-");
				buf.append(c.id);

				if (markedUpInnerCss != null && markedUpInnerCss.length() > 0)
					buf.append(markedUpInnerCss);
				// Gradebook2 customization ends here

				buf.append("\" ");
				buf.append(attr);
				buf.append(">");
				buf.append(rv);
				buf.append("</div></td>");	
			}

			buf.append("</tr>");
			if (enableRowBody) {
				buf.append("<tr class=x-grid3-row-body-tr style=\"\"><td colspan=");
				buf.append(rowBodyColSpanCount);
				buf.append(" class=x-grid3-body-cell tabIndex=0><div class=x-grid3-row-body>${body}</div></td></tr>");
			}
			buf.append("</tbody></table></div>");
		}

		return buf.toString();
	}
}
