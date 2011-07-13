package org.sakaiproject.gradebook.gwt.client.wizard.validators;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;

public class IntegerValidator implements Validator  {
	
	protected String errorMessage;

	public IntegerValidator (String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String validate(Field<?> field, String value) {

		Integer v = null;
		try {
			v = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return errorMessage;
		}
		
		if (v <= 0) 
			return errorMessage;
	

		return null;
	}
	
	
}
