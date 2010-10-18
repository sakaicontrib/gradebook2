package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.math.BigDecimal;
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

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal(".6"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, TEST_SCALE);
		CategoryCalculationUnit hwUnit = new CategoryCalculationUnitImpl(new BigDecimal(".4"), Integer.valueOf(0), null, Boolean.FALSE, TEST_SCALE);
		CategoryCalculationUnit ecUnit = new CategoryCalculationUnitImpl(new BigDecimal(".1"), Integer.valueOf(0), Boolean.TRUE, Boolean.FALSE, TEST_SCALE);

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

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal(".6"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, TEST_SCALE);
		CategoryCalculationUnit hwUnit = new CategoryCalculationUnitImpl(new BigDecimal(".2"), Integer.valueOf(0), null, Boolean.FALSE, TEST_SCALE);
		CategoryCalculationUnit ecUnit = new CategoryCalculationUnitImpl(new BigDecimal(".1"), Integer.valueOf(0), Boolean.TRUE, Boolean.FALSE, TEST_SCALE);

		CategoryCalculationUnit emptyUnit = new CategoryCalculationUnitImpl(new BigDecimal("0"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, TEST_SCALE);

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

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal(".6"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, TEST_SCALE);
		CategoryCalculationUnit hwUnit = new CategoryCalculationUnitImpl(new BigDecimal(".4"), Integer.valueOf(0), null, Boolean.FALSE, TEST_SCALE);
		CategoryCalculationUnit ecUnit = new CategoryCalculationUnitImpl(new BigDecimal(".1"), Integer.valueOf(0), Boolean.TRUE, Boolean.FALSE, TEST_SCALE);

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

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal(".7"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, TEST_SCALE);
		CategoryCalculationUnit hwUnit = new CategoryCalculationUnitImpl(new BigDecimal(".3"), Integer.valueOf(0), null, Boolean.FALSE, TEST_SCALE);
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
