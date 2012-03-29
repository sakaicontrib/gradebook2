package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.sakai.calculations.GradeRecordCalculationUnit;

/**
 * 
 * This class performs top level gradebook calculations. It also keeps track of the associated CategoryCalculationUnits.
 *
 */

public interface GradebookCalculationUnit {

	/**
	 * This method:
	 * - Sums up all the points received, points possible and extra credit points.
	 * - If the sum of points received or the sum of points possible is zero, we return zero as the course grade
	 * - Calculates the percentage score by dividing points received by points possible.
	 * - When appropriate and if present, it add the scaled extra credit points to the percentage score.
	 * - At the end, it returns the course grade by multiplying the percentage score by 100
	 * 
	 * @param units
	 * @param totalGradebookPoints
	 * @param isExtraCreditScaled
	 * @return ZERO if points possible is ZERO, or if points received is ZERO otherwise it returns the course grade
	 */
	public BigDecimal calculatePointsBasedCourseGrade(List<GradeRecordCalculationUnit> units, BigDecimal totalGradebookPoints, boolean isExtraCreditScaled);
	
	/**
	 * This method:
	 * - Sums up all the points received, points possible, and extra credit points per CATEGORY
	 * - If the sum of points received or the sum of points possible is zero, we return zero as the course grade
	 * - Calculates the percentage score by dividing points received by points possible.
	 * - When appropriate and if present, it add the scaled extra credit points to the percentage score.
	 * - At the end, it returns the course grade by multiplying the percentage score by 100
	 * 
	 * @param categoryGradeUnitListMap
	 * @param totalGradebookPoints
	 * @param isExtraCreditScaled
	 * @return ZERO if points possible is ZERO, or if points received is ZERO otherwise it returns the course grade
	 */
	public BigDecimal calculatePointsBasedCourseGrade(Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap, BigDecimal totalGradebookPoints, boolean isExtraCreditScaled);
	
	/**
	 * This method:
	 * - Sums up all the category points received, category points possible, and category extra credit points 
	 *   for every CATEGORY
	 *   handling the different paths for:
	 *   - weighted by points and not weighted by points
	 *   -- in the weighed by points path, it handles extra credit or not extra credit categories
	 *   - it calculates a categoryGrade, which is bases on a ratio of categoryPointsReceived / categoryPointsPossible
	 *   -- it also add any scaleExtraCreditPoints to the categoryGrade
	 *   - The categoryGrade is then assigned to the categoryUnit
	 * - Then it loops over all the categories again adding up all the category 
	 *    contributions (categoryGrade * categoryWeight) and adds them to the course grade
	 *
	 * @param categoryGradeUnitListMap
	 * @param totalGradebookPoints
	 * @param isExtraCreditScaled
	 * @param hasCategoryManuallyEqualWeightedAssignmentsMap
	 * @return returns null if the categoryWeightSum is null, otherwise it returns the course grade or 
	 */
	public BigDecimal calculateWeightedCourseGrade(Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap, Map<String, Boolean> hasCategoryManuallyEqualWeightedAssignmentsMap, BigDecimal totalGradebookPoints, boolean isExtraCreditScaled);
	
}
