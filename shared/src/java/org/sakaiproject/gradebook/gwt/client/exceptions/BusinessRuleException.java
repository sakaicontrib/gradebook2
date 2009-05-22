package org.sakaiproject.gradebook.gwt.client.exceptions;

public class BusinessRuleException extends InvalidInputException {

	private static final long serialVersionUID = 1L;

	public BusinessRuleException() {
		super();
	}
	
	public BusinessRuleException(String message) {
		super(message);
	}

}
