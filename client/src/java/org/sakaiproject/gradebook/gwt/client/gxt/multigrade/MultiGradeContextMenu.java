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
import java.util.EnumSet;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.RestBuilder;
import org.sakaiproject.gradebook.gwt.client.RestBuilder.Method;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradeRecordUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.gxt.model.GradeEventModel;
import org.sakaiproject.gradebook.gwt.client.model.key.GradeEventKey;
import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;
import org.sakaiproject.gradebook.gwt.client.util.Base64;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.DataReader;
import com.extjs.gxt.ui.client.data.HttpProxy;
import com.extjs.gxt.ui.client.data.JsonLoadResultReader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.rpc.AsyncCallback;

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
		
		ContentPanel addCommentContainer = new ContentPanel();
		addCommentContainer.setHeaderVisible(false);
		addCommentTextArea = new TextArea() {
			
			@Override
			protected void onKeyPress(FieldEvent fe) {
			    super.onKeyPress(fe);

			    switch (fe.getKeyCode()) {
			    case KeyCodes.KEY_ENTER:
			    	addComment(owner, getValue());
			    	break;
			    }
			    
			}
			
		};
		addCommentTextArea.setSize(200, 150);
		// GBRK-199 MJW - IE needs the container size to be set as well. 
		addCommentContainer.setSize(200, 150); 
		addCommentContainer.add(addCommentTextArea);
		addCommentContainer.addButton(new Button("Submit", new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent be) {
				addComment(owner, addCommentTextArea.getValue());
			}
			
		}));
		
		contextMenuAddSubMenu = new Menu();
		contextMenuAddSubMenu.add(addCommentContainer);
		
		ContentPanel editCommentContainer = new ContentPanel();
		editCommentContainer.setHeaderVisible(false);
		editCommentTextArea = new TextArea() {
			
			@Override
			protected void onKeyPress(FieldEvent fe) {
			    super.onKeyPress(fe);

			    switch (fe.getKeyCode()) {
			    case KeyCodes.KEY_ENTER:		    
			    	editComment(owner, getValue());
			    	break;
			    }
			    
			}
			
		};
		editCommentTextArea.setSize(200, 150);
		// GBRK-199 MJW - IE needs the container size to be set as well. 
		editCommentContainer.setSize(200, 150);
		editCommentContainer.add(editCommentTextArea);
		editCommentContainer.addButton(new Button("Submit", new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent be) {
				editComment(owner, editCommentTextArea.getValue());
			}
			
		}));
		
		
		contextMenuEditSubMenu = new Menu();
		contextMenuEditSubMenu.add(editCommentContainer);
		
		contextMenuViewSubMenu = new Menu();
		
		
		RestBuilder builder = RestBuilder.getInstance(Method.GET, GWT.getModuleBaseURL(),
				AppConstants.REST_FRAGMENT, AppConstants.GRADE_EVENT_FRAGMENT);
		
		HttpProxy<String> proxy = new HttpProxy<String>(builder) {
			
			public void load(final DataReader<String> reader, final Object loadConfig, final AsyncCallback<String> callback) {
				ModelData learner = owner.getSelectedModel();
				if (learner == null)
					return;
				
				String learnerUid = learner.get(LearnerKey.S_UID.name());
				Long selectedAssignment = owner.getSelectedAssignment();
				
				initUrl = RestBuilder.buildInitUrl(GWT.getModuleBaseURL(),
						AppConstants.REST_FRAGMENT, AppConstants.GRADE_EVENT_FRAGMENT,
						Base64.encode(learnerUid), String.valueOf(selectedAssignment));

				super.load(reader, loadConfig, callback);
			}
			
		};  

		ModelType type = new ModelType();
		type.setRoot(AppConstants.LIST_ROOT);
		type.setTotalName(AppConstants.TOTAL);
		
		for (GradeEventKey key : EnumSet.allOf(GradeEventKey.class)) {
			type.addField(key.name(), key.name()); 
		}
		
		// need a loader, proxy, and reader  
		JsonLoadResultReader<ListLoadResult<ModelData>> reader = new JsonLoadResultReader<ListLoadResult<ModelData>>(type);  

		final ListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy, reader);
				
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		
		
		ColumnConfig column = new ColumnConfig();  
		column.setId(GradeEventKey.T_GRADED.name());  
		column.setHeader("Date");
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setWidth(120);
		column.setGroupable(false);
		column.setMenuDisabled(true);
		column.setSortable(false);
		configs.add(column); 
		
		column = new ColumnConfig();  
		column.setId(GradeEventKey.S_GRD.name());  
		column.setHeader("Grade");
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setWidth(70);
		column.setGroupable(false);
		column.setMenuDisabled(true);
		column.setSortable(false);
		configs.add(column);
		
		column = new ColumnConfig();  
		column.setId(GradeEventKey.S_GRDR_NM.name());
		column.setHeader("Grader");
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setWidth(120);
		column.setGroupable(false);
		column.setMenuDisabled(true);
		column.setSortable(false);
		configs.add(column);
		
		final ColumnModel cm = new ColumnModel(configs);
		
		
		ListStore<GradeEventModel> store = new ListStore<GradeEventModel>(loader);
		store.setModelComparer(new EntityModelComparer<GradeEventModel>(GradeEventKey.S_ID.name()));
		viewGradeHistoryGrid = new Grid<GradeEventModel>(store, cm);
		viewGradeHistoryGrid.setBorders(true);
		
		LayoutContainer layoutContainer = new LayoutContainer();
		layoutContainer.setLayout(new FitLayout());
		layoutContainer.setSize(300, 250);
		layoutContainer.add(viewGradeHistoryGrid);
		layoutContainer.setScrollMode(Scroll.AUTO);
		
		contextMenuViewSubMenu.add(layoutContainer);
		
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
					
					ModelData learner = owner.getSelectedModel();
					Long itemId = owner.getSelectedAssignment();
					
					if (learner != null && itemId != null) {
						String property = DataTypeConversionUtil.buildCommentTextKey(String.valueOf(itemId)); //new StringBuilder().append(String.valueOf(itemId)).append(AppConstants.COMMENT_TEXT_FLAG).toString();
						
						String commentText = learner.get(property);
						editCommentTextArea.setValue(commentText);
					}
				}
				
				if (contextMenuViewGradeLogItem.isEnabled()) {
					if (viewGradeHistoryGrid.isRendered())
						viewGradeHistoryGrid.getView().refresh(false);
					loader.load();
				}
			} 
		});
	}
	
	@Override
	public void showAt(int x, int y) {
		super.showAt(x - 3, y - 3);
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
	
	
	private void addComment(StudentModelOwner owner, String value) {
		ListStore<ModelData> learnerStore = owner.getStore();
		ModelData learner = owner.getSelectedModel();
    	Long itemId = owner.getSelectedAssignment();
    	
    	String property = DataTypeConversionUtil.buildCommentTextKey(String.valueOf(itemId)); //new StringBuilder().append(String.valueOf(itemId)).append(AppConstants.COMMENT_TEXT_FLAG).toString();
    	
    	String newCommentText = value;
    	String oldCommentText = learner.get(property);
    	
    	Dispatcher.forwardEvent(GradebookEvents.UpdateLearnerGradeRecord.getEventType(), new GradeRecordUpdate(learnerStore, learner, property, "Comment", oldCommentText, newCommentText));
	}
	
	private void editComment(StudentModelOwner owner, String value) {
		ListStore<ModelData> learnerStore = owner.getStore();
		ModelData learner = owner.getSelectedModel();
    	Long itemId = owner.getSelectedAssignment();
    	
    	String property = DataTypeConversionUtil.buildCommentTextKey(String.valueOf(itemId)); //new StringBuilder().append(String.valueOf(itemId)).append(AppConstants.COMMENT_TEXT_FLAG).toString();
    	
    	String newCommentText = value;
    	String oldCommentText = learner.get(property);
    	
    	Dispatcher.forwardEvent(GradebookEvents.UpdateLearnerGradeRecord.getEventType(), new GradeRecordUpdate(learnerStore, learner, property, "Comment", oldCommentText, newCommentText));
	}	
}
