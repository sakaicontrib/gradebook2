package org.sakaiproject.gradebook.gwt.client.gxt.event;

import com.extjs.gxt.ui.client.store.Record;

public class GradeMapUpdate {

	public Record record;
	public Object value, startValue;
	
	public GradeMapUpdate(Record record, Object value, Object startValue) {
		this.record = record;
		this.value = value;
		this.startValue = startValue;
	}
	
}
