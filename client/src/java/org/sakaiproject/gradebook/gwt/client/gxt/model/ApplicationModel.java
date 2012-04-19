/***********************************************************************************
*
* Copyright (c) 2008, 2009, 2010, 2011, 2012 The Regents of the University of California
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
package org.sakaiproject.gradebook.gwt.client.gxt.model;

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationSetup;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.key.ApplicationKey;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ModelData;


public class ApplicationModel extends EntityModel implements ApplicationSetup {

	private static final long serialVersionUID = 1L;
		
	public ApplicationModel() {
		super();
	}
	
	public ApplicationModel(EntityOverlay overlay) {
		super(overlay);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Application#getGradebookModels()
	 */
	public List<Gradebook> getGradebookModels() {
		return get(ApplicationKey.A_GB_MODELS.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Application#setGradebookModels(java.util.List)
	 */
	public void setGradebookModels(List<Gradebook> gradebookModels) {
		set(ApplicationKey.A_GB_MODELS.name(), gradebookModels);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Application#getPlacementId()
	 */
	public String getPlacementId() {
		return get(ApplicationKey.S_PLACE_ID.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Application#setPlacementId(java.lang.String)
	 */
	public void setPlacementId(String placementId) {
		set(ApplicationKey.S_PLACE_ID.name(), placementId);
	}

	/*@Override
	public String getDisplayName() {
		return "Gradebook Tool";
	}*/
	
	public String getIdentifier() {	
		return getPlacementId();
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Application#getHelpUrl()
	 */
	public String getHelpUrl() {
		return get(ApplicationKey.S_HELPURL.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Application#setHelpUrl(java.lang.String)
	 */
	public void setHelpUrl(String helpUrl) {
		set(ApplicationKey.S_HELPURL.name(), helpUrl);
	}

	public boolean isSearchRosterByFieldEnabled() {
		boolean enabled = false;
		Boolean enabledObj = get(ApplicationKey.S_FIND_BY_FIELD.name());
		if(enabledObj != null) {
			enabled = enabledObj.booleanValue();
		}
		return enabled;
	}

	/**
	 * @param isEnabled
	 */
	public void setSearchRosterByFieldEnabled(boolean isEnabled) {
		set(ApplicationKey.S_FIND_BY_FIELD.name(), Boolean.valueOf(isEnabled));
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Application#getEnabledGradeTypes()
	 */
	public List<GradeType> getEnabledGradeTypes() {
		return get(ApplicationKey.V_ENBLD_GRD_TYPES.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Application#setEnabledGradeTypes(java.util.List)
	 */
	public void setEnabledGradeTypes(List<GradeType> enabledGradeTypes) {
		set(ApplicationKey.V_ENBLD_GRD_TYPES.name(), enabledGradeTypes);
	}
	
	public boolean isChildString(String property) {
		return isGradeType(property);
	}

	public boolean isGradeType(String property) {
		return property.equals(ApplicationKey.V_ENBLD_GRD_TYPES.name());
	}

	public ModelData newChildModel(String property, EntityOverlay overlay) {
		if (property.equals(ApplicationKey.A_GB_MODELS.name()))
			return new GradebookModel(overlay);

		return new BaseModel();
	}

	public void setShowWeightedEnabled(boolean isShowWeightedEnabled) {
		set(ApplicationKey.S_SH_WTD_ENABLED.name(), Boolean.toString(isShowWeightedEnabled));
	}
	
	public boolean isShowWeightedEnabled() {
		return (Boolean.TRUE.toString().equals(get(ApplicationKey.S_SH_WTD_ENABLED.name())));
	}

	public void setAuthorizationDetails(String authorizationDetails) {
		
		set(ApplicationKey.S_AUTH_DETAILS.name(), authorizationDetails);
		
	}

	public String getAuthorizationDetails() {
		
		return get(ApplicationKey.S_AUTH_DETAILS.name());
	}

	public boolean checkFinalGradeSubmissionStatus() {
		
		Boolean status = get(ApplicationKey.B_CHECK_FINAL_GRADE_SUBMISSION_STATUS.name());
		
		if(null != status) {
			
			return status.booleanValue();
		}
		
		return false;
	}

	public void setCheckFinalGradeSubmissionStatus(boolean status) {

		set(ApplicationKey.B_CHECK_FINAL_GRADE_SUBMISSION_STATUS.name(), Boolean.valueOf(status));
	}
	
	public int getCachedDataAge() {
		
		Integer age = get(ApplicationKey.I_CACHED_DATA_AGE.name());
		
		if(null != age) {
			
			return age.intValue();
		}
		
		return AppConstants.CURRENT_STATISTICS_DATA;
	}
	
	public void setCachedDataAge(int age) {
		
		set(ApplicationKey.I_CACHED_DATA_AGE.name(), Integer.valueOf(age));
	}

	public boolean isFinalGradeSubmissionEnabled() {
		
		Boolean isEnabled = get(ApplicationKey.B_ENABLE_FINAL_GRADE_SUBMISSION.name());
		
		if(null != isEnabled) {
			
			return isEnabled.booleanValue();
		}
		
		return false;
	}

	public void setFinalGradeSubmissionEnabled(boolean isEnabled) {

		set(ApplicationKey.B_ENABLE_FINAL_GRADE_SUBMISSION.name(), Boolean.valueOf(isEnabled));
	}
}
