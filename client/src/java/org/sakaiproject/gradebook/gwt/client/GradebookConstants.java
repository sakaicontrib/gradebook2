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

public interface GradebookConstants extends Constants {

	String gradebookHeader();
	String gradingInstructions();
	String choice();
	String choiceTitle();
	String summaryAndGradingOptions();
	String importExportGrades();
	String exportRoster();
	String gradeMappingImageTitle();
	String gradeMapping();
	String statsImageTitle();
	String statistics();
	String statsTotalNumberStudents();
	String statsNumberOfGradeableItems();
	String statsHighScore();
	String statsLowScore();
	String statsMedian();
	String statsMean();
	String statsStandardDeviation();
	String gradeType();
	String gradeTypePoints();
	String gradeTypePercentages();
	String gradeTypeLetterGrades();
	String gradeTypeText();
	String excludeNullGrades();
	String showCourseGradeToStudents();
	String recalculating();
	String failedToRecalculate();
	String unknownException();
	
	String view();
	String viewAllSections();
	String gradingDirection();
	String gradingDirectionHorizontal();
	String gradingDirectionVertical();
	String navigationBehavior();
	String navigationBehaviorPage();
	String navigationBehaviorWrap();
	String navigationBehaviorNoWrap();
	
	String search();
	String clear();
	
	String importDirections();
	String importSection1Header();
	String importSection2Header();
	String importSection3Header();
	String importSection1Content();
	String importSection2Content();
	String importSection3Content();
	String importSection3File();
	String importButton();
	
	String setupAddCategoryLabel();
	String setupSave();
	String setupColumnCategoryWeightingDefaultText();
	String setupColumnGradeItem();
	String setupColumnGradeItemDefaultText();
	String setupColumnItemWeighting();
	String setupColumnItemWeightingDefaultText();
	String setupColumnItemPoints();
	String setupColumnItemPointsDefaultText();
	String setupColumnItemExtraCredit();
	String setupColumnItemDueDate();
	String setupColumnItemRelease();
	String setupColumnItemInclude(); 
	String setupColumnItemRemove();
	String setupColumnCategoryTitle();
	String setupColumnCategoryWeighting();
	String setupColumnCategoryInclude();
	String setupColumnCategoryRemove();
	
	String setupNonNumericWeighting();
	String setupNonNumericPoints();
	String setupRemoveEmptyItem();
	
	String setupCategoryWeightingLabel();
	String setupEqualWeightLabel();
	String setupDropLowestLabel();
	
	String defaultEmptyInlineEditText();
	
	String booleanTrueTitle();
	String booleanFalseTitle();
	
	
	// New
	
	String changingPointsRecalculatesGrades();
	
	
}
