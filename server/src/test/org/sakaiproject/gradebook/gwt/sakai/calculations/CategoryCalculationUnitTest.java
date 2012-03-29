package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


import junit.framework.TestCase;

public class CategoryCalculationUnitTest extends TestCase {
	
	private static final int TEST_SCALE = 50;

	public CategoryCalculationUnitTest() {
		super();
	}
	
	// API TESTS
	
	public void testCalculate1() {

		BigDecimal categoryWeightTotal = new BigDecimal("0.4");
		int dropLowest = 0;
		boolean isExtraCredit = false;
		boolean isPointsWeighted = false;
		
		CategoryCalculationUnit categoryCalculationUnit = new CategoryCalculationUnitImpl(categoryWeightTotal, dropLowest, isExtraCredit, isPointsWeighted, Boolean.FALSE, TEST_SCALE);
		
		BigDecimal pointsReceived = new BigDecimal("7.7");
		BigDecimal pointsPossible = new BigDecimal("10.0");
		BigDecimal percentOfCategory = new BigDecimal("0.25");
		Boolean extraCredit = Boolean.FALSE;
		GradeRecordCalculationUnit gradeRecordCalculationUnit = new GradeRecordCalculationUnitImpl(pointsReceived, pointsPossible, percentOfCategory, extraCredit, TEST_SCALE);
		
		List<GradeRecordCalculationUnit> gradeRecordCalculationUnits = new ArrayList<GradeRecordCalculationUnit>();
		gradeRecordCalculationUnits.add(gradeRecordCalculationUnit);
		
		BigDecimal result1 = categoryCalculationUnit.calculate(gradeRecordCalculationUnits, null, false);
		//assertEquals(result1, new BigDecimal("0.7700"));
		assertTrue(result1.compareTo(new BigDecimal("0.7700")) == 0);
		
		BigDecimal result2 = categoryCalculationUnit.calculate(null, null, false);
		assertNull(result2);
	}
	
	public void testCalculate2() {

		BigDecimal categoryWeightTotal = new BigDecimal("0.4");
		int dropLowest = 1;
		boolean isExtraCredit = false;
		boolean isPointsWeighted = false;
		
		CategoryCalculationUnit categoryCalculationUnit = new CategoryCalculationUnitImpl(categoryWeightTotal, dropLowest, isExtraCredit, isPointsWeighted, Boolean.FALSE, TEST_SCALE);
		
		BigDecimal pointsReceived1 = new BigDecimal("6.5");
		BigDecimal pointsPossible1 = new BigDecimal("10.0");
		BigDecimal percentOfCategory1 = new BigDecimal("0.25");
		Boolean extraCredit1 = Boolean.FALSE;
		GradeRecordCalculationUnit gradeRecordCalculationUnit1 = new GradeRecordCalculationUnitImpl(pointsReceived1, pointsPossible1, percentOfCategory1, extraCredit1, TEST_SCALE);
		
		BigDecimal pointsReceived2 = new BigDecimal("8.13");
		BigDecimal pointsPossible2 = new BigDecimal("10.0");
		BigDecimal percentOfCategory2 = new BigDecimal("0.25");
		Boolean extraCredit2 = Boolean.FALSE;
		GradeRecordCalculationUnit gradeRecordCalculationUnit2 = new GradeRecordCalculationUnitImpl(pointsReceived2, pointsPossible2, percentOfCategory2, extraCredit2, TEST_SCALE);
		
		List<GradeRecordCalculationUnit> gradeRecordCalculationUnits = new ArrayList<GradeRecordCalculationUnit>();
		gradeRecordCalculationUnits.add(gradeRecordCalculationUnit1);
		gradeRecordCalculationUnits.add(gradeRecordCalculationUnit2);
		
		BigDecimal result1 = categoryCalculationUnit.calculate(gradeRecordCalculationUnits, null, false);
		//assertEquals(result1, new BigDecimal("0.81300"));
		assertTrue(result1.compareTo(new BigDecimal("0.81300")) == 0);
		
		BigDecimal result2 = categoryCalculationUnit.calculate(null, null, false);
		assertNull(result2);
	}
	
	public void testCalculate3() {

		BigDecimal categoryWeightTotal = new BigDecimal("0.4");
		int dropLowest = 0;
		boolean isExtraCredit = true;
		boolean isPointsWeighted = false;
		
		CategoryCalculationUnit categoryCalculationUnit = new CategoryCalculationUnitImpl(categoryWeightTotal, dropLowest, isExtraCredit, isPointsWeighted, Boolean.FALSE, TEST_SCALE);
		
		List<GradeRecordCalculationUnit> gradeRecordCalculationUnits = new ArrayList<GradeRecordCalculationUnit>();
		BigDecimal result1 = categoryCalculationUnit.calculate(gradeRecordCalculationUnits, null, false);
		assertNull(result1);
		
		BigDecimal pointsReceived = new BigDecimal("7.7");
		BigDecimal pointsPossible = new BigDecimal("10.0");
		BigDecimal percentOfCategory = new BigDecimal("0.25");
		Boolean extraCredit = Boolean.TRUE;
		GradeRecordCalculationUnit gradeRecordCalculationUnit = new GradeRecordCalculationUnitImpl(pointsReceived, pointsPossible, percentOfCategory, extraCredit, TEST_SCALE);
		gradeRecordCalculationUnit.setExcused(false);
		gradeRecordCalculationUnits.add(gradeRecordCalculationUnit);
		BigDecimal result2 = categoryCalculationUnit.calculate(gradeRecordCalculationUnits, null, true);
		//assertEquals(result2, new BigDecimal("0.7700"));
		assertTrue(result2.compareTo(new BigDecimal("0.7700")) == 0);
	}
	
	public void testIsExtraCredit() {
		
		CategoryCalculationUnit categoryCalculationUnit1 = new CategoryCalculationUnitImpl(new BigDecimal("0.4"), 0, true, true, Boolean.FALSE, TEST_SCALE);
		assertTrue(categoryCalculationUnit1.isExtraCredit());
				
		CategoryCalculationUnit categoryCalculationUnit2 = new CategoryCalculationUnitImpl(new BigDecimal("0.4"), 0, false, true, Boolean.FALSE, TEST_SCALE);
		assertFalse(categoryCalculationUnit2.isExtraCredit());		
	}
	
	public void testGetCategoryWeightTotal() {
		
		BigDecimal categoryWeightTotal = new BigDecimal("0.6");
		int dropLowest = 0;
		boolean isExtraCredit = false;
		boolean isPointsWeighted = false;
		
		CategoryCalculationUnit categoryCalculationUnit = new CategoryCalculationUnitImpl(categoryWeightTotal, dropLowest, isExtraCredit, isPointsWeighted, Boolean.FALSE, TEST_SCALE);
		
		BigDecimal result = categoryCalculationUnit.getCategoryWeightTotal();
		assertEquals(result, categoryWeightTotal);
	}
	
	public void testGetCategoryGrade() {
		
		BigDecimal categoryWeightTotal = new BigDecimal("0.6");
		int dropLowest = 0;
		boolean isExtraCredit = false;
		boolean isPointsWeighted = false;
		
		CategoryCalculationUnit categoryCalculationUnit = new CategoryCalculationUnitImpl(categoryWeightTotal, dropLowest, isExtraCredit, isPointsWeighted, Boolean.FALSE, TEST_SCALE);
		
		assertNull(categoryCalculationUnit.getCategoryGrade());
		
		BigDecimal categoryGrade = new BigDecimal("0.91");
		categoryCalculationUnit.setCategoryGrade(categoryGrade);
		assertEquals(categoryGrade, categoryCalculationUnit.getCategoryGrade());
	
	}
	
	// Testing getter/setter
	public void testgetDropLowest() {
		
		BigDecimal categoryWeightTotal = new BigDecimal("0.6");
		int dropLowest = 3;
		boolean isExtraCredit = false;
		boolean isPointsWeighted = false;
		
		CategoryCalculationUnit categoryCalculationUnit = new CategoryCalculationUnitImpl(categoryWeightTotal, dropLowest, isExtraCredit, isPointsWeighted, Boolean.FALSE, TEST_SCALE);
		
		assertEquals(dropLowest, categoryCalculationUnit.getDropLowest());
		
		categoryCalculationUnit.setDropLowest(4);
		assertEquals(4, categoryCalculationUnit.getDropLowest());
	}
	
	// Testing getter/setter
	public void testIsPointsWeighted() {
		
		BigDecimal categoryWeightTotal = new BigDecimal("0.6");
		int dropLowest = 3;
		boolean isExtraCredit = false;
		boolean isPointsWeighted = true;
		
		CategoryCalculationUnit categoryCalculationUnit = new CategoryCalculationUnitImpl(categoryWeightTotal, dropLowest, isExtraCredit, isPointsWeighted, Boolean.FALSE, TEST_SCALE);
		
		assertTrue(categoryCalculationUnit.isPointsWeighted());
		
		categoryCalculationUnit.setPointsWeighted(false);
		assertFalse(categoryCalculationUnit.isPointsWeighted());
		
	}
	
	// Testing getter/setter
	public void testGetTotalCategoryPoints() {
		
		BigDecimal categoryWeightTotal = new BigDecimal("0.6");
		int dropLowest = 3;
		boolean isExtraCredit = false;
		boolean isPointsWeighted = true;
		
		CategoryCalculationUnit categoryCalculationUnit = new CategoryCalculationUnitImpl(categoryWeightTotal, dropLowest, isExtraCredit, isPointsWeighted, Boolean.FALSE, TEST_SCALE);
		
		BigDecimal totalCategoryPoints = new BigDecimal("40");
		categoryCalculationUnit.setTotalCategoryPoints(totalCategoryPoints);
		assertEquals(totalCategoryPoints, categoryCalculationUnit.getTotalCategoryPoints());
	}
}
