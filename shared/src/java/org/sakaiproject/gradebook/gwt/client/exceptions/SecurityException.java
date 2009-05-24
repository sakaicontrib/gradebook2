package org.sakaiproject.gradebook.gwt.client.exceptions;

import java.io.Serializable;


public class SecurityException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;

	public SecurityException() {
	}

	public SecurityException(String message) {
		super(message);
	}

	public SecurityException(Throwable cause) {
		super(cause);
	}

	public SecurityException(String message, Throwable cause) {
		super(message, cause);
	}
}
