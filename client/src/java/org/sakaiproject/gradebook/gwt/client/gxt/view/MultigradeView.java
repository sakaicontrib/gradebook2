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
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.RestBuilder;
import org.sakaiproject.gradebook.gwt.client.RestBuilder.Method;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
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
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.ModelData;
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
		
	}

	public ListStore<ModelData> getStore() {
		return multigradeStore;
	}

	public void deselectAll() {
		getMultiGradeContentPanel().deselectAll();
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
				onEndItemUpdates((Gradebook)event.getData());
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
				onRefreshCourseGrades((Gradebook)event.getData());
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
				getMultiGradeContentPanel();
				Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
				onSwitchGradebook(selectedGradebook);
				break;
			case SWITCH_GRADEBOOK:
				onSwitchGradebook((Gradebook)event.getData());
				break;
			case USER_CHANGE:
				onUserChange((UserEntityAction<?>)event.getData());
				break;
			case MASK_MULTI_GRADE_GRID:
				maskMultiGradeGrid();
				break;
			case UNMASK_MULTI_GRADE_GRID:
				unmaskMultiGradeGrid();
				break;
		}
	}

	protected void initUI(ApplicationSetup model) {
		Gradebook gbModel = model.getGradebookModels().get(0);
		buildLoaderAndStore(gbModel);
	}
	
	private void buildLoaderAndStore(Gradebook gbModel) {
		//ModelType type = LearnerTranslater.generateLearnerModelType();
		
		multigradeLoader = RestBuilder.getLearnerLoader(Method.GET, 
				GWT.getModuleBaseURL(), AppConstants.REST_FRAGMENT, AppConstants.ROSTER_FRAGMENT);

		multigradeStore = new ListStore<ModelData>(multigradeLoader);
		multigradeStore.setModelComparer(new EntityModelComparer<ModelData>(LearnerKey.S_UID.name()));
		multigradeStore.setMonitorChanges(true);
		multigradeStore.setDefaultSort(LearnerKey.S_LST_NM_FRST.name(), SortDir.ASC);

		multigradeStore.addListener(Store.Sort, storeListener);
	}

	protected void onBeginItemUpdates() {
		getMultiGradeContentPanel().onBeginItemUpdates();
	}

	protected void onBrowseLearner(BrowseLearner event) {
		getMultiGradeContentPanel().onBrowseLearner(event);
	}

	protected void onEndItemUpdates(Gradebook gradebookModel) {
		getMultiGradeContentPanel().onEndItemUpdates(gradebookModel.getConfigurationModel(),
				gradebookModel.getColumns(), (ItemModel)gradebookModel.getGradebookItemModel());
	}

	protected void onItemCreated(ItemModel itemModel) {
		getMultiGradeContentPanel().onItemCreated(itemModel);
	}

	protected void onItemDeleted(Item itemModel) {
		getMultiGradeContentPanel().onItemDeleted(itemModel);
	}

	protected void onItemUpdated(Item itemModel) {	
		getMultiGradeContentPanel().onItemUpdated(itemModel);
	}

	protected void onLearnerGradeRecordUpdated(UserEntityAction<?> action) {
		getMultiGradeContentPanel().onLearnerGradeRecordUpdated(action);
	}

	protected void onRefreshCourseGrades(Gradebook gradebookModel) {
		getMultiGradeContentPanel().onRefreshCourseGrades(gradebookModel.getConfigurationModel(),
				gradebookModel.getColumns(), (ItemModel)gradebookModel.getGradebookItemModel());
	}

	protected void onRefreshGradebookItems(Gradebook gradebookModel) {
		//buildLoaderAndStore(gradebookModel);
		getMultiGradeContentPanel().onRefreshGradebookItems(gradebookModel, null);
	}

	protected void onRefreshGradebookSetup(Gradebook gradebookModel) {
		getMultiGradeContentPanel().onRefreshGradebookSetup(gradebookModel);
	}

	protected void onShowColumns(ShowColumnsEvent event) {
		getMultiGradeContentPanel().onShowColumns(event);
	}

	protected void onSwitchGradebook(Gradebook selectedGradebook) {
		getMultiGradeContentPanel().onSwitchGradebook(selectedGradebook);
	}

	protected void onUserChange(UserEntityAction<?> action) {
		getMultiGradeContentPanel().onUserChange(action);
	}

	public MultiGradeContentPanel getMultiGradeContentPanel() {
		if (multigrade == null) {
			this.multigrade = new MultiGradeContentPanel(multigradeStore, false) {

				protected PagingLoader<PagingLoadResult<ModelData>> newLoader() {
					return multigradeLoader;
				}

				protected ListStore<ModelData> newStore() {
					return multigradeStore;
				}
			};

			Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
			multigrade.addGrid(selectedGradebook.getConfigurationModel(),
					selectedGradebook.getColumns(), (ItemModel)selectedGradebook.getGradebookItemModel());
			// GRBK-483 hide and disable the toggle button when not usable
			if (selectedGradebook.getGradebookItemModel().getCategoryType() == CategoryType.WEIGHTED_CATEGORIES &&
			selectedGradebook.getGradebookItemModel().getGradeType() == GradeType.POINTS)
				multigrade.enableShowWeightedButton();
			else 
				multigrade.disableShowWeightedButton();
		}
		return multigrade;
	}
	
	protected void maskMultiGradeGrid() {
		
		multigrade.maskMultiGradeGrid();
	}
	
	protected void unmaskMultiGradeGrid() {
		
		multigrade.unmaskMultiGradeGrid();
	}
	
}
