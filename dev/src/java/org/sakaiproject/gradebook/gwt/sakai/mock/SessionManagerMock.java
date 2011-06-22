package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.mutable.MutableLong;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.impl.MySession;

public class SessionManagerMock implements SessionManager {

	MySession session = null;
	
	
	public int getActiveUserCount(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Session getCurrentSession() {
		
		if(null == session) {
		 
			session = new MySession(this, java.util.UUID.randomUUID().toString(), null, null, null, null, 0, null, new MutableLong(1000l* 60l* 60l* 24l));
		}
		
		return session;
	}

	public String getCurrentSessionUserId() {
		
		if(null != session) {
			
			return session.getId();
		}
		
		return null;
	}

	public ToolSession getCurrentToolSession() {
		// TODO Auto-generated method stub
		return null;
	}

	public Session getSession(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Session> getSessions() {
		// TODO Auto-generated method stub
		return null;
	}

	public String makeSessionId(HttpServletRequest arg0, Principal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setCurrentSession(Session arg0) {
		// TODO Auto-generated method stub
		
	}

	public void setCurrentToolSession(ToolSession arg0) {
		// TODO Auto-generated method stub
		
	}

	public Session startSession() {
		// TODO Auto-generated method stub
		return null;
	}

	public Session startSession(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
