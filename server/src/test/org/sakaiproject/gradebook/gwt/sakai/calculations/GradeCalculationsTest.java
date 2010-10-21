package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.collections.FastHashMap;
import org.apache.commons.math.stat.Frequency;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.codehaus.jackson.map.deser.StdDeserializationContext;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.sakai.GradeCalculations;
import org.sakaiproject.gradebook.gwt.sakai.calculations2.BigDecimalCalculationsWrapper;
import org.sakaiproject.gradebook.gwt.sakai.calculations2.GradeCalculationsImpl;
import org.sakaiproject.gradebook.gwt.sakai.model.GradeItem;
import org.sakaiproject.gradebook.gwt.sakai.model.GradeStatistics;
import org.sakaiproject.gradebook.gwt.sakai.model.StudentScore;
import org.sakaiproject.gradebook.gwt.sakai.rest.resource.Resource;
import org.sakaiproject.gradebook.gwt.server.Util;
import org.sakaiproject.gradebook.gwt.server.model.GradeItemImpl;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.AssignmentGradeRecord;
import org.sakaiproject.tool.gradebook.Category;
import org.sakaiproject.tool.gradebook.Gradebook;
import org.sakaiproject.tool.gradebook.LetterGradeMapping;
import org.sakaiproject.tool.gradebook.LetterGradePercentMapping;
import org.sakaiproject.gradebook.gwt.sakai.calculations2.GradeCalculationsImpl;

import sun.security.util.BigInt;

public class GradeCalculationsTest extends TestCase {
	
	public static final String DEFAULT_GRADE_DATA_FILE_PATH = "org/sakaiproject/gradebook/gwt/sakai/calculations/GradeData.dat";
	public static final String PROP_GRADE_DATA_FILE_PATH = "gb2.test.gradedata.path";
	private String dataFilePath = DEFAULT_GRADE_DATA_FILE_PATH;
	private static final Map<String, Double> gradeMap;
    static {
        Map<String, Double> aMap = new HashMap<String, Double>();
        aMap.put("A+", 98.3333333333d);
        aMap.put("A", 95d);
        aMap.put("A-", 91.6666666666d);
        aMap.put("B+", 88.3333333333d);
        aMap.put("B", 85d);
        aMap.put("B-", 81.6666666666d);
        aMap.put("C+", 78.3333333333d);
        aMap.put("C", 75d);
        aMap.put("C-", 71.6666666666d);
        aMap.put("D+", 68.3333333333d);
        aMap.put("D", 65d);
        aMap.put("D-", 61.6666666666d);
        aMap.put("F", 58.3333333333d);
        aMap.put("0", 0d);
        gradeMap = Collections.unmodifiableMap(aMap);
    }
	
	BigDecimal meanFromFile = null;
	BigDecimal stdevSampFromFile = null;
	BigDecimal stdevPopFromFile = null;
	BigDecimal medianFromFile = null;
	BigDecimal modeFromFile = null;

	
	private GradeCalculations calculator = null;
	private static String GRADE_ITEM_JSON_ASN1 = "{\"B_ACTIVE\":false, \"B_EDITABLE\":true, \"L_CTGRY_ID\":1, \"S_NM\":\"ASN-1\", \"B_X_CRDT\":false, \"B_EQL_WGHT\":true, \"B_INCLD\":true, \"B_RLSD\":false, \"B_NLLS_ZEROS\":false, \"B_WT_BY_PTS\":false, \"D_PCT_GRD\":null, \"S_PCT_GRD\":\"null\", \"D_PCT_CTGRY\":null, \"S_PCT_CTGRY\":\"null\", \"D_PNTS\":null, \"S_PNTS\":\"null\", \"W_DUE\":null, \"I_DRP_LWST\":null, \"S_ITM_TYPE\":\"ITEM\"}";
	private static String GRADE_ITEM_JSON_ASN2 = "{\"B_ACTIVE\":false, \"B_EDITABLE\":true, \"L_CTGRY_ID\":1, \"S_NM\":\"ASN-2\", \"B_X_CRDT\":false, \"B_EQL_WGHT\":true, \"B_INCLD\":true, \"B_RLSD\":false, \"B_NLLS_ZEROS\":false, \"B_WT_BY_PTS\":false, \"D_PCT_GRD\":null, \"S_PCT_GRD\":\"null\", \"D_PCT_CTGRY\":null, \"S_PCT_CTGRY\":\"null\", \"D_PNTS\":null, \"S_PNTS\":\"null\", \"W_DUE\":null, \"I_DRP_LWST\":null, \"S_ITM_TYPE\":\"ITEM\"}";
	private static String GRADE_ITEM_JSON_ASN3 = "{\"B_ACTIVE\":false, \"B_EDITABLE\":true, \"L_CTGRY_ID\":1, \"S_NM\":\"ASN-3\", \"B_X_CRDT\":false, \"B_EQL_WGHT\":true, \"B_INCLD\":true, \"B_RLSD\":false, \"B_NLLS_ZEROS\":false, \"B_WT_BY_PTS\":false, \"D_PCT_GRD\":null, \"S_PCT_GRD\":\"null\", \"D_PCT_CTGRY\":null, \"S_PCT_CTGRY\":\"null\", \"D_PNTS\":null, \"S_PNTS\":\"null\", \"W_DUE\":null, \"I_DRP_LWST\":null, \"S_ITM_TYPE\":\"ITEM\"}";
	private static String GRADE_ITEM_JSON_ASN4 = "{\"B_ACTIVE\":false, \"B_EDITABLE\":true, \"L_CTGRY_ID\":1, \"S_NM\":\"ASN-4\", \"B_X_CRDT\":false, \"B_EQL_WGHT\":true, \"B_INCLD\":true, \"B_RLSD\":false, \"B_NLLS_ZEROS\":false, \"B_WT_BY_PTS\":false, \"D_PCT_GRD\":null, \"S_PCT_GRD\":\"null\", \"D_PCT_CTGRY\":null, \"S_PCT_CTGRY\":\"null\", \"D_PNTS\":null, \"S_PNTS\":\"null\", \"W_DUE\":null, \"I_DRP_LWST\":null, \"S_ITM_TYPE\":\"ITEM\"}";
	private static String GRADE_ITEM_JSON_CATEGORY = "{\"B_ACTIVE\":false, \"B_EDITABLE\":true, \"S_CTGRY_NAME\":\"\", \"S_NM\":\"Category 1\", \"B_X_CRDT\":false, \"B_EQL_WGHT\":false, \"B_INCLD\":true, \"B_RLSD\":false, \"B_NLLS_ZEROS\":false, \"B_WT_BY_PTS\":false, \"D_PCT_GRD\":null, \"S_PCT_GRD\":\"null\", \"D_PCT_CTGRY\":null, \"S_PCT_CTGRY\":\"null\", \"D_PNTS\":null, \"S_PNTS\":\"null\", \"W_DUE\":null, \"I_DRP_LWST\":null, \"S_ITM_TYPE\":\"CATEGORY\"}";

	// set this flag to true to expect high precision numbers as results
	private boolean FULL_PRECISION = false;
	
	BigDecimalCalculationsWrapper helper = new BigDecimalCalculationsWrapper(50);

	public GradeCalculationsTest() {
		
		
		if(FULL_PRECISION) {
			GradeCalculationsImpl old = new GradeCalculationsImpl(50);
			old.init();
			calculator = old;
		} else {
			GradeCalculationsOOImpl newImpl = new GradeCalculationsOOImpl();
			newImpl.init();
			calculator = newImpl;
		}

		try {
			dataFilePath = System.getProperty(PROP_GRADE_DATA_FILE_PATH);
		} catch(Exception e) {// nada
			}
		

		if (null == dataFilePath) {
			dataFilePath = DEFAULT_GRADE_DATA_FILE_PATH;
		}





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
		caseData = 			new String[] {"3",	FULL_PRECISION ? ".3333333333333333333333" : ".3333333333"};
		testData.put(testName, caseData);
		testName = "CEWTest5";
		caseData = 			new String[] {"3",	
				(new BigDecimal("1")).divide(new BigDecimal("3"),FULL_PRECISION ? new MathContext(100) : GradeCalculations.MATH_CONTEXT).toString()};
		testData.put(testName, caseData);
		testName = "CEWTest6";
		caseData = 			new String[] {"4",	".25"};
		testData.put(testName, caseData);
		testName = "CEWTest7";
		caseData = 			new String[] {"5",	".2"};
		testData.put(testName, caseData);
		testName = "DeprecatedTestResult-2";
		caseData = 			new String[] {"6",	FULL_PRECISION ? ".166666666666666666" : ".1666666667"};
		testData.put(testName, caseData);
		testName = "CEWTest9";
		caseData = 			new String[] {"6",	
				(new BigDecimal("1")).divide(new BigDecimal("6"), FULL_PRECISION ? new MathContext(100) : GradeCalculations.MATH_CONTEXT).toString()};
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
		testData.put(testName, caseData);
		testName = "DeprecatedTestResult";
		caseData = 			new String[] {"111111111111",	null,	FULL_PRECISION ? "1111111111.11": "1111111111.0"};
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
		
		assertEquals("DeprecatedNumericalEquivalence-4", FULL_PRECISION? -1 : 0, (new BigDecimal("112.2222222")).compareTo(results[1]));
		assertEquals("DeprecatedNumericalInequivalence-1", FULL_PRECISION? -1 : 0, (new BigDecimal("112.2222222")).compareTo(results[1]));
		
		

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
		
		isWeighted = true;
		results = calculator.calculatePointsCategoryPercentSum(category, assignments, isWeighted, isCategoryExtraCredit);
		catPercentSum = results[0];
		catPercentTotal = results[1];
			
		
		results = calculator.calculateCourseGradeCategoryPercents(asn1, new BigDecimal("100.000"), catPercentSum, catPercentTotal, true);

		BigDecimal courseGradePercent = results[0];
		BigDecimal percentCategory = results[1];
		System.out.println("courseGradePercent: " + courseGradePercent);
		System.out.println("percentCategory: " + percentCategory);
		
		assertEquals("NumericalInequivalence-1:", 0, 
				FULL_PRECISION ?
						(new BigDecimal("22.22222222222222222222222222222222222222222222222222")).compareTo(courseGradePercent)
						: (new BigDecimal("22.22222222")).compareTo(courseGradePercent));
		assertEquals("NumericalInequivalence-2:", 0, 
				FULL_PRECISION ?
						(new BigDecimal("22.22222222222222222222222222222222222222222222222200")).compareTo(percentCategory)
						: (new BigDecimal("22.22222222000")).compareTo(percentCategory));
		
		
		results = calculator.calculateCourseGradeCategoryPercents(asn3, new BigDecimal("100.000"), catPercentSum, catPercentTotal, true);

		courseGradePercent = results[0];
		percentCategory = results[1];
		System.out.println("courseGradePercent: " + courseGradePercent);
		System.out.println("percentCategory: " + percentCategory);
		
		assertEquals("NumericalInequivalence-1:", 0, 
				FULL_PRECISION ?
						(new BigDecimal("37.03703703703703703703703703703703703703703703703704")).compareTo(courseGradePercent)
						: (new BigDecimal("37.03703704")).compareTo(courseGradePercent));
		assertEquals("NumericalInequivalence-2:", 0, 
				FULL_PRECISION ?
						(new BigDecimal("37.03703703703703703703703703703703703703703703703700")).compareTo(percentCategory)
						: (new BigDecimal("37.03703704000")).compareTo(percentCategory));

		

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
		
		for (int i =0 ; i<2 ; ++i) { // first time thru with all four assignments

			for (GradeItem assignment: assignments) {
				results = calculator.calculateCourseGradeCategoryPercents(assignment, BigDecimal.valueOf(100d)/* cat percent of grade */, 
						catPercentSum, catPointSum, Util.checkBoolean(category.getEnforcePointWeighting()));

				BigDecimal courseGradePercent = results[0];
				BigDecimal percentCategory = results[1];
//				System.out.println("Results: " + 
//						assignment.getName() + "-->" + courseGradePercent + ", " + percentCategory);

				

				if(0 == i)  {
					assertEquals("NumericalEquivalence-1", 0, (new BigDecimal("25")).compareTo(courseGradePercent));
					assertEquals("NumericalEquivalence-2", 0, (new BigDecimal("0.25")).compareTo(percentCategory));
				} else {
					/// these are numbers with too low of a precision
					assertEquals("DeprecatedNumericalEquivalence-3", FULL_PRECISION ? -1 : 0, (new BigDecimal("33.33333333")).compareTo(courseGradePercent));
					assertEquals("DeprecatedNumericalEquivalence-4", FULL_PRECISION ? -1 : 0, (new BigDecimal("0.3333333333")).compareTo(percentCategory));
					
					/// these are numbers that stuffs 'the most' into a double
					assertEquals("DeprecatedNumericalEquivalence-3", FULL_PRECISION ? 0 : 1, (new BigDecimal("33.333333333333330")).compareTo(courseGradePercent));
					assertEquals("DeprecatedNumericalEquivalence-4", FULL_PRECISION ? 0 : 1, (new BigDecimal("0.3333333333333333")).compareTo(percentCategory));
					
					
				}

			}
			
			assignments = Arrays.asList(asn2, asn3, asn4); // next time thru with only three
			
			weight = calculator.calculateEqualWeight(3);
			asn2.setWeighting(weight);
			asn3.setWeighting(weight);
			asn4.setWeighting(weight);
		}

		
	}

	public void testCalculateItemGradePercent() {
		
		final String LOTTA_NINES = "25.999999999";
		
		// these should not be equal
		assertEquals("DeprecatedNumericalEqualivalence-1", FULL_PRECISION ? 1 : 0, (new BigDecimal("26"))
				.compareTo(calculator.calculateItemGradePercent(
						new BigDecimal("100"), new BigDecimal("100"), new BigDecimal(LOTTA_NINES), true)));
		
		// these should be equal
		assertEquals("DeprecatedNumericalEqualivalence-2", FULL_PRECISION ? 0 : -1, (new BigDecimal(LOTTA_NINES))
				.compareTo(calculator.calculateItemGradePercent(
						new BigDecimal("100"), new BigDecimal("100"), new BigDecimal(LOTTA_NINES), true)));
		
		// non-normalized: these should not be equal
		assertEquals("DeprecatedNumericalEqualivalence-1", FULL_PRECISION ? 1 : 0, (new BigDecimal("26"))
				.compareTo(calculator.calculateItemGradePercent(
						new BigDecimal("1"), new BigDecimal("1"), new BigDecimal(LOTTA_NINES).divide(new BigDecimal("100", new MathContext(100))), false)));
		
		// non-normalized: these should not be equal
		assertEquals("DeprecatedNumericalEqualivalence-1", FULL_PRECISION ? 0 : -1, (new BigDecimal(LOTTA_NINES))
				.compareTo(calculator.calculateItemGradePercent(
						new BigDecimal("1"), new BigDecimal("1"), new BigDecimal(LOTTA_NINES).divide(new BigDecimal("100", new MathContext(100))), false)));
		
//		System.out.println(
//				"CalculateItemGradePercent-->" + calculator.calculateItemGradePercent(
//						new BigDecimal("1"), new BigDecimal("1"), new BigDecimal(LOTTA_NINES).divide(new BigDecimal("100", new MathContext(100))), false));
//				
		//TODO: 'fuzzing'?
	}


	public void testCalculateStatistics() {
		GradeDataLoader data = new GradeDataLoader(dataFilePath);
		
		assertTrue("Missing data from test input file", data.isAllTestStatsKeysPresent());
		assertNotNull("Null Scores", data.getScores());
		
		// calculate some stats using commons-math for giggles
		DescriptiveStatistics stats = new DescriptiveStatistics();
		Frequency frequency = new Frequency();
		
		for (StudentScore score : data.getScores()) {
			stats.addValue(score.getScore().doubleValue());
			 
			  //  create frequency distro buckets based on strings
			  //  using scale of 2 and half-up rounding
			  	 
			frequency.addValue(score);
		}
		//shouldn't commons math know how to find the mode?!
		Set<StudentScore> modeValues = new HashSet<StudentScore>(); // should enforce uniqueness
		long last = 0;
		for (java.util.Iterator<Comparable<?>> i = frequency.valuesIterator();i.hasNext();) {
			StudentScore value = (StudentScore) i.next();
			long count = frequency.getCount(value);
			if (count>last) {
				modeValues.add(value);
				last = count;
			}
			
		}
		
		double mean = stats.getMean();
		double std = stats.getStandardDeviation();
		double median = stats.getPercentile(50d);
	
		
		/* since we'll be comparing results against expected
		 * values in the file, if the file expects deprecated results, get them
		 */
		GradeCalculations temp = calculator;
		if(data.isUseDeprecatedCalculations() && FULL_PRECISION) {
			calculator = new GradeCalculationsOOImpl();
		} else if (!data.isUseDeprecatedCalculations()) {
			calculator = new GradeCalculationsImpl(data.getScale());
		}
		
		/**
		 * NOTE: The result of these calculations is sensitive to 
		 * the scale and precision of source data and the intermediary storage while calculating
		 * the SUM of the data values (which is passed to the 'calculateStatistics' method
		 * 
		 * Here we will use full presision and calculate our own sum 
		 * 
		 */

		GradeStatistics calculatedStats = calculator.calculateStatistics(data.getScores(), getScoresSum(data), "1");


		//Assertions
		
		// Currently the mean returned is a rounded, scale 2 value since it corresponds to one bucket of a histogram 
		
		
				/*  here, for a simple comparison,
				 * we only need to test that commons-math 
				 * and our impl class both come up with the same rounded and scaled value
				 */
		assertEquals("StatsTest-mean", 
				// expected value is same as calculated with commons-math
				// high precision, half-up rounded to scale 2
				BigDecimal.valueOf(mean).setScale(2, RoundingMode.HALF_UP), 
				calculatedStats.getMean().setScale(2, RoundingMode.HALF_UP));
		
		

		
		/* this only tests our local 'best' algorithm for stdev (population)
		 * It doesn't use file data, but check to see of the impl is the high precision impl first
		 */
		if(!data.isUseDeprecatedCalculations() && FULL_PRECISION)
			assertEquals("High-precision-StatsTest-stddev-test-standard-population",
				// expected value is same as calculated with algorithm used
				// in high-precision calculations impl
				// not that this also tests equality wrt scale and precision
				-1, 
				helper.subtract(getOnlinePopulationStandardDeviation(data), 
				calculatedStats.getStandardDeviation()).abs()
				.compareTo(data.getAcceptableError()));
		
		

		
		/* STANDARD DEVIATION of the population */
		String fromFile = (String)data.getTestStatsByKey().get(GradeDataLoader.INPUT_KEY_STDEVP);	
		
		/// this tests value, precision and scale
		assertEquals("StatsTest-stdevp-from-test-file - error:" +
				helper.subtract(new BigDecimal(fromFile), calculatedStats.getStandardDeviation().setScale(data.getScale())).abs(), -1, /// this is (left side < right side) 
				helper.subtract(new BigDecimal(fromFile), calculatedStats.getStandardDeviation().setScale(data.getScale())).abs()
				.compareTo(data.getAcceptableError()));
			
		
		/* MEAN */
		/* the older calculation units returned a mean of scale 2 so adjust if necessary */
		if(data.isUseDeprecatedCalculations()) {
			fromFile = new BigDecimal((String)data.getTestStatsByKey().get(GradeDataLoader.INPUT_KEY_MEAN))
						.setScale(2, RoundingMode.HALF_UP)
						.toString();
		} else {
			fromFile = (String)data.getTestStatsByKey().get(GradeDataLoader.INPUT_KEY_MEAN);
		}
		
		
		
		/// this tests value, precision and scale ... the value currently returned is of scale 2
		assertEquals("StatsTest-mean-from-test-file", -1,
				helper.subtract(new BigDecimal(fromFile), calculatedStats.getMean()).abs()
				.compareTo(data.getAcceptableError()));
		
		
		/* MEDIAN */
		fromFile = (String)data.getTestStatsByKey().get(GradeDataLoader.INPUT_KEY_MEDIAN);
		
		// this is number of scale 2
		assertEquals("StatsTest-median-from-test-file", new BigDecimal(fromFile), calculatedStats.getMedian());
		
		
		/* MODE */
		Object o = data.getTestStatsByKey().get(GradeDataLoader.INPUT_KEY_MODE);
		
		if (o instanceof String) {
			String s = (String)o;
			assertEquals("StatsTest-mode-COUNT-from-test-file", 1, calculatedStats.getModeList().size());
		} else {
			ArrayList<BigDecimal> l = (ArrayList<BigDecimal>) o;
			assertEquals("StatsTest-mode-COUNT-from-test-file", l.size(), calculatedStats.getModeList().size());
			for(int i=0;i<calculatedStats.getModeList().size();++i) {
				assertTrue("StatsTest-mode-from-test-file-" + i + " : " + l.get(i), calculatedStats.getModeList().contains(l.get(i)));
			}
			
		}
		
		
		
		/* reset the overridden calulator implementation */
		calculator = temp;
		
	}
	
	private BigDecimal getOnlineSampleStandardDeviation(GradeDataLoader data) {
		// == sqrt ( ((pop stdev)^2 * N)/(N-1) )
		helper = new BigDecimalCalculationsWrapper(data.getScale());
		
		BigDecimal stdevpop = getOnlinePopulationStandardDeviation(data);
		return doSqrt(helper.
				divide(helper.
						multiply(BigDecimal.valueOf(data.getScores().size()),helper.
								multiply(stdevpop, stdevpop)), BigDecimal.valueOf(data.getScores().size() - 1)));
	}
  
	private BigDecimal doSqrt(BigDecimal operand) {
		if (FULL_PRECISION) {
			return helper.sqrt(operand);
		} else {
			BigSquareRoot bigSR =  new BigSquareRoot();
			return bigSR.get(operand);
		}
		
	}

	private BigDecimal getOnlinePopulationStandardDeviation(GradeDataLoader data) {
		
		helper = new BigDecimalCalculationsWrapper(data.getScale());
		/*
		 * Donald Knuth's "The Art of Computer Programming, Volume 2: Seminumerical Algorithms", section 4.2.2.
		 * Knuth attributes this method to B.P. Welford, Technometrics, 4,(1962), 419-420.
		 * 
		 * M(1) = x(1), M(k) = M(k-1) (x(k) - M(k-1) / k
		 * S(1) = 0, S(k) = S(k-1) (x(k) - M(k-1)) * (x(k) - M(k))
		 *
		 * for 2 <= k <= n, then
		 *
		 * sigma = sqrt(S(n) / (n - 1))
		 * 
		 * (also: http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance)
		 */
		
		if (data.getScores().size() == 0) { return null; } 
		
		
		
		int n = 0;
		// estimate the mean
		BigDecimal mean = helper.divide(getScoresSum(data), BigDecimal.valueOf(data.getScores().size()));;
		BigDecimal M2 = BigDecimal.ZERO;
		BigDecimal delta = null;
		BigDecimal variance = null;
		

		
		for (StudentScore score : data.getScores()) {
			n++;
			delta = helper.subtract(score.getScore(), mean);
			
			mean = helper.add(mean, helper.divide(delta, BigDecimal.valueOf(n)));
			
			M2 = helper.add(M2,helper.multiply(delta, helper.subtract(score.getScore(), mean)));
			//System.out.println("variance(" + n + "): " + helper.divide(M2, BigDecimal.valueOf(n)));
			//System.out.println("variance : " + helper.divide(M2, BigDecimal.valueOf(n-1)));
		}
		

		// note: using n, not n-1... ie, the population variance, not the sample variance
		variance = helper.divide(M2, BigDecimal.valueOf(n)); 
		return helper.sqrt(variance).setScale(data.getScale(), RoundingMode.HALF_UP);
	}

	private BigDecimal getScoresSum(GradeDataLoader data) {
		BigDecimal rt = BigDecimal.ZERO;
		for (StudentScore score : data.getScores()) {
			rt = rt.add(score.getScore());
		}
		return rt;
	}
	
	public void testConvertPercentageToLetterGrade() {
		
		BigDecimal oneHundred = new BigDecimal("100");
		BigDecimal ninety = new BigDecimal("90");
		BigDecimal eighty = new BigDecimal("80");
		BigDecimal seventy = new BigDecimal("70");
		BigDecimal sixty = new BigDecimal("60");
		
		assertEquals("Percent2Grade - (A+)", "A+", calculator.convertPercentageToLetterGrade(oneHundred));
		
		assertEquals("Percent2Grade - (A-)", "A-", calculator.convertPercentageToLetterGrade(ninety));
		
		assertEquals("Percent2Grade - (B-)", "B-", calculator.convertPercentageToLetterGrade(eighty));
		
		assertEquals("Percent2Grade - (C-)", "C-", calculator.convertPercentageToLetterGrade(seventy));
		
		assertEquals("Percent2Grade - (D-)", "D-", calculator.convertPercentageToLetterGrade(sixty));
		
		assertEquals("Percent2Grade - (F)", "F", calculator.convertPercentageToLetterGrade(BigDecimal.ONE));
		
		assertEquals("Percent2Grade - (Ungraded?)", "0", calculator.convertPercentageToLetterGrade(BigDecimal.ZERO));

		/**** A min
		 * 90 + ((100 - 90)/3)
		 */
		BigDecimal firstThird = ninety
				.add(BigDecimal.TEN.movePointRight(1).subtract(ninety)
				.divide(new BigDecimal("3"), helper.getScale(), RoundingMode.HALF_UP));
		assertEquals("Percent2Grade - (A min)", "A", calculator.convertPercentageToLetterGrade(firstThird));
		
		/*
		 * service uses 6 as a precision ... with tens, there would be 4 decimal places
		 * the smallest unit being 1x10^-4 or, in better bigdecimal notation, 1x1^-3 
		 *
		 */
		BigDecimal aLittle = BigDecimal.ONE.movePointLeft(4);
		
		/* now, just one unit down should change the grade 
		 * but we also need to make sure rounding done in the service
		 * doesn't take that away 
		 */
		BigDecimal maxAMinus = firstThird.subtract(aLittle);
		maxAMinus = preemptRoundingIfNecessary(maxAMinus); // it won't be, but just for consistency
		
		assertEquals("Percent2Grade - (A- max)", "A-", calculator.convertPercentageToLetterGrade(maxAMinus));
		
		/**** A+ min
		 * 90 + (2*(100 - 90)/3)
		 */
		
		BigDecimal secondThird = ninety
		.add(BigDecimal.TEN.movePointRight(1).subtract(ninety)
		.divide(new BigDecimal("3"), helper.getScale(), RoundingMode.HALF_UP).multiply(new BigDecimal("2")));
		
		
		assertEquals("Percent2Grade - (A+ min)", "A+", calculator.convertPercentageToLetterGrade(secondThird));
		
		/* now, just one unit down should change the grade 
		 * but we also need to make sure rounding done in the service
		 * doesn't take that away 
		
		 * A max */
		BigDecimal maxA = secondThird.subtract(aLittle);
		maxA = preemptRoundingIfNecessary(maxA);
		assertEquals("Percent2Grade - (A max)", "A", calculator.convertPercentageToLetterGrade(maxA));
		
		/* take one unit off ninety and we should get 
		 * 
		 * B+ max
		 */
		BigDecimal maxBPlus = ninety.subtract(aLittle);
		maxBPlus = preemptRoundingIfNecessary(maxBPlus);
		assertEquals("Percent2Grade - (B+ max)", "B+", calculator.convertPercentageToLetterGrade(maxBPlus));
		
		/**** B min
		 * 80 + ((90 - 80)/3)
		 */
		firstThird = eighty
				.add(ninety.subtract(eighty)
				.divide(new BigDecimal("3"), helper.getScale(), RoundingMode.HALF_UP));
		assertEquals("Percent2Grade - (B min)", "B", calculator.convertPercentageToLetterGrade(firstThird));
		
		
		/* now, just one unit down should change the grade 
		 * but we also need to make sure rounding done in the service
		 * doesn't take that away 
		 *
		  B- max */
		BigDecimal maxBMinus = firstThird.subtract(aLittle);
		maxAMinus = preemptRoundingIfNecessary(maxBMinus); // it won't be, but just for consistency
		
		assertEquals("Percent2Grade - (B- max)", "B-", calculator.convertPercentageToLetterGrade(maxBMinus));
		
		/**** B+ min
		 * 80 + (2*(90 - 80)/3)
		 */
		
		secondThird = eighty
		.add(ninety.subtract(eighty)
		.divide(new BigDecimal("3"), helper.getScale(), RoundingMode.HALF_UP).multiply(new BigDecimal("2")));
		
		
		assertEquals("Percent2Grade - (B+ min)", "B+", calculator.convertPercentageToLetterGrade(secondThird));
		
		/* now, just one unit down should change the grade 
		 * but we also need to make sure rounding done in the service
		 * doesn't take that away 
		 *
		 B max */
		BigDecimal maxB = secondThird.subtract(aLittle);
		maxB = preemptRoundingIfNecessary(maxB);
		assertEquals("Percent2Grade - (B max)", "B", calculator.convertPercentageToLetterGrade(maxB));
		
		
		/*--------------------------------*/
		
		/* take one unit off eighty and we should get 
		 * 
		 * C+ max
		 */
		BigDecimal maxCPlus = eighty.subtract(aLittle);
		maxCPlus = preemptRoundingIfNecessary(maxCPlus);
		assertEquals("Percent2Grade - (C+ max)", "C+", calculator.convertPercentageToLetterGrade(maxCPlus));
		
		/**** C min
		 * 70 + ((80 - 70)/3)
		 */
		firstThird = seventy
				.add(eighty.subtract(seventy)
				.divide(new BigDecimal("3"), helper.getScale(), RoundingMode.HALF_UP));
		assertEquals("Percent2Grade - (C min)", "C", calculator.convertPercentageToLetterGrade(firstThird));
		
		
		/* now, just one unit down should change the grade 
		 * but we also need to make sure rounding done in the service
		 * doesn't take that away 
		 *
		  C- max */
		BigDecimal maxCMinus = firstThird.subtract(aLittle);
		maxCMinus = preemptRoundingIfNecessary(maxCMinus); // it won't be, but just for consistency
		
		assertEquals("Percent2Grade - (C- max)", "C-", calculator.convertPercentageToLetterGrade(maxCMinus));
		
		/**** C+ min
		 * 70 + (2*(80 - 70)/3)
		 */
		
		secondThird = seventy
		.add(eighty.subtract(seventy)
		.divide(new BigDecimal("3"), helper.getScale(), RoundingMode.HALF_UP).multiply(new BigDecimal("2")));
		
		
		assertEquals("Percent2Grade - (C+ min)", "C+", calculator.convertPercentageToLetterGrade(secondThird));
		
		/* now, just one unit down should change the grade 
		 * but we also need to make sure rounding done in the service
		 * doesn't take that away 
		 *
		 C max */
		BigDecimal maxC = secondThird.subtract(aLittle);
		maxC = preemptRoundingIfNecessary(maxC);
		assertEquals("Percent2Grade - (C max)", "C", calculator.convertPercentageToLetterGrade(maxC));

		
		/*--------------------------------*/
		
		/* take one unit off seventy and we should get 
		 * 
		 * D+ max
		 */
		BigDecimal maxDPlus = seventy.subtract(aLittle);
		maxDPlus = preemptRoundingIfNecessary(maxDPlus);
		assertEquals("Percent2Grade - (D+ max)", "D+", calculator.convertPercentageToLetterGrade(maxDPlus));
		
		/**** D min
		 * 60 + ((70 - 60)/3)
		 */
		firstThird = sixty
				.add(seventy.subtract(sixty)
				.divide(new BigDecimal("3"), helper.getScale(), RoundingMode.HALF_UP));
		assertEquals("Percent2Grade - (D min)", "D", calculator.convertPercentageToLetterGrade(firstThird));
		
		
		/* now, just one unit down should change the grade 
		 * but we also need to make sure rounding done in the service
		 * doesn't take that away 
		 *
		  D- max */
		BigDecimal maxDMinus = firstThird.subtract(aLittle);
		maxDMinus = preemptRoundingIfNecessary(maxDMinus); // it won't be, but just for consistency
		
		assertEquals("Percent2Grade - (D- max)", "D-", calculator.convertPercentageToLetterGrade(maxDMinus));
		
		/**** D+ min
		 * 60 + (2*(70 - 60)/3)
		 */
		
		secondThird = sixty
		.add(seventy.subtract(sixty)
		.divide(new BigDecimal("3"), helper.getScale(), RoundingMode.HALF_UP).multiply(new BigDecimal("2")));
		
		
		assertEquals("Percent2Grade - (D+ min)", "D+", calculator.convertPercentageToLetterGrade(secondThird));
		
		/* now, just one unit down should change the grade 
		 * but we also need to make sure rounding done in the service
		 * doesn't take that away 
		 *
		 D max */
		BigDecimal maxD = secondThird.subtract(aLittle);
		maxD = preemptRoundingIfNecessary(maxD);
		assertEquals("Percent2Grade - (D max)", "D", calculator.convertPercentageToLetterGrade(maxD));

		/*--------------------------------*/
		
		/* take one unit off sixty and we should get 
		 * 
		 * F max
		 */
		BigDecimal maxF = sixty.subtract(aLittle);
		maxF = preemptRoundingIfNecessary(maxF);
		assertEquals("Percent2Grade - (F max)", "F", calculator.convertPercentageToLetterGrade(maxF));
		
		/**** F min
		 * NOTE: zero values are treated specially in this method, returning the String "0"
		 *    So, the value 1 is the lowest F grade
		 *    
		 *    TODO: find out why and if it has undesired outcomes
		 */
		
		assertEquals("Percent2Grade - (Zero Grade - Special Case)", "0", calculator.convertPercentageToLetterGrade(BigDecimal.ZERO));
		
		/* 
		 * So, the value 1 is the lowest F grade
		 */
		assertEquals("Percent2Grade - (F min)", "F", calculator.convertPercentageToLetterGrade(BigDecimal.ONE));
		
	}

	private BigDecimal preemptRoundingIfNecessary(BigDecimal grade) {
		if(grade.subtract(grade.setScale(4, RoundingMode.HALF_UP)).compareTo(BigDecimal.ZERO) < 0 )  {
			// rounding will bump the last digit up one
			// so round it to the floor value first
			grade = grade.setScale(4, RoundingMode.FLOOR);
		}
		return grade;
	}

	
	/*
	 *  won't be testing .... these next two
	 *  depend on a Spring injected map in the OOTB
	 *  impl's .... 
	 */
//	public void testIsValidLetterGrade() {
//		System.out.println("testIsValidLetterGrade yet implemented");
//	}

// won't be testing
//	public void testConvertLetterGradeToPercentage() {
//		System.out.println("testConvertLetterGradeToPercentage yet implemented");
//	}

	public void testGetCategoryWeight() {
		
		
		Category category = new Category();
		category.setExtraCredit(false);
		
		Date dueDate = null;
		Gradebook gradebook = new Gradebook("GRADEBOOK_ID");
		gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY);
		category.setGradebook(gradebook);
		Double weight = calculator.calculateEqualWeight(4);
		
		// total of 130 
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
		
		BigDecimal result = calculator.getCategoryWeight(category);
		
		assertEquals("TestGetCategoryWeight - no cat weight", null, result);
		
		gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_NO_CATEGORY);
		
		result = calculator.getCategoryWeight(category);
		
		// no cat - 130 points 
		assertEquals("TestGetCategoryWeight -  no cat", 0, new BigDecimal("130").compareTo(result));
		
		// 100%
		gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY);
		category.setWeight(100d);
		result = calculator.getCategoryWeight(category);
		assertEquals("TestGetCategoryWeight - 100%", BigDecimal.valueOf(category.getWeight()), result);
		
		// unweighted
		category.setUnweighted(true);
		result = calculator.getCategoryWeight(category);
		assertEquals("TestGetCategoryWeight - unweighted", null, result);
		
		

	}
	
	

	public void testGetCourseGrade() {
		Category category = new Category();
		category.setExtraCredit(false);
		category.setId(new Long(1));
		
		Date dueDate = null;
		Gradebook gradebook = new Gradebook("GRADEBOOK_ID");
		
		category.setGradebook(gradebook);
		Double weight = calculator.calculateEqualWeight(6);
		
		Long id = new Long(0);
		Assignment asn1 = new Assignment(gradebook, "ASN-1", Double.valueOf(30), dueDate);
		asn1.setAssignmentWeighting(weight);
		asn1.setCategory(category);
		asn1.setId(++id);
		Assignment asn2 = new Assignment(gradebook, "ASN-2", Double.valueOf(40), dueDate);
		asn2.setAssignmentWeighting(weight);
		asn2.setCategory(category);
		asn2.setId(++id);
		Assignment asn3 = new Assignment(gradebook, "ASN-3", Double.valueOf(50), dueDate);
		asn3.setAssignmentWeighting(weight);
		asn3.setCategory(category);
		asn3.setId(++id);
		Assignment asn4 = new Assignment(gradebook, "ASN-4", Double.valueOf(10), dueDate);
		asn4.setAssignmentWeighting(weight);
		asn4.setCategory(category);
		asn4.setId(++id);
		Assignment asn5 = new Assignment(gradebook, "ASN-5", Double.valueOf(10), dueDate);
		asn4.setAssignmentWeighting(weight);
		asn4.setCategory(category);
		asn4.setId(++id);
		Assignment asn6 = new Assignment(gradebook, "ASN-6", Double.valueOf(10), dueDate);
		asn4.setAssignmentWeighting(weight);
		asn4.setCategory(category);
		asn4.setId(++id);
		
		Double[] values = {
				0d ,
				0d ,
				0d ,
				9.0000000d 
		};
		

		
		List<Category> items = new ArrayList<Category>();
		items.add(category);
		
		List<Assignment> assignments = Arrays.asList(asn1, asn2, asn3, asn4, asn5, asn6);
		category.setAssignmentList(assignments);
		
		gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_ONLY_CATEGORY);
		assertEquals("Course Grade - 9 out of 10 - Just Category Weighting", 
				FULL_PRECISION ?
						new BigDecimal("90.0")
						: new BigDecimal("90.00"), 
						
				calculator.getCourseGrade(gradebook, items, getRecordUnits(values, asn4, "4"), false));
		
		gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY);
		category.setWeight(1d);
		assertEquals("Course Grade - 9 out of 10 - Just Category Weighting", 
				FULL_PRECISION ?
						new BigDecimal("89.999999999999999999999999999999999999999999999999939999999999999772000")
						: new BigDecimal("89.999999999999999996400000000000000"),
				
				calculator.getCourseGrade(gradebook, items, getRecordUnits(values, asn4, "4"), false));
		
		
		
	}
	
	private Map<Long, AssignmentGradeRecord> getRecordUnits(Double[] scores, Assignment ass, String studentId) {
		Map<Long, AssignmentGradeRecord> units = new HashMap<Long, AssignmentGradeRecord>();

		for (int i=0;i<scores.length;i++) {
			BigDecimal pointsEarned = scores[i] == null ? null : BigDecimal.valueOf((Double)scores[i]);
			
			units.put(ass.getId(), 
					new AssignmentGradeRecord(ass, studentId, pointsEarned.doubleValue()));
		}

		return units;
	}
	

	public void testGetNewPointsGrade() {
		

		assertEquals("NewPointsGrade - ", 
				FULL_PRECISION ? 
						new BigDecimal("89.189189189189189189189189189189189189189189189189100")
						: new BigDecimal("89.18918917"), 
						calculator.getNewPointsGrade(66d, 100d, 74d));
		
		assertEquals("NewPointsGrade - ", 
				FULL_PRECISION ? 
						new BigDecimal("89.999999999999999999999999999999999999999999999999910")
						: new BigDecimal("89.99999998"), 
						calculator.getNewPointsGrade(66.6d, 100d, 74d));
		
	}

	public void testGetPercentAsPointsEarned() {
		Gradebook gradebook = new Gradebook("GRADEBOOK_ID");
		gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY);
		Assignment asn = new Assignment(gradebook, "ASN", 10d, null);
		
		assertEquals("PercentAsPoints - 15 sig decimal places", new BigDecimal("8.99999999999999900"),
				calculator.getPercentAsPointsEarned(asn, 89.99999999999999d));
		assertEquals("PercentAsPoints - 16 sig decimal places", new BigDecimal("9.00"),
				calculator.getPercentAsPointsEarned(asn, 89.9999999999999999d));
		
		
	}

	public void testGetPointsEarnedAsPercent() {
		Gradebook gradebook = new Gradebook("GRADEBOOK_ID");
		gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY);
		Assignment asn = new Assignment(gradebook, "ASN", 10d, null);
		
		AssignmentGradeRecord agr = new AssignmentGradeRecord(asn, "", 9d);
		assertEquals("Points as percent - grade = 9", 
				FULL_PRECISION ? 
						new BigDecimal("90") 
						: new BigDecimal("90.00000"), 
				calculator.getPointsEarnedAsPercent(asn, agr));
		
		agr.setPointsEarned(8.9999999999d);
		assertEquals("Points as percent - grade = 8.9999999999d", 
				FULL_PRECISION ? 
						new BigDecimal("89.999999999") 
						: new BigDecimal("90.000000"),
				calculator.getPointsEarnedAsPercent(asn, agr));
		
		agr.setPointsEarned(8.999999999d);
		assertEquals("Points as percent - grade = 8.999999999d", new BigDecimal("89.99999999"),
				calculator.getPointsEarnedAsPercent(asn, agr));

		assertNull("points as percent - null", calculator.getPointsEarnedAsPercent(null, agr));

	}
	
	public void testGetDoublePointForLetterGradeRecord() {
		
		Gradebook gradebook = new Gradebook("GRADEBOOK_ID");
		gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY);
		Assignment asn = new Assignment(gradebook, "ASN", 10d, null);
		
		LetterGradePercentMapping mapping = new LetterGradePercentMapping();
		mapping.setGradeMap(gradeMap);
		calculator.setLetterGradeMap(gradeMap);
		
		
		/// should end up with the midpoint for the lettergrade in each case
		
		AssignmentGradeRecord agr = new AssignmentGradeRecord(asn, "", 9d);
		agr.setLetterEarned(calculator.convertPercentageToLetterGrade(calculator.getPointsEarnedAsPercent(asn, agr)));
		
		assertEquals("get double for letter grade - 9d", 
				FULL_PRECISION ?
						9.16666666666d
						: 9.166666667d,
						calculator.calculateDoublePointForLetterGradeRecord(asn, mapping, agr));
		
		agr = new AssignmentGradeRecord(asn, "", 8.9d);
		agr.setLetterEarned(calculator.convertPercentageToLetterGrade(calculator.getPointsEarnedAsPercent(asn, agr)));
		assertEquals("get double for letter grade - 8.9d", 
				
				FULL_PRECISION ?
						8.83333333333d
						: 8.833333333d,
						calculator.calculateDoublePointForLetterGradeRecord(asn, mapping, agr));
		
		agr = new AssignmentGradeRecord(asn, "", 8.999999d);
		agr.setLetterEarned(calculator.convertPercentageToLetterGrade(calculator.getPointsEarnedAsPercent(asn, agr)));
		assertEquals("get double for letter grade - 8.999999d", 
				
				FULL_PRECISION ?
						9.16666666666d
						: 9.166666667d,
						calculator.calculateDoublePointForLetterGradeRecord(asn, mapping, agr));
		
		
	}
	
	public void testCalculateDoublePointForRecord() {
		Gradebook gradebook = new Gradebook("GRADEBOOK_ID");
		gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY);
		Assignment asn = new Assignment(gradebook, "ASN", 10d, null);
		
		
		/* 0 decimal places */
		AssignmentGradeRecord agr = new AssignmentGradeRecord(asn, "", 9d);
		agr.setPercentEarned(calculator.getPointsEarnedAsPercent(asn, agr).doubleValue());
		
		assertEquals("Double Points from Record - 9d", 9d, calculator.calculateDoublePointForRecord(asn, agr));
		
		/* 1 decimal places */
		agr = new AssignmentGradeRecord(asn, "", 8.9d);
		agr.setPercentEarned(calculator.getPointsEarnedAsPercent(asn, agr).doubleValue());
		
		assertEquals("Double Points from Record - 8.9d", 8.9d, calculator.calculateDoublePointForRecord(asn, agr));
		
		/* 6 decimal places - first instance where input differs from output */
		agr = new AssignmentGradeRecord(asn, "", 8.999999d);
		agr.setPercentEarned(calculator.getPointsEarnedAsPercent(asn, agr).doubleValue());
		
		assertEquals("Double Points from Record - 8.999999d", 
						8.999998999999999d, calculator.calculateDoublePointForRecord(asn, agr));
		
		/* 7 decimal places */
		agr = new AssignmentGradeRecord(asn, "", 8.9999999d);
		agr.setPercentEarned(calculator.getPointsEarnedAsPercent(asn, agr).doubleValue());
		
		assertEquals("Double Points from Record - 8.9999999d", 
						8.999999899999999d, calculator.calculateDoublePointForRecord(asn, agr));
		
		/* 8 decimal places */		
		agr = new AssignmentGradeRecord(asn, "", 8.99999999d);
		agr.setPercentEarned(calculator.getPointsEarnedAsPercent(asn, agr).doubleValue());
		
		assertEquals("Double Points from Record - 8.99999999d", 
						8.999999990000001d, calculator.calculateDoublePointForRecord(asn, agr));
		
		/* 9 decimal places */
		agr = new AssignmentGradeRecord(asn, "", 8.999999999d);
		agr.setPercentEarned(calculator.getPointsEarnedAsPercent(asn, agr).doubleValue());
		
		assertEquals("Double Points from Record - 8.999999999d", 
						8.999999999d, calculator.calculateDoublePointForRecord(asn, agr));
		
		/* 10 decimal places */
		agr = new AssignmentGradeRecord(asn, "", 8.9999999999d);
		agr.setPercentEarned(calculator.getPointsEarnedAsPercent(asn, agr).doubleValue());
		
		assertEquals("Double Points from Record - 8.9999999999d", 
				FULL_PRECISION ?
						8.999999999899998d
						: 9.0d, 
						calculator.calculateDoublePointForRecord(asn, agr));
		
		
	}



}
