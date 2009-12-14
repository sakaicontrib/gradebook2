package org.sakaiproject.gradebook.gwt.client.gxt.view.components;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.form.ComboBox;

public class ItemFormComboBox<D extends ModelData> extends ComboBox<D> {

	public ItemFormComboBox(String displayField, String name, String label) {
		super();
		setDisplayField(displayField);
		setEditable(false);
		setName(name);
		setFieldLabel(label);
		setForceSelection(true);
		setLazyRender(true);
		setTriggerAction(TriggerAction.ALL);
		setVisible(false);
	}
	
}
