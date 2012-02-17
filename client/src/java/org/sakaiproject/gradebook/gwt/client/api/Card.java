package org.sakaiproject.gradebook.gwt.client.api;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.FormPanel;

public interface Card {
	
	public I18nConstants i18n = Registry.get(AppConstants.I18N);

	public void setHtmlText(String html);

	public void setTitle(String string);

	public void setFormPanel(FormPanel formpanel);

	public void addFinishListener(Listener<BaseEvent> listener);
	
	public void addCardCloseListener(Listener<BaseEvent> listener);

	public boolean isValid();

	public void notifyFinishListeners();
	
	public void notifyCardCloseListeners();
	
	public FormPanel getFormPanel();
	
	/*
	 *  NOTE: Using a LayoutContainer instead of a form means you forfeit meaningful use of 
	 *  panel.isValid() for button presses.
	 *  
	 */
	
	public void setLayoutContainer(LayoutContainer container);
	
	public LayoutContainer getLayoutContainer();
	
}
