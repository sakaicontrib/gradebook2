package org.sakaiproject.gradebook.gwt.client.gxt.multigrade;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.table.CellRenderer;

public class CheckBoxCellRenderer implements CellRenderer {
	
	public String render(Component item, String property, Object value) {
		if (value == null)
			return "";
				
		Boolean v = (Boolean)value;
		String on = v != null && v.booleanValue() ? "-on" : "";
		
		return new StringBuilder().append("<div class='x-grid3-check-col").append(on).append(" x-grid3-cc-").append(property).append("'>&#160;</div>").toString();
	}

}
