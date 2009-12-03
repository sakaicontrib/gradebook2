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
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.AltItemTreePanel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.ItemFormPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.ItemTreePanel;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumnModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModelComparer;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.data.TreeModelReader;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.TreeStore;

public class TreeView extends View {

	private AltItemTreePanel treePanel;
	private ItemFormPanel formPanel;

	private TreeLoader<ItemModel> treeLoader;
	private TreeStore<ItemModel> treeStore;

	private GradebookModel selectedGradebook;

	private boolean isInitialized;

	public TreeView(Controller controller, I18nConstants i18n, boolean isEditable) {
		super(controller);
		this.formPanel = new ItemFormPanel(i18n);
		this.isInitialized = false;
		
		
		if (treeLoader == null) {
			treeLoader = new BaseTreeLoader<ItemModel>(new TreeModelReader() {

				@Override
				protected List<? extends ModelData> getChildren(ModelData parent) {
					List visibleChildren = new ArrayList();
					List<? extends ModelData> children = super.getChildren(parent);

					for (ModelData model : children) {
						visibleChildren.add(model);
					}

					return visibleChildren;
				}
			}) {

				@Override
				public boolean hasChildren(ItemModel parent) {
					
					if (parent.getItemType() == Type.CATEGORY)
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

			//treePanel.onTreeStoreInitialized(treeStore, selectedGradebook.isUserAbleToEditAssessments());
			formPanel.onTreeStoreInitialized(treeStore);
		}
		
		this.treePanel = new AltItemTreePanel(treeStore, i18n, isEditable);
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
				onItemDeleted((ItemModel)event.getData());
				break;
			case ITEM_UPDATED:
				onItemUpdated((ItemModel)event.getData());
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
				onLoadItemTreeModel((GradebookModel)event.getData());
				break;
			case NEW_CATEGORY:
				onNewCategory((ItemModel)event.getData());
				break;
			case NEW_ITEM:
				onNewItem((ItemModel)event.getData());
				break;
			case REFRESH_GRADEBOOK_ITEMS:
				onRefreshGradebookItems((GradebookModel)event.getData());
				break;
			case REFRESH_GRADEBOOK_SETUP:
				onRefreshGradebookSetup((GradebookModel)event.getData());
				break;
			case SELECT_ITEM:
				onSelectItem((String)event.getData());
				break;
			case STARTUP:
				GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
				onSwitchGradebook(selectedGradebook);
				break;
			case SWITCH_EDIT_ITEM:
				onSwitchEditItem((ItemModel)event.getData());
				break;
			case SWITCH_GRADEBOOK:
				onSwitchGradebook((GradebookModel)event.getData());
				break;
			case USER_CHANGE:
				onUserChange((UserEntityAction<?>)event.getData());
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

	protected void onItemDeleted(ItemModel itemModel) {
		formPanel.onItemDeleted(itemModel);
	}

	protected void onItemUpdated(ItemModel itemModel) {
		formPanel.onItemUpdated(itemModel);
	}

	protected void onLoadItemTreeModel(GradebookModel selectedGradebook) {
	}

	protected void onMaskItemTree() {
		treePanel.onMaskItemTree();
	}

	protected void onNewCategory(ItemModel itemModel) {
		formPanel.onNewCategory(itemModel);
	}

	protected void onNewItem(ItemModel itemModel) {
		formPanel.onNewItem(itemModel);
	}

	protected void onRefreshGradebookItems(GradebookModel gradebookModel) {
		onMaskItemTree();
		treeStore.removeAll();
		ItemModel gradebookItemModel = gradebookModel.getGradebookItemModel();
		ItemModel rootItemModel = new ItemModel();
		rootItemModel.setItemType(Type.ROOT);
		rootItemModel.setName("Root");
		gradebookItemModel.setParent(rootItemModel);
		rootItemModel.add(gradebookItemModel);
		treePanel.onBeforeLoadItemTreeModel(gradebookModel, rootItemModel);
		treePanel.onRefreshGradebookItems(gradebookModel, treeLoader, rootItemModel);
		formPanel.onLoadItemTreeModel(rootItemModel);

		//treePanel.expandTrees();
		//treePanel.layout();
		onUnmaskItemTree();
	}

	protected void onRefreshGradebookSetup(GradebookModel gradebookModel) {
		treePanel.onRefreshGradebookSetup(gradebookModel);
	}

	protected void onSelectItem(String itemModelId) {

		if (treeStore != null) {
			List<ItemModel> itemModels = treeStore.findModels(ItemModel.Key.ID.name(), itemModelId);
			if (itemModels != null) {
				for (ItemModel itemModel : itemModels) {
					Type itemType = itemModel.getItemType();
					if (itemType == Type.ITEM) {
						onEditItem(itemModel);
						break;
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
	protected void onSwitchGradebook(final GradebookModel selectedGradebook) {
		this.selectedGradebook = selectedGradebook;

		formPanel.onSwitchGradebook(selectedGradebook);
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

			treePanel.onTreeStoreInitialized(treeStore, selectedGradebook.isUserAbleToEditAssessments());
			formPanel.onTreeStoreInitialized(treeStore);
		}

		onRefreshGradebookSetup(selectedGradebook);
		onRefreshGradebookItems(selectedGradebook);
	}

	protected void onUnmaskItemTree() {
		treePanel.onUnmaskItemTree();
		formPanel.onActionCompleted();
	}

	protected void onUserChange(UserEntityAction<?> action) {
		treePanel.onUserChange(action);
	}

	private FixedColumnModel findFixedByColumnId(String fixedId) {
		FixedColumnModel fixedModel = null;

		if (selectedGradebook != null) {
			List<FixedColumnModel> fixedColumns = selectedGradebook.getColumns();

			for (FixedColumnModel current : fixedColumns) {

				if (current.getIdentifier().equals(fixedId)) {
					fixedModel = current;
					break;
				}

			}
		}

		return fixedModel;
	}

	private ItemModel findItemByColumnId(String itemModelId) {
		ItemModel itemModel = null;

		List<ItemModel> itemModels = treeStore.findModels(ItemModel.Key.ID.name(), itemModelId);
		if (itemModels != null) {
			for (ItemModel current : itemModels) {
				Type itemType = current.getItemType();
				if (itemType == Type.ITEM) {
					itemModel = current;
					break;
				}
			}
		}

		return itemModel;
	}

	// Public accessors

	public AltItemTreePanel getTreePanel() {
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


	// Helper methods

	private boolean isTreeRefreshUnnecessary(GradebookModel selectedGradebook) {
		// First thing we need to do here is decide whether we can avoid making expensive ui changes
		ItemModel oldGradebookItemModel = this.selectedGradebook == null ? null : this.selectedGradebook.getGradebookItemModel();
		ItemModel newGradebookItemModel = selectedGradebook == null ? null : selectedGradebook.getGradebookItemModel();
		CategoryType oldCategoryType = oldGradebookItemModel == null ? null : oldGradebookItemModel.getCategoryType();
		CategoryType newCategoryType = newGradebookItemModel == null ? null : newGradebookItemModel.getCategoryType();

		this.selectedGradebook = selectedGradebook;

		return (isInitialized && oldCategoryType != null && newCategoryType != null
				&& oldCategoryType == newCategoryType);
	}

}
