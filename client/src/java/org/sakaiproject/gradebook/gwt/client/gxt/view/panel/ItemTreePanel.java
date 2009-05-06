package org.sakaiproject.gradebook.gwt.client.gxt.view.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.GradebookState;
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
import org.sakaiproject.gradebook.gwt.client.gxt.settings.LogConfig;
import org.sakaiproject.gradebook.gwt.client.gxt.settings.LogDisplay;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.ItemCellRenderer;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.ItemNumberCellRenderer;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.ItemTreeTableBinder;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.ItemTreeTableHeader;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumnModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.XDOM;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.binder.TreeBinder;
import com.extjs.gxt.ui.client.binder.TreeTableBinder;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.data.TreeModelReader;
import com.extjs.gxt.ui.client.event.CheckChangedEvent;
import com.extjs.gxt.ui.client.event.CheckChangedListener;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TreeEvent;
import com.extjs.gxt.ui.client.event.TreeTableEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.table.TableColumn;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.extjs.gxt.ui.client.widget.tree.TreeSelectionModel;
import com.extjs.gxt.ui.client.widget.tree.Tree.CheckCascade;
import com.extjs.gxt.ui.client.widget.treetable.TreeTable;
import com.extjs.gxt.ui.client.widget.treetable.TreeTableColumn;
import com.extjs.gxt.ui.client.widget.treetable.TreeTableColumnModel;
import com.extjs.gxt.ui.client.widget.treetable.TreeTableHeader;
import com.extjs.gxt.ui.client.widget.treetable.TreeTableView;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.KeyboardListener;

public class ItemTreePanel extends ContentPanel {
	
	private enum SelectionType { CREATE_CATEGORY, CREATE_ITEM, UPDATE_ITEM, DELETE_ITEM };
	
	private static final String selectionTypeField = "selectionType";
	
	private static int CHARACTER_WIDTH = 7;
		
	// Listeners
	private CheckChangedListener checkChangedListener;
	private SelectionListener<MenuEvent> menuSelectionListener;
	private SelectionChangedListener<ItemModel> selectionChangedListener;
	private Listener<TreeEvent> treeEventListener;
	private Listener<TreeTableEvent> treeTableEventListener;

	// Components
	private Menu treeContextMenu;
	private MenuItem addCategoryMenuItem, updateCategoryMenuItem, updateItemMenuItem, deleteCategoryMenuItem, deleteItemMenuItem;
	private Tree learnerAttributeTree;
	private Tree itemTree;
	private TreeTable treeTable;
	private TreeTableView treeTableView;
	private TreeTableBinder<ItemModel> treeTableBinder;
	private TreeTableColumn percentCourseGradeColumn;
	private TreeTableColumn percentCategoryColumn;
	private TreeTableColumn pointsColumn;
	private TreeTableColumnModel treeTableColumnModel;
	private TreeBinder<BaseTreeModel<TreeModel>> treeBinder;
	private TreeStore<BaseTreeModel<TreeModel>> staticColumnStore;

	// We have to track which static columns are visible somewhere
	private Set<String> fullStaticIdSet;
	private Set<String> visibleStaticIdSet;
	
	private boolean isLearnerAttributeTreeLoaded;
	
	private List<ItemModel> selectedItemModels;
	private List<BaseTreeModel<TreeModel>> checkedSelection;
	
	private GradebookModel selectedGradebook;
	
	private boolean isEditable;
	private I18nConstants i18n;
	
	public ItemTreePanel(I18nConstants i18n, boolean isEditable) {
		this.enableLayout = false;
		this.i18n = i18n;
		this.isEditable = isEditable;
		this.fullStaticIdSet = new HashSet<String>();
		this.isLearnerAttributeTreeLoaded = false;
		this.visibleStaticIdSet = new HashSet<String>();
		setBorders(true);
		setHeading(i18n.navigationPanelHeader());
		setLayout(new FillLayout());
		initListeners();
		newEditableTree(i18n);
		newLearnerAttributeTree(i18n);
	
		TabPanel tabPanel = new AriaTabPanel();
		
		TabItem item = new AriaTabItem(i18n.navigationPanelFixedTabHeader()) {
			@Override
			protected void onResize(int width, int height) {
				super.onResize(width, height);
			
				treeTable.setHeight(height);
				
				/*
				LogConfig infoConfig = new LogConfig("Test", "Some info");
				infoConfig.display = 5000;
				infoConfig.width = 300;
				infoConfig.height = 200;
				infoConfig.isPermanent = true;
				
				Point point = getPosition(false);
				
				int y = point.y + getHeight(); // - infoConfig.height;
				
				LogDisplay.display(point.x, y, infoConfig);*/
			}
		};
		item.setLayout(new FlowLayout());
		//item.setAutoHeight(true);
		treeTable.setAutoHeight(true);
		//treeTable.setDeferHeight(true);
		//treeTable.setSize(450, 483);
		treeTable.setWidth(450);
		treeTable.setHorizontalScroll(true);
		item.add(treeTable);
		item.setScrollMode(Scroll.AUTO);
		tabPanel.add(item);
		
		item = new AriaTabItem(i18n.navigationPanelDynamicTabHeader());
		item.setLayout(new FlowLayout());
		learnerAttributeTree.setWidth(450);
		item.add(learnerAttributeTree);
		item.setScrollMode(Scroll.AUTO);
		tabPanel.add(item);
		
		add(tabPanel);
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
	    getHeader().setId("itemtreelabel");
	    Accessibility.setRole(el().dom, "region");
	    Accessibility.setRole(getHeader().el().dom, "heading");
	    treeTable.setHeight(getHeight(true));
	}

	public void expandTrees() {
		if (learnerAttributeTree != null)
			learnerAttributeTree.expandAll();
		if (itemTree != null)
			itemTree.expandAll();
		if (treeTable != null)
			treeTable.expandAll();
	}
	
	public void onBeforeLoadItemTreeModel(GradebookModel selectedGradebook, ItemModel rootItem) {
		
	}
	
	public void onShowStaticColumn(String id) {
		BaseTreeModel<TreeModel> model = staticColumnStore.findModel("id", id);
		TreeItem treeItem = (TreeItem)treeBinder.findItem(model);
		treeItem.setChecked(true);
	}
	
	public void onHideColumn(ItemModel itemModel) {
		TreeItem treeItem = (TreeItem)treeTableBinder.findItem(itemModel);
		
		treeItem.setChecked(false);
	}
	
	public void onItemCreated(ItemModel itemModel) {
		TreeItem treeItem = (TreeItem)treeTableBinder.findItem(itemModel);
		
		if (treeItem != null) {
			treeItem.getParentItem().setExpanded(true);
			//treeItem.setExpanded(true);
			treeItem.setChecked(true);
		}
	}
	
	public void onLoadItemTreeModel(GradebookModel selectedGradebook, TreeLoader<ItemModel> treeLoader, ItemModel rootItem) {
		//System.out.println("ItemTreePanel: Load Item Tree Model");
		this.selectedGradebook = selectedGradebook;
		if (addCategoryMenuItem != null)
			addCategoryMenuItem.setVisible(selectedGradebook.getGradebookItemModel().getCategoryType() != CategoryType.NO_CATEGORIES);
		
		switch (selectedGradebook.getGradebookItemModel().getCategoryType()) {
		case NO_CATEGORIES:
		case SIMPLE_CATEGORIES:
			percentCourseGradeColumn.setHidden(true);
			percentCategoryColumn.setHidden(true);
			pointsColumn.setHidden(false);
			break;
		case WEIGHTED_CATEGORIES:
			percentCourseGradeColumn.setHidden(false);
			percentCategoryColumn.setHidden(false);
			pointsColumn.setHidden(true);
			break;
		}
		
		if (selectedItemModels == null) {
			fullStaticIdSet.clear();
			visibleStaticIdSet.clear();
			selectedItemModels = new ArrayList<ItemModel>();
			List<String> selectedItemModelIds = GradebookState.getSelectedMultigradeColumns(selectedGradebook.getGradebookUid());
			// Deal with static visible columns
			for (FixedColumnModel column : selectedGradebook.getColumns()) {
				fullStaticIdSet.add(column.getIdentifier());
				if (selectedItemModelIds.contains(column.getIdentifier()))
					visibleStaticIdSet.add(column.getIdentifier());
			}
			
			// Deal with the dynamic columns now
			ItemModel gradebookItemModel = selectedGradebook.getGradebookItemModel();
			if (gradebookItemModel != null) {
				boolean isEntireGradebookChecked = true;
				for (ItemModel c1 : gradebookItemModel.getChildren()) {
					switch (c1.getItemType()) {
					case CATEGORY:
						boolean isEntireCategoryChecked = true;
						for (ItemModel c2 : c1.getChildren()) {
							if (selectedItemModelIds.contains(c2.getIdentifier()))
								selectedItemModels.add(c2);
							else {
								isEntireCategoryChecked = false;
								isEntireGradebookChecked = false;
							}
						}
						if (isEntireCategoryChecked)
							selectedItemModels.add(c1);
						break;
					case ITEM:
						if (selectedItemModelIds.contains(c1.getIdentifier()))
							selectedItemModels.add(c1);
						else {
							isEntireCategoryChecked = false;
							isEntireGradebookChecked = false;
						}
						break;
					}
				}
				if (isEntireGradebookChecked)
					selectedItemModels.add(gradebookItemModel);
			}
		}

		showColumns(selectedItemModels);
	
		treeLoader.load(rootItem);
		loadLearnerAttributeTree(selectedGradebook);
		
		if (rendered) {
			treeTableBinder.setCheckedSelection(selectedItemModels);
			treeBinder.setCheckedSelection(checkedSelection);
		}
	
	}
	
	public void onMaskItemTree() {
		treeTable.mask("Saving changes");
	}
	
	public void onSingleGrade() {
		treeTable.getSelectionModel().deselectAll();
	}
	
	public void onSwitchGradebook(GradebookModel selectedGradebook) {
		this.selectedGradebook = selectedGradebook;
		if (addCategoryMenuItem != null)
			addCategoryMenuItem.setVisible(selectedGradebook.getGradebookItemModel().getCategoryType() != CategoryType.NO_CATEGORIES);

		//loadLearnerAttributeTree(selectedGradebook);
		this.enableLayout = true;
	}
	
	public void onTreeStoreInitialized(TreeStore<ItemModel> treeStore) {
		
		treeTableBinder = new ItemTreeTableBinder(treeTable, treeStore);
		treeTableBinder.addSelectionChangedListener(selectionChangedListener);
		treeTableBinder.addCheckListener(checkChangedListener);

		
		/*TreeDragSource source = new TreeDragSource(treeTableBinder);
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

		TreeDropTarget target = new TreeDropTarget(treeTableBinder);
		target.setAllowSelfAsSource(true);
		target.setFeedback(Feedback.BOTH); */
		
	}

	public void onUnmaskItemTree() {
		treeTable.unmask();
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
					addCategoryMenuItem.setVisible(((GradebookModel)action.getModel()).getGradebookItemModel().getCategoryType() != CategoryType.NO_CATEGORIES);
					break;
				}
				
				break;
			}
			break;
		
		}
	}
	
	protected TreeTable newEditableTree(I18nConstants i18n) {
		List<TreeTableColumn> columns = new ArrayList<TreeTableColumn>();
		
		ItemCellRenderer cellRenderer = new ItemCellRenderer();
		
		TreeTableColumn nameColumn = new TreeTableColumn(ItemModel.Key.NAME.name(), 
				ItemModel.getPropertyName(ItemModel.Key.NAME), 180);
		nameColumn.setRenderer(cellRenderer);
		nameColumn.setSortable(false);
		columns.add(nameColumn);
		
		ItemNumberCellRenderer numericCellRenderer = new ItemNumberCellRenderer(DataTypeConversionUtil.getShortNumberFormat());		
		GradebookModel gbModel = Registry.get(AppConstants.CURRENT);
		
		percentCourseGradeColumn =  new TreeTableColumn(ItemModel.Key.PERCENT_COURSE_GRADE.name(), 
				ItemModel.getPropertyName(ItemModel.Key.PERCENT_COURSE_GRADE), ItemModel.getPropertyName(ItemModel.Key.PERCENT_COURSE_GRADE).length() * CHARACTER_WIDTH + 30);
		percentCourseGradeColumn.setAlignment(HorizontalAlignment.RIGHT);
		percentCourseGradeColumn.setHidden(gbModel.getGradebookItemModel().getCategoryType() == CategoryType.SIMPLE_CATEGORIES);
		percentCourseGradeColumn.setRenderer(numericCellRenderer);
		percentCourseGradeColumn.setSortable(false);
		columns.add(percentCourseGradeColumn);
		
		percentCategoryColumn =  new TreeTableColumn(ItemModel.Key.PERCENT_CATEGORY.name(), 
				ItemModel.getPropertyName(ItemModel.Key.PERCENT_CATEGORY), ItemModel.getPropertyName(ItemModel.Key.PERCENT_CATEGORY).length() * CHARACTER_WIDTH + 30);
		percentCategoryColumn.setAlignment(HorizontalAlignment.RIGHT);
		percentCategoryColumn.setHidden(gbModel.getGradebookItemModel().getCategoryType() == CategoryType.SIMPLE_CATEGORIES);
		percentCategoryColumn.setRenderer(numericCellRenderer);
		percentCategoryColumn.setSortable(false);
		columns.add(percentCategoryColumn);
		
		pointsColumn = new TreeTableColumn(ItemModel.Key.POINTS.name(), 
				ItemModel.getPropertyName(ItemModel.Key.POINTS), ItemModel.getPropertyName(ItemModel.Key.POINTS).length() * CHARACTER_WIDTH + 30);
		pointsColumn.setAlignment(HorizontalAlignment.RIGHT);
		pointsColumn.setHidden(gbModel.getGradebookItemModel().getCategoryType() != CategoryType.WEIGHTED_CATEGORIES);
		pointsColumn.setRenderer(numericCellRenderer);
		pointsColumn.setSortable(false);
		columns.add(pointsColumn);
		
		treeTableColumnModel = new TreeTableColumnModel(columns);
		treeTable = new TreeTable(treeTableColumnModel) {
			@Override
			protected void onRender(Element target, int index) {
				super.onRender(target, index);
				Accessibility.setRole(el().dom, "treegrid");
				Accessibility.setState(el().dom, "aria-labelledby", "itemtreelabel");
				treeTableBinder.setCheckedSelection(selectedItemModels);
				//treeTable.setHeight(483); //ItemTreePanel.this.getHeight(true));
				expandAll();
			}
		};
		
		treeTableView = new TreeTableView() {
			
			protected void init(final TreeTable treeTable) {
				super.init(treeTable);

				Listener l = new Listener<TreeTableEvent>() {

					public void handleEvent(TreeTableEvent be) {
						switch (be.type) {
						case Events.HiddenChange: {
							TableColumn c = cm.getColumn(be.columnIndex);
							if (c.isHidden())
								((ItemTreeTableHeader) treeTable
										.getTableHeader())
										.hideColumn(be.columnIndex);
							else
								((ItemTreeTableHeader) treeTable
										.getTableHeader())
										.showColumn(be.columnIndex);
							break;
						}
						}
					}
				};

				cm.addListener(Events.HiddenChange, l);
			}
			
			protected void render() {
			    scrollBarWidth = XDOM.getScrollBarWidth();

			    StringBuffer sb = new StringBuffer();
			    sb.append("<div style='overflow: hidden;'>");
			    sb.append("<div style='overflow: auto;'>");
			    sb.append("<div class='my-treetbl-data'>");
			    sb.append("<div class='my-treetbl-tree'></div>");
			    sb.append("</div></div></div>");
			    String bodyHTML = sb.toString();
			    
			    Element div = DOM.createDiv();
			    DOM.setInnerHTML(div, bodyHTML.toString());
			    scrollEl = new El(El.fly(div).getSubChild(2));
			    dataEl = new El(DOM.getFirstChild(scrollEl.dom));
			    treeDiv = dataEl.firstChild().dom;
			    DOM.appendChild(treeDiv, treeTable.getRootItem().getElement());
			    DOM.appendChild(treeTable.getElement(), DOM.getFirstChild(div));

			    if (!GXT.isIE) {
			      DOM.setElementPropertyInt(treeTable.getElement(), "tabIndex", 0);
			    }

			    treeTable.disableTextSelection(true);

			    DOM.sinkEvents(scrollEl.dom, Event.ONSCROLL);
			  }
			
		};
		
		TreeTableHeader treeTableHeader = new ItemTreeTableHeader(treeTable);
		treeTable.setTableHeader(treeTableHeader);
		treeTable.setDeferHeight(true);
		treeTable.setView(treeTableView);
		treeTable.setCheckable(true);
		treeTable.setCheckStyle(CheckCascade.CHILDREN);
		treeTable.setAnimate(true);
		treeTable.getStyle().setLeafIconStyle("gbEditItemIcon");
		
		treeTable.addListener(Events.KeyPress, treeEventListener);
		treeTable.addListener(Events.RowDoubleClick, treeTableEventListener);

		treeTable.setSelectionModel(new TreeSelectionModel(SelectionMode.SINGLE));
		if (isEditable)
			treeTable.setContextMenu(newTreeContextMenu(i18n)); 
		
		return treeTable;
	}
	
	protected Tree newLearnerAttributeTree(I18nConstants i18n) {
		
		learnerAttributeTree = new AriaTree() {
			@Override
			protected void onRender(Element target, int index) {
			    super.onRender(target, index);
			
			    if (checkedSelection != null)
			    	treeBinder.setCheckedSelection(checkedSelection);
			    
			    learnerAttributeTree.setHeight(ItemTreePanel.this.getHeight(true));
			    
			    expandAll();
			}
		};
		learnerAttributeTree.getStyle().setNodeOpenIconStyle("gbAttrIcon");
		learnerAttributeTree.getStyle().setNodeCloseIconStyle("gbAttrIcon");
		learnerAttributeTree.getStyle().setLeafIconStyle("gbAttrLeafIcon");
		learnerAttributeTree.setCheckable(true);
		learnerAttributeTree.setCheckStyle(CheckCascade.CHILDREN);
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
				
					Dispatcher.forwardEvent(GradebookEvents.ShowColumns.getEventType(), 
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
	
		itemTree = new AriaTree() {
			@Override
			protected void onRender(Element target, int index) {
			    super.onRender(target, index);
			
			    expandAll();
			}
		};
		itemTree.getStyle().setLeafIconStyle("gbItemIcon");
		itemTree.addListener(Events.SelectionChange, treeEventListener);
		itemTree.addListener(Events.RowDoubleClick, treeEventListener);
		itemTree.setSelectionModel(new TreeSelectionModel(SelectionMode.MULTI));

		itemTree.setContextMenu(newTreeContextMenu(i18n));  
		
		/*MenuItem expandMenuItem = new AriaMenuItem();
		expandMenuItem.setText("Expand");
		expandMenuItem.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				itemTree.expandAll();
			}


			
		});
		treeContextMenu.add(expandMenuItem);*/
		
		return itemTree;
	}
	
	
	
	private void loadLearnerAttributeTree(GradebookModel selectedGradebook) {
		if (isLearnerAttributeTreeLoaded)
			return;
		
		isLearnerAttributeTreeLoaded = true;
		
		TreeLoader loader = new BaseTreeLoader(new TreeModelReader());
		
		BaseTreeModel<TreeModel> root = new BaseTreeModel<TreeModel>();
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("name", "Learner Attributes");
		properties.put("id", "learnerAttributes");
		BaseTreeModel<TreeModel> learnerAttributes = new BaseTreeModel<TreeModel>(properties);
		learnerAttributes.setParent(root);
		root.add(learnerAttributes);
		
		properties = new HashMap<String, Object>();
		properties.put("name", "Grades");
		properties.put("id", "gradingColumns");
		BaseTreeModel<TreeModel> gradingColumns = new BaseTreeModel<TreeModel>(properties);
		gradingColumns.setParent(root);
		root.add(gradingColumns);
		
		checkedSelection = new ArrayList<BaseTreeModel<TreeModel>>();
		
		//fullStaticIdSet.clear();
		for (org.sakaiproject.gradebook.gwt.client.model.FixedColumnModel column : selectedGradebook.getColumns()) {
			properties = new HashMap<String, Object>();
			properties.put("name", column.getName());
			properties.put("id", column.getIdentifier());
			properties.put("hidden", column.isHidden());
			
			//if (column.isHidden() == null || !column.isHidden().booleanValue()) 
			//	visibleStaticIdSet.add(column.getIdentifier());
			//fullStaticIdSet.add(column.getIdentifier());
			BaseTreeModel<TreeModel> model = new BaseTreeModel<TreeModel>(properties);
			
			if (column.getIdentifier().equals(StudentModel.Key.GRADE_OVERRIDE.name()) ||
					column.getIdentifier().equals(StudentModel.Key.COURSE_GRADE.name())) {
				model.setParent(gradingColumns);
				gradingColumns.add(model);
			} else {
				model.setParent(learnerAttributes);
				learnerAttributes.add(model);
			}
			
			if (visibleStaticIdSet.contains(column.getIdentifier()))
				checkedSelection.add(model);
		}
		
		staticColumnStore = new TreeStore<BaseTreeModel<TreeModel>>(loader);
	
		treeBinder = 
			new TreeBinder<BaseTreeModel<TreeModel>>(learnerAttributeTree, staticColumnStore) {
			
			@Override
			protected TreeItem createItem(BaseTreeModel<TreeModel> model) {
				TreeItem item = new AriaTreeItem();
				
				item.addListener(Events.KeyPress, new KeyListener() {
					public void componentKeyPress(ComponentEvent event) {
						switch (event.getKeyCode()) {
						case KeyboardListener.KEY_ENTER:
							TreeItem item = treeTable.getSelectedItem();
							if (item != null) {
								item.setChecked(true);	
							}
							break;
						}
					}
				});
				
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
		
		treeBinder.addCheckListener(checkChangedListener);
		
		loader.load(root);
	}
	
	private Menu newTreeContextMenu(I18nConstants i18n) {

		treeContextMenu = new AriaMenu();
		treeContextMenu.setWidth(130);
		
		addCategoryMenuItem = new AriaMenuItem();
		addCategoryMenuItem.setData(selectionTypeField, SelectionType.CREATE_CATEGORY);
		addCategoryMenuItem.setIconStyle("gbAddCategoryIcon");
		addCategoryMenuItem.setItemId(AppConstants.ID_CT_ADD_CATEGORY_MENUITEM);
		addCategoryMenuItem.setText(i18n.headerAddCategory());
		addCategoryMenuItem.setTitle(i18n.headerAddCategoryTitle());
		addCategoryMenuItem.addSelectionListener(menuSelectionListener);
		treeContextMenu.add(addCategoryMenuItem);
		
		updateCategoryMenuItem = new AriaMenuItem();
		updateCategoryMenuItem.setData(selectionTypeField, SelectionType.UPDATE_ITEM);
		updateCategoryMenuItem.setIconStyle("gbEditCategoryIcon");
		updateCategoryMenuItem.setItemId(AppConstants.ID_CT_EDIT_CATEGORY_MENUITEM);
		updateCategoryMenuItem.setText(i18n.headerEditCategory());
		updateCategoryMenuItem.addSelectionListener(menuSelectionListener);
		treeContextMenu.add(updateCategoryMenuItem);
	
		deleteCategoryMenuItem = new AriaMenuItem();
		deleteCategoryMenuItem.setData(selectionTypeField, SelectionType.DELETE_ITEM);
		deleteCategoryMenuItem.setIconStyle("gbDeleteCategoryIcon");
		deleteCategoryMenuItem.setItemId(AppConstants.ID_CT_DELETE_ITEM_MENUITEM);
		deleteCategoryMenuItem.setText(i18n.headerDeleteCategory());
		deleteCategoryMenuItem.addSelectionListener(menuSelectionListener);
		treeContextMenu.add(deleteCategoryMenuItem);
		
		
		MenuItem menuItem = new AriaMenuItem();
		menuItem.setData(selectionTypeField, SelectionType.CREATE_ITEM);
		menuItem.setIconStyle("gbAddItemIcon");
		menuItem.setItemId(AppConstants.ID_CT_ADD_ITEM_MENUITEM);
		menuItem.setText(i18n.headerAddItem());
		menuItem.setTitle(i18n.headerAddItemTitle());
		menuItem.addSelectionListener(menuSelectionListener);
		treeContextMenu.add(menuItem);

		updateItemMenuItem = new AriaMenuItem();
		updateItemMenuItem.setData(selectionTypeField, SelectionType.UPDATE_ITEM);
		updateItemMenuItem.setIconStyle("gbEditItemIcon");
		updateItemMenuItem.setItemId(AppConstants.ID_CT_EDIT_ITEM_MENUITEM);
		updateItemMenuItem.setText(i18n.headerEditItem());
		updateItemMenuItem.addSelectionListener(menuSelectionListener);
		treeContextMenu.add(updateItemMenuItem);
	
		deleteItemMenuItem = new AriaMenuItem();
		deleteItemMenuItem.setData(selectionTypeField, SelectionType.DELETE_ITEM);
		deleteItemMenuItem.setIconStyle("gbDeleteItemIcon");
		deleteItemMenuItem.setItemId(AppConstants.ID_CT_DELETE_ITEM_MENUITEM);
		deleteItemMenuItem.setText(i18n.headerDeleteItem());
		deleteItemMenuItem.addSelectionListener(menuSelectionListener);
		treeContextMenu.add(deleteItemMenuItem);
		
		return treeContextMenu;
	}
		
	protected void showColumns(List<ItemModel> selectedItemModels) {
		Set<String> selectedItemModelIdSet = new HashSet<String>();
		boolean selectAll = false;
		
		if (selectedItemModels != null) {
			for (ItemModel selectedItemModel : selectedItemModels) {
				//selectedItemModelIdSet.add(selectedItemModel.getIdentifier());
				
				// If the root or gradebook is selected then we don't need to mess around any further
				switch (selectedItemModel.getItemType()) {
				case ITEM:
					selectedItemModelIdSet.add(selectedItemModel.getIdentifier());
					break;
				}
			}
		}
		
		// Ensure that either the ID or DISPLAY NAME or SORT NAME is visible
		/*if (!visibleStaticIdSet.contains(StudentModel.Key.DISPLAY_ID.name()) && !visibleStaticIdSet.contains(StudentModel.Key.DISPLAY_NAME.name())
				&& !visibleStaticIdSet.contains(StudentModel.Key.SORT_NAME.name()))
			visibleStaticIdSet.add(StudentModel.Key.SORT_NAME.name());
		*/
		
		Dispatcher.forwardEvent(GradebookEvents.ShowColumns.getEventType(), 
				new ShowColumnsEvent(selectAll, fullStaticIdSet, visibleStaticIdSet, selectedItemModelIdSet));
	
	}
	
	private void initListeners() {
		
		checkChangedListener = new CheckChangedListener() {
			
			public void checkChanged(CheckChangedEvent event) {
				if (event.getCheckProvider() instanceof TreeTableBinder)
					selectedItemModels = (List<ItemModel>)event.getCheckedSelection();
				
				if (!visibleStaticIdSet.contains(StudentModel.Key.DISPLAY_ID.name()) && !visibleStaticIdSet.contains(StudentModel.Key.DISPLAY_NAME.name())
						&& !visibleStaticIdSet.contains(StudentModel.Key.LAST_NAME_FIRST.name())) {
					onShowStaticColumn(StudentModel.Key.LAST_NAME_FIRST.name());
				} 
				
				showColumns(selectedItemModels);
				
				if (selectedGradebook != null)
					GradebookState.setSelectedMultigradeColumns(selectedGradebook.getGradebookUid(), visibleStaticIdSet, selectedItemModels);				
			}
			
		};
		
		menuSelectionListener = new SelectionListener<MenuEvent>() {
			
			public void componentSelected(MenuEvent me) {
				SelectionType selectionType = me.item.getData(selectionTypeField);
				TreeItem item = (TreeItem) treeTable.getSelectionModel().getSelectedItem();
				switch (selectionType) {
				case CREATE_CATEGORY:
					Dispatcher.forwardEvent(GradebookEvents.NewCategory.getEventType(), item.getModel());
					break;
				case CREATE_ITEM:
					Dispatcher.forwardEvent(GradebookEvents.NewItem.getEventType(), item.getModel());
					break;
				case UPDATE_ITEM:
					Dispatcher.forwardEvent(GradebookEvents.StartEditItem.getEventType(), item.getModel());
					break;
				case DELETE_ITEM:
					Dispatcher.forwardEvent(GradebookEvents.ConfirmDeleteItem.getEventType(), item.getModel());
					break;
				}
			}
			
		};

		selectionChangedListener = new SelectionChangedListener<ItemModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ItemModel> se) {
				ItemModel itemModel = se.getSelectedItem();
				
				if (itemModel != null && isEditable) {
					boolean isNotGradebook = itemModel.getItemType() != Type.GRADEBOOK;
					
					switch (itemModel.getItemType()) {
					case CATEGORY:
						updateCategoryMenuItem.setVisible(true);
						updateItemMenuItem.setVisible(false);
						deleteCategoryMenuItem.setVisible(true);
						deleteItemMenuItem.setVisible(false);	
						break;
					case GRADEBOOK:
						updateCategoryMenuItem.setVisible(false);
						updateItemMenuItem.setVisible(false);
						deleteCategoryMenuItem.setVisible(false);
						deleteItemMenuItem.setVisible(false);		
						break;
					case ITEM:
						updateCategoryMenuItem.setVisible(false);
						updateItemMenuItem.setVisible(true);
						deleteCategoryMenuItem.setVisible(false);
						deleteItemMenuItem.setVisible(true);
						break;
					}
					
					
					
					Dispatcher.forwardEvent(GradebookEvents.SwitchEditItem.getEventType(), itemModel);
					//Dispatcher.forwardEvent(GradebookEvents.StartEditItem, itemModel);
				}
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
						Dispatcher.forwardEvent(GradebookEvents.StartEditItem.getEventType(), itemModel);
					} else {
						Dispatcher.forwardEvent(GradebookEvents.StartEditItem.getEventType(), null);
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
		
		treeTableEventListener = new Listener<TreeTableEvent>() {
			
			public void handleEvent(TreeTableEvent tte) {
				switch (tte.type) {
				case Events.RowDoubleClick:
					ItemModel itemModel = (ItemModel)tte.item.getModel();
					Dispatcher.forwardEvent(GradebookEvents.StartEditItem.getEventType(), itemModel);
					
					tte.stopEvent();
					break;
				}
			}
			
		};

	}

	public TreeTable getTreeTable() {
		return treeTable;
	}
	
}
