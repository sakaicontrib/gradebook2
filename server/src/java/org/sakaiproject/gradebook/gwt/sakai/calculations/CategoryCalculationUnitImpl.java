package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CategoryCalculationUnitImpl extends BigDecimalCalculationsWrapper implements CategoryCalculationUnit {

	private static final Log log = LogFactory.getLog(CategoryCalculationUnitImpl.class);
	private BigDecimal categoryGrade;

	// This is the desired weight for the category that the user enters
	private BigDecimal categoryWeightTotal;

	private int dropLowest;
	private boolean isExtraCredit;
	private boolean isPointsWeighted;
	private BigDecimal totalCategoryPoints;
	private boolean isEqualWeighted; 

	private int totalNumberOfItems; 

	private List<GradeRecordCalculationUnit> unitsToDrop;

	// Making default constructor private since this class needs to be instantiated with a scale
	private CategoryCalculationUnitImpl() {
	}

	public CategoryCalculationUnitImpl(BigDecimal categoryWeightTotal, Integer dropLowest, Boolean extraCredit, Boolean usePoints, Boolean useEqual, int scale) {
		super(scale);
		this.categoryWeightTotal = categoryWeightTotal;
		this.dropLowest = dropLowest == null ? 0 : dropLowest.intValue();
		this.isExtraCredit = extraCredit == null ? false : extraCredit.booleanValue();
		this.unitsToDrop = new LinkedList<GradeRecordCalculationUnit>();
		this.isPointsWeighted = usePoints == null ? false : usePoints.booleanValue();
		this.isEqualWeighted = useEqual == null ? false : useEqual.booleanValue();
		this.totalNumberOfItems = 0;  

	}


	public BigDecimal calculate(List<GradeRecordCalculationUnit> units, Boolean hasCategoryManuallyEqualWeightedAssignments, boolean isExtraCreditScaled) {	

		if (units == null)
			return null;

		BigDecimal sumScores = sumScaledScores(units, isExtraCreditScaled, hasCategoryManuallyEqualWeightedAssignments);

		// When drop lowest is not set, the calculation is very straightforward
		/*
		 * GRBK-942 - drop lowest should only drop if we have 
		 * enough items to drop. So this means we need N + 1 items where
		 * N is the drop lowest #. 
		 * 
		 * NOTE: this code depends on that all items in units are not excused which is
		 * how it works when it was written, if that changes this could break. 
		 * 
		 * Units can contain EC items, so we have to find the non EC items. 
		 */
		int nonECItems = countNumberOfNonECActiveUnit(units);
		
		if (dropLowest <= 0 || (units != null && nonECItems <= dropLowest) ) {
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

		categoryGrade = sumScaledScores(unitsToCount, isExtraCreditScaled, hasCategoryManuallyEqualWeightedAssignments);
		return categoryGrade;
	}

	private BigDecimal sumScaledScores(List<GradeRecordCalculationUnit> units, boolean isExtraCreditScaled, Boolean hasCategoryManuallyEqualWeightedAssignments) {

		if (isEqualWeighted) {
			
			return sumScaledScoresEquallyWeighted(units, isExtraCreditScaled);
		}
		else {
			/*
			 * GRBK-875 : Adding logic to calculate a category that has manually equally weighted items the same
			 * way as if it calculates it when the category has the "Weight items equally" option checked.
			 */
			if(!isEqualWeighted && !hasEqualWeights(units)) {
			
				return sumScaledScoresNormal(units, isExtraCreditScaled);
			}
			else if(isExtraCredit && (null == hasCategoryManuallyEqualWeightedAssignments || !hasCategoryManuallyEqualWeightedAssignments.booleanValue())) { // GRBK-1255
				
				return sumScaledScoresNormal(units, isExtraCreditScaled);
			}
			else {
				
				// Note that since we're not equally weighted set, we cannot drop lowest.  If we ever 
				// let them drop lowest when equal weighted is set this will likely fail hard.
				int remCount = 0; 
				List<GradeRecordCalculationUnit> tmpUnits;
				tmpUnits = new ArrayList<GradeRecordCalculationUnit>();
				// This list cannot have any null weighted items in it for this list. 
				for(GradeRecordCalculationUnit unit : units) {
					if (null != unit && unit.getPercentOfCategory() == null)
					{
						remCount++; 
					}
					else if(null != unit && unit.getPercentOfCategory().compareTo(BigDecimal.ZERO) == 0)
					{
						remCount++;
					}
					else
					{
						tmpUnits.add(unit); 
					}
				}
				this.totalNumberOfItems -= remCount; 
				
				return sumScaledScoresEquallyWeighted(tmpUnits, isExtraCreditScaled);
			}
		}

	}

	/*
	 * Helper method that determines if a manually weighed category 
	 * has equally weighted grade items
	 */
	private boolean hasEqualWeights(List<GradeRecordCalculationUnit> units ) {

		int numActive = 0; 
		BigDecimal weight = null;

		if(null == units || units.size() < 2) {
			return false;
		}
		
		for(GradeRecordCalculationUnit unit : units) {

			if (null != unit)
			{
				if (null != unit.getPercentOfCategory() )
				{
					if(unit.getPercentOfCategory().compareTo(BigDecimal.ZERO) == 0) {
						continue;
					}
					else if(null == weight) {
						weight = unit.getPercentOfCategory();
					}
					else if(unit.isExtraCredit() && !isExtraCredit()) {
						continue;
					}
					else {
						if(unit.getPercentOfCategory().compareTo(weight) != 0) {
							return false;
						}
					}
					numActive++; 
				}
			}
			
		}
		// If the category doesn't have enough items active, meaning non null unit weights we'll return false and let it go thru the normal method. 
		
		if (numActive > 1)
		{

			return true;
		}
		else
		{
			return false; 
		}
	}
	
	private BigDecimal sumScaledScoresEquallyWeighted(List<GradeRecordCalculationUnit> units, boolean isExtraCreditScaled) 
	{	
		log.debug("sumScaledScoresEquallyWeighted for EC Category: " + this.isExtraCredit + " with EC Scaling: " + isExtraCreditScaled); 
		BigDecimal sumScores = null;
		BigDecimal extraCreditScore = BigDecimal.ZERO; 
		/*
		 * If the category is an extra credit category and the extra credit scale 
		 * is set, then we want to use the actual number of items graded to determine 
		 * the average.  If extra credit is not scaled, then we want to calculate the 
		 * grade based on total number of items in the category, ie including the excused 
		 * item. 
		 * 
		 * This assumption/idea is based on the old method which is called sumScaledScoresNormal 
		 * 
		 * we implement this by inverse because if the category is not extra credit we don't want to 
		 * do anything as well as if the extra credit is scaled we want to leave it alone.
		 */
		int numActiveItems;
		if (isExtraCredit && !isExtraCreditScaled )
		{
			numActiveItems = totalNumberOfItems;				
			log.debug("EC Cat w/o scaling, numActiveItems=" + numActiveItems);
		}
		else // Not extra credit category or extra credit category with extra credit scaling turned on
		{
			numActiveItems = countNumberOfActiveUnit(units); 
			log.debug("Normal, numActiveItems=" + numActiveItems);

		}

		for (GradeRecordCalculationUnit unit : units) 
		{
			if (unit.isExcused())
				continue;
			if (unit.isExtraCredit() && !isExtraCredit) // extra credit item in a non extra credit category
			{
				BigDecimal scaledItemWeight = unit.getPercentOfCategory(); 
				BigDecimal scaledScore = unit.calculate(scaledItemWeight);
				if (scaledScore != null)
				{
					log.debug("EC: Adding " + scaledScore + " to " + extraCreditScore); 
					extraCreditScore = add(extraCreditScore, scaledScore);
				}
			}
			else
			{
				BigDecimal scaledScore = unit.getPercentageScore();

				// Calling this so drop lowest is OK.  for now. 

				unit.calculateEqually(numActiveItems);
				if (scaledScore != null) {
					if (sumScores == null)
					{
						sumScores = BigDecimal.ZERO;
					}
					log.debug("NM: Adding " + scaledScore + " to " + sumScores); 
					sumScores = add(sumScores, scaledScore);
				}
			}
		} // for

		if (numActiveItems > 0 && sumScores != null)
		{
			log.debug("Dividing " + sumScores + " by " + numActiveItems);
			sumScores = divide(sumScores, new BigDecimal(numActiveItems));
			log.debug("sumScores before extra credit: " + sumScores);
			sumScores = add(sumScores, extraCreditScore);
		}
		log.debug("returning sumScores=" + ( (sumScores == null) ? "null" : sumScores));
		return sumScores; 
	}


	private BigDecimal sumScaledScoresNormal(List<GradeRecordCalculationUnit> units, boolean isExtraCreditScaled) {
		BigDecimal sum = sumUnitWeights(units, false);

		if (sum == null)
			return null;

		BigDecimal sumScores = null;

		for (GradeRecordCalculationUnit unit : units) {

			if (unit.isExcused())
				continue;

			if (unit.isExtraCredit() && !(isExtraCreditScaled && isExtraCredit)) {

				sum = BigDecimal.ONE;
			}

			BigDecimal scaledItemWeight = null;
			
			if(null != unit.getPercentOfCategory() && sum.compareTo(BigDecimal.ZERO) > 0) {
				
				scaledItemWeight = divide(unit.getPercentOfCategory(), sum);
			}
			
			BigDecimal scaledScore = unit.calculate(scaledItemWeight);

			if (scaledScore != null) {
				if (sumScores == null)
					sumScores = BigDecimal.ZERO;

				sumScores = add(sumScores, scaledScore);
			}
		}

		return sumScores;
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

					sumUnitWeight = add(sumUnitWeight, itemWeight);
				}
			}
		}

		return sumUnitWeight;
	}

	private int countNumberOfActiveUnit(List<GradeRecordCalculationUnit> units) {

		int numActiveUnits = 0; 

		if (units != null) {
			for (GradeRecordCalculationUnit unit : units) {

				if (unit.isExtraCredit() && !isExtraCredit)
					continue;

				if (unit.isExcused())
					continue;
				numActiveUnits++;
			}
		}

		return numActiveUnits;
	}

	private int countNumberOfNonECActiveUnit(List<GradeRecordCalculationUnit> units) {

		int numActiveUnits = 0; 

		if (units != null) {
			for (GradeRecordCalculationUnit unit : units) {

				if ( unit.isExtraCredit() )
					continue;

				if (unit.isExcused())
					continue;
				numActiveUnits++;
			}
		}

		return numActiveUnits;
	}

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


	public void setDropLowest(int dropLowest) {
		this.dropLowest = dropLowest;
	}


	public boolean isPointsWeighted() {
		return isPointsWeighted;
	}


	public void setPointsWeighted(boolean isPointsWeighted) {
		this.isPointsWeighted = isPointsWeighted;
	}


	public void setCategoryGrade(BigDecimal categoryGrade) {
		this.categoryGrade = categoryGrade;
	}


	public BigDecimal getTotalCategoryPoints() {
		return totalCategoryPoints;
	}


	public void setTotalCategoryPoints(BigDecimal totalCategoryPoints) {
		this.totalCategoryPoints = totalCategoryPoints;
	}

	public boolean isEqualWeighted() {
		return isEqualWeighted;
	}

	public void setEqualWeighted(boolean isEqualWeighted) {
		this.isEqualWeighted = isEqualWeighted;
	}

	public void setTotalNumberOfItems(int totalNumberOfItems) {
		this.totalNumberOfItems = totalNumberOfItems;
	}
}
