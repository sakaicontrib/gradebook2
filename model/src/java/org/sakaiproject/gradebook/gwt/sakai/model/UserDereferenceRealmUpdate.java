package org.sakaiproject.gradebook.gwt.sakai.model;

import java.util.Date;

public class UserDereferenceRealmUpdate {

	private Long id;
	private String realmId;
	private Date lastUpdate;
	private Integer realmCount;
	
	public UserDereferenceRealmUpdate() {
		
	}
	
	public UserDereferenceRealmUpdate(String realmId, Integer realmCount) {
		this.realmId = realmId;
		this.lastUpdate = new Date();
		this.realmCount = realmCount;
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

	public Integer getRealmCount() {
		return realmCount;
	}

	public void setRealmCount(Integer realmCount) {
		this.realmCount = realmCount;
	}
	
}
