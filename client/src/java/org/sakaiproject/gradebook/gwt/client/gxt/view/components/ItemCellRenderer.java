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

package org.sakaiproject.gradebook.gwt.client.gxt.view.components;

import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;

import com.extjs.gxt.ui.client.widget.table.CellRenderer;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;

public class ItemCellRenderer implements CellRenderer<TreeItem> {

	
	public String render(TreeItem item, String property, Object value) {
		String prefix = "";
		String suffix = "";
		String result = null;
		ItemModel itemModel = (ItemModel)item.getModel();
		
		boolean isCategory = itemModel.getItemType() == Type.CATEGORY;
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
		
		if (isCategory) {
			int dropLowest = itemModel.getDropLowest() == null ? 0 : itemModel.getDropLowest().intValue();
			
			if (dropLowest > 0)
				suffix = new StringBuilder(" <font style=\"font-size:8pt\">(-").append(dropLowest).append(")</font>").toString();
		}
		
		return new StringBuilder().append("<span class=\"").append(cssClasses.toString())
			.append("\">").append(prefix).append(result).append(suffix).append("</span>").toString();
	}
	
}
