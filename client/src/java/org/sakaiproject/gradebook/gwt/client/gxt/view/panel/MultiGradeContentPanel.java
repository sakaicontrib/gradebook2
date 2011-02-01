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
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.gxt.GbCellEditor;
import org.sakaiproject.gradebook.gwt.client.gxt.GbEditorGrid;
import org.sakaiproject.gradebook.gwt.client.gxt.ItemModelProcessor;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaButton;
import org.sakaiproject.gradebook.gwt.client.gxt.custom.widget.grid.CustomColumnModel;
import org.sakaiproject.gradebook.gwt.client.gxt.custom.widget.grid.CustomGridView;
import org.sakaiproject.gradebook.gwt.client.gxt.event.BrowseLearner;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradeRecordUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ShowColumnsEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ConfigurationModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.LearnerUtil;
import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.ExtraCreditNumericCellRenderer;
import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.MultiGradeContextMenu;
import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.MultiGradeLoadConfig;
import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.MultigradeSelectionModel;
import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.StudentModelOwner;
import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.UnweightedNumericCellRenderer;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.SectionsComboBox;
import org.sakaiproject.gradebook.gwt.client.model.Configuration;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumn;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;
import org.sakaiproject.gradebook.gwt.client.model.key.SectionKey;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.EntityType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.type.GroupType;
import org.sakaiproject.gradebook.gwt.client.resource.GradebookResources;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.extjs.gxt.ui.client.event.BaseEvent;
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
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;

public abstract class MultiGradeContentPanel extends GradebookPanel implements StudentModelOwner {

	private enum PageOverflow { TOP, BOTTOM, NONE };

	protected static final int DEFAULT_PAGE_SIZE = 19;
	
	public enum RefreshAction { NONE, REFRESHDATA, REFRESHCOLUMNS, REFRESHLOCALCOLUMNS, REFRESHCOLUMNSANDDATA, REFRESHLOCALCOLUMNSANDDATA };
	
	protected EditorGrid<ModelData> grid;
	//protected ListStore<ModelData> store;
	
	protected String gridId;
	protected PagingToolBar pagingToolBar;
	protected EntityType entityType;
	
	protected NumberFormat defaultNumberFormat;
	
	protected CustomColumnModel cm;
	protected PagingLoadConfig loadConfig;
	
	protected ContentPanel gridOwner;
	
	protected RefreshAction refreshAction = RefreshAction.NONE;
	
	protected boolean isPopulated = false;
	protected final boolean isImport;
	
	private ToolBar searchToolBar;
	private LayoutContainer toolBarContainer;
	private Long commentingAssignmentId; 
	private ModelData commentingStudentModel;

	private GridCellRenderer<ModelData> unweightedNumericCellRenderer;
	private GridCellRenderer<ModelData> extraCreditNumericCellRenderer;

	private int currentIndex = -1;

	private MultiGradeContextMenu contextMenu;

	//private CheckBox useClassicNavigationCheckBox;
	private LabelField modeLabel;
	private TextField<String> searchField;
	private NumberField pageSizeField;
	
	private SectionsComboBox<ModelData> sectionListBox;

	private Listener<ComponentEvent> componentEventListener;
	private Listener<GridEvent<ModelData>> gridEventListener;
	//private Listener<RefreshCourseGradesEvent> refreshCourseGradesListener;
	//private Listener<UserChangeEvent> userChangeEventListener;

	private MultigradeSelectionModel<ModelData> cellSelectionModel;
	
	public MultiGradeContentPanel(ListStore<ModelData> store, boolean isImport) {
		super();
		this.isImport = isImport;
		
		if (isImport) {
			setHeading(i18n.multigradeImportHeader());
			setHeaderVisible(true);
		} else
			setHeaderVisible(false);
		
		setLayout(new FitLayout());
		setIconStyle("icon-table");
		setMonitorWindowResize(true);
	
		this.defaultNumberFormat = DataTypeConversionUtil.getDefaultNumberFormat();

		initListeners();

		pagingToolBar = newPagingToolBar(DEFAULT_PAGE_SIZE);	
		
		addComponents();

		// This UserChangeEvent listener
//		addListener(GradebookEvents.UserChange.getEventType(), userChangeEventListener);
//		addListener(GradebookEvents.RefreshCourseGrades.getEventType(), refreshCourseGradesListener);
	}
	
	public void addGrid(Configuration configModel, List<FixedColumn> staticColumns, ItemModel gradebookItemModel) {
		
		pagingToolBar.bind(newLoader());
		
		cm = newColumnModel(configModel, staticColumns, gradebookItemModel);
		grid = new GbEditorGrid<ModelData>(newStore(), cm);
		loadConfig = newLoadConfig(newStore(), getPageSize());
		
		addGridListenersAndPlugins(grid);
		
		GridView view = newGridView();
		
		grid.setView(view);
		grid.setLoadMask(true);
		grid.setBorders(true);
	
		grid.addListener(Events.ValidateEdit, new Listener<GridEvent>() {

			public void handleEvent(final GridEvent ge) {
				// By setting ge.doit to false, we ensure that the AfterEdit event is not thrown. Which means we have to throw it ourselves onSuccess
				ge.stopEvent();
				
				Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
				
				if (selectedGradebook != null)
					editCell(selectedGradebook, ge.getRecord(), ge.getProperty(), ge.getValue(), ge.getStartValue(), ge);
			}

		});
		
		// GRBK-775 : Making sure that we don't loose the entered grade
		// when the user scrolls 
		grid.addListener(Events.BodyScroll, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                grid.stopEditing();
            }
        });
		
		grid.setStripeRows(true);
		
		Menu gridContextMenu = newContextMenu();
		
		if (gridContextMenu != null)
			grid.setContextMenu(gridContextMenu);
		
		
		add(grid);
	}

	public void deselectAll() {
		cellSelectionModel.deselectAll();
	}
	
	public void doRefresh(Configuration configModel, List<FixedColumn> staticColumns, ItemModel gradebookItemModel) {
		switch (refreshAction) {
		case REFRESHDATA:
			if (pagingToolBar != null)
				pagingToolBar.refresh();
			break;
		case REFRESHCOLUMNS:
		case REFRESHLOCALCOLUMNS:
		case REFRESHCOLUMNSANDDATA:
			refreshGrid(refreshAction, configModel, staticColumns, gradebookItemModel);
			break;
		}
		refreshAction = RefreshAction.NONE;
	}

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
	
	protected abstract ListStore<ModelData> newStore();

	protected abstract PagingLoader<PagingLoadResult<ModelData>> newLoader();
	
	
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

	public void onEndItemUpdates(Configuration configModel, List<FixedColumn> staticColumns, ItemModel gradebookItemModel) {
		doRefresh(configModel, staticColumns, gradebookItemModel);
	}

	public void onLearnerGradeRecordUpdated(UserEntityAction<?> action) { 


	}

	public void onItemCreated(ItemModel itemModel) {
		Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);

		ItemModel gradebookModel = (ItemModel)selectedGradebook.getGradebookItemModel();
		
		ItemModel categoryItemModel = (ItemModel) selectedGradebook.getCategoryItemModel(itemModel.getCategoryId());
		
		if(gradebookModel.equals(categoryItemModel)) {
			gradebookModel.getChildren().add(itemModel);
		
		} else {
			for (ModelData category : gradebookModel.getChildren()) {
				if (category.equals(categoryItemModel))
					((BaseTreeModel) category).getChildren().add(itemModel);
			}
		}

		onRefreshGradebookItems(selectedGradebook, null);
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

		onRefreshGradebookItems(selectedGradebook, null);
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
					if (sectionListBox != null) {
						List<ModelData> selectedItems = sectionListBox.getSelection();
						if (selectedItems != null && selectedItems.size() > 0) {
							ModelData m = selectedItems.get(0);
							sectionUuid = m.get(SectionKey.S_ID.name());
						}
					}
					loadConfig = new MultiGradeLoadConfig();
					loadConfig.setLimit(0);
					loadConfig.setOffset(pageSize);
					((MultiGradeLoadConfig) loadConfig).setSearchString(searchString);
					((MultiGradeLoadConfig) loadConfig).setSectionUuid(sectionUuid);
					((BasePagingLoader)newLoader()).useLoadConfig(loadConfig);
					newLoader().load(0, pageSize);
				} else if (ce.getType() == GradebookEvents.ClearSearch.getEventType()) {
					int pageSize = getPageSize();
					searchField.setValue(null);
					String sectionUuid = null;
					if (sectionListBox != null) {
						List<ModelData> selectedItems = sectionListBox.getSelection();
						if (selectedItems != null && selectedItems.size() > 0) {
							ModelData m = selectedItems.get(0);
							sectionUuid = m.get(SectionKey.S_ID.name());
						}
					}
					loadConfig = new MultiGradeLoadConfig();
					loadConfig.setLimit(0);
					loadConfig.setOffset(pageSize);
					((MultiGradeLoadConfig) loadConfig).setSectionUuid(sectionUuid);
					((BasePagingLoader)newLoader()).useLoadConfig(loadConfig);
					newLoader().load(0, pageSize);
				}
			}
		};

		gridEventListener = new Listener<GridEvent<ModelData>>() {

			public void handleEvent(GridEvent<ModelData> ge) {

				if (ge.getType().equals(Events.CellClick)) {
					if (!isImport) {
						if (ge.getColIndex() == 0 || ge.getColIndex() == 1 || ge.getColIndex() == 2) {
							ModelData selectedLearner = newStore().getAt(ge.getRowIndex());
							Dispatcher.forwardEvent(GradebookEvents.SingleGrade.getEventType(), selectedLearner);
							ge.getGrid().getSelectionModel().select(ge.getRowIndex(), false);
						}
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
							// GRBK-575 : disabling all context menu items if the user clicks
							// on a none grade item cell
							contextMenu.enableAddComment(false);
							contextMenu.enableEditComment(false);
							contextMenu.enableViewGradeHistory(false);
							return;
						}
						commentingStudentModel = newStore().getAt(ge.getRowIndex());
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

		/*refreshCourseGradesListener = new Listener<RefreshCourseGradesEvent>() {

			public void handleEvent(RefreshCourseGradesEvent rcge) {
				// These events are fired from the setup screens or the student view dialog/container
				// so they can be triggered by onShow rather than via an immediate refresh.

				queueDeferredRefresh(RefreshAction.REFRESHDATA);
			}

		};*/
	}

	public void onShowColumns(ShowColumnsEvent event) {
		showColumns(event, cm);
	}
	
	public void onSwitchGradebook(Gradebook selectedGradebook) {

		Configuration configModel = selectedGradebook.getConfigurationModel();
		ItemModel gradebookItemModel = (ItemModel)selectedGradebook.getGradebookItemModel();
		List<FixedColumn> staticColumns = selectedGradebook.getColumns();
		
		if (newStore() != null) {
			// Set the default sort field and direction on the store based on Cookies
			String storedSortField = configModel.getSortField(gridId);
			boolean isAscending = configModel.isAscending(gridId);

			SortDir sortDir = isAscending ? SortDir.ASC : SortDir.DESC;

			if (storedSortField != null) 
				newStore().setDefaultSort(storedSortField, sortDir);
		}
		
		//Boolean useClassicNavigation = Boolean.valueOf(configModel.isClassicNavigation());
		//useClassicNavigationCheckBox.setValue(useClassicNavigation);
		
		int pageSize = configModel.getPageSize(gridId);
		
		if (pageSize == -1)
			pageSize = DEFAULT_PAGE_SIZE;
		
		pagingToolBar.setPageSize(pageSize);
		
		onRefreshGradebookSetup(selectedGradebook);
		
		if (!isPopulated) 
			reconfigureGrid(newColumnModel(configModel, staticColumns, gradebookItemModel));

		pagingToolBar.refresh();
		pageSizeField.setValue(Integer.valueOf(pageSize));
	}

	public void onUserChange(UserEntityAction<?> action) {

	}

	protected CustomColumnModel newColumnModel(Configuration configModel, List<FixedColumn> staticColumns, ItemModel gradebookItemModel) {
		CustomColumnModel columnModel = assembleColumnModel(configModel, staticColumns, gradebookItemModel);
		return columnModel;
	}
	
	protected void reconfigureGrid(CustomColumnModel cm) {
		this.cm = cm;
		
		newLoader().setRemoteSort(true);
		
		loadConfig = newLoadConfig(newStore(), getPageSize());

		((BasePagingLoader)newLoader()).useLoadConfig(loadConfig);

		pagingToolBar.bind(newLoader());

		grid.reconfigure(newStore(), cm);
	}

	protected GridView newGridView() {
		// SAK-2378
		CustomGridView view = new CustomGridView(gridId) {

			protected void init(Grid grid) { 
				super.init(grid);
			}

			protected boolean isClickable(ModelData model, String property) {
				if (isImport)
					return false;
				
				return property.equals(LearnerKey.S_DSPLY_NM.name()) ||
					property.equals(LearnerKey.S_LST_NM_FRST.name()) ||
					property.equals(LearnerKey.S_DSPLY_ID.name());
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

				if (isImport) {
					boolean isUserNotFound = DataTypeConversionUtil.checkBoolean((Boolean)model.get(LearnerKey.B_USR_NT_FD.name()));

					if (isUserNotFound)
						return resources.css().gbCellDropped();
				}
				
				if (!LearnerUtil.isFixed(property)) {
										
					if (isImport || (isShowDirtyCells && r != null)) {
						String failedProperty = DataTypeConversionUtil.buildFailedKey(property);
						String failedMessage = r == null ? (String)model.get(failedProperty) : (String)r.get(failedProperty);
						
						// GRBK-668
						String convertedProperty = DataTypeConversionUtil.buildConvertedMessageKey(property);
						String convertedMessage = r == null ? (String)model.get(convertedProperty) : (String)r.get(convertedProperty);
						
						if (failedMessage != null) {
							if (isImport)
								css.append(" ").append(resources.css().gbCellFailedImport());
							else {
								if (GXT.isIE)
									css.append(" ieGbCellFailed");
								else {
									css.append(" ").append(resources.css().gbCellFailed());
								}
							}
						}
						else if(null != convertedMessage) { // GRBK-668
							if (isImport)
								css.append(" ").append(resources.css().gbCellConvertedValueImport());
							else {
								if (GXT.isIE)
									css.append(" ieGbCellSucceeded ");
								else {
									css.append(" ").append(resources.css().gbCellConvertedValueImport());
								}
							}
						}
					}
					
					if (isShowDirtyCells && r != null /*&& isPropertyChanged*/) {
						String successProperty = DataTypeConversionUtil.buildSuccessKey(property);
						String successMessage = (String)r.get(successProperty);
	
						if (successMessage != null) {
							if (GXT.isIE)
								css.append(" ieGbCellSucceeded");
							else
								css.append(" ").append(resources.css().gbCellSucceeded());
						}
					}
	
					if (isDropped(model, property)) {
						css.append(" ").append(resources.css().gbCellDropped());
					}
	
					if (isReleased(model, property)) {
						css.append(" ").append(resources.css().gbReleased());
					}
				} 
				
				if (css.length() > 0)
					return css.toString();

				return null;
			}

			@Override
			protected String markupInnerCss(ModelData model, String property, boolean isShowDirtyCells, boolean isPropertyChanged) {
				StringBuilder innerCssClass = new StringBuilder();
				
				GradebookResources resources = Registry.get(AppConstants.RESOURCES);
					
				if (isCommented(model, property)) {
					if (GXT.isIE)
						innerCssClass.append(" ieGbCellCommented");
					else
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
	
	protected Menu newContextMenu() {
		contextMenu = new MultiGradeContextMenu(this);
		return contextMenu;
	}
	
	protected PagingToolBar newPagingToolBar(int pageSize) {
		PagingToolBar pagingToolBar = new PagingToolBar(pageSize);
		PagingToolBar.PagingToolBarMessages messages = pagingToolBar.getMessages();
		messages.setAfterPageText(i18n.pagingAfterPageText());
		messages.setBeforePageText(i18n.pagingPageText());
		messages.setDisplayMsg(i18n.pagingDisplayMsgText());
		
		return pagingToolBar;
	}

	protected PagingLoadConfig newLoadConfig(ListStore<ModelData> store, int pageSize) {
		SortInfo sortInfo = store.getSortState();
		MultiGradeLoadConfig loadConfig = new MultiGradeLoadConfig();
		loadConfig.setLimit(0);
		loadConfig.setOffset(pageSize);	
		if (sortInfo == null && !isImport)
			sortInfo = new SortInfo(LearnerKey.S_LST_NM_FRST.name(), SortDir.ASC);

		loadConfig.setSortInfo(sortInfo);

		return loadConfig;
	}

	protected void addComponents() {

		// We only need to do this once
		if (rendered)
			return;

		unweightedNumericCellRenderer = new UnweightedNumericCellRenderer();
		extraCreditNumericCellRenderer = new ExtraCreditNumericCellRenderer();

		searchToolBar = new ToolBar();
		
		if (!isImport) {
			
	
			sectionListBox = new SectionsComboBox<ModelData>();	
			sectionListBox.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
	
				@Override
				public void selectionChanged(SelectionChangedEvent<ModelData> se) {
					ModelData model = se.getSelectedItem();
	
					String searchString = searchField.getValue();
					String sectionUuid = null;
	
					if (model != null) 
						sectionUuid = model.get(SectionKey.S_ID.name());
	
					int pageSize = getPageSize();
					loadConfig = new MultiGradeLoadConfig();
					loadConfig.setLimit(0);
					loadConfig.setOffset(pageSize);				
					((MultiGradeLoadConfig) loadConfig).setSearchString(searchString);
					((MultiGradeLoadConfig) loadConfig).setSectionUuid(sectionUuid);
					((BasePagingLoader)newLoader()).useLoadConfig(loadConfig);
					newLoader().load(0, pageSize);
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
			

			searchToolBar.add(searchField);
			searchToolBar.add(doSearchItem);
			searchToolBar.add(clearSearchItem);
			searchToolBar.add(new SeparatorToolItem());
			searchToolBar.add(sectionListBox);
		}
		
		/*useClassicNavigationCheckBox = new CheckBox();
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
			
		});*/
		
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

						if (pageSize != null && pageSize.intValue() > 0) {
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

		
		
		//searchToolBar.add(new SeparatorToolItem());
		//searchToolBar.add(useClassicNavigationCheckBox);
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

	public void onRefreshCourseGrades(Configuration configModel, List<FixedColumn> staticColumns, ItemModel gradebookItemModel) {
		RefreshAction actualAction = RefreshAction.REFRESHDATA;
		switch (this.refreshAction) {
			case REFRESHLOCALCOLUMNS:
				actualAction = RefreshAction.REFRESHLOCALCOLUMNSANDDATA;
				break;
			case REFRESHCOLUMNS:
				actualAction = RefreshAction.REFRESHCOLUMNSANDDATA;	
				break;
		}
		refreshGrid(actualAction, configModel, staticColumns, gradebookItemModel);
	}

	public void onRefreshGradebookItems(Gradebook gradebookModel, Item gradebookItem) {
		switch (this.refreshAction) {
			case REFRESHDATA:
				this.refreshAction = RefreshAction.REFRESHCOLUMNSANDDATA;	
				break;
			default:
				this.refreshAction = RefreshAction.REFRESHCOLUMNS;
				break;
		}
		// FIXME: Probably need this!
		//doRefresh(false);
		if (modeLabel != null) {
			StringBuilder modeLabelText = new StringBuilder();

			if (gradebookItem == null)
				gradebookItem = gradebookModel.getGradebookItemModel();
			
			modeLabelText
			.append(getDisplayName(gradebookItem.getCategoryType()))
			.append("/")
			.append(getDisplayName(gradebookItem.getGradeType()))
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

	private ColumnConfig buildColumn(Configuration configModel, GradeType gradeType, FixedColumn column) {
		boolean isHidden = configModel.isColumnHidden(AppConstants.ITEMTREE, column.getIdentifier(), DataTypeConversionUtil.checkBoolean(column.isHidden()));
		return buildColumn(configModel, gradeType, column.getKey(), column.getIdentifier(),
				column.getName(), true, false, convertBoolean(column.isEditable()), isHidden);
	}

	private ColumnConfig buildColumn(Configuration configModel, GradeType gradeType, Item item) {
		boolean isHidden = configModel.isColumnHidden(AppConstants.ITEMTREE, item.getIdentifier(), true);
		
		if (isImport) 
			isHidden = !item.isChecked();
		
		StringBuilder columnNameBuilder = new StringBuilder().append(item.getName());

		if (gradeType != null) {
			columnNameBuilder.append(" ").append(i18n.columnSuffixPrefix());
			switch (gradeType) {
				case POINTS:
					columnNameBuilder.append(item.getPoints()).append(i18n.columnSuffixPoints());
					break;
				case PERCENTAGES:
					columnNameBuilder.append(i18n.columnSuffixPercentages());
					break;
				case LETTERS:
					columnNameBuilder.append(i18n.columnSuffixLetterGrades());
					break;
			}
		} else {
			GWT.log("Grade Type is null for some reason", null);
		}

		if (item.getStudentModelKey() != null && item.getStudentModelKey().equals(LearnerKey.S_OVRD_GRD.name()))
			columnNameBuilder.append(" ").append(i18n.columnSuffixPrefix()).append(i18n.columnSuffixLetterGrades());

		return buildColumn(configModel, gradeType, item.getStudentModelKey(), 
				item.getIdentifier(), columnNameBuilder.toString(), 
				convertBoolean(item.getIncluded()), convertBoolean(item.getExtraCredit()), item.getSource() == null, isHidden);
	}

	private ColumnConfig buildColumn(Configuration configModel, GradeType gradeType, String property, String id, 
			String name, boolean isIncluded, boolean isExtraCredit, boolean isEditable, boolean defaultHidden) {

		int columnWidth = configModel.getColumnWidth(gridId, id, name);

		ColumnConfig config = new ColumnConfig(id, name, columnWidth);
		config.setHidden(defaultHidden);

		Field<?> field = null;
		LearnerKey key = LearnerKey.valueOf(property);
		switch (key) {
			case S_ITEM:
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
			case S_CRS_GRD:

				break;
			case S_OVRD_GRD:
				TextField<String> textField = new TextField<String>();
				textField.addInputStyleName(resources.css().gbTextFieldInput());
				textField.setSelectOnFocus(true);
				field = textField;

				if (!isIncluded)
					config.setRenderer(unweightedTextCellRenderer);

				break;
		}

		if (field != null && isEditable && !isImport) {
			CellEditor editor = new GbCellEditor(field);
			editor.setCompleteOnEnter(false);
			editor.setCancelOnEsc(true);
			config.setEditor(editor);
		}

		return config;
	}

	// FIXME: When changing gradebooks we will need to re-assemble the column model
	private CustomColumnModel assembleColumnModel(Configuration configModel, List<FixedColumn> staticColumns, ItemModel gradebookItemModel) {

		GradeType gradeType = (null != gradebookItemModel) ? gradebookItemModel.getGradeType() : null;
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		if (staticColumns != null) {
			for (FixedColumn column : staticColumns) {
				if (!isImport || LearnerKey.valueOf(column.getIdentifier()).getGroup() != GroupType.GRADES) {	
					ColumnConfig config = buildColumn(configModel, gradeType, column);
					configs.add(config);
				}
			}
		}

		if (gradebookItemModel != null) {
			for (ModelData m : gradebookItemModel.getChildren()) {
				ItemModel child = (ItemModel)m;
				switch (child.getItemType()) {
					case CATEGORY:
						for (ModelData item : child.getChildren()) {
							configs.add(buildColumn(configModel, gradeType, (Item) item));
						}
						break;
					case ITEM:
						configs.add(buildColumn(configModel, gradeType, child));
						break;
				}
			}
		}

		CustomColumnModel cm = new CustomColumnModel(configs);

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
	
	protected void doRefreshGrid(RefreshAction action, boolean useExistingColumnModel, 
			Configuration configModel, List<FixedColumn> staticColumns, ItemModel gradebookItemModel) {

		if (!useExistingColumnModel || cm == null)
			cm = newColumnModel(configModel, staticColumns, gradebookItemModel);
		
		grid.reconfigure(newStore(), cm);
		
		if (grid.isRendered())
			grid.el().unmask();
	}
	
	public void refreshGrid(RefreshAction refreshAction, 
			Configuration configModel, List<FixedColumn> staticColumns, ItemModel gradebookItemModel) {

		boolean includeData = false;

		switch (refreshAction) {
			case REFRESHDATA:
				includeData = true;
				break;
			case REFRESHLOCALCOLUMNSANDDATA:
				includeData = true;
			case REFRESHLOCALCOLUMNS:
				doRefreshGrid(refreshAction, true, configModel, staticColumns, gradebookItemModel);
				return;
			case REFRESHCOLUMNSANDDATA:
				includeData = true;
			case REFRESHCOLUMNS:
				doRefreshGrid(refreshAction, false, configModel, staticColumns, gradebookItemModel);
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
	
	public PagingToolBar getPagingToolBar() {
		return pagingToolBar;
	}

	public int getPageSize() {
		int pageSize = DEFAULT_PAGE_SIZE;
		
		if (pagingToolBar != null) 
			pageSize = pagingToolBar.getPageSize();
		
		return pageSize;
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
	
	public EditorGrid<ModelData> getGrid() {
		return grid;
	}
	
	public ListStore<ModelData> getStore() {
		return newStore();
	}

	public RefreshAction getRefreshAction() {
		return refreshAction;
	}

	public void setRefreshAction(RefreshAction refreshAction) {
		this.refreshAction = refreshAction;
	}

}
