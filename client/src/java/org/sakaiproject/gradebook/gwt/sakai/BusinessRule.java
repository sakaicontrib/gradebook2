package org.sakaiproject.gradebook.gwt.sakai;

import org.sakaiproject.gradebook.gwt.client.exceptions.BusinessRuleException;

public interface BusinessRule {

	public void isSatisfied() throws BusinessRuleException;
	
}
