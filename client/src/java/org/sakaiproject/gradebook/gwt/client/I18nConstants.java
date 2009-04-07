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
package org.sakaiproject.gradebook.gwt.client;

import com.google.gwt.i18n.client.Constants;

public interface I18nConstants extends Constants {

	String categoryName();
	String itemName();
	
	String gradeTypePoints();
	String gradeTypePercentages();
	
	String orgTypeNoCategories();
	String orgTypeCategories();
	String orgTypeWeightedCategories();

	String navigationPanelHeader();
	String navigationPanelDynamicTabHeader();
	String navigationPanelFixedTabHeader();
	
	String columnTitleDisplayId();
	String columnTitleDisplayName();
	String columnTitleEmail();
	String columnTitleSection();
	
	String newMenuHeader();
	String prefMenuHeader();
	String prefMenuEnablePopups();
	String prefMenuGradebookName();
	String prefMenuOrgTypeHeader();
	String prefMenuOrgTypeLabel();
	String prefMenuGradeTypeHeader();
	String prefMenuReleaseGradesYes();
	String prefMenuReleaseGradesNo();
	String viewMenuHeader();
	String moreMenuHeader();
	String helpMenuHeader();
	
	String tabGradesHeader();
	String tabGradeScaleHeader();
	String tabHistoryHeader();
	
	String singleViewHeader();
	String singleGradeHeader();
	
	String nextLearner();
	String prevLearner();
	String viewAsLearner();
	String close();
	
	String addCategoryHeading();
	
	String addItemHeading();
	String addItemDirections();
	String addItemName();
	String addItemPointsEmpty();
	String addItemPoints();
	String addItemWeightEmpty();
	String addItemWeight();
	String addItemDueDateEmpty();
	String addItemDueDate();
	String addItemNoCategoryHeading();
	String addItemNoCategoryMessage();
	
	String deleteItemHeading();
	String editItemHeading();
	String gradeScaleHeading();
	String helpHeading();
	String historyHeading();
	String learnerSummaryHeading();
	String newCategoryHeading();
	String newItemHeading();
	
	String headerAddCategory();
	String headerAddCategoryTitle();
	String headerAddItem();
	String headerAddItemTitle();
	String headerEditCategory();
	String headerEditCategoryTitle();
	String headerEditItem();
	String headerEditItemTitle();
	String headerFinalGrade();
	String headerFinalGradeTitle();
	String headerExport();
	String headerExportTitle();
	String headerDeleteCategory();
	String headerDeleteCategoryTitle();
	String headerDeleteItem();
	String headerDeleteItemTitle();
	String headerGradeScale();
	String headerGradeScaleTitle();
	String headerHideItem();
	String headerHideItemTitle();
	String headerHistory();
	String headerHistoryTitle();
	String headerImport();
	String headerImportTitle();
	String headerSortAscending();
	String headerSortDescending();
	String headerSortAscendingTitle();
	String headerSortDescendingTitle();
	String requiredLabel();
	
	String cancelButton();
	String closeButton();
	String createButton();
	String deleteButton();
	String saveButton();

	
	String itemNameRequiredTitle();
	String itemNameRequiredText();
	
	String directionsConfirmDeleteItem();
	
	String confirmChangingWeightEquallyWeighted();
	String changingPointsRecalculatesGrades();
	
	String helpHtml();

	String learnerTabCommentHeader();
	String learnerTabExcuseHeader();
	String learnerTabGradeHeader();
		
	String unknownException();
	
	String finalGradeSubmissionTitle();
	String finalGradeSubmissionConfirmTitle();
	String finalGradeSubmissionConfirmText();
	String finalGradeSubmissionMessageText1a();
	String finalGradeSubmissionMessageText1b();
	String finalGradeSubmissionMessageText2a();
	String finalGradeSubmissionMessageText3a();
	String finalGradeSubmissionMessageText4a();
	String finalGradeSubmissionMessageText5a();
	String finalGradeSubmissionMessageText6a();
}
