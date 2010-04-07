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
package org.sakaiproject.gradebook.gwt.client.gxt.model;

import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumn;
import org.sakaiproject.gradebook.gwt.client.model.key.FixedColumnKey;
import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;

public class FixedColumnModel extends EntityTreeModel implements FixedColumn {

	private static final long serialVersionUID = 1L;
	
	public FixedColumnModel() {
		super();
		setUnweighted(Boolean.FALSE);
		setHidden(Boolean.FALSE);
		setEditable(Boolean.FALSE);
	}
	
	public FixedColumnModel(EntityOverlay overlay) {
		super(overlay);
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
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#getIdentifier()
	 */
	public String getIdentifier() {
		return get(FixedColumnKey.S_ID.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setIdentifier(java.lang.String)
	 */
	public void setIdentifier(String id) {
		set(FixedColumnKey.S_ID.name(), id);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#getName()
	 */
	public String getName() {
		return get(FixedColumnKey.S_NAME.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setName(java.lang.String)
	 */
	public void setName(String name) {
		set(FixedColumnKey.S_NAME.name(), name);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#getWidth()
	 */
	public Integer getWidth() {
		return get(FixedColumnKey.I_WIDTH.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setWidth(java.lang.Integer)
	 */
	public void setWidth(Integer width) {
		set(FixedColumnKey.I_WIDTH.name(), width);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setAssignmentId(java.lang.Long)
	 */
	public void setAssignmentId(Long assignmentId) {
		set(FixedColumnKey.L_ITEM_ID.name(), assignmentId);
	}
	

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#getAssignmentId()
	 */
	public Long getAssignmentId() {
		return getLong(FixedColumnKey.L_ITEM_ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#getMaxPoints()
	 */
	public Double getMaxPoints() {
		return get(FixedColumnKey.D_PNTS.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setMaxPoints(java.lang.Double)
	 */
	public void setMaxPoints(Double maxPoints) {
		set(FixedColumnKey.D_PNTS.name(), maxPoints);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#getKey()
	 */
	public String getKey() {
		return get(FixedColumnKey.O_LRNR_KEY.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setKey(java.lang.String)
	 */
	public void setKey(String key) {
		set(FixedColumnKey.O_LRNR_KEY.name(), key);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#getCategoryId()
	 */
	public Long getCategoryId() {
		return getLong(FixedColumnKey.L_CTGRY_ID.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setCategoryId(java.lang.Long)
	 */
	public void setCategoryId(Long categoryId) {
		set(FixedColumnKey.L_CTGRY_ID.name(), categoryId);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#getCategoryName()
	 */
	public String getCategoryName() {
		return get(FixedColumnKey.S_CTGRY_NM.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setCategoryName(java.lang.String)
	 */
	public void setCategoryName(String categoryName) {
		set(FixedColumnKey.S_CTGRY_NM.name(), categoryName);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#isUnweighted()
	 */
	public Boolean isUnweighted() {
		return get(FixedColumnKey.B_UNWGHTD.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setUnweighted(java.lang.Boolean)
	 */
	public void setUnweighted(Boolean isUnweighted) {
		set(FixedColumnKey.B_UNWGHTD.name(), isUnweighted);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#isHidden()
	 */
	public Boolean isHidden() {
		return get(FixedColumnKey.B_HDN.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setHidden(java.lang.Boolean)
	 */
	public void setHidden(Boolean isHidden) {
		set(FixedColumnKey.B_HDN.name(), isHidden);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#isEditable()
	 */
	public Boolean isEditable() {
		return get(FixedColumnKey.B_EDIT.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setEditable(java.lang.Boolean)
	 */
	public void setEditable(Boolean isEditable) {
		set(FixedColumnKey.B_EDIT.name(), isEditable);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#isExtraCredit()
	 */
	public Boolean isExtraCredit() {
		return get(FixedColumnKey.B_X_CRDT.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setExtraCredit(java.lang.Boolean)
	 */
	public void setExtraCredit(Boolean isExtraCredit) {
		set(FixedColumnKey.B_X_CRDT.name(), isExtraCredit);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#getDisplayName()
	 */
	public String getDisplayName() {
		return getName();
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#isChecked()
	 */
	public boolean isChecked() {
		return DataTypeConversionUtil.checkBoolean((Boolean)get(FixedColumnKey.B_CHCKD.name()));
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setChecked(boolean)
	 */
	public void setChecked(boolean isChecked) {
		set(FixedColumnKey.B_CHCKD.name(), Boolean.valueOf(isChecked));
	}

}
