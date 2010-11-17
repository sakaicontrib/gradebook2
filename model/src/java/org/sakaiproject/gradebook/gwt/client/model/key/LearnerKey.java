/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model.key;

import org.sakaiproject.gradebook.gwt.client.model.type.GroupType;

public enum LearnerKey { 
	S_UID("learnerUid", GroupType.STUDENT_INFORMATION), 
	S_EID("learnerEid", GroupType.STUDENT_INFORMATION),
	S_DSPLY_ID("displayId", GroupType.STUDENT_INFORMATION), 
	S_DSPLY_NM("displayName", GroupType.STUDENT_INFORMATION), 
	S_LST_NM_FRST("lastNameFirst", GroupType.STUDENT_INFORMATION),
	S_EMAIL("email", GroupType.STUDENT_INFORMATION), 
	S_SECT("section", GroupType.STUDENT_INFORMATION), 
	S_CRS_GRD("courseGrade", GroupType.GRADES), 
	S_LTR_GRD("letterGrade", GroupType.GRADES),
	S_CALC_GRD("calculatedGrade", GroupType.GRADES),
	S_RAW_GRD("fullPrecisionCalculatedGrade", GroupType.GRADES),
	S_OVRD_GRD("overrideGrade", GroupType.GRADES), 
	S_ITEM("item", GroupType.ASSIGNMENTS),
	S_EXPRT_CM_ID("exportCMId", GroupType.STUDENT_INFORMATION),
	S_EXPRT_USR_ID("exportUserId", GroupType.STUDENT_INFORMATION),
	S_FNL_GRD_ID("finalGradeId", GroupType.STUDENT_INFORMATION),
	B_GRD_OVRDN("isGradeOverridden", GroupType.GRADES),
	B_USR_NT_FD("isUserNotFound", GroupType.STUDENT_INFORMATION),
	S_DSPLY_CM_ID("displayCMId", GroupType.STUDENT_INFORMATION);

	private String property;
	private GroupType group;

	private LearnerKey(String property, GroupType group) {
		this.property = property;
		this.group = group;
	}

	public GroupType getGroup() {
		return group;
	}

	public String getProperty() {
		return property;
	}
	
}
