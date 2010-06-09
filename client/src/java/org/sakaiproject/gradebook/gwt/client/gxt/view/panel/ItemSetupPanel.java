package org.sakaiproject.gradebook.gwt.client.gxt.view.panel;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.gxt.ItemModelProcessor;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;

public class ItemSetupPanel extends GradebookPanel {

	private ComboBox<ItemModel> categoryPicker;
	private ListStore<ItemModel> categoriesStore;
	private CellEditor categoryEditor;
	private ListStore<ItemModel> itemStore;
	private EditorGrid<ItemModel> itemGrid;

	public ItemSetupPanel() {

		super();

		categoriesStore = new ListStore<ItemModel>();

		setLayout(new FitLayout());

		ArrayList<ColumnConfig> itemColumns = new ArrayList<ColumnConfig>();

		TextField<String> textField = new TextField<String>();
		textField.addInputStyleName(resources.css().gbTextFieldInput());
		CellEditor textCellEditor = new CellEditor(textField);

		ColumnConfig name = new ColumnConfig(ItemKey.S_NM.name(), "Item", 200);
		name.setEditor(textCellEditor);
		itemColumns.add(name);

		ColumnConfig percentCategory = new ColumnConfig(ItemKey.D_PCT_CTGRY.name(), "% Category", 100);
		percentCategory.setEditor(new CellEditor(new NumberField()));
		itemColumns.add(percentCategory);

		ColumnConfig points = new ColumnConfig(ItemKey.D_PNTS.name(), "Points", 100);
		points.setEditor(new CellEditor(new NumberField()));
		itemColumns.add(points);

		categoryPicker = new ComboBox<ItemModel>(); 
		//categoryPicker.setAllowBlank(false); 
		//categoryPicker.setAllQuery(null);
		categoryPicker.setDisplayField(ItemKey.S_NM.name());  
		//categoryPicker.setEditable(false);
		categoryPicker.setEditable(true);
		categoryPicker.setTriggerAction(TriggerAction.ALL);
		categoryPicker.setEmptyText("Required");
		categoryPicker.setFieldLabel("Category");
		categoryPicker.setForceSelection(true);
		categoryPicker.setStore(categoriesStore);
		categoryPicker.setValueField(ItemKey.S_ID.name());
		//categoryPicker.addInputStyleName(resources.css().gbTextFieldInput());

		//ColumnConfig category = new ColumnConfig(ItemKey.L_CTGRY_ID.name(), "Category", 140);
		//ColumnConfig category = new ColumnConfig(ItemKey.S_ID.name(), "Category", 140);
		ColumnConfig category = new ColumnConfig(ItemKey.L_CTGRY_ID.name(), "Category", 140);

		categoryEditor = new CellEditor(categoryPicker) {

			@Override
			public Object preProcessValue(Object value) {
				
				Long categoryId = (Long) value;
				
				ItemModel categoryModel = categoriesStore.findModel(ItemKey.L_ITM_ID.name());
				
				return categoryModel;
				
//				GWT.log("DEBUG: CellEditor.preProcessValue()");
//				GWT.log("DEBUG: CellEditor.preProcessValue() value = " + value);
//				
//				ItemModel itemModel = itemStore.findModel(ItemKey.S_ID.name(), (String) value);
//				String categoryName = itemModel.get(ItemKey.S_PARENT.name());
//				GWT.log("DEBUG: CellEditor.preProcessValue() categoryName = " + categoryName);
//				
//				ItemModel categoryModel = categoriesStore.findModel(ItemKey.S_NM.name(), categoryName);
//				if(null == categoryModel) {
//					GWT.log("DEBUG: CellEditor.preProcessValue() categoryModel = NULL");
//				}
//				
//				GWT.log("DEBUG: CellEditor.preProcessValue() categoryName = " + categoryModel.getName());
//				return categoryModel;
			}
			
			@Override
			public Object postProcessValue(Object value) {
				GWT.log("DEBUG: CellEditor.postProcessValue()");
				GWT.log("DEBUG: CellEditor.postProcessValue() value = " + value);

				if (value == null) {
					
					return value;
				}
				
				String identifier = ((ItemModel) value).getIdentifier();
				GWT.log("DEBUG: CellEditor.postProcessValue() identifier = " + identifier);
				GWT.log("DEBUG: CellEditor.postProcessValue() name = " + ((ItemModel) value).getName());
				
				// FIXME: we need to figure out how to handle both cases were the identifier is:
				// - a string: NEW:CAT:N
				// - a number: N
				
				Long categoryId = null;
				try {
					
					categoryId = Long.valueOf(identifier);
				}
				catch(NumberFormatException nfe) {
					
				}
				
				//return identifier;
				return categoryId;
			}
		};
		
		category.setEditor(categoryEditor);

		category.setRenderer(new GridCellRenderer() {

			public String render(ModelData model, String property, ColumnData config, 
					int rowIndex, int colIndex, ListStore store, Grid grid) {

				GWT.log("DEBUG: GridCellRenderer.render()");


				Object identifier = model.get(property);
				GWT.log("DEBUG: GCR.render() property = " + property);
				GWT.log("DEBUG: GCR.render() identifier = " + identifier);
				
				String lookupId = null;

				if (identifier instanceof Long) 
					lookupId = String.valueOf(identifier);
				else
					lookupId = (String)identifier;

				//GWT.log("DEBUG: REN lookupId = " + lookupId);
				Item itemModel = categoriesStore.findModel(ItemKey.S_ID.name(), lookupId);

				if (itemModel == null) {
					GWT.log("DEBUG: REN ItemModel is null : returning name = " + model.get(ItemKey.S_PARENT.name()));
					List<ItemModel> categories = categoriesStore.getModels();
					for(ItemModel category : categories) {
						GWT.log("DEBUG: REN ... categoryId = " + category.getIdentifier() + " : categoryName = " + category.getName());
					}
					return model.get(ItemKey.S_PARENT.name());
				}
				
				

				GWT.log("DEBUG: REN returning categoryNane = " + itemModel.getName());
				return itemModel.getName();
			}

		});
		
		itemColumns.add(category);

		ColumnModel itemColumnModel = new ColumnModel(itemColumns);
		itemStore = new ListStore<ItemModel>();

		itemGrid = new EditorGrid<ItemModel>(itemStore, itemColumnModel);
		itemGrid.setBorders(true);
		//itemGrid.setView(new BaseCustomGridView());
		itemGrid.setView(new GridView());
		add(itemGrid);

	}

	public void onRender(Item gradebookItemModel) {
		
		refreshCategoryPickerStore(gradebookItemModel);

		List<ItemModel> gradeItems = (List<ItemModel>) getGradeItems(gradebookItemModel);
		itemStore.add(gradeItems);
	}
	

	/*
	 * Get all the grade items
	 */
	private ArrayList<? extends Item> getGradeItems(Item gradebookItemModel) {

		ArrayList<Item> items = new ArrayList<Item>();

		CategoryType categoryType = gradebookItemModel.getCategoryType();

		if(CategoryType.NO_CATEGORIES == categoryType) {

			items.addAll(gradebookItemModel.getSubItems());
		}
		else {

			List<Item> categories = gradebookItemModel.getSubItems();

			for(Item category : categories) {

				items.addAll(category.getSubItems());
			}
		}

		return items;
	}

	
	private void refreshCategoryPickerStore(Item gradebookItemModel) {
		categoriesStore.removeAll();
		if (gradebookItemModel != null) {

			ItemModelProcessor processor = new ItemModelProcessor(gradebookItemModel) {

				@Override
				public void doCategory(Item categoryModel) {
					categoriesStore.add((ItemModel)categoryModel);
				}

			};

			processor.process();
		}
	}
	
	public void showItems() {
		List<ItemModel> items = itemStore.getModels();
		for(Item item : items) {
			GWT.log("DEBUG: XX Item : name = " + item.getName() + " : categoryName = " + item.getCategoryName() + " : categoryId = " + item.getCategoryId());
		}
	}
}
