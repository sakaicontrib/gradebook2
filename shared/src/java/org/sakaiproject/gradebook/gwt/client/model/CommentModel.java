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

	@Override
	public String getDisplayName() {
		return null;
	}

	@Override
	public String getIdentifier() {
		return get(CommentKey.ID.name());
	}

	public void setIdentifier(String id) {
		set(CommentKey.ID.name(), id);
	}

	public String getText() {
		return get(CommentKey.TEXT.name());
	}
	
	public void setText(String text) {
		set(CommentKey.TEXT.name(), text);
	}
	
	public String getStudentUid() {
		return get(CommentKey.STUDENT_UID.name());
	}
	
	public void setStudentUid(String studentUid) {
		set(CommentKey.STUDENT_UID.name(), studentUid);
	}
	
	public Long getAssignmentId() {
		return get(CommentKey.ASSIGNMENT_ID.name());
	}
	
	public void setAssignmentId(Long id) {
		set(CommentKey.ASSIGNMENT_ID.name(), id);
	}
	
	public String getGraderName() {
		return get(CommentKey.GRADER_NAME.name());
	}
	
	public void setGraderName(String graderName) {
		set(CommentKey.GRADER_NAME.name(), graderName);
	}
	
}
