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

import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;

public class RefreshCourseGradesEvent extends BaseEvent {

	private ModelData student;
	private Long assignmentId;
	private LearnerKey key;
	private Object value;
	private String courseGrade;
	private boolean isSingleChange;
	
	public RefreshCourseGradesEvent() {
		super(null);
		this.isSingleChange = false;
	}

	public RefreshCourseGradesEvent(ModelData student, String courseGrade, Long assignmentId, Object value) {
		super(null);
		this.student = student;
		this.courseGrade = courseGrade;
		this.isSingleChange = true;
		this.assignmentId = assignmentId;
		this.value = value;
	}

	public ModelData getStudent() {
		return student;
	}

	public void setStudent(ModelData student) {
		this.student = student;
	}

	public String getCourseGrade() {
		return courseGrade;
	}

	public void setCourseGrade(String courseGrade) {
		this.courseGrade = courseGrade;
	}

	public boolean isSingleChange() {
		return isSingleChange;
	}

	public void setSingleChange(boolean isSingleChange) {
		this.isSingleChange = isSingleChange;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Long getAssignmentId() {
		return assignmentId;
	}

	public LearnerKey getKey() {
		return key;
	}
	
}
