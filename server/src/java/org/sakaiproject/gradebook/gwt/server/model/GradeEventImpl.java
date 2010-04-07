package org.sakaiproject.gradebook.gwt.server.model;

import org.sakaiproject.gradebook.gwt.client.model.GradeEvent;
import org.sakaiproject.gradebook.gwt.client.model.key.GradeEventKey;

public class GradeEventImpl extends BaseModel implements GradeEvent {

	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.GradeEvent#getIdentifier()
	 */
	public String getIdentifier() {
		return get(GradeEventKey.S_ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.GradeEvent#setIdentifier(java.lang.String)
	 */
	public void setIdentifier(String id) {
		set(GradeEventKey.S_ID.name(), id);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.GradeEvent#getGraderName()
	 */
	public String getGraderName() {
		return get(GradeEventKey.S_GRDR_NM.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.GradeEvent#setGraderName(java.lang.String)
	 */
	public void setGraderName(String graderName) {
		set(GradeEventKey.S_GRDR_NM.name(), graderName);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.GradeEvent#getGrade()
	 */
	public String getGrade() {
		return get(GradeEventKey.S_GRD.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.GradeEvent#setGrade(java.lang.String)
	 */
	public void setGrade(String grade) {
		set(GradeEventKey.S_GRD.name(), grade);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.GradeEvent#getDateGraded()
	 */
	public String getDateGraded() {
		return get(GradeEventKey.T_GRADED.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.gxt.model.GradeEvent#setDateGraded(java.lang.String)
	 */
	public void setDateGraded(String dateGraded) {
		set(GradeEventKey.T_GRADED.name(), dateGraded);
	}
	
}
