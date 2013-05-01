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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.RestBuilder;
import org.sakaiproject.gradebook.gwt.client.RestBuilder.Method;
import org.sakaiproject.gradebook.gwt.client.RestCallback;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.gxt.JsonUtil;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradeMapUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradeRecordUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ItemCreate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ItemUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ShowColumnsEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ConfigurationModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityOverlay;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.LearnerModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.LearnerUtil;
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
import org.sakaiproject.gradebook.gwt.client.util.Base64;

import com.extjs.gxt.ui.client.Registry;
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

		String jsonText = model == null ? null : model.getJSON();

		RestBuilder builder = RestBuilder.getInstance(Method.PUT, 
				GWT.getModuleBaseURL(),
				AppConstants.REST_FRAGMENT,
				AppConstants.CONFIG_FRAGMENT, String.valueOf(gradebookId));

		builder.sendRequest(200, 400, jsonText, new RestCallback() {

			public void onSuccess(Request request, Response response) {

				EntityOverlay overlay = JsonUtil.toOverlay(response.getText());
				ModelData m = new ConfigurationModel(overlay);

				Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
				Configuration configModel = selectedGradebook.getConfigurationModel();

				Collection<String> propertyNames = m.getPropertyNames();
				if (propertyNames != null) {
					List<String> names = new ArrayList<String>(propertyNames);

					for (int i=0;i<names.size();i++) {
						String name = names.get(i);
						Object value = m.get(name);
						configModel.set(name, value);
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

		ItemModel model = event.item;
		String jsonText = model == null ? null : model.getJSON();

		builder.sendRequest(200, 400, jsonText, new RestCallback() {

			public void onError(Request request, Throwable exception) {
				super.onError(request, exception);
				Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
				Dispatcher.forwardEvent(GradebookEvents.HideUserFeedback.getEventType());
				Dispatcher.forwardEvent(GradebookEvents.UnmaskMultiGradeGrid.getEventType());
			}

			public void onFailure(Request request, Throwable exception) {
				Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(exception, "Failed to create item: "));
				Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
				Dispatcher.forwardEvent(GradebookEvents.HideUserFeedback.getEventType());
				Dispatcher.forwardEvent(GradebookEvents.UnmaskMultiGradeGrid.getEventType());
			}

			public void onSuccess(Request request, Response response) {
				
				EntityOverlay overlay = JsonUtil.toOverlay(response.getText());
				ItemModel itemModel = new ItemModel(overlay); 

				switch (itemModel.getItemType()) {
				case GRADEBOOK:
					Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
					selectedGradebook.setGradebookGradeItem(itemModel);
					Dispatcher.forwardEvent(GradebookEvents.ItemUpdated.getEventType(), itemModel);
					Dispatcher.forwardEvent(GradebookEvents.RefreshGradebookItems.getEventType(),
							selectedGradebook);
					Dispatcher.forwardEvent(GradebookEvents.RefreshCourseGrades.getEventType(),
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
				Dispatcher.forwardEvent(GradebookEvents.HideUserFeedback.getEventType());
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

		String jsonText = model == null ? null : ((EntityModel)model).getJSON();

		builder.sendRequest(200, 400, jsonText, new RestCallback() {

			@Override
			public void onSuccess(Request request, Response response) {
				
				EntityOverlay overlay = JsonUtil.toOverlay(response.getText());
				ModelData model = new EntityModel(overlay); //translater.translate(result);
				Dispatcher.forwardEvent(GradebookEvents.PermissionCreated.getEventType(),
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
				Dispatcher.forwardEvent(GradebookEvents.RefreshCourseGrades.getEventType(),
						selectedGradebook);
			}

		});
	}

	private void onDeleteItemSuccess(ItemUpdate event) {
		Dispatcher.forwardEvent(GradebookEvents.ItemDeleted.getEventType(), event.item);
		TreeStore<ItemModel> treeStore = (TreeStore<ItemModel>)event.store;
		ItemModel categoryItemModel = getCategoryItemModel(event.item.getCategoryId());
		treeStore.remove(categoryItemModel, (ItemModel)event.item);
	}

	private void onDeleteItem(final ItemUpdate event) {
		Dispatcher.forwardEvent(GradebookEvents.MaskItemTree.getEventType());

		if(null != event.item) {
			event.item.setRemoved(Boolean.TRUE);
		}

		RestBuilder builder = RestBuilder.getInstance(Method.DELETE, 
				GWT.getModuleBaseURL(),
				AppConstants.REST_FRAGMENT,
				AppConstants.ITEM_FRAGMENT);

		ItemModel model = (ItemModel)event.item;
		String jsonText = model == null ? null : model.getJSON();

		builder.sendRequest(200, 400, jsonText, new RestCallback() {

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
				
				EntityOverlay overlay = JsonUtil.toOverlay(response.getText());
				ItemModel itemModel = new ItemModel(overlay);

				Dispatcher.forwardEvent(GradebookEvents.BeginItemUpdates.getEventType());
				onUpdateItemSuccess(event, itemModel);
				onDeleteItemSuccess(event);
				Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
				Dispatcher.forwardEvent(GradebookEvents.EndItemUpdates.getEventType(), selectedGradebook);
				Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
			}
		});
	}

	private void onDeletePermission(ModelData model) {
		Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);

		Long gradebookId = selectedGradebook.getGradebookId();

		if(null != model) {
			model.set(PermissionKey.L_GB_ID.name(), gradebookId);
		}

		RestBuilder builder = RestBuilder.getInstance(Method.DELETE, 
				GWT.getModuleBaseURL(),
				AppConstants.REST_FRAGMENT,
				AppConstants.PERMISSION_FRAGMENT, selectedGradebook.getGradebookUid());

		String jsonText = model == null ? null : ((EntityModel)model).getJSON();

		builder.sendRequest(200, 400, jsonText, new RestCallback() {

			@Override
			public void onSuccess(Request request, Response response) {
				
				EntityOverlay overlay = JsonUtil.toOverlay(response.getText());
				ModelData model = new EntityModel(overlay);
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

		String message = new StringBuilder().append(i18n.gradeUpdateFailedException()).append(" ").append(record.get(LearnerKey.S_DSPLY_NM.name())).append(". ").toString();

		Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(caught, message));
		event.onError(event);
	}

	private void setFailedFlag(Record record, String property, String message) {
		String failedProperty = DataTypeConversionUtil.buildFailedKey(property);
		if (record.isModified(failedProperty))
			record.set(failedProperty, null);
		if (message != null) 
			record.set(failedProperty, message);
	}

	private void setSuccessFlag(Record record, String property) {
		String successProperty = DataTypeConversionUtil.buildSuccessKey(property);
		if (record.isModified(successProperty))
			record.set(successProperty, null);
		record.set(successProperty, "Success");
	}

	private void onUpdateGradeRecordSuccess(GradeRecordUpdate event, ModelData result) {
		Record record = event.record;
		String property = event.property;


		Collection<String> recordPropertyNames = record.getPropertyNames();
		Collection<String> resultPropertyNames = result.getPropertyNames();

		Set<String> unionPropertyNames = new HashSet<String>();
		if (recordPropertyNames != null)
			unionPropertyNames.addAll(recordPropertyNames);
		if (resultPropertyNames != null)
			unionPropertyNames.addAll(resultPropertyNames);

		if (unionPropertyNames != null) {
			for (String p : unionPropertyNames) {
				// We're only interested in assignment ids here
				if (!LearnerUtil.isFixed(p)) {

					Object newObj = result.get(p);
					Object oldObj = record.get(p);

					if (newObj == null && oldObj != null) {
						// If the entry is now missing, we want to remove it
						record.set(p, null);
					} else {
						// Otherwise, we simply replace the entry
						record.set(p, newObj);
					}
				}
			}
		}

		String courseGrade = result.get(LearnerKey.S_CRS_GRD.name());

		if (courseGrade != null && record.isModified(LearnerKey.S_CRS_GRD.name()))
			record.set(LearnerKey.S_CRS_GRD.name(), null);
		record.set(LearnerKey.S_CRS_GRD.name(), courseGrade);

		String calculatedGrade = result.get(LearnerKey.S_CALC_GRD.name());
		if (calculatedGrade != null && record.isModified(LearnerKey.S_CALC_GRD.name()))
			record.set(LearnerKey.S_CALC_GRD.name(), null);
		record.set(LearnerKey.S_CALC_GRD.name(), calculatedGrade);

		String letterGrade = result.get(LearnerKey.S_LTR_GRD.name());
		if (letterGrade != null && record.isModified(LearnerKey.S_LTR_GRD.name()))
			record.set(LearnerKey.S_LTR_GRD.name(), null);
		record.set(LearnerKey.S_LTR_GRD.name(), letterGrade);

		// Ensure that we clear out any older failure messages
		// Save the exception message on the record
		setFailedFlag(record, property, null);
		setSuccessFlag(record, property);

		record.setValid(property, true);

		// FIXME: Move all this to a log event listener
		StringBuilder buffer = new StringBuilder();
		String displayName = (String)record.get(LearnerKey.S_DSPLY_NM.name());
		if (displayName != null)
			buffer.append(displayName);
		buffer.append(":").append(event.label);

		String message = null;
		if (property.startsWith(AppConstants.COMMENT_TEXT_FLAG)) {
			message = buffer.append("- stored comment as '")
			.append(result.get(property))
			.append("'").toString();
		} else {
			message = buffer.append("- stored item grade as '")
			.append(result.get(property))
			.append("' and recalculated course grade to '").append(result.get(LearnerKey.S_CRS_GRD.name()))
			.append("'").toString();
		}

		Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent("Success", message));
		event.onSuccess(event);
	}

	private void onUpdateGradeMap(final GradeMapUpdate event) {
		Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
		final Record record = event.record;

		String gradebookUid = selectedGradebook.getGradebookUid();
		String gradebookId = String.valueOf(selectedGradebook.getGradebookId());
		String letterGrade = (null != record) ? (String)record.get(GradeMapKey.S_LTR_GRD.name()) : null;

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
				Dispatcher.forwardEvent(GradebookEvents.GradeScaleUpdateError.getEventType());
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
		String studentUid = (String)record.getModel().get(LearnerKey.S_UID.name());
		String itemId = (String)event.property;

		JSONObject json = new JSONObject();

		switch (classType) {
		case BOOLEAN:
			if (event.value != null)
				json.put(AppConstants.BOOL_VALUE_CONSTANT, JSONBoolean.getInstance(DataTypeConversionUtil.checkBoolean((Boolean)event.value)));
			if (event.oldValue != null)
				json.put(AppConstants.BOOL_START_VALUE_CONSTANT, JSONBoolean.getInstance(DataTypeConversionUtil.checkBoolean((Boolean)event.oldValue)));
			entity = AppConstants.EXCUSE_FRAGMENT;
			break;
		case STRING:
			if (event.value != null)
				json.put(AppConstants.STR_VALUE_CONSTANT, new JSONString((String)event.value));
			if (event.oldValue != null)
				json.put(AppConstants.STR_START_VALUE_CONSTANT, new JSONString((String)event.oldValue));
			json.put(AppConstants.NUMERIC_FRAGMENT, JSONBoolean.getInstance(false));
			entity = AppConstants.STRING_FRAGMENT;
			break;
		case DOUBLE:
			if (event.value != null)
				json.put(AppConstants.VALUE_CONSTANT, new JSONNumber((Double)event.value));
			if (event.oldValue != null)
				json.put(AppConstants.START_VALUE_CONSTANT, new JSONNumber((Double)event.oldValue));
			json.put(AppConstants.NUMERIC_FRAGMENT, JSONBoolean.getInstance(true));
			entity = AppConstants.NUMERIC_FRAGMENT;
			break;
		}

		if (event.property.startsWith(AppConstants.COMMENT_TEXT_FLAG))
			entity = "comment";

		RestBuilder builder = RestBuilder.getInstance(Method.PUT, 
				GWT.getModuleBaseURL(),
				AppConstants.REST_FRAGMENT,
				AppConstants.LEARNER_FRAGMENT, entity, gradebookUid, itemId,  Base64.encode(studentUid));

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
				
				EntityOverlay overlay = JsonUtil.toOverlay(response.getText());
				LearnerModel result = new LearnerModel(overlay);

				record.beginEdit();
				onUpdateGradeRecordSuccess(event, result);
				record.endEdit();

				Dispatcher.forwardEvent(GradebookEvents.LearnerGradeRecordUpdated.getEventType(), action);				
			}

		});
	}

	private void onRevertItem(final ItemUpdate event) {
		String property = event.property;
		Record record = event.record;

		record.set(property, null);
		record.set(property, event.oldValue);

		record.setValid(property, false);
	}

	private ShowColumnsEvent lastEvent = null;

	private void onShowColumns(final ShowColumnsEvent event) {
		if (lastEvent != null && event != null && event.equals(lastEvent)) {
			if (showColumnsTask != null)
				showColumnsTask.cancel();
		}

		lastEvent = event;
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

			event.record.reject(false);
		}

		Dispatcher.forwardEvent(GradebookEvents.FailedToUpdateItem.getEventType(), event);
		Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(caught, "Failed to update item: "));
	}

	private void onUpdateItemSuccess(ItemUpdate event, ItemModel result) {

		boolean isCategoryTypeUpdated = false;
		boolean isGradeTypeUpdated = false;
		boolean isGradeScaleUpdated = false;
		boolean isReleaseGradesUpdated = false;
		boolean isReleaseItemsUpdated = false;
		boolean isExtraCreditScaled = false;

		Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);

		if (event.record != null && event.record.isEditing()) {
			Map<String, Object> changes = event.record.getChanges();

			isGradeScaleUpdated = changes != null && changes.get(ItemKey.L_GRD_SCL_ID.name()) != null;
			isGradeTypeUpdated = changes != null && changes.get(ItemKey.G_GRD_TYPE.name()) != null;
			isCategoryTypeUpdated = changes != null && changes.get(ItemKey.C_CTGRY_TYPE.name()) != null;
			isReleaseGradesUpdated = changes != null && changes.get(ItemKey.B_REL_GRDS.name()) != null;
			isReleaseItemsUpdated = changes != null && changes.get(ItemKey.B_REL_ITMS.name()) != null;
			isExtraCreditScaled = changes != null && changes.get(ItemKey.B_SCL_X_CRDT.name()) != null;
			
			ItemModel item = null;
			ModelData returnedItem = null;
			if (!changes.isEmpty()){
				item = (ItemModel) event.record.getModel();
				returnedItem = findItemModel(item, result);				
			}
			if( null == returnedItem ) {
				///send notification for alert
			} else {
				item.setProperties(returnedItem.getProperties());
			}
			

			event.record.commit(false);
		} else {
			//// I think this needs a user notification or some other check to make sure that the state is right.
			GWT.log("!!!! swallowed error!!!!\nevent.record="+event.record +"\nevent.record.isEditing()="+ event.record.isEditing() + "\n"+ new Date());
		}

		switch (result.getItemType()) {
		case GRADEBOOK:

			Dispatcher.forwardEvent(GradebookEvents.ItemUpdated.getEventType(), result);

			selectedGradebook.setGradebookGradeItem(result);

			if (isCategoryTypeUpdated || isGradeTypeUpdated || isReleaseGradesUpdated || isReleaseItemsUpdated) {
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
			} else if (isGradeScaleUpdated) {
				Dispatcher.forwardEvent(GradebookEvents.RefreshCourseGrades.getEventType(),
						selectedGradebook);
			} else {
				Dispatcher.forwardEvent(GradebookEvents.RefreshGradebookSetup.getEventType(),
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

	private ModelData findItemModel(ItemModel item, ItemModel result) {

		ModelData rv = null;
		if (result != null) {
			if (result.equals(item)) {
				return result;
			}
			if (!result.getChildren().isEmpty()) {
				List<ModelData> l = result.getChildren();   
				int i = l.indexOf(item);
				if(i >= 0) {
					rv = l.get(i);
				} else {
					for (ModelData d:l) {
						rv = findItemModel(item, (ItemModel)d);
						if (null != rv)
							break;
					}
				}
			}
		}
		return rv;
			
		
	}

	private void onUpdateItem(final ItemUpdate event) {
			
		Dispatcher.forwardEvent(GradebookEvents.MaskItemTree.getEventType());

		RestBuilder builder = RestBuilder.getInstance(Method.PUT, 
				GWT.getModuleBaseURL(),
				AppConstants.REST_FRAGMENT,
				AppConstants.ITEM_FRAGMENT);

		ItemModel model = (ItemModel)event.getModifiedItem();
		String jsonText = model == null ? null : model.getJSON();

		builder.sendRequest(200, 400, jsonText, new RestCallback() {

			public void onError(Request request, Throwable exception) {
				super.onError(request, exception);
				onUpdateItemFailure(event, exception);
				Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
				Dispatcher.forwardEvent(GradebookEvents.HideUserFeedback.getEventType());
				Dispatcher.forwardEvent(GradebookEvents.UnmaskMultiGradeGrid.getEventType());
			}

			public void onFailure(Request request, Throwable exception) {
				onUpdateItemFailure(event, exception);
				Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
				Dispatcher.forwardEvent(GradebookEvents.HideUserFeedback.getEventType());
				Dispatcher.forwardEvent(GradebookEvents.UnmaskMultiGradeGrid.getEventType());
			}

			public void onSuccess(Request request, Response response) {
				
				EntityOverlay overlay = JsonUtil.toOverlay(response.getText());
				ItemModel itemModel = new ItemModel(overlay);

				Dispatcher.forwardEvent(GradebookEvents.BeginItemUpdates.getEventType());
				onUpdateItemSuccess(event, itemModel);
				Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
				Dispatcher.forwardEvent(GradebookEvents.EndItemUpdates.getEventType(), selectedGradebook);
				Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
				Dispatcher.forwardEvent(GradebookEvents.HideUserFeedback.getEventType());
				if(!event.close) {
					Dispatcher.forwardEvent(GradebookEvents.StartEditItem.getEventType(),event.item);
				}
			}
		});
	}

	private void doCreateItem(ItemCreate itemCreate, ItemModel createdItem) {
		TreeStore<ItemModel> treeStore = (TreeStore<ItemModel>)itemCreate.store;
		ItemModel categoryItemModel = getCategoryItemModel(createdItem.getCategoryId());
		treeStore.add(categoryItemModel, createdItem, true);
		Dispatcher.forwardEvent(GradebookEvents.ItemCreated.getEventType(), createdItem);
		doUpdatePercentCourseGradeTotal(itemCreate.store, itemCreate.item, createdItem);
	}

	private void doUpdatePercentCourseGradeTotal(Store store, Item oldItem, ItemModel updatedItem) {
		switch (updatedItem.getItemType()) {
		case CATEGORY:
			ItemModel gradebookItemModel = getCategoryItemModel(updatedItem.getCategoryId());
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

	private void replaceProperty(String property, Record record, ItemModel item) {
		Object value = item.get(property);

		record.set(property, null);

		if (value != null)
			record.set(property, value);
	}

	private ItemModel getCategoryItemModel(Long categoryId) {

		Gradebook gradebookModel = Registry.get(AppConstants.CURRENT);
		return (ItemModel) gradebookModel.getCategoryItemModel(categoryId);
	}
}
