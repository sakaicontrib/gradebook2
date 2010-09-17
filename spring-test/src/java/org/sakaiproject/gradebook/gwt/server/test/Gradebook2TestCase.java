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

	public Gradebook2TestCase()
	{
		setAutowireMode(AUTOWIRE_BY_NAME);
	}
	
	protected String[] getConfigLocations() {
		String[] context = new String[1];
		context[0] = "classpath:applicationContext-test.xml";
		return context;
	}

	protected void onSetup() throws Exception
	{
		if (service == null || gbToolService == null)
		{
			//setUp();
			ConfigurableApplicationContext context = applicationContext;
			if (context != null)
			{
				DevelopmentModeBean dmb = (DevelopmentModeBean) context.getBean("DevelopmentModeBean");
				service = dmb.getService(); 
				gbToolService = (GradebookToolService) context.getBean("org.sakaiproject.gradebook.gwt.sakai.GradebookToolService", GradebookToolService.class);
			}
		}
	}

	public Gradebook2ComponentService getService() {
		return service;
	}

	public GradebookToolService getGbToolService() {
		return gbToolService;
	}

}
