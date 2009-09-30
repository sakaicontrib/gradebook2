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
package org.sakaiproject.gradebook.gwt.client.gxt.custom.widget.grid;

import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;


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


	@Override
	protected String doRender(List<ColumnData> cs, List<ModelData> rows, int startRow, int colCount, boolean stripe) {

		int last = colCount - 1;

		String tstyle = new StringBuilder("width:").append(getTotalWidth()).append(";").toString();

		StringBuilder buf = new StringBuilder();
		StringBuilder cb = new StringBuilder();

		for (int j = 0; j < rows.size(); j++) {
			ModelData model = (ModelData) rows.get(j);
			Record r = ds.hasRecord(model) ? ds.getRecord(model) : null;

			int rowIndex = (j + startRow);

			for (int i = 0; i < colCount; i++) {
				ColumnData c = cs.get(i);
				c.css = c.css == null ? "" : c.css;
				String rv = getRenderedValue(c, rowIndex, i, model, c.name);

				StringBuilder css = new StringBuilder();
				if (i == 0)
					css.append("x-grid-cell-first ");
				else if (i == last)
					css.append("x-grid3-cell-last ");
				else
					css.append(" ");

				if (c.css != null)
					css.append(c.css);

				String attr = c.cellAttr != null ? c.cellAttr : "";
				String cellAttr = c.cellAttr != null ? c.cellAttr : "";

				StringBuilder cssBuilder = new StringBuilder().append(css.toString());
				StringBuilder innerCssClass = new StringBuilder().append("x-grid3-cell-inner x-grid3-col-").append(c.id);

				boolean isDirty = isShowDirtyCells();
				boolean isChanged = r != null && r.getChanges().containsKey(c.id);

				String markedUpCss = markupCss(r, model, c.id, isDirty, isChanged);
				String markedUpInnerCss = markupInnerCss(model, c.id, isDirty, isChanged);

				if (markedUpCss != null)
					cssBuilder.append(markedUpCss);

				if (markedUpInnerCss != null)
					innerCssClass.append(markedUpInnerCss);

				if (rv == null || rv.equals(""))
					rv = "&nbsp;";

				cb.append("<td class=\"x-grid3-col x-grid3-cell x-grid3-td-");
				cb.append(c.id);
				cb.append(" ");
				cb.append(cssBuilder.toString());
				cb.append("\" style=\"");
				cb.append(c.style);
				cb.append("\" tabIndex=0 ");
				cb.append(cellAttr);
				cb.append("><div class=\"").append(innerCssClass.toString()).append("\" ");
				cb.append(attr);
				cb.append(">");
				cb.append(rv);
				cb.append("</div></td>");

			}

			StringBuilder altBuffer = new StringBuilder();
			if (stripe && ((rowIndex + 1) % 2 == 0)) {
				altBuffer.append(" x-grid3-row-alt");
			}

			if (viewConfig != null) {
				altBuffer.append(" ").append(viewConfig.getRowStyle(model, rowIndex, ds));
			}

			buf.append("<div class=\"x-grid3-row ");
			buf.append(altBuffer.toString());
			buf.append("\" style=\"");
			buf.append(tstyle);
			buf.append("\"><table class=x-grid3-row-table border=0 cellspacing=0 cellpadding=0 style=\"");
			buf.append(tstyle);
			buf.append("\"><tbody><tr>");
			buf.append(cb.toString());
			buf.append("</tr>");

			if (enableRowBody) {
				buf.append("<tr class=x-grid3-row-body-tr style=\"\"><td colspan=")
				.append(colCount)
				.append(" class=x-grid3-body-cell tabIndex=0><div class=x-grid3-row-body>${body}</div></td></tr>");
			}

			buf.append("</tbody></table></div>");

			cb = new StringBuilder();
		}
		return buf.toString();
	}
	
	protected String getRenderedValue(ColumnData data, int rowIndex,
			int colIndex, ModelData m, String property) {
		GridCellRenderer r = cm.getRenderer(colIndex);
		if (r != null) {
			return (String) r.render(ds.getAt(rowIndex), property, data, rowIndex,
					colIndex, ds, grid);
		}
		Object val = m.get(property);

		ColumnConfig c = cm.getColumn(colIndex);

		if (val != null && c.getNumberFormat() != null && val instanceof Number) {
			Number n = (Number) val;
			NumberFormat nf = cm.getColumn(colIndex).getNumberFormat();
			val = nf.format(n.doubleValue());
		} else if (val != null && c.getDateTimeFormat() != null) {
			DateTimeFormat dtf = c.getDateTimeFormat();
			val = dtf.format((Date) val);
		}

		if (val != null) {
			return val.toString();
		}
		return "";
	}
}
