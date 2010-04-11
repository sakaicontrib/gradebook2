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

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;

public class ClientUploadUtility {

	public static List<BeanModel> convertHeadersToBeanModels(List<NewImportHeader> headers) {
		List<BeanModel> itemModels = new ArrayList<BeanModel>();
		BeanModelFactory factory = BeanModelLookup.get().getFactory(headers.get(0).getClass());
		if (factory == null) {
			throw new RuntimeException("No BeanModelFactory found for " + headers.get(0).getClass());
		}
		List<BeanModel> converted = factory.createModel(headers);
		itemModels.addAll(converted);

		return itemModels;
	}


	/*
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
	}*/


}
