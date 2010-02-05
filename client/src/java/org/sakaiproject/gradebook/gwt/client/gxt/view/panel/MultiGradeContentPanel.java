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
package org.sakaiproject.gradebook.gwt.client.gxt.view.panel;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.RestBuilder;
import org.sakaiproject.gradebook.gwt.client.RestBuilder.Method;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.gxt.GbCellEditor;
import org.sakaiproject.gradebook.gwt.client.gxt.GridPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.ItemModelProcessor;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaButton;
import org.sakaiproject.gradebook.gwt.client.gxt.custom.widget.grid.CustomColumnModel;
import org.sakaiproject.gradebook.gwt.client.gxt.custom.widget.grid.CustomGridView;
import org.sakaiproject.gradebook.gwt.client.gxt.event.BrowseLearner;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradeRecordUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.RefreshCourseGradesEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ShowColumnsEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.event.UserChangeEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ConfigurationModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.ExtraCreditNumericCellRenderer;
import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.MultiGradeContextMenu;
import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.MultiGradeLoadConfig;
import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.MultigradeSelectionModel;
import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.StudentModelOwner;
import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.UnweightedNumericCellRenderer;
import org.sakaiproject.gradebook.gwt.client.model.Configuration;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumn;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;
import org.sakaiproject.gradebook.gwt.client.model.key.SectionKey;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.EntityType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;
import org.sakaiproject.gradebook.gwt.client.resource.GradebookResources;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.tips.ToolTip;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;

public class MultiGradeContentPanel extends GridPanel<ModelData> implements StudentModelOwner {

	private enum PageOverflow { TOP, BOTTOM, NONE };

	private ToolBar searchToolBar;
	private LayoutContainer toolBarContainer;
	private Long commentingAssignmentId; 
	private ModelData commentingStudentModel;

	private GridCellRenderer<ModelData> unweightedNumericCellRenderer;
	private GridCellRenderer<ModelData> extraCreditNumericCellRenderer;

	private int currentIndex = -1;

	private MultiGradeContextMenu contextMenu;

	private CheckBox useClassicNavigationCheckBox;
	private LabelField modeLabel;
	private TextField<String> searchField;
	private NumberField pageSizeField;

	private Listener<ComponentEvent> componentEventListener;
	private Listener<GridEvent<ModelData>> gridEventListener;
	private Listener<RefreshCourseGradesEvent> refreshCourseGradesListener;
	private Listener<UserChangeEvent> userChangeEventListener;

	private MultigradeSelectionModel<ModelData> cellSelectionModel;
	private ListLoader<ListLoadResult<ModelData>> sectionsLoader;


	public MultiGradeContentPanel(ContentPanel childPanel) {
		super(AppConstants.MULTIGRADE, EntityType.LEARNER, childPanel);
		setHeaderVisible(false);

		// This UserChangeEvent listener
		addListener(GradebookEvents.UserChange.getEventType(), userChangeEventListener);
		addListener(GradebookEvents.RefreshCourseGrades.getEventType(), refreshCourseGradesListener);
	}

	public void deselectAll() {
		cellSelectionModel.deselectAll();
	}

	@Override
	public void editCell(Gradebook selectedGradebook, Record record, String property, Object value, Object startValue, GridEvent gridEvent) {

		String columnHeader = "";
		if (gridEvent != null) {
			String className = grid.getView().getCell(gridEvent.getRowIndex(), gridEvent.getColIndex()).getClassName();
			String gbDroppedText = new StringBuilder(" ").append(resources.css().gbCellDropped()).toString();
			className = className.replace(gbDroppedText, "");
			grid.getView().getCell(gridEvent.getRowIndex(), gridEvent.getColIndex()).setClassName(className);
			grid.getView().getCell(gridEvent.getRowIndex(), gridEvent.getColIndex()).setInnerText("Saving...");

			columnHeader = grid.getColumnModel().getColumnHeader(gridEvent.getColIndex());
		}

		Dispatcher.forwardEvent(GradebookEvents.UpdateLearnerGradeRecord.getEventType(), new GradeRecordUpdate(record, property, columnHeader, startValue, value));
	}

	public ModelData getSelectedModel() {
		return commentingStudentModel;
	}

	public Long getSelectedAssignment() {
		return commentingAssignmentId;
	}

	public void onBeginItemUpdates() {
		refreshAction = RefreshAction.NONE;
	}

	/*
	 * When the user clicks on the next or previous buttons in the student view dialog, an event is thrown to the dispatcher and this 
	 * method is eventually called. It must decide whether the next learner in the grid is on the current page or not, and choose whether 
	 * to change pages before throwing the event that notifies the dialog box to show a new learner
	 */
	public void onBrowseLearner(BrowseLearner be) {
		ModelData current = be.learner;
		currentIndex = grid.getStore().indexOf(current);

		int pageSize = getPageSize();

		PageOverflow pageOverflow = PageOverflow.NONE;
		// Do processing for paging -- if we reach the end or beginning of a page
		switch (be.type) {
			case PREV:
				currentIndex--;
				if (currentIndex < 0)
					pageOverflow = PageOverflow.TOP;
				break;
			case NEXT:
				currentIndex++;
				if (currentIndex >= pageSize || grid.getStore().getAt(currentIndex) == null) 
					pageOverflow = PageOverflow.BOTTOM;
				break;
			case CURRENT:
				break;
		}

		boolean requiresPageChange = pageOverflow != PageOverflow.NONE;

		int activePage = pagingToolBar.getActivePage();
		int numberOfPages = pagingToolBar.getTotalPages();

		switch (pageOverflow) {
			case TOP:
				// Go to the last record on the page
				currentIndex = pageSize - 1;
				// And we are on the first page, then go to the last page
				if (activePage == 1)
					pagingToolBar.last();
				// Otherwise, go to the one before
				else
					pagingToolBar.previous();
				break;
			case BOTTOM:
				currentIndex = 0;
				// And if we are on the last page
				if (activePage == numberOfPages) {
					pagingToolBar.first();
				} else {
					pagingToolBar.next();
				}
				break;
		}

		// Any of these cases indicate that we may have gone off the page
		// (1) a negative index
		// (2) an index greater than the page size
		// (3) a null item in the store, in particular on the last page when there are fewer populated rows than the page size allows
		if (requiresPageChange) {
			grid.getStore().addListener(Store.DataChanged, new Listener<StoreEvent<ModelData>>() {

				public void handleEvent(StoreEvent<ModelData> se) {
					ModelData selectedLearner = null;
					while (selectedLearner == null && currentIndex >= 0) {
						selectedLearner = ((ListStore<ModelData>)se.getStore()).getAt(currentIndex);
						if (selectedLearner != null) {

						} else {
							currentIndex--;
						}
					}
					grid.getStore().removeListener(Store.DataChanged, this);

					if (selectedLearner != null)
						cellSelectionModel.select(currentIndex, false);

				}

			});
		} else {
			ModelData selectedLearner = grid.getStore().getAt(currentIndex);

			if (selectedLearner != null)
				cellSelectionModel.select(currentIndex, false);
		}
	}

	public void onEditMode(Boolean enable) {

	}

	public void onEndItemUpdates() {
		doRefresh(true);
	}

	public void onLearnerGradeRecordUpdated(UserEntityAction<?> action) { 


	}

	public void onItemCreated(ItemModel itemModel) {
		Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);

		ItemModel gradebookModel = (ItemModel)selectedGradebook.getGradebookItemModel();

		if (gradebookModel.equals(itemModel.getParent())) {
			gradebookModel.getChildren().add(itemModel);
		} else {
			for (ModelData category : gradebookModel.getChildren()) {
				if (category.equals(itemModel.getParent()))
					((BaseTreeModel) category).getChildren().add(itemModel);
			}
		}

		onRefreshGradebookItems(selectedGradebook);
	}

	public void onItemDeleted(Item itemModel) {
		Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);

		ItemModel gradebookModel = (ItemModel)selectedGradebook.getGradebookItemModel();

		if (gradebookModel.getChildren().contains(itemModel)) {
			gradebookModel.getChildren().remove(itemModel);
		} else {
			for (ModelData category : gradebookModel.getChildren()) {
				if (((BaseTreeModel) category).getChildren().contains(itemModel))
					((BaseTreeModel) category).getChildren().remove(itemModel);
			}
		}

		onRefreshGradebookItems(selectedGradebook);
	}

	public void onItemUpdated(Item itemModel) {

		Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
		final GradeType gradeType = selectedGradebook.getGradebookItemModel().getGradeType();

		ItemModelProcessor processor = new ItemModelProcessor(itemModel) {

			@Override
			public void doCategory(Item categoryModel) {
				if (categoryModel.isActive()) 
					queueDeferredRefresh(RefreshAction.REFRESHDATA);
			}

			@Override
			public void doItem(Item itemModel) {
				if (itemModel.isActive()) 
					queueDeferredRefresh(RefreshAction.REFRESHDATA);

				ColumnConfig column = cm.getColumnById(itemModel.getIdentifier());

				if (column != null) {
					if (itemModel.getName() != null) {
						StringBuilder name = new StringBuilder();
						name.append(itemModel.getName());
						switch (gradeType) {
							case POINTS:
								name.append(" [").append(itemModel.getPoints()).append("]");
								break;
							case PERCENTAGES:
								name.append(" [%]");
						}
						column.setHeader(name.toString());
						queueDeferredRefresh(RefreshAction.REFRESHLOCALCOLUMNS);
					}

					boolean isIncluded = itemModel.getIncluded() != null && itemModel.getIncluded().booleanValue();
					boolean isExtraCredit = itemModel.getExtraCredit() != null && itemModel.getExtraCredit().booleanValue();

					if (!isIncluded)
						column.setRenderer(unweightedNumericCellRenderer);
					else if (isExtraCredit)
						column.setRenderer(extraCreditNumericCellRenderer);
					else
						column.setRenderer(null);
				}	
			}

		};

		processor.process();
	}


	protected void initListeners() {

		componentEventListener = new Listener<ComponentEvent>() {

			public void handleEvent(ComponentEvent ce) {


				// FIXME: This could be condensed significantly
				if (ce.getType() == GradebookEvents.DoSearch.getEventType()) {
					int pageSize = getPageSize();
					String searchString = searchField.getValue();
					String sectionUuid = null;	
					if (loadConfig != null)
						sectionUuid = ((MultiGradeLoadConfig) loadConfig).getSectionUuid();
					loadConfig = new MultiGradeLoadConfig();
					loadConfig.setLimit(0);
					loadConfig.setOffset(pageSize);
					((MultiGradeLoadConfig) loadConfig).setSearchString(searchString);
					((MultiGradeLoadConfig) loadConfig).setSectionUuid(sectionUuid);
					((BasePagingLoader)loader).useLoadConfig(loadConfig);
					loader.load(0, pageSize);
				} else if (ce.getType() == GradebookEvents.ClearSearch.getEventType()) {
					int pageSize = getPageSize();
					searchField.setValue(null);
					String sectionUuid = null;
					if (loadConfig != null)
						sectionUuid = ((MultiGradeLoadConfig) loadConfig).getSectionUuid();
					loadConfig = new MultiGradeLoadConfig();
					loadConfig.setLimit(0);
					loadConfig.setOffset(pageSize);
					((MultiGradeLoadConfig) loadConfig).setSectionUuid(sectionUuid);
					((BasePagingLoader)loader).useLoadConfig(loadConfig);
					loader.load(0, pageSize);
				}
			}

		};

		gridEventListener = new Listener<GridEvent<ModelData>>() {

			public void handleEvent(GridEvent<ModelData> ge) {

				if (ge.getType().equals(Events.CellClick)) {
					if (ge.getColIndex() == 0 || ge.getColIndex() == 1 || ge.getColIndex() == 2) {
						ModelData selectedLearner = store.getAt(ge.getRowIndex());
						Dispatcher.forwardEvent(GradebookEvents.SingleGrade.getEventType(), selectedLearner);
						ge.getGrid().getSelectionModel().select(ge.getRowIndex(), false);
					}
				} else if (ge.getType().equals(Events.ContextMenu)) {
					if (ge.getRowIndex() >= 0 && ge.getColIndex() >= 0) {
						ColumnConfig c = grid.getColumnModel().getColumn(
								ge.getColIndex());
						String assignIdStr = c.getId();
						long assignId;

						try {
							assignId = Long.parseLong(assignIdStr);
						} catch (NumberFormatException e) {
							ge.stopEvent();
							return;
						}
						commentingStudentModel = store.getAt(ge.getRowIndex());
						commentingAssignmentId = Long.valueOf(assignId);

						String commentedKey = DataTypeConversionUtil.buildCommentKey(commentingAssignmentId.toString());
						
						Boolean commentFlag = (Boolean) commentingStudentModel
								.get(commentedKey);

						boolean isCommented = commentFlag != null
								&& commentFlag.booleanValue();

						if (isCommented) {
							contextMenu.enableAddComment(false);
							contextMenu.enableEditComment(true);
						} else {
							contextMenu.enableAddComment(true);
							contextMenu.enableEditComment(false);
						}

						boolean isGraded = true; // gradedFlag != null &&
													// gradedFlag.booleanValue();

						contextMenu.enableViewGradeHistory(isGraded);
					} else
						ge.stopEvent();
				}
			}
		};

		refreshCourseGradesListener = new Listener<RefreshCourseGradesEvent>() {

			public void handleEvent(RefreshCourseGradesEvent rcge) {
				// These events are fired from the setup screens or the student view dialog/container
				// so they can be triggered by onShow rather than via an immediate refresh.

				queueDeferredRefresh(RefreshAction.REFRESHDATA);
			}

		};
	}

	public void onShowColumns(ShowColumnsEvent event) {
		showColumns(event, cm);
	}

	public void onSwitchGradebook(Gradebook selectedGradebook) {

		Configuration configModel = selectedGradebook.getConfigurationModel();
		
		if (store != null) {
			// Set the default sort field and direction on the store based on Cookies
			String storedSortField = configModel.getSortField(gridId);
			boolean isAscending = configModel.isAscending(gridId);

			SortDir sortDir = isAscending ? SortDir.ASC : SortDir.DESC;

			if (storedSortField != null) 
				store.setDefaultSort(storedSortField, sortDir);
		}
		
		Boolean useClassicNavigation = Boolean.valueOf(configModel.isClassicNavigation());
		useClassicNavigationCheckBox.setValue(useClassicNavigation);
		
		int pageSize = configModel.getPageSize(gridId);
		
		if (pageSize == -1)
			pageSize = DEFAULT_PAGE_SIZE;
		
		pagingToolBar.setPageSize(pageSize);

		onRefreshGradebookSetup(selectedGradebook);
		reconfigureGrid(newColumnModel(selectedGradebook));

		if (loader != null) 
			loader.load(0, pageSize);
		pageSizeField.setValue(Integer.valueOf(pageSize));

	}

	public void onUserChange(UserEntityAction<?> action) {

	}

	protected CustomColumnModel newColumnModel(Gradebook selectedGradebook) {
		CustomColumnModel columnModel = assembleColumnModel(selectedGradebook);
		return columnModel;
	}

	@Override
	protected GridView newGridView() {
		// SAK-2378
		CustomGridView view = new CustomGridView(gridId) {

			private Timer showTimer;
			private com.google.gwt.dom.client.Element overCell;
			private ToolTip toolTip;

			protected void init(Grid grid) { 
				super.init(grid);
			}

			protected boolean isClickable(ModelData model, String property) {
				return property.equals(LearnerKey.DISPLAY_NAME.name()) ||
				property.equals(LearnerKey.LAST_NAME_FIRST.name()) ||
				property.equals(LearnerKey.DISPLAY_ID.name());
			}

			protected boolean isCommented(ModelData model, String property) {
				String commentedProperty = DataTypeConversionUtil.buildCommentKey(property);
				Boolean isCommented = model.get(commentedProperty);

				return isCommented != null && isCommented.booleanValue();
			}

			protected boolean isDropped(ModelData model, String property) {
				String droppedProperty = DataTypeConversionUtil.buildDroppedKey(property);
				Boolean isDropped = model.get(droppedProperty);

				return isDropped != null && isDropped.booleanValue();
			}

			protected boolean isReleased(ModelData model, String property) {
				return false;
			}

			@Override
			protected String markupCss(Record r, ModelData model, String property, boolean isShowDirtyCells, boolean isPropertyChanged) {
				StringBuilder css = new StringBuilder();

				if (isShowDirtyCells && isPropertyChanged) {

					Object startValue = r.getChanges().get(property);
					Object currentValue = r.get(property);

					String failedProperty = new StringBuilder().append(property).append(GridPanel.FAILED_FLAG).toString();
					String failedMessage = (String)r.get(failedProperty);

					if (failedMessage != null) {
						css.append(" ").append(resources.css().gbCellFailed());
					} else if (startValue == null || !startValue.equals(currentValue)) {
						css.append(" ").append(resources.css().gbCellSucceeded());
					}
				}

				if (isDropped(model, property)) {
					css.append(" ").append(resources.css().gbCellDropped());
				}

				if (isReleased(model, property)) {
					css.append(" ").append(resources.css().gbReleased());
				}

				if (css.length() > 0)
					return css.toString();

				return null;
			}

			@Override
			protected String markupInnerCss(ModelData model, String property, boolean isShowDirtyCells, boolean isPropertyChanged) {

				GradebookResources resources = Registry.get(AppConstants.RESOURCES);
				StringBuilder innerCssClass = new StringBuilder();

				if (isCommented(model, property)) {
					innerCssClass.append(" ").append(resources.css().gbCellCommented());
				} 

				if (isClickable(model, property)) {
					innerCssClass.append(" ").append(resources.css().gbCellClickable());
				}

				if (innerCssClass.length() > 0)
					return innerCssClass.toString();

				return null;
			}
		};
		view.setEmptyText("-");

		return view;
	}

	@Override
	protected Menu newContextMenu() {
		contextMenu = new MultiGradeContextMenu(this);
		return contextMenu;
	}

	@Override
	protected PagingLoadConfig newLoadConfig(ListStore<ModelData> store, int pageSize) {
		SortInfo sortInfo = store.getSortState();
		MultiGradeLoadConfig loadConfig = new MultiGradeLoadConfig();
		loadConfig.setLimit(0);
		loadConfig.setOffset(pageSize);	
		if (sortInfo == null)
			sortInfo = new SortInfo(LearnerKey.LAST_NAME_FIRST.name(), SortDir.ASC);

		loadConfig.setSortInfo(sortInfo);

		return loadConfig;
	}

	@Override
	protected void addComponents() {

		// We only need to do this once
		if (rendered)
			return;

		unweightedNumericCellRenderer = new UnweightedNumericCellRenderer();
		extraCreditNumericCellRenderer = new ExtraCreditNumericCellRenderer();

		sectionsLoader = RestBuilder.getDelayLoader(AppConstants.LIST_ROOT, EnumSet.allOf(SectionKey.class), Method.GET, 
				GWT.getModuleBaseURL(), AppConstants.REST_FRAGMENT, AppConstants.SECTION_FRAGMENT);
			
		sectionsLoader.setRemoteSort(true);

		ListStore<ModelData> sectionStore = new ListStore<ModelData>(sectionsLoader);
		sectionStore.setModelComparer(new EntityModelComparer<ModelData>(SectionKey.ID.name()));

		ComboBox<ModelData> sectionListBox = new ComboBox<ModelData>(); 
		//sectionListBox.setAllQuery(null);
		sectionListBox.setEditable(false);
		sectionListBox.setFieldLabel("Sections");
		sectionListBox.setDisplayField(SectionKey.SECTION_NAME.name());  
		sectionListBox.setStore(sectionStore);
		sectionListBox.setForceSelection(true);
		sectionListBox.setEmptyText(i18n.sectionEmptyText());
		sectionListBox.setTriggerAction(ComboBox.TriggerAction.ALL);

		sectionListBox.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				ModelData model = se.getSelectedItem();

				String searchString = null;
				String sectionUuid = null;

				if (loadConfig != null)
					searchString = ((MultiGradeLoadConfig) loadConfig).getSearchString();
				if (model != null) 
					sectionUuid = model.get(SectionKey.ID.name());

				int pageSize = getPageSize();
				loadConfig = new MultiGradeLoadConfig();
				loadConfig.setLimit(0);
				loadConfig.setOffset(pageSize);				
				((MultiGradeLoadConfig) loadConfig).setSearchString(searchString);
				((MultiGradeLoadConfig) loadConfig).setSectionUuid(sectionUuid);
				((BasePagingLoader)loader).useLoadConfig(loadConfig);
				loader.load(0, pageSize);
			}

		});

		
		searchField = new TextField<String>();
		searchField.setEmptyText(i18n.searchLearnerEmptyText());
		searchField.setWidth(180);
		searchField.addKeyListener(new KeyListener() {
			public void componentKeyPress(ComponentEvent event) {
				switch (event.getKeyCode()) {
					case KeyCodes.KEY_ENTER:
						fireEvent(GradebookEvents.DoSearch.getEventType(), event);
						break;
				}
			}
		});

		addListener(GradebookEvents.DoSearch.getEventType(), componentEventListener);

		addListener(GradebookEvents.ClearSearch.getEventType(), componentEventListener);

		
		AriaButton doSearchItem = new AriaButton(i18n.findButton(), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				fireEvent(GradebookEvents.DoSearch.getEventType(), ce);
			}

		});

		doSearchItem.setToolTip("Search for all students with name matching the entered text");


		AriaButton clearSearchItem = new AriaButton(i18n.clearButton(), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				fireEvent(GradebookEvents.ClearSearch.getEventType(), ce);
			}

		});

		useClassicNavigationCheckBox = new CheckBox();
		useClassicNavigationCheckBox.setBoxLabel(i18n.useClassicNavigation());
		useClassicNavigationCheckBox.addListener(Events.Change, new Listener<FieldEvent>() {

			public void handleEvent(FieldEvent be) {
				Boolean isChecked = (Boolean)be.getValue();	
				cellSelectionModel.setUseClassic(isChecked != null && isChecked.booleanValue());
				
				Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
				Configuration model = new ConfigurationModel(selectedGradebook.getGradebookId());
				model.setClassicNavigation(isChecked);
				Dispatcher.forwardEvent(GradebookEvents.Configuration.getEventType(), model);
			}
			
		});
		
		modeLabel = new LabelField();
		
		int pageSize = getPageSize();
		pageSizeField = new NumberField();
		pageSizeField.setValue(Integer.valueOf(pageSize));
		pageSizeField.setWidth(35);
		pageSizeField.addKeyListener(new KeyListener() {
			public void componentKeyPress(ComponentEvent event) {
				switch (event.getKeyCode()) {
					case KeyCodes.KEY_ENTER:
						Number pageSize = pageSizeField.getValue();

						if (pageSize != null && pageSize.intValue() > 0 && pageSize.intValue() <= Integer.MAX_VALUE) {
							Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
							Configuration model = new ConfigurationModel(selectedGradebook.getGradebookId());
							model.setPageSize(gridId, Integer.valueOf(pageSize.intValue()));

							Dispatcher.forwardEvent(GradebookEvents.Configuration.getEventType(), model);
							
							newLoader().setLimit(pageSize.intValue());
							pagingToolBar.setPageSize(pageSize.intValue());
							pagingToolBar.refresh();
						}
						break;
				}
			}
		});

		searchToolBar = new ToolBar();
		searchToolBar.add(searchField);
		searchToolBar.add(doSearchItem);
		searchToolBar.add(clearSearchItem);
		searchToolBar.add(new SeparatorToolItem());
		searchToolBar.add(sectionListBox);
		searchToolBar.add(new SeparatorToolItem());
		searchToolBar.add(useClassicNavigationCheckBox);
		searchToolBar.add(new FillToolItem());
		searchToolBar.add(modeLabel);


		pagingToolBar.add(new SeparatorToolItem());
		pagingToolBar.add(new LabelField(new StringBuilder().append(i18n.pageSizeLabel()).append(" ").toString()));
		pagingToolBar.add(pageSizeField);

		toolBarContainer = new LayoutContainer();
		toolBarContainer.setLayout(new RowLayout());
		toolBarContainer.add(pagingToolBar, new RowData(1, 1));
		toolBarContainer.add(searchToolBar, new RowData(1, -1));

		setTopComponent(searchToolBar);
		setBottomComponent(pagingToolBar);
	}

	@Override
	protected void addGridListenersAndPlugins(final EditorGrid<ModelData> grid) {

		// We only need to do this once
		if (rendered)
			return;

		grid.addListener(Events.CellClick, gridEventListener);
		grid.addListener(Events.ContextMenu, gridEventListener);

		cellSelectionModel = new MultigradeSelectionModel<ModelData>();
		cellSelectionModel.setSelectionMode(SelectionMode.SINGLE);
		cellSelectionModel.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> sce) {
				ModelData learner = sce.getSelectedItem();

				if (learner != null) 
					Dispatcher.forwardEvent(GradebookEvents.SelectLearner.getEventType(), learner);
			}

		});
		grid.setSelectionModel(cellSelectionModel);
	}

	public void onRefreshCourseGrades() {
		RefreshAction actualAction = RefreshAction.REFRESHDATA;
		switch (this.refreshAction) {
			case REFRESHLOCALCOLUMNS:
				actualAction = RefreshAction.REFRESHLOCALCOLUMNSANDDATA;
				break;
			case REFRESHCOLUMNS:
				actualAction = RefreshAction.REFRESHCOLUMNSANDDATA;	
				break;
		}
		refreshGrid(actualAction, true);
	}

	public void onRefreshGradebookItems(Gradebook gradebookModel) {
		switch (this.refreshAction) {
			case REFRESHDATA:
				this.refreshAction = RefreshAction.REFRESHCOLUMNSANDDATA;	
				break;
			default:
				this.refreshAction = RefreshAction.REFRESHCOLUMNS;
				break;
		}
		doRefresh(false);
		if (modeLabel != null) {
			StringBuilder modeLabelText = new StringBuilder();

			modeLabelText
			.append(getDisplayName(gradebookModel.getGradebookItemModel().getCategoryType()))
			.append("/")
			.append(getDisplayName(gradebookModel.getGradebookItemModel().getGradeType()))
			.append(" ").append(i18n.modeText());

			modeLabel.setText(modeLabelText.toString());	
		}
	}

	public void onRefreshGradebookSetup(Gradebook gradebookModel) {
		if (modeLabel != null) {
			StringBuilder modeLabelText = new StringBuilder();

			modeLabelText
			.append(getDisplayName(gradebookModel.getGradebookItemModel().getCategoryType()))
			.append("/")
			.append(getDisplayName(gradebookModel.getGradebookItemModel().getGradeType()))
			.append(" Mode");

			modeLabel.setText(modeLabelText.toString());	
		}
	}

	@Override
	public void onResize(int x, int y) {
		super.onResize(x, y);

		if (isRendered() && toolBarContainer != null)
			toolBarContainer.setWidth(getWidth());
	}


	@Override
	protected void onRender(Element parent, int pos) {	    
		super.onRender(parent, pos);
	}


	private boolean convertBoolean(Boolean b) {
		return b != null && b.booleanValue();
	}

	private ColumnConfig buildColumn(Gradebook selectedGradebook, FixedColumn column, Configuration configModel) {
		boolean isHidden = configModel.isColumnHidden(AppConstants.ITEMTREE, column.getIdentifier(), column.isHidden());
		return buildColumn(selectedGradebook, column.getKey(), column.getIdentifier(), column.getName(),
				true, false, convertBoolean(column.isEditable()), isHidden);
	}

	private ColumnConfig buildColumn(Gradebook selectedGradebook, Item item, Configuration configModel) {
		boolean isHidden = configModel.isColumnHidden(AppConstants.ITEMTREE, item.getIdentifier(), true);
		StringBuilder columnNameBuilder = new StringBuilder().append(item.getName());

		Item gradebookItemModel = selectedGradebook.getGradebookItemModel();
		GradeType gradeType = gradebookItemModel.getGradeType();
		
		if (gradeType != null) {
			switch (gradeType) {
				case POINTS:
					columnNameBuilder.append(" [").append(item.getPoints()).append("pts]");
					break;
				case PERCENTAGES:
					columnNameBuilder.append(" [%]");
					break;
				case LETTERS:
					columnNameBuilder.append(" [A-F]");
					break;
			}
		} else {
			GWT.log("Grade Type is null for some reason", null);
		}

		if (item.getStudentModelKey() != null && item.getStudentModelKey().equals(LearnerKey.GRADE_OVERRIDE.name()))
			columnNameBuilder.append(" [A-F]");

		return buildColumn(selectedGradebook, item.getStudentModelKey(), item.getIdentifier(), 
				columnNameBuilder.toString(), convertBoolean(item.getIncluded()), 
				convertBoolean(item.getExtraCredit()), item.getSource() == null, isHidden);
	}

	private ColumnConfig buildColumn(Gradebook selectedGradebook, String property, String id, String name, 
			boolean isIncluded, boolean isExtraCredit, boolean isEditable, boolean defaultHidden) {

		Configuration configModel = selectedGradebook.getConfigurationModel();
		int columnWidth = configModel.getColumnWidth(gridId, id, name);

		ColumnConfig config = new ColumnConfig(id, name, columnWidth);
		config.setHidden(defaultHidden);

		Field<?> field = null;
		LearnerKey key = LearnerKey.valueOf(property);
		switch (key) {
			case ASSIGNMENT:
				Item gradebookItemModel = selectedGradebook.getGradebookItemModel();
				GradeType gradeType = gradebookItemModel.getGradeType();
				
				if (gradeType != null) {
					switch (gradeType) {
						case POINTS:
						case PERCENTAGES:
							config.setAlignment(HorizontalAlignment.RIGHT);
							config.setNumberFormat(defaultNumberFormat);
	
							NumberField numberField = new NumberField();
							numberField.setFormat(defaultNumberFormat);
							numberField.setPropertyEditorType(Double.class);
							numberField.setSelectOnFocus(true);
							numberField.addInputStyleName(resources.css().gbNumericFieldInput());
							field = numberField;
	
							if (!isIncluded)
								config.setRenderer(unweightedNumericCellRenderer);
							else if (isExtraCredit)
								config.setRenderer(extraCreditNumericCellRenderer);
	
							break;
						case LETTERS:
							TextField<String> textField = new TextField<String>();
							textField.setSelectOnFocus(true);
							textField.addInputStyleName(resources.css().gbTextFieldInput());
							field = textField;
	
							if (!isIncluded)
								config.setRenderer(unweightedTextCellRenderer);
							else if (isExtraCredit)
								config.setRenderer(extraCreditTextCellRenderer);
	
							break;
					}
				} else {
					GWT.log("Grade Type is null for some reason", null);
				}

				break;
			case COURSE_GRADE:

				break;
			case GRADE_OVERRIDE:
				TextField<String> textField = new TextField<String>();
				textField.addInputStyleName(resources.css().gbTextFieldInput());
				textField.setSelectOnFocus(true);
				field = textField;

				if (!isIncluded)
					config.setRenderer(unweightedTextCellRenderer);

				break;
		}

		if (field != null && isEditable) {
			CellEditor editor = new GbCellEditor(field);
			editor.setCompleteOnEnter(false);
			editor.setCancelOnEsc(true);
			config.setEditor(editor);
		}

		return config;
	}

	// FIXME: When changing gradebooks we will need to re-assemble the column model
	private CustomColumnModel assembleColumnModel(Gradebook selectedGradebook) {

		List<FixedColumn> staticColumns = selectedGradebook.getColumns();

		ItemModel gradebookItemModel = (ItemModel)selectedGradebook.getGradebookItemModel();
		Configuration configModel = selectedGradebook.getConfigurationModel();

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		for (FixedColumn column : staticColumns) {
			ColumnConfig config = buildColumn(selectedGradebook, column, configModel);
			configs.add(config);
		}

		for (ModelData m : gradebookItemModel.getChildren()) {
			ItemModel child = (ItemModel)m;
			switch (child.getItemType()) {
				case CATEGORY:
					for (ModelData item : child.getChildren()) {
						configs.add(buildColumn(selectedGradebook, (Item) item, configModel));
					}
					break;
				case ITEM:
					configs.add(buildColumn(selectedGradebook, child, configModel));
					break;
			}
		}

		CustomColumnModel cm = new CustomColumnModel(selectedGradebook.getGradebookUid(), gridId, configs);

		return cm;
	}

	private String getDisplayName(CategoryType categoryType) {
		switch (categoryType) {
		case NO_CATEGORIES:
			return i18n.orgTypeNoCategories();
		case SIMPLE_CATEGORIES:
			return i18n.orgTypeCategories();
		case WEIGHTED_CATEGORIES:
			return i18n.orgTypeWeightedCategories();
		}
		return "N/A";
	}

	private String getDisplayName(GradeType gradeType) {
		switch (gradeType) {
		case POINTS:
			return i18n.gradeTypePoints();
		case PERCENTAGES:
			return i18n.gradeTypePercentages();
		case LETTERS:
			return i18n.gradeTypeLetters();
		}
		
		return "N/A";
	}
	
	@Override
	protected void refreshGrid(RefreshAction refreshAction, boolean useExistingColumnModel) {

		boolean includeData = false;

		switch (refreshAction) {
			case REFRESHDATA:
				includeData = true;
				break;
			case REFRESHLOCALCOLUMNSANDDATA:
				includeData = true;
			case REFRESHLOCALCOLUMNS:
				super.refreshGrid(refreshAction, true);
				return;
			case REFRESHCOLUMNSANDDATA:
				includeData = true;
			case REFRESHCOLUMNS:
				super.refreshGrid(refreshAction, false);
				break;
			case NONE:
				// Do nothing
				break;
			default:
				includeData = true;
				break;
		}

		if (includeData)
			pagingToolBar.refresh();
	}

	private GridCellRenderer<ModelData> unweightedTextCellRenderer = new GridCellRenderer<ModelData>() {

		public Object render(ModelData model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<ModelData> store, Grid<ModelData> grid) {

			Object value = model.get(property);

			if (value == null)
				return "&nbsp;";

			return "<div style=\"color:darkgray; font-style: italic;\">" + value.toString() + "</div>";
		}

	};

	private GridCellRenderer<ModelData> extraCreditTextCellRenderer = new GridCellRenderer<ModelData>() {

		public String render(ModelData model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<ModelData> store, Grid<ModelData> grid) {

			Object value = model.get(property);

			if (value == null)
				return "&nbsp;";

			return "<div style=\"color:darkgreen;\">" + value.toString() + "</div>";
		}
	};

	private void showColumns(ShowColumnsEvent event, CustomColumnModel cm) {
		if (cm != null) {

			if (event.isSingle) {
				toggle(event.model, event.isHidden);
			} else {
				grid.hide();
				// Loop through every column and show/hide it
				for (int i=0;i<cm.getColumnCount();i++) {
					ColumnConfig column = cm.getColumn(i);

					// The first step is to check if this is a static column
					boolean isStatic = event.fullStaticIdSet.contains(column.getId());
					// If it is static, then is it even visible? 
					boolean isStaticVisible = isStatic && event.visibleStaticIdSet.contains(column.getId());

					if (isStatic) {
						cm.setHidden(i, !isStaticVisible);
					} else if (event.selectedItemModelIdSet != null){
						boolean showColumn = (!isStatic && event.selectAll) || event.selectedItemModelIdSet.contains(column.getId());
						if (cm.isHidden(i) == showColumn)
							cm.setHidden(i, !showColumn);
					}
				}
				grid.show();
			}
		}
	}
	
	private void toggle(ItemModel m, boolean isHidden) {
		grid.hide();
		switch (m.getItemType()) {
		case GRADEBOOK:
			toggleCategory(m, isHidden);
			break;
		case CATEGORY:
			toggleCategory(m, isHidden);
			break;
		case ITEM:
			toggleItem(m, isHidden);
			break;
		}
		grid.show();
		grid.getView().layout();
	}
	
	private void toggleCategory(ItemModel m, boolean isHidden) {
		if (m.getChildCount() > 0) {
			for (int i=0;i<m.getChildCount();i++) {
				ItemModel child = (ItemModel)m.getChild(i);
				switch (child.getItemType()) {
				case CATEGORY:
					toggleCategory(child, isHidden);
					break;
				case ITEM:
					toggleItem(child, isHidden);
					break;
				}
			}
		}
	}
	
	private void toggleItem(Item m, boolean isHidden) {
		int columnIndex = cm.findColumnIndex(m.getIdentifier());

		if (columnIndex != -1) {
			cm.setHidden(columnIndex, isHidden);
		}
	}

}
