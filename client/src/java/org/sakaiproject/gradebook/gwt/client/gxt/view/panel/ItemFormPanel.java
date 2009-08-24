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

import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.InlineEditField;
import org.sakaiproject.gradebook.gwt.client.gxt.InlineEditNumberField;
import org.sakaiproject.gradebook.gwt.client.gxt.ItemModelProcessor;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaButton;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ItemCreate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ItemUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.view.AppView;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.NullSensitiveCheckBox;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModelComparer;
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
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
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
	private CheckBox releaseGradesField;
	private CheckBox releaseItemsField;
	private CheckBox scaledExtraCreditField;
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
	private Listener<FieldEvent> checkboxChangeListener;
	private SelectionListener<ButtonEvent> selectionListener;
	private SelectionChangedListener<ItemModel> categorySelectionChangedListener;
	private SelectionChangedListener otherSelectionChangedListener;

	private I18nConstants i18n;

	private RowLayout layout;
	private RowData topRowData, bottomRowData;
	private Button okButton, okCloseButton, cancelButton;

	private GradebookModel selectedGradebook;
	private ItemModel selectedItemModel;
	private Type createItemType;

	private boolean isDelete;
	private boolean hasChanges;

	private Mode mode;

	@SuppressWarnings("unchecked")
	public ItemFormPanel(I18nConstants i18n) {
		this.i18n = i18n;
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

		nameField = new InlineEditField<String>();
		nameField.setAllowBlank(false);
		nameField.setName(ItemModel.Key.NAME.name());
		nameField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.NAME));
		nameField.addKeyListener(keyListener);

		formPanel.add(nameField);

		categoryStore = new ListStore<ItemModel>();
		categoryStore.setModelComparer(new ItemModelComparer<ItemModel>());

		categoryPicker = new ComboBox<ItemModel>();
		categoryPicker.addKeyListener(keyListener);
		categoryPicker.setDisplayField(ItemModel.Key.NAME.name());
		categoryPicker.setName(ItemModel.Key.CATEGORY_ID.name());
		categoryPicker.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.CATEGORY_NAME));
		categoryPicker.setVisible(false);
		categoryPicker.setStore(categoryStore);
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

		releaseGradesField = new NullSensitiveCheckBox();
		releaseGradesField.setName(ItemModel.Key.RELEASEGRADES.name());
		releaseGradesField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.RELEASEGRADES));
		releaseGradesField.setVisible(false);
		formPanel.add(releaseGradesField);

		releaseItemsField = new NullSensitiveCheckBox();
		releaseItemsField.setName(ItemModel.Key.RELEASEITEMS.name());
		releaseItemsField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.RELEASEITEMS));
		releaseItemsField.setVisible(false);
		formPanel.add(releaseItemsField);

		percentCourseGradeField = new InlineEditNumberField();
		percentCourseGradeField.setName(ItemModel.Key.PERCENT_COURSE_GRADE.name());
		percentCourseGradeField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.PERCENT_COURSE_GRADE));
		percentCourseGradeField.setFormat(DataTypeConversionUtil.getLongNumberFormat());
		percentCourseGradeField.setAllowDecimals(true);
		percentCourseGradeField.setMinValue(Double.valueOf(0.000000d));
		percentCourseGradeField.setMaxValue(Double.valueOf(100.000000d));
		percentCourseGradeField.setVisible(false);
		formPanel.add(percentCourseGradeField);

		percentCategoryField = new InlineEditNumberField();
		percentCategoryField.setName(ItemModel.Key.PERCENT_CATEGORY.name());
		percentCategoryField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.PERCENT_CATEGORY));
		percentCategoryField.setFormat(DataTypeConversionUtil.getLongNumberFormat());
		percentCategoryField.setAllowDecimals(true);
		percentCategoryField.setMinValue(Double.valueOf(0.000000d));
		percentCategoryField.setMaxValue(Double.valueOf(100.000000d));
		percentCategoryField.setVisible(false);
		formPanel.add(percentCategoryField);

		pointsField = new InlineEditNumberField();
		pointsField.setName(ItemModel.Key.POINTS.name());
		pointsField.setEmptyText("Default is 100 points");
		pointsField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.POINTS));
		pointsField.setFormat(DataTypeConversionUtil.getDefaultNumberFormat());
		pointsField.setAllowDecimals(true);
		pointsField.setMinValue(Double.valueOf(0.0001d));
		pointsField.setVisible(false);
		formPanel.add(pointsField);
		
		dropLowestField = new InlineEditNumberField();
		dropLowestField.setEmptyText("0");
		dropLowestField.setName(ItemModel.Key.DROP_LOWEST.name());
		dropLowestField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.DROP_LOWEST));
		dropLowestField.setAllowDecimals(false);
		dropLowestField.setPropertyEditorType(Integer.class);
		dropLowestField.setVisible(false);
		formPanel.add(dropLowestField);

		dueDateField = new DateField();
		dueDateField.setName(ItemModel.Key.DUE_DATE.name());
		dueDateField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.DUE_DATE));
		dueDateField.setVisible(false);
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

		scaledExtraCreditField = new NullSensitiveCheckBox();
		scaledExtraCreditField.setName(ItemModel.Key.EXTRA_CREDIT_SCALED.name());
		scaledExtraCreditField.setFieldLabel(ItemModel.getPropertyName(ItemModel.Key.EXTRA_CREDIT_SCALED));
		scaledExtraCreditField.setVisible(false);
		formPanel.add(scaledExtraCreditField);
		
		okButton = new AriaButton("", selectionListener, 's');
		addButton(okButton);

		okCloseButton = new AriaButton(i18n.saveAndCloseButton(), selectionListener, 'm');
		addButton(okCloseButton);

		cancelButton = new AriaButton(i18n.cancelButton(), selectionListener, 'x');
		cancelButton.setData(selectionTypeField, SelectionType.CANCEL);

		addButton(cancelButton);

		topRowData = new RowData(1, 70, new Margins(10));
		bottomRowData = new RowData(1, 1, new Margins(0, 0, 5, 0));
		add(directionsField, topRowData);
		add(formPanel, bottomRowData);
	}

	public void onActionCompleted() {
	}

	public void onConfirmDeleteItem(ItemModel itemModel) {
		removeListeners();
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
		addListeners();
	}

	public void onEditItem(ItemModel itemModel, boolean expand) {

		if (!expand && !isVisible())
			return;

		AppView.EastCard activeCard = AppView.EastCard.EDIT_ITEM;
		
		if (itemModel != null) {
			
			switch (itemModel.getItemType()) {
			case CATEGORY:
				activeCard = AppView.EastCard.EDIT_CATEGORY;
				break;
			case GRADEBOOK:
				activeCard = AppView.EastCard.EDIT_GRADEBOOK;
			}
			
		}
		Dispatcher.forwardEvent(GradebookEvents.ExpandEastPanel.getEventType(), activeCard);

		clearActiveRecord();

		if (mode == Mode.EDIT && selectedItemModel != null && itemModel != null && itemModel.equals(selectedItemModel))
			return;

		removeListeners();
		formPanel.hide();

		this.mode = Mode.EDIT;
		this.createItemType = null;
		this.selectedItemModel = itemModel;
		this.directionsField.setText("");
		this.directionsField.setVisible(false);

		boolean isInitialized = false;
		if (formBindings == null) {
			initFormBindings();
			isInitialized = true;
		} else {
			formBindings.unbind();
		}

		boolean isAllowedToEdit = DataTypeConversionUtil.checkBoolean(selectedGradebook.isUserAbleToEditAssessments());


		okButton.setText(i18n.saveButton());
		okButton.setData(selectionTypeField, SelectionType.SAVE);
		okButton.setVisible(isAllowedToEdit && itemModel != null && itemModel.isEditable());
		okCloseButton.setVisible(isAllowedToEdit && itemModel != null && itemModel.isEditable());
		okCloseButton.setData(selectionTypeField, SelectionType.SAVECLOSE);
		okCloseButton.setText(i18n.saveAndCloseButton());

		if (itemModel != null) {
			Type itemType = itemModel.getItemType();

			clearChanges();
			formBindings.bind(itemModel);
			initState(itemType, itemModel, false);
		} else {
			formBindings.unbind();
		}

		formPanel.show();
		nameField.focus();
		addListeners();
	}

	public void onItemCreated(ItemModel itemModel) {

		ItemModelProcessor processor = new ItemModelProcessor(itemModel) {

			public void doCategory(ItemModel categoryModel) {
				categoryStore.add(categoryModel);
			}

			public void doItem(ItemModel itemModel) {
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

	}

	public void onItemUpdated(ItemModel itemModel) {
		categoryPicker.setEnabled(true);
		categoryTypePicker.setEnabled(true);
		gradeTypePicker.setEnabled(true);
		clearChanges();
	}

	public void onLoadItemTreeModel(ItemModel rootItemModel) {

		if (categoryStore != null) {
			categoryStore.removeAll();
		}

		for (ItemModel gradebook : rootItemModel.getChildren()) {
			for (ItemModel category : gradebook.getChildren()) {

				// Ensure that we're dealing with a category
				if (category.getItemType() == Type.CATEGORY) {
					categoryStore.add(category);
				}

			}
		}

		ListStore<ModelData> categoryTypeStore = new ListStore<ModelData>();

		categoryTypeStore.add(getCategoryTypeModel(GradebookModel.CategoryType.NO_CATEGORIES));
		categoryTypeStore.add(getCategoryTypeModel(GradebookModel.CategoryType.SIMPLE_CATEGORIES));
		categoryTypeStore.add(getCategoryTypeModel(GradebookModel.CategoryType.WEIGHTED_CATEGORIES));

		categoryTypePicker.setStore(categoryTypeStore);


		ListStore<ModelData> gradeTypeStore = new ListStore<ModelData>();

		gradeTypeStore.add(getGradeTypeModel(GradebookModel.GradeType.POINTS));
		gradeTypeStore.add(getGradeTypeModel(GradebookModel.GradeType.PERCENTAGES));
		gradeTypeStore.add(getGradeTypeModel(GradebookModel.GradeType.LETTERS));
		
		gradeTypePicker.setStore(gradeTypeStore);
		
	
		if (selectedItemModel != null) {
			removeListeners();
			/*CategoryType categoryType = selectedGradebook.getGradebookItemModel().getCategoryType();
			Type itemType = selectedItemModel.getItemType();
			boolean isAllowedToEdit = DataTypeConversionUtil.checkBoolean(selectedGradebook.isUserAbleToEditAssessments());
			boolean hasWeights = categoryType == CategoryType.WEIGHTED_CATEGORIES;
			boolean isNotGradebook = itemType != Type.GRADEBOOK;
			
			initField(scaledExtraCreditField, !isDelete && isAllowedToEdit, !isNotGradebook);*/
			addListeners();
		}

	}

	public void onNewCategory(ItemModel itemModel) {
		removeListeners();
		this.mode = Mode.NEW;

		this.directionsField.setText("");
		this.directionsField.setVisible(false);
		this.createItemType = Type.CATEGORY;
		this.selectedItemModel = null;

		clearActiveRecord();

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

		includedField.setValue(Boolean.TRUE);
		nameField.focus();
		addListeners();
	}

	public void onNewItem(ItemModel itemModel) {
		removeListeners();
		this.mode = Mode.NEW;

		this.directionsField.setText("");
		this.directionsField.setVisible(false);
		this.createItemType = Type.ITEM;
		this.selectedItemModel = null;

		clearActiveRecord();

		if (formBindings != null) 
			formBindings.unbind();

		okButton.setText(i18n.createButton());
		okButton.setData(selectionTypeField, SelectionType.CREATE);
		okCloseButton.setVisible(true);
		okCloseButton.setData(selectionTypeField, SelectionType.CREATECLOSE);
		okCloseButton.setText(i18n.createAndCloseButton());

		includedField.setValue(Boolean.TRUE);

		clearFields();

		initState(Type.ITEM, itemModel, false);

		if (itemModel != null) {
			if (itemModel.getCategoryId() != null) {
				List<ItemModel> models = treeStore.findModels(ItemModel.Key.ID.name(), String.valueOf(itemModel.getCategoryId()));
				for (ItemModel category : models) {
					if (category.getItemType() == Type.CATEGORY)
						categoryPicker.setValue(category);
				}
			}
		}
		establishSelectedCategoryState(itemModel);

		includedField.setValue(Boolean.TRUE);
		nameField.focus();
		addListeners();
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
			case LETTERS:
				model.set("name", i18n.gradeTypeLetters());
				model.set("value", GradebookModel.GradeType.LETTERS);
				break;
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

		boolean isAllowedToEdit = DataTypeConversionUtil.checkBoolean(selectedGradebook.isUserAbleToEditAssessments());
		boolean hasCategories = categoryType != CategoryType.NO_CATEGORIES;
		boolean hasWeights = categoryType == CategoryType.WEIGHTED_CATEGORIES;
		boolean isNotGradebook = itemType != Type.GRADEBOOK;
		boolean isCategory = itemType == Type.CATEGORY;
		boolean isItem = itemType == Type.ITEM;
		boolean isExternal = false;
		boolean isCreateNewItem = createItemType == Type.ITEM && mode == Mode.NEW;

		boolean isPercentCategoryVisible = false;

		formPanel.clear();

		boolean isEditable = true;
		boolean isEqualWeight = false;
		boolean isExtraCredit = false;
		boolean isDropLowestVisible = isEditable && isCategory;
		
		if (itemModel != null) {
			isExtraCredit = DataTypeConversionUtil.checkBoolean(itemModel.getExtraCredit());
			isEditable = itemModel.isEditable();
			String source = itemModel.get(ItemModel.Key.SOURCE.name());
			isExternal = source != null && source.trim().length() > 0;
			ItemModel category = null;
			switch (itemModel.getItemType()) {
				case GRADEBOOK:
					isPercentCategoryVisible = false;
					break;
				case CATEGORY:
					category = itemModel;
					break;
				case ITEM:
					category = itemModel.getParent();
					break;
				default:
					isPercentCategoryVisible = (hasWeights && isExtraCredit) && isItem;
			}

			isEqualWeight = category == null ? false : DataTypeConversionUtil.checkBoolean(category.getEqualWeightAssignments());
			isPercentCategoryVisible = hasWeights && (!isEqualWeight || isExtraCredit) && isItem;
			isDropLowestVisible = isDropLowestVisible && !isExtraCredit; 
			
			if (isDropLowestVisible && category != null && !hasWeights && hasCategories) {
				if (category.getChildCount() > 0) {
					Double points = null;
					for (int i=0;i<category.getChildCount();i++) {
						ItemModel item = category.getChild(i);
						if (!DataTypeConversionUtil.checkBoolean(item.getExtraCredit())) {
							if (points == null)
								points = item.getPoints();
							else if (!points.equals(item.getPoints())) {
								isDropLowestVisible = false;
								break;
							}
						}
					}
				}
			}
		} else {
			isPercentCategoryVisible = hasWeights && isItem;
		}
		
		

		initField(nameField, isAllowedToEdit && isEditable && !isDelete && !isExternal, true);
		initField(pointsField, isAllowedToEdit && !isDelete && !isExternal, isEditable && isItem);
		initField(percentCategoryField, isAllowedToEdit && !isDelete && (isItem || isCreateNewItem), isEditable && isPercentCategoryVisible);
		initField(percentCourseGradeField, isAllowedToEdit && !isDelete, isEditable && isCategory && hasWeights);
		initField(equallyWeightChildrenField, isAllowedToEdit && !isDelete, isEditable && isCategory && hasWeights);
		initField(extraCreditField, isAllowedToEdit && !isDelete, isEditable && isNotGradebook);
		initField(dropLowestField, isAllowedToEdit && !isDelete, isDropLowestVisible);
		initField(dueDateField, isAllowedToEdit && !isDelete && !isExternal, isEditable && isItem);
		initField(includedField, isAllowedToEdit && !isDelete, isEditable && isNotGradebook);
		initField(releasedField, isAllowedToEdit && !isDelete, isEditable && isItem);
		initField(releaseGradesField, isAllowedToEdit && !isDelete, isEditable && !isNotGradebook);
		initField(releaseItemsField, isAllowedToEdit && !isDelete, isEditable && !isNotGradebook);
		initField(categoryPicker, isAllowedToEdit && !isDelete, isEditable && hasCategories && isItem);
		initField(categoryTypePicker, isAllowedToEdit, isEditable && !isNotGradebook);
		initField(gradeTypePicker, isAllowedToEdit, isEditable && !isNotGradebook);
		initField(sourceField, false, isEditable && isItem);
		initField(scaledExtraCreditField, !isDelete && isAllowedToEdit, !isNotGradebook);
	}

	private void initField(Field field, boolean isEnabled, boolean isVisible) {

		field.setEnabled(isEnabled);
		field.setVisible(isVisible);
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
								public void updateModel() {
									final Object val = onConvertFieldValue(field.getValue());
									if (store != null) {
										
											Record r = store.getRecord(model);
											if (r != null) {
												if (!r.isEditing())
													r.beginEdit();
	
												r.setValid(property, field
														.isValid());
												r.set(property, val);
											}
										
									} //else {
									//	model.set(property, val);
									//}

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

											return (ItemModel)store.findModel(ItemModel.Key.ID.name(), String.valueOf(categoryId));
										}

										return null;
									}
								});
							} else if (name.equals(ItemModel.Key.CATEGORYTYPE.name()) ||
									name.equals(ItemModel.Key.GRADETYPE.name())) {
								b.setConvertor(new Converter() {
									public Object convertFieldValue(Object value) {
										if (value instanceof ModelData && ((ModelData)value).get("value") != null) {
											return ((ModelData)value).get("value");
										}

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

	private void addListeners() {
		categoryPicker.addSelectionChangedListener(categorySelectionChangedListener);
		categoryTypePicker.addSelectionChangedListener(otherSelectionChangedListener);
		gradeTypePicker.addSelectionChangedListener(otherSelectionChangedListener);
		releaseGradesField.addListener(Events.Change, checkboxChangeListener);
		releaseItemsField.addListener(Events.Change, checkboxChangeListener);
		percentCourseGradeField.addKeyListener(keyListener);
		percentCategoryField.addKeyListener(keyListener);
		pointsField.addKeyListener(keyListener);
		dropLowestField.addKeyListener(keyListener);
		dueDateField.addKeyListener(keyListener);
		includedField.addListener(Events.Change, checkboxChangeListener);
		extraCreditField.addListener(Events.Change, extraCreditChangeListener);
		equallyWeightChildrenField.addListener(Events.Change, checkboxChangeListener);
		releasedField.addListener(Events.Change, checkboxChangeListener);
		scaledExtraCreditField.addListener(Events.Change, checkboxChangeListener);
	}

	private void removeListeners() {
		categoryPicker.removeListener(Events.SelectionChange, categorySelectionChangedListener);
		categoryTypePicker.removeListener(Events.SelectionChange, otherSelectionChangedListener);
		gradeTypePicker.removeListener(Events.SelectionChange, otherSelectionChangedListener);
		releaseGradesField.removeListener(Events.Change, checkboxChangeListener);
		releaseItemsField.removeListener(Events.Change, checkboxChangeListener);
		percentCourseGradeField.removeKeyListener(keyListener);
		percentCategoryField.removeKeyListener(keyListener);
		pointsField.removeKeyListener(keyListener);
		dropLowestField.removeKeyListener(keyListener);
		dueDateField.removeKeyListener(keyListener);
		includedField.removeListener(Events.Change, checkboxChangeListener);
		extraCreditField.removeListener(Events.Change, extraCreditChangeListener);
		equallyWeightChildrenField.removeListener(Events.Change, checkboxChangeListener);
		releasedField.removeListener(Events.Change, checkboxChangeListener);
		scaledExtraCreditField.removeListener(Events.Change, checkboxChangeListener);
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

		otherSelectionChangedListener = new SelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent se) {

			}

			public void handleEvent(BaseEvent be) {
				setChanges();
			}

		};

		keyListener = new KeyListener() {

			public void componentKeyPress(ComponentEvent event) {
				setChanges();
			}

		};

		checkboxChangeListener = new Listener<FieldEvent>() {

			public void handleEvent(FieldEvent fe) {
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
				setChanges();

				if (selectedItemModel != null) {
					switch (selectedItemModel.getItemType()) {
						case CATEGORY:
							initField(dropLowestField, !isDelete, !DataTypeConversionUtil.checkBoolean(isChecked));
						break;
						case ITEM:
							initField(percentCategoryField, !isDelete, hasWeights 
									&& (!DataTypeConversionUtil.checkBoolean(isEqualWeight) 
											|| (isChecked != null && isChecked.booleanValue())));
						break;
					}
				} else if (createItemType == Type.ITEM) {
					initField(percentCategoryField, !isDelete, hasWeights 
							&& (!DataTypeConversionUtil.checkBoolean(isEqualWeight) || (isChecked != null && isChecked.booleanValue())));
				} else if (createItemType == Type.CATEGORY) {
					initField(dropLowestField, !isDelete, !DataTypeConversionUtil.checkBoolean(isChecked));
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
						Record record = null;

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
								else {
									String categoryName = categoryPicker.getRawValue();
									item.setCategoryName(categoryName);
								}

								Integer dropLowest = dropLowestField.getValue() == null ? null : Integer.valueOf(dropLowestField.getValue().intValue());

								item.setName(nameField.getValue());
								item.setExtraCredit(extraCreditField.getValue());
								item.setEqualWeightAssignments(equallyWeightChildrenField.getValue());
								item.setIncluded(includedField.getValue());
								item.setReleased(releasedField.getValue());
								item.setPercentCourseGrade((Double)percentCourseGradeField.getValue());
								item.setPercentCategory((Double)percentCategoryField.getValue());
								item.setPoints((Double)pointsField.getValue());
								item.setDueDate(dueDateField.getValue());
								item.setDropLowest(dropLowest);
								item.setItemType(createItemType);

								clearChanges();

								Dispatcher.forwardEvent(GradebookEvents.CreateItem.getEventType(), new ItemCreate(treeStore, item, close));
								break;
							case DELETE:
								Dispatcher.forwardEvent(GradebookEvents.HideFormPanel.getEventType(), Boolean.FALSE);
								Dispatcher.forwardEvent(GradebookEvents.DeleteItem.getEventType(), new ItemUpdate(treeStore, selectedItemModel, ItemModel.Key.REMOVED.name(), Boolean.FALSE, Boolean.TRUE));
								break;
							case CANCEL:
								clearActiveRecord();
								Dispatcher.forwardEvent(GradebookEvents.HideFormPanel.getEventType(), Boolean.FALSE);
								break;
							case SAVECLOSE:
								close = true;
							case SAVE:
								if (selectedItemModel != null) 
									record = treeStore.getRecord(selectedItemModel);
								if (nameField.validate() 
										&& (!percentCategoryField.isVisible() || percentCategoryField.validate()) 
										&& (!percentCourseGradeField.isVisible() || percentCourseGradeField.validate())
										&& (!pointsField.isVisible() || pointsField.validate())) {

									if (record != null) {
									
										Map<String, Object> changes = record.getChanges();
										
										
										if (changes != null 
												&& changes.get(ItemModel.Key.POINTS.name()) != null
												&& !changes.get(ItemModel.Key.POINTS.name())
												.equals(selectedItemModel.get(ItemModel.Key.POINTS.name()))) {
											
											Listener<WindowEvent> listener = new Listener<WindowEvent>() {
	
												public void handleEvent(WindowEvent be) {
													Dialog dialog = (Dialog) be.component;  
													Button btn = dialog.getButtonPressed();
													
													Record r = treeStore.getRecord(selectedItemModel);
													if (r != null) {
														if (!r.isEditing())
															r.beginEdit();
													
														if (btn.getItemId().equals(Dialog.CANCEL)) {
															return;
														} else if (btn.getItemId().equals(Dialog.YES)) {
															r.set(ItemModel.Key.DO_RECALCULATE_POINTS.name(), Boolean.TRUE);
														} else {
															r.set(ItemModel.Key.DO_RECALCULATE_POINTS.name(), Boolean.FALSE);
														}
														
														Dispatcher.forwardEvent(GradebookEvents.UpdateItem.getEventType(), new ItemUpdate(treeStore, r, selectedItemModel, true));
													}
													
												}
											};
											
											MessageBox box = new MessageBox();
										    box.setTitle(i18n.doRecalculatePointsTitle());
										    box.setMessage(i18n.doRecalculatePointsMessage());
										    box.addCallback(listener);
										    box.setIcon(MessageBox.QUESTION);
										    box.setButtons(MessageBox.YESNOCANCEL);
										    box.show();
										    
										    return;
										}
									}
									
									Dispatcher.forwardEvent(GradebookEvents.UpdateItem.getEventType(), new ItemUpdate(treeStore, record, selectedItemModel, close));
								}
								break;
						}
					}
				}

			}

		};

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
					categoryPicker.select(itemModel);
					break;
				case ITEM:
					category = itemModel.getParent();
					if (category != null && category.getItemType() == Type.CATEGORY)
						categoryPicker.select(category);
					break;
			}
		} 
	}

	private void refreshSelectedCategoryState(ItemModel itemModel) {
		CategoryType categoryType = selectedGradebook.getGradebookItemModel().getCategoryType();

		boolean hasWeights = categoryType == CategoryType.WEIGHTED_CATEGORIES;
		boolean isPercentCategoryVisible = false;
		boolean isItem = selectedItemModel != null && selectedItemModel.getItemType() == Type.ITEM;
		boolean isCreateNewItem = createItemType == Type.ITEM && mode == Mode.NEW;

		if (itemModel != null) {

			ItemModel category = null;
			switch (itemModel.getItemType()) {
				case CATEGORY:
					category = itemModel;
					break;
				case ITEM:
					category = itemModel.getParent();
					break;
			}

			if (category != null && category.getItemType() == Type.CATEGORY)
				isPercentCategoryVisible = hasWeights && 
				(!DataTypeConversionUtil.checkBoolean(category.getEqualWeightAssignments()) || 
						(isItem && DataTypeConversionUtil.checkBoolean(extraCreditField.getValue())));
		}


		initField(percentCategoryField, !isDelete && (isItem || isCreateNewItem), isPercentCategoryVisible);
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

	private void clearActiveRecord() {
		Record record = null;
		if (selectedItemModel != null) {
			record = treeStore.getRecord(selectedItemModel);
		}

		if (record != null && record.isEditing()) {
			record.reject(false);
			record = null;
		}
	}

	private void clearFields() {

	}

}
