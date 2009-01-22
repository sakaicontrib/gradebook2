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
package org.sakaiproject.gradebook.gwt.client.action;

import java.util.Date;

import org.sakaiproject.gradebook.gwt.client.model.AssignmentModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;

public class UserAssignmentCreateAction extends UserEntityCreateAction<AssignmentModel> {
	
	private static final long serialVersionUID = 1L;

	//protected Double points;
	//protected Date dueDate;
	
	public UserAssignmentCreateAction() {
		super();
	}
	
	public UserAssignmentCreateAction(GradebookModel gbModel, Long categoryId, String name, 
			Double weight, Double points, Date dueDate) {
		super(gbModel, EntityType.GRADE_ITEM, categoryId, name, weight);
		setPoints(points);
		setDueDate(dueDate);
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
	
}
