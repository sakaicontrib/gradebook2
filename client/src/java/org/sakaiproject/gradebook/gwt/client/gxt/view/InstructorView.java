/**********************************************************************************
 *
 * Copyright (c) 2008, 2009, 2010, 2011 The Regents of the University of California
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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.ExportDetails;
import org.sakaiproject.gradebook.gwt.client.ExportDetails.ExportType;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.RestBuilder;
import org.sakaiproject.gradebook.gwt.client.RestCallback;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.api.Card;
import org.sakaiproject.gradebook.gwt.client.api.Wizard;
import org.sakaiproject.gradebook.gwt.client.gin.WidgetInjector;
import org.sakaiproject.gradebook.gwt.client.gxt.JsonUtil;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaButton;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaMenu;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaMenuItem;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ItemUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityOverlay;
import org.sakaiproject.gradebook.gwt.client.gxt.model.FinalGradeSubmissionStatusModel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.NullSensitiveCheckBox;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.BorderLayoutPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.GradeScalePanel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.HistoryPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.ItemFormPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.LearnerSummaryPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.PermissionsPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.StatisticsPanel;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationSetup;
import org.sakaiproject.gradebook.gwt.client.model.FinalGradeSubmissionStatus;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;
import org.sakaiproject.gradebook.gwt.client.resource.GradebookResources;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class InstructorView extends AppView {

	private static final String MENU_SELECTOR_FLAG = "menuSelector";
	private static final String INCLUDE_COMMENTS = "inc_comments";
	public enum MenuSelector { ADD_CATEGORY, ADD_ITEM, IMPORT, EXPORT, EXPORT_DATA, EXPORT_DATA_CSV, EXPORT_STRUCTURE, EXPORT_STRUCTURE_CSV, EXPORT_DATA_XLS, EXPORT_STRUCTURE_XLS, FINAL_GRADE, GRADE_SCALE, SETUP, HISTORY, GRADER_PERMISSION_SETTINGS, STATISTICS, EXPORT_STRUCTURE_XLSX, EXPORT_DATA_XLSX };

	private TreeView treeView;
	private MultigradeView multigradeView;
	private SingleGradeView singleGradeView;
	private PermissionsView permissionsView;

	private ContentPanel borderLayoutContainer;
	private LayoutContainer centerLayoutContainer;
	private ContentPanel eastLayoutContainer;
	private BorderLayout borderLayout;
	private CardLayout centerCardLayout;
	private CardLayout eastCardLayout;
	private LearnerSummaryPanel singleGradeContainer;
	private GradeScalePanel gradeScalePanel;
	private HistoryPanel historyPanel;
	private StatisticsPanel statisticsPanel;

	private List<TabConfig> tabConfigurations;

	private Listener<MenuEvent> menuEventListener;
	private SelectionListener<MenuEvent> menuSelectionListener;
	private SelectionListener<ButtonEvent> toolBarSelectionListener;

	private ToolBar toolBar;

	private Menu fileMenu;
	private Menu viewMenu;
	private Menu editMenu;

	private MenuItem addCategoryMenuItem;

	private BorderLayoutData centerData;
	private BorderLayoutData eastData;
	private BorderLayoutData northData;
	private BorderLayoutData westData;

	private GradebookResources resources;
	private I18nConstants i18n;
	private boolean isEditable;
	
	protected Wizard exportWizard = null;
	private CheckBox exportCommentsCheckbox;
	private ExportDetails exportDetails;
	
	private LabelField finalGradeSubmissionStatusBanner;

	public InstructorView(Controller controller, TreeView treeView, 
			MultigradeView multigradeView, SingleGradeView singleGradeView, 
			PermissionsView permissionsView, boolean isEditable, final boolean isNewGradebook) {
		super(controller);
		this.isEditable = isEditable;
		this.tabConfigurations = new ArrayList<TabConfig>();
		this.treeView = treeView;
		this.multigradeView = multigradeView;
		this.singleGradeView = singleGradeView;
		this.i18n = Registry.get(AppConstants.I18N);
		this.permissionsView = permissionsView;
		this.resources = Registry.get(AppConstants.RESOURCES);

		initListeners();

		toolBar = new ToolBar();
		borderLayoutContainer = new BorderLayoutPanel(); 
		borderLayoutContainer.setId("borderLayoutContainer");
		borderLayoutContainer.setHeaderVisible(false);
		borderLayoutContainer.setTopComponent(toolBar);

		borderLayout = new BorderLayout();  
		borderLayoutContainer.setLayout(borderLayout);

		centerData = new BorderLayoutData(LayoutRegion.CENTER); 
		centerData.setMinSize(100);
		centerData.setMargins(new Margins(5, 0, 5, 0)); 

		eastData = new BorderLayoutData(LayoutRegion.EAST,840, 420, 840);
		eastData.setSplit(true);
		eastData.setCollapsible(true);
		eastData.setFloatable(false);
		eastData.setMargins(new Margins(5));
		eastData.setMaxSize(840);
		eastData.setHidden(true);

		northData = new BorderLayoutData(LayoutRegion.NORTH, 50);
		northData.setCollapsible(false);
		northData.setHidden(true);

		westData = new BorderLayoutData(LayoutRegion.WEST, 400, 100, 800);  
		westData.setSplit(true);  
		westData.setCollapsible(true);  
		westData.setMargins(new Margins(5));

		centerLayoutContainer = new LayoutContainer();
		centerCardLayout = new CardLayout();
		centerLayoutContainer.setLayout(centerCardLayout);

		eastLayoutContainer = new ContentPanel() {
			protected void onRender(Element parent, int index) {
				super.onRender(parent, index);
			}
		};

		eastLayoutContainer.setId("cardLayoutContainer");
		eastLayoutContainer.setWidth(600);
		eastLayoutContainer.setBorders(true);
		eastLayoutContainer.setBodyBorder(true);
		eastLayoutContainer.setFrame(true);
		eastCardLayout = new CardLayout();
		eastLayoutContainer.setLayout(eastCardLayout);

		borderLayoutContainer.add(new LayoutContainer(), northData);
		borderLayoutContainer.add(treeView.getTreePanel(), westData);
		borderLayoutContainer.add(centerLayoutContainer, centerData);
		borderLayoutContainer.add(eastLayoutContainer, eastData);

		if (isEditable) {
			tabConfigurations.add(new TabConfig(AppConstants.TAB_SETUP, i18n.tabSetupHeader(), resources.application_edit(), true, MenuSelector.SETUP, i18n.editMenuGradebookSetupHeading()));
			tabConfigurations.add(new TabConfig(AppConstants.TAB_GRADESCALE, i18n.tabGradeScaleHeader(), resources.calculator_edit(), true, MenuSelector.GRADE_SCALE, i18n.editMenuGradescaleHeading()));
			tabConfigurations.add(new TabConfig(AppConstants.TAB_GRADER_PER_SET, i18n.tabGraderPermissionSettingsHeader(), resources.table_add(), true, MenuSelector.GRADER_PERMISSION_SETTINGS, i18n.editMenuGraderPermissionsHeading()));
			tabConfigurations.add(new TabConfig(AppConstants.TAB_HISTORY, i18n.tabHistoryHeader(), resources.calendar(), true, MenuSelector.HISTORY, i18n.viewMenuHistoryHeading()));
		}
		tabConfigurations.add(new TabConfig(AppConstants.TAB_STATISTICS, i18n.tabStatisticsHeader(), resources.chart_curve(), true, MenuSelector.STATISTICS, i18n.viewMenuStatsHeading()));

		populateToolBar(i18n);

		viewport.add(borderLayoutContainer);
		viewportLayout.setActiveItem(borderLayoutContainer);
	}

	@Override
	protected void initialize() {
		super.initialize();
	}

	@Override
	protected void initUI(ApplicationSetup model) {
		Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);		

		if (addCategoryMenuItem != null)
			addCategoryMenuItem.setVisible(selectedGradebook.getGradebookItemModel().getCategoryType() != CategoryType.NO_CATEGORIES);

		if (DataTypeConversionUtil.checkBoolean(selectedGradebook.isNewGradebook()))
			Dispatcher.forwardEvent(GradebookEvents.StartEditItem.getEventType(), selectedGradebook.getGradebookItemModel());

		centerLayoutContainer.add(multigradeView.getMultiGradeContentPanel());
		centerLayoutContainer.add(treeView.getFormPanel());
		centerCardLayout.setActiveItem(multigradeView.getMultiGradeContentPanel());

		Boolean isNewGBFromAuth = Registry.get(AppConstants.IS_NEW_GRADEBOOK);
		Boolean isNewGBFromApp = selectedGradebook.isNewGradebook();
		boolean isNewGradebook = DataTypeConversionUtil.checkBoolean(isNewGBFromAuth) ||
		DataTypeConversionUtil.checkBoolean(isNewGBFromApp);
		if (isNewGradebook) {
			onShowSetup();
		}
		
		// GRBK-824
		if(model.checkFinalGradeSubmissionStatus()) {
			
			checkFinalGradeSubmissionStatus(selectedGradebook.getGradebookUid(), true);
		}
	}

	@Override
	protected void onCloseNotification() {
	}

	@Override
	protected void onExpandEastPanel(EastCard activeCard) {

		ItemFormPanel formPanel = treeView.getFormPanel();

		switch (activeCard) {
		case GRADE_SCALE:
		case HELP:
		case HISTORY:
		case STATISTICS:
			eastData.setSize(840); 
			borderLayout.show(LayoutRegion.EAST);
			borderLayout.expand(LayoutRegion.EAST);
			break;
		case LEARNER_SUMMARY:
			eastData.setSize(420); 
			borderLayout.show(LayoutRegion.EAST);
			borderLayout.expand(LayoutRegion.EAST);
			break;
		default:
			// GRBK-981 : Check if this action is hiding, setting inactive, the grade scale panel
			if(null != gradeScalePanel && gradeScalePanel.equals(eastCardLayout.getActiveItem())){
				gradeScalePanel.onClose();
			}
			borderLayout.hide(LayoutRegion.EAST);
			break;
		}

		switch (activeCard) {
		case DELETE_CATEGORY:
			formPanel.setHeading(i18n.deleteCategoryHeading());
			centerCardLayout.setActiveItem(treeView.getFormPanel());
			multigradeView.deselectAll();
			break;
		case DELETE_ITEM:
			formPanel.setHeading(i18n.deleteItemHeading());
			centerCardLayout.setActiveItem(treeView.getFormPanel());
			multigradeView.deselectAll();
			break;
		case GRADE_SCALE:
			eastLayoutContainer.setHeading(i18n.gradeScaleHeading());
			eastCardLayout.setActiveItem(gradeScalePanel);
			break;
		case STATISTICS:
			viewportLayout.setActiveItem(statisticsPanel);
			break;
		case HISTORY:
			viewportLayout.setActiveItem(historyPanel);
			break;
		case NEW_CATEGORY:
			formPanel.setHeading(i18n.newCategoryHeading());
			centerCardLayout.setActiveItem(treeView.getFormPanel());
			multigradeView.deselectAll();
			break;
		case NEW_ITEM:
			formPanel.setHeading(i18n.newItemHeading());
			centerCardLayout.setActiveItem(treeView.getFormPanel());
			multigradeView.deselectAll();
			break;
		case EDIT_CATEGORY:
			formPanel.setHeading(i18n.editCategoryHeading());
			centerCardLayout.setActiveItem(treeView.getFormPanel());
			multigradeView.deselectAll();
			break;
		case EDIT_GRADEBOOK:
			formPanel.setHeading(i18n.editGradebookHeading());
			centerCardLayout.setActiveItem(treeView.getFormPanel());
			multigradeView.deselectAll();
			break;
		case EDIT_ITEM:
			formPanel.setHeading(i18n.editItemHeading());
			centerCardLayout.setActiveItem(treeView.getFormPanel());
			multigradeView.deselectAll();
			break;
		case LEARNER_SUMMARY:
			eastLayoutContainer.setHeading(i18n.learnerSummaryHeading());
			eastCardLayout.setActiveItem(singleGradeContainer);
			break;
		}

	}

	@Override
	protected void onFailedToUpdateItem(ItemUpdate itemUpdate) {
		if (gradeScalePanel != null) {
			gradeScalePanel.onFailedToUpdateItem(itemUpdate);
		}
	}

	@Override
	protected void onGradeTypeUpdated(Gradebook selectedGradebook) {
		if (singleGradeContainer != null) {
			singleGradeContainer.onGradeTypeUpdated(selectedGradebook);
		}
	}

	@Override
	protected void onItemCreated(Item itemModel) {
		onHideEastPanel(Boolean.FALSE);
	}

	@Override
	protected void onLearnerGradeRecordUpdated(UserEntityUpdateAction action) {
		if (singleGradeContainer != null && singleGradeContainer.isVisible()) {
			singleGradeContainer.updateLearnerItems(multigradeView.getStore(), treeView.getTreeStore());
			singleGradeContainer.onLearnerGradeRecordUpdated(action.getModel());
		}

		if (statisticsPanel != null && statisticsPanel.isVisible()) {
			statisticsPanel.onLearnerGradeRecordUpdated(action.getModel());
		}
	}

	@Override
	protected void onLoadItemTreeModel(Gradebook selectedGradebook) {

	}

	@Override
	protected void onNewCategory(Item itemModel) {
		onExpandEastPanel(EastCard.NEW_CATEGORY);
	}

	@Override
	protected void onNewItem(Item itemModel) {
		onExpandEastPanel(EastCard.NEW_ITEM);
	}

	@Override
	protected void onOpenNotification() {

	}

	@Override
	protected void onRefreshGradebookItems(Gradebook gradebookModel) {

	}

	@Override
	protected void onRefreshGradebookSetup(Gradebook gradebookModel) {
		if (addCategoryMenuItem != null)
			addCategoryMenuItem.setVisible(gradebookModel.getGradebookItemModel().getCategoryType() != CategoryType.NO_CATEGORIES);

		if (singleGradeContainer != null)
			singleGradeContainer.onRefreshGradebookSetup(gradebookModel);


		Boolean isLetterGrade = gradebookModel.getGradebookItemModel().getGradeType() == GradeType.LETTERS;
		MenuItem gradeScale = (MenuItem) editMenu.getItemByItemId(AppConstants.WINDOW_MENU_ITEM_PREFIX  + AppConstants.TAB_GRADESCALE);
		if(isLetterGrade){
			gradeScale.disable();
		} else {
			gradeScale.enable();
		}
	}

	@Override
	protected void onRefreshGradeScale(Gradebook gradebookModel) {
		if (gradeScalePanel != null) 
			gradeScalePanel.onRefreshGradeScale(gradebookModel);
	}

	@Override
	protected void onSelectLearner(ModelData learner) {
		if (singleGradeContainer != null && singleGradeContainer.isVisible()) {
			onSingleGrade(learner);
		}
	}

	@Override
	protected void onSingleGrade(final ModelData learnerGradeRecordCollection) {

		if (singleGradeContainer == null) {
			singleGradeContainer = new LearnerSummaryPanel();
			eastLayoutContainer.add(singleGradeContainer);
		}
		singleGradeContainer.onChangeModel(multigradeView.getStore(), treeView.getTreeStore(), learnerGradeRecordCollection);
		onExpandEastPanel(EastCard.LEARNER_SUMMARY);

	}

	@Override
	protected void onSingleView(ModelData learner) {
		viewport.add(singleGradeView.getDialog());
		viewportLayout.setActiveItem(singleGradeView.getDialog());
	}

	@Override
	protected void onShowGradeScale(Boolean show) {
		
		if (gradeScalePanel == null) {
			gradeScalePanel = new GradeScalePanel(isEditable, treeView);
			eastLayoutContainer.add(gradeScalePanel);
		}
		// GRBK-668
		gradeScalePanel.setState();
		onExpandEastPanel(EastCard.GRADE_SCALE);
	}

	@Override
	protected void onShowHistory(String identifier) {
		
		if (historyPanel == null) {
			historyPanel = new HistoryPanel(i18n);
			viewport.add(historyPanel);
		}
		viewportLayout.setActiveItem(historyPanel);
	}

	protected void onShowSetup() {
		Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);

		if (selectedGradebook != null) {
			Item itemModel = selectedGradebook.getGradebookItemModel();
			Dispatcher.forwardEvent(GradebookEvents.StartEditItem.getEventType(), itemModel);
		}
	}

	@Override
	protected void onShowStatistics() {
		if (statisticsPanel == null) {
			statisticsPanel = new StatisticsPanel(i18n);
			viewport.add(statisticsPanel);
		}
		statisticsPanel.onLearnerGradeRecordUpdated(null);
		viewportLayout.setActiveItem(statisticsPanel);
	}

	@Override
	protected void onStopStatistics() {
		viewportLayout.setActiveItem(borderLayoutContainer);
	}

	@Override
	protected void onStartEditItem(Item itemModel) {
		AppView.EastCard activeCard = AppView.EastCard.EDIT_ITEM;

		if (itemModel != null) {
			switch (itemModel.getItemType()) {
			case CATEGORY:
				activeCard = AppView.EastCard.EDIT_CATEGORY;
				break;
			case GRADEBOOK:
				activeCard = AppView.EastCard.EDIT_GRADEBOOK;
			}
		}
		onExpandEastPanel(activeCard);
	}

	@Override
	protected void onStartImport() {

	}

	@Override
	protected void onStopImport() {
		viewportLayout.setActiveItem(borderLayoutContainer);
	}

	@Override
	protected void onStartGraderPermissions() {
		PermissionsPanel permissionsPanel = permissionsView.getPermissionsPanelInstance();
		viewport.add(permissionsPanel);
		viewportLayout.setActiveItem(permissionsPanel);
	}

	@Override
	protected void onStopGraderPermissions() {
		viewportLayout.setActiveItem(borderLayoutContainer);
	}

	@Override
	protected void onHideEastPanel(Boolean doCommit) {
		borderLayout.hide(LayoutRegion.EAST);
	}

	protected void onHideFormPanel() {
		centerCardLayout.setActiveItem(multigradeView.getMultiGradeContentPanel());
		
	}

	@Override
	protected void onSwitchGradebook(Gradebook selectedGradebook) {

		if (addCategoryMenuItem != null)
			addCategoryMenuItem.setVisible(selectedGradebook.getGradebookItemModel().getCategoryType() != CategoryType.NO_CATEGORIES);


		Boolean isLetterGrade = (selectedGradebook.getGradebookItemModel().getGradeType() == GradeType.LETTERS);
		MenuItem gradeScale = (MenuItem) editMenu.getItemByItemId(AppConstants.WINDOW_MENU_ITEM_PREFIX  + AppConstants.TAB_GRADESCALE);
		if(isLetterGrade){
			gradeScale.disable();
		} else {
			gradeScale.enable();
		}
	}
	
	@Override
	protected void onGradeScaleUpdateError() {
		if (gradeScalePanel != null) {
			gradeScalePanel.onGradeScaleUpdateError();
		}
	}

	@Override
	protected void onShowFinalGradeSubmissionStatus() {
	
		Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
		checkFinalGradeSubmissionStatus(selectedGradebook.getGradebookUid(), false);
	}
	
	/*
	 * The goal here is to reduce the number of overall listeners in the application to a minimum
	 */
	private void initListeners() {

		menuEventListener = new Listener<MenuEvent>() {

			public void handleEvent(MenuEvent me) {

				if (me.getType().equals(Events.Select)) {
					MenuItem menuItem = (MenuItem)me.getItem();
					MenuSelector menuSelector = menuItem.getData(MENU_SELECTOR_FLAG);

					switch (menuSelector) {
					case GRADE_SCALE:
						onShowGradeScale(Boolean.TRUE);
						break;
					case HISTORY:
						onShowHistory(null);
						break;
					case GRADER_PERMISSION_SETTINGS:
						onStartGraderPermissions();
						break;
					case STATISTICS:
						onShowStatistics();
						break;
					case SETUP:
						onShowSetup();
						break;
					}
				}
			}

		};

		final Map<MenuSelector, ExportType> exportTypeByMenuSelector = new HashMap<MenuSelector, ExportType>();
		exportTypeByMenuSelector.put(MenuSelector.EXPORT_DATA_XLS, ExportType.XLS97);
		exportTypeByMenuSelector.put(MenuSelector.EXPORT_STRUCTURE_XLS, ExportType.XLS97);
		exportTypeByMenuSelector.put(MenuSelector.EXPORT_DATA_CSV, ExportType.CSV);
		exportTypeByMenuSelector.put(MenuSelector.EXPORT_STRUCTURE_CSV, ExportType.CSV);
		exportTypeByMenuSelector.put(MenuSelector.EXPORT_STRUCTURE_XLSX, ExportType.XLSX);
		exportTypeByMenuSelector.put(MenuSelector.EXPORT_DATA_XLSX, ExportType.XLSX);
		final EnumSet<MenuSelector> exportingStructureSelections = EnumSet.of(
				MenuSelector.EXPORT_STRUCTURE_XLS,MenuSelector.EXPORT_STRUCTURE_CSV,MenuSelector.EXPORT_STRUCTURE_XLSX);

		menuSelectionListener = new SelectionListener<MenuEvent>() {

			protected ExportDetails ex;

			@Override
			public void componentSelected(MenuEvent me) {
				MenuSelector selector = me.getItem().getData(MENU_SELECTOR_FLAG);
				ExportType exportType = exportTypeByMenuSelector.get(selector);
				boolean includeStructure = exportingStructureSelections.contains(selector);
				switch (selector) {
				case ADD_CATEGORY:
					Dispatcher.forwardEvent(GradebookEvents.NewCategory.getEventType());
					break;
				case ADD_ITEM:
					Dispatcher.forwardEvent(GradebookEvents.NewItem.getEventType());
					break;
				case EXPORT_DATA_XLS:
				case EXPORT_DATA_XLSX:
				case EXPORT_STRUCTURE_XLS:
				case EXPORT_STRUCTURE_XLSX:
				case EXPORT_DATA_CSV:
				case EXPORT_STRUCTURE_CSV:
					ex = getExportDetails();
					ex.setFileType(exportType);
					ex.setIncludeStructure(includeStructure);
					ex.setSectionUid(multigradeView.getMultiGradeContentPanel().getSelectedSectionUid());							
					//handleExport(ex);
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

		toolBarSelectionListener = new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent tbe) {

				String helpUrl = Registry.get(AppConstants.HELP_URL);
				Window.open(helpUrl, "_blank", "resizable=yes,scrollbars=yes,outerHeight=600,outerWidth=350");
			}
		};
	}

	protected ExportDetails getExportDetails() {
		

			WidgetInjector injector = Registry.get(AppConstants.WIDGET_INJECTOR);
			exportWizard = injector.getWizardProvider().get();


		exportDetails = new ExportDetails();
		
		exportWizard.setHeading(i18n.exportWizardHeading());
		exportWizard.setHeaderTitle(i18n.exportWizardTitle());
		exportWizard.setClosable(false);
		exportWizard.setShowWestImageContainer(false);
		exportWizard.setPanelBackgroundColor("#FFFFFF");
		exportWizard.setProgressIndicator(Wizard.Indicator.PROGRESSBAR);
		exportWizard.setFinishButtonText(i18n.headerExport());
		
		FormPanel formPanel = new FormPanel();
		formPanel.setLabelWidth(500);
		
		Card exportBooleanChoices = exportWizard.newCard(i18n.exportChoices());
		
		exportCommentsCheckbox = newCheckBox(INCLUDE_COMMENTS, i18n.exportIncludeComments(), i18n.exportIncludeComments());
		
		exportCommentsCheckbox.setValue(true);
		
		formPanel.setLayout(new FormLayout());
		formPanel.add(exportCommentsCheckbox);
		
		exportBooleanChoices.setFormPanel(formPanel);
		
		exportBooleanChoices.addFinishListener(new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				exportDetails.setIncludeComments(exportCommentsCheckbox.getValue());
				handleExport(exportDetails);
				
			}
			
			private void handleExport(ExportDetails ex) {
				
				
				// GRBK-804 we'll now not popup as we can import percentages
				Dispatcher.forwardEvent(GradebookEvents.StartExport.getEventType(), ex);
			}
			
		});
		
		exportWizard.show();
		
		exportWizard.resize(-60, -100);
		
		return exportDetails;
	}

	/*
	 * Create a top-level toolbar with menu drop downs
	 */
	private ToolBar populateToolBar(I18nConstants i18n) {

		if (isEditable) {
			AriaButton fileItem = new AriaButton(i18n.newMenuHeader());
			fileItem.setMenu(newFileMenu(i18n));
			toolBar.add(fileItem);
		}

		if (isEditable) {
			AriaButton editItem = new AriaButton(i18n.editMenuHeader());
			editMenu = newEditMenu(i18n);
			editItem.setMenu(editMenu);
			toolBar.add(editItem);
		}

		AriaButton viewItem = new AriaButton(i18n.viewMenuHeader());
		viewMenu = newViewMenu(i18n);
		viewItem.setMenu(viewMenu);

		AriaButton moreItem = new AriaButton(i18n.moreMenuHeader());
		moreItem.setMenu(newMoreActionsMenu());

		AriaButton helpItem = new AriaButton(i18n.helpMenuHeader());
		helpItem.addSelectionListener(toolBarSelectionListener);

		toolBar.add(viewItem);
		toolBar.add(moreItem);
		toolBar.add(helpItem);

		// GRBK-824
		finalGradeSubmissionStatusBanner = new LabelField();
		finalGradeSubmissionStatusBanner.setStyleName(resources.css().gbFinalGradeSubissionStatus());
		finalGradeSubmissionStatusBanner.hide();
		toolBar.add(finalGradeSubmissionStatusBanner);
		
		toolBar.add(new FillToolItem());

		String version = Registry.get(AppConstants.VERSION);
		LabelField versionLabel = new LabelField(version);
		toolBar.add(versionLabel);

		return toolBar;
	}

	/*
	 * Create a new file menu 
	 */
	private Menu newFileMenu(I18nConstants i18n) {

		// This should be obvious. Just create the required menu object and its menu items
		fileMenu = new AriaMenu();
		addCategoryMenuItem = new AriaMenuItem(i18n.fileMenuNewCategory(), menuSelectionListener);
		addCategoryMenuItem.setData(MENU_SELECTOR_FLAG, MenuSelector.ADD_CATEGORY);
		addCategoryMenuItem.setIcon(AbstractImagePrototype.create(resources.folder_add()));
		addCategoryMenuItem.setId(AppConstants.ID_ADD_CATEGORY_MENUITEM);
		MenuItem addItem = new AriaMenuItem(i18n.fileMenuNewItem(), menuSelectionListener);
		addItem.setData(MENU_SELECTOR_FLAG, MenuSelector.ADD_ITEM);
		addItem.setIcon(AbstractImagePrototype.create(resources.table_add()));
		addItem.setId(AppConstants.ID_ADD_ITEM_MENUITEM);

		// Attach the items to the menu
		fileMenu.add(addCategoryMenuItem);
		fileMenu.add(addItem);


		return fileMenu;
	}

	private Menu newViewMenu(I18nConstants i18n) 
	{
		Menu viewMenu = new AriaMenu();

		for (TabConfig tabConfig : tabConfigurations) {
			if (tabConfig.id.equals(AppConstants.TAB_HISTORY) || 
					tabConfig.id.equals(AppConstants.TAB_STATISTICS) )
			{
				MenuItem tabBasedMenuItem = newTabBasedMenuItem(tabConfig);
				viewMenu.add(tabBasedMenuItem);
			}
		}	
		return viewMenu;
	}
	private Menu newEditMenu(I18nConstants i18n) {
		Menu editMenu = new AriaMenu();

		for (TabConfig tabConfig : tabConfigurations) {
			if (tabConfig.id.equals(AppConstants.TAB_SETUP) || 
					tabConfig.id.equals(AppConstants.TAB_GRADER_PER_SET) || 
					tabConfig.id.equals(AppConstants.TAB_GRADESCALE))
			{
				MenuItem tabBasedMenuItem = newTabBasedMenuItem(tabConfig);
				editMenu.add(tabBasedMenuItem);
			}
		}

		return editMenu;
	}

	private MenuItem newTabBasedMenuItem(TabConfig tabConfig) {
		String id = new StringBuilder().append(AppConstants.WINDOW_MENU_ITEM_PREFIX).append(tabConfig.id).toString();
		MenuItem menuItem = new AriaMenuItem();
		menuItem.setText(tabConfig.menuHeader);
		menuItem.setData(MENU_SELECTOR_FLAG, tabConfig.menuSelector);
		menuItem.setEnabled(tabConfig.isClosable);
		menuItem.setId(id);
		menuItem.setIcon(AbstractImagePrototype.create(tabConfig.icon));
		tabConfig.menuItemId = id;

		menuItem.addListener(Events.Select, menuEventListener);

		return menuItem;
	}


	private Menu newMoreActionsMenu() {
		Menu moreActionsMenu = new AriaMenu();

		MenuItem menuItem = new AriaMenuItem(i18n.headerExport());
		menuItem.setData(MENU_SELECTOR_FLAG, MenuSelector.EXPORT);
		//menuItem.setIconStyle(resources.css().gbExportItemIcon());
		menuItem.setIcon(AbstractImagePrototype.create(resources.page_white_put()));
		menuItem.setTitle(i18n.headerExportTitle());
		moreActionsMenu.add(menuItem);

		Menu subMenu = new AriaMenu();
		menuItem.setSubMenu(subMenu);

		Menu typeMenu = subMenu; 

		// If we're dealing with an "editable" instance of the tool, then make the appropriate submenus for export
		if (isEditable) {
			menuItem = new AriaMenuItem(i18n.headerExportData());
			menuItem.setData(MENU_SELECTOR_FLAG, MenuSelector.EXPORT_DATA);
			menuItem.setTitle(i18n.headerExportDataTitle());
			subMenu.add(menuItem);

			typeMenu = new AriaMenu();
			menuItem.setSubMenu(typeMenu);
		}

		menuItem = new AriaMenuItem(i18n.headerExportCSV(), menuSelectionListener);
		menuItem.setData(MENU_SELECTOR_FLAG, MenuSelector.EXPORT_DATA_CSV);
		menuItem.setTitle(i18n.headerExportCSVTitle());
		typeMenu.add(menuItem);

		menuItem = new AriaMenuItem(i18n.headerExportXLS(), menuSelectionListener);
		menuItem.setData(MENU_SELECTOR_FLAG, MenuSelector.EXPORT_DATA_XLS);
		menuItem.setTitle(i18n.headerExportXLSTitle());
		typeMenu.add(menuItem);
		
		menuItem = new AriaMenuItem(i18n.headerExportXLSX(), menuSelectionListener);
		menuItem.setData(MENU_SELECTOR_FLAG, MenuSelector.EXPORT_DATA_XLSX);
		menuItem.setTitle(i18n.headerExportXLSXTitle());
		typeMenu.add(menuItem);

		// If we're dealing with an "editable" instance of the tool, show the other editing menu items
		if (isEditable) {
			menuItem = new AriaMenuItem(i18n.headerExportStructure());
			menuItem.setData(MENU_SELECTOR_FLAG, MenuSelector.EXPORT_STRUCTURE);
			menuItem.setTitle(i18n.headerExportStructureTitle());
			subMenu.add(menuItem);

			typeMenu = new AriaMenu();
			menuItem.setSubMenu(typeMenu);

			menuItem = new AriaMenuItem(i18n.headerExportCSV(), menuSelectionListener);
			menuItem.setData(MENU_SELECTOR_FLAG, MenuSelector.EXPORT_STRUCTURE_CSV);
			menuItem.setTitle(i18n.headerExportCSVTitle());
			typeMenu.add(menuItem);

			menuItem = new AriaMenuItem(i18n.headerExportXLS(), menuSelectionListener);
			menuItem.setData(MENU_SELECTOR_FLAG, MenuSelector.EXPORT_STRUCTURE_XLS);
			menuItem.setTitle(i18n.headerExportXLSTitle());
			typeMenu.add(menuItem);
			
			menuItem = new AriaMenuItem(i18n.headerExportXLSX(), menuSelectionListener);
			menuItem.setData(MENU_SELECTOR_FLAG, MenuSelector.EXPORT_STRUCTURE_XLSX);
			menuItem.setTitle(i18n.headerExportXLSXTitle());
			typeMenu.add(menuItem);

			menuItem = new AriaMenuItem(i18n.headerImport(), menuSelectionListener);
			menuItem.setData(MENU_SELECTOR_FLAG, MenuSelector.IMPORT);
			menuItem.setIcon(AbstractImagePrototype.create(resources.page_white_get()));
			menuItem.setTitle(i18n.headerImportTitle());
			moreActionsMenu.add(menuItem);

			// GRBK-37 : TPA
			menuItem = new AriaMenuItem(i18n.headerFinalGrade(), menuSelectionListener);
			menuItem.setData(MENU_SELECTOR_FLAG, MenuSelector.FINAL_GRADE);
			menuItem.setIcon(AbstractImagePrototype.create(resources.page_white_put()));
			menuItem.setTitle(i18n.headerFinalGradeTitle());
			moreActionsMenu.add(menuItem);
		}

		return moreActionsMenu;
	}
	
	protected CheckBox newCheckBox(String name, String label, String tooltip) {
		CheckBox checkbox = null;
		
		checkbox = new NullSensitiveCheckBox();

		checkbox.setName(name);
		checkbox.setFieldLabel(label);
		checkbox.setAutoHeight(false);
		checkbox.setAutoWidth(false);
		checkbox.setVisible(true);
		checkbox.setToolTip(newToolTipConfig(tooltip));
		checkbox.setReadOnly(false);

		return checkbox;
	}
	
	protected ToolTipConfig newToolTipConfig(String text) {
		ToolTipConfig ttc = new ToolTipConfig(text);
		ttc.setDismissDelay(10000);
		return ttc;
	}
	
	/*
	 * GRBK-824
	 * Make a REST call that checks if the final grades have been submitted for 
	 * the current gradebook
	 */
	private void checkFinalGradeSubmissionStatus(String gradebookUid, final boolean showDialog) {
		
		if(null == gradebookUid) {
		
			Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(i18n.finalGradeSubmissionStatusDialogTitle(), i18n.finalGradeSubmissionStatusErrorMessage()));
			return;
		}
		
		RestBuilder builder = RestBuilder.getInstance(RestBuilder.Method.GET, 
				GWT.getModuleBaseURL(),
				AppConstants.REST_FRAGMENT,
				AppConstants.STARTUP_FRAGMENT,
				AppConstants.FINAL_GRADES_SUB_FRAGMENT,
				gradebookUid);

		builder.sendRequest(200, 400, null, new RestCallback() {

			public void onError(Request request, Throwable exception) {
				
				Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(i18n.finalGradeSubmissionStatusDialogTitle(), i18n.finalGradeSubmissionStatusErrorMessage()));
			}

			public void onFailure(Request request, Throwable exception) {
				
				Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(i18n.finalGradeSubmissionStatusDialogTitle(), i18n.finalGradeSubmissionStatusErrorMessage()));
			}

			public void onSuccess(Request request, Response response) {
				
				EntityOverlay overlay = JsonUtil.toOverlay(response.getText());

				FinalGradeSubmissionStatus finalGradeSubmissionStatus = new FinalGradeSubmissionStatusModel(overlay);
				
				String dialogMessage = finalGradeSubmissionStatus.getDialogNotificationMessage();
				String bannerMessage = finalGradeSubmissionStatus.getBannerNotificationMessage();
				
				if(null != dialogMessage && !"".equals(dialogMessage) && null != bannerMessage && !"".equals(bannerMessage)) {

					// Banner
					finalGradeSubmissionStatusBanner.setText(bannerMessage);
					finalGradeSubmissionStatusBanner.show();

					if(showDialog) {

						// Dialog
						Dialog dialog = new Dialog();
						dialog.setHeading(i18n.finalGradeSubmissionStatusDialogTitle());
						dialog.setButtons(Dialog.OK);
						dialog.addText(dialogMessage);
						dialog.setButtonAlign(HorizontalAlignment.CENTER);
						dialog.setBodyStyleName(resources.css().gbFinalGradeSubissionStatusDialog());
						dialog.setMinWidth(450);					
						dialog.setModal(true);
						dialog.setHideOnButtonClick(true);  
						dialog.show();
					}
				}
			}
		});
	}
}
