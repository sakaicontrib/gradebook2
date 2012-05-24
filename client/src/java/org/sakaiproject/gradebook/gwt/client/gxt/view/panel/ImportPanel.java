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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.RestBuilder;
import org.sakaiproject.gradebook.gwt.client.RestCallback;
import org.sakaiproject.gradebook.gwt.client.api.Card;
import org.sakaiproject.gradebook.gwt.client.api.ImportSettings;
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
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;
import org.sakaiproject.gradebook.gwt.client.model.key.UploadKey;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.type.ItemType;
import org.sakaiproject.gradebook.gwt.client.wizard.validators.MinMaxDoubleValidator;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.MemoryProxy;
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
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
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
	private FileUploadPanel fileUploadPanel;
	protected MessageBox uploadBox, uploadingBox;
	private UploadModel upload;
	private Dialog forceOverwriteDialog;

	private List<NotificationEvent> finishNotifications = new ArrayList<NotificationEvent> ();
	
	private MultiGradeContentPanel multigrade;
	private ImportItemSetupPanel setupPanel;

	private PagingLoader<PagingLoadResult<ModelData>> multigradeLoader;
	private ListStore<ModelData> multigradeStore;
	
	private ItemModel gradebookItemModel;

	private boolean isGradingFailure;
	
	private ImportSettings importSettings = new ImportSettingsModel();
	private Wizard wizard;
	private Wizard unMatchedLearnersDisplay;
		
	
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
		

		errorReturnButton = new Button(i18n.importPanelReturnButton());
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
		
		/// file import wizard setup
		WidgetInjector injector = Registry.get(AppConstants.WIDGET_INJECTOR);
		wizard = injector.getWizardProvider().get();
		
		wizard.setHeading(i18n.importWizardHeading());
		wizard.setHeaderTitle(i18n.importWizardTitle());
		
		wizard.setResizable(true);
		wizard.setClosable(false);
		wizard.setShowWestImageContainer(false);
		wizard.setPanelBackgroundColor("#FFFFFF");
		wizard.setContainer(fileUploadContainer.getElement());
		wizard.setHidePreviousButtonOnFirstCard(true);
		
		wizard.setProgressIndicator(Wizard.Indicator.PROGRESSBAR);
		wizard.addCancelListener(new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) { /// need to max points to proceed, cancel import
				Dispatcher.forwardEvent(GradebookEvents.StopImport.getEventType());
			}
			
		});
		
		fileUploadPanel = new FileUploadPanel(this);		
		Card card1 = wizard.newCard(i18n.importFileStep1Label());	
		card1.setFormPanel(fileUploadPanel);
		

		/*
		 * we want the progress indicator to indicate another step
		 * but that step will handled outside of the wizard. This'll 
		 * work as long as the first card has a finish listener that 
		 * closes everything up cleanly.
		 */
		
		wizard.setFinishButtonText(wizard.getNextButtonText());
		
		wizard.newCard(i18n.preparingPreview());

		card1.addCardCloseListener(new Listener<BaseEvent>() {
			
			@Override
			public void handleEvent(BaseEvent be) {
				fileUploadPanel.readFile();
				
			}
		});
		
		wizard.setSize(600, 500);
				
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
			
			EntityOverlay overlay = JsonUtil.toOverlay(unescapeHtml(result));
			upload = new UploadModel(overlay);
			hasErrors = upload.hasErrors(); 
			
			msgsFromServer = upload.getNotes(); 
			
			importSettings = upload.getImportSettings();
			
			
						
			// If we have errors make sure the text box gets all the attention
			if (hasErrors) {
				
				mainCardLayout.setActiveItem(errorContainer);
				wizard.show();
				wizard.pressPreviousButton();
				 
			} else {
			
				gradebookItemModel = (ItemModel)upload.getGradebookItemModel();
	
				fixMangledHtmlNames(gradebookItemModel); 
				if (gradebookItemModel == null) {
					throw new Exception(i18n.noItemModelFound());
				}
				
				refreshSetupPanel(importSettings.isJustStructure());
				wizard.hide();
				WidgetInjector injector = Registry.get(AppConstants.WIDGET_INJECTOR);
				unMatchedLearnersDisplay = injector.getWizardProvider().get();
				displayAnyUnmatchedLearners(unMatchedLearnersDisplay);
			}
			
			
		} catch (Exception e) {
			
			Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(e));
			
		} finally {
			uploadBox.close();
		}
		
		if(!hasErrors && importSettings.isScantron()) {
			GradeType type = ((Gradebook)Registry.get(AppConstants.CURRENT)).getGradebookItemModel().getGradeType();
			
			if( type == GradeType.PERCENTAGES || type == GradeType.LETTERS ) {
				pointsConversionWizard();
			}
		}


		if (msgsFromServer != null && msgsFromServer.length() > 0){
			String severity = hasErrors ? i18n.errorOccurredGeneric() : i18n.exportWarnUserFileCannotBeImportedTitle();
			Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(severity, msgsFromServer, true, true));
		}
	}
	
	/*
	 *  this wipes out the old wizard value to display the list, if any are found,
	 *  and if the param is null... otherwise it creates the wizard in the supplied param
	 */

	private void displayAnyUnmatchedLearners(Wizard suppliedWizard) {
		
		Wizard theWiz = wizard;
		List<LearnerModel> unmatched = getUnmatchedLearnerList(upload.getRows());
		if (unmatched.size() == 0)
			return;
		
		// unmatched users found
		if (suppliedWizard != null) {
			theWiz = suppliedWizard;
		} else {
			theWiz.reset();
		}
		
		theWiz.setHeading(i18n.usersNotFoundInSite());
		theWiz.setClosable(false);
		theWiz.setShowWestImageContainer(false);
		theWiz.setPanelBackgroundColor("#EEEEEE");
		theWiz.setContainer(this.getElement());
		
		theWiz.setProgressIndicator(Wizard.Indicator.NONE);
		theWiz.setHidePreviousButtonOnFirstCard(true);
		
		theWiz.hideCancelButton(true);
	    
	    theWiz.setFinishButtonText(i18n.done());
	    theWiz.setHideOnFinish(true);
	    
	    theWiz.setContainer(this.getElement());
	    
	    theWiz.setHideHeaderPanel(true);
	    
	    theWiz.setSize(350, 400);
			    
	    VerticalPanel layout = new VerticalPanel();
		    
	    Card card1 = theWiz.newCard("");
	    
	    card1.setLayoutContainer(layout);
	    
	    ColumnConfig idCol = new ColumnConfig(LearnerKey.S_UID.name(), i18n.studentPanelHeadingId(), 200);

	    GridCellRenderer<LearnerModel> renderer = new GridCellRenderer<LearnerModel>() {  

			public Object render(LearnerModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<LearnerModel> store, Grid<LearnerModel> grid) {
				return model.getIdentifier();
			}  
	    	  };  
	    idCol.setRenderer(renderer);  
	    List<ColumnConfig> cc = new ArrayList<ColumnConfig>();
	    cc.add(idCol);
	    ColumnModel cm = new ColumnModel(cc);
	    
		
		ListStore<LearnerModel> store = 
			new ListStore<LearnerModel>(
					new BaseListLoader<ListLoadResult<LearnerModel>>(
							new MemoryProxy<LearnerModel>(unmatched)));
		
		store.setModelComparer(new EntityModelComparer<LearnerModel>(LearnerKey.S_UID.name()));
		store.setDefaultSort(LearnerKey.S_UID.name(), SortDir.ASC);
		
		
	    
	    Grid<LearnerModel> grid = new Grid<LearnerModel>(store, cm);
	    	    
	    grid.setHeight(250);
	    grid.setWidth(250);
	    grid.getColumnModel().setColumnWidth(0, 250);
	    
	    grid.getView().setAutoFill(true);
	    grid.setView(new GridView() {

			@Override
			protected Menu createContextMenu(int colIndex) {
				Menu m = super.createContextMenu(colIndex);
				Component thing = m.getItem(2);
				m.remove(thing);
				return m;
			}
	    	
	    });

	    layout.add(grid);
	    layout.setScrollMode(Scroll.AUTO);
	    grid.getStore().getLoader().load();

		
	    theWiz.show();
	}

	private List<LearnerModel> getUnmatchedLearnerList(List<Learner> rows) {
		List<LearnerModel> rv = new ArrayList<LearnerModel>();
		for (Learner l : rows) {
			if (l.getUserNotFound()) {
				rv.add((LearnerModel)l);
			}
		}
		
		
		return rv;
	}

	private void refreshSetupPanel(boolean hideGrid) {
		
		
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

			BorderLayoutData westData = null;
			if(!hideGrid) {
				westData = new BorderLayoutData(LayoutRegion.WEST, 550, 200, 800);
			}else{
				westData = new BorderLayoutData(LayoutRegion.CENTER, 550, 200, 800);
			}
			westData.setSplit(true);  
			westData.setCollapsible(!hideGrid);  
			westData.setMargins(new Margins(5));

			BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER); 
			centerData.setMinSize(100);
			centerData.setMargins(new Margins(5, 0, 5, 0)); 

			centerCardLayoutContainer.add(multigrade);
			centerCardLayout.setActiveItem(multigrade);

			borderLayoutContainer.add(setupPanel, westData);
			borderLayoutContainer.add(centerCardLayoutContainer, centerData);
			multigrade.setHeight(mainCardLayoutContainer.getHeight() - 100);
			if (hideGrid) {
				centerCardLayoutContainer.hide();
				}
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

		String message = null;
		if(!importSettings2.isJustStructure()) {
			message = new StringBuilder().append(i18n.uploadingLearnerGradesPrefix()).append(" ")
			.append(numberOfLearners).append(" ").append(i18n.uploadingLearnerGradesSuffix()).toString();
		} else {
			message = i18n.justStructureImportingMessage();
		}
		

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
					pointsConversionWizard();
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

					uploadingBox.setProgressText(i18n.loading());
					
					finishNotifications.add(new NotificationEvent(i18n.importCompletedTitle(), i18n.importCompleted(), true));

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
				
				Dispatcher.forwardEvent(GradebookEvents.StopImport.getEventType());
				Dispatcher.forwardEvent(GradebookEvents.LayoutItemTreePanel.getEventType());
				fileUploadPanel.clear();
			}
		});
	}
	
	
	public void startImportWizard() {
		
		wizard.show();
		
		//wizard.resize(150,0);
		
	}

	protected void pointsConversionWizard() {
		//show the wizard and ask for points possible for scantron
		WidgetInjector injector = Registry.get(AppConstants.WIDGET_INJECTOR);
		wizard = injector.getWizardProvider().get();

		// 1st and only card 
		
		Card card1 = wizard.newCard(i18n.importWizardCardTitlePointsPossible());
		
		wizard.setClosable(false);
		wizard.setShowWestImageContainer(false);
		wizard.setPanelBackgroundColor("#FFFFFF");
		wizard.setContainer(this.getElement());
		
		wizard.setProgressIndicator(Wizard.Indicator.NONE);
		wizard.setHidePreviousButtonOnFirstCard(true);
		wizard.addCancelListener(new Listener<BaseEvent>() {

			public void handleEvent(BaseEvent be) { /// need to max points to proceed, cancel import
				Dispatcher.forwardEvent(GradebookEvents.StopImport.getEventType());
				fileUploadPanel.clear();
			}
			
		});
		
		
		
		//get the min a max values from the data
		@SuppressWarnings("unchecked")
		final List<ItemModel> gradeItems = (List<ItemModel>) setupPanel.getGradeItems(gradebookItemModel);
		final List<Learner> rows = upload.getRows();
		/*
		 * this is scantron with only one key in it which is *not* in LearnerKey: an item
		 */
		final ItemModel i = gradeItems.get(0);
		Double minScore = getMinScoreForItem(i, rows);
		Double maxScoreFromFile = getMaxScoreForItem(i, rows);		
		
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
		
		// scan the rows and see if the first non null value is a letter or a number
		boolean importingLetters = false;
		for (Learner l: rows) {
			String o = l.get(i.getIdentifier() + AppConstants.ACTUAL_SCORE_SUFFIX);
			if (null == o)
				o = l.get(i.getIdentifier());
			if (null == o)
				continue;
			
			if (o.trim().matches("^[A-Za-z]")) {
				importingLetters = true;
				break;
			}
		}
		StringBuffer cardInfo = new StringBuffer(i18n.importPromptScantronMaxPoints() + "<br/>");

		if (!importingLetters) {
			cardInfo.append(i18nTemplates.importDataMinValue(""+minScore.intValue()))
			        .append(i18nTemplates.importDataMaxValue("" + maxScoreFromFile.intValue()));
		} else {
			cardInfo.append(i18n.gradeTypeLetters());
			maxScoreFromFile = 1d;
		}
			
		card1.setHtmlText(cardInfo.toString());
		final Double maxScore = maxScoreFromFile;
		
		final boolean assumeLetterImport = importingLetters;
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
				Double maxPoints = null;
				Number entry = maxPointsNumberField.getValue();
				
				
				if (null == entry) 
					maxPoints = maxScore;
				else 
					maxPoints = Double.valueOf(entry.doubleValue());
								// TODO: i18n (Decimal format symbol)
				//importSettings.setScantronMaxPoints(maxPoints.toString().substring(0, maxPoints.toString().indexOf(".")));
				
				if(!assumeLetterImport) { 
					
					for (Learner row : rows) {
						// GRBK-1105
						// Note the below string has not been vetted, but GRBK-1104 should correct this to not be able to happen. 
						String newValStr = "??"; 
						
						
						Double pnts = null;
						Object o = row.get(i.getIdentifier());
						if (o != null) {
							try {
								pnts = Double.valueOf((String)o);
							} catch (NumberFormatException e) {
								try {
									pnts = Double.valueOf((String)row.get(i.getIdentifier() + AppConstants.ACTUAL_SCORE_SUFFIX));
								} catch (NumberFormatException nfe2) {
									pnts = 0d;
								}
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
							String errorProp = DataTypeConversionUtil.buildFailedKey(i.getIdentifier());
							if (errorFound)
							{
								row.set(errorProp, "true");
								
							} else {
								row.set(errorProp, null);
							}
						}
							
						
					}
					/*
					 * this is scantron or clicker with only one key in it which is *not* in LearnerKey: an item
					 */
					
					
					if(i.getIdentifier().startsWith(AppConstants.FAILED_FLAG)) {
						i.setIdentifier(DataTypeConversionUtil.unpackItemIdFromKey(i.getIdentifier()));
					}
					
				}
				
				gradeItems.remove(i);
				i.setPoints(maxPoints);
				gradeItems.add(i);
				setupPanel.getItemStore().removeAll();
				refreshSetupPanel(false);
			}

			
		});
		
		card1.setFormPanel(form);
		
		wizard.setHeading(i18n.importWizardHeading());
		
		if (assumeLetterImport){
			wizard.setFinishButtonText(i18n.wizardDefaultFinishButton());
			wizard.setHeaderTitle(i18n.scantronMaxPointsFieldLabel());
		} else {
			wizard.setFinishButtonText(i18n.importPointsConversionFinish());
			wizard.setHeaderTitle(i18n.importPointsConversionTitle());
		}
		
		wizard.show();
		
		wizard.setSize(540, 350);

	}
	
	/* pass in true as last param to find lowest value, false to find the highest */
	private Double getBoundaryScoreForItem(ItemModel i, List<Learner> rows, boolean findlower) {
		Double boundary = findlower ? Double.MAX_VALUE : 0d;
		String score = null;
		Double d = null;
		String id = i.getIdentifier();
		Gradebook gb = Registry.get(AppConstants.CURRENT);
		if (gb != null && gb.getGradebookItemModel().getGradeType().equals(GradeType.LETTERS)) {
			id += AppConstants.ACTUAL_SCORE_SUFFIX;
		}
		for (Learner row : rows) {
			try {
				score = (String) row.get(id);
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

	public void finish() {
		for (NotificationEvent event : finishNotifications) {
			Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), event);
		}
		
	}
	
	private String unescapeHtml(String input) {
		return filterTextThroughTextAreaDOMElement(input);
	
	};
	
	private native String filterTextThroughTextAreaDOMElement(String input) /*-{
		var y = document.createElement('textarea');
		y.innerHTML = input;
		return y.value;
	
	
	}-*/;

}
