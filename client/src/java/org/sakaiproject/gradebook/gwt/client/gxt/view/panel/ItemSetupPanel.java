package org.sakaiproject.gradebook.gwt.client.gxt.view.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.custom.widget.grid.BaseCustomGridView;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
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
		categoryPicker.setAllowBlank(false); 
		categoryPicker.setAllQuery(null);
		categoryPicker.setDisplayField(ItemKey.S_NM.name());  
		categoryPicker.setEditable(true);
		categoryPicker.setEmptyText("Required");
		categoryPicker.setFieldLabel("Category");
		categoryPicker.setForceSelection(true);
		categoryPicker.setStore(categoriesStore);
		categoryPicker.setValueField(ItemKey.S_ID.name());
		categoryPicker.addInputStyleName(resources.css().gbTextFieldInput());

		ColumnConfig category = new ColumnConfig(ItemKey.L_CTGRY_ID.name(), "Category", 140);

		categoryEditor = new CellEditor(categoryPicker) {

			@Override
			public Object postProcessValue(Object value) {
				if (value != null) {
					Item model = (Item)value;
					return model.getIdentifier();
				}
				return "None/Default";
			}

			@Override
			public Object preProcessValue(Object value) {
				Long id = (Long)value;

				return categoriesStore.findModel(ItemKey.S_ID.name(), String.valueOf(id));
			}

		};
		
		category.setEditor(categoryEditor);

		category.setRenderer(new GridCellRenderer() {

			public String render(ModelData model, String property, ColumnData config, 
					int rowIndex, int colIndex, ListStore store, Grid grid) {

				Object identifier = model.get(property);

				String lookupId = null;

				if (identifier instanceof Long) 
					lookupId = String.valueOf(identifier);
				else
					lookupId = (String)identifier;

				Item itemModel = categoriesStore.findModel(ItemKey.S_ID.name(), lookupId);

				if (itemModel == null)
					return AppConstants.DEFAULT_CATEGORY_NAME;

				return itemModel.getName();
			}

		});
		
		itemColumns.add(category);

		ColumnModel itemColumnModel = new ColumnModel(itemColumns);
		itemStore = new ListStore<ItemModel>();

		itemGrid = new EditorGrid<ItemModel>(itemStore, itemColumnModel);
		itemGrid.setBorders(true);
		itemGrid.setView(new BaseCustomGridView());

		add(itemGrid);

	}

	public void onRender(Item gradebookItemModel) {

		populateCategoryItemStore(gradebookItemModel);

		List<ItemModel> gradeItems = (List<ItemModel>) getGradeItems(gradebookItemModel);
		itemStore.add(gradeItems);

	}


	/*
	 * Get all the unique categories
	 */
	private List<? extends Item> getCategoryItems(List<Item> gradebookItemModels) {

		Map<String, Item> uniqueCategories = new HashMap<String, Item>();

		for(Item gradebookItemModel : gradebookItemModels) {

			CategoryType categoryType = gradebookItemModel.getCategoryType();

			if(CategoryType.NO_CATEGORIES != categoryType) {

				List<Item> categories = gradebookItemModel.getSubItems();

				for(Item category : categories) {

					String identifier = category.getIdentifier();

					if(!uniqueCategories.containsKey(identifier)) {

						uniqueCategories.put(identifier, category);
					}
				}
			}
		}

		return new ArrayList<Item>(uniqueCategories.values());
	}

	/*
	 * Get all the grade items
	 */
	private ArrayList<? extends Item> getGradeItems(Item gradebookItemModel) {

		ArrayList<Item> items = new ArrayList<Item>();

		CategoryType categoryType = gradebookItemModel.getCategoryType();

		if(CategoryType.NO_CATEGORIES == categoryType) {

			items.addAll(gradebookItemModel.getSubItems());

			GWT.log("getGradeItems : NO_CATEGRIES");
		}
		else {

			List<Item> categories = gradebookItemModel.getSubItems();

			for(Item category : categories) {

				items.addAll(category.getSubItems());
			}

			GWT.log("getGradeItems : CATEGRIES");
		}

		return items;
	}


	/*
	 * This gets all the categories (import file and selected GB)  and sets the categoriesStore
	 */
	private void populateCategoryItemStore(final Item importGradebookItemModel) {

		final Gradebook selectedGradebook = Registry.get(AppConstants.CURRENT);

		// If the current/selected GB has categories, we list them as well
		CategoryType categoryType = selectedGradebook.getGradebookItemModel().getCategoryType();

		if(CategoryType.NO_CATEGORIES != categoryType) {

			List<Item> categoryItemModels = new ArrayList<Item>();
			categoryItemModels.add(importGradebookItemModel);
			categoryItemModels.add(selectedGradebook.getGradebookItemModel());

			categoriesStore.removeAll();
			categoriesStore.add((List<ItemModel>)getCategoryItems(categoryItemModels));	
		}
		else {

			List<Item> categoryItemModels = new ArrayList<Item>();
			categoryItemModels.add(importGradebookItemModel);

			categoriesStore.removeAll();
			categoriesStore.add((List<ItemModel>) getCategoryItems(categoryItemModels));	

		}
	}
}
