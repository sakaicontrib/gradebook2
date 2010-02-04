package org.sakaiproject.gradebook.gwt.client.model.key;

import java.util.ArrayList;

import org.sakaiproject.gradebook.gwt.client.model.Configuration;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.Learner;

public enum GradebookKey {
	GRADEBOOKUID(String.class), GRADEBOOKID(Long.class), NAME(String.class), 
	USERASSTUDENT(Learner.class),
	COLUMNS(ArrayList.class), USERNAME(String.class), 
	GRADEBOOKITEMMODEL(Item.class), ISNEWGRADEBOOK(Boolean.class),
	CONFIGURATIONMODEL(Configuration.class), STATSMODELS(ArrayList.class);

	Class<?> type;

	private GradebookKey(Class<?> type) {
		this.type = type;
	}
	
	public Class<?> getType() {
		return type;
	}
	
}
