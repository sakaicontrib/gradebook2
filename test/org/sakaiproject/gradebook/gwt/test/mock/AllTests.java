package org.sakaiproject.gradebook.gwt.test.mock;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.sakaiproject.gradebook.gwt.test.mock");
		//$JUnit-BEGIN$
		suite.addTestSuite(GradeCalculationTest.class);
		suite.addTestSuite(ToolFacadeNoCategoriesPercentageTest.class);
		suite.addTestSuite(ToolFacadeCategoriesPercentageTest.class);
		suite.addTestSuite(ToolFacadeWeightedCategoriesPercentageTest.class);
		suite.addTestSuite(ToolFacadeNoCategoriesPointTest.class);
		suite.addTestSuite(ToolFacadeCategoriesPointTest.class);
		suite.addTestSuite(ToolFacadeWeightedCategoriesPointTest.class);
		//$JUnit-END$
		return suite;
	}

}
