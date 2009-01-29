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
package org.sakaiproject.gradebook.gwt.client.gxt.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.GradebookConstants;
import org.sakaiproject.gradebook.gwt.client.GradebookToolFacadeAsync;
import org.sakaiproject.gradebook.gwt.client.action.PageRequestAction;
import org.sakaiproject.gradebook.gwt.client.action.RemoteCommand;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.custom.widget.grid.CustomColumnModel;
import org.sakaiproject.gradebook.gwt.client.gxt.NotifyingAsyncCallback;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.RefreshCourseGradesEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.event.UserChangeEvent;
import org.sakaiproject.gradebook.gwt.client.model.AssignmentModel;
import org.sakaiproject.gradebook.gwt.client.model.CategoryModel;
import org.sakaiproject.gradebook.gwt.client.model.EntityModel;
import org.sakaiproject.gradebook.gwt.client.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemEntityModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.SummaryColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.SummaryRenderer;
import com.extjs.gxt.ui.client.widget.grid.SummaryType;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SettingsAssignmentContentPanel extends SettingsGridPanel<AssignmentModel> {
	
	private SettingsCategoryContentPanel categoriesPanel;
	
	private ItemCheckColumnConfig releasedColumn;
	
	public SettingsAssignmentContentPanel(String gradebookUid, SettingsCategoryContentPanel categoriesPanel) {
		super(gradebookUid, "assignmentgrid", EntityType.GRADE_ITEM);
		this.categoriesPanel = categoriesPanel;

		setHeaderVisible(false);
		setLayout(new FitLayout());
		
		addListener(GradebookEvents.UserChange, new Listener<UserChangeEvent>() {
			
			public void handleEvent(UserChangeEvent uce) {
				if (uce.getAction() instanceof UserEntityAction) {
				
					UserEntityAction entityAction = (UserEntityAction)uce.getAction();
					// These are generally coming from the Category Setup panel
					switch (entityAction.getEntityType()) {
					case GRADE_ITEM:
						switch(entityAction.getActionType()) {
						case CREATE:
							reloadData();
							break;
						case UPDATE:
							UserEntityUpdateAction<AssignmentModel> updateAction = (UserEntityUpdateAction<AssignmentModel>)uce.getAction();
							AssignmentModel.Key assignmentModelKey = AssignmentModel.Key.valueOf(updateAction.getKey());
							switch (assignmentModelKey) {
							case INCLUDED: case REMOVED: case EXTRA_CREDIT:
								reloadWeights(assignmentModelKey, updateAction.getModel());
								break;
							}
							break;
						}
						break;
					case CATEGORY:
						switch(entityAction.getActionType()) {
						case UPDATE:
							UserEntityUpdateAction updateAction = (UserEntityUpdateAction)uce.getAction();
							CategoryModel.Key categoryModelKey = CategoryModel.Key.valueOf(updateAction.getKey());
							
							switch(categoryModelKey) {
							case NAME: case INCLUDED: case REMOVED: case EQUAL_WEIGHT:
								reloadData();
								break;
							}
							// FIXME: I think we should be able to just refresh, since the recalculation
							// FIXME: will happen on the server anyway
							/*case EQUAL_WEIGHT:
								recalculateEqualWeightingAssignments(updateAction.getEntityId(), (Boolean)updateAction.getValue(), false);
								break;
							};*/
							break;
						}
						break;
					case GRADEBOOK:
						switch (entityAction.getActionType()) {
						case UPDATE:
							UserEntityUpdateAction updateAction = (UserEntityUpdateAction)uce.getAction();
							
							GradebookModel.Key gradebookKey = GradebookModel.Key.valueOf(updateAction.getKey());
	
							switch (gradebookKey) {
							case CATEGORYTYPE:
								CategoryType categoryType = (CategoryType) updateAction.getValue();
								
								switch (categoryType) {
								case WEIGHTED_CATEGORIES:
									weightColumn.setHidden(false);
									break;
								default:
									weightColumn.setHidden(true);
									break;
								}
								if (isRendered()) {
									if (grid != null && grid.getView() != null && grid.getStore() != null)
										grid.getView().refresh(true);
								}
								reloadData();
								break;
							}
							
							break;
						}
						break;	
					}
				}
			}
		});
		
	}
	
	public void reloadData() {
		pagingToolBar.refresh();
	}
	
	/*public void reloadWeights(AssignmentModel.Key key, final AssignmentModel changedModel) {
		GradebookToolFacadeAsync service = Registry.get("service");
		PageRequestAction pageAction = newPageRequestAction();
		
		boolean isDeleted = changedModel.getRemoved() != null && changedModel.getRemoved().booleanValue();
		boolean showDeleted = showDeletedItems != null && showDeletedItems.isPressed();
		
		if (isDeleted && !showDeleted) 
			store.remove(changedModel);
		
		service.getEntityPage(pageAction, loadConfig, new NotifyingAsyncCallback<PagingLoadResult<EntityModel>>() {

			public void onSuccess(PagingLoadResult<EntityModel> result) {
				List<EntityModel> models = result.getData();
				
				for (EntityModel model : models) {
					AssignmentModel serverModel = (AssignmentModel)model;
					Double weight = serverModel.getWeighting();
					
					AssignmentModel actualModel = store.findModel(serverModel);
				
					actualModel.setWeighting(weight);
					
					store.update(actualModel);
				}
				
			}
			
		});
	}*/

	public EditorGrid<AssignmentModel> getAssignmentGrid() {
		return grid;
	}

	@Override
	protected void addGridListenersAndPlugins(EditorGrid<AssignmentModel> grid) {
		super.addGridListenersAndPlugins(grid);
		
		grid.addPlugin(releasedColumn);
	}

	@Override
	protected CustomColumnModel newColumnModel() {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();  
		
		SummaryColumnConfig categoryColumn = new SummaryColumnConfig(AssignmentModel.Key.CATEGORY_NAME.name(), 
				ItemEntityModel.getPropertyName(AssignmentModel.Key.CATEGORY_NAME), 180);
		categoryColumn.setHidden(true);
		categoryColumn.setMenuDisabled(true);
		columns.add(categoryColumn);
		
		nameColumn.setSummaryRenderer(new SummaryRenderer() {
			public String render(Double value, Map<String, Double> data) {
				return "<div style=\"color:darkgray;font-weight:bold;font-size:10pt\">Total:</div>";
			}
		});
		columns.add(nameColumn);
		
		columns.add(includedColumn);
		
		SummaryColumnConfig pointsColumn =  new SummaryColumnConfig(AssignmentModel.Key.POINTS.name(), 
				ItemEntityModel.getPropertyName(AssignmentModel.Key.POINTS), 80);
		pointsColumn.setAlignment(HorizontalAlignment.RIGHT);
		pointsColumn.setEditor(defaultNumericCellEditor);
		pointsColumn.setGroupable(false);
		pointsColumn.setMenuDisabled(true);
		pointsColumn.setNumberFormat(defaultNumberFormat);
		pointsColumn.setSortable(false);
		pointsColumn.setSummaryType(SummaryType.SUM);
		pointsColumn.setSummaryRenderer(new SummaryRenderer() {
			public String render(Double value, Map<String, Double> data) {
				String result = "0";
				if (value != null)
					result = defaultNumberFormat.format(value);
				return new StringBuilder("<div style=\"color:darkgray;font-weight:bold\">").append(result).append("</div>").toString();
			}
		});
		columns.add(pointsColumn);
		
		columns.add(weightColumn);

		columns.add(extraCreditColumn);
		
		
		//TimeField timeField = new TimeField();
		//timeField.getPropertyEditor().setFormat(DateTimeFormat.getFormat("hh:mm"));
		
		DateField dateField = new DateField();
		dateField.getPropertyEditor().setFormat(DateTimeFormat.getFormat("MM/dd/yy"));
		
		//MultiField multiField = new MultiField("dueDate", dateField, timeField);
		
		SummaryColumnConfig dueDateColumn =  new SummaryColumnConfig(AssignmentModel.Key.DUE_DATE.name(), 
				ItemEntityModel.getPropertyName(AssignmentModel.Key.DUE_DATE), 120);
		dueDateColumn.setGroupable(false);
		dueDateColumn.setEditor(new CellEditor(dateField));
		dueDateColumn.setDateTimeFormat(DateTimeFormat.getMediumDateTimeFormat());
		dueDateColumn.setMenuDisabled(true);
		dueDateColumn.setSortable(false);
		//columns.add(dueDateColumn);
		
		releasedColumn =  new ItemCheckColumnConfig(AssignmentModel.Key.RELEASED.name(), 
				ItemEntityModel.getPropertyName(AssignmentModel.Key.RELEASED), 80);
		releasedColumn.setGroupable(false);
		releasedColumn.setMenuDisabled(true);
		releasedColumn.setSortable(false);
		columns.add(releasedColumn);
		
		SummaryColumnConfig sourceColumn =  new SummaryColumnConfig(AssignmentModel.Key.SOURCE.name(), 
				ItemEntityModel.getPropertyName(AssignmentModel.Key.SOURCE), 120);
		sourceColumn.setGroupable(false);
		sourceColumn.setMenuDisabled(true);
		sourceColumn.setSortable(false);
		columns.add(sourceColumn);

		columns.add(removedColumn);
		
		
		GradebookModel gbModel = Registry.get(gradebookUid);
		switch (gbModel.getCategoryType()) {
		case WEIGHTED_CATEGORIES:
			weightColumn.setHidden(false);
			break;
		default:
			weightColumn.setHidden(true);
			break;
		}
		
		CustomColumnModel cm = new CustomColumnModel(gradebookUid, gridId, columns);
		
		return cm;
	}

	
	@Override 
	protected ListStore<AssignmentModel> newStore(BasePagingLoader<PagingLoadConfig, PagingLoadResult<AssignmentModel>> loader) {
		GroupingStore<AssignmentModel> store = (GroupingStore<AssignmentModel>)super.newStore(loader);
		store.groupBy(AssignmentModel.Key.CATEGORY_NAME.name());
		store.setModelComparer(new EntityModelComparer<AssignmentModel>());
		return store;
	}
	
	@Override
	protected void updateView(UserEntityAction<AssignmentModel> action, Record record, AssignmentModel model) {
		super.updateView(action, record, model);

		record.set(AssignmentModel.Key.WEIGHT.name(), model.getWeighting());
		record.set(AssignmentModel.Key.INCLUDED.name(), model.getIncluded());
		
		/*String property = action.getKey();
		AssignmentModel.Key assignmentModelKey = AssignmentModel.Key.valueOf(property);
		switch (assignmentModelKey) {
		case REMOVED:
		case INCLUDED: 
		case EXTRA_CREDIT: 
			// FIXME: Would it be possible to simply refresh the view at this point? It should achieve the
			// FIXME: same result
			//pagingToolBar.refresh();
			//recalculateEqualWeightingAssignments(model.getCategoryId(), null, false);
		}*/
	}
	
	@Override
	protected void afterUpdateView(UserEntityAction<AssignmentModel> action, Record record, AssignmentModel model) {
		AssignmentModel startModel = action.getModel();
		String originalName = (String)startModel.get(AssignmentModel.Key.NAME.name());
		
		String property = action.getKey();
		action.announce(originalName, property, action.getValue());
		
		// TODO: This should probably be more general -- assumes that there is only one case of a prerequisite action
		if (action.getPrerequisiteAction() != null) {		
			UserChangeEvent categoryUpdateEvent = new UserChangeEvent(action.getPrerequisiteAction());					
			// Note that this action will not be sent back to the server 
			categoriesPanel.fireEvent(GradebookEvents.UserChange, categoryUpdateEvent);
		}
		
		UserChangeEvent event = new UserChangeEvent(action);
		// This event will tell other components that the assignment change succeeded
		SettingsAssignmentContentPanel.this.fireEvent(GradebookEvents.UserChange, event);	
	}
	
	@Override
	protected boolean validateEdit(final RemoteCommand<AssignmentModel> remoteCommand, final UserEntityUpdateAction<AssignmentModel> action, final Record record, final GridEvent gridEvent) {
		
		String property = action.getKey();
		AssignmentModel.Key assignmentModelKey = AssignmentModel.Key.valueOf(property);
		switch (assignmentModelKey) {
		case POINTS:
			
			Dialog verify = new Dialog() {
				@Override
				protected void onButtonPressed(Button button) {
					super.onButtonPressed(button);
				
					if (button == yesBtn) {
						action.setDoRecalculateChildren(Boolean.TRUE);
						doEdit(remoteCommand, action);
						//service.updateEntity(event.getAction(), callback);
						//service.updateAssignment(model.getIdentifier(), AssignmentModel.Key.POINTS, (Double)value, callback);
					} else if (button == noBtn) {
						doEdit(remoteCommand, action);
						//service.updateEntity(event.getAction(), callback);
					} else {
						record.set(action.getKey(), action.getStartValue());
						record.commit(false);
						
						if (gridEvent != null)
							grid.fireEvent(Events.AfterEdit, gridEvent);
					}
				}
			};
			GradebookConstants i18n = Registry.get("i18n");
			verify.addText(i18n.changingPointsRecalculatesGrades());
			verify.setBodyStyleName("pad-text");
			verify.setButtons(Dialog.YESNOCANCEL);
			verify.setHeading("Confirm");
			verify.setHideOnButtonClick(true);
			verify.setLayout(new FitLayout());
			verify.setModal(true);
			verify.setScrollMode(Scroll.AUTO);
			verify.setWidth(300);
			verify.show();
			
			// Since we're going to run the doEdit here by itself, we don't want it to be run when this method returns
			return false;
		case WEIGHT:
			if (gridEvent != null)
				grid.getView().getCell(gridEvent.rowIndex, gridEvent.colIndex).setInnerText("Verifying edit...");
			
			AssignmentModel model = action.getModel();
			final CategoryModel categoryModel = categoriesPanel.getStore().findModel(CategoryModel.Key.ID.name(), String.valueOf(model.getCategoryId()));
			
			if (categoryModel != null && categoryModel.getEqualWeightAssignments() != null && categoryModel.getEqualWeightAssignments().booleanValue()) {
				
				final Listener<WindowEvent> l = new Listener<WindowEvent>() { 
					
					public void handleEvent(WindowEvent ce) {  
						Dialog dialog = (Dialog) ce.component;  
						Button btn = dialog.getButtonPressed();
						
						if (btn.getItemId().equals(Dialog.YES)) {
							AssignmentModel model = (AssignmentModel)record.getModel();
							record.commit(false);
							
							/*UserEntityUpdateAction<CategoryModel> categoryUpdateAction = 
								new UserEntityUpdateAction<CategoryModel>(gradebookUid, 
										categoryModel, CategoryModel.Key.EQUAL_WEIGHT.name(), 
										ClassType.BOOLEAN, Boolean.FALSE, Boolean.TRUE);
										
							// Indicates that this is UI/DOM/Browser information only
							categoryUpdateAction.setSendToServer(false);
							
							action.setPrerequisiteAction(categoryUpdateAction);
							*/
							
							doEdit(remoteCommand, action);
							//categoriesPanel.updateView(categoryUpdateAction, record, gridEvent);
							//service.updateEntity(event.getAction(), callback);
			
							//service.updateAssignment(model.getIdentifier(), AssignmentModel.Key.WEIGHT, (Double)value, callback);
							//EqualWeightEvent evt = new EqualWeightEvent(model.getCategoryId(), ItemType.CATEGORY, false);
							//categoriesPanel.fireEvent(GradebookEvents.EqualWeight, evt); 
						} else {
							record.set(action.getKey(), action.getStartValue());
							record.commit(false);
						}

					}  
				};
				
				MessageBox.confirm("Confirm", "This category has equal weighting for all assignments. If you want to edit the individual assignment weights then it will be necessary to uncheck the equal weighting field on the category. Do you want to do this?", l);
				
				return false;
			}
			
			RefreshCourseGradesEvent cwe = new RefreshCourseGradesEvent();
			fireEvent(GradebookEvents.RefreshCourseGrades, cwe);
			break;
		default:
			if (gridEvent != null)
				grid.getView().getCell(gridEvent.rowIndex, gridEvent.colIndex).setInnerText("Saving edit...");
			break;
		};
		
		return true;
	}
}
