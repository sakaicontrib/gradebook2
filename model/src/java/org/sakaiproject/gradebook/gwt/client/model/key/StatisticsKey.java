/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model.key;

public enum StatisticsKey { 
	ID("id"), NAME("name"), MEAN("mean"), MEDIAN("median"),
	MODE("mode"), STANDARD_DEVIATION("standardDeviation"), 
	ASSIGN_ID("itemId"), RANK("rank");
	
	private String property;
	
	private StatisticsKey(String property) {
		this.property = property;
	}
	
	public String toString() {
		return property;
	}

}