package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.sakai.calculations.CategoryCalculationUnit;
import org.sakaiproject.gradebook.gwt.sakai.calculations.GradeRecordCalculationUnit;
import org.sakaiproject.gradebook.gwt.sakai.calculations.GradebookCalculationUnit;

public class GradebookCalculationUnitImpl extends BigDecimalCalculationsWrapper implements GradebookCalculationUnit {


	final static BigDecimal BIG_DECIMAL_100 = new BigDecimal("100");

	private Map<String, CategoryCalculationUnit> categoryUnitMap;

	// Making default constructor private since this class needs to be instantiated with a scale
	private GradebookCalculationUnitImpl() {
	}
	
	
	public GradebookCalculationUnitImpl(int scale) {
		super(scale);
	}

	public GradebookCalculationUnitImpl(Map<String, CategoryCalculationUnit> categoryUnitMap, int scale) {
		super(scale);
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
				
				if (sumExtraCreditPoints != null)
					percentageScore = add(percentageScore, scaleExtraCreditPoints(sumExtraCreditPoints, sumPointsPossible, totalGradebookPoints, isExtraCreditScaled));
					
				courseGrade = percentageScore;
			}

		}

		if (courseGrade != null)
			courseGrade = multiply(courseGrade, BIG_DECIMAL_100);

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
					
				if (categoryExtraCreditPoints != null)
					sumExtraCreditPoints = add(sumExtraCreditPoints, categoryExtraCreditPoints);
					
			} else {
		
				if (categoryPointsReceived != null) {
					if (sumPoints == null)
						sumPoints = BigDecimal.ZERO;
					
					sumPoints = add(sumPoints, categoryPointsReceived);
				}
				
				if (categoryPointsPossible != null) {
					if (sumPointsPossible == null)
						sumPointsPossible = BigDecimal.ZERO;
					
					sumPointsPossible = add(sumPointsPossible, categoryPointsPossible);
				}
				
				if (categoryExtraCreditPoints != null) {
					if (sumExtraCreditPoints == null)
						sumExtraCreditPoints = BigDecimal.ZERO;

					if (categoryExtraCreditPoints != null)
						sumExtraCreditPoints = add(sumExtraCreditPoints, categoryExtraCreditPoints);
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

			if (sumExtraCreditPoints != null)
				percentageScore = add(percentageScore, scaleExtraCreditPoints(sumExtraCreditPoints, sumPointsPossible, totalGradebookPoints, isExtraCreditScaled));
				
			courseGrade = percentageScore;
		}

		if (courseGrade != null)
			courseGrade = multiply(courseGrade, BIG_DECIMAL_100);
			
		return courseGrade;
	}

	public BigDecimal calculateWeightedCourseGrade(Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap, Map<String, Boolean> hasCategoryManuallyEqualWeightedAssignmentsMap, BigDecimal totalGradebookPoints, boolean isExtraCreditScaled) {

		BigDecimal categoryWeightDesiredSum = BigDecimal.ZERO;
		BigDecimal categoryWeightSum = null; 
		BigDecimal courseGrade = null;
		BigDecimal extraCreditSum = BigDecimal.ZERO;

		// First pass calculates all the grades
		for (String categoryKey : categoryUnitMap.keySet()) {
			CategoryCalculationUnit categoryUnit = categoryUnitMap.get(categoryKey);
			List<GradeRecordCalculationUnit> units = categoryGradeUnitListMap.get(categoryKey);
			
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
						
				} else {
					if (categoryPointsReceived != null && categoryPointsPossible != null) {
						
						categoryGrade = divide(categoryPointsReceived, categoryPointsPossible);
						
						
						if (categoryExtraCreditPoints != null) {
							categoryGrade = add(categoryGrade, scaleExtraCreditPoints(categoryExtraCreditPoints, categoryPointsPossible, totalCategoryPoints, isExtraCreditScaled));
						}
						
					}
				}
				
				if (categoryGrade != null)
					categoryUnit.setCategoryGrade(categoryGrade);
				
			} else {
				categoryGrade = categoryUnit.calculate(units, hasCategoryManuallyEqualWeightedAssignmentsMap.get(categoryKey), isExtraCreditScaled);	
			} // else
			
			if (categoryWeight != null && !categoryUnit.isExtraCredit())
				categoryWeightDesiredSum = add(categoryWeightDesiredSum,categoryWeight);
				
			if (categoryGrade != null && categoryWeight != null) {
				if (categoryUnit.isExtraCredit()) {
					extraCreditSum = add(extraCreditSum, categoryWeight);
				} else {
					if (categoryWeightSum == null)
						categoryWeightSum = BigDecimal.ZERO;
					categoryWeightSum = add(categoryWeightSum, categoryWeight);
				} // else
			} // if
		} // for

		if (categoryWeightSum == null)
			return null;
		
		for (String categoryKey : categoryUnitMap.keySet()) {
			CategoryCalculationUnit categoryUnit = categoryUnitMap.get(categoryKey);

			BigDecimal categoryGrade = categoryUnit.getCategoryGrade();
			BigDecimal categoryWeight = categoryUnit.getCategoryWeightTotal();

			if (categoryGrade != null) {

				if (categoryGrade.compareTo(BigDecimal.ONE) > 0)
					categoryGrade = BigDecimal.ONE;

				if (categoryGrade.compareTo(BigDecimal.ZERO) < 0)
					categoryGrade = BigDecimal.ZERO;

				if (categoryWeight != null) {

					if (categoryUnit.isExtraCredit()) {
						categoryWeight = multiply(categoryWeight, BigDecimal.ONE);
					}
					else {

						// GRBK-887 : Prevent division by zero
						if(categoryWeightSum.compareTo(BigDecimal.ZERO) == 0) {
							
							categoryWeight = BigDecimal.ZERO;
						}
						else {
							
							categoryWeight = divide(categoryWeight, categoryWeightSum);
						}
					}

					BigDecimal contributionToCourseGrade = multiply(categoryGrade, categoryWeight);

					if (courseGrade == null)
						courseGrade = BigDecimal.ZERO;

					courseGrade = add(courseGrade, contributionToCourseGrade);

				} // if 
			} // if

		} // for


		if (courseGrade != null)
			courseGrade = multiply(courseGrade, BIG_DECIMAL_100);

		return courseGrade;
	}



	private BigDecimal[] sumPoints(List<GradeRecordCalculationUnit> units, int dropLowest) {

		BigDecimal[] result = new BigDecimal[3];

		BigDecimal sumPoints = null;
		BigDecimal sumPointsPossible = null;
		BigDecimal sumExtraCreditPoints = null;

		if (units != null && !units.isEmpty()) {

			
			/*
			 * GRBK-942 / GRBK-504
			 * 
			 *  We want to only do drop lowest if we have enough units to 
			 *  do so.  The way units is constructed is it doesn't include 
			 *  excused items, so... 
			 */
			int nonECCnt = countNonECItems(units); 
			boolean doCalculateDropLowest = (dropLowest > 0) && (nonECCnt > dropLowest);
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
						
					} else {
						if (sumPoints == null)
							sumPoints = BigDecimal.ZERO;
	
						sumPoints = add(sumPoints, pointsReceived);
					}
				}

				if (pointsPossible != null && !unit.isExtraCredit()) {
					if (sumPointsPossible == null)
						sumPointsPossible = BigDecimal.ZERO;

					sumPointsPossible = add(sumPointsPossible, pointsPossible);
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
			for (int i = 0; i < dropLowest; i++) {
				GradeRecordCalculationUnit entry = orderingList.get(i);

				BigDecimal pointsReceived = entry.getPointsReceived();
				BigDecimal pointsPossible = entry.getPointsPossible();

				entry.setDropped(true);

				if (pointsReceived != null && pointsPossible != null) {
					sumPoints = subtract(sumPoints, pointsReceived);
					sumPointsPossible = subtract(sumPointsPossible, pointsPossible);

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
	
	private int countNonECItems(List<GradeRecordCalculationUnit> units) {
		int cnt = 0; 
		
		if (null != units && units.size() > 0)
		{
			for (GradeRecordCalculationUnit u : units)
			{
				/*
				 * Note that at the present time isExcused appears a no-op, but 
				 * in case that changes we'll check anyways... 
				 */
				if (u.isExcused() || u.isExtraCredit())
				{
					continue; 
				}
				cnt++; 
			}
		}
		
		return cnt; 
	}


	private BigDecimal scaleExtraCreditPoints(BigDecimal extraCreditPoints, BigDecimal pointsPossible, BigDecimal totalGradebookPoints, boolean isExtraCreditScaled) {
		
		if (extraCreditPoints == null || pointsPossible == null || extraCreditPoints.compareTo(BigDecimal.ZERO) == 0)
			return BigDecimal.ZERO;
		
		if (isExtraCreditScaled)
			return divide(extraCreditPoints, totalGradebookPoints);
		
 		return divide(extraCreditPoints, pointsPossible);

	}
	
}
