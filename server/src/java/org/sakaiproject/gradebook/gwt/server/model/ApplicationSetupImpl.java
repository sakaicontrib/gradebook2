package org.sakaiproject.gradebook.gwt.server.model;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.model.ApplicationSetup;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.key.ApplicationKey;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;

public class ApplicationSetupImpl extends BaseModel implements
		ApplicationSetup {

	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Application#getEnabledGradeTypes()
	 */
	public List<GradeType> getEnabledGradeTypes() {
		List<GradeType> enabledGradeTypes = new ArrayList<GradeType>();
		List<String> gradeTypes = get(ApplicationKey.V_ENBLD_GRD_TYPES.name());
		for (String gradeType : gradeTypes) {
			enabledGradeTypes.add(GradeType.valueOf(gradeType));	
		}
		
		return enabledGradeTypes;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Application#getGradebookModels()
	 */
	public List<Gradebook> getGradebookModels() {
		return get(ApplicationKey.A_GB_MODELS.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Application#getHelpUrl()
	 */
	public String getHelpUrl() {
		return get(ApplicationKey.S_HELPURL.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Application#getIdentifier()
	 */
	public String getIdentifier() {
		return getPlacementId();
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Application#getPlacementId()
	 */
	public String getPlacementId() {
		return get(ApplicationKey.S_PLACE_ID.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Application#setEnabledGradeTypes(java.util.List)
	 */
	public void setEnabledGradeTypes(List<GradeType> enabledGradeTypes) {
		List<String> gradeTypes = new ArrayList<String>();
		for (GradeType gradeType : enabledGradeTypes) {
			gradeTypes.add(gradeType.name());	
		}
		
		set(ApplicationKey.V_ENBLD_GRD_TYPES.name(), gradeTypes);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Application#setGradebookModels(java.util.List)
	 */
	public void setGradebookModels(List<Gradebook> gradebookModels) {
		set(ApplicationKey.A_GB_MODELS.name(), gradebookModels);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Application#setHelpUrl(java.lang.String)
	 */
	public void setHelpUrl(String helpUrl) {
		set(ApplicationKey.S_HELPURL.name(), helpUrl);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.Application#setPlacementId(java.lang.String)
	 */
	public void setPlacementId(String placementId) {
		set(ApplicationKey.S_PLACE_ID.name(), placementId);
	}

	public void setShowWeightedEnabled(boolean isShowWeightedEnabled) {
		set(ApplicationKey.S_SH_WTD_ENABLED.name(), Boolean.toString(isShowWeightedEnabled));
	}
	
	public boolean isShowWeightedEnabled() {
		return ("true".equals(get(ApplicationKey.S_SH_WTD_ENABLED.name())));
	}
}
