package org.sakaiproject.gradebook.gwt.sakai;

import org.sakaiproject.authz.api.Member;

public class SampleAccessAdvisor implements AccessAdvisor {

	public boolean isLearner(Member member) {
		return (member.getRole().getId().equals("Student") 
				|| member.getRole().getId().equals("Open Campus"))
				&& member.isActive();
	}

}
