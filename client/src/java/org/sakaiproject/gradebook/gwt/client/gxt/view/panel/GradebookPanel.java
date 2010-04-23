package org.sakaiproject.gradebook.gwt.client.gxt.view.panel;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.I18nMessages;
import org.sakaiproject.gradebook.gwt.client.resource.GradebookResources;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Text;

public abstract class GradebookPanel extends ContentPanel {

	protected final I18nConstants i18n;
	protected final I18nMessages i18nTemplates;
	protected final GradebookResources resources;
	
	public GradebookPanel() {
		super();
		this.i18n = Registry.get(AppConstants.I18N);
		this.i18nTemplates = Registry.get(AppConstants.I18N_TEMPLATES);
		this.resources = Registry.get(AppConstants.RESOURCES);
		
		if (isAdviceImplemented()) {
			Text text = new Text(getAdvice());
			text.addStyleName(resources.css().gbAdvice());
			setBottomComponent(text);
		}
	}
	
	protected String getAdvice() {
		return "";
	}
	
	protected boolean isAdviceImplemented() {
		return false;
	}
	
}
