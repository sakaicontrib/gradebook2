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
package org.sakaiproject.gradebook.gwt.sakai;

import java.io.Serializable;
import java.util.Comparator;

import org.sakaiproject.tool.gradebook.AssignmentGradeRecord;

public class AssignmentComparator implements Comparator<UserRecord>, Serializable {

	private static final long serialVersionUID = 1L;
	private Long assignmentId;
	private boolean isDesc;
	
	public AssignmentComparator(Long assignmentId, boolean isDesc) {
		this.assignmentId = assignmentId;
		this.isDesc = isDesc;
	}
	
	public int compare(UserRecord o1, UserRecord o2) {
		
		if (o2.getGradeRecordMap() == null) 
			return isDesc ? 1 : -1;
		if (o1.getGradeRecordMap() == null)
			return isDesc ? -1 : 1;
		
		AssignmentGradeRecord r1 = o1.getGradeRecordMap().get(assignmentId);
		AssignmentGradeRecord r2 = o2.getGradeRecordMap().get(assignmentId);
		
		if (r2 == null)
			return isDesc ? 1 : -1;
		if (r1 == null)
			return isDesc ? -1 : 1;
		
		// FIXME: Will this work for letter grades?
		Double p1 = r1.getPointsEarned();
		Double p2 = r2.getPointsEarned();
		
		if (p2 == null)
			return isDesc ? 1 : -1;
		if (p1 == null)
			return isDesc ? -1 : 1;
		
		return p1.compareTo(p2);
	}

}
