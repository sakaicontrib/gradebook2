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
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.RestBuilder;
import org.sakaiproject.gradebook.gwt.client.RestCallback;
import org.sakaiproject.gradebook.gwt.client.api.Card;
import org.sakaiproject.gradebook.gwt.client.api.Wizard;
import org.sakaiproject.gradebook.gwt.client.gin.WidgetInjector;
import org.sakaiproject.gradebook.gwt.client.gxt.InlineEditNumberField;
import org.sakaiproject.gradebook.gwt.client.gxt.ItemModelProcessor;
import org.sakaiproject.gradebook.gwt.client.gxt.JsonUtil;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityOverlay;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ImportSettingsModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.LearnerModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.UploadModel;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.ImportSettings;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;
import org.sakaiproject.gradebook.gwt.client.model.key.UploadKey;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.type.ItemType;
import org.sakaiproject.gradebook.gwt.client.resource.GradebookResources;
import org.sakaiproject.gradebook.gwt.client.wizard.validators.MinMaxDoubleValidator;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.PagingModelMemoryProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Element;

public class ImportPanel extends GradebookPanel {
	
	// GRBK-1105
	private final static int I100 = 100; 
	private final static Double MINIMUM_PERCENTAGE_VALUE = Double.valueOf("0"); 
	private final static Double MAXIMUM_PERCENTAGE_VALUE = Double.valueOf("100"); 

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
	private Dialog forceOverwriteDialog;

	private MultiGradeContentPanel multigrade;
	private ImportItemSetupPanel setupPanel;

	private PagingLoader<PagingLoadResult<ModelData>> multigradeLoader;
	private ListStore<ModelData> multigradeStore;
	
	private ItemModel gradebookItemModel;

	private boolean isGradingFailure;
	
	private ImportSettings importSettings = new ImportSettingsModel();
	private Wizard wizard;
	
	private GradebookResources resources = Registry.get(AppConstants.RESOURCES);
	
	
	public ImportPanel() {

		super();

		setCollapsible(false);
		setFrame(true);
		setHeaderVisible(true);
		setHeading(i18n.headerImport());
		setHideCollapseTool(true);
		setLayout(new FitLayout());
		setBodyStyle("backgroundColor: slategrey;");

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
				uploadSpreadsheet(upload, importSettings);
								
			}
		});
		
		borderLayoutContainer.addButton(submitButton);

		cancelButton = new Button(i18n.importPanelCancelButton());
		cancelButton.setMinWidth(120);
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				Dispatcher.forwardEvent(GradebookEvents.StopImport.getEventType());
				Dispatcher.forwardEvent(GradebookEvents.LayoutItemTreePanel.getEventType());
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
		add(mainCardLayoutContainer, new FitData(50));
		
		forceOverwriteDialog = new Dialog()  {

			@Override
			protected void onButtonPressed(Button button) {
				uploadingBox.close();
				super.onButtonPressed(button);
				
				if (button.getItemId().equals(Dialog.OK)) {
					submitButton.setVisible(false);
					importSettings.setForceOverwriteAssignments(true);
					uploadSpreadsheet(getUploadModel(), importSettings);
				} else {
					
					
					submitButton.setVisible(true);
				}
			}
		};
		forceOverwriteDialog.setHeading(i18n.exportWarnUserFileCannotBeImportedTitle());
		forceOverwriteDialog.setBodyStyle("fontWeight:bold;padding:13px;");
		forceOverwriteDialog.setSize(300, 100);
		forceOverwriteDialog.setHideOnButtonClick(true);
		forceOverwriteDialog.setButtons(Dialog.OKCANCEL);
		I18nConstants i18n = Registry.get(AppConstants.I18N);
		forceOverwriteDialog.getButtonById(Dialog.OK).setText(i18n.importOverwriteExistingAssignmentsButton());
		forceOverwriteDialog.addText(i18n.importOverwriteExistingAssignmentsWarning());
		
	}

	protected void onRender(Element parent, int pos) {
		
		super.onRender(parent, pos);
	}

	@Override
	protected void onResize(final int width, final int height) {
		
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
		
		String msgsFromServer = null;
		boolean hasErrors = false;
		try {

			// Getting the JSON from REST call and create an UploadModel
			
			EntityOverlay overlay = JsonUtil.toOverlay(result);
			upload = new UploadModel(overlay);
			hasErrors = upload.hasErrors(); 
			
			msgsFromServer = upload.getNotes(); 
			
			importSettings = upload.getImportSettings();
			
			
						
			// If we have errors make sure the text box gets all the attention
			if (hasErrors) {
				
				mainCardLayout.setActiveItem(errorContainer);
				 
			} else {
			
				gradebookItemModel = (ItemModel)upload.getGradebookItemModel();
	
				fixMangledHtmlNames(gradebookItemModel); 
				if (gradebookItemModel == null) {
					throw new Exception(i18n.noItemModelFound());
				}
				
				refreshSetupPanel();
			}
			
			
		} catch (Exception e) {
			
			Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(e));
			
		} finally {
			
			uploadBox.close();
		}
		
		if(!hasErrors && ((Gradebook)Registry.get(AppConstants.CURRENT)).getGradebookItemModel().getGradeType() == GradeType.PERCENTAGES
				&& importSettings.isScantron()) {
			
			getScantonOrClickerPointsWithWizard();
		}

		if (msgsFromServer != null && msgsFromServer.length() > 0){
			String severity = hasErrors ? i18n.errorOccurredGeneric() : i18n.exportWarnUserFileCannotBeImportedTitle();
			Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(severity, msgsFromServer, true, true));
		}
	}

	private void refreshSetupPanel() {
		
		
		Gradebook gradebookModel = Registry.get(AppConstants.CURRENT);
		
		if(setupPanel == null) {
			setupPanel = new ImportItemSetupPanel();
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

		
		
	}

	private native String repairString(String inStr) /*-{
		var temp_div = document.createElement('div');
		temp_div.innerHTML = inStr.replace(/>/g, "&gt;").replace(/</g, "&lt;");
		return temp_div.firstChild?temp_div.firstChild.nodeValue:''
	}-*/;

	private void fixMangledHtmlNames(ItemModel gradebookItemModel) {
		
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
		
		Gradebook gradebookModel = Registry.get(AppConstants.CURRENT);
		ItemModel rootItemModel = new ItemModel();
		rootItemModel.setItemType(ItemType.ROOT);
		rootItemModel.setName("Root");
		gradebookItemModel.setParent(rootItemModel);
		rootItemModel.add(gradebookItemModel);

		multigrade.onRefreshGradebookItems(gradebookModel, gradebookItemModel);
	}

	private void showMessageBox(String alertText, boolean overrideText) {
		
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


	private void uploadSpreadsheet(UploadModel spreadsheetModel, ImportSettings importSettings2) {
				
		Gradebook gbModel = Registry.get(AppConstants.CURRENT);

		int numberOfLearners = upload.getRows() == null ? 0 : upload.getRows().size();

		String message = new StringBuilder().append(i18n.uploadingLearnerGradesPrefix()).append(" ")
		.append(numberOfLearners).append(" ").append(i18n.uploadingLearnerGradesSuffix()).toString();

		uploadingBox = MessageBox.wait(i18n.uploadingLearnerGradesTitle(), message, i18n.uploadingLearnerGradesStatus());

		StringBuilder url = (new StringBuilder(GWT.getModuleBaseURL()))
			.append(AppConstants.REST_FRAGMENT).append("/")
			.append(AppConstants.UPLOAD_FRAGMENT).append("/")
			.append(gbModel.getGradebookUid()).append("/")
			.append(String.valueOf(gbModel.getGradebookId()));
		
		
		if(importSettings2.isForceOverwriteAssignments()) {
			url.append("/").append(AppConstants.OVERWRITE_FRAGMENT).append("/").append("true");
		}
		String maxPoints = importSettings2.getScantronMaxPoints();
		if(maxPoints != null) {
			url.append("/").append(AppConstants.MAXPNTS_FRAGMENT).append("/").append(maxPoints);
		}

		String jsonText = spreadsheetModel == null ? null : spreadsheetModel.getJSON();
		RestBuilder builder = RestBuilder.getInstance(RestBuilder.Method.PUT, url.toString());
		builder.sendRequest(200, 400, jsonText, new RestCallback() {

			

			@Override
			public void onError(Request request, Throwable caught, Integer statusCode) {
				
				if (401 == statusCode) {
					uploadingBox.close();
					forceOverwriteDialog.show();
				} else if (411 == statusCode) {
					uploadingBox.close();
					submitButton.setVisible(true);
					getScantonOrClickerPointsWithWizard();
				} else {
					onFailure(request, caught);
				}
				
			}

			@Override
			public void onFailure(Request request, Throwable caught) {
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
									//GWT.log("Could not find a model for: " + currentUid, null);
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
				} finally {
					Dispatcher.forwardEvent(GradebookEvents.SwitchGradebook.getEventType(), selectedGradebook);
					uploadingBox.close();
				}

				if (isGradingFailure) {
					Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(i18n.importGradesFailedTitle(), i18n.importGradesFailedMessage(), true, true));
				}
			}
		});
	}

	protected void getScantonOrClickerPointsWithWizard() {
		//show the wizard and ask for points possible for scantron
		WidgetInjector injector = Registry.get(AppConstants.WIDGET_INJECTOR);
		wizard = injector.getWizardProvider().get();

		// 1st and only card 
		
		Card card1 = wizard.newCard(i18n.importWizardCardTitlePointsPossible());//TODO i18n
		
		wizard.setClosable(false);
		wizard.setShowWestImageContainer(false);
		wizard.setPanelBackgroundColor("#FFFFFF");
		wizard.setContainer(this.getElement());
		
		wizard.setProgressIndicator(Wizard.Indicator.PROGRESSBAR);
		wizard.addCancelListener(new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) { /// need to max points to proceed, cancel import
				Dispatcher.forwardEvent(GradebookEvents.StopImport.getEventType());
				fileUploadPanel.clear();
			}
			
		});
		
		
		
		//get the min a max values from the data
		@SuppressWarnings("unchecked")
		List<ItemModel> gradeItems = (List<ItemModel>) setupPanel.getGradeItems(gradebookItemModel);
		List<Learner> rows = upload.getRows();
		/*
		 * this is scantron with only one key in it which is *not* in LearnerKey: an item
		 */
		ItemModel i = gradeItems.get(0);
		Double minScore = getMinScoreForItem(i, rows);
		final Double maxScore = getMaxScoreForItem(i, rows);
		
		card1.setHtmlText(i18n.importPromptScantronMaxPoints() 
				+ "<br/>"
				+ i18nTemplates.importDataMinValue("" + minScore.intValue())
				+ i18nTemplates.importDataMaxValue("" + maxScore.intValue()));
		
		card1.setTitle(i18n.importWizardCardTitlePointsPossible());
		FormPanel form = new FormPanel();
		
		FormLayout layout = new FormLayout();
		layout.setLabelAlign(LabelAlign.TOP);
		form.setLayout(layout);
		
		// GRBK-1104 : change TextField to NumberField
		final NumberField maxPointsNumberField = new InlineEditNumberField() {
			protected boolean validateValue(String value) {
				if (null == value || "".equals(value.trim())) {
					return true;
				}
				return super.validateValue(value);
			}
		};
		
		
		maxPointsNumberField.setName(ItemKey.D_PNTS.name());
		maxPointsNumberField.setEmptyText(i18nTemplates.pointsFieldEmptyText(maxScore.toString()));
		maxPointsNumberField.setFieldLabel(i18n.scantronMaxPointsFieldLabel());
		maxPointsNumberField.setFormat(DataTypeConversionUtil.getDefaultNumberFormat());
		maxPointsNumberField.setAllowDecimals(true);
		maxPointsNumberField.setAllowNegative(false);
		maxPointsNumberField.setAutoValidate(true);
		maxPointsNumberField.setMinValue(maxScore);
		maxPointsNumberField.setAllowBlank(false);
		maxPointsNumberField.setValidator(new MinMaxDoubleValidator(maxScore, i18n.itemFormPanelEditPointsInvalid()));
		form.add(maxPointsNumberField);
		
		card1.addFinishListener(new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) {
				
				Number entry = maxPointsNumberField.getValue();
				Double maxPoints = null;
				
				if (null == entry) 
					maxPoints = maxScore;
				else 
					maxPoints = Double.valueOf(entry.doubleValue());
								// TODO: i18n (Decimal format symbol)
				importSettings.setScantronMaxPoints(maxPoints.toString().substring(0, maxPoints.toString().indexOf(".")));

				@SuppressWarnings("unchecked")
				List<ItemModel> gradeItems = (List<ItemModel>) setupPanel.getGradeItems(gradebookItemModel);
				ItemModel i = gradeItems.get(0);
				
				List<Learner> rows = upload.getRows();
				for (Learner row : rows) {
					// GRBK-1105
					// Note the below string has not been vetted, but GRBK-1104 should correct this to not be able to happen. 
					String newValStr = "??"; 
					Double pnts = null;
					try {
						pnts = Double.valueOf((String)row.get(i.getIdentifier()));
					} catch (NumberFormatException e) {
						pnts = 0d;
					} catch (NullPointerException npe) {
						pnts = 0d;
					}
					boolean errorFound = false; 
					if (Double.valueOf(0).compareTo(maxPoints) != 0 ) {
						
						Double derivedPercentageVal = Double.valueOf(pnts / maxPoints * I100); 
						
						if (derivedPercentageVal.compareTo(MINIMUM_PERCENTAGE_VALUE) < 0 || derivedPercentageVal.compareTo(MAXIMUM_PERCENTAGE_VALUE) > 0) {
							
							errorFound = true; 
						}
						
						newValStr = derivedPercentageVal.toString(); 
					}
					else {
						errorFound = true; 
					}
					
					row.set(i.getIdentifier(), newValStr);
					if (errorFound)
					{
						String errorProp = DataTypeConversionUtil.buildFailedKey(i.getIdentifier());
						row.set(errorProp, "true");
						
					}
				}
				/*
				 * this is scantron or clicker with only one key in it which is *not* in LearnerKey: an item
				 */
				
				gradeItems.remove(i);
				i.setPoints(maxPoints);
				if(i.getIdentifier().startsWith(AppConstants.FAILED_FLAG)) {
					i.setIdentifier(DataTypeConversionUtil.unpackItemIdFromKey(i.getIdentifier()));
				}
				gradeItems.add(i);
				setupPanel.getItemStore().removeAll();
				refreshSetupPanel();
			}

			
		});
		
		card1.setFormPanel(form);
		
		wizard.setHeading(i18n.importWizardTitle());
		wizard.setHeaderTitle("Scantron");
		
		wizard.show();
		
		wizard.resize(0,-50);

	}
	
	/* pass in true as last param to find lowest value, false to find the highest */
	private Double getBoundaryScoreForItem(ItemModel i, List<Learner> rows, boolean findlower) {
		Double boundary = findlower ? Double.MAX_VALUE : 0d;
		String score = null;
		Double d = null;
		for (Learner row : rows) {
			try {
				score = (String) row.get(i.getIdentifier());
				if (null == score)
					continue;
				d = Double.valueOf(score);
			} catch (NumberFormatException e) {
				continue;
			}
			if(findlower){
				boundary = d<boundary ? d : boundary;
			} else {
				boundary = d>boundary ? d : boundary;
			}
			
			}
		/* set reasonable limits if no values were found */
		if (findlower) {
			if (boundary == Double.MAX_VALUE) {
				boundary = 0d;
			}
			
		} else if(boundary == 0d) {
			boundary = 100d;
		}
		return boundary;
	}
	
	private Double getMaxScoreForItem(ItemModel i, List<Learner> rows) {
		return getBoundaryScoreForItem(i, rows, false);
	}

	private Double getMinScoreForItem(ItemModel i, List<Learner> rows) {
		return getBoundaryScoreForItem(i, rows, true);
	}

	protected UploadModel getUploadModel() {
		return this.upload;
	}
}
