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
import org.sakaiproject.gradebook.gwt.client.model.ItemKey;

import com.extjs.gxt.ui.client.binder.TreeTableBinder;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.TreeEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.extjs.gxt.ui.client.widget.tree.TreeItemUI;
import com.extjs.gxt.ui.client.widget.treetable.TreeTable;
import com.extjs.gxt.ui.client.widget.treetable.TreeTableItem;
import com.extjs.gxt.ui.client.widget.treetable.TreeTableItemUI;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

public class ItemTreeTableBinder extends TreeTableBinder<ItemModel> {


	public ItemTreeTableBinder(TreeTable treeTable, TreeStore<ItemModel> store) {
		super(treeTable, store);
		
		setDisplayProperty(ItemKey.NAME.name());
		setAutoLoad(true);
	}

	@Override
	protected TreeItem createItem(ItemModel model) {
		int cols = treeTable.getColumnCount();
	    TreeTableItem item = new TreeTableItem(new Object[cols]) {
	    	
	    	@Override
	    	protected void onRender(Element target, int index) {
	    		super.onRender(target, index);
	    		
	    		if (isRoot())
	    			getElement().setAttribute("accesskey", "g");
	    	}
	    	
	    	// This is overridden to prevent double click 
	    	@Override
	    	protected TreeItemUI getTreeItemUI() {
	    	    TreeTableItemUI itemUI = new TreeTableItemUI(this) {
	    	    	protected void handleClickEvent(TreeEvent te) {
					    TreeItem item = te.getItem();
					    if (te.getType().equals(Event.ONCLICK)) {
					      Element target = te.getTarget();
					      if (target != null && te.within(item.getUI().getJointElement())) {
					        item.toggle();
					      }
					      te.cancelBubble();
					    } 
					}
					
					@Override
					public void onClick(TreeEvent te) {
						te.cancelBubble();
					}
					@Override
					public void onDoubleClick(ComponentEvent ce) {
						ce.cancelBubble();
					}
	    	    };
	    	    return itemUI;
	    	}
	    };
	    setModel(item, model);
	    for (int j = 0; j < cols; j++) {
	      String id = getColumnId(j);
	      Object val = getTextValue(model, id);
	      if (val == null) val = model.get(id);
	      item.setValue(j, val);
	    }
	    for (int i = 0; i < cols; i++) {
	        String id = getColumnId(i);
	        String style = (styleProvider == null) ? null : styleProvider.getStringValue(model, id);
	        item.setCellStyle(i, style == null ? "" : style);
	    }

	    String txt = getTextValue(model, displayProperty);
	    if (txt == null && displayProperty != null) {
	      txt = model.get(displayProperty);
	    } else {
	      txt = model.toString();
	    }

	    String icon = getIconValue(model, displayProperty);

	    item.setIconStyle(icon);
	    item.setText(txt);
		
	    if (loader != null) {
	      item.setLeaf(!loader.hasChildren(model));
	    } else {
	      item.setLeaf(!hasChildren(model));
	    }

		switch (model.getItemType()) {
		case GRADEBOOK:
			item.setIconStyle("gbGradebookIcon");
			break;
		case CATEGORY:
			item.setLeaf(false);
			break;
		}
		
		return item;
	}
}
