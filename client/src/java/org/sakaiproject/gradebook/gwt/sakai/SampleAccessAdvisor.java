package org.sakaiproject.gradebook.gwt.sakai;

import org.sakaiproject.authz.api.Member;

public class SampleAccessAdvisor implements AccessAdvisor {

	public boolean isLearner(Member member) {
		String role = member.getRole() == null ? "" : member.getRole().getId();
		
		return (role.equalsIgnoreCase("Student") 
				|| role.equalsIgnoreCase("Open Campus")
				|| role.equalsIgnoreCase("Access"))
				&& member.isActive();
	}

}
