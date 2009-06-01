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

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GradebookModel extends EntityModel implements IsSerializable {

	public enum GradeType { POINTS("Points"), PERCENTAGES("Percentages"), LETTERS("Letters"), TEXT("Text");
		private String displayName;
		
		GradeType(String displayName) {
			this.displayName = displayName;
		}
	
		public String getDisplayName() {
			return displayName;
		}
		
	};
	
	public enum CategoryType { NO_CATEGORIES("No Categories"), SIMPLE_CATEGORIES("Categories"), 
		WEIGHTED_CATEGORIES("Weighted Categories");
	
		private String displayName;
		
		CategoryType(String displayName) {
			this.displayName = displayName;
		}
	
		public String getDisplayName() {
			return displayName;
		}
		
	}
	public enum Key { NAME("Name"), CATEGORYTYPE("Category Type"), GRADETYPE("Grade Type"), RELEASEGRADES("Release Grades");
	
		private String displayName;
	
		Key(String displayName) {
			this.displayName = displayName;
		}
		
		public String getDisplayName() {
			return displayName;
		}
	
	}
	
	private static final long serialVersionUID = 1L;
	
	private String gradebookUid;
	private Long gradebookId;
	private String name;
	private Boolean isReleaseGrades;
	private CategoryType categoryType;
	private GradeType gradeType;
	private Boolean isUserAbleToViewOwnGrades;
	private Boolean isUserHasGraderPermissions;
	private Boolean isUserAbleToGrade;
	private Boolean isUserAbleToEditAssessments;
	private StudentModel userAsStudent;
	private List<FixedColumnModel> columns;
	private String userName;
	private ItemModel gradebookItemModel;
	private Boolean isNewGradebook;

	public GradebookModel() {
		this.isNewGradebook = Boolean.FALSE;
	}

	public String getGradebookUid() {
		return gradebookUid;
	}


	public void setGradebookUid(String gradebookUid) {
		this.gradebookUid = gradebookUid;
	}


	public Long getGradebookId() {
		return gradebookId;
	}


	public void setGradebookId(Long gradebookId) {
		this.gradebookId = gradebookId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}	
	
	public List<FixedColumnModel> getColumns() {
		return columns;
	}

	public void setColumns(List<FixedColumnModel> columns) {
		this.columns = columns;
	}

	public Boolean isUserAbleToViewOwnGrades() {
		return isUserAbleToViewOwnGrades;
	}

	public void setUserAbleToViewOwnGrades(Boolean isUserAbleToViewOwnGrades) {
		this.isUserAbleToViewOwnGrades = isUserAbleToViewOwnGrades;
	}

	public Boolean isUserHasGraderPermissions() {
		return isUserHasGraderPermissions;
	}

	public void setUserHasGraderPermissions(Boolean isUserHasGraderPermissions) {
		this.isUserHasGraderPermissions = isUserHasGraderPermissions;
	}

	public Boolean isUserAbleToGrade() {
		return isUserAbleToGrade;
	}

	public void setUserAbleToGrade(Boolean isUserAbleToGrade) {
		this.isUserAbleToGrade = isUserAbleToGrade;
	}

	public Boolean isUserAbleToEditAssessments() {
		return isUserAbleToEditAssessments;
	}

	public void setUserAbleToEditAssessments(Boolean isUserAbleToEditAssessments) {
		this.isUserAbleToEditAssessments = isUserAbleToEditAssessments;
	}

	public StudentModel getUserAsStudent() {
		return userAsStudent;
	}

	public void setUserAsStudent(StudentModel userAsStudent) {
		this.userAsStudent = userAsStudent;
	}

	@Override
	public String getDisplayName() {
		return getName();
	}

	@Override
	public String getIdentifier() {
		return gradebookUid;
	}

	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public ItemModel getGradebookItemModel() {	
		return gradebookItemModel;
	}
	
	public void setGradebookItemModel(ItemModel gradebookItemModel) {
		this.gradebookItemModel = gradebookItemModel;
	}

	public Boolean isNewGradebook() {
		return isNewGradebook;
	}

	public void setNewGradebook(Boolean isNewGradebook) {
		this.isNewGradebook = isNewGradebook;
	}
}
