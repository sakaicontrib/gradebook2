package org.sakaiproject.gradebook.gwt.client.model;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.BusinessLogicCode;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.type.ItemType;

public interface Item {

	public abstract <X> X get(String property);
	
	public abstract Long getCategoryId();

	public abstract String getCategoryName();

	public abstract CategoryType getCategoryType();

	public abstract String getDataType();

	public abstract Boolean getDoRecalculatePoints();

	public abstract Integer getDropLowest();

	public abstract Date getDueDate() throws InvalidInputException;

	public abstract Boolean getEnforcePointWeighting();

	public abstract Boolean getEqualWeightAssignments();

	public abstract Boolean getExtraCredit();

	public abstract Boolean getExtraCreditScaled();

	public abstract String getGradebook();

	public abstract Long getGradeScaleId();

	public abstract GradeType getGradeType();

	public abstract String getIdentifier();

	public abstract Boolean getIncluded();

	public abstract Boolean getIsPercentage();

	public abstract Long getItemId();

	public abstract Integer getItemOrder();

	public abstract ItemType getItemType();

	public abstract String getName();

	public abstract Boolean getNullsAsZeros();

	public abstract Double getPercentCategory();

	public abstract Double getPercentCourseGrade();

	public abstract Double getPoints();

	public abstract Map<String, Object> getProperties();

	public abstract Collection<String> getPropertyNames();

	public abstract Boolean getReleased();

	public abstract Boolean getReleaseGrades();

	public abstract Boolean getReleaseItems();

	public abstract Boolean getRemoved();

	public abstract Boolean getShowItemStatistics();
	
	public abstract Boolean getShowStatisticsChart();

	public abstract Boolean getShowMean();

	public abstract Boolean getShowMedian();

	public abstract Boolean getShowMode();

	public abstract Boolean getShowRank();

	public abstract String getSource();

	public abstract String getStudentModelKey();
	
	public abstract List<Item> getSubItems();

	public abstract Integer getSubItemCount();
	
	public abstract Double getWeighting();

	public abstract boolean isActive();

	public abstract boolean isChecked();

	public abstract boolean isEditable();

	public abstract void setActive(boolean isActive);

	public abstract void setCategoryId(Long categoryId);

	public abstract void setCategoryName(String categoryName);

	public abstract void setCategoryType(CategoryType type);

	public abstract void setChecked(boolean isChecked);

	public abstract void setDataType(String dataType);

	public abstract void setDoRecalculatePoints(Boolean doRecalculate);

	public abstract void setDropLowest(Integer dropLowest);

	public abstract void setDueDate(Date dueDate);

	public abstract void setEditable(boolean isEditable);

	public abstract void setEnforcePointWeighting(Boolean doEnforce);

	public abstract void setEqualWeightAssignments(Boolean equalWeight);

	public abstract void setExtraCredit(Boolean extraCredit);

	public abstract void setExtraCreditScaled(Boolean scaled);

	public abstract void setGradebook(String gradebook);

	public abstract void setGradeScaleId(Long id);

	public abstract void setGradeType(GradeType type);

	public abstract void setIdentifier(String id);

	public abstract void setIncluded(Boolean included);

	public abstract void setIsPercentage(Boolean isPercentage);

	public abstract void setItemId(Long itemId);

	public abstract void setItemOrder(Integer itemOrder);

	public abstract void setItemType(ItemType type);

	public abstract void setName(String name);

	public abstract void setNullsAsZeros(Boolean nullsAsZeros);

	public abstract void setPercentCategory(Double percent);

	public abstract void setPercentCourseGrade(Double percent);

	public abstract void setPoints(Double points);

	public abstract void setReleased(Boolean released);

	public abstract void setReleaseGrades(Boolean release);

	public abstract void setReleaseItems(Boolean release);

	public abstract void setRemoved(Boolean removed);

	public abstract void setShowItemStatistics(Boolean showItemStatistics);
	
	public abstract void setShowStatisticsChart(Boolean showStatisticsChart);

	public abstract void setShowMean(Boolean showMean);
	
	public abstract void setShowMedian(Boolean showMedian);
	
	public abstract void setShowMode(Boolean showMode);
	
	public abstract void setShowRank(Boolean showRank);
	
	public abstract void setSource(String source);
	
	public abstract void setStudentModelKey(String key);

	public abstract void setWeighting(Double weighting);
	
	public abstract boolean isScaledExtraCreditEnabled();
	
	public abstract void setScaledExtraCreditEnabled(Boolean allowScaledExtraCredit);
	
	public abstract List<BusinessLogicCode> getIgnoredBusinessRules();

	public abstract void setNotCalculable(boolean isNotCalculable);

	public abstract boolean isNotCalculable();
	
}
