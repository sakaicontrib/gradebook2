package org.sakaiproject.gradebook2.test.sakai;

import java.util.HashMap;
import java.util.Map;

import org.sakaiproject.log.api.LogConfigurationManager;
import org.sakaiproject.log.api.LogPermissionException;

public class Log4jConfigurationManager implements LogConfigurationManager {

	
	protected Map m_ignore = new HashMap();

	public void destroy()
	{
		
	}
	
	public void init()
	{
		
	}
	
	public void setIgnore(Map ignore)
	{
		m_ignore = ignore;
	}
	
	public boolean setLogLevel(String arg0, String arg1)
			throws LogPermissionException {
		
		return false;
	}

}
