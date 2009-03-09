package org.sakaiproject.gradebook.gwt.client.gxt.a11y;

import com.extjs.gxt.ui.client.widget.menu.AdapterMenuItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.Widget;

public class AriaAdapterMenuItem extends AdapterMenuItem {

	public AriaAdapterMenuItem(Widget widget) {
		super(widget);
	}

	@Override
	protected void onRender(Element target, int index) {
		super.onRender(target, index);
		Accessibility.setRole(el().dom, Accessibility.ROLE_MENUITEM);
	}
	
}
