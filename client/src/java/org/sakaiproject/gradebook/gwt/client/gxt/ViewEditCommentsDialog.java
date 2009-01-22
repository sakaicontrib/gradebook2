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

import org.sakaiproject.gradebook.gwt.client.GradebookToolFacadeAsync;
import org.sakaiproject.gradebook.gwt.client.action.RemoteCommand;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction.ClassType;
import org.sakaiproject.gradebook.gwt.client.model.GradeRecordModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;

public class ViewEditCommentsDialog extends Dialog 
{
	private static final Notifier notifier = new Notifier();

	public static final String DEFAULT_NO_COMMENTS_MSG = "The assignment does not have any comments"; 
	final private GradebookModel gbModel; 
	final private GradebookToolFacadeAsync service;
	private boolean isStudentView; 
	
	private GradeRecordModel assignment; 
	private LabelField warningMessage; 
	private LabelField commentsHeader; 
	private LabelField commentsText; 
	private Button editButton; 
	private Button updateButton; 
	private Button close; 
	private TextArea editBlock; 
	private LabelField instructions; 
	
	private LabelField studentName; 
	private LabelField assignmentName; 
	private LabelField points; 
	private LabelField maxPoints; 
	
	
	private StudentModel student; 

	private FieldSet studentInfoHeading; 
	private FieldSet commentHeading; 
	private FieldSet commentForm; 
	private FlexTable studentAssignInfo; 
		
	public ViewEditCommentsDialog(GradebookModel m, final GradebookToolFacadeAsync service, boolean isStudentView) {
		super(); 
		
		gbModel = m; 
		this.service = service;
		this.isStudentView = isStudentView;
		
		setBodyBorder(true);
		setButtons(Dialog.OK);
		setHeaderVisible(true);
		setHeading("Add/Edit/View Comments");
		setResizable(true);
		setDraggable(true);
		setCloseAction(CloseAction.CLOSE);
		setHideOnButtonClick(false); 
		setLayout(new FlowLayout());
		setPlain(false);
		getButtonBar().hide();

		updateButton = new Button("Update");
		updateButton.addSelectionListener(new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				String newCommentText; 
				
				newCommentText = editBlock.getValue(); 
				UserEntityUpdateAction<GradeRecordModel> action = 
					new UserEntityUpdateAction<GradeRecordModel>(gbModel, assignment, GradeRecordModel.Key.COMMENTS.name(), ClassType.STRING, newCommentText, commentsText.getText());
				action.setStudentModel(student);
								
				RemoteCommand<GradeRecordModel> remoteCommand = 
					new RemoteCommand<GradeRecordModel>(){

						@Override
						public void onCommandFailure(UserEntityAction<GradeRecordModel> action, Throwable caught) {
							setCommentsText(commentsText.getText()); 
							hideEditForm();
							editButton.show();
							notifier.notify("Comment", "Error - Failed to comment on {0} for {1}. ", assignment.getAssignmentName(), student.getStudentName());
						}
					
						@Override
						public void onCommandSuccess(UserEntityAction<GradeRecordModel> action, GradeRecordModel result) {
							notifier.notify("Comment", "Commented on {0} for {1}. ", result.getAssignmentName(), student.getStudentName());
							setCommentsText(result.getComments()); 
							hideEditForm();
							editButton.show();
						}
					
				};
				
				
				remoteCommand.execute(action);
			}
			
		});
		
		warningMessage = new LabelField(); 
		commentsHeader = new LabelField("This assignment has the following comment: "); 
		commentsText = new LabelField();


		createStudentHeadingInfo(); 
		add(studentInfoHeading);
		
		commentHeading = new FieldSet(); 
		commentHeading.setLayout(new RowLayout()); 
		commentHeading.setHeading("Current Comment"); 
		commentHeading.add(commentsText); 

		add(commentHeading); 

		if (isStudentView)
		{
			setEditButtonForStudentView();
		}
		else
		{
			setEditButtonForInstructorView();
		}
		add(editButton);
		
		// Build the regular form 
		commentForm = new FieldSet(); 
		commentForm.setHeading("Edit Comment"); 
		commentForm.setLayout(new RowLayout());
		
		instructions = new LabelField("Please make changes to the comment below: "); 
		editBlock = new TextArea();
		editBlock.setFieldLabel("Comment: ");
		editBlock.setWidth("75%");
		editBlock.setHeight(120);

		commentForm.add(instructions);
		commentForm.add(editBlock); 
		commentForm.add(updateButton);
		add(commentForm); 
		
		hideEditForm(); 
		setCommentsText(null);
	}

	private void createStudentHeadingInfo() 
	{
		studentInfoHeading = new FieldSet(); 
		studentInfoHeading.setHeading("Student/Assignment Information"); 
		studentInfoHeading.setLayout(new FlowLayout()); 
		
		
		studentName = new LabelField(); 
		assignmentName = new LabelField(); 
		points = new LabelField(); 
		maxPoints = new LabelField(); 
		
		
		
		studentAssignInfo = new FlexTable();
		studentAssignInfo.setWidth("100%");
		studentAssignInfo.setText(1, 0, "Student Name"); 
		
		studentAssignInfo.setWidget(1, 1, studentName);
		
		studentAssignInfo.setText(2, 0, "Assignment Name"); 
		studentAssignInfo.setWidget(2, 1, assignmentName);
		
		studentAssignInfo.setText(1, 2, "Points Earned"); 
		studentAssignInfo.setWidget(1, 3, points);

		studentAssignInfo.setText(2, 2, "Max Points"); 
		studentAssignInfo.setWidget(2, 3, maxPoints);

		studentInfoHeading.add(studentAssignInfo); 
	}
	
	private void setEditButtonForInstructorView() 
	{
		if (editButton == null)
		{
			editButton = new Button("Edit Comment", new SelectionListener<ComponentEvent>() {

				@Override
				public void componentSelected(ComponentEvent ce) {
					showEditForm();
					editBlock.setEmptyText(commentsText.getText()); 
					editButton.hide();
				}
				
			});
		}
		else
		{
			editButton.removeAllListeners(); 
			editButton.addSelectionListener(new SelectionListener<ComponentEvent>() {

				@Override
				public void componentSelected(ComponentEvent ce) {
					showEditForm();
					editBlock.setEmptyText(commentsText.getText()); 
					editButton.hide();
				}
				
			});
		}
		editButton.setVisible(true);
	}

	private void showEditForm() 
	{
		instructions.setVisible(true); 
		editBlock.setVisible(true); 
		updateButton.setVisible(true); 
		commentForm.setVisible(true);
	}
	
	private void hideEditForm() 
	{
		instructions.setVisible(false); 
		editBlock.setVisible(false); 
		updateButton.setVisible(false);
		commentForm.setVisible(false); 
	
	}
	private void setEditButtonForStudentView() 
	{
		if (editButton == null)
		{
			editButton = new Button("Edit Comment", new SelectionListener<ComponentEvent>() {

				@Override
				public void componentSelected(ComponentEvent ce) {
					// FIXME - better handling of error case
					Window.alert("Students are not able to edit/change comments"); 
				}
				
			});
		}
		else
		{
			editButton.removeAllListeners(); 
			editButton.addSelectionListener(new SelectionListener<ComponentEvent>() {

				@Override
				public void componentSelected(ComponentEvent ce) {
					// FIXME - better handling of error case
					Window.alert("Students are not able to edit/change comments"); 
				}
				
			});
		}
		
		editButton.setVisible(false);
	}

	public void setCommentsText(String commentsText) 
	{
		if (commentsText != null && commentsText.length() > 0)
		{
			this.commentsText.setText(commentsText);
		}
		else
		{
			this.commentsText.setText(DEFAULT_NO_COMMENTS_MSG); 
		}
		editBlock.setEmptyText(this.commentsText.getText()); 
	}




	@Override
	public void show() {
		hideEditForm();
		editButton.show(); 
		super.show();
	}


	public GradeRecordModel getAssignment() {
		return assignment;
	}


	public void setAssignment(GradeRecordModel assignment) {
		// FIXME - need to handle percentages et all
		this.assignment = assignment;
		this.assignmentName.setText(assignment.getAssignmentName());
		this.points.setText(assignment.getPointsEarned().toString());
		this.maxPoints.setText(assignment.getPointsPossible().toString());

	}


	public StudentModel getStudent() {
		return student;
	}


	public void setStudent(StudentModel student) {
		this.student = student;
		this.studentName.setText(student.getStudentName());
	}

	
}
