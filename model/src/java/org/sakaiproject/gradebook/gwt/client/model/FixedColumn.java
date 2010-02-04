package org.sakaiproject.gradebook.gwt.client.model;

public interface FixedColumn {

	public abstract String getIdentifier();

	public abstract void setIdentifier(String id);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract Integer getWidth();

	public abstract void setWidth(Integer width);

	public abstract void setAssignmentId(Long assignmentId);

	public abstract Long getAssignmentId();

	public abstract Double getMaxPoints();

	public abstract void setMaxPoints(Double maxPoints);

	public abstract String getKey();

	public abstract void setKey(String key);

	public abstract Long getCategoryId();

	public abstract void setCategoryId(Long categoryId);

	public abstract String getCategoryName();

	public abstract void setCategoryName(String categoryName);

	public abstract Boolean isUnweighted();

	public abstract void setUnweighted(Boolean isUnweighted);

	public abstract Boolean isHidden();

	public abstract void setHidden(Boolean isHidden);

	public abstract Boolean isEditable();

	public abstract void setEditable(Boolean isEditable);

	public abstract Boolean isExtraCredit();

	public abstract void setExtraCredit(Boolean isExtraCredit);

	public abstract String getDisplayName();

	public abstract boolean isChecked();

	public abstract void setChecked(boolean isChecked);

}