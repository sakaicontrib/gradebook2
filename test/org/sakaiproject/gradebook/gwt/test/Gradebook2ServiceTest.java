package org.sakaiproject.gradebook.gwt.test;

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

public class Gradebook2ServiceTest extends TestCase {

	private static final String GRADEBOOK_UID = "12312409345";
	private Gradebook2ServiceImpl service;
	private ItemModel category;
	private GradebookModel gbModel;
	
	public Gradebook2ServiceTest(String name) {
		super(name);
		service = new Gradebook2ServiceImpl() {
			@Override
			protected String getGradebookUid() {
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
		security.setAuthz(new AuthzMock(sectionAwareness));
		security.setAuthn(new AuthnMock());
		security.setSectionAwareness(sectionAwareness);
		security.setGbService(gbService);
		
		service.setAdvisor(new SampleInstitutionalAdvisor());
		service.setBusinessLogic(businessLogic);
		service.setGbService(gbService);
		service.setGradeCalculations(new GradeCalculationsOOImpl());
		service.setSecurity(security);
	}

	
	/*
	 * Tests item update business rule #1
	 * 
	 * (1) If points is null, set points to 100
	 */
	public void testSetItemPointsNull() throws Exception {
		
		// Grab first item from category
		ItemModel item = null;
		for (ItemModel child : category.getChildren()) {
			item = child;
			break;
		}
		
		item.setPoints(null);
		
		ItemModel parent = service.updateItemModel(item);
		
		for (ItemModel c : parent.getChildren()) {
			if (c.isActive()) {
				assertEquals(Double.valueOf(100d), c.getPoints());
			}
			
		}
		
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
	public void testCreateDuplicateItemNameInCategory() throws Exception {
		
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
	 * Test item update business rule #6
	 * 
	 * (6) must not include an item in grading that has been deleted (removed = true) or that has a category that has been deleted (removed = true)
	 */
	public void testIncludeDeletedItemFromDeletedCategory() throws Exception {
		// Grab first item from category
		ItemModel item = null;
		for (ItemModel child : category.getChildren()) {
			item = child;
			break;
		}
		
		item.setRemoved(Boolean.TRUE);
		
		ItemModel parent = service.updateItemModel(item);
		
		for (ItemModel c : parent.getChildren()) {
			if (c.isActive()) {
				assertTrue(c.getRemoved());
				item = c;
			}
		}
		
		item.setIncluded(Boolean.TRUE);
		
		boolean isExceptionThrown = false;
		
		try {
			parent = service.updateItemModel(item);
		} catch (BusinessRuleException bre) {
			isExceptionThrown = true;
		}
		
		assertTrue(isExceptionThrown);
		
		// FIXME: Need to handle deleted category/included item case
	}
	
	/*
	 * Test item update business rule #
	 * 
	 * (7) if item is "included" and category has "equal weighting" then recalculate all item weights for this category
	 */
	public void testRecalculateItemWeightsOnIncludedOrUnincludedItem() throws Exception {
		
		// Grab first item from category
		ItemModel item = null;
		for (ItemModel child : category.getChildren()) {
			item = child;
			break;
		}
		
		item.setIncluded(Boolean.FALSE);
		
		ItemModel parent = service.updateItemModel(item);
		
		for (ItemModel c : parent.getChildren()) {
			if (!c.isActive()) {
				assertEquals(BigDecimal.valueOf(33.3333d).setScale(4, RoundingMode.HALF_EVEN), BigDecimal.valueOf(c.getPercentCategory().doubleValue()).setScale(4, RoundingMode.HALF_EVEN));
			}
		}
	}
	
	
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
		essaysCategory.setPercentCourseGrade(Double.valueOf(60d));
		essaysCategory.setDropLowest(Integer.valueOf(0));
		essaysCategory.setEqualWeightAssignments(Boolean.TRUE);
		essaysCategory.setItemType(Type.CATEGORY);
		essaysCategory.setIncluded(Boolean.TRUE);
		essaysCategory = service.addItemCategory(gradebookUid, gradebookId, essaysCategory);
		
		ItemModel essay1 = new ItemModel();
		essay1.setName("Essay 1");
		essay1.setPoints(Double.valueOf(20d));
		essay1.setDueDate(new Date());
		essay1.setCategoryId(essaysCategory.getCategoryId());
		essay1.setReleased(Boolean.TRUE);
		essay1.setItemType(Type.ITEM);
		essay1.setIncluded(Boolean.TRUE);
		service.createItem(gradebookUid, gradebookId, essay1, true);
		
		ItemModel essay2 = new ItemModel();
		essay2.setName("Essay 2");
		essay2.setPoints(Double.valueOf(20d));
		essay2.setDueDate(new Date());
		essay2.setCategoryId(essaysCategory.getCategoryId());
		essay2.setReleased(Boolean.TRUE);
		essay2.setItemType(Type.ITEM);
		essay2.setIncluded(Boolean.TRUE);
		service.createItem(gradebookUid, gradebookId, essay2, true);
		
		ItemModel essay3 = new ItemModel();
		essay3.setName("Essay 3");
		essay3.setPoints(Double.valueOf(20d));
		essay3.setDueDate(new Date());
		essay3.setCategoryId(essaysCategory.getCategoryId());
		essay3.setReleased(Boolean.TRUE);
		essay3.setItemType(Type.ITEM);
		essay3.setIncluded(Boolean.TRUE);
		service.createItem(gradebookUid, gradebookId, essay3, true);
		
		ItemModel essay4 = new ItemModel();
		essay4.setName("Essay 4");
		essay4.setPoints(Double.valueOf(20d));
		essay4.setDueDate(new Date());
		essay4.setCategoryId(essaysCategory.getCategoryId());
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

	
	
	
}
