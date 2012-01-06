/**********************************************************************************
*
* $Id:$
*
***********************************************************************************
*
* Copyright (c) 2008, 2009, 2010 The Regents of the University of California
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

package org.sakaiproject.gradebook.gwt.client.model.key;



public enum ItemKey {
	S_ID("id"), 
	S_NM("name"), 
	D_WGHT("weight"), 
	B_EQL_WGHT("isEqualWeight"), 
	B_X_CRDT("isExtraCredit"), 
	B_INCLD("isIncluded"), 
	B_RMVD("isRemoved"), 
	S_GB_NAME("gradebookName"), 
	I_DRP_LWST("dropLowestX"), 
	S_CTGRY_NAME("categoryName"), 
	L_CTGRY_ID("categoryId"),
	S_CTGRY_ID("categoryId"),
	W_DUE("dueDate"), 
	D_PNTS("points"), 
	S_PNTS("pointsAsString"), 
	B_RLSD("isReleased"), 
	B_NLLS_ZEROS("isNullZero"), 
	S_SOURCE("source"), 
	S_ITM_TYPE("itemType"), 
	D_PCT_GRD("percentCourseGrade"), 
	S_PCT_GRD("percentCourseGradeAsString"),
	D_PCT_CTGRY("percentCategory"), 
	S_PCT_CTGRY("percentCategoryAsString"), 
	B_IS_PCT("isPercentage"), 
	O_LRNR_KEY("learnerKey"),
	L_ITM_ID("itemId"), 
	S_DATA_TYPE("dataType"), 
	C_CTGRY_TYPE("categoryType"),
	G_GRD_TYPE("gradeType"), 
	B_REL_GRDS("isReleaseGrades"), 
	B_REL_ITMS("isReleaseItems"),
	I_SRT_ORDR("sortOrder"), 
	L_GRD_SCL_ID("gradeScaleId"), 
	B_SCL_X_CRDT("isScaleExtraCredit"),
	B_RECALC_PTS("isRecalculatePoints"),
	B_WT_BY_PTS("isWeightByPoints"), 
	B_SHW_MEAN("isShowMean"),
	B_SHW_MEDIAN("isShowMedian"), 
	B_SHW_MODE("isShowMode"), 
	B_SHW_RANK("isShowRank"),
	B_SHW_ITM_STATS("isShowItemStats"),
	B_SHW_STATS_CHART("isShowStatisticsChart"),
	A_CHILDREN("children"), 
	B_ACTIVE("isActive"),
	B_EDITABLE("isEditable"), 
	B_CHCKD("isChecked"), 
	S_PARENT("parent"), 
	B_ALW_SCL_X_CRDT("isAllowScaledExtraCredit"),
	B_ISNT_CALCBLE("isNotCalculable"), 
	A_IGNOR("ignoredBusinessRules");
	
	private String property;

	private ItemKey(String property) {
		this.property = property;
	}

	public String getProperty() {
		return property;
	}
}
