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
package org.sakaiproject.gradebook.gwt.client.model;

import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction.ClassType;

public class StudentModel extends EntityModel implements Comparable<StudentModel> {
	
	public static final String COMMENTED_FLAG = ":C";
	public static final String COMMENT_TEXT_FLAG = ":T";
	public static final String DROP_FLAG = ":D";
	public static final String EXCUSE_FLAG = ":E";
	public static final String FAILED_FLAG = ":F";
	public static final String GRADED_FLAG = ":G";
	
	
	private static final long serialVersionUID = 1L;

	public StudentModel() {
		super();
	}
	
	public StudentModel(Map<String, Object> properties) {
		super(properties);
	}
	
	public static ClassType lookupClassType(String property, GradeType gradeType) {
		
		if (property.equals(LearnerKey.GRADE_OVERRIDE.name()))
			return ClassType.STRING;
		
		if (property.endsWith(COMMENT_TEXT_FLAG))
			return ClassType.STRING;
		
		if (property.endsWith(EXCUSE_FLAG))
			return ClassType.BOOLEAN;
		
		if (gradeType == GradeType.LETTERS)
			return ClassType.STRING;
		
		return ClassType.DOUBLE;
	}
	
	public String getIdentifier() {
		return get(LearnerKey.UID.name());
	}

	public void setIdentifier(String id) {
		set(LearnerKey.UID.name(), id);
	}
	
	public String getEid() {
		return get(LearnerKey.EID.name());
	}
	
	public void setEid(String eid) {
		set(LearnerKey.EID.name(), eid);
	}
	
	public String getDisplayName() {
		return get(LearnerKey.DISPLAY_NAME.name());
	}
	
	public String getLastNameFirst() {
		return get(LearnerKey.LAST_NAME_FIRST.name());
	}
	
	public void setLastNameFirst(String name) {
		set(LearnerKey.LAST_NAME_FIRST.name(), name);
	}
	
	public String getStudentName()
	{
		return get(LearnerKey.DISPLAY_NAME.name());
	}
	
	public void setStudentName(String studentName)
	{
		set(LearnerKey.DISPLAY_NAME.name(), studentName);
	}

	public String getStudentDisplayId()
	{
		return get(LearnerKey.DISPLAY_ID.name());
	}
	
	public void setStudentDisplayId(String studentDisplayId)
	{
		set(LearnerKey.DISPLAY_ID.name(), studentDisplayId);
	}
	
	public String getStudentEmail()
	{
		return get(LearnerKey.EMAIL.name());
	}
	
	public void setStudentEmail(String studentEmail)
	{
		set(LearnerKey.EMAIL.name(), studentEmail);
	}

	public String getStudentSections()
	{
		return get(LearnerKey.SECTION.name());
	}
	
	public void setStudentSections(String studentSections)
	{
		set(LearnerKey.SECTION.name(), studentSections);
	}

	public String getStudentGrade()
	{
		return get(LearnerKey.COURSE_GRADE.name());
	}
	
	public void setStudentGrade(String studentGrade)
	{
		set(LearnerKey.COURSE_GRADE.name(), studentGrade);
	}
	
	public String getCalculatedGrade()
	{
		return get(LearnerKey.CALCULATED_GRADE.name());
	}
	
	public void setCalculatedGrade(String calculatedGrade)
	{
		set(LearnerKey.CALCULATED_GRADE.name(), calculatedGrade);
	}
	
	public String getLetterGrade() {
		return get(LearnerKey.LETTER_GRADE.name());
	}
	
	public void setLetterGrade(String letterGrade) {
		set(LearnerKey.LETTER_GRADE.name(), letterGrade);
	}
	
	public String getExportCmId()
	{
		return get(LearnerKey.EXPORT_CM_ID.name());
	}
	
	public void setExportCmId(String exportCmId)
	{
		set(LearnerKey.EXPORT_CM_ID.name(), exportCmId);
	}
	
	public String getExportUserId()
	{
		return get(LearnerKey.EXPORT_USER_ID.name());
	}
	
	public void setExportUserId(String exportUserId)
	{
		set(LearnerKey.EXPORT_USER_ID.name(), exportUserId);
	}
	
	public String getFinalGradeUserId() {
		return get(LearnerKey.FINAL_GRADE_USER_ID.name());
	}
	
	public void setFinalGradeUserId(String finalGradeUserId) {
		set(LearnerKey.FINAL_GRADE_USER_ID.name(), finalGradeUserId);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StudentModel) {
			StudentModel other = (StudentModel)obj;
		
			return getIdentifier().equals(other.getIdentifier());
		}
		return false;
	}

	public int compareTo(StudentModel o) {
		return getIdentifier().compareTo(o.getIdentifier());
	}
	
}
