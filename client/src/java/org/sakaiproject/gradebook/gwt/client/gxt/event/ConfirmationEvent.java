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

package org.sakaiproject.gradebook.gwt.client.gxt.event;

public class ConfirmationEvent {

	public String text;
	
	public int okEventType;
	public Object okEventData;
	
	public int cancelEventType;
	public Object cancelEventData;
	
	public ConfirmationEvent(String text) {
		this.text = text;
		this.okEventType = -1;
		this.cancelEventType = -1;
	}
	
}
