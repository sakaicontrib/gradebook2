/**********************************************************************************
*
* Copyright (c) 2008, 2009, 2010, 2011 The Regents of the University of California
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

package org.sakaiproject.gradebook.gwt.client;

import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;

import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;

public abstract class RestCallback {

	public abstract void onSuccess(Request request, Response response);
	
	public void onFailure(Request request, Throwable exception) {
		onError(request, exception, 500);
	}
	
	public void onError(Request request, Throwable exception, Integer statusCode) {
		Dispatcher.forwardEvent(GradebookEvents.Exception.getEventType(), new NotificationEvent(exception));
	}
	
	public void onError(Request request, Throwable exception) {
		onError(request, exception, null);
	}
}
