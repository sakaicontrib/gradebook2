package org.sakaiproject.gradebook.gwt.client.model;

public class UserModel extends EntityModel {

	private static final long serialVersionUID = 1L;
	
	public enum Key { ID, USER_DISPLAY_NAME };

	public UserModel() {
		super();
	}
	
	public UserModel(String userId, String userDisplayName) {
		super();
		setUserId(userId);
		setUserDisplayName(userDisplayName);
	}
	
	public void setUserId(String userId) {
		set(Key.ID.name(), userId);
	}
	
	public String getUserId() {
		return get(Key.ID.name());
	}
	
	public String getUserDisplayName() {
		return get(Key.USER_DISPLAY_NAME.name());
	}
	
	public void setUserDisplayName(String userDisplayName) {
		set(Key.USER_DISPLAY_NAME.name(), userDisplayName);
	}
	
	@Override
	public String getIdentifier() {
		return getUserId();
	}
	
	@Override
	public String getDisplayName() {
		return getDisplayName();
	}
	
}
