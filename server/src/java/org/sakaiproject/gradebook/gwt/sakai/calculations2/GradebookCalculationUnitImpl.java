package org.sakaiproject.gradebook.gwt.sakai.calculations2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.AppConstants;


public class GradebookCalculationUnitImpl extends BigDecimalCalculationsWrapper implements GradebookCalculationUnit {


	final static BigDecimal BIG_DECIMAL_100 = new BigDecimal("100");

	private Map<String, CategoryCalculationUnit> categoryUnitMap;

	public GradebookCalculationUnitImpl() {

	}

	public GradebookCalculationUnitImpl(Map<String, CategoryCalculationUnit> categoryUnitMap) {
		this.categoryUnitMap = categoryUnitMap;	
	}


	public BigDecimal calculatePointsBasedCourseGrade(List<GradeRecordCalculationUnit> units, BigDecimal totalGradebookPoints, boolean isExtraCreditScaled) {
		BigDecimal courseGrade = null;

		BigDecimal[] result = sumPoints(units, 0);

		if (result != null) {
			BigDecimal sumPoints = result[0];
			BigDecimal sumPointsPossible = result[1];
			BigDecimal sumExtraCreditPoints = result[2];

			if (sumPoints != null && sumPointsPossible != null) {

				// If the points possible add up to zero, then return zero
				if (sumPointsPossible.compareTo(BigDecimal.ZERO) == 0)
					return BigDecimal.ZERO;

				if (sumPoints.compareTo(BigDecimal.ZERO) == 0) 
					return BigDecimal.ZERO;

				BigDecimal percentageScore = divide(sumPoints, sumPointsPossible);
				//BigDecimal percentageScore = sumPoints.divide(sumPointsPossible, GradeCalculations.MATH_CONTEXT);

				if (sumExtraCreditPoints != null)
					percentageScore = add(percentageScore, scaleExtraCreditPoints(sumExtraCreditPoints, sumPointsPossible, totalGradebookPoints, isExtraCreditScaled));
					//percentageScore = percentageScore.add(scaleExtraCreditPoints(sumExtraCreditPoints, sumPointsPossible, totalGradebookPoints, isExtraCreditScaled));
				
				courseGrade = percentageScore;
			}

		}

		if (courseGrade != null)
			courseGrade = multiply(courseGrade, BIG_DECIMAL_100);
			//courseGrade = courseGrade.multiply(BIG_DECIMAL_100);

		return courseGrade;
	}

	public BigDecimal calculatePointsBasedCourseGrade(Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap, BigDecimal totalGradebookPoints, boolean isExtraCreditScaled) {
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


			// For extra credit categories, we simply add up all the points for that category without adding the points possible to that sum			
			if (categoryUnit.isExtraCredit()) {
				if (sumExtraCreditPoints == null)
					sumExtraCreditPoints = BigDecimal.ZERO;

				if (categoryPointsReceived != null)
					sumExtraCreditPoints = add(sumExtraCreditPoints, categoryPointsReceived);
					//sumExtraCreditPoints = sumExtraCreditPoints.add(categoryPointsReceived);
					
				if (categoryExtraCreditPoints != null)
					sumExtraCreditPoints = add(sumExtraCreditPoints, categoryExtraCreditPoints);
					//sumExtraCreditPoints = sumExtraCreditPoints.add(categoryExtraCreditPoints);
					
			} else {
		
				if (categoryPointsReceived != null) {
					if (sumPoints == null)
						sumPoints = BigDecimal.ZERO;
					
					sumPoints = add(sumPoints, categoryPointsReceived);
					//sumPoints = sumPoints.add(categoryPointsReceived);
				}
				
				if (categoryPointsPossible != null) {
					if (sumPointsPossible == null)
						sumPointsPossible = BigDecimal.ZERO;
					
					sumPointsPossible = add(sumPointsPossible, categoryPointsPossible);
					//sumPointsPossible = sumPointsPossible.add(categoryPointsPossible);
				}
				
				if (categoryExtraCreditPoints != null) {
					if (sumExtraCreditPoints == null)
						sumExtraCreditPoints = BigDecimal.ZERO;

					if (categoryExtraCreditPoints != null)
						sumExtraCreditPoints = add(sumExtraCreditPoints, categoryExtraCreditPoints);
						//sumExtraCreditPoints = sumExtraCreditPoints.add(categoryExtraCreditPoints);
				}
			}


		}

		if (sumPoints != null && sumPointsPossible != null) {

			// If the points possible add up to zero, then return zero
			if (sumPointsPossible.compareTo(BigDecimal.ZERO) == 0)
				return BigDecimal.ZERO;

			if (sumPoints.compareTo(BigDecimal.ZERO) == 0) 
				return BigDecimal.ZERO;


			BigDecimal percentageScore = divide(sumPoints, sumPointsPossible);
			//BigDecimal percentageScore = sumPoints.divide(sumPointsPossible, GradeCalculationsOOImpl.MATH_CONTEXT);

			if (sumExtraCreditPoints != null)
				percentageScore = add(percentageScore, scaleExtraCreditPoints(sumExtraCreditPoints, sumPointsPossible, totalGradebookPoints, isExtraCreditScaled));
				//percentageScore = percentageScore.add(scaleExtraCreditPoints(sumExtraCreditPoints, sumPointsPossible, totalGradebookPoints, isExtraCreditScaled));
			
			courseGrade = percentageScore;
		}

		if (courseGrade != null)
			courseGrade = multiply(courseGrade, BIG_DECIMAL_100);
			//courseGrade = courseGrade.multiply(BIG_DECIMAL_100);

		return courseGrade;
	}

	public BigDecimal calculateWeightedCourseGrade(Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap, BigDecimal totalGradebookPoints, boolean isExtraCreditScaled) {

		BigDecimal categoryWeightDesiredSum = BigDecimal.ZERO;
		BigDecimal categoryWeightSum = null; 
		BigDecimal courseGrade = null;
		BigDecimal extraCreditSum = BigDecimal.ZERO;

		// First pass calculates all the grades
		for (String categoryKey : categoryUnitMap.keySet()) {
			CategoryCalculationUnit categoryUnit = categoryUnitMap.get(categoryKey);
			List<GradeRecordCalculationUnit> units = categoryGradeUnitListMap.get(categoryKey);

			//if (units == null)
			//	continue;
			
			BigDecimal categoryGrade = null;
			BigDecimal categoryWeight = categoryUnit.getCategoryWeightTotal();
			
			if (categoryUnit.isPointsWeighted()) {
				BigDecimal[] categoryResult = sumPoints(units, categoryUnit.getDropLowest());

				BigDecimal categoryPointsReceived = categoryResult[0];
				BigDecimal categoryPointsPossible = categoryResult[1];
				BigDecimal categoryExtraCreditPoints = categoryResult[2];
				BigDecimal totalCategoryPoints = categoryUnit.getTotalCategoryPoints();
				
				if (categoryUnit.isExtraCredit()) {
					if (categoryExtraCreditPoints != null && totalGradebookPoints != null)
						categoryGrade = divide(categoryExtraCreditPoints, totalGradebookPoints);
						//categoryGrade = categoryExtraCreditPoints.divide(totalGradebookPoints, GradeCalculationsOOImpl.MATH_CONTEXT);
				} else {
					if (categoryPointsReceived != null && categoryPointsPossible != null) {
						
						categoryGrade = divide(categoryPointsReceived, categoryPointsPossible);
						//categoryGrade = categoryPointsReceived.divide(categoryPointsPossible, GradeCalculationsOOImpl.MATH_CONTEXT);
						
						if (categoryExtraCreditPoints != null) {
							categoryGrade = add(categoryGrade, scaleExtraCreditPoints(categoryExtraCreditPoints, categoryPointsPossible, totalCategoryPoints, isExtraCreditScaled));
							//categoryGrade = categoryGrade.add(scaleExtraCreditPoints(categoryExtraCreditPoints, categoryPointsPossible, totalCategoryPoints, isExtraCreditScaled));
						}
						
					}
				}
				
				if (categoryGrade != null)
					categoryUnit.setCategoryGrade(categoryGrade);
				
			} else {
				categoryGrade = categoryUnit.calculate(units, isExtraCreditScaled);	
			} // else
			
			if (categoryWeight != null && !categoryUnit.isExtraCredit())
				categoryWeightDesiredSum = add(categoryWeightDesiredSum,categoryWeight);
				//categoryWeightDesiredSum = categoryWeightDesiredSum.add(categoryWeight);
	
			if (categoryGrade != null && categoryWeight != null) {
				if (categoryUnit.isExtraCredit()) {
					extraCreditSum = add(extraCreditSum, categoryWeight);
					//extraCreditSum = extraCreditSum.add(categoryWeight);
				} else {
					if (categoryWeightSum == null)
						categoryWeightSum = BigDecimal.ZERO;
					categoryWeightSum = add(categoryWeightSum, categoryWeight);
					//categoryWeightSum = categoryWeightSum.add(categoryWeight);
				} // else
			} // if
		} // for

		if (categoryWeightSum == null)
			return null;

		BigDecimal ratio = BigDecimal.ONE;

		if (categoryWeightSum.compareTo(BigDecimal.ZERO) != 0) 
			ratio = divide(categoryWeightDesiredSum, categoryWeightSum);
			//ratio = categoryWeightDesiredSum.divide(categoryWeightSum, RoundingMode.HALF_EVEN);

		for (String categoryKey : categoryUnitMap.keySet()) {
			CategoryCalculationUnit categoryUnit = categoryUnitMap.get(categoryKey);

			BigDecimal categoryGrade = categoryUnit.getCategoryGrade();
			BigDecimal categoryWeight = categoryUnit.getCategoryWeightTotal();

			if (categoryGrade != null) {

				BigDecimal multiplicand = ratio;

				if (categoryUnit.isExtraCredit())
					multiplicand = BigDecimal.ONE;

				if (categoryGrade.compareTo(BigDecimal.ONE) > 0)
					categoryGrade = BigDecimal.ONE;

				if (categoryGrade.compareTo(BigDecimal.ZERO) < 0)
					categoryGrade = BigDecimal.ZERO;

				if (categoryWeight != null) {
					categoryWeight = multiply(categoryWeight, multiplicand);
					//categoryWeight = categoryWeight.multiply(multiplicand);

					BigDecimal contributionToCourseGrade = multiply(categoryGrade, categoryWeight);
					//BigDecimal contributionToCourseGrade = categoryGrade.multiply(categoryWeight);
					
					if (courseGrade == null)
						courseGrade = BigDecimal.ZERO;

					courseGrade = add(courseGrade, contributionToCourseGrade);
					//courseGrade = courseGrade.add(contributionToCourseGrade);
				} // if 
			} // if

		} // for


		if (courseGrade != null)
			courseGrade = multiply(courseGrade, BIG_DECIMAL_100);
			//courseGrade = courseGrade.multiply(BIG_DECIMAL_100);

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
					if (unit.isExtraCredit()) {
						if (sumExtraCreditPoints == null)
							sumExtraCreditPoints = BigDecimal.ZERO;

						sumExtraCreditPoints = add(sumExtraCreditPoints, pointsReceived);
						//sumExtraCreditPoints = sumExtraCreditPoints.add(pointsReceived);
					} else {
						if (sumPoints == null)
							sumPoints = BigDecimal.ZERO;
	
						sumPoints = add(sumPoints, pointsReceived);
						//sumPoints = sumPoints.add(pointsReceived);
					}
				}

				if (pointsPossible != null && !unit.isExtraCredit()) {
					if (sumPointsPossible == null)
						sumPointsPossible = BigDecimal.ZERO;

					sumPointsPossible = add(sumPointsPossible, pointsPossible);
					//sumPointsPossible = sumPointsPossible.add(pointsPossible);
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

				entry.setDropped(true);

				if (pointsReceived != null && pointsPossible != null) {
					sumPoints = subtract(sumPoints, pointsReceived);
					//sumPoints = sumPoints.subtract(pointsReceived, GradeCalculations.MATH_CONTEXT);
					sumPointsPossible = subtract(sumPointsPossible, pointsPossible);
					//sumPointsPossible = sumPointsPossible.subtract(pointsPossible, GradeCalculations.MATH_CONTEXT);

					// This is a special case where the sumPoints is non-null because the drop
					// lowest was not taken into account earlier, but it should be null
					if (sumPoints.compareTo(BigDecimal.ZERO) == 0 && 
							sumPointsPossible.compareTo(BigDecimal.ZERO) == 0) {
						sumPoints = null;
						sumPointsPossible = null;
					}
				}
			}

			result[0] = sumPoints;
			result[1] = sumPointsPossible;
			result[2] = sumExtraCreditPoints;

		} // if

		return result;
	}
	
	private BigDecimal scaleExtraCreditPoints(BigDecimal extraCreditPoints, BigDecimal pointsPossible, BigDecimal totalGradebookPoints, boolean isExtraCreditScaled) {
		if (extraCreditPoints == null || pointsPossible == null || extraCreditPoints.compareTo(BigDecimal.ZERO) == 0)
			return BigDecimal.ZERO;
		
		if (isExtraCreditScaled)
			return divide(extraCreditPoints, totalGradebookPoints);
 			//return extraCreditPoints.divide(totalGradebookPoints, RoundingMode.HALF_EVEN);
		
 		return divide(extraCreditPoints, pointsPossible);
 		//return extraCreditPoints.divide(pointsPossible, RoundingMode.HALF_EVEN);
	}
}
