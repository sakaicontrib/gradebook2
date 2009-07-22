package org.sakaiproject.gradebook.gwt.client.gxt.upload;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImportFile implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private ArrayList<ImportHeader> items;
	
	private ArrayList<ImportRow> rows;
	
	private Boolean hasCategories;
	
	private String notes; 
	
	private boolean hasErrors; 
	
	public ImportFile() {
		hasErrors = false; 
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}


	public boolean isHasErrors() {
		return hasErrors;
	}

	public void setHasErrors(boolean hasErrors) {
		this.hasErrors = hasErrors;
	}

	public List<ImportRow> getRows() {
		return rows;
	}

	public void setRows(ArrayList<ImportRow> rows) {
		this.rows = rows;
	}

	public ArrayList<ImportHeader> getItems() {
		return items;
	}

	public void setItems(ArrayList<ImportHeader> items) {
		this.items = items;
	}

	public Boolean getHasCategories() {
		return hasCategories;
	}

	public void setHasCategories(Boolean hasCategories) {
		this.hasCategories = hasCategories;
	}

	
	
}
