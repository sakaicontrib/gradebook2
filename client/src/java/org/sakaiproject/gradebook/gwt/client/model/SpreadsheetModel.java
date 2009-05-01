package org.sakaiproject.gradebook.gwt.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModelTag;


public class SpreadsheetModel extends EntityModel implements BeanModelTag {

	private static final long serialVersionUID = 1L;
	
	private String displayName;
	private List<ItemModel> headers;
	private List<StudentModel> rows;
	private boolean isPercentage;
	private List<String> results;
	
	public SpreadsheetModel() {
		
	}

	public List<ItemModel> getHeaders() {
		return headers;
	}

	public void setHeaders(List<ItemModel> headers) {
		this.headers = headers;
	}

	public List<StudentModel> getRows() {
		return rows;
	}

	public void setRows(List<StudentModel> rows) {
		this.rows = rows;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getIdentifier() {
		return displayName;
	}

	public boolean isPercentage() {
		return isPercentage;
	}

	public void setPercentage(boolean isPercentage) {
		this.isPercentage = isPercentage;
	}

	public List<String> getResults() {
		return results;
	}

	public void setResults(List<String> results) {
		this.results = results;
	}
	
}
