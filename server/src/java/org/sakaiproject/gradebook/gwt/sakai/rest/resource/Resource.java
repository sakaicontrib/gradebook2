package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ComponentService;

public class Resource {

	private static final Log log = LogFactory.getLog(Resource.class);
	
	protected Gradebook2ComponentService service;
	
	protected <X> X fromJson(String text, Class<?> type) {
		X o = null;
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			o = (X)mapper.readValue(text, type);
		} catch (Exception e) {
			log.error("Caught an exception deserializing from JSON: " + text, e);
		}
		
		return o;
	}
	
	protected String toJson(List<?> list, int size) {
		Map<String,Object> wrapper = new HashMap<String, Object>();
		wrapper.put(AppConstants.LIST_ROOT, list);
		wrapper.put(AppConstants.TOTAL, String.valueOf(size));
		
		return toJson(wrapper);
	}
	
	protected String toJson(Object o) {
		ObjectMapper mapper = new ObjectMapper();
		StringWriter w = new StringWriter();
		try {
			mapper.writeValue(w, o);
		} catch (Exception e) {
			log.error("Caught an exception serializing to JSON: ", e);
		}
		
		return w.toString();
	}
	
	public Gradebook2ComponentService getService() {
		return service;
	}

	public void setService(Gradebook2ComponentService service) {
		this.service = service;
	}
	
}
