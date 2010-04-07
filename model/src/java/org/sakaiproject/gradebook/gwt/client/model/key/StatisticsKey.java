/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model.key;

public enum StatisticsKey { 
	S_ID("id"), 
	S_NM("name"), 
	S_MEAN("mean"), 
	S_MEDIAN("median"),
	S_MODE("mode"), 
	S_STD_DEV("standardDeviation"), 
	S_ITEM_ID("itemId"), 
	S_RANK("rank");
	
	private String property;
	
	private StatisticsKey(String property) {
		this.property = property;
	}
	
	public String toString() {
		return property;
	}

}