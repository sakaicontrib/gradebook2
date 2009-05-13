package org.sakaiproject.gradebook.gwt.client.gxt.controller;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.Gradebook2RPCServiceAsync;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction.ClassType;
import org.sakaiproject.gradebook.gwt.client.gxt.Notifier;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradeRecordUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ItemCreate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ItemUpdate;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ServiceController extends Controller {

	private static final Notifier notifier = new Notifier();
	
	public static final String FAILED_FLAG = ":F";
	
	public ServiceController() {
		registerEventTypes(GradebookEvents.CreateItem.getEventType());
		registerEventTypes(GradebookEvents.DeleteItem.getEventType());
		registerEventTypes(GradebookEvents.RevertItem.getEventType());
		registerEventTypes(GradebookEvents.UpdateLearnerGradeRecord.getEventType());
		registerEventTypes(GradebookEvents.UpdateItem.getEventType());
	}
	
	@Override
	public void handleEvent(AppEvent<?> event) {
		switch (GradebookEvents.getEvent(event.type).getEventKey()) {
		case CREATE_ITEM:
			onCreateItem((ItemCreate)event.data);
			break;
		case DELETE_ITEM:
			onDeleteItem((ItemUpdate)event.data);
			break;
		case REVERT_ITEM:
			onRevertItem((ItemUpdate)event.data);
			break;
		case UPDATE_LEARNER_GRADE_RECORD:
			onUpdateGradeRecord((GradeRecordUpdate)event.data);
			break;
		case UPDATE_ITEM:
			onUpdateItem((ItemUpdate)event.data);
			break;
		}
	}
	
	private void onCreateItem(final ItemCreate event) {
		Dispatcher.forwardEvent(GradebookEvents.MaskItemTree.getEventType());
		GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
		
		EntityType entityType = EntityType.GRADE_ITEM;
		
		if (event.item.getItemType() == Type.CATEGORY)
			entityType = EntityType.CATEGORY;
		
		//final UserEntityCreateAction<ItemModel> action = new UserEntityCreateAction<ItemModel>(selectedGradebook, entityType, event.item);		
		
		Gradebook2RPCServiceAsync service = Registry.get("service");
		AsyncCallback<ItemModel> callback = new AsyncCallback<ItemModel>() {

			public void onFailure(Throwable caught) {
				
				notifier.notifyError(caught);
				
				String message = new StringBuilder("Failed to create item: ").append(caught.getMessage()).toString();
				
				Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), message);
				Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
			}
			
			public void onSuccess(ItemModel result) {
				if (event.close)
					Dispatcher.forwardEvent(GradebookEvents.HideFormPanel.getEventType(), Boolean.FALSE);
				
				switch (result.getItemType()) {
				case GRADEBOOK:
					GradebookModel selectedGradebook = Registry
							.get(AppConstants.CURRENT);
					selectedGradebook.setGradebookItemModel(result);
					Dispatcher
							.forwardEvent(GradebookEvents.ItemUpdated.getEventType(), result);
					Dispatcher.forwardEvent(GradebookEvents.LoadItemTreeModel.getEventType(),
							selectedGradebook);
					break;
				case CATEGORY:
					if (result.isActive())
						doCreateItem(event, result);
					else
						doUpdateItem(event.store, null, null, result);

					for (ItemModel item : result.getChildren()) {
						if (item.isActive())
							doCreateItem(event, item);
						else
							doUpdateItem(event.store, null, null, item);
					}
					break;
				case ITEM:
					if (result.isActive())
						doCreateItem(event, result);
					else
						doUpdateItem(event.store, null, null, result);
					break;
				}
				
				Dispatcher.forwardEvent(GradebookEvents.HideEastPanel.getEventType(), Boolean.FALSE);
				Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
			}
		};		
		
		service.create(selectedGradebook.getGradebookUid(), selectedGradebook.getGradebookId(), event.item, entityType, callback);
	}
	
	private void onDeleteItemSuccess(ItemUpdate event) {
		Dispatcher.forwardEvent(GradebookEvents.ItemDeleted.getEventType(), event.item);
		TreeStore<ItemModel> treeStore = (TreeStore<ItemModel>)event.store;
		treeStore.remove(event.item.getParent(), event.item);
	}
	
	private void onDeleteItem(final ItemUpdate event) {
		Dispatcher.forwardEvent(GradebookEvents.MaskItemTree.getEventType());
		event.item.setRemoved(Boolean.TRUE);
		
		Gradebook2RPCServiceAsync service = Registry.get("service");
		AsyncCallback<ItemModel> callback = new AsyncCallback<ItemModel>() {

			public void onFailure(Throwable caught) {
				onUpdateItemFailure(event, caught);
				Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
			}
			
			public void onSuccess(ItemModel result) {
				onUpdateItemSuccess(event, result);
				onDeleteItemSuccess(event);
				Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
			}
		};
		
		service.update((ItemModel)event.item, EntityType.ITEM, null, callback);
	}
	
	private void onUpdateGradeRecordSuccess(GradeRecordUpdate event, StudentModel result) {
		Record record = event.record;
		String property = event.property;
		
		// Need to refresh any items that may have been dropped
		for (String p : result.getPropertyNames()) {
			boolean needsRefreshing = false;
			
			int index = -1;
			
			if (p.endsWith(StudentModel.DROP_FLAG)) {
				index = p.indexOf(StudentModel.DROP_FLAG);
				needsRefreshing = true;
			} else if (p.endsWith(StudentModel.COMMENTED_FLAG)) {
				index = p.indexOf(StudentModel.COMMENTED_FLAG);
				needsRefreshing = true;
			}
			
			if (needsRefreshing && index != -1) {
				String assignmentId = p.substring(0, index);
				Object value = result.get(assignmentId);
				Boolean recordFlagValue = (Boolean)record.get(p);
				Boolean resultFlagValue = result.get(p);
			
				boolean isDropped = resultFlagValue != null && resultFlagValue.booleanValue();
				boolean wasDropped = recordFlagValue != null && recordFlagValue.booleanValue();
				
				record.set(p, resultFlagValue);
				
				if (isDropped || wasDropped) {
					record.set(assignmentId, null);
					record.set(assignmentId, value);
					//r.setDirty(true);
				}
			}
		}
		
		String courseGrade = result.get(StudentModel.Key.COURSE_GRADE.name());
		
		record.set(StudentModel.Key.COURSE_GRADE.name(), null);
		if (courseGrade != null) 
			record.set(StudentModel.Key.COURSE_GRADE.name(), courseGrade);
		

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

		// FIXME: Move all this to a log event listener
		StringBuilder buffer = new StringBuilder();
		String displayName = (String)record.get(StudentModel.Key.DISPLAY_NAME.name());
		if (displayName != null)
			buffer.append(displayName);
		buffer.append(":").append(event.label);
		//notifier.notify(buffer.toString(), 
		//		"Stored item grade as '{0}' and recalculated course grade to '{1}' ", result.get(property), result.get(StudentModel.Key.COURSE_GRADE.name()));
	
		String message = null;
		if (property.endsWith(StudentModel.COMMENT_TEXT_FLAG)) {
			message = buffer.append("- stored comment as '")
				.append(result.get(property))
				.append("'").toString();
		} else {
			message = buffer.append("- stored item grade as '")
				.append(result.get(property))
				.append("' and recalculated course grade to '").append(result.get(StudentModel.Key.COURSE_GRADE.name()))
				.append("'").toString();
		}
		
		notifier.notify("Success", message);
		Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), message);
	}
	
	private void onUpdateGradeRecord(final GradeRecordUpdate event) {
		
		GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
		ClassType classType = StudentModel.lookupClassType(event.property);
		
		final Record record = event.record;
		final UserEntityUpdateAction<StudentModel> action = new UserEntityUpdateAction<StudentModel>(selectedGradebook, (StudentModel)record.getModel(), event.property, classType, event.value, event.oldValue);		
		
		AsyncCallback<StudentModel> callback = new AsyncCallback<StudentModel>() {
			
			public void onFailure(Throwable caught) {
				record.beginEdit();
				
				String property = event.property;
						
				// Save the exception message on the record
				String failedProperty = property + FAILED_FLAG;
				record.set(failedProperty, caught.getMessage());
						
				// We have to fool the system into thinking that the value has changed, since
				// we snuck in that "Saving grade..." under the radar.
				record.set(property, null);
				record.set(property, event.oldValue);
					
				record.setValid(property, false);
				
				String message = new StringBuilder("Failed to update grade: ").append(caught.getMessage()).toString();
				
				record.endEdit();
				
				notifier.notifyError(caught);
				
				//notifier.notifyError("Exception", message);
				Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), message);			
			}
			
			public void onSuccess(StudentModel result) {
				record.beginEdit();
				onUpdateGradeRecordSuccess(event, result);
				record.endEdit();
				Dispatcher.forwardEvent(GradebookEvents.LearnerGradeRecordUpdated.getEventType(), action);
			}		
			
		};
		
		Gradebook2RPCServiceAsync service = Registry.get("service");
		service.update((StudentModel)record.getModel(), EntityType.LEARNER, action, callback);
	}

	private void onRevertItem(final ItemUpdate event) {
		String property = event.property;
		Record record = event.record;
		
		record.set(property, null);
		record.set(property, event.oldValue);
			
		record.setValid(property, false);
	}
	
	private void onUpdateItemFailure(ItemUpdate event, Throwable caught) {
		
		event.record.reject(true);
		event.record.cancelEdit();
		
		notifier.notifyError(caught);
		
		String message = new StringBuilder("Failed to update item: ").append(caught.getMessage()).toString();
		
		Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), message);
	}
	
	private void onUpdateItemSuccess(ItemUpdate event, ItemModel result) {
		if (event.close)
			Dispatcher.forwardEvent(GradebookEvents.HideFormPanel.getEventType(), Boolean.FALSE);
		
		switch (result.getItemType()) {
		case GRADEBOOK:
			GradebookModel selectedGradebook = Registry
					.get(AppConstants.CURRENT);
			selectedGradebook.setGradebookItemModel(result);
			Dispatcher.forwardEvent(GradebookEvents.ItemUpdated.getEventType(), result);

			boolean isGradebookUpdated = false;
			if (result.getCategoryType() != selectedGradebook.getGradebookItemModel().getCategoryType()) {
				selectedGradebook.getGradebookItemModel().setCategoryType(result.getCategoryType());
				isGradebookUpdated = true;
			}
			if (result.getGradeType() != selectedGradebook.getGradebookItemModel().getGradeType()) {
				selectedGradebook.getGradebookItemModel().setGradeType(result.getGradeType());
				isGradebookUpdated = true;
			}

			if (isGradebookUpdated) {
				Dispatcher.forwardEvent(GradebookEvents.SwitchGradebook.getEventType(),
						selectedGradebook);
			} else {
				Dispatcher.forwardEvent(GradebookEvents.LoadItemTreeModel.getEventType(),
						selectedGradebook);
			}
			break;
		case CATEGORY:
			doUpdateItem(event, result);

			for (ItemModel item : result.getChildren()) {
				doUpdateItem(event, item);
			}
			
			break;
		case ITEM:
			doUpdateItem(event, result);
			break;
		}

		event.record.commit(false);
		event.record.endEdit();
		
	}

	private void onUpdateItem(final ItemUpdate event) {
		Dispatcher.forwardEvent(GradebookEvents.MaskItemTree.getEventType());
		
		Gradebook2RPCServiceAsync service = Registry.get("service");
		AsyncCallback<ItemModel> callback = new AsyncCallback<ItemModel>() {

			public void onFailure(Throwable caught) {
				onUpdateItemFailure(event, caught);
				Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
			}
			
			public void onSuccess(ItemModel result) {
				Dispatcher.forwardEvent(GradebookEvents.BeginItemUpdates.getEventType());
				onUpdateItemSuccess(event, result);
				Dispatcher.forwardEvent(GradebookEvents.EndItemUpdates.getEventType());
				Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
			}
		};
		
		service.update((ItemModel)event.getModifiedItem(), EntityType.ITEM, null, callback);
	}
	
	private void doCreateItem(ItemCreate itemCreate, ItemModel createdItem) {
		TreeStore<ItemModel> treeStore = (TreeStore<ItemModel>)itemCreate.store;
		treeStore.add(createdItem.getParent(), createdItem, true);
		Dispatcher.forwardEvent(GradebookEvents.ItemCreated.getEventType(), createdItem);
		doUpdatePercentCourseGradeTotal(itemCreate.store, itemCreate.item, createdItem);
	}
	
	private void doUpdatePercentCourseGradeTotal(Store store, ItemModel oldItem, ItemModel updatedItem) {
		switch (updatedItem.getItemType()) {
		case CATEGORY:
			ItemModel gradebookItemModel = updatedItem.getParent();
			if (gradebookItemModel != null)
				doUpdateItem(store, null, null, gradebookItemModel);
			break;
		}
	}
	
	private void doUpdateItem(ItemUpdate itemUpdate, ItemModel updatedItem) {
		doUpdatePercentCourseGradeTotal(itemUpdate.store, itemUpdate.item, updatedItem);
		doUpdateItem(itemUpdate.store, itemUpdate.property, itemUpdate.record, updatedItem);
	}
	
	private void doUpdateItem(Store store, String property, Record record, ItemModel updatedItem) {
		TreeStore<ItemModel> treeStore = (TreeStore<ItemModel>)store;
		
		if (property == null || record == null 
				|| !doUpdateViaRecord(property, record, updatedItem)) {
			if (updatedItem != null) {
				treeStore.update(updatedItem);
				Dispatcher.forwardEvent(GradebookEvents.ItemUpdated.getEventType(), updatedItem);
			}
		}
	}
	
	private boolean doUpdateViaRecord(String property, Record record, ItemModel item) {
		// Don't modify the record unless the record's item model has been passed in
		if (!record.getModel().equals(item)) 
			return false;
		
		//record.beginEdit();
		
		// Do it for the property being explicitly changed
		replaceProperty(property, record, item);
		
		// Do it for properties that may be implicitly changed
		ItemModel.Key key = ItemModel.Key.valueOf(property);
		switch (key) {
		case EXTRA_CREDIT:
		case EQUAL_WEIGHT:
		case INCLUDED:
			replaceProperty(ItemModel.Key.PERCENT_CATEGORY.name(), record, item);
			replaceProperty(ItemModel.Key.PERCENT_COURSE_GRADE.name(), record, item);
			break;
		}
		
		//record.endEdit();
		
		return true;
	}
	
	private void replaceProperty(String property, Record record, ItemModel item) {
		Object value = item.get(property);
		
		record.set(property, null);
		
		if (value != null)
			record.set(property, value);
	}
	
}
