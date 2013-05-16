/**********************************************************************************
 * Copyright (c) 2008, 2009, 2010, 2011 The Regents of the University of California
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
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.gxt.InlineEditField;
import org.sakaiproject.gradebook.gwt.client.gxt.InlineEditNumberField;
import org.sakaiproject.gradebook.gwt.client.gxt.ItemModelProcessor;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaButton;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ItemCreate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ItemUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModelComparer;
import org.sakaiproject.gradebook.gwt.client.gxt.view.AppView;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.ItemFormComboBox;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.NullSensitiveCheckBox;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.type.ItemType;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binding.Converter;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.data.BaseModel;
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
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
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
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

public class ItemFormPanel extends GradebookPanel {

	private enum Mode { DELETE, EDIT, NEW };
	private enum SelectionType { CLOSE, CREATE, CREATECLOSE, CANCEL, REQUEST_DELETE, DELETE, SAVE, SAVECLOSE };

	private static final String selectionTypeField = "selectionType";
	private static final String NAME_DISPLAY_FIELD = "name";
	private static final String VALUE_DISPLAY_FIELD = "value";
	// GRBK-1054 
	private static final String INSTRUCTIONAL_MSG_DISPLAY_FIELD = "imesg";

	private FormPanel formPanel;
	private FormBinding formBindings;

	private AdapterField dropLowestAdapterField;
	private LabelField directionsField;
	private TextField<String> nameField;
	private ComboBox<ModelData> categoryTypePicker, gradeTypePicker;
	private ComboBox<ItemModel> categoryPicker;
	private CheckBox enforcePointWeightingField, equallyWeightChildrenField, extraCreditField, includedField;
	private CheckBox nullsAsZerosField, releasedField, releaseGradesField, releaseItemsField, scaledExtraCreditField;
	private CheckBox showMeanField, showMedianField, showModeField;
	private CheckBox showRankField, showItemStatsField, showStatisticsChartField;
	private NumberField percentCourseGradeField, percentCategoryField, pointsField, dropLowestField;
	private DateField dueDateField;
	private TextField<String> sourceField;

	private FieldSet displayToStudentFieldSet;

	// GRBK-1054
	private Html informationMessageCategoryTypeInGradebookSetup; 
	private Html informationMessageGradeTypeInGradebookSetup; 
	private FieldSet instructionsForCategoryTypePickerSet;
	private FieldSet instructionsForGradeTypePickerSet;

	private ListStore<ItemModel> categoryStore;
	private TreeStore<ItemModel> treeStore;
	private ListStore<ModelData> gradeTypeStore;

	private KeyListener keyListener;
	private Listener<BindingEvent> bindListener;
	private Listener<DatePickerEvent> datePickerListener;
	private Listener<FieldEvent> extraCreditChangeListener, checkboxChangeListener, enforcePointWeightingListener, equallyWeightChildrenListener;
	private SelectionListener<ButtonEvent> selectionListener;
	private SelectionChangedListener<ItemModel> categorySelectionChangedListener;
	// GRBK-1054
	private SelectionChangedListener<ModelData> categoryTypeSelectionChangedListener;
	private SelectionChangedListener<ModelData> gradeTypeSelectionChangedListener;

	private RowData topRowData, bottomRowData;
	private Button deleteButton, okButton, okCloseButton, cancelButton;

	private Item gradebookItemModel;
	private ItemModel selectedItemModel;
	private ItemType createItemType;

	private boolean isListeningEnabled;
	private boolean isDelete;
	private boolean hasChanges;

	// GRBK-1054
	private boolean firstTimeInEditGradebook; 	
		
	// GRBK-943 - we need to know what the multigrid has in terms of page size. 
	private MultiGradeContentPanel multiGradePanel; 
	private Mode mode;

	private boolean hasTreeItemDragAndDropMarker;
	private boolean alertDone;

	private boolean hasUnprocessedSaveState;
		
	public ItemFormPanel() {
		super();
		this.hasTreeItemDragAndDropMarker = false;
		this.isListeningEnabled = true;
		setHeaderVisible(true);
		setFrame(true);
		setScrollMode(Scroll.AUTO);
		setLayout(new FlowLayout());
		// GRBK-943
		multiGradePanel = null;
		alertDone = false; 
		// GRBK-1054 - we use this to show a dialog and to prepopulate the instructional messages in the help boxes. 
		firstTimeInEditGradebook = true; 
		initListeners();

		formPanel = new FormPanel(){

			@Override
			public List<Field<?>> getFields() {
				List<Field<?>> rv = super.getFields();
				rv.add(dropLowestField);
				return rv;
				}
			
		};
		formPanel.setButtonAlign(Style.HorizontalAlignment.LEFT);
		formPanel.setHeaderVisible(false);
		formPanel.setLabelWidth(180);
		formPanel.setVisible(false);

		categoryStore = new ListStore<ItemModel>();
		categoryStore.setModelComparer(new ItemModelComparer<ItemModel>());

		deleteButton = new AriaButton(i18n.deleteButton(), selectionListener, 'd');
		deleteButton.setData(selectionTypeField, SelectionType.REQUEST_DELETE);
		formPanel.addButton(deleteButton);

		formPanel.getButtonBar().add(new FillToolItem());

		okButton = new AriaButton("", selectionListener, 's');
		formPanel.addButton(okButton);

		okCloseButton = new AriaButton(i18n.saveAndCloseButton(), selectionListener, 'm');
		formPanel.addButton(okCloseButton);

		cancelButton = new AriaButton(i18n.closeButton(), selectionListener, 'x');
		cancelButton.setData(selectionTypeField, SelectionType.CANCEL);

		formPanel.addButton(cancelButton);

		ListStore<ModelData> categoryTypeStore = new ListStore<ModelData>();
		categoryTypeStore.setModelComparer(new EntityModelComparer<ModelData>(VALUE_DISPLAY_FIELD));
		categoryTypeStore.add(getCategoryTypeModel(CategoryType.NO_CATEGORIES));
		categoryTypeStore.add(getCategoryTypeModel(CategoryType.SIMPLE_CATEGORIES));
		categoryTypeStore.add(getCategoryTypeModel(CategoryType.WEIGHTED_CATEGORIES));		

		directionsField = new LabelField();
		directionsField.setName("directions");

		nameField = new InlineEditField<String>();
		nameField.setAllowBlank(false);
		nameField.setName(ItemKey.S_NM.name());
		nameField.setFieldLabel(i18n.nameFieldLabel());
		nameField.addKeyListener(keyListener);

		formPanel.add(nameField);

		categoryPicker = new ItemFormComboBox<ItemModel>(ItemKey.S_NM.name(), ItemKey.L_CTGRY_ID.name(), i18n.categoryName());
		categoryPicker.addKeyListener(keyListener);
		categoryPicker.setStore(categoryStore);
		formPanel.add(categoryPicker);

		// GRBK-1054
		categoryTypePicker = new ItemFormComboBox<ModelData>(NAME_DISPLAY_FIELD, ItemKey.C_CTGRY_TYPE.name(), "");
		categoryTypePicker.setStore(categoryTypeStore);
		// GRBK-1054 - we set a tooltip, perhaps overkill, perhaps not. 
		categoryTypePicker.setToolTip(i18n.categoryTypePickerTooltip());

		// GRBK-1054 - This is the box that appears above the field for instructions with it.  
		informationMessageCategoryTypeInGradebookSetup = new Html();
		informationMessageCategoryTypeInGradebookSetup.setHtml(i18n.gradebookSetupCategoryMessageForDefault());
		informationMessageCategoryTypeInGradebookSetup.setStyleName(resources.css().setupInstructionsLabelForCategory());
		
		instructionsForCategoryTypePickerSet = new FieldSet();  
		instructionsForCategoryTypePickerSet.setCollapsible(false);
		instructionsForCategoryTypePickerSet.setHeading(i18n.gradebookSetupInstructionalForCategoryTypeGroupingHeading());  
		instructionsForCategoryTypePickerSet.setCheckboxToggle(false);  
		instructionsForCategoryTypePickerSet.setAutoHeight(true);
		instructionsForCategoryTypePickerSet.setScrollMode(Scroll.AUTO);
		instructionsForCategoryTypePickerSet.setVisible(false);
		instructionsForCategoryTypePickerSet.add(categoryTypePicker);
		instructionsForCategoryTypePickerSet.add(informationMessageCategoryTypeInGradebookSetup);
		formPanel.add(instructionsForCategoryTypePickerSet); 

		gradeTypePicker = new ItemFormComboBox<ModelData>(NAME_DISPLAY_FIELD, ItemKey.G_GRD_TYPE.name(), "");
		// GRBK-1054 - for consistency, we put a tooltip on the grade type picker. 
		gradeTypePicker.setToolTip(i18n.gradeTypePickerTooltip());

		// GRBK-1054 - This is the box that appears above the field for instructions with it.  
		informationMessageGradeTypeInGradebookSetup = new Html();
		informationMessageGradeTypeInGradebookSetup.setHtml(i18n.gradebookSetupGradeTypeMessageForDefault());
		informationMessageGradeTypeInGradebookSetup.setStyleName(resources.css().setupInstructionsLabelForGradeType());
		instructionsForGradeTypePickerSet = new FieldSet();  
		instructionsForGradeTypePickerSet.setHeading(i18n.gradebookSetupInstructionalForGradeTypeGroupingHeading());  
		instructionsForGradeTypePickerSet.setCheckboxToggle(false);  
		instructionsForGradeTypePickerSet.setAutoHeight(true);
		instructionsForGradeTypePickerSet.setScrollMode(Scroll.AUTO);
		instructionsForGradeTypePickerSet.setVisible(false);
		instructionsForGradeTypePickerSet.add(gradeTypePicker); 
		instructionsForGradeTypePickerSet.add(informationMessageGradeTypeInGradebookSetup);
		
		formPanel.add(instructionsForGradeTypePickerSet); 

		scaledExtraCreditField = new NullSensitiveCheckBox();
		scaledExtraCreditField.setName(ItemKey.B_SCL_X_CRDT.name());
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

		main.setWidth(500);

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
		releaseGradesField.setName(ItemKey.B_REL_GRDS.name());
		releaseGradesField.setFieldLabel(i18n.releaseGradesFieldLabel());
		releaseGradesField.setVisible(false);
		releaseGradesField.setToolTip(newToolTipConfig(i18n.releaseGradesToolTip()));
		left.add(releaseGradesField);

		releaseItemsField = new NullSensitiveCheckBox();
		releaseItemsField.setName(ItemKey.B_REL_ITMS.name());
		releaseItemsField.setFieldLabel(i18n.releaseItemsFieldLabel());
		releaseItemsField.setVisible(false);
		releaseItemsField.setToolTip(newToolTipConfig(i18n.releaseItemsToolTip()));
		left.add(releaseItemsField);

		showMeanField = new NullSensitiveCheckBox();
		showMeanField.setName(ItemKey.B_SHW_MEAN.name());
		showMeanField.setFieldLabel(i18n.showMeanFieldLabel());
		showMeanField.setVisible(false);
		showMeanField.setToolTip(newToolTipConfig(i18n.showMeanToolTip()));
		left.add(showMeanField);

		showMedianField = new NullSensitiveCheckBox();
		showMedianField.setName(ItemKey.B_SHW_MEDIAN.name());
		showMedianField.setFieldLabel(i18n.showMedianFieldLabel());
		showMedianField.setVisible(false);
		showMedianField.setToolTip(newToolTipConfig(i18n.showMedianToolTip()));
		left.add(showMedianField);

		showModeField = new NullSensitiveCheckBox();
		showModeField.setName(ItemKey.B_SHW_MODE.name());
		showModeField.setFieldLabel(i18n.showModeFieldLabel());
		showModeField.setVisible(false);
		showModeField.setToolTip(newToolTipConfig(i18n.showModeToolTip()));
		right.add(showModeField);

		showRankField = new NullSensitiveCheckBox();
		showRankField.setName(ItemKey.B_SHW_RANK.name());
		showRankField.setFieldLabel(i18n.showRankFieldLabel());
		showRankField.setVisible(false);
		showRankField.setToolTip(newToolTipConfig(i18n.showRankToolTip()));
		right.add(showRankField);

		showItemStatsField = new NullSensitiveCheckBox();
		showItemStatsField.setName(ItemKey.B_SHW_ITM_STATS.name());
		showItemStatsField.setFieldLabel(i18n.showItemStatsFieldLabel());
		showItemStatsField.setVisible(false);
		showItemStatsField.setToolTip(newToolTipConfig(i18n.showItemStatsToolTip()));
		//right.add(showItemStatsField);
		
		showStatisticsChartField = new NullSensitiveCheckBox();
		showStatisticsChartField.setName(ItemKey.B_SHW_STATS_CHART.name());
		showStatisticsChartField.setFieldLabel(i18n.showStatisticsChartFieldLabel());
		showStatisticsChartField.setVisible(false);
		showStatisticsChartField.setToolTip(newToolTipConfig(i18n.showStatisticsChartToolTip()));
		right.add(showStatisticsChartField);

		main.add(left, new ColumnData(.5));
		main.add(right, new ColumnData(.5));

		displayToStudentFieldSet.add(main);

		formPanel.add(displayToStudentFieldSet);

		percentCourseGradeField = new InlineEditNumberField();
		percentCourseGradeField.setName(ItemKey.D_PCT_GRD.name());
		percentCourseGradeField.setFieldLabel(i18n.percentCourseGradeFieldLabel());
		percentCourseGradeField.setFormat(DataTypeConversionUtil.getLongNumberFormat());
		percentCourseGradeField.setAllowDecimals(true);
		percentCourseGradeField.setMinValue(Double.valueOf(0.000000d));
		percentCourseGradeField.setMaxValue(Double.valueOf(100.000000d));
		percentCourseGradeField.setVisible(false);
		percentCourseGradeField.setToolTip(newToolTipConfig(i18n.percentCourseGradeToolTip()));
		formPanel.add(percentCourseGradeField);

		percentCategoryField = new InlineEditNumberField();
		percentCategoryField.setName(ItemKey.D_PCT_CTGRY.name());
		percentCategoryField.setFieldLabel(i18n.percentCategoryFieldLabel());
		percentCategoryField.setFormat(DataTypeConversionUtil.getLongNumberFormat());
		percentCategoryField.setAllowDecimals(true);
		percentCategoryField.setMinValue(Double.valueOf(0.000000d));
		percentCategoryField.setMaxValue(Double.valueOf(100.000000d));
		percentCategoryField.setVisible(false);
		percentCategoryField.setToolTip(newToolTipConfig(i18n.percentCategoryToolTip()));
		formPanel.add(percentCategoryField);

		pointsField = new InlineEditNumberField();
		pointsField.setName(ItemKey.D_PNTS.name());
		pointsField.setEmptyText(i18n.pointsFieldEmptyText());
		pointsField.setFieldLabel(i18n.pointsFieldLabel());
		pointsField.setFormat(DataTypeConversionUtil.getDefaultNumberFormat());
		pointsField.setAllowDecimals(true);
		pointsField.setAllowNegative(false); 
		pointsField.setVisible(false);
		formPanel.add(pointsField);
		
		// GRBK-1128
		dropLowestField = new InlineEditNumberField();
		dropLowestField.setEmptyText("0");
		dropLowestField.setName(ItemKey.I_DRP_LWST.name());
		dropLowestField.setAllowDecimals(false);
		dropLowestField.setMinValue(Integer.valueOf(0)); 
		dropLowestField.setMaxValue(Integer.valueOf(99));
		dropLowestField.setMaxLength(2);
		dropLowestField.setPropertyEditorType(Integer.class);
		dropLowestField.setVisible(false);
		dropLowestField.setWidth(210);
		
		Image helpIcon = new Image(resources.help());
		helpIcon.setStyleName(resources.css().helpIcon());   	
		WidgetComponent wc = new WidgetComponent(helpIcon);
		
		ToolTipConfig config = new ToolTipConfig();  
	    config.setTitle(i18n.dropLowestHelpTextTitle());  
		config.setCloseable(true);
	    config.setMouseOffset(new int[] {0, 0});
	    config.setTemplate(new Template(getTemplate(i18n.dropLowestHelpText())));  
	    
	    wc.setToolTip(config);
	   		
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(dropLowestField);
		hp.add(wc);		

		dropLowestAdapterField = new AdapterField(hp);
		dropLowestAdapterField.setFieldLabel(i18n.dropLowestFieldLabel());
		formPanel.add(dropLowestAdapterField);
		// end GRBK-1128
		
		dueDateField = new DateField();
		dueDateField.setName(ItemKey.W_DUE.name());
		dueDateField.setFieldLabel(i18n.dueDateFieldLabel());
		dueDateField.setVisible(false);
		dueDateField.setEmptyText(i18n.dueDateEmptyText());
		// GRBK-961 : Setting the date input format so that it doesn't change as the default
		// GXT version format changes between updates
		dueDateField.getPropertyEditor().setFormat(DateTimeFormat.getFormat(AppConstants.INPUT_DATE));
		formPanel.add(dueDateField);

		sourceField = new TextField<String>();
		sourceField.setName(ItemKey.S_SOURCE.name());
		sourceField.setFieldLabel(i18n.sourceFieldLabel());
		sourceField.setEnabled(false);
		sourceField.setEmptyText("Gradebook");
		sourceField.setVisible(false);
		formPanel.add(sourceField);

		includedField = new NullSensitiveCheckBox();
		includedField.setName(ItemKey.B_INCLD.name());
		includedField.setFieldLabel(i18n.includedFieldLabel());
		includedField.setVisible(false);
		includedField.setToolTip(newToolTipConfig(i18n.includedToolTip()));
		formPanel.add(includedField);

		extraCreditField = new NullSensitiveCheckBox();
		extraCreditField.setName(ItemKey.B_X_CRDT.name());
		extraCreditField.setFieldLabel(i18n.extraCreditFieldLabel());
		extraCreditField.setVisible(false);
		extraCreditField.setToolTip(newToolTipConfig(i18n.extraCreditToolTip()));
		formPanel.add(extraCreditField);

		releasedField = new NullSensitiveCheckBox();
		releasedField.setName(ItemKey.B_RLSD.name());
		releasedField.setFieldLabel(i18n.releasedFieldLabel());
		releasedField.setVisible(false);
		releasedField.setToolTip(newToolTipConfig(i18n.releasedToolTip()));
		formPanel.add(releasedField);

		nullsAsZerosField = new NullSensitiveCheckBox();
		nullsAsZerosField.setName(ItemKey.B_NLLS_ZEROS.name());
		nullsAsZerosField.setFieldLabel(i18n.nullsAsZerosFieldLabel());
		nullsAsZerosField.setVisible(false);
		nullsAsZerosField.setToolTip(newToolTipConfig(i18n.nullsAsZerosToolTip()));
		formPanel.add(nullsAsZerosField);

		equallyWeightChildrenField = new NullSensitiveCheckBox();
		equallyWeightChildrenField.setName(ItemKey.B_EQL_WGHT.name());
		equallyWeightChildrenField.setFieldLabel(i18n.equallyWeightChildrenFieldLabel());
		equallyWeightChildrenField.setVisible(false);
		equallyWeightChildrenField.setToolTip(newToolTipConfig(i18n.equallyWeightChildrenToolTip()));
		formPanel.add(equallyWeightChildrenField);

		enforcePointWeightingField = new NullSensitiveCheckBox();
		enforcePointWeightingField.setName(ItemKey.B_WT_BY_PTS.name());
		enforcePointWeightingField.setFieldLabel(i18n.enforcePointWeightingFieldLabel());
		enforcePointWeightingField.setVisible(false);
		enforcePointWeightingField.setToolTip(newToolTipConfig(i18n.enforcePointWeightingToolTip()));
		formPanel.add(enforcePointWeightingField);

		topRowData = new RowData(1, 70, new Margins(10));
		bottomRowData = new RowData(1, 1, new Margins(0, 0, 5, 0));
		add(directionsField, topRowData);
		add(formPanel, bottomRowData);
		

	}

	// GRBK-833 : This is called from the TreeView once it receives a FINISH_TREE_ITEM_DRAG_AND_DROP event
	public void setTreeItemDragAndDropMarker(boolean state) {

		this.hasTreeItemDragAndDropMarker = state;
		// Also we hide the edit form panel when we drag and drop, otherwise it's not updated
		if(formPanel.isVisible()) {
			//hideFormPanel();
		}
	}

	public void clearSelected() {
		this.selectedItemModel = null;
	}

	public void onRequestDeleteItem(final ItemModel itemModel) {
		if (hasChanges) {
			MessageBox.confirm(i18n.hasChangesTitle(), i18n.hasChangesMessage(), new Listener<MessageBoxEvent>() {

				public void handleEvent(MessageBoxEvent be) {
					Button button = be.getButtonClicked();

					if (button.getItemId().equals(Dialog.YES))
						doConfirmDeleteItem(itemModel);
				}

			});
		} else {
			doConfirmDeleteItem(itemModel);
		}
	}

	private void doConfirmDeleteItem(ItemModel itemModel) {
		// GRBK-943
		checkMGPanelForPageSize();
		formPanel.hide();
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

			ItemType itemType = itemModel.getItemType();
			initState(itemType, itemModel, true, true);
			directionsField.setHeight(80);
			directionsField.setText(i18n.directionsConfirmDeleteItem());
			directionsField.setStyleName(resources.css().gbWarning());
			directionsField.setVisible(true);

			formBindings.bind(itemModel);

			showDeleteScreen();
		}
		formPanel.show();
	}

	public void onEditItem(final ItemModel itemModel, final boolean expand) {

		if (!expand && !isVisible())
			return;

		// GRBK-943
		checkMGPanelForPageSize();
		
		clearActiveRecord();

		// This seems to prevent double click propagations in the item tree
		// GRBK-833 : don't execute this in case a tree item was dragged and dropped 
		if (!hasTreeItemDragAndDropMarker && mode == Mode.EDIT && selectedItemModel != null && itemModel != null && itemModel.equals(selectedItemModel)) {
			return;
		}

		if (hasChanges && !hasTreeItemDragAndDropMarker) {
			MessageBox.confirm(i18n.hasChangesTitle(), i18n.hasChangesMessage(), new Listener<MessageBoxEvent>() {

				public void handleEvent(MessageBoxEvent be) {
					Button button = be.getButtonClicked();

					if (button.getText().equals("Yes"))
						doEditItem(itemModel, expand, true);
				}

			});
		} else {
			doEditItem(itemModel, expand, true);
		}

		// GRBK-833 : At this point, we can reset the hasTreeItemDragAndDropMarker state to false 
		if(hasTreeItemDragAndDropMarker) {
			hasTreeItemDragAndDropMarker = false;
		}
	}

	private void doEditItemButtons(Item itemModel) {
		boolean isAllowedToEdit = DataTypeConversionUtil.checkBoolean((Boolean)Registry.get(AppConstants.IS_ABLE_TO_EDIT));

		if(null != itemModel) {
			deleteButton.setVisible(itemModel.getItemType() != ItemType.GRADEBOOK && isAllowedToEdit);
			deleteButton.setEnabled(itemModel.getItemType() != ItemType.GRADEBOOK && isAllowedToEdit);
		}
		okButton.setText(i18n.saveButton());
		okButton.setData(selectionTypeField, SelectionType.SAVE);
		okButton.setVisible(isAllowedToEdit && itemModel != null && itemModel.isEditable());
		okCloseButton.setVisible(isAllowedToEdit && itemModel != null && itemModel.isEditable());
		okCloseButton.setData(selectionTypeField, SelectionType.SAVECLOSE);
		okCloseButton.setText(i18n.saveAndCloseButton());
	}

	private void doEditItem(ItemModel itemModel, boolean expand, boolean doEnableButtons) {
		removeListeners();
		formPanel.hide();
		
		this.mode = Mode.EDIT;
		this.createItemType = null;
		this.selectedItemModel = itemModel;
		this.directionsField.setText("");
		this.directionsField.setVisible(false);

		// GRBK-1054 - We hide our messages as we do not know if they'll be visible or not. 
		informationMessageCategoryTypeInGradebookSetup.hide();
		informationMessageGradeTypeInGradebookSetup.hide(); 
		
		if (formBindings == null) {
			initFormBindings();
		} else {
			formBindings.removeAllListeners();
			formBindings.unbind();
		}

		if (itemModel != null) {
			ItemType itemType = itemModel.getItemType();
			formBindings.addListener(Events.Bind, bindListener);
			formBindings.bind(itemModel);
			initState(itemType, itemModel, false, doEnableButtons);
			
			// GRBK-1054 - We want to add some instructional messages for gradebook setup. 
			if (itemType == ItemType.GRADEBOOK)
			{
				if (firstTimeInEditGradebook)
				{
					// The first time through, we want to set the messages to be proper for the types in the item model
					// Later, if it changes, the selection listener on the combo box will take care of this. 
					informationMessageCategoryTypeInGradebookSetup.setHtml(getCategoryTypePickerInstString(itemModel.getCategoryType()));	
					informationMessageGradeTypeInGradebookSetup.setHtml(getGradeTypePickerInstString(itemModel.getGradeType()));	
	
					firstTimeInEditGradebook = false; 
				}
				// The initState above will show the boxes, we just show the labels at this point. 
				informationMessageCategoryTypeInGradebookSetup.show();
				informationMessageGradeTypeInGradebookSetup.show(); 

			}
			else if (itemType == ItemType.CATEGORY)
			{
				if (itemModel.getChildCount() == 0)
				{
					// GRBK-1083 
					
					/*
					 *  NOTE: Cannot set disabled as that disabled the tooltip as well, best
					 *  solution I came up with is to set the component read only, which disables clicking, and then set the style to make it look dead. 
					 */
					
					releasedField.setToolTip(i18n.itemFormPanelReleasedItemForCategoryEmptyMessage()); 
					releasedField.addStyleName(resources.css().gbDisableCheckbox());
					releasedField.setReadOnly(true); 
				}
			}
		} else {
			formBindings.addListener(Events.UnBind, bindListener);
			formBindings.unbind();
		}

		if (doEnableButtons)
			doEditItemButtons(selectedItemModel);

		formPanel.show();
		clearChanges(); /// this only works if the panel is rendered
		nameField.focus();
	}

	// GRBK-1054 	
	private String getGradeTypePickerInstString(GradeType gradeType) {
		
		switch (gradeType)
		{
		case LETTERS:
			return i18n.gradebookSetupGradeTypeMessageForLetterGrades();
		case PERCENTAGES:
			return i18n.gradebookSetupGradeTypeMessageForPercentages(); 
		case POINTS:
			return i18n.gradebookSetupGradeTypeMessageForPoints(); 

		}
		return ""; 
	}
	// GRBK-1054 
	private String getCategoryTypePickerInstString(CategoryType categoryType) {
		switch (categoryType)
		{
		case NO_CATEGORIES:
			return i18n.gradebookSetupCategoryMessageForNoCats(); 
		case SIMPLE_CATEGORIES:
			return i18n.gradebookSetupCategoryMessageForSimpleCats(); 
		case WEIGHTED_CATEGORIES:
			return i18n.gradebookSetupCategoryMessageForWeightedCats(); 

		}
		return ""; 
	}

	public void onItemCreated(Item itemModel) {

		ItemModelProcessor processor = new ItemModelProcessor(itemModel) {

			@Override
			public void doCategory(Item categoryModel) {
				categoryStore.add((ItemModel)categoryModel);
			}

			@Override
			public void doItem(Item itemModel) {

			}

		};

		processor.process();
	}

	public void onItemDeleted(Item itemModel) {

	}

	public void onItemUpdated(Item itemModel) {
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
				if (category.getItemType() == ItemType.CATEGORY) {
					categoryStore.add(category);
				}
			}
		}
		
	}

	public void onNewCategory(final ItemModel itemModel) {
		// GRBK-943
		checkMGPanelForPageSize();
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
		formPanel.hide();
		removeListeners();
		this.mode = Mode.NEW;

		this.directionsField.setText("");
		this.directionsField.setVisible(false);
		this.createItemType = ItemType.CATEGORY;
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

		initState(ItemType.CATEGORY, itemModel, false, true);

		// GRBK-1083 - New categories are always empty... 
		// So we disable the checkbox... 
		
		/*
		 *  NOTE: Cannot set disabled as that disabled the tooltip as well, best
		 *  solution I came up with is to set the component read only, which disables clicking, and then set the style to make it look dead. 
		 */
		releasedField.setToolTip(i18n.itemFormPanelReleasedItemForCategoryEmptyMessage()); 
		releasedField.addStyleName(resources.css().gbDisableCheckbox());
		releasedField.setReadOnly(true); 

		establishSelectedCategoryState(itemModel);

		includedField.setValue(Boolean.TRUE);
		nameField.focus();
		addListeners();
		formPanel.show();
	}

	public void onNewItem(final ItemModel itemModel) {
		// GRBK-943
		checkMGPanelForPageSize();
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
		this.createItemType = ItemType.ITEM;
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

		initState(ItemType.ITEM, itemModel, false, true);

		if (itemModel != null) {
			if (itemModel.getCategoryId() != null) {
				List<ItemModel> models = treeStore.findModels(ItemKey.S_ID.name(), String.valueOf(itemModel.getCategoryId()));
				for (ItemModel category : models) {
					if (category.getItemType() == ItemType.CATEGORY)
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

	public void onRefreshGradebookSetup(Gradebook selectedGradebook, ItemModel gradebookItemModel) {
		this.gradebookItemModel = gradebookItemModel;

		if (formBindings != null) {
			if (mode == Mode.EDIT && selectedItemModel != null 
					&& selectedItemModel.getItemType() == ItemType.GRADEBOOK) {
				selectedItemModel = gradebookItemModel;
				doEditItem(selectedItemModel, true, false);
				clearChanges();
			}
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

	public void onSwitchGradebook(Gradebook selectedGradebook, Item gradebookItemModel) {
		this.gradebookItemModel = gradebookItemModel;

		if (gradeTypeStore == null) {
			gradeTypeStore = new ListStore<ModelData>();
			gradeTypeStore.setModelComparer(new EntityModelComparer<ModelData>(VALUE_DISPLAY_FIELD));

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
		ModelData model = new BaseModel();

		// Initialize type picker
		switch (categoryType) {
		case NO_CATEGORIES:
			model.set(NAME_DISPLAY_FIELD, i18n.orgTypeNoCategories());
			model.set(VALUE_DISPLAY_FIELD, CategoryType.NO_CATEGORIES);
			break;
		case SIMPLE_CATEGORIES:
			model.set(NAME_DISPLAY_FIELD, i18n.orgTypeCategories());
			model.set(VALUE_DISPLAY_FIELD, CategoryType.SIMPLE_CATEGORIES);
			break;	
		case WEIGHTED_CATEGORIES:
			model.set(NAME_DISPLAY_FIELD, i18n.orgTypeWeightedCategories());
			model.set(VALUE_DISPLAY_FIELD, CategoryType.WEIGHTED_CATEGORIES);
			break;	
		}
		// GRBK-1054 - we set the message for the instructions here to make it a bit easier for the selection changed listner. 
		model.set(INSTRUCTIONAL_MSG_DISPLAY_FIELD, getCategoryTypePickerInstString(categoryType));
		return model;
	}

	private ModelData getGradeTypeModel(GradeType gradeType) {
		ModelData model = new BaseModel();

		switch (gradeType) {
		case LETTERS:
			model.set(NAME_DISPLAY_FIELD, i18n.gradeTypeLetters());
			model.set(VALUE_DISPLAY_FIELD, GradeType.LETTERS);
			break;
		case POINTS:
			model.set(NAME_DISPLAY_FIELD, i18n.gradeTypePoints());
			model.set(VALUE_DISPLAY_FIELD, GradeType.POINTS);
			break;
		case PERCENTAGES:
			model.set(NAME_DISPLAY_FIELD, i18n.gradeTypePercentages());
			model.set(VALUE_DISPLAY_FIELD, GradeType.PERCENTAGES);
			break;
		}
		// GRBK-1054 - we set the message for the instructions here to make it a bit easier for the selection changed listner. 
		model.set(INSTRUCTIONAL_MSG_DISPLAY_FIELD, getGradeTypePickerInstString(gradeType));
		return model;
	}	

	private void initState(ItemType itemType, ItemModel itemModel, boolean isDelete, boolean doEnableButtons) {
		
		this.isDelete = isDelete;
		clearChanges();

		if (doEnableButtons) {
			okButton.setEnabled(true);
			okCloseButton.setEnabled(true);
		}

		Item gradebookItem = gradebookItemModel;
		CategoryType categoryType = gradebookItem.getCategoryType();

		boolean isAllowedToEdit = DataTypeConversionUtil.checkBoolean((Boolean)Registry.get(AppConstants.IS_ABLE_TO_EDIT));
		boolean hasCategories = categoryType != CategoryType.NO_CATEGORIES;
		boolean hasWeights = categoryType == CategoryType.WEIGHTED_CATEGORIES;
		boolean isNotGradebook = itemType != ItemType.GRADEBOOK;
		boolean isCategory = itemType == ItemType.CATEGORY;
		boolean isItem = itemType == ItemType.ITEM;
		boolean isExternal = false;
		boolean isCreateNewItem = createItemType == ItemType.ITEM && mode == Mode.NEW;

		boolean isPercentCategoryVisible = false;
		boolean isWeightByPointsVisible = false;

		formPanel.clear();

		boolean isEditable = true;
		boolean isExtraCredit = false;
		boolean isDropLowestVisible = isEditable && isCategory;
		boolean isEqualWeight = false;
		boolean isWeightByPoints = false;
		boolean isParentExtraCreditCategory = false;

		if (itemModel != null) {
			isExtraCredit = DataTypeConversionUtil.checkBoolean(itemModel.getExtraCredit());
			isEditable = itemModel.isEditable();
			String source = itemModel.get(ItemKey.S_SOURCE.name());
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
				category = getCategoryItemModel(itemModel.getCategoryId());
				// GRBK-599 : Determine if the item's category is an extra credit category. Don't show the % Category field
				// for items that are part of an extra credit category and the category is equally weighted
				isParentExtraCreditCategory = (category != null && category.getExtraCredit()) ? true : false;

				// GRBK-833 : Make sure that grade items in an extra credit category have the extra credit checkbox checked
				if(isParentExtraCreditCategory) {
					extraCreditField.setValue(Boolean.TRUE);
				}
				break;
			default:
				isPercentCategoryVisible = (hasWeights && isExtraCredit) && isItem;
			}

			isEqualWeight = category == null ? false : DataTypeConversionUtil.checkBoolean(category.getEqualWeightAssignments());
			isWeightByPoints = category == null ? false : DataTypeConversionUtil.checkBoolean(category.getEnforcePointWeighting());
			isWeightByPointsVisible = isEditable && isCategory && hasWeights;
			isWeightByPointsVisible = category == null ? isWeightByPointsVisible : isWeightByPointsVisible && !DataTypeConversionUtil.checkBoolean(category.getExtraCredit());
			isPercentCategoryVisible = hasWeights && (!isEqualWeight || isExtraCredit) && isItem && !isWeightByPoints && (!isParentExtraCreditCategory || !isEqualWeight);

			isDropLowestVisible = checkIfDropLowestVisible(categoryType, isEditable, isCategory, isExtraCredit, isEqualWeight, isWeightByPoints); 

		} else {
			isPercentCategoryVisible = hasWeights && isItem;
		}

		//Common panel fields
		initField(nameField, isAllowedToEdit && isEditable && !isDelete && !isExternal, true);
		initField(dropLowestAdapterField, isAllowedToEdit && !isDelete, isDropLowestVisible);
		initField(dropLowestField, isAllowedToEdit && !isDelete, isDropLowestVisible);
		initField(extraCreditField, !isParentExtraCreditCategory && isAllowedToEdit && !isDelete, isEditable && isNotGradebook); // GRBK-833
		initField(includedField, isAllowedToEdit && !isDelete, isEditable && isNotGradebook);
		initField(releasedField, isAllowedToEdit && !isDelete, isEditable && isNotGradebook);

		//Item panel fields
		initField(categoryPicker, isAllowedToEdit && !isDelete, isEditable && hasCategories && isItem);
		initField(dueDateField, isAllowedToEdit && !isDelete && !isExternal, isEditable && isItem);
		initField(nullsAsZerosField, isAllowedToEdit && !isDelete, isEditable && isItem);
		initField(percentCategoryField, isAllowedToEdit && !isDelete && (isItem || isCreateNewItem), isEditable && isPercentCategoryVisible);
		initField(pointsField, isAllowedToEdit && !isDelete && !isExternal, isEditable && isItem);
		initField(sourceField, false, isEditable && isItem);

		//Category panel fields
		initField(percentCourseGradeField, isAllowedToEdit && !isDelete, isEditable && isCategory && hasWeights);
		initField(enforcePointWeightingField, isAllowedToEdit && !isDelete, isWeightByPointsVisible);
		initField(equallyWeightChildrenField, isAllowedToEdit && !isDelete, isEditable && isCategory && hasWeights && !isWeightByPoints);
		
		//Gradebook Setup panel fields
		initField(categoryTypePicker, isAllowedToEdit, isEditable && !isNotGradebook);
		initField(gradeTypePicker, isAllowedToEdit, isEditable && !isNotGradebook);
		initField(releaseGradesField, isAllowedToEdit && !isDelete, isEditable && !isNotGradebook);
		initField(releaseItemsField, isAllowedToEdit && !isDelete, isEditable && !isNotGradebook);
		initField(scaledExtraCreditField, isAllowedToEdit && !isDelete, !isNotGradebook && gradebookItem.isScaledExtraCreditEnabled());
		initField(showMeanField, isAllowedToEdit && !isDelete, isEditable && !isNotGradebook);
		initField(showMedianField, isAllowedToEdit && !isDelete, isEditable && !isNotGradebook);
		initField(showModeField, isAllowedToEdit && !isDelete, isEditable && !isNotGradebook);
		initField(showRankField, isAllowedToEdit && !isDelete, isEditable && !isNotGradebook);
		initField(showItemStatsField, isAllowedToEdit && !isDelete, isEditable && !isNotGradebook);
		initField(showStatisticsChartField, isAllowedToEdit && !isDelete, isEditable && !isNotGradebook);

		displayToStudentFieldSet.setEnabled(isAllowedToEdit && !isDelete);
		displayToStudentFieldSet.setVisible(isEditable && !isNotGradebook);
		
		// GRBK-1054 - these sets should mirror the logic for the grade type picker and the category type picker respectively. 
		instructionsForCategoryTypePickerSet.setEnabled(isAllowedToEdit && !isDelete);
		instructionsForCategoryTypePickerSet.setVisible(isEditable && !isNotGradebook);
		instructionsForGradeTypePickerSet.setEnabled(isAllowedToEdit && !isDelete);
		instructionsForGradeTypePickerSet.setVisible(isEditable && !isNotGradebook);
		
		// GRBK-1083
		releasedField.setReadOnly(false); 
		releasedField.removeToolTip();
		releasedField.removeStyleName(resources.css().gbDisableCheckbox()); 

	}

	private boolean checkIfDropLowestVisible(CategoryType categoryType, boolean isEditable, 
		boolean isCategory, boolean isExtraCredit, boolean isEqualWeight, boolean isWeightByPoints) {

		boolean isDropLowestVisible = false;
		if (isEditable && isCategory && !isExtraCredit) {
			
			boolean isWeightedCategories = categoryType != null && categoryType == CategoryType.WEIGHTED_CATEGORIES;
			boolean isUnweightedCategories = categoryType != null && categoryType == CategoryType.SIMPLE_CATEGORIES;
			
			if (isWeightedCategories) {
				if (isEqualWeight && !isWeightByPoints) {
					isDropLowestVisible = true;
				}
			} else if (isUnweightedCategories) {
				isDropLowestVisible = true;
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
					if (!bindings.containsKey(f.getName())) {
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

							if (name.equals(ItemKey.L_CTGRY_ID.name())) {
								b.setConverter(new Converter() {
									public Object convertFieldValue(Object value) {

										if (value instanceof ItemModel)
											return ((Item)value).getCategoryId();

										return value;
									}

									public Object convertModelValue(Object value) {
										if (value == null)
											return null;

										if (value instanceof Number) {
											Long categoryId = Long.valueOf(((Number)value).longValue());

											return store.findModel(ItemKey.S_ID.name(), String.valueOf(categoryId));
										}

										return null;
									}
								});
							} else if (name.equals(ItemKey.C_CTGRY_TYPE.name()) ||
									name.equals(ItemKey.G_GRD_TYPE.name())) {
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
							bindings.put(f.getName(), b);
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
		// GRBK-1054 we now use separate listeners for the two type pickers. 
		categoryTypePicker.addSelectionChangedListener(categoryTypeSelectionChangedListener);
		gradeTypePicker.addSelectionChangedListener(gradeTypeSelectionChangedListener);
		releaseGradesField.addListener(Events.Change, checkboxChangeListener);
		releaseItemsField.addListener(Events.Change, checkboxChangeListener);
		percentCourseGradeField.addKeyListener(keyListener);
		percentCategoryField.addKeyListener(keyListener);
		pointsField.addKeyListener(keyListener);
		dropLowestField.addKeyListener(keyListener);
		dueDateField.getDatePicker().addListener(Events.Select, datePickerListener);
		includedField.addListener(Events.Change, checkboxChangeListener);
		extraCreditField.addListener(Events.Change, extraCreditChangeListener);
		releasedField.addListener(Events.Change, checkboxChangeListener);
		nullsAsZerosField.addListener(Events.Change, checkboxChangeListener);
		scaledExtraCreditField.addListener(Events.Change, checkboxChangeListener);
		enforcePointWeightingField.addListener(Events.Change, enforcePointWeightingListener);
		equallyWeightChildrenField.addListener(Events.Change, equallyWeightChildrenListener);
		showMeanField.addListener(Events.Change, checkboxChangeListener);
		showMedianField.addListener(Events.Change, checkboxChangeListener);
		showModeField.addListener(Events.Change, checkboxChangeListener);
		showRankField.addListener(Events.Change, checkboxChangeListener);
		showItemStatsField.addListener(Events.Change, checkboxChangeListener);
		showStatisticsChartField.addListener(Events.Change, checkboxChangeListener);
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
		// GRBK-1054
		categoryTypePicker.removeListener(Events.SelectionChange, categoryTypeSelectionChangedListener);
		gradeTypePicker.removeListener(Events.SelectionChange, gradeTypeSelectionChangedListener);
		releaseGradesField.removeListener(Events.Change, checkboxChangeListener);
		releaseItemsField.removeListener(Events.Change, checkboxChangeListener);
		percentCourseGradeField.removeKeyListener(keyListener);
		percentCategoryField.removeKeyListener(keyListener);
		pointsField.removeKeyListener(keyListener);
		dropLowestField.removeKeyListener(keyListener);
		dueDateField.getDatePicker().removeListener(Events.Select, datePickerListener);
		includedField.removeListener(Events.Change, checkboxChangeListener);
		extraCreditField.removeListener(Events.Change, extraCreditChangeListener);
		releasedField.removeListener(Events.Change, checkboxChangeListener);
		nullsAsZerosField.removeListener(Events.Change, checkboxChangeListener);
		scaledExtraCreditField.removeListener(Events.Change, checkboxChangeListener);
		enforcePointWeightingField.removeListener(Events.Change, enforcePointWeightingListener);
		equallyWeightChildrenField.removeListener(Events.Change, equallyWeightChildrenListener);
		showMeanField.removeListener(Events.Change, checkboxChangeListener);
		showMedianField.removeListener(Events.Change, checkboxChangeListener);
		showModeField.removeListener(Events.Change, checkboxChangeListener);
		showRankField.removeListener(Events.Change, checkboxChangeListener);
		showItemStatsField.removeListener(Events.Change, checkboxChangeListener);
		showStatisticsChartField.removeListener(Events.Change, checkboxChangeListener);
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
		
		// GRBK-1054
		categoryTypeSelectionChangedListener = new SelectionChangedListener<ModelData>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				ModelData d = se.getSelectedItem(); 
				if (null == d)
					return;
				String catText = d.get(INSTRUCTIONAL_MSG_DISPLAY_FIELD);
				informationMessageCategoryTypeInGradebookSetup.setHtml(catText);
				setChanges();
			}
		};
		
		// GRBK-1054
		gradeTypeSelectionChangedListener = new SelectionChangedListener<ModelData>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				ModelData d = se.getSelectedItem(); 
				if (null == d)
					return;
				String catText = d.get(INSTRUCTIONAL_MSG_DISPLAY_FIELD);
				informationMessageGradeTypeInGradebookSetup.setHtml(catText);
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
				manageDisplayToStudentOptions();
			}
		};

		enforcePointWeightingListener = new Listener<FieldEvent>() {

			public void handleEvent(FieldEvent fe) {
				boolean isChecked = DataTypeConversionUtil.checkBoolean(((CheckBox)fe.getField()).getValue());
				CategoryType categoryType = gradebookItemModel.getCategoryType();
				boolean hasWeights = categoryType == CategoryType.WEIGHTED_CATEGORIES;
				boolean isDropLowestVisible = false;
				boolean isEqualWeight = DataTypeConversionUtil.checkBoolean(equallyWeightChildrenField.getValue());
				boolean isExtraCredit = DataTypeConversionUtil.checkBoolean(extraCreditField.getValue());

				if (selectedItemModel != null) {
					switch (selectedItemModel.getItemType()) {
					case CATEGORY:
						initField(equallyWeightChildrenField, !isDelete, !isChecked && hasWeights);
						isDropLowestVisible = checkIfDropLowestVisible(categoryType, true, true, isExtraCredit, isEqualWeight, isChecked); 
						initField(dropLowestField, !isDelete, isDropLowestVisible);
						initField(dropLowestAdapterField, !isDelete, isDropLowestVisible);
						break;
					}
				} else if (createItemType == ItemType.CATEGORY) {
					initField(equallyWeightChildrenField, !isDelete, !isChecked && hasWeights);
					isDropLowestVisible = checkIfDropLowestVisible(categoryType, true, true, isExtraCredit, isEqualWeight, isChecked);
					initField(dropLowestField, !isDelete, isDropLowestVisible);
					initField(dropLowestAdapterField, !isDelete, isDropLowestVisible);
				}
				setChanges();
			}
		};
				
		equallyWeightChildrenListener = new Listener<FieldEvent>() {

			public void handleEvent(FieldEvent fe) {
				boolean isChecked = DataTypeConversionUtil.checkBoolean(((CheckBox)fe.getField()).getValue());		
				
				CategoryType categoryType = gradebookItemModel.getCategoryType();
				boolean isDropLowestVisible = false;
				boolean isExtraCredit = DataTypeConversionUtil.checkBoolean(extraCreditField.getValue());
				boolean isWeightByPoints = DataTypeConversionUtil.checkBoolean(enforcePointWeightingField.getValue());

				if (selectedItemModel != null) {
					switch (selectedItemModel.getItemType()) {
					case CATEGORY:
						isDropLowestVisible = checkIfDropLowestVisible(categoryType, true, true, isExtraCredit, isChecked, isWeightByPoints);						
						initField(dropLowestField, !isDelete, isDropLowestVisible);
						initField(dropLowestAdapterField, !isDelete, isDropLowestVisible);
						break;
					}
				} else if (createItemType == ItemType.CATEGORY) {
					isDropLowestVisible = checkIfDropLowestVisible(categoryType, true, true, isExtraCredit, isChecked, isWeightByPoints);
					initField(dropLowestField, !isDelete, isDropLowestVisible);
					initField(dropLowestAdapterField, !isDelete, isDropLowestVisible);
				}
				
				setChanges();
			}
		};

		extraCreditChangeListener = new Listener<FieldEvent>() {

			public void handleEvent(FieldEvent fe) {
				boolean isChecked = DataTypeConversionUtil.checkBoolean(((CheckBox)fe.getField()).getValue());
				CategoryType categoryType = gradebookItemModel.getCategoryType();
				Item category = categoryPicker.getValue();
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

						isDropLowestVisible = checkIfDropLowestVisible(categoryType, true, true, isChecked, isEqualWeight, isWeightByPoints);
						initField(dropLowestField, !isDelete, isDropLowestVisible);
						initField(dropLowestAdapterField, !isDelete, isDropLowestVisible);
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
				} else if (createItemType == ItemType.ITEM) {
					isWeightByPoints = category == null ? false : DataTypeConversionUtil.checkBoolean(category.getEnforcePointWeighting());
					isEqualWeight = category == null ? false : DataTypeConversionUtil.checkBoolean(category.getEqualWeightAssignments());

					initField(percentCategoryField, !isDelete, hasWeights && !isWeightByPoints
							&& (!isEqualWeight || isChecked));
				} else if (createItemType == ItemType.CATEGORY) {
					isWeightByPoints = DataTypeConversionUtil.checkBoolean(enforcePointWeightingField.getValue());
					isEqualWeight = DataTypeConversionUtil.checkBoolean(equallyWeightChildrenField.getValue());

					isDropLowestVisible = checkIfDropLowestVisible(categoryType, true, true, isChecked, isEqualWeight, isWeightByPoints);
					initField(dropLowestField, !isDelete, isDropLowestVisible);
					initField(dropLowestAdapterField, !isDelete, isDropLowestVisible);
					initField(equallyWeightChildrenField, !isDelete, hasWeights && !isWeightByPoints);
				}
			}
		};

		selectionListener = new SelectionListener<ButtonEvent>() {

			private boolean close;

			@Override
			public void componentSelected(ButtonEvent be) {
				Button button = be.getButton();
				if (button != null) {
					SelectionType selectionType = button.getData(selectionTypeField);
					if (selectionType != null) {

						close = false;
						Record record = null;

						switch (selectionType) {

						case CREATECLOSE:
							close = true;
							Dispatcher.forwardEvent(GradebookEvents.MaskMultiGradeGrid.getEventType());
							hideFormPanel();
						case CREATE:
							// GRBK-786 GRBK-790 GRBK-789 
							if (validateFormForEditOrCreate(false) )
							{
								
								Dispatcher.forwardEvent(GradebookEvents.ShowUserFeedback.getEventType(), i18n.applicationUpdating(), false);
								
								if(close) {
									hasUnprocessedSaveState = false;
								}
								else {
									hasUnprocessedSaveState = true;
								}
								
								ItemModel item = new ItemModel();

								Item category = categoryPicker.getValue();

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
								sendItemCreateEvent(item, close);
								selectedItemModel = item;
							}
							else {
								
								/*
								 *  GRBK-1092
								 *  Canceling any ongoing user notification message and grid masking  
								 */
								Dispatcher.forwardEvent(GradebookEvents.HideUserFeedback.getEventType());
								Dispatcher.forwardEvent(GradebookEvents.UnmaskMultiGradeGrid.getEventType());
							}

							break;
						case DELETE:
							sendItemDeleteEvent(selectedItemModel);
							break;
						case CLOSE:
							/*
							 * IMPORTANT: Do not change the method order in this case statement:
							 * hideFormPanel() needs to be called before the RefreshCourseGrades event is dispatched
							 */
							hideFormPanel();
							if(null != selectedItemModel) {
								record = treeStore.getRecord(selectedItemModel);
								if(record.isDirty() || hasUnprocessedSaveState) {
									Dispatcher.forwardEvent(GradebookEvents.ShowUserFeedback.getEventType(), i18n.applicationUpdating(), false);
									Dispatcher.forwardEvent(GradebookEvents.MaskMultiGradeGrid.getEventType());
									Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
									Dispatcher.forwardEvent(GradebookEvents.RefreshCourseGrades.getEventType(), selectedGradebook);
									clearActiveRecord();
								}
							}
							hasUnprocessedSaveState = false;
							break;
						case CANCEL:
							/*
							 * IMPORTANT: Do not change the method order in this case statement:
							 * hideFormPanel() needs to be called before the RefreshCourseGrades event is dispatched
							 */
							hideFormPanel();
							if(hasUnprocessedSaveState) {
								Dispatcher.forwardEvent(GradebookEvents.ShowUserFeedback.getEventType(), i18n.applicationUpdating(), false);
								Dispatcher.forwardEvent(GradebookEvents.MaskMultiGradeGrid.getEventType());
								Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
								Dispatcher.forwardEvent(GradebookEvents.RefreshCourseGrades.getEventType(), selectedGradebook);
							}
							clearActiveRecord();
							hasUnprocessedSaveState = false;
							break;
						case REQUEST_DELETE:
							onRequestDeleteItem(selectedItemModel);
							break;
						case SAVECLOSE:
							close = true;
							
						case SAVE:
							Dispatcher.forwardEvent(GradebookEvents.MaskMultiGradeGrid.getEventType());
							hideFormPanel();
							if(close) {
								hasUnprocessedSaveState = false;
							}
							else {
								hasUnprocessedSaveState = true;
							}
							clearChanges();
							if (selectedItemModel != null) 
								record = treeStore.getRecord(selectedItemModel);								
							if (validateFormForEditOrCreate(true)) {
								
								Dispatcher.forwardEvent(GradebookEvents.ShowUserFeedback.getEventType(), i18n.applicationUpdating(), false);

								if (record != null) {

									Map<String, Object> changes = record.getChanges();


									if (changes != null 
											&& changes.get(ItemKey.D_PNTS.name()) != null
											&& !changes.get(ItemKey.D_PNTS.name())
											.equals(selectedItemModel.get(ItemKey.D_PNTS.name()))) {

										Listener<MessageBoxEvent> listener = new Listener<MessageBoxEvent>() {

											public void handleEvent(MessageBoxEvent be) {
												Button btn = be.getButtonClicked();

												Record r = treeStore.getRecord(selectedItemModel);
												if (r != null) {
													if (!r.isEditing())
														r.beginEdit();

													if (btn.getItemId().equals(Dialog.CANCEL)) {
														setChanges();
														Dispatcher.forwardEvent(GradebookEvents.HideUserFeedback.getEventType());
														if (close)
															Dispatcher.forwardEvent(GradebookEvents.UnmaskMultiGradeGrid.getEventType());
														return;
													} else if (btn.getItemId().equals(Dialog.YES)) {
														r.set(ItemKey.B_RECALC_PTS.name(), Boolean.TRUE);
													} else {
														r.set(ItemKey.B_RECALC_PTS.name(), Boolean.FALSE);
													}

													sendItemUpdateEvent(r, selectedItemModel, close);
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

								sendItemUpdateEvent(record, selectedItemModel, close);								
							}
							else {
								
								/*
								 *  GRBK-1092
								 *  Canceling any ongoing user notification message and grid masking
								 */
								Dispatcher.forwardEvent(GradebookEvents.HideUserFeedback.getEventType());
								Dispatcher.forwardEvent(GradebookEvents.UnmaskMultiGradeGrid.getEventType());
							}
							
							break; // End of switch case SAVE:
						}
					}
				}
			}
		};
	}
	// GRBK-811  Removed validating the equally weighted option client side.  For some reason it was causing problems with deletes.  

	private boolean validateFormForEditOrCreate(boolean isEdit)
	{

		List<String> errors = new ArrayList<String>(); 

		boolean nameValid = nameField.validate(); 

		boolean percentCategoryValid = (!percentCategoryField.isVisible() || percentCategoryField.validate());
		boolean percentGradeValid = (!percentCourseGradeField.isVisible() || percentCourseGradeField.validate());
		boolean pointsValid = (!pointsField.isVisible() || pointsField.validate());
		boolean dropLowestValid = (!ItemFormPanel.this.dropLowestField.isVisible() || ItemFormPanel.this.dropLowestField.validate());

		if (!nameValid)
		{
			if (isEdit)
			{
				errors.add(i18n.itemFormPanelEditNameInvalid());
			}
			else
			{
				errors.add(i18n.itemFormPanelCreateNameInvalid());
			}
		}

		if (!percentCategoryValid)
		{
			if (isEdit)
			{
				errors.add(i18n.itemFormPanelEditPercentCatgeoryInvalid());
			}
			else
			{
				errors.add(i18n.itemFormPanelCreatePercentCatgeoryInvalid());
			}
		}

		if (!percentGradeValid)
		{
			if (isEdit)
			{
				errors.add(i18n.itemFormPanelEditPercentGradeInvalid());
			}
			else
			{
				errors.add(i18n.itemFormPanelCreatePercentGradeInvalid());
			}

		}

		if (!pointsValid)
		{
			if (isEdit)
			{
				errors.add(i18n.itemFormPanelEditPointsInvalid());
			}
			else
			{
				errors.add(i18n.itemFormPanelCreatePointsInvalid());
			}
		}
		if (!dropLowestValid)
		{
			if (isEdit)
			{
				errors.add(i18n.itemFormPanelEditDropLowestInvalid());
			}
			else
			{
				errors.add(i18n.itemFormPanelCreateDropLowestInvalid());
			}
		}


		if (errors.size() > 0)
		{
			StringBuffer sb = new StringBuffer();
			if (isEdit)
			{
				sb.append(i18n.itemFormPanelEditPretext());				
			}
			else
			{
				sb.append(i18n.itemFormPanelCreatePretext());				

			}
			for (String c : errors)
			{
				sb.append("<li>");
				sb.append(c);
				sb.append("</li>"); 
			}

			if (isEdit)
			{
				sb.append(i18n.itemFormPanelEditPosttext());
			}
			else
			{
				sb.append(i18n.itemFormPanelCreatePosttext()); 
			}

			if (isEdit)
			{
				Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(i18n.itemFormPanelEditNotificationTitle(), sb.toString(), true));
			}
			else
			{
				Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(i18n.itemFormPanelCreateNotificationTitle(), sb.toString(), true));
			}
			return false; 
		}
		return true; 

	}
	
	protected void sendItemCreateEvent(ItemModel item, boolean close) {
		Dispatcher.forwardEvent(GradebookEvents.CreateItem.getEventType(), new ItemCreate(treeStore, item, close));
	}

	protected void sendItemDeleteEvent(ItemModel item) {
		Dispatcher.forwardEvent(GradebookEvents.HideFormPanel.getEventType(), Boolean.FALSE);
		Dispatcher.forwardEvent(GradebookEvents.DeleteItem.getEventType(), new ItemUpdate(treeStore, item, ItemKey.B_RMVD.name(), Boolean.FALSE, Boolean.TRUE));
	}

	protected void sendItemUpdateEvent(Record record, ItemModel item, boolean close) {
		Dispatcher.forwardEvent(GradebookEvents.UpdateItem.getEventType(), new ItemUpdate(treeStore, record, item, close));
	}

	protected void showDeleteScreen() {
		Dispatcher.forwardEvent(GradebookEvents.ExpandEastPanel.getEventType(), AppView.EastCard.DELETE_ITEM);
	}

	protected void showEditScreen(AppView.EastCard activeCard) {
		Dispatcher.forwardEvent(GradebookEvents.ExpandEastPanel.getEventType(), activeCard);
	}

	protected void hideFormPanel() {
		Dispatcher.forwardEvent(GradebookEvents.HideFormPanel.getEventType(), Boolean.FALSE);
	}

	private void establishSelectedCategoryState(ItemModel itemModel) {
		if (itemModel == null)
			return;

		CategoryType categoryType = gradebookItemModel.getCategoryType();

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
				category = getCategoryItemModel(itemModel.getCategoryId());
				if (category != null && category.getItemType() == ItemType.CATEGORY)
					categoryPicker.select(category);
				break;
			}
		} 
	}

	private void refreshSelectedCategoryState(ItemModel itemModel) {
		CategoryType categoryType = gradebookItemModel.getCategoryType();

		boolean isAllowedToEdit = DataTypeConversionUtil.checkBoolean((Boolean)Registry.get(AppConstants.IS_ABLE_TO_EDIT));
		boolean hasWeights = categoryType == CategoryType.WEIGHTED_CATEGORIES;
		boolean isPercentCategoryVisible = false;
		/*
		 * GRBK-592 This method seems only to be called for the category picker, which 
		 * seems to only be displayed for an item. 
		 * 
		 */

		boolean isItem = true; 
		boolean isCreateNewItem = createItemType == ItemType.ITEM && mode == Mode.NEW;

		if (itemModel != null) {

			Item category = null;
			switch (itemModel.getItemType()) {
			case CATEGORY:
				category = itemModel;
				break;
			case ITEM:
				category = getCategoryItemModel(itemModel.getCategoryId());
				break;
			}

			if (category != null && category.getItemType() == ItemType.CATEGORY) {
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
			cancelButton.setData(selectionTypeField, SelectionType.CANCEL);
		}
	}

	public void clearChanges() {
		hasChanges = false;
		okButton.setEnabled(false);
		okCloseButton.setEnabled(false);
		cancelButton.setText(i18n.closeButton());
		cancelButton.setData(selectionTypeField, SelectionType.CLOSE);
	}

	/*
	 * GRBK-616
	 * Helper method that manages the "Released items:" (RI) and "Statistics Chart:" SC
	 * dependencies.
	 * If RI is checked enable SC
	 * If RI is unchecked disable SC and un-check SC
	 */
	private void manageDisplayToStudentOptions() {
		
		boolean enableShowStatisticsChartField = DataTypeConversionUtil.checkBoolean(releaseItemsField.getValue());
		
		if(enableShowStatisticsChartField) {
		
			showStatisticsChartField.enable();
		}
		else {
			showStatisticsChartField.setValue(Boolean.FALSE);
			showStatisticsChartField.disable();
		}
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

	private ItemModel getCategoryItemModel(Long categoryId) {

		Gradebook gradebookModel = Registry.get(AppConstants.CURRENT);
		return (ItemModel) gradebookModel.getCategoryItemModel(categoryId);
	}

	// GRBK-943
	public MultiGradeContentPanel getMultiGradePanel() {
		return multiGradePanel;
	}

	public void setMultiGradePanel(MultiGradeContentPanel multiGradePanel) {
		this.multiGradePanel = multiGradePanel;
	}
	
	public void checkMGPanelForPageSize()
	{
		if (multiGradePanel != null)
		{
			if (multiGradePanel.getPagingToolBar().getPageSize() > AppConstants.ITEM_MANIP_PERFORMANCE_TRIGGER)
			{
				if (!alertDone) 
				{
					//GRBK-1147
					Dialog dialog = new Dialog();
					dialog.setHeading(i18n.performanceItemFormPanelTitle());
					dialog.addText(i18n.performanceItemFormPanelMsg());
					dialog.setButtons(Dialog.OK);
					dialog.setButtonAlign(HorizontalAlignment.CENTER);
					dialog.setMinWidth(450);
					dialog.setModal(true);
					dialog.setHideOnButtonClick(true);  
					dialog.show();
					
					alertDone = true; 
				}
			}
		}
	}
	
	//GRBK-1128
    private native String getTemplate(String toolTipText) /*-{ 
    var html = [ 
    '<div>',
    '<p>'+toolTipText,
    '</p>',
    '</div>' 
    
    ]; 
    return html.join(""); 
  }-*/; 

}
