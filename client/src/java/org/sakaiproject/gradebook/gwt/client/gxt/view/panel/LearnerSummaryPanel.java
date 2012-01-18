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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.gxt.InlineEditField;
import org.sakaiproject.gradebook.gwt.client.gxt.InlineEditNumberField;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaButton;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaTabPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.event.BrowseLearner;
import org.sakaiproject.gradebook.gwt.client.gxt.event.BrowseLearner.BrowseType;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradeRecordUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PropertyChangeEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.Widget;

public class LearnerSummaryPanel extends GradebookPanel {

	private static final String FIELD_STATE_FIELD = "fieldState";
	private static final String BUTTON_SELECTOR_FLAG = "buttonSelector";
	private enum ButtonSelector { CLOSE, COMMENT, NEXT, PREVIOUS, VIEW_AS_LEARNER };

	private ContentPanel learnerInfoPanel;
	private FormBinding formBinding;
	private FormPanel formPanel;
	private LayoutContainer commentFormPanel;
	private LayoutContainer excuseFormPanel;
	private LayoutContainer scoreFormPanel;
	private SelectionListener<ComponentEvent> selectionListener;
	private ModelData learner;

	private FormLayout commentFormLayout;
	private FormLayout excuseFormLayout;
	private FormLayout scoreFormLayout;

	private FlexTableContainer learnerInfoTable;

	private boolean isPossibleGradeTypeChanged = false;
	private int scoresDropped = 0;

	public LearnerSummaryPanel() {
		setHeaderVisible(false);
		setId("learnerSummaryPanel");
		setLayout(new FlowLayout());
		setScrollMode(Scroll.AUTO);
		setWidth(400);

		initListeners();

		add(newLearnerInfoPanel());

		FlowLayout formLayout = new FlowLayout();

		formPanel = new FormPanel();
		formPanel.setHeaderVisible(false);
		formPanel.setLayout(formLayout);
		formPanel.setScrollMode(Scroll.AUTO);

		TabPanel tabPanel = new AriaTabPanel();
		tabPanel.setPlain(true);
		tabPanel.setBorderStyle(true);

		TabItem tab = new TabItem(i18n.learnerTabGradeHeader());
		tab.addStyleName(resources.css().gbTabMargins());
		tab.setLayout(new FlowLayout());
		tab.add(newGradeFormPanel());
		tab.setScrollMode(Scroll.AUTOY);
		tabPanel.add(tab);

		tab = new TabItem(i18n.learnerTabCommentHeader());
		tab.addStyleName(resources.css().gbTabMargins());
		tab.setLayout(new FitLayout());
		tab.add(newCommentFormPanel());
		tab.setScrollMode(Scroll.AUTOY);
		tabPanel.add(tab);

		tab = new TabItem(i18n.learnerTabExcuseHeader());
		tab.addStyleName(resources.css().gbTabMargins());
		tab.setLayout(new FitLayout());
		tab.add(newExcuseFormPanel());
		tab.setScrollMode(Scroll.AUTOY);
		tabPanel.add(tab);

		formPanel.add(tabPanel);

		add(formPanel);

		Button button = new AriaButton(i18n.prevLearner(), selectionListener);
		button.setData(BUTTON_SELECTOR_FLAG, ButtonSelector.PREVIOUS);
		addButton(button);

		button = new AriaButton(i18n.nextLearner(), selectionListener);
		button.setData(BUTTON_SELECTOR_FLAG, ButtonSelector.NEXT);
		addButton(button);

		button = new AriaButton(i18n.viewAsLearner(), selectionListener);
		button.setData(BUTTON_SELECTOR_FLAG, ButtonSelector.VIEW_AS_LEARNER);
		addButton(button);

		button = new AriaButton(i18n.close(), selectionListener);
		button.setData(BUTTON_SELECTOR_FLAG, ButtonSelector.CLOSE);
		addButton(button);
	}

	public void onChangeModel(ListStore<ModelData> learnerStore, TreeStore<ItemModel> treeStore, ModelData learner) {
		this.learner = learner;
		this.scoresDropped = 0;
		

		if (learner != null) {
			verifyFormPanelComponents(treeStore, learnerStore);

			formBinding.setStore(learnerStore);
			formBinding.bind(learner);
		}

		for (Component item : scoreFormPanel.getItems()) {
			if (item instanceof Field) {
				Field<?> field = (Field<?>)item;
				field.setEnabled(true);
				verifyFieldState(field, learner);
			}
		}

		for (Component item : commentFormPanel.getItems()) {
			if (item instanceof Field)
				((Field<?>)item).setEnabled(true);
		}

		for (Component item : excuseFormPanel.getItems()) {
			if (item instanceof Field)
				((Field<?>)item).setEnabled(true);
		}
		
		/*
		 *  GRBK-504 this call should occur after call to VerifyFieldState
		 *  in order to get updated count for dropped grades
		 */
		updateLearnerInfo(learner, false);
	}

	public void onGradeTypeUpdated(Gradebook selectedGradebook) {
		this.isPossibleGradeTypeChanged = true;
	}

	public void onLearnerGradeRecordUpdated(ModelData learner) {
		if (this.learner != null && learner != null) { 
			String uid1 = this.learner.get(LearnerKey.S_UID.name());
			String uid2 = learner.get(LearnerKey.S_UID.name());

			if (uid1 != null && uid2 != null && uid1.equals(uid2))
				updateLearnerInfo(learner, true);
		}
	}

	public void onRefreshGradebookSetup(Gradebook gradebookModel) {

	}

	@Override
	protected void onResize(final int width, final int height) {
		commentFormLayout.setDefaultWidth(width - 60);

		super.onResize(width, height);
	}

	private void addField(Set<String> itemIdSet, ItemModel item, int row, GradeType gradeType) {
		String itemId = new StringBuilder().append(AppConstants.LEARNER_SUMMARY_FIELD_PREFIX).append(item.getIdentifier()).toString();
		String source = item.getSource();
		boolean isStatic = source != null && source.equals(AppConstants.STATIC);

		if (!itemIdSet.contains(itemId) && !isStatic) {

			String dataType = item.getDataType();

			if (dataType != null) {
				StringBuilder emptyText = new StringBuilder();
				boolean isEmptyTextFilled = false;
				switch (gradeType) {
				case PERCENTAGES:
					emptyText.append("Enter a value between 0 and 100");
					isEmptyTextFilled = true;
				case POINTS:
					NumberField field = new InlineEditNumberField();

					if (!isEmptyTextFilled)
						emptyText.append("Enter a value between 0 and ").append(DataTypeConversionUtil.formatDoubleAsPointsString(item.getPoints()));

					field.setItemId(itemId);
					field.addInputStyleName(resources.css().gbNumericFieldInput());
					//field.addKeyListener(keyListener);
					field.setFieldLabel(item.getName());
					field.setFormat(DataTypeConversionUtil.getDefaultNumberFormat());
					field.setName(item.getIdentifier());
					field.setToolTip(emptyText.toString());
					field.setWidth(50);
					field.setLabelStyle("overflow: hidden");

					verifyFieldState(field, item);

					scoreFormPanel.add(field);
					break;
				case LETTERS:
					TextField<String> textField = new InlineEditField<String>();

					emptyText.append("Enter a letter grade");

					textField.setItemId(itemId);
					textField.addInputStyleName(resources.css().gbTextFieldInput());
					textField.setFieldLabel(item.getName());
					textField.setName(item.getIdentifier());
					textField.setToolTip(emptyText.toString());
					textField.setWidth(50);
					textField.setLabelStyle("overflow: hidden");

					verifyFieldState(textField, item);

					scoreFormPanel.add(textField);
				}

				String checkBoxName = DataTypeConversionUtil.buildExcusedKey(item.getIdentifier());
				CheckBox checkbox = new CheckBox();
				checkbox.setFieldLabel(item.getName());
				checkbox.setName(checkBoxName);
				checkbox.setLabelStyle("overflow: hidden;");
				excuseFormPanel.add(checkbox);

				String commentId = DataTypeConversionUtil.buildCommentTextKey(item.getIdentifier());
				TextArea textArea = new TextArea();
				textArea.addInputStyleName(resources.css().gbTextAreaInput());
				textArea.setFieldLabel(item.getName());
				textArea.setItemId(itemId);
				textArea.setName(commentId);

				commentFormPanel.add(textArea);
			}
		}
	}

	private void initListeners() {

		selectionListener = new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent be) {
				Component c = be.getComponent();
				ButtonSelector selector = c.getData(BUTTON_SELECTOR_FLAG);

				BrowseLearner bse = null;

				switch (selector) {
				case CLOSE:
					Dispatcher.forwardEvent(GradebookEvents.HideEastPanel.getEventType(), Boolean.FALSE);
					break;
				case COMMENT:
					break;
				case NEXT:
					bse = new BrowseLearner(learner, BrowseType.NEXT);
					Dispatcher.forwardEvent(GradebookEvents.BrowseLearner.getEventType(), bse);
					break;
				case PREVIOUS:
					bse = new BrowseLearner(learner, BrowseType.PREV);
					Dispatcher.forwardEvent(GradebookEvents.BrowseLearner.getEventType(), bse);
					break;
				case VIEW_AS_LEARNER:
					Dispatcher.forwardEvent(GradebookEvents.SingleView.getEventType(), learner);
					break;
				}

			}


		};	

	}

	private LayoutContainer newCommentFormPanel() {
		commentFormPanel = new LayoutContainer();
		commentFormLayout = new FormLayout();
		commentFormLayout.setLabelAlign(LabelAlign.TOP);
		commentFormPanel.setLayout(commentFormLayout);
		commentFormPanel.setScrollMode(Scroll.AUTOY);

		return commentFormPanel;
	}

	private LayoutContainer newExcuseFormPanel() {
		excuseFormPanel = new LayoutContainer();
		excuseFormLayout = new FormLayout();
		excuseFormLayout.setLabelAlign(LabelAlign.LEFT);
		excuseFormLayout.setLabelSeparator("");
		excuseFormLayout.setLabelWidth(180);
		excuseFormPanel.setLayout(excuseFormLayout);
		excuseFormPanel.setScrollMode(Scroll.AUTOY);

		return excuseFormPanel;
	}

	private LayoutContainer newGradeFormPanel() {
		scoreFormPanel = new LayoutContainer();
		scoreFormLayout = new FormLayout();
		scoreFormLayout.setDefaultWidth(50);
		scoreFormLayout.setLabelSeparator("");
		scoreFormLayout.setLabelWidth(180);
		scoreFormPanel.setLayout(scoreFormLayout);
		scoreFormPanel.setScrollMode(Scroll.AUTOY);

		return scoreFormPanel;
	}

	private ContentPanel newLearnerInfoPanel() {
		learnerInfoTable = new FlexTableContainer(new FlexTable()); 
		learnerInfoTable.setStyleName(resources.css().gbStudentInformation());
		learnerInfoPanel = new ContentPanel();
		learnerInfoPanel.setHeaderVisible(false);
		learnerInfoPanel.setHeading("Individual Grade Summary");
		learnerInfoPanel.setScrollMode(Scroll.AUTO);
		learnerInfoPanel.add(learnerInfoTable);

		return learnerInfoPanel;
	}

	private static final String rowHeight = "22px";

	private void updateLearnerInfo(ModelData learnerGradeRecordCollection, boolean isByEvent) {		
		// To force a refresh, let's first hide the owning panel
		learnerInfoPanel.hide();

		// Now, let's update the student information table
		FlexCellFormatter formatter = learnerInfoTable.getFlexCellFormatter();

		learnerInfoTable.setText(1, 0, i18n.columnTitleDisplayName());
		formatter.setStyleName(1, 0, resources.css().gbImpact());
		formatter.setHeight(1, 0, rowHeight);
		learnerInfoTable.setText(1, 1, (String)learnerGradeRecordCollection.get(LearnerKey.S_DSPLY_NM.name()));
		formatter.setHeight(1, 1, rowHeight);
		learnerInfoTable.setAutoHeight(true);

		learnerInfoTable.setText(2, 0, i18n.columnTitleEmail());
		formatter.setStyleName(2, 0, resources.css().gbImpact());
		formatter.setHeight(2, 0, rowHeight);
		learnerInfoTable.setText(2, 1, (String)learnerGradeRecordCollection.get(LearnerKey.S_EMAIL.name()));
		formatter.setHeight(2, 1, rowHeight);

		learnerInfoTable.setText(3, 0, i18n.columnTitleDisplayId());
		formatter.setStyleName(3, 0, resources.css().gbImpact());
		formatter.setHeight(3, 0, rowHeight);
		learnerInfoTable.setText(3, 1, (String)learnerGradeRecordCollection.get(LearnerKey.S_DSPLY_ID.name()));
		formatter.setHeight(3, 1, rowHeight);

		learnerInfoTable.setText(4, 0, i18n.columnTitleSection());
		formatter.setStyleName(4, 0, resources.css().gbImpact());
		formatter.setHeight(4, 0, rowHeight);
		learnerInfoTable.setText(4, 1, (String)learnerGradeRecordCollection.get(LearnerKey.S_SECT.name()));
		formatter.setHeight(4, 1, rowHeight);

		learnerInfoTable.setText(5, 0, i18n.courseGrade());
		formatter.setStyleName(5, 0, resources.css().gbImpact());
		formatter.setHeight(5, 0, rowHeight);
		learnerInfoTable.setText(5, 1, (String)learnerGradeRecordCollection.get(LearnerKey.S_CRS_GRD.name()));
		formatter.setHeight(5, 1, rowHeight);
		learnerInfoPanel.show();
		
		learnerInfoTable.setText(6, 0, i18n.scoresDropped());
		formatter.setStyleName(6, 0, resources.css().gbImpact());
		formatter.setHeight(6, 0, rowHeight);
		learnerInfoTable.setText(6, 1, "" + getCountOfScoresDropped());
		formatter.setHeight(6, 1, rowHeight);
		learnerInfoPanel.show();
	}

	private int getCountOfScoresDropped() {
		return scoresDropped ;
	}

	private void verifyFormPanelComponents(TreeStore<ItemModel> treeStore, final ListStore<ModelData> learnerStore) {

		boolean isLayoutNecessary = false;
		if (isPossibleGradeTypeChanged) {
			scoreFormPanel.removeAll();
			excuseFormPanel.removeAll();
			commentFormPanel.removeAll();
			formBinding.unbind();
			formBinding = null;
			this.isPossibleGradeTypeChanged = false;
			isLayoutNecessary = true;
		}

		List<ItemModel> rootItems = treeStore.getRootItems();

		List<Component> allItems = scoreFormPanel.getItems();
		Set<String> itemIdSet = new HashSet<String>();
		if (allItems != null) {
			for (Component c : allItems) {
				itemIdSet.add(c.getItemId());
			}
		}

		Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
		GradeType gradeType = selectedGradebook.getGradebookItemModel().getGradeType();

		int row = 0;
		if (rootItems != null) {
			for (ItemModel root : rootItems) {

				if (root.getChildCount() > 0) {
					for (ModelData m : root.getChildren()) {
						ItemModel child = (ItemModel)m;
						if (child.getChildCount() > 0) {

							for (ModelData m2 : child.getChildren()) {
								ItemModel subchild = (ItemModel)m2;
								addField(itemIdSet, subchild, row, gradeType);
								row++;
							}

						} else {
							addField(itemIdSet, child, row, gradeType);
							row++;
						}

					}
				} 

			}
		}

		if (isLayoutNecessary) {
			scoreFormPanel.layout();
		}

		if (formBinding == null) {

			formBinding = new FormBinding(formPanel, true) {
			
				public void autoBind() {
				
					for (Field f : panel.getFields()) {
						
						if (!bindings.containsKey(f.getName())) {
					
							String name = f.getName();
							
							if (name != null && name.length() > 0) {
							
								FieldBinding b = new FieldBinding(f, f.getName()) {

									private boolean isBinding = false;

									@Override
									public void bind(ModelData model) {
										this.isBinding = true;
										super.bind(model);
										this.isBinding = false;
									}

									@Override
									protected void onFieldChange(FieldEvent e) {
										// We don't want to send events when we're still binding
										if (isBinding)
											return;

										ModelData learner = this.model;
										e.getField().setEnabled(false);

										Dispatcher.forwardEvent(GradebookEvents.UpdateLearnerGradeRecord.getEventType(), new GradeRecordUpdate(learnerStore, learner, e.getField().getName(), e.getField().getFieldLabel(), e.getOldValue(), e.getValue()));
									}

									@Override
									protected void onModelChange(PropertyChangeEvent event) {
										super.onModelChange(event);

										if (field != null) {
											verifyFieldState(field, (BaseModelData)event.getSource());

											boolean isEnabled = true;
											if (!field.isEnabled())
												field.setEnabled(isEnabled);
										}
									}
								};
								
								bindings.put(f.getName(), b);
							}
						}
						else {
						}
					}
				}
			};
		}
	}


	private void verifyFieldState(Field field, ModelData model) {
		String dropFlag = DataTypeConversionUtil.buildDroppedKey(field.getName());

		Boolean dropFlagValue = model.get(dropFlag);
		boolean isDropped = dropFlagValue != null && dropFlagValue.booleanValue();

		if (isDropped) {
			field.setData(FIELD_STATE_FIELD, Boolean.TRUE);
			field.addInputStyleName(resources.css().gbCellDropped());
			scoresDropped ++;
		} else {
			dropFlagValue = field.getData(FIELD_STATE_FIELD);
			isDropped = dropFlagValue != null && dropFlagValue.booleanValue();
			if (isDropped)
				field.removeInputStyleName(resources.css().gbCellDropped());
		}
	}


	public static class FlexTableContainer extends WidgetComponent {

		private FlexTable table;

		public FlexTableContainer(FlexTable table) {
			super(table);
			this.table = table;
		}

		public FlexCellFormatter getFlexCellFormatter() {
			return table.getFlexCellFormatter();
		}

		public void setText(int row, int column, String text) {
			table.setText(row, column, text);
		}

		public void setWidget(int row, int column, Widget widget) {
			table.setWidget(row, column, widget);
		}
	}


	public void updateLearnerItems(ListStore<ModelData> store,
			TreeStore<ItemModel> treeStore) {
		onChangeModel(store, treeStore, this.learner);
		
	}
}
