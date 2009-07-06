package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;

public class CategoryCalculationUnit {

	private BigDecimal categoryGrade;
	
	// This is the desired weight for the category that the user enters
	private BigDecimal categoryWeightTotal;
	// This is the weight for the category based on the items that have been graded and not excused
	/*private BigDecimal categoryActualPercentTotal;
	// This is the actual weight for the category based on the items that have been graded, not excused, and not dropped
	private BigDecimal adjustedCategoryActualPercentTotal;
	
	private BigDecimal factorXPass1;
	private BigDecimal factorXPass2;*/
	
	private int dropLowest;
	private boolean isExtraCredit;
	
	private List<GradeRecordCalculationUnit> unitsToDrop;
	
	public CategoryCalculationUnit(BigDecimal categoryWeightTotal, Integer dropLowest, Boolean extraCredit) {
		this.categoryWeightTotal = categoryWeightTotal;
		this.dropLowest = dropLowest == null ? 0 : dropLowest.intValue();
		this.isExtraCredit = extraCredit == null ? false : extraCredit.booleanValue();
		this.unitsToDrop = new LinkedList<GradeRecordCalculationUnit>();
	}
	
	
	public BigDecimal calculate(List<GradeRecordCalculationUnit> units) {	
		
		BigDecimal sumScores = sumScaledScores(units);
		
		// When drop lowest is not set, the calculation is very straightforward
		if (dropLowest <= 0) {
			categoryGrade = sumScores;
			return categoryGrade;
		}
		
		// Note that drop lowest only works when all the scores for this category are equally weighted
		Collections.sort(units, new Comparator<GradeRecordCalculationUnit>() {

			public int compare(GradeRecordCalculationUnit o1, GradeRecordCalculationUnit o2) {
				if (o2 == null || o1 == null)
					return 0;

				if (o1.getScaledScore() == null || o2.getScaledScore() == null)
					return 0;
				
				return o1.getScaledScore().compareTo(o2.getScaledScore());
			}
			
		});
		
		int numberOfUnitsDropped = 0;
		
		List<GradeRecordCalculationUnit> unitsToCount = new ArrayList<GradeRecordCalculationUnit>();
		
		// We don't want to include excused records in our determination of drop lowest items
		for (GradeRecordCalculationUnit unit : units) {
			if (numberOfUnitsDropped < dropLowest && !unit.isExcused() && !unit.isExtraCredit()) {
				unit.setDropped(true);
				unitsToDrop.add(unit);
				numberOfUnitsDropped++;
			} else {
				unit.setDropped(false);
				unitsToCount.add(unit);
			}
		}

		categoryGrade = sumScaledScores(unitsToCount);
		return categoryGrade;
	}
	
	
	private BigDecimal sumScaledScores(List<GradeRecordCalculationUnit> units) {
		BigDecimal sum = sumUnitWeights(units, false);
		
		if (sum == null)
			return null;
		
		BigDecimal ratio = BigDecimal.ONE;
		
		if (sum.compareTo(BigDecimal.ZERO) != 0) 
			ratio = BigDecimal.ONE.setScale(AppConstants.SCALE).divide(sum, RoundingMode.HALF_EVEN);
		
		BigDecimal sumScores = null;
		
		for (GradeRecordCalculationUnit unit : units) {

			if (unit.isExcused())
				continue;
			
			BigDecimal multiplicand = ratio;
			
			if (unit.isExtraCredit())
				multiplicand = BigDecimal.ONE;
			
			BigDecimal scaledItemWeight = findScaledItemWeight(multiplicand, unit.getPercentOfCategory());
			BigDecimal scaledScore = unit.calculate(scaledItemWeight);
			
			if (scaledScore != null) {
				if (sumScores == null)
					sumScores = BigDecimal.ZERO;
				
				sumScores = sumScores.add(scaledScore);
			}
		}
				
		return sumScores;
	}
	
	
	private BigDecimal findScaledItemWeight(BigDecimal ratio, BigDecimal itemWeight) {
		if (ratio == null || itemWeight == null)
			return null;
		
		return ratio.multiply(itemWeight);
	}
	
	private BigDecimal sumUnitWeights(List<GradeRecordCalculationUnit> units, boolean doExtraCredit) {
		
		BigDecimal sumUnitWeight = null;
		
		if (units != null) {
			for (GradeRecordCalculationUnit unit : units) {
				
				if (unit.isExtraCredit() && !isExtraCredit)
					continue;
				
				if (unit.isExcused())
					continue;
				
				BigDecimal itemWeight = unit.getPercentOfCategory();
				
				if (itemWeight != null) {
					if (sumUnitWeight == null) 
						sumUnitWeight = BigDecimal.ZERO;
						
					sumUnitWeight = sumUnitWeight.add(itemWeight);
				}
			}
		}
		
		return sumUnitWeight;
	}

	/*
	public BigDecimal calculateGrade(List<GradeRecordCalculationUnit> units, Mode mode, PrintWriter writer) {
		
		pass1(units, mode, writer);
		
		pass2(units, mode, writer);
			
		return pass3(units, mode, writer);
	}
	
	public void pass1(List<GradeRecordCalculationUnit> units, Mode mode, PrintWriter writer) {
		
		Sum sum = new Sum(mode, writer);
		for (GradeRecordCalculationUnit unit : units) {
			unit.calculatePercentageScore(mode, writer);
			unit.calculateIdealPercentOverall(categoryWeightTotal, mode, writer);
			unit.calculateGradedPercentOverall(mode, writer);
			BigDecimal actualPercent = unit.getActualPercentOverall(mode, writer);
			
			if (!unit.isExcused())
				sum.add(actualPercent);
		}
		
		categoryActualPercentTotal = sum.calculate();
		
		if (writer != null) writer.println("Calculating factorXPass1");
		Division d = new Division(categoryWeightTotal, categoryActualPercentTotal, mode, writer);
		
		factorXPass1 = d.perform();
	}
	
	public BigDecimal pass2NoDropLowest(List<GradeRecordCalculationUnit> units, Mode mode, PrintWriter writer) {
		Sum s = new Sum(mode, writer);
		for (GradeRecordCalculationUnit unit : units) {
			unit.calculateScalingFactorForPass2(factorXPass1, mode, writer);
			s.add(unit.calculateScaledScoreBeforeDropLowest(mode, writer));
		}
		
		return s.perform();
	}
	
	public List<GradeRecordCalculationUnit> pass2(List<GradeRecordCalculationUnit> units, Mode mode, PrintWriter writer) {
		
		for (GradeRecordCalculationUnit unit : units) {
			unit.calculateScalingFactorForPass2(factorXPass1, mode, writer);
			unit.calculateScaledScoreBeforeDropLowest(mode, writer);
			unit.calculateScaledScoreBeforeDropLowestDifferential(mode, writer);
		}
		
		int sizeOf = units.size();
		
		if (writer != null) writer.println("Calculating factorXPass2");
		if (dropLowest > 0) { // && sizeOf >= dropLowest) {
			
			Collections.sort(units, new Comparator<GradeRecordCalculationUnit>() {

				public int compare(GradeRecordCalculationUnit o1, GradeRecordCalculationUnit o2) {
					if (o2 == null || o1 == null)
						return 0;

					if (o2.getScaledScoreBeforeDropLowestDifferential() == null || o1.getScaledScoreBeforeDropLowestDifferential() == null)
						return 0;
					
					return o2.getScaledScoreBeforeDropLowestDifferential().compareTo(o1.getScaledScoreBeforeDropLowestDifferential());
				}
				
			});
			
			int numberOfUnitsDropped = 0;
			
			// We don't want to include excused records in our determination of drop lowest items
			for (GradeRecordCalculationUnit unit : units) {
				if (numberOfUnitsDropped >= dropLowest)
					break;
				
				if (!unit.isExcused()) {
					unitsToDrop.add(unit);
					numberOfUnitsDropped++;
				}
			}
			
			//unitsToDrop = units.subList(0, dropLowest);
			BigDecimal droppedWeightTotalAdjustment = calculateDroppedUnitsWeightTotal(unitsToDrop, mode, writer);
			
			Subtraction s = new Subtraction(categoryActualPercentTotal, droppedWeightTotalAdjustment, mode, writer);
			
			adjustedCategoryActualPercentTotal = s.perform();
			
			Division d = new Division(categoryWeightTotal, adjustedCategoryActualPercentTotal, mode, writer);
			
			factorXPass2 = d.perform();
		} else {
			factorXPass2 = factorXPass1;
			if (writer != null) writer.println("Set factorXPass2 = factorXPass1 " + factorXPass2);
		}
		
		return unitsToDrop;
	}
	
	public BigDecimal pass3(List<GradeRecordCalculationUnit> units, Mode mode, PrintWriter writer) {
		
		Sum s = new Sum(mode, writer);
		for (GradeRecordCalculationUnit unit : units) {
			if (unitsToDrop == null || !unitsToDrop.contains(unit)) {
				unit.calculateScalingFactorForPass3(factorXPass2, mode, writer);
				if (!unit.isExcused())
					s.add(unit.calculateScaledScoreAfterDropLowest(mode, writer));
			}
		}
		
		return s.perform();
	}
	
	private BigDecimal calculateDroppedUnitsWeightTotal(List<GradeRecordCalculationUnit> droppedUnits, Mode mode, PrintWriter writer) {
		Sum sum = new Sum(mode, writer);
		for (GradeRecordCalculationUnit unit : droppedUnits) {
			sum.add(unit.getActualPercentOverall());
		}
		return sum.perform();
	}*/


	public boolean isExtraCredit() {
		return isExtraCredit;
	}


	public BigDecimal getCategoryWeightTotal() {
		return categoryWeightTotal;
	}


	public BigDecimal getCategoryGrade() {
		return categoryGrade;
	}


	public int getDropLowest() {
		return dropLowest;
	}
	
	
}
