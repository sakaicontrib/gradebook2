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
package org.sakaiproject.gradebook.gwt.sakai.mock;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2AuthzImpl;

public class Gradebook2AuthzMockImpl extends Gradebook2AuthzImpl {

	private boolean isStartUp = false;
	
	public Gradebook2AuthzMockImpl() {
		
	}

	@Override
	public boolean isAdminUser() {

		String roleProperty = System.getProperty("gb2.role");

		if (roleProperty != null && roleProperty.equals("admin")) 
			return true;

		return false;
	}

	@Override
	protected String getSiteContext() {

		return AppConstants.TEST_SITE_CONTEXT_ID;
	}
	
	@Override
	protected boolean hasPermission(String gradebookUid, String permission) {
		// When we're starting up we need all the permissions
		if (isStartUp()) 
			return true;
		
		String roleProperty = System.getProperty("gb2.role");
			
		if (roleProperty == null || roleProperty.equals("anonymous")) 
			return false;
			
		if (roleProperty.equals("ta"))
			return AppConstants.PERMISSION_GRADE_SECTION.equals(permission);
			
		if (roleProperty.equals("student")) 
			return AppConstants.PERMISSION_VIEW_OWN_GRADES.equals(permission);
			
		return true;
	}
	
	@Override
	public boolean isUserTAinSection(String sectionUid) {
		
		String roleProperty = System.getProperty("gb2.role");

		if (roleProperty.equals("ta")) {

			String sectionPrefix = SiteMock.GROUP_ID_PREFIX;
			
			if(null != sectionUid && sectionUid.contains(sectionPrefix)) {
				return true;
			}
			else if(null != sectionUid && sectionUid.equals(AppConstants.TEST_SITE_ID)) {
				return true;
			}
		}

		return false;
	}

	public boolean isStartUp() {
		return isStartUp;
	}

	public void setStartUp(boolean isStartUp) {
		this.isStartUp = isStartUp;
	}
		
}
