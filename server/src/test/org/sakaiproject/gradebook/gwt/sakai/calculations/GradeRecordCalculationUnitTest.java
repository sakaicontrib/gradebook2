package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.math.BigDecimal;



import junit.framework.TestCase;

public class GradeRecordCalculationUnitTest extends TestCase {
	
	private static final int TEST_SCALE = 50;
	
	public GradeRecordCalculationUnitTest() {
		super();
	}

	// API TESTS
	
	public void testCalculate() {
		
		BigDecimal pointsReceived = new BigDecimal("89");
		BigDecimal pointsPossible = new BigDecimal("100");
		BigDecimal percentOfCategory = new BigDecimal("10");
		Boolean extraCredit = Boolean.FALSE;
		
		/*
		 * The constructor calculates the percentageScore via: pointsReceived / pointsPossible
		 * percentageScore = 89 / 100 = 0.89
		 * 
		 */
		GradeRecordCalculationUnit gradeRecordCalculationUnit = new GradeRecordCalculationUnitImpl(pointsReceived, pointsPossible, percentOfCategory, extraCredit, TEST_SCALE);
		
		BigDecimal result1 = gradeRecordCalculationUnit.calculate(new BigDecimal("0.4"));
		assertEquals(0, new BigDecimal("0.356").compareTo(result1));
		
		BigDecimal result2 = gradeRecordCalculationUnit.calculate(null);
		assertNull(result2);
		
	}
	
	public void testCalculatePercentageScore() {
		
		BigDecimal pointsReceived = new BigDecimal("89");
		BigDecimal pointsPossible = new BigDecimal("100");
		BigDecimal percentOfCategory = new BigDecimal("10");
		Boolean extraCredit = Boolean.FALSE;
		
		/*
		 * The constructor calculates the percentageScore via: pointsReceived / pointsPossible
		 * percentageScore = 89 / 100 = 0.89
		 * 
		 */
		GradeRecordCalculationUnit gradeRecordCalculationUnit = new GradeRecordCalculationUnitImpl(pointsReceived, pointsPossible, percentOfCategory, extraCredit, TEST_SCALE);
		assertEquals(0, new BigDecimal("0.89").compareTo(gradeRecordCalculationUnit.getPercentageScore()));
		
		GradeRecordCalculationUnit gradeRecordCalculationUnit2 = new GradeRecordCalculationUnitImpl(BigDecimal.ZERO, pointsPossible, percentOfCategory, extraCredit, TEST_SCALE);
		assertEquals(0, BigDecimal.ZERO.compareTo(gradeRecordCalculationUnit2.getPercentageScore()));
		
		GradeRecordCalculationUnit gradeRecordCalculationUnit3 = new GradeRecordCalculationUnitImpl(null, pointsPossible, percentOfCategory, extraCredit, TEST_SCALE);
		assertNull(gradeRecordCalculationUnit3.getPercentageScore());
		
		GradeRecordCalculationUnit gradeRecordCalculationUnit4 = new GradeRecordCalculationUnitImpl(pointsReceived, BigDecimal.ZERO, percentOfCategory, extraCredit, TEST_SCALE);
		assertEquals(0, BigDecimal.ZERO.compareTo(gradeRecordCalculationUnit4.getPercentageScore()));
	}
	
	public void testCalculateRawDifference() {
		
		BigDecimal pointsReceived = new BigDecimal("89");
		BigDecimal pointsPossible = new BigDecimal("100");
		BigDecimal percentOfCategory = new BigDecimal("10");
		Boolean extraCredit = Boolean.FALSE;
		
		/*
		 * The constructor calculates the percentageScore via: pointsReceived / pointsPossible
		 * percentageScore = 89 / 100 = 0.89
		 * 
		 */
		GradeRecordCalculationUnit gradeRecordCalculationUnit = new GradeRecordCalculationUnitImpl(pointsReceived, pointsPossible, percentOfCategory, extraCredit, TEST_SCALE);
		gradeRecordCalculationUnit.calculateRawDifference();
		assertEquals(0, new BigDecimal("11").compareTo(gradeRecordCalculationUnit.getPointsDifference()));
		
		GradeRecordCalculationUnit gradeRecordCalculationUnit2 = new GradeRecordCalculationUnitImpl(null, pointsPossible, percentOfCategory, extraCredit, TEST_SCALE);
		gradeRecordCalculationUnit2.calculateRawDifference();
		assertNull(gradeRecordCalculationUnit2.getPointsDifference());
		
		GradeRecordCalculationUnit gradeRecordCalculationUnit3 = new GradeRecordCalculationUnitImpl(pointsReceived, null, percentOfCategory, extraCredit, TEST_SCALE);
		gradeRecordCalculationUnit3.calculateRawDifference();
		assertNull(gradeRecordCalculationUnit3.getPointsDifference());
	}

	public void testGettersAndSetters() {
		
		BigDecimal pointsReceived = new BigDecimal("89");
		BigDecimal pointsPossible = new BigDecimal("100");
		BigDecimal percentOfCategory = new BigDecimal("10");
		Boolean extraCredit = Boolean.TRUE;
		
		GradeRecordCalculationUnit gradeRecordCalculationUnit = new GradeRecordCalculationUnitImpl(pointsReceived, pointsPossible, percentOfCategory, extraCredit, TEST_SCALE);
		
		// getPercentOfCategory()
		assertEquals(0, percentOfCategory.compareTo(gradeRecordCalculationUnit.getPercentOfCategory()));
		
		// getScaledScore()
		BigDecimal result = gradeRecordCalculationUnit.calculate(new BigDecimal("0.4"));
		assertEquals(0, result.compareTo(gradeRecordCalculationUnit.getScaledScore()));
		
		// isExcused() / setExcused()
		gradeRecordCalculationUnit.setExcused(true);
		assertTrue(gradeRecordCalculationUnit.isExcused());
		
		// isExtraCredit()
		assertTrue(gradeRecordCalculationUnit.isExtraCredit());
		
		// getPointsReceived()
		assertEquals(0, pointsReceived.compareTo(gradeRecordCalculationUnit.getPointsReceived()));
		
		// getPointsPossible()
		assertEquals(0, pointsPossible.compareTo(gradeRecordCalculationUnit.getPointsPossible()));
		
		// isDropped() / setDropped()
		gradeRecordCalculationUnit.setDropped(true);
		assertTrue(gradeRecordCalculationUnit.isDropped());
		
		// getActualRecord() / setActualRecord()
		gradeRecordCalculationUnit.setActualRecord(new Object());
		assertNotNull(gradeRecordCalculationUnit.getActualRecord());
		
	}
	
	// Constructor Tests
	public void testConstructor() {
		
		// Test case where extra credit is null
		BigDecimal pointsReceived = new BigDecimal("89");
		BigDecimal pointsPossible = new BigDecimal("100");
		BigDecimal percentOfCategory = new BigDecimal("10");
		//Boolean extraCredit = Boolean.FALSE;
		
		GradeRecordCalculationUnit gradeRecordCalculationUnit = new GradeRecordCalculationUnitImpl(pointsReceived, pointsPossible, percentOfCategory, null, TEST_SCALE);
		assertFalse(gradeRecordCalculationUnit.isExtraCredit());
	}
}
