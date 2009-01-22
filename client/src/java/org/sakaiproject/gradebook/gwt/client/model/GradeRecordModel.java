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
package org.sakaiproject.gradebook.gwt.client.model;

import java.util.Map;

public class GradeRecordModel extends EntityModel {

	private static final long serialVersionUID = 1L;

	public enum Key { ID, POINTS_EARNED, PERCENT_EARNED, LETTER_EARNED, CATEGORY_NAME, ASSIGNMENT_NAME, ASSIGNMENT_ID, MAX_POINTS,
		COURSE_GRADE, EXCLUDED, WEIGHT, LOG, COMMENTS, DROPPED };
	
	public GradeRecordModel() {
		super();
	}
	
	public GradeRecordModel(Map<String, Object> properties) {
		super(properties);
	}
	
	public String getIdentifier() {
		return get(Key.ID.name());
	}

	public void setIdentifier(String id) {
		set(Key.ID.name(), id);
	}
	
	public String getDisplayName() {
		return get(Key.ASSIGNMENT_NAME.name());
	}

	public Double getPointsEarned() {
		return get(Key.POINTS_EARNED.name());
	}
	
	public void setPointsEarned(Double pointsEarned) {
		set(Key.POINTS_EARNED.name(), pointsEarned);
	}
	
	public Double getPercentEarned() {
		return get(Key.PERCENT_EARNED.name());
	}
	
	public void setPercentEarned(Double pointsEarned) {
		set(Key.PERCENT_EARNED.name(), pointsEarned);
	}
	
	public String getLetterEarned() {
		return get(Key.LETTER_EARNED.name());
	}
	
	public void setLetterEarned(String pointsEarned) {
		set(Key.LETTER_EARNED.name(), pointsEarned);
	}
	
	public String getCategoryName() {
		return get(Key.CATEGORY_NAME.name());
	}
	
	public void setCategoryName(String categoryName) {
		set(Key.CATEGORY_NAME.name(), categoryName);
	}
	
	public String getAssignmentName() {
		return get(Key.ASSIGNMENT_NAME.name());
	}
	
	public void setAssignmentName(String assignmentName) {
		set(Key.ASSIGNMENT_NAME.name(), assignmentName);
	}

	public Long getAssignmentId() {
		return get(Key.ASSIGNMENT_ID.name());
	}
	
	public void setAssignmentId(Long assignmentId) {
		set(Key.ASSIGNMENT_ID.name(), assignmentId);
	}

	public void setPointsPossible(Double maxPoints) {
		set(Key.MAX_POINTS.name(), maxPoints);
	}
	
	public Double getPointsPossible()
	{
		return get(Key.MAX_POINTS.name());
	}

	public String getCourseGrade() {
		return get(Key.COURSE_GRADE.name());
	}
	
	public void setCourseGrade(String courseGrade) {
		set(Key.COURSE_GRADE.name(), courseGrade);
	}
	
	public Boolean getExcluded() {
		return get(Key.EXCLUDED.name());
	}
	
	public void setExcluded(Boolean excluded) {
		set(Key.EXCLUDED.name(), excluded);
	}
	
	public Double getWeight() {
		return get(Key.WEIGHT.name());
	}
	
	public void setWeight(Double weight) {
		set(Key.WEIGHT.name(), weight);
	}
	
	public Boolean getLog() {
		return get(Key.LOG.name());
	}
	
	public void setLog(Boolean log) {
		set(Key.LOG.name(), log);
	}
	
	public String getComments() {
		return get(Key.COMMENTS.name());
	}
	
	public void setComments(String comments) {
		set(Key.COMMENTS.name(), comments);
	}
	
	public Boolean getDropped() {
		return get(Key.DROPPED.name());
	}
	
	public void setDropped(Boolean dropped) {
		set(Key.DROPPED.name(), dropped);
	}
	

	@Override
	public boolean equals(Object obj) {
		
		if (obj != null)
		{
			if (obj instanceof GradeRecordModel) {
				String a; 
				String b; 
				GradeRecordModel other = (GradeRecordModel) obj;
				
				a = this.getIdentifier(); 
				b = other.getIdentifier(); 
				
				if (a == null && b == null)
				{
					return true; 
				}
				else if (a == null || b == null) 
				{
					return false; 
				}
				else
				{
					return a.equals(b);
				}
			}
		}
		return false;
	}
}
