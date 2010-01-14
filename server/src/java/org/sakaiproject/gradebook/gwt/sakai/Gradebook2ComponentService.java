package org.sakaiproject.gradebook.gwt.sakai;

import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;

public interface Gradebook2ComponentService {

	public Map<String, Object> assignComment(String assignmentId, String studentUid, String text);
	
	public Map<String, Object> assignScore(String gradebookUid, String studentUid, String assignmentId, Double value, Double previousValue) throws InvalidInputException;
	
	public Map<String, Object> assignScore(String gradebookUid, String studentUid, String assignmentId, String value, String previousValue) throws InvalidInputException;
	
	public Map<String, Object> createItem(String gradebookUid, Long gradebookId, Map<String, Object> attributes) throws InvalidInputException;
	
	public Map<String, Object> getApplicationMap(String... gradebookUids);
	
	public List<Map<String,Object>> getGradeEvents(Long assignmentId, String studentUid);
	
	public List<Map<String,Object>> getVisibleSections(String gradebookUid, boolean enableAllSectionsEntry, String allSectionsEntryTitle);
	
	public Boolean updateConfiguration(Long gradebookId, String field, String value);
	
	public Map<String, Object> updateItem(Map<String, Object> attributes) throws InvalidInputException;
	
}
