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
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.RestBuilder;
import org.sakaiproject.gradebook.gwt.client.RestBuilder.Method;
import org.sakaiproject.gradebook.gwt.client.api.Card;
import org.sakaiproject.gradebook.gwt.client.api.Wizard;
import org.sakaiproject.gradebook.gwt.client.gin.WidgetInjector;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradeRecordUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.gxt.model.GradeEventModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.LearnerModel;
import org.sakaiproject.gradebook.gwt.client.model.key.GradeEventKey;
import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;
import org.sakaiproject.gradebook.gwt.client.resource.GradebookResources;
import org.sakaiproject.gradebook.gwt.client.util.Base64;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.DataReader;
import com.extjs.gxt.ui.client.data.HttpProxy;
import com.extjs.gxt.ui.client.data.JsonLoadResultReader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextBox;

public class MultiGradeContextMenu extends Menu {

	private MenuItem contextMenuAddCommentItem;
	private MenuItem contextMenuEditCommentItem;
	private MenuItem contextMenuViewGradeLogItem;
	
	private TextArea editCommentTextArea;
	private Grid<GradeEventModel> viewGradeHistoryGrid;
	private Wizard wizard;
	private I18nConstants i18n;
	
	Text student = new Text();
	Text assignment = new Text();
	
	 
	protected final GradebookResources resources = Registry.get(AppConstants.RESOURCES);
	private FormPanel commentCardForm;
	private BaseListLoader<ListLoadResult<ModelData>> loader;
	private Text commentUpdateStatus;
	private Text historyLoadStatus;
	private boolean modified;
	
	

	
	public MultiGradeContextMenu(final StudentModelOwner owner) {
		super();
		
		this.i18n = Registry.get(AppConstants.I18N);
		
		contextMenuEditCommentItem = new MenuItem(i18n.editCommentsMenu());
		
		
		contextMenuViewGradeLogItem = new MenuItem("View Grade History"); //TODO: I18N
		
		add(contextMenuEditCommentItem);
		
		contextMenuEditCommentItem.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				prepareDialog(owner);
				showDialog(owner, 1);
				
			}
		});
		
		add(contextMenuViewGradeLogItem);
		contextMenuViewGradeLogItem.addSelectionListener(new SelectionListener<MenuEvent> () {

			@Override
			public void componentSelected(MenuEvent ce) {
				prepareDialog(owner);
				showDialog(owner, 2);
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
	
	
	private void editComment(StudentModelOwner owner, String value) {
		ListStore<ModelData> learnerStore = owner.getStore();
		ModelData learner = owner.getSelectedModel();
    	Long itemId = owner.getSelectedAssignment();
    	
    	String property = DataTypeConversionUtil.buildCommentTextKey(String.valueOf(itemId)); //new StringBuilder().append(String.valueOf(itemId)).append(AppConstants.COMMENT_TEXT_FLAG).toString();
    	
    	String newCommentText = value;
    	String oldCommentText = learner.get(property);
    	
    	Dispatcher.forwardEvent(GradebookEvents.UpdateLearnerGradeRecord.getEventType(), new GradeRecordUpdate(learnerStore, learner, property, "Comment", oldCommentText, newCommentText) {
    		public void onSuccess(GradeRecordUpdate update) {
    			GWT.log(i18n.saved());
    			editCommentTextArea.setOriginalValue(editCommentTextArea.getValue());
    		}
    		public void onError(GradeRecordUpdate update) {
    			commentUpdateStatus.setText(i18n.errorOccurredGeneric());
    			GWT.log("error saving comment");
    		}
    	});
	}	
	
	private void prepareDialog(final StudentModelOwner owner) {
		
		LearnerModel data =  (LearnerModel) owner.getSelectedModel();
		
		if (null == data) {
			GWT.log("null data in menuselection for comment edit menu");
			return;
		}
		
		WidgetInjector injector = Registry.get(AppConstants.WIDGET_INJECTOR);
		wizard = injector.getWizardProvider().get();

		// 1st and only card 
		
		Card commentsCard = wizard.newCard(i18n.commentName());
		Card historyCard = wizard.newCard(i18n.headerHistoryTitle());
		
		wizard.setClosable(false);
		wizard.setShowWestImageContainer(false);
		wizard.setPanelBackgroundColor("#FFFFFF");
		
		wizard.setContainer(owner.getElement());
		
		wizard.setProgressIndicator(Wizard.Indicator.NONE);
		wizard.setHidePreviousButtonOnFirstCard(true);
		wizard.setHideFinishButtonOnLastCard(true);
		wizard.setHideHeaderPanel(false);
		
		wizard.setModal(true);
		wizard.setResizable(false);
		wizard.setSize(655, 555);
		
		wizard.setLayout(new FitLayout());
		
		wizard.setNextButtonText(i18n.headerHistory());
		wizard.setPreviousButtonText(i18n.editCommentsMenu());
		
		wizard.setHidePreviousButtonOnFirstCard(true);
		
		wizard.addCancelListener(new DirtyCheckListener());
		wizard.setCancelButtonText(i18n.close());
		
		 /// ---- start setup for comment editing
		final Button submit = new Button("Submit");
		submit.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent be) {
				editComment(owner, editCommentTextArea.getValue());
				submit.setEnabled(false);
				setModified(true);
				setSavedText();
			}
			
		});
		submit.setEnabled(false);
		submit.setVisible(true);
		
		
		editCommentTextArea = new TextArea() {
			
			@Override
			protected void onKeyUp(FieldEvent fe) {
				
				super.onKeyUp(fe);
				
				submit.setEnabled(editCommentTextArea.isDirty());
				commentUpdateStatus.setText(editCommentTextArea.isDirty() || !modified ? "" : i18n.saved());
			}

			@Override
			public boolean isDirty() {
				String newValue = getInputEl().getValue();
				return !Util.equalWithNull(newValue, originalValue)
						&& !i18n.noCommentsYet().equals(newValue) ;
			}
			
		};
		
		editCommentTextArea.setFieldLabel(i18n.commentName());
		editCommentTextArea.setEmptyText(i18n.noCommentsYet());
		editCommentTextArea.addStyleName("commentTextarea");
		
		
		ContentPanel editCommentContainer = new ContentPanel();
		editCommentContainer.setHeaderVisible(false);
		
		editCommentContainer.add(editCommentTextArea);		
	    
		commentCardForm = new FormPanel();
		FormLayout flayout = new FormLayout();
		flayout.setLabelAlign(LabelAlign.TOP);
		commentCardForm.setLayout(flayout);
			
		commentsCard.setTitle(i18n.editCommentsMenu());
		historyCard.setTitle(i18n.headerHistory());
		
		wizard.setHeaderTitle(data.getDisplayName() + " - '" + owner.getSelectedColumnHeader() + "'");
		
		commentCardForm.add(editCommentTextArea, new FormData("100% 85%"));
		
		commentUpdateStatus = new Text();
		commentCardForm.add(commentUpdateStatus);
		commentCardForm.addButton(submit);
		
		commentsCard.setFormPanel(commentCardForm);
		
		
		/// ----- start setup for the grade history grid
		
		historyLoadStatus = new Text(i18n.applicationLoading());
		historyLoadStatus.setStyleAttribute("font-size", "12");
		
		RestBuilder builder = RestBuilder.getInstance(Method.GET, GWT.getModuleBaseURL(),
				AppConstants.REST_FRAGMENT, AppConstants.GRADE_EVENT_FRAGMENT);
		
		HttpProxy<BaseListLoadResult<String>> proxy = new HttpProxy<BaseListLoadResult<String>>(builder) {

			@Override
			public void load(DataReader<BaseListLoadResult<String>> reader,
					Object loadConfig,
					final AsyncCallback<BaseListLoadResult<String>> callback) {
				ModelData learner = owner.getSelectedModel();
				if (learner == null)
					return;
				
				String learnerUid = learner.get(LearnerKey.S_UID.name());
				Long selectedAssignment = owner.getSelectedAssignment();
				
				initUrl = RestBuilder.buildInitUrl(GWT.getModuleBaseURL(),
						AppConstants.REST_FRAGMENT, AppConstants.GRADE_EVENT_FRAGMENT,
						Base64.encode(learnerUid), String.valueOf(selectedAssignment));
				
				

				super.load(reader, loadConfig, new AsyncCallback<BaseListLoadResult<String>>() {
				
					@Override
					public void onFailure(Throwable caught) {
						historyLoadStatus.setText(i18n.errorOccurredGeneric());
				        callback.onFailure(caught);
				      }

					@Override
					public void onSuccess(BaseListLoadResult<String> result) {
						historyLoadStatus.setText(i18n.historyLoaded());
						callback.onSuccess(result);
						
					}
				    
				});
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

		loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy, reader);
				
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
		viewGradeHistoryGrid.setWidth(400);
		viewGradeHistoryGrid.setHeight(290);
		viewGradeHistoryGrid.getView().setAutoFill(true);
		
		LayoutContainer layoutContainer = new LayoutContainer();
		layoutContainer.setLayout(new FitLayout());
		layoutContainer.add(viewGradeHistoryGrid);//, new FormData("100% 75%") );
		layoutContainer.setScrollMode(Scroll.AUTO);
		layoutContainer.add(historyLoadStatus);
		
		historyCard.setLayoutContainer(layoutContainer);
				
	}
	protected void setSavedText() {
		commentUpdateStatus.setText(i18n.saved());
	}

	protected void setModified(boolean b) {
		this.modified = b;
		
	}

	private void showDialog(final StudentModelOwner owner, int page) {
		setModified(false);
		if (null == owner)
			return;
		
		LearnerModel data =  (LearnerModel) owner.getSelectedModel();
				
		if (null == data) {
			GWT.log("null data in menuselection for comment edit menu");
			return;
		}
		Long itemId = owner.getSelectedAssignment();
		
		if (data != null && itemId != null) {
			String property = DataTypeConversionUtil.buildCommentTextKey(String.valueOf(itemId)); 
			
			String commentText = data.get(property);
			commentText = null == commentText ? "" : commentText;
			editCommentTextArea.setValue(commentText);
			editCommentTextArea.setOriginalValue(commentText);

		}
		
		if (viewGradeHistoryGrid.isRendered()) {
			viewGradeHistoryGrid.getView().refresh(false);
		}
		loader.load();
		viewGradeHistoryGrid.getView().layout();

		wizard.show();
		if(page>1) 
			wizard.pressNextButton();
		
	}
	
	public class DirtyCheckListener implements Listener<BaseEvent> {

		public void handleEvent(BaseEvent be) {
			editCommentTextArea.enable();
			if (editCommentTextArea.isDirty()) {
				confirm(i18n.hasChangesMessage());
			}
		}
		
		private void confirm(String msg) {
			MessageBox.confirm("", msg, new Listener<MessageBoxEvent> () {

				
				public void handleEvent(MessageBoxEvent be) {
					Button clicked = be.getButtonClicked();
					if (!clicked.getItemId().equals(Dialog.YES)) {
						wizard.show();
						wizard.pressPreviousButton();
					}
					
				}
				
			});
		}
		
		 
		
	}
	
	

	
}
