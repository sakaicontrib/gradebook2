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

public class EnteredGradeComparator implements Comparator<UserRecord>, Serializable {

	private static final long serialVersionUID = 1L;
	protected boolean isDesc;

	public EnteredGradeComparator(boolean isDesc) {
		this.isDesc = isDesc;
	}

	public int compare(UserRecord o1, UserRecord o2) {

		String d1 = getLetterGrade(o1);
		String d2 = getLetterGrade(o2);

		if (d2 == null) 
			return isDesc ? 1 : -1;
		if (d1 == null)
			return isDesc ? -1 : 1;

		// Let's assume we want the first character (the letter grade) to count most, followed by
		// the +/-, and tie breakers are based on the actual numeric score

		char c0_1 = d1.charAt(0);
		char c0_2 = d2.charAt(0);

		int diff = c0_1 - c0_2;

		if (diff != 0)
			return c0_1<c0_2 ? -1 : (c0_1==c0_2 ? 0 : 1);

		if (d1.length() <= 0 || d2.length() <= 0)
			return c0_1<c0_2 ? -1 : (c0_1==c0_2 ? 0 : 1);

		char c1_1 = 'y';
		char c1_2 = 'y';

		if (d1.length() > 1)
			c1_1 = d1.charAt(1);

		if (d2.length() > 1)
			c1_2 = d2.charAt(1);

		char s1 = getCompareChar(c1_1);
		char s2 = getCompareChar(c1_2);

		return s1<s2 ? -1 : (s1==s2 ? 0 : 1);
	}

	protected String getLetterGrade(UserRecord record) {

		if (null == record || null == record.getCourseGradeRecord())
			return null;

		return record.getCourseGradeRecord().getEnteredGrade();
	}

	protected char getCompareChar(char c) {

		char s1 = 'y';

		switch (c) {
			case '+':
				s1 = 'x';
				break;
			case ' ':
				s1 = 'y';
				break;
			case '-':
				s1 = 'z';
				break;
		}

		return s1;
	}
}
