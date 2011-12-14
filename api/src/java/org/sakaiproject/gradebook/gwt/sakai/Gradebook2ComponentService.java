package org.sakaiproject.gradebook.gwt.sakai;

import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.BusinessLogicCode;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.exceptions.GradebookCreationException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidDataException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.exceptions.SecurityException;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationSetup;
import org.sakaiproject.gradebook.gwt.client.model.FinalGradeSubmissionResult;
import org.sakaiproject.gradebook.gwt.client.model.FinalGradeSubmissionStatus;
import org.sakaiproject.gradebook.gwt.client.model.GradeEvent;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.History;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.client.model.Permission;
import org.sakaiproject.gradebook.gwt.client.model.Roster;
import org.sakaiproject.gradebook.gwt.client.model.Statistics;
import org.sakaiproject.gradebook.gwt.client.model.Upload;
import org.sakaiproject.gradebook.gwt.sakai.InstitutionalAdvisor.Column;
import org.sakaiproject.gradebook.gwt.sakai.model.GradeItem;
import org.sakaiproject.gradebook.gwt.sakai.model.UserDereference;
import org.sakaiproject.site.api.Site;

public interface Gradebook2ComponentService {

	public Learner assignComment(String assignmentId, String studentUid, String text);
	
	public Learner assignExcused(String assignmentId, String studentUid, Boolean isExcused) throws InvalidInputException;
	
	public Learner assignScore(String gradebookUid, String studentUid, String assignmentId, Double value, Double previousValue) throws InvalidInputException;
	
	public Learner assignScore(String gradebookUid, String studentUid, String assignmentId, String value, String previousValue) throws InvalidInputException;
	
	public Item createItem(String gradebookUid, Long gradebookId, Item item, boolean enforceNoNewCategories) throws InvalidInputException;
	
	public Permission createPermission(String gradebookUid, Long gradebookId, Permission permissionRequest) throws SecurityException, InvalidInputException;
	
	public GradeItem decorateGradebook(GradeItem gradebookGradeItem, Long categoryId, Long assignmentId);
	
	public Permission deletePermission(String gradebookUid, Permission permissionDeleteRequest) throws SecurityException;
	
	public List<UserDereference> findAllUserDereferences();

	public String getExportUserId(UserDereference dereference);
	
	public ApplicationSetup getApplicationSetup(String... gradebookUids) throws GradebookCreationException;
	
	public String getAuthorizationDetails(String... gradebookUids);
	
	public List<Map<String,Object>> getAvailableGradeFormats(String gradebookUid, Long gradebookId);
	
	public Gradebook getGradebook(String uid);
	
	public List<GradeEvent> getGradeEvents(Long assignmentId, String studentUid);
	
	public List<Map<String,Object>> getGradeMaps(String gradebookUid) throws SecurityException;
	
	public List<Map<String, Object>> getGraders(String gradebookUid, Long gradebookId) throws SecurityException;
	
	public Map<String,Object> getGradesVerification(String gradebookUid, Long gradebookId) throws SecurityException;
	
	public History getHistory(String gradebookUid, Long gradebookId, Integer offset, Integer limit) throws SecurityException;
	
	public Item getItem(String gradebookUid, Long gradebookId, String type);
	
	public List<Item> getItems(String gradebookUid, Long gradebookId, String type);
	
	public Roster getRoster(String gradebookUid, Long gradebookId, Integer limit, Integer offset, List<String> sectionList, String searchString, String searchField, String sortField, boolean includeCMId, boolean isDescending, boolean isShowWeighted);
	
	public List<Permission> getPermissions(String gradebookUid, Long gradebookId, String graderId) throws SecurityException;
	
	
	public Site getSite();

	/*
	 * This method returns a two dimension array. Array index 0 returns the frequencies for positive number grades,
	 * and array index 1 returns the frequencies for negative number grades
	 * 
	 * @since 1.3.0
	 */
	public int[][] getGradeItemStatistics(String gradebookUid, Long assignmentId, String sectionId) throws SecurityException, InvalidDataException;
	
	/*
	 * @deprecated As of 1.3.0
	 * @see getGraderStatistics(String, Long, String)
	 * @see getLearnerStatistics(String, Long, String)
	 */
	@Deprecated 
	public List<Statistics> getStatistics(String gradebookUid, Long gradebookId, String studentId) throws SecurityException;
	
	/*
	 * @since 1.3.0
	 */
	public List<Statistics> getGraderStatistics(String gradebookUid, Long gradebookId, String sectionId) throws SecurityException;
	
	/*
	 * @since 1.3.0
	 */
	public List<Statistics> getLearnerStatistics(String gradebookUid, Long gradebookId, String learnerId) throws SecurityException;
	
	/*
	 * @since 1.5.0
	 */
	public Map<String, Integer> getCourseGradeStatistics(String gradebookUid) throws SecurityException, InvalidDataException;
	
	/*
	 * @since 1.6.0
	 */
	public Map<String, Integer> getCourseGradeStatistics(String gradebookUid, String sectionId) throws SecurityException, InvalidDataException;
	
	
	public List<Map<String,Object>> getVisibleSections(String gradebookUid, boolean enableAllSectionsEntry, String allSectionsEntryTitle);

	/**
	 * Does system config setting enable finding learners by user-id, email address, 
	 * last-name-first, etc, when using the search form in the instructor's view of grades?
	 * @return true if search-by-field is enabled, false otherwise.
	 */
	public boolean isSearchRosterByFieldEnabled();

	public boolean isValidLetterGrade(String letterGrade);

	public void postEvent(String message, String gradebookId, String... args);
	
	public void resetGradeMap(String gradebookUid) throws SecurityException;
	
	public FinalGradeSubmissionResult submitFinalGrade(List<Map<Column, String>> studentDataList, String gradebookUid);
	
	public Boolean updateConfiguration(Long gradebookId, String field, String value);
	
	public void updateGradeMap(String gradebookUid, String affectedLetterGrade, Object value) throws InvalidInputException, SecurityException;
	
	public Item updateItem(Item item) throws InvalidInputException;
	
	public Upload upload(String gradebookUid, Long gradebookId, Upload upload, boolean isDryRun) throws InvalidInputException;
	
	public void saveFullGradebookFromClientModel(Gradebook newGradebook) throws FatalException, InvalidInputException;
	
	public Upload upload(String gradebookUid, Long gradebookId,
			Upload upload, boolean isDryRun,
			List<BusinessLogicCode> ignoredBusinessRules) throws InvalidInputException;
	
	/*
	 * @since 1.7.0
	 * 
	 * Method that dispatches a final grade submission status request to the InstitutionalAdvisor 
	 */
	public FinalGradeSubmissionStatus getFinalGradeSubmissionStatus(String gradebookUid);
	
}
