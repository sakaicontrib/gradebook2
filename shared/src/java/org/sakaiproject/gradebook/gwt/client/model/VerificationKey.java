/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model;

public enum VerificationKey { 
	NUMBER_LEARNERS(Integer.class), IS_MISSING_SCORES, IS_FULLY_WEIGHTED;
	
	private Class<?> type;
	
	private VerificationKey() {
		
	}
	
	private VerificationKey(Class<?> type) {
		this.type = type;
	}
	
	public Class<?> getType() {
		return type;
	}
}

