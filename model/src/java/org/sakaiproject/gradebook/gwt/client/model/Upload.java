package org.sakaiproject.gradebook.gwt.client.model;

import java.util.List;

public interface Upload {

	public abstract List<Item> getHeaders();

	public abstract void setHeaders(List<Item> headers);

	public abstract List<Learner> getRows();

	public abstract void setRows(List<Learner> rows);

	public abstract void setDisplayName(String displayName);

	public abstract String getIdentifier();

	public abstract boolean isPercentage();

	public abstract void setPercentage(boolean isPercentage);

	public abstract List<String> getResults();

	public abstract void setResults(List<String> results);

	public abstract Item getGradebookItemModel();

	public abstract void setGradebookItemModel(Item gradebookItemModel);

}