/**********************************************************************************
*
* Copyright (c) 2008, 2009, 2010, 2011 The Regents of the University of California
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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sakaiproject.gradebook.gwt.client.model.FinalGradeSubmissionResult;
import org.sakaiproject.gradebook.gwt.client.model.FinalGradeSubmissionStatus;
import org.sakaiproject.gradebook.gwt.sakai.model.UserDereference;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.tool.gradebook.Gradebook;

public interface InstitutionalAdvisor {

	public enum Column { STUDENT_NAME, STUDENT_UID, STUDENT_GRADE, LETTER_GRADE, EXPORT_USER_ID, EXPORT_CM_ID, FINAL_GRADE_USER_ID, RAW_GRADE };
	
	/**
	 * 
	 * @param group : The Authz Group
	 * @return List of enrollment set eids
	 */
	public List<String> getExportCourseManagementSetEids(Group group);
	
	/**
	 * 
	 * @param userEid : The user's EID
	 * @param group : The user's Authz Group
	 * 
	 * @return the export course management id 
	 */
	public String getExportCourseManagementId(String userEid, Group group, List<String> enrollmentSetEids);
	
	/**
	 * 
	 * @param dereference : UserDereference, a representation of user data
	 * @return the id to be used for this individual on both import/export
	 */
	public String getExportUserId(UserDereference dereference);
	
	/**
	 * @param dereference : UserDereference, a representation of user data
	 */
	public String getFinalGradeUserId(UserDereference dereference);
	
	/**
	 * Method to retrieve the array of role keys for valid learners 
	 * 
	 * @return array of Long objects representing the actual Sakai role keys
	 */
	public String[] getLearnerRoleNames();

	/**
	 * 
	 * @return
	 */
	public boolean isExportCourseManagementIdByGroup();
	
	/**
	 * 
	 * @param grade
	 * @param learnerEid
	 * @param learnerDisplayId
	 * @param gradebook
	 * @param scaledGrades
	 * @return
	 */
	public boolean isValidOverrideGrade(String grade, String learnerEid, String learnerDisplayId, Gradebook gradebook, Set<String> scaledGrades);
	
	
	/**
	 * Method to submit final grades to the SIS.
	 * 
	 * @param studentDataList : a list of Map objects containing all the student data properties
	 * @param gradebookUid : a String identifier for this gradebook
	 *
	 * @return finalGradeSubmissionResult
	 */
	public FinalGradeSubmissionResult submitFinalGrade(List<Map<Column,String>> studentDataList, String gradebookUid);
	
		
	/**
	 * given a section eid as a string, return a unique human readable form of the section id. 
	 * Depending on the CMS implementation, this could be the same as the eid,
	 * the section title, or something other locally identifiable id number or code for a section. 
	 * 
	 * @param sectionEid : a CMS section eid
	 */
	public String getDisplaySectionId(String sectionEid);
	
	
	/**
	 * given a list of section eids, return one that should be used to identify a students primary enrollment in a site
	 *  - since GB[2] cannot differentiate among the different sections it is an institutional obligation to determine 
	 *  the chosen section (for grading etc)
	 * 
	 * @param eids : a list of eids to sort out
	 */
	public String getPrimarySectionEid(List<String> eids);
	
	/**
	 * @since 1.7.0
	 * 
	 * Method that determines if the current user accesses a gradebook
	 * for which the grades have been submitted.
	 * 
	 * @param gradebookUid Gradebook UID
	 * @param hasFinalGradeSubmission indicates if the final grades have been submitted via GB2
	 * 
	 * @return status messages if final grades have been submitted
	 * 
	 */
	public FinalGradeSubmissionStatus hasFinalGradeSubmission(String gradebookUid, boolean hasFinalGradeSubmission);

	
}
