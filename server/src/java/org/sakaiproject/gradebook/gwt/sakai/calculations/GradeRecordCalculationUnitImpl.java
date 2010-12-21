package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.math.BigDecimal;

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

	// Making default constructor private since this class needs to be instantiated with a scale
	private GradeRecordCalculationUnitImpl() {
	}
	
	public GradeRecordCalculationUnitImpl(BigDecimal pointsReceived, BigDecimal pointsPossible, BigDecimal percentOfCategory, Boolean extraCredit, int scale) {
		super(scale);
		this.pointsReceived = pointsReceived;
		this.pointsPossible = pointsPossible;
		this.percentOfCategory = percentOfCategory ;
		calculatePercentageScore();
		isExcused = pointsReceived == null;
		this.isExtraCredit = extraCredit == null ? false : extraCredit.booleanValue();
	}

	public BigDecimal calculate(BigDecimal weight) {

		if (percentageScore != null && weight != null) {
			scaledScore = multiply(percentageScore, weight);
			return scaledScore;
		}

		return null;
	}

	// Calculate equally is used in situations where the category is equally weighted
	public BigDecimal calculateEqually(int numItems) {

		if (percentageScore != null && numItems > 0) {
			scaledScore = divide(percentageScore, new BigDecimal(numItems));
			return scaledScore;
		}

		return null;
	}

	public void calculatePercentageScore() {

		if (pointsReceived == null || pointsPossible == null)
			return;

		if (pointsReceived.compareTo(BigDecimal.ZERO) == 0 || pointsPossible.compareTo(BigDecimal.ZERO) == 0)
			percentageScore = BigDecimal.ZERO;

		if (pointsPossible.compareTo(BigDecimal.ZERO) != 0)
		{
			percentageScore = divide(pointsReceived, pointsPossible);
		}
	}

	public void calculateRawDifference() {
		if (pointsReceived != null && pointsPossible != null)
			pointsDifference = subtract(pointsPossible, pointsReceived);
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
