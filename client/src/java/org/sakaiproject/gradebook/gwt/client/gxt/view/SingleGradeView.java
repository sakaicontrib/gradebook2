package org.sakaiproject.gradebook.gwt.client.gxt.view;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.gxt.StudentViewDialog;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;

public class SingleGradeView extends View {

	private StudentViewDialog dialog;
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
	protected void handleEvent(AppEvent<?> event) {
		StudentModel learnerGradeRecordCollection = null;
		switch (GradebookEvents.getEvent(event.type).getEventKey()) {
		case ITEM_UPDATED:
			onItemUpdated((ItemModel)event.data);
			break;
		case REFRESH_GRADEBOOK_SETUP:
			onRefreshGradebookSetup((GradebookModel)event.data);
			break;
		case SINGLE_GRADE:
		case SINGLE_VIEW:
			learnerGradeRecordCollection = (StudentModel)event.data;
			onChangeModel(learnerGradeRecordCollection);
			break;
		case SELECT_LEARNER:
			learnerGradeRecordCollection = (StudentModel)event.data;
			onChangeModel(learnerGradeRecordCollection);
			break;
		case USER_CHANGE:
			onUserChange((UserEntityAction<?>)event.data);
			break;
		}
	}
	
	@Override
	protected void initialize() {
		dialog = new StudentViewDialog(!isEditable);
		dialog.setSize(400, 350);
	}
	
	private void onRefreshGradebookSetup(GradebookModel selectedGradebook) {
		dialog.onRefreshGradebookSetup(selectedGradebook);
	}
	
	private void onChangeModel(StudentModel learnerGradeRecordCollection) {
		GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
		dialog.onChangeModel(selectedGradebook, learnerGradeRecordCollection);
		dialog.show();
	}
	
	private void onItemUpdated(ItemModel itemModel) {
		dialog.onItemUpdated(itemModel);
	}
	
	private void onUserChange(UserEntityAction<?> action) {
		dialog.onUserChange(action);
	}

	public StudentViewDialog getDialog() {
		return dialog;
	}
	
}
