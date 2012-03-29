package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import junit.framework.TestCase;

public class GradebookCalculationUnitTest extends TestCase {

	private static final int TEST_SCALE = 50;

	private static final String ESSAYS_ID = "1";
	private static final String HW_ID = "2";
	private static final String EC_ID = "3";
	private static final String EMPTY_ID = "4";

	public GradebookCalculationUnitTest() {
		super();
	}

	// API TESTS

	public void testCalculatePointsBasedCourseGrade1() {

		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal(".6"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);
		CategoryCalculationUnit hwUnit = new CategoryCalculationUnitImpl(new BigDecimal(".4"), Integer.valueOf(0), null, Boolean.FALSE, Boolean.FALSE,TEST_SCALE);
		CategoryCalculationUnit ecUnit = new CategoryCalculationUnitImpl(new BigDecimal(".1"), Integer.valueOf(0), Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);

		categoryUnitMap.put(ESSAYS_ID, essayUnit);
		categoryUnitMap.put(HW_ID, hwUnit);
		categoryUnitMap.put(EC_ID, ecUnit);

		GradebookCalculationUnit gradebookCalculationUnit = new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);

		String[][] values = {
				{  "9",  "10", "4.17", null },
				{  "9",  "10", "4.17", null },
				{ "9", "10", "4.17", null },
				{ "9", "10", "4.17", null },
				{ "9", "10", "4.17", null },
				{ "9", "10", "4.17", null },
				
		};


		List<GradeRecordCalculationUnit> units = getRecordUnits(values);
		BigDecimal totalGradebookPoints = new BigDecimal("200");
		BigDecimal result = gradebookCalculationUnit.calculatePointsBasedCourseGrade(units, totalGradebookPoints, true);

		assertEquals(new BigDecimal("90.0"), result);		
	}

	public void testCalculatePointsBasedCourseGrade2() {

		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal(".6"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE,TEST_SCALE);
		CategoryCalculationUnit hwUnit = new CategoryCalculationUnitImpl(new BigDecimal(".2"), Integer.valueOf(0), null, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);
		CategoryCalculationUnit ecUnit = new CategoryCalculationUnitImpl(new BigDecimal(".1"), Integer.valueOf(0), Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);

		CategoryCalculationUnit emptyUnit = new CategoryCalculationUnitImpl(new BigDecimal("0"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);

		categoryUnitMap.put(ESSAYS_ID, essayUnit);
		categoryUnitMap.put(HW_ID, hwUnit);
		categoryUnitMap.put(EC_ID, ecUnit);
		categoryUnitMap.put(EMPTY_ID, emptyUnit);
		
		GradebookCalculationUnit gradebookCalculationUnit = new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);

		String[][] essayValues = {
				{  "0", "20", "0.10", null },
				{  "0", "20", "0.10", null },
				{  "0", "20", "0.40", null }
		};

		String[][] hwValues = {
				{ "20", "20", "0.30", null },
				{ "10", "10", "0.30", null },
				{ "10", "10", "0.40", null }
		};


		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);


		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);

		BigDecimal totalGradebookPoints = new BigDecimal("100");
		
		BigDecimal result = gradebookCalculationUnit.calculatePointsBasedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);
		
		assertEquals(new BigDecimal("40.0"), result);
	}

	public void testCalculateWeightedCourseGrade() {

		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal(".6"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE,TEST_SCALE);
		CategoryCalculationUnit hwUnit = new CategoryCalculationUnitImpl(new BigDecimal(".4"), Integer.valueOf(0), null, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);
		CategoryCalculationUnit ecUnit = new CategoryCalculationUnitImpl(new BigDecimal(".1"), Integer.valueOf(0), Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);

		categoryUnitMap.put(ESSAYS_ID, essayUnit);
		categoryUnitMap.put(HW_ID, hwUnit);
		categoryUnitMap.put(EC_ID, ecUnit);

		GradebookCalculationUnit gradebookCalculationUnit = new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);

		String[][] essayValues = {
				{ "4",  "5", "0.20", null },
				{null,  "9", "0.20", null },
				{ "8", "10", "0.10", null },
				{ "8", "10", "0.10", null },
				{"16", "20", "0.40", null }
		};

		String[][] hwValues = {
				{  "8", "10", "0.30", null },
				{  "8", "10", "0.30", null },
				{  "8", "10", "0.40", null }
		};


		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);
		Map<String, Boolean> isManuallyEqualWeightedMap = new HashMap<String, Boolean>();
		isManuallyEqualWeightedMap.put(ESSAYS_ID, Boolean.FALSE);
		isManuallyEqualWeightedMap.put(HW_ID, Boolean.FALSE);

		BigDecimal totalGradebookPoints = new BigDecimal("100");

		BigDecimal result = gradebookCalculationUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, isManuallyEqualWeightedMap, totalGradebookPoints, false);

		//assertEquals(new BigDecimal("80.000000"), result);
		assertTrue(result.compareTo(new BigDecimal("80.00")) == 0);
	}

	// GBRK-774 Equal weight Category 
	
	// We had many problems with 8.9995 and cases where the value 
	// would vary depending on the # of transactions in the GB.  
	// This tests up to 41 entries in the category.
	public void testWeightedSingleCategoryWithEqualWeightingWithManyEntries() {

		boolean errorsFound = false; 
		int totalNumberOfAssignments = 41; 
		
		for (int i = 1; i <= totalNumberOfAssignments; i++) 
		{
			String[][] hwValues = buildAssignmentArray("8.9995", "10.0", i); 
			BigDecimal totalGradebookPoints = new BigDecimal(i * 10);
			BigDecimal result = getResultForSingleCategoryWithEqualWeighting(hwValues, totalGradebookPoints, true, 0);
			
			BigDecimal expectedResult = new BigDecimal("90.00"); 
			
			if (result.compareTo(expectedResult) != 0)
			{
				System.out.println("Expected result is " + expectedResult + ", but we got " + result + " on iteration " + i);
				errorsFound = true; 
			}
		}
		assertFalse(errorsFound); 
	}
	// Upper Bound
	public void testWeightedSingleCategoryWithEqualWeighting1() {

		// Make the big array so we don't have to hand calculate it
		String[][] hwValues = buildAssignmentArray("8.9995", "10", 19); 
		// Now change just one of the values
		hwValues[0][0] = "8.9996"; 
		

		BigDecimal totalGBPoints = new BigDecimal("190"); 
		BigDecimal expectedRawScore = new BigDecimal("89.995052631578947368421052631578947368421052631579"); 
		BigDecimal expectedGradeScore = new BigDecimal("90.00");  
			
		BigDecimal actualResult = getResultForSingleCategoryWithEqualWeighting(hwValues, totalGBPoints, false, 0);
		assertEquals(expectedRawScore, actualResult);
		
		actualResult = getResultForSingleCategoryWithEqualWeighting(hwValues, totalGBPoints, true, 0);
		assertEquals(expectedGradeScore, actualResult);

	}		
	// Lower bound
	public void testWeightedSingleCategoryWithEqualWeighting2() {

		// Make the big array so we don't have to hand calculate it
		String[][] hwValues = buildAssignmentArray("8.9995", "10", 19); 
		// Now change just one of the values
		hwValues[0][0] = "8.9994"; 

		BigDecimal totalGBPoints = new BigDecimal("190"); 
		BigDecimal expectedRawScore = new BigDecimal("89.994947368421052631578947368421052631578947368421"); 
		BigDecimal expectedGradeScore = new BigDecimal("89.99");  
			
		BigDecimal actualResult = getResultForSingleCategoryWithEqualWeighting(hwValues, totalGBPoints, false, 0);
		assertEquals(expectedRawScore, actualResult);
		
		actualResult = getResultForSingleCategoryWithEqualWeighting(hwValues, totalGBPoints, true, 0);
		assertEquals(expectedGradeScore, actualResult);

	}		

	// Drop lowest, with all equal 
	public void testWeightedSingleCategoryWithEqualWeightingDropLowest1() {

		// Make the big array so we don't have to hand calculate it
		String[][] hwValues = buildAssignmentArray("8.9995", "10", 19); 

		BigDecimal totalGBPoints = new BigDecimal("190"); 
		BigDecimal expectedRawScore = new BigDecimal("89.995"); 
		BigDecimal expectedGradeScore = new BigDecimal("90.00");  
			
		BigDecimal actualResult = getResultForSingleCategoryWithEqualWeighting(hwValues, totalGBPoints, false, 1);
		assertEquals(expectedRawScore, actualResult);
		
		actualResult = getResultForSingleCategoryWithEqualWeighting(hwValues, totalGBPoints, true, 1);
		assertEquals(expectedGradeScore, actualResult);

	}		

	// Drop lowest, with one lower, same as case 1 with N-1
	public void testWeightedSingleCategoryWithEqualWeightingDropLowest2() {

		// Make the big array so we don't have to hand calculate it
		String[][] hwValues = buildAssignmentArray("8.9995", "10", 19); 
		hwValues[0][0] = "8.9994"; 
		
		BigDecimal totalGBPoints = new BigDecimal("190"); 
		BigDecimal expectedRawScore = new BigDecimal("89.995"); 
		BigDecimal expectedGradeScore = new BigDecimal("90.00");  
			
		BigDecimal actualResult = getResultForSingleCategoryWithEqualWeighting(hwValues, totalGBPoints, false, 1);
		assertEquals(expectedRawScore, actualResult);
		
		actualResult = getResultForSingleCategoryWithEqualWeighting(hwValues, totalGBPoints, true, 1);
		assertEquals(expectedGradeScore, actualResult);

	}		

	// Drop lowest 1 with 2 values lower, basically same case as N being 18 with 1 value lower
	public void testWeightedSingleCategoryWithEqualWeightingDropLowest3() {

		// Make the big array so we don't have to hand calculate it
		String[][] hwValues = buildAssignmentArray("8.9995", "10", 19); 
		// Make two values smaller, shouldn't matter what for the lower one
		hwValues[0][0] = "8.9994"; 
		hwValues[9][0] = "8.9987"; 
		
		
		BigDecimal totalGBPoints = new BigDecimal("190"); 
		BigDecimal expectedRawScore = new BigDecimal("89.994944444444444444444444444444444444444444444444"); 
		BigDecimal expectedGradeScore = new BigDecimal("89.99");  
			
		BigDecimal actualResult = getResultForSingleCategoryWithEqualWeighting(hwValues, totalGBPoints, false, 1);
		assertEquals(expectedRawScore, actualResult);
		
		actualResult = getResultForSingleCategoryWithEqualWeighting(hwValues, totalGBPoints, true, 1);
		assertEquals(expectedGradeScore, actualResult);

	}		

	// basically the same case except, we're  tacking on 10 percent 
	public void testWeightedSingleCategoryWithEqualWeightingEC1() {

		// Make the big array so we don't have to hand calculate it
		String[][] hwValues = buildAssignmentArray("8.9995", "10", 19); 
	
		// Make item 10 in the list a fully credited EC item worth 10% of the category . 
		hwValues[10][0] = "10"; 
		hwValues[10][1] = "10"; 
		hwValues[10][2] = ".10"; 
		hwValues[10][3] = "true"; 
		
		BigDecimal totalGBPoints = new BigDecimal("180"); 
		BigDecimal expectedRawScore = new BigDecimal("89.995").add(new BigDecimal("10")); 
		BigDecimal expectedGradeScore = new BigDecimal("100.00");  
			
		BigDecimal actualResult = getResultForSingleCategoryWithEqualWeighting(hwValues, totalGBPoints, false, 0);
		assertEquals(expectedRawScore, actualResult);
		
		actualResult = getResultForSingleCategoryWithEqualWeighting(hwValues, totalGBPoints, true, 0);
		assertEquals(expectedGradeScore, actualResult);

	}		
	
	// basically the same case except slightly non round numbers for the EC value. 
	public void testWeightedSingleCategoryWithEqualWeightingEC2() {

		// Make the big array so we don't have to hand calculate it
		String[][] hwValues = buildAssignmentArray("8.9995", "10", 19); 
	
		// Make item 10 in the list a fully credited EC item worth 10% of the category . 
		hwValues[10][0] = "7.3517"; 
		hwValues[10][1] = "10"; 
		hwValues[10][2] = ".08"; 
		hwValues[10][3] = "true"; 
		
		BigDecimal totalGBPoints = new BigDecimal("180"); 
		
		// Doing this this way to have it make sense, basically its the total score plus the EC value.  
		BigDecimal expectedRawScore = new BigDecimal("89.995").add(new BigDecimal("5.88136")); 
		BigDecimal expectedGradeScore = new BigDecimal("95.88");  
			
		BigDecimal actualResult = getResultForSingleCategoryWithEqualWeighting(hwValues, totalGBPoints, false, 0);
		assertEquals(expectedRawScore, actualResult);
		
		actualResult = getResultForSingleCategoryWithEqualWeighting(hwValues, totalGBPoints, true, 0);
		assertEquals(expectedGradeScore, actualResult);

	}		


	// Once more with feeling... 
	public void testWeightedSingleCategoryWithEqualWeightingECAndDropLowest() {

		// Make the big array so we don't have to hand calculate it
		String[][] hwValues = buildAssignmentArray("8.9995", "10", 19); 
	
		// Drop lowest, but we want to make one value higher 
		
		hwValues[2][0] = "8.9994"; 
		hwValues[7][0] = "8.9997"; 
		
		// We'll do two ec items, first one with give 3 percent
		hwValues[10][0] = "10"; 
		hwValues[10][1] = "10"; 
		hwValues[10][2] = ".03"; 
		hwValues[10][3] = "true"; 

		// Second one is all primes
		hwValues[14][0] = "23"; 
		hwValues[14][1] = "31"; 
		hwValues[14][2] = ".07"; 
		hwValues[14][3] = "true"; 

		BigDecimal totalGBPoints = new BigDecimal("180"); 
		BigDecimal expectedRawScore = new BigDecimal("89.995125").add(new BigDecimal("3")).add(new BigDecimal("5.19354838709677419354838709677419354838709677419358")); 
		BigDecimal expectedGradeScore = new BigDecimal("98.19");  
			
		BigDecimal actualResult = getResultForSingleCategoryWithEqualWeighting(hwValues, totalGBPoints, false, 1);
		assertEquals(expectedRawScore, actualResult);
		
		actualResult = getResultForSingleCategoryWithEqualWeighting(hwValues, totalGBPoints, true, 1);
		assertEquals(expectedGradeScore, actualResult);

	}		

	/*
	 * time for EC categories, but EC works slightly different, we need a real category as well. 
	 */
	
	// Case 1: Regular category 100% of grade, 0/100 - F with EC Category worth 100% of grade with 3 units as above. 
	
	public void testWeightedSingleCategoryWithEqualWeightingForECCategory() {

		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit hwUnit = new CategoryCalculationUnitImpl(BigDecimal.ONE, Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE); 
		CategoryCalculationUnit ecUnit = new CategoryCalculationUnitImpl(new BigDecimal("1"), Integer.valueOf(0), Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, TEST_SCALE);

		categoryUnitMap.put(HW_ID, hwUnit);
		categoryUnitMap.put(EC_ID, ecUnit);

		GradebookCalculationUnit gradebookCalculationUnit = new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);

		String[][] hwValues = {
				{ "0", "100", "1.0", null }
		};

		String[][] ecValues = {
				{ "8.9995", "10", "0.33", null },
				{ "8.9995", "10", "0.33", null },
				{ "8.9995", "10", "0.33", null }
		};

		List<GradeRecordCalculationUnit> ecUnits = getRecordUnits(ecValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);
		ecUnit.setTotalNumberOfItems(3); 

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(EC_ID, ecUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);
		
		Map<String, Boolean> isManuallyEqualWeightedMap = new HashMap<String, Boolean>();
		isManuallyEqualWeightedMap.put(HW_ID, Boolean.FALSE);
		isManuallyEqualWeightedMap.put(EC_ID, Boolean.TRUE);
		
		BigDecimal totalGradebookPoints = new BigDecimal("100");

		BigDecimal result = gradebookCalculationUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, isManuallyEqualWeightedMap, totalGradebookPoints, false).stripTrailingZeros();

		assertEquals(new BigDecimal("89.995"), result);
	}		

	// Case 2: Regular category 100% of grade, 72/100 - C with EC Category worth 20% of grade with 3 units as above. 
	
	public void testWeightedSingleCategoryWithEqualWeightingForECCategory2() {

		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit hwUnit = new CategoryCalculationUnitImpl(new BigDecimal("1"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE); 
		CategoryCalculationUnit ecUnit = new CategoryCalculationUnitImpl(new BigDecimal(".2"), Integer.valueOf(0), Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, TEST_SCALE);

		categoryUnitMap.put(HW_ID, hwUnit);
		categoryUnitMap.put(EC_ID, ecUnit);

		GradebookCalculationUnit gradebookCalculationUnit = new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);

		String[][] hwValues = {
				{ "72", "100", "1.0", null }
		};

		String[][] ecValues = {
				{ "8.9995", "10", "0.33", null },
				{ "8.9995", "10", "0.33", null },
				{ "8.9995", "10", "0.33", null }
		};

		ecUnit.setTotalNumberOfItems(3); 
		List<GradeRecordCalculationUnit> ecUnits = getRecordUnits(ecValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(EC_ID, ecUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits); 
		
		Map<String, Boolean> isManuallyEqualWeightedMap = new HashMap<String, Boolean>();
		isManuallyEqualWeightedMap.put(HW_ID, Boolean.FALSE);
		isManuallyEqualWeightedMap.put(EC_ID, Boolean.TRUE);
		
		BigDecimal totalGradebookPoints = new BigDecimal("100");

		BigDecimal result = gradebookCalculationUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, isManuallyEqualWeightedMap, totalGradebookPoints, false).stripTrailingZeros();
		// Full grade
		assertEquals(new BigDecimal("89.999"), result);
		
		// Check category just be sure
		assertEquals(new BigDecimal(".89995"), ecUnit.getCategoryGrade()); 
	}		

	/*
	 * Case 3
	 * 
	 * Regular category of 100% with 1 item of 62/100 - Grade 62.0 
	 * 
	 * EC Category worth 10% of grade.  
	 * 5 Items, 3 Graded No scaled EC
	 */
	
	public void testWeightedSingleCategoryWithEqualWeightingForECCategory3() {

		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit hwUnit = new CategoryCalculationUnitImpl(new BigDecimal("1"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE); 
		CategoryCalculationUnit ecUnit = new CategoryCalculationUnitImpl(new BigDecimal(".1"), Integer.valueOf(0), Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, TEST_SCALE);

		categoryUnitMap.put(HW_ID, hwUnit);
		categoryUnitMap.put(EC_ID, ecUnit);

		GradebookCalculationUnit gradebookCalculationUnit = new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);

		String[][] hwValues = {
				{ "62", "100", "1.0", null }
		};

		String[][] ecValues = {
				{ "8.9995", "10", "0.2", null },
				{ "8.9995", "10", "0.2", null },
				{ null, "10", "0.2", null },
				{ null, "10", "0.2", null },
				{ "8.9995", "10", "0.2", null }
		};

		ecUnit.setTotalNumberOfItems(5); 
		List<GradeRecordCalculationUnit> ecUnits = getRecordUnits(ecValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(EC_ID, ecUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits); 
		
		Map<String, Boolean> isManuallyEqualWeightedMap = new HashMap<String, Boolean>();
		isManuallyEqualWeightedMap.put(HW_ID, Boolean.FALSE);
		isManuallyEqualWeightedMap.put(EC_ID, Boolean.TRUE);
		
		BigDecimal totalGradebookPoints = new BigDecimal("100");

		BigDecimal result = gradebookCalculationUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, isManuallyEqualWeightedMap, totalGradebookPoints, false).stripTrailingZeros();
		// Full grade
		assertEquals(new BigDecimal("67.3997"), result);
		
		// Check category just be sure
		assertEquals(new BigDecimal("0.53997"), ecUnit.getCategoryGrade()); 
	}		

	/*
	 * Case 4
	 * 
	 * Same as Case 3, but with scaled EC
	 */
	
	public void testWeightedSingleCategoryWithEqualWeightingForECCategory4() {

		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit hwUnit = new CategoryCalculationUnitImpl(new BigDecimal("1"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE); 
		CategoryCalculationUnit ecUnit = new CategoryCalculationUnitImpl(new BigDecimal(".1"), Integer.valueOf(0), Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, TEST_SCALE);

		categoryUnitMap.put(HW_ID, hwUnit);
		categoryUnitMap.put(EC_ID, ecUnit);

		GradebookCalculationUnit gradebookCalculationUnit = new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);

		String[][] hwValues = {
				{ "62", "100", "1.0", null }
		};

		String[][] ecValues = {
				{ "8.9995", "10", "0.2", null },
				{ "8.9995", "10", "0.2", null },
				{ null, "10", "0.2", null },
				{ null, "10", "0.2", null },
				{ "8.9995", "10", "0.2", null }
		};

		List<GradeRecordCalculationUnit> ecUnits = getRecordUnits(ecValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(EC_ID, ecUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits); 
		
		BigDecimal totalGradebookPoints = new BigDecimal("100");

		Map<String, Boolean> isManuallyEqualWeightedMap = new HashMap<String, Boolean>();
		isManuallyEqualWeightedMap.put(HW_ID, Boolean.FALSE);
		isManuallyEqualWeightedMap.put(EC_ID, Boolean.TRUE);
		
		BigDecimal result = gradebookCalculationUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, isManuallyEqualWeightedMap, totalGradebookPoints, true).stripTrailingZeros();
		// Full grade
		assertEquals(new BigDecimal("70.9995"), result);
		
		// Check category just be sure
		assertEquals(new BigDecimal("0.89995"), ecUnit.getCategoryGrade()); 
	}		

	// This models a real site with real grades 
	public void testWeightedECEqualTmp1() {

		String MIDTERM_ID = "1";
		String FINAL_ID = "2";
		String PAPER_ID = "3";
		String SECTIONS_ID = "4";
		String ECHW_ID = "5";

		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		// Midterms - 2 assignments, equal weighted
		CategoryCalculationUnit midtermUnit = new CategoryCalculationUnitImpl(
				new BigDecimal(".4"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, TEST_SCALE); 

		// Final - 1 assignment 
		CategoryCalculationUnit finalUnit = new CategoryCalculationUnitImpl(
				new BigDecimal(".25"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE); 

		// paper - 1 assignment
		CategoryCalculationUnit paperUnit = new CategoryCalculationUnitImpl(
				new BigDecimal(".23"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE); 

		// section - 6 assignments equal weighted
		CategoryCalculationUnit sectionsUnit = new CategoryCalculationUnitImpl(
				new BigDecimal(".12"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, TEST_SCALE); 

		// extra credit homework extra credit category, 3 assignments
		CategoryCalculationUnit ecUnit = new CategoryCalculationUnitImpl(
				new BigDecimal(".03"), Integer.valueOf(0), Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, TEST_SCALE); 

		

		categoryUnitMap.put(MIDTERM_ID, midtermUnit);
		categoryUnitMap.put(FINAL_ID, finalUnit);
		categoryUnitMap.put(PAPER_ID, paperUnit);
		categoryUnitMap.put(SECTIONS_ID, sectionsUnit);
		categoryUnitMap.put(ECHW_ID, ecUnit);

		GradebookCalculationUnit gradebookCalculationUnit = new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);

		String[][] midtermValues = {
				{ "94", "100", "0.5", null },
				{ "94", "100", "0.5", null }
		};

		String[][] finalValues = {
				{ null, "100", "1.0", null }
		};

		String[][] paperValues = {
				{ null, "100", "1.0", null }
		};

		String[][] sectionsValues = {
				{ "100", "100", "0.1667", null },
				{ "100", "100", "0.1667", null },
				{ "100", "100", "0.1667", null },
				{ "100", "100", "0.1667", null },
				{ "100", "100", "0.1667", null },
				{ "100", "100", "0.1667", null }
		};

		String[][] ecValues = {
				{ "100", "100", "0.3333", null },
				{ "100", "100", "0.3333", null },
				{ "0", "10", "0.3333", null }
		};

		List<GradeRecordCalculationUnit> midtermUnits = getRecordUnits(midtermValues);
		List<GradeRecordCalculationUnit> finalUnits = getRecordUnits(finalValues);
		List<GradeRecordCalculationUnit> paperUnits = getRecordUnits(paperValues);
		List<GradeRecordCalculationUnit> sectionsUnits = getRecordUnits(sectionsValues);
		List<GradeRecordCalculationUnit> ecUnits = getRecordUnits(ecValues);

		
		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();

		categoryGradeUnitListMap.put(MIDTERM_ID, midtermUnits);
		categoryGradeUnitListMap.put(FINAL_ID, finalUnits);
		categoryGradeUnitListMap.put(PAPER_ID, paperUnits);
		categoryGradeUnitListMap.put(SECTIONS_ID, sectionsUnits);
		categoryGradeUnitListMap.put(ECHW_ID, ecUnits);
		
		Map<String, Boolean> isManuallyEqualWeightedMap = new HashMap<String, Boolean>();
		isManuallyEqualWeightedMap.put(MIDTERM_ID, Boolean.TRUE);
		isManuallyEqualWeightedMap.put(FINAL_ID, Boolean.FALSE);
		isManuallyEqualWeightedMap.put(PAPER_ID, Boolean.FALSE);
		isManuallyEqualWeightedMap.put(SECTIONS_ID, Boolean.TRUE);
		isManuallyEqualWeightedMap.put(ECHW_ID, Boolean.TRUE);
		
		BigDecimal totalGradebookPoints = new BigDecimal("100");

		BigDecimal result = gradebookCalculationUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, isManuallyEqualWeightedMap, totalGradebookPoints, true).stripTrailingZeros();
		// Full grade
		
		assertEquals(new BigDecimal("97.38"), getRoundedGrade(result));
		
	}		

	// variation on previous case, but w/o scaled EC - should not make a difference. 
	public void testWeightedECEqualTmp2() {

		String MIDTERM_ID = "1";
		String FINAL_ID = "2";
		String PAPER_ID = "3";
		String SECTIONS_ID = "4";
		String ECHW_ID = "5";

		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		// Midterms - 2 assignments, equal weighted
		CategoryCalculationUnit midtermUnit = new CategoryCalculationUnitImpl(
				new BigDecimal(".4"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, TEST_SCALE); 

		// Final - 1 assignment 
		CategoryCalculationUnit finalUnit = new CategoryCalculationUnitImpl(
				new BigDecimal(".25"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE); 

		// paper - 1 assignment
		CategoryCalculationUnit paperUnit = new CategoryCalculationUnitImpl(
				new BigDecimal(".23"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE); 

		// section - 6 assignments equal weighted
		CategoryCalculationUnit sectionsUnit = new CategoryCalculationUnitImpl(
				new BigDecimal(".12"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, TEST_SCALE); 

		// extra credit homework extra credit category, 3 assignments
		CategoryCalculationUnit ecUnit = new CategoryCalculationUnitImpl(
				new BigDecimal(".03"), Integer.valueOf(0), Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, TEST_SCALE); 

		

		categoryUnitMap.put(MIDTERM_ID, midtermUnit);
		categoryUnitMap.put(FINAL_ID, finalUnit);
		categoryUnitMap.put(PAPER_ID, paperUnit);
		categoryUnitMap.put(SECTIONS_ID, sectionsUnit);
		categoryUnitMap.put(ECHW_ID, ecUnit);

		GradebookCalculationUnit gradebookCalculationUnit = new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);

		String[][] midtermValues = {
				{ "94", "100", "0.5", null },
				{ "94", "100", "0.5", null }
		};

		String[][] finalValues = {
				{ null, "100", "1.0", null }
		};

		String[][] paperValues = {
				{ null, "100", "1.0", null }
		};

		String[][] sectionsValues = {
				{ "100", "100", "0.1667", null },
				{ "100", "100", "0.1667", null },
				{ "100", "100", "0.1667", null },
				{ "100", "100", "0.1667", null },
				{ "100", "100", "0.1667", null },
				{ "100", "100", "0.1667", null }
		};

		String[][] ecValues = {
				{ "100", "100", "0.3333", null },
				{ "100", "100", "0.3333", null },
				{ "0", "10", "0.3333", null }
		};

		ecUnit.setTotalNumberOfItems(3); 
		List<GradeRecordCalculationUnit> midtermUnits = getRecordUnits(midtermValues);
		List<GradeRecordCalculationUnit> finalUnits = getRecordUnits(finalValues);
		List<GradeRecordCalculationUnit> paperUnits = getRecordUnits(paperValues);
		List<GradeRecordCalculationUnit> sectionsUnits = getRecordUnits(sectionsValues);
		List<GradeRecordCalculationUnit> ecUnits = getRecordUnits(ecValues);

		
		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();

		categoryGradeUnitListMap.put(MIDTERM_ID, midtermUnits);
		categoryGradeUnitListMap.put(FINAL_ID, finalUnits);
		categoryGradeUnitListMap.put(PAPER_ID, paperUnits);
		categoryGradeUnitListMap.put(SECTIONS_ID, sectionsUnits);
		categoryGradeUnitListMap.put(ECHW_ID, ecUnits);
		
		Map<String, Boolean> isManuallyEqualWeightedMap = new HashMap<String, Boolean>();
		isManuallyEqualWeightedMap.put(MIDTERM_ID, Boolean.TRUE);
		isManuallyEqualWeightedMap.put(FINAL_ID, Boolean.FALSE);
		isManuallyEqualWeightedMap.put(PAPER_ID, Boolean.FALSE);
		isManuallyEqualWeightedMap.put(SECTIONS_ID, Boolean.TRUE);
		isManuallyEqualWeightedMap.put(ECHW_ID, Boolean.TRUE);
		
		BigDecimal totalGradebookPoints = new BigDecimal("100");

		BigDecimal result = gradebookCalculationUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, isManuallyEqualWeightedMap, totalGradebookPoints, false).stripTrailingZeros();
		// Full grade
		
		assertEquals(new BigDecimal("97.38"), getRoundedGrade(result));
		
	}		
	
	// variation on previous case, but w/o scaled EC and one nulled out EC assignment - we lose a point
	public void testWeightedECEqualTmp3() {

		String MIDTERM_ID = "1";
		String FINAL_ID = "2";
		String PAPER_ID = "3";
		String SECTIONS_ID = "4";
		String ECHW_ID = "5";

		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		// Midterms - 2 assignments, equal weighted
		CategoryCalculationUnit midtermUnit = new CategoryCalculationUnitImpl(
				new BigDecimal(".4"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, TEST_SCALE); 

		// Final - 1 assignment 
		CategoryCalculationUnit finalUnit = new CategoryCalculationUnitImpl(
				new BigDecimal(".25"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE); 

		// paper - 1 assignment
		CategoryCalculationUnit paperUnit = new CategoryCalculationUnitImpl(
				new BigDecimal(".23"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE); 

		// section - 6 assignments equal weighted
		CategoryCalculationUnit sectionsUnit = new CategoryCalculationUnitImpl(
				new BigDecimal(".12"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, TEST_SCALE); 

		// extra credit homework extra credit category, 3 assignments
		CategoryCalculationUnit ecUnit = new CategoryCalculationUnitImpl(
				new BigDecimal(".03"), Integer.valueOf(0), Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, TEST_SCALE); 

		

		categoryUnitMap.put(MIDTERM_ID, midtermUnit);
		categoryUnitMap.put(FINAL_ID, finalUnit);
		categoryUnitMap.put(PAPER_ID, paperUnit);
		categoryUnitMap.put(SECTIONS_ID, sectionsUnit);
		categoryUnitMap.put(ECHW_ID, ecUnit);

		GradebookCalculationUnit gradebookCalculationUnit = new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);

		String[][] midtermValues = {
				{ "94", "100", "0.5", null },
				{ "94", "100", "0.5", null }
		};

		String[][] finalValues = {
				{ null, "100", "1.0", null }
		};

		String[][] paperValues = {
				{ null, "100", "1.0", null }
		};

		String[][] sectionsValues = {
				{ "100", "100", "0.1667", null },
				{ "100", "100", "0.1667", null },
				{ "100", "100", "0.1667", null },
				{ "100", "100", "0.1667", null },
				{ "100", "100", "0.1667", null },
				{ "100", "100", "0.1667", null }
		};

		String[][] ecValues = {
				{ "100", "100", "0.3333", null },
				{ null, "100", "0.3333", null },
				{ "0", "10", "0.3333", null }
		};

		ecUnit.setTotalNumberOfItems(3); 
		List<GradeRecordCalculationUnit> midtermUnits = getRecordUnits(midtermValues);
		List<GradeRecordCalculationUnit> finalUnits = getRecordUnits(finalValues);
		List<GradeRecordCalculationUnit> paperUnits = getRecordUnits(paperValues);
		List<GradeRecordCalculationUnit> sectionsUnits = getRecordUnits(sectionsValues);
		List<GradeRecordCalculationUnit> ecUnits = getRecordUnits(ecValues);

		
		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();

		categoryGradeUnitListMap.put(MIDTERM_ID, midtermUnits);
		categoryGradeUnitListMap.put(FINAL_ID, finalUnits);
		categoryGradeUnitListMap.put(PAPER_ID, paperUnits);
		categoryGradeUnitListMap.put(SECTIONS_ID, sectionsUnits);
		categoryGradeUnitListMap.put(ECHW_ID, ecUnits);
		
		Map<String, Boolean> isManuallyEqualWeightedMap = new HashMap<String, Boolean>();
		isManuallyEqualWeightedMap.put(MIDTERM_ID, Boolean.TRUE);
		isManuallyEqualWeightedMap.put(FINAL_ID, Boolean.FALSE);
		isManuallyEqualWeightedMap.put(PAPER_ID, Boolean.FALSE);
		isManuallyEqualWeightedMap.put(SECTIONS_ID, Boolean.TRUE);
		isManuallyEqualWeightedMap.put(ECHW_ID, Boolean.TRUE);
		
		BigDecimal totalGradebookPoints = new BigDecimal("100");

		BigDecimal result = gradebookCalculationUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, isManuallyEqualWeightedMap, totalGradebookPoints, false).stripTrailingZeros();
		// Full grade
		
		assertEquals(new BigDecimal("96.38"), getRoundedGrade(result));
		
	}		
	
	// variation on previous case, but w scaled EC and one nulled out EC assignment - we should get 1.5 from the DB
	public void testWeightedECEqualTmp4() {

		String MIDTERM_ID = "1";
		String FINAL_ID = "2";
		String PAPER_ID = "3";
		String SECTIONS_ID = "4";
		String ECHW_ID = "5";

		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		// Midterms - 2 assignments, equal weighted
		CategoryCalculationUnit midtermUnit = new CategoryCalculationUnitImpl(
				new BigDecimal(".4"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, TEST_SCALE); 

		// Final - 1 assignment 
		CategoryCalculationUnit finalUnit = new CategoryCalculationUnitImpl(
				new BigDecimal(".25"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE); 

		// paper - 1 assignment
		CategoryCalculationUnit paperUnit = new CategoryCalculationUnitImpl(
				new BigDecimal(".23"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE); 

		// section - 6 assignments equal weighted
		CategoryCalculationUnit sectionsUnit = new CategoryCalculationUnitImpl(
				new BigDecimal(".12"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, TEST_SCALE); 

		// extra credit homework extra credit category, 3 assignments
		CategoryCalculationUnit ecUnit = new CategoryCalculationUnitImpl(
				new BigDecimal(".03"), Integer.valueOf(0), Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, TEST_SCALE); 

		categoryUnitMap.put(MIDTERM_ID, midtermUnit);
		categoryUnitMap.put(FINAL_ID, finalUnit);
		categoryUnitMap.put(PAPER_ID, paperUnit);
		categoryUnitMap.put(SECTIONS_ID, sectionsUnit);
		categoryUnitMap.put(ECHW_ID, ecUnit);

		GradebookCalculationUnit gradebookCalculationUnit = new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);

		String[][] midtermValues = {
				{ "94", "100", "0.5", null },
				{ "94", "100", "0.5", null }
		};

		String[][] finalValues = {
				{ null, "100", "1.0", null }
		};

		String[][] paperValues = {
				{ null, "100", "1.0", null }
		};

		String[][] sectionsValues = {
				{ "100", "100", "0.1667", null },
				{ "100", "100", "0.1667", null },
				{ "100", "100", "0.1667", null },
				{ "100", "100", "0.1667", null },
				{ "100", "100", "0.1667", null },
				{ "100", "100", "0.1667", null }
		};

		String[][] ecValues = {
				{ "100", "100", "0.3333", null },
				{ null, "100", "0.3333", null },
				{ "0", "10", "0.3333", null }
		};

		ecUnit.setTotalNumberOfItems(3); 
		
		List<GradeRecordCalculationUnit> midtermUnits = getRecordUnits(midtermValues);
		List<GradeRecordCalculationUnit> finalUnits = getRecordUnits(finalValues);
		List<GradeRecordCalculationUnit> paperUnits = getRecordUnits(paperValues);
		List<GradeRecordCalculationUnit> sectionsUnits = getRecordUnits(sectionsValues);
		List<GradeRecordCalculationUnit> ecUnits = getRecordUnits(ecValues);

		
		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();

		categoryGradeUnitListMap.put(MIDTERM_ID, midtermUnits);
		categoryGradeUnitListMap.put(FINAL_ID, finalUnits);
		categoryGradeUnitListMap.put(PAPER_ID, paperUnits);
		categoryGradeUnitListMap.put(SECTIONS_ID, sectionsUnits);
		categoryGradeUnitListMap.put(ECHW_ID, ecUnits);
		
		Map<String, Boolean> isManuallyEqualWeightedMap = new HashMap<String, Boolean>();
		isManuallyEqualWeightedMap.put(MIDTERM_ID, Boolean.TRUE);
		isManuallyEqualWeightedMap.put(FINAL_ID, Boolean.FALSE);
		isManuallyEqualWeightedMap.put(PAPER_ID, Boolean.FALSE);
		isManuallyEqualWeightedMap.put(SECTIONS_ID, Boolean.TRUE);
		isManuallyEqualWeightedMap.put(ECHW_ID, Boolean.TRUE);
		
		BigDecimal totalGradebookPoints = new BigDecimal("100");

		BigDecimal result = gradebookCalculationUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, isManuallyEqualWeightedMap, totalGradebookPoints, true).stripTrailingZeros();
		// Full grade
		
		assertEquals(new BigDecimal("96.88"), getRoundedGrade(result));
		
	}		
	
	
	
	// utility methods
	
	private BigDecimal getResultForSingleCategoryWithEqualWeighting(String[][] hwValues, BigDecimal totalGradebookPoints, boolean roundToGradedValueScale, int dropLowest) 
	{
		BigDecimal ret = null; 
	
		if (hwValues != null && totalGradebookPoints != null)
		{
			Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();
			CategoryCalculationUnit hwUnit = new CategoryCalculationUnitImpl(new BigDecimal("1.0"), Integer.valueOf(dropLowest), null, Boolean.FALSE, Boolean.TRUE, TEST_SCALE);
			categoryUnitMap.put(HW_ID, hwUnit);
			GradebookCalculationUnit gradebookCalculationUnit = new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);
			List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);
			Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
			categoryGradeUnitListMap.put(HW_ID, hwUnits);
			
			
			
			if (roundToGradedValueScale)
			{
				ret = getRoundedGrade(gradebookCalculationUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, new HashMap<String, Boolean>(), totalGradebookPoints, false)); 
			}
			else
			{
				ret = gradebookCalculationUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, new HashMap<String, Boolean>(), totalGradebookPoints, false).stripTrailingZeros(); 
			}
		}
		
		return ret; 
	}
	
	
	private BigDecimal getRoundedGrade(BigDecimal inGrade)
	{
		if (inGrade == null)
			return null; 
		
		return inGrade.setScale(2, RoundingMode.HALF_UP);
	}
	
	private String[][] buildAssignmentArray(String pointsEarned, String pointsPossible, int iterations) {
		
		if (iterations <= 0)
			return null;
		
		String[][] ret = new String[iterations][4]; 
		
		BigDecimal weight = BigDecimal.ONE.divide(new BigDecimal(iterations), 10, RoundingMode.DOWN);
		
		for (int i = 0; i < iterations ; i++)
		{
			ret[i][0] = pointsEarned;
			ret[i][1] = pointsPossible;
			ret[i][2] = weight.toString();
			ret[i][3] = null; 
		}
		return ret; 
	}

	private List<GradeRecordCalculationUnit> getRecordUnits(String[][] matrix) {

		List<GradeRecordCalculationUnit> units = new ArrayList<GradeRecordCalculationUnit>();

		for (int i = 0; i < matrix.length; i++) {

			BigDecimal pointsEarned = matrix[i][0] == null ? null : new BigDecimal(matrix[i][0]);
			BigDecimal pointsPossible = matrix[i][1] == null ? null : new BigDecimal(matrix[i][1]);
			BigDecimal itemWeight = matrix[i][2] == null ? null : new BigDecimal(matrix[i][2]);
			Boolean extraCredit = matrix[i][3] == null ? Boolean.FALSE : Boolean.TRUE;

			units.add(new GradeRecordCalculationUnitImpl(pointsEarned, pointsPossible, itemWeight, extraCredit, TEST_SCALE));
		}

		return units;
	}
	
	
	
	public void testPartialWeighting() {
		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal(".7"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);
		CategoryCalculationUnit hwUnit = new CategoryCalculationUnitImpl(new BigDecimal(".3"), Integer.valueOf(0), null, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);

		categoryUnitMap.put(ESSAYS_ID, essayUnit);
		categoryUnitMap.put(HW_ID, hwUnit);

		GradebookCalculationUnit gradebookUnit =  new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);
		
		String[][] essayValues = {
				{  "9.0000000000",  "10.0000000000", "0.1666666667", null },
		};

		String[][] HWValues = {
				{  "9.0000000000",  "10.0000000000", "0.1666666667", null },
		};

		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> HWUnits = getRecordUnits(HWValues);
		
		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, HWUnits);
		
		Map<String, Boolean> isManuallyEqualWeightedMap = new HashMap<String, Boolean>();
		isManuallyEqualWeightedMap.put(ESSAYS_ID, Boolean.FALSE);
		isManuallyEqualWeightedMap.put(HW_ID, Boolean.FALSE);

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, isManuallyEqualWeightedMap, null, false);

		// GRBK-861
		assertTrue(courseGrade.compareTo(new BigDecimal("90.00")) == 0);
	}
	
	public void testGRBK_861_Part1() {
		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal("0.3"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);
		CategoryCalculationUnit hwUnit = new CategoryCalculationUnitImpl(new BigDecimal("0.7"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);

		categoryUnitMap.put(ESSAYS_ID, essayUnit);
		categoryUnitMap.put(HW_ID, hwUnit);

		GradebookCalculationUnit gradebookUnit =  new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);
	
		String[][] essayValues = {
				{"89.995", "100", "0.3", null},
				{"89.995", "100", "0.3", null},
				{"89.995", "100", "0.4", null}
		};
		
		// Leaving the HW grades out to trigger the special case where only one category has been graded, and that category triggers a 1/3 issue
		String[][] hwValues = {};
		
		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);

		Map<String, Boolean> isManuallyEqualWeightedMap = new HashMap<String, Boolean>();
		isManuallyEqualWeightedMap.put(ESSAYS_ID, Boolean.FALSE);
		isManuallyEqualWeightedMap.put(HW_ID, Boolean.FALSE);

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, isManuallyEqualWeightedMap, null, false);

		assertTrue(courseGrade.compareTo(new BigDecimal("89.995")) == 0);
	}
	
	public void testGRBK_862_Part1() {
		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal("1.0"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);

		categoryUnitMap.put(ESSAYS_ID, essayUnit);

		GradebookCalculationUnit gradebookUnit =  new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);
	
		String[][] essayValues = {
				{"89.995", "100", "0.33333", null},
				{"89.995", "100", "0.33333", null},
				{"89.995", "100", "0.33334", null}
		};
		
		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
	
		Map<String, Boolean> isManuallyEqualWeightedMap = new HashMap<String, Boolean>();
		isManuallyEqualWeightedMap.put(ESSAYS_ID, Boolean.FALSE);
		
		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, isManuallyEqualWeightedMap, new BigDecimal("300"), false);

		//assertEquals(new BigDecimal("89.99500000000"), courseGrade);
		assertTrue(courseGrade.compareTo(new BigDecimal("89.995")) == 0);
	}
	
	public void testGRBK_875_Part1() {
		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal("1.0"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);

		categoryUnitMap.put(ESSAYS_ID, essayUnit);

		GradebookCalculationUnit gradebookUnit =  new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);
	
		String[][] essayValues = {
				{"89.995", "100", "0.25", null},
				{"89.995", "100", "0.25", null},
				{"89.995", "100", "0.25", null},
				{null, "100", "0.25", null}
		};
		
		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);

		Map<String, Boolean> isManuallyEqualWeightedMap = new HashMap<String, Boolean>();
		isManuallyEqualWeightedMap.put(ESSAYS_ID, Boolean.FALSE);

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, isManuallyEqualWeightedMap, new BigDecimal("400"), false);

		assertTrue(courseGrade.compareTo(new BigDecimal("89.995")) == 0);
	}
	

	public void testGRBK_875_EQNull() {
		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal("1.0"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);

		categoryUnitMap.put(ESSAYS_ID, essayUnit);

		GradebookCalculationUnit gradebookUnit =  new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);
	
		String[][] essayValues = {
				{"89.995", "100", "0.25", null},
				{"89.995", "100", null, null},
				{"89.995", "100", null, null},
				{null, "100", "0.25", null}
		};
		
		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
	
		Map<String, Boolean> isManuallyEqualWeightedMap = new HashMap<String, Boolean>();
		isManuallyEqualWeightedMap.put(ESSAYS_ID, Boolean.FALSE);
		
		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, isManuallyEqualWeightedMap, new BigDecimal("400"), false);

		assertTrue(courseGrade.compareTo(new BigDecimal("89.995")) == 0);
	}

	public void testGRBK_875_ECCatSimEQWithNull() {
		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit ecUnit = new CategoryCalculationUnitImpl(new BigDecimal("1.0"), Integer.valueOf(0), Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);
		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal("1.0"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);
			
		ecUnit.setTotalNumberOfItems(4); 
		categoryUnitMap.put(ESSAYS_ID, essayUnit);
		categoryUnitMap.put(EC_ID, ecUnit);

		
		GradebookCalculationUnit gradebookUnit =  new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);
	
		
		String[][] essayValues = {
				{ "0", "100", "1.0", null }
		};

		String[][] ecValues = {
				{"89.995", "100", "0.25", "true"},
				{"89.995", "100", null, "true"},
				{"89.995", "100", null, "true"},
				{null, "100", "0.25", "true"}
		};
		
		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> ecUnits = getRecordUnits(ecValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(EC_ID, ecUnits);

		Map<String, Boolean> isManuallyEqualWeightedMap = new HashMap<String, Boolean>();
		isManuallyEqualWeightedMap.put(ESSAYS_ID, Boolean.FALSE);
		isManuallyEqualWeightedMap.put(EC_ID, Boolean.TRUE);

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, isManuallyEqualWeightedMap, new BigDecimal("400"), false);

		assertTrue(courseGrade.compareTo(new BigDecimal("44.9975")) == 0);
	}
	
	public void testGRBK_1255_ECCatSimEQWith() {
		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit ecUnit = new CategoryCalculationUnitImpl(new BigDecimal("0.1"), Integer.valueOf(0), Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);
		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal("1.0"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);
			
		ecUnit.setTotalNumberOfItems(4); 
		categoryUnitMap.put(ESSAYS_ID, essayUnit);
		categoryUnitMap.put(EC_ID, ecUnit);

		
		GradebookCalculationUnit gradebookUnit =  new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);
	
		
		String[][] essayValues = {
				{ "100", "100", "1.0", null }
		};

		String[][] ecValues = {
				{"3", "3", "0.3", "true"},
				{"3", "3", "0.3", "true"}
		};
		
		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> ecUnits = getRecordUnits(ecValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(EC_ID, ecUnits);
	
		Map<String, Boolean> isManuallyEqualWeightedMap = new HashMap<String, Boolean>();
		isManuallyEqualWeightedMap.put(ESSAYS_ID, Boolean.FALSE);
		isManuallyEqualWeightedMap.put(EC_ID, Boolean.FALSE);

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, isManuallyEqualWeightedMap, new BigDecimal("106"), false);

		assertTrue(courseGrade.compareTo(new BigDecimal("106")) == 0);
	}
	

	public void testGRBK_875_ECCatSimEQWithNullWithScaling() {
		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit ecUnit = new CategoryCalculationUnitImpl(new BigDecimal("1.0"), Integer.valueOf(0), Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);
		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal("1.0"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);
			
		ecUnit.setTotalNumberOfItems(4); 
		categoryUnitMap.put(ESSAYS_ID, essayUnit);
		categoryUnitMap.put(EC_ID, ecUnit);

		
		GradebookCalculationUnit gradebookUnit =  new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);
	
		
		String[][] essayValues = {
				{ "0", "100", "1.0", null }
		};

		String[][] ecValues = {
				{"89.995", "100", "0.25", "true"},
				{"89.995", "100", null, "true"},
				{"89.995", "100", null, "true"},
				{null, "100", "0.25", "true"}
		};
		
		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> ecUnits = getRecordUnits(ecValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(EC_ID, ecUnits);
	
		Map<String, Boolean> isManuallyEqualWeightedMap = new HashMap<String, Boolean>();
		isManuallyEqualWeightedMap.put(ESSAYS_ID, Boolean.FALSE);
		isManuallyEqualWeightedMap.put(EC_ID, Boolean.FALSE);

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, isManuallyEqualWeightedMap, new BigDecimal("400"), true);

		assertTrue(courseGrade.compareTo(new BigDecimal("89.995")) == 0);
	}


	
	public void testGRBK_861_Part2() {
		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal("1.0"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);

		categoryUnitMap.put(ESSAYS_ID, essayUnit);

		GradebookCalculationUnit gradebookUnit =  new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);
	
		String[][] essayValues = {
				{"89.995", "100", "0", null}
				
		};
		
		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);

		Map<String, Boolean> isManuallyEqualWeightedMap = new HashMap<String, Boolean>();
		isManuallyEqualWeightedMap.put(ESSAYS_ID, Boolean.FALSE);

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, isManuallyEqualWeightedMap, new BigDecimal("100"), false);
		assertNull(courseGrade);
	}
	
	public void testGRBK_877_Part1() {
		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal("0"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, TEST_SCALE);

		categoryUnitMap.put(ESSAYS_ID, essayUnit);

		GradebookCalculationUnit gradebookUnit =  new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);
	
		String[][] essayValues = {
				
				{"89.995", "100", "0", null}
		};
		
		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
	
		Map<String, Boolean> isManuallyEqualWeightedMap = new HashMap<String, Boolean>();
		isManuallyEqualWeightedMap.put(ESSAYS_ID, Boolean.FALSE);
		
		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, isManuallyEqualWeightedMap, new BigDecimal("100"), false);
		
		assertTrue(courseGrade.compareTo(BigDecimal.ZERO) == 0);
	}
	
	public void testGRBK_942_WC_1()
	{
		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal("1.0"), 
				Integer.valueOf(2), 
				Boolean.FALSE, 
				Boolean.FALSE, 
				Boolean.TRUE, 
				TEST_SCALE);

		categoryUnitMap.put(ESSAYS_ID, essayUnit);

		GradebookCalculationUnit gradebookUnit =  new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);
	
		
		String[][] essayValues = {
				{"42", "50", null, null},
				{"40", "50", null, null}
		};

		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
	
		Map<String, Boolean> isManuallyEqualWeightedMap = new HashMap<String, Boolean>();
		isManuallyEqualWeightedMap.put(ESSAYS_ID, Boolean.FALSE);

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, isManuallyEqualWeightedMap, new BigDecimal("100"), false);
		assertTrue(courseGrade.compareTo(new BigDecimal("82.0")) == 0);

	}

	public void testGRBK_942_WC_2()
	{
		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal("1.0"), 
				Integer.valueOf(1), 
				Boolean.FALSE, 
				Boolean.FALSE, 
				Boolean.TRUE, 
				TEST_SCALE);

		categoryUnitMap.put(ESSAYS_ID, essayUnit);

		GradebookCalculationUnit gradebookUnit =  new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);
	
		
		String[][] essayValues = {
				{"42", "50", null, null},
				{"40", "50", null, null}
		};

		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);

		Map<String, Boolean> isManuallyEqualWeightedMap = new HashMap<String, Boolean>();
		isManuallyEqualWeightedMap.put(ESSAYS_ID, Boolean.FALSE);

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, isManuallyEqualWeightedMap, new BigDecimal("100"), false);
		assertTrue(courseGrade.compareTo(new BigDecimal("84.0")) == 0);

	}
	
	public void testGRBK_942_WC_3()
	{
		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal("1.0"), 
				Integer.valueOf(1), 
				Boolean.FALSE, 
				Boolean.FALSE, 
				Boolean.TRUE, 
				TEST_SCALE);

		categoryUnitMap.put(ESSAYS_ID, essayUnit);

		GradebookCalculationUnit gradebookUnit =  new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);
	
		
		String[][] essayValues = {
				{"42", "50", null, null},
				{"10", "10", "0.10", "true"},
				{"40", "50", null, null}
		};

		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
	
		Map<String, Boolean> isManuallyEqualWeightedMap = new HashMap<String, Boolean>();
		isManuallyEqualWeightedMap.put(ESSAYS_ID, Boolean.FALSE);

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, isManuallyEqualWeightedMap, new BigDecimal("100"), false);
		assertTrue(courseGrade.compareTo(new BigDecimal("94.0")) == 0);

	}


	public void testGRBK_942_SC_1()
	{
		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal("1.0"), 
				Integer.valueOf(4), 
				Boolean.FALSE, 
				Boolean.TRUE, 
				Boolean.FALSE, 
				TEST_SCALE);

		categoryUnitMap.put(ESSAYS_ID, essayUnit);

		GradebookCalculationUnit gradebookUnit =  new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);
		
		String[][] essayValues = {
				{"35", "50", null, null},
				{"40", "50", null, null},
				{"20", "50", null, null},
				{"45", "50", null, null}
		};

		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);

		BigDecimal courseGrade = gradebookUnit.calculatePointsBasedCourseGrade(categoryGradeUnitListMap, new BigDecimal("200"), false);

		assertTrue(courseGrade.compareTo(new BigDecimal("70.0")) == 0);
	}
	
	public void testGRBK_942_SC_2()
	{
		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal("1.0"), 
				Integer.valueOf(2), 
				Boolean.FALSE, 
				Boolean.TRUE, 
				Boolean.FALSE, 
				TEST_SCALE);

		categoryUnitMap.put(ESSAYS_ID, essayUnit);

		GradebookCalculationUnit gradebookUnit =  new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);
		
		String[][] essayValues = {
				{"35", "50", null, null},
				{"40", "50", null, null},
				{"20", "50", null, null},
				{"45", "50", null, null}
		};

		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);

		BigDecimal courseGrade = gradebookUnit.calculatePointsBasedCourseGrade(categoryGradeUnitListMap, new BigDecimal("200"), false);

		assertTrue(courseGrade.compareTo(new BigDecimal("85.0")) == 0);
	}

	public void testGRBK_942_SC_3()
	{
		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal("1.0"), 
				Integer.valueOf(2), 
				Boolean.FALSE, 
				Boolean.TRUE, 
				Boolean.FALSE, 
				TEST_SCALE);

		categoryUnitMap.put(ESSAYS_ID, essayUnit);

		GradebookCalculationUnit gradebookUnit =  new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);
		
		String[][] essayValues = {
				{"35", "50", null, null},
				{"40", "50", null, null},
				{"20", "50", null, null},
				{"7", "10", null, "true"},
				{"45", "50", null, null}
		};

		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);

		BigDecimal courseGrade = gradebookUnit.calculatePointsBasedCourseGrade(categoryGradeUnitListMap, new BigDecimal("220"), false);

		assertTrue(courseGrade.compareTo(new BigDecimal("92.0")) == 0);
	}


}
