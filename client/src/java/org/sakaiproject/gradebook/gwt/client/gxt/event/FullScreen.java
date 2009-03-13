package org.sakaiproject.gradebook.gwt.client.gxt.event;

import org.sakaiproject.gradebook.gwt.client.gxt.view.AppView;

public class FullScreen {

	public AppView.EastCard eastCard;
	public boolean isFull;
	
	public FullScreen(AppView.EastCard eastCard, boolean isFull) {
		this.eastCard = eastCard;
		this.isFull = isFull;
	}
	
}
