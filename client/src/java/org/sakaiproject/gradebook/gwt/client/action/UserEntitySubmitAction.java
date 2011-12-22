package org.sakaiproject.gradebook.gwt.client.action;

import org.sakaiproject.gradebook.gwt.client.gxt.model.LearnerModel;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.type.ActionType;
import org.sakaiproject.gradebook.gwt.client.model.type.ClassType;
import org.sakaiproject.gradebook.gwt.client.model.type.EntityType;

public class UserEntitySubmitAction extends UserEntityUpdateAction<LearnerModel> {

	private static final long serialVersionUID = 1L;

	public UserEntitySubmitAction() {
		super(ActionType.SUBMITTED);
	}

	public UserEntitySubmitAction(Gradebook gbModel, LearnerModel model, String key, 
			ClassType classType, Object value, Object startValue) {
		super(gbModel, model, key, classType, value, startValue);
		setEntityType(EntityType.LEARNER);
		setActionType(ActionType.SUBMITTED);
	}
	
	public String toString() {
		StringBuilder text = new StringBuilder();

		Object value = getValue();

		text.append(getActionType().getVerb()).append(" \"").append(value).append("\"");

		return text.toString();
	}

}
