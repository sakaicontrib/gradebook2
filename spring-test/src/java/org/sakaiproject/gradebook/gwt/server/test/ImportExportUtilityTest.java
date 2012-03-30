package org.sakaiproject.gradebook.gwt.server.test;

import java.io.FileInputStream;
import java.io.FileReader;
import java.net.URL;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.api.ImportSettings;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.client.model.Roster;
import org.sakaiproject.gradebook.gwt.client.model.Upload;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;
import org.sakaiproject.gradebook.gwt.server.ImportExportUtility;
import org.sakaiproject.gradebook.gwt.server.ImportSettingsImpl;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockMultipartFile;

public class ImportExportUtilityTest extends Gradebook2TestCase 
{
	ImportExportUtility ieutil; 
	
	// All this does is makes sure all the wiring is OK 
	public void testSetup() throws Exception
	{
		boolean good = getService() != null;
		
		if (good)
		{
			good = ieutil != null; 
		}
		assertTrue("There is a wiring issue with this test class", good); 
	}
	
	/*
	 * Tests no cats, points with 10 students, relatively simple 
	 */
	public void testImportSimple() throws Exception
	{
		String dataFileName = "importTest1.csv"; 
		URL ifname = getClass().getResource(dataFileName); 
		assertNotNull("Cannot find required test file", ifname); 
		Upload data = null; 
		FileReader r = null; 
		r = new FileReader(ifname.getPath()); 
		data = ieutil.parseImportCSV(AppConstants.TEST_SITE_CONTEXT_ID, r);
		// Make sure what we get back is non null
		assertNotNull(data); 		
		// Don't want errors, the file importTest1 is very simple. 
		assertFalse(data.hasErrors()); 

		// Make sure we're a points / no cats gb 
		boolean isNoCats = false; 
		boolean isPoints = false; 

		Item im = data.getGradebookItemModel(); 
		isNoCats = im.getCategoryType() == CategoryType.NO_CATEGORIES;
		isPoints = im.getGradeType() == GradeType.POINTS; 
		
		assertTrue("Bad Category type", isNoCats); 
		assertTrue("Bad Grade type", isPoints); 
		
		// Check the student count.
		List<Learner>  dataRows = data.getRows(); 
		int numStudents = dataRows.size(); 
		
		org.sakaiproject.tool.gradebook.Gradebook g = getGbToolService().getGradebook(AppConstants.TEST_SITE_CONTEXT_ID); 
		assertTrue("The number of students is unexpected", numStudents == 10); 

		// Now finish the import by calling the upload. 
		getService().upload(AppConstants.TEST_SITE_CONTEXT_ID, g.getId(), data, false); 
		
		// Grab the roster for verification 
		Roster roster = getService().getRoster(AppConstants.TEST_SITE_CONTEXT_ID, g.getId(), Integer.valueOf(20), Integer.valueOf(0), null, null, null, null, true, true, false);
		
		assertNotNull(roster); 
		
		/*
		 * Now lets check the file for a couple of students to make sure the final grade they get is OK. 
		 * 
		 */
		Learner currentStudent = null; 
		String cgrade = ""; 
		
		// First check student 1
		currentStudent = getLearnerFromRosterById(roster, "1"); 
		
		assertNotNull(currentStudent); 
		cgrade = currentStudent.getCalculatedGrade(); 
		assertEquals("87.50",cgrade); 
		
		currentStudent = null; 
		cgrade = ""; 
		// Now student 7 
		
		currentStudent = getLearnerFromRosterById(roster, "7"); 
		cgrade = currentStudent.getCalculatedGrade(); 
		assertEquals("93.83", cgrade);		

		
	}
	
	// GRBK-804 - percentage import of grades 
	
	public void testImportGRBK_804_NoCats() throws Exception
	{
		clearGB();
		Gradebook g = getService().getGradebook(AppConstants.TEST_SITE_CONTEXT_ID); 
		assertNotNull(g); 
		String dataFileName = "importGRBK_804_NC.csv"; 
		URL ifname = getClass().getResource(dataFileName); 
		assertNotNull("Cannot find required test file", ifname); 
		Upload data = null; 
		FileReader r = null; 
		r = new FileReader(ifname.getPath()); 
		data = ieutil.parseImportCSV(AppConstants.TEST_SITE_CONTEXT_ID, r);
		assertNotNull(data); 
		assertFalse(data.hasErrors()); 

		// Make sure we're a points / no cats gb 
		boolean isNoCats = false; 
		boolean isPercentages = false; 

		Item im = data.getGradebookItemModel(); 
		isNoCats = im.getCategoryType() == CategoryType.NO_CATEGORIES;
		isPercentages = im.getGradeType() == GradeType.PERCENTAGES; 
		
		assertTrue("Bad Category type", isNoCats); 
		assertTrue("Bad Grade type", isPercentages); 
		
		// Check the student count.
		List<Learner>  dataRows = data.getRows(); 
		int numStudents = dataRows.size(); 
		
		assertTrue("The number of students is unexpected", numStudents == 10); 
		getService().upload(AppConstants.TEST_SITE_CONTEXT_ID, g.getGradebookId(), data, false); 
	
		Roster roster = getService().getRoster(AppConstants.TEST_SITE_CONTEXT_ID, g.getGradebookId(), Integer.valueOf(20), 
				Integer.valueOf(0), null, null, null, null, true, true, false);
		assertNotNull(roster); 
		
		/*
		 * Now lets check the file for a couple of students to make sure the final grade they get is OK. 
		 * 
		 */
		Learner currentStudent = null; 
		String cgrade = ""; 
		
		// First check student 1
		currentStudent = getLearnerFromRosterById(roster, "1"); 
		
		assertNotNull(currentStudent); 
		cgrade = currentStudent.getCalculatedGrade(); 
		assertEquals("64.03",cgrade); 
		
		currentStudent = null; 
		cgrade = ""; 
		// Now student 7 
		
		currentStudent = getLearnerFromRosterById(roster, "7"); 
		cgrade = currentStudent.getCalculatedGrade(); 
		assertEquals("72.56", cgrade);		
	}

	public void testImportGRBK_804_SimpleCategories() throws Exception
	{
		clearGB();
		Gradebook g = getService().getGradebook(AppConstants.TEST_SITE_CONTEXT_ID); 
		assertNotNull(g); 
		String dataFileName = "importGRBK_804_SC.csv"; 
		URL ifname = getClass().getResource(dataFileName); 
		assertNotNull("Cannot find required test file", ifname); 
		Upload data = null; 
		FileReader r = null; 
		r = new FileReader(ifname.getPath()); 
		data = ieutil.parseImportCSV(AppConstants.TEST_SITE_CONTEXT_ID, r);
		assertNotNull(data); 
		assertFalse(data.hasErrors()); 

		// Make sure we're a points / no cats gb 
		boolean isSimpleCats = false; 
		boolean isPercentages = false; 

		Item im = data.getGradebookItemModel(); 
		isSimpleCats = im.getCategoryType() == CategoryType.SIMPLE_CATEGORIES;
		isPercentages = im.getGradeType() == GradeType.PERCENTAGES; 
		
		assertTrue("Bad Category type", isSimpleCats); 
		assertTrue("Bad Grade type", isPercentages); 
		
		// Check the student count.
		List<Learner>  dataRows = data.getRows(); 
		int numStudents = dataRows.size(); 
		
		assertTrue("The number of students is unexpected", numStudents == 10); 
		getService().upload(AppConstants.TEST_SITE_CONTEXT_ID, g.getGradebookId(), data, false); 
	
		Roster roster = getService().getRoster(AppConstants.TEST_SITE_CONTEXT_ID, g.getGradebookId(), Integer.valueOf(20), 
				Integer.valueOf(0), null, null, null, null, true, true, false);
		assertNotNull(roster); 
		
		/*
		 * Now lets check the file for a couple of students to make sure the final grade they get is OK. 
		 * 
		 */
		Learner currentStudent = null; 
		String cgrade = ""; 
		
		// First check student 4
		currentStudent = getLearnerFromRosterById(roster, "4"); 
		
		assertNotNull(currentStudent); 
		cgrade = currentStudent.getCalculatedGrade(); 
		assertEquals("93.71",cgrade); 
		
		currentStudent = null; 
		cgrade = ""; 
		// Now student 8
		
		currentStudent = getLearnerFromRosterById(roster, "8"); 
		cgrade = currentStudent.getCalculatedGrade(); 
		assertEquals("72.30", cgrade);		
	}

	public void testImportGRBK_804_WeightedCategories() throws Exception
	{
		clearGB();
		Gradebook g = getService().getGradebook(AppConstants.TEST_SITE_CONTEXT_ID); 
		assertNotNull(g); 
		String dataFileName = "importGRBK_804_WC.csv"; 
		URL ifname = getClass().getResource(dataFileName); 
		assertNotNull("Cannot find required test file", ifname); 
		Upload data = null; 
		FileReader r = null; 
		r = new FileReader(ifname.getPath()); 
		data = ieutil.parseImportCSV(AppConstants.TEST_SITE_CONTEXT_ID, r);
		assertNotNull(data); 
		assertFalse(data.hasErrors()); 

		// Make sure we're a points / no cats gb 
		boolean isWeightedCats = false; 
		boolean isPercentages = false; 

		Item im = data.getGradebookItemModel(); 
		isWeightedCats = im.getCategoryType() == CategoryType.WEIGHTED_CATEGORIES;
		isPercentages = im.getGradeType() == GradeType.PERCENTAGES; 
		
		assertTrue("Bad Category type", isWeightedCats); 
		assertTrue("Bad Grade type", isPercentages); 
		
		// Check the student count.
		List<Learner>  dataRows = data.getRows(); 
		int numStudents = dataRows.size(); 
		
		assertTrue("The number of students is unexpected", numStudents == 10); 
		getService().upload(AppConstants.TEST_SITE_CONTEXT_ID, g.getGradebookId(), data, false); 
	
		Roster roster = getService().getRoster(AppConstants.TEST_SITE_CONTEXT_ID, g.getGradebookId(), Integer.valueOf(20), 
				Integer.valueOf(0), null, null, null, null, true, true, false);
		assertNotNull(roster); 
		
		/*
		 * Now lets check the file for a couple of students to make sure the final grade they get is OK. 
		 * 
		 */
		Learner currentStudent = null; 
		String cgrade = ""; 
		
		// First check student 3
		currentStudent = getLearnerFromRosterById(roster, "3"); 
		
		assertNotNull(currentStudent); 
		cgrade = currentStudent.getCalculatedGrade(); 
		assertEquals("77.47",cgrade); 
		
		currentStudent = null; 
		cgrade = ""; 
		// Now student 7 
		
		currentStudent = getLearnerFromRosterById(roster, "7"); 
		cgrade = currentStudent.getCalculatedGrade(); 
		assertEquals("85.31", cgrade);		
		
		currentStudent = null; 
		cgrade = ""; 
		// Now student 0 
		
		currentStudent = getLearnerFromRosterById(roster, "0"); 
		cgrade = currentStudent.getCalculatedGrade(); 
		assertEquals("0.00", cgrade);		
	
	}
	
	public void testImportGRBK_804_scantron_normal() throws Exception
	{
		// so for a scantron, we need to be a particular mode, so easiest way is to do the simple cats thing
		testImportGRBK_804_SimpleCategories();
		String dataFileName = "importGRBK_804_scantron.xls"; 
		URL ifname = getClass().getResource(dataFileName); 
		assertNotNull("Cannot find required test file", ifname); 
		Upload data = null; 
		FileInputStream is = null; 
		is = new FileInputStream(ifname.getPath()); 
		MockMultipartFile file = new MockMultipartFile(dataFileName,is);
		ImportSettings settings = new ImportSettingsImpl();
		data = ieutil.parseImportXLS(file, settings);
		assertNotNull(data); 
		assertTrue("Scantron files can be imported but should not be", data.hasErrors()); 
		
	}
	public void testImportGRBK_804_scantron_rescore() throws Exception
	{
		// so for a scantron, we need to be a particular mode, so easiest way is to do the simple cats thing
		testImportGRBK_804_SimpleCategories();
		String dataFileName = "importGRBK_804_scantron.xls"; 
		URL ifname = getClass().getResource(dataFileName); 
		assertNotNull("Cannot find required test file", ifname); 
		Upload data = null; 
		FileInputStream is = null; 
		is = new FileInputStream(ifname.getPath()); 
		MockMultipartFile file = new MockMultipartFile(dataFileName,is);
		ImportSettings settings = new ImportSettingsImpl();
		data = ieutil.parseImportXLS(file, settings);
		assertNotNull(data); 
		assertTrue("Scantron files can be imported but should not be", data.hasErrors()); 
		
	}
	
	private void clearGB() {
		getDevModeBean().deleteAndRecreateGradebook(AppConstants.TEST_SITE_CONTEXT_ID); 
	}

	/**
	 * This probably should move into some util, but for now its fine where it is. 
	 * 
	 * @param roster - Must not be null, I normally will check this with an assertNotNull right before calling
	 * @param string - Null checked, doesn't matter, but GIGO applies. 
	 * @return
	 */
	public Learner getLearnerFromRosterById(Roster roster, String id) {
		
		List<Learner> rows = roster.getLearnerPage(); 
		
		if (rows != null && id != null && !"".equals(id))
		{
			for (Learner cur : rows)
			{
				if (cur.getIdentifier().equals(id))
				{
					return cur; 
				}
			}
		}
		return null;
	}

	@Override
	protected void onSetUp() throws Exception {
		super.onSetUp();
		ConfigurableApplicationContext context = applicationContext;
		ieutil = (ImportExportUtility) context.getBean("org.sakaiproject.gradebook.gwt.server.ImportExportUtility", ImportExportUtility.class);

	}
	

}
