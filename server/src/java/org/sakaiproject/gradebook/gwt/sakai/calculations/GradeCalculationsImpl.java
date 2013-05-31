package org.sakaiproject.gradebook.gwt.sakai.calculations;

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
import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.sakai.GradeCalculations;
import org.sakaiproject.gradebook.gwt.sakai.model.GradeItem;
import org.sakaiproject.gradebook.gwt.sakai.model.GradeStatistics;
import org.sakaiproject.gradebook.gwt.sakai.model.StudentScore;
import org.sakaiproject.gradebook.gwt.server.Util;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.AssignmentGradeRecord;
import org.sakaiproject.tool.gradebook.Category;
import org.sakaiproject.tool.gradebook.Gradebook;
import org.sakaiproject.component.api.ServerConfigurationService;

import net.sf.ehcache.Cache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.Element;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

public class GradeCalculationsImpl extends BigDecimalCalculationsWrapper implements GradeCalculations {

	private static final Log log = LogFactory.getLog(GradeCalculationsImpl.class);
	
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

	private  static final int PRECISION = 7;

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
	
	// cache settings
	protected Cache cache;
	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}
	
	private ServerConfigurationService configService = null;
	
	public ServerConfigurationService getConfigService() {
		return configService;
	}

	public void setConfigService(ServerConfigurationService configService) {
		this.configService = configService;
	}

	/*
	 * This is the letter grade map injected by Spring holding the static letter to percent mappings
	 */
	private Map<String, Double> letterGradeMap;
	
	private BigDecimalFactory bdf = new BigDecimalFactory();

	// Constructor
	public GradeCalculationsImpl() {
		super();
	}

	// Constructor
	public GradeCalculationsImpl(int scale) {
		super(scale);
	}


	// Spring IoC init
	public void init() {

		two = bdf.sameBigDecimal("2");
		three = bdf.sameBigDecimal("3");
		oneThird = divide(BigDecimal.ONE, three); 
		twoThirds = divide(two, three);

		gradeAplus = bdf.sameBigDecimal(add(bdf.sameBigDecimal("96"), twoThirds).toPlainString().substring(0, PRECISION));
		gradeA = bdf.sameBigDecimal(add(bdf.sameBigDecimal("93"), oneThird).toPlainString().substring(0, PRECISION));
		gradeAminus = bdf.sameBigDecimal("90");

		gradeBplus = bdf.sameBigDecimal(add(bdf.sameBigDecimal("86"), twoThirds).toPlainString().substring(0, PRECISION));
		gradeB = bdf.sameBigDecimal(add(bdf.sameBigDecimal("83"), oneThird).toPlainString().substring(0, PRECISION));
		gradeBminus = bdf.sameBigDecimal("80");

		gradeCplus = bdf.sameBigDecimal(add(bdf.sameBigDecimal("76"), twoThirds).toPlainString().substring(0, PRECISION));
		gradeC = bdf.sameBigDecimal(add(bdf.sameBigDecimal("73"), oneThird).toPlainString().substring(0, PRECISION));
		gradeCminus = bdf.sameBigDecimal("70");


		gradeDplus = bdf.sameBigDecimal(add(bdf.sameBigDecimal("66"), twoThirds).toPlainString().substring(0, PRECISION));
		gradeD = bdf.sameBigDecimal(add(bdf.sameBigDecimal("63"), oneThird).toPlainString().substring(0, PRECISION));
		gradeDminus = bdf.sameBigDecimal("60");

		gradeF = bdf.sameBigDecimal("60");

	}

	public Double calculateEqualWeight(int numberOfItems) {
		if (numberOfItems <= 1)
			return Double.valueOf(1d);

		BigDecimal result = divide(BigDecimal.ONE, bdf.sameBigDecimal(String.valueOf(numberOfItems)));

		return Double.valueOf(result.doubleValue());
	}

	public Double calculateItemWeightAsPercentage(Double requestedItemWeight, Double requestedItemPoints) {
		BigDecimal weight = null;

		// Obviously, if the user asks for a non-null value, give it to them
		if (requestedItemWeight != null)
			weight = bdf.sameBigDecimalToString(requestedItemWeight.doubleValue());
			//weight = bdf.sameBigDecimal(String.valueOf(requestedItemWeight.doubleValue()));
		else 
			weight = bdf.sameBigDecimalToString(requestedItemPoints.doubleValue());
		//weight = bdf.sameBigDecimal(String.valueOf(requestedItemPoints.doubleValue()));

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

		boolean isIncluded = Util.checkBoolean((a == null) ? Boolean.FALSE : a.getIncluded());

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
				courseGradePercent = calculateItemGradePercentDecimal(percentGrade, percentCategorySum, assignmentWeight);
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

	public BigDecimal calculateItemGradePercentDecimal(BigDecimal percentGrade, BigDecimal sumCategoryPercents, BigDecimal assignmentWeight) {

		if (percentGrade.compareTo(BigDecimal.ZERO) == 0 
				|| sumCategoryPercents.compareTo(BigDecimal.ZERO) == 0 
				|| assignmentWeight.compareTo(BigDecimal.ZERO) == 0)
			return BigDecimal.ZERO;

		BigDecimal categoryPercentRatio = null;

		categoryPercentRatio = sumCategoryPercents;

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
		if (gradeList == null || gradeList.isEmpty())
			return statistics;

		// general statistics
		statistics = calculateGeneralStatistics(statistics, gradeList, sum);

		// user rank statistics
		statistics = calculateUserRankStatistics(statistics, gradeList, rankStudentId);
		
		return statistics;
	}
	
	public GradeStatistics calculateStatistics(String context, List<StudentScore> gradeList, BigDecimal sum, String rankStudentId) {
		GradeStatistics statistics = new GradeStatistics();
		if (gradeList == null || gradeList.isEmpty())
			return statistics;
		
		// use cache
		String cacheKey = "gb2_calculateStatistics_" + context + "_" + gradeList.size() + "_" + sum.toString();
		if (cache != null)
		{	
			// deal with cache
			statistics = cacheCalculateGeneralStatistics(statistics, gradeList, sum, cacheKey);
		}
		else
		{
			statistics = calculateGeneralStatistics(statistics, gradeList, sum);
		}
		
		// user rank statistics
		statistics = calculateUserRankStatistics(statistics, gradeList, rankStudentId);
		
		return statistics;
	}

	private synchronized GradeStatistics cacheCalculateGeneralStatistics(GradeStatistics statistics, List<StudentScore> gradeList, BigDecimal sum, String cacheKey) {
		//check with cache first
		Element e = cache.get(cacheKey);
		if (e != null) {
			log.debug("calculateGeneralStatistics cache hit for:" + cacheKey);
			statistics = (GradeStatistics) e.getObjectValue();
		}
		else
		{
			// cache miss
			statistics = calculateGeneralStatistics(statistics, gradeList, sum);
			cache.put(new Element(cacheKey, statistics));
			log.debug("calculateGeneralStatistics cache miss for:" + cacheKey);
		}

		return statistics;
	}

	private GradeStatistics calculateGeneralStatistics(GradeStatistics statistics, List<StudentScore> gradeList, BigDecimal sum) {
		List<BigDecimal> modeList = null; 
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
	
				BigDecimal roundedCourseGrade = courseGrade.setScale(AppConstants.DISPLAY_SCALE, GradeCalculations.DISPLAY_ROUNDING);
				 
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
	
			if (count.compareTo(BigDecimal.ZERO) != 0) {
				variance = divide(sumOfSquareOfDifferences, count);
				if (variance != null)
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
						median = divide(s, bdf.sameBigDecimal("2"));
				} else {
					// If we have an odd number of elements, simply choose the middle one
					median = gradeList.get(middle).getScore();
				}
			}
		}
	
		//scale and round the output...
		// here at the end of the process, we are using half_up
	
		statistics.setStudentTotal(gradeList.size()); 
		statistics.setMean(null != mean ? mean.setScale(getScale(), RoundingMode.HALF_UP) : null);
		statistics.setMedian(median);
		statistics.setModeList(modeList);
		statistics.setStandardDeviation(null != standardDeviation ? standardDeviation.setScale(getScale(), RoundingMode.HALF_UP) : null);
		return statistics;
	}
	
	/**
	 * calculate the user rank and insert into statistics object
	 * @param statistics
	 * @param gradeList
	 * @param rankStudentId
	 */
	private GradeStatistics calculateUserRankStatistics(GradeStatistics statistics, List<StudentScore> gradeList, String rankStudentId) {
		if (rankStudentId != null && gradeList != null && gradeList.size() > 0)
		{
			Collections.sort(gradeList);
			Iterator<StudentScore> it = gradeList.iterator();
			StudentScore targ = null; 
			while (it.hasNext())
			{
				StudentScore s = it.next(); 
				//log.debug("rankStudentId: " + rankStudentId);
				//log.debug("getUserUid(): " + s.getUserUid());
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
		
		return statistics;
	}

	public String convertPercentageToLetterGrade(BigDecimal percentage) {

		if (percentage != null) {

			percentage = bdf.sameBigDecimal(percentage.toPlainString(), new MathContext(6, RoundingMode.HALF_UP));

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
			//categoryWeight = bdf.sameBigDecimal(category.getWeight().toString());
			categoryWeight = bdf.sameBigDecimalToString(category.getWeight());
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
		BigDecimal rv = null;
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
		GradebookCalculationUnit gradebookUnit = new GradebookCalculationUnitImpl(getScale());

		return gradebookUnit.calculatePointsBasedCourseGrade(gradeRecordUnits, totalGradebookPoints, isExtraCreditScaled);
	}

	@SuppressWarnings("unchecked")
	private BigDecimal getCategoriesCourseGrade(Collection<Category> categoriesWithAssignments, Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap,
			boolean isWeighted, boolean isExtraCreditScaled) {

		if (categoriesWithAssignments == null && assignmentGradeRecordMap != null) 
			categoriesWithAssignments = generateCategoriesWithAssignments(assignmentGradeRecordMap);

		if (categoriesWithAssignments == null)
			return null;

		Map<String, CategoryCalculationUnit> categoryUnitMap = new HashMap<String, CategoryCalculationUnit>();

		Map<String, List<GradeRecordCalculationUnit>> categoryGradeUnitListMap = new HashMap<String, List<GradeRecordCalculationUnit>>();
		Map<String, Boolean> hasCategoryManuallyEqualWeightedAssignmentsMap = new HashMap<String, Boolean>();
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
				CategoryCalculationUnit categoryCalculationUnit = new CategoryCalculationUnitImpl(categoryWeight, Integer.valueOf(category.getDrop_lowest()), category.isExtraCredit(), category.isEnforcePointWeighting(), category.isEqualWeightAssignments(), getScale());
				categoryUnitMap.put(categoryKey, categoryCalculationUnit);

				List<GradeRecordCalculationUnit> gradeRecordUnits = new ArrayList<GradeRecordCalculationUnit>();

				List<Assignment> assignments = category.getAssignmentList();
				if (assignments == null)
					continue;

				boolean isExtraCreditCategory = isExtraCredit(category);
				BigDecimal totalCategoryPoints = populateGradeRecordUnits(category, categoryCalculationUnit, assignments, gradeRecordUnits, assignmentGradeRecordMap, isWeighted, isExtraCreditCategory)[1];

				if (isExtraCreditCategory) {
				
					totalCategoryPoints = BigDecimal.ZERO;
				}

				totalGradebookPoints = add(totalGradebookPoints, totalCategoryPoints);

				categoryGradeUnitListMap.put(categoryKey, gradeRecordUnits);
				if(isWeighted && category.isExtraCredit()) {
				
					hasCategoryManuallyEqualWeightedAssignmentsMap.put(categoryKey, hasEqualWeights(assignments));
				}
				
			} // for
		}

		GradebookCalculationUnit gradebookUnit = new GradebookCalculationUnitImpl(categoryUnitMap, getScale());

		if (isWeighted) {
		
			return gradebookUnit.calculateWeightedCourseGrade(categoryGradeUnitListMap, hasCategoryManuallyEqualWeightedAssignmentsMap, totalGradebookPoints, isExtraCreditScaled);
		}

		return gradebookUnit.calculatePointsBasedCourseGrade(categoryGradeUnitListMap, totalGradebookPoints, isExtraCreditScaled);
	}

	private BigDecimal[] populateGradeRecordUnits(
			Category category,
			CategoryCalculationUnit categoryCalculationUnit, 
			Collection<Assignment> assignments,
			List<GradeRecordCalculationUnit> gradeRecordUnits,
			Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap,
			boolean isWeighted,
			boolean isExtraCreditCategory) {

		BigDecimal totalCategoryPoints = BigDecimal.ZERO;
		BigDecimal totalCategoryPercent = BigDecimal.ZERO;
		BigDecimal myTotalCategoryPercent = BigDecimal.ZERO;
		int totalUnexcusedItems = 0; 
		boolean isWeightByPointsCategory = category == null ? false : Util.checkBoolean(category.isEnforcePointWeighting());
		boolean doPreventUnequalDropLowest = ((isWeighted && isWeightByPointsCategory) || !isWeighted);

		Double lastPointValue = null;
		BigDecimal lastPercentValue = BigDecimal.ZERO;

		int dropLowest = category == null ? 0 : category.getDrop_lowest();
		// GRBK-989, if the category is extra credit, then don't allow drop lowest
		if (isExtraCreditCategory) 
			dropLowest = 0;
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

		int numberOfItemsNotCounted = 0;
		if (assignments != null) {
			
			for (Assignment assignment : assignments) {
				
				if (assignment.isNotCounted())
					numberOfItemsNotCounted++;

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
						totalCategoryPercent = add(totalCategoryPercent, multiply(assignmentWeight, BIG_DECIMAL_100));
						myTotalCategoryPercent = add(myTotalCategoryPercent, multiply(assignmentWeight, BIG_DECIMAL_100));
					}

				}

				if (gradeRecordUnits != null) {
					AssignmentGradeRecord assignmentGradeRecord = assignmentGradeRecordMap == null ? null : assignmentGradeRecordMap.get(assignment.getId());

					boolean isGraded = isGraded(assignmentGradeRecord);

					if (isNullsAsZeros || isGraded) {
						// Make sure it's not excused
						if (!isExcused(assignmentGradeRecord)) {

							//BigDecimal pointsEarned = !isGraded ? BigDecimal.ZERO : bdf.sameBigDecimal(assignmentGradeRecord.getPointsEarned().toString());
							BigDecimal pointsEarned = !isGraded ? BigDecimal.ZERO : bdf.sameBigDecimalToString(assignmentGradeRecord.getPointsEarned());
							//BigDecimal pointsPossible = bdf.sameBigDecimal(assignment.getPointsPossible().toString());
							BigDecimal pointsPossible = bdf.sameBigDecimalToString(assignment.getPointsPossible());

							Boolean isExtraCredit = Boolean.valueOf(isExtraCreditItemOrCategory);

							GradeRecordCalculationUnit gradeRecordUnit = new GradeRecordCalculationUnitImpl(pointsEarned, 
									pointsPossible, assignmentWeight, isExtraCredit, getScale()) {

								@Override
								public void setDropped(boolean isDropped) {
									super.setDropped(isDropped);

									AssignmentGradeRecord gradeRecord = (AssignmentGradeRecord)getActualRecord();

									//GRBK-680 - 'Give Ungraded No Credit' - Zeros given in scores not marked when dropped
									if (gradeRecord != null)
										gradeRecord.setDropped(Boolean.valueOf(isDropped));
								}

							};

							gradeRecordUnit.setActualRecord(assignmentGradeRecord);
							// GRBK-784 - we need a count of items in this thing. 
							totalUnexcusedItems++; 
							gradeRecordUnits.add(gradeRecordUnit);
						} else {
							
							// GRBK-1345
							if(null != assignmentWeight) {
							
								// GRBK 483 - we deduct this assignment weight
							
								myTotalCategoryPercent = subtract(myTotalCategoryPercent, multiply(assignmentWeight, BIG_DECIMAL_100));
							}
						}
					}
					else						
					{						
						// GRBK-1354 - added assignmentWeight null check
						// GRBK 483 - we deduct this assignment weight
						if (!isExtraCreditItem && null != assignmentWeight) {
						
							myTotalCategoryPercent = subtract(myTotalCategoryPercent, multiply(assignmentWeight, BIG_DECIMAL_100));
						}
						
						// GRBK-784 - we need a count of items in this thing. 
						if (!isExcused(assignmentGradeRecord))
						{
							totalUnexcusedItems++; 
						}
						
					}
				}
			}
		}

		if (categoryCalculationUnit != null && totalUnexcusedItems > 0 && isExtraCreditCategory)
		{
			categoryCalculationUnit.setTotalNumberOfItems(totalUnexcusedItems);
		}
		// When we get here we can assume that if drop lowest is greater than 0, it means the points are equal for
		// all items

		int numberOfItems = assignments == null ? 0 : assignments.size();

		if (dropLowest > 0 && totalCategoryPoints != null) {
			if (dropLowest > numberOfItems - numberOfItemsNotCounted) 
				dropLowest = numberOfItems - numberOfItemsNotCounted;

			BigDecimal representativePointsPossible = lastPointValue == null ? BigDecimal.ZERO : BigDecimal.valueOf(lastPointValue.doubleValue());
			totalCategoryPoints = subtract(totalCategoryPoints, multiply(BigDecimal.valueOf(dropLowest), representativePointsPossible));
			if (totalCategoryPercent != null && lastPercentValue != null)
				totalCategoryPercent = subtract(totalCategoryPercent, multiply(BigDecimal.valueOf(dropLowest), multiply(lastPercentValue, BIG_DECIMAL_100)));
			if (myTotalCategoryPercent != null && lastPercentValue != null)
				myTotalCategoryPercent = subtract(myTotalCategoryPercent, multiply(BigDecimal.valueOf(dropLowest), multiply(lastPercentValue, BIG_DECIMAL_100)));
		}

		if (categoryCalculationUnit != null)
			categoryCalculationUnit.setTotalCategoryPoints(totalCategoryPoints);

		BigDecimal[] result = new BigDecimal[2];
		result[0] = totalCategoryPercent;
		result[1] = totalCategoryPoints;

		// GRBK-483. Using the myTotalCategoryPercent and totalCategoryPoints just calculated to set the earnedPercentage within category.
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
								
				if (gradeRecordUnits != null) {
					AssignmentGradeRecord assignmentGradeRecord = assignmentGradeRecordMap == null ? null : assignmentGradeRecordMap.get(assignment.getId());
		 		
					boolean isGraded = isGraded(assignmentGradeRecord);
					
					if (isNullsAsZeros || isGraded) {

						//BigDecimal pointsEarned = !isGraded ? BigDecimal.ZERO : bdf.sameBigDecimal(assignmentGradeRecord.getPointsEarned().toString());
						BigDecimal pointsEarned = !isGraded ? BigDecimal.ZERO : bdf.sameBigDecimalToString(assignmentGradeRecord.getPointsEarned());
						//BigDecimal pointsPossible = bdf.sameBigDecimal(assignment.getPointsPossible().toString());
						BigDecimal pointsPossible = bdf.sameBigDecimalToString(assignment.getPointsPossible());
						
						Boolean isExtraCredit = Boolean.valueOf(isExtraCreditItemOrCategory);
						GradeRecordCalculationUnit gradeRecordUnit = new GradeRecordCalculationUnitImpl(pointsEarned, pointsPossible, assignmentWeight, isExtraCredit, getScale());
	
						if (assignmentGradeRecord != null && assignmentGradeRecord.getPointsEarned() != null) {
							BigDecimal earnedPercentWithinCategory = null;
							if (isWeightByPointsCategory) {

								// GBRK-869 : prevent division by zero
								if(BigDecimal.ZERO.compareTo(totalCategoryPoints) == 0) {
									
									earnedPercentWithinCategory = BigDecimal.ZERO;
								}
								else {
								
									earnedPercentWithinCategory = divide(pointsEarned,totalCategoryPoints);
								}
								
							} else {
								
								// GRBK-869 : prevent division by zero
								if(BigDecimal.ZERO.compareTo(myTotalCategoryPercent) == 0  || isExtraCreditItem) {
									
									earnedPercentWithinCategory = gradeRecordUnit.getPercentageScore();
								}
								else {
									
									earnedPercentWithinCategory = multiply(BIG_DECIMAL_100, multiply(gradeRecordUnit.getPercentageScore(), divide(assignmentWeight, myTotalCategoryPercent)));
								}
							}
							assignmentGradeRecord.setEarnedWeightedPercentage(earnedPercentWithinCategory);
						}							
					}
				}
			}
		}
		
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
		// GRBK-989, if the category is extra credit, then don't allow drop lowest
		if (isExtraCreditCategory) 
			dropLowest = 0;
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
						totalCategoryPercent = add(totalCategoryPercent, assignmentWeight);
					}
				}


				if (gradeRecordUnits != null) {
					AssignmentGradeRecord assignmentGradeRecord = assignmentGradeRecordMap == null ? null : assignmentGradeRecordMap.get(assignment.getItemId());

					boolean isGraded = isGraded(assignmentGradeRecord);

					if (isNullsAsZeros || isGraded) {
						// Make sure it's not excused
						if (!isExcused(assignmentGradeRecord)) {

							//BigDecimal pointsEarned = !isGraded ? BigDecimal.ZERO : bdf.sameBigDecimal(assignmentGradeRecord.getPointsEarned().toString());
							BigDecimal pointsEarned = !isGraded ? BigDecimal.ZERO : bdf.sameBigDecimalToString(assignmentGradeRecord.getPointsEarned());
							//BigDecimal pointsPossible = bdf.sameBigDecimal(assignment.getPoints().toString());
							BigDecimal pointsPossible = bdf.sameBigDecimalToString(assignment.getPoints());

							Boolean isExtraCredit = Boolean.valueOf(isExtraCreditItemOrCategory);

							GradeRecordCalculationUnit gradeRecordUnit = new GradeRecordCalculationUnitImpl(pointsEarned, 
									pointsPossible, assignmentWeight, isExtraCredit, getScale()) {

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

		//BigDecimal max = bdf.sameBigDecimal(maxPointValue.toString());
		BigDecimal max = bdf.sameBigDecimalToString(maxPointValue);
		//BigDecimal maxStart = bdf.sameBigDecimal(maxPointStartValue.toString());
		BigDecimal maxStart = bdf.sameBigDecimalToString(maxPointStartValue);
		BigDecimal ratio = BigDecimal.ZERO; 
		if (maxStart.compareTo(BigDecimal.ZERO) != 0)
		{
			ratio = divide(max, maxStart);
		}
		//BigDecimal points = bdf.sameBigDecimal(pointValue.toString());
		BigDecimal points = bdf.sameBigDecimalToString(pointValue);

		return multiply(points, ratio);
	}

	public BigDecimal getPercentAsPointsEarned(Assignment assignment, Double percentage) {
		BigDecimal pointsEarned = null;

		if (percentage != null) {
			//BigDecimal percent = bdf.sameBigDecimal(percentage.toString());
			BigDecimal percent = bdf.sameBigDecimalToString(percentage);
			//BigDecimal maxPoints = bdf.sameBigDecimal(assignment.getPointsPossible().toString());
			BigDecimal maxPoints = bdf.sameBigDecimalToString(assignment.getPointsPossible());
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

		//pointsEarned = bdf.sameBigDecimal(assignmentGradeRecord.getPointsEarned().toString());
		pointsEarned = bdf.sameBigDecimalToString(assignmentGradeRecord.getPointsEarned());
		if (assignment.getPointsPossible() != null) {
			//pointsPossible = bdf.sameBigDecimal(assignment.getPointsPossible().toString());
			pointsPossible = bdf.sameBigDecimalToString(assignment.getPointsPossible());
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

	//GRBK-1201
	public boolean hasAssignmentWeight(Assignment assignment) {

		// If the assignment doesn't exist or has no weight then we return false
		if (assignment != null && assignment.getAssignmentWeighting() != null)
			return true;
		else
			return false;
	}
	
	
	protected Double calculateEquivalentPointValueForPercent(Double doublePointsPossible, Double doublePercentEarned) {
		
		if (doublePointsPossible == null || doublePercentEarned == null)
			return null;

//		BigDecimal pointsPossible = bdf.sameBigDecimal(doublePointsPossible.toString());
		BigDecimal pointsPossible = bdf.sameBigDecimalToString(doublePointsPossible);
		BigDecimal percentEarned = bdf.sameBigDecimalToString(doublePercentEarned);
//		BigDecimal percentEarned = bdf.sameBigDecimal(doublePercentEarned.toString());
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

		BigDecimal assignmentWeight = BigDecimal.ZERO;

		// If the assignment doesn't exist or has no weight then we return null
		if (null == assignment || isDeleted(assignment)) 
			return null;

		Gradebook gradebook = assignment.getGradebook();

		switch (gradebook.getCategory_type()) {
		case GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY:
			if (null == assignment.getAssignmentWeighting() || isUnweighted(assignment)) 
				return null;
			//assignmentWeight = bdf.sameBigDecimal(assignment.getAssignmentWeighting().toString());
			assignmentWeight = bdf.sameBigDecimalToString(assignment.getAssignmentWeighting());
			break;
		default:
			if (null == assignment.getPointsPossible())
				return null;

			//assignmentWeight = bdf.sameBigDecimal(assignment.getPointsPossible().toString());
			assignmentWeight = bdf.sameBigDecimalToString(assignment.getPointsPossible());
			break;
		}

		return assignmentWeight;
	}

	private BigDecimal getAssignmentWeight(GradeItem assignment, CategoryType categoryType) {

		BigDecimal assignmentWeight = BigDecimal.ZERO;

		// If the assignment doesn't exist or has no weight then we return null
		if (null == assignment || Util.checkBoolean(assignment.getRemoved())) 
			return null;

		switch (categoryType) {
		case WEIGHTED_CATEGORIES:
			if (null == assignment.getWeighting() || !Util.checkBoolean(assignment.getIncluded())) 
				return null;
			//assignmentWeight = bdf.sameBigDecimal(assignment.getWeighting().toString());
			assignmentWeight = bdf.sameBigDecimalToString(assignment.getWeighting());
			break;
		default:
			if (null == assignment.getPoints())
				return null;

			//assignmentWeight = bdf.sameBigDecimal(assignment.getPoints().toString());
			assignmentWeight = bdf.sameBigDecimalToString(assignment.getPoints());
			break;
		}

		return assignmentWeight;
	}

	// GRBK-483: calc and return earnedWeightedPercentag when setting Map by appendItemData 
	public BigDecimal getEarnedWeightedPercentage(Assignment assignment, AssignmentGradeRecord assignmentGradeRecord) {
		BigDecimal earnedWeightedPercentage = null;
		if (!isBlank(assignment, assignmentGradeRecord)) {
			BigDecimal categoryweight = getCategoryWeight(assignment.getCategory());
			BigDecimal percentageEarned = assignmentGradeRecord.getEarnedWeightedPercentage();
			if (percentageEarned != null && categoryweight != null) 
				earnedWeightedPercentage = multiply(BIG_DECIMAL_100, multiply(percentageEarned, categoryweight));			
		}
		return earnedWeightedPercentage;
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

	private boolean isExtraCredit(Category category) {
		return category.isExtraCredit() == null ? false : category.isExtraCredit().booleanValue();
	}

	private boolean isGraded(AssignmentGradeRecord assignmentGradeRecord) {
		return assignmentGradeRecord != null && assignmentGradeRecord.getPointsEarned() != null;
	}

	private boolean isExcused(AssignmentGradeRecord assignmentGradeRecord) {
		if (assignmentGradeRecord == null)
			return false;

		return assignmentGradeRecord.isExcludedFromGrade() == null ? false : assignmentGradeRecord.isExcludedFromGrade().booleanValue();
	}

	private boolean isUnweighted(Assignment assignment) {
		return assignment.isNotCounted();
	}

	private boolean isUnweighted(Category category) {
		return category.isUnweighted() == null ? false : category.isUnweighted().booleanValue();
	}
	
	// GRBK-1255
	private Boolean hasEqualWeights(List<Assignment> assignments) {
	
		if(null == assignments || assignments.size() < 2) {

			return Boolean.FALSE;
		}
		
		Double weight = null;
		
		for(Assignment assignment : assignments) {
			
			if(assignment.isNotCounted()) {

				continue;
			}
			else if(null == assignment.getAssignmentWeighting()) {
				
				/*
				 * In case of a NULL assignment weight, we set the compare weight to -1.
				 * This will handles cases where we have NULL and non NULL assignment weights
				 */
				weight = Double.valueOf(-1);
			}
			else if(null == weight) {
				
				weight = assignment.getAssignmentWeighting();
			}
			else if(weight.compareTo(assignment.getAssignmentWeighting()) != 0) {

				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;
	}

	/*
	 * Spring IoC setter method
	 */
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


}
