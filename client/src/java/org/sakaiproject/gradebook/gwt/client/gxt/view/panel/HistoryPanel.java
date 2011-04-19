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

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.RestBuilder;
import org.sakaiproject.gradebook.gwt.client.RestBuilder.Method;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaButton;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.model.key.ActionKey;
import org.sakaiproject.gradebook.gwt.client.model.type.EntityType;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.binding.Converter;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.core.client.GWT;

public class HistoryPanel extends EntityPanel {

	private ColumnModel columnModel;
	private FormBinding formBinding;
	private FormPanel formPanel;
	private PagingLoader<PagingLoadResult<ModelData>> loader;
	private Grid<ModelData> grid;
	private GridSelectionModel<ModelData> selectionModel;
	private PagingToolBar pagingToolBar;
	private ListStore<ModelData> store;
	private SelectionChangedListener<ModelData> selectionListener;

	private FieldSet fieldSet;
	private Converter converter;


	public HistoryPanel(I18nConstants i18n) {
		super(i18n, true);
	}

	protected LayoutContainer getFormPanel() {
		return fieldSet;
	}

	protected void initialize() {
		setFrame(true);
		setHeading(i18n.historyHeading());
		setLayout(new FitLayout());


		converter = new Converter() {

			public Object convertModelValue(Object value) {

				if (value != null && value instanceof String && (((String)value).equalsIgnoreCase("true") || ((String)value).equalsIgnoreCase("false")))
					return Boolean.valueOf((String)value);

				return value;
			}

			public Object convertFieldValue(Object value) {
				return value;
			}

		};


		FormLayout formPanelLayout = new FormLayout();
		formPanelLayout.setLabelSeparator(":");
		formPanelLayout.setLabelWidth(180);

		formPanel = new FormPanel();
		formPanel.setHeaderVisible(false);
		formPanel.setLayout(formPanelLayout);
		formPanel.setVisible(false);

		LabelField dateField = new LabelField();
		dateField.setName(ActionKey.S_RECORD.name());
		dateField.setFieldLabel(i18n.actionDateFieldLabel());
		dateField.setStyleAttribute("font-size", "12pt");
		formPanel.add(dateField);

		LabelField descriptionField = new LabelField();
		descriptionField.setName(ActionKey.S_DESC.name());
		descriptionField.setFieldLabel(i18n.actionDescriptionFieldLabel());
		descriptionField.setStyleAttribute("font-size", "12pt");
		formPanel.add(descriptionField);

		LabelField entityField = new LabelField();
		entityField.setName(ActionKey.S_ENTY_NM.name());
		entityField.setFieldLabel(i18n.actionEntityFieldLabel());
		entityField.setStyleAttribute("font-size", "12pt");
		formPanel.add(entityField);

		LabelField actorNameField = new LabelField();
		actorNameField.setName(ActionKey.S_GRDR_NM.name());
		actorNameField.setFieldLabel(i18n.actionActor());
		actorNameField.setStyleAttribute("font-size", "12pt");
		formPanel.add(actorNameField);

		FormLayout formLayout = new FormLayout();
		formLayout.setLabelSeparator(":");
		formLayout.setLabelWidth(180);

		fieldSet = new FieldSet();
		fieldSet.setHeading(i18n.actionDetails());
		fieldSet.setCheckboxToggle(false);
		fieldSet.setLayout(formLayout);

		formPanel.add(fieldSet);

		loader = RestBuilder.getPagingDelayLoader(
				Method.GET, null, GWT.getModuleBaseURL(), 
				AppConstants.REST_FRAGMENT, AppConstants.HISTORY_FRAGMENT);

		pagingToolBar = new PagingToolBar(20);
		pagingToolBar.bind(loader);

		setBottomComponent(pagingToolBar);

		ArrayList<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig(ActionKey.S_RECORD.name(), i18n.actionDateFieldLabel(), 200);
		configs.add(column);

		column = new ColumnConfig(ActionKey.S_DESC.name(), i18n.actionDescriptionFieldLabel(), 150);
		configs.add(column);

		column = new ColumnConfig(ActionKey.S_ENTY_NM.name(), i18n.actionEntityFieldLabel(), 180);
		configs.add(column);

		column = new ColumnConfig(ActionKey.S_LRNR_NM.name(), i18n.actionStudentNameFieldLabel(), 140);
		configs.add(column);

		columnModel = new ColumnModel(configs);
		store = new ListStore<ModelData>(loader);
		store.setModelComparer(new EntityModelComparer<ModelData>(ActionKey.S_ID.name()));
		selectionListener = new SelectionChangedListener<ModelData>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				ModelData action = se.getSelectedItem();

				formPanel.hide();

				if (action == null) 
					formBinding.unbind();
				else {
					formBinding.bind(action);
					initState(action);
				}
				formPanel.show();
			}

		};

		selectionModel = new GridSelectionModel<ModelData>();
		selectionModel.addSelectionChangedListener(selectionListener);
		selectionModel.setSelectionMode(SelectionMode.SINGLE);
		grid = new Grid<ModelData>(store, columnModel);
		grid.setBorders(true);
		grid.setSelectionModel(selectionModel);

		LayoutContainer container = new LayoutContainer() {
			@Override
			protected void onResize(int width, int height) {
				super.onResize(width, height);

				grid.setHeight(height - 5);
			}
		};
		container.setLayout(new ColumnLayout());

		container.add(grid, new ColumnData(.60));
		container.add(formPanel, new ColumnData(.40));

		add(container);

		Button button = new AriaButton(i18n.close(), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent be) {
				Dispatcher.forwardEvent(GradebookEvents.StopStatistics.getEventType(), Boolean.FALSE);
			}

		});
		addButton(button);
	}

	@Override
	protected void bindFormPanel() {
		formBinding = new FormBinding(formPanel, true) {
			public void autoBind() {
				for (Field<?> f : panel.getFields()) {
					if (!bindings.containsKey(f.getId())) {
						String name = f.getName();
						if (name != null && name.length() > 0) {
							FieldBinding b = new FieldBinding(f, f.getName());
							b.setConverter(converter);
							bindings.put(f.getId(), b);
						}
					}
				}
			}
		};
	}

	@Override
	protected void onRender(com.google.gwt.user.client.Element parent, int pos) {
		super.onRender(parent, pos);
		loader.load(0, 20);
	}

	@Override
	protected void onShow() {
		super.onShow();
		loader.load(0, 20);
	}

	private void initState(ModelData action) {
		String entityTypeString = action.get(ActionKey.O_ENTY_TYPE.name());
		EntityType entityType = entityTypeString == null ? null : EntityType.valueOf(entityTypeString);
		boolean isGradebook = entityType != null && entityType == EntityType.GRADEBOOK;
		boolean isCategory = entityType != null && entityType == EntityType.CATEGORY;
		boolean isItem = entityType != null && entityType == EntityType.ITEM;
		boolean isGradeOrCourseGrade = (entityType != null && entityType == EntityType.GRADE_RECORD ||
				entityType != null && entityType == EntityType.COURSE_GRADE_RECORD);

		studentNameField.setVisible(isGradeOrCourseGrade);
		fieldSet.setVisible(isItem || isGradeOrCourseGrade);

		directionsField.setVisible(false);
		nameField.setVisible(isGradebook || isCategory || isItem);
		categoryTypePicker.setVisible(isGradebook);
		gradeTypePicker.setVisible(isGradebook);
		categoryPicker.setVisible(false);
		includedField.setVisible(isCategory || isItem);
		extraCreditField.setVisible(isCategory || isItem);
		equallyWeightChildrenField.setVisible(isCategory);
		releasedField.setVisible(isItem);
		nullsAsZerosField.setVisible(isItem);
		releaseGradesField.setVisible(isGradebook);
		releaseItemsField.setVisible(isGradebook);
		scaledExtraCreditField.setVisible(isGradebook);
		enforcePointWeightingField.setVisible(isCategory);
		showMeanField.setVisible(isGradebook);
		showMedianField.setVisible(isGradebook);
		showModeField.setVisible(isGradebook);
		showRankField.setVisible(isGradebook);
		showItemStatsField.setVisible(isGradebook);
		percentCourseGradeField.setVisible(isCategory);
		percentCategoryField.setVisible(isItem);
		pointsField.setVisible(isItem);
		dropLowestField.setVisible(isCategory);
		dueDateField.setVisible(isItem);
		sourceField.setVisible(isItem);
	}

	@Override
	protected void attachListeners() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initializeStores() {
		// TODO Auto-generated method stub

	}

}
