package org.sakaiproject.gradebook.gwt.client.gxt.view;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.GradebookToolFacadeAsync;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.action.PageRequestAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.gxt.NotifyingAsyncCallback;
import org.sakaiproject.gradebook.gwt.client.gxt.event.BrowseLearner;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ShowColumnsEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.MultiGradeContentPanel;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationModel;
import org.sakaiproject.gradebook.gwt.client.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.DataReader;
import com.extjs.gxt.ui.client.data.ModelReader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.ListStore;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MultigradeView extends View {

	private MultiGradeContentPanel multigrade;
	
	private BasePagingLoader<PagingLoadConfig, PagingLoadResult<StudentModel>> multigradeLoader;
	private ListStore<StudentModel> multigradeStore;
	
	
	public MultigradeView(Controller controller, I18nConstants i18n) {
		super(controller);
		this.multigrade = new MultiGradeContentPanel(null) {
			
			protected BasePagingLoader<PagingLoadConfig, PagingLoadResult<StudentModel>> newLoader() {
				return multigradeLoader;
			}
			
			protected ListStore<StudentModel> newStore(BasePagingLoader<PagingLoadConfig, PagingLoadResult<StudentModel>> loader) {
				return multigradeStore;
			}
		};
		this.multigrade.setHeight(550);
	}
	
	public ListStore<StudentModel> getStore() {
		return multigrade.getStore();
	}

	public void deselectAll() {
		multigrade.deselectAll();
	}
	
	@Override
	protected void handleEvent(AppEvent<?> event) {
		switch(event.type) {
		case GradebookEvents.BrowseLearner:
			onBrowseLearner((BrowseLearner)event.data);
			break;
		case GradebookEvents.LearnerGradeRecordUpdated:
			onLearnerGradeRecordUpdated((UserEntityAction<?>)event.data);
			break;
		case GradebookEvents.ItemCreated:
			onItemCreated((ItemModel)event.data);
			break;
		case GradebookEvents.ItemDeleted:
			onItemDeleted((ItemModel)event.data);
			break;
		case GradebookEvents.ItemUpdated:
			onItemUpdated((ItemModel)event.data);
			break;
		case GradebookEvents.LoadItemTreeModel:
			onLoadItemTreeModel((GradebookModel)event.data);
			break;
		case GradebookEvents.RefreshCourseGrades:
			onRefreshCourseGrades();
			break;
		case GradebookEvents.ShowColumns:
			onShowColumns((ShowColumnsEvent)event.data);
			break;
		case GradebookEvents.Startup:
			ApplicationModel applicationModel = (ApplicationModel)event.data;
			initUI(applicationModel);
			GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
			onSwitchGradebook(selectedGradebook);
			break;
		case GradebookEvents.SwitchGradebook:
			onSwitchGradebook((GradebookModel)event.data);
			break;
		case GradebookEvents.UserChange:
			onUserChange((UserEntityAction<?>)event.data);
			break;
		}
	}

	protected void initUI(ApplicationModel model) {
		
		RpcProxy<PagingLoadConfig, PagingLoadResult<StudentModel>> proxy = 
			new RpcProxy<PagingLoadConfig, PagingLoadResult<StudentModel>>() {
			@Override
			protected void load(PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<StudentModel>> callback) {
				GradebookToolFacadeAsync service = Registry.get("service");
				GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
				PageRequestAction pageAction = new PageRequestAction(EntityType.STUDENT, selectedGradebook.getGradebookUid(), selectedGradebook.getGradebookId());
				service.getEntityPage(pageAction, loadConfig, callback);
			}
			
			@Override
			public void load(final DataReader<PagingLoadConfig, PagingLoadResult<StudentModel>> reader, 
					final PagingLoadConfig loadConfig, final AsyncCallback<PagingLoadResult<StudentModel>> callback) {
				load(loadConfig, new NotifyingAsyncCallback<PagingLoadResult<StudentModel>>() {

					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						callback.onFailure(caught);
					}

					public void onSuccess(PagingLoadResult<StudentModel> result) {
						try {
							PagingLoadResult<StudentModel> data = null;
							if (reader != null) {
								data = reader.read(loadConfig, result);
							} else {
								data = result;
							}
							callback.onSuccess(data);
						} catch (Exception e) {
							callback.onFailure(e);
						}
					}

				});
			}
		};
		
		multigradeLoader = new BasePagingLoader<PagingLoadConfig, PagingLoadResult<StudentModel>>(proxy, new ModelReader<PagingLoadConfig>());
		
		multigradeStore = new ListStore<StudentModel>(multigradeLoader);
		multigradeStore.setModelComparer(new EntityModelComparer<StudentModel>());
		multigradeStore.setMonitorChanges(true);
	}
	
	protected void onBrowseLearner(BrowseLearner event) {
		multigrade.onBrowseLearner(event);
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
	
	protected void onLoadItemTreeModel(GradebookModel selectedGradebook) {
		multigrade.onLoadItemTreeModel(selectedGradebook);
	}
	
	protected void onRefreshCourseGrades() {
		multigrade.onRefreshCourseGrades();
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
