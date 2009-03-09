package org.sakaiproject.gradebook.gwt.client.gxt.a11y;

import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Accessibility;

public class AriaTree extends Tree {

	@Override
	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		Accessibility.setRole(el().dom, "tree");
		Accessibility.setState(el().dom, "aria-labelledby", "itemtreelabel");
	}
	
}
