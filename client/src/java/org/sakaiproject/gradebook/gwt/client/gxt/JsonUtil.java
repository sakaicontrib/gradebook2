package org.sakaiproject.gradebook.gwt.client.gxt;

import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityOverlay;

import com.google.gwt.core.client.JsArray;

public class JsonUtil {

	
	public static EntityOverlay toOverlay(String json) {
		JsArray<EntityOverlay> array = asOverlay(json);
		
		if (array != null && array.length() > 0) {
			return array.get(0);
		}
		
		return null;
	}
	
	public static final native JsArray<EntityOverlay> asOverlay(String json) /*-{
		return eval("[" + json + "]");
	}-*/;
	
}
