package org.sakaiproject.gradebook.gwt.client.model;

import java.util.ArrayList;

public enum GradebookKey {
	GRADEBOOKUID(String.class), GRADEBOOKID(Long.class), NAME(String.class), 
	USERASSTUDENT(StudentModel.class),
	COLUMNS(ArrayList.class), USERNAME(String.class), 
	GRADEBOOKITEMMODEL(ItemModel.class), ISNEWGRADEBOOK(Boolean.class),
	CONFIGURATIONMODEL(ConfigurationModel.class), STATSMODELS(ArrayList.class);

	Class<?> type;

	private GradebookKey(Class<?> type) {
		this.type = type;
	}
	
	public Class<?> getType() {
		return type;
	}
	
}
