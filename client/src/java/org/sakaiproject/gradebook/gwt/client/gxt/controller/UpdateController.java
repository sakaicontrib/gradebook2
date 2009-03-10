package org.sakaiproject.gradebook.gwt.client.gxt.controller;

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.GradebookToolFacadeAsync;
import org.sakaiproject.gradebook.gwt.client.action.RemoteCommand;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction.ClassType;
import org.sakaiproject.gradebook.gwt.client.gxt.Notifier;
import org.sakaiproject.gradebook.gwt.client.gxt.NotifyingAsyncCallback;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradeRecordUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ItemUpdate;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.Info;

public class UpdateController extends Controller {

	private static final Notifier notifier = new Notifier();
	
	public static final String FAILED_FLAG = ":F";
	
	public UpdateController() {
		registerEventTypes(GradebookEvents.DeleteItem);
		registerEventTypes(GradebookEvents.RevertItem);
		registerEventTypes(GradebookEvents.UpdateLearnerGradeRecord);
		registerEventTypes(GradebookEvents.UpdateItem);
	}
	
	@Override
	public void handleEvent(AppEvent<?> event) {
		switch (event.type) {
		case GradebookEvents.DeleteItem:
			Info.display("Delete Item", "Not yet implemented");
			break;
		case GradebookEvents.RevertItem:
			onRevertItem((ItemUpdate)event.data);
			break;
		case GradebookEvents.UpdateLearnerGradeRecord:
			onUpdateGradeRecord((GradeRecordUpdate)event.data);
			break;
		case GradebookEvents.UpdateItem:
			onUpdateItem((ItemUpdate)event.data);
			break;
		}
	}
	
	
	private void onUpdateGradeRecord(GradeRecordUpdate event) {
		
		final Record record = event.record;
		
		RemoteCommand<StudentModel> remoteCommand = new RemoteCommand<StudentModel>() {
			
			private static final long serialVersionUID = 1L;

			public void onCommandFailure(UserEntityAction<StudentModel> action, Throwable caught) {
				String property = action.getKey();
						
				// Save the exception message on the record
				String failedProperty = property + FAILED_FLAG;
				record.set(failedProperty, caught.getMessage());
						
				// We have to fool the system into thinking that the value has changed, since
				// we snuck in that "Saving grade..." under the radar.
				record.set(property, null);
				record.set(property, action.getStartValue());
					
				record.setValid(property, false);
				
				String message = new StringBuilder("Failed to update grade: ").append(caught.getMessage()).toString();
				
				notifier.notifyError("Exception", message);
				Dispatcher.forwardEvent(GradebookEvents.Notification, message);
			}
			
			public void onCommandSuccess(UserEntityAction<StudentModel> action, StudentModel result) {
				
				// Need to refresh any items that may have been dropped
				for (String property : result.getPropertyNames()) {
					boolean needsRefreshing = false;
					
					int index = -1;
					
					if (property.endsWith(StudentModel.DROP_FLAG)) {
						index = property.indexOf(StudentModel.DROP_FLAG);
						needsRefreshing = true;
					} else if (property.endsWith(StudentModel.COMMENTED_FLAG)) {
						index = property.indexOf(StudentModel.COMMENTED_FLAG);
						needsRefreshing = true;
					}
					
					if (needsRefreshing && index != -1) {
						String assignmentId = property.substring(0, index);
						Object value = result.get(assignmentId);
						Boolean recordFlagValue = (Boolean)record.get(property);
						Boolean resultFlagValue = result.get(property);
					
						boolean isDropped = resultFlagValue != null && resultFlagValue.booleanValue();
						boolean wasDropped = recordFlagValue != null && recordFlagValue.booleanValue();
						
						record.set(property, resultFlagValue);
						
						if (isDropped || wasDropped) {
							record.set(assignmentId, null);
							record.set(assignmentId, value);
							//r.setDirty(true);
						}
					}
				}
				
				String courseGrade = result.get(StudentModel.Key.COURSE_GRADE.name());
				
				if (courseGrade != null) {
					result.set(StudentModel.Key.COURSE_GRADE.name(), null);
					result.set(StudentModel.Key.COURSE_GRADE.name(), courseGrade);
				}
				
				String property = action.getKey();
				
				// Ensure that we clear out any older failure messages
				// Save the exception message on the record
				String failedProperty = property + FAILED_FLAG;
				record.set(failedProperty, null);
			
				record.setValid(property, true);
				
				Object value = result.get(property);
				
				if (value == null)
					record.set(property, null);
				else
					record.set(property, value);
				
				//record.set(StudentModel.Key.COURSE_GRADE.name(), result.get(StudentModel.Key.COURSE_GRADE.name()));
						
				Dispatcher.forwardEvent(GradebookEvents.LearnerGradeRecordUpdated, action);
				
				// FIXME: Move all this to a log event listener
				StringBuilder buffer = new StringBuilder();
				buffer.append(action.getEntityName());
				//notifier.notify(buffer.toString(), 
				//		"Stored item grade as '{0}' and recalculated course grade to '{1}' ", result.get(property), result.get(StudentModel.Key.COURSE_GRADE.name()));
					
				String message = new StringBuilder(action.getEntityName()).append(": stored item grade as '")
					.append(result.get(property))
					.append("' and recalculated course grade to '").append(result.get(StudentModel.Key.COURSE_GRADE.name()))
					.append("'").toString();
				
				notifier.notify("Success", message);
				Dispatcher.forwardEvent(GradebookEvents.Notification, message);
			}		
		};

		GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
		ClassType classType = StudentModel.lookupClassType(event.property);
		
		UserEntityUpdateAction<StudentModel> action = new UserEntityUpdateAction<StudentModel>(selectedGradebook, (StudentModel)record.getModel(), event.property, classType, event.value, event.oldValue);		
		remoteCommand.execute(action);
	}

	private void onRevertItem(final ItemUpdate event) {
		String property = event.property;
		Record record = event.record;
		
		record.set(property, null);
		record.set(property, event.oldValue);
			
		record.setValid(property, false);
	}
	
	
	private void onUpdateItem(final ItemUpdate event) {
		final Record record = event.record;
		final String property = event.property;
		
		GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
		ClassType classType = ItemModel.lookupClassType(event.property);
		
		final UserEntityUpdateAction<ItemModel> action = new UserEntityUpdateAction<ItemModel>(selectedGradebook, (ItemModel)record.getModel(), event.property, classType, event.value, event.oldValue);		
		
		GradebookToolFacadeAsync service = Registry.get("service");
		NotifyingAsyncCallback<List<ItemModel>> callback = new NotifyingAsyncCallback<List<ItemModel>>() {
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				
				// Save the exception message on the record
				String failedProperty = property + FAILED_FLAG;
				record.set(failedProperty, caught.getMessage());
						
				// We have to fool the system into thinking that the value has changed, since
				// we snuck in that "Saving..." under the radar.
				record.set(property, null);
				record.set(property, event.oldValue);
					
				record.setValid(property, false);
				
				notifier.notifyError("Error", "Failed to update: {0} ", caught.getMessage());
				
				String message = new StringBuilder("Failed to update item: ").append(caught.getMessage()).toString();
				
				Dispatcher.forwardEvent(GradebookEvents.Notification, message);
			}
			
			public void onSuccess(List<ItemModel> resultList) {
				// Ensure that we clear out any older failure messages
				// Save the exception message on the record
				String failedProperty = property + FAILED_FLAG;
				record.set(failedProperty, null);
			
				record.setValid(property, true);
				
				Object value = event.value;
				
				if (value == null)
					record.set(property, null);
				else
					record.set(property, value);
				
				//Dispatcher.forwardEvent(GradebookEvents.UserChange, action);
				
				StringBuilder buffer = new StringBuilder();
				buffer.append(action.getEntityName());
				//notifier.notify(buffer.toString(), 
				//		"Stored item grade as '{0}' and recalculated course grade to '{1}' ", value, result.get(StudentModel.Key.COURSE_GRADE.name()));
				//Info.display("FIXME", "Need to handle the update better");	
				
				for (ItemModel itemModel : resultList) {
					Dispatcher.forwardEvent(GradebookEvents.ItemUpdated, itemModel);
				}
				
				//Info.display("Items", "Number: " + resultList.size());
			}
		};
		
		service.updateItemEntity((UserEntityUpdateAction<ItemModel>)action, callback);
	
	}
	
}
