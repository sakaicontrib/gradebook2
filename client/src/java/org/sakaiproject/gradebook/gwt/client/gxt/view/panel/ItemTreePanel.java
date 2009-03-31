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
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.ItemTreeTableHeader;
import org.sakaiproject.gradebook.gwt.client.model.ColumnModel;
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
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TreeEvent;
import com.extjs.gxt.ui.client.event.TreeTableEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.table.CellRenderer;
import com.extjs.gxt.ui.client.widget.table.NumberCellRenderer;
import com.extjs.gxt.ui.client.widget.table.TableColumn;
import com.extjs.gxt.ui.client.widget.tree.Tree;
import com.extjs.gxt.ui.client.widget.tree.TreeItem;
import com.extjs.gxt.ui.client.widget.tree.TreeItemUI;
import com.extjs.gxt.ui.client.widget.tree.TreeSelectionModel;
import com.extjs.gxt.ui.client.widget.tree.Tree.CheckCascade;
import com.extjs.gxt.ui.client.widget.treetable.TreeTable;
import com.extjs.gxt.ui.client.widget.treetable.TreeTableColumn;
import com.extjs.gxt.ui.client.widget.treetable.TreeTableColumnModel;
import com.extjs.gxt.ui.client.widget.treetable.TreeTableHeader;
import com.extjs.gxt.ui.client.widget.treetable.TreeTableItem;
import com.extjs.gxt.ui.client.widget.treetable.TreeTableItemUI;
import com.extjs.gxt.ui.client.widget.treetable.TreeTableView;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Accessibility;

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
	private MenuItem addCategoryMenuItem, updateItemMenuItem, deleteItemMenuItem;
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
		//setLayout(new FitLayout());
		setLayout(new FillLayout());
		initListeners();
		newEditableTree(i18n);
		newLearnerAttributeTree(i18n);
	
		TabPanel tabPanel = new AriaTabPanel();
		
		TabItem item = new AriaTabItem(i18n.navigationPanelFixedTabHeader());
		item.setLayout(new FlowLayout());
		item.setAutoHeight(true);
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
	
	/*@Override
	protected boolean doLayout() {
		return super.doLayout();
	}*/
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
	    getHeader().setId("itemtreelabel");
	    Accessibility.setRole(el().dom, "region");
	    Accessibility.setRole(getHeader().el().dom, "heading");
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
			for (ColumnModel column : selectedGradebook.getColumns()) {
				fullStaticIdSet.add(column.getIdentifier());
				if (selectedItemModelIds.contains(column.getIdentifier()))
					visibleStaticIdSet.add(column.getIdentifier());
			}
			// Ensure that either the ID or DISPLAY NAME or SORT NAME is visible
			if (!visibleStaticIdSet.contains(StudentModel.Key.DISPLAY_ID.name()) && !visibleStaticIdSet.contains(StudentModel.Key.DISPLAY_NAME.name())
					&& !visibleStaticIdSet.contains(StudentModel.Key.SORT_NAME.name()))
				visibleStaticIdSet.add(StudentModel.Key.DISPLAY_NAME.name());
			
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
		
		treeTableBinder = new TreeTableBinder<ItemModel>(treeTable, treeStore) {
			
			@Override
			protected TreeItem createItem(ItemModel model) {
				int cols = treeTable.getColumnCount();
			    TreeTableItem item = new TreeTableItem(new Object[cols]) {
			    	@Override
			    	protected TreeItemUI getTreeItemUI() {
			    	    return new TreeTableItemUI(this) {
			    	    	protected void handleClickEvent(TreeEvent te) {
							    TreeItem item = te.item;
							    if (te.type == Event.ONCLICK) {
							      Element target = te.getTarget();
							      if (target != null && te.within(item.getUI().getJointEl())) {
							        item.toggle();
							      }
							      te.cancelBubble();
							    } 
							}
							
							@Override
							public void onClick(TreeEvent te) {
								te.cancelBubble();
							}
							@Override
							public void onDoubleClick(ComponentEvent ce) {
								ce.cancelBubble();
							}
			    	    };
			    	}
			    };
			    setModel(item, model);
			    for (int j = 0; j < cols; j++) {
			      String id = getColumnId(j);
			      Object val = getTextValue(model, id);
			      if (val == null) val = model.get(id);
			      item.setValue(j, val);
			    }
			    for (int i = 0; i < cols; i++) {
			        String id = getColumnId(i);
			        String style = (styleProvider == null) ? null : styleProvider.getStringValue(model, id);
			        item.setCellStyle(i, style == null ? "" : style);
			    }
			    //update(model);
			    //updateItemValues(item);
			    //updateItemStyles(item);

			    String txt = getTextValue(model, displayProperty);
			    if (txt == null && displayProperty != null) {
			      txt = model.get(displayProperty);
			    } else {
			      txt = model.toString();
			    }

			    String icon = getIconValue(model, displayProperty);

			    item.setIconStyle(icon);
			    item.setText(txt);
				
				//TreeItem item = super.createItem(model);

			    if (loader != null) {
			      item.setLeaf(!loader.hasChildren(model));
			    } else {
			      item.setLeaf(!hasChildren(model));
			    }

				boolean isGradebook = model.getItemType() == Type.GRADEBOOK;
				if (isGradebook)
					item.setIconStyle("gbGradebookIcon");
				
				
				
				return item;
			}
			
		};
		treeTableBinder.setDisplayProperty(ItemModel.Key.NAME.name());
		treeTableBinder.addSelectionChangedListener(selectionChangedListener);
		treeTableBinder.addCheckListener(checkChangedListener);
		treeTableBinder.setAutoLoad(true);
		
		/*TreeDragSource source = new TreeDragSource(treeBinder);
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
		
		CellRenderer<TreeItem> cellRenderer = new CellRenderer<TreeItem>() {

			public String render(TreeItem item, String property, Object value) {
				String prefix = "";
				String result = null;
				ItemModel itemModel = (ItemModel)item.getModel();
				
				boolean isItem = itemModel.getItemType() == Type.ITEM;
				boolean isName = property.equals(ItemModel.Key.NAME.name());
				boolean isIncluded = itemModel.getIncluded() == null || itemModel.getIncluded().booleanValue();		
				boolean isExtraCredit = itemModel.getExtraCredit() != null && itemModel.getExtraCredit().booleanValue();
				boolean isReleased = itemModel.getReleased() != null && itemModel.getReleased().booleanValue();
				
				if (value == null)
					return null;
				
				result = (String)value;
				
				StringBuilder cssClasses = new StringBuilder();
				
				if (isName) {
					if (!isIncluded) {
						cssClasses.append("gbNotIncluded");
						if (isItem)
							item.setIconStyle("gbItemIcon");
					} else if (isItem)
						item.setIconStyle("gbEditItemIcon");
				}
				
				if (isExtraCredit) 
					cssClasses.append(" gbCellExtraCredit");
				
				if (isReleased)
					cssClasses.append(" gbReleased");
				
				return new StringBuilder().append("<span class=\"").append(cssClasses)
					.append("\">").append(prefix).append(result).append("</span>").toString();
			}
			
		};
		
		TreeTableColumn nameColumn = new TreeTableColumn(ItemModel.Key.NAME.name(), 
				ItemModel.getPropertyName(ItemModel.Key.NAME), 180);
		nameColumn.setRenderer(cellRenderer);
		nameColumn.setSortable(false);
		columns.add(nameColumn);
		
		NumberCellRenderer<TreeItem> numericCellRenderer = new NumberCellRenderer<TreeItem>(DataTypeConversionUtil.getShortNumberFormat()) {
			
			@Override
			public String render(final TreeItem item, String property, final Object value) {
				String prefix = "";
				String result = null;
				final ItemModel itemModel = (ItemModel)item.getModel();
				
				if (itemModel != null && itemModel.getItemType() != null) {
					boolean isItem = itemModel.getItemType() == Type.ITEM;
					boolean isCategory = itemModel.getItemType() == Type.CATEGORY;
					boolean isGradebook = !isItem && !isCategory;
					boolean isPercentCategory = property.equals(ItemModel.Key.PERCENT_CATEGORY.name());
					boolean isPercentGrade = property.equals(ItemModel.Key.PERCENT_COURSE_GRADE.name());
					
					if (isGradebook && isPercentCategory)
						return "-";
						
					if (value == null)
						return null;
					
					boolean isName = property.equals(ItemModel.Key.NAME.name());
					
					boolean isIncluded = itemModel.getIncluded() != null && itemModel.getIncluded().booleanValue();				
					boolean isTooBig = (isPercentCategory || isPercentGrade) 
						&& ((Double)value).doubleValue() > 100.00001d;
					boolean isTooSmall = ((isPercentCategory && isCategory) || (isPercentGrade && isGradebook)) && ((Double)value).doubleValue() < 99.9994d;
					
					result = super.render(item, property, value);
					
					StringBuilder cssClasses = new StringBuilder();
					
					if (!isIncluded && isName)
						cssClasses.append("gbNotIncluded");
					
					if (!isItem) 
						cssClasses.append(" gbCellStrong");
					
					if (isTooBig || isTooSmall)
						cssClasses.append(" gbCellError");
						
					boolean isExtraCredit = itemModel.getExtraCredit() != null && itemModel.getExtraCredit().booleanValue();
					if (isExtraCredit) {
						
						if (isPercentGrade || (isPercentCategory && isItem)) {
							cssClasses.append(" gbCellExtraCredit");
							prefix = "+";
						}

					}
					
					StringBuilder builder = new StringBuilder().append("<span class=\"").append(cssClasses)
						.append("\">").append(prefix).append(result).append("</span>");
					
					if ((isCategory && isPercentCategory) || (isGradebook && isPercentGrade)) {
						builder.append(" / 100");
					} 
 					
					
					return builder.toString();
				
				}
				return "";
			}
		};
		
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
		//treeTable.expandAll();
		//treeTable.setHeight(300);
		//treeTable.setWidth(500);
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
		for (org.sakaiproject.gradebook.gwt.client.model.ColumnModel column : selectedGradebook.getColumns()) {
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
		
		TreeStore<BaseTreeModel<TreeModel>> treeStore = new TreeStore<BaseTreeModel<TreeModel>>(loader);
	
		treeBinder = 
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
		
		Dispatcher.forwardEvent(GradebookEvents.ShowColumns, 
				new ShowColumnsEvent(selectAll, fullStaticIdSet, visibleStaticIdSet, selectedItemModelIdSet));
	
	}
	
	private void initListeners() {
		
		checkChangedListener = new CheckChangedListener() {
			
			public void checkChanged(CheckChangedEvent event) {
				if (event.getCheckProvider() instanceof TreeTableBinder)
					selectedItemModels = (List<ItemModel>)event.getCheckedSelection();
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
					Dispatcher.forwardEvent(GradebookEvents.NewCategory, item.getModel());
					break;
				case CREATE_ITEM:
					Dispatcher.forwardEvent(GradebookEvents.NewItem, item.getModel());
					break;
				case UPDATE_ITEM:
					Dispatcher.forwardEvent(GradebookEvents.StartEditItem, item.getModel());
					break;
				case DELETE_ITEM:
					Dispatcher.forwardEvent(GradebookEvents.ConfirmDeleteItem, item.getModel());
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
					
					//updateItemMenuItem.setVisible(isNotGradebook);
					deleteItemMenuItem.setVisible(isNotGradebook);
					
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
		
		treeTableEventListener = new Listener<TreeTableEvent>() {
			
			public void handleEvent(TreeTableEvent tte) {
				switch (tte.type) {
				case Events.RowDoubleClick:
					ItemModel itemModel = (ItemModel)tte.item.getModel();
					Dispatcher.forwardEvent(GradebookEvents.StartEditItem, itemModel);
					
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
