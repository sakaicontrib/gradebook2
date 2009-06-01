package org.sakaiproject.gradebook.gwt.client.gxt.view.panel;

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.InlineEditField;
import org.sakaiproject.gradebook.gwt.client.gxt.InlineEditNumberField;
import org.sakaiproject.gradebook.gwt.client.gxt.ItemModelProcessor;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ItemCreate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ItemUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.view.AppView;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.NullSensitiveCheckBox;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.binding.Converter;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PropertyChangeEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class ItemFormPanel extends ContentPanel {

	private enum Mode { DELETE, EDIT, NEW };
	private enum SelectionType { CLOSE, CREATE, CREATECLOSE, CANCEL, DELETE, SAVE, SAVECLOSE };
	
	private static final String selectionTypeField = "selectionType";
	
	private FormPanel formPanel;
	private FormBinding formBindings;
	
	private LabelField directionsField;
	private TextField<String> nameField;
	private ComboBox<ModelData> categoryTypePicker;
	private ComboBox<ModelData> gradeTypePicker;
	private ComboBox<ItemModel> categoryPicker;
	private CheckBox includedField;
	private CheckBox extraCreditField;
	private CheckBox equallyWeightChildrenField;
	private CheckBox releasedField;
	private NumberField percentCourseGradeField;
	private NumberField percentCategoryField;
	private NumberField pointsField;
	private NumberField dropLowestField;
	private DateField dueDateField;
	private TextField<String> sourceField;
	
	private ListStore<ItemModel> categoryStore;
	private TreeStore<ItemModel> treeStore;
	
	private KeyListener keyListener;
	private Listener<FieldEvent> extraCreditChangeListener;
	private SelectionListener<ButtonEvent> selectionListener;
	private SelectionChangedListener<ItemModel> categorySelectionChangedListener;
	//private StoreListener<ItemModel> storeListener;
	
	private I18nConstants i18n;
	
	private RowLayout layout;
	private RowData topRowData, bottomRowData;
	private Button okButton, okCloseButton, cancelButton;
	private boolean isFull;
	
	private GradebookModel selectedGradebook;
	private ItemModel selectedItemModel;
	private Type createItemType;
	private Record record;
	
	private boolean isDelete;
	private boolean hasChanges;
	
	private Mode mode;
	
	@SuppressWarnings("unchecked")
	public ItemFormPanel(I18nConstants i18n) {
		this.i18n = i18n;
		this.isFull = false;
		setHeaderVisible(true);
		setFrame(true);
		
		layout = new RowLayout();
		setLayout(layout);
		layout.setOrientation(Orientation.VERTICAL);
		
		initListeners();
		
		formPanel = new FormPanel();
		formPanel.setHeaderVisible(false);
		formPanel.setLabelWidth(180);

		directionsField = new LabelField();
		directionsField.setName("directions");
		//formPanel.add(directionsField);
		
		nameField = new InlineEditField<String>();
		nameField.setAllowBlank(false);
		nameField.setName(ItemModel.Key.NAME.name());
		nameField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.NAME));
		nameField.addKeyListener(keyListener);
		
		formPanel.add(nameField);
	
		categoryPicker = new ComboBox<ItemModel>();
		categoryPicker.addKeyListener(keyListener);
		categoryPicker.setDisplayField(ItemModel.Key.NAME.name());
		categoryPicker.setName(ItemModel.Key.CATEGORY_ID.name());
		categoryPicker.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.CATEGORY_NAME));
		categoryPicker.addSelectionChangedListener(categorySelectionChangedListener);
		categoryPicker.setVisible(false);
		formPanel.add(categoryPicker);
		
		
		categoryTypePicker = new ComboBox<ModelData>();
		categoryTypePicker.setDisplayField("name");
		categoryTypePicker.setName(ItemModel.Key.CATEGORYTYPE.name());
		categoryTypePicker.setEditable(false);
		categoryTypePicker.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.CATEGORYTYPE));
		categoryTypePicker.setForceSelection(true);
		categoryTypePicker.setVisible(false);
		formPanel.add(categoryTypePicker);
		
		gradeTypePicker = new ComboBox<ModelData>();
		gradeTypePicker.setDisplayField("name");
		gradeTypePicker.setEditable(false);
		gradeTypePicker.setName(ItemModel.Key.GRADETYPE.name());
		gradeTypePicker.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.GRADETYPE));
		gradeTypePicker.setForceSelection(true);
		gradeTypePicker.setVisible(false);
		
		formPanel.add(gradeTypePicker);

		percentCourseGradeField = new InlineEditNumberField();
		percentCourseGradeField.setName(ItemModel.Key.PERCENT_COURSE_GRADE.name());
		percentCourseGradeField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.PERCENT_COURSE_GRADE));
		percentCourseGradeField.setFormat(DataTypeConversionUtil.getLongNumberFormat());
		percentCourseGradeField.setAllowDecimals(true);
		percentCourseGradeField.setMinValue(Double.valueOf(0.000000d));
		percentCourseGradeField.setMaxValue(Double.valueOf(100.000000d));
		percentCourseGradeField.setVisible(false);
		percentCourseGradeField.addKeyListener(keyListener);
		formPanel.add(percentCourseGradeField);
		
		percentCategoryField = new InlineEditNumberField();
		percentCategoryField.setName(ItemModel.Key.PERCENT_CATEGORY.name());
		percentCategoryField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.PERCENT_CATEGORY));
		percentCategoryField.setFormat(DataTypeConversionUtil.getLongNumberFormat());
		percentCategoryField.setAllowDecimals(true);
		percentCategoryField.setMinValue(Double.valueOf(0.000000d));
		percentCategoryField.setMaxValue(Double.valueOf(100.000000d));
		percentCategoryField.setVisible(false);
		percentCategoryField.addKeyListener(keyListener);
		formPanel.add(percentCategoryField);
			
		pointsField = new InlineEditNumberField();
		pointsField.setName(ItemModel.Key.POINTS.name());
		pointsField.setEmptyText("Default is 100 points");
		pointsField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.POINTS));
		pointsField.setFormat(DataTypeConversionUtil.getDefaultNumberFormat());
		pointsField.setAllowDecimals(true);
		pointsField.setMinValue(Double.valueOf(0.000000d));
		pointsField.setVisible(false);
		pointsField.addKeyListener(keyListener);
		formPanel.add(pointsField);
		
		dropLowestField = new InlineEditNumberField();
		dropLowestField.setEmptyText("0");
		dropLowestField.setName(ItemModel.Key.DROP_LOWEST.name());
		dropLowestField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.DROP_LOWEST));
		dropLowestField.setAllowDecimals(false);
		dropLowestField.setPropertyEditorType(Integer.class);
		dropLowestField.setVisible(false);
		dropLowestField.addKeyListener(keyListener);
		formPanel.add(dropLowestField);
		
		dueDateField = new DateField();
		dueDateField.setName(ItemModel.Key.DUE_DATE.name());
		dueDateField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.DUE_DATE));
		dueDateField.setVisible(false);
		dueDateField.addKeyListener(keyListener);
		formPanel.add(dueDateField);
		
		sourceField = new TextField<String>();
		sourceField.setName(ItemModel.Key.SOURCE.name());
		sourceField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.SOURCE));
		sourceField.setEnabled(false);
		sourceField.setEmptyText("Gradebook");
		sourceField.setVisible(false);
		formPanel.add(sourceField);

		includedField = new NullSensitiveCheckBox();
		includedField.setName(ItemModel.Key.INCLUDED.name());
		includedField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.INCLUDED));
		includedField.setVisible(false);
		formPanel.add(includedField);
		
		extraCreditField = new NullSensitiveCheckBox();
		extraCreditField.setName(ItemModel.Key.EXTRA_CREDIT.name());
		extraCreditField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.EXTRA_CREDIT));
		extraCreditField.setVisible(false);
		extraCreditField.addListener(Events.Change, extraCreditChangeListener);
		formPanel.add(extraCreditField);
		
		equallyWeightChildrenField = new NullSensitiveCheckBox();
		equallyWeightChildrenField.setName(ItemModel.Key.EQUAL_WEIGHT.name());
		equallyWeightChildrenField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.EQUAL_WEIGHT));
		equallyWeightChildrenField.setVisible(false);
		formPanel.add(equallyWeightChildrenField);
		
		releasedField = new NullSensitiveCheckBox();
		releasedField.setName(ItemModel.Key.RELEASED.name());
		releasedField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.RELEASED));
		releasedField.setVisible(false);
		formPanel.add(releasedField);
			
		okButton = new Button("", selectionListener);
		addButton(okButton);
		
		okCloseButton = new Button(i18n.saveAndCloseButton(), selectionListener);
		addButton(okCloseButton);
		
		cancelButton = new Button(i18n.cancelButton(), selectionListener);
		cancelButton.setData(selectionTypeField, SelectionType.CANCEL);
		
		addButton(cancelButton);
		
		topRowData = new RowData(1, 70, new Margins(10));
		bottomRowData = new RowData(1, 1, new Margins(0, 0, 5, 0));
		add(directionsField, topRowData);
		add(formPanel, bottomRowData);
	}
	
	public void onActionCompleted() {
		//okButton.setEnabled(true);
	}
	
	public void onConfirmDeleteItem(ItemModel itemModel) {
		this.mode = Mode.DELETE;
		this.createItemType = null;
		this.selectedItemModel = itemModel;
		
		if (formBindings == null)
			initFormBindings();
		
		okButton.setText(i18n.deleteButton());
		okButton.setData(selectionTypeField, SelectionType.DELETE);
		okCloseButton.setVisible(false);
		
		if (itemModel != null) {
			Type itemType = itemModel.getItemType();
			initState(itemType, itemModel, true);
			directionsField.setText(i18n.directionsConfirmDeleteItem());
			directionsField.setStyleName("gbWarning");
			directionsField.setVisible(true);
			
			formBindings.bind(itemModel);
			
			Dispatcher.forwardEvent(GradebookEvents.ExpandEastPanel.getEventType(), AppView.EastCard.DELETE_ITEM);
		}
	}
	
	public void onEditItem(ItemModel itemModel, boolean expand) {
		
		if (!expand && !isVisible())
			return;
		
		//if (expand) {
			AppView.EastCard activeCard = AppView.EastCard.EDIT_ITEM;
			switch (itemModel.getItemType()) {
			case CATEGORY:
				activeCard = AppView.EastCard.EDIT_CATEGORY;
				break;
			case GRADEBOOK:
				activeCard = AppView.EastCard.EDIT_GRADEBOOK;
			}
			Dispatcher.forwardEvent(GradebookEvents.ExpandEastPanel.getEventType(), activeCard);
		//}
		
		if (mode == Mode.EDIT && selectedItemModel != null && itemModel != null && itemModel.equals(selectedItemModel))
			return;
		
		this.mode = Mode.EDIT;
		this.createItemType = null;
		this.selectedItemModel = itemModel;
		this.directionsField.setText("");
		this.directionsField.setVisible(false);
		
		if (formBindings == null)
			initFormBindings();
		
		okButton.setText(i18n.saveButton());
		okButton.setData(selectionTypeField, SelectionType.SAVE);
		okCloseButton.setVisible(true);
		okCloseButton.setData(selectionTypeField, SelectionType.SAVECLOSE);
		okCloseButton.setText(i18n.saveAndCloseButton());
		
		if (itemModel != null) {
			Type itemType = itemModel.getItemType();
			initState(itemType, itemModel, false);
			clearChanges();
			formBindings.bind(itemModel);
		} else {
			formBindings.unbind();
		}
	}

	public void onItemCreated(ItemModel itemModel) {

		ItemModelProcessor processor = new ItemModelProcessor(itemModel) {
			
			public void doCategory(ItemModel categoryModel) {
				clearForm(categoryModel);
				categoryStore.add(categoryModel);
			}
			
			public void doItem(ItemModel itemModel) {
				clearForm(itemModel);
			}
			
			private void clearForm(ItemModel itemModel) {
				if (itemModel != null && itemModel.isActive()) {
					switch (mode) {
					case NEW:
						formPanel.clear();
						Type itemType = Type.ITEM;
						
						if (itemModel.getItemType() != null)
							itemType = itemModel.getItemType();
						
						initState(itemType, itemModel, false);
						establishSelectedCategoryState(itemModel);
						break;
					}
				}
			}
			
		};
		
		processor.process();
	}
	
	public void onItemDeleted(ItemModel itemModel) {
		ItemModelProcessor processor = new ItemModelProcessor(itemModel) {
			
			public void doCategory(ItemModel categoryModel) {
				clearForm(categoryModel);
				if (DataTypeConversionUtil.checkBoolean(categoryModel.getRemoved()))
					categoryStore.remove(categoryModel);
			}
			
			public void doItem(ItemModel itemModel) {
				clearForm(itemModel);
			}
			
			private void clearForm(ItemModel itemModel) {
				if (itemModel != null && itemModel.isActive()) {
					switch (mode) {
					case NEW:
						formPanel.clear();
						Type itemType = Type.ITEM;
						
						if (itemModel.getItemType() != null)
							itemType = itemModel.getItemType();
						
						initState(itemType, itemModel, false);
						establishSelectedCategoryState(itemModel);
						break;
					}
				}
			}
			
		};
		
		processor.process();
	}
	
	public void onItemUpdated(ItemModel itemModel) {
		categoryPicker.setEnabled(true);
		categoryTypePicker.setEnabled(true);
		gradeTypePicker.setEnabled(true);
		
		
		/*ItemModelProcessor processor = new ItemModelProcessor(itemModel) {
			
			public void doCategory(ItemModel categoryModel) {
				clearForm(categoryModel);
				
				if (DataTypeConversionUtil.checkBoolean(categoryModel.getRemoved()))
					categoryStore.remove(categoryModel);
			}
			
			public void doItem(ItemModel itemModel) {
				clearForm(itemModel);
			}
			
			private void clearForm(ItemModel itemModel) {
				if (itemModel != null && itemModel.isActive()) {
					switch (mode) {
					case NEW:
						formPanel.clear();
						Type itemType = Type.ITEM;
						
						if (itemModel.getItemType() != null)
							itemType = itemModel.getItemType();
						
						initState(itemType, itemModel, false);
						establishSelectedCategoryState(itemModel);
						break;
					}
				}
			}
			
		};
		
		processor.process();
		*/
		/*ItemModelProcessor processor = new ItemModelProcessor(itemModel) {
			
			public void doCategory(ItemModel categoryModel) {
				resetValues(categoryModel, false);
			}
			
			public void doItem(ItemModel itemModel) {
				resetValues(itemModel, true);
			}
			
			
			private void resetValues(ItemModel itemModel, boolean isItem) {
		
				if (itemModel != null && itemModel.isActive()) {
					if (selectedItemModel != null && selectedItemModel.equals(itemModel)) {
						switch (mode) {
						case EDIT:
							onEditItem(itemModel, false);
							
							break;
						}
					}
				}
				
			}
			
		};
		
		processor.process();*/
		
	}
	
	public void onLoadItemTreeModel(ItemModel rootItemModel) {
		
		if (categoryStore != null) {
			categoryStore.removeAllListeners();
			categoryStore.removeAll();
		}
		
		// FIXME: Do we need to eliminate old category stores?  
		categoryStore = new ListStore<ItemModel>();
		for (ItemModel gradebook : rootItemModel.getChildren()) {
			for (ItemModel category : gradebook.getChildren()) {
			
				// Ensure that we're dealing with a category
				if (category.getItemType() == Type.CATEGORY) {
					categoryStore.add(category);
				}
			
			}
		}
		categoryPicker.setStore(categoryStore);
		
		
		ListStore<ModelData> categoryTypeStore = new ListStore<ModelData>();
		
		categoryTypeStore.add(getCategoryTypeModel(GradebookModel.CategoryType.NO_CATEGORIES));
		categoryTypeStore.add(getCategoryTypeModel(GradebookModel.CategoryType.SIMPLE_CATEGORIES));
		categoryTypeStore.add(getCategoryTypeModel(GradebookModel.CategoryType.WEIGHTED_CATEGORIES));
		
		categoryTypePicker.setStore(categoryTypeStore);
		
		
		ListStore<ModelData> gradeTypeStore = new ListStore<ModelData>();
		
		gradeTypeStore.add(getGradeTypeModel(GradebookModel.GradeType.POINTS));
		gradeTypeStore.add(getGradeTypeModel(GradebookModel.GradeType.PERCENTAGES));
		
		gradeTypePicker.setStore(gradeTypeStore);

	}
	
	public void onNewCategory(ItemModel itemModel) {
		this.mode = Mode.NEW;
		
		this.directionsField.setText("");
		this.directionsField.setVisible(false);
		this.createItemType = Type.CATEGORY;
		this.selectedItemModel = null;
		
		if (formBindings != null) 
			formBindings.unbind();

		includedField.setValue(Boolean.TRUE);
		
		okButton.setText(i18n.createButton());
		okButton.setData(selectionTypeField, SelectionType.CREATE);
		okCloseButton.setVisible(true);
		okCloseButton.setData(selectionTypeField, SelectionType.CREATECLOSE);
		okCloseButton.setText(i18n.createAndCloseButton());
		
		clearFields();
		
		initState(Type.CATEGORY, itemModel, false);
		establishSelectedCategoryState(itemModel);
		
		ItemModel gradebookItemModel = selectedGradebook.getGradebookItemModel();
		
		if (gradebookItemModel != null) {
			
			
		}
		
		includedField.setValue(Boolean.TRUE);
	}
	
	public void onNewItem(ItemModel itemModel) {
		this.mode = Mode.NEW;
		
		this.directionsField.setText("");
		this.directionsField.setVisible(false);
		this.createItemType = Type.ITEM;
		this.selectedItemModel = null;
		
		if (formBindings != null) 
			formBindings.unbind();

		okButton.setText(i18n.createButton());
		okButton.setData(selectionTypeField, SelectionType.CREATE);
		okCloseButton.setVisible(true);
		okCloseButton.setData(selectionTypeField, SelectionType.CREATECLOSE);
		okCloseButton.setText(i18n.createAndCloseButton());
		
		includedField.setValue(Boolean.TRUE);
		
		if (itemModel != null) {
			if (itemModel.getCategoryId() != null) {
				List<ItemModel> models = treeStore.findModels(ItemModel.Key.ID.name(), String.valueOf(itemModel.getCategoryId()));
				for (ItemModel category : models) {
					if (category.getItemType() == Type.CATEGORY)
						categoryPicker.setValue(category);
				}
			}
		}
		
		clearFields();
		
		initState(Type.ITEM, itemModel, false);
		establishSelectedCategoryState(itemModel);
		
		includedField.setValue(Boolean.TRUE);
	}
	
	public void onTreeStoreInitialized(TreeStore<ItemModel> treeStore) {
		this.treeStore = treeStore;
		//this.treeStore.addStoreListener(storeListener);
		
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
		this.selectedGradebook = selectedGradebook;
	}

	private CategoryType getCategoryType(ModelData categoryTypeModel) {
		return categoryTypeModel.get("value");
	}
	
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
	
	private GradeType getGradeType(ModelData gradeTypeModel) {
		return gradeTypeModel.get("value");
	}
	
	private ModelData getGradeTypeModel(GradeType gradeType) {
		ModelData model = new BaseModelData();
		
		switch (gradeType) {
		case POINTS:
			model.set("name", i18n.gradeTypePoints());
			model.set("value", GradebookModel.GradeType.POINTS);
			break;
		case PERCENTAGES:
			model.set("name", i18n.gradeTypePercentages());
			model.set("value", GradebookModel.GradeType.PERCENTAGES);
			break;
		}
		
		return model;
	}	
	
	private void initState(Type itemType, ItemModel itemModel, boolean isDelete) {
		this.isDelete = isDelete;
		clearChanges();
		
		okButton.setEnabled(true);
		okCloseButton.setEnabled(true);
		
		CategoryType categoryType = selectedGradebook.getGradebookItemModel().getCategoryType();
		
		boolean hasCategories = categoryType != CategoryType.NO_CATEGORIES;
		boolean hasWeights = categoryType == CategoryType.WEIGHTED_CATEGORIES;
		boolean isNotGradebook = itemType != Type.GRADEBOOK;
		boolean isCategory = itemType == Type.CATEGORY;
		boolean isItem = itemType == Type.ITEM;
		boolean isExternal = false;
		boolean isCreateNewItem = createItemType == Type.ITEM && mode == Mode.NEW;
		
		boolean isPercentCategoryVisible = false;

		formPanel.clear();
		
		
		if (itemModel != null) {
			boolean isExtraCredit = DataTypeConversionUtil.checkBoolean(itemModel.getExtraCredit());
			String source = itemModel.get(ItemModel.Key.SOURCE.name());
			isExternal = source != null && source.trim().length() > 0;
			ItemModel category = null;
			switch (itemModel.getItemType()) {
			case GRADEBOOK:
				isPercentCategoryVisible = false;
				break;
			case CATEGORY:
				category = itemModel;
				if (category != null && category.getItemType() == Type.CATEGORY)
					isPercentCategoryVisible = hasWeights || isExtraCredit || !DataTypeConversionUtil.checkBoolean(category.getEqualWeightAssignments());
				break;
			case ITEM:
				category = itemModel.getParent();
				if (category != null && category.getItemType() == Type.CATEGORY)
					isPercentCategoryVisible = hasWeights || isExtraCredit || !DataTypeConversionUtil.checkBoolean(category.getEqualWeightAssignments());
				break;
			default:
				isPercentCategoryVisible = (hasWeights || isExtraCredit) && isItem;
			}
		} else {
			isPercentCategoryVisible = hasWeights && isItem;
		}
		
		initField(nameField, !isDelete && !isExternal, true);
		initField(pointsField, !isDelete && !isExternal, isItem);
		initField(percentCategoryField, !isDelete && (isItem || isCreateNewItem), isPercentCategoryVisible);
		initField(percentCourseGradeField, !isDelete, isCategory && hasWeights);
		initField(equallyWeightChildrenField, !isDelete, isCategory && hasWeights);
		initField(extraCreditField, !isDelete, isNotGradebook);
		initField(dropLowestField, !isDelete, isCategory);
		initField(dueDateField, !isDelete && !isExternal, isItem);
		initField(includedField, !isDelete, isNotGradebook);
		initField(releasedField, !isDelete, isItem);
		initField(categoryPicker, !isDelete, hasCategories && isItem);
		initField(categoryTypePicker, true, !isNotGradebook);
		initField(gradeTypePicker, true, !isNotGradebook);
		initField(sourceField, false, isItem);
	}
	
	private void initField(Field field, boolean isEnabled, boolean isVisible) {
		//if (field.isEnabled() != isEnabled)
			field.setEnabled(isEnabled);
		
		//if (!field.isRendered() || field.isVisible() != isVisible)
			field.setVisible(isVisible);
		
		field.clearInvalid();
		field.clearState();
		
		if (formBindings != null) {
			FieldBinding fieldBinding = formBindings.getBinding(field);
			if (fieldBinding != null && fieldBinding.getModel() != null && fieldBinding.getProperty() != null)
				fieldBinding.updateField();
		}
	}
	
	
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
									//e.field.setEnabled(false);
									
									setChanges();
									
									String property = e.field.getName();
									
									record = store.getRecord(selectedItemModel);
									record.beginEdit();
									
									if (property.equals(ItemModel.Key.CATEGORY_ID.name())) {
										
										ItemModel oldModel = (ItemModel)e.oldValue;
										ItemModel newModel = (ItemModel)e.value;
										
										record.set(property, newModel.getCategoryId());
		
									} else if (property.equals(ItemModel.Key.CATEGORYTYPE.name())) {
										
										CategoryType oldCategoryType = getCategoryType((ModelData)e.oldValue);
										CategoryType newCategoryType = getCategoryType((ModelData)e.value);
										
										record.set(property, newCategoryType);

									} else if (property.equals(ItemModel.Key.GRADETYPE.name())) {
										
										GradeType oldGradeType = getGradeType((ModelData)e.oldValue);
										GradeType newGradeType = getGradeType((ModelData)e.value);
										
										record.set(property, newGradeType);

									} else {
										record.set(property, e.value);
									}
									
									//Dispatcher.forwardEvent(GradebookEvents.UpdateItem, new ItemUpdate(store, itemModel, e.field.getName(), e.oldValue, e.value));
									
								}
								
								@Override
								protected void onModelChange(PropertyChangeEvent event) {
									super.onModelChange(event);

								}
							};

							if (name.equals(ItemModel.Key.CATEGORY_ID.name())) {
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
							} else if (name.equals(ItemModel.Key.CATEGORYTYPE.name()) ||
									name.equals(ItemModel.Key.GRADETYPE.name())) {
								b.setConvertor(new Converter() {
									public Object convertFieldValue(Object value) {
										return value;
									}
									 
									public Object convertModelValue(Object value) {
										if (value == null)
											return null;
										
										if (value instanceof CategoryType)
											return getCategoryTypeModel((CategoryType)value);
										if (value instanceof GradeType)
											return getGradeTypeModel((GradeType)value);
										else if (value instanceof ModelData) {
											return value;
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
		
		categorySelectionChangedListener = new SelectionChangedListener<ItemModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ItemModel> se) {
				ItemModel itemModel = se.getSelectedItem();
				refreshSelectedCategoryState(itemModel);
				setChanges();
			}
			
		};
		
		keyListener = new KeyListener() {

			public void componentKeyPress(ComponentEvent event) {
				setChanges();
			}
			
		};
		
		extraCreditChangeListener = new Listener<FieldEvent>() {

			public void handleEvent(FieldEvent fe) {
				Boolean isChecked = ((CheckBox)fe.field).getValue();
				CategoryType categoryType = selectedGradebook.getGradebookItemModel().getCategoryType();
				ItemModel category = categoryPicker.getValue();
				Boolean isEqualWeight = category == null ? Boolean.FALSE : category.getEqualWeightAssignments();
				boolean hasWeights = categoryType == CategoryType.WEIGHTED_CATEGORIES;
				
				if (selectedItemModel != null) {
					switch (selectedItemModel.getItemType()) {
					case ITEM:
						initField(percentCategoryField, !isDelete, hasWeights 
								&& (!DataTypeConversionUtil.checkBoolean(isEqualWeight) 
										|| (isChecked != null && isChecked.booleanValue())));
						break;
					}
				} else if (createItemType == Type.ITEM) {
					initField(percentCategoryField, !isDelete, hasWeights 
							&& (!DataTypeConversionUtil.checkBoolean(isEqualWeight) || (isChecked != null && isChecked.booleanValue())));
				}
				
			}
			
		};
		
		selectionListener = new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent be) {
				Button button = be.button;
				if (button != null) {
					SelectionType selectionType = button.getData(selectionTypeField);
					if (selectionType != null) {
						
						boolean close = false;
						
						switch (selectionType) {
						case CLOSE:
							Dispatcher.forwardEvent(GradebookEvents.HideFormPanel.getEventType(), Boolean.FALSE);
							break;
						case CREATECLOSE:
							close = true;
						case CREATE:
							if (nameField.getValue() == null) {
								MessageBox.alert(i18n.itemNameRequiredTitle(), i18n.itemNameRequiredText(), null);
								return;
							}
							
							ItemModel item = new ItemModel();
							
							ItemModel category = categoryPicker.getValue();
							
							if (category != null) 
								item.setCategoryId(category.getCategoryId());
							
							item.setName(nameField.getValue());
							item.setExtraCredit(extraCreditField.getValue());
							item.setEqualWeightAssignments(equallyWeightChildrenField.getValue());
							item.setIncluded(includedField.getValue());
							item.setReleased(releasedField.getValue());
							item.setPercentCourseGrade((Double)percentCourseGradeField.getValue());
							item.setPercentCategory((Double)percentCategoryField.getValue());
							item.setPoints((Double)pointsField.getValue());
							item.setDueDate(dueDateField.getValue());
							item.setItemType(createItemType);
							
							clearChanges();
							
							Dispatcher.forwardEvent(GradebookEvents.CreateItem.getEventType(), new ItemCreate(treeStore, item, close));
							break;
						case DELETE:
							Dispatcher.forwardEvent(GradebookEvents.HideFormPanel.getEventType(), Boolean.FALSE);
							Dispatcher.forwardEvent(GradebookEvents.DeleteItem.getEventType(), new ItemUpdate(treeStore, selectedItemModel, ItemModel.Key.REMOVED.name(), Boolean.FALSE, Boolean.TRUE));
							break;
						case CANCEL:
							Dispatcher.forwardEvent(GradebookEvents.HideFormPanel.getEventType(), Boolean.FALSE);
							break;
						case SAVECLOSE:
							close = true;
						case SAVE:
							if (nameField.validate() 
									&& (!percentCategoryField.isVisible() || percentCategoryField.validate()) 
									&& (!percentCourseGradeField.isVisible() || percentCourseGradeField.validate())
									&& (!pointsField.isVisible() || pointsField.validate())) {
								clearChanges();
								Dispatcher.forwardEvent(GradebookEvents.UpdateItem.getEventType(), new ItemUpdate(treeStore, record, selectedItemModel, close));
							}
							break;
						}
					}
				}

			}
			
		};
		
/*		storeListener = new StoreListener<ItemModel>() {
			
			public void storeUpdate(StoreEvent<ItemModel> se) {
				switch (se.operation) {
				case REJECT:
					if (selectedItemModel != null) {
						switch (mode) {
						case EDIT:
							Type itemType = selectedItemModel.getItemType();
							//initState(itemType, selectedItemModel, false);
							break;
						};
					}
					break;
				}
			}
			
		};*/
		
	}
	
	private void establishSelectedCategoryState(ItemModel itemModel) {
		if (itemModel == null)
			return;
		
		CategoryType categoryType = selectedGradebook.getGradebookItemModel().getCategoryType();
		
		boolean hasCategories = categoryType != CategoryType.NO_CATEGORIES;
		
		if (hasCategories) {
			
			ItemModel category = null;
			switch (itemModel.getItemType()) {
			case GRADEBOOK:
				break;
			case CATEGORY:
				categoryPicker.setValue(itemModel);
				break;
			case ITEM:
				category = itemModel.getParent();
				if (category != null && category.getItemType() == Type.CATEGORY)
					categoryPicker.setValue(category);
				break;
			}
		} 
	}

	private void refreshSelectedCategoryState(ItemModel itemModel) {
		CategoryType categoryType = selectedGradebook.getGradebookItemModel().getCategoryType();
		
		boolean hasCategories = categoryType != CategoryType.NO_CATEGORIES;
		boolean hasWeights = categoryType == CategoryType.WEIGHTED_CATEGORIES;
		boolean isPercentCategoryVisible = false;
		boolean isItem = selectedItemModel != null && selectedItemModel.getItemType() == ItemModel.Type.ITEM;
		boolean isCreateNewItem = createItemType == Type.ITEM && mode == Mode.NEW;
		
		if (itemModel != null) {
		
			ItemModel category = null;
			switch (itemModel.getItemType()) {
			case GRADEBOOK:
				isPercentCategoryVisible = false;
				break;
			case CATEGORY:
				category = itemModel;
				if (category != null && category.getItemType() == Type.CATEGORY)
					isPercentCategoryVisible = hasCategories && hasWeights 
						&& (!DataTypeConversionUtil.checkBoolean(category.getEqualWeightAssignments()) || 
						DataTypeConversionUtil.checkBoolean(extraCreditField.getValue()));
				break;
			case ITEM:
				category = itemModel.getParent();
				if (category != null && category.getItemType() == Type.CATEGORY)
					isPercentCategoryVisible = hasCategories && hasWeights && 
						(!DataTypeConversionUtil.checkBoolean(category.getEqualWeightAssignments()) || 
								DataTypeConversionUtil.checkBoolean(extraCreditField.getValue()));
				break;
			}
		} 
		
		initField(percentCategoryField, !isDelete && (isItem || isCreateNewItem), isPercentCategoryVisible || DataTypeConversionUtil.checkBoolean(extraCreditField.getValue()));
	}
	

	public TreeStore<ItemModel> getTreeStore() {
		return treeStore;
	}

	
	public boolean hasChanges() {
		return hasChanges;
	}
	
	
	public void setChanges() {
		if (!hasChanges) {
			hasChanges = true;
			okButton.setEnabled(true);
			okCloseButton.setEnabled(true);
		}
	}
	
	public void clearChanges() {
		hasChanges = false;
		okButton.setEnabled(false);
		okCloseButton.setEnabled(false);
	}
	
	private void clearFields() {
		
	}
	
}
