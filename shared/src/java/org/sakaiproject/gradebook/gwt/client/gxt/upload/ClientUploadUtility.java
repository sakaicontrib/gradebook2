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

package org.sakaiproject.gradebook.gwt.client.gxt.upload;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.gxt.upload.ImportHeader.Field;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.LearnerKey;
import org.sakaiproject.gradebook.gwt.client.model.SpreadsheetModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;

public class ClientUploadUtility {

	public static List<BeanModel> convertHeadersToBeanModels(List<ImportHeader> headers) {
		List<BeanModel> itemModels = new ArrayList<BeanModel>();
		BeanModelFactory factory = BeanModelLookup.get().getFactory(headers.get(0).getClass());
		if (factory == null) {
			throw new RuntimeException("No BeanModelFactory found for " + headers.get(0).getClass());
		}
		List<BeanModel> converted = factory.createModel(headers);
		itemModels.addAll(converted);

		return itemModels;
	}

	public static ArrayList<ItemModel> convertHeadersToItemModels(ArrayList<ImportHeader> headers) {
		ArrayList<ItemModel> items = new ArrayList<ItemModel>();

		if (headers != null) {
			for (ImportHeader header : headers) {

				ItemModel itemModel = new ItemModel();

				if (header == null)
					continue;

				if (header.getId().equals("ID"))
					continue;

				if (header.getId().equals("NAME"))
					continue;
				
				Type type = Type.ITEM;
				
				if (header.getField().equals(Field.COMMENT.name()))
					type = Type.COMMENT;

				itemModel.setIdentifier(header.getId());
				itemModel.setItemType(type);
				itemModel.setName(header.getHeaderName());
				itemModel.setPoints(header.getPoints());
				itemModel.setExtraCredit(header.getExtraCredit());
				itemModel.setIncluded(Boolean.valueOf(!DataTypeConversionUtil.checkBoolean(header.getUnincluded())));
				if (header.getCategoryId() != null) {
					itemModel.setCategoryId(Long.valueOf(header.getCategoryId()));
					itemModel.setCategoryName(header.getCategoryName());
				}
				itemModel.setPercentCategory(header.getPercentCategory());

				items.add(itemModel);

			}
		}

		return items;
	}


	public static SpreadsheetModel composeSpreadsheetModelFromBeanModels(List<BeanModel> headers, 
			List<ModelData> importRows, List<ColumnConfig> previewColumns) {

		// Create new items
		List<ItemModel> items = new ArrayList<ItemModel>();
		if (headers != null) {
			for (BeanModel importHeader : headers) {
				String categoryId = importHeader.get("categoryId");
				String categoryName = importHeader.get("categoryName");

				ItemModel item = new ItemModel();
				item.setIdentifier((String)importHeader.get("id"));
				if (categoryId != null && !categoryId.equals("null"))
					item.setCategoryId(Long.valueOf(categoryId));
				if (categoryName != null && !categoryName.equals("null"))
					item.setCategoryName(categoryName);
				item.setName((String)importHeader.get("headerName"));

				boolean isPercentage = importHeader.get("isPercentage") != null && ((Boolean)importHeader.get("isPercentage")).booleanValue();
				if (!isPercentage) 
					item.setPoints((Double)importHeader.get("points"));

				item.setPercentCategory((Double)importHeader.get("percentCategory"));

				item.setIsPercentage(Boolean.valueOf(isPercentage));

				items.add(item);
			}
		}

		return composeSpreadsheetModel(items, importRows, previewColumns);
	}

	public static SpreadsheetModel composeSpreadsheetModel(List<ItemModel> items, 
			List<ModelData> importRows, List<ColumnConfig> previewColumns) {

		SpreadsheetModel spreadsheetModel = new SpreadsheetModel();

		spreadsheetModel.setHeaders(items);

		List<ModelData> rows = new ArrayList<ModelData>();
		for (ModelData importRow : importRows) {

			boolean isUserNotFound = DataTypeConversionUtil.checkBoolean((Boolean)importRow.get("userNotFound"));

			if (isUserNotFound)
				continue;

			String uid = importRow.get("userUid");
			if (uid == null)
				uid = importRow.get("userImportId");

			ModelData student = new BaseModelData();
			student.set(LearnerKey.UID.name(), uid);

			for (ColumnConfig column : previewColumns) {
				String id = column.getId();
				student.set(id, importRow.get(id));
			}
			rows.add(student);
		}

		spreadsheetModel.setRows(rows);

		return spreadsheetModel;
	}

}
