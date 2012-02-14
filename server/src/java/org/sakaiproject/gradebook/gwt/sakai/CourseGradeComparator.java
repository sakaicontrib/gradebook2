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

import java.math.BigDecimal;

public class CourseGradeComparator extends EnteredGradeComparator {

	public CourseGradeComparator(boolean isDesc) {
		super(isDesc);
	}

	public int compare(UserRecord o1, UserRecord o2) {

		int result = super.compare(o1, o2);

		if (result != 0)
			return result;

		BigDecimal b1 = getCalculatedGrade(o1);
		BigDecimal b2 = getCalculatedGrade(o2);
		
		if (b2 == null) 
			return isDesc ? 1 : -1;
		if (b1 == null)
			return isDesc ? -1 : 1;

		return b2.compareTo(b1);
	}

	protected String getLetterGrade(UserRecord record) {

		if (null == record || null == record.getDisplayGrade())
			return null;

		return record.getDisplayGrade().getLetterGrade();
	}
	
	protected BigDecimal getCalculatedGrade(UserRecord record) {
		if (null == record || null == record.getDisplayGrade())
			return null;

		return record.getDisplayGrade().getCalculatedGrade();
	}
	
}
