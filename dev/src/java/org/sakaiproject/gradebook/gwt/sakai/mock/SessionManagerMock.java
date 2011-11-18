package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.mutable.MutableLong;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.impl.MySession;

public class SessionManagerMock implements SessionManager {

	MySession session = null;
	Locale locale = null;
	
	public SessionManagerMock(){
		String l = System.getProperty("gb2.locale", "en_US");
		String[] parts = l.split("\\_");
		System.out.println("locale: " + parts[0] + ":" + parts[1]);
		switch (parts.length) {
		case 1: 
			locale = new Locale(parts[0]);
			break;
		case 2: 
			locale = new Locale(parts[0], parts[1]);
			break;
		case 3: 
			locale = new Locale(parts[0], parts[1], parts[3]);
			break;
		case 0: 
		default:
			locale = new Locale("en", "US");
			
		}
		locale = new Locale(parts[0], parts[1]);
		
	}
	// this is a direct copy of *protected* string declaration in ResourceLoader
	// session key string for determining validity of ResourceBundle cache
	protected String LOCALE_SESSION_KEY = "sakai.locale.";
	
	public int getActiveUserCount(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Session getCurrentSession() {
		
		if(null == session) {
		 
			session = new MySession(this, java.util.UUID.randomUUID().toString(), null, null, null, null, 0, null, new MutableLong(1000l* 60l* 60l* 24l));
			session.setUserId("0");
			session.setUserEid("UserEidForDevMode");
			session.setAttribute(LOCALE_SESSION_KEY+getCurrentSessionUserId(), locale);
			session.setAttribute("locale", locale);
		}
		
		return session;
	}

	public String getCurrentSessionUserId() {
		
		if(null != session) {
			return session.getUserId();
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
