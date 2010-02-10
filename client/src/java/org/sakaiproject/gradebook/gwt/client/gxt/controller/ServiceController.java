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

package org.sakaiproject.gradebook.gwt.client.gxt.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.RestBuilder;
import org.sakaiproject.gradebook.gwt.client.RestCallback;
import org.sakaiproject.gradebook.gwt.client.RestBuilder.Method;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.gxt.JsonTranslater;
import org.sakaiproject.gradebook.gwt.client.gxt.LearnerTranslater;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradeMapUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradeRecordUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ItemCreate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ItemUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ShowColumnsEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ConfigurationModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.Configuration;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumn;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.key.GradeMapKey;
import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;
import org.sakaiproject.gradebook.gwt.client.model.key.PermissionKey;
import org.sakaiproject.gradebook.gwt.client.model.type.ClassType;
import org.sakaiproject.gradebook.gwt.client.model.type.ItemType;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.DelayedTask;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class ServiceController extends Controller {

	private DelayedTask showColumnsTask;
	private I18nConstants i18n;
	
	public ServiceController(I18nConstants i18n) {
		this.i18n = i18n;
		registerEventTypes(GradebookEvents.Configuration.getEventType());
		registerEventTypes(GradebookEvents.CreateItem.getEventType());
		registerEventTypes(GradebookEvents.CreatePermission.getEventType());
		registerEventTypes(GradebookEvents.DeleteGradeMap.getEventType());
		registerEventTypes(GradebookEvents.DeleteItem.getEventType());
		registerEventTypes(GradebookEvents.DeletePermission.getEventType());
		registerEventTypes(GradebookEvents.RevertItem.getEventType());
		registerEventTypes(GradebookEvents.ShowColumns.getEventType());
		registerEventTypes(GradebookEvents.UpdateLearnerGradeRecord.getEventType());
		registerEventTypes(GradebookEvents.UpdateGradeMap.getEventType());
		registerEventTypes(GradebookEvents.UpdateItem.getEventType());
	}

	@Override
	public void handleEvent(AppEvent event) {
		switch (GradebookEvents.getEvent(event.getType()).getEventKey()) {
			case CONFIGURATION:
				onConfigure((ConfigurationModel)event.getData());
				break;
			case CREATE_ITEM:
				onCreateItem((ItemCreate)event.getData());
				break;
			case CREATE_PERMISSION:
				onCreatePermission((ModelData)event.getData());
				break;
			case DELETE_GRADE_MAP:
				onDeleteGradeMap();
				break;
			case DELETE_ITEM:
				onDeleteItem((ItemUpdate)event.getData());
				break;
			case DELETE_PERMISSION:
				onDeletePermission((ModelData)event.getData());
				break;
			case REVERT_ITEM:
				onRevertItem((ItemUpdate)event.getData());
				break;
			case SHOW_COLUMNS:
				onShowColumns((ShowColumnsEvent)event.getData());
				break;
			case UPDATE_LEARNER_GRADE_RECORD:
				onUpdateGradeRecord((GradeRecordUpdate)event.getData());
				break;
			case UPDATE_GRADE_MAP:
				onUpdateGradeMap((GradeMapUpdate)event.getData());
				break;
			case UPDATE_ITEM:
				onUpdateItem((ItemUpdate)event.getData());
				break;
		}
	}

	private void onConfigure(final ConfigurationModel event) {
		doConfigure(event);
	}
	
	private void doConfigure(final ConfigurationModel model) {

		Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);	
		
		Long gradebookId = selectedGradebook.getGradebookId();

		JSONObject json = RestBuilder.convertModel(model);

		RestBuilder builder = RestBuilder.getInstance(Method.PUT, 
				GWT.getModuleBaseURL(),
				AppConstants.REST_FRAGMENT,
				AppConstants.CONFIG_FRAGMENT, String.valueOf(gradebookId));
		
		builder.sendRequest(204, 400, json.toString(), new RestCallback() {

			public void onSuccess(Request request, Response response) {
				Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
				Configuration configModel = selectedGradebook.getConfigurationModel();

				Collection<String> propertyNames = model.getPropertyNames();
				if (propertyNames != null) {
					List<String> names = new ArrayList<String>(propertyNames);

					for (int i=0;i<names.size();i++) {
						String name = names.get(i);
						Object value = model.get(name);
						((ModelData)configModel).set(name, value);
					}
				}
			}
			
		});
	}
	
	private void onCreateItem(final ItemCreate event) {
		Dispatcher.forwardEvent(GradebookEvents.MaskItemTree.getEventType());
		
		Gradebook gbModel = Registry.get(AppConstants.CURRENT);
		
		RestBuilder builder = RestBuilder.getInstance(Method.POST, 
				GWT.getModuleBaseURL(),
				AppConstants.REST_FRAGMENT,
				AppConstants.ITEM_FRAGMENT, gbModel.getGradebookUid(),
				String.valueOf(gbModel.getGradebookId()));
		
		JSONObject jsonObject = RestBuilder.convertModel(event.item);
		
		builder.sendRequest(200, 400, jsonObject.toString(), new RestCallback() {

			public void onError(Request request, Throwable exception) {
				super.onError(request, exception);
				Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
			}

			public void onFailure(Request request, Throwable exception) {
				Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(exception, "Failed to create item: "));
				Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
			}

			public void onSuccess(Request request, Response response) {
				String result = response.getText();

				JsonTranslater translater = new JsonTranslater(EnumSet.allOf(ItemKey.class)) {
					protected ModelData newModelInstance() {
						return new ItemModel();
					}
				};
				ItemModel itemModel = (ItemModel)translater.translate(result);
				
				if (event.close)
					Dispatcher.forwardEvent(GradebookEvents.HideFormPanel.getEventType(), Boolean.FALSE);

				switch (itemModel.getItemType()) {
					case GRADEBOOK:
						Gradebook selectedGradebook = Registry
						.get(AppConstants.CURRENT);
						selectedGradebook.setGradebookGradeItem(itemModel);
						Dispatcher
						.forwardEvent(GradebookEvents.ItemUpdated.getEventType(), itemModel);
						Dispatcher.forwardEvent(GradebookEvents.RefreshGradebookItems.getEventType(),
								selectedGradebook);
						break;
					case CATEGORY:
						if (itemModel.isActive())
							doCreateItem(event, itemModel);
						else
							doUpdateItem(event.store, null, null, itemModel);

						for (ModelData m : itemModel.getChildren()) {
							ItemModel item = (ItemModel)m;
							if (item.isActive())
								doCreateItem(event, item);
							else
								doUpdateItem(event.store, null, null, item);
						}
						break;
					case ITEM:
						if (itemModel.isActive())
							doCreateItem(event, itemModel);
						else
							doUpdateItem(event.store, null, null, itemModel);
						break;
				}

				Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
			}
			
		});
	}

	private void onCreatePermission(ModelData model) {
		Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
		
		String gradebookUid = selectedGradebook.getGradebookUid();
		String gradebookId = String.valueOf(selectedGradebook.getGradebookId());

		RestBuilder builder = RestBuilder.getInstance(Method.POST, 
				GWT.getModuleBaseURL(),
				AppConstants.REST_FRAGMENT,
				AppConstants.PERMISSION_FRAGMENT, gradebookUid, gradebookId);
		
		JSONObject jsonObject = RestBuilder.convertModel(model);
		
		builder.sendRequest(200, 400, jsonObject.toString(), new RestCallback() {
			
			@Override
			public void onSuccess(Request request, Response response) {
				String result = response.getText();
				JsonTranslater translater = new JsonTranslater(EnumSet.allOf(PermissionKey.class)) {
					protected ModelData newModelInstance() {
						return new BaseModel();
					}
				};
				ModelData model = translater.translate(result);
				Dispatcher.forwardEvent(
						GradebookEvents.PermissionCreated.getEventType(),
						model);
			}
			
		});
	}
	
	private void onDeleteGradeMap() {
		Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
				
		String gradebookUid = selectedGradebook.getGradebookUid();
		String gradebookId = String.valueOf(selectedGradebook.getGradebookId());

		RestBuilder builder = RestBuilder.getInstance(Method.DELETE, 
				GWT.getModuleBaseURL(),
				AppConstants.REST_FRAGMENT,
				AppConstants.GRADE_MAP_FRAGMENT, gradebookUid, gradebookId);
		

		builder.sendRequest(204, 400, null, new RestCallback() {
			
			@Override
			public void onSuccess(Request request, Response response) {
				Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
				Dispatcher.forwardEvent(GradebookEvents.RefreshGradeScale.getEventType(),
						selectedGradebook);
			}
			
		});
	}
	
	private void onDeleteItemSuccess(ItemUpdate event) {
		Dispatcher.forwardEvent(GradebookEvents.ItemDeleted.getEventType(), event.item);
		TreeStore<ItemModel> treeStore = (TreeStore<ItemModel>)event.store;
		treeStore.remove((ItemModel) ((BaseTreeModel)event.item).getParent(), (ItemModel)event.item);
	}

	private void onDeleteItem(final ItemUpdate event) {
		Dispatcher.forwardEvent(GradebookEvents.MaskItemTree.getEventType());
		event.item.setRemoved(Boolean.TRUE);

		RestBuilder builder = RestBuilder.getInstance(Method.DELETE, 
				GWT.getModuleBaseURL(),
				AppConstants.REST_FRAGMENT,
				AppConstants.ITEM_FRAGMENT);
		
		JSONObject jsonObject = RestBuilder.convertModel((ModelData)event.item);
		
		builder.sendRequest(200, 400, jsonObject.toString(), new RestCallback() {

			public void onError(Request request, Throwable exception) {
				super.onError(request, exception);
				onUpdateItemFailure(event, exception);
				Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
			}

			public void onFailure(Request request, Throwable exception) {
				onUpdateItemFailure(event, exception);
				Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
			}

			public void onSuccess(Request request, Response response) {
				String result = response.getText();

				JsonTranslater translater = new JsonTranslater(EnumSet.allOf(ItemKey.class)) {
					protected ModelData newModelInstance() {
						return new ItemModel();
					}
				};
				ItemModel itemModel = (ItemModel)translater.translate(result);
				
				Dispatcher.forwardEvent(GradebookEvents.BeginItemUpdates.getEventType());
				onUpdateItemSuccess(event, itemModel);
				onDeleteItemSuccess(event);
				Dispatcher.forwardEvent(GradebookEvents.EndItemUpdates.getEventType());
				Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
			}
			
		});
	}

	private void onDeletePermission(ModelData model) {
		Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
		
		Long gradebookId = selectedGradebook.getGradebookId();
		model.set(PermissionKey.GRADEBOOK_ID.name(), gradebookId);
		
		RestBuilder builder = RestBuilder.getInstance(Method.DELETE, 
				GWT.getModuleBaseURL(),
				AppConstants.REST_FRAGMENT,
				AppConstants.PERMISSION_FRAGMENT);
		
		JSONObject jsonObject = RestBuilder.convertModel(model);
		
		builder.sendRequest(200, 400, jsonObject.toString(), new RestCallback() {
			
			@Override
			public void onSuccess(Request request, Response response) {
				String result = response.getText();
				JsonTranslater translater = new JsonTranslater(EnumSet.allOf(PermissionKey.class)) {
					protected ModelData newModelInstance() {
						return new BaseModel();
					}
				};
				ModelData model = translater.translate(result);
				Dispatcher.forwardEvent(
						GradebookEvents.PermissionDeleted.getEventType(),
						model);
			}
			
		});
	}
	
	private void onUpdateGradeRecordFailure(GradeRecordUpdate event, Throwable caught) {
		Record record = event.record;
		String property = event.property;

		// Save the exception message on the record
		String failedMessage = caught != null && caught.getMessage() != null ? caught.getMessage() : "Failed";
		setFailedFlag(record, property, failedMessage);
		
		// We have to fool the system into thinking that the value has changed, since
		// we snuck in that "Saving grade..." under the radar.
		if (event.oldValue == null && event.value != null)
			record.set(property, event.value);
		else 
			record.set(property, null);
		
		record.set(property, event.oldValue);

		record.setValid(property, false);

		String message = new StringBuilder().append(i18n.gradeUpdateFailedException()).append(" ").append(record.get(LearnerKey.DISPLAY_NAME.name())).append(". ").toString();
		
		Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(caught, message));
	}
	
	private void setFailedFlag(Record record, String property, String message) {
		String failedProperty = 
			new StringBuilder(property).append(AppConstants.FAILED_FLAG).toString();
		if (record.isModified(failedProperty))
			record.set(failedProperty, null);
		if (message != null) 
			record.set(failedProperty, message);
	}
	
	private void setSuccessFlag(Record record, String property) {
		String successProperty = 
			new StringBuilder(property).append(AppConstants.SUCCESS_FLAG).toString();
		if (record.isModified(successProperty))
			record.set(successProperty, null);
		record.set(successProperty, "Success");
	}
	
	private void onUpdateGradeRecordSuccess(GradeRecordUpdate event, ModelData result) {
		Record record = event.record;
		String property = event.property;

		// Need to refresh any items that may have been dropped
		for (String p : result.getPropertyNames()) {
			boolean needsRefreshing = false;

			int index = -1;

			if (p.endsWith(AppConstants.DROP_FLAG)) {
				index = p.indexOf(AppConstants.DROP_FLAG);
				needsRefreshing = true;
			} else if (p.endsWith(AppConstants.COMMENTED_FLAG)) {
				index = p.indexOf(AppConstants.COMMENTED_FLAG);
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
				}
			}
		}

		String courseGrade = result.get(LearnerKey.COURSE_GRADE.name());

		if (record.isModified(LearnerKey.COURSE_GRADE.name()))
			record.set(LearnerKey.COURSE_GRADE.name(), null);
		if (courseGrade != null) 
			record.set(LearnerKey.COURSE_GRADE.name(), courseGrade);
		
		String calculatedGrade = result.get(LearnerKey.CALCULATED_GRADE.name());
		if (record.isModified(LearnerKey.CALCULATED_GRADE.name()))
			record.set(LearnerKey.CALCULATED_GRADE.name(), null);
		if (calculatedGrade != null)
			record.set(LearnerKey.CALCULATED_GRADE.name(), calculatedGrade);
		
		String letterGrade = result.get(LearnerKey.LETTER_GRADE.name());
		if (record.isModified(LearnerKey.LETTER_GRADE.name()))
			record.set(LearnerKey.LETTER_GRADE.name(), null);
		if (letterGrade != null)
			record.set(LearnerKey.LETTER_GRADE.name(), letterGrade);

		// Ensure that we clear out any older failure messages
		// Save the exception message on the record
		setFailedFlag(record, property, null);
		setSuccessFlag(record, property);

		record.setValid(property, true);

		Object value = result.get(property);

		if (value == null)
			record.set(property, null);
		else
			record.set(property, value);

		// FIXME: Move all this to a log event listener
		StringBuilder buffer = new StringBuilder();
		String displayName = (String)record.get(LearnerKey.DISPLAY_NAME.name());
		if (displayName != null)
			buffer.append(displayName);
		buffer.append(":").append(event.label);
		
		String message = null;
		if (property.endsWith(AppConstants.COMMENT_TEXT_FLAG)) {
			message = buffer.append("- stored comment as '")
			.append(result.get(property))
			.append("'").toString();
		} else {
			message = buffer.append("- stored item grade as '")
			.append(result.get(property))
			.append("' and recalculated course grade to '").append(result.get(LearnerKey.COURSE_GRADE.name()))
			.append("'").toString();
		}

		Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent("Success", message));
	}

	private void onUpdateGradeMap(final GradeMapUpdate event) {
		Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
		final Record record = event.record;
		
		String gradebookUid = selectedGradebook.getGradebookUid();
		String gradebookId = String.valueOf(selectedGradebook.getGradebookId());
		String letterGrade = (String)record.get(GradeMapKey.LETTER_GRADE.name());
		
		RestBuilder builder = RestBuilder.getInstance(Method.PUT, 
				GWT.getModuleBaseURL(),
				AppConstants.REST_FRAGMENT,
				AppConstants.GRADE_MAP_FRAGMENT, gradebookUid, gradebookId, letterGrade);
		
		Double value = record == null ? null : (Double)event.value;
		Double startValue = record == null ? null : (Double)event.startValue;
		
		JSONObject json = new JSONObject();
		if (value != null)
			json.put(AppConstants.VALUE_CONSTANT, new JSONNumber(value.doubleValue()));
		if (startValue != null)
			json.put(AppConstants.START_VALUE_CONSTANT, new JSONNumber(startValue.doubleValue()));
		
		builder.sendRequest(204, 400, json.toString(), new RestCallback() {

			@Override
			public void onFailure(Request request, Throwable exception) {
				super.onFailure(request, exception);
				record.reject(false);
			}
			
			@Override
			public void onSuccess(Request request, Response response) {
				Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
				Dispatcher.forwardEvent(GradebookEvents.RefreshGradeScale.getEventType(),
						selectedGradebook);
				record.commit(false);
			}
			
		});
	}
	
	private void onUpdateGradeRecord(final GradeRecordUpdate event) {

		final Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);	
		
		ClassType classType = DataTypeConversionUtil.lookupClassType(event.property, selectedGradebook.getGradebookItemModel().getGradeType());

		final Record record = event.record;
		if ((event.oldValue == null || event.oldValue.equals("")) 
				&& (event.value == null || event.value.equals("")))
			return;
		
		final UserEntityUpdateAction<ModelData> action = new UserEntityUpdateAction<ModelData>(selectedGradebook, record.getModel(), event.property, classType, event.value, event.oldValue);		

		String gradebookUid = selectedGradebook.getGradebookUid();
		String entity = null;
		String studentUid = (String)record.getModel().get(LearnerKey.UID.name());
		String itemId = (String)event.property;
		
		JSONObject json = new JSONObject();

		switch (classType) {
		case STRING:
			if (event.value != null)
				json.put(AppConstants.STR_VALUE_CONSTANT, new JSONString((String)event.value));
			if (event.oldValue != null)
				json.put(AppConstants.STR_START_VALUE_CONSTANT, new JSONString((String)event.oldValue));
			json.put("numeric", JSONBoolean.getInstance(false));
			entity = "string";
			break;
		case DOUBLE:
			if (event.value != null)
				json.put(AppConstants.VALUE_CONSTANT, new JSONNumber((Double)event.value));
			if (event.oldValue != null)
				json.put(AppConstants.START_VALUE_CONSTANT, new JSONNumber((Double)event.oldValue));
			json.put("numeric", JSONBoolean.getInstance(true));
			entity = "numeric";
			break;
		}
		
		if (event.property.endsWith(AppConstants.COMMENT_TEXT_FLAG))
			entity = "comment";

		RestBuilder builder = RestBuilder.getInstance(Method.PUT, 
				GWT.getModuleBaseURL(),
				AppConstants.REST_FRAGMENT,
				AppConstants.LEARNER_FRAGMENT, entity, gradebookUid, itemId, studentUid);
		
		
		builder.sendRequest(200, 400, json.toString(), new RestCallback() {

			public void onError(Request request, Throwable exception) {
				super.onError(request, exception);
				record.beginEdit();
				onUpdateGradeRecordFailure(event, exception);
				record.endEdit();
			}

			public void onFailure(Request request, Throwable exception) {
				record.beginEdit();
				onUpdateGradeRecordFailure(event, exception);
				record.endEdit();
			}

			public void onSuccess(Request request, Response response) {
				JsonTranslater reader = new LearnerTranslater(selectedGradebook.getGradebookItemModel(), false);
				ModelData result = reader.translate(response.getText());
				
				record.beginEdit();
				onUpdateGradeRecordSuccess(event, result);
				record.endEdit();
				//record.commit(false);
				Dispatcher.forwardEvent(GradebookEvents.LearnerGradeRecordUpdated.getEventType(), action);				
			}
			
		});
		
		/*
		AsyncCallback<ModelData> callback = new AsyncCallback<ModelData>() {

			public void onFailure(Throwable caught) {

				record.beginEdit();

				String property = event.property;

				// Save the exception message on the record
				String failedProperty = property + FAILED_FLAG;
				record.set(failedProperty, caught.getMessage());

				// We have to fool the system into thinking that the value has changed, since
				// we snuck in that "Saving grade..." under the radar.
				if (event.oldValue == null && event.value != null)
					record.set(property, event.value);
				else 
					record.set(property, null);
				record.set(property, event.oldValue);

				record.setValid(property, false);

				record.endEdit();

				Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(caught, "Failed to update grade: "));			
			}

			public void onSuccess(ModelData result) {
				record.beginEdit();
				onUpdateGradeRecordSuccess(event, result);
				record.endEdit();
				Dispatcher.forwardEvent(GradebookEvents.LearnerGradeRecordUpdated.getEventType(), action);
			}		

		};

		Gradebook2RPCServiceAsync service = Registry.get("service");
		service.update((ModelData)record.getModel(), EntityType.LEARNER, action, SecureToken.get(), callback);
		*/
	}

	private void onRevertItem(final ItemUpdate event) {
		String property = event.property;
		Record record = event.record;

		record.set(property, null);
		record.set(property, event.oldValue);

		record.setValid(property, false);
	}

	private void onShowColumns(final ShowColumnsEvent event) {
		if (showColumnsTask != null)
			showColumnsTask.cancel();
		
		showColumnsTask = new DelayedTask(new Listener<BaseEvent>() {
		
			public void handleEvent(BaseEvent be) {
				doShowColumns(event);
			}
		});
		
		showColumnsTask.delay(1000);
	}
	
	private void doShowColumns(ShowColumnsEvent event) {
		Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
		ConfigurationModel model = new ConfigurationModel(selectedGradebook.getGradebookId());
		
		if (event.isSingle) {
			boolean hidden = event.isHidden;

			if (event.isFixed)
				buildColumnConfigModel(model, event.fixedModel, hidden);
			else
				buildColumnConfigModel(model, event.model, hidden);

			
		} else {
			
			for (String id : event.fullStaticIdSet) {
				boolean isHidden = !event.visibleStaticIdSet.contains(id);
				buildColumnConfigModel(model, id, isHidden);
			}

		}
		
		doConfigure(model);
		
		/*
		
		Gradebook2RPCServiceAsync service = Registry.get(AppConstants.SERVICE);

		AsyncCallback<ConfigurationModel> callback = new AsyncCallback<ConfigurationModel>() {

			public void onFailure(Throwable caught) {
				// FIXME: Should we notify the user when this fails?
			}

			public void onSuccess(ConfigurationModel result) {
				GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
				ConfigurationModel configModel = selectedGradebook.getConfigurationModel();

				Collection<String> propertyNames = result.getPropertyNames();
				if (propertyNames != null) {
					List<String> names = new ArrayList<String>(propertyNames);

					for (int i=0;i<names.size();i++) {
						String name = names.get(i);
						String value = result.get(name);
						configModel.set(name, value);
					}
				}
			}

		};

		service.update(model, EntityType.CONFIGURATION, null, SecureToken.get(), callback);*/
	}
	
	private void buildColumnConfigModel(Configuration model, String identifier, boolean isHidden) {
		model.setColumnHidden(AppConstants.ITEMTREE, identifier, Boolean.valueOf(isHidden));
	}
	
	private void buildColumnConfigModel(Configuration model, FixedColumn fixedModel, boolean isHidden) {
		model.setColumnHidden(AppConstants.ITEMTREE, fixedModel.getIdentifier(), Boolean.valueOf(isHidden));
	}
	
	private void buildColumnConfigModel(Configuration model, ItemModel itemModel, boolean isHidden) {
		switch (itemModel.getItemType()) {
		case GRADEBOOK:
		case CATEGORY:
			for (int i=0;i<itemModel.getChildCount();i++) {
				ItemModel child = (ItemModel)itemModel.getChild(i);
				buildColumnConfigModel(model, child, isHidden);
			}
			break;
		case ITEM:
			model.setColumnHidden(AppConstants.ITEMTREE, itemModel.getIdentifier(), Boolean.valueOf(isHidden));
			break;
		}
	}

	private void onUpdateItemFailure(ItemUpdate event, Throwable caught) {

		if (event.record != null) {
			Map<String, Object> changes = event.record.getChanges();
			
			event.record.reject(false);
		}
		
		Dispatcher.forwardEvent(GradebookEvents.FailedToUpdateItem.getEventType(), event);
		Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(caught, "Failed to update item: "));
	}

	private void onUpdateItemSuccess(ItemUpdate event, ItemModel result) {
		if (event.close)
			Dispatcher.forwardEvent(GradebookEvents.HideFormPanel.getEventType(), Boolean.FALSE);

		boolean isCategoryTypeUpdated = false;
		boolean isGradeTypeUpdated = false;
		boolean isGradeScaleUpdated = false;
		boolean isReleaseGradesUpdated = false;
		boolean isReleaseItemsUpdated = false;
		boolean isExtraCreditScaled = false;
		
		Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);

		if (event.record != null && event.record.isEditing()) {
			Map<String, Object> changes = event.record.getChanges();

			isGradeScaleUpdated = changes != null && changes.get(ItemKey.GRADESCALEID.name()) != null;
			isGradeTypeUpdated = changes != null && changes.get(ItemKey.GRADETYPE.name()) != null;
			isCategoryTypeUpdated = changes != null && changes.get(ItemKey.CATEGORYTYPE.name()) != null;
			isReleaseGradesUpdated = changes != null && changes.get(ItemKey.RELEASEGRADES.name()) != null;
			isReleaseItemsUpdated = changes != null && changes.get(ItemKey.RELEASEITEMS.name()) != null;
			isExtraCreditScaled = changes != null && changes.get(ItemKey.EXTRA_CREDIT_SCALED.name()) != null;
			
			event.record.commit(false);
		}

		switch (result.getItemType()) {
			case GRADEBOOK:

				Dispatcher.forwardEvent(GradebookEvents.ItemUpdated.getEventType(), result);

				selectedGradebook.setGradebookGradeItem(result);
				
				if (isCategoryTypeUpdated || isReleaseGradesUpdated || isReleaseItemsUpdated) {
					Dispatcher.forwardEvent(GradebookEvents.RefreshGradebookSetup.getEventType(),
							selectedGradebook);
				} 

				if (isGradeScaleUpdated) {
					Dispatcher.forwardEvent(GradebookEvents.RefreshGradeScale.getEventType(),
							selectedGradebook);
				}
				
				if (isGradeTypeUpdated) {
					Dispatcher.forwardEvent(GradebookEvents.GradeTypeUpdated.getEventType(), selectedGradebook);
				}

				if (event.item.getItemType() != ItemType.GRADEBOOK || isGradeTypeUpdated || isCategoryTypeUpdated ||
						isExtraCreditScaled) {
					Dispatcher.forwardEvent(GradebookEvents.RefreshGradebookItems.getEventType(),
							selectedGradebook);

					Dispatcher.forwardEvent(GradebookEvents.RefreshCourseGrades.getEventType(),
							selectedGradebook);
				}

				break;
			case CATEGORY:

				doUpdateItem(event, result);

				for (ModelData item : result.getChildren()) {

					doUpdateItem(event, (ItemModel) item);
				}

				if (event.getModifiedItem() != null && event.getModifiedItem().getItemType() != ItemType.CATEGORY)
					return;

				break;
			case ITEM:
				doUpdateItem(event, result);
				break;
		}

	}

	private void onUpdateItem(final ItemUpdate event) {
		
		Dispatcher.forwardEvent(GradebookEvents.MaskItemTree.getEventType());
		
		RestBuilder builder = RestBuilder.getInstance(Method.PUT, 
				GWT.getModuleBaseURL(),
				AppConstants.REST_FRAGMENT,
				AppConstants.ITEM_FRAGMENT);
		
		JSONObject jsonObject = RestBuilder.convertModel((ModelData)event.getModifiedItem());
		
		builder.sendRequest(200, 400, jsonObject.toString(), new RestCallback() {

			public void onError(Request request, Throwable exception) {
				super.onError(request, exception);
				onUpdateItemFailure(event, exception);
				Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
			}

			public void onFailure(Request request, Throwable exception) {
				onUpdateItemFailure(event, exception);
				Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
			}

			public void onSuccess(Request request, Response response) {
				String result = response.getText();

				JsonTranslater translater = new JsonTranslater(EnumSet.allOf(ItemKey.class)) {
					protected ModelData newModelInstance() {
						return new ItemModel();
					}
				};
				ItemModel itemModel = (ItemModel)translater.translate(result);
				
				Dispatcher.forwardEvent(GradebookEvents.BeginItemUpdates.getEventType());
				onUpdateItemSuccess(event, itemModel);
				Dispatcher.forwardEvent(GradebookEvents.EndItemUpdates.getEventType());
				Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
			}
			
		});
		
		/*
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

		service.update((ItemModel)event.getModifiedItem(), EntityType.ITEM, null, SecureToken.get(), callback);
		*/		
	}

	private void doCreateItem(ItemCreate itemCreate, ItemModel createdItem) {
		TreeStore<ItemModel> treeStore = (TreeStore<ItemModel>)itemCreate.store;
		treeStore.add((ItemModel) createdItem.getParent(), createdItem, true);
		Dispatcher.forwardEvent(GradebookEvents.ItemCreated.getEventType(), createdItem);
		doUpdatePercentCourseGradeTotal(itemCreate.store, itemCreate.item, createdItem);
	}

	private void doUpdatePercentCourseGradeTotal(Store store, Item oldItem, ItemModel updatedItem) {
		switch (updatedItem.getItemType()) {
			case CATEGORY:
				ItemModel gradebookItemModel = (ItemModel) updatedItem.getParent();
				if (gradebookItemModel != null && gradebookItemModel.getItemType() == ItemType.GRADEBOOK)
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

		if (updatedItem.isActive() && record != null) {
			record.beginEdit();
			for (String p : updatedItem.getPropertyNames()) {
				replaceProperty(p, record, updatedItem);
			}
			record.commit(false);
			Dispatcher.forwardEvent(GradebookEvents.ItemUpdated.getEventType(), updatedItem);
		} else {
			treeStore.update(updatedItem);
		}
	}

	private boolean doUpdateViaRecord(Record record, ItemModel item) {
		// Don't modify the record unless the record's item model has been passed in
		if (!record.getModel().equals(item)) 
			return false;

		record.beginEdit();

		for (String property : item.getPropertyNames()) {
			// Do it for the property being explicitly changed
			replaceProperty(property, record, item);
		}

		record.endEdit();

		return true;
	}

	private void replaceProperty(String property, Record record, ItemModel item) {
		Object value = item.get(property);

		record.set(property, null);

		if (value != null)
			record.set(property, value);
	}

	private Item getActiveItem(ItemModel parent) {
		if (parent.isActive())
			return parent;

		for (ModelData m : parent.getChildren()) {
			ItemModel c = (ItemModel)m;
			if (c.isActive()) {
				return c;
			}

			if (c.getChildCount() > 0) {
				Item activeItem = getActiveItem(c);

				if (activeItem != null)
					return activeItem;
			}
		}

		return null;
	}
}
