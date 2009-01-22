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

import java.util.Date;
import java.util.Map;

public class AssignmentModel extends ItemEntityModel {

	private static final long serialVersionUID = 1L;


	public AssignmentModel() {
		super();
	}
	
	public AssignmentModel(Map<String, Object> properties) {
		super(properties);
	}

	public String getCategoryName() {
		return get(Key.CATEGORY_NAME.name());
	}
	
	public void setCategoryName(String categoryName) {
		set(Key.CATEGORY_NAME.name(), categoryName);
	}
	
	public Long getCategoryId() {
		return get(Key.CATEGORY_ID.name());
	}
	
	public void setCategoryId(Long categoryId) {
		set(Key.CATEGORY_ID.name(), categoryId);
	}
	
	public Double getPoints() {
		return get(Key.POINTS.name());
	}
	
	public void setPoints(Double points) {
		set(Key.POINTS.name(), points);
	}
	
	public Date getDueDate() {
		return get(Key.DUE_DATE.name());
	}
	
	public void setDueDate(Date dueDate) {
		set(Key.DUE_DATE.name(), dueDate);
	}
	
	public Boolean getReleased() {
		return get(Key.RELEASED.name());
	}
	
	public void setReleased(Boolean released) {
		set(Key.RELEASED.name(), released);
	}
	
	public String getSource() {
		return get(Key.SOURCE.name());
	}
	
	public void setSource(String source) {
		set(Key.SOURCE.name(), source);
	}

	
}
