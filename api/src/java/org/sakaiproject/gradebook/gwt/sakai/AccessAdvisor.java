package org.sakaiproject.gradebook.gwt.sakai;

import org.sakaiproject.authz.api.Member;

public interface AccessAdvisor {
	
	public boolean isLearner(Member member);
	
}
