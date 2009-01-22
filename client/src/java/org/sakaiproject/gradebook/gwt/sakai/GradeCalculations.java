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
import java.util.Collection;
import java.util.Map;

import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.AssignmentGradeRecord;
import org.sakaiproject.tool.gradebook.Category;

public interface GradeCalculations {

	public BigDecimal getNewPointsGrade(Double pointValue, Double maxPointValue, Double maxPointStartValue);
	
	public BigDecimal getPercentAsPointsEarned(Assignment assignment, Double percentage);
	
	/**
	 * Result = PointsEarned * 100 / PointsPossible
	 * 
	 * @param assignment an assignment
	 * @param assignmentGradeRecord an assignmentGradeRecord that is linked to the assignment, for a specific user 
	 * 
	 * @return points earned as percent for an assignment, or null if assignment or assignmentGradeRecord are null
	 */
	public BigDecimal getPointsEarnedAsPercent(Assignment assignment, AssignmentGradeRecord assignmentGradeRecord);
	
	/**
	 * Result = AssignmentWeight * PointsEarnedAsPercent / 100
	 * 
	 * @param assignment an assignment
	 * @param pointsEarnedAsPercent the result from the getPointsEarnedAsPercent(...) method
	 * @param enableAssignmentConstraints a boolean that indicates if the assignment "constrains", such as [ isCounted, isRemoved, isExtraCredit] are applied
	 * 
	 * @return points earned as a weighted percentage for an assignment, or null if assignment or assignmentGradeRecord are null
	 */
	public BigDecimal getEarnedWeightedPercentage(Assignment assignment, BigDecimal pointsEarnedAsPercent, Boolean enableAssignmentConstraints);
	
	
	/**
	 * Result = SUM( getPointsEarnedAsPercent(...) ) for a given category
	 * 
	 * @param categoryWithAssignments a category e.g. [Home Works, Labs, Exams, Labs, ...] containing a collection of assignments that a user has taken
	 * @param assignmentGradeRecordMap a map of assignmentId to assignmentGradeRecords for a specific user
	 * 
	 * @return the sum of adding results from getPointsEarnedAsPercent(...) for a specific category, where result >= BigDecimal.ZERO
	 */
	public BigDecimal sumEarnedWeightedPercentages(Category categoryWithAssignments, Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap);
	
	
	/**
	 * Result = SUM( assignment.getAssignmentWeighting() ) for a given category
	 * 
	 * @param categoryWithAssignments a category that contains a collection of assignments for which there may exist an AssignmentGradeRecord for a specific user
	 * @param assignmentGradeRecordMap a map of assignmentId to assignmentGradeRecords for a specific user
	 * 
	 * @return the sum of adding up all the assignment weights for a specific category, where result >= BigDecimal.ZERO
	 */
	public BigDecimal sumAssignmentWeights(Category categoryWithAssignments, Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap);

	
	/**
	 * Result = The Category Weight while taking into consideration the category "constraints" such as [isRemove, ...]
	 * 
	 * @param category a category
	 * 
	 * @return the category weight, or null if the category is null
	 */
	public BigDecimal getCategoryWeight(Category category);
	
	
	/**
	 * Result = SUM( all the weighted extra credit assignments) for a specific category
	 * 
	 * @param categoryWithAssignments a category that contains a collection of assignments for which there may exist an AssignmentGradeRecord for a specific user
	 * @param assignmentGradeRecordMap a map of assignmentId to assignmentGradeRecords for a specific user
	 * 
	 * @return the sum of all weighed extra credit assignments for a specific category, where result >= BigDecimal.ZERO
	 */
	public BigDecimal sumExtraCreditEarnedWeightedPercentage(Category categoryWithAssignments, Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap);

	
	/**
	 * Result = sumEarnedWeightedPercentages(...) * 100 / sumAssignmentWeights(...) + sumExtraCreditEarnedWeightedPercentage(...)
	 * 
	 * @param categoryWithAssignments a category that contains a collection of assignments for which there may exist an AssignmentGradeRecord for a specific user
	 * @param assignmentGradeRecordMap a map of assignmentId to assignmentGradeRecords for a specific user
	 * 
	 * @return the category grade, where result >= BigDecimal.ZERO
	 */
	public BigDecimal getCategoryGrade(Category categoryWithAssignments, Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap);
	
	
	/**
	 * Result = SUM( getCategoryGrade(...)  * getCategoryWeight(...) / 100)
	 * 
	 * @param categoryWithAssignments a category that contains a collection of assignments for which there may exist an AssignmentGradeRecord for a specific user
	 * @param assignmentGradeRecordMap a map of assignmentId to assignmentGradeRecords for a specific user
	 * 
	 * @return the course grade, where result >= BigDecimal.ZERO
	 */
	public BigDecimal getCourseGrade(Collection<Category> categoriesWithAssignments, Map<Long, AssignmentGradeRecord> assignmentGradeRecordMap);
	
}
