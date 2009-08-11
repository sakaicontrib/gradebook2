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

package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.sakaiproject.gradebook.gwt.client.AppConstants;

public class GradeRecordCalculationUnit {

	public static final MathContext MATH_CONTEXT = new MathContext(10, RoundingMode.HALF_EVEN);
	private static final int SCALE = 10;

	private BigDecimal pointsReceived;
	private BigDecimal pointsPossible;
	private BigDecimal pointsDifference;
	private BigDecimal percentOfCategory;
	private BigDecimal scaledScore;

	// This is simply the points received divided by the points possible
	private BigDecimal percentageScore;

	private boolean isDropped = false;
	private boolean isExcused = false;
	private boolean isExtraCredit = false;

	protected Object actualRecord;

	public GradeRecordCalculationUnit(BigDecimal pointsReceived, BigDecimal pointsPossible, BigDecimal percentOfCategory, Boolean extraCredit) {
		this.pointsReceived = pointsReceived == null ? null : pointsReceived.setScale(SCALE, RoundingMode.HALF_EVEN);
		this.pointsPossible = pointsPossible == null ? null : pointsPossible.setScale(SCALE, RoundingMode.HALF_EVEN);
		this.percentOfCategory = percentOfCategory == null ? null : percentOfCategory.setScale(SCALE, RoundingMode.HALF_EVEN);
		calculatePercentageScore();
		isExcused = pointsReceived == null;
		this.isExtraCredit = extraCredit == null ? false : extraCredit.booleanValue();
	}

	public GradeRecordCalculationUnit(BigDecimal percentageScore, BigDecimal percentOfCategory, Boolean extraCredit) {
		this.percentageScore = percentageScore == null ? null : percentageScore.setScale(SCALE, RoundingMode.HALF_EVEN);
		this.percentOfCategory = percentOfCategory == null ? null : percentOfCategory.setScale(SCALE, RoundingMode.HALF_EVEN);

		isExcused = pointsReceived == null;
		this.isExtraCredit = extraCredit == null ? false : extraCredit.booleanValue();
	}

	public BigDecimal calculate(BigDecimal weight) {

		if (percentageScore != null && weight != null) {
			scaledScore = percentageScore.multiply(weight);
			return scaledScore;
		}

		return null;
	}

	public void calculatePercentageScore() {

		if (pointsReceived == null || pointsPossible == null)
			return;

		if (pointsReceived.compareTo(BigDecimal.ZERO) == 0 || pointsPossible.compareTo(BigDecimal.ZERO) == 0)
			percentageScore = BigDecimal.ZERO.setScale(AppConstants.SCALE, RoundingMode.HALF_EVEN);

		if (pointsPossible.compareTo(BigDecimal.ZERO) != 0)
		{
			percentageScore = pointsReceived.divide(pointsPossible, RoundingMode.HALF_EVEN);
		}
		else
		{
			percentageScore = BigDecimal.ZERO;
		}
	}

	public void calculateRawDifference() {
		if (pointsReceived != null && pointsPossible != null)
			pointsDifference = pointsPossible.subtract(pointsReceived);
		else
			pointsDifference = null;
	}

	public BigDecimal getPercentOfCategory() {
		return percentOfCategory;
	}

	public BigDecimal getScaledScore() {
		return scaledScore;
	}

	public boolean isExcused() {
		return isExcused;
	}

	public boolean isExtraCredit() {
		return isExtraCredit;
	}

	public BigDecimal getPointsReceived() {
		return pointsReceived;
	}

	public BigDecimal getPointsPossible() {
		return pointsPossible;
	}

	public BigDecimal getPercentageScore() {
		return percentageScore;
	}

	public void setExcused(boolean isExcused) {
		this.isExcused = isExcused;
	}

	public boolean isDropped() {
		return isDropped;
	}

	public void setDropped(boolean isDropped) {
		this.isDropped = isDropped;
	}

	public Object getActualRecord() {
		return actualRecord;
	}

	public void setActualRecord(Object actualRecord) {
		this.actualRecord = actualRecord;
	}

	public BigDecimal getPointsDifference() {
		return pointsDifference;
	}

}
