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
package org.sakaiproject.gradebook.gwt.client.action;

import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;

public class UserEntityGradeAction extends UserEntityUpdateAction<StudentModel> {

	private static final long serialVersionUID = 1L;

	public UserEntityGradeAction() {
		super(ActionType.GRADED);
	}

	public UserEntityGradeAction(Gradebook gbModel, StudentModel model, String key, 
			ClassType classType, Object value, Object startValue) {
		super(gbModel, model, key, classType, value, startValue);
		setEntityType(EntityType.LEARNER);
		setActionType(ActionType.GRADED);
	}
	
	public String toString() {
		StringBuilder text = new StringBuilder();

		Object value = getValue();
		Object startValue = getStartValue();

		text.append(getActionType().getVerb()).append(" '").append(value)
		.append("'");

		if (startValue != null)
			text.append(" from '").append(startValue).append("' ");

		return text.toString();
	}

}
