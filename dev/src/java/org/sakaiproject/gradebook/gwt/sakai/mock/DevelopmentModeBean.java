/**********************************************************************************
 *
 * $Id:$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2008, 2009 The Regents of the University of California
 *
 * Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.exceptions.BusinessRuleException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.gxt.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationSetup;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.type.ItemType;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ComponentService;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ComponentServiceImpl;
import org.sakaiproject.gradebook.gwt.sakai.GradebookToolService;
import org.sakaiproject.gradebook.gwt.sakai.model.GradeItem;
import org.sakaiproject.gradebook.gwt.server.model.GradeItemImpl;
import org.sakaiproject.service.gradebook.shared.ConflictingAssignmentNameException;
import org.sakaiproject.service.gradebook.shared.GradebookExternalAssessmentService;
import org.sakaiproject.service.gradebook.shared.GradebookFrameworkService;
import org.sakaiproject.service.gradebook.shared.GradebookService;

import com.google.gwt.core.client.GWT;

public class DevelopmentModeBean {
	@Deprecated
	public static final String PROP_GB2_DEV_MOCKDATA = "gb2.dev.mockItems";
	
	public static final String PROP_GB2_DEV_MOCKGB = "gb2.dev.mockGradebook";
	public static final String PROP_GB2_DEV_MOCKITEMS = "gb2.dev.mockGradebookWithData";


	private static final long serialVersionUID = 1L;
	private Gradebook2ComponentService service;
	private GradebookExternalAssessmentService externalService;
	private GradebookFrameworkService frameworkService; 
	private GradebookToolService gbService;
	
	public void setGbService(GradebookToolService gbService) {
		this.gbService = gbService;
	}

	public void init() {	

		boolean runSetup = false; 
		boolean populateData = false; 

		if (System.getProperties().containsKey(PROP_GB2_DEV_MOCKDATA))
		{
			boolean mockItems = System.getProperty(PROP_GB2_DEV_MOCKDATA).equals("true"); 
			runSetup = mockItems;
			populateData = mockItems;
		}
		else
		{
			boolean mockGradebook = System.getProperty(PROP_GB2_DEV_MOCKGB, "true").equals("true"); 
			boolean mockGradebookItems = System.getProperty(PROP_GB2_DEV_MOCKITEMS, "true").equals("true"); 
			
			runSetup =  mockGradebook || mockGradebookItems;
			populateData = mockGradebookItems;
		}
		
		if(runSetup) {
			setUpMockData(populateData);
		}
		
		
	}
	// In the spring based unit testing we need to delete gradebooks, so this method will do that. 
	
	public void deleteAndRecreateGradebook(String gradebookUid) 
	{
		if (gradebookUid != null && !"".equals(gradebookUid))
		{
			frameworkService.deleteGradebook(gradebookUid); 
		}
		setUpMockData(false);
	}
	

	private void setUpMockData(boolean populate) {

		try {
			
			Gradebook2AuthzMockImpl authz = (Gradebook2AuthzMockImpl)((Gradebook2ComponentServiceImpl)service).getAuthz();
			
			// The following call has side-effects that will be needed for Deve mode: creating gradebooks, groups amd other stuff
			// TODO: make this process *not* a side-effect
			String authDetails = service.getAuthorizationDetails(new String[]{AppConstants.TEST_SITE_CONTEXT_ID, ArchiveServiceMock.ANOTHER_SITE_CONTEXT});
			// since we want to set up another site's gradebook too, we have to 
			// pass  in the uid's
			ApplicationSetup applicationSetup = service.getApplicationSetup(
					new String[]{AppConstants.TEST_SITE_CONTEXT_ID, ArchiveServiceMock.ANOTHER_SITE_CONTEXT});
			List<Gradebook> gbModels = applicationSetup.getGradebookModels();
			
			authz.setStartUp(true);
			
			// get the main test gb
			
			Map<String, Gradebook> gradebookBySiteId = new HashMap<String, Gradebook>();
			for (Gradebook gb : gbModels ){
				if (AppConstants.TEST_SITE_CONTEXT_ID.equals(gb.getGradebookUid())) {
					gradebookBySiteId.put(AppConstants.TEST_SITE_CONTEXT_ID, gb);
				} else if (ArchiveServiceMock.ANOTHER_SITE_CONTEXT.equals(gb.getGradebookUid())) {
					gradebookBySiteId.put(ArchiveServiceMock.ANOTHER_SITE_CONTEXT, gb);
				}
			}
			


			createMainTestGradebook(gradebookBySiteId.get(AppConstants.TEST_SITE_CONTEXT_ID), populate);
			
			createSecondGradebook(gradebookBySiteId.get(ArchiveServiceMock.ANOTHER_SITE_CONTEXT), populate);
			
			authz.setStartUp(false);
			
		} catch (Exception fe) {
			GWT.log("Failed to update gradebook properties", fe);
			throw new RuntimeException(fe);
		}
	}

	

	private void createMainTestGradebook(Gradebook gbModel, boolean populate) throws InvalidInputException {
		 
		Item itemModel = gbModel.getGradebookItemModel();
		
		org.sakaiproject.tool.gradebook.Gradebook gradebook = gbService.getGradebook(gbModel.getGradebookUid());
		gradebook.setGrade_type(GradebookService.GRADE_TYPE_LETTER);
		gradebook.setCourseGradeDisplayed(Boolean.TRUE);
		gbService.updateGradebook(gradebook);
		
		itemModel.setName("Gradebook");
		itemModel.setCategoryType(CategoryType.WEIGHTED_CATEGORIES);
		itemModel.setGradeType(GradeType.LETTERS);
		itemModel.setItemType(ItemType.GRADEBOOK);
		itemModel.setExtraCreditScaled(Boolean.TRUE);
		itemModel.setReleaseGrades(Boolean.TRUE);
		itemModel.setReleaseItems(Boolean.TRUE);
		itemModel.setShowItemStatistics(Boolean.TRUE); /// obsolete?
		itemModel.setShowMean(Boolean.FALSE);
		itemModel.setShowMedian(Boolean.FALSE);
		itemModel.setShowMode(Boolean.TRUE);
		itemModel.setShowRank(Boolean.TRUE);
		itemModel.setShowStatisticsChart(Boolean.TRUE); /// this requires the GRBK-616 patch
		service.updateItem(itemModel);
		
		String gradebookUid = gbModel.getGradebookUid();
		Long gradebookId = gbModel.getGradebookId();
		System.out.println("gradebookUid: " + gbModel.getGradebookUid());
		
		if (!populate)
			return; 
		GradeItem essaysCategory = new GradeItemImpl();
		essaysCategory.setName("My Essays");
		essaysCategory.setPercentCourseGrade(Double.valueOf(50d));
		essaysCategory.setDropLowest(Integer.valueOf(0));
		essaysCategory.setEqualWeightAssignments(Boolean.TRUE);
		essaysCategory.setItemType(ItemType.CATEGORY);
		essaysCategory.setIncluded(Boolean.TRUE);
		essaysCategory.setExtraCredit(Boolean.FALSE);
		essaysCategory.setEnforcePointWeighting(Boolean.TRUE);
		try {
		  essaysCategory = getActiveItem((GradeItem)service.createItem(gradebookUid, gradebookId, essaysCategory, false));
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		}  
		
		GradeItem hwCategory = new GradeItemImpl();
		hwCategory.setName("My Homework");
		hwCategory.setPercentCourseGrade(Double.valueOf(40d));
		hwCategory.setDropLowest(Integer.valueOf(0));
		hwCategory.setEqualWeightAssignments(Boolean.TRUE);
		hwCategory.setItemType(ItemType.CATEGORY);
		hwCategory.setExtraCredit(Boolean.FALSE);
		hwCategory.setIncluded(Boolean.TRUE);
		try {
		hwCategory = getActiveItem((GradeItem)service.createItem(gradebookUid, gradebookId, hwCategory, false));
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 
		
//		GradeItem emptyCategory = new GradeItemImpl();
//		emptyCategory.setName("Empty");
//		emptyCategory.setPercentCourseGrade(Double.valueOf(10d));
//		emptyCategory.setDropLowest(Integer.valueOf(0));
//		emptyCategory.setEqualWeightAssignments(Boolean.TRUE);
//		emptyCategory.setItemType(ItemType.CATEGORY);
//		emptyCategory.setIncluded(Boolean.TRUE);
//		emptyCategory = getActiveItem((GradeItem)service.createItem(gradebookUid, gradebookId, emptyCategory, false));
//		
		GradeItem emptyCategory = new GradeItemImpl();
		emptyCategory.setName("Empty");
		emptyCategory.setPercentCourseGrade(Double.valueOf(10d));
		emptyCategory.setDropLowest(Integer.valueOf(0));
		emptyCategory.setEqualWeightAssignments(Boolean.TRUE);
		emptyCategory.setItemType(ItemType.CATEGORY);
		emptyCategory.setIncluded(Boolean.TRUE);
		emptyCategory.setExtraCredit(Boolean.FALSE);
		try {
		  emptyCategory = getActiveItem((GradeItem)service.createItem(gradebookUid, gradebookId, emptyCategory, false));
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 
		
		
		GradeItem ecCategory = new GradeItemImpl();
		ecCategory.setName("Extra Credit");
		ecCategory.setPercentCourseGrade(Double.valueOf(10d));
		ecCategory.setDropLowest(Integer.valueOf(0));
		ecCategory.setEqualWeightAssignments(Boolean.TRUE);
		ecCategory.setItemType(ItemType.CATEGORY);
		ecCategory.setExtraCredit(Boolean.TRUE);
		ecCategory.setIncluded(Boolean.TRUE);
		try {
		  ecCategory = getActiveItem((GradeItem)service.createItem(gradebookUid, gradebookId, ecCategory, false));
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 
		
		GradeItem essay1 = new GradeItemImpl();
		essay1.setName("Essay 1");
		essay1.setPoints(Double.valueOf(10d));
		essay1.setDueDate(new Date());
		essay1.setCategoryId(essaysCategory.getCategoryId());
		essay1.setReleased(Boolean.TRUE);
		essay1.setItemType(ItemType.ITEM);
		essay1.setIncluded(Boolean.TRUE);
		essay1.setItemOrder(1);
		try {
		  essay1 = getActiveItem((GradeItem)service.createItem(gradebookUid, gradebookId, essay1, false));
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 
		
		Learner learner = gbModel.getUserAsStudent();
		if (learner != null) {
			service.assignScore(gradebookUid, learner.getIdentifier(), essay1.getIdentifier(), "B", null);
		}
		
		GradeItem essay2 = new GradeItemImpl();
		essay2.setName("Essay 2");
		essay2.setPoints(Double.valueOf(20d));
		essay2.setDueDate(new Date());
		essay2.setCategoryId(essaysCategory.getCategoryId());
		essay2.setReleased(Boolean.TRUE);
		essay2.setItemType(ItemType.ITEM);
		essay2.setIncluded(Boolean.TRUE);
		essay2.setItemOrder(0);
		try {  
		  service.createItem(gradebookUid, gradebookId, essay2, false);
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 
		
		GradeItem essay3 = new GradeItemImpl();
		essay3.setName("Essay 3");
		essay3.setPoints(Double.valueOf(10d));
		essay3.setDueDate(new Date());
		essay3.setCategoryId(essaysCategory.getCategoryId());
		essay3.setReleased(Boolean.TRUE);
		essay3.setItemType(ItemType.ITEM);
		essay3.setIncluded(Boolean.TRUE);
		try {
		  service.createItem(gradebookUid, gradebookId, essay3, false);
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 

		GradeItem ecEssay = new GradeItemImpl();
		ecEssay.setName("EC Essay");
		ecEssay.setPercentCategory(Double.valueOf(100d));
		ecEssay.setPoints(Double.valueOf(5d));
		ecEssay.setDueDate(new Date());
		ecEssay.setCategoryId(essaysCategory.getCategoryId());
		ecEssay.setIncluded(Boolean.TRUE);
		ecEssay.setExtraCredit(Boolean.TRUE);
		ecEssay.setReleased(Boolean.FALSE);
		try {
		  service.createItem(gradebookUid, gradebookId, ecEssay, false);
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 

		try {
			externalService.addExternalAssessment(gradebookUid, "sakai.assignment.tool", "http://assignments.ucdavis.edu", "Assignment 1", 
				Double.valueOf(10d), 
				null, "Assignments", Boolean.FALSE);
		} catch (ConflictingAssignmentNameException ane) {
			System.out.println("WARNING: " + ane.getMessage());
		}
		
		GradeItem hw1 = new GradeItemImpl();
		hw1.setName("HW 1");
		hw1.setPoints(Double.valueOf(10d));
		hw1.setDueDate(new Date());
		hw1.setCategoryId(hwCategory.getCategoryId());
		hw1.setItemType(ItemType.ITEM);
		hw1.setIncluded(Boolean.TRUE);
		hw1.setReleased(Boolean.TRUE);
		try {
		  service.createItem(gradebookUid, gradebookId, hw1, false);
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 
		
		GradeItem hw2 = new GradeItemImpl();
		hw2.setName("HW 2");
		hw2.setPoints(Double.valueOf(10d));
		hw2.setDueDate(new Date());
		hw2.setCategoryId(hwCategory.getCategoryId());
		hw2.setItemType(ItemType.ITEM);
		hw2.setIncluded(Boolean.TRUE);
		hw2.setReleased(Boolean.TRUE);
		try {
		  service.createItem(gradebookUid, gradebookId, hw2, false);
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 
		
		GradeItem hw3 = new GradeItemImpl();
		hw3.setName("HW 3");
		hw3.setPoints(Double.valueOf(10d));
		hw3.setDueDate(new Date());
		hw3.setCategoryId(hwCategory.getCategoryId());
		hw3.setItemType(ItemType.ITEM);
		hw3.setIncluded(Boolean.TRUE);
		hw3.setReleased(Boolean.TRUE);
		try {
		  service.createItem(gradebookUid, gradebookId, hw3, false);
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 
		
		GradeItem hw4 = new GradeItemImpl();
		hw4.setName("HW 4");
		hw4.setPoints(Double.valueOf(10d));
		hw4.setDueDate(new Date());
		hw4.setCategoryId(hwCategory.getCategoryId());
		hw4.setItemType(ItemType.ITEM);
		hw4.setIncluded(Boolean.TRUE);
		hw4.setReleased(Boolean.TRUE);
		try { 
		  service.createItem(gradebookUid, gradebookId, hw4, false);
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 

		
		GradeItem ec1 = new GradeItemImpl();
		ec1.setName("EC 1");
		ec1.setPercentCategory(Double.valueOf(100d));
		ec1.setPoints(Double.valueOf(10d));
		ec1.setDueDate(new Date());
		ec1.setCategoryId(ecCategory.getCategoryId());
		ec1.setIncluded(Boolean.TRUE);
		ec1.setExtraCredit(Boolean.TRUE);
		ec1.setReleased(Boolean.FALSE);
		try {
		  service.createItem(gradebookUid, gradebookId, ec1, false);
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 
		
		GradeItem ec2 = new GradeItemImpl();
		ec2.setName("EC 2");
		ec2.setPercentCategory(Double.valueOf(100d));
		ec2.setPoints(Double.valueOf(10d));
		ec2.setDueDate(new Date());
		ec2.setCategoryId(ecCategory.getCategoryId());
		ec2.setIncluded(Boolean.TRUE);
		ec2.setExtraCredit(Boolean.TRUE);
		ec2.setReleased(Boolean.FALSE);
		try {
		  service.createItem(gradebookUid, gradebookId, ec2, false);
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 
	}
	
private void createSecondGradebook(Gradebook gbModel, boolean populate) throws InvalidInputException {
		
		Item itemModel = gbModel.getGradebookItemModel();
		
		
		itemModel.setName("My Deafult Gradebook"); //sic - caopying the name of an existing GB for testing 
		itemModel.setCategoryType(CategoryType.WEIGHTED_CATEGORIES);
		itemModel.setGradeType(GradeType.POINTS);
		itemModel.setItemType(ItemType.GRADEBOOK);
		itemModel.setExtraCreditScaled(Boolean.TRUE);
		itemModel.setReleaseGrades(Boolean.FALSE);
		itemModel.setReleaseItems(Boolean.TRUE);
		itemModel.setShowItemStatistics(Boolean.TRUE);
		itemModel.setShowMean(Boolean.FALSE);
		itemModel.setShowMedian(Boolean.FALSE);
		itemModel.setShowMode(Boolean.TRUE);
				
		service.updateItem(itemModel);
		
		String gradebookUid = gbModel.getGradebookUid();
		Long gradebookId = gbModel.getGradebookId();
		System.out.println("gradebookUid: " + gradebookUid);
		
		if(!populate)
			return;
		
		
		//Quizes
		GradeItem quizesCategory = new GradeItemImpl();
		quizesCategory.setName("Quizzes");
		quizesCategory.setPercentCourseGrade(Double.valueOf(50d));
		quizesCategory.setDropLowest(Integer.valueOf(0));
		quizesCategory.setEqualWeightAssignments(Boolean.TRUE);
		quizesCategory.setItemType(ItemType.CATEGORY);
		quizesCategory.setIncluded(Boolean.TRUE);
		quizesCategory.setEnforcePointWeighting(Boolean.TRUE);
		quizesCategory.setDropLowest(1);
		quizesCategory.setPercentCategory(0.30d);
		quizesCategory.setEqualWeightAssignments(Boolean.TRUE);
		quizesCategory.setExtraCredit(Boolean.TRUE);
		try {
		  quizesCategory = getActiveItem((GradeItem)service.createItem(gradebookUid, gradebookId, quizesCategory, false));
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 

		GradeItem quiz1 = new GradeItemImpl();
		quiz1.setName("Quiz 1");
		quiz1.setPoints(Double.valueOf(10d));
		quiz1.setDueDate(new Date());
		quiz1.setCategoryId(quizesCategory.getCategoryId());
		quiz1.setItemType(ItemType.ITEM);
		quiz1.setIncluded(Boolean.TRUE);
		quiz1.setReleased(Boolean.TRUE);
		try {
		  service.createItem(gradebookUid, gradebookId, quiz1, false);
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 
		
		GradeItem quiz2 = new GradeItemImpl();
		quiz2.setName("Quiz 2");
		quiz2.setPoints(Double.valueOf(10d));
		quiz2.setDueDate(new Date());
		quiz2.setCategoryId(quizesCategory.getCategoryId());
		quiz2.setItemType(ItemType.ITEM);
		quiz2.setIncluded(Boolean.TRUE);
		quiz2.setReleased(Boolean.TRUE);
		try {
		  service.createItem(gradebookUid, gradebookId, quiz2, false);
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 
		
		GradeItem quiz3 = new GradeItemImpl();
		quiz3.setName("Quiz 3");
		quiz3.setPoints(Double.valueOf(10d));
		quiz3.setDueDate(new Date());
		quiz3.setCategoryId(quizesCategory.getCategoryId());
		quiz3.setItemType(ItemType.ITEM);
		quiz3.setIncluded(Boolean.TRUE);
		quiz3.setReleased(Boolean.TRUE);
		try {
		  service.createItem(gradebookUid, gradebookId, quiz3, false);
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 
		
		GradeItem quiz4 = new GradeItemImpl();
		quiz4.setName("Quiz 4");
		quiz4.setPoints(Double.valueOf(10d));
		quiz4.setDueDate(new Date());
		quiz4.setCategoryId(quizesCategory.getCategoryId());
		quiz4.setItemType(ItemType.ITEM);
		quiz4.setIncluded(Boolean.TRUE);
		quiz4.setReleased(Boolean.TRUE);
		try {
		  service.createItem(gradebookUid, gradebookId, quiz4, false);
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 
		
		GradeItem quiz5 = new GradeItemImpl();
		quiz5.setName("Quiz 5");
		quiz5.setPoints(Double.valueOf(10d));
		quiz5.setDueDate(new Date());
		quiz5.setCategoryId(quizesCategory.getCategoryId());
		quiz5.setItemType(ItemType.ITEM);
		quiz5.setIncluded(Boolean.TRUE);
		quiz5.setReleased(Boolean.TRUE);
		try {
		  service.createItem(gradebookUid, gradebookId, quiz5, false);
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 
		
		GradeItem quiz6 = new GradeItemImpl();
		quiz6.setName("Quiz 6");
		quiz6.setPoints(Double.valueOf(10d));
		quiz6.setDueDate(new Date());
		quiz6.setCategoryId(quizesCategory.getCategoryId());
		quiz6.setItemType(ItemType.ITEM);
		quiz6.setIncluded(Boolean.TRUE);
		quiz6.setReleased(Boolean.TRUE);
		try {
		  service.createItem(gradebookUid, gradebookId, quiz6, false);
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 
		
		
		
		
		//Tests
		GradeItem testsCategory = new GradeItemImpl();
		testsCategory.setName("Tests");
		testsCategory.setPercentCourseGrade(Double.valueOf(50d));
		testsCategory.setDropLowest(Integer.valueOf(0));
		testsCategory.setEqualWeightAssignments(Boolean.TRUE);
		testsCategory.setItemType(ItemType.CATEGORY);
		testsCategory.setIncluded(Boolean.TRUE);
		testsCategory.setEnforcePointWeighting(Boolean.TRUE);
		testsCategory.setDropLowest(1);
		testsCategory.setPercentCategory(0.50d);
		testsCategory.setEqualWeightAssignments(Boolean.TRUE);
		testsCategory.setExtraCredit(Boolean.FALSE);
		try {
		  testsCategory = getActiveItem((GradeItem)service.createItem(gradebookUid, gradebookId, testsCategory, false));
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 

		GradeItem midterm1 = new GradeItemImpl();
		midterm1.setName("Midterm 1");
		midterm1.setPoints(Double.valueOf(10d));
		midterm1.setDueDate(new Date());
		midterm1.setCategoryId(testsCategory.getCategoryId());
		midterm1.setItemType(ItemType.ITEM);
		midterm1.setIncluded(Boolean.TRUE);
		midterm1.setReleased(Boolean.TRUE);
		try {
		  service.createItem(gradebookUid, gradebookId, midterm1, false);
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 
		
		GradeItem midterm2 = new GradeItemImpl();
		midterm2.setName("Midterm 2");
		midterm2.setPoints(Double.valueOf(10d));
		midterm2.setDueDate(new Date());
		midterm2.setCategoryId(testsCategory.getCategoryId());
		midterm2.setItemType(ItemType.ITEM);
		midterm2.setIncluded(Boolean.TRUE);
		midterm2.setReleased(Boolean.TRUE);
		try {
		  service.createItem(gradebookUid, gradebookId, midterm2, false);
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 
		
		GradeItem finalExam = new GradeItemImpl();
		finalExam.setName("Final");
		finalExam.setPoints(Double.valueOf(10d));
		finalExam.setDueDate(new Date());
		finalExam.setCategoryId(testsCategory.getCategoryId());
		finalExam.setItemType(ItemType.ITEM);
		finalExam.setIncluded(Boolean.TRUE);
		finalExam.setReleased(Boolean.TRUE);
		try {
		  service.createItem(gradebookUid, gradebookId, finalExam, false);
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 
		
		
		
		//Essay
		GradeItem essayCategory = new GradeItemImpl();
		essayCategory.setName("Essay");
		essayCategory.setPercentCourseGrade(Double.valueOf(50d));
		essayCategory.setDropLowest(Integer.valueOf(0));
		essayCategory.setEqualWeightAssignments(Boolean.TRUE);
		essayCategory.setItemType(ItemType.CATEGORY);
		essayCategory.setIncluded(Boolean.TRUE);
		essayCategory.setEnforcePointWeighting(Boolean.TRUE);
		essayCategory.setPercentCategory(0.10d);
		essayCategory.setEqualWeightAssignments(Boolean.FALSE);
		essayCategory.setExtraCredit(Boolean.FALSE);
		try {
		  essayCategory = getActiveItem((GradeItem)service.createItem(gradebookUid, gradebookId, essayCategory, false));
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 

		GradeItem essay = new GradeItemImpl();
		essay.setName("Essay");
		essay.setPoints(Double.valueOf(10d));
		essay.setDueDate(new Date());
		essay.setCategoryId(essayCategory.getCategoryId());
		essay.setItemType(ItemType.ITEM);
		essay.setIncluded(Boolean.TRUE);
		essay.setReleased(Boolean.TRUE);
		try {
			service.createItem(gradebookUid, gradebookId, essay, false);
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 
		
		
		//Participation
		GradeItem participationCategory = new GradeItemImpl();
		participationCategory.setName("Participation");
		participationCategory.setPercentCourseGrade(Double.valueOf(50d));
		participationCategory.setDropLowest(Integer.valueOf(0));
		participationCategory.setEqualWeightAssignments(Boolean.TRUE);
		participationCategory.setItemType(ItemType.CATEGORY);
		participationCategory.setIncluded(Boolean.TRUE);
		participationCategory.setEnforcePointWeighting(Boolean.TRUE);
		participationCategory.setPercentCategory(0.10d);
		participationCategory.setEqualWeightAssignments(Boolean.TRUE);
		participationCategory.setExtraCredit(Boolean.FALSE);
		try {
			participationCategory = getActiveItem((GradeItem)service.createItem(gradebookUid, gradebookId, participationCategory, false));
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 
		

		GradeItem participationWeek1_5 = new GradeItemImpl();
		participationWeek1_5.setName("Participation Week 1-5");
		participationWeek1_5.setPoints(Double.valueOf(10d));
		participationWeek1_5.setDueDate(new Date());
		participationWeek1_5.setCategoryId(participationCategory.getCategoryId());
		participationWeek1_5.setItemType(ItemType.ITEM);
		participationWeek1_5.setIncluded(Boolean.TRUE);
		participationWeek1_5.setReleased(Boolean.TRUE);
		try {
			service.createItem(gradebookUid, gradebookId, participationWeek1_5, false);
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 
		
		GradeItem participationWeek6_10 = new GradeItemImpl();
		participationWeek6_10.setName("Participation Week 6-10");
		participationWeek6_10.setPoints(Double.valueOf(10d));
		participationWeek6_10.setDueDate(new Date());
		participationWeek6_10.setCategoryId(participationCategory.getCategoryId());
		participationWeek6_10.setItemType(ItemType.ITEM);
		participationWeek6_10.setIncluded(Boolean.TRUE);
		participationWeek6_10.setReleased(Boolean.TRUE);
		try {
			service.createItem(gradebookUid, gradebookId, participationWeek6_10, false);
		} catch (BusinessRuleException re) {
			System.out.println("WARNING: " + re.getMessage());
		} 
		
		
	}


	private GradeItem getActiveItem(GradeItem parent) {
		if (parent.isActive())
			return parent;
		
		for (GradeItem c : parent.getChildren()) {
			
			if (c.isActive()) {
				return c;
			}
			
			if (c.getChildCount() > 0) {
				GradeItem activeItem = getActiveItem(c);
				
				if (activeItem != null)
					return activeItem;
			}
		}
		
		return null;
	}
	
	public Gradebook2ComponentService getService() {
		return service;
	}

	public void setService(Gradebook2ComponentService service) {
		this.service = service;
	}

	public GradebookExternalAssessmentService getExternalService() {
		return externalService;
	}

	public void setExternalService(
			GradebookExternalAssessmentService externalService) {
		this.externalService = externalService;
	}

	public GradebookFrameworkService getFrameworkService() {
		return frameworkService;
	}

	public void setFrameworkService(GradebookFrameworkService frameworkService) {
		this.frameworkService = frameworkService;
	}
}
