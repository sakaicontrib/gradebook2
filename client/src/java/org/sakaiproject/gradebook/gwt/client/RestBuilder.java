package org.sakaiproject.gradebook.gwt.client;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.model.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.GradeType;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

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
	
	public static RestBuilder getInstance(Method method, String ... urlArgs) {
		return getInstance(method, buildInitUrl(urlArgs));
	}
	
	public static JSONObject convertModel(BaseModel model) {
		JSONObject json = new JSONObject();
		
		Map<String, Object> map = model.getProperties();
		
		for (String key : map.keySet()) {
			Object obj = map.get(key);
			
			if (obj != null) {
				if (obj instanceof String)
					json.put(key, new JSONString((String)obj));
				else if (obj instanceof Number) 
					json.put(key, new JSONNumber(((Number)obj).doubleValue()));
				else if (obj instanceof Boolean)
					json.put(key, JSONBoolean.getInstance(((Boolean)obj).booleanValue()));
				else if (obj instanceof List) {
					JSONArray array = new JSONArray();
				
					int i=0;
					for (Object element : (List)obj) {
						if (element != null) {
							if (element instanceof BaseModel)
								array.set(i++, convertModel((BaseModel)element));
							else if (element instanceof String) 
								array.set(i++, new JSONString((String)element));
						}
					}
					
					json.put(key, array);
				} else if (obj instanceof CategoryType) {
					json.put(key, new JSONString(((CategoryType)obj).name()));
				} else if (obj instanceof GradeType) {
					json.put(key, new JSONString(((GradeType)obj).name()));
				} else if (obj instanceof Date) {
					json.put(key, new JSONNumber(((Date)obj).getTime()));
				} else {
					
					Object o = obj;
				}
			}
		}
		
		return json;
	}
	
	public Request sendRequest(final int successCode, final int failureCode, String requestData, final RestCallback callback) {
		Request request = null;
		try {
			super.sendRequest(requestData, new RequestCallback() {
	
				public void onError(Request request, Throwable exception) {
					callback.onError(request, exception);
				}
	
				public void onResponseReceived(Request request, Response response) {
					if (response.getStatusCode() == failureCode)
						callback.onFailure(request, new Exception(response.getText()));
					else if (response.getStatusCode() == successCode)
						callback.onSuccess(request, response);
					else
						callback.onError(request, new Exception("Unexpected response from server: " + response.getStatusCode()));
				}
				
			});
		} catch (RequestException re) {
			callback.onError(request, re);
		}
		return request;
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
	
	public static String buildInitUrl(String ... args) {
		StringBuilder builder = new StringBuilder();
		
		for (int i=0;i<args.length;i++) {
			builder.append(args[i]);
			
			if (!args[i].endsWith("/"))
				builder.append("/");
		}
		
		return builder.toString();
	}
	
	
	
}
