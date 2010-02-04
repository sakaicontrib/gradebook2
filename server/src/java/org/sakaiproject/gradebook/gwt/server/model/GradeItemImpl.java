package org.sakaiproject.gradebook.gwt.server.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.type.ItemType;
import org.sakaiproject.gradebook.gwt.sakai.Util;

public class GradeItemImpl extends BaseModel implements GradeItem {

	private static final long serialVersionUID = 1L;
	
	public GradeItemImpl() {
		super();
	}
	
	public GradeItemImpl(Map<String,Object> map) {
		super(map);
	}
	
	public GradeItemImpl(String id, String name, CategoryType categoryType, GradeType gradeType, Long gradeScaleId,
			Boolean doReleaseGrades, Boolean doReleaseItems, Boolean doScaleExtraCredit, Boolean doShowMean, Boolean doShowMedian,
			Boolean doShowMode, Boolean doShowRank, Boolean doShowItemStatistics) {
		
		put(ItemKey.ID.name(), id);
		put(ItemKey.NAME.name(), name);
		put(ItemKey.ITEM_TYPE.name(), ItemType.GRADEBOOK);
		put(ItemKey.CATEGORYTYPE.name(), categoryType);
		put(ItemKey.GRADETYPE.name(), gradeType);
		put(ItemKey.RELEASEGRADES.name(), doReleaseGrades);
		put(ItemKey.RELEASEITEMS.name(), doReleaseItems);
		put(ItemKey.GRADESCALEID.name(), gradeScaleId);
		put(ItemKey.EXTRA_CREDIT_SCALED.name(), doScaleExtraCredit);
		put(ItemKey.SHOWMEAN.name(), doShowMean);
		put(ItemKey.SHOWMEDIAN.name(), doShowMedian);
		put(ItemKey.SHOWMODE.name(), doShowMode);
		put(ItemKey.SHOWRANK.name(), doShowRank);
		put(ItemKey.SHOWITEMSTATS.name(), doShowItemStatistics);
	}
	
	public GradeItemImpl(String id, String name, String gradebookName, Long categoryId, Double percentCourseGrade,
			Integer dropLowest, Integer sortOrder, Boolean doEqualWeight, Boolean doExtraCredit, Boolean doInclude, Boolean doRemove,
			Boolean doRelease, Boolean isEditable, Boolean doPointWeighting) {

		put(ItemKey.ID.name(), id);
		put(ItemKey.NAME.name(), name);
		put(ItemKey.ITEM_TYPE.name(), ItemType.CATEGORY);
		put(ItemKey.GRADEBOOK.name(), gradebookName);
		put(ItemKey.CATEGORY_ID.name(), categoryId);
		put(ItemKey.WEIGHT.name(), percentCourseGrade);
		put(ItemKey.EQUAL_WEIGHT.name(), doEqualWeight);
		put(ItemKey.EXTRA_CREDIT.name(), doExtraCredit);
		put(ItemKey.INCLUDED.name(), doInclude);
		put(ItemKey.DROP_LOWEST.name(), dropLowest);
		put(ItemKey.REMOVED.name(), doRemove);
		put(ItemKey.RELEASED.name(), doRelease);
		put(ItemKey.PERCENT_COURSE_GRADE.name(), percentCourseGrade);	
		put(ItemKey.IS_EDITABLE.name(), isEditable);
		put(ItemKey.ITEM_ORDER.name(), sortOrder);
		put(ItemKey.ENFORCE_POINT_WEIGHTING.name(), doPointWeighting);
	}
	
	public GradeItemImpl(String id, String name, String categoryName, Long categoryId, Long itemId,
			Double points, Double percentCategory, Double percentCourseGrade, Double itemWeight, Boolean doRelease, Boolean doInclude,
			Date dueDate, Boolean doExtraCredit, Boolean doRemove, String source, String dataType, String learnerKey, Integer itemOrder, 
			Boolean doNullsAsZeros) {
		
		put(ItemKey.ID.name(), id);
		put(ItemKey.NAME.name(), name);
		put(ItemKey.ITEM_TYPE.name(), ItemType.ITEM);
		put(ItemKey.CATEGORY_NAME.name(), categoryName);
		put(ItemKey.CATEGORY_ID.name(), categoryId);
		put(ItemKey.ASSIGNMENT_ID.name(), itemId);
		put(ItemKey.WEIGHT.name(), itemWeight);
		put(ItemKey.RELEASED.name(), doRelease);
		put(ItemKey.INCLUDED.name(), doInclude);
		put(ItemKey.DUE_DATE.name(), dueDate);
		put(ItemKey.POINTS.name(), points);
		put(ItemKey.EXTRA_CREDIT.name(), doExtraCredit);
		put(ItemKey.REMOVED.name(), doRemove);
		put(ItemKey.SOURCE.name(), source);
		put(ItemKey.DATA_TYPE.name(), dataType);
		put(ItemKey.STUDENT_MODEL_KEY.name(), learnerKey);
		put(ItemKey.ITEM_ORDER.name(), itemOrder);
		put(ItemKey.PERCENT_CATEGORY.name(), percentCategory);
		put(ItemKey.PERCENT_COURSE_GRADE.name(), percentCourseGrade);
		put(ItemKey.NULLSASZEROS.name(), doNullsAsZeros);
	}
	
	public void addChild(GradeItem child) {

		assert(child != null);

		child.setParentName(getName());
		
		List<Map<String,Object>> childrenList = (List<Map<String,Object>>)get(ItemKey.CHILDREN.name());

		if (childrenList == null)
			childrenList = new ArrayList<Map<String,Object>>();

		if (!childrenList.contains(child))
			childrenList.add(child.getProperties());

		put(ItemKey.CHILDREN.name(), childrenList);
	}
	
	// Getters

	public Long getCategoryId() {
		return Util.toLong(get(ItemKey.CATEGORY_ID.name()));
	}

	public String getCategoryName() {
		return Util.toString(get(ItemKey.CATEGORY_NAME.name()));
	}

	public CategoryType getCategoryType() {
		return Util.toCategoryType(get(ItemKey.CATEGORYTYPE.name()));
	}
	
	public int getChildCount() {
		List<Map<String,Object>> childrenList = (List<Map<String,Object>>)get(ItemKey.CHILDREN.name());
		
		if (childrenList != null) {
			return childrenList.size();
		}
		
		return 0;
	}

	public List<GradeItem> getChildren() {
		List<GradeItem> children = new ArrayList<GradeItem>();
		List<Map<String,Object>> childrenList = (List<Map<String,Object>>)get(ItemKey.CHILDREN.name());
	
		if (childrenList != null) {
			for (Map<String,Object> childMap : childrenList) {
				children.add(new GradeItemImpl(childMap));
			}
		}
		
		return children;
	}

	public String getDataType() {
		return Util.toString(get(ItemKey.DATA_TYPE.name()));
	}

	public String getDisplayName() {
		return getName();
	}

	public Boolean getDoRecalculatePoints() {
		return Util.toBoolean(get(ItemKey.DO_RECALCULATE_POINTS.name()));
	}

	public Integer getDropLowest() {
		return Util.toInteger(get(ItemKey.DROP_LOWEST.name()));
	}

	public Date getDueDate() {
		return Util.toDate(get(ItemKey.DUE_DATE.name()));
	}

	public Boolean getEnforcePointWeighting() {
		return Util.toBoolean(get(ItemKey.ENFORCE_POINT_WEIGHTING.name()));
	}

	public Boolean getEqualWeightAssignments() {
		return Util.toBoolean(get(ItemKey.EQUAL_WEIGHT.name()));
	}

	public Boolean getExtraCredit() {
		return Util.toBoolean(get(ItemKey.EXTRA_CREDIT.name()));
	}

	public Boolean getExtraCreditScaled() {
		return Util.toBoolean(get(ItemKey.EXTRA_CREDIT_SCALED.name()));
	}

	public String getGradebook() {
		return Util.toString(get(ItemKey.GRADEBOOK.name()));
	}

	public Long getGradeScaleId() {
		return Util.toLong(get(ItemKey.GRADESCALEID.name()));
	}

	public GradeType getGradeType() {
		return Util.toGradeType(get(ItemKey.GRADETYPE.name()));
	}

	public String getIdentifier() {
		return Util.toString(get(ItemKey.ID.name()));
	}

	public Boolean getIncluded() {
		return Util.toBoolean(get(ItemKey.INCLUDED.name()));
	}

	public Boolean getIsPercentage() {
		return Util.toBoolean(get(ItemKey.IS_PERCENTAGE.name()));
	}

	public Long getItemId() {
		return Util.toLong(get(ItemKey.ASSIGNMENT_ID.name()));
	}

	public Integer getItemOrder() {
		return Util.toInteger(get(ItemKey.ITEM_ORDER.name()));
	}

	public ItemType getItemType() {
		return Util.toItemType(get(ItemKey.ITEM_TYPE.name()));
	}

	public Map<String, Object> getMap() {
		return this;
	}

	public String getName() {
		return Util.toString(get(ItemKey.NAME.name()));
	}

	public Boolean getNullsAsZeros() {
		return Util.toBoolean(get(ItemKey.NULLSASZEROS.name()));
	}

	public String getParentName() {
		return Util.toString(get(ItemKey.PARENT_NAME.name()));
	}

	public Double getPercentCategory() {
		return Util.toDouble(get(ItemKey.PERCENT_CATEGORY.name()));
	}

	public Double getPercentCourseGrade() {
		return Util.toDouble(get(ItemKey.PERCENT_COURSE_GRADE.name()));
	}

	public Double getPoints() {
		return Util.toDouble(get(ItemKey.POINTS.name()));
	}

	public Map<String, Object> getProperties() {
		return this;
	}

	public Collection<String> getPropertyNames() {
		return new ArrayList<String>(keySet());
	}

	public Boolean getReleased() {
		return Util.toBoolean(get(ItemKey.RELEASED.name()));
	}

	public Boolean getReleaseGrades() {
		return Util.toBoolean(get(ItemKey.RELEASEGRADES.name()));
	}

	public Boolean getReleaseItems() {
		return Util.toBoolean(get(ItemKey.RELEASEITEMS.name()));
	}

	public Boolean getRemoved() {
		return Util.toBoolean(get(ItemKey.REMOVED.name()));
	}

	public Boolean getShowItemStatistics() {
		return Util.toBoolean(get(ItemKey.SHOWITEMSTATS.name()));
	}

	public Boolean getShowMean() {
		return Util.toBoolean(get(ItemKey.SHOWMEAN.name()));
	}

	public Boolean getShowMedian() {
		return Util.toBoolean(get(ItemKey.SHOWMEDIAN.name()));
	}

	public Boolean getShowMode() {
		return Util.toBoolean(get(ItemKey.SHOWMODE.name()));
	}

	public Boolean getShowRank() {
		return Util.toBoolean(get(ItemKey.SHOWRANK.name()));
	}

	public String getSource() {
		return Util.toString(get(ItemKey.SOURCE.name()));
	}
	
	public String getStudentModelKey() {
		return Util.toString(get(ItemKey.STUDENT_MODEL_KEY.name()));
	}
	
	// Setters
	
	public Double getWeighting() {
		return Util.toDouble(get(ItemKey.WEIGHT.name()));
	}

	public boolean isActive() {
		return Util.toBooleanPrimitive(get(ItemKey.IS_ACTIVE.name()));
	}
	
	public boolean isChecked() {
		return Util.toBooleanPrimitive(get(ItemKey.IS_CHECKED.name()));
	}
	
	public boolean isEditable() {
		return Util.toBooleanPrimitive(get(ItemKey.IS_EDITABLE.name()));
	}

	/*public boolean isIncluded() {
		return Util.toBooleanPrimitive(get(ItemKey.INCLUDED.name()));
	}

	public boolean isRemoved() {
		return Util.toBooleanPrimitive(get(ItemKey.REMOVED.name()));
	}*/

	public void setActive(boolean isActive) {
		put(ItemKey.IS_ACTIVE.name(), isActive);
	}

	public void setCategoryId(Long categoryId) {
		put(ItemKey.CATEGORY_ID.name(), categoryId);
	}

	public void setCategoryName(String categoryName) {
		put(ItemKey.CATEGORY_NAME.name(), categoryName);
	}

	public void setCategoryType(CategoryType type) {
		put(ItemKey.CATEGORYTYPE.name(), type);
	}

	public void setChecked(boolean isChecked) {
		put(ItemKey.IS_CHECKED.name(), isChecked);
	}

	public void setDataType(String dataType) {
		put(ItemKey.DATA_TYPE.name(), dataType);
	}

	public void setDoRecalculatePoints(Boolean doRecalculate) {
		put(ItemKey.DO_RECALCULATE_POINTS.name(), doRecalculate);
	}

	public void setDropLowest(Integer dropLowest) {
		put(ItemKey.DROP_LOWEST.name(), dropLowest);
	}

	public void setDueDate(Date dueDate) {
		put(ItemKey.DUE_DATE.name(), dueDate);	}

	public void setEditable(boolean isEditable) {
		put(ItemKey.IS_EDITABLE.name(), Boolean.valueOf(isEditable));
	}

	public void setEnforcePointWeighting(Boolean doEnforce) {
		put(ItemKey.ENFORCE_POINT_WEIGHTING.name(), doEnforce);
	}

	public void setEqualWeightAssignments(Boolean equalWeight) {
		put(ItemKey.EQUAL_WEIGHT.name(), equalWeight);
	}

	public void setExtraCredit(Boolean extraCredit) {
		put(ItemKey.EXTRA_CREDIT.name(), extraCredit);
	}

	public void setExtraCreditScaled(Boolean scaled) {
		put(ItemKey.EXTRA_CREDIT_SCALED.name(), scaled);
	}

	public void setGradebook(String gradebook) {
		put(ItemKey.GRADEBOOK.name(), gradebook);
	}

	public void setGradeScaleId(Long id) {
		put(ItemKey.GRADESCALEID.name(), id);
	}

	public void setGradeType(GradeType type) {
		put(ItemKey.GRADETYPE.name(), type.name());
	}

	public void setIdentifier(String id) {
		put(ItemKey.ID.name(), id);
	}

	public void setIncluded(Boolean included) {
		put(ItemKey.INCLUDED.name(), included);
	}

	public void setIsPercentage(Boolean isPercentage) {
		put(ItemKey.IS_PERCENTAGE.name(), isPercentage);
	}

	public void setItemId(Long itemId) {
		put(ItemKey.ASSIGNMENT_ID.name(), itemId);
	}

	public void setItemOrder(Integer itemOrder) {
		put(ItemKey.ITEM_ORDER.name(), itemOrder);
	}

	public void setItemType(ItemType type) {
		put(ItemKey.ITEM_TYPE.name(), type.name());
	}

	public void setName(String name) {
		put(ItemKey.NAME.name(), name);
	}

	public void setNullsAsZeros(Boolean nullsAsZeros) {
		put(ItemKey.NULLSASZEROS.name(), nullsAsZeros);
	}

	public void setParentName(String parentName) {
		put(ItemKey.PARENT_NAME.name(), parentName);
	}

	public void setPercentCategory(Double percent) {
		put(ItemKey.PERCENT_CATEGORY.name(), percent);
	}

	public void setPercentCourseGrade(Double percent) {
		put(ItemKey.PERCENT_COURSE_GRADE.name(), percent);
	}

	public void setPoints(Double points) {
		put(ItemKey.POINTS.name(), points);
	}

	public void setReleased(Boolean released) {
		put(ItemKey.RELEASED.name(), released);
	}

	public void setReleaseGrades(Boolean release) {
		put(ItemKey.RELEASEGRADES.name(), release);
	}

	public void setReleaseItems(Boolean release) {
		put(ItemKey.RELEASEITEMS.name(), release);
	}

	public void setRemoved(Boolean removed) {
		put(ItemKey.REMOVED.name(), removed);
	}

	public void setShowItemStatistics(Boolean showItemStatistics) {
		put(ItemKey.SHOWITEMSTATS.name(), showItemStatistics);
	}

	public void setShowMean(Boolean showMean) {
		put(ItemKey.SHOWMEAN.name(), showMean);
	}

	public void setShowMedian(Boolean showMedian) {
		put(ItemKey.SHOWMEDIAN.name(), showMedian);
	}

	public void setShowMode(Boolean showMode) {
		put(ItemKey.SHOWMODE.name(), showMode);
	}

	public void setShowRank(Boolean showRank) {
		put(ItemKey.SHOWRANK.name(), showRank);
	}

	public void setSource(String source) {
		put(ItemKey.SOURCE.name(), source);
	}

	public void setStudentModelKey(String key) {
		put(ItemKey.STUDENT_MODEL_KEY.name(), key);
	}
	
	public void setWeighting(Double weighting) {
		put(ItemKey.WEIGHT.name(), weighting);
	}
	
}
