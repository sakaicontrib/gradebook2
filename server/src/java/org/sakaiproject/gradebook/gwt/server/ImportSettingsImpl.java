package org.sakaiproject.gradebook.gwt.server;


import java.util.List;

import org.sakaiproject.gradebook.gwt.client.BusinessLogicCode;
import org.sakaiproject.gradebook.gwt.client.model.ImportSettings;
import org.sakaiproject.gradebook.gwt.client.model.key.UploadKey;
import org.sakaiproject.gradebook.gwt.server.model.BaseModel;

public class ImportSettingsImpl extends BaseModel implements ImportSettings {
	
	private static final long serialVersionUID = 1334L;
	private List<BusinessLogicCode> ignoredBusinessRules;
	
	public ImportSettingsImpl () {
		super();
		setScantron(false);
		setForceOverwriteAssignments(false);
	}
	
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.ImportSetting#isScantron()
	 */
	public Boolean isScantron() {
		return Util.checkBoolean((Boolean)get(UploadKey.B_SCNTRN.name()));
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.ImportSetting#setScantron(boolean)
	 */
	public void setScantron(boolean scantron) {
		put(UploadKey.B_SCNTRN.name(),Boolean.valueOf(scantron));
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.ImportSetting#isForceOverwriteAssignments()
	 */
	public Boolean isForceOverwriteAssignments() {
		return Util.checkBoolean((Boolean)get(UploadKey.B_OVRWRT));
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.ImportSetting#setForceOverwriteAssignments(boolean)
	 */
	public void setForceOverwriteAssignments(boolean forceOverwriteAssignments) {
		put(UploadKey.B_OVRWRT.name(),Boolean.valueOf(forceOverwriteAssignments));
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.ImportSetting#getScantronMaxPoints()
	 */
	public String getScantronMaxPoints() {
		return get(UploadKey.S_MXPNTS.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.ImportSetting#setScantronMaxPoints(java.lang.String)
	 */
	public void setScantronMaxPoints(String scantronMaxPoints) {
		put(UploadKey.S_MXPNTS.name(),scantronMaxPoints);
	}


	public void setIgnoredBusinessRules(
			List<BusinessLogicCode> ignoredBusinessRules) {
		this.ignoredBusinessRules = ignoredBusinessRules;
		
	}


	public List<BusinessLogicCode> getIgnoredBusinessRules() {
		return ignoredBusinessRules;
	}
	

}
