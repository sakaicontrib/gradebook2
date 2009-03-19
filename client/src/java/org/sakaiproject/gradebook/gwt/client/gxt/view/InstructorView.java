package org.sakaiproject.gradebook.gwt.client.gxt.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.GradebookState;
import org.sakaiproject.gradebook.gwt.client.GradebookToolFacadeAsync;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.action.PageRequestAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.gxt.NotifyingAsyncCallback;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaMenu;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaMenuItem;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaTabItem;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaTabPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.event.BrowseLearner;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ShowColumnsEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.MultiGradeContentPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.HelpPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.HistoryPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.LearnerSummaryPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.GradeScalePanel;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationModel;
import org.sakaiproject.gradebook.gwt.client.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.DataReader;
import com.extjs.gxt.ui.client.data.ModelReader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.event.ToolBarEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.TabPanel.TabPosition;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InstructorView extends AppView {
	
	private static final String MENU_SELECTOR_FLAG = "menuSelector";
	public enum MenuSelector { ADD_CATEGORY, ADD_ITEM, IMPORT, EXPORT, GRADE_SCALE, HISTORY };
	
	// The instructor view maintains a link to tree view, since it is required to instantiate multigrade
	private TreeView treeView;
	
	private FitLayout contentPanelLayout;
	private ContentPanel contentPanel;
	private LayoutContainer borderLayoutContainer;
	private ContentPanel cardLayoutContainer;
	private BorderLayout borderLayout;
	private CardLayout cardLayout;
	private LearnerSummaryPanel singleGradeContainer;
	private HelpPanel helpPanel;
	private GradeScalePanel gradeScalePanel;
	private HistoryPanel historyPanel;
	
	private MultiGradeContentPanel multigrade;
	private Map<String, ContentPanel> tabContentPanelMap;
	private List<TabConfig> tabConfigurations;
	
	private Listener<MenuEvent> menuEventListener;
	private SelectionListener<MenuEvent> menuSelectionListener;
	private SelectionListener<ToolBarEvent> toolBarSelectionListener;
	private Listener<TabPanelEvent> tabPanelEventListener;
	
	private Menu fileMenu;
	private Menu windowMenu;
	private PreferencesMenu preferencesMenu;
	
	private MenuItem addCategoryMenuItem;
	
	private TabPanel tabPanel;
	private boolean tabMode = false;
	
	private BorderLayoutData centerData;
	private BorderLayoutData eastData;
	private BorderLayoutData northData;
	private BorderLayoutData westData;
	
	
	private ListStore<StudentModel> store;
	private I18nConstants i18n;
	
	public InstructorView(Controller controller, TreeView treeView, NotificationView notificationView) {
		super(controller, notificationView);
		this.tabConfigurations = new ArrayList<TabConfig>();
		this.tabContentPanelMap = new HashMap<String, ContentPanel>();
		this.treeView = treeView;
	}

	@Override
	protected void initialize() {
		super.initialize();
		
		i18n = Registry.get(AppConstants.I18N);
		GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
		initTabs(i18n, selectedGradebook);
		initListeners();
		contentPanelLayout = new FitLayout();
		contentPanel = new ContentPanel();
		contentPanel.setHeaderVisible(false);
		contentPanel.setLayout(contentPanelLayout);
		contentPanel.setTopComponent(newToolBar(i18n, selectedGradebook));
		
		//contentPanel.add(notificationView.getNotificationPanel(), new RowData(1, 35));
		
		borderLayoutContainer = new LayoutContainer(); 
		
		borderLayout = new BorderLayout();  
		borderLayoutContainer.setLayout(borderLayout);
		
		centerData = new BorderLayoutData(LayoutRegion.CENTER); 
		centerData.setMinSize(100);
		centerData.setMargins(new Margins(5, 0, 5, 0)); 
		
		eastData = new BorderLayoutData(LayoutRegion.EAST, 420);
		eastData.setSplit(true);
		eastData.setCollapsible(true);
		eastData.setFloatable(false);
		//eastData.setHidden(true);
		eastData.setMargins(new Margins(5));
		eastData.setMaxSize(800);
		
		northData = new BorderLayoutData(LayoutRegion.NORTH, 50);
		northData.setCollapsible(false);
		//northData.setHidden(true);
		
		westData = new BorderLayoutData(LayoutRegion.WEST, 400);  
		westData.setSplit(true);  
		westData.setCollapsible(true);  
		westData.setMargins(new Margins(5));
		westData.setMinSize(100);
	}
	
	@Override
	protected void initUI(ApplicationModel model) {
		GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);		
		switchTabMode(selectedGradebook.getGradebookUid(), tabMode, true);
		
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
		
		PagingLoader<PagingLoadConfig> loader = new BasePagingLoader<PagingLoadConfig, PagingLoadResult<StudentModel>>(proxy, new ModelReader<PagingLoadConfig>());
		
		store = new ListStore<StudentModel>(loader);
		store.setModelComparer(new EntityModelComparer<StudentModel>());
		store.setMonitorChanges(true);
		
		viewport.add(contentPanel);
	}
	
	@Override
	protected void onBrowseLearner(BrowseLearner event) {
		if (multigrade != null)
			multigrade.onBrowseLearner(event);
	}
	
	@Override
	protected void onCloseNotification() {
		//borderLayout.hide(LayoutRegion.NORTH);
	}
	
	@Override
	protected void onExpandEastPanel(EastCard activeCard) {

		borderLayout.show(LayoutRegion.EAST);
		borderLayout.expand(LayoutRegion.EAST);

		switch (activeCard) {
		case DELETE_ITEM:
			cardLayoutContainer.setHeading(i18n.deleteItemHeading());
			cardLayout.setActiveItem(treeView.getFormPanel());
			if (multigrade != null)
				multigrade.deselectAll();
			break;
		case GRADE_SCALE:
			cardLayoutContainer.setHeading(i18n.gradeScaleHeading());
			cardLayout.setActiveItem(gradeScalePanel);
			break;
		case HELP:
			cardLayoutContainer.setHeading(i18n.helpHeading());
			cardLayout.setActiveItem(helpPanel);
			break;
		case HISTORY:
			cardLayoutContainer.setHeading(i18n.historyHeading());
			cardLayout.setActiveItem(historyPanel);
			break;
		case NEW_CATEGORY:
			cardLayoutContainer.setHeading(i18n.newCategoryHeading());
			cardLayout.setActiveItem(treeView.getFormPanel());
			if (multigrade != null)
				multigrade.deselectAll();
			break;
		case NEW_ITEM:
			cardLayoutContainer.setHeading(i18n.newItemHeading());
			cardLayout.setActiveItem(treeView.getFormPanel());
			if (multigrade != null)
				multigrade.deselectAll();
			break;
		case EDIT_ITEM:
			cardLayoutContainer.setHeading(i18n.editItemHeading());
			cardLayout.setActiveItem(treeView.getFormPanel());
			if (multigrade != null)
				multigrade.deselectAll();
			break;
		case LEARNER_SUMMARY:
			cardLayoutContainer.setHeading(i18n.learnerSummaryHeading());
			cardLayout.setActiveItem(singleGradeContainer);
			break;
		}

	}
	
	@Override
	protected void onItemCreated(ItemModel itemModel) {
		if (multigrade != null)
			multigrade.onItemCreated(itemModel);
		
		onHideEastPanel(Boolean.FALSE);
	}
	
	@Override
	protected void onItemDeleted(ItemModel itemModel) {
		if (multigrade != null)
			multigrade.onItemDeleted(itemModel);
	}
	
	@Override
	protected void onItemUpdated(ItemModel itemModel) {	
		if (multigrade != null)
			multigrade.onItemUpdated(itemModel);
	}
	
	@Override
	protected void onLearnerGradeRecordUpdated(UserEntityAction<?> action) {
		if (multigrade != null)
			multigrade.onLearnerGradeRecordUpdated(action);
	}
	
	@Override
	protected void onLoadItemTreeModel(GradebookModel selectedGradebook) {
		if (multigrade != null)
			multigrade.onLoadItemTreeModel(selectedGradebook);
	}
	
	@Override
	protected void onNewCategory(ItemModel itemModel) {
		onExpandEastPanel(EastCard.NEW_CATEGORY);
	}
	
	@Override
	protected void onNewItem(ItemModel itemModel) {
		onExpandEastPanel(EastCard.NEW_ITEM);
	}
	
	@Override
	protected void onOpenNotification() {
		//borderLayout.show(LayoutRegion.NORTH);
	}
	
	@Override
	protected void onRefreshCourseGrades() {
		if (multigrade != null)
			multigrade.onRefreshCourseGrades();
	}
	
	@Override
	protected void onSelectLearner(StudentModel learner) {
		if (singleGradeContainer != null && singleGradeContainer.isVisible()) {
			onSingleGrade(learner);
		}
	}
	
	@Override
	protected void onSingleGrade(StudentModel learnerGradeRecordCollection) {
		if (singleGradeContainer == null) {
			singleGradeContainer = new LearnerSummaryPanel(multigrade.getStore());
			cardLayoutContainer.add(singleGradeContainer);
		}
		singleGradeContainer.onChangeModel(multigrade.getStore(), treeView.getTreeStore(), learnerGradeRecordCollection);
		onExpandEastPanel(EastCard.LEARNER_SUMMARY);
	}
	
	@Override
	protected void onShowColumns(ShowColumnsEvent event) {
		if (multigrade != null)
			multigrade.onShowColumns(event);
	}
	
	protected void onShowGradeScale(Boolean show) {
		if (gradeScalePanel == null) {
			gradeScalePanel = new GradeScalePanel();
			cardLayoutContainer.add(gradeScalePanel);
		}
		onExpandEastPanel(EastCard.GRADE_SCALE);
		//cardLayout.setActiveItem(gradeScalePanel);
	}
	
	protected void onShowHistory(Boolean show) {
		if (historyPanel == null) {
			historyPanel = new HistoryPanel();
			cardLayoutContainer.add(historyPanel);
		}
		onExpandEastPanel(EastCard.HISTORY);
		//cardLayout.setActiveItem(historyPanel);
	}
	
	@Override
	protected void onStartEditItem(ItemModel itemModel) {
		onExpandEastPanel(EastCard.EDIT_ITEM);
	}
	
	@Override
	protected void onHideEastPanel(Boolean doCommit) {
		borderLayout.hide(LayoutRegion.EAST);
	}
	
	@Override
	protected void onSwitchGradebook(GradebookModel selectedGradebook) {
		
		if (multigrade != null) 
			multigrade.onSwitchGradebook(selectedGradebook);
		
		if (preferencesMenu != null)
			preferencesMenu.onSwitchGradebook(selectedGradebook);
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onUserChange(UserEntityAction<?> action) {

		switch (action.getEntityType()) {
		case GRADEBOOK:
			switch (action.getActionType()) {
			case UPDATE:
				UserEntityUpdateAction<GradebookModel> updateAction = (UserEntityUpdateAction<GradebookModel>)action;
				GradebookModel.Key gradebookModelKey = GradebookModel.Key.valueOf(updateAction.getKey());
				switch (gradebookModelKey) {
				case CATEGORYTYPE:
					GradebookModel selectedGradebook = updateAction.getModel();
					addCategoryMenuItem.setVisible(selectedGradebook.getCategoryType() != CategoryType.NO_CATEGORIES);
					break;
				}
				break;
			}
			break;
		}

		if (multigrade != null)
			multigrade.onUserChange(action);
	}

	/*
	 * The goal here is to reduce the number of overall listeners in the application to a minimum
	 */
	private void initListeners() {
		
		menuEventListener = new Listener<MenuEvent>() {

			public void handleEvent(MenuEvent me) {
				
				if (me.type == Events.Select) {
					MenuItem menuItem = (MenuItem)me.item;
					MenuSelector menuSelector = menuItem.getData(MENU_SELECTOR_FLAG);
					
					switch (menuSelector) {
					case GRADE_SCALE:
						onShowGradeScale(Boolean.TRUE);
						break;
					case HISTORY:
						onShowHistory(Boolean.TRUE);
						break;
					}
					
					/*String menuItemId = me.item.getId();
					int indexOfPrefix = menuItemId.indexOf(AppConstants.WINDOW_MENU_ITEM_PREFIX);
					if (indexOfPrefix != -1) {
						int index = indexOfPrefix + AppConstants.WINDOW_MENU_ITEM_PREFIX.length();
						String tabItemId = menuItemId.substring(index);
						GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
						if (!tabMode)
							switchTabMode(selectedGradebook.getGradebookUid(), true, false);
						
						TabItem tabItem = tabPanel.findItem(tabItemId, false);
					
						if (menuItem.isChecked()) {
		
							if (tabItem == null) {
								for (TabConfig tabConfig : tabConfigurations) {
									if (tabConfig.id.equals(tabItemId)) {
										tabItem = newTabItem(tabConfig);
										tabPanel.add(tabItem);
										break;
									}
								}
							} 
							
							tabPanel.setSelection(tabItem);
							tabPanel.saveState();
						} else {
							if (tabItem != null) {
								tabItem.close();
							}
						}
					}*/
				}
			}
			
		};
				
		menuSelectionListener = new SelectionListener<MenuEvent>() {
			
			@Override
			public void componentSelected(MenuEvent me) {
				MenuSelector selector = me.item.getData(MENU_SELECTOR_FLAG);
				
				switch (selector) {
				case ADD_CATEGORY:
					Dispatcher.forwardEvent(GradebookEvents.NewCategory);
					break;
				case ADD_ITEM:
					Dispatcher.forwardEvent(GradebookEvents.NewItem);
					break;
				case EXPORT:
					Dispatcher.forwardEvent(GradebookEvents.StartExport);
					break;
				case IMPORT:
					Dispatcher.forwardEvent(GradebookEvents.StartImport);
					break;
				}
			}
		
		};
		
		tabPanelEventListener = new Listener<TabPanelEvent>() {

			public void handleEvent(TabPanelEvent tpe) {
				// Ensure that we only response to Remove events
				if (tpe.type == Events.Remove) {
					// First thing, we need to make sure we uncheck the relevant menu item
					// So we generate the menu item id (by convention)
					String menuItemId = new StringBuilder().append(AppConstants.WINDOW_MENU_ITEM_PREFIX).append(tpe.item.getItemId()).toString();
					// Look for it in the window menu
					CheckMenuItem checkMenuItem = (CheckMenuItem)windowMenu.getItemByItemId(menuItemId);
					if (checkMenuItem != null)
						checkMenuItem.setChecked(false, true);
					
					// Now, in the case where we have only one tab, we can switch off tab mode
					if (tpe.container.getItemCount() == 1) {
						GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
						//PersistentStore.storePersistentField(selectedGradebook.getGradebookUid(), AppConstants.TAB_MODE, "checked", Boolean.FALSE.toString());
						switchTabMode(selectedGradebook.getGradebookUid(), false, false);
					}
				}
			}
			
		};
		
		toolBarSelectionListener = new SelectionListener<ToolBarEvent>() {

			@Override
			public void componentSelected(ToolBarEvent tbe) {
				onExpandEastPanel(EastCard.HELP);
			}
			
		};
	}
	
	private void initTabs(I18nConstants i18n, GradebookModel selectedGradebook) {
		//tabConfigurations.add(new TabConfig(AppConstants.TAB_GRADES, i18n.tabGradesHeader(), false));
		tabConfigurations.add(new TabConfig(AppConstants.TAB_GRADESCALE, i18n.tabGradeScaleHeader(), true, MenuSelector.GRADE_SCALE));
		tabConfigurations.add(new TabConfig(AppConstants.TAB_HISTORY, i18n.tabHistoryHeader(), true, MenuSelector.HISTORY));
	
		String gradebookUid = selectedGradebook.getGradebookUid();
		tabMode = GradebookState.getTabMode(gradebookUid);
	}
	
	private LayoutContainer getBorderLayoutContainer() {
		if (multigrade == null) {
			multigrade = new MultiGradeContentPanel(null);
			
			cardLayoutContainer = new ContentPanel() {
				protected void onRender(Element parent, int index) {
				    super.onRender(parent, index);
				    //borderLayout.hide(LayoutRegion.EAST);
				    //treeView.getTreePanel().expandTrees();
				}
			};
						
			helpPanel = new HelpPanel() {
				protected void onRender(Element parent, int index) {
				    super.onRender(parent, index);
				    borderLayout.collapse(LayoutRegion.EAST);
				}
			};
			
			cardLayoutContainer.setWidth(400);
			cardLayoutContainer.setBorders(true);
			cardLayoutContainer.setBodyBorder(true);
			cardLayoutContainer.setFrame(true);
			cardLayout = new CardLayout();
			cardLayoutContainer.setLayout(cardLayout);
			cardLayoutContainer.add(helpPanel);
			cardLayoutContainer.add(treeView.getFormPanel());
			cardLayout.setActiveItem(helpPanel);
			
			//borderLayoutContainer.add(notificationView.getNotificationPanel(), northData);
			borderLayoutContainer.add(treeView.getTreePanel(), westData);
			borderLayoutContainer.add(multigrade, centerData);
			borderLayoutContainer.add(cardLayoutContainer, eastData);
		}
		
		return borderLayoutContainer;
	}
	
	private TabItem newTabItem(TabConfig tabConfig) {
		TabItem tab = new AriaTabItem(tabConfig.header);  
		tab.addStyleName("pad-text");  
		//tab.add(contentPanel);
		tab.setClosable(tabConfig.isClosable);
		tab.setLayout(new FitLayout());
		tab.setItemId(tabConfig.id);
		
		ContentPanel tabContentPanel = null;
		if (tab.getItemCount() > 0)
			tabContentPanel = (ContentPanel)tab.getItem(0);
		
		if (tabContentPanel == null) {
			if (tabContentPanelMap.containsKey(tabConfig.id)) 
				tabContentPanel = tabContentPanelMap.get(tabConfig.id);
			else {
				if (tabConfig.id.equals(AppConstants.TAB_GRADES)) {
					tab.add(getBorderLayoutContainer());
				} else if (tabConfig.id.equals(AppConstants.TAB_GRADESCALE)) {
					tab.add(new GradeScalePanel());
				} else if (tabConfig.id.equals(AppConstants.TAB_HISTORY)) {
					tab.add(new HistoryPanel());
				}
			}
		}
		
		String menuItemId = new StringBuilder().append(AppConstants.WINDOW_MENU_ITEM_PREFIX).append(tabConfig.id).toString();
		CheckMenuItem checkMenuItem = (CheckMenuItem)windowMenu.getItemByItemId(menuItemId);
		if (checkMenuItem != null)
			checkMenuItem.setChecked(true, true);
		
		return tab;
	}
	
	/*
	 * Create a top-level toolbar with menu drop downs
	 */
	private ToolBar newToolBar(I18nConstants i18n, GradebookModel selectedGradebook) {
		
		ToolBar toolBar = new ToolBar();
		TextToolItem fileItem = new TextToolItem(i18n.newMenuHeader());
		fileItem.setMenu(newFileMenu(i18n, selectedGradebook));
		
		TextToolItem preferencesItem = new TextToolItem(i18n.prefMenuHeader());
		preferencesItem.setMenu(newPreferencesMenu(i18n, selectedGradebook));
		
		TextToolItem windowItem = new TextToolItem(i18n.viewMenuHeader());
		windowMenu = newWindowMenu(i18n, selectedGradebook);
		windowItem.setMenu(windowMenu);
		
		TextToolItem moreItem = new TextToolItem(i18n.moreMenuHeader());
		moreItem.setMenu(newMoreActionsMenu());
		
		TextToolItem helpItem = new TextToolItem(i18n.helpMenuHeader());
		helpItem.addSelectionListener(toolBarSelectionListener);
		
		toolBar.add(fileItem);
		toolBar.add(preferencesItem);
		toolBar.add(windowItem);
		toolBar.add(moreItem);
		toolBar.add(helpItem);
		
		return toolBar;
	}
	
	/*
	 * Create a new file menu 
	 */
	private Menu newFileMenu(I18nConstants i18n, GradebookModel selectedGradebook) {
		
		// This should be obvious. Just create the required menu object and its menu items
		fileMenu = new AriaMenu();
		addCategoryMenuItem = new AriaMenuItem(i18n.categoryName(), menuSelectionListener);
		addCategoryMenuItem.setData(MENU_SELECTOR_FLAG, MenuSelector.ADD_CATEGORY);
		addCategoryMenuItem.setIconStyle("gbAddCategoryIcon");
		addCategoryMenuItem.setId(AppConstants.ID_ADD_CATEGORY_MENUITEM);
		MenuItem addItem = new AriaMenuItem(i18n.itemName(), menuSelectionListener);
		addItem.setData(MENU_SELECTOR_FLAG, MenuSelector.ADD_ITEM);
		addItem.setIconStyle("gbAddItemIcon");
		addItem.setId(AppConstants.ID_ADD_ITEM_MENUITEM);
		
		// Attach the items to the menu
		fileMenu.add(addCategoryMenuItem);
		fileMenu.add(addItem);
		
		addCategoryMenuItem.setVisible(selectedGradebook.getCategoryType() != CategoryType.NO_CATEGORIES);

		return fileMenu;
	}
	
	private Menu newPreferencesMenu(I18nConstants i18n, GradebookModel selectedGradebook) {
		preferencesMenu = new PreferencesMenu(i18n, treeView);
		preferencesMenu.onSwitchGradebook(selectedGradebook);
		return preferencesMenu;
	}
	
	private Menu newWindowMenu(I18nConstants i18n, GradebookModel selectedGradebook) {
		Menu windowMenu = new AriaMenu();
	
		for (TabConfig tabConfig : tabConfigurations) {
			MenuItem windowMenuItem = newWindowMenuItem(tabConfig);
			windowMenu.add(windowMenuItem);
		}
		
		return windowMenu;
	}
	
	private MenuItem newWindowMenuItem(TabConfig tabConfig) {
		String id = new StringBuilder().append(AppConstants.WINDOW_MENU_ITEM_PREFIX).append(tabConfig.id).toString();
		MenuItem menuItem = new AriaMenuItem();
		menuItem.setText(tabConfig.header);
		//menuItem.setChecked(!tabMode, true);
		menuItem.setData(MENU_SELECTOR_FLAG, tabConfig.menuSelector);
		menuItem.setEnabled(tabConfig.isClosable);
		menuItem.setId(id);
		tabConfig.menuItemId = id;
		
		/*String storedTabVisibility = PersistentStore.getPersistentField(gradebookUid, "tab", name);
		if (storedTabVisibility != null) {
			Boolean isChecked = Boolean.valueOf(storedTabVisibility);
			if (isChecked != null) {
				menuItem.setChecked(isChecked.booleanValue());
			}
		}*/

		menuItem.addListener(Events.Select, menuEventListener);
		
		return menuItem;
	}
	
	
	private Menu newMoreActionsMenu() {
		Menu moreActionsMenu = new Menu();
		
		MenuItem menuItem = new MenuItem(i18n.headerExport(), menuSelectionListener);
		menuItem.setData(MENU_SELECTOR_FLAG, MenuSelector.EXPORT);
		menuItem.setIconStyle("gbExportItemIcon");
		menuItem.setTitle(i18n.headerExportTitle());
		moreActionsMenu.add(menuItem);
		
		menuItem = new MenuItem(i18n.headerImport(), menuSelectionListener);
		menuItem.setData(MENU_SELECTOR_FLAG, MenuSelector.IMPORT);
		menuItem.setIconStyle("gbImportItemIcon");
		menuItem.setTitle(i18n.headerImportTitle());
		moreActionsMenu.add(menuItem);
		
		return moreActionsMenu;
	}

	private void switchTabMode(String gradebookUid, boolean tabMode, boolean populateTabItems) {
		this.tabMode = tabMode;
		
		if (contentPanel == null)
			return;
		
		GradebookState.setTabMode(gradebookUid, tabMode);
		//PersistentStore.storePersistentField(gradebookUid, AppConstants.TAB_MODE, "checked", Boolean.valueOf(tabMode).toString());
		
		LayoutContainer borderLayoutContainer = getBorderLayoutContainer();
		
		if (tabMode) {
			if (tabPanel == null) {
				tabPanel = new AriaTabPanel();
				tabPanel.addListener(Events.Remove, tabPanelEventListener);
			}
			
			tabPanel.setResizeTabs(true);
			tabPanel.setMinTabWidth(150);
			tabPanel.setTabPosition(TabPosition.TOP);
			
			for (TabConfig tabConfig : tabConfigurations) {
				TabItem tabItem = tabPanel.findItem(tabConfig.id, false);
					
				if (tabItem == null) {
					if (populateTabItems || tabConfig.id.equals(AppConstants.TAB_GRADES)) {
						tabItem = newTabItem(tabConfig);
						tabPanel.add(tabItem);
					}
				}
			}
	
			if (contentPanel.getItemCount() > 1)
				removeMainContainer(borderLayoutContainer);
			
			
			addMainContainer(tabPanel);
		} else {
			
			if (tabPanel != null) {
				List<TabItem> tabItems = tabPanel.getItems();
				
				removeMainContainer(tabPanel);
				if (tabItems != null) {
					// Iterate through the tab items and make sure we've cleared out their child components
					for (TabItem tabItem : tabItems) {
						
						if (tabItem.getItemId().equals(AppConstants.TAB_GRADES)) {
							// The multigrade item is a special case, since we want to ensure that it gets added
							// to the content panel rather than simply being discarded. This will remove it
							// from the tabItem due to a call to item.removeFromParent() 
							addMainContainer(getBorderLayoutContainer());
						
						} else {
							// Otherwise, we want to maintain a reference to this content panel so it doesn't 
							// simply get thrown away
							ContentPanel tabContentPanel = (ContentPanel)tabItem.getItem(0);
							if (tabContentPanel != null)
								tabContentPanelMap.put(tabItem.getItemId(), tabContentPanel);
							
							// Now remove the child components
							tabItem.removeAll();
						}
					}
					// Now we can remove all of the tab items
					tabPanel.removeAll();
				}
			} else {
				addMainContainer(getBorderLayoutContainer());
			}

		}
		
		//if (contentPanel.isRendered())
		//	contentPanel.layout();
	}
	
	
	private void addMainContainer(Container<?> container) {
		contentPanel.add(container);
		//contentPanelLayout.setActiveItem(container);
	}
	
	private void removeMainContainer(Container<?> container) {
		contentPanel.remove(container);
	}
	
}
