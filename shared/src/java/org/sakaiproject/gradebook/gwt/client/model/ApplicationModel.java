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


public class ApplicationModel extends EntityModel {

	private static final long serialVersionUID = 1L;
	
	private List<GradebookModel> gradebookModels;
	private String placementId;
	private String helpUrl;
	private List<GradeType> enabledGradeTypes;
	private CategoryType categoryType;
		
	public ApplicationModel() {
		
	}
	
	public List<GradebookModel> getGradebookModels() {
		return get(ApplicationKey.GRADEBOOKMODELS.name());
	}

	public void setGradebookModels(List<GradebookModel> gradebookModels) {
		set(ApplicationKey.GRADEBOOKMODELS.name(), gradebookModels);
	}
	
	public String getPlacementId() {
		return get(ApplicationKey.PLACEMENTID.name());
	}

	public void setPlacementId(String placementId) {
		set(ApplicationKey.PLACEMENTID.name(), placementId);
	}

	@Override
	public String getDisplayName() {
		
		return "Gradebook Tool";
	}

	@Override
	public String getIdentifier() {
		
		return getPlacementId();
	}

	public String getHelpUrl() {
		return get(ApplicationKey.HELPURL.name());
	}

	public void setHelpUrl(String helpUrl) {
		set(ApplicationKey.HELPURL.name(), helpUrl);
	}

	public List<GradeType> getEnabledGradeTypes() {
		return get(ApplicationKey.ENABLEDGRADETYPES.name());
	}

	public void setEnabledGradeTypes(List<GradeType> enabledGradeTypes) {
		set(ApplicationKey.ENABLEDGRADETYPES.name(), enabledGradeTypes);
	}
	
}
