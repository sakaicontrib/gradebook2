package org.sakaiproject.gradebook.gwt.client.gxt.upload;

import java.io.Serializable;
import java.util.List;

public class ImportFile implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<ImportHeader> items;
	
	private List<ImportRow> rows;
	
	private Boolean hasCategories;
	
	public ImportFile() {
		
	}

	public List<ImportRow> getRows() {
		return rows;
	}

	public void setRows(List<ImportRow> rows) {
		this.rows = rows;
	}

	public List<ImportHeader> getItems() {
		return items;
	}

	public void setItems(List<ImportHeader> items) {
		this.items = items;
	}

	public Boolean getHasCategories() {
		return hasCategories;
	}

	public void setHasCategories(Boolean hasCategories) {
		this.hasCategories = hasCategories;
	}

	
	
}
