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

import org.sakaiproject.gradebook.gwt.sakai.Gradebook2Authn;
import org.sakaiproject.tool.gradebook.facades.Authn;


public class AuthnMock implements Gradebook2Authn, Authn {

	public AuthnMock() {
		
	}
	
	public String getUserUid() {
		return "prof123";
	}

	public void setAuthnContext(Object arg0) {
		// TODO Auto-generated method stub
		
	}
}
