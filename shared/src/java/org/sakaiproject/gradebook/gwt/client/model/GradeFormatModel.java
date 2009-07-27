package org.sakaiproject.gradebook.gwt.client.model;

public class GradeFormatModel extends EntityModel {

	private static final long serialVersionUID = 1L;

	public enum Key { ID, NAME };
	
	
	public String getName() {
		return get(Key.NAME.name());
	}
	
	public void setName(String name) {
		set(Key.NAME.name(), name);
	}
	
	@Override
	public String getDisplayName() {
		return getName();
	}

	@Override
	public String getIdentifier() {
		return get(Key.ID.name());
	}
	
	public void setIdentifier(String id) {
		set(Key.ID.name(), id);
	}

}
