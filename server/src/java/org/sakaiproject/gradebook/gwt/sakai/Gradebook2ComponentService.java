package org.sakaiproject.gradebook.gwt.sakai;

import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;

public interface Gradebook2ComponentService {

	public Map<String, Object> assignComment(String assignmentId, String studentUid, String text);
	
	public Map<String, Object> assignScore(String gradebookUid, String studentUid, String assignmentId, Double value, Double previousValue) throws InvalidInputException;
	
	public Map<String, Object> assignScore(String gradebookUid, String studentUid, String assignmentId, String value, String previousValue) throws InvalidInputException;
	
}
