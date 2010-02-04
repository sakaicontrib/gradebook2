package org.sakaiproject.gradebook.gwt.client.gxt.model.type;

import org.sakaiproject.gradebook.gwt.client.model.key.ApplicationKey;

import com.extjs.gxt.ui.client.data.ModelType;

public class ApplicationModelType extends ModelType {

	public ApplicationModelType() {
		addField(ApplicationKey.ENABLEDGRADETYPES.name());
		addField(ApplicationKey.HELPURL.name());
		addField(ApplicationKey.PLACEMENTID.name());
		addField(ApplicationKey.GRADEBOOKMODELS.name());
	}
	
}
