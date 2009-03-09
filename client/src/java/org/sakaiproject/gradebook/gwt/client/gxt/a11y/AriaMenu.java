package org.sakaiproject.gradebook.gwt.client.gxt.a11y;

import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Accessibility;

public class AriaMenu extends Menu {

	protected void onRender(Element parent, int pos) {
	    super.onRender(parent, pos);
	    Accessibility.setRole(el().dom, "menu");
	}
	
}
