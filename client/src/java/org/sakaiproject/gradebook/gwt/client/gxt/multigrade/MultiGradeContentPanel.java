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

import org.sakaiproject.gradebook.gwt.client.GradebookToolFacadeAsync;
import org.sakaiproject.gradebook.gwt.client.PersistentStore;
import org.sakaiproject.gradebook.gwt.client.action.PageRequestAction;
import org.sakaiproject.gradebook.gwt.client.action.RemoteCommand;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityGetAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.custom.widget.grid.CustomColumnModel;
import org.sakaiproject.gradebook.gwt.client.custom.widget.grid.CustomGridView;
import org.sakaiproject.gradebook.gwt.client.gxt.GridPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.InstructorViewContainer;
import org.sakaiproject.gradebook.gwt.client.gxt.Notifier;
import org.sakaiproject.gradebook.gwt.client.gxt.StudentViewDialog;
import org.sakaiproject.gradebook.gwt.client.gxt.ViewEditCommentsDialog;
import org.sakaiproject.gradebook.gwt.client.gxt.event.BrowseStudentEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.IndividualStudentEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.event.RefreshCourseGradesEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.event.UserChangeEvent;
import org.sakaiproject.gradebook.gwt.client.model.AssignmentModel;
import org.sakaiproject.gradebook.gwt.client.model.CategoryModel;
import org.sakaiproject.gradebook.gwt.client.model.ColumnModel;
import org.sakaiproject.gradebook.gwt.client.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.model.GradeRecordModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.SectionModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.XDOM;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
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
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
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
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.AdapterToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToggleToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.KeyboardListener;

public class MultiGradeContentPanel extends GridPanel<StudentModel> implements StudentModelOwner {
	
	private static final Notifier notifier = new Notifier();

	private StudentViewDialog singleView; 
	private ToolBar searchToolBar;
	private LayoutContainer toolBarContainer;
	private InstructorViewContainer instructorViewContainer;
	private ViewEditCommentsDialog comments; 
	private Long commentingAssignmentId; 
	private StudentModel commentingStudentModel; 
	private List<ColumnModel> columnDefinitions;
	
	private GridCellRenderer<StudentModel> unweightedNumericCellRenderer;
	private GridCellRenderer<StudentModel> extraCreditNumericCellRenderer;
	
	private ToggleToolItem doNotifyItem;
	
	private int currentIndex = -1;
	
	private MultiGradeContextMenu contextMenu;
	
	
	public MultiGradeContentPanel(final String gradebookUid, InstructorViewContainer ivContainer) {
		super(gradebookUid, "multigrade", EntityType.STUDENT);
		this.instructorViewContainer = ivContainer;
		setHeaderVisible(false);

		GradebookModel model = Registry.get(gradebookUid);
		
		final GradebookToolFacadeAsync service = Registry.get("service");
		
		comments = new ViewEditCommentsDialog(model, service, false); 
		comments.show(); 
		comments.hide(); 
		
		singleView = new StudentViewDialog(gradebookUid, service); 
		
		
		// This UserChangeEvent listener
		addListener(GradebookEvents.UserChange, new Listener<UserChangeEvent>() {

			public void handleEvent(UserChangeEvent uce) {
				
				// Pass these events on to the single view component
				singleView.fireEvent(GradebookEvents.UserChange, uce);
				
				// Respond to the events 
				if (uce.getAction() instanceof UserEntityAction) {
					UserEntityAction action = uce.getAction();
					
					if (instructorViewContainer.getHistoryDialog() != null) {
						instructorViewContainer.getHistoryDialog().fireEvent(GradebookEvents.UserChange, uce);
					}
					
					// FIXME: Ideally we want to ensure that these methods are only called once at the end of a series of operations
					switch (action.getEntityType()) {
					case GRADE_ITEM:
						switch (action.getActionType()) {
						case CREATE:
							// We want to do this immediately, since a "Create" action probably comes
							// from the "Add Assignment" dialog box that may be shown while the multigrade
							// screen is still visible
							refreshGrid();
							break;
						case UPDATE:
							AssignmentModel.Key assignmentModelKey = AssignmentModel.Key.valueOf(((UserEntityUpdateAction)action).getKey());
							switch (assignmentModelKey) {
							// Update actions will (always?) result from user changes on the setup 
							// screens, so they should be deferred to the "onShow" method
							
							// Name changes mean header needs to update, similarly for delete or include
							case NAME: case REMOVED: case INCLUDED: 
								queueDeferredRefresh(RefreshAction.REFRESHCOLUMNS);
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
							case EXTRA_CREDIT:
								queueDeferredRefresh(RefreshAction.REFRESHCOLUMNS);
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
							case REMOVED: case INCLUDED:
								queueDeferredRefresh(RefreshAction.REFRESHCOLUMNS);
								break;
							// Weight changes just mean we need to refresh the screen
							case WEIGHT: case DROP_LOWEST:
								queueDeferredRefresh(RefreshAction.REFRESHDATA);
								break;
							case EXTRA_CREDIT:
								queueDeferredRefresh(RefreshAction.REFRESHCOLUMNS);
								break;
							}
							break;
						}
						
						break;
					case GRADEBOOK:
						switch (action.getActionType()) {
						case UPDATE:
							// Update actions will (always?) result from user changes on the setup 
							// screens, so they should be deferred to the "onShow" method
							GradebookModel.Key gradebookModelKey = GradebookModel.Key.valueOf(((UserEntityUpdateAction)action).getKey());
							switch (gradebookModelKey) {
							case GRADETYPE:
								queueDeferredRefresh(RefreshAction.REFRESHDATA);
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
			
		});
		
		
		
		addListener(GradebookEvents.RefreshCourseGrades, new Listener<RefreshCourseGradesEvent>() {

			public void handleEvent(RefreshCourseGradesEvent rcge) {
				// These events are fired from the setup screens or the student view dialog/container
				// so they can be triggered by onShow rather than via an immediate refresh.
				
				queueDeferredRefresh(RefreshAction.REFRESHDATA);
			}
			
		});
		
		singleView.addListener(GradebookEvents.UserChange, new Listener<UserChangeEvent>() {

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
			
		});

		final Listener<StoreEvent> pageListener = new Listener<StoreEvent>() {

			public void handleEvent(StoreEvent be) {
				StudentModel freshRow = grid.getStore().getAt(currentIndex);
				IndividualStudentEvent event = new IndividualStudentEvent(freshRow);
				if (singleView.fireEvent(GradebookEvents.SingleView, event)) {
					Point pos = getInstructorViewContainer().getPosition(false);
					singleView.setPosition(pos.x, pos.y);
					singleView.setSize(XDOM.getViewportSize().width, XDOM.getViewportSize().height - 35);
					singleView.show();
					//MultiGradeContentPanel.this.hide();
				}
				grid.getStore().removeListener(Store.DataChanged, this);
			}
		
		};
		
		singleView.addListener(Events.BeforeShow, new Listener<WindowEvent>() {

			public void handleEvent(WindowEvent be) {
				MultiGradeContentPanel.this.hide();
			}
			
		});
		
		singleView.addListener(Events.Close, new Listener<WindowEvent>() {

			public void handleEvent(WindowEvent be) {
				MultiGradeContentPanel.this.show();
			}
			
		});
		
		singleView.addListener(GradebookEvents.BrowseStudent, new Listener<BrowseStudentEvent>() {

			public void handleEvent(BrowseStudentEvent be) {
				StudentModel current = be.getStudent();
				currentIndex = grid.getStore().indexOf(current);
				// Do processing for paging -- if we reach the end or beginning of a page
				switch (be.getType()) {
				case PREV:
					currentIndex--;
					break;
				case NEXT:
					currentIndex++;
					break;
				case CURRENT:
					break;
				}
				
				boolean requiresPageChange = false;
				
				int activePage = pagingToolBar.getActivePage();
				int numberOfPages = pagingToolBar.getTotalPages();
				
				if (currentIndex < 0 || currentIndex >= pageSize) {
					requiresPageChange = true;
					grid.getStore().addListener(Store.DataChanged, pageListener);
				}

				// If we are at the first record - 1
				if (currentIndex < 0) {
					// And we are on the first page, then go to the last page
					if (activePage == 1)
						pagingToolBar.last();
					// Otherwise, go to the one before
					else
						pagingToolBar.previous();
					// Either way, go to the last record on the page
					currentIndex = pageSize - 1;
				// If we are at the last record + 1
				} else if (currentIndex >= pageSize) {
					// And if we are on the last page
					if (activePage == numberOfPages) {
						pagingToolBar.first();
					} else {
						pagingToolBar.next();
					}
					currentIndex = 0;
				}
				
				if (!requiresPageChange) {
					StudentModel freshRow = grid.getStore().getAt(currentIndex);
					IndividualStudentEvent event = new IndividualStudentEvent(freshRow);
					singleView.fireEvent(GradebookEvents.SingleView, event);
				}
			}
			
		});
	}
	
	public StudentModel getSelectedModel() {
		return commentingStudentModel;
	}
	
	public Long getSelectedAssignment() {
		return commentingAssignmentId;
	}
	
	
	protected void onShowContextMenu(int x, int y) {
		super.onShowContextMenu(x, y);
		
		
		
	}
	
	protected CustomColumnModel newColumnModel() {
		GradebookModel model = Registry.get(gradebookUid);
		return assembleColumnModel(model.getColumns());
	}
	
	@Override
	protected GridView newGridView() {
		// SAK-2378
		CustomGridView view = new CustomGridView(gradebookUid) {
			
			private Timer showTimer;
			private com.google.gwt.dom.client.Element overCell;
			private ToolTip toolTip;
			private boolean isShowingToolTip;
			
			protected void init(Grid grid) { 
				super.init(grid);
				isShowingToolTip = false;
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
			
			protected void onCellOver(com.google.gwt.dom.client.Element cell, ComponentEvent ce) {
				if (grid.isTrackMouseOver()) {
					if (overCell != cell) {
				        overCell = cell;
				        
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
			
			protected void handleComponentEvent(ComponentEvent ce) {
			    super.handleComponentEvent(ce);
			    
			    com.google.gwt.dom.client.Element cell = findCell(ce.getTarget());
			    switch (ce.type) {
			      case Event.ONMOUSEOVER:
			        if (cell != null) onCellOver(cell, ce);
			        break;
			      case Event.ONMOUSEOUT:
			        if (overCell != null) onCellOut(overCell);
			        break;
			    }
			}
		};
		view.setEmptyText("-");
		
		return view;
	}
	
	protected Menu newContextMenu() {
		contextMenu = new MultiGradeContextMenu(gradebookUid, this);
		
		return contextMenu;
	}

	protected void addComponents() {
		unweightedNumericCellRenderer = new UnweightedNumericCellRenderer();
		extraCreditNumericCellRenderer = new ExtraCreditNumericCellRenderer();
		
		RpcProxy<PagingLoadConfig, PagingLoadResult<SectionModel>> sectionsProxy = new RpcProxy<PagingLoadConfig, PagingLoadResult<SectionModel>>() {
			@Override
			protected void load(PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<SectionModel>> callback) {
				GradebookToolFacadeAsync service = Registry.get("service");
				GradebookModel model = Registry.get(gradebookUid);
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
		
		final TextField<String> searchField = new TextField<String>();
		searchField.setEmptyText("Student name");
		searchField.setWidth(180);
		searchField.addKeyListener(new KeyListener() {
			public void componentKeyPress(ComponentEvent event) {
			    switch (event.getKeyCode()) {
			    case KeyboardListener.KEY_ENTER:
			    	fireEvent(GradebookEvents.DoSearch, event);
			    	break;
			    }
			}
		});
		
		store.addListener(Store.Sort, new Listener<StoreEvent>() {

			public void handleEvent(StoreEvent se) {
				String sortField = ((ListStore)se.store).getSortField();
				SortDir sortDir = ((ListStore)se.store).getSortDir();
				String sortDirection = sortDir == null || sortDir == SortDir.DESC ? "Descending" : "Ascending";
					
				PersistentStore.storePersistentField(gradebookUid, gridId, "sortField", sortField);
				PersistentStore.storePersistentField(gradebookUid, gridId, "sortDir", sortDirection);
			}
			
		});
		
		addListener(GradebookEvents.DoSearch, new Listener<ComponentEvent>() {

			public void handleEvent(ComponentEvent ce) {
				
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
			}
			
		});
		
		addListener(GradebookEvents.ClearSearch, new Listener<ComponentEvent>() {

			public void handleEvent(ComponentEvent ce) {
				
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
			
		});
		
		AdapterToolItem searchFieldItem = new AdapterToolItem(searchField);

		TextToolItem doSearchItem = new TextToolItem("Find", new SelectionListener<ToolBarEvent>() {

			@Override
			public void componentSelected(ToolBarEvent ce) {
				fireEvent(GradebookEvents.DoSearch, ce);
			}
			
		});
		
		doSearchItem.setToolTip("Search for all students with name matching the entered text");
		
		
		TextToolItem clearSearchItem = new TextToolItem("Clear", new SelectionListener<ToolBarEvent>() {

			@Override
			public void componentSelected(ToolBarEvent ce) {
				fireEvent(GradebookEvents.ClearSearch, ce);
			}
			
		});
		
		doNotifyItem = new ToggleToolItem("Show Notifications");
		
		pagingToolBar.add(searchFieldItem);
		pagingToolBar.add(doSearchItem);
		pagingToolBar.add(clearSearchItem);
		pagingToolBar.add(new SeparatorToolItem());
		pagingToolBar.add(sectionChooserItem);
		pagingToolBar.add(new SeparatorToolItem());
		pagingToolBar.add(doNotifyItem);
		
		searchToolBar = new ToolBar();
		
		toolBarContainer = new LayoutContainer();
		toolBarContainer.setLayout(new RowLayout());
		toolBarContainer.add(pagingToolBar, new RowData(1, 1));
		toolBarContainer.add(searchToolBar, new RowData(1, -1));
		
		setTopComponent(toolBarContainer);
		toolBarContainer.layout();
	}
	
	@Override
	protected void addGridListenersAndPlugins(final EditorGrid<StudentModel> grid) {
		grid.addListener(Events.CellDoubleClick, new Listener<GridEvent>() {

			public void handleEvent(GridEvent ge) {
				if (ge.colIndex == 1 || ge.colIndex == 2) {
					StudentModel myStudent = store.getAt(ge.rowIndex);
					IndividualStudentEvent e = new IndividualStudentEvent(myStudent); 
					if (singleView.fireEvent(GradebookEvents.SingleView, e)) {
						int frameHeight = getInstructorViewContainer().getFrameHeight();
						singleView.setPosition(0, frameHeight);
						singleView.setSize(XDOM.getViewportSize().width, XDOM.getViewportSize().height - frameHeight);
						singleView.show();
					}
				}
			}
		});
		
		grid.addListener(Events.ContextMenu, new Listener<GridEvent>(){

			public void handleEvent(GridEvent be) {
				// FIXME - Can this be done better??? 
				if (be.rowIndex >= 0 && be.colIndex >= 0)
				{
					ColumnConfig c = grid.getColumnModel().getColumn(
							be.colIndex);
					String assignIdStr = c.getId();
					long assignId;

					try {
						assignId = Long.parseLong(assignIdStr);
					} catch (NumberFormatException e) {
						be.doit = false;
						return;
					}
					commentingStudentModel = store.getAt(be.rowIndex);
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
					
					Boolean gradedFlag = (Boolean)commentingStudentModel.get(assignId + StudentModel.GRADED_FLAG);
					
					boolean isGraded = gradedFlag != null && gradedFlag.booleanValue();
					
					contextMenu.enableViewGradeHistory(isGraded);
					
					
					int windowWidth = 0; 
					int windowHeight = 0;
					int cellLeft = 0; 
					int cellWidth = 0; 

					int cellTop = 0; 
					int cellHeight = 0; 
					
					int bestX = 0; 
					int bestY = 0; 
				
					int commentsWidth; 
					int commentsHeight; 
					
					if (comments != null) 
					{
						commentsHeight = comments.getOffsetHeight();
						commentsWidth = comments.getOffsetWidth(); 
					}
					else
					{
						commentsWidth = 0; 
						commentsHeight = 0; 
					}
					windowWidth = XDOM.getViewportSize().width; 
					windowHeight = XDOM.getViewportSize().height - getInstructorViewContainer().getFrameHeight(); 
					
					cellLeft = grid.getView().getCell(be.rowIndex, be.colIndex).getAbsoluteLeft();
					cellWidth = grid.getView().getCell(be.rowIndex, be.colIndex).getOffsetWidth();

					cellTop = grid.getView().getCell(be.rowIndex, be.colIndex).getAbsoluteTop();
					cellHeight = grid.getView().getCell(be.rowIndex, be.colIndex).getOffsetHeight();
					
					bestX = cellLeft + cellWidth; 
					bestY = cellTop + cellHeight; 
					
					if ( (bestX + commentsWidth) > windowWidth )
					{
						bestX = cellLeft - commentsWidth; 
						if (bestX < 0)
						{
							// In this case we'll overlap our cell, but that should be ok
							bestX = 0; 
						}
					}
					
					if ( (bestY + commentsHeight) > windowHeight)
					{
						bestY = cellTop - commentsHeight;
						if (bestY  < 0)
						{
							// In this case we'll overlap our cell, but that should be ok
							bestY = 0; 
						}
					}
					
				}
				
				
				else
				{
					be.doit = false; 
				}
			}
		
		});
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
	
	@Override
	public void onResize(int x, int y) {
		super.onResize(x, y);
		
		int frameHeight = getInstructorViewContainer().getFrameHeight();
		singleView.setPosition(0, frameHeight);
		singleView.setSize(XDOM.getViewportSize().width, XDOM.getViewportSize().height - frameHeight);
		
		toolBarContainer.setWidth(getWidth());
	}

	
	@Override
	protected void onRender(Element parent, int pos) {	    
	    super.onRender(parent, pos);
	    
	    
		/*if (getBody() != null) {
			int h = 31; //e.getOffsetHeight();
			int numRows = getBody().getHeight() / h - 1;
			
			if (pageSize != numRows && numRows > 1) {
				pageSize = numRows;
				//loader.load(0, pageSize);
				
				if (pagingToolBar != null) {
					pagingToolBar.setPageSize(numRows);
					pagingToolBar.refresh();
				}
			}
		}*/
	}
	
	
	private CustomColumnModel assembleColumnModel(List<ColumnModel> columns) {
		this.columnDefinitions = columns;
		GradebookModel gbModel = Registry.get(gradebookUid);

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>(columns.size());
		int i = 0;
		
		for (ColumnModel column : columns) {
			int columnWidth = column.getWidth() == null ? 0 : column.getWidth().intValue();
			boolean isHidden = column.isHidden() != null && column.isHidden().booleanValue();
			
			String storedHidden = PersistentStore.getPersistentField(gradebookUid, gridId, "column:" + column.getIdentifier() + ":hidden");
			if (storedHidden != null) {
				isHidden = Boolean.valueOf(storedHidden).booleanValue();
			}
			String storedWidth = PersistentStore.getPersistentField(gradebookUid, gridId, "column:" + column.getIdentifier() + ":width");
			if (storedWidth != null) {
				columnWidth = Integer.parseInt(storedWidth);
			}
			
			ColumnConfig config = new ColumnConfig(column.getIdentifier(), column.getName(), columnWidth);
			
			config.setHidden(isHidden);
			
			Field<?> field = null;
			StudentModel.Key key = StudentModel.Key.valueOf(column.getKey());
			boolean isUnweighted = column.isUnweighted() != null && column.isUnweighted().booleanValue();
			boolean isExtraCredit = column.isExtraCredit() != null && column.isExtraCredit().booleanValue();
			switch (key) {
			case ASSIGNMENT:
				switch (gbModel.getGradeType()) {
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
						
						if (isUnweighted)
							config.setRenderer(unweightedNumericCellRenderer);
						else if (isExtraCredit)
							config.setRenderer(extraCreditNumericCellRenderer);
						
						break;
					case LETTERS:
						TextField<String> textField = new TextField<String>();
						textField.setSelectOnFocus(true);
						textField.addInputStyleName("gbTextFieldInput");
						field = textField;
						
						if (isUnweighted)
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
				
				if (isUnweighted)
					config.setRenderer(unweightedTextCellRenderer);
				
				break;
			}
			
			boolean isEditable = column.isEditable() != null && column.isEditable().booleanValue();
			if (field != null && isEditable) {
				final CellEditor editor = new CellEditor(field);
				editor.setCompleteOnEnter(true);
				editor.setCancelOnEsc(true);
				config.setEditor(editor);
			}

			configs.add(config);
			
			//if (column.getId().equals(StudentModel.Key.COURSE_GRADE.name()) && config.isHidden() == false)
			//	courseGradeColumn = i;
			
			i++;
		}
	
		CustomColumnModel cm = new CustomColumnModel(gradebookUid, gridId, configs);
		
		return cm;
	}
	
	@Override
	protected void refreshGrid() {
		
		GradebookModel gbModel = Registry.get(gradebookUid);
		UserEntityGetAction<ColumnModel> action = 
			new UserEntityGetAction<ColumnModel>(gbModel, EntityType.COLUMN);
		
		RemoteCommand<ColumnModel> remoteCommand = 
			new RemoteCommand<ColumnModel>() {

			@Override
			public void onCommandListSuccess(UserEntityAction<ColumnModel> action, List<ColumnModel> columns) {
	
				GradebookModel gbModel = Registry.get(gradebookUid);
				gbModel.setColumns(columns);
				
				reconfigureGrid(gradebookUid, columns);
			}
		};
		
		remoteCommand.executeList(action);
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
	
	@Override
	protected void beforeUpdateView(UserEntityAction<StudentModel> action, Record record, StudentModel model) {
		for (ColumnModel columnDefinition : columnDefinitions) {
			String id = columnDefinition.getIdentifier();
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
			
		if (doNotifyItem != null && doNotifyItem.isPressed())
			notifier.notify(buffer.toString(), 
				"Stored item grade as '{0}' and recalculated course grade to '{1}' ", model.get(property), model.get(StudentModel.Key.COURSE_GRADE.name()));
	
		fireEvent(GradebookEvents.UserChange, new UserChangeEvent(action));
	}
		
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
	
	private void reconfigureGrid(String gradebookUid, List<ColumnModel> columns) {
		cm = assembleColumnModel(columns);
		grid.reconfigure(store, cm);
		grid.el().unmask();
	}


	public InstructorViewContainer getInstructorViewContainer() {
		return instructorViewContainer;
	}
}
