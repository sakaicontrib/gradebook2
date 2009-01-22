package org.sakaiproject.gradebook.gwt.sakai.mock;

import org.sakaiproject.section.api.coursemanagement.LearningContext;
import org.sakaiproject.section.api.coursemanagement.ParticipationRecord;
import org.sakaiproject.section.api.coursemanagement.User;
import org.sakaiproject.section.api.facade.Role;

public class ParticipationRecordMock implements ParticipationRecord {

	private LearningContext learningContext;
	private Role role;
	private User user;
	
	public ParticipationRecordMock(LearningContext learningContext, Role role, User user) {
		this.learningContext = learningContext;
		this.role = role;
		this.user = user;
	}
	
	public LearningContext getLearningContext() {
		return learningContext;
	}

	public Role getRole() {
		return role;
	}

	public User getUser() {
		return user;
	}

}
