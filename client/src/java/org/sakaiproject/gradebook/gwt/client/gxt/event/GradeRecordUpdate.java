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

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;

public class GradeRecordUpdate {

	public Record record;
	public ModelData learner;
	public String property;
	public String label;
	public Object oldValue;
	public Object value;
	
	public GradeRecordUpdate(Store store, ModelData learner, String property, String label, Object oldValue, Object value) {
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
	
	public void onSuccess (GradeRecordUpdate update) {
		
	}
	public void onError (GradeRecordUpdate update) {
		
	}
	
	
}
