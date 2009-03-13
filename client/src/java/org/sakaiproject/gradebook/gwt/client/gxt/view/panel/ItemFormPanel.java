package org.sakaiproject.gradebook.gwt.client.gxt.view.panel;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ConfirmationEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.event.FullScreen;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ItemCreate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ItemUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.view.AppView;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.binding.Converter;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PropertyChangeEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.MarginData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.treetable.TreeTable;

public class ItemFormPanel extends ContentPanel {

	private static int CHARACTER_WIDTH = 7;
	
	private FormPanel formPanel;
	private FormBinding formBindings;
	
	private TextField<String> nameField;
	private ComboBox<ItemModel> categoryList;
	private CheckBox includedField;
	private CheckBox extraCreditField;
	private CheckBox equallyWeightChildrenField;
	private CheckBox releasedField;
	private NumberField percentCourseGradeField;
	private NumberField percentCategoryField;
	private NumberField pointsField;
	private DateField dueDateField;
	private TextField<String> sourceField;
	
	private TreeTable treeTable;
	//private TreeTableBinder<ItemModel> treeBinder;
	//private TreeLoader<ItemModel> treeLoader;
	private TreeStore<ItemModel> treeStore;
	private SelectionChangedListener<SimpleComboValue<String>> comboSelectionChangedListener;
	private SelectionChangedListener<ItemModel> selectionChangedListener;
	
	private I18nConstants i18n;
	
	private RowLayout layout;
	private RowData topRowData, bottomRowData;
	private Button createButton;
	private boolean isFull;
	
	@SuppressWarnings("unchecked")
	public ItemFormPanel(I18nConstants i18n) {
		this.i18n = i18n;
		this.isFull = false;
		setHeaderVisible(false);
		setFrame(true);
		
		layout = new RowLayout();
		setLayout(layout);
		layout.setOrientation(Orientation.VERTICAL);
		
		initListeners();
		/*
		treeLoader = new BaseTreeLoader(new TreeModelReader() {
			
			@Override
			protected List<? extends ModelData> getChildren(ModelData parent) {
				List visibleChildren = new ArrayList();
				List<? extends ModelData> children = super.getChildren(parent);
				
				for (ModelData model : children) {
					String source = model.get(ItemModel.Key.SOURCE.name());
					if (source == null || !source.equals("Static"))
						visibleChildren.add(model);
				}
				
				return visibleChildren;
			}
		});
		
		treeStore = new TreeStore<ItemModel>(treeLoader);

		treeStore.setModelComparer(new ItemModelComparer());
		*/
		
		formPanel = new FormPanel();
		formPanel.setHeaderVisible(false);
		formPanel.setLabelWidth(120);
		//setWidth(400);

		nameField = new TextField<String>();
		nameField.setName(ItemModel.Key.NAME.name());
		nameField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.NAME));
		
		formPanel.add(nameField);
	
		categoryList = new ComboBox<ItemModel>();
		categoryList.setDisplayField(ItemModel.Key.NAME.name());
		categoryList.setName(ItemModel.Key.CATEGORY_ID.name());
		categoryList.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.CATEGORY_NAME));
		formPanel.add(categoryList);
		
		//addGradebookFormItems(formPanel, i18n);
		
		percentCourseGradeField = new NumberField();
		percentCourseGradeField.setName(ItemModel.Key.PERCENT_COURSE_GRADE.name());
		percentCourseGradeField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.PERCENT_COURSE_GRADE));
		percentCourseGradeField.setFormat(DataTypeConversionUtil.getLongNumberFormat());
		percentCourseGradeField.setAllowDecimals(true);
		percentCourseGradeField.setMaxValue(Double.valueOf(100.000000d));
		formPanel.add(percentCourseGradeField);
		
		percentCategoryField = new NumberField();
		percentCategoryField.setName(ItemModel.Key.PERCENT_CATEGORY.name());
		percentCategoryField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.PERCENT_CATEGORY));
		percentCategoryField.setFormat(DataTypeConversionUtil.getLongNumberFormat());
		percentCategoryField.setAllowDecimals(true);
		percentCategoryField.setMaxValue(Double.valueOf(100.000000d));
		formPanel.add(percentCategoryField);
			
		pointsField = new NumberField();
		pointsField.setName(ItemModel.Key.POINTS.name());
		pointsField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.POINTS));
		pointsField.setFormat(DataTypeConversionUtil.getDefaultNumberFormat());
		pointsField.setAllowDecimals(true);
		formPanel.add(pointsField);
		
		dueDateField = new DateField();
		dueDateField.setName(ItemModel.Key.DUE_DATE.name());
		dueDateField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.DUE_DATE));
		formPanel.add(dueDateField);
		
		sourceField = new TextField<String>();
		sourceField.setName(ItemModel.Key.SOURCE.name());
		sourceField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.SOURCE));
		sourceField.setEnabled(false);
		sourceField.setEmptyText("Gradebook");
		formPanel.add(sourceField);
		
		LayoutContainer checkBoxContainer = new LayoutContainer();
		ColumnLayout columnLayout = new ColumnLayout();
		checkBoxContainer.setLayout(columnLayout);
		
		LayoutContainer left = new LayoutContainer();
		LayoutContainer right = new LayoutContainer();
		
		setLayoutData(left, new MarginData(0));
		setLayoutData(right, new MarginData(0));
		
		FormLayout leftFormLayout = new FormLayout();
		leftFormLayout.setPadding(0);
		leftFormLayout.setLabelWidth(120);
		
		left.setLayout(leftFormLayout);
		
		FormLayout rightFormLayout = new FormLayout();
		rightFormLayout.setPadding(0);
		rightFormLayout.setLabelWidth(120);
		
		right.setLayout(rightFormLayout);
		
		includedField = new CheckBox();
		includedField.setName(ItemModel.Key.INCLUDED.name());
		includedField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.INCLUDED));
		left.add(includedField);
		
		extraCreditField = new CheckBox();
		extraCreditField.setName(ItemModel.Key.EXTRA_CREDIT.name());
		extraCreditField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.EXTRA_CREDIT));
		left.add(extraCreditField);
		
		equallyWeightChildrenField = new CheckBox();
		equallyWeightChildrenField.setName(ItemModel.Key.EQUAL_WEIGHT.name());
		equallyWeightChildrenField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.EQUAL_WEIGHT));
		right.add(equallyWeightChildrenField);
		
		releasedField = new CheckBox();
		releasedField.setName(ItemModel.Key.RELEASED.name());
		releasedField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.RELEASED));
		right.add(releasedField);
		
		checkBoxContainer.add(left, new ColumnData(200));
		checkBoxContainer.add(right, new ColumnData(200));
		
		formPanel.add(checkBoxContainer);
	/*
		List<TreeTableColumn> columns = new ArrayList<TreeTableColumn>();
		
		CellRenderer<TreeItem> cellRenderer = new CellRenderer<TreeItem>() {

			public String render(TreeItem item, String property, Object value) {
				String prefix = "";
				String result = null;
				ItemModel itemModel = (ItemModel)item.getModel();
				
				boolean isName = property.equals(ItemModel.Key.NAME.name());
				boolean isIncluded = itemModel.getIncluded() == null || itemModel.getIncluded().booleanValue();		
				boolean isExtraCredit = itemModel.getExtraCredit() != null && itemModel.getExtraCredit().booleanValue();
				boolean isReleased = itemModel.getReleased() != null && itemModel.getReleased().booleanValue();
				
				if (value == null)
					return null;
				
				result = (String)value;
				
				StringBuilder cssClasses = new StringBuilder();
				
				if (!isIncluded && isName)
					cssClasses.append("gbNotIncluded");
				
				if (isExtraCredit) 
					cssClasses.append(" gbCellExtraCredit");
				
				if (isReleased)
					cssClasses.append(" gbReleased");
				
				return new StringBuilder().append("<span class=\"").append(cssClasses)
					.append("\">").append(prefix).append(result).append("</span>").toString();
			}
			
		};
		
		TreeTableColumn nameColumn = new TreeTableColumn(ItemModel.Key.NAME.name(), 
				ItemModel.getPropertyName(ItemModel.Key.NAME), 180);
		nameColumn.setRenderer(cellRenderer);
		nameColumn.setSortable(false);
		columns.add(nameColumn);
		
		NumberCellRenderer<TreeItem> numericCellRenderer = new NumberCellRenderer<TreeItem>(DataTypeConversionUtil.getShortNumberFormat()) {
			
			@Override
			public String render(final TreeItem item, String property, final Object value) {
				String prefix = "";
				String result = null;
				final ItemModel itemModel = (ItemModel)item.getModel();
				
				if (itemModel != null && itemModel.getItemType() != null) {
					boolean isItem = itemModel.getItemType().equalsIgnoreCase(Type.ITEM.getName());
					boolean isCategory = itemModel.getItemType().equalsIgnoreCase(Type.CATEGORY.getName());
					boolean isGradebook = !isItem && !isCategory;
					boolean isPercentCategory = property.equals(ItemModel.Key.PERCENT_CATEGORY.name());
					boolean isPercentGrade = property.equals(ItemModel.Key.PERCENT_COURSE_GRADE.name());
					
					if (isGradebook && isPercentCategory)
						return "-";
						
					if (value == null)
						return null;
					
					boolean isName = property.equals(ItemModel.Key.NAME.name());
					
					boolean isIncluded = itemModel.getIncluded() != null && itemModel.getIncluded().booleanValue();				
					boolean isTooBig = (isPercentCategory || isPercentGrade) 
						&& ((Double)value).doubleValue() > 100.00001d;
					boolean isTooSmall = ((isPercentCategory && isCategory) || (isPercentGrade && isGradebook)) && ((Double)value).doubleValue() < 99.9994d;
					
					result = super.render(item, property, value);
					
					StringBuilder cssClasses = new StringBuilder();
					
					if (!isIncluded && isName)
						cssClasses.append("gbNotIncluded");
					
					if (!isItem) 
						cssClasses.append(" gbCellStrong");
					
					if (isTooBig || isTooSmall)
						cssClasses.append(" gbCellError");
						
					boolean isExtraCredit = itemModel.getExtraCredit() != null && itemModel.getExtraCredit().booleanValue();
					if (isExtraCredit) {
						
						if (isPercentGrade || (isPercentCategory && isItem)) {
							cssClasses.append(" gbCellExtraCredit");
							prefix = "+";
						}

					}
					
					StringBuilder builder = new StringBuilder().append("<span class=\"").append(cssClasses)
						.append("\">").append(prefix).append(result).append("</span>");
					
					if ((isCategory && isPercentCategory) || (isGradebook && isPercentGrade)) {
						builder.append(" / 100");
					} 
 					
					
					return builder.toString();
				
				}
				return "";
			}
		};
		
		GradebookModel gbModel = Registry.get(AppConstants.CURRENT);
		
		TreeTableColumn percentCourseGradeColumn =  new TreeTableColumn(ItemModel.Key.PERCENT_COURSE_GRADE.name(), 
				ItemModel.getPropertyName(ItemModel.Key.PERCENT_COURSE_GRADE), ItemModel.getPropertyName(ItemModel.Key.PERCENT_COURSE_GRADE).length() * CHARACTER_WIDTH + 30);
		percentCourseGradeColumn.setAlignment(HorizontalAlignment.RIGHT);
		percentCourseGradeColumn.setHidden(gbModel.getCategoryType() == CategoryType.SIMPLE_CATEGORIES);
		percentCourseGradeColumn.setRenderer(numericCellRenderer);
		percentCourseGradeColumn.setSortable(false);
		columns.add(percentCourseGradeColumn);
		
		TreeTableColumn percentCategoryColumn =  new TreeTableColumn(ItemModel.Key.PERCENT_CATEGORY.name(), 
				ItemModel.getPropertyName(ItemModel.Key.PERCENT_CATEGORY), ItemModel.getPropertyName(ItemModel.Key.PERCENT_CATEGORY).length() * CHARACTER_WIDTH + 30);
		percentCategoryColumn.setAlignment(HorizontalAlignment.RIGHT);
		percentCategoryColumn.setHidden(gbModel.getCategoryType() == CategoryType.SIMPLE_CATEGORIES);
		percentCategoryColumn.setRenderer(numericCellRenderer);
		percentCategoryColumn.setSortable(false);
		columns.add(percentCategoryColumn);
		
		TreeTableColumnModel treeTableColumnModel = new TreeTableColumnModel(columns);
		treeTable = new TreeTable(treeTableColumnModel) {
			@Override
			protected void onRender(Element target, int index) {
				super.onRender(target, index);
				Accessibility.setRole(el().dom, "treegrid");
				Accessibility.setState(el().dom, "aria-labelledby", "itemtreelabel");
			}
		};
		treeTable.setAnimate(false);
		treeTable.getStyle().setLeafIconStyle("gbEditItemIcon");
		//treeTable.expandAll();
		treeTable.setHeight(300);
		//treeTable.addListener(Events.RowClick, treeTableEventListener);
		treeTable.setSelectionModel(new TreeSelectionModel(SelectionMode.SINGLE));
		
		treeBinder = new TreeTableBinder<ItemModel>(treeTable, treeStore);
		treeBinder.setDisplayProperty(ItemModel.Key.NAME.name());
		treeBinder.addSelectionChangedListener(selectionChangedListener);
	
		fullScreenButton = new Button("Full Screen", new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				doFullScreen();
			}
			
		});
		
		addButton(fullScreenButton);
		
		*/
		
		createButton = new Button("Create", new SelectionListener<ButtonEvent>() {
			@Override  
			public void componentSelected(ButtonEvent be) { 
				ItemModel item = new ItemModel();
				
				ItemModel category = categoryList.getValue();
				
				if (category != null) 
					item.setCategoryId(category.getCategoryId());
				
				item.setName(nameField.getValue());
				item.setExtraCredit(extraCreditField.getValue());
				//item.setEqualWeightAssignments(equallyWeightChildrenField.getValue());
				item.setIncluded(includedField.getValue());
				item.setReleased(releasedField.getValue());
				item.setPercentCategory((Double)percentCategoryField.getValue());
				item.setPoints((Double)pointsField.getValue());
				item.setDueDate(dueDateField.getValue());
							
				Dispatcher.forwardEvent(GradebookEvents.CreateItem, new ItemCreate(treeStore, item));
			}
		});
		createButton.setVisible(false);
		
		addButton(createButton);
		
		
		addButton(new Button("Close", new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) { 
				/*if (isFull) {
					doFullScreen();
				}*/
				Dispatcher.forwardEvent(GradebookEvents.HideEastPanel, Boolean.FALSE);
			}
			
		}));
		topRowData = new RowData(1, 1, new Margins(0, 0, 5, 0));
		bottomRowData = new RowData(1, .5);
		add(formPanel, topRowData);
		//add(treeTable, bottomRowData);
	}
	
	
	/*@Override
	protected void onRender(Element parent, int pos) {
	    super.onRender(parent, pos);
	
	    if (selectionForRender != null)
	    	typePickerComboBox.setSelection(selectionForRender);
	    
	}*/
	
	public void doFullScreen() {
		/*if (isFull) {
			fullScreenButton.setText("Full Screen");
			layout.setOrientation(Orientation.VERTICAL);
			topRowData.setWidth(1);
			topRowData.setHeight(.5);
			bottomRowData.setWidth(1);
			bottomRowData.setHeight(.5);
			removeAll();
			add(formPanel, topRowData);
			add(treeTable, bottomRowData);
			layout();
		} else {
			fullScreenButton.setText("Reduce");
			topRowData.setWidth(.5);
			topRowData.setHeight(1);
			bottomRowData.setWidth(.5);
			bottomRowData.setHeight(1);
			removeAll();
			add(treeTable, bottomRowData);
			add(formPanel, topRowData);
			layout.setOrientation(Orientation.HORIZONTAL);
			layout();
		}*/
		isFull = !isFull;
		Dispatcher.forwardEvent(GradebookEvents.FullScreen, new FullScreen(AppView.EastCard.EDIT_ITEM, isFull));
	}
	
	public void onEditItem(ItemModel itemModel) {
		
		if (formBindings == null)
			initFormBindings();
		
		createButton.setVisible(false);
		
		if (itemModel != null) {
			String itemType = itemModel.get(ItemModel.Key.ITEM_TYPE.name());
			String source = itemModel.get(ItemModel.Key.SOURCE.name());
			boolean isNotGradebook = !itemType.equalsIgnoreCase(Type.GRADEBOOK.getName());
			boolean isCategory = itemType.equalsIgnoreCase(Type.CATEGORY.getName());
			boolean isItem = itemType.equalsIgnoreCase(Type.ITEM.getName());
			boolean isExternal = source != null && source.trim().length() > 0;
			
			pointsField.setEnabled(!isExternal);
			percentCategoryField.setEnabled(true);
			percentCourseGradeField.setEnabled(true);
			equallyWeightChildrenField.setEnabled(true);
			extraCreditField.setEnabled(true);
			dueDateField.setEnabled(!isExternal);
			includedField.setEnabled(true);
			releasedField.setEnabled(true);
			categoryList.setEnabled(true);
			
			categoryList.setVisible(isItem);
			extraCreditField.setVisible(isNotGradebook);
			equallyWeightChildrenField.setVisible(isCategory);
			includedField.setVisible(isNotGradebook);
			releasedField.setVisible(isItem);
			//percentCategoryField.setEnabled(isItem);
			percentCategoryField.setVisible(isItem);
			percentCourseGradeField.setVisible(isCategory);
			pointsField.setVisible(isItem);
			dueDateField.setVisible(isItem);
			sourceField.setVisible(isItem);
			//typePickerComboBox.setVisible(!isNotGradebook);
			
			/*TreeItem treeItem = (TreeItem)treeBinder.findItem(itemModel);
			TreeItem selectedItem = treeTable.getSelectedItem();
			if (treeItem != null && (selectedItem == null || !treeItem.equals(selectedItem))) {
				treeBinder.removeSelectionListener(selectionChangedListener);
				treeTable.setSelectedItem(treeItem);
				treeBinder.addSelectionChangedListener(selectionChangedListener);
			}*/
			
			//formBindings.unbind();
			formBindings.bind(itemModel);
			
			Dispatcher.forwardEvent(GradebookEvents.ExpandEastPanel, AppView.EastCard.EDIT_ITEM);
		} else {
			formBindings.unbind();
		}
		
		/*treeStore.removeAll();
		ItemModel rootItemModel = new ItemModel();
		rootItemModel.setItemType(Type.ROOT.getName());
		rootItemModel.setName("Root");
		itemModel.setParent(rootItemModel);
		rootItemModel.add(itemModel);
		treeLoader.load(rootItemModel);*/
	}

	public void onItemUpdated(ItemModel itemModel) {
		//Record record = treeStore.getRecord(itemModel);
		
		//record.set(ItemModel.Key.PERCENT_CATEGORY.name(), itemModel.get(ItemModel.Key.PERCENT_CATEGORY.name()));
		//treeLoader.load(itemModel);
		
		//Info.display("Item Updated", "Percent category is " + itemModel.get(ItemModel.Key.PERCENT_CATEGORY.name()));
		//treeStore.update(itemModel);
		
		categoryList.setEnabled(true);
		//percentCategoryField.setEnabled(true);
		//percentCourseGradeField.setEnabled(true);
	}
	
	public void onLoadItemTreeModel(ItemModel rootItemModel) {
		
		// FIXME: Do we need to eliminate old category stores?  
		ListStore<ItemModel> categoryStore = new ListStore<ItemModel>();
		for (ItemModel gradebook : rootItemModel.getChildren()) {
			for (ItemModel category : gradebook.getChildren()) {
			
				// Ensure that we're dealing with a category
				if (category.getItemType().equalsIgnoreCase(Type.CATEGORY.getName())) {
					categoryStore.add(category);
				}
			
			}
		}
		categoryList.setStore(categoryStore);
		
		/*treeStore.removeAll();
		treeLoader.load(rootItemModel);
		if (treeTable != null)
			treeTable.expandAll();
		
		if (formBindings != null) {
			formBindings.unbind();
			formBindings.clear();
			formBindings = null;
		}
		
		if (! rendered) {
			return;
		}
		
		initFormBindings();
		*/
		
//		Info.display("Loading", "Loading tree model");
	}
	
	public void onNewItem(ItemModel itemModel) {
		
		if (formBindings != null) {
			formBindings.unbind();
		}

		createButton.setVisible(true);
		
		if (itemModel != null) {
			String itemType = itemModel.get(ItemModel.Key.ITEM_TYPE.name());
			String source = itemModel.get(ItemModel.Key.SOURCE.name());
			boolean isNotGradebook = !itemType.equalsIgnoreCase(Type.GRADEBOOK.getName());
			boolean isCategory = itemType.equalsIgnoreCase(Type.CATEGORY.getName());
			boolean isItem = itemType.equalsIgnoreCase(Type.ITEM.getName());
			boolean isExternal = source != null && source.trim().length() > 0;
			
			if (itemModel.getCategoryId() != null) {
				ItemModel category = treeStore.findModel(ItemModel.Key.ID.name(), String.valueOf(itemModel.getCategoryId()));
				categoryList.setValue(category);
			}
			
			pointsField.setEnabled(!isExternal);
			percentCategoryField.setEnabled(true);
			percentCourseGradeField.setEnabled(true);
			equallyWeightChildrenField.setEnabled(true);
			extraCreditField.setEnabled(true);
			dueDateField.setEnabled(!isExternal);
			includedField.setEnabled(true);
			releasedField.setEnabled(true);
			categoryList.setEnabled(true);
			
			categoryList.setVisible(isItem);
			extraCreditField.setVisible(isNotGradebook);
			equallyWeightChildrenField.setVisible(isCategory);
			includedField.setVisible(isNotGradebook);
			releasedField.setVisible(isItem);
			//percentCategoryField.setEnabled(isItem);
			percentCategoryField.setVisible(isItem);
			percentCourseGradeField.setVisible(isCategory);
			pointsField.setVisible(isItem);
			dueDateField.setVisible(isItem);
			sourceField.setVisible(isItem);
			
		}
		
	}
	
	public void onTreeStoreInitialized(TreeStore<ItemModel> treeStore) {
		this.treeStore = treeStore;
		
		if (formBindings != null) {
			formBindings.unbind();
			formBindings.clear();
			formBindings = null;
		}
		
		if (! rendered) {
			return;
		}
		
		initFormBindings();
	}
	
	public void onSwitchGradebook(GradebookModel selectedGradebook) {
		/*List<ModelData> selection = new ArrayList<ModelData>();
		ModelData model = getCategoryTypeModel(selectedGradebook.getCategoryType());
		if (model != null) {
			selection.add(model);
			if (typePickerComboBox != null && typePickerComboBox.isRendered()) 
				typePickerComboBox.setSelection(selection);
			else
				selectionForRender = selection;
		}*/
	}
	
	//private ListField<ModelData> typePickerComboBox;
	//private List<ModelData> selectionForRender;
	
	private ModelData getCategoryTypeModel(CategoryType categoryType) {
		ModelData model = new BaseModelData();
		
		// Initialize type picker
		switch (categoryType) {
	    case NO_CATEGORIES:
	    	model.set("name", i18n.orgTypeNoCategories());
	    	model.set("value", GradebookModel.CategoryType.NO_CATEGORIES);
	    	break;
	    case SIMPLE_CATEGORIES:
	    	model.set("name", i18n.orgTypeCategories());
	    	model.set("value", GradebookModel.CategoryType.SIMPLE_CATEGORIES);
	    	break;	
	    case WEIGHTED_CATEGORIES:
	    	model.set("name", i18n.orgTypeWeightedCategories());
	    	model.set("value", GradebookModel.CategoryType.WEIGHTED_CATEGORIES);
	    	break;	
	    }
		
		return model;
	}
	
	/*private void addGradebookFormItems(FormPanel formPanel, I18nConstants i18n) {
		
		ListStore<ModelData> store = new ListStore<ModelData>();
		
		store.add(getCategoryTypeModel(GradebookModel.CategoryType.NO_CATEGORIES));
		store.add(getCategoryTypeModel(GradebookModel.CategoryType.SIMPLE_CATEGORIES));
		store.add(getCategoryTypeModel(GradebookModel.CategoryType.WEIGHTED_CATEGORIES));
		
		typePickerComboBox = new DataList();
		*/
		// Choose a category type
		/*typePickerComboBox = new DataList<ModelData>() {
			
			@Override
			protected void onRender(Element parent, int pos) {
			    super.onRender(parent, pos);
			
			    if (selectionForRender != null)
			    	setSelection(selectionForRender);
			    
			}
			
		}; 
		typePickerComboBox.setStore(store);
		//typePickerComboBox.setAllQuery(null);
		//typePickerComboBox.setEditable(false);
		typePickerComboBox.setFieldLabel(i18n.prefMenuOrgTypeLabel());
		//typePickerComboBox.setForceSelection(true);
		typePickerComboBox.setName(ItemModel.Key.CATEGORYTYPE.name());
		typePickerComboBox.setDisplayField("name");
		typePickerComboBox.setValueField("value");*/
		
		/*typePickerComboBox.add(i18n.orgTypeNoCategories());
		typePickerComboBox.add(i18n.orgTypeCategories());
		typePickerComboBox.add(i18n.orgTypeWeightedCategories());
		*/
		/*typePickerComboBox.setPropertyEditor(new ListModelPropertyEditor<SimpleComboValue<String>>(){
			
		      public String getStringValue(SimpleComboValue<String> value) {
		    	  return value.getValue();
		      }
		});*/
	    
	    //typePickerComboBox.addSelectionChangedListener(comboSelectionChangedListener);
	    
		//formPanel.add(typePickerComboBox);
		
		/*
		
		// Choose a grade type
		MenuItem chooseGradeType = new AriaMenuItem(i18n.prefMenuGradeTypeHeader());
		
		Menu gradeTypeSubMenu = new AriaMenu() {
			protected void onRender(Element parent, int pos) {
			    super.onRender(parent, pos);
			    Accessibility.setRole(el().dom, "menu");
			}
		};
		pointsMenuItem = new AriaCheckMenuItem(i18n.gradeTypePoints());
		pointsMenuItem.setId(AppConstants.ID_GT_POINTS_MENUITEM);
		pointsMenuItem.setGroup("gradetype");
		gradeTypeSubMenu.add(pointsMenuItem);
		percentagesMenuItem = new AriaCheckMenuItem(i18n.gradeTypePercentages());
		percentagesMenuItem.setId(AppConstants.ID_GT_PERCENTAGES_MENUITEM);
		percentagesMenuItem.setGroup("gradetype");
		
		gradeTypeSubMenu.add(percentagesMenuItem);
		
		pointsMenuItem.addListener(Events.CheckChange, menuListener);
		percentagesMenuItem.addListener(Events.CheckChange, menuListener);
		
		chooseGradeType.setSubMenu(gradeTypeSubMenu);
		add(chooseGradeType);
		
		// Choose a grade type
		MenuItem releaseCourseGrades = new AriaMenuItem("Do you want to release course grades to learners?");
		
		Menu releaseCourseGradesSubMenu = new AriaMenu();
		releaseGradesYesMenuItem = new AriaCheckMenuItem(i18n.prefMenuReleaseGradesYes());
		releaseGradesYesMenuItem.setId(AppConstants.ID_RG_YES_MENUITEM);
		releaseGradesYesMenuItem.setGroup("releasegrades");
		releaseCourseGradesSubMenu.add(releaseGradesYesMenuItem);
		releaseGradesNoMenuItem = new AriaCheckMenuItem(i18n.prefMenuReleaseGradesNo());
		releaseGradesNoMenuItem.setId(AppConstants.ID_RG_NO_MENUITEM);
		releaseGradesNoMenuItem.setGroup("releasegrades");
		releaseCourseGradesSubMenu.add(releaseGradesNoMenuItem);
		
		releaseGradesYesMenuItem.addListener(Events.CheckChange, menuListener);
		releaseGradesNoMenuItem.addListener(Events.CheckChange, menuListener);
		
		releaseCourseGrades.setSubMenu(releaseCourseGradesSubMenu);
		add(releaseCourseGrades);*/
	//}
	
	private void initFormBindings() {
		formBindings = new FormBinding(formPanel, true) {
			public void autoBind() {
				for (Field f : panel.getFields()) {
					if (!bindings.containsKey(f)) {
						String name = f.getName();
						if (name != null && name.length() > 0) {
							FieldBinding b = new FieldBinding(f, f.getName()) {
								
								@Override
								public void updateField() {
									/*if (field != null && field instanceof ComboBox) {
										Object val = onConvertModelValue(model.get(property));
									    ((ComboBox<ItemModel>)field).setValue((ItemModel)val);
									} else*/
										super.updateField();
								}
								
								@Override
								protected void onFieldChange(FieldEvent e) {									
									ItemModel itemModel = (ItemModel)this.model;
									e.field.setEnabled(false);
									
									String property = e.field.getName();
									
									if (property.equals(ItemModel.Key.PERCENT_CATEGORY.name())) {
										
										Boolean equalWeight = itemModel.getParent().get(ItemModel.Key.EQUAL_WEIGHT.name());
										
										if (equalWeight != null && equalWeight.booleanValue()) {
											I18nConstants i18n = Registry.get(AppConstants.I18N);
											ConfirmationEvent event = new ConfirmationEvent(i18n.confirmChangingWeightEquallyWeighted());
											
											ItemUpdate itemUpdate = new ItemUpdate(store, itemModel, e.field.getName(), e.oldValue, e.value);
											
											event.okEventType = GradebookEvents.UpdateItem;
											event.okEventData = itemUpdate;
											
											event.cancelEventType = GradebookEvents.RevertItem;
											event.cancelEventData = itemUpdate;
											
											Dispatcher.forwardEvent(GradebookEvents.Confirmation, event);
											
											return;
										}
									} 
									
									if (property.equals(ItemModel.Key.CATEGORY_ID.name())) {
										
										ItemModel oldModel = (ItemModel)e.oldValue;
										ItemModel newModel = (ItemModel)e.value;
										
										Dispatcher.forwardEvent(GradebookEvents.UpdateItem, new ItemUpdate(store, itemModel, e.field.getName(), oldModel.getCategoryId(), newModel.getCategoryId()));
										return;
									}
									
									Dispatcher.forwardEvent(GradebookEvents.UpdateItem, new ItemUpdate(store, itemModel, e.field.getName(), e.oldValue, e.value));
									
								}
								
								@Override
								protected void onModelChange(PropertyChangeEvent event) {
									super.onModelChange(event);
									
									if (field != null)
										field.setEnabled(true);
								}
							};
							/*if (f instanceof ListField) {
								b.setConvertor(new Converter() {
									public Object convertModelValue(Object value) {
										if (value == null)
											return null;
										
										CategoryType categoryType = (CategoryType)value;
									    return getCategoryTypeModel(categoryType);
									}
								});
							}*/
							
							if (f instanceof ComboBox) {
								b.setConvertor(new Converter() {
									public Object convertFieldValue(Object value) {
										
										if (value instanceof ItemModel)
											return ((ItemModel)value).getCategoryId();
										
										
										return value;
									}
									 
									public Object convertModelValue(Object value) {
										if (value == null)
											return null;
										
										if (value instanceof Long) {
											Long categoryId = (Long)value;
										
											return store.findModel(ItemModel.Key.ID.name(), String.valueOf(categoryId));
										}
										
										return null;
									}
								});
							}
							bindings.put(f, b);
						}
					}
				}
			}
		};
		formBindings.setStore(treeStore);
	}
	
	private void initListeners() {

		comboSelectionChangedListener = new SelectionChangedListener<SimpleComboValue<String>>() {

			@Override
			public void selectionChanged(
					SelectionChangedEvent<SimpleComboValue<String>> se) {
				
				
			}
			
		};
		
		selectionChangedListener = new SelectionChangedListener<ItemModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ItemModel> se) {
				ItemModel itemModel = se.getSelectedItem();
				
				if (itemModel != null) {
					Dispatcher.forwardEvent(GradebookEvents.StartEditItem, itemModel);
				}
			}
			
		};
		
		/*treeTableEventListener = new Listener<TreeTableEvent>() {
			public void handleEvent(TreeTableEvent tte) {
				switch (tte.type) {
				case Events.RowClick:
					if (tte.rowIndex > 0) {
						ItemModel itemModel = (ItemModel)tte.item.getModel();
						Dispatcher.forwardEvent(GradebookEvents.StartEditItem, itemModel);
					} else {
						Dispatcher.forwardEvent(GradebookEvents.StartEditItem, null);
					}
					tte.stopEvent();
					break;
				}
			}
		};*/
		
	}


	public TreeStore<ItemModel> getTreeStore() {
		return treeStore;
	}

	
}
