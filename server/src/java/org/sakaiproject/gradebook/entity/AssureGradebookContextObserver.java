package org.sakaiproject.gradebook.entity;

import org.sakaiproject.entity.api.ContextObserver;
import org.sakaiproject.service.gradebook.shared.GradebookFrameworkService;
import org.sakaiproject.util.ResourceLoader;

public class AssureGradebookContextObserver implements ContextObserver {

	// Set via IoC
	private ResourceLoader i18n;
	
	private GradebookFrameworkService frameworkService = null;
	private String[] myToolIds;

	
	public void setFrameworkService(GradebookFrameworkService frameworkService) {
		this.frameworkService = frameworkService;
	}

	public void contextCreated(String context, boolean toolPlacement) {
		if (!frameworkService .isGradebookDefined(context)) {

			frameworkService.addGradebook(context, i18n
					.getString("defaultGradebookName"));
		}
	}

	public void contextDeleted(String context, boolean toolPlacement) {
		// TODO Auto-generated method stub

	}

	public void contextUpdated(String context, boolean toolPlacement) {
		// TODO Auto-generated method stub

	}

	public String[] myToolIds() {
		// TODO Auto-generated method stub
		return myToolIds;
	}

	public void setMyToolIds(String[] myToolIds) {
		this.myToolIds = myToolIds;
	}
	
	public void setI18n(ResourceLoader i18n) {
		this.i18n = i18n;
	}


}
