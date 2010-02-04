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
package org.sakaiproject.gradebook.gwt.client.gxt.model;

import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;


public class LearnerModel extends EntityModel implements Comparable<LearnerModel>, Learner {
	
	private static final long serialVersionUID = 1L;

	public LearnerModel() {
		super();
	}
	
	public LearnerModel(Map<String, Object> properties) {
		super(properties);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getIdentifier()
	 */
	public String getIdentifier() {
		return get(LearnerKey.UID.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setIdentifier(java.lang.String)
	 */
	public void setIdentifier(String id) {
		set(LearnerKey.UID.name(), id);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getEid()
	 */
	public String getEid() {
		return get(LearnerKey.EID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setEid(java.lang.String)
	 */
	public void setEid(String eid) {
		set(LearnerKey.EID.name(), eid);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getDisplayName()
	 */
	public String getDisplayName() {
		return get(LearnerKey.DISPLAY_NAME.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getLastNameFirst()
	 */
	public String getLastNameFirst() {
		return get(LearnerKey.LAST_NAME_FIRST.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setLastNameFirst(java.lang.String)
	 */
	public void setLastNameFirst(String name) {
		set(LearnerKey.LAST_NAME_FIRST.name(), name);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getStudentName()
	 */
	public String getStudentName()
	{
		return get(LearnerKey.DISPLAY_NAME.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setStudentName(java.lang.String)
	 */
	public void setStudentName(String studentName)
	{
		set(LearnerKey.DISPLAY_NAME.name(), studentName);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getStudentDisplayId()
	 */
	public String getStudentDisplayId()
	{
		return get(LearnerKey.DISPLAY_ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setStudentDisplayId(java.lang.String)
	 */
	public void setStudentDisplayId(String studentDisplayId)
	{
		set(LearnerKey.DISPLAY_ID.name(), studentDisplayId);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getStudentEmail()
	 */
	public String getStudentEmail()
	{
		return get(LearnerKey.EMAIL.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setStudentEmail(java.lang.String)
	 */
	public void setStudentEmail(String studentEmail)
	{
		set(LearnerKey.EMAIL.name(), studentEmail);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getStudentSections()
	 */
	public String getStudentSections()
	{
		return get(LearnerKey.SECTION.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setStudentSections(java.lang.String)
	 */
	public void setStudentSections(String studentSections)
	{
		set(LearnerKey.SECTION.name(), studentSections);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getStudentGrade()
	 */
	public String getStudentGrade()
	{
		return get(LearnerKey.COURSE_GRADE.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setStudentGrade(java.lang.String)
	 */
	public void setStudentGrade(String studentGrade)
	{
		set(LearnerKey.COURSE_GRADE.name(), studentGrade);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getCalculatedGrade()
	 */
	public String getCalculatedGrade()
	{
		return get(LearnerKey.CALCULATED_GRADE.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setCalculatedGrade(java.lang.String)
	 */
	public void setCalculatedGrade(String calculatedGrade)
	{
		set(LearnerKey.CALCULATED_GRADE.name(), calculatedGrade);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getLetterGrade()
	 */
	public String getLetterGrade() {
		return get(LearnerKey.LETTER_GRADE.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setLetterGrade(java.lang.String)
	 */
	public void setLetterGrade(String letterGrade) {
		set(LearnerKey.LETTER_GRADE.name(), letterGrade);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getExportCmId()
	 */
	public String getExportCmId()
	{
		return get(LearnerKey.EXPORT_CM_ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setExportCmId(java.lang.String)
	 */
	public void setExportCmId(String exportCmId)
	{
		set(LearnerKey.EXPORT_CM_ID.name(), exportCmId);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getExportUserId()
	 */
	public String getExportUserId()
	{
		return get(LearnerKey.EXPORT_USER_ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setExportUserId(java.lang.String)
	 */
	public void setExportUserId(String exportUserId)
	{
		set(LearnerKey.EXPORT_USER_ID.name(), exportUserId);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getFinalGradeUserId()
	 */
	public String getFinalGradeUserId() {
		return get(LearnerKey.FINAL_GRADE_USER_ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setFinalGradeUserId(java.lang.String)
	 */
	public void setFinalGradeUserId(String finalGradeUserId) {
		set(LearnerKey.FINAL_GRADE_USER_ID.name(), finalGradeUserId);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LearnerModel) {
			Learner other = (Learner)obj;
		
			return getIdentifier().equals(other.getIdentifier());
		}
		return false;
	}

	public int compareTo(LearnerModel o) {
		return getIdentifier().compareTo(o.getIdentifier());
	}
	
}
