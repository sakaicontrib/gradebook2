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
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.GradebookState;
import org.sakaiproject.gradebook.gwt.client.GradebookToolFacadeAsync;
import org.sakaiproject.gradebook.gwt.client.action.PageRequestAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.gxt.GridPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.custom.widget.grid.CustomColumnModel;
import org.sakaiproject.gradebook.gwt.client.gxt.custom.widget.grid.CustomGridView;
import org.sakaiproject.gradebook.gwt.client.gxt.event.BrowseLearner;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradeRecordUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.RefreshCourseGradesEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ShowColumnsEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.event.UserChangeEvent;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumnModel;
import org.sakaiproject.gradebook.gwt.client.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.SectionModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelReader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ToolBarEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CellSelectionModel;
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
import com.extjs.gxt.ui.client.widget.toolbar.AdapterToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.KeyboardListener;

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

	private TextField<String> searchField;
	
	private Listener<ComponentEvent> componentEventListener;
	private Listener<GridEvent> gridEventListener;
	private Listener<RefreshCourseGradesEvent> refreshCourseGradesListener;
	private Listener<StoreEvent> storeListener;
	private Listener<UserChangeEvent> userChangeEventListener;
	
	private ShowColumnsEvent lastShowColumnsEvent;
	
	private CellSelectionModel<StudentModel> cellSelectionModel;
	
	public MultiGradeContentPanel(ContentPanel childPanel) {
		super(AppConstants.MULTIGRADE, EntityType.STUDENT, childPanel);
		setId(AppConstants.MULTIGRADE);
		setHeaderVisible(false);
		
		// This UserChangeEvent listener
		addListener(GradebookEvents.UserChange.getEventType(), userChangeEventListener);
		addListener(GradebookEvents.RefreshCourseGrades.getEventType(), refreshCourseGradesListener);
		
		/*singleView.addListener(GradebookEvents.UserChange, new Listener<UserChangeEvent>() {

			public void handleEvent(UserChangeEvent uce) {
				if (uce.getAction() instanceof UserEntityAction) {
					UserEntityAction action = (UserEntityAction)uce.getAction();
					
					// FIXME: Ideally we want to ensure that these methods are only called once at the end of a series of operations
					switch (action.getEntityType()) {
						case GRADE_RECORD:
							switch (action.getActionType()) {
							case UPDATE:
								UserEntityUpdateAction<GradeRecordModel> recordUpdateAction = 
									(UserEntityUpdateAction<GradeRecordModel>)action;
								GradeRecordModel recordModel = recordUpdateAction.getModel();
								GradeRecordModel.Key recordModelKey = GradeRecordModel.Key.valueOf(recordUpdateAction.getKey());
								
								StudentModel studentModel = recordUpdateAction.getStudentModel();
								
								Record r = store.getRecord(studentModel);
								
								// First, clear out any currently dropped
								for (String property : studentModel.getPropertyNames()) {
									if (property.endsWith(StudentModel.DROP_FLAG)) {
										int dropFlagIndex = property.indexOf(StudentModel.DROP_FLAG);
											
										String assignmentId = property.substring(0, dropFlagIndex);
										Object value = studentModel.get(assignmentId);
										Boolean recordDropped = (Boolean)r.get(property);
										Boolean modelDropped = studentModel.get(property);
									
										boolean isDropped = modelDropped != null && modelDropped.booleanValue();
										boolean wasDropped = recordDropped != null && recordDropped.booleanValue();
										
										r.set(property, modelDropped);
										
										if (isDropped || wasDropped) {
											r.set(assignmentId, null);
											r.set(assignmentId, value);
											//r.setDirty(true);
										}
									}
								}
								
								String courseGrade = studentModel.get(StudentModel.Key.COURSE_GRADE.name());
								
								if (courseGrade != null) {
									r.set(StudentModel.Key.COURSE_GRADE.name(), null);
									r.set(StudentModel.Key.COURSE_GRADE.name(), courseGrade);
								}
								
								r.endEdit();

								break;
							}
							break;
					}
				}
			}
			
		});*/

		/*final Listener<StoreEvent> pageListener = new Listener<StoreEvent>() {

			public void handleEvent(StoreEvent be) {
				StudentModel freshRow = grid.getStore().getAt(currentIndex);
				IndividualStudentEvent event = new IndividualStudentEvent(freshRow);
				
				if (singleView == null)
					buildSingleView();
				
				if (singleView.fireEvent(GradebookEvents.SingleView, event)) {
					Point pos = getInstructorViewContainer().getPosition(false);
					singleView.setPosition(pos.x, pos.y);
					singleView.setSize(XDOM.getViewportSize().width, XDOM.getViewportSize().height - 35);
					singleView.show();
					//MultiGradeContentPanel.this.hide();
				}
				grid.getStore().removeListener(Store.DataChanged, this);
			}
		
		};*/
		
		
	}
	
	public void deselectAll() {
		cellSelectionModel.deselectAll();
	}
	
	@Override
	public void editCell(GradebookModel selectedGradebook, Record record, String property, Object value, Object startValue, GridEvent gridEvent) {
		
		String columnHeader = "";
		if (gridEvent != null) {
			String className = grid.getView().getCell(gridEvent.rowIndex, gridEvent.colIndex).getClassName();
			className = className.replace(" gbCellDropped", "");
			grid.getView().getCell(gridEvent.rowIndex, gridEvent.colIndex).setClassName(className);
			grid.getView().getCell(gridEvent.rowIndex, gridEvent.colIndex).setInnerText("Saving...");
			
			columnHeader = grid.getColumnModel().getColumnHeader(gridEvent.colIndex);
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
						selectedLearner = ((ListStore<StudentModel>)se.store).getAt(currentIndex);
						if (selectedLearner != null) {
							//--Dispatcher.forwardEvent(GradebookEvents.SingleGrade, selectedLearner);
							//Dispatcher.forwardEvent(GradebookEvents.SingleView, selectedLearner);
						} else {
							currentIndex--;
						}
					}
					grid.getStore().removeListener(Store.DataChanged, this);
					
					if (selectedLearner != null)
						cellSelectionModel.select(currentIndex);
					
				}
				
			});
		} else {
			StudentModel selectedLearner = grid.getStore().getAt(currentIndex);
			//--Dispatcher.forwardEvent(GradebookEvents.SingleGrade, selectedLearner);
			//Dispatcher.forwardEvent(GradebookEvents.SingleView, selectedLearner);
			
			if (selectedLearner != null)
				cellSelectionModel.select(currentIndex);
		}
	}

	public void onEditMode(Boolean enable) {

	}
	
	public void onEndItemUpdates() {
		refreshGrid(refreshAction);
	}
	
	public void onLearnerGradeRecordUpdated(UserEntityAction<?> action) { 
		
		
	}
	
	public void onItemCreated(ItemModel itemModel) {
		GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);

		ItemModel gradebookModel = selectedGradebook.getGradebookItemModel();
		
		if (gradebookModel.equals(itemModel.getParent())) {
			gradebookModel.getChildren().add(itemModel);
		} else {
			for (ItemModel category : gradebookModel.getChildren()) {
				if (category.equals(itemModel.getParent()))
					category.getChildren().add(itemModel);
			}
		}
		
		onLoadItemTreeModel(selectedGradebook);
	}
	
	public void onItemDeleted(ItemModel itemModel) {
		GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);

		ItemModel gradebookModel = selectedGradebook.getGradebookItemModel();
		
		if (gradebookModel.getChildren().contains(itemModel)) {
			gradebookModel.getChildren().remove(itemModel);
		} else {
			for (ItemModel category : gradebookModel.getChildren()) {
				if (category.getChildren().contains(itemModel))
					category.getChildren().remove(itemModel);
			}
		}
		
		onLoadItemTreeModel(selectedGradebook);
	}
	
	public void onItemUpdated(ItemModel itemModel) {

		switch (itemModel.getItemType()) {
			
		case GRADEBOOK:
			//refreshGrid(RefreshAction.REFRESHCOLUMNSANDDATA);
			//showColumns(lastShowColumnsEvent);
			break;
		case CATEGORY:
			if (itemModel.isActive()) {
				queueDeferredRefresh(RefreshAction.REFRESHDATA);
			}
			break;
		case ITEM:
			if (itemModel.isActive()) {
				queueDeferredRefresh(RefreshAction.REFRESHDATA);
			}
			ColumnConfig column = cm.getColumnById(itemModel.getIdentifier());
			
			if (column != null) {
				if (itemModel.getName() != null) {
					column.setHeader(itemModel.getName());
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
			
			//if (grid.isRendered())
			//	grid.getView().refresh(true);
			
			//showColumns(lastShowColumnsEvent);
			break;
		}
		
		
/*
			switch (assignmentModelKey) {
			// Update actions will (always?) result from user changes on the setup 
			// screens, so they should be deferred to the "onShow" method
			
			// Name changes mean header needs to update, similarly for delete or include
			case NAME:
			case EXTRA_CREDIT:
			case INCLUDED:
			case REMOVED: 
				updateColumns(itemModel);
				queueDeferredRefresh(RefreshAction.REFRESHLOCALCOLUMNS);
				break;
			// Weight changes just mean we need to refresh the screen
			case WEIGHT:  
				queueDeferredRefresh(RefreshAction.REFRESHDATA);
				break;
			case POINTS:
				Boolean isRecalculated = ((UserEntityUpdateAction)action).getDoRecalculateChildren();
				if (isRecalculated != null && isRecalculated.booleanValue())
					queueDeferredRefresh(RefreshAction.REFRESHDATA);
				break;

			}
			break;
		
		}
	case CATEGORY:
		switch (action.getActionType()) {
		case CREATE:
			
			break;
		case UPDATE:
			// Update actions will (always?) result from user changes on the setup 
			// screens, so they should be deferred to the "onShow" method
			CategoryModel.Key categoryModelKey = CategoryModel.Key.valueOf(((UserEntityUpdateAction)action).getKey());
			switch (categoryModelKey) {
			// Don't need to worry about Category name changes
			case REMOVED: case INCLUDED: case EXTRA_CREDIT:
				queueDeferredRefresh(RefreshAction.REFRESHCOLUMNS);
				break;
			// Weight changes just mean we need to refresh the screen
			case WEIGHT: case DROP_LOWEST:
				queueDeferredRefresh(RefreshAction.REFRESHDATA);
				break;
			}
			break;
		}
		
		break;
	case GRADEBOOK:
		switch (action.getActionType()) {
		case UPDATE:
			// We want to do this immediately, since these actions are now being
			// fired from the top level menu and multigrade may well be visible.
			GradebookModel.Key gradebookModelKey = GradebookModel.Key.valueOf(((UserEntityUpdateAction)action).getKey());
			switch (gradebookModelKey) {
			case GRADETYPE:
				refreshGrid(RefreshAction.REFRESHCOLUMNSANDDATA);
				break;
			case CATEGORYTYPE: 
				refreshGrid(RefreshAction.REFRESHCOLUMNS);
				break;
			}
			
			break;
		}*/
	}
	
	/*@Override
	public void onSwitchGradebook(GradebookModel selectedGradebook) {
		super.onSwitchGradebook(selectedGradebook);
	}*/
	
	protected void initListeners() {
		
		componentEventListener = new Listener<ComponentEvent>() {

			public void handleEvent(ComponentEvent ce) {
				
				// FIXME: This could be condensed significantly
				if (ce.type == GradebookEvents.DoSearch.getEventType()) {
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
				} else if (ce.type == GradebookEvents.ClearSearch.getEventType()) {
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
				
				switch (ge.type) {
				case Events.CellClick:
					if (ge.colIndex == 1 || ge.colIndex == 2) {
						StudentModel selectedLearner = store.getAt(ge.rowIndex);
						Dispatcher.forwardEvent(GradebookEvents.SingleGrade.getEventType(), selectedLearner);
						ge.grid.getSelectionModel().select(ge.rowIndex);
					}
					break;
				case Events.ContextMenu:
					if (ge.rowIndex >= 0 && ge.colIndex >= 0) {
						ColumnConfig c = grid.getColumnModel().getColumn(ge.colIndex);
						String assignIdStr = c.getId();
						long assignId;

						try {
							assignId = Long.parseLong(assignIdStr);
						} catch (NumberFormatException e) {
							ge.doit = false;
							return;
						}
						commentingStudentModel = store.getAt(ge.rowIndex);
						commentingAssignmentId = new Long(assignId);
						
						Boolean commentFlag = (Boolean)commentingStudentModel.get(assignId + StudentModel.COMMENTED_FLAG);
						
						boolean isCommented = commentFlag != null && commentFlag.booleanValue();
						
						if (isCommented) {
							contextMenu.enableAddComment(false);
							contextMenu.enableEditComment(true);
						} else {
							contextMenu.enableAddComment(true);
							contextMenu.enableEditComment(false);
						}
						
						//Boolean gradedFlag = (Boolean)commentingStudentModel.get(assignId + StudentModel.GRADED_FLAG);
						
						boolean isGraded = true; //gradedFlag != null && gradedFlag.booleanValue();
						
						contextMenu.enableViewGradeHistory(isGraded);
					} else
						ge.doit = false; 
					break;
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
		
		
		storeListener = new Listener<StoreEvent>() {

			public void handleEvent(StoreEvent se) {
				String sortField = ((ListStore)se.store).getSortField();
				SortDir sortDir = ((ListStore)se.store).getSortDir();
				boolean isAscending = sortDir == SortDir.ASC;
				//String sortDirection = sortDir == null || sortDir == SortDir.DESC ? "Descending" : "Ascending";
				
				GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
				String gradebookUid = selectedGradebook.getGradebookUid();
				GradebookState.setSortInfo(gradebookUid, AppConstants.MULTIGRADE, sortField, isAscending);
			}
			
		};
		
		
		/*userChangeEventListener = new Listener<UserChangeEvent>() {

			public void handleEvent(UserChangeEvent uce) {
				
				// Pass these events on to the single view component
				if (singleView != null)
					singleView.fireEvent(GradebookEvents.UserChange, uce);
				
				// Respond to the events 
				if (uce.getAction() instanceof UserEntityAction) {
					UserEntityAction action = uce.getAction();
					
					/--*
					if (instructorViewContainer.getHistoryDialog() != null) {
						instructorViewContainer.getHistoryDialog().fireEvent(GradebookEvents.UserChange, uce);
					}
					*--/
					
					// FIXME: Ideally we want to ensure that these methods are only called once at the end of a series of operations
					switch (action.getEntityType()) {
					case GRADE_ITEM:
						switch (action.getActionType()) {
						case CREATE:
							// We want to do this immediately, since a "Create" action probably comes
							// from the "Add Assignment" dialog box that may be shown while the multigrade
							// screen is still visible
							refreshGrid(RefreshAction.REFRESHCOLUMNS);
							break;
						case UPDATE:
							AssignmentModel.Key assignmentModelKey = AssignmentModel.Key.valueOf(((UserEntityUpdateAction)action).getKey());
							GradebookModel gbModel = Registry.get(gradebookUid);
							switch (assignmentModelKey) {
							// Update actions will (always?) result from user changes on the setup 
							// screens, so they should be deferred to the "onShow" method
							
							// Name changes mean header needs to update, similarly for delete or include
							case NAME:
							case EXTRA_CREDIT:
							case INCLUDED:
							case REMOVED: 
								updateColumns(assignmentModelKey, (AssignmentModel)action.getModel());
								queueDeferredRefresh(RefreshAction.REFRESHLOCALCOLUMNS);
								break;
							// Weight changes just mean we need to refresh the screen
							case WEIGHT:  
								queueDeferredRefresh(RefreshAction.REFRESHDATA);
								break;
							case POINTS:
								Boolean isRecalculated = ((UserEntityUpdateAction)action).getDoRecalculateChildren();
								if (isRecalculated != null && isRecalculated.booleanValue())
									queueDeferredRefresh(RefreshAction.REFRESHDATA);
								break;

							}
							break;
						}
						
						break;
					case CATEGORY:
						switch (action.getActionType()) {
						case CREATE:
							
							break;
						case UPDATE:
							// Update actions will (always?) result from user changes on the setup 
							// screens, so they should be deferred to the "onShow" method
							CategoryModel.Key categoryModelKey = CategoryModel.Key.valueOf(((UserEntityUpdateAction)action).getKey());
							switch (categoryModelKey) {
							// Don't need to worry about Category name changes
							case REMOVED: case INCLUDED: case EXTRA_CREDIT:
								queueDeferredRefresh(RefreshAction.REFRESHCOLUMNS);
								break;
							// Weight changes just mean we need to refresh the screen
							case WEIGHT: case DROP_LOWEST:
								queueDeferredRefresh(RefreshAction.REFRESHDATA);
								break;
							}
							break;
						}
						
						break;
					case GRADEBOOK:
						switch (action.getActionType()) {
						case UPDATE:
							// We want to do this immediately, since these actions are now being
							// fired from the top level menu and multigrade may well be visible.
							GradebookModel.Key gradebookModelKey = GradebookModel.Key.valueOf(((UserEntityUpdateAction)action).getKey());
							switch (gradebookModelKey) {
							case GRADETYPE:
								refreshGrid(RefreshAction.REFRESHCOLUMNSANDDATA);
								break;
							case CATEGORYTYPE: 
								refreshGrid(RefreshAction.REFRESHCOLUMNS);
								break;
							}
							
							break;
						}
					case GRADE_SCALE:
						switch (action.getActionType()) {
						case UPDATE:
							queueDeferredRefresh(RefreshAction.REFRESHDATA);
							break;
						}
						
						break;
					}
					
					
				}
				
				
				
			}
			
		};*/
		
		/*windowEventListener = new Listener<WindowEvent>() {

			public void handleEvent(WindowEvent be) {
				switch (be.type) {
				case Events.BeforeShow:	
					MultiGradeContentPanel.this.hide();
					break;
				case Events.Close:
					MultiGradeContentPanel.this.show();
					break;
				}
			}
			
		};*/
	}
	
	public void onLoadItemTreeModel(GradebookModel selectedGradebook) {
		cm = assembleColumnModel(selectedGradebook);
		grid.reconfigure(store, cm);
		grid.el().unmask();
	}
	
	public void onShowColumns(ShowColumnsEvent event) {
		this.lastShowColumnsEvent = event;
		showColumns(event, cm);
	}
	
	public void onSwitchGradebook(GradebookModel selectedGradebook) {
		String gradebookUid = selectedGradebook.getGradebookUid();
		if (store != null) {
			// Set the default sort field and direction on the store based on Cookies
			String storedSortField = GradebookState.getSortField(gradebookUid, gridId);
			boolean isAscending = GradebookState.isAscending(gradebookUid, gridId);
			
			SortDir sortDir = isAscending ? SortDir.ASC : SortDir.DESC;

			if (storedSortField != null) 
				store.setDefaultSort(storedSortField, sortDir);
		}

		if (grid == null) 
			add(newGrid(newColumnModel(selectedGradebook)));
		else
			onLoadItemTreeModel(selectedGradebook);
		
		if (loader != null) 
			loader.load(0, pageSize);
		
		//if (lastShowColumnsEvent != null)
		//	showColumns(lastShowColumnsEvent);
	}
	
	public void onUserChange(UserEntityAction<?> action) {
		/*switch (action.getEntityType()) {
		case GRADE_ITEM:
			switch (action.getActionType()) {
			case CREATE:
				// We want to do this immediately, since a "Create" action probably comes
				// from the "Add Assignment" dialog box that may be shown while the multigrade
				// screen is still visible
				refreshGrid(RefreshAction.REFRESHCOLUMNS);
				break;
			case UPDATE:
				AssignmentModel.Key assignmentModelKey = AssignmentModel.Key.valueOf(((UserEntityUpdateAction)action).getKey());
				switch (assignmentModelKey) {
				// Update actions will (always?) result from user changes on the setup 
				// screens, so they should be deferred to the "onShow" method
				
				// Name changes mean header needs to update, similarly for delete or include
				case NAME:
				case EXTRA_CREDIT:
				case INCLUDED:
				case REMOVED: 
					updateColumns(assignmentModelKey, (AssignmentModel)action.getModel());
					refreshGrid(RefreshAction.REFRESHLOCALCOLUMNS);
					break;
				// Weight changes just mean we need to refresh the screen
				case WEIGHT:  
					refreshGrid(RefreshAction.REFRESHDATA);
					break;
				case POINTS:
					Boolean isRecalculated = ((UserEntityUpdateAction)action).getDoRecalculateChildren();
					if (isRecalculated != null && isRecalculated.booleanValue())
						refreshGrid(RefreshAction.REFRESHDATA);
					break;

				}
				break;
			}
			
			break;
		case CATEGORY:
			switch (action.getActionType()) {
			case CREATE:
				
				break;
			case UPDATE:
				// Update actions will (always?) result from user changes on the setup 
				// screens, so they should be deferred to the "onShow" method
				CategoryModel.Key categoryModelKey = CategoryModel.Key.valueOf(((UserEntityUpdateAction)action).getKey());
				switch (categoryModelKey) {
				// Don't need to worry about Category name changes
				case REMOVED: case INCLUDED: case EXTRA_CREDIT:
					refreshGrid(RefreshAction.REFRESHCOLUMNS);
					break;
				// Weight changes just mean we need to refresh the screen
				case WEIGHT: case DROP_LOWEST:
					refreshGrid(RefreshAction.REFRESHDATA);
					break;
				}
				break;
			}
			
			break;
		case GRADEBOOK:
			switch (action.getActionType()) {
			case UPDATE:
				// We want to do this immediately, since these actions are now being
				// fired from the top level menu and multigrade may well be visible.
				GradebookModel.Key gradebookModelKey = GradebookModel.Key.valueOf(((UserEntityUpdateAction)action).getKey());
				switch (gradebookModelKey) {
				case GRADETYPE:
					refreshGrid(RefreshAction.REFRESHCOLUMNSANDDATA);
					break;
				case CATEGORYTYPE: 
					//addCategoryMenuItem.setVisible(((GradebookModel)action.getModel()).getCategoryType() != CategoryType.NO_CATEGORIES);
					refreshGrid(RefreshAction.REFRESHCOLUMNS);
					break;
				}
				
				break;
			}
			break;
		case GRADE_RECORD:
			switch (action.getActionType()) {
			case UPDATE:
				UserEntityUpdateAction<GradeRecordModel> recordUpdateAction = 
					(UserEntityUpdateAction<GradeRecordModel>)action;
				GradeRecordModel recordModel = recordUpdateAction.getModel();
				GradeRecordModel.Key recordModelKey = GradeRecordModel.Key.valueOf(recordUpdateAction.getKey());
				
				StudentModel studentModel = recordUpdateAction.getStudentModel();
				
				Record r = store.getRecord(studentModel);
				
				// First, clear out any currently dropped
				for (String property : studentModel.getPropertyNames()) {
					if (property.endsWith(StudentModel.DROP_FLAG)) {
						int dropFlagIndex = property.indexOf(StudentModel.DROP_FLAG);
							
						String assignmentId = property.substring(0, dropFlagIndex);
						Object value = studentModel.get(assignmentId);
						Boolean recordDropped = (Boolean)r.get(property);
						Boolean modelDropped = studentModel.get(property);
					
						boolean isDropped = modelDropped != null && modelDropped.booleanValue();
						boolean wasDropped = recordDropped != null && recordDropped.booleanValue();
						
						r.set(property, modelDropped);
						
						if (isDropped || wasDropped) {
							r.set(assignmentId, null);
							r.set(assignmentId, value);
							//r.setDirty(true);
						}
					}
				}
				
				String courseGrade = studentModel.get(StudentModel.Key.COURSE_GRADE.name());
				
				if (courseGrade != null) {
					r.set(StudentModel.Key.COURSE_GRADE.name(), null);
					r.set(StudentModel.Key.COURSE_GRADE.name(), courseGrade);
				}
				
				r.endEdit();

				break;
			}
			break;
		case GRADE_SCALE:
			switch (action.getActionType()) {
			case UPDATE:
				queueDeferredRefresh(RefreshAction.REFRESHDATA);
				break;
			}
			
			break;
		}*/
	}
	
	protected CustomColumnModel newColumnModel(GradebookModel selectedGradebook) {
		CustomColumnModel columnModel = assembleColumnModel(selectedGradebook);
		return columnModel;
	}
	
	@Override 
	protected Grid<StudentModel> newGrid(CustomColumnModel cm) {
		Grid<StudentModel> grid = super.newGrid(cm);
		cellSelectionModel = new CellSelectionModel<StudentModel>();
		cellSelectionModel.setSelectionMode(SelectionMode.SINGLE);
		cellSelectionModel.addSelectionChangedListener(new SelectionChangedListener<StudentModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<StudentModel> sce) {
				StudentModel learner = sce.getSelectedItem();
				
				if (learner != null && learner instanceof StudentModel) 
					Dispatcher.forwardEvent(GradebookEvents.SelectLearner.getEventType(), learner);
			}
		
		});
		grid.setSelectionModel(cellSelectionModel);
		//grid.setSelectionModel(new MultiGradeCellSelectionModel());
		return grid;
	}
	
	@Override
	protected GridView newGridView() {
		// SAK-2378
		CustomGridView view = new CustomGridView(gridId) {
			
			private Timer showTimer;
			private com.google.gwt.dom.client.Element overCell;
			private ToolTip toolTip;
			private boolean isShowingToolTip;
			
			protected void init(Grid grid) { 
				super.init(grid);
				isShowingToolTip = false;
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
			/*
			protected void onCellOver(com.google.gwt.dom.client.Element cell, ComponentEvent ce) {
				if (grid.isTrackMouseOver()) {
					if (overCell != cell) {
				        overCell = cell;
				        
				        if (ce.event == null)
				        	return;
				        
				        int x = ce.event.getClientX();
				        int y = ce.event.getClientY();
				        
				        int t = overCell.getAbsoluteTop();
				        int l = overCell.getAbsoluteLeft();
				        
				        int ox = x - l;
				        int oy = y - t;
				        
				        int width = overCell.getOffsetWidth();
				        
				        int bx = (l + width) - x; 
				       
				        boolean isLeftCorner = oy <= 15 && ox <= 15;
				        boolean isRightCorner = oy <= 15 && bx <= 15;
				        
				        if (isLeftCorner || isRightCorner) {
				        	if (!isShowingToolTip) {
					        	int row = findRowIndex(cell);
						        int column = findCellIndex(cell, null);
					        	
						        if (row >= 0 && column >= 0) {
							        com.extjs.gxt.ui.client.widget.grid.ColumnModel columnModel = grid.getColumnModel();
							        StudentModel model = (StudentModel)grid.getStore().getAt(row);
							        
							        if (columnModel != null && model != null) {
							        	ColumnConfig columnConfig = columnModel.getColumn(column);
							        	String property = columnConfig.getDataIndex();
							        	
							        	Record record = grid.getStore().getRecord(model);
							        	
							        	
							        	if (isLeftCorner) {
								        	Object startValue = record.getChanges().get(property);
								        	
								        	String failedProperty = property+ GridPanel.FAILED_FLAG;
											String failedMessage = (String)record.get(failedProperty);
								        	
											String text = null;
											
											if (failedMessage != null) {
												text = new StringBuilder()
													.append("Failed: ").append(failedMessage).toString();
													
											} else if (startValue != null) {
								        		Object currentValue = record.get(property);
								        		text = new StringBuilder()
								        			.append("Successfully modified from " + startValue + " to " + currentValue).toString();
											}
											
											if (text != null) {
									        	ToolTipConfig config = new ToolTipConfig(text);
									        	config.setDismissDelay(2000);
									        	config.setTrackMouse(false);
									        	
									        	if (toolTip == null) {
									        		toolTip = new ToolTip(grid);
									        	}
									        	
									        	final int mx = ce.event.getClientX();
										        final int my = ce.event.getClientY();
									        	
									        	toolTip.update(config);
									        	
									        	isShowingToolTip = true;
									        	showTimer = new Timer() {
									                public void run() {
									                	toolTip.showAt(mx, my);
									                	isShowingToolTip = false;
									                }
									            };
									            showTimer.schedule(500);
								        	}
							        	} else {
							        		String commentProperty = property + StudentModel.COMMENTED_FLAG;
								        	
								        	Boolean commentFlag = (Boolean)record.get(commentProperty);
								        	boolean isCommented = commentFlag != null && commentFlag.booleanValue();
								        	
								        	if (isCommented) {
								        		String text = "Has a comment attached";
							        		
									        	ToolTipConfig config = new ToolTipConfig(text);
									        	config.setDismissDelay(2000);
									        	config.setTrackMouse(false);
									        	
									        	if (toolTip == null) {
									        		toolTip = new ToolTip(grid);
									        	}
									        	
									        	final int mx = ce.event.getClientX();
										        final int my = ce.event.getClientY();
									        	
									        	toolTip.update(config);
									        	
									        	isShowingToolTip = true;
									        	showTimer = new Timer() {
									                public void run() {
									                	toolTip.showAt(mx, my);
									                	isShowingToolTip = false;
									                }
									            };
									            showTimer.schedule(500);
								        	}
							        	}
							        }
						        }
				        	}
				        } else {
				        
				        	if (showTimer != null)
								showTimer.cancel();
							isShowingToolTip = false;
				        }
				    }
				}
			}
			
			
			protected void onCellOut(com.google.gwt.dom.client.Element cell) {
				if (grid.isTrackMouseOver() && overCell != null) {
					overCell = null;
					if (showTimer != null)
						showTimer.cancel();
					isShowingToolTip = false;
				}
			}
			
			protected void handleComponentEvent(GridEvent ge) {
			    super.handleComponentEvent(ge);
			    
			    com.google.gwt.dom.client.Element cell = findCell(ge.getTarget());
			    switch (ge.type) {
			      case Event.ONMOUSEOVER:
			        if (cell != null) onCellOver(cell, ge);
			        break;
			      case Event.ONMOUSEOUT:
			        if (overCell != null) onCellOut(overCell);
			        break;
			    }
			}*/
		};
		view.setEmptyText("-");
		
		return view;
	}
	
	protected Menu newContextMenu() {
		contextMenu = new MultiGradeContextMenu(this);
		return contextMenu;
	}
	
	@Override
	protected void addComponents() {
		
		// We only need to do this once
		if (rendered)
			return;
		
		unweightedNumericCellRenderer = new UnweightedNumericCellRenderer();
		extraCreditNumericCellRenderer = new ExtraCreditNumericCellRenderer();
				
		RpcProxy<PagingLoadConfig, PagingLoadResult<SectionModel>> sectionsProxy = new RpcProxy<PagingLoadConfig, PagingLoadResult<SectionModel>>() {
			@Override
			protected void load(PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<SectionModel>> callback) {
				GradebookToolFacadeAsync service = Registry.get("service");
				GradebookModel model = Registry.get(AppConstants.CURRENT);
				PageRequestAction action = new PageRequestAction(EntityType.SECTION, model.getGradebookUid(), model.getGradebookId());
				service.getEntityPage(action, loadConfig, callback);
			}
		};
		
		BasePagingLoader<PagingLoadConfig, PagingLoadResult<SectionModel>> sectionsLoader = 
			new BasePagingLoader<PagingLoadConfig, PagingLoadResult<SectionModel>>(sectionsProxy, new ModelReader<PagingLoadConfig>());
		
		sectionsLoader.setRemoteSort(true);
		sectionsLoader.load(0, 50);
		
		SectionModel allSections = new SectionModel();
		allSections.setSectionId("all");
		allSections.setSectionName("All Sections");
		
		ListStore<SectionModel> sectionStore = new ListStore<SectionModel>(sectionsLoader);
		//sectionStore.add(allSections);
		sectionStore.setModelComparer(new EntityModelComparer<SectionModel>());
		
		ComboBox<SectionModel> sectionListBox = new ComboBox<SectionModel>(); 
		sectionListBox.setAllQuery(null);
		sectionListBox.setEditable(false);
		sectionListBox.setFieldLabel("Sections");
		sectionListBox.setDisplayField("sectionName");  
		sectionListBox.setStore(sectionStore);
		sectionListBox.setForceSelection(true);
		//sectionListBox.select(0);
		sectionListBox.setEmptyText("Section");
		
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
				
				loadConfig = new MultiGradeLoadConfig();
				loadConfig.setLimit(0);
				loadConfig.setOffset(pageSize);				
				((MultiGradeLoadConfig) loadConfig).setSearchString(searchString);
				((MultiGradeLoadConfig) loadConfig).setSectionUuid(sectionUuid);
				loader.useLoadConfig(loadConfig);
				loader.load(0, pageSize);
			}
			
		});

		AdapterToolItem sectionChooserItem = new AdapterToolItem(sectionListBox);
		
		searchField = new TextField<String>();
		searchField.setEmptyText("Student name");
		searchField.setWidth(180);
		searchField.addKeyListener(new KeyListener() {
			public void componentKeyPress(ComponentEvent event) {
			    switch (event.getKeyCode()) {
			    case KeyboardListener.KEY_ENTER:
			    	fireEvent(GradebookEvents.DoSearch.getEventType(), event);
			    	break;
			    }
			}
		});
		
		store.addListener(Store.Sort, storeListener);
		
		addListener(GradebookEvents.DoSearch.getEventType(), componentEventListener);
		
		addListener(GradebookEvents.ClearSearch.getEventType(), componentEventListener);
		
		AdapterToolItem searchFieldItem = new AdapterToolItem(searchField);

		TextToolItem doSearchItem = new TextToolItem("Find", new SelectionListener<ToolBarEvent>() {

			@Override
			public void componentSelected(ToolBarEvent ce) {
				fireEvent(GradebookEvents.DoSearch.getEventType(), ce);
			}
			
		});
		
		doSearchItem.setToolTip("Search for all students with name matching the entered text");
		
		
		TextToolItem clearSearchItem = new TextToolItem("Clear", new SelectionListener<ToolBarEvent>() {

			@Override
			public void componentSelected(ToolBarEvent ce) {
				fireEvent(GradebookEvents.ClearSearch.getEventType(), ce);
			}
			
		});
		
		searchToolBar = new ToolBar();
		searchToolBar.add(searchFieldItem);
		searchToolBar.add(doSearchItem);
		searchToolBar.add(clearSearchItem);
		searchToolBar.add(new SeparatorToolItem());
		searchToolBar.add(sectionChooserItem);
		
		toolBarContainer = new LayoutContainer();
		toolBarContainer.setLayout(new RowLayout());
		toolBarContainer.add(pagingToolBar, new RowData(1, 1));
		toolBarContainer.add(searchToolBar, new RowData(1, -1));
		
		//setTopComponent(toolBarContainer);
		//toolBarContainer.layout();

		setTopComponent(searchToolBar);
		setBottomComponent(pagingToolBar);
	}
	
	@Override
	protected void addGridListenersAndPlugins(final EditorGrid<StudentModel> grid) {
		grid.addListener(Events.CellClick, gridEventListener);
		grid.addListener(Events.ContextMenu, gridEventListener);
		/*grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<StudentModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<StudentModel> sce) {
				StudentModel learner = sce.getSelectedItem();
				
				if (learner != null) 
					Dispatcher.forwardEvent(GradebookEvents.SelectLearner, learner);
			}
		
		});*/
	}
	
	// TODO: This can probably be removed
	/*@Override
	protected void onRender(Element parent, int pos) {
	    super.onRender(parent, pos);
	
	    if (grid.el() != null) {
			//Info.display("Grid height", "" + grid.getHeight());
			//if (grid.getView() != null) {
			//	com.google.gwt.dom.client.Element e = grid.getView().getRow(0);
			//	if (e != null) {
					int h = 31; //e.getOffsetHeight();
					int numRows = grid.getHeight() / h - 1;
					
					pageSize = numRows;
					loader.load(0, pageSize);
					Info.display("Fits ", "" + numRows);
					
					if (pagingToolBar != null) {
						pagingToolBar.setPageSize(numRows);
						
					}
				//}
			//}
		}
	}*/
	
	public void onRefreshCourseGrades() {
		//refreshGrid(RefreshAction.REFRESHLOCALCOLUMNS);
	}
	
	@Override
	public void onResize(int x, int y) {
		super.onResize(x, y);
		
		/*if (singleView != null) {
			int frameHeight = getInstructorViewContainer().getFrameHeight();
			singleView.setPosition(0, frameHeight);
			singleView.setSize(XDOM.getViewportSize().width, XDOM.getViewportSize().height - frameHeight);
		}*/
		
		//if (gridOwner != null) {
		
		if (isRendered() && toolBarContainer != null)
			toolBarContainer.setWidth(getWidth());
		
			/*if (singleView != null) {
				singleView.setPosition(gridOwner.getAbsoluteLeft(), gridOwner.getAbsoluteTop());
				singleView.setSize(gridOwner.getWidth(), gridOwner.getHeight());
			}*/
			
		//}
	}

	
	@Override
	protected void onRender(Element parent, int pos) {	    
	    super.onRender(parent, pos);
	}

	
	private boolean convertBoolean(Boolean b) {
		return b != null && b.booleanValue();
	}
	
	private ColumnConfig buildColumn(GradebookModel selectedGradebook, FixedColumnModel column) {
		return buildColumn(selectedGradebook, column.getKey(), column.getIdentifier(), column.getName(),
				true, false, convertBoolean(column.isEditable()), convertBoolean(column.isHidden()));
	}
	
	private ColumnConfig buildColumn(GradebookModel selectedGradebook, ItemModel item) {
		
		StringBuilder columnNameBuilder = new StringBuilder().append(item.getName());
		
		switch (selectedGradebook.getGradebookItemModel().getGradeType()) {
		case POINTS:
			columnNameBuilder.append(" (").append(item.getPoints()).append(")");
			break;
		case PERCENTAGES:
			columnNameBuilder.append(" (%)");
			break;
		case LETTERS:
			columnNameBuilder.append(" (A-F)");
			break;
		}
		
		return buildColumn(selectedGradebook, item.getStudentModelKey(), item.getIdentifier(), 
				columnNameBuilder.toString(), convertBoolean(item.getIncluded()), 
				convertBoolean(item.getExtraCredit()), item.getSource() == null, false);
	}
	
	private ColumnConfig buildColumn(GradebookModel selectedGradebook, String property, String id, String name, 
			boolean isIncluded, boolean isExtraCredit, boolean isEditable, boolean defaultHidden) {
		
		String gradebookUid = selectedGradebook.getGradebookUid();
		int columnWidth = GradebookState.getColumnWidth(gradebookUid, gridId, id, name);
		boolean isHidden = !id.equals(StudentModel.Key.LAST_NAME_FIRST); //GradebookState.isColumnHidden(gradebookUid, gridId, id, defaultHidden);
		
		ColumnConfig config = new ColumnConfig(id, name, columnWidth);
		
		config.setHidden(isHidden);
		
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
					//numberField.setMaxValue(column.getMaxPoints());
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
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		
		for (FixedColumnModel column : staticColumns) {
			ColumnConfig config = buildColumn(selectedGradebook, column);
			configs.add(config);
		}
		
		for (ItemModel child : gradebookItemModel.getChildren()) {
			
			switch (child.getItemType()) {
			case CATEGORY:
				for (ItemModel item : child.getChildren()) {
					configs.add(buildColumn(selectedGradebook, item));
				}
				break;
			case ITEM:
				configs.add(buildColumn(selectedGradebook, child));
				break;
			}
		}
		
		CustomColumnModel cm = new CustomColumnModel(selectedGradebook.getGradebookUid(), gridId, configs);
		
		if (lastShowColumnsEvent != null)
			showColumns(lastShowColumnsEvent, cm);
		
		return cm;
	}
	

	@Override
	protected void refreshGrid(RefreshAction refreshAction) {
		
		boolean includeData = false;
		
		switch (refreshAction) {
		case REFRESHDATA:
			includeData = true;
			break;
		case REFRESHLOCALCOLUMNS:
			super.refreshGrid(refreshAction);
			return;
		case REFRESHCOLUMNSANDDATA:
			super.refreshGrid(refreshAction);
			includeData = true;
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
		
		
		/*final Boolean refreshData = Boolean.valueOf(includeData);
		
		GradebookToolFacadeAsync service = Registry.get(AppConstants.SERVICE);
		GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
		
		service.getEntityTreeModel(selectedGradebook.getGradebookUid(), null, new AsyncCallback<ItemModel>() {

			public void onFailure(Throwable caught) {
				Dispatcher.forwardEvent(GradebookEvents.Exception, caught);
			}

			public void onSuccess(ItemModel result) {
				GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
				selectedGradebook.setGradebookItemModel(result);
				Dispatcher.forwardEvent(GradebookEvents.LoadItemTreeModel, selectedGradebook);
				if (refreshData.equals(Boolean.TRUE))
					pagingToolBar.refresh();
			}
			
		});*/

	}
	
	private void doRefresh(String property, Record record, StudentModel model, StudentModel startModel) {
		String dropProperty = new StringBuilder(property).append(StudentModel.DROP_FLAG).toString();
		
		Boolean isDropped = model.get(dropProperty);
		boolean doDrop = isDropped != null && isDropped.booleanValue();
		
		Boolean wasDropped = startModel.get(dropProperty);
		
		boolean unDrop = wasDropped != null && wasDropped.booleanValue();
		
		if (doDrop || unDrop) {
			record.set(dropProperty, model.get(dropProperty));
			if (model.get(property) != null)
				record.set(property, model.get(property));
		}
		
	}
	
	/*@Override
	protected void beforeUpdateView(UserEntityAction<StudentModel> action, Record record, StudentModel model) {
		// FIXME: Is this going to be satisfactory?
		for (int i=0;i<cm.getColumnCount();i++) {
			ColumnConfig config = cm.getColumn(i);
			String id = config.getId();
			StudentModel startModel = action.getModel();
			doRefresh(id, record, model, startModel);
		}
	}
	
	@Override
	protected void updateView(UserEntityAction<StudentModel> action, Record record, StudentModel model) {
		super.updateView(action, record, model);
		
		record.set(StudentModel.Key.COURSE_GRADE.name(), model.get(StudentModel.Key.COURSE_GRADE.name()));
	}
	
	@Override
	protected void afterUpdateView(UserEntityAction<StudentModel> action, Record record, StudentModel model) {
		
		String property = action.getKey();
		//String propertyName = ((UserEntityUpdateAction<StudentModel>)action).getPropertyName();
			
		StringBuilder buffer = new StringBuilder();
		buffer.append(action.getEntityName());
		//buffer.append(" : ");
		//buffer.append(propertyName);
			
		//if (doNotifyItem != null && doNotifyItem.isPressed())
			notifier.notify(buffer.toString(), 
				"Stored item grade as '{0}' and recalculated course grade to '{1}' ", model.get(property), model.get(StudentModel.Key.COURSE_GRADE.name()));
	
		Dispatcher.forwardEvent(GradebookEvents.UserChange, action);
	}*/
		
	private GridCellRenderer<StudentModel> unweightedTextCellRenderer = new GridCellRenderer<StudentModel>() {

		public String render(StudentModel model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<StudentModel> store) {
			
			Object value = model.get(property);
			
			if (value == null)
				return "&nbsp;";
			
			return "<div style=\"color:darkgray; font-style: italic;\">" + value.toString() + "</div>";
		}
	};
	
	private GridCellRenderer<StudentModel> extraCreditTextCellRenderer = new GridCellRenderer<StudentModel>() {

		public String render(StudentModel model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<StudentModel> store) {
			
			Object value = model.get(property);
			
			if (value == null)
				return "&nbsp;";
			
			return "<div style=\"color:darkgreen;\">" + value.toString() + "</div>";
		}
	};
	
	/*private void reconfigureGrid(String gradebookUid, ItemModel rootItemModel) {
		cm = assembleColumnModel(rootItemModel);
		grid.reconfigure(store, cm);
		grid.el().unmask();
	}*/
	
	private void showColumns(ShowColumnsEvent event, CustomColumnModel cm) {
		if (cm != null) {
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
		}
	}

	/*private void updateColumns(AssignmentModel.Key key, AssignmentModel model) {
		GradebookModel gbModel = Registry.get(AppConstants.CURRENT);
		ColumnModel removeColumn = null;
		List<ColumnModel> columns = gbModel.getColumns();
		if (columns != null) {
			for (ColumnModel column : columns) {
				if (column.getIdentifier().equals(model.getIdentifier())) {
					switch (key) {
					case NAME:
						column.setName(model.getName());
						break;
					case EXTRA_CREDIT:
						Boolean isExtraCredit = model.getExtraCredit();
						if (isExtraCredit != null)
							column.setExtraCredit(isExtraCredit);
						break;
					case INCLUDED:
						Boolean isIncluded = model.getIncluded();
						if (isIncluded != null)
							column.setUnweighted(Boolean.valueOf(!isIncluded.booleanValue()));
						break;
					case REMOVED: 
						removeColumn = column;
						break;
					}
				}
			}
		}
		
		if (removeColumn != null)
			columns.remove(removeColumn);
		
		gbModel.setColumns(columns);
	}*/

}
