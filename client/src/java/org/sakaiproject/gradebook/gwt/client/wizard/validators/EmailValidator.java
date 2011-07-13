package org.sakaiproject.gradebook.gwt.client.wizard.validators;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;

public class EmailValidator implements Validator {
	private String errorMessage;
	
	public EmailValidator (String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String validate(Field<?> field, String value) {
		if (!value.toUpperCase().matches("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}")) return errorMessage;
		return null;
	}
}



