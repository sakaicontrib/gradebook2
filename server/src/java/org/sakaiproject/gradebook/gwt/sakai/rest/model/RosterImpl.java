package org.sakaiproject.gradebook.gwt.sakai.rest.model;

import java.util.List;
import java.util.HashMap;

import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.client.model.Roster;
import org.sakaiproject.gradebook.gwt.client.model.key.RosterKey;
import org.sakaiproject.gradebook.gwt.sakai.Util;


public class RosterImpl extends HashMap<String, Object> implements Roster {

	private static final long serialVersionUID = 1L;

	public RosterImpl(List<Learner> learners, Integer total) {
		super();
		setLearnerPage(learners);
		setTotal(total);
	}
	
	public List<Learner> getLearnerPage() {
		return (List<Learner>)get(RosterKey.LEARNER_PAGE.toString());
	}

	public Integer getTotal() {
		return Util.toInteger(get(RosterKey.TOTAL.toString()));
	}

	public void setLearnerPage(List<Learner> learners) {
		put(RosterKey.LEARNER_PAGE.toString(), learners);
	}

	public void setTotal(Integer total) {
		put(RosterKey.TOTAL.toString(), total);
	}

}
