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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.sakai.calculations.CategoryCalculationUnit;
import org.sakaiproject.gradebook.gwt.sakai.calculations.GradeRecordCalculationUnit;
import org.sakaiproject.gradebook.gwt.sakai.calculations.GradebookCalculationUnit;

import junit.framework.TestCase;

public class GradeCalculationExtraCreditTest extends TestCase {


	private static final String ESSAYS_ID = "1";
	private static final String HW_ID = "2";
	private static final String EC_ID = "3";
	private static final String EMPTY_ID = "4";

	private GradebookCalculationUnit gradebookUnit;
	private boolean isExtraCreditScaled = false;


	public void testEightyWithExtraCreditItemCategoryWeighting() {

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
		BigDecimal courseGrade = gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, isExtraCreditScaled);

		assertEqualsAtScale2(BigDecimal.valueOf(90.00d), courseGrade);
	}	


	protected void setUp() throws Exception {
		super.setUp();

		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		CategoryCalculationUnit essayUnit = new CategoryCalculationUnit(new BigDecimal("1.0"), Integer.valueOf(0), Boolean.FALSE, Boolean.FALSE);

		categoryUnitMap.put(ESSAYS_ID, essayUnit);


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
		System.out.println("Comparing " + first + " to " + second.setScale(2, RoundingMode.HALF_EVEN));
		assertTrue(first.setScale(2, RoundingMode.HALF_EVEN).equals(second.setScale(2, RoundingMode.HALF_EVEN)));
	}

}


