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
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.GradeType;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class ItemModel extends BaseTreeModel<ItemModel> {

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
	
	public enum Key {
		ID("Id"), NAME("Name"), WEIGHT("Weight"), EQUAL_WEIGHT("Equal Weight Items"), 
		EXTRA_CREDIT("Extra Credit"), 
		INCLUDED("Include in Grade"), REMOVED("Delete"), GRADEBOOK("Gradebook"), 
		DROP_LOWEST("Drop Lowest"), 
		CATEGORY_NAME("Category"), CATEGORY_ID("Category Id"), DUE_DATE("Due Date"), 
		POINTS("Points"), 
		RELEASED("Release scores"), SOURCE("Source"), ITEM_TYPE("Type"), 
		PERCENT_COURSE_GRADE("% Grade"),
		PERCENT_CATEGORY("% Category"), IS_PERCENTAGE("Is Percentage"), 
		STUDENT_MODEL_KEY("Student Model Key"),
		ASSIGNMENT_ID("Item Id"), DATA_TYPE("Data Type"), CATEGORYTYPE("Organization"),
		GRADETYPE("Grade Type"), RELEASEGRADES("Display Course Grades"), 
		RELEASEITEMS("Display Released Items"),
		ITEM_ORDER("Order"), GRADESCALEID("grade scale id"), 
		EXTRA_CREDIT_SCALED("Project Extra Credit"),
		DO_RECALCULATE_POINTS("Scale scores to same percentage"),
		ENFORCE_POINT_WEIGHTING("Weight By Points");
		
		private String propertyName;
		
		private Key(String propertyName) {
			this.propertyName = propertyName;
		}
		
		public String getPropertyName() {
			return propertyName;
		}
	};

	private boolean isActive;
	private boolean isEditable;
	
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
		return get(Key.NAME.name());
	}
	
	public static ClassType lookupClassType(String property) {
		Key key = Key.valueOf(property);
		
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
	
	public static String getPropertyName(String property) {
		Key key = getProperty(property);
		
		return getPropertyName(key);
	}
	
	public static String getPropertyName(Key key) {
		if (key == null)
			return "";
		
		return key.getPropertyName();
	}

	public static Key getProperty(String key) {
		try {
			return Key.valueOf(key);
		} catch (IllegalArgumentException iae) {
			// Don't need to log this.
		}
		return null;
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

	public Double getWeighting() {
		return get(Key.WEIGHT.name());
	}

	public void setWeighting(Double weighting) {
		set(Key.WEIGHT.name(), weighting);
	}

	public Boolean getExtraCredit() {
		return get(Key.EXTRA_CREDIT.name());
	}

	public void setExtraCredit(Boolean extraCredit) {
		set(Key.EXTRA_CREDIT.name(), extraCredit);
	}

	public Boolean getIncluded() {
		return get(Key.INCLUDED.name());
	}

	public void setIncluded(Boolean included) {
		set(Key.INCLUDED.name(), included);
	}

	public Boolean getRemoved() {
		return get(Key.REMOVED.name());
	}

	public void setRemoved(Boolean removed) {
		set(Key.REMOVED.name(), removed);
	}
	
	public Type getItemType() {
		String typeName = get(Key.ITEM_TYPE.name());
		if (typeName == null)
			return null;
		return Type.valueOf(typeName);
	}

	public void setItemType(Type type) {
		set(Key.ITEM_TYPE.name(), type.name());
	}
	
	// Category specific
	public String getGradebook() {
		return get(Key.GRADEBOOK.name());
	}
	
	public void setGradebook(String gradebook) {
		set(Key.GRADEBOOK.name(), gradebook);
	}
	
	public Boolean getEqualWeightAssignments() {
		return get(Key.EQUAL_WEIGHT.name());
	}
	
	public void setEqualWeightAssignments(Boolean equalWeight) {
		set(Key.EQUAL_WEIGHT.name(), equalWeight);
	}
	
	public Integer getDropLowest() {
		return get(Key.DROP_LOWEST.name());
	}
	
	public void setDropLowest(Integer dropLowest) {
		set(Key.DROP_LOWEST.name(), dropLowest);
	}
	
	// Assignment specific
	public String getCategoryName() {
		return get(Key.CATEGORY_NAME.name());
	}
	
	public void setCategoryName(String categoryName) {
		set(Key.CATEGORY_NAME.name(), categoryName);
	}
	
	public Long getItemId() {
		return get(Key.ASSIGNMENT_ID.name());
	}
	
	public void setItemId(Long itemId) {
		set(Key.ASSIGNMENT_ID.name(), itemId);
	}
	
	public Long getCategoryId() {
		Object o = get(Key.CATEGORY_ID.name());
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
		set(Key.CATEGORY_ID.name(), categoryId);
	}
	
	public Double getPoints() {
		return get(Key.POINTS.name());
	}
	
	public void setPoints(Double points) {
		set(Key.POINTS.name(), points);
	}
	
	public Date getDueDate() {
		return get(Key.DUE_DATE.name());
	}
	
	public void setDueDate(Date dueDate) {
		set(Key.DUE_DATE.name(), dueDate);
	}
	
	public Boolean getReleased() {
		return get(Key.RELEASED.name());
	}
	
	public void setReleased(Boolean released) {
		set(Key.RELEASED.name(), released);
	}
	
	public String getSource() {
		return get(Key.SOURCE.name());
	}
	
	public void setSource(String source) {
		set(Key.SOURCE.name(), source);
	}
	
	public Double getPercentCourseGrade() {
		return get(Key.PERCENT_COURSE_GRADE.name());
	}
	
	public void setPercentCourseGrade(Double percent) {
		set(Key.PERCENT_COURSE_GRADE.name(), percent);
	}
	
	public Double getPercentCategory() {
		return get(Key.PERCENT_CATEGORY.name());
	}
	
	public void setPercentCategory(Double percent) {
		set(Key.PERCENT_CATEGORY.name(), percent);
	}
	
	public Boolean getIsPercentage() {
		return get(Key.IS_PERCENTAGE.name());
	}
	
	public void setIsPercentage(Boolean isPercentage) {
		set(Key.IS_PERCENTAGE.name(), isPercentage);
	}
	
	public String getStudentModelKey() {
		return get(Key.STUDENT_MODEL_KEY.name());
	}
	
	public void setStudentModelKey(String key) {
		set(Key.STUDENT_MODEL_KEY.name(), key);
	}
	
	public String getDataType() {
		return get(Key.DATA_TYPE.name());
	}
	
	public void setDataType(String dataType) {
		set(Key.DATA_TYPE.name(), dataType);
	}
	
	public CategoryType getCategoryType() {
		return get(Key.CATEGORYTYPE.name());
	}
	
	public void setCategoryType(CategoryType type) {
		set(Key.CATEGORYTYPE.name(), type);
	}
	
	public GradeType getGradeType() {
		return get(Key.GRADETYPE.name());
	}
	
	public void setGradeType(GradeType type) {
		set(Key.GRADETYPE.name(), type);
	}
	
	public Boolean getReleaseGrades() {
		return get(Key.RELEASEGRADES.name());
	}
	
	public void setReleaseGrades(Boolean release) {
		set(Key.RELEASEGRADES.name(), release);
	}
	
	public Boolean getReleaseItems() {
		return get(Key.RELEASEITEMS.name());
	}
	
	public void setReleaseItems(Boolean release) {
		set(Key.RELEASEITEMS.name(), release);
	}
	
	public Integer getItemOrder() {
		return get(Key.ITEM_ORDER.name());
	}
	
	public void setItemOrder(Integer itemOrder) {
		set(Key.ITEM_ORDER.name(), itemOrder);
	}
	
	public Long getGradeScaleId() {
		return get(Key.GRADESCALEID.name());
	}
	
	public void setGradeScaleId(Long id) {
		set(Key.GRADESCALEID.name(), id);
	}
	
	public Boolean getExtraCreditScaled() {
		return get(Key.EXTRA_CREDIT_SCALED.name());
	}
	
	public void setExtraCreditScaled(Boolean scaled) {
		set(Key.EXTRA_CREDIT_SCALED.name(), scaled);
	}
	
	public Boolean getDoRecalculatePoints() {
		return get(Key.DO_RECALCULATE_POINTS.name());
	}
	
	public void setDoRecalculatePoints(Boolean doRecalculate) {
		set(Key.DO_RECALCULATE_POINTS.name(), doRecalculate);
	}
	
	public Boolean getEnforcePointWeighting() {
		return get(Key.ENFORCE_POINT_WEIGHTING.name());
	}
	
	public void setEnforcePointWeighting(Boolean doEnforce) {
		set(Key.ENFORCE_POINT_WEIGHTING.name(), doEnforce);
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
	
}
