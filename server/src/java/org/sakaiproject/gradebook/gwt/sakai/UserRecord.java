/**********************************************************************************
 *
 * $Id:$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2008, 2009 The Regents of the University of California
 *
 * Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 **********************************************************************************/
package org.sakaiproject.gradebook.gwt.sakai;

import java.math.BigDecimal;
import java.util.Map;

import org.sakaiproject.tool.gradebook.AssignmentGradeRecord;
import org.sakaiproject.tool.gradebook.Comment;
import org.sakaiproject.tool.gradebook.CourseGradeRecord;
import org.sakaiproject.user.api.User;

public class UserRecord {

	private String userUid;
	private String userEid;
	private String displayId;
	private String displayName;
	private String lastNameFirst;
	private String sortName;
	private String email;
	private String sectionTitle;
	private boolean isPopulated;
	private boolean isCalculated;
	private CourseGradeRecord courseGradeRecord;
	private Map<Long, AssignmentGradeRecord> gradeRecordMap;
	private Map<Long, Comment> commentMap;
	private DisplayGrade displayGrade;
	private BigDecimal calculatedGrade;
	private String exportCourseManagementId;
	private String displayCourseManagementId = null;
	
	private String exportUserId;
	private String finalGradeUserId;

	public UserRecord(String userUid) {
		this.userUid = userUid;
		this.isPopulated = false;
	}

	public UserRecord(User user) {
		populate(user);
	}

	public UserRecord(String userUid, String userEid, String displayId, String displayName, String lastNameFirst, String sortName,
			String email) {
		this.userUid = userUid;
		this.userEid = userEid;
		this.displayId = displayId;
		this.displayName = displayName;
		this.lastNameFirst = lastNameFirst;
		this.sortName = sortName;
		this.email = email;
		this.isPopulated = true;
	}

	public void populate(User user) {
		String lastName = user.getLastName() == null ? "" : user.getLastName();
		String firstName = user.getFirstName() == null ? "" : user.getFirstName();

		String sortName = new StringBuilder().append(lastName.toUpperCase()).append(firstName.toUpperCase()).toString();
		String lastNameFirst = new StringBuilder().append(lastName).append(", ").append(firstName).toString();

		this.userUid = user.getId();
		this.userEid = user.getEid();
		this.displayId = user.getDisplayId();
		this.displayName = user.getDisplayName();
		this.lastNameFirst = lastNameFirst;
		this.sortName = sortName;
		this.email = user.getEmail();
		this.isPopulated = true;
	}

	public String getUserUid() {
		return userUid;
	}

	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}

	public String getDisplayId() {
		return displayId;
	}

	public void setDisplayId(String displayId) {
		this.displayId = displayId;
	}

	public String getDisplayName() {

		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getSortName() {

		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSectionTitle() {
		return sectionTitle;
	}

	public void setSectionTitle(String sectionTitle) {
		this.sectionTitle = sectionTitle;
	}

	public CourseGradeRecord getCourseGradeRecord() {
		return courseGradeRecord;
	}

	public void setCourseGradeRecord(CourseGradeRecord courseGradeRecord) {
		this.courseGradeRecord = courseGradeRecord;
	}

	public boolean isPopulated() {
		return isPopulated;
	}

	public Map<Long, AssignmentGradeRecord> getGradeRecordMap() {
		return gradeRecordMap;
	}

	public void setGradeRecordMap(Map<Long, AssignmentGradeRecord> gradeRecordMap) {
		this.gradeRecordMap = gradeRecordMap;
	}

	public DisplayGrade getDisplayGrade() {
		return displayGrade;
	}

	public void setDisplayGrade(DisplayGrade displayGrade) {
		this.displayGrade = displayGrade;
	}

	public boolean isCalculated() {
		return isCalculated;
	}

	public void setCalculated(boolean isCalculated) {
		this.isCalculated = isCalculated;
	}

	public Map<Long, Comment> getCommentMap() {
		return commentMap;
	}

	public void setCommentMap(Map<Long, Comment> commentMap) {
		this.commentMap = commentMap;
	}

	public String getUserEid() {
		return userEid;
	}

	public void setUserEid(String userEid) {
		this.userEid = userEid;
	}

	public String getExportCourseManagementId() {
		return exportCourseManagementId;
	}

	public void setExportCourseManagementId(String exportCourseManagementId) {
		this.exportCourseManagementId = exportCourseManagementId;
	}

	public String getExportUserId() {
		return exportUserId;
	}

	public void setExportUserId(String exportUserId) {
		this.exportUserId = exportUserId;
	}

	public String getLastNameFirst() {
		return lastNameFirst;
	}

	public void setLastNameFirst(String lastNameFirst) {
		this.lastNameFirst = lastNameFirst;
	}

	public String getFinalGradeUserId() {
		return finalGradeUserId;
	}

	public void setFinalGradeUserId(String finalGradeUserId) {
		this.finalGradeUserId = finalGradeUserId;
	}

	public BigDecimal getCalculatedGrade() {
		return calculatedGrade;
	}

	public void setCalculatedGrade(BigDecimal calculatedGrade) {
		this.calculatedGrade = calculatedGrade;
	}
	
	public String getDisplayCourseManagementId() {
		return displayCourseManagementId;
	}

	public void setDisplayCourseManagementId(String displayCourseManagementId) {
		this.displayCourseManagementId = displayCourseManagementId;
	}

	
}
