package org.sakaiproject.gradebook.gwt.client.gxt.view;

public class TabConfig {

	public String id;
	public String header;
	public boolean isClosable;
	public String menuItemId;
	
	public TabConfig(String id, String header, boolean isClosable) {
		this.id = id;
		this.header = header;
		this.isClosable = isClosable;
	}
	
}
