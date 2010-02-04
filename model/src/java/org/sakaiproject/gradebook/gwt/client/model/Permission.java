package org.sakaiproject.gradebook.gwt.client.model;

public interface Permission {

	public abstract String getCategoryDisplayName();

	public abstract Long getCategoryId();

	public abstract String getDeleteAction();

	public abstract Long getGradebookId();

	public abstract Long getId();

	public abstract String getIdentifier();

	public abstract String getPermissionId();

	public abstract String getSectionDisplayName();

	public abstract String getSectionId();

	public abstract String getUserDisplayName();

	public abstract String getUserId();

	public abstract void setCategoryDisplayName(String categoryDisplayName);

	public abstract void setCategoryId(Long categoryId);

	public abstract void setDeleteAction(String deleteAction);

	public abstract void setGradebookId(Long gradebookId);

	public abstract void setId(Long id);

	public abstract void setPermissionId(String permissionId);

	public abstract void setSectionDisplayName(String sectionDisplayName);

	public abstract void setSectionId(String sectionId);
	
	public abstract void setUserDisplayName(String userDisplayName);
	
	public abstract void setUserId(String userId);

}