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

package org.sakaiproject.gradebook.gwt.client.gxt;


import java.util.List;

import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;

import com.extjs.gxt.ui.client.data.ModelData;

public abstract class ItemModelProcessor {

	protected ItemModel result;

	private ItemModel gradebookItemModel;

	public ItemModelProcessor(ItemModel gradebookItemModel) {
		this.gradebookItemModel = gradebookItemModel;
	}

	public void process() {

		processItem(gradebookItemModel, -1);

	}

	public void doGradebook(ItemModel gradebookModel, int childIndex) {
		doGradebook(gradebookModel);
	}

	public void doGradebook(ItemModel gradebookModel) {

	}

	public void doCategory(ItemModel categoryModel) {

	}

	public void doCategory(ItemModel categoryModel, int childIndex) {
		doCategory(categoryModel);
	}

	public void doItem(ItemModel itemModel) {

	}

	public void doItem(ItemModel itemModel, int childIndex) {
		doItem(itemModel);
	}


	private void processItem(ItemModel itemModel, int childIndex) {

		if (itemModel == null)
			return;

		Type itemType = itemModel.getItemType();

		if (itemType != null) {
			switch (itemType) {
				case GRADEBOOK:
					doGradebook(itemModel, childIndex);
					break;
				case CATEGORY:
					doCategory(itemModel, childIndex);
					break;
				case ITEM:
					doItem(itemModel, childIndex);
					break;
			}
		}

		List children = itemModel.getChildren();

		if (children != null) {
			for (int i=0;i<children.size();i++) {
				processItem((ItemModel)children.get(i), i);
			}
		}

	}

	public static ItemModel getActiveItem(ItemModel parent) {
		if (parent.isActive())
			return parent;

		for (ModelData m : parent.getChildren()) {
			ItemModel c = (ItemModel)m;
			if (c.isActive()) {
				return c;
			}

			if (c.getChildCount() > 0) {
				ItemModel activeItem = getActiveItem(c);

				if (activeItem != null)
					return activeItem;
			}
		}

		return null;
	}

	public ItemModel getResult() {
		return result;
	}

}
