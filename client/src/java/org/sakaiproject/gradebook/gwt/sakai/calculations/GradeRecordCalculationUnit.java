package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.io.PrintWriter;
import java.math.BigDecimal;

import org.sakaiproject.gradebook.gwt.sakai.calculations.Calculation.Mode;

public class GradeRecordCalculationUnit {

	private BigDecimal pointsReceived;
	private BigDecimal pointsPossible;
	private BigDecimal percentOfCategory;
	
	// This is simply the points received divided by the points possible
	private BigDecimal percentageScore;
	// This is the straightforward product of assignment weight and category weight
	private BigDecimal idealPercentOverall;
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
	private BigDecimal scaledScoreAfterDropLowest;
	
	
	public GradeRecordCalculationUnit(BigDecimal pointsReceived, BigDecimal pointsPossible, BigDecimal percentOfCategory) {
		this.pointsReceived = pointsReceived;
		this.pointsPossible = pointsPossible;
		this.percentOfCategory = percentOfCategory;
	}
	
	public void calculatePercentageScore(Mode mode, PrintWriter writer) {
		if (writer != null) writer.println("Calculate percentage score: ");
		Division d = new Division(pointsReceived, pointsPossible, mode, writer);
		percentageScore = d.perform();
	}
	
	public void calculateIdealPercentOverall(BigDecimal categoryTotalWeight, Mode mode, PrintWriter writer) {
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
		return scaledScoreBeforeDropLowestDifferential;
	}

	public BigDecimal getPercentOfCategory() {
		return percentOfCategory;
	}

	public BigDecimal getActualPercentOverall() {
		return actualPercentOverall;
	}
	
	
}
