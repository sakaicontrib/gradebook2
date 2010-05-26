/**********************************************************************************
*
* $Id$
*
***********************************************************************************
*
* Copyright (c) 2008, 2009 The Regents of the University of California
*
* Licensed under the
* Educational Community License, Version 2.0 (the "License"); you may
* not use this file except in compliance with the License. You may
* obtain a copy of the License at
* 
* http://www.osedu.org/licenses/ECL-2.0
* 
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an "AS IS"
* BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
* or implied. See the License for the specific language governing
* permissions and limitations under the License.
*
**********************************************************************************/

package org.sakaiproject.gradebook.gwt.client.advisor;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.api.ClientExportAdvisor;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;

// GRBK-417
public class ClientExportAdvisorImpl implements ClientExportAdvisor {

	private I18nConstants i18n;
	
	
	public ClientExportAdvisorImpl() {
		
		i18n = Registry.get(AppConstants.I18N);
	}
	
	public void handleServerResponse(String responseText) {
		
		if(null == responseText || "".equals(responseText)) {
			Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(i18n.finalGradeSubmissionTitle(), i18n.finalGradeSubmissionMessageText8a(), true));
		}
		else {
			Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(i18n.finalGradeSubmissionTitle(), i18n.finalGradeSubmissionMessageText4a(), true));
			//Info.display(i18n.finalGradeSubmissionTitle(), i18n.finalGradeSubmissionMessageText4a());
			showStatus(responseText, i18n.popupBlockerDetected());
		}
	}
	
	private native boolean showStatus(String responseText, String errorText) /*-{
		 var $rv = false;
		 var $p = null;
		 
		 try {
				$p = $wnd.open(responseText, "_blank","status=0,toolbar=0,menubar=0,location=0,scrollbars=1,resizable=1");
			} catch (error) { // it may be good to log errors once client logging is possible
				$wnd.alert(errorText);
			}
		
		if ($p) {
			$rv = true;
		} else {
			$wnd.alert(errorText);
		}
		return $rv;
	    
	}-*/;

}
