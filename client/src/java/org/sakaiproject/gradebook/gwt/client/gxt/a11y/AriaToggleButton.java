package org.sakaiproject.gradebook.gwt.client.gxt.a11y;

import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Accessibility;


public class AriaToggleButton extends ToggleButton {
	
	private char accessKey; 
	
	public AriaToggleButton(String text) { 
		super(text);
	}
	
	public AriaToggleButton(String text, SelectionListener listener) { 
		super(text, listener);
	}
	
	public AriaToggleButton(String text, SelectionListener listener, char accessKey) {
		this(text, listener);
		this.accessKey = accessKey;
	}

	protected void onRender(Element target, int index) { 
		super.onRender(target, index);
		
		Accessibility.setRole(el().dom, Accessibility.ROLE_BUTTON); 
		
		getElement().setAttribute("accesskey", String.valueOf(accessKey));
	}
}
