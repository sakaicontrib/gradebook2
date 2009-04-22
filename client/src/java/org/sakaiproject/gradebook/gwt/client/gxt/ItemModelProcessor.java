package org.sakaiproject.gradebook.gwt.client.gxt;

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;

public abstract class ItemModelProcessor {

	protected ItemModel result;
	
	private ItemModel gradebookItemModel;
	
	public ItemModelProcessor(ItemModel gradebookItemModel) {
		this.gradebookItemModel = gradebookItemModel;
	}
	
	public void process() {
		
		processItem(gradebookItemModel);

	}
	
	public void doCategory(ItemModel categoryModel) {
		
	}
	
	public void doItem(ItemModel itemModel) {
		
	}
	
	
	private void processItem(ItemModel itemModel) {
		
		if (itemModel == null)
			return;
		
		Type itemType = itemModel.getItemType();
		
		if (itemType != null) {
			switch (itemType) {
			case CATEGORY:
				doCategory(itemModel);
				break;
			case ITEM:
				doItem(itemModel);
				break;
			}
		}
		
		List children = itemModel.getChildren();
		
		if (children != null) {
			for (int i=0;i<children.size();i++) {
				processItem((ItemModel)children.get(i));
			}
		}
		
	}

	public ItemModel getResult() {
		return result;
	}
	
}
