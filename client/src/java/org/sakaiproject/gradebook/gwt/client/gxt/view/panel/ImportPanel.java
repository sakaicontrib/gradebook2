package org.sakaiproject.gradebook.gwt.client.gxt.view.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.Gradebook2RPCServiceAsync;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.SecureToken;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.gxt.GridPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.ItemModelProcessor;
import org.sakaiproject.gradebook.gwt.client.gxt.custom.widget.grid.BaseCustomGridView;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.upload.ClientUploadUtility;
import org.sakaiproject.gradebook.gwt.client.gxt.upload.ImportHeader;
import org.sakaiproject.gradebook.gwt.client.gxt.upload.ImportHeader.Field;
import org.sakaiproject.gradebook.gwt.client.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.SpreadsheetModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.MemoryProxy;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CellSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ImportPanel extends ContentPanel {

	private FileUploadField file;
	
	private ListStore<StudentModel> rowStore;
	private ListStore<ItemModel> itemStore;
	private ListStore<BaseModel> resultStore;
	private ListStore<ItemModel> categoriesStore;
	private Grid<StudentModel> grid;
	private FormPanel fileUploadPanel;
	private Map<String, ImportHeader> headerMap;
	
	private LayoutContainer mainCardLayoutContainer; //, subCardLayoutContainer;
	private CardLayout mainCardLayout; //, subCardLayout;

	private TabPanel tabPanel;
	
	private TabItem previewTab, columnsTab;

	private Button submitButton, cancelButton; 
	
	private ContentPanel step1Container;
	private LayoutContainer fileUploadContainer;
	
	private ArrayList<ColumnConfig> previewColumns;
	
	private MemoryProxy<ListLoadResult<BaseModel>> proxy;
	private ListLoader<?> loader;
	private MessageBox uploadBox;
	

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
		
		/*fieldListener = new Listener<FieldEvent>() {

			public void handleEvent(FieldEvent be) {
				readFile();
			}
			
		};
		
		currentStep = Step.ONE;*/
		
		// Set up store
		resultStore = new ListStore<BaseModel>();
		
		categoriesStore = new ListStore<ItemModel>();
	}
	
	protected void onClose() {
		
	}
	
	private void refreshCategoryPickerStore(ItemModel gradebookItemModel) {
		categoriesStore.removeAll();
		if (gradebookItemModel != null) {
			
			ItemModelProcessor processor = new ItemModelProcessor(gradebookItemModel) {
				
				@Override
				public void doCategory(ItemModel categoryModel) {
					categoriesStore.add(categoryModel);
				}
				
			};
			
			processor.process();
		}
	}
	
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);

		GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
		
		if (selectedGradebook != null) 
			refreshCategoryPickerStore(selectedGradebook.getGradebookItemModel());
		
		
		int fileUploadHeight = 100;
		int subHeight = getHeight() - fileUploadHeight;
		
		mainCardLayout = new CardLayout();
		mainCardLayoutContainer = new LayoutContainer();
		mainCardLayoutContainer.setLayout(mainCardLayout);
		
		//subCardLayout = new CardLayout();
		//subCardLayoutContainer = new LayoutContainer();
		//subCardLayoutContainer.setLayout(subCardLayout);
		//subCardLayoutContainer.setHeight(400);
		
		tabPanel = new TabPanel();
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		rowStore = new ListStore<StudentModel>();
		rowStore.setMonitorChanges(true);
		rowStore.setModelComparer(new EntityModelComparer<StudentModel>());
		
		ColumnModel cm = new ColumnModel(configs);
		grid = new Grid<StudentModel>(rowStore, cm);
		grid.setLoadMask(false);
		grid.setHeight(300);
		
		CellSelectionModel<StudentModel> cellSelectionModel = new CellSelectionModel<StudentModel>();
		cellSelectionModel.setSelectionMode(SelectionMode.SINGLE);
		grid.setSelectionModel(cellSelectionModel);
		grid.setView(new BaseCustomGridView() {
			
			protected boolean isClickable(ModelData model, String property) {
				return property.equals(StudentModel.Key.DISPLAY_NAME.name()) ||
					property.equals(StudentModel.Key.LAST_NAME_FIRST.name()) ||
					property.equals(StudentModel.Key.DISPLAY_ID.name());
			}
			
			protected boolean isCommented(ModelData model, String property) {
				String commentedProperty = property + StudentModel.COMMENTED_FLAG;
				Boolean isCommented = model.get(commentedProperty);
				
				return isCommented != null && isCommented.booleanValue();
			}
			
			protected boolean isDropped(ModelData model, String property) {
				String droppedProperty = property + StudentModel.DROP_FLAG;
				Boolean isDropped = model.get(droppedProperty);
				
				return isDropped != null && isDropped.booleanValue();
			}
			
			protected boolean isReleased(ModelData model, String property) {
				return false;
			}
			
			@Override
			protected String markupCss(Record r, ModelData model, String property, boolean isShowDirtyCells, boolean isPropertyChanged) {
				
				boolean isUserNotFound = DataTypeConversionUtil.checkBoolean((Boolean)model.get("userNotFound"));
					
				if (isUserNotFound)
					return "gbCellDropped";
				
				StringBuilder css = new StringBuilder();
				
				if (isShowDirtyCells && isPropertyChanged) {
					
					Object startValue = r.getChanges().get(property);
					Object currentValue = r.get(property);
					
					String failedProperty = new StringBuilder().append(property).append(GridPanel.FAILED_FLAG).toString();
					String failedMessage = (String)r.get(failedProperty);
					
					if (failedMessage != null) {
						css.append(" gbCellFailed");
					} else {
						css.append(" gbCellSucceeded");
					}
				}

				if (isDropped(model, property)) {
					css.append(" gbCellDropped");
				}
				
				if (isReleased(model, property)) {
					css.append(" gbReleased");
				}
				
				if (css.length() > 0)
					return css.toString();
				
				return null;
			}
		});
		
		/*previewFieldSet = new FieldSet(); 
		previewFieldSet.setLayout(new FlowLayout());
		previewFieldSet.setHeading("Preview"); 
		previewFieldSet.setHeight(330);
		previewFieldSet.add(grid, new MarginData(5));*/
		
		boolean hasCategories = selectedGradebook.getGradebookItemModel().getCategoryType() != CategoryType.NO_CATEGORIES;
		
		previewTab = new TabItem("Data");
		previewTab.setLayout(new FlowLayout());
		previewTab.add(grid);
		
		tabPanel.add(previewTab);
		
		columnsTab = new TabItem("Setup");
		columnsTab.setLayout(new FlowLayout());
		columnsTab.add(buildItemGrid());
		
		tabPanel.add(columnsTab);
		
		tabPanel.setHeight(380);
		
		//subCardLayoutContainer.add(tabPanel);
		
		if (hasCategories) {
			
		} else {
			columnsTab.setVisible(false);
			/*previewFieldSet = new FieldSet(); 
			previewFieldSet.setLayout(new FlowLayout());
			previewFieldSet.setHeading("Data"); 
			previewFieldSet.setHeight(400);
			previewFieldSet.add(grid, new MarginData(5));
			
			subCardLayoutContainer.add(previewFieldSet);*/
		}
		
		//subCardLayoutContainer.setHeight(subHeight);
				
		step1Container = new ContentPanel();
		step1Container.setHeaderVisible(false);
		step1Container.setLayout(new FitLayout());
		step1Container.add(tabPanel);
		
		submitButton = new Button("Next");
		submitButton.setMinWidth(120);
		submitButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {		
				SpreadsheetModel spreadsheetModel = ClientUploadUtility.composeSpreadsheetModel(itemStore.getModels(), rowStore.getModels(), previewColumns);
				
				uploadSpreadsheet(spreadsheetModel);
				
				submitButton.setVisible(false);
			}
		});
		step1Container.addButton(submitButton);
		//submitButton.setEnabled(false);
		
		
		
		cancelButton = new Button("Cancel");
		cancelButton.setMinWidth(120);
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				Dispatcher.forwardEvent(GradebookEvents.StopImport.getEventType());
				fileUploadPanel.clear();
			}
		});
		
		step1Container.addButton(cancelButton);
		
		fileUploadContainer = new LayoutContainer();
		fileUploadContainer.setLayout(new FlowLayout());
		fileUploadContainer.add(buildFileUploadPanel());
		
		mainCardLayoutContainer.add(fileUploadContainer);
		mainCardLayoutContainer.add(step1Container);
		mainCardLayout.setActiveItem(fileUploadContainer);
		
		add(mainCardLayoutContainer); 
	}

	private void uploadSpreadsheet(SpreadsheetModel spreadsheetModel) {
		GradebookModel gbModel = Registry.get(AppConstants.CURRENT);
		
		int numberOfLearners = 0;
		List<StudentModel> learners = spreadsheetModel.getRows();
		if (learners != null)
			numberOfLearners = learners.size();
		
		String message = new StringBuilder().append(i18n.uploadingLearnerGradesPrefix()).append(" ")
			.append(numberOfLearners).append(" ").append(i18n.uploadingLearnerGradesSuffix()).toString();
		
		
		final MessageBox box = MessageBox.wait(i18n.uploadingLearnerGradesTitle(), message, i18n.uploadingLearnerGradesStatus());
		
		AsyncCallback<SpreadsheetModel> callback =
			new AsyncCallback<SpreadsheetModel>() {

				public void onFailure(Throwable caught) {
					Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(caught));
					box.close();
				}

				public void onSuccess(SpreadsheetModel result) {
					
					try {
						for (StudentModel student : result.getRows()) {
							
							boolean hasChanges = DataTypeConversionUtil.checkBoolean((Boolean)student.get(AppConstants.IMPORT_CHANGES));
							
							if (hasChanges) {
								Record record = rowStore.getRecord(student);
								record.beginEdit();
								
								for (String p : student.getPropertyNames()) {
									boolean needsRefreshing = false;
									
									int index = -1;
									
									if (p.endsWith(StudentModel.FAILED_FLAG)) {
										index = p.indexOf(StudentModel.FAILED_FLAG);
										needsRefreshing = true;
									} 
									
									if (needsRefreshing && index != -1) {
										String assignmentId = p.substring(0, index);
										Object value = result.get(assignmentId);
										
										record.set(assignmentId, null);
										record.set(assignmentId, value);
	
									}
								}
								record.endEdit();
							}
						}
		
						box.setProgressText("Loading");
						
						cancelButton.setText("Done");
						
						//advancedContainer.layout();
						
						Dispatcher.forwardEvent(GradebookEvents.RefreshCourseGrades.getEventType());
						
						GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
						selectedGradebook.setGradebookItemModel(result.getGradebookItemModel());
						Dispatcher.forwardEvent(GradebookEvents.LoadItemTreeModel.getEventType(), selectedGradebook);
					} catch (Exception e) {
						Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(e));
					} finally {
						box.close();
					}
					
					//fireEvent(GradebookEvents.UserChange.getEventType(), new UserChangeEvent(action));
					
				}
			
			
		};
		
		Gradebook2RPCServiceAsync service = Registry.get(AppConstants.SERVICE);
		
		service.create(gbModel.getGradebookUid(), gbModel.getGradebookId(), spreadsheetModel, EntityType.SPREADSHEET, SecureToken.get(), callback);
		
	}
	
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

		//fileUploadPanel.setWidth(1);
		fileUploadPanel.setLayout(formLayout);
		

		file = new FileUploadField() {
			@Override
			protected void onChange(ComponentEvent ce) {
				super.onChange(ce);
				//readFile();
			}
		};
		file.setAllowBlank(false);
		file.setFieldLabel("File");
		file.setName("Test");

		fileUploadPanel.add(file);
		
		HiddenField<String> gradebookUidField = new HiddenField<String>();
		gradebookUidField.setName("gradebookUid");
		gradebookUidField.setValue(gbModel.getGradebookUid());
		fileUploadPanel.add(gradebookUidField);
				
		Button submitButton = new Button("Next");
		submitButton.setMinWidth(120);
		submitButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				readFile();
			}
			
		});
		fileUploadPanel.addButton(submitButton);
		
		Button cancelButton = new Button("Cancel");
		cancelButton.setMinWidth(120);
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				Dispatcher.forwardEvent(GradebookEvents.StopImport.getEventType());
				fileUploadPanel.clear();
			}
		});
		
		fileUploadPanel.addButton(cancelButton);
				
		fileUploadPanel.addListener(Events.Submit, new Listener<FormEvent>() {

			public void handleEvent(FormEvent fe) {
				readSubmitResponse(fe.resultHtml);
			}
							
		});
		
		return fileUploadPanel;
	}
	
	private void readSubmitResponse(String result) {

		try {
			
			rowStore.removeAll();
			
			JSONValue jsonValue = JSONParser.parse(result);
			
			if (jsonValue == null)
				throw new Exception("Server response incorrect. Unable to parse result.");
			
			JSONObject jsonWrapper = jsonValue.isObject();
			
			if (jsonWrapper == null)
				throw new Exception("Server response incorrect. Unable to read data.");
			
			JSONObject jsonObject = jsonWrapper.get("org.sakaiproject.gradebook.gwt.client.gxt.upload.ImportFile").isObject();
			
			JSONArray headersArray = getArray(jsonObject, "items");
			previewColumns = new ArrayList<ColumnConfig>();
			
			Boolean hasCategoriesBoolean = getBoolean(jsonObject, "hasCategories");
			
			boolean hasCategories = DataTypeConversionUtil.checkBoolean(hasCategoriesBoolean);
			
			if (hasCategories) {
				columnsTab.setVisible(true);
			
				final GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
				
				if (selectedGradebook != null) {
					
					if (selectedGradebook.getGradebookItemModel().getCategoryType() == CategoryType.NO_CATEGORIES) {
						
						Gradebook2RPCServiceAsync service = Registry.get(AppConstants.SERVICE);
				
						AsyncCallback<GradebookModel> callback = new AsyncCallback<GradebookModel>() {
	
							public void onFailure(Throwable caught) {
								Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(caught));
							}
	
							public void onSuccess(GradebookModel result) {
								
								if (result.getGradebookItemModel() != null) {
									refreshCategoryPickerStore(result.getGradebookItemModel());
									
									selectedGradebook.setGradebookItemModel(result.getGradebookItemModel());
									Dispatcher.forwardEvent(GradebookEvents.LoadItemTreeModel.getEventType(), selectedGradebook);
								}
							}
						};
						
						service.get(selectedGradebook.getGradebookUid(), selectedGradebook.getGradebookId(), EntityType.GRADEBOOK, null, null, SecureToken.get(), callback);
						
						refreshCategoryPickerStore(selectedGradebook.getGradebookItemModel());
					}
					
				}
			}
			
			boolean hasUnassignedItem = false;
			
			if (headersArray != null) {	
				headerMap.clear();
				for (int i=0;i<headersArray.size();i++) {
					JSONValue value = headersArray.get(i);
					if (value == null) 
						continue;
					
					JSONObject jsonHeaderObject = value.isObject();
					
					if (jsonHeaderObject == null)
						continue;
					
					String name = getString(jsonHeaderObject, "value");
					String id = getString(jsonHeaderObject, "id");
					String headerName = getString(jsonHeaderObject, "headerName");
					Double points = getDouble(jsonHeaderObject, "points");
					String field = getString(jsonHeaderObject, "field");
					String categoryName = getString(jsonHeaderObject, "categoryName");
					String categoryId = getString(jsonHeaderObject, "categoryId");
					Double percentCategory = getDouble(jsonHeaderObject, "percentCategory");
					Boolean isExtraCredit = getBoolean(jsonHeaderObject, "extraCredit");
					Boolean isUnincluded = getBoolean(jsonHeaderObject, "unincluded");
					
					int width = 200;
								
					StringBuilder nameBuilder = new StringBuilder();
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
					header.setPercentCategory(percentCategory);
					header.setExtraCredit(isExtraCredit);
					header.setUnincluded(isUnincluded);
					
					if (header.getField() != null && header.getField().equals("ITEM"))
						headerMap.put(id, header);
				}
				ColumnModel cm = new ColumnModel(previewColumns);
				grid.reconfigure(rowStore, cm);
			}
			
			
			mainCardLayout.setActiveItem(step1Container);
			
			if (hasUnassignedItem && hasCategories) {
				showSetupPanel();
			}
			
			
	
			JSONArray rowsArray = getArray(jsonObject, "rows");
			ArrayList<StudentModel> models = new ArrayList<StudentModel>();
			if (rowsArray != null) {
				StringBuilder heading = new StringBuilder().append("Data (").append(rowsArray.size()).append(" records)");
				previewTab.setText(heading.toString());
					
				for (int i=0;i<rowsArray.size();i++) {
					JSONValue value = rowsArray.get(i);
					if (value == null)
						continue;
					
					JSONObject rowObject = value.isObject();
					String userUid = getString(rowObject.isObject(), "userUid");
					String userImportId = getString(rowObject.isObject(), "userImportId");
					String userDisplayName = getString(rowObject.isObject(), "userDisplayName");
					Boolean userNotFound = getBoolean(rowObject.isObject(), "isUserNotFound");
					JSONArray columnsArray = getArray(rowObject, "columns");
						
					StudentModel model = new StudentModel();
					if (userUid != null)
						model.setIdentifier(userUid);
					else if (userImportId != null)
						model.setIdentifier(userImportId);
					
					model.set("userUid", userUid);
					model.set("userImportId", userImportId);
					model.set("userDisplayName", userDisplayName);
					model.set("userNotFound", userNotFound);
					
					if (columnsArray != null) {
						for (int j=0;j<columnsArray.size();j++) {
							if (previewColumns != null && previewColumns.size() > j) {
								ColumnConfig config = previewColumns.get(j);
								if (config != null) {
									JSONValue itemValue = columnsArray.get(j);
									if (itemValue == null)
										continue;
									JSONString itemString = itemValue.isString();
									if (itemString == null)
										continue;
									model.set(config.getId(), itemString.stringValue());
								}
							}
						}
							
					}
						
					models.add(model);
				}
			}
				
			if (models != null)
				rowStore.add(models);
				
				
			ColumnModel cm = grid.getColumnModel();
			ArrayList<ImportHeader> headers = new ArrayList<ImportHeader>();
					
			// First, we need to ensure that all of the assignments exist
			for (int i=0;i<cm.getColumnCount();i++) {
				ColumnConfig config = cm.getColumn(i);
				
				if (config == null)
					continue;
				
				String id = config.getId();
							
				ImportHeader header = headerMap.get(id);
						
				if (header != null) {
					headers.add(header);
				}					
			}
				
			ArrayList<ItemModel> itemModels = ClientUploadUtility.convertHeadersToItemModels(headers);
	        itemStore.add(itemModels);
		        
		} catch (Exception e) {
			Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(e));
		} finally {
			uploadBox.close();
		}
			        
	}
		
	private EditorGrid<ItemModel> buildItemGrid() {
		ContentPanel panel = new ContentPanel();
		panel.setLayout(new FitLayout());
		panel.setHeaderVisible(false);
		
		ArrayList<ColumnConfig> itemColumns = new ArrayList<ColumnConfig>();
		
		TextField<String> textField = new TextField<String>();
		textField.addInputStyleName("gbTextFieldInput");
		CellEditor textCellEditor = new CellEditor(textField);
		
		ColumnConfig name = new ColumnConfig(ItemModel.Key.NAME.name(), "Item", 200);
		name.setEditor(textCellEditor);
		itemColumns.add(name);
		
		ColumnConfig percentCategory = new ColumnConfig(ItemModel.Key.PERCENT_CATEGORY.name(), "% Category", 100);
		percentCategory.setEditor(new CellEditor(new NumberField()));
		itemColumns.add(percentCategory);
		
		ColumnConfig points = new ColumnConfig(ItemModel.Key.POINTS.name(), "Points", 100);
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
		categoryPicker.setValueField(ItemModel.Key.ID.name());
		categoryPicker.addInputStyleName("gbTextFieldInput");

		ColumnConfig category = new ColumnConfig(ItemModel.Key.CATEGORY_ID.name(), "Category", 140);
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
				Long id = (Long)value;
				
				ComboBox<ItemModel> combo = (ComboBox<ItemModel>)getField();
				/*List<ItemModel> models = categoriesStore.getModels();
				
				for (ItemModel model : models) {
					if (model.getName().equals(value))
						return model;
				}*/
				
				return categoriesStore.findModel(ItemModel.Key.ID.name(), String.valueOf(id));
			}
			
		});
		
		category.setRenderer(new GridCellRenderer() {

			public String render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex, ListStore store) {
			
				Object identifier = model.get(property);
				
				String lookupId = null;
				
				if (identifier instanceof Long) 
					lookupId = String.valueOf(identifier);
				else
					lookupId = (String)identifier;
				
				ItemModel itemModel = categoriesStore.findModel(ItemModel.Key.ID.name(), lookupId);
				
				if (itemModel == null)
					return "Unassigned";
				
				return itemModel.getName();
			}
			
		});
		itemColumns.add(category);
		
		ColumnModel itemColumnModel = new ColumnModel(itemColumns);
		itemStore = new ListStore<ItemModel>();
		
		EditorGrid<ItemModel> itemGrid = new EditorGrid<ItemModel>(itemStore, itemColumnModel);
		itemGrid.setHeight(300);
		itemGrid.setBorders(true);
		itemGrid.setView(new BaseCustomGridView());
        
		//panel.add(itemGrid);
		
		
		LayoutContainer container = new LayoutContainer();
		container.setLayout(new FitLayout());
		
		/*ContentPanel directionsPanel = new ContentPanel();
		directionsPanel.setFrame(true);
		directionsPanel.setWidth(1);
		directionsPanel.setHeaderVisible(false);
		
		String text = "Listed below are the items that will be imported, according to the column headers "
			+ "you provided in your file. Note that items that do not yet exist in the current gradebook, "
			+ "or that exist in a different category from the one selected, will be created on import. ";
		
		directionsPanel.add(new LabelField(text));
		
		container.add(directionsPanel, new RowData(1, -1));
		*/
		/*
		FitLayout fitLayout = new FitLayout();
		FieldSet itemFieldSet = new FieldSet(); 
		itemFieldSet.setLayout(fitLayout);
		itemFieldSet.setHeading("Items"); 
		itemFieldSet.setHeight(300);  
		itemFieldSet.add(panel, new MarginData(5));*/
		
		//container.add(itemGrid);
		
		return itemGrid;
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
		
		ArrayList<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		
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
