package org.sakaiproject.gradebook.gwt.client.advisor;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.api.ClientExportAdvisor;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.Info;


public class SampleClientExportAdvisor implements ClientExportAdvisor {

	private I18nConstants i18n;
	
	
	public SampleClientExportAdvisor() {
		
		i18n = Registry.get(AppConstants.I18N);
	}
	
	public void handleServerResponse(String responseText) {

		Info.display(i18n.finalGradeSubmissionTitle(), i18n.finalGradeSubmissionMessageText7a());
	}

}
