package org.sakaiproject.gradebook.gwt.client.gxt.view.components;

import org.sakaiproject.gradebook.gwt.client.model.PermissionEntryModel;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;

public class PermissionDeleteCellRenderer implements GridCellRenderer<PermissionEntryModel> {

	public String render(PermissionEntryModel model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<PermissionEntryModel> store) {
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<span class=\"gbCellClickable\">");
		stringBuilder.append(model.get(property));
		stringBuilder.append("</span>");
		return stringBuilder.toString();
	}
}
