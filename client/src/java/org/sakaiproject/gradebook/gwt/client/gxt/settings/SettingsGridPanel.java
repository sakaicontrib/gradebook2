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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.GradebookToolFacadeAsync;
import org.sakaiproject.gradebook.gwt.client.action.PageRequestAction;
import org.sakaiproject.gradebook.gwt.client.action.RemoteCommand;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.custom.widget.grid.CustomGroupSummaryView;
import org.sakaiproject.gradebook.gwt.client.gxt.GridPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.NotifyingAsyncCallback;
import org.sakaiproject.gradebook.gwt.client.model.AssignmentModel;
import org.sakaiproject.gradebook.gwt.client.model.CategoryModel;
import org.sakaiproject.gradebook.gwt.client.model.EntityModel;
import org.sakaiproject.gradebook.gwt.client.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemEntityModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.util.Params;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.grid.SummaryColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.SummaryRenderer;
import com.extjs.gxt.ui.client.widget.grid.SummaryType;
import com.extjs.gxt.ui.client.widget.toolbar.ToggleToolItem;
import com.google.gwt.i18n.client.NumberFormat;


public abstract class SettingsGridPanel<M extends ItemEntityModel> extends GridPanel<M> {

	protected ToggleToolItem showDeletedItems;
	
	protected CellEditor defaultNumericCellEditor;
	protected CellEditor defaultTextCellEditor;
	
	protected NumberField defaultNumberField;
	protected TextField<String> defaultTextField;
	
	protected SummaryColumnConfig nameColumn;
	protected ItemCheckColumnConfig includedColumn;
	protected SummaryColumnConfig weightColumn;
	protected ItemCheckColumnConfig extraCreditColumn;
	protected ItemCheckColumnConfig removedColumn;
	
	
	protected GridCellRenderer<M> textCellRenderer = new GridCellRenderer<M>() {

		public String render(M model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<M> store) {
			
			String value = model.get(property);
			
			if (value == null)
				return "&nbsp;";
			
			Boolean isRemoved = model.getRemoved();
			
			if (isRemoved != null && isRemoved.booleanValue()) {
				return "<div style=\"color:darkgray; text-decoration: line-through;\">" + value + "</div>";
			}
			
			return value;
		}
	};
	
	public SettingsGridPanel(String gradebookUid, String gridId, EntityType entityType) {
		super(gradebookUid, gridId, entityType);

		setTopComponent(pagingToolBar);
	}
	
	public void reloadWeights(ItemEntityModel.Key key, M changedModel) {
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
					M serverModel = (M)model;
					Double weight = serverModel.getWeighting();
					
					M actualModel = store.findModel(serverModel);
				
					if (actualModel.getWeighting() == null || !actualModel.getWeighting().equals(weight))
						actualModel.setWeighting(weight);
					
					store.update(actualModel);
				}
				
			}
			
		});
	}
	
	//protected abstract void doCall(GradebookModel model, GradebookToolFacadeAsync service, boolean showDeleted, AsyncCallback<PagingLoadResult<M>> callback);
	
	@Override
	protected void addComponents() {
		
		this.defaultTextField = new TextField<String>();
		this.defaultTextField.addInputStyleName("gbTextFieldInput");
		this.defaultNumberField = new NumberField();
		this.defaultNumberField.addInputStyleName("gbNumericFieldInput");
		this.defaultNumberField.setFormat(defaultNumberFormat);
		this.defaultNumberField.setSelectOnFocus(true);
		this.defaultTextCellEditor = new CellEditor(defaultTextField);
		this.defaultNumericCellEditor = new CellEditor(defaultNumberField);
		
		showDeletedItems = new ToggleToolItem("Show Deleted") {
			
			@Override
			protected void onButtonToggle(ButtonEvent be) {
				super.onButtonToggle(be);
				
				loader.load(0, pageSize);
			}
			
		};
		pagingToolBar.add(showDeletedItems);
		
		nameColumn = new SummaryColumnConfig(CategoryModel.Key.NAME.name(), 
				ItemEntityModel.getPropertyName(CategoryModel.Key.NAME), 260);
		nameColumn.setEditor(defaultTextCellEditor);
		nameColumn.setGroupable(false);
		nameColumn.setMenuDisabled(true);
		nameColumn.setRenderer(textCellRenderer);
		nameColumn.setSortable(false);
		nameColumn.setSummaryType(SummaryType.COUNT);
		
		includedColumn =  new ItemCheckColumnConfig(CategoryModel.Key.INCLUDED.name(), 
				ItemEntityModel.getPropertyName(CategoryModel.Key.INCLUDED), 150);
		includedColumn.setGroupable(false);
		includedColumn.setMenuDisabled(true);
		includedColumn.setSortable(false);
		
		GradebookModel gbModel = Registry.get(gradebookUid);		
		weightColumn =  new SummaryColumnConfig(AssignmentModel.Key.WEIGHT.name(), 
				ItemEntityModel.getPropertyName(CategoryModel.Key.WEIGHT), 80);
		weightColumn.setAlignment(HorizontalAlignment.RIGHT);
		weightColumn.setEditor(defaultNumericCellEditor);
		weightColumn.setGroupable(false);
		weightColumn.setHidden(gbModel.getCategoryType() == CategoryType.SIMPLE_CATEGORIES);
		weightColumn.setMenuDisabled(true);
		weightColumn.setNumberFormat(defaultNumberFormat);
		weightColumn.setRenderer(textCellRenderer);
		weightColumn.setSortable(false);
		weightColumn.setSummaryType(new SummaryType() {

			@Override
		    public double render(Object v, ModelData m, String field, Map<String, Object> data) {
		      if (v == null) {
		         v= 0d;
		      }
		      double currentVal = m.get(field) == null ? 0.0 : ((Number)m.get(field)).doubleValue();
		      return ((Double) v) + currentVal;
		    }
			
		});
		weightColumn.setSummaryRenderer(new SummaryRenderer() {
			public String render(Double value, Map<String, Double> data) {
				
				double v = value == null ? 0.0 : value.doubleValue();
				String s = defaultNumberFormat.format(v);
				
				if (value != null && (value.compareTo(Double.valueOf(99.999)) < 0 || value.compareTo(Double.valueOf(100.001)) > 0)) 
					return "<div style=\"color:red;font-weight:bold\">" + s + "%</div>";
				
				return "<div style=\"color:darkgray;font-weight:bold\">" + s + "%</div>";
			}
		});
		
		extraCreditColumn =  new ItemCheckColumnConfig(CategoryModel.Key.EXTRA_CREDIT.name(), 
				ItemEntityModel.getPropertyName(CategoryModel.Key.EXTRA_CREDIT), 120);
		extraCreditColumn.setGroupable(false);
		extraCreditColumn.setMenuDisabled(true);
		extraCreditColumn.setSortable(false);
		
		removedColumn = new ItemCheckColumnConfig(CategoryModel.Key.REMOVED.name(), 
				ItemEntityModel.getPropertyName(CategoryModel.Key.REMOVED), 150);
		removedColumn.setGroupable(false);
		removedColumn.setMenuDisabled(true);
		removedColumn.setSortable(false);
	}
	
	@Override
	protected void addGridListenersAndPlugins(EditorGrid<M> grid) {
	
		grid.addPlugin(includedColumn);
		grid.addPlugin(extraCreditColumn);
		grid.addPlugin(removedColumn);
		
	}

	@Override
	protected GridView newGridView() {
		CustomGroupSummaryView summary = new CustomGroupSummaryView() {
			protected Map<String, Object> calculate(List<ModelData> models, List<ColumnData> cs) {
			    Map<String, Object> data = new HashMap<String, Object>();

			    for (int j = 0, jlen = models.size(); j < jlen; j++) {
			      ModelData m = models.get(j);
			      for (int i = 0, len = cs.size(); i < len; i++) {
			        ColumnData c = cs.get(i);
			        SummaryColumnConfig cf = (SummaryColumnConfig) cm.getColumn(i);
			        if (cf.getSummaryType() != null) {
			        	Boolean extraCreditFlag = m.get(ItemEntityModel.Key.EXTRA_CREDIT.name());
			        	Boolean deletedFlag = m.get(ItemEntityModel.Key.REMOVED.name());
			        	Boolean includedFlag = m.get(ItemEntityModel.Key.INCLUDED.name());
			        	
			        	boolean isExtraCreditChecked = extraCreditFlag == null ? false : extraCreditFlag.booleanValue();
			        	boolean isDeleted = deletedFlag == null ? false : deletedFlag.booleanValue();
			        	boolean isIncluded = includedFlag == null ? false : includedFlag.booleanValue();
			        	
			        	if (!isExtraCreditChecked || isDeleted || !isIncluded ||
			        			(!c.name.equals(ItemEntityModel.Key.WEIGHT.name()) 
			        			 && !c.name.equals(ItemEntityModel.Key.POINTS.name()))) 
			        		data.put(c.name, cf.getSummaryType().render(data.get(c.name), m, c.name, data));
			        	else if (c.name.equals(ItemEntityModel.Key.WEIGHT.name())) {
			        		String ecWeight = ItemEntityModel.Key.WEIGHT.name() + ":ec";
			        		data.put(ecWeight, SummaryType.SUM.render(data.get(ecWeight), m, ItemEntityModel.Key.WEIGHT.name(), data));
			        	} else if (c.name.equals(ItemEntityModel.Key.POINTS.name())) {
			        		String ecPoints = ItemEntityModel.Key.POINTS.name() + ":ec";
			        		data.put(ecPoints, SummaryType.SUM.render(data.get(ecPoints), m, ItemEntityModel.Key.POINTS.name(), data));
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
			    	  String ecWeight = ItemEntityModel.Key.WEIGHT.name() + ":ec";
			    	  String ecPoints = ItemEntityModel.Key.POINTS.name() + ":ec";
			    	  
			    	  if (c.name.equals(ItemEntityModel.Key.WEIGHT.name()) && data.get(ecWeight) != null) {
			    		  p.set("value", cf.getSummaryRenderer().render(data.get(c.name), data) + " + " + ecWeightRenderer.render(data.get(ecWeight), data));
			    	  } else if (c.name.equals(ItemEntityModel.Key.POINTS.name()) && data.get(ecPoints) != null) { 
			    		  p.set("value", cf.getSummaryRenderer().render(data.get(c.name), data) + " + " + ecPointsRenderer.render(data.get(ecPoints), data));
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
			
			private SummaryRenderer ecPointsRenderer = new SummaryRenderer() {
				public String render(Double value, Map<String, Double> data) {
					
					double v = value == null ? 0.0 : value.doubleValue();
					NumberFormat f = defaultNumberFormat;
					String s = f.format(v);
					
					return "<div style=\"color:green;font-weight:bold\">" + s + "</div>";
				}
			};
			
			private SummaryRenderer ecWeightRenderer = new SummaryRenderer() {
				public String render(Double value, Map<String, Double> data) {
					
					double v = value == null ? 0.0 : value.doubleValue();
					NumberFormat f = NumberFormat.getFormat("#");
					String s = f.format(v);
					
					return "<div style=\"color:green;font-weight:bold\">" + s + "%</div>";
				}
			};
		};
		summary.setShowGroupedColumn(false);
		summary.setAutoFill(true);
		
		return summary;
	}
	
	/*@Override
	protected BasePagingLoader<PagingLoadConfig, PagingLoadResult<M>> newLoader() {
		RpcProxy<PagingLoadConfig, PagingLoadResult<M>> proxy = new RpcProxy<PagingLoadConfig, PagingLoadResult<M>>() {
			@Override
			protected void load(PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<M>> callback) {
				GradebookToolFacadeAsync service = Registry.get("service");
				GradebookModel model = Registry.get(gradebookUid);
				boolean showDeleted = showDeletedItems != null && showDeletedItems.isPressed();
				doCall(model, service, showDeleted, callback);
			}
		};
		
		BasePagingLoader<PagingLoadConfig, PagingLoadResult<M>> loader = 
			new BasePagingLoader<PagingLoadConfig, PagingLoadResult<M>>(proxy, new ModelReader<PagingLoadConfig>());
		return loader;
	}*/

	@Override
	protected PageRequestAction newPageRequestAction() {
		GradebookModel model = Registry.get(gradebookUid);
		PageRequestAction pageRequestAction = new PageRequestAction(entityType, model.getGradebookUid(), model.getGradebookId());
		
		boolean showDeleted = showDeletedItems != null && showDeletedItems.isPressed();
		// The undelete view is the less restricted view, therefore it's equivalent to include all
		pageRequestAction.setIncludeAll(Boolean.valueOf(showDeleted));
		
		return pageRequestAction;
	}
	
	@Override
	protected ListStore<M> newStore(BasePagingLoader<PagingLoadConfig, PagingLoadResult<M>> loader) {
		GroupingStore<M> store = new GroupingStore<M>(loader);   
		store.setModelComparer(new EntityModelComparer<M>());
		return store;
	}
	
	public class ItemCheckColumnConfig extends SummaryCheckColumnConfig {
		
		public ItemCheckColumnConfig(String id, String name, int width) {
			super(id, name, width);
		}
		
		@Override
		protected void changeValue(final Record record, final String property, Boolean value, Boolean startValue) {
			UserEntityUpdateAction<M> action = newEntityUpdateAction(record, property, value, startValue, null);
			// Since we're not passing a gridEvent on in this case, we need to set the entity name here
			EntityModel model = (EntityModel)record.getModel();
			if (model != null) {
				String entityName = new StringBuilder().append(getHeader())
					.append(" : ").append(model.getDisplayName()).toString();
				action.setEntityName(entityName);
			}
			RemoteCommand<M> remoteCommand = newRemoteCommand(record, property, value, startValue, null);
			doEdit(remoteCommand, action);
		}

	}
}
