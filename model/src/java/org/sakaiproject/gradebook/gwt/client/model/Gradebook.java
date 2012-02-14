package org.sakaiproject.gradebook.gwt.client.model;

import java.util.List;

public interface Gradebook {

	public abstract String getGradebookUid();

	public abstract void setGradebookUid(String gradebookUid);

	public abstract Long getGradebookId();

	public abstract void setGradebookId(Long gradebookId);

	public abstract Configuration getConfigurationModel();

	public abstract void setConfigurationModel(Configuration configuration);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract List<FixedColumn> getColumns();

	public abstract void setColumns(List<FixedColumn> columns);

	public abstract Learner getUserAsStudent();

	public abstract void setUserAsStudent(Learner userAsStudent);

	public abstract String getIdentifier();

	public abstract String getUserName();

	public abstract void setUserName(String userName);

	public abstract Item getGradebookItemModel();

	public abstract void setGradebookGradeItem(Item gradebookGradeItem);

	public abstract Boolean isNewGradebook();

	public abstract void setNewGradebook(Boolean isNewGradebook);

	public abstract List<Statistics> getStatsModel();

	public abstract void setStatsModel(List<Statistics> statsModel);
	
	public abstract Item getCategoryItemModel(Long categoryId);

	public abstract String toXml();
	
	public abstract void fromXml(String xml);

	public abstract Item getItemByIdentifier(String id);
}