/**********************************************************************************
 *
 * Copyright (c) 2008, 2009, 2010, 2011 The Regents of the University of California
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.RestBuilder;
import org.sakaiproject.gradebook.gwt.client.RestBuilder.Method;
import org.sakaiproject.gradebook.gwt.client.RestCallback;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaButton;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradeMapUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ItemUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.TreeView;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.NullSensitiveCheckBox;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.key.GradeFormatKey;
import org.sakaiproject.gradebook.gwt.client.model.key.GradeMapKey;
import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;

public class GradeScalePanel extends GradebookPanel implements StatisticsChartLoaderListener {

	private ListLoader<ListLoadResult<ModelData>> loader;
	private ListLoader<ListLoadResult<ModelData>> gradeFormatLoader;
	private ListStore<ModelData> gradeFormatStore;

	private ComboBox<ModelData> gradeFormatListBox;
	private EditorGrid<ModelData> grid;
	private ToolBar toolbar;

	private Long currentGradeScaleId;

	private boolean isEditable;

	private Text letterGradeScaleMessage = new Text(i18n.gradeScaleLetterGradeMessage());

	private Button closeButton;
	private Button resetToDefaultButton;

	private NumberFormat shortNumberFormat = DataTypeConversionUtil.getShortNumberFormat();

	private HorizontalPanel horizontalPanel;

	private StatisticsChartPanel statisticsChartPanel;
	
	private Label instructionLabel;
	
	private CheckBox toggleCheckBox; 
	
	// GRBK-981
	private boolean hasChartUpdates = false;
	
	// GRBK-1013
	private boolean hasGradeScaleUpdates = false;
	
	/*
	 *  GRBK-1071:
	 *  We don't need to get the statistics data if the visualization
	 *  API hasn't been loaded yet.
	 */
	private boolean canGetStatisticsChartData = false;

	public GradeScalePanel(boolean isEditable, final TreeView treeView) {

		super();

		this.isEditable = isEditable;

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
				Boolean isFormatLocked = Boolean.FALSE; 
				Gradebook selectedGradebookModel = Registry.get(AppConstants.CURRENT);
				Item selectedItemModel = selectedGradebookModel.getGradebookItemModel();
				ModelData gradeFormatModel = se.getSelectedItem();

				currentGradeScaleId = gradeFormatModel == null ? null : (Long)gradeFormatModel.get(GradeFormatKey.L_ID.name());

				if (gradeFormatModel != null)
				{
					isFormatLocked = (Boolean) gradeFormatModel.get(GradeFormatKey.B_LK.name());
					// Unsure if this could happen, but will guard it anyways.. 
					if (isFormatLocked == null) 
					{
						isFormatLocked = Boolean.FALSE; 
					}
				}
				if (currentGradeScaleId != null && !currentGradeScaleId.equals(selectedItemModel.getGradeScaleId())) {

					if (isFormatLocked.booleanValue())
					{
						Window.alert(i18n.gradeFormatCannotBeChangedWarning());
						loader.load(); 
					}
					else
					{
						showUserFeedback();
						Record record = treeView.getTreeStore().getRecord((ItemModel)selectedItemModel);
						record.beginEdit();
						record.set(ItemKey.L_GRD_SCL_ID.name(), currentGradeScaleId);
						grid.mask();
						ItemUpdate itemUpdate = new ItemUpdate(treeView.getTreeStore(), record, selectedItemModel, false);
						itemUpdate.property = ItemKey.L_GRD_SCL_ID.name();
						Dispatcher.forwardEvent(GradebookEvents.UpdateItem.getEventType(), itemUpdate);
					}
					

				} else {
					loader.load();
				}
			}

		});

		toolbar.add(gradeFormatListBox);
		
		// GRBK-982 : Adding tool item separator and chart update toggle button
		
		SeparatorToolItem separatorToolItem = new SeparatorToolItem();
		separatorToolItem.addStyleName(resources.css().gbGradeScaleSeparatorToolItem());
		
		
		toolbar.add(separatorToolItem);
		
		final String toolTipOn = i18n.gradeScaleChartUpdateToggleToolTipOn(); 
		final String toolTipOff = i18n.gradeScaleChartUpdateToggleToolTipOff(); 
		
		toggleCheckBox = new NullSensitiveCheckBox(); 
		toggleCheckBox.setToolTip(toolTipOn); 
		toggleCheckBox.setValue(Boolean.TRUE);
		toggleCheckBox.setBoxLabel(i18n.gradeScaleChartUpdateToggle());
		
		toggleCheckBox.addListener(Events.Change, new Listener<FieldEvent>() {
			
			public void handleEvent(FieldEvent be) {
				boolean isChecked = DataTypeConversionUtil.checkBoolean(((CheckBox)be.getField()).getValue());
				if (isChecked) 
				{
					toggleCheckBox.setToolTip(toolTipOn); 
					statisticsChartPanel.unmask();
					if(hasChartUpdates) {
						getStatisticsChartData();
						hasChartUpdates = false;
					}
				}
				else
				{
					toggleCheckBox.setToolTip(toolTipOff); 					
					statisticsChartPanel.mask();
				}
			}
		});
				
	    toolbar.add(toggleCheckBox);
	    
		setTopComponent(toolbar);

		gradeFormatLoader.addListener(Loader.Load, new Listener<LoadEvent>() {

			public void handleEvent(LoadEvent be) {
				loadIfPossible();
			}

		});

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

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
		column.setNumberFormat(shortNumberFormat);
		// GRBK-668: We determine if this columns is editable via setState()
		configs.add(column);

		column = new ColumnConfig();  
		column.setId(GradeMapKey.D_TO.name());
		column.setHeader(i18n.toHeader());
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setWidth(100);
		column.setGroupable(false);
		column.setMenuDisabled(true);
		column.setSortable(false);
		column.setNumberFormat(shortNumberFormat);
		column.setStyle("background-color:#A9A9A9!important;"); // GRBK-874
		
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
		setLayout(new RowLayout());


		grid = new EditorGrid<ModelData>(store, cm);  
		grid.setStyleAttribute("borderTop", "none");   
		grid.setBorders(true);
		grid.setAutoHeight(true);
		grid.addListener(Events.ValidateEdit, new Listener<GridEvent<ModelData>>() {

			public void handleEvent(GridEvent<ModelData> ge) {

				// By setting ge.doit to false, we ensure that the AfterEdit event is not thrown. Which means we have to throw it ourselves onSuccess
				ge.stopEvent();

				final Record record = ge.getRecord();
				Object newValue = ge.getValue();
				Object originalValue = ge.getStartValue();

				Double nValue = (Double) newValue;
				Double oValue = (Double) originalValue;

				// Only update if the user actually changed a grade scale value
				if(null != nValue && nValue.compareTo(oValue) != 0) {
					
					hasGradeScaleUpdates = true;
					
					if(!toggleCheckBox.getValue().booleanValue()) {
						
						hasChartUpdates = true;
					}
					showUserFeedback();
					Dispatcher.forwardEvent(GradebookEvents.UpdateGradeMap.getEventType(), new GradeMapUpdate(record, newValue, originalValue));
				}
			}
		});
		
		closeButton = new AriaButton(i18n.close(), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent be) {
				
				onClose();
				
				Dispatcher.forwardEvent(GradebookEvents.HideEastPanel.getEventType(), Boolean.FALSE);
			}
		});

		resetToDefaultButton = new AriaButton(i18n.resetGradingScale(), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				Dispatcher.forwardEvent(GradebookEvents.DeleteGradeMap.getEventType());
			}
		}); 

		// GRBK-668
		letterGradeScaleMessage.setStyleAttribute("padding", "10px");
		letterGradeScaleMessage.setStyleAttribute("color", "red");
		add(letterGradeScaleMessage);

		horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(grid);
		statisticsChartPanel = new StatisticsChartPanel(this);
		statisticsChartPanel.setLegendPosition(LegendPosition.TOP);
		statisticsChartPanel.setChartWidth(500);
		horizontalPanel.add(statisticsChartPanel);
		horizontalPanel.setSpacing(10);
		add(horizontalPanel);

		// GRBK-874
		instructionLabel = new Label(i18n.gradeScaleInstructions());
		instructionLabel.addStyleName(resources.css().gradeScaleInstructions());
		add(instructionLabel);
		
		addButton(resetToDefaultButton); 
		addButton(closeButton);
		
		// GRBK-959
		setScrollMode(Scroll.AUTO);
	}

	public void onGradeScaleUpdateError() {
		
		hideUserFeedback();
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
		
		hideUserFeedback();
	}
	
	/*
	 * This method is called if GradeScalePanel is closed, set inactive, in the eastCardLayout.
	 * For example, this happens when a grade item edit is started and the ItemFromPanel is shown.
	 */
	public void onClose() {
		
		/*
		 * This is done so when the user closes the grade scale, the checkbox is active
		 * and the chart is rendered the next time they enter the grade scale panel. If we 
		 * don't "reset" the checkbox, then the chart area is grayed out "inactive" the next
		 * time they view the grade scale.
		 */
		toggleCheckBox.setValue(Boolean.TRUE);
		hideUserFeedback();
		refreshCourseGrades();
	}

	public void onRefreshGradeScale(Gradebook selectedGradebook) {

		loader.load();

		// The onRefreshGradeScale method is called after the user selects a grade scale from the ComboBox
		if(toggleCheckBox.getValue().booleanValue()) {
	
			getStatisticsChartData();
		}
	}

	/*
	 * GRBK-668
	 * Method that adjusts the UI according to the grade type
	 */ 
	public void setState() {

		Gradebook gradebookModel = Registry.get(AppConstants.CURRENT);
		Item itemModel = gradebookModel.getGradebookItemModel();
		GradeType gradeType = itemModel.getGradeType();

		if(GradeType.LETTERS == gradeType) {

			letterGradeScaleMessage.show();
			resetToDefaultButton.hide();
			grid.getColumnModel().getColumnById(GradeMapKey.D_FROM.name()).setEditor(null);
		}
		else {

			letterGradeScaleMessage.hide();
			resetToDefaultButton.show();

			if (isEditable) {

				NumberField numberField = new NumberField();
				numberField.addInputStyleName(resources.css().gbNumericFieldInput());
				NumberFormat defaultNumberFormat = DataTypeConversionUtil.getDefaultNumberFormat();
				numberField.setFormat(defaultNumberFormat);
				grid.getColumnModel().getColumnById(GradeMapKey.D_FROM.name()).setEditor(new CellEditor(numberField));
			}
		}

		getStatisticsChartData();
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

	private void getStatisticsChartData() {

		/*
		 *  GRBK-1071
		 *  No need to get the data if the visualization API hasn't been loaded yet
		 */
		if(!canGetStatisticsChartData) {
			
			return;
		}
		
		showUserFeedback();
		
		Gradebook gbModel = Registry.get(AppConstants.CURRENT);

		RestBuilder builder = RestBuilder.getInstance(
				Method.GET,
				GWT.getModuleBaseURL(),
				AppConstants.REST_FRAGMENT,
				AppConstants.STATISTICS_FRAGMENT,
				AppConstants.COURSE_FRAGMENT,
				gbModel.getGradebookUid());


		builder.sendRequest(200, 400, null, new RestCallback() {

			public void onError(Request request, Throwable caught) {

				hideUserFeedback();
				Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(i18n.statisticsDataErrorTitle(), i18n.statisticsDataErrorMsg(), true));
			}

			public void onFailure(Request request, Throwable exception) {

				hideUserFeedback();
				Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(i18n.statisticsDataErrorTitle(), i18n.statisticsDataErrorMsg(), true));
			}

			public void onSuccess(Request request, Response response) {

				/*
				 * The response text contains a sorted linked-list map, where the keys are the letter grades and the values
				 * are the frequency.
				 * e.g. {"F":0, "D-":3, "D":1, "D+":0, "C-":5, "C":0, "C+":1, "B-":0, "B":20, "B+":0, "A-":3, "A":12, "A+":1}
				 * 
				 */
				JSONValue jsonValue = JSONParser.parseStrict(response.getText());
				JSONObject jsonObject = jsonValue.isObject();
				Set<String> keys = jsonObject.keySet();

				// Initialize the DataTable
				DataTable dataTable = statisticsChartPanel.createDataTable();
				dataTable.addColumn(ColumnType.STRING, i18n.statisticsChartLabelDistribution());
				dataTable.addColumn(ColumnType.NUMBER, i18n.statisticsChartLabelFrequency());
				dataTable.addRows(keys.size());

				Iterator<String> iter = keys.iterator();
				int index = 0;
				while(iter.hasNext()) {

					String key = iter.next();
					dataTable.setValue(index, 0, key);
					dataTable.setValue(index, 1, jsonObject.get(key).isNumber().doubleValue());
					index++;
				}

				statisticsChartPanel.show();
				
				hideUserFeedback();
			}
		});
	}
	
	/*
	 * Dispatching showUserFeedback() as well as handle local logic
	 */
	private void showUserFeedback() {

		statisticsChartPanel.showUserFeedback(toggleCheckBox.getValue().booleanValue());
		
		if(toggleCheckBox.getValue().booleanValue()) {
		
			toggleCheckBox.disable();
		}
	}
	
	/*
	 * Dispatching hideUserFeedback() as well as handle local logic
	 */
	private void hideUserFeedback() {
		
		statisticsChartPanel.hideUserFeedback(toggleCheckBox.getValue().booleanValue());
		
		if(toggleCheckBox.getValue().booleanValue()) {
		
			toggleCheckBox.enable();
		}
	}
	
	/*
	 * GRBK-981 : Helper method that send an event to refresh the course grades
	 */
	private void refreshCourseGrades() {

		if(hasGradeScaleUpdates) {
			
			// This used to be done in the ServiceController's onUpdateGradeMap(...) method
			Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
			Dispatcher.forwardEvent(GradebookEvents.RefreshCourseGrades.getEventType(), selectedGradebook);
			hasChartUpdates = false;
			hasGradeScaleUpdates = false;
		}
	}

	/*
	 * Implementing the StatisticsChartLoaderListener API. The StatisticsChartPanel
	 * does calls this method once the visualization API has been loaded. 
	 */
	public void load() {
		
		canGetStatisticsChartData = true;
		getStatisticsChartData();
	}
}


