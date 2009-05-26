package org.sakaiproject.gradebook.gwt.client.model;


public class SubmissionVerificationModel extends EntityModel {

	private static final long serialVersionUID = 1L;

	private int numberOfLearners;
	private boolean isMissingScores;
	
	public SubmissionVerificationModel() {
		
	}
	
	public SubmissionVerificationModel(int numberOfLearners, boolean isMissingScores) {
		this.numberOfLearners = numberOfLearners;
		this.isMissingScores = isMissingScores;
	}


	public int getNumberOfLearners() {
		return numberOfLearners;
	}


	public void setNumberOfLearners(int numberOfLearners) {
		this.numberOfLearners = numberOfLearners;
	}


	public boolean isMissingScores() {
		return isMissingScores;
	}


	public void setMissingScores(boolean isMissingScores) {
		this.isMissingScores = isMissingScores;
	}


	@Override
	public String getDisplayName() {
		return null;
	}


	@Override
	public String getIdentifier() {
		return "SUBMISSIONVERIFICATION";
	}
	
	
}
