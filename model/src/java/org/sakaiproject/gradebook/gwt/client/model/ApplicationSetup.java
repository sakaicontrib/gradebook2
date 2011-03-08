package org.sakaiproject.gradebook.gwt.client.model;

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;

public interface ApplicationSetup {

	public abstract List<Gradebook> getGradebookModels();

	public abstract void setGradebookModels(List<Gradebook> gradebookModels);

	public abstract String getPlacementId();

	public abstract void setPlacementId(String placementId);

	public abstract String getIdentifier();

	public abstract String getHelpUrl();

	public abstract void setHelpUrl(String helpUrl);

	/**
	 * Does system config setting enable finding learners by user-id, email address, 
	 * last-name-first, etc, when using the search form in the instructor's view of grades?
	 * @return true if search-by-field is enabled, false otherwise.
	 */
	public abstract boolean isSearchRosterByFieldEnabled();

	/**
	 * @param isEnabled
	 */
	public abstract void setSearchRosterByFieldEnabled(boolean isEnabled);

	public abstract List<GradeType> getEnabledGradeTypes();

	public abstract void setEnabledGradeTypes(List<GradeType> enabledGradeTypes);

	public abstract boolean isShowWeightedEnabled();

	public abstract void setShowWeightedEnabled(boolean isShowWeightedEnabled);

}