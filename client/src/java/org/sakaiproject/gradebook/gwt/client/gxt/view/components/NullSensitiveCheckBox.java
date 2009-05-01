package org.sakaiproject.gradebook.gwt.client.gxt.view.components;

import com.extjs.gxt.ui.client.widget.form.CheckBox;

public class NullSensitiveCheckBox extends CheckBox {

	@Override
	public void setValue(Boolean value) {
		super.setValue(value == null ? Boolean.FALSE : value);
	}
	
}
