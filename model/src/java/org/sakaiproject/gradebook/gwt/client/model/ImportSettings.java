package org.sakaiproject.gradebook.gwt.client.model;

public interface ImportSettings {

	public abstract Boolean isScantron();

	public abstract void setScantron(boolean scantron);

	public abstract Boolean isForceOverwriteAssignments();

	public abstract void setForceOverwriteAssignments(
			boolean forceOverwriteAssignments);

	public abstract String getScantronMaxPoints();

	public abstract void setScantronMaxPoints(String scantronMaxPoints);

}