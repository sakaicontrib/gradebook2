package org.sakaiproject.gradebook.gwt.client.gxt.view.components;

import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;

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
		
		setDisplayProperty(ItemModel.Key.NAME.name());
		setAutoLoad(true);
	}

	@Override
	protected TreeItem createItem(ItemModel model) {
		int cols = treeTable.getColumnCount();
	    TreeTableItem item = new TreeTableItem(new Object[cols]) {
	    	@Override
	    	protected TreeItemUI getTreeItemUI() {
	    	    TreeTableItemUI itemUI = new TreeTableItemUI(this) {
	    	    	protected void handleClickEvent(TreeEvent te) {
					    TreeItem item = te.item;
					    if (te.type == Event.ONCLICK) {
					      Element target = te.getTarget();
					      if (target != null && te.within(item.getUI().getJointEl())) {
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
	    //update(model);
	    //updateItemValues(item);
	    //updateItemStyles(item);

	    String txt = getTextValue(model, displayProperty);
	    if (txt == null && displayProperty != null) {
	      txt = model.get(displayProperty);
	    } else {
	      txt = model.toString();
	    }

	    String icon = getIconValue(model, displayProperty);

	    item.setIconStyle(icon);
	    item.setText(txt);
		
		//TreeItem item = super.createItem(model);

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
