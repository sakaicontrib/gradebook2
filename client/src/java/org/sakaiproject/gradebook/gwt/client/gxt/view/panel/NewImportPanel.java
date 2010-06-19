/**********************************************************************************
*
* $Id:$
*
***********************************************************************************
*
* Copyright (c) 2008, 2009, 2010 The Regents of the University of California
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

import java.util.Collection;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.RestBuilder;
import org.sakaiproject.gradebook.gwt.client.RestCallback;
import org.sakaiproject.gradebook.gwt.client.dev.ItemUtil;
import org.sakaiproject.gradebook.gwt.client.gxt.ItemModelProcessor;
import org.sakaiproject.gradebook.gwt.client.gxt.JsonUtil;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityOverlay;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.LearnerModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.UploadModel;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;
import org.sakaiproject.gradebook.gwt.client.model.key.UploadKey;
import org.sakaiproject.gradebook.gwt.client.model.type.ItemType;


import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.PagingModelMemoryProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Element;

public class NewImportPanel extends GradebookPanel {

	private CardLayout mainCardLayout, centerCardLayout;
	private LayoutContainer mainCardLayoutContainer, centerCardLayoutContainer; 
	private Button submitButton, cancelButton, errorReturnButton; 
	private ContentPanel borderLayoutContainer;
	private BorderLayout borderLayout;
	private ContentPanel errorContainer; 
	private LayoutContainer fileUploadContainer;
	private FormPanel fileUploadPanel;
	protected MessageBox uploadBox, uploadingBox;
	private UploadModel upload;

	private MultiGradeContentPanel multigrade;
	private ItemSetupPanel setupPanel;

	private PagingLoader<PagingLoadResult<ModelData>> multigradeLoader;
	private ListStore<ModelData> multigradeStore;
	
	private ItemModel gradebookItemModel;

	private boolean isGradingFailure;

	public NewImportPanel() {

		super();

		setCollapsible(false);
		setFrame(true);
		setHeaderVisible(true);
		setHeading(i18n.headerImport());
		setHideCollapseTool(true);
		setLayout(new FitLayout());

		mainCardLayout = new CardLayout();
		mainCardLayoutContainer = new LayoutContainer();
		mainCardLayoutContainer.setLayout(mainCardLayout);

		centerCardLayout = new CardLayout();
		centerCardLayoutContainer = new LayoutContainer();
		centerCardLayoutContainer.setLayout(centerCardLayout);

		borderLayout = new BorderLayout();
		borderLayoutContainer = new ContentPanel();
		borderLayoutContainer.setHeaderVisible(false);
		borderLayoutContainer.setLayout(borderLayout);

		/*
		 * This is the next button that uploads the imported data to the server
		 */
		submitButton = new Button(i18n.importPanelNextButton());
		submitButton.setMinWidth(120);
		submitButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {	

				submitButton.setVisible(false);
				
				upload.setGradebookItemModel(gradebookItemModel);

				uploadSpreadsheet(upload);
			}
		});
		
		borderLayoutContainer.addButton(submitButton);

		cancelButton = new Button(i18n.importPanelCancelButton());
		cancelButton.setMinWidth(120);
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				Dispatcher.forwardEvent(GradebookEvents.StopImport.getEventType());
				fileUploadPanel.clear();
			}
		});

		borderLayoutContainer.addButton(cancelButton);

		errorReturnButton = new Button(i18n.importPanelRetrunButton());
		errorReturnButton.setMinWidth(120);
		errorReturnButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				Dispatcher.forwardEvent(GradebookEvents.StopImport.getEventType());
				fileUploadPanel.clear();
			}
		});
		errorContainer = new ContentPanel(); 
		errorContainer.setHeaderVisible(false); 
		errorContainer.setLayout(new FitLayout()); 
		errorContainer.addButton(errorReturnButton);

		fileUploadContainer = new LayoutContainer();
		fileUploadContainer.setLayout(new FlowLayout());
		fileUploadPanel = new FileUploadPanel(this);
		fileUploadContainer.add(fileUploadPanel);

		mainCardLayoutContainer.add(fileUploadContainer);
		mainCardLayoutContainer.add(borderLayoutContainer);
		mainCardLayoutContainer.add(errorContainer); 
		mainCardLayout.setActiveItem(fileUploadContainer);
		add(mainCardLayoutContainer);
	}


	protected void onRender(Element parent, int pos) {
		
		super.onRender(parent, pos);
	}

	@Override
	protected void onResize(final int width, final int height) {
		
		GWT.log("DEBUG: onResize(...)");
		
		super.onResize(width, height);

		if (multigrade != null)
			multigrade.setHeight(height - 100);
		
		// FIXME : make this work for setupPanel
	}
	
	
	/*
	 * This method is called to read the response from the 
	 * server side file upload
	 */
	protected void readSubmitResponse(String result) {

		GWT.log("DEBUG: readSubmitResponse(...)");
		
		String msgsFromServer = null;
		
		try {

			// Getting the JSON from REST call and create an UploadModel
			EntityOverlay overlay = JsonUtil.toOverlay(result);
			upload = new UploadModel(overlay);

			boolean hasErrors = upload.hasErrors(); 
			
			msgsFromServer = upload.getNotes(); 
			
			// If we have errors we want to do something different
			if (hasErrors) {
				errorContainer.addText(msgsFromServer); 
				mainCardLayout.setActiveItem(errorContainer);
				return; 
			}

			if(setupPanel == null) {
				setupPanel = new ItemSetupPanel();
			}

			Gradebook gradebookModel = Registry.get(AppConstants.CURRENT);
			gradebookItemModel = (ItemModel)upload.getGradebookItemModel();

			fixMangledHtmlNames(gradebookItemModel); 
			if (gradebookItemModel == null) {
				throw new Exception("Could not find the gradebook item model");
			}

			// Populate the item setup panel
			setupPanel.onRender(gradebookItemModel);

			// Populate the multi grade grid panel
			if (multigrade == null) {
				PagingModelMemoryProxy proxy = new PagingModelMemoryProxy(upload.getRows());  
				multigradeLoader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);  
				multigradeLoader.setRemoteSort(true);

				multigradeStore = new ListStore<ModelData>(multigradeLoader);
				multigradeStore.setModelComparer(new EntityModelComparer<ModelData>(LearnerKey.S_UID.name()));
				multigradeStore.setMonitorChanges(true);
				multigradeStore.setDefaultSort(LearnerKey.S_LST_NM_FRST.name(), SortDir.ASC);


				multigrade = new MultiGradeContentPanel(multigradeStore, true) {

					protected PagingLoader<PagingLoadResult<ModelData>> newLoader() {
						return multigradeLoader;
					}

					protected ListStore<ModelData> newStore() {
						return multigradeStore;
					}

				};

				multigrade.addGrid(gradebookModel.getConfigurationModel(), gradebookModel.getColumns(),
						gradebookItemModel);

				BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 550, 200, 800);  
				westData.setSplit(true);  
				westData.setCollapsible(true);  
				westData.setMargins(new Margins(5));

				BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER); 
				centerData.setMinSize(100);
				centerData.setMargins(new Margins(5, 0, 5, 0)); 

				centerCardLayoutContainer.add(multigrade);
				centerCardLayout.setActiveItem(multigrade);

				borderLayoutContainer.add(setupPanel, westData);
				borderLayoutContainer.add(centerCardLayoutContainer, centerData);
				multigrade.setHeight(mainCardLayoutContainer.getHeight() - 100);
			}

			refreshGradebookItemModel(gradebookItemModel);

			mainCardLayout.setActiveItem(borderLayoutContainer);
			mainCardLayoutContainer.layout();

			multigradeLoader.load();

			boolean showPanel =  false; 
			boolean hasDefaultMsg = false;
			StringBuilder sb = null; 

			if (showPanel) {

				String sendText = ""; 
				if (sb != null )
				{
					sendText = sb.toString(); 
					sb = null;
				}
				showMessageBox(sendText, !hasDefaultMsg);
			}
			
		} catch (Exception e) {
			
			Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(e));
			GWT.log("Caught exception: ", e);
			
		} finally {
			
			uploadBox.close();
		}

		if (msgsFromServer != null && msgsFromServer.length() > 0){
			Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent("Warning", msgsFromServer, true, true));
		}
	}

	private native String repairString(String inStr) /*-{
		var temp_div = document.createElement('div');
		temp_div.innerHTML = inStr.replace(/>/g, "&gt;").replace(/</g, "&lt;");
		return temp_div.firstChild?temp_div.firstChild.nodeValue:''
	}-*/;

	private void fixMangledHtmlNames(ItemModel gradebookItemModel) {

		GWT.log("DEBUG: fixMangledHtmlNames(...)");
		
		ItemModelProcessor processor = new ItemModelProcessor(gradebookItemModel) {

			@Override
			public void doCategory(Item categoryModel) {
				String r = repairString(categoryModel.getName());
				categoryModel.setName(r); 
			}

			@Override
			public void doGradebook(Item gradebookModel) {
				String r = repairString(gradebookModel.getName());
				gradebookModel.setName(r); 
			}

			@Override
			public void doItem(Item itemModel) {
				String r = repairString(itemModel.getName());
				itemModel.setName(r);
			}
		};

		processor.process();
	}

	private void refreshGradebookItemModel(ItemModel gradebookItemModel) {
		
		GWT.log("DEBUG: refreshGradebookItemModel(...)");
		Gradebook gradebookModel = Registry.get(AppConstants.CURRENT);
		ItemModel rootItemModel = new ItemModel();
		rootItemModel.setItemType(ItemType.ROOT);
		rootItemModel.setName("Root");
		gradebookItemModel.setParent(rootItemModel);
		rootItemModel.add(gradebookItemModel);

		multigrade.onRefreshGradebookItems(gradebookModel, gradebookItemModel);
	}

	private void showMessageBox(String alertText, boolean overrideText) {
		
		GWT.log("DEBUG: showMessageBox(...)");
		
		String defaultMessageText =  i18n.importDefaultShowPanelMessage();
		String messageText;
		StringBuilder sb; 
		if (overrideText)
		{
			if (alertText == null || "".equals(alertText))
			{
				// If they give us nothing and want to override, then we'll still put the default
				sb = new StringBuilder(defaultMessageText); 
			}
			else
			{
				sb = new StringBuilder(); 
			}
		}
		else
		{
			sb = new StringBuilder(defaultMessageText); 
			if (alertText != null && !"".equals(alertText))
			{
				sb.append("<br>");
			}
		}

		sb.append(alertText);
		messageText = sb.toString(); 
		sb = null; 

		MessageBox.alert(i18n.importSetupRequiredTitle(), messageText, null);
	}


	private void uploadSpreadsheet(UploadModel spreadsheetModel) {
		
		GWT.log("DEBUG: uploadSpreadsheet(...)");
		
		Gradebook gbModel = Registry.get(AppConstants.CURRENT);

		int numberOfLearners = upload.getRows() == null ? 0 : upload.getRows().size();

		String message = new StringBuilder().append(i18n.uploadingLearnerGradesPrefix()).append(" ")
		.append(numberOfLearners).append(" ").append(i18n.uploadingLearnerGradesSuffix()).toString();

		uploadingBox = MessageBox.wait(i18n.uploadingLearnerGradesTitle(), message, i18n.uploadingLearnerGradesStatus());

		RestBuilder builder = RestBuilder.getInstance(RestBuilder.Method.PUT, GWT.getModuleBaseURL(),
				AppConstants.REST_FRAGMENT,
				AppConstants.UPLOAD_FRAGMENT, gbModel.getGradebookUid(), String.valueOf(gbModel.getGradebookId()));

		String jsonText = spreadsheetModel == null ? null : spreadsheetModel.getJSON();

		builder.sendRequest(200, 400, jsonText, new RestCallback() {

			@Override
			public void onError(Request request, Throwable caught) {
				Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(caught));
				uploadingBox.close();
				submitButton.setVisible(true);
			}

			@Override
			public void onSuccess(Request request, Response response) {

				Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);

				try {
					String text = response.getText();
					EntityOverlay overlay = JsonUtil.toOverlay(text);
					UploadModel result = new UploadModel(overlay);

					List<Learner> rows = result.getRows();

					int numberOfScoresChanged = 0;
					if (rows != null) {
						int rowNumber = 0;
						for (Learner student : rows) {

							boolean hasChanges = DataTypeConversionUtil.checkBoolean((Boolean)student.get(AppConstants.IMPORT_CHANGES));

							if (hasChanges) {
								LearnerModel model = (LearnerModel) multigradeStore.getAt(rowNumber);
								String learnerUid = model == null ? null : model.getIdentifier();
								String currentUid = student.get(LearnerKey.S_UID.name());
								if (learnerUid == null || !learnerUid.equals(currentUid))
									model = (LearnerModel) multigradeStore.findModel(LearnerKey.S_UID.name(), currentUid);

								if (model == null) {
									GWT.log("Could not find a model for: " + currentUid, null);
									continue;
								}

								Record record = multigradeStore.getRecord(model);
								record.beginEdit();

								Collection<String> propertyNames = ((LearnerModel)student).getPropertyNames();

								if (propertyNames != null) {
									for (String p : propertyNames) {
										boolean needsRefreshing = false;

										if (p.startsWith(AppConstants.FAILED_FLAG)) {
											needsRefreshing = true;
										} else if (p.startsWith(AppConstants.SUCCESS_FLAG)) {
											needsRefreshing = true;
											numberOfScoresChanged++;
										}

										if (needsRefreshing) {
											String assignmentId = DataTypeConversionUtil.unpackItemIdFromKey(p);
											if (assignmentId != null) {
												Object value = student.get(assignmentId);

												Object obj = student.get(p);
												if (obj != null)
													record.set(p, obj);

												record.set(assignmentId, null);
												record.set(assignmentId, value);
											}
										}
									}
								}
								record.endEdit();

							}

							rowNumber++;
						}
					}

					uploadingBox.setProgressText("Loading");

					cancelButton.setText("Done");

					ItemModel gradebookItem = result.get(UploadKey.M_GB_ITM.name());


					if (gradebookItem != null)
						selectedGradebook.setGradebookGradeItem(gradebookItem);

					if (gradebookItem != null) {
						//treeStore.removeAll();
						ItemModel rootItemModel = new ItemModel();
						rootItemModel.setItemType(ItemType.ROOT);
						rootItemModel.setName("Root");
						gradebookItem.setParent(rootItemModel);
						rootItemModel.add(gradebookItem);
						mainCardLayout.setActiveItem(borderLayoutContainer);
						mainCardLayoutContainer.layout();
					}

				} catch (Exception e) {
					Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(e));
					GWT.log("Caught unexpected exception.", e);
				} finally {
					Dispatcher.forwardEvent(GradebookEvents.SwitchGradebook.getEventType(), selectedGradebook);
					//Dispatcher.forwardEvent(GradebookEvents.RefreshCourseGrades.getEventType(), selectedGradebook);
					uploadingBox.close();
				}

				if (isGradingFailure) {
					Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(i18n.importGradesFailedTitle(), i18n.importGradesFailedMessage(), true, true));
				}
			}
		});
	}
}
