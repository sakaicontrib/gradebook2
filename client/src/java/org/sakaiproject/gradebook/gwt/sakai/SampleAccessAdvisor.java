package org.sakaiproject.gradebook.gwt.sakai;

import org.sakaiproject.authz.api.Member;

public class SampleAccessAdvisor implements AccessAdvisor {

	public Long[] getLearnerRoleKeys() {
		Long[] roleKeys = { Long.valueOf(8l), Long.valueOf(110l) };
		return roleKeys;
	}
	
	
	public boolean isLearner(Member member) {
		String role = member.getRole() == null ? "" : member.getRole().getId();
		
		return (role.equalsIgnoreCase("Student") 
				|| role.equalsIgnoreCase("Open Campus")
				|| role.equalsIgnoreCase("Access"))
				&& member.isActive();
	}

}
