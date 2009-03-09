package org.sakaiproject.gradebook.gwt.client.gxt.view.panel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaMenu;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaMenuItem;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaTabItem;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaTabPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaTree;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaTreeItem;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ShowColumnsEvent;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.binder.TreeBinder;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.data.TreeModelReader;
import com.extjs.gxt.ui.client.dnd.TreeDragSource;
import com.extjs.gxt.ui.client.dnd.TreeDropTarget;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TreeEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.extjs.gxt.ui.client.widget.tree.TreeSelectionModel;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Accessibility;

public class ItemTreePanel extends ContentPanel {
	
	private Menu treeContextMenu;
	private MenuItem addCategoryMenuItem;
	
	private SelectionListener<MenuEvent> menuSelectionListener;
	private SelectionChangedListener<ItemModel> selectionChangedListener;
	private Listener<TreeEvent> treeEventListener;

	private Tree learnerAttributeTree;
	private Tree itemTree;

	// We have to track which static columns are visible somewhere
	private Set<String> fullStaticIdSet;
	private Set<String> visibleStaticIdSet;
		
	public ItemTreePanel(I18nConstants i18n) {
		this.fullStaticIdSet = new HashSet<String>();
		this.visibleStaticIdSet = new HashSet<String>();
		setBorders(true);
		setHeading(i18n.navigationPanelHeader());
		setLayout(new FitLayout());
		initListeners();

		TabPanel tabPanel = new AriaTabPanel();
		
		TabItem item = new AriaTabItem(i18n.navigationPanelFixedTabHeader());
		item.setLayout(new FitLayout());
		item.add(newNavigationTree(i18n));
		tabPanel.add(item);
		
		item = new AriaTabItem(i18n.navigationPanelDynamicTabHeader());
		item.setLayout(new FitLayout());
		item.add(newLearnerAttributeTree(i18n));
		tabPanel.add(item);
		
		add(tabPanel);
	}
	
	public void onLoadItemTreeModel(ItemModel rootItem) {
		itemTree.expandAll();
	}
	
	public void onSwitchGradebook(GradebookModel selectedGradebook) {
		addCategoryMenuItem.setVisible(selectedGradebook.getCategoryType() != CategoryType.NO_CATEGORIES);
		
		TreeLoader loader = new BaseTreeLoader(new TreeModelReader());
	
		BaseTreeModel<TreeModel> root = new BaseTreeModel<TreeModel>();
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("name", "Learner Attributes");
		properties.put("id", "root");
		BaseTreeModel<TreeModel> parent = new BaseTreeModel<TreeModel>(properties);
		parent.setParent(root);
		root.add(parent);
		fullStaticIdSet.clear();
		for (org.sakaiproject.gradebook.gwt.client.model.ColumnModel column : selectedGradebook.getColumns()) {
			properties = new HashMap<String, Object>();
			properties.put("name", column.getName());
			properties.put("id", column.getIdentifier());
			properties.put("hidden", column.isHidden());
			if (column.isHidden() == null || !column.isHidden().booleanValue()) 
				visibleStaticIdSet.add(column.getIdentifier());
			fullStaticIdSet.add(column.getIdentifier());
			BaseTreeModel<TreeModel> model = new BaseTreeModel<TreeModel>(properties);
			model.setParent(parent);
			parent.add(model);
		}
		
		TreeStore<BaseTreeModel<TreeModel>> treeStore = new TreeStore<BaseTreeModel<TreeModel>>(loader);
	
		TreeBinder<BaseTreeModel<TreeModel>> treeBinder = 
			new TreeBinder<BaseTreeModel<TreeModel>>(learnerAttributeTree, treeStore) {
			
			@Override
			protected TreeItem createItem(BaseTreeModel<TreeModel> model) {
				TreeItem item = new AriaTreeItem();
				Boolean hidden = model.get("hidden");
				boolean isHidden = hidden != null && hidden.booleanValue();
				item.setChecked(!isHidden);
				
			    update(item, model);

			    if (loader != null) {
			      item.setLeaf(!loader.hasChildren(model));
			    } else {
			      item.setLeaf(!hasChildren(model));
			    }

			    setModel(item, model);
			    return item;
			}
		};
		
		treeBinder.setDisplayProperty("name");
		treeBinder.setAutoLoad(true);


		loader.load(root);
	}
	
	public void onTreeStoreInitialized(TreeStore<ItemModel> treeStore) {

		TreeBinder<ItemModel> treeBinder = new TreeBinder<ItemModel>(itemTree, treeStore) {
			
			@Override
			protected TreeItem createItem(ItemModel model) {
				TreeItem item = new AriaTreeItem();

				item.setId(new StringBuilder().append(model.getItemType()).append(":").append(model.getName()).toString());
				//item.addListener(Events.BeforeCollapse, treeEventListener);
				//item.addListener(Events.BeforeExpand, treeEventListener);
				
			    update(item, model);

			    if (loader != null) {
			      item.setLeaf(!loader.hasChildren(model));
			    } else {
			      item.setLeaf(!hasChildren(model));
			    }

			    setModel(item, model);
			    return item;
			}
			
		};
		treeBinder.setDisplayProperty(ItemModel.Key.NAME.name());
		treeBinder.setAutoLoad(true);
		treeBinder.addSelectionChangedListener(selectionChangedListener);

		TreeDragSource source = new TreeDragSource(treeBinder);
		source.addDNDListener(new DNDListener() {
			@Override
			public void dragStart(DNDEvent e) {
				TreeItem item = itemTree.findItem(e.getTarget());
				if (item != null && item == itemTree.getRootItem().getItem(0)
						&& itemTree.getRootItem().getItemCount() == 1) {
					e.doit = false;
					e.status.setStatus(false);
					return;
				}
				super.dragStart(e);
			}
		});

		TreeDropTarget target = new TreeDropTarget(treeBinder);
		target.setAllowSelfAsSource(true);
		target.setFeedback(Feedback.BOTH); 
	}

	
	public void onUserChange(UserEntityAction<?> action) {
		switch (action.getEntityType()) {
		case GRADEBOOK:
			switch (action.getActionType()) {
			case UPDATE:
				// We want to do this immediately, since these actions are now being
				// fired from the top level menu and multigrade may well be visible.
				GradebookModel.Key gradebookModelKey = GradebookModel.Key.valueOf(((UserEntityUpdateAction)action).getKey());
				switch (gradebookModelKey) {
				case CATEGORYTYPE: 
					addCategoryMenuItem.setVisible(((GradebookModel)action.getModel()).getCategoryType() != CategoryType.NO_CATEGORIES);
					break;
				}
				
				break;
			}
			break;
		
		}
	}
	
	protected Tree newLearnerAttributeTree(I18nConstants i18n) {
		
		learnerAttributeTree = new AriaTree();
		learnerAttributeTree.setCheckable(true);
		learnerAttributeTree.setSelectionModel(new TreeSelectionModel(SelectionMode.SINGLE));
		learnerAttributeTree.addListener(Events.CheckChange, new Listener<TreeEvent>() {

			public void handleEvent(TreeEvent te) {
				TreeItem item = te.item;
				ModelData model = te.item.getModel();
				String id = model.get("id");
				
				if (id != null) {
					if (item.isChecked())
						visibleStaticIdSet.add(id);
					else
						visibleStaticIdSet.remove(id);
				
					Dispatcher.forwardEvent(GradebookEvents.ShowColumns, 
							new ShowColumnsEvent(false, fullStaticIdSet, visibleStaticIdSet, null));
				}
			}
			
		});
		
		//itemTree.addListener(Events.SelectionChange, treeEventListener);
		//itemTree.addListener(Events.RowDoubleClick, treeEventListener);
		//itemTree.setSelectionModel(new TreeSelectionModel(SelectionMode.MULTI));
		
		return learnerAttributeTree;
	}

	protected Tree newNavigationTree(I18nConstants i18n) {
	
		itemTree = new AriaTree();
		itemTree.addListener(Events.SelectionChange, treeEventListener);
		itemTree.addListener(Events.RowDoubleClick, treeEventListener);
		itemTree.setSelectionModel(new TreeSelectionModel(SelectionMode.MULTI));
		
		treeContextMenu = new AriaMenu();
		treeContextMenu.setWidth(130);

		addCategoryMenuItem = new AriaMenuItem();
		addCategoryMenuItem.setItemId(AppConstants.ID_CT_ADD_CATEGORY_MENUITEM);
		addCategoryMenuItem.setText(i18n.addCategoryHeading());
		addCategoryMenuItem.addSelectionListener(menuSelectionListener);
		treeContextMenu.add(addCategoryMenuItem);
		
		//addCategoryMenuItem.setVisible(selectedGradebook.getCategoryType() != CategoryType.NO_CATEGORIES);
		
		MenuItem addItemMenuItem = new AriaMenuItem();
		addItemMenuItem.setItemId(AppConstants.ID_CT_ADD_ITEM_MENUITEM);
		addItemMenuItem.setText(i18n.addItemHeading());
		addItemMenuItem.addSelectionListener(menuSelectionListener);
		treeContextMenu.add(addItemMenuItem);

		
		MenuItem editItemMenuItem = new AriaMenuItem();
		editItemMenuItem.setItemId(AppConstants.ID_CT_EDIT_ITEM_MENUITEM);
		editItemMenuItem.setText(i18n.editItemHeading());
		editItemMenuItem.addSelectionListener(menuSelectionListener);
		treeContextMenu.add(editItemMenuItem);
		
		itemTree.setContextMenu(treeContextMenu);  
		
		return itemTree;
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
	    super.onRender(parent, pos);
	    getHeader().setId("itemtreelabel");
	    Accessibility.setRole(el().dom, "region");
	    Accessibility.setRole(getHeader().el().dom, "heading");
	    
	    learnerAttributeTree.expandAll();
	    itemTree.expandAll();
	}
	
	protected void showColumns(List<ItemModel> selectedItemModels) {
		Set<String> selectedItemModelIdSet = new HashSet<String>();
		boolean selectAll = false;
			
		for (ItemModel selectedItemModel : selectedItemModels) {
			// If the root or gradebook is selected then we don't need to mess around any further
			if (selectedItemModel.getItemType().equals(Type.ROOT.getName()) || selectedItemModel.getItemType().equals(Type.GRADEBOOK.getName())) {
				selectAll = true;
				break;
			} else if (selectedItemModel.getItemType().equals(Type.CATEGORY.getName())) {
				for (ItemModel childItemModel : selectedItemModel.getChildren()) 
					selectedItemModelIdSet.add(childItemModel.getIdentifier());
			} else
				selectedItemModelIdSet.add(selectedItemModel.getIdentifier());
		}
		
		Dispatcher.forwardEvent(GradebookEvents.ShowColumns, 
				new ShowColumnsEvent(selectAll, fullStaticIdSet, visibleStaticIdSet, selectedItemModelIdSet));
	}
	
	private void initListeners() {
		menuSelectionListener = new SelectionListener<MenuEvent>() {
			
			public void componentSelected(MenuEvent ce) {
				String itemId = ce.item.getItemId();
				TreeItem item = (TreeItem) itemTree.getSelectionModel().getSelectedItem();
				if (item != null) {
					if (itemId.equals(AppConstants.ID_CT_ADD_CATEGORY_MENUITEM)) 
						Dispatcher.forwardEvent(GradebookEvents.NewCategory, item.getModel());
					else if (itemId.equals(AppConstants.ID_CT_ADD_ITEM_MENUITEM)) 
						Dispatcher.forwardEvent(GradebookEvents.NewItem, item.getModel());
					else if (itemId.equals(AppConstants.ID_CT_EDIT_ITEM_MENUITEM)) 
						Dispatcher.forwardEvent(GradebookEvents.StartEditItem, item.getModel());
				}
			}
			
		};
		selectionChangedListener = new SelectionChangedListener<ItemModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ItemModel> se) {
				showColumns(se.getSelection());
			}
			
		};
		
		treeEventListener = new Listener<TreeEvent>() {

			public void handleEvent(TreeEvent te) {
				switch (te.type) {
				case Events.BeforeExpand:
					Accessibility.setState(te.item.getElement(), "aria-expanded", "true");
					break;
				case Events.BeforeCollapse:
					Accessibility.setState(te.item.getElement(), "aria-expanded", "false");
					break;
				case Events.RowDoubleClick:
					if (te.index > 0) {
						ItemModel itemModel = (ItemModel)te.item.getModel();
						Dispatcher.forwardEvent(GradebookEvents.StartEditItem, itemModel);
					} else {
						Dispatcher.forwardEvent(GradebookEvents.StartEditItem, null);
					}
					te.stopEvent();
					break;
				case Events.SelectionChange:
					if (te.selected != null) {
						StringBuilder listOfIds = new StringBuilder();
						for (TreeItem item : te.selected) {
							listOfIds.append(item.getId()).append(" ");
						}
						Accessibility.setState(te.tree.el().dom, "aria-activedescendant", listOfIds.toString());
					}
					break;
				}
			}

		};

	}
	
}
