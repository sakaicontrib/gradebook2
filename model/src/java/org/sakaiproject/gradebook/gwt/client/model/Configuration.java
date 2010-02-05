package org.sakaiproject.gradebook.gwt.client.model;

import java.util.List;

public interface Configuration {

	public abstract <X> X get(String property);
	
	public abstract Long getGradebookId();

	public abstract void setGradebookId(Long gradebookId);

	public abstract String getUserUid();

	public abstract void setUserUid(String userUid);

	public abstract boolean isAscending(String gridId);

	public abstract boolean isColumnHidden(String gridId, String columnId);

	public abstract boolean isColumnHidden(String gridId, String columnId,
			boolean valueForNull);

	public abstract int getColumnWidth(String gridId, String columnId,
			String name);

	public abstract int getPageSize(String gridId);

	public abstract <X> X set(String property, X value);
	
	public abstract void setPageSize(String gridId, Integer pageSize);

	public abstract List<String> getSelectedMultigradeColumns();

	public abstract String getSortField(String gridId);

	public abstract void setColumnHidden(String gridId, String columnId,
			Boolean isHidden);

	public abstract void setColumnWidth(String gridId, String columnId,
			Integer width);

	public abstract void setSortDirection(String gridId, Boolean isAscending);

	public abstract void setSortField(String gridId, String sortField);

	public abstract boolean isClassicNavigation();
	
	public abstract void setClassicNavigation(Boolean useClassicNavigation);
	
}