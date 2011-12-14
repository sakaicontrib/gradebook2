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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.gradebook.gwt.client.model.FinalGradeSubmissionResult;
import org.sakaiproject.gradebook.gwt.client.model.FinalGradeSubmissionStatus;
import org.sakaiproject.gradebook.gwt.sakai.model.UserDereference;
import org.sakaiproject.gradebook.gwt.server.model.FinalGradeSubmissionResultImpl;
import org.sakaiproject.gradebook.gwt.server.model.FinalGradeSubmissionStatusImpl;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.tool.gradebook.Gradebook;


public class SampleInstitutionalAdvisor implements InstitutionalAdvisor {

	private static final Log log = LogFactory.getLog(SampleInstitutionalAdvisor.class);

	private final String FILE_EXTENSION = ".csv";
	private final String FILE_HEADER = "User Eid,Name,Site Title : Group Title,Grade";
	
	// Final Grade Submission Status (FGSS)
	private final String FGSS_BANNER_MESSAGE = "The final grade process has begun";
	private final String FGSS_DIALOG_MESSAGE = "The final grade process has begun.  Please contact all course graders before making any Gradebook changes.";

	String finalGradeSubmissionPath = null;

	private SiteService siteService = null;
	private ToolManager toolManager = null;

	public List<String> getExportCourseManagementSetEids(Group group) {
		if(null == group) {
			log.error("ERROR : Group is null");
			return null;
		}
		if(null == group.getProviderGroupId()) {
			log.warn("Group Provider Id is null");
			return null;
		}
		return Arrays.asList(group.getProviderGroupId().split("\\+"));
	}

	public String getExportCourseManagementId(String userEid, Group group, List<String> enrollmentSetEids) {

		if (null == group) {
			log.error("ERROR : Group is null");
			return null;
		}
		
		if (null == group.getContainingSite()) {
			log.warn("Containing site is null");
			return null;
		}

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(group.getContainingSite().getTitle());
		stringBuilder.append(" : ");
		stringBuilder.append(group.getTitle());

		return stringBuilder.toString();
	}

	public String getExportUserId(UserDereference dereference) {

		return dereference.getDisplayId();
	}

	public String getFinalGradeUserId(UserDereference dereference) {

		return dereference.getEid();
	}

	public String[] getLearnerRoleNames() {
		String[] roleKeys = { "Student", "Open Campus", "access" };
		return roleKeys;
	}

	public boolean isExportCourseManagementIdByGroup() {
		return false;
	}

	public boolean isValidOverrideGrade(String grade, String learnerEid, String learnerDisplayId, Gradebook gradebook, Set<String> scaledGrades) {

		if (scaledGrades.contains(grade))
			return true;

		return false;
	}

	public FinalGradeSubmissionResult submitFinalGrade(List<Map<Column, String>> studentDataList, String gradebookUid) {

		FinalGradeSubmissionResult finalGradeSubmissionResult = new FinalGradeSubmissionResultImpl();
		
		if (null == finalGradeSubmissionPath || "".equals(finalGradeSubmissionPath)) {
			log.error("ERROR: Null and or empty test failed for finalGradeSubmissionPath");
			// 500 Internal Server Error
			finalGradeSubmissionResult.setStatus(500);
			return finalGradeSubmissionResult;
		}

		
		// Test if the path has a trailing file separator
		if(!finalGradeSubmissionPath.endsWith(File.separator)) {
			
			finalGradeSubmissionPath += File.separator;
		}
		
		String outputPath = finalGradeSubmissionPath;

		// Getting the siteId
		String siteId = null;

		try {

			Site site = siteService.getSite(toolManager.getCurrentPlacement().getContext());
			siteId = site.getId();

		} catch (IdUnusedException e) {
			
			log.error("EXCEPTION: Wasn't able to get the siteId", e);
			// 500 Internal Server Error
			finalGradeSubmissionResult.setStatus(500);
			return finalGradeSubmissionResult;
		}

		try {

			siteId = URLEncoder.encode(siteId, "utf-8");

		} catch (UnsupportedEncodingException e) {
			
			log.error("EXCEPTION: Wasn't able to url encode the siteId", e);
			// 500 Internal Server Error
			finalGradeSubmissionResult.setStatus(500);
			return finalGradeSubmissionResult;
		}

		// Test if path to final grade submission file exits
		File finalGradesPath = new File(outputPath);
		
		if(!finalGradesPath.exists()) {
			
			try {

				if(!finalGradesPath.mkdirs()) {
					
					log.error("Wasn't able to create final grade submission folder(s)");
					// 500 Internal Server Error
					finalGradeSubmissionResult.setStatus(500);
					return finalGradeSubmissionResult;
				}
			}
			catch(SecurityException se) {
				
				log.error("EXCEPTION: Wasn't able to create final grade submission folder(s)", se);
				// 500 Internal Server Error
				finalGradeSubmissionResult.setStatus(500);
				return finalGradeSubmissionResult;
			}
		}

		// Using string buffer for thread safety
		StringBuffer finalGradeSubmissionFile = new StringBuffer();
		finalGradeSubmissionFile.append(outputPath);
		finalGradeSubmissionFile.append(siteId);
		finalGradeSubmissionFile.append(FILE_EXTENSION);
		File finalGradesFile = new File(finalGradeSubmissionFile.toString());

		log.info("Writing final grades to " + finalGradesFile.getPath());

		PrintWriter filePrintWriter = null;

		try {
			
			if ((finalGradesFile.createNewFile() || (finalGradesFile.delete() && finalGradesFile.createNewFile())) && finalGradesFile.canWrite()) {

				filePrintWriter = new PrintWriter(new BufferedWriter(new FileWriter(finalGradesFile)));

				filePrintWriter.println(FILE_HEADER);

				// Using string buffer for thread safety
				StringBuffer exportData = null;

				for (Map<Column, String> studentData : studentDataList) {
					exportData = new StringBuffer();
					exportData.append(studentData.get(Column.FINAL_GRADE_USER_ID));
					exportData.append(",");
					exportData.append(studentData.get(Column.STUDENT_NAME));
					exportData.append(",");
					exportData.append(studentData.get(Column.EXPORT_CM_ID));
					exportData.append(",");
					exportData.append(studentData.get(Column.LETTER_GRADE));
					filePrintWriter.println(exportData.toString());
				}

				filePrintWriter.flush();
				filePrintWriter.close();
				
				
				// 201 Created
				finalGradeSubmissionResult.setStatus(201);
				
			} else {
				log.error("Wasn't able to create final grade submission file");
				// 500 Internal Server Error
				finalGradeSubmissionResult.setStatus(500);
				return finalGradeSubmissionResult;
			}

		} catch (IOException e) {

			log.error("EXCEPTION: Wasn't able to access the final grade submission file", e);
			// 500 Internal Server Error
			finalGradeSubmissionResult.setStatus(500);
			return finalGradeSubmissionResult;
		}
		
		return finalGradeSubmissionResult;
	}
	
	// API Impl
	public FinalGradeSubmissionStatus hasFinalGradeSubmission(String gradebookUid, boolean hasFinalGradeSubmission) {
		
		/*
		 * By default, when the user clicks on the final grade submission menu item,
		 * GB2 marks the gradebook as "locked". So we check for locked status, and
		 * set the messages in the FinalGradeSubmissionStatus object accordingly.
		 * 
		 * In addition, an institution could check its final grade submission system,
		 * and show appropriate messages to the user in case final grades 
		 * have been submitted.
		 * 
		 */
		
		FinalGradeSubmissionStatus finalGradeSubmissionStatus = new FinalGradeSubmissionStatusImpl();

		if(hasFinalGradeSubmission) {

			finalGradeSubmissionStatus.setBannerNotificationMessage(FGSS_BANNER_MESSAGE);
			finalGradeSubmissionStatus.setDialogNotificationMessage(FGSS_DIALOG_MESSAGE);
		}
		else {
			
			/*
			 * We don't set the banner and dialog messages so that the client doesn't show the messages
			 */
		}

		return finalGradeSubmissionStatus;
	}

	/*
	 * IOC setters:
	 */

	public void setFinalGradeSubmissionPath(String finalGradeSubmissionPath) {

		this.finalGradeSubmissionPath = finalGradeSubmissionPath;
	}

	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

	public void setToolManager(ToolManager toolManager) {
		this.toolManager = toolManager;
	}
	
	public String getDisplaySectionId(String enrollmentSetEid) {
		return "DisplayId for eid: " + enrollmentSetEid;
	}

	public String getPrimarySectionEid(List<String> eids) {
		if(null == eids || eids.isEmpty()) {
			return "";
		}
		else {
			return eids.get(0);
		}
	}
}
