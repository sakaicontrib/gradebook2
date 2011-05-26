/**********************************************************************************
*
* Copyright (c) 2008, 2009, 2010, 2011 The Regents of the University of California
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

package org.sakaiproject.gradebook.gwt.client.gxt.model;

import java.util.Comparator;

import org.sakaiproject.gradebook.gwt.client.model.Statistics;

public class StatisticsComparator implements Comparator<Statistics> {

	public int compare(Statistics arg0, Statistics arg1) {
		
		if (arg0 != null && arg1 != null) {
			
			String id1 = arg0.getAssignmentId();
			String id2 = arg1.getAssignmentId();
			
			if (null != id1 && null != id2) {
				
				return id1.compareTo(id2); 
			}
		}
		return -1;
	}
}
