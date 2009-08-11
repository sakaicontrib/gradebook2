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

import java.util.HashMap;
import java.util.Map;

public class IocMock {

	private static IocMock instance = null;
	private Map<String, Object> lookup = new HashMap<String, Object>();

	protected IocMock() { }

	public static IocMock getInstance() {

		if (instance == null) {
			instance = new IocMock();
		}
		return instance;
	}

	public Object getClassInstance(String className) {
		return lookup.get(className);
	}

	public void registerClassInstance(String className, Object object) {
		lookup.put(className, object);
	}

}
