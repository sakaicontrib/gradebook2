package org.sakaiproject.gradebook.gwt.client.model;

import java.util.Date;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class ItemModel extends BaseTreeModel {

	public enum Type { ROOT("Root"), GRADEBOOK("Gradebook") , CATEGORY("Category"), ITEM("Item");
	
		private String name;
		
		private Type(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}
	
	public enum Key {
		ID("Id"), NAME("Name"), WEIGHT("Weight"), EQUAL_WEIGHT("Equal Weight Items"), EXTRA_CREDIT("Extra Credit"), 
		INCLUDED("Include in Grade"), REMOVED("Delete"), GRADEBOOK("Gradebook"), DROP_LOWEST("Drop Lowest"), 
		CATEGORY_NAME("Category"), CATEGORY_ID("Category Id"), DUE_DATE("Due Date"), POINTS("Points"), 
		RELEASED("Is Released"), SOURCE("Source"), ITEM_TYPE("Type"), PERCENT_COURSE_GRADE("% Grade"),
		PERCENT_CATEGORY("% Category");
		
		private String propertyName;
		
		private Key(String propertyName) {
			this.propertyName = propertyName;
		}
		
		public String getPropertyName() {
			return propertyName;
		}
	};

	public ItemModel() {
		super();
	}

	public ItemModel(Map<String, Object> properties) {
		super(properties);
	}
	
	public String getDisplayName() {
		return get(Key.NAME.name());
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
	
	public String getItemType() {
		return get(Key.ITEM_TYPE.name());
	}

	public void setItemType(String type) {
		set(Key.ITEM_TYPE.name(), type);
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
	
	public Long getCategoryId() {
		return get(Key.CATEGORY_ID.name());
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
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ItemModel) {
			ItemModel other = (ItemModel) obj;

			if (getIdentifier() == null || other.getIdentifier() == null)
				return false;
			
			String s1 = new StringBuilder().append(getItemType()).append(":").append(getIdentifier()).toString();
			String s2 = new StringBuilder().append(other.getItemType()).append(":").append(other.getIdentifier()).toString();
			
			return s1.equals(s2);
		}
		return false;
	}
	
	 @Override
	 public int hashCode() {
		 String id = new StringBuilder().append(getItemType()).append(":").append(getIdentifier()).toString();
		 int hash = 0;
		 if (id != null) 
			 hash = id.hashCode();
		 return hash;
	 }
	
}
