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
package org.sakaiproject.gradebook.gwt.client.gxt.multigrade;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.GradebookToolFacadeAsync;
import org.sakaiproject.gradebook.gwt.client.action.RemoteCommand;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityCreateAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityGetAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradeRecordUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.model.CommentModel;
import org.sakaiproject.gradebook.gwt.client.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.model.GradeEventModel;
import org.sakaiproject.gradebook.gwt.client.model.GradeScaleRecordModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadConfig;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.AdapterMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.KeyboardListener;

public class MultiGradeContextMenu extends Menu {

	private MenuItem contextMenuAddCommentItem;
	private MenuItem contextMenuEditCommentItem;
	private MenuItem contextMenuViewGradeLogItem;
	
	private Menu contextMenuAddSubMenu;
	private Menu contextMenuEditSubMenu;
	private Menu contextMenuViewSubMenu;
	
	private TextArea addCommentTextArea;
	private TextArea editCommentTextArea;
	private Grid<GradeEventModel> viewGradeHistoryGrid;

	
	public MultiGradeContextMenu(final StudentModelOwner owner) {
		super();
		
		contextMenuAddCommentItem = new MenuItem("Add Comment");
		contextMenuEditCommentItem = new MenuItem("Edit Comment");
		contextMenuViewGradeLogItem = new MenuItem("View Grade History");
		
		addCommentTextArea = new TextArea() {
			
			@Override
			protected void onKeyPress(FieldEvent fe) {
			    super.onKeyPress(fe);

			    switch (fe.getKeyCode()) {
			    case KeyboardListener.KEY_ENTER:
			    	
			    	ListStore<StudentModel> learnerStore = owner.getStore();
			    	StudentModel learner = owner.getSelectedModel();
			    	Long itemId = owner.getSelectedAssignment();
			    	
			    	String property = new StringBuilder().append(String.valueOf(itemId)).append(StudentModel.COMMENT_TEXT_FLAG).toString();
			    	
			    	String newCommentText = getValue();
			    	String oldCommentText = learner.get(property);
			    	
			    	Dispatcher.forwardEvent(GradebookEvents.UpdateLearnerGradeRecord.getEventType(), new GradeRecordUpdate(learnerStore, learner, property, "Comment", oldCommentText, newCommentText));
			    	
			    	/*
			    	if (studentModel != null) {
				    	CommentModel commentModel = new CommentModel();
				    	commentModel.setStudentUid(studentModel.getIdentifier());
				    	commentModel.setAssignmentId(owner.getSelectedAssignment());
				    	commentModel.setText(getValue());
				    	
				    	GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
				    	UserEntityCreateAction<CommentModel> action = 
				    		new UserEntityCreateAction<CommentModel>(selectedGradebook, EntityType.COMMENT, commentModel);
				    	
				    	RemoteCommand<CommentModel> remoteCommand = 
							new RemoteCommand<CommentModel>() {

								@Override
								public void onCommandSuccess(UserEntityAction<CommentModel> action, CommentModel result) {
									MultiGradeContextMenu.this.hide();
									
									String property = String.valueOf(owner.getSelectedAssignment());
									
									Record record = owner.getStore().getRecord(studentModel);
									// Force refresh of cell
									Object value = record.get(property);
									record.set(property, null);
									record.set(property, value);
									record.set(property + StudentModel.COMMENTED_FLAG, Boolean.TRUE);
									record.endEdit();
								}
								
				    	};
				    	
				    	remoteCommand.execute(action);
			    	}*/
			    	
			    	break;
			    }
			    
			}
			
		};
		addCommentTextArea.setSize(200, 150);
		AdapterMenuItem addTextBox = new AdapterMenuItem(addCommentTextArea);
		addTextBox.setHideOnClick(false);
		
		contextMenuAddSubMenu = new Menu();
		contextMenuAddSubMenu.add(addTextBox);
		
		
		editCommentTextArea = new TextArea() {
			
			@Override
			protected void onKeyPress(FieldEvent fe) {
			    super.onKeyPress(fe);

			    switch (fe.getKeyCode()) {
			    case KeyboardListener.KEY_ENTER:
			    			    	
			    	ListStore<StudentModel> learnerStore = owner.getStore();
			    	StudentModel learner = owner.getSelectedModel();
			    	Long itemId = owner.getSelectedAssignment();
			    	
			    	String property = new StringBuilder().append(String.valueOf(itemId)).append(StudentModel.COMMENT_TEXT_FLAG).toString();
			    	
			    	String newCommentText = getValue();
			    	String oldCommentText = learner.get(property);
			    	
			    	Dispatcher.forwardEvent(GradebookEvents.UpdateLearnerGradeRecord.getEventType(), new GradeRecordUpdate(learnerStore, learner, property, "Comment", oldCommentText, newCommentText));
			    	
			    	/*if (commentModel != null) {
			    		GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
				    	UserEntityUpdateAction<CommentModel> action = 
				    		new UserEntityUpdateAction<CommentModel>(selectedGradebook, commentModel, 
				    				CommentModel.Key.TEXT.name(), ClassType.STRING, getValue(), commentModel.getText());
				    	
				    	RemoteCommand<CommentModel> remoteCommand = 
							new RemoteCommand<CommentModel>() {

								@Override
								public void onCommandSuccess(UserEntityAction<CommentModel> action, CommentModel result) {
									commentModel = result;
									
									if (commentModel != null) {
										editCommentTextArea.setValue(commentModel.getText());
									}
									
									MultiGradeContextMenu.this.hide();
									
									String property = String.valueOf(owner.getSelectedAssignment());
									
									StudentModel studentModel = owner.getSelectedModel();
									Record record = owner.getStore().getRecord(studentModel);
									// Force refresh of cell
									Object value = record.get(property);
									record.set(property, null);
									record.set(property, value);
									record.set(property + StudentModel.COMMENTED_FLAG, Boolean.TRUE);
									record.endEdit();
								}
				    	
				    	};
				    	
				    	remoteCommand.execute(action);
			    	}*/
			    	
			    	break;
			    }
			    
			}
			
		};
		editCommentTextArea.setSize(200, 150);
		AdapterMenuItem editTextBox = new AdapterMenuItem(editCommentTextArea);
		editTextBox.setHideOnClick(false);
		
		contextMenuEditSubMenu = new Menu();
		contextMenuEditSubMenu.add(editTextBox);
		
		contextMenuViewSubMenu = new Menu();
		
		RpcProxy<ListLoadConfig, List<GradeScaleRecordModel>> proxy = new RpcProxy<ListLoadConfig, List<GradeScaleRecordModel>>() {
			
			@Override
			protected void load(ListLoadConfig listLoadConfig, AsyncCallback<List<GradeScaleRecordModel>> callback) {
				
				GradebookToolFacadeAsync service = Registry.get("service");
				UserEntityGetAction<GradeScaleRecordModel> action = 
					new UserEntityGetAction<GradeScaleRecordModel>(EntityType.GRADE_EVENT, String.valueOf(owner.getSelectedAssignment()));
				action.setStudentUid(owner.getSelectedModel().getIdentifier());
				service.getEntityList(action, callback);
				
			}
		};
		
		
		final ListLoader loader = new BaseListLoader(proxy);  
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		
		// Currently, the default number format is #.#####
		NumberFormat defaultNumberFormat = DataTypeConversionUtil.getDefaultNumberFormat();

		ColumnConfig column = new ColumnConfig();  
		column.setId(GradeEventModel.Key.DATE_GRADED.name());  
		column.setHeader("Date");
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setWidth(120);
		column.setGroupable(false);
		column.setMenuDisabled(true);
		column.setSortable(false);
		configs.add(column); 
		
		column = new ColumnConfig();  
		column.setId(GradeEventModel.Key.GRADE.name());  
		column.setHeader("Grade");
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setWidth(70);
		column.setGroupable(false);
		column.setMenuDisabled(true);
		column.setSortable(false);
		configs.add(column);
		
		column = new ColumnConfig();  
		column.setId(GradeEventModel.Key.GRADER_NAME.name());
		column.setHeader("Grader");
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setWidth(120);
		column.setGroupable(false);
		column.setMenuDisabled(true);
		column.setSortable(false);
		configs.add(column);
		
		final ColumnModel cm = new ColumnModel(configs);
		
		
		ListStore<GradeEventModel> store = new ListStore<GradeEventModel>(loader);
		store.setModelComparer(new EntityModelComparer<GradeEventModel>());
		viewGradeHistoryGrid = new Grid<GradeEventModel>(store, cm);
		//viewGradeHistoryGrid.setSize(300, 250);
		viewGradeHistoryGrid.setBorders(true);
		
		LayoutContainer layoutContainer = new LayoutContainer();
		layoutContainer.setLayout(new FitLayout());
		layoutContainer.setSize(300, 250);
		layoutContainer.add(viewGradeHistoryGrid);
		layoutContainer.setScrollMode(Scroll.AUTO);
		
		AdapterMenuItem gradeHistoryGrid = new AdapterMenuItem(layoutContainer);
		gradeHistoryGrid.setHideOnClick(false);
		contextMenuViewSubMenu.add(gradeHistoryGrid);
		
		contextMenuAddCommentItem.setSubMenu(contextMenuAddSubMenu);
		contextMenuEditCommentItem.setSubMenu(contextMenuEditSubMenu);
		contextMenuViewGradeLogItem.setSubMenu(contextMenuViewSubMenu);
		
		add(contextMenuAddCommentItem);
		add(contextMenuEditCommentItem);
		add(contextMenuViewGradeLogItem);
		
		addListener(Events.BeforeShow, new Listener<MenuEvent>() {

			public void handleEvent(MenuEvent be) {
				addCommentTextArea.setValue(null);
				editCommentTextArea.setValue(null);
				
				if (contextMenuEditCommentItem.isEnabled()) {
					
					StudentModel learner = owner.getSelectedModel();
					Long itemId = owner.getSelectedAssignment();
					
					if (learner != null && itemId != null) {
						String property = new StringBuilder().append(String.valueOf(itemId)).append(StudentModel.COMMENT_TEXT_FLAG).toString();
						
						String commentText = learner.get(property);
						editCommentTextArea.setValue(commentText);
					}
					/*GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
					UserEntityGetAction<CommentModel> action = 
			    		new UserEntityGetAction<CommentModel>(selectedGradebook, EntityType.COMMENT, 
			    				String.valueOf(owner.getSelectedAssignment()), Boolean.TRUE);
					action.setStudentUid(owner.getSelectedModel().getIdentifier());
					
					
					
					
					RemoteCommand<CommentModel> remoteCommand = 
						new RemoteCommand<CommentModel>() {

							@Override
							public void onCommandSuccess(UserEntityAction<CommentModel> action, CommentModel result) {
								commentModel = result;
								
								if (commentModel != null) {
									editCommentTextArea.setValue(commentModel.getText());
								}
							}
						
					};
					
					remoteCommand.execute(action);*/
				}
				
				if (contextMenuViewGradeLogItem.isEnabled()) {
					if (viewGradeHistoryGrid.isRendered())
						viewGradeHistoryGrid.getView().refresh(false);
					loader.load();
				}
			} 
		});
	}
	
	public void enableAddComment(boolean isEnabled) {
		contextMenuAddCommentItem.setEnabled(isEnabled);
	}
	
	public void enableEditComment(boolean isEnabled) {
		contextMenuEditCommentItem.setEnabled(isEnabled);
	}
	
	public void enableViewGradeHistory(boolean isEnabled) {
		contextMenuViewGradeLogItem.setEnabled(isEnabled);
	}
	
	
	
}
