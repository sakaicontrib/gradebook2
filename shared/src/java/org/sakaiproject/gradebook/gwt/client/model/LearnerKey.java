/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model;

public enum LearnerKey { 
	UID(Group.STUDENT_INFORMATION), 
	EID(Group.STUDENT_INFORMATION),
	DISPLAY_ID(Group.STUDENT_INFORMATION), 
	DISPLAY_NAME(Group.STUDENT_INFORMATION), 
	LAST_NAME_FIRST(Group.STUDENT_INFORMATION),
	EMAIL(Group.STUDENT_INFORMATION), 
	SECTION(Group.STUDENT_INFORMATION), 
	COURSE_GRADE(Group.GRADES), 
	LETTER_GRADE(Group.GRADES),
	CALCULATED_GRADE(Group.GRADES),
	GRADE_OVERRIDE(Group.GRADES), 
	ASSIGNMENT(Group.ASSIGNMENTS),
	EXPORT_CM_ID(Group.STUDENT_INFORMATION),
	EXPORT_USER_ID(Group.STUDENT_INFORMATION),
	FINAL_GRADE_USER_ID(Group.STUDENT_INFORMATION),
	IS_GRADE_OVERRIDDEN(Group.GRADES);

	private Group group;

	private LearnerKey(Group group) {
		this.group = group;
	}

	public Group getGroup() {
		return group;
	}

}