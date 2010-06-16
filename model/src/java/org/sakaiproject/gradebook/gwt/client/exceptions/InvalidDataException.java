package org.sakaiproject.gradebook.gwt.client.exceptions;

import java.io.Serializable;

public class InvalidDataException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public InvalidDataException() {}
	
	public InvalidDataException(String message) {
		super(message);
	}
}
