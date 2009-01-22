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

import org.sakaiproject.gradebook.gwt.client.model.AssignmentModel;
import org.sakaiproject.gradebook.gwt.client.model.CategoryModel;
import org.sakaiproject.gradebook.gwt.client.model.CommentModel;
import org.sakaiproject.gradebook.gwt.client.model.EntityModel;
import org.sakaiproject.gradebook.gwt.client.model.GradeRecordModel;
import org.sakaiproject.gradebook.gwt.client.model.GradeScaleRecordModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemEntityModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;


public class UserEntityUpdateAction<M extends EntityModel> extends UserEntityAction<M> {

	private static final long serialVersionUID = 1L;

	private Boolean doRecalculateChildren;
	
	public UserEntityUpdateAction() {
		super(ActionType.UPDATE);
	}
	
	public UserEntityUpdateAction(ActionType actionType) {
		super(actionType);
	}
	
	public UserEntityUpdateAction(GradebookModel gbModel, M model, String key, ClassType classType, Object value, Object startValue) {
		super(gbModel, ActionType.UPDATE);
		setModel(model);
		setEntityName(model.getDisplayName());
		setKey(key);
		this.classType = classType;
		
		setValue(value);
		setStartValue(startValue);
		
		if (model instanceof StudentModel) {
			setEntityType(EntityType.STUDENT);
			setActionType(ActionType.GRADED);
		} else if (model instanceof GradeRecordModel) {
			setEntityType(EntityType.GRADE_RECORD);
		} else if (model instanceof AssignmentModel) {
			setEntityType(EntityType.GRADE_ITEM);
		} else if (model instanceof CategoryModel) {
			setEntityType(EntityType.CATEGORY);
		} else if (model instanceof GradebookModel) {
			setEntityType(EntityType.GRADEBOOK);
		} else if (model instanceof CommentModel) {
			setEntityType(EntityType.COMMENT);
		} else if (model instanceof GradeScaleRecordModel) {
			setEntityType(EntityType.GRADE_SCALE);
		}
		
	}
		
	@Override
	public void announce(Object... params) {
		notifyUser(" '{0}', set '{1}' to '{2}' ", params);
	}
	
	public Boolean getDoRecalculateChildren() {
		return doRecalculateChildren;
	}

	public void setDoRecalculateChildren(Boolean doRecalculateChildren) {
		this.doRecalculateChildren = doRecalculateChildren;
	}
	
	public String toString() {
		StringBuilder text = new StringBuilder();
		
		Object value = getValue();
		Object startValue = getStartValue();
		
		/*String propertyName = getPropertyName();
		
		if (propertyName == null)
			propertyName = ItemEntityModel.getPropertyName(getKey());
		*/
		
		//text.append(getEntityName());
		
		//if (propertyName != null)
		//	text.append(" : ").append(propertyName).append("");
		
		text.append(getActionType().getVerb()).append(" '").append(value)
			.append("'");
		
		if (startValue != null)
			text.append(" from '").append(startValue).append("' ");
		
		return text.toString();
	}

	/*public String getPropertyName() {		
		return get(Key.PROPERTY_NAME.name());
	}

	public void setPropertyName(String propertyName) {
		set(Key.PROPERTY_NAME.name(), propertyName);
	}*/
}
