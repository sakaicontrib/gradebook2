package org.sakaiproject.gradebook.gwt.client.gxt.view.panel;

import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class NotificationPanel extends Window {

	public NotificationPanel() {
		super();
		//baseStyle = "gbNotificationPanel";
		setClosable(true);
		setFrame(false);
		setHeaderVisible(false);
		setLayout(new FitLayout());
		setShadow(true);
	}
	
	
	/*public void onShowNotification(int x, int y) {
		
		RootPanel.get().add(this);
		el().makePositionable(true);

		el().setLeftTop(x, y);
		setSize(700, 50);
		show();
		//setSize(config.width, config.height);

		//blur();
		//if (!config.isPermanent)
		//	afterShow();
	}*/
	
}
