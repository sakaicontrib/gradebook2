package org.sakaiproject.gradebook.gwt.client.gxt;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TriggerField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.google.gwt.event.dom.client.KeyCodes;

public class GbCellEditor extends CellEditor {

	public GbCellEditor(Field<? extends Object> field) {
		super(field);
	}

	/*
	protected void onSpecialKey(FieldEvent fe) {
	    int key = fe.getKeyCode();

	    if (isCompleteOnEnter() && key == KeyCodes.KEY_ENTER) {
	      fe.stopEvent();
	      completeEdit();
	      
	    } else if (isCancelOnEsc() && key == KeyCodes.KEY_ESCAPE) {
	      cancelEdit();
	    } else {
	      fireEvent(Events.SpecialKey, fe);
	    }

	    if (getField() instanceof TriggerField
	        && (key == KeyCodes.KEY_ENTER || key == KeyCodes.KEY_ESCAPE || key == KeyCodes.KEY_TAB)) {
	      triggerBlur((TriggerField) getField());
	    }
	  }
	*/
}
