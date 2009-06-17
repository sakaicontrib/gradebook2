package org.sakaiproject.gradebook.gwt.sakai.mock;

import org.sakaiproject.section.api.coursemanagement.User;

public class UserSectionMock implements User {
	
	String uid;
	String displayId;
	String displayName;
	String sortName;
	
	public UserSectionMock(String uid, String displayId, String displayName, String sortName) {
		this.uid = uid;
		this.displayId = displayId;
		this.displayName = displayName;
		this.sortName = sortName;
	}

	public String getDisplayId() {
		// TODO Auto-generated method stub
		return displayId;
	}

	public String getDisplayName() {
		// TODO Auto-generated method stub
		return displayName;
	}

	public String getSortName() {
		// TODO Auto-generated method stub
		return sortName;
	}

	public String getUserUid() {
		// TODO Auto-generated method stub
		return uid;
	}

}
