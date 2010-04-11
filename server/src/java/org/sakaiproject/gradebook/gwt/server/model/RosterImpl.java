package org.sakaiproject.gradebook.gwt.server.model;

import java.util.List;
import java.util.HashMap;

import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.client.model.Roster;
import org.sakaiproject.gradebook.gwt.client.model.key.RosterKey;
import org.sakaiproject.gradebook.gwt.server.Util;


public class RosterImpl extends HashMap<String, Object> implements Roster {

	private static final long serialVersionUID = 1L;

	public RosterImpl(List<Learner> learners, Integer total) {
		super();
		setLearnerPage(learners);
		setTotal(total);
	}
	
	public List<Learner> getLearnerPage() {
		return (List<Learner>)get(RosterKey.A_PAGE.name());
	}

	public Integer getTotal() {
		return Util.toInteger(get(RosterKey.I_TOTAL.name()));
	}

	public void setLearnerPage(List<Learner> learners) {
		put(RosterKey.A_PAGE.name(), learners);
	}

	public void setTotal(Integer total) {
		put(RosterKey.I_TOTAL.name(), total);
	}

}
