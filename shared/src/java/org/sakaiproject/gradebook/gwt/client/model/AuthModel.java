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

import java.util.HashMap;

import com.extjs.gxt.ui.client.data.BaseModel;

public class AuthModel extends BaseModel {

	private static final long serialVersionUID = 1L;
	
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
	
	
	public String toString() {
		StringBuilder buffer = new StringBuilder().append("?");
		
		boolean hasEntry = false;
		if (isUserAbleToViewOwnGrades != null) {
			buffer.append("isUserAbleToViewOwnGrades=").append(String.valueOf(isUserAbleToViewOwnGrades));
			hasEntry = true;
		}
		if (isUserHasGraderPermissions != null) {
			if (hasEntry)
				buffer.append("&");
			buffer.append("isUserHasGraderPermissions=").append(String.valueOf(isUserHasGraderPermissions));
			hasEntry = true;
		}
		if (isUserAbleToGrade != null) {
			if (hasEntry)
				buffer.append("&");
			buffer.append("isUserAbleToGrade=").append(String.valueOf(isUserAbleToGrade));
			hasEntry = true;
		}
		if (isUserAbleToEditAssessments != null) {
			if (hasEntry)
				buffer.append("&");
			buffer.append("isUserAbleToEditAssessments=").append(String.valueOf(isUserAbleToEditAssessments));
			hasEntry = true;
		}
		if (isNewGradebook != null) {
			if (hasEntry)
				buffer.append("&");
			buffer.append("isNewGradebook=").append(String.valueOf(isNewGradebook));
			hasEntry = true;
		}
		if (placementId != null) {
			if (hasEntry)
				buffer.append("&");
			buffer.append("placementId=").append(placementId);
		}
		
		return buffer.toString();
	}
	
	public void parse(String authString) {

		if (authString != null) {
			HashMap<String, Object> nameValueMap = new HashMap<String, Object>();
			
			authString = authString.substring(1);
			
			String[] params = authString.split("&");
			
			if (params != null) {
				for (int i=0;i<params.length;i++) {
					String[] parts = params[i].split("=");
					
					Object value = null;

					if (!parts[0].equals("placementId") && parts[1] != null)
						value = Boolean.valueOf(parts[1]);
					else
						value = parts[1];
					
					if (value != null)
						nameValueMap.put(parts[0], value);
				}
				
				if (nameValueMap != null) {
					isUserAbleToViewOwnGrades = (Boolean)nameValueMap.get("isUserAbleToViewOwnGrades");
					isUserHasGraderPermissions = (Boolean)nameValueMap.get("isUserHasGraderPermissions");
					isUserAbleToGrade = (Boolean)nameValueMap.get("isUserAbleToGrade");
					isUserAbleToEditAssessments = (Boolean)nameValueMap.get("isUserAbleToEditAssessments");
					isNewGradebook = (Boolean)nameValueMap.get("isNewGradebook");
					placementId = (String)nameValueMap.get("placementId");
				}
			}
		}
	}
	
}
