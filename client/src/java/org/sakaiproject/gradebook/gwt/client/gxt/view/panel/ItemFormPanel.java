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

import org.sakaiproject.gradebook.gwt.client.AppConstants;
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
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.ItemFormComboBox;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.NullSensitiveCheckBox;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModelComparer;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binding.Converter;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BindingEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DatePickerEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;

public class ItemFormPanel extends GradebookPanel {

	
	private enum Mode { DELETE, EDIT, NEW };
	private enum SelectionType { CLOSE, CREATE, CREATECLOSE, CANCEL, REQUEST_DELETE, DELETE, SAVE, SAVECLOSE };

	private static final String selectionTypeField = "selectionType";
	private static final String NAME_DISPLAY_FIELD = "name", VALUE_DISPLAY_FIELD = "value";
	
	
	private FormPanel formPanel;
	private FormBinding formBindings;

	private LabelField directionsField;
	private TextField<String> nameField;
	private ComboBox<ModelData> categoryTypePicker, gradeTypePicker;
	private ComboBox<ItemModel> categoryPicker;
	private CheckBox includedField, extraCreditField, equallyWeightChildrenField, releasedField;
	private CheckBox nullsAsZerosField, releaseGradesField, releaseItemsField, scaledExtraCreditField;
	private CheckBox enforcePointWeightingField, showMeanField, showMedianField, showModeField;
	private CheckBox showRankField, showItemStatsField;
	private NumberField percentCourseGradeField, percentCategoryField, pointsField, dropLowestField;
	private DateField dueDateField;
	private TextField<String> sourceField;

	private FieldSet displayToStudentFieldSet;
	
	private ListStore<ItemModel> categoryStore;
	private TreeStore<ItemModel> treeStore;
	private ListStore<ModelData> gradeTypeStore;

	private KeyListener keyListener;
	private Listener<BindingEvent> bindListener;
	private Listener<DatePickerEvent> datePickerListener;
	private Listener<FieldEvent> extraCreditChangeListener, checkboxChangeListener, enforcePointWeightingListener;
	private SelectionListener<ButtonEvent> selectionListener;
	private SelectionChangedListener<ItemModel> categorySelectionChangedListener;
	private SelectionChangedListener<ModelData> otherSelectionChangedListener;

	private RowData topRowData, bottomRowData;
	private Button deleteButton, okButton, okCloseButton, cancelButton;

	private GradebookModel selectedGradebook;
	private ItemModel selectedItemModel;
	private Type createItemType;

	private boolean isListeningEnabled;
	private boolean isDelete;
	private boolean hasChanges;

	private Mode mode;

	public ItemFormPanel() {
		super();
		this.isListeningEnabled = true;
		setHeaderVisible(true);
		setFrame(true);
		setScrollMode(Scroll.AUTO);
		setButtonAlign(Style.HorizontalAlignment.LEFT);
		setLayout(new FlowLayout());

		initListeners();
		
		categoryStore = new ListStore<ItemModel>();
		categoryStore.setModelComparer(new ItemModelComparer<ItemModel>());
		
		deleteButton = new AriaButton(i18n.deleteButton(), selectionListener, 'd');
		deleteButton.setData(selectionTypeField, SelectionType.REQUEST_DELETE);
		addButton(deleteButton);
		
		getButtonBar().add(new FillToolItem());
		
		okButton = new AriaButton("", selectionListener, 's');
		addButton(okButton);

		okCloseButton = new AriaButton(i18n.saveAndCloseButton(), selectionListener, 'm');
		addButton(okCloseButton);

		cancelButton = new AriaButton(i18n.closeButton(), selectionListener, 'x');
		cancelButton.setData(selectionTypeField, SelectionType.CANCEL);

		addButton(cancelButton);
	    
	    ListStore<ModelData> categoryTypeStore = new ListStore<ModelData>();
		categoryTypeStore.add(getCategoryTypeModel(GradebookModel.CategoryType.NO_CATEGORIES));
		categoryTypeStore.add(getCategoryTypeModel(GradebookModel.CategoryType.SIMPLE_CATEGORIES));
		categoryTypeStore.add(getCategoryTypeModel(GradebookModel.CategoryType.WEIGHTED_CATEGORIES));
	    
		formPanel = new FormPanel();
		formPanel.setHeaderVisible(false);
		formPanel.setLabelWidth(180);
		formPanel.setVisible(false);

		directionsField = new LabelField();
		directionsField.setName("directions");

		nameField = new InlineEditField<String>();
		nameField.setAllowBlank(false);
		nameField.setName(ItemModel.Key.NAME.name());
		nameField.setFieldLabel(i18n.nameFieldLabel());
		nameField.addKeyListener(keyListener);

		formPanel.add(nameField);

		categoryPicker = new ItemFormComboBox<ItemModel>(ItemModel.Key.NAME.name(), ItemModel.Key.CATEGORY_ID.name(), i18n.categoryName());
		categoryPicker.addKeyListener(keyListener);
		categoryPicker.setStore(categoryStore);
		formPanel.add(categoryPicker);

		categoryTypePicker = new ItemFormComboBox<ModelData>(NAME_DISPLAY_FIELD, ItemModel.Key.CATEGORYTYPE.name(), i18n.categoryTypeFieldLabel());
		categoryTypePicker.setStore(categoryTypeStore);
		formPanel.add(categoryTypePicker);

		gradeTypePicker = new ItemFormComboBox<ModelData>(NAME_DISPLAY_FIELD, ItemModel.Key.GRADETYPE.name(), i18n.gradeTypeFieldLabel());
		formPanel.add(gradeTypePicker);

		scaledExtraCreditField = new NullSensitiveCheckBox();
		scaledExtraCreditField.setName(ItemModel.Key.EXTRA_CREDIT_SCALED.name());
		scaledExtraCreditField.setFieldLabel(i18n.scaledExtraCreditFieldLabel());
		scaledExtraCreditField.setVisible(false);
		scaledExtraCreditField.setToolTip(newToolTipConfig(i18n.scaledExtraCreditToolTip()));
		formPanel.add(scaledExtraCreditField);
		
		final LayoutContainer main = new LayoutContainer();
		main.setLayout(new ColumnLayout());
		
		displayToStudentFieldSet = new FieldSet();  
		displayToStudentFieldSet.setHeading(i18n.displayToStudentsHeading());  
		displayToStudentFieldSet.setCheckboxToggle(false);  
		displayToStudentFieldSet.setLayout(new FlowLayout());
		displayToStudentFieldSet.setAutoHeight(true);
		displayToStudentFieldSet.setScrollMode(Scroll.AUTO);
		displayToStudentFieldSet.setVisible(false);

		main.setWidth(400);
		
		FormLayout leftLayout = new FormLayout(); 
		leftLayout.setLabelWidth(140);
		leftLayout.setDefaultWidth(100);
		
		LayoutContainer left = new LayoutContainer();
		left.setLayout(leftLayout);
		
		FormLayout rightLayout = new FormLayout(); 
		rightLayout.setLabelWidth(140);
		rightLayout.setDefaultWidth(50);
		
		LayoutContainer right = new LayoutContainer();
		right.setLayout(rightLayout);
		
		releaseGradesField = new NullSensitiveCheckBox();
		releaseGradesField.setName(ItemModel.Key.RELEASEGRADES.name());
		releaseGradesField.setFieldLabel(i18n.releaseGradesFieldLabel());
		releaseGradesField.setVisible(false);
		releaseGradesField.setToolTip(newToolTipConfig(i18n.releaseGradesToolTip()));
		left.add(releaseGradesField);

		releaseItemsField = new NullSensitiveCheckBox();
		releaseItemsField.setName(ItemModel.Key.RELEASEITEMS.name());
		releaseItemsField.setFieldLabel(i18n.releaseItemsFieldLabel());
		releaseItemsField.setVisible(false);
		releaseItemsField.setToolTip(newToolTipConfig(i18n.releaseItemsToolTip()));
		left.add(releaseItemsField);
		
		showMeanField = new NullSensitiveCheckBox();
		showMeanField.setName(ItemModel.Key.SHOWMEAN.name());
		showMeanField.setFieldLabel(i18n.showMeanFieldLabel());
		showMeanField.setVisible(false);
		showMeanField.setToolTip(newToolTipConfig(i18n.showMeanToolTip()));
		left.add(showMeanField);
		
		showMedianField = new NullSensitiveCheckBox();
		showMedianField.setName(ItemModel.Key.SHOWMEDIAN.name());
		showMedianField.setFieldLabel(i18n.showMedianFieldLabel());
		showMedianField.setVisible(false);
		showMedianField.setToolTip(newToolTipConfig(i18n.showMedianToolTip()));
		left.add(showMedianField);
		
		showModeField = new NullSensitiveCheckBox();
		showModeField.setName(ItemModel.Key.SHOWMODE.name());
		showModeField.setFieldLabel(i18n.showModeFieldLabel());
		showModeField.setVisible(false);
		showModeField.setToolTip(newToolTipConfig(i18n.showModeToolTip()));
		right.add(showModeField);
		
		showRankField = new NullSensitiveCheckBox();
		showRankField.setName(ItemModel.Key.SHOWRANK.name());
		showRankField.setFieldLabel(i18n.showRankFieldLabel());
		showRankField.setVisible(false);
		showRankField.setToolTip(newToolTipConfig(i18n.showRankToolTip()));
		right.add(showRankField);
		
		showItemStatsField = new NullSensitiveCheckBox();
		showItemStatsField.setName(ItemModel.Key.SHOWITEMSTATS.name());
		showItemStatsField.setFieldLabel(i18n.showItemStatsFieldLabel());
		showItemStatsField.setVisible(false);
		showItemStatsField.setToolTip(newToolTipConfig(i18n.showItemStatsToolTip()));
		right.add(showItemStatsField);
		
		main.add(left, new ColumnData(.5));
		main.add(right, new ColumnData(.5));
		
		displayToStudentFieldSet.add(main);
		
		formPanel.add(displayToStudentFieldSet);

		percentCourseGradeField = new InlineEditNumberField();
		percentCourseGradeField.setName(ItemModel.Key.PERCENT_COURSE_GRADE.name());
		percentCourseGradeField.setFieldLabel(i18n.percentCourseGradeFieldLabel());
		percentCourseGradeField.setFormat(DataTypeConversionUtil.getLongNumberFormat());
		percentCourseGradeField.setAllowDecimals(true);
		percentCourseGradeField.setMinValue(Double.valueOf(0.000000d));
		percentCourseGradeField.setMaxValue(Double.valueOf(100.000000d));
		percentCourseGradeField.setVisible(false);
		percentCourseGradeField.setToolTip(newToolTipConfig(i18n.percentCourseGradeToolTip()));
		formPanel.add(percentCourseGradeField);

		percentCategoryField = new InlineEditNumberField();
		percentCategoryField.setName(ItemModel.Key.PERCENT_CATEGORY.name());
		percentCategoryField.setFieldLabel(i18n.percentCategoryFieldLabel());
		percentCategoryField.setFormat(DataTypeConversionUtil.getLongNumberFormat());
		percentCategoryField.setAllowDecimals(true);
		percentCategoryField.setMinValue(Double.valueOf(0.000000d));
		percentCategoryField.setMaxValue(Double.valueOf(100.000000d));
		percentCategoryField.setVisible(false);
		percentCategoryField.setToolTip(newToolTipConfig(i18n.percentCategoryToolTip()));
		formPanel.add(percentCategoryField);

		pointsField = new InlineEditNumberField();
		pointsField.setName(ItemModel.Key.POINTS.name());
		pointsField.setEmptyText(i18n.pointsFieldEmptyText());
		pointsField.setFieldLabel(i18n.pointsFieldLabel());
		pointsField.setFormat(DataTypeConversionUtil.getDefaultNumberFormat());
		pointsField.setAllowDecimals(true);
		pointsField.setMinValue(Double.valueOf(0.0001d));
		pointsField.setVisible(false);
		formPanel.add(pointsField);
		
		dropLowestField = new InlineEditNumberField();
		dropLowestField.setEmptyText("0");
		dropLowestField.setName(ItemModel.Key.DROP_LOWEST.name());
		dropLowestField.setFieldLabel(i18n.dropLowestFieldLabel());
		dropLowestField.setAllowDecimals(false);
		dropLowestField.setPropertyEditorType(Integer.class);
		dropLowestField.setVisible(false);
		dropLowestField.setToolTip(i18n.dropLowestToolTip());
		formPanel.add(dropLowestField);

		dueDateField = new DateField();
		dueDateField.setName(ItemModel.Key.DUE_DATE.name());
		dueDateField.setFieldLabel(i18n.dueDateFieldLabel());
		dueDateField.setVisible(false);
		dueDateField.setEmptyText(i18n.dueDateEmptyText());
		formPanel.add(dueDateField);

		sourceField = new TextField<String>();
		sourceField.setName(ItemModel.Key.SOURCE.name());
		sourceField.setFieldLabel(i18n.sourceFieldLabel());
		sourceField.setEnabled(false);
		sourceField.setEmptyText("Gradebook");
		sourceField.setVisible(false);
		formPanel.add(sourceField);

		includedField = new NullSensitiveCheckBox();
		includedField.setName(ItemModel.Key.INCLUDED.name());
		includedField.setFieldLabel(i18n.includedFieldLabel());
		includedField.setVisible(false);
		includedField.setToolTip(newToolTipConfig(i18n.includedToolTip()));
		formPanel.add(includedField);

		extraCreditField = new NullSensitiveCheckBox();
		extraCreditField.setName(ItemModel.Key.EXTRA_CREDIT.name());
		extraCreditField.setFieldLabel(i18n.extraCreditFieldLabel());
		extraCreditField.setVisible(false);
		extraCreditField.setToolTip(newToolTipConfig(i18n.extraCreditToolTip()));
		formPanel.add(extraCreditField);

		equallyWeightChildrenField = new NullSensitiveCheckBox();
		equallyWeightChildrenField.setName(ItemModel.Key.EQUAL_WEIGHT.name());
		equallyWeightChildrenField.setFieldLabel(i18n.equallyWeightChildrenFieldLabel());
		equallyWeightChildrenField.setVisible(false);
		equallyWeightChildrenField.setToolTip(newToolTipConfig(i18n.equallyWeightChildrenToolTip()));
		formPanel.add(equallyWeightChildrenField);

		releasedField = new NullSensitiveCheckBox();
		releasedField.setName(ItemModel.Key.RELEASED.name());
		releasedField.setFieldLabel(i18n.releasedFieldLabel());
		releasedField.setVisible(false);
		releasedField.setToolTip(newToolTipConfig(i18n.releasedToolTip()));
		formPanel.add(releasedField);
		
		nullsAsZerosField = new NullSensitiveCheckBox();
		nullsAsZerosField.setName(ItemModel.Key.NULLSASZEROS.name());
		nullsAsZerosField.setFieldLabel(i18n.nullsAsZerosFieldLabel());
		nullsAsZerosField.setVisible(false);
		nullsAsZerosField.setToolTip(newToolTipConfig(i18n.nullsAsZerosToolTip()));
		formPanel.add(nullsAsZerosField);
		
		enforcePointWeightingField = new NullSensitiveCheckBox();
		enforcePointWeightingField.setName(ItemModel.Key.ENFORCE_POINT_WEIGHTING.name());
		enforcePointWeightingField.setFieldLabel(i18n.enforcePointWeightingFieldLabel());
		enforcePointWeightingField.setVisible(false);
		enforcePointWeightingField.setToolTip(newToolTipConfig(i18n.enforcePointWeightingToolTip()));
		formPanel.add(enforcePointWeightingField);

		topRowData = new RowData(1, 70, new Margins(10));
		bottomRowData = new RowData(1, 1, new Margins(0, 0, 5, 0));
		add(directionsField, topRowData);
		add(formPanel, bottomRowData);
		
	}

	public void onRequestDeleteItem(final ItemModel itemModel) {
		if (hasChanges) {
			MessageBox.confirm(i18n.hasChangesTitle(), i18n.hasChangesMessage(), new Listener<MessageBoxEvent>() {

				public void handleEvent(MessageBoxEvent be) {
					Button button = be.getButtonClicked();
				
					if (button.getText().equals("Yes"))
						doConfirmDeleteItem(itemModel);
				}
				
			});
		} else {
			doConfirmDeleteItem(itemModel);
		}
	}
	
	private void doConfirmDeleteItem(ItemModel itemModel) {
		removeListeners();
		this.mode = Mode.DELETE;
		this.createItemType = null;
		this.selectedItemModel = itemModel;

		if (formBindings == null)
			initFormBindings();

		deleteButton.setVisible(false);
		okButton.setText(i18n.deleteButton());
		okButton.setData(selectionTypeField, SelectionType.DELETE);
		okCloseButton.setVisible(false);

		if (itemModel != null) {
			clearChanges();
			
			Type itemType = itemModel.getItemType();
			initState(itemType, itemModel, true);
			directionsField.setHeight(80);
			directionsField.setText(i18n.directionsConfirmDeleteItem());
			directionsField.setStyleName(resources.css().gbWarning());
			directionsField.setVisible(true);

			formBindings.bind(itemModel);

			Dispatcher.forwardEvent(GradebookEvents.ExpandEastPanel.getEventType(), AppView.EastCard.DELETE_ITEM);
		}
	}

	public void onEditItem(final ItemModel itemModel, final boolean expand) {

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
		
		if (hasChanges) {
			MessageBox.confirm(i18n.hasChangesTitle(), i18n.hasChangesMessage(), new Listener<MessageBoxEvent>() {

				public void handleEvent(MessageBoxEvent be) {
					Button button = be.getButtonClicked();
				
					if (button.getText().equals("Yes"))
						doEditItem(itemModel, expand);
				}
				
			});
		} else {
			doEditItem(itemModel, expand);
		}
	}
	
	private void doEditItemButtons(ItemModel itemModel) {
		boolean isAllowedToEdit = DataTypeConversionUtil.checkBoolean(selectedGradebook.isUserAbleToEditAssessments());

		deleteButton.setVisible(itemModel.getItemType() != Type.GRADEBOOK);
		okButton.setText(i18n.saveButton());
		okButton.setData(selectionTypeField, SelectionType.SAVE);
		okButton.setVisible(isAllowedToEdit && itemModel != null && itemModel.isEditable());
		okCloseButton.setVisible(isAllowedToEdit && itemModel != null && itemModel.isEditable());
		okCloseButton.setData(selectionTypeField, SelectionType.SAVECLOSE);
		okCloseButton.setText(i18n.saveAndCloseButton());
	}
	
	private void doEditItem(ItemModel itemModel, boolean expand) {
		removeListeners();
		formPanel.hide();

		this.mode = Mode.EDIT;
		this.createItemType = null;
		this.selectedItemModel = itemModel;
		this.directionsField.setText("");
		this.directionsField.setVisible(false);

		if (formBindings == null) {
			initFormBindings();
		} else {
			formBindings.unbind();
		}

		if (itemModel != null) {
			Type itemType = itemModel.getItemType();
			clearChanges();
			formBindings.addListener(Events.Bind, bindListener);
			formBindings.bind(itemModel);
			initState(itemType, itemModel, false);
		} else {
			formBindings.addListener(Events.UnBind, bindListener);
			formBindings.unbind();
		}

		doEditItemButtons(selectedItemModel);
		
		formPanel.show();
		nameField.focus();
	}

	public void onItemCreated(ItemModel itemModel) {

		ItemModelProcessor processor = new ItemModelProcessor(itemModel) {

			public void doCategory(ItemModel categoryModel) {
				categoryStore.add(categoryModel);
			}

			public void doItem(ItemModel itemModel) {
			
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

		for (ModelData gradebook : rootItemModel.getChildren()) {
			for (ModelData m : ((BaseTreeModel) gradebook).getChildren()) {
				ItemModel category = (ItemModel)m;
				// Ensure that we're dealing with a category
				if (category.getItemType() == Type.CATEGORY) {
					categoryStore.add(category);
				}

			}
		}

	}

	public void onNewCategory(final ItemModel itemModel) {
		if (hasChanges) {
			MessageBox.confirm(i18n.hasChangesTitle(), i18n.hasChangesMessage(), new Listener<MessageBoxEvent>() {

				public void handleEvent(MessageBoxEvent be) {
					Button button = be.getButtonClicked();
				
					if (button.getText().equals("Yes"))
						doNewCategory(itemModel);
				}
				
			});
		} else {
			doNewCategory(itemModel);
		}
	}
	
	private void doNewCategory(ItemModel itemModel) {
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

		deleteButton.setVisible(false);
		okButton.setText(i18n.createButton());
		okButton.setData(selectionTypeField, SelectionType.CREATE);
		okCloseButton.setVisible(true);
		okCloseButton.setData(selectionTypeField, SelectionType.CREATECLOSE);
		okCloseButton.setText(i18n.createAndCloseButton());

		clearFields();
		clearChanges();

		initState(Type.CATEGORY, itemModel, false);
		establishSelectedCategoryState(itemModel);

		includedField.setValue(Boolean.TRUE);
		nameField.focus();
		addListeners();
	}

	public void onNewItem(final ItemModel itemModel) {
		
		if (hasChanges) {
			MessageBox.confirm(i18n.hasChangesTitle(), i18n.hasChangesMessage(), new Listener<MessageBoxEvent>() {

				public void handleEvent(MessageBoxEvent be) {
					Button button = be.getButtonClicked();
				
					if (button.getText().equals("Yes"))
						doNewItem(itemModel);
				}
				
			});
		} else {
			doNewItem(itemModel);
		}
		
	}
	
	private void doNewItem(ItemModel itemModel) {
		formPanel.hide();
		removeListeners();
		this.mode = Mode.NEW;

		this.directionsField.setText("");
		this.directionsField.setVisible(false);
		this.createItemType = Type.ITEM;
		this.selectedItemModel = null;

		clearActiveRecord();

		if (formBindings != null) 
			formBindings.unbind();

		deleteButton.setVisible(false);
		okButton.setText(i18n.createButton());
		okButton.setData(selectionTypeField, SelectionType.CREATE);
		okCloseButton.setVisible(true);
		okCloseButton.setData(selectionTypeField, SelectionType.CREATECLOSE);
		okCloseButton.setText(i18n.createAndCloseButton());

		includedField.setValue(Boolean.TRUE);

		clearFields();
		clearChanges();

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
		formPanel.show();
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
		
		if (gradeTypeStore == null) {
			gradeTypeStore = new ListStore<ModelData>();
	
			List<GradeType> enabledGradeTypes = Registry.get(AppConstants.ENABLED_GRADE_TYPES);
			
			if (enabledGradeTypes != null) {
				for (int i=0;i<enabledGradeTypes.size();i++) {
					gradeTypeStore.add(getGradeTypeModel(enabledGradeTypes.get(i)));
				}
			}
			
			gradeTypePicker.setStore(gradeTypeStore);
		}
	}

	private ModelData getCategoryTypeModel(CategoryType categoryType) {
		ModelData model = new BaseModelData();

		// Initialize type picker
		switch (categoryType) {
			case NO_CATEGORIES:
				model.set(NAME_DISPLAY_FIELD, i18n.orgTypeNoCategories());
				model.set(VALUE_DISPLAY_FIELD, GradebookModel.CategoryType.NO_CATEGORIES);
				break;
			case SIMPLE_CATEGORIES:
				model.set(NAME_DISPLAY_FIELD, i18n.orgTypeCategories());
				model.set(VALUE_DISPLAY_FIELD, GradebookModel.CategoryType.SIMPLE_CATEGORIES);
				break;	
			case WEIGHTED_CATEGORIES:
				model.set(NAME_DISPLAY_FIELD, i18n.orgTypeWeightedCategories());
				model.set(VALUE_DISPLAY_FIELD, GradebookModel.CategoryType.WEIGHTED_CATEGORIES);
				break;	
		}

		return model;
	}

	private ModelData getGradeTypeModel(GradeType gradeType) {
		ModelData model = new BaseModelData();

		switch (gradeType) {
			case LETTERS:
				model.set(NAME_DISPLAY_FIELD, i18n.gradeTypeLetters());
				model.set(VALUE_DISPLAY_FIELD, GradebookModel.GradeType.LETTERS);
				break;
			case POINTS:
				model.set(NAME_DISPLAY_FIELD, i18n.gradeTypePoints());
				model.set(VALUE_DISPLAY_FIELD, GradebookModel.GradeType.POINTS);
				break;
			case PERCENTAGES:
				model.set(NAME_DISPLAY_FIELD, i18n.gradeTypePercentages());
				model.set(VALUE_DISPLAY_FIELD, GradebookModel.GradeType.PERCENTAGES);
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
		boolean isWeightByPointsVisible = false;
		
		formPanel.clear();

		boolean isEditable = true;
		boolean isEqualWeight = false;
		boolean isExtraCredit = false;
		boolean isDropLowestVisible = isEditable && isCategory;
		boolean isWeightByPoints = false;
		
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
					category = (ItemModel) itemModel.getParent();
					break;
				default:
					isPercentCategoryVisible = (hasWeights && isExtraCredit) && isItem;
			}

			isWeightByPoints = category == null ? false : DataTypeConversionUtil.checkBoolean(category.getEnforcePointWeighting());
			isEqualWeight = category == null ? false : DataTypeConversionUtil.checkBoolean(category.getEqualWeightAssignments());
			isPercentCategoryVisible = hasWeights && (!isEqualWeight || isExtraCredit) && isItem && !isWeightByPoints;
			isWeightByPointsVisible = isEditable && isCategory && hasWeights;
			isWeightByPointsVisible = category == null ? isWeightByPointsVisible : isWeightByPointsVisible && !DataTypeConversionUtil.checkBoolean(category.getExtraCredit());
			
			isDropLowestVisible = checkIfDropLowestVisible(category, categoryType, isEditable, isCategory, isWeightByPoints, isExtraCredit);
		} else {
			isPercentCategoryVisible = hasWeights && isItem;
		}
		
		initField(nameField, isAllowedToEdit && isEditable && !isDelete && !isExternal, true);
		initField(pointsField, isAllowedToEdit && !isDelete && !isExternal, isEditable && isItem);
		initField(percentCategoryField, isAllowedToEdit && !isDelete && (isItem || isCreateNewItem), isEditable && isPercentCategoryVisible);
		initField(percentCourseGradeField, isAllowedToEdit && !isDelete, isEditable && isCategory && hasWeights);
		initField(equallyWeightChildrenField, isAllowedToEdit && !isDelete, isEditable && isCategory && hasWeights && !isWeightByPoints);
		initField(extraCreditField, isAllowedToEdit && !isDelete, isEditable && isNotGradebook);
		initField(dropLowestField, isAllowedToEdit && !isDelete, isDropLowestVisible);
		initField(dueDateField, isAllowedToEdit && !isDelete && !isExternal, isEditable && isItem);
		initField(includedField, isAllowedToEdit && !isDelete, isEditable && isNotGradebook);
		initField(releasedField, isAllowedToEdit && !isDelete, isEditable && isNotGradebook);
		initField(nullsAsZerosField, isAllowedToEdit && !isDelete, isEditable && isItem);
		initField(categoryPicker, isAllowedToEdit && !isDelete, isEditable && hasCategories && isItem);
		initField(categoryTypePicker, isAllowedToEdit, isEditable && !isNotGradebook);
		initField(gradeTypePicker, isAllowedToEdit, isEditable && !isNotGradebook);
		initField(sourceField, false, isEditable && isItem);
		initField(scaledExtraCreditField, !isDelete && isAllowedToEdit, !isNotGradebook);
		initField(enforcePointWeightingField, !isDelete && isAllowedToEdit, isWeightByPointsVisible);
		
		initField(releaseGradesField, isAllowedToEdit && !isDelete, isEditable && !isNotGradebook);
		initField(releaseItemsField, isAllowedToEdit && !isDelete, isEditable && !isNotGradebook);
		initField(showMeanField, isAllowedToEdit && !isDelete, isEditable && !isNotGradebook);
		initField(showMedianField, isAllowedToEdit && !isDelete, isEditable && !isNotGradebook);
		initField(showModeField, isAllowedToEdit && !isDelete, isEditable && !isNotGradebook);
		initField(showRankField, isAllowedToEdit && !isDelete, isEditable && !isNotGradebook);
		initField(showItemStatsField, isAllowedToEdit && !isDelete, isEditable && !isNotGradebook);
		
		displayToStudentFieldSet.setEnabled(isAllowedToEdit && !isDelete);
		displayToStudentFieldSet.setVisible(isEditable && !isNotGradebook);
	}
	
	
	private boolean checkIfDropLowestVisible(ItemModel category, CategoryType categoryType, boolean isEditable, 
			boolean isCategory, boolean isWeightByPoints, boolean isExtraCredit) {
		boolean isDropLowestVisible = isEditable && isCategory && !isExtraCredit;
		boolean isWeightedCategories = categoryType == CategoryType.WEIGHTED_CATEGORIES;
		boolean isUnweightedCategories = categoryType == CategoryType.SIMPLE_CATEGORIES;
		
		if (isDropLowestVisible && category != null 
				&& ((isWeightByPoints && isWeightedCategories) || isUnweightedCategories)) {
			if (category.getChildCount() > 0) {
				Double points = null;
				for (int i=0;i<category.getChildCount();i++) {
					ItemModel item = (ItemModel) category.getChild(i);
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
		
		return isDropLowestVisible;
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
										
									} 
								}

							};

							if (name.equals(ItemModel.Key.CATEGORY_ID.name())) {
								b.setConverter(new Converter() {
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
								b.setConverter(new Converter() {
									public Object convertFieldValue(Object value) {
										if (value instanceof ModelData && ((ModelData)value).get(VALUE_DISPLAY_FIELD) != null) {
											return ((ModelData)value).get(VALUE_DISPLAY_FIELD);
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
							bindings.put(f.getId(), b);
						}
					}
				}
			}
		};
		formBindings.setStore(treeStore);
	}

	private void addListeners() {
		if (isListeningEnabled)
			return;
		
		categoryPicker.addSelectionChangedListener(categorySelectionChangedListener);
		categoryTypePicker.addSelectionChangedListener(otherSelectionChangedListener);
		gradeTypePicker.addSelectionChangedListener(otherSelectionChangedListener);
		releaseGradesField.addListener(Events.Change, checkboxChangeListener);
		releaseItemsField.addListener(Events.Change, checkboxChangeListener);
		percentCourseGradeField.addKeyListener(keyListener);
		percentCategoryField.addKeyListener(keyListener);
		pointsField.addKeyListener(keyListener);
		dropLowestField.addKeyListener(keyListener);
		dueDateField.getDatePicker().addListener(Events.Select, datePickerListener);
		includedField.addListener(Events.Change, checkboxChangeListener);
		extraCreditField.addListener(Events.Change, extraCreditChangeListener);
		equallyWeightChildrenField.addListener(Events.Change, checkboxChangeListener);
		releasedField.addListener(Events.Change, checkboxChangeListener);
		nullsAsZerosField.addListener(Events.Change, checkboxChangeListener);
		scaledExtraCreditField.addListener(Events.Change, checkboxChangeListener);
		enforcePointWeightingField.addListener(Events.Change, enforcePointWeightingListener);
		showMeanField.addListener(Events.Change, checkboxChangeListener);
		showMedianField.addListener(Events.Change, checkboxChangeListener);
		showModeField.addListener(Events.Change, checkboxChangeListener);
		showRankField.addListener(Events.Change, checkboxChangeListener);
		showItemStatsField.addListener(Events.Change, checkboxChangeListener);	
		
		isListeningEnabled = true;
	}

	private void removeListeners() {
		if (!isListeningEnabled) 
			return;

		if (formBindings != null) {
			formBindings.removeListener(Events.Bind, bindListener);
			formBindings.removeListener(Events.UnBind, bindListener);
		}
		
		categoryPicker.removeListener(Events.SelectionChange, categorySelectionChangedListener);
		categoryTypePicker.removeListener(Events.SelectionChange, otherSelectionChangedListener);
		gradeTypePicker.removeListener(Events.SelectionChange, otherSelectionChangedListener);
		releaseGradesField.removeListener(Events.Change, checkboxChangeListener);
		releaseItemsField.removeListener(Events.Change, checkboxChangeListener);
		percentCourseGradeField.removeKeyListener(keyListener);
		percentCategoryField.removeKeyListener(keyListener);
		pointsField.removeKeyListener(keyListener);
		dropLowestField.removeKeyListener(keyListener);
		dueDateField.getDatePicker().removeListener(Events.Select, datePickerListener);
		includedField.removeListener(Events.Change, checkboxChangeListener);
		extraCreditField.removeListener(Events.Change, extraCreditChangeListener);
		equallyWeightChildrenField.removeListener(Events.Change, checkboxChangeListener);
		releasedField.removeListener(Events.Change, checkboxChangeListener);
		nullsAsZerosField.removeListener(Events.Change, checkboxChangeListener);
		scaledExtraCreditField.removeListener(Events.Change, checkboxChangeListener);
		enforcePointWeightingField.removeListener(Events.Change, enforcePointWeightingListener);
		showMeanField.removeListener(Events.Change, checkboxChangeListener);
		showMedianField.removeListener(Events.Change, checkboxChangeListener);
		showModeField.removeListener(Events.Change, checkboxChangeListener);
		showRankField.removeListener(Events.Change, checkboxChangeListener);
		showItemStatsField.removeListener(Events.Change, checkboxChangeListener);
		
		isListeningEnabled = false;
	}

	private void initListeners() {

		bindListener = new Listener<BindingEvent>() {

			public void handleEvent(BindingEvent be) {
				addListeners();
			}
			
		};
		
		categorySelectionChangedListener = new SelectionChangedListener<ItemModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ItemModel> se) {
				ItemModel itemModel = se.getSelectedItem();
				refreshSelectedCategoryState(itemModel);
				setChanges();
			}

		};

		datePickerListener = new Listener<DatePickerEvent>() {

			public void handleEvent(DatePickerEvent be) {
				setChanges();
			}
			
		};
		
		otherSelectionChangedListener = new SelectionChangedListener<ModelData>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
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

		enforcePointWeightingListener = new Listener<FieldEvent>() {
			
			public void handleEvent(FieldEvent fe) {
				boolean isChecked = DataTypeConversionUtil.checkBoolean(((CheckBox)fe.getField()).getValue());
				CategoryType categoryType = selectedGradebook.getGradebookItemModel().getCategoryType();
				boolean hasWeights = categoryType == CategoryType.WEIGHTED_CATEGORIES;
				setChanges();
				boolean isDropLowestVisible = false;
				boolean isExtraCredit = DataTypeConversionUtil.checkBoolean(extraCreditField.getValue());
				
				if (selectedItemModel != null) {
					switch (selectedItemModel.getItemType()) {
						case CATEGORY:
							initField(equallyWeightChildrenField, !isDelete, !isChecked && hasWeights);
							isDropLowestVisible = checkIfDropLowestVisible(selectedItemModel, categoryType, true, true, isChecked, isExtraCredit);
							initField(dropLowestField, !isDelete, isDropLowestVisible);
						break;
					}
				} else if (createItemType == Type.CATEGORY) {
					initField(equallyWeightChildrenField, !isDelete, !isChecked && hasWeights);
					isDropLowestVisible = checkIfDropLowestVisible(selectedItemModel, categoryType, true, true, isChecked, isExtraCredit);
					initField(dropLowestField, !isDelete, isDropLowestVisible);
				}
			}
			
		};
		
		extraCreditChangeListener = new Listener<FieldEvent>() {

			public void handleEvent(FieldEvent fe) {
				boolean isChecked = DataTypeConversionUtil.checkBoolean(((CheckBox)fe.getField()).getValue());
				CategoryType categoryType = selectedGradebook.getGradebookItemModel().getCategoryType();
				ItemModel category = categoryPicker.getValue();
				boolean isWeightByPoints = false;
				boolean isEqualWeight = false;
				boolean hasWeights = categoryType == CategoryType.WEIGHTED_CATEGORIES;
				setChanges();
				boolean isDropLowestVisible = false;
				
				if (selectedItemModel != null) {
					switch (selectedItemModel.getItemType()) {
						case CATEGORY:
							isWeightByPoints = DataTypeConversionUtil.checkBoolean(enforcePointWeightingField.getValue());
							isEqualWeight = DataTypeConversionUtil.checkBoolean(equallyWeightChildrenField.getValue());
							
							isDropLowestVisible = checkIfDropLowestVisible(selectedItemModel, categoryType, true, true, isWeightByPoints, isChecked);
							initField(dropLowestField, !isDelete, isDropLowestVisible);
							initField(enforcePointWeightingField, !isDelete, hasWeights && !isChecked);
							initField(equallyWeightChildrenField, !isDelete, hasWeights && !isWeightByPoints);
						break;
						case ITEM:
							isWeightByPoints = category == null ? false : DataTypeConversionUtil.checkBoolean(category.getEnforcePointWeighting());
							isEqualWeight = category == null ? false : DataTypeConversionUtil.checkBoolean(category.getEqualWeightAssignments());
							
							initField(percentCategoryField, !isDelete, hasWeights && !isWeightByPoints
									&& (!isEqualWeight || isChecked));
						break;
					}
				} else if (createItemType == Type.ITEM) {
					isWeightByPoints = category == null ? false : DataTypeConversionUtil.checkBoolean(category.getEnforcePointWeighting());
					isEqualWeight = category == null ? false : DataTypeConversionUtil.checkBoolean(category.getEqualWeightAssignments());
					
					initField(percentCategoryField, !isDelete, hasWeights && !isWeightByPoints
							&& (!isEqualWeight || isChecked));
				} else if (createItemType == Type.CATEGORY) {
					isWeightByPoints = DataTypeConversionUtil.checkBoolean(enforcePointWeightingField.getValue());
					isEqualWeight = DataTypeConversionUtil.checkBoolean(equallyWeightChildrenField.getValue());
					
					isDropLowestVisible = checkIfDropLowestVisible(selectedItemModel, categoryType, true, true, isWeightByPoints, isChecked);
					initField(dropLowestField, !isDelete, isDropLowestVisible);
					initField(equallyWeightChildrenField, !isDelete, hasWeights && !isWeightByPoints);
				}
			}

		};

		selectionListener = new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent be) {
				Button button = be.getButton();
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
								item.setNullsAsZeros(nullsAsZerosField.getValue());
								item.setEnforcePointWeighting(enforcePointWeightingField.getValue());
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
							case REQUEST_DELETE:
								onRequestDeleteItem(selectedItemModel);
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
											
											Listener<MessageBoxEvent> listener = new Listener<MessageBoxEvent>() {
	
												public void handleEvent(MessageBoxEvent be) {
													Button btn = be.getButtonClicked();
													
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
					category = (ItemModel) itemModel.getParent();
					if (category != null && category.getItemType() == Type.CATEGORY)
						categoryPicker.select(category);
					break;
			}
		} 
	}

	private void refreshSelectedCategoryState(ItemModel itemModel) {
		CategoryType categoryType = selectedGradebook.getGradebookItemModel().getCategoryType();

		boolean isAllowedToEdit = DataTypeConversionUtil.checkBoolean(selectedGradebook.isUserAbleToEditAssessments());
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
					category = (ItemModel) itemModel.getParent();
					break;
			}

			if (category != null && category.getItemType() == Type.CATEGORY) {
				boolean isNotEquallyWeighted = !DataTypeConversionUtil.checkBoolean(category.getEqualWeightAssignments());
				boolean isExtraCreditItem = DataTypeConversionUtil.checkBoolean(extraCreditField.getValue());
				boolean isWeightedByPoints = DataTypeConversionUtil.checkBoolean(category.getEnforcePointWeighting());
				
				isPercentCategoryVisible = (hasWeights && isItem) && !isWeightedByPoints &&
					(isNotEquallyWeighted || isExtraCreditItem);
			}
		}


		initField(percentCategoryField, isAllowedToEdit && !isDelete && (isItem || isCreateNewItem), isPercentCategoryVisible);
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
			cancelButton.setText(i18n.cancelButton());
		}
	}

	public void clearChanges() {
		hasChanges = false;
		okButton.setEnabled(false);
		okCloseButton.setEnabled(false);
		cancelButton.setText(i18n.closeButton());
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
	
	private ToolTipConfig newToolTipConfig(String text) {
		ToolTipConfig ttc = new ToolTipConfig(text);
		ttc.setDismissDelay(10000);
		return ttc;
	}

}
