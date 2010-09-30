package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import org.sakaiproject.tool.gradebook.Category;
import org.sakaiproject.tool.gradebook.Gradebook;
import org.sakaiproject.gradebook.gwt.sakai.calculations2.GradeCalculationsImpl;

import sun.security.util.BigInt;

public class GradeCalculationsOOImplTest extends TestCase {
	
	public static final String GRADE_DATA_FILE_PATH = "org/sakaiproject/gradebook/gwt/sakai/calculations/GradeData.dat";
	public static final Object PROP_GRADE_DATA_FILE_PATH = "gb2.test.gradedata.path";
	private String dataFilePath = GRADE_DATA_FILE_PATH;
	
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
	private boolean FULL_PRECISION = true;
	
	BigDecimalCalculationsWrapper helper = new BigDecimalCalculationsWrapper(50);

	public GradeCalculationsOOImplTest() {
		

		if(FULL_PRECISION)
			calculator = new GradeCalculationsImpl(50);
		else
			calculator = new GradeCalculationsOOImpl();
		
		if (System.getProperties().contains(PROP_GRADE_DATA_FILE_PATH)) {
			if (dataFilePath != null )
				try {
					File file = new File(dataFilePath.trim());
					if(!(file.exists() && file.canRead())) {
						System.err.println("Cannot read file '" + dataFilePath + "' ... using default: " + GRADE_DATA_FILE_PATH);
					}
				} catch (SecurityException se) {
					System.err.println("SecurityException: Cannot read file '" + dataFilePath + "' ... using default: " + GRADE_DATA_FILE_PATH);
				}
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

	// not an api method
//	public void testCalculateItemGradePercentDecimal() {
//		System.out.println("testCalculateItemGradePercentDecimal not tested: not an implementation if a API method");
//		
//	}

	public void testCalculateStatistics() {
		GradeDataLoader data = new GradeDataLoader(GRADE_DATA_FILE_PATH);
		
		assertTrue(data.isAllTestStatsKeysPresent());
		assertNotNull(data.getScores());
		
		// calculate stats using commons-math
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

		GradeStatistics calculatedStats = calculator.calculateStatistics(data.getScores(), getScoresSum(data), null);


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
				getOnlinePopulationStandardDeviation(data), 
				calculatedStats.getStandardDeviation());
		
		

		
		/* STANDARD DEVIATION of the population */
		String fromFile = (String)data.getTestStatsByKey().get(GradeDataLoader.INPUT_KEY_STDEVP);	
		
		/// this tests value, precision and scale
		assertEquals("StatsTest-stdevp-from-test-file", new BigDecimal(fromFile), calculatedStats.getStandardDeviation().setScale(data.getScale()));
			
		
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
		assertEquals("StatsTest-mean-from-test-file", new BigDecimal(fromFile), calculatedStats.getMean());
		
		
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
		
		//System.out.println("open-office stdevp: " + new BigDecimal("19.4463453858135"));
		//System.out.println("calculated   stdev: " + calculatedStats.getStandardDeviation()
		//		.setScale(13, RoundingMode.HALF_UP));
		
		
		/*
		 * assertEquals("StatsTest-stddev-openoffice-population",
		 
				// expected value is same as calculated with 
				// open office (v3.1.1-19.34.fc12)
				// using stdevp() : "population standard deviation"
				// oocalc apparently uses a scale of 13 with half_up
				0, (new BigDecimal("19.4463453858135")).compareTo( 
				calculatedStats.getStandardDeviation().setScale(13, RoundingMode.HALF_UP)));
		*/
		
		
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
