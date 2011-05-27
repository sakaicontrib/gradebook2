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


public class AuthModel {

	private static final long serialVersionUID = 1L;
	
	public static String AUTHMODEL_STRING_DELIMITER = "!";
	
	private Boolean isUserAbleToViewOwnGrades;
	private Boolean isUserHasGraderPermissions;
	private Boolean isUserAbleToGrade;
	private Boolean isUserAbleToEditAssessments;
	private Boolean isNewGradebook;
	private String placementId;
	
	public AuthModel() {
		
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

	public Boolean isNewGradebook() {
		return isNewGradebook;
	}

	public void setNewGradebook(Boolean isNewGradebook) {
		this.isNewGradebook = isNewGradebook;
	}
	
	public String getPlacementId() {
		return placementId;
	}

	public void setPlacementId(String placementId) {
		this.placementId = placementId;
	}
	
	private String fromBoolean(Boolean b) {
		if (b != null && b.booleanValue())
			return "b";
		return "a";
	}
	
	private Boolean toBoolean(char c) {
		switch (c) {
		case 'b':
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	private String pack(Boolean b1, Boolean b2, Boolean b3, Boolean b4, Boolean b5, Boolean b6, String id) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(fromBoolean(b1)).append(fromBoolean(b2)).append(fromBoolean(b3))
			.append(fromBoolean(b4)).append(fromBoolean(b5)).append(fromBoolean(b6)).append(id);
		
		return builder.toString();
	}
	
	// FFFF.sdf
	private void unpack(String s) {
		if (s != null) {
			isUserAbleToViewOwnGrades = toBoolean(s.charAt(0));
			isUserHasGraderPermissions = toBoolean(s.charAt(1));
			isUserAbleToGrade = toBoolean(s.charAt(2));
			isUserAbleToEditAssessments = toBoolean(s.charAt(3));
			isNewGradebook = toBoolean(s.charAt(4));
			placementId = s.substring(6);
		}
	}
	
	public String toString() {
		return new StringBuilder()
			.append(pack(isUserAbleToViewOwnGrades, isUserHasGraderPermissions, isUserAbleToGrade,
				isUserAbleToEditAssessments, isNewGradebook, Boolean.FALSE, placementId)).toString();
	}
	
	public void parse(String authString) {
		parse(authString, 0);
	}
	
	/*
	 * parse the authmodel found at index 'index' in the string
	 */
	
	public void parse(String authString, int index) {
		String[] parts = authString.split(AUTHMODEL_STRING_DELIMITER);
		
		if (parts.length>0 && index>-1 && index<parts.length
				&& parts[index] != null && parts[index].length()>0) {
			unpack(parts[index]);
		}
	}
	
}
