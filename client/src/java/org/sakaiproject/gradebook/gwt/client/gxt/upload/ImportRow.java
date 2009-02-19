package org.sakaiproject.gradebook.gwt.client.gxt.upload;

import java.util.List;

public class ImportRow {

	private String userUid;
	private String userImportId;
	private String userDisplayName;
	private boolean isUserNotFound;
	
	private List<String> columns;
	
	public ImportRow() {
		this.isUserNotFound = false;
	}

	public String getUserUid() {
		return userUid;
	}

	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}

	public String getUserImportId() {
		return userImportId;
	}

	public void setUserImportId(String userImportId) {
		this.userImportId = userImportId;
	}

	public String getUserDisplayName() {
		return userDisplayName;
	}

	public void setUserDisplayName(String userDisplayName) {
		this.userDisplayName = userDisplayName;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public boolean isUserNotFound() {
		return isUserNotFound;
	}

	public void setUserNotFound(boolean isUserNotFound) {
		this.isUserNotFound = isUserNotFound;
	}
	
	
	
	
}
