package org.sakaiproject.gradebook.gwt.client.gxt.event;

import org.sakaiproject.gradebook.gwt.client.model.ItemModel;

import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;

public class ItemUpdate {

	public Store store;
	public Record record;
	public ItemModel item;
	public String property;
	public Object oldValue;
	public Object value;
	
	public ItemUpdate(Store store, ItemModel item) {
		this.item = item;
		this.store = store;
		this.record = store.getRecord(item);
	}
	
	public ItemUpdate(Store store, ItemModel item, String property, Object oldValue, Object value) {
		this.item = item;
		this.property = property;
		this.oldValue = oldValue;
		this.value = value;
		this.record = store.getRecord(item);
		this.store = store;
	}
	
	public ItemUpdate(Record record, String property, Object oldValue, Object value) {
		this.record = record;
		this.property = property;
		this.oldValue = oldValue;
		this.value = value;
	}
	
}
