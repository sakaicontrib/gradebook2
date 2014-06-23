package org.sakaiproject.gradebook.gwt.sakai.mock;

import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.UsageSessionService;
import org.sakaiproject.memory.impl.BasicMemoryService;

public class MockMemoryService extends BasicMemoryService 
{
	private AuthzGroupService authzGroupService;
	public void setAuthzGroupService(AuthzGroupService authzGroupService) {
		this.authzGroupService = authzGroupService;
	}

	protected EventTrackingService eventTrackingService;
	public void setEventTrackingService(EventTrackingService eventTrackingService) {
	    this.eventTrackingService = eventTrackingService;
	}
	
	protected ServerConfigurationService serverConfigurationService;
	public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) { 
		this.serverConfigurationService = serverConfigurationService;
	}
	
	protected UsageSessionService usageSessionService;
	public void setUsageSessionService(UsageSessionService usageSessionService) {
		this.usageSessionService = usageSessionService;
	}

	@Override
	protected AuthzGroupService authzGroupService() {
		
		return authzGroupService;
	}

	@Override
	protected EventTrackingService eventTrackingService() {
		
		return eventTrackingService;
	}

	@Override
	protected SecurityService securityService() {
		return null;
	}

	@Override
	protected ServerConfigurationService serverConfigurationService() {
		// TODO Auto-generated method stub
		return serverConfigurationService;
	}

	@Override
	protected UsageSessionService usageSessionService() {
		// TODO Auto-generated method stub
		return usageSessionService;
	}
	
}
