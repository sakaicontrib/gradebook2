/**********************************************************************************
*
* $Id: SettingsGradingScaleContentPanel.java 6638 2009-01-22 01:27:23Z jrenfro $
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
import org.sakaiproject.gradebook.gwt.client.Gradebook2RPCServiceAsync;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.action.Action.ActionType;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaButton;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.event.UserChangeEvent;
import org.sakaiproject.gradebook.gwt.client.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.model.GradeScaleRecordMapModel;
import org.sakaiproject.gradebook.gwt.client.model.GradeScaleRecordModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadConfig;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GradeScalePanel extends ContentPanel {

	private boolean isEditable;
	
	@SuppressWarnings("unchecked")
	public GradeScalePanel(I18nConstants i18n, boolean isEditable) {
		super();
		this.isEditable = isEditable;
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		
		// Currently, the default number format is #.#####
		NumberFormat defaultNumberFormat = DataTypeConversionUtil.getDefaultNumberFormat();

		ColumnConfig column = new ColumnConfig();  
		column.setId(GradeScaleRecordModel.Key.LETTER_GRADE.name());  
		column.setHeader("Letter Grade");
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setWidth(100);
		column.setGroupable(false);
		column.setMenuDisabled(true);
		column.setSortable(false);
		configs.add(column); 
		
		column = new ColumnConfig();  
		column.setId(GradeScaleRecordModel.Key.FROM_RANGE.name());  
		column.setHeader("From");
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setWidth(70);
		column.setGroupable(false);
		column.setMenuDisabled(true);
		column.setSortable(false);
		column.setNumberFormat(defaultNumberFormat);
		if (isEditable) {
			NumberField numberField = new NumberField();
			numberField.addInputStyleName("gbNumericFieldInput");
			numberField.setMaxValue(Double.valueOf(100d));
			column.setEditor(new CellEditor(numberField));
		}
		configs.add(column);
		
		column = new ColumnConfig();  
		column.setId(GradeScaleRecordModel.Key.TO_RANGE.name());
		column.setHeader("To");
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setWidth(100);
		column.setGroupable(false);
		column.setMenuDisabled(true);
		column.setSortable(false);
		column.setNumberFormat(defaultNumberFormat);
		configs.add(column);
		
		RpcProxy<ListLoadConfig, ListLoadResult<GradeScaleRecordModel>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<GradeScaleRecordModel>>() {
			
			@Override
			protected void load(ListLoadConfig listLoadConfig, AsyncCallback<ListLoadResult<GradeScaleRecordModel>> callback) {
				GradebookModel gbModel = Registry.get(AppConstants.CURRENT);
				Gradebook2RPCServiceAsync service = Registry.get("service");
				service.getPage(gbModel.getGradebookUid(), gbModel.getGradebookId(), EntityType.GRADE_SCALE, null, callback);
			}
			
		};
		
		ListLoader loader = new BaseListLoader(proxy);  

		final ListStore<GradeScaleRecordModel> store = new ListStore<GradeScaleRecordModel>(loader);
		store.setModelComparer(new EntityModelComparer<GradeScaleRecordModel>());

		loader.load();  
		
		final ColumnModel cm = new ColumnModel(configs);


		setBodyBorder(false);
		setHeaderVisible(false);
		setHeading("Selected Grade Mapping");
		setButtonAlign(HorizontalAlignment.RIGHT);
		setLayout(new FitLayout());
		setSize(600, 300);
		
		final EditorGrid<GradeScaleRecordModel> grid = new EditorGrid<GradeScaleRecordModel>(store, cm);  
		grid.setStyleAttribute("borderTop", "none");   
		grid.setBorders(true);
		grid.addListener(Events.ValidateEdit, new Listener<GridEvent>() {

			public void handleEvent(GridEvent ge) {
				
				// By setting ge.doit to false, we ensure that the AfterEdit event is not thrown. Which means we have to throw it ourselves onSuccess
				ge.doit = false;
				
				final Record record = ge.record;
				String property = ge.property;
				Object newValue = ge.value;
				Object originalValue = ge.startValue;
				final GridEvent gridEvent = ge;
				
				if (gridEvent != null) 
					grid.getView().getCell(gridEvent.rowIndex, gridEvent.colIndex).setInnerText("Saving edit...");

				GradeScaleRecordModel model = (GradeScaleRecordModel)record.getModel();
				GradebookModel gbModel = Registry.get(AppConstants.CURRENT);
				//UserEntityUpdateAction<GradeScaleRecordModel> action = 
				//	new UserEntityUpdateAction<GradeScaleRecordModel>(gbModel, model, property, ClassType.DOUBLE, newValue, originalValue);
				
				AsyncCallback<GradeScaleRecordMapModel> callback = 
					new AsyncCallback<GradeScaleRecordMapModel>() {

						public void onFailure(Throwable caught) {
							Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(caught));
						}

						public void onSuccess(GradeScaleRecordMapModel result) {
							
							for(GradeScaleRecordModel baseModel : result.getRecords()) {
								store.update(baseModel);
							}
							
							if (gridEvent != null)
								grid.fireEvent(Events.AfterEdit, gridEvent);
					
							GradeScalePanel.this.fireEvent(GradebookEvents.UserChange.getEventType(), new UserChangeEvent(EntityType.GRADE_SCALE, ActionType.UPDATE));

						}
					
					
				};
				
				Gradebook2RPCServiceAsync service = Registry.get("service");
				
				service.update(new GradeScaleRecordMapModel(gbModel.getGradebookUid(), gbModel.getGradebookId(), model), EntityType.GRADE_SCALE, null, callback);
				
				/*
				RemoteCommand<GradeScaleRecordModel> remoteCommand = 
					new RemoteCommand<GradeScaleRecordModel>() {

						@Override
						public void onCommandListSuccess(UserEntityAction<GradeScaleRecordModel> action, List<GradeScaleRecordModel> result) {
							for(GradeScaleRecordModel baseModel : result) {
								store.update(baseModel);
							}
							
							if (gridEvent != null)
								grid.fireEvent(Events.AfterEdit, gridEvent);
					
							GradeScalePanel.this.fireEvent(GradebookEvents.UserChange.getEventType(), new UserChangeEvent(EntityType.GRADE_SCALE, ActionType.UPDATE));
						}
					
				};
				
				remoteCommand.executeList(action);*/
			}
		});
		
		add(grid); 
		
		Button button = new AriaButton(i18n.close(), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent be) {
				Dispatcher.forwardEvent(GradebookEvents.HideEastPanel.getEventType(), Boolean.FALSE);
			}
			
		});
		addButton(button);
	}
}
