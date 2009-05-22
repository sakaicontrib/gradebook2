package org.sakaiproject.gradebook.gwt.sakai.mock;

import org.sakaiproject.section.api.coursemanagement.LearningContext;

public class LearningContextMock implements LearningContext {

	private String uuid;
	private String title;
	
	public LearningContextMock(String uuid, String title) {
		super();
		this.uuid = uuid;
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUuid() {
		return uuid;
	}

}
