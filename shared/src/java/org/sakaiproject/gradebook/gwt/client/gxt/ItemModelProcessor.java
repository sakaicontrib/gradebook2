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

import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.type.ItemType;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;

public abstract class ItemModelProcessor {

	protected Item result;

	private Item gradebookItemModel;

	public ItemModelProcessor(Item gradebookItemModel) {
		this.gradebookItemModel = gradebookItemModel;
	}

	public void process() {

		processItem(gradebookItemModel, -1);

	}

	public void doGradebook(Item gradebookModel, int childIndex) {
		doGradebook(gradebookModel);
	}

	public void doGradebook(Item gradebookModel) {

	}

	public void doCategory(Item categoryModel) {

	}

	public void doCategory(Item categoryModel, int childIndex) {
		doCategory(categoryModel);
	}

	public void doItem(Item itemModel) {

	}

	public void doItem(Item itemModel, int childIndex) {
		doItem(itemModel);
	}


	private void processItem(Item itemModel, int childIndex) {

		if (itemModel == null)
			return;

		ItemType itemType = itemModel.getItemType();

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

		List children = ((BaseTreeModel)itemModel).getChildren();

		if (children != null) {
			for (int i=0;i<children.size();i++) {
				processItem((Item)children.get(i), i);
			}
		}

	}

	public static Item getActiveItem(Item parent) {
		if (parent.isActive())
			return parent;

		for (ModelData m : ((BaseTreeModel)parent).getChildren()) {
			Item c = (Item)m;
			if (c.isActive()) {
				return c;
			}

			if (((BaseTreeModel)c).getChildCount() > 0) {
				Item activeItem = getActiveItem(c);

				if (activeItem != null)
					return activeItem;
			}
		}

		return null;
	}

	public Item getResult() {
		return result;
	}

}
