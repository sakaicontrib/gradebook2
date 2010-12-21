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

package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;


import org.sakaiproject.gradebook.gwt.sakai.calculations.BigDecimalCalculationsWrapper;
import org.sakaiproject.gradebook.gwt.sakai.calculations.CategoryCalculationUnit;
import org.sakaiproject.gradebook.gwt.sakai.calculations.CategoryCalculationUnitImpl;
import org.sakaiproject.gradebook.gwt.sakai.calculations.GradeRecordCalculationUnit;
import org.sakaiproject.gradebook.gwt.sakai.calculations.GradeRecordCalculationUnitImpl;
import org.sakaiproject.gradebook.gwt.sakai.calculations.GradebookCalculationUnit;
import org.sakaiproject.gradebook.gwt.sakai.calculations.GradebookCalculationUnitImpl;

public class GradeCalculationTest2 extends TestCase {

	private static final String ESSAYS_ID = "1";
	private static final String HW_ID = "2";
	private static final String EC_ID = "3";
	private static final String EMPTY_ID = "4";
	
	private final int TEST_SCALE = 50;

	private BigDecimal totalGradebookPoints = null;
	
	public GradeCalculationTest2(String name) {
		super(name);
	}

	public void testPerfectPoints() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditGradebook();
		
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

		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditGradebook();
		
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
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditGradebook();
		
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
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditGradebook();
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
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditGradebook();
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
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditGradebook();
		
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

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);

		assertEqualsAtScale2(BigDecimal.valueOf(100.00d), courseGrade);
	}


	public void testZeroBothWeighting() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditGradebook();
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

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);

		assertEqualsAtScale2(BigDecimal.valueOf(0.00d), courseGrade);
	}

	public void testZeroEssaysPerfectHWWeighting() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditGradebook();
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

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);

		assertEqualsAtScale2(BigDecimal.valueOf(40.00d), courseGrade);
	}


	public void testPerfectEssaysZeroHWWeighting() {

		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditGradebook();
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

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);

		assertEqualsAtScale2(BigDecimal.valueOf(60.00d), courseGrade);
	}


	public void testEightyPercentEverywhereWeighting() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditGradebook();
		
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

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);

		assertEqualsAtScale2(BigDecimal.valueOf(80.00d), courseGrade);
	}

	public void testNinetyPercentEverywhereWeighting() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditGradebook();
		
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

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);

		assertEqualsAtScale2(BigDecimal.valueOf(90.00d), courseGrade);
	}


	public void testPerfectWeighting() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditGradebook();
		
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

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);

		assertEqualsAtScale2(BigDecimal.valueOf(100.00d), courseGrade);
	}

	public void testEightyPercentManyEssaysWeighting() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditGradebook();
		
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

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);

		long end = System.currentTimeMillis();

		assertEqualsAtScale2(BigDecimal.valueOf(80.00d), courseGrade);

		// Performance check -- should be able to calculate this in under a tenth of a second : will obviously depend on hardware
		assertTrue((end-start) < 100);
	}


	public void test106PercentEssaysWeighting() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditGradebook();
		
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

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);

		assertEqualsAtScale2(BigDecimal.valueOf(100.00d), courseGrade);
	}

	public void testPerfectWithExtraCreditCategoryWeighting() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditGradebook();
		
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

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);

		assertEqualsAtScale2(BigDecimal.valueOf(110.00d), courseGrade);
	}

	
	
	public void testPerfectPointsIncomplete() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditEmptyIncompleteGradebook();

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
	
	public void testZeroPointsIncomplete() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditEmptyIncompleteGradebook();

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
	
	public void testEightyPercentPointsIncomplete() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditEmptyIncompleteGradebook();

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
	
	public void testZeroBothPointsCategoriesIncomplete() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditEmptyIncompleteGradebook();
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
	
	public void testZeroEssaysPerfectHWPointsCategoriesIncomplete() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditEmptyIncompleteGradebook();
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
	
	
	public void testPartialWeightingIncomplete() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditEmptyIncompleteGradebook();
		
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
		
		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);
	
		assertEqualsAtScale2(BigDecimal.valueOf(80.00d), courseGrade);
	}
	
	
	public void testZeroBothWeightingIncomplete() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditEmptyIncompleteGradebook();
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
		
		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);
	
		assertEqualsAtScale2(BigDecimal.valueOf(0.00d), courseGrade);
	}
	
	public void testZeroEssaysPerfectHWWeightingIncomplete() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditEmptyIncompleteGradebook();
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
		
		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);
	
		assertEqualsAtScale2(BigDecimal.valueOf(20.00d), courseGrade);
	}
	
	
	public void testPerfectEssaysZeroHWWeightingIncomplete() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditEmptyIncompleteGradebook();

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
		
		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);
	
		assertEqualsAtScale2(BigDecimal.valueOf(60.00d), courseGrade);
	}
	
	
	public void testEightyPercentEverywhereWeightingIncomplete() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditEmptyIncompleteGradebook();

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
		
		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);
	
		assertEqualsAtScale2(BigDecimal.valueOf(64.00d), courseGrade);
	}
	
	public void testNinetyPercentEverywhereWeightingIncomplete() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditEmptyIncompleteGradebook();

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
		
		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);
	
		assertEqualsAtScale2(BigDecimal.valueOf(72.00d), courseGrade);
	}
	
	
	public void testPerfectWeightingIncomplete() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditEmptyIncompleteGradebook();
		
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

		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);

		assertEqualsAtScale2(BigDecimal.valueOf(80.00d), courseGrade);
	}
	
	public void testEightyPercentManyEssaysWeightingIncomplete() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditEmptyIncompleteGradebook();
		
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
		
		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);
	
		long end = System.currentTimeMillis();
		
		assertEqualsAtScale2(BigDecimal.valueOf(64.00d), courseGrade);
		
		// Performance check -- should be able to calculate this in under a tenth of a second : will obviously depend on hardware
		assertTrue((end-start) < 100);
	}
	
	
	public void test106PercentEssaysWeightingIncomplete() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditEmptyIncompleteGradebook();

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
		
		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);

		assertEqualsAtScale2(BigDecimal.valueOf(80.00d), courseGrade);
	}

	
	public void testPerfectWithExtraCreditCategoryWeightingIncomplete() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditEmptyIncompleteGradebook();
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
		
		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);

		assertEqualsAtScale2(BigDecimal.valueOf(90.00d), courseGrade);
	}
	
	public void testEightyWithExtraCreditItemCategoryWeighting() {
		GradebookCalculationUnit gradebookUnit = getEssaysGradebook();
		Double[][] essayValues = {
				{  8d, 10d, 0.20d, null },
				{  8d, 10d, 0.20d, null },
				{  8d, 10d, 0.20d, null },
				{  8d, 10d, 0.20d, null },
				{  null, 10d, 0.20d, null },
				{  10d, 10d, 0.10d, 1d }
		};

		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);

		BigDecimal totalGradebookPoints = null;
		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);

		assertEqualsAtScale2(BigDecimal.valueOf(90.00d), courseGrade);
	}
	
	public void testPerfectWeightedNotExtraCreditScaled10and1() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditEmptyCompleteGradebook();
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
				{ 10d, 10d, .50d, 1d },
				{  1d, 10d, .50d, 1d }
		};
		
		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);
		List<GradeRecordCalculationUnit> ecUnits = getRecordUnits(ecValues);
				
		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);
		categoryGradeUnitListMap.put(EC_ID, ecUnits);
		
		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);

		assertEqualsAtScale2(BigDecimal.valueOf(105.50d), courseGrade);
	}
	
	// Should have same result as above, since they are both entered with a value
	public void testPerfectWeightedExtraCreditScaled10and1() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditEmptyCompleteGradebook();
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
				{ 10d, 10d, .50d, 1d },
				{  1d, 10d, .50d, 1d }
		};
		
		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);
		List<GradeRecordCalculationUnit> ecUnits = getRecordUnits(ecValues);
				
		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);
		categoryGradeUnitListMap.put(EC_ID, ecUnits);
		
		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, true);

		assertEqualsAtScale2(BigDecimal.valueOf(105.50d), courseGrade);
	}
	
	public void testPerfectWeightedNotExtraCreditScaled10andBlank() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditEmptyCompleteGradebook();
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
				{ 10d, 10d, .50d, 1d },
				{ null, 10d, .50d, 1d }
		};
		
		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);
		List<GradeRecordCalculationUnit> ecUnits = getRecordUnits(ecValues);
				
		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);
		categoryGradeUnitListMap.put(EC_ID, ecUnits);
		
		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);

		assertEqualsAtScale2(BigDecimal.valueOf(105.00d), courseGrade);
	}
	
	public void testPerfectWeightedExtraCreditScaled10andBlank() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditEmptyCompleteGradebook();
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
				{ 10d, 10d, .50d, 1d },
				{ null, 10d, .50d, 1d }
		};
		
		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);
		List<GradeRecordCalculationUnit> ecUnits = getRecordUnits(ecValues);
				
		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);
		categoryGradeUnitListMap.put(EC_ID, ecUnits);
		
		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, true);

		assertEqualsAtScale2(BigDecimal.valueOf(110.00d), courseGrade);
	}
	
	
	public void testPerfectWeightedNotExtraCreditScaled1andBlank() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditEmptyCompleteGradebook();
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
				{ 1d, 10d, .50d, 1d },
				{ null, 10d, .50d, 1d }
		};
		
		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);
		List<GradeRecordCalculationUnit> ecUnits = getRecordUnits(ecValues);
				
		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);
		categoryGradeUnitListMap.put(EC_ID, ecUnits);
		
		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);

		assertEqualsAtScale2(BigDecimal.valueOf(100.50d), courseGrade);
	}
	
	public void testPerfectWeightedExtraCreditScaled1andBlank() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditEmptyCompleteGradebook();
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
				{ 1d, 10d, .50d, 1d },
				{ null, 10d, .50d, 1d }
		};
		
		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);
		List<GradeRecordCalculationUnit> ecUnits = getRecordUnits(ecValues);
				
		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);
		categoryGradeUnitListMap.put(EC_ID, ecUnits);
		
		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, true);

		assertEqualsAtScale2(BigDecimal.valueOf(101.00d), courseGrade);
	}
	
	
	public void testPerfectWeightedNotExtraCreditScaled3andBlank() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditEmptyCompleteGradebook();
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
				{ 3d, 10d, .50d, 1d },
				{ null, 10d, .50d, 1d }
		};
		
		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);
		List<GradeRecordCalculationUnit> ecUnits = getRecordUnits(ecValues);
				
		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);
		categoryGradeUnitListMap.put(EC_ID, ecUnits);
		
		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, false);

		assertEqualsAtScale2(BigDecimal.valueOf(101.50d), courseGrade);
	}
	
	public void testPerfectWeightedExtraCreditScaled3andBlank() {
		GradebookCalculationUnit gradebookUnit = getEssaysHomeworkExtraCreditEmptyCompleteGradebook();
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
				{ 3d, 10d, .50d, 1d },
				{ null, 10d, .50d, 1d }
		};
		
		List<GradeRecordCalculationUnit> essayUnits = getRecordUnits(essayValues);
		List<GradeRecordCalculationUnit> hwUnits = getRecordUnits(hwValues);
		List<GradeRecordCalculationUnit> ecUnits = getRecordUnits(ecValues);
				
		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		categoryGradeUnitListMap.put(ESSAYS_ID, essayUnits);
		categoryGradeUnitListMap.put(HW_ID, hwUnits);
		categoryGradeUnitListMap.put(EC_ID, ecUnits);
		
		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, true);

		assertEqualsAtScale2(BigDecimal.valueOf(103.00d), courseGrade);
	}
	
	public void testBigDecimalCalculationsWrapperDivide() {
		
		BigDecimalCalculationsWrapper calc = new BigDecimalCalculationsWrapper();
		
		BigDecimal two = new BigDecimal("2");
		BigDecimal three = new BigDecimal("3");
		
		BigDecimal result = calc.divide(two, three);
		
		BigDecimal compare = new BigDecimal("0.66666666666666666666666666666666666666666666666667");
		assertTrue(result.equals(compare));
	}
	
	public void testBigDecimalCalculationsWrapperDivideByZero() {
		
		BigDecimalCalculationsWrapper calc = new BigDecimalCalculationsWrapper();

		BigDecimal two = new BigDecimal("2");
		BigDecimal zero = new BigDecimal("0");

		Exception arithmeticException = null;
		try {
			
			BigDecimal result = calc.divide(two, zero);
		}
		catch(ArithmeticException ae) {
			arithmeticException = ae;
		}
		
		assertNotNull(arithmeticException);

	}

	public void testBigDecimalCalculationsWrapperMultiply() {

		BigDecimalCalculationsWrapper calc = new BigDecimalCalculationsWrapper();

		BigDecimal num1 = new BigDecimal("20.0011");
		BigDecimal num2 = new BigDecimal("44.0102030405060708090");

		BigDecimal result = calc.multiply(num1, num2);
		BigDecimal compare = new BigDecimal("880.25247203346597285788990");
		
		assertTrue(result.equals(compare));
	}
	
	public void testBigDecimalCalculationsWrapperAdd() {
		
		BigDecimalCalculationsWrapper calc = new BigDecimalCalculationsWrapper();

		BigDecimal num1 = new BigDecimal("99.1317");
		BigDecimal num2 = new BigDecimal("81.191");

		BigDecimal result = calc.add(num1, num2);
		BigDecimal compare = new BigDecimal("180.3227");
		
		assertTrue(result.equals(compare));
	}
	
	public void testBigDecimalCalculationsWrappersubtract() {

		BigDecimalCalculationsWrapper calc = new BigDecimalCalculationsWrapper();

		BigDecimal num1 = new BigDecimal("99.1317");
		BigDecimal num2 = new BigDecimal("81.191");

		BigDecimal result = calc.subtract(num1, num2);
		BigDecimal compare = new BigDecimal("17.9407");

		assertTrue(result.equals(compare));
	}

	protected GradebookCalculationUnit getEssaysGradebook() {
		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal("1.0"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);

		categoryUnitMap.put(ESSAYS_ID, essayUnit);


		return new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);
	}
	
	protected GradebookCalculationUnit getEssaysHomeworkExtraCreditGradebook() {
		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal(".6"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);
		CategoryCalculationUnit hwUnit = new CategoryCalculationUnitImpl(new BigDecimal(".4"), Integer.valueOf(0), null, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);
		CategoryCalculationUnit ecUnit = new CategoryCalculationUnitImpl(new BigDecimal(".1"), Integer.valueOf(0), Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);

		categoryUnitMap.put(ESSAYS_ID, essayUnit);
		categoryUnitMap.put(HW_ID, hwUnit);
		categoryUnitMap.put(EC_ID, ecUnit);

		return new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);
	}
	
	
	protected GradebookCalculationUnit getEssaysHomeworkExtraCreditEmptyIncompleteGradebook() {
		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();
		
		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal(".6"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);
		CategoryCalculationUnit hwUnit = new CategoryCalculationUnitImpl(new BigDecimal(".2"), Integer.valueOf(0), null, Boolean.FALSE,Boolean.FALSE, TEST_SCALE);
		CategoryCalculationUnit ecUnit = new CategoryCalculationUnitImpl(new BigDecimal(".1"), Integer.valueOf(0), Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);
		
		CategoryCalculationUnit emptyUnit = new CategoryCalculationUnitImpl(new BigDecimal("0"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE,TEST_SCALE);
		
		categoryUnitMap.put(ESSAYS_ID, essayUnit);
		categoryUnitMap.put(HW_ID, hwUnit);
		categoryUnitMap.put(EC_ID, ecUnit);
		categoryUnitMap.put(EMPTY_ID, emptyUnit);
		
		
		return new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);
	}

	protected GradebookCalculationUnit getEssaysHomeworkExtraCreditEmptyCompleteGradebook() {
		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();
		
		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal(".6"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);
		CategoryCalculationUnit hwUnit = new CategoryCalculationUnitImpl(new BigDecimal(".4"), Integer.valueOf(0), null, Boolean.FALSE, Boolean.FALSE,TEST_SCALE);
		CategoryCalculationUnit ecUnit = new CategoryCalculationUnitImpl(new BigDecimal(".1"), Integer.valueOf(0), Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);
		
		CategoryCalculationUnit emptyUnit = new CategoryCalculationUnitImpl(new BigDecimal("0"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE,TEST_SCALE);
		
		categoryUnitMap.put(ESSAYS_ID, essayUnit);
		categoryUnitMap.put(HW_ID, hwUnit);
		categoryUnitMap.put(EC_ID, ecUnit);
		categoryUnitMap.put(EMPTY_ID, emptyUnit);
		
		
		return new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);
	}
	
	protected GradebookCalculationUnit getEssaysHomeworkExtraCreditEmptyCompleteGradebookPointsWeightedEssays() {
		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();
		
		CategoryCalculationUnit essayUnit = new CategoryCalculationUnitImpl(new BigDecimal(".6"), Integer.valueOf(0), Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, TEST_SCALE);
		CategoryCalculationUnit hwUnit = new CategoryCalculationUnitImpl(new BigDecimal(".4"), Integer.valueOf(0), null, Boolean.FALSE, Boolean.FALSE,TEST_SCALE);
		CategoryCalculationUnit ecUnit = new CategoryCalculationUnitImpl(new BigDecimal(".1"), Integer.valueOf(0), Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, TEST_SCALE);
		
		CategoryCalculationUnit emptyUnit = new CategoryCalculationUnitImpl(new BigDecimal("0"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE, Boolean.FALSE,TEST_SCALE);
		
		categoryUnitMap.put(ESSAYS_ID, essayUnit);
		categoryUnitMap.put(HW_ID, hwUnit);
		categoryUnitMap.put(EC_ID, ecUnit);
		categoryUnitMap.put(EMPTY_ID, emptyUnit);
		
		
		return new GradebookCalculationUnitImpl(categoryUnitMap, TEST_SCALE);
	}
	
	private List<GradeRecordCalculationUnit> getRecordUnits(Double[][] matrix) {
		List<GradeRecordCalculationUnit> units = new ArrayList<GradeRecordCalculationUnit>();

		for (int i=0;i<matrix.length;i++) {
			BigDecimal pointsEarned = matrix[i][0] == null ? null : BigDecimal.valueOf(matrix[i][0]);
			BigDecimal pointsPossible = matrix[i][1] == null ? null : BigDecimal.valueOf(matrix[i][1]);
			BigDecimal itemWeight = matrix[i][2] == null ? null : BigDecimal.valueOf(matrix[i][2]);
			Boolean extraCredit = matrix[i][3] == null ? Boolean.FALSE : Boolean.TRUE;

			units.add(new GradeRecordCalculationUnitImpl(pointsEarned, pointsPossible, itemWeight, extraCredit, TEST_SCALE));
		}

		return units;
	}


	private void assertEqualsAtScale2(BigDecimal first, BigDecimal second) {
		BigDecimal firstBig = first.setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal secondBig = second.setScale(2, RoundingMode.HALF_EVEN);
		try {
			assertTrue(firstBig.equals(secondBig));
		} catch (AssertionFailedError e) {
			System.out.println("" + firstBig + " != " + secondBig);
			throw e;
		}
	}

}
