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

package org.sakaiproject.gradebook.gwt.client.gxt.view;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.model.FixedColumnModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModelComparer;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.ItemFormPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.ItemTreePanel;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumn;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.type.ItemType;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.data.TreeModelReader;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.TreeStore;

public class TreeView extends View {

	private ItemTreePanel treePanel;
	private ItemFormPanel formPanel;

	private TreeLoader<ItemModel> treeLoader;
	private TreeStore<ItemModel> treeStore;

	private Gradebook selectedGradebook;

	public TreeView(Controller controller, boolean isEditable) {
		super(controller);
		this.formPanel = new ItemFormPanel();
		
		
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
			treeStore.setKeyProvider(new ModelKeyProvider<ItemModel>() {  

				public String getKey(ItemModel model) {  
					return new StringBuilder()
						.append(model.getItemType().getName())
						.append(":")
						.append(model.getIdentifier()).toString();
				}

			});

			formPanel.onTreeStoreInitialized(treeStore);
		}
		
		this.treePanel = new ItemTreePanel(treeStore, isEditable, false);
	}

	@Override
	protected void handleEvent(AppEvent event) {
		switch(GradebookEvents.getEvent(event.getType()).getEventKey()) {
			case CONFIRM_DELETE_ITEM:
				onConfirmDeleteItem((ItemModel)event.getData());
				break;
			case SELECT_DELETE_ITEM:
				onConfirmDeleteItem((String)event.getData());
				break;
			case ITEM_CREATED:
				onItemCreated((ItemModel)event.getData());
				break;
			case ITEM_DELETED:
				onItemDeleted((Item)event.getData());
				break;
			case ITEM_UPDATED:
				onItemUpdated((Item)event.getData());
				break;
			case HIDE_COLUMN:
				onHideColumn((String)event.getData());
				break;
			case SINGLE_GRADE:
				onSingleGrade();
				break;
			case START_EDIT_ITEM:
				onEditItem((ItemModel)event.getData());
				break;
			case HIDE_EAST_PANEL:
				onEditItemComplete((Boolean)event.getData());
				break;
			case LOAD_ITEM_TREE_MODEL:
				onLoadItemTreeModel((Gradebook)event.getData());
				break;
			case NEW_CATEGORY:
				// GRBK-591 : If the category gets created via the main "File -> New Category",
				// menu, then the event has a null ItemModel. In this case, we just pass in the GB item model
				ItemModel itemModel = event.getData();
				if(null == itemModel) {
					Gradebook gradebookModel = Registry.get(AppConstants.CURRENT);
					itemModel = (ItemModel) gradebookModel.getGradebookItemModel();
				}
				onNewCategory(itemModel);
				break;
			case NEW_ITEM:
				onNewItem((ItemModel)event.getData());
				break;
			case REFRESH_GRADEBOOK_ITEMS:
				onRefreshGradebookItems((Gradebook)event.getData());
				break;
			case REFRESH_GRADEBOOK_SETUP:
				onRefreshGradebookSetup((Gradebook)event.getData());
				break;
			case SELECT_ITEM:
				onSelectItem((String)event.getData());
				break;
			case STARTUP:
				Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);
				onSwitchGradebook(selectedGradebook);
				break;
			case SWITCH_EDIT_ITEM:
				onSwitchEditItem((ItemModel)event.getData());
				break;
			case SWITCH_GRADEBOOK:
				onSwitchGradebook((Gradebook)event.getData());
				break;
			case FINISH_TREE_ITEM_DRAG_AND_DROP:
				// GRBK-833 : This event is dispatched in the ItemTreePanel drag and drop code section
				formPanel.setTreeItemDragAndDropMarker(true);
				break;
			case LAYOUT_ITEM_TREE_PANEL:
				treePanel.onLayout();
				break;
		}
	}

	protected void onConfirmDeleteItem(String itemModelId) {
		ItemModel itemModel = findItemByColumnId(itemModelId);

		if (itemModel != null)
			formPanel.onRequestDeleteItem(itemModel);
	}

	protected void onConfirmDeleteItem(ItemModel itemModel) {
		formPanel.onRequestDeleteItem(itemModel);
	}

	protected void onEditItem(ItemModel itemModel) {
		formPanel.onEditItem(itemModel, true);
	}

	protected void onEditItemComplete(Boolean doCommit) {
		if (doCommit.booleanValue())
			treeStore.commitChanges();
		else
			treeStore.rejectChanges();	
	}

	protected void onHideColumn(String columnId) {
		ItemModel itemModel = findItemByColumnId(columnId);

		if (itemModel != null)
			treePanel.onHideColumn(itemModel);
		else {
			// It's probably a fixed column
			FixedColumnModel fixedModel = findFixedByColumnId(columnId);
			treePanel.onHideColumn(fixedModel);
		}
	}

	protected void onItemCreated(ItemModel itemModel) {
		treePanel.onItemCreated(itemModel);
		formPanel.onItemCreated(itemModel);
	}

	protected void onItemDeleted(Item itemModel) {
		formPanel.onItemDeleted(itemModel);
	}

	protected void onItemUpdated(Item itemModel) {
		formPanel.onItemUpdated(itemModel);
	}

	protected void onLoadItemTreeModel(Gradebook selectedGradebook) {
	}

	protected void onMaskItemTree() {
	}

	protected void onNewCategory(ItemModel itemModel) {
		formPanel.onNewCategory(itemModel);
	}

	protected void onNewItem(ItemModel itemModel) {
		formPanel.onNewItem(itemModel);
	}

	protected void onRefreshGradebookItems(Gradebook gradebookModel) {////message here?
		treeStore.removeAll();
		ItemModel gradebookItemModel = (ItemModel)gradebookModel.getGradebookItemModel();
		ItemModel rootItemModel = new ItemModel();
		rootItemModel.setItemType(ItemType.ROOT);
		rootItemModel.setName("Root");
		gradebookItemModel.setParent(rootItemModel);
		rootItemModel.add(gradebookItemModel);
		treePanel.onBeforeLoadItemTreeModel(gradebookModel, rootItemModel);
		treePanel.onRefreshGradebookItems(gradebookModel, treeLoader, rootItemModel);
		formPanel.onLoadItemTreeModel(rootItemModel);
	}

	protected void onRefreshGradebookSetup(Gradebook gradebookModel) {
		treePanel.onRefreshGradebookSetup(gradebookModel, (ItemModel)gradebookModel.getGradebookItemModel());
		formPanel.onRefreshGradebookSetup(gradebookModel, (ItemModel)gradebookModel.getGradebookItemModel());
	}

	protected void onSelectItem(String itemModelId) {

		if (treeStore != null) {
			String key = new StringBuilder()
				.append(ItemType.ITEM.getName())
				.append(":")
				.append(itemModelId).toString();
			
			ItemModel m = treeStore.findModel(key);
			
			if (m != null) {
				onEditItem(m);
			} else {
				List<ItemModel> itemModels = treeStore.findModels(ItemKey.S_ID.name(), itemModelId);
				if (itemModels != null) {
					for (ItemModel itemModel : itemModels) {
						ItemType itemType = itemModel.getItemType();
						if (itemType == ItemType.ITEM) {
							onEditItem(itemModel);
							break;
						}
					}
				}
			}
		}
	}

	protected void onSingleGrade() {
		treePanel.onSingleGrade();
	}

	protected void onSwitchEditItem(ItemModel itemModel) {
		formPanel.onEditItem(itemModel, false);
	}

	@SuppressWarnings("unchecked")
	protected void onSwitchGradebook(final Gradebook selectedGradebook) {
		this.selectedGradebook = selectedGradebook;

		formPanel.onSwitchGradebook(selectedGradebook, selectedGradebook.getGradebookItemModel());
		treePanel.onSwitchGradebook(selectedGradebook);

		// FIXME: Need to send an event to show which ones are checked

		if (treeLoader == null) {
			treeLoader = new BaseTreeLoader(new TreeModelReader() {

				@Override
				protected List<? extends ModelData> getChildren(ModelData parent) {
					List visibleChildren = new ArrayList();
					List<? extends ModelData> children = super.getChildren(parent);

					for (ModelData model : children) {
						visibleChildren.add(model);
					}

					return visibleChildren;
				}
			});
		}

		if (treeStore == null) {
			treeStore = new TreeStore<ItemModel>(treeLoader);
			treeStore.setModelComparer(new ItemModelComparer());

			treePanel.onTreeStoreInitialized(treeStore, (Boolean)Registry.get(AppConstants.IS_ABLE_TO_EDIT));
			formPanel.onTreeStoreInitialized(treeStore);
		}

		onRefreshGradebookSetup(selectedGradebook);
		onRefreshGradebookItems(selectedGradebook);
	}

	protected void onUnmaskItemTree() {
	}



	private FixedColumnModel findFixedByColumnId(String fixedId) {
		FixedColumnModel fixedModel = null;

		if (selectedGradebook != null) {
			List<FixedColumn> fixedColumns = selectedGradebook.getColumns();

			for (FixedColumn current : fixedColumns) {

				if (current.getIdentifier().equals(fixedId)) {
					fixedModel = (FixedColumnModel)current;
					break;
				}

			}
		}

		return fixedModel;
	}

	private ItemModel findItemByColumnId(String itemModelId) {
		ItemModel itemModel = null;

		List<ItemModel> itemModels = treeStore.findModels(ItemKey.S_ID.name(), itemModelId);
		if (itemModels != null) {
			for (ItemModel current : itemModels) {
				ItemType itemType = current.getItemType();
				if (itemType == ItemType.ITEM) {
					itemModel = current;
					break;
				}
			}
		}

		return itemModel;
	}

	// Public accessors

	public ItemTreePanel getTreePanel() {
		return treePanel;
	}

	public ItemFormPanel getFormPanel() {
		return formPanel;
	}

	public TreeStore<ItemModel> getTreeStore() {
		return treeStore;
	}

	public void setTreeStore(TreeStore<ItemModel> treeStore) {
		this.treeStore = treeStore;
	}
}
