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

public class StudentModel extends EntityModel implements Comparable<StudentModel> {
	
	public enum Group {
		STUDENT_INFORMATION("Student Information"),
		GRADES("Grades"), 
		ASSIGNMENTS("Assignments");
		
		private String displayName;
		
		private Group(String displayName) {
			this.displayName = displayName;
		}
		
		public String getDisplayName() {
			return displayName;
		}
	};
	
	public enum Key { 
		UID(Group.STUDENT_INFORMATION, ""), 
		DISPLAY_ID(Group.STUDENT_INFORMATION, "Id"), 
		DISPLAY_NAME(Group.STUDENT_INFORMATION, "Display Name"), 
		SORT_NAME(Group.STUDENT_INFORMATION, "Sort Name"), 
		EMAIL(Group.STUDENT_INFORMATION, "Email"), 
		SECTION(Group.STUDENT_INFORMATION, "Section"), 
		COURSE_GRADE(Group.GRADES, "Course Grade"), 
		GRADE_OVERRIDE(Group.GRADES, "Grade Override"), 
		ASSIGNMENT(Group.ASSIGNMENTS, "");
	
		private Group group;
		private String displayName;
	
		private Key(Group group, String displayName) {
			this.group = group;
			this.displayName = displayName;
		}
	
		public Group getGroup() {
			return group;
		}
		
		public String getDisplayName() {
			return displayName;
		}
	
	};

	public static final String DROP_FLAG = ":D";
	public static final String GRADED_FLAG = ":G";
	public static final String COMMENTED_FLAG = ":C";
	
	private static final long serialVersionUID = 1L;

	public StudentModel() {
		super();
	}
	
	public StudentModel(Map<String, Object> properties) {
		super(properties);
	}
	
	public String getIdentifier() {
		return get(Key.UID.name());
	}

	public void setIdentifier(String id) {
		set(Key.UID.name(), id);
	}
	
	public String getDisplayName() {
		return get(Key.DISPLAY_NAME.name());
	}
	
	public String getStudentName()
	{
		return get(Key.DISPLAY_NAME.name());
	}
	
	public void setStudentName(String studentName)
	{
		set(Key.DISPLAY_NAME.name(), studentName);
	}

	public String getStudentDisplayId()
	{
		return get(Key.DISPLAY_ID.name());
	}
	
	public void setStudentDisplayId(String studentDisplayId)
	{
		set(Key.DISPLAY_ID.name(), studentDisplayId);
	}
	
	public String getStudentEmail()
	{
		return get(Key.EMAIL.name());
	}
	
	public void setStudentEmail(String studentEmail)
	{
		set(Key.EMAIL.name(), studentEmail);
	}

	public String getStudentSections()
	{
		return get(Key.SECTION.name());
	}
	
	public void setStudentSections(String studentSections)
	{
		set(Key.SECTION.name(), studentSections);
	}

	public String getStudentGrade()
	{
		return get(Key.COURSE_GRADE.name());
	}
	
	public void setStudentGrade(String studentGrade)
	{
		set(Key.COURSE_GRADE.name(), studentGrade);
	}

	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StudentModel) {
			StudentModel other = (StudentModel)obj;
		
			return getIdentifier().equals(other.getIdentifier());
		}
		return false;
	}

	public int compareTo(StudentModel o) {
		return getIdentifier().compareTo(o.getIdentifier());
	}
	
}
