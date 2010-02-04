package org.sakaiproject.gradebook.gwt.client.gxt.model.type;

import org.sakaiproject.gradebook.gwt.client.model.key.GradebookKey;

import com.extjs.gxt.ui.client.data.DataField;
import com.extjs.gxt.ui.client.data.ModelType;

public class GradebookModelType extends ModelType {

	public GradebookModelType() {
		addField(GradebookKey.GRADEBOOKUID);
		addField(GradebookKey.GRADEBOOKID);
		addField(GradebookKey.NAME);
		addField(GradebookKey.GRADEBOOKITEMMODEL);
		addField(GradebookKey.COLUMNS);
		addField(GradebookKey.USERNAME);
		addField(GradebookKey.ISNEWGRADEBOOK);
		addField(GradebookKey.CONFIGURATIONMODEL);
		addField(GradebookKey.STATSMODELS);
		addField(GradebookKey.USERASSTUDENT);
	}
	
	public void addField(GradebookKey key) {
		DataField field = new DataField(key.name());
		if (key.getType() != null)
			field.setType(key.getType());
		addField(field);
	}
	
}
