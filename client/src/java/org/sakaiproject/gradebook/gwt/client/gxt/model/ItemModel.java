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

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.BusinessLogicCode;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.ClassType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.type.ItemType;

import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;

public class ItemModel extends EntityTreeModel implements Item {

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
	
	public ItemModel(EntityOverlay overlay) {
		super(overlay);
		setActive(false);
		setEditable(true);
	}
	
	@Override
	public void insert(ModelData child, int index) {
		adopt(child);
		List<ModelData> children = getChildren();
	    children.add(index, child);
	    //if (children.size() == 1)
	    setChildren(children);
	    ChangeEvent evt = new ChangeEvent(Add, this);
	    evt.setParent(this);
	    evt.setItem(child);
	    evt.setIndex(index);
	    notify(evt);
	}
	
	@Override
	public int indexOf(ModelData child) {
		return getChildren().indexOf(child);
	}
	
	@Override
	public ModelData getChild(int index) {
		List<ModelData> children = getChildren();
		int size = children == null ? 0 : children.size();
		if ((index < 0) || (index >= size)) return null;
		    return children.get(index);
	}

	/**
	 * Returns the number of children.
	 * 
	 * @return the number of children
	 */
	@Override
	public int getChildCount() {
		List<ModelData> children = getChildren();
		if (children == null)
			return 0;
		
		return children.size();
	}

	/**
	 * Returns the model's children.
	 * 
	 * @return the children
	 */
	@Override
	public List<ModelData> getChildren() {
		List<ModelData> children = get(ItemKey.A_CHILDREN.name());
		if (children == null)
			return new ArrayList<ModelData>();
		return children;
	}
	
	public void removeChild(int i) {
		List<ModelData> children = getChildren();
		
		if (children.size() > i) {
			children.remove(i);
		}
		
		setChildren(children);
	}
	
	@Override
	public void setChildren(List<ModelData> children) {
		set(ItemKey.A_CHILDREN.name(), children);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getDisplayName()
	 */
	public String getDisplayName() {
		return get(ItemKey.S_NM.name());
	}
	
	public static ClassType lookupClassType(String property) {
		ItemKey key = ItemKey.valueOf(property);
		
		switch (key) {
		case S_ID: case S_NM: case S_GB_NAME: case S_CTGRY_NAME: case S_SOURCE: case S_ITM_TYPE:
		case O_LRNR_KEY: case S_DATA_TYPE:
			return ClassType.STRING;
		case D_WGHT: case D_PNTS: case D_PCT_GRD: case D_PCT_CTGRY:
			return ClassType.DOUBLE;
		case B_EQL_WGHT: case B_X_CRDT: case B_INCLD: case B_RMVD: case B_RLSD:
		case B_IS_PCT: case B_REL_GRDS:
			return ClassType.BOOLEAN;
		case I_DRP_LWST:
			return ClassType.INTEGER;
		case L_CTGRY_ID: case L_ITM_ID:
			return ClassType.LONG;
		case W_DUE:
			return ClassType.DATE;
		case C_CTGRY_TYPE:
			return ClassType.CATEGORYTYPE;
		case G_GRD_TYPE:
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
		return get(ItemKey.S_ID.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setIdentifier(java.lang.String)
	 */
	public void setIdentifier(String id) {
		set(ItemKey.S_ID.name(), id);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getName()
	 */
	public String getName() {
		return get(ItemKey.S_NM.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setName(java.lang.String)
	 */
	public void setName(String name) {
		set(ItemKey.S_NM.name(), name);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getWeighting()
	 */
	public Double getWeighting() {
		return get(ItemKey.D_WGHT.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setWeighting(java.lang.Double)
	 */
	public void setWeighting(Double weighting) {
		set(ItemKey.D_WGHT.name(), weighting);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getExtraCredit()
	 */
	public Boolean getExtraCredit() {
		return get(ItemKey.B_X_CRDT.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setExtraCredit(java.lang.Boolean)
	 */
	public void setExtraCredit(Boolean extraCredit) {
		set(ItemKey.B_X_CRDT.name(), extraCredit);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getIncluded()
	 */
	public Boolean getIncluded() {
		return get(ItemKey.B_INCLD.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setIncluded(java.lang.Boolean)
	 */
	public void setIncluded(Boolean included) {
		set(ItemKey.B_INCLD.name(), included);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getRemoved()
	 */
	public Boolean getRemoved() {
		return get(ItemKey.B_RMVD.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setRemoved(java.lang.Boolean)
	 */
	public void setRemoved(Boolean removed) {
		set(ItemKey.B_RMVD.name(), removed);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getItemType()
	 */
	public ItemType getItemType() {
		String typeName = get(ItemKey.S_ITM_TYPE.name());
		if (typeName == null)
			return null;
		return ItemType.valueOf(typeName);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setItemType(org.sakaiproject.gradebook.gwt.client.model.ItemType)
	 */
	public void setItemType(ItemType type) {
		set(ItemKey.S_ITM_TYPE.name(), type.name());
	}
	
	// Category specific
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getGradebook()
	 */
	public String getGradebook() {
		return get(ItemKey.S_GB_NAME.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setGradebook(java.lang.String)
	 */
	public void setGradebook(String gradebook) {
		set(ItemKey.S_GB_NAME.name(), gradebook);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getEqualWeightAssignments()
	 */
	public Boolean getEqualWeightAssignments() {
		return get(ItemKey.B_EQL_WGHT.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setEqualWeightAssignments(java.lang.Boolean)
	 */
	public void setEqualWeightAssignments(Boolean equalWeight) {
		set(ItemKey.B_EQL_WGHT.name(), equalWeight);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getDropLowest()
	 */
	public Integer getDropLowest() {
		return get(ItemKey.I_DRP_LWST.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setDropLowest(java.lang.Integer)
	 */
	public void setDropLowest(Integer dropLowest) {
		set(ItemKey.I_DRP_LWST.name(), dropLowest);
	}
	
	// Assignment specific
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getCategoryName()
	 */
	public String getCategoryName() {
		return get(ItemKey.S_CTGRY_NAME.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setCategoryName(java.lang.String)
	 */
	public void setCategoryName(String categoryName) {
		set(ItemKey.S_CTGRY_NAME.name(), categoryName);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getItemId()
	 */
	public Long getItemId() {
		return getLong(ItemKey.L_ITM_ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setItemId(java.lang.Long)
	 */
	public void setItemId(Long itemId) {
		set(ItemKey.L_ITM_ID.name(), itemId);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getCategoryId()
	 */
	public Long getCategoryId() {
		Object o = getLong(ItemKey.L_CTGRY_ID.name());
		
		// FIXME: we need to check if this is still true
		// adding GWT log messages so that we can spot this in GWT DEV mode
		/* 
		 * This hack exists because for some odd reason, the category ID is sometimes a string.  Maybe bad serialization? 
		 */
		if (null == o) 
		{
			return Long.valueOf(-1);
		}
		else if (o instanceof Long)
		{
			return (Long) o; 
		}
		else if (o instanceof Integer) 
		{
			GWT.log("ERROR: #### this should not happen ####");
			return Long.valueOf(((Integer)o).intValue());
		}
		else
		{
			GWT.log("ERROR: #### this should not happen ####");
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
		set(ItemKey.L_CTGRY_ID.name(), categoryId);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getPoints()
	 */
	public Double getPoints() {
		return get(ItemKey.D_PNTS.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setPoints(java.lang.Double)
	 */
	public void setPoints(Double points) {
		set(ItemKey.D_PNTS.name(), points);
		set(ItemKey.S_PNTS.name(), String.valueOf(points));
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getDueDate()
	 */
	public Date getDueDate() {
		Object obj = get(ItemKey.W_DUE.name());
		
		if (obj instanceof Long)
			return new Date((Long)obj);
		
		return (Date)obj;
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setDueDate(java.util.Date)
	 */
	public void setDueDate(Date dueDate) {
		set(ItemKey.W_DUE.name(), dueDate);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getReleased()
	 */
	public Boolean getReleased() {
		return get(ItemKey.B_RLSD.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setReleased(java.lang.Boolean)
	 */
	public void setReleased(Boolean released) {
		set(ItemKey.B_RLSD.name(), released);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getNullsAsZeros()
	 */
	public Boolean getNullsAsZeros() {
		return get(ItemKey.B_NLLS_ZEROS.name()) != null 
	      && ((Boolean)get(ItemKey.B_NLLS_ZEROS.name()));
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setNullsAsZeros(java.lang.Boolean)
	 */
	public void setNullsAsZeros(Boolean nullsAsZeros) {
		set(ItemKey.B_NLLS_ZEROS.name(), nullsAsZeros);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getSource()
	 */
	public String getSource() {
		return get(ItemKey.S_SOURCE.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setSource(java.lang.String)
	 */
	public void setSource(String source) {
		set(ItemKey.S_SOURCE.name(), source);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getPercentCourseGrade()
	 */
	public Double getPercentCourseGrade() {
		return get(ItemKey.D_PCT_GRD.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setPercentCourseGrade(java.lang.Double)
	 */
	public void setPercentCourseGrade(Double percent) {
		set(ItemKey.D_PCT_GRD.name(), percent);
		set(ItemKey.S_PCT_GRD.name(), String.valueOf(percent));
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getPercentCategory()
	 */
	public Double getPercentCategory() {
		return get(ItemKey.D_PCT_CTGRY.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setPercentCategory(java.lang.Double)
	 */
	public void setPercentCategory(Double percent) {
		set(ItemKey.D_PCT_CTGRY.name(), percent);
		set(ItemKey.S_PCT_CTGRY.name(), String.valueOf(percent));
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getIsPercentage()
	 */
	public Boolean getIsPercentage() {
		return get(ItemKey.B_IS_PCT.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setIsPercentage(java.lang.Boolean)
	 */
	public void setIsPercentage(Boolean isPercentage) {
		set(ItemKey.B_IS_PCT.name(), isPercentage);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getStudentModelKey()
	 */
	public String getStudentModelKey() {
		return get(ItemKey.O_LRNR_KEY.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setStudentModelKey(java.lang.String)
	 */
	public void setStudentModelKey(String key) {
		set(ItemKey.O_LRNR_KEY.name(), key);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getDataType()
	 */
	public String getDataType() {
		return get(ItemKey.S_DATA_TYPE.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setDataType(java.lang.String)
	 */
	public void setDataType(String dataType) {
		set(ItemKey.S_DATA_TYPE.name(), dataType);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getCategoryType()
	 */
	public CategoryType getCategoryType() {
		Object obj = get(ItemKey.C_CTGRY_TYPE.name());
		
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
		set(ItemKey.C_CTGRY_TYPE.name(), type);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getGradeType()
	 */
	public GradeType getGradeType() {
		Object obj = get(ItemKey.G_GRD_TYPE.name());
		
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
		set(ItemKey.G_GRD_TYPE.name(), type);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getReleaseGrades()
	 */
	public Boolean getReleaseGrades() {
		return get(ItemKey.B_REL_GRDS.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setReleaseGrades(java.lang.Boolean)
	 */
	public void setReleaseGrades(Boolean release) {
		set(ItemKey.B_REL_GRDS.name(), release);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getReleaseItems()
	 */
	public Boolean getReleaseItems() {
		return get(ItemKey.B_REL_ITMS.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setReleaseItems(java.lang.Boolean)
	 */
	public void setReleaseItems(Boolean release) {
		set(ItemKey.B_REL_ITMS.name(), release);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getItemOrder()
	 */
	public Integer getItemOrder() {
		return get(ItemKey.I_SRT_ORDR.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setItemOrder(java.lang.Integer)
	 */
	public void setItemOrder(Integer itemOrder) {
		set(ItemKey.I_SRT_ORDR.name(), itemOrder);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getGradeScaleId()
	 */
	public Long getGradeScaleId() {
		return getLong(ItemKey.L_GRD_SCL_ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setGradeScaleId(java.lang.Long)
	 */
	public void setGradeScaleId(Long id) {
		set(ItemKey.L_GRD_SCL_ID.name(), id);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getExtraCreditScaled()
	 */
	public Boolean getExtraCreditScaled() {
		return get(ItemKey.B_SCL_X_CRDT.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setExtraCreditScaled(java.lang.Boolean)
	 */
	public void setExtraCreditScaled(Boolean scaled) {
		set(ItemKey.B_SCL_X_CRDT.name(), scaled);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getDoRecalculatePoints()
	 */
	public Boolean getDoRecalculatePoints() {
		return get(ItemKey.B_RECALC_PTS.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setDoRecalculatePoints(java.lang.Boolean)
	 */
	public void setDoRecalculatePoints(Boolean doRecalculate) {
		set(ItemKey.B_RECALC_PTS.name(), doRecalculate);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getEnforcePointWeighting()
	 */
	public Boolean getEnforcePointWeighting() {
		return get(ItemKey.B_WT_BY_PTS.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setEnforcePointWeighting(java.lang.Boolean)
	 */
	public void setEnforcePointWeighting(Boolean doEnforce) {
		set(ItemKey.B_WT_BY_PTS.name(), doEnforce);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getShowMean()
	 */
	public Boolean getShowMean() {
		return get(ItemKey.B_SHW_MEAN.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setShowMean(java.lang.Boolean)
	 */
	public void setShowMean(Boolean showMean) {
		set(ItemKey.B_SHW_MEAN.name(), showMean);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getShowMedian()
	 */
	public Boolean getShowMedian() {
		return get(ItemKey.B_SHW_MEDIAN.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setShowMedian(java.lang.Boolean)
	 */
	public void setShowMedian(Boolean showMedian) {
		set(ItemKey.B_SHW_MEDIAN.name(), showMedian);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getShowMode()
	 */
	public Boolean getShowMode() {
		return get(ItemKey.B_SHW_MODE.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setShowMode(java.lang.Boolean)
	 */
	public void setShowMode(Boolean showMode) {
		set(ItemKey.B_SHW_MODE.name(), showMode);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getShowRank()
	 */
	public Boolean getShowRank() {
		return get(ItemKey.B_SHW_RANK.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setShowRank(java.lang.Boolean)
	 */
	public void setShowRank(Boolean showRank) {
		set(ItemKey.B_SHW_RANK.name(), showRank);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getShowItemStatistics()
	 */
	public Boolean getShowItemStatistics() {
		return get(ItemKey.B_SHW_ITM_STATS.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setShowItemStatistics(java.lang.Boolean)
	 */
	public void setShowItemStatistics(Boolean showItemStatistics) {
		set(ItemKey.B_SHW_ITM_STATS.name(), showItemStatistics);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#getShowStatisticsChart()
	 */
	public Boolean getShowStatisticsChart() {
		return get(ItemKey.B_SHW_STATS_CHART.name());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setShowStatisticsChart(java.lang.Boolean)
	 */
	public void setShowStatisticsChart(Boolean showStatisticsChart) {
		set(ItemKey.B_SHW_STATS_CHART.name(), showStatisticsChart);
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
		 // FIXME: TPA : getItemType() can return null : need to protect against null pointer exception : old import
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
		Boolean active = get(ItemKey.B_ACTIVE.name());
		return active == null ? false : active.booleanValue();
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setActive(boolean)
	 */
	public void setActive(boolean isActive) {
		set(ItemKey.B_ACTIVE.name(), Boolean.valueOf(isActive));
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#isEditable()
	 */
	public boolean isEditable() {
		Boolean active = get(ItemKey.B_EDITABLE.name());
		return active == null ? true : active.booleanValue();
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setEditable(boolean)
	 */
	public void setEditable(boolean isEditable) {
		set(ItemKey.B_EDITABLE.name(), Boolean.valueOf(isEditable));
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#isChecked()
	 */
	public boolean isChecked() {
		Boolean active = get(ItemKey.B_CHCKD.name());
		return active == null ? false : active.booleanValue();
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Item#setChecked(boolean)
	 */
	public void setChecked(boolean isChecked) {
		set(ItemKey.B_CHCKD.name(), Boolean.valueOf(isChecked));
	}

	/*public void addChild(Item child) {
		add((ModelData)child);
	}*/

	public boolean isIncluded() {
		return DataTypeConversionUtil.checkBoolean(getIncluded());
	}

	public boolean isRemoved() {
		return DataTypeConversionUtil.checkBoolean(getRemoved());
	}
	
	public boolean isScaledExtraCreditEnabled() {
		return DataTypeConversionUtil.checkBoolean((Boolean)get(ItemKey.B_ALW_SCL_X_CRDT.name()));
	}

	public void setScaledExtraCreditEnabled(Boolean allowScaledExtraCredit) {
		set(ItemKey.B_ALW_SCL_X_CRDT.name(), allowScaledExtraCredit);
	}
	
	public boolean isNotCalculable() {
		return DataTypeConversionUtil.checkBoolean((Boolean)get(ItemKey.B_ISNT_CALCBLE.name()));
	}
	
	public void setNotCalculable(boolean isNotCalculable) {
		set(ItemKey.B_ISNT_CALCBLE.name(), Boolean.valueOf(isNotCalculable));
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
	
	public boolean isDate(String property) {
		return property.equals(ItemKey.W_DUE.name());
	}
	
	public DateTimeFormat getDateTimeFormat(String property) {
		if (property.equals(ItemKey.W_DUE.name())) {
			return DateTimeFormat.getFormat(AppConstants.SHORT_DATE);
		}
		return DateTimeFormat.getMediumDateFormat();
	}
	
	public boolean isGradeType(String property) {
		 return property.equals(ItemKey.G_GRD_TYPE.name());
	}
	 
	public boolean isCategoryType(String property) {
		 return property.equals(ItemKey.C_CTGRY_TYPE.name());
	}
		
	public ModelData newChildModel(String property, EntityOverlay overlay) {
		 return new ItemModel(overlay);
	}
	
	private void adopt(ModelData child) {
		TreeModel p = getParentInternal(child);
		if (p != null && p != this) {
			p.remove(child);
		}
		setParentInternal(child);
	}
	
	private void setParentInternal(ModelData child) {
		if (child instanceof TreeModel) {
			TreeModel treeChild = (TreeModel) child;
			treeChild.setParent(this);
		} else {
			child.set("gxt-parent", child);
		}
	}

	private TreeModel getParentInternal(ModelData child) {
		if (child instanceof TreeModel) {
			TreeModel treeChild = (TreeModel) child;
			return treeChild.getParent();
		} else {
			return (TreeModel) child.get("gxt-parent");
		}
	}

	public List<BusinessLogicCode> getIgnoredBusinessRules() {
		List<BusinessLogicCode> rules = get(ItemKey.A_IGNOR.name());
		if (rules == null) {
			rules = new ArrayList<BusinessLogicCode>();
			set(ItemKey.A_IGNOR.name(), rules);
		}
		return rules;
	}
}
