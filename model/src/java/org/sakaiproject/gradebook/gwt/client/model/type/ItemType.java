/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model.type;

public enum ItemType { ROOT("Root"), GRADEBOOK("Gradebook") , CATEGORY("Category"), ITEM("Item"), COMMENT("Comment");

	private String name;
	
	private ItemType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}