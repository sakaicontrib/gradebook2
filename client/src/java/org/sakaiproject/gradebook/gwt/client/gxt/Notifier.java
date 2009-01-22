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

import org.sakaiproject.gradebook.gwt.client.gxt.settings.LogConfig;
import org.sakaiproject.gradebook.gwt.client.gxt.settings.LogDisplay;

import com.extjs.gxt.ui.client.XDOM;
import com.extjs.gxt.ui.client.util.Params;

public class Notifier {

	public Notifier() {
		
	}
	
	public void notify(String title, String text, Object...values) {
		Params infoParams = new Params(values);
		//InfoConfig infoConfig = new InfoConfig(title, text, infoParams);
		//infoConfig.display = 5000;
		//Info.display(infoConfig);
		
		int panelWidth = XDOM.getViewportSize().width / 2;
		int x = XDOM.getViewportSize().width / 2 - panelWidth / 2;
		
		LogConfig infoConfig = new LogConfig(title, text, infoParams);
		infoConfig.display = 5000;
		infoConfig.width = panelWidth;
		infoConfig.height = 60;
		
		LogDisplay.display(x, 0, infoConfig);
	}
	
	
}
