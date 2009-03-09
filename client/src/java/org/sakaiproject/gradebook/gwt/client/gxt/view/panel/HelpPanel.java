package org.sakaiproject.gradebook.gwt.client.gxt.view.panel;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class HelpPanel extends ContentPanel {

	public HelpPanel() {
		super();
		baseStyle = "gbHelpPanel";
		setHeaderVisible(false);
		setLayout(new FitLayout());
		setStylePrimaryName("gbHelpPanel");
		
		I18nConstants i18n = Registry.get(AppConstants.I18N);
		Html text = addText(i18n.helpHtml());
		text.setStyleAttribute("background-color", "yellow");
		
		addButton(new Button(i18n.close(), new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {  
				Dispatcher.forwardEvent(GradebookEvents.HideEastPanel, Boolean.FALSE);
			}
			
		}));
	}
	
}
