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

public class CommentModel extends EntityModel {

	private static final long serialVersionUID = 1L;

	public enum Key { ID, TEXT, STUDENT_UID, ASSIGNMENT_ID, GRADER_NAME };
	
	@Override
	public String getDisplayName() {
		return null;
	}

	@Override
	public String getIdentifier() {
		return get(Key.ID.name());
	}

	public void setIdentifier(String id) {
		set(Key.ID.name(), id);
	}

	public String getText() {
		return get(Key.TEXT.name());
	}
	
	public void setText(String text) {
		set(Key.TEXT.name(), text);
	}
	
	public String getStudentUid() {
		return get(Key.STUDENT_UID.name());
	}
	
	public void setStudentUid(String studentUid) {
		set(Key.STUDENT_UID.name(), studentUid);
	}
	
	public Long getAssignmentId() {
		return get(Key.ASSIGNMENT_ID.name());
	}
	
	public void setAssignmentId(Long id) {
		set(Key.ASSIGNMENT_ID.name(), id);
	}
	
	public String getGraderName() {
		return get(Key.GRADER_NAME.name());
	}
	
	public void setGraderName(String graderName) {
		set(Key.GRADER_NAME.name(), graderName);
	}
	
}
