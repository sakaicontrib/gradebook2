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

import org.sakaiproject.gradebook.gwt.client.GradebookToolFacadeAsync;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.IndividualStudentEvent;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

public class GradebookContainer extends LayoutContainer {

	private InstructorViewContainer instructorViewContainer;
	private StudentViewContainer studentViewContainer;
	
	private String gradebookUid;
	
	public GradebookContainer(String gradebookUid) {
		this.gradebookUid = gradebookUid;
		setLayout(new FitLayout());
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		GradebookModel gbModel = Registry.get(gradebookUid);
		
		boolean isUserAbleToGrade = gbModel.isUserAbleToGrade() == null ? false : gbModel.isUserAbleToGrade().booleanValue();
		boolean isUserAbleToViewOwnGrades = gbModel.isUserAbleToViewOwnGrades() == null ? false : gbModel.isUserAbleToViewOwnGrades().booleanValue();
		
		if (isUserAbleToGrade) {
			renderInstructorView(gbModel);
		} else if (isUserAbleToViewOwnGrades) {
			renderStudentView(gbModel);
		}
		
	}
	
	private void renderStudentView(GradebookModel gbModel) {
		GradebookToolFacadeAsync service = Registry.get("service");
		studentViewContainer = new StudentViewContainer(true);
		studentViewContainer.fireEvent(GradebookEvents.SingleGrade, new IndividualStudentEvent(gbModel.getUserAsStudent()));
		add(studentViewContainer);
	}

	
	private void renderInstructorView(GradebookModel gbModel) {
		instructorViewContainer = new InstructorViewContainer(gradebookUid, this);
		add(instructorViewContainer);
	}

	public InstructorViewContainer getInstructorViewContainer() {
		return instructorViewContainer;
	}
}
