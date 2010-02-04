package org.sakaiproject.gradebook.gwt.client.model.key;

public enum GradeFormatKey { ID(Long.class), NAME(String.class);

	private Class<?> type;
	
	private GradeFormatKey() {
		
	}
	
	private GradeFormatKey(Class<?> type) {
		this.type = type;
	}
	
	public Class<?> getType() {
		return type;
	}

}