package org.sakaiproject.gradebook.gwt.client.gxt.event;

import org.sakaiproject.gradebook.gwt.client.model.StudentModel;

import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;

public class GradeRecordUpdate {

	public Record record;
	public StudentModel learner;
	public String property;
	public String label;
	public Object oldValue;
	public Object value;
	
	public GradeRecordUpdate(Store store, StudentModel learner, String property, String label, Object oldValue, Object value) {
		this.learner = learner;
		this.property = property;
		this.label = label;
		this.oldValue = oldValue;
		this.value = value;
		this.record = store.getRecord(learner);
	}
	
	public GradeRecordUpdate(Record record, String property, String label, Object oldValue, Object value) {
		this.record = record;
		this.property = property;
		this.label = label;
		this.oldValue = oldValue;
		this.value = value;
	}
	
}
