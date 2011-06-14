package org.sakaiproject.gradebook.gwt.server.test;

import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ComponentService;
import org.sakaiproject.gradebook.gwt.sakai.GradebookToolService;
import org.sakaiproject.gradebook.gwt.sakai.mock.DevelopmentModeBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

abstract class Gradebook2TestCase extends AbstractDependencyInjectionSpringContextTests
{
	private Gradebook2ComponentService service = null;
	private GradebookToolService gbToolService = null;
	private DevelopmentModeBean devModeBean = null; 

	public Gradebook2TestCase()
	{
		setAutowireMode(AUTOWIRE_BY_NAME);
	}
	
	protected String[] getConfigLocations() {
		String[] context = new String[1];
		context[0] = "classpath:applicationContext-test.xml";
		return context;
	}

	protected void onSetUp() throws Exception
	{
		if (service == null || gbToolService == null)
		{
			//setUp();
			ConfigurableApplicationContext context = applicationContext;
			if (context != null)
			{
				if (context.containsBean("DevelopmentModeBean"))
				{
					devModeBean = (DevelopmentModeBean) context.getBean("DevelopmentModeBean");
					service = devModeBean.getService(); 
					gbToolService = (GradebookToolService) context.getBean("org.sakaiproject.gradebook.gwt.sakai.GradebookToolService", GradebookToolService.class);
				}
			}
			
			checkWiring(); 
			checkCLI(); 
		}
	}
	
	// We always want to make sure wiring is OK, that we have a comp service.  If we don't no reason to continue.  Children can do this as well...
	private void checkWiring() 
	{
		assertNotNull("There is an issue with the wiring of the base test case. ", service); 
	}
	
	// This test is to ensure that we've a proper command line argument set
	/*
	 * At the time of writing, the gb2.mode should be hosted, and we disable security ala hosted mode. 
	 */
	private void checkCLI()
	{
		String setupMessage = "The test system is not configured properly for spring testing. " +
				"While creating the launcher for this test you need to configure the command line to " +
				"contain the same arguments as hosted mode.  " +
				"At the time of this writing the VM arguments are: \n" +
				"-XstartOnFirstThread -Xmx1028m -XX:PermSize=256m -XX:MaxPermSize=400m -Dgb2.security.enabled=false -Dgb2.mode=hosted -Dgb2.role=instructor -Dgb2.enable.scaled.extra.credit=instructor -Dgb2.dev.mockGradebook=true -Dgb2.dev.mockGradebookWithData=false -Dgb2.mockuser.count=10";
		String cl = System.getProperty("gb2.mode");
		assertNotNull(setupMessage, cl);
		assertEquals(setupMessage, "hosted", cl);
		
		cl = System.getProperty("gb2.security.enabled");
		assertNotNull(setupMessage, cl); 
		assertEquals(setupMessage, "false", cl);
			}
	public Gradebook2ComponentService getService() {
		return service;
	}

	public GradebookToolService getGbToolService() {
		return gbToolService;
	}

	public DevelopmentModeBean getDevModeBean() {
		return devModeBean;
	}

}
