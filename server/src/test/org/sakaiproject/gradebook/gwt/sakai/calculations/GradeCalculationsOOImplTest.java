package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.FastHashMap;
import org.codehaus.jackson.map.ObjectMapper;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.sakai.GradeCalculations;
import org.sakaiproject.gradebook.gwt.sakai.model.GradeItem;
import org.sakaiproject.gradebook.gwt.sakai.rest.resource.Resource;
import org.sakaiproject.gradebook.gwt.server.Util;
import org.sakaiproject.gradebook.gwt.server.model.GradeItemImpl;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.Category;
import org.sakaiproject.tool.gradebook.Gradebook;

import com.google.gwt.rpc.client.ast.DoubleValueCommand;

import junit.framework.TestCase;

public class GradeCalculationsOOImplTest extends TestCase {
	
	private GradeCalculations calculator = null;
	private static String GRADE_ITEM_JSON_ASN1 = "{\"B_ACTIVE\":false, \"B_EDITABLE\":true, \"L_CTGRY_ID\":1, \"S_NM\":\"ASN-1\", \"B_X_CRDT\":false, \"B_EQL_WGHT\":true, \"B_INCLD\":true, \"B_RLSD\":false, \"B_NLLS_ZEROS\":false, \"B_WT_BY_PTS\":false, \"D_PCT_GRD\":null, \"S_PCT_GRD\":\"null\", \"D_PCT_CTGRY\":null, \"S_PCT_CTGRY\":\"null\", \"D_PNTS\":null, \"S_PNTS\":\"null\", \"W_DUE\":null, \"I_DRP_LWST\":null, \"S_ITM_TYPE\":\"ITEM\"}";
	private static String GRADE_ITEM_JSON_ASN2 = "{\"B_ACTIVE\":false, \"B_EDITABLE\":true, \"L_CTGRY_ID\":1, \"S_NM\":\"ASN-2\", \"B_X_CRDT\":false, \"B_EQL_WGHT\":true, \"B_INCLD\":true, \"B_RLSD\":false, \"B_NLLS_ZEROS\":false, \"B_WT_BY_PTS\":false, \"D_PCT_GRD\":null, \"S_PCT_GRD\":\"null\", \"D_PCT_CTGRY\":null, \"S_PCT_CTGRY\":\"null\", \"D_PNTS\":null, \"S_PNTS\":\"null\", \"W_DUE\":null, \"I_DRP_LWST\":null, \"S_ITM_TYPE\":\"ITEM\"}";
	private static String GRADE_ITEM_JSON_ASN3 = "{\"B_ACTIVE\":false, \"B_EDITABLE\":true, \"L_CTGRY_ID\":1, \"S_NM\":\"ASN-3\", \"B_X_CRDT\":false, \"B_EQL_WGHT\":true, \"B_INCLD\":true, \"B_RLSD\":false, \"B_NLLS_ZEROS\":false, \"B_WT_BY_PTS\":false, \"D_PCT_GRD\":null, \"S_PCT_GRD\":\"null\", \"D_PCT_CTGRY\":null, \"S_PCT_CTGRY\":\"null\", \"D_PNTS\":null, \"S_PNTS\":\"null\", \"W_DUE\":null, \"I_DRP_LWST\":null, \"S_ITM_TYPE\":\"ITEM\"}";
	private static String GRADE_ITEM_JSON_ASN4 = "{\"B_ACTIVE\":false, \"B_EDITABLE\":true, \"L_CTGRY_ID\":1, \"S_NM\":\"ASN-4\", \"B_X_CRDT\":false, \"B_EQL_WGHT\":true, \"B_INCLD\":true, \"B_RLSD\":false, \"B_NLLS_ZEROS\":false, \"B_WT_BY_PTS\":false, \"D_PCT_GRD\":null, \"S_PCT_GRD\":\"null\", \"D_PCT_CTGRY\":null, \"S_PCT_CTGRY\":\"null\", \"D_PNTS\":null, \"S_PNTS\":\"null\", \"W_DUE\":null, \"I_DRP_LWST\":null, \"S_ITM_TYPE\":\"ITEM\"}";
	private static String GRADE_ITEM_JSON_CATEGORY = "{\"B_ACTIVE\":false, \"B_EDITABLE\":true, \"S_CTGRY_NAME\":\"\", \"S_NM\":\"Category 1\", \"B_X_CRDT\":false, \"B_EQL_WGHT\":false, \"B_INCLD\":true, \"B_RLSD\":false, \"B_NLLS_ZEROS\":false, \"B_WT_BY_PTS\":false, \"D_PCT_GRD\":null, \"S_PCT_GRD\":\"null\", \"D_PCT_CTGRY\":null, \"S_PCT_CTGRY\":\"null\", \"D_PNTS\":null, \"S_PNTS\":\"null\", \"W_DUE\":null, \"I_DRP_LWST\":null, \"S_ITM_TYPE\":\"CATEGORY\"}";

	public GradeCalculationsOOImplTest() {
		calculator = new GradeCalculationsOOImpl();
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@SuppressWarnings("unchecked")
	public void testCalculateEqualWeight() {
		Map<String, String[]> testData = new FastHashMap(8);
		String testName = "IWAPTest1";
		String[] caseData =	new String[] {"-1",	"1"};
		testData.put(testName, caseData);
		testName = "CEWTest2";
		caseData = 			new String[] {"0",	"1"};
		testData.put(testName, caseData);
		testName = "CEWTest3";
		caseData = 			new String[] {"1",	"1"};
		testData.put(testName, caseData);
		testName = "CEWTest4";
		caseData = 			new String[] {"2",	".5"};
		testData.put(testName, caseData);
		testName = "DeprecatedTestResult-1";
		caseData = 			new String[] {"3",	".3333333333"};
		testData.put(testName, caseData);
		testName = "CEWTest5";
		caseData = 			new String[] {"3",	
				(new BigDecimal("1")).divide(new BigDecimal("3"),GradeCalculations.MATH_CONTEXT).toString()};
		testData.put(testName, caseData);
		testName = "CEWTest6";
		caseData = 			new String[] {"4",	".25"};
		testData.put(testName, caseData);
		testName = "CEWTest7";
		caseData = 			new String[] {"5",	".2"};
		testData.put(testName, caseData);
		testName = "DeprecatedTestResult-2";
		caseData = 			new String[] {"6",	".1666666667"};
		testData.put(testName, caseData);
		testName = "CEWTest9";
		caseData = 			new String[] {"6",	
				(new BigDecimal("1")).divide(new BigDecimal("6"), GradeCalculations.MATH_CONTEXT).toString()};
		testData.put(testName, caseData);
		
		//TODO: test nulls

		for (String key : testData.keySet()) {
			
			assertEquals( key,
					new Double(testData.get(key)[1]),
					calculator
						.calculateEqualWeight( 
								testData.get(key)[0] != null ? new Integer(testData.get(key)[0]) : null)
								);
		}
		
	}

	@SuppressWarnings("unchecked")
	public void testCalculateItemWeightAsPercentage() {
		
		Map<String, String[]> testData = new FastHashMap(8);
		String testName = "IWAPTest1";
		String[] caseData =	new String[] {"10",	"10",	".10"};
		testData.put(testName, caseData);
		testName = "IWAPTest2";
		caseData = 			new String[] {"10",	"1", 	".10"};
		testData.put(testName, caseData);
		testName = "IWAPTest3";
		caseData = 			new String[] {"1",	"10",	".01"};
		testData.put(testName, caseData);
		testData.put(testName, caseData);
		testName = "IWAPTest4";
		caseData = 			new String[] {"1",	null,	".01"};
		testData.put(testName, caseData);
		testName = "IWAPTest5";
		caseData = 			new String[] {"10",	null,	".1"};
		testData.put(testName, caseData);
		testName = "IWAPTest6";
		caseData = 			new String[] {null,	"10",	".1"};
		testData.put(testName, caseData);
		testName = "IWAPTest7";
		caseData = 			new String[] {null,	"1",	".01"};
		testData.put(testName, caseData);
		testName = "IWAPTest8";
		caseData = 			new String[] {"11111111111",	null,	"111111111.1"};
		testData.put(testName, caseData);
		testName = "DeprecatedTestResult";
		caseData = 			new String[] {"111111111111",	null,	"1111111111.0"};
		// values should be 		
		// caseData = 			new String[] {"111111111111",	null,	"1111111111.1"};

		testData.put(testName, caseData);
		

		for (String key : testData.keySet()) {
			
			assertEquals( key,
					new Double(testData.get(key)[2]),
					calculator
						.calculateItemWeightAsPercentage(
								testData.get(key)[0] != null ? new Double(testData.get(key)[0]) : null, 
								testData.get(key)[1] != null ? new Double(testData.get(key)[1]) : null)
								);
		}
		
		
	}


	public void testCalculatePointsCategoryPercentSumCategoryListOfAssignmentBooleanBoolean() {
		
		Category category = new Category();
		category.setExtraCredit(false);
		
		Date dueDate = null;
		Gradebook gradebook = new Gradebook("GRADEBOOK_ID");
		gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY);
		category.setGradebook(gradebook);
		Double weight = calculator.calculateEqualWeight(4);
		
		Assignment asn1 = new Assignment(gradebook, "ASN-1", Double.valueOf(30), dueDate);
		asn1.setAssignmentWeighting(weight);
		asn1.setCategory(category);
		Assignment asn2 = new Assignment(gradebook, "ASN-2", Double.valueOf(40), dueDate);
		asn2.setAssignmentWeighting(weight);
		asn2.setCategory(category);
		Assignment asn3 = new Assignment(gradebook, "ASN-3", Double.valueOf(50), dueDate);
		asn3.setAssignmentWeighting(weight);
		asn3.setCategory(category);
		Assignment asn4 = new Assignment(gradebook, "ASN-4", Double.valueOf(10), dueDate);
		asn4.setAssignmentWeighting(weight);
		asn4.setCategory(category);
		
		List<Assignment> assignments = Arrays.asList(asn1, asn2, asn3, asn4);
		category.setAssignmentList(assignments);
		
		boolean isWeighted = false;
		boolean isCategoryExtraCredit = false;
		
		BigDecimal[] results = calculator.calculatePointsCategoryPercentSum(category, assignments, isWeighted, isCategoryExtraCredit);
		
		/// we will be removing context and the results below have a precision of 5 which
		/// will not be apparent after the refactor
		assertEquals("NumericalEquivalence-1", 0, (new BigDecimal("100.000")).compareTo(results[0]));
		assertEquals("NumericalEquivalence-2", 0, (new BigDecimal("130.0")).compareTo(results[1]));
		
		asn1.setPointsPossible(Double.valueOf(12.222222222222));
		results = calculator.calculatePointsCategoryPercentSum(category, assignments, isWeighted, isCategoryExtraCredit);
		
		assertEquals("NumericalEquivalence-3", (new BigDecimal("100.000")).compareTo(results[0]), 0);
		
		assertEquals("DeprecatedNumericalEquivalence-4", 0, (new BigDecimal("112.2222222")).compareTo(results[1]));
		assertEquals("DeprecatedNumericalInequivalence-1", -1, (BigDecimal.valueOf(asn1.getPointsPossible())).compareTo(results[1]));

		

	}

	@SuppressWarnings("unchecked")
	public void testCalculatePointsCategoryPercentSumGradeItemListOfGradeItemCategoryTypeBoolean() {
		
		GradeItem category = 
			new GradeItemImpl((Map<String,Object>)Resource.convertFromJson(GRADE_ITEM_JSON_CATEGORY, Map.class));
		category.setEqualWeightAssignments(true);
		category.setExtraCredit(false);
		
		Date dueDate = null;
		Gradebook gradebook = new Gradebook("GRADEBOOK_ID");
		gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY);
		category.setGradebook(gradebook.getUid());
		Double weight = calculator.calculateEqualWeight(4);
		
		GradeItem asn1 = new GradeItemImpl((Map<String, Object>)Resource.convertFromJson(GRADE_ITEM_JSON_ASN1, Map.class));
		asn1.setPoints(Double.valueOf(30));
		asn1.setWeighting(weight);
		asn1.setCategoryName(category.getCategoryName());
		GradeItem asn2 = new GradeItemImpl((Map<String, Object>)Resource.convertFromJson(GRADE_ITEM_JSON_ASN2, Map.class));
		asn2.setPoints(Double.valueOf(40));
		asn2.setWeighting(weight);
		GradeItem asn3 = new GradeItemImpl((Map<String, Object>)Resource.convertFromJson(GRADE_ITEM_JSON_ASN3, Map.class));
		asn3.setPoints(Double.valueOf(50));
		asn3.setWeighting(weight);
		GradeItem asn4 = new GradeItemImpl((Map<String, Object>)Resource.convertFromJson(GRADE_ITEM_JSON_ASN4, Map.class));
		asn4.setPoints(Double.valueOf(10));
		asn4.setWeighting(weight);
		
		List<GradeItem> assignments = Arrays.asList(asn1, asn2, asn3, asn4);
		
		
		boolean isWeighted = false;
		boolean isCategoryExtraCredit = false;
		BigDecimal[] results = calculator.calculatePointsCategoryPercentSum(category, assignments, CategoryType.WEIGHTED_CATEGORIES, isCategoryExtraCredit);

		
		/// we will be removing context and the results below have a precision of 5 which
		/// will not be apparent after the refactor
		assertEquals("NumericalEquivalence-1", 0, (new BigDecimal("1")).compareTo(results[0]));
		assertEquals("NumericalEquivalence-2", 0, (new BigDecimal("130.0")).compareTo(results[1]));
		
		asn1.setPoints(Double.valueOf(12.22222222222222222222222222222222222222222));
		results = calculator.calculatePointsCategoryPercentSum(category, assignments, CategoryType.SIMPLE_CATEGORIES, isCategoryExtraCredit);
			
		
				
		assertEquals("DeprecatedNumericalEquivalence-3", 0, (new BigDecimal("112.222222222222221")).compareTo(results[0]));
		assertEquals("DeprecatedNumericalEquivalence-4", 0, (new BigDecimal("112.222222222222221")).compareTo(results[1]));
		assertEquals("DeprecatedNumericalInequivalence-1", -1, (BigDecimal.valueOf(asn1.getPoints()).compareTo(results[0])));

	}


	public void testCalculateCourseGradeCategoryPercentsAssignmentBigDecimalBigDecimalBigDecimalBoolean() {
		Category category = new Category();
		category.setName("Category 1");
		category.setExtraCredit(false);
		
		Date dueDate = null;
		Gradebook gradebook = new Gradebook("GRADEBOOK_ID");
		gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY);
		category.setGradebook(gradebook);
		Double weight = calculator.calculateEqualWeight(5);
		
		Assignment asn1 = new Assignment(gradebook, "ASN-1", Double.valueOf(30), dueDate);
		asn1.setAssignmentWeighting(weight);
		asn1.setCategory(category);
		Assignment asn2 = new Assignment(gradebook, "ASN-2", Double.valueOf(40), dueDate);
		asn2.setAssignmentWeighting(weight);
		asn2.setCategory(category);
		Assignment asn3 = new Assignment(gradebook, "ASN-3", Double.valueOf(50), dueDate);
		asn3.setAssignmentWeighting(weight);
		asn3.setCategory(category);
		Assignment asn4 = new Assignment(gradebook, "ASN-4", Double.valueOf(10), dueDate);
		asn4.setAssignmentWeighting(weight);
		asn4.setCategory(category);
		Assignment asn5 = new Assignment(gradebook, "ASN-5", Double.valueOf(5), dueDate);
		asn5.setAssignmentWeighting(weight);
		asn5.setCategory(category);
		
		List<Assignment> assignments = Arrays.asList(asn1, asn2, asn3, asn4, asn5);
		category.setAssignmentList(assignments);
		
		boolean isWeighted = false;
		boolean isCategoryExtraCredit = false;
		
		BigDecimal[] results = calculator.calculatePointsCategoryPercentSum(category, assignments, isWeighted, isCategoryExtraCredit);
		BigDecimal catPercentSum = results[0];
		BigDecimal catPercentTotal = results[1];

		for (Assignment a : assignments) {
			results = calculator.calculateCourseGradeCategoryPercents(a, new BigDecimal("100.000"), catPercentSum, catPercentTotal, false);

			BigDecimal courseGradePercent = results[0];
			BigDecimal percentCategory = results[1];
			
			assertEquals("NumericalInequivalence-1:", 0, (new BigDecimal("20")).compareTo(courseGradePercent));
			assertEquals("NumericalInequivalence-2:", 0, (new BigDecimal("20")).compareTo(percentCategory));

		}

	}

	public void testCalculateCourseGradeCategoryPercentsGradeItemBigDecimalBigDecimalBigDecimalBoolean() {
		
		GradeItem category = 
			new GradeItemImpl((Map<String,Object>)Resource.convertFromJson(GRADE_ITEM_JSON_CATEGORY, Map.class));
		category.setEqualWeightAssignments(true);
		category.setExtraCredit(false);
		
		Date dueDate = null;
		Gradebook gradebook = new Gradebook("GRADEBOOK_ID");
		gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY);
		category.setGradebook(gradebook.getUid());
		Double weight = calculator.calculateEqualWeight(4);
		
		GradeItem asn1 = new GradeItemImpl((Map<String, Object>)Resource.convertFromJson(GRADE_ITEM_JSON_ASN1, Map.class));
		asn1.setPoints(Double.valueOf(30));
		asn1.setWeighting(weight);
		asn1.setCategoryName(category.getCategoryName());
		GradeItem asn2 = new GradeItemImpl((Map<String, Object>)Resource.convertFromJson(GRADE_ITEM_JSON_ASN2, Map.class));
		asn2.setPoints(Double.valueOf(40));
		asn2.setWeighting(weight);
		asn1.setCategoryName(category.getCategoryName());
		GradeItem asn3 = new GradeItemImpl((Map<String, Object>)Resource.convertFromJson(GRADE_ITEM_JSON_ASN3, Map.class));
		asn3.setPoints(Double.valueOf(50));
		asn3.setWeighting(weight);
		asn1.setCategoryName(category.getCategoryName());
		GradeItem asn4 = new GradeItemImpl((Map<String, Object>)Resource.convertFromJson(GRADE_ITEM_JSON_ASN4, Map.class));
		asn4.setPoints(Double.valueOf(10));
		asn4.setWeighting(weight);
		asn1.setCategoryName(category.getCategoryName());
		
		List<GradeItem> assignments = Arrays.asList(asn1, asn2, asn3, asn4);
		
		
		boolean isWeighted = false;
		boolean isCategoryExtraCredit = false;
		
		BigDecimal[] results = calculator.calculatePointsCategoryPercentSum(category, assignments, CategoryType.WEIGHTED_CATEGORIES, isCategoryExtraCredit);
		BigDecimal catPercentSum = results[0];
		BigDecimal catPointSum = results[1];
		
//		System.out.println("Results: " + 
//				category.getName() + "-->" + results[0] + ", " + results[1]);
		for (GradeItem assignment: assignments) {
			results = calculator.calculateCourseGradeCategoryPercents(assignment, BigDecimal.valueOf(100d)/* cat percent of grade */, 
					catPercentSum, catPointSum, Util.checkBoolean(category.getEnforcePointWeighting()));

			BigDecimal courseGradePercent = results[0];
			BigDecimal percentCategory = results[1];
			System.out.println("Results: " + 
					assignment.getName() + "-->" + courseGradePercent + ", " + percentCategory);
			
			assertEquals("NumericalEquivalence-1", 0, (new BigDecimal("25")).compareTo(courseGradePercent));
			assertEquals("NumericalEquivalence-2", 0, (new BigDecimal("0.25")).compareTo(percentCategory));

		}
		

			
		
	}

	public void testCalculateItemGradePercent() {
		
		final String LOTTA_NINES = "25.999999999";
		
		// these should not be equal
		assertEquals("DeprecatedNumericalEqualivalence-1", 0, (new BigDecimal("26"))
				.compareTo(calculator.calculateItemGradePercent(
						new BigDecimal("100"), new BigDecimal("100"), new BigDecimal(LOTTA_NINES), true)));
		
		// these should be equal
		assertEquals("DeprecatedNumericalEqualivalence-2", -1, (new BigDecimal(LOTTA_NINES))
				.compareTo(calculator.calculateItemGradePercent(
						new BigDecimal("100"), new BigDecimal("100"), new BigDecimal(LOTTA_NINES), true)));
		
		//TODO: add coverage of nonnormalized path and some more 'fuzz'
	}

	public void testCalculateItemGradePercentDecimal() {
		//System.out.println(calculator.calculateItemGradePercent(
		//		new BigDecimal("100"), new BigDecimal("100"), new BigDecimal(LOTTA_NINES), true));
		
	}

	public void testCalculateStatistics() {
		System.out.println("testCalculateStatistics yet implemented");
	}

	public void testConvertPercentageToLetterGrade() {
		System.out.println("testConvertPercentageToLetterGrade yet implemented");
	}

	public void testIsValidLetterGrade() {
		System.out.println("testIsValidLetterGrade yet implemented");
	}

	public void testConvertLetterGradeToPercentage() {
		System.out.println("testConvertLetterGradeToPercentage yet implemented");
	}

	public void testGetCategoryWeight() {
		System.out.println("testGetCategoryWeight yet implemented");
	}

	public void testGetCourseGrade() {
		System.out.println("testGetCourseGrade yet implemented");
	}

	public void testGetNewPointsGrade() {
		System.out.println("testGetNewPointsGrade yet implemented");
	}

	public void testGetPercentAsPointsEarned() {
		System.out.println("testGetPercentAsPointsEarned yet implemented");
	}

	public void testGetPointsEarnedAsPercent() {
		System.out.println("testGetPointsEarnedAsPercent yet implemented");
	}



}
