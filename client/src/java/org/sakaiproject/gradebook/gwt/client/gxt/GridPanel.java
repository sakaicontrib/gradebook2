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
package org.sakaiproject.gradebook.gwt.client.gxt;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.Gradebook2RPCServiceAsync;
import org.sakaiproject.gradebook.gwt.client.PersistentStore;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction.ClassType;
import org.sakaiproject.gradebook.gwt.client.gxt.custom.widget.grid.BaseCustomGridView;
import org.sakaiproject.gradebook.gwt.client.gxt.custom.widget.grid.CustomColumnModel;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.event.UserChangeEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.MultiGradeLoadConfig;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.GradebookPanel;
import org.sakaiproject.gradebook.gwt.client.model.ConfigurationModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.LearnerKey;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.HttpProxy;
import com.extjs.gxt.ui.client.data.JsonLoadResultReader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class GridPanel<M extends ModelData> extends GradebookPanel {

	protected static final int DEFAULT_PAGE_SIZE = 19;
	
	public enum RefreshAction { NONE, REFRESHDATA, REFRESHCOLUMNS, REFRESHLOCALCOLUMNS, REFRESHCOLUMNSANDDATA, REFRESHLOCALCOLUMNSANDDATA };
	
	public static final String FAILED_FLAG = ":F";
	
	protected EditorGrid<M> grid;
	protected BasePagingLoader<PagingLoadResult<M>> loader;
	protected ListStore<M> store;
	
	//protected String gradebookUid;
	protected String gridId;
	protected PagingToolBar pagingToolBar;
	protected EntityType entityType;
	
	protected NumberFormat defaultNumberFormat;
	
	protected CustomColumnModel cm;
	protected PagingLoadConfig loadConfig;
	
	protected ContentPanel gridOwner;
	
	protected RefreshAction refreshAction = RefreshAction.NONE;
	
	public GridPanel(String gridId, EntityType entityType) {
		this(gridId, entityType, null);
	}
	
	public GridPanel(String gridId, EntityType entityType, ContentPanel childPanel) {
		super();
		this.gridId = gridId;
		this.entityType = entityType;
		
		setHeaderVisible(false);
		setLayout( new FitLayout());
		setIconStyle("icon-table");
		setMonitorWindowResize(true);
	
		this.defaultNumberFormat = DataTypeConversionUtil.getDefaultNumberFormat();

		initListeners();

		pagingToolBar = newPagingToolBar(DEFAULT_PAGE_SIZE);	
		
		addComponents();

		store = new ListStore<M>();
		cm  = new CustomColumnModel("", gridId, new ArrayList<ColumnConfig>());
	
		grid = new GbEditorGrid<M>(store, cm);
		
		addGridListenersAndPlugins(grid);
		
		GridView view = newGridView();
		
		grid.setView(view);
		grid.setLoadMask(true);
		grid.setBorders(true);
	
		grid.addListener(Events.ValidateEdit, new Listener<GridEvent>() {

			public void handleEvent(final GridEvent ge) {
				// By setting ge.doit to false, we ensure that the AfterEdit event is not thrown. Which means we have to throw it ourselves onSuccess
				ge.stopEvent();
				
				GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
				
				if (selectedGradebook != null)
					editCell(selectedGradebook, ge.getRecord(), ge.getProperty(), ge.getValue(), ge.getStartValue(), ge);
			}

		});
		
		addGridListenersAndPlugins(grid);
		
		grid.setStripeRows(true);
		//grid.setDeferHeight(true);
		
		Menu gridContextMenu = newContextMenu();
		
		if (gridContextMenu != null)
			grid.setContextMenu(gridContextMenu);
		
		
		add(grid);
	}

	public void doRefresh(boolean useExistingColumnModel) {
		switch (refreshAction) {
		case REFRESHDATA:
			if (pagingToolBar != null)
				pagingToolBar.refresh();
			break;
		case REFRESHCOLUMNS:
		case REFRESHLOCALCOLUMNS:
		case REFRESHCOLUMNSANDDATA:
			refreshGrid(refreshAction, useExistingColumnModel);
			break;
		}
		refreshAction = RefreshAction.NONE;
	}
	
	
	protected FormPanel createForm() {
		return null;
	}
	
	/*
	 * This method will be run whenever the user switches gradebooks, and also on startup
	 */
	public void onSwitchGradebook(GradebookModel selectedGradebook) {
		String gradebookUid = selectedGradebook.getGradebookUid();
		if (store != null) {
			// Set the default sort field and direction on the store based on Cookies
			String storedSortField = PersistentStore.getPersistentField(gradebookUid, gridId, "sortField");
			String storedSortDirection = PersistentStore.getPersistentField(gradebookUid, gridId, "sortDir");
			
			SortDir sortDir = null;
			if (storedSortDirection != null) {
				if (storedSortDirection.equals("Descending"))
					sortDir = SortDir.DESC;
				else
					sortDir = SortDir.ASC;
			}
			
			if (storedSortField != null) 
				store.setDefaultSort(storedSortField, sortDir);
		}
		
		ConfigurationModel configModel = selectedGradebook.getConfigurationModel();
		
		int ps = configModel.getPageSize(gridId);
		
		if (ps != -1) {
			//setPageSize(ps);
		
			if (pagingToolBar != null)
				pagingToolBar.setPageSize(ps);
		}
		
		if (loader != null) 
			loader.load(0, ps);
	}
	
	public void editCell(GradebookModel selectedGradebook, Record record, String property, Object value, Object startValue, GridEvent ge) {
		/*UserEntityUpdateAction<M> action = newEntityUpdateAction(selectedGradebook, record, property, value, startValue, ge);
		
		if (validateEdit(property, value, startValue, record, ge)) {
			doEdit(record, action, ge);
		}*/
	}
	
	public CustomColumnModel getColumnModel() {
		return cm;
	}
	
	public ListStore<M> getStore() {
		return store;
	}
	
	public PagingToolBar getToolBar() {
		return pagingToolBar;
	}
	
	protected abstract CustomColumnModel newColumnModel(GradebookModel selectedGradebook);
		
	protected void addComponents()  {
		// Empty
	}
	
	protected void addGridListenersAndPlugins(EditorGrid<M> grid) {
		// Empty
	}
	
	protected void afterUpdateView(UserEntityAction<M> action, Record record, M model) {
		
		fireEvent(GradebookEvents.UserChange.getEventType(), new UserChangeEvent(action));
	}
	
	protected void beforeUpdateView(UserEntityAction<M> action, Record record, M model) {
		
	}
	
	protected void initListeners() {
		
	}
	
	protected Menu newContextMenu() {
		return null;
	}
	
	protected void reconfigureGrid(CustomColumnModel cm) {
		this.cm = cm;
		
		loader = newLoader();
		loader.setRemoteSort(true);
		
		store = newStore(loader);
		
		loadConfig = newLoadConfig(store, getPageSize());

		loader.useLoadConfig(loadConfig);

		pagingToolBar.bind(loader);

		grid.reconfigure(store, cm);
		
		if (grid.isRendered())
			grid.el().unmask();
	}
	
	protected GridView newGridView() {
		return new BaseCustomGridView();
	}
	
	protected PagingLoadConfig newLoadConfig(ListStore<M> store, int pageSize) {
		SortInfo sortInfo = store.getSortState();
		MultiGradeLoadConfig loadConfig = new MultiGradeLoadConfig();
		loadConfig.setLimit(0);
		loadConfig.setOffset(pageSize);	
		if (sortInfo != null)
			loadConfig.setSortInfo(sortInfo);
		
		return loadConfig;
	}
	
	protected BasePagingLoader<PagingLoadResult<M>> newLoader() {
		
		List<ColumnConfig> columns = cm.getColumns();
		ModelType type = new ModelType();  
		//type.setRoot("records");  
		
		for (ColumnConfig config : columns) {
			type.addField(config.getDataIndex(), config.getDataIndex());  
		}
		
		// use a http proxy to get the data  
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, GWT.getHostPageBaseURL() + "rest/learner.json");  
		
		HttpProxy<String> proxy = new HttpProxy<String>(builder);  

		// need a loader, proxy, and reader  
		JsonLoadResultReader<PagingLoadResult<M>> reader = new JsonLoadResultReader<PagingLoadResult<M>>(type);  

		return new BasePagingLoader<PagingLoadResult<M>>(proxy, reader);  
	}
	
	protected PagingToolBar newPagingToolBar(int pageSize) {
		PagingToolBar pagingToolBar = new PagingToolBar(pageSize);
		PagingToolBar.PagingToolBarMessages messages = pagingToolBar.getMessages();
		messages.setAfterPageText(i18n.pagingAfterPageText());
		messages.setBeforePageText(i18n.pagingPageText());
		messages.setDisplayMsg(i18n.pagingDisplayMsgText());
		
		return pagingToolBar;
	}
	
	protected ListStore<M> newStore(BasePagingLoader<PagingLoadResult<M>> loader) {
		ListStore<M> store = new ListStore<M>(loader);
		//store.setModelComparer(new EntityModelComparer<M>());
		store.setMonitorChanges(true);
		return store;
	}
	
	protected void refreshGrid(RefreshAction action, boolean useExistingColumnModel) {
		GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
		if (!useExistingColumnModel || cm == null)
			cm = newColumnModel(selectedGradebook);
		grid.reconfigure(store, cm);
		
		if (grid.isRendered())
			grid.el().unmask();
	}
	
	protected void queueDeferredRefresh(RefreshAction newRefreshAction) {
		switch (this.refreshAction) {
		// We don't want to 'demote' a refresh columns action to a refresh data action
		case NONE:
			this.refreshAction = newRefreshAction;
			break;
		case REFRESHDATA:
			switch (newRefreshAction) {
			case REFRESHDATA:
				this.refreshAction = newRefreshAction;
				break;
			case REFRESHLOCALCOLUMNS:
			case REFRESHCOLUMNS:
				this.refreshAction = RefreshAction.REFRESHCOLUMNSANDDATA;
				break;
			}
			break;
		case REFRESHLOCALCOLUMNS:
			switch (newRefreshAction) {
			case REFRESHDATA:
				this.refreshAction = RefreshAction.REFRESHCOLUMNSANDDATA;
				break;
			case REFRESHLOCALCOLUMNS:
			case REFRESHCOLUMNS:
				this.refreshAction = RefreshAction.REFRESHCOLUMNS;
				break;
			}
			break;
		case REFRESHCOLUMNS:
			switch (newRefreshAction) {
			case REFRESHDATA:
				this.refreshAction = RefreshAction.REFRESHCOLUMNSANDDATA;
				break;
			case REFRESHLOCALCOLUMNS:
			case REFRESHCOLUMNS:
				this.refreshAction = RefreshAction.REFRESHCOLUMNS;
				break;
			}
			break;
		}
	}
	
	protected void updateView(UserEntityAction<M> action, Record record, M model) {
		String property = action.getKey();
		if (model.get(property) == null)
			record.set(property, null);
		else
			record.set(property, model.get(property));
	}

	protected UserEntityUpdateAction<M> newEntityUpdateAction(GradebookModel selectedGradebook, final Record record, final String property, 
			final Object value, final Object startValue, final GridEvent gridEvent) {
		ColumnConfig config = null;
		
		if (gridEvent != null) {
			String className = grid.getView().getCell(gridEvent.getRowIndex(), gridEvent.getColIndex()).getClassName();
			String gbDroppedText = new StringBuilder(" ").append(resources.css().gbCellDropped()).toString();
			className = className.replace(gbDroppedText, "");
			grid.getView().getCell(gridEvent.getRowIndex(), gridEvent.getColIndex()).setClassName(className);
			grid.getView().getCell(gridEvent.getRowIndex(), gridEvent.getColIndex()).setInnerText("Saving...");
		
			config = cm.getColumn(gridEvent.getColIndex());
		}
		
		ClassType classType = ClassType.BOOLEAN;
		
		if (config != null && config.getEditor() != null) {
			
			if (config.getEditor().getField() instanceof NumberField) {
				Class type = ((NumberField)config.getEditor().getField()).getPropertyEditorType();
				
				classType = ClassType.DOUBLE;
				
				if (type != null && type.equals(Integer.class))
					classType = ClassType.INTEGER;
			
			} else if (config.getEditor().getField() instanceof TextField)
				classType = ClassType.STRING;
			
		}
		M model = (M)record.getModel();
		
		UserEntityUpdateAction<M> action = new UserEntityUpdateAction<M>(selectedGradebook, model, property, classType, value, startValue);
		
		if (config != null) {
			String entityName = new StringBuilder().append(config.getHeader())
				.append(" : ").append((String)model.get(LearnerKey.DISPLAY_NAME.name())).toString();
			action.setEntityName(entityName);
		}
		
		return action;
	}
	
	protected void doEdit(final Record record, final UserEntityUpdateAction<M> action, final GridEvent gridEvent) {
		/*Gradebook2RPCServiceAsync service = Registry.get(AppConstants.SERVICE);
		
		AsyncCallback<M> callback = new AsyncCallback<M>() {

			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(caught));
				
				String property = action.getKey();

				// Save the exception message on the record
				String failedProperty = property + FAILED_FLAG;
				record.set(failedProperty, caught.getMessage());
						
				// We have to fool the system into thinking that the value has changed, since
				// we snuck in that "Saving grade..." under the radar.
				record.set(property, null);
				record.set(property, action.getStartValue());
						
				if (gridEvent != null)
					grid.fireEvent(Events.AfterEdit, gridEvent);
			}

			public void onSuccess(M result) {
				// Ensure that we clear out any older failure messages
				// Save the exception message on the record
				String failedProperty = action.getKey() + FAILED_FLAG;
				record.set(failedProperty, null);
						
				beforeUpdateView(action, record, result);
				
				updateView(action, record, result);
						
				afterUpdateView(action, record, result);
				
				if (gridEvent != null) {
					grid.fireEvent(Events.AfterEdit, gridEvent);
				}
			}
			
		};*/
		
		// FIXME: Need to switch this to REST-based PUT
		//service.update(action.getModel(), action.getEntityType(), null, SecureToken.get(), callback);
		
	}
	
	protected boolean validateEdit(String property, Object value, Object startValue, Record record, GridEvent gridEvent) {
		return true;
	}

	public EditorGrid<M> getGrid() {
		return grid;
	}

	public BasePagingLoader<PagingLoadResult<M>> getLoader() {
		return loader;
	}

	public PagingToolBar getPagingToolBar() {
		return pagingToolBar;
	}

	public int getPageSize() {
		int pageSize = DEFAULT_PAGE_SIZE;
		
		if (pagingToolBar != null) 
			pageSize = pagingToolBar.getPageSize();
		
		return pageSize;
	}
}
