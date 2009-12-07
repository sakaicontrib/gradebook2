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
package org.sakaiproject.gradebook.gwt.client.gxt.multigrade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.Gradebook2RPCServiceAsync;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.SecureToken;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
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
import org.sakaiproject.gradebook.gwt.client.model.ConfigurationModel;
import org.sakaiproject.gradebook.gwt.client.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumnModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.SectionModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.GradeType;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelReader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MultiGradeContentPanel extends GridPanel<StudentModel> implements StudentModelOwner {

	private enum PageOverflow { TOP, BOTTOM, NONE };

	private ToolBar searchToolBar;
	private LayoutContainer toolBarContainer;
	private Long commentingAssignmentId; 
	private StudentModel commentingStudentModel;

	private GridCellRenderer<StudentModel> unweightedNumericCellRenderer;
	private GridCellRenderer<StudentModel> extraCreditNumericCellRenderer;

	private int currentIndex = -1;

	private MultiGradeContextMenu contextMenu;

	private LabelField modeLabel;
	private TextField<String> searchField;
	private NumberField pageSizeField;

	private Listener<ComponentEvent> componentEventListener;
	private Listener<GridEvent> gridEventListener;
	private Listener<RefreshCourseGradesEvent> refreshCourseGradesListener;
	//private Listener<StoreEvent> storeListener;
	private Listener<UserChangeEvent> userChangeEventListener;

	private MultigradeSelectionModel<StudentModel> cellSelectionModel;
	private BasePagingLoader<PagingLoadResult<SectionModel>> sectionsLoader;


	public MultiGradeContentPanel(ContentPanel childPanel, I18nConstants i18n) {
		super(AppConstants.MULTIGRADE, EntityType.LEARNER, childPanel, i18n);
		setHeaderVisible(false);

		// This UserChangeEvent listener
		addListener(GradebookEvents.UserChange.getEventType(), userChangeEventListener);
		addListener(GradebookEvents.RefreshCourseGrades.getEventType(), refreshCourseGradesListener);

	}

	public void deselectAll() {
		cellSelectionModel.deselectAll();
	}

	@Override
	public void editCell(GradebookModel selectedGradebook, Record record, String property, Object value, Object startValue, GridEvent gridEvent) {

		String columnHeader = "";
		if (gridEvent != null) {
			String className = grid.getView().getCell(gridEvent.getRowIndex(), gridEvent.getColIndex()).getClassName();
			className = className.replace(" gbCellDropped", "");
			grid.getView().getCell(gridEvent.getRowIndex(), gridEvent.getColIndex()).setClassName(className);
			grid.getView().getCell(gridEvent.getRowIndex(), gridEvent.getColIndex()).setInnerText("Saving...");

			columnHeader = grid.getColumnModel().getColumnHeader(gridEvent.getColIndex());
		}

		Dispatcher.forwardEvent(GradebookEvents.UpdateLearnerGradeRecord.getEventType(), new GradeRecordUpdate(record, property, columnHeader, startValue, value));
	}

	public StudentModel getSelectedModel() {
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
		StudentModel current = be.learner;
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
			grid.getStore().addListener(Store.DataChanged, new Listener<StoreEvent<StudentModel>>() {

				public void handleEvent(StoreEvent<StudentModel> se) {
					StudentModel selectedLearner = null;
					while (selectedLearner == null && currentIndex >= 0) {
						selectedLearner = ((ListStore<StudentModel>)se.getStore()).getAt(currentIndex);
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
			StudentModel selectedLearner = grid.getStore().getAt(currentIndex);

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
		GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);

		ItemModel gradebookModel = selectedGradebook.getGradebookItemModel();

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

	public void onItemDeleted(ItemModel itemModel) {
		GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);

		ItemModel gradebookModel = selectedGradebook.getGradebookItemModel();

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

	public void onItemUpdated(ItemModel itemModel) {

		GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
		final GradeType gradeType = selectedGradebook.getGradebookItemModel().getGradeType();

		ItemModelProcessor processor = new ItemModelProcessor(itemModel) {

			public void doCategory(ItemModel categoryModel) {
				if (categoryModel.isActive()) 
					queueDeferredRefresh(RefreshAction.REFRESHDATA);
			}

			public void doItem(ItemModel itemModel) {
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
					loader.useLoadConfig(loadConfig);
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
					loader.useLoadConfig(loadConfig);
					loader.load(0, pageSize);
				}
			}

		};

		gridEventListener = new Listener<GridEvent>() {

			public void handleEvent(GridEvent ge) {

				if (ge.getType().equals(Events.CellClick)) {
					if (ge.getColIndex() == 0 || ge.getColIndex() == 1 || ge.getColIndex() == 2) {
						StudentModel selectedLearner = store.getAt(ge.getRowIndex());
						Dispatcher.forwardEvent(GradebookEvents.SingleGrade.getEventType(), selectedLearner);
						ge.getGrid().getSelectionModel().select(ge.getRowIndex(), false);
					}
				} else if (ge.getType().equals(
						Events.ContextMenu.getEventCode())) {
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

						Boolean commentFlag = (Boolean) commentingStudentModel
								.get(assignId + StudentModel.COMMENTED_FLAG);

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

	public void onSwitchGradebook(GradebookModel selectedGradebook) {

		if (store != null) {
			ConfigurationModel configModel = selectedGradebook.getConfigurationModel();

			// Set the default sort field and direction on the store based on Cookies
			String storedSortField = configModel.getSortField(gridId);
			boolean isAscending = configModel.isAscending(gridId);

			SortDir sortDir = isAscending ? SortDir.ASC : SortDir.DESC;

			if (storedSortField != null) 
				store.setDefaultSort(storedSortField, sortDir);
		}

		onRefreshGradebookSetup(selectedGradebook);
		reconfigureGrid(newColumnModel(selectedGradebook));

		int pageSize = getPageSize();
		if (loader != null) 
			loader.load(0, pageSize);

		//if (sectionsLoader != null)
		//	sectionsLoader.load();
	}

	public void onUserChange(UserEntityAction<?> action) {

	}

	protected CustomColumnModel newColumnModel(GradebookModel selectedGradebook) {
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
				return property.equals(StudentModel.Key.DISPLAY_NAME.name()) ||
				property.equals(StudentModel.Key.LAST_NAME_FIRST.name()) ||
				property.equals(StudentModel.Key.DISPLAY_ID.name());
			}

			protected boolean isCommented(ModelData model, String property) {
				String commentedProperty = property + StudentModel.COMMENTED_FLAG;
				Boolean isCommented = model.get(commentedProperty);

				return isCommented != null && isCommented.booleanValue();
			}

			protected boolean isDropped(ModelData model, String property) {
				String droppedProperty = property + StudentModel.DROP_FLAG;
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
						css.append(" gbCellFailed");
					} else if (startValue == null || !startValue.equals(currentValue)) {
						css.append(" gbCellSucceeded");
					}
				}

				if (isDropped(model, property)) {
					css.append(" gbCellDropped");
				}

				if (isReleased(model, property)) {
					css.append(" gbReleased");
				}

				if (css.length() > 0)
					return css.toString();

				return null;
			}

			@Override
			protected String markupInnerCss(ModelData model, String property, boolean isShowDirtyCells, boolean isPropertyChanged) {

				StringBuilder innerCssClass = new StringBuilder();

				if (isCommented(model, property)) {
					innerCssClass.append(" gbCellCommented");
				} 

				if (isClickable(model, property)) {
					innerCssClass.append(" gbCellClickable");
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
	protected PagingLoadConfig newLoadConfig(ListStore<StudentModel> store, int pageSize) {
		SortInfo sortInfo = store.getSortState();
		MultiGradeLoadConfig loadConfig = new MultiGradeLoadConfig();
		loadConfig.setLimit(0);
		loadConfig.setOffset(pageSize);	
		if (sortInfo == null)
			sortInfo = new SortInfo(StudentModel.Key.LAST_NAME_FIRST.name(), SortDir.ASC);

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

		RpcProxy<PagingLoadResult<SectionModel>> sectionsProxy = new RpcProxy<PagingLoadResult<SectionModel>>() {
			@Override
			protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<SectionModel>> callback) {
				Gradebook2RPCServiceAsync service = Registry.get("service");
				GradebookModel model = Registry.get(AppConstants.CURRENT);
				service.getPage(model.getGradebookUid(), model.getGradebookId(), EntityType.SECTION, (PagingLoadConfig)loadConfig, SecureToken.get(), callback);
			}
		};

		sectionsLoader = 
			new BasePagingLoader<PagingLoadResult<SectionModel>>(sectionsProxy, new ModelReader());

		sectionsLoader.setRemoteSort(true);

		SectionModel allSections = new SectionModel();
		allSections.setSectionId("all");
		allSections.setSectionName("All Sections");

		ListStore<SectionModel> sectionStore = new ListStore<SectionModel>(sectionsLoader);
		sectionStore.setModelComparer(new EntityModelComparer<SectionModel>());

		ComboBox<SectionModel> sectionListBox = new ComboBox<SectionModel>(); 
		sectionListBox.setAllQuery(null);
		sectionListBox.setEditable(false);
		sectionListBox.setFieldLabel("Sections");
		sectionListBox.setDisplayField(SectionModel.Key.SECTION_NAME.name());  
		sectionListBox.setStore(sectionStore);
		sectionListBox.setForceSelection(true);
		sectionListBox.setEmptyText(i18n.sectionEmptyText());

		sectionListBox.addSelectionChangedListener(new SelectionChangedListener<SectionModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<SectionModel> se) {
				SectionModel model = se.getSelectedItem();

				String searchString = null;
				String sectionUuid = null;

				if (loadConfig != null)
					searchString = ((MultiGradeLoadConfig) loadConfig).getSearchString();
				if (model != null) 
					sectionUuid = model.getSectionId();

				int pageSize = getPageSize();
				loadConfig = new MultiGradeLoadConfig();
				loadConfig.setLimit(0);
				loadConfig.setOffset(pageSize);				
				((MultiGradeLoadConfig) loadConfig).setSearchString(searchString);
				((MultiGradeLoadConfig) loadConfig).setSectionUuid(sectionUuid);
				loader.useLoadConfig(loadConfig);
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
							GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
							ConfigurationModel model = new ConfigurationModel(selectedGradebook.getGradebookId());
							model.setPageSize(gridId, Integer.valueOf(pageSize.intValue()));

							Gradebook2RPCServiceAsync service = Registry.get(AppConstants.SERVICE);

							AsyncCallback<ConfigurationModel> callback = new AsyncCallback<ConfigurationModel>() {

								public void onFailure(Throwable caught) {
									// FIXME: Should we notify the user when this fails?
								}

								public void onSuccess(ConfigurationModel result) {
									GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
									ConfigurationModel configModel = selectedGradebook.getConfigurationModel();

									Collection<String> propertyNames = result.getPropertyNames();
									if (propertyNames != null) {
										List<String> names = new ArrayList<String>(propertyNames);

										for (int i=0;i<names.size();i++) {
											String name = names.get(i);
											String value = result.get(name);
											configModel.set(name, value);
										}
									}
								}

							};

							service.update(model, EntityType.CONFIGURATION, null, SecureToken.get(), callback);

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
	protected void addGridListenersAndPlugins(final EditorGrid<StudentModel> grid) {

		// We only need to do this once
		if (rendered)
			return;

		grid.addListener(Events.CellClick, gridEventListener);
		grid.addListener(Events.ContextMenu, gridEventListener);

		cellSelectionModel = new MultigradeSelectionModel<StudentModel>();
		cellSelectionModel.setSelectionMode(SelectionMode.SINGLE);
		cellSelectionModel.addSelectionChangedListener(new SelectionChangedListener<StudentModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<StudentModel> sce) {
				StudentModel learner = sce.getSelectedItem();

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

	public void onRefreshGradebookItems(GradebookModel gradebookModel) {
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

	public void onRefreshGradebookSetup(GradebookModel gradebookModel) {
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

	private ColumnConfig buildColumn(GradebookModel selectedGradebook, FixedColumnModel column, ConfigurationModel configModel) {
		boolean isHidden = configModel.isColumnHidden(AppConstants.ITEMTREE, column.getIdentifier(), column.isHidden());
		return buildColumn(selectedGradebook, column.getKey(), column.getIdentifier(), column.getName(),
				true, false, convertBoolean(column.isEditable()), isHidden);
	}

	private ColumnConfig buildColumn(GradebookModel selectedGradebook, ItemModel item, ConfigurationModel configModel) {
		boolean isHidden = configModel.isColumnHidden(AppConstants.ITEMTREE, item.getIdentifier(), true);
		StringBuilder columnNameBuilder = new StringBuilder().append(item.getName());

		switch (selectedGradebook.getGradebookItemModel().getGradeType()) {
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

		if (item.getStudentModelKey() != null && item.getStudentModelKey().equals(StudentModel.Key.GRADE_OVERRIDE.name()))
			columnNameBuilder.append(" [A-F]");

		return buildColumn(selectedGradebook, item.getStudentModelKey(), item.getIdentifier(), 
				columnNameBuilder.toString(), convertBoolean(item.getIncluded()), 
				convertBoolean(item.getExtraCredit()), item.getSource() == null, isHidden);
	}

	private ColumnConfig buildColumn(GradebookModel selectedGradebook, String property, String id, String name, 
			boolean isIncluded, boolean isExtraCredit, boolean isEditable, boolean defaultHidden) {

		ConfigurationModel configModel = selectedGradebook.getConfigurationModel();
		int columnWidth = configModel.getColumnWidth(gridId, id, name);

		ColumnConfig config = new ColumnConfig(id, name, columnWidth);
		config.setHidden(defaultHidden);

		Field<?> field = null;
		StudentModel.Key key = StudentModel.Key.valueOf(property);
		switch (key) {
			case ASSIGNMENT:
				switch (selectedGradebook.getGradebookItemModel().getGradeType()) {
					case POINTS:
					case PERCENTAGES:
						config.setAlignment(HorizontalAlignment.RIGHT);
						config.setNumberFormat(defaultNumberFormat);

						NumberField numberField = new NumberField();
						numberField.setFormat(defaultNumberFormat);
						numberField.setPropertyEditorType(Double.class);
						numberField.setSelectOnFocus(true);
						numberField.addInputStyleName("gbNumericFieldInput");
						field = numberField;

						if (!isIncluded)
							config.setRenderer(unweightedNumericCellRenderer);
						else if (isExtraCredit)
							config.setRenderer(extraCreditNumericCellRenderer);

						break;
					case LETTERS:
						TextField<String> textField = new TextField<String>();
						textField.setSelectOnFocus(true);
						textField.addInputStyleName("gbTextFieldInput");
						field = textField;

						if (!isIncluded)
							config.setRenderer(unweightedTextCellRenderer);
						else if (isExtraCredit)
							config.setRenderer(extraCreditTextCellRenderer);

						break;
				}

				break;
			case COURSE_GRADE:

				break;
			case GRADE_OVERRIDE:
				TextField<String> textField = new TextField<String>();
				textField.addInputStyleName("gbTextFieldInput");
				textField.setSelectOnFocus(true);
				field = textField;

				if (!isIncluded)
					config.setRenderer(unweightedTextCellRenderer);

				break;
		}

		if (field != null && isEditable) {
			final CellEditor editor = new CellEditor(field);
			editor.setCompleteOnEnter(true);
			editor.setCancelOnEsc(true);
			config.setEditor(editor);
		}

		return config;
	}

	// FIXME: When changing gradebooks we will need to re-assemble the column model
	private CustomColumnModel assembleColumnModel(GradebookModel selectedGradebook) {

		List<FixedColumnModel> staticColumns = selectedGradebook.getColumns();

		ItemModel gradebookItemModel = selectedGradebook.getGradebookItemModel();
		ConfigurationModel configModel = selectedGradebook.getConfigurationModel();

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		for (FixedColumnModel column : staticColumns) {
			ColumnConfig config = buildColumn(selectedGradebook, column, configModel);
			configs.add(config);
		}

		for (ModelData m : gradebookItemModel.getChildren()) {
			ItemModel child = (ItemModel)m;
			switch (child.getItemType()) {
				case CATEGORY:
					for (ModelData item : child.getChildren()) {
						configs.add(buildColumn(selectedGradebook, (ItemModel) item, configModel));
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

	private GridCellRenderer<StudentModel> unweightedTextCellRenderer = new GridCellRenderer<StudentModel>() {

		public Object render(StudentModel model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<StudentModel> store, Grid<StudentModel> grid) {

			Object value = model.get(property);

			if (value == null)
				return "&nbsp;";

			return "<div style=\"color:darkgray; font-style: italic;\">" + value.toString() + "</div>";
		}

	};

	private GridCellRenderer<StudentModel> extraCreditTextCellRenderer = new GridCellRenderer<StudentModel>() {

		public String render(StudentModel model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<StudentModel> store, Grid<StudentModel> grid) {

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
				/*int columnIndex = cm.findColumnIndex(event.itemModelId);

				if (columnIndex != -1) {
					cm.setHidden(columnIndex, event.isHidden);
				}*/
			} else {

				/*if (!event.visibleStaticIdSet.contains(StudentModel.Key.DISPLAY_ID.name()) 
						&& !event.visibleStaticIdSet.contains(StudentModel.Key.DISPLAY_NAME.name())
						&& !event.visibleStaticIdSet.contains(StudentModel.Key.LAST_NAME_FIRST.name())) 
					event.visibleStaticIdSet.add(StudentModel.Key.LAST_NAME_FIRST.name());
				*/
				
				grid.setVisible(false);
				// Loop through every column and show/hide it
				for (int i=0;i<cm.getColumnCount();i++) {
					ColumnConfig column = cm.getColumn(i);

					// The first step is to check if this is a static column
					boolean isStatic = event.fullStaticIdSet.contains(column.getId());
					// If it is static, then is it even visible? 
					boolean isStaticVisible = isStatic && event.visibleStaticIdSet.contains(column.getId());

					if (isStatic)
						cm.setHidden(i, !isStaticVisible);
					else if (event.selectedItemModelIdSet != null){
						boolean showColumn = (!isStatic && event.selectAll) || event.selectedItemModelIdSet.contains(column.getId());
						if (cm.isHidden(i) == showColumn)
							cm.setHidden(i, !showColumn);
					}
				}
				grid.setVisible(true);
			}
		}
	}
	
	private void toggle(ItemModel m, boolean isHidden) {
		grid.setVisible(false);
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
		grid.setVisible(true);
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
	
	private void toggleItem(ItemModel m, boolean isHidden) {
		int columnIndex = cm.findColumnIndex(m.getIdentifier());

		if (columnIndex != -1) {
			cm.setHidden(columnIndex, isHidden);
		}
	}

}
