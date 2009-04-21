package org.sakaiproject.gradebook.gwt.client.gxt.view.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.action.RemoteCommand;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityCreateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.UserChangeEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.upload.ImportHeader;
import org.sakaiproject.gradebook.gwt.client.gxt.upload.ImportHeader.Field;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.SpreadsheetModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.MemoryProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.MarginData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Element;

public class ImportPanel extends ContentPanel {

	private static final String ADVANCED_SHOW = "Edit Columns";
	private static final String ADVANCED_HIDE = "Hide Columns";
	
	private enum Step { ONE, TWO, THREE };
	
	private Step currentStep;

	private ListStore<BaseModel> rowStore;
	private ListStore<BeanModel> itemStore;
	private ListStore<BaseModel> resultStore;
	private ListStore<ItemModel> categoriesStore;
	private Grid<BaseModel> grid;
	private FormPanel fileUploadPanel;
	private Map<String, ImportHeader> headerMap;
	
	private LayoutContainer mainCardLayoutContainer, subCardLayoutContainer;
	private CardLayout mainCardLayout, subCardLayout;
	
	private LayoutContainer advancedContainer, resultsContainer;
	
	private TabPanel tabPanel;
	private TabItem step1, step2, step3;
	
	private FieldSet previewFieldSet;
	private Button previewButton, advancedButton, submitButton, cancelButton; //, nextButton, backButton, cancelButton;
	
	private LayoutContainer step1Container, step2Container, step3Container;
	
	private List<ColumnConfig> previewColumns;
	
	private MemoryProxy<ListLoadResult<BaseModel>> proxy;
	private ListLoader<?> loader;
	//private PagingToolBar toolBar;
	private List<BaseModel> resultModels;
	private MessageBox uploadBox;
	
	public ImportPanel() {
		super();
		setCollapsible(false);
		setFrame(true);
		setHeaderVisible(true);
		setHeading("Import");
		//setHeight(560);
		setHideCollapseTool(true);
		setLayout(new FitLayout());
		
		headerMap = new HashMap<String, ImportHeader>();
		
		/*addListener(Events.BeforeClose, new Listener<ComponentEvent>() {

			public void handleEvent(ComponentEvent be) {
				
				if (step1Container != null)
					step1Container.removeAll();
				if (step2Container != null)
					step2Container.removeAll();
				if (step3Container != null)
					step3Container.removeAll();
				
				if (rowStore != null)
					rowStore.removeAll();
				if (itemStore != null)
					itemStore.removeAll();
				
			}
			
		});*/
		
		currentStep = Step.ONE;
		
		// Set up store
		resultStore = new ListStore<BaseModel>();
		
		categoriesStore = new ListStore<ItemModel>();
	}
	
	protected void onClose() {
		
	}
	
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);

		
		int fileUploadHeight = 190;
		int subHeight = getHeight() - fileUploadHeight;
		
		mainCardLayout = new CardLayout();
		mainCardLayoutContainer = new LayoutContainer();
		mainCardLayoutContainer.setLayout(mainCardLayout);
		
		subCardLayout = new CardLayout();
		subCardLayoutContainer = new LayoutContainer();
		subCardLayoutContainer.setLayout(subCardLayout);
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		rowStore = new ListStore<BaseModel>();
		rowStore.setMonitorChanges(true);
		ColumnModel cm = new ColumnModel(configs);
		grid = new Grid<BaseModel>(rowStore, cm);
		grid.setLoadMask(false);
		grid.setHeight(300);
		
		previewFieldSet = new FieldSet(); 
		previewFieldSet.setLayout(new FlowLayout());
		previewFieldSet.setHeading("Preview"); 
		previewFieldSet.setHeight(330);
		previewFieldSet.add(grid, new MarginData(5));
		
		subCardLayoutContainer.add(previewFieldSet);
		subCardLayoutContainer.setHeight(subHeight);
				
		step1Container = new LayoutContainer();
		step1Container.setLayout(new RowLayout());
		step1Container.add(buildFileUploadPanel(), new RowData(1, fileUploadHeight));
		step1Container.add(subCardLayoutContainer, new RowData(1, subHeight, new Margins(5)));
		
		mainCardLayoutContainer.add(step1Container);
		mainCardLayout.setActiveItem(step1Container);
		
		resultsContainer = buildResultsContainer();
		
		subCardLayoutContainer.add(resultsContainer);
		
		add(mainCardLayoutContainer); //, new ColumnData(1));
		//add(buildButtonContainer(), new ColumnData(130));
		
		
		/*
		tabPanel = new TabPanel();
		step1 = new TabItem("Step 1");
		step1.add(step1Container);
		step1.setLayout(new FitLayout());
		tabPanel.add(step1);
	
		step2Container = new LayoutContainer();
		step2Container.setLayout(new FitLayout());

		step2 = new TabItem("Step 2");
		step2.setEnabled(false);
		step2.setLayout(new FitLayout());
		step2.add(step2Container);
		tabPanel.add(step2);

		step3Container = buildResultsContainer();
		//step3Container.setLayout(new FitLayout());
		//step3Container.add(buildResultsContainer());
		
		step3 = new TabItem("Step 3");
		step3.setLayout(new FitLayout());
		step3.setEnabled(false);
		step3.add(step3Container);
		tabPanel.add(step3);
		
		add(tabPanel, new ColumnData(1));
		add(buildButtonContainer(), new ColumnData(130));
		*/
	}
	
	/*protected void gotoStep1() {
		tabPanel.setSelection(step1);
		backButton.setEnabled(false);
		step1.setEnabled(true);
		step2.setEnabled(false);
		step3.setEnabled(false);
		currentStep = Step.ONE;
		
		nextButton.setText("Next");
	}
	
	protected void gotoStep2() {
		step2.setEnabled(true);
		tabPanel.setSelection(step2);
		
		ColumnModel cm = grid.getColumnModel();
		List<ImportHeader> headers = new ArrayList<ImportHeader>();
		
		// First, we need to ensure that all of the assignments exist
		for (int i=0;i<cm.getColumnCount();i++) {
			ColumnConfig config = cm.getColumn(i);
		
			//if (!config.isHidden()) {
				BaseModel model = new BaseModel();
				
				String id = config.getId();
				
				ImportHeader header = headerMap.get(id);
				
				if (header != null) {
					//header.setChecker(config.isHidden());
					headers.add(header);
				}
			//}
			
		}
		
		step2Container.add(buildItemContainer(headers));
		step2.layout();
		
		nextButton.setText("Finish");
		backButton.setEnabled(true);
		step1.setEnabled(false);
		step2.setEnabled(true);
		step3.setEnabled(false);
		currentStep = Step.TWO;
		//confirmButton.setEnabled(true);
	}
	
	protected void gotoStep3() {
		step3.setEnabled(true);
		tabPanel.setSelection(step3);
		
		/--*List<BeanModel> models = new ArrayList<BeanModel>();
		BeanModelFactory factory = BeanModelLookup.get().getFactory(headers.get(0).getClass());
        if (factory == null) {
          throw new RuntimeException("No BeanModelFactory found for " + headers.get(0).getClass());
        }
        List<BeanModel> converted = factory.createModel(headers);
        models.addAll(converted);
		*--/
		
		SpreadsheetModel spreadsheetModel = new SpreadsheetModel();
		
		// Create new items
		List<ItemModel> items = new ArrayList<ItemModel>();
		for (BeanModel importHeader : itemStore.getModels()) {
			//String assignmentId = importHeader.get("id");
			//if (assignmentId == null)
			//	createNewItem((Long)ImportHeader.get("categoryId"), (String)ImportHeader.get("headerName"), Double.valueOf(0d), Double.parseDouble((String)ImportHeader.get("points")), new Date());			
		
			ItemModel item = new ItemModel();
			item.setIdentifier((String)importHeader.get("id"));
			item.setCategoryId((Long)importHeader.get("categoryId"));
			item.setCategoryName((String)importHeader.get("categoryName"));
			item.setName((String)importHeader.get("headerName"));
			
			boolean isPercentage = importHeader.get("isPercentage") != null && ((Boolean)importHeader.get("isPercentage")).booleanValue();
			if (!isPercentage) 
				item.setPoints((Double)importHeader.get("points"));
			
			item.setIsPercentage(Boolean.valueOf(isPercentage));
			
			items.add(item);
		}
		
		spreadsheetModel.setHeaders(items);
		
		List<StudentModel> rows = new ArrayList<StudentModel>();
		for (BaseModel importRow : rowStore.getModels()) {
			
			String uid = importRow.get("userUid");
			if (uid == null)
				uid = importRow.get("userImportId");
			
			StudentModel student = new StudentModel();
			student.setIdentifier(uid);
			
			for (ColumnConfig column : previewColumns) {
				String id = column.getId();
				student.set(id, importRow.get(id));
			}
			rows.add(student);
		}
		
		spreadsheetModel.setRows(rows);
		
		uploadSpreadsheet(spreadsheetModel);
		
		nextButton.setText("Close");
		backButton.setVisible(false);
		cancelButton.setVisible(false);
		step1.setEnabled(false);
		step2.setEnabled(false);
		step3.setEnabled(true);
		currentStep = Step.THREE;
	}*/
	
	private void uploadSpreadsheet(SpreadsheetModel spreadsheetModel) {
		GradebookModel gbModel = Registry.get(AppConstants.CURRENT);
		UserEntityCreateAction<SpreadsheetModel> action = 
			new UserEntityCreateAction<SpreadsheetModel>(gbModel, EntityType.SPREADSHEET, spreadsheetModel);
		
		final MessageBox box = MessageBox.wait("Progress", "Uploading grades, please wait...", "Uploading...");
		
		RemoteCommand<SpreadsheetModel> remoteCommand = 
			new RemoteCommand<SpreadsheetModel>() {

				@Override
				public void onCommandFailure(UserEntityAction<SpreadsheetModel> action, Throwable caught) {
					box.close();
				}
				
			
				@Override
				public void onCommandSuccess(UserEntityAction<SpreadsheetModel> action, SpreadsheetModel result) {
					
					subCardLayout.setActiveItem(resultsContainer);
					
					action.setModel(result);
					
					//BaseModel model = new BaseModel();
					//model.set("desc", new StringBuilder().append("Created new assignment as ").append(result.getName()).toString());
					
					//resultStore.add(model);
					
					
					resultModels = new ArrayList<BaseModel>();
					for (String desc : result.getResults()) {
						//resultDataList.add(desc);
						BaseModel model = new BaseModel();
						model.set("desc", desc);
						resultModels.add(model);
					}
					//resultStore.add(models);
					proxy.setData(new BaseListLoadResult<BaseModel>(resultModels)); //, 0, resultModels.size()));
					//loader.load(0, 50);
					//toolBar.refresh();
					box.setProgressText("Loading");
					loader.load();
					
					box.close();
					
					Dispatcher.forwardEvent(GradebookEvents.RefreshCourseGrades.getEventType());
					
					//step3Container.add(buildResultsContainer());
					//step3.layout();
					
					fireEvent(GradebookEvents.UserChange.getEventType(), new UserChangeEvent(action));
				}
			
		};
		
		remoteCommand.execute(action);
	}
	
	/*
	private LayoutContainer buildButtonContainer() {
		LayoutContainer buttonContainer = new LayoutContainer();
		buttonContainer.setLayout(new RowLayout());
		
		backButton = new Button("Back");
		backButton.setMinWidth(120);
		backButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				switch (currentStep) {
				case TWO:	
					gotoStep1();
					break;
				case THREE:
					gotoStep2();
					break;
				}
			}
			
		});
		
		backButton.setEnabled(false);
		
		nextButton = new Button("Next");
		nextButton.setMinWidth(120);
		nextButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				switch (currentStep) {
				case ONE:	
					gotoStep2();
					break;
				case TWO:
					gotoStep3();
					break;
				case THREE:
					Dispatcher.forwardEvent(GradebookEvents.StopImport);
					break;
				}
			}
		});
		buttonContainer.add(nextButton, new RowData(120, 20, new Margins(5)));
		nextButton.setEnabled(false);
		
		buttonContainer.add(backButton, new RowData(120, 20, new Margins(5)));
				
		cancelButton = new Button("Cancel");
		cancelButton.setMinWidth(120);
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				Dispatcher.forwardEvent(GradebookEvents.StopImport);
				fileUploadPanel.clear();
			}
		});
		buttonContainer.add(cancelButton, new RowData(120, 20, new Margins(5)));
		
		return buttonContainer;
	}*/
	
	private FormPanel buildFileUploadPanel() {
		GradebookModel gbModel = Registry.get(AppConstants.CURRENT);
		
		FormLayout formLayout = new FormLayout();
		formLayout.setDefaultWidth(350);
		formLayout.setLabelWidth(120);
		
		//String uri = "/rest/upload/" + gradebookUid;
		
		fileUploadPanel = new FormPanel();

		fileUploadPanel.setHeaderVisible(false);

		fileUploadPanel.setFrame(true);
		fileUploadPanel.setAction(GWT.getModuleBaseURL() + "/importHandler");
		fileUploadPanel.setEncoding(Encoding.MULTIPART);
		fileUploadPanel.setMethod(Method.POST);
		fileUploadPanel.setPadding(4);
		fileUploadPanel.setButtonAlign(HorizontalAlignment.RIGHT);

		fileUploadPanel.setWidth(1);
		fileUploadPanel.setLayout(formLayout);
		

		FileUploadField file = new FileUploadField() {
			@Override
			protected void onChange(ComponentEvent ce) {
				super.onChange(ce);
				previewButton.setEnabled(true);
			}
		};
		file.setAllowBlank(false);
		file.setFieldLabel("File");
		file.setName("Test");

		fileUploadPanel.add(file);
		
		
		CheckBox delimiterComma = new CheckBox();
		delimiterComma.setId("delimiter:comma");
		delimiterComma.setBoxLabel("Comma");
		delimiterComma.setValue(Boolean.TRUE);
		
		CheckBox delimiterTab = new CheckBox();
		delimiterTab.setId("delimiter:tab");
		delimiterTab.setBoxLabel("Tab");
		delimiterTab.setValue(Boolean.TRUE);
		
		CheckBox delimiterSpace = new CheckBox();
		delimiterSpace.setId("delimiter:space");
		delimiterSpace.setBoxLabel("Space");

		CheckBox delimiterColon = new CheckBox();
		delimiterColon.setId("delimiter:colon");
		delimiterColon.setBoxLabel("Colon");

		CheckBoxGroup group = new CheckBoxGroup();
		group.setId("delimiters");
		group.setName("Delimiters");
		group.setFieldLabel("Separator");
		group.add(delimiterComma);
		group.add(delimiterTab);
		group.add(delimiterSpace);
		group.add(delimiterColon);
		fileUploadPanel.add(group);
		
		delimiterComma.setName("delimiter:comma");
		delimiterTab.setName("delimiter:tab");
		delimiterSpace.setName("delimiter:space");
		delimiterColon.setName("delimiter:colon");
		
		HiddenField<String> gradebookUidField = new HiddenField<String>();
		gradebookUidField.setName("gradebookUid");
		gradebookUidField.setValue(gbModel.getGradebookUid());
		//left.add(gradebookUidField);
		fileUploadPanel.add(gradebookUidField);
		
		
		final CheckBox excludeGradeData = new CheckBox();
		excludeGradeData.setName("excludegrades");
		excludeGradeData.setFieldLabel("Exclude grades");
		fileUploadPanel.add(excludeGradeData);
		
		advancedButton = new Button(ADVANCED_SHOW);
		advancedButton.setMinWidth(120);
		advancedButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				Component activeItem = subCardLayout.getActiveItem();
				if (activeItem != null && activeItem.equals(previewFieldSet)) {
					subCardLayout.setActiveItem(advancedContainer);
					advancedButton.setText(ADVANCED_HIDE);
				} else {
					subCardLayout.setActiveItem(previewFieldSet);
					advancedButton.setText(ADVANCED_SHOW);
				}
			}
			
		});
		fileUploadPanel.addButton(advancedButton);
		
		previewButton = new Button("Preview");
		previewButton.setMinWidth(120);
		previewButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				subCardLayout.setActiveItem(previewFieldSet);
				uploadBox = MessageBox.wait("Progress", "Reading file, please wait...", "Parsing...");
				fileUploadPanel.submit();
				submitButton.setEnabled(true);
			}
			
		});
		fileUploadPanel.addButton(previewButton);
		
		submitButton = new Button("Submit");
		submitButton.setMinWidth(120);
		submitButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				SpreadsheetModel spreadsheetModel = new SpreadsheetModel();
				
				// Create new items
				List<ItemModel> items = new ArrayList<ItemModel>();
				for (BeanModel importHeader : itemStore.getModels()) {
					//String assignmentId = importHeader.get("id");
					//if (assignmentId == null)
					//	createNewItem((Long)ImportHeader.get("categoryId"), (String)ImportHeader.get("headerName"), Double.valueOf(0d), Double.parseDouble((String)ImportHeader.get("points")), new Date());			
				
					ItemModel item = new ItemModel();
					item.setIdentifier((String)importHeader.get("id"));
					item.setCategoryId((Long)importHeader.get("categoryId"));
					item.setCategoryName((String)importHeader.get("categoryName"));
					item.setName((String)importHeader.get("headerName"));
					
					boolean isPercentage = importHeader.get("isPercentage") != null && ((Boolean)importHeader.get("isPercentage")).booleanValue();
					if (!isPercentage) 
						item.setPoints((Double)importHeader.get("points"));
					
					item.setIsPercentage(Boolean.valueOf(isPercentage));
					
					items.add(item);
				}
				
				spreadsheetModel.setHeaders(items);
				
				List<StudentModel> rows = new ArrayList<StudentModel>();
				for (BaseModel importRow : rowStore.getModels()) {
					
					String uid = importRow.get("userUid");
					if (uid == null)
						uid = importRow.get("userImportId");
					
					StudentModel student = new StudentModel();
					student.setIdentifier(uid);
					
					for (ColumnConfig column : previewColumns) {
						String id = column.getId();
						student.set(id, importRow.get(id));
					}
					rows.add(student);
				}
				
				spreadsheetModel.setRows(rows);
				
				uploadSpreadsheet(spreadsheetModel);
				
			}
		});
		fileUploadPanel.addButton(submitButton);
		submitButton.setEnabled(false);
		
		cancelButton = new Button("Cancel");
		cancelButton.setMinWidth(120);
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				Dispatcher.forwardEvent(GradebookEvents.StopImport.getEventType());
				fileUploadPanel.clear();
			}
		});
		
		fileUploadPanel.addButton(cancelButton);
		
		
		advancedButton.setEnabled(false);
		previewButton.setEnabled(false);
				
		fileUploadPanel.addListener(Events.Submit, new Listener<FormEvent>() {

			public void handleEvent(FormEvent fe) {
				rowStore.removeAll();
				
				JSONValue jsonValue = JSONParser.parse(fe.resultHtml);
				JSONObject jsonObject = jsonValue.isObject().get("org.sakaiproject.gradebook.gwt.client.gxt.upload.ImportFile").isObject();
				
				JSONArray headersArray = getArray(jsonObject, "items");
				previewColumns = new ArrayList<ColumnConfig>();
				
				if (headersArray != null) {	
					headerMap.clear();
					for (int i=0;i<headersArray.size();i++) {
						String name = getString(headersArray.get(i).isObject(), "value");
						String id = getString(headersArray.get(i).isObject(), "id");
						String headerName = getString(headersArray.get(i).isObject(), "headerName");
						Double points = getDouble(headersArray.get(i).isObject(), "points");
						String field = getString(headersArray.get(i).isObject(), "field");
						String categoryName = getString(headersArray.get(i).isObject(), "categoryName");
						
						ColumnConfig column = new ColumnConfig(id, name, 200);
						previewColumns.add(column);
						
						ImportHeader header = new ImportHeader(Field.valueOf(field), headerName);
						header.setId(id);
						header.setHeaderName(headerName);
						header.setPoints(points);
						header.setField(field);
						header.setCategoryName(categoryName);
						
						if (header.getField() != null && header.getField().equals("ITEM"))
							headerMap.put(id, header);
					}
					ColumnModel cm = new ColumnModel(previewColumns);
					grid.reconfigure(rowStore, cm);
					
					advancedButton.setEnabled(true);
					//grid.setVisible(true);
					//nextButton.setEnabled(true);
				}

				boolean isGradeDataExcluded = excludeGradeData.getValue() != null && excludeGradeData.getValue().booleanValue();
				if (! isGradeDataExcluded) {
					JSONArray rowsArray = getArray(jsonObject, "rows");
					List<BaseModel> models = new ArrayList<BaseModel>();
					if (rowsArray != null) {
						StringBuilder heading = new StringBuilder("Preview (").append(rowsArray.size()).append(" records)");
						previewFieldSet.setHeading(heading.toString());
						for (int i=0;i<rowsArray.size();i++) {
							JSONObject rowObject = rowsArray.get(i).isObject();
							String userUid = getString(rowObject.isObject(), "userUid");
							String userImportId = getString(rowObject.isObject(), "userImportId");
							String userDisplayName = getString(rowObject.isObject(), "userDisplayName");
							JSONArray columnsArray = getArray(rowObject, "columns");
							
							BaseModel model = new BaseModel();
							model.set("userUid", userUid);
							model.set("userImportId", userImportId);
							model.set("userDisplayName", userDisplayName);
							
							if (columnsArray != null) {
								for (int j=0;j<columnsArray.size();j++) {
									if (previewColumns != null && previewColumns.size() > j) {
										ColumnConfig config = previewColumns.get(j);
										if (config != null)
											model.set(config.getId(), columnsArray.get(j).isString().stringValue());
									
									}
								}
								
							}
							
							models.add(model);
						}
					}
					
					if (models != null)
						rowStore.add(models);
					
					uploadBox.close();
					
					ColumnModel cm = grid.getColumnModel();
					List<ImportHeader> headers = new ArrayList<ImportHeader>();
						
					// First, we need to ensure that all of the assignments exist
					for (int i=0;i<cm.getColumnCount();i++) {
						ColumnConfig config = cm.getColumn(i);
						
							//if (!config.isHidden()) {
							BaseModel model = new BaseModel();
								
							String id = config.getId();
								
							ImportHeader header = headerMap.get(id);
							
							if (header != null) {
								//header.setChecker(config.isHidden());
								headers.add(header);
							}
						//}
							
					}
						
					advancedContainer = buildItemContainer(headers);
					
					subCardLayoutContainer.add(advancedContainer);
					
				}
				
			}
			
		});
		
		return fileUploadPanel;
	}
	
	private LayoutContainer buildItemContainer(List<ImportHeader> headers) {
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setHeaderVisible(false);
		
		List<ColumnConfig> itemColumns = new ArrayList<ColumnConfig>();
		
		TextField<String> textField = new TextField<String>();
		textField.addInputStyleName("gbTextFieldInput");
		CellEditor textCellEditor = new CellEditor(textField);
		
		ColumnConfig name = new ColumnConfig("headerName", "Item", 200);
		name.setEditor(textCellEditor);
		itemColumns.add(name);
		
		ColumnConfig points = new ColumnConfig("points", "Points", 100);
		points.setEditor(textCellEditor);
		itemColumns.add(points);

		ComboBox<ItemModel> categoryPicker = new ComboBox<ItemModel>(); 
		categoryPicker.setAllowBlank(false); 
		categoryPicker.setAllQuery(null);
		categoryPicker.setDisplayField(ItemModel.Key.NAME.name());  
		categoryPicker.setEditable(true);
		categoryPicker.setEmptyText("Required");
		categoryPicker.setFieldLabel("Category");
		categoryPicker.setForceSelection(true);
		categoryPicker.setStore(categoriesStore);
		categoryPicker.addInputStyleName("gbTextFieldInput");

		ColumnConfig category = new ColumnConfig("categoryName", "Category", 140);
		category.setEditor(new CellEditor(categoryPicker) {
			
			@Override
			public Object postProcessValue(Object value) {
			    if (value != null) {
			    	ItemModel model = (ItemModel)value;
			    	return model.getName();
			    }
				return "None/Default";
			}

			@Override
			public Object preProcessValue(Object value) {
				ComboBox<ItemModel> combo = (ComboBox<ItemModel>)getField();
				List<ItemModel> models = categoriesStore.getModels();
				
				for (ItemModel model : models) {
					if (model.getName().equals(value))
						return model;
				}
				
				return null;
			}
			
		});
		itemColumns.add(category);
		
		ColumnModel itemColumnModel = new ColumnModel(itemColumns);
		itemStore = new ListStore<BeanModel>();
		
		EditorGrid<BeanModel> itemGrid = new EditorGrid<BeanModel>(itemStore, itemColumnModel);
		//itemGrid.setSelectionModel(sm);
		//itemGrid.addPlugin(sm);
		itemGrid.setHeight(300);
		List<BeanModel> models = new ArrayList<BeanModel>();
		BeanModelFactory factory = BeanModelLookup.get().getFactory(headers.get(0).getClass());
        if (factory == null) {
          throw new RuntimeException("No BeanModelFactory found for " + headers.get(0).getClass());
        }
        List<BeanModel> converted = factory.createModel(headers);
        models.addAll(converted);
		
        itemStore.add(models);
        
        //sm.selectAll();
        
        /*int i=0;
        for (ImportHeader header : headers) {
        	if (header.getChecker()) 
        		sm.select(i);
        	
        	i++;
        }
        */
        
		panel.add(itemGrid);
		
		
		LayoutContainer container = new LayoutContainer();
		container.setLayout(new RowLayout());
		
		ContentPanel directionsPanel = new ContentPanel();
		directionsPanel.setFrame(true);
		directionsPanel.setWidth(1);
		directionsPanel.setHeaderVisible(false);
		
		String text = "Listed below are the items that will be imported, according to the column headers "
			+ "you provided in your file. Note that items that do not yet exist in the current gradebook, "
			+ "or that exist in a different category from the one selected, will be created on import. ";
		
		directionsPanel.add(new LabelField(text));
		
		container.add(directionsPanel, new RowData(1, -1));
		
		
		FitLayout fitLayout = new FitLayout();
		FieldSet itemFieldSet = new FieldSet(); 
		itemFieldSet.setLayout(fitLayout);
		itemFieldSet.setHeading("Items"); 
		itemFieldSet.setHeight(320);  
		itemFieldSet.add(panel, new MarginData(5));
		
		container.add(itemFieldSet, new RowData(1, 1));
		
		return container;
	}
	
	private FieldSet buildPreviewFieldSet() {
		ContentPanel dataPanel = new ContentPanel();
		dataPanel.setLayout(new FlowLayout());
		dataPanel.setHeaderVisible(false);
		//dataPanel.setScrollMode(Scroll.AUTO);
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		rowStore = new ListStore<BaseModel>();
		rowStore.setMonitorChanges(true);
		ColumnModel cm = new ColumnModel(configs);
		grid = new Grid<BaseModel>(rowStore, cm);
		grid.setLoadMask(false);
		//grid.setVisible(false);
		grid.setHeight(300);
		dataPanel.add(grid);
		
		//rowStore.add((BaseModel)null);
		
		//formPanel.add(dataPanel);
		
		//add(dataPanel, new RowData(1, 1));
		
		FitLayout fitLayout = new FitLayout();
		previewFieldSet = new FieldSet(); 
		previewFieldSet.setLayout(fitLayout);
		previewFieldSet.setHeading("Preview"); 
		previewFieldSet.setHeight(330);
		previewFieldSet.add(dataPanel, new MarginData(5));
		
		//previewFieldSet.setVisible(false);
		
		return previewFieldSet;
	}
	
	
	private LayoutContainer buildResultsContainer() {
		
		final ContentPanel container = new ContentPanel();
		container.setHeaderVisible(false);
		container.setLayout(new FitLayout());
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		
		ColumnConfig desc = new ColumnConfig("desc", "Description", 2000);
		configs.add(desc);
		
		ColumnModel resultColumnModel = new ColumnModel(configs);
		
		proxy = new MemoryProxy<ListLoadResult<BaseModel>>(null);
		loader = new BaseListLoader(proxy);
		
		//loader.load(0, 50);  
		
		resultStore = new ListStore<BaseModel>(loader);  
		/*toolBar = new PagingToolBar(50);  
		toolBar.bind(loader); 
		container.setBottomComponent(toolBar);
		*/
		
		EditorGrid<BaseModel> resultGrid = new EditorGrid<BaseModel>(resultStore, resultColumnModel);
		//itemGrid.setSelectionModel(sm);
		//itemGrid.addPlugin(sm);
		resultGrid.setHeight(380);
		resultGrid.setWidth(2000);
		//resultGrid.setAutoExpandColumn("desc");
		resultGrid.setAutoExpandMax(2000);
		
		/*BasePagingLoader loader;
		ListView<BaseModel> view = new ListView<BaseModel>(resultStore);
		view.setDisplayProperty("desc");
		view.setHeight(300);
		container.add(view);*/
		container.add(resultGrid);
		container.setHeight(380);
		
		return container;
	}
	
	
	private JSONArray getArray(JSONObject object, String property) {
		
		JSONValue value = object.get(property);
		
		if (value == null)
			return null;
		
		return value.isArray();
	}
	
	private Double getDouble(JSONObject object, String property) {
		if (object == null)
			return null;
		
		JSONValue value = null;
		if (property != null) {
			value = object.get(property);
			if (value == null)
				return null;
		} else {
			value = object;
		}
		
		JSONNumber number = value.isNumber();
		if (number == null)
			return null;
		
		return Double.valueOf(number.doubleValue());
	}
	
	private String getString(JSONObject object, String property) {
		if (object == null)
			return null;
		
		JSONValue value = null;
		if (property != null) {
			value = object.get(property);
			if (value == null)
				return null;
		} else {
			value = object;
		}
		
		JSONString string = value.isString();
		if (string == null)
			return null;
		
		return string.stringValue();
	}
	
	/*private ImportFile parseImport(String content) {
		return JSONParser.parse(content);
	}*/
	
}
