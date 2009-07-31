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

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.model.GradeRecordModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
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
	
	public void onRefreshGradebookSetup(GradebookModel selectedGradebook) {
		this.selectedGradebook = selectedGradebook;
		
		updateCourseGrade(learnerGradeRecordCollection.getStudentGrade());
		
		setStudentInfoTable();
		
		setGradeInfoTable(selectedGradebook, learnerGradeRecordCollection);
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

	}
	
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);

	}
	
	public void onResize(int x, int y) {
		super.onResize(x, y);
		

	}
	
	public void refreshColumns() {

	}
	
	public void refreshData() {

	}

	
	private void updateCourseGrade(String newGrade)
	{
		if (!isStudentView || (selectedGradebook != null && selectedGradebook.getGradebookItemModel() != null 
				&& DataTypeConversionUtil.checkBoolean(selectedGradebook.getGradebookItemModel().getReleaseGrades()))) {
			// To force a refresh, let's first hide the owning panel
			studentInformationPanel.hide();
			studentInformation.setText(6, 1, newGrade);
			studentInformationPanel.show();
		} else {
			studentInformationPanel.hide();
			studentInformation.setText(6, 0, "");
			studentInformation.setText(6, 1, "");
			studentInformationPanel.show();
		}
		
		if (learnerGradeRecordCollection != null)
			learnerGradeRecordCollection.set(StudentModel.Key.COURSE_GRADE.name(), newGrade);
	}
	
	// FIXME - i18n 
	// FIXME - need to assess impact of doing it this way... 
	
	private void setStudentInfoTable() {		
		// To force a refresh, let's first hide the owning panel
		studentInformationPanel.hide();
	
		// Now, let's update the student information table
		FlexCellFormatter formatter = studentInformation.getFlexCellFormatter();
		
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
		Object value = learner.get(itemId);
		String commentFlag = new StringBuilder().append(itemId).append(StudentModel.COMMENT_TEXT_FLAG).toString();
		String comment = learner.get(commentFlag);
		String excusedFlag = new StringBuilder().append(itemId).append(StudentModel.DROP_FLAG).toString();
		
		boolean isExcused = DataTypeConversionUtil.checkBoolean((Boolean)learner.get(excusedFlag));
		boolean isIncluded = DataTypeConversionUtil.checkBoolean((Boolean)item.getIncluded());
		
		gradeInformation.setText(row, 0, item.getName());
        formatter.setStyleName(row, 0, "gbRecordLabel");
        formatter.setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);
        
        StringBuilder resultBuilder = new StringBuilder();
        if (value == null)
        	resultBuilder.append("Ungraded");
        else {
        	
        	switch (selectedGradebook.getGradebookItemModel().getGradeType()) {
        	case POINTS:
        		resultBuilder.append(NumberFormat.getDecimalFormat().format(((Double)value).doubleValue()));
        		
        		if (item.getPoints() != null)
        			resultBuilder.append(" out of ").append(item.getPoints()).append(" points");
        		
        		break;
        	case PERCENTAGES:
        		resultBuilder.append(NumberFormat.getDecimalFormat().format(((Double)value).doubleValue()))
        			.append("%");
        		
        		break;
        	case LETTERS:
        		resultBuilder.append(value);
        		break;
        	}
        	
        	
        }
        
        if (!isIncluded) 
        	resultBuilder.append(" (not included in grade)");
        
        if (isExcused)
        	resultBuilder.append(" (excused)");
        
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
		
		// Start by removing all existing rows, since we may be reducing the visibility
		for (int i=gradeInformation.getRowCount() - 1;i>=0;i--) {
			for (int j=0;i<gradeInformation.getCellCount(i);j++) {
				gradeInformation.removeCell(i, j);
			}
			gradeInformation.removeRow(i);
		}
		
		ItemModel gradebookItemModel = selectedGradebook.getGradebookItemModel();
		
		boolean isDisplayReleasedItems = DataTypeConversionUtil.checkBoolean(gradebookItemModel.getReleaseItems());
		
		if (isDisplayReleasedItems) {
			boolean isNothingToDisplay = true;
			int row=0;
			if (gradebookItemModel.getChildCount() > 0) {
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
								        formatter.setColSpan(row, 0, 3);
								        row++;
									}
									isCategoryHeaderDisplayed = true;
								}
								
								populateGradeInfoRow(row, item, learner, formatter);
								isNothingToDisplay = false;
								row++;
							} 
						}
						break;
					case ITEM:
						if (DataTypeConversionUtil.checkBoolean(child.getReleased())) {
							populateGradeInfoRow(row, child, learner, formatter);
							isNothingToDisplay = false;
							row++;
						}
						break;
					}
				}
			}
			
			if (isNothingToDisplay) {
				I18nConstants i18n = Registry.get(AppConstants.I18N);
				gradeInformation.setText(0, 0, i18n.notifyNoReleasedItems());
			}
			
		} else {
			I18nConstants i18n = Registry.get(AppConstants.I18N);
			gradeInformation.setText(0, 0, i18n.notifyNotDisplayingReleasedItems());
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
	
	/*private void syncStudentModel(GradeRecordModel model) {
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
