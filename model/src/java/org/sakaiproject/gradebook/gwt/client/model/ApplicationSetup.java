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

	public abstract List<GradeType> getEnabledGradeTypes();

	public abstract void setEnabledGradeTypes(List<GradeType> enabledGradeTypes);

	public abstract boolean isShowWeightedEnabled();

	public abstract void setShowWeightedEnabled(boolean isShowWeightedEnabled);

}