package org.sakaiproject.gradebook.gwt.sakai.model;

import java.io.Serializable;

public class UserConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String userUid;
	private Long gradebookId;
	private String configField;
	private String configValue;
	
	
	public UserConfiguration() {
		
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


	public Long getGradebookId() {
		return gradebookId;
	}


	public void setGradebookId(Long gradebookId) {
		this.gradebookId = gradebookId;
	}


	public String getConfigField() {
		return configField;
	}


	public void setConfigField(String configField) {
		this.configField = configField;
	}


	public String getConfigValue() {
		return configValue;
	}


	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}

	
}
