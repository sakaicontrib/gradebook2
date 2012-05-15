/**********************************************************************************
 *
 * $Id:$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2008, 2009, 2010 The Regents of the University of California
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

package org.sakaiproject.gradebook.gwt.client.gxt.view.panel;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.gxt.ItemModelProcessor;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class ImportItemSetupPanel extends GradebookPanel {

	private ComboBox<ItemModel> categoryComboBox;
	private ListStore<ItemModel> categoryStore;
	private CellEditor categoryCellEditor;
	private ListStore<ItemModel> itemStore;
	private EditorGrid<ItemModel> itemGrid;

	private ColumnConfig columnConfigName;
	private ColumnConfig columnConfigPercentCategory;
	private ColumnConfig columnConfigPoints;
	private ColumnConfig columnConfigCategory;

	private static final String ITEM_MARKER = "+";
	private static final String ITEM_PREFIX = ":";


	public ImportItemSetupPanel() {

		super();

		categoryStore = new ListStore<ItemModel>();

		setLayout(new FitLayout());

		// Grid: setup / configuration
		ArrayList<ColumnConfig> itemColumns = new ArrayList<ColumnConfig>();

		TextField<String> textField = new TextField<String>();
		textField.addInputStyleName(resources.css().gbTextFieldInput());
		CellEditor textCellEditor = new CellEditor(textField);

		setHeading(i18n.navigationPanelImportHeader());
		
		columnConfigName = new ColumnConfig(ItemKey.S_NM.name(), i18n.importSetupGridItemHeader(), 200);
		columnConfigName.setEditor(textCellEditor);
		itemColumns.add(columnConfigName);

		columnConfigPercentCategory = new ColumnConfig(ItemKey.D_PCT_CTGRY.name(), i18n.importSetupGridCategoryPercentHeader(), 100);
		columnConfigPercentCategory.setEditor(new CellEditor(new NumberField()));
		itemColumns.add(columnConfigPercentCategory);

		columnConfigPoints = new ColumnConfig(ItemKey.D_PNTS.name(), i18n.importSetupGridPointsHeader(), 100);
		columnConfigPoints.setEditor(new CellEditor(new NumberField()));
		itemColumns.add(columnConfigPoints);

		categoryComboBox = new ComboBox<ItemModel>(); 
		categoryComboBox.setDisplayField(ItemKey.S_NM.name());  
		categoryComboBox.setEditable(true);
		categoryComboBox.setTriggerAction(TriggerAction.ALL);
		categoryComboBox.setForceSelection(true);
		categoryComboBox.setStore(categoryStore);
		categoryComboBox.addInputStyleName(resources.css().gbTextFieldInput());

		columnConfigCategory = new ColumnConfig(ItemKey.S_ID.name(), i18n.importSetupGridCategoryHeader(), 140);

		categoryCellEditor = new CellEditor(categoryComboBox) {

			// Called before the editor sets the value on the wrapped field
			@Override
			public Object preProcessValue(Object value) {

				// Method argument is the selected grid item model id
				String assignmentId = (String) value;

				// Get the assignment and the associated category name
				ItemModel assignment = itemStore.findModel(ItemKey.S_ID.name(), assignmentId);
				String categoryName = assignment.get(ItemKey.S_PARENT.name());

				// Find the category from the category name
				ItemModel category = categoryStore.findModel(ItemKey.S_NM.name(), categoryName);

				// Mark the assignment as the one the user is performing an action on.
				// We will use the marker in the postProcessValue() method to find the assignment,
				// since the assignment is not readily available in that method.
				assignment.set(ItemKey.S_CTGRY_ID.name(), ITEM_MARKER);

				// FIXME:
				// Returning the category. Interestingly, I am not quite sure what the returned
				// object is used for. Testing this with returning null didn't change anything.
				// Following GXT sample code ??
				return category;
			}

			// Called after the editor completes an edit.
			@Override
			public Object postProcessValue(Object value) {

				// Method argument is the selected category model
				ItemModel category = (ItemModel) value;

				// Get the categoryId
				String categoryId = category.get(ItemKey.S_ID.name());

				// We search through all the assignments to find the one that has been marked
				// by the preProcessValue() method
				List<ItemModel> assignments = itemStore.getModels();

				for(ItemModel assignment : assignments) {

					if(ITEM_MARKER.equals(assignment.get(ItemKey.S_CTGRY_ID.name()))) {

						// We have found the marked assignment and are setting the string based categoryId
						// Also, this string based category is used on the server side to reassociate the
						// assignment with the correct category before things are persisted and updated
						assignment.set(ItemKey.S_CTGRY_ID.name(), categoryId);

						// Returning the assignmentId but prefix it so that the renderer thinks
						// that something changes. If we were to just return the assignmentId,
						// the renderer is not called. This also sets the assignmentId to this new
						// prefixed ID. This is fixed in the renderer code.
						return ITEM_PREFIX + assignment.get(ItemKey.S_ID.name());
					}
				}

				// In case we didn't find a marked assignment, we return null
				return null;
			}
		};

		columnConfigCategory.setEditor(categoryCellEditor);

		columnConfigCategory.setRenderer(new GridCellRenderer<ItemModel>() {

			public String render(ItemModel model, String property, ColumnData config, 
					int rowIndex, int colIndex, ListStore<ItemModel> store, Grid<ItemModel> grid) {

				// Method argument "model" is the selected grid item model				
				String categoryId = model.get(ItemKey.S_CTGRY_ID.name());

				// Case when we render the grid for the first time
				if(null == categoryId || "".equals(categoryId)) {

					// Making sure that assignments have the categoryId
					// set if possible
					if(model.getCategoryId().equals(Long.valueOf(-1l))) {

						String categoryName = model.get(ItemKey.S_PARENT.name());
						if(null != categoryName && !"".equals(categoryName)) {

							Item category = categoryStore.findModel(ItemKey.S_NM.name(), categoryName);
							// GRBK-681 - Since we don't care if the category id is non numeric, I assume we don't care if there's no category at all.

							if (category != null)
							{
								try {

									Long catId = Long.valueOf((String) category.get(ItemKey.S_ID.name()));
									model.setCategoryId(catId);
								}
								catch(NumberFormatException nfe) {
									// We don't do anything
								}
							}
						}	
					}

					return model.get(ItemKey.S_PARENT.name());
				}
				else { // Case where a user selects a different category from the ComboBox

					// First we "restore" the itemId since we prefixed it in the postProcessValue() method
					String assignmentId = model.get(ItemKey.S_ID.name());

					if(assignmentId.startsWith(ITEM_PREFIX)) {

						String fixedAssignmentId = assignmentId.substring(ITEM_PREFIX.length());
						model.set(ItemKey.S_ID.name(), fixedAssignmentId);
					}

					ItemModel category = categoryStore.findModel(ItemKey.S_ID.name(), categoryId);
					// GRBK-681
					if (category != null )
					{
						String categoryName = category.get(ItemKey.S_NM.name());
						model.set(ItemKey.S_PARENT.name(), categoryName);

						return categoryName;
					}
					else
					{
						return ""; 
					}
				}
			}
		});

		itemColumns.add(columnConfigCategory);

		ColumnModel itemColumnModel = new ColumnModel(itemColumns);
		itemStore = new ListStore<ItemModel>();

		itemGrid = new EditorGrid<ItemModel>(itemStore, itemColumnModel);
		itemGrid.setBorders(true);

		// TODO: In the old upload code, we used the BaseCustomGridView() instead of the GridView()
		// I am not sure yet if this is still needed. Maybe for some CSS adjustments?
		itemGrid.setView(new GridView());

		add(itemGrid);

	}

	public void onRender(Item gradebookItemModel) {

		refreshCategoryPickerStore(gradebookItemModel);

		List<ItemModel> gradeItems = (List<ItemModel>) getGradeItems(gradebookItemModel);
		itemStore.add(gradeItems);

		// GRBK-643
		// If we have an import file/GB that doesn't have categories, we hide the columns in the setup grid
		CategoryType cateogryType = gradebookItemModel.getCategoryType();
		if(CategoryType.NO_CATEGORIES == cateogryType) {

			// Hide category related columns
			columnConfigCategory.setHidden(true);
			columnConfigPercentCategory.setHidden(true);
		}
	}


	/*
	 * Get all the grade items
	 */
	public ArrayList<? extends Item> getGradeItems(Item gradebookItemModel) {

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

		categoryStore.removeAll();

		if (gradebookItemModel != null) {

			ItemModelProcessor processor = new ItemModelProcessor(gradebookItemModel) {

				@Override
				public void doCategory(Item categoryModel) {
					categoryStore.add((ItemModel)categoryModel);
				}
			};

			processor.process();
		}
	}

	public ListStore<ItemModel> getItemStore() {
		return itemStore;
		
	}
}
