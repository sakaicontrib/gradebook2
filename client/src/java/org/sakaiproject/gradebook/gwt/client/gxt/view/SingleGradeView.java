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
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.ViewAsStudentPanel;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;

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
			onItemUpdated((Item)event.getData());
			break;
		case REFRESH_GRADEBOOK_SETUP:
			onRefreshGradebookSetup((Gradebook)event.getData());
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
		case LEARNER_GRADE_RECORD_UPDATED:
			onLearnerGradeRecordUpdated((UserEntityUpdateAction)event.getData());
			break;
		case GRADE_TYPE_UPDATED:
			onGradeTypeUpdated((Gradebook)event.getData());
			break;
		}
	}
	
	@Override
	protected void initialize() {
		dialog = new ViewAsStudentPanel(!isEditable);
	}
	
	private void onRefreshGradebookSetup(Gradebook selectedGradebook) {
		dialog.onRefreshGradebookSetup(selectedGradebook);
	}
	
	private void onChangeModel(ModelData learnerGradeRecordCollection) {
		Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
		dialog.onChangeModel(selectedGradebook, learnerGradeRecordCollection);
		dialog.show();
	}
	
	private void onGradeTypeUpdated(Gradebook selectedGradebook) {
		
	}
	
	private void onItemUpdated(Item itemModel) {
		dialog.onItemUpdated(itemModel);
	}
	
	private void onLearnerGradeRecordUpdated(UserEntityUpdateAction action) {
		dialog.onLearnerGradeRecordUpdated(action.getModel());
	}

	public ViewAsStudentPanel getDialog() {
		return dialog;
	}
}
