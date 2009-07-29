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
import org.sakaiproject.gradebook.gwt.client.SecureToken;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.ActionType;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaButton;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ItemUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.event.UserChangeEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.view.TreeView;
import org.sakaiproject.gradebook.gwt.client.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.model.GradeFormatModel;
import org.sakaiproject.gradebook.gwt.client.model.GradeScaleRecordMapModel;
import org.sakaiproject.gradebook.gwt.client.model.GradeScaleRecordModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Key;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadConfig;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.AdapterToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GradeScalePanel extends ContentPanel {
	
	private ListLoader<ListLoadConfig> loader;
	private ListLoader<ListLoadConfig> gradeFormatLoader;
	private ListStore<GradeFormatModel> gradeFormatStore;
	
	private ComboBox<GradeFormatModel> gradeFormatListBox;
	private EditorGrid<GradeScaleRecordModel> grid;
	private ToolBar toolbar;
	
	private Long currentGradeScaleId;
	private boolean isLoaded;
	
	@SuppressWarnings("unchecked")
	public GradeScalePanel(I18nConstants i18n, boolean isEditable, final TreeView treeView) {
		super();
		this.isLoaded = false;
		
		toolbar = new ToolBar();
		
		LabelField gradeScale = new LabelField("Grade format: ");
		toolbar.add(new AdapterToolItem(gradeScale));
		
		RpcProxy<ListLoadConfig, ListLoadResult<GradeFormatModel>> gradeFormatProxy = new RpcProxy<ListLoadConfig, ListLoadResult<GradeFormatModel>>() {
			@Override
			protected void load(ListLoadConfig loadConfig, AsyncCallback<ListLoadResult<GradeFormatModel>> callback) {
				Gradebook2RPCServiceAsync service = Registry.get("service");
				GradebookModel model = Registry.get(AppConstants.CURRENT);
				service.getPage(model.getGradebookUid(), model.getGradebookId(), EntityType.GRADE_FORMAT, null, SecureToken.get(), callback);
			}
		};
		
		gradeFormatLoader = 
			new BaseListLoader<ListLoadConfig, ListLoadResult<GradeFormatModel>>(gradeFormatProxy);
		
		gradeFormatLoader.setRemoteSort(true);

		gradeFormatStore = new ListStore<GradeFormatModel>(gradeFormatLoader);
		gradeFormatStore.setModelComparer(new EntityModelComparer<GradeFormatModel>());
		
		gradeFormatListBox = new ComboBox<GradeFormatModel>(); 
		gradeFormatListBox.setAllQuery(null);
		gradeFormatListBox.setEditable(false);
		gradeFormatListBox.setFieldLabel("Grade Format");
		gradeFormatListBox.setDisplayField(GradeFormatModel.Key.NAME.name());  
		gradeFormatListBox.setStore(gradeFormatStore);
		gradeFormatListBox.setForceSelection(true);

		gradeFormatListBox.addSelectionChangedListener(new SelectionChangedListener<GradeFormatModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<GradeFormatModel> se) {
				GradebookModel selectedGradebookModel = Registry.get(AppConstants.CURRENT);
				ItemModel selectedItemModel = selectedGradebookModel.getGradebookItemModel();
				GradeFormatModel gradeFormatModel = se.getSelectedItem();
				
				currentGradeScaleId = Long.valueOf(gradeFormatModel.getIdentifier());
				
				if (currentGradeScaleId != null && !currentGradeScaleId.equals(selectedItemModel.getGradeScaleId())) {
					Record record = treeView.getTreeStore().getRecord(selectedItemModel);
					record.beginEdit();
					record.set(Key.GRADESCALEID.name(), currentGradeScaleId);
					grid.mask();
					Dispatcher.forwardEvent(GradebookEvents.UpdateItem.getEventType(), new ItemUpdate(treeView.getTreeStore(), record, selectedItemModel, false));
				} else {
					loader.load();
				}
			}
			
			
		});
		
		toolbar.add(new AdapterToolItem(gradeFormatListBox));
		
		setTopComponent(toolbar);
		
		gradeFormatLoader.addListener(Loader.Load, new Listener<LoadEvent>() {

			public void handleEvent(LoadEvent be) {
				loadIfPossible();
			}
			
		});
		
		gradeFormatLoader.load();
		
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
				service.getPage(gbModel.getGradebookUid(), gbModel.getGradebookId(), EntityType.GRADE_SCALE, null, SecureToken.get(), callback);
			}
			
		};
		
		loader = new BaseListLoader(proxy);  

		final ListStore<GradeScaleRecordModel> store = new ListStore<GradeScaleRecordModel>(loader);
		store.setModelComparer(new EntityModelComparer<GradeScaleRecordModel>());

		loader.addListener(Loader.Load, new Listener<LoadEvent>() {

			public void handleEvent(LoadEvent be) {
				grid.unmask();
			}
			
		});
		
		loader.load();
		
		final ColumnModel cm = new ColumnModel(configs);


		setBodyBorder(false);
		setHeaderVisible(false);
		setHeading("Selected Grade Mapping");
		setButtonAlign(HorizontalAlignment.RIGHT);
		setLayout(new FitLayout());
		setSize(600, 300);
		
		grid = new EditorGrid<GradeScaleRecordModel>(store, cm);  
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
				UserEntityUpdateAction<StudentModel> action = 
					new UserEntityUpdateAction<StudentModel>(gbModel, null, property, org.sakaiproject.gradebook.gwt.client.action.UserEntityAction.ClassType.DOUBLE, newValue, originalValue);
				
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
				
				service.update(new GradeScaleRecordMapModel(gbModel.getGradebookUid(), gbModel.getGradebookId(), model), EntityType.GRADE_SCALE, action, SecureToken.get(), callback);
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
	
	
	public void onRefreshGradeScale(GradebookModel selectedGradebook) {
		//loadGradeScaleData(selectedGradebook);
		loader.load();
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		//loadIfPossible();
	}
	
	
	private void loadGradeScaleData(GradebookModel selectedGradebook) {
		Long selectedGradeScaleId = selectedGradebook.getGradebookItemModel().getGradeScaleId();
		for (int i=0;i<gradeFormatStore.getCount();i++) {
			GradeFormatModel m = gradeFormatStore.getAt(i);
			if (m.getIdentifier().equals(String.valueOf(selectedGradeScaleId))) {
				gradeFormatListBox.setValue(m);
				currentGradeScaleId = selectedGradeScaleId;
				break;
			}
		}
		this.isLoaded = true;
	}

	private void loadIfPossible() {
		GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
		
		if (selectedGradebook != null) {
			loadGradeScaleData(selectedGradebook);
		}
	}
	
}

