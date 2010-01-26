package org.sakaiproject.gradebook.gwt.sakai;

import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;

public interface Gradebook2ComponentService {

	public Map<String, Object> assignComment(String assignmentId, String studentUid, String text);
	
	public Map<String, Object> assignScore(String gradebookUid, String studentUid, String assignmentId, Double value, Double previousValue) throws InvalidInputException;
	
	public Map<String, Object> assignScore(String gradebookUid, String studentUid, String assignmentId, String value, String previousValue) throws InvalidInputException;
	
	public Map<String, Object> createItem(String gradebookUid, Long gradebookId, Map<String, Object> attributes) throws InvalidInputException;
	
	public Map<String, Object> createPermission(String gradebookUid, Long gradebookId, Map<String, Object> attributes) throws InvalidInputException;
	
	public Map<String, Object> deletePermission(Map<String, Object> attributes);
	
	public Map<String, Object> getApplicationMap(String... gradebookUids);
	
	public List<Map<String,Object>> getAvailableGradeFormats(String gradebookUid, Long gradebookId);
	
	public List<Map<String,Object>> getGradeEvents(Long assignmentId, String studentUid);
	
	public List<Map<String,Object>> getGradeMaps(String gradebookUid);
	
	public List<Map<String, Object>> getGraders(String gradebookUid,
			Long gradebookId);
	
	public List<Map<String,Object>> getHistory(String gradebookUid, Long gradebookId,
			Integer offset, Integer limit);
	
	public List<Map<String,Object>> getItems(String gradebookUid, Long gradebookId, String type);
	
	public List<Map<String,Object>> getPermissions(String gradebookUid, Long gradebookId, String graderId);
	
	public List<Map<String,Object>> getVisibleSections(String gradebookUid, boolean enableAllSectionsEntry, String allSectionsEntryTitle);
	
	public void resetGradeMap(String gradebookUid);
	
	public Boolean updateConfiguration(Long gradebookId, String field, String value);
	
	public void updateGradeMap(String gradebookUid, String affectedLetterGrade, Object value) throws InvalidInputException;
	
	public Map<String, Object> updateItem(Map<String, Object> attributes) throws InvalidInputException;

}
