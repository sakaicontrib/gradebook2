package org.sakaiproject.gradebook.gwt.client.gxt.view.components;

import com.extjs.gxt.ui.client.widget.treetable.TreeTable;
import com.extjs.gxt.ui.client.widget.treetable.TreeTableHeader;

public class ItemTreeTableHeader extends TreeTableHeader {

	public ItemTreeTableHeader(TreeTable treeTable) {
		super(treeTable);
	}
	
	public void hideColumn(int index) {
		super.showColumn(index, false);
	}
	
	public void showColumn(int index) {
		super.showColumn(index, true);
	}
	

}
