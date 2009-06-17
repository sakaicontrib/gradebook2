package org.sakaiproject.gradebook.gwt.client.gxt.view.panel;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.Gradebook2RPCServiceAsync;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.SecureToken;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaButton;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.PermissionDeleteCellRenderer;
import org.sakaiproject.gradebook.gwt.client.model.CategoryModel;
import org.sakaiproject.gradebook.gwt.client.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.PermissionEntryListModel;
import org.sakaiproject.gradebook.gwt.client.model.PermissionEntryModel;
import org.sakaiproject.gradebook.gwt.client.model.SectionModel;
import org.sakaiproject.gradebook.gwt.client.model.UserModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ListLoadConfig;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.ModelReader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CellSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;


public class GraderPermissionSettingsPanel extends ContentPanel {
	
	private final static int DELETE_ACTION_GRID_CELL = 4;

	private VerticalPanel mainVerticalPanel = null;
	private HorizontalPanel inputHorizontalPanel = null;
	private HorizontalPanel userSelectionHorizontalPanel = null;
	private HorizontalPanel createPermissionHorizontalPanel = null;
	private Grid<PermissionEntryModel> grid = null;
	private ListStore<PermissionEntryModel> permissionEntryListStore = null;
	
	private ComboBox<UserModel> userComboBox = null;
	private ComboBox<Permission> permissionComboBox = null;
	private ComboBox<CategoryModel> categoryComboBox = null;
	private ComboBox<SectionModel> sectionComboBox = null;
	
	public GraderPermissionSettingsPanel(I18nConstants i18n, boolean isEditable) {
		super();

		mainVerticalPanel = new VerticalPanel();
		mainVerticalPanel.setSpacing(5);
		mainVerticalPanel.setSize("100%", "100%");

		inputHorizontalPanel = new HorizontalPanel();
		
		userSelectionHorizontalPanel = new HorizontalPanel();
		userSelectionHorizontalPanel.setSpacing(5);
		
		createPermissionHorizontalPanel = new HorizontalPanel();
		createPermissionHorizontalPanel.setSpacing(5);
		
		permissionEntryListStore = new ListStore<PermissionEntryModel>();
		
		
		// LOADING DATA
		
		// USERS
		RpcProxy<ListLoadConfig, ListLoadResult<UserModel>> userProxy = new RpcProxy<ListLoadConfig, ListLoadResult<UserModel>>() {
			
			@Override
			protected void load(ListLoadConfig listLoadConfig, AsyncCallback<ListLoadResult<UserModel>> callback) {
				GradebookModel gbModel = Registry.get(AppConstants.CURRENT);
				Gradebook2RPCServiceAsync service = Registry.get("service");
				service.getPage(gbModel.getGradebookUid(), gbModel.getGradebookId(), EntityType.USER, null, SecureToken.get(), callback);
			}
			
		};
		ListLoader userLoader = new BaseListLoader(userProxy);  
		userLoader.load();
		ListStore<UserModel> userListStore = new ListStore<UserModel>(userLoader);
		userListStore.setModelComparer(new EntityModelComparer<UserModel>());
		
		
		// PERMISSIONS
		List<Permission> permissionList = new ArrayList<Permission>();
		permissionList.add(new Permission("grade"));
		permissionList.add(new Permission("view"));
		ListStore<Permission> permissionListStore = new ListStore<Permission>();
		permissionListStore.add(permissionList);
		
		
		// CATEGORIES
		RpcProxy<ListLoadConfig, ListLoadResult<CategoryModel>> categoryProxy = new RpcProxy<ListLoadConfig, ListLoadResult<CategoryModel>>() {

			@Override
			protected void load(ListLoadConfig listLoadConfig, AsyncCallback<ListLoadResult<CategoryModel>> callback) {
				GradebookModel gbModel = Registry.get(AppConstants.CURRENT);
				Gradebook2RPCServiceAsync service = Registry.get("service");
				service.getPage(gbModel.getGradebookUid(), gbModel.getGradebookId(), EntityType.CATEGORY_NOT_REMOVED, null, SecureToken.get(), callback);
			}
		};
		ListLoader categoryLoader = new BaseListLoader(categoryProxy);
		categoryLoader.load();
		ListStore<CategoryModel> categoryListStore = new ListStore<CategoryModel>(categoryLoader);
		categoryListStore.setModelComparer(new EntityModelComparer<CategoryModel>());
		
		
		// SECTIONS
		RpcProxy<PagingLoadConfig, PagingLoadResult<SectionModel>> sectionsProxy = new RpcProxy<PagingLoadConfig, PagingLoadResult<SectionModel>>() {
			@Override
			protected void load(PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<SectionModel>> callback) {
				Gradebook2RPCServiceAsync service = Registry.get("service");
				GradebookModel model = Registry.get(AppConstants.CURRENT);
				service.getPage(model.getGradebookUid(), model.getGradebookId(), EntityType.PERMISSION_SECTIONS, loadConfig, SecureToken.get(), callback);
			}
		};

		BasePagingLoader<PagingLoadConfig, PagingLoadResult<SectionModel>> sectionsLoader = 
			new BasePagingLoader<PagingLoadConfig, PagingLoadResult<SectionModel>>(sectionsProxy, new ModelReader<PagingLoadConfig>());
		sectionsLoader.setRemoteSort(true);
		sectionsLoader.load(0, 50);
		ListStore<SectionModel> sectionStore = new ListStore<SectionModel>(sectionsLoader);
		sectionStore.setModelComparer(new EntityModelComparer<SectionModel>());
		
		
		// Combo Boxes
		
		// Users
		userComboBox = new ComboBox<UserModel>();
		userComboBox.setEmptyText("Users");
		userComboBox.setDisplayField(UserModel.Key.USER_DISPLAY_NAME.name());
		userComboBox.setWidth(150); 
		userComboBox.setStore(userListStore);
		userComboBox.setTypeAhead(true);
		userComboBox.setTriggerAction(TriggerAction.ALL);
		userComboBox.setEditable(false);
		userComboBox.addSelectionChangedListener(new SelectionChangedListener<UserModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<UserModel> se) {
				
				// Reset UI components
				permissionComboBox.reset();
				sectionComboBox.reset();
				categoryComboBox.reset();
				permissionEntryListStore.removeAll();
				
				// Get selected user model
				//UserModel userModel = se.getSelectedItem();
				
				loadGrid();
				createPermissionHorizontalPanel.show();	
			}
		});
		
		// Permissions
		permissionComboBox = new ComboBox<Permission>();
		permissionComboBox.setEmptyText("Permissions");
		permissionComboBox.setDisplayField("name");
		permissionComboBox.setWidth(100); 
		permissionComboBox.setStore(permissionListStore);
		permissionComboBox.setTypeAhead(true);
		permissionComboBox.setTriggerAction(TriggerAction.ALL);
		permissionComboBox.setEditable(false);

		// Categories
		categoryComboBox = new ComboBox<CategoryModel>();
		categoryComboBox.setEmptyText("Categories");
		categoryComboBox.setDisplayField(CategoryModel.Key.CATEGORY_DISPLAY_NAME.name());
		categoryComboBox.setWidth(150); 
		categoryComboBox.setStore(categoryListStore);
		categoryComboBox.setTypeAhead(true);
		categoryComboBox.setTriggerAction(TriggerAction.ALL);
		categoryComboBox.setEditable(false);


		// Sections
		sectionComboBox = new ComboBox<SectionModel>();
		sectionComboBox.setEmptyText("Sections");
		sectionComboBox.setDisplayField(SectionModel.Key.SECTION_NAME.name());
		sectionComboBox.setWidth(150); 
		sectionComboBox.setStore(sectionStore);
		sectionComboBox.setTypeAhead(true);
		sectionComboBox.setTriggerAction(TriggerAction.ALL);
		sectionComboBox.setEditable(false);
		
		
		// Add Button
		Button addButton = new Button("Add");
		addButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				PermissionEntryModel permissionEntryModel = new PermissionEntryModel();
				
				// Enforce selection
				List<UserModel> users = userComboBox.getSelection();
				if(null != users && 1 == users.size()) {
					permissionEntryModel.setUserDisplayName(users.get(0).getUserDisplayName());
					permissionEntryModel.setUserId(users.get(0).getUserId());
				}
				else {
					MessageBox.alert("Warn", "Please select a user", null);
					return;
				}
				
				// Enforce selection
				List<Permission> permissions = permissionComboBox.getSelection();
				if(null != permissions && 1 == permissions.size()) {
					permissionEntryModel.setPermissionId(permissions.get(0).getName());
				}
				else {
					MessageBox.alert("Warn", "Please select a permission", null);
					return;
				}
				
				// If no category and or section is selected, we assume that all categories and or all sections are selected
				
				List<CategoryModel> categories = categoryComboBox.getSelection();
				if(null != categories && 1 == categories.size()) {
					CategoryModel categoryModel = categories.get(0);
					permissionEntryModel.setCategoryId(categoryModel.getCategoryId());
					permissionEntryModel.setCategoryDisplayName((null == categoryModel.getCategoryId()) ? "All" : categoryModel.getCategoryDisplayName());
				}
				else {
					// Per old gradebook, it seems that if all categories are selected, the ID is set to null
					permissionEntryModel.setCategoryId(null);
					permissionEntryModel.setCategoryDisplayName("All");
				}
				
				List<SectionModel> sections = sectionComboBox.getSelection();
				if(null != sections && 1 == sections.size()) {
					SectionModel sectionModel = sections.get(0);
					permissionEntryModel.setSectionId(sectionModel.getSectionId());
					permissionEntryModel.setSectionDisplayName((null == sectionModel.getSectionId()) ? "All" : sectionModel.getSectionName());
					
				}
				else {
					// Per old gradebook, it seems that if all sections are selected, the ID is set to null
					permissionEntryModel.setSectionId(null);
					permissionEntryModel.setSectionDisplayName("All");
				}
				
				permissionEntryModel.setDeleteAction("Delete");
				
				// Before we actually add the permission, we check if it's a duplicate
				if(isDuplicate(permissionEntryModel)) {
					
					MessageBox.alert("WARN", "Selected permission already exists", null);
				}
				else {
					// RPC Create Call
					create(permissionEntryModel);
				}
			}
		});

		
		// GRID
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  

		ColumnConfig column = new ColumnConfig();  
		column.setId(PermissionEntryModel.Key.USER_DISPLAY_NAME.name());  
		column.setHeader("User");  
		column.setWidth(150);
		column.setMenuDisabled(true);
		column.setSortable(false);
		configs.add(column);  
		
		column = new ColumnConfig();  
		column.setId(PermissionEntryModel.Key.PERMISSION_ID.name());  
		column.setHeader("Permission");  
		column.setWidth(100);
		column.setMenuDisabled(true);
		column.setSortable(false);
		configs.add(column);

		// We only show the categories if the GB is setup that way
		if(hasCategories()) {
			
			column = new ColumnConfig();
			column.setId(PermissionEntryModel.Key.CATEGORY_DISPLAY_NAME.name());
			column.setHeader("Category");
			column.setWidth(150);
			column.setMenuDisabled(true);
			column.setSortable(false);
			configs.add(column);
		}
		
		column = new ColumnConfig();  
		column.setId(PermissionEntryModel.Key.SECTION_DISPLAY_NAME.name());  
		column.setHeader("Section");  
		column.setWidth(150);
		column.setMenuDisabled(true);
		column.setSortable(false);
		configs.add(column);
		
		column = new ColumnConfig();  
		column.setId(PermissionEntryModel.Key.DELETE_ACTION.name());  
		column.setHeader("Delete");  
		column.setWidth(100);
		column.setMenuDisabled(true);
		column.setSortable(false);
		column.setRenderer(new PermissionDeleteCellRenderer());
		configs.add(column);
		
		  
		ColumnModel permissionEntryColumnModel = new ColumnModel(configs);
		final CellSelectionModel<PermissionEntryModel> cellSelectionModel = new CellSelectionModel<PermissionEntryModel>();
		cellSelectionModel.setSelectionMode(SelectionMode.SINGLE);
		grid = new Grid<PermissionEntryModel>(permissionEntryListStore, permissionEntryColumnModel);  
		grid.setBorders(true);
		grid.setSelectionModel(cellSelectionModel);
		grid.setAutoHeight(true);
		grid.addListener(Events.CellClick, new Listener<GridEvent>() {

			public void handleEvent(GridEvent gridEvent) {

				if(DELETE_ACTION_GRID_CELL == cellSelectionModel.getSelectCell().cell) {
					PermissionEntryModel permissionEntryModel = cellSelectionModel.getSelectCell().model;
					Gradebook2RPCServiceAsync service = Registry.get("service");
					GradebookModel model = Registry.get(AppConstants.CURRENT);
					service.delete(model.getGradebookUid(), model.getGradebookId(), permissionEntryModel, EntityType.PERMISSION_ENTRY, SecureToken.get(), getDeletePermissionEntryAsyncCallback());
				}
			}
		});

		inputHorizontalPanel.add(userSelectionHorizontalPanel);
		inputHorizontalPanel.add(createPermissionHorizontalPanel);
		
		userSelectionHorizontalPanel.add(userComboBox);
		createPermissionHorizontalPanel.add(new HTML("can"));
		createPermissionHorizontalPanel.add(permissionComboBox);
		if(hasCategories()) {
			createPermissionHorizontalPanel.add(categoryComboBox);
			createPermissionHorizontalPanel.add(new HTML("in"));
		}
		createPermissionHorizontalPanel.add(sectionComboBox);
		createPermissionHorizontalPanel.add(addButton);
		
		// Initially we hide the create permission panel
		createPermissionHorizontalPanel.hide();

		mainVerticalPanel.add(inputHorizontalPanel);
		mainVerticalPanel.add(grid);
		
		add(mainVerticalPanel);
		
		// Standard Close button
		Button button = new AriaButton(i18n.close(), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent be) {
				Dispatcher.forwardEvent(GradebookEvents.HideEastPanel.getEventType(), Boolean.FALSE);
			}
			
		});

		addButton(button);		
	}
	
	private AsyncCallback<PermissionEntryListModel> getPermissionEntryListAsyncCallback() {
		return new AsyncCallback<PermissionEntryListModel>() {

			public void onFailure(Throwable caught) {
				// FIXME: show message
			}

			public void onSuccess(PermissionEntryListModel result) {
				permissionEntryListStore.removeAll();
				List<PermissionEntryModel> permissionEntryModelList = result.getEntries();
				
				if(null != permissionEntryModelList && permissionEntryModelList.size() > 0) {
					permissionEntryListStore.add(permissionEntryModelList);
				}
			}
		};
	}
	
	private AsyncCallback<PermissionEntryModel> getCreatePermissionEntryAsyncCallback() {
		
		return new AsyncCallback<PermissionEntryModel>() {

			public void onFailure(Throwable caught) {
				// FIXME: show message
			}

			public void onSuccess(PermissionEntryModel result) {

				permissionEntryListStore.add(result);
			}
		};
	}
	
	private AsyncCallback<PermissionEntryModel> getDeletePermissionEntryAsyncCallback() {
		return new AsyncCallback<PermissionEntryModel>() {

			public void onFailure(Throwable caught) {
				// FIXME: show message
			}

			public void onSuccess(PermissionEntryModel result) {
				permissionEntryListStore.remove(result);
				loadGrid();
			}
		};
	}
	
	private void loadGrid() {
		
		List<UserModel> users = userComboBox.getSelection();
		if(null != users && 1 == users.size()) {

			String userId = users.get(0).getUserId();
			Gradebook2RPCServiceAsync service = Registry.get("service");
			GradebookModel gbModel = Registry.get(AppConstants.CURRENT);
			service.get(gbModel.getGradebookUid(), gbModel.getGradebookId(), EntityType.PERMISSION_ENTRY, userId, Boolean.TRUE, SecureToken.get(), getPermissionEntryListAsyncCallback());
		}
	}
	
	private void create(PermissionEntryModel permissionEntryModel) {
		
		Gradebook2RPCServiceAsync service = Registry.get("service");
		GradebookModel model = Registry.get(AppConstants.CURRENT);
		service.create(model.getGradebookUid(), model.getGradebookId(), permissionEntryModel, EntityType.PERMISSION_ENTRY, SecureToken.get(), getCreatePermissionEntryAsyncCallback());
	}
			
	private boolean hasCategories() {
		GradebookModel gbModel = Registry.get(AppConstants.CURRENT);
		CategoryType categoryType = gbModel.getCategoryType();
		
		switch(categoryType) {
		case NO_CATEGORIES:
			return false;
		case SIMPLE_CATEGORIES:
		case WEIGHTED_CATEGORIES:
			return true;
		default:
			return false;
		}
	}
	
	private boolean isDuplicate(PermissionEntryModel newPermissionEntryModel) {
		
		List<PermissionEntryModel> permissionEntryModels = permissionEntryListStore.getModels();
		
		for(PermissionEntryModel permissionEntryModel : permissionEntryModels) {
			
			// Compare user
			if(!permissionEntryModel.getUserId().equals(newPermissionEntryModel.getUserId())) {
				continue;
			}
			
			// Compare permission
			if(!permissionEntryModel.getPermissionId().equals(newPermissionEntryModel.getPermissionId())) {
				continue;
			}
			
			// Compare sections
			if((null != permissionEntryModel.getSectionId() && null != newPermissionEntryModel.getSectionId()) &&
				(!permissionEntryModel.getSectionId().equals(newPermissionEntryModel.getSectionId()))) {
				continue;
			}
			
			if(null == permissionEntryModel.getSectionId() && null != newPermissionEntryModel.getSectionId()) {
				continue;
			}
			
			if(null != permissionEntryModel.getSectionId() && null == newPermissionEntryModel.getSectionId()) {
				continue;
			}
			
			// If the gradebook is setup with no categories, we are done
			if(!hasCategories()) {
				return true;
			}
			
			// At this point of the checking, the gradebook was setup with categories
			
			// Compare categories
			if((null != permissionEntryModel.getCategoryId() && null != newPermissionEntryModel.getCategoryId()) &&
			    (!permissionEntryModel.getCategoryId().equals(newPermissionEntryModel.getCategoryId()))) {
				continue;
			}
			
			if(null == permissionEntryModel.getCategoryId() && null != newPermissionEntryModel.getCategoryId()) {
				continue;
			}
			
			if(null != permissionEntryModel.getCategoryId() && null == newPermissionEntryModel.getCategoryId()) {
				continue;
			}
			
			return true;
		}
		
		return false;
	}
	
	
	private class Permission extends BaseModel {

		private static final long serialVersionUID = 1L;
		
		public Permission(String name) {
			super();
			setName(name);
		}
		
		public String getName() {
			return get("name");
		}

		public void setName(String name) {
			set("name", name);
		}
	}
}
