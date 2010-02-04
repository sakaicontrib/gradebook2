/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model.type;

public enum EntityType { AUTH("authorization"), APPLICATION("application"), GRADE_ITEM("grade item"),
	CATEGORY("category"), COLUMN("column"), COMMENT("comment"), CONFIGURATION("configuration"), CATEGORY_NOT_REMOVED("category not removed"),
	GRADEBOOK("gradebook"), GRADE_SCALE("grade scale"), COURSE_GRADE_RECORD("course grade record"), GRADE_RECORD("grade record"), 
	GRADE_EVENT("grade event"), USER("user"), PERMISSION_ENTRY("permission entry"),
	SECTION("section"), PERMISSION_SECTIONS("permission sections"), LEARNER("learner"), LEARNER_ID("learner id"), ACTION("action"), ITEM("item"),
	SPREADSHEET("spreadsheet"), SUBMISSION_VERIFICATION("submission verification"), 
	STATISTICS("statistics"), GRADE_FORMAT("grade format"), GRADE_SUBMISSION("grade submission");

private String name;

private EntityType(String name) {
	this.name = name;
}

@Override

public String toString() {
	return name;
}

}