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

	public enum Key { NAME, CATEGORYTYPE, GRADETYPE, RELEASEGRADES }
	
	private static final long serialVersionUID = 1L;
	
	private String gradebookUid;
	private Long gradebookId;
	private String name;
	/*private Boolean isReleaseGrades;
	private CategoryType categoryType;
	private GradeType gradeType;
	private Boolean isUserAbleToViewOwnGrades;
	private Boolean isUserHasGraderPermissions;
	private Boolean isUserAbleToGrade;
	private Boolean isUserAbleToEditAssessments;*/
	private StudentModel userAsStudent;
	private List<FixedColumnModel> columns;
	private String userName;
	private ItemModel gradebookItemModel;
	private Boolean isNewGradebook;
	private ConfigurationModel configurationModel;
	
	private List<StatisticsModel> statsModel; 
	

	public GradebookModel() {
		setNewGradebook(Boolean.FALSE);
	}

	public String getGradebookUid() {
		return get(GradebookKey.GRADEBOOKUID.name());
	}


	public void setGradebookUid(String gradebookUid) {
		set(GradebookKey.GRADEBOOKUID.name(), gradebookUid);
	}


	public Long getGradebookId() {
		return get(GradebookKey.GRADEBOOKID.name());
	}


	public void setGradebookId(Long gradebookId) {
		set(GradebookKey.GRADEBOOKID.name(), gradebookId);
	}

	public ConfigurationModel getConfigurationModel() {
		return get(GradebookKey.CONFIGURATIONMODEL.name());
	}

	public void setConfigurationModel(ConfigurationModel configurationModel) {
		set(GradebookKey.CONFIGURATIONMODEL.name(), configurationModel);
	}

	public String getName() {
		return get(GradebookKey.NAME.name());
	}


	public void setName(String name) {
		set(GradebookKey.NAME.name(), name);
	}	
	
	public List<FixedColumnModel> getColumns() {
		return get(GradebookKey.COLUMNS.name());
	}

	public void setColumns(List<FixedColumnModel> columns) {
		set(GradebookKey.COLUMNS.name(), columns);
	}

	/*public CategoryType getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(CategoryType categoryType) {
		this.categoryType = categoryType;
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
	}*/


	public StudentModel getUserAsStudent() {
		return get(GradebookKey.USERASSTUDENT.name());
	}

	public void setUserAsStudent(StudentModel userAsStudent) {
		set(GradebookKey.USERASSTUDENT.name(), userAsStudent);
	}

	@Override
	public String getDisplayName() {
		return getName();
	}

	@Override
	public String getIdentifier() {
		return getGradebookUid();
	}

	public String getUserName() {
		return get(GradebookKey.USERNAME.name());
	}
	
	public void setUserName(String userName) {
		set(GradebookKey.USERNAME.name(), userName);
	}
	
	public ItemModel getGradebookItemModel() {	
		return get(GradebookKey.GRADEBOOKITEMMODEL.name());
	}
	
	public void setGradebookItemModel(ItemModel gradebookItemModel) {
		set(GradebookKey.GRADEBOOKITEMMODEL.name(), gradebookItemModel);
	}

	public Boolean isNewGradebook() {
		return get(GradebookKey.ISNEWGRADEBOOK.name());
	}

	public void setNewGradebook(Boolean isNewGradebook) {
		set(GradebookKey.ISNEWGRADEBOOK.name(), isNewGradebook);
	}

	public List<StatisticsModel> getStatsModel() {
		return get(GradebookKey.STATSMODELS.name());
	}

	public void setStatsModel(List<StatisticsModel> statsModel) {
		set(GradebookKey.STATSMODELS.name(), statsModel);
	}
}
