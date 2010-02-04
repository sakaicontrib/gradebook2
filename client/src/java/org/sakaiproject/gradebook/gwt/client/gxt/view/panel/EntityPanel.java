package org.sakaiproject.gradebook.gwt.client.gxt.view.panel;

import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.InlineEditField;
import org.sakaiproject.gradebook.gwt.client.gxt.InlineEditNumberField;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.NullSensitiveCheckBox;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;

public abstract class EntityPanel extends ContentPanel {

	protected Field directionsField;
	protected Field nameField;
	protected Field categoryTypePicker;
	protected Field gradeTypePicker;
	protected Field categoryPicker;
	protected CheckBox includedField;
	protected CheckBox extraCreditField;
	protected CheckBox equallyWeightChildrenField;
	protected CheckBox releasedField;
	protected CheckBox nullsAsZerosField;
	protected CheckBox releaseGradesField;
	protected CheckBox releaseItemsField;
	protected CheckBox scaledExtraCreditField;
	protected CheckBox enforcePointWeightingField;
	protected CheckBox showMeanField;
	protected CheckBox showMedianField;
	protected CheckBox showModeField;
	protected CheckBox showRankField;
	protected CheckBox showItemStatsField;
	protected Field percentCourseGradeField;
	protected Field percentCategoryField;
	protected Field pointsField;
	protected Field dropLowestField;
	protected Field dueDateField;
	protected Field sourceField;
	
	private FieldSet displayToStudentFieldSet;
	
	protected final I18nConstants i18n;
	private final boolean isReadOnly;
	
	protected Gradebook selectedGradebook;
	
	public EntityPanel(I18nConstants i18n, boolean isReadOnly) {
		this.i18n = i18n;
		this.isReadOnly = isReadOnly;
		
		initialize();
		initializeStores();
		initializeCheckBoxes();
		if (isReadOnly) {
			initializeReadOnly();
		} else {
			initializeEditable();
			attachListeners();
		}
		attachFields(getFormPanel());
		bindFormPanel();
	}
	
	protected abstract void initialize();
	
	protected abstract LayoutContainer getFormPanel();
	
	protected abstract void bindFormPanel();
	
	protected void attachFields(LayoutContainer formPanel) {
		formPanel.add(nameField);
		formPanel.add(categoryPicker);
		formPanel.add(categoryTypePicker);
		formPanel.add(gradeTypePicker);
		formPanel.add(scaledExtraCreditField);
		
		if (isReadOnly) {
			attachDisplayToStudentFields(formPanel, formPanel);
		} else {
			displayToStudentFieldSet = new FieldSet();  
			displayToStudentFieldSet.setHeading(i18n.displayToStudentsHeading());  
			displayToStudentFieldSet.setCheckboxToggle(false);  
			displayToStudentFieldSet.setLayout(new FitLayout());
			displayToStudentFieldSet.setVisible(false);
			
			LayoutContainer main = new LayoutContainer();
			main.setLayout(new ColumnLayout());
			
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
			
			attachDisplayToStudentFields(left, right);
			
			main.add(left, new ColumnData(.5));
			main.add(right, new ColumnData(.5));
			
			displayToStudentFieldSet.add(main);
			
			formPanel.add(displayToStudentFieldSet);
		}
		
		formPanel.add(percentCourseGradeField);
		formPanel.add(percentCategoryField);
		formPanel.add(pointsField);
		formPanel.add(dropLowestField);
		formPanel.add(dueDateField);
		formPanel.add(sourceField);
		formPanel.add(includedField);
		formPanel.add(extraCreditField);
		formPanel.add(equallyWeightChildrenField);
		formPanel.add(releasedField);
		formPanel.add(nullsAsZerosField);
		formPanel.add(enforcePointWeightingField);
	}
	
	protected void attachDisplayToStudentFields(LayoutContainer left, LayoutContainer right) {
		left.add(releaseGradesField);
		left.add(releaseItemsField);
		left.add(showMeanField);
		left.add(showMedianField);
		right.add(showModeField);
		right.add(showRankField);
		right.add(showItemStatsField);
	}
	
	protected abstract void initializeStores(); /* {
		categoryStore = new ListStore<ItemModel>();
		categoryStore.setModelComparer(new ItemModelComparer<ItemModel>());
		categoryPicker.setStore(categoryStore);
	}*/
	
	protected abstract void attachListeners(); /* {
		nameField.addKeyListener(keyListener);
		categoryPicker.addKeyListener(keyListener);
	} */
	
	private CheckBox newCheckBox(String name, String label, String tooltip) {
		CheckBox checkbox = null;
		if (isReadOnly) 
			checkbox = new CheckBox();
		else
			checkbox = new NullSensitiveCheckBox();
		
		checkbox.setName(name);
		checkbox.setFieldLabel(label);
		checkbox.setVisible(false);
		checkbox.setToolTip(newToolTipConfig(tooltip));
		checkbox.setReadOnly(isReadOnly);
		
		return checkbox;
	}
	
	private void initializeCheckBoxes() {
		scaledExtraCreditField = newCheckBox(ItemKey.EXTRA_CREDIT_SCALED.name(), i18n.scaledExtraCreditFieldLabel(), i18n.scaledExtraCreditToolTip());
		
		displayToStudentFieldSet = new FieldSet();  
		displayToStudentFieldSet.setHeading(i18n.displayToStudentsHeading());  
		displayToStudentFieldSet.setCheckboxToggle(false);  
		displayToStudentFieldSet.setLayout(new FitLayout());
		displayToStudentFieldSet.setVisible(false);
				
		releaseGradesField = newCheckBox(ItemKey.RELEASEGRADES.name(), i18n.releaseGradesFieldLabel(), i18n.releaseGradesToolTip());
		releaseItemsField = newCheckBox(ItemKey.RELEASEITEMS.name(), i18n.releaseItemsFieldLabel(), i18n.releaseItemsToolTip());
		showMeanField = newCheckBox(ItemKey.SHOWMEAN.name(), i18n.showMeanFieldLabel(), i18n.showMeanToolTip());		
		showMedianField = newCheckBox(ItemKey.SHOWMEDIAN.name(), i18n.showMedianFieldLabel(), i18n.showMedianToolTip());
		showModeField = newCheckBox(ItemKey.SHOWMODE.name(), i18n.showModeFieldLabel(), i18n.showModeToolTip());
		showRankField = newCheckBox(ItemKey.SHOWRANK.name(), i18n.showRankFieldLabel(), i18n.showRankToolTip());
		showItemStatsField = newCheckBox(ItemKey.SHOWITEMSTATS.name(), i18n.showItemStatsFieldLabel(), i18n.showItemStatsToolTip());
		includedField = newCheckBox(ItemKey.INCLUDED.name(), i18n.includedFieldLabel(), i18n.includedToolTip());
		extraCreditField = newCheckBox(ItemKey.EXTRA_CREDIT.name(), i18n.extraCreditFieldLabel(), i18n.extraCreditToolTip());
		equallyWeightChildrenField = newCheckBox(ItemKey.EQUAL_WEIGHT.name(), i18n.equallyWeightChildrenFieldLabel(), i18n.equallyWeightChildrenToolTip());
		releasedField = newCheckBox(ItemKey.RELEASED.name(), i18n.releasedFieldLabel(), i18n.releasedToolTip());
		nullsAsZerosField = newCheckBox(ItemKey.NULLSASZEROS.name(), i18n.nullsAsZerosFieldLabel(), i18n.nullsAsZerosToolTip());
		enforcePointWeightingField = newCheckBox(ItemKey.ENFORCE_POINT_WEIGHTING.name(), i18n.enforcePointWeightingFieldLabel(), i18n.enforcePointWeightingToolTip());
	}
	
	private void initializeReadOnly() {
		
		directionsField = new LabelField();
		directionsField.setName("directions");

		LabelField nameField = new LabelField();
		nameField.setName(ItemKey.NAME.name());
		nameField.setFieldLabel(i18n.nameFieldLabel());
		nameField.setStyleAttribute("font-size", "12pt");
		this.nameField = nameField;

		LabelField categoryPicker = new LabelField();
		categoryPicker.setName(ItemKey.CATEGORY_NAME.name());
		categoryPicker.setFieldLabel(i18n.categoryName());
		categoryPicker.setStyleAttribute("font-size", "12pt");
		categoryPicker.setVisible(false);
		this.categoryPicker = categoryPicker;

		LabelField categoryTypePicker = new LabelField();
		categoryTypePicker.setName(ItemKey.CATEGORYTYPE.name());
		categoryTypePicker.setFieldLabel(i18n.categoryTypeFieldLabel());
		categoryTypePicker.setStyleAttribute("font-size", "12pt");
		categoryTypePicker.setVisible(false);
		this.categoryTypePicker = categoryTypePicker;

		LabelField gradeTypePicker = new LabelField();
		gradeTypePicker.setName(ItemKey.GRADETYPE.name());
		gradeTypePicker.setFieldLabel(i18n.gradeTypeFieldLabel());
		gradeTypePicker.setStyleAttribute("font-size", "12pt");
		gradeTypePicker.setVisible(false);
		this.gradeTypePicker = gradeTypePicker;
				
		LabelField percentCourseGradeField = new LabelField();
		percentCourseGradeField.setName(ItemKey.PERCENT_COURSE_GRADE.name());
		percentCourseGradeField.setFieldLabel(i18n.percentCourseGradeFieldLabel());
		percentCourseGradeField.setStyleAttribute("font-size", "12pt");
		percentCourseGradeField.setVisible(false);
		percentCourseGradeField.setToolTip(newToolTipConfig(i18n.percentCourseGradeToolTip()));
		this.percentCourseGradeField = percentCourseGradeField;
		
		LabelField percentCategoryField = new LabelField();
		percentCategoryField.setName(ItemKey.PERCENT_CATEGORY.name());
		percentCategoryField.setFieldLabel(i18n.percentCategoryFieldLabel());
		percentCategoryField.setStyleAttribute("font-size", "12pt");
		percentCategoryField.setVisible(false);
		percentCategoryField.setToolTip(newToolTipConfig(i18n.percentCategoryToolTip()));
		this.percentCategoryField = percentCategoryField;
		
		LabelField pointsField = new LabelField();
		pointsField.setName(ItemKey.POINTS.name());
		pointsField.setEmptyText(i18n.pointsFieldEmptyText());
		pointsField.setFieldLabel(i18n.pointsFieldLabel());
		pointsField.setStyleAttribute("font-size", "12pt");
		pointsField.setVisible(false);
		this.pointsField = pointsField;

		LabelField dropLowestField = new LabelField();
		dropLowestField.setEmptyText("0");
		dropLowestField.setName(ItemKey.DROP_LOWEST.name());
		dropLowestField.setFieldLabel(i18n.dropLowestFieldLabel());
		dropLowestField.setStyleAttribute("font-size", "12pt");
		dropLowestField.setVisible(false);
		dropLowestField.setToolTip(i18n.dropLowestToolTip());
		this.dropLowestField = dropLowestField;

		LabelField dueDateField = new LabelField();
		dueDateField.setName(ItemKey.DUE_DATE.name());
		dueDateField.setFieldLabel(i18n.dueDateFieldLabel());
		dueDateField.setStyleAttribute("font-size", "12pt");
		dueDateField.setVisible(false);
		dueDateField.setEmptyText(i18n.dueDateEmptyText());
		this.dueDateField = dueDateField;

		LabelField sourceField = new LabelField();
		sourceField.setName(ItemKey.SOURCE.name());
		sourceField.setFieldLabel(i18n.sourceFieldLabel());
		sourceField.setEnabled(false);
		sourceField.setEmptyText("Gradebook");
		sourceField.setStyleAttribute("font-size", "12pt");
		sourceField.setVisible(false);
		this.sourceField = sourceField;
		
	}
	
	private void initializeEditable() {
		
		directionsField = new LabelField();
		directionsField.setName("directions");

		InlineEditField<String> nameField = new InlineEditField<String>();
		nameField.setAllowBlank(false);
		nameField.setName(ItemKey.NAME.name());
		nameField.setFieldLabel(i18n.nameFieldLabel());
		this.nameField = nameField;

		ComboBox<ItemModel> categoryPicker = new ComboBox<ItemModel>();
		categoryPicker.setDisplayField(ItemKey.NAME.name());
		categoryPicker.setName(ItemKey.CATEGORY_ID.name());
		categoryPicker.setFieldLabel(i18n.categoryName());
		categoryPicker.setVisible(false);
		this.categoryPicker = categoryPicker;

		ComboBox<ModelData> categoryTypePicker = new ComboBox<ModelData>();
		categoryTypePicker.setDisplayField("name");
		categoryTypePicker.setName(ItemKey.CATEGORYTYPE.name());
		categoryTypePicker.setEditable(false);
		categoryTypePicker.setFieldLabel(i18n.categoryTypeFieldLabel());
		categoryTypePicker.setForceSelection(true);
		categoryTypePicker.setVisible(false);
		this.categoryTypePicker = categoryTypePicker;

		ComboBox<ModelData> gradeTypePicker = new ComboBox<ModelData>();
		gradeTypePicker.setDisplayField("name");
		gradeTypePicker.setEditable(false);
		gradeTypePicker.setName(ItemKey.GRADETYPE.name());
		gradeTypePicker.setFieldLabel(i18n.gradeTypeFieldLabel());
		gradeTypePicker.setForceSelection(true);
		gradeTypePicker.setVisible(false);
		this.gradeTypePicker = gradeTypePicker;
				
		InlineEditNumberField percentCourseGradeField = new InlineEditNumberField();
		percentCourseGradeField.setName(ItemKey.PERCENT_COURSE_GRADE.name());
		percentCourseGradeField.setFieldLabel(i18n.percentCourseGradeFieldLabel());
		percentCourseGradeField.setFormat(DataTypeConversionUtil.getLongNumberFormat());
		percentCourseGradeField.setAllowDecimals(true);
		percentCourseGradeField.setMinValue(Double.valueOf(0.000000d));
		percentCourseGradeField.setMaxValue(Double.valueOf(100.000000d));
		percentCourseGradeField.setVisible(false);
		percentCourseGradeField.setToolTip(newToolTipConfig(i18n.percentCourseGradeToolTip()));
		this.percentCourseGradeField = percentCourseGradeField;
		
		InlineEditNumberField percentCategoryField = new InlineEditNumberField();
		percentCategoryField.setName(ItemKey.PERCENT_CATEGORY.name());
		percentCategoryField.setFieldLabel(i18n.percentCategoryFieldLabel());
		percentCategoryField.setFormat(DataTypeConversionUtil.getLongNumberFormat());
		percentCategoryField.setAllowDecimals(true);
		percentCategoryField.setMinValue(Double.valueOf(0.000000d));
		percentCategoryField.setMaxValue(Double.valueOf(100.000000d));
		percentCategoryField.setVisible(false);
		percentCategoryField.setToolTip(newToolTipConfig(i18n.percentCategoryToolTip()));
		this.percentCategoryField = percentCategoryField;
		
		InlineEditNumberField pointsField = new InlineEditNumberField();
		pointsField.setName(ItemKey.POINTS.name());
		pointsField.setEmptyText(i18n.pointsFieldEmptyText());
		pointsField.setFieldLabel(i18n.pointsFieldLabel());
		pointsField.setFormat(DataTypeConversionUtil.getDefaultNumberFormat());
		pointsField.setAllowDecimals(true);
		pointsField.setMinValue(Double.valueOf(0.0001d));
		pointsField.setVisible(false);
		this.pointsField = pointsField;

		InlineEditNumberField dropLowestField = new InlineEditNumberField();
		dropLowestField.setEmptyText("0");
		dropLowestField.setName(ItemKey.DROP_LOWEST.name());
		dropLowestField.setFieldLabel(i18n.dropLowestFieldLabel());
		dropLowestField.setAllowDecimals(false);
		dropLowestField.setPropertyEditorType(Integer.class);
		dropLowestField.setVisible(false);
		dropLowestField.setToolTip(i18n.dropLowestToolTip());
		this.dropLowestField = dropLowestField;

		DateField dueDateField = new DateField();
		dueDateField.setName(ItemKey.DUE_DATE.name());
		dueDateField.setFieldLabel(i18n.dueDateFieldLabel());
		dueDateField.setVisible(false);
		dueDateField.setEmptyText(i18n.dueDateEmptyText());
		this.dueDateField = dueDateField;

		TextField<String> sourceField = new TextField<String>();
		sourceField.setName(ItemKey.SOURCE.name());
		sourceField.setFieldLabel(i18n.sourceFieldLabel());
		sourceField.setEnabled(false);
		sourceField.setEmptyText("Gradebook");
		sourceField.setVisible(false);
		this.sourceField = sourceField;
	
	}

	
	private void initField(Field field, boolean isEnabled, boolean isVisible) {

		field.setEnabled(isEnabled);
		field.setVisible(isVisible);
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
					Item item = (Item) category.getChild(i);
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
	
	private ToolTipConfig newToolTipConfig(String text) {
		ToolTipConfig ttc = new ToolTipConfig(text);
		ttc.setDismissDelay(10000);
		return ttc;
	}
	
}
