package org.sakaiproject.gradebook.gwt.sakai.rest.model;

import java.util.HashMap;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.client.model.LearnerKey;

public class LearnerImpl extends HashMap<String,Object> implements Learner, Comparable<Learner> {

	private static final long serialVersionUID = 1L;

	public LearnerImpl(Map<String,Object> map) {
		super();
		putAll(map);
	}
	
	public <X> X get(String property) {
		return (X)super.get(property);
	}
	
	public Map<String, Object> getProperties() {
		return this;
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
		put(LearnerKey.UID.name(), id);
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
		put(LearnerKey.EID.name(), eid);
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
		put(LearnerKey.LAST_NAME_FIRST.name(), name);
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
		put(LearnerKey.DISPLAY_NAME.name(), studentName);
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
		put(LearnerKey.DISPLAY_ID.name(), studentDisplayId);
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
		put(LearnerKey.EMAIL.name(), studentEmail);
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
		put(LearnerKey.SECTION.name(), studentSections);
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
		put(LearnerKey.COURSE_GRADE.name(), studentGrade);
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
		put(LearnerKey.CALCULATED_GRADE.name(), calculatedGrade);
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
		put(LearnerKey.LETTER_GRADE.name(), letterGrade);
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
		put(LearnerKey.EXPORT_CM_ID.name(), exportCmId);
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
		put(LearnerKey.EXPORT_USER_ID.name(), exportUserId);
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
		put(LearnerKey.FINAL_GRADE_USER_ID.name(), finalGradeUserId);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Learner) {
			Learner other = (Learner)obj;
		
			return getIdentifier().equals(other.getIdentifier());
		}
		return false;
	}

	public int compareTo(Learner o) {
		return getIdentifier().compareTo(o.getIdentifier());
	}

}
