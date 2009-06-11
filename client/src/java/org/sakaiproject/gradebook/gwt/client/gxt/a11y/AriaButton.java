package org.sakaiproject.gradebook.gwt.client.gxt.a11y;

import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Accessibility;

public class AriaButton extends Button {

	private char accessKey;
	
	public AriaButton(String text, SelectionListener listener) {
		super(text, listener);
	}
	
	public AriaButton(String text, SelectionListener listener, char accessKey) {
		this(text, listener);
		this.accessKey = accessKey;
	}
	
	protected void onRender(Element target, int index) {
	    super.onRender(target, index);
	 
	    Accessibility.setRole(el().dom, Accessibility.ROLE_BUTTON);
	
	    getElement().setAttribute("accesskey", String.valueOf(accessKey));
	}
	
}
