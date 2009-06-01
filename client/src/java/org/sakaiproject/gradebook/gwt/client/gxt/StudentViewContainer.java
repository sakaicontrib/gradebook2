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
package org.sakaiproject.gradebook.gwt.client.gxt;

import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.model.GradeRecordModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

public class StudentViewContainer extends ContentPanel {
	
	private TextField<String> defaultTextField= new TextField<String>();
	private TextArea defaultTextArea = new TextArea();
	private NumberFormat defaultNumberFormat = NumberFormat.getFormat("#.###");
	private NumberField defaultNumberField = new NumberField();
    private FlexTable studentInformation, gradeInformation;
    private ContentPanel studentInformationPanel, gradeInformationPanel;

	private StudentModel learnerGradeRecordCollection;
	
	private boolean isStudentView;
	
	private GradebookModel selectedGradebook;
	
	public StudentViewContainer(boolean isStudentView) {
		this.isStudentView = isStudentView;
		this.defaultNumberField.setFormat(defaultNumberFormat);
		this.defaultNumberField.setSelectOnFocus(true);
		this.defaultNumberField.addInputStyleName("gbNumericFieldInput");
		this.defaultTextArea.addInputStyleName("gbTextAreaInput");
		this.defaultTextField.addInputStyleName("gbTextFieldInput");
		
		setFrame(true);
		setHeaderVisible(false);
		setLayout(new RowLayout());

		studentInformation = new FlexTable(); 
		studentInformation.setStyleName("gbStudentInformation");
		studentInformationPanel = new ContentPanel();
		studentInformationPanel.setBorders(true);
		studentInformationPanel.setFrame(true);
		studentInformationPanel.setHeaderVisible(false);
		studentInformationPanel.setHeading("Individual Grade Summary");
		studentInformationPanel.setLayout(new FitLayout());
		studentInformationPanel.setScrollMode(Scroll.AUTO);
		studentInformationPanel.add(studentInformation);
		add(studentInformationPanel, new RowData(-1, -1, new Margins(5, 0, 0, 0)));

		gradeInformation = new FlexTable();
		gradeInformation.setStyleName("gbStudentInformation");
		gradeInformationPanel = new ContentPanel();
		gradeInformationPanel.setBorders(true);
		gradeInformationPanel.setFrame(true);
		gradeInformationPanel.setHeaderVisible(false);
		gradeInformationPanel.setLayout(new FlowLayout());
		gradeInformationPanel.setScrollMode(Scroll.AUTO);
		gradeInformationPanel.add(gradeInformation);
		add(gradeInformationPanel, new RowData(1, 1, new Margins(5, 0, 0, 0)));
		
	}
	
	public StudentModel getStudentRow() {
		return learnerGradeRecordCollection;
	}
	
	public void onChangeModel(GradebookModel selectedGradebook, StudentModel learnerGradeRecordCollection) {
		if (learnerGradeRecordCollection != null) {
			this.selectedGradebook = selectedGradebook;
			this.learnerGradeRecordCollection = learnerGradeRecordCollection;
			
			updateCourseGrade(learnerGradeRecordCollection.getStudentGrade());
			
			setStudentInfoTable();
			
			setGradeInfoTable(selectedGradebook, learnerGradeRecordCollection);
		}
	}
	
	public void onItemUpdated(ItemModel itemModel) {
		//refreshData();
	}
	
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
	
		//if (gradeItemsPanel != null)
		//	gradeItemsPanel.getLoader().load(0, pageSize);
		
	}
	
	public void onResize(int x, int y) {
		super.onResize(x, y);
		
		/*if (gradeItemsPanel != null && gradeItemsPanel.getBody() != null) {
			int h = 31; //e.getOffsetHeight();
			int numRows = gradeItemsPanel.getBody().getHeight() / h - 1;
			
			if (pageSize != numRows) {
				pageSize = numRows;
				if (gradeItemsPanel != null){
					gradeItemsPanel.getLoader().load(0, pageSize);
					
					if (gradeItemsPanel.getToolBar() != null) {
						gradeItemsPanel.getToolBar().setPageSize(numRows);		
					}
				}
			}
		}*/
	}
	
	public void refreshColumns() {
		/*CustomColumnModel cm = new CustomColumnModel(selectedGradebook.getGradebookUid(), GRID_ID, buildColumns(selectedGradebook));
		EditorGrid<GradeRecordModel> grid = gradeItemsPanel.getGrid();
		if (grid != null) {
			grid.reconfigure(gradeItemsPanel.getStore(), gradeItemsPanel.getColumnModel());
			if (grid.el() != null) {
				grid.el().unmask();
			}
		}*/
	}
	
	public void refreshData() {
		//if (gradeItemsPanel.getToolBar() != null)
		//	gradeItemsPanel.getToolBar().refresh();
	}
	
	/*private GridPanel<GradeRecordModel> newGradeItemsPanel() {
		GridPanel<GradeRecordModel> gradeItemsPanel = new GridPanel<GradeRecordModel>(GRID_ID, EntityType.GRADE_RECORD) {

			@Override
			protected CustomColumnModel newColumnModel(GradebookModel selectedGradebook) {
				GradebookModel model = Registry.get(AppConstants.CURRENT);
				return new CustomColumnModel(model.getGradebookUid(), gridId, buildColumns(model));
			}

			@Override
			protected GridView newGridView() {
				GridView gridView = new BaseCustomGridView() {
					
					protected boolean isDropped(ModelData model, String property) {
						GradeRecordModel.Key recordModelKey = GradeRecordModel.Key.valueOf(property);
						switch (recordModelKey) {
						case POINTS_EARNED:
						case PERCENT_EARNED:
						case LETTER_EARNED:
							Boolean isDropped = model.get(GradeRecordModel.Key.DROPPED.name());
							Boolean isExcused = model.get(GradeRecordModel.Key.EXCLUDED.name());
							return (isDropped != null && isDropped.booleanValue()) || (isExcused != null && isExcused.booleanValue());
						}
						return false;
					}
					
					protected void onCellSelect(int row, int col) {

					}
				};
				
				return gridView;
			}

			@Override
			protected PageRequestAction newPageRequestAction(GradebookModel selectedGradebook) {
				PageRequestAction pageRequestAction = new PageRequestAction(entityType, selectedGradebook.getGradebookUid(), selectedGradebook.getGradebookId());
				StudentModel studentModel = getStudentModel();
				if (studentModel != null)
					pageRequestAction.setStudentUid(studentModel.getIdentifier());
				
				// The student view is a more restricted view, therefore it's opposite is include all
				pageRequestAction.setIncludeAll(Boolean.valueOf(!StudentViewContainer.this.isStudentView));
				
				return pageRequestAction;
			}

			@Override
			protected ListStore<GradeRecordModel> newStore(
					BasePagingLoader<PagingLoadConfig, PagingLoadResult<GradeRecordModel>> loader) {
				
				ListStore<GradeRecordModel> store = new ListStore<GradeRecordModel>(loader) {
					protected void registerModel(GradeRecordModel model) {
						super.registerModel(model);
					
						syncStudentModel(model);
					}
				}; 
				store.setModelComparer(new EntityModelComparer<GradeRecordModel>());
				
				return store;
			}
			
			
			@Override
			protected void updateView(UserEntityAction<GradeRecordModel> action, Record record, GradeRecordModel model) {
				//GradebookModel gbModel = Registry.get(gradebookUid);
				
				Object value = null;
				switch (selectedGradebook.getGradeType()) {
				case POINTS:
					value = model.get(GradeRecordModel.Key.POINTS_EARNED.name());
					record.set(GradeRecordModel.Key.POINTS_EARNED.name(), value); 
					break;
				case PERCENTAGES:
					value = model.get(GradeRecordModel.Key.PERCENT_EARNED.name());
					record.set(GradeRecordModel.Key.PERCENT_EARNED.name(), value);
					break;
				case LETTERS:
					value = model.get(GradeRecordModel.Key.LETTER_EARNED.name());
					record.set(GradeRecordModel.Key.LETTER_EARNED.name(), value);
					break;
				}
				
				record.set(GradeRecordModel.Key.DROPPED.name(), model.getDropped());
				if (model.getComments() != null)
					record.set(GradeRecordModel.Key.COMMENTS.name(), model.getComments());
				record.set(GradeRecordModel.Key.WEIGHT.name(), model.getWeight());
				record.set(GradeRecordModel.Key.LOG.name(), model.getLog());
				
				updateCourseGrade(model.getCourseGrade());
			}
			
			@Override
			protected void afterUpdateView(UserEntityAction<GradeRecordModel> action, Record record, GradeRecordModel model) {
							
				Boolean oldDropped = (Boolean)record.getChanges().get(GradeRecordModel.Key.DROPPED.name());
				boolean wasDropped = oldDropped != null && oldDropped.booleanValue();
				boolean isDropped = model.getDropped() != null && model.getDropped().booleanValue();
							
				if (isDropped && !wasDropped)
					pagingToolBar.refresh();
				else if (wasDropped && !isDropped)
					pagingToolBar.refresh();
				else 
					syncStudentModel(model);
							
				action.setStudentModel(learnerGradeRecordCollection);
				action.setModel(model);
				UserChangeEvent event = new UserChangeEvent(action);
				StudentViewContainer.this.fireEvent(GradebookEvents.UserChange, event);
				
				GradeRecordModel.Key recordModelKey = GradeRecordModel.Key.valueOf(action.getKey());
				
				boolean isExcused = model.getExcluded() == null ? false : model.getExcluded().booleanValue();

				switch (recordModelKey) {
				case COMMENTS:
					notifier.notify("Comment", "Commented on {0} for {1}. ", model.getAssignmentName(), learnerGradeRecordCollection.getStudentName());
					break;
				case EXCLUDED:
					if (isExcused)
						notifier.notify("Excuse", "Excused {0} from {1}. Student will no longer be graded for the assignment. ", learnerGradeRecordCollection.getStudentName(), model.getAssignmentName());
					else
						notifier.notify("Unexcuse", "Unexcused {0} from {1}. Student will now be graded for the assignment. ", learnerGradeRecordCollection.getStudentName(), model.getAssignmentName());
					break;
				case POINTS_EARNED:
				case PERCENT_EARNED:
				case LETTER_EARNED:
					StringBuilder buffer = new StringBuilder();
					buffer.append(learnerGradeRecordCollection.getStudentName());
					buffer.append(" : ");
					buffer.append(model.getAssignmentName());
						
					notifier.notify(buffer.toString(), 
							"Stored item grade as '{0}' and recalculated course grade to '{1}' ", 
							model.get(action.getKey()), model.getCourseGrade());
					
					break;
				};
			}
			
			@Override
			protected UserEntityUpdateAction<GradeRecordModel> newEntityUpdateAction(GradebookModel selectedGradebook, Record record, String property, Object value, Object startValue, GridEvent gridEvent) {
				UserEntityUpdateAction<GradeRecordModel> action = super.newEntityUpdateAction(selectedGradebook, record, property, value, startValue, gridEvent);
				action.setStudentModel(getStudentModel());
				
				return action;
			}
			
		};
		
		EditorGrid<GradeRecordModel> grid = gradeItemsPanel.getGrid();
		CustomColumnModel cm = gradeItemsPanel.getColumnModel();
		if (!isStudentView) {
			ExcludeColumnConfig excludeColumn = (ExcludeColumnConfig)cm.getColumnById(GradeRecordModel.Key.EXCLUDED.name());
			LogColumnConfig logColumn = (LogColumnConfig)cm.getColumnById(GradeRecordModel.Key.LOG.name());
			grid.addPlugin(excludeColumn);
			grid.addPlugin(logColumn);
		}
		gradeItemsPanel.setHeaderVisible(false);
		gradeItemsPanel.setHeading("Categories/Assignments");
		gradeItemsPanel.setTopComponent(gradeItemsPanel.getPagingToolBar());
		//gradeItemsPanel.setLayout(new FitLayout());
		//gradeItemsPanel.setScrollMode(Scroll.AUTO);
		//Grid<AssignmentRecordModel> gradeItemsGrid = buildGrid(gradeItemsPanel);
		//gradeItemsPanel.add(gradeItemsGrid);
		add(gradeItemsPanel, new RowData(1, 1));
		
		return gradeItemsPanel;
	}*/
	
	private void updateCourseGrade(String newGrade)
	{
		if (!isStudentView || (DataTypeConversionUtil.checkBoolean(selectedGradebook.getGradebookItemModel().getReleaseGrades()))) {
			// To force a refresh, let's first hide the owning panel
			studentInformationPanel.hide();
			studentInformation.setText(6, 1, newGrade);
			studentInformationPanel.show();
		}
		
		learnerGradeRecordCollection.set(StudentModel.Key.COURSE_GRADE.name(), newGrade);
	}
	
	// FIXME - i18n 
	// FIXME - need to assess impact of doing it this way... 
	
	private void setStudentInfoTable() {		
		// To force a refresh, let's first hide the owning panel
		studentInformationPanel.hide();
	
		// Now, let's update the student information table
		FlexCellFormatter formatter = studentInformation.getFlexCellFormatter();
		
		/*studentInformation.setText(0, 0, "Individual Grade Summary");
		formatter.setColSpan(0, 0, 2);
		formatter.setStyleName(0, 0, "gbHeader");*/
		
        studentInformation.setText(1, 0, "Name");
        formatter.setStyleName(1, 0, "gbImpact");
        studentInformation.setText(1, 1, learnerGradeRecordCollection.getStudentName());

        studentInformation.setText(2, 0, "Email");
        formatter.setStyleName(2, 0, "gbImpact");
        studentInformation.setText(2, 1, learnerGradeRecordCollection.getStudentEmail());

        studentInformation.setText(3, 0, "ID");
        formatter.setStyleName(3, 0, "gbImpact");
        studentInformation.setText(3, 1, learnerGradeRecordCollection.getStudentDisplayId());

        studentInformation.setText(4, 0, "Section");
        formatter.setStyleName(4, 0, "gbImpact");
        studentInformation.setText(4, 1, learnerGradeRecordCollection.getStudentSections());
    
        studentInformation.setText(5, 0, "");
        formatter.setColSpan(5, 0, 2);
        
        if (!isStudentView || (DataTypeConversionUtil.checkBoolean(selectedGradebook.getGradebookItemModel().getReleaseGrades()))) {
	        studentInformation.setText(6, 0, "Course Grade");
	        formatter.setStyleName(6, 0, "gbImpact");
	        studentInformation.setText(6, 1, learnerGradeRecordCollection.getStudentGrade());
        }
        studentInformationPanel.show();
	}

	private void populateGradeInfoRow(int row, ItemModel item, StudentModel learner, FlexCellFormatter formatter) {
		String itemId = item.getIdentifier();
		Double value = learner.get(itemId);
		String commentFlag = new StringBuilder().append(itemId).append(StudentModel.COMMENT_TEXT_FLAG).toString();
		String comment = learner.get(commentFlag);
		String excusedFlag = new StringBuilder().append(itemId).append(StudentModel.DROP_FLAG).toString();
		boolean isExcused = DataTypeConversionUtil.checkBoolean((Boolean)learner.get(excusedFlag));
		
		gradeInformation.setText(row, 0, item.getName());
        formatter.setStyleName(row, 0, "gbRecordLabel");
        formatter.setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);
        
        StringBuilder resultBuilder = new StringBuilder();
        if (value == null)
        	resultBuilder.append("Ungraded");
        else
        	resultBuilder.append(NumberFormat.getDecimalFormat().format(value));
        if (isExcused)
        	resultBuilder.append(" (excluded from grade)");
        gradeInformation.setText(row, 1, resultBuilder.toString());
        formatter.setStyleName(row, 1, "gbRecordField");
        gradeInformation.setText(row, 2, comment);
        formatter.setStyleName(row, 2, "gbRecordField");
        
	}
	
	private void setGradeInfoTable(GradebookModel selectedGradebook, StudentModel learner) {		
		// To force a refresh, let's first hide the owning panel
		gradeInformationPanel.hide();
	
		// Now, let's update the student information table
		FlexCellFormatter formatter = gradeInformation.getFlexCellFormatter();
		
		gradeInformation.clear();
		
		ItemModel gradebookItemModel = selectedGradebook.getGradebookItemModel();
		
		int row=0;
		for (ItemModel child : gradebookItemModel.getChildren()) {
			
			switch (child.getItemType()) {
			case CATEGORY:
				boolean isCategoryHeaderDisplayed = false;
				
				for (ItemModel item : child.getChildren()) {
					if (DataTypeConversionUtil.checkBoolean(item.getReleased())) {
						
						if (!isCategoryHeaderDisplayed) {
							if (selectedGradebook.getGradebookItemModel().getCategoryType() != CategoryType.NO_CATEGORIES) {
								gradeInformation.setText(row, 0, child.getName());
						        formatter.setStyleName(row, 0, "gbHeader");
						        row++;
							}
							isCategoryHeaderDisplayed = true;
						}
						
						populateGradeInfoRow(row, item, learner, formatter);
						row++;
					}
				}
				break;
			case ITEM:
				if (DataTypeConversionUtil.checkBoolean(child.getReleased())) {
					populateGradeInfoRow(row, child, learner, formatter);
					row++;
				}
				break;
			}
		}

        gradeInformationPanel.show();
	}
	
	private GridCellRenderer<GradeRecordModel> numericCellRenderer = new GridCellRenderer<GradeRecordModel>() {

		public String render(GradeRecordModel model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<GradeRecordModel> store) {
			
			Double value = model.get(property);
			
			if (value == null)
				return "&nbsp;";
			
			return defaultNumberFormat.format(value.doubleValue());
		}
	};
	
	private GridCellRenderer<GradeRecordModel> textCellRenderer = new GridCellRenderer<GradeRecordModel>() {

		public String render(GradeRecordModel model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<GradeRecordModel> store) {
			
			Object value = model.get(property);
			
			if (value == null)
				return "&nbsp;";
			
			return value.toString();
		}
	};
	
	private GridCellRenderer<GradeRecordModel> disabledNumericCellRenderer = new GridCellRenderer<GradeRecordModel>() {

		public String render(GradeRecordModel model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<GradeRecordModel> store) {
			
			Double value = model.get(property);
			
			if (value == null)
				return "&nbsp;";
			
			return "<span class=\"gbUneditable\">" + defaultNumberFormat.format(value) + "</span>";
		}
		
	};
	
	private GridCellRenderer<GradeRecordModel> disabledTextCellRenderer = new GridCellRenderer<GradeRecordModel>() {

		public String render(GradeRecordModel model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<GradeRecordModel> store) {
			
			Object value = model.get(property);
			
			if (value == null)
				return "&nbsp;";
			
			return "<span class=\"gbUneditable\">" +  value.toString() + "</span>";
		}
	};
	
	/*private CellEditor numericCellEditor = new CellEditor(defaultNumberField);

	private CellEditor textCellEditor = new CellEditor(defaultTextField);
	
	private List<ColumnConfig> buildColumns(GradebookModel selectedGradebook) {

		// FIXME  - i18n 
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();  
		
		ColumnConfig categoryColumn = new ColumnConfig(GradeRecordModel.Key.CATEGORY_NAME.name(), "Category", 120);
		categoryColumn.setHidden(true);
		//categoryColumn.setMenuDisabled(true);
		columns.add(categoryColumn);
		
		ColumnConfig nameColumn = new ColumnConfig(GradeRecordModel.Key.ASSIGNMENT_NAME.name(), "Name", 220);
		nameColumn.setGroupable(false);
		nameColumn.setSortable(false);
		columns.add(nameColumn);

		switch (selectedGradebook.getGradeType()) {
		case POINTS:
			ColumnConfig pointsColumn =  new ColumnConfig(GradeRecordModel.Key.POINTS_EARNED.name(), "Points", 80);
			pointsColumn.setAlignment(HorizontalAlignment.RIGHT);
			if (!isStudentView)
				pointsColumn.setEditor(numericCellEditor);
			pointsColumn.setGroupable(false);
			//pointsColumn.setMenuDisabled(true);
			pointsColumn.setNumberFormat(defaultNumberFormat);
			pointsColumn.setSortable(false);
			pointsColumn.setRenderer(numericCellRenderer);
			columns.add(pointsColumn);
			break;
		case PERCENTAGES:
			ColumnConfig percentColumn =  new ColumnConfig(GradeRecordModel.Key.PERCENT_EARNED.name(), "Percent", 80);
			percentColumn.setAlignment(HorizontalAlignment.RIGHT);
			if (!isStudentView)
				percentColumn.setEditor(numericCellEditor);
			percentColumn.setGroupable(false);
			//percentColumn.setMenuDisabled(true);
			percentColumn.setNumberFormat(defaultNumberFormat);
			percentColumn.setRenderer(numericCellRenderer);
			percentColumn.setSortable(false);
			columns.add(percentColumn);
			break;
		case LETTERS:
			ColumnConfig letterColumn =  new ColumnConfig(GradeRecordModel.Key.LETTER_EARNED.name(), "Letter", 80);
			letterColumn.setAlignment(HorizontalAlignment.LEFT);
			if (!isStudentView)
				letterColumn.setEditor(textCellEditor);
			letterColumn.setGroupable(false);
			//letterColumn.setMenuDisabled(true);
			letterColumn.setRenderer(textCellRenderer);
			letterColumn.setSortable(false);
			columns.add(letterColumn);
		};
		
		ColumnConfig maxPointsColumn = new ColumnConfig(GradeRecordModel.Key.MAX_POINTS.name(), "Max Value", 60);
		maxPointsColumn.setGroupable(false);
		maxPointsColumn.setSortable(false);
		maxPointsColumn.setNumberFormat(defaultNumberFormat);
		maxPointsColumn.setAlignment(HorizontalAlignment.RIGHT);
		maxPointsColumn.setRenderer(disabledNumericCellRenderer);
		columns.add(maxPointsColumn);
		
		ColumnConfig weightingColumn = new ColumnConfig(GradeRecordModel.Key.WEIGHT.name(), "Percent of Grade", 100);
		weightingColumn.setGroupable(false);
		weightingColumn.setSortable(false);
		weightingColumn.setNumberFormat(defaultNumberFormat);
		weightingColumn.setAlignment(HorizontalAlignment.RIGHT);
		weightingColumn.setRenderer(disabledNumericCellRenderer);
		columns.add(weightingColumn);
		
		if (!isStudentView) {
			logColumn = new LogColumnConfig(GradeRecordModel.Key.LOG.name(), "Log", 60);
			logColumn.setGroupable(false);
			logColumn.setSortable(false);
			columns.add(logColumn);
			
			ExcludeColumnConfig excludeColumn =  new ExcludeColumnConfig(GradeRecordModel.Key.EXCLUDED.name(), "Excuse", 60);
			excludeColumn.setGroupable(false);
			//excludeColumn.setMenuDisabled(true);
			excludeColumn.setSortable(false);
			columns.add(excludeColumn);
		}
		
		ColumnConfig commentColumn = new ColumnConfig(GradeRecordModel.Key.COMMENTS.name(), "Comments", 500);
		commentColumn.setGroupable(false);
		commentColumn.setSortable(false);
		commentColumn.setAlignment(HorizontalAlignment.LEFT);
		commentColumn.setRenderer(textCellRenderer);
		// The problem here is that, when we disable the text area it may cut off text that the student
		// needs to see.
		if (!isStudentView)
			commentColumn.setEditor(new CellEditor(defaultTextArea));
		columns.add(commentColumn);
		
		return columns;
	}*/
	
	/*
	private Grid<AssignmentRecordModel> buildGrid(ContentPanel panel) {
		final GradebookModel gbModel = Registry.get(gradebookUid);
		final GradebookToolFacadeAsync service = Registry.get("service");
		
		RpcProxy<PagingLoadConfig, PagingLoadResult<AssignmentRecordModel>> proxy = new RpcProxy<PagingLoadConfig, PagingLoadResult<AssignmentRecordModel>>() {
			@Override
			protected void load(PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<AssignmentRecordModel>> callback) {
				GradebookModel model = Registry.get(gradebookUid);
				service.getAssignmentRecords(model.getGradebookUid(), model.getGradebookId(), studentModel.getIdentifier(), Boolean.valueOf(isStudentView), loadConfig, callback);
			}
		};
		
		loader = 
			new BasePagingLoader<PagingLoadConfig, PagingLoadResult<AssignmentRecordModel>>(proxy, new ModelReader<PagingLoadConfig>());
		
		loader.setRemoteSort(true);

		toolBar = new PagingToolBar(pageSize) {
			public void refresh() {
				super.refresh();
				
				// Need to make sure that we keep the StudentModel synced with the AssignmentRecordModel data
				// this is a bit of a hack, but seems like the most consistent and reliable way to 
				// ensure that all of the drop/excuse information gets transmitted without unnecessary
				// round-trips to the server
				
				//refreshStudentModel();
				
			}
		};
		toolBar.bind(loader);

		panel.setTopComponent(toolBar);
		
		store = new ListStore<AssignmentRecordModel>(loader) {
			protected void registerModel(AssignmentRecordModel model) {
				super.registerModel(model);
			
				syncStudentModel(model);
			}
		}; 
		store.setModelComparer(new SimpleModelComparer<AssignmentRecordModel>());
		
		cm = new ColumnModel(buildColumns());

		GridView gridView = new BaseCustomGridView() {
			
			protected boolean isDropped(ModelData model, String property) {
				AssignmentRecordModel.Key recordModelKey = AssignmentRecordModel.Key.valueOf(property);
				switch (recordModelKey) {
				case POINTS_EARNED:
				case PERCENT_EARNED:
				case LETTER_EARNED:
					Boolean isDropped = model.get(AssignmentRecordModel.Key.DROPPED.name());
					Boolean isExcused = model.get(AssignmentRecordModel.Key.EXCLUDED.name());
					return (isDropped != null && isDropped.booleanValue()) || (isExcused != null && isExcused.booleanValue());
				}
				return false;
			}
			
			protected void onCellSelect(int row, int col) {

			}
		};

		grid = new EditorGrid<AssignmentRecordModel>(store, cm);

		if (!isStudentView) {
			ExcludeColumnConfig excludeColumn = (ExcludeColumnConfig)cm.getColumnById(AssignmentRecordModel.Key.EXCLUDED.name());
			LogColumnConfig logColumn = (LogColumnConfig)cm.getColumnById(AssignmentRecordModel.Key.LOG.name());
			grid.addPlugin(excludeColumn);
			grid.addPlugin(logColumn);
		}
		
		grid.setAutoExpandColumn(AssignmentRecordModel.Key.COMMENTS.name());
		grid.setBorders(true);
		grid.setLoadMask(true);
		grid.setStripeRows(true);
		grid.setView(gridView);
		grid.getView().setShowDirtyCells(false);
		
		grid.addListener(Events.ValidateEdit, new Listener<GridEvent>() {

			public void handleEvent(final GridEvent ge) {
				GradebookToolFacadeAsync service = Registry.get("service");
				final AssignmentRecordModel assign = (AssignmentRecordModel) ge.record.getModel();
				ge.doit = false;
				
				makeChange(ge.record, ge.property, ge.value, ge.startValue, ge);
			}
			
		});
		
		
		return grid;
	}*/
	
	/*private List<ColumnConfig> buildSummaryColumns() {
		GradebookModel gbModel = Registry.get(gradebookUid);
		
		// FIXME  - i18n 
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();  
		
		SummaryColumnConfig categoryColumn = new SummaryColumnConfig(AssignmentRecordModel.Key.CATEGORY_NAME.name(), "Category", 120);
		categoryColumn.setHidden(true);
		categoryColumn.setMenuDisabled(true);
		columns.add(categoryColumn);
		
		SummaryColumnConfig nameColumn = new SummaryColumnConfig(AssignmentRecordModel.Key.ASSIGNMENT_NAME.name(), "Name", 240);
		nameColumn.setGroupable(false);
		nameColumn.setSortable(false);
		columns.add(nameColumn);

		switch (gbModel.getGradeType()) {
		case POINTS:
			SummaryColumnConfig pointsColumn =  new SummaryColumnConfig(AssignmentRecordModel.Key.POINTS_EARNED.name(), "Grade", 120);
			pointsColumn.setAlignment(HorizontalAlignment.RIGHT);
			if (!isStudentView)
				pointsColumn.setEditor(new CellEditor(defaultNumberField));
			pointsColumn.setGroupable(false);
			pointsColumn.setMenuDisabled(true);
			pointsColumn.setNumberFormat(defaultNumberFormat);
			pointsColumn.setSortable(false);
			columns.add(pointsColumn);
			break;
		case PERCENTAGES:
			SummaryColumnConfig percentColumn =  new SummaryColumnConfig(AssignmentRecordModel.Key.PERCENT_EARNED.name(), "Grade", 120);
			percentColumn.setAlignment(HorizontalAlignment.RIGHT);
			if (!isStudentView)
				percentColumn.setEditor(new CellEditor(defaultNumberField));
			percentColumn.setGroupable(false);
			percentColumn.setMenuDisabled(true);
			percentColumn.setNumberFormat(defaultNumberFormat);
			percentColumn.setSortable(false);
			columns.add(percentColumn);
			break;
		case LETTERS:
			SummaryColumnConfig letterColumn =  new SummaryColumnConfig(AssignmentRecordModel.Key.LETTER_EARNED.name(), "Grade", 120);
			letterColumn.setAlignment(HorizontalAlignment.LEFT);
			if (!isStudentView)
				letterColumn.setEditor(new CellEditor(new TextField<String>()));
			letterColumn.setGroupable(false);
			letterColumn.setMenuDisabled(true);
			letterColumn.setSortable(false);
			columns.add(letterColumn);
		};
		
		if (!isStudentView) {
			
			SummaryColumnConfig maxPointsColumn = new SummaryColumnConfig(AssignmentRecordModel.Key.MAX_POINTS.name(), "Max Value", 120);
			maxPointsColumn.setGroupable(false);
			maxPointsColumn.setSortable(false);
			maxPointsColumn.setNumberFormat(defaultNumberFormat);
			maxPointsColumn.setAlignment(HorizontalAlignment.RIGHT);
			maxPointsColumn.setRenderer(new GridCellRenderer() {
	
				public String render(ModelData model, String property,
						ColumnData config, int rowIndex, int colIndex,
						ListStore store) {
					
					return "<span class=\"gbUneditable\">" + defaultNumberFormat.format((Double)model.get(property)) + "</span>";
				}
				
			});
			columns.add(maxPointsColumn);
			
			SummaryColumnConfig weightingColumn = new SummaryColumnConfig(AssignmentRecordModel.Key.WEIGHT.name(), "Percent of Grade", 150);
			weightingColumn.setGroupable(false);
			weightingColumn.setSortable(false);
			weightingColumn.setNumberFormat(defaultNumberFormat);
			weightingColumn.setAlignment(HorizontalAlignment.RIGHT);
			weightingColumn.setRenderer(new GridCellRenderer() {
	
				public String render(ModelData model, String property,
						ColumnData config, int rowIndex, int colIndex,
						ListStore store) {
					
					return "<span class=\"gbUneditable\">" + defaultNumberFormat.format((Double)model.get(property)) + "</span>";
				}
				
			});
			columns.add(weightingColumn);
		
			SummaryLogColumnConfig logColumn = new SummaryLogColumnConfig(AssignmentRecordModel.Key.LOG.name(), "Log", 100, logDialog);
			logColumn.setGroupable(false);
			logColumn.setSortable(false);
			columns.add(logColumn);
			
			SummaryExcludeColumnConfig excludeColumn =  new SummaryExcludeColumnConfig(AssignmentRecordModel.Key.EXCLUDED.name(), "Excuse", 100);
			excludeColumn.setGroupable(false);
			excludeColumn.setMenuDisabled(true);
			excludeColumn.setSortable(false);
			columns.add(excludeColumn);
		}
		
		SummaryColumnConfig commentColumn = new SummaryColumnConfig(AssignmentRecordModel.Key.COMMENTS.name(), "Comments", 500);
		commentColumn.setGroupable(false);
		commentColumn.setSortable(false);
		commentColumn.setAlignment(HorizontalAlignment.LEFT);
		if (!isStudentView)
			commentColumn.setEditor(new CellEditor(new TextArea()));
		columns.add(commentColumn);
		
		return columns;
	}
	
	private Grid<AssignmentRecordModel> buildSummaryGrid(ContentPanel panel) {
		final GradebookModel gbModel = Registry.get(gradebookUid);
		final GradebookToolFacadeAsync service = Registry.get("service");
		
		RpcProxy<PagingLoadConfig, PagingLoadResult<AssignmentRecordModel>> proxy = new RpcProxy<PagingLoadConfig, PagingLoadResult<AssignmentRecordModel>>() {
			@Override
			protected void load(PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<AssignmentRecordModel>> callback) {
				GradebookModel model = Registry.get(gradebookUid);
				service.getAssignmentRecords(model.getGradebookUid(), model.getGradebookId(), studentModel.getStudentUid(), isStudentView, loadConfig, callback);
			}
		};
		
		loader = 
			new BasePagingLoader<PagingLoadConfig, PagingLoadResult<AssignmentRecordModel>>(proxy, new ModelReader<PagingLoadConfig>());
		
		loader.setRemoteSort(true);

		final PagingToolBar toolBar = new PagingToolBar(pageSize);
		toolBar.bind(loader);

		panel.setTopComponent(toolBar);
		
		final GroupingStore<AssignmentRecordModel> store = new GroupingStore<AssignmentRecordModel>(loader);  
		store.groupBy(AssignmentRecordModel.Key.CATEGORY_NAME.name());
		store.setModelComparer(new SimpleModelComparer<AssignmentRecordModel>());
		
		cm = new ColumnModel(buildSummaryColumns());

		GroupSummaryView summary = new GroupSummaryView();
		summary.setShowGroupedColumn(false);

		
		grid = new EditorGrid<AssignmentRecordModel>(store, cm);

		if (!isStudentView) {
			SummaryExcludeColumnConfig excludeColumn = (SummaryExcludeColumnConfig)cm.getColumnById(AssignmentRecordModel.Key.EXCLUDED.name());
			SummaryLogColumnConfig logColumn = (SummaryLogColumnConfig)cm.getColumnById(AssignmentRecordModel.Key.LOG.name());
			grid.addPlugin(excludeColumn);
			grid.addPlugin(logColumn);
		}
		
		grid.setBorders(true);
		grid.setLoadMask(true);
		grid.setStripeRows(true);
		grid.setView(summary);
		grid.getView().setShowDirtyCells(false);
		
		grid.addListener(Events.ValidateEdit, new Listener<GridEvent>() {

			public void handleEvent(final GridEvent ge) {
				GradebookToolFacadeAsync service = Registry.get("service");
				final AssignmentRecordModel assign = (AssignmentRecordModel) ge.record.getModel();
				ge.doit = false;
				
				ColumnConfig config = cm.getColumn(ge.colIndex);
				
				
				if (config.getId().equals(AssignmentRecordModel.Key.COMMENTS.name())) {
					grid.getView().getCell(ge.rowIndex, ge.colIndex).setInnerText("Saving comment...");
					
					NotifyingAsyncCallback<AssignmentRecordModel> callback = new NotifyingAsyncCallback<AssignmentRecordModel>() {
						
						public void onSuccess(AssignmentRecordModel result) {
							ge.record.set(AssignmentRecordModel.Key.COMMENTS.name(), result.get(AssignmentRecordModel.Key.COMMENTS.name()));
							grid.fireEvent(Events.AfterEdit, ge);
							notifier.notify("Comment", "Commented on {0} for {1}. ", result.getAssignmentName(), studentModel.getStudentName());
						}
						
					};
					AssignmentRecordModel model = (AssignmentRecordModel)ge.record.getModel();
					service.updateGradeRecordModel(gradebookUid, gbModel.getGradebookId(), studentModel.getStudentUid(), model, AssignmentRecordModel.Key.COMMENTS, (String)ge.value, callback);
				} else {
					grid.getView().getCell(ge.rowIndex, ge.colIndex).setInnerText("Saving grade...");
					
					NotifyingAsyncCallback<AssignmentRecordModel> callback = new NotifyingAsyncCallback<AssignmentRecordModel>() {
						
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							// We have to fool the system into thinking that the value has changed, since
							// we snuck in that "Saving grade..." under the radar.
							ge.record.set(ge.property, null);
							ge.record.set(ge.property, ge.startValue);
						}

						public void onSuccess(AssignmentRecordModel result) {
							ColumnConfig config = cm.getColumn(ge.colIndex);
							
							Object value = null;
							
							if (config.getNumberFormat() != null) {
								GradebookModel gbModel = Registry.get(gradebookUid);
								
								switch (gbModel.getGradeType()) {
								case POINTS:
									value = result.get(AssignmentRecordModel.Key.POINTS_EARNED.name());
									ge.record.set(AssignmentRecordModel.Key.POINTS_EARNED.name(), value); 
									break;
								case PERCENTAGES:
									value = result.get(AssignmentRecordModel.Key.PERCENT_EARNED.name());
									ge.record.set(AssignmentRecordModel.Key.PERCENT_EARNED.name(), value);
									break;
								}
								
							} else {
								value = result.get(AssignmentRecordModel.Key.LETTER_EARNED.name());
								ge.record.set(AssignmentRecordModel.Key.LETTER_EARNED.name(), value);
							}
							
							ge.record.set(AssignmentRecordModel.Key.LOG.name(), result.getLog());
							ge.record.setDirty(true);
							
							ge.record.commit(false);
							grid.fireEvent(Events.AfterEdit, ge);
							
							
							updateCourseGrade(result.getCourseGrade());
							
							RefreshCourseGradesEvent rcge = new RefreshCourseGradesEvent(studentModel, result.getCourseGrade(), result.getAssignmentId(), value);
							StudentViewContainer.this.fireEvent(GradebookEvents.RefreshCourseGrades, rcge);
							
							// FIXME Update label for course grade once its in. ???
							StringBuilder buffer = new StringBuilder();
							buffer.append(result.getAssignmentName());
							buffer.append(" : ");
							buffer.append(studentModel.getStudentName());
							
							notifier.notify(buffer.toString(), 
									"Stored assignment grade as '{0}' and recalculated course grade to '{1}' ", 
									result.get(AssignmentRecordModel.Key.POINTS_EARNED.name()), result.getCourseGrade());
						}
						
					};
					
					if (config.getNumberFormat() != null) {
						Double value = (Double) ge.value;
						Double previousValue = (Double) ge.startValue;
						service.scoreNumericItem(gradebookUid, gbModel.getGradebookId(), studentModel.getStudentUid(), assign, value, previousValue, callback);
						ge.record.set(AssignmentRecordModel.Key.POINTS_EARNED.name(), null);
						ge.doit = false;
					} else {
						String value = (String) ge.value;
						String previousValue = (String) ge.startValue;
						service.scoreTextItem(gradebookUid, gbModel.getGradebookId(), studentModel.getStudentUid(), assign, value, previousValue, callback);
						ge.record.set(AssignmentRecordModel.Key.POINTS_EARNED.name(), null);
						ge.doit = false;
					}
				}
			}
			
		});
		
		
		return grid;
	}*/

	/*
	public class ExcludeColumnConfig extends CheckColumnConfig {
		
		
		public ExcludeColumnConfig(String id, String name, int width) {
			super(id, name, width);
		}
		
		protected void init() {
		    setRenderer(new GridCellRenderer() {
		      public String render(ModelData model, String property, ColumnData config, int rowIndex,
		          int colIndex, ListStore store) {
		        Boolean v = model.get(property);
		        String on = v != null && v.booleanValue() ? "-on" : "";
		        config.css = "x-grid3-check-col-td";
		        return "<div class='x-grid3-check-col" + on + " x-grid3-cc-" + getId() + "'>&#160;</div>";
		      }
		    });
		  }
		
		protected void onMouseDown(GridEvent ge) {
		    String cls = ge.getTarget().getClassName();
		    if (cls != null && cls.indexOf("x-grid3-cc-" + getId()) != -1) {
		      ge.stopEvent();
		      int index = grid.getView().findRowIndex(ge.getTarget());
		      ModelData m = grid.getStore().getAt(index);
		      Record r = grid.getStore().getRecord(m);
		      Boolean bool = (Boolean) m.get(getDataIndex());
		      boolean b = bool == null ? false : bool.booleanValue();
		      r.set(getDataIndex(), !b);
		      changeValue(r, getDataIndex(), Boolean.valueOf(!b), Boolean.valueOf(b));
		    }
		  }
		
		protected void changeValue(final Record record, final String property, Boolean value, Boolean startValue) {
			UserEntityUpdateAction<GradeRecordModel> action = 
				gradeItemsPanel.newEntityUpdateAction(selectedGradebook, record, property, value, startValue, null);
			
			RemoteCommand<GradeRecordModel> remoteCommand = 
				gradeItemsPanel.newRemoteCommand(record, property, value, startValue, null);
			
			gradeItemsPanel.doEdit(remoteCommand, action);
		}
	}*/
	
	/*
	 * ColumnConfig config = cm.getColumn(ge.colIndex);
				
				if (config.getId().equals(AssignmentRecordModel.Key.COMMENTS.name())) {
					grid.getView().getCell(ge.rowIndex, ge.colIndex).setInnerText("Saving edit...");
					NotifyingAsyncCallback<AssignmentRecordModel> callback = new NotifyingAsyncCallback<AssignmentRecordModel>() {
						
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							ge.record.set(AssignmentRecordModel.Key.COMMENTS.name(), null);
							ge.record.set(AssignmentRecordModel.Key.COMMENTS.name(), ge.startValue);
							grid.fireEvent(Events.AfterEdit, ge);
						}
						
						public void onSuccess(AssignmentRecordModel result) {
							ge.record.set(AssignmentRecordModel.Key.COMMENTS.name(), result.get(AssignmentRecordModel.Key.COMMENTS.name()));
							grid.fireEvent(Events.AfterEdit, ge);
							notifier.notify("Comment", "Commented on {0} for {1}. ", result.getAssignmentName(), studentModel.getStudentName());
						}
						
					};
					AssignmentRecordModel model = (AssignmentRecordModel)ge.record.getModel();
					service.updateGradeRecordModel(gradebookUid, gbModel.getGradebookId(), studentModel.getStudentUid(), model, AssignmentRecordModel.Key.COMMENTS, (String)ge.value, callback);
				} else {
					NotifyingAsyncCallback<AssignmentRecordModel> callback = new NotifyingAsyncCallback<AssignmentRecordModel>() {
						
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							
							
							ColumnConfig config = cm.getColumn(ge.colIndex);
							if (config.getNumberFormat() != null) {
								GradebookModel gbModel = Registry.get(gradebookUid);
								
								switch (gbModel.getGradeType()) {
								case POINTS:
									ge.record.set(AssignmentRecordModel.Key.POINTS_EARNED.name(), null);
									ge.record.set(AssignmentRecordModel.Key.POINTS_EARNED.name(), ge.startValue); 
									break;
								case PERCENTAGES:
									ge.record.set(AssignmentRecordModel.Key.PERCENT_EARNED.name(), null);
									ge.record.set(AssignmentRecordModel.Key.PERCENT_EARNED.name(), ge.startValue);
									break;
								}
								
							} else {
								ge.record.set(AssignmentRecordModel.Key.LETTER_EARNED.name(), null);
								ge.record.set(AssignmentRecordModel.Key.LETTER_EARNED.name(), ge.startValue);
							}
							
							grid.fireEvent(Events.AfterEdit, ge);
						}

						public void onSuccess(AssignmentRecordModel result) {
							
							ColumnConfig config = cm.getColumn(ge.colIndex);
							Object value = null;
							if (config.getNumberFormat() != null) {
								GradebookModel gbModel = Registry.get(gradebookUid);
								
								switch (gbModel.getGradeType()) {
								case POINTS:
									value = result.get(AssignmentRecordModel.Key.POINTS_EARNED.name());
									ge.record.set(AssignmentRecordModel.Key.POINTS_EARNED.name(), value); 
									break;
								case PERCENTAGES:
									value = result.get(AssignmentRecordModel.Key.PERCENT_EARNED.name());
									ge.record.set(AssignmentRecordModel.Key.PERCENT_EARNED.name(), value);
									break;
								}
								
							} else {
								value = result.get(AssignmentRecordModel.Key.LETTER_EARNED.name());
								ge.record.set(AssignmentRecordModel.Key.LETTER_EARNED.name(), value);
							}
							
							ge.record.set(AssignmentRecordModel.Key.WEIGHT.name(), result.get(AssignmentRecordModel.Key.WEIGHT.name()));
							
							//ge.record.commit(false);
							grid.fireEvent(Events.AfterEdit, ge);
							
							updateCourseGrade(result.getCourseGrade());
							
							RefreshCourseGradesEvent rcge = new RefreshCourseGradesEvent(studentModel, result.getCourseGrade(), result.getAssignmentId(), value);
							StudentViewContainer.this.fireEvent(GradebookEvents.RefreshCourseGrades, rcge);
							
							// FIXME Update label for course grade once its in. ???
							StringBuilder buffer = new StringBuilder();
							buffer.append(result.getAssignmentName());
							buffer.append(" : ");
							buffer.append(studentModel.getStudentName());
							
							notifier.notify(buffer.toString(), 
									"Stored assignment grade as '{0}' and recalculated course grade to '{1}' ", 
									result.get(AssignmentRecordModel.Key.POINTS_EARNED.name()), result.getCourseGrade());
						}
						
					};
					
					if (config.getNumberFormat() != null) {
						Double value = (Double) ge.value;
						Double previousValue = (Double) ge.startValue;
						service.scoreNumericItem(gradebookUid, gbModel.getGradebookId(), studentModel.getStudentUid(), assign, value, previousValue, callback);
						ge.record.set(AssignmentRecordModel.Key.POINTS_EARNED.name(), null);
						ge.doit = false;
					} else {
						String value = (String) ge.value;
						String previousValue = (String) ge.startValue;
						service.scoreTextItem(gradebookUid, gbModel.getGradebookId(), studentModel.getStudentUid(), assign, value, previousValue, callback);
						ge.record.set(AssignmentRecordModel.Key.POINTS_EARNED.name(), null);
						ge.doit = false;
					}
				}
	 */
	
	/*private void makeChange(final Record record, final String property, final Object value, 
			final Object startValue, final GridEvent gridEvent) {
		GradebookToolFacadeAsync service = (GradebookToolFacadeAsync) Registry.get("service");
		AssignmentRecordModel model = (AssignmentRecordModel)record.getModel();
		GradebookModel gbModel = Registry.get(gradebookUid);
	
		ClassType classType = ClassType.STRING;
		
		if (gridEvent != null) {
			ColumnConfig column = cm.getColumn(gridEvent.colIndex);
			
			if (! (column instanceof ExcludeColumnConfig))
				grid.getView().getCell(gridEvent.rowIndex, gridEvent.colIndex).setInnerText("Saving...");

			if (column.getEditor() != null && column.getEditor().getField() instanceof NumberField)
				classType = ClassType.DOUBLE;
			
			
		} else {
			classType = ClassType.BOOLEAN;
		}
			
		UserEntityUpdateAction action = 
			new UserEntityUpdateAction<AssignmentRecordModel>(gbModel.getGradebookUid(), model, property, classType, value, startValue);
		action.setStudentModel(studentModel);
		
		//UserGradeRecordEntityUpdateAction action = new UserGradeRecordEntityUpdateAction(EntityType.GRADE_RECORD, 
		//		model.getIdentifier(), property, value, startValue, studentModel, model);
		UserChangeEvent<UserEntityUpdateAction> event = new UserChangeEvent<UserEntityUpdateAction>(action);
		
		NotifyingAsyncCallback<AssignmentRecordModel> callback = 
			new NotifyingAsyncCallback<AssignmentRecordModel>(event) {

			public void onFailure(Throwable caught) {
				super.onFailure(caught);
			
				record.set(property, null);
				record.set(property, startValue); 
				
				if (gridEvent != null)
					grid.fireEvent(Events.AfterEdit, gridEvent);
			}
			
			public void onSuccess(AssignmentRecordModel result) {
				boolean isExcused = result.getExcluded() == null ? false : result.getExcluded().booleanValue();
				
				AssignmentRecordModel.Key recordModelKey = AssignmentRecordModel.Key.valueOf(property);
				
				Object value = null;
				
				GradebookModel gbModel = Registry.get(gradebookUid);
						
				switch (gbModel.getGradeType()) {
				case POINTS:
					value = result.get(AssignmentRecordModel.Key.POINTS_EARNED.name());
					record.set(AssignmentRecordModel.Key.POINTS_EARNED.name(), value); 
					break;
				case PERCENTAGES:
					value = result.get(AssignmentRecordModel.Key.PERCENT_EARNED.name());
					record.set(AssignmentRecordModel.Key.PERCENT_EARNED.name(), value);
					break;
				case LETTERS:
					value = result.get(AssignmentRecordModel.Key.LETTER_EARNED.name());
					record.set(AssignmentRecordModel.Key.LETTER_EARNED.name(), value);
					break;
				}
				
				record.set(AssignmentRecordModel.Key.DROPPED.name(), result.getDropped());
				record.set(AssignmentRecordModel.Key.COMMENTS.name(), result.getComments());
				record.set(AssignmentRecordModel.Key.WEIGHT.name(), result.getWeight());
				record.set(AssignmentRecordModel.Key.LOG.name(), result.getLog());
				
				if (gridEvent != null)
					grid.fireEvent(Events.AfterEdit, gridEvent);
				
				updateCourseGrade(result.getCourseGrade());
				
				//RefreshCourseGradesEvent rcge = new RefreshCourseGradesEvent(studentModel, result.getCourseGrade(), result.getAssignmentId(), value);
				//StudentViewContainer.this.fireEvent(GradebookEvents.RefreshCourseGrades, rcge);
	
				UserChangeEvent<?> event = getEvent();
	//			UserGradeRecordEntityUpdateAction updateAction = (UserGradeRecordEntityUpdateAction)event.getAction();
				UserEntityUpdateAction updateAction = (UserEntityUpdateAction)event.getAction();
				
				Boolean oldDropped = (Boolean)record.getChanges().get(AssignmentRecordModel.Key.DROPPED.name());
				boolean wasDropped = oldDropped != null && oldDropped.booleanValue();
				boolean isDropped = result.getDropped() != null && result.getDropped().booleanValue();
				
				if (isDropped && !wasDropped)
					toolBar.refresh();
				else if (wasDropped && !isDropped)
					toolBar.refresh();
				else 
					syncStudentModel(result);
				
				updateAction.setStudentModel(studentModel);
				updateAction.setModel(result);
				StudentViewContainer.this.fireEvent(GradebookEvents.UserChange, event);
				
				switch (recordModelKey) {
				case COMMENTS:
					notifier.notify("Comment", "Commented on {0} for {1}. ", result.getAssignmentName(), studentModel.getStudentName());
					break;
				case EXCLUDED:
					if (isExcused)
						notifier.notify("Excuse", "Excused {0} from {1}. Student will no longer be graded for the assignment. ", studentModel.getStudentName(), result.getAssignmentName());
					else
						notifier.notify("Unexcuse", "Unexcused {0} from {1}. Student will now be graded for the assignment. ", studentModel.getStudentName(), result.getAssignmentName());
					break;
				case POINTS_EARNED:
				case PERCENT_EARNED:
				case LETTER_EARNED:
					StringBuilder buffer = new StringBuilder();
					buffer.append(result.getAssignmentName());
					buffer.append(" : ");
					buffer.append(studentModel.getStudentName());
					
					notifier.notify(buffer.toString(), 
							"Stored assignment grade as '{0}' and recalculated course grade to '{1}' ", 
							result.get(property), result.getCourseGrade());
					break;
				};
			}
		};
		
		service.updateEntity(event.getAction(), callback);
		
		
	}*/

	
	private void syncStudentModel(GradeRecordModel model) {
		if (learnerGradeRecordCollection != null) {
			String columnId = String.valueOf(model.getAssignmentId());
			String droppedId = columnId + StudentModel.DROP_FLAG;
							
			switch (selectedGradebook.getGradebookItemModel().getGradeType()) {
			case POINTS:
				learnerGradeRecordCollection.set(columnId, model.getPointsEarned());
				break;
			case PERCENTAGES:
				learnerGradeRecordCollection.set(columnId, model.getPercentEarned());
				break;
			case LETTERS:
				learnerGradeRecordCollection.set(columnId, model.getLetterEarned());
				break;
			};
			
			boolean isExcluded = model.getExcluded() != null && model.getExcluded().booleanValue();
			boolean isDropped = model.getDropped() != null && model.getDropped().booleanValue();
			learnerGradeRecordCollection.set(droppedId, Boolean.valueOf(isDropped || isExcluded));
		}
	}
	
	/*public class SummaryExcludeColumnConfig extends SummaryCheckColumnConfig {
		
		
		public SummaryExcludeColumnConfig(String id, String name, int width) {
			super(id, name, width);
		}
		
		@Override
		protected void changeValue(final Record record, final String property, Boolean value, Boolean startValue) {
			GradebookToolFacadeAsync service = (GradebookToolFacadeAsync) Registry.get("service");
			AssignmentRecordModel model = (AssignmentRecordModel)record.getModel();
			GradebookModel gbModel = Registry.get(gradebookUid);
			service.updateGradeRecordModel(gradebookUid, gbModel.getGradebookId(), studentModel.getStudentUid(), model, AssignmentRecordModel.Key.EXCLUDED, (Boolean)record.get(property), new NotifyingAsyncCallback<AssignmentRecordModel>() {

				public void onFailure(Throwable caught) {
					super.onFailure(caught);
					record.reject(false);
				}

				public void onSuccess(AssignmentRecordModel result) {
					updateCourseGrade(result.getCourseGrade());
					record.commit(false);
					boolean isExcused = result.getExcluded() == null ? false : result.getExcluded().booleanValue();
					
					if (isExcused)
						notifier.notify("Excuse", "Excused {0} from {1}. Student will no longer be graded for the assignment. ", studentModel.getStudentName(), result.getAssignmentName());
					else
						notifier.notify("Unexcuse", "Unexcused {0} from {1}. Student will now be graded for the assignment. ", studentModel.getStudentName(), result.getAssignmentName());
				}
				
			});
		}
	}*/

	public boolean isStudentView() {
		return isStudentView;
	}

	public void setStudentView(boolean isStudentView) {
		this.isStudentView = isStudentView;
	}

	public StudentModel getStudentModel() {
		return learnerGradeRecordCollection;
	}
	
	
}
