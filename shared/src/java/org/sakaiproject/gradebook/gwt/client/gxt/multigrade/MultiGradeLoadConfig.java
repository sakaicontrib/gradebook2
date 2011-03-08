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
package org.sakaiproject.gradebook.gwt.client.gxt.multigrade;

import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;

public class MultiGradeLoadConfig extends BasePagingLoadConfig {

	private static final long serialVersionUID = 1L;

	public MultiGradeLoadConfig() {
		super();
	}

	public String getSectionUuid() {
		return get("sectionUuid");
	}

	public void setSectionUuid(String sectionUuid) {
		set("sectionUuid", sectionUuid);
	}

	public String getSearchString() {
		return get("searchString");
	}

	public void setSearchString(String searchString) {
		set("searchString", searchString);
	}
	
	public String getShowWeighted() {
		return get("showWeighted");
	}

	public String getSearchField() {
		return get("searchField");
	}
	
	public void setSearchField(String searchField) {
		set("searchField", searchField);
	}

	public void setShowWeighted(String showWeighted) {
		set("showWeighted", showWeighted);
	}

}
