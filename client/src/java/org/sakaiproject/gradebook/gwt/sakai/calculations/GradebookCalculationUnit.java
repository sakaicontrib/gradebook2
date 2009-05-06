package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.AppConstants;

public class GradebookCalculationUnit {

	final static BigDecimal BIG_DECIMAL_100 = new BigDecimal("100.0");
	
	private Map<String, CategoryCalculationUnit> categoryUnitMap;
	
	public GradebookCalculationUnit() {
		
	}
	
	public GradebookCalculationUnit(Map<String, CategoryCalculationUnit> categoryUnitMap) {
		this.categoryUnitMap = categoryUnitMap;	
	}
	
	
	public BigDecimal calculatePointsBasedCourseGrade(List<GradeRecordCalculationUnit> units) {
		BigDecimal courseGrade = null;
		
		BigDecimal[] result = sumPoints(units, 0);
		
		if (result != null) {
			BigDecimal sumPoints = result[0];
			BigDecimal sumPointsPossible = result[1];
			BigDecimal sumExtraCreditPoints = result[2];
			
			if (sumPoints != null && sumPointsPossible != null) {
			
				// If the points possible add up to zero, then return zero
				if (sumPointsPossible.compareTo(BigDecimal.ZERO) == 0)
					return BigDecimal.ZERO.setScale(AppConstants.SCALE);
				
				
				if (sumExtraCreditPoints != null)
					sumPoints = sumPoints.add(sumExtraCreditPoints);
				
				if (sumPoints.compareTo(BigDecimal.ZERO) == 0) 
					return BigDecimal.ZERO.setScale(AppConstants.SCALE);
					
				
				BigDecimal percentageScore = sumPoints.divide(sumPointsPossible, RoundingMode.HALF_EVEN);
			
				courseGrade = percentageScore;
			}
			
		}
		
		if (courseGrade != null)
			courseGrade = courseGrade.multiply(BIG_DECIMAL_100);
		
		return courseGrade;
	}
	
	public BigDecimal calculatePointsBasedCourseGrade(Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap) {
		BigDecimal courseGrade = null;
		
		BigDecimal sumPoints = null;
		BigDecimal sumPointsPossible = null;
		BigDecimal sumExtraCreditPoints = null;
		
		// First pass calculates all the grades
		for (String categoryKey : categoryUnitMap.keySet()) {
			CategoryCalculationUnit categoryUnit = categoryUnitMap.get(categoryKey);
			List<GradeRecordCalculationUnit> units = categoryGradeUnitListMap.get(categoryKey);
			
			BigDecimal[] categoryResult = sumPoints(units, categoryUnit.getDropLowest());
		
			BigDecimal categoryPointsReceived = categoryResult[0];
			BigDecimal categoryPointsPossible = categoryResult[1];
			BigDecimal categoryExtraCreditPoints = categoryResult[2];
			
			if (categoryPointsPossible != null && categoryPointsReceived != null) {
				
				// For extra credit categories, we simply add up all the points for that category without adding the points possible to that sum			
				if (categoryUnit.isExtraCredit()) {
					if (sumExtraCreditPoints == null)
						sumExtraCreditPoints = BigDecimal.ZERO.setScale(AppConstants.SCALE);
					
					sumExtraCreditPoints = sumExtraCreditPoints.add(categoryPointsReceived);
					sumExtraCreditPoints = sumExtraCreditPoints.add(categoryExtraCreditPoints);
				} else {
					if (sumPoints == null)
						sumPoints = BigDecimal.ZERO.setScale(AppConstants.SCALE);
					
					if (sumPointsPossible == null)
						sumPointsPossible = BigDecimal.ZERO.setScale(AppConstants.SCALE);
					
					sumPoints = sumPoints.add(categoryPointsReceived);
					sumPointsPossible = sumPointsPossible.add(categoryPointsPossible);
					
					if (categoryExtraCreditPoints != null) {
						if (sumExtraCreditPoints == null)
							sumExtraCreditPoints = BigDecimal.ZERO.setScale(AppConstants.SCALE);
						
						sumExtraCreditPoints = sumExtraCreditPoints.add(categoryExtraCreditPoints);
					}
				}
			}
			
		}
		
		if (sumPoints != null && sumPointsPossible != null) {
			
			// If the points possible add up to zero, then return zero
			if (sumPointsPossible.compareTo(BigDecimal.ZERO) == 0)
				return BigDecimal.ZERO.setScale(AppConstants.SCALE);
			
			
			if (sumExtraCreditPoints != null)
				sumPoints = sumPoints.add(sumExtraCreditPoints);
			
			if (sumPoints.compareTo(BigDecimal.ZERO) == 0) 
				return BigDecimal.ZERO.setScale(AppConstants.SCALE);
				
			
			BigDecimal percentageScore = sumPoints.divide(sumPointsPossible, RoundingMode.HALF_EVEN);
		
			courseGrade = percentageScore;
		}
		
		if (courseGrade != null)
			courseGrade = courseGrade.multiply(BIG_DECIMAL_100);
		
		return courseGrade;
	}
	
	public BigDecimal calculateWeightedCourseGrade(Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap) {
		
		BigDecimal categoryWeightDesiredSum = BigDecimal.ZERO.setScale(AppConstants.SCALE);
		BigDecimal categoryWeightSum = BigDecimal.ZERO.setScale(AppConstants.SCALE);
		BigDecimal courseGrade = null;
		BigDecimal extraCreditSum = BigDecimal.ZERO.setScale(AppConstants.SCALE);

		// First pass calculates all the grades
		for (String categoryKey : categoryUnitMap.keySet()) {
			CategoryCalculationUnit categoryUnit = categoryUnitMap.get(categoryKey);
			List<GradeRecordCalculationUnit> units = categoryGradeUnitListMap.get(categoryKey);
			
			BigDecimal categoryGrade = categoryUnit.calculate(units);	
			BigDecimal categoryWeight = categoryUnit.getCategoryWeightTotal();
			
			if (categoryWeight != null && !categoryUnit.isExtraCredit())
				categoryWeightDesiredSum = categoryWeightDesiredSum.add(categoryWeight);
			
			if (categoryGrade != null) {
				if (categoryUnit.isExtraCredit()) {
					extraCreditSum = extraCreditSum.add(categoryWeight);
				} else {
					categoryWeightSum = categoryWeightSum.add(categoryWeight);
				}

			} // if
			
		} // for
		
		BigDecimal ratio = BigDecimal.ONE;
			
		if (categoryWeightSum.compareTo(BigDecimal.ZERO) != 0) 
			ratio = categoryWeightDesiredSum.divide(categoryWeightSum, RoundingMode.HALF_EVEN);
		
		for (String categoryKey : categoryUnitMap.keySet()) {
			CategoryCalculationUnit categoryUnit = categoryUnitMap.get(categoryKey);
			
			BigDecimal categoryGrade = categoryUnit.getCategoryGrade();
			BigDecimal categoryWeight = categoryUnit.getCategoryWeightTotal();
			
			if (categoryGrade != null) {

				if (categoryGrade.compareTo(BigDecimal.ONE) > 0)
					categoryGrade = BigDecimal.ONE.setScale(AppConstants.SCALE);
			
				if (categoryGrade.compareTo(BigDecimal.ZERO) < 0)
					categoryGrade = BigDecimal.ZERO.setScale(AppConstants.SCALE);
	
				if (categoryWeight != null) {
					categoryWeight = categoryWeight.multiply(ratio);
					
					BigDecimal contributionToCourseGrade = categoryGrade.multiply(categoryWeight);
	
					if (courseGrade == null)
						courseGrade = BigDecimal.ZERO;
					
					courseGrade = courseGrade.add(contributionToCourseGrade);
				} // if 
			} // if
			
		} // for
		
		
		if (categoryWeightSum == null)
			return null;


		if (courseGrade != null)
			courseGrade = courseGrade.multiply(BIG_DECIMAL_100);

		return courseGrade;
	}
	
	
	
	private BigDecimal[] sumPoints(List<GradeRecordCalculationUnit> units, int dropLowest) {
		
		BigDecimal[] result = new BigDecimal[3];
		
		BigDecimal sumPoints = null;
		BigDecimal sumPointsPossible = null;
		BigDecimal sumExtraCreditPoints = null;

		if (units != null && !units.isEmpty()) {

			boolean doCalculateDropLowest = dropLowest > 0;
			List<GradeRecordCalculationUnit> orderingList = null;
			
			if (doCalculateDropLowest)
				orderingList = new ArrayList<GradeRecordCalculationUnit>();
			
			for (GradeRecordCalculationUnit unit : units) {

				if (unit.isExcused())
					continue;
				
				BigDecimal pointsReceived = unit.getPointsReceived();
				BigDecimal pointsPossible = unit.getPointsPossible();
				
				if (pointsReceived != null) {
					if (sumPoints == null)
						sumPoints = BigDecimal.ZERO.setScale(AppConstants.SCALE);
					
					sumPoints = sumPoints.add(pointsReceived);
				}
				
				if (pointsPossible != null && !unit.isExtraCredit()) {
					
					if (unit.isExtraCredit()) {
						if (sumExtraCreditPoints == null)
							sumExtraCreditPoints = BigDecimal.ZERO.setScale(AppConstants.SCALE);
						
						sumExtraCreditPoints = sumExtraCreditPoints.add(pointsPossible);
					} else {
						if (sumPointsPossible == null)
							sumPointsPossible = BigDecimal.ZERO.setScale(AppConstants.SCALE);
						
						sumPointsPossible = sumPointsPossible.add(pointsPossible);
					}
				}
				
				if (doCalculateDropLowest && !unit.isExtraCredit()) {	
					unit.calculateRawDifference();
					
					if (unit.getPointsDifference() != null)
						orderingList.add(unit);
				}
				
				
			} // for 
		
			if (! doCalculateDropLowest) {
				result[0] = sumPoints;
				result[1] = sumPointsPossible;
				result[2] = sumExtraCreditPoints;
				
				return result;
			}
			
			// There's no point in dropping the lowest scores if there are no scores
			if (sumPoints == null || sumPointsPossible == null)
				return result;
			
			// This will sort the ordering list from highest to lowest loss, so we can simply drop the scores at the beginning 
			Collections.sort(orderingList, new Comparator<GradeRecordCalculationUnit>() {

				public int compare(GradeRecordCalculationUnit o1, GradeRecordCalculationUnit o2) {
					BigDecimal difference1 = o1.getPointsDifference();
					BigDecimal difference2 = o2.getPointsDifference();
					
					if (difference1 == null || difference2 == null)
						return 0;
					
					return difference2.compareTo(difference1);
				}
				
			});
		
		
			// Obviously we can't drop more scores than we have
			if (dropLowest > orderingList.size())
				dropLowest = orderingList.size();
			
			// Now just go thru the list and subtract the dropped ones from both points possible and points received . . . this means they won't be included 
			// in the calculation
			for (int i=0;i<dropLowest;i++) {
				GradeRecordCalculationUnit entry = orderingList.get(i);
				
				BigDecimal pointsReceived = entry.getPointsReceived();
				BigDecimal pointsPossible = entry.getPointsPossible();
				
				if (pointsReceived != null && pointsPossible != null) {
					sumPoints = sumPoints.subtract(pointsReceived);
					sumPointsPossible = sumPointsPossible.subtract(pointsPossible);
				}
			}
		
			result[0] = sumPoints;
			result[1] = sumPointsPossible;
			result[2] = sumExtraCreditPoints;

		} // if
		
		return result;
	}
	
	
	/*public static void main(String[] args) {
		
		long start = System.nanoTime();
		CategoryCalculationUnit essayUnit = new CategoryCalculationUnit(new BigDecimal(".60"), Integer.valueOf(1));
		CategoryCalculationUnit hwUnit = new CategoryCalculationUnit(new BigDecimal(".40"), Integer.valueOf(0));
		
		List<GradeRecordCalculationUnit> essayUnits = new ArrayList<GradeRecordCalculationUnit>();
		essayUnits.add(new GradeRecordCalculationUnit(new BigDecimal("4.0"), 
				new BigDecimal("5.0"), new BigDecimal(".20")));
		essayUnits.add(new GradeRecordCalculationUnit(null, 
				new BigDecimal("9.0"), new BigDecimal(".20")));
		essayUnits.add(new GradeRecordCalculationUnit(new BigDecimal("6.0"), 
				new BigDecimal("10.0"), new BigDecimal(".10")));
		essayUnits.add(new GradeRecordCalculationUnit(new BigDecimal("8.0"), 
				new BigDecimal("10.0"), new BigDecimal(".10")));
		essayUnits.add(new GradeRecordCalculationUnit(new BigDecimal("19.0"), 
				new BigDecimal("20.0"), new BigDecimal(".40")));
		
		PrintWriter writer = null; //new PrintWriter(System.out);
		
		Mode mode = Mode.DO;
		
		
		BigDecimal essaysGrade = essayUnit.calculateGrade(essayUnits, mode, writer);
		
		if (writer != null) writer.flush();
		
		List<GradeRecordCalculationUnit> hwUnits = new ArrayList<GradeRecordCalculationUnit>();
		hwUnits.add(new GradeRecordCalculationUnit(new BigDecimal("10.0"), 
				new BigDecimal("10.0"), new BigDecimal(".30")));
		hwUnits.add(new GradeRecordCalculationUnit(new BigDecimal("10.0"), 
				new BigDecimal("10.0"), new BigDecimal(".30")));
		hwUnits.add(new GradeRecordCalculationUnit(new BigDecimal("10.0"), 
				new BigDecimal("10.0"), new BigDecimal(".40")));
		
		BigDecimal hwGrade = hwUnit.calculateGrade(hwUnits, mode, writer);
		
		long end = System.nanoTime();
		
		System.out.println("Elapsed: " + (end-start));
		
		if (writer != null) writer.flush();
		System.out.println("Essays: " + essaysGrade);
		System.out.println("Homework: " + hwGrade);
		
		BigDecimal total = essaysGrade.add(hwGrade);
		
		System.out.println("Total: " + total);
	
		if (writer != null) writer.close();
	}*/
	
}
