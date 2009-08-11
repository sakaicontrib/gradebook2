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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CourseGradeComparator extends EnteredGradeComparator {

	private static final Log log = LogFactory.getLog(CourseGradeComparator.class);

	public CourseGradeComparator(boolean isDesc) {
		super(isDesc);
	}

	public int compare(UserRecord o1, UserRecord o2) {

		int result = super.compare(o1, o2);

		if (result != 0)
			return result;

		String d1 = getStringField(o1);
		String d2 = getStringField(o2);

		Double g1 = getNumericGrade(d1);
		Double g2 = getNumericGrade(d2);

		if (g2 == null) 
			return isDesc ? 1 : -1;
		if (g1 == null)
			return isDesc ? -1 : 1;

		return g2.compareTo(g1);
	}

	protected String getStringField(UserRecord record) {

		if (null == record)
			return null;

		return record.getDisplayGrade();
	}

	private Double getNumericGrade(String s) {
		int open = s.indexOf('(');
		int close = s.indexOf("%)");

		if (open == -1 || close == -1)
			return null;

		String num = s.substring(open+1, close);

		Double d = null;
		try {
			d = Double.valueOf(num);
		} catch (NumberFormatException nfe) {
			log.error("Caught a nfe on interpreting " + num + " as a double ");
		}
		return d;
	}


}
