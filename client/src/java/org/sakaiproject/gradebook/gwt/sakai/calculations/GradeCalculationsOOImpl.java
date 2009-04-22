package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.sakai.GradeCalculations;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.AssignmentGradeRecord;
import org.sakaiproject.tool.gradebook.Category;
import org.sakaiproject.tool.gradebook.Gradebook;

public class GradeCalculationsOOImpl implements GradeCalculations {

	final static BigDecimal BIG_DECIMAL_100 = new BigDecimal("100.00000");
	public static final MathContext MATH_CONTEXT = new MathContext(10, RoundingMode.HALF_DOWN);
	
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
						categoryWeight = categoryWeight.add(assignmentWeight);
				}
			}
			break;
		}
		
		return categoryWeight;
	}

	public BigDecimal getCourseGrade(Collection<Category> categoriesWithAssignments, Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap) {
		if (categoriesWithAssignments == null && assignmentGradeRecordMap != null) 
			categoriesWithAssignments = generateCategoriesWithAssignments(assignmentGradeRecordMap);
			
		return null;
	}

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
			percentageEarned = pointsEarned.multiply(BIG_DECIMAL_100).divide(pointsPossible, MATH_CONTEXT);
		}
		return percentageEarned;
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
	
	/*private boolean isExtraCredit(Category category) {
		return category.isExtraCredit() == null ? false : category.isExtraCredit().booleanValue();
	}*/
	
	private boolean isNormalCredit(Assignment assignment) {
		boolean isExtraCredit = isExtraCredit(assignment);
		return assignment.isCounted() && !assignment.isRemoved() && !isExtraCredit;
	}
	
	private boolean isUnweighted(Assignment assignment) {
		return assignment.isUnweighted() == null ? false : assignment.isUnweighted().booleanValue();
	}
	
	private boolean isUnweighted(Category category) {
		return category.isUnweighted() == null ? false : category.isUnweighted().booleanValue();
	}

}
