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

import java.util.EnumSet;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.RestBuilder;
import org.sakaiproject.gradebook.gwt.client.RestBuilder.Method;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.gxt.ItemModelProcessor;
import org.sakaiproject.gradebook.gwt.client.gxt.event.BrowseLearner;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ShowColumnsEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.MultiGradeContentPanel;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationModel;
import org.sakaiproject.gradebook.gwt.client.model.ConfigurationModel;
import org.sakaiproject.gradebook.gwt.client.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.LearnerKey;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.util.DelayedTask;
import com.google.gwt.core.client.GWT;

public class MultigradeView extends View {

	private MultiGradeContentPanel multigrade;

	private PagingLoader<PagingLoadResult<ModelData>> multigradeLoader;
	private ListStore<ModelData> multigradeStore;
	private Listener<StoreEvent> storeListener;

	private DelayedTask syncTask;

	public MultigradeView(Controller controller, I18nConstants i18n) {
		super(controller);
		storeListener = new Listener<StoreEvent>() {

			public void handleEvent(StoreEvent se) {
				final String sortField = ((ListStore)se.getStore()).getSortField();
				final SortDir sortDir = ((ListStore)se.getStore()).getSortDir();
				final boolean isAscending = sortDir == SortDir.ASC;

				if (syncTask != null)
					syncTask.cancel();
				
				syncTask = new DelayedTask(new Listener<BaseEvent>() {
					
					public void handleEvent(BaseEvent be) {
						GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
						ConfigurationModel configModel = new ConfigurationModel(selectedGradebook.getGradebookId());
						configModel.setSortField(AppConstants.MULTIGRADE, sortField);
						configModel.setSortDirection(AppConstants.MULTIGRADE, Boolean.valueOf(isAscending));
						
						Dispatcher.forwardEvent(GradebookEvents.Configuration.getEventType(), configModel);
					}
					
				});
			}

		};
		this.multigrade = new MultiGradeContentPanel(null) {

			protected PagingLoader<PagingLoadResult<ModelData>> newLoader() {
				return multigradeLoader;
			}

			protected ListStore<ModelData> newStore(PagingLoader<PagingLoadResult<ModelData>> loader) {
				return multigradeStore;
			}
		};
	}

	public ListStore<ModelData> getStore() {
		return multigrade.getStore();
	}

	public void deselectAll() {
		multigrade.deselectAll();
	}

	@Override
	protected void handleEvent(AppEvent event) {
		switch(GradebookEvents.getEvent(event.getType()).getEventKey()) {
			case BEGIN_ITEM_UPDATES:
				onBeginItemUpdates();
				break;
			case BROWSE_LEARNER:
				onBrowseLearner((BrowseLearner)event.getData());
				break;
			case END_ITEM_UPDATES:
				onEndItemUpdates();
				break;
			case LEARNER_GRADE_RECORD_UPDATED:
				onLearnerGradeRecordUpdated((UserEntityAction<?>)event.getData());
				break;
			case ITEM_CREATED:
				onItemCreated((ItemModel)event.getData());
				break;
			case ITEM_DELETED:
				onItemDeleted((ItemModel)event.getData());
				break;
			case ITEM_UPDATED:
				onItemUpdated((ItemModel)event.getData());
				break;
			case REFRESH_COURSE_GRADES:
				onRefreshCourseGrades();
				break;
			case REFRESH_GRADEBOOK_ITEMS:
				onRefreshGradebookItems((GradebookModel)event.getData());
				break;
			case REFRESH_GRADEBOOK_SETUP:
				onRefreshGradebookSetup((GradebookModel)event.getData());
				break;
			case SHOW_COLUMNS:
				onShowColumns((ShowColumnsEvent)event.getData());
				break;
			case STARTUP:
				ApplicationModel applicationModel = (ApplicationModel)event.getData();
				initUI(applicationModel);
				GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
				onSwitchGradebook(selectedGradebook);
				break;
			case SWITCH_GRADEBOOK:
				onSwitchGradebook((GradebookModel)event.getData());
				break;
			case USER_CHANGE:
				onUserChange((UserEntityAction<?>)event.getData());
				break;
		}
	}

	protected void initUI(ApplicationModel model) {

		GradebookModel gbModel = model.getGradebookModels().get(0);
		
		final ModelType type = new ModelType();  
		type.setRoot(AppConstants.LIST_ROOT);
		type.setTotalName(AppConstants.TOTAL);
		
		for (LearnerKey key : EnumSet.allOf(LearnerKey.class)) {
			type.addField(key.name(), key.name()); 
		}
		
		ItemModelProcessor processor = new ItemModelProcessor(gbModel.getGradebookItemModel()) {
			public void doItem(ItemModel itemModel) {
				String id = itemModel.getIdentifier();
				type.addField(id, id);
				String droppedKey = DataTypeConversionUtil.buildDroppedKey(id);
				type.addField(droppedKey, droppedKey);
				
				String commentedKey = DataTypeConversionUtil.buildCommentKey(id);
				type.addField(commentedKey, commentedKey);
				
				String commentTextKey = DataTypeConversionUtil.buildCommentTextKey(id);
				type.addField(commentTextKey, commentTextKey);
				
				String excusedKey = DataTypeConversionUtil.buildExcusedKey(id);
				type.addField(excusedKey, excusedKey);
			}
		};
		
		processor.process();
		
		/*
		RestBuilder builder = RestBuilder.getInstance(Method.GET, GWT.getModuleBaseURL(),
				AppConstants.REST_FRAGMENT, AppConstants.ROSTER_FRAGMENT,
				gbModel.getGradebookUid(), String.valueOf(gbModel.getGradebookId()));
		
		HttpProxy<String> proxy = new HttpProxy<String>(builder);  

		// need a loader, proxy, and reader  
		JsonPagingLoadResultReader<PagingLoadResult<ModelData>> reader = new JsonPagingLoadResultReader<PagingLoadResult<ModelData>>(type) {
			protected ModelData newModelInstance() {
			    return new BaseModel();
			}
		};  */

		multigradeLoader = RestBuilder.getPagingDelayLoader(type, Method.GET, 
				GWT.getModuleBaseURL(), AppConstants.REST_FRAGMENT, AppConstants.ROSTER_FRAGMENT);
			
			//new BasePagingLoader<PagingLoadResult<ModelData>>(proxy, reader);  
		
		multigradeStore = new ListStore<ModelData>(multigradeLoader);
		multigradeStore.setModelComparer(new EntityModelComparer<ModelData>(LearnerKey.UID.name()));
		multigradeStore.setMonitorChanges(true);
		multigradeStore.setDefaultSort(LearnerKey.LAST_NAME_FIRST.name(), SortDir.ASC);

		multigradeStore.addListener(Store.Sort, storeListener);
	}

	protected void onBeginItemUpdates() {
		multigrade.onBeginItemUpdates();
	}

	protected void onBrowseLearner(BrowseLearner event) {
		multigrade.onBrowseLearner(event);
	}

	protected void onEndItemUpdates() {
		multigrade.onEndItemUpdates();
	}

	protected void onItemCreated(ItemModel itemModel) {
		multigrade.onItemCreated(itemModel);
	}

	protected void onItemDeleted(ItemModel itemModel) {
		multigrade.onItemDeleted(itemModel);
	}

	protected void onItemUpdated(ItemModel itemModel) {	
		multigrade.onItemUpdated(itemModel);
	}

	protected void onLearnerGradeRecordUpdated(UserEntityAction<?> action) {
		multigrade.onLearnerGradeRecordUpdated(action);
	}

	protected void onRefreshCourseGrades() {
		multigrade.onRefreshCourseGrades();
	}

	protected void onRefreshGradebookItems(GradebookModel gradebookModel) {
		multigrade.onRefreshGradebookItems(gradebookModel);
	}

	protected void onRefreshGradebookSetup(GradebookModel gradebookModel) {
		multigrade.onRefreshGradebookSetup(gradebookModel);
	}

	protected void onShowColumns(ShowColumnsEvent event) {
		multigrade.onShowColumns(event);
	}

	protected void onSwitchGradebook(GradebookModel selectedGradebook) {
		multigrade.onSwitchGradebook(selectedGradebook);
	}

	protected void onUserChange(UserEntityAction<?> action) {
		multigrade.onUserChange(action);
	}

	public MultiGradeContentPanel getMultiGradeContentPanel() {
		return multigrade;
	}

}
