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

package org.sakaiproject.gradebook.gwt.client.gxt.model;

import org.sakaiproject.gradebook.gwt.client.model.Statistics;
import org.sakaiproject.gradebook.gwt.client.model.key.StatisticsKey;

public class StatisticsModel extends EntityModel implements Comparable<StatisticsModel>, Statistics {

	private static final long serialVersionUID = 1L;

	public StatisticsModel() {
		super();
	}
	
	public StatisticsModel(EntityOverlay overlay) {
		super(overlay);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getId()
	 */
	public String getId() {
		return get(StatisticsKey.S_ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setId(java.lang.String)
	 */
	public void setId(String id) {
		set(StatisticsKey.S_ID.name(), id);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getName()
	 */
	public String getName() {
		return get(StatisticsKey.S_NM.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setName(java.lang.String)
	 */
	public void setName(String name) {
		set(StatisticsKey.S_NM.name(), name);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getMean()
	 */
	public String getMean() {
		return get(StatisticsKey.S_MEAN.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setMean(java.lang.String)
	 */
	public void setMean(String mean) {
		set(StatisticsKey.S_MEAN.name(), mean);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getMedian()
	 */
	public String getMedian() {
		return get(StatisticsKey.S_MEDIAN.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setMedian(java.lang.String)
	 */
	public void setMedian(String median) {
		set(StatisticsKey.S_MEDIAN.name(), median);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getMode()
	 */
	public String getMode() {
		return get(StatisticsKey.S_MODE.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setMode(java.lang.String)
	 */
	public void setMode(String mode) {
		set(StatisticsKey.S_MODE.name(), mode);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getRank()
	 */
	public String getRank() {
		return get(StatisticsKey.S_RANK.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setRank(java.lang.String)
	 */
	public void setRank(String rank) {
		set(StatisticsKey.S_RANK.name(), rank);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getStandardDeviation()
	 */
	public String getStandardDeviation() {
		return get(StatisticsKey.S_STD_DEV.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setStandardDeviation(java.lang.String)
	 */
	public void setStandardDeviation(String sd) {
		set(StatisticsKey.S_STD_DEV.name(), sd);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getAssignmentId()
	 */
	public String getAssignmentId() {
		return get(StatisticsKey.S_ITEM_ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setAssignmentId(java.lang.String)
	 */
	public void setAssignmentId(String id) {
		set(StatisticsKey.S_ITEM_ID.name(), id);
	}

	public int compareTo(StatisticsModel o) {
		
		if (o != null && o.getAssignmentId() != null && getAssignmentId() != null)
		{
			return getAssignmentId().compareTo(o.getAssignmentId()); 
		}
		return -1; 
	}

	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		   if (obj == this) { return true; }
		   if (obj.getClass() != getClass()) {
		     return false;
		   }
		   StatisticsModel rhs = (StatisticsModel) obj;
		   return getAssignmentId().equals(rhs.getAssignmentId());
	}
}
