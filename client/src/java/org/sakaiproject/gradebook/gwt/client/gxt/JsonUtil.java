package org.sakaiproject.gradebook.gwt.client.gxt;

import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityOverlay;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

public class JsonUtil {

	
	public static EntityOverlay toOverlay(String json) {
		
		 JSONObject  jsonObject = (JSONObject) JSONParser.parseStrict(json);
		 return  jsonObject.getJavaScriptObject().cast();
	}
	
}
