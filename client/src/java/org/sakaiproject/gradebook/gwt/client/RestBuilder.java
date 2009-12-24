package org.sakaiproject.gradebook.gwt.client;

import com.extjs.gxt.ui.client.GXT;
import com.google.gwt.http.client.RequestBuilder;

public class RestBuilder extends RequestBuilder {

	public enum Method { GET, POST, PUT, DELETE };
	
	protected RestBuilder(String method, String url) {
		super(method, url);
	}
	
	public static RestBuilder getInstance(Method method, String url) {
		String header = null;
		switch (method) {
		case DELETE:
		case PUT:
			//if (GXT.isSafari) {
				header = method.name();
				method = Method.POST;
			//}
			break;
		}
		RestBuilder builder = new RestBuilder(method.name(), url);
		
		if (header != null)
			builder.setHeader("X-HTTP-Method-Override", header);
		
		builder.setHeader("Content-Type", "application/json; charset=utf-8");
		
		return builder;
	}
	
	protected static String getMethod(Method method) {
		switch (method) {
		case DELETE:
		case PUT:
			if (GXT.isSafari) {
				method = Method.POST;
				
			}
			break;
		}
		
		return method.name();
	}
	

}
