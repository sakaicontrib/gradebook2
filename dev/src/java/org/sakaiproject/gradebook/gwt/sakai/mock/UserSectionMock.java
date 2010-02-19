/**********************************************************************************
 *
 * $Id: UserSectionMock.java 63685 2009-09-30 01:33:01Z jlrenfro@ucdavis.edu $
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

import org.sakaiproject.section.api.coursemanagement.User;

public class UserSectionMock implements User {
	
	String uid;
	String displayId;
	String displayName;
	String sortName;
	
	public UserSectionMock(String uid, String displayId, String displayName, String sortName) {
		this.uid = uid;
		this.displayId = displayId;
		this.displayName = displayName;
		this.sortName = sortName;
	}

	public String getDisplayId() {
		// TODO Auto-generated method stub
		return displayId;
	}

	public String getDisplayName() {
		// TODO Auto-generated method stub
		return displayName;
	}

	public String getSortName() {
		// TODO Auto-generated method stub
		return sortName;
	}

	public String getUserUid() {
		// TODO Auto-generated method stub
		return uid;
	}

}
