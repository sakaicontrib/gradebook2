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

public class GradeEventModel extends EntityModel {

	private static final long serialVersionUID = 1L;

	public enum Key { ID, GRADER_NAME, GRADE, DATE_GRADED };
	
	public GradeEventModel() {
		super();
	}
	
	public GradeEventModel(Map<String, Object> properties) {
		super(properties);
	}
	
	public String getIdentifier() {
		return get(Key.ID.name());
	}
	
	public void setIdentifier(String id) {
		set(Key.ID.name(), id);
	}
	
	public String getGraderName() {
		return get(Key.GRADER_NAME.name());
	}
	
	public void setGraderName(String graderName) {
		set(Key.GRADER_NAME.name(), graderName);
	}
	
	public String getGrade() {
		return get(Key.GRADE.name());
	}
	
	public void setGrade(String grade) {
		set(Key.GRADE.name(), grade);
	}
	
	public String getDateGraded() {
		return get(Key.DATE_GRADED.name());
	}
	
	public void setDateGraded(String dateGraded) {
		set(Key.DATE_GRADED.name(), dateGraded);
	}

	@Override
	public String getDisplayName() {
		return getGrade();
	}

	
}
