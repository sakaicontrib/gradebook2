package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.sakaiproject.gradebook.gwt.client.Gradebook2RPCService;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.exceptions.BusinessRuleException;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ResourceProducer;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2Security;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2SecurityImpl;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ServiceImpl;
import org.sakaiproject.gradebook.gwt.sakai.GradebookToolService;
import org.sakaiproject.gradebook.gwt.sakai.SampleInstitutionalAdvisor;
import org.sakaiproject.gradebook.gwt.sakai.UserRecord;
import org.sakaiproject.gradebook.gwt.sakai.calculations.GradeCalculationsOOImpl;
import org.sakaiproject.section.api.SectionAwareness;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.tool.gradebook.Gradebook;
import org.sakaiproject.user.api.User;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class Gradebook2ResourceProducerMock extends RemoteServiceServlet implements Gradebook2RPCService {

	private static final long serialVersionUID = 1L;
	
	private Gradebook2ResourceProducer producer;
	
	public void init() {
		Gradebook2ServiceImpl service = new Gradebook2ServiceImpl() {
			@Override
			protected String getGradebookUid() {
				return "12312409345";
			}
			
			@Override
			protected String getSiteContext() {
				return "blah";
			}
			
			@Override
			protected Site getSite() {
				return new SiteMock("mock");
			}
			
			private List<UserRecord> userRecords;
			private final int DEFAULT_NUMBER_TEST_LEARNERS = 200;
			
			protected List<UserRecord> findLearnerRecordPage(Gradebook gradebook, Site site, String[] realmIds, List<String> groupReferences, 
					Map<String, Group> groupReferenceMap, String sortField, String searchField, String searchCriteria,
					int offset, int limit, 
					boolean isAscending) {
				
				if (userRecords == null) {
					userRecords = new ArrayList<UserRecord>(2000);
					for (int i=0;i<DEFAULT_NUMBER_TEST_LEARNERS;i++) {
						userRecords.add(createUserRecord());
					}
				}
				
				return userRecords;
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
			
			private UserRecord createUserRecord() {
				String studentId = String.valueOf(100000 + getRandomInt(899999));
				String firstName = FIRST_NAMES[getRandomInt(FIRST_NAMES.length)];
				String lastName = LAST_NAMES[getRandomInt(LAST_NAMES.length)];
				String eid = lastName.toLowerCase();
				String lastNameFirst = lastName + ", " + firstName;
				String sortName = lastName.toUpperCase() + "  " + firstName.toUpperCase();
				String displayName = firstName + " " + lastName;
				String section = getRandomSection();
				String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@nowhere.edu";
			
				UserRecord userRecord = new UserRecord(studentId, eid, studentId, displayName,
						lastNameFirst, sortName, email);
				userRecord.setSectionTitle("Section " + section);
				
				return userRecord;
			}
		};
		
		IocMock.getInstance().registerClassInstance(Gradebook2ServiceImpl.class.getName(), service);
		
		GradebookToolService gbService = new GradebookToolServiceMock();		
		SectionAwareness sectionAwareness = new SectionAwarenessMock();
		
		Gradebook2Security security = new Gradebook2SecurityImpl();
		security.setAuthz(new AuthzMock(sectionAwareness));
		security.setAuthn(new AuthnMock());
		security.setSectionAwareness(sectionAwareness);
		security.setGbService(gbService);
		
		service.setAdvisor(new SampleInstitutionalAdvisor());
		service.setGbService(gbService);
		service.setGradeCalculations(new GradeCalculationsOOImpl());
		service.setSecurity(security);
		
		producer = new Gradebook2ResourceProducer();
		producer.setService(service);
		
		try {

			GradebookModel gbModel = service.getGradebook("emptyid");
			
			ItemModel gradebook = new ItemModel();
			gradebook.setName("Test Gradebook");
			gradebook.setCategoryType(CategoryType.WEIGHTED_CATEGORIES);
			gradebook.setGradeType(GradeType.PERCENTAGES);
			gradebook.setItemType(Type.GRADEBOOK);
			
			service.updateItemModel(gradebook);
			
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

			
			ItemModel hwCategory = new ItemModel();
			hwCategory.setName("My Homework");
			hwCategory.setPercentCourseGrade(Double.valueOf(60d));
			hwCategory.setDropLowest(Integer.valueOf(0));
			hwCategory.setEqualWeightAssignments(Boolean.TRUE);
			hwCategory.setItemType(Type.CATEGORY);
			hwCategory.setIncluded(Boolean.TRUE);
			hwCategory = service.addItemCategory(gradebookUid, gradebookId, hwCategory);
			
			ItemModel essay1 = new ItemModel();
			essay1.setName("Essay 1");
			essay1.setPoints(Double.valueOf(20d));
			essay1.setDueDate(new Date());
			essay1.setCategoryId(essaysCategory.getCategoryId());
			essay1.setReleased(Boolean.TRUE);
			essay1.setItemType(Type.ITEM);
			essay1.setIncluded(Boolean.TRUE);
			service.addItem(gradebookUid, gradebookId, essay1, true);
			
			ItemModel essay2 = new ItemModel();
			essay2.setName("Essay 2");
			essay2.setPoints(Double.valueOf(20d));
			essay2.setDueDate(new Date());
			essay2.setCategoryId(essaysCategory.getCategoryId());
			essay2.setReleased(Boolean.TRUE);
			essay2.setItemType(Type.ITEM);
			essay2.setIncluded(Boolean.TRUE);
			service.addItem(gradebookUid, gradebookId, essay2, true);
			
			ItemModel essay3 = new ItemModel();
			essay3.setName("Essay 3");
			essay3.setPoints(Double.valueOf(20d));
			essay3.setDueDate(new Date());
			essay3.setCategoryId(essaysCategory.getCategoryId());
			essay3.setReleased(Boolean.TRUE);
			essay3.setItemType(Type.ITEM);
			essay3.setIncluded(Boolean.TRUE);
			service.addItem(gradebookUid, gradebookId, essay3, true);
			

			ItemModel hw1 = new ItemModel();
			hw1.setName("HW 1");
			hw1.setPoints(Double.valueOf(10d));
			hw1.setDueDate(new Date());
			hw1.setCategoryId(hwCategory.getCategoryId());
			hw1.setItemType(Type.ITEM);
			hw1.setIncluded(Boolean.TRUE);
			hw1.setReleased(Boolean.FALSE);
			service.addItem(gradebookUid, gradebookId, hw1, true);
			
			ItemModel hw2 = new ItemModel();
			hw2.setName("HW 2");
			hw2.setPoints(Double.valueOf(10d));
			hw2.setDueDate(new Date());
			hw2.setCategoryId(hwCategory.getCategoryId());
			hw2.setItemType(Type.ITEM);
			hw2.setIncluded(Boolean.TRUE);
			hw2.setReleased(Boolean.FALSE);
			service.addItem(gradebookUid, gradebookId, hw2, true);
			
			ItemModel hw3 = new ItemModel();
			hw3.setName("HW 3");
			hw3.setPoints(Double.valueOf(10d));
			hw3.setDueDate(new Date());
			hw3.setCategoryId(hwCategory.getCategoryId());
			hw3.setItemType(Type.ITEM);
			hw3.setIncluded(Boolean.TRUE);
			hw3.setReleased(Boolean.FALSE);
			service.addItem(gradebookUid, gradebookId, hw3, true);
			
			ItemModel hw4 = new ItemModel();
			hw4.setName("HW 4");
			hw4.setPoints(Double.valueOf(10d));
			hw4.setDueDate(new Date());
			hw4.setCategoryId(hwCategory.getCategoryId());
			hw4.setItemType(Type.ITEM);
			hw4.setIncluded(Boolean.TRUE);
			hw4.setReleased(Boolean.FALSE);
			service.addItem(gradebookUid, gradebookId, hw4, true);

		} catch (Exception fe) {
			GWT.log("Failed to update gradebook properties", fe);
		}
	}
	
	public <X extends BaseModel> X create(String entityUid, Long entityId, X model, EntityType type) throws BusinessRuleException, FatalException {
		return producer.create(entityUid, entityId, model, type);
	}

	public <X extends BaseModel> X delete(X model) {
		return producer.delete(model);
	}

	public <X extends BaseModel> X get(String entityUid, Long entityId, EntityType type, String learnerUid, Boolean doShowAll) throws FatalException {
		return producer.<X>get(entityUid, entityId, type, learnerUid, doShowAll);
	}

	public <X extends BaseModel, Y extends ListLoadResult<X>> Y getPage(String uid, Long id, EntityType type, PagingLoadConfig config) throws FatalException {
		return producer.<X,Y>getPage(uid, id, type, config);
	}

	public <X extends BaseModel> X update(X model, EntityType type, UserEntityUpdateAction<StudentModel> action) throws InvalidInputException, FatalException {
		return producer.update(model, type, action);
	}

}
