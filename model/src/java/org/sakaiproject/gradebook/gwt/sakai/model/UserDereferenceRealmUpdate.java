package org.sakaiproject.gradebook.gwt.sakai.model;

import java.util.Date;

public class UserDereferenceRealmUpdate {

	private Long id;
	private String realmId;
	private Date lastUpdate;
	
	public UserDereferenceRealmUpdate() {
		
	}
	
	public UserDereferenceRealmUpdate(String realmId) {
		this.realmId = realmId;
		this.lastUpdate = new Date();
	}
	
	public String getRealmId() {
		return realmId;
	}
	public void setRealmId(String realmId) {
		this.realmId = realmId;
	}
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}
