package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolSession;

public class SessionManagerMock implements SessionManager {

	@Override
	public int getActiveUserCount(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Session getCurrentSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCurrentSessionUserId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ToolSession getCurrentToolSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Session getSession(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Session> getSessions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String makeSessionId(HttpServletRequest arg0, Principal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCurrentSession(Session arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCurrentToolSession(ToolSession arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Session startSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Session startSession(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
