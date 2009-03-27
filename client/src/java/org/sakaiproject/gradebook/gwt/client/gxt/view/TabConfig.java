package org.sakaiproject.gradebook.gwt.client.gxt.view;

import org.sakaiproject.gradebook.gwt.client.gxt.view.InstructorView.MenuSelector;

public class TabConfig {

	public String id;
	public String header;
	public boolean isClosable;
	public String menuItemId;
	public MenuSelector menuSelector;
	public String iconStyle;
	
	public TabConfig(String id, String header, String iconStyle, boolean isClosable, MenuSelector menuSelector) {
		this.id = id;
		this.header = header;
		this.iconStyle = iconStyle;
		this.isClosable = isClosable;
		this.menuSelector = menuSelector;
	}
	
}
