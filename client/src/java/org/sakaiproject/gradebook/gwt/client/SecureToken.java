package org.sakaiproject.gradebook.gwt.client;

import com.google.gwt.user.client.Cookies;


public class SecureToken {

	static public String get() {
		
	    return Cookies.getCookie("JSESSIONID");
	  }
}
