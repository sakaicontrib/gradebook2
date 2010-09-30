package org.sakaiproject.gradebook.gwt.sakai.calculations2;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.sakai.GradeCalculations;
import org.sakaiproject.gradebook.gwt.sakai.calculations.CategoryCalculationUnit;
import org.sakaiproject.gradebook.gwt.sakai.calculations.GradeRecordCalculationUnit;
import org.sakaiproject.gradebook.gwt.sakai.calculations.GradebookCalculationUnit;
import org.sakaiproject.gradebook.gwt.sakai.model.GradeItem;
import org.sakaiproject.gradebook.gwt.sakai.model.GradeStatistics;
import org.sakaiproject.gradebook.gwt.sakai.model.StudentScore;
import org.sakaiproject.gradebook.gwt.server.Util;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.AssignmentGradeRecord;
import org.sakaiproject.tool.gradebook.Category;
import org.sakaiproject.tool.gradebook.Gradebook;
import org.sakaiproject.tool.gradebook.LetterGradePercentMapping;

public class GradeCalculationsImpl extends BigDecimalCalculationsWrapper implements GradeCalculations {

	private static final Log log = LogFactory.getLog(GradeCalculationsImpl.class);

	/* a MathContext for inexact values. 
	 * precision = 10 using standard engineering half-up rounding */
	//public static final MathContext MATH_CONTEXT_LG = new MathContext(10, RoundingMode.HALF_UP);

	/* a BigDecimal for percentage calculations. scale = 5 */
	private final static BigDecimal BIG_DECIMAL_100 = new BigDecimal("100");

	/*
	 * GRBK-668 
	 * The old code did not use an evenly distributed grading scale.  
	 * This does.  It also uses full precision numbers, possibly overkill, but it is wanted. 
	 * 
	 * Note:  special adds are done to fully represent fractions.  
	 */
	private  BigDecimal two;
	private  BigDecimal three; 
	private  BigDecimal oneThird; 
	private  BigDecimal twoThirds;

	private  static int PRECISION = 7;

	private  BigDecimal gradeAplus;
	private  BigDecimal gradeA;
	private  BigDecimal gradeAminus;

	private  BigDecimal gradeBplus;
	private  BigDecimal gradeB;
	private  BigDecimal gradeBminus;

	private  BigDecimal gradeCplus;
	private  BigDecimal gradeC;
	private  BigDecimal gradeCminus;


	private  BigDecimal gradeDplus;
	private  BigDecimal gradeD;
	private  BigDecimal gradeDminus;

	private  BigDecimal gradeF;

	private final static String A_PLUS = "A+";
	private final static String A = "A";
	private final static String A_MINUS = "A-";
	private final static String B_PLUS = "B+";
	private final static String B = "B";
	private final static String B_MINUS = "B-";
	private final static String C_PLUS = "C+";
	private final static String C = "C";
	private final static String C_MINUS = "C-";
	private final static String D_PLUS = "D+";
	private final static String D = "D";
	private final static String D_MINUS = "D-";
	private final static String F = "F";
	private final static String ZERO = "0";

	public Map<String, Double> letterGradeMap;

	// Constructor
	public GradeCalculationsImpl() {
		super();
		log.info("GradeCalculationsImpl default constructor called.");
	}

	// Constructor
	public GradeCalculationsImpl(int precision) {
		super(precision);
		log.info("#### TEST #### GradeCalculationsImpl(int precision) constructor called. This should only occure during JUnit tests");
	}


	// Spring IoC init
	public void init() {

		two = new BigDecimal("2");
		three = new BigDecimal("3"); 
		oneThird = divide(BigDecimal.ONE, three); 
		twoThirds = divide(two, three);

		PRECISION = 7;

		gradeAplus = new BigDecimal(add(new BigDecimal("96"), twoThirds).toPlainString().substring(0, PRECISION));
		gradeA = new BigDecimal(add(new BigDecimal("93"), oneThird).toPlainString().substring(0, PRECISION));
		gradeAminus = new BigDecimal("90");

		gradeBplus = new BigDecimal(add(new BigDecimal("86"), twoThirds).toPlainString().substring(0, PRECISION));
		gradeB = new BigDecimal(add(new BigDecimal("83"), oneThird).toPlainString().substring(0, PRECISION));
		gradeBminus = new BigDecimal("80");

		gradeCplus = new BigDecimal(add(new BigDecimal("76"), twoThirds).toPlainString().substring(0, PRECISION));
		gradeC = new BigDecimal(add(new BigDecimal("73"), oneThird).toPlainString().substring(0, PRECISION));
		gradeCminus = new BigDecimal("70");


		gradeDplus = new BigDecimal(add(new BigDecimal("66"), twoThirds).toPlainString().substring(0, PRECISION));
		gradeD = new BigDecimal(add(new BigDecimal("63"), oneThird).toPlainString().substring(0, PRECISION));
		gradeDminus = new BigDecimal("60");

		gradeF = new BigDecimal("60");

	}

	public Double calculateEqualWeight(int numberOfItems) {
		if (numberOfItems <= 1)
			return Double.valueOf(1d);

		BigDecimal result = divide(BigDecimal.ONE, BigDecimal.valueOf(numberOfItems) );

		return Double.valueOf(result.doubleValue());
	}

	public Double calculateItemWeightAsPercentage(Double requestedItemWeight, Double requestedItemPoints) {
		BigDecimal weight = null;

		// Obviously, if the user asks for a non-null value, give it to them
		if (requestedItemWeight != null)
			weight = BigDecimal.valueOf(requestedItemWeight.doubleValue());
		else
			weight = BigDecimal.valueOf(requestedItemPoints.doubleValue());

		BigDecimal result = divide(weight, BIG_DECIMAL_100);

		return Double.valueOf(result.doubleValue());
	}

	public BigDecimal[] calculatePointsCategoryPercentSum(Category category, List<Assignment> assignments, boolean isWeighted, boolean isCategoryExtraCredit) {		
		return populateGradeRecordUnits(category, null, assignments, null, null, isWeighted, isCategoryExtraCredit);
	}

	public BigDecimal[] calculatePointsCategoryPercentSum(GradeItem category, List<GradeItem> assignments, CategoryType categoryType, boolean isCategoryExtraCredit) {
		return populateGradeRecordUnits(category, null, assignments, null, null, categoryType, isCategoryExtraCredit);
	}

	public BigDecimal[] calculateCourseGradeCategoryPercents(Assignment a, BigDecimal percentGrade, BigDecimal percentCategorySum, BigDecimal pointsSum, boolean isEnforcePointWeighting) {

		boolean isUnweighted = (a != null) ? a.isNotCounted() : false;

		BigDecimal courseGradePercent = BigDecimal.ZERO;
		BigDecimal percentCategory = BigDecimal.ZERO;
		if (!isUnweighted) {
			if (isEnforcePointWeighting) {
				double p = a == null || a.getPointsPossible() == null ? 0d : a.getPointsPossible().doubleValue();

				BigDecimal assignmentPoints = BigDecimal.valueOf(p);

				courseGradePercent = calculateItemGradePercent(percentGrade, pointsSum, assignmentPoints, true);

				if (assignmentPoints.compareTo(BigDecimal.ZERO) == 0 || pointsSum.compareTo(BigDecimal.ZERO) == 0)
					percentCategory = BigDecimal.ZERO;
				else
					percentCategory = multiply(BIG_DECIMAL_100, divide(assignmentPoints, pointsSum));

			} else {
				double w = a == null || a.getAssignmentWeighting() == null ? 0d : a.getAssignmentWeighting().doubleValue();

				BigDecimal assignmentWeight = BigDecimal.valueOf(w);
				courseGradePercent = calculateItemGradePercent(percentGrade, percentCategorySum, assignmentWeight, false);
				percentCategory = multiply(BIG_DECIMAL_100, assignmentWeight);

			}
		}


		BigDecimal[] result = new BigDecimal[2];
		result[0] = courseGradePercent;
		result[1] = percentCategory;

		return result;
	}

	public BigDecimal[] calculateCourseGradeCategoryPercents(GradeItem a, BigDecimal percentGrade, 
			BigDecimal percentCategorySum, BigDecimal pointsSum, boolean isEnforcePointWeighting) {

		boolean isIncluded = Util.checkBoolean((a == null) ? false : a.getIncluded());

		BigDecimal courseGradePercent = BigDecimal.ZERO;
		BigDecimal percentCategory = BigDecimal.ZERO;
		if (isIncluded) {
			if (isEnforcePointWeighting) {
				double p = (a == null || a.getPoints() == null) ? 0d : a.getPoints().doubleValue();

				BigDecimal assignmentPoints = BigDecimal.valueOf(p);

				courseGradePercent = calculateItemGradePercent(percentGrade, pointsSum, assignmentPoints, true);

				if (assignmentPoints.compareTo(BigDecimal.ZERO) == 0 || pointsSum.compareTo(BigDecimal.ZERO) == 0)
					percentCategory = BigDecimal.ZERO;
				else
					percentCategory = multiply(BIG_DECIMAL_100, divide(assignmentPoints, pointsSum));

			} else {
				double w = a == null || a.getWeighting() == null ? 0d : a.getWeighting().doubleValue();

				BigDecimal assignmentWeight = BigDecimal.valueOf(w);
				courseGradePercent = calculateItemGradePercentDecimal(percentGrade, percentCategorySum, assignmentWeight, false);
				percentCategory = assignmentWeight;

			}
		}


		BigDecimal[] result = new BigDecimal[2];
		result[0] = courseGradePercent;
		result[1] = percentCategory;

		return result;
	}

	/*
	 *  For example: 
	 *  
	 *  (a) If percentGrade is 60, sumCategoryPercents is 100, and assignmentWeight is 0.25, then this item should be worth 15 % of the course grade
	 *  
	 *  	15 = ( 60 * .25 ) / 1
	 *  	
	 *  (b) If percentGrade is 60, sumCategoryPercents is 80, and assignmentWeight is 0.25, then this item should be worth > 15 % of the course grade
	 * 
	 * 		x  = ( 60 * .25 ) / .8
	 * 
	 */
	public BigDecimal calculateItemGradePercent(BigDecimal percentGrade, BigDecimal sumCategoryPercents, BigDecimal assignmentWeight, boolean doNormalizeTo100) {

		if (percentGrade.compareTo(BigDecimal.ZERO) == 0 
				|| sumCategoryPercents.compareTo(BigDecimal.ZERO) == 0 
				|| assignmentWeight.compareTo(BigDecimal.ZERO) == 0)
			return BigDecimal.ZERO;

		BigDecimal categoryPercentRatio = null;

		if (doNormalizeTo100)
			categoryPercentRatio = sumCategoryPercents;
		else
			categoryPercentRatio = divide(sumCategoryPercents, BIG_DECIMAL_100);

		return divide(multiply(assignmentWeight, percentGrade), categoryPercentRatio);
	}

	public BigDecimal calculateItemGradePercentDecimal(BigDecimal percentGrade, BigDecimal sumCategoryPercents, BigDecimal assignmentWeight, boolean doNormalizeTo100) {

		if (percentGrade.compareTo(BigDecimal.ZERO) == 0 
				|| sumCategoryPercents.compareTo(BigDecimal.ZERO) == 0 
				|| assignmentWeight.compareTo(BigDecimal.ZERO) == 0)
			return BigDecimal.ZERO;

		BigDecimal categoryPercentRatio = null;

		if (doNormalizeTo100)
			categoryPercentRatio = sumCategoryPercents;
		else
			categoryPercentRatio = sumCategoryPercents; // TODO: Do we need the if doNormalizeTo100 ?
		//categoryPercentRatio = sumCategoryPercents.divide(BigDecimal.ONE, MATH_CONTEXT);

		return divide(multiply(assignmentWeight, percentGrade), categoryPercentRatio);
	}

/*
 * Notes for calculation of running or online standard deviation:
 * 
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
 * (also see: http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance)
 */

	public GradeStatistics calculateStatistics(List<StudentScore> gradeList, BigDecimal sum, String rankStudentId) {
		GradeStatistics statistics = new GradeStatistics();
		List<BigDecimal> modeList = null; 
		if (gradeList == null || gradeList.isEmpty())
			return statistics;

		int n = 0;
		BigDecimal count = BigDecimal.valueOf(gradeList.size());
		BigDecimal mean = null; 
		BigDecimal variance = null;

		if (count.compareTo(BigDecimal.ZERO) != 0)
			mean = divide(sum, count); /// first estimate the mean

		List<FrequencyScore> frequencies = new ArrayList<FrequencyScore>();
		Map<BigDecimal, Integer> frequencyMap = new HashMap<BigDecimal, Integer>();
		BigDecimal standardDeviation = null;
		// Once we have the mean course grade, we can calculate the standard deviation from that mean
		// That is, for the equation S = sqrt(A/c)
		if (gradeList != null && mean != null) {
			BigDecimal sumOfSquareOfDifferences = BigDecimal.ZERO; // the sum of squares of differences from the *current* mean
			for (StudentScore rec : gradeList) {
				n++;
				BigDecimal courseGrade = rec.getScore(); 

				BigDecimal roundedCourseGrade = courseGrade.setScale(2, GradeCalculations.MATH_CONTEXT.getRoundingMode());
				 
				BigDecimal difference = subtract(courseGrade, mean);
				

				mean = add(mean,divide(difference, BigDecimal.valueOf(n))); // new mean value
				
				sumOfSquareOfDifferences = // summation using new mean value
					add(sumOfSquareOfDifferences, multiply(difference, subtract(courseGrade, mean))); 

				Integer frequency = frequencyMap.get(roundedCourseGrade);
				if (frequency == null)
					frequency = Integer.valueOf(1);
				else
					frequency = Integer.valueOf(1 + frequency.intValue());

				frequencyMap.put(roundedCourseGrade, frequency);

			}
			

			if (frequencyMap.size() > 0) 
			{
				modeList = new ArrayList<BigDecimal>(); 

				Set<BigDecimal> keys = frequencyMap.keySet(); 

				for (BigDecimal k : keys)
				{
					FrequencyScore sc = new FrequencyScore();
					Integer frequency = frequencyMap.get(k);

					sc.setFrequency(frequency);
					sc.setScore(k); 
					frequencies.add(sc);
				}

				Collections.sort(frequencies); 
				Collections.reverse(frequencies);

				boolean first = true; 
				Iterator<FrequencyScore> it = frequencies.iterator();
				FrequencyScore largest = null; 
				while (it.hasNext())
				{
					FrequencyScore s = it.next(); 
					if (first)
					{
						largest = s; 
						modeList.add(s.getScore()); 
						first = false; 
					}
					else
					{
						if (s.getFrequency().equals(largest.getFrequency()))
						{
							modeList.add(s.getScore()); 
						}
					}
				}
				// GBRK-325 so if the largest frequency in the set is 1, we really have no mode... 
				if (largest.getFrequency().intValue() == 1)
				{
					modeList.clear();
				}

			}

			if (count.compareTo(BigDecimal.ZERO) != 0 && sumOfSquareOfDifferences.compareTo(BigDecimal.ZERO) != 0) {
				variance = divide(sumOfSquareOfDifferences, count);
				if (variance != null && variance.compareTo(BigDecimal.ZERO) != 0)
					standardDeviation = sqrt(variance);
				
			}
		}

		BigDecimal median = null;
		if (gradeList != null && gradeList.size() > 0) {
			if (gradeList.size() == 1) {
				median = gradeList.get(0).getScore();
			} else {
				Collections.sort(gradeList);
				int middle = gradeList.size() / 2;
				if (gradeList.size() % 2 == 0) {
					// If we have an even number of elements then grab the middle two
					BigDecimal first = gradeList.get(middle - 1).getScore();
					BigDecimal second = gradeList.get(middle).getScore();

					BigDecimal s = add(first, second);

					if (s.compareTo(BigDecimal.ZERO) == 0)
						median = BigDecimal.ZERO;
					else
						median = divide(s, new BigDecimal("2"));
				} else {
					// If we have an odd number of elements, simply choose the middle one
					median = gradeList.get(middle).getScore();
				}
			}

			if (rankStudentId != null && gradeList != null && gradeList.size() > 0)
			{


				Iterator<StudentScore> it = gradeList.iterator();
				StudentScore targ = null; 
				while (it.hasNext())
				{
					StudentScore s = it.next(); 
					log.debug("rankStudentId: " + rankStudentId);
					log.debug("sgetUserUid(): " + s.getUserUid());
					if (s.getUserUid().equals(rankStudentId))
					{
						targ = s; 
					}
				}

				if (targ != null)
				{
					Collections.reverse(gradeList); 

					for (int i = 0; i < gradeList.size() ; i++)
					{
						StudentScore cur = gradeList.get(i); 

						if (targ.getScore().equals(cur.getScore()))
						{
							statistics.setRank(i+1);
							statistics.setStudentTotal(gradeList.size()); 

						}
					}
				}
				else
				{
					statistics.setRank(-1);
					statistics.setStudentTotal(gradeList.size());
				}
			}
		}


		//scale and round the output...
		// here at the end of the process, we are using half_up

		statistics.setStudentTotal(gradeList.size()); 
		statistics.setMean(mean.setScale(getScale(), RoundingMode.HALF_UP));
		statistics.setMedian(median);
		statistics.setModeList(modeList);
		statistics.setStandardDeviation(standardDeviation.setScale(getScale(), RoundingMode.HALF_UP));

		return statistics;
	}

	public String convertPercentageToLetterGrade(BigDecimal percentage) {

		if (percentage != null) {

			percentage = new BigDecimal(percentage.toPlainString(), new MathContext(6, RoundingMode.HALF_UP));

			if (percentage.compareTo(BigDecimal.ZERO) == 0)
				return ZERO;

			if (percentage.compareTo(gradeF) < 0)
				return F; 

			if (percentage.compareTo(gradeAplus) >= 0)
				return A_PLUS;

			if (percentage.compareTo(gradeA) >= 0)
				return A;

			if (percentage.compareTo(gradeAminus) >= 0)
				return A_MINUS;

			if (percentage.compareTo(gradeBplus) >= 0)
				return B_PLUS;

			if (percentage.compareTo(gradeB) >= 0)
				return B;

			if (percentage.compareTo(gradeBminus) >= 0)
				return B_MINUS;

			if (percentage.compareTo(gradeCplus) >= 0)
				return C_PLUS;

			if (percentage.compareTo(gradeC) >= 0)
				return C;

			if (percentage.compareTo(gradeCminus) >= 0)
				return C_MINUS;

			if (percentage.compareTo(gradeDplus) >= 0)
				return D_PLUS;

			if (percentage.compareTo(gradeD) >= 0)
				return D;

			if (percentage.compareTo(gradeDminus) >= 0)
				return D_MINUS;

		}

		return null;
	}

	public boolean isValidLetterGrade(String letterGrade) {
		if (letterGrade == null || letterGrade.trim().length() == 0)
			return true;

		return letterGradeMap.containsKey(letterGrade.toUpperCase());
	}

	public Double convertLetterGradeToPercentage(String letterGrade) {
		Double percentage = null;

		if (letterGrade != null) {
			return letterGradeMap.get(letterGrade.toUpperCase());
		}

		return percentage;
	}

	public BigDecimal getCategoryWeight(Category category) {
		BigDecimal categoryWeight = null;

		if (null == category || isDeleted(category)) {
			return null;
		}

		Gradebook gradebook = category.getGradebook();

		switch (gradebook.getCategory_type()) {
		case GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY:
			if (null == category.getWeight() || isUnweighted(category))
				return null;
			categoryWeight = new BigDecimal(category.getWeight().toString());
			break;
		default:
			categoryWeight = BigDecimal.ZERO;

			List<Assignment> assignments = (List<Assignment>)category.getAssignmentList();
			if (assignments != null) {
				for (Assignment assignment : assignments) {
					BigDecimal assignmentWeight = getAssignmentWeight(assignment);

					if (assignmentWeight != null)
						categoryWeight = add(categoryWeight, assignmentWeight);
				}
			}
			break;
		}

		return categoryWeight;
	}

	@SuppressWarnings("unchecked")
	public BigDecimal getCourseGrade(Gradebook gradebook, Collection<?> items, Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap, boolean isExtraCreditScaled) {
		boolean isWeighted = true;
		switch (gradebook.getCategory_type()) {
		case GradebookService.CATEGORY_TYPE_NO_CATEGORY:
			return getNoCategoriesCourseGrade((Collection<Assignment>)items, assignmentGradeRecordMap, isExtraCreditScaled);
		case GradebookService.CATEGORY_TYPE_ONLY_CATEGORY:
			isWeighted = false;
		case GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY:
			return getCategoriesCourseGrade((Collection<Category>)items, assignmentGradeRecordMap, isWeighted, isExtraCreditScaled);
		}

		return null;
	}

	private BigDecimal getNoCategoriesCourseGrade(Collection<Assignment> assignments, Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap, boolean isExtraCreditScaled) {
		List<GradeRecordCalculationUnit> gradeRecordUnits = new ArrayList<GradeRecordCalculationUnit>();

		BigDecimal totalGradebookPoints = populateGradeRecordUnits(null, null, assignments, gradeRecordUnits, assignmentGradeRecordMap, false, false)[1];
		log.debug("getNoCategoriesCourseGrade - totalGradebookPoints=" + totalGradebookPoints);
		GradebookCalculationUnit gradebookUnit = new GradebookCalculationUnit();

		return gradebookUnit.calculatePointsBasedCourseGrade(gradeRecordUnits, totalGradebookPoints, isExtraCreditScaled);
	}

	@SuppressWarnings("unchecked")
	private BigDecimal getCategoriesCourseGrade(Collection<Category> categoriesWithAssignments, Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap,
			boolean isWeighted, boolean isExtraCreditScaled) {

		if (categoriesWithAssignments == null && assignmentGradeRecordMap != null) 
			categoriesWithAssignments = generateCategoriesWithAssignments(assignmentGradeRecordMap);

		if (categoriesWithAssignments == null) // || assignmentGradeRecordMap == null)
			return null;

		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();

		BigDecimal totalGradebookPoints = BigDecimal.ZERO;
		if (categoriesWithAssignments != null) {
			for (Category category : categoriesWithAssignments) {

				if (category == null)
					continue;

				if (category.isRemoved())
					continue;

				if (isUnweighted(category))
					continue;

				String categoryKey = String.valueOf(category.getId());

				BigDecimal categoryWeight = getCategoryWeight(category);
				CategoryCalculationUnit categoryCalculationUnit = new CategoryCalculationUnit(categoryWeight, Integer.valueOf(category.getDrop_lowest()), category.isExtraCredit(), category.isEnforcePointWeighting());
				categoryUnitMap.put(categoryKey, categoryCalculationUnit);

				List<GradeRecordCalculationUnit> gradeRecordUnits = new ArrayList<GradeRecordCalculationUnit>();

				List<Assignment> assignments = category.getAssignmentList();
				if (assignments == null)
					continue;

				boolean isExtraCreditCategory = isExtraCredit(category);

				BigDecimal totalCategoryPoints = populateGradeRecordUnits(category, categoryCalculationUnit, assignments, gradeRecordUnits, assignmentGradeRecordMap, isWeighted, isExtraCreditCategory)[1];

				if (isExtraCreditCategory)
					totalCategoryPoints = BigDecimal.ZERO;

				totalGradebookPoints = add(totalGradebookPoints, totalCategoryPoints);

				categoryGradeUnitListMap.put(categoryKey, gradeRecordUnits);

			} // for
		}

		GradebookCalculationUnit gradebookUnit = new GradebookCalculationUnit(categoryUnitMap);

		if (isWeighted)
			return gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, isExtraCreditScaled);

		return gradebookUnit.calculatePointsBasedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, isExtraCreditScaled);
	}

	private BigDecimal[] populateGradeRecordUnits(Category category, CategoryCalculationUnit categoryCalculationUnit, 
			Collection<Assignment> assignments, List<GradeRecordCalculationUnit> gradeRecordUnits, Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap, boolean isWeighted, boolean isExtraCreditCategory) {

		BigDecimal totalCategoryPoints = BigDecimal.ZERO;
		BigDecimal totalCategoryPercent = BigDecimal.ZERO;

		boolean isWeightByPointsCategory = category == null ? false : Util.checkBoolean(category.isEnforcePointWeighting());
		boolean doPreventUnequalDropLowest = ((isWeighted && isWeightByPointsCategory) || !isWeighted);

		Double lastPointValue = null;
		BigDecimal lastPercentValue = BigDecimal.ZERO;

		int dropLowest = category == null ? 0 : category.getDrop_lowest();
		// Check to ensure that we don't apply drop lowest with unweighted, unequal point value items
		if (dropLowest > 0 && assignments != null) {
			for (Assignment assignment : assignments) {

				if (assignment.isRemoved() || assignment.isNotCounted())
					continue;

				// Exclude extra credit items from determining whether drop lowest should be allowed
				if (Util.checkBoolean(assignment.isExtraCredit()))
					continue;

				if (doPreventUnequalDropLowest && lastPointValue != null && !lastPointValue.equals(assignment.getPointsPossible())) {
					if (categoryCalculationUnit != null)
						categoryCalculationUnit.setDropLowest(0);
					dropLowest = 0;
					break;
				}
				lastPointValue = assignment.getPointsPossible();

				BigDecimal wt = getAssignmentWeight(assignment);
				if (wt != null)
					lastPercentValue = wt;
			}
		}

		if (assignments != null) {
			for (Assignment assignment : assignments) {

				if (assignment.isRemoved())
					continue;

				if (isUnweighted(assignment))
					continue;

				BigDecimal assignmentWeight = getAssignmentWeight(assignment);

				boolean isExtraCreditItem = Util.checkBoolean(assignment.isExtraCredit());
				boolean isExtraCreditItemOrCategory = isExtraCreditCategory || isExtraCreditItem;
				boolean isNullsAsZeros = Util.checkBoolean(assignment.getCountNullsAsZeros());
				boolean isUnweighted = assignment.isNotCounted();

				if //(!isExtraCreditItemOrCategory 
				((!isExtraCreditItem || isExtraCreditCategory)
						&& assignment.getPointsPossible() != null) {
					totalCategoryPoints = add(totalCategoryPoints, BigDecimal.valueOf(assignment.getPointsPossible().doubleValue()));

					if (!isUnweighted && null != assignmentWeight) {
						//double assignmentCategoryPercent = assignment.getAssignmentWeighting() == null ? 0.0 : assignment.getAssignmentWeighting().doubleValue();	
						totalCategoryPercent = add(totalCategoryPercent, multiply(assignmentWeight, BIG_DECIMAL_100));
					}
				}

				if (gradeRecordUnits != null) {
					AssignmentGradeRecord assignmentGradeRecord = assignmentGradeRecordMap == null ? null : assignmentGradeRecordMap.get(assignment.getId());

					boolean isGraded = isGraded(assignmentGradeRecord);

					if (isNullsAsZeros || isGraded) {
						// Make sure it's not excused
						if (!isExcused(assignmentGradeRecord)) {

							BigDecimal pointsEarned = !isGraded ? BigDecimal.ZERO : new BigDecimal(assignmentGradeRecord.getPointsEarned().toString());
							BigDecimal pointsPossible = new BigDecimal(assignment.getPointsPossible().toString());

							Boolean isExtraCredit = Boolean.valueOf(isExtraCreditItemOrCategory);

							GradeRecordCalculationUnit gradeRecordUnit = new GradeRecordCalculationUnit(pointsEarned, 
									pointsPossible, assignmentWeight, isExtraCredit) {

								@Override
								public void setDropped(boolean isDropped) {
									super.setDropped(isDropped);

									AssignmentGradeRecord gradeRecord = (AssignmentGradeRecord)getActualRecord();

									if (gradeRecord != null && gradeRecord.getPointsEarned() != null)
										gradeRecord.setDropped(Boolean.valueOf(isDropped));
								}

							};

							gradeRecordUnit.setActualRecord(assignmentGradeRecord);

							gradeRecordUnits.add(gradeRecordUnit);
						}
					}
				}
			}
		}

		// When we get here we can assume that if drop lowest is greater than 0, it means the points are equal for
		// all items

		int numberOfItems = assignments == null ? 0 : assignments.size();

		if (dropLowest > 0 && totalCategoryPoints != null) {
			if (dropLowest > numberOfItems) 
				dropLowest = numberOfItems;

			BigDecimal representativePointsPossible = lastPointValue == null ? BigDecimal.ZERO : BigDecimal.valueOf(lastPointValue.doubleValue());
			totalCategoryPoints = subtract(totalCategoryPoints, multiply(BigDecimal.valueOf(dropLowest), representativePointsPossible));
			if (totalCategoryPercent != null && lastPercentValue != null)
				totalCategoryPercent = subtract(totalCategoryPercent, multiply(BigDecimal.valueOf(dropLowest), multiply(lastPercentValue, BIG_DECIMAL_100)));
		}

		if (categoryCalculationUnit != null)
			categoryCalculationUnit.setTotalCategoryPoints(totalCategoryPoints);

		BigDecimal[] result = new BigDecimal[2];
		result[0] = totalCategoryPercent;
		result[1] = totalCategoryPoints;

		return result;
	}


	private BigDecimal[] populateGradeRecordUnits(GradeItem category, CategoryCalculationUnit categoryCalculationUnit, 
			Collection<GradeItem> assignments, List<GradeRecordCalculationUnit> gradeRecordUnits, 
			Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap, CategoryType categoryType, boolean isExtraCreditCategory) {

		BigDecimal totalCategoryPoints = BigDecimal.ZERO;
		BigDecimal totalCategoryPercent = BigDecimal.ZERO;

		boolean isWeighted = categoryType == CategoryType.WEIGHTED_CATEGORIES;
		boolean isWeightByPointsCategory = category == null ? false : Util.checkBoolean(category.getEnforcePointWeighting());
		boolean doPreventUnequalDropLowest = ((isWeighted && isWeightByPointsCategory) || !isWeighted);

		Double lastPointValue = null;
		BigDecimal lastPercentValue = null;

		int dropLowest = category == null || category.getDropLowest() == null ? 0 : category.getDropLowest().intValue();
		// Check to ensure that we don't apply drop lowest with unweighted, unequal point value items
		if (dropLowest > 0 && assignments != null) {
			for (GradeItem assignment : assignments) {
				// Exclude extra credit items from determining whether drop lowest should be allowed
				if (Util.checkBoolean(assignment.getExtraCredit()))
					continue;

				if (doPreventUnequalDropLowest && lastPointValue != null && !lastPointValue.equals(assignment.getPoints())) {
					if (categoryCalculationUnit != null)
						categoryCalculationUnit.setDropLowest(0);
					dropLowest = 0;
					break;
				}
				lastPointValue = assignment.getPoints();
				lastPercentValue = getAssignmentWeight(assignment, categoryType);
			}
		}

		if (assignments != null) {
			for (GradeItem assignment : assignments) {

				if (Util.checkBoolean(assignment.getRemoved()))
					continue;

				boolean isIncluded = Util.checkBoolean(assignment.getIncluded());

				if (! isIncluded)
					continue;

				BigDecimal assignmentWeight = getAssignmentWeight(assignment, categoryType);

				boolean isExtraCreditItem = Util.checkBoolean(assignment.getExtraCredit());
				boolean isExtraCreditItemOrCategory = isExtraCreditCategory || isExtraCreditItem;
				boolean isNullsAsZeros = Util.checkBoolean(assignment.getNullsAsZeros());


				if //(!isExtraCreditItemOrCategory 
				((!isExtraCreditItem || isExtraCreditCategory)
						&& assignment.getPoints() != null) {
					totalCategoryPoints = add(totalCategoryPoints, BigDecimal.valueOf(assignment.getPoints().doubleValue()));

					if (null != assignmentWeight) {
						//double assignmentCategoryPercent = assignment.getAssignmentWeighting() == null ? 0.0 : assignment.getAssignmentWeighting().doubleValue();	
						totalCategoryPercent = add(totalCategoryPercent, assignmentWeight);
					}
				}


				if (gradeRecordUnits != null) {
					AssignmentGradeRecord assignmentGradeRecord = assignmentGradeRecordMap == null ? null : assignmentGradeRecordMap.get(assignment.getItemId());

					boolean isGraded = isGraded(assignmentGradeRecord);

					if (isNullsAsZeros || isGraded) {
						// Make sure it's not excused
						if (!isExcused(assignmentGradeRecord)) {

							BigDecimal pointsEarned = !isGraded ? BigDecimal.ZERO : new BigDecimal(assignmentGradeRecord.getPointsEarned().toString());
							BigDecimal pointsPossible = new BigDecimal(assignment.getPoints().toString());

							Boolean isExtraCredit = Boolean.valueOf(isExtraCreditItemOrCategory);

							GradeRecordCalculationUnit gradeRecordUnit = new GradeRecordCalculationUnit(pointsEarned, 
									pointsPossible, assignmentWeight, isExtraCredit) {

								@Override
								public void setDropped(boolean isDropped) {
									super.setDropped(isDropped);

									AssignmentGradeRecord gradeRecord = (AssignmentGradeRecord)getActualRecord();

									if (gradeRecord != null && gradeRecord.getPointsEarned() != null)
										gradeRecord.setDropped(Boolean.valueOf(isDropped));
								}

							};

							gradeRecordUnit.setActualRecord(assignmentGradeRecord);

							gradeRecordUnits.add(gradeRecordUnit);
						}
					}
				}
			}
		}

		// When we get here we can assume that if drop lowest is greater than 0, it means the points are equal for
		// all items

		int numberOfItems = assignments == null ? 0 : assignments.size();

		if (dropLowest > 0 && totalCategoryPoints != null) {
			if (dropLowest > numberOfItems) 
				dropLowest = numberOfItems;

			BigDecimal representativePointsPossible = lastPointValue == null ? BigDecimal.ZERO : BigDecimal.valueOf(lastPointValue.doubleValue());
			totalCategoryPoints = subtract(totalCategoryPoints, multiply(BigDecimal.valueOf(dropLowest), representativePointsPossible));
			if (null != totalCategoryPercent  && null != lastPercentValue)
			{
				totalCategoryPercent = subtract(totalCategoryPercent, multiply(BigDecimal.valueOf(dropLowest), lastPercentValue));
			}
		}

		if (categoryCalculationUnit != null)
			categoryCalculationUnit.setTotalCategoryPoints(totalCategoryPoints);

		BigDecimal[] result = new BigDecimal[2];
		result[0] = totalCategoryPercent;
		result[1] = totalCategoryPoints;

		return result;
	}

	public BigDecimal getNewPointsGrade(Double pointValue, Double maxPointValue, Double maxPointStartValue) {

		BigDecimal max = new BigDecimal(maxPointValue.toString());
		BigDecimal maxStart = new BigDecimal(maxPointStartValue.toString());
		BigDecimal ratio = BigDecimal.ZERO; 
		if (maxStart.compareTo(BigDecimal.ZERO) != 0)
		{
			ratio = divide(max, maxStart);
		}
		BigDecimal points = new BigDecimal(pointValue.toString());

		return multiply(points, ratio);
	}

	public BigDecimal getPercentAsPointsEarned(Assignment assignment, Double percentage) {
		BigDecimal pointsEarned = null;

		if (percentage != null) {
			BigDecimal percent = new BigDecimal(percentage.toString());
			BigDecimal maxPoints = new BigDecimal(assignment.getPointsPossible().toString());
			pointsEarned = multiply(divide(percent, BIG_DECIMAL_100), maxPoints);
		}

		return pointsEarned;	
	}

	public BigDecimal getPointsEarnedAsPercent(Assignment assignment, AssignmentGradeRecord assignmentGradeRecord) {
		BigDecimal percentageEarned = null;
		BigDecimal pointsEarned = null;
		BigDecimal pointsPossible = null;

		if (isBlank(assignment, assignmentGradeRecord)) {
			return percentageEarned;
		}

		pointsEarned = new BigDecimal(assignmentGradeRecord.getPointsEarned().toString());
		if (assignment.getPointsPossible() != null) {
			pointsPossible = new BigDecimal(assignment.getPointsPossible().toString());
			percentageEarned = divide(multiply(pointsEarned, BIG_DECIMAL_100), pointsPossible);
		}
		return percentageEarned;
	}
	
	public Double calculateDoublePointForRecord(Assignment assignment, AssignmentGradeRecord gradeRecordFromCall) {
		
		if(gradeRecordFromCall.getPercentEarned() != null)
		{
			if(gradeRecordFromCall.getPercentEarned().doubleValue() / 100.0 < 0)
			{
				throw new IllegalArgumentException("percent for record is less than 0 for percentage points in GradebookManagerHibernateImpl.calculateDoublePointForRecord");
			}
			return new Double(assignment.getPointsPossible().doubleValue() * (gradeRecordFromCall.getPercentEarned().doubleValue() / 100.0));
		}
		else
			return null;
	}

	
	public Double calculateDoublePointForLetterGradeRecord(Assignment assignment, LetterGradePercentMapping letterGradePercentMapping, AssignmentGradeRecord gradeRecordFromCall) {

		Double doublePercentage = letterGradePercentMapping.getValue(gradeRecordFromCall.getLetterEarned());
		if(doublePercentage == null)
		{
			log.error("percentage for " + gradeRecordFromCall.getLetterEarned() + " is not found in letter grade mapping in GradebookManagerHibernateImpl.calculateDoublePointForLetterGradeRecord");
			return null;
		}

		return calculateEquivalentPointValueForPercent(assignment.getPointsPossible(), doublePercentage);
	}

	
	protected Double calculateEquivalentPointValueForPercent(Double doublePointsPossible, Double doublePercentEarned) {
		
		if (doublePointsPossible == null || doublePercentEarned == null)
			return null;

		BigDecimal pointsPossible = new BigDecimal(doublePointsPossible.toString());
		BigDecimal percentEarned = new BigDecimal(doublePercentEarned.toString());
		BigDecimal equivPoints = multiply(pointsPossible, divide(percentEarned, BIG_DECIMAL_100));
		return new Double(equivPoints.doubleValue());
	}

	private Collection<Category> generateCategoriesWithAssignments(Map<Long, AssignmentGradeRecord>  assignmentGradeRecordMap) {
		Collection<Category> categoriesWithAssignments = new ArrayList<Category>();

		Map<Long, Category> categoryMap = new HashMap<Long, Category>();
		for (AssignmentGradeRecord gradeRecord : assignmentGradeRecordMap.values()) {
			if (gradeRecord != null) {
				Assignment assignment = gradeRecord.getAssignment();
				if (assignment != null) {
					Category category = assignment.getCategory();
					if (category != null) {
						Long categoryId = category.getId();

						Category storedCategory = categoryMap.get(categoryId);

						if (storedCategory != null)
							category = storedCategory;
						else {
							categoryMap.put(categoryId, category);
							category.setAssignmentList(new ArrayList<Assignment>());
						}

						category.getAssignmentList().add(assignment);
					}
				}
			}

			categoriesWithAssignments = categoryMap.values();
		}

		return categoriesWithAssignments;
	}

	private BigDecimal getAssignmentWeight(Assignment assignment) {

		BigDecimal assignmentWeight = null;

		// If the assignment doesn't exist or has no weight then we return null
		if (null == assignment || isDeleted(assignment)) 
			return null;

		Gradebook gradebook = assignment.getGradebook();

		switch (gradebook.getCategory_type()) {
		case GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY:
			if (null == assignment.getAssignmentWeighting() || isUnweighted(assignment)) 
				return null;
			assignmentWeight = new BigDecimal(assignment.getAssignmentWeighting().toString());
			break;
		default:
			if (null == assignment.getPointsPossible())
				return null;

			assignmentWeight = new BigDecimal(assignment.getPointsPossible().toString());
			break;
		}

		return assignmentWeight;
	}

	private BigDecimal getAssignmentWeight(GradeItem assignment, CategoryType categoryType) {

		BigDecimal assignmentWeight = null;

		// If the assignment doesn't exist or has no weight then we return null
		if (null == assignment || Util.checkBoolean(assignment.getRemoved())) 
			return null;

		switch (categoryType) {
		case WEIGHTED_CATEGORIES:
			if (null == assignment.getWeighting() || !Util.checkBoolean(assignment.getIncluded())) 
				return null;
			assignmentWeight = new BigDecimal(assignment.getWeighting().toString());
			break;
		default:
			if (null == assignment.getPoints())
				return null;

			assignmentWeight = new BigDecimal(assignment.getPoints().toString());
			break;
		}

		return assignmentWeight;
	}

	private boolean isBlank(Assignment assignment, AssignmentGradeRecord assignmentGradeRecord) {
		return null == assignment || null == assignmentGradeRecord || null == assignmentGradeRecord.getPointsEarned();
	}

	private boolean isDeleted(Assignment assignment) {
		return assignment.isRemoved();
	}

	private boolean isDeleted(Category category) {
		return category.isRemoved();
	}

	private boolean isExtraCredit(Assignment assignment) {
		return assignment.isExtraCredit() == null ? false : assignment.isExtraCredit().booleanValue();
	}

	private boolean isExtraCredit(Category category) {
		return category.isExtraCredit() == null ? false : category.isExtraCredit().booleanValue();
	}

	private boolean isGraded(AssignmentGradeRecord assignmentGradeRecord) {
		return assignmentGradeRecord != null && assignmentGradeRecord.getPointsEarned() != null;
	}

	private boolean isDropped(AssignmentGradeRecord assignmentGradeRecord) {
		return assignmentGradeRecord != null && assignmentGradeRecord.isDropped() != null && assignmentGradeRecord.isDropped().booleanValue();
	}

	private boolean isExcused(AssignmentGradeRecord assignmentGradeRecord) {
		if (assignmentGradeRecord == null)
			return false;

		return assignmentGradeRecord.isExcludedFromGrade() == null ? false : assignmentGradeRecord.isExcludedFromGrade().booleanValue();
	}


	private boolean isNormalCredit(Assignment assignment) {
		boolean isExtraCredit = isExtraCredit(assignment);
		return assignment.isCounted() && !assignment.isRemoved() && !isExtraCredit;
	}

	private boolean isUnweighted(Assignment assignment) {
		return assignment.isNotCounted();
	}

	private boolean isUnweighted(Category category) {
		return category.isUnweighted() == null ? false : category.isUnweighted().booleanValue();
	}

	public Map<String, Double> getLetterGradeMap() {
		return letterGradeMap;
	}

	public void setLetterGradeMap(Map<String, Double> letterGradeMap) {
		this.letterGradeMap = letterGradeMap;
	}

	static class FrequencyScore implements Comparable<FrequencyScore>
	{
		private BigDecimal score; 
		private Integer frequency;

		public BigDecimal getScore() {
			return score;
		}
		public void setScore(BigDecimal score) {
			this.score = score;
		}
		public Integer getFrequency() {
			return frequency;
		}
		public void setFrequency(Integer frequency) {
			this.frequency = frequency;
		}

		public int compareTo(FrequencyScore o) {
			if (o != null && o.getFrequency() != null)
			{
				return getFrequency().compareTo(o.getFrequency());
			}
			return -1;
		}
		@Override
		public boolean equals(Object obj) {

			if (obj instanceof FrequencyScore && obj != null)
			{
				FrequencyScore ext = (FrequencyScore) obj; 
				if (ext.getScore() != null)
				{
					return ext.getScore().equals(getScore());
				}
			}
			return false; 
		} 



	}

	/*
	 * BigSquareRoot is being stuffed in here as an inner class to keep it tight with this high precision
	 * calculation class until we can remove the low precision code.
	 */
	


}
