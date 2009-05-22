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

public abstract class ItemEntityModel extends EntityModel {

	private static final long serialVersionUID = 1L;

	public enum Key {
		ID("Id"), NAME("Name"), WEIGHT("Weight"), EQUAL_WEIGHT("Equal Weight Items"), EXTRA_CREDIT("Extra Credit"), 
		INCLUDED("Include in Grade"), REMOVED("Delete"), GRADEBOOK("Gradebook"), DROP_LOWEST("Drop Lowest"), 
		CATEGORY_NAME("Category"), CATEGORY_ID("Category Id"), DUE_DATE("Due Date"), POINTS("Points"), RELEASED("Is Released"), SOURCE("Source");
		
		private String propertyName;
		
		private Key(String propertyName) {
			this.propertyName = propertyName;
		}
		
		public String getPropertyName() {
			return propertyName;
		}
	};

	public ItemEntityModel() {
		super();
	}

	public ItemEntityModel(Map<String, Object> properties) {
		super(properties);
	}
	
	@Override
	public String getDisplayName() {
		return get(Key.NAME.name());
	}
	
	public static String getPropertyName(String property) {
		Key key = getProperty(property);
		
		return getPropertyName(key);
	}
	
	public static String getPropertyName(Key key) {
		if (key == null)
			return "";
		
		return key.getPropertyName();
	}

	public static Key getProperty(String key) {
		try {
			return Key.valueOf(key);
		} catch (IllegalArgumentException iae) {
			// Don't need to log this.
		}
		return null;
	}
	
	public String getIdentifier() {
		return get(Key.ID.name());
	}

	public void setIdentifier(String id) {
		set(Key.ID.name(), id);
	}

	public String getName() {
		return get(Key.NAME.name());
	}

	public void setName(String name) {
		set(Key.NAME.name(), name);
	}

	public Double getWeighting() {
		return get(Key.WEIGHT.name());
	}

	public void setWeighting(Double weighting) {
		set(Key.WEIGHT.name(), weighting);
	}

	public Boolean getExtraCredit() {
		return get(Key.EXTRA_CREDIT.name());
	}

	public void setExtraCredit(Boolean extraCredit) {
		set(Key.EXTRA_CREDIT.name(), extraCredit);
	}

	public Boolean getIncluded() {
		return get(Key.INCLUDED.name());
	}

	public void setIncluded(Boolean included) {
		set(Key.INCLUDED.name(), included);
	}

	public Boolean getRemoved() {
		return get(Key.REMOVED.name());
	}

	public void setRemoved(Boolean removed) {
		set(Key.REMOVED.name(), removed);
	}


}
