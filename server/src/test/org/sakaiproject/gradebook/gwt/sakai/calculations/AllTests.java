package org.sakaiproject.gradebook.gwt.sakai.calculations;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(GradeRecordCalculationUnitTest.class);
		suite.addTestSuite(GradebookCalculationUnitTest.class);
		suite.addTestSuite(CategoryCalculationUnitTest.class);
		//$JUnit-END$
		return suite;
	}

}
