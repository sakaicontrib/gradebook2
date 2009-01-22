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

import org.sakaiproject.gradebook.gwt.client.action.RemoteCommand;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.custom.widget.grid.CustomColumnModel;
import org.sakaiproject.gradebook.gwt.client.gxt.Notifier;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.UserChangeEvent;
import org.sakaiproject.gradebook.gwt.client.model.AssignmentModel;
import org.sakaiproject.gradebook.gwt.client.model.CategoryModel;
import org.sakaiproject.gradebook.gwt.client.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemEntityModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.SummaryColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.SummaryRenderer;
import com.google.gwt.i18n.client.NumberFormat;

public class SettingsCategoryContentPanel extends SettingsGridPanel<CategoryModel> {

	private ItemCheckColumnConfig equalWeightColumn;
	
	public SettingsCategoryContentPanel(String gradebookUid) {
		super(gradebookUid, "categorygrid", EntityType.CATEGORY);
		
		/*if (gbModel.getCategoryType() == CategoryType.SIMPLE_CATEGORIES) {
			recalculateEqualWeightingCategories(store);
		}
		
		TextToolItem equalWeightItem = new TextToolItem("Set equal weighting");
		
		equalWeightItem.addListener(Events.Select, new Listener<ToolBarEvent>() {

			public void handleEvent(ToolBarEvent be) {
				recalculateEqualWeightingCategories(store);
			}
			
		});
		
		pagingToolBar.add(equalWeightItem);
		*/
		
		addListener(GradebookEvents.UserChange, new Listener<UserChangeEvent>() {

			public void handleEvent(UserChangeEvent uce) {
				UserEntityAction action = (UserEntityAction)uce.getAction();
				
				switch (action.getEntityType()) {
				case GRADE_ITEM:
					switch (action.getActionType()) {
					case UPDATE:
						UserEntityUpdateAction updateAction = (UserEntityUpdateAction)action;
						AssignmentModel.Key assignmentKey = AssignmentModel.Key.valueOf(updateAction.getKey());
						switch (assignmentKey) {
						case WEIGHT:
							Object value = updateAction.getValue();
							Object startValue = updateAction.getStartValue();
							if (startValue == null 
									|| ! value.equals(startValue)) {
								reloadData();
							}
							break;
						}
						
						break;
					}
					break;
				case CATEGORY:
					switch (action.getActionType()) {
					case CREATE:
						pagingToolBar.refresh();
						break;
					case UPDATE:
						/*UserEntityUpdateAction<CategoryModel> updateAction = (UserEntityUpdateAction<CategoryModel>)action;
						if (!action.isSendToServer()) {
							// Therefore, we only need to update the UI
							CategoryModel model = updateAction.getModel(); //getStore().findModel(CategoryModel.Key.ID.name(), updateAction.getEntityId());
							if (model != null) {
								Record record = getStore().getRecord(model);
								record.set(updateAction.getKey(), action.getValue());
								record.commit(false);
							}
						}*/
						break;
					}
					
					
					break;
				case GRADEBOOK:
					switch (action.getActionType()) {
					case UPDATE:
						UserEntityUpdateAction updateAction = (UserEntityUpdateAction)action;
						
						GradebookModel.Key gradebookKey = GradebookModel.Key.valueOf(updateAction.getKey());
						
						
						switch (gradebookKey) {
						case CATEGORYTYPE:
							CategoryType categoryType = (CategoryType) updateAction.getValue();
							
							switch (categoryType) {
							case WEIGHTED_CATEGORIES:
								weightColumn.setHidden(false);
								equalWeightColumn.setHidden(false);
								break;
							default:
								weightColumn.setHidden(true);
								equalWeightColumn.setHidden(true);
								break;
							}
							
							if (isRendered()) {
								if (grid != null && grid.getView() != null)
									grid.getView().refresh(true);
							}
							
							break;
						}
						
						break;
					}
				}
			}
			
		});
		
	}
	
	/*public void recalculateEqualWeightingCategories(final ListStore<CategoryModel> store) {
		GradebookToolFacadeAsync service = Registry.get("service");
		GradebookModel gbModel = Registry.get(gradebookUid);
		NotifyingAsyncCallback<List<CategoryModel>> callback 
			= new NotifyingAsyncCallback<List<CategoryModel>>() {

			public void onSuccess(List<CategoryModel> models) {
				
				if (models != null && !models.isEmpty()) {
					Object[] values = new Object[2];
					values[0] = String.valueOf(models.size());
					for (CategoryModel model : models) {
						CategoryModel oldModel = store.findModel(model);
						Record r = store.getRecord(oldModel);
						r.set(CategoryModel.Key.WEIGHT.name(), model.getWeighting());
						r.setDirty(true);
						r.commit(false);
						if (values[1] == null)
							values[1] = defaultNumberFormat.format(model.getWeighting().doubleValue());
					}
					
					notifier.notify("Recalculate", 
							"Recalculated all ({0}) included, non-extra credit category weightings to '{1}' ", 
							values);
					fireEvent(GradebookEvents.RefreshCourseGrades, new RefreshCourseGradesEvent());
				}
			
			}
			
		};
		service.recalculateEqualWeightingCategories(gradebookUid, gbModel.getGradebookId(), Boolean.TRUE, callback);
	}*/
	
	public void reloadData() {
		pagingToolBar.refresh();
	}
	
	@Override
	protected void addGridListenersAndPlugins(EditorGrid<CategoryModel> grid) {
		super.addGridListenersAndPlugins(grid);
		
		grid.addPlugin(equalWeightColumn);
	}
	
	protected CustomColumnModel newColumnModel() {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();  
		
		SummaryColumnConfig gbColumn = new SummaryColumnConfig(CategoryModel.Key.GRADEBOOK.name(), 
				ItemEntityModel.getPropertyName(AssignmentModel.Key.GRADEBOOK), 150);
		gbColumn.setGroupable(true);
		gbColumn.setSortable(false);
		columns.add(gbColumn);

		nameColumn.setSummaryRenderer(new SummaryRenderer() {
			public String render(Double value, Map<String, Double> data) {
				return "<div style=\"color:darkgray;font-weight:bold;font-size:10pt\">Total:</div>";
			}
		});
		
		columns.add(nameColumn);
		
		columns.add(includedColumn);
		
		columns.add(weightColumn);
		
		columns.add(extraCreditColumn);
		
		equalWeightColumn =  new ItemCheckColumnConfig(CategoryModel.Key.EQUAL_WEIGHT.name(), 
				ItemEntityModel.getPropertyName(AssignmentModel.Key.EQUAL_WEIGHT), 150);
		equalWeightColumn.setGroupable(false);
		equalWeightColumn.setMenuDisabled(true);
		equalWeightColumn.setSortable(false);
		columns.add(equalWeightColumn);
		
		NumberFormat dropLowestNumberFormat = NumberFormat.getFormat("#");
		SummaryColumnConfig dropLowestColumn = new SummaryColumnConfig(CategoryModel.Key.DROP_LOWEST.name(), 
				ItemEntityModel.getPropertyName(AssignmentModel.Key.DROP_LOWEST), 180);
		NumberField dropLowestField = new NumberField();
		dropLowestField.addInputStyleName("gbNumericFieldInput");
		dropLowestField.setAllowDecimals(false);
		dropLowestField.setMinValue(0.0);
		dropLowestField.setPropertyEditorType(Integer.class);
		dropLowestField.setFormat(dropLowestNumberFormat);
		dropLowestField.setSelectOnFocus(true);
		dropLowestColumn.setAlignment(HorizontalAlignment.CENTER);
		dropLowestColumn.setEditor(new CellEditor(dropLowestField));
		dropLowestColumn.setGroupable(false);
		dropLowestColumn.setMenuDisabled(true);
		dropLowestColumn.setSortable(false);
		columns.add(dropLowestColumn);
		
		columns.add(removedColumn);
		
		GradebookModel gbModel = Registry.get(gradebookUid);
		switch (gbModel.getCategoryType()) {
		case WEIGHTED_CATEGORIES:
			weightColumn.setHidden(false);
			equalWeightColumn.setHidden(false);
			break;
		default:
			weightColumn.setHidden(true);
			equalWeightColumn.setHidden(true);
			break;
		}
		
		CustomColumnModel columnModel = new CustomColumnModel(gradebookUid, gridId, columns);
		return columnModel;
	}
	
	@Override 
	protected ListStore<CategoryModel> newStore(BasePagingLoader<PagingLoadConfig, PagingLoadResult<CategoryModel>> loader) {
		GroupingStore<CategoryModel> store = (GroupingStore<CategoryModel>)super.newStore(loader);
		store.groupBy(CategoryModel.Key.GRADEBOOK.name());
		store.setModelComparer(new EntityModelComparer<CategoryModel>());
		return store;
	}
	
	/*private Grid<CategoryModel> buildCategoryGrid() {
		final GradebookToolFacadeAsync service = Registry.get("service");
		
		RpcProxy<PagingLoadConfig, PagingLoadResult<CategoryModel>> proxy = new RpcProxy<PagingLoadConfig, PagingLoadResult<CategoryModel>>() {
			@Override
			protected void load(PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<CategoryModel>> callback) {
				GradebookModel model = Registry.get(gradebookUid);
				boolean showDeleted = showDeletedItems != null && showDeletedItems.isPressed();
				service.getCategories(model.getGradebookUid(), model.getGradebookId(), showDeleted, loadConfig, callback);
			}
		};
		
		final BasePagingLoader<PagingLoadConfig, PagingLoadResult<CategoryModel>> loader = 
			new BasePagingLoader<PagingLoadConfig, PagingLoadResult<CategoryModel>>(proxy, new ModelReader<PagingLoadConfig>());
		
		loader.setRemoteSort(true);
		loader.load(0, pageSize);
		

		toolBar = new PagingToolBar(pageSize);
		toolBar.bind(loader);
		
		showDeletedItems = new ToggleToolItem("Show Deleted") {
			
			@Override
			protected void onButtonToggle(ButtonEvent be) {
				super.onButtonToggle(be);
				
				loader.load(0, pageSize);
			}
			
		};
		toolBar.add(showDeletedItems);

		setTopComponent(toolBar);
		
		store = new GroupingStore<CategoryModel>(loader);   
		store.groupBy(CategoryModel.Key.GRADEBOOK.name());
		store.setModelComparer(new CategoryModelComparer());
		
		categoryColumnModel = newColumnModel();

		GroupSummaryView summary = new GroupSummaryView() {
			protected Map<String, Object> calculate(List<ModelData> models, List<ColumnData> cs) {
			    Map<String, Object> data = new HashMap<String, Object>();

			    for (int j = 0, jlen = models.size(); j < jlen; j++) {
			      ModelData m = models.get(j);
			      for (int i = 0, len = cs.size(); i < len; i++) {
			        ColumnData c = cs.get(i);
			        SummaryColumnConfig cf = (SummaryColumnConfig) cm.getColumn(i);
			        if (cf.getSummaryType() != null) {
			        	boolean isExtraCreditChecked = m.get(CategoryModel.Key.EXTRA_CREDIT.name()) == null ? false : ((Boolean)m.get(AssignmentModel.Key.EXTRA_CREDIT.name())).booleanValue();
			        	if (!c.name.equals(CategoryModel.Key.WEIGHT.name()) || !isExtraCreditChecked) 
			        		data.put(c.name, cf.getSummaryType().render(data.get(c.name), m, c.name, data));
			        	else {
			        		String ecName = CategoryModel.Key.WEIGHT.name() + ":ec";
			        		data.put(ecName, SummaryType.SUM.render(data.get(ecName), m, CategoryModel.Key.WEIGHT.name(), data));
			        	}
			        }
			      }
			    }
			    return data;
			}
			
			protected String renderSummary(Map<String, Double> data, List<ColumnData> cs) {
			    StringBuilder buf = new StringBuilder();
			    int last = cs.size() - 1;
			    for (int i = 0, len = cs.size(); i < len; i++) {
			      ColumnData c = cs.get(i);
			      SummaryColumnConfig cf = (SummaryColumnConfig) cm.getColumn(i);
			      Params p = new Params();
			      p.set("id", c.id);
			      p.set("style", c.style);
			      String css = i == 0 ? "x-grid3-cell-first " : (i == last ? "x-grid3-cell-last " : "");
			      p.set("css", css);
			      if (cf.getSummaryFormat() != null) {
			        p.set("value", cf.getSummaryFormat().format(data.get(c.name)));
			      } else if (cf.getSummaryRenderer() != null) {
			    	  String ecName = CategoryModel.Key.WEIGHT.name() + ":ec";
			    	  if (c.name.equals(CategoryModel.Key.WEIGHT.name()) && data.get(ecName) != null) {
			    		  p.set("value", cf.getSummaryRenderer().render(data.get(c.name), data) + " + " + ecRenderer.render(data.get(ecName), data));
			    	  } else 
			    		  p.set("value", cf.getSummaryRenderer().render(data.get(c.name), data));
			      } else {
			        p.set("value", data.get(c.name));
			      }
			      buf.append(cellTpl.applyTemplate(p));

			    }
			    Params rp = new Params();
			    rp.set("tstyle", "width:" + getTotalWidth() + ";");
			    rp.set("cells", buf.toString());

			    return rowTpl.applyTemplate(rp);
			}
			
			private SummaryRenderer ecRenderer = new SummaryRenderer() {
				public String render(Double value, Map<String, Double> data) {
					
					double v = value == null ? 0.0 : value.doubleValue();
					NumberFormat f = NumberFormat.getFormat("#");
					String s = f.format(v);
					
					return "<div style=\"color:green;font-weight:bold\">" + s + "%</div>";
				}
			};
		};

		summary.setAutoFill(true);
		summary.setShowGroupedColumn(false);
		
		CategoryCheckColumnConfig includedColumn = (CategoryCheckColumnConfig)categoryColumnModel.getColumnById(CategoryModel.Key.INCLUDED.name());
		CategoryCheckColumnConfig extraCreditColumn = (CategoryCheckColumnConfig)categoryColumnModel.getColumnById(CategoryModel.Key.EXTRA_CREDIT.name());
		CategoryCheckColumnConfig equalWeightColumn = (CategoryCheckColumnConfig)categoryColumnModel.getColumnById(CategoryModel.Key.EQUAL_WEIGHT.name());
		CategoryCheckColumnConfig removedColumn = (CategoryCheckColumnConfig)categoryColumnModel.getColumnById(CategoryModel.Key.REMOVED.name());

		categoryGrid = new EditorGrid<CategoryModel>(store, categoryColumnModel);
		categoryGrid.addPlugin(includedColumn);
		categoryGrid.addPlugin(extraCreditColumn);
		categoryGrid.addPlugin(equalWeightColumn);
		categoryGrid.addPlugin(removedColumn);
		categoryGrid.setBorders(true);
		categoryGrid.setLoadMask(true);
		categoryGrid.setStripeRows(true);
		categoryGrid.setTrackMouseOver(true);
		categoryGrid.setView(summary);
		
		categoryGrid.addListener(Events.ValidateEdit, new Listener<GridEvent>() {

			public void handleEvent(final GridEvent ge) {
				// By setting ge.doit to false, we ensure that the AfterEdit event is not thrown. Which means we have to throw it ourselves onSuccess
				ge.doit = false;
				
				makeChange(ge.record, ge.property, ge.value, ge.startValue, ge);				
			}
		});
		
		return categoryGrid;
	}
	
	private void makeChange(final Record record, final String property, final Object value, 
			final Object startValue, final GridEvent gridEvent) {
		
		if (gridEvent != null) 
			categoryGrid.getView().getCell(gridEvent.rowIndex, gridEvent.colIndex).setInnerText("Saving edit...");
		
		GradebookToolFacadeAsync service = Registry.get("service");
		CategoryModel model = (CategoryModel)record.getModel();
		
		UserEntityUpdateAction action = new UserEntityUpdateAction(EntityType.CATEGORY, 
				model.getIdentifier(), property, value, startValue);
		final UserChangeEvent<UserEntityUpdateAction> event = new UserChangeEvent<UserEntityUpdateAction>(action);
		
		NotifyingAsyncCallback<CategoryModel> callback = 
			new NotifyingAsyncCallback<CategoryModel>(event) {

			public void onSuccess(CategoryModel result) {
				record.set(property, result.get(property));
				record.set(CategoryModel.Key.WEIGHT.name(), result.get(CategoryModel.Key.WEIGHT.name()));
				
				boolean isRefreshNecessary = false;
				CategoryModel.Key categoryModelKey = CategoryModel.Key.valueOf(event.getAction().getKey());
				switch (categoryModelKey) {
				case REMOVED:
					isRefreshNecessary = true;
				case INCLUDED: 
				case EXTRA_CREDIT: 
					GradebookModel gbModel = Registry.get(gradebookUid);
					if (gbModel.getCategoryType() == CategoryType.EQUAL_CATEGORIES) {
						recalculateEqualWeightingCategories(categoryGrid.getStore());
					}
					break;
				case EQUAL_WEIGHT:
					boolean isSetEqualWeight = record.get(property) == null ? false : ((Boolean)record.get(property)).booleanValue();
					if (isSetEqualWeight) {
						// TODO: Not sure how to replace this with the new event strategy
						EqualWeightEvent evt = new EqualWeightEvent(result.getIdentifier(), ItemType.CATEGORY, true);
						SettingsCategoryContentPanel.this.fireEvent(GradebookEvents.EqualWeight, evt);
					} 
					break;
				}
				
				if (isRefreshNecessary) {
					if (showDeletedItems == null || !showDeletedItems.isPressed())
						toolBar.refresh();
				}
				
				if (gridEvent != null)
					categoryGrid.fireEvent(Events.AfterEdit, gridEvent);
				
				notifier.notify("Update", 
						"Updated category '{0}', set '{1}' to '{2}' ", 
						result.getName(), property, value);
				
				// This event will tell other components that the assignment change succeeded
				SettingsCategoryContentPanel.this.fireEvent(GradebookEvents.UserChange, getEvent());
			}
			
		};
		
		service.updateEntity(event.getAction(), callback);

	}
	
	public class CategoryCheckColumnConfig extends SummaryCheckColumnConfig {
		
		public CategoryCheckColumnConfig(String id, String name, int width) {
			super(id, name, width);
		}
		
		@Override
		protected void changeValue(final Record record, final String property, Boolean value, Boolean startValue) {
			//makeChange(record, property, value, startValue, null);
			validateEdit(record, property, value, startValue, null);
		}

	}


	public GroupingStore<CategoryModel> getStore() {
		return store;
	}*/
	
	/*@Override
	protected void doCall(GradebookModel model,
			GradebookToolFacadeAsync service, boolean showDeleted,
			AsyncCallback<PagingLoadResult<CategoryModel>> callback) {
		
		service.getCategories(model.getGradebookUid(), model.getGradebookId(), showDeleted, loadConfig, callback);
	}*/
	
	@Override
	protected void updateView(UserEntityAction<CategoryModel> action, Record record, CategoryModel model) {
		super.updateView(action, record, model);
		
		record.set(CategoryModel.Key.WEIGHT.name(), model.get(CategoryModel.Key.WEIGHT.name()));
		
		boolean isRefreshNecessary = false;
		String property = action.getKey();
		CategoryModel.Key categoryModelKey = CategoryModel.Key.valueOf(property);
		switch (categoryModelKey) {
		case REMOVED:
			if (showDeletedItems == null || !showDeletedItems.isPressed())
				isRefreshNecessary = true;
		/*	
		case INCLUDED: 
		case EXTRA_CREDIT: 
			GradebookModel gbModel = Registry.get(gradebookUid);
			if (gbModel.getCategoryType() == CategoryType.SIMPLE_CATEGORIES) {
				recalculateEqualWeightingCategories(grid.getStore());
			}*/
			break;
		/*case EQUAL_WEIGHT:
			boolean isSetEqualWeight = record.get(property) == null ? false : ((Boolean)record.get(property)).booleanValue();
			if (isSetEqualWeight) {
				// TODO: Not sure how to replace this with the new event strategy
				EqualWeightEvent evt = new EqualWeightEvent(Long.valueOf(model.getIdentifier()), ItemType.CATEGORY, true);
				SettingsCategoryContentPanel.this.fireEvent(GradebookEvents.EqualWeight, evt);
			} 
			break;*/
		}
		
		if (isRefreshNecessary) {
			pagingToolBar.refresh();
		}
	}
	
	@Override
	protected boolean validateEdit(RemoteCommand<CategoryModel> remoteCommand, 
			UserEntityUpdateAction<CategoryModel> action, Record record, final GridEvent gridEvent) {
		return true;
	}
}
