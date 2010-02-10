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
import org.sakaiproject.gradebook.gwt.client.gxt.LearnerTranslater;
import org.sakaiproject.gradebook.gwt.client.gxt.event.BrowseLearner;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ShowColumnsEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ConfigurationModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.MultiGradeContentPanel;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationSetup;
import org.sakaiproject.gradebook.gwt.client.model.Configuration;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.SortDir;
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
						Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
						Configuration configModel = new ConfigurationModel(selectedGradebook.getGradebookId());
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
				onItemDeleted((Item)event.getData());
				break;
			case ITEM_UPDATED:
				onItemUpdated((Item)event.getData());
				break;
			case REFRESH_COURSE_GRADES:
				onRefreshCourseGrades();
				break;
			case REFRESH_GRADEBOOK_ITEMS:
				onRefreshGradebookItems((Gradebook)event.getData());
				break;
			case REFRESH_GRADEBOOK_SETUP:
				onRefreshGradebookSetup((Gradebook)event.getData());
				break;
			case SHOW_COLUMNS:
				onShowColumns((ShowColumnsEvent)event.getData());
				break;
			case STARTUP:
				ApplicationSetup applicationModel = (ApplicationSetup)event.getData();
				initUI(applicationModel);
				Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
				onSwitchGradebook(selectedGradebook);
				break;
			case SWITCH_GRADEBOOK:
				onSwitchGradebook((Gradebook)event.getData());
				break;
			case USER_CHANGE:
				onUserChange((UserEntityAction<?>)event.getData());
				break;
		}
	}

	protected void initUI(ApplicationSetup model) {

		Gradebook gbModel = model.getGradebookModels().get(0);
		
		ModelType type = LearnerTranslater.generateLearnerModelType(gbModel.getGradebookItemModel());
		
		multigradeLoader = RestBuilder.getPagingDelayLoader(type, Method.GET, 
				GWT.getModuleBaseURL(), AppConstants.REST_FRAGMENT, AppConstants.ROSTER_FRAGMENT);

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

	protected void onItemDeleted(Item itemModel) {
		multigrade.onItemDeleted(itemModel);
	}

	protected void onItemUpdated(Item itemModel) {	
		multigrade.onItemUpdated(itemModel);
	}

	protected void onLearnerGradeRecordUpdated(UserEntityAction<?> action) {
		multigrade.onLearnerGradeRecordUpdated(action);
	}

	protected void onRefreshCourseGrades() {
		multigrade.onRefreshCourseGrades();
	}

	protected void onRefreshGradebookItems(Gradebook gradebookModel) {
		multigrade.onRefreshGradebookItems(gradebookModel);
	}

	protected void onRefreshGradebookSetup(Gradebook gradebookModel) {
		multigrade.onRefreshGradebookSetup(gradebookModel);
	}

	protected void onShowColumns(ShowColumnsEvent event) {
		multigrade.onShowColumns(event);
	}

	protected void onSwitchGradebook(Gradebook selectedGradebook) {
		multigrade.onSwitchGradebook(selectedGradebook);
	}

	protected void onUserChange(UserEntityAction<?> action) {
		multigrade.onUserChange(action);
	}

	public MultiGradeContentPanel getMultiGradeContentPanel() {
		return multigrade;
	}

}
