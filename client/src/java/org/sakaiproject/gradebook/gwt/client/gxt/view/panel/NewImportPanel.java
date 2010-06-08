package org.sakaiproject.gradebook.gwt.client.gxt.view.panel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.RestBuilder;
import org.sakaiproject.gradebook.gwt.client.RestCallback;
import org.sakaiproject.gradebook.gwt.client.gxt.ItemModelProcessor;
import org.sakaiproject.gradebook.gwt.client.gxt.JsonUtil;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ShowColumnsEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityOverlay;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModelComparer;
import org.sakaiproject.gradebook.gwt.client.gxt.model.LearnerModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.UploadModel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.AppView;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.MultiGradeContentPanel.RefreshAction;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;
import org.sakaiproject.gradebook.gwt.client.model.key.UploadKey;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.ItemType;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.PagingModelMemoryProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.data.TreeModelReader;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
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
	private FileUploadField file;
	private LayoutContainer fileUploadContainer;
	private FormPanel fileUploadPanel;
	private MessageBox uploadBox, uploadingBox;
	private UploadModel upload;

	private String msgsFromServer;

	private MultiGradeContentPanel multigrade;
	//private ItemTreePanel treePanel;
	private ItemSetupPanel setupPanel;
	private ItemFormPanel formPanel;

	private PagingLoader<PagingLoadResult<ModelData>> multigradeLoader;
	private ListStore<ModelData> multigradeStore;
	private TreeStore<ItemModel> treeStore;
	private TreeLoader<ItemModel> treeLoader;

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

		submitButton = new Button("Next");
		submitButton.setMinWidth(120);
		submitButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {	
				submitButton.setVisible(false);

				List<ItemModel> rootItems = treeStore.getRootItems();
				if (rootItems != null && rootItems.size() > 0) {
					ItemModel rootItem = rootItems.get(0);
					if (rootItem.getItemType() == ItemType.GRADEBOOK) {
						upload.setGradebookItemModel(rootItem);
					} else if (rootItem.getItemType() == ItemType.ROOT && rootItem.getChildCount() > 0) {
						ItemModel gradebookItem = (ItemModel)rootItem.getChild(0);
						upload.setGradebookItemModel(gradebookItem);
					}
				}

				uploadSpreadsheet(upload);

			}
		});
		borderLayoutContainer.addButton(submitButton);

		cancelButton = new Button("Cancel");
		cancelButton.setMinWidth(120);
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				Dispatcher.forwardEvent(GradebookEvents.StopImport.getEventType());
				fileUploadPanel.clear();
			}
		});

		borderLayoutContainer.addButton(cancelButton);

		errorReturnButton = new Button("Return");
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
		fileUploadContainer.add(buildFileUploadPanel());

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
		super.onResize(width, height);

		if (multigrade != null)
			multigrade.setHeight(height - 100);

	}

	private FormPanel buildFileUploadPanel() {
		final Gradebook gbModel = Registry.get(AppConstants.CURRENT);

		FormLayout formLayout = new FormLayout();
		formLayout.setDefaultWidth(350);
		formLayout.setLabelWidth(120);

		fileUploadPanel = new FormPanel();

		fileUploadPanel.setHeaderVisible(false);

		String action = new StringBuilder().append(GWT.getHostPageBaseURL())
		.append(AppConstants.IMPORT_SERVLET).toString();

		fileUploadPanel.setFrame(true);
		fileUploadPanel.setAction(action);
		fileUploadPanel.setEncoding(Encoding.MULTIPART);
		fileUploadPanel.setMethod(Method.POST);
		fileUploadPanel.setPadding(4);
		fileUploadPanel.setButtonAlign(HorizontalAlignment.RIGHT);

		fileUploadPanel.setLayout(formLayout);


		file = new FileUploadField() {
			@Override
			protected void onChange(ComponentEvent ce) {
				super.onChange(ce);
			}
		};
		file.setAllowBlank(false);
		file.setFieldLabel(i18n.fileLabel());
		file.setName("Test");

		fileUploadPanel.add(file);

		HiddenField<String> gradebookUidField = new HiddenField<String>();
		gradebookUidField.setName("gradebookUid");
		gradebookUidField.setValue(gbModel.getGradebookUid());
		fileUploadPanel.add(gradebookUidField);


		/*CheckBox preventScantronOverwrite = new CheckBox();
		preventScantronOverwrite.setFieldLabel(i18n.preventScantronOverwrite());
		preventScantronOverwrite.setName("preventScantronOverwrite");
		fileUploadPanel.add(preventScantronOverwrite);*/

		Button submitButton = new Button(i18n.nextButton());
		submitButton.setMinWidth(120);
		submitButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				readFile();
			}

		});
		fileUploadPanel.addButton(submitButton);

		Button cancelButton = new Button(i18n.cancelButton());
		cancelButton.setMinWidth(120);
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				Dispatcher.forwardEvent(GradebookEvents.StopImport.getEventType());
				fileUploadPanel.clear();
			}
		});

		fileUploadPanel.addButton(cancelButton);

		fileUploadPanel.addListener(Events.Submit, new Listener<FormEvent>() {

			public void handleEvent(FormEvent fe) {
				readSubmitResponse(fe.getResultHtml());
			}

		});

		return fileUploadPanel;
	}

	private native String repairString(String inStr) /*-{
		var temp_div = document.createElement('div');
		temp_div.innerHTML = inStr.replace(/>/g, "&gt;").replace(/</g, "&lt;");
		return temp_div.firstChild?temp_div.firstChild.nodeValue:''
	}-*/;

	private void readFile() {

		if (file.getValue() != null && file.getValue().trim().length() > 0) {
			uploadBox = MessageBox.wait(i18n.importProgressTitle(), i18n.importReadingFileMessage(), i18n.importParsingMessage());
			fileUploadPanel.submit();
		}
	}

	private void readSubmitResponse(String result) {

		try {

			EntityOverlay overlay = JsonUtil.toOverlay(result);
			upload = new UploadModel(overlay);

			boolean hasErrors = upload.hasErrors(); //DataTypeConversionUtil.checkBoolean(getBoolean(jsonObject, "hasErrors")); 
			//boolean hasAssignmentNameIssueForScantron = upload.isNotifyAssignmentName(); //DataTypeConversionUtil.checkBoolean(getBoolean(jsonObject, "notifyAssignmentName")); 

			msgsFromServer = upload.getNotes(); //getString(jsonObject, "notes");

			// If we have errors we want to do something different
			if (hasErrors) {
				errorContainer.addText(msgsFromServer); 
				mainCardLayout.setActiveItem(errorContainer);
				return; 
			} 

			if (treeLoader == null) {
				treeLoader = new BaseTreeLoader<ItemModel>(new TreeModelReader()) {

					@Override
					public boolean hasChildren(ItemModel parent) {

						if (parent.getItemType() != ItemType.ITEM)
							return true;

						if (parent instanceof TreeModel) {
							return !((TreeModel) parent).isLeaf();
						}
						return false;
					}
				};
			}

			if (treeStore == null) {
				treeStore = new TreeStore<ItemModel>(treeLoader);
				treeStore.setModelComparer(new ItemModelComparer<ItemModel>());
				ModelKeyProvider<ItemModel> modelKeyProvider = new ModelKeyProvider<ItemModel>() {  

					public String getKey(ItemModel model) {  
						return new StringBuilder()
						.append(model.getItemType().getName())
						.append(":")
						.append(model.getIdentifier()).toString();
					}

				};

				treeStore.setKeyProvider(modelKeyProvider);
			}

			if(setupPanel == null) {
				setupPanel = new ItemSetupPanel();
			}

			//			if (treePanel == null) {
			//				treePanel = new ItemTreePanel(treeStore, true, true) {
			//					
			//					@Override
			//					protected void sendShowColumnsEvent(ShowColumnsEvent event) {
			//						multigrade.onShowColumns(event);
			//					}
			//					
			//					@Override
			//					protected void doNewCategory(Item item) {
			//						//Dispatcher.forwardEvent(GradebookEvents.NewCategory.getEventType(), item);
			//					}
			//					
			//					@Override
			//					protected void doNewItem(Item item) {
			//						//Dispatcher.forwardEvent(GradebookEvents.NewItem.getEventType(), item);
			//					}
			//					
			//					@Override
			//					protected void doDeleteItem(Item item) {
			//						//Dispatcher.forwardEvent(GradebookEvents.ConfirmDeleteItem.getEventType(), item);
			//					}
			//					
			//					@Override
			//					protected void doChangeEditItem(Item item) {
			//						//Dispatcher.forwardEvent(GradebookEvents.SwitchEditItem.getEventType(), item);
			//						formPanel.onEditItem((ItemModel)item, true);
			//					}
			//					
			//					@Override
			//					protected void doEditItem(Item item) {
			//						//Dispatcher.forwardEvent(GradebookEvents.StartEditItem.getEventType(), item);
			//						formPanel.onEditItem((ItemModel)item, true);
			//					}
			//					
			//				};
			//			}

			if (formPanel == null) {
				formPanel = new ItemFormPanel() {

					protected void sendCancelEvent() {
						centerCardLayout.setActiveItem(multigrade);
					}

					protected void sendItemCreateEvent(ItemModel item, boolean close) {
						// Can't create items right now in import panel
					}

					protected void sendItemDeleteEvent(ItemModel item) {
						// Can't delete items right now in import panel
					}

					protected void sendItemUpdateEvent(Record record, final ItemModel item, boolean close) {

						if (item != null) {
							switch (item.getItemType()) {
							case GRADEBOOK:
								refreshGradebookItemModel(item);
								return;
							case CATEGORY:
								record.commit(false);
								return;
							case ITEM:
								ItemModel gradebookItemModel = (ItemModel)upload.getGradebookItemModel();

								boolean doFullRefresh = localUpdateItem(gradebookItemModel, item, record);

								if (doFullRefresh) {
									doRefreshScreen(gradebookItemModel);
								} 

								break;
							}
						}

						formPanel.clearChanges();
						formPanel.clearSelected();

						if (close) {
							sendCancelEvent();
						} else {
							formPanel.onEditItem(item, true);
						}
					}


					private void doRefreshScreen(ItemModel gradebookItemModel) {
						Gradebook gradebookModel = Registry.get(AppConstants.CURRENT);
						refreshGradebookItemModel(gradebookItemModel);
						multigrade.onRefreshGradebookItems(gradebookModel, gradebookItemModel);
						multigrade.doRefreshGrid(RefreshAction.REFRESHCOLUMNS, false, gradebookModel.getConfigurationModel(), gradebookModel.getColumns(), gradebookItemModel);
					}

					protected void showDeleteScreen() {
						centerCardLayout.setActiveItem(formPanel);
					}

					protected void showEditScreen(AppView.EastCard activeCard) {
						switch (activeCard) {
						case EDIT_GRADEBOOK:
							formPanel.setHeading(i18n.editGradebookHeading());
							break;
						case EDIT_CATEGORY:
							formPanel.setHeading(i18n.editCategoryHeading());
							break;
						case EDIT_ITEM:
							formPanel.setHeading(i18n.editItemHeading());
							break;
						}
						centerCardLayout.setActiveItem(formPanel);
					}

					protected void hideFormPanel() {
						centerCardLayout.setActiveItem(multigrade);
					}
				};

			}

			Gradebook gradebookModel = Registry.get(AppConstants.CURRENT);
			ItemModel gradebookItemModel = (ItemModel)upload.getGradebookItemModel();
			
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

				BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 400, 100, 800);  
				westData.setSplit(true);  
				westData.setCollapsible(true);  
				westData.setMargins(new Margins(5));

				BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER); 
				centerData.setMinSize(100);
				centerData.setMargins(new Margins(5, 0, 5, 0)); 

				centerCardLayoutContainer.add(multigrade);
				centerCardLayoutContainer.add(formPanel);
				centerCardLayout.setActiveItem(multigrade);

				borderLayoutContainer.add(setupPanel, westData);
				//borderLayoutContainer.add(treePanel, westData);
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


	private boolean localUpdateItem(ItemModel gradebookItemModel, final ItemModel item, final Record record) {
		boolean doFullRefresh = false;

		final CategoryType categoryType = gradebookItemModel.getCategoryType();

		Map<String, Object> changes = record.getChanges();
		boolean isNameUpdated = changes != null && changes.get(ItemKey.S_NM.name()) != null;
		boolean isCategoryUpdated = changes != null && changes.get(ItemKey.L_CTGRY_ID.name()) != null;
		boolean isCategoryPercentChanged = changes != null && changes.get(ItemKey.D_PCT_CTGRY.name()) != null;
		boolean isPointsChanged = changes != null && changes.get(ItemKey.D_PNTS.name()) != null;
		boolean isIncludedChanges = changes != null && changes.get(ItemKey.B_INCLD.name()) != null;

		final boolean isWeighted = categoryType == CategoryType.WEIGHTED_CATEGORIES;
		final boolean mayInvalidateWeightedCalculations = isIncludedChanges || (item.isIncluded() && isCategoryPercentChanged);
		final boolean mayInvalidatePointBasedCalculations = isIncludedChanges ||  (item.isIncluded() && isPointsChanged);

		final Long oldCategoryId = (Long)changes.get(ItemKey.L_CTGRY_ID.name());
		final Long newCategoryId = (Long)record.get(ItemKey.L_CTGRY_ID.name());

		// Category is only really updated if the old and new category ids differ
		final boolean doUpdateCategory = isCategoryUpdated && oldCategoryId != null && newCategoryId != null && !oldCategoryId.equals(newCategoryId);

		// We need to update the category first 
		if (doUpdateCategory || mayInvalidateWeightedCalculations || mayInvalidatePointBasedCalculations) {

			//if (mayImpactCalculations && ) {
			//	item.setNotCalculable(true);
			//}

			ItemModelProcessor processor = new ItemModelProcessor(gradebookItemModel) {

				public void doCategory(Item categoryModel) {

					// Add the item to the new parent
					if (categoryModel.getCategoryId() != null) {

						if (categoryModel.getCategoryId().equals(newCategoryId)) {
							// Ensure that we can't make an item included if it's parent is not
							if (!((ItemModel)categoryModel).isIncluded())
								item.setIncluded(Boolean.FALSE);

							// We will definitely screw up calculations if we're not in weighted mode and we change points
							boolean doesImpactCalculations = mayInvalidatePointBasedCalculations;

							// If we are in weighted mode and we're not doing "weight by points", then it depends on percent category changes
							if (isWeighted) {
								boolean isWeightByPoints = DataTypeConversionUtil.checkBoolean(((ItemModel)categoryModel).getEnforcePointWeighting());

								if (!isWeightByPoints) {
									doesImpactCalculations = mayInvalidateWeightedCalculations;
								}
							}

							if (doesImpactCalculations) {
								if (((ItemModel)categoryModel).isIncluded())
									((ItemModel)categoryModel).setNotCalculable(true);

								item.setNotCalculable(true);
							}

							if (doUpdateCategory) {
								int count = ((ItemModel)categoryModel).getChildCount();
								((ItemModel)categoryModel).insert(item, count);
							}
						}

					}

				}

				public void doItem(Item parent, Item itemModel, int childIndex) {
					// Remove the item from its old parent

					// Find same item by string id (since this may be a new item)
					// But make sure you're not removing it from the new parent
					if (doUpdateCategory && itemModel.getIdentifier().equals(item.getIdentifier())
							&& !parent.getCategoryId().equals(newCategoryId)) {

						if (parent != null) {
							ItemModel p = (ItemModel)parent;
							if (p.isIncluded())
								p.setNotCalculable(true);
							int i = p.indexOf((ItemModel)itemModel);
							p.removeChild(i);
						}
					}
				}

			};

			processor.process();
			doFullRefresh = true;
		}

		// Handle the case where we want to replace an existing item with this one
		if (isNameUpdated) {

			//final CategoryType categoryType = gradebookItemModel.getCategoryType();

			ItemModelProcessor processor = new ItemModelProcessor(gradebookItemModel) {

				public void doItem(Item parent, Item itemModel, int childIndex) {

					// If there is an existing item with this name
					if (itemModel.getName().equals(item.getName()) &&
							!itemModel.getIdentifier().equals(item.getIdentifier())) {

						// If we're in no categories mode, or the category ids match
						if (categoryType == CategoryType.NO_CATEGORIES 
								|| item.getCategoryId().equals(itemModel.getCategoryId())) {

							result = itemModel;

							boolean isNotifyAssignmentName = upload.isNotifyAssignmentName();
							Long itemId = item.getItemId();
							if (parent != null && isNotifyAssignmentName && 
									(itemId == null || itemId.equals(Long.valueOf(-1l)))) {
								ItemModel p = (ItemModel)parent;
								int i = p.indexOf((ItemModel)itemModel);
								p.removeChild(i);
							}
						}
					}
				}

			};

			processor.process();

			Item existingItem = processor.getResult();
			if (existingItem != null) {
				boolean isNotifyAssignmentName = upload.isNotifyAssignmentName();
				Long itemId = item.getItemId();
				if (isNotifyAssignmentName && 
						(itemId == null || itemId.equals(Long.valueOf(-1l)))) {
					item.setItemId(existingItem.getItemId());
					doFullRefresh = true;
				} else {
					StringBuilder builder = new StringBuilder();
					builder.append("There is already an existing item called \"").append(existingItem.getName()).append("\" ")
					.append("Please enter a different name for the grade item.");

					record.reject(false);
					Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent("Warning", builder.toString(), true));
					return false;
				}

			} else {
				doFullRefresh = true;
			}
		} 

		record.commit(false);

		return doFullRefresh;
	}

	private void refreshGradebookItemModel(ItemModel gradebookItemModel) {
		Gradebook gradebookModel = Registry.get(AppConstants.CURRENT);
		treeStore.removeAll();
		ItemModel rootItemModel = new ItemModel();
		rootItemModel.setItemType(ItemType.ROOT);
		rootItemModel.setName("Root");
		gradebookItemModel.setParent(rootItemModel);
		rootItemModel.add(gradebookItemModel);


		//		treePanel.onBeforeLoadItemTreeModel(gradebookModel, rootItemModel);
		//		treePanel.onRefreshGradebookItems(gradebookModel, treeLoader, rootItemModel);
		//		treePanel.onRefreshGradebookSetup(gradebookModel, gradebookItemModel);

		multigrade.onRefreshGradebookItems(gradebookModel, gradebookItemModel);

		formPanel.onRefreshGradebookSetup(gradebookModel, gradebookItemModel);
		formPanel.onSwitchGradebook(gradebookModel, gradebookItemModel);
		formPanel.onTreeStoreInitialized(treeStore);
		formPanel.onLoadItemTreeModel(rootItemModel);
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


	private void uploadSpreadsheet(UploadModel spreadsheetModel) {
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
						treeStore.removeAll();
						ItemModel rootItemModel = new ItemModel();
						rootItemModel.setItemType(ItemType.ROOT);
						rootItemModel.setName("Root");
						gradebookItem.setParent(rootItemModel);
						rootItemModel.add(gradebookItem);
						//						treePanel.onBeforeLoadItemTreeModel(selectedGradebook, rootItemModel);
						//						treePanel.onRefreshGradebookItems(selectedGradebook, treeLoader, rootItemModel);
						//						treePanel.onRefreshGradebookSetup(selectedGradebook, gradebookItem);
						//setupPanel.onRender(rootItemModel);

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
