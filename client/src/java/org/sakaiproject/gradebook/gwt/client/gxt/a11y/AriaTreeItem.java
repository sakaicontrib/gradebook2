package org.sakaiproject.gradebook.gwt.client.gxt.a11y;

import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Accessibility;

public class AriaTreeItem extends TreeItem {

	@Override
	public void setExpanded(boolean expanded) {
		super.setExpanded(expanded);
		
		if (rendered)
			Accessibility.setState(el().dom, "aria-expanded", String.valueOf(expanded));
	}
	
	@Override
	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		Accessibility.setRole(el().dom, "treeitem");
		Accessibility.setState(el().dom, "aria-expanded", "false");
	}
	
}
