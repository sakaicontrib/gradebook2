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
package org.sakaiproject.gradebook.gwt.client.gxt.model;

import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.model.GradeEvent;
import org.sakaiproject.gradebook.gwt.client.model.key.GradeEventKey;

public class GradeEventModel extends EntityModel implements GradeEvent {

	private static final long serialVersionUID = 1L;

	public GradeEventModel() {
		super();
	}
	
	public GradeEventModel(Map<String, Object> properties) {
		super(properties);
	}
	
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
