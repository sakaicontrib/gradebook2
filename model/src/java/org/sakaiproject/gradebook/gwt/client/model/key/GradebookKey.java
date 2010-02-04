package org.sakaiproject.gradebook.gwt.client.model.key;

import java.util.ArrayList;

import org.sakaiproject.gradebook.gwt.client.model.Configuration;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.Learner;

public enum GradebookKey {
	GRADEBOOKUID,
	GRADEBOOKID(Long.class), 
	NAME,
	GRADEBOOKITEMMODEL(Item.class),
	COLUMNS(ArrayList.class), 
	USERNAME,  
	ISNEWGRADEBOOK(Boolean.class),
	CONFIGURATIONMODEL(Configuration.class), 
	STATSMODELS(ArrayList.class),
	USERASSTUDENT(Learner.class);

	Class<?> type;

	private GradebookKey() {
		this.type = null;
	}
	
	private GradebookKey(Class<?> type) {
		this.type = type;
	}
	
	public Class<?> getType() {
		return type;
	}
	
}
