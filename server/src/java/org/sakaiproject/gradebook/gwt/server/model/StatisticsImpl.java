package org.sakaiproject.gradebook.gwt.server.model;

import org.sakaiproject.gradebook.gwt.client.model.Statistics;
import org.sakaiproject.gradebook.gwt.client.model.key.StatisticsKey;

public class StatisticsImpl extends BaseModel implements Statistics {

	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getAssignmentId()
	 */
	public String getAssignmentId() {
		return get(StatisticsKey.S_ITEM_ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getId()
	 */
	public String getId() {
		return get(StatisticsKey.S_ID.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getMean()
	 */
	public String getMean() {
		return get(StatisticsKey.S_MEAN.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getMedian()
	 */
	public String getMedian() {
		return get(StatisticsKey.S_MEDIAN.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getMode()
	 */
	public String getMode() {
		return get(StatisticsKey.S_MODE.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getName()
	 */
	public String getName() {
		return get(StatisticsKey.S_NM.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getRank()
	 */
	public String getRank() {
		return get(StatisticsKey.S_RANK.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#getStandardDeviation()
	 */
	public String getStandardDeviation() {
		return get(StatisticsKey.S_STD_DEV.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setAssignmentId(java.lang.String)
	 */
	public void setAssignmentId(String id) {
		set(StatisticsKey.S_ITEM_ID.name(), id);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setId(java.lang.String)
	 */
	public void setId(String id) {
		set(StatisticsKey.S_ID.name(), id);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setMean(java.lang.String)
	 */
	public void setMean(String mean) {
		set(StatisticsKey.S_MEAN.name(), mean);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setMedian(java.lang.String)
	 */
	public void setMedian(String median) {
		set(StatisticsKey.S_MEDIAN.name(), median);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setMode(java.lang.String)
	 */
	public void setMode(String mode) {
		set(StatisticsKey.S_MODE.name(), mode);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setName(java.lang.String)
	 */
	public void setName(String name) {
		set(StatisticsKey.S_NM.name(), name);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setRank(java.lang.String)
	 */
	public void setRank(String rank) {
		set(StatisticsKey.S_RANK.name(), rank);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Statistics#setStandardDeviation(java.lang.String)
	 */
	public void setStandardDeviation(String sd) {
		set(StatisticsKey.S_STD_DEV.name(), sd);
	}
	
}
