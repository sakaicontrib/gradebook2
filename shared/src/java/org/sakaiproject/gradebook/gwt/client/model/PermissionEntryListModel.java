package org.sakaiproject.gradebook.gwt.client.model;

import java.util.List;

public class PermissionEntryListModel extends EntityModel {
	
	public enum Key { ENTRIES }

	public PermissionEntryListModel() {
		super();
	}
	
	public void setEntries(List<PermissionEntryModel> list) {
		set(Key.ENTRIES.name(), list);
	}
	
	public List<PermissionEntryModel> getEntries() {
		return get(Key.ENTRIES.name());
	}
	
	@Override
	public String getDisplayName() {
		return null;
	}

	@Override
	public String getIdentifier() {
		return null;
	}
}
