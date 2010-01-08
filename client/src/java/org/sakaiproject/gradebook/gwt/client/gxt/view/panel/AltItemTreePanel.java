package org.sakaiproject.gradebook.gwt.client.gxt.view.panel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.Gradebook2RPCServiceAsync;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.SecureToken;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaMenu;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaMenuItem;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaTabItem;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaTabPanel;
import org.sakaiproject.gradebook.gwt.client.gxt.custom.ItemTreeSelectionModel;
import org.sakaiproject.gradebook.gwt.client.gxt.custom.widget.grid.ItemTreeGridView;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ItemUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ShowColumnsEvent;
import org.sakaiproject.gradebook.gwt.client.model.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.ConfigurationModel;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumnKey;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumnModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.LearnerKey;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.data.TreeModelReader;
import com.extjs.gxt.ui.client.dnd.TreeGridDragSource;
import com.extjs.gxt.ui.client.dnd.TreeGridDropTarget;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.DND.TreeSource;
import com.extjs.gxt.ui.client.event.CheckChangedEvent;
import com.extjs.gxt.ui.client.event.CheckChangedListener;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridView;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.CheckCascade;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.CheckNodes;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.Joint;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class AltItemTreePanel extends GradebookPanel {

	private static int CHARACTER_WIDTH = 7;
	private enum SelectionType { CREATE_CATEGORY, CREATE_ITEM, UPDATE_ITEM, DELETE_ITEM, MOVE_DOWN, MOVE_UP };
	private static final String selectionTypeField = "selectionType";
	
	private Menu treeContextMenu;
	private MenuItem addCategoryMenuItem, updateCategoryMenuItem, updateItemMenuItem, deleteCategoryMenuItem, deleteItemMenuItem;
	private MenuItem moveDownMenuItem, moveUpMenuItem;
	
	private TreeLoader<FixedColumnModel> learnerAttributeLoader;
	private TreeGrid<ItemModel> itemGrid;
	private TreePanel<FixedColumnModel> learnerAttributeTree;
	
	private final boolean isEditable;
	
	// Listeners
	private CheckChangedListener<FixedColumnModel> checkListener;
	private LoadListener itemLoadListener, attributeLoadListener;
	private Listener<GridEvent<ItemModel>> gridEventListener;
	private SelectionListener<MenuEvent> menuSelectionListener;
	private SelectionChangedListener<ItemModel> selectionChangedListener;
		
	private ColumnConfig percentCourseGradeColumn, percentCategoryColumn, pointsColumn;
	private ColumnModel cm;
	private ItemTreeSelectionModel sm;
	private TreeStore<FixedColumnModel> learnerAttributeStore;
	private TreeStore<ItemModel> treeStore;
	
	private FixedColumnModel learnerAttributeRoot, learnerAttributes, gradingColumns; 
	
	private HashSet<String> fullStaticIdSet;
	private List<FixedColumnModel> checkedSelection;
	
	private boolean isAllowedToDropToGradebook = false;
	private boolean isLearnerAttributeTreeLoaded = false;
	
	public AltItemTreePanel(TreeStore<ItemModel> treeStore, boolean isEditable) {
		super();
		this.treeStore = treeStore;
		this.enableLayout = false;
		this.isEditable = isEditable;
		setBorders(true);
		setHeading(i18n.navigationPanelHeader());
		setLayout(new FillLayout());
		initListeners();

		sm = new ItemTreeSelectionModel();
		sm.addSelectionChangedListener(selectionChangedListener);
		sm.setSelectionMode(SelectionMode.MULTI);
		
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

		columns.add(sm.getColumn());
		
		TreeGridCellRenderer<ItemModel> renderer = new TreeGridCellRenderer<ItemModel>();
		
		ColumnConfig nameColumn = new ColumnConfig(ItemKey.NAME.name(), 
				i18n.nameFieldLabel(), 200);
		nameColumn.setMenuDisabled(true);
		nameColumn.setRenderer(renderer);
		nameColumn.setSortable(false);
		columns.add(nameColumn);
		
		GridCellRenderer<ItemModel> numericRenderer = new GridCellRenderer<ItemModel>() {

			public Object render(ItemModel itemModel, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ItemModel> store, Grid<ItemModel> grid) {
				
				String prefix = "";
				String result = null;
				Object value = itemModel.get(property);
				
				if (itemModel != null && itemModel.getItemType() != null) {
					boolean isItem = itemModel.getItemType() == Type.ITEM;
					boolean isCategory = itemModel.getItemType() == Type.CATEGORY;
					boolean isGradebook = !isItem && !isCategory;
					boolean isPercentCategory = property.equals(ItemKey.PERCENT_CATEGORY.name());
					boolean isPercentGrade = property.equals(ItemKey.PERCENT_COURSE_GRADE.name());
					boolean isPoints = property.equals(ItemKey.POINTS.name());
					
					if (value == null)
						return null;
					
					if (isGradebook && isPercentCategory)
						return "-";
											
					boolean isIncluded = itemModel.getIncluded() != null && itemModel.getIncluded().booleanValue();				
					boolean isTooBig = (isPercentCategory || isPercentGrade) 
						&& ((Double)value).doubleValue() > 100.00001d;
					boolean isTooSmall = ((isPercentCategory && isCategory) || (isPercentGrade && isGradebook)) && ((Double)value).doubleValue() < 99.9994d;
					
					result = DataTypeConversionUtil.getDefaultNumberFormat().format(((Double)value).doubleValue());
					
					StringBuilder cssClasses = new StringBuilder();
					
					if (!isIncluded && (isItem || isCategory))
						cssClasses.append(resources.css().gbNotIncluded());
					
					if (!isItem) 
						cssClasses.append(" ").append(resources.css().gbCellStrong());
					
					if (isTooBig || isTooSmall)
						cssClasses.append(" ").append(resources.css().gbCellError());
						
					boolean isExtraCredit = itemModel.getExtraCredit() != null && itemModel.getExtraCredit().booleanValue();
					if (isExtraCredit) {
						
						if (isPercentGrade || (isPercentCategory && isItem) || isPoints) {
							cssClasses.append(" ").append(resources.css().gbCellExtraCredit());
							prefix = "+ ";
						}

					}
					
					StringBuilder builder = new StringBuilder().append("<span class=\"").append(cssClasses.toString())
						.append("\">").append(prefix).append(result).append("</span>");
					
					if ((isCategory && isPercentCategory) || (isGradebook && isPercentGrade)) {
						builder.append(" / 100");
					} 
						
					
					return builder.toString();
				
				}
				return "";
			}
			
		};
		
		percentCourseGradeColumn =  new ColumnConfig(ItemKey.PERCENT_COURSE_GRADE.name(), 
				i18n.percentCourseGradeFieldLabel(), i18n.percentCourseGradeFieldLabel().length() * CHARACTER_WIDTH + 30);
		percentCourseGradeColumn.setAlignment(HorizontalAlignment.RIGHT);
		percentCourseGradeColumn.setRenderer(numericRenderer);
		percentCourseGradeColumn.setSortable(false);
		columns.add(percentCourseGradeColumn);

		percentCategoryColumn =  new ColumnConfig(ItemKey.PERCENT_CATEGORY.name(), 
				i18n.percentCategoryFieldLabel(), i18n.percentCategoryFieldLabel().length() * CHARACTER_WIDTH + 20);
		percentCategoryColumn.setAlignment(HorizontalAlignment.RIGHT);
		percentCategoryColumn.setRenderer(numericRenderer);
		percentCategoryColumn.setSortable(false);
		columns.add(percentCategoryColumn);

		pointsColumn = new ColumnConfig(ItemKey.POINTS.name(), 
				i18n.pointsFieldLabel(), i18n.pointsFieldLabel().length() * CHARACTER_WIDTH + 30);
		pointsColumn.setAlignment(HorizontalAlignment.RIGHT);
		pointsColumn.setRenderer(numericRenderer);
		pointsColumn.setSortable(false);
		columns.add(pointsColumn);

		cm = new ColumnModel(columns);
		
		
		TreeGridView itemGridView = new ItemTreeGridView() {
			
			private String collapseHtml = GXT.IMAGES.tree_collapsed().getHTML();
			private String expandHtml = GXT.IMAGES.tree_expanded().getHTML();
			
			public String getTemplate(ModelData m, String id, String text,
					AbstractImagePrototype icon, boolean checkable,
					Joint joint, int level) {

				ItemModel itemModel = (ItemModel)m;

				boolean isIncluded = itemModel.getIncluded() != null && itemModel.getIncluded().booleanValue();
				boolean isItem = itemModel.getItemType() == Type.ITEM;
				boolean isCategory = itemModel.getItemType() == Type.CATEGORY;
				boolean isReleased = itemModel.getReleased() != null && itemModel.getReleased().booleanValue();
				int dropLowest = itemModel.getDropLowest() == null ? 0 : itemModel.getDropLowest().intValue();			
				
				StringBuffer sb = new StringBuffer();
				sb.append("<div id=\"");
				sb.append(id);
				sb.append("\" class=\"x-tree3-node\">");

				sb.append("<div class=\"x-tree3-el\">");

				String h = "";
				switch (joint) {
				case COLLAPSED:
					h = collapseHtml;
					break;
				case EXPANDED:
					h = expandHtml;
					break;
				default:
					h = "<img src=\"" + GXT.BLANK_IMAGE_URL
							+ "\" style='width: 16px'>";
				}

				sb.append("<img src=\"");
				sb.append(GXT.BLANK_IMAGE_URL);
				sb.append("\" style=\"height: 18px; width: ");
				sb.append(level * 18);
				sb.append("px;\" />");
				sb.append(h);
				if (checkable) {
					sb.append(GXT.IMAGES.unchecked().getHTML());
				} else {
					sb.append("<span></span>");
				}
				if (icon != null) {
					sb.append(icon.getHTML());
				} else {
					sb.append("<span></span>");
				}
				sb.append("<span class=\"x-tree3-node-text");
				if (!isIncluded && (isItem || isCategory))
					sb.append(" ").append(resources.css().gbNotIncluded());
				if (!isItem) 
					sb.append(" ").append(resources.css().gbCellStrong());
				else if (isReleased) 
					sb.append(" ").append(resources.css().gbReleased());
				
				boolean isExtraCredit = itemModel.getExtraCredit() != null && itemModel.getExtraCredit().booleanValue();
				if (isExtraCredit) 
					sb.append(" ").append(resources.css().gbCellExtraCredit());
				sb.append("\">&nbsp;");
				sb.append(text);
				if (dropLowest > 0) {
					sb.append("<font style=\"font-style: regular;font-size:9pt\"> -").append(dropLowest).append("</font>");
				}
				sb.append("</span>");		

				sb.append("</div>");
				sb.append("</div>");

				return sb.toString();
			}
		};

		itemGrid = new TreeGrid<ItemModel>(treeStore, cm) {
			protected void onDoubleClick(GridEvent<ItemModel> e) {
				if (e.getRowIndex() != -1) {
					fireEvent(Events.RowDoubleClick, e);
					if (e.getColIndex() != -1) {
						fireEvent(Events.CellDoubleClick, e);
					}
				}
			}
		};
		itemGrid.addListener(Events.RowDoubleClick, gridEventListener);
		itemGrid.setSelectionModel(sm);
		itemGrid.setStripeRows(false);
		itemGrid.setView(itemGridView);
		itemGrid.setWidth(500);
		
		if (isEditable)
			itemGrid.setContextMenu(newTreeContextMenu(i18n)); 
		
		TabPanel tabPanel = new AriaTabPanel();

		TabItem item = new AriaTabItem(i18n.navigationPanelFixedTabHeader()) {
			@Override
			protected void onResize(int width, int height) {
				super.onResize(width, height);

				itemGrid.setHeight(height);
			}
		};
		item.setLayout(new FitLayout());
		item.add(itemGrid);
		tabPanel.add(item);

		learnerAttributeLoader = new BaseTreeLoader<FixedColumnModel>(new TreeModelReader());

		learnerAttributeRoot = new FixedColumnModel();

		learnerAttributes = new FixedColumnModel();
		learnerAttributes.setName("Learner Attributes");
		learnerAttributes.setIdentifier("learnerAttributes");
		learnerAttributes.setParent(learnerAttributeRoot);
		learnerAttributeRoot.add(learnerAttributes);

		gradingColumns = new FixedColumnModel();
		gradingColumns.setName("Grades");
		gradingColumns.setIdentifier("gradingColumns");
		gradingColumns.setParent(learnerAttributeRoot);
		learnerAttributeRoot.add(gradingColumns);

			
		learnerAttributeStore = new TreeStore<FixedColumnModel>(learnerAttributeLoader);
		learnerAttributeTree = new TreePanel<FixedColumnModel>(learnerAttributeStore) {
			@Override
			  protected void onRender(Element target, int index) {
			    super.onRender(target, index);
			    learnerAttributeTree.removeCheckListener(checkListener);
			    learnerAttributeTree.setCheckedSelection(checkedSelection);
			    learnerAttributeTree.addCheckListener(checkListener);
			}
		};
		learnerAttributeTree.setAutoLoad(true);
		learnerAttributeTree.setCheckable(true);
		learnerAttributeTree.setCheckStyle(CheckCascade.CHILDREN);
		learnerAttributeTree.setCheckNodes(CheckNodes.LEAF);
		learnerAttributeTree.setDisplayProperty(FixedColumnKey.NAME.name());
		learnerAttributeTree.setStateful(true);
		learnerAttributeTree.setStateId(AppConstants.LEARNER_ATTRIBUTE_TREE);
		
		item = new AriaTabItem(i18n.navigationPanelDynamicTabHeader());
		item.setLayout(new FitLayout());
		learnerAttributeTree.setWidth(500);
		item.add(learnerAttributeTree);
		tabPanel.add(item);

		add(tabPanel);
	}
	
	public void onBeforeLoadItemTreeModel(GradebookModel selectedGradebook, ItemModel rootItem) {
		
	}
	
	public void onHideColumn(FixedColumnModel fixedModel) {
		if (fixedModel == null)
			return;

		if (learnerAttributeTree.isRendered())
			learnerAttributeTree.setChecked(fixedModel, false);
		else {
			checkedSelection.remove(fixedModel);
			showColumns(checkedSelection);
		}
	}
	
	public void onHideColumn(ItemModel itemModel) {
		if (itemModel == null)
			return;
	
		sm.toggle(itemModel, false);
	}
	
	public void onItemCreated(ItemModel itemModel) {
		if (itemModel == null)
			return;
		
		sm.toggle(itemModel, true);
	}
	
	public void onMaskItemTree() {
		itemGrid.mask();
	}
	
	public void onRefreshGradebookItems(GradebookModel gradebookModel, TreeLoader<ItemModel> treeLoader, final ItemModel rootItem) {
		if (itemLoadListener != null)
			treeLoader.removeLoadListener(itemLoadListener);
		
		itemLoadListener = new LoadListener() {
			public void loaderLoad(LoadEvent le) {
				ItemModel gradebookItemModel = (ItemModel)rootItem.getChild(0);
				if (gradebookItemModel != null)
					itemGrid.setExpanded(gradebookItemModel, true, true);
			}
		};
		treeLoader.addLoadListener(itemLoadListener);
		treeLoader.load(rootItem);
		if (itemGrid.isRendered())
			itemGrid.getView().refresh(true);

		if (!isLearnerAttributeTreeLoaded) {
			learnerAttributeStore.removeAll();
			this.fullStaticIdSet = new HashSet<String>();
			ConfigurationModel configModel = gradebookModel.getConfigurationModel();
			checkedSelection = new ArrayList<FixedColumnModel>();
			for (FixedColumnModel column : gradebookModel.getColumns()) {
				
				fullStaticIdSet.add(column.getIdentifier());
				
				boolean isDefaultHidden = column.isHidden();
				boolean isChecked = !configModel.isColumnHidden(AppConstants.ITEMTREE, column.getIdentifier(), isDefaultHidden);
				
				if (isChecked)
					checkedSelection.add(column);
				column.setChecked(isChecked);
				
				LearnerKey key = LearnerKey.valueOf(column.getIdentifier());
				
				switch (key.getGroup()) {
				case GRADES:
					column.setParent(gradingColumns);
					gradingColumns.add(column);
					break;
				default:
					column.setParent(learnerAttributes);
					learnerAttributes.add(column);	
				}
			}
			
			learnerAttributeLoader.addLoadListener(attributeLoadListener);
	
			learnerAttributeLoader.load(learnerAttributeRoot);
			isLearnerAttributeTreeLoaded = true;
		}
	}
	
	public void onRefreshGradebookSetup(GradebookModel gradebookModel) {
		if (addCategoryMenuItem != null)
			addCategoryMenuItem.setVisible(gradebookModel.getGradebookItemModel().getCategoryType() != CategoryType.NO_CATEGORIES);

		ConfigurationModel configModel = gradebookModel.getConfigurationModel();

		if (configModel != null) {
			switch (gradebookModel.getGradebookItemModel().getCategoryType()) {
				case NO_CATEGORIES:
				case SIMPLE_CATEGORIES:
					cm.setHidden(2, true);
					cm.setHidden(3, true);
					cm.setHidden(4, configModel.isColumnHidden(AppConstants.ITEMTREE_HEADER, AppConstants.ITEMTREE_POINTS_NOWEIGHTS, false));
					//percentCourseGradeColumn.setHidden(true);
					//percentCourseGradeColumn.setMenuDisabled(true);
					//percentCategoryColumn.setHidden(true);
					//percentCategoryColumn.setMenuDisabled(true);
					//pointsColumn.setHidden(configModel.isColumnHidden(AppConstants.ITEMTREE_HEADER, AppConstants.ITEMTREE_POINTS_NOWEIGHTS, false));
					break;
				case WEIGHTED_CATEGORIES:
					cm.setHidden(2, configModel.isColumnHidden(AppConstants.ITEMTREE_HEADER, AppConstants.ITEMTREE_PERCENT_GRADE, false));
					cm.setHidden(3, configModel.isColumnHidden(AppConstants.ITEMTREE_HEADER, AppConstants.ITEMTREE_PERCENT_CATEGORY, false));
					cm.setHidden(4, configModel.isColumnHidden(AppConstants.ITEMTREE_HEADER, AppConstants.ITEMTREE_POINTS_WEIGHTS, false));
					
					//percentCourseGradeColumn.setHidden(configModel.isColumnHidden(AppConstants.ITEMTREE_HEADER, AppConstants.ITEMTREE_PERCENT_GRADE, false));
					//percentCategoryColumn.setHidden(configModel.isColumnHidden(AppConstants.ITEMTREE_HEADER, AppConstants.ITEMTREE_PERCENT_CATEGORY, false));
					//pointsColumn.setHidden(configModel.isColumnHidden(AppConstants.ITEMTREE_HEADER, AppConstants.ITEMTREE_POINTS_WEIGHTS, true));
					break;
			}
		}
	}
	
	public void onSingleGrade() {
		sm.deselectAll();
	}
	
	public void onSwitchGradebook(GradebookModel selectedGradebook) {
		ConfigurationModel configModel = selectedGradebook.getConfigurationModel();
		
		ItemModel gradebookItemModel = selectedGradebook.getGradebookItemModel();
		if (gradebookItemModel != null) {
			boolean isEntireGradebookChecked = true;
			for (ModelData m1 : gradebookItemModel.getChildren()) {
				ItemModel c1 = (ItemModel)m1;
				switch (c1.getItemType()) {
					case CATEGORY:
						boolean isEntireCategoryChecked = c1.getChildCount() > 0;
						for (ModelData m2 : c1.getChildren()) {
							ItemModel c2 = (ItemModel)m2;
							boolean isChecked = !configModel.isColumnHidden(AppConstants.ITEMTREE, c2.getIdentifier(), true);
							c2.setChecked(isChecked);
							if (!isChecked) {
								isEntireCategoryChecked = false;
								isEntireGradebookChecked = false;
							}
						}
						c1.setChecked(isEntireCategoryChecked);
						break;
					case ITEM:
						boolean isChecked = !configModel.isColumnHidden(AppConstants.ITEMTREE, c1.getIdentifier(), true);
						c1.setChecked(isChecked);
						if (!isChecked) {
							isEntireCategoryChecked = false;
							isEntireGradebookChecked = false;
						}
						break;
				}
			}
			gradebookItemModel.setChecked(isEntireGradebookChecked);
		}
		
		isAllowedToDropToGradebook = gradebookItemModel.getCategoryType() == CategoryType.NO_CATEGORIES;
		
		// Only allow the user to reorder if s/he has the permission
		if (DataTypeConversionUtil.checkBoolean(isEditable)) {

			TreeGridDragSource source = new TreeGridDragSource(itemGrid) {
				
				
				@Override
				protected void onDragDrop(DNDEvent event) {

				}

				/*
				@Override
				protected void onDragStart(DNDEvent e) {
					TreeItem item = tree.findItem(e.getTarget());
					if (item == null || e.getTarget(".my-tree-joint", 3) != null) {
						e.stopEvent();
						return;
					}

					boolean leaf = treeSource == TreeSource.LEAF
					|| treeSource == TreeSource.BOTH;
					boolean node = treeSource == TreeSource.NODE
					|| treeSource == TreeSource.BOTH;

					List<TreeItem> sel = tree.getSelectionModel()
					.getSelectedItems();

					if (sel.size() > 0) {
						boolean ok = true;
						for (TreeItem ti : sel) {
							ItemModel m = (ItemModel)ti.getModel();
							Type t = m.getItemType();

							if ((t != Type.GRADEBOOK)) {
								continue;
							}

							ok = false;
							break;
						}

						if (ok) {
							if (sel.get(0).getModel() != null) {
								List models = new ArrayList();
								for (TreeItem ti : sel) {
									models.add(binder.getTreeStore().getModelState(
											(ModelData) ti.getModel()));
								}
								e.setData(models);
							} else {
								e.setData(sel);
							}

							e.getStatus().update(Format.substitute(getStatusText(), sel
									.size()));

						} else {
							e.stopEvent();
						}

					} else {
						e.stopEvent();
					}
				}*/
			};
			source.addDNDListener(new DNDListener() {
				@Override
				public void dragStart(DNDEvent e) {
					
					TreeGrid<ItemModel>.TreeNode item = itemGrid.findNode(e.getTarget());
					ItemModel itemModel = item == null ? null : item.getModel();
					
					if (itemModel != null || itemModel.getItemType() == Type.GRADEBOOK) {
						e.stopEvent();
						e.getStatus().setStatus(false);
						return;
					}
					super.dragStart(e);
				}


			});

			TreeGridDropTarget target = new TreeGridDropTarget(itemGrid) {

				@Override
				protected void appendModel(final ModelData p, final List<ModelData> models, int index) {
					if (models.size() == 0)
						return;
					if (models.get(0) instanceof TreeModel) {
						TreeModel test = (TreeModel) models.get(0);
						// drop is in form from tree store
						if (test.getPropertyNames().contains("model")) {
							final ItemModel item = (ItemModel)test.get("model");
							item.setItemOrder(Integer.valueOf(index));
							if (p != null && item.getItemType() == Type.ITEM)
								item.setCategoryId(((ItemModel)p).getCategoryId());
														
							Gradebook2RPCServiceAsync service = Registry.get("service");
							AsyncCallback<ItemModel> callback = new AsyncCallback<ItemModel>() {

								public void onFailure(Throwable caught) {
									Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(caught, "Failed to update item: "));
									Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
								}

								public void onSuccess(ItemModel result) {
									Dispatcher.forwardEvent(GradebookEvents.BeginItemUpdates.getEventType());

									GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
									selectedGradebook.setGradebookItemModel(result);
									Dispatcher.forwardEvent(GradebookEvents.RefreshGradebookItems.getEventType(),
												selectedGradebook);
									Dispatcher.forwardEvent(GradebookEvents.EndItemUpdates.getEventType());
									Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
								}
							};

							service.update(item, EntityType.ITEM, null, SecureToken.get(), callback);

							/*for (ModelData tm : models) {
								ModelData child = tm.get("model");
								List sub = (List) ((TreeModel) tm)
										.getChildren();
								appendModel(child, sub, 0);

							}*/
							return;
						}
						
					}
				}
				/*
				protected void showFeedback(DNDEvent event) {
					final TreeNode item = treeGrid.findNode(event.getTarget());
					if (item == null) {
						if (activeItem != null) {
							clearStyle(activeItem);
						}
					}

					if (event.getDropTarget().getComponent() == event
							.getDragSource().getComponent()) {
						TreeGrid source = (TreeGrid) event.getDragSource().getComponent();
						ModelData sel = source.getSelectionModel()
								.getSelectedItem();
						ModelData overModel = item.getModel();
						if (overModel == sel) {
							Insert.get().hide();
							event.getStatus().setStatus(false);
							return;
						}
						List<ModelData> children = treeGrid.getTreeStore()
								.getChildren(sel, true);
						if (children.contains(item.getModel())) {
							Insert.get().hide();
							event.getStatus().setStatus(false);
							return;
						}
					}

					boolean append = feedback == Feedback.APPEND
							|| feedback == Feedback.BOTH;
					boolean insert = feedback == Feedback.INSERT
							|| feedback == Feedback.BOTH;

					if (item == null) {
						handleAppend(event, item);
					} else if (insert) {
						boolean isValidDrop = true;
						if (activeItem.getParent() != null) {
														
							if (!isAllowedToDropToGradebook) {
								ItemModel itemModel = (ItemModel)activeItem.getModel();
								ItemModel parentItem = (ItemModel)activeItem.getParent().getModel();
								isValidDrop = parentItem.getItemType() == Type.GRADEBOOK && itemModel.getItemType() == Type.CATEGORY;
							}
						}
						if (! isValidDrop) {
							//event.getStatus().setStatus(false);
							//return;
						}
						handleInsert(event, item);
					} else if ((!item.isLeaf() || isAllowDropOnLeaf()) && append) {
						handleAppend(event, item);
					} else {
						if (activeItem != null) {
							clearStyle(activeItem);
						}
						status = -1;
						activeItem = null;
						appendItem = null;
						Insert.get().hide();
						event.getStatus().setStatus(false);
					}
				}*/
				
				/*
				protected void handleInsert(DNDEvent event, final TreeNode item) {
					// clear any active append item
					if (activeItem != null && activeItem != item) {
						clearStyle(activeItem);
					}

					int height = treeGrid.getView().getRow(item.getModel())
							.getOffsetHeight();
					int mid = height / 2;
					int top = treeGrid.getView().getRow(item.getModel())
							.getAbsoluteTop();
					mid += top;
					int y = event.getClientY();
					boolean before = y < mid;

					if (!item.isLeaf() || isAllowDropOnLeaf()) {
						if ((before && y > top + 4)
								|| (!before && y < top + height - 4)) {
							handleAppend(event, item);
							return;
						}
					}

					if (event.getDropTarget().getComponent() == event
							.getDragSource().getComponent()) {
						TreeGrid source = (TreeGrid) event.getDragSource().getComponent();
						ModelData sel = source.getSelectionModel()
								.getSelectedItem();
						ModelData overModel = item.getModel();
						if (before
								&& overModel == treeGrid.getTreeStore()
										.getNextSibling(sel)) {
							Insert.get().hide();
							event.getStatus().setStatus(false);
							return;
						}
					}

					appendItem = null;

					status = before ? 0 : 1;

					if (activeItem != null) {
						clearStyle(activeItem);
					}

					activeItem = item;

					if (activeItem != null) {
						int idx = 0;
						boolean isValidDrop = true;
						String parent = "no parent";
						if (activeItem.getParent() != null) {
							parent = ((ItemModel)activeItem.getParent().getModel()).getName();
														
							if (!isAllowedToDropToGradebook) {
								ItemModel itemModel = (ItemModel)activeItem.getModel();
								ItemModel parentItem = (ItemModel)activeItem.getParent().getModel();
								isValidDrop = parentItem.getItemType() == Type.GRADEBOOK && itemModel.getItemType() == Type.CATEGORY;
							}
							
							idx = activeItem.getParent().indexOf(item);
						} else {
							idx = treeGrid.getTreeStore().indexOf(
									activeItem.getModel());
						}

						String status = "x-tree-drop-ok-between";
						if (before && idx == 0) {
							status = "x-tree-drop-ok-above";
						} else if (idx > 1 && !before
								&& idx == item.getParent().getItemCount() - 1) {
							status = "x-tree-drop-ok-below";
						}
						//Info.display("Status", status + " " + idx + " " + parent);
						
						//if (isValidDrop)
						//	event.getStatus().setStatus(true, status);
						//else {
						//	event.getStatus().setStatus(false);
						//	return;
						//}
						
						if (before) {
							showInsert(event, (Element) treeGrid.getView()
									.getRow(item.getModel()), true);
						} else {
							showInsert(event, (Element) treeGrid.getView()
									.getRow(item.getModel()), false);
						}
					}
				}

				private void showInsert(DNDEvent event, Element elem, boolean before) {
				    Insert insert = Insert.get();
				    insert.show();
				    Rectangle rect = El.fly(elem).getBounds();
				    int y = before ? rect.y - 2 : (rect.y + rect.height - 4);
				    insert.setBounds(rect.x, y, rect.width, 6);
				}*/
				
				/*
				@Override
				protected void appendModel(final ModelData p, final TreeModel model, final int index) {

					final ItemModel child = model.get("model");
					child.setItemOrder(Integer.valueOf(index));

					Type childType = child.getItemType();

					if (childType == Type.GRADEBOOK)
						return;

					if (p == null) {
						return;
					} else {
						ItemModel destParent = (ItemModel)p;
						ItemModel orgParent = (ItemModel) child.getParent();

						Long destParentId = destParent == null ? null : destParent.getCategoryId();
						Long orgParentId = orgParent == null ? null : orgParent.getCategoryId();

						Type parentType = destParent.getItemType();

						if (childType == Type.ITEM && parentType == Type.CATEGORY && destParentId != null && orgParentId != null
								&& destParentId.compareTo(orgParentId) != 0)
							child.setCategoryId(destParentId);

						Type allowedParentType = Type.CATEGORY;

						GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
						if (selectedGradebook.getGradebookItemModel().getCategoryType() == CategoryType.NO_CATEGORIES)
							allowedParentType = Type.GRADEBOOK;

						if (childType == Type.ITEM && parentType != allowedParentType)
							return;
						if (childType == Type.CATEGORY && parentType != Type.GRADEBOOK)
							return;
						if (parentType == Type.ROOT)
							return;

					}

					Dispatcher.forwardEvent(GradebookEvents.MaskItemTree.getEventType());

					Gradebook2RPCServiceAsync service = Registry.get("service");
					AsyncCallback<ItemModel> callback = new AsyncCallback<ItemModel>() {

						public void onFailure(Throwable caught) {
							Dispatcher.forwardEvent(GradebookEvents.Notification.getEventType(), new NotificationEvent(caught, "Failed to update item: "));
							Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
						}

						public void onSuccess(ItemModel result) {
							Dispatcher.forwardEvent(GradebookEvents.BeginItemUpdates.getEventType());

							ItemModel activeItem = ItemModelProcessor.getActiveItem(result);

							int realIndex = activeItem.getItemOrder().intValue();

							if (binder != null) {
								ModelData m = (ModelData) model.get("model");
								ModelData px = binder.getTreeStore().getParent(m);
								if (px != null) {

									binder.getTreeStore().remove(px, m);
								} else {
									binder.getTreeStore().remove(m);
								}
							} 

							if (p == null) {
								binder.getTreeStore().insert(activeItem, realIndex, false);
							} else {
								binder.getTreeStore().insert(p, activeItem, realIndex, false);
							}
							List<ModelData> children = model.getChildren();
							for (int i = 0; i < children.size(); i++) {
								appendModel(activeItem, (TreeModel) children.get(i), i);
							}

							ItemModelProcessor processor = new ItemModelProcessor(result) {

								public void doGradebook(ItemModel itemModel) {
									if (!itemModel.isActive())
										binder.getTreeStore().update(itemModel);
								}

								public void doCategory(ItemModel itemModel) {
									if (!itemModel.isActive())
										binder.getTreeStore().update(itemModel);
								}

								public void doItem(ItemModel itemModel) {
									if (!itemModel.isActive())
										binder.getTreeStore().update(itemModel);
								}

							};

							processor.process();

							Dispatcher.forwardEvent(GradebookEvents.EndItemUpdates.getEventType());
							Dispatcher.forwardEvent(GradebookEvents.UnmaskItemTree.getEventType());
						}
					};

					service.update((ItemModel)child, EntityType.ITEM, null, SecureToken.get(), callback);

				}*/
			};

			target.setAllowSelfAsSource(true);
			target.setFeedback(Feedback.BOTH); 
			source.setTreeGridSource(TreeSource.BOTH);
		}
	}
	
	public void onTreeStoreInitialized(TreeStore<ItemModel> treeStore, Boolean isAbleToEdit) {

		// FIXME: This one can be removed ultimately
	}
	
	public void onUnmaskItemTree() {
		itemGrid.unmask();
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
	
	protected String getAdvice() {
		return i18n.treeDirections();
	}
	
	
	private void initListeners() {

		attributeLoadListener = new LoadListener() {
			public void loaderBeforeLoad(LoadEvent le) {
				//learnerAttributeTree.removeCheckListener(checkListener);
			}
			
			public void loaderLoad(LoadEvent le) {
				//learnerAttributeTree.setCheckedSelection(checkedSelection);
				learnerAttributeTree.setExpanded(learnerAttributes, true, true);
				learnerAttributeTree.setExpanded(gradingColumns, true, true);
				//learnerAttributeTree.addCheckListener(checkListener);
			}
		};
		
		checkListener = new CheckChangedListener<FixedColumnModel>() {
			
			public void checkChanged(CheckChangedEvent<FixedColumnModel> event) {
				List<FixedColumnModel> models = event.getCheckedSelection();
				showColumns(models);
			}
			
		};
		
		
		menuSelectionListener = new SelectionListener<MenuEvent>() {

			public void componentSelected(MenuEvent me) {
				SelectionType selectionType = me.getItem().getData(selectionTypeField);
				ItemModel item = sm.getSelectedItem();
				int itemOrder = item.getItemOrder() == null ? 0 : item.getItemOrder().intValue();
				switch (selectionType) {
					case CREATE_CATEGORY:
						Dispatcher.forwardEvent(GradebookEvents.NewCategory.getEventType(), item);
						break;
					case CREATE_ITEM:
						Dispatcher.forwardEvent(GradebookEvents.NewItem.getEventType(), item);
						break;
					case UPDATE_ITEM:
						Dispatcher.forwardEvent(GradebookEvents.StartEditItem.getEventType(), item);
						break;
					case DELETE_ITEM:
						Dispatcher.forwardEvent(GradebookEvents.ConfirmDeleteItem.getEventType(), item);
						break;
					case MOVE_DOWN:
						item.setItemOrder(Integer.valueOf(itemOrder + 1));
						Dispatcher.forwardEvent(GradebookEvents.UpdateItem.getEventType(), new ItemUpdate(treeStore, null, item, false));
						break;
					case MOVE_UP:
						item.setItemOrder(Integer.valueOf(itemOrder - 1));
						Dispatcher.forwardEvent(GradebookEvents.UpdateItem.getEventType(), new ItemUpdate(treeStore, null, item, false));
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
							deleteCategoryMenuItem.setVisible(itemModel.isEditable());
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
				}
			}

		};

		gridEventListener = new Listener<GridEvent<ItemModel>>() {

			public void handleEvent(GridEvent<ItemModel> ge) {
				if (ge.getType().equals(Events.RowDoubleClick)) 
					doSelectItem(ge);
			}

		};

	}
	
	private void doSelectItem(GridEvent ge) {
		ItemModel itemModel = (ItemModel)ge.getModel();
		Dispatcher.forwardEvent(GradebookEvents.StartEditItem.getEventType(), itemModel);
		ge.stopEvent();
	}
	
	private Menu newTreeContextMenu(I18nConstants i18n) {

		treeContextMenu = new AriaMenu();
		treeContextMenu.setWidth(180);

		addCategoryMenuItem = new AriaMenuItem();
		addCategoryMenuItem.setData(selectionTypeField, SelectionType.CREATE_CATEGORY);
		addCategoryMenuItem.setIconStyle(resources.css().gbAddCategoryIcon());
		addCategoryMenuItem.setItemId(AppConstants.ID_CT_ADD_CATEGORY_MENUITEM);
		addCategoryMenuItem.setText(i18n.headerAddCategory());
		addCategoryMenuItem.setTitle(i18n.headerAddCategoryTitle());
		addCategoryMenuItem.addSelectionListener(menuSelectionListener);
		treeContextMenu.add(addCategoryMenuItem);

		updateCategoryMenuItem = new AriaMenuItem();
		updateCategoryMenuItem.setData(selectionTypeField, SelectionType.UPDATE_ITEM);
		updateCategoryMenuItem.setIconStyle(resources.css().gbEditCategoryIcon());
		updateCategoryMenuItem.setItemId(AppConstants.ID_CT_EDIT_CATEGORY_MENUITEM);
		updateCategoryMenuItem.setText(i18n.headerEditCategory());
		updateCategoryMenuItem.addSelectionListener(menuSelectionListener);
		treeContextMenu.add(updateCategoryMenuItem);

		deleteCategoryMenuItem = new AriaMenuItem();
		deleteCategoryMenuItem.setData(selectionTypeField, SelectionType.DELETE_ITEM);
		deleteCategoryMenuItem.setIconStyle(resources.css().gbDeleteCategoryIcon());
		deleteCategoryMenuItem.setItemId(AppConstants.ID_CT_DELETE_ITEM_MENUITEM);
		deleteCategoryMenuItem.setText(i18n.headerDeleteCategory());
		deleteCategoryMenuItem.addSelectionListener(menuSelectionListener);
		treeContextMenu.add(deleteCategoryMenuItem);


		MenuItem menuItem = new AriaMenuItem();
		menuItem.setData(selectionTypeField, SelectionType.CREATE_ITEM);
		menuItem.setIconStyle(resources.css().gbAddItemIcon());
		menuItem.setItemId(AppConstants.ID_CT_ADD_ITEM_MENUITEM);
		menuItem.setText(i18n.headerAddItem());
		menuItem.setTitle(i18n.headerAddItemTitle());
		menuItem.addSelectionListener(menuSelectionListener);
		treeContextMenu.add(menuItem);

		updateItemMenuItem = new AriaMenuItem();
		updateItemMenuItem.setData(selectionTypeField, SelectionType.UPDATE_ITEM);
		updateItemMenuItem.setIconStyle(resources.css().gbEditItemIcon());
		updateItemMenuItem.setItemId(AppConstants.ID_CT_EDIT_ITEM_MENUITEM);
		updateItemMenuItem.setText(i18n.headerEditItem());
		updateItemMenuItem.addSelectionListener(menuSelectionListener);
		treeContextMenu.add(updateItemMenuItem);

		deleteItemMenuItem = new AriaMenuItem();
		deleteItemMenuItem.setData(selectionTypeField, SelectionType.DELETE_ITEM);
		deleteItemMenuItem.setIconStyle(resources.css().gbDeleteItemIcon());
		deleteItemMenuItem.setItemId(AppConstants.ID_CT_DELETE_ITEM_MENUITEM);
		deleteItemMenuItem.setText(i18n.headerDeleteItem());
		deleteItemMenuItem.addSelectionListener(menuSelectionListener);
		treeContextMenu.add(deleteItemMenuItem);
		
		/*moveDownMenuItem = new AriaMenuItem();
		moveDownMenuItem.setData(selectionTypeField, SelectionType.MOVE_DOWN);
		moveDownMenuItem.setIconStyle("gbMoveDownIcon");
		moveDownMenuItem.setItemId(AppConstants.ID_CT_MOVE_DOWN_MENUITEM);
		moveDownMenuItem.setText(i18n.headerMoveDown());
		moveDownMenuItem.addSelectionListener(menuSelectionListener);
		treeContextMenu.add(moveDownMenuItem);
		
		moveUpMenuItem = new AriaMenuItem();
		moveUpMenuItem.setData(selectionTypeField, SelectionType.MOVE_UP);
		moveUpMenuItem.setIconStyle("gbMoveUpIcon");
		moveUpMenuItem.setItemId(AppConstants.ID_CT_MOVE_UP_MENUITEM);
		moveUpMenuItem.setText(i18n.headerMoveUp());
		moveUpMenuItem.addSelectionListener(menuSelectionListener);
		treeContextMenu.add(moveUpMenuItem);*/
		
		return treeContextMenu;
	}
	
	private void showColumns(List<FixedColumnModel> models) {
		HashSet<String> staticIds = new HashSet<String>();
		
		if (models == null)
			return;
		
		for (int i=0;i<models.size();i++) {
			FixedColumnModel model = models.get(i);
		
			String id = model.get("id");

			if (id == null)
				id = model.get(ItemKey.ID.name());

			if (id != null)
				staticIds.add(id);
		}
		Dispatcher.forwardEvent(GradebookEvents.ShowColumns.getEventType(), 
				new ShowColumnsEvent(fullStaticIdSet, staticIds));
	}
}
