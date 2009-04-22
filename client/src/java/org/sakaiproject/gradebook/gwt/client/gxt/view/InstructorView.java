package org.sakaiproject.gradebook.gwt.client.gxt.view;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaMenu;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaMenuItem;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.BorderLayoutPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.GradeScalePanel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.HelpPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.HistoryPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.LearnerSummaryPanel;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ToolBarEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;

public class InstructorView extends AppView {
	
	private static final String MENU_SELECTOR_FLAG = "menuSelector";
	public enum MenuSelector { ADD_CATEGORY, ADD_ITEM, IMPORT, EXPORT, FINAL_GRADE, GRADE_SCALE, HISTORY };
	
	// The instructor view maintains a link to tree view, since it is required to instantiate multigrade
	private TreeView treeView;
	private MultigradeView multigradeView;
	private ImportExportView importExportView;
	private SingleGradeView singleGradeView;
	
	private ContentPanel borderLayoutContainer;
	private ContentPanel cardLayoutContainer;
	private BorderLayout borderLayout;
	private CardLayout cardLayout;
	private LearnerSummaryPanel singleGradeContainer;
	private HelpPanel helpPanel;
	private GradeScalePanel gradeScalePanel;
	private HistoryPanel historyPanel;
	
	private List<TabConfig> tabConfigurations;
	
	private Listener<MenuEvent> menuEventListener;
	private SelectionListener<MenuEvent> menuSelectionListener;
	private SelectionListener<ToolBarEvent> toolBarSelectionListener;
	
	private ToolBar toolBar;
	
	private Menu fileMenu;
	private Menu windowMenu;
	private PreferencesMenu preferencesMenu;
	
	private MenuItem addCategoryMenuItem;
	
	private BorderLayoutData centerData;
	private BorderLayoutData eastData;
	private BorderLayoutData northData;
	private BorderLayoutData westData;
	
	
	
	private I18nConstants i18n;
	private boolean isEditable;
	
	public InstructorView(Controller controller, TreeView treeView, MultigradeView multigradeView, 
			NotificationView notificationView, ImportExportView importExportView, 
			SingleGradeView singleGradeView, boolean isEditable) {
		super(controller, notificationView);
		this.isEditable = isEditable;
		this.tabConfigurations = new ArrayList<TabConfig>();
		this.treeView = treeView;
		this.multigradeView = multigradeView;
		this.importExportView = importExportView;
		this.singleGradeView = singleGradeView;
		
		toolBar = new ToolBar();
		borderLayoutContainer = new BorderLayoutPanel(); 
		borderLayoutContainer.setId("borderLayoutContainer");
		borderLayoutContainer.setHeaderVisible(false);
		borderLayoutContainer.setTopComponent(toolBar);
		viewport.add(borderLayoutContainer);
		viewportLayout.setActiveItem(borderLayoutContainer);
		
		borderLayout = new BorderLayout();  
		borderLayoutContainer.setLayout(borderLayout);
		
		centerData = new BorderLayoutData(LayoutRegion.CENTER); 
		centerData.setMinSize(100);
		centerData.setMargins(new Margins(5, 0, 5, 0)); 
		
		eastData = new BorderLayoutData(LayoutRegion.EAST, 420);
		eastData.setSplit(true);
		eastData.setCollapsible(true);
		eastData.setFloatable(false);
		eastData.setMargins(new Margins(5));
		eastData.setMaxSize(800);
		
		northData = new BorderLayoutData(LayoutRegion.NORTH, 50);
		northData.setCollapsible(false);
		
		westData = new BorderLayoutData(LayoutRegion.WEST, 400, 100, 800);  
		westData.setSplit(true);  
		westData.setCollapsible(true);  
		westData.setMargins(new Margins(5));
		
		cardLayoutContainer = new ContentPanel() {
			protected void onRender(Element parent, int index) {
				super.onRender(parent, index);
			}
		};

		helpPanel = new HelpPanel() {
			protected void onRender(Element parent, int index) {
				super.onRender(parent, index);
				borderLayout.collapse(LayoutRegion.EAST);
			}
		};

		cardLayoutContainer.setId("cardLayoutContainer");
		cardLayoutContainer.setWidth(400);
		cardLayoutContainer.setBorders(true);
		cardLayoutContainer.setBodyBorder(true);
		cardLayoutContainer.setFrame(true);
		cardLayout = new CardLayout();
		cardLayoutContainer.setLayout(cardLayout);
		cardLayoutContainer.add(helpPanel);
		cardLayoutContainer.add(treeView.getFormPanel());
		cardLayout.setActiveItem(helpPanel);

		borderLayoutContainer.add(treeView.getTreePanel(), westData);
		borderLayoutContainer.add(multigradeView.getMultiGradeContentPanel(), centerData);
		borderLayoutContainer.add(cardLayoutContainer, eastData);
	}

	@Override
	protected void initialize() {
		super.initialize();
	
		i18n = Registry.get(AppConstants.I18N);

		initListeners();
	}
	
	@Override
	protected void initUI(ApplicationModel model) {
		GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);		

		tabConfigurations.add(new TabConfig(AppConstants.TAB_GRADESCALE, i18n.tabGradeScaleHeader(), "gbGradeScaleButton", true, MenuSelector.GRADE_SCALE));
		tabConfigurations.add(new TabConfig(AppConstants.TAB_HISTORY, i18n.tabHistoryHeader(), "gbHistoryButton", true, MenuSelector.HISTORY));

		populateToolBar(i18n, selectedGradebook);

	}

	@Override
	protected void onCloseNotification() {
	}
	
	@Override
	protected void onExpandEastPanel(EastCard activeCard) {

		borderLayout.show(LayoutRegion.EAST);
		borderLayout.expand(LayoutRegion.EAST);

		switch (activeCard) {
		case DELETE_ITEM:
			cardLayoutContainer.setHeading(i18n.deleteItemHeading());
			cardLayout.setActiveItem(treeView.getFormPanel());
			multigradeView.deselectAll();
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
			multigradeView.deselectAll();
			break;
		case NEW_ITEM:
			cardLayoutContainer.setHeading(i18n.newItemHeading());
			cardLayout.setActiveItem(treeView.getFormPanel());
			multigradeView.deselectAll();
			break;
		case EDIT_ITEM:
			cardLayoutContainer.setHeading(i18n.editItemHeading());
			cardLayout.setActiveItem(treeView.getFormPanel());
			multigradeView.deselectAll();
			break;
		case LEARNER_SUMMARY:
			cardLayoutContainer.setHeading(i18n.learnerSummaryHeading());
			cardLayout.setActiveItem(singleGradeContainer);
			break;
		}

	}
	
	@Override
	protected void onItemCreated(ItemModel itemModel) {
		onHideEastPanel(Boolean.FALSE);
	}
	
	@Override
	protected void onLearnerGradeRecordUpdated(UserEntityUpdateAction action) {
		if (singleGradeContainer != null && singleGradeContainer.isVisible()) {
			singleGradeContainer.onLearnerGradeRecordUpdated((StudentModel)action.getModel());
		}
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
			singleGradeContainer = new LearnerSummaryPanel(i18n);
			cardLayoutContainer.add(singleGradeContainer);
		}
		singleGradeContainer.onChangeModel(multigradeView.getStore(), treeView.getTreeStore(), learnerGradeRecordCollection);
		onExpandEastPanel(EastCard.LEARNER_SUMMARY);
	}
	
	@Override
	protected void onSingleView(StudentModel learner) {
		viewport.add(singleGradeView.getDialog());
		viewportLayout.setActiveItem(singleGradeView.getDialog());
	}

	@Override
	protected void onShowGradeScale(Boolean show) {
		if (gradeScalePanel == null) {
			gradeScalePanel = new GradeScalePanel(i18n, isEditable);
			cardLayoutContainer.add(gradeScalePanel);
		}
		onExpandEastPanel(EastCard.GRADE_SCALE);
	}
	
	@Override
	protected void onShowHistory(String identifier) {
		if (historyPanel == null) {
			historyPanel = new HistoryPanel(i18n);
			cardLayoutContainer.add(historyPanel);
		}
		onExpandEastPanel(EastCard.HISTORY);
	}
	
	@Override
	protected void onStartEditItem(ItemModel itemModel) {
		onExpandEastPanel(EastCard.EDIT_ITEM);
	}
	
	@Override
	protected void onStartImport() {
		viewport.add(importExportView.getImportDialog());
		viewportLayout.setActiveItem(importExportView.getImportDialog());
	}
	
	@Override
	protected void onStopImport() {
		viewportLayout.setActiveItem(borderLayoutContainer);
	}
	
	@Override
	protected void onHideEastPanel(Boolean doCommit) {
		borderLayout.hide(LayoutRegion.EAST);
	}
	
	@Override
	protected void onSwitchGradebook(GradebookModel selectedGradebook) {

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
					addCategoryMenuItem.setVisible(selectedGradebook.getGradebookItemModel().getCategoryType() != CategoryType.NO_CATEGORIES);
					break;
				}
				break;
			}
			break;
		}
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
						onShowHistory(null);
						break;
					}
				}
			}
			
		};
				
		menuSelectionListener = new SelectionListener<MenuEvent>() {
			
			@Override
			public void componentSelected(MenuEvent me) {
				MenuSelector selector = me.item.getData(MENU_SELECTOR_FLAG);
				
				switch (selector) {
				case ADD_CATEGORY:
					Dispatcher.forwardEvent(GradebookEvents.NewCategory.getEventType());
					break;
				case ADD_ITEM:
					Dispatcher.forwardEvent(GradebookEvents.NewItem.getEventType());
					break;
				case EXPORT:
					Dispatcher.forwardEvent(GradebookEvents.StartExport.getEventType());
					break;
				case IMPORT:
					Dispatcher.forwardEvent(GradebookEvents.StartImport.getEventType());
					break;
				case FINAL_GRADE:
					Dispatcher.forwardEvent(GradebookEvents.StartFinalgrade.getEventType());
					break;
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
		
	/*
	 * Create a top-level toolbar with menu drop downs
	 */
	private ToolBar populateToolBar(I18nConstants i18n, GradebookModel selectedGradebook) {
		
		//ToolBar toolBar = new ToolBar();
		if (isEditable) {
			TextToolItem fileItem = new TextToolItem(i18n.newMenuHeader());
			fileItem.setMenu(newFileMenu(i18n, selectedGradebook));
			toolBar.add(fileItem);
		}
		
		TextToolItem preferencesItem = new TextToolItem(i18n.prefMenuHeader());
		preferencesItem.setMenu(newPreferencesMenu(i18n, selectedGradebook));
		
		TextToolItem windowItem = new TextToolItem(i18n.viewMenuHeader());
		windowMenu = newWindowMenu(i18n, selectedGradebook);
		windowItem.setMenu(windowMenu);
		
		TextToolItem moreItem = new TextToolItem(i18n.moreMenuHeader());
		moreItem.setMenu(newMoreActionsMenu());
		
		TextToolItem helpItem = new TextToolItem(i18n.helpMenuHeader());
		helpItem.addSelectionListener(toolBarSelectionListener);
		
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
		
		addCategoryMenuItem.setVisible(selectedGradebook.getGradebookItemModel().getCategoryType() != CategoryType.NO_CATEGORIES);

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
		menuItem.setData(MENU_SELECTOR_FLAG, tabConfig.menuSelector);
		menuItem.setEnabled(tabConfig.isClosable);
		menuItem.setId(id);
		menuItem.setIconStyle(tabConfig.iconStyle);
		tabConfig.menuItemId = id;

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
		
		if (isEditable) {
			menuItem = new MenuItem(i18n.headerImport(), menuSelectionListener);
			menuItem.setData(MENU_SELECTOR_FLAG, MenuSelector.IMPORT);
			menuItem.setIconStyle("gbImportItemIcon");
			menuItem.setTitle(i18n.headerImportTitle());
			moreActionsMenu.add(menuItem);
		}
		
		// GRBK-37 : TPA
		menuItem = new MenuItem(i18n.headerFinalGrade(), menuSelectionListener);
		menuItem.setData(MENU_SELECTOR_FLAG, MenuSelector.FINAL_GRADE);
		menuItem.setIconStyle("gbExportItemIcon");
		menuItem.setTitle(i18n.headerFinalGradeTitle());
		moreActionsMenu.add(menuItem);
		
		return moreActionsMenu;
	}
	
}
