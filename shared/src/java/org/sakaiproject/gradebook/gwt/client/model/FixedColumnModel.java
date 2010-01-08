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

	private static final long serialVersionUID = 1L;
	
	private boolean isChecked;
	
	public FixedColumnModel() {
		super();
		setUnweighted(Boolean.FALSE);
		setHidden(Boolean.FALSE);
		setEditable(Boolean.FALSE);
	}
	
	public FixedColumnModel(LearnerKey key, String displayName, Integer width, Boolean isHidden) {
		setIdentifier(key.name());
		setName(displayName);
		setKey(key.name());
		setWidth(width);
		setUnweighted(Boolean.FALSE);
		setHidden(isHidden);
		setEditable(Boolean.FALSE);
	}
	
	public FixedColumnModel(Long assignmentId, String name, LearnerKey key, Integer width) {
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
		return get(FixedColumnKey.ID.name());
	}

	public void setIdentifier(String id) {
		set(FixedColumnKey.ID.name(), id);
	}

	public String getName() {
		return get(FixedColumnKey.NAME.name());
	}

	public void setName(String name) {
		set(FixedColumnKey.NAME.name(), name);
	}

	public Integer getWidth() {
		return get(FixedColumnKey.WIDTH.name());
	}

	public void setWidth(Integer width) {
		set(FixedColumnKey.WIDTH.name(), width);
	}

	public void setAssignmentId(Long assignmentId) {
		set(FixedColumnKey.ASSIGNMENT_ID.name(), assignmentId);
	}
	

	public Long getAssignmentId() {
		return get(FixedColumnKey.ASSIGNMENT_ID.name());
	}
	
	public Double getMaxPoints() {
		return get(FixedColumnKey.POINTS.name());
	}

	public void setMaxPoints(Double maxPoints) {
		set(FixedColumnKey.POINTS.name(), maxPoints);
	}

	public String getKey() {
		return get(FixedColumnKey.STUDENT_MODEL_KEY.name());
	}

	public void setKey(String key) {
		set(FixedColumnKey.STUDENT_MODEL_KEY.name(), key);
	}

	public Long getCategoryId() {
		return get(FixedColumnKey.CATEGORY_ID.name());
	}

	public void setCategoryId(Long categoryId) {
		set(FixedColumnKey.CATEGORY_ID.name(), categoryId);
	}

	public String getCategoryName() {
		return get(FixedColumnKey.CATEGORY_NAME.name());
	}

	public void setCategoryName(String categoryName) {
		set(FixedColumnKey.CATEGORY_NAME.name(), categoryName);
	}

	public Boolean isUnweighted() {
		return get(FixedColumnKey.UNWEIGHTED.name());
	}

	public void setUnweighted(Boolean isUnweighted) {
		set(FixedColumnKey.UNWEIGHTED.name(), isUnweighted);
	}

	public Boolean isHidden() {
		return get(FixedColumnKey.HIDDEN.name());
	}

	public void setHidden(Boolean isHidden) {
		set(FixedColumnKey.HIDDEN.name(), isHidden);
	}

	public Boolean isEditable() {
		return get(FixedColumnKey.EDITABLE.name());
	}

	public void setEditable(Boolean isEditable) {
		set(FixedColumnKey.EDITABLE.name(), isEditable);
	}

	public Boolean isExtraCredit() {
		return get(FixedColumnKey.EXTRA_CREDIT.name());
	}
	
	public void setExtraCredit(Boolean isExtraCredit) {
		set(FixedColumnKey.EXTRA_CREDIT.name(), isExtraCredit);
	}

	public String getDisplayName() {
		return getName();
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

}
