package org.sakaiproject.gradebook.gwt.client.model;

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.BusinessLogicCode;

public interface ImportSettings {

	public abstract Boolean isScantron();

	public abstract void setScantron(boolean scantron);

	public abstract Boolean isForceOverwriteAssignments();

	public abstract void setForceOverwriteAssignments(
			boolean forceOverwriteAssignments);

	public abstract String getScantronMaxPoints();

	public abstract void setScantronMaxPoints(String scantronMaxPoints);
	
	public List<BusinessLogicCode> getIgnoredBusinessRules();
	
	public void setIgnoredBusinessRules(List<BusinessLogicCode> rules);
	
	public Boolean isJustStructure();
	
	public void setJustStructure (Boolean yes);

}