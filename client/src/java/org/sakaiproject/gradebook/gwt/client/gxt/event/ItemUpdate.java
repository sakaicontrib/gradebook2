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

package org.sakaiproject.gradebook.gwt.client.gxt.event;

import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.Item;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;

public class ItemUpdate {

	public enum UpdateType { FIELD, SETUP };
	
	public Store store;
	public Record record;
	public Item item;
	public String property;
	public Object oldValue;
	public Object value;
	public boolean close;
	public UpdateType updateType;
	
	public ItemUpdate(Store store, Record record, Item item, boolean close) {
		this.store = store;
		this.record = record;
		this.item = item;
		this.close = close;
	}
	
	public ItemUpdate(Store store, Item item) {
		this.item = item;
		this.store = store;
		this.record = store.getRecord((ModelData)item);
	}
	
	public ItemUpdate(Store store, Item item, String property, Object oldValue, Object value) {
		this.item = item;
		this.property = property;
		this.oldValue = oldValue;
		this.value = value;
		this.record = store.getRecord((ModelData)item);
		this.store = store;
	}
	
	public ItemUpdate(Record record, String property, Object oldValue, Object value) {
		this.record = record;
		this.property = property;
		this.oldValue = oldValue;
		this.value = value;
	}
	
	public Item getModifiedItem() {
		if (record != null) {
			return (ItemModel)record.getModel();
		}
		
		return item;
	}

	public UpdateType getUpdateType() {
		return updateType;
	}

	public void setUpdateType(UpdateType updateType) {
		this.updateType = updateType;
	}
	
}
