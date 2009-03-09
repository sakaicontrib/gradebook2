package org.sakaiproject.gradebook.gwt.client.gxt.a11y;

import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Accessibility;

public class AriaMenuItem extends MenuItem {

	public AriaMenuItem() {
		super();
	}
	
	public AriaMenuItem(String text) {
		super(text);
	}
	
	public AriaMenuItem(String text, SelectionListener listener) {
		super(text, listener);
	}
	
	@Override
	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		Accessibility.setRole(el().dom, Accessibility.ROLE_MENUITEM);
	}
	
}
