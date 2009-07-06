package org.sakaiproject.gradebook.gwt.client.gxt.view.components;

import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;

import com.extjs.gxt.ui.client.widget.table.CellRenderer;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;

public class ItemCellRenderer implements CellRenderer<TreeItem> {

	
	public String render(TreeItem item, String property, Object value) {
		String prefix = "";
		String result = null;
		ItemModel itemModel = (ItemModel)item.getModel();
		
		ItemModel parent = itemModel.getParent();
		
		boolean isItem = itemModel.getItemType() == Type.ITEM;
		boolean isName = property.equals(ItemModel.Key.NAME.name());
		boolean isIncluded = itemModel.getIncluded() == null || itemModel.getIncluded().booleanValue();		
		boolean isExtraCredit = itemModel.getExtraCredit() != null && itemModel.getExtraCredit().booleanValue();
		boolean isReleased = itemModel.getReleased() != null && itemModel.getReleased().booleanValue();
		
		if (value == null)
			return null;
		
		result = (String)value;
		
		StringBuilder cssClasses = new StringBuilder();
		
		if (isName) {
			if (!isIncluded) {
				cssClasses.append("gbNotIncluded");
				if (isItem)
					item.setIconStyle("gbItemIcon");
			} else if (isItem)
				item.setIconStyle("gbEditItemIcon");
		}
		
		if (isExtraCredit) 
			cssClasses.append(" gbCellExtraCredit");
		
		if (isReleased)
			cssClasses.append(" gbReleased");
		
		return new StringBuilder().append("<span class=\"").append(cssClasses.toString())
			.append("\">").append(prefix).append(result).append("</span>").toString();
	}
	
}
