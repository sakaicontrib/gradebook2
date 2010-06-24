/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model.type;

public enum ActionType { CREATE("Create", "Added "), 
	GET("Get", "retrieved"), 
	GRADED("Grade", "Set grade to"),
	UPDATE("Update", "Updated "), 
	DELETE("Delete", "Deleted "),
	SUBMITTED("Submit", "Submitted "),
	IMPORT_GRADE_CHANGE("File-Import", "Import score: ");
	

private String desc;
private String verb;

private ActionType(String desc, String verb) {
	this.desc = desc;
	this.verb = verb;
}

public String getVerb() {
	return verb;
}

@Override
public String toString() {
	return desc;
}

}
