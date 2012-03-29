package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.math.BigDecimal;
import java.util.List;

/**
 * 
 * This class performs category level calculations as well as acting as a POJO, storing category level data.
 *
 */
public interface CategoryCalculationUnit {

	/**
	 * This method sums up all the scaled scores using different processes paths to handle
	 * drop lowest and extra credit
	 * 
	 * @param units in the category
	 * @param hasCategoryManuallyEqualWeightedAssignments
	 * @param isExtraCreditScaled
	 * @param categoryId
	 * @return the sum of all relevant scaled scores
	 */
	public BigDecimal calculate(List<GradeRecordCalculationUnit> units, Boolean hasCategoryManuallyEqualWeightedAssignments, boolean isExtraCreditScaled);
	
	/**
	 * Getter method
	 * 
	 * @return isExtraCredit
	 */
	public boolean isExtraCredit();

	/**
	 * Getter method
	 * 
	 * @return categoryWeightTotal
	 */
	public BigDecimal getCategoryWeightTotal();

	/**
	 * Getter method
	 * 
	 * @return categoryGrade
	 */
	public BigDecimal getCategoryGrade();

	/**
	 * Getter method
	 * 
	 * @return dropLowest
	 */
	public int getDropLowest();

	/**
	 * Setter method
	 * 
	 * @param dropLowest
	 */
	public void setDropLowest(int dropLowest);

	/**
	 * Getter method
	 * 
	 * @return isPointsWeighted
	 */
	public boolean isPointsWeighted();

	/**
	 * Setter method
	 * 
	 * @param isPointsWeighted
	 */
	public void setPointsWeighted(boolean isPointsWeighted);

	/**
	 * Setter method
	 * 
	 * @param categoryGrade
	 */
	public void setCategoryGrade(BigDecimal categoryGrade);

	/**
	 * Getter method
	 * 
	 * @return totalCategoryPoints
	 */
	public BigDecimal getTotalCategoryPoints();

	/**
	 * Setter method
	 * 
	 * @param totalCategoryPoints
	 */
	public void setTotalCategoryPoints(BigDecimal totalCategoryPoints);
	
	/**
	 * Setter method
	 * 
	 * @param totalNumberOfItems
	 */
	public void setTotalNumberOfItems(int totalNumberOfItems);
}
