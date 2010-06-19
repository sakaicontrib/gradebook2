package org.sakaiproject.gradebook.gwt.client.dev;

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.type.ItemType;

import com.google.gwt.core.client.GWT;

public class ItemUtil {


	public static void showItem(Item item) {

		GWT.log(" ");
		GWT.log("===== START ====");
		gwtLogItems(item);
		GWT.log("===== END ====");
		GWT.log(" ");
	}

	private static void gwtLogItems(Item item) {

		ItemType itemType = item.getItemType();

		switch(itemType) {

		case ITEM:
			GWT.log("#-#-# S_ID = " + item.get(ItemKey.S_ID.name()) + 
					" : S_CTGRY_ID = " + item.get(ItemKey.S_CTGRY_ID.name()) +
					" : L_CTGRY_ID = " + item.get(ItemKey.L_CTGRY_ID.name()) +
					" : S_PARENT = " + item.get(ItemKey.S_PARENT.name()));
			break;

		case CATEGORY:
			GWT.log("#-# S_ID = " + item.get(ItemKey.S_ID.name()) +
					" : S_CTGRY_ID = " + item.get(ItemKey.S_CTGRY_ID.name()) +
					" : L_CTGRY_ID = " + item.get(ItemKey.L_CTGRY_ID.name()) +
					" : S_PARENT = " + item.get(ItemKey.S_PARENT.name()));
			break;

		case GRADEBOOK:
			GWT.log("# S_ID = " + item.get(ItemKey.S_ID.name()) +
					" : S_CTGRY_ID = " + item.get(ItemKey.S_CTGRY_ID.name()) +
					" : L_CTGRY_ID = " + item.get(ItemKey.L_CTGRY_ID.name()) +
					" : S_PARENT = " + item.get(ItemKey.S_PARENT.name()));
			break;

		default:

		}


		List<Item> children = item.getSubItems();
		if(null != children) {

			for(Item child : children) {
				gwtLogItems(child);
			}
		}
	}

}
