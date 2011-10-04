package org.sakaiproject.gradebook.gwt.server.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.BusinessLogicCode;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.type.ItemType;
import org.sakaiproject.gradebook.gwt.sakai.model.GradeItem;
import org.sakaiproject.gradebook.gwt.server.Util;

public class GradeItemImpl extends BaseModel implements GradeItem {

	private static final long serialVersionUID = 1L;
	
	public GradeItemImpl() {
		super();
	}
	
	public GradeItemImpl(Map<String,Object> map) {
		super(map);
		
		List<Map<String,Object>> children = (List<Map<String, Object>>) map.get(ItemKey.A_CHILDREN.name());
		List<GradeItem> items = new ArrayList<GradeItem>();
		
		if (children != null) {
			for (int i=0;i<children.size();i++) {
				GradeItem item = new GradeItemImpl(children.get(i));
				item.setParentName(getName());
				items.add(item);
			}
		}
		
		setChildren(items);	
	}
	
	public void addChild(GradeItem child) {

		assert(child != null);

		child.setParentName(getName());
		
		List<Item> childrenList = (List<Item>)get(ItemKey.A_CHILDREN.name());

		if (childrenList == null)
			childrenList = new ArrayList<Item>();

		if (!childrenList.contains(child))
			childrenList.add(child);

		put(ItemKey.A_CHILDREN.name(), childrenList);
	}
	
	public void setChildren(List<GradeItem> children) {
		put(ItemKey.A_CHILDREN.name(), children);
	}
	
	// Getters

	public Long getCategoryId() {
		return Util.toLong(get(ItemKey.L_CTGRY_ID.name()));
	}

	public String getCategoryName() {
		return Util.toString(get(ItemKey.S_CTGRY_NAME.name()));
	}

	public CategoryType getCategoryType() {
		return Util.toCategoryType(get(ItemKey.C_CTGRY_TYPE.name()));
	}
	
	public int getChildCount() {
		List<Map<String,Object>> childrenList = (List<Map<String,Object>>)get(ItemKey.A_CHILDREN.name());
		
		if (childrenList != null) {
			return childrenList.size();
		}
		
		return 0;
	}

	public List<GradeItem> getChildren() {
		
		List<GradeItem> children = (List<GradeItem>)get(ItemKey.A_CHILDREN.name()); 
		
		if (children == null)
			children = new ArrayList<GradeItem>();
		
		return children;
	}

	public String getDataType() {
		return Util.toString(get(ItemKey.S_DATA_TYPE.name()));
	}

	public String getDisplayName() {
		return getName();
	}

	public Boolean getDoRecalculatePoints() {
		return Util.toBoolean(get(ItemKey.B_RECALC_PTS.name()));
	}

	public Integer getDropLowest() {
		return Util.toInteger(get(ItemKey.I_DRP_LWST.name()));
	}

	public Date getDueDate() throws InvalidInputException {
		return Util.toDate(get(ItemKey.W_DUE.name()));
	}

	public Boolean getEnforcePointWeighting() {
		return Util.toBoolean(get(ItemKey.B_WT_BY_PTS.name()));
	}

	public Boolean getEqualWeightAssignments() {
		return Util.toBoolean(get(ItemKey.B_EQL_WGHT.name()));
	}

	public Boolean getExtraCredit() {
		return Util.toBoolean(get(ItemKey.B_X_CRDT.name()));
	}

	public Boolean getExtraCreditScaled() {
		return Util.toBoolean(get(ItemKey.B_SCL_X_CRDT.name()));
	}

	public String getGradebook() {
		return Util.toString(get(ItemKey.S_GB_NAME.name()));
	}

	public Long getGradeScaleId() {
		return Util.toLong(get(ItemKey.L_GRD_SCL_ID.name()));
	}

	public GradeType getGradeType() {
		return Util.toGradeType(get(ItemKey.G_GRD_TYPE.name()));
	}

	public String getIdentifier() {
		return Util.toString(get(ItemKey.S_ID.name()));
	}

	public Boolean getIncluded() {
		return Util.toBoolean(get(ItemKey.B_INCLD.name()));
	}

	public Boolean getIsPercentage() {
		return Util.toBoolean(get(ItemKey.B_IS_PCT.name()));
	}

	public Long getItemId() {
		return Util.toLong(get(ItemKey.L_ITM_ID.name()));
	}

	public Integer getItemOrder() {
		return Util.toInteger(get(ItemKey.I_SRT_ORDR.name()));
	}

	public ItemType getItemType() {
		return Util.toItemType(get(ItemKey.S_ITM_TYPE.name()));
	}

	public Map<String, Object> getMap() {
		return this;
	}

	public String getName() {
		return Util.toString(get(ItemKey.S_NM.name()));
	}

	public Boolean getNullsAsZeros() {
		return get(ItemKey.B_NLLS_ZEROS.name()) != null 
	      && ((Boolean)get(ItemKey.B_NLLS_ZEROS.name()));
	}

	public String getParentName() {
		return Util.toString(get(ItemKey.S_PARENT.name()));
	}

	public Double getPercentCategory() {
		return Util.toDouble(get(ItemKey.D_PCT_CTGRY.name()));
	}

	public Double getPercentCourseGrade() {
		return Util.toDouble(get(ItemKey.D_PCT_GRD.name()));
	}

	public Double getPoints() {
		return Util.toDouble(get(ItemKey.D_PNTS.name()));
	}

	public Map<String, Object> getProperties() {
		return this;
	}

	public Collection<String> getPropertyNames() {
		return new ArrayList<String>(keySet());
	}

	public Boolean getReleased() {
		return Util.toBoolean(get(ItemKey.B_RLSD.name()));
	}

	public Boolean getReleaseGrades() {
		return Util.toBoolean(get(ItemKey.B_REL_GRDS.name()));
	}

	public Boolean getReleaseItems() {
		return Util.toBoolean(get(ItemKey.B_REL_ITMS.name()));
	}

	public Boolean getRemoved() {
		return Util.toBoolean(get(ItemKey.B_RMVD.name()));
	}

	public Boolean getShowItemStatistics() {
		return Util.toBoolean(get(ItemKey.B_SHW_ITM_STATS.name()));
	}
	
	public Boolean getShowStatisticsChart() {
		return Util.toBoolean(get(ItemKey.B_SHW_STATS_CHART.name()));
	}

	public Boolean getShowMean() {
		return Util.toBoolean(get(ItemKey.B_SHW_MEAN.name()));
	}

	public Boolean getShowMedian() {
		return Util.toBoolean(get(ItemKey.B_SHW_MEDIAN.name()));
	}

	public Boolean getShowMode() {
		return Util.toBoolean(get(ItemKey.B_SHW_MODE.name()));
	}

	public Boolean getShowRank() {
		return Util.toBoolean(get(ItemKey.B_SHW_RANK.name()));
	}

	public String getSource() {
		return Util.toString(get(ItemKey.S_SOURCE.name()));
	}
	
	public String getStudentModelKey() {
		return Util.toString(get(ItemKey.O_LRNR_KEY.name()));
	}
	
	@SuppressWarnings("unchecked")
	public List<Item> getSubItems() {
		return (List<Item>)get(ItemKey.A_CHILDREN.name());
	}
	
	public Integer getSubItemCount() {
		List<?> list = get(ItemKey.A_CHILDREN.name());
		return list == null ? Integer.valueOf(0) : Integer.valueOf(list.size());
	}
	
	// Setters
	
	public Double getWeighting() {
		return Util.toDouble(get(ItemKey.D_WGHT.name()));
	}

	public boolean isActive() {
		return Util.toBooleanPrimitive(get(ItemKey.B_ACTIVE.name()));
	}
	
	public boolean isChecked() {
		return Util.toBooleanPrimitive(get(ItemKey.B_CHCKD.name()));
	}
	
	public boolean isEditable() {
		return Util.toBooleanPrimitive(get(ItemKey.B_EDITABLE.name()));
	}

	public void setActive(boolean isActive) {
		put(ItemKey.B_ACTIVE.name(), isActive);
	}

	public void setCategoryId(Long categoryId) {
		put(ItemKey.L_CTGRY_ID.name(), categoryId);
	}

	public void setCategoryName(String categoryName) {
		put(ItemKey.S_CTGRY_NAME.name(), categoryName);
	}

	public void setCategoryType(CategoryType type) {
		put(ItemKey.C_CTGRY_TYPE.name(), type);
	}

	public void setChecked(boolean isChecked) {
		put(ItemKey.B_CHCKD.name(), isChecked);
	}

	public void setDataType(String dataType) {
		put(ItemKey.S_DATA_TYPE.name(), dataType);
	}

	public void setDoRecalculatePoints(Boolean doRecalculate) {
		put(ItemKey.B_RECALC_PTS.name(), doRecalculate);
	}

	public void setDropLowest(Integer dropLowest) {
		put(ItemKey.I_DRP_LWST.name(), dropLowest);
	}

	public void setDueDate(Date dueDate) {
		put(ItemKey.W_DUE.name(), dueDate);	}

	public void setEditable(boolean isEditable) {
		put(ItemKey.B_EDITABLE.name(), Boolean.valueOf(isEditable));
	}

	public void setEnforcePointWeighting(Boolean doEnforce) {
		put(ItemKey.B_WT_BY_PTS.name(), doEnforce);
	}

	public void setEqualWeightAssignments(Boolean equalWeight) {
		put(ItemKey.B_EQL_WGHT.name(), equalWeight);
	}

	public void setExtraCredit(Boolean extraCredit) {
		put(ItemKey.B_X_CRDT.name(), extraCredit);
	}

	public void setExtraCreditScaled(Boolean scaled) {
		put(ItemKey.B_SCL_X_CRDT.name(), scaled);
	}

	public void setGradebook(String gradebook) {
		put(ItemKey.S_GB_NAME.name(), gradebook);
	}

	public void setGradeScaleId(Long id) {
		put(ItemKey.L_GRD_SCL_ID.name(), id);
	}

	public void setGradeType(GradeType type) {
		put(ItemKey.G_GRD_TYPE.name(), type.name());
	}

	public void setIdentifier(String id) {
		put(ItemKey.S_ID.name(), id);
	}

	public void setIncluded(Boolean included) {
		put(ItemKey.B_INCLD.name(), included);
	}

	public void setIsPercentage(Boolean isPercentage) {
		put(ItemKey.B_IS_PCT.name(), isPercentage);
	}

	public void setItemId(Long itemId) {
		put(ItemKey.L_ITM_ID.name(), itemId);
	}

	public void setItemOrder(Integer itemOrder) {
		put(ItemKey.I_SRT_ORDR.name(), itemOrder);
	}

	public void setItemType(ItemType type) {
		put(ItemKey.S_ITM_TYPE.name(), type.name());
	}

	public void setName(String name) {
		put(ItemKey.S_NM.name(), name);
	}

	public void setNullsAsZeros(Boolean nullsAsZeros) {
		put(ItemKey.B_NLLS_ZEROS.name(), nullsAsZeros);
	}

	public void setParentName(String parentName) {
		put(ItemKey.S_PARENT.name(), parentName);
	}

	public void setPercentCategory(Double percent) {
		put(ItemKey.D_PCT_CTGRY.name(), percent);
	}

	public void setPercentCourseGrade(Double percent) {
		put(ItemKey.D_PCT_GRD.name(), percent);
	}

	public void setPoints(Double points) {
		put(ItemKey.D_PNTS.name(), points);
	}

	public void setReleased(Boolean released) {
		put(ItemKey.B_RLSD.name(), released);
	}

	public void setReleaseGrades(Boolean release) {
		put(ItemKey.B_REL_GRDS.name(), release);
	}

	public void setReleaseItems(Boolean release) {
		put(ItemKey.B_REL_ITMS.name(), release);
	}

	public void setRemoved(Boolean removed) {
		put(ItemKey.B_RMVD.name(), removed);
	}

	public void setShowItemStatistics(Boolean showItemStatistics) {
		put(ItemKey.B_SHW_ITM_STATS.name(), showItemStatistics);
	}
	
	public void setShowStatisticsChart(Boolean showStatisticsChart) {
		set(ItemKey.B_SHW_STATS_CHART.name(), showStatisticsChart);
	}

	public void setShowMean(Boolean showMean) {
		put(ItemKey.B_SHW_MEAN.name(), showMean);
	}

	public void setShowMedian(Boolean showMedian) {
		put(ItemKey.B_SHW_MEDIAN.name(), showMedian);
	}

	public void setShowMode(Boolean showMode) {
		put(ItemKey.B_SHW_MODE.name(), showMode);
	}

	public void setShowRank(Boolean showRank) {
		put(ItemKey.B_SHW_RANK.name(), showRank);
	}

	public void setSource(String source) {
		put(ItemKey.S_SOURCE.name(), source);
	}

	public void setStudentModelKey(String key) {
		put(ItemKey.O_LRNR_KEY.name(), key);
	}
	
	public void setWeighting(Double weighting) {
		put(ItemKey.D_WGHT.name(), weighting);
	}
	
	public boolean isScaledExtraCreditEnabled() {
		return Util.toBooleanPrimitive(get(ItemKey.B_ALW_SCL_X_CRDT.name()));
	}

	public void setScaledExtraCreditEnabled(Boolean allowScaledExtraCredit) {
		put(ItemKey.B_ALW_SCL_X_CRDT.name(), allowScaledExtraCredit);
	}

	public List<BusinessLogicCode> getIgnoredBusinessRules() {
		List<BusinessLogicCode> rules = get(ItemKey.A_IGNOR.name());
		if (rules == null) {
			rules = new ArrayList<BusinessLogicCode>();
			set(ItemKey.A_IGNOR.name(), rules);
			}
		return rules;
	}

	public boolean isNotCalculable() {
		return DataTypeConversionUtil.checkBoolean((Boolean)get(ItemKey.B_ISNT_CALCBLE.name()));
	}
	
	public void setNotCalculable(boolean isNotCalculable) {
		set(ItemKey.B_ISNT_CALCBLE.name(), Boolean.valueOf(isNotCalculable));
	}

	
}
