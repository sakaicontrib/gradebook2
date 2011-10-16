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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.gradebook.gwt.sakai.model.UserDereference;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.tool.gradebook.Gradebook;


public class SampleInstitutionalAdvisor implements InstitutionalAdvisor {

	private static final Log log = LogFactory.getLog(SampleInstitutionalAdvisor.class);

	private final String CONTENT_TYPE_TEXT_HTML_UTF8 = "text/html; charset=UTF-8";
	private final String FILE_EXTENSION = ".csv";
	private final String FILE_HEADER = "User Eid,Name,Site Title : Group Title,Grade";

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

	public void submitFinalGrade(List<Map<Column, String>> studentDataList, String gradebookUid, HttpServletRequest request, HttpServletResponse response) {

		if (null == finalGradeSubmissionPath || "".equals(finalGradeSubmissionPath)) {
			log.error("ERROR: Null and or empty test failed for finalGradeSubmissionPath");
			// 500 Internal Server Error
			response.setStatus(500);
			return;
		}

		
		// Test if the path has a trailing file separator
		if(!finalGradeSubmissionPath.endsWith(File.separator)) {
			finalGradeSubmissionPath += File.separator;
		}
		
		String outputPath = finalGradeSubmissionPath;
		
		boolean relativePath = false;
		// if the path does not begin with a file separator, save relative to webroot
		if(!finalGradeSubmissionPath.startsWith((File.separator))) {
			////WARNING: This may not work in Weblogic J2EE containers: getRealPath() is optional
			outputPath = request.getSession().getServletContext().getRealPath("/") + outputPath;
			
			
			if (log.isDebugEnabled()) {
				log.debug("found relative path for gradefiles, setting relative to webroot: " + outputPath);
			}
			
			relativePath = true;
			
			log
					.info("found relative path for gradefiles, setting relative to webroot: "
							+ outputPath);
		}

		response.setContentType(CONTENT_TYPE_TEXT_HTML_UTF8);

		// Getting the siteId
		String siteId = null;

		try {

			Site site = siteService.getSite(toolManager.getCurrentPlacement().getContext());
			siteId = site.getId();

		} catch (IdUnusedException e2) {
			log.error("EXCEPTION: Wasn't able to get the siteId");
			// 500 Internal Server Error
			response.setStatus(500);
			e2.printStackTrace();
			return;
		}

		try {

			siteId = URLEncoder.encode(siteId, "utf-8");

		} catch (UnsupportedEncodingException e1) {
			log.error("EXCEPTION: Wasn't able to url encode the siteId");
			// 500 Internal Server Error
			response.setStatus(500);
			e1.printStackTrace();
			return;
		}

		// Test if path to final grade submission file exits
		File finalGradesPath = new File(outputPath);
		if(!finalGradesPath.exists()) {
			try {

				if(!finalGradesPath.mkdirs()) {
					log.error("Wasn't able to create final grade submission folder(s)");
					// 500 Internal Server Error
					response.setStatus(500);
					return;
				}
			}
			catch(SecurityException se) {
				log.error("EXCEPTION: Wasn't able to create final grade submission folder(s)");
				// 500 Internal Server Error
				response.setStatus(500);
				se.printStackTrace();
				return;
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
				response.setStatus(201);

				if(relativePath) {
					PrintWriter w = response.getWriter();
					StringBuffer next = new StringBuffer(request.getScheme());
					next.append("://")
					.append(request.getServerName())
					.append(":")
					.append(request.getLocalPort())
					.append("/")
					.append(finalGradeSubmissionPath) 
					.append(siteId)
					.append(FILE_EXTENSION);
				
	
					log.info("returning URL: " + next);
					w.append(next.toString());
					w.close();
				}
				
			} else {
				log.error("Wasn't able to create final grade submission file");
				// 500 Internal Server Error
				response.setStatus(500);
				return;
			}

		} catch (IOException e) {

			log.error("EXCEPTION: Wasn't able to access the final grade submission file");
			// 500 Internal Server Error
			response.setStatus(500);
			e.printStackTrace();
			return;
		}

	}
	
	// API Impl
	public boolean hasFinalGradeSubmission(String gradebookUid) {
		
		/*
		 * An institution could check the final grade submission system,
		 * and indicate if grades have been submitted for a course.
		 */
		return false;
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
