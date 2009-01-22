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
package org.sakaiproject.gradebook.gwt.client.model;

import java.util.Map;

public class SectionModel extends EntityModel {

	private static final long serialVersionUID = 1L;

	public SectionModel() {
		super();
	}
	
	public SectionModel(Map<String, Object> properties) {
		super(properties);
	}
	
	public String getSectionId() {
		return get("sectionId");
	}
	
	public void setSectionId(String sectionId) {
		set("sectionId", sectionId);
	}
	
	public String getSectionName() {
		return get("sectionName");
	}
	
	public void setSectionName(String sectionName) {
		set("sectionName", sectionName);
	}

	@Override
	public String getDisplayName() {
		return getSectionName();
	}

	@Override
	public String getIdentifier() {
		return getSectionId();
	}
	
}
