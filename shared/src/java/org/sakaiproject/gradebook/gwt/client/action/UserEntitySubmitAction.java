package org.sakaiproject.gradebook.gwt.client.action;

import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;

public class UserEntitySubmitAction extends UserEntityUpdateAction<StudentModel> {

	private static final long serialVersionUID = 1L;

	public UserEntitySubmitAction() {
		super(ActionType.SUBMITTED);
	}

	public UserEntitySubmitAction(Gradebook gbModel, StudentModel model, String key, 
			ClassType classType, Object value, Object startValue) {
		super(gbModel, model, key, classType, value, startValue);
		setEntityType(EntityType.LEARNER);
		setActionType(ActionType.SUBMITTED);
	}
	
	public String toString() {
		StringBuilder text = new StringBuilder();

		Object value = getValue();
		Object startValue = getStartValue();

		text.append(getActionType().getVerb()).append(" \"").append(value).append("\"");

		return text.toString();
	}

}
