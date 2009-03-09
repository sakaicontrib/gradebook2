package org.sakaiproject.gradebook.gwt.client.gxt.a11y;

import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Accessibility;

public class AriaCheckMenuItem extends CheckMenuItem {

	public AriaCheckMenuItem() {
		super();
	}
	
	public AriaCheckMenuItem(String text) {
		super(text);
	}
	
	protected void onRender(Element parent, int pos) {
	    super.onRender(parent, pos);
	    Accessibility.setRole(el().dom, "menuitemcheckbox");
	}
	
}
