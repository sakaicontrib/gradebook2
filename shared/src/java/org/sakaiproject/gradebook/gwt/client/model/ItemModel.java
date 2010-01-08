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

import java.util.Date;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction.ClassType;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class ItemModel extends BaseTreeModel {

	private static final long serialVersionUID = 1L;

	public enum Type { ROOT("Root"), GRADEBOOK("Gradebook") , CATEGORY("Category"), ITEM("Item"), COMMENT("Comment");
	
		private String name;
		
		private Type(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}
	
	private boolean isActive;
	private boolean isEditable;
	private boolean isChecked;
	
	public ItemModel() {
		super();
		this.isActive = false;
		this.isEditable = true;
	}

	public ItemModel(Map<String, Object> properties) {
		super(properties);
		this.isActive = false;
		this.isEditable = true;
	}
	
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
	
	public String getIdentifier() {
		return get(ItemKey.ID.name());
	}

	public void setIdentifier(String id) {
		set(ItemKey.ID.name(), id);
	}

	public String getName() {
		return get(ItemKey.NAME.name());
	}

	public void setName(String name) {
		set(ItemKey.NAME.name(), name);
	}

	public Double getWeighting() {
		return get(ItemKey.WEIGHT.name());
	}

	public void setWeighting(Double weighting) {
		set(ItemKey.WEIGHT.name(), weighting);
	}

	public Boolean getExtraCredit() {
		return get(ItemKey.EXTRA_CREDIT.name());
	}

	public void setExtraCredit(Boolean extraCredit) {
		set(ItemKey.EXTRA_CREDIT.name(), extraCredit);
	}

	public Boolean getIncluded() {
		return get(ItemKey.INCLUDED.name());
	}

	public void setIncluded(Boolean included) {
		set(ItemKey.INCLUDED.name(), included);
	}

	public Boolean getRemoved() {
		return get(ItemKey.REMOVED.name());
	}

	public void setRemoved(Boolean removed) {
		set(ItemKey.REMOVED.name(), removed);
	}
	
	public Type getItemType() {
		String typeName = get(ItemKey.ITEM_TYPE.name());
		if (typeName == null)
			return null;
		return Type.valueOf(typeName);
	}

	public void setItemType(Type type) {
		set(ItemKey.ITEM_TYPE.name(), type.name());
	}
	
	// Category specific
	public String getGradebook() {
		return get(ItemKey.GRADEBOOK.name());
	}
	
	public void setGradebook(String gradebook) {
		set(ItemKey.GRADEBOOK.name(), gradebook);
	}
	
	public Boolean getEqualWeightAssignments() {
		return get(ItemKey.EQUAL_WEIGHT.name());
	}
	
	public void setEqualWeightAssignments(Boolean equalWeight) {
		set(ItemKey.EQUAL_WEIGHT.name(), equalWeight);
	}
	
	public Integer getDropLowest() {
		return get(ItemKey.DROP_LOWEST.name());
	}
	
	public void setDropLowest(Integer dropLowest) {
		set(ItemKey.DROP_LOWEST.name(), dropLowest);
	}
	
	// Assignment specific
	public String getCategoryName() {
		return get(ItemKey.CATEGORY_NAME.name());
	}
	
	public void setCategoryName(String categoryName) {
		set(ItemKey.CATEGORY_NAME.name(), categoryName);
	}
	
	public Long getItemId() {
		return get(ItemKey.ASSIGNMENT_ID.name());
	}
	
	public void setItemId(Long itemId) {
		set(ItemKey.ASSIGNMENT_ID.name(), itemId);
	}
	
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
	
	public void setCategoryId(Long categoryId) {
		set(ItemKey.CATEGORY_ID.name(), categoryId);
	}
	
	public Double getPoints() {
		return get(ItemKey.POINTS.name());
	}
	
	public void setPoints(Double points) {
		set(ItemKey.POINTS.name(), points);
		set(ItemKey.POINTS_STRING.name(), String.valueOf(points));
	}
	
	public Date getDueDate() {
		return get(ItemKey.DUE_DATE.name());
	}
	
	public void setDueDate(Date dueDate) {
		set(ItemKey.DUE_DATE.name(), dueDate);
	}
	
	public Boolean getReleased() {
		return get(ItemKey.RELEASED.name());
	}
	
	public void setReleased(Boolean released) {
		set(ItemKey.RELEASED.name(), released);
	}
	
	public Boolean getNullsAsZeros() {
		return get(ItemKey.NULLSASZEROS.name());
	}
	
	public void setNullsAsZeros(Boolean nullsAsZeros) {
		set(ItemKey.NULLSASZEROS.name(), nullsAsZeros);
	}
	
	public String getSource() {
		return get(ItemKey.SOURCE.name());
	}
	
	public void setSource(String source) {
		set(ItemKey.SOURCE.name(), source);
	}
	
	public Double getPercentCourseGrade() {
		return get(ItemKey.PERCENT_COURSE_GRADE.name());
	}
	
	public void setPercentCourseGrade(Double percent) {
		set(ItemKey.PERCENT_COURSE_GRADE.name(), percent);
		set(ItemKey.PERCENT_COURSE_GRADE_STRING.name(), String.valueOf(percent));
	}
	
	public Double getPercentCategory() {
		return get(ItemKey.PERCENT_CATEGORY.name());
	}
	
	public void setPercentCategory(Double percent) {
		set(ItemKey.PERCENT_CATEGORY.name(), percent);
		set(ItemKey.PERCENT_CATEGORY_STRING.name(), String.valueOf(percent));
	}
	
	public Boolean getIsPercentage() {
		return get(ItemKey.IS_PERCENTAGE.name());
	}
	
	public void setIsPercentage(Boolean isPercentage) {
		set(ItemKey.IS_PERCENTAGE.name(), isPercentage);
	}
	
	public String getStudentModelKey() {
		return get(ItemKey.STUDENT_MODEL_KEY.name());
	}
	
	public void setStudentModelKey(String key) {
		set(ItemKey.STUDENT_MODEL_KEY.name(), key);
	}
	
	public String getDataType() {
		return get(ItemKey.DATA_TYPE.name());
	}
	
	public void setDataType(String dataType) {
		set(ItemKey.DATA_TYPE.name(), dataType);
	}
	
	public CategoryType getCategoryType() {
		Object obj = get(ItemKey.CATEGORYTYPE.name());
		
		if (obj == null)
			return null;
		
		if (obj instanceof CategoryType)
			return (CategoryType)obj;
		
		return CategoryType.valueOf((String)obj);
	}
	
	public void setCategoryType(CategoryType type) {
		set(ItemKey.CATEGORYTYPE.name(), type);
	}
	
	public GradeType getGradeType() {
		Object obj = get(ItemKey.GRADETYPE.name());
		
		if (obj == null)
			return null;
		
		if (obj instanceof GradeType)
			return (GradeType)obj;
		
		return GradeType.valueOf((String)obj);
	}
	
	public void setGradeType(GradeType type) {
		set(ItemKey.GRADETYPE.name(), type);
	}
	
	public Boolean getReleaseGrades() {
		return get(ItemKey.RELEASEGRADES.name());
	}
	
	public void setReleaseGrades(Boolean release) {
		set(ItemKey.RELEASEGRADES.name(), release);
	}
	
	public Boolean getReleaseItems() {
		return get(ItemKey.RELEASEITEMS.name());
	}
	
	public void setReleaseItems(Boolean release) {
		set(ItemKey.RELEASEITEMS.name(), release);
	}
	
	public Integer getItemOrder() {
		return get(ItemKey.ITEM_ORDER.name());
	}
	
	public void setItemOrder(Integer itemOrder) {
		set(ItemKey.ITEM_ORDER.name(), itemOrder);
	}
	
	public Long getGradeScaleId() {
		return get(ItemKey.GRADESCALEID.name());
	}
	
	public void setGradeScaleId(Long id) {
		set(ItemKey.GRADESCALEID.name(), id);
	}
	
	public Boolean getExtraCreditScaled() {
		return get(ItemKey.EXTRA_CREDIT_SCALED.name());
	}
	
	public void setExtraCreditScaled(Boolean scaled) {
		set(ItemKey.EXTRA_CREDIT_SCALED.name(), scaled);
	}
	
	public Boolean getDoRecalculatePoints() {
		return get(ItemKey.DO_RECALCULATE_POINTS.name());
	}
	
	public void setDoRecalculatePoints(Boolean doRecalculate) {
		set(ItemKey.DO_RECALCULATE_POINTS.name(), doRecalculate);
	}
	
	public Boolean getEnforcePointWeighting() {
		return get(ItemKey.ENFORCE_POINT_WEIGHTING.name());
	}
	
	public void setEnforcePointWeighting(Boolean doEnforce) {
		set(ItemKey.ENFORCE_POINT_WEIGHTING.name(), doEnforce);
	}
	
	public Boolean getShowMean() {
		return get(ItemKey.SHOWMEAN.name());
	}
	
	public void setShowMean(Boolean showMean) {
		set(ItemKey.SHOWMEAN.name(), showMean);
	}
	
	public Boolean getShowMedian() {
		return get(ItemKey.SHOWMEDIAN.name());
	}
	
	public void setShowMedian(Boolean showMedian) {
		set(ItemKey.SHOWMEDIAN.name(), showMedian);
	}
	
	public Boolean getShowMode() {
		return get(ItemKey.SHOWMODE.name());
	}
	
	public void setShowMode(Boolean showMode) {
		set(ItemKey.SHOWMODE.name(), showMode);
	}
	
	public Boolean getShowRank() {
		return get(ItemKey.SHOWRANK.name());
	}
	
	public void setShowRank(Boolean showRank) {
		set(ItemKey.SHOWRANK.name(), showRank);
	}
	
	public Boolean getShowItemStatistics() {
		return get(ItemKey.SHOWITEMSTATS.name());
	}
	
	public void setShowItemStatistics(Boolean showItemStatistics) {
		set(ItemKey.SHOWITEMSTATS.name(), showItemStatistics);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ItemModel) {
			ItemModel other = (ItemModel) obj;

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

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
}
