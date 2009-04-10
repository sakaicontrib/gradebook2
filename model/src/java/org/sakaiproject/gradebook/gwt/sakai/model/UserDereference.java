package org.sakaiproject.gradebook.gwt.sakai.model;

import java.util.Date;

public class UserDereference {

	private Long id;
	private String userUid;
	private String displayId;
	private String sortName;
	private String email;
	private Date createdOn;
	
	public UserDereference() {
		
	}
	
	public UserDereference(String userUid) {
		this.userUid = userUid;
	}
	
	public UserDereference(String userUid, String displayId, String sortName, String email) {
		this.userUid = userUid;
		this.displayId = displayId;
		this.sortName = sortName;
		this.email = email;
		this.createdOn = new Date();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserUid() {
		return userUid;
	}
	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public String getDisplayId() {
		return displayId;
	}

	public void setDisplayId(String displayId) {
		this.displayId = displayId;
	}

	
	
}
