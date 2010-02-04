package org.sakaiproject.gradebook.gwt.sakai.rest.model;

import java.util.HashMap;

import org.sakaiproject.gradebook.gwt.client.model.FixedColumn;
import org.sakaiproject.gradebook.gwt.client.model.key.FixedColumnKey;
import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;
import org.sakaiproject.gradebook.gwt.sakai.Util;

public class FixedColumnImpl extends HashMap<String, Object> implements FixedColumn {

	private static final long serialVersionUID = 1L;

	public FixedColumnImpl() {
		super();
		setUnweighted(Boolean.FALSE);
		setHidden(Boolean.FALSE);
		setEditable(Boolean.FALSE);
	}
	
	public FixedColumnImpl(LearnerKey key, String displayName, Integer width, Boolean isHidden) {
		super();
		setIdentifier(key.name());
		setName(displayName);
		setKey(key.name());
		setWidth(width);
		setUnweighted(Boolean.FALSE);
		setHidden(isHidden);
		setEditable(Boolean.FALSE);
	}
	
	public FixedColumnImpl(Long assignmentId, String name, LearnerKey key, Integer width) {
		super();
		setIdentifier(String.valueOf(assignmentId));
		setName(name);
		setAssignmentId(assignmentId);
		setKey(key.name());
		setWidth(width);
		setUnweighted(Boolean.FALSE);
		setHidden(Boolean.FALSE);
		setEditable(Boolean.TRUE);
	}
	
	public <X> X get(String property) {
		return (X)super.get(property);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#getAssignmentId()
	 */
	public Long getAssignmentId() {
		return Util.toLong(get(FixedColumnKey.ASSIGNMENT_ID.name()));
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#getCategoryId()
	 */
	public Long getCategoryId() {
		return Util.toLong(get(FixedColumnKey.CATEGORY_ID.name()));
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#getCategoryName()
	 */
	public String getCategoryName() {
		return get(FixedColumnKey.CATEGORY_NAME.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#getDisplayName()
	 */
	public String getDisplayName() {
		return getName();
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#getIdentifier()
	 */
	public String getIdentifier() {
		return get(FixedColumnKey.ID.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#getKey()
	 */
	public String getKey() {
		return get(FixedColumnKey.STUDENT_MODEL_KEY.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#getMaxPoints()
	 */
	public Double getMaxPoints() {
		return Util.toDouble(get(FixedColumnKey.POINTS.name()));
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#getName()
	 */
	public String getName() {
		return get(FixedColumnKey.NAME.name());
	}
	

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#getWidth()
	 */
	public Integer getWidth() {
		return Util.toInteger(get(FixedColumnKey.WIDTH.name()));
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#isChecked()
	 */
	public boolean isChecked() {
		return Util.toBooleanPrimitive(get(FixedColumnKey.IS_CHECKED.name()));
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#isEditable()
	 */
	public Boolean isEditable() {
		return Util.toBoolean(get(FixedColumnKey.EDITABLE.name()));
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#isExtraCredit()
	 */
	public Boolean isExtraCredit() {
		return Util.toBoolean(get(FixedColumnKey.EXTRA_CREDIT.name()));
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#isHidden()
	 */
	public Boolean isHidden() {
		return Util.toBoolean(get(FixedColumnKey.HIDDEN.name()));
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#isUnweighted()
	 */
	public Boolean isUnweighted() {
		return Util.toBoolean(get(FixedColumnKey.UNWEIGHTED.name()));
	}

	public <X> X set(String property, X value) {
		return (X)put(property, value);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setAssignmentId(java.lang.Long)
	 */
	public void setAssignmentId(Long assignmentId) {
		set(FixedColumnKey.ASSIGNMENT_ID.name(), assignmentId);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setCategoryId(java.lang.Long)
	 */
	public void setCategoryId(Long categoryId) {
		set(FixedColumnKey.CATEGORY_ID.name(), categoryId);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setCategoryName(java.lang.String)
	 */
	public void setCategoryName(String categoryName) {
		set(FixedColumnKey.CATEGORY_NAME.name(), categoryName);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setChecked(boolean)
	 */
	public void setChecked(boolean isChecked) {
		set(FixedColumnKey.IS_CHECKED.name(), Boolean.valueOf(isChecked));
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setEditable(java.lang.Boolean)
	 */
	public void setEditable(Boolean isEditable) {
		set(FixedColumnKey.EDITABLE.name(), isEditable);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setExtraCredit(java.lang.Boolean)
	 */
	public void setExtraCredit(Boolean isExtraCredit) {
		set(FixedColumnKey.EXTRA_CREDIT.name(), isExtraCredit);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setHidden(java.lang.Boolean)
	 */
	public void setHidden(Boolean isHidden) {
		set(FixedColumnKey.HIDDEN.name(), isHidden);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setIdentifier(java.lang.String)
	 */
	public void setIdentifier(String id) {
		set(FixedColumnKey.ID.name(), id);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setKey(java.lang.String)
	 */
	public void setKey(String key) {
		set(FixedColumnKey.STUDENT_MODEL_KEY.name(), key);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setMaxPoints(java.lang.Double)
	 */
	public void setMaxPoints(Double maxPoints) {
		set(FixedColumnKey.POINTS.name(), maxPoints);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setName(java.lang.String)
	 */
	public void setName(String name) {
		set(FixedColumnKey.NAME.name(), name);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setUnweighted(java.lang.Boolean)
	 */
	public void setUnweighted(Boolean isUnweighted) {
		set(FixedColumnKey.UNWEIGHTED.name(), isUnweighted);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.FixedColumn#setWidth(java.lang.Integer)
	 */
	public void setWidth(Integer width) {
		set(FixedColumnKey.WIDTH.name(), width);
	}
	
}
