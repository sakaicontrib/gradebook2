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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.ClassType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.type.ItemType;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;

public class ItemModel extends BaseTreeModel implements Item {

	private static final long serialVersionUID = 1L;
	
	public ItemModel() {
		super();
		setActive(false);
		setEditable(true);
	}

	public ItemModel(Map<String, Object> properties) {
		super(properties);
		setActive(false);
		setEditable(true);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getDisplayName()
	 */
	public String getDisplayName() {
		return get(ItemKey.NAME.name());
	}
	
	public static ClassType lookupClassType(String property) {
		ItemKey key = ItemKey.valueOf(property);
		
		switch (key) {
		case ID: case NAME: case GRADEBOOK: case CATEGORY_NAME: case SOURCE: case ITEM_TYPE:
		case STUDENT_MODEL_KEY: case DATA_TYPE:
			return ClassType.STRING;
		case WEIGHT: case POINTS: case PERCENT_COURSE_GRADE: case PERCENT_CATEGORY:
			return ClassType.DOUBLE;
		case EQUAL_WEIGHT: case EXTRA_CREDIT: case INCLUDED: case REMOVED: case RELEASED:
		case IS_PERCENTAGE: case RELEASEGRADES:
			return ClassType.BOOLEAN;
		case DROP_LOWEST:
			return ClassType.INTEGER;
		case CATEGORY_ID: case ASSIGNMENT_ID:
			return ClassType.LONG;
		case DUE_DATE:
			return ClassType.DATE;
		case CATEGORYTYPE:
			return ClassType.CATEGORYTYPE;
		case GRADETYPE:
			return ClassType.GRADETYPE;
		}
		
		return null;
	}

	public static ItemKey getProperty(String key) {
		try {
			return ItemKey.valueOf(key);
		} catch (IllegalArgumentException iae) {
			// Don't need to log this.
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getIdentifier()
	 */
	public String getIdentifier() {
		return get(ItemKey.ID.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setIdentifier(java.lang.String)
	 */
	public void setIdentifier(String id) {
		set(ItemKey.ID.name(), id);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getName()
	 */
	public String getName() {
		return get(ItemKey.NAME.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setName(java.lang.String)
	 */
	public void setName(String name) {
		set(ItemKey.NAME.name(), name);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getWeighting()
	 */
	public Double getWeighting() {
		return get(ItemKey.WEIGHT.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setWeighting(java.lang.Double)
	 */
	public void setWeighting(Double weighting) {
		set(ItemKey.WEIGHT.name(), weighting);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getExtraCredit()
	 */
	public Boolean getExtraCredit() {
		return get(ItemKey.EXTRA_CREDIT.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setExtraCredit(java.lang.Boolean)
	 */
	public void setExtraCredit(Boolean extraCredit) {
		set(ItemKey.EXTRA_CREDIT.name(), extraCredit);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getIncluded()
	 */
	public Boolean getIncluded() {
		return get(ItemKey.INCLUDED.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setIncluded(java.lang.Boolean)
	 */
	public void setIncluded(Boolean included) {
		set(ItemKey.INCLUDED.name(), included);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getRemoved()
	 */
	public Boolean getRemoved() {
		return get(ItemKey.REMOVED.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setRemoved(java.lang.Boolean)
	 */
	public void setRemoved(Boolean removed) {
		set(ItemKey.REMOVED.name(), removed);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getItemType()
	 */
	public ItemType getItemType() {
		String typeName = get(ItemKey.ITEM_TYPE.name());
		if (typeName == null)
			return null;
		return ItemType.valueOf(typeName);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setItemType(org.sakaiproject.gradebook.gwt.client.model.ItemType)
	 */
	public void setItemType(ItemType type) {
		set(ItemKey.ITEM_TYPE.name(), type.name());
	}
	
	// Category specific
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getGradebook()
	 */
	public String getGradebook() {
		return get(ItemKey.GRADEBOOK.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setGradebook(java.lang.String)
	 */
	public void setGradebook(String gradebook) {
		set(ItemKey.GRADEBOOK.name(), gradebook);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getEqualWeightAssignments()
	 */
	public Boolean getEqualWeightAssignments() {
		return get(ItemKey.EQUAL_WEIGHT.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setEqualWeightAssignments(java.lang.Boolean)
	 */
	public void setEqualWeightAssignments(Boolean equalWeight) {
		set(ItemKey.EQUAL_WEIGHT.name(), equalWeight);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getDropLowest()
	 */
	public Integer getDropLowest() {
		return get(ItemKey.DROP_LOWEST.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setDropLowest(java.lang.Integer)
	 */
	public void setDropLowest(Integer dropLowest) {
		set(ItemKey.DROP_LOWEST.name(), dropLowest);
	}
	
	// Assignment specific
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getCategoryName()
	 */
	public String getCategoryName() {
		return get(ItemKey.CATEGORY_NAME.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setCategoryName(java.lang.String)
	 */
	public void setCategoryName(String categoryName) {
		set(ItemKey.CATEGORY_NAME.name(), categoryName);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getItemId()
	 */
	public Long getItemId() {
		return get(ItemKey.ASSIGNMENT_ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setItemId(java.lang.Long)
	 */
	public void setItemId(Long itemId) {
		set(ItemKey.ASSIGNMENT_ID.name(), itemId);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getCategoryId()
	 */
	public Long getCategoryId() {
		Object o = get(ItemKey.CATEGORY_ID.name());
		/* 
		 * This hack exists because for some odd reason, the category ID is sometimes a string.  Maybe bad serialization? 
		 */
		if (null == o) 
		{
			return new Long(-1); 
		}
		else if (o instanceof Long)
		{
			return (Long) o; 
		}
		else if (o instanceof Integer) 
		{
			return Long.valueOf(((Integer)o).intValue());
		}
		else
		{
			Long ret = null; 
			try {
				ret= Long.decode((String) o);
			} catch (NumberFormatException e) {
				ret = Long.valueOf(-1); 
			}
			return ret; 
		}
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setCategoryId(java.lang.Long)
	 */
	public void setCategoryId(Long categoryId) {
		set(ItemKey.CATEGORY_ID.name(), categoryId);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getPoints()
	 */
	public Double getPoints() {
		return get(ItemKey.POINTS.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setPoints(java.lang.Double)
	 */
	public void setPoints(Double points) {
		set(ItemKey.POINTS.name(), points);
		set(ItemKey.POINTS_STRING.name(), String.valueOf(points));
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getDueDate()
	 */
	public Date getDueDate() {
		Object obj = get(ItemKey.DUE_DATE.name());
		
		if (obj instanceof Long)
			return new Date((Long)obj);
		
		return (Date)obj;
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setDueDate(java.util.Date)
	 */
	public void setDueDate(Date dueDate) {
		set(ItemKey.DUE_DATE.name(), dueDate);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getReleased()
	 */
	public Boolean getReleased() {
		return get(ItemKey.RELEASED.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setReleased(java.lang.Boolean)
	 */
	public void setReleased(Boolean released) {
		set(ItemKey.RELEASED.name(), released);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getNullsAsZeros()
	 */
	public Boolean getNullsAsZeros() {
		return get(ItemKey.NULLSASZEROS.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setNullsAsZeros(java.lang.Boolean)
	 */
	public void setNullsAsZeros(Boolean nullsAsZeros) {
		set(ItemKey.NULLSASZEROS.name(), nullsAsZeros);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getSource()
	 */
	public String getSource() {
		return get(ItemKey.SOURCE.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setSource(java.lang.String)
	 */
	public void setSource(String source) {
		set(ItemKey.SOURCE.name(), source);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getPercentCourseGrade()
	 */
	public Double getPercentCourseGrade() {
		return get(ItemKey.PERCENT_COURSE_GRADE.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setPercentCourseGrade(java.lang.Double)
	 */
	public void setPercentCourseGrade(Double percent) {
		set(ItemKey.PERCENT_COURSE_GRADE.name(), percent);
		set(ItemKey.PERCENT_COURSE_GRADE_STRING.name(), String.valueOf(percent));
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getPercentCategory()
	 */
	public Double getPercentCategory() {
		return get(ItemKey.PERCENT_CATEGORY.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setPercentCategory(java.lang.Double)
	 */
	public void setPercentCategory(Double percent) {
		set(ItemKey.PERCENT_CATEGORY.name(), percent);
		set(ItemKey.PERCENT_CATEGORY_STRING.name(), String.valueOf(percent));
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getIsPercentage()
	 */
	public Boolean getIsPercentage() {
		return get(ItemKey.IS_PERCENTAGE.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setIsPercentage(java.lang.Boolean)
	 */
	public void setIsPercentage(Boolean isPercentage) {
		set(ItemKey.IS_PERCENTAGE.name(), isPercentage);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getStudentModelKey()
	 */
	public String getStudentModelKey() {
		return get(ItemKey.STUDENT_MODEL_KEY.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setStudentModelKey(java.lang.String)
	 */
	public void setStudentModelKey(String key) {
		set(ItemKey.STUDENT_MODEL_KEY.name(), key);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getDataType()
	 */
	public String getDataType() {
		return get(ItemKey.DATA_TYPE.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setDataType(java.lang.String)
	 */
	public void setDataType(String dataType) {
		set(ItemKey.DATA_TYPE.name(), dataType);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getCategoryType()
	 */
	public CategoryType getCategoryType() {
		Object obj = get(ItemKey.CATEGORYTYPE.name());
		
		if (obj == null)
			return null;
		
		if (obj instanceof CategoryType)
			return (CategoryType)obj;
		
		return CategoryType.valueOf((String)obj);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setCategoryType(org.sakaiproject.gradebook.gwt.client.model.CategoryType)
	 */
	public void setCategoryType(CategoryType type) {
		set(ItemKey.CATEGORYTYPE.name(), type);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getGradeType()
	 */
	public GradeType getGradeType() {
		Object obj = get(ItemKey.GRADETYPE.name());
		
		if (obj == null)
			return null;
		
		if (obj instanceof GradeType)
			return (GradeType)obj;
		
		return GradeType.valueOf((String)obj);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setGradeType(org.sakaiproject.gradebook.gwt.client.model.GradeType)
	 */
	public void setGradeType(GradeType type) {
		set(ItemKey.GRADETYPE.name(), type);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getReleaseGrades()
	 */
	public Boolean getReleaseGrades() {
		return get(ItemKey.RELEASEGRADES.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setReleaseGrades(java.lang.Boolean)
	 */
	public void setReleaseGrades(Boolean release) {
		set(ItemKey.RELEASEGRADES.name(), release);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getReleaseItems()
	 */
	public Boolean getReleaseItems() {
		return get(ItemKey.RELEASEITEMS.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setReleaseItems(java.lang.Boolean)
	 */
	public void setReleaseItems(Boolean release) {
		set(ItemKey.RELEASEITEMS.name(), release);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getItemOrder()
	 */
	public Integer getItemOrder() {
		return get(ItemKey.ITEM_ORDER.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setItemOrder(java.lang.Integer)
	 */
	public void setItemOrder(Integer itemOrder) {
		set(ItemKey.ITEM_ORDER.name(), itemOrder);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getGradeScaleId()
	 */
	public Long getGradeScaleId() {
		return get(ItemKey.GRADESCALEID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setGradeScaleId(java.lang.Long)
	 */
	public void setGradeScaleId(Long id) {
		set(ItemKey.GRADESCALEID.name(), id);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getExtraCreditScaled()
	 */
	public Boolean getExtraCreditScaled() {
		return get(ItemKey.EXTRA_CREDIT_SCALED.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setExtraCreditScaled(java.lang.Boolean)
	 */
	public void setExtraCreditScaled(Boolean scaled) {
		set(ItemKey.EXTRA_CREDIT_SCALED.name(), scaled);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getDoRecalculatePoints()
	 */
	public Boolean getDoRecalculatePoints() {
		return get(ItemKey.DO_RECALCULATE_POINTS.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setDoRecalculatePoints(java.lang.Boolean)
	 */
	public void setDoRecalculatePoints(Boolean doRecalculate) {
		set(ItemKey.DO_RECALCULATE_POINTS.name(), doRecalculate);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getEnforcePointWeighting()
	 */
	public Boolean getEnforcePointWeighting() {
		return get(ItemKey.ENFORCE_POINT_WEIGHTING.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setEnforcePointWeighting(java.lang.Boolean)
	 */
	public void setEnforcePointWeighting(Boolean doEnforce) {
		set(ItemKey.ENFORCE_POINT_WEIGHTING.name(), doEnforce);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getShowMean()
	 */
	public Boolean getShowMean() {
		return get(ItemKey.SHOWMEAN.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setShowMean(java.lang.Boolean)
	 */
	public void setShowMean(Boolean showMean) {
		set(ItemKey.SHOWMEAN.name(), showMean);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getShowMedian()
	 */
	public Boolean getShowMedian() {
		return get(ItemKey.SHOWMEDIAN.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setShowMedian(java.lang.Boolean)
	 */
	public void setShowMedian(Boolean showMedian) {
		set(ItemKey.SHOWMEDIAN.name(), showMedian);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getShowMode()
	 */
	public Boolean getShowMode() {
		return get(ItemKey.SHOWMODE.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setShowMode(java.lang.Boolean)
	 */
	public void setShowMode(Boolean showMode) {
		set(ItemKey.SHOWMODE.name(), showMode);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getShowRank()
	 */
	public Boolean getShowRank() {
		return get(ItemKey.SHOWRANK.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setShowRank(java.lang.Boolean)
	 */
	public void setShowRank(Boolean showRank) {
		set(ItemKey.SHOWRANK.name(), showRank);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getShowItemStatistics()
	 */
	public Boolean getShowItemStatistics() {
		return get(ItemKey.SHOWITEMSTATS.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setShowItemStatistics(java.lang.Boolean)
	 */
	public void setShowItemStatistics(Boolean showItemStatistics) {
		set(ItemKey.SHOWITEMSTATS.name(), showItemStatistics);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ItemModel) {
			Item other = (Item) obj;

			if (getIdentifier() == null || other.getIdentifier() == null)
				return false;
			
			String s1 = new StringBuilder().append(getItemType().name()).append(":").append(getIdentifier()).toString();
			String s2 = new StringBuilder().append(other.getItemType().name()).append(":").append(other.getIdentifier()).toString();
			
			return s1.equals(s2);
		}
		return false;
	}
	
	 @Override
	 public int hashCode() {
		 String id = new StringBuilder().append(getItemType().name()).append(":").append(getIdentifier()).toString();
		 int hash = 0;
		 if (id != null) 
			 hash = id.hashCode();
		 return hash;
	 }

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#isActive()
	 */
	public boolean isActive() {
		Boolean active = get(ItemKey.IS_ACTIVE.name());
		return active == null ? false : active.booleanValue();
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setActive(boolean)
	 */
	public void setActive(boolean isActive) {
		set(ItemKey.IS_ACTIVE.name(), Boolean.valueOf(isActive));
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#isEditable()
	 */
	public boolean isEditable() {
		Boolean active = get(ItemKey.IS_EDITABLE.name());
		return active == null ? true : active.booleanValue();
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setEditable(boolean)
	 */
	public void setEditable(boolean isEditable) {
		set(ItemKey.IS_EDITABLE.name(), Boolean.valueOf(isEditable));
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#isChecked()
	 */
	public boolean isChecked() {
		Boolean active = get(ItemKey.IS_CHECKED.name());
		return active == null ? false : active.booleanValue();
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setChecked(boolean)
	 */
	public void setChecked(boolean isChecked) {
		set(ItemKey.IS_CHECKED.name(), Boolean.valueOf(isChecked));
	}

	public void addChild(Item child) {
		add((ModelData)child);
	}

	public boolean isIncluded() {
		return DataTypeConversionUtil.checkBoolean(getIncluded());
	}

	public boolean isRemoved() {
		return DataTypeConversionUtil.checkBoolean(getRemoved());
	}

	public List<Item> getSubItems() {
		List<ModelData> children = getChildren();
		List<Item> subItems = new ArrayList<Item>();
		if (children != null) {
			for (int i=0;i<children.size();i++) {
				subItems.add((ItemModel)children.get(i));
			}
		}
		return subItems;
	}

	public Integer getSubItemCount() {
		List<?> children = getChildren();
		return children == null ? Integer.valueOf(0) : children.size();
	}
	
}
