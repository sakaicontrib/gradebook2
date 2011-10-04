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

package org.sakaiproject.gradebook.gwt.client.gxt.upload;

import java.io.Serializable;

import org.sakaiproject.gradebook.gwt.client.model.Item;

public class ImportHeader implements Serializable {

	public enum Field { S_ID, S_NAME, S_CRS_GRD, S_LTR_GRD, S_CALC_GRD, S_ADT_GRD, S_GRB_OVRD, S_ITEM, S_COMMENT, S_EMPTY }

	public enum Mode { POINTS, PERCENTAGES, LETTERGRADES };

	private static final long serialVersionUID = 1L;

	private String id;
	private Field field;
	private String assignmentId;
	private String categoryId;
	private String headerName;
	private Mode mode;
	private String value;
	private Double numericValue;
	private String categoryName;
	private String points;
	private boolean isPercentage;
	private String percentCategory;
	private boolean extraCredit;
	private boolean unincluded;
	private boolean isReleaseScores;
	
	public boolean isReleaseScores() {
		return isReleaseScores;
	}

	public void setReleaseScores(boolean isReleaseScores) {
		this.isReleaseScores = isReleaseScores;
	}

	public boolean isGiveungradedNoCredit() {
		return isGiveungradedNoCredit;
	}

	public void setGiveungradedNoCredit(boolean isGiveungradedNoCredit) {
		this.isGiveungradedNoCredit = isGiveungradedNoCredit;
	}

	private boolean isGiveungradedNoCredit;
	private int columnIndex;
	private Item item;
	
	private boolean checker;

	public ImportHeader() {

	}

	public ImportHeader(Field field, String value, int columnIndex) {
		this.field = field;
		this.value = value;
		this.columnIndex = columnIndex;
	}

	public ImportHeader(String header, int columnIndex) {
		this.headerName = header;
		this.columnIndex = columnIndex;
	}

	public ImportHeader(String assignmentId, String headerName, Mode mode, Double numericValue) {
		this.assignmentId = assignmentId;
		this.headerName = headerName;
		this.mode = mode;
		this.numericValue = numericValue;
	}

	public String getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(String assignmentId) {
		this.assignmentId = assignmentId;
	}

	public String getHeaderName() {
		return headerName;
	}

	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public Double getNumericValue() {
		return numericValue;
	}

	public void setNumericValue(Double numericValue) {
		this.numericValue = numericValue;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPoints() {
		return points;
	}

	public void setPoints(String points) {
		this.points = points;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public boolean getChecker() {
		return checker;
	}

	public void setChecker(boolean isHidden) {
		this.checker = isHidden;
	}

	public boolean isPercentage() {
		return isPercentage;
	}

	public void setPercentage(boolean isPercentage) {
		this.isPercentage = isPercentage;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getPercentCategory() {
		return percentCategory;
	}

	public void setPercentCategory(String percentCategory) {
		this.percentCategory = percentCategory;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isExtraCredit() {
		return extraCredit;
	}

	public void setExtraCredit(boolean extraCredit) {
		this.extraCredit = extraCredit;
	}

	public boolean isUnincluded() {
		return unincluded;
	}

	public void setUnincluded(boolean unincluded) {
		this.unincluded = unincluded;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
}
