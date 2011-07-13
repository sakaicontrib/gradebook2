package org.sakaiproject.gradebook.gwt.client.gxt.model;

import org.sakaiproject.gradebook.gwt.client.gxt.JsonUtil;
import org.sakaiproject.gradebook.gwt.client.model.ImportSettings;
import org.sakaiproject.gradebook.gwt.client.model.key.UploadKey;

public class ImportSettingsImpl extends EntityModel implements ImportSettings {

	
	private static final long serialVersionUID = 43434L;

	
	public ImportSettingsImpl() {
		super();
		
		setScantron(false);
		setForceOverwriteAssignments(false);
	}
	
	public ImportSettingsImpl(EntityModel e) {
		super(e.getOverlay());
		
		 //TODO: remove below cuz it is only there to make me feel safe, I think.
		setScantron(isScantron());
		setForceOverwriteAssignments(isForceOverwriteAssignments());
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.ImportSetting#isScantron()
	 */
	public Boolean isScantron() {
		return get(UploadKey.B_SCNTRN.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.ImportSetting#setScantron(boolean)
	 */
	public void setScantron(boolean scantron) {
		set(UploadKey.B_SCNTRN.name(),scantron);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.ImportSetting#isForceOverwriteAssignments()
	 */
	public Boolean isForceOverwriteAssignments() {
		return get(UploadKey.B_OVRWRT.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.ImportSetting#setForceOverwriteAssignments(boolean)
	 */
	public void setForceOverwriteAssignments(boolean forceOverwriteAssignments) {
		set(UploadKey.B_OVRWRT.name(),forceOverwriteAssignments);
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
		set(UploadKey.S_MXPNTS.name(),scantronMaxPoints);
	}
	
}
