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

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.I18nMessages;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;
import org.sakaiproject.gradebook.gwt.client.resource.GradebookResources;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.ColumnChart;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;

public class StatisticsChartPanel extends ContentPanel {

	private final I18nConstants i18n;
	private I18nMessages i18nMessages;
	private GradebookResources resources;

	private Image columnChartIcon;
	private Image pieChartIcon;
	private Image lineChartIcon;

	private LayoutContainer graphPanelContainer;
	private LayoutContainer chartIconPanelContainer;

	private DataTable dataTable;

	private LegendPosition legendPosition = LegendPosition.RIGHT;

	private int chartWidth = AppConstants.CHART_WIDTH;
	private int chartHeight = AppConstants.CHART_HEIGHT;

	private StatisticsChartLoaderListener statisticsChartLoaderListener;
	
	// GRBK-897 
	/*
	 * This value is necessary because when we get above a certain point of haxis labels, there's overlap.  So we tilt 
	 * the labels to get more in.  
	 */
	private final static int tiltAfterNumberOfRows = 15; 
	// This is used by the various panels that use chars
	// GRBK-897, now we have 20 buckets
	private final static String[] RANGE = new String[] {
		"0-4",
		"5-9",
		"10-14",
		"15-19",
		"20-24",
		"25-29",
		"30-34",
		"35-39",
		"40-44",
		"45-49",
		"50-54",
		"55-59",
		"60-64",
		"65-69",
		"70-74",
		"75-79",
		"80-84",
		"85-89",
		"90-94",
		"95-100"};
	
	private boolean hasActiveNotifications = false;

	private boolean isVisualizationApiLoaded = false;

	public enum ChartIconPlacement {
		BOTTOM, RIGHT
	};

	public StatisticsChartPanel() {

		this(null, ChartIconPlacement.BOTTOM, AppConstants.CURRENT_STATISTICS_DATA);
	}

	public StatisticsChartPanel(ChartIconPlacement chartIconPlacement, int dataAge) {

		this(null, chartIconPlacement, dataAge);
	}
	
	public StatisticsChartPanel(StatisticsChartLoaderListener statisticsChartLoaderListener) {
		
		this(statisticsChartLoaderListener, ChartIconPlacement.BOTTOM, AppConstants.CURRENT_STATISTICS_DATA);
	}

	public StatisticsChartPanel(
			StatisticsChartLoaderListener statisticsChartLoaderListener,
			ChartIconPlacement chartIconPlacement,
			int dataAge) {

		this.statisticsChartLoaderListener = statisticsChartLoaderListener;

		// Loading visualization APIs
		VisualizationUtils.loadVisualizationApi(new VisualizationRunnable(),
				CoreChart.PACKAGE);

		this.i18n = Registry.get(AppConstants.I18N);
		this.i18nMessages = Registry.get(AppConstants.I18N_TEMPLATES);
		this.resources = Registry.get(AppConstants.RESOURCES);

		// Create the image icons
		columnChartIcon = new Image(resources.chart_bar());
		columnChartIcon.setStyleName(resources.css().statisticsChartIcon());
		pieChartIcon = new Image(resources.chart_pie());
		pieChartIcon.setStyleName(resources.css().statisticsChartIcon());
		lineChartIcon = new Image(resources.chart_line());
		lineChartIcon.setStyleName(resources.css().statisticsChartIcon());

		columnChartIcon.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				graphPanelContainer.removeAll();
				// GRBK-897, we set the tilt on the chart if the number of items in the chart are above a certain value
				boolean tilt = dataTable.getNumberOfRows() > tiltAfterNumberOfRows; 
				graphPanelContainer.add(new ColumnChart(dataTable, createColumnChartOptions(tilt)));
				graphPanelContainer.layout();
			}
		});

		lineChartIcon.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				graphPanelContainer.removeAll();
				// GRBK-897, we set the tilt on the chart if the number of items in the chart are above a certain value
				boolean tilt = dataTable.getNumberOfRows() > tiltAfterNumberOfRows; 
				graphPanelContainer.add(new LineChart(dataTable, createLineChartOptions(tilt)));
				graphPanelContainer.layout();
			}
		});

		pieChartIcon.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				graphPanelContainer.removeAll();
				graphPanelContainer.add(new PieChart(dataTable,
						createPieChartOptions()));
				graphPanelContainer.layout();
			}
		});

		setFrame(true);
		setBodyBorder(true);
		
		if(AppConstants.CURRENT_STATISTICS_DATA != dataAge) {
			
			StringBuilder sb = new StringBuilder(i18n.statisticsChartTitle());
			sb.append(" <span style=\"color:black; background-color:#FDF5E6; font-weight:normal; float:right; border-radius:3px; padding:4px 5px;font-size:120%\">").append(i18nMessages.statisticsDataAge(dataAge)).append("</span>");
			setHeading(sb.toString());
		}
		else {
			
			setHeading(i18n.statisticsChartTitle());
		}
		
		graphPanelContainer = new HorizontalPanel();

		/*
		 * We allow to place the statistics' chart type icons either at the
		 * bottom or on the right of the chart. The following switch statement
		 * configures the containers accordingly
		 */
		switch (chartIconPlacement) {

		case BOTTOM:
			chartIconPanelContainer = new HorizontalPanel();
			chartIconPanelContainer.setStyleName(resources.css()
					.statisticsChartIconPanelContainer());
			add(graphPanelContainer);
			add(chartIconPanelContainer);
			break;
		case RIGHT:
			chartIconPanelContainer = new VerticalPanel();
			setLayout(new ColumnLayout());
			add(graphPanelContainer, new ColumnData(.95));
			add(chartIconPanelContainer, new ColumnData(0.05));
			break;
		default:

		}

		chartIconPanelContainer.add(columnChartIcon);
		chartIconPanelContainer.add(pieChartIcon);
		chartIconPanelContainer.add(lineChartIcon);

		layout();
	}

	@Override
	public void show() {

		// Before we instantiate a graph, we check
		// if the Visualization APIs have been loaded properly
		if (!isVisualizationApiLoaded) {

			Dispatcher.forwardEvent(
					GradebookEvents.Notification.getEventType(),
					new NotificationEvent(i18n.statisticsDataErrorTitle(), i18n
							.statisticsVisualizationErrorMsg(), true));
			hide();
			return;
		}

		if (null != dataTable) {

			super.show();
			graphPanelContainer.removeAll();
			boolean tilt = dataTable.getNumberOfRows() > tiltAfterNumberOfRows; 
			graphPanelContainer.add(new ColumnChart(dataTable, createColumnChartOptions(tilt)));
			unmask();
			graphPanelContainer.layout();

		} else {

			Dispatcher.forwardEvent(
					GradebookEvents.Notification.getEventType(),
					new NotificationEvent(i18n.statisticsDataErrorTitle(), i18n
							.statisticsDataErrorMsg(), true));
		}
	}

	public void setDataTable(DataTable dataTable) {

		this.dataTable = dataTable;
	}

	public void setLegendPosition(LegendPosition legendPosition) {

		this.legendPosition = legendPosition;
	}

	public void setChartHeight(int height) {

		this.chartHeight = height;
	}

	public void setChartWidth(int width) {

		this.chartWidth = width;
	}

	public String[] getXAxisRangeLabels() {

		return RANGE;
	}

	/*
	 * If no additional condition needs to be checked, pass "null" as the
	 * function argument
	 */
	public void showUserFeedback(Boolean additionalCondition) {

		/*
		 * If additionalCondition is not null, we use its boolean value. If
		 * additionalCondition is null we use true so that we don't alter the
		 * boolean logic X && TRUE = X
		 */
		boolean condition = (null != additionalCondition ? additionalCondition
				.booleanValue() : true);

		if (!hasActiveNotifications && condition) {

			Dispatcher.forwardEvent(
					GradebookEvents.ShowUserFeedback.getEventType(),
					i18n.statisticsGradebookLoadingChart(), false);
			mask();
			hasActiveNotifications = true;
		}
	}

	/*
	 * If no additional condition needs to be checked, pass "null" as the
	 * function argument
	 */
	public void hideUserFeedback(Boolean additionalCondition) {

		/*
		 * If additionalCondition is not null, we use its boolean value. If
		 * additionalCondition is null we use true so that we don't alter the
		 * boolean logic X && TRUE = X
		 */
		boolean condition = (null != additionalCondition ? additionalCondition
				.booleanValue() : true);

		if (hasActiveNotifications && condition) {

			Dispatcher.forwardEvent(
					GradebookEvents.HideUserFeedback.getEventType(), false);
			unmask();
			hasActiveNotifications = false;
		}
	}

	/*
	 * Creates a DataTabel, keeps a reference to it, and returns it
	 */
	public DataTable createDataTable() {

		dataTable = DataTable.create();
		return dataTable;
	}
	
	private PieChart.PieOptions createPieChartOptions() {

		PieChart.PieOptions options = PieChart.createPieOptions();
		options.setWidth(chartWidth);
		options.setHeight(chartHeight);
		options.set3D(AppConstants.IS_CHART_3D);
		options.setLegend(legendPosition);
		return options;
	}

	private Options createColumnChartOptions(boolean tilt) {
		
		Options options = Options.create();
		options.setWidth(chartWidth);
		options.setHeight(chartHeight);
		options.setLegend(legendPosition);
		// GRBK-897 - these options show every axis entry as well as tilt them 60 degrees
		if (tilt)
		{
			AxisOptions hopts = AxisOptions.create(); 
			hopts.set("showTextEvery", 1.0);
			hopts.set("slantedText", true); 
			hopts.set("slantedTextAngle", 60.0);
			options.setHAxisOptions(hopts);
		}
		return options;
	}

	private Options createLineChartOptions(boolean tilt) {
		
		Options options = Options.create();
		options.setWidth(chartWidth);
		options.setHeight(chartHeight);
		options.setLegend(legendPosition);
		// GRBK-897 - these options show every axis entry as well as tilt them 60 degrees
		if (tilt)
		{
			AxisOptions hopts = AxisOptions.create(); 
			hopts.set("showTextEvery", 1.0);
			hopts.set("slantedText", true); 
			hopts.set("slantedTextAngle", 60.0);
			options.setHAxisOptions(hopts);
		}		
		return options;
	}

	/*
	 * An instance of this runnable class is called once the Visualization APIs
	 * have been loaded via VisualizationUtils.loadVisualizationApi(...)
	 */
	private class VisualizationRunnable implements Runnable {

		public void run() {

			isVisualizationApiLoaded = true;

			if (null != statisticsChartLoaderListener) {

				statisticsChartLoaderListener.load();
			}
		}
	}
}
