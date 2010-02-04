package org.sakaiproject.gradebook.gwt.client.model;

public interface GradeEvent {

	public abstract String getIdentifier();

	public abstract void setIdentifier(String id);

	public abstract String getGraderName();

	public abstract void setGraderName(String graderName);

	public abstract String getGrade();

	public abstract void setGrade(String grade);

	public abstract String getDateGraded();

	public abstract void setDateGraded(String dateGraded);

}