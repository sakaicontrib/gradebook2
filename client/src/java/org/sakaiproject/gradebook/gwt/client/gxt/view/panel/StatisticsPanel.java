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

package org.sakaiproject.gradebook.gwt.client.gxt.view.panel;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.RestBuilder;
import org.sakaiproject.gradebook.gwt.client.RestCallback;
import org.sakaiproject.gradebook.gwt.client.UrlArgsCallback;
import org.sakaiproject.gradebook.gwt.client.RestBuilder.Method;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaButton;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.gxt.model.StatisticsModel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.SectionsComboBox;
import org.sakaiproject.gradebook.gwt.client.model.key.SectionKey;
import org.sakaiproject.gradebook.gwt.client.model.key.StatisticsKey;
import org.sakaiproject.gradebook.gwt.client.resource.GradebookResources;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
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
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.ColumnChart;
import com.google.gwt.visualization.client.visualizations.LineChart;
import com.google.gwt.visualization.client.visualizations.PieChart;


public class StatisticsPanel extends ContentPanel {

	private final I18nConstants i18n;

	private ListLoader<ListLoadResult<ModelData>> loader;

	private GradebookResources resources;

	private VerticalPanel mainVerticalPanel;
	private HorizontalPanel horizontalPanel;
	private HorizontalPanel horizontalIconPanel;
	private VerticalPanel verticalGraphPanel;
	private Panel statisticsGraphPanel;

	private SectionsComboBox<ModelData> sectionsComboBox;

	private Panel gridPanel;

	private Grid<StatisticsModel> grid = null;

	private Image columnChartIcon;
	private Image pieChartIcon;
	private Image lineChartIcon;

	private DataTable dataTable;

	private PieChart pieChart;
	private ColumnChart columnChart;
	private LineChart lineChart;

	private int selectedGradeItemRow = 0;
	private String selectedAssignmentId;
	private String selectedSectionId;

	private boolean isVisualizationApiLoaded = false;

	private Map<String, DataTable> dataTableCache = new HashMap<String, DataTable>();

	private final static int FIRST_ROW = 0;
	private final static String CHART_TITLE = "Grades Distribution";
	private final static int CHART_WIDTH = 600;
	private final static int CHART_HEIGHT = 300;
	private final static boolean IS_CHART_3D = true;


	private final static String[] RANGE = new String[]{"0-9",
		"10-19",
		"20-29",
		"30-39",
		"40-49",
		"50-59",
		"60-69",
		"70-79",
		"80-89",
		"90-100"};

	public StatisticsPanel(final I18nConstants i18n) {

		super();

		// Loading visualization APIs
		VisualizationUtils.loadVisualizationApi(new VisualizationRunnable(), PieChart.PACKAGE,  ColumnChart.PACKAGE, LineChart.PACKAGE);

		// Getting needed resources
		this.i18n = i18n;
		this.resources = Registry.get(AppConstants.RESOURCES);

		// Configure main ContentPanel
		setHeading(i18n.statisticsHeading());
		setFrame(true);

		// Configuring various UI elements: initializing, adding handlers etc.
		configureChartIcons();

		sectionsComboBox = new SectionsComboBox<ModelData>();
		sectionsComboBox.setStyleName(resources.css().gbStatisticsChartPanel());
		sectionsComboBox.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

			public void selectionChanged(SelectionChangedEvent<ModelData> se) {

				// When the section selection changes, we do:
				// - Reloaded the grid with the section relevant data
				// - Reset the selected grade item so that new data is loaded
				//   if the user clicks on a grade item row
				// - Remove existing chart
				removeAllWidgetsFrom(statisticsGraphPanel);
				horizontalIconPanel.setVisible(false);
				grid.getStore().removeAll();
				grid.getStore().getLoader().load();
				selectedGradeItemRow = 0;
			}
		});

		setBodyBorder(true);
		setButtonAlign(HorizontalAlignment.RIGHT);

		grid = getGrid();

		Button button = new AriaButton(i18n.close(), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent be) {
				Dispatcher.forwardEvent(GradebookEvents.StopStatistics.getEventType(), Boolean.FALSE);

				// Remove existing chart
				removeAllWidgetsFrom(statisticsGraphPanel);

				// Hide the graph icons
				horizontalIconPanel.setVisible(false);

				// Reset the last selected grade item row
				selectedGradeItemRow = 0;

				// Clearing the data table cache
				dataTableCache.clear();
				
				// Reset the section selection
				sectionsComboBox.reset();
			}
		});

		addButton(button);

		mainVerticalPanel = new VerticalPanel();
		mainVerticalPanel.setSpacing(10);

		horizontalPanel = new HorizontalPanel();
		verticalGraphPanel = new  VerticalPanel();
		verticalGraphPanel.setStyleName(resources.css().gbStatisticsChartPanel());
		statisticsGraphPanel = new SimplePanel();
		horizontalIconPanel = new HorizontalPanel();

		HTML instructions = new HTML(i18n.statisticsGraphInstructions());
		mainVerticalPanel.add(instructions);
		mainVerticalPanel.add(sectionsComboBox);
		mainVerticalPanel.add(horizontalPanel);

		gridPanel = new SimplePanel();
		gridPanel.add(grid);
		horizontalPanel.add(gridPanel);
		horizontalPanel.add(verticalGraphPanel);

		horizontalIconPanel.setWidth("30%");
		horizontalIconPanel.setStyleName(resources.css().gbStatisticsChartIconPanel());
		horizontalIconPanel.setVisible(false);

		verticalGraphPanel.add(statisticsGraphPanel);
		verticalGraphPanel.add(horizontalIconPanel);

		horizontalIconPanel.add(columnChartIcon);
		horizontalIconPanel.add(pieChartIcon);
		horizontalIconPanel.add(lineChartIcon);

		add(mainVerticalPanel);
	}

	public void onLearnerGradeRecordUpdated(ModelData learner) {
		
		loader.load();
	}

	private PieChart.Options createPieChartOptions() {
		
		PieChart.Options options = PieChart.Options.create();
		options.setWidth(CHART_WIDTH);
		options.setHeight(CHART_HEIGHT);
		options.set3D(IS_CHART_3D);
		options.setTitle(CHART_TITLE);
		return options;
	}

	private ColumnChart.Options createColumnChartOptions() {
		
		ColumnChart.Options options = ColumnChart.Options.create();
		options.setWidth(CHART_WIDTH);
		options.setHeight(CHART_HEIGHT);
		options.set3D(IS_CHART_3D);
		options.setTitle(CHART_TITLE);
		return options;
	}

	private LineChart.Options createLineChartOptions() {
		
		LineChart.Options options = LineChart.Options.create();
		options.setWidth(CHART_WIDTH);
		options.setHeight(CHART_HEIGHT);
		options.setTitle(CHART_TITLE);
		return options;
	}

	private void getStatisticsData(String assignmentId, String sectionId) {

		// First we check the cache if we have the data already
		String cacheKey = assignmentId + sectionId;

		if(dataTableCache.containsKey(cacheKey)) {
			
			dataTable = dataTableCache.get(cacheKey);
			showGraph();
		}
		else {
			
			RestBuilder builder = RestBuilder.getInstance(
					Method.GET,
					GWT.getModuleBaseURL(),
					AppConstants.REST_FRAGMENT,
					AppConstants.STATISTICS_FRAGMENT,
					AppConstants.INSTRUCTOR_FRAGMENT,
					assignmentId,
					getSelectedSection());


			// Keeping track of the assignmentId so that we can add the dataTable to
			// the cache once the call returns
			selectedAssignmentId = assignmentId;
			selectedSectionId = sectionId;

			builder.sendRequest(200, 400, null, new RestCallback() {

				public void onError(Request request, Throwable caught) {

					Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(i18n.statisticsDataErrorTitle(), i18n.statisticsDataErrorMsg(), false));
				}

				public void onFailure(Request request, Throwable exception) {

					Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(i18n.statisticsDataErrorTitle(), i18n.statisticsDataErrorMsg(), false));
				}

				public void onSuccess(Request request, Response response) {

					JSONValue jsonValue = JSONParser.parse(response.getText());
					JSONArray jsonArray = jsonValue.isArray();

					dataTable = DataTable.create();
					dataTable.addColumn(ColumnType.STRING, "Range");
					dataTable.addColumn(ColumnType.NUMBER, "Frequencey");
					dataTable.addRows(jsonArray.size());

					for (int i = 0; i < jsonArray.size(); i++) {

						// Set label
						dataTable.setValue(i, 0, RANGE[i]);

						// Set value
						double value = jsonArray.get(i).isNumber().doubleValue();
						dataTable.setValue(i, 1, value);
					}

					// adding the dataTable to the cache
					dataTableCache.put(selectedAssignmentId + selectedSectionId, dataTable);
					showGraph();
				}
			});
		}
	}

	private void showGraph() {

		columnChart = new ColumnChart(dataTable, createColumnChartOptions());
		statisticsGraphPanel.add(columnChart);
		horizontalIconPanel.setVisible(true);
	}

	private void removeAllWidgetsFrom(Panel panel) {

		Iterator<Widget> itr = panel.iterator();
		
		while(itr.hasNext()) {
			
			itr.next();
			itr.remove();
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
		grid.setStyleAttribute("borderTop", "none");   
		grid.setBorders(true);
		grid.setAutoHeight(true);

		grid.addListener(Events.RowClick, new Listener<GridEvent<?>>() {

			public void handleEvent(GridEvent<?> gridEvent) {

				// If we click on the first row, which shows the Course Grade data, we don't do anything
				int rowIndex = gridEvent.getRowIndex();

				if(rowIndex != FIRST_ROW) {

					// We keep track of the selected row in case the user keeps
					// clicking on the same grade item, in which case we don't
					// need to fetch new data
					if(selectedGradeItemRow == rowIndex) {
						return;
					}
					else {

						selectedGradeItemRow = rowIndex;
					}
					
					// Remove existing chart
					removeAllWidgetsFrom(statisticsGraphPanel);

					String assignmentId = gridEvent.getModel().get(StatisticsKey.S_ITEM_ID.name());

					// Getting the mean so that we can determine if any grades have been entered
					// for the selected grade item
					String mean = gridEvent.getModel().get(StatisticsKey.S_MEAN.name());

					if(null != mean && !AppConstants.STATISTICS_DATA_NA.equals(mean)) {

						// Before we get the data and show the graph(s), we check
						// if the Visualization APIs have been loaded properly
						if(isVisualizationApiLoaded) {
							
							getStatisticsData(assignmentId, getSelectedSection());
						}
						else {
							
							Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(i18n.statisticsDataErrorTitle(), i18n.statisticsVisualizationErrorMsg(), false));
						}
					}
					else {

						// If there is no data to show, we hide the graph icons
						horizontalIconPanel.setVisible(false);
					}
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

	private void configureChartIcons() {

		columnChartIcon = new Image(resources.chart_bar());
		pieChartIcon = new Image(resources.chart_pie());
		lineChartIcon = new Image(resources.chart_line());

		columnChartIcon.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				columnChart = new ColumnChart(dataTable, createColumnChartOptions());
				removeAllWidgetsFrom(statisticsGraphPanel);
				statisticsGraphPanel.add(columnChart);
				horizontalIconPanel.setVisible(true);
			}
		});

		lineChartIcon.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				lineChart = new LineChart(dataTable, createLineChartOptions());
				removeAllWidgetsFrom(statisticsGraphPanel);
				statisticsGraphPanel.add(lineChart);
				horizontalIconPanel.setVisible(true);
			}
		});

		pieChartIcon.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				pieChart = new PieChart(dataTable, createPieChartOptions());
				removeAllWidgetsFrom(statisticsGraphPanel);
				statisticsGraphPanel.add(pieChart);
				horizontalIconPanel.setVisible(true);
			}
		});
	}

	// Method that returns the selected section
	private String getSelectedSection() {

		List<ModelData> selection = sectionsComboBox.getSelection();

		if(null != selection && selection.size() == 1) {

			return URL.encodeComponent((String) selection.get(0).get(SectionKey.S_ID.name()));
		}
		else {

			return AppConstants.ALL_SECTIONS;
		}

	}

	/*
	 * An instance of this runnable class is called once the
	 * Visualization APIs have been loaded via
	 * VisualizationUtils.loadVisualizationApi(...)
	 */
	private class VisualizationRunnable implements Runnable {

		public void run() {
			isVisualizationApiLoaded = true;
		}
	}
}
