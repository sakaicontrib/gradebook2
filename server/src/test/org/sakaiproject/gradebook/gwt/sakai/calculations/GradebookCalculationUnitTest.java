package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.sakai.calculations2.CategoryCalculationUnit;
import org.sakaiproject.gradebook.gwt.sakai.calculations2.CategoryCalculationUnitImpl;
import org.sakaiproject.gradebook.gwt.sakai.calculations2.GradeRecordCalculationUnit;
import org.sakaiproject.gradebook.gwt.sakai.calculations2.GradeRecordCalculationUnitImpl;
import org.sakaiproject.gradebook.gwt.sakai.calculations2.GradebookCalculationUnit;
import org.sakaiproject.gradebook.gwt.sakai.calculations2.GradebookCalculationUnitImpl;

import junit.framework.TestCase;

public class GradebookCalculationUnitTest extends TestCase {

	private final int TEST_SCALE = 50;

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

		BigDecimal totalGradebookPoints = new BigDecimal("100");

		BigDecimal result = gradebookCalculationUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);

		assertEquals(new BigDecimal("80.000000"), result);
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

	
	public BigDecimal getResultForSingleCategoryWithEqualWeighting(String[][] hwValues, BigDecimal totalGradebookPoints, boolean roundToGradedValueScale, int dropLowest) 
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
				ret = getRoundedGrade(gradebookCalculationUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false)); 
			}
			else
			{
				ret = gradebookCalculationUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false).stripTrailingZeros(); 
			}
		}
		
		return ret; 
	}
	
	
	public BigDecimal getRoundedGrade(BigDecimal inGrade)
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
		//CategoryCalculationUnit ecUnit = new CategoryCalculationUnit(new BigDecimal(".1"), Integer.valueOf(0), Boolean.TRUE, Boolean.FALSE);

		categoryUnitMap.put(ESSAYS_ID, essayUnit);
		categoryUnitMap.put(HW_ID, hwUnit);
		//categoryUnitMap.put(EC_ID, ecUnit);

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
		


		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, null, false);

		assertEquals(new BigDecimal("89.99999999999999999999999999999999999999999999999999999999424000"), courseGrade);
	}
	
}
