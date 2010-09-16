package org.sakaiproject.gradebook.gwt.sakai.calculations2;

import java.math.BigDecimal;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.sakai.GradeCalculations;

public class GradeRecordCalculationUnitImpl extends BigDecimalCalculationsWrapper implements GradeRecordCalculationUnit  {

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

	public GradeRecordCalculationUnitImpl(BigDecimal pointsReceived, BigDecimal pointsPossible, BigDecimal percentOfCategory, Boolean extraCredit) {
		this.pointsReceived = pointsReceived == null ? null : pointsReceived.setScale(AppConstants.SCALE, GradeCalculations.MATH_CONTEXT.getRoundingMode());
		this.pointsPossible = pointsPossible == null ? null : pointsPossible.setScale(AppConstants.SCALE, GradeCalculations.MATH_CONTEXT.getRoundingMode());
		this.percentOfCategory = percentOfCategory == null ? null : percentOfCategory.setScale(AppConstants.SCALE, GradeCalculations.MATH_CONTEXT.getRoundingMode());
		calculatePercentageScore();
		isExcused = pointsReceived == null;
		this.isExtraCredit = extraCredit == null ? false : extraCredit.booleanValue();
	}

	public GradeRecordCalculationUnitImpl(BigDecimal percentageScore, BigDecimal percentOfCategory, Boolean extraCredit) {
		this.percentageScore = percentageScore == null ? null : percentageScore.setScale(AppConstants.SCALE, GradeCalculations.MATH_CONTEXT.getRoundingMode());
		this.percentOfCategory = percentOfCategory == null ? null : percentOfCategory.setScale(AppConstants.SCALE, GradeCalculations.MATH_CONTEXT.getRoundingMode());

		isExcused = pointsReceived == null;
		this.isExtraCredit = extraCredit == null ? false : extraCredit.booleanValue();
	}

	public BigDecimal calculate(BigDecimal weight) {

		if (percentageScore != null && weight != null) {
			scaledScore = multiply(percentageScore, weight);
			//scaledScore = percentageScore.multiply(weight);
			return scaledScore;
		}

		return null;
	}

	public void calculatePercentageScore() {

		if (pointsReceived == null || pointsPossible == null)
			return;

		if (pointsReceived.compareTo(BigDecimal.ZERO) == 0 || pointsPossible.compareTo(BigDecimal.ZERO) == 0)
			percentageScore = BigDecimal.ZERO.setScale(AppConstants.SCALE, GradeCalculations.MATH_CONTEXT.getRoundingMode());

		if (pointsPossible.compareTo(BigDecimal.ZERO) != 0)
		{
			percentageScore = divide(pointsReceived, pointsPossible);
			//percentageScore = pointsReceived.divide(pointsPossible, GradeCalculations.MATH_CONTEXT);
		}
		else
		{
			percentageScore = BigDecimal.ZERO;
		}
	}

	public void calculateRawDifference() {
		if (pointsReceived != null && pointsPossible != null)
			pointsDifference = subtract(pointsPossible, pointsReceived);
			//pointsDifference = pointsPossible.subtract(pointsReceived, GradeCalculations.MATH_CONTEXT);
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
