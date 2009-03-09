package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.sakaiproject.gradebook.gwt.sakai.calculations.Calculation.Mode;

public class CategoryCalculationUnit {

	// This is the desired weight for the category that the user enters
	private BigDecimal categoryWeightTotal;
	// This is the weight for the category based on the items that have been graded and not excused
	private BigDecimal categoryActualPercentTotal;
	// This is the actual weight for the category based on the items that have been graded, not excused, and not dropped
	private BigDecimal adjustedCategoryActualPercentTotal;
	
	private BigDecimal factorXPass1;
	private BigDecimal factorXPass2;
	
	private Integer dropLowest;
	
	private List<GradeRecordCalculationUnit> unitsToDrop;
	
	public CategoryCalculationUnit(BigDecimal categoryWeightTotal, Integer dropLowest) {
		this.categoryWeightTotal = categoryWeightTotal;
		this.dropLowest = dropLowest;
	}
	
	
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
		if (dropLowest != null && dropLowest.intValue() > 0 && sizeOf >= dropLowest.intValue()) {
			
			Collections.sort(units, new Comparator<GradeRecordCalculationUnit>() {

				public int compare(GradeRecordCalculationUnit o1, GradeRecordCalculationUnit o2) {
					if (o2 == null || o1 == null)
						return 0;
					if (o2.getScaledScoreBeforeDropLowestDifferential() == null || o1.getScaledScoreBeforeDropLowestDifferential() == null)
						return 0;
					
					return o2.getScaledScoreBeforeDropLowestDifferential().compareTo(o1.getScaledScoreBeforeDropLowestDifferential());
				}
				
			});
			
			unitsToDrop = units.subList(0, dropLowest.intValue());
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
	}
	
	
}
