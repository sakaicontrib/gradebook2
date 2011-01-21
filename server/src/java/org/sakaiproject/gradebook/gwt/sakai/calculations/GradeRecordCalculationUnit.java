package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.math.BigDecimal;

/**
 * Many things in this module are set via the constructor, so 
 * there's more done in the impl than would seem to be indicated by the interface. 
 * 
 * For purposes of this documentation, item below refers to the item this object was created to represent. 
 *  
 * This object is instantiated with the points possible, the points received, the 
 * percent of this item's contribution to the category, and whether the item an extra 
 * credit item or not. 
 *
 * 
 */
public interface GradeRecordCalculationUnit {
	
	/**
	 * Takes the weight multiplied by the percentage score to produce scaled score.  This value 
	 * is then stored as the scaled score and returned.  If either the weight or the percentage 
	 * score is null, then nothing happens.
	 * 
	 * @param weight
	 * @return the scaled score, or if either weight or percentage score is null, then null is returned. 
	 */
	public BigDecimal calculate(BigDecimal weight);

	/**
	 * Takes the percentage score divided by the number of items in the category to approximate the 
	 * scaled score.  This value is then stored as the scaled score and returned. 
	 * 
	 * NOTE: Scaled score is not necessarily fully accurate by this calculation.
	 *  
	 * @param numItems number of Items in the category
	 * @return the scaled score after calculation, or if the percentage score is null or the number of items is <= 0, then null is returned. 
	 */
	public BigDecimal calculateEqually(int numItems);

	// FIXME (GRBK-795)- Further Investigation Required - The impl sets the percentage score to zero if we're in a divide by zero situation, that seems strange... 

	/**
	 * Calculates the percentage score by taking the points received divided by the points possible. 
	 * If either points possible or points received are null, then nothing happens. 
	 * 
	 * If either points possible or points received are zero, then the percentage score is set to zero. 
	 */
	public void calculatePercentageScore();
	
	/**
	 * If pointsReceived and pointsPossible are not null, it subtracts pointsReceived from pointsPossible
	 * and assigns the result to pointsDifference. If either of pointsReceived or pointsPossible are null,
	 * pointsDifference set to null.
	 */
	public void calculateRawDifference();
	
	/**
	 * Getter method
	 * 
	 * @return percentOfCategory
	 */
	public BigDecimal getPercentOfCategory();

	/**
	 * Getter method
	 * 
	 * @return scaledScore
	 */
	public BigDecimal getScaledScore();

	/**
	 * Getter method
	 * 
	 * @return isExcused
	 */
	public boolean isExcused();

	/**
	 * Getter method
	 * 
	 * @return isExtraCredit
	 */
	public boolean isExtraCredit();

	/**
	 * Getter method
	 * 
	 * @return pointsReceived
	 */
	public BigDecimal getPointsReceived();

	/**
	 * Getter method
	 * 
	 * @return pointsPossible
	 */
	public BigDecimal getPointsPossible();

	/**
	 * Getter method
	 * 
	 * @return percentageScore
	 */
	public BigDecimal getPercentageScore();

	/**
	 * Setter method
	 * 
	 * @param isExcused
	 */
	public void setExcused(boolean isExcused);

	/**
	 * Getter method
	 * 
	 * @return isDropped
	 */
	public boolean isDropped();

	/**
	 * Setter method
	 * 
	 * @param isDropped
	 */
	public void setDropped(boolean isDropped);

	/**
	 * Getter method
	 * 
	 * @return actualRecord
	 */
	public Object getActualRecord();

	/**
	 * Setter method
	 * 
	 * @param actualRecord
	 */
	public void setActualRecord(Object actualRecord);

	/**
	 * Getter method
	 * 
	 * @return pointsDifference
	 */
	public BigDecimal getPointsDifference();

}
