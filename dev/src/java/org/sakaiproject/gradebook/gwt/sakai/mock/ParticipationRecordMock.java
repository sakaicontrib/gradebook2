/**********************************************************************************
 *
 * $Id:$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2008, 2009 The Regents of the University of California
 *
 * Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 **********************************************************************************/

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
