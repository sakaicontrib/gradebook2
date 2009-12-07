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
import org.sakaiproject.gradebook.gwt.client.Gradebook2RPCServiceAsync;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.SecureToken;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.action.Action.Key;
import org.sakaiproject.gradebook.gwt.client.gxt.NotifyingAsyncCallback;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaButton;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.binding.Converter;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.DataReader;
import com.extjs.gxt.ui.client.data.ModelReader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
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
import com.google.gwt.user.client.rpc.AsyncCallback;

public class HistoryPanel extends EntityPanel {

	private ColumnModel columnModel;
	private FormBinding formBinding;
	private FormPanel formPanel;
	private PagingLoader<PagingLoadResult<UserEntityAction<?>>> loader;
	private Grid<UserEntityAction<?>> grid;
	private GridSelectionModel<UserEntityAction<?>> selectionModel;
	private PagingToolBar pagingToolBar;
	private ListStore<UserEntityAction<?>> store;
	private SelectionChangedListener<UserEntityAction<?>> selectionListener;
	
	private LabelField studentNameField;
	
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
				
				if (value instanceof String && (((String)value).equalsIgnoreCase("true") || ((String)value).equalsIgnoreCase("false")))
					return Boolean.valueOf((String)value);
				
			    return value;
			}
			
			public Object convertFieldValue(Object value) {
			    return value;
			}
			
		};
		
		RpcProxy<PagingLoadResult<UserEntityAction<?>>> proxy = new RpcProxy<PagingLoadResult<UserEntityAction<?>>>() {
			@Override
			protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<UserEntityAction<?>>> callback) {
				Gradebook2RPCServiceAsync service = Registry.get("service");
				GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
				service.getPage(selectedGradebook.getGradebookUid(), selectedGradebook.getGradebookId(), EntityType.ACTION, (PagingLoadConfig)loadConfig, SecureToken.get(), callback);
			}
			
			@Override
			public void load(final DataReader<PagingLoadResult<UserEntityAction<?>>> reader, final Object loadConfig, final AsyncCallback<PagingLoadResult<UserEntityAction<?>>> callback) {
				load(loadConfig, new NotifyingAsyncCallback<PagingLoadResult<UserEntityAction<?>>>() {

					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						callback.onFailure(caught);
					}

					public void onSuccess(PagingLoadResult<UserEntityAction<?>> result) {
						try {
							PagingLoadResult<UserEntityAction<?>> data = null;
							if (reader != null) {
								data = reader.read(loadConfig, result);
							} else {
								data = result;
							}
							callback.onSuccess(data);
						} catch (Exception e) {
							callback.onFailure(e);
						}
					}

				});
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
		dateField.setName(Key.DATE_RECORDED.name());
		dateField.setFieldLabel(i18n.actionDateFieldLabel());
		dateField.setStyleAttribute("font-size", "12pt");
		formPanel.add(dateField);
		
		LabelField descriptionField = new LabelField();
		descriptionField.setName(Key.DESCRIPTION.name());
		descriptionField.setFieldLabel(i18n.actionDescriptionFieldLabel());
		descriptionField.setStyleAttribute("font-size", "12pt");
		formPanel.add(descriptionField);
		
		LabelField entityField = new LabelField();
		entityField.setName(Key.ENTITY_NAME.name());
		entityField.setFieldLabel(i18n.actionEntityFieldLabel());
		entityField.setStyleAttribute("font-size", "12pt");
		formPanel.add(entityField);
		
		studentNameField = new LabelField();
		studentNameField.setName(Key.STUDENT_NAME.name());
		studentNameField.setFieldLabel(i18n.actionStudentNameFieldLabel());
		studentNameField.setStyleAttribute("font-size", "12pt");
		formPanel.add(studentNameField);
		
		LabelField actorNameField = new LabelField();
		actorNameField.setName(Key.GRADER_NAME.name());
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
		
		
		loader = new BasePagingLoader<PagingLoadResult<UserEntityAction<?>>>(proxy, new ModelReader());
		
		pagingToolBar = new PagingToolBar(20);
		pagingToolBar.bind(loader);
		
		setBottomComponent(pagingToolBar);
		
		ArrayList<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		
		ColumnConfig column = new ColumnConfig(Key.DATE_RECORDED.name(), i18n.actionDateFieldLabel(), 200);
		configs.add(column);
		
		column = new ColumnConfig(Key.DESCRIPTION.name(), i18n.actionDescriptionFieldLabel(), 150);
		configs.add(column);
		
		column = new ColumnConfig(Key.ENTITY_NAME.name(), i18n.actionEntityFieldLabel(), 180);
		configs.add(column);
		
		column = new ColumnConfig(Key.STUDENT_NAME.name(), i18n.actionStudentNameFieldLabel(), 140);
		configs.add(column);
		
		columnModel = new ColumnModel(configs);
		store = new ListStore<UserEntityAction<?>>(loader);
		selectionListener = new SelectionChangedListener<UserEntityAction<?>>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<UserEntityAction<?>> se) {
				UserEntityAction<?> action = se.getSelectedItem();
				
				formPanel.hide();
				
				if (action == null) 
					formBinding.unbind();
				else 
					formBinding.bind(action);
				
				initState(action);
				formPanel.show();
			}
			
		};
		
		selectionModel = new GridSelectionModel<UserEntityAction<?>>();
		selectionModel.addSelectionChangedListener(selectionListener);
		grid = new Grid<UserEntityAction<?>>(store, columnModel);
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
	
	private void initState(UserEntityAction<?> action) {
		boolean isGradebook = action.getEntityType() == EntityType.GRADEBOOK;
		boolean isCategory = action.getEntityType() == EntityType.CATEGORY;
		boolean isItem = action.getEntityType() == EntityType.ITEM;
		boolean isGradeOrCourseGrade = (action.getEntityType() == EntityType.GRADE_RECORD ||
				action.getEntityType() == EntityType.COURSE_GRADE_RECORD);
		
		studentNameField.setVisible(isGradeOrCourseGrade);
		fieldSet.setVisible(!isGradeOrCourseGrade);
		
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

	
/*
	 private void createExpander() {

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		XTemplate tpl = XTemplate.create(getTemplate());

		RowExpander expander = new RowExpander();
		expander.setTemplate(tpl);

		configs.add(expander);

		ColumnConfig column = new ColumnConfig();
		column.setId(Action.Key.ENTITY_NAME.name());
		column.setHeader("Name");
		column.setWidth(200);
		configs.add(column);

		
		loader = newLoader();
		ListStore<BaseModel> store = new ListStore<BaseModel>(loader);
		//store.add(stocks);

		ColumnModel cm = new ColumnModel(configs);

		Grid<BaseModel> grid = new Grid<BaseModel>(store, cm);
		grid.setBorders(true);
		grid.addPlugin(expander);
		grid.getView().setForceFit(true);
		add(grid);

	} 
	 
	private native String getTemplate() /--*-{
		var html = [ 
		'<p><b>Type:</b> {ENTITY_TYPE}</p>', 
		'<p><b>Action:</b> {ACTION_TYPE}</p>',  
		'<tpl if="ACTION_TYPE ==\'GRADED\'"><p><b>Score:</b> {score}</p></tpl>' 
		]; 
		return html.join("");
	}-*--/;  
	
	@Override
	protected void addComponents() {
		setBottomComponent(pagingToolBar);
	}
	
	@Override
	protected void addGridListenersAndPlugins(final EditorGrid<UserEntityAction> grid) {
		// We only need to do this once
		if (rendered)
			return;
		
		ComponentPlugin plugin = (ComponentPlugin)cm.getColumn(0);
		grid.addPlugin(plugin);
		grid.getView().setForceFit(true); 
	}
	
	@Override
	protected void reconfigureGrid(CustomColumnModel cm) {
		super.reconfigureGrid(cm);
	}
	
	@Override
	protected CustomColumnModel newColumnModel(GradebookModel selectedGradebook) {
		
		String gradebookUid = selectedGradebook.getGradebookUid();
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		
		String html = new StringBuilder()
			.append("<p><b>Desc:</b> {").append(Action.Key.DESCRIPTION.name()).append("}</p>")
			.append("<p><b>Name:</b> {").append(ItemModel.Key.NAME.name()).append("}</p>")
			.toString();
		
		XTemplate tpl = XTemplate.create(html);

		RowExpander expander = new RowExpander();
		expander.setTemplate(tpl);

		configs.add(expander); 
		
		ColumnConfig datePerformed = new ColumnConfig(Action.Key.DATE_PERFORMED.name(),
				"Timestamp", 200);
		datePerformed.setDateTimeFormat(DateTimeFormat.getMediumDateTimeFormat());
		datePerformed.setHidden(true);
		configs.add(datePerformed);
		
		ColumnConfig dateRecorded = new ColumnConfig(Action.Key.DATE_RECORDED.name(),
				"Time Recorded", 200);
		dateRecorded.setDateTimeFormat(DateTimeFormat.getMediumDateTimeFormat());
		dateRecorded.setHidden(false);
		configs.add(dateRecorded);
		
		ColumnConfig entityType = new ColumnConfig(Action.Key.ENTITY_TYPE.name(),
				"Type", 120);
		configs.add(entityType);
		
		ColumnConfig graderName = new ColumnConfig(Action.Key.GRADER_NAME.name(),
				"Grader", 120);
		
		configs.add(graderName);
		
		CustomColumnModel cm = new CustomColumnModel(gradebookUid, gridId, configs);
		
		return cm;
	}
	
	
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		loader.load(0, getPageSize());
	}*/

}
