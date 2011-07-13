package org.sakaiproject.gradebook.gwt.client.model;

import java.util.Collection;
import java.util.Map;

public interface Learner {

	public abstract <X> X get(String property);
	
	public abstract <X> X set(String property, X value);
	
	public abstract Map<String, Object> getProperties();
	
	public abstract String getIdentifier();

	public abstract void setIdentifier(String id);

	public abstract String getEid();

	public abstract void setEid(String eid);

	public abstract String getDisplayName();

	public abstract String getLastNameFirst();

	public abstract void setLastNameFirst(String name);

	public abstract String getStudentName();

	public abstract void setStudentName(String studentName);

	public abstract String getStudentDisplayId();

	public abstract void setStudentDisplayId(String studentDisplayId);

	public abstract String getStudentEmail();

	public abstract void setStudentEmail(String studentEmail);

	public abstract String getStudentSections();

	public abstract void setStudentSections(String studentSections);

	public abstract String getStudentGrade();

	public abstract void setStudentGrade(String studentGrade);

	public abstract String getCalculatedGrade();

	public abstract void setCalculatedGrade(String calculatedGrade);

	public abstract String getLetterGrade();

	public abstract void setLetterGrade(String letterGrade);

	public abstract String getExportCmId();

	public abstract void setExportCmId(String exportCmId);

	public abstract String getExportUserId();

	public abstract void setExportUserId(String exportUserId);

	public abstract String getFinalGradeUserId();

	public abstract void setFinalGradeUserId(String finalGradeUserId);

	public abstract Boolean getUserNotFound();
	
	public abstract void setUserNotFound(Boolean isNotFound);
	
	public Collection<String> getPropertyNames();
	
	
}