package org.sakaiproject.gradebook.gwt.server;

import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class WebApplicationContext extends XmlWebApplicationContext implements ConfigurableWebApplicationContext {

	public String[] getConfigLocations() {
		String hostedModeProperty = System.getProperty("gb2.mode");
		
		boolean isHostedMode = null != hostedModeProperty && "hosted".equals(hostedModeProperty);
		
		String[] configLocations = new String[1];
		
		if (isHostedMode)
			configLocations[0] = "WEB-INF/applicationContext-hosted.xml"; 
		else
			configLocations[0] = "WEB-INF/applicationContext.xml";
		
		return configLocations;
	}

}
