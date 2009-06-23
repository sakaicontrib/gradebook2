package org.sakaiproject.gradebook.gwt.sakai;

import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;

public class Gradebook2AuthnImpl implements Gradebook2Authn {

	public String getUserUid() {
		Session session = SessionManager.getCurrentSession();
        String userId = session.getUserId();
        return userId;
	}

	public void setAuthnContext(Object whatToAuthn) {
		// TODO Auto-generated method stub

	}

}
