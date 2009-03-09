package org.sakaiproject.gradebook.gwt.client.gxt.a11y;

import com.extjs.gxt.ui.client.widget.TabItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Accessibility;

public class AriaTabItem extends TabItem {

	public AriaTabItem() {
		super();
	}
	
	public AriaTabItem(String text) {
		super(text);
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		Accessibility.setRole(el().dom, Accessibility.ROLE_TAB);
		Accessibility.setState(el().dom, "aria-controls", getTabPanel().el().getId());
	}
	
}
