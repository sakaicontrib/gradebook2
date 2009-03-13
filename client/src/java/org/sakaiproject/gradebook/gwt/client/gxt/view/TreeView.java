package org.sakaiproject.gradebook.gwt.client.gxt.view;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.ItemFormPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.ItemTreePanel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModelComparer;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.data.TreeModelReader;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;

public class TreeView extends View {

	private ItemTreePanel treePanel;
	private ItemFormPanel formPanel;
	
	private TreeLoader<ItemModel> treeLoader;
	private TreeStore<ItemModel> treeStore;
	
	//private FormBinding formBindings;
	
	public TreeView(Controller controller, I18nConstants i18n) {
		super(controller);
		this.treePanel = new ItemTreePanel(i18n);
		this.formPanel = new ItemFormPanel(i18n);
	}

	@Override
	protected void handleEvent(AppEvent<?> event) {
		switch(event.type) {
		case GradebookEvents.ItemUpdated:
			onItemUpdated((ItemModel)event.data);
			break;
		case GradebookEvents.StartEditItem:
			onEditItem((ItemModel)event.data);
			break;
		case GradebookEvents.HideEastPanel:
			onEditItemComplete((Boolean)event.data);
			break;
		case GradebookEvents.LoadItemTreeModel:
			onLoadItemTreeModel((GradebookModel)event.data);
			break;
		case GradebookEvents.SelectItem:
			onSelectItem((String)event.data);
			break;
		case GradebookEvents.Startup:
			GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
			onSwitchGradebook(selectedGradebook);
			break;
		case GradebookEvents.SwitchGradebook:
			onSwitchGradebook((GradebookModel)event.data);
			break;
		case GradebookEvents.UserChange:
			onUserChange((UserEntityAction<?>)event.data);
			break;
		}
	}
	
	protected void onEditItem(ItemModel itemModel) {
		formPanel.onEditItem(itemModel);
	}
	
	protected void onEditItemComplete(Boolean doCommit) {
		if (doCommit.booleanValue())
			treeStore.commitChanges();
		else
			treeStore.rejectChanges();	
	}
	
	protected void onItemUpdated(ItemModel itemModel) {
		//treeStore.update(itemModel);
		formPanel.onItemUpdated(itemModel);
	}
	
	protected void onLoadItemTreeModel(GradebookModel selectedGradebook) {
		treeStore.removeAll();
		ItemModel gradebookItemModel = selectedGradebook.getGradebookItemModel();
		ItemModel rootItemModel = new ItemModel();
		rootItemModel.setItemType(Type.ROOT.getName());
		rootItemModel.setName("Root");
		gradebookItemModel.setParent(rootItemModel);
		rootItemModel.add(gradebookItemModel);
		treeLoader.load(rootItemModel);
		treePanel.onLoadItemTreeModel(rootItemModel);
		formPanel.onLoadItemTreeModel(rootItemModel);
		treePanel.expandTrees();
	}
	
	protected void onSelectItem(String itemModelId) {
		
		if (treeStore != null) {
			List<ItemModel> itemModels = treeStore.findModels(ItemModel.Key.ID.name(), itemModelId);
			if (itemModels != null) {
				for (ItemModel itemModel : itemModels) {
					String itemType = itemModel.getItemType();
					String source = itemModel.getSource();
					if (itemType.equals(Type.ITEM.getName()) && (source == null || !source.equals("Static"))) {
						onEditItem(itemModel);
						break;
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void onSwitchGradebook(GradebookModel selectedGradebook) {
		
		if (treeLoader == null) {
			treeLoader = new BaseTreeLoader(new TreeModelReader() {
	
				@Override
				protected List<? extends ModelData> getChildren(ModelData parent) {
					List visibleChildren = new ArrayList();
					List<? extends ModelData> children = super.getChildren(parent);
					
					for (ModelData model : children) {
						String source = model.get(ItemModel.Key.SOURCE.name());
						if (source == null || !source.equals("Static"))
							visibleChildren.add(model);
					}
					
					return visibleChildren;
				}
			});
		}
		
		if (treeStore == null) {
			treeStore = new TreeStore<ItemModel>(treeLoader);
			treeStore.setStoreSorter(new StoreSorter<ItemModel>() {

				@Override
				public int compare(Store store, ItemModel m1, ItemModel m2,
						String property) {
					boolean m1Category = m1.getItemType().equalsIgnoreCase("Category");
					boolean m2Category = m2.getItemType().equalsIgnoreCase("Category");

					if (m1Category && !m2Category) {
						return -1;
					} else if (!m1Category && m2Category) {
						return 1;
					}

					return super.compare(store, m1, m2, property);
				}
			});
			treeStore.setModelComparer(new ItemModelComparer());
			treePanel.onTreeStoreInitialized(treeStore);
			formPanel.onTreeStoreInitialized(treeStore);
			//formPanel.onTreeStoreInitialized(treeStore);
		}
		
		/*if (formBindings == null) {
			formBindings = new FormBinding(formPanel.getFormPanel(), true);
			formBindings.setStore(treeStore);
		}*/
		
		onLoadItemTreeModel(selectedGradebook);
		
		/*treeStore.removeAll();
		treeLoader.load(selectedGradebook.getRootItemModel());
		treePanel.onLoadItemTreeModel(selectedGradebook.getRootItemModel());
		formPanel.onLoadItemTreeModel(selectedGradebook.getRootItemModel());
		*/
		formPanel.onSwitchGradebook(selectedGradebook);
		treePanel.onSwitchGradebook(selectedGradebook);
	}
	
	protected void onUserChange(UserEntityAction<?> action) {
		treePanel.onUserChange(action);
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
