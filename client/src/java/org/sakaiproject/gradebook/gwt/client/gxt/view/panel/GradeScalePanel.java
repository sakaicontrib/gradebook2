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
import java.util.EnumSet;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.RestBuilder;
import org.sakaiproject.gradebook.gwt.client.RestBuilder.Method;
import org.sakaiproject.gradebook.gwt.client.gxt.NewModelCallback;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaButton;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradeMapUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ItemUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityOverlay;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.TreeView;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.key.GradeFormatKey;
import org.sakaiproject.gradebook.gwt.client.model.key.GradeMapKey;
import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;

public class GradeScalePanel extends GradebookPanel {
	
	private ListLoader<ListLoadResult<ModelData>> loader;
	private ListLoader<ListLoadResult<ModelData>> gradeFormatLoader;
	private ListStore<ModelData> gradeFormatStore;
	
	private ComboBox<ModelData> gradeFormatListBox;
	private EditorGrid<ModelData> grid;
	private ToolBar toolbar;
	
	private Long currentGradeScaleId;
	
	@SuppressWarnings("unchecked")
	public GradeScalePanel(boolean isEditable, final TreeView treeView) {
		super();
		
		toolbar = new ToolBar();
		
		LabelField gradeScale = new LabelField(i18n.gradeFormatLabel());
		toolbar.add(gradeScale);
		
		gradeFormatLoader = RestBuilder.getDelayLoader(AppConstants.LIST_ROOT, 
				EnumSet.allOf(GradeFormatKey.class), Method.GET, null, null,
				GWT.getModuleBaseURL(), 
				AppConstants.REST_FRAGMENT, AppConstants.GRADE_FORMAT_FRAGMENT);

		gradeFormatLoader.setRemoteSort(true);

		gradeFormatStore = new ListStore<ModelData>(gradeFormatLoader);
		gradeFormatStore.setModelComparer(new EntityModelComparer<ModelData>(GradeFormatKey.L_ID.name()));
		
		gradeFormatListBox = new ComboBox<ModelData>(); 
		gradeFormatListBox.setAllQuery(null);
		gradeFormatListBox.setEditable(false);
		gradeFormatListBox.setFieldLabel("Grade Format");
		gradeFormatListBox.setDisplayField(GradeFormatKey.S_NM.name());  
		gradeFormatListBox.setStore(gradeFormatStore);
		gradeFormatListBox.setForceSelection(true);
		gradeFormatListBox.setLazyRender(true);

		gradeFormatListBox.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				Gradebook selectedGradebookModel = Registry.get(AppConstants.CURRENT);
				Item selectedItemModel = selectedGradebookModel.getGradebookItemModel();
				ModelData gradeFormatModel = se.getSelectedItem();
				
				currentGradeScaleId = gradeFormatModel == null ? null : (Long)gradeFormatModel.get(GradeFormatKey.L_ID.name());
				
				if (currentGradeScaleId != null && !currentGradeScaleId.equals(selectedItemModel.getGradeScaleId())) {
					Record record = treeView.getTreeStore().getRecord((ItemModel)selectedItemModel);
					record.beginEdit();
					record.set(ItemKey.L_GRD_SCL_ID.name(), currentGradeScaleId);
					grid.mask();
					ItemUpdate itemUpdate = new ItemUpdate(treeView.getTreeStore(), record, selectedItemModel, false);
					itemUpdate.property = ItemKey.L_GRD_SCL_ID.name();
					Dispatcher.forwardEvent(GradebookEvents.UpdateItem.getEventType(), itemUpdate);
				} else {
					loader.load();
				}
			}
			
		});
		
		toolbar.add(gradeFormatListBox);
		
		setTopComponent(toolbar);
		
		gradeFormatLoader.addListener(Loader.Load, new Listener<LoadEvent>() {

			public void handleEvent(LoadEvent be) {
				loadIfPossible();
			}
			
		});

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		
		// Currently, the default number format is #.#####
		NumberFormat defaultNumberFormat = DataTypeConversionUtil.getDefaultNumberFormat();

		ColumnConfig column = new ColumnConfig();  
		column.setId(GradeMapKey.S_LTR_GRD.name());  
		column.setHeader(i18n.letterGradeHeader());
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setWidth(100);
		column.setGroupable(false);
		column.setMenuDisabled(true);
		column.setSortable(false);
		configs.add(column); 
		
		column = new ColumnConfig();  
		column.setId(GradeMapKey.D_FROM.name());  
		column.setHeader(i18n.fromHeader());
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setWidth(70);
		column.setGroupable(false);
		column.setMenuDisabled(true);
		column.setSortable(false);
		column.setNumberFormat(defaultNumberFormat);
		if (isEditable) {
			NumberField numberField = new NumberField();
			numberField.addInputStyleName(resources.css().gbNumericFieldInput());
			//numberField.setMaxValue(Double.valueOf(100d));
			numberField.setFormat(defaultNumberFormat);
			column.setEditor(new CellEditor(numberField));
		}
		configs.add(column);
		
		column = new ColumnConfig();  
		column.setId(GradeMapKey.D_TO.name());
		column.setHeader(i18n.toHeader());
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setWidth(100);
		column.setGroupable(false);
		column.setMenuDisabled(true);
		column.setSortable(false);
		column.setNumberFormat(defaultNumberFormat);
		configs.add(column);

		loader = RestBuilder.getDelayLoader(AppConstants.LIST_ROOT, 
				EnumSet.allOf(GradeMapKey.class), Method.GET, null, null,
				GWT.getModuleBaseURL(), AppConstants.REST_FRAGMENT, AppConstants.GRADE_MAP_FRAGMENT);

		final ListStore<ModelData> store = new ListStore<ModelData>(loader);
		store.setModelComparer(new EntityModelComparer<ModelData>(GradeMapKey.S_LTR_GRD.name()));

		loader.addListener(Loader.Load, new Listener<LoadEvent>() {

			public void handleEvent(LoadEvent be) {
				grid.unmask();
			}
			
		});
		
		final ColumnModel cm = new ColumnModel(configs);
		setBodyBorder(false);
		setHeaderVisible(false);
		setHeading("Selected Grade Mapping");
		setButtonAlign(HorizontalAlignment.RIGHT);
		setLayout(new FitLayout());
		setSize(600, 300);
		
		grid = new EditorGrid<ModelData>(store, cm);  
		grid.setStyleAttribute("borderTop", "none");   
		grid.setBorders(true);
		grid.addListener(Events.ValidateEdit, new Listener<GridEvent>() {

			public void handleEvent(GridEvent ge) {
				
				// By setting ge.doit to false, we ensure that the AfterEdit event is not thrown. Which means we have to throw it ourselves onSuccess
				ge.stopEvent();
				
				final Record record = ge.getRecord();
				// FindBugs
				// String property = ge.getProperty();
				Object newValue = ge.getValue();
				Object originalValue = ge.getStartValue();

				Dispatcher.forwardEvent(GradebookEvents.UpdateGradeMap.getEventType(), new GradeMapUpdate(record, newValue, originalValue));
			}
		});
		
		add(grid); 
		
		Button button = new AriaButton(i18n.close(), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent be) {
				Dispatcher.forwardEvent(GradebookEvents.HideEastPanel.getEventType(), Boolean.FALSE);
			}
			
		});
		
		Button resetButton = new AriaButton(i18n.resetGradingScale(), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				Dispatcher.forwardEvent(GradebookEvents.DeleteGradeMap.getEventType());
			}
			
		}); 
		addButton(resetButton); 
		addButton(button);
	}
	
	public void onFailedToUpdateItem(ItemUpdate itemUpdate) {
		
		// Ensure that the failure is on an attempt to update the GRADESCALEID
		if (itemUpdate.property != null && itemUpdate.property.equals(ItemKey.L_GRD_SCL_ID.name())) {
			
			Long gradeScaleId = itemUpdate.item.get(ItemKey.L_GRD_SCL_ID.name());
		
			if (gradeScaleId != null && currentGradeScaleId != null &&
					!currentGradeScaleId.equals(gradeScaleId)) {
				
				loadGradeScaleData(gradeScaleId);
			}
		}
		
	}
	
	public void onRefreshGradeScale(Gradebook selectedGradebook) {
		loader.load();
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		gradeFormatLoader.load();
	}
	
	
	private void loadGradeScaleData(Long selectedGradeScaleId) {
		for (int i=0;i<gradeFormatStore.getCount();i++) {
			ModelData m = gradeFormatStore.getAt(i);
			Long id1 = m.get(GradeFormatKey.L_ID.name());
			if (id1 != null && id1.equals(selectedGradeScaleId)) {
				if (currentGradeScaleId == null || !currentGradeScaleId.equals(selectedGradeScaleId)) {
					gradeFormatListBox.setValue(m);
				}
				break;
			}
		}
	}
	
	private void loadGradeScaleData(Gradebook selectedGradebook) {
		Long selectedGradeScaleId = selectedGradebook.getGradebookItemModel().getGradeScaleId();
		loadGradeScaleData(selectedGradeScaleId);
	}

	private void loadIfPossible() {
		Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
		
		if (selectedGradebook != null) {
			loadGradeScaleData(selectedGradebook);
		}
	}
	
}

