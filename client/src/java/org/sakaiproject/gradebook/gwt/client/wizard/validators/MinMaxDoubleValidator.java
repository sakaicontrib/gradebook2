package org.sakaiproject.gradebook.gwt.client.wizard.validators;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nMessages;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;

public class MinMaxDoubleValidator implements Validator {

	
	protected String errorMessage;

	public MinMaxDoubleValidator (String errorMessage) {
		this.errorMessage = errorMessage;
	}

	Double min = Double.MIN_VALUE;
	I18nMessages i18n = Registry.get(AppConstants.I18N_TEMPLATES);
	
	
	
	public MinMaxDoubleValidator(Double min, String errorMessage) {
		this(errorMessage);
		this.min = min;
	}
	

	public String validate(Field<?> field, String value) {

		Double v = null;
		try {
			v = Double.parseDouble(value);
			
			return v<min ? (errorMessage + "  " + 
					i18n.greaterThanOrEqualToValue("" + min)) : null ;
			
		} catch (NumberFormatException e) {
			return errorMessage;
		}

		
	}
	
}
