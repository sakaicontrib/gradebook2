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
package org.sakaiproject.gradebook.gwt.client;

import com.google.gwt.user.client.Cookies;

public class PersistentStore {

	public static String getPersistentField(String gradebookUid, String storeId, String prefix) {
		String cookieField = getPersistentFieldName(gradebookUid, storeId, prefix);
		
		//System.out.println("Field returned: " + cookieField);
		return Cookies.getCookie(cookieField);
	}
	
	public static void storePersistentField(String gradebookUid, String storeId, String prefix, String fieldValue) {
		String cookieField = getPersistentFieldName(gradebookUid, storeId, prefix);
		
		//System.out.println("Field saved: " + cookieField);
		Cookies.setCookie(cookieField, fieldValue);
	}
	
	private static String getPersistentFieldName(String gradebookUid, String storeId, String prefix) {
		String storeValue = new StringBuilder("gb:").append(gradebookUid).append(":store:")
			.append(storeId).append(":prop:").append(prefix).toString();
	
		return storeValue;
	}
	
}
