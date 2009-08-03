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
	// This is the straightforward product of assignment weight and category weight
	/*private BigDecimal idealPercentOverall;
	// This is the ideal percent overall, but when an item has not been graded, it's null
	private BigDecimal actualPercentOverall;
	
	
	// This is the product of ideal percent overall and the percentage score
	private BigDecimal gradedPercentOverall;
	// This is the product of ideal percent overall and the pass 1 factor X
	private BigDecimal scalingFactorForPass2;
	// This is the product of ideal percent overall and the pass 2 factor X
	private BigDecimal scalingFactorForPass3;
	
	// This is the product of ideal percent overall and the pass 2 scaling factor for this item
	private BigDecimal scaledScoreBeforeDropLowest;
	// Order by this and drop by biggest to smallest
	private BigDecimal scaledScoreBeforeDropLowestDifferential;
	// This is the product of ideal percent overall and the pass 3 factor X
	private BigDecimal scaledScoreAfterDropLowest;*/
	
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
	
	/*public void calculateIdealPercentOverall(BigDecimal categoryTotalWeight, Mode mode, PrintWriter writer) {
		if (writer != null) writer.println("Calculate ideal percentage overall:");
		Multiplication m = new Multiplication(percentOfCategory, categoryTotalWeight, mode, writer);
		idealPercentOverall = m.perform();
	}
	
	public void calculateGradedPercentOverall(Mode mode, PrintWriter writer) {
		if (writer != null) writer.println("Calculate graded percent overall:");
		Multiplication m = new Multiplication(percentageScore, idealPercentOverall, mode, writer);
		gradedPercentOverall = m.perform();
		
		if (gradedPercentOverall != null)
			actualPercentOverall = idealPercentOverall;
	}
	
	public BigDecimal getActualPercentOverall(Mode mode, PrintWriter writer) {
		if (writer != null) writer.println("Return actual percent overall: " + actualPercentOverall);
		return actualPercentOverall;
	}
	
	public void calculateScalingFactorForPass2(BigDecimal factorX, Mode mode, PrintWriter writer) {
		if (writer != null) writer.println("Calculate scaling factor for pass 2:");
		Multiplication m = new Multiplication(idealPercentOverall, factorX, mode, writer);
		scalingFactorForPass2 = m.perform();
	}
	
	public BigDecimal calculateScaledScoreBeforeDropLowest(Mode mode, PrintWriter writer) {
		if (writer != null) writer.println("Calculate scaled score before drop lowest:");
		Multiplication m = new Multiplication(percentageScore, scalingFactorForPass2, mode, writer);
		scaledScoreBeforeDropLowest = m.perform();
		
		return scaledScoreBeforeDropLowest;
	}
	
	public void calculateScaledScoreBeforeDropLowestDifferential(Mode mode, PrintWriter writer) {
		Subtraction s = new Subtraction(scalingFactorForPass2, scaledScoreBeforeDropLowest, mode, writer);
		scaledScoreBeforeDropLowestDifferential = s.perform();
	}
	
	public void calculateScalingFactorForPass3(BigDecimal factorX, Mode mode, PrintWriter writer) {
		if (writer != null) writer.println("Calculate scaling factor for pass 3:");
		Multiplication m = new Multiplication(idealPercentOverall, factorX, mode, writer);
		scalingFactorForPass3 = m.perform();
	}
	
	public BigDecimal calculateScaledScoreAfterDropLowest(Mode mode, PrintWriter writer) {
		if (writer != null) writer.println("Calculate scaled score after drop lowest:");
		Multiplication m = new Multiplication(percentageScore, scalingFactorForPass3, mode, writer);
		scaledScoreAfterDropLowest = m.perform();
		return scaledScoreAfterDropLowest;
	}

	public BigDecimal getScaledScoreBeforeDropLowestDifferential() {
		
		//if (isExcused)
		//	return BigDecimal.valueOf(0d);
		
		
		return scaledScoreBeforeDropLowestDifferential;
	}

	public BigDecimal getActualPercentOverall() {
		return actualPercentOverall;
	}*/

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
