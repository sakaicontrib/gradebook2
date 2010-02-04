/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model.key;

import org.sakaiproject.gradebook.gwt.client.model.type.GroupType;

public enum LearnerKey { 
	UID(GroupType.STUDENT_INFORMATION), 
	EID(GroupType.STUDENT_INFORMATION),
	DISPLAY_ID(GroupType.STUDENT_INFORMATION), 
	DISPLAY_NAME(GroupType.STUDENT_INFORMATION), 
	LAST_NAME_FIRST(GroupType.STUDENT_INFORMATION),
	EMAIL(GroupType.STUDENT_INFORMATION), 
	SECTION(GroupType.STUDENT_INFORMATION), 
	COURSE_GRADE(GroupType.GRADES), 
	LETTER_GRADE(GroupType.GRADES),
	CALCULATED_GRADE(GroupType.GRADES),
	GRADE_OVERRIDE(GroupType.GRADES), 
	ASSIGNMENT(GroupType.ASSIGNMENTS),
	EXPORT_CM_ID(GroupType.STUDENT_INFORMATION),
	EXPORT_USER_ID(GroupType.STUDENT_INFORMATION),
	FINAL_GRADE_USER_ID(GroupType.STUDENT_INFORMATION),
	IS_GRADE_OVERRIDDEN(GroupType.GRADES);

	private GroupType group;

	private LearnerKey(GroupType group) {
		this.group = group;
	}

	public GroupType getGroup() {
		return group;
	}

}