package org.sakaiproject.gradebook.gwt.client.gxt.a11y;

import com.extjs.gxt.ui.client.widget.TabPanel;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Accessibility;

public class AriaTabPanel extends TabPanel {

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		Accessibility.setRole(el().dom, Accessibility.ROLE_TABPANEL);
	}
	
}
