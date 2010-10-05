package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.gradebook.gwt.sakai.calculations2.CategoryCalculationUnitImpl;
import org.sakaiproject.gradebook.gwt.sakai.calculations2.CategoryCalculationUnit;
import org.sakaiproject.gradebook.gwt.sakai.calculations2.GradeRecordCalculationUnit;
import org.sakaiproject.gradebook.gwt.sakai.calculations2.GradeRecordCalculationUnitImpl;

import junit.framework.TestCase;

public class CategoryCalculationUnitTest extends TestCase {
	
	private final int TEST_SCALE = 50;

	public CategoryCalculationUnitTest() {
		super();
	}
	
	// API TESTS
	
	public void testCalculate() {

		BigDecimal categoryWeightTotal = new BigDecimal("50");
		int dropLowest = 0;
		boolean isExtraCredit = false;
		boolean isPointsWeighted = false;
		
		CategoryCalculationUnit categoryCalculationUnit = new CategoryCalculationUnitImpl(categoryWeightTotal, dropLowest, isExtraCredit, isPointsWeighted, TEST_SCALE);
		
		BigDecimal pointsReceived = new BigDecimal("89");
		BigDecimal pointsPossible = new BigDecimal("100");
		BigDecimal percentOfCategory = new BigDecimal("10");
		Boolean extraCredit = Boolean.FALSE;
		GradeRecordCalculationUnit gradeRecordCalculationUnit = new GradeRecordCalculationUnitImpl(pointsReceived, pointsPossible, percentOfCategory, extraCredit, TEST_SCALE);
		
		List<GradeRecordCalculationUnit> gradeRecordCalculationUnits = new ArrayList<GradeRecordCalculationUnit>();
		gradeRecordCalculationUnits.add(gradeRecordCalculationUnit);
		BigDecimal result = categoryCalculationUnit.calculate(gradeRecordCalculationUnits, false);
		
	}
}
