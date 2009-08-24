/**********************************************************************************
 *
 * $Id:$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2008, 2009 The Regents of the University of California
 *
 * Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.gradebook2.test;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.sakaiproject.gradebook.gwt.sakai.calculations.CategoryCalculationUnit;
import org.sakaiproject.gradebook.gwt.sakai.calculations.GradeRecordCalculationUnit;
import org.sakaiproject.gradebook.gwt.sakai.calculations.GradebookCalculationUnit;
import org.sakaiproject.gradebook.gwt.sakai.calculations.Calculation.Mode;

public class GradeCalculationTest extends TestCase {

	private static final String ESSAYS_ID = "1";
	private static final String HW_ID = "2";
	private static final String EC_ID = "3";

	private GradebookCalculationUnit gradebookUnit;

	public GradeCalculationTest(String name) {
		super(name);
	}


	public void testPerfectPoints() {

		Double[][] values = {
				{  5d,  5d, 0.20d, null },
				{  9d,  9d, 0.20d, null },
				{ 10d, 10d, 0.10d, null },
				{ 10d, 10d, 0.10d, null },
				{ 20d, 20d, 0.40d, null },
				{ 10d, 10d, 0.30d, null },
				{ 10d, 10d, 0.30d, null },
				{ 10d, 10d, 0.40d, null }
		};


		List<GradeRecordCalculationUnit> units = getRecordUnits(values);
		BigDecimal totalGradebookPoints = BigDecimal.ZERO;
		BigDecimal courseGrade = gradebookUnit.calculatePointsBasedCourseGrade(units, totalGradebookPoints, false);

		assertEqualsAtScale2(BigDecimal.valueOf(100.00d), courseGrade);
	}

	public void testZeroPoints() {

		Double[][] values = {
				{  0d,  5d, 0.20d, null },
				{  0d,  9d, 0.20d, null },
				{  0d, 10d, 0.10d, null },
				{  0d, 10d, 0.10d, null },
				{  0d, 20d, 0.40d, null },
				{  0d, 10d, 0.30d, null },
				{  0d, 10d, 0.30d, null },
				{  0d, 10d, 0.40d, null }
		};


		List<GradeRecordCalculationUnit> units = getRecordUnits(values);
		BigDecimal totalGradebookPoints = BigDecimal.ZERO;
		BigDecimal courseGrade = gradebookUnit.calculatePointsBasedCourseGrade(units, totalGradebookPoints, false);

		assertEqualsAtScale2(BigDecimal.valueOf(0.00d), courseGrade);
	}

	public void testEightyPercentPoints() {

		Double[][] values = {
				{  4d,  5d, 0.20d, null },
				{null,  9d, 0.20d, null },
				{  8d, 10d, 0.10d, null },
				{  8d, 10d, 0.10d, null },
				{ 16d, 20d, 0.40d, null },
				{  8d, 10d, 0.30d, null },
				{  8d, 10d, 0.30d, null },
				{  8d, 10d, 0.40d, null }
		};


		List<GradeRecordCalculationUnit> units = getRecordUnits(values);
		BigDecimal totalGradebookPoints = BigDecimal.ZERO;
		BigDecimal courseGrade = gradebookUnit.calculatePointsBasedCourseGrade(units, totalGradebookPoints, false);

		assertEqualsAtScale2(BigDecimal.valueOf(80.00d), courseGrade);
	}

	public void testZeroBothPointsCategories() {
		Double[][] essayValues = {
				{  0d,  5d, 0.20d, null },
				{  0d,  9d, 0.20d, null },
				{  0d, 10d, 0.10d, null },
				{  0d, 10d, 0.10d, null },
				{  0d, 20d, 0.40d, null }
		};

		Double[][] hwValues = {
				{  0d, 10d, 0.30d, null },
				{  0d, 10d, 0.30d, null },
				{  0d, 10d, 0.40d, null }
		};

		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);
		BigDecimal totalGradebookPoints = BigDecimal.ZERO;
		BigDecimal courseGrade = gradebookUnit.calculatePointsBasedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);

		assertEqualsAtScale2(BigDecimal.valueOf(0.00d), courseGrade);
	}

	public void testZeroEssaysPerfectHWPointsCategories() {
		Double[][] essayValues = {
				{  0d, 20d, 0.10d, null },
				{  0d, 20d, 0.10d, null },
				{  0d, 20d, 0.40d, null }
		};

		Double[][] hwValues = {
				{ 20d, 20d, 0.30d, null },
				{ 10d, 10d, 0.30d, null },
				{ 10d, 10d, 0.40d, null }
		};


		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);


		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);
		BigDecimal totalGradebookPoints = BigDecimal.ZERO;
		BigDecimal courseGrade = gradebookUnit.calculatePointsBasedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);

		assertEqualsAtScale2(BigDecimal.valueOf(40.00d), courseGrade);
	}


	public void testPartialWeighting() {

		Double[][] essayValues = {
				{  5d,  5d, 0.20d, null },
				{  9d,  9d, 0.20d, null },
				{null, 10d, 0.10d, null },
				{null, 10d, 0.10d, null },
				{null, 20d, 0.40d, null }
		};

		Double[][] hwValues = {
				{null, 10d, 0.30d, null },
				{null, 10d, 0.30d, null },
				{null, 10d, 0.40d, null }
		};

		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap);

		assertEqualsAtScale2(BigDecimal.valueOf(100.00d), courseGrade);
	}


	public void testZeroBothWeighting() {
		Double[][] essayValues = {
				{  0d,  5d, 0.20d, null },
				{  0d,  9d, 0.20d, null },
				{  0d, 10d, 0.10d, null },
				{  0d, 10d, 0.10d, null },
				{  0d, 20d, 0.40d, null }
		};

		Double[][] hwValues = {
				{  0d, 10d, 0.30d, null },
				{  0d, 10d, 0.30d, null },
				{  0d, 10d, 0.40d, null }
		};

		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap);

		assertEqualsAtScale2(BigDecimal.valueOf(0.00d), courseGrade);
	}

	public void testZeroEssaysPerfectHWWeighting() {
		Double[][] essayValues = {
				{  0d,  5d, 0.20d, null },
				{  0d,  9d, 0.20d, null },
				{  0d, 10d, 0.10d, null },
				{  0d, 10d, 0.10d, null },
				{  0d, 20d, 0.40d, null }
		};

		Double[][] hwValues = {
				{ 10d, 10d, 0.30d, null },
				{ 10d, 10d, 0.30d, null },
				{ 10d, 10d, 0.40d, null }
		};


		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);


		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap);

		assertEqualsAtScale2(BigDecimal.valueOf(40.00d), courseGrade);
	}


	public void testPerfectEssaysZeroHWWeighting() {

		Double[][] essayValues = {
				{  5d,  5d, 0.20d, null },
				{  9d,  9d, 0.20d, null },
				{ 10d, 10d, 0.10d, null },
				{ 10d, 10d, 0.10d, null },
				{ 20d, 20d, 0.40d, null }
		};

		Double[][] hwValues = {
				{  0d, 10d, 0.30d, null },
				{  0d, 10d, 0.30d, null },
				{  0d, 10d, 0.40d, null }
		};


		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);


		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap);

		assertEqualsAtScale2(BigDecimal.valueOf(60.00d), courseGrade);
	}


	public void testEightyPercentEverywhereWeighting() {

		Double[][] essayValues = {
				{  4d,  5d, 0.20d, null },
				{null,  9d, 0.20d, null },
				{  8d, 10d, 0.10d, null },
				{  8d, 10d, 0.10d, null },
				{ 16d, 20d, 0.40d, null }
		};

		Double[][] hwValues = {
				{  8d, 10d, 0.30d, null },
				{  8d, 10d, 0.30d, null },
				{  8d, 10d, 0.40d, null }
		};


		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap);

		assertEqualsAtScale2(BigDecimal.valueOf(80.00d), courseGrade);
	}

	public void testNinetyPercentEverywhereWeighting() {

		Double[][] essayValues = {
				{  4.5d,  5d, 0.20d, null },
				{null,  9d, 0.20d, null },
				{  9d, 10d, 0.10d, null },
				{  9d, 10d, 0.10d, null },
				{ 18d, 20d, 0.40d, null }
		};

		Double[][] hwValues = {
				{  9d, 10d, 0.30d, null },
				{  9d, 10d, 0.30d, null },
				{  9d, 10d, 0.40d, null }
		};


		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap);

		assertEqualsAtScale2(BigDecimal.valueOf(90.00d), courseGrade);
	}


	public void testPerfectWeighting() {

		Double[][] essayValues = {
				{null,  5d, 0.20d, null },
				{  9d,  9d, 0.20d, null },
				{ 10d, 10d, 0.10d, null },
				{ 10d, 10d, 0.10d, null },
				{ 20d, 20d, 0.40d, null }
		};

		Double[][] hwValues = {
				{ 10d, 10d, 0.30d, null },
				{ 10d, 10d, 0.30d, null },
				{ 10d, 10d, 0.40d, null }
		};


		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap);

		assertEqualsAtScale2(BigDecimal.valueOf(100.00d), courseGrade);
	}

	public void testEightyPercentManyEssaysWeighting() {

		int numberOfEssays = 400;

		Double[][] essayValues = new Double[numberOfEssays][4];

		for (int i=0;i<numberOfEssays;i++) {
			essayValues[i][0] = 8d;
			essayValues[i][1] = 10d;
			essayValues[i][2] = 0.0025d; 
			essayValues[i][3] = null;
		}

		Double[][] hwValues = {
				{  8d, 10d, 0.30d, null },
				{  8d, 10d, 0.30d, null },
				{  8d, 10d, 0.40d, null }
		};


		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);

		long start = System.currentTimeMillis();

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap);

		long end = System.currentTimeMillis();

		assertEqualsAtScale2(BigDecimal.valueOf(80.00d), courseGrade);

		// Performance check -- should be able to calculate this in under a tenth of a second : will obviously depend on hardware
		assertTrue((end-start) < 100);
	}


	public void test106PercentEssaysWeighting() {

		Double[][] essayValues = {
				{  5d,  5d, 0.20d, null },
				{  9d,  9d, 0.20d, null },
				{ 10d, 10d, 0.10d, null },
				{ 10d, 10d, 0.10d, null },
				{ 20d, 20d, 0.40d, null },
				{ 10d, 10d, 0.10d, 1d }
		};

		Double[][] hwValues = {
				{ 10d, 10d, 0.30d, null },
				{ 10d, 10d, 0.30d, null },
				{ 10d, 10d, 0.40d, null }
		};


		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap);

		assertEqualsAtScale2(BigDecimal.valueOf(100.00d), courseGrade);
	}

	public void testPerfectWithExtraCreditCategoryWeighting() {

		Double[][] essayValues = {
				{  5d,  5d, 0.20d, null },
				{  9d,  9d, 0.20d, null },
				{ 10d, 10d, 0.10d, null },
				{ 10d, 10d, 0.10d, null },
				{ 20d, 20d, 0.40d, null }
		};

		Double[][] hwValues = {
				{ 10d, 10d, 0.30d, null },
				{ 10d, 10d, 0.30d, null },
				{ 10d, 10d, 0.40d, null }
		};

		Double[][] ecValues = {
				{ 10d, 10d, 1.00d, null }
		};

		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);
		List<GradeRecordCalculationUnit> ecUnits = getRecordUnits(ecValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);
		categoryGradeUnitListMap.put(EC_ID, ecUnits);

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap);

		assertEqualsAtScale2(BigDecimal.valueOf(110.00d), courseGrade);
	}

	protected void setUp() throws Exception {
		super.setUp();

		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnit(new BigDecimal(".6"), Integer.valueOf(0), Boolean.FALSE);
		CategoryCalculationUnit hwUnit = new CategoryCalculationUnit(new BigDecimal(".4"), Integer.valueOf(0), null);
		CategoryCalculationUnit ecUnit = new CategoryCalculationUnit(new BigDecimal(".1"), Integer.valueOf(0), Boolean.TRUE);

		categoryUnitMap.put(ESSAYS_ID, essayUnit);
		categoryUnitMap.put(HW_ID, hwUnit);
		categoryUnitMap.put(EC_ID, ecUnit);

		gradebookUnit = new GradebookCalculationUnit(categoryUnitMap);

	}

	protected void tearDown() throws Exception {
		super.tearDown();

		gradebookUnit = null;
	}


	private List<GradeRecordCalculationUnit> getRecordUnits(Double[][] matrix) {
		List<GradeRecordCalculationUnit> units = new ArrayList<GradeRecordCalculationUnit>();

		for (int i=0;i<matrix.length;i++) {
			BigDecimal pointsEarned = matrix[i][0] == null ? null : BigDecimal.valueOf(matrix[i][0]);
			BigDecimal pointsPossible = matrix[i][1] == null ? null : BigDecimal.valueOf(matrix[i][1]);
			BigDecimal itemWeight = matrix[i][2] == null ? null : BigDecimal.valueOf(matrix[i][2]);
			Boolean extraCredit = matrix[i][3] == null ? Boolean.FALSE : Boolean.TRUE;

			units.add(new GradeRecordCalculationUnit(pointsEarned, pointsPossible, itemWeight, extraCredit));
		}

		return units;
	}


	private void assertEqualsAtScale2(BigDecimal first, BigDecimal second) {
		assertTrue(first.setScale(2, RoundingMode.HALF_EVEN).equals(second.setScale(2, RoundingMode.HALF_EVEN)));
	}

}
