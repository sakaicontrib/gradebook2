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
		
		return m1.equals(m2);
	}

}
