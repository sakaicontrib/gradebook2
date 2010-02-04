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
		
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getId()
	 */
	public String getId() {
		return get(StatisticsKey.ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setId(java.lang.String)
	 */
	public void setId(String id) {
		set(StatisticsKey.ID.name(), id);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getName()
	 */
	public String getName() {
		return get(StatisticsKey.NAME.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setName(java.lang.String)
	 */
	public void setName(String name) {
		set(StatisticsKey.NAME.name(), name);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getMean()
	 */
	public String getMean() {
		return get(StatisticsKey.MEAN.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setMean(java.lang.String)
	 */
	public void setMean(String mean) {
		set(StatisticsKey.MEAN.name(), mean);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getMedian()
	 */
	public String getMedian() {
		return get(StatisticsKey.MEDIAN.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setMedian(java.lang.String)
	 */
	public void setMedian(String median) {
		set(StatisticsKey.MEDIAN.name(), median);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getMode()
	 */
	public String getMode() {
		return get(StatisticsKey.MODE.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setMode(java.lang.String)
	 */
	public void setMode(String mode) {
		set(StatisticsKey.MODE.name(), mode);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getRank()
	 */
	public String getRank() {
		return get(StatisticsKey.RANK.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setRank(java.lang.String)
	 */
	public void setRank(String rank) {
		set(StatisticsKey.RANK.name(), rank);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getStandardDeviation()
	 */
	public String getStandardDeviation() {
		return get(StatisticsKey.STANDARD_DEVIATION.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setStandardDeviation(java.lang.String)
	 */
	public void setStandardDeviation(String sd) {
		set(StatisticsKey.STANDARD_DEVIATION.name(), sd);
	}

	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return get(StatisticsKey.ID.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getAssignmentId()
	 */
	public String getAssignmentId() {
		return get(StatisticsKey.ASSIGN_ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setAssignmentId(java.lang.String)
	 */
	public void setAssignmentId(String id) {
		set(StatisticsKey.ASSIGN_ID.name(), id);
	}

	public int compareTo(StatisticsModel o) {
		
		if (o != null && o.getAssignmentId() != null && getAssignmentId() != null)
		{
			return getAssignmentId().compareTo(o.getAssignmentId()); 
		}
		return -1; 
	}
	
}
