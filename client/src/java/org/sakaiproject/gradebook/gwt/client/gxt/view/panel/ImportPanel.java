package org.sakaiproject.gradebook.gwt.client.gxt.view.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.action.RemoteCommand;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityCreateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.gxt.ItemModelProcessor;
import org.sakaiproject.gradebook.gwt.client.gxt.custom.widget.grid.BaseCustomGridView;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.UserChangeEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.upload.ImportHeader;
import org.sakaiproject.gradebook.gwt.client.gxt.upload.ImportHeader.Field;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.ItemCellRenderer;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.ItemNumberCellRenderer;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.ItemTreeTableBinder;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.SpreadsheetModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.MemoryProxy;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.data.TreeModelReader;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.MarginData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.treetable.TreeTable;
import com.extjs.gxt.ui.client.widget.treetable.TreeTableColumn;
import com.extjs.gxt.ui.client.widget.treetable.TreeTableColumnModel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Accessibility;

public class ImportPanel extends ContentPanel {

	private static final String ADVANCED_SHOW = "Edit Columns";
	private static final String ADVANCED_HIDE = "Hide Columns";
	
	private static int CHARACTER_WIDTH = 7;
	
	private enum Step { ONE, TWO, THREE };
	
	private Step currentStep;

	private FileUploadField file;
	
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
	
	private TabItem previewTab, columnsTab;
	
	private FieldSet previewFieldSet;
	private Button previewButton, advancedButton, submitButton, cancelButton; //, nextButton, backButton, cancelButton;
	
	private LayoutContainer step1Container, step2Container, step3Container;
	
	private List<ColumnConfig> previewColumns;
	
	private MemoryProxy<ListLoadResult<BaseModel>> proxy;
	private ListLoader<?> loader;
	//private PagingToolBar toolBar;
	private List<BaseModel> resultModels;
	private MessageBox uploadBox;
	
	private Listener fieldListener;
	private I18nConstants i18n;
	
	public ImportPanel(I18nConstants i18n) {
		super();
		this.i18n = i18n;
		setCollapsible(false);
		setFrame(true);
		setHeaderVisible(true);
		setHeading(i18n.headerImport());
		setHideCollapseTool(true);
		setLayout(new FitLayout());
		
		headerMap = new HashMap<String, ImportHeader>();
		
		fieldListener = new Listener<FieldEvent>() {

			public void handleEvent(FieldEvent be) {
				readFile();
			}
			
		};
		
		currentStep = Step.ONE;
		
		// Set up store
		resultStore = new ListStore<BaseModel>();
		
		categoriesStore = new ListStore<ItemModel>();
	}
	
	protected void onClose() {
		
	}
	
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);

		GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
		
		categoriesStore.removeAll();
		if (selectedGradebook != null) {
			ItemModel gradebookItemModel = selectedGradebook.getGradebookItemModel();
		
			ItemModelProcessor processor = new ItemModelProcessor(gradebookItemModel) {
				
				@Override
				public void doCategory(ItemModel categoryModel) {
					categoriesStore.add(categoryModel);
				}
				
			};
			
			processor.process();
		}
		
		
		int fileUploadHeight = 100;
		int subHeight = getHeight() - fileUploadHeight;
		
		mainCardLayout = new CardLayout();
		mainCardLayoutContainer = new LayoutContainer();
		mainCardLayoutContainer.setLayout(mainCardLayout);
		
		subCardLayout = new CardLayout();
		subCardLayoutContainer = new LayoutContainer();
		subCardLayoutContainer.setLayout(subCardLayout);
		
		tabPanel = new TabPanel();
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		rowStore = new ListStore<BaseModel>();
		rowStore.setMonitorChanges(true);
		ColumnModel cm = new ColumnModel(configs);
		grid = new Grid<BaseModel>(rowStore, cm);
		grid.setLoadMask(false);
		grid.setHeight(300);
		
		grid.setView(new BaseCustomGridView() {
			@Override
			protected String markupCss(Record record, ModelData model, String property, boolean isShowDirtyCells, boolean isPropertyChanged) {
				
				boolean isUserNotFound = DataTypeConversionUtil.checkBoolean((Boolean)model.get("userNotFound"));
					
				if (isUserNotFound)
					return "gbCellDropped";
				
				
				return null;
			}
		});
		
		/*previewFieldSet = new FieldSet(); 
		previewFieldSet.setLayout(new FlowLayout());
		previewFieldSet.setHeading("Preview"); 
		previewFieldSet.setHeight(330);
		previewFieldSet.add(grid, new MarginData(5));*/
		
		previewTab = new TabItem("Data");
		previewTab.setLayout(new FillLayout());
		previewTab.add(grid);
		
		tabPanel.add(previewTab);
		
		columnsTab = new TabItem("Setup");
		tabPanel.add(columnsTab);
		
		tabPanel.setHeight(400);
		
		subCardLayoutContainer.add(tabPanel);
		subCardLayoutContainer.setHeight(subHeight);
				
		step1Container = new LayoutContainer();
		step1Container.setLayout(new RowLayout());
		step1Container.add(buildFileUploadPanel(), new RowData(1, fileUploadHeight));
		step1Container.add(subCardLayoutContainer, new RowData(1, subHeight, new Margins(5)));
		
		mainCardLayoutContainer.add(step1Container);
		mainCardLayout.setActiveItem(step1Container);
		
		resultsContainer = buildResultsContainer();
		
		subCardLayoutContainer.add(resultsContainer);
		
		add(mainCardLayoutContainer); 
	}

	private void uploadSpreadsheet(SpreadsheetModel spreadsheetModel) {
		GradebookModel gbModel = Registry.get(AppConstants.CURRENT);
		UserEntityCreateAction<SpreadsheetModel> action = 
			new UserEntityCreateAction<SpreadsheetModel>(gbModel, EntityType.SPREADSHEET, spreadsheetModel);
		
		int numberOfLearners = 0;
		List<StudentModel> learners = spreadsheetModel.getRows();
		if (learners != null)
			numberOfLearners = learners.size();
		
		String message = new StringBuilder().append(i18n.uploadingLearnerGradesPrefix()).append(" ")
			.append(numberOfLearners).append(" ").append(i18n.uploadingLearnerGradesSuffix()).toString();
		
		
		final MessageBox box = MessageBox.wait(i18n.uploadingLearnerGradesTitle(), message, i18n.uploadingLearnerGradesStatus());
		
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
					
					resultModels = new ArrayList<BaseModel>();
					for (String desc : result.getResults()) {
						BaseModel model = new BaseModel();
						model.set("desc", desc);
						resultModels.add(model);
					}
					proxy.setData(new BaseListLoadResult<BaseModel>(resultModels)); 
					box.setProgressText("Loading");
					loader.load();
					
					box.close();
					
					Dispatcher.forwardEvent(GradebookEvents.RefreshCourseGrades.getEventType());
					
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
		final GradebookModel gbModel = Registry.get(AppConstants.CURRENT);
		
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
		

		file = new FileUploadField() {
			@Override
			protected void onChange(ComponentEvent ce) {
				super.onChange(ce);
				readFile();
			}
		};
		file.setAllowBlank(false);
		file.setFieldLabel("File");
		file.setName("Test");

		fileUploadPanel.add(file);
		
		/*
		CheckBox delimiterComma = new CheckBox();
		delimiterComma.setId("delimiter:comma");
		delimiterComma.setBoxLabel("Comma");
		delimiterComma.addListener(Events.Change, fieldListener);
		
		CheckBox delimiterTab = new CheckBox();
		delimiterTab.setId("delimiter:tab");
		delimiterTab.setBoxLabel("Tab");
		delimiterTab.setValue(Boolean.TRUE);
		delimiterTab.addListener(Events.Change, fieldListener);
		
		CheckBox delimiterSpace = new CheckBox();
		delimiterSpace.setId("delimiter:space");
		delimiterSpace.setBoxLabel("Space");
		delimiterSpace.addListener(Events.Change, fieldListener);

		CheckBox delimiterColon = new CheckBox();
		delimiterColon.setId("delimiter:colon");
		delimiterColon.setBoxLabel("Colon");
		delimiterColon.addListener(Events.Change, fieldListener);
		

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
		delimiterColon.setName("delimiter:colon");*/
		
		HiddenField<String> gradebookUidField = new HiddenField<String>();
		gradebookUidField.setName("gradebookUid");
		gradebookUidField.setValue(gbModel.getGradebookUid());
		//left.add(gradebookUidField);
		fileUploadPanel.add(gradebookUidField);
		
		
		/*final CheckBox excludeGradeData = new CheckBox();
		excludeGradeData.setName("excludegrades");
		excludeGradeData.setFieldLabel("Exclude grades");
		fileUploadPanel.add(excludeGradeData);*/
		
		/*advancedButton = new Button(ADVANCED_SHOW);
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
		fileUploadPanel.addButton(advancedButton);*/
		
		/*previewButton = new Button("Read File");
		previewButton.setMinWidth(120);
		previewButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				subCardLayout.setActiveItem(tabPanel);
				uploadBox = MessageBox.wait("Progress", "Reading file, please wait...", "Parsing...");
				fileUploadPanel.submit();
				submitButton.setEnabled(true);
			}
			
		});
		fileUploadPanel.addButton(previewButton);*/
		
		submitButton = new Button("Import");
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
				
					Object categoryId = importHeader.get("categoryId");
					
					ItemModel item = new ItemModel();
					item.setIdentifier((String)importHeader.get("id"));
					if (categoryId != null)
						item.setCategoryId(Long.valueOf((String)categoryId));
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
					
					boolean isUserNotFound = DataTypeConversionUtil.checkBoolean((Boolean)importRow.get("userNotFound"));
					
					if (isUserNotFound)
						continue;
					
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
		
		
		//advancedButton.setEnabled(false);
		//previewButton.setEnabled(false);
				
		fileUploadPanel.addListener(Events.Submit, new Listener<FormEvent>() {

			public void handleEvent(FormEvent fe) {
				
				submitButton.setEnabled(true);
				
				rowStore.removeAll();
				
				JSONValue jsonValue = JSONParser.parse(fe.resultHtml);
				JSONObject jsonObject = jsonValue.isObject().get("org.sakaiproject.gradebook.gwt.client.gxt.upload.ImportFile").isObject();
				
				JSONArray headersArray = getArray(jsonObject, "items");
				previewColumns = new ArrayList<ColumnConfig>();
				
				
				boolean hasCategories = gbModel.getGradebookItemModel().getCategoryType() != CategoryType.NO_CATEGORIES;
				boolean hasUnassignedItem = false;
				
				if (headersArray != null) {	
					headerMap.clear();
					for (int i=0;i<headersArray.size();i++) {
						JSONObject jsonHeaderObject = headersArray.get(i).isObject();
						String name = getString(jsonHeaderObject, "value");
						String id = getString(jsonHeaderObject, "id");
						String headerName = getString(jsonHeaderObject, "headerName");
						Double points = getDouble(jsonHeaderObject, "points");
						String field = getString(jsonHeaderObject, "field");
						String categoryName = getString(jsonHeaderObject, "categoryName");
						String categoryId = getString(jsonHeaderObject, "categoryId");
									
						int width = 200;
									
						StringBuilder nameBuilder = new StringBuilder();
						
						//if (!hasNoCategories && categoryName != null && headerName != null)
						//	nameBuilder.append(categoryName).append(" : ").append(headerName);
						//else
						nameBuilder.append(name);
						
						if (id == null)
							continue;
						
						if (id.equals("ID"))
							width = 100;
						else if (!id.equals("NAME"))
							width = name.length() * 7;

						if (id.startsWith("NEW:")) {
							nameBuilder.append("*");
						
							if (categoryId == null)
								hasUnassignedItem = true;
						}
						
						ColumnConfig column = new ColumnConfig(id, nameBuilder.toString(), width);
						previewColumns.add(column);
						
						
						ImportHeader header = new ImportHeader(Field.valueOf(field), headerName);
						header.setId(id);
						header.setHeaderName(headerName);
						header.setPoints(points);
						header.setField(field);
						header.setCategoryName(categoryName);
						header.setCategoryId(categoryId);
						
						if (header.getField() != null && header.getField().equals("ITEM"))
							headerMap.put(id, header);
					}
					ColumnModel cm = new ColumnModel(previewColumns);
					grid.reconfigure(rowStore, cm);
					
					//advancedButton.setEnabled(true);
					//grid.setVisible(true);
					//nextButton.setEnabled(true);
				}
				
				
				if (hasUnassignedItem && hasCategories) {
					showSetupPanel();
				}
				

				//boolean isGradeDataExcluded = excludeGradeData.getValue() != null && excludeGradeData.getValue().booleanValue();
				//if (! isGradeDataExcluded) {
					JSONArray rowsArray = getArray(jsonObject, "rows");
					List<BaseModel> models = new ArrayList<BaseModel>();
					if (rowsArray != null) {
						StringBuilder heading = new StringBuilder("Data (").append(rowsArray.size()).append(" records)");
						previewTab.setText(heading.toString());
						for (int i=0;i<rowsArray.size();i++) {
							JSONObject rowObject = rowsArray.get(i).isObject();
							String userUid = getString(rowObject.isObject(), "userUid");
							String userImportId = getString(rowObject.isObject(), "userImportId");
							String userDisplayName = getString(rowObject.isObject(), "userDisplayName");
							Boolean userNotFound = getBoolean(rowObject.isObject(), "isUserNotFound");
							JSONArray columnsArray = getArray(rowObject, "columns");
							
							BaseModel model = new BaseModel();
							model.set("userUid", userUid);
							model.set("userImportId", userImportId);
							model.set("userDisplayName", userDisplayName);
							model.set("userNotFound", userNotFound);
							
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
					
					subCardLayout.setActiveItem(tabPanel);
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
						
					advancedContainer = buildItemContainerX(headers);
					columnsTab.add(advancedContainer);
					
					//subCardLayoutContainer.add(columnsTab);
					
				}
				
			//}
			
		});
		
		return fileUploadPanel;
	}
	
	private LayoutContainer buildItemContainer(List<ImportHeader> headers) {
		LayoutContainer container = new LayoutContainer();
		
		ItemNumberCellRenderer numericCellRenderer = new ItemNumberCellRenderer(DataTypeConversionUtil.getShortNumberFormat());		

		List<TreeTableColumn> columns = new ArrayList<TreeTableColumn>();
		GradebookModel gbModel = Registry.get(AppConstants.CURRENT);
		
		ItemCellRenderer cellRenderer = new ItemCellRenderer();
		TreeTableColumn nameColumn = new TreeTableColumn(ItemModel.Key.NAME.name(), 
				ItemModel.getPropertyName(ItemModel.Key.NAME), 180);
		nameColumn.setRenderer(cellRenderer);
		nameColumn.setSortable(false);
		columns.add(nameColumn);
		
		TreeTableColumn percentCourseGradeColumn =  new TreeTableColumn(ItemModel.Key.PERCENT_COURSE_GRADE.name(), 
				ItemModel.getPropertyName(ItemModel.Key.PERCENT_COURSE_GRADE), ItemModel.getPropertyName(ItemModel.Key.PERCENT_COURSE_GRADE).length() * CHARACTER_WIDTH + 30);
		percentCourseGradeColumn.setAlignment(HorizontalAlignment.RIGHT);
		percentCourseGradeColumn.setHidden(gbModel.getGradebookItemModel().getCategoryType() == CategoryType.SIMPLE_CATEGORIES);
		percentCourseGradeColumn.setRenderer(numericCellRenderer);
		percentCourseGradeColumn.setSortable(false);
		columns.add(percentCourseGradeColumn);
		
		TreeTableColumn percentCategoryColumn =  new TreeTableColumn(ItemModel.Key.PERCENT_CATEGORY.name(), 
				ItemModel.getPropertyName(ItemModel.Key.PERCENT_CATEGORY), ItemModel.getPropertyName(ItemModel.Key.PERCENT_CATEGORY).length() * CHARACTER_WIDTH + 30);
		percentCategoryColumn.setAlignment(HorizontalAlignment.RIGHT);
		percentCategoryColumn.setHidden(gbModel.getGradebookItemModel().getCategoryType() == CategoryType.SIMPLE_CATEGORIES);
		percentCategoryColumn.setRenderer(numericCellRenderer);
		percentCategoryColumn.setSortable(false);
		columns.add(percentCategoryColumn);
		
		TreeTableColumn pointsColumn = new TreeTableColumn(ItemModel.Key.POINTS.name(), 
				ItemModel.getPropertyName(ItemModel.Key.POINTS), ItemModel.getPropertyName(ItemModel.Key.POINTS).length() * CHARACTER_WIDTH + 30);
		pointsColumn.setAlignment(HorizontalAlignment.RIGHT);
		pointsColumn.setHidden(gbModel.getGradebookItemModel().getCategoryType() != CategoryType.WEIGHTED_CATEGORIES);
		pointsColumn.setRenderer(numericCellRenderer);
		pointsColumn.setSortable(false);
		columns.add(pointsColumn);
		
		TreeTableColumnModel treeTableColumnModel = new TreeTableColumnModel(columns);
		TreeTable treeTable = new TreeTable(treeTableColumnModel) {
			@Override
			protected void onRender(Element target, int index) {
				super.onRender(target, index);
				Accessibility.setRole(el().dom, "treegrid");
				Accessibility.setState(el().dom, "aria-labelledby", "itemtreelabel");
				//treeTableBinder.setCheckedSelection(selectedItemModels);
				//treeTable.setHeight(483); //ItemTreePanel.this.getHeight(true));
				expandAll();
			}
		};
		
		container.add(treeTable);
		
		TreeLoader treeLoader = new BaseTreeLoader(new TreeModelReader() {
			
			@Override
			protected List<? extends ModelData> getChildren(ModelData parent) {
				List visibleChildren = new ArrayList();
				List<? extends ModelData> children = super.getChildren(parent);
				
				for (ModelData model : children) {
					//String source = model.get(ItemModel.Key.SOURCE.name());
					//if (source == null || !source.equals("Static"))
					visibleChildren.add(model);
				}
				
				return visibleChildren;
			}
		});
		
		TreeStore<ItemModel> treeStore = new TreeStore<ItemModel>(treeLoader);
		
		ItemTreeTableBinder treeTableBinder = new ItemTreeTableBinder(treeTable, treeStore);
		
		
		
		
		ItemModel gradebookItemModel = gbModel.getGradebookItemModel();
		ItemModel rootItemModel = new ItemModel();
		rootItemModel.setItemType(Type.ROOT);
		rootItemModel.setName("Root");
		gradebookItemModel.setParent(rootItemModel);
		rootItemModel.add(gradebookItemModel);
		
		
		treeLoader.load(rootItemModel);
		
		
		
		
		
		
		
		
		/*
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
		points.setEditor(new CellEditor(new NumberField()));
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
		itemGrid.setHeight(300);
		List<BeanModel> models = new ArrayList<BeanModel>();
		BeanModelFactory factory = BeanModelLookup.get().getFactory(headers.get(0).getClass());
        if (factory == null) {
          throw new RuntimeException("No BeanModelFactory found for " + headers.get(0).getClass());
        }
        List<BeanModel> converted = factory.createModel(headers);
        models.addAll(converted);
		
        itemStore.add(models);
        
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
		
		container.add(itemFieldSet, new RowData(1, 1));*/
		
		return container;
	}
	
	private LayoutContainer buildItemContainerX(List<ImportHeader> headers) {
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
		points.setEditor(new CellEditor(new NumberField()));
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

		ColumnConfig category = new ColumnConfig("categoryId", "Category", 140);
		category.setEditor(new CellEditor(categoryPicker) {
			
			@Override
			public Object postProcessValue(Object value) {
			    if (value != null) {
			    	ItemModel model = (ItemModel)value;
			    	return model.getIdentifier();
			    }
				return "None/Default";
			}

			@Override
			public Object preProcessValue(Object value) {
				String id = (String)value;
				
				ComboBox<ItemModel> combo = (ComboBox<ItemModel>)getField();
				/*List<ItemModel> models = categoriesStore.getModels();
				
				for (ItemModel model : models) {
					if (model.getName().equals(value))
						return model;
				}*/
				
				return categoriesStore.findModel(ItemModel.Key.ID.name(), id);
			}
			
		});
		
		category.setRenderer(new GridCellRenderer() {

			public String render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex, ListStore store) {
			
				String identifier = model.get(property);
				ItemModel itemModel = categoriesStore.findModel(ItemModel.Key.ID.name(), identifier);
				
				if (itemModel == null)
					return "Unassigned";
				
				return itemModel.getName();
			}
			
		});
		itemColumns.add(category);
		
		ColumnModel itemColumnModel = new ColumnModel(itemColumns);
		itemStore = new ListStore<BeanModel>();
		
		EditorGrid<BeanModel> itemGrid = new EditorGrid<BeanModel>(itemStore, itemColumnModel);
		itemGrid.setHeight(300);
		List<BeanModel> models = new ArrayList<BeanModel>();
		BeanModelFactory factory = BeanModelLookup.get().getFactory(headers.get(0).getClass());
        if (factory == null) {
          throw new RuntimeException("No BeanModelFactory found for " + headers.get(0).getClass());
        }
        List<BeanModel> converted = factory.createModel(headers);
        models.addAll(converted);
		
        itemStore.add(models);
        
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
	
	/*private FieldSet buildPreviewFieldSet() {
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
		grid.setBorders(true);
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
	}*/
	
	
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
	
	private Boolean getBoolean(JSONObject object, String property) {
		if (object == null)
			return Boolean.FALSE;
		
		JSONValue value = null;
		if (property != null) {
			value = object.get(property);
			if (value == null)
				return Boolean.FALSE;
		} else {
			value = object;
		}
		
		JSONBoolean bool = value.isBoolean();
		if (bool == null)
			return Boolean.FALSE;
		
		return Boolean.valueOf(bool.booleanValue());
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
	
	private void readFile() {
		
		if (file.getValue() != null && file.getValue().trim().length() > 0) {
			uploadBox = MessageBox.wait("Progress", "Reading file, please wait...", "Parsing...");
			fileUploadPanel.submit();
		}
	}
	
	
	private void showSetupPanel() {
		MessageBox.alert("Setup Required", "You have items that are not assigned to a category", new Listener<WindowEvent>() {

			public void handleEvent(WindowEvent be) {
				tabPanel.setSelection(columnsTab);
			}
			
		});
	}
	
	/*private ImportFile parseImport(String content) {
		return JSONParser.parse(content);
	}*/
	
}
