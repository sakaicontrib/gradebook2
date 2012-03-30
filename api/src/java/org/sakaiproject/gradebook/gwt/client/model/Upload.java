package org.sakaiproject.gradebook.gwt.client.model;

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.api.ImportSettings;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;


public interface Upload {

	public List<Item> getHeaders();

	public void setHeaders(List<Item> headers);

	public List<Learner> getRows();

	public void setRows(List<Learner> rows);

	public boolean isPercentage();

	public void setPercentage(boolean isPercentage);

	public List<String> getResults();

	public void setResults(List<String> results);

	public Item getGradebookItemModel();

	public void setGradebookItemModel(Item gradebookItemModel);

	public boolean hasErrors();
	
	public void setErrors(boolean hasErrors);
	
	public String getNotes();
	
	public void setNotes(String notes);
	
	public boolean isNotifyAssignmentName();
	
	public void setNotifyAssignmentName(boolean doNotify);

	public GradeType getGradeType();
	
	public void setGradeType(GradeType gradeType);
	
	public CategoryType getCategoryType();
	
	public void setCategoryType(CategoryType categoryType);

	public abstract void setImportSettings(ImportSettings importSettings);

	public abstract ImportSettings getImportSettings();
	
}