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
package org.sakaiproject.gradebook.gwt.client.model;

import java.util.Map;
import com.extjs.gxt.ui.client.data.BaseModel;

public abstract class EntityModel extends BaseModel {

	private static final long serialVersionUID = 1L;


	public EntityModel() {
		super();
	}
	
	public EntityModel(Map<String, Object> properties) {
		super(properties);
	}

	public abstract String getIdentifier();
	
	public abstract String getDisplayName();
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EntityModel) {
			EntityModel other = (EntityModel) obj;

			if (getIdentifier() == null || other.getIdentifier() == null)
				return false;
			
			return getIdentifier().equals(other.getIdentifier());
		}
		return false;
	}
	
	 @Override
	 public int hashCode() {
		 String id = getIdentifier();
		 int hash = 0;
		 if (id != null) 
			 hash = id.hashCode();
		 return hash;
	 }
	 
}
