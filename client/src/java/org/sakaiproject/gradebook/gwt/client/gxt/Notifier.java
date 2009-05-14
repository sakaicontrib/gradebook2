/**********************************************************************************
*
* $Id:$
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
package org.sakaiproject.gradebook.gwt.client.gxt;

import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.exceptions.BusinessRuleException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.gxt.settings.LogConfig;
import org.sakaiproject.gradebook.gwt.client.gxt.settings.LogDisplay;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.XDOM;
import com.extjs.gxt.ui.client.util.Params;

public class Notifier {

	public Notifier() {
		
	}
	
	public void notify(String title, String text, Object...values) {
		Boolean notificationsEnabled = Registry.get("enableNotifications");
		
		if (notificationsEnabled != null && notificationsEnabled.booleanValue()) {
			Params infoParams = new Params(values);
			
			int panelWidth = XDOM.getViewportSize().width / 2;
			int x = XDOM.getViewportSize().width / 2 - panelWidth / 2;
			
			LogConfig infoConfig = new LogConfig(title, text, infoParams);
			infoConfig.display = 5000;
			infoConfig.width = panelWidth;
			infoConfig.height = 60;
			
			LogDisplay.display(x, 0, infoConfig);
		}
	}
	
	public void notifyError(Throwable e) {
		String message = e.getMessage();
		String cause = "";
		if (e == null || e.getMessage() == null) {
			I18nConstants i18n = Registry.get("i18n");
			message = i18n.unknownException();
		}
		
		if (e.getCause() != null && e.getCause().getMessage() != null) {
			cause = e.getCause().getMessage();
		}
		
		if (e instanceof BusinessRuleException) {
			notifyUserError("Request Denied", " {0} ", message);
			return;
		}
		
		if (e instanceof InvalidInputException) {
			notifyUserError("Invalid Input", " {0} ", message);
			return;
		}
		
		if (e instanceof Exception) {
			String title = "Request Failed"; 
			String text = " {0} : {1} "; 
			Object[] values = { message, cause };
			
			Params infoParams = new Params(values);
	
			int panelWidth = XDOM.getViewportSize().width / 2;
			int x = XDOM.getViewportSize().width / 2 - panelWidth / 2;
			
			LogConfig infoConfig = new LogConfig(title, text, infoParams);
			//infoConfig.display = 20000;
			infoConfig.width = panelWidth;
			infoConfig.height = 60;
			infoConfig.isPermanent = true;
			
			LogDisplay.display(x, 0, infoConfig);
		}
	}
	
	public void notifyUserError(String title, String text, Object...values) {
		Params infoParams = new Params(values);

		int panelWidth = XDOM.getViewportSize().width / 2;
		int x = XDOM.getViewportSize().width / 2 - panelWidth / 2;
		
		LogConfig infoConfig = new LogConfig(title, text, infoParams);
		infoConfig.display = 20000;
		infoConfig.width = panelWidth;
		infoConfig.height = 60;
		
		LogDisplay.display(x, 0, infoConfig);
	}
	
	
}
