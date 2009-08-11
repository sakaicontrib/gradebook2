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

public class NotificationEvent {

	private String title;
	private String context;
	private String message;
	private Throwable t;
	private boolean isFailure;
	
	
	public NotificationEvent(String title, String message) {
		this.title = title;
		this.message = message;
		this.isFailure = false;
	}
	
	public NotificationEvent(Throwable t) {
		this.t = t;
		this.isFailure = true;
	}
	
	public NotificationEvent(Throwable t, String context) {
		this(t);
		this.context = context;
		this.isFailure = true;
	}
	
	public Throwable getError() {
		return t;
	}
	
	public String getText() {
		StringBuilder text = new StringBuilder();
		
		if (context != null)
			text.append(context);
		
		if (t != null)
			text.append(t.getMessage());
		
		if (message != null)
			text.append(message);
		
		return text.toString();
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public boolean isFailure() {
		return isFailure;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
