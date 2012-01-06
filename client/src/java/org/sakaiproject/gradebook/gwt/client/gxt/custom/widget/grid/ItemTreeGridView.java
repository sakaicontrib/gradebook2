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

import java.util.Date;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.Item;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridView;
import com.extjs.gxt.ui.client.widget.treegrid.WidgetTreeGridCellRenderer;
import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Widget;

public class ItemTreeGridView extends TreeGridView {
	
	public void onRowCheck(int rowIndex) {
		Element row = getRow(rowIndex);
		if (row != null) {
			onRowOut(row);
			addRowStyle(row, "x-grid3-row-checked");
		}
	}

	public void onRowUncheck(int rowIndex) {
		Element row = getRow(rowIndex);
		if (row != null) {
			removeRowStyle(row, "x-grid3-row-checked");
		}
	}

	protected String doRender(List<ColumnData> cs, List<ModelData> rows,
			int startRow, int colCount, boolean stripe) {
		int last = colCount - 1;
		String tstyle = "width:" + getTotalWidth() + "px;";

		StringBuilder buf = new StringBuilder();

		for (int j = 0; j < rows.size(); j++) {
			ModelData model = (ModelData) rows.get(j);
			Record r = ds.hasRecord(model) ? ds.getRecord(model) : null;

			int rowIndex = (j + startRow);

			buf.append("<div class=\"x-grid3-row ");
			if (stripe && ((rowIndex + 1) % 2 == 0)) {
				buf.append(" x-grid3-row-alt");
			}
			if (isShowDirtyCells() && r != null && r.isDirty()) {
				buf.append(" x-grid3-dirty-row");
			}
			if (model instanceof ItemModel && ((Item)model).isChecked()) {
				buf.append(" x-grid3-row-checked");
			}
			if (viewConfig != null) {
				buf.append(" ");
				buf.append(viewConfig.getRowStyle(model, rowIndex, ds));
			}
			buf.append("\" style=\"");
			buf.append(tstyle);
			buf
					.append("\"><table class=x-grid3-row-table border=0 cellspacing=0 cellpadding=0 style=\"");
			buf.append(tstyle);
			buf.append("\"><tbody><tr>");

			for (int i = 0; i < colCount; i++) {
				ColumnData c = cs.get(i);
				c.css = c.css == null ? "" : c.css;
				String rv = getRenderedValue(c, rowIndex, i, model, c.name);

				String attr = c.cellAttr != null ? c.cellAttr : "";
				String cellAttr = c.cellAttr != null ? c.cellAttr : "";

				buf.append("<td class=\"x-grid3-col x-grid3-cell x-grid3-td-");
				buf.append(c.id);
				buf.append(" ");
				buf.append(i == 0 ? "x-grid-cell-first "
						: (i == last ? "x-grid3-cell-last " : ""));
				if (c.css != null) {
					buf.append(c.css);
				}
				if (isShowDirtyCells() && r != null
						&& r.getChanges().containsKey(c.id)) {
					buf.append(" x-grid3-dirty-cell");
				}
				buf.append("\" style=\"");
				buf.append(c.style);
				buf.append("\" tabIndex=0 ");
				buf.append(cellAttr);
				buf.append("><div class=\"x-grid3-cell-inner x-grid3-col-");
				buf.append(c.id);
				buf.append("\" ");
				buf.append(attr);
				buf.append(">");
				buf.append(rv);
				buf.append("</div></td>");
			}

			buf.append("</tr>");
			buf
					.append((enableRowBody ? ("<tr class=x-grid3-row-body-tr style=\"\"><td colspan="
							+ colCount + " class=x-grid3-body-cell tabIndex=0><div class=x-grid3-row-body>${body}</div></td></tr>")
							: ""));
			buf.append("</tbody></table></div>");
		}

		return buf.toString();
	}
	
	// FIXME: There seems to be a substantial bug in GXT 2.0.4 that requires the following
	// commented out override version to be included. 
	@Override
	protected String getRenderedValue(ColumnData data, int rowIndex,
			int colIndex, ModelData m, String property) {
		GridCellRenderer<ModelData> r = cm.getRenderer(colIndex);
		if (r != null) {
			Object o = r.render(ds.getAt(rowIndex), property, data, rowIndex,
					colIndex, ds, grid);
			if (o instanceof Widget || r instanceof WidgetTreeGridCellRenderer) {
				return "";
			} else {
				if (o == null)
					return "";
				return o.toString();
			}
		}
		Object val = m.get(property);

		ColumnConfig c = cm.getColumn(colIndex);

		if (val != null && c.getNumberFormat() != null) {
			Number n = (Number) val;
			NumberFormat nf = cm.getColumn(colIndex).getNumberFormat();
			val = nf.format(n.doubleValue());
		} else if (val != null && c.getDateTimeFormat() != null) {
			DateTimeFormat dtf = c.getDateTimeFormat();
			val = dtf.format((Date) val);
		}

		String text = null;
		if (val != null) {
			text = val.toString();
		}
		return text == null || text.equals("") ? "&#160;" : text;
	}
	
}
