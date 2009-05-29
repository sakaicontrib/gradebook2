package org.sakaiproject.gradebook.gwt.client;

import com.google.gwt.user.client.Cookies;


public class SecureToken {

	public static String get() {
	    return Cookies.getCookie("JSESSIONID");
	}
}
