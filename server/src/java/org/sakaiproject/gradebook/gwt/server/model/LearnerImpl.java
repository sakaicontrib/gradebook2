package org.sakaiproject.gradebook.gwt.server.model;

import java.util.Collection;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;

public class LearnerImpl extends BaseModel implements Learner, Comparable<Learner> {

	private static final long serialVersionUID = 1L;

	public LearnerImpl(Map<String,Object> map) {
		super(map);
	}
	
	public LearnerImpl() {
		super();
	}

	public Map<String, Object> getProperties() {
		return this;
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
		put(LearnerKey.S_UID.name(), id);
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
		put(LearnerKey.S_EID.name(), eid);
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
		put(LearnerKey.S_LST_NM_FRST.name(), name);
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
		put(LearnerKey.S_DSPLY_NM.name(), studentName);
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
		put(LearnerKey.S_DSPLY_ID.name(), studentDisplayId);
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
		put(LearnerKey.S_EMAIL.name(), studentEmail);
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
		put(LearnerKey.S_SECT.name(), studentSections);
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
		put(LearnerKey.S_CRS_GRD.name(), studentGrade);
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
		put(LearnerKey.S_CALC_GRD.name(), calculatedGrade);
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
		put(LearnerKey.S_LTR_GRD.name(), letterGrade);
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
		put(LearnerKey.S_EXPRT_CM_ID.name(), exportCmId);
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
		put(LearnerKey.S_EXPRT_USR_ID.name(), exportUserId);
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
		put(LearnerKey.S_FNL_GRD_ID.name(), finalGradeUserId);
	}
	
	public Boolean getUserNotFound() {
		return get(LearnerKey.B_USR_NT_FD.name());
	}

	public void setUserNotFound(Boolean isNotFound) {
		put(LearnerKey.B_USR_NT_FD.name(), isNotFound);
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
	
	
	public int compareTo(Learner o) {
		return getIdentifier().compareTo(o.getIdentifier());
	}

	@Override
	public Collection<String> getPropertyNames() {
		return super.getPropertyNames();
	}

}
