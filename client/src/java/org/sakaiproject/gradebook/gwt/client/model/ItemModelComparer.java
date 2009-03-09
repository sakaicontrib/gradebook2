package org.sakaiproject.gradebook.gwt.client.model;

import com.extjs.gxt.ui.client.data.ModelComparer;

public class ItemModelComparer<M extends ItemModel> implements ModelComparer<M> {

	public boolean equals(M m1, M m2) {
		if (m1 == null && m2 == null)
			return true;
		else if (m1 == null)
			return false;
		else if (m2 == null)
			return false;
			
		String id1 = new StringBuilder().append(m1.getItemType()).append(m1.getIdentifier()).toString();
		String id2 = new StringBuilder().append(m2.getItemType()).append(m2.getIdentifier()).toString();
		
		if (id1 != null && id2 != null)
			return id1.equals(id2);
		
		return false;
	}

}
