package org.sakaiproject.gradebook.gwt.client.model;

import java.util.List;

public interface Roster {

	public abstract List<Learner> getLearnerPage();
	
	public abstract void setLearnerPage(List<Learner> learners);
	
	public abstract Integer getTotal();
	
	public abstract void setTotal(Integer total);
	
}
