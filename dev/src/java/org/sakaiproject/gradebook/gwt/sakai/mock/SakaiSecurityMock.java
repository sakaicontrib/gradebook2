package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.util.Collection;

import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.impl.SakaiSecurity;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.memory.api.MemoryService;
import org.sakaiproject.thread_local.api.ThreadLocalManager;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.UserDirectoryService;

public class SakaiSecurityMock extends SakaiSecurity {



	private UserDirectoryService userDirectoryService;

	private MemoryService memoryService;

	public void setMemoryService(MemoryService memoryService) {
		this.memoryService = memoryService;
	}

	public void init() {
		/// this will disable any caching
	}
	
	@Override
	protected ThreadLocalManager threadLocalManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AuthzGroupService authzGroupService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected UserDirectoryService userDirectoryService() {
		// TODO Auto-generated method stub
		return userDirectoryService;
	}

	public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
		this.userDirectoryService = userDirectoryService;
	}

	@Override
	protected MemoryService memoryService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected EntityManager entityManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SessionManager sessionManager() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public boolean unlock(String userId, String lock, String reference,
			Collection authzGroupIds) {
		// TODO Auto-generated method stub
		return super.unlock(userId, lock, reference, authzGroupIds);
	}

	public SecurityAdvisor popAdvisor(SecurityAdvisor advisor) {
		// TODO Auto-generated method stub
		return super.popAdvisor();
	}



	@Override
	protected EventTrackingService eventTrackingService() {
		// TODO Auto-generated method stub
		return null;
	}




}
