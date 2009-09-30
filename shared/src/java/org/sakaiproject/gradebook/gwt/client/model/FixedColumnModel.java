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
package org.sakaiproject.gradebook.gwt.client.model;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class FixedColumnModel extends BaseTreeModel {

	public enum Key {ID, NAME, ASSIGNMENT_ID, CATEGORY_ID, CATEGORY_NAME, WIDTH, POINTS, UNWEIGHTED, 
		HIDDEN, EDITABLE, STUDENT_MODEL_KEY, EXTRA_CREDIT };
	
	private static final long serialVersionUID = 1L;
	
	public FixedColumnModel() {
		super();
		setUnweighted(Boolean.FALSE);
		setHidden(Boolean.FALSE);
		setEditable(Boolean.FALSE);
	}
	
	public FixedColumnModel(StudentModel.Key key, Integer width, Boolean isHidden) {
		setIdentifier(key.name());
		setName(key.getDisplayName());
		setKey(key.name());
		setWidth(width);
		setUnweighted(Boolean.FALSE);
		setHidden(isHidden);
		setEditable(Boolean.FALSE);
	}
	
	public FixedColumnModel(Long assignmentId, String name, StudentModel.Key key, Integer width) {
		setIdentifier(String.valueOf(assignmentId));
		setName(name);
		setAssignmentId(assignmentId);
		setKey(key.name());
		setWidth(width);
		setUnweighted(Boolean.FALSE);
		setHidden(Boolean.FALSE);
		setEditable(Boolean.TRUE);
	}
	
	public String getIdentifier() {
		return get(Key.ID.name());
	}

	public void setIdentifier(String id) {
		set(Key.ID.name(), id);
	}

	public String getName() {
		return get(Key.NAME.name());
	}

	public void setName(String name) {
		set(Key.NAME.name(), name);
	}

	public Integer getWidth() {
		return get(Key.WIDTH.name());
	}

	public void setWidth(Integer width) {
		set(Key.WIDTH.name(), width);
	}

	public void setAssignmentId(Long assignmentId) {
		set(Key.ASSIGNMENT_ID.name(), assignmentId);
	}
	

	public Long getAssignmentId() {
		return get(Key.ASSIGNMENT_ID.name());
	}
	
	public Double getMaxPoints() {
		return get(Key.POINTS.name());
	}

	public void setMaxPoints(Double maxPoints) {
		set(Key.POINTS.name(), maxPoints);
	}

	public String getKey() {
		return get(Key.STUDENT_MODEL_KEY.name());
	}

	public void setKey(String key) {
		set(Key.STUDENT_MODEL_KEY.name(), key);
	}

	public Long getCategoryId() {
		return get(Key.CATEGORY_ID.name());
	}

	public void setCategoryId(Long categoryId) {
		set(Key.CATEGORY_ID.name(), categoryId);
	}

	public String getCategoryName() {
		return get(Key.CATEGORY_NAME.name());
	}

	public void setCategoryName(String categoryName) {
		set(Key.CATEGORY_NAME.name(), categoryName);
	}

	public Boolean isUnweighted() {
		return get(Key.UNWEIGHTED.name());
	}

	public void setUnweighted(Boolean isUnweighted) {
		set(Key.UNWEIGHTED.name(), isUnweighted);
	}

	public Boolean isHidden() {
		return get(Key.HIDDEN.name());
	}

	public void setHidden(Boolean isHidden) {
		set(Key.HIDDEN.name(), isHidden);
	}

	public Boolean isEditable() {
		return get(Key.EDITABLE.name());
	}

	public void setEditable(Boolean isEditable) {
		set(Key.EDITABLE.name(), isEditable);
	}

	public Boolean isExtraCredit() {
		return get(Key.EXTRA_CREDIT.name());
	}
	
	public void setExtraCredit(Boolean isExtraCredit) {
		set(Key.EXTRA_CREDIT.name(), isExtraCredit);
	}

	public String getDisplayName() {
		return getName();
	}

}
