package org.sakaiproject.gradebook.entity;

import java.util.ResourceBundle;

import org.sakaiproject.entity.api.ContextObserver;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.service.gradebook.shared.GradebookFrameworkService;

public class AssureGradebookContextObserver implements ContextObserver {

	private static ResourceBundle i18n = ResourceBundle.getBundle("org.sakaiproject.gradebook.gwt.client.I18nConstants");
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

}
