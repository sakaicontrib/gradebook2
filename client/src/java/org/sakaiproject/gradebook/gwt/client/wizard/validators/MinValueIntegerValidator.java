package org.sakaiproject.gradebook.gwt.client.wizard.validators;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nMessages;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.form.Field;

public class MinValueIntegerValidator extends IntegerValidator {

	int min = Integer.MIN_VALUE;
	I18nMessages i18n = Registry.get(AppConstants.I18N_TEMPLATES);
	
	
	public MinValueIntegerValidator(String errorMessage) {
		super(errorMessage);
	}
	
	public MinValueIntegerValidator(Integer min, String errorMessage) {
		super(errorMessage);
		this.min = min;
	}
	
	
	@Override
	public String validate(Field<?> field, String value) {
		if (super.validate(field, value) == null) {
			try {
				Integer v = Integer.parseInt(value);
				
				return v<min ? (errorMessage + "  " + 
						i18n.greaterThanOrEqualToValue("" + min)) : null ;
				
			} catch (NumberFormatException e) {
				// huh? this should be avoided in superClass
				return "MinValueIntegerValidator error";
			}
		}
		return errorMessage;
		
	}
}
