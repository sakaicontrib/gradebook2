package org.sakaiproject.gradebook.gwt.client.gxt;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.google.gwt.user.client.ui.KeyboardListener;

public class InlineEditNumberField extends NumberField {

	@Override
	protected void onKeyPress(FieldEvent fe) {
		super.onKeyPress(fe);
		
		switch (fe.event.getKeyCode()) {
		case KeyboardListener.KEY_ENTER:
			complete();
			break;
		}
		
	}
	
	public void complete() {
		if (!GXT.isOpera && focusStyle != null) {
			getFocusEl().removeStyleName(focusStyle);
		}
		hasFocus = false;

		validate();

		if ((focusValue == null && getValue() != null)
				|| (focusValue != null && !focusValue
						.equals(getValue()))) {
			fireChangeEvent(focusValue, getValue());
		}
		fireEvent(Events.Blur, new FieldEvent(this));
	}
	
}
