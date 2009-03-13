package org.sakaiproject.gradebook.gwt.client.gxt.view.panel;

import org.sakaiproject.gradebook.gwt.client.model.GradeEventModel;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.RootPanel;

public class NotificationPanel extends ContentPanel {

	public NotificationPanel() {
		super();
		baseStyle = "gbNotificationPanel";
		setFrame(false);
		setHeaderVisible(false);
		setLayout(new FitLayout());
	}
	
	
	public void onShowNotification(int x, int y) {
		RootPanel.get().add(this);
		el().makePositionable(true);

		el().setLeftTop(x, y);
		setSize(700, 50);
		//setSize(config.width, config.height);

		//blur();
		//if (!config.isPermanent)
		//	afterShow();
	}
	
}
