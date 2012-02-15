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

package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import net.sf.ehcache.Element;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidDataException;
import org.sakaiproject.gradebook.gwt.client.exceptions.SecurityException;

import com.sun.jersey.core.util.Base64;

@Path("statistics")
public class Statistics extends Resource {
	
	@GET @Path("/instructor/{uid}/{id}/{sectionId}")
	@Produces("application/json")
	public String getInstructorStatistics(
			@PathParam("uid") String gradebookUid,
			@PathParam("id") Long gradebookId,
			@PathParam("sectionId") String sectionId) throws SecurityException {

		List<org.sakaiproject.gradebook.gwt.client.model.Statistics> list = 
			service.getGraderStatistics(gradebookUid, gradebookId, Base64.base64Decode(sectionId));
		return toJson(list, list.size());
	}

	// Accessed by client StudentPanel : use caching
	@GET @Path("/{uid}/{id}/{studentUid}")
	@Produces("application/json")
	public String getStudentStatistics(
			@PathParam("uid") String gradebookUid,
			@PathParam("id") Long gradebookId,
			@PathParam("studentUid") String studentUid) throws SecurityException {

		String cacheKey = null;
		String stats = null;
		if(useCache != null && useCache.booleanValue()) {
			cacheKey = getCacheKey(Resource.CACHE_KEY_STUDENT_STATISTICS, gradebookUid, studentUid);
			Element e = cache.get(cacheKey);
			if (e != null) {
				stats = (String) e.getObjectValue();
			}
		}
		if(stats == null) {
			List<org.sakaiproject.gradebook.gwt.client.model.Statistics> list = 
				service.getLearnerStatistics(gradebookUid, gradebookId, Base64.base64Decode(studentUid));
			stats = toJson(list, list.size());
			if(cacheKey != null) {
				cache.put(new Element(cacheKey,stats));
			}
		}
		return stats;
	}

	@GET @Path("/instructor/{uid}/{id}/{assignmentId}/{sectionId}")
	@Produces("application/json")
	public String getStatisticsData(
			@PathParam("uid") String gradebookUid,
			@PathParam("id") Long gradebookId,
			@PathParam("assignmentId") Long assignmentId,
			@PathParam("sectionId") String sectionId) throws SecurityException, InvalidDataException {


		int[][] gradeFrequencies = service.getGradeItemStatistics(gradebookUid, assignmentId, Base64.base64Decode(sectionId));
		return toJson(gradeFrequencies);
	}
	
	// Accessed by client StudentPanel : use caching
	@GET @Path("/student/{uid}/{id}/{assignmentId}")
	@Produces("application/json")
	public String getStudentStatisticsData(
			@PathParam("uid") String gradebookUid,
			@PathParam("id") Long gradebookId,
			@PathParam("assignmentId") Long assignmentId) throws SecurityException, InvalidDataException {

		String cacheKey = null;
		String stats = null;
		if(useCache != null && useCache.booleanValue()) {
			cacheKey = getCacheKey(Resource.CACHE_KEY_STUDENT_STATISTICS_DATA, gradebookUid, assignmentId.toString());
			Element e = cache.get(cacheKey);
			if (e != null) {
				stats = (String) e.getObjectValue();
			}
		}
		if(stats == null) {
			int[][] gradeFrequencies = service.getGradeItemStatistics(gradebookUid, assignmentId, AppConstants.ALL);
			stats = toJson(gradeFrequencies);
			if(cacheKey != null) {
				cache.put(new Element(cacheKey,stats));
			}
		}
		return stats;
	}
	
	@GET @Path("/course/{uid}")
	@Produces("application/json")
	public String getCourseStatisticsData(
			@PathParam("uid") String gradebookUid) throws SecurityException, InvalidDataException {

		Map<String, Integer> gradeFrequencies = service.getCourseGradeStatistics(gradebookUid);
		return toJson(gradeFrequencies);
	}
	
	@GET @Path("/course/{uid}/{sectionId}")
	@Produces("application/json")
	public String getCourseStatisticsData(
			@PathParam("uid") String gradebookUid,
			@PathParam("sectionId") String sectionId) throws SecurityException, InvalidDataException {

		Map<String, Integer> gradeFrequencies = service.getCourseGradeStatistics(gradebookUid, Base64.base64Decode(sectionId));
		return  toJson(gradeFrequencies);
	}

	// Accessed by client StudentPanel : use caching
	@GET @Path("/student/course/{uid}/{sectionId}")
	@Produces("application/json")
	public String getStudentCourseStatisticsData(
			@PathParam("uid") String gradebookUid,
			@PathParam("sectionId") String sectionId) throws SecurityException, InvalidDataException {

		String cacheKey = null;
		String stats = null;
		if(useCache != null && useCache.booleanValue()) {
			cacheKey = getCacheKey(Resource.CACHE_KEY_COURSE_STATISTICS_DATA, gradebookUid, sectionId);
			Element e = cache.get(cacheKey);
			if (e != null) {
				stats = (String) e.getObjectValue();
			}
		}
		if(stats == null) {
			Map<String, Integer> gradeFrequencies = service.getCourseGradeStatistics(gradebookUid, Base64.base64Decode(sectionId));
			stats = toJson(gradeFrequencies);
			if(cacheKey != null) {
				cache.put(new Element(cacheKey,stats));
			}
		}
		return stats;
	}
	
}
