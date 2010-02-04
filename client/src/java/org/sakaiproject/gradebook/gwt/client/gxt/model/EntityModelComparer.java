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

import com.extjs.gxt.ui.client.data.ModelComparer;
import com.extjs.gxt.ui.client.data.ModelData;

public class EntityModelComparer<M extends ModelData> implements ModelComparer<M> {

	private String key;
	
	public EntityModelComparer(String key) {
		this.key = key;
	}
	
	public boolean equals(M m1, M m2) {
		if (m1 == null && m2 == null)
			return true;
		else if (m1 == null)
			return false;
		else if (m2 == null)
			return false;
			
		Object id1 = m1.get(key);
		Object id2 = m2.get(key);
		
		if (id1 != null && id2 != null)
			return id1.equals(id2);
		
		return false;
	}

}
