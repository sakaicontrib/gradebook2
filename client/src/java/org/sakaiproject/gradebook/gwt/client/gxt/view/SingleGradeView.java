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

package org.sakaiproject.gradebook.gwt.client.gxt.view;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.ViewAsStudentPanel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;

public class SingleGradeView extends View {

	private ViewAsStudentPanel dialog;
	private boolean isEditable;
	
	public SingleGradeView(Controller controller, boolean isEditable) {
		super(controller);
		this.isEditable = isEditable;
	}
	
	public boolean isDialogVisible() {
		if (dialog != null) {
			return dialog.isVisible();
		}
		return false;
	}

	@Override
	protected void handleEvent(AppEvent event) {
		ModelData learnerGradeRecordCollection = null;
		switch (GradebookEvents.getEvent(event.getType()).getEventKey()) {
		case ITEM_UPDATED:
			onItemUpdated((ItemModel)event.getData());
			break;
		case REFRESH_GRADEBOOK_SETUP:
			onRefreshGradebookSetup((GradebookModel)event.getData());
			break;
		case SINGLE_GRADE:
		case SINGLE_VIEW:
			learnerGradeRecordCollection = event.getData();
			onChangeModel(learnerGradeRecordCollection);
			break;
		case SELECT_LEARNER:
			learnerGradeRecordCollection = (ModelData)event.getData();
			onChangeModel(learnerGradeRecordCollection);
			break;
		case USER_CHANGE:
			onUserChange((UserEntityAction<?>)event.getData());
			break;
		case LEARNER_GRADE_RECORD_UPDATED:
			onLearnerGradeRecordUpdated((UserEntityUpdateAction)event.getData());
			break;
		case GRADE_TYPE_UPDATED:
			onGradeTypeUpdated((GradebookModel)event.getData());
			break;
		}
	}
	
	@Override
	protected void initialize() {
		dialog = new ViewAsStudentPanel(!isEditable);
		dialog.setSize(400, 350);
	}
	
	private void onRefreshGradebookSetup(GradebookModel selectedGradebook) {
		dialog.onRefreshGradebookSetup(selectedGradebook);
	}
	
	private void onChangeModel(ModelData learnerGradeRecordCollection) {
		GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
		dialog.onChangeModel(selectedGradebook, learnerGradeRecordCollection);
		dialog.show();
	}
	
	private void onGradeTypeUpdated(GradebookModel selectedGradebook) {
		
	}
	
	private void onItemUpdated(ItemModel itemModel) {
		dialog.onItemUpdated(itemModel);
	}
	
	private void onLearnerGradeRecordUpdated(UserEntityUpdateAction action) {
		dialog.onLearnerGradeRecordUpdated(action.getModel());
	}
	
	private void onUserChange(UserEntityAction<?> action) {
		dialog.onUserChange(action);
	}

	public ViewAsStudentPanel getDialog() {
		return dialog;
	}
	
}
