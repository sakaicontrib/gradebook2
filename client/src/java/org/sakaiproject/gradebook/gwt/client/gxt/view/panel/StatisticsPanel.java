/**********************************************************************************
 *
 * $Id:$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2008, 2009, 2010 The Regents of the University of California
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.RestBuilder;
import org.sakaiproject.gradebook.gwt.client.RestBuilder.Method;
import org.sakaiproject.gradebook.gwt.client.RestCallback;
import org.sakaiproject.gradebook.gwt.client.UrlArgsCallback;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaButton;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.gxt.model.StatisticsModel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.SectionsComboBox;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.key.SectionKey;
import org.sakaiproject.gradebook.gwt.client.model.key.StatisticsKey;
import org.sakaiproject.gradebook.gwt.client.util.Base64;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
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
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;

public class StatisticsPanel extends ContentPanel {

	private final I18nConstants i18n;

	private ListLoader<ListLoadResult<ModelData>> loader;

	private HorizontalPanel gridAndChartHorizontalPanel;
	
	private StatisticsChartPanel statisticsChartPanel;

	private SectionsComboBox<ModelData> sectionsComboBox;

	private Grid<StatisticsModel> grid = null;

	private DataTable dataTable;

	private int selectedGradeItemRow = -1;
	private String selectedAssignmentId;
	private String selectedSectionId;

	private Map<String, DataTable> dataTableCache = new HashMap<String, DataTable>();

	private final static int FIRST_ROW = 0;

	private final static String COURSE_CACHE_KEY_PREFIX = "course-grade";

	public StatisticsPanel(final I18nConstants i18n) {

		super();

		// Getting needed resources
		this.i18n = i18n;

		// Configure main ContentPanel
		setHeading(i18n.statisticsHeading());
		setFrame(true);
		setBodyBorder(true);
		setButtonAlign(HorizontalAlignment.RIGHT);
		setBodyStyle("padding: 10px");
		setScrollMode(Scroll.AUTO);
		

		sectionsComboBox = new SectionsComboBox<ModelData>();
		sectionsComboBox.setStyleAttribute("padding-left", "10px");
		sectionsComboBox.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

			public void selectionChanged(SelectionChangedEvent<ModelData> se) {

				statisticsChartPanel.hide();
				grid.getStore().removeAll();
				grid.getStore().getLoader().load();
				selectedGradeItemRow = -1;
			}
		});

		// Adding the combobox
		add(sectionsComboBox);
		
		// Adding the instructions on how to show a chart
		Text instructions = new Text(i18n.statisticsGraphInstructions());
		instructions.setStyleAttribute("padding", "10px");
		add(instructions);

		// Creating the grade item statistics grid
		grid = getGrid();
		
		// Creating the chart panel and initially hide it
		statisticsChartPanel = new StatisticsChartPanel();
		statisticsChartPanel.setLegendPosition(LegendPosition.TOP);
		statisticsChartPanel.setSize(AppConstants.CHART_WIDTH, AppConstants.CHART_HEIGHT + 80);
		statisticsChartPanel.hide();
		
		gridAndChartHorizontalPanel = new HorizontalPanel();
		gridAndChartHorizontalPanel.setSpacing(10);
		gridAndChartHorizontalPanel.add(grid);
		gridAndChartHorizontalPanel.add(statisticsChartPanel);
		add(gridAndChartHorizontalPanel);
		
		
		// Creating the close button to the ContentPanel
		Button closeButton = new AriaButton(i18n.close(), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent be) {
				Dispatcher.forwardEvent(GradebookEvents.StopStatistics.getEventType(), Boolean.FALSE);

				// Hide the chart panel
				statisticsChartPanel.hide();

				// Reset the last selected grade item row
				selectedGradeItemRow = -1;

				// Clearing the data table cache
				dataTableCache.clear();
				
				// Reset the section selection
				sectionsComboBox.reset();
			}
		});

		addButton(closeButton);

	}

	public void onLearnerGradeRecordUpdated(ModelData learner) {
		
		loader.load();
	}

	

	private void getGradeItemStatisticsChartData(String assignmentId, String sectionId) {

		// First we check the cache if we have the data already
		String cacheKey = assignmentId + sectionId;
		
		if(dataTableCache.containsKey(cacheKey)) {
			
			// Cache hit
			dataTable = dataTableCache.get(cacheKey);
			statisticsChartPanel.setDataTable(dataTable);
			statisticsChartPanel.show();
		}
		else {
			
			statisticsChartPanel.showUserFeedback(null);
			
			// Data is not in cache yet
			Gradebook gbModel = Registry.get(AppConstants.CURRENT);
			
			RestBuilder builder = RestBuilder.getInstance(
					Method.GET,
					GWT.getModuleBaseURL(),
					AppConstants.REST_FRAGMENT,
					AppConstants.STATISTICS_FRAGMENT,
					AppConstants.INSTRUCTOR_FRAGMENT,
					gbModel.getGradebookUid(),
					gbModel.getGradebookId().toString(),
					assignmentId,
					sectionId);


			// Keeping track of the assignmentId so that we can add the dataTable to
			// the cache once the call returns
			selectedAssignmentId = assignmentId;
			selectedSectionId = sectionId;

			builder.sendRequest(200, 400, null, new RestCallback() {

				public void onError(Request request, Throwable caught) {
					
					statisticsChartPanel.hideUserFeedback(null);
					Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(i18n.statisticsDataErrorTitle(), i18n.statisticsDataErrorMsg(), true));
				}

				public void onFailure(Request request, Throwable exception) {
					
					statisticsChartPanel.hideUserFeedback(null);
					Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(i18n.statisticsDataErrorTitle(), i18n.statisticsDataErrorMsg(), true));
				}

				public void onSuccess(Request request, Response response) {

					// Getting two dimensional INT array
					// GRBK-939 
					// deprecated method can cause security problems as it evals. 
					
					/*
					 * GRBK-939 According to google this will toss exceptions if the input
					 * is null or empty, so we'll guard against that and send a notify if
					 * we have this problem... 
					 * 
					 */
					String jsonText = response.getText(); 
					
					if (jsonText != null && !"".equals(jsonText) )
					{
					
						JSONValue jsonValue = JSONParser.parseStrict(jsonText);
						JSONArray jsonArray = jsonValue.isArray();

						JSONArray positiveFrequencies = jsonArray.get(AppConstants.POSITIVE_NUMBER).isArray();

						dataTable = statisticsChartPanel.createDataTable();
						dataTable.addColumn(ColumnType.STRING, i18n.statisticsChartLabelDistribution());
						dataTable.addColumn(ColumnType.NUMBER, i18n.statisticsChartLabelFrequency());
						dataTable.addRows(positiveFrequencies.size());

						String[] xAxisRangeLables = statisticsChartPanel.getXAxisRangeLabels();
						
						for (int i = 0; i < positiveFrequencies.size(); i++) {

							// Set label
							dataTable.setValue(i, 0, xAxisRangeLables[i]);

							// Set value
							double positiveValue = positiveFrequencies.get(i).isNumber().doubleValue();
							dataTable.setValue(i, 1, positiveValue);
						}

						// adding the dataTable to the cache
						dataTableCache.put(selectedAssignmentId + selectedSectionId, dataTable);
						statisticsChartPanel.show();
					}
					else
					{
						Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(i18n.statisticsDataErrorTitle(), i18n.statisticsDataErrorMsg(), true));
					}
					
					statisticsChartPanel.hideUserFeedback(null);
				}
			});
		}
	}

	/*
	 * Gets the course/section specific statistics data and renders
	 * the chart.
	 * 
	 *  @param sectionId : This needs to be a Base64 encoded string
	 */
	private void getCourseStatisticsChartData(String sectionId) {
		
		// First we check the cache if we have the data already
		String cacheKey = COURSE_CACHE_KEY_PREFIX + sectionId;

		if(dataTableCache.containsKey(cacheKey)) {

			// Cache hit
			dataTable = dataTableCache.get(cacheKey);
			statisticsChartPanel.setDataTable(dataTable);
			statisticsChartPanel.show();
		}
		else {

			statisticsChartPanel.showUserFeedback(null);

			Gradebook gbModel = Registry.get(AppConstants.CURRENT);

			RestBuilder builder = RestBuilder.getInstance(
					Method.GET,
					GWT.getModuleBaseURL(),
					AppConstants.REST_FRAGMENT,
					AppConstants.STATISTICS_FRAGMENT,
					AppConstants.COURSE_FRAGMENT,
					gbModel.getGradebookUid(),
					sectionId);

			selectedSectionId = sectionId;

			builder.sendRequest(200, 400, null, new RestCallback() {

				public void onError(Request request, Throwable caught) {

					statisticsChartPanel.hideUserFeedback(null);
					Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(i18n.statisticsDataErrorTitle(), i18n.statisticsDataErrorMsg(), true));
				}

				public void onFailure(Request request, Throwable exception) {

					statisticsChartPanel.hideUserFeedback(null);
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

					// Initialize the datatable
					dataTable = statisticsChartPanel.createDataTable();
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
					
					// adding the dataTable to the cache
					dataTableCache.put(COURSE_CACHE_KEY_PREFIX + selectedSectionId, dataTable);

					statisticsChartPanel.hideUserFeedback(null);
				}
			});
		}
	}

	private Grid<StatisticsModel> getGrid() {

		// Passing the selected section to the rest builder
		UrlArgsCallback urlArgsCallback = new UrlArgsCallback() {

			public String getUrlArg() {
				
				String selectedSectionId = getSelectedSection();
				return selectedSectionId;
			}
		};

		// Get the Statistics grid data
		loader = RestBuilder.getDelayLoader(
				AppConstants.LIST_ROOT, 
				EnumSet.allOf(StatisticsKey.class),
				Method.GET,
				urlArgsCallback,
				null,
				GWT.getModuleBaseURL(),
				AppConstants.REST_FRAGMENT,
				AppConstants.STATISTICS_FRAGMENT,
				AppConstants.INSTRUCTOR_FRAGMENT);


		final ListStore<StatisticsModel> store = new ListStore<StatisticsModel>(loader);
		store.setModelComparer(new EntityModelComparer<StatisticsModel>(StatisticsKey.S_ID.name()));

		final ColumnModel cm = new ColumnModel(configureGrid());

		Grid<StatisticsModel> grid = new Grid<StatisticsModel>(store, cm);
		grid.setBorders(true);
		grid.setAutoHeight(true);
		grid.addListener(Events.RowClick, new Listener<GridEvent<?>>() {

			public void handleEvent(GridEvent<?> gridEvent) {

				// If we click on the first row, which shows the Course Grade data, we don't do anything
				int rowIndex = gridEvent.getRowIndex();

				// We keep track of the selected row in case the user keeps
				// clicking on the same grade item, in which case we don't
				// need to fetch new data
				if(selectedGradeItemRow == rowIndex) {
					
					return;
				}
				else {

					selectedGradeItemRow = rowIndex;
				}

				String assignmentId = gridEvent.getModel().get(StatisticsKey.S_ITEM_ID.name());

				// Getting the mean so that we can determine if any grades have been entered
				// for the selected grade item
				String mean = gridEvent.getModel().get(StatisticsKey.S_MEAN.name());

				if(null != mean && !AppConstants.STATISTICS_DATA_NA.equals(mean)) {

					if(FIRST_ROW == rowIndex) {

						getCourseStatisticsChartData(getSelectedSection());
					}
					else {

						getGradeItemStatisticsChartData(assignmentId, getSelectedSection());
					}
				}
				else {

					// If there is no data to show, we hide the chart
					statisticsChartPanel.hide();
				}
			}
		});

		return grid;
	}


	private List<ColumnConfig> configureGrid() {

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();  
		column.setId(StatisticsKey.S_NM.name());  
		column.setHeader(i18n.statsNameHeader());
		column.setWidth(200);
		column.setGroupable(false);
		column.setMenuDisabled(true);
		column.setSortable(false);
		configs.add(column); 

		column = new ColumnConfig();  
		column.setId(StatisticsKey.S_MEAN.name());  
		column.setHeader(i18n.statsMeanHeader());
		column.setWidth(80);
		column.setGroupable(false);
		column.setMenuDisabled(true);
		column.setSortable(false);
		configs.add(column);

		column = new ColumnConfig();  
		column.setId(StatisticsKey.S_STD_DEV.name());  
		column.setHeader(i18n.statsStdDvHeader());
		column.setWidth(80);
		column.setGroupable(false);
		column.setMenuDisabled(true);
		column.setSortable(false);
		configs.add(column);

		column = new ColumnConfig();  
		column.setId(StatisticsKey.S_MEDIAN.name());  
		column.setHeader(i18n.statsMedianHeader());
		column.setWidth(80);
		column.setGroupable(false);
		column.setMenuDisabled(true);
		column.setSortable(false);
		configs.add(column);

		column = new ColumnConfig();  
		column.setId(StatisticsKey.S_MODE.name());  
		column.setHeader(i18n.statsModeHeader());
		column.setWidth(160);
		column.setGroupable(false);
		column.setMenuDisabled(true);
		column.setSortable(false);
		column.setResizable(true); 
		configs.add(column);

		return configs;
	}

	// Method that returns the selected section
	private String getSelectedSection() {

		List<ModelData> selection = sectionsComboBox.getSelection();

		String sectionId = null;
		
		if(null != selection && selection.size() == 1) {

			sectionId = (String) selection.get(0).get(SectionKey.S_ID.name());
			
		}
		else {

			sectionId = AppConstants.ALL;
		}
		
		// GRBK-636 : Since the sctionIds have characters that are not URL safe, we Base64 encode
		// the sectionId here and then decode it on the server side. Initially, we tried to just 
		// URL encode the sectionId but that didn't work because "something" on the server side
		// decoded the URL and then return a 400
		return Base64.encode(sectionId);

	}
}
