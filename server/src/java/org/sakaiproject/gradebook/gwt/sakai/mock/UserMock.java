package org.sakaiproject.gradebook.gwt.sakai.mock;

import org.sakaiproject.section.api.coursemanagement.User;

public class UserMock implements User {

	private String userUid;
	private String displayId;
	private String displayName;
	private String sortName;
	
	public UserMock(String userUid, String displayId, String displayName, String sortName) {
		super();
		this.userUid = userUid;
		this.displayId = displayId;
		this.displayName = displayName;
		this.sortName = sortName;
	}
	
	public String getUserUid() {
		return userUid;
	}
	
	public String getDisplayId() {
		return displayId;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String getSortName() {
		return sortName;
	}

}
