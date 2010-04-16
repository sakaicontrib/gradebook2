package org.sakaiproject.gradebook.gwt.sakai.model;

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.model.Item;

public interface GradeItem extends Item {
	
	public abstract <X> X get(String property);
	
	public abstract void addChild(GradeItem child);
	
	public abstract int getChildCount();

	public abstract List<GradeItem> getChildren();
	
	public abstract String getParentName();
	
	public abstract void setParentName(String parentName);
	
}