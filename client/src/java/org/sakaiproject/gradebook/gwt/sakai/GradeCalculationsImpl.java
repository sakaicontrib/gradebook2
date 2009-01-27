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
package org.sakaiproject.gradebook.gwt.sakai;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.AssignmentGradeRecord;
import org.sakaiproject.tool.gradebook.Category;
import org.sakaiproject.tool.gradebook.Gradebook;

public class GradeCalculationsImpl implements GradeCalculations {
	
	// Class Members
	final static BigDecimal BIG_DECIMAL_100 = new BigDecimal("100.0");
	public static final MathContext MATH_CONTEXT = new MathContext(10, RoundingMode.HALF_DOWN);
	private static final Log log = LogFactory.getLog(GradeCalculationsImpl.class);
	
	// Default constructor
	public GradeCalculationsImpl() { }
	
	public BigDecimal getNewPointsGrade(Double pointValue, Double maxPointValue, Double maxPointStartValue) {
		
		BigDecimal max = new BigDecimal(maxPointValue.toString());
		BigDecimal maxStart = new BigDecimal(maxPointStartValue.toString());
		BigDecimal ratio = max.divide(maxStart, MATH_CONTEXT);
		BigDecimal points = new BigDecimal(pointValue.toString());
		
		return points.multiply(ratio, MATH_CONTEXT);
	}
	
	public BigDecimal getPercentAsPointsEarned(Assignment assignment, Double percentage) {
		
		BigDecimal pointsEarned = null;
		
		if (percentage != null) {
			BigDecimal percent = new BigDecimal(percentage.toString());
			BigDecimal maxPoints = new BigDecimal(assignment.getPointsPossible().toString());
			pointsEarned = percent.divide(BIG_DECIMAL_100, MATH_CONTEXT).multiply(maxPoints);
		}
		
		return pointsEarned;
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
	
	private boolean isNormalCredit(Assignment assignment) {
		boolean isExtraCredit = isExtraCredit(assignment);
		return assignment.isCounted() && !assignment.isRemoved() && !isExtraCredit;
	}
	
	private boolean isUnweighted(Assignment assignment) {
		return assignment.isUnweighted() == null ? false : assignment.isUnweighted().booleanValue();
	}
	
	// We assume that a null assignmentGradeRecord or a null points earned means that this assignment is not yet graded
	// this is the same behavior as when a given assignment grade record has been excluded from the calculations
	private boolean isGraded(AssignmentGradeRecord assignmentGradeRecord) {
		return assignmentGradeRecord != null && assignmentGradeRecord.getPointsEarned() != null;
	}
	
	private boolean isDropped(AssignmentGradeRecord assignmentGradeRecord) {
		return assignmentGradeRecord != null && assignmentGradeRecord.isDropped() != null && assignmentGradeRecord.isDropped().booleanValue();
	}
	
	private boolean isExcused(AssignmentGradeRecord assignmentGradeRecord) {
		return assignmentGradeRecord.isExcluded() == null ? false : assignmentGradeRecord.isExcluded().booleanValue();
	}
	
	// API IMPL Section
	public BigDecimal getPointsEarnedAsPercent(Assignment assignment, AssignmentGradeRecord assignmentGradeRecord) {

		BigDecimal percentageEarned = null;
		BigDecimal pointsEarned = null;
		BigDecimal pointsPossible = null;

		if (isBlank(assignment, assignmentGradeRecord)) {
			return percentageEarned;
		}
		
		pointsEarned = new BigDecimal(assignmentGradeRecord.getPointsEarned().toString());
		pointsPossible = new BigDecimal(assignment.getPointsPossible().toString());
		percentageEarned = pointsEarned.multiply(BIG_DECIMAL_100).divide(pointsPossible, MATH_CONTEXT);

		return percentageEarned;
	}

	// TPA : Complies with spreadsheet calculations
	public BigDecimal getEarnedWeightedPercentage(Assignment assignment, BigDecimal pointsEarnedAsPercent, Boolean enableAssignmentConstraints) {
		
		BigDecimal assignmentWeight = getAssignmentWeight(assignment);
		
		if (null == assignment || isDeleted(assignment) || null == pointsEarnedAsPercent || null == assignmentWeight) {
			return null;
		}
		
		BigDecimal weightedPercentageEarned = assignmentWeight.multiply(pointsEarnedAsPercent); //.divide(BIG_DECIMAL_100, MATH_CONTEXT);
				
		if (!enableAssignmentConstraints.booleanValue() || isNormalCredit(assignment)) {
			return weightedPercentageEarned;
		}
		
		return BigDecimal.ZERO;
	}
	
	public BigDecimal sumEarnedWeightedPercentages(Category categoryWithAssignments, Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap) {
		 
		BigDecimal sumAssignmentsEarnedWeightedPercentage = null;

		if (isDeleted(categoryWithAssignments))
			return null;
		
		List<Assignment> assignments = categoryWithAssignments.getAssignmentList();
		List<AssignmentGradeRecord> agrs = new ArrayList<AssignmentGradeRecord>();
		
		if (assignments != null && !assignments.isEmpty()) {
			// Only used to calculate overall weight
			BigDecimal categoryWeight = getCategoryWeight(categoryWithAssignments);
			// Loop over all assignments
			for (Assignment assignment : assignments) {
				// For a normal credit assignment
				if (!isDeleted(assignment) && !isExtraCredit(assignment)) {
					// Only used to calculate overall weight
					BigDecimal assignmentWeight = getAssignmentWeight(assignment);
					// Find the AssignmentGradeRecord for this assignment
					AssignmentGradeRecord assignmentGradeRecord = assignmentGradeRecordMap.get(assignment.getId());
					// Make sure it has a grade 			
					if (isGraded(assignmentGradeRecord)) {
						// Make sure it's not excused
						if (!isExcused(assignmentGradeRecord)) {
							BigDecimal pointsEarnedAsPercent = getPointsEarnedAsPercent(assignment, assignmentGradeRecord);
							BigDecimal earnedWeightedPercentage = getEarnedWeightedPercentage(assignment, pointsEarnedAsPercent, Boolean.TRUE);
							if (earnedWeightedPercentage != null) {
								assignmentGradeRecord.setEarnedWeightedPercentage(earnedWeightedPercentage);
								assignmentGradeRecord.setDropped(Boolean.FALSE);
								BigDecimal overallWeight = null;
								if (categoryWeight != null && assignmentWeight != null)
									overallWeight = categoryWeight.multiply(assignmentWeight);
								assignmentGradeRecord.setOverallWeight(overallWeight);
								agrs.add(assignmentGradeRecord);
							}
						} // if
					} // if 		
				} // if 
			} // for 
		} // if
		
		// Check to see if we need to drop the lowest N assignments for a given user
		int nDropLowest = categoryWithAssignments.getDrop_lowest();
		int sizeOf = agrs.size();
		
		// We cannot drop lowest when we don't have categories
		Gradebook gradebook = categoryWithAssignments.getGradebook();
		if (gradebook.getCategory_type() == GradebookService.CATEGORY_TYPE_NO_CATEGORY)
			nDropLowest = 0;
		
		List<AssignmentGradeRecord> subList = agrs;
		
		if (nDropLowest != 0 && sizeOf >= nDropLowest) {
			Collections.sort(agrs, new Comparator<AssignmentGradeRecord>() {

				public int compare(AssignmentGradeRecord o1, AssignmentGradeRecord o2) {
					return o1.getEarnedWeightedPercentage().compareTo(o2.getEarnedWeightedPercentage());
				}
			
			});
			subList = agrs.subList(nDropLowest, sizeOf);
			
			List<AssignmentGradeRecord> excludedSubList = agrs.subList(0, nDropLowest);
			
			for (AssignmentGradeRecord excluded : excludedSubList) {
				excluded.setDropped(Boolean.TRUE);
				excluded.setOverallWeight(BigDecimal.ZERO);
			}
		}
		
		for (AssignmentGradeRecord agr : subList) {
			if (!isDropped(agr) && agr.getEarnedWeightedPercentage() != null) {
				if (sumAssignmentsEarnedWeightedPercentage == null)
					sumAssignmentsEarnedWeightedPercentage = BigDecimal.ZERO;
				sumAssignmentsEarnedWeightedPercentage = sumAssignmentsEarnedWeightedPercentage.add(agr.getEarnedWeightedPercentage());
			} // if 
		} // for
		
		return sumAssignmentsEarnedWeightedPercentage;
	}

	
	public BigDecimal sumAssignmentWeights(Category categoryWithAssignments, Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap) {

		BigDecimal sumAssignmentWeight = BigDecimal.ZERO;
		
		// Only sum assignment weights for normal credit categories in this method
		//if (!isExtraCredit(categoryWithAssignments)) {
		
			// Grab all of the assignments
			List<Assignment> assignments = categoryWithAssignments.getAssignmentList();
			
			// Make sure there's something there
			if (assignments != null && !assignments.isEmpty()) {
				// Loop over all assignments
				for (Assignment assignment : assignments) {
		
					// Only normal credit assignments should be processed here
					if (!isDeleted(assignment) && !isExtraCredit(assignment)) {
						// Make sure that there exists an AssignmentGradeRecord before we add the weight
						AssignmentGradeRecord assignmentGradeRecord = assignmentGradeRecordMap.get(assignment.getId());
			
						BigDecimal assignmentWeight = getAssignmentWeight(assignment);
						
						if (assignmentGradeRecord != null && assignmentWeight != null && assignmentGradeRecord.getPointsEarned() != null) {
							
							// Adjusted logic to take care of extra credit categories 
							if (!isExcused(assignmentGradeRecord) && !isDropped(assignmentGradeRecord)) {
								sumAssignmentWeight = sumAssignmentWeight.add(assignmentWeight);
							} // if	
						} // if
					} // if 
				} // for
			} // if 
		//} // if 
		
		return sumAssignmentWeight;
	}
	
	public BigDecimal getAssignmentWeight(Assignment assignment) {
		
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
	
	
	public BigDecimal getCategoryWeight(Category category) {
		
		BigDecimal categoryWeight = null;
			
		if (null == category || isDeleted(category)) {
			return null;
		}
		
		Gradebook gradebook = category.getGradebook();
		
		switch (gradebook.getCategory_type()) {
		case GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY:
			categoryWeight = new BigDecimal(category.getWeight().toString());
			break;
		default:
			categoryWeight = BigDecimal.ZERO;
			
			List<Assignment> assignments = (List<Assignment>)category.getAssignmentList();
			if (assignments != null) {
				for (Assignment assignment : assignments) {
					BigDecimal assignmentWeight = getAssignmentWeight(assignment);
					
					if (assignmentWeight != null)
						categoryWeight = categoryWeight.add(assignmentWeight);
				}
			}
			break;
		}
		
		return categoryWeight;
	}


	
	public BigDecimal sumExtraCreditEarnedWeightedPercentage(Category categoryWithAssignments, Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap) {

		if (isDeleted(categoryWithAssignments))
			return null;
		
		BigDecimal sumAssignmentsExtraCreditEarnedWeightedPercentage = null;
		
		List<Assignment> assignments = categoryWithAssignments.getAssignmentList();
		
		if (assignments != null && !assignments.isEmpty()) {
			// Loop over all assignments
			for(Assignment assignment : assignments) {
				
				// Don't sum deleted or null assignments, only ones that are extra credit
				if (assignment != null && !isDeleted(assignment) && isExtraCredit(assignment)) {
					// Find the AssignmentGradeRecord for this assignment
					AssignmentGradeRecord assignmentGradeRecord = assignmentGradeRecordMap.get(assignment.getId());
		
					// Make sure we actually have a grade here
					if (isGraded(assignmentGradeRecord)) {

						// Don't sum assignments that have been excused
						if (!isExcused(assignmentGradeRecord)) {
							// Ensure that extra credit records are not dropped
							if (assignmentGradeRecord != null)
								assignmentGradeRecord.setDropped(Boolean.FALSE);
							
							BigDecimal pointsEarnedAsPercent = getPointsEarnedAsPercent(assignment, assignmentGradeRecord);
							BigDecimal earnedWeightedPercentage = getEarnedWeightedPercentage(assignment, pointsEarnedAsPercent, Boolean.FALSE);
							// Ensure we didn't get a null back
							if (earnedWeightedPercentage != null) {
								if (sumAssignmentsExtraCreditEarnedWeightedPercentage == null)
									sumAssignmentsExtraCreditEarnedWeightedPercentage = BigDecimal.ZERO;
								sumAssignmentsExtraCreditEarnedWeightedPercentage = sumAssignmentsExtraCreditEarnedWeightedPercentage.add(earnedWeightedPercentage);
							} // if 
						} // if 
					} // if 
				} // if 
			} // for 
		} // if 
		
		return sumAssignmentsExtraCreditEarnedWeightedPercentage;
	}	

	
	public BigDecimal getCategoryGrade(Category categoryWithAssignments,
			   Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap) {
		
		// First we check if category has a ZERO weight
		BigDecimal categoryWeight = getCategoryWeight(categoryWithAssignments);
		
		// A null category weight is a deleted or null category
		if (null == categoryWeight)
			return null;
		
		// A zero category weight is one that will never produce a non-zero grade
		if (BigDecimal.ZERO.compareTo(categoryWeight) == 0) {
			return BigDecimal.ZERO;
		}
		
		BigDecimal earnedWeightedPercentage = sumEarnedWeightedPercentages(categoryWithAssignments, assignmentGradeRecordMap);
			
		BigDecimal earnedWeightedPercentageExtraCredit = sumExtraCreditEarnedWeightedPercentage(categoryWithAssignments, assignmentGradeRecordMap);
		
		BigDecimal assignmentWeights = sumAssignmentWeights(categoryWithAssignments, assignmentGradeRecordMap);
		
		// If earned weighted percentage is null then the category has been removed or is unweighted
		if (earnedWeightedPercentage == null)
			return earnedWeightedPercentageExtraCredit;
		
		// In the case where our earned weighted percentage is zero and we have some extra credit, just return
		// the extra credit. 
		if (earnedWeightedPercentageExtraCredit != null && earnedWeightedPercentage.compareTo(BigDecimal.ZERO) == 0)
			return earnedWeightedPercentageExtraCredit;
		
		// Of course, if there's no extra credit, and the earned weighted percentage is zero, then we want to return zero
		// and not null, which would be misleading
		if (earnedWeightedPercentage.compareTo(BigDecimal.ZERO) == 0)
			return BigDecimal.ZERO;
		
		if (assignmentWeights.compareTo(BigDecimal.ZERO) == 0)
			return null;
		
		BigDecimal assignmentSum = earnedWeightedPercentage.divide(assignmentWeights, MATH_CONTEXT);
		
		if (earnedWeightedPercentageExtraCredit != null)
			assignmentSum = assignmentSum.add(earnedWeightedPercentageExtraCredit);
		
		return assignmentSum;
	}
	
	
	public BigDecimal getCourseGrade(Collection<Category> categoriesWithAssignments, Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap) {

		BigDecimal courseGrade = null;
		
		BigDecimal categoryWeightSum = BigDecimal.ZERO;
		BigDecimal extraCreditSum = BigDecimal.ZERO;
		BigDecimal extraCredit = BigDecimal.ZERO;
		
		if (categoriesWithAssignments != null && !categoriesWithAssignments.isEmpty() && assignmentGradeRecordMap != null) {
		
			Gradebook gradebook = categoriesWithAssignments.iterator().next().getGradebook();
			
			switch (gradebook.getCategory_type()) {
			case GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY:
				for (Category category : categoriesWithAssignments) {
					BigDecimal categoryGrade = getCategoryGrade(category, assignmentGradeRecordMap);
					BigDecimal categoryWeight = getCategoryWeight(category);
					
					if (categoryGrade != null) {
						BigDecimal weightedGrade = categoryGrade.multiply(categoryWeight);
						
						boolean isExtraCredit = category.isExtraCredit() == null ? false : category.isExtraCredit().booleanValue();
						if (isExtraCredit) {
							extraCreditSum = extraCreditSum.add(categoryWeight);
							extraCredit = extraCredit.add(weightedGrade);
						} else {
							categoryWeightSum = categoryWeightSum.add(categoryWeight);
							if (courseGrade == null)
								courseGrade = BigDecimal.ZERO;
							courseGrade = courseGrade.add(weightedGrade);
						} // else
					} // if 
				} // for 
				
				if (categoryWeightSum.compareTo(BigDecimal.ZERO) == 0) {
					return null;
				} else if (categoryWeightSum.compareTo(BigDecimal.ONE) < 0) {
					if (courseGrade == null)
						courseGrade = BigDecimal.ZERO;
					courseGrade = courseGrade.divide(categoryWeightSum, RoundingMode.HALF_EVEN);
				} 
				
				break;
			default:
				BigDecimal categoryGradeSum = BigDecimal.ZERO;
				for (Category category : categoriesWithAssignments) {
					BigDecimal categoryGrade = getCategoryGrade(category, assignmentGradeRecordMap);
					BigDecimal categoryWeight = getCategoryWeight(category);
					
					if (categoryGrade != null) {
						boolean isExtraCredit = category.isExtraCredit() == null ? false : category.isExtraCredit().booleanValue();
						if (isExtraCredit) {
							extraCreditSum = extraCreditSum.add(categoryWeight);
							extraCredit = extraCredit.add(categoryGrade);
						} else {
							categoryWeightSum = categoryWeightSum.add(categoryWeight);
							categoryGradeSum = categoryGradeSum.add(categoryGrade.multiply(categoryWeight));
						} // else
					} // if 
				}
				
				if (courseGrade == null)
					courseGrade = BigDecimal.ZERO;
				
				if (categoryWeightSum.compareTo(BigDecimal.ZERO) != 0)
					courseGrade = categoryGradeSum.divide(categoryWeightSum, RoundingMode.HALF_EVEN);
				break;
			} // switch
		} // if 

		if (extraCredit.compareTo(BigDecimal.ZERO) != 0)
			courseGrade = courseGrade.add(extraCredit);

		// We don't want to return anything larger than 100%
		if (courseGrade != null && courseGrade.compareTo(BIG_DECIMAL_100) > 0)
			courseGrade = BIG_DECIMAL_100;
		
		return courseGrade;
	}
	
	
	
}
