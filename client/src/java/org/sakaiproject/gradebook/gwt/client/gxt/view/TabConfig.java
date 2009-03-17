package org.sakaiproject.gradebook.gwt.client.gxt.view;

import org.sakaiproject.gradebook.gwt.client.gxt.view.InstructorView.MenuSelector;

public class TabConfig {

	public String id;
	public String header;
	public boolean isClosable;
	public String menuItemId;
	public MenuSelector menuSelector;
	
	public TabConfig(String id, String header, boolean isClosable, MenuSelector menuSelector) {
		this.id = id;
		this.header = header;
		this.isClosable = isClosable;
		this.menuSelector = menuSelector;
	}
	
}
