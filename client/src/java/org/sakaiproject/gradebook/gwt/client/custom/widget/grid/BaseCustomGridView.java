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
package org.sakaiproject.gradebook.gwt.client.custom.widget.grid;

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.gxt.GridPanel;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.GridView;


public class BaseCustomGridView extends GridView {
	
	protected boolean isClickable(ModelData model, String property) {
		return false;
	}
	
	protected boolean isDropped(ModelData model, String property) {
		return false;
	}
	
	protected boolean isCommented(ModelData model, String property) {
		return false;
	}
	
	protected boolean isReleased(ModelData model, String property) {
		return false;
	}
	
	@Override
	protected String doRender(List<ColumnData> cs, List rows, int startRow,
			int colCount, boolean stripe) {
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
				String rv = getRenderedValue(c, rowIndex, i, model, c.id);

				StringBuilder css = new StringBuilder();
				
				if (i == 0)
					css.append("x-grid-cell-first ");
				else if (i == last)
					css.append("x-grid3-cell-last ");
				else
					css.append(" ");
				
				if (c.css != null)
					css.append(c.css);
				
				//String css = (i == 0 ? "x-grid-cell-first "
				//		: (i == last ? "x-grid3-cell-last " : " "))
				//		+ " " + (c.css == null ? "" : c.css);
				String attr = c.cellAttr != null ? c.cellAttr : "";
				String cellAttr = c.cellAttr != null ? c.cellAttr : "";

				StringBuilder innerCssClass = new StringBuilder().append("x-grid3-cell-inner x-grid3-col-")
					.append(c.id);
				
				
				if (isShowDirtyCells() && r != null
						&& r.getChanges().containsKey(c.id)) {
					
					Object startValue = r.getChanges().get(c.id);
					Object currentValue = r.get(c.id);
					
					String failedProperty = new StringBuilder().append(c.id).append(GridPanel.FAILED_FLAG).toString();
					String failedMessage = (String)r.get(failedProperty);
					
					if (failedMessage != null) {
						css.append(" gbCellFailed");
					} else if (startValue == null || !startValue.equals(currentValue)) {
						css.append(" gbCellSucceeded");
					} 
					//css += " x-grid3-dirty-cell";
				}
				
				if (isCommented(model, c.id)) {
					innerCssClass.append(" gbCellCommented");
				} 
				
				if (isClickable(model, c.id)) {
					innerCssClass.append(" gbCellClickable");
				}
				
				if (isDropped(model, c.id)) {
					css.append(" gbCellDropped");
				}
				
				if (isReleased(model, c.id)) {
					css.append(" gbReleased");
				}
				
				if (rv == null || rv.equals(""))
					rv = "&nbsp;";

				cb.append("<td class=\"x-grid3-col x-grid3-cell x-grid3-td-").append(c.id).append(" ")
				  .append(css.toString()).append("\" style=\"").append(c.style)
				  .append("\" tabIndex=").append(j*i+1).append(" ")
				  .append(cellAttr).append(">");
				
				cb.append("<div class=\"").append(innerCssClass.toString()).append("\" ")
					.append(attr).append(">").append(rv).append("</div></td>");

			}

			StringBuilder altBuffer = new StringBuilder();
			if (stripe && ((rowIndex + 1) % 2 == 0)) {
				altBuffer.append(" x-grid3-row-alt");
			}

			if (isShowDirtyCells() && r != null && r.isDirty()) {
				altBuffer.append(" x-grid3-dirty-row");
			}

			if (viewConfig != null) {
				altBuffer.append(" ").append(viewConfig.getRowStyle(model, rowIndex, ds));
			}

			buf.append("<div class=\"x-grid3-row ")
				.append(altBuffer.toString()).append("\" style=\"")
				.append(tstyle)
				.append("\"><table class=x-grid3-row-table border=0 cellspacing=0 cellpadding=0 style=\"")
				.append(tstyle)
				.append("\"><tbody><tr>")
				.append(cb.toString()).append("</tr>");
			
			if (enableRowBody) {
				buf.append("<tr class=x-grid3-row-body-tr style=\"\"><td colspan=")
				   .append(colCount)
				   .append(" class=x-grid3-body-cell tabIndex=0><div class=x-grid3-row-body>${body}</div></td></tr>");
			} else {
			   	buf.append("</tbody></table></div>");
		    }
			cb = new StringBuilder();
		}
		return buf.toString();
	}
	

}
