package org.sakaiproject.gradebook2.test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import junit.framework.TestCase;

import org.sakaiproject.gradebook.gwt.client.exceptions.BusinessRuleException;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;
import org.sakaiproject.gradebook.gwt.sakai.BusinessLogicImpl;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2Security;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2SecurityImpl;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ServiceImpl;
import org.sakaiproject.gradebook.gwt.sakai.GradebookToolService;
import org.sakaiproject.gradebook.gwt.sakai.SampleInstitutionalAdvisor;
import org.sakaiproject.gradebook.gwt.sakai.UserRecord;
import org.sakaiproject.gradebook.gwt.sakai.calculations.GradeCalculationsOOImpl;
import org.sakaiproject.gradebook.gwt.sakai.mock.AuthnMock;
import org.sakaiproject.gradebook.gwt.sakai.mock.AuthzMock;
import org.sakaiproject.gradebook.gwt.sakai.mock.GradebookToolServiceMock;
import org.sakaiproject.gradebook.gwt.sakai.mock.IocMock;
import org.sakaiproject.gradebook.gwt.sakai.mock.SectionAwarenessMock;
import org.sakaiproject.gradebook.gwt.sakai.mock.SiteMock;
import org.sakaiproject.gradebook.gwt.sakai.model.UserDereference;
import org.sakaiproject.section.api.SectionAwareness;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.tool.gradebook.Gradebook;

import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class Gradebook2ServiceWeightedCategoriesTest extends TestCase {

	private static final String GRADEBOOK_UID = "12312409345";
	private Gradebook2ServiceImpl service;
	private ItemModel category;
	private GradebookModel gbModel;
	
	public Gradebook2ServiceWeightedCategoriesTest(String name) {
		super(name);
		service = new Gradebook2ServiceImpl() {
			@Override
			protected String lookupDefaultGradebookUid() {
				return GRADEBOOK_UID;
			}
			
			@Override
			protected String getSiteContext() {
				return "blah";
			}
			
			@Override
			protected Site getSite() {
				return new SiteMock("mock");
			}
			
			private List<UserDereference> dereferences;
			private final int DEFAULT_NUMBER_TEST_LEARNERS = 200;
			
			protected List<UserRecord> findLearnerRecordPage(Gradebook gradebook, Site site, String[] realmIds, List<String> groupReferences, 
					Map<String, Group> groupReferenceMap, String sortField, String searchField, String searchCriteria,
					int offset, int limit, 
					boolean isAscending) {
				
				List<UserRecord> userRecords = null;
				if (userRecords == null) {
					if (dereferences == null)
						findAllUserDereferences();
					
					userRecords = new ArrayList<UserRecord>(DEFAULT_NUMBER_TEST_LEARNERS);
					for (int i=offset;i<offset+limit;i++) {
						UserDereference dereference = dereferences.get(i);
						UserRecord userRecord = new UserRecord(dereference.getUserUid(), dereference.getEid(), dereference.getDisplayId(), dereference.getDisplayName(),
								dereference.getLastNameFirst(), dereference.getSortName(), dereference.getEmail());
						userRecord.setExportUserId(getExportUserId(dereference));
						userRecord.setFinalGradeUserId(getFinalGradeUserId(dereference));
						userRecords.add(userRecord);
					}
				}
				
				return userRecords;
			}
			
			
			public List<UserDereference> findAllUserDereferences() {
				
				if (dereferences == null) {
					dereferences = new ArrayList<UserDereference>(DEFAULT_NUMBER_TEST_LEARNERS);
					for (int i=0;i<DEFAULT_NUMBER_TEST_LEARNERS;i++) {
						dereferences.add(createUserRecord());
					}
				}
				
				return dereferences;
			}
			
			
			/*
			 * TEST DATA
			 */
			private final String[] FIRST_NAMES = { "Joel", "John", "Kelly",
				"Freeland", "Bruce", "Rajeev", "Thomas", "Jon", "Mary", "Jane",
				"Susan", "Cindy", "Veronica", "Shana", "Shania", "Olin", "Brenda",
				"Lowell", "Doug", "Yiyun", "Xi-Ming", "Grady", "Martha", "Stewart", 
				"Kennedy", "Joseph", "Iosef", "Sean", "Timothy", "Paula", "Keith",
				"Ignatius", "Iona", "Owen", "Ian", "Ewan", "Rachel", "Wendy", 
				"Quentin", "Nancy", "Mckenna", "Kaylee", "Aaron", "Erin", "Maris", 
				"D.", "Quin", "Tara", "Moira", "Bristol" };

			private  final String[] LAST_NAMES = { "Smith", "Paterson",
				"Haterson", "Raterson", "Johnson", "Sonson", "Paulson", "Li",
				"Yang", "Redford", "Shaner", "Bradley", "Herzog", "O'Neil", "Williams",
				"Simone", "Oppenheimer", "Brown", "Colgan", "Frank", "Grant", "Klein",
				"Miller", "Taylor", "Schwimmer", "Rourer", "Depuis", "Vaugh", "Auerbach", 
				"Shannon", "Stepford", "Banks", "Ashby", "Lynne", "Barclay", "Barton",
				"Cromwell", "Dering", "Dunlevy", "Ethelstan", "Fry", "Gilly",
				"Goodrich", "Granger", "Griffith", "Herbert", "Hurst", "Keigwin", 
				"Paddock", "Pillings", "Landon", "Lawley", "Osborne", "Scarborough",
				"Whiting", "Wibert", "Worth", "Tremaine", "Barnum", "Beal", "Beers", 
				"Bellamy", "Barnwell", "Beckett", "Breck", "Cotesworth", 
				"Coventry", "Elphinstone", "Farnham", "Ely", "Dutton", "Durham",
				"Eberlee", "Eton", "Edgecomb", "Eastcote", "Gloucester", "Lewes", 
				"Leland", "Mansfield", "Lancaster", "Oakham", "Nottingham", "Norfolk",
				"Poole", "Ramsey", "Rawdon", "Rhodes", "Riddell", "Vesey", "Van Wyck",
				"Van Ness", "Twickenham", "Trowbridge", "Ames", "Agnew", "Adlam", 
				"Aston", "Askew", "Alford", "Bedeau", "Beauchamp" };
			
			private final String[] SECTIONS = { "001", "002", "003", "004" };
			
			private Random random = new Random();
			
			private int getRandomInt(int max) {
				return random.nextInt(max);
			}
			
			private String getRandomSection() {
				return SECTIONS[getRandomInt(SECTIONS.length)];
			}
			
			private UserDereference createUserRecord() {
				String studentId = String.valueOf(100000 + getRandomInt(899999));
				String firstName = FIRST_NAMES[getRandomInt(FIRST_NAMES.length)];
				String lastName = LAST_NAMES[getRandomInt(LAST_NAMES.length)];
				String eid = lastName.toLowerCase();
				String lastNameFirst = lastName + ", " + firstName;
				String sortName = lastName.toUpperCase() + "  " + firstName.toUpperCase();
				String displayName = firstName + " " + lastName;
				String section = getRandomSection();
				String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@nowhere.edu";
			
				UserDereference userRecord = new UserDereference(studentId, eid, studentId, displayName,
						lastNameFirst, sortName, email);
				//userRecord.setSectionTitle("Section " + section);
				//userRecord.setExportUserId(studentId);
				
				return userRecord;
			}
		};
		
		IocMock.getInstance().registerClassInstance(Gradebook2ServiceImpl.class.getName(), service);
		
		GradebookToolService gbService = new GradebookToolServiceMock();		
		SectionAwareness sectionAwareness = new SectionAwarenessMock();
		
		BusinessLogicImpl businessLogic = new BusinessLogicImpl();
		businessLogic.setGbService(gbService);
		
		Gradebook2Security security = new Gradebook2SecurityImpl();
		//security.setAuthz(new AuthzMock(sectionAwareness));
		//security.setAuthn(new AuthnMock());
		security.setSectionAwareness(sectionAwareness);
		//security.setGbToolService(gbService);
		
		service.setAdvisor(new SampleInstitutionalAdvisor());
		service.setBusinessLogic(businessLogic);
		service.setGbService(gbService);
		service.setGradeCalculations(new GradeCalculationsOOImpl());
		service.setSecurity(security);
	}

	/*
	 * Set a user with all perfect grades and then override his/her course grade to an "F". Then remove that override.
	 */
	public void testAddAndRemoveOverrideGrade() throws Exception {
		
		int numberOfRows = 19;
		
		PagingLoadConfig config = new BasePagingLoadConfig(0, numberOfRows);
		PagingLoadResult<StudentModel> learnerResult = service.getStudentRows(gbModel.getGradebookUid(), gbModel.getGradebookId(), config, Boolean.TRUE);
		
		assertNotNull(learnerResult);
		
		List<StudentModel> learners = learnerResult.getData();
		assertEquals(numberOfRows, learners.size());
		
		StudentModel firstLearner = learners.get(0);
		
		assertNull(firstLearner.getStudentGrade());
		
		List<String> itemIds = new ArrayList<String>(); 
		StudentModel updatedRecord = null;
		
		int count = 0;
		// Assign all grades to 100
		for (ItemModel child : category.getChildren()) {
			itemIds.add(child.getIdentifier());
		
			Double previousValue = firstLearner.get(child.getIdentifier());
			updatedRecord = service.scoreNumericItem(gbModel.getGradebookUid(), firstLearner, child.getIdentifier(), Double.valueOf(100d), previousValue);
		
			count++;
			
			if (count == 4) 
				assertEquals("A+ (100.00%) ", updatedRecord.getStudentGrade());
			else
				assertEquals("A+ (100.00%) ***", updatedRecord.getStudentGrade());
		}

		String previousValue = updatedRecord.get(StudentModel.Key.GRADE_OVERRIDE.name());
		
		updatedRecord = service.scoreTextItem(gbModel.getGradebookUid(), firstLearner, StudentModel.Key.GRADE_OVERRIDE.name(), "F", previousValue);
		
		assertEquals("F (override)", updatedRecord.getStudentGrade());
		
		previousValue = updatedRecord.get(StudentModel.Key.GRADE_OVERRIDE.name());
		
		updatedRecord = service.scoreTextItem(gbModel.getGradebookUid(), firstLearner, StudentModel.Key.GRADE_OVERRIDE.name(), null, previousValue);
		
		assertEquals("A+ (100.00%) ", updatedRecord.getStudentGrade());
	}
	
	
	
	/*
	 * Set item points to 100
	 */
	public void testSetItemPointsTo100() throws Exception {
		Double result = testSetItemPointsTo(Double.valueOf(100d));
		assertEquals(Double.valueOf(100d), result);
	}
	
	/*
	 * Set item points to 0
	 */
	public void testSetItemPointsTo0() throws Exception {
		Double result = testSetItemPointsTo(Double.valueOf(0d));
		assertEquals(Double.valueOf(0d), result);
	}
	
	/*
	 * Set item points to 50
	 */
	public void testSetItemPointsTo50() throws Exception {
		Double result = testSetItemPointsTo(Double.valueOf(50d));
		assertEquals(Double.valueOf(50d), result);
	}
	
	/*
	 * Set item points to 50
	 */
	public void testSetItemPointsToNegative100() throws Exception {
		Double result = testSetItemPointsTo(Double.valueOf(-100d));
		assertEquals(Double.valueOf(-100d), result);
	}
	
	
	/*
	 * Test category add business rule #2
	 * 
	 * (2) new category name must not duplicate an existing category name
	 */
	public void testAddDuplicateCategoryName() throws Exception {
		ItemModel essaysCategory = new ItemModel();
		essaysCategory.setName("My Essays");
		essaysCategory.setPercentCourseGrade(Double.valueOf(60d));
		essaysCategory.setDropLowest(Integer.valueOf(0));
		essaysCategory.setEqualWeightAssignments(Boolean.TRUE);
		essaysCategory.setItemType(Type.CATEGORY);
		essaysCategory.setIncluded(Boolean.TRUE);
		
		boolean isExceptionThrown = false;
		try {
			service.addItemCategory(gbModel.getGradebookUid(), gbModel.getGradebookId(), essaysCategory);
		} catch (BusinessRuleException bre) {
			isExceptionThrown = true;
		}
	
		assertTrue(isExceptionThrown);
	}
	
	/*
	 * Test item add business rule #4
	 * 
	 * (4) new item name must not duplicate an active (removed = false) item name in the same category, otherwise throw exception
	 */
	public void testAddDuplicateItemNameWithinCategory() throws Exception {
		
		ItemModel essay1 = new ItemModel();
		essay1.setName("Essay 1");
		essay1.setPoints(Double.valueOf(20d));
		essay1.setDueDate(new Date());
		essay1.setCategoryId(category.getCategoryId());
		essay1.setReleased(Boolean.TRUE);
		essay1.setItemType(Type.ITEM);
		essay1.setIncluded(Boolean.TRUE);
	
		boolean isExceptionThrown = false;
		
		try {
			service.createItem(gbModel.getGradebookUid(), gbModel.getGradebookId(), essay1, true);
		} catch (BusinessRuleException bre) {
			isExceptionThrown = true;
		}
		
		assertTrue(isExceptionThrown);
		
	}
	
	
	/*
	 * Test item add business rule #5
	 * 
	 * (5) if item is "included" and category has "equal weighting" then recalculate all item weights for this category
	 * (6) item must include a valid category id
	 */
	public void testRecalculateWeightOnNewIncludedItem() throws Exception {
		
		ItemModel item = new ItemModel();
		item.setName("Essay 5");
		item.setPoints(Double.valueOf(20d));
		item.setDueDate(new Date());
		item.setCategoryId(category.getCategoryId());
		item.setReleased(Boolean.TRUE);
		item.setItemType(Type.ITEM);
		item.setIncluded(Boolean.TRUE);

		ItemModel parent = service.createItem(gbModel.getGradebookUid(), gbModel.getGradebookId(), item, false);
		
		int numberOfItems = 0;
		for (ItemModel c : parent.getChildren()) {		
			assertEquals(BigDecimal.valueOf(20.0000d).setScale(4, RoundingMode.HALF_EVEN), BigDecimal.valueOf(c.getPercentCategory().doubleValue()).setScale(4, RoundingMode.HALF_EVEN));
			assertTrue(c.getIncluded());
			numberOfItems++;
		}
		
		assertEquals(5, numberOfItems);
	}
	
	
	
	/*
	 * Test item update business rule #1
	 * 
	 * (1) If points is null, set points to 100
	 */
	public void testSetItemPointsNull() throws Exception {
		
		Double result = testSetItemPointsTo(null);
		assertEquals(Double.valueOf(100d), result);
	}
	
	
	/*
	 * Test item update business rule #2
	 * 
	 * (2) If weight is null, set weight to be equivalent to points value -- needs to happen after #1
	 */
	public void testSetItemWeightNull() throws Exception {
		// Grab first item from category
		ItemModel item = null;
		for (ItemModel child : category.getChildren()) {
			item = child;
			break;
		}
		
		item.setPercentCategory(null);
		
		ItemModel parent = service.updateItemModel(item);
		
		for (ItemModel c : parent.getChildren()) {
			if (c.isActive()) {
				assertEquals(Double.valueOf(20d), c.getPercentCategory());
			}
		}	
	}
	
	
	/*
	 * Test item update business rule #5
	 * 
	 * (5) new item name must not duplicate an active (removed = false) item name in the same category, otherwise throw exception
	 */
	public void testUpdateDuplicateItemNameInCategory() throws Exception {
		
		ItemModel item = getFirstItemInCategory(category);
		assertEquals("Essay 1", item.getName());
		
		item.setName("Essay 2");
		
		boolean isExceptionThrown = false;
		
		try {
			service.updateItemModel(item);
		} catch (BusinessRuleException bre) {
			isExceptionThrown = true;
		}
		
		assertTrue(isExceptionThrown);
	}
	
	/*
	 * Test item update business rule #6 (part a)
	 * 
	 * (6a) must not include an item in grading that has been deleted (removed = true) 
	 */
	public void testIncludeDeletedItem() throws Exception {
		// Grab first item from category
		ItemModel item = getFirstItemInCategory(category);
		
		item.setRemoved(Boolean.TRUE);
		
		ItemModel activeItem = getActiveItem(service.updateItemModel(item));
		assertTrue(activeItem.getRemoved());
		
		activeItem.setIncluded(Boolean.TRUE);
		
		boolean isExceptionThrown = false;
		
		try {
			activeItem = getActiveItem(service.updateItemModel(item));
		} catch (BusinessRuleException bre) {
			isExceptionThrown = true;
		}
		
		assertTrue(isExceptionThrown);
	}
	
	/*
	 * Test item update business rule #6 (part b)
	 * 
	 * (6b) must not include an item in grading that has a category that has been deleted (removed = true)
	 */
	// FIXME: This is really an anomolous case that may not need testing, since the item itself will not appear after it's category has been deleted
	public void testIncludeItemFromDeletedCategory() throws Exception {
		// First, ensure that category is deleted
		category.setRemoved(Boolean.TRUE);
		ItemModel item = getFirstItemInCategory(category);
		
		ItemModel deletedCategory = getActiveItem(service.updateItemModel(category));

		//assertFalse(item.getIncluded());
		item.setIncluded(Boolean.TRUE);

		boolean isExceptionThrown = false;
		
		try {
			getActiveItem(service.updateItemModel(item));
		} catch (BusinessRuleException bre) {
			isExceptionThrown = true;
		}
		
		assertTrue(isExceptionThrown);
	}
	
	
	/*
	 * Test item update business rule #
	 * 
	 * (7) if item is "included" and category has "equal weighting" then recalculate all item weights for this category
	 */
	public void testRecalculateItemWeightsOnIncludedOrUnincludedItem() throws Exception {
		
		// Grab first item from category
		ItemModel item = getFirstItemInCategory(category);
		assertTrue(item.getIncluded());
		item.setIncluded(Boolean.FALSE);

		ItemModel parent = service.updateItemModel(item);
		
		for (ItemModel c : parent.getChildren()) {
			if (!c.isActive()) {
				assertEquals(BigDecimal.valueOf(33.3333d).setScale(4, RoundingMode.HALF_EVEN), BigDecimal.valueOf(c.getPercentCategory().doubleValue()).setScale(4, RoundingMode.HALF_EVEN));
				assertTrue(c.getIncluded());
			} else {
				assertEquals(BigDecimal.valueOf(25.0000d).setScale(4, RoundingMode.HALF_EVEN), BigDecimal.valueOf(c.getPercentCategory().doubleValue()).setScale(4, RoundingMode.HALF_EVEN));
				assertFalse(c.getIncluded());
			}
		}
	}
	
	/*
	 * Test item update business rule #8
	 * 
	 * (8) item must include a valid category id
	 */
	public void testItemMustIncludeCategoryId() throws Exception {
		// Grab first item from category
		ItemModel item = getFirstItemInCategory(category);
		assertNotNull(item.getCategoryId());
		item.setCategoryId(null);

		boolean isExceptionThrown = false;
		
		try {
			getActiveItem(service.updateItemModel(item));
		} catch (BusinessRuleException bre) {
			isExceptionThrown = true;
		}
		
		assertTrue(isExceptionThrown);
	}

	
	/*
	 * Test item update business rule #9
	 * 
	 * (9) if category has changed, then if the old category had equal weighting and the item was included in that category, then recalculate all item weights for that category
	 */
	public void testOldCategoryOfMovedItemWithEqualWeightsMustBeRecalculated() throws Exception {
		// Make sure that the test category has equal weight items
		assertTrue(category.getEqualWeightAssignments());
		
		// Create a new target category to move the first item to
		ItemModel hwCategory = new ItemModel();
		hwCategory.setName("Homework");
		hwCategory.setPercentCourseGrade(Double.valueOf(60d));
		hwCategory.setDropLowest(Integer.valueOf(0));
		hwCategory.setEqualWeightAssignments(Boolean.TRUE);
		hwCategory.setItemType(Type.CATEGORY);
		hwCategory.setIncluded(Boolean.TRUE);
		
		ItemModel destinationCategory = getActiveItem(service.addItemCategory(gbModel.getGradebookUid(), gbModel.getGradebookId(), hwCategory));
		
		// Grab first item from category
		ItemModel item = getFirstItemInCategory(category);
		assertNotNull(item.getCategoryId());
		
		// Move the item to the destinationCategory
		item.setCategoryId(destinationCategory.getCategoryId());

		ItemModel movedItem = getActiveItem(service.updateItemModel(item));
		
		// Make sure it got correctly moved
		assertNotNull(movedItem);
		assertNotNull(movedItem.getCategoryId());
		assertNotNull(destinationCategory.getCategoryId());
		assertEquals(movedItem.getCategoryId(), destinationCategory.getCategoryId());
		
		// Now grab the old category again
		GradebookModel freshInstanceOfGradebookModel = service.getGradebook(gbModel.getGradebookUid());
		
		boolean foundCategory = false;
		// Search for the old category in the fresh gradebook model instance
		for (ItemModel child : freshInstanceOfGradebookModel.getGradebookItemModel().getChildren()) {
			if (child.getCategoryId().equals(category.getCategoryId())) {
				for (ItemModel c : child.getChildren()) {
					assertEquals(BigDecimal.valueOf(33.3333d).setScale(4, RoundingMode.HALF_EVEN), BigDecimal.valueOf(c.getPercentCategory().doubleValue()).setScale(4, RoundingMode.HALF_EVEN));
					assertTrue(c.getIncluded());
				}
				
				foundCategory = true;
			}
		}
		
		assertTrue(foundCategory);
		
	}
	
	
	/*
	 * Test item update business rule #10
	 * 
	 * (10) if item weight changes then remove the equal weighting flag (set to false) for the owning category
	 * 	   	 
	 */
	public void testRemoveCategoryEqualWeightWhenItemWeightChanges() throws Exception {
		// Make sure that the test category has equal weight items
		assertTrue(category.getEqualWeightAssignments());
		ItemModel item = getFirstItemInCategory(category);
		item.setPercentCategory(Double.valueOf(100d));
		
		ItemModel categoryItemModel = service.updateItemModel(item);
		ItemModel updatedItem = getActiveItem(categoryItemModel);
		
		assertEquals(Type.CATEGORY, categoryItemModel.getItemType());
		assertEquals(categoryItemModel.getCategoryId(), updatedItem.getCategoryId());
		assertEquals(Double.valueOf(100d), updatedItem.getPercentCategory());
		assertFalse(categoryItemModel.getEqualWeightAssignments());
		assertEquals(Double.valueOf(175d), categoryItemModel.getPercentCategory());
	}
	
	
	/*
	 * Test item update business rule #11
	 * 
	 * (11) if category is not included, then cannot include item
	 */
	public void testIncludeItemFromUnincludedCategory() throws Exception {
		// First, ensure that category is included
		assertTrue(category.getIncluded());
		category.setIncluded(Boolean.FALSE);
		
		ItemModel unincludedCategory = getActiveItem(service.updateItemModel(category));
		assertFalse(unincludedCategory.getIncluded());
		
		ItemModel item = getFirstItemInCategory(unincludedCategory);
		
		assertFalse(item.getIncluded());
		item.setIncluded(Boolean.TRUE);

		boolean isExceptionThrown = false;
		
		try {
			getActiveItem(service.updateItemModel(item));
		} catch (BusinessRuleException bre) {
			isExceptionThrown = true;
		}
		
		assertTrue(isExceptionThrown);
	}
	
	
	/*
	 * Test item update business rule #12
	 * 
	 * (12) if category is removed, then cannot unremove item
	 */
	// FIXME: This is really an anomolous case that may not need testing, since the item itself will not appear after it's category has been deleted
	public void testUnremoveItemFromRemovedCategory() throws Exception {
		// First, ensure that category is not removed
		assertFalse(category.getRemoved());
		// Remove it
		category.setRemoved(Boolean.TRUE);
		
		ItemModel removedCategory = getActiveItem(service.updateItemModel(category));
		assertNull(removedCategory);
		
		/*
		assertTrue(removedCategory.getRemoved());
		
		ItemModel item = getFirstItemInCategory(removedCategory);
		
		assertTrue(item.getRemoved());
		item.setRemoved(Boolean.FALSE);

		boolean isExceptionThrown = false;
		
		try {
			getActiveItem(service.updateItemModel(item));
		} catch (BusinessRuleException bre) {
			isExceptionThrown = true;
		}
		
		assertTrue(isExceptionThrown);*/
	}
	
	
	/*
	 * Test add category business rule #1 (on every setup)
	 * (1) if no other categories exist, then make the category weight 100%
	 * 
	 */
	protected void setUp() throws Exception {
		super.setUp();
		gbModel = service.getGradebook(GRADEBOOK_UID);
		
		ItemModel gradebookItemModel = gbModel.getGradebookItemModel();
		gradebookItemModel.setGradeType(GradeType.PERCENTAGES);
		gradebookItemModel.setCategoryType(CategoryType.WEIGHTED_CATEGORIES);
		service.updateItemModel(gradebookItemModel);
		
		
		String gradebookUid = gbModel.getGradebookUid();
		Long gradebookId = gbModel.getGradebookId();
		
		ItemModel essaysCategory = new ItemModel();
		essaysCategory.setName("My Essays");
		essaysCategory.setPercentCourseGrade(Double.valueOf(100d));
		essaysCategory.setDropLowest(Integer.valueOf(0));
		essaysCategory.setEqualWeightAssignments(Boolean.TRUE);
		essaysCategory.setItemType(Type.CATEGORY);
		essaysCategory.setIncluded(Boolean.TRUE);
		ItemModel addedCategory = getActiveItem(service.addItemCategory(gradebookUid, gradebookId, essaysCategory));
		
		assertEquals(Type.CATEGORY, addedCategory.getItemType());
		assertEquals(Double.valueOf(100d), addedCategory.getPercentCourseGrade());
		
		ItemModel essay1 = new ItemModel();
		essay1.setName("Essay 1");
		essay1.setPoints(Double.valueOf(20d));
		essay1.setDueDate(new Date());
		essay1.setCategoryId(addedCategory.getCategoryId());
		essay1.setReleased(Boolean.TRUE);
		essay1.setItemType(Type.ITEM);
		essay1.setIncluded(Boolean.TRUE);
		service.createItem(gradebookUid, gradebookId, essay1, true);
		
		ItemModel essay2 = new ItemModel();
		essay2.setName("Essay 2");
		essay2.setPoints(Double.valueOf(20d));
		essay2.setDueDate(new Date());
		essay2.setCategoryId(addedCategory.getCategoryId());
		essay2.setReleased(Boolean.TRUE);
		essay2.setItemType(Type.ITEM);
		essay2.setIncluded(Boolean.TRUE);
		service.createItem(gradebookUid, gradebookId, essay2, true);
		
		ItemModel essay3 = new ItemModel();
		essay3.setName("Essay 3");
		essay3.setPoints(Double.valueOf(20d));
		essay3.setDueDate(new Date());
		essay3.setCategoryId(addedCategory.getCategoryId());
		essay3.setReleased(Boolean.TRUE);
		essay3.setItemType(Type.ITEM);
		essay3.setIncluded(Boolean.TRUE);
		service.createItem(gradebookUid, gradebookId, essay3, true);
		
		ItemModel essay4 = new ItemModel();
		essay4.setName("Essay 4");
		essay4.setPoints(Double.valueOf(20d));
		essay4.setDueDate(new Date());
		essay4.setCategoryId(addedCategory.getCategoryId());
		essay4.setReleased(Boolean.TRUE);
		essay4.setItemType(Type.ITEM);
		essay4.setIncluded(Boolean.TRUE);
	
		category = service.createItem(gradebookUid, gradebookId, essay4, true);
		
		for (ItemModel child : category.getChildren()) {
			Double percentCategory = child.getPercentCategory();
			BigDecimal pC = BigDecimal.valueOf(percentCategory.doubleValue());
			
			assertTrue(pC.setScale(2).compareTo(BigDecimal.valueOf(25.0)) == 0);
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	private Double testSetItemPointsTo(Double value) throws Exception {
		
		// Grab first item from category
		ItemModel item = getFirstItemInCategory(category);
		
		item.setPoints(value);

		ItemModel activeItem = getActiveItem(service.updateItemModel(item));
		assertNotNull(activeItem);
		
		return activeItem.getPoints();
	}
	
	private ItemModel getActiveItem(ItemModel parent) {
		if (parent.isActive())
			return parent;
		
		for (ItemModel c : parent.getChildren()) {
			if (c.isActive()) {
				return c;
			}
			
			if (c.getChildCount() > 0) {
				ItemModel activeItem = getActiveItem(c);
				
				if (activeItem != null)
					return activeItem;
			}
		}
		
		return null;
	}

	private ItemModel getFirstItemInCategory(ItemModel category) {
		for (ItemModel child : category.getChildren()) {
			return child;
		}
		
		return null;
	}
	
}
