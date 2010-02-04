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
package org.sakaiproject.gradebook.gwt.client.gxt.settings;

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.gxt.model.GradeEventModel;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Params;

public class LogConfig {

	/**
	 * The info title (defaults to null).
	 */
	public String title;

	/**
	 * The info text (defaults to null).
	 */
	public String text;

	/**
	 * The index or key based substitution values.
	 */
	public Params params;

	/**
	 * The time in millseconds to display a message (defaults to 2500).
	 */
	public int display = 5000;

	/**
	 * The info width (defaults to 225).
	 */
	public int width = 225;

	/**
	 * The info height (defaults to 75).
	 */
	public int height = 75;

	/**
	 * Determines if the window should stick around permanently
	 */
	public boolean isPermanent = false;
	
	/**
	 * Listener to be notified when the info is displayed (defaults to null).
	 */
	public Listener listener;

	public List<GradeEventModel> events;

	public LogConfig(String title, String text) {
		this.title = title;
		this.text = text;
	}

	public LogConfig(String title, String text, Params params) {
		this.title = title;
		this.text = text;
		this.params = params;
	}

	public LogConfig(String title, List<GradeEventModel> events, Params params) {
		this.title = title;
		this.events = events;
		this.params = params;
	}

}
