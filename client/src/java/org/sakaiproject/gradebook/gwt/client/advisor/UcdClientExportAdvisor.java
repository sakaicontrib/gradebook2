package org.sakaiproject.gradebook.gwt.client.advisor;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.api.ClientExportAdvisor;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.Info;


public class UcdClientExportAdvisor implements ClientExportAdvisor {

private I18nConstants i18n;
	
	
	public UcdClientExportAdvisor() {
		
		i18n = Registry.get(AppConstants.I18N);
	}
	
	public void handleServerResponse(String responseText) {
		
		if(null == responseText || "".equals(responseText)) {
			
			Info.display(i18n.finalGradeSubmissionTitle(), i18n.finalGradeSubmissionMessageText3a());
		}
		else {
			
			Info.display(i18n.finalGradeSubmissionTitle(), i18n.finalGradeSubmissionMessageText4a());
			com.google.gwt.user.client.Window.open(responseText, "_blank","status=0,toolbar=0,menubar=0,location=0,scrollbars=1,resizable=1");
		}
	}

}
