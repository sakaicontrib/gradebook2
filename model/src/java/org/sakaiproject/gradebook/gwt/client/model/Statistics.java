package org.sakaiproject.gradebook.gwt.client.model;

public interface Statistics {

	public abstract String getId();

	public abstract void setId(String id);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getMean();

	public abstract void setMean(String mean);

	public abstract String getMedian();

	public abstract void setMedian(String median);

	public abstract String getMode();

	public abstract void setMode(String mode);

	public abstract String getRank();

	public abstract void setRank(String rank);

	public abstract String getStandardDeviation();

	public abstract void setStandardDeviation(String sd);

	public abstract String getAssignmentId();

	public abstract void setAssignmentId(String id);

}