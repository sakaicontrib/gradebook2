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
	
	public LearnerModel(EntityOverlay obj) {
		super(obj);
	}
	
	public LearnerModel(Map<String, Object> properties) {
		super(properties);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getIdentifier()
	 */
	public String getIdentifier() {
		return get(LearnerKey.S_UID.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setIdentifier(java.lang.String)
	 */
	public void setIdentifier(String id) {
		set(LearnerKey.S_UID.name(), id);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getEid()
	 */
	public String getEid() {
		return get(LearnerKey.S_EID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setEid(java.lang.String)
	 */
	public void setEid(String eid) {
		set(LearnerKey.S_EID.name(), eid);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getDisplayName()
	 */
	public String getDisplayName() {
		return get(LearnerKey.S_DSPLY_NM.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getLastNameFirst()
	 */
	public String getLastNameFirst() {
		return get(LearnerKey.S_LST_NM_FRST.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setLastNameFirst(java.lang.String)
	 */
	public void setLastNameFirst(String name) {
		set(LearnerKey.S_LST_NM_FRST.name(), name);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getStudentName()
	 */
	public String getStudentName()
	{
		return get(LearnerKey.S_DSPLY_NM.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setStudentName(java.lang.String)
	 */
	public void setStudentName(String studentName)
	{
		set(LearnerKey.S_DSPLY_NM.name(), studentName);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getStudentDisplayId()
	 */
	public String getStudentDisplayId()
	{
		return get(LearnerKey.S_DSPLY_ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setStudentDisplayId(java.lang.String)
	 */
	public void setStudentDisplayId(String studentDisplayId)
	{
		set(LearnerKey.S_DSPLY_ID.name(), studentDisplayId);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getStudentEmail()
	 */
	public String getStudentEmail()
	{
		return get(LearnerKey.S_EMAIL.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setStudentEmail(java.lang.String)
	 */
	public void setStudentEmail(String studentEmail)
	{
		set(LearnerKey.S_EMAIL.name(), studentEmail);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getStudentSections()
	 */
	public String getStudentSections()
	{
		return get(LearnerKey.S_SECT.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setStudentSections(java.lang.String)
	 */
	public void setStudentSections(String studentSections)
	{
		set(LearnerKey.S_SECT.name(), studentSections);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getStudentGrade()
	 */
	public String getStudentGrade()
	{
		return get(LearnerKey.S_CRS_GRD.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setStudentGrade(java.lang.String)
	 */
	public void setStudentGrade(String studentGrade)
	{
		set(LearnerKey.S_CRS_GRD.name(), studentGrade);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getCalculatedGrade()
	 */
	public String getCalculatedGrade()
	{
		return get(LearnerKey.S_CALC_GRD.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setCalculatedGrade(java.lang.String)
	 */
	public void setCalculatedGrade(String calculatedGrade)
	{
		set(LearnerKey.S_CALC_GRD.name(), calculatedGrade);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getLetterGrade()
	 */
	public String getLetterGrade() {
		return get(LearnerKey.S_LTR_GRD.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setLetterGrade(java.lang.String)
	 */
	public void setLetterGrade(String letterGrade) {
		set(LearnerKey.S_LTR_GRD.name(), letterGrade);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getExportCmId()
	 */
	public String getExportCmId()
	{
		return get(LearnerKey.S_EXPRT_CM_ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setExportCmId(java.lang.String)
	 */
	public void setExportCmId(String exportCmId)
	{
		set(LearnerKey.S_EXPRT_CM_ID.name(), exportCmId);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getExportUserId()
	 */
	public String getExportUserId()
	{
		return get(LearnerKey.S_EXPRT_USR_ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setExportUserId(java.lang.String)
	 */
	public void setExportUserId(String exportUserId)
	{
		set(LearnerKey.S_EXPRT_USR_ID.name(), exportUserId);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#getFinalGradeUserId()
	 */
	public String getFinalGradeUserId() {
		return get(LearnerKey.S_FNL_GRD_ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Learner#setFinalGradeUserId(java.lang.String)
	 */
	public void setFinalGradeUserId(String finalGradeUserId) {
		set(LearnerKey.S_FNL_GRD_ID.name(), finalGradeUserId);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Learner) {
			Learner other = (Learner)obj;
			
			if(getIdentifier() == null) {
				if(other.getIdentifier() == null) {
					return true;
				}
				return false;
			}
		
			return getIdentifier().equals(other.getIdentifier());
		}
		return false;
	}


	@Override
	public int hashCode() {
		int rv = 1;
		int p = 24323;
		rv = p*rv + ((getIdentifier() == null?0:getIdentifier().hashCode()));
		
		return rv;
	}

	public int compareTo(LearnerModel o) {
		return getIdentifier().compareTo(o.getIdentifier());
	}

	public Boolean getUserNotFound() {
		return get(LearnerKey.B_USR_NT_FD.name());
	}

	public void setUserNotFound(Boolean isNotFound) {
		set(LearnerKey.B_USR_NT_FD.name(), isNotFound);
	}
	
}
